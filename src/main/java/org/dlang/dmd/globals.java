package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.console.*;
import static org.dlang.dmd.utils.*;

public class globals {
    static int versionNumbercached = 0;


    public enum TARGET 
    {
        Linux(true),
        OSX(false),
        FreeBSD(false),
        OpenBSD(false),
        Solaris(false),
        Windows(false),
        DragonFlyBSD(false),
        ;
        public boolean value;
        TARGET(boolean value){ this.value = value; }
    }


    public enum DiagnosticReporting 
    {
        error((byte)0),
        inform((byte)1),
        off((byte)2),
        ;
        public byte value;
        DiagnosticReporting(byte value){ this.value = value; }
    }


    public enum CHECKENABLE 
    {
        _default((byte)0),
        off((byte)1),
        on((byte)2),
        safeonly((byte)3),
        ;
        public byte value;
        CHECKENABLE(byte value){ this.value = value; }
    }


    public enum CHECKACTION 
    {
        D((byte)0),
        C((byte)1),
        halt((byte)2),
        context((byte)3),
        ;
        public byte value;
        CHECKACTION(byte value){ this.value = value; }
    }


    public enum CPU 
    {
        x87(0),
        mmx(1),
        sse(2),
        sse2(3),
        sse3(4),
        ssse3(5),
        sse4_1(6),
        sse4_2(7),
        avx(8),
        avx2(9),
        avx512(10),
        baseline(11),
        native_(12),
        ;
        public int value;
        CPU(int value){ this.value = value; }
    }


    public enum JsonFieldFlags 
    {
        none(0),
        compilerInfo(1),
        buildInfo(2),
        modules(4),
        semantics(8),
        ;
        public int value;
        JsonFieldFlags(int value){ this.value = value; }
    }


    public enum CppStdRevision 
    {
        cpp98(199711),
        cpp11(201103),
        cpp14(201402),
        cpp17(201703),
        ;
        public int value;
        CppStdRevision(int value){ this.value = value; }
    }

    public static class Param
    {
        public boolean obj = true;
        public boolean link = true;
        public boolean dll;
        public boolean lib;
        public boolean multiobj;
        public boolean oneobj;
        public boolean trace;
        public boolean tracegc;
        public boolean verbose;
        public boolean vcg_ast;
        public boolean showColumns;
        public boolean vtls;
        public boolean vgc;
        public boolean vfield;
        public boolean vcomplex;
        public byte symdebug;
        public boolean symdebugref;
        public boolean alwaysframe;
        public boolean optimize;
        public boolean map;
        public boolean is64bit = false;
        public boolean isLP64;
        public boolean isLinux;
        public boolean isOSX;
        public boolean isWindows;
        public boolean isFreeBSD;
        public boolean isOpenBSD;
        public boolean isDragonFlyBSD;
        public boolean isSolaris;
        public boolean hasObjectiveC;
        public boolean mscoff = false;
        public DiagnosticReporting useDeprecated = DiagnosticReporting.inform;
        public boolean stackstomp;
        public boolean useUnitTests;
        public boolean useInline = false;
        public boolean useDIP25;
        public boolean noDIP25;
        public boolean release;
        public boolean preservePaths;
        public DiagnosticReporting warnings = DiagnosticReporting.off;
        public boolean pic;
        public boolean color;
        public boolean cov;
        public byte covPercent;
        public boolean nofloat;
        public boolean ignoreUnsupportedPragmas;
        public boolean useModuleInfo = true;
        public boolean useTypeInfo = true;
        public boolean useExceptions = true;
        public boolean betterC;
        public boolean addMain;
        public boolean allInst;
        public boolean check10378;
        public boolean bug10378;
        public boolean fix16997;
        public boolean fixAliasThis;
        public boolean vsafe;
        public boolean ehnogc;
        public boolean dtorFields;
        public boolean fieldwise;
        public CppStdRevision cplusplus = CppStdRevision.cpp98;
        public boolean markdown;
        public boolean vmarkdown;
        public boolean showGaggedErrors;
        public boolean printErrorContext;
        public boolean manual;
        public boolean usage;
        public boolean mcpuUsage;
        public boolean transitionUsage;
        public boolean checkUsage;
        public boolean checkActionUsage;
        public boolean revertUsage;
        public boolean previewUsage;
        public boolean externStdUsage;
        public boolean logo;
        public CPU cpu = CPU.baseline;
        public CHECKENABLE useInvariants = CHECKENABLE._default;
        public CHECKENABLE useIn = CHECKENABLE._default;
        public CHECKENABLE useOut = CHECKENABLE._default;
        public CHECKENABLE useArrayBounds = CHECKENABLE._default;
        public CHECKENABLE useAssert = CHECKENABLE._default;
        public CHECKENABLE useSwitchError = CHECKENABLE._default;
        public CHECKENABLE boundscheck = CHECKENABLE._default;
        public CHECKACTION checkAction = CHECKACTION.D;
        public int errorLimit = 20;
        public ByteSlice argv0;
        public DArray<BytePtr> modFileAliasStrings;
        public DArray<BytePtr> imppath;
        public DArray<BytePtr> fileImppath;
        public ByteSlice objdir;
        public ByteSlice objname;
        public ByteSlice libname;
        public boolean doDocComments;
        public BytePtr docdir;
        public BytePtr docname;
        public DArray<BytePtr> ddocfiles;
        public boolean doHdrGeneration;
        public ByteSlice hdrdir;
        public ByteSlice hdrname;
        public boolean hdrStripPlainFunctions = true;
        public boolean doJsonGeneration;
        public ByteSlice jsonfilename;
        public JsonFieldFlags jsonFieldFlags;
        public OutBuffer mixinOut;
        public BytePtr mixinFile;
        public int mixinLines;
        public int debuglevel;
        public DArray<BytePtr> debugids;
        public int versionlevel;
        public DArray<BytePtr> versionids;
        public ByteSlice defaultlibname;
        public ByteSlice debuglibname;
        public ByteSlice mscrtlib;
        public ByteSlice moduleDepsFile;
        public OutBuffer moduleDeps;
        public boolean debugb;
        public boolean debugc;
        public boolean debugf;
        public boolean debugr;
        public boolean debugx;
        public boolean debugy;
        public boolean run;
        public DArray<BytePtr> runargs;
        public DArray<BytePtr> objfiles;
        public DArray<BytePtr> linkswitches;
        public DArray<BytePtr> libfiles;
        public DArray<BytePtr> dllfiles;
        public ByteSlice deffile;
        public ByteSlice resfile;
        public ByteSlice exefile;
        public ByteSlice mapfile;
        public Param(){}
        public Param(boolean obj, boolean link, boolean dll, boolean lib, boolean multiobj, boolean oneobj, boolean trace, boolean tracegc, boolean verbose, boolean vcg_ast, boolean showColumns, boolean vtls, boolean vgc, boolean vfield, boolean vcomplex, byte symdebug, boolean symdebugref, boolean alwaysframe, boolean optimize, boolean map, boolean is64bit, boolean isLP64, boolean isLinux, boolean isOSX, boolean isWindows, boolean isFreeBSD, boolean isOpenBSD, boolean isDragonFlyBSD, boolean isSolaris, boolean hasObjectiveC, boolean mscoff, DiagnosticReporting useDeprecated, boolean stackstomp, boolean useUnitTests, boolean useInline, boolean useDIP25, boolean noDIP25, boolean release, boolean preservePaths, DiagnosticReporting warnings, boolean pic, boolean color, boolean cov, byte covPercent, boolean nofloat, boolean ignoreUnsupportedPragmas, boolean useModuleInfo, boolean useTypeInfo, boolean useExceptions, boolean betterC, boolean addMain, boolean allInst, boolean check10378, boolean bug10378, boolean fix16997, boolean fixAliasThis, boolean vsafe, boolean ehnogc, boolean dtorFields, boolean fieldwise, CppStdRevision cplusplus, boolean markdown, boolean vmarkdown, boolean showGaggedErrors, boolean printErrorContext, boolean manual, boolean usage, boolean mcpuUsage, boolean transitionUsage, boolean checkUsage, boolean checkActionUsage, boolean revertUsage, boolean previewUsage, boolean externStdUsage, boolean logo, CPU cpu, CHECKENABLE useInvariants, CHECKENABLE useIn, CHECKENABLE useOut, CHECKENABLE useArrayBounds, CHECKENABLE useAssert, CHECKENABLE useSwitchError, CHECKENABLE boundscheck, CHECKACTION checkAction, int errorLimit, ByteSlice argv0, DArray<BytePtr> modFileAliasStrings, DArray<BytePtr> imppath, DArray<BytePtr> fileImppath, ByteSlice objdir, ByteSlice objname, ByteSlice libname, boolean doDocComments, BytePtr docdir, BytePtr docname, DArray<BytePtr> ddocfiles, boolean doHdrGeneration, ByteSlice hdrdir, ByteSlice hdrname, boolean hdrStripPlainFunctions, boolean doJsonGeneration, ByteSlice jsonfilename, JsonFieldFlags jsonFieldFlags, OutBuffer mixinOut, BytePtr mixinFile, int mixinLines, int debuglevel, DArray<BytePtr> debugids, int versionlevel, DArray<BytePtr> versionids, ByteSlice defaultlibname, ByteSlice debuglibname, ByteSlice mscrtlib, ByteSlice moduleDepsFile, OutBuffer moduleDeps, boolean debugb, boolean debugc, boolean debugf, boolean debugr, boolean debugx, boolean debugy, boolean run, DArray<BytePtr> runargs, DArray<BytePtr> objfiles, DArray<BytePtr> linkswitches, DArray<BytePtr> libfiles, DArray<BytePtr> dllfiles, ByteSlice deffile, ByteSlice resfile, ByteSlice exefile, ByteSlice mapfile) {
            this.obj = obj;
            this.link = link;
            this.dll = dll;
            this.lib = lib;
            this.multiobj = multiobj;
            this.oneobj = oneobj;
            this.trace = trace;
            this.tracegc = tracegc;
            this.verbose = verbose;
            this.vcg_ast = vcg_ast;
            this.showColumns = showColumns;
            this.vtls = vtls;
            this.vgc = vgc;
            this.vfield = vfield;
            this.vcomplex = vcomplex;
            this.symdebug = symdebug;
            this.symdebugref = symdebugref;
            this.alwaysframe = alwaysframe;
            this.optimize = optimize;
            this.map = map;
            this.is64bit = is64bit;
            this.isLP64 = isLP64;
            this.isLinux = isLinux;
            this.isOSX = isOSX;
            this.isWindows = isWindows;
            this.isFreeBSD = isFreeBSD;
            this.isOpenBSD = isOpenBSD;
            this.isDragonFlyBSD = isDragonFlyBSD;
            this.isSolaris = isSolaris;
            this.hasObjectiveC = hasObjectiveC;
            this.mscoff = mscoff;
            this.useDeprecated = useDeprecated;
            this.stackstomp = stackstomp;
            this.useUnitTests = useUnitTests;
            this.useInline = useInline;
            this.useDIP25 = useDIP25;
            this.noDIP25 = noDIP25;
            this.release = release;
            this.preservePaths = preservePaths;
            this.warnings = warnings;
            this.pic = pic;
            this.color = color;
            this.cov = cov;
            this.covPercent = covPercent;
            this.nofloat = nofloat;
            this.ignoreUnsupportedPragmas = ignoreUnsupportedPragmas;
            this.useModuleInfo = useModuleInfo;
            this.useTypeInfo = useTypeInfo;
            this.useExceptions = useExceptions;
            this.betterC = betterC;
            this.addMain = addMain;
            this.allInst = allInst;
            this.check10378 = check10378;
            this.bug10378 = bug10378;
            this.fix16997 = fix16997;
            this.fixAliasThis = fixAliasThis;
            this.vsafe = vsafe;
            this.ehnogc = ehnogc;
            this.dtorFields = dtorFields;
            this.fieldwise = fieldwise;
            this.cplusplus = cplusplus;
            this.markdown = markdown;
            this.vmarkdown = vmarkdown;
            this.showGaggedErrors = showGaggedErrors;
            this.printErrorContext = printErrorContext;
            this.manual = manual;
            this.usage = usage;
            this.mcpuUsage = mcpuUsage;
            this.transitionUsage = transitionUsage;
            this.checkUsage = checkUsage;
            this.checkActionUsage = checkActionUsage;
            this.revertUsage = revertUsage;
            this.previewUsage = previewUsage;
            this.externStdUsage = externStdUsage;
            this.logo = logo;
            this.cpu = cpu;
            this.useInvariants = useInvariants;
            this.useIn = useIn;
            this.useOut = useOut;
            this.useArrayBounds = useArrayBounds;
            this.useAssert = useAssert;
            this.useSwitchError = useSwitchError;
            this.boundscheck = boundscheck;
            this.checkAction = checkAction;
            this.errorLimit = errorLimit;
            this.argv0 = argv0;
            this.modFileAliasStrings = modFileAliasStrings;
            this.imppath = imppath;
            this.fileImppath = fileImppath;
            this.objdir = objdir;
            this.objname = objname;
            this.libname = libname;
            this.doDocComments = doDocComments;
            this.docdir = docdir;
            this.docname = docname;
            this.ddocfiles = ddocfiles;
            this.doHdrGeneration = doHdrGeneration;
            this.hdrdir = hdrdir;
            this.hdrname = hdrname;
            this.hdrStripPlainFunctions = hdrStripPlainFunctions;
            this.doJsonGeneration = doJsonGeneration;
            this.jsonfilename = jsonfilename;
            this.jsonFieldFlags = jsonFieldFlags;
            this.mixinOut = mixinOut;
            this.mixinFile = mixinFile;
            this.mixinLines = mixinLines;
            this.debuglevel = debuglevel;
            this.debugids = debugids;
            this.versionlevel = versionlevel;
            this.versionids = versionids;
            this.defaultlibname = defaultlibname;
            this.debuglibname = debuglibname;
            this.mscrtlib = mscrtlib;
            this.moduleDepsFile = moduleDepsFile;
            this.moduleDeps = moduleDeps;
            this.debugb = debugb;
            this.debugc = debugc;
            this.debugf = debugf;
            this.debugr = debugr;
            this.debugx = debugx;
            this.debugy = debugy;
            this.run = run;
            this.runargs = runargs;
            this.objfiles = objfiles;
            this.linkswitches = linkswitches;
            this.libfiles = libfiles;
            this.dllfiles = dllfiles;
            this.deffile = deffile;
            this.resfile = resfile;
            this.exefile = exefile;
            this.mapfile = mapfile;
        }

        public Param opAssign(Param that) {
            this.obj = that.obj;
            this.link = that.link;
            this.dll = that.dll;
            this.lib = that.lib;
            this.multiobj = that.multiobj;
            this.oneobj = that.oneobj;
            this.trace = that.trace;
            this.tracegc = that.tracegc;
            this.verbose = that.verbose;
            this.vcg_ast = that.vcg_ast;
            this.showColumns = that.showColumns;
            this.vtls = that.vtls;
            this.vgc = that.vgc;
            this.vfield = that.vfield;
            this.vcomplex = that.vcomplex;
            this.symdebug = that.symdebug;
            this.symdebugref = that.symdebugref;
            this.alwaysframe = that.alwaysframe;
            this.optimize = that.optimize;
            this.map = that.map;
            this.is64bit = that.is64bit;
            this.isLP64 = that.isLP64;
            this.isLinux = that.isLinux;
            this.isOSX = that.isOSX;
            this.isWindows = that.isWindows;
            this.isFreeBSD = that.isFreeBSD;
            this.isOpenBSD = that.isOpenBSD;
            this.isDragonFlyBSD = that.isDragonFlyBSD;
            this.isSolaris = that.isSolaris;
            this.hasObjectiveC = that.hasObjectiveC;
            this.mscoff = that.mscoff;
            this.useDeprecated = that.useDeprecated;
            this.stackstomp = that.stackstomp;
            this.useUnitTests = that.useUnitTests;
            this.useInline = that.useInline;
            this.useDIP25 = that.useDIP25;
            this.noDIP25 = that.noDIP25;
            this.release = that.release;
            this.preservePaths = that.preservePaths;
            this.warnings = that.warnings;
            this.pic = that.pic;
            this.color = that.color;
            this.cov = that.cov;
            this.covPercent = that.covPercent;
            this.nofloat = that.nofloat;
            this.ignoreUnsupportedPragmas = that.ignoreUnsupportedPragmas;
            this.useModuleInfo = that.useModuleInfo;
            this.useTypeInfo = that.useTypeInfo;
            this.useExceptions = that.useExceptions;
            this.betterC = that.betterC;
            this.addMain = that.addMain;
            this.allInst = that.allInst;
            this.check10378 = that.check10378;
            this.bug10378 = that.bug10378;
            this.fix16997 = that.fix16997;
            this.fixAliasThis = that.fixAliasThis;
            this.vsafe = that.vsafe;
            this.ehnogc = that.ehnogc;
            this.dtorFields = that.dtorFields;
            this.fieldwise = that.fieldwise;
            this.cplusplus = that.cplusplus;
            this.markdown = that.markdown;
            this.vmarkdown = that.vmarkdown;
            this.showGaggedErrors = that.showGaggedErrors;
            this.printErrorContext = that.printErrorContext;
            this.manual = that.manual;
            this.usage = that.usage;
            this.mcpuUsage = that.mcpuUsage;
            this.transitionUsage = that.transitionUsage;
            this.checkUsage = that.checkUsage;
            this.checkActionUsage = that.checkActionUsage;
            this.revertUsage = that.revertUsage;
            this.previewUsage = that.previewUsage;
            this.externStdUsage = that.externStdUsage;
            this.logo = that.logo;
            this.cpu = that.cpu;
            this.useInvariants = that.useInvariants;
            this.useIn = that.useIn;
            this.useOut = that.useOut;
            this.useArrayBounds = that.useArrayBounds;
            this.useAssert = that.useAssert;
            this.useSwitchError = that.useSwitchError;
            this.boundscheck = that.boundscheck;
            this.checkAction = that.checkAction;
            this.errorLimit = that.errorLimit;
            this.argv0 = that.argv0;
            this.modFileAliasStrings = that.modFileAliasStrings;
            this.imppath = that.imppath;
            this.fileImppath = that.fileImppath;
            this.objdir = that.objdir;
            this.objname = that.objname;
            this.libname = that.libname;
            this.doDocComments = that.doDocComments;
            this.docdir = that.docdir;
            this.docname = that.docname;
            this.ddocfiles = that.ddocfiles;
            this.doHdrGeneration = that.doHdrGeneration;
            this.hdrdir = that.hdrdir;
            this.hdrname = that.hdrname;
            this.hdrStripPlainFunctions = that.hdrStripPlainFunctions;
            this.doJsonGeneration = that.doJsonGeneration;
            this.jsonfilename = that.jsonfilename;
            this.jsonFieldFlags = that.jsonFieldFlags;
            this.mixinOut = that.mixinOut;
            this.mixinFile = that.mixinFile;
            this.mixinLines = that.mixinLines;
            this.debuglevel = that.debuglevel;
            this.debugids = that.debugids;
            this.versionlevel = that.versionlevel;
            this.versionids = that.versionids;
            this.defaultlibname = that.defaultlibname;
            this.debuglibname = that.debuglibname;
            this.mscrtlib = that.mscrtlib;
            this.moduleDepsFile = that.moduleDepsFile;
            this.moduleDeps = that.moduleDeps;
            this.debugb = that.debugb;
            this.debugc = that.debugc;
            this.debugf = that.debugf;
            this.debugr = that.debugr;
            this.debugx = that.debugx;
            this.debugy = that.debugy;
            this.run = that.run;
            this.runargs = that.runargs;
            this.objfiles = that.objfiles;
            this.linkswitches = that.linkswitches;
            this.libfiles = that.libfiles;
            this.dllfiles = that.dllfiles;
            this.deffile = that.deffile;
            this.resfile = that.resfile;
            this.exefile = that.exefile;
            this.mapfile = that.mapfile;
            return this;
        }
    }
    static int STRUCTALIGN_DEFAULT = -1;
    public static class Global
    {
        public ByteSlice inifilename;
        public ByteSlice mars_ext =  new ByteSlice("d");
        public ByteSlice obj_ext;
        public ByteSlice lib_ext;
        public ByteSlice dll_ext;
        public ByteSlice doc_ext =  new ByteSlice("html");
        public ByteSlice ddoc_ext =  new ByteSlice("ddoc");
        public ByteSlice hdr_ext =  new ByteSlice("di");
        public ByteSlice json_ext =  new ByteSlice("json");
        public ByteSlice map_ext =  new ByteSlice("map");
        public boolean run_noext;
        public ByteSlice copyright =  new ByteSlice("Copyright (C) 1999-2019 by The D Language Foundation, All Rights Reserved");
        public ByteSlice written =  new ByteSlice("written by Walter Bright");
        public DArray<BytePtr> path;
        public DArray<BytePtr> filePath;
        public ByteSlice _version;
        public ByteSlice vendor;
        public Param params;
        public int errors;
        public int warnings;
        public int gag;
        public int gaggedErrors;
        public int gaggedWarnings;
        public BytePtr console;
        public DArray<Identifier> versionids;
        public DArray<Identifier> debugids;
        public  int startGagging() {
            this.gag += 1;
            this.gaggedWarnings = 0;
            return this.gaggedErrors;
        }

        public  boolean endGagging(int oldGagged) {
            boolean anyErrs = this.gaggedErrors != oldGagged;
            this.gag -= 1;
            this.errors -= this.gaggedErrors - oldGagged;
            this.gaggedErrors = oldGagged;
            return anyErrs;
        }

        public  void increaseErrorCount() {
            if ((this.gag) != 0)
                this.gaggedErrors += 1;
            this.errors += 1;
        }

        public  void _init() {
            this.obj_ext =  new ByteSlice("o");
            this.lib_ext =  new ByteSlice("a");
            this.dll_ext =  new ByteSlice("so");
            this.run_noext = true;
            this._version =  new ByteSlice("v2.086.0\n\u0000");
            this.vendor =  new ByteSlice("Digital Mars D");
            this.params.color = Console.detectTerminal();
        }

        public  void deinitialize() {
            this.opAssign(new Global(new ByteSlice(),  new ByteSlice("d"), new ByteSlice(), new ByteSlice(), new ByteSlice(),  new ByteSlice("html"),  new ByteSlice("ddoc"),  new ByteSlice("di"),  new ByteSlice("json"),  new ByteSlice("map"), false,  new ByteSlice("Copyright (C) 1999-2019 by The D Language Foundation, All Rights Reserved"),  new ByteSlice("written by Walter Bright"), null, null, new ByteSlice(), new ByteSlice(), new Param(true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, (byte)0, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, DiagnosticReporting.inform, false, false, false, false, false, false, false, DiagnosticReporting.off, false, false, false, (byte)0, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, CppStdRevision.cpp98, false, false, false, false, false, false, false, false, false, false, false, false, false, false, CPU.baseline, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKACTION.D, 20, new ByteSlice(), new DArray<BytePtr>(), null, null, new ByteSlice(), new ByteSlice(), new ByteSlice(), false, null, null, new DArray<BytePtr>(), false, new ByteSlice(), new ByteSlice(), true, false, new ByteSlice(), JsonFieldFlags.none, null, null, 0, 0, null, 0, null, new ByteSlice(), new ByteSlice(), new ByteSlice(), new ByteSlice(), null, false, false, false, false, false, false, false, new DArray<BytePtr>(), new DArray<BytePtr>(), new DArray<BytePtr>(), new DArray<BytePtr>(), new DArray<BytePtr>(), new ByteSlice(), new ByteSlice(), new ByteSlice(), new ByteSlice()), 0, 0, 0, 0, 0, null, null, null));
        }

        public  int versionNumber() {
            int cached = globals.versionNumbercached;
            if (cached == 0)
            {
                int major = 0;
                int minor = 0;
                boolean point = false;
                {
                    BytePtr p = this._version.toBytePtr().plus(1);
                    for (; ;p.postInc()){
                        byte c = p.get(0);
                        if ((isdigit((int)c)) != 0)
                        {
                            minor = minor * 10 + (int)c - 48;
                        }
                        else if (c == (byte)46)
                        {
                            if (point)
                                break;
                            point = true;
                            major = minor;
                            minor = 0;
                        }
                        else
                            break;
                    }
                }
                cached = major * 1000 + minor;
            }
            return cached;
        }

        public  ByteSlice finalDefaultlibname() {
            return this.params.betterC ? new ByteSlice() : (this.params.symdebug) != 0 ? this.params.debuglibname : this.params.defaultlibname;
        }

        public Global(){}
        public Global(ByteSlice inifilename, ByteSlice mars_ext, ByteSlice obj_ext, ByteSlice lib_ext, ByteSlice dll_ext, ByteSlice doc_ext, ByteSlice ddoc_ext, ByteSlice hdr_ext, ByteSlice json_ext, ByteSlice map_ext, boolean run_noext, ByteSlice copyright, ByteSlice written, DArray<BytePtr> path, DArray<BytePtr> filePath, ByteSlice _version, ByteSlice vendor, Param params, int errors, int warnings, int gag, int gaggedErrors, int gaggedWarnings, BytePtr console, DArray<Identifier> versionids, DArray<Identifier> debugids) {
            this.inifilename = inifilename;
            this.mars_ext = mars_ext;
            this.obj_ext = obj_ext;
            this.lib_ext = lib_ext;
            this.dll_ext = dll_ext;
            this.doc_ext = doc_ext;
            this.ddoc_ext = ddoc_ext;
            this.hdr_ext = hdr_ext;
            this.json_ext = json_ext;
            this.map_ext = map_ext;
            this.run_noext = run_noext;
            this.copyright = copyright;
            this.written = written;
            this.path = path;
            this.filePath = filePath;
            this._version = _version;
            this.vendor = vendor;
            this.params = params;
            this.errors = errors;
            this.warnings = warnings;
            this.gag = gag;
            this.gaggedErrors = gaggedErrors;
            this.gaggedWarnings = gaggedWarnings;
            this.console = console;
            this.versionids = versionids;
            this.debugids = debugids;
        }

        public Global opAssign(Global that) {
            this.inifilename = that.inifilename;
            this.mars_ext = that.mars_ext;
            this.obj_ext = that.obj_ext;
            this.lib_ext = that.lib_ext;
            this.dll_ext = that.dll_ext;
            this.doc_ext = that.doc_ext;
            this.ddoc_ext = that.ddoc_ext;
            this.hdr_ext = that.hdr_ext;
            this.json_ext = that.json_ext;
            this.map_ext = that.map_ext;
            this.run_noext = that.run_noext;
            this.copyright = that.copyright;
            this.written = that.written;
            this.path = that.path;
            this.filePath = that.filePath;
            this._version = that._version;
            this.vendor = that.vendor;
            this.params = that.params;
            this.errors = that.errors;
            this.warnings = that.warnings;
            this.gag = that.gag;
            this.gaggedErrors = that.gaggedErrors;
            this.gaggedWarnings = that.gaggedWarnings;
            this.console = that.console;
            this.versionids = that.versionids;
            this.debugids = that.debugids;
            return this;
        }
    }
    public static class Loc
    {
        public BytePtr filename;
        public int linnum;
        public int charnum;
        public static Loc initial;
        public  Loc __ctor(BytePtr filename, int linnum, int charnum) {
            this.linnum = linnum;
            this.charnum = charnum;
            this.filename = filename;
            return this;
        }

        public  BytePtr toChars(boolean showColumns) {
            OutBuffer buf = new OutBuffer();
            if (this.filename != null)
            {
                buf.writestring(this.filename);
            }
            if ((this.linnum) != 0)
            {
                buf.writeByte(40);
                buf.print((long)this.linnum);
                if (showColumns && (this.charnum) != 0)
                {
                    buf.writeByte(44);
                    buf.print((long)this.charnum);
                }
                buf.writeByte(41);
            }
            return buf.extractChars();
        }

        public  boolean equals(Loc loc) {
            return !(global.params.showColumns) || this.charnum == loc.charnum && this.linnum == loc.linnum && FileName.equals(this.filename, loc.filename);
        }

        public  boolean opEquals(Loc loc) {
            return this.charnum == loc.charnum && this.linnum == loc.linnum && this.filename == loc.filename || this.filename != null && loc.filename != null && strcmp(this.filename, loc.filename) == 0;
        }

        public  int toHash() {
            int hash = hashOf(this.linnum);
            hash = hashOf(this.charnum, hash);
            hash = hashOf(toDString(this.filename), hash);
            return hash;
        }

        public  boolean isValid() {
            return this.filename != null;
        }

        public Loc(){}
        public Loc(BytePtr filename, int linnum, int charnum) {
            this.filename = filename;
            this.linnum = linnum;
            this.charnum = charnum;
        }

        public Loc opAssign(Loc that) {
            this.filename = that.filename;
            this.linnum = that.linnum;
            this.charnum = that.charnum;
            return this;
        }
    }

    public enum LINK 
    {
        default_(0),
        d(1),
        c(2),
        cpp(3),
        windows(4),
        pascal(5),
        objc(6),
        system(7),
        ;
        public int value;
        LINK(int value){ this.value = value; }
    }


    public enum CPPMANGLE 
    {
        def(0),
        asStruct(1),
        asClass(2),
        ;
        public int value;
        CPPMANGLE(int value){ this.value = value; }
    }


    public enum MATCH 
    {
        nomatch(0),
        convert(1),
        constant(2),
        exact(3),
        ;
        public int value;
        MATCH(int value){ this.value = value; }
    }


    public enum PINLINE 
    {
        default_(0),
        never(1),
        always(2),
        ;
        public int value;
        PINLINE(int value){ this.value = value; }
    }

    static Global global;
}
