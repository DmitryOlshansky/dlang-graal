package org.dlang.dmd.root

import junit.framework.TestCase


class TestSlice : TestCase() {
    fun testStripLeadingNewLine() {
        assertEquals(stripLeadingLineTerminator(ByteSlice("\nfoo")), ByteSlice("foo"))
        assertEquals(stripLeadingLineTerminator(ByteSlice("\u000Bfoo")), ByteSlice("foo"))
        assertEquals(stripLeadingLineTerminator(ByteSlice("\u000Cfoo")), ByteSlice("foo"))
        assertEquals(stripLeadingLineTerminator(ByteSlice("\rfoo")), ByteSlice("foo"))
        assertEquals(stripLeadingLineTerminator(ByteSlice("\u0085foo")), ByteSlice("foo"))
        assertEquals(stripLeadingLineTerminator(ByteSlice("\u2028foo")), ByteSlice("foo"))
        assertEquals(stripLeadingLineTerminator(ByteSlice("\u2029foo")), ByteSlice("foo"))
        assertEquals(stripLeadingLineTerminator(ByteSlice("\n\rfoo")), ByteSlice("foo"))
    }
    fun testReverse() {
        val a1 = Slice(arrayOf<Int>(), 0, 0)
        assertEquals(reverse(a1), Slice(arrayOf<Int>()))
        val a2 = Slice(arrayOf(2))
        assertEquals(reverse(a2), Slice(arrayOf(2)))
        val a3 = Slice(arrayOf(2,3))
        assertEquals(reverse(a3), Slice(arrayOf(3,2)))
        val a4 = Slice(arrayOf(2, 3, 4))
        assertEquals(reverse(a4), Slice(arrayOf(4, 3, 2)))
        val a5 = Slice(arrayOf(2,3,4,5))
        assertEquals(reverse(a5), Slice(arrayOf(5,4,3,2)))
    }

}