package org.dlang.dmd.root


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
    for (i in ptr.offset until ptr.data.size) {
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

fun strstr(ptr: BytePtr, needle: BytePtr) : BytePtr? = strstr(ptr, needle.slice(0, strlen(needle)))

fun strstr(ptr: BytePtr, needle: ByteSlice): BytePtr? {
    var ch = needle[0]
    if (ch == 0.toByte()) return null
    var p: BytePtr? = ptr
    while (p!!.get() != 0.toByte() && p.offset != p.data.size) {
        p = strchr(p, ch.toInt())
        if (p === null) return null
        if (strncmp(p, needle, needle.length) == 0) return p
        p.plusAssign(1)
    }
    return null
}

fun strcat(dest: BytePtr, src: BytePtr) {
    val len = strlen(dest)
    val slen = strlen(src)
    src.data.copyInto(dest.data, dest.offset + len, src.offset, src.offset + slen)
    dest.data[slen + len] = 0.toByte()
}

fun strdup(src: BytePtr) = BytePtr(src.data.copyOf(), src.offset)

fun xarraydup(src: ByteSlice) = ByteSlice(src.data.copyOfRange(src.beg, src.end))

fun xstrdup(src: ByteSlice) = Mem.xstrdup(src)

fun sprintf(ptr: BytePtr, fmt: BytePtr, vararg args: Any?): Int {
    val arr = arrayOfNulls<Any>(args.size)
    args.copyInto(arr, 0, 0, args.size)
    return vsprintf(ptr, fmt, slice(arr))
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

@Suppress("UNUSED_PARAMETER")
fun getcwd(s: BytePtr?, i: Int) = BytePtr(Paths.get(".").toAbsolutePath().normalize().toString())

fun time(s: Ptr<Int>): Int {
    val unixTime = System.currentTimeMillis() / 1000
    s.set(0, unixTime.toInt())
    return unixTime.toInt()
}


fun ctime(s: Ptr<Int>): BytePtr {
    val df = SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy\n")
    val unix = Date(s[0]!!*1000L)
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

fun addu(a: Int, b: Int, overflow: Ref<Boolean>): Int {
    overflow.value = false
    return a + b
}

fun mulu(a: Int, b: Int, overflow: Ref<Boolean>): Int {
    overflow.value = false
    return a * b
}

fun addu(a: Long, b: Long, overflow: Ref<Boolean>): Long {
    overflow.value = false
    return a + b
}

fun mulu(a: Long, b: Long, overflow: Ref<Boolean>): Long {
    overflow.value = false
    return a * b
}

fun<T> pequals(lhs: T?, rhs: T?): Boolean {
    if (lhs === null) return rhs === null
    if (rhs === null) return false
    return lhs == rhs
}

@Suppress("UNUSED_PARAMETER")
fun destroy(a: Any)  {}

fun hashOf(any: Any) = any.hashCode()

fun hashOf(any: Any, seed: Int) = any.hashCode() + seed * 31

@Suppress("UNUSED_PARAMETER")
fun<A,B> comma(a: A?, b: B?) = b

@Suppress("UNUSED_PARAMETER")
fun expr(arg: Boolean) {}

@Suppress("UNUSED_PARAMETER")
fun expr(arg: Void) {}

// Erasure tag - used to disambiguate overloadset with same erasures
object ETag1
object ETag2
object ETag3

fun assertMsg(cond: Boolean, msg: ByteSlice) {
    require(cond){ msg }
}

fun<T> slice(arr: Array<T?>): Slice<T>  = RawSlice(arr)

fun slice(bytes: ByteArray) = ByteSlice(bytes)

fun<T> slice(arr: Array<Array<T?>>): Slice<Slice<T>> {
    return RawSlice<Slice<T>>(arr.map { RawSlice<T>(it) }.toTypedArray())
}

fun slice(arr: Array<CharArray>): Slice<CharSlice> {
    return RawSlice<CharSlice>(arr.map { CharSlice(it) }.toTypedArray())
}

fun slice(arr: Array<ByteArray>): Slice<ByteSlice> {
    return RawSlice<ByteSlice>(arr.map { ByteSlice(it) }.toTypedArray())
}

fun slice(arr: CharArray) = CharSlice(arr)

fun slice(arr: IntArray) = IntSlice(arr)

fun concat(first: Byte, next: ByteSlice): ByteSlice {
    val arr = ByteArray(next.length + 1)
    arr[0] = first
    next.copyTo(slice(arr).slice(1))
    return slice(arr)
}

fun concat(first: ByteSlice, next: ByteSlice): ByteSlice {
    val arr = ByteArray(first.length + next.length)
    first.copyTo(slice(arr))
    next.copyTo(slice(arr).slice(first.length))
    return ByteSlice(arr)
}

fun<T> pcopy(ptr: Ptr<T>?): Ptr<T>? = ptr?.copy()

fun pcopy(ptr: BytePtr?) = ptr?.copy()

fun<T> pcopy(any: T?) = any

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

fun<T> ref(v: T?) = Ref(v)

fun<T> refPtr(v: T) = RefPtr(Ref(v))

fun<T> ptr(v: Ref<T>) = RefPtr(v)

fun ptr(v: ByteSlice) = v.ptr()

fun<T> ptr(arr: Array<T?>) = RawPtr<T>(arr)

fun<T> ptr(v: Slice<T>) = v.ptr()

fun exit(code: Int): Nothing = exitProcess(code)

// stub out speller
@Suppress("UNUSED_PARAMETER")
object speller {
    //fun <T> invoke(fn: (ByteSlice, Ref<Int>) -> T) = null
    fun <T> invoke(fn: (ByteSlice) -> T) = null
}

// ======== STDIO ===========

typealias StdIo = _IO_FILE

class _IO_FILE(val handle: PrintStream)

fun getenv(s: ByteSlice): BytePtr? = getenv(s.ptr())

fun getenv(s: BytePtr): BytePtr? {
    val r = System.getenv(s.toString())
    return if (r != null) BytePtr(r) else null
}

fun isatty(n: Int):Int = if(System.console() != null && n >= 0 && n <= 2) 1 else 0

fun printf(fmt: ByteSlice, vararg args: Any?) = fprintf(stdout, fmt, *args)
fun printf(fmt: BytePtr, vararg args: Any?) = fprintf(stdout, fmt, *args)

fun vsprintf(dest: BytePtr, fmt: BytePtr, args: Slice<Any>): Int {
    val outbuf = OutBuffer()
    outbuf.vprintf(fmt, args)
    val result = outbuf.extractSlice()
    for (i in 0 until result.length) {
        dest[i] = result[i]
    }
    dest[result.length] = 0.toByte()
    return result.length
}

fun fprintf(io: Ptr<StdIo>, fmt: BytePtr, vararg args: Any?) = fprintf(io, fmt.slice(0, strlen(fmt)), *args)

fun fprintf(io: Ptr<StdIo>, fmt: ByteSlice, vararg args: Any?) {
    val outbuf = OutBuffer()
    val arr = arrayOfNulls<Any?>(args.size)
    args.copyInto(arr)
    outbuf.vprintf(fmt, slice(arr))
    io.get()?.handle?.print(outbuf.extractSlice().toString())
}

fun fputs(s: ByteSlice, io: Ptr<StdIo>) {
    io.get()?.handle?.print(s.toString())
}

fun fputs(s: BytePtr, io: Ptr<StdIo>) {
    io.get()?.handle?.print(s.toString())
}

fun fputc(c: Int, io: Ptr<StdIo>) {
    io.get()?.handle?.print(c.toChar())
}

fun fflush(io: Ptr<StdIo>) = io.get()?.handle?.flush()

@JvmField
val stdout = refPtr(_IO_FILE(System.out))

@JvmField
val stderr = refPtr(_IO_FILE(System.err))

// ======= std.getopt =======

data class GetoptResult(@JvmField val helpWanted: Boolean, @JvmField val options: List<Pair<ByteSlice, ByteSlice>>) {
    fun copy(): GetoptResult = this
}

fun getopt(@Suppress("UNUSED_PARAMETER") args: Ref<Slice<ByteSlice>>, vararg params: Any): GetoptResult
{
    var help = false
    val options = mutableListOf<Pair<ByteSlice,ByteSlice>>()
    // ignore args and use System.properties instead
    assert(params.size % 3 == 0)
    for (i in 0 until params.size/3) {
        val name = params[3 * i]
        val message = params[3 * i + 1]
        require(name is ByteSlice && message is ByteSlice)
        options.add(Pair(name, message))
        val prop = System.getProperty(name.toString())
        if (prop === null) help = true
        else {
            val target = params[3 * i + 2]
            if (target is Ptr<*>) {
                (target as Ptr<ByteSlice>)[0] = ByteSlice(prop)
            }
        }
    }
    return GetoptResult(help, options)
}

fun defaultGetoptPrinter(text: ByteSlice, options: List<Pair<ByteSlice, ByteSlice>>) {
    printf(ByteSlice("%s\nOptions:\n"), text)
    for (item in options) {
        printf(ByteSlice("%s - %s\n"), item.first, item.second);
    }
}

// ======= std.string =======

fun toStringz(s: ByteSlice) = s.ptr()

// ========== AA ============
fun<K,V> update(aa: AA<K,V>, key: K, ins:() -> V, upd:(V) -> V) {
    val s = aa.getLvalue(key)
    val v = s.get(0)
    if (v == null) s.set(0, ins())
    else s.set(0, upd(v))
}

// Goto exception singletons
object Dispatch : Exception() // unhandled dispatch!
object Dispatch0 : Exception()
object Dispatch1 : Exception()
object Dispatch2 : Exception()
object Dispatch3 : Exception()
object Dispatch4 : Exception()
object Dispatch5 : Exception()

// Assert false at the end of final switch
object SwitchError: RuntimeException()
