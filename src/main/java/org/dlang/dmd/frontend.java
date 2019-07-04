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
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dinifile.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.filecache.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mars.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.target.*;

public class frontend {


    static ByteSlice sep =  new ByteSlice(":");
    static ByteSlice exe =  new ByteSlice("");
    public static class Diagnostics
    {
        public int errors;
        public int warnings;
        public  boolean hasErrors() {
            return this.errors > 0;
        }

        public  boolean hasWarnings() {
            return this.warnings > 0;
        }

        public Diagnostics(){
        }
        public Diagnostics copy(){
            Diagnostics r = new Diagnostics();
            r.errors = errors;
            r.warnings = warnings;
            return r;
        }
        public Diagnostics(int errors, int warnings) {
            this.errors = errors;
            this.warnings = warnings;
        }

        public Diagnostics opAssign(Diagnostics that) {
            this.errors = that.errors;
            this.warnings = that.warnings;
            return this;
        }
    }

    public static class ContractChecking 
    {
        public static final byte default_ = CHECKENABLE._default;
        public static final byte disabled = CHECKENABLE.off;
        public static final byte enabled = CHECKENABLE.on;
        public static final byte enabledInSafe = CHECKENABLE.safeonly;
    }

    public static class ContractChecks
    {
        public byte precondition = ContractChecking.enabled;
        public byte invariant_ = ContractChecking.enabled;
        public byte postcondition = ContractChecking.enabled;
        public byte arrayBounds = ContractChecking.enabled;
        public byte assert_ = ContractChecking.enabled;
        public byte switchError = ContractChecking.enabled;
        public ContractChecks(){
        }
        public ContractChecks copy(){
            ContractChecks r = new ContractChecks();
            r.precondition = precondition;
            r.invariant_ = invariant_;
            r.postcondition = postcondition;
            r.arrayBounds = arrayBounds;
            r.assert_ = assert_;
            r.switchError = switchError;
            return r;
        }
        public ContractChecks(byte precondition, byte invariant_, byte postcondition, byte arrayBounds, byte assert_, byte switchError) {
            this.precondition = precondition;
            this.invariant_ = invariant_;
            this.postcondition = postcondition;
            this.arrayBounds = arrayBounds;
            this.assert_ = assert_;
            this.switchError = switchError;
        }

        public ContractChecks opAssign(ContractChecks that) {
            this.precondition = that.precondition;
            this.invariant_ = that.invariant_;
            this.postcondition = that.postcondition;
            this.arrayBounds = that.arrayBounds;
            this.assert_ = that.assert_;
            this.switchError = that.switchError;
            return this;
        }
    }
    public static void initDMD(Slice<ByteSlice> versionIdentifiers, ContractChecks contractChecks) {
        Ref<Slice<ByteSlice>> versionIdentifiers_ref = ref(versionIdentifiers);
        global._init();
        {
            (__withSym).useIn = contractChecks.precondition;
            (__withSym).useInvariants = contractChecks.invariant_;
            (__withSym).useOut = contractChecks.postcondition;
            (__withSym).useArrayBounds = contractChecks.arrayBounds;
            (__withSym).useAssert = contractChecks.assert_;
            (__withSym).useSwitchError = contractChecks.switchError;
        }
        each(versionIdentifiers_ref);
        setTarget(global.params);
        addDefaultVersionIdentifiers(global.params);
        Type._init();
        Id.initialize();
        dmodule.Module._init();
        target._init(global.params);
        Expression._init();
        Objc._init();
        builtin_init();
        FileCache._init();
        CTFloat.initialize();
    }

    public static void deinitializeDMD() {
        global.deinitialize();
        Type.deinitialize();
        Id.deinitialize();
        dmodule.Module.deinitialize();
        target.deinitialize();
        Expression.deinitialize();
        Objc.deinitialize();
        builtinDeinitialize();
    }

    public static void addImport(ByteSlice path) {
        if (global.path == null)
            global.path = new DArray<BytePtr>();
        (global.path).push(toStringz(path));
    }

    public static void addStringImport(ByteSlice path) {
        if (global.filePath == null)
            global.filePath = new DArray<BytePtr>();
        (global.filePath).push(toStringz(path));
    }

    public static ByteSlice findDMDConfig(ByteSlice dmdFilePath) {
        ByteSlice configFile =  new ByteSlice("dmd.conf");
        return idup(findConfFile(dmdFilePath,  new ByteSlice("dmd.conf")));
    }

    public static ByteSlice findLDCConfig(ByteSlice ldcFilePath) {
        ByteSlice execDir = dirName(ldcFilePath).copy();
        ByteSlice ldcConfig =  new ByteSlice("ldc2.conf").copy();
        FilterResultnothingSlice<ByteSlice> ldcConfigs = filter(slice(new ByteSlice[]{buildPath(slice(new ByteSlice[]{getcwd(), toByteSlice(ldcConfig)})), buildPath(slice(new ByteSlice[]{execDir, toByteSlice(ldcConfig)})), buildPath(slice(new ByteSlice[]{dirName(execDir),  new ByteSlice("etc"), toByteSlice(ldcConfig)})), buildPath(slice(new ByteSlice[]{ new ByteSlice("~/.ldc"), toByteSlice(ldcConfig)})), buildPath(slice(new ByteSlice[]{execDir,  new ByteSlice("etc"), toByteSlice(ldcConfig)})), buildPath(slice(new ByteSlice[]{execDir,  new ByteSlice("etc"),  new ByteSlice("ldc"), toByteSlice(ldcConfig)})), buildPath(slice(new ByteSlice[]{ new ByteSlice("/etc"), toByteSlice(ldcConfig)})), buildPath(slice(new ByteSlice[]{ new ByteSlice("/etc/ldc"), toByteSlice(ldcConfig)}))})).copy();
        if (ldcConfigs.empty())
            return new ByteSlice();
        return ldcConfigs.front();
    }

    public static ByteSlice determineDefaultCompiler() {
        Slice<ByteSlice> compilers = slice(new ByteSlice[]{ new ByteSlice("dmd"),  new ByteSlice("gdc"),  new ByteSlice("gdmd"),  new ByteSlice("ldc2"),  new ByteSlice("ldmd2")}).copy();
        if (opBinaryRight( new ByteSlice("DMD")))
            compilers = (slice(new ByteSlice[]{environment.get( new ByteSlice("DMD"), new ByteSlice())}).concat(compilers)).copy();
        Result paths = splitter(environment.get( new ByteSlice("PATH"),  new ByteSlice("")),  new ByteSlice(":")).copy();
        FilterResultnothingResult res = filter(joiner(map.invoke(compilers))).copy();
        return !(res.empty()) ? res.front() : new ByteSlice();
    }

    public static MapResultnothingUniqResultnothingSortedRangeSlice<ByteSlice>nothing parseImportPathsFromConfig(ByteSlice iniFile, ByteSlice execDir) {
        return map(uniq(sort(array(joiner(map.invoke(new File(iniFile,  new ByteSlice("r")).byLineCopy(Flag.no, (byte)10)))))));
    }

    public static MapResultnothingUniqResultnothingSortedRangeSlice<ByteSlice>nothing findImportPaths() {
        ByteSlice execFilePath = determineDefaultCompiler().copy();
        assertMsg(execFilePath != new ByteSlice(),  new ByteSlice("No D compiler found. `Use parseImportsFromConfig` manually."));
        ByteSlice execDir = dirName(execFilePath).copy();
        ByteSlice iniFile = new ByteSlice();
        if ((endsWith(execFilePath,  new ByteSlice("ldc"),  new ByteSlice("ldc2"),  new ByteSlice("ldmd"),  new ByteSlice("ldmd2"))) != 0)
            iniFile = findLDCConfig(toByteSlice(execFilePath)).copy();
        else
            iniFile = findDMDConfig(toByteSlice(execFilePath)).copy();
        assertMsg((iniFile != new ByteSlice() && exists(iniFile)),  new ByteSlice("No valid config found."));
        return parseImportPathsFromConfig(toByteSlice(iniFile), toByteSlice(execDir));
    }

    public static void fullSemantic(dmodule.Module m) {
        m.importedFrom = m;
        m.importAll(null);
        dsymbolSemantic(m, null);
        dmodule.Module.dprogress = 1;
        dmodule.Module.runDeferredSemantic();
        semantic2(m, null);
        dmodule.Module.runDeferredSemantic2();
        semantic3(m, null);
        dmodule.Module.runDeferredSemantic3();
    }

    public static ByteSlice prettyPrint(dmodule.Module m) {
        OutBuffer buf = new OutBuffer(null, 0, 0, 0, true, false).copy();
        try {
            HdrGenState hgs = new HdrGenState(false, false, true, false, 0, 0, 0, false, null).copy();
            moduleToBuffer2(m, buf, hgs);
            Ref<ByteSlice> generated = ref(replace(fromStringz(buf.extractData()),  new ByteSlice("\u0009"),  new ByteSlice("    ")).copy());
            return assumeUnique(generated);
        }
        finally {
        }
    }

    public static DiagnosticReporter defaultDiagnosticReporter() {
        return new StderrDiagnosticReporter(global.params.useDeprecated);
    }

}
