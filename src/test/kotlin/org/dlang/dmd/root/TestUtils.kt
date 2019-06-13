package org.dlang.dmd.root

import junit.framework.TestCase
import org.dlang.dmd.*
import org.junit.Assert

class TestUtils : TestCase() {

    fun cstring(str: String):BytePtr  {
        val bytes = str.toByteArray().copyOf(str.length + 1)
        bytes[str.length] = 0
        return BytePtr(bytes)
    }

    fun testToCStringThen(){
        assertTrue(toCStringThen({ it == BytePtr("Hello world\u0000") }, ByteSlice("Hello world"))
        assertTrue(toCStringThen({ it ==  BytePtr("Hello world\u0000\u0000"), ByteSlice("Hello world\u0000"))
        //assertTrue(null.toCStringThen!((v) => v == "\0"));
    }
}