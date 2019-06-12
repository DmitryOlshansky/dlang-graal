package org.dlang.dmd.root

import java.lang.StringBuilder
import java.util.*

class Slice<T> (val data: Array<T>, var beg: Int, var end: Int) : RootObject() {

    constructor(arr: Array<T>) : this(arr, 0, arr.size)

    operator fun set(idx: Int, value: T) {
        data[beg+idx] = value
    }

    operator fun get(idx: Int): T = data[beg+idx]

    fun ptr() = Ptr(data, beg)

    fun slice(from:Int, to:Int): Slice<T> {
        return Slice(data, from+beg, to+beg)
    }

    fun slice(from:Int): Slice<T> {
        return Slice(data, from+beg, end)
    }

    fun slice(): Slice<T>  = Slice(data, beg, end)

    val length: Int
        get() = end - beg

    private fun isEqualTo(other: Slice<*>): Boolean {
        if (length != other.length) return false
        for (i in 0 until length) {
            if ((this[i] == null).xor(other[i] == null)) return false
            if (this[i] == null && other[i] == null) continue
            if (!this[i]!!.equals(other[i])) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is Slice<*> -> isEqualTo(other)
            else -> false
        }

    override fun hashCode(): Int {
        return data.hashCode() + 17*beg + 31*end
    }

    override fun toChars(): BytePtr  {
        val s = StringBuilder()
        s.append("[")
        for (i in beg until end) {
            if (i != 0)
                s.append(", ")
            s.append(data[i].toString())
        }
        s.append("]")
        return BytePtr(s.toString())
    }
}

class ByteSlice(var data: ByteArray, var beg: Int, var end: Int): RootObject() {

    constructor(): this(ByteArray(0))

    constructor(s: String) : this(s.toByteArray())

    constructor(arr: ByteArray) : this(arr, 0, arr.size)

    operator fun set(idx: Int, value: Byte) {
        data[beg+idx] = value
    }

    operator fun get(idx: Int): Byte = data[beg+idx]

    fun set(value: Byte) = data.fill(value, beg, end)

    fun ptr() = BytePtr(data, beg)

    fun toBytePtr() = ptr()

    fun slice(from:Int, to:Int): ByteSlice {
        return ByteSlice(data, from+beg, to+beg)
    }

    fun slice(from:Int): ByteSlice {
        return ByteSlice(data, from+beg, end)
    }

    fun append(next: ByteSlice): ByteSlice {
        val arr = ByteArray(length + next.length)
        data.copyInto(arr, 0, beg, end)
        next.data.copyInto(arr, length, next.beg, next.end)
        data = arr
        return this
    }

    fun append(next: Byte): ByteSlice {
        // TODO: assumes full slice and realloc on every append
        data = data.copyOf(data.size + 1)
        data[data.size - 1] = next
        return this
    }

    fun concat(next: ByteSlice): ByteSlice {
        val arr = ByteArray(length + next.length)
        data.copyInto(arr, 0, beg, end)
        next.data.copyInto(arr, length, next.beg, next.end)
        return ByteSlice(arr)
    }

    val length: Int
    get() = end - beg

    private fun isEqualTo(other: ByteSlice): Boolean {
        if (length != other.length) return false
        for (i in 0 until length) {
            if (this[i] != other[i]) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is ByteSlice -> isEqualTo(other)
            else -> false
        }

    override fun hashCode(): Int {
        return data.hashCode() + 17 * beg + 31 * end
    }

    override fun toString(): String = String(data, beg, end - beg)

    override fun toChars(): BytePtr = ptr()

    fun toByteSlice() = this
}

class CharSlice(val data: CharArray, var beg: Int, var end: Int) : RootObject() {
    constructor(s: String) : this(s.toCharArray())

    constructor(arr: CharArray) : this(arr, 0, arr.size)

    operator fun set(idx: Int, value: Char) {
        data[beg+idx] = value
    }

    operator fun get(idx: Int): Char = data[beg+idx]

    fun ptr() = WCharPtr(data, beg)

    fun slice(from:Int, to:Int): CharSlice {
        return CharSlice(data, from+beg, to+beg)
    }

    fun slice(from:Int): CharSlice {
        return CharSlice(data, from+beg, end)
    }

    val length: Int
        get() = end - beg

    private fun isEqualTo(other: CharSlice): Boolean {
        if (length != other.length) return false
        for (i in 0 until length) {
            if (this[i] != other[i]) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is CharSlice -> isEqualTo(other)
            else -> false
        }

    override fun hashCode(): Int {
        return data.hashCode() + 17 * beg + 31 * end
    }

    override fun toChars(): BytePtr  {
        val s = StringBuilder()
        s.append("[")
        for (i in beg until end) {
            if (i != 0)
                s.append(", ")
            s.append(data[i])
        }
        s.append("]")
        return BytePtr(s.toString())
    }

}

class IntSlice(val data: IntArray, var beg: Int, var end: Int) : RootObject() {

    constructor(arr: IntArray) : this(arr, 0, arr.size)

    operator fun set(idx: Int, value: Int) {
        data[beg+idx] = value
    }

    operator fun get(idx: Int): Int = data[beg+idx]

    fun ptr() = IntPtrReal(data, beg)

    fun slice(from:Int, to:Int): IntSlice {
        return IntSlice(data, from+beg, to+beg)
    }

    fun slice(from:Int): IntSlice {
        return IntSlice(data, from+beg, end)
    }

    val length: Int
        get() = end - beg

    private fun isEqualTo(other: IntSlice): Boolean {
        if (length != other.length) return false
        for (i in 0 until length) {
            if (this[i] != other[i]) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is IntSlice -> isEqualTo(other)
            else -> false
        }

    override fun hashCode(): Int {
        return data.hashCode() + 17 * beg + 31 * end
    }

    override fun toChars(): BytePtr  {
        val s = StringBuilder()
        s.append("[")
        for (i in beg until end) {
            if (i != 0)
                s.append(", ")
            s.append(data[i].toString())
        }
        s.append("]")
        return BytePtr(s.toString())
    }
}



/**
 * Strips one leading line terminator of the given string.
 *
 * The following are what the Unicode standard considers as line terminators:
 *
 * | Name                | D Escape Sequence | Unicode Code Point |
 * |---------------------|-------------------|--------------------|
 * | Line feed           | `\n`              | `U+000A`           |
 * | Line tabulation     | `\v`              | `U+000B`           |
 * | Form feed           | `\f`              | `U+000C`           |
 * | Carriage return     | `\r`              | `U+000D`           |
 * | Next line           |                   | `U+0085`           |
 * | Line separator      |                   | `U+2028`           |
 * | Paragraph separator |                   | `U+2029`           |
 *
 * This function will also strip `\n\r`.
 */
fun stripLeadingLineTerminator(str: ByteSlice): ByteSlice {
    val nextLine = ByteSlice(byteArrayOf(0xC2.toByte(), 0x85.toByte()))
    val lineSeparator = ByteSlice(byteArrayOf(0xE2.toByte(), 0x80.toByte(), 0xA8.toByte()))
    val paragraphSeparator = ByteSlice(byteArrayOf(0xE2.toByte(), 0x80.toByte(), 0xA9.toByte()))

    if (str.length == 0)
        return str

    when (str[0])
    {
        '\n'.toByte() ->
        {
            if (str.length >= 2 && str[1] == '\r'.toByte())
                return str.slice(2)
            else
                return str.slice(1)
        }
        0x0B.toByte(), 0x0C.toByte(), '\r'.toByte() -> return str.slice(1)

        nextLine[0] ->
        {
            if (str.length >= 2 && str.slice(0, 2) == nextLine)
                return str.slice(2)

            return str
        }

        lineSeparator[0] ->
        {
            if (str.length >= 3)
            {
                val prefix = str.slice(0, 3)

                if (prefix == lineSeparator || prefix == paragraphSeparator)
                    return str.slice(3)
            }

            return str
        }

        else -> return str
    }
}
