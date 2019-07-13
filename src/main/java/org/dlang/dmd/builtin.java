package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;

public class builtin {

    static StringTable builtins = new StringTable();
    public static void add_builtin(BytePtr mangle, Function3<Loc,FuncDeclaration,Ptr<DArray<Expression>>,Expression> fp) {
        builtins.insert(mangle, strlen(mangle), fp);
    }

    public static Function3<Loc,FuncDeclaration,Ptr<DArray<Expression>>,Expression> builtin_lookup(BytePtr mangle) {
        {
            Ptr<StringValue> sv = builtins.lookup(mangle, strlen(mangle));
            if ((sv) != null)
                return ((Function3<Loc,FuncDeclaration,Ptr<DArray<Expression>>,Expression>)(sv.get()).ptrvalue);
        }
        return null;
    }

    public static Expression eval_unimp(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        return null;
    }

    public static Expression eval_sin(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.sin(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_cos(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.cos(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_tan(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.tan(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_sqrt(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.sqrt(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_fabs(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.fabs(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_ldexp(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 135));
        return new RealExp(loc, CTFloat.ldexp(arg0.toReal(), (int)arg1.toInteger()), arg0.type.value);
    }

    public static Expression eval_log(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.log(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_log2(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.log2(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_log10(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.log10(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_exp(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.exp(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_expm1(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.expm1(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_exp2(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.exp2(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_round(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.round(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_floor(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.floor(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_ceil(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.ceil(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_trunc(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.trunc(arg0.toReal()), arg0.type.value);
    }

    public static Expression eval_copysign(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.copysign(arg0.toReal(), arg1.toReal()), arg0.type.value);
    }

    public static Expression eval_pow(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.pow(arg0.toReal(), arg1.toReal()), arg0.type.value);
    }

    public static Expression eval_fmin(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.fmin(arg0.toReal(), arg1.toReal()), arg0.type.value);
    }

    public static Expression eval_fmax(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.fmax(arg0.toReal(), arg1.toReal()), arg0.type.value);
    }

    public static Expression eval_fma(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 140));
        Expression arg2 = (arguments.get()).get(2);
        assert(((arg2.op.value & 0xFF) == 140));
        return new RealExp(loc, CTFloat.fma(arg0.toReal(), arg1.toReal(), arg2.toReal()), arg0.type.value);
    }

    public static Expression eval_isnan(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new IntegerExp(loc, (CTFloat.isNaN(arg0.toReal()) ? 1 : 0), Type.tbool.value);
    }

    public static Expression eval_isinfinity(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        return new IntegerExp(loc, (CTFloat.isInfinity(arg0.toReal()) ? 1 : 0), Type.tbool.value);
    }

    public static Expression eval_isfinite(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        boolean value = !CTFloat.isNaN(arg0.toReal()) && !CTFloat.isInfinity(arg0.toReal());
        return new IntegerExp(loc, (value ? 1 : 0), Type.tbool.value);
    }

    public static Expression eval_bsf(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 135));
        long n = arg0.toInteger();
        if ((n == 0L))
            error(loc, new BytePtr("`bsf(0)` is undefined"));
        return new IntegerExp(loc, (long)bsf(n), Type.tint32.value);
    }

    public static Expression eval_bsr(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 135));
        long n = arg0.toInteger();
        if ((n == 0L))
            error(loc, new BytePtr("`bsr(0)` is undefined"));
        return new IntegerExp(loc, (long)bsr(n), Type.tint32.value);
    }

    public static Expression eval_bswap(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 135));
        long n = arg0.toInteger();
        byte ty = arg0.type.value.toBasetype().ty.value;
        if (((ty & 0xFF) == ENUMTY.Tint64) || ((ty & 0xFF) == ENUMTY.Tuns64))
            return new IntegerExp(loc, bswap(n), arg0.type.value);
        else
            return new IntegerExp(loc, (long)bswap((int)n), arg0.type.value);
    }

    public static Expression eval_popcnt(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 135));
        long n = arg0.toInteger();
        return new IntegerExp(loc, (long)popcnt(n), Type.tint32.value);
    }

    public static Expression eval_yl2x(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 140));
        double x = arg0.toReal();
        double y = arg1.toReal();
        Ref<Double> result = ref(CTFloat.zero.value);
        CTFloat.yl2x(ptr(x), ptr(y), ptr(result));
        return new RealExp(loc, result.value, arg0.type.value);
    }

    public static Expression eval_yl2xp1(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        Expression arg0 = (arguments.get()).get(0);
        assert(((arg0.op.value & 0xFF) == 140));
        Expression arg1 = (arguments.get()).get(1);
        assert(((arg1.op.value & 0xFF) == 140));
        double x = arg0.toReal();
        double y = arg1.toReal();
        Ref<Double> result = ref(CTFloat.zero.value);
        CTFloat.yl2xp1(ptr(x), ptr(y), ptr(result));
        return new RealExp(loc, result.value, arg0.type.value);
    }

    public static void builtin_init() {
        builtins._init(65);
        add_builtin(new BytePtr("_D4core4math3sinFNaNbNiNfeZe"), builtin::eval_sin);
        add_builtin(new BytePtr("_D4core4math3cosFNaNbNiNfeZe"), builtin::eval_cos);
        add_builtin(new BytePtr("_D4core4math3tanFNaNbNiNfeZe"), builtin::eval_tan);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNfeZe"), builtin::eval_sqrt);
        add_builtin(new BytePtr("_D4core4math4fabsFNaNbNiNfeZe"), builtin::eval_fabs);
        add_builtin(new BytePtr("_D4core4math5expm1FNaNbNiNfeZe"), builtin::eval_unimp);
        add_builtin(new BytePtr("_D4core4math4exp2FNaNbNiNfeZe"), builtin::eval_unimp);
        add_builtin(new BytePtr("_D4core4math3sinFNaNbNiNeeZe"), builtin::eval_sin);
        add_builtin(new BytePtr("_D4core4math3cosFNaNbNiNeeZe"), builtin::eval_cos);
        add_builtin(new BytePtr("_D4core4math3tanFNaNbNiNeeZe"), builtin::eval_tan);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNeeZe"), builtin::eval_sqrt);
        add_builtin(new BytePtr("_D4core4math4fabsFNaNbNiNeeZe"), builtin::eval_fabs);
        add_builtin(new BytePtr("_D4core4math5expm1FNaNbNiNeeZe"), builtin::eval_unimp);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNfdZd"), builtin::eval_sqrt);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNffZf"), builtin::eval_sqrt);
        add_builtin(new BytePtr("_D4core4math5atan2FNaNbNiNfeeZe"), builtin::eval_unimp);
        if (true)
        {
            add_builtin(new BytePtr("_D4core4math4yl2xFNaNbNiNfeeZe"), builtin::eval_yl2x);
        }
        else
        {
            add_builtin(new BytePtr("_D4core4math4yl2xFNaNbNiNfeeZe"), builtin::eval_unimp);
        }
        if (true)
        {
            add_builtin(new BytePtr("_D4core4math6yl2xp1FNaNbNiNfeeZe"), builtin::eval_yl2xp1);
        }
        else
        {
            add_builtin(new BytePtr("_D4core4math6yl2xp1FNaNbNiNfeeZe"), builtin::eval_unimp);
        }
        add_builtin(new BytePtr("_D4core4math6rndtolFNaNbNiNfeZl"), builtin::eval_unimp);
        add_builtin(new BytePtr("_D3std4math3tanFNaNbNiNfeZe"), builtin::eval_tan);
        add_builtin(new BytePtr("_D3std4math5expm1FNaNbNiNfeZe"), builtin::eval_unimp);
        add_builtin(new BytePtr("_D3std4math3tanFNaNbNiNeeZe"), builtin::eval_tan);
        add_builtin(new BytePtr("_D3std4math3expFNaNbNiNeeZe"), builtin::eval_exp);
        add_builtin(new BytePtr("_D3std4math5expm1FNaNbNiNeeZe"), builtin::eval_expm1);
        add_builtin(new BytePtr("_D3std4math4exp2FNaNbNiNeeZe"), builtin::eval_exp2);
        add_builtin(new BytePtr("_D3std4math5atan2FNaNbNiNfeeZe"), builtin::eval_unimp);
        add_builtin(new BytePtr("_D4core4math5ldexpFNaNbNiNfeiZe"), builtin::eval_ldexp);
        add_builtin(new BytePtr("_D3std4math3logFNaNbNiNfeZe"), builtin::eval_log);
        add_builtin(new BytePtr("_D3std4math4log2FNaNbNiNfeZe"), builtin::eval_log2);
        add_builtin(new BytePtr("_D3std4math5log10FNaNbNiNfeZe"), builtin::eval_log10);
        add_builtin(new BytePtr("_D3std4math5roundFNbNiNeeZe"), builtin::eval_round);
        add_builtin(new BytePtr("_D3std4math5roundFNaNbNiNeeZe"), builtin::eval_round);
        add_builtin(new BytePtr("_D3std4math5floorFNaNbNiNefZf"), builtin::eval_floor);
        add_builtin(new BytePtr("_D3std4math5floorFNaNbNiNedZd"), builtin::eval_floor);
        add_builtin(new BytePtr("_D3std4math5floorFNaNbNiNeeZe"), builtin::eval_floor);
        add_builtin(new BytePtr("_D3std4math4ceilFNaNbNiNefZf"), builtin::eval_ceil);
        add_builtin(new BytePtr("_D3std4math4ceilFNaNbNiNedZd"), builtin::eval_ceil);
        add_builtin(new BytePtr("_D3std4math4ceilFNaNbNiNeeZe"), builtin::eval_ceil);
        add_builtin(new BytePtr("_D3std4math5truncFNaNbNiNeeZe"), builtin::eval_trunc);
        add_builtin(new BytePtr("_D3std4math4fminFNaNbNiNfeeZe"), builtin::eval_fmin);
        add_builtin(new BytePtr("_D3std4math4fmaxFNaNbNiNfeeZe"), builtin::eval_fmax);
        add_builtin(new BytePtr("_D3std4math__T8copysignTfTfZQoFNaNbNiNeffZf"), builtin::eval_copysign);
        add_builtin(new BytePtr("_D3std4math__T8copysignTdTdZQoFNaNbNiNeddZd"), builtin::eval_copysign);
        add_builtin(new BytePtr("_D3std4math__T8copysignTeTeZQoFNaNbNiNeeeZe"), builtin::eval_copysign);
        add_builtin(new BytePtr("_D3std4math__T3powTfTfZQjFNaNbNiNeffZf"), builtin::eval_pow);
        add_builtin(new BytePtr("_D3std4math__T3powTdTdZQjFNaNbNiNeddZd"), builtin::eval_pow);
        add_builtin(new BytePtr("_D3std4math__T3powTeTeZQjFNaNbNiNeeeZe"), builtin::eval_pow);
        add_builtin(new BytePtr("_D3std4math3fmaFNaNbNiNfeeeZe"), builtin::eval_fma);
        add_builtin(new BytePtr("_D3std4math__T5isNaNTeZQjFNaNbNiNeeZb"), builtin::eval_isnan);
        add_builtin(new BytePtr("_D3std4math__T5isNaNTdZQjFNaNbNiNedZb"), builtin::eval_isnan);
        add_builtin(new BytePtr("_D3std4math__T5isNaNTfZQjFNaNbNiNefZb"), builtin::eval_isnan);
        add_builtin(new BytePtr("_D3std4math__T10isInfinityTeZQpFNaNbNiNeeZb"), builtin::eval_isinfinity);
        add_builtin(new BytePtr("_D3std4math__T10isInfinityTdZQpFNaNbNiNedZb"), builtin::eval_isinfinity);
        add_builtin(new BytePtr("_D3std4math__T10isInfinityTfZQpFNaNbNiNefZb"), builtin::eval_isinfinity);
        add_builtin(new BytePtr("_D3std4math__T8isFiniteTeZQmFNaNbNiNeeZb"), builtin::eval_isfinite);
        add_builtin(new BytePtr("_D3std4math__T8isFiniteTdZQmFNaNbNiNedZb"), builtin::eval_isfinite);
        add_builtin(new BytePtr("_D3std4math__T8isFiniteTfZQmFNaNbNiNefZb"), builtin::eval_isfinite);
        add_builtin(new BytePtr("_D4core5bitop3bsfFNaNbNiNfkZi"), builtin::eval_bsf);
        add_builtin(new BytePtr("_D4core5bitop3bsrFNaNbNiNfkZi"), builtin::eval_bsr);
        add_builtin(new BytePtr("_D4core5bitop3bsfFNaNbNiNfmZi"), builtin::eval_bsf);
        add_builtin(new BytePtr("_D4core5bitop3bsrFNaNbNiNfmZi"), builtin::eval_bsr);
        add_builtin(new BytePtr("_D4core5bitop5bswapFNaNbNiNfkZk"), builtin::eval_bswap);
        add_builtin(new BytePtr("_D4core5bitop7_popcntFNaNbNiNfkZi"), builtin::eval_popcnt);
        add_builtin(new BytePtr("_D4core5bitop7_popcntFNaNbNiNftZt"), builtin::eval_popcnt);
        if (global.params.is64bit)
            add_builtin(new BytePtr("_D4core5bitop7_popcntFNaNbNiNfmZi"), builtin::eval_popcnt);
    }

    public static void builtinDeinitialize() {
        builtins.opAssign(new StringTable(null, 0, null, 0, 0, 0, 0));
    }

    public static int isBuiltin(FuncDeclaration fd) {
        if ((fd.builtin == BUILTIN.unknown))
        {
            Function3<Loc,FuncDeclaration,Ptr<DArray<Expression>>,Expression> fp = pcopy(builtin_lookup(mangleExact(fd)));
            fd.builtin = fp != null ? BUILTIN.yes : BUILTIN.no;
        }
        return fd.builtin;
    }

    public static Expression eval_builtin(Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments) {
        if ((fd.builtin == BUILTIN.yes))
        {
            Function3<Loc,FuncDeclaration,Ptr<DArray<Expression>>,Expression> fp = pcopy(builtin_lookup(mangleExact(fd)));
            assert(fp != null);
            return (fp).invoke(loc, fd, arguments);
        }
        return null;
    }

}
