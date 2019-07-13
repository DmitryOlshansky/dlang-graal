package org.dlang.dmd;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.builtin.builtinDeinitialize;
import static org.dlang.dmd.builtin.builtin_init;
import static org.dlang.dmd.dsymbolsem.dsymbolSemantic;
import static org.dlang.dmd.errors.DiagnosticReporter;
import static org.dlang.dmd.errors.StderrDiagnosticReporter;
import static org.dlang.dmd.expression.Expression;
import static org.dlang.dmd.filecache.FileCache;
import static org.dlang.dmd.globals.CHECKENABLE;
import static org.dlang.dmd.globals.global;
import static org.dlang.dmd.id.Id;
import static org.dlang.dmd.mars.addDefaultVersionIdentifiers;
import static org.dlang.dmd.mars.setTarget;
import static org.dlang.dmd.mtype.Type;
import static org.dlang.dmd.objc.Objc;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.semantic2.semantic2;
import static org.dlang.dmd.semantic3.semantic3;
import static org.dlang.dmd.target.target;

public class frontend {


    static ByteSlice sep = new ByteSlice(":");
    static ByteSlice exe = new ByteSlice("");
    public static class Diagnostics
    {
        public int errors = 0;
        public int warnings = 0;
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
        global._init();
        global.params.useIn = contractChecks.precondition;
        global.params.useInvariants = contractChecks.invariant_;
        global.params.useOut = contractChecks.postcondition;
        global.params.useArrayBounds = contractChecks.arrayBounds;
        global.params.useAssert = contractChecks.assert_;
        global.params.useSwitchError = contractChecks.switchError;
        DArray arr  = new DArray<ByteSlice>();
        arr.pushSlice(versionIdentifiers);
        global.versionids = refPtr(arr);
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

    // defaulted all parameters starting with #2
    public static void initDMD(Slice<ByteSlice> versionIdentifiers) {
        initDMD(versionIdentifiers, new ContractChecks(ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled));
    }

    // defaulted all parameters starting with #1
    public static void initDMD() {
        initDMD(slice(new ByteSlice[]{}), new ContractChecks(ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled, ContractChecking.enabled));
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
        if ((global.path == null))
        {
            global.path = refPtr(new DArray<BytePtr>());
        }
        (global.path.get()).push(toStringz(path));
    }

    public static void addStringImport(ByteSlice path) {
        if ((global.filePath == null))
        {
            global.filePath = refPtr(new DArray<BytePtr>());
        }
        (global.filePath.get()).push(toStringz(path));
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

    public static DiagnosticReporter defaultDiagnosticReporter() {
        return new StderrDiagnosticReporter(global.params.useDeprecated);
    }

}
