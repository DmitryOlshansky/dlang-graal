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
        if ((!pequals(type, e.type)))
        {
            e = e.copy();
            e.type = type;
        }
        return e;
    }

    public static int isConst(Expression e) {
        switch ((e.op & 0xFF))
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
        (ue_ref.value) = new UnionExp(new CTFEExp(TOK.cantExpression));
    }

    public static UnionExp Neg(Type type, Expression e1) {
        UnionExp ue = null;
        Loc loc = e1.loc.copy();
        if (e1.type.isreal())
        {
            ue = new UnionExp(new RealExp(loc, -e1.toReal(), type));
        }
        else if (e1.type.isimaginary())
        {
            ue = new UnionExp(new RealExp(loc, -e1.toImaginary(), type));
        }
        else if (e1.type.iscomplex())
        {
            ue = new UnionExp(new ComplexExp(loc, e1.toComplex().opNeg(), type));
        }
        else
        {
            ue = new UnionExp(new IntegerExp(loc, -e1.toInteger(), type));
        }
        return ue;
    }

    public static UnionExp Com(Type type, Expression e1) {
        UnionExp ue = null;
        Loc loc = e1.loc.copy();
        ue = new UnionExp(new IntegerExp(loc, ~e1.toInteger(), type));
        return ue;
    }

    public static UnionExp Not(Type type, Expression e1) {
        UnionExp ue = null;
        Loc loc = e1.loc.copy();
        ue = new UnionExp(new IntegerExp(loc, e1.isBool(false) ? 1 : 0, type));
        return ue;
    }

    public static UnionExp Bool(Type type, Expression e1) {
        UnionExp ue = null;
        Loc loc = e1.loc.copy();
        ue = new UnionExp(new IntegerExp(loc, e1.isBool(true) ? 1 : 0, type));
        return ue;
    }

    public static UnionExp Add(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        if (type.isreal())
        {
            ue = new UnionExp(new RealExp(loc, e1.toReal() + e2.toReal(), type));
        }
        else if (type.isimaginary())
        {
            ue = new UnionExp(new RealExp(loc, e1.toImaginary() + e2.toImaginary(), type));
        }
        else if (type.iscomplex())
        {
            complex_t c1 = c1 = new complex_t(CTFloat.zero);
            double r1 = CTFloat.zero;
            double i1 = CTFloat.zero;
            complex_t c2 = c2 = new complex_t(CTFloat.zero);
            double r2 = CTFloat.zero;
            double i2 = CTFloat.zero;
            complex_t v = v = new complex_t(CTFloat.zero);
            int x = 0;
            if (e1.type.isreal())
            {
                r1 = e1.toReal();
                x = 0;
            }
            else if (e1.type.isimaginary())
            {
                i1 = e1.toImaginary();
                x = 3;
            }
            else
            {
                c1 = e1.toComplex().copy();
                x = 6;
            }
            if (e2.type.isreal())
            {
                r2 = e2.toReal();
            }
            else if (e2.type.isimaginary())
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
                    v = new complex_t(CTFloat.zero, i1 + i2).copy();
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
            ue = new UnionExp(new ComplexExp(loc, v, type));
        }
        else if (((e1.op & 0xFF) == 25))
        {
            SymOffExp soe = (SymOffExp)e1;
            ue = new UnionExp(new SymOffExp(loc, soe.var, soe.offset + e2.toInteger()));
            ue.exp().type = type;
        }
        else if (((e2.op & 0xFF) == 25))
        {
            SymOffExp soe = (SymOffExp)e2;
            ue = new UnionExp(new SymOffExp(loc, soe.var, soe.offset + e1.toInteger()));
            ue.exp().type = type;
        }
        else
            ue = new UnionExp(new IntegerExp(loc, e1.toInteger() + e2.toInteger(), type));
        return ue;
    }

    public static UnionExp Min(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        if (type.isreal())
        {
            ue = new UnionExp(new RealExp(loc, e1.toReal() - e2.toReal(), type));
        }
        else if (type.isimaginary())
        {
            ue = new UnionExp(new RealExp(loc, e1.toImaginary() - e2.toImaginary(), type));
        }
        else if (type.iscomplex())
        {
            complex_t c1 = c1 = new complex_t(CTFloat.zero);
            double r1 = CTFloat.zero;
            double i1 = CTFloat.zero;
            complex_t c2 = c2 = new complex_t(CTFloat.zero);
            double r2 = CTFloat.zero;
            double i2 = CTFloat.zero;
            complex_t v = v = new complex_t(CTFloat.zero);
            int x = 0;
            if (e1.type.isreal())
            {
                r1 = e1.toReal();
                x = 0;
            }
            else if (e1.type.isimaginary())
            {
                i1 = e1.toImaginary();
                x = 3;
            }
            else
            {
                c1 = e1.toComplex().copy();
                x = 6;
            }
            if (e2.type.isreal())
            {
                r2 = e2.toReal();
            }
            else if (e2.type.isimaginary())
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
                    v = new complex_t(CTFloat.zero, i1 - i2).copy();
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
            ue = new UnionExp(new ComplexExp(loc, v, type));
        }
        else if (((e1.op & 0xFF) == 25))
        {
            SymOffExp soe = (SymOffExp)e1;
            ue = new UnionExp(new SymOffExp(loc, soe.var, soe.offset - e2.toInteger()));
            ue.exp().type = type;
        }
        else
        {
            ue = new UnionExp(new IntegerExp(loc, e1.toInteger() - e2.toInteger(), type));
        }
        return ue;
    }

    public static UnionExp Mul(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        if (type.isfloating())
        {
            complex_t c = c = new complex_t(CTFloat.zero);
            double r = CTFloat.zero;
            if (e1.type.isreal())
            {
                r = e1.toReal();
                c = e2.toComplex().copy();
                c = new complex_t(r * creall(c), r * cimagl(c)).copy();
            }
            else if (e1.type.isimaginary())
            {
                r = e1.toImaginary();
                c = e2.toComplex().copy();
                c = new complex_t(-r * cimagl(c), r * creall(c)).copy();
            }
            else if (e2.type.isreal())
            {
                r = e2.toReal();
                c = e1.toComplex().copy();
                c = new complex_t(r * creall(c), r * cimagl(c)).copy();
            }
            else if (e2.type.isimaginary())
            {
                r = e2.toImaginary();
                c = e1.toComplex().copy();
                c = new complex_t(-r * cimagl(c), r * creall(c)).copy();
            }
            else
                c = e1.toComplex().opMul(e2.toComplex()).copy();
            if (type.isreal())
                ue = new UnionExp(new RealExp(loc, creall(c), type));
            else if (type.isimaginary())
                ue = new UnionExp(new RealExp(loc, cimagl(c), type));
            else if (type.iscomplex())
                ue = new UnionExp(new ComplexExp(loc, c, type));
            else
                throw new AssertionError("Unreachable code!");
        }
        else
        {
            ue = new UnionExp(new IntegerExp(loc, e1.toInteger() * e2.toInteger(), type));
        }
        return ue;
    }

    public static UnionExp Div(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        if (type.isfloating())
        {
            complex_t c = c = new complex_t(CTFloat.zero);
            if (e2.type.isreal())
            {
                if (e1.type.isreal())
                {
                    ue = new UnionExp(new RealExp(loc, e1.toReal() / e2.toReal(), type));
                    return ue;
                }
                double r = e2.toReal();
                c = e1.toComplex().copy();
                c = new complex_t(creall(c) / r, cimagl(c) / r).copy();
            }
            else if (e2.type.isimaginary())
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
                ue = new UnionExp(new RealExp(loc, creall(c), type));
            else if (type.isimaginary())
                ue = new UnionExp(new RealExp(loc, cimagl(c), type));
            else if (type.iscomplex())
                ue = new UnionExp(new ComplexExp(loc, c, type));
            else
                throw new AssertionError("Unreachable code!");
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
                ue = new UnionExp(new ErrorExp());
                return ue;
            }
            if ((n2 == -1L) && !type.isunsigned())
            {
                if (((long)n1 == -2147483648L) && ((type.toBasetype().ty & 0xFF) != ENUMTY.Tint64))
                {
                    e2.error(new BytePtr("integer overflow: `int.min / -1`"));
                    ue = new UnionExp(new ErrorExp());
                    return ue;
                }
                else if (((long)n1 == -9223372036854775808L))
                {
                    e2.error(new BytePtr("integer overflow: `long.min / -1L`"));
                    ue = new UnionExp(new ErrorExp());
                    return ue;
                }
            }
            if (e1.type.isunsigned() || e2.type.isunsigned())
                n = (long)((long)n1 / (long)n2);
            else
                n = n1 / n2;
            ue = new UnionExp(new IntegerExp(loc, n, type));
        }
        return ue;
    }

    public static UnionExp Mod(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        if (type.isfloating())
        {
            complex_t c = c = new complex_t(CTFloat.zero);
            if (e2.type.isreal())
            {
                double r2 = e2.toReal();
                c = new complex_t(e1.toReal() % r2, e1.toImaginary() % r2).copy();
            }
            else if (e2.type.isimaginary())
            {
                double i2 = e2.toImaginary();
                c = new complex_t(e1.toReal() % i2, e1.toImaginary() % i2).copy();
            }
            else
                throw new AssertionError("Unreachable code!");
            if (type.isreal())
                ue = new UnionExp(new RealExp(loc, creall(c), type));
            else if (type.isimaginary())
                ue = new UnionExp(new RealExp(loc, cimagl(c), type));
            else if (type.iscomplex())
                ue = new UnionExp(new ComplexExp(loc, c, type));
            else
                throw new AssertionError("Unreachable code!");
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
                ue = new UnionExp(new ErrorExp());
                return ue;
            }
            if ((n2 == -1L) && !type.isunsigned())
            {
                if (((long)n1 == -2147483648L) && ((type.toBasetype().ty & 0xFF) != ENUMTY.Tint64))
                {
                    e2.error(new BytePtr("integer overflow: `int.min %% -1`"));
                    ue = new UnionExp(new ErrorExp());
                    return ue;
                }
                else if (((long)n1 == -9223372036854775808L))
                {
                    e2.error(new BytePtr("integer overflow: `long.min %% -1L`"));
                    ue = new UnionExp(new ErrorExp());
                    return ue;
                }
            }
            if (e1.type.isunsigned() || e2.type.isunsigned())
                n = (long)((long)n1 % (long)n2);
            else
                n = n1 % n2;
            ue = new UnionExp(new IntegerExp(loc, n, type));
        }
        return ue;
    }

    public static UnionExp Pow(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = new UnionExp().copy();
        if (e2.type.isintegral())
        {
            long n = e2.toInteger();
            boolean neg = false;
            if (!e2.type.isunsigned() && ((long)n < 0L))
            {
                if (e1.type.isintegral())
                {
                    cantExp(ue);
                    return ue;
                }
                neg = true;
                n = -n;
            }
            else
                neg = false;
            UnionExp ur = new UnionExp().copy();
            UnionExp uv = new UnionExp().copy();
            if (e1.type.iscomplex())
            {
                ur = new UnionExp(new ComplexExp(loc, e1.toComplex(), e1.type));
                uv = new UnionExp(new ComplexExp(loc, new complex_t(CTFloat.one), e1.type));
            }
            else if (e1.type.isfloating())
            {
                ur = new UnionExp(new RealExp(loc, e1.toReal(), e1.type));
                uv = new UnionExp(new RealExp(loc, CTFloat.one, e1.type));
            }
            else
            {
                ur = new UnionExp(new IntegerExp(loc, e1.toInteger(), e1.type));
                uv = new UnionExp(new IntegerExp(loc, 1, e1.type));
            }
            Expression r = ur.exp();
            Expression v = uv.exp();
            for (; (n != 0L);){
                if ((n & 1L) != 0)
                {
                    uv = Mul(loc, v.type, v, r).copy();
                }
                n >>= 1;
                ur = Mul(loc, r.type, r, r).copy();
            }
            if (neg)
            {
                UnionExp one = new UnionExp().copy();
                one = new UnionExp(new RealExp(loc, CTFloat.one, v.type));
                uv = Div(loc, v.type, one.exp(), v).copy();
            }
            if (type.iscomplex())
                ue = new UnionExp(new ComplexExp(loc, v.toComplex(), type));
            else if (type.isintegral())
                ue = new UnionExp(new IntegerExp(loc, v.toInteger(), type));
            else
                ue = new UnionExp(new RealExp(loc, v.toReal(), type));
        }
        else if (e2.type.isfloating())
        {
            if ((e1.toReal() < CTFloat.zero))
            {
                ue = new UnionExp(new RealExp(loc, target.RealProperties.nan, type));
            }
            else
                cantExp(ue);
        }
        else
            cantExp(ue);
        return ue;
    }

    public static UnionExp Shl(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        ue = new UnionExp(new IntegerExp(loc, e1.toInteger() << (int)e2.toInteger(), type));
        return ue;
    }

    public static UnionExp Shr(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        long value = e1.toInteger();
        long dcount = e2.toInteger();
        assert((dcount <= 4294967295L));
        int count = (int)dcount;
        switch ((e1.type.toBasetype().ty & 0xFF))
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
                ue = new UnionExp(new ErrorExp());
                return ue;
            default:
            throw new AssertionError("Unreachable code!");
        }
        ue = new UnionExp(new IntegerExp(loc, value, type));
        return ue;
    }

    public static UnionExp Ushr(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        long value = e1.toInteger();
        long dcount = e2.toInteger();
        assert((dcount <= 4294967295L));
        int count = (int)dcount;
        switch ((e1.type.toBasetype().ty & 0xFF))
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
                ue = new UnionExp(new ErrorExp());
                return ue;
            default:
            throw new AssertionError("Unreachable code!");
        }
        ue = new UnionExp(new IntegerExp(loc, value, type));
        return ue;
    }

    public static UnionExp And(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        ue = new UnionExp(new IntegerExp(loc, e1.toInteger() & e2.toInteger(), type));
        return ue;
    }

    public static UnionExp Or(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        ue = new UnionExp(new IntegerExp(loc, e1.toInteger() | e2.toInteger(), type));
        return ue;
    }

    public static UnionExp Xor(Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        ue = new UnionExp(new IntegerExp(loc, e1.toInteger() ^ e2.toInteger(), type));
        return ue;
    }

    public static UnionExp Equal(byte op, Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        int cmp = 0;
        double r1 = CTFloat.zero;
        double r2 = CTFloat.zero;
        assert(((op & 0xFF) == 58) || ((op & 0xFF) == 59));
        if (((e1.op & 0xFF) == 13))
        {
            if (((e2.op & 0xFF) == 13))
                cmp = 1;
            else if (((e2.op & 0xFF) == 121))
            {
                StringExp es2 = (StringExp)e2;
                cmp = ((0 == es2.len) ? 1 : 0);
            }
            else if (((e2.op & 0xFF) == 47))
            {
                ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
                cmp = (((es2.elements == null) || (0 == (es2.elements).length)) ? 1 : 0);
            }
            else
            {
                cantExp(ue);
                return ue;
            }
        }
        else if (((e2.op & 0xFF) == 13))
        {
            if (((e1.op & 0xFF) == 121))
            {
                StringExp es1 = (StringExp)e1;
                cmp = ((0 == es1.len) ? 1 : 0);
            }
            else if (((e1.op & 0xFF) == 47))
            {
                ArrayLiteralExp es1 = (ArrayLiteralExp)e1;
                cmp = (((es1.elements == null) || (0 == (es1.elements).length)) ? 1 : 0);
            }
            else
            {
                cantExp(ue);
                return ue;
            }
        }
        else if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es2 = (StringExp)e2;
            if (((es1.sz & 0xFF) != (es2.sz & 0xFF)))
            {
                assert(global.errors != 0);
                cantExp(ue);
                return ue;
            }
            if ((es1.len == es2.len) && (memcmp(es1.string, es2.string, (es1.sz & 0xFF) * es1.len) == 0))
                cmp = 1;
            else
                cmp = 0;
        }
        else if (((e1.op & 0xFF) == 47) && ((e2.op & 0xFF) == 47))
        {
            ArrayLiteralExp es1 = (ArrayLiteralExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            if ((es1.elements == null) || ((es1.elements).length == 0) && (es2.elements == null) || ((es2.elements).length == 0))
                cmp = 1;
            else if ((es1.elements == null) || (es2.elements == null))
                cmp = 0;
            else if (((es1.elements).length != (es2.elements).length))
                cmp = 0;
            else
            {
                {
                    int i = 0;
                    for (; (i < (es1.elements).length);i++){
                        Expression ee1 = es1.getElement(i);
                        Expression ee2 = es2.getElement(i);
                        ue = Equal(TOK.equal, loc, Type.tint32, ee1, ee2).copy();
                        if (CTFEExp.isCantExp(ue.exp()))
                            return ue;
                        cmp = (int)ue.exp().toInteger();
                        if ((cmp == 0))
                            break;
                    }
                }
            }
        }
        else if (((e1.op & 0xFF) == 47) && ((e2.op & 0xFF) == 121))
        {
            Expression etmp = e1;
            e1 = e2;
            e2 = etmp;
            /*goto Lsa*//*unrolled goto*/
        /*Lsa:*/
            StringExp es1 = (StringExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            int dim1 = es1.len;
            int dim2 = es2.elements != null ? (es2.elements).length : 0;
            if ((dim1 != dim2))
                cmp = 0;
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
                            return ue;
                        }
                        cmp = ((c == ee2.toInteger()) ? 1 : 0);
                        if ((cmp == 0))
                            break;
                    }
                }
            }
        }
        else if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 47))
        {
        /*Lsa:*/
            StringExp es1 = (StringExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            int dim1 = es1.len;
            int dim2 = es2.elements != null ? (es2.elements).length : 0;
            if ((dim1 != dim2))
                cmp = 0;
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
                            return ue;
                        }
                        cmp = ((c == ee2.toInteger()) ? 1 : 0);
                        if ((cmp == 0))
                            break;
                    }
                }
            }
        }
        else if (((e1.op & 0xFF) == 49) && ((e2.op & 0xFF) == 49))
        {
            StructLiteralExp es1 = (StructLiteralExp)e1;
            StructLiteralExp es2 = (StructLiteralExp)e2;
            if ((!pequals(es1.sd, es2.sd)))
                cmp = 0;
            else if ((es1.elements == null) || ((es1.elements).length == 0) && (es2.elements == null) || ((es2.elements).length == 0))
                cmp = 1;
            else if ((es1.elements == null) || (es2.elements == null))
                cmp = 0;
            else if (((es1.elements).length != (es2.elements).length))
                cmp = 0;
            else
            {
                cmp = 1;
                {
                    int i = 0;
                    for (; (i < (es1.elements).length);i++){
                        Expression ee1 = (es1.elements).get(i);
                        Expression ee2 = (es2.elements).get(i);
                        if ((pequals(ee1, ee2)))
                            continue;
                        if ((ee1 == null) || (ee2 == null))
                        {
                            cmp = 0;
                            break;
                        }
                        ue = Equal(TOK.equal, loc, Type.tint32, ee1, ee2).copy();
                        if (((ue.exp().op & 0xFF) == 233))
                            return ue;
                        cmp = (int)ue.exp().toInteger();
                        if ((cmp == 0))
                            break;
                    }
                }
            }
        }
        else if ((e1.isConst() != 1) || (e2.isConst() != 1))
        {
            cantExp(ue);
            return ue;
        }
        else if (e1.type.isreal())
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
        else if (e1.type.isimaginary())
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
        else if (e1.type.iscomplex())
        {
            cmp = e1.toComplex().opEquals(e2.toComplex());
        }
        else if (e1.type.isintegral() || ((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
        {
            cmp = ((e1.toInteger() == e2.toInteger()) ? 1 : 0);
        }
        else
        {
            cantExp(ue);
            return ue;
        }
        if (((op & 0xFF) == 59))
            cmp ^= 1;
        ue = new UnionExp(new IntegerExp(loc, cmp, type));
        return ue;
    }

    public static UnionExp Identity(byte op, Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        int cmp = 0;
        if (((e1.op & 0xFF) == 13))
        {
            cmp = (((e2.op & 0xFF) == 13) ? 1 : 0);
        }
        else if (((e2.op & 0xFF) == 13))
        {
            cmp = 0;
        }
        else if (((e1.op & 0xFF) == 25) && ((e2.op & 0xFF) == 25))
        {
            SymOffExp es1 = (SymOffExp)e1;
            SymOffExp es2 = (SymOffExp)e2;
            cmp = (((pequals(es1.var, es2.var)) && (es1.offset == es2.offset)) ? 1 : 0);
        }
        else
        {
            if (e1.type.isreal())
            {
                cmp = RealIdentical(e1.toReal(), e2.toReal());
            }
            else if (e1.type.isimaginary())
            {
                cmp = RealIdentical(e1.toImaginary(), e2.toImaginary());
            }
            else if (e1.type.iscomplex())
            {
                complex_t v1 = e1.toComplex().copy();
                complex_t v2 = e2.toComplex().copy();
                cmp = (((RealIdentical(creall(v1), creall(v2)) != 0) && (RealIdentical(cimagl(v1), cimagl(v1)) != 0)) ? 1 : 0);
            }
            else
            {
                ue = Equal(((op & 0xFF) == 60) ? TOK.equal : TOK.notEqual, loc, type, e1, e2).copy();
                return ue;
            }
        }
        if (((op & 0xFF) == 61))
            cmp ^= 1;
        ue = new UnionExp(new IntegerExp(loc, cmp, type));
        return ue;
    }

    public static UnionExp Cmp(byte op, Loc loc, Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        long n = 0L;
        double r1 = CTFloat.zero;
        double r2 = CTFloat.zero;
        if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es2 = (StringExp)e2;
            int sz = (es1.sz & 0xFF);
            assert((sz == (es2.sz & 0xFF)));
            int len = es1.len;
            if ((es2.len < len))
                len = es2.len;
            int rawCmp = memcmp(es1.string, es2.string, sz * len);
            if ((rawCmp == 0))
                rawCmp = (es1.len - es2.len);
            n = (long)specificCmp(op, rawCmp);
        }
        else if ((e1.isConst() != 1) || (e2.isConst() != 1))
        {
            cantExp(ue);
            return ue;
        }
        else if (e1.type.isreal())
        {
            r1 = e1.toReal();
            r2 = e2.toReal();
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            n = (long)realCmp(op, r1, r2);
        }
        else if (e1.type.isimaginary())
        {
            r1 = e1.toImaginary();
            r2 = e2.toImaginary();
        /*L1:*/
            n = (long)realCmp(op, r1, r2);
        }
        else if (e1.type.iscomplex())
        {
            throw new AssertionError("Unreachable code!");
        }
        else
        {
            long n1 = 0L;
            long n2 = 0L;
            n1 = (long)e1.toInteger();
            n2 = (long)e2.toInteger();
            if (e1.type.isunsigned() || e2.type.isunsigned())
                n = (long)intUnsignedCmp(op, (long)n1, (long)n2);
            else
                n = (long)intSignedCmp(op, n1, n2);
        }
        ue = new UnionExp(new IntegerExp(loc, n, type));
        return ue;
    }

    public static UnionExp Cast(Loc loc, Type type, Type to, Expression e1) {
        UnionExp ue = null;
        Type tb = to.toBasetype();
        Type typeb = type.toBasetype();
        if (e1.type.equals(type) && type.equals(to))
        {
            ue = new UnionExp(new UnionExp(e1));
            return ue;
        }
        if (((e1.op & 0xFF) == 229) && ((TypeVector)e1.type).basetype.equals(type) && type.equals(to))
        {
            Expression ex = ((VectorExp)e1).e1;
            ue = new UnionExp(new UnionExp(ex));
            return ue;
        }
        if ((e1.type.implicitConvTo(to) >= MATCH.constant) || (to.implicitConvTo(e1.type) >= MATCH.constant))
        {
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            Expression ex = expType(to, e1);
            ue = new UnionExp(new UnionExp(ex));
            return ue;
        }
        if (((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tdelegate) && (e1.type.implicitConvTo(to) == MATCH.convert))
        {
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            Expression ex = expType(to, e1);
            ue = new UnionExp(new UnionExp(ex));
            return ue;
        }
        if (((e1.op & 0xFF) == 121))
        {
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) && ((typeb.ty & 0xFF) == ENUMTY.Tarray) && (tb.nextOf().size() == typeb.nextOf().size()))
            {
                /*goto L1*//*unrolled goto*/
            /*L1:*/
                Expression ex = expType(to, e1);
                ue = new UnionExp(new UnionExp(ex));
                return ue;
            }
        }
        if (((e1.op & 0xFF) == 47) && (pequals(typeb, tb)))
        {
        /*L1:*/
            Expression ex = expType(to, e1);
            ue = new UnionExp(new UnionExp(ex));
            return ue;
        }
        if ((e1.isConst() != 1))
        {
            cantExp(ue);
        }
        else if (((tb.ty & 0xFF) == ENUMTY.Tbool))
        {
            ue = new UnionExp(new IntegerExp(loc, e1.toInteger() != 0L, type));
        }
        else if (type.isintegral())
        {
            if (e1.type.isfloating())
            {
                long result = 0L;
                double r = e1.toReal();
                switch ((typeb.ty & 0xFF))
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
                ue = new UnionExp(new IntegerExp(loc, result, type));
            }
            else if (type.isunsigned())
                ue = new UnionExp(new IntegerExp(loc, e1.toUInteger(), type));
            else
                ue = new UnionExp(new IntegerExp(loc, e1.toInteger(), type));
        }
        else if (tb.isreal())
        {
            double value = e1.toReal();
            ue = new UnionExp(new RealExp(loc, value, type));
        }
        else if (tb.isimaginary())
        {
            double value = e1.toImaginary();
            ue = new UnionExp(new RealExp(loc, value, type));
        }
        else if (tb.iscomplex())
        {
            complex_t value = e1.toComplex().copy();
            ue = new UnionExp(new ComplexExp(loc, value, type));
        }
        else if (tb.isscalar())
        {
            ue = new UnionExp(new IntegerExp(loc, e1.toInteger(), type));
        }
        else if (((tb.ty & 0xFF) == ENUMTY.Tvoid))
        {
            cantExp(ue);
        }
        else if (((tb.ty & 0xFF) == ENUMTY.Tstruct) && ((e1.op & 0xFF) == 135))
        {
            StructDeclaration sd = tb.toDsymbol(null).isStructDeclaration();
            assert(sd != null);
            DArray<Expression> elements = new DArray<Expression>();
            {
                int i = 0;
                for (; (i < sd.fields.length);i++){
                    VarDeclaration v = sd.fields.get(i);
                    UnionExp zero = new UnionExp().copy();
                    zero = new UnionExp(new IntegerExp(0));
                    ue = Cast(loc, v.type, v.type, zero.exp()).copy();
                    if (((ue.exp().op & 0xFF) == 233))
                        return ue;
                    (elements).push(ue.exp().copy());
                }
            }
            ue = new UnionExp(new StructLiteralExp(loc, sd, elements));
            ue.exp().type = type;
        }
        else
        {
            if ((!pequals(type, Type.terror)))
            {
                error(loc, new BytePtr("cannot cast `%s` to `%s`"), e1.type.toChars(), type.toChars());
            }
            ue = new UnionExp(new ErrorExp());
        }
        return ue;
    }

    public static UnionExp ArrayLength(Type type, Expression e1) {
        UnionExp ue = null;
        Loc loc = e1.loc.copy();
        if (((e1.op & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            ue = new UnionExp(new IntegerExp(loc, es1.len, type));
        }
        else if (((e1.op & 0xFF) == 47))
        {
            ArrayLiteralExp ale = (ArrayLiteralExp)e1;
            int dim = ale.elements != null ? (ale.elements).length : 0;
            ue = new UnionExp(new IntegerExp(loc, dim, type));
        }
        else if (((e1.op & 0xFF) == 48))
        {
            AssocArrayLiteralExp ale = (AssocArrayLiteralExp)e1;
            int dim = (ale.keys).length;
            ue = new UnionExp(new IntegerExp(loc, dim, type));
        }
        else if (((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
        {
            Expression e = ((TypeSArray)e1.type.toBasetype()).dim;
            ue = new UnionExp(new UnionExp(e));
        }
        else
            cantExp(ue);
        return ue;
    }

    public static UnionExp Index(Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        Loc loc = e1.loc.copy();
        assert(e1.type != null);
        if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 135))
        {
            StringExp es1 = (StringExp)e1;
            long i = e2.toInteger();
            if ((i >= (long)es1.len))
            {
                e1.error(new BytePtr("string index %llu is out of bounds `[0 .. %llu]`"), i, (long)es1.len);
                ue = new UnionExp(new ErrorExp());
            }
            else
            {
                ue = new UnionExp(new IntegerExp(loc, es1.charAt(i), type));
            }
        }
        else if (((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) && ((e2.op & 0xFF) == 135))
        {
            TypeSArray tsa = (TypeSArray)e1.type.toBasetype();
            long length = tsa.dim.toInteger();
            long i = e2.toInteger();
            if ((i >= length))
            {
                e1.error(new BytePtr("array index %llu is out of bounds `%s[0 .. %llu]`"), i, e1.toChars(), length);
                ue = new UnionExp(new ErrorExp());
            }
            else if (((e1.op & 0xFF) == 47))
            {
                ArrayLiteralExp ale = (ArrayLiteralExp)e1;
                Expression e = ale.getElement((int)i);
                e.type = type;
                e.loc = loc.copy();
                if (hasSideEffect(e))
                    cantExp(ue);
                else
                    ue = new UnionExp(new UnionExp(e));
            }
            else
                cantExp(ue);
        }
        else if (((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray) && ((e2.op & 0xFF) == 135))
        {
            long i = e2.toInteger();
            if (((e1.op & 0xFF) == 47))
            {
                ArrayLiteralExp ale = (ArrayLiteralExp)e1;
                if ((i >= (long)(ale.elements).length))
                {
                    e1.error(new BytePtr("array index %llu is out of bounds `%s[0 .. %u]`"), i, e1.toChars(), (ale.elements).length);
                    ue = new UnionExp(new ErrorExp());
                }
                else
                {
                    Expression e = ale.getElement((int)i);
                    e.type = type;
                    e.loc = loc.copy();
                    if (hasSideEffect(e))
                        cantExp(ue);
                    else
                        ue = new UnionExp(new UnionExp(e));
                }
            }
            else
                cantExp(ue);
        }
        else if (((e1.op & 0xFF) == 48))
        {
            AssocArrayLiteralExp ae = (AssocArrayLiteralExp)e1;
            {
                int i = (ae.keys).length;
                for (; i != 0;){
                    i--;
                    Expression ekey = (ae.keys).get(i);
                    ue = Equal(TOK.equal, loc, Type.tbool, ekey, e2).copy();
                    if (CTFEExp.isCantExp(ue.exp()))
                        return ue;
                    if (ue.exp().isBool(true))
                    {
                        Expression e = (ae.values).get(i);
                        e.type = type;
                        e.loc = loc.copy();
                        if (hasSideEffect(e))
                            cantExp(ue);
                        else
                            ue = new UnionExp(new UnionExp(e));
                        return ue;
                    }
                }
            }
            cantExp(ue);
        }
        else
            cantExp(ue);
        return ue;
    }

    public static UnionExp Slice(Type type, Expression e1, Expression lwr, Expression upr) {
        UnionExp ue = null;
        Loc loc = e1.loc.copy();
        Function4<Long,Long,Long,Long,Boolean> sliceBoundsCheck = new Function4<Long,Long,Long,Long,Boolean>(){
            public Boolean invoke(Long lwr, Long upr, Long newlwr, Long newupr) {
                assert((lwr <= upr));
                return !((newlwr <= newupr) && (lwr <= newlwr) && (newupr <= upr));
            }
        };
        if (((e1.op & 0xFF) == 121) && ((lwr.op & 0xFF) == 135) && ((upr.op & 0xFF) == 135))
        {
            StringExp es1 = (StringExp)e1;
            long ilwr = lwr.toInteger();
            long iupr = upr.toInteger();
            if (sliceBoundsCheck.invoke(0L, (long)es1.len, ilwr, iupr))
                cantExp(ue);
            else
            {
                int len = (int)(iupr - ilwr);
                byte sz = es1.sz;
                Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
                memcpy((BytePtr)s, ((es1.string.plus((int)(ilwr * (long)(sz & 0xFF))))), (len * (sz & 0xFF)));
                ue = new UnionExp(new StringExp(loc, s, len, es1.postfix));
                StringExp es = (StringExp)ue.exp();
                es.sz = sz;
                es.committed = es1.committed;
                es.type = type;
            }
        }
        else if (((e1.op & 0xFF) == 47) && ((lwr.op & 0xFF) == 135) && ((upr.op & 0xFF) == 135) && !hasSideEffect(e1))
        {
            ArrayLiteralExp es1 = (ArrayLiteralExp)e1;
            long ilwr = lwr.toInteger();
            long iupr = upr.toInteger();
            if (sliceBoundsCheck.invoke(0L, (long)(es1.elements).length, ilwr, iupr))
                cantExp(ue);
            else
            {
                DArray<Expression> elements = new DArray<Expression>((int)(iupr - ilwr));
                memcpy((BytePtr)((elements).tdata()), (((es1.elements).tdata().plus((int)ilwr * 4))), ((int)(iupr - ilwr) * 4));
                ue = new UnionExp(new ArrayLiteralExp(e1.loc, type, elements));
            }
        }
        else
            cantExp(ue);
        return ue;
    }

    public static void sliceAssignArrayLiteralFromString(ArrayLiteralExp existingAE, StringExp newval, int firstIndex) {
        int len = newval.len;
        Type elemType = existingAE.type.nextOf();
        {
            int __key844 = 0;
            int __limit845 = len;
            for (; (__key844 < __limit845);__key844 += 1) {
                int j = __key844;
                int val = newval.getCodeUnit(j);
                existingAE.elements.set(j + firstIndex, new IntegerExp(newval.loc, (long)val, elemType));
            }
        }
    }

    public static void sliceAssignStringFromArrayLiteral(StringExp existingSE, ArrayLiteralExp newae, int firstIndex) {
        assert(((existingSE.ownedByCtfe & 0xFF) != 0));
        {
            int __key846 = 0;
            int __limit847 = (newae.elements).length;
            for (; (__key846 < __limit847);__key846 += 1) {
                int j = __key846;
                existingSE.setCodeUnit(firstIndex + j, (int)newae.getElement(j).toInteger());
            }
        }
    }

    public static void sliceAssignStringFromString(StringExp existingSE, StringExp newstr, int firstIndex) {
        assert(((existingSE.ownedByCtfe & 0xFF) != 0));
        int sz = (existingSE.sz & 0xFF);
        assert((sz == (newstr.sz & 0xFF)));
        memcpy((BytePtr)((existingSE.string.plus((firstIndex * sz)))), (newstr.string), (sz * newstr.len));
    }

    public static int sliceCmpStringWithString(StringExp se1, StringExp se2, int lo1, int lo2, int len) {
        int sz = (se1.sz & 0xFF);
        assert((sz == (se2.sz & 0xFF)));
        return memcmp((se1.string.plus((sz * lo1))), (se2.string.plus((sz * lo2))), sz * len);
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
                    return c;
            }
        }
        return 0;
    }

    public static DArray<Expression> copyElements(Expression e1, Expression e2) {
        Ref<DArray<Expression>> elems = ref(new DArray<Expression>());
        Function1<ArrayLiteralExp,Void> append = new Function1<ArrayLiteralExp,Void>(){
            public Void invoke(ArrayLiteralExp ale) {
                if (ale.elements == null)
                    return null;
                int d = (elems.value).length;
                (elems.value).append(ale.elements);
                {
                    Slice<Expression> __r850 = (elems.value).opSlice(d, (elems.value).length).copy();
                    int __key851 = 0;
                    for (; (__key851 < __r850.getLength());__key851 += 1) {
                        Expression el = __r850.get(__key851);
                        if (el == null)
                            el = ale.basis;
                    }
                }
            }
        };
        if (((e1.op & 0xFF) == 47))
            append.invoke((ArrayLiteralExp)e1);
        else
            (elems.value).push(e1);
        if (e2 != null)
        {
            if (((e2.op & 0xFF) == 47))
                append.invoke((ArrayLiteralExp)e2);
            else
                (elems.value).push(e2);
        }
        return elems.value;
    }

    // defaulted all parameters starting with #2
    public static DArray<Expression> copyElements(Expression e1) {
        copyElements(e1, null);
    }

    public static UnionExp Cat(Type type, Expression e1, Expression e2) {
        UnionExp ue = null;
        Expression e = CTFEExp.cantexp;
        Loc loc = e1.loc.copy();
        Type t = null;
        Type t1 = e1.type.toBasetype();
        Type t2 = e2.type.toBasetype();
        if (((e1.op & 0xFF) == 13) && ((e2.op & 0xFF) == 135) || ((e2.op & 0xFF) == 49))
        {
            e = e2;
            t = t1;
            /*goto L2*//*unrolled goto*/
        /*L2:*/
            Type tn = e.type.toBasetype();
            if (((tn.ty & 0xFF) == ENUMTY.Tchar) || ((tn.ty & 0xFF) == ENUMTY.Twchar) || ((tn.ty & 0xFF) == ENUMTY.Tdchar))
            {
                if (t.nextOf() != null)
                    t = t.nextOf().toBasetype();
                byte sz = (byte)t.size();
                long v = e.toInteger();
                int len = ((t.ty & 0xFF) == (tn.ty & 0xFF)) ? 1 : utf_codeLength((sz & 0xFF), (int)v);
                Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
                if (((t.ty & 0xFF) == (tn.ty & 0xFF)))
                    Port.valcpy(s, v, (sz & 0xFF));
                else
                    utf_encode((sz & 0xFF), s, (int)v);
                ue = new UnionExp(new StringExp(loc, s, len));
                StringExp es = (StringExp)ue.exp();
                es.type = type;
                es.sz = sz;
                es.committed = (byte)1;
            }
            else
            {
                DArray<Expression> elements = new DArray<Expression>();
                (elements).push(e);
                ue = new UnionExp(new ArrayLiteralExp(e.loc, type, elements));
            }
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 135) || ((e1.op & 0xFF) == 49) && ((e2.op & 0xFF) == 13))
        {
            e = e1;
            t = t2;
        /*L2:*/
            Type tn = e.type.toBasetype();
            if (((tn.ty & 0xFF) == ENUMTY.Tchar) || ((tn.ty & 0xFF) == ENUMTY.Twchar) || ((tn.ty & 0xFF) == ENUMTY.Tdchar))
            {
                if (t.nextOf() != null)
                    t = t.nextOf().toBasetype();
                byte sz = (byte)t.size();
                long v = e.toInteger();
                int len = ((t.ty & 0xFF) == (tn.ty & 0xFF)) ? 1 : utf_codeLength((sz & 0xFF), (int)v);
                Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
                if (((t.ty & 0xFF) == (tn.ty & 0xFF)))
                    Port.valcpy(s, v, (sz & 0xFF));
                else
                    utf_encode((sz & 0xFF), s, (int)v);
                ue = new UnionExp(new StringExp(loc, s, len));
                StringExp es = (StringExp)ue.exp();
                es.type = type;
                es.sz = sz;
                es.committed = (byte)1;
            }
            else
            {
                DArray<Expression> elements = new DArray<Expression>();
                (elements).push(e);
                ue = new UnionExp(new ArrayLiteralExp(e.loc, type, elements));
            }
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 13) && ((e2.op & 0xFF) == 13))
        {
            if ((pequals(type, e1.type)))
            {
                if (((t1.ty & 0xFF) == ENUMTY.Tarray) && (pequals(t2, t1.nextOf())))
                {
                    ue = new UnionExp(new ArrayLiteralExp(e1.loc, type, e2));
                    assert(ue.exp().type != null);
                    return ue;
                }
                else
                {
                    ue = new UnionExp(new UnionExp(e1));
                    assert(ue.exp().type != null);
                    return ue;
                }
            }
            if ((pequals(type, e2.type)))
            {
                ue = new UnionExp(new UnionExp(e2));
                assert(ue.exp().type != null);
                return ue;
            }
            ue = new UnionExp(new NullExp(e1.loc, type));
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es2 = (StringExp)e2;
            int len = es1.len + es2.len;
            byte sz = es1.sz;
            if (((sz & 0xFF) != (es2.sz & 0xFF)))
            {
                assert(global.errors != 0);
                cantExp(ue);
                assert(ue.exp().type != null);
                return ue;
            }
            Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
            memcpy((BytePtr)(((BytePtr)s)), (es1.string), (es1.len * (sz & 0xFF)));
            memcpy((BytePtr)((((BytePtr)s).plus((es1.len * (sz & 0xFF))))), (es2.string), (es2.len * (sz & 0xFF)));
            ue = new UnionExp(new StringExp(loc, s, len));
            StringExp es = (StringExp)ue.exp();
            es.sz = sz;
            es.committed = (byte)((es1.committed & 0xFF) | (es2.committed & 0xFF));
            es.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e2.op & 0xFF) == 121) && ((e1.op & 0xFF) == 47) && t1.nextOf().isintegral())
        {
            StringExp es = (StringExp)e2;
            ArrayLiteralExp ea = (ArrayLiteralExp)e1;
            int len = es.len + (ea.elements).length;
            DArray<Expression> elems = new DArray<Expression>(len);
            {
                int i = 0;
                for (; (i < (ea.elements).length);i += 1){
                    elems.set(i, ea.getElement(i));
                }
            }
            ue = new UnionExp(new ArrayLiteralExp(e1.loc, type, elems));
            ArrayLiteralExp dest = (ArrayLiteralExp)ue.exp();
            sliceAssignArrayLiteralFromString(dest, es, (ea.elements).length);
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 47) && t2.nextOf().isintegral())
        {
            StringExp es = (StringExp)e1;
            ArrayLiteralExp ea = (ArrayLiteralExp)e2;
            int len = es.len + (ea.elements).length;
            DArray<Expression> elems = new DArray<Expression>(len);
            {
                int i = 0;
                for (; (i < (ea.elements).length);i += 1){
                    elems.set(es.len + i, ea.getElement(i));
                }
            }
            ue = new UnionExp(new ArrayLiteralExp(e1.loc, type, elems));
            ArrayLiteralExp dest = (ArrayLiteralExp)ue.exp();
            sliceAssignArrayLiteralFromString(dest, es, 0);
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 135))
        {
            StringExp es1 = (StringExp)e1;
            StringExp es = null;
            byte sz = es1.sz;
            long v = e2.toInteger();
            boolean homoConcat = (long)(sz & 0xFF) == t2.size();
            int len = es1.len;
            len += homoConcat ? 1 : utf_codeLength((sz & 0xFF), (int)v);
            Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
            memcpy((BytePtr)s, (es1.string), (es1.len * (sz & 0xFF)));
            if (homoConcat)
                Port.valcpy((((BytePtr)s).plus(((sz & 0xFF) * es1.len))), v, (sz & 0xFF));
            else
                utf_encode((sz & 0xFF), (((BytePtr)s).plus(((sz & 0xFF) * es1.len))), (int)v);
            ue = new UnionExp(new StringExp(loc, s, len));
            es = (StringExp)ue.exp();
            es.sz = sz;
            es.committed = es1.committed;
            es.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 135) && ((e2.op & 0xFF) == 121))
        {
            StringExp es2 = (StringExp)e2;
            int len = 1 + es2.len;
            byte sz = es2.sz;
            long v = e1.toInteger();
            Object s = pcopy(Mem.xmalloc(len * (sz & 0xFF)));
            Port.valcpy(((BytePtr)s), v, (sz & 0xFF));
            memcpy((BytePtr)((((BytePtr)s).plus((sz & 0xFF)))), (es2.string), (es2.len * (sz & 0xFF)));
            ue = new UnionExp(new StringExp(loc, s, len));
            StringExp es = (StringExp)ue.exp();
            es.sz = sz;
            es.committed = es2.committed;
            es.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 47) && ((e2.op & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            DArray<Expression> elems = copyElements(e1, e2);
            ue = new UnionExp(new ArrayLiteralExp(e1.loc, null, elems));
            e = ue.exp();
            if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                e.type = t1.nextOf().sarrayOf((long)(elems).length);
            }
            else
                e.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 47) && ((e2.op & 0xFF) == 13) && t1.nextOf().equals(t2.nextOf()))
        {
            e = e1;
            /*goto L3*//*unrolled goto*/
        /*L3:*/
            DArray<Expression> elems = copyElements(e, null);
            ue = new UnionExp(new ArrayLiteralExp(e.loc, null, elems));
            e = ue.exp();
            if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                e.type = t1.nextOf().sarrayOf((long)(elems).length);
            }
            else
                e.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 13) && ((e2.op & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            e = e2;
        /*L3:*/
            DArray<Expression> elems = copyElements(e, null);
            ue = new UnionExp(new ArrayLiteralExp(e.loc, null, elems));
            e = ue.exp();
            if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                e.type = t1.nextOf().sarrayOf((long)(elems).length);
            }
            else
                e.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 47) || ((e1.op & 0xFF) == 13) && (e1.type.toBasetype().nextOf() != null) && e1.type.toBasetype().nextOf().equals(e2.type))
        {
            DArray<Expression> elems = ((e1.op & 0xFF) == 47) ? copyElements(e1, null) : new DArray<Expression>();
            (elems).push(e2);
            ue = new UnionExp(new ArrayLiteralExp(e1.loc, null, elems));
            e = ue.exp();
            if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                e.type = e2.type.sarrayOf((long)(elems).length);
            }
            else
                e.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e2.op & 0xFF) == 47) && e2.type.toBasetype().nextOf().equals(e1.type))
        {
            DArray<Expression> elems = copyElements(e1, e2);
            ue = new UnionExp(new ArrayLiteralExp(e2.loc, null, elems));
            e = ue.exp();
            if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                e.type = e1.type.sarrayOf((long)(elems).length);
            }
            else
                e.type = type;
            assert(ue.exp().type != null);
            return ue;
        }
        else if (((e1.op & 0xFF) == 13) && ((e2.op & 0xFF) == 121))
        {
            t = e1.type;
            e = e2;
            /*goto L1*//*unrolled goto*/
        /*L1:*/
            Type tb = t.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) && tb.nextOf().equivalent(e.type))
            {
                DArray<Expression> expressions = new DArray<Expression>();
                (expressions).push(e);
                ue = new UnionExp(new ArrayLiteralExp(loc, t, expressions));
                e = ue.exp();
            }
            else
            {
                ue = new UnionExp(new UnionExp(e));
                e = ue.exp();
            }
            if (!e.type.equals(type))
            {
                StringExp se = (StringExp)e.copy();
                e = se.castTo(null, type);
                ue = new UnionExp(new UnionExp(e));
                e = ue.exp();
            }
        }
        else if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 13))
        {
            e = e1;
            t = e2.type;
        /*L1:*/
            Type tb = t.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) && tb.nextOf().equivalent(e.type))
            {
                DArray<Expression> expressions = new DArray<Expression>();
                (expressions).push(e);
                ue = new UnionExp(new ArrayLiteralExp(loc, t, expressions));
                e = ue.exp();
            }
            else
            {
                ue = new UnionExp(new UnionExp(e));
                e = ue.exp();
            }
            if (!e.type.equals(type))
            {
                StringExp se = (StringExp)e.copy();
                e = se.castTo(null, type);
                ue = new UnionExp(new UnionExp(e));
                e = ue.exp();
            }
        }
        else
            cantExp(ue);
        assert(ue.exp().type != null);
        return ue;
    }

    public static UnionExp Ptr(Type type, Expression e1) {
        UnionExp ue = null;
        if (((e1.op & 0xFF) == 74))
        {
            AddExp ae = (AddExp)e1;
            if (((ae.e1.op & 0xFF) == 19) && ((ae.e2.op & 0xFF) == 135))
            {
                AddrExp ade = (AddrExp)ae.e1;
                if (((ade.e1.op & 0xFF) == 49))
                {
                    StructLiteralExp se = (StructLiteralExp)ade.e1;
                    int offset = (int)ae.e2.toInteger();
                    Expression e = se.getField(type, offset);
                    if (e != null)
                    {
                        ue = new UnionExp(new UnionExp(e));
                        return ue;
                    }
                }
            }
        }
        cantExp(ue);
        return ue;
    }

}
