package org.dlang.dmd.root

import junit.framework.TestCase

class TestOutBuffer : TestCase() {

    fun check(expected: String, fmt: String, vararg args: Any?) {
        val outbuf = OutBuffer()
        val arr = arrayOfNulls<Any?>(args.size)
        args.copyInto(arr)
        outbuf.vprintf(ByteSlice(fmt), slice(arr))
        assertEquals(expected, outbuf.extractSlice().toString())

    }

    fun testEachSpecifier() {
        check("42", "%d", 42)
        check("67", "%o", 7 + 6*8)
        check("2a", "%llx", 42)
        check("1234567890", "%ld", 1234567890L)
        check("12cQAZ", "%s%s%c%s", 1, 2.toByte(), 'c', "QAZ")
        check("32.5", "%f", 32.5)
        check("1", "%ll", 1)
        check("A:%10", "A:%%%i", 10)
        check("_Slice_", "_%s_", ByteSlice("Slice123").slice(0, 5))
        check("_Ptr_", "_%s_", BytePtr("APtr").plus(1))
        check("   1", "%4d", 1)
        check(" 1", "%.2d", 1)
        check("           1", "%12d", 1)
        check("1111", "%.3d", 1111)
        check("Ptr<ABCDE> null", "%p %p", BytePtr("ABCDE"), null)
    }

    fun testMixtures() {
        check("AB_X", "%.*s_%s", 2, BytePtr("ABC"), "X")
        check("  ABABC", "%4s%.*s", "AB", 3, BytePtr("ABC"))
        check("   1  234   4.5", "%.4s%.*s%6s", 1, 5, ByteSlice("1234").slice(1), 4.5)
    }
}