package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.complex.*;
import static org.dlang.dmd.ctfeexpr.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;

public class constfold {

    static boolean LOG = false;
    public static Expression expType(Type type, Expression e) {
        if ((!pequals(type, e.type.value)))
        {
            e = e.copy();
            e.type.value = type;
        }
        return e;
    }

    public static int isConst(Expression e) {
        switch ((e.op.value & 0xFF))
        {
            case 135:
            case 140:
            case 147:
                return 1;
            case 13:
                return 0;
            case 25:
                return 2;
            default:
            return 0;
        }
        throw new AssertionError("Unreachable code!");
    }

    public static void cantExp(UnionExp ue) {
        Ref<UnionExp> ue_ref = ref(ue);
        ue_ref.value = new UnionExp().copy();
        (ptr(ue_ref)) = new UnionExp(new CTFEExp(TOK.cantExpression));
    }

    public static UnionExp Neg(Type type, Expression e1) {
        Ref<UnionExp> ue = ref(null);
        Loc loc = e1.loc.value.copy();
        if (e1.type.value.isreal())
        {
            ptr(ue) = new UnionExp(new RealExp(loc, -e1.toReal(), type));
        }
        else if (e1.type.value.isimaginary())
        {
            ptr(ue) = new UnionExp(new RealExp(loc, -e1.toImaginary(), type));
        }
        else if (e1.type.value.iscomplex())
        {
            ptr(ue) = new UnionExp(new ComplexExp(loc, e1.toComplex().opNeg(), type));
        }
        else
        {
            ptr(ue) = new UnionExp(new IntegerExp(loc, -e1.toInteger(), type));
        }
        return ue.value;
    }

    public static UnionExp Com(Type type, Expression e1) {
        Ref<UnionExp> ue = ref(null);
        Loc loc = e1.loc.value.copy();
        ptr(ue) = new UnionExp(new IntegerExp(loc, ~e1.toInteger(), type));
        return ue.value;
    }

    public static UnionExp Not(Type type, Expression e1) {
        Ref<UnionExp> ue = ref(null);
        Loc loc = e1.loc.value.copy();
        ptr(ue) = new UnionExp(new IntegerExp(loc, e1.isBool(false) ? 1 : 0, type));
        return ue.value;
    }

    public static UnionExp Bool(Type type, Expression e1) {
        Ref<UnionExp> ue = ref(null);
        Loc loc = e1.loc.value.copy();
        ptr(ue) = new UnionExp(new IntegerExp(loc, e1.isBool(true) ? 1 : 0, type));
        return ue.value;
    }

    public static UnionExp Add(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        if (type.isreal())
        {
            ptr(ue) = new UnionExp(new RealExp(loc, e1.toReal() + e2.toReal(), type));
        }
        else if (type.isimaginary())
        {
            ptr(ue) = new UnionExp(new RealExp(loc, e1.toImaginary() + e2.toImaginary(), type));
        }
        else if (type.iscomplex())
        {
            complex_t c1 = c1 = new complex_t(CTFloat.zero.value);
            double r1 = CTFloat.zero.value;
            double i1 = CTFloat.zero.value;
            complex_t c2 = c2 = new complex_t(CTFloat.zero.value);
            double r2 = CTFloat.zero.value;
            double i2 = CTFloat.zero.value;
            complex_t v = v = new complex_t(CTFloat.zero.value);
            int x = 0;
            if (e1.type.value.isreal())
            {
                r1 = e1.toReal();
                x = 0;
            }
            else if (e1.type.value.isimaginary())
            {
                i1 = e1.toImaginary();
                x = 3;
            }
            else
            {
                c1 = e1.toComplex().copy();
                x = 6;
            }
            if (e2.type.value.isreal())
            {
                r2 = e2.toReal();
            }
            else if (e2.type.value.isimaginary())
            {
                i2 = e2.toImaginary();
                x += 1;
            }
            else
            {
                c2 = e2.toComplex().copy();
                x += 2;
            }
            switch (x)
            {
                case 0:
                    v = new complex_t(r1 + r2).copy();
                    break;
                case 1:
                    v = new complex_t(r1, i2).copy();
                    break;
                case 2:
                    v = new complex_t(r1 + creall(c2), cimagl(c2)).copy();
                    break;
                case 3:
                    v = new complex_t(r2, i1).copy();
                    break;
                case 4:
                    v = new complex_t(CTFloat.zero.value, i1 + i2).copy();
                    break;
                case 5:
                    v = new complex_t(creall(c2), i1 + cimagl(c2)).copy();
                    break;
                case 6:
                    v = new complex_t(creall(c1) + r2, cimagl(c2)).copy();
                    break;
                case 7:
                    v = new complex_t(creall(c1), cimagl(c1) + i2).copy();
                    break;
                case 8:
                    v = c1.opAdd(c2).copy();
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            ptr(ue) = new UnionExp(new ComplexExp(loc, v, type));
        }
        else if (((e1.op.value & 0xFF) == 25))
        {
            SymOffExp soe = (SymOffExp)e1;
            ptr(ue) = new UnionExp(new SymOffExp(loc, soe.var.value, soe.offset.value + e2.toInteger()));
            ue.value.exp().type.value = type;
        }
        else if (((e2.op.value & 0xFF) == 25))
        {
            SymOffExp soe = (SymOffExp)e2;
            ptr(ue) = new UnionExp(new SymOffExp(loc, soe.var.value, soe.offset.value + e1.toInteger()));
            ue.value.exp().type.value = type;
        }
        else
        {
            ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() + e2.toInteger(), type));
        }
        return ue.value;
    }

    public static UnionExp Min(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        if (type.isreal())
        {
            ptr(ue) = new UnionExp(new RealExp(loc, e1.toReal() - e2.toReal(), type));
        }
        else if (type.isimaginary())
        {
            ptr(ue) = new UnionExp(new RealExp(loc, e1.toImaginary() - e2.toImaginary(), type));
        }
        else if (type.iscomplex())
        {
            complex_t c1 = c1 = new complex_t(CTFloat.zero.value);
            double r1 = CTFloat.zero.value;
            double i1 = CTFloat.zero.value;
            complex_t c2 = c2 = new complex_t(CTFloat.zero.value);
            double r2 = CTFloat.zero.value;
            double i2 = CTFloat.zero.value;
            complex_t v = v = new complex_t(CTFloat.zero.value);
            int x = 0;
            if (e1.type.value.isreal())
            {
                r1 = e1.toReal();
                x = 0;
            }
            else if (e1.type.value.isimaginary())
            {
                i1 = e1.toImaginary();
                x = 3;
            }
            else
            {
                c1 = e1.toComplex().copy();
                x = 6;
            }
            if (e2.type.value.isreal())
            {
                r2 = e2.toReal();
            }
            else if (e2.type.value.isimaginary())
            {
                i2 = e2.toImaginary();
                x += 1;
            }
            else
            {
                c2 = e2.toComplex().copy();
                x += 2;
            }
            switch (x)
            {
                case 0:
                    v = new complex_t(r1 - r2).copy();
                    break;
                case 1:
                    v = new complex_t(r1, -i2).copy();
                    break;
                case 2:
                    v = new complex_t(r1 - creall(c2), -cimagl(c2)).copy();
                    break;
                case 3:
                    v = new complex_t(-r2, i1).copy();
                    break;
                case 4:
                    v = new complex_t(CTFloat.zero.value, i1 - i2).copy();
                    break;
                case 5:
                    v = new complex_t(-creall(c2), i1 - cimagl(c2)).copy();
                    break;
                case 6:
                    v = new complex_t(creall(c1) - r2, cimagl(c1)).copy();
                    break;
                case 7:
                    v = new complex_t(creall(c1), cimagl(c1) - i2).copy();
                    break;
                case 8:
                    v = c1.opSub(c2).copy();
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            ptr(ue) = new UnionExp(new ComplexExp(loc, v, type));
        }
        else if (((e1.op.value & 0xFF) == 25))
        {
            SymOffExp soe = (SymOffExp)e1;
            ptr(ue) = new UnionExp(new SymOffExp(loc, soe.var.value, soe.offset.value - e2.toInteger()));
            ue.value.exp().type.value = type;
        }
        else
        {
            ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() - e2.toInteger(), type));
        }
        return ue.value;
    }

    public static UnionExp Mul(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        if (type.isfloating())
        {
            complex_t c = c = new complex_t(CTFloat.zero.value);
            double r = CTFloat.zero.value;
            if (e1.type.value.isreal())
            {
                r = e1.toReal();
                c = e2.toComplex().copy();
                c = new complex_t(r * creall(c), r * cimagl(c)).copy();
            }
            else if (e1.type.value.isimaginary())
            {
                r = e1.toImaginary();
                c = e2.toComplex().copy();
                c = new complex_t(-r * cimagl(c), r * creall(c)).copy();
            }
            else if (e2.type.value.isreal())
            {
                r = e2.toReal();
                c = e1.toComplex().copy();
                c = new complex_t(r * creall(c), r * cimagl(c)).copy();
            }
            else if (e2.type.value.isimaginary())
            {
                r = e2.toImaginary();
                c = e1.toComplex().copy();
                c = new complex_t(-r * cimagl(c), r * creall(c)).copy();
            }
            else
            {
                c = e1.toComplex().opMul(e2.toComplex()).copy();
            }
            if (type.isreal())
            {
                ptr(ue) = new UnionExp(new RealExp(loc, creall(c), type));
            }
            else if (type.isimaginary())
            {
                ptr(ue) = new UnionExp(new RealExp(loc, cimagl(c), type));
            }
            else if (type.iscomplex())
            {
                ptr(ue) = new UnionExp(new ComplexExp(loc, c, type));
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
        }
        else
        {
            ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() * e2.toInteger(), type));
        }
        return ue.value;
    }

    public static UnionExp Div(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        if (type.isfloating())
        {
            complex_t c = c = new complex_t(CTFloat.zero.value);
            if (e2.type.value.isreal())
            {
                if (e1.type.value.isreal())
                {
                    ptr(ue) = new UnionExp(new RealExp(loc, e1.toReal() / e2.toReal(), type));
                    return ue.value;
                }
                double r = e2.toReal();
                c = e1.toComplex().copy();
                c = new complex_t(creall(c) / r, cimagl(c) / r).copy();
            }
            else if (e2.type.value.isimaginary())
            {
                double r = e2.toImaginary();
                c = e1.toComplex().copy();
                c = new complex_t(cimagl(c) / r, -creall(c) / r).copy();
            }
            else
            {
                c = e1.toComplex().opDiv(e2.toComplex()).copy();
            }
            if (type.isreal())
            {
                ptr(ue) = new UnionExp(new RealExp(loc, creall(c), type));
            }
            else if (type.isimaginary())
            {
                ptr(ue) = new UnionExp(new RealExp(loc, cimagl(c), type));
            }
            else if (type.iscomplex())
            {
                ptr(ue) = new UnionExp(new ComplexExp(loc, c, type));
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
        }
        else
        {
            long n1 = 0L;
            long n2 = 0L;
            long n = 0L;
            n1 = (long)e1.toInteger();
            n2 = (long)e2.toInteger();
            if ((n2 == 0L))
            {
                e2.error(new BytePtr("divide by 0"));
                ptr(ue) = new UnionExp(new ErrorExp());
                return ue.value;
            }
            if ((n2 == -1L) && !type.isunsigned())
            {
                if (((long)n1 == -2147483648L) && ((type.toBasetype().ty.value & 0xFF) != ENUMTY.Tint64))
                {
                    e2.error(new BytePtr("integer overflow: `int.min / -1`"));
                    ptr(ue) = new UnionExp(new ErrorExp());
                    return ue.value;
                }
                else if (((long)n1 == -9223372036854775808L))
                {
                    e2.error(new BytePtr("integer overflow: `long.min / -1L`"));
                    ptr(ue) = new UnionExp(new ErrorExp());
                    return ue.value;
                }
            }
            if (e1.type.value.isunsigned() || e2.type.value.isunsigned())
            {
                n = (long)((long)n1 / (long)n2);
            }
            else
            {
                n = n1 / n2;
            }
            ptr(ue) = new UnionExp(new IntegerExp(loc, n, type));
        }
        return ue.value;
    }

    public static UnionExp Mod(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        if (type.isfloating())
        {
            complex_t c = c = new complex_t(CTFloat.zero.value);
            if (e2.type.value.isreal())
            {
                double r2 = e2.toReal();
                c = new complex_t(e1.toReal() % r2, e1.toImaginary() % r2).copy();
            }
            else if (e2.type.value.isimaginary())
            {
                double i2 = e2.toImaginary();
                c = new complex_t(e1.toReal() % i2, e1.toImaginary() % i2).copy();
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            if (type.isreal())
            {
                ptr(ue) = new UnionExp(new RealExp(loc, creall(c), type));
            }
            else if (type.isimaginary())
            {
                ptr(ue) = new UnionExp(new RealExp(loc, cimagl(c), type));
            }
            else if (type.iscomplex())
            {
                ptr(ue) = new UnionExp(new ComplexExp(loc, c, type));
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
        }
        else
        {
            long n1 = 0L;
            long n2 = 0L;
            long n = 0L;
            n1 = (long)e1.toInteger();
            n2 = (long)e2.toInteger();
            if ((n2 == 0L))
            {
                e2.error(new BytePtr("divide by 0"));
                ptr(ue) = new UnionExp(new ErrorExp());
                return ue.value;
            }
            if ((n2 == -1L) && !type.isunsigned())
            {
                if (((long)n1 == -2147483648L) && ((type.toBasetype().ty.value & 0xFF) != ENUMTY.Tint64))
                {
                    e2.error(new BytePtr("integer overflow: `int.min %% -1`"));
                    ptr(ue) = new UnionExp(new ErrorExp());
                    return ue.value;
                }
                else if (((long)n1 == -9223372036854775808L))
                {
                    e2.error(new BytePtr("integer overflow: `long.min %% -1L`"));
                    ptr(ue) = new UnionExp(new ErrorExp());
                    return ue.value;
                }
            }
            if (e1.type.value.isunsigned() || e2.type.value.isunsigned())
            {
                n = (long)((long)n1 % (long)n2);
            }
            else
            {
                n = n1 % n2;
            }
            ptr(ue) = new UnionExp(new IntegerExp(loc, n, type));
        }
        return ue.value;
    }

    public static UnionExp Pow(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (e2.type.value.isintegral())
        {
            long n = e2.toInteger();
            boolean neg = false;
            if (!e2.type.value.isunsigned() && ((long)n < 0L))
            {
                if (e1.type.value.isintegral())
                {
                    cantExp(ue);
                    return ue.value;
                }
                neg = true;
                n = -n;
            }
            else
            {
                neg = false;
            }
            Ref<UnionExp> ur = ref(new UnionExp().copy());
            Ref<UnionExp> uv = ref(new UnionExp().copy());
            if (e1.type.value.iscomplex())
            {
                ptr(ur) = new UnionExp(new ComplexExp(loc, e1.toComplex(), e1.type.value));
                ptr(uv) = new UnionExp(new ComplexExp(loc, new complex_t(CTFloat.one.value), e1.type.value));
            }
            else if (e1.type.value.isfloating())
            {
                ptr(ur) = new UnionExp(new RealExp(loc, e1.toReal(), e1.type.value));
                ptr(uv) = new UnionExp(new RealExp(loc, CTFloat.one.value, e1.type.value));
            }
            else
            {
                ptr(ur) = new UnionExp(new IntegerExp(loc, e1.toInteger(), e1.type.value));
                ptr(uv) = new UnionExp(new IntegerExp(loc, 1, e1.type.value));
            }
            Expression r = ur.value.exp();
            Expression v = uv.value.exp();
            for (; (n != 0L);){
                if ((n & 1L) != 0)
                {
                    uv.value = Mul(loc, v.type.value, v, r).copy();
                }
                n >>= 1;
                ur.value = Mul(loc, r.type.value, r, r).copy();
            }
            if (neg)
            {
                Ref<UnionExp> one = ref(new UnionExp().copy());
                ptr(one) = new UnionExp(new RealExp(loc, CTFloat.one.value, v.type.value));
                uv.value = Div(loc, v.type.value, one.value.exp(), v).copy();
            }
            if (type.iscomplex())
            {
                ptr(ue) = new UnionExp(new ComplexExp(loc, v.toComplex(), type));
            }
            else if (type.isintegral())
            {
                ptr(ue) = new UnionExp(new IntegerExp(loc, v.toInteger(), type));
            }
            else
            {
                ptr(ue) = new UnionExp(new RealExp(loc, v.toReal(), type));
            }
        }
        else if (e2.type.value.isfloating())
        {
            if ((e1.toReal() < CTFloat.zero.value))
            {
                ptr(ue) = new UnionExp(new RealExp(loc, target.RealProperties.nan.value, type));
            }
            else
            {
                cantExp(ue);
            }
        }
        else
        {
            cantExp(ue);
        }
        return ue.value;
    }

    public static UnionExp Shl(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() << (int)e2.toInteger(), type));
        return ue.value;
    }

    public static UnionExp Shr(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        long value = e1.toInteger();
        long dcount = e2.toInteger();
        assert((dcount <= 4294967295L));
        int count = (int)dcount;
        switch ((e1.type.value.toBasetype().ty.value & 0xFF))
        {
            case 13:
                value = (long)(((byte)value & 0xFF) >> count);
                break;
            case 14:
            case 31:
                value = (long)(((byte)value & 0xFF) >> count);
                break;
            case 15:
                value = (long)((int)(int)value >> count);
                break;
            case 16:
            case 32:
                value = (long)((int)(int)value >> count);
                break;
            case 17:
                value = (long)((int)value >> count);
                break;
            case 18:
            case 33:
                value = (long)((int)value >> count);
                break;
            case 19:
                value = (long)((long)value >> count);
                break;
            case 20:
                value = value >> count;
                break;
            case 34:
                ptr(ue) = new UnionExp(new ErrorExp());
                return ue.value;
            default:
            throw new AssertionError("Unreachable code!");
        }
        ptr(ue) = new UnionExp(new IntegerExp(loc, value, type));
        return ue.value;
    }

    public static UnionExp Ushr(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        long value = e1.toInteger();
        long dcount = e2.toInteger();
        assert((dcount <= 4294967295L));
        int count = (int)dcount;
        switch ((e1.type.value.toBasetype().ty.value & 0xFF))
        {
            case 13:
            case 14:
            case 31:
                value = (value & 255L) >> count;
                break;
            case 15:
            case 16:
            case 32:
                value = (value & 65535L) >> count;
                break;
            case 17:
            case 18:
            case 33:
                value = (value & 4294967295L) >> count;
                break;
            case 19:
            case 20:
                value = value >> count;
                break;
            case 34:
                ptr(ue) = new UnionExp(new ErrorExp());
                return ue.value;
            default:
            throw new AssertionError("Unreachable code!");
        }
        ptr(ue) = new UnionExp(new IntegerExp(loc, value, type));
        return ue.value;
    }

    public static UnionExp And(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() & e2.toInteger(), type));
        return ue.value;
    }

    public static UnionExp Or(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() | e2.toInteger(), type));
        return ue.value;
    }

    public static UnionExp Xor(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() ^ e2.toInteger(), type));
        return ue.value;
    }

    public static UnionExp Equal(byte op, Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        int cmp = 0;
        double r1 = CTFloat.zero.value;
        double r2 = CTFloat.zero.value;
        assert(((op & 0xFF) == 58) || ((op & 0xFF) == 59));
        if (((e1.op.value & 0xFF) == 13))
        {
            if (((e2.op.value & 0xFF) == 13))
            {
                cmp = 1;
            }
            else if (((e2.op.value & 0xFF) == 121))
            {
                StringExp es2 = (StringExp)e2;
                cmp = ((0 == es2.len.value) ? 1 : 0);
            }
            else if (((e2.op.value & 0xFF) == 47))
            {
                ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
                cmp = (((es2.elements.value == null) || (0 == (es2.elements.value.get()).length.value)) ? 1 : 0);
            }
            else
            {
                cantExp(ue);
                return ue.value;
            }
        }
        else if (((e2.op.value & 0xFF) == 13))
        {
            if (((e1.op.value & 0xFF) == 121))
            {
                StringExp es1 = (StringExp)e1;
                cmp = ((0 == es1.len.value) ? 1 : 0);
            }
            else if (((e1.op.value & 0xFF) == 47))
            {
                ArrayLiteralExp es1 = (ArrayLiteralExp)e1;
                cmp = (((es1.elements.value == null) || (0 == (es1.elements.value.get()).length.value)) ? 1 : 0);
            }
            else
            {
                cantExp(ue);
                return ue.value;
            }
        }
        else if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es2 = (StringExp)e2;
            if (((es1.sz.value & 0xFF) != (es2.sz.value & 0xFF)))
            {
                assert(global.errors.value != 0);
                cantExp(ue);
                return ue.value;
            }
            if ((es1.len.value == es2.len.value) && (memcmp(es1.string.value, es2.string.value, (es1.sz.value & 0xFF) * es1.len.value) == 0))
            {
                cmp = 1;
            }
            else
            {
                cmp = 0;
            }
        }
        else if (((e1.op.value & 0xFF) == 47) && ((e2.op.value & 0xFF) == 47))
        {
            ArrayLiteralExp es1 = (ArrayLiteralExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            if ((es1.elements.value == null) || ((es1.elements.value.get()).length.value == 0) && (es2.elements.value == null) || ((es2.elements.value.get()).length.value == 0))
            {
                cmp = 1;
            }
            else if ((es1.elements.value == null) || (es2.elements.value == null))
            {
                cmp = 0;
            }
            else if (((es1.elements.value.get()).length.value != (es2.elements.value.get()).length.value))
            {
                cmp = 0;
            }
            else
            {
                {
                    int i = 0;
                    for (; (i < (es1.elements.value.get()).length.value);i++){
                        Expression ee1 = es1.getElement(i);
                        Expression ee2 = es2.getElement(i);
                        ue.value = Equal(TOK.equal, loc, Type.tint32.value, ee1, ee2).copy();
                        if (CTFEExp.isCantExp(ue.value.exp()))
                        {
                            return ue.value;
                        }
                        cmp = (int)ue.value.exp().toInteger();
                        if ((cmp == 0))
                        {
                            break;
                        }
                    }
                }
            }
        }
        else if (((e1.op.value & 0xFF) == 47) && ((e2.op.value & 0xFF) == 121))
        {
            Expression etmp = e1;
            e1 = e2;
            e2 = etmp;
            /*goto Lsa*//*unrolled goto*/
        /*Lsa:*/
            StringExp es1 = (StringExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            int dim1 = es1.len.value;
            int dim2 = es2.elements.value != null ? (es2.elements.value.get()).length.value : 0;
            if ((dim1 != dim2))
            {
                cmp = 0;
            }
            else
            {
                cmp = 1;
                {
                    int i = 0;
                    for (; (i < dim1);i++){
                        long c = (long)es1.charAt((long)i);
                        Expression ee2 = es2.getElement(i);
                        if ((ee2.isConst() != 1))
                        {
                            cantExp(ue);
                            return ue.value;
                        }
                        cmp = ((c == ee2.toInteger()) ? 1 : 0);
                        if ((cmp == 0))
                        {
                            break;
                        }
                    }
                }
            }
        }
        else if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 47))
        {
        /*Lsa:*/
            StringExp es1 = (StringExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            int dim1 = es1.len.value;
            int dim2 = es2.elements.value != null ? (es2.elements.value.get()).length.value : 0;
            if ((dim1 != dim2))
            {
                cmp = 0;
            }
            else
            {
                cmp = 1;
                {
                    int i = 0;
                    for (; (i < dim1);i++){
                        long c = (long)es1.charAt((long)i);
                        Expression ee2 = es2.getElement(i);
                        if ((ee2.isConst() != 1))
                        {
                            cantExp(ue);
                            return ue.value;
                        }
                        cmp = ((c == ee2.toInteger()) ? 1 : 0);
                        if ((cmp == 0))
                        {
                            break;
                        }
                    }
                }
            }
        }
        else if (((e1.op.value & 0xFF) == 49) && ((e2.op.value & 0xFF) == 49))
        {
            StructLiteralExp es1 = (StructLiteralExp)e1;
            StructLiteralExp es2 = (StructLiteralExp)e2;
            if ((!pequals(es1.sd, es2.sd)))
            {
                cmp = 0;
            }
            else if ((es1.elements.value == null) || ((es1.elements.value.get()).length.value == 0) && (es2.elements.value == null) || ((es2.elements.value.get()).length.value == 0))
            {
                cmp = 1;
            }
            else if ((es1.elements.value == null) || (es2.elements.value == null))
            {
                cmp = 0;
            }
            else if (((es1.elements.value.get()).length.value != (es2.elements.value.get()).length.value))
            {
                cmp = 0;
            }
            else
            {
                cmp = 1;
                {
                    int i = 0;
                    for (; (i < (es1.elements.value.get()).length.value);i++){
                        Expression ee1 = (es1.elements.value.get()).get(i);
                        Expression ee2 = (es2.elements.value.get()).get(i);
                        if ((pequals(ee1, ee2)))
                        {
                            continue;
                        }
                        if ((ee1 == null) || (ee2 == null))
                        {
                            cmp = 0;
                            break;
                        }
                        ue.value = Equal(TOK.equal, loc, Type.tint32.value, ee1, ee2).copy();
                        if (((ue.value.exp().op.value & 0xFF) == 233))
                        {
                            return ue.value;
                        }
                        cmp = (int)ue.value.exp().toInteger();
                        if ((cmp == 0))
                        {
                            break;
                        }
                    }
                }
            }
        }
        else if ((e1.isConst() != 1) || (e2.isConst() != 1))
        {
            cantExp(ue);
            return ue.value;
        }
        else if (e1.type.value.isreal())
        {
            r1 = e1.toReal();
            r2 = e2.toReal();
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            if (CTFloat.isNaN(r1) || CTFloat.isNaN(r2))
            {
                cmp = 0;
            }
            else
            {
                cmp = ((r1 == r2) ? 1 : 0);
            }
        }
        else if (e1.type.value.isimaginary())
        {
            r1 = e1.toImaginary();
            r2 = e2.toImaginary();
        /*L1:*/
            if (CTFloat.isNaN(r1) || CTFloat.isNaN(r2))
            {
                cmp = 0;
            }
            else
            {
                cmp = ((r1 == r2) ? 1 : 0);
            }
        }
        else if (e1.type.value.iscomplex())
        {
            cmp = e1.toComplex().opEquals(e2.toComplex());
        }
        else if (e1.type.value.isintegral() || ((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tpointer))
        {
            cmp = ((e1.toInteger() == e2.toInteger()) ? 1 : 0);
        }
        else
        {
            cantExp(ue);
            return ue.value;
        }
        if (((op & 0xFF) == 59))
        {
            cmp ^= 1;
        }
        ptr(ue) = new UnionExp(new IntegerExp(loc, cmp, type));
        return ue.value;
    }

    public static UnionExp Identity(byte op, Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        int cmp = 0;
        if (((e1.op.value & 0xFF) == 13))
        {
            cmp = (((e2.op.value & 0xFF) == 13) ? 1 : 0);
        }
        else if (((e2.op.value & 0xFF) == 13))
        {
            cmp = 0;
        }
        else if (((e1.op.value & 0xFF) == 25) && ((e2.op.value & 0xFF) == 25))
        {
            SymOffExp es1 = (SymOffExp)e1;
            SymOffExp es2 = (SymOffExp)e2;
            cmp = (((pequals(es1.var.value, es2.var.value)) && (es1.offset.value == es2.offset.value)) ? 1 : 0);
        }
        else
        {
            if (e1.type.value.isreal())
            {
                cmp = RealIdentical(e1.toReal(), e2.toReal());
            }
            else if (e1.type.value.isimaginary())
            {
                cmp = RealIdentical(e1.toImaginary(), e2.toImaginary());
            }
            else if (e1.type.value.iscomplex())
            {
                complex_t v1 = e1.toComplex().copy();
                complex_t v2 = e2.toComplex().copy();
                cmp = (((RealIdentical(creall(v1), creall(v2)) != 0) && (RealIdentical(cimagl(v1), cimagl(v1)) != 0)) ? 1 : 0);
            }
            else
            {
                ue.value = Equal(((op & 0xFF) == 60) ? TOK.equal : TOK.notEqual, loc, type, e1, e2).copy();
                return ue.value;
            }
        }
        if (((op & 0xFF) == 61))
        {
            cmp ^= 1;
        }
        ptr(ue) = new UnionExp(new IntegerExp(loc, cmp, type));
        return ue.value;
    }

    public static UnionExp Cmp(byte op, Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        long n = 0L;
        double r1 = CTFloat.zero.value;
        double r2 = CTFloat.zero.value;
        if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es2 = (StringExp)e2;
            int sz = (es1.sz.value & 0xFF);
            assert((sz == (es2.sz.value & 0xFF)));
            int len = es1.len.value;
            if ((es2.len.value < len))
            {
                len = es2.len.value;
            }
            int rawCmp = memcmp(es1.string.value, es2.string.value, sz * len);
            if ((rawCmp == 0))
            {
                rawCmp = (es1.len.value - es2.len.value);
            }
            n = (long)specificCmp(op, rawCmp);
        }
        else if ((e1.isConst() != 1) || (e2.isConst() != 1))
        {
            cantExp(ue);
            return ue.value;
        }
        else if (e1.type.value.isreal())
        {
            r1 = e1.toReal();
            r2 = e2.toReal();
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            n = (long)realCmp(op, r1, r2);
        }
        else if (e1.type.value.isimaginary())
        {
            r1 = e1.toImaginary();
            r2 = e2.toImaginary();
        /*L1:*/
            n = (long)realCmp(op, r1, r2);
        }
        else if (e1.type.value.iscomplex())
        {
            throw new AssertionError("Unreachable code!");
        }
        else
        {
            long n1 = 0L;
            long n2 = 0L;
            n1 = (long)e1.toInteger();
            n2 = (long)e2.toInteger();
            if (e1.type.value.isunsigned() || e2.type.value.isunsigned())
            {
                n = (long)intUnsignedCmp(op, (long)n1, (long)n2);
            }
            else
            {
                n = (long)intSignedCmp(op, n1, n2);
            }
        }
        ptr(ue) = new UnionExp(new IntegerExp(loc, n, type));
        return ue.value;
    }

    public static UnionExp Cast(Loc loc, Type type, Type to, Expression e1) {
        Ref<UnionExp> ue = ref(null);
        Type tb = to.toBasetype();
        Type typeb = type.toBasetype();
        if (e1.type.value.equals(type) && type.equals(to))
        {
            ptr(ue) = new UnionExp(new UnionExp(e1));
            return ue.value;
        }
        if (((e1.op.value & 0xFF) == 229) && ((TypeVector)e1.type.value).basetype.value.equals(type) && type.equals(to))
        {
            Expression ex = ((VectorExp)e1).e1.value;
            ptr(ue) = new UnionExp(new UnionExp(ex));
            return ue.value;
        }
        if ((e1.type.value.implicitConvTo(to) >= MATCH.constant) || (to.implicitConvTo(e1.type.value) >= MATCH.constant))
        {
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            Expression ex = expType(to, e1);
            ptr(ue) = new UnionExp(new UnionExp(ex));
            return ue.value;
        }
        if (((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tdelegate) && (e1.type.value.implicitConvTo(to) == MATCH.convert))
        {
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            Expression ex = expType(to, e1);
            ptr(ue) = new UnionExp(new UnionExp(ex));
            return ue.value;
        }
        if (((e1.op.value & 0xFF) == 121))
        {
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) && ((typeb.ty.value & 0xFF) == ENUMTY.Tarray) && (tb.nextOf().size() == typeb.nextOf().size()))
            {
                /*goto L1*//*unrolled goto*/
            /*L1:*/
                Expression ex = expType(to, e1);
                ptr(ue) = new UnionExp(new UnionExp(ex));
                return ue.value;
            }
        }
        if (((e1.op.value & 0xFF) == 47) && (pequals(typeb, tb)))
        {
        /*L1:*/
            Expression ex = expType(to, e1);
            ptr(ue) = new UnionExp(new UnionExp(ex));
            return ue.value;
        }
        if ((e1.isConst() != 1))
        {
            cantExp(ue);
        }
        else if (((tb.ty.value & 0xFF) == ENUMTY.Tbool))
        {
            ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger() != 0L, type));
        }
        else if (type.isintegral())
        {
            if (e1.type.value.isfloating())
            {
                long result = 0L;
                double r = e1.toReal();
                switch ((typeb.ty.value & 0xFF))
                {
                    case 13:
                        result = (long)(byte)(long)r;
                        break;
                    case 31:
                    case 14:
                        result = (long)(byte)(long)r;
                        break;
                    case 15:
                        result = (long)(int)(long)r;
                        break;
                    case 32:
                    case 16:
                        result = (long)(int)(long)r;
                        break;
                    case 17:
                        result = (long)(int)r;
                        break;
                    case 33:
                    case 18:
                        result = (long)(int)r;
                        break;
                    case 19:
                        result = (long)(long)r;
                        break;
                    case 20:
                        result = (long)r;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                ptr(ue) = new UnionExp(new IntegerExp(loc, result, type));
            }
            else if (type.isunsigned())
            {
                ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toUInteger(), type));
            }
            else
            {
                ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger(), type));
            }
        }
        else if (tb.isreal())
        {
            double value = e1.toReal();
            ptr(ue) = new UnionExp(new RealExp(loc, value, type));
        }
        else if (tb.isimaginary())
        {
            double value = e1.toImaginary();
            ptr(ue) = new UnionExp(new RealExp(loc, value, type));
        }
        else if (tb.iscomplex())
        {
            complex_t value = e1.toComplex().copy();
            ptr(ue) = new UnionExp(new ComplexExp(loc, value, type));
        }
        else if (tb.isscalar())
        {
            ptr(ue) = new UnionExp(new IntegerExp(loc, e1.toInteger(), type));
        }
        else if (((tb.ty.value & 0xFF) == ENUMTY.Tvoid))
        {
            cantExp(ue);
        }
        else if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct) && ((e1.op.value & 0xFF) == 135))
        {
            StructDeclaration sd = tb.toDsymbol(null).isStructDeclaration();
            assert(sd != null);
            Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>());
            {
                int i = 0;
                for (; (i < sd.fields.length.value);i++){
                    VarDeclaration v = sd.fields.get(i);
                    Ref<UnionExp> zero = ref(new UnionExp().copy());
                    ptr(zero) = new UnionExp(new IntegerExp(0));
                    ue.value = Cast(loc, v.type.value, v.type.value, zero.value.exp()).copy();
                    if (((ue.value.exp().op.value & 0xFF) == 233))
                    {
                        return ue.value;
                    }
                    (elements.get()).push(ue.value.exp().copy());
                }
            }
            ptr(ue) = new UnionExp(new StructLiteralExp(loc, sd, elements));
            ue.value.exp().type.value = type;
        }
        else
        {
            if ((!pequals(type, Type.terror.value)))
            {
                error(loc, new BytePtr("cannot cast `%s` to `%s`"), e1.type.value.toChars(), type.toChars());
            }
            ptr(ue) = new UnionExp(new ErrorExp());
        }
        return ue.value;
    }

    public static UnionExp ArrayLength(Type type, Expression e1) {
        Ref<UnionExp> ue = ref(null);
        Loc loc = e1.loc.value.copy();
        if (((e1.op.value & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            ptr(ue) = new UnionExp(new IntegerExp(loc, es1.len.value, type));
        }
        else if (((e1.op.value & 0xFF) == 47))
        {
            ArrayLiteralExp ale = (ArrayLiteralExp)e1;
            int dim = ale.elements.value != null ? (ale.elements.value.get()).length.value : 0;
            ptr(ue) = new UnionExp(new IntegerExp(loc, dim, type));
        }
        else if (((e1.op.value & 0xFF) == 48))
        {
            AssocArrayLiteralExp ale = (AssocArrayLiteralExp)e1;
            int dim = (ale.keys.value.get()).length.value;
            ptr(ue) = new UnionExp(new IntegerExp(loc, dim, type));
        }
        else if (((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
        {
            Expression e = ((TypeSArray)e1.type.value.toBasetype()).dim.value;
            ptr(ue) = new UnionExp(new UnionExp(e));
        }
        else
        {
            cantExp(ue);
        }
        return ue.value;
    }

    public static UnionExp Index(Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        Loc loc = e1.loc.value.copy();
        assert(e1.type.value != null);
        if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 135))
        {
            StringExp es1 = (StringExp)e1;
            long i = e2.toInteger();
            if ((i >= (long)es1.len.value))
            {
                e1.error(new BytePtr("string index %llu is out of bounds `[0 .. %llu]`"), i, (long)es1.len.value);
                ptr(ue) = new UnionExp(new ErrorExp());
            }
            else
            {
                ptr(ue) = new UnionExp(new IntegerExp(loc, es1.charAt(i), type));
            }
        }
        else if (((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray) && ((e2.op.value & 0xFF) == 135))
        {
            TypeSArray tsa = (TypeSArray)e1.type.value.toBasetype();
            long length = tsa.dim.value.toInteger();
            long i = e2.toInteger();
            if ((i >= length))
            {
                e1.error(new BytePtr("array index %llu is out of bounds `%s[0 .. %llu]`"), i, e1.toChars(), length);
                ptr(ue) = new UnionExp(new ErrorExp());
            }
            else if (((e1.op.value & 0xFF) == 47))
            {
                ArrayLiteralExp ale = (ArrayLiteralExp)e1;
                Expression e = ale.getElement((int)i);
                e.type.value = type;
                e.loc.value = loc.copy();
                if (hasSideEffect(e))
                {
                    cantExp(ue);
                }
                else
                {
                    ptr(ue) = new UnionExp(new UnionExp(e));
                }
            }
            else
            {
                cantExp(ue);
            }
        }
        else if (((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray) && ((e2.op.value & 0xFF) == 135))
        {
            long i = e2.toInteger();
            if (((e1.op.value & 0xFF) == 47))
            {
                ArrayLiteralExp ale = (ArrayLiteralExp)e1;
                if ((i >= (long)(ale.elements.value.get()).length.value))
                {
                    e1.error(new BytePtr("array index %llu is out of bounds `%s[0 .. %u]`"), i, e1.toChars(), (ale.elements.value.get()).length.value);
                    ptr(ue) = new UnionExp(new ErrorExp());
                }
                else
                {
                    Expression e = ale.getElement((int)i);
                    e.type.value = type;
                    e.loc.value = loc.copy();
                    if (hasSideEffect(e))
                    {
                        cantExp(ue);
                    }
                    else
                    {
                        ptr(ue) = new UnionExp(new UnionExp(e));
                    }
                }
            }
            else
            {
                cantExp(ue);
            }
        }
        else if (((e1.op.value & 0xFF) == 48))
        {
            AssocArrayLiteralExp ae = (AssocArrayLiteralExp)e1;
            {
                int i = (ae.keys.value.get()).length.value;
                for (; i != 0;){
                    i--;
                    Expression ekey = (ae.keys.value.get()).get(i);
                    ue.value = Equal(TOK.equal, loc, Type.tbool.value, ekey, e2).copy();
                    if (CTFEExp.isCantExp(ue.value.exp()))
                    {
                        return ue.value;
                    }
                    if (ue.value.exp().isBool(true))
                    {
                        Expression e = (ae.values.value.get()).get(i);
                        e.type.value = type;
                        e.loc.value = loc.copy();
                        if (hasSideEffect(e))
                        {
                            cantExp(ue);
                        }
                        else
                        {
                            ptr(ue) = new UnionExp(new UnionExp(e));
                        }
                        return ue.value;
                    }
                }
            }
            cantExp(ue);
        }
        else
        {
            cantExp(ue);
        }
        return ue.value;
    }

    public static UnionExp Slice(Type type, Expression e1, Expression lwr, Expression upr) {
        Ref<UnionExp> ue = ref(null);
        Loc loc = e1.loc.value.copy();
        Function4<Long,Long,Long,Long,Boolean> sliceBoundsCheck = new Function4<Long,Long,Long,Long,Boolean>(){
            public Boolean invoke(Long lwr, Long upr, Long newlwr, Long newupr) {
                Ref<Long> lwr_ref = ref(lwr);
                Ref<Long> upr_ref = ref(upr);
                Ref<Long> newlwr_ref = ref(newlwr);
                Ref<Long> newupr_ref = ref(newupr);
                assert((lwr_ref.value <= upr_ref.value));
                return !((newlwr_ref.value <= newupr_ref.value) && (lwr_ref.value <= newlwr_ref.value) && (newupr_ref.value <= upr_ref.value));
            }
        };
        if (((e1.op.value & 0xFF) == 121) && ((lwr.op.value & 0xFF) == 135) && ((upr.op.value & 0xFF) == 135))
        {
            StringExp es1 = (StringExp)e1;
            long ilwr = lwr.toInteger();
            long iupr = upr.toInteger();
            if (sliceBoundsCheck.invoke(0L, (long)es1.len.value, ilwr, iupr))
            {
                cantExp(ue);
            }
            else
            {
                int len = (int)(iupr - ilwr);
                byte sz = es1.sz.value;
                Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
                memcpy((BytePtr)s, ((es1.string.value.plus((int)(ilwr * (long)(sz & 0xFF))))), (len * (sz & 0xFF)));
                ptr(ue) = new UnionExp(new StringExp(loc, s, len, es1.postfix.value));
                StringExp es = (StringExp)ue.value.exp();
                es.sz.value = sz;
                es.committed.value = es1.committed.value;
                es.type.value = type;
            }
        }
        else if (((e1.op.value & 0xFF) == 47) && ((lwr.op.value & 0xFF) == 135) && ((upr.op.value & 0xFF) == 135) && !hasSideEffect(e1))
        {
            ArrayLiteralExp es1 = (ArrayLiteralExp)e1;
            long ilwr = lwr.toInteger();
            long iupr = upr.toInteger();
            if (sliceBoundsCheck.invoke(0L, (long)(es1.elements.value.get()).length.value, ilwr, iupr))
            {
                cantExp(ue);
            }
            else
            {
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>((int)(iupr - ilwr)));
                memcpy((BytePtr)((elements.get()).tdata()), (((es1.elements.value.get()).tdata().plus((int)ilwr * 4))), ((int)(iupr - ilwr) * 4));
                ptr(ue) = new UnionExp(new ArrayLiteralExp(e1.loc.value, type, elements));
            }
        }
        else
        {
            cantExp(ue);
        }
        return ue.value;
    }

    public static void sliceAssignArrayLiteralFromString(ArrayLiteralExp existingAE, StringExp newval, int firstIndex) {
        int len = newval.len.value;
        Type elemType = existingAE.type.value.nextOf();
        {
            int __key844 = 0;
            int __limit845 = len;
            for (; (__key844 < __limit845);__key844 += 1) {
                int j = __key844;
                int val = newval.getCodeUnit(j);
                existingAE.elements.value.get().set(j + firstIndex, new IntegerExp(newval.loc.value, (long)val, elemType));
            }
        }
    }

    public static void sliceAssignStringFromArrayLiteral(StringExp existingSE, ArrayLiteralExp newae, int firstIndex) {
        assert(((existingSE.ownedByCtfe & 0xFF) != 0));
        {
            int __key846 = 0;
            int __limit847 = (newae.elements.value.get()).length.value;
            for (; (__key846 < __limit847);__key846 += 1) {
                int j = __key846;
                existingSE.setCodeUnit(firstIndex + j, (int)newae.getElement(j).toInteger());
            }
        }
    }

    public static void sliceAssignStringFromString(StringExp existingSE, StringExp newstr, int firstIndex) {
        assert(((existingSE.ownedByCtfe & 0xFF) != 0));
        int sz = (existingSE.sz.value & 0xFF);
        assert((sz == (newstr.sz.value & 0xFF)));
        memcpy((BytePtr)((existingSE.string.value.plus((firstIndex * sz)))), (newstr.string.value), (sz * newstr.len.value));
    }

    public static int sliceCmpStringWithString(StringExp se1, StringExp se2, int lo1, int lo2, int len) {
        int sz = (se1.sz.value & 0xFF);
        assert((sz == (se2.sz.value & 0xFF)));
        return memcmp((se1.string.value.plus((sz * lo1))), (se2.string.value.plus((sz * lo2))), sz * len);
    }

    public static int sliceCmpStringWithArray(StringExp se1, ArrayLiteralExp ae2, int lo1, int lo2, int len) {
        {
            int __key848 = 0;
            int __limit849 = len;
            for (; (__key848 < __limit849);__key848 += 1) {
                int j = __key848;
                int val2 = (int)ae2.getElement(j + lo2).toInteger();
                int val1 = se1.getCodeUnit(j + lo1);
                int c = (val1 - val2);
                if (c != 0)
                {
                    return c;
                }
            }
        }
        return 0;
    }

    public static Ptr<DArray<Expression>> copyElements(Expression e1, Expression e2) {
        Ptr<DArray<Expression>> elems = refPtr(new DArray<Expression>());
        Function1<ArrayLiteralExp,Void> append = new Function1<ArrayLiteralExp,Void>(){
            public Void invoke(ArrayLiteralExp ale) {
                if (ale.elements.value == null)
                {
                    return null;
                }
                IntRef d = ref((elems.get()).length.value);
                (elems.get()).append(ale.elements.value);
                {
                    Ref<Slice<Expression>> __r850 = ref((elems.get()).opSlice(d.value, (elems.get()).length.value).copy());
                    IntRef __key851 = ref(0);
                    for (; (__key851.value < __r850.value.getLength());__key851.value += 1) {
                        Ref<Expression> el = ref(__r850.value.get(__key851.value));
                        if (el.value == null)
                        {
                            el.value = ale.basis.value;
                        }
                    }
                }
                return null;
            }
        };
        if (((e1.op.value & 0xFF) == 47))
        {
            append.invoke((ArrayLiteralExp)e1);
        }
        else
        {
            (elems.get()).push(e1);
        }
        if (e2 != null)
        {
            if (((e2.op.value & 0xFF) == 47))
            {
                append.invoke((ArrayLiteralExp)e2);
            }
            else
            {
                (elems.get()).push(e2);
            }
        }
        return elems;
    }

    // defaulted all parameters starting with #2
    public static Ptr<DArray<Expression>> copyElements(Expression e1) {
        return copyElements(e1, null);
    }

    public static UnionExp Cat(Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        Expression e = CTFEExp.cantexp.value;
        Loc loc = e1.loc.value.copy();
        Type t = null;
        Type t1 = e1.type.value.toBasetype();
        Type t2 = e2.type.value.toBasetype();
        if (((e1.op.value & 0xFF) == 13) && ((e2.op.value & 0xFF) == 135) || ((e2.op.value & 0xFF) == 49))
        {
            e = e2;
            t = t1;
            /*goto L2*//*unrolled goto*/
        /*L2:*/
            Type tn = e.type.value.toBasetype();
            if (((tn.ty.value & 0xFF) == ENUMTY.Tchar) || ((tn.ty.value & 0xFF) == ENUMTY.Twchar) || ((tn.ty.value & 0xFF) == ENUMTY.Tdchar))
            {
                if (t.nextOf() != null)
                {
                    t = t.nextOf().toBasetype();
                }
                byte sz = (byte)t.size();
                long v = e.toInteger();
                int len = ((t.ty.value & 0xFF) == (tn.ty.value & 0xFF)) ? 1 : utf_codeLength((sz & 0xFF), (int)v);
                Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
                if (((t.ty.value & 0xFF) == (tn.ty.value & 0xFF)))
                {
                    Port.valcpy(s, v, (sz & 0xFF));
                }
                else
                {
                    utf_encode((sz & 0xFF), s, (int)v);
                }
                ptr(ue) = new UnionExp(new StringExp(loc, s, len));
                StringExp es = (StringExp)ue.value.exp();
                es.type.value = type;
                es.sz.value = sz;
                es.committed.value = (byte)1;
            }
            else
            {
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>());
                (elements.get()).push(e);
                ptr(ue) = new UnionExp(new ArrayLiteralExp(e.loc.value, type, elements));
            }
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 135) || ((e1.op.value & 0xFF) == 49) && ((e2.op.value & 0xFF) == 13))
        {
            e = e1;
            t = t2;
        /*L2:*/
            Type tn = e.type.value.toBasetype();
            if (((tn.ty.value & 0xFF) == ENUMTY.Tchar) || ((tn.ty.value & 0xFF) == ENUMTY.Twchar) || ((tn.ty.value & 0xFF) == ENUMTY.Tdchar))
            {
                if (t.nextOf() != null)
                {
                    t = t.nextOf().toBasetype();
                }
                byte sz = (byte)t.size();
                long v = e.toInteger();
                int len = ((t.ty.value & 0xFF) == (tn.ty.value & 0xFF)) ? 1 : utf_codeLength((sz & 0xFF), (int)v);
                Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
                if (((t.ty.value & 0xFF) == (tn.ty.value & 0xFF)))
                {
                    Port.valcpy(s, v, (sz & 0xFF));
                }
                else
                {
                    utf_encode((sz & 0xFF), s, (int)v);
                }
                ptr(ue) = new UnionExp(new StringExp(loc, s, len));
                StringExp es = (StringExp)ue.value.exp();
                es.type.value = type;
                es.sz.value = sz;
                es.committed.value = (byte)1;
            }
            else
            {
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>());
                (elements.get()).push(e);
                ptr(ue) = new UnionExp(new ArrayLiteralExp(e.loc.value, type, elements));
            }
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 13) && ((e2.op.value & 0xFF) == 13))
        {
            if ((pequals(type, e1.type.value)))
            {
                if (((t1.ty.value & 0xFF) == ENUMTY.Tarray) && (pequals(t2, t1.nextOf())))
                {
                    ptr(ue) = new UnionExp(new ArrayLiteralExp(e1.loc.value, type, e2));
                    assert(ue.value.exp().type.value != null);
                    return ue.value;
                }
                else
                {
                    ptr(ue) = new UnionExp(new UnionExp(e1));
                    assert(ue.value.exp().type.value != null);
                    return ue.value;
                }
            }
            if ((pequals(type, e2.type.value)))
            {
                ptr(ue) = new UnionExp(new UnionExp(e2));
                assert(ue.value.exp().type.value != null);
                return ue.value;
            }
            ptr(ue) = new UnionExp(new NullExp(e1.loc.value, type));
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es2 = (StringExp)e2;
            int len = es1.len.value + es2.len.value;
            byte sz = es1.sz.value;
            if (((sz & 0xFF) != (es2.sz.value & 0xFF)))
            {
                assert(global.errors.value != 0);
                cantExp(ue);
                assert(ue.value.exp().type.value != null);
                return ue.value;
            }
            Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
            memcpy((BytePtr)(((BytePtr)s)), (es1.string.value), (es1.len.value * (sz & 0xFF)));
            memcpy((BytePtr)((((BytePtr)s).plus((es1.len.value * (sz & 0xFF))))), (es2.string.value), (es2.len.value * (sz & 0xFF)));
            ptr(ue) = new UnionExp(new StringExp(loc, s, len));
            StringExp es = (StringExp)ue.value.exp();
            es.sz.value = sz;
            es.committed.value = (byte)((es1.committed.value & 0xFF) | (es2.committed.value & 0xFF));
            es.type.value = type;
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e2.op.value & 0xFF) == 121) && ((e1.op.value & 0xFF) == 47) && t1.nextOf().isintegral())
        {
            StringExp es = (StringExp)e2;
            ArrayLiteralExp ea = (ArrayLiteralExp)e1;
            int len = es.len.value + (ea.elements.value.get()).length.value;
            Ptr<DArray<Expression>> elems = refPtr(new DArray<Expression>(len));
            {
                int i = 0;
                for (; (i < (ea.elements.value.get()).length.value);i += 1){
                    elems.get().set(i, ea.getElement(i));
                }
            }
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e1.loc.value, type, elems));
            ArrayLiteralExp dest = (ArrayLiteralExp)ue.value.exp();
            sliceAssignArrayLiteralFromString(dest, es, (ea.elements.value.get()).length.value);
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 47) && t2.nextOf().isintegral())
        {
            StringExp es = (StringExp)e1;
            ArrayLiteralExp ea = (ArrayLiteralExp)e2;
            int len = es.len.value + (ea.elements.value.get()).length.value;
            Ptr<DArray<Expression>> elems = refPtr(new DArray<Expression>(len));
            {
                int i = 0;
                for (; (i < (ea.elements.value.get()).length.value);i += 1){
                    elems.get().set(es.len.value + i, ea.getElement(i));
                }
            }
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e1.loc.value, type, elems));
            ArrayLiteralExp dest = (ArrayLiteralExp)ue.value.exp();
            sliceAssignArrayLiteralFromString(dest, es, 0);
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 135))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es = null;
            byte sz = es1.sz.value;
            long v = e2.toInteger();
            boolean homoConcat = (long)(sz & 0xFF) == t2.size();
            int len = es1.len.value;
            len += homoConcat ? 1 : utf_codeLength((sz & 0xFF), (int)v);
            Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
            memcpy((BytePtr)s, (es1.string.value), (es1.len.value * (sz & 0xFF)));
            if (homoConcat)
            {
                Port.valcpy((((BytePtr)s).plus(((sz & 0xFF) * es1.len.value))), v, (sz & 0xFF));
            }
            else
            {
                utf_encode((sz & 0xFF), (((BytePtr)s).plus(((sz & 0xFF) * es1.len.value))), (int)v);
            }
            ptr(ue) = new UnionExp(new StringExp(loc, s, len));
            es = (StringExp)ue.value.exp();
            es.sz.value = sz;
            es.committed.value = es1.committed.value;
            es.type.value = type;
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 135) && ((e2.op.value & 0xFF) == 121))
        {
            StringExp es2 = (StringExp)e2;
            int len = 1 + es2.len.value;
            byte sz = es2.sz.value;
            long v = e1.toInteger();
            Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
            Port.valcpy(((BytePtr)s), v, (sz & 0xFF));
            memcpy((BytePtr)((((BytePtr)s).plus((sz & 0xFF)))), (es2.string.value), (es2.len.value * (sz & 0xFF)));
            ptr(ue) = new UnionExp(new StringExp(loc, s, len));
            StringExp es = (StringExp)ue.value.exp();
            es.sz.value = sz;
            es.committed.value = es2.committed.value;
            es.type.value = type;
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 47) && ((e2.op.value & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            Ptr<DArray<Expression>> elems = copyElements(e1, e2);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e1.loc.value, null, elems));
            e = ue.value.exp();
            if (((type.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e.type.value = t1.nextOf().sarrayOf((long)(elems.get()).length.value);
            }
            else
            {
                e.type.value = type;
            }
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 47) && ((e2.op.value & 0xFF) == 13) && t1.nextOf().equals(t2.nextOf()))
        {
            e = e1;
            /*goto L3*//*unrolled goto*/
        /*L3:*/
            Ptr<DArray<Expression>> elems = copyElements(e, null);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e.loc.value, null, elems));
            e = ue.value.exp();
            if (((type.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e.type.value = t1.nextOf().sarrayOf((long)(elems.get()).length.value);
            }
            else
            {
                e.type.value = type;
            }
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 13) && ((e2.op.value & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            e = e2;
        /*L3:*/
            Ptr<DArray<Expression>> elems = copyElements(e, null);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e.loc.value, null, elems));
            e = ue.value.exp();
            if (((type.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e.type.value = t1.nextOf().sarrayOf((long)(elems.get()).length.value);
            }
            else
            {
                e.type.value = type;
            }
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 47) || ((e1.op.value & 0xFF) == 13) && (e1.type.value.toBasetype().nextOf() != null) && e1.type.value.toBasetype().nextOf().equals(e2.type.value))
        {
            Ptr<DArray<Expression>> elems = ((e1.op.value & 0xFF) == 47) ? copyElements(e1, null) : refPtr(new DArray<Expression>());
            (elems.get()).push(e2);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e1.loc.value, null, elems));
            e = ue.value.exp();
            if (((type.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e.type.value = e2.type.value.sarrayOf((long)(elems.get()).length.value);
            }
            else
            {
                e.type.value = type;
            }
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e2.op.value & 0xFF) == 47) && e2.type.value.toBasetype().nextOf().equals(e1.type.value))
        {
            Ptr<DArray<Expression>> elems = copyElements(e1, e2);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e2.loc.value, null, elems));
            e = ue.value.exp();
            if (((type.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e.type.value = e1.type.value.sarrayOf((long)(elems.get()).length.value);
            }
            else
            {
                e.type.value = type;
            }
            assert(ue.value.exp().type.value != null);
            return ue.value;
        }
        else if (((e1.op.value & 0xFF) == 13) && ((e2.op.value & 0xFF) == 121))
        {
            t = e1.type.value;
            e = e2;
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            Type tb = t.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) && tb.nextOf().equivalent(e.type.value))
            {
                Ptr<DArray<Expression>> expressions = refPtr(new DArray<Expression>());
                (expressions.get()).push(e);
                ptr(ue) = new UnionExp(new ArrayLiteralExp(loc, t, expressions));
                e = ue.value.exp();
            }
            else
            {
                ptr(ue) = new UnionExp(new UnionExp(e));
                e = ue.value.exp();
            }
            if (!e.type.value.equals(type))
            {
                StringExp se = (StringExp)e.copy();
                e = se.castTo(null, type);
                ptr(ue) = new UnionExp(new UnionExp(e));
                e = ue.value.exp();
            }
        }
        else if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 13))
        {
            e = e1;
            t = e2.type.value;
        /*L1:*/
            Type tb = t.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) && tb.nextOf().equivalent(e.type.value))
            {
                Ptr<DArray<Expression>> expressions = refPtr(new DArray<Expression>());
                (expressions.get()).push(e);
                ptr(ue) = new UnionExp(new ArrayLiteralExp(loc, t, expressions));
                e = ue.value.exp();
            }
            else
            {
                ptr(ue) = new UnionExp(new UnionExp(e));
                e = ue.value.exp();
            }
            if (!e.type.value.equals(type))
            {
                StringExp se = (StringExp)e.copy();
                e = se.castTo(null, type);
                ptr(ue) = new UnionExp(new UnionExp(e));
                e = ue.value.exp();
            }
        }
        else
        {
            cantExp(ue);
        }
        assert(ue.value.exp().type.value != null);
        return ue.value;
    }

    public static UnionExp Ptr(Type type, Expression e1) {
        Ref<UnionExp> ue = ref(null);
        if (((e1.op.value & 0xFF) == 74))
        {
            AddExp ae = (AddExp)e1;
            if (((ae.e1.value.op.value & 0xFF) == 19) && ((ae.e2.value.op.value & 0xFF) == 135))
            {
                AddrExp ade = (AddrExp)ae.e1.value;
                if (((ade.e1.value.op.value & 0xFF) == 49))
                {
                    StructLiteralExp se = (StructLiteralExp)ade.e1.value;
                    int offset = (int)ae.e2.value.toInteger();
                    Expression e = se.getField(type, offset);
                    if (e != null)
                    {
                        ptr(ue) = new UnionExp(new UnionExp(e));
                        return ue.value;
                    }
                }
            }
        }
        cantExp(ue);
        return ue.value;
    }

}
