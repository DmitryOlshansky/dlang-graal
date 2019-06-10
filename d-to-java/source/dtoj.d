module dtoj;

import dmd.astcodegen : ASTCodegen;
import dmd.dmodule : Module;
import dmd.dsymbol : Dsymbol;
import dmd.globals : Global;

import std.file : readFile = read;
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


Module runFullFrontend(Ast = ASTCodegen)(
    const string filename,
    const string content,
    const string[] versionIdentifiers,
    const string[] importPaths,
    const string[] stringImportPaths)
{

    return runParser!Ast(filename, content, versionIdentifiers, importPaths)
        .runSemanticAnalyzer(stringImportPaths);
}

Module runParser(Ast = ASTCodegen)(
    const string filename,
    const string content,
    const string[] versionIdentifiers,
    const string[] importPaths)
{
    import std.algorithm : each;
    import std.range : chain;

    import dmd.frontend : addImport, initDMD, findImportPaths,
        parseModule, parseImportPathsFromConfig;
    import dmd.globals : global;

    global.params.mscoff = global.params.is64bit;
    initDMD(versionIdentifiers);

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
	string[] importPaths;
	string[] stringImportPaths;
	auto resp = getopt(args,
		"I", &importPaths,
		"J", &stringImportPaths
	);

	if (resp.helpWanted) {
		defaultGetoptPrinter("Some information about the program.", resp.options);
		return;
	}
	foreach (target; args[1..$]) {
		const content = cast(string)readFile(target);
		auto m = runFullFrontend(baseName(target), content, ["NoBackend", "NoMain", "MARS"], importPaths, stringImportPaths);
		writeln(m.toJava);
	}
	
}