package org.dlang.dmd.root

import java.lang.Exception
import java.lang.StringBuilder

abstract class Ptr<T> : RootObject() {
    abstract fun copy(): Ptr<T>

    abstract operator fun set(idx: Int, value: T)

    abstract operator fun get(idx: Int): T?

    abstract fun get(): T?
}

class RawPtr<T>(val data: Array<T?>, var offset: Int) : Ptr<T>() {

    constructor(arr: Array<T?>): this(arr, 0)

    override fun copy() = RawPtr<T>(data, offset)

    operator fun inc(): RawPtr<T> {
        offset ++
        return this
    }

    fun postInc(): RawPtr<T> {
        val r = RawPtr<T>(data, offset)
        offset++
        return r
    }

    operator fun dec(): RawPtr<T> {
        offset--
        return this
    }

    fun postDec(): RawPtr<T> {
        val r = RawPtr<T>(data, offset)
        offset--
        return r
    }

    operator fun plus(delta: Int) = RawPtr(data, offset+delta)

    operator fun minus(delta: Int) = RawPtr(data, offset-delta)

    operator fun minus(rhs: RawPtr<T>): Int {
        require(rhs.data === data)
        return offset - rhs.offset
    }

    operator fun plusAssign(delta: Int) {
        offset += delta
    }

    operator fun minusAssign(delta: Int) {
        offset -= delta
    }

    override operator fun set(idx: Int, value: T) {
        data[offset+idx] = value
    }

    override operator fun get(idx: Int): T? = data[offset+idx]

    override fun get(): T? = data[offset]

    fun slice(start: Int, end: Int) = Slice(data, start + offset, end + offset)

    override fun equals(other: Any?): Boolean =
        when (other) {
            is RawPtr<*> -> data === (other as RawPtr<T>).data && offset == other.offset
            else -> false
        }

    override fun hashCode(): Int {
        return data.hashCode() + 31 * offset
    }

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

class RefPtr<T>(val ref: Ref<T>) : Ptr<T>() {

    override fun copy() = RefPtr(ref)

    override operator fun get(idx: Int): T {
        require(idx == 0)
        return ref.value
    }

    override fun get() = ref.value

    override fun set(idx: Int, value: T) {
        require(idx == 0)
        ref.value = value
    }

    override fun toChars(): BytePtr = BytePtr(ref.value.toString())
}

class BytePtrPtr(val ref: BytePtr) : Ptr<BytePtr>() {

    override fun copy() = BytePtrPtr(ref)

    override operator fun get(idx: Int): BytePtr {
        require(idx == 0)
        return ref
    }

    override fun get() = ref.copy()

    override fun set(idx: Int, value: BytePtr) {
        require(idx == 0)
        ref.data = value.data
        ref.offset = value.offset
    }

    override fun toChars(): BytePtr = ref.toChars()

    override fun equals(other: Any?): Boolean =
        when (other) {
            is BytePtrPtr -> ref === other.ref
            else -> false
        }

    override fun hashCode(): Int {
        return ref.hashCode()
    }
}


// ~C-string
class BytePtr(var data: ByteArray, var offset: Int) : RootObject() {

    constructor(arr: ByteArray) : this(arr, 0)

    constructor(s: String) : this(s.toByteArray(), 0)

    constructor(n: Int): this(ByteArray(n))

    constructor(): this(ByteArray(0))

    fun copy() = BytePtr(data, offset)

    operator fun inc():  BytePtr {
        offset ++
        return this
    }

    fun postInc(): BytePtr {
        val r = BytePtr(data, offset)
        offset++
        return r
    }

    operator fun dec(): BytePtr {
        offset--
        return this
    }

    fun postDec(): BytePtr {
        val r = BytePtr(data, offset)
        offset--
        return r
    }

    operator fun plus(delta: Int) = BytePtr(data, offset+delta)

    operator fun minus(delta: Int) = BytePtr(data, offset-delta)

    operator fun minus(rhs: BytePtr): Int {
        require(rhs.data === data)
        return offset - rhs.offset
    }

    fun plusAssign(delta: Int): BytePtr {
        offset += delta
        return this
    }

    fun minusAssign(delta: Int): BytePtr {
        offset -= delta
        return this
    }

    fun lessThan(ptr: BytePtr): Boolean {
        require(data === ptr.data)
        return offset < ptr.offset
    }

    fun lessOrEqual(ptr: BytePtr): Boolean {
        require(data === ptr.data)
        return offset <= ptr.offset
    }

    fun greaterThan(ptr: BytePtr): Boolean {
        require(data === ptr.data)
        return offset > ptr.offset
    }

    fun greaterOrEqual(ptr: BytePtr): Boolean {
        require(data === ptr.data)
        return offset >= ptr.offset
    }

    operator fun set(idx: Int, value: Byte) {
        data[offset+idx] = value
    }

    operator fun get(idx: Int): Byte = data[offset+idx]

    fun get() = data[offset]

    fun slice(start: Int, end: Int) = ByteSlice(data, start + offset, end + offset)

    fun toBytePtr(): BytePtr = this

    fun toCharPtr(): CharPtr = ProxyCharPtr(this.data, offset)

    fun toIntPtr(): IntPtr = ProxyIntPtr(this.data, offset)

    override fun toChars(): BytePtr = this

    override fun toString(): String = String(data, offset, strlen(this))

    override fun equals(other: Any?): Boolean =
        when (other) {
            is BytePtr -> data === other.data && offset == other.offset
            else -> false
        }

    override fun hashCode(): Int {
        return data.hashCode() + 31 * offset
    }
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
    fun copy(): IntPtr

    operator fun plus(delta: Int): IntPtr

    operator fun minus(delta: Int): IntPtr

    operator fun plusAssign(delta: Int): Unit

    operator fun minusAssign(delta: Int): Unit

    operator fun set(idx: Int, value: Int): Unit

    operator fun get(idx: Int): Int
}

class RawIntPtr(val data: IntArray, var offset: Int) : IntPtr {

    constructor(arr: IntArray) : this(arr, 0)

    override fun copy(): RawIntPtr = RawIntPtr(data, offset)

    override operator fun plus(delta: Int) = RawIntPtr(data, offset+delta)

    override operator fun minus(delta: Int) = RawIntPtr(data, offset-delta)

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


class BadPointerArithmetic(message: String) : Exception(message)

class IntRefPtr(val ref: IntRef) : IntPtr {

    override fun copy(): IntPtr = IntRefPtr(ref)

    private fun fail(): IntPtr = throw BadPointerArithmetic("not allowed on int ref ptr")

    override fun plus(delta: Int): IntPtr = fail()

    override fun minus(delta: Int): IntPtr = fail()

    override fun plusAssign(delta: Int) { fail() }

    override fun minusAssign(delta: Int) {  fail() }

    override fun set(idx: Int, value: Int) {
        require(idx == 0)
        ref.value = value
    }

    override fun get(idx: Int): Int {
        require(idx == 0)
        return ref.value
    }

}

class ProxyIntPtr(val data: ByteArray, var offset: Int): IntPtr {

    override fun copy(): IntPtr = ProxyIntPtr(data, offset)

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
