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
        private Ref<Type> t = ref(null);
        private Ref<Ptr<Scope>> sc = ref(null);
        private Ref<Expression> result = ref(null);
        public  ImplicitCastTo(Ptr<Scope> sc, Type t) {
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            Ref<Type> t_ref = ref(t);
            this.sc.value = sc_ref.value;
            this.t.value = t_ref.value;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            IntRef match = ref(e_ref.value.implicitConvTo(this.t.value));
            if (match.value != 0)
            {
                if ((match.value == MATCH.constant) && (e_ref.value.type.value.constConv(this.t.value) != 0) || !e_ref.value.isLvalue() && e_ref.value.type.value.equivalent(this.t.value))
                {
                    this.result.value = e_ref.value.copy();
                    this.result.value.type.value = this.t.value;
                    return ;
                }
                Ref<AggregateDeclaration> ad = ref(isAggregate(e_ref.value.type.value));
                if ((ad.value != null) && (ad.value.aliasthis.value != null))
                {
                    IntRef adMatch = ref(MATCH.nomatch);
                    if (((ad.value.type.value.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        adMatch.value = ((TypeStruct)ad.value.type.value).implicitConvToWithoutAliasThis(this.t.value);
                    }
                    else
                    {
                        adMatch.value = ((TypeClass)ad.value.type.value).implicitConvToWithoutAliasThis(this.t.value);
                    }
                    if (adMatch.value == 0)
                    {
                        Ref<Type> tob = ref(this.t.value.toBasetype());
                        Type t1b = e_ref.value.type.value.toBasetype();
                        Ref<AggregateDeclaration> toad = ref(isAggregate(tob.value));
                        if ((!pequals(ad.value, toad.value)))
                        {
                            if (((t1b.ty.value & 0xFF) == ENUMTY.Tclass) && ((tob.value.ty.value & 0xFF) == ENUMTY.Tclass))
                            {
                                Ref<ClassDeclaration> t1cd = ref(t1b.isClassHandle());
                                ClassDeclaration tocd = tob.value.isClassHandle();
                                IntRef offset = ref(0);
                                if (tocd.isBaseOf(t1cd.value, ptr(offset)))
                                {
                                    this.result.value = new CastExp(e_ref.value.loc.value, e_ref.value, this.t.value);
                                    this.result.value.type.value = this.t.value;
                                    return ;
                                }
                            }
                            this.result.value = resolveAliasThis(this.sc.value, e_ref.value, false);
                            this.result.value = this.result.value.castTo(this.sc.value, this.t.value);
                            return ;
                        }
                    }
                }
                this.result.value = e_ref.value.castTo(this.sc.value, this.t.value);
                return ;
            }
            this.result.value = e_ref.value.optimize(0, false);
            if ((!pequals(this.result.value, e_ref.value)))
            {
                this.result.value.accept(this);
                return ;
            }
            if (((this.t.value.ty.value & 0xFF) != ENUMTY.Terror) && ((e_ref.value.type.value.ty.value & 0xFF) != ENUMTY.Terror))
            {
                if (this.t.value.deco.value == null)
                {
                    e_ref.value.error(new BytePtr("forward reference to type `%s`"), this.t.value.toChars());
                }
                else
                {
                    Slice<BytePtr> ts = toAutoQualChars(e_ref.value.type.value, this.t.value);
                    e_ref.value.error(new BytePtr("cannot implicitly convert expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), ts.get(0), ts.get(1));
                }
            }
            this.result.value = new ErrorExp();
        }

        public  void visit(StringExp e) {
            Ref<StringExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.value.op.value & 0xFF) == 121))
            {
                ((StringExp)this.result.value).committed.value = e_ref.value.committed.value;
            }
        }

        public  void visit(ErrorExp e) {
            Ref<ErrorExp> e_ref = ref(e);
            this.result.value = e_ref.value;
        }

        public  void visit(FuncExp e) {
            Ref<FuncExp> e_ref = ref(e);
            Ref<FuncExp> fe = ref(null);
            if ((e_ref.value.matchType(this.t.value, this.sc.value, ptr(fe), 0) > MATCH.nomatch))
            {
                this.result.value = fe.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            Type tb = this.result.value.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) && global.params.useTypeInfo.value && (Type.dtypeinfo.value != null))
            {
                semanticTypeInfo(this.sc.value, ((TypeDArray)tb).next.value);
            }
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.value.op.value & 0xFF) != 31))
            {
                return ;
            }
            e_ref.value = (SliceExp)this.result.value;
            if (((e_ref.value.e1.value.op.value & 0xFF) == 47))
            {
                ArrayLiteralExp ale = (ArrayLiteralExp)e_ref.value.e1.value;
                Type tb = this.t.value.toBasetype();
                Ref<Type> tx = ref(null);
                if (((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    tx.value = tb.nextOf().sarrayOf(ale.elements.value != null ? (long)(ale.elements.value.get()).length.value : 0L);
                }
                else
                {
                    tx.value = tb.nextOf().arrayOf();
                }
                e_ref.value.e1.value = ale.implicitCastTo(this.sc.value, tx.value);
            }
        }


        public ImplicitCastTo() {}
    }
    private static class ClassCheck
    {
        public static boolean convertible(Loc loc, ClassDeclaration cd, byte mod) {
            Ref<Loc> loc_ref = ref(loc);
            Ref<Byte> mod_ref = ref(mod);
            {
                IntRef i = ref(0);
                for (; (i.value < cd.fields.length.value);i.value++){
                    VarDeclaration v = cd.fields.get(i.value);
                    Ref<Initializer> _init = ref(v._init.value);
                    if (_init.value != null)
                    {
                        if (_init.value.isVoidInitializer() != null)
                        {
                        }
                        else {
                            Ref<ExpInitializer> ei = ref(_init.value.isExpInitializer());
                            if ((ei.value) != null)
                            {
                                Ref<Type> tb = ref(v.type.value.toBasetype());
                                if ((implicitMod(ei.value.exp.value, tb.value, mod_ref.value) == MATCH.nomatch))
                                {
                                    return false;
                                }
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                    else if (!v.type.value.isZeroInit(loc_ref.value))
                    {
                        return false;
                    }
                }
            }
            return cd.baseClass.value != null ? convertible(loc_ref.value, cd.baseClass.value, mod_ref.value) : true;
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
        private Ref<Type> t = ref(null);
        private IntRef result = ref(0);
        public  ImplicitConvTo(Type t) {
            Ref<Type> t_ref = ref(t);
            this.t.value = t_ref.value;
            this.result.value = MATCH.nomatch;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            if ((pequals(this.t.value, Type.terror.value)))
            {
                return ;
            }
            if (e_ref.value.type.value == null)
            {
                e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                e_ref.value.type.value = Type.terror.value;
            }
            Ref<Expression> ex = ref(e_ref.value.optimize(0, false));
            if (ex.value.type.value.equals(this.t.value))
            {
                this.result.value = MATCH.exact;
                return ;
            }
            if ((!pequals(ex.value, e_ref.value)))
            {
                this.result.value = ex.value.implicitConvTo(this.t.value);
                return ;
            }
            IntRef match = ref(e_ref.value.type.value.implicitConvTo(this.t.value));
            if ((match.value != MATCH.nomatch))
            {
                this.result.value = match.value;
                return ;
            }
            if (e_ref.value.type.value.isintegral() && this.t.value.isintegral() && (e_ref.value.type.value.isTypeBasic() != null) && (this.t.value.isTypeBasic() != null))
            {
                Ref<IntRange> src = ref(getIntRange(e_ref.value).copy());
                IntRange target = IntRange.fromType(this.t.value).copy();
                if (target.contains(src.value))
                {
                    this.result.value = MATCH.convert;
                    return ;
                }
            }
        }

        public static int implicitMod(Expression e, Type t, byte mod) {
            Ref<Byte> mod_ref = ref(mod);
            Ref<Type> tprime = ref(null);
            if (((t.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                tprime.value = t.nextOf().castMod(mod_ref.value).pointerTo();
            }
            else if (((t.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                tprime.value = t.nextOf().castMod(mod_ref.value).arrayOf();
            }
            else if (((t.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                tprime.value = t.nextOf().castMod(mod_ref.value).sarrayOf(t.size() / t.nextOf().size());
            }
            else
            {
                tprime.value = t.castMod(mod_ref.value);
            }
            return e.implicitConvTo(tprime.value);
        }

        public static int implicitConvToAddMin(BinExp e, Type t) {
            Ref<Type> t_ref = ref(t);
            Ref<Type> tb = ref(t_ref.value.toBasetype());
            Type typeb = e.type.value.toBasetype();
            if (((typeb.ty.value & 0xFF) != ENUMTY.Tpointer) || ((tb.value.ty.value & 0xFF) != ENUMTY.Tpointer))
            {
                return MATCH.nomatch;
            }
            Type t1b = e.e1.value.type.value.toBasetype();
            Type t2b = e.e2.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tpointer) && t2b.isintegral() && t1b.equivalent(tb.value))
            {
                IntRef m = ref(e.e1.value.implicitConvTo(t_ref.value));
                return (m.value > MATCH.constant) ? MATCH.constant : m.value;
            }
            if (((t2b.ty.value & 0xFF) == ENUMTY.Tpointer) && t1b.isintegral() && t2b.equivalent(tb.value))
            {
                IntRef m = ref(e.e2.value.implicitConvTo(t_ref.value));
                return (m.value > MATCH.constant) ? MATCH.constant : m.value;
            }
            return MATCH.nomatch;
        }

        public  void visit(AddExp e) {
            Ref<AddExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value == MATCH.nomatch))
            {
                this.result.value = implicitConvToAddMin(e_ref.value, this.t.value);
            }
        }

        public  void visit(MinExp e) {
            Ref<MinExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value == MATCH.nomatch))
            {
                this.result.value = implicitConvToAddMin(e_ref.value, this.t.value);
            }
        }

        public  void visit(IntegerExp e) {
            Ref<IntegerExp> e_ref = ref(e);
            IntRef m = ref(e_ref.value.type.value.implicitConvTo(this.t.value));
            if ((m.value >= MATCH.constant))
            {
                this.result.value = m.value;
                return ;
            }
            Ref<Byte> ty = ref(e_ref.value.type.value.toBasetype().ty.value);
            Ref<Byte> toty = ref(this.t.value.toBasetype().ty.value);
            Ref<Byte> oldty = ref(ty.value);
            if ((m.value == MATCH.nomatch) && ((this.t.value.ty.value & 0xFF) == ENUMTY.Tenum))
            {
                return ;
            }
            if (((this.t.value.ty.value & 0xFF) == ENUMTY.Tvector))
            {
                TypeVector tv = (TypeVector)this.t.value;
                TypeBasic tb = tv.elementType();
                if (((tb.ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    return ;
                }
                toty.value = tb.ty.value;
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
                            {
                                return ;
                            }
                            break;
                        case 13:
                            if (((ty.value & 0xFF) == ENUMTY.Tuns64) && ((value.value & 4294967168L) != 0))
                            {
                                return ;
                            }
                            else if (((long)((byte)value.value & 0xFF) != value.value))
                            {
                                return ;
                            }
                            break;
                        case 31:
                            if (((oldty.value & 0xFF) == ENUMTY.Twchar) || ((oldty.value & 0xFF) == ENUMTY.Tdchar) && (value.value > 127L))
                            {
                                return ;
                            }
                            /*goto case*/{ __dispatch1 = 14; continue dispatched_1; }
                        case 14:
                            __dispatch1 = 0;
                            if (((long)((byte)value.value & 0xFF) != value.value))
                            {
                                return ;
                            }
                            break;
                        case 15:
                            if (((ty.value & 0xFF) == ENUMTY.Tuns64) && ((value.value & 4294934528L) != 0))
                            {
                                return ;
                            }
                            else if (((long)(int)(int)value.value != value.value))
                            {
                                return ;
                            }
                            break;
                        case 32:
                            if (((oldty.value & 0xFF) == ENUMTY.Tdchar) && (value.value > 55295L) && (value.value < 57344L))
                            {
                                return ;
                            }
                            /*goto case*/{ __dispatch1 = 16; continue dispatched_1; }
                        case 16:
                            __dispatch1 = 0;
                            if (((long)(int)(int)value.value != value.value))
                            {
                                return ;
                            }
                            break;
                        case 17:
                            if (((ty.value & 0xFF) == ENUMTY.Tuns32))
                            {
                            }
                            else if (((ty.value & 0xFF) == ENUMTY.Tuns64) && ((value.value & 2147483648L) != 0))
                            {
                                return ;
                            }
                            else if (((long)(int)value.value != value.value))
                            {
                                return ;
                            }
                            break;
                        case 18:
                            if (((ty.value & 0xFF) == ENUMTY.Tint32))
                            {
                            }
                            else if (((long)(int)value.value != value.value))
                            {
                                return ;
                            }
                            break;
                        case 33:
                            if ((value.value > 1114111L))
                            {
                                return ;
                            }
                            break;
                        case 21:
                            if (!isLosslesslyConvertibleToFPFloat.invoke())
                            {
                                return ;
                            }
                            break;
                        case 22:
                            if (!isLosslesslyConvertibleToFPDouble.invoke())
                            {
                                return ;
                            }
                            break;
                        case 23:
                            if (!isLosslesslyConvertibleToFPDouble.invoke())
                            {
                                return ;
                            }
                            break;
                        case 3:
                            if (((ty.value & 0xFF) == ENUMTY.Tpointer) && ((e_ref.value.type.value.toBasetype().nextOf().ty.value & 0xFF) == (this.t.value.toBasetype().nextOf().ty.value & 0xFF)))
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
            this.result.value = MATCH.convert;
        }

        public  void visit(ErrorExp e) {
        }

        public  void visit(NullExp e) {
            Ref<NullExp> e_ref = ref(e);
            if (e_ref.value.type.value.equals(this.t.value))
            {
                this.result.value = MATCH.exact;
                return ;
            }
            if (this.t.value.equivalent(e_ref.value.type.value))
            {
                this.result.value = MATCH.constant;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            if (((e_ref.value.type.value.ty.value & 0xFF) == (this.t.value.ty.value & 0xFF)) && ((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tstruct) && (pequals(((TypeStruct)e_ref.value.type.value).sym.value, ((TypeStruct)this.t.value).sym.value)))
            {
                this.result.value = MATCH.constant;
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.elements.value.get()).length.value);i.value++){
                        Ref<Expression> el = ref((e_ref.value.elements.value.get()).get(i.value));
                        if (el.value == null)
                        {
                            continue;
                        }
                        Ref<Type> te = ref(e_ref.value.sd.fields.get(i.value).type.value.addMod(this.t.value.mod.value));
                        IntRef m2 = ref(el.value.implicitConvTo(te.value));
                        if ((m2.value < this.result.value))
                        {
                            this.result.value = m2.value;
                        }
                    }
                }
            }
        }

        public  void visit(StringExp e) {
            Ref<StringExp> e_ref = ref(e);
            if ((e_ref.value.committed.value == 0) && ((this.t.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((this.t.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid))
            {
                return ;
            }
            if (!(((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer)))
            {
                this.visit((Expression)e_ref);
                return ;
            }
            Ref<Byte> tyn = ref(e_ref.value.type.value.nextOf().ty.value);
            if (!(((tyn.value & 0xFF) == ENUMTY.Tchar) || ((tyn.value & 0xFF) == ENUMTY.Twchar) || ((tyn.value & 0xFF) == ENUMTY.Tdchar)))
            {
                this.visit((Expression)e_ref);
                return ;
            }
            switch ((this.t.value.ty.value & 0xFF))
            {
                case 1:
                    if (((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                    {
                        Ref<Byte> tynto = ref(this.t.value.nextOf().ty.value);
                        if (((tynto.value & 0xFF) == (tyn.value & 0xFF)))
                        {
                            if ((((TypeSArray)e_ref.value.type.value).dim.value.toInteger() == ((TypeSArray)this.t.value).dim.value.toInteger()))
                            {
                                this.result.value = MATCH.exact;
                            }
                            return ;
                        }
                        if (((tynto.value & 0xFF) == ENUMTY.Tchar) || ((tynto.value & 0xFF) == ENUMTY.Twchar) || ((tynto.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            if ((e_ref.value.committed.value != 0) && ((tynto.value & 0xFF) != (tyn.value & 0xFF)))
                            {
                                return ;
                            }
                            IntRef fromlen = ref(e_ref.value.numberOfCodeUnits((tynto.value & 0xFF)));
                            IntRef tolen = ref((int)((TypeSArray)this.t.value).dim.value.toInteger());
                            if ((tolen.value < fromlen.value))
                            {
                                return ;
                            }
                            if ((tolen.value != fromlen.value))
                            {
                                this.result.value = MATCH.convert;
                                return ;
                            }
                        }
                        if ((e_ref.value.committed.value == 0) && ((tynto.value & 0xFF) == ENUMTY.Tchar) || ((tynto.value & 0xFF) == ENUMTY.Twchar) || ((tynto.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            this.result.value = MATCH.exact;
                            return ;
                        }
                    }
                    else if (((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray))
                    {
                        Ref<Byte> tynto_1 = ref(this.t.value.nextOf().ty.value);
                        if (((tynto_1.value & 0xFF) == ENUMTY.Tchar) || ((tynto_1.value & 0xFF) == ENUMTY.Twchar) || ((tynto_1.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            if ((e_ref.value.committed.value != 0) && ((tynto_1.value & 0xFF) != (tyn.value & 0xFF)))
                            {
                                return ;
                            }
                            IntRef fromlen_1 = ref(e_ref.value.numberOfCodeUnits((tynto_1.value & 0xFF)));
                            IntRef tolen_1 = ref((int)((TypeSArray)this.t.value).dim.value.toInteger());
                            if ((tolen_1.value < fromlen_1.value))
                            {
                                return ;
                            }
                            if ((tolen_1.value != fromlen_1.value))
                            {
                                this.result.value = MATCH.convert;
                                return ;
                            }
                        }
                        if (((tynto_1.value & 0xFF) == (tyn.value & 0xFF)))
                        {
                            this.result.value = MATCH.exact;
                            return ;
                        }
                        if ((e_ref.value.committed.value == 0) && ((tynto_1.value & 0xFF) == ENUMTY.Tchar) || ((tynto_1.value & 0xFF) == ENUMTY.Twchar) || ((tynto_1.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            this.result.value = MATCH.exact;
                            return ;
                        }
                    }
                case 0:
                case 3:
                    Type tn = this.t.value.nextOf();
                    IntRef m = ref(MATCH.exact);
                    if (((e_ref.value.type.value.nextOf().mod.value & 0xFF) != (tn.mod.value & 0xFF)))
                    {
                        if (!tn.isConst() && !tn.isImmutable())
                        {
                            return ;
                        }
                        m.value = MATCH.constant;
                    }
                    if (e_ref.value.committed.value == 0)
                    {
                        switch ((tn.ty.value & 0xFF))
                        {
                            case 31:
                                if (((e_ref.value.postfix.value & 0xFF) == 119) || ((e_ref.value.postfix.value & 0xFF) == 100))
                                {
                                    m.value = MATCH.convert;
                                }
                                this.result.value = m.value;
                                return ;
                            case 32:
                                if (((e_ref.value.postfix.value & 0xFF) != 119))
                                {
                                    m.value = MATCH.convert;
                                }
                                this.result.value = m.value;
                                return ;
                            case 33:
                                if (((e_ref.value.postfix.value & 0xFF) != 100))
                                {
                                    m.value = MATCH.convert;
                                }
                                this.result.value = m.value;
                                return ;
                            case 9:
                                if (((TypeEnum)tn).sym.value.isSpecial())
                                {
                                    {
                                        Ref<TypeBasic> tob = ref(tn.toBasetype().isTypeBasic());
                                        if ((tob.value) != null)
                                        {
                                            this.result.value = tn.implicitConvTo(tob.value);
                                        }
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
            Ref<Type> tb = ref(this.t.value.toBasetype());
            Type typeb = e_ref.value.type.value.toBasetype();
            if (((tb.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((typeb.ty.value & 0xFF) == ENUMTY.Tarray) || ((typeb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                this.result.value = MATCH.exact;
                Type typen = typeb.nextOf().toBasetype();
                if (((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    TypeSArray tsa = (TypeSArray)tb.value;
                    if (((long)(e_ref.value.elements.value.get()).length.value != tsa.dim.value.toInteger()))
                    {
                        this.result.value = MATCH.nomatch;
                    }
                }
                Ref<Type> telement = ref(tb.value.nextOf());
                if ((e_ref.value.elements.value.get()).length.value == 0)
                {
                    if (((typen.ty.value & 0xFF) != ENUMTY.Tvoid))
                    {
                        this.result.value = typen.implicitConvTo(telement.value);
                    }
                }
                else
                {
                    if (e_ref.value.basis.value != null)
                    {
                        IntRef m = ref(e_ref.value.basis.value.implicitConvTo(telement.value));
                        if ((m.value < this.result.value))
                        {
                            this.result.value = m.value;
                        }
                    }
                    {
                        IntRef i = ref(0);
                        for (; (i.value < (e_ref.value.elements.value.get()).length.value);i.value++){
                            Ref<Expression> el = ref((e_ref.value.elements.value.get()).get(i.value));
                            if ((this.result.value == MATCH.nomatch))
                            {
                                break;
                            }
                            if (el.value == null)
                            {
                                continue;
                            }
                            IntRef m = ref(el.value.implicitConvTo(telement.value));
                            if ((m.value < this.result.value))
                            {
                                this.result.value = m.value;
                            }
                        }
                    }
                }
                if (this.result.value == 0)
                {
                    this.result.value = e_ref.value.type.value.implicitConvTo(this.t.value);
                }
                return ;
            }
            else if (((tb.value.ty.value & 0xFF) == ENUMTY.Tvector) && ((typeb.ty.value & 0xFF) == ENUMTY.Tarray) || ((typeb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                this.result.value = MATCH.exact;
                TypeVector tv = (TypeVector)tb.value;
                TypeSArray tbase = (TypeSArray)tv.basetype.value;
                assert(((tbase.ty.value & 0xFF) == ENUMTY.Tsarray));
                IntRef edim = ref((e_ref.value.elements.value.get()).length.value);
                Ref<Long> tbasedim = ref(tbase.dim.value.toInteger());
                if (((long)edim.value > tbasedim.value))
                {
                    this.result.value = MATCH.nomatch;
                    return ;
                }
                Ref<Type> telement = ref(tv.elementType());
                if (((long)edim.value < tbasedim.value))
                {
                    Expression el = typeb.nextOf().defaultInitLiteral(e_ref.value.loc.value);
                    IntRef m = ref(el.implicitConvTo(telement.value));
                    if ((m.value < this.result.value))
                    {
                        this.result.value = m.value;
                    }
                }
                {
                    IntRef __key899 = ref(0);
                    IntRef __limit900 = ref(edim.value);
                    for (; (__key899.value < __limit900.value);__key899.value += 1) {
                        IntRef i = ref(__key899.value);
                        Expression el = (e_ref.value.elements.value.get()).get(i.value);
                        IntRef m = ref(el.implicitConvTo(telement.value));
                        if ((m.value < this.result.value))
                        {
                            this.result.value = m.value;
                        }
                        if ((this.result.value == MATCH.nomatch))
                        {
                            break;
                        }
                    }
                }
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(AssocArrayLiteralExp e) {
            Ref<AssocArrayLiteralExp> e_ref = ref(e);
            Type tb = this.t.value.toBasetype();
            Type typeb = e_ref.value.type.value.toBasetype();
            if (!(((tb.ty.value & 0xFF) == ENUMTY.Taarray) && ((typeb.ty.value & 0xFF) == ENUMTY.Taarray)))
            {
                this.visit((Expression)e_ref);
                return ;
            }
            this.result.value = MATCH.exact;
            {
                IntRef i = ref(0);
                for (; (i.value < (e_ref.value.keys.value.get()).length.value);i.value++){
                    Ref<Expression> el = ref((e_ref.value.keys.value.get()).get(i.value));
                    IntRef m = ref(el.value.implicitConvTo(((TypeAArray)tb).index.value));
                    if ((m.value < this.result.value))
                    {
                        this.result.value = m.value;
                    }
                    if ((this.result.value == MATCH.nomatch))
                    {
                        break;
                    }
                    el.value = (e_ref.value.values.value.get()).get(i.value);
                    m.value = el.value.implicitConvTo(tb.nextOf());
                    if ((m.value < this.result.value))
                    {
                        this.result.value = m.value;
                    }
                    if ((this.result.value == MATCH.nomatch))
                    {
                        break;
                    }
                }
            }
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            boolean LOG = false;
            this.visit((Expression)e_ref);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            if ((e_ref.value.f.value != null) && e_ref.value.f.value.isReturnIsolated() && !global.params.vsafe.value || (e_ref.value.f.value.isPure() >= PURE.strong) || (pequals(e_ref.value.f.value.ident.value, Id.dup.value)) && (pequals(e_ref.value.f.value.toParent2(), ClassDeclaration.object.value.toParent())))
            {
                this.result.value = e_ref.value.type.value.immutableOf().implicitConvTo(this.t.value);
                if ((this.result.value > MATCH.constant))
                {
                    this.result.value = MATCH.constant;
                }
                return ;
            }
            Ref<Type> tx = ref(e_ref.value.f.value != null ? e_ref.value.f.value.type.value : e_ref.value.e1.value.type.value);
            tx.value = tx.value.toBasetype();
            if (((tx.value.ty.value & 0xFF) != ENUMTY.Tfunction))
            {
                return ;
            }
            TypeFunction tf = (TypeFunction)tx.value;
            if ((tf.purity.value == PURE.impure))
            {
                return ;
            }
            if ((e_ref.value.f.value != null) && e_ref.value.f.value.isNested())
            {
                return ;
            }
            if ((e_ref.value.type.value.immutableOf().implicitConvTo(this.t.value) < MATCH.constant) && (e_ref.value.type.value.addMod((byte)2).implicitConvTo(this.t.value) < MATCH.constant) && (e_ref.value.type.value.implicitConvTo(this.t.value.addMod((byte)2)) < MATCH.constant))
            {
                return ;
            }
            Type tb = this.t.value.toBasetype();
            Ref<Byte> mod = ref(tb.mod.value);
            if (tf.isref.value)
            {
            }
            else
            {
                Ref<Type> ti = ref(getIndirection(this.t.value));
                if (ti.value != null)
                {
                    mod.value = ti.value.mod.value;
                }
            }
            if (((mod.value & 0xFF) & MODFlags.wild) != 0)
            {
                return ;
            }
            IntRef nparams = ref(tf.parameterList.length());
            IntRef j = ref((((tf.linkage.value == LINK.d) && (tf.parameterList.varargs.value == VarArg.variadic)) ? 1 : 0));
            if (((e_ref.value.e1.value.op.value & 0xFF) == 27))
            {
                DotVarExp dve = (DotVarExp)e_ref.value.e1.value;
                Type targ = dve.e1.value.type.value;
                if ((targ.constConv(targ.castMod(mod.value)) == MATCH.nomatch))
                {
                    return ;
                }
            }
            {
                IntRef i = ref(j.value);
                for (; (i.value < (e_ref.value.arguments.value.get()).length.value);i.value += 1){
                    Ref<Expression> earg = ref((e_ref.value.arguments.value.get()).get(i.value));
                    Ref<Type> targ = ref(earg.value.type.value.toBasetype());
                    if ((i.value - j.value < nparams.value))
                    {
                        Parameter fparam = tf.parameterList.get(i.value - j.value);
                        if ((fparam.storageClass.value & 8192L) != 0)
                        {
                            return ;
                        }
                        Ref<Type> tparam = ref(fparam.type.value);
                        if (tparam.value == null)
                        {
                            continue;
                        }
                        if ((fparam.storageClass.value & 2101248L) != 0)
                        {
                            if ((targ.value.constConv(tparam.value.castMod(mod.value)) == MATCH.nomatch))
                            {
                                return ;
                            }
                            continue;
                        }
                    }
                    if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                    {
                        return ;
                    }
                }
            }
            this.result.value = MATCH.constant;
        }

        public  void visit(AddrExp e) {
            this.result.value = e.type.value.implicitConvTo(this.t.value);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            Type tb = this.t.value.toBasetype();
            Type typeb = e.type.value.toBasetype();
            if (((e.e1.value.op.value & 0xFF) == 214) && ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) || ((tb.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                OverExp eo = (OverExp)e.e1.value;
                Ref<FuncDeclaration> f = ref(null);
                {
                    IntRef i = ref(0);
                    for (; (i.value < eo.vars.a.length.value);i.value++){
                        Dsymbol s = eo.vars.a.get(i.value);
                        Ref<FuncDeclaration> f2 = ref(s.isFuncDeclaration());
                        assert(f2.value != null);
                        if (f2.value.overloadExactMatch(tb.nextOf()) != null)
                        {
                            if (f.value != null)
                            {
                                ScopeDsymbol.multiplyDefined(e.loc.value, f.value, f2.value);
                            }
                            else
                            {
                                f.value = f2.value;
                            }
                            this.result.value = MATCH.exact;
                        }
                    }
                }
            }
            if (((e.e1.value.op.value & 0xFF) == 26) && ((typeb.ty.value & 0xFF) == ENUMTY.Tpointer) && ((typeb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction) && ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) && ((tb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                throw new AssertionError("Unreachable code!");
            }
        }

        public  void visit(SymOffExp e) {
            this.result.value = e.type.value.implicitConvTo(this.t.value);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            Type tb = this.t.value.toBasetype();
            Type typeb = e.type.value.toBasetype();
            if (((typeb.ty.value & 0xFF) == ENUMTY.Tpointer) && ((typeb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction) && ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) || ((tb.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                {
                    Ref<FuncDeclaration> f = ref(e.var.value.isFuncDeclaration());
                    if ((f.value) != null)
                    {
                        f.value = f.value.overloadExactMatch(tb.nextOf());
                        if (f.value != null)
                        {
                            if (((tb.ty.value & 0xFF) == ENUMTY.Tdelegate) && f.value.needThis() || f.value.isNested() || ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) && !(f.value.needThis() || f.value.isNested()))
                            {
                                this.result.value = MATCH.exact;
                            }
                        }
                    }
                }
            }
        }

        public  void visit(DelegateExp e) {
            this.result.value = e.type.value.implicitConvTo(this.t.value);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            Type tb = this.t.value.toBasetype();
            Type typeb = e.type.value.toBasetype();
            if (((typeb.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((tb.ty.value & 0xFF) == ENUMTY.Tdelegate))
            {
                if ((e.func.value != null) && (e.func.value.overloadExactMatch(tb.nextOf()) != null))
                {
                    this.result.value = MATCH.exact;
                }
            }
        }

        public  void visit(FuncExp e) {
            Ref<FuncExp> e_ref = ref(e);
            IntRef m = ref(e_ref.value.matchType(this.t.value, null, null, 1));
            if ((m.value > MATCH.nomatch))
            {
                this.result.value = m.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(AndExp e) {
            Ref<AndExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            IntRef m1 = ref(e_ref.value.e1.value.implicitConvTo(this.t.value));
            IntRef m2 = ref(e_ref.value.e2.value.implicitConvTo(this.t.value));
            this.result.value = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(OrExp e) {
            Ref<OrExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            IntRef m1 = ref(e_ref.value.e1.value.implicitConvTo(this.t.value));
            IntRef m2 = ref(e_ref.value.e2.value.implicitConvTo(this.t.value));
            this.result.value = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(XorExp e) {
            Ref<XorExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            IntRef m1 = ref(e_ref.value.e1.value.implicitConvTo(this.t.value));
            IntRef m2 = ref(e_ref.value.e2.value.implicitConvTo(this.t.value));
            this.result.value = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(CondExp e) {
            IntRef m1 = ref(e.e1.value.implicitConvTo(this.t.value));
            IntRef m2 = ref(e.e2.value.implicitConvTo(this.t.value));
            this.result.value = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(CommaExp e) {
            e.e2.value.accept(this);
        }

        public  void visit(CastExp e) {
            Ref<CastExp> e_ref = ref(e);
            this.result.value = e_ref.value.type.value.implicitConvTo(this.t.value);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            if (this.t.value.isintegral() && e_ref.value.e1.value.type.value.isintegral() && (e_ref.value.e1.value.implicitConvTo(this.t.value) != MATCH.nomatch))
            {
                this.result.value = MATCH.convert;
            }
            else
            {
                this.visit((Expression)e_ref);
            }
        }

        public  void visit(NewExp e) {
            Ref<NewExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            if ((e_ref.value.type.value.immutableOf().implicitConvTo(this.t.value.immutableOf()) == MATCH.nomatch))
            {
                return ;
            }
            Type tb = this.t.value.toBasetype();
            Ref<Byte> mod = ref(tb.mod.value);
            {
                Ref<Type> ti = ref(getIndirection(this.t.value));
                if ((ti.value) != null)
                {
                    mod.value = ti.value.mod.value;
                }
            }
            if (((mod.value & 0xFF) & MODFlags.wild) != 0)
            {
                return ;
            }
            if (e_ref.value.thisexp.value != null)
            {
                Type targ = e_ref.value.thisexp.value.type.value;
                if ((targ.constConv(targ.castMod(mod.value)) == MATCH.nomatch))
                {
                    return ;
                }
            }
            Ref<FuncDeclaration> fd = ref(e_ref.value.allocator.value);
            {
                IntRef count = ref(0);
                for (; (count.value < 2);comma(count.value += 1, fd.value = e_ref.value.member.value)){
                    if (fd.value == null)
                    {
                        continue;
                    }
                    if (fd.value.errors.value || ((fd.value.type.value.ty.value & 0xFF) != ENUMTY.Tfunction))
                    {
                        return ;
                    }
                    TypeFunction tf = (TypeFunction)fd.value.type.value;
                    if ((tf.purity.value == PURE.impure))
                    {
                        return ;
                    }
                    if ((pequals(fd.value, e_ref.value.member.value)))
                    {
                        if ((e_ref.value.type.value.immutableOf().implicitConvTo(this.t.value) < MATCH.constant) && (e_ref.value.type.value.addMod((byte)2).implicitConvTo(this.t.value) < MATCH.constant) && (e_ref.value.type.value.implicitConvTo(this.t.value.addMod((byte)2)) < MATCH.constant))
                        {
                            return ;
                        }
                    }
                    Ptr<DArray<Expression>> args = (pequals(fd.value, e_ref.value.allocator.value)) ? e_ref.value.newargs.value : e_ref.value.arguments.value;
                    IntRef nparams = ref(tf.parameterList.length());
                    IntRef j = ref((((tf.linkage.value == LINK.d) && (tf.parameterList.varargs.value == VarArg.variadic)) ? 1 : 0));
                    {
                        IntRef i = ref(j.value);
                        for (; (i.value < (e_ref.value.arguments.value.get()).length.value);i.value += 1){
                            Ref<Expression> earg = ref((args.get()).get(i.value));
                            Ref<Type> targ = ref(earg.value.type.value.toBasetype());
                            if ((i.value - j.value < nparams.value))
                            {
                                Parameter fparam = tf.parameterList.get(i.value - j.value);
                                if ((fparam.storageClass.value & 8192L) != 0)
                                {
                                    return ;
                                }
                                Ref<Type> tparam = ref(fparam.type.value);
                                if (tparam.value == null)
                                {
                                    continue;
                                }
                                if ((fparam.storageClass.value & 2101248L) != 0)
                                {
                                    if ((targ.value.constConv(tparam.value.castMod(mod.value)) == MATCH.nomatch))
                                    {
                                        return ;
                                    }
                                    continue;
                                }
                            }
                            if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                            {
                                return ;
                            }
                        }
                    }
                }
            }
            if ((e_ref.value.member.value == null) && (e_ref.value.arguments.value != null))
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.arguments.value.get()).length.value);i.value += 1){
                        Ref<Expression> earg = ref((e_ref.value.arguments.value.get()).get(i.value));
                        if (earg.value == null)
                        {
                            continue;
                        }
                        Ref<Type> targ = ref(earg.value.type.value.toBasetype());
                        if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                        {
                            return ;
                        }
                    }
                }
            }
            Ref<Type> ntb = ref(e_ref.value.newtype.value.toBasetype());
            if (((ntb.value.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                ntb.value = ntb.value.nextOf().toBasetype();
            }
            if (((ntb.value.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = ((TypeStruct)ntb.value).sym.value;
                sd.size(e_ref.value.loc.value);
                if (sd.isNested())
                {
                    return ;
                }
            }
            if (ntb.value.isZeroInit(e_ref.value.loc.value))
            {
                if (((ntb.value.ty.value & 0xFF) == ENUMTY.Tclass))
                {
                    Ref<ClassDeclaration> cd = ref(((TypeClass)ntb.value).sym.value);
                    cd.value.size(e_ref.value.loc.value);
                    if (cd.value.isNested())
                    {
                        return ;
                    }
                    assert(cd.value.isInterfaceDeclaration() == null);
                    if (!ClassCheck.convertible(e_ref.value.loc.value, cd.value, mod.value))
                    {
                        return ;
                    }
                }
            }
            else
            {
                Ref<Expression> earg = ref(e_ref.value.newtype.value.defaultInitLiteral(e_ref.value.loc.value));
                Ref<Type> targ = ref(e_ref.value.newtype.value.toBasetype());
                if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                {
                    return ;
                }
            }
            this.result.value = MATCH.constant;
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result.value != MATCH.nomatch))
            {
                return ;
            }
            Ref<Type> tb = ref(this.t.value.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((typeb.value.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                typeb.value = toStaticArrayType(e_ref.value);
                if (typeb.value != null)
                {
                    this.result.value = typeb.value.implicitConvTo(this.t.value);
                }
                return ;
            }
            Type t1b = e_ref.value.e1.value.type.value.toBasetype();
            if (((tb.value.ty.value & 0xFF) == ENUMTY.Tarray) && typeb.value.equivalent(tb.value))
            {
                Type tbn = tb.value.nextOf();
                Ref<Type> tx = ref(null);
                if (((t1b.ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    tx.value = tbn.arrayOf();
                }
                if (((t1b.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    tx.value = tbn.pointerTo();
                }
                if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray) && !e_ref.value.e1.value.isLvalue())
                {
                    tx.value = tbn.sarrayOf(t1b.size() / tbn.size());
                }
                if (tx.value != null)
                {
                    this.result.value = e_ref.value.e1.value.implicitConvTo(tx.value);
                    if ((this.result.value > MATCH.constant))
                    {
                        this.result.value = MATCH.constant;
                    }
                }
            }
            if (((tb.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e_ref.value.e1.value.op.value & 0xFF) == 121))
            {
                e_ref.value.e1.value.accept(this);
            }
        }


        public ImplicitConvTo() {}
    }
    static Ref<BytePtr> visitmsg = ref(new BytePtr("cannot form delegate due to covariant return type"));
    private static class CastTo extends Visitor
    {
        private Ref<Type> t = ref(null);
        private Ref<Ptr<Scope>> sc = ref(null);
        private Ref<Expression> result = ref(null);
        public  CastTo(Ptr<Scope> sc, Type t) {
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            Ref<Type> t_ref = ref(t);
            this.sc.value = sc_ref.value;
            this.t.value = t_ref.value;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            if (e_ref.value.type.value.equals(this.t.value))
            {
                this.result.value = e_ref.value;
                return ;
            }
            if (((e_ref.value.op.value & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e_ref.value).var.value.isVarDeclaration());
                if ((v.value != null) && ((v.value.storage_class.value & 8388608L) != 0))
                {
                    this.result.value = e_ref.value.ctfeInterpret();
                    this.result.value.loc.value = e_ref.value.loc.value.copy();
                    this.result.value = this.result.value.castTo(this.sc.value, this.t.value);
                    return ;
                }
            }
            Ref<Type> tob = ref(this.t.value.toBasetype());
            Ref<Type> t1b = ref(e_ref.value.type.value.toBasetype());
            if (tob.value.equals(t1b.value))
            {
                this.result.value = e_ref.value.copy();
                this.result.value.type.value = this.t.value;
                return ;
            }
            boolean tob_isFV = ((tob.value.ty.value & 0xFF) == ENUMTY.Tstruct) || ((tob.value.ty.value & 0xFF) == ENUMTY.Tsarray);
            boolean t1b_isFV = ((t1b.value.ty.value & 0xFF) == ENUMTY.Tstruct) || ((t1b.value.ty.value & 0xFF) == ENUMTY.Tsarray);
            boolean tob_isFR = ((tob.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((tob.value.ty.value & 0xFF) == ENUMTY.Tdelegate);
            boolean t1b_isFR = ((t1b.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1b.value.ty.value & 0xFF) == ENUMTY.Tdelegate);
            boolean tob_isR = tob_isFR || ((tob.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((tob.value.ty.value & 0xFF) == ENUMTY.Taarray) || ((tob.value.ty.value & 0xFF) == ENUMTY.Tclass);
            boolean t1b_isR = t1b_isFR || ((t1b.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((t1b.value.ty.value & 0xFF) == ENUMTY.Taarray) || ((t1b.value.ty.value & 0xFF) == ENUMTY.Tclass);
            boolean tob_isA = tob.value.isintegral() || tob.value.isfloating();
            boolean t1b_isA = t1b.value.isintegral() || t1b.value.isfloating();
            Ref<Boolean> hasAliasThis = ref(false);
            try {
                {
                    Ref<AggregateDeclaration> t1ad = ref(isAggregate(t1b.value));
                    if ((t1ad.value) != null)
                    {
                        Ref<AggregateDeclaration> toad = ref(isAggregate(tob.value));
                        if ((!pequals(t1ad.value, toad.value)) && (t1ad.value.aliasthis.value != null))
                        {
                            if (((t1b.value.ty.value & 0xFF) == ENUMTY.Tclass) && ((tob.value.ty.value & 0xFF) == ENUMTY.Tclass))
                            {
                                Ref<ClassDeclaration> t1cd = ref(t1b.value.isClassHandle());
                                ClassDeclaration tocd = tob.value.isClassHandle();
                                IntRef offset = ref(0);
                                if (tocd.isBaseOf(t1cd.value, ptr(offset)))
                                {
                                    /*goto Lok*/throw Dispatch0.INSTANCE;
                                }
                            }
                            hasAliasThis.value = true;
                        }
                    }
                    else if (((tob.value.ty.value & 0xFF) == ENUMTY.Tvector) && ((t1b.value.ty.value & 0xFF) != ENUMTY.Tvector))
                    {
                        TypeVector tv = (TypeVector)tob.value;
                        this.result.value = new CastExp(e_ref.value.loc.value, e_ref.value, tv.elementType());
                        this.result.value = new VectorExp(e_ref.value.loc.value, this.result.value, tob.value);
                        this.result.value = expressionSemantic(this.result.value, this.sc.value);
                        return ;
                    }
                    else if (((tob.value.ty.value & 0xFF) != ENUMTY.Tvector) && ((t1b.value.ty.value & 0xFF) == ENUMTY.Tvector))
                    {
                        if (((tob.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            if ((t1b.value.size(e_ref.value.loc.value) == tob.value.size(e_ref.value.loc.value)))
                            {
                                /*goto Lok*/throw Dispatch0.INSTANCE;
                            }
                        }
                        /*goto Lfail*//*unrolled goto*/
                    /*Lfail:*/
                        if (hasAliasThis.value)
                        {
                            this.result.value = tryAliasThisCast(e_ref.value, this.sc.value, tob.value, t1b.value, this.t.value);
                            if (this.result.value != null)
                            {
                                return ;
                            }
                        }
                        e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.value.toChars());
                        this.result.value = new ErrorExp();
                        return ;
                    }
                    else if ((t1b.value.implicitConvTo(tob.value) == MATCH.constant) && this.t.value.equals(e_ref.value.type.value.constOf()))
                    {
                        this.result.value = e_ref.value.copy();
                        this.result.value.type.value = this.t.value;
                        return ;
                    }
                }
                if (tob_isA && t1b_isA || ((t1b.value.ty.value & 0xFF) == ENUMTY.Tpointer) || t1b_isA && tob_isA || ((tob.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                }
                if (tob_isA && t1b_isR || t1b_isFV || t1b_isA && tob_isR || tob_isFV)
                {
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result.value = tryAliasThisCast(e_ref.value, this.sc.value, tob.value, t1b.value, this.t.value);
                        if (this.result.value != null)
                        {
                            return ;
                        }
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.value.toChars());
                    this.result.value = new ErrorExp();
                    return ;
                }
                if (tob_isFV && t1b_isFV)
                {
                    if (hasAliasThis.value)
                    {
                        this.result.value = tryAliasThisCast(e_ref.value, this.sc.value, tob.value, t1b.value, this.t.value);
                        if (this.result.value != null)
                        {
                            return ;
                        }
                    }
                    if ((t1b.value.size(e_ref.value.loc.value) == tob.value.size(e_ref.value.loc.value)))
                    {
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    Slice<BytePtr> ts = toAutoQualChars(e_ref.value.type.value, this.t.value);
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s` because of different sizes"), e_ref.value.toChars(), ts.get(0), ts.get(1));
                    this.result.value = new ErrorExp();
                    return ;
                }
                if (tob_isFV && ((t1b.value.ty.value & 0xFF) == ENUMTY.Tnull) || t1b_isR || t1b_isFV && ((tob.value.ty.value & 0xFF) == ENUMTY.Tnull) || tob_isR)
                {
                    if (((tob.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t1b.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                    {
                        this.result.value = new AddrExp(e_ref.value.loc.value, e_ref.value, this.t.value);
                        return ;
                    }
                    if (((tob.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((t1b.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                    {
                        Ref<Long> fsize = ref(t1b.value.nextOf().size());
                        Ref<Long> tsize = ref(tob.value.nextOf().size());
                        if ((((TypeSArray)t1b.value).dim.value.toInteger() * fsize.value % tsize.value != 0L))
                        {
                            e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s` since sizes don't line up"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.value.toChars());
                            this.result.value = new ErrorExp();
                            return ;
                        }
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result.value = tryAliasThisCast(e_ref.value, this.sc.value, tob.value, t1b.value, this.t.value);
                        if (this.result.value != null)
                        {
                            return ;
                        }
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.value.toChars());
                    this.result.value = new ErrorExp();
                    return ;
                }
                if (((tob.value.ty.value & 0xFF) == (t1b.value.ty.value & 0xFF)) && tob_isR && t1b_isR)
                {
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                }
                if (((tob.value.ty.value & 0xFF) == ENUMTY.Tnull) && ((t1b.value.ty.value & 0xFF) != ENUMTY.Tnull))
                {
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result.value = tryAliasThisCast(e_ref.value, this.sc.value, tob.value, t1b.value, this.t.value);
                        if (this.result.value != null)
                        {
                            return ;
                        }
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.value.toChars());
                    this.result.value = new ErrorExp();
                    return ;
                }
                if (((t1b.value.ty.value & 0xFF) == ENUMTY.Tnull) && ((tob.value.ty.value & 0xFF) != ENUMTY.Tnull))
                {
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                }
                if (tob_isFR && t1b_isR || t1b_isFR && tob_isR)
                {
                    if (((tob.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t1b.value.ty.value & 0xFF) == ENUMTY.Tarray))
                    {
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    if (((tob.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t1b.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                    {
                        e_ref.value.deprecation(new BytePtr("casting from %s to %s is deprecated"), e_ref.value.type.value.toChars(), this.t.value.toChars());
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result.value = tryAliasThisCast(e_ref.value, this.sc.value, tob.value, t1b.value, this.t.value);
                        if (this.result.value != null)
                        {
                            return ;
                        }
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.value.toChars());
                    this.result.value = new ErrorExp();
                    return ;
                }
                if (((t1b.value.ty.value & 0xFF) == ENUMTY.Tvoid) && ((tob.value.ty.value & 0xFF) != ENUMTY.Tvoid))
                {
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result.value = tryAliasThisCast(e_ref.value, this.sc.value, tob.value, t1b.value, this.t.value);
                        if (this.result.value != null)
                        {
                            return ;
                        }
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.value.toChars());
                    this.result.value = new ErrorExp();
                    return ;
                }
            }
            catch(Dispatch0 __d){}
        /*Lok:*/
            this.result.value = new CastExp(e_ref.value.loc.value, e_ref.value, this.t.value);
            this.result.value.type.value = this.t.value;
        }

        public  void visit(ErrorExp e) {
            Ref<ErrorExp> e_ref = ref(e);
            this.result.value = e_ref.value;
        }

        public  void visit(RealExp e) {
            Ref<RealExp> e_ref = ref(e);
            if (!e_ref.value.type.value.equals(this.t.value))
            {
                if (e_ref.value.type.value.isreal() && this.t.value.isreal() || e_ref.value.type.value.isimaginary() && this.t.value.isimaginary())
                {
                    this.result.value = e_ref.value.copy();
                    this.result.value.type.value = this.t.value;
                }
                else
                {
                    this.visit((Expression)e_ref);
                }
                return ;
            }
            this.result.value = e_ref.value;
        }

        public  void visit(ComplexExp e) {
            Ref<ComplexExp> e_ref = ref(e);
            if (!e_ref.value.type.value.equals(this.t.value))
            {
                if (e_ref.value.type.value.iscomplex() && this.t.value.iscomplex())
                {
                    this.result.value = e_ref.value.copy();
                    this.result.value.type.value = this.t.value;
                }
                else
                {
                    this.visit((Expression)e_ref);
                }
                return ;
            }
            this.result.value = e_ref.value;
        }

        public  void visit(NullExp e) {
            Ref<NullExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.value.op.value & 0xFF) == 13))
            {
                NullExp ex = (NullExp)this.result.value;
                ex.committed.value = (byte)1;
                return ;
            }
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.value.op.value & 0xFF) == 49))
            {
                ((StructLiteralExp)this.result.value).stype.value = this.t.value;
            }
        }

        public  void visit(StringExp e) {
            Ref<StringExp> e_ref = ref(e);
            IntRef copied = ref(0);
            if ((e_ref.value.committed.value == 0) && ((this.t.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((this.t.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid))
            {
                e_ref.value.error(new BytePtr("cannot convert string literal to `void*`"));
                this.result.value = new ErrorExp();
                return ;
            }
            Ref<StringExp> se = ref(e_ref.value);
            if (e_ref.value.committed.value == 0)
            {
                se.value = (StringExp)e_ref.value.copy();
                se.value.committed.value = (byte)1;
                copied.value = 1;
            }
            if (e_ref.value.type.value.equals(this.t.value))
            {
                this.result.value = se.value;
                return ;
            }
            Ref<Type> tb = ref(this.t.value.toBasetype());
            Type typeb = e_ref.value.type.value.toBasetype();
            if (((tb.value.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((typeb.ty.value & 0xFF) != ENUMTY.Tdelegate))
            {
                this.visit((Expression)e_ref);
                return ;
            }
            if (typeb.equals(tb.value))
            {
                if (copied.value == 0)
                {
                    se.value = (StringExp)e_ref.value.copy();
                    copied.value = 1;
                }
                se.value.type.value = this.t.value;
                this.result.value = se.value;
                return ;
            }
            if ((e_ref.value.committed.value != 0) && ((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((typeb.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                se.value = (StringExp)e_ref.value.copy();
                Ref<Long> szx = ref(tb.value.nextOf().size());
                assert((szx.value <= 255L));
                se.value.sz.value = (byte)szx.value;
                se.value.len.value = (int)((TypeSArray)tb.value).dim.value.toInteger();
                se.value.committed.value = (byte)1;
                se.value.type.value = this.t.value;
                IntRef fullSize = ref((se.value.len.value + 1) * (se.value.sz.value & 0xFF));
                if ((fullSize.value > (e_ref.value.len.value + 1) * (e_ref.value.sz.value & 0xFF)))
                {
                    Ref<Object> s = ref(pcopy(Mem.xmalloc(fullSize.value)));
                    IntRef srcSize = ref(e_ref.value.len.value * (e_ref.value.sz.value & 0xFF));
                    memcpy((BytePtr)s.value, (se.value.string.value), srcSize.value);
                    memset(((BytePtr)s.value).plus(srcSize.value), 0, fullSize.value - srcSize.value);
                    se.value.string.value = pcopy((((BytePtr)s.value)));
                }
                this.result.value = se.value;
                return ;
            }
            try {
                try {
                    if (((tb.value.ty.value & 0xFF) != ENUMTY.Tsarray) && ((tb.value.ty.value & 0xFF) != ENUMTY.Tarray) && ((tb.value.ty.value & 0xFF) != ENUMTY.Tpointer))
                    {
                        if (copied.value == 0)
                        {
                            se.value = (StringExp)e_ref.value.copy();
                            copied.value = 1;
                        }
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    }
                    if (((typeb.ty.value & 0xFF) != ENUMTY.Tsarray) && ((typeb.ty.value & 0xFF) != ENUMTY.Tarray) && ((typeb.ty.value & 0xFF) != ENUMTY.Tpointer))
                    {
                        if (copied.value == 0)
                        {
                            se.value = (StringExp)e_ref.value.copy();
                            copied.value = 1;
                        }
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    }
                    if ((typeb.nextOf().size() == tb.value.nextOf().size()))
                    {
                        if (copied.value == 0)
                        {
                            se.value = (StringExp)e_ref.value.copy();
                            copied.value = 1;
                        }
                        if (((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            /*goto L2*/throw Dispatch0.INSTANCE;
                        }
                        se.value.type.value = this.t.value;
                        this.result.value = se.value;
                        return ;
                    }
                    if (e_ref.value.committed.value != 0)
                    {
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    }
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
                            IntRef newlen = ref(0);
                            IntRef tfty = ref((typeb.nextOf().toBasetype().ty.value & 0xFF));
                            IntRef ttty = ref((tb.value.nextOf().toBasetype().ty.value & 0xFF));
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
                                                for (; (u.value < e_ref.value.len.value);){
                                                    IntRef c = ref(0x0ffff);
                                                    Ref<BytePtr> p = ref(pcopy(utf_decodeChar(se.value.string.value, e_ref.value.len.value, u, c)));
                                                    if (p.value != null)
                                                    {
                                                        e_ref.value.error(new BytePtr("%s"), p.value);
                                                    }
                                                    else
                                                    {
                                                        buffer.writeUTF16(c.value);
                                                    }
                                                }
                                            }
                                            newlen.value = buffer.offset.value / 2;
                                            buffer.writeUTF16(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 7969:
                                            {
                                                IntRef u_1 = ref(0);
                                                for (; (u_1.value < e_ref.value.len.value);){
                                                    IntRef c_1 = ref(0x0ffff);
                                                    Ref<BytePtr> p_1 = ref(pcopy(utf_decodeChar(se.value.string.value, e_ref.value.len.value, u_1, c_1)));
                                                    if (p_1.value != null)
                                                    {
                                                        e_ref.value.error(new BytePtr("%s"), p_1.value);
                                                    }
                                                    buffer.write4(c_1.value);
                                                    newlen.value++;
                                                }
                                            }
                                            buffer.write4(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8223:
                                            {
                                                IntRef u_2 = ref(0);
                                                for (; (u_2.value < e_ref.value.len.value);){
                                                    IntRef c_2 = ref(0x0ffff);
                                                    Ref<BytePtr> p_2 = ref(pcopy(utf_decodeWchar(se.value.wstring.value, e_ref.value.len.value, u_2, c_2)));
                                                    if (p_2.value != null)
                                                    {
                                                        e_ref.value.error(new BytePtr("%s"), p_2.value);
                                                    }
                                                    else
                                                    {
                                                        buffer.writeUTF8(c_2.value);
                                                    }
                                                }
                                            }
                                            newlen.value = buffer.offset.value;
                                            buffer.writeUTF8(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8225:
                                            {
                                                IntRef u_3 = ref(0);
                                                for (; (u_3.value < e_ref.value.len.value);){
                                                    IntRef c_3 = ref(0x0ffff);
                                                    Ref<BytePtr> p_3 = ref(pcopy(utf_decodeWchar(se.value.wstring.value, e_ref.value.len.value, u_3, c_3)));
                                                    if (p_3.value != null)
                                                    {
                                                        e_ref.value.error(new BytePtr("%s"), p_3.value);
                                                    }
                                                    buffer.write4(c_3.value);
                                                    newlen.value++;
                                                }
                                            }
                                            buffer.write4(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8479:
                                            {
                                                IntRef u_4 = ref(0);
                                                for (; (u_4.value < e_ref.value.len.value);u_4.value++){
                                                    IntRef c_4 = ref(se.value.dstring.value.get(u_4.value));
                                                    if (!utf_isValidDchar(c_4.value))
                                                    {
                                                        e_ref.value.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_4.value);
                                                    }
                                                    else
                                                    {
                                                        buffer.writeUTF8(c_4.value);
                                                    }
                                                    newlen.value++;
                                                }
                                            }
                                            newlen.value = buffer.offset.value;
                                            buffer.writeUTF8(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8480:
                                            {
                                                IntRef u_5 = ref(0);
                                                for (; (u_5.value < e_ref.value.len.value);u_5.value++){
                                                    IntRef c_5 = ref(se.value.dstring.value.get(u_5.value));
                                                    if (!utf_isValidDchar(c_5.value))
                                                    {
                                                        e_ref.value.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_5.value);
                                                    }
                                                    else
                                                    {
                                                        buffer.writeUTF16(c_5.value);
                                                    }
                                                    newlen.value++;
                                                }
                                            }
                                            newlen.value = buffer.offset.value / 2;
                                            buffer.writeUTF16(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        /*L1:*/
                                        case -1:
                                        __dispatch4 = 0;
                                            if (copied.value == 0)
                                            {
                                                se.value = (StringExp)e_ref.value.copy();
                                                copied.value = 1;
                                            }
                                            se.value.string.value = pcopy(buffer.extractData());
                                            se.value.len.value = newlen.value;
                                            {
                                                Ref<Long> szx = ref(tb.value.nextOf().size());
                                                assert((szx.value <= 255L));
                                                se.value.sz.value = (byte)szx.value;
                                            }
                                            break;
                                        default:
                                        assert((typeb.nextOf().size() != tb.value.nextOf().size()));
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
                if (((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    IntRef dim2 = ref((int)((TypeSArray)tb.value).dim.value.toInteger());
                    if ((dim2.value != se.value.len.value))
                    {
                        IntRef newsz = ref((se.value.sz.value & 0xFF));
                        IntRef d = ref((dim2.value < se.value.len.value) ? dim2.value : se.value.len.value);
                        Ref<Object> s = ref(pcopy(Mem.xmalloc((dim2.value + 1) * newsz.value)));
                        memcpy((BytePtr)s.value, (se.value.string.value), (d.value * newsz.value));
                        memset(((BytePtr)s.value).plus((d.value * newsz.value)), 0, (dim2.value + 1 - d.value) * newsz.value);
                        se.value.string.value = pcopy((((BytePtr)s.value)));
                        se.value.len.value = dim2.value;
                    }
                }
                se.value.type.value = this.t.value;
                this.result.value = se.value;
                return ;
            }
            catch(Dispatch1 __d){}
        /*Lcast:*/
            this.result.value = new CastExp(e_ref.value.loc.value, se.value, this.t.value);
            this.result.value.type.value = this.t.value;
        }

        public  void visit(AddrExp e) {
            Ref<AddrExp> e_ref = ref(e);
            this.result.value = e_ref.value;
            Type tb = this.t.value.toBasetype();
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (tb.equals(typeb.value))
            {
                this.result.value = e_ref.value.copy();
                this.result.value.type.value = this.t.value;
                return ;
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 214) && ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) || ((tb.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                OverExp eo = (OverExp)e_ref.value.e1.value;
                Ref<FuncDeclaration> f = ref(null);
                {
                    IntRef i = ref(0);
                    for (; (i.value < eo.vars.a.length.value);i.value++){
                        Dsymbol s = eo.vars.a.get(i.value);
                        Ref<FuncDeclaration> f2 = ref(s.isFuncDeclaration());
                        assert(f2.value != null);
                        if (f2.value.overloadExactMatch(tb.nextOf()) != null)
                        {
                            if (f.value != null)
                            {
                                ScopeDsymbol.multiplyDefined(e_ref.value.loc.value, f.value, f2.value);
                            }
                            else
                            {
                                f.value = f2.value;
                            }
                        }
                    }
                }
                if (f.value != null)
                {
                    f.value.tookAddressOf.value++;
                    Ref<SymOffExp> se = ref(new SymOffExp(e_ref.value.loc.value, f.value, 0L, false));
                    expressionSemantic(se.value, this.sc.value);
                    this.visit(se.value);
                    return ;
                }
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 26) && ((typeb.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((typeb.value.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction) && ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) && ((tb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                VarExp ve = (VarExp)e_ref.value.e1.value;
                Ref<FuncDeclaration> f = ref(ve.var.value.isFuncDeclaration());
                if (f.value != null)
                {
                    assert(f.value.isImportedSymbol());
                    f.value = f.value.overloadExactMatch(tb.nextOf());
                    if (f.value != null)
                    {
                        this.result.value = new VarExp(e_ref.value.loc.value, f.value, false);
                        this.result.value.type.value = f.value.type.value;
                        this.result.value = new AddrExp(e_ref.value.loc.value, this.result.value, this.t.value);
                        return ;
                    }
                }
            }
            {
                Ref<FuncDeclaration> f = ref(isFuncAddress(e_ref.value, null));
                if ((f.value) != null)
                {
                    if (f.value.checkForwardRef(e_ref.value.loc.value))
                    {
                        this.result.value = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(TupleExp e) {
            Ref<TupleExp> e_ref = ref(e);
            if (e_ref.value.type.value.equals(this.t.value))
            {
                this.result.value = e_ref.value;
                return ;
            }
            Ref<TupleExp> te = ref((TupleExp)e_ref.value.copy());
            te.value.e0.value = e_ref.value.e0.value != null ? e_ref.value.e0.value.copy() : null;
            te.value.exps.value = (e_ref.value.exps.value.get()).copy();
            {
                IntRef i = ref(0);
                for (; (i.value < (te.value.exps.value.get()).length.value);i.value++){
                    Ref<Expression> ex = ref((te.value.exps.value.get()).get(i.value));
                    ex.value = ex.value.castTo(this.sc.value, this.t.value);
                    te.value.exps.value.get().set(i.value, ex.value);
                }
            }
            this.result.value = te.value;
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            Ref<ArrayLiteralExp> ae = ref(e_ref.value);
            Ref<Type> tb = ref(this.t.value.toBasetype());
            if (((tb.value.ty.value & 0xFF) == ENUMTY.Tarray) && global.params.vsafe.value)
            {
                if (checkArrayLiteralEscape(this.sc.value, ae.value, false))
                {
                    this.result.value = new ErrorExp();
                    return ;
                }
            }
            if ((pequals(e_ref.value.type.value, this.t.value)))
            {
                this.result.value = e_ref.value;
                return ;
            }
            Type typeb = e_ref.value.type.value.toBasetype();
            try {
                if (((tb.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((typeb.ty.value & 0xFF) == ENUMTY.Tarray) || ((typeb.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    if (((tb.value.nextOf().toBasetype().ty.value & 0xFF) == ENUMTY.Tvoid) && ((typeb.nextOf().toBasetype().ty.value & 0xFF) != ENUMTY.Tvoid))
                    {
                    }
                    else if (((typeb.ty.value & 0xFF) == ENUMTY.Tsarray) && ((typeb.nextOf().toBasetype().ty.value & 0xFF) == ENUMTY.Tvoid))
                    {
                    }
                    else
                    {
                        if (((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            TypeSArray tsa = (TypeSArray)tb.value;
                            if (((long)(e_ref.value.elements.value.get()).length.value != tsa.dim.value.toInteger()))
                            {
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                        }
                        ae.value = (ArrayLiteralExp)e_ref.value.copy();
                        if (e_ref.value.basis.value != null)
                        {
                            ae.value.basis.value = e_ref.value.basis.value.castTo(this.sc.value, tb.value.nextOf());
                        }
                        ae.value.elements.value = (e_ref.value.elements.value.get()).copy();
                        {
                            IntRef i = ref(0);
                            for (; (i.value < (e_ref.value.elements.value.get()).length.value);i.value++){
                                Ref<Expression> ex = ref((e_ref.value.elements.value.get()).get(i.value));
                                if (ex.value == null)
                                {
                                    continue;
                                }
                                ex.value = ex.value.castTo(this.sc.value, tb.value.nextOf());
                                ae.value.elements.value.get().set(i.value, ex.value);
                            }
                        }
                        ae.value.type.value = this.t.value;
                        this.result.value = ae.value;
                        return ;
                    }
                }
                else if (((tb.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((typeb.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<Type> tp = ref(typeb.nextOf().pointerTo());
                    if (!tp.value.equals(ae.value.type.value))
                    {
                        ae.value = (ArrayLiteralExp)e_ref.value.copy();
                        ae.value.type.value = tp.value;
                    }
                }
                else if (((tb.value.ty.value & 0xFF) == ENUMTY.Tvector) && ((typeb.ty.value & 0xFF) == ENUMTY.Tarray) || ((typeb.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    TypeVector tv = (TypeVector)tb.value;
                    Ref<TypeSArray> tbase = ref((TypeSArray)tv.basetype.value);
                    assert(((tbase.value.ty.value & 0xFF) == ENUMTY.Tsarray));
                    IntRef edim = ref((e_ref.value.elements.value.get()).length.value);
                    Ref<Long> tbasedim = ref(tbase.value.dim.value.toInteger());
                    if (((long)edim.value > tbasedim.value))
                    {
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                    ae.value = (ArrayLiteralExp)e_ref.value.copy();
                    ae.value.type.value = tbase.value;
                    ae.value.elements.value = (e_ref.value.elements.value.get()).copy();
                    Ref<Type> telement = ref(tv.elementType());
                    {
                        IntRef __key901 = ref(0);
                        IntRef __limit902 = ref(edim.value);
                        for (; (__key901.value < __limit902.value);__key901.value += 1) {
                            IntRef i = ref(__key901.value);
                            Ref<Expression> ex = ref((e_ref.value.elements.value.get()).get(i.value));
                            ex.value = ex.value.castTo(this.sc.value, telement.value);
                            ae.value.elements.value.get().set(i.value, ex.value);
                        }
                    }
                    (ae.value.elements.value.get()).setDim((int)tbasedim.value);
                    {
                        IntRef __key903 = ref(edim.value);
                        IntRef __limit904 = ref((int)tbasedim.value);
                        for (; (__key903.value < __limit904.value);__key903.value += 1) {
                            IntRef i = ref(__key903.value);
                            Ref<Expression> ex = ref(typeb.nextOf().defaultInitLiteral(e_ref.value.loc.value));
                            ex.value = ex.value.castTo(this.sc.value, telement.value);
                            ae.value.elements.value.get().set(i.value, ex.value);
                        }
                    }
                    Ref<Expression> ev = ref(new VectorExp(e_ref.value.loc.value, ae.value, tb.value));
                    ev.value = expressionSemantic(ev.value, this.sc.value);
                    this.result.value = ev.value;
                    return ;
                }
            }
            catch(Dispatch0 __d){}
        /*L1:*/
            this.visit((Expression)ae);
        }

        public  void visit(AssocArrayLiteralExp e) {
            Ref<AssocArrayLiteralExp> e_ref = ref(e);
            if ((pequals(e_ref.value.type.value, this.t.value)))
            {
                this.result.value = e_ref.value;
                return ;
            }
            Type tb = this.t.value.toBasetype();
            Type typeb = e_ref.value.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Taarray) && ((typeb.ty.value & 0xFF) == ENUMTY.Taarray) && ((tb.nextOf().toBasetype().ty.value & 0xFF) != ENUMTY.Tvoid))
            {
                Ref<AssocArrayLiteralExp> ae = ref((AssocArrayLiteralExp)e_ref.value.copy());
                ae.value.keys.value = (e_ref.value.keys.value.get()).copy();
                ae.value.values.value = (e_ref.value.values.value.get()).copy();
                assert(((e_ref.value.keys.value.get()).length.value == (e_ref.value.values.value.get()).length.value));
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.keys.value.get()).length.value);i.value++){
                        Ref<Expression> ex = ref((e_ref.value.values.value.get()).get(i.value));
                        ex.value = ex.value.castTo(this.sc.value, tb.nextOf());
                        ae.value.values.value.get().set(i.value, ex.value);
                        ex.value = (e_ref.value.keys.value.get()).get(i.value);
                        ex.value = ex.value.castTo(this.sc.value, ((TypeAArray)tb).index.value);
                        ae.value.keys.value.get().set(i.value, ex.value);
                    }
                }
                ae.value.type.value = this.t.value;
                this.result.value = ae.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(SymOffExp e) {
            Ref<SymOffExp> e_ref = ref(e);
            if ((pequals(e_ref.value.type.value, this.t.value)) && !e_ref.value.hasOverloads.value)
            {
                this.result.value = e_ref.value;
                return ;
            }
            Type tb = this.t.value.toBasetype();
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (tb.equals(typeb.value))
            {
                this.result.value = e_ref.value.copy();
                this.result.value.type.value = this.t.value;
                ((SymOffExp)this.result.value).hasOverloads.value = false;
                return ;
            }
            if (e_ref.value.hasOverloads.value && ((typeb.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((typeb.value.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction) && ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) || ((tb.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                Ref<FuncDeclaration> f = ref(e_ref.value.var.value.isFuncDeclaration());
                f.value = f.value != null ? f.value.overloadExactMatch(tb.nextOf()) : null;
                if (f.value != null)
                {
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tdelegate))
                    {
                        if (f.value.needThis() && (hasThis(this.sc.value) != null))
                        {
                            this.result.value = new DelegateExp(e_ref.value.loc.value, new ThisExp(e_ref.value.loc.value), f.value, false, null);
                            this.result.value = expressionSemantic(this.result.value, this.sc.value);
                        }
                        else if (f.value.needThis())
                        {
                            e_ref.value.error(new BytePtr("no `this` to create delegate for `%s`"), f.value.toChars());
                            this.result.value = new ErrorExp();
                            return ;
                        }
                        else if (f.value.isNested())
                        {
                            this.result.value = new DelegateExp(e_ref.value.loc.value, literal_B6589FC6AB0DC82C(), f.value, false, null);
                            this.result.value = expressionSemantic(this.result.value, this.sc.value);
                        }
                        else
                        {
                            e_ref.value.error(new BytePtr("cannot cast from function pointer to delegate"));
                            this.result.value = new ErrorExp();
                            return ;
                        }
                    }
                    else
                    {
                        this.result.value = new SymOffExp(e_ref.value.loc.value, f.value, 0L, false);
                        this.result.value.type.value = this.t.value;
                    }
                    f.value.tookAddressOf.value++;
                    return ;
                }
            }
            {
                Ref<FuncDeclaration> f = ref(isFuncAddress(e_ref.value, null));
                if ((f.value) != null)
                {
                    if (f.value.checkForwardRef(e_ref.value.loc.value))
                    {
                        this.result.value = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(DelegateExp e) {
            Ref<DelegateExp> e_ref = ref(e);
            Type tb = this.t.value.toBasetype();
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (tb.equals(typeb.value) && !e_ref.value.hasOverloads.value)
            {
                IntRef offset = ref(0);
                e_ref.value.func.value.tookAddressOf.value++;
                if ((e_ref.value.func.value.tintro.value != null) && e_ref.value.func.value.tintro.value.nextOf().isBaseOf(e_ref.value.func.value.type.value.nextOf(), ptr(offset)) && (offset.value != 0))
                {
                    e_ref.value.error(new BytePtr("%s"), dcast.visitmsg.value);
                }
                this.result.value = e_ref.value.copy();
                this.result.value.type.value = this.t.value;
                return ;
            }
            if (((typeb.value.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((tb.ty.value & 0xFF) == ENUMTY.Tdelegate))
            {
                if (e_ref.value.func.value != null)
                {
                    Ref<FuncDeclaration> f = ref(e_ref.value.func.value.overloadExactMatch(tb.nextOf()));
                    if (f.value != null)
                    {
                        IntRef offset = ref(0);
                        if ((f.value.tintro.value != null) && f.value.tintro.value.nextOf().isBaseOf(f.value.type.value.nextOf(), ptr(offset)) && (offset.value != 0))
                        {
                            e_ref.value.error(new BytePtr("%s"), dcast.visitmsg.value);
                        }
                        if ((!pequals(f.value, e_ref.value.func.value)))
                        {
                            f.value.tookAddressOf.value++;
                        }
                        this.result.value = new DelegateExp(e_ref.value.loc.value, e_ref.value.e1.value, f.value, false, e_ref.value.vthis2.value);
                        this.result.value.type.value = this.t.value;
                        return ;
                    }
                    if (e_ref.value.func.value.tintro.value != null)
                    {
                        e_ref.value.error(new BytePtr("%s"), dcast.visitmsg.value);
                    }
                }
            }
            {
                Ref<FuncDeclaration> f = ref(isFuncAddress(e_ref.value, null));
                if ((f.value) != null)
                {
                    if (f.value.checkForwardRef(e_ref.value.loc.value))
                    {
                        this.result.value = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(FuncExp e) {
            Ref<FuncExp> e_ref = ref(e);
            Ref<FuncExp> fe = ref(null);
            if ((e_ref.value.matchType(this.t.value, this.sc.value, ptr(fe), 1) > MATCH.nomatch))
            {
                this.result.value = fe.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            if (!e_ref.value.type.value.equals(this.t.value))
            {
                this.result.value = new CondExp(e_ref.value.loc.value, e_ref.value.econd.value, e_ref.value.e1.value.castTo(this.sc.value, this.t.value), e_ref.value.e2.value.castTo(this.sc.value, this.t.value));
                this.result.value.type.value = this.t.value;
                return ;
            }
            this.result.value = e_ref.value;
        }

        public  void visit(CommaExp e) {
            Ref<CommaExp> e_ref = ref(e);
            Ref<Expression> e2c = ref(e_ref.value.e2.value.castTo(this.sc.value, this.t.value));
            if ((!pequals(e2c.value, e_ref.value.e2.value)))
            {
                this.result.value = new CommaExp(e_ref.value.loc.value, e_ref.value.e1.value, e2c.value, true);
                this.result.value.type.value = e2c.value.type.value;
            }
            else
            {
                this.result.value = e_ref.value;
                this.result.value.type.value = e_ref.value.e2.value.type.value;
            }
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            Type tb = this.t.value.toBasetype();
            Type typeb = e_ref.value.type.value.toBasetype();
            if (e_ref.value.type.value.equals(this.t.value) || ((typeb.ty.value & 0xFF) != ENUMTY.Tarray) || ((tb.ty.value & 0xFF) != ENUMTY.Tarray) && ((tb.ty.value & 0xFF) != ENUMTY.Tsarray))
            {
                this.visit((Expression)e_ref);
                return ;
            }
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                if (typeb.nextOf().equivalent(tb.nextOf()))
                {
                    this.result.value = e_ref.value.copy();
                    this.result.value.type.value = this.t.value;
                }
                else
                {
                    this.visit((Expression)e_ref);
                }
                return ;
            }
            Ref<TypeSArray> tsa = ref((TypeSArray)toStaticArrayType(e_ref.value));
            if ((tsa.value != null) && (tsa.value.size(e_ref.value.loc.value) == tb.size(e_ref.value.loc.value)))
            {
                this.result.value = e_ref.value.copy();
                this.result.value.type.value = this.t.value;
                return ;
            }
            if ((tsa.value != null) && tsa.value.dim.value.equals(((TypeSArray)tb).dim.value))
            {
                Ref<Type> t1b = ref(e_ref.value.e1.value.type.value.toBasetype());
                if (((t1b.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    t1b.value = tb.nextOf().sarrayOf(((TypeSArray)t1b.value).dim.value.toInteger());
                }
                else if (((t1b.value.ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    t1b.value = tb.nextOf().arrayOf();
                }
                else if (((t1b.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    t1b.value = tb.nextOf().pointerTo();
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
                if ((e_ref.value.e1.value.implicitConvTo(t1b.value) > MATCH.nomatch))
                {
                    Ref<Expression> e1x = ref(e_ref.value.e1.value.implicitCastTo(this.sc.value, t1b.value));
                    assert(((e1x.value.op.value & 0xFF) != 127));
                    e_ref.value = (SliceExp)e_ref.value.copy();
                    e_ref.value.e1.value = e1x.value;
                    e_ref.value.type.value = this.t.value;
                    this.result.value = e_ref.value;
                    return ;
                }
            }
            Slice<BytePtr> ts = toAutoQualChars(tsa.value != null ? tsa.value : e_ref.value.type.value, this.t.value);
            e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), ts.get(0), ts.get(1));
            this.result.value = new ErrorExp();
        }


        public CastTo() {}
    }
    private static class InferType extends Visitor
    {
        private Ref<Type> t = ref(null);
        private IntRef flag = ref(0);
        private Ref<Expression> result = ref(null);
        public  InferType(Type t, int flag) {
            Ref<Type> t_ref = ref(t);
            IntRef flag_ref = ref(flag);
            this.t.value = t_ref.value;
            this.flag.value = flag_ref.value;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            this.result.value = e_ref.value;
        }

        public  void visit(ArrayLiteralExp ale) {
            Ref<ArrayLiteralExp> ale_ref = ref(ale);
            Type tb = this.t.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                Ref<Type> tn = ref(tb.nextOf());
                if (ale_ref.value.basis.value != null)
                {
                    ale_ref.value.basis.value = inferType(ale_ref.value.basis.value, tn.value, this.flag.value);
                }
                {
                    IntRef i = ref(0);
                    for (; (i.value < (ale_ref.value.elements.value.get()).length.value);i.value++){
                        Ref<Expression> e = ref((ale_ref.value.elements.value.get()).get(i.value));
                        if (e.value != null)
                        {
                            e.value = inferType(e.value, tn.value, this.flag.value);
                            ale_ref.value.elements.value.get().set(i.value, e.value);
                        }
                    }
                }
            }
            this.result.value = ale_ref.value;
        }

        public  void visit(AssocArrayLiteralExp aale) {
            Ref<AssocArrayLiteralExp> aale_ref = ref(aale);
            Ref<Type> tb = ref(this.t.value.toBasetype());
            if (((tb.value.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                TypeAArray taa = (TypeAArray)tb.value;
                Ref<Type> ti = ref(taa.index.value);
                Ref<Type> tv = ref(taa.nextOf());
                {
                    IntRef i = ref(0);
                    for (; (i.value < (aale_ref.value.keys.value.get()).length.value);i.value++){
                        Ref<Expression> e = ref((aale_ref.value.keys.value.get()).get(i.value));
                        if (e.value != null)
                        {
                            e.value = inferType(e.value, ti.value, this.flag.value);
                            aale_ref.value.keys.value.get().set(i.value, e.value);
                        }
                    }
                }
                {
                    IntRef i = ref(0);
                    for (; (i.value < (aale_ref.value.values.value.get()).length.value);i.value++){
                        Ref<Expression> e = ref((aale_ref.value.values.value.get()).get(i.value));
                        if (e.value != null)
                        {
                            e.value = inferType(e.value, tv.value, this.flag.value);
                            aale_ref.value.values.value.get().set(i.value, e.value);
                        }
                    }
                }
            }
            this.result.value = aale_ref.value;
        }

        public  void visit(FuncExp fe) {
            Ref<FuncExp> fe_ref = ref(fe);
            if (((this.t.value.ty.value & 0xFF) == ENUMTY.Tdelegate) || ((this.t.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((this.t.value.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                fe_ref.value.fd.value.treq.value = this.t.value;
            }
            this.result.value = fe_ref.value;
        }

        public  void visit(CondExp ce) {
            Ref<CondExp> ce_ref = ref(ce);
            Ref<Type> tb = ref(this.t.value.toBasetype());
            ce_ref.value.e1.value = inferType(ce_ref.value.e1.value, tb.value, this.flag.value);
            ce_ref.value.e2.value = inferType(ce_ref.value.e2.value, tb.value, this.flag.value);
            this.result.value = ce_ref.value;
        }


        public InferType() {}
    }
    private static class IntRangeVisitor extends Visitor
    {
        private Ref<IntRange> range = ref(new IntRange());
        public  void visit(Expression e) {
            this.range.value = IntRange.fromType(e.type.value).copy();
        }

        public  void visit(IntegerExp e) {
            this.range.value = new IntRange(new SignExtendedNumber(e.getInteger(), false))._cast(e.type.value).copy();
        }

        public  void visit(CastExp e) {
            this.range.value = getIntRange(e.e1.value)._cast(e.type.value).copy();
        }

        public  void visit(AddExp e) {
            IntRange ir1 = getIntRange(e.e1.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.opBinary_plus(ir2)._cast(e.type.value).copy();
        }

        public  void visit(MinExp e) {
            IntRange ir1 = getIntRange(e.e1.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.opBinary_minus(ir2)._cast(e.type.value).copy();
        }

        public  void visit(DivExp e) {
            IntRange ir1 = getIntRange(e.e1.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.opBinary_div(ir2)._cast(e.type.value).copy();
        }

        public  void visit(MulExp e) {
            IntRange ir1 = getIntRange(e.e1.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.opBinary_mul(ir2)._cast(e.type.value).copy();
        }

        public  void visit(ModExp e) {
            Ref<ModExp> e_ref = ref(e);
            IntRange ir1 = getIntRange(e_ref.value.e1.value).copy();
            IntRange ir2 = getIntRange(e_ref.value.e2.value).copy();
            if (!ir2.absNeg().imin.negative.value)
            {
                this.visit((Expression)e_ref);
                return ;
            }
            this.range.value = ir1.opBinary_mod(ir2)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(AndExp e) {
            IntRange result = new IntRange();
            Ref<Boolean> hasResult = ref(false);
            result.unionOrAssign(getIntRange(e.e1.value).opBinary__(getIntRange(e.e2.value)), hasResult);
            assert(hasResult.value);
            this.range.value = result._cast(e.type.value).copy();
        }

        public  void visit(OrExp e) {
            IntRange result = new IntRange();
            Ref<Boolean> hasResult = ref(false);
            result.unionOrAssign(getIntRange(e.e1.value).opBinary__(getIntRange(e.e2.value)), hasResult);
            assert(hasResult.value);
            this.range.value = result._cast(e.type.value).copy();
        }

        public  void visit(XorExp e) {
            IntRange result = new IntRange();
            Ref<Boolean> hasResult = ref(false);
            result.unionOrAssign(getIntRange(e.e1.value).opBinary__(getIntRange(e.e2.value)), hasResult);
            assert(hasResult.value);
            this.range.value = result._cast(e.type.value).copy();
        }

        public  void visit(ShlExp e) {
            IntRange ir1 = getIntRange(e.e1.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.opBinary_ll(ir2)._cast(e.type.value).copy();
        }

        public  void visit(ShrExp e) {
            IntRange ir1 = getIntRange(e.e1.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.opBinary_rr(ir2)._cast(e.type.value).copy();
        }

        public  void visit(UshrExp e) {
            IntRange ir1 = getIntRange(e.e1.value).castUnsigned(e.e1.value.type.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.opBinary_rrr(ir2)._cast(e.type.value).copy();
        }

        public  void visit(AssignExp e) {
            this.range.value = getIntRange(e.e2.value)._cast(e.type.value).copy();
        }

        public  void visit(CondExp e) {
            IntRange ir1 = getIntRange(e.e1.value).copy();
            IntRange ir2 = getIntRange(e.e2.value).copy();
            this.range.value = ir1.unionWith(ir2)._cast(e.type.value).copy();
        }

        public  void visit(VarExp e) {
            Ref<VarExp> e_ref = ref(e);
            Ref<Expression> ie = ref(null);
            Ref<VarDeclaration> vd = ref(e_ref.value.var.value.isVarDeclaration());
            if ((vd.value != null) && (vd.value.range.value != null))
            {
                this.range.value = (vd.value.range.value.get())._cast(e_ref.value.type.value).copy();
            }
            else if ((vd.value != null) && (vd.value._init.value != null) && !vd.value.type.value.isMutable() && ((ie.value = vd.value.getConstInitializer(true)) != null))
            {
                ie.value.accept(this);
            }
            else
            {
                this.visit((Expression)e_ref);
            }
        }

        public  void visit(CommaExp e) {
            e.e2.value.accept(this);
        }

        public  void visit(ComExp e) {
            IntRange ir = getIntRange(e.e1.value).copy();
            this.range.value = new IntRange(new SignExtendedNumber(~ir.imax.value, !ir.imax.negative.value), new SignExtendedNumber(~ir.imin.value, !ir.imin.negative.value))._cast(e.type.value).copy();
        }

        public  void visit(NegExp e) {
            IntRange ir = getIntRange(e.e1.value).copy();
            this.range.value = ir.opUnary_minus()._cast(e.type.value).copy();
        }


        public IntRangeVisitor() {}
    }

    static boolean LOG = false;
    public static Expression implicitCastTo(Expression e, Ptr<Scope> sc, Type t) {
        // skipping duplicate class ImplicitCastTo
        ImplicitCastTo v = new ImplicitCastTo(sc, t);
        e.accept(v);
        return v.result.value;
    }

    public static int implicitConvTo(Expression e, Type t) {
        // skipping duplicate class ImplicitConvTo
        ImplicitConvTo v = new ImplicitConvTo(t);
        e.accept(v);
        return v.result.value;
    }

    public static Type toStaticArrayType(SliceExp e) {
        if ((e.lwr.value != null) && (e.upr.value != null))
        {
            Expression lwr = e.lwr.value.optimize(0, false);
            Expression upr = e.upr.value.optimize(0, false);
            if ((lwr.isConst() != 0) && (upr.isConst() != 0))
            {
                int len = (int)(upr.toUInteger() - lwr.toUInteger());
                return e.type.value.toBasetype().nextOf().sarrayOf((long)len);
            }
        }
        else
        {
            Type t1b = e.e1.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                return t1b;
            }
        }
        return null;
    }

    public static Expression tryAliasThisCast(Expression e, Ptr<Scope> sc, Type tob, Type t1b, Type t) {
        Expression result = null;
        AggregateDeclaration t1ad = isAggregate(t1b);
        if (t1ad == null)
        {
            return null;
        }
        AggregateDeclaration toad = isAggregate(tob);
        if ((pequals(t1ad, toad)) || (t1ad.aliasthis.value == null))
        {
            return null;
        }
        result = resolveAliasThis(sc, e, false);
        int errors = global.startGagging();
        result = result.castTo(sc, t);
        return global.endGagging(errors) ? null : result;
    }

    public static Expression castTo(Expression e, Ptr<Scope> sc, Type t) {
        // skipping duplicate class CastTo
        CastTo v = new CastTo(sc, t);
        e.accept(v);
        return v.result.value;
    }

    public static Expression inferType(Expression e, Type t, int flag) {
        // skipping duplicate class InferType
        if (t == null)
        {
            return e;
        }
        InferType v = new InferType(t, flag);
        e.accept(v);
        return v.result.value;
    }

    // defaulted all parameters starting with #3
    public static Expression inferType(Expression e, Type t) {
        return inferType(e, t, 0);
    }

    public static Expression scaleFactor(BinExp be, Ptr<Scope> sc) {
        Type t1b = be.e1.value.type.value.toBasetype();
        Type t2b = be.e2.value.type.value.toBasetype();
        Expression eoff = null;
        if (((t1b.ty.value & 0xFF) == ENUMTY.Tpointer) && t2b.isintegral())
        {
            Type t = Type.tptrdiff_t;
            long stride = t1b.nextOf().size(be.loc.value);
            if (!t.equals(t2b))
            {
                be.e2.value = be.e2.value.castTo(sc, t);
            }
            eoff = be.e2.value;
            be.e2.value = new MulExp(be.loc.value, be.e2.value, new IntegerExp(Loc.initial.value, stride, t));
            be.e2.value.type.value = t;
            be.type.value = be.e1.value.type.value;
        }
        else if (((t2b.ty.value & 0xFF) == ENUMTY.Tpointer) && t1b.isintegral())
        {
            Type t = Type.tptrdiff_t;
            Expression e = null;
            long stride = t2b.nextOf().size(be.loc.value);
            if (!t.equals(t1b))
            {
                e = be.e1.value.castTo(sc, t);
            }
            else
            {
                e = be.e1.value;
            }
            eoff = e;
            e = new MulExp(be.loc.value, e, new IntegerExp(Loc.initial.value, stride, t));
            e.type.value = t;
            be.type.value = be.e2.value.type.value;
            be.e1.value = be.e2.value;
            be.e2.value = e;
        }
        else
        {
            throw new AssertionError("Unreachable code!");
        }
        if (((sc.get()).func.value != null) && ((sc.get()).intypeof.value == 0))
        {
            eoff = eoff.optimize(0, false);
            if (((eoff.op.value & 0xFF) == 135) && (eoff.toInteger() == 0L))
            {
            }
            else if ((sc.get()).func.value.setUnsafe())
            {
                be.error(new BytePtr("pointer arithmetic not allowed in @safe functions"));
                return new ErrorExp();
            }
        }
        return be;
    }

    public static boolean isVoidArrayLiteral(Expression e, Type other) {
        for (; ((e.op.value & 0xFF) == 47) && ((e.type.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((((ArrayLiteralExp)e).elements.value.get()).length.value == 1);){
            ArrayLiteralExp ale = (ArrayLiteralExp)e;
            e = ale.getElement(0);
            if (((other.ty.value & 0xFF) == ENUMTY.Tsarray) || ((other.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                other = other.nextOf();
            }
            else
            {
                return false;
            }
        }
        if (((other.ty.value & 0xFF) != ENUMTY.Tsarray) && ((other.ty.value & 0xFF) != ENUMTY.Tarray))
        {
            return false;
        }
        Type t = e.type.value;
        return ((e.op.value & 0xFF) == 47) && ((t.ty.value & 0xFF) == ENUMTY.Tarray) && ((t.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid) && ((((ArrayLiteralExp)e).elements.value.get()).length.value == 0);
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
                {
                    pt_ref.value.set(0, t.value);
                }
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
        if (((op & 0xFF) != 100) || ((t1b.ty.value & 0xFF) != (t2b.ty.value & 0xFF)) && (t1b.isTypeBasic() != null) && (t2b.isTypeBasic() != null))
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
        if (((t1.value.mod.value & 0xFF) != (t2.value.mod.value & 0xFF)) && ((t1.value.ty.value & 0xFF) == ENUMTY.Tenum) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tenum) && (pequals(((TypeEnum)t1.value).sym.value, ((TypeEnum)t2.value).sym.value)))
        {
            byte mod = MODmerge(t1.value.mod.value, t2.value.mod.value);
            t1.value = t1.value.castMod(mod);
            t2.value = t2.value.castMod(mod);
        }
        while(true) try {
        /*Lagain:*/
            t1b = t1.value.toBasetype();
            t2b = t2.value.toBasetype();
            byte ty = impcnvResult.get((t1b.ty.value & 0xFF)).get((t2b.ty.value & 0xFF));
            if (((ty & 0xFF) != ENUMTY.Terror))
            {
                byte ty1 = impcnvType1.get((t1b.ty.value & 0xFF)).get((t2b.ty.value & 0xFF));
                byte ty2 = impcnvType2.get((t1b.ty.value & 0xFF)).get((t2b.ty.value & 0xFF));
                if (((t1b.ty.value & 0xFF) == (ty1 & 0xFF)))
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
            if (((t1.value.ty.value & 0xFF) == ENUMTY.Ttuple) || ((t2.value.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                return Lincompatible.invoke();
            }
            if (t1.value.equals(t2.value))
            {
                if (((t.value.ty.value & 0xFF) == ENUMTY.Tenum))
                {
                    t.value = t1b;
                }
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((t1.value.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
            {
                Type t1n = t1.value.nextOf();
                Type t2n = t2.value.nextOf();
                if (t1n.equals(t2n))
                {
                }
                else if (((t1n.ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    t.value = t2.value;
                }
                else if (((t2n.ty.value & 0xFF) == ENUMTY.Tvoid))
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
                else if (((t1n.ty.value & 0xFF) == ENUMTY.Tfunction) && ((t2n.ty.value & 0xFF) == ENUMTY.Tfunction))
                {
                    TypeFunction tf1 = (TypeFunction)t1n;
                    TypeFunction tf2 = (TypeFunction)t2n;
                    tf1.purityLevel();
                    tf2.purityLevel();
                    TypeFunction d = (TypeFunction)tf1.syntaxCopy();
                    if ((tf1.purity.value != tf2.purity.value))
                    {
                        d.purity.value = PURE.impure;
                    }
                    assert((d.purity.value != PURE.fwdref));
                    d.isnothrow.value = tf1.isnothrow.value && tf2.isnothrow.value;
                    d.isnogc.value = tf1.isnogc.value && tf2.isnogc.value;
                    if ((tf1.trust.value == tf2.trust.value))
                    {
                        d.trust.value = tf1.trust.value;
                    }
                    else if ((tf1.trust.value <= TRUST.system) || (tf2.trust.value <= TRUST.system))
                    {
                        d.trust.value = TRUST.system;
                    }
                    else
                    {
                        d.trust.value = TRUST.trusted;
                    }
                    Type tx = null;
                    if (((t1.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                    {
                        tx = new TypeDelegate(d);
                    }
                    else
                    {
                        tx = d.pointerTo();
                    }
                    tx = typeSemantic(tx, e1.value.loc.value, sc_ref.value);
                    if ((t1.value.implicitConvTo(tx) != 0) && (t2.value.implicitConvTo(tx) != 0))
                    {
                        t.value = tx;
                        e1.value = e1.value.castTo(sc_ref.value, t.value);
                        e2.value = e2.value.castTo(sc_ref.value, t.value);
                        return Lret.invoke();
                    }
                    return Lincompatible.invoke();
                }
                else if (((t1n.mod.value & 0xFF) != (t2n.mod.value & 0xFF)))
                {
                    if (!t1n.isImmutable() && !t2n.isImmutable() && ((t1n.isShared() ? 1 : 0) != (t2n.isShared() ? 1 : 0)))
                    {
                        return Lincompatible.invoke();
                    }
                    byte mod = MODmerge(t1n.mod.value, t2n.mod.value);
                    t1.value = t1n.castMod(mod).pointerTo();
                    t2.value = t2n.castMod(mod).pointerTo();
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                else if (((t1n.ty.value & 0xFF) == ENUMTY.Tclass) && ((t2n.ty.value & 0xFF) == ENUMTY.Tclass))
                {
                    ClassDeclaration cd1 = t1n.isClassHandle();
                    ClassDeclaration cd2 = t2n.isClassHandle();
                    IntRef offset = ref(0);
                    if (cd1.isBaseOf(cd2, ptr(offset)))
                    {
                        if (offset.value != 0)
                        {
                            e2.value = e2.value.castTo(sc_ref.value, t.value);
                        }
                    }
                    else if (cd2.isBaseOf(cd1, ptr(offset)))
                    {
                        t.value = t2.value;
                        if (offset.value != 0)
                        {
                            e1.value = e1.value.castTo(sc_ref.value, t.value);
                        }
                    }
                    else
                    {
                        return Lincompatible.invoke();
                    }
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
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((e2.value.op.value & 0xFF) == 13) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t2.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid) || ((e2.value.op.value & 0xFF) == 47) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((t2.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid) && (((TypeSArray)t2.value).dim.value.toInteger() == 0L) || isVoidArrayLiteral(e2.value, t1.value))
            {
                /*goto Lx1*//*unrolled goto*/
            /*Lx1:*/
                t.value = t1.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((e1.value.op.value & 0xFF) == 13) && ((t1.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t1.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid) || ((e1.value.op.value & 0xFF) == 47) && ((t1.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((t1.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid) && (((TypeSArray)t1.value).dim.value.toInteger() == 0L) || isVoidArrayLiteral(e1.value, t2.value))
            {
                /*goto Lx2*//*unrolled goto*/
            /*Lx2:*/
                t.value = t2.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((m = t1.value.implicitConvTo(t2.value)) != MATCH.nomatch))
            {
                if (((t1.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((e2.value.op.value & 0xFF) == 47) && ((op & 0xFF) != 70))
                {
                    return Lt1.invoke();
                }
                if ((m == MATCH.constant) && ((op & 0xFF) == 76) || ((op & 0xFF) == 77) || ((op & 0xFF) == 81) || ((op & 0xFF) == 82) || ((op & 0xFF) == 83) || ((op & 0xFF) == 227) || ((op & 0xFF) == 87) || ((op & 0xFF) == 88) || ((op & 0xFF) == 89))
                {
                    t.value = t2.value;
                    return Lret.invoke();
                }
                return Lt2.invoke();
            }
            else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tarray) && (t2.value.implicitConvTo(t1.value) != 0))
            {
                if (((t2.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((e1.value.op.value & 0xFF) == 47) && ((op & 0xFF) != 70))
                {
                    return Lt2.invoke();
                }
                return Lt1.invoke();
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t1.value.nextOf().mod.value & 0xFF) != (t2.value.nextOf().mod.value & 0xFF)))
            {
                Type t1n = t1.value.nextOf();
                Type t2n = t2.value.nextOf();
                byte mod = (byte)0;
                if (((e1.value.op.value & 0xFF) == 13) && ((e2.value.op.value & 0xFF) != 13))
                {
                    mod = t2n.mod.value;
                }
                else if (((e1.value.op.value & 0xFF) != 13) && ((e2.value.op.value & 0xFF) == 13))
                {
                    mod = t1n.mod.value;
                }
                else if (!t1n.isImmutable() && !t2n.isImmutable() && ((t1n.isShared() ? 1 : 0) != (t2n.isShared() ? 1 : 0)))
                {
                    return Lincompatible.invoke();
                }
                else
                {
                    mod = MODmerge(t1n.mod.value, t2n.mod.value);
                }
                if (((t1.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    t1.value = t1n.castMod(mod).pointerTo();
                }
                else
                {
                    t1.value = t1n.castMod(mod).arrayOf();
                }
                if (((t2.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    t2.value = t2n.castMod(mod).pointerTo();
                }
                else
                {
                    t2.value = t2n.castMod(mod).arrayOf();
                }
                t.value = t1.value;
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                if (((t1.value.mod.value & 0xFF) != (t2.value.mod.value & 0xFF)))
                {
                    byte mod = (byte)0;
                    if (((e1.value.op.value & 0xFF) == 13) && ((e2.value.op.value & 0xFF) != 13))
                    {
                        mod = t2.value.mod.value;
                    }
                    else if (((e1.value.op.value & 0xFF) != 13) && ((e2.value.op.value & 0xFF) == 13))
                    {
                        mod = t1.value.mod.value;
                    }
                    else if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                    {
                        return Lincompatible.invoke();
                    }
                    else
                    {
                        mod = MODmerge(t1.value.mod.value, t2.value.mod.value);
                    }
                    t1.value = t1.value.castMod(mod);
                    t2.value = t2.value.castMod(mod);
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                /*goto Lcc*/throw Dispatch.INSTANCE;
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tclass) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tclass))
            {
            /*Lcc:*/
                for (; 1 != 0;){
                    int i1 = e2.value.implicitConvTo(t1.value);
                    int i2 = e1.value.implicitConvTo(t2.value);
                    if ((i1 != 0) && (i2 != 0))
                    {
                        if (((t1.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                        {
                            i1 = MATCH.nomatch;
                        }
                        else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                        {
                            i2 = MATCH.nomatch;
                        }
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
                    else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tclass))
                    {
                        TypeClass tc1 = (TypeClass)t1.value;
                        TypeClass tc2 = (TypeClass)t2.value;
                        ClassDeclaration cd1 = tc1.sym.value.baseClass.value;
                        ClassDeclaration cd2 = tc2.sym.value.baseClass.value;
                        if ((cd1 != null) && (cd2 != null))
                        {
                            t1.value = cd1.type.value.castMod(t1.value.mod.value);
                            t2.value = cd2.type.value.castMod(t2.value.mod.value);
                        }
                        else if (cd1 != null)
                        {
                            t1.value = cd1.type.value;
                        }
                        else if (cd2 != null)
                        {
                            t2.value = cd2.type.value;
                        }
                        else
                        {
                            return Lincompatible.invoke();
                        }
                    }
                    else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t1.value).sym.value.aliasthis.value != null))
                    {
                        if ((att1 != null) && (pequals(e1.value.type.value, att1)))
                        {
                            return Lincompatible.invoke();
                        }
                        if ((att1 == null) && e1.value.type.value.checkAliasThisRec())
                        {
                            att1 = e1.value.type.value;
                        }
                        e1.value = resolveAliasThis(sc_ref.value, e1.value, false);
                        t1.value = e1.value.type.value;
                        continue;
                    }
                    else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t2.value).sym.value.aliasthis.value != null))
                    {
                        if ((att2 != null) && (pequals(e2.value.type.value, att2)))
                        {
                            return Lincompatible.invoke();
                        }
                        if ((att2 == null) && e2.value.type.value.checkAliasThisRec())
                        {
                            att2 = e2.value.type.value;
                        }
                        e2.value = resolveAliasThis(sc_ref.value, e2.value, false);
                        t2.value = e2.value.type.value;
                        continue;
                    }
                    else
                    {
                        return Lincompatible.invoke();
                    }
                }
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tstruct) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                if (((t1.value.mod.value & 0xFF) != (t2.value.mod.value & 0xFF)))
                {
                    if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                    {
                        return Lincompatible.invoke();
                    }
                    byte mod = MODmerge(t1.value.mod.value, t2.value.mod.value);
                    t1.value = t1.value.castMod(mod);
                    t2.value = t2.value.castMod(mod);
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                TypeStruct ts1 = (TypeStruct)t1.value;
                TypeStruct ts2 = (TypeStruct)t2.value;
                if ((!pequals(ts1.sym.value, ts2.sym.value)))
                {
                    if ((ts1.sym.value.aliasthis.value == null) && (ts2.sym.value.aliasthis.value == null))
                    {
                        return Lincompatible.invoke();
                    }
                    int i1 = MATCH.nomatch;
                    int i2 = MATCH.nomatch;
                    Expression e1b = null;
                    Expression e2b = null;
                    if (ts2.sym.value.aliasthis.value != null)
                    {
                        if ((att2 != null) && (pequals(e2.value.type.value, att2)))
                        {
                            return Lincompatible.invoke();
                        }
                        if ((att2 == null) && e2.value.type.value.checkAliasThisRec())
                        {
                            att2 = e2.value.type.value;
                        }
                        e2b = resolveAliasThis(sc_ref.value, e2.value, false);
                        i1 = e2b.implicitConvTo(t1.value);
                    }
                    if (ts1.sym.value.aliasthis.value != null)
                    {
                        if ((att1 != null) && (pequals(e1.value.type.value, att1)))
                        {
                            return Lincompatible.invoke();
                        }
                        if ((att1 == null) && e1.value.type.value.checkAliasThisRec())
                        {
                            att1 = e1.value.type.value;
                        }
                        e1b = resolveAliasThis(sc_ref.value, e1.value, false);
                        i2 = e1b.implicitConvTo(t2.value);
                    }
                    if ((i1 != 0) && (i2 != 0))
                    {
                        return Lincompatible.invoke();
                    }
                    if (i1 != 0)
                    {
                        return Lt1.invoke();
                    }
                    else if (i2 != 0)
                    {
                        return Lt2.invoke();
                    }
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
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tstruct) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                if (((t1.value.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t1.value).sym.value.aliasthis.value != null))
                {
                    if ((att1 != null) && (pequals(e1.value.type.value, att1)))
                    {
                        return Lincompatible.invoke();
                    }
                    if ((att1 == null) && e1.value.type.value.checkAliasThisRec())
                    {
                        att1 = e1.value.type.value;
                    }
                    e1.value = resolveAliasThis(sc_ref.value, e1.value, false);
                    t1.value = e1.value.type.value;
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                if (((t2.value.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t2.value).sym.value.aliasthis.value != null))
                {
                    if ((att2 != null) && (pequals(e2.value.type.value, att2)))
                    {
                        return Lincompatible.invoke();
                    }
                    if ((att2 == null) && e2.value.type.value.checkAliasThisRec())
                    {
                        att2 = e2.value.type.value;
                    }
                    e2.value = resolveAliasThis(sc_ref.value, e2.value, false);
                    t2.value = e2.value.type.value;
                    t.value = t2.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                return Lincompatible.invoke();
            }
            else if (((e1.value.op.value & 0xFF) == 121) || ((e1.value.op.value & 0xFF) == 13) && (e1.value.implicitConvTo(t2.value) != 0))
            {
                return Lt2.invoke();
            }
            else if (((e2.value.op.value & 0xFF) == 121) || ((e2.value.op.value & 0xFF) == 13) && (e2.value.implicitConvTo(t1.value) != 0))
            {
                return Lt1.invoke();
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tsarray) && (e2.value.implicitConvTo(t1.value.nextOf().arrayOf()) != 0))
            {
            /*Lx1:*/
                t.value = t1.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tsarray) && (e1.value.implicitConvTo(t2.value.nextOf().arrayOf()) != 0))
            {
            /*Lx2:*/
                t.value = t2.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tvector) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tvector))
            {
                TypeVector tv1 = (TypeVector)t1.value;
                TypeVector tv2 = (TypeVector)t2.value;
                if (!tv1.basetype.value.equals(tv2.basetype.value))
                {
                    return Lincompatible.invoke();
                }
                /*goto LmodCompare*//*unrolled goto*/
            /*LmodCompare:*/
                if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                {
                    return Lincompatible.invoke();
                }
                byte mod = MODmerge(t1.value.mod.value, t2.value.mod.value);
                t1.value = t1.value.castMod(mod);
                t2.value = t2.value.castMod(mod);
                t.value = t1.value;
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tvector) && ((t2.value.ty.value & 0xFF) != ENUMTY.Tvector) && (e2.value.implicitConvTo(t1.value) != 0))
            {
                e2.value = e2.value.castTo(sc_ref.value, t1.value);
                t2.value = t1.value;
                t.value = t1.value;
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tvector) && ((t1.value.ty.value & 0xFF) != ENUMTY.Tvector) && (e1.value.implicitConvTo(t2.value) != 0))
            {
                e1.value = e1.value.castTo(sc_ref.value, t2.value);
                t1.value = t2.value;
                t.value = t1.value;
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (t1.value.isintegral() && t2.value.isintegral())
            {
                if (((t1.value.ty.value & 0xFF) != (t2.value.ty.value & 0xFF)))
                {
                    if (((t1.value.ty.value & 0xFF) == ENUMTY.Tvector) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tvector))
                    {
                        return Lincompatible.invoke();
                    }
                    e1.value = integralPromotions(e1.value, sc_ref.value);
                    e2.value = integralPromotions(e2.value, sc_ref.value);
                    t1.value = e1.value.type.value;
                    t2.value = e2.value.type.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                assert(((t1.value.ty.value & 0xFF) == (t2.value.ty.value & 0xFF)));
            /*LmodCompare:*/
                if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                {
                    return Lincompatible.invoke();
                }
                byte mod = MODmerge(t1.value.mod.value, t2.value.mod.value);
                t1.value = t1.value.castMod(mod);
                t2.value = t2.value.castMod(mod);
                t.value = t1.value;
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tnull) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tnull))
            {
                byte mod = MODmerge(t1.value.mod.value, t2.value.mod.value);
                t.value = t1.value.castMod(mod);
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
                return Lret.invoke();
            }
            else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tnull) && ((t1.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((t1.value.ty.value & 0xFF) == ENUMTY.Taarray) || ((t1.value.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                return Lt1.invoke();
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tnull) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((t2.value.ty.value & 0xFF) == ENUMTY.Taarray) || ((t2.value.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                return Lt2.invoke();
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tarray) && isBinArrayOp(op) && isArrayOpOperand(e1.value))
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
                else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tarray) && isArrayOpOperand(e2.value))
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
                    {
                        return Lincompatible.invoke();
                    }
                }
                else
                {
                    return Lincompatible.invoke();
                }
            }
            else if (((t2.value.ty.value & 0xFF) == ENUMTY.Tarray) && isBinArrayOp(op) && isArrayOpOperand(e2.value))
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
                {
                    return Lincompatible.invoke();
                }
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
        Function0<Expression> errorReturn = new Function0<Expression>(){
            public Expression invoke() {
                Ref<Expression> ex = ref(be.incompatibleTypes());
                if (((ex.value.op.value & 0xFF) == 127))
                {
                    return ex.value;
                }
                return new ErrorExp();
            }
        };
        Type t1 = be.e1.value.type.value.toBasetype();
        Type t2 = be.e2.value.type.value.toBasetype();
        if (((be.op.value & 0xFF) == 75) || ((be.op.value & 0xFF) == 74))
        {
            if (((t1.ty.value & 0xFF) == ENUMTY.Tstruct) && ((t2.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                return errorReturn.invoke();
            }
            else if (((t1.ty.value & 0xFF) == ENUMTY.Tclass) && ((t2.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                return errorReturn.invoke();
            }
            else if (((t1.ty.value & 0xFF) == ENUMTY.Taarray) && ((t2.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                return errorReturn.invoke();
            }
        }
        if (!typeMerge(sc, be.op.value, ptr(be.type), ptr(be.e1), ptr(be.e2)))
        {
            return errorReturn.invoke();
        }
        if (((be.e1.value.op.value & 0xFF) == 127))
        {
            return be.e1.value;
        }
        if (((be.e2.value.op.value & 0xFF) == 127))
        {
            return be.e2.value;
        }
        return null;
    }

    public static Expression integralPromotions(Expression e, Ptr<Scope> sc) {
        switch ((e.type.value.toBasetype().ty.value & 0xFF))
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
                e = e.castTo(sc, Type.tuns32.value);
                break;
            default:
            break;
        }
        return e;
    }

    public static Expression charPromotions(Expression e, Ptr<Scope> sc) {
        switch ((e.type.value.toBasetype().ty.value & 0xFF))
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
        if (global.params.fix16997)
        {
            ue.e1.value = integralPromotions(ue.e1.value, sc);
        }
        else
        {
            switch ((ue.e1.value.type.value.toBasetype().ty.value & 0xFF))
            {
                case 13:
                case 14:
                case 15:
                case 16:
                case 31:
                case 32:
                case 33:
                    ue.deprecation(new BytePtr("integral promotion not done for `%s`, use '-preview=intpromote' switch or `%scast(int)(%s)`"), ue.toChars(), Token.toChars(ue.op.value), ue.e1.value.toChars());
                    break;
                default:
                break;
            }
        }
    }

    public static boolean arrayTypeCompatible(Loc loc, Type t1, Type t2) {
        t1 = t1.toBasetype().merge2();
        t2 = t2.toBasetype().merge2();
        if (((t1.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tpointer))
        {
            if ((t1.nextOf().implicitConvTo(t2.nextOf()) < MATCH.constant) && (t2.nextOf().implicitConvTo(t1.nextOf()) < MATCH.constant) && ((t1.nextOf().ty.value & 0xFF) != ENUMTY.Tvoid) && ((t2.nextOf().ty.value & 0xFF) != ENUMTY.Tvoid))
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
        if (((t1.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t2.ty.value & 0xFF) == (t1.ty.value & 0xFF)))
        {
            if ((t1.nextOf().implicitConvTo(t2.nextOf()) >= MATCH.constant) || (t2.nextOf().implicitConvTo(t1.nextOf()) >= MATCH.constant))
            {
                return true;
            }
        }
        return false;
    }

    public static IntRange getIntRange(Expression e) {
        // skipping duplicate class IntRangeVisitor
        IntRangeVisitor v = new IntRangeVisitor();
        e.accept(v);
        return v.range.value;
    }

}
