package org.dlang.dmd

import junit.framework.TestCase
import org.dlang.dmd.globals.global
import org.dlang.dmd.root.ByteSlice

class TestParser : TestCase() {

    fun testCase(data: String) {
        val slice = ByteSlice(data).append(0.toByte())
        val reporter = errors.StderrDiagnosticReporter()
        global.params.isLinux = true
        globals.global._init()
        astbase.ASTBase.Type._init()
        val parser = parse.ParserASTBase(null, slice, false, reporter)
        parser.nextToken()
        println(parser.parseModule())
    }

    fun testMain() {
        testCase("void main(){}")
    }
}