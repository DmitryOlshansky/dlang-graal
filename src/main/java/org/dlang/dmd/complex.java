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
        public double re;
        public double im;
        public  complex_t(double re) {
            this(re, CTFloat.zero);
        }

        public  complex_t(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public  complex_t opAdd(complex_t y) {
            return new complex_t(this.re + y.re, this.im + y.im);
        }

        public  complex_t opSub(complex_t y) {
            return new complex_t(this.re - y.re, this.im - y.im);
        }

        public  complex_t opNeg() {
            return new complex_t(-this.re, -this.im);
        }

        public  complex_t opMul(complex_t y) {
            return new complex_t(this.re * y.re - this.im * y.im, this.im * y.re + this.re * y.im);
        }

        public  complex_t opMul_r(double x) {
            return new complex_t(x).opMul(this);
        }

        public  complex_t opMul(double y) {
            return this.opMul(new complex_t(y));
        }

        public  complex_t opDiv(double y) {
            return this.opDiv(new complex_t(y));
        }

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
        public  boolean opCastBoolean() {
            return (this.re != 0) || (this.im != 0);
        }


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
    public static double creall(complex_t x) {
        return x.re;
    }

    public static double cimagl(complex_t x) {
        return x.im;
    }

}
