package org.dlang.dmd.root


fun strlen(ptr: BytePtr): Int {
    if (ptr.data[ptr.data.size-1] != 0.toByte())
        return ptr.data.size - ptr.offset
    var i = ptr.data.size - 1
    while (ptr.data[i] == 0.toByte()) i--
    return i - ptr.offset
}

fun strchr(ptr: BytePtr, c: Byte): BytePtr? {
    for (i in ptr.offset .. ptr.data.size) {
        if (ptr.data[i] == c) return BytePtr(ptr.data, i)
    }
    return null
}


fun realloc(ptr: BytePtr, size: Int): BytePtr  {
    require(ptr.offset == 0)
    return BytePtr(ptr.data.copyOf(size))
}

fun memcpy(dest: BytePtr, from: BytePtr, size: Int) = memmove(dest, from, size)

fun memmove(dest: BytePtr, from: BytePtr, size: Int)  {
    from.data.copyInto(dest.data, dest.offset, from.offset, from.offset + size)
}

fun memset(data: BytePtr, value: Int, nbytes: Int) {
    data.data.fill(value.toByte(), data.offset, data.offset + nbytes)
}

fun xmalloc(size: Int) = BytePtr(size)

// compatibility shim
object mem {
    fun malloc(size: Int) = BytePtr(size)
}

fun<T> slice(arr: Array<T>): Slice<T>  = Slice(arr)

fun<T> slice(arr: Array<Array<T>>): Slice<Slice<T>> {
    return Slice(arr.map { Slice(it) }.toTypedArray())
}

fun slice(arr: Array<CharArray>): Slice<CharSlice> {
    return Slice(arr.map { CharSlice(it) }.toTypedArray())
}

fun slice(arr: Array<ByteArray>): Slice<ByteSlice> {
    return Slice(arr.map { ByteSlice(it) }.toTypedArray())
}


fun slice(arr: CharArray) = CharSlice(arr)

fun slice(arr: IntArray) = IntSlice(arr)

fun ref(v: Int) = IntRef(v)

fun<T> ref(v: T) = Ref(v)

fun<T> ref(v: Ref<T>) = v

