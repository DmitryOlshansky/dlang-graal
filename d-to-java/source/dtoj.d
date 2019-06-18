module dtoj;

import dmd.astcodegen : ASTCodegen;
import dmd.dmodule : Module;
import dmd.dsymbol : Dsymbol;
import dmd.globals : Global;

import std.file : readFile = read, writeFile = write;
import std.getopt, std.path, std.stdio;

import visitors.declaration;

class DiagnosticsException : Exception
{
    import dmd.frontend : Diagnostics;

    Diagnostics diagnostics;

    @nogc @safe pure nothrow this(Diagnostics diagnostics,
        string file = __FILE__, size_t line = __LINE__,
        Throwable nextInChain = null)
    {
        this.diagnostics = diagnostics;
        super(null, file, line, nextInChain);
    }
}

void handleDiagnosticErrors()
{
    import dmd.frontend : Diagnostics;
    import dmd.globals : global;

    if (!global.errors)
        return;

    Diagnostics diagnostics = {
        errors: global.errors,
        warnings: global.warnings
    };

    throw new DiagnosticsException(diagnostics);
}

Module runParser(Ast = ASTCodegen)(
    const string filename,
    const string content,
    const string[] importPaths,
    bool unittests)
{
    import std.algorithm : each;
    import std.range : chain;

    import dmd.frontend : addImport, initDMD, findImportPaths,
        parseModule, parseImportPathsFromConfig;
    import dmd.globals : global;

    global.params.is64bit = false;
    global.params.mscoff = global.params.is64bit;
    global.params.useUnitTests = unittests;

    findImportPaths
        .chain(importPaths)
        .each!addImport;

    auto t = parseModule!Ast(filename, content);

    if (t.diagnostics.hasErrors)
        throw new DiagnosticsException(t.diagnostics);

    return t.module_;
}

Module runSemanticAnalyzer(Module module_, const string[] stringImportPaths)
{
    import std.algorithm : each;

    import dmd.frontend : fullSemantic, addStringImport;

    stringImportPaths.each!addStringImport;

    fullSemantic(module_);
    handleDiagnosticErrors();

    return module_;
}

void main(string[] args) {
    import dmd.frontend : initDMD;
	string[] importPaths;
	string[] stringImportPaths;
    string outputDir = ".";
    bool unittests;
	auto resp = getopt(args,
		"I", &importPaths,
		"J", &stringImportPaths,
        "u|unittest", &unittests,
        "out", &outputDir
	);

	if (resp.helpWanted) {
		defaultGetoptPrinter("Some information about the program.", resp.options);
		return;
	}
    Module[] mods = [];
    initDMD(["NoBackend", "NoMain", "MARS"]);
	foreach (source; args[1..$]) {
        stderr.writefln("Parsing %s", baseName(source));
		const content = cast(string)readFile(source);
        mods ~= runParser(source, content, importPaths, unittests);
    }
    foreach (ref m; mods) {
        stderr.writefln("Semantics run %s", m.toString);
        runSemanticAnalyzer(m, stringImportPaths);
    }
    foreach(i, source; args[1..$]) {
        const modName = baseName(source);
        const java = modName[0..$-2] ~ ".java";
        stderr.writefln("\nConverting %s -> %s", modName, java);
        const target = outputDir ~ "/" ~ java;
		writeFile(target, mods[i].toJava);
	}
	
}