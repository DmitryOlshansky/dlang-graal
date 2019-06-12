package org.dlang.dmd.root

data class StringValue(@JvmField var hash: Int, @JvmField var ptrvalue: Any?)

class StringTable {
    private val table = HashMap<ByteSlice, StringValue?>()

    fun _init(size: Int) {}

    fun reset(size: Int) {
        table.clear()
    }

    /**
    Looks up the given string in the string table and returns its associated
        value.

        Params:
        s = the string to look up
        length = the length of $(D_PARAM s)
        str = the string to look up

        Returns: the string's associated value, or `null` if the string doesn't
        exist in the string table
     */
    fun lookup(str: ByteSlice): StringValue? = table[str]

    /// ditto
    fun lookup(s: BytePtr, length: Int) = lookup(s.slice(0, length))

    /**
        Inserts the given string and the given associated value into the string
        table.

        Params:
        s = the string to insert
        length = the length of $(D_PARAM s)
        ptrvalue = the value to associate with the inserted string
        str = the string to insert
        value = the value to associate with the inserted string

        Returns: the newly inserted value, or `null` if the string table already
        contains the string
    */
    fun insert(str: ByteSlice, ptrvalue: Any?): StringValue? {
        if (table[str] !== null) return null
        else {
            val value = StringValue(str.hashCode(), ptrvalue)
            table[str] = value
            return value
        }
    }

    /// ditto
    fun insert(s: BytePtr, length: Int, value: Any?) = insert(s.slice(0, length), value)

    fun update(str: ByteSlice): StringValue {
        val value = StringValue(str.hashCode(), null)
        table[str] = value
        // printf("update %.*s %p\n", cast(int)str.length, str.ptr, table[i].value ?: NULL);
        return value
    }

    fun update(s: BytePtr, length: Int) = update(s.slice(0, length))

    /********************************
     * Walk the contents of the string table,
     * calling fp for each entry.
     * Params:
     *      fp = function to call. Returns !=0 to stop
     * Returns:
     *      last return value of fp call
     */
    fun apply(func: (StringValue) -> Int): Int
    {
        for (se in table.values)
        {
            if (se?.ptrvalue === null)  continue
            val r = func(se)
            if (r != 0) return r
        }
        return 0
    }

    fun opApply(dg: (StringValue) -> Int): Int = apply(dg)

}