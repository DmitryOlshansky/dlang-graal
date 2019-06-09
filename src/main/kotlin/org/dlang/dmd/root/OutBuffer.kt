package org.dlang.dmd.root

class OutBuffer {
    var data_ : BytePtr = BytePtr(16)
    var offset: Int = 0
    var size: Int = 0
    var level: Int = 0
    var doindent: Boolean = false
    private var notlinehead: Boolean = false



    fun extractdata() : BytePtr {
        val p = data_
        data_ = BytePtr(16)
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
            data_ = realloc(data_, size)
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
            data_.slice(offset, offset + level).set('\t'.toByte())
            offset += level
        }
        notlinehead = true
    }

    fun write(data_: BytePtr, nbytes: Int)
    {
        if (doindent && !notlinehead)
            indent()
        reserve(nbytes)
        memcpy(this.data_ + offset, data_, nbytes)
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
        memmove(data_ + len, data_, offset)
        memcpy(data_, string, len)
        offset += len
    }

    // write newline
    fun writenl()
    {
        writeByte('\n')
        if (doindent)
            notlinehead = false
    }

    fun writeByte(b: Char)
    {
        if (doindent && !notlinehead && b != '\n')
            indent()
        reserve(1)
        this.data_[offset] = b.toByte()
        offset++
    }

    fun writeUTF8(b: Int)
    {
        reserve(6)
        if (b <= 0x7F)
        {
            this.data_[offset] = b.toByte()
            offset++
        }
        else if (b <= 0x7FF)
        {
            this.data_[offset + 0] = ((b shr 6) or 0xC0).toByte()
            this.data_[offset + 1] = ((b and 0x3F) or 0x80).toByte()
            offset += 2
        }
        else if (b <= 0xFFFF)
        {
            this.data_[offset + 0] = ((b shr 12) or 0xE0).toByte()
            this.data_[offset + 1] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data_[offset + 2] = ((b and 0x3F) or 0x80).toByte()
            offset += 3
        }
        else if (b <= 0x1FFFFF)
        {
            this.data_[offset + 0] = ((b shr 18) or 0xF0).toByte()
            this.data_[offset + 1] = (((b shr 12) and 0x3F) or 0x80).toByte()
            this.data_[offset + 2] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data_[offset + 3] = ((b and 0x3F) or 0x80).toByte()
            offset += 4
        }
        else if (b <= 0x3FFFFFF)
        {
            this.data_[offset + 0] = ((b shr 24) or 0xF8).toByte()
            this.data_[offset + 1] = (((b shr 18) and 0x3F) or 0x80).toByte()
            this.data_[offset + 2] = (((b shr 12) and 0x3F) or 0x80).toByte()
            this.data_[offset + 3] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data_[offset + 4] = ((b and 0x3F) or 0x80).toByte()
            offset += 5
        }
        else if (b <= 0x7FFFFFFF)
        {
            this.data_[offset + 0] = ((b shr 30) or 0xFC).toByte()
            this.data_[offset + 1] = (((b shr 24) and 0x3F) or 0x80).toByte()
            this.data_[offset + 2] = (((b shr 18) and 0x3F) or 0x80).toByte()
            this.data_[offset + 3] = (((b shr 12) and 0x3F) or 0x80).toByte()
            this.data_[offset + 4] = (((b shr 6) and 0x3F) or 0x80).toByte()
            this.data_[offset + 5] = ((b and 0x3F) or 0x80).toByte()
            offset += 6
        }
        else
            assert(false)
    }

    fun prependbyte(b: Int)
    {
        reserve(1)
        memmove(data_ + 1, data_, offset)
        data_[0] = b.toByte()
        offset++
    }

    fun writewchar(w: Char)
    {
        write4(w)
    }

    fun writeword(w: Char)
    {
        val newline = '\n'
        if (doindent && !notlinehead && w != newline)
            indent()

        reserve(2)
        (this.data_ + offset).toCharPtr()[0] = w
        offset += 2
    }

    fun writeUTF16(w: Int)
    {
        reserve(4)
        if (w <= 0xFFFF)
        {
            (this.data_ + offset).toCharPtr()[0] = w.toChar()
            offset += 2
        }
        else if (w <= 0x10FFFF)
        {
            (this.data_ + offset).toCharPtr()[0] = ((w shr 10) + 0xD7C0).toChar()
            (this.data_ + offset + 2) .toCharPtr()[0]= ((w and 0x3FF) or 0xDC00).toChar()
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
        (this.data_ + offset).toIntPtr()[0] = w
        offset += 4
    }

    fun write(buf: OutBuffer?)
    {
        if (buf !== null)
        {
            reserve(buf.offset)
            memcpy(data_ + offset, buf.data_, buf.offset)
            offset += buf.offset
        }
    }

    extern (C++) void write(RootObject obj) /*nothrow*/
    {
        if (obj)
        {
            writestring(obj.toChars())
        }
    }

    extern (C++) void fill0(size_t nbytes) pure nothrow
    {
        reserve(nbytes)
        memset(data_ + offset, 0, nbytes)
        offset += nbytes
    }

    extern (C++) void vprintf(const(char)* format, va_list args) nothrow
    {
        int count
        if (doindent)
            write(null, 0) // perform indent
        uint psize = 128
        for ()
        {
            reserve(psize)
            version (Windows)
            {
                count = _vsnprintf(cast(char*)data_ + offset, psize, format, args)
                if (count != -1)
                    break
                psize *= 2
            }
            else version (Posix)
        {
            va_list va
            va_copy(va, args)
            /*
             The functions vprintf(), vfprintf(), vsprintf(), vsnprintf()
             are equivalent to the functions printf(), fprintf(), sprintf(),
             snprintf(), respectively, except that they are called with a
             va_list instead of a variable number of arguments. These
             functions do not call the va_end macro. Consequently, the value
             of ap is undefined after the call. The application should call
             va_end(ap) itself afterwards.
             */
            count = vsnprintf(cast(char*)data_ + offset, psize, format, va)
            va_end(va)
            if (count == -1)
                psize *= 2
            else if (count >= psize)
                psize = count + 1
            else
                break
        }
            else
            {
                assert(0)
            }
        }
        offset += count
    }

    extern (C++) void printf(const(char)* format, ...) nothrow
    {
        va_list ap
        va_start(ap, format)
        vprintf(format, ap)
        va_end(ap)
    }

    /**************************************
     * Convert `u` to a string and append it to the buffer.
     * Params:
     *  u = integral value to append
     */
    extern (C++) void print(ulong u) pure nothrow
    {
        //import core.internal.string  // not available
        UnsignedStringBuf buf = void
        writestring(unsignedToTempString(u, buf))
    }

    extern (C++) void bracket(char left, char right) pure nothrow
    {
        reserve(2)
        memmove(data_ + 1, data_, offset)
        data_[0] = left
        data_[offset + 1] = right
        offset += 2
    }

    /******************
     * Insert left at i, and right at j.
     * Return index just past right.
     */
    extern (C++) size_t bracket(size_t i, const(char)* left, size_t j, const(char)* right) pure nothrow
    {
        size_t leftlen = strlen(left)
        size_t rightlen = strlen(right)
        reserve(leftlen + rightlen)
        insert(i, left, leftlen)
        insert(j + leftlen, right, rightlen)
        return j + leftlen + rightlen
    }

    extern (C++) void spread(size_t offset, size_t nbytes) pure nothrow
    {
        reserve(nbytes)
        memmove(data_ + offset + nbytes, data_ + offset, this.offset - offset)
        this.offset += nbytes
    }

    /****************************************
     * Returns: offset + nbytes
     */
    extern (C++) size_t insert(size_t offset, const(void)* p, size_t nbytes) pure nothrow
    {
        spread(offset, nbytes)
        memmove(data_ + offset, p, nbytes)
        return offset + nbytes
    }

    size_t insert(size_t offset, const(char)[] s) pure nothrow
    {
        return insert(offset, s.ptr, s.length)
    }

    extern (C++) void remove(size_t offset, size_t nbytes) pure nothrow @nogc
    {
        memmove(data_ + offset, data_ + offset + nbytes, this.offset - (offset + nbytes))
        this.offset -= nbytes
    }

    extern (D) const(char)[] peekSlice() pure nothrow @nogc
    {
        return (cast(const char*)data_)[0 .. offset]
    }

    /***********************************
     * Extract the data_ as a slice and take ownership of it.
     */
    extern (D) char[] extractSlice() pure nothrow @nogc
    {
        auto length = offset
        auto p = extractdata_()
        return p[0 .. length]
    }

    // Append terminating null if necessary and get view of internal buffer
    extern (C++) char* peekChars() pure nothrow
    {
        if (!offset || data_[offset - 1] != '\0')
        {
            writeByte(0)
            offset-- // allow appending more
        }
        return cast(char*)data_
    }

    // Append terminating null if necessary and take ownership of data_
    extern (C++) char* extractChars() pure nothrow
    {
        if (!offset || data_[offset - 1] != '\0')
            writeByte(0)
        return extractdata_()
    }

}