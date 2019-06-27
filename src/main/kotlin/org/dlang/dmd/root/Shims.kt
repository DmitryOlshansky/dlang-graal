package org.dlang.dmd.root

import org.dlang.dmd.utils

import java.io.PrintStream
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


fun strlen(ptr: BytePtr): Int {
    var i = ptr.offset
    while (i < ptr.data.size && ptr.data[i] != 0.toByte()) {
        i++
    }
    return i - ptr.offset
}

fun strchr(ptr: BytePtr, c: Int): BytePtr? {
    for (i in ptr.offset .. ptr.data.size) {
        if (ptr.data[i] == c.toByte()) return BytePtr(ptr.data, i)
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

fun strncmp(ptr: BytePtr, ptr2: BytePtr, n: Int): Int {
    var i = ptr.offset
    var j = ptr2.offset
    var k = 0
    while (i < ptr.data.size && j < ptr2.data.size && k++ < n) {
        val delta = ptr.data[i++] - ptr2.data[j++]
        if (delta != 0) return delta.toInt()
    }
    if (i - ptr.offset == j - ptr2.offset) return 0
    else if(i - ptr.offset < j - ptr2.offset) return -1
    else return 1
}

fun strncmp(ptr: BytePtr, s2: ByteSlice, n: Int): Int {
    var i = ptr.offset
    var j = s2.beg
    var k = 0
    while (i < ptr.data.size && j < s2.end && k++ < n) {
        val delta = ptr.data[i++] - s2.data[j++]
        if (delta != 0) return delta.toInt()
    }
    if (i - ptr.offset == j - s2.beg) return 0
    else if(i - ptr.offset < j - s2.beg) return -1
    else return 1
}

fun strstr(ptr: BytePtr, needle: BytePtr) : BytePtr? = null //TODO: stub!

fun strstr(ptr: BytePtr, needle: ByteSlice): BytePtr? = null //TODO: stub!

fun strcat(dest: BytePtr, src: ByteSlice) {
    val len = strlen(dest)
    src.data.copyInto(dest.data, dest.offset + len, src.beg, src.end)
}

fun strdup(src: BytePtr) = BytePtr(src.data.copyOf(), src.offset)

fun xarraydup(src: ByteSlice) = ByteSlice(src.data.copyOfRange(src.beg, src.end))

fun xstrdup(src: ByteSlice) = Mem.xstrdup(src)

fun sprintf(ptr: BytePtr, fmt: ByteSlice, vararg args: Any?) {
    val s = String.format(fmt.toString(), *args)
    var i = 0
    for (c in s) {
        ptr[i++] = c.toByte()
    }
    ptr[i] = 0.toByte()
}

fun isalpha(c: Int): Int = if(Character.isAlphabetic(c) || c == '_'.toInt()) 1 else 0

fun isprint(c: Int): Int = if(Character.isISOControl(c)) 0 else 1

fun isdigit(c: Int): Int = if(Character.isDigit(c)) 1 else 0

fun isxdigit(c: Int): Int =
    if(Character.isDigit(c) || (c >= 'a'.toInt() && c <= 'f'.toInt() || (c >= 'A'.toInt() && c <= 'F'.toInt()))) 1 else 0

fun isalnum(c: Int): Int = if(Character.isAlphabetic(c) || Character.isDigit(c)) 1 else 0

fun islower(c: Int): Int = if(Character.isLowerCase(c)) 1 else 0

fun isupper(c: Int): Int = if(Character.isUpperCase(c)) 1 else 0

fun tolower(c: Int): Int = Character.toLowerCase(c)

fun toupper(c: Int): Int = Character.toUpperCase(c)

fun isspace(c: Int):Int = if (Character.isSpaceChar(c)) 1 else 0

fun realloc(ptr: BytePtr?, size: Int): BytePtr  {
    require(ptr === null || ptr.offset == 0)
    return if (ptr === null) BytePtr(ByteArray(size))
    else BytePtr(ptr.data.copyOf(size))
}

fun getcwd(s: BytePtr?, i: Int) = BytePtr(Paths.get(".").toAbsolutePath().normalize().toString())

fun time(s: IntPtr): Int {
    val unixTime = System.currentTimeMillis() / 1000
    s.set(0, unixTime.toInt())
    return unixTime.toInt()
}


fun ctime(s: IntPtr): BytePtr {
    val df = SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy\n")
    val unix = Date(s.get(0)*1000L)
    return BytePtr(df.format(unix))
}

fun memcmp(a: BytePtr, b: BytePtr, size: Int): Int {
    for (i in 0 until size) {
        val delta = a[i] - b[i]
        if (delta != 0) return if(delta > 0) 1 else -1
    }
    return 0
}

fun memcmp(a: BytePtr, b: ByteSlice, size: Int): Int = memcmp(a, b.ptr(), size)

fun memcpy(dest: BytePtr, from: BytePtr, size: Int) = memmove(dest, from, size)

fun memmove(dest: BytePtr, from: BytePtr, size: Int)  {
    from.data.copyInto(dest.data, dest.offset, from.offset, from.offset + size)
}

fun memset(data: BytePtr, value: Int, nbytes: Int) {
    data.data.fill(value.toByte(), data.offset, data.offset + nbytes)
}

fun __equals(a: Any, b: Any) = a == b

fun addu(a: Long, b: Long, overflow: Ref<Boolean>): Long {
    overflow.value = false
    return a + b
}

fun mulu(a: Long, b: Long, overflow: Ref<Boolean>): Long {
    overflow.value = false
    return a * b
}

fun destroy(a: Any)  {}

fun hashOf(any: Any) = any.hashCode()

fun hashOf(any: Any, seed: Int) = any.hashCode() + seed * 31

fun<A,B> comma(a: A?, b: B?) = b

fun<T> slice(arr: Array<T?>): Slice<T>  = Slice(arr)

fun slice(bytes: ByteArray) = ByteSlice(bytes)

fun<T> slice(arr: Array<Array<T?>>): Slice<Slice<T>> {
    return Slice<Slice<T>>(arr.map { Slice(it) }.toTypedArray())
}

fun slice(arr: Array<CharArray>): Slice<CharSlice> {
    return Slice<CharSlice>(arr.map { CharSlice(it) }.toTypedArray())
}

fun slice(arr: Array<ByteArray>): Slice<ByteSlice> {
    return Slice<ByteSlice>(arr.map { ByteSlice(it) }.toTypedArray())
}

fun slice(arr: CharArray) = CharSlice(arr)

fun slice(arr: IntArray) = IntSlice(arr)

fun<T> pcopy(ptr: Ptr<T>?) = ptr?.copy()

fun pcopy(ptr: BytePtr?) = ptr?.copy()

fun pcopy(ptr: IntPtr?) = ptr?.copy()

fun pcopy(any: Any?) = any

fun toByteSlice(slice: ByteSlice) = slice

fun toBytePtr(any: Any): BytePtr =
    when(any) {
        is BytePtr -> any
        is ByteSlice -> any.ptr()
        else -> throw Exception("Not implemented toBytePtr for $any")
    }

fun toCharPtr(any: Any): CharPtr =
    when (any) {
        is BytePtr -> any.toCharPtr()
        is CharPtr -> any
        else -> throw Exception("Not implemented toCharPtr for $any")
    }

fun toIntPtr(any: Any): IntPtr =
    when (any) {
        is BytePtr -> any.toIntPtr()
        is IntPtr -> any
        else -> throw Exception("Not implemented toCharPtr for $any")
    }



fun ref(v: Int) = IntRef(v)

fun<T> ref(v: T) = Ref(v)

fun<T> ref(v: Ref<T>) = v

fun<T> ptr(v: Ref<T>) = RefPtr(v)

fun ptr(v: BytePtr) = BytePtrPtr(v)

fun ptr(v: IntRef) = IntRefPtr(v)

fun ptr(v: ByteSlice) = v.ptr()

fun<T> ptr(v: Slice<T>) = v.ptr()

fun exit(code: Int): Nothing = exitProcess(code)

// stub out speller
fun<T> speller(fn: (ByteSlice, IntRef) -> T) = null

// ======== STDIO ===========

typealias StdIo = _IO_FILE

class _IO_FILE(val handle: PrintStream)

fun getenv(s: ByteSlice): BytePtr = BytePtr(System.getenv(s.toString()))
fun getenv(s: BytePtr): BytePtr = BytePtr(System.getenv(s.toString()))

fun isatty(n: Int):Int = if(System.console() != null) 1 else 0

fun printf(fmt: ByteSlice, vararg args: Any?) = fprintf(stdout, fmt, *args)

fun vsprintf(dest: BytePtr, fmt: BytePtr, args: Slice<Any>): Int {
    val outbuf = OutBuffer()
    outbuf.vprintf(fmt, args)
    val result = outbuf.extractSlice()
    for (i in 0 until result.length) {
        dest[i] = result[i]
    }
    return result.length
}

fun fprintf(io: StdIo, fmt: ByteSlice, vararg args: Any?) {
    val outbuf = OutBuffer()
    val arr = arrayOfNulls<Any?>(args.size)
    args.copyInto(arr)
    outbuf.vprintf(fmt, slice(arr))
    io.handle.print(outbuf.extractSlice().toString())
}

fun fputs(s: ByteSlice, io: StdIo) {
    io.handle.print(s.toString())
}

fun fputs(s: BytePtr, io: StdIo) {
    io.handle.print(s.toString())
}

fun fputc(c: Int, io: StdIo) {
    io.handle.print(c.toChar())
}

fun fflush(io: StdIo) = io.handle.flush()

@JvmField
val stdout = _IO_FILE(System.out)

@JvmField
val stderr = _IO_FILE(System.err)

// ========== AA ============
fun<K,V> update(aa: AA<K,V>, key: K, ins:() -> V, upd:(V) -> V) {
    val s = aa.getLvalue(key)
    val v = s.get(0)
    if (v == null) s.set(0, ins())
    else s.set(0, upd(v))
}

// Goto exception singletons
object Dispatch0 : Exception()
object Dispatch1 : Exception()
object Dispatch2 : Exception()
object Dispatch3 : Exception()
object Dispatch4 : Exception()
object Dispatch5 : Exception()

// Assert false at the end of final switch
object SwitchError: RuntimeException()
