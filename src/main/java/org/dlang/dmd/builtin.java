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
    public static void add_builtin(BytePtr mangle, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> fp) {
        builtins.insert(mangle, strlen(mangle), fp);
    }

    public static Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookup(BytePtr mangle) {
        {
            StringValue sv = builtins.lookup(mangle, strlen(mangle));
            if (sv != null)
                return toFunction3<Loc,FuncDeclaration,DArray<Expression>,Expression>((sv).ptrvalue);
        }
        return null;
    }

    public static Expression eval_unimp(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        return null;
    }

    public static Expression eval_sin(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.sin(arg0.toReal()), arg0.type);
    }

    public static Expression eval_cos(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.cos(arg0.toReal()), arg0.type);
    }

    public static Expression eval_tan(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.tan(arg0.toReal()), arg0.type);
    }

    public static Expression eval_sqrt(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.sqrt(arg0.toReal()), arg0.type);
    }

    public static Expression eval_fabs(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.fabs(arg0.toReal()), arg0.type);
    }

    public static Expression eval_ldexp(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 135);
        return new RealExp(loc, CTFloat.ldexp(arg0.toReal(), (int)arg1.toInteger()), arg0.type);
    }

    public static Expression eval_log(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.log(arg0.toReal()), arg0.type);
    }

    public static Expression eval_log2(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.log2(arg0.toReal()), arg0.type);
    }

    public static Expression eval_log10(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.log10(arg0.toReal()), arg0.type);
    }

    public static Expression eval_exp(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.exp(arg0.toReal()), arg0.type);
    }

    public static Expression eval_expm1(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.expm1(arg0.toReal()), arg0.type);
    }

    public static Expression eval_exp2(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.exp2(arg0.toReal()), arg0.type);
    }

    public static Expression eval_round(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.round(arg0.toReal()), arg0.type);
    }

    public static Expression eval_floor(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.floor(arg0.toReal()), arg0.type);
    }

    public static Expression eval_ceil(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.ceil(arg0.toReal()), arg0.type);
    }

    public static Expression eval_trunc(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.trunc(arg0.toReal()), arg0.type);
    }

    public static Expression eval_copysign(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.copysign(arg0.toReal(), arg1.toReal()), arg0.type);
    }

    public static Expression eval_pow(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.pow(arg0.toReal(), arg1.toReal()), arg0.type);
    }

    public static Expression eval_fmin(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.fmin(arg0.toReal(), arg1.toReal()), arg0.type);
    }

    public static Expression eval_fmax(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.fmax(arg0.toReal(), arg1.toReal()), arg0.type);
    }

    public static Expression eval_fma(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 140);
        Expression arg2 = (arguments).get(2);
        assert((arg2.op & 0xFF) == 140);
        return new RealExp(loc, CTFloat.fma(arg0.toReal(), arg1.toReal(), arg2.toReal()), arg0.type);
    }

    public static Expression eval_isnan(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new IntegerExp(loc, (CTFloat.isNaN(arg0.toReal()) ? 1 : 0), Type.tbool);
    }

    public static Expression eval_isinfinity(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        return new IntegerExp(loc, (CTFloat.isInfinity(arg0.toReal()) ? 1 : 0), Type.tbool);
    }

    public static Expression eval_isfinite(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        boolean value = (!(CTFloat.isNaN(arg0.toReal())) && !(CTFloat.isInfinity(arg0.toReal())));
        return new IntegerExp(loc, (value ? 1 : 0), Type.tbool);
    }

    public static Expression eval_bsf(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 135);
        long n = arg0.toInteger();
        if (n == 0L)
            error(loc, new BytePtr("`bsf(0)` is undefined"));
        return new IntegerExp(loc, (long)bsf(n), Type.tint32);
    }

    public static Expression eval_bsr(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 135);
        long n = arg0.toInteger();
        if (n == 0L)
            error(loc, new BytePtr("`bsr(0)` is undefined"));
        return new IntegerExp(loc, (long)bsr(n), Type.tint32);
    }

    public static Expression eval_bswap(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 135);
        long n = arg0.toInteger();
        byte ty = arg0.type.toBasetype().ty;
        if (((ty & 0xFF) == ENUMTY.Tint64 || (ty & 0xFF) == ENUMTY.Tuns64))
            return new IntegerExp(loc, bswap(n), arg0.type);
        else
            return new IntegerExp(loc, (long)bswap((int)n), arg0.type);
    }

    public static Expression eval_popcnt(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 135);
        long n = arg0.toInteger();
        return new IntegerExp(loc, (long)popcnt(n), Type.tint32);
    }

    public static Expression eval_yl2x(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 140);
        Ref<Double> x = ref(arg0.toReal());
        Ref<Double> y = ref(arg1.toReal());
        Ref<Double> result = ref(CTFloat.zero);
        CTFloat.yl2x(ptr(x), ptr(y), ptr(result));
        return new RealExp(loc, result.value, arg0.type);
    }

    public static Expression eval_yl2xp1(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        Expression arg0 = (arguments).get(0);
        assert((arg0.op & 0xFF) == 140);
        Expression arg1 = (arguments).get(1);
        assert((arg1.op & 0xFF) == 140);
        Ref<Double> x = ref(arg0.toReal());
        Ref<Double> y = ref(arg1.toReal());
        Ref<Double> result = ref(CTFloat.zero);
        CTFloat.yl2xp1(ptr(x), ptr(y), ptr(result));
        return new RealExp(loc, result.value, arg0.type);
    }

    public static void builtin_init() {
        builtins._init(65);
        add_builtin(new BytePtr("_D4core4math3sinFNaNbNiNfeZe"), eval_sin);
        add_builtin(new BytePtr("_D4core4math3cosFNaNbNiNfeZe"), eval_cos);
        add_builtin(new BytePtr("_D4core4math3tanFNaNbNiNfeZe"), eval_tan);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNfeZe"), eval_sqrt);
        add_builtin(new BytePtr("_D4core4math4fabsFNaNbNiNfeZe"), eval_fabs);
        add_builtin(new BytePtr("_D4core4math5expm1FNaNbNiNfeZe"), eval_unimp);
        add_builtin(new BytePtr("_D4core4math4exp2FNaNbNiNfeZe"), eval_unimp);
        add_builtin(new BytePtr("_D4core4math3sinFNaNbNiNeeZe"), eval_sin);
        add_builtin(new BytePtr("_D4core4math3cosFNaNbNiNeeZe"), eval_cos);
        add_builtin(new BytePtr("_D4core4math3tanFNaNbNiNeeZe"), eval_tan);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNeeZe"), eval_sqrt);
        add_builtin(new BytePtr("_D4core4math4fabsFNaNbNiNeeZe"), eval_fabs);
        add_builtin(new BytePtr("_D4core4math5expm1FNaNbNiNeeZe"), eval_unimp);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNfdZd"), eval_sqrt);
        add_builtin(new BytePtr("_D4core4math4sqrtFNaNbNiNffZf"), eval_sqrt);
        add_builtin(new BytePtr("_D4core4math5atan2FNaNbNiNfeeZe"), eval_unimp);
        if (true)
        {
            add_builtin(new BytePtr("_D4core4math4yl2xFNaNbNiNfeeZe"), eval_yl2x);
        }
        else
        {
            add_builtin(new BytePtr("_D4core4math4yl2xFNaNbNiNfeeZe"), eval_unimp);
        }
        if (true)
        {
            add_builtin(new BytePtr("_D4core4math6yl2xp1FNaNbNiNfeeZe"), eval_yl2xp1);
        }
        else
        {
            add_builtin(new BytePtr("_D4core4math6yl2xp1FNaNbNiNfeeZe"), eval_unimp);
        }
        add_builtin(new BytePtr("_D4core4math6rndtolFNaNbNiNfeZl"), eval_unimp);
        add_builtin(new BytePtr("_D3std4math3tanFNaNbNiNfeZe"), eval_tan);
        add_builtin(new BytePtr("_D3std4math5expm1FNaNbNiNfeZe"), eval_unimp);
        add_builtin(new BytePtr("_D3std4math3tanFNaNbNiNeeZe"), eval_tan);
        add_builtin(new BytePtr("_D3std4math3expFNaNbNiNeeZe"), eval_exp);
        add_builtin(new BytePtr("_D3std4math5expm1FNaNbNiNeeZe"), eval_expm1);
        add_builtin(new BytePtr("_D3std4math4exp2FNaNbNiNeeZe"), eval_exp2);
        add_builtin(new BytePtr("_D3std4math5atan2FNaNbNiNfeeZe"), eval_unimp);
        add_builtin(new BytePtr("_D4core4math5ldexpFNaNbNiNfeiZe"), eval_ldexp);
        add_builtin(new BytePtr("_D3std4math3logFNaNbNiNfeZe"), eval_log);
        add_builtin(new BytePtr("_D3std4math4log2FNaNbNiNfeZe"), eval_log2);
        add_builtin(new BytePtr("_D3std4math5log10FNaNbNiNfeZe"), eval_log10);
        add_builtin(new BytePtr("_D3std4math5roundFNbNiNeeZe"), eval_round);
        add_builtin(new BytePtr("_D3std4math5roundFNaNbNiNeeZe"), eval_round);
        add_builtin(new BytePtr("_D3std4math5floorFNaNbNiNefZf"), eval_floor);
        add_builtin(new BytePtr("_D3std4math5floorFNaNbNiNedZd"), eval_floor);
        add_builtin(new BytePtr("_D3std4math5floorFNaNbNiNeeZe"), eval_floor);
        add_builtin(new BytePtr("_D3std4math4ceilFNaNbNiNefZf"), eval_ceil);
        add_builtin(new BytePtr("_D3std4math4ceilFNaNbNiNedZd"), eval_ceil);
        add_builtin(new BytePtr("_D3std4math4ceilFNaNbNiNeeZe"), eval_ceil);
        add_builtin(new BytePtr("_D3std4math5truncFNaNbNiNeeZe"), eval_trunc);
        add_builtin(new BytePtr("_D3std4math4fminFNaNbNiNfeeZe"), eval_fmin);
        add_builtin(new BytePtr("_D3std4math4fmaxFNaNbNiNfeeZe"), eval_fmax);
        add_builtin(new BytePtr("_D3std4math__T8copysignTfTfZQoFNaNbNiNeffZf"), eval_copysign);
        add_builtin(new BytePtr("_D3std4math__T8copysignTdTdZQoFNaNbNiNeddZd"), eval_copysign);
        add_builtin(new BytePtr("_D3std4math__T8copysignTeTeZQoFNaNbNiNeeeZe"), eval_copysign);
        add_builtin(new BytePtr("_D3std4math__T3powTfTfZQjFNaNbNiNeffZf"), eval_pow);
        add_builtin(new BytePtr("_D3std4math__T3powTdTdZQjFNaNbNiNeddZd"), eval_pow);
        add_builtin(new BytePtr("_D3std4math__T3powTeTeZQjFNaNbNiNeeeZe"), eval_pow);
        add_builtin(new BytePtr("_D3std4math3fmaFNaNbNiNfeeeZe"), eval_fma);
        add_builtin(new BytePtr("_D3std4math__T5isNaNTeZQjFNaNbNiNeeZb"), eval_isnan);
        add_builtin(new BytePtr("_D3std4math__T5isNaNTdZQjFNaNbNiNedZb"), eval_isnan);
        add_builtin(new BytePtr("_D3std4math__T5isNaNTfZQjFNaNbNiNefZb"), eval_isnan);
        add_builtin(new BytePtr("_D3std4math__T10isInfinityTeZQpFNaNbNiNeeZb"), eval_isinfinity);
        add_builtin(new BytePtr("_D3std4math__T10isInfinityTdZQpFNaNbNiNedZb"), eval_isinfinity);
        add_builtin(new BytePtr("_D3std4math__T10isInfinityTfZQpFNaNbNiNefZb"), eval_isinfinity);
        add_builtin(new BytePtr("_D3std4math__T8isFiniteTeZQmFNaNbNiNeeZb"), eval_isfinite);
        add_builtin(new BytePtr("_D3std4math__T8isFiniteTdZQmFNaNbNiNedZb"), eval_isfinite);
        add_builtin(new BytePtr("_D3std4math__T8isFiniteTfZQmFNaNbNiNefZb"), eval_isfinite);
        add_builtin(new BytePtr("_D4core5bitop3bsfFNaNbNiNfkZi"), eval_bsf);
        add_builtin(new BytePtr("_D4core5bitop3bsrFNaNbNiNfkZi"), eval_bsr);
        add_builtin(new BytePtr("_D4core5bitop3bsfFNaNbNiNfmZi"), eval_bsf);
        add_builtin(new BytePtr("_D4core5bitop3bsrFNaNbNiNfmZi"), eval_bsr);
        add_builtin(new BytePtr("_D4core5bitop5bswapFNaNbNiNfkZk"), eval_bswap);
        add_builtin(new BytePtr("_D4core5bitop7_popcntFNaNbNiNfkZi"), eval_popcnt);
        add_builtin(new BytePtr("_D4core5bitop7_popcntFNaNbNiNftZt"), eval_popcnt);
        if (global.params.is64bit)
            add_builtin(new BytePtr("_D4core5bitop7_popcntFNaNbNiNfmZi"), eval_popcnt);
    }

    public static void builtinDeinitialize() {
        builtins.opAssign(new StringTable(null, 0, null, 0, 0, 0, 0));
    }

    public static int isBuiltin(FuncDeclaration fd) {
        if (fd.builtin == BUILTIN.unknown)
        {
            Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> fp = pcopy(builtin_lookup(mangleExact(fd)));
            fd.builtin = fp != null ? BUILTIN.yes : BUILTIN.no;
        }
        return fd.builtin;
    }

    public static Expression eval_builtin(Loc loc, FuncDeclaration fd, DArray<Expression> arguments) {
        if (fd.builtin == BUILTIN.yes)
        {
            Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> fp = pcopy(builtin_lookup(mangleExact(fd)));
            assert(fp != null);
            return (fp).invoke(loc, fd, arguments);
        }
        return null;
    }

}
