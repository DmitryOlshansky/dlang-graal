package org.dlang.dmd.root

import junit.framework.TestCase
import org.dlang.dmd.utils.toCStringThen

class TestUtils : TestCase() {

    fun cstring(str: String):BytePtr  {
        val bytes = str.toByteArray().copyOf(str.length + 1)
        bytes[str.length] = 0
        return BytePtr(bytes)
    }

    fun testToCStringThen(){
        assertTrue(toCStringThen({ memcmp(it, BytePtr("Hello world\u0000"), 12) == 0 }, ByteSlice("Hello world")))
        assertTrue(toCStringThen({ memcmp(it,  BytePtr("Hello world\u0000\u0000"), 13) == 0 }, ByteSlice("Hello world\u0000")))
        assertTrue(toCStringThen({ v -> memcmp(v, BytePtr("\u0000"), 1) == 0 }, ByteSlice()))
    }
}