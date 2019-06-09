package org.dlang.dmd.root

class Ptr<T>(val data: Array<T>, var offset: Int) {

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

}

// ~C-string
class BytePtr(val data: ByteArray, var offset: Int) {

    constructor(arr: ByteArray) : this(arr, 0)

    constructor(s: String) : this(s.toByteArray(), 0)

    operator fun plus(delta: Int) = BytePtr(data, offset+delta)

    operator fun minus(delta: Int) = BytePtr(data, offset-delta)

    operator fun plusAssign(delta: Int) {
        offset += delta
    }

    operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    fun deref(): Byte = data[offset]

    operator fun set(idx: Int, value: Byte) {
        data[offset+idx] = value
    }

    operator fun get(idx: Int): Byte = data[offset+idx]
}

class CharPtr(val data: CharArray, var offset: Int) {

    constructor(arr: CharArray) : this(arr, 0)

    constructor(s: String) : this(s.toCharArray(), 0)

    operator fun plus(delta: Int) = CharPtr(data, offset+delta)

    operator fun minus(delta: Int) = CharPtr(data, offset-delta)

    operator fun plusAssign(delta: Int) {
        offset += delta
    }

    operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    fun deref(): Char = data[offset]

    operator fun set(idx: Int, value: Char) {
        data[offset+idx] = value
    }

    operator fun get(idx: Int): Char = data[offset+idx]
}

class IntPtr(val data: IntArray, var offset: Int) {

    constructor(arr: IntArray) : this(arr, 0)

    operator fun plus(delta: Int) = IntPtr(data, offset+delta)

    operator fun minus(delta: Int) = IntPtr(data, offset-delta)

    operator fun plusAssign(delta: Int) {
        offset += delta
    }

    operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    fun deref(): Int = data[offset]

    operator fun set(idx: Int, value: Int) {
        data[offset+idx] = value
    }

    operator fun get(idx: Int): Int = data[offset+idx]
}