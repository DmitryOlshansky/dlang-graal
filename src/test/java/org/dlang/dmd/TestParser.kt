package org.dlang.dmd

import junit.framework.TestCase
import org.dlang.dmd.globals.global
import org.dlang.dmd.root.ByteSlice

enum class Kind {
    function,
    variable,
    struct,
    clazz
}

data class Decl(val kind: Kind, val id: String)

class TestParser : TestCase() {

    fun testCase(data: String, expected: Array<Decl>) {
        val slice = ByteSlice(data).append(0.toByte())
        val reporter = errors.StderrDiagnosticReporter()
        global.params.isLinux = true
        globals.global._init()
        astbase.ASTBase.Type._init()
        val parser = parse.ParserASTBase(null, slice, false, reporter)
        parser.nextToken()
        val decls = parser.parseModule()
        assertEquals(expected.size, decls.length)
        for (i in 0 until expected.size) {
            when (expected[i].kind) {
                Kind.function ->
                    assertTrue(decls[i]?.isFuncDeclaration() != null)
                Kind.variable ->
                    assertTrue(decls[i]?.isVarDeclaration() != null)
                Kind.struct ->
                    assertTrue(decls[i]?.isAggregateDeclaration() != null)
                Kind.clazz ->
                    assertTrue(decls[i]?.isClassDeclaration() != null)
            }
            assertEquals(expected[i].id, decls[i]?.ident.toString())
        }
    }

    fun testMain() {
        testCase("void main(){} int a;", arrayOf(Decl(Kind.function, "main"), Decl(Kind.variable, "a")))
    }

    fun testAggregates() {
        testCase("struct A { int a; } class C { A a; }", arrayOf(Decl(Kind.struct, "A"), Decl(Kind.clazz, "C")))
    }
}