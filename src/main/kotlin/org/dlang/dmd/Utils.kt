package org.dlang.dmd

import org.dlang.dmd.root.*
import org.dlang.dmd.globals.*
import org.dlang.dmd.errors.*
import org.dlang.dmd.root.filename.*

object utils {
    /**
     * Normalize path by turning forward slashes into backslashes
     *
     * Params:
     *   src = Source path, using unix-style ('/') path separators
     *
     * Returns:
     *   A newly-allocated string with '/' turned into backslashes
     */
    @JvmStatic
    fun toWinPath(src: BytePtr?): BytePtr? {
        if (src === null) return null
        val result = strdup(src);
        var p = result;
        while (p[0] != 0.toByte()) {
            if (p[0] == '/'.toByte())
                p[0] = '\\'.toByte()
            p.plus(1)
        }
        return result
    }

    /**
     * Reads a file, terminate the program on error
     *
     * Params:
     *   loc = The line number information from where the call originates
     *   filename = Path to file
     */
    @JvmStatic
    fun readFile(loc: Loc, filename: BytePtr): FileBuffer {
        val result = File.read(filename)

        if (result.success) {
            error(loc, "Error reading file '%s'", filename)
            fatal()
        }
        return FileBuffer(result.extractData())
    }


    /**
     * Writes a file, terminate the program on error
     *
     * Params:
     *   loc = The line number information from where the call originates
     *   filename = Path to file
     *   data = Full content of the file to be written
     */
    @JvmStatic
    fun writeFile(loc: Loc, filename: ByteSlice, data: ByteSlice) {
        ensurePathToNameExists(Loc.initial, filename);
        if (!File.write(filename, data)) {
            error(loc, ByteSlice("Error writing file '%*.s'"), filename.length, filename.ptr())
            fatal()
        }
    }

    /// Ditto
    @JvmStatic
    fun writeFile(loc: Loc, filename: BytePtr, data: BytePtr, size: Int) {
        writeFile(loc, toDString(filename), data.slice(0, size))
    }


    /**
     * Ensure the root path (the path minus the name) of the provided path
     * exists, and terminate the process if it doesn't.
     *
     * Params:
     *   loc = The line number information from where the call originates
     *   name = a path to check (the name is stripped)
     */
    @JvmStatic
    fun ensurePathToNameExists(loc: Loc, name: ByteSlice) {
        val pt = FileName.path(name)
        if (pt.length != 0) {
            if (!FileName.ensurePathExists(pt)) {
                error(loc, BytePtr("cannot create directory %*.s"), pt.length, pt.ptr());
                fatal();
            }
        }
    }

    ///ditto
    @JvmStatic
    fun ensurePathToNameExists(loc: Loc, name: BytePtr) {
        ensurePathToNameExists(loc, toDString(name))
    }

    /**
     * Takes a path, and escapes '(', ')' and backslashes
     *
     * Params:
     *   buf = Buffer to write the escaped path to
     *   fname = Path to escape
     */
    @JvmStatic
    fun escapePath(buf: OutBuffer, fname: BytePtr) {
        while (fname.data.size != fname.offset) {
            when (fname[0]) {
                0.toByte() -> return
                '('.toByte(), ')'.toByte(), '\\'.toByte() -> {
                    buf.writeByte('\\'.toInt())
                    buf.writeByte(fname[0].toInt())
                }
                else -> buf.writeByte(fname[0].toInt())
            }
            fname.plus(1)
        }
    }

    /// Slices a `\0`-terminated C-string, excluding the terminator
    @JvmStatic
    fun toDString(s: BytePtr?): ByteSlice {
        return if (s !== null) s.slice(0, strlen(s)) else ByteSlice("");
    }

    /**
    Compare two slices for equality, in a case-insensitive way

    Comparison is based on `char` and does not do decoding.
    As a result, it's only really accurate for plain ASCII strings.

    Params:
    s1 = string to compare
    s2 = string to compare

    Returns:
    `true` if `s1 == s2` regardless of case
     */
    @JvmStatic
    fun iequals(s1: ByteSlice, s2: ByteSlice): Boolean {

        if (s1.length != s2.length) return false

        for (idx in 0 until s1.length) {
            val c1 = s1[idx]
            val c2 = s2[idx]
            if (c1 != c2)
                if (toupper(c1.toInt()) != toupper(c2.toInt()))
                    return false
        }
        return true
    }

    /**
    Copy the content of `src` into a C-string ('\0' terminated) then call `dg`

    The intent of this function is to provide an allocation-less
    way to call a C function using a D slice.
    The function internally allocates a buffer if needed, but frees it on exit.

    Note:
    The argument to `dg` is `scope`. To keep the data around after `dg` exits,
    one has to copy it.

    Params:
    src = Slice to use to call the C function
    dg  = Delegate to call afterwards

    Returns:
    The return value of `T`
     */
    @JvmStatic
    fun <T> toCStringThen(dg: (BytePtr) -> T, src: ByteSlice): T {
        val bytes = src.data.copyOf(src.data.size + 1)
        bytes[src.end] = 0
        return dg(BytePtr(bytes, src.beg))
    }

    @JvmStatic
    val stdout = _IO_FILE(System.out)

    @JvmStatic
    val stderr = _IO_FILE(System.err)
}
