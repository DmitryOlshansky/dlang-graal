package org.dlang.dmd;

import junit.framework.TestCase;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import org.dlang.dmd.root.BytePtr;
import org.dlang.dmd.root.ByteSlice;
import org.dlang.dmd.root.Slice;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.dlang.dmd.globals.global;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.root.ShimsKt.*;

public class TestLexer extends TestCase {
    private static final byte[][] initializer_0 = {{(byte)0}, {(byte)39, (byte)0}, {(byte)39, (byte)26}, {(byte)123, (byte)123, (byte)113, (byte)123, (byte)0}, {(byte)255, (byte)0}, {(byte)255, (byte)128, (byte)0}, {(byte)255, (byte)255, (byte)0}, {(byte)255, (byte)255, (byte)0}, {(byte)120, (byte)34, (byte)26}};
    static Slice<ByteSlice> __unittest_L168_C1testcases = slice(initializer_0);

    public void test_0() {
        ByteSlice text =  new ByteSlice("int\u0000");
        errors.StderrDiagnosticReporter diagnosticReporter = new errors.StderrDiagnosticReporter(global.params.useDeprecated);
        lexer.Lexer lex1 = new lexer.Lexer(null, toBytePtr(text), 0, text.getLength(), false, false, diagnosticReporter);
        byte tok = tokens.TOK.reserved;
        tok = lex1.nextToken();
        assertEquals(133, (tok & 0xFF));
        tok = lex1.nextToken();
        assertEquals(11, tok);
        tok = lex1.nextToken();
        assertEquals(11, tok);
        tok = lex1.nextToken();
        assertEquals(11, tok);
    }

    public void test_1() {
        int errors = global.startGagging();
        {
            Slice<ByteSlice> list = __unittest_L168_C1testcases;
            for (int i = 0; i < list.getLength();i += 1) {
                ByteSlice testcase = list.get(i);
                errors.StderrDiagnosticReporter diagnosticReporter = new errors.StderrDiagnosticReporter(global.params.useDeprecated);
                lexer.Lexer lex2 = new lexer.Lexer(null, toBytePtr(testcase), 0, testcase.getLength() - 1, false, false, diagnosticReporter);
                byte tok = lex2.nextToken();
                int iterations = 1;
                for (; tok != 11 && iterations++ < testcase.getLength();){
                    tok = lex2.nextToken();
                }
                assertEquals(11, tok);
                tok = lex2.nextToken();
                assertEquals(11, tok);
            }
        }
        global.endGagging(errors);
    }

    public void test_2() {
        // from template test!(Byte)
        Function2<ByteSlice,Byte,Void> testByte = new Function2<ByteSlice,Byte,Void>(){
            public Void invoke(ByteSlice sequence, Byte expected){
                AssertDiagnosticReporter assertOnError = new AssertDiagnosticReporter();
                sequence.append((byte)0);
                BytePtr p = pcopy(sequence.ptr());
                lexer.Lexer lex = new lexer.Lexer(null, p, 0, sequence.getLength(), false, false, assertOnError);
                assertEquals((expected & 0xFF), lex.escapeSequence(globals.Loc.initial, assertOnError, p));
                assertEquals(lex.p.plus(1), toBytePtr((toBytePtr(sequence).plus(sequence.getLength()))));
                return null;
            }
        };

        // from template test!(Integer)
        Function2<ByteSlice,Integer,Void> testInteger = new Function2<ByteSlice,Integer,Void>(){
            public Void invoke(ByteSlice sequence, Integer expected){
                AssertDiagnosticReporter assertOnError = new AssertDiagnosticReporter();
                sequence.append((byte)0);
                BytePtr p = pcopy(toBytePtr(toBytePtr(sequence)));
                lexer.Lexer lex = new lexer.Lexer(null, sequence.ptr(), 0, sequence.getLength(), false, false, assertOnError);
                assertEquals((int) expected, lex.escapeSequence(globals.Loc.initial, assertOnError, p));
                assertEquals(lex.p.plus(1), toBytePtr((toBytePtr(sequence).plus(sequence.getLength()))));
                return null;
            }
        };

        // from template test!(Character)
        Function2<ByteSlice,Character,Void> testCharacter = new Function2<ByteSlice,Character,Void>(){
            public Void invoke(ByteSlice sequence, Character expected){
                AssertDiagnosticReporter assertOnError = new AssertDiagnosticReporter();
                sequence.append((byte)0);
                BytePtr p = pcopy(toBytePtr(toBytePtr(sequence)));
                lexer.Lexer lex = new lexer.Lexer(null, sequence.ptr(), 0, sequence.getLength(), false, false, assertOnError);
                assertEquals((int) expected, lex.escapeSequence(globals.Loc.initial, assertOnError, p));
                assertEquals(lex.p.plus(1), toBytePtr((toBytePtr(sequence).plus(sequence.getLength()))));
                return null;
            }
        };

        // from template test!(Integer)

        testByte.invoke( new ByteSlice("'"), (byte)39);
        testByte.invoke( new ByteSlice("\""), (byte)34);
        testByte.invoke( new ByteSlice("?"), (byte)63);
        testByte.invoke( new ByteSlice("\\"), (byte)92);
        testByte.invoke( new ByteSlice("0"), (byte)0);
        testByte.invoke( new ByteSlice("a"), (byte)7);
        testByte.invoke( new ByteSlice("b"), (byte)8);
        testByte.invoke( new ByteSlice("f"), (byte)12);
        testByte.invoke( new ByteSlice("n"), (byte)10);
        testByte.invoke( new ByteSlice("r"), (byte)13);
        testByte.invoke( new ByteSlice("t"), (byte)9);
        testByte.invoke( new ByteSlice("v"), (byte)11);
        testInteger.invoke( new ByteSlice("x00"), 0);
        testInteger.invoke( new ByteSlice("xff"), 255);
        testInteger.invoke( new ByteSlice("xFF"), 255);
        testInteger.invoke( new ByteSlice("xa7"), 167);
        testInteger.invoke( new ByteSlice("x3c"), 60);
        testInteger.invoke( new ByteSlice("xe2"), 226);
        testByte.invoke( new ByteSlice("1"), (byte)1);
        testByte.invoke( new ByteSlice("42"), (byte)34);
        testByte.invoke( new ByteSlice("357"), (byte)239);
        testCharacter.invoke( new ByteSlice("u1234"), '\u1234');
        testCharacter.invoke( new ByteSlice("uf0e4"), '\uf0e4');
        testInteger.invoke( new ByteSlice("U0001f603"), 0x1f603);
        testByte.invoke( new ByteSlice("&quot;"), (byte)34);
        testByte.invoke( new ByteSlice("&lt;"), (byte)60);
        testByte.invoke( new ByteSlice("&gt;"), (byte)62);
    }

    public void test_3() {
        Function4<ByteSlice,ByteSlice,Integer,Integer,Void> test = new Function4<ByteSlice,ByteSlice,Integer,Integer,Void>(){
            public Void invoke(ByteSlice sequence, ByteSlice expectedError, Integer expectedReturnValue, Integer expectedScanLength){
                ExpectDiagnosticReporter handler = new ExpectDiagnosticReporter(expectedError);
                sequence.append((byte)0);
                BytePtr p = pcopy(toBytePtr(toBytePtr(sequence)));
                lexer.Lexer lex = new lexer.Lexer(null, sequence.ptr(), 0, sequence.getLength(), false, false, handler);
                int actualReturnValue = lex.escapeSequence(globals.Loc.initial, handler, p);
                assertTrue(handler.gotError);
                assertEquals((int) expectedReturnValue, actualReturnValue);
                int actualScanLength = (lex.p.minus(toBytePtr(toBytePtr(sequence))));
                assertEquals((int) expectedScanLength, actualScanLength);
                return null;
            }
        };
        test.invoke( new ByteSlice("c"),  new ByteSlice("undefined escape sequence \\c"), 0x00063, 1);
        test.invoke( new ByteSlice("!"),  new ByteSlice("undefined escape sequence \\!"), 0x00021, 1);
        test.invoke( new ByteSlice("x1"),  new ByteSlice("escape hex sequence has 1 hex digits instead of 2"), 0x00001, 2);
        test.invoke( new ByteSlice("u1"),  new ByteSlice("escape hex sequence has 1 hex digits instead of 4"), 0x00001, 2);
        test.invoke( new ByteSlice("u12"),  new ByteSlice("escape hex sequence has 2 hex digits instead of 4"), 0x00012, 3);
        test.invoke( new ByteSlice("u123"),  new ByteSlice("escape hex sequence has 3 hex digits instead of 4"), 0x00123, 4);
        test.invoke( new ByteSlice("U0"),  new ByteSlice("escape hex sequence has 1 hex digits instead of 8"), 0x00000, 2);
        test.invoke( new ByteSlice("U00"),  new ByteSlice("escape hex sequence has 2 hex digits instead of 8"), 0x00000, 3);
        test.invoke( new ByteSlice("U000"),  new ByteSlice("escape hex sequence has 3 hex digits instead of 8"), 0x00000, 4);
        test.invoke( new ByteSlice("U0000"),  new ByteSlice("escape hex sequence has 4 hex digits instead of 8"), 0x00000, 5);
        test.invoke( new ByteSlice("U0001f"),  new ByteSlice("escape hex sequence has 5 hex digits instead of 8"), 0x0001f, 6);
        test.invoke( new ByteSlice("U0001f6"),  new ByteSlice("escape hex sequence has 6 hex digits instead of 8"), 0x001f6, 7);
        test.invoke( new ByteSlice("U0001f60"),  new ByteSlice("escape hex sequence has 7 hex digits instead of 8"), 0x01f60, 8);
        test.invoke( new ByteSlice("ud800"),  new ByteSlice("invalid UTF character \\U0000d800"), 0x0003f, 5);
        test.invoke( new ByteSlice("udfff"),  new ByteSlice("invalid UTF character \\U0000dfff"), 0x0003f, 5);
        test.invoke( new ByteSlice("U00110000"),  new ByteSlice("invalid UTF character \\U00110000"), 0x0003f, 9);
        test.invoke( new ByteSlice("xg0"),  new ByteSlice("undefined escape hex sequence \\xg"), 0x00067, 2);
        test.invoke( new ByteSlice("ug000"),  new ByteSlice("undefined escape hex sequence \\ug"), 0x00067, 2);
        test.invoke( new ByteSlice("Ug0000000"),  new ByteSlice("undefined escape hex sequence \\Ug"), 0x00067, 2);
        test.invoke( new ByteSlice("&BAD;"),  new ByteSlice("unnamed character entity &BAD;"), 0x0003f, 5);
        test.invoke( new ByteSlice("&quot"),  new ByteSlice("unterminated named entity &quot;"), 0x0003f, 5);
        test.invoke( new ByteSlice("400"),  new ByteSlice("escape octal sequence \\400 is larger than \\377"), 0x00100, 3);
    }

    static void testCase(String data, byte[] toks, Object[] values) {
        ByteSlice slice = new ByteSlice(data).append((byte)0);
        AssertDiagnosticReporter assertOnError = new AssertDiagnosticReporter();
        lexer.Lexer lex = new lexer.Lexer(null, slice.ptr(), 0, slice.getLength(), false, false, assertOnError);
        int i = 0;
        while (lex.nextToken() != 11) {
            assertEquals(toks[i], lex.token.value);
            if (lex.token.value == TOK.int32Literal || lex.token.value == TOK.wcharLiteral)
                assertEquals(values[i], lex.token.intvalue);
            if (lex.token.value == TOK.identifier)
                assertEquals(values[i], lex.token.ident.name.toString());
            i++;

        }
    }

    public void testNumbers() {
        testCase("42", new byte[]{TOK.int32Literal}, new Object[]{ 42L });
        testCase("1 + a", new byte[]{TOK.int32Literal, TOK.add, TOK.identifier}, new Object[]{ 1L, null, "a" });
        testCase("'０'", new byte[]{TOK.wcharLiteral}, new Object[]{ 65296L });
    }

    public void testMain() {
        testCase("void main(){}",
            new byte[]{TOK.void_, TOK.identifier, TOK.leftParentheses, TOK.rightParentheses, TOK.leftCurly, TOK.rightCurly},
            new Object[]{ null, "main", null, null, null, null}
        );
    }

    public void testRegression1() {
        testCase("0x0FFF_FFFF_FFFF_FFFFUL",
                new byte[]{ TOK.uns64Literal},
                new Object[]{0xFFF_FFFF_FFFFL}
        );
    }

    public void testRegression2() {
        testCase("18446744073709551615u",
                new byte[]{ TOK.uns64Literal},
                new Object[]{ -1L }
        );
    }

}
