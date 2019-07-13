package org.dlang.dmd.entry

import org.dlang.dmd.*
import org.dlang.dmd.errors.*
import org.dlang.dmd.root.*
import org.dlang.dmd.root.filename.*


fun main(args: Array<String>) {
    timeit {
        val arr = args.toMutableList()
        arr.add(0, "dtool")
        dtool.main(RawSlice<ByteSlice>(arr.map { ByteSlice(it) }.toTypedArray()))
    }
}

fun timeit(fn: () -> Unit) {
    val start = System.nanoTime()
    fn()
    val end = System.nanoTime()
    printf(ByteSlice("Total: %f ms\n"), (end - start) / 1000000)
}