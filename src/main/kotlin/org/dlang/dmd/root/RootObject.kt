package org.dlang.dmd.root

import java.lang.AssertionError

object DYNCAST {
    @JvmField val _object = 0
    @JvmField val expression = 1
    @JvmField val dsymbol = 2
    @JvmField val type = 3
    @JvmField val identifier = 4
    @JvmField val tuple = 5
    @JvmField val parameter = 6
    @JvmField val statement = 7
    @JvmField val condition = 8
    @JvmField val templateparameter = 9
}

abstract class RootObject {
    open fun toChars(): BytePtr { throw AssertionError("unimplemented toChars") }

    open fun asString(): ByteSlice {
        val ptr = toChars()
        return ByteSlice(ptr.data, ptr.offset, strlen(ptr))
    }

    override fun toString(): String {
        val slice = asString()
        return String(slice.data, slice.beg, slice.length)
    }

    fun toBuffer(buf: OutBuffer) {
        buf.writestring(toChars())
    }

    open fun dyncast() = DYNCAST._object
}