package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.mtype.*;

public class intrange {

    public static long copySign(long x, boolean sign) {
        return x - (sign ? 1 : 0) ^ -(sign ? 1 : 0);
    }

    public static class SignExtendedNumber
    {
        public long value;
        public boolean negative;
        public static SignExtendedNumber fromInteger(long value_) {
            return new SignExtendedNumber(value_, (boolean)(value_ >> 63));
        }

        public static SignExtendedNumber extreme(boolean minimum) {
            return new SignExtendedNumber((long)((minimum ? 1 : 0) - 1), minimum);
        }

        public static SignExtendedNumber max() {
            return new SignExtendedNumber(-1L, false);
        }

        public static SignExtendedNumber min() {
            return new SignExtendedNumber(0L, true);
        }

        public  boolean isMinimum() {
            return (this.negative && this.value == 0L);
        }

        public  boolean opEquals(SignExtendedNumber a) {
            return (this.value == a.value && (this.negative ? 1 : 0) == (a.negative ? 1 : 0));
        }

        public  int opCmp(SignExtendedNumber a) {
            if ((this.negative ? 1 : 0) != (a.negative ? 1 : 0))
            {
                if (this.negative)
                    return -1;
                else
                    return 1;
            }
            if (this.value < a.value)
                return -1;
            else if (this.value > a.value)
                return 1;
            else
                return 0;
        }

        // from template opUnary!(_~)
        public  SignExtendedNumber opUnary_~() {
            if (~this.value == 0L)
                return new SignExtendedNumber(~this.value, false);
            else
                return new SignExtendedNumber(~this.value, !(this.negative));
        }


        // from template opUnary!(_-)
        public  SignExtendedNumber opUnary_-() {
            if (this.value == 0L)
                return new SignExtendedNumber(-(this.negative ? 1 : 0), false);
            else
                return new SignExtendedNumber(-this.value, !(this.negative));
        }


        // from template opBinary!(_+)
        public  SignExtendedNumber opBinary_+(SignExtendedNumber rhs) {
            long sum = this.value + rhs.value;
            boolean carry = (sum < this.value && sum < rhs.value);
            if ((this.negative ? 1 : 0) != (rhs.negative ? 1 : 0))
                return new SignExtendedNumber(sum, !(carry));
            else if (this.negative)
                return new SignExtendedNumber(carry ? sum : 0L, true);
            else
                return new SignExtendedNumber(carry ? -1L : sum, false);
        }


        // from template opBinary!(_-)
        public  SignExtendedNumber opBinary_-(SignExtendedNumber rhs) {
            if (rhs.isMinimum())
                return this.negative ? new SignExtendedNumber(this.value, false) : max();
            else
                return this.opBinary_+(rhs.opUnary_-());
        }


        public SignExtendedNumber(){
        }
        public SignExtendedNumber copy(){
            SignExtendedNumber r = new SignExtendedNumber();
            r.value = value;
            r.negative = negative;
            return r;
        }
        public SignExtendedNumber(long value, boolean negative) {
            this.value = value;
            this.negative = negative;
        }

        public SignExtendedNumber opAssign(SignExtendedNumber that) {
            this.value = that.value;
            this.negative = that.negative;
            return this;
        }
    }
    public static class IntRange
    {
        public SignExtendedNumber imin = new SignExtendedNumber();
        public SignExtendedNumber imax = new SignExtendedNumber();
        public  IntRange(IntRange another) {
            this.imin = another.imin.copy();
            this.imax = another.imax.copy();
        }

        public  IntRange(SignExtendedNumber a) {
            this.imin = a.copy();
            this.imax = a.copy();
        }

        public  IntRange(SignExtendedNumber lower, SignExtendedNumber upper) {
            this.imin = lower.copy();
            this.imax = upper.copy();
        }

        public static IntRange fromType(Type type) {
            return fromType(type, type.isunsigned());
        }

        public static IntRange fromType(Type type, boolean isUnsigned) {
            if ((!(type.isintegral()) || (type.toBasetype().ty & 0xFF) == ENUMTY.Tvector))
                return widest();
            long mask = type.sizemask();
            SignExtendedNumber lower = new SignExtendedNumber(0L, false).copy();
            SignExtendedNumber upper = new SignExtendedNumber(mask, false).copy();
            if ((type.toBasetype().ty & 0xFF) == ENUMTY.Tdchar)
                upper.value = 1114111L;
            else if (!(isUnsigned))
            {
                lower.value = ~(mask >> 1);
                lower.negative = true;
                upper.value = mask >> 1;
            }
            return new IntRange(lower, upper);
        }

        public static IntRange fromNumbers2(SignExtendedNumber numbers) {
            if (numbers.get(0).opCmp(numbers.get(1)) < 0)
                return new IntRange(numbers.get(0), numbers.get(1));
            else
                return new IntRange(numbers.get(1), numbers.get(0));
        }

        public static IntRange fromNumbers4(SignExtendedNumber numbers) {
            IntRange ab = fromNumbers2(numbers).copy();
            IntRange cd = fromNumbers2(numbers.plus(24)).copy();
            if (cd.imin.opCmp(ab.imin) < 0)
                ab.imin = cd.imin.copy();
            if (cd.imax.opCmp(ab.imax) > 0)
                ab.imax = cd.imax.copy();
            return ab;
        }

        public static IntRange widest() {
            return new IntRange(SignExtendedNumber.min(), SignExtendedNumber.max());
        }

        public  IntRange castSigned(long mask) {
            long halfChunkMask = mask >> 1;
            long minHalfChunk = this.imin.value & ~halfChunkMask;
            long maxHalfChunk = this.imax.value & ~halfChunkMask;
            int minHalfChunkNegativity = (this.imin.negative ? 1 : 0);
            int maxHalfChunkNegativity = (this.imax.negative ? 1 : 0);
            if ((minHalfChunk & mask) != 0)
            {
                minHalfChunk += halfChunkMask + 1L;
                if (minHalfChunk == 0L)
                    minHalfChunkNegativity -= 1;
            }
            if ((maxHalfChunk & mask) != 0)
            {
                maxHalfChunk += halfChunkMask + 1L;
                if (maxHalfChunk == 0L)
                    maxHalfChunkNegativity -= 1;
            }
            if ((minHalfChunk == maxHalfChunk && minHalfChunkNegativity == maxHalfChunkNegativity))
            {
                this.imin.value &= mask;
                this.imax.value &= mask;
                this.imin.negative = (this.imin.value & ~halfChunkMask) != 0L;
                this.imax.negative = (this.imax.value & ~halfChunkMask) != 0L;
                halfChunkMask += 1L;
                this.imin.value = (this.imin.value ^ halfChunkMask) - halfChunkMask;
                this.imax.value = (this.imax.value ^ halfChunkMask) - halfChunkMask;
            }
            else
            {
                this.imin = new SignExtendedNumber(~halfChunkMask, true).copy();
                this.imax = new SignExtendedNumber(halfChunkMask, false).copy();
            }
            return this;
        }

        public  IntRange castUnsigned(long mask) {
            long minChunk = this.imin.value & ~mask;
            long maxChunk = this.imax.value & ~mask;
            if ((minChunk == maxChunk && (this.imin.negative ? 1 : 0) == (this.imax.negative ? 1 : 0)))
            {
                this.imin.value &= mask;
                this.imax.value &= mask;
            }
            else
            {
                this.imin.value = 0L;
                this.imax.value = mask;
            }
            this.imin.negative = (this.imax.negative = false);
            return this;
        }

        public  IntRange castDchar() {
            this.castUnsigned(4294967295L);
            if (this.imin.value > 1114111L)
                this.imin.value = 1114111L;
            if (this.imax.value > 1114111L)
                this.imax.value = 1114111L;
            return this;
        }

        public  IntRange _cast(Type type) {
            if ((!(type.isintegral()) || (type.toBasetype().ty & 0xFF) == ENUMTY.Tvector))
                return this;
            else if (!(type.isunsigned()))
                return this.castSigned(type.sizemask());
            else if ((type.toBasetype().ty & 0xFF) == ENUMTY.Tdchar)
                return this.castDchar();
            else
                return this.castUnsigned(type.sizemask());
        }

        public  IntRange castUnsigned(Type type) {
            if ((!(type.isintegral()) || (type.toBasetype().ty & 0xFF) == ENUMTY.Tvector))
                return this.castUnsigned(-1L);
            else if ((type.toBasetype().ty & 0xFF) == ENUMTY.Tdchar)
                return this.castDchar();
            else
                return this.castUnsigned(type.sizemask());
        }

        public  boolean contains(IntRange a) {
            return (this.imin.opCmp(a.imin) <= 0 && this.imax.opCmp(a.imax) >= 0);
        }

        public  boolean containsZero() {
            return ((this.imin.negative && !(this.imax.negative)) || (!(this.imin.negative) && this.imin.value == 0L));
        }

        public  IntRange absNeg() {
            if (this.imax.negative)
                return this;
            else if (!(this.imin.negative))
                return new IntRange(this.imax.opUnary_-(), this.imin.opUnary_-());
            else
            {
                SignExtendedNumber imaxAbsNeg = this.imax.opUnary_-().copy();
                return new IntRange(imaxAbsNeg.opCmp(this.imin) < 0 ? imaxAbsNeg : this.imin, new SignExtendedNumber(0L, false));
            }
        }

        public  IntRange unionWith(IntRange other) {
            return new IntRange(this.imin.opCmp(other.imin) < 0 ? this.imin : other.imin, this.imax.opCmp(other.imax) > 0 ? this.imax : other.imax);
        }

        public  void unionOrAssign(IntRange other, Ref<Boolean> union_) {
            if ((!(union_.value) || this.imin.opCmp(other.imin) > 0))
                this.imin = other.imin.copy();
            if ((!(union_.value) || this.imax.opCmp(other.imax) < 0))
                this.imax = other.imax.copy();
            union_.value = true;
        }

        public  IntRange dump(BytePtr funcName, Expression e) {
            printf( new ByteSlice("[(%c)%#018llx, (%c)%#018llx] @ %s ::: %s\n"), this.imin.negative ? 45 : 43, this.imin.value, this.imax.negative ? 45 : 43, this.imax.value, funcName, e.toChars());
            return this;
        }

        public  void splitBySign(IntRange negRange, Ref<Boolean> hasNegRange, IntRange nonNegRange, Ref<Boolean> hasNonNegRange) {
            hasNegRange.value = this.imin.negative;
            if (hasNegRange.value)
            {
                negRange.imin = this.imin.copy();
                negRange.imax = (this.imax.negative ? this.imax : new SignExtendedNumber(-1L, true)).copy();
            }
            hasNonNegRange.value = !(this.imax.negative);
            if (hasNonNegRange.value)
            {
                nonNegRange.imin = (this.imin.negative ? new SignExtendedNumber(0L, false) : this.imin).copy();
                nonNegRange.imax = this.imax.copy();
            }
        }

        // from template opUnary!(_~)
        public  IntRange opUnary_~() {
            return new IntRange(this.imax.opUnary_~(), this.imin.opUnary_~());
        }


        public static SignExtendedNumber maxOr(IntRange lhs, IntRange rhs) {
            long x = 0L;
            boolean sign = false;
            long xor = lhs.imax.value ^ rhs.imax.value;
            long and = lhs.imax.value & rhs.imax.value;
            IntRange lhsc = lhsc = new IntRange(lhs);
            IntRange rhsc = rhsc = new IntRange(rhs);
            if (lhsc.imax.negative ^ rhsc.imax.negative)
            {
                sign = true;
                if (lhsc.imax.negative)
                {
                    if (!(lhsc.imin.negative))
                    {
                        lhsc.imin.value = 0L;
                    }
                    if (!(rhsc.imin.negative))
                    {
                        rhsc.imin.value = 0L;
                    }
                }
            }
            else if (lhsc.imin.negative & rhsc.imin.negative)
            {
                sign = true;
            }
            else if (lhsc.imax.negative & rhsc.imax.negative)
            {
                return new SignExtendedNumber(-1L, false);
            }
            {
                long d = -9223372036854775808L;
                for (; (d) != 0;d >>= 1){
                    if ((xor & d) != 0)
                    {
                        x |= d;
                        if ((lhsc.imax.value & d) != 0)
                        {
                            if ((~lhsc.imin.value & d) != 0)
                            {
                                lhsc.imin.value = 0L;
                            }
                        }
                        else
                        {
                            if ((~rhsc.imin.value & d) != 0)
                            {
                                rhsc.imin.value = 0L;
                            }
                        }
                    }
                    else if ((lhsc.imin.value & rhsc.imin.value & d) != 0)
                    {
                        x |= d;
                    }
                    else if ((and & d) != 0)
                    {
                        x |= (d << 1) - 1L;
                        break;
                    }
                }
            }
            SignExtendedNumber range = new SignExtendedNumber(x, sign).copy();
            return range;
        }

        public static SignExtendedNumber minOr(IntRange lhs, IntRange rhs) {
            return maxAnd(lhs.opUnary_~(), rhs.opUnary_~()).opUnary_~();
        }

        public static SignExtendedNumber maxAnd(IntRange lhs, IntRange rhs) {
            long x = 0L;
            boolean sign = false;
            IntRange lhsc = lhsc = new IntRange(lhs);
            IntRange rhsc = rhsc = new IntRange(rhs);
            if (lhsc.imax.negative & rhsc.imax.negative)
            {
                sign = true;
            }
            {
                long d = -9223372036854775808L;
                for (; (d) != 0;d >>= 1){
                    if ((lhsc.imax.value & rhsc.imax.value & d) != 0)
                    {
                        x |= d;
                        if ((~lhsc.imin.value & d) != 0)
                        {
                            lhsc.imin.value = 0L;
                        }
                        if ((~rhsc.imin.value & d) != 0)
                        {
                            rhsc.imin.value = 0L;
                        }
                    }
                    else if (((~lhsc.imin.value & d) != 0 && (lhsc.imax.value & d) != 0))
                    {
                        lhsc.imax.value |= d - 1L;
                    }
                    else if (((~rhsc.imin.value & d) != 0 && (rhsc.imax.value & d) != 0))
                    {
                        rhsc.imax.value |= d - 1L;
                    }
                }
            }
            SignExtendedNumber range = new SignExtendedNumber(x, sign).copy();
            return range;
        }

        public static SignExtendedNumber minAnd(IntRange lhs, IntRange rhs) {
            return maxOr(lhs.opUnary_~(), rhs.opUnary_~()).opUnary_~();
        }

        public static void swap(IntRange a, IntRange b) {
            IntRange aux = a.copy();
            a = b.copy();
            b = aux.copy();
        }

        public IntRange(){
            imin = new SignExtendedNumber();
            imax = new SignExtendedNumber();
        }
        public IntRange copy(){
            IntRange r = new IntRange();
            r.imin = imin.copy();
            r.imax = imax.copy();
            return r;
        }
        public IntRange opAssign(IntRange that) {
            this.imin = that.imin;
            this.imax = that.imax;
            return this;
        }
    }
}
