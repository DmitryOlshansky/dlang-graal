package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;

import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;

public class utf {
    private static final char[][] initializer_0 = {{'\u00aa', '\u00aa'}, {'\u00b5', '\u00b5'}, {'\u00b7', '\u00b7'}, {'\u00ba', '\u00ba'}, {'\u00c0', '\u00d6'}, {'\u00d8', '\u00f6'}, {'\u00f8', '\u01f5'}, {'\u01fa', '\u0217'}, {'\u0250', '\u02a8'}, {'\u02b0', '\u02b8'}, {'\u02bb', '\u02bb'}, {'\u02bd', '\u02c1'}, {'\u02d0', '\u02d1'}, {'\u02e0', '\u02e4'}, {'\u037a', '\u037a'}, {'\u0386', '\u0386'}, {'\u0388', '\u038a'}, {'\u038c', '\u038c'}, {'\u038e', '\u03a1'}, {'\u03a3', '\u03ce'}, {'\u03d0', '\u03d6'}, {'\u03da', '\u03da'}, {'\u03dc', '\u03dc'}, {'\u03de', '\u03de'}, {'\u03e0', '\u03e0'}, {'\u03e2', '\u03f3'}, {'\u0401', '\u040c'}, {'\u040e', '\u044f'}, {'\u0451', '\u045c'}, {'\u045e', '\u0481'}, {'\u0490', '\u04c4'}, {'\u04c7', '\u04c8'}, {'\u04cb', '\u04cc'}, {'\u04d0', '\u04eb'}, {'\u04ee', '\u04f5'}, {'\u04f8', '\u04f9'}, {'\u0531', '\u0556'}, {'\u0559', '\u0559'}, {'\u0561', '\u0587'}, {'\u05b0', '\u05b9'}, {'\u05bb', '\u05bd'}, {'\u05bf', '\u05bf'}, {'\u05c1', '\u05c2'}, {'\u05d0', '\u05ea'}, {'\u05f0', '\u05f2'}, {'\u0621', '\u063a'}, {'\u0640', '\u0652'}, {'\u0660', '\u0669'}, {'\u0670', '\u06b7'}, {'\u06ba', '\u06be'}, {'\u06c0', '\u06ce'}, {'\u06d0', '\u06dc'}, {'\u06e5', '\u06e8'}, {'\u06ea', '\u06ed'}, {'\u06f0', '\u06f9'}, {'\u0901', '\u0903'}, {'\u0905', '\u0939'}, {'\u093d', '\u094d'}, {'\u0950', '\u0952'}, {'\u0958', '\u0963'}, {'\u0966', '\u096f'}, {'\u0981', '\u0983'}, {'\u0985', '\u098c'}, {'\u098f', '\u0990'}, {'\u0993', '\u09a8'}, {'\u09aa', '\u09b0'}, {'\u09b2', '\u09b2'}, {'\u09b6', '\u09b9'}, {'\u09be', '\u09c4'}, {'\u09c7', '\u09c8'}, {'\u09cb', '\u09cd'}, {'\u09dc', '\u09dd'}, {'\u09df', '\u09e3'}, {'\u09e6', '\u09f1'}, {'\u0a02', '\u0a02'}, {'\u0a05', '\u0a0a'}, {'\u0a0f', '\u0a10'}, {'\u0a13', '\u0a28'}, {'\u0a2a', '\u0a30'}, {'\u0a32', '\u0a33'}, {'\u0a35', '\u0a36'}, {'\u0a38', '\u0a39'}, {'\u0a3e', '\u0a42'}, {'\u0a47', '\u0a48'}, {'\u0a4b', '\u0a4d'}, {'\u0a59', '\u0a5c'}, {'\u0a5e', '\u0a5e'}, {'\u0a66', '\u0a6f'}, {'\u0a74', '\u0a74'}, {'\u0a81', '\u0a83'}, {'\u0a85', '\u0a8b'}, {'\u0a8d', '\u0a8d'}, {'\u0a8f', '\u0a91'}, {'\u0a93', '\u0aa8'}, {'\u0aaa', '\u0ab0'}, {'\u0ab2', '\u0ab3'}, {'\u0ab5', '\u0ab9'}, {'\u0abd', '\u0ac5'}, {'\u0ac7', '\u0ac9'}, {'\u0acb', '\u0acd'}, {'\u0ad0', '\u0ad0'}, {'\u0ae0', '\u0ae0'}, {'\u0ae6', '\u0aef'}, {'\u0b01', '\u0b03'}, {'\u0b05', '\u0b0c'}, {'\u0b0f', '\u0b10'}, {'\u0b13', '\u0b28'}, {'\u0b2a', '\u0b30'}, {'\u0b32', '\u0b33'}, {'\u0b36', '\u0b39'}, {'\u0b3d', '\u0b43'}, {'\u0b47', '\u0b48'}, {'\u0b4b', '\u0b4d'}, {'\u0b5c', '\u0b5d'}, {'\u0b5f', '\u0b61'}, {'\u0b66', '\u0b6f'}, {'\u0b82', '\u0b83'}, {'\u0b85', '\u0b8a'}, {'\u0b8e', '\u0b90'}, {'\u0b92', '\u0b95'}, {'\u0b99', '\u0b9a'}, {'\u0b9c', '\u0b9c'}, {'\u0b9e', '\u0b9f'}, {'\u0ba3', '\u0ba4'}, {'\u0ba8', '\u0baa'}, {'\u0bae', '\u0bb5'}, {'\u0bb7', '\u0bb9'}, {'\u0bbe', '\u0bc2'}, {'\u0bc6', '\u0bc8'}, {'\u0bca', '\u0bcd'}, {'\u0be7', '\u0bef'}, {'\u0c01', '\u0c03'}, {'\u0c05', '\u0c0c'}, {'\u0c0e', '\u0c10'}, {'\u0c12', '\u0c28'}, {'\u0c2a', '\u0c33'}, {'\u0c35', '\u0c39'}, {'\u0c3e', '\u0c44'}, {'\u0c46', '\u0c48'}, {'\u0c4a', '\u0c4d'}, {'\u0c60', '\u0c61'}, {'\u0c66', '\u0c6f'}, {'\u0c82', '\u0c83'}, {'\u0c85', '\u0c8c'}, {'\u0c8e', '\u0c90'}, {'\u0c92', '\u0ca8'}, {'\u0caa', '\u0cb3'}, {'\u0cb5', '\u0cb9'}, {'\u0cbe', '\u0cc4'}, {'\u0cc6', '\u0cc8'}, {'\u0cca', '\u0ccd'}, {'\u0cde', '\u0cde'}, {'\u0ce0', '\u0ce1'}, {'\u0ce6', '\u0cef'}, {'\u0d02', '\u0d03'}, {'\u0d05', '\u0d0c'}, {'\u0d0e', '\u0d10'}, {'\u0d12', '\u0d28'}, {'\u0d2a', '\u0d39'}, {'\u0d3e', '\u0d43'}, {'\u0d46', '\u0d48'}, {'\u0d4a', '\u0d4d'}, {'\u0d60', '\u0d61'}, {'\u0d66', '\u0d6f'}, {'\u0e01', '\u0e3a'}, {'\u0e40', '\u0e5b'}, {'\u0e81', '\u0e82'}, {'\u0e84', '\u0e84'}, {'\u0e87', '\u0e88'}, {'\u0e8a', '\u0e8a'}, {'\u0e8d', '\u0e8d'}, {'\u0e94', '\u0e97'}, {'\u0e99', '\u0e9f'}, {'\u0ea1', '\u0ea3'}, {'\u0ea5', '\u0ea5'}, {'\u0ea7', '\u0ea7'}, {'\u0eaa', '\u0eab'}, {'\u0ead', '\u0eae'}, {'\u0eb0', '\u0eb9'}, {'\u0ebb', '\u0ebd'}, {'\u0ec0', '\u0ec4'}, {'\u0ec6', '\u0ec6'}, {'\u0ec8', '\u0ecd'}, {'\u0ed0', '\u0ed9'}, {'\u0edc', '\u0edd'}, {'\u0f00', '\u0f00'}, {'\u0f18', '\u0f19'}, {'\u0f20', '\u0f33'}, {'\u0f35', '\u0f35'}, {'\u0f37', '\u0f37'}, {'\u0f39', '\u0f39'}, {'\u0f3e', '\u0f47'}, {'\u0f49', '\u0f69'}, {'\u0f71', '\u0f84'}, {'\u0f86', '\u0f8b'}, {'\u0f90', '\u0f95'}, {'\u0f97', '\u0f97'}, {'\u0f99', '\u0fad'}, {'\u0fb1', '\u0fb7'}, {'\u0fb9', '\u0fb9'}, {'\u10a0', '\u10c5'}, {'\u10d0', '\u10f6'}, {'\u1e00', '\u1e9b'}, {'\u1ea0', '\u1ef9'}, {'\u1f00', '\u1f15'}, {'\u1f18', '\u1f1d'}, {'\u1f20', '\u1f45'}, {'\u1f48', '\u1f4d'}, {'\u1f50', '\u1f57'}, {'\u1f59', '\u1f59'}, {'\u1f5b', '\u1f5b'}, {'\u1f5d', '\u1f5d'}, {'\u1f5f', '\u1f7d'}, {'\u1f80', '\u1fb4'}, {'\u1fb6', '\u1fbc'}, {'\u1fbe', '\u1fbe'}, {'\u1fc2', '\u1fc4'}, {'\u1fc6', '\u1fcc'}, {'\u1fd0', '\u1fd3'}, {'\u1fd6', '\u1fdb'}, {'\u1fe0', '\u1fec'}, {'\u1ff2', '\u1ff4'}, {'\u1ff6', '\u1ffc'}, {'\u203f', '\u2040'}, {'\u207f', '\u207f'}, {'\u2102', '\u2102'}, {'\u2107', '\u2107'}, {'\u210a', '\u2113'}, {'\u2115', '\u2115'}, {'\u2118', '\u211d'}, {'\u2124', '\u2124'}, {'\u2126', '\u2126'}, {'\u2128', '\u2128'}, {'\u212a', '\u2131'}, {'\u2133', '\u2138'}, {'\u2160', '\u2182'}, {'\u3005', '\u3007'}, {'\u3021', '\u3029'}, {'\u3041', '\u3093'}, {'\u309b', '\u309c'}, {'\u30a1', '\u30f6'}, {'\u30fb', '\u30fc'}, {'\u3105', '\u312c'}, {'\u4e00', '\u9fa5'}, {'\uac00', '\ud7a3'}};
    private static final int[] initializer_1 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 255, 255};
    static Slice<CharSlice> isUniAlphaALPHA_TABLE = slice(initializer_0);
    static BytePtr utf_decodeCharUTF8_DECODE_OK = null;
    static BytePtr utf_decodeCharUTF8_DECODE_OUTSIDE_CODE_SPACE = new BytePtr("Outside Unicode code space");
    static BytePtr utf_decodeCharUTF8_DECODE_TRUNCATED_SEQUENCE = new BytePtr("Truncated UTF-8 sequence");
    static BytePtr utf_decodeCharUTF8_DECODE_OVERLONG = new BytePtr("Overlong UTF-8 sequence");
    static BytePtr utf_decodeCharUTF8_DECODE_INVALID_TRAILER = new BytePtr("Invalid trailing code unit");
    static BytePtr utf_decodeCharUTF8_DECODE_INVALID_CODE_POINT = new BytePtr("Invalid code point decoded");
    static IntSlice utf_decodeCharUTF8_STRIDE = slice(initializer_1);
    static BytePtr utf_decodeWcharUTF16_DECODE_OK = null;
    static BytePtr utf_decodeWcharUTF16_DECODE_TRUNCATED_SEQUENCE = new BytePtr("Truncated UTF-16 sequence");
    static BytePtr utf_decodeWcharUTF16_DECODE_INVALID_SURROGATE = new BytePtr("Invalid low surrogate");
    static BytePtr utf_decodeWcharUTF16_DECODE_UNPAIRED_SURROGATE = new BytePtr("Unpaired surrogate");
    static BytePtr utf_decodeWcharUTF16_DECODE_INVALID_CODE_POINT = new BytePtr("Invalid code point decoded");

    public static boolean utf_isValidDchar(int c) {
        if (c < 55296)
            return true;
        if (c > 57343 && c <= 1114111)
            return true;
        return false;
    }

    public static boolean isUniAlpha(int c) {
        Slice<CharSlice> ALPHA_TABLE = utf.isUniAlphaALPHA_TABLE;
        int high = 244;
        int low = c < (int)ALPHA_TABLE.get(0).get(0) || (int)ALPHA_TABLE.get(high).get(1) < c ? high + 1 : 0;
        for (; low <= high;){
            int mid = low + high >> 1;
            if (c < (int)ALPHA_TABLE.get(mid).get(0))
                high = mid - 1;
            else if ((int)ALPHA_TABLE.get(mid).get(1) < c)
                low = mid + 1;
            else
            {
                assert((int)ALPHA_TABLE.get(mid).get(0) <= c && c <= (int)ALPHA_TABLE.get(mid).get(1));
                return true;
            }
        }
        return false;
    }

    public static int utf_codeLengthChar(int c) {
        if (c <= 127)
            return 1;
        if (c <= 2047)
            return 2;
        if (c <= 65535)
            return 3;
        if (c <= 1114111)
            return 4;
        throw new AssertionError("Unreachable code!");
    }

    public static int utf_codeLengthWchar(int c) {
        return c <= 65535 ? 1 : 2;
    }

    public static int utf_codeLength(int sz, int c) {
        if (sz == 1)
            return utf_codeLengthChar(c);
        if (sz == 2)
            return utf_codeLengthWchar(c);
        assert(sz == 4);
        return 1;
    }

    public static void utf_encodeChar(BytePtr s, int c) {
        assert(s != null);
        assert(utf_isValidDchar(c));
        if (c <= 127)
        {
            s.set(0, (byte)c);
        }
        else if (c <= 2047)
        {
            s.set(0, (byte)(192 | c >> 6));
            s.set(1, (byte)(128 | c & 63));
        }
        else if (c <= 65535)
        {
            s.set(0, (byte)(224 | c >> 12));
            s.set(1, (byte)(128 | c >> 6 & 63));
            s.set(2, (byte)(128 | c & 63));
        }
        else if (c <= 1114111)
        {
            s.set(0, (byte)(240 | c >> 18));
            s.set(1, (byte)(128 | c >> 12 & 63));
            s.set(2, (byte)(128 | c >> 6 & 63));
            s.set(3, (byte)(128 | c & 63));
        }
        else
            throw new AssertionError("Unreachable code!");
    }

    public static void utf_encodeWchar(CharPtr s, int c) {
        assert(s != null);
        assert(utf_isValidDchar(c));
        if (c <= 65535)
        {
            s.set(0, (char)c);
        }
        else
        {
            s.set(0, (char)((c - 65536 >> 10 & 1023) + 55296));
            s.set(1, (char)((c - 65536 & 1023) + 56320));
        }
    }

    public static void utf_encode(int sz, Object s, int c) {
        if (sz == 1)
            utf_encodeChar(s.toBytePtr(), c);
        else if (sz == 2)
            utf_encodeWchar(s.toCharPtr(), c);
        else
        {
            assert(sz == 4);
            (s.toIntPtr()).set(0, c);
        }
    }

    public static BytePtr utf_decodeChar(BytePtr s, int len, IntRef ridx, IntRef rresult) {
        rresult.value = '\uffff';
        BytePtr UTF8_DECODE_OK = utf.utf_decodeCharUTF8_DECODE_OK;
        BytePtr UTF8_DECODE_OUTSIDE_CODE_SPACE = utf.utf_decodeCharUTF8_DECODE_OUTSIDE_CODE_SPACE;
        BytePtr UTF8_DECODE_TRUNCATED_SEQUENCE = utf.utf_decodeCharUTF8_DECODE_TRUNCATED_SEQUENCE;
        BytePtr UTF8_DECODE_OVERLONG = utf.utf_decodeCharUTF8_DECODE_OVERLONG;
        BytePtr UTF8_DECODE_INVALID_TRAILER = utf.utf_decodeCharUTF8_DECODE_INVALID_TRAILER;
        BytePtr UTF8_DECODE_INVALID_CODE_POINT = utf.utf_decodeCharUTF8_DECODE_INVALID_CODE_POINT;
        IntSlice UTF8_STRIDE = utf.utf_decodeCharUTF8_STRIDE;
        assert(s != null);
        int i = ridx.value++;
        assert(i < len);
        byte u = s.get(i);
        rresult.value = u;
        int n = UTF8_STRIDE.get((int)u);
        switch (n)
        {
            case 1:
                return UTF8_DECODE_OK;
            case 2:
            case 3:
            case 4:
                break;
            default:
            {
                return new BytePtr("Outside Unicode code space");
            }
        }
        if (len < i + n)
            return new BytePtr("Truncated UTF-8 sequence");
        int c = (u & ((byte)1 << ((byte)7 - n)) - (byte)1);
        byte u2 = s.get(i += 1);
        if ((u & (byte)254) == (byte)192 || u == (byte)224 && (u2 & (byte)224) == (byte)128 || u == (byte)240 && (u2 & (byte)240) == (byte)128 || u == (byte)248 && (u2 & (byte)248) == (byte)128 || u == (byte)252 && (u2 & (byte)252) == (byte)128)
            return new BytePtr("Overlong UTF-8 sequence");
        {
            n += i - 1;
            for (; i != n;i += 1){
                u = s.get(i);
                if ((u & (byte)192) != (byte)128)
                    return new BytePtr("Invalid trailing code unit");
                c = (c << 6 | (u & (byte)63));
            }
        }
        if (!(utf_isValidDchar(c)))
            return new BytePtr("Invalid code point decoded");
        ridx.value = i;
        rresult.value = c;
        return UTF8_DECODE_OK;
    }

    public static BytePtr utf_decodeWchar(CharPtr s, int len, IntRef ridx, IntRef rresult) {
        rresult.value = '\uffff';
        BytePtr UTF16_DECODE_OK = utf.utf_decodeWcharUTF16_DECODE_OK;
        BytePtr UTF16_DECODE_TRUNCATED_SEQUENCE = utf.utf_decodeWcharUTF16_DECODE_TRUNCATED_SEQUENCE;
        BytePtr UTF16_DECODE_INVALID_SURROGATE = utf.utf_decodeWcharUTF16_DECODE_INVALID_SURROGATE;
        BytePtr UTF16_DECODE_UNPAIRED_SURROGATE = utf.utf_decodeWcharUTF16_DECODE_UNPAIRED_SURROGATE;
        BytePtr UTF16_DECODE_INVALID_CODE_POINT = utf.utf_decodeWcharUTF16_DECODE_INVALID_CODE_POINT;
        assert(s != null);
        int i = ridx.value++;
        assert(i < len);
        int u = rresult.value = (int)s.get(i);
        if (u < 55296)
            return UTF16_DECODE_OK;
        if (55296 <= u && u <= 56319)
        {
            if (len <= i + 1)
                return new BytePtr("Truncated UTF-16 sequence");
            char u2 = s.get(i + 1);
            if ((int)u2 < 56320 || 57343 < u)
                return new BytePtr("Invalid low surrogate");
            u = ((u - 55232 << 10) + ((int)u2 - 56320));
            ridx.value += 1;
        }
        else if (56320 <= u && u <= 57343)
            return new BytePtr("Unpaired surrogate");
        if (!(utf_isValidDchar(u)))
            return new BytePtr("Invalid code point decoded");
        rresult.value = u;
        return UTF16_DECODE_OK;
    }

}
