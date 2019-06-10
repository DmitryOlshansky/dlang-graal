package org.dlang.dmd.root

class OutBuffer {
    var data_ : BytePtr = BytePtr(16)
    var offset: Int = 0
    var size: Int = 0
    var level: Int = 0
    var doindent: Boolean = false
    private var notlinehead: Boolean = false

    fun extractData() : BytePtr {
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
        writeByte('\n'.toInt())
        if (doindent)
            notlinehead = false
    }

    fun writeByte(b: Int)
    {
        if (doindent && !notlinehead && b != '\n'.toInt())
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
        write4(w.toInt())
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
        memset(data_ + offset, 0, nbytes)
        offset += nbytes
    }

    fun printf(format: ByteSlice, vararg args: Any?)
    {
       writestring(ByteSlice(String.format(format.toString(), args)))
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
        memmove(data_ + 1, data_, offset)
        data_[0] = left
        data_[offset + 1] = right
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
        memmove(data_ + offset + nbytes, data_ + offset, this.offset - offset)
        this.offset += nbytes
    }

    /****************************************
     * Returns: offset + nbytes
     */
    fun insert(offset: Int, p: BytePtr, nbytes: Int): Int
    {
        spread(offset, nbytes)
        memmove(data_ + offset, p, nbytes)
        return offset + nbytes
    }

    fun insert(offset: Int,  s: ByteSlice): Int
    {
        return insert(offset, s.ptr(), s.length)
    }

    fun remove(offset: Int, nbytes: Int)
    {
        memmove(data_ + offset, data_ + offset + nbytes, this.offset - (offset + nbytes))
        this.offset -= nbytes
    }

    fun peekSlice(): ByteSlice
    {
        return data_.slice(0, offset)
    }

    /***********************************
     * Extract the data_ as a slice and take ownership of it.
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
        if (offset == 0 || data_[offset - 1] != 0.toByte())
        {
            writeByte(0)
            offset-- // allow appending more
        }
        return data_
    }

    // Append terminating null if necessary and take ownership of data_
    fun extractChars(): BytePtr
    {
        if (offset == 0 || data_[offset - 1] != 0.toByte())
            writeByte(0)
        return extractData()
    }
}