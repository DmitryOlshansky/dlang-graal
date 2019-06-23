module dlex;

import core.stdc.stdio, core.stdc.string;

import std.string;
import std.getopt;

import dmd.errors;
import dmd.globals;
import dmd.lexer;
import dmd.tokens;

import dmd.root.file;
import dmd.root.filename;
import dmd.root.outbuffer;


int main(string[] args)
{
	string outdir = ".";
	auto res = getopt(args,
		"outdir", "output directory", &outdir
	);
	if (res.helpWanted) {
		defaultGetoptPrinter("Trivial D lexer based on DMD.", res.options);
		return 1;
	}
	foreach(arg; args[1..$]) {
		auto argz = arg.toStringz;
		auto buffer = File.read(argz);
        if (!buffer.success) {
            fprintf(stderr, "Failed to read from file: %s", argz);
            return 2;
        }
        auto buf = buffer.extractData();
        scope lex = new Lexer(argz, cast(char*)buf.ptr, 0, buf.length, true, true, new StderrDiagnosticReporter(DiagnosticReporting.error));
        auto dest = FileName.forceExt(FileName.name(argz), "tk");
        auto filePath = outdir ~ "/" ~ dest[0..strlen(dest)];
        scope output = new OutBuffer();
        int i = 0;
        while (lex.nextToken() != TOK.endOfFile) {
            output.printf("%4d", lex.token.value);
            if (++i == 20) {
                output.printf(" | Line %5d |\n", lex.token.loc.linnum);
                i  = 0;
            }
        }
        if (i != 0) output.printf(" | Line %5d |\n", lex.token.loc.linnum);
        if (!File.write(filePath.toStringz, output.extractSlice()))
            fprintf(stderr, "Failed to write file: %s\n", dest);
	}
	return 0;
}
