package org.dlang.dmd.root

import junit.framework.TestCase

class TestStringTable : TestCase() {
    fun testUpdate(){
        val table = StringTable()
        val value = table.update(ByteSlice("test"))
        val name = ByteSlice(byteArrayOf('o'.toByte(), 't'.toByte(), 'e'.toByte(), 's'.toByte(), 't'.toByte()), 1, 5)
        value.ptrvalue = "Hello"
        assertEquals("Hello", table.update(name).ptrvalue)
    }
}