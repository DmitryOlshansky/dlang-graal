package org.dlang.dmd.entry

import org.dlang.dmd.*
import org.dlang.dmd.errors.*
import org.dlang.dmd.root.*
import org.dlang.dmd.root.filename.*


fun main(args: Array<String>) {
    val outdir = System.getProperty("outdir", ".")
    val tool = System.getProperty("tool", "lex")
    globals.global.params.isLinux = true
    globals.global._init()
    astbase.ASTBase.Type._init()
    timeit {
        for (arg in args) {
            when(tool) {
                "lex" -> processFile(arg, outdir, "tk", ::lex)
                "lispy" -> processFile(arg, outdir, "ast", ::lispy)
            }

        }
    }
}

fun timeit(fn: () -> Unit) {
    val start = System.nanoTime()
    fn()
    val end = System.nanoTime()
    printf(ByteSlice("Total: %f ms\n"), (end - start) / 1000000)
}

fun processFile(arg: String, outdir: String, suffix: String, fn: (BytePtr, ByteSlice) -> ByteSlice) {
    val argz = BytePtr(arg)
    val buffer = File.read(argz)
    if (!buffer.success) {
        fprintf(stderr, ByteSlice("Failed to read from file: %s"), argz)
        exit(1)
    }
    val dest = FileName.forceExt(FileName.name(argz), BytePtr(suffix))
    val buf = buffer.extractData()
    val output = fn(argz, buf)
    val path = ByteSlice("$outdir/$dest")
    if (!File.write(path, output))
        fprintf(stderr, ByteSlice("Failed to write file: %s"), dest.toString())
}

fun lex(argz: BytePtr, buf: ByteSlice): ByteSlice {
    val lex = lexer.Lexer(argz, buf.ptr(), 0, buf.length, true, true, StderrDiagnosticReporter(0))
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
    return out.extractSlice()
}

fun lispy(argz: BytePtr, buf: ByteSlice): ByteSlice {
    val p = parse.ParserASTBase(null, buf, true, StderrDiagnosticReporter(0))
    p.nextToken()
    val decls = p.parseModule()
    val lispPrint = parser.LispyPrint()
    lispPrint.buf = OutBuffer()
    lispPrint.buf.doindent = true
    for (i in 0 until decls.length)
        decls[i]?.accept(lispPrint)
    return lispPrint.buf.extractSlice()
}