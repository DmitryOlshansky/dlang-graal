package org.dlang.dmd.entry

import org.dlang.dmd.errors.*
import org.dlang.dmd.lexer
import org.dlang.dmd.root.*
import org.dlang.dmd.tokens
import org.dlang.dmd.root.filename.*
import org.dlang.dmd.utils.stderr


fun main(args: Array<String>) {
    for (i in args.indices) {
        val buffer = File.read(BytePtr(args[i]))
        if (!buffer.success) {
            fprintf(stderr, ByteSlice("Failed to read from file: %s"), args[i])
            exit(1)
        }
        val buf = buffer.extractData()
        val lex = lexer.Lexer(BytePtr(args[i]), buf.ptr(), 0, buf.length, true, true, StderrDiagnosticReporter(0))
        val dest = FileName.forceExt(FileName.name(BytePtr(args[i])), BytePtr("tk"))
        val out = OutBuffer()
        var i = 0
        while (lex.nextToken() != tokens.TOK.endOfFile) {
            out.printf(ByteSlice("%4d"), lex.token.value.toUByte().toInt())
            if (++i == 20) {
                out.writeByte('\n'.toInt())
                i  = 0
            }
        }
        if (!File.write(dest, out.extractSlice()))
            fprintf(stderr, ByteSlice("Failed to write file: %s"), dest.toString())
    }
}