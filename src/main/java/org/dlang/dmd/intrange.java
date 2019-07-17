package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.mtype.*;

public class intrange {

    // Erasure: copySign<long, boolean>
    public static long copySign(long x, boolean sign) {
        return x - (sign ? 1 : 0) ^ -(sign ? 1 : 0);
    }

    public static class SignExtendedNumber
    {
        public long value = 0L;
        public boolean negative = false;
        // Erasure: fromInteger<long>
        public static SignExtendedNumber fromInteger(long value_) {
            return new SignExtendedNumber(value_, (value_ >> 63 != 0));
        }

        // Erasure: extreme<boolean>
        public static SignExtendedNumber extreme(boolean minimum) {
            return new SignExtendedNumber((long)((minimum ? 1 : 0) - 1), minimum);
        }

        // Erasure: max<>
        public static SignExtendedNumber max() {
            return new SignExtendedNumber(-1L, false);
        }

        // Erasure: min<>
        public static SignExtendedNumber min() {
            return new SignExtendedNumber(0L, true);
        }

        // Erasure: isMinimum<>
        public  boolean isMinimum() {
            return this.negative && (this.value == 0L);
        }

        // Erasure: opEquals<SignExtendedNumber>
        public  boolean opEquals(SignExtendedNumber a) {
            return (this.value == a.value) && ((this.negative ? 1 : 0) == (a.negative ? 1 : 0));
        }

        // Erasure: opCmp<SignExtendedNumber>
        public  int opCmp(SignExtendedNumber a) {
            if (((this.negative ? 1 : 0) != (a.negative ? 1 : 0)))
            {
                if (this.negative)
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
            if ((this.value < a.value))
            {
                return -1;
            }
            else if ((this.value > a.value))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }

        // from template opUnary!(_invert)
        // Erasure: opUnary_invert<>
        public  SignExtendedNumber opUnary_invert() {
            if ((~this.value == 0L))
            {
                return new SignExtendedNumber(~this.value, false);
            }
            else
            {
                return new SignExtendedNumber(~this.value, !this.negative);
            }
        }


        // from template opUnary!(_minus)
        // Erasure: opUnary_minus<>
        public  SignExtendedNumber opUnary_minus() {
            if ((this.value == 0L))
            {
                return new SignExtendedNumber(-(this.negative ? 1 : 0), false);
            }
            else
            {
                return new SignExtendedNumber(-this.value, !this.negative);
            }
        }


        // from template opBinary!(_plus)
        // Erasure: opBinary_plus<SignExtendedNumber>
        public  SignExtendedNumber opBinary_plus(SignExtendedNumber rhs) {
            long sum = this.value + rhs.value;
            boolean carry = (sum < this.value) && (sum < rhs.value);
            if (((this.negative ? 1 : 0) != (rhs.negative ? 1 : 0)))
            {
                return new SignExtendedNumber(sum, !carry);
            }
            else if (this.negative)
            {
                return new SignExtendedNumber(carry ? sum : 0L, true);
            }
            else
            {
                return new SignExtendedNumber(carry ? -1L : sum, false);
            }
        }


        // from template opBinary!(_minus)
        // Erasure: opBinary_minus<SignExtendedNumber>
        public  SignExtendedNumber opBinary_minus(SignExtendedNumber rhs) {
            if (rhs.isMinimum())
            {
                return this.negative ? new SignExtendedNumber(this.value, false) : max();
            }
            else
            {
                return this.opBinary_plus(rhs.opUnary_minus());
            }
        }


        // from template opBinary!(_mul)
        // Erasure: opBinary_mul<SignExtendedNumber>
        public  SignExtendedNumber opBinary_mul(SignExtendedNumber rhs) {
            if ((this.value == 0L))
            {
                if (!this.negative)
                {
                    return this;
                }
                else if (rhs.negative)
                {
                    return max();
                }
                else
                {
                    return (rhs.value == 0L) ? rhs : this;
                }
            }
            else if ((rhs.value == 0L))
            {
                return rhs.opBinary_mul(this);
            }
            SignExtendedNumber rv = new SignExtendedNumber();
            long tAbs = copySign(this.value, this.negative);
            long aAbs = copySign(rhs.value, rhs.negative);
            rv.negative = (this.negative ? 1 : 0) != (rhs.negative ? 1 : 0);
            if ((-1L / tAbs < aAbs))
            {
                rv.value = (long)((rv.negative ? 1 : 0) - 1);
            }
            else
            {
                rv.value = copySign(tAbs * aAbs, rv.negative);
            }
            return rv;
        }


        // from template opBinary!(_div)
        // Erasure: opBinary_div<SignExtendedNumber>
        public  SignExtendedNumber opBinary_div(SignExtendedNumber rhs) {
            if ((rhs.value == 0L))
            {
                if (rhs.negative)
                {
                    return new SignExtendedNumber((((this.value == 0L) && this.negative) ? 1 : 0), false);
                }
                else
                {
                    return extreme(this.negative);
                }
            }
            long aAbs = copySign(rhs.value, rhs.negative);
            long rvVal = 0L;
            if (!this.isMinimum())
            {
                rvVal = copySign(this.value, this.negative) / aAbs;
            }
            else if ((aAbs & aAbs - 1L) != 0)
            {
                rvVal = -1L / aAbs;
            }
            else
            {
                if ((aAbs == 1L))
                {
                    return extreme(!rhs.negative);
                }
                rvVal = -9223372036854775808L;
                aAbs >>= 1;
                if ((aAbs & -6148914691236517206L) != 0)
                {
                    rvVal >>= 1;
                }
                if ((aAbs & -3689348814741910324L) != 0)
                {
                    rvVal >>= 2;
                }
                if ((aAbs & -1085102592571150096L) != 0)
                {
                    rvVal >>= 4;
                }
                if ((aAbs & -71777214294589696L) != 0)
                {
                    rvVal >>= 8;
                }
                if ((aAbs & -281470681808896L) != 0)
                {
                    rvVal >>= 16;
                }
                if ((aAbs & -4294967296L) != 0)
                {
                    rvVal >>= 32;
                }
            }
            boolean rvNeg = (this.negative ? 1 : 0) != (rhs.negative ? 1 : 0);
            rvVal = copySign(rvVal, rvNeg);
            return new SignExtendedNumber(rvVal, (rvVal != 0L) && rvNeg);
        }


        // from template opBinary!(_ll)
        // Erasure: opBinary_ll<SignExtendedNumber>
        public  SignExtendedNumber opBinary_ll(SignExtendedNumber rhs) {
            if ((this.value == 0L))
            {
                return this;
            }
            else if (rhs.negative)
            {
                return extreme(this.negative);
            }
            long v = copySign(this.value, this.negative);
            int r = 0;
            int s = 0;
            r = (((v > 4294967295L) ? 1 : 0) << 5);
            v >>= (int)(long)r;
            s = (((v > 65535L) ? 1 : 0) << 4);
            v >>= (int)(long)s;
            r |= s;
            s = (((v > 255L) ? 1 : 0) << 3);
            v >>= (int)(long)s;
            r |= s;
            s = (((v > 15L) ? 1 : 0) << 2);
            v >>= (int)(long)s;
            r |= s;
            s = (((v > 3L) ? 1 : 0) << 1);
            v >>= (int)(long)s;
            r |= s;
            (long)r |= v >> 1;
            long allowableShift = (long)(63 - r);
            if ((rhs.value > allowableShift))
            {
                return extreme(this.negative);
            }
            else
            {
                return new SignExtendedNumber(this.value << (int)rhs.value, this.negative);
            }
        }


        // from template opBinary!(_rr)
        // Erasure: opBinary_rr<SignExtendedNumber>
        public  SignExtendedNumber opBinary_rr(SignExtendedNumber rhs) {
            if (rhs.negative || (rhs.value > 63L))
            {
                return this.negative ? new SignExtendedNumber(-1L, true) : new SignExtendedNumber(0L, false);
            }
            else if (this.isMinimum())
            {
                return (rhs.value == 0L) ? this : new SignExtendedNumber(-1L << (int)(64L - rhs.value), true);
            }
            long x = this.value ^ (long)-(this.negative ? 1 : 0);
            x >>= (int)rhs.value;
            return new SignExtendedNumber(x ^ (long)-(this.negative ? 1 : 0), this.negative);
        }


        public SignExtendedNumber(){ }
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
        // Erasure: __ctor<IntRange>
        public  IntRange(IntRange another) {
            this.imin.opAssign(another.imin.copy());
            this.imax.opAssign(another.imax.copy());
        }

        // Erasure: __ctor<SignExtendedNumber>
        public  IntRange(SignExtendedNumber a) {
            this.imin.opAssign(a.copy());
            this.imax.opAssign(a.copy());
        }

        // Erasure: __ctor<SignExtendedNumber, SignExtendedNumber>
        public  IntRange(SignExtendedNumber lower, SignExtendedNumber upper) {
            this.imin.opAssign(lower.copy());
            this.imax.opAssign(upper.copy());
        }

        // Erasure: fromType<Type>
        public static IntRange fromType(Type type) {
            return fromType(type, type.isunsigned());
        }

        // Erasure: fromType<Type, boolean>
        public static IntRange fromType(Type type, boolean isUnsigned) {
            if (!type.isintegral() || ((type.toBasetype().ty & 0xFF) == ENUMTY.Tvector))
            {
                return widest();
            }
            long mask = type.sizemask();
            SignExtendedNumber lower = new SignExtendedNumber(0L, false).copy();
            SignExtendedNumber upper = new SignExtendedNumber(mask, false).copy();
            if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tdchar))
            {
                upper.value = 1114111L;
            }
            else if (!isUnsigned)
            {
                lower.value = ~(mask >> 1);
                lower.negative = true;
                upper.value = mask >> 1;
            }
            return new IntRange(lower, upper);
        }

        // Erasure: fromNumbers2<Ptr>
        public static IntRange fromNumbers2(Ptr<SignExtendedNumber> numbers) {
            if ((numbers.get(0).opCmp(numbers.get(1)) < 0))
            {
                return new IntRange(numbers.get(0), numbers.get(1));
            }
            else
            {
                return new IntRange(numbers.get(1), numbers.get(0));
            }
        }

        // Erasure: fromNumbers4<Ptr>
        public static IntRange fromNumbers4(Ptr<SignExtendedNumber> numbers) {
            IntRange ab = fromNumbers2(numbers).copy();
            IntRange cd = fromNumbers2(numbers.plus(24)).copy();
            if ((cd.imin.opCmp(ab.imin) < 0))
            {
                ab.imin.opAssign(cd.imin.copy());
            }
            if ((cd.imax.opCmp(ab.imax) > 0))
            {
                ab.imax.opAssign(cd.imax.copy());
            }
            return ab;
        }

        // Erasure: widest<>
        public static IntRange widest() {
            return new IntRange(SignExtendedNumber.min(), SignExtendedNumber.max());
        }

        // Erasure: castSigned<long>
        public  IntRange castSigned(long mask) {
            long halfChunkMask = mask >> 1;
            long minHalfChunk = this.imin.value & ~halfChunkMask;
            long maxHalfChunk = this.imax.value & ~halfChunkMask;
            int minHalfChunkNegativity = (this.imin.negative ? 1 : 0);
            int maxHalfChunkNegativity = (this.imax.negative ? 1 : 0);
            if ((minHalfChunk & mask) != 0)
            {
                minHalfChunk += halfChunkMask + 1L;
                if ((minHalfChunk == 0L))
                {
                    minHalfChunkNegativity -= 1;
                }
            }
            if ((maxHalfChunk & mask) != 0)
            {
                maxHalfChunk += halfChunkMask + 1L;
                if ((maxHalfChunk == 0L))
                {
                    maxHalfChunkNegativity -= 1;
                }
            }
            if ((minHalfChunk == maxHalfChunk) && (minHalfChunkNegativity == maxHalfChunkNegativity))
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
                this.imin.opAssign(new SignExtendedNumber(~halfChunkMask, true));
                this.imax.opAssign(new SignExtendedNumber(halfChunkMask, false));
            }
            return this;
        }

        // Erasure: castUnsigned<long>
        public  IntRange castUnsigned(long mask) {
            long minChunk = this.imin.value & ~mask;
            long maxChunk = this.imax.value & ~mask;
            if ((minChunk == maxChunk) && ((this.imin.negative ? 1 : 0) == (this.imax.negative ? 1 : 0)))
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

        // Erasure: castDchar<>
        public  IntRange castDchar() {
            this.castUnsigned(4294967295L);
            if ((this.imin.value > 1114111L))
            {
                this.imin.value = 1114111L;
            }
            if ((this.imax.value > 1114111L))
            {
                this.imax.value = 1114111L;
            }
            return this;
        }

        // Erasure: _cast<Type>
        public  IntRange _cast(Type type) {
            if (!type.isintegral() || ((type.toBasetype().ty & 0xFF) == ENUMTY.Tvector))
            {
                return this;
            }
            else if (!type.isunsigned())
            {
                return this.castSigned(type.sizemask());
            }
            else if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tdchar))
            {
                return this.castDchar();
            }
            else
            {
                return this.castUnsigned(type.sizemask());
            }
        }

        // Erasure: castUnsigned<Type>
        public  IntRange castUnsigned(Type type) {
            if (!type.isintegral() || ((type.toBasetype().ty & 0xFF) == ENUMTY.Tvector))
            {
                return this.castUnsigned(-1L);
            }
            else if (((type.toBasetype().ty & 0xFF) == ENUMTY.Tdchar))
            {
                return this.castDchar();
            }
            else
            {
                return this.castUnsigned(type.sizemask());
            }
        }

        // Erasure: contains<IntRange>
        public  boolean contains(IntRange a) {
            return (this.imin.opCmp(a.imin) <= 0) && (this.imax.opCmp(a.imax) >= 0);
        }

        // Erasure: containsZero<>
        public  boolean containsZero() {
            return this.imin.negative && !this.imax.negative || !this.imin.negative && (this.imin.value == 0L);
        }

        // Erasure: absNeg<>
        public  IntRange absNeg() {
            if (this.imax.negative)
            {
                return this;
            }
            else if (!this.imin.negative)
            {
                return new IntRange(this.imax.opUnary_minus(), this.imin.opUnary_minus());
            }
            else
            {
                SignExtendedNumber imaxAbsNeg = this.imax.opUnary_minus().copy();
                return new IntRange((imaxAbsNeg.opCmp(this.imin) < 0) ? imaxAbsNeg : this.imin, new SignExtendedNumber(0L, false));
            }
        }

        // Erasure: unionWith<IntRange>
        public  IntRange unionWith(IntRange other) {
            return new IntRange((this.imin.opCmp(other.imin) < 0) ? this.imin : other.imin, (this.imax.opCmp(other.imax) > 0) ? this.imax : other.imax);
        }

        // Erasure: unionOrAssign<IntRange, boolean>
        public  void unionOrAssign(IntRange other, Ref<Boolean> union_) {
            if (!union_.value || (this.imin.opCmp(other.imin) > 0))
            {
                this.imin.opAssign(other.imin.copy());
            }
            if (!union_.value || (this.imax.opCmp(other.imax) < 0))
            {
                this.imax.opAssign(other.imax.copy());
            }
            union_.value = true;
        }

        // Erasure: dump<Ptr, Expression>
        public  IntRange dump(BytePtr funcName, Expression e) {
            printf(new BytePtr("[(%c)%#018llx, (%c)%#018llx] @ %s ::: %s\n"), this.imin.negative ? 45 : 43, this.imin.value, this.imax.negative ? 45 : 43, this.imax.value, funcName, e.toChars());
            return this;
        }

        // Erasure: splitBySign<IntRange, boolean, IntRange, boolean>
        public  void splitBySign(IntRange negRange, Ref<Boolean> hasNegRange, IntRange nonNegRange, Ref<Boolean> hasNonNegRange) {
            hasNegRange.value = this.imin.negative;
            if (hasNegRange.value)
            {
                negRange.imin.opAssign(this.imin.copy());
                negRange.imax.opAssign(this.imax.negative ? this.imax : new SignExtendedNumber(-1L, true).copy());
            }
            hasNonNegRange.value = !this.imax.negative;
            if (hasNonNegRange.value)
            {
                nonNegRange.imin.opAssign(this.imin.negative ? new SignExtendedNumber(0L, false) : this.imin.copy());
                nonNegRange.imax.opAssign(this.imax.copy());
            }
        }

        // from template opUnary!(_invert)
        // Erasure: opUnary_invert<>
        public  IntRange opUnary_invert() {
            return new IntRange(this.imax.opUnary_invert(), this.imin.opUnary_invert());
        }


        // from template opUnary!(_minus)
        // Erasure: opUnary_minus<>
        public  IntRange opUnary_minus() {
            return new IntRange(this.imax.opUnary_minus(), this.imin.opUnary_minus());
        }


        // from template opBinary!(__)
        // Erasure: opBinary__<IntRange>
        public  IntRange opBinary__(IntRange rhs) {
            if ((((this.imin.negative ^ this.imax.negative) ? 1 : 0) != 1) && (((rhs.imin.negative ^ rhs.imax.negative) ? 1 : 0) != 1))
            {
                return new IntRange(minAnd(this, rhs), maxAnd(this, rhs));
            }
            Ref<IntRange> l = ref(l.value = new IntRange(this));
            Ref<IntRange> r = ref(r.value = new IntRange(rhs));
            if ((((this.imin.negative ^ this.imax.negative) ? 1 : 0) == 1) && (((rhs.imin.negative ^ rhs.imax.negative) ? 1 : 0) == 1))
            {
                SignExtendedNumber max = (l.value.imax.value > r.value.imax.value) ? l.value.imax : r.value.imax.copy();
                l.value.imax.value = -1L;
                l.value.imax.negative = true;
                r.value.imax.value = -1L;
                r.value.imax.negative = true;
                return new IntRange(minAnd(l.value, r.value), max);
            }
            else
            {
                if ((((l.value.imin.negative ^ l.value.imax.negative) ? 1 : 0) == 1))
                {
                    swap(l, r);
                }
                SignExtendedNumber minAndNeg = minAnd(l.value, new IntRange(r.value.imin, new SignExtendedNumber(-1L, false))).copy();
                SignExtendedNumber minAndPos = minAnd(l.value, new IntRange(new SignExtendedNumber(0L, false), r.value.imax)).copy();
                SignExtendedNumber maxAndNeg = maxAnd(l.value, new IntRange(r.value.imin, new SignExtendedNumber(-1L, false))).copy();
                SignExtendedNumber maxAndPos = maxAnd(l.value, new IntRange(new SignExtendedNumber(0L, false), r.value.imax)).copy();
                SignExtendedNumber min = (minAndNeg.opCmp(minAndPos) < 0) ? minAndNeg : minAndPos.copy();
                SignExtendedNumber max = (maxAndNeg.opCmp(maxAndPos) > 0) ? maxAndNeg : maxAndPos.copy();
                IntRange range = range = new IntRange(min, max);
                return range;
            }
        }


        // from template opBinary!(__)
        // removed duplicate function, [["IntRange fromNumbers4Ptr<SignExtendedNumber>", "IntRange castUnsignedlong", "IntRange castDchar", "IntRange opBinaryIntRange__", "IntRange fromTypeType, boolean", "IntRange _castType", "IntRange opUnary_invert", "IntRange absNeg", "boolean containsIntRange", "IntRange widest", "IntRange fromTypeType", "IntRange __ctorSignExtendedNumber", "void splitBySignIntRange, Ref<Boolean>, IntRange, Ref<Boolean>", "IntRange __ctorSignExtendedNumber, SignExtendedNumber", "boolean containsZero", "IntRange castSignedlong", "IntRange castUnsignedType", "IntRange unionWithIntRange", "IntRange __ctorIntRange", "void unionOrAssignIntRange, Ref<Boolean>", "IntRange dumpBytePtr, Expression", "IntRange opUnary_minus", "IntRange fromNumbers2Ptr<SignExtendedNumber>"]] signature: IntRange opBinaryIntRange__

        // from template opBinary!(__)
        // removed duplicate function, [["IntRange fromNumbers4Ptr<SignExtendedNumber>", "IntRange castUnsignedlong", "IntRange castDchar", "IntRange opBinaryIntRange__", "IntRange fromTypeType, boolean", "IntRange _castType", "IntRange opUnary_invert", "IntRange absNeg", "boolean containsIntRange", "IntRange widest", "IntRange fromTypeType", "IntRange __ctorSignExtendedNumber", "void splitBySignIntRange, Ref<Boolean>, IntRange, Ref<Boolean>", "IntRange __ctorSignExtendedNumber, SignExtendedNumber", "boolean containsZero", "IntRange castSignedlong", "IntRange castUnsignedType", "IntRange unionWithIntRange", "IntRange __ctorIntRange", "void unionOrAssignIntRange, Ref<Boolean>", "IntRange dumpBytePtr, Expression", "IntRange opUnary_minus", "IntRange fromNumbers2Ptr<SignExtendedNumber>"]] signature: IntRange opBinaryIntRange__

        // from template opBinary!(_plus)
        // Erasure: opBinary_plus<IntRange>
        public  IntRange opBinary_plus(IntRange rhs) {
            return new IntRange(this.imin.opBinary_plus(rhs.imin), this.imax.opBinary_plus(rhs.imax));
        }


        // from template opBinary!(_minus)
        // Erasure: opBinary_minus<IntRange>
        public  IntRange opBinary_minus(IntRange rhs) {
            return new IntRange(this.imin.opBinary_minus(rhs.imax), this.imax.opBinary_minus(rhs.imin));
        }


        // from template opBinary!(_mul)
        // Erasure: opBinary_mul<IntRange>
        public  IntRange opBinary_mul(IntRange rhs) {
            Slice<SignExtendedNumber> bdy = new RawSlice<SignExtendedNumber>(new SignExtendedNumber[4]);
            bdy.set(0, this.imin.opBinary_mul(rhs.imin));
            bdy.set(1, this.imin.opBinary_mul(rhs.imax));
            bdy.set(2, this.imax.opBinary_mul(rhs.imin));
            bdy.set(3, this.imax.opBinary_mul(rhs.imax));
            return fromNumbers4(bdy.ptr());
        }


        // from template opBinary!(_div)
        // Erasure: opBinary_div<IntRange>
        public  IntRange opBinary_div(IntRange rhs) {
            if ((rhs.imax.value == 0L) && (rhs.imin.value == 0L))
            {
                return widest();
            }
            if ((rhs.imax.value == 0L))
            {
                rhs.imax.value--;
            }
            else if ((rhs.imin.value == 0L))
            {
                rhs.imin.value++;
            }
            if (!this.imin.negative && !this.imax.negative && !rhs.imin.negative && !rhs.imax.negative)
            {
                return new IntRange(this.imin.opBinary_div(rhs.imax), this.imax.opBinary_div(rhs.imin));
            }
            else
            {
                Slice<SignExtendedNumber> bdy = new RawSlice<SignExtendedNumber>(new SignExtendedNumber[4]);
                bdy.set(0, this.imin.opBinary_div(rhs.imin));
                bdy.set(1, this.imin.opBinary_div(rhs.imax));
                bdy.set(2, this.imax.opBinary_div(rhs.imin));
                bdy.set(3, this.imax.opBinary_div(rhs.imax));
                return fromNumbers4(bdy.ptr());
            }
        }


        // from template opBinary!(_mod)
        // Erasure: opBinary_mod<IntRange>
        public  IntRange opBinary_mod(IntRange rhs) {
            IntRange irNum = this.copy();
            IntRange irDen = rhs.absNeg().copy();
            irDen.imin.opAssign(irDen.imin.opBinary_plus(new SignExtendedNumber(1L, false)).copy());
            irDen.imax.opAssign(irDen.imin.opUnary_minus().copy());
            if (!irNum.imin.negative)
            {
                irNum.imin.value = 0L;
            }
            else if ((irNum.imin.opCmp(irDen.imin) < 0))
            {
                irNum.imin.opAssign(irDen.imin.copy());
            }
            if (irNum.imax.negative)
            {
                irNum.imax.negative = false;
                irNum.imax.value = 0L;
            }
            else if ((irNum.imax.opCmp(irDen.imax) > 0))
            {
                irNum.imax.opAssign(irDen.imax.copy());
            }
            return irNum;
        }


        // from template opBinary!(_ll)
        // Erasure: opBinary_ll<IntRange>
        public  IntRange opBinary_ll(IntRange rhs) {
            if (rhs.imin.negative)
            {
                rhs.opAssign(new IntRange(new SignExtendedNumber(0L, false), new SignExtendedNumber(64L, false)).copy());
            }
            SignExtendedNumber lower = this.imin.opBinary_ll(this.imin.negative ? rhs.imax : rhs.imin).copy();
            SignExtendedNumber upper = this.imax.opBinary_ll(this.imax.negative ? rhs.imin : rhs.imax).copy();
            return new IntRange(lower, upper);
        }


        // from template opBinary!(_rr)
        // Erasure: opBinary_rr<IntRange>
        public  IntRange opBinary_rr(IntRange rhs) {
            if (rhs.imin.negative)
            {
                rhs.opAssign(new IntRange(new SignExtendedNumber(0L, false), new SignExtendedNumber(64L, false)).copy());
            }
            SignExtendedNumber lower = this.imin.opBinary_rr(this.imin.negative ? rhs.imin : rhs.imax).copy();
            SignExtendedNumber upper = this.imax.opBinary_rr(this.imax.negative ? rhs.imax : rhs.imin).copy();
            return new IntRange(lower, upper);
        }


        // from template opBinary!(_rrr)
        // Erasure: opBinary_rrr<IntRange>
        public  IntRange opBinary_rrr(IntRange rhs) {
            if (rhs.imin.negative)
            {
                rhs.opAssign(new IntRange(new SignExtendedNumber(0L, false), new SignExtendedNumber(64L, false)).copy());
            }
            return new IntRange(this.imin.opBinary_rr(rhs.imax), this.imax.opBinary_rr(rhs.imin));
        }


        // Erasure: maxOr<IntRange, IntRange>
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
                    if (!lhsc.imin.negative)
                    {
                        lhsc.imin.value = 0L;
                    }
                    if (!rhsc.imin.negative)
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
                for (; d != 0;d >>= 1){
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

        // Erasure: minOr<IntRange, IntRange>
        public static SignExtendedNumber minOr(IntRange lhs, IntRange rhs) {
            return maxAnd(lhs.opUnary_invert(), rhs.opUnary_invert()).opUnary_invert();
        }

        // Erasure: maxAnd<IntRange, IntRange>
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
                for (; d != 0;d >>= 1){
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
                    else if (((~lhsc.imin.value & d) != 0) && ((lhsc.imax.value & d) != 0))
                    {
                        lhsc.imax.value |= d - 1L;
                    }
                    else if (((~rhsc.imin.value & d) != 0) && ((rhsc.imax.value & d) != 0))
                    {
                        rhsc.imax.value |= d - 1L;
                    }
                }
            }
            SignExtendedNumber range = new SignExtendedNumber(x, sign).copy();
            return range;
        }

        // Erasure: minAnd<IntRange, IntRange>
        public static SignExtendedNumber minAnd(IntRange lhs, IntRange rhs) {
            return maxOr(lhs.opUnary_invert(), rhs.opUnary_invert()).opUnary_invert();
        }

        // Erasure: swap<IntRange, IntRange>
        public static void swap(IntRange a, IntRange b) {
            IntRange aux = a.copy();
            a.opAssign(b.copy());
            b.opAssign(aux.copy());
        }

        public IntRange(){ }
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
