package org.dlang.dmd.root

enum class DYNCAST {
    _object,
    expression,
    dsymbol,
    type,
    identifier,
    tuple,
    parameter,
    statement,
    condition,
    templateparameter
}

abstract class RootObject {
    abstract fun toChars(): BytePtr

    open fun asString(): ByteSlice {
        val ptr = toChars()
        return ByteSlice(ptr.data, ptr.offset, strlen(ptr))
    }

    override fun toString(): String {
        val slice = asString()
        return String(slice.data, slice.beg, slice.length)
    }

    fun toBuffer(buf: OutBuffer) = {
        buf.writestring(toChars())
    }

    open fun dyncast() = DYNCAST._object
}