package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;

public class complex {

    public static class complex_t
    {
        public double re = 0.0;
        public double im = 0.0;
        // Erasure: __ctor<double>
        public  complex_t(double re) {
            this(re, CTFloat.zero);
        }

        // Erasure: __ctor<double, double>
        public  complex_t(double re, double im) {
            this.re = re;
            this.im = im;
        }

        // Erasure: opAdd<complex_t>
        public  complex_t opAdd(complex_t y) {
            return new complex_t(this.re + y.re, this.im + y.im);
        }

        // Erasure: opSub<complex_t>
        public  complex_t opSub(complex_t y) {
            return new complex_t(this.re - y.re, this.im - y.im);
        }

        // Erasure: opNeg<>
        public  complex_t opNeg() {
            return new complex_t(-this.re, -this.im);
        }

        // Erasure: opMul<complex_t>
        public  complex_t opMul(complex_t y) {
            return new complex_t(this.re * y.re - this.im * y.im, this.im * y.re + this.re * y.im);
        }

        // Erasure: opMul_r<double>
        public  complex_t opMul_r(double x) {
            return new complex_t(x).opMul(this);
        }

        // Erasure: opMul<double>
        public  complex_t opMul(double y) {
            return this.opMul(new complex_t(y));
        }

        // Erasure: opDiv<double>
        public  complex_t opDiv(double y) {
            return this.opDiv(new complex_t(y));
        }

        // Erasure: opDiv<complex_t>
        public  complex_t opDiv(complex_t y) {
            if ((CTFloat.fabs(y.re) < CTFloat.fabs(y.im)))
            {
                double r = y.re / y.im;
                double den = y.im + r * y.re;
                return new complex_t((this.re * r + this.im) / den, (this.im * r - this.re) / den);
            }
            else
            {
                double r = y.im / y.re;
                double den = y.re + r * y.im;
                return new complex_t((this.re + r * this.im) / den, (this.im - r * this.re) / den);
            }
        }

        // from template opCast!(Boolean)
        // Erasure: opCastBoolean<>
        public  boolean opCastBoolean() {
            return (this.re != 0) || (this.im != 0);
        }


        // Erasure: opEquals<complex_t>
        public  int opEquals(complex_t y) {
            return (((this.re == y.re) && (this.im == y.im)) ? 1 : 0);
        }

        public complex_t(){
        }
        public complex_t copy(){
            complex_t r = new complex_t();
            r.re = re;
            r.im = im;
            return r;
        }
        public complex_t opAssign(complex_t that) {
            this.re = that.re;
            this.im = that.im;
            return this;
        }
    }
    // Erasure: creall<complex_t>
    public static double creall(complex_t x) {
        return x.re;
    }

    // Erasure: cimagl<complex_t>
    public static double cimagl(complex_t x) {
        return x.im;
    }

}
