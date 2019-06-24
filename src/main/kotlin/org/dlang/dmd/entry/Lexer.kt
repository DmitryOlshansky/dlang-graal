package org.dlang.dmd.entry

import org.dlang.dmd.errors.*
import org.dlang.dmd.lexer
import org.dlang.dmd.root.*
import org.dlang.dmd.tokens
import org.dlang.dmd.root.filename.*
import org.dlang.dmd.utils.stderr


fun main(args: Array<String>) {
    val outdir = System.getProperty("outdir", ".")
    for (i in 0 until 10)
        timeit {
            for (arg in args) {
                lex(arg, outdir)
            }
        }
}

fun timeit(fn: () -> Unit) {
    val start = System.nanoTime()
    fn()
    val end = System.nanoTime()
    printf(ByteSlice("Total: %f ms\n"), (end - start) / 1000000)
}

fun lex(arg: String, outdir: String) {
    val argz = BytePtr(arg)
    val buffer = File.read(argz)
    if (!buffer.success) {
        fprintf(stderr, ByteSlice("Failed to read from file: %s"), argz)
        exit(1)
    }
    val buf = buffer.extractData()
    val lex = lexer.Lexer(argz, buf.ptr(), 0, buf.length, true, true, StderrDiagnosticReporter(0))
    val dest = FileName.forceExt(FileName.name(argz), BytePtr("tk"))
    val out = OutBuffer()
    var i = 0
    while (lex.nextToken() != tokens.TOK.endOfFile) {
        out.printf(ByteSlice("%4d"), lex.token.value.toUByte().toInt())
        if (++i == 20) {
            out.printf(ByteSlice(" | Line %5d |\n"), lex.token.loc.linnum)
            i  = 0
        }
    }
    if (i != 0) out.printf(ByteSlice(" | Line %5d |\n"), lex.token.loc.linnum)
    val path = ByteSlice("$outdir/$dest")
    if (!File.write(path, out.extractSlice()))
        fprintf(stderr, ByteSlice("Failed to write file: %s"), dest.toString())
}