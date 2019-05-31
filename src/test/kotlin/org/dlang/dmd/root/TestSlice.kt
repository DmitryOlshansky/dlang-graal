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
}