package org.dlang.dmd

import org.dlang.dmd.root.BytePtr
import org.dlang.dmd.root.IntRef
import org.dlang.dmd.utf.isUniAlpha
import org.dlang.dmd.utf.utf_decodeChar
import org.junit.Assert.*
import org.junit.Test

class TestUtf {
    @Test
    fun utfDecode() {
        val text = BytePtr("Вышел зайчик погулять")
        val idx = IntRef(0)
        val dchar = IntRef(0)
        assertEquals( null, utf_decodeChar(text, text.data.size, idx, dchar))
        assertEquals(2, idx.value)
        assertEquals('В'.toInt(), dchar.value)

        assertEquals( null, utf_decodeChar(text, text.data.size, idx, dchar))
        assertEquals(4, idx.value)
        assertEquals('ы'.toInt(), dchar.value)

        while (idx.value < text.data.size) {
            assertEquals( null, utf_decodeChar(text, text.data.size, idx, dchar))
        }
        assert(idx.value == text.data.size)
    }

    @Test
    fun uniAlpha() {
        assertTrue(isUniAlpha('Ю'.toInt()))
        assertFalse(isUniAlpha('1'.toInt()))
    }

}