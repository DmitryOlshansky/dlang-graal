package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.builtin.*;
import static org.dlang.dmd.cli.*;
import static org.dlang.dmd.compiler.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.console.*;
import static org.dlang.dmd.dinifile.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.doc.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.filecache.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.inline.*;
import static org.dlang.dmd.json.*;
import static org.dlang.dmd.lib.*;
import static org.dlang.dmd.link.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.utils.*;

public class mars {

    private static class CheckOptions 
    {
        public static final int success = 0;
        public static final int error = 1;
        public static final int help = 2;
    }


    public static void logo() {
        printf(new BytePtr("DMD%llu D Compiler %.*s\n%.*s %.*s\n"), 32L, global._version.getLength() - 1, toBytePtr(global._version), global.copyright.getLength(), toBytePtr(global.copyright), global.written.getLength(), toBytePtr(global.written));
    }

    public static void printInternalFailure(_IO_FILE stream) {
        fputs(new BytePtr("---\nERROR: This is a compiler bug.\nPlease report it via https://issues.dlang.org/enter_bug.cgi\nwith, preferably, a reduced, reproducible example and the information below.\nDustMite (https://github.com/CyberShadow/DustMite/wiki) can help with the reduction.\n---\n"), stream);
        fprintf(stream, new BytePtr("DMD %.*s\n"), global._version.getLength() - 1, toBytePtr(global._version));
        printPredefinedVersions(stream);
        printGlobalConfigs(stream);
        fputs(new BytePtr("---\n"), stream);
    }

    public static void usage() {
        logo();
        ByteSlice help = CLIUsage.usage().copy();
        ByteSlice inifileCanon = FileName.canonicalName(global.inifilename).copy();
        printf(new BytePtr("\nDocumentation: https://dlang.org/\nConfig file: %.*s\nUsage:\n  dmd [<option>...] <file>...\n  dmd [<option>...] -run <file> [<arg>...]\n\nWhere:\n  <file>           D source file\n  <arg>            Argument to pass when running the resulting program\n\n<option>:\n  @<cmdfile>       read arguments from cmdfile\n%.*s"), inifileCanon.getLength(), toBytePtr(inifileCanon), help.getLength(), help.get(0));
    }

    public static int tryMain(int argc, Ptr<BytePtr> argv, Param params) {
        DArray<BytePtr> files = new DArray<BytePtr>();
        try {
            DArray<BytePtr> libmodules = new DArray<BytePtr>();
            try {
                global._init();
                if ((argc < 1) || (argv == null))
                {
                /*Largs:*/
                    error(Loc.initial, new BytePtr("missing or null command line arguments"));
                    fatal();
                }
                DArray<BytePtr> arguments = arguments = new DArray<BytePtr>(argc);
                try {
                    {
                        int i = 0;
                    L_outer1:
                        for (; (i < argc);i++){
                            if (argv.get(i) == null)
                                /*goto Largs*/throw Dispatch0.INSTANCE;
                            arguments.set(i, argv.get(i));
                        }
                    }
                    if (response_expand(arguments))
                        error(Loc.initial, new BytePtr("can't open response file"));
                    files.reserve(arguments.length - 1);
                    params.argv0 = toDString(arguments.get(0)).copy();
                    global.inifilename = parse_conf_arg(arguments).copy();
                    if (global.inifilename.getLength() != 0)
                    {
                        if ((global.inifilename.getLength() != 0) && (FileName.exists(global.inifilename) == 0))
                            error(Loc.initial, new BytePtr("Config file '%.*s' does not exist."), global.inifilename.getLength(), toBytePtr(global.inifilename));
                    }
                    else
                    {
                        global.inifilename = findConfFile(params.argv0, new ByteSlice("dmd.conf")).copy();
                    }
                    File.ReadResult iniReadResult = toCStringThen.invoke(global.inifilename).copy();
                    try {
                        ByteSlice inifileBuffer = iniReadResult.buffer.data.copy();
                        ByteSlice inifilepath = FileName.path(global.inifilename).copy();
                        DArray<BytePtr> sections = new DArray<BytePtr>();
                        try {
                            StringTable environment = new StringTable();
                            try {
                                environment._init(7);
                                sections.push(new BytePtr("Environment"));
                                parseConfFile(environment, global.inifilename, inifilepath, inifileBuffer, sections);
                                BytePtr arch = pcopy(params.is64bit ? new BytePtr("64") : new BytePtr("32"));
                                arch = pcopy(parse_arch_arg(arguments, arch));
                                {
                                    DArray<BytePtr> dflags = new DArray<BytePtr>();
                                    try {
                                        getenv_setargv(readFromEnv(environment, new BytePtr("DFLAGS")), dflags);
                                        environment.reset(7);
                                        arch = pcopy(parse_arch_arg(dflags, arch));
                                    }
                                    finally {
                                    }
                                }
                                boolean is64bit = (arch.get(0) & 0xFF) == 54;
                                ByteSlice envsection = new ByteSlice(new byte[80]);
                                sprintf(ptr(envsection), new BytePtr("Environment%s"), arch);
                                sections.push(ptr(envsection));
                                parseConfFile(environment, global.inifilename, inifilepath, inifileBuffer, sections);
                                getenv_setargv(readFromEnv(environment, new BytePtr("DFLAGS")), arguments);
                                updateRealEnvironment(environment);
                                environment.reset(1);
                                if (parseCommandLine(arguments, argc, params, files))
                                {
                                    Loc loc = new Loc();
                                    errorSupplemental(loc, new BytePtr("run `dmd` to print the compiler manual"));
                                    errorSupplemental(loc, new BytePtr("run `dmd -man` to open browser on manual"));
                                    return 1;
                                }
                                if (params.usage)
                                {
                                    usage();
                                    return 0;
                                }
                                if (params.logo)
                                {
                                    logo();
                                    return 0;
                                }
                                Function1<ByteSlice,Integer> printHelpUsage = new Function1<ByteSlice,Integer>(){
                                    public Integer invoke(ByteSlice help){
                                        printf(new BytePtr("%.*s"), help.getLength(), help.get(0));
                                        return global.errors != 0 ? 1 : 0;
                                    }
                                };
                                Function1<Slice<ByteSlice>,ByteSlice> generateUsageChecks = new Function1<Slice<ByteSlice>,ByteSlice>(){
                                    public ByteSlice invoke(Slice<ByteSlice> params){
                                        ByteSlice s = new ByteSlice();
                                        {
                                            Slice<ByteSlice> __r1514 = params.copy();
                                            int __key1515 = 0;
                                            for (; (__key1515 < __r1514.getLength());__key1515 += 1) {
                                                ByteSlice n = __r1514.get(__key1515).copy();
                                                s.append(new ByteSlice("\n                if (params.").concat(n).concat(new ByteSlice("Usage)\n                    return printHelpUsage(CLIUsage.")).concat(n).concat(new ByteSlice("Usage);\n            ")));
                                            }
                                        }
                                        return s;
                                    }
                                };
                                if (params.mcpuUsage)
                                    return printHelpUsage.invoke(new ByteSlice("CPU architectures supported by -mcpu=id:\n  =[h|help|?]    list information on all available choices\n  =baseline      use default architecture as determined by target\n  =avx           use AVX 1 instructions\n  =avx2          use AVX 2 instructions\n  =native        use CPU architecture that this compiler is running on\n"));
                                if (params.transitionUsage)
                                    return printHelpUsage.invoke(new ByteSlice("Language transitions listed by -transition=name:\n  =all              list information on all language transitions\n  =field            list all non-mutable fields which occupy an object instance\n  =complex          give deprecation messages about all usages of complex or imaginary types\n  =tls              list all variables going into thread local storage\n  =vmarkdown        list instances of Markdown replacements in Ddoc\n"));
                                if (params.checkUsage)
                                    return printHelpUsage.invoke(new ByteSlice("Enable or disable specific checks:\n  =[h|help|?]           List information on all available choices\n  =assert[=[on|off]]    Assertion checking\n  =bounds[=[on|off]]    Array bounds checking\n  =in[=[on|off]]        Generate In contracts\n  =invariant[=[on|off]] Class/struct invariants\n  =out[=[on|off]]       Out contracts\n  =switch[=[on|off]]    Final switch failure checking\n  =on                   Enable all assertion checking\n                        (default for non-release builds)\n  =off                  Disable all assertion checking\n"));
                                if (params.checkActionUsage)
                                    return printHelpUsage.invoke(new ByteSlice("Behavior on assert/boundscheck/finalswitch failure:\n  =[h|help|?]    List information on all available choices\n  =D             Usual D behavior of throwing an AssertError\n  =C             Call the C runtime library assert failure function\n  =halt          Halt the program execution (very lightweight)\n  =context       Use D assert with context information (when available)\n"));
                                if (params.previewUsage)
                                    return printHelpUsage.invoke(new ByteSlice("Upcoming language changes listed by -preview=name:\n  =all              list information on all upcoming language changes\n  =dip25            implement https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md (Sealed references)\n  =dip1000          implement https://github.com/dlang/DIPs/blob/master/DIPs/other/DIP1000.md (Scoped Pointers)\n  =dip1008          implement https://github.com/dlang/DIPs/blob/master/DIPs/DIP1008.md (@nogc Throwable)\n  =fieldwise        use fieldwise comparisons for struct equality\n  =markdown         enable Markdown replacements in Ddoc\n  =fixAliasThis     when a symbol is resolved, check alias this scope before going to upper scopes\n  =intpromote       fix integral promotions for unary + - ~ operators\n  =dtorfields       destruct fields of partially constructed objects\n  =rvaluerefparam   enable rvalue arguments to ref parameters\n"));
                                if (params.revertUsage)
                                    return printHelpUsage.invoke(new ByteSlice("Revertable language changes listed by -revert=name:\n  =all              list information on all revertable language changes\n  =dip25            revert DIP25 changes https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md\n"));
                                if (params.externStdUsage)
                                    return printHelpUsage.invoke(new ByteSlice("Available C++ standards:\n  =[h|help|?]           List information on all available choices\n  =c++98                Sets `__traits(getTargetInfo, \"cppStd\")` to `199711`\n  =c++11                Sets `__traits(getTargetInfo, \"cppStd\")` to `201103`\n  =c++14                Sets `__traits(getTargetInfo, \"cppStd\")` to `201402`\n  =c++17                Sets `__traits(getTargetInfo, \"cppStd\")` to `201703`\n"));
                                if (params.manual)
                                {
                                    browse(new BytePtr("http://dlang.org/dmd-linux.html"));
                                    return 0;
                                }
                                if (params.color)
                                    global.console = pcopy((Console.create(stderr)));
                                setTarget(params);
                                setTargetCPU(params);
                                if (((params.is64bit ? 1 : 0) != (is64bit ? 1 : 0)))
                                    error(Loc.initial, new BytePtr("the architecture must not be changed in the %s section of %.*s"), ptr(envsection), global.inifilename.getLength(), toBytePtr(global.inifilename));
                                if (global.errors != 0)
                                {
                                    fatal();
                                }
                                if ((files.length == 0))
                                {
                                    if (params.jsonFieldFlags != 0)
                                    {
                                        generateJson(null);
                                        return 0;
                                    }
                                    usage();
                                    return 1;
                                }
                                reconcileCommands(params, files.length);
                                if (params.versionids != null)
                                {
                                    Slice<BytePtr> __r1516 = (params.versionids).opSlice().copy();
                                    int __key1517 = 0;
                                    for (; (__key1517 < __r1516.getLength());__key1517 += 1) {
                                        BytePtr charz = pcopy(__r1516.get(__key1517));
                                        VersionCondition.addGlobalIdent(charz.slice(0,strlen(charz)));
                                    }
                                }
                                if (params.debugids != null)
                                {
                                    Slice<BytePtr> __r1518 = (params.debugids).opSlice().copy();
                                    int __key1519 = 0;
                                    for (; (__key1519 < __r1518.getLength());__key1519 += 1) {
                                        BytePtr charz = pcopy(__r1518.get(__key1519));
                                        DebugCondition.addGlobalIdent(charz.slice(0,strlen(charz)));
                                    }
                                }
                                setTarget(params);
                                addDefaultVersionIdentifiers(params);
                                setDefaultLibrary();
                                Type._init();
                                Id.initialize();
                                dmodule.Module._init();
                                target._init(params);
                                Expression._init();
                                Objc._init();
                                builtin_init();
                                FileCache._init();
                                CTFloat.initialize();
                                if (params.verbose)
                                {
                                    printPredefinedVersions(stdout);
                                    printGlobalConfigs(stdout);
                                }
                                Function1<DArray<BytePtr>,DArray<BytePtr>> buildPath = new Function1<DArray<BytePtr>,DArray<BytePtr>>(){
                                    public DArray<BytePtr> invoke(DArray<BytePtr> imppath){
                                        DArray<BytePtr> result = null;
                                        if (imppath != null)
                                        {
                                            {
                                                Slice<BytePtr> __r1520 = (imppath).opSlice().copy();
                                                int __key1521 = 0;
                                                for (; (__key1521 < __r1520.getLength());__key1521 += 1) {
                                                    BytePtr path = pcopy(__r1520.get(__key1521));
                                                    DArray<BytePtr> a = FileName.splitPath(path);
                                                    if (a != null)
                                                    {
                                                        if (result == null)
                                                            result = new DArray<BytePtr>();
                                                        (result).append(a);
                                                    }
                                                }
                                            }
                                        }
                                        return result;
                                    }
                                };
                                if (params.mixinFile != null)
                                {
                                    params.mixinOut = (OutBuffer)calloc(1, 20);
                                    atexit(mars::flushMixins);
                                }
                                try {
                                    global.path = buildPath.invoke(params.imppath);
                                    global.filePath = buildPath.invoke(params.fileImppath);
                                    if (params.addMain)
                                        files.push(new BytePtr("__main.d"));
                                    DArray<dmodule.Module> modules = createModules(files, libmodules).copy();
                                    try {
                                        boolean ASYNCREAD = false;
                                        {
                                            Slice<dmodule.Module> __r1522 = modules.opSlice().copy();
                                            int __key1523 = 0;
                                            for (; (__key1523 < __r1522.getLength());__key1523 += 1) {
                                                dmodule.Module m = __r1522.get(__key1523);
                                                if (params.addMain && __equals(m.srcfile.asString(), new ByteSlice("__main.d")))
                                                {
                                                    ByteSlice data = arraydup(new ByteSlice("int main(){return 0;}\u0000\u0000")).copy();
                                                    m.srcBuffer = new FileBuffer(toByteSlice(data.slice(0,data.getLength() - 2)));
                                                }
                                                else if (__equals(m.srcfile.asString(), new ByteSlice("__stdin.d")))
                                                {
                                                    FileBuffer buffer = readFromStdin().copy();
                                                    m.srcBuffer = new FileBuffer(buffer.extractData());
                                                }
                                            }
                                        }
                                        {
                                            Slice<dmodule.Module> __r1524 = modules.opSlice().copy();
                                            int __key1525 = 0;
                                            for (; (__key1525 < __r1524.getLength());__key1525 += 1) {
                                                dmodule.Module m = __r1524.get(__key1525);
                                                expr(m.read(Loc.initial));
                                            }
                                        }
                                        boolean anydocfiles = false;
                                        int filecount = modules.length;
                                        {
                                            int filei = 0;
                                            int modi = 0;
                                            for (; (filei < filecount);comma(filei++, modi++)){
                                                dmodule.Module m = modules.get(modi);
                                                if (params.verbose)
                                                    message(new BytePtr("parse     %s"), m.toChars());
                                                if (dmodule.Module.rootModule == null)
                                                    dmodule.Module.rootModule = m;
                                                m.importedFrom = m;
                                                if (!params.oneobj || (modi == 0) || m.isDocFile)
                                                    m.deleteObjFile();
                                                m.parse();
                                                if (m.isHdrFile)
                                                {
                                                    {
                                                        int j = 0;
                                                        for (; (j < params.objfiles.length);j++){
                                                            if ((m.objfile.toChars() == params.objfiles.get(j)))
                                                            {
                                                                params.objfiles.remove(j);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if ((params.objfiles.length == 0))
                                                        expr(params.link = false);
                                                }
                                                if (m.isDocFile)
                                                {
                                                    expr(anydocfiles = true);
                                                    gendocfile(m);
                                                    modules.remove(modi);
                                                    modi--;
                                                    {
                                                        int j = 0;
                                                        for (; (j < params.objfiles.length);j++){
                                                            if ((m.objfile.toChars() == params.objfiles.get(j)))
                                                            {
                                                                params.objfiles.remove(j);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if ((params.objfiles.length == 0))
                                                        expr(params.link = false);
                                                }
                                            }
                                        }
                                        if (anydocfiles && (modules.length != 0) && params.oneobj || (params.objname.getLength() != 0))
                                        {
                                            error(Loc.initial, new BytePtr("conflicting Ddoc and obj generation options"));
                                            fatal();
                                        }
                                        if (global.errors != 0)
                                            fatal();
                                        if (params.doHdrGeneration)
                                        {
                                            {
                                                Slice<dmodule.Module> __r1526 = modules.opSlice().copy();
                                                int __key1527 = 0;
                                                for (; (__key1527 < __r1526.getLength());__key1527 += 1) {
                                                    dmodule.Module m = __r1526.get(__key1527);
                                                    if (m.isHdrFile)
                                                        continue;
                                                    if (params.verbose)
                                                        message(new BytePtr("import    %s"), m.toChars());
                                                    genhdrfile(m);
                                                }
                                            }
                                        }
                                        if (global.errors != 0)
                                            fatal();
                                        {
                                            Slice<dmodule.Module> __r1528 = modules.opSlice().copy();
                                            int __key1529 = 0;
                                            for (; (__key1529 < __r1528.getLength());__key1529 += 1) {
                                                dmodule.Module m = __r1528.get(__key1529);
                                                if (params.verbose)
                                                    message(new BytePtr("importall %s"), m.toChars());
                                                m.importAll(null);
                                            }
                                        }
                                        if (global.errors != 0)
                                            fatal();
                                        backend_init();
                                        {
                                            Slice<dmodule.Module> __r1530 = modules.opSlice().copy();
                                            int __key1531 = 0;
                                            for (; (__key1531 < __r1530.getLength());__key1531 += 1) {
                                                dmodule.Module m = __r1530.get(__key1531);
                                                if (params.verbose)
                                                    message(new BytePtr("semantic  %s"), m.toChars());
                                                dsymbolSemantic(m, null);
                                            }
                                        }
                                        dmodule.Module.dprogress = 1;
                                        dmodule.Module.runDeferredSemantic();
                                        if (dmodule.Module.deferred.length != 0)
                                        {
                                            {
                                                int i = 0;
                                                for (; (i < dmodule.Module.deferred.length);i++){
                                                    Dsymbol sd = dmodule.Module.deferred.get(i);
                                                    sd.error(new BytePtr("unable to resolve forward reference in definition"));
                                                }
                                            }
                                        }
                                        {
                                            Slice<dmodule.Module> __r1532 = modules.opSlice().copy();
                                            int __key1533 = 0;
                                            for (; (__key1533 < __r1532.getLength());__key1533 += 1) {
                                                dmodule.Module m = __r1532.get(__key1533);
                                                if (params.verbose)
                                                    message(new BytePtr("semantic2 %s"), m.toChars());
                                                semantic2(m, null);
                                            }
                                        }
                                        dmodule.Module.runDeferredSemantic2();
                                        if (global.errors != 0)
                                            fatal();
                                        {
                                            Slice<dmodule.Module> __r1534 = modules.opSlice().copy();
                                            int __key1535 = 0;
                                            for (; (__key1535 < __r1534.getLength());__key1535 += 1) {
                                                dmodule.Module m = __r1534.get(__key1535);
                                                if (params.verbose)
                                                    message(new BytePtr("semantic3 %s"), m.toChars());
                                                semantic3(m, null);
                                            }
                                        }
                                        if (includeImports)
                                        {
                                            {
                                                int i = 0;
                                                for (; (i < compiledImports.length);i++){
                                                    dmodule.Module m = compiledImports.get(i);
                                                    assert(m.isRoot());
                                                    if (params.verbose)
                                                        message(new BytePtr("semantic3 %s"), m.toChars());
                                                    semantic3(m, null);
                                                    modules.push(m);
                                                }
                                            }
                                        }
                                        dmodule.Module.runDeferredSemantic3();
                                        if (global.errors != 0)
                                            fatal();
                                        if (params.useInline)
                                        {
                                            {
                                                Slice<dmodule.Module> __r1536 = modules.opSlice().copy();
                                                int __key1537 = 0;
                                                for (; (__key1537 < __r1536.getLength());__key1537 += 1) {
                                                    dmodule.Module m = __r1536.get(__key1537);
                                                    if (params.verbose)
                                                        message(new BytePtr("inline scan %s"), m.toChars());
                                                    inlineScanModule(m);
                                                }
                                            }
                                        }
                                        if ((global.errors != 0) || (global.warnings != 0))
                                            fatal();
                                        {
                                            OutBuffer ob = params.moduleDeps;
                                            if ((ob) != null)
                                            {
                                                {
                                                    int __key1538 = 1;
                                                    int __limit1539 = modules.get(0).aimports.length;
                                                    for (; (__key1538 < __limit1539);__key1538 += 1) {
                                                        int i = __key1538;
                                                        semantic3OnDependencies((__dop1540.get(i)));
                                                    }
                                                }
                                                ByteSlice data = (ob).peekSlice().copy();
                                                if (params.moduleDepsFile.getLength() != 0)
                                                    writeFile(Loc.initial, params.moduleDepsFile, toByteSlice(data));
                                                else
                                                    printf(new BytePtr("%.*s"), data.getLength(), toBytePtr(data));
                                            }
                                        }
                                        printCtfePerformanceStats();
                                        Library library = null;
                                        if (params.lib)
                                        {
                                            if ((params.objfiles.length == 0))
                                            {
                                                error(Loc.initial, new BytePtr("no input files"));
                                                return 1;
                                            }
                                            library = Library.factory();
                                            library.setFilename(params.objdir, params.libname);
                                            {
                                                Slice<BytePtr> __r1541 = libmodules.opSlice().copy();
                                                int __key1542 = 0;
                                                for (; (__key1542 < __r1541.getLength());__key1542 += 1) {
                                                    BytePtr p = pcopy(__r1541.get(__key1542));
                                                    library.addObject(p, new ByteSlice());
                                                }
                                            }
                                        }
                                        if (params.doJsonGeneration)
                                        {
                                            generateJson(modules);
                                        }
                                        if ((global.errors == 0) && params.doDocComments)
                                        {
                                            {
                                                Slice<dmodule.Module> __r1543 = modules.opSlice().copy();
                                                int __key1544 = 0;
                                                for (; (__key1544 < __r1543.getLength());__key1544 += 1) {
                                                    dmodule.Module m = __r1543.get(__key1544);
                                                    gendocfile(m);
                                                }
                                            }
                                        }
                                        if (params.vcg_ast)
                                        {
                                            {
                                                Slice<dmodule.Module> __r1545 = modules.opSlice().copy();
                                                int __key1546 = 0;
                                                for (; (__key1546 < __r1545.getLength());__key1546 += 1) {
                                                    dmodule.Module mod = __r1545.get(__key1546);
                                                    OutBuffer buf = new OutBuffer(null, 0, 0, 0, false, false).copy();
                                                    try {
                                                        expr(buf.doindent = true);
                                                        moduleToBuffer(buf, mod);
                                                        ByteSlice cgFilename = FileName.addExt(mod.srcfile.asString(), new ByteSlice("cg")).copy();
                                                        expr(File.write(toBytePtr(cgFilename), toByteSlice(buf.peekSlice())));
                                                    }
                                                    finally {
                                                    }
                                                }
                                            }
                                        }
                                        if (!params.obj)
                                        {
                                        }
                                        else if (params.oneobj)
                                        {
                                            dmodule.Module firstm = null;
                                            {
                                                Slice<dmodule.Module> __r1547 = modules.opSlice().copy();
                                                int __key1548 = 0;
                                                for (; (__key1548 < __r1547.getLength());__key1548 += 1) {
                                                    dmodule.Module m = __r1547.get(__key1548);
                                                    if (m.isHdrFile)
                                                        continue;
                                                    if (firstm == null)
                                                    {
                                                        firstm = m;
                                                        obj_start(m.srcfile.toChars());
                                                    }
                                                    if (params.verbose)
                                                        message(new BytePtr("code      %s"), m.toChars());
                                                    genObjFile(m, false);
                                                    if ((entrypoint != null) && (pequals(m, rootHasMain)))
                                                        genObjFile(entrypoint, false);
                                                }
                                            }
                                            if ((global.errors == 0) && (firstm != null))
                                            {
                                                obj_end(library, firstm.objfile.toChars());
                                            }
                                        }
                                        else
                                        {
                                            {
                                                Slice<dmodule.Module> __r1549 = modules.opSlice().copy();
                                                int __key1550 = 0;
                                                for (; (__key1550 < __r1549.getLength());__key1550 += 1) {
                                                    dmodule.Module m = __r1549.get(__key1550);
                                                    if (m.isHdrFile)
                                                        continue;
                                                    if (params.verbose)
                                                        message(new BytePtr("code      %s"), m.toChars());
                                                    obj_start(m.srcfile.toChars());
                                                    genObjFile(m, params.multiobj);
                                                    if ((entrypoint != null) && (pequals(m, rootHasMain)))
                                                        genObjFile(entrypoint, params.multiobj);
                                                    obj_end(library, m.objfile.toChars());
                                                    obj_write_deferred(library);
                                                    if ((global.errors != 0) && !params.lib)
                                                        m.deleteObjFile();
                                                }
                                            }
                                        }
                                        if (params.lib && (global.errors == 0))
                                            library.write();
                                        backend_term();
                                        if (global.errors != 0)
                                            fatal();
                                        int status = 0;
                                        if (params.objfiles.length == 0)
                                        {
                                            if (params.link)
                                                error(Loc.initial, new BytePtr("no object files to link"));
                                        }
                                        else
                                        {
                                            if (params.link)
                                                status = runLINK();
                                            if (params.run)
                                            {
                                                if (status == 0)
                                                {
                                                    status = runProgram();
                                                    {
                                                        Slice<dmodule.Module> __r1551 = modules.opSlice().copy();
                                                        int __key1552 = 0;
                                                        for (; (__key1552 < __r1551.getLength());__key1552 += 1) {
                                                            dmodule.Module m = __r1551.get(__key1552);
                                                            m.deleteObjFile();
                                                            if (params.oneobj)
                                                                break;
                                                        }
                                                    }
                                                    toCStringThen.invoke(params.exefile);
                                                }
                                            }
                                        }
                                        if ((global.errors != 0) || (global.warnings != 0))
                                            fatal();
                                        return status;
                                    }
                                    finally {
                                    }
                                }
                                finally {
                                    flushMixins();
                                }
                            }
                            finally {
                            }
                        }
                        finally {
                        }
                    }
                    finally {
                    }
                }
                finally {
                }
            }
            finally {
            }
        }
        finally {
        }
    }

    public static FileBuffer readFromStdin() {
        int bufIncrement = 131072;
        int pos = 0;
        int sz = 131072;
        BytePtr buffer = null;
        for (; ;){
            buffer = pcopy(((BytePtr)Mem.xrealloc(buffer, sz + 2)));
            do {
                {
                    assert((sz > pos));
                    int rlen = fread((buffer.plus(pos)), 1, sz - pos, stdin);
                    pos += rlen;
                    if (ferror(stdin) != 0)
                    {
                        error(Loc.initial, new BytePtr("cannot read from stdin, errno = %d"), __errno_location());
                        fatal();
                    }
                    if (feof(stdin) != 0)
                    {
                        assert((pos < sz + 2));
                        buffer.set(pos, (byte)0);
                        buffer.set((pos + 1), (byte)0);
                        return new FileBuffer(buffer.slice(0,pos));
                    }
                }
            } while ((pos < sz));
            sz += 131072;
        }
        throw new AssertionError("Unreachable code!");
    }

    public static void generateJson(DArray<dmodule.Module> modules) {
        OutBuffer buf = new OutBuffer();
        try {
            json_generate(buf, modules);
            ByteSlice name = global.params.jsonfilename.copy();
            if (__equals(name, new ByteSlice("-")))
            {
                int n = fwrite(buf.data, 1, buf.offset, stdout);
                assert((n == buf.offset));
            }
            else
            {
                ByteSlice jsonfilename = new ByteSlice();
                if (name.getLength() != 0)
                {
                    jsonfilename = FileName.defaultExt(name, toByteSlice(global.json_ext)).copy();
                }
                else
                {
                    if ((global.params.objfiles.length == 0))
                    {
                        error(Loc.initial, new BytePtr("cannot determine JSON filename, use `-Xf=<file>` or provide a source file"));
                        fatal();
                    }
                    ByteSlice n = toDString(global.params.objfiles.get(0)).copy();
                    n = FileName.name(n).copy();
                    jsonfilename = FileName.forceExt(n, toByteSlice(global.json_ext)).copy();
                }
                writeFile(Loc.initial, jsonfilename, toByteSlice(buf.peekSlice()));
            }
        }
        finally {
        }
    }


    public static void getenv_setargv(BytePtr envvalue, DArray<BytePtr> args) {
        if (envvalue == null)
            return ;
        BytePtr env = pcopy(Mem.xstrdup(envvalue));
    L_outer2:
        for (; 1 != 0;){
            {
                int __dispatch0 = 0;
                dispatched_0:
                do {
                    switch (__dispatch0 != 0 ? __dispatch0 : (env.get() & 0xFF))
                    {
                        case 32:
                        case 9:
                            env.postInc();
                            break;
                        case 0:
                            return ;
                        default:
                        __dispatch0 = 0;
                        (args).push(env);
                        BytePtr p = pcopy(env);
                        int slash = 0;
                        boolean instring = false;
                    L_outer3:
                        for (; 1 != 0;){
                            byte c = env.postInc().get();
                            {
                                int __dispatch1 = 0;
                                dispatched_1:
                                do {
                                    switch (__dispatch1 != 0 ? __dispatch1 : (c & 0xFF))
                                    {
                                        case 34:
                                            p.minusAssign((slash >> 1));
                                            if ((slash & 1) != 0)
                                            {
                                                p.postDec();
                                                /*goto default*/ { __dispatch1 = -1; continue dispatched_1; }
                                            }
                                            expr((instring ? 1 : 0) ^= 1);
                                            slash = 0;
                                            continue L_outer3;
                                        case 32:
                                        case 9:
                                            if (instring)
                                                /*goto default*/ { __dispatch1 = -1; continue dispatched_1; }
                                            p.set(0, (byte)0);
                                            break;
                                        case 92:
                                            slash++;
                                            p.postInc().set(0, c);
                                            continue L_outer3;
                                        case 0:
                                            p.set(0, (byte)0);
                                            return ;
                                        default:
                                        __dispatch1 = 0;
                                        slash = 0;
                                        p.postInc().set(0, c);
                                        continue L_outer3;
                                    }
                                } while(__dispatch1 != 0);
                            }
                            break;
                        }
                        break;
                    }
                } while(__dispatch0 != 0);
            }
        }
    }

    public static BytePtr parse_arch_arg(DArray<BytePtr> args, BytePtr arch) {
        {
            Slice<BytePtr> __r1553 = (args).opSlice().copy();
            int __key1554 = 0;
            for (; (__key1554 < __r1553.getLength());__key1554 += 1) {
                BytePtr p = pcopy(__r1553.get(__key1554));
                if (((p.get(0) & 0xFF) == 45))
                {
                    if ((strcmp(p.plus(1), new BytePtr("m32")) == 0) || (strcmp(p.plus(1), new BytePtr("m32mscoff")) == 0) || (strcmp(p.plus(1), new BytePtr("m64")) == 0))
                        arch = pcopy((p.plus(2)));
                    else if ((strcmp(p.plus(1), new BytePtr("run")) == 0))
                        break;
                }
            }
        }
        return arch;
    }

    public static ByteSlice parse_conf_arg(DArray<BytePtr> args) {
        ByteSlice conf = new ByteSlice();
        {
            Slice<BytePtr> __r1555 = (args).opSlice().copy();
            int __key1556 = 0;
            for (; (__key1556 < __r1555.getLength());__key1556 += 1) {
                BytePtr p = pcopy(__r1555.get(__key1556));
                ByteSlice arg = toDString(p).copy();
                if ((arg.getLength() != 0) && ((arg.get(0) & 0xFF) == 45))
                {
                    if ((arg.getLength() >= 6) && __equals(arg.slice(1,6), new ByteSlice("conf=")))
                    {
                        conf = arg.slice(6,arg.getLength()).copy();
                    }
                    else if (__equals(arg.slice(1,arg.getLength()), new ByteSlice("run")))
                        break;
                }
            }
        }
        return conf;
    }

    public static void setDefaultLibrary() {
        if ((global.params.defaultlibname == new ByteSlice()))
        {
            global.params.defaultlibname = new ByteSlice("libphobos2.a").copy();
        }
        else if (global.params.defaultlibname.getLength() == 0)
            global.params.defaultlibname = new ByteSlice().copy();
        if ((global.params.debuglibname == new ByteSlice()))
            global.params.debuglibname = global.params.defaultlibname.copy();
    }

    public static void setTarget(Param params) {
        expr(params.isLinux = true);
    }

    public static void addDefaultVersionIdentifiers(Param params) {
        VersionCondition.addPredefinedGlobalIdent(new ByteSlice("DigitalMars"));
        if (params.isWindows)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Windows"));
            if (global.params.mscoff)
            {
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CRuntime_Microsoft"));
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_Microsoft"));
            }
            else
            {
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CRuntime_DigitalMars"));
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_DigitalMars"));
            }
        }
        else if (params.isLinux)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Posix"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("linux"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("ELFv1"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CRuntime_Glibc"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_Gcc"));
        }
        else if (params.isOSX)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Posix"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("OSX"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_Clang"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("darwin"));
        }
        else if (params.isFreeBSD)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Posix"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("FreeBSD"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("ELFv1"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_Clang"));
        }
        else if (params.isOpenBSD)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Posix"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("OpenBSD"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("ELFv1"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_Gcc"));
        }
        else if (params.isDragonFlyBSD)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Posix"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("DragonFlyBSD"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("ELFv1"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_Gcc"));
        }
        else if (params.isSolaris)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Posix"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Solaris"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("ELFv1"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("CppRuntime_Sun"));
        }
        else
        {
            throw new AssertionError("Unreachable code!");
        }
        VersionCondition.addPredefinedGlobalIdent(new ByteSlice("LittleEndian"));
        VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_Version2"));
        VersionCondition.addPredefinedGlobalIdent(new ByteSlice("all"));
        if ((params.cpu >= CPU.sse2))
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_SIMD"));
            if ((params.cpu >= CPU.avx))
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_AVX"));
            if ((params.cpu >= CPU.avx2))
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_AVX2"));
        }
        if (params.is64bit)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_InlineAsm_X86_64"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("X86_64"));
            if (params.isWindows)
            {
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Win64"));
            }
        }
        else
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_InlineAsm"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_InlineAsm_X86"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("X86"));
            if (params.isWindows)
            {
                VersionCondition.addPredefinedGlobalIdent(new ByteSlice("Win32"));
            }
        }
        if (params.isLP64)
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_LP64"));
        if (params.doDocComments)
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_Ddoc"));
        if (params.cov)
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_Coverage"));
        if (((params.pic & 0xFF) != 0))
            VersionCondition.addPredefinedGlobalIdent(((params.pic & 0xFF) == 1) ? new ByteSlice("D_PIC") : new ByteSlice("D_PIE"));
        if (params.useUnitTests)
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("unittest"));
        if (((params.useAssert & 0xFF) == 2))
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("assert"));
        if (((params.useArrayBounds & 0xFF) == 1))
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_NoBoundsChecks"));
        if (params.betterC)
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_BetterC"));
        }
        else
        {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_ModuleInfo"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_Exceptions"));
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_TypeInfo"));
        }
        VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_HardFloat"));
    }

    public static void printPredefinedVersions(_IO_FILE stream) {
        if (global.versionids != null)
        {
            OutBuffer buf = new OutBuffer();
            {
                Slice<Identifier> __r1557 = (global.versionids).opSlice().copy();
                int __key1558 = 0;
                for (; (__key1558 < __r1557.getLength());__key1558 += 1) {
                    Identifier str = __r1557.get(__key1558);
                    buf.writeByte(32);
                    buf.writestring(str.toChars());
                }
            }
            fprintf(stream, new BytePtr("predefs  %s\n"), buf.peekChars());
        }
    }

    public static void printGlobalConfigs(_IO_FILE stream) {
        fprintf(stream, new BytePtr("binary    %.*s\n"), global.params.argv0.getLength(), toBytePtr(global.params.argv0));
        fprintf(stream, new BytePtr("version   %.*s\n"), global._version.getLength() - 1, toBytePtr(global._version));
        ByteSlice iniOutput = global.inifilename.getLength() != 0 ? global.inifilename : new ByteSlice("(none)").copy();
        fprintf(stream, new BytePtr("config    %.*s\n"), iniOutput.getLength(), toBytePtr(iniOutput));
        {
            StringTable environment = new StringTable();
            try {
                environment._init(0);
                DArray<BytePtr> dflags = new DArray<BytePtr>();
                try {
                    getenv_setargv(readFromEnv(environment, new BytePtr("DFLAGS")), dflags);
                    environment.reset(1);
                    OutBuffer buf = new OutBuffer();
                    {
                        Slice<BytePtr> __r1559 = dflags.opSlice().copy();
                        int __key1560 = 0;
                        for (; (__key1560 < __r1559.getLength());__key1560 += 1) {
                            BytePtr flag = pcopy(__r1559.get(__key1560));
                            boolean needsQuoting = false;
                            {
                                ByteSlice __r1561 = flag.slice(0,strlen(flag)).copy();
                                int __key1562 = 0;
                                for (; (__key1562 < __r1561.getLength());__key1562 += 1) {
                                    byte c = __r1561.get(__key1562);
                                    if (!((isalnum((c & 0xFF)) != 0) || ((c & 0xFF) == 95)))
                                    {
                                        expr(needsQuoting = true);
                                        break;
                                    }
                                }
                            }
                            if (strchr(flag, 32) != null)
                                buf.printf(new BytePtr("'%s' "), flag);
                            else
                                buf.printf(new BytePtr("%s "), flag);
                        }
                    }
                    ByteSlice res = buf.peekSlice().getLength() != 0 ? buf.peekSlice().slice(0,buf.peekSlice().getLength() - 1) : new ByteSlice("(none)").copy();
                    fprintf(stream, new BytePtr("DFLAGS    %.*s\n"), res.getLength(), toBytePtr(res));
                }
                finally {
                }
            }
            finally {
            }
        }
    }

    public static void setTargetCPU(Param params) {
        if (target.isXmmSupported())
        {
            switch (params.cpu)
            {
                case CPU.baseline:
                    params.cpu = CPU.sse2;
                    break;
                case CPU.native_:
                    params.cpu = avx2() ? CPU.avx2 : avx() ? CPU.avx : CPU.sse2;
                    break;
                default:
                break;
            }
        }
        else
            params.cpu = CPU.x87;
    }

    public static void flushMixins() {
        if (global.params.mixinOut == null)
            return ;
        assert(global.params.mixinFile != null);
        expr(File.write(global.params.mixinFile, toByteSlice((global.params.mixinOut).peekSlice())));
        (global.params.mixinOut).destroy();
        global.params.mixinOut = null;
    }

    public static boolean parseCommandLine(DArray<BytePtr> arguments, int argc, Param params, DArray<BytePtr> files) {
        Ref<Boolean> errors = ref(false);
        Function2<BytePtr,BytePtr,Void> error = new Function2<BytePtr,BytePtr,Void>(){
            public Void invoke(BytePtr format, BytePtr arg){
                error(Loc.initial, format, arg);
                expr(errors.value = true);
            }
        };
        Function2<BytePtr,Integer,Integer> parseDigits = new Function2<BytePtr,Integer,Integer>(){
            public Integer invoke(BytePtr p, Integer max){
                int value = 0;
                Ref<Boolean> overflow = ref(false);
                {
                    int d = 0;
                    for (; ((d = (p.get() & 0xFF) - 48) < 10);p.plusAssign(1)){
                        value = mulu(value, 10, overflow);
                        value = addu(value, d, overflow);
                    }
                }
                return overflow.value || (value > max) || (p.get() != 0) ? -1 : value;
            }
        };
        Function2<BytePtr,ByteSlice,Boolean> startsWith = new Function2<BytePtr,ByteSlice,Boolean>(){
            public Boolean invoke(BytePtr p, ByteSlice s){
                {
                    ByteSlice __r1563 = s.copy();
                    int __key1564 = 0;
                    for (; (__key1564 < __r1563.getLength());__key1564 += 1) {
                        byte c = __r1563.get(__key1564);
                        if (((c & 0xFF) != (p.get() & 0xFF)))
                            return false;
                        p.plusAssign(1);
                    }
                }
                return true;
            }
        };
        Function2<BytePtr,ByteSlice,Void> errorInvalidSwitch = new Function2<BytePtr,ByteSlice,Void>(){
            public Void invoke(BytePtr p, ByteSlice availableOptions){
                error.invoke(new BytePtr("Switch `%s` is invalid"), p);
                if ((availableOptions != new ByteSlice()))
                    errorSupplemental(Loc.initial, new BytePtr("%.*s"), availableOptions.getLength(), toBytePtr(availableOptions));
            }
        };
        Function3<BytePtr,Boolean,ByteSlice,Integer> checkOptions = new Function3<BytePtr,Boolean,ByteSlice,Integer>(){
            public Integer invoke(BytePtr p, Ref<Boolean> usageFlag, ByteSlice missingMsg){
                if (((p.get() & 0xFF) == 0) || ((p.get() & 0xFF) == 61) && (p.get(1) == 0))
                {
                    error(Loc.initial, new BytePtr("%.*s"), missingMsg.getLength(), toBytePtr(missingMsg));
                    expr(errors.value = true);
                    expr(usageFlag.value = true);
                    return CheckOptions.help;
                }
                if (((p.get() & 0xFF) != 61))
                    return CheckOptions.error;
                p.postInc();
                if (((p.get() & 0xFF) == 104) || ((p.get() & 0xFF) == 63) && (p.get(1) == 0) || (strcmp(p, new BytePtr("help")) == 0))
                {
                    expr(usageFlag.value = true);
                    return CheckOptions.help;
                }
                return CheckOptions.success;
            }
        };
        Function2<ByteSlice,ByteSlice,ByteSlice> checkOptionsMixin = new Function2<ByteSlice,ByteSlice,ByteSlice>(){
            public ByteSlice invoke(ByteSlice usageFlag, ByteSlice missingMsg){
                return new ByteSlice("\n            final switch (checkOptions(p + len - 1, params.").concat(usageFlag).concat(new ByteSlice(",\"")).concat(missingMsg).concat(new ByteSlice("\"))\n            {\n                case CheckOptions.error:\n                    goto Lerror;\n                case CheckOptions.help:\n                    return false;\n                case CheckOptions.success:\n                    break;\n            }\n        "));
            }
        };
        // from template parseCLIOption!(_preview[Feature("dip25", "useDIP25", "implement https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md (Sealed references)", true, false), Feature("dip1000", "vsafe", "implement https://github.com/dlang/DIPs/blob/master/DIPs/other/DIP1000.md (Scoped Pointers)", true, false), Feature("dip1008", "ehnogc", "implement https://github.com/dlang/DIPs/blob/master/DIPs/DIP1008.md (@nogc Throwable)", true, false), Feature("fieldwise", "fieldwise", "use fieldwise comparisons for struct equality", true, false), Feature("markdown", "markdown", "enable Markdown replacements in Ddoc", true, false), Feature("fixAliasThis", "fixAliasThis", "when a symbol is resolved, check alias this scope before going to upper scopes", true, false), Feature("intpromote", "fix16997", "fix integral promotions for unary + - ~ operators", true, false), Feature("dtorfields", "dtorFields", "destruct fields of partially constructed objects", true, false), Feature("rvaluerefparam", "rvalueRefParam", "enable rvalue arguments to ref parameters", true, false)])
        Function2<Param,BytePtr,Boolean> parseCLIOption_preview[Feature("dip25", "useDIP25", "implement https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md (Sealed references)", true, false), Feature("dip1000", "vsafe", "implement https://github.com/dlang/DIPs/blob/master/DIPs/other/DIP1000.md (Scoped Pointers)", true, false), Feature("dip1008", "ehnogc", "implement https://github.com/dlang/DIPs/blob/master/DIPs/DIP1008.md (@nogc Throwable)", true, false), Feature("fieldwise", "fieldwise", "use fieldwise comparisons for struct equality", true, false), Feature("markdown", "markdown", "enable Markdown replacements in Ddoc", true, false), Feature("fixAliasThis", "fixAliasThis", "when a symbol is resolved, check alias this scope before going to upper scopes", true, false), Feature("intpromote", "fix16997", "fix integral promotions for unary + - ~ operators", true, false), Feature("dtorfields", "dtorFields", "destruct fields of partially constructed objects", true, false), Feature("rvaluerefparam", "rvalueRefParam", "enable rvalue arguments to ref parameters", true, false)] = new Function2<Param,BytePtr,Boolean>(){
            public Boolean invoke(Param params, BytePtr p){
                BytePtr ps = pcopy(p.plus(7).plus(1));
                if (Identifier.isValidIdentifier(ps.plus(1)))
                {
                    Function0<ByteSlice> generateTransitionsText_preview[Feature("dip25", "useDIP25", "implement https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md (Sealed references)", true, false), Feature("dip1000", "vsafe", "implement https://github.com/dlang/DIPs/blob/master/DIPs/other/DIP1000.md (Scoped Pointers)", true, false), Feature("dip1008", "ehnogc", "implement https://github.com/dlang/DIPs/blob/master/DIPs/DIP1008.md (@nogc Throwable)", true, false), Feature("fieldwise", "fieldwise", "use fieldwise comparisons for struct equality", true, false), Feature("markdown", "markdown", "enable Markdown replacements in Ddoc", true, false), Feature("fixAliasThis", "fixAliasThis", "when a symbol is resolved, check alias this scope before going to upper scopes", true, false), Feature("intpromote", "fix16997", "fix integral promotions for unary + - ~ operators", true, false), Feature("dtorfields", "dtorFields", "destruct fields of partially constructed objects", true, false), Feature("rvaluerefparam", "rvalueRefParam", "enable rvalue arguments to ref parameters", true, false)] = new Function0<ByteSlice>(){
                        public ByteSlice invoke(){
                            ByteSlice buf = new ByteSlice("case \"all\":").copy();
                            {
                                Slice<Usage.Feature> __r1569 = slice(new Usage.Feature[]{new Usage.Feature(new ByteSlice("dip25"), new ByteSlice("useDIP25"), new ByteSlice("implement https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md (Sealed references)"), true, false), new Usage.Feature(new ByteSlice("dip1000"), new ByteSlice("vsafe"), new ByteSlice("implement https://github.com/dlang/DIPs/blob/master/DIPs/other/DIP1000.md (Scoped Pointers)"), true, false), new Usage.Feature(new ByteSlice("dip1008"), new ByteSlice("ehnogc"), new ByteSlice("implement https://github.com/dlang/DIPs/blob/master/DIPs/DIP1008.md (@nogc Throwable)"), true, false), new Usage.Feature(new ByteSlice("fieldwise"), new ByteSlice("fieldwise"), new ByteSlice("use fieldwise comparisons for struct equality"), true, false), new Usage.Feature(new ByteSlice("markdown"), new ByteSlice("markdown"), new ByteSlice("enable Markdown replacements in Ddoc"), true, false), new Usage.Feature(new ByteSlice("fixAliasThis"), new ByteSlice("fixAliasThis"), new ByteSlice("when a symbol is resolved, check alias this scope before going to upper scopes"), true, false), new Usage.Feature(new ByteSlice("intpromote"), new ByteSlice("fix16997"), new ByteSlice("fix integral promotions for unary + - ~ operators"), true, false), new Usage.Feature(new ByteSlice("dtorfields"), new ByteSlice("dtorFields"), new ByteSlice("destruct fields of partially constructed objects"), true, false), new Usage.Feature(new ByteSlice("rvaluerefparam"), new ByteSlice("rvalueRefParam"), new ByteSlice("enable rvalue arguments to ref parameters"), true, false)});
                                int __key1570 = 0;
                                for (; (__key1570 < 9);__key1570 += 1) {
                                    Usage.Feature t = __r1569.get(__key1570).copy();
                                    if (t.deprecated_)
                                        continue;
                                    buf.append(new ByteSlice("params.").concat(t.paramName).concat(new ByteSlice(" = true;")));
                                }
                            }
                            buf.append(new ByteSlice("break;\n"));
                            {
                                Slice<Usage.Feature> __r1571 = slice(new Usage.Feature[]{new Usage.Feature(new ByteSlice("dip25"), new ByteSlice("useDIP25"), new ByteSlice("implement https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md (Sealed references)"), true, false), new Usage.Feature(new ByteSlice("dip1000"), new ByteSlice("vsafe"), new ByteSlice("implement https://github.com/dlang/DIPs/blob/master/DIPs/other/DIP1000.md (Scoped Pointers)"), true, false), new Usage.Feature(new ByteSlice("dip1008"), new ByteSlice("ehnogc"), new ByteSlice("implement https://github.com/dlang/DIPs/blob/master/DIPs/DIP1008.md (@nogc Throwable)"), true, false), new Usage.Feature(new ByteSlice("fieldwise"), new ByteSlice("fieldwise"), new ByteSlice("use fieldwise comparisons for struct equality"), true, false), new Usage.Feature(new ByteSlice("markdown"), new ByteSlice("markdown"), new ByteSlice("enable Markdown replacements in Ddoc"), true, false), new Usage.Feature(new ByteSlice("fixAliasThis"), new ByteSlice("fixAliasThis"), new ByteSlice("when a symbol is resolved, check alias this scope before going to upper scopes"), true, false), new Usage.Feature(new ByteSlice("intpromote"), new ByteSlice("fix16997"), new ByteSlice("fix integral promotions for unary + - ~ operators"), true, false), new Usage.Feature(new ByteSlice("dtorfields"), new ByteSlice("dtorFields"), new ByteSlice("destruct fields of partially constructed objects"), true, false), new Usage.Feature(new ByteSlice("rvaluerefparam"), new ByteSlice("rvalueRefParam"), new ByteSlice("enable rvalue arguments to ref parameters"), true, false)});
                                int __key1572 = 0;
                                for (; (__key1572 < 9);__key1572 += 1) {
                                    Usage.Feature t = __r1571.get(__key1572).copy();
                                    buf.append(new ByteSlice("case \"").concat(t.name).concat(new ByteSlice("\":")));
                                    if (t.deprecated_)
                                        buf.append(new ByteSlice("deprecation(Loc.initial, \"`-preview=").concat(t.name).concat(new ByteSlice("` no longer has any effect.\"); ")));
                                    buf.append(new ByteSlice("params.").concat(t.paramName).concat(new ByteSlice(" = true; return true;")));
                                }
                            }
                            return buf;
                        }
                    };
                    BytePtr ident = pcopy(ps.plus(1));
                    switch (__switch(ident.slice(0,strlen(ident))))
                    {
                        case 0:
                            expr(params.useDIP25 = true);
                            expr(params.vsafe = true);
                            expr(params.ehnogc = true);
                            expr(params.fieldwise = true);
                            expr(params.markdown = true);
                            expr(params.fixAliasThis = true);
                            expr(params.fix16997 = true);
                            expr(params.dtorFields = true);
                            expr(params.rvalueRefParam = true);
                            break;
                        case 1:
                            expr(params.useDIP25 = true);
                            return true;
                        case 2:
                            expr(params.vsafe = true);
                            return true;
                        case 3:
                            expr(params.ehnogc = true);
                            return true;
                        case 5:
                            expr(params.fieldwise = true);
                            return true;
                        case 4:
                            expr(params.markdown = true);
                            return true;
                        case 8:
                            expr(params.fixAliasThis = true);
                            return true;
                        case 7:
                            expr(params.fix16997 = true);
                            return true;
                        case 6:
                            expr(params.dtorFields = true);
                            return true;
                        case 9:
                            expr(params.rvalueRefParam = true);
                            return true;
                        default:
                        return false;
                    }
                }
                return false;
            }
        };

        // from template parseCLIOption!(_revert[Feature("dip25", "noDIP25", "revert DIP25 changes https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md", true, false), Feature("import", "bug10378", "revert to single phase name lookup", true, true)])
        Function2<Param,BytePtr,Boolean> parseCLIOption_revert[Feature("dip25", "noDIP25", "revert DIP25 changes https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md", true, false), Feature("import", "bug10378", "revert to single phase name lookup", true, true)] = new Function2<Param,BytePtr,Boolean>(){
            public Boolean invoke(Param params, BytePtr p){
                BytePtr ps = pcopy(p.plus(6).plus(1));
                if (Identifier.isValidIdentifier(ps.plus(1)))
                {
                    Function0<ByteSlice> generateTransitionsText_revert[Feature("dip25", "noDIP25", "revert DIP25 changes https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md", true, false), Feature("import", "bug10378", "revert to single phase name lookup", true, true)] = new Function0<ByteSlice>(){
                        public ByteSlice invoke(){
                            ByteSlice buf = new ByteSlice("case \"all\":").copy();
                            {
                                Slice<Usage.Feature> __r1573 = slice(new Usage.Feature[]{new Usage.Feature(new ByteSlice("dip25"), new ByteSlice("noDIP25"), new ByteSlice("revert DIP25 changes https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md"), true, false), new Usage.Feature(new ByteSlice("import"), new ByteSlice("bug10378"), new ByteSlice("revert to single phase name lookup"), true, true)});
                                int __key1574 = 0;
                                for (; (__key1574 < 2);__key1574 += 1) {
                                    Usage.Feature t = __r1573.get(__key1574).copy();
                                    if (t.deprecated_)
                                        continue;
                                    buf.append(new ByteSlice("params.").concat(t.paramName).concat(new ByteSlice(" = true;")));
                                }
                            }
                            buf.append(new ByteSlice("break;\n"));
                            {
                                Slice<Usage.Feature> __r1575 = slice(new Usage.Feature[]{new Usage.Feature(new ByteSlice("dip25"), new ByteSlice("noDIP25"), new ByteSlice("revert DIP25 changes https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md"), true, false), new Usage.Feature(new ByteSlice("import"), new ByteSlice("bug10378"), new ByteSlice("revert to single phase name lookup"), true, true)});
                                int __key1576 = 0;
                                for (; (__key1576 < 2);__key1576 += 1) {
                                    Usage.Feature t = __r1575.get(__key1576).copy();
                                    buf.append(new ByteSlice("case \"").concat(t.name).concat(new ByteSlice("\":")));
                                    if (t.deprecated_)
                                        buf.append(new ByteSlice("deprecation(Loc.initial, \"`-revert=").concat(t.name).concat(new ByteSlice("` no longer has any effect.\"); ")));
                                    buf.append(new ByteSlice("params.").concat(t.paramName).concat(new ByteSlice(" = true; return true;")));
                                }
                            }
                            return buf;
                        }
                    };
                    BytePtr ident = pcopy(ps.plus(1));
                    switch (__switch(ident.slice(0,strlen(ident))))
                    {
                        case 0:
                            expr(params.noDIP25 = true);
                            break;
                        case 1:
                            expr(params.noDIP25 = true);
                            return true;
                        case 2:
                            deprecation(Loc.initial, new BytePtr("`-revert=import` no longer has any effect."));
                            expr(params.bug10378 = true);
                            return true;
                        default:
                        return false;
                    }
                }
                return false;
            }
        };

        // from template parseCLIOption!(_transition[Feature("field", "vfield", "list all non-mutable fields which occupy an object instance", true, false), Feature("checkimports", "check10378", "give deprecation messages about 10378 anomalies", true, true), Feature("complex", "vcomplex", "give deprecation messages about all usages of complex or imaginary types", true, false), Feature("tls", "vtls", "list all variables going into thread local storage", true, false), Feature("vmarkdown", "vmarkdown", "list instances of Markdown replacements in Ddoc", true, false)])
        Function2<Param,BytePtr,Boolean> parseCLIOption_transition[Feature("field", "vfield", "list all non-mutable fields which occupy an object instance", true, false), Feature("checkimports", "check10378", "give deprecation messages about 10378 anomalies", true, true), Feature("complex", "vcomplex", "give deprecation messages about all usages of complex or imaginary types", true, false), Feature("tls", "vtls", "list all variables going into thread local storage", true, false), Feature("vmarkdown", "vmarkdown", "list instances of Markdown replacements in Ddoc", true, false)] = new Function2<Param,BytePtr,Boolean>(){
            public Boolean invoke(Param params, BytePtr p){
                BytePtr ps = pcopy(p.plus(10).plus(1));
                if (Identifier.isValidIdentifier(ps.plus(1)))
                {
                    Function0<ByteSlice> generateTransitionsText_transition[Feature("field", "vfield", "list all non-mutable fields which occupy an object instance", true, false), Feature("checkimports", "check10378", "give deprecation messages about 10378 anomalies", true, true), Feature("complex", "vcomplex", "give deprecation messages about all usages of complex or imaginary types", true, false), Feature("tls", "vtls", "list all variables going into thread local storage", true, false), Feature("vmarkdown", "vmarkdown", "list instances of Markdown replacements in Ddoc", true, false)] = new Function0<ByteSlice>(){
                        public ByteSlice invoke(){
                            ByteSlice buf = new ByteSlice("case \"all\":").copy();
                            {
                                Slice<Usage.Feature> __r1565 = slice(new Usage.Feature[]{new Usage.Feature(new ByteSlice("field"), new ByteSlice("vfield"), new ByteSlice("list all non-mutable fields which occupy an object instance"), true, false), new Usage.Feature(new ByteSlice("checkimports"), new ByteSlice("check10378"), new ByteSlice("give deprecation messages about 10378 anomalies"), true, true), new Usage.Feature(new ByteSlice("complex"), new ByteSlice("vcomplex"), new ByteSlice("give deprecation messages about all usages of complex or imaginary types"), true, false), new Usage.Feature(new ByteSlice("tls"), new ByteSlice("vtls"), new ByteSlice("list all variables going into thread local storage"), true, false), new Usage.Feature(new ByteSlice("vmarkdown"), new ByteSlice("vmarkdown"), new ByteSlice("list instances of Markdown replacements in Ddoc"), true, false)});
                                int __key1566 = 0;
                                for (; (__key1566 < 5);__key1566 += 1) {
                                    Usage.Feature t = __r1565.get(__key1566).copy();
                                    if (t.deprecated_)
                                        continue;
                                    buf.append(new ByteSlice("params.").concat(t.paramName).concat(new ByteSlice(" = true;")));
                                }
                            }
                            buf.append(new ByteSlice("break;\n"));
                            {
                                Slice<Usage.Feature> __r1567 = slice(new Usage.Feature[]{new Usage.Feature(new ByteSlice("field"), new ByteSlice("vfield"), new ByteSlice("list all non-mutable fields which occupy an object instance"), true, false), new Usage.Feature(new ByteSlice("checkimports"), new ByteSlice("check10378"), new ByteSlice("give deprecation messages about 10378 anomalies"), true, true), new Usage.Feature(new ByteSlice("complex"), new ByteSlice("vcomplex"), new ByteSlice("give deprecation messages about all usages of complex or imaginary types"), true, false), new Usage.Feature(new ByteSlice("tls"), new ByteSlice("vtls"), new ByteSlice("list all variables going into thread local storage"), true, false), new Usage.Feature(new ByteSlice("vmarkdown"), new ByteSlice("vmarkdown"), new ByteSlice("list instances of Markdown replacements in Ddoc"), true, false)});
                                int __key1568 = 0;
                                for (; (__key1568 < 5);__key1568 += 1) {
                                    Usage.Feature t = __r1567.get(__key1568).copy();
                                    buf.append(new ByteSlice("case \"").concat(t.name).concat(new ByteSlice("\":")));
                                    if (t.deprecated_)
                                        buf.append(new ByteSlice("deprecation(Loc.initial, \"`-transition=").concat(t.name).concat(new ByteSlice("` no longer has any effect.\"); ")));
                                    buf.append(new ByteSlice("params.").concat(t.paramName).concat(new ByteSlice(" = true; return true;")));
                                }
                            }
                            return buf;
                        }
                    };
                    BytePtr ident = pcopy(ps.plus(1));
                    switch (__switch(ident.slice(0,strlen(ident))))
                    {
                        case 0:
                            expr(params.vfield = true);
                            expr(params.vcomplex = true);
                            expr(params.vtls = true);
                            expr(params.vmarkdown = true);
                            break;
                        case 2:
                            expr(params.vfield = true);
                            return true;
                        case 5:
                            deprecation(Loc.initial, new BytePtr("`-transition=checkimports` no longer has any effect."));
                            expr(params.check10378 = true);
                            return true;
                        case 3:
                            expr(params.vcomplex = true);
                            return true;
                        case 1:
                            expr(params.vtls = true);
                            return true;
                        case 4:
                            expr(params.vmarkdown = true);
                            return true;
                        default:
                        return false;
                    }
                }
                return false;
            }
        };

        {
            int i = 1;
        L_outer4:
            for (; (i < arguments.length);i++){
                BytePtr p = pcopy(arguments.get(i));
                ByteSlice arg = p.slice(0,strlen(p)).copy();
                if (((p.get() & 0xFF) != 45))
                {
                    files.push(p);
                    continue L_outer4;
                }
                if (__equals(arg, new ByteSlice("-allinst")))
                    expr(params.allInst = true);
                else if (__equals(arg, new ByteSlice("-de")))
                    params.useDeprecated = DiagnosticReporting.error;
                else if (__equals(arg, new ByteSlice("-d")))
                    params.useDeprecated = DiagnosticReporting.off;
                else if (__equals(arg, new ByteSlice("-dw")))
                    params.useDeprecated = DiagnosticReporting.inform;
                else if (__equals(arg, new ByteSlice("-c")))
                    expr(params.link = false);
                else if (startsWith.invoke(p.plus(1), new ByteSlice("checkaction")))
                {
                    int len = 13;
                    {
                        int __dispatch6 = 0;
                        dispatched_6:
                        do {
                            switch (__dispatch6 != 0 ? __dispatch6 : checkOptions.invoke(p.plus(13).minus(1), params.checkActionUsage, new ByteSlice("`-check=<behavior>` requires a behavior")))
                            {
                                case CheckOptions.error:
                                    /*goto Lerror*//*unrolled goto*/
                                    files.push(new BytePtr("__stdin.d"));
                                case CheckOptions.help:
                                    return false;
                                case CheckOptions.success:
                                    break;
                                default:
                                throw SwitchError.INSTANCE;
                            }
                        } while(__dispatch6 != 0);
                    }
                    if ((strcmp(p.plus(13), new BytePtr("D")) == 0))
                        params.checkAction = CHECKACTION.D;
                    else if ((strcmp(p.plus(13), new BytePtr("C")) == 0))
                        params.checkAction = CHECKACTION.C;
                    else if ((strcmp(p.plus(13), new BytePtr("halt")) == 0))
                        params.checkAction = CHECKACTION.halt;
                    else if ((strcmp(p.plus(13), new BytePtr("context")) == 0))
                        params.checkAction = CHECKACTION.context;
                    else
                    {
                        errorInvalidSwitch.invoke(p, new ByteSlice());
                        expr(params.checkActionUsage = true);
                        return false;
                    }
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("check")))
                {
                    int len = 7;
                    {
                        int __dispatch7 = 0;
                        dispatched_7:
                        do {
                            switch (__dispatch7 != 0 ? __dispatch7 : checkOptions.invoke(p.plus(7).minus(1), params.checkUsage, new ByteSlice("`-check=<action>` requires an action")))
                            {
                                case CheckOptions.error:
                                    /*goto Lerror*//*unrolled goto*/
                                    files.push(new BytePtr("__stdin.d"));
                                case CheckOptions.help:
                                    return false;
                                case CheckOptions.success:
                                    break;
                                default:
                                throw SwitchError.INSTANCE;
                            }
                        } while(__dispatch7 != 0);
                    }
                    Function3<BytePtr,ByteSlice,Byte,Boolean> check = new Function3<BytePtr,ByteSlice,Byte,Boolean>(){
                        public Boolean invoke(BytePtr p, ByteSlice name, Ref<Byte> ce){
                            p.plusAssign(7);
                            if (startsWith.invoke(p, name))
                            {
                                p.plusAssign(name.getLength());
                                if (((p.get() & 0xFF) == 0) || (strcmp(p, new BytePtr("=on")) == 0))
                                {
                                    ce.value = CHECKENABLE.on;
                                    return true;
                                }
                                else if ((strcmp(p, new BytePtr("=off")) == 0))
                                {
                                    ce.value = CHECKENABLE.off;
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    if (!(check.invoke(p, new ByteSlice("assert"), params.useAssert) || check.invoke(p, new ByteSlice("bounds"), params.useArrayBounds) || check.invoke(p, new ByteSlice("in"), params.useIn) || check.invoke(p, new ByteSlice("invariant"), params.useInvariants) || check.invoke(p, new ByteSlice("out"), params.useOut) || check.invoke(p, new ByteSlice("switch"), params.useSwitchError)))
                    {
                        errorInvalidSwitch.invoke(p, new ByteSlice());
                        expr(params.checkUsage = true);
                        return false;
                    }
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("color")))
                {
                    if (((p.get(6) & 0xFF) == 61))
                    {
                        if ((strcmp(p.plus(7), new BytePtr("on")) == 0))
                            expr(params.color = true);
                        else if ((strcmp(p.plus(7), new BytePtr("off")) == 0))
                            expr(params.color = false);
                        else if ((strcmp(p.plus(7), new BytePtr("auto")) != 0))
                        {
                            errorInvalidSwitch.invoke(p, new ByteSlice("Available options for `-color` are `on`, `off` and `auto`"));
                            return true;
                        }
                    }
                    else if (p.get(6) != 0)
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                    else
                        expr(params.color = true);
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("conf=")))
                {
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("cov")))
                {
                    expr(params.cov = true);
                    if (((p.get(4) & 0xFF) == 61))
                    {
                        if (isdigit((p.get(5) & 0xFF)) != 0)
                        {
                            int percent = parseDigits.invoke(p.plus(5), 100);
                            if ((percent == -1))
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            params.covPercent = (byte)percent;
                        }
                        else
                        {
                            errorInvalidSwitch.invoke(p, new ByteSlice("Only a number can be passed to `-cov=<num>`"));
                            return true;
                        }
                    }
                    else if (p.get(4) != 0)
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                }
                else if (__equals(arg, new ByteSlice("-shared")))
                    expr(params.dll = true);
                else if (__equals(arg, new ByteSlice("-fPIC")))
                {
                    params.pic = PIC.pic;
                }
                else if (__equals(arg, new ByteSlice("-fPIE")))
                {
                    params.pic = PIC.pie;
                }
                else if (__equals(arg, new ByteSlice("-map")))
                    expr(params.map = true);
                else if (__equals(arg, new ByteSlice("-multiobj")))
                    expr(params.multiobj = true);
                else if (startsWith.invoke(p.plus(1), new ByteSlice("mixin=")))
                {
                    BytePtr tmp = pcopy(p.plus(6).plus(1));
                    if (tmp.get(0) == 0)
                        /*goto Lnoarg*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                    params.mixinFile = pcopy(Mem.xstrdup(tmp));
                }
                else if (__equals(arg, new ByteSlice("-g")))
                    params.symdebug = (byte)1;
                else if (__equals(arg, new ByteSlice("-gf")))
                {
                    if (params.symdebug == 0)
                        params.symdebug = (byte)1;
                    expr(params.symdebugref = true);
                }
                else if (__equals(arg, new ByteSlice("-gs")))
                    expr(params.alwaysframe = true);
                else if (__equals(arg, new ByteSlice("-gx")))
                    expr(params.stackstomp = true);
                else if (__equals(arg, new ByteSlice("-lowmem")))
                {
                }
                else if ((arg.getLength() > 6) && __equals(arg.slice(0,6), new ByteSlice("--DRT-")))
                {
                    continue L_outer4;
                }
                else if (__equals(arg, new ByteSlice("-m32")))
                {
                    expr(params.is64bit = false);
                    expr(params.mscoff = false);
                }
                else if (__equals(arg, new ByteSlice("-m64")))
                {
                    expr(params.is64bit = true);
                }
                else if (__equals(arg, new ByteSlice("-m32mscoff")))
                {
                    error.invoke(new BytePtr("-m32mscoff can only be used on windows"), null);
                }
                else if ((strncmp(p.plus(1), new BytePtr("mscrtlib="), 9) == 0))
                {
                    error.invoke(new BytePtr("-mscrtlib"), null);
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("profile")))
                {
                    if (((p.get(8) & 0xFF) == 61))
                    {
                        if ((strcmp(p.plus(9), new BytePtr("gc")) == 0))
                            expr(params.tracegc = true);
                        else
                        {
                            errorInvalidSwitch.invoke(p, new ByteSlice("Only `gc` is allowed for `-profile`"));
                            return true;
                        }
                    }
                    else if (p.get(8) != 0)
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                    else
                        expr(params.trace = true);
                }
                else if (__equals(arg, new ByteSlice("-v")))
                    expr(params.verbose = true);
                else if (__equals(arg, new ByteSlice("-vcg-ast")))
                    expr(params.vcg_ast = true);
                else if (__equals(arg, new ByteSlice("-vtls")))
                    expr(params.vtls = true);
                else if (__equals(arg, new ByteSlice("-vcolumns")))
                    expr(params.showColumns = true);
                else if (__equals(arg, new ByteSlice("-vgc")))
                    expr(params.vgc = true);
                else if (startsWith.invoke(p.plus(1), new ByteSlice("verrors")))
                {
                    if (((p.get(8) & 0xFF) == 61) && (isdigit((p.get(9) & 0xFF)) != 0))
                    {
                        int num = parseDigits.invoke(p.plus(9), 2147483647);
                        if ((num == -1))
                            /*goto Lerror*//*unrolled goto*/
                            files.push(new BytePtr("__stdin.d"));
                        params.errorLimit = num;
                    }
                    else if (startsWith.invoke(p.plus(9), new ByteSlice("spec")))
                    {
                        expr(params.showGaggedErrors = true);
                    }
                    else if (startsWith.invoke(p.plus(9), new ByteSlice("context")))
                    {
                        expr(params.printErrorContext = true);
                    }
                    else
                    {
                        errorInvalidSwitch.invoke(p, new ByteSlice("Only number, `spec`, or `context` are allowed for `-verrors`"));
                        return true;
                    }
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("mcpu")))
                {
                    int len = 6;
                    {
                        int __dispatch8 = 0;
                        dispatched_8:
                        do {
                            switch (__dispatch8 != 0 ? __dispatch8 : checkOptions.invoke(p.plus(6).minus(1), params.mcpuUsage, new ByteSlice("`-mcpu=<architecture>` requires an architecture")))
                            {
                                case CheckOptions.error:
                                    /*goto Lerror*//*unrolled goto*/
                                    files.push(new BytePtr("__stdin.d"));
                                case CheckOptions.help:
                                    return false;
                                case CheckOptions.success:
                                    break;
                                default:
                                throw SwitchError.INSTANCE;
                            }
                        } while(__dispatch8 != 0);
                    }
                    if (Identifier.isValidIdentifier(p.plus(6)))
                    {
                        BytePtr ident = pcopy(p.plus(6));
                        switch (__switch(ident.slice(0,strlen(ident))))
                        {
                            case 3:
                                params.cpu = CPU.baseline;
                                break;
                            case 0:
                                params.cpu = CPU.avx;
                                break;
                            case 1:
                                params.cpu = CPU.avx2;
                                break;
                            case 2:
                                params.cpu = CPU.native_;
                                break;
                            default:
                            errorInvalidSwitch.invoke(p, new ByteSlice("Only `baseline`, `avx`, `avx2` or `native` are allowed for `-mcpu`"));
                            expr(params.mcpuUsage = true);
                            return false;
                        }
                    }
                    else
                    {
                        errorInvalidSwitch.invoke(p, new ByteSlice("Only `baseline`, `avx`, `avx2` or `native` are allowed for `-mcpu`"));
                        expr(params.mcpuUsage = true);
                        return false;
                    }
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("extern-std")))
                {
                    int len = 12;
                    {
                        int __dispatch10 = 0;
                        dispatched_10:
                        do {
                            switch (__dispatch10 != 0 ? __dispatch10 : checkOptions.invoke(p.plus(12).minus(1), params.externStdUsage, new ByteSlice("`-extern-std=<standard>` requires a standard")))
                            {
                                case CheckOptions.error:
                                    /*goto Lerror*//*unrolled goto*/
                                    files.push(new BytePtr("__stdin.d"));
                                case CheckOptions.help:
                                    return false;
                                case CheckOptions.success:
                                    break;
                                default:
                                throw SwitchError.INSTANCE;
                            }
                        } while(__dispatch10 != 0);
                    }
                    if ((strcmp(p.plus(12), new BytePtr("c++98")) == 0))
                        params.cplusplus = CppStdRevision.cpp98;
                    else if ((strcmp(p.plus(12), new BytePtr("c++11")) == 0))
                        params.cplusplus = CppStdRevision.cpp11;
                    else if ((strcmp(p.plus(12), new BytePtr("c++14")) == 0))
                        params.cplusplus = CppStdRevision.cpp14;
                    else if ((strcmp(p.plus(12), new BytePtr("c++17")) == 0))
                        params.cplusplus = CppStdRevision.cpp17;
                    else
                    {
                        error.invoke(new BytePtr("Switch `%s` is invalid"), p);
                        expr(params.externStdUsage = true);
                        return false;
                    }
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("transition")))
                {
                    int len = 12;
                    {
                        int __dispatch11 = 0;
                        dispatched_11:
                        do {
                            switch (__dispatch11 != 0 ? __dispatch11 : checkOptions.invoke(p.plus(12).minus(1), params.transitionUsage, new ByteSlice("`-transition=<name>` requires a name")))
                            {
                                case CheckOptions.error:
                                    /*goto Lerror*//*unrolled goto*/
                                    files.push(new BytePtr("__stdin.d"));
                                case CheckOptions.help:
                                    return false;
                                case CheckOptions.success:
                                    break;
                                default:
                                throw SwitchError.INSTANCE;
                            }
                        } while(__dispatch11 != 0);
                    }
                    if (!parseCLIOption_transition[Feature("field", "vfield", "list all non-mutable fields which occupy an object instance", true, false), Feature("checkimports", "check10378", "give deprecation messages about 10378 anomalies", true, true), Feature("complex", "vcomplex", "give deprecation messages about all usages of complex or imaginary types", true, false), Feature("tls", "vtls", "list all variables going into thread local storage", true, false), Feature("vmarkdown", "vmarkdown", "list instances of Markdown replacements in Ddoc", true, false)].invoke(params, p))
                    {
                        if (isdigit((p.get(12) & 0xFF)) != 0)
                        {
                            int num = parseDigits.invoke(p.plus(12), 2147483647);
                            if ((num == -1))
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            switch (num)
                            {
                                case 3449:
                                    expr(params.vfield = true);
                                    break;
                                case 10378:
                                    expr(params.bug10378 = true);
                                    break;
                                case 14246:
                                    expr(params.dtorFields = true);
                                    break;
                                case 14488:
                                    expr(params.vcomplex = true);
                                    break;
                                case 16997:
                                    expr(params.fix16997 = true);
                                    break;
                                default:
                                error.invoke(new BytePtr("Transition `%s` is invalid"), p);
                                expr(params.transitionUsage = true);
                                return false;
                            }
                        }
                        else if (Identifier.isValidIdentifier(p.plus(12)))
                        {
                            BytePtr ident = pcopy(p.plus(12));
                            switch (__switch(ident.slice(0,strlen(ident))))
                            {
                                case 0:
                                    expr(params.bug10378 = true);
                                    break;
                                case 2:
                                    expr(params.dtorFields = true);
                                    break;
                                case 3:
                                    expr(params.fix16997 = true);
                                    break;
                                case 1:
                                    expr(params.markdown = true);
                                    break;
                                default:
                                error.invoke(new BytePtr("Transition `%s` is invalid"), p);
                                expr(params.transitionUsage = true);
                                return false;
                            }
                        }
                        errorInvalidSwitch.invoke(p, new ByteSlice());
                        expr(params.transitionUsage = true);
                        return false;
                    }
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("preview")))
                {
                    int len = 9;
                    {
                        int __dispatch14 = 0;
                        dispatched_14:
                        do {
                            switch (__dispatch14 != 0 ? __dispatch14 : checkOptions.invoke(p.plus(9).minus(1), params.previewUsage, new ByteSlice("`-preview=<name>` requires a name")))
                            {
                                case CheckOptions.error:
                                    /*goto Lerror*//*unrolled goto*/
                                    files.push(new BytePtr("__stdin.d"));
                                case CheckOptions.help:
                                    return false;
                                case CheckOptions.success:
                                    break;
                                default:
                                throw SwitchError.INSTANCE;
                            }
                        } while(__dispatch14 != 0);
                    }
                    if (!parseCLIOption_preview[Feature("dip25", "useDIP25", "implement https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md (Sealed references)", true, false), Feature("dip1000", "vsafe", "implement https://github.com/dlang/DIPs/blob/master/DIPs/other/DIP1000.md (Scoped Pointers)", true, false), Feature("dip1008", "ehnogc", "implement https://github.com/dlang/DIPs/blob/master/DIPs/DIP1008.md (@nogc Throwable)", true, false), Feature("fieldwise", "fieldwise", "use fieldwise comparisons for struct equality", true, false), Feature("markdown", "markdown", "enable Markdown replacements in Ddoc", true, false), Feature("fixAliasThis", "fixAliasThis", "when a symbol is resolved, check alias this scope before going to upper scopes", true, false), Feature("intpromote", "fix16997", "fix integral promotions for unary + - ~ operators", true, false), Feature("dtorfields", "dtorFields", "destruct fields of partially constructed objects", true, false), Feature("rvaluerefparam", "rvalueRefParam", "enable rvalue arguments to ref parameters", true, false)].invoke(params, p))
                    {
                        error.invoke(new BytePtr("Preview `%s` is invalid"), p);
                        expr(params.previewUsage = true);
                        return false;
                    }
                    if (params.vsafe)
                        expr(params.useDIP25 = true);
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("revert")))
                {
                    int len = 8;
                    {
                        int __dispatch15 = 0;
                        dispatched_15:
                        do {
                            switch (__dispatch15 != 0 ? __dispatch15 : checkOptions.invoke(p.plus(8).minus(1), params.revertUsage, new ByteSlice("`-revert=<name>` requires a name")))
                            {
                                case CheckOptions.error:
                                    /*goto Lerror*//*unrolled goto*/
                                    files.push(new BytePtr("__stdin.d"));
                                case CheckOptions.help:
                                    return false;
                                case CheckOptions.success:
                                    break;
                                default:
                                throw SwitchError.INSTANCE;
                            }
                        } while(__dispatch15 != 0);
                    }
                    if (!parseCLIOption_revert[Feature("dip25", "noDIP25", "revert DIP25 changes https://github.com/dlang/DIPs/blob/master/DIPs/archive/DIP25.md", true, false), Feature("import", "bug10378", "revert to single phase name lookup", true, true)].invoke(params, p))
                    {
                        error.invoke(new BytePtr("Revert `%s` is invalid"), p);
                        expr(params.revertUsage = true);
                        return false;
                    }
                    if (params.noDIP25)
                        expr(params.useDIP25 = false);
                }
                else if (__equals(arg, new ByteSlice("-w")))
                    params.warnings = DiagnosticReporting.error;
                else if (__equals(arg, new ByteSlice("-wi")))
                    params.warnings = DiagnosticReporting.inform;
                else if (__equals(arg, new ByteSlice("-O")))
                    expr(params.optimize = true);
                else if (((p.get(1) & 0xFF) == 111))
                {
                    BytePtr path = null;
                    {
                        int __dispatch16 = 0;
                        dispatched_16:
                        do {
                            switch (__dispatch16 != 0 ? __dispatch16 : (p.get(2) & 0xFF))
                            {
                                case 45:
                                    expr(params.obj = false);
                                    break;
                                case 100:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    path = pcopy((p.plus(3).plus((((p.get(3) & 0xFF) == 61) ? 1 : 0))));
                                    params.objdir = toDString(path).copy();
                                    break;
                                case 102:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    path = pcopy((p.plus(3).plus((((p.get(3) & 0xFF) == 61) ? 1 : 0))));
                                    params.objname = toDString(path).copy();
                                    break;
                                case 112:
                                    if (p.get(3) != 0)
                                        /*goto Lerror*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    expr(params.preservePaths = true);
                                    break;
                                case 0:
                                    error.invoke(new BytePtr("-o no longer supported, use -of or -od"), null);
                                    break;
                                default:
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            }
                        } while(__dispatch16 != 0);
                    }
                }
                else if (((p.get(1) & 0xFF) == 68))
                {
                    expr(params.doDocComments = true);
                    {
                        int __dispatch17 = 0;
                        dispatched_17:
                        do {
                            switch (__dispatch17 != 0 ? __dispatch17 : (p.get(2) & 0xFF))
                            {
                                case 100:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    params.docdir = pcopy((p.plus(3).plus((((p.get(3) & 0xFF) == 61) ? 1 : 0))));
                                    break;
                                case 102:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    params.docname = pcopy((p.plus(3).plus((((p.get(3) & 0xFF) == 61) ? 1 : 0))));
                                    break;
                                case 0:
                                    break;
                                default:
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            }
                        } while(__dispatch17 != 0);
                    }
                }
                else if (((p.get(1) & 0xFF) == 72))
                {
                    expr(params.doHdrGeneration = true);
                    {
                        int __dispatch18 = 0;
                        dispatched_18:
                        do {
                            switch (__dispatch18 != 0 ? __dispatch18 : (p.get(2) & 0xFF))
                            {
                                case 100:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    params.hdrdir = toDString(p.plus(3).plus((((p.get(3) & 0xFF) == 61) ? 1 : 0))).copy();
                                    break;
                                case 102:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    params.hdrname = toDString(p.plus(3).plus((((p.get(3) & 0xFF) == 61) ? 1 : 0))).copy();
                                    break;
                                case 0:
                                    break;
                                default:
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            }
                        } while(__dispatch18 != 0);
                    }
                }
                else if (((p.get(1) & 0xFF) == 88))
                {
                    expr(params.doJsonGeneration = true);
                    {
                        int __dispatch19 = 0;
                        dispatched_19:
                        do {
                            switch (__dispatch19 != 0 ? __dispatch19 : (p.get(2) & 0xFF))
                            {
                                case 102:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    params.jsonfilename = toDString(p.plus(3).plus((((p.get(3) & 0xFF) == 61) ? 1 : 0))).copy();
                                    break;
                                case 105:
                                    if (p.get(3) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    if (((p.get(3) & 0xFF) != 61))
                                        /*goto Lerror*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    if (p.get(4) == 0)
                                        /*goto Lnoarg*//*unrolled goto*/
                                        files.push(new BytePtr("__stdin.d"));
                                    {
                                        int flag = tryParseJsonField(p.plus(4));
                                        if (flag == 0)
                                        {
                                            error.invoke(new BytePtr("unknown JSON field `-Xi=%s`, expected one of `compilerInfo`, `buildInfo`, `modules`, `semantics`"), p.plus(4));
                                            continue L_outer4;
                                        }
                                        global.params.jsonFieldFlags |= flag;
                                    }
                                    break;
                                case 0:
                                    break;
                                default:
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            }
                        } while(__dispatch19 != 0);
                    }
                }
                else if (__equals(arg, new ByteSlice("-ignore")))
                    expr(params.ignoreUnsupportedPragmas = true);
                else if (__equals(arg, new ByteSlice("-inline")))
                {
                    expr(params.useInline = true);
                    expr(params.hdrStripPlainFunctions = false);
                }
                else if (__equals(arg, new ByteSlice("-i")))
                    expr(includeImports = true);
                else if (startsWith.invoke(p.plus(1), new ByteSlice("i=")))
                {
                    expr(includeImports = true);
                    if (p.get(3) == 0)
                    {
                        error.invoke(new BytePtr("invalid option '%s', module patterns cannot be empty"), p);
                    }
                    else
                    {
                        includeModulePatterns.push(p.plus(3));
                    }
                }
                else if (__equals(arg, new ByteSlice("-dip25")))
                    expr(params.useDIP25 = true);
                else if (__equals(arg, new ByteSlice("-dip1000")))
                {
                    expr(params.useDIP25 = true);
                    expr(params.vsafe = true);
                }
                else if (__equals(arg, new ByteSlice("-dip1008")))
                {
                    expr(params.ehnogc = true);
                }
                else if (__equals(arg, new ByteSlice("-lib")))
                    expr(params.lib = true);
                else if (__equals(arg, new ByteSlice("-nofloat")))
                    expr(params.nofloat = true);
                else if (__equals(arg, new ByteSlice("-quiet")))
                {
                }
                else if (__equals(arg, new ByteSlice("-release")))
                    expr(params.release = true);
                else if (__equals(arg, new ByteSlice("-betterC")))
                    expr(params.betterC = true);
                else if (__equals(arg, new ByteSlice("-noboundscheck")))
                {
                    params.boundscheck = CHECKENABLE.off;
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("boundscheck")))
                {
                    if (((p.get(12) & 0xFF) == 61))
                    {
                        if ((strcmp(p.plus(13), new BytePtr("on")) == 0))
                        {
                            params.boundscheck = CHECKENABLE.on;
                        }
                        else if ((strcmp(p.plus(13), new BytePtr("safeonly")) == 0))
                        {
                            params.boundscheck = CHECKENABLE.safeonly;
                        }
                        else if ((strcmp(p.plus(13), new BytePtr("off")) == 0))
                        {
                            params.boundscheck = CHECKENABLE.off;
                        }
                        else
                            /*goto Lerror*//*unrolled goto*/
                            files.push(new BytePtr("__stdin.d"));
                    }
                    else
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                }
                else if (__equals(arg, new ByteSlice("-unittest")))
                    expr(params.useUnitTests = true);
                else if (((p.get(1) & 0xFF) == 73))
                {
                    if (params.imppath == null)
                        params.imppath = new DArray<BytePtr>();
                    (params.imppath).push(p.plus(2).plus((((p.get(2) & 0xFF) == 61) ? 1 : 0)));
                }
                else if (((p.get(1) & 0xFF) == 109) && ((p.get(2) & 0xFF) == 118) && ((p.get(3) & 0xFF) == 61))
                {
                    if ((p.get(4) != 0) && (strchr(p.plus(5), 61) != null))
                    {
                        params.modFileAliasStrings.push(p.plus(4));
                    }
                    else
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                }
                else if (((p.get(1) & 0xFF) == 74))
                {
                    if (params.fileImppath == null)
                        params.fileImppath = new DArray<BytePtr>();
                    (params.fileImppath).push(p.plus(2).plus((((p.get(2) & 0xFF) == 61) ? 1 : 0)));
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("debug")) && ((p.get(6) & 0xFF) != 108))
                {
                    if (((p.get(6) & 0xFF) == 61))
                    {
                        if (isdigit((p.get(7) & 0xFF)) != 0)
                        {
                            int level = parseDigits.invoke(p.plus(7), 2147483647);
                            if ((level == -1))
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            params.debuglevel = level;
                        }
                        else if (Identifier.isValidIdentifier(p.plus(7)))
                        {
                            if (params.debugids == null)
                                params.debugids = new DArray<BytePtr>();
                            (params.debugids).push(p.plus(7));
                        }
                        else
                            /*goto Lerror*//*unrolled goto*/
                            files.push(new BytePtr("__stdin.d"));
                    }
                    else if (p.get(6) != 0)
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                    else
                        params.debuglevel = 1;
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("version")))
                {
                    if (((p.get(8) & 0xFF) == 61))
                    {
                        if (isdigit((p.get(9) & 0xFF)) != 0)
                        {
                            int level = parseDigits.invoke(p.plus(9), 2147483647);
                            if ((level == -1))
                                /*goto Lerror*//*unrolled goto*/
                                files.push(new BytePtr("__stdin.d"));
                            params.versionlevel = level;
                        }
                        else if (Identifier.isValidIdentifier(p.plus(9)))
                        {
                            if (params.versionids == null)
                                params.versionids = new DArray<BytePtr>();
                            (params.versionids).push(p.plus(9));
                        }
                        else
                            /*goto Lerror*//*unrolled goto*/
                            files.push(new BytePtr("__stdin.d"));
                    }
                    else
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                }
                else if (__equals(arg, new ByteSlice("--b")))
                    expr(params.debugb = true);
                else if (__equals(arg, new ByteSlice("--c")))
                    expr(params.debugc = true);
                else if (__equals(arg, new ByteSlice("--f")))
                    expr(params.debugf = true);
                else if (__equals(arg, new ByteSlice("--help")) || __equals(arg, new ByteSlice("-h")))
                {
                    expr(params.usage = true);
                    return false;
                }
                else if (__equals(arg, new ByteSlice("--r")))
                    expr(params.debugr = true);
                else if (__equals(arg, new ByteSlice("--version")))
                {
                    expr(params.logo = true);
                    return false;
                }
                else if (__equals(arg, new ByteSlice("--x")))
                    expr(params.debugx = true);
                else if (__equals(arg, new ByteSlice("--y")))
                    expr(params.debugy = true);
                else if (((p.get(1) & 0xFF) == 76))
                {
                    params.linkswitches.push(p.plus(2).plus((((p.get(2) & 0xFF) == 61) ? 1 : 0)));
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("defaultlib=")))
                {
                    params.defaultlibname = toDString(p.plus(1).plus(11)).copy();
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("debuglib=")))
                {
                    params.debuglibname = toDString(p.plus(1).plus(9)).copy();
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("deps")))
                {
                    if (params.moduleDeps != null)
                    {
                        error.invoke(new BytePtr("-deps[=file] can only be provided once!"), null);
                        break;
                    }
                    if (((p.get(5) & 0xFF) == 61))
                    {
                        params.moduleDepsFile = toDString(p.plus(1).plus(5)).copy();
                        if (params.moduleDepsFile.get(0) == 0)
                            /*goto Lnoarg*//*unrolled goto*/
                            files.push(new BytePtr("__stdin.d"));
                    }
                    else if (((p.get(5) & 0xFF) != 0))
                    {
                        /*goto Lerror*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                    }
                    params.moduleDeps = new OutBuffer(null, 0, 0, 0, false, false);
                }
                else if (__equals(arg, new ByteSlice("-main")))
                {
                    expr(params.addMain = true);
                }
                else if (startsWith.invoke(p.plus(1), new ByteSlice("man")))
                {
                    expr(params.manual = true);
                    return false;
                }
                else if (__equals(arg, new ByteSlice("-run")))
                {
                    expr(params.run = true);
                    int length = argc - i - 1;
                    if (length != 0)
                    {
                        BytePtr ext = pcopy(FileName.ext(arguments.get(i + 1)));
                        if ((ext != null) && ((FileName.equals(ext, new BytePtr("d")) ? 1 : 0) == 0) && ((FileName.equals(ext, new BytePtr("di")) ? 1 : 0) == 0))
                        {
                            error.invoke(new BytePtr("-run must be followed by a source file, not '%s'"), arguments.get(i + 1));
                            break;
                        }
                        if ((strcmp(arguments.get(i + 1), new BytePtr("-")) == 0))
                            files.push(new BytePtr("__stdin.d"));
                        else
                            files.push(arguments.get(i + 1));
                        params.runargs.setDim(length - 1);
                        {
                            int j = 0;
                            for (; (j < length - 1);j += 1){
                                params.runargs.set(j, arguments.get(i + 2 + j));
                            }
                        }
                        i += length;
                    }
                    else
                    {
                        expr(params.run = false);
                        /*goto Lnoarg*//*unrolled goto*/
                        files.push(new BytePtr("__stdin.d"));
                    }
                }
                else if (((p.get(1) & 0xFF) == 0))
                    files.push(new BytePtr("__stdin.d"));
                else
                {
                /*Lerror:*/
                    error.invoke(new BytePtr("unrecognized switch '%s'"), arguments.get(i));
                    continue L_outer4;
                /*Lnoarg:*/
                    error.invoke(new BytePtr("argument expected for switch '%s'"), arguments.get(i));
                    continue L_outer4;
                }
            }
        }
        return errors.value;
    }

    public static void reconcileCommands(Param params, int numSrcFiles) {
        if (params.lib && params.dll)
            error(Loc.initial, new BytePtr("cannot mix -lib and -shared"));
        expr(params.isLP64 = params.is64bit);
        if (((params.boundscheck & 0xFF) != 0))
        {
            if (((params.useArrayBounds & 0xFF) == 0))
                params.useArrayBounds = params.boundscheck;
        }
        if (params.useUnitTests)
        {
            if (((params.useAssert & 0xFF) == 0))
                params.useAssert = CHECKENABLE.on;
        }
        if (params.release)
        {
            if (((params.useInvariants & 0xFF) == 0))
                params.useInvariants = CHECKENABLE.off;
            if (((params.useIn & 0xFF) == 0))
                params.useIn = CHECKENABLE.off;
            if (((params.useOut & 0xFF) == 0))
                params.useOut = CHECKENABLE.off;
            if (((params.useArrayBounds & 0xFF) == 0))
                params.useArrayBounds = CHECKENABLE.safeonly;
            if (((params.useAssert & 0xFF) == 0))
                params.useAssert = CHECKENABLE.off;
            if (((params.useSwitchError & 0xFF) == 0))
                params.useSwitchError = CHECKENABLE.off;
        }
        else
        {
            if (((params.useInvariants & 0xFF) == 0))
                params.useInvariants = CHECKENABLE.on;
            if (((params.useIn & 0xFF) == 0))
                params.useIn = CHECKENABLE.on;
            if (((params.useOut & 0xFF) == 0))
                params.useOut = CHECKENABLE.on;
            if (((params.useArrayBounds & 0xFF) == 0))
                params.useArrayBounds = CHECKENABLE.on;
            if (((params.useAssert & 0xFF) == 0))
                params.useAssert = CHECKENABLE.on;
            if (((params.useSwitchError & 0xFF) == 0))
                params.useSwitchError = CHECKENABLE.on;
        }
        if (params.betterC)
        {
            params.checkAction = CHECKACTION.C;
            expr(params.useModuleInfo = false);
            expr(params.useTypeInfo = false);
            expr(params.useExceptions = false);
        }
        if (!params.obj || params.lib)
            expr(params.link = false);
        if (params.link)
        {
            params.exefile = params.objname.copy();
            expr(params.oneobj = true);
            if (params.objname.getLength() != 0)
            {
                params.objname = FileName.forceExt(params.objname, global.obj_ext).copy();
                if (params.objdir.getLength() != 0)
                {
                    ByteSlice name = FileName.name(params.objname).copy();
                    params.objname = FileName.combine(params.objdir, name).copy();
                }
            }
        }
        else if (params.run)
        {
            error(Loc.initial, new BytePtr("flags conflict with -run"));
            fatal();
        }
        else if (params.lib)
        {
            params.libname = params.objname.copy();
            params.objname = new ByteSlice().copy();
            if (!params.cov && !params.trace)
                expr(params.multiobj = true);
        }
        else
        {
            if ((params.objname.getLength() != 0) && (numSrcFiles != 0))
            {
                expr(params.oneobj = true);
            }
        }
        if (params.noDIP25)
            expr(params.useDIP25 = false);
    }

    public static DArray<dmodule.Module> createModules(DArray<BytePtr> files, DArray<BytePtr> libmodules) {
        DArray<dmodule.Module> modules = new DArray<dmodule.Module>();
        modules.reserve(files.length);
        boolean firstmodule = true;
        {
            int i = 0;
        L_outer5:
            for (; (i < files.length);i++){
                ByteSlice name = new ByteSlice();
                ByteSlice p = toDString(files.get(i)).copy();
                p = FileName.name(p).copy();
                ByteSlice ext = FileName.ext(p).copy();
                if (ext.getLength() != 0)
                {
                    if (FileName.equals(ext, global.obj_ext))
                    {
                        global.params.objfiles.push(files.get(i));
                        libmodules.push(files.get(i));
                        continue L_outer5;
                    }
                    if (FileName.equals(ext, global.lib_ext))
                    {
                        global.params.libfiles.push(files.get(i));
                        libmodules.push(files.get(i));
                        continue L_outer5;
                    }
                    if (FileName.equals(ext, global.dll_ext))
                    {
                        global.params.dllfiles.push(files.get(i));
                        libmodules.push(files.get(i));
                        continue L_outer5;
                    }
                    if (__equals(ext, toByteSlice(global.ddoc_ext)))
                    {
                        global.params.ddocfiles.push(files.get(i));
                        continue L_outer5;
                    }
                    if (FileName.equals(ext, toByteSlice(global.json_ext)))
                    {
                        expr(global.params.doJsonGeneration = true);
                        global.params.jsonfilename = toDString(files.get(i)).copy();
                        continue L_outer5;
                    }
                    if (FileName.equals(ext, toByteSlice(global.map_ext)))
                    {
                        global.params.mapfile = toDString(files.get(i)).copy();
                        continue L_outer5;
                    }
                    if (FileName.equals(ext, toByteSlice(global.mars_ext)) || FileName.equals(ext, toByteSlice(global.hdr_ext)) || FileName.equals(ext, new ByteSlice("dd")))
                    {
                        name = FileName.removeExt(p).copy();
                        if ((name.getLength() == 0) || __equals(name, new ByteSlice("..")) || __equals(name, new ByteSlice(".")))
                        {
                        /*Linvalid:*/
                            error(Loc.initial, new BytePtr("invalid file name '%s'"), files.get(i));
                            fatal();
                        }
                    }
                    else
                    {
                        error(Loc.initial, new BytePtr("unrecognized file extension %.*s"), ext.getLength(), toBytePtr(ext));
                        fatal();
                    }
                }
                else
                {
                    name = p.copy();
                    if (name.getLength() == 0)
                        /*goto Linvalid*/throw Dispatch0.INSTANCE;
                }
                Identifier id = Identifier.idPool(name);
                dmodule.Module m = new dmodule.Module(toDString(files.get(i)), id, (global.params.doDocComments ? 1 : 0), (global.params.doHdrGeneration ? 1 : 0));
                modules.push(m);
                if (firstmodule)
                {
                    global.params.objfiles.push(m.objfile.toChars());
                    expr(firstmodule = false);
                }
            }
        }
        return modules;
    }

}
