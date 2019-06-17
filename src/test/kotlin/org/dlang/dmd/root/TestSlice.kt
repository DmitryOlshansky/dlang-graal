package org.dlang.dmd.root

import junit.framework.TestCase
import org.dlang.dmd.root.stripLeadingLineTerminator as spl

class TestSlice : TestCase() {
    fun testStripLeadingNewLine() {
        assertEquals(spl(ByteSlice("\nfoo")), ByteSlice("foo"))
        assertEquals(spl(ByteSlice("\u000Bfoo")), ByteSlice("foo"))
        assertEquals(spl(ByteSlice("\u000Cfoo")), ByteSlice("foo"))
        assertEquals(spl(ByteSlice("\rfoo")), ByteSlice("foo"))
        assertEquals(spl(ByteSlice("\u0085foo")), ByteSlice("foo"))
        assertEquals(spl(ByteSlice("\u2028foo")), ByteSlice("foo"))
        assertEquals(spl(ByteSlice("\u2029foo")), ByteSlice("foo"))
        assertEquals(spl(ByteSlice("\n\rfoo")), ByteSlice("foo"))
    }
    fun testReverse() {
        val a1 = Slice(arrayOf<Int?>(), 0, 0)
        assertEquals(reverse(a1), Slice(arrayOf<Int?>()))
        val a2 = Slice(arrayOf<Int?>(2))
        assertEquals(reverse(a2), Slice(arrayOf<Int?>(2)))
        val a3 = Slice(arrayOf<Int?>(2,3))
        assertEquals(reverse(a3), Slice(arrayOf<Int?>(3,2)))
        val a4 = Slice(arrayOf<Int?>(2, 3, 4))
        assertEquals(reverse(a4), Slice(arrayOf<Int?>(4, 3, 2)))
        val a5 = Slice(arrayOf<Int?>(2,3,4,5))
        assertEquals(reverse(a5), Slice(arrayOf<Int?>(5,4,3,2)))
    }

}