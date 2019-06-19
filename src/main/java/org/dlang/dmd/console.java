package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;

import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;

public class console {


    public static class Color 
    {
        public static final int black = 0;
        public static final int red = 1;
        public static final int green = 2;
        public static final int blue = 4;
        public static final int yellow = 3;
        public static final int magenta = 5;
        public static final int cyan = 6;
        public static final int lightGray = 7;
        public static final int bright = 8;
        public static final int darkGray = 8;
        public static final int brightRed = 9;
        public static final int brightGreen = 10;
        public static final int brightBlue = 12;
        public static final int brightYellow = 11;
        public static final int brightMagenta = 13;
        public static final int brightCyan = 14;
        public static final int white = 15;
    }

    public static class Console
    {

        public _IO_FILE _fp;
        public  _IO_FILE fp() {
            return this._fp;
        }

        public static boolean detectTerminal() {
            BytePtr term = pcopy(getenv(new BytePtr("TERM")));
            return (isatty(2)) != 0 && term != null && (term.get(0)) != 0 && strcmp(term,  new ByteSlice("dumb")) != 0;
        }

        public static Console create(_IO_FILE fp) {
            Console c = new Console(null);
            (c)._fp = fp;
            return c;
        }

        public  void setColorBright(boolean bright) {
            fprintf(this._fp,  new ByteSlice("\u001b[%dm"), (bright ? 1 : 0));
        }

        public  void setColor(int color) {
            fprintf(this._fp,  new ByteSlice("\u001b[%d;%dm"), (color & Color.bright) != 0 ? 1 : 0, 30 + (color & -9));
        }

        public  void resetColor() {
            fputs( new ByteSlice("\u001b[m"), this._fp);
        }

        public Console(){
        }
        public Console copy(){
            Console r = new Console();
            r._fp = _fp;
            return r;
        }
        public Console(_IO_FILE _fp) {
            this._fp = _fp;
        }

        public Console opAssign(Console that) {
            this._fp = that._fp;
            return this;
        }
    }
        // from template RTInfo!(Console)
        static IntPtr RTInfoConsole = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{4, 1});

        // from template __equals!(Byte,Byte)
        public static boolean __equalsByteByte(ByteSlice lhs, ByteSlice rhs) {
                // from template at!(Byte)
                Function2<ByteSlice,Integer,Byte> atByte = new Function2<ByteSlice,Integer,Byte>(){
                    public Byte invoke(ByteSlice r, Integer i){
                        return (toBytePtr(r)).get(i);
                    }
                };

            if (lhs.getLength() != rhs.getLength())
                return false;
            if (lhs.getLength() == 0 && rhs.getLength() == 0)
                return true;
            Function0<Boolean> __lambda3Byte = new Function0<Boolean>(){
                public Boolean invoke(){
                    return memcmp(toBytePtr(lhs), toBytePtr(rhs), lhs.getLength() * 1) == 0;
                }
            };
            if (!(__ctfe))
            {
                return __lambda3.invoke();
            }
            else
            {
                {
                    int __key19Byte = 0;
                    int __limit20Byte = lhs.getLength();
                    for (; __key19 < __limit20;__key19 += 1) {
                        int uByte = __key19;
                        if ((atByte.invoke(lhs, u) & 0xFF) != (atByte.invoke(rhs, u) & 0xFF))
                            return false;
                    }
                }
                return true;
            }
        }


        // from template Unqual!(Byte)


        // from template at!(Byte)

        // from template _isStaticArray!(FileName)
        static boolean _isStaticArrayFileName = false;

        // from template destroy!(,FileName)

        // from template _isStaticArray!(FileBuffer)
        static boolean _isStaticArrayFileBuffer = false;

        // from template destroy!(,FileBuffer)

        // from template _isStaticArray!(Slice<ByteSlice>)
        static boolean _isStaticArraySlice<ByteSlice> = false;

        // from template destroy!(,Slice<ByteSlice>)

        // from template RTInfo!(FileAndLines)
        static IntPtr RTInfoFileAndLines = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{24, 44});

        // from template _isStaticArray!(StringValue)
        static boolean _isStaticArrayStringValue = false;

        // from template destroy!(,StringValue)

        // from template RTInfo!(FileCache)
        static IntPtr RTInfoFileCache = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{28, 5});

        // from template RTInfo!(DArray<BytePtr>)
        static IntPtr RTInfoDArray<BytePtr> = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{16, 10});

        // from template RTInfo!(DArray<Identifier>)
        static IntPtr RTInfoDArray<Identifier> = ptr(RTInfoImpl);

        // from template __equals!(Byte,Byte)

        // from template Unqual!(Byte)


        // from template at!(Byte)

        // from template __equals!(Byte,Byte)

        // from template Unqual!(Byte)


        // from template at!(Byte)

        // from template RTInfo!(Param)
        static IntPtr RTInfoParam = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{400, -369098752, 1423087018, -1431655883, 10});

        // from template RTInfo!(Global)
        static IntPtr RTInfoGlobal = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{556, 1464511146, 1962934272, -1435940139, 1431655706, 1797});

        // from template hashOf!(Integer)
        public static int hashOfInteger(int val) {
            return val;
        }


        // from template hashOf!(Integer)
        public static int hashOfInteger(int val, int seed) {
            int c1Integer = -862048943;
            int c2Integer = 461845907;
            int c3Integer = -430675100;
            int r1Integer = 15;
            int r2Integer = 13;
            int hInteger = -862048943 * val;
            h = h << 15 | h >>> 17;
            h = h * 461845907 ^ seed;
            h = h << 13 | h >>> 19;
            return h * 5 + -430675100;
        }


        // from template hashOf!(ByteSlice)
        public static int hashOfByteSlice(ByteSlice val, int seed) {
            return bytesHash(toUbyte(val), seed);
        }


        // from template canBitwiseHash!(Byte)

        static boolean canBitwiseHashByte = true;

        // from template Unqual!(ByteSlice)

        // from template toUbyte!(Byte)
        public static ByteSlice toUbyteByte(ByteSlice arr) {
            return toByteSlice(arr);
        }


        // from template bytesHashAlignedBy!(Byte)
        public static int bytesHashByte(ByteSlice bytes, int seed) {
            int lenByte = bytes.getLength();
            BytePtr dataByte = pcopy(toBytePtr(bytes));
            int nblocksByte = len / 4;
            int h1Byte = seed;
            int c1Byte = -862048943;
            int c2Byte = 461845907;
            int c3Byte = -430675100;
            BytePtr end_dataByte = pcopy(data.plus((nblocks * 4) * 1));
            for (; data != end_data;data.plusAssign(4)){
                int k1Byte = get32bits(data);
                k1 *= -862048943;
                k1 = k1 << 15 | k1 >> 17;
                k1 *= 461845907;
                h1 ^= k1;
                h1 = h1 << 13 | h1 >> 19;
                h1 = h1 * 5 + -430675100;
            }
            int k1Byte = 0;
            {
                int __dispatch0 = 0;
                dispatched_0:
                do {
                    switch (__dispatch0 != 0 ? __dispatch0 : len & 3)
                    {
                        case 3:
                            k1 ^= ((data.get(2) & 0xFF) << 16);
                        case 2:
                            k1 ^= ((data.get(1) & 0xFF) << 8);
                        case 1:
                            k1 ^= (data.get(0) & 0xFF);
                            k1 *= -862048943;
                            k1 = k1 << 15 | k1 >> 17;
                            k1 *= 461845907;
                            h1 ^= k1;
                            /*goto default*/ { __dispatch0 = -1; continue dispatched_0; }
                        default:
                        {
                        }
                    }
                } while(__dispatch0 != 0);
            }
            h1 ^= len;
            h1 = (h1 ^ h1 >> 16) * -2048144789;
            h1 = (h1 ^ h1 >> 13) * -1028477387;
            h1 ^= h1 >> 16;
            return h1;
        }


        // from template bytesHash!()

        // from template get32bits!()
        public static int get32bits(BytePtr x) {
            return (x.get(3) & 0xFF) << 24 | (x.get(2) & 0xFF) << 16 | (x.get(1) & 0xFF) << 8 | (x.get(0) & 0xFF);
        }


        // from template RTInfo!(Loc)
        static IntPtr RTInfoLoc = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{12, 1});

        // from template RTInfo!(NameId)
        static IntPtr RTInfoNameId = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{12, 2});

        // from template va_start!(BytePtr)

        // from template RTInfo!(DiagnosticReporter)
        static IntPtr RTInfoDiagnosticReporter = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{8, 0});

        // from template RTInfo!(StderrDiagnosticReporter)
        static IntPtr RTInfoStderrDiagnosticReporter = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{24, 0});

        // from template RTInfo!(Id)
        static IntPtr RTInfoId = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{1, 0});

        // from template RTInfo!(Msgtable)
        static IntPtr RTInfoMsgtable = ptr(RTInfoImpl);

        // from template RTInfo!(Key)
        static IntPtr RTInfoKey = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{20, 17});

        // from template isCreateOperation!(Function0<Integer>,Integer)

        static boolean isCreateOperationFunction0<Integer>Integer = true;

        // from template isUpdateOperation!(Function1<Integer,Integer>,Integer)

        static boolean isUpdateOperationFunction1<Integer,Integer>Integer = true;

        // from template update!(Key,Integer,Function0<Integer>,Function1<Integer,Integer>)
        public static void updateKeyIntegerFunction0<Integer>Function1<Integer,Integer>(Ref<AA<Key,Integer>> aa, Key key, Function0<Integer> create, Function1<Integer,Integer> update) {
            boolean foundKeyIntegerFunction0<Integer>Function1<Integer,Integer> = false;
            Function0<IntPtr> __lambda5KeyIntegerFunction0<Integer>Function1<Integer,Integer> = new Function0<IntPtr>(){
                public IntPtr invoke(){
                    return toIntPtr(_aaGetX(toPtr<Object>(aa.value), 4, key, found));
                }
            };
            IntPtr pKeyIntegerFunction0<Integer>Function1<Integer,Integer> = pcopy(__lambda5.invoke());
            if (!(found))
                p.set(0, (create).invoke());
            else
                p.set(0, (update).invoke(p.get(0)));
        }


        // from template isSafeCopyable!(Key)
        static boolean isSafeCopyableKey = true;

        // from template RTInfo!(U)
        static IntPtr RTInfoU = ptr(RTInfoImpl);

        // from template isUpdateOperation!(Function1<Integer,Integer>,Integer)

        static boolean isUpdateOperationFunction1<Integer,Integer>Integer = true;

        // from template update!(Key,Integer,Function0<Integer>,Function1<Integer,Integer>)

        // from template RTInfo!(Identifier)
        static IntPtr RTInfoIdentifier = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{16, 8});

        // from template RTInfo!(Lexer)
        static IntPtr RTInfoLexer = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{116, 432491812});

        // from template RTInfo!(AssertDiagnosticReporter)
        static IntPtr RTInfoAssertDiagnosticReporter = ptr(RTInfoImpl);

        // from template __equals!(Byte,Byte)

        // from template Unqual!(Byte)

        // from template at!(Byte)

        // from template at!(Byte)

        // from template RTInfo!(ExpectDiagnosticReporter)
        static IntPtr RTInfoExpectDiagnosticReporter = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{17, 8});

        // from template RTInfo!(Token)
        static IntPtr RTInfoToken = ptr(RTInfoImpl);

        // from template RTInfoImpl!()
        static IntSlice RTInfoImpl = slice(new int[]{48, 467});

}
