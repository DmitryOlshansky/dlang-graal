package org.dlang.dmd;

import junit.framework.TestCase;
import org.dlang.dmd.root.ByteSlice;
import org.dlang.dmd.root.Slice;

import static org.dlang.dmd.globals.global;
import static org.dlang.dmd.root.ShimsKt.toBytePtr;

public class TestLexer extends TestCase {

    public void test_0() {
        ByteSlice text =  new ByteSlice("int\u0000");
        errors.StderrDiagnosticReporter diagnosticReporter = new errors.StderrDiagnosticReporter(global.params.useDeprecated);
        lexer.Lexer lex1 = new lexer.Lexer(null, toBytePtr(text), 0, text.getLength(), false, false, diagnosticReporter);
        byte tok = tokens.TOK.reserved;
        tok = lex1.nextToken();
        assertEquals(133, (tok & 0xFF));
        tok = lex1.nextToken();
        assertTrue(tok == 11);
        tok = lex1.nextToken();
        assertTrue(tok == 11);
        tok = lex1.nextToken();
        assertTrue(tok == 11);
    }

    public void test_1() {
        int errors = global.startGagging();
        {
            Slice<ByteSlice> list = lexer.__unittest_L168_C1testcases;
            for (int i = 0; i < list.getLength();i += 1) {
                System.out.printf("Testcase %d: %s\n", i, list.get(i).toString());
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
}
