package org.dlang.dmd.root

class DArray<T>(storage: Array<Any?>, len: Int) {
    var data: Array<Any?> = storage
    @JvmField var length: Int = len

    constructor(): this(arrayOf())

    constructor(len: Int) : this(arrayOfNulls(len), len)

    constructor(arr: Array<Any?>): this(arr, 0)

    fun toChars() = asString().ptr()

    // shallow copy
    fun copy() = DArray<T>(data, length)

    fun asString(): ByteSlice {
        val accum = mutableListOf<Byte>()
        accum.add('['.toByte())
        for (i in 0 until length) {
            if (i != 0 ) {
                accum.add(','.toByte())
                accum.add(' '.toByte())
            }
            val src = if (data[i] != null)
                data[i]!!.toString().toByteArray()
            else
                "null".toByteArray()
            for (v in src) accum.add(v)
        }
        accum.add(']'.toByte())
        return ByteSlice(accum.toByteArray())
    }

    override fun toString(): String {
        val slice = asString()
        return String(slice.data, slice.beg, slice.length)
    }

    operator fun get(i: Int): T? {
        assert(i < length)
        return data[i] as T?
    }

    operator fun set(i: Int, v: T?) {
        assert(i < length)
        data[i] = v
    }

    val dim: Int
        get() = length

    fun setDim(size: Int) {
        if (size > data.size) reserve(size - data.size)
        length = size
    }

    fun pushSlice(a: Slice<T>): DArray<T> {
        val oldLength = length
        setDim(oldLength + a.length)
        a.data.copyInto(data, oldLength, a.beg, a.length)
        return this
    }

    fun append(a: DArray<T>): DArray<T> {
        insert(length, a)
        return this
    }

    fun reserve(more: Int) {
        if (data.isEmpty()) data = data.copyOf(more)
        else if (length + more > data.size) {
            data = data.copyOf((data.size + more) * 3 / 2)
        }
    }

    fun push(item: T): DArray<T> {
        reserve(1)
        data[length++] = item
        return this
    }

    fun shift(item: T) {
        val before = length++
        data.copyOf(before+1)
        data.copyInto(data, 1, 0,  before)
        data[0] = item
    }

    fun remove(i: Int) {
        data.copyInto(data, i, i + 1, length)
        length--
    }

    fun insert(i: Int, arr: DArray<T>?) {
        if (arr != null) {
            reserve(arr.length)
            val d = arr.length
            reserve(d)
            if (length != i)
                data.copyInto(data, i + d, i, length)
            arr.data.copyInto(data, i, 0, d)
            length += d
        }
    }

    fun insert(i: Int, ptr: T?) {
        reserve(1)
        data.copyInto(data, i + 1, i, length)
        data[i] = ptr
        length++
    }

    fun zero() = data.fill(null, 0, length)

    fun pop(): T? = data[--length] as T?

    fun slice(): Slice<T> =  Slice(data as Array<T?>, 0, length)

    fun opSlice() = slice()

    fun slice(a: Int, b: Int): Slice<T?> {
        assert(b in a..length)
        return Slice(data as Array<T?>, a, b)
    }

    fun opSlice(a: Int, b: Int) = slice(a, b)

    override fun equals(other: Any?): Boolean =
        when(other) {
            is DArray<*> -> this.slice() == other.slice()
            else -> false
        }

    override fun hashCode(): Int {
        return data.hashCode() + 31*length
    }
}

class BitArray {
    private var array: IntArray = IntArray(4)
    private var len = 0

    var length: Int
        get() = len
        set(n: Int) {
            array = array.copyOf(n / 32 + if (n % 32 > 0) 1 else 0)
            len = n
        }


    operator fun set(idx: Int, b: Boolean) {
        assert(idx < length)
        if (b)
            array[idx / 32] = array[idx / 32].or(1.shl(idx % 32))
        else
            array[idx / 32] = array[idx / 32].and(1.shl(idx % 32).inv())
    }

    operator fun get(idx: Int): Boolean =
        array[idx / 32].and(1.shl(idx % 32)) != 0
}


fun<T> darray(size: Int): DArray<T> = DArray(arrayOfNulls<Any?>(size))

fun<T > darray(): DArray<T> = darray(16)


fun <T> darrayOf(vararg elements: T?): DArray<T> {
    val array = Array<Any?>(elements.size){ null }
    elements.copyInto(array)
    return DArray(array, array.size)
}

fun<T> peekSlice(array: DArray<T>?): Slice<T>? = array?.slice()

/**
 * Reverse an array in-place.
 * Params:
 *      a = array
 * Returns:
 *      reversed a[]
 */
fun<T> reverse(a: Slice<T>):Slice<T> {
    if (a.length > 1)
    {
        val mid = (a.length + 1).shr(1);
        for (i in 0 until mid)
        {
            val e = a[i]
            a[i] = a[a.length - 1 - i]
            a[a.length - 1 - i] = e
        }
    }
    return a
}

/**
 * Splits the array at $(D index) and expands it to make room for $(D length)
 * elements by shifting everything past $(D index) to the right.
 * Params:
 *  array = the array to split.
 *  index = the index to split the array from.
 *  length = the number of elements to make room for starting at $(D index).
 */
fun<T: RootObject> split(array: DArray<T>, index: Int, length: Int) {
    if (length > 0)
    {
        val previousDim = array.length
        array.setDim(array.length + length)
        array.data.copyInto(array.data, index+length, index, previousDim)
    }
}
