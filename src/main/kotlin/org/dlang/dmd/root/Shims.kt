package org.dlang.dmd.root

import java.io.PrintStream
import java.nio.file.Paths


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

fun strcmp(ptr: BytePtr, ptr2: BytePtr): Int {
    var i = ptr.offset
    var j = ptr2.offset
    while (i < ptr.data.size && j < ptr2.data.size) {
        val delta = ptr.data[i++] - ptr2.data[j++]
        if (delta != 0) return delta.toInt()
    }
    if (i == ptr.data.size && j == ptr2.data.size) return 0
    else if(i == ptr.data.size) return -1
    else return 1
}

fun strcmp(ptr: BytePtr, s2: ByteSlice): Int {
    var i = ptr.offset
    var j = s2.beg
    while (i < ptr.data.size && j < s2.end) {
        val delta = ptr.data[i++] - s2.data[j++]
        if (delta != 0) return delta.toInt()
    }
    if (i == ptr.data.size && j == s2.end) return 0
    else if(i == ptr.data.size) return -1
    else return 1
}

fun strcat(dest: BytePtr, src: ByteSlice) {
    val len = strlen(dest)
    src.data.copyInto(dest.data, dest.offset + len, src.beg, src.end)
}

fun strdup(src: BytePtr) = BytePtr(src.data.copyOf(), src.offset)

fun sprintf(ptr: BytePtr, fmt: ByteSlice, vararg args: Any?) {
    val s = String.format(fmt.toString(), args)
    var i = 0
    for (c in s) {
        ptr[i++] = c.toByte()
    }
    ptr[i] = 0.toByte()
}

fun isprint(c: Int): Int = if(Character.isISOControl(c)) 0 else 1

fun isdigit(c: Int): Int = if(Character.isDigit(c)) 1 else 0

fun tolower(c: Int) = Character.toLowerCase(c)

fun toupper(c: Int) = Character.toUpperCase(c)

fun realloc(ptr: BytePtr, size: Int): BytePtr  {
    require(ptr.offset == 0)
    return BytePtr(ptr.data.copyOf(size))
}

fun getcwd(s: BytePtr, i: Int) = BytePtr(Paths.get(".").toAbsolutePath().normalize().toString())


fun memcpy(dest: BytePtr, from: BytePtr, size: Int) = memmove(dest, from, size)

fun memmove(dest: BytePtr, from: BytePtr, size: Int)  {
    from.data.copyInto(dest.data, dest.offset, from.offset, from.offset + size)
}

fun memset(data: BytePtr, value: Int, nbytes: Int) {
    data.data.fill(value.toByte(), data.offset, data.offset + nbytes)
}

fun __equals(a: Any, b: Any) = a == b

fun hashOf(any: Any) = any.hashCode()

fun hashOf(any: Any, seed: Int) = any.hashCode() + seed * 31

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

// stub out speller
fun<T> speller(fn: (ByteSlice, IntRef) -> T) = null

typealias StdIo = _IO_FILE

class _IO_FILE(val handle: PrintStream) {

    companion object{
        val stdout = _IO_FILE(System.out)
        val stderr = _IO_FILE(System.err)
    }
}

fun getenv(s: ByteSlice): BytePtr = BytePtr(System.getenv(s.toString()))

fun isatty(n: Int):Int = if(System.console() != null) 1 else 0

fun printf(io: StdIo, fmt: ByteSlice, vararg args: Any?) = fprintf(StdIo.stdout, fmt, args)

fun fprintf(io: StdIo, fmt: ByteSlice, vararg args: Any?) {
    val result = String.format(fmt.toString(), args)
    io.handle.print(result)
}

fun fputs(s: ByteSlice, io: StdIo) {
    io.handle.print(s.toString())
}
