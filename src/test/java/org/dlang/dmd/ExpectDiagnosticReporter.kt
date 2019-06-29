package org.dlang.dmd

import junit.framework.TestCase.assertEquals
import org.dlang.dmd.root.*

private class ExpectDiagnosticReporter(expected: ByteSlice) : errors.DiagnosticReporter() {
    private val expected: ByteSlice
    @JvmField var gotError: Boolean = false

    init {
        this.expected = expected.copy()
    }

    override fun errorCount(): Int {
        throw AssertionError("Unreachable code!")
    }

    override fun warningCount(): Int {
        throw AssertionError("Unreachable code!")
    }

    override fun deprecationCount(): Int {
        throw AssertionError("Unreachable code!")
    }

    override fun error(loc: globals.Loc, format: BytePtr, args: Slice<Any>) {
        this.gotError = true
        val buffer = ByteSlice(ByteArray(100))
        val actual = buffer.slice(0, vsprintf(ptr(buffer), format, args)).copy()
        assertEquals(this.expected, actual)
    }

    override fun errorSupplemental(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
        throw AssertionError("Unreachable code!")
    }

    override fun warning(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
        throw AssertionError("Unreachable code!")
    }

    override fun warningSupplemental(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
        throw AssertionError("Unreachable code!")
    }

    override fun deprecation(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
        throw AssertionError("Unreachable code!")
    }

    override fun deprecationSupplemental(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
        throw AssertionError("Unreachable code!")
    }

    override fun copy(): errors.DiagnosticReporter {
        return this
    }

}