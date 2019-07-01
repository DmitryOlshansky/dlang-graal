package org.dlang.dmd.root

import java.lang.IllegalArgumentException

enum class FormatState {
    start, // normal symbols
    percent, // after %
    percentDigit, // %[0-9]*
    percentDot, // %. or %[0-9].
    percentDotStar, // %.*
}

class OutBuffer {
    @JvmField var data: BytePtr
    @JvmField var offset: Int
    var size: Int
    @JvmField var level: Int
    @JvmField var doindent: Boolean
    private var notlinehead: Boolean

    constructor(): this(null) {}

    constructor(data: BytePtr? = null, offset: Int = 0, size: Int = 0, level: Int = 0, doindent: Boolean = false,
                notlinehead: Boolean = false) {
        this.data = if (data === null) BytePtr(size) else data
        this.offset = offset
        this.size = size
        this.level = level
        this.doindent = doindent
        this.notlinehead = notlinehead
    }

    fun extractData() : BytePtr {
        val p = data
        data = BytePtr(16)
        offset = 0
        size = 0
        return p
    }

    fun reserve(nbytes: Int): Unit
    {
        //printf("OutBuffer::reserve: size = %d, offset = %d, nbytes = %d\n", size, offset, nbytes)
        if (size - offset < nbytes)
        {
            /* Increase by factor of 1.5 round up to 16 bytes.
             * The odd formulation is so it will map onto single x86 LEA instruction.
             */
            size = (((offset + nbytes) * 3 + 30) / 2) and 15.inv()
            data = realloc(data, size)
        }
    }

    fun setsize(size: Int)
    {
        offset = size
    }

    fun reset()
    {
        offset = 0
    }

    fun indent(): Unit
    {
        if (level != 0)
        {
            reserve(level)
            data.slice(offset, offset + level).set('\t'.toByte())
            offset += level
        }
        notlinehead = true
    }

    fun write(data: BytePtr, nbytes: Int)
    {
        if (doindent && !notlinehead)
            indent()
        reserve(nbytes)
        memcpy(this.data + offset, data, nbytes)
        offset += nbytes
    }

    fun writestring(string: BytePtr)
    {
        write(string, strlen(string))
    }

    fun writestring(s: ByteSlice)
    {
        write(s.ptr(), s.length)
    }

    fun prependstring(string: BytePtr)
    {
        val len = strlen(string)
        reserve(len)
        memmove(data + len, data, offset)
        memcpy(data, string, len)
        offset += len
    }

    // write newline
    fun writenl()
    {
        writeByte('\n'.toInt())
        if (doindent)
            notlinehead = false
    }

    fun writeByte(b: Int)
    {
        if (doindent && !notlinehead && b != '\n'.toInt())
            indent()
        reserve(1)
        this.data[offset] = b.toByte()
        offset++
    }

    fun writeUTF8(b: Int)
    {
        reserve(6)
        if (b <= 0x7F)
        {
            this.data[offset] = b.toByte()
            offset++
        }
        else if (b <= 0x7FF)
        {
            this.data[offset + 0] = ((b shr 6) or 0xC0).toByte()
            this.data[offset + 1] = ((b and 0x3F) or 0x80).toByte()
            offset += 2
        }
        else if (b <= 0xFFFF)
        {
            this.data[offset + 0] = ((b shr 12) or 0xE0).toByte()
            this.data[offset + 1] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data[offset + 2] = ((b and 0x3F) or 0x80).toByte()
            offset += 3
        }
        else if (b <= 0x1FFFFF)
        {
            this.data[offset + 0] = ((b shr 18) or 0xF0).toByte()
            this.data[offset + 1] = (((b shr 12) and 0x3F) or 0x80).toByte()
            this.data[offset + 2] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data[offset + 3] = ((b and 0x3F) or 0x80).toByte()
            offset += 4
        }
        else if (b <= 0x3FFFFFF)
        {
            this.data[offset + 0] = ((b shr 24) or 0xF8).toByte()
            this.data[offset + 1] = (((b shr 18) and 0x3F) or 0x80).toByte()
            this.data[offset + 2] = (((b shr 12) and 0x3F) or 0x80).toByte()
            this.data[offset + 3] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data[offset + 4] = ((b and 0x3F) or 0x80).toByte()
            offset += 5
        }
        else if (b <= 0x7FFFFFFF)
        {
            this.data[offset + 0] = ((b shr 30) or 0xFC).toByte()
            this.data[offset + 1] = (((b shr 24) and 0x3F) or 0x80).toByte()
            this.data[offset + 2] = (((b shr 18) and 0x3F) or 0x80).toByte()
            this.data[offset + 3] = (((b shr 12) and 0x3F) or 0x80).toByte()
            this.data[offset + 4] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data[offset + 5] = ((b and 0x3F) or 0x80).toByte()
            offset += 6
        }
        else
            assert(false)
    }

    fun prependbyte(b: Int)
    {
        reserve(1)
        memmove(data + 1, data, offset)
        data[0] = b.toByte()
        offset++
    }

    fun writewchar(w: Char)
    {
        write4(w.toInt())
    }

    fun writeword(w: Char)
    {
        val newline = '\n'
        if (doindent && !notlinehead && w != newline)
            indent()

        reserve(2)
        (this.data + offset).toCharPtr()[0] = w
        offset += 2
    }

    fun writeUTF16(w: Int)
    {
        reserve(4)
        if (w <= 0xFFFF)
        {
            (this.data + offset).toCharPtr()[0] = w.toChar()
            offset += 2
        }
        else if (w <= 0x10FFFF)
        {
            (this.data + offset).toCharPtr()[0] = ((w shr 10) + 0xD7C0).toChar()
            (this.data + offset + 2) .toCharPtr()[0]= ((w and 0x3FF) or 0xDC00).toChar()
            offset += 4
        }
        else
            assert(false)
    }

    fun write4(w: Int)
    {
        if (doindent && !notlinehead)
            indent()
        reserve(4)
        (this.data + offset).toIntPtr()[0] = w
        offset += 4
    }

    fun write(buf: OutBuffer?)
    {
        if (buf !== null)
        {
            reserve(buf.offset)
            memcpy(data + offset, buf.data, buf.offset)
            offset += buf.offset
        }
    }

    fun write(obj: RootObject?)
    {
        if (obj !== null)
        {
            writestring(obj.toChars())
        }
    }

    fun fill0(nbytes : Int)
    {
        reserve(nbytes)
        memset(data + offset, 0, nbytes)
        offset += nbytes
    }

    fun printf(format: ByteSlice, vararg args: Any?) {
        val temp = Array<Any?>(args.size) { null }
        args.copyInto(temp)
        vprintf(format, slice(temp))
    }

    private fun fmtOne(format: ByteSlice, j: Int, width: Int, padChar: Char, args: Slice<Any>, longPrefix: Boolean = false): Int {
        fun extent(): Int {
            var w = width
            if (w < 0) {
                w = (args[0] as Number).toInt()
                require(w >= 0) { "Negative width passed as variable format specifier"}
                args.beg++
            }
            return w
        }
        fun write(){
            val w = extent()
            var arg = args[0]
            if (w != 0) when (arg) {
                is ByteSlice -> {
                    if (w <= arg.length) writestring(arg.slice(0, w))
                    else {
                        for (k in arg.length until w) writeByte(padChar.toInt())
                        writestring(arg)
                    }
                }
                is BytePtr -> {
                    val len = strlen(arg)
                    if (w <= len) writestring(arg.slice(0, w))
                    else writestring(arg)
                }
                else -> writestring(ByteSlice(arg.toString().padStart(w, padChar)))
            }
            else when (arg) {
                is ByteSlice -> writestring(arg)
                is BytePtr -> writestring(arg)
                else -> writestring(ByteSlice(arg.toString()))
            }
            args.beg += 1
        }
        fun writeRadix(r: Int) {
            val w = extent()
            val arg = args[0]
            val str = when (arg) {
                is Byte -> arg.toString(r)
                is Short -> arg.toString(r)
                is Int -> arg.toString(r)
                is Long ->  arg.toString(r)
                else -> throw IllegalArgumentException("Expected integer for %x specifier, got ${arg}")
            }
            if (w != 0) writestring(ByteSlice(str.padStart(w, padChar)))
            else writestring(ByteSlice(str))
        }
        if (j == format.length && longPrefix) {
            write()
            return j
        }
        else
            require(j < format.length) {  "Unexpected end of format string" }

        when (format[j].toChar()) {
            'l' -> {
                return fmtOne(format, j + 1, width,  padChar, args, true)
            }
            's', 'i', 'd', 'f' -> {
                write()
            }
            'p' -> { // print pointer as values... but handle nulls
                val arg = args[0]
                args[0] = if (arg === null) "null" else "Ptr<${arg}>"
                write()
            }
            'o' -> {
                writeRadix(8)
            }
            'x' -> {
                writeRadix(16)
            }
            'c' -> {
                val arg = args[0]
                when (arg){
                    is Number -> writeByte(arg.toInt())
                    is Char -> writeByte(arg.toInt())
                    else -> throw IllegalArgumentException("Expected number for %c specifier")
                }
                args.beg += 1
            }
            else -> throw IllegalArgumentException("Expected %s, %c, %d, %i, %f but got %${format[j].toChar()}")
        }
        return j + 1
    }

    // this is very limited but sensible subset of printf
    fun vprintf(format: ByteSlice, args: Slice<Any>) {
        val values = args.copy()
        var state = FormatState.start
        var width = 0 // 0 - any, -1 - star,
        var pad = ' '
        var i = 0
        val len = format.length
        while (i < len) {
            when (state) {
                FormatState.start -> {
                    if (format[i] == '%'.toByte()) state = FormatState.percent
                    else
                        writeByte(format[i].toInt())
                    i++
                }
                FormatState.percent -> {
                    if (format[i] == '%'.toByte()) {
                        state = FormatState.start
                        writeByte('%'.toInt())
                        i++
                    }
                    else if (format[i] == '.'.toByte()) {
                        state = FormatState.percentDot
                        i++
                    }
                    else if(format[i] == '0'.toByte()) {
                        pad = '0'
                        i++
                    }
                    else if (isdigit(format[i].toInt()) != 0) {
                        state = FormatState.percentDigit
                        width = format[i] - '0'.toInt()
                        i++
                    }
                    else {
                        i = fmtOne(format, i, width, pad, values)
                        state = FormatState.start
                        pad = ' '
                        width = 0
                    }
                }
                FormatState.percentDigit -> {
                    if (isdigit(format[i].toInt()) != 0) {
                        width = 10 * width + (format[i] - '0'.toInt())
                        i++
                    }
                    else {
                        i = fmtOne(format, i, width, pad, values)
                        state = FormatState.start
                        pad = ' '
                        width = 0
                    }
                }
                FormatState.percentDot -> {
                    if (isdigit(format[i].toInt()) != 0) {
                        state = FormatState.percentDigit
                        width = 10 * width + (format[i] - '0'.toInt())
                        i++
                    }
                    else if(format[i].toByte() == '*'.toByte()) {
                        state = FormatState.percentDotStar
                        width = -1
                        i++
                    }
                    else {
                        i = fmtOne(format, i, width, pad, values)
                        state = FormatState.start
                        pad = ' '
                        width = 0

                    }
                }
                FormatState.percentDotStar -> {
                    i = fmtOne(format, i, width, pad, values)
                    state = FormatState.start
                    pad = ' '
                    width = 0
                }
            }
        }
    }

    fun vprintf(format: BytePtr, args: Slice<Any>) {
        vprintf(format.slice(0, strlen(format)), args)
    }
    /**************************************
     * Convert `u` to a string and append it to the buffer.
     * Params:
     *  u = integral value to append
     */
    fun print(u: Long)
    {
        //import core.internal.string  // not available
        writestring(ByteSlice(u.toString()))
    }

    fun bracket(left: Byte, right: Byte)
    {
        reserve(2)
        memmove(data + 1, data, offset)
        data[0] = left
        data[offset + 1] = right
        offset += 2
    }

    /******************
     * Insert left at i, and right at j.
     * Return index just past right.
     */
    fun bracket(i: Int, left: BytePtr, j: Int, right: BytePtr): Int
    {
        val leftlen = strlen(left)
        val rightlen = strlen(right)
        reserve(leftlen + rightlen)
        insert(i, left, leftlen)
        insert(j + leftlen, right, rightlen)
        return j + leftlen + rightlen
    }

    fun spread(offset: Int, nbytes: Int)
    {
        reserve(nbytes)
        memmove(data + offset + nbytes, data + offset, this.offset - offset)
        this.offset += nbytes
    }

    /****************************************
     * Returns: offset + nbytes
     */
    fun insert(offset: Int, p: BytePtr, nbytes: Int): Int
    {
        spread(offset, nbytes)
        memmove(data + offset, p, nbytes)
        return offset + nbytes
    }

    fun insert(offset: Int,  s: ByteSlice): Int
    {
        return insert(offset, s.ptr(), s.length)
    }

    fun remove(offset: Int, nbytes: Int)
    {
        memmove(data + offset, data + offset + nbytes, this.offset - (offset + nbytes))
        this.offset -= nbytes
    }

    fun peekSlice(): ByteSlice
    {
        return data.slice(0, offset)
    }

    /***********************************
     * Extract the data as a slice and take ownership of it.
     */
    fun extractSlice(): ByteSlice
    {
        val length = offset
        val p = extractData()
        return p.slice(0, length)
    }

    // Append terminating null if necessary and get view of internal buffer
    fun peekChars(): BytePtr
    {
        if (offset == 0 || data[offset - 1] != 0.toByte())
        {
            writeByte(0)
            offset-- // allow appending more
        }
        return data
    }

    // Append terminating null if necessary and take ownership of data
    fun extractChars(): BytePtr
    {
        if (offset == 0 || data[offset - 1] != 0.toByte())
            writeByte(0)
        return extractData()
    }
}