package org.dlang.dmd.root

class AssocArray<K,V> {
    val table = HashMap<K, Ptr<Any?>>()

    /**
    Returns: The number of key/value pairs.
     */
    fun length(): Int  = table.size

    /**
    Lookup value associated with `key` and return the address to it. If the `key`
    has not been added, it adds it and returns the address to the new value.

    Params:
    key = key to lookup the value for

    Returns: the address to the value associated with `key`. If `key` does not exist, it
    is added and the address to the new value is returned.
     */
    fun getLvalue(key: K): Ptr<V?> {
        val v = table[key]
        if (v !== null) return v as Ptr<V?>
        else {
            val newValue = Ptr<Any?>(arrayOfNulls(1))
            table[key] = newValue
            return newValue as Ptr<V?>
        }
    }

    /**
    Lookup and return the value associated with `key`, if the `key` has not been
    added, it returns null.

    Params:
    key = key to lookup the value for

    Returns: the value associated with `key` if present, otherwise, null.
     */
    fun opIndex(key: K): Ptr<V?>?  = table[key] as Ptr<V?>?
}