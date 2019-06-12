package org.dlang.dmd.root

import java.lang.StringBuilder

class Ptr<T>(val data: Array<T>, var offset: Int) : RootObject() {

    constructor(arr: Array<T>): this(arr, 0)

    operator fun plus(delta: Int) = Ptr(data, offset+delta)

    operator fun minus(delta: Int) = Ptr(data, offset-delta)

    operator fun plusAssign(delta: Int) {
        offset += delta
    }

    operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    fun deref(): T = data[offset]

    operator fun set(idx: Int, value: T) {
        data[offset+idx] = value
    }

    operator fun get(idx: Int): T = data[offset+idx]

    override fun toChars(): BytePtr  {
        val s = StringBuilder()
        s.append("[")
        for (i in offset until data.size) {
            if (i != 0)
                s.append(", ")
            s.append(data[i].toString())
        }
        s.append("]")
        return BytePtr(s.toString())
    }
}

// ~C-string
class BytePtr(val data: ByteArray, var offset: Int) : RootObject() {

    constructor(arr: ByteArray) : this(arr, 0)

    constructor(s: String) : this(s.toByteArray(), 0)

    constructor(n: Int): this(ByteArray(n))

    constructor(): this(ByteArray(0))

    operator fun plus(delta: Int) = BytePtr(data, offset+delta)

    operator fun minus(delta: Int) = BytePtr(data, offset-delta)

    operator fun plusAssign(delta: Int) {
        offset += delta
    }

    operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    operator fun set(idx: Int, value: Byte) {
        data[offset+idx] = value
    }

    operator fun get(idx: Int): Byte = data[offset+idx]

    fun slice(start: Int, end: Int) = ByteSlice(data, start + offset, end + offset)

    fun toBytePtr(): BytePtr = this

    fun toCharPtr(): CharPtr = ProxyCharPtr(this.data, offset)

    fun toIntPtr(): IntPtr = ProxyIntPtr(this.data, offset)

    override fun toChars(): BytePtr = this
}

interface CharPtr {
    operator fun plus(delta: Int): CharPtr

    operator fun minus(delta: Int): CharPtr

    operator fun plusAssign(delta: Int): Unit

    operator fun minusAssign(delta: Int): Unit

    operator fun set(idx: Int, value: Char): Unit

    operator fun get(idx: Int): Char
}

class WCharPtr(val data: CharArray, var offset: Int) : CharPtr{

    constructor(arr: CharArray) : this(arr, 0)

    constructor(s: String) : this(s.toCharArray(), 0)

    override operator fun plus(delta: Int) = WCharPtr(data, offset+delta)

    override operator fun minus(delta: Int) = WCharPtr(data, offset-delta)

    override operator fun plusAssign(delta: Int) {
        offset += delta
    }

    override operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    override operator fun set(idx: Int, value: Char) {
        data[offset+idx] = value
    }

    override operator fun get(idx: Int): Char = data[offset+idx]
}

class ProxyCharPtr(val data: ByteArray, var offset: Int): CharPtr {
    override operator fun plus(delta: Int) = ProxyCharPtr(data, offset + 2*delta)

    override operator fun minus(delta: Int) = ProxyCharPtr(data, offset - 2*delta)

    override operator fun plusAssign(delta: Int) {
        offset += 2*delta
    }

    override operator fun minusAssign(delta: Int) {
        offset -= 2*delta
    }

    override operator fun set(idx: Int, value: Char) {
        data[offset + 2*idx] = (value.toInt() and 0xFF).toByte()
        data[offset + 2*idx + 1] = (value.toInt() shr 8).toByte()
    }

    override operator fun get(idx: Int): Char = (data[offset + 2*idx] + (data[offset + 2*idx + 1].toInt() shl 8)).toChar()
}

interface IntPtr {
    operator fun plus(delta: Int): IntPtr

    operator fun minus(delta: Int): IntPtr

    operator fun plusAssign(delta: Int): Unit

    operator fun minusAssign(delta: Int): Unit

    operator fun set(idx: Int, value: Int): Unit

    operator fun get(idx: Int): Int
}

class IntPtrReal(val data: IntArray, var offset: Int) : IntPtr {

    constructor(arr: IntArray) : this(arr, 0)

    override operator fun plus(delta: Int) = IntPtrReal(data, offset+delta)

    override operator fun minus(delta: Int) = IntPtrReal(data, offset-delta)

    override operator fun plusAssign(delta: Int) {
        offset += delta
    }

    override operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    override operator fun set(idx: Int, value: Int) {
        data[offset+idx] = value
    }

    override operator fun get(idx: Int): Int = data[offset+idx]
}

class ProxyIntPtr(val data: ByteArray, var offset: Int): IntPtr {
    override operator fun plus(delta: Int) = ProxyIntPtr(data, offset + 4*delta)

    override operator fun minus(delta: Int) = ProxyIntPtr(data, offset - 4*delta)

    override operator fun plusAssign(delta: Int) {
        offset += 4*delta
    }

    override operator fun minusAssign(delta: Int) {
        offset -= 4*delta
    }

    override operator fun set(idx: Int, value: Int) {
        data[offset + 4*idx] = (value.toInt() and 0xFF).toByte()
        data[offset + 4*idx + 1] = ((value.toInt() shr 8) and 0xFF).toByte()
        data[offset + 4*idx + 1] = ((value.toInt() shr 16) and 0xFF).toByte()
        data[offset + 4*idx + 1] = (value.toInt() shr 24).toByte()
    }

    override operator fun get(idx: Int): Int {
        val r = data[offset + 4*idx] + (data[offset + 4*idx + 1].toInt() shl 8) +
            (data[offset + 4*idx + 2].toInt() shl 16) + (data[offset + 4*idx + 3].toInt() shl 24)
        return r
    }
}