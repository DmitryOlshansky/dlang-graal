package org.dlang.dmd.root

import com.google.common.hash.Hashing
import java.lang.StringBuilder

interface Slice<T> {
    fun copy(): Slice<T>

    operator fun set(idx: Int, value: T?)

    operator fun get(idx: Int): T?

    fun ptr(): Ptr<T>

    fun slice(from:Int, to:Int): Slice<T>

    fun slice(from:Int): Slice<T>

    fun slice(): Slice<T>

    fun copyTo(dest: Slice<T>)

    fun append(next: T): Slice<T>

    var beg: Int

    var end: Int

    val length: Int
}

class RawSlice<T> (var data: Array<T?>, override var beg: Int, override var end: Int) : RootObject(), Slice<T> {

    constructor(arr: Array<T?>) : this(arr, 0, arr.size)

    constructor() : this(emptyArray<Any>() as Array<T?>, 0, 0)

    override fun copy() = RawSlice<T>(data, beg, end)

    override  operator fun set(idx: Int, value: T?) {
        data[beg+idx] = value
    }

    override operator fun get(idx: Int): T? = data[beg+idx]

    override fun ptr():Ptr<T> = RawPtr(data, beg)

    override fun slice(from:Int, to:Int): Slice<T> {
        return RawSlice(data, from+beg, to+beg)
    }

    override fun slice(from:Int): Slice<T> {
        return RawSlice(data, from+beg, end)
    }

    override fun slice(): Slice<T>  = RawSlice(data, beg, end)

    override fun copyTo(dest: Slice<T>) {
        require(dest.length == length)
        require(dest is RawSlice)
        data.copyInto(dest.data, dest.beg, beg, end)
    }

    override fun append(next: T): Slice<T> {
        // TODO: assumes full slice and realloc on every append
        data = data.copyOf(data.size + 1)
        data[data.size - 1] = next
        return this
    }

    override val length: Int
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

class RefSlice<T>(val ref: Ref<T>) : Slice<T> {

    override fun copy(): Slice<T> = RefSlice(ref)

    override fun set(idx: Int, value: T?) {
        require(idx == 0)
        ref.value = value
    }

    override fun get(idx: Int): T? {
        require(idx == 0)
        return ref.value
    }

    override fun ptr(): Ptr<T> = RefPtr(ref)

    override fun slice(from: Int, to: Int): Slice<T> {
        require(from == 0 && to == 1)
        return RefSlice(ref)
    }

    override fun slice(from: Int): Slice<T> {
        require(from == 0)
        return RefSlice(ref)
    }

    override fun slice(): Slice<T> {
        return RefSlice(ref)
    }

    override fun copyTo(dest: Slice<T>) {
        dest[0] = ref.value
    }

    override fun append(next: T): Slice<T> {
        val arr = Array<Any?>(2) { null}
        arr[0] = ref.value as Any
        arr[1] = next as Any
        return RawSlice(arr as Array<T?>)
    }

    override val length: Int
        get() = 1

    override var beg: Int
        get() = 0
        set(v: Int) = throw Exception("Unsupported beg property for RefSlice")

    override var end: Int
        get() = 0
        set(v: Int) = throw Exception("Unsupported end property for RefSlice")
}

class ByteSlice(var data: ByteArray, var beg: Int, var end: Int): RootObject() {

    constructor(): this(ByteArray(0))

    constructor(s: String) : this(s.toByteArray())

    constructor(arr: ByteArray) : this(arr, 0, arr.size)

    fun copy() = ByteSlice(data, beg, end)

    operator fun set(idx: Int, value: Byte) {
        data[beg+idx] = value
    }

    operator fun get(idx: Int): Byte = data[beg+idx]

    fun set(value: Byte) = data.fill(value, beg, end)

    fun ptr() =
        if (end == data.size) BytePtr(data, beg)
        else BytePtr(data.copyOfRange(beg, end))

    fun toBytePtr() = ptr()

    fun slice(from:Int, to:Int): ByteSlice {
        return ByteSlice(data, from+beg, to+beg)
    }

    fun slice(from:Int): ByteSlice {
        return ByteSlice(data, from+beg, end)
    }

    fun copyTo(dest: ByteSlice) {
        require(dest.length == length)
        data.copyInto(dest.data, dest.beg, beg, end)
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
        beg = beg
        end = end + 1
        return this
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
        val hasher = Hashing.goodFastHash(32)
        return hasher.hashBytes(data, beg, end - beg).asInt()
    }

    override fun toString(): String = String(data, beg, end - beg)

    override fun toChars(): BytePtr = ptr()

    fun toByteSlice() = this
}

class CharSlice(val data: CharArray, var beg: Int, var end: Int) : RootObject(), CharSequence {

    constructor(s: String) : this(s.toCharArray())

    constructor(arr: CharArray) : this(arr, 0, arr.size)

    fun copy() = CharSlice(data, beg, end)

    operator fun set(idx: Int, value: Char) {
        data[beg+idx] = value
    }

    override operator fun get(index: Int): Char = data[beg+index]

    fun ptr() = WCharPtr(data, beg)

    fun slice(from:Int, to:Int): CharSlice {
        return CharSlice(data, from+beg, to+beg)
    }

    fun slice(from:Int): CharSlice {
        return CharSlice(data, from+beg, end)
    }

    override val length: Int
        get() = end - beg

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = slice(startIndex, endIndex)

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
        val hasher = Hashing.goodFastHash(32)
        return hasher.hashUnencodedChars(this).asInt()
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

    fun copy() = IntSlice(data, beg, end)

    operator fun set(idx: Int, value: Int) {
        data[beg+idx] = value
    }

    operator fun get(idx: Int): Int = data[beg+idx]

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
        val hasher = Hashing.goodFastHash(32).newHasher()
        for (i in beg until  end) {
            hasher.putInt(data[i])
        }
        return hasher.hashCode()
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

typealias Strings = DArray<BytePtr>