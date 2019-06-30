package org.dlang.dmd.root

import junit.framework.TestCase
import org.dlang.dmd.tokens

class TestShims : TestCase() {

    fun testStrChr() {
        val p = BytePtr("ABCDEFG")
        val c = 'B'.toInt()
        assertEquals("BCDEFG", strchr(p, c)?.toString())
        val c2 = 'A'.toInt()
        assertEquals("ABCDEFG", strchr(p, c2)?.toString())
        val c3 = 'G'.toInt()
        assertEquals("G", strchr(p, c3)?.toString())
        val c4 = 'X'.toInt()
        assertEquals(null, strchr(p, c4)?.toString())
    }

    fun testStrStr() {
        val p = BytePtr("Hello, world!")
        val n1 = ByteSlice("ell")
        assertEquals("ello, world!", strstr(p, n1)?.toString())
        val n2 = ByteSlice("He")
        assertEquals("Hello, world!", strstr(p, n2)?.toString())
        val n3 = BytePtr("!")
        assertEquals("!", strstr(p, n3)?.toString())
        val n4 = BytePtr("World")
        assertEquals(null, strstr(p, n4)?.toString())
    }

    fun testRefPtr() {
        val r = ptr(ref<tokens.Token>(null))
        val t: tokens.Token? = null
        assertTrue(r.get() == t)
    }
}