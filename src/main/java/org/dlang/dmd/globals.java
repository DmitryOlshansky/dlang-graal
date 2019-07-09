package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.console.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.utils.*;

public class globals {
    static int versionNumbercached = 0;

    // from template xversion!(_DragonFlyBSD)
    static boolean xversion_DragonFlyBSD = false;

    // from template xversion!(_FreeBSD)
    static boolean xversion_FreeBSD = false;

    // from template xversion!(_OSX)
    static boolean xversion_OSX = false;

    // from template xversion!(_OpenBSD)
    static boolean xversion_OpenBSD = false;

    // from template xversion!(_Solaris)
    static boolean xversion_Solaris = false;

    // from template xversion!(_Windows)
    static boolean xversion_Windows = false;

    // from template xversion!(_linux)
    static boolean xversion_linux = true;


    public static class TARGET 
    {
        public static final boolean Linux = true;
        public static final boolean OSX = false;
        public static final boolean FreeBSD = false;
        public static final boolean OpenBSD = false;
        public static final boolean Solaris = false;
        public static final boolean Windows = false;
        public static final boolean DragonFlyBSD = false;
    }


    public static class DiagnosticReporting 
    {
        public static final byte error = (byte)0;
        public static final byte inform = (byte)1;
        public static final byte off = (byte)2;
    }


    public static class CHECKENABLE 
    {
        public static final byte _default = (byte)0;
        public static final byte off = (byte)1;
        public static final byte on = (byte)2;
        public static final byte safeonly = (byte)3;
    }


    public static class CHECKACTION 
    {
        public static final byte D = (byte)0;
        public static final byte C = (byte)1;
        public static final byte halt = (byte)2;
        public static final byte context = (byte)3;
    }


    public static class CPU 
    {
        public static final int x87 = 0;
        public static final int mmx = 1;
        public static final int sse = 2;
        public static final int sse2 = 3;
        public static final int sse3 = 4;
        public static final int ssse3 = 5;
        public static final int sse4_1 = 6;
        public static final int sse4_2 = 7;
        public static final int avx = 8;
        public static final int avx2 = 9;
        public static final int avx512 = 10;
        public static final int baseline = 11;
        public static final int native_ = 12;
    }


    public static class PIC 
    {
        public static final byte fixed = (byte)0;
        public static final byte pic = (byte)1;
        public static final byte pie = (byte)2;
    }


    public static class JsonFieldFlags 
    {
        public static final int none = 0;
        public static final int compilerInfo = 1;
        public static final int buildInfo = 2;
        public static final int modules = 4;
        public static final int semantics = 8;
    }


    public static class CppStdRevision 
    {
        public static final int cpp98 = 199711;
        public static final int cpp11 = 201103;
        public static final int cpp14 = 201402;
        public static final int cpp17 = 201703;
    }

    public static class Param
    {
        public boolean obj = true;
        public boolean link = true;
        public boolean dll = false;
        public boolean lib = false;
        public boolean multiobj = false;
        public boolean oneobj = false;
        public boolean trace = false;
        public boolean tracegc = false;
        public boolean verbose = false;
        public boolean vcg_ast = false;
        public boolean showColumns = false;
        public boolean vtls = false;
        public boolean vgc = false;
        public boolean vfield = false;
        public boolean vcomplex = false;
        public byte symdebug = 0;
        public boolean symdebugref = false;
        public boolean alwaysframe = false;
        public boolean optimize = false;
        public boolean map = false;
        public boolean is64bit = false;
        public boolean isLP64 = false;
        public boolean isLinux = false;
        public boolean isOSX = false;
        public boolean isWindows = false;
        public boolean isFreeBSD = false;
        public boolean isOpenBSD = false;
        public boolean isDragonFlyBSD = false;
        public boolean isSolaris = false;
        public boolean hasObjectiveC = false;
        public boolean mscoff = false;
        public byte useDeprecated = DiagnosticReporting.inform;
        public boolean stackstomp = false;
        public boolean useUnitTests = false;
        public boolean useInline = false;
        public boolean useDIP25 = false;
        public boolean noDIP25 = false;
        public boolean release = false;
        public boolean preservePaths = false;
        public byte warnings = DiagnosticReporting.off;
        public byte pic = PIC.fixed;
        public boolean color = false;
        public boolean cov = false;
        public byte covPercent = 0;
        public boolean nofloat = false;
        public boolean ignoreUnsupportedPragmas = false;
        public boolean useModuleInfo = true;
        public boolean useTypeInfo = true;
        public boolean useExceptions = true;
        public boolean betterC = false;
        public boolean addMain = false;
        public boolean allInst = false;
        public boolean check10378 = false;
        public boolean bug10378 = false;
        public boolean fix16997 = false;
        public boolean fixAliasThis = false;
        public boolean vsafe = false;
        public boolean ehnogc = false;
        public boolean dtorFields = false;
        public boolean fieldwise = false;
        public boolean rvalueRefParam = false;
        public int cplusplus = CppStdRevision.cpp98;
        public boolean markdown = false;
        public boolean vmarkdown = false;
        public boolean showGaggedErrors = false;
        public boolean printErrorContext = false;
        public boolean manual = false;
        public boolean usage = false;
        public boolean mcpuUsage = false;
        public boolean transitionUsage = false;
        public boolean checkUsage = false;
        public boolean checkActionUsage = false;
        public boolean revertUsage = false;
        public boolean previewUsage = false;
        public boolean externStdUsage = false;
        public boolean logo = false;
        public int cpu = CPU.baseline;
        public byte useInvariants = CHECKENABLE._default;
        public byte useIn = CHECKENABLE._default;
        public byte useOut = CHECKENABLE._default;
        public byte useArrayBounds = CHECKENABLE._default;
        public byte useAssert = CHECKENABLE._default;
        public byte useSwitchError = CHECKENABLE._default;
        public byte boundscheck = CHECKENABLE._default;
        public byte checkAction = CHECKACTION.D;
        public int errorLimit = 20;
        public ByteSlice argv0;
        public DArray<BytePtr> modFileAliasStrings = new DArray<BytePtr>();
        public DArray<BytePtr> imppath;
        public DArray<BytePtr> fileImppath;
        public ByteSlice objdir;
        public ByteSlice objname;
        public ByteSlice libname;
        public boolean doDocComments = false;
        public BytePtr docdir;
        public BytePtr docname;
        public DArray<BytePtr> ddocfiles = new DArray<BytePtr>();
        public boolean doHdrGeneration = false;
        public ByteSlice hdrdir;
        public ByteSlice hdrname;
        public boolean hdrStripPlainFunctions = true;
        public boolean doJsonGeneration = false;
        public ByteSlice jsonfilename;
        public int jsonFieldFlags = 0;
        public OutBuffer mixinOut;
        public BytePtr mixinFile;
        public int mixinLines = 0;
        public int debuglevel = 0;
        public DArray<BytePtr> debugids;
        public int versionlevel = 0;
        public DArray<BytePtr> versionids;
        public ByteSlice defaultlibname;
        public ByteSlice debuglibname;
        public ByteSlice mscrtlib;
        public ByteSlice moduleDepsFile;
        public OutBuffer moduleDeps;
        public boolean debugb = false;
        public boolean debugc = false;
        public boolean debugf = false;
        public boolean debugr = false;
        public boolean debugx = false;
        public boolean debugy = false;
        public boolean run = false;
        public DArray<BytePtr> runargs = new DArray<BytePtr>();
        public DArray<BytePtr> objfiles = new DArray<BytePtr>();
        public DArray<BytePtr> linkswitches = new DArray<BytePtr>();
        public DArray<BytePtr> libfiles = new DArray<BytePtr>();
        public DArray<BytePtr> dllfiles = new DArray<BytePtr>();
        public ByteSlice deffile;
        public ByteSlice resfile;
        public ByteSlice exefile;
        public ByteSlice mapfile;
        public  boolean isPOSIX() {
            boolean __result = false;
            try {
                __result = this.isLinux || this.isOSX || this.isFreeBSD || this.isOpenBSD || this.isDragonFlyBSD || this.isSolaris;
                /*goto __returnLabel*/throw Dispatch0.INSTANCE;
            }
            catch(Dispatch0 __d){}
        /*__returnLabel:*/
            {
                boolean result = __result;
                {
                    assert(result || this.isWindows);
                }
            }
            return __result;
        }

        public Param(){
            modFileAliasStrings = new DArray<BytePtr>();
            ddocfiles = new DArray<BytePtr>();
            runargs = new DArray<BytePtr>();
            objfiles = new DArray<BytePtr>();
            linkswitches = new DArray<BytePtr>();
            libfiles = new DArray<BytePtr>();
            dllfiles = new DArray<BytePtr>();
        }
        public Param copy(){
            Param r = new Param();
            r.obj = obj;
            r.link = link;
            r.dll = dll;
            r.lib = lib;
            r.multiobj = multiobj;
            r.oneobj = oneobj;
            r.trace = trace;
            r.tracegc = tracegc;
            r.verbose = verbose;
            r.vcg_ast = vcg_ast;
            r.showColumns = showColumns;
            r.vtls = vtls;
            r.vgc = vgc;
            r.vfield = vfield;
            r.vcomplex = vcomplex;
            r.symdebug = symdebug;
            r.symdebugref = symdebugref;
            r.alwaysframe = alwaysframe;
            r.optimize = optimize;
            r.map = map;
            r.is64bit = is64bit;
            r.isLP64 = isLP64;
            r.isLinux = isLinux;
            r.isOSX = isOSX;
            r.isWindows = isWindows;
            r.isFreeBSD = isFreeBSD;
            r.isOpenBSD = isOpenBSD;
            r.isDragonFlyBSD = isDragonFlyBSD;
            r.isSolaris = isSolaris;
            r.hasObjectiveC = hasObjectiveC;
            r.mscoff = mscoff;
            r.useDeprecated = useDeprecated;
            r.stackstomp = stackstomp;
            r.useUnitTests = useUnitTests;
            r.useInline = useInline;
            r.useDIP25 = useDIP25;
            r.noDIP25 = noDIP25;
            r.release = release;
            r.preservePaths = preservePaths;
            r.warnings = warnings;
            r.pic = pic;
            r.color = color;
            r.cov = cov;
            r.covPercent = covPercent;
            r.nofloat = nofloat;
            r.ignoreUnsupportedPragmas = ignoreUnsupportedPragmas;
            r.useModuleInfo = useModuleInfo;
            r.useTypeInfo = useTypeInfo;
            r.useExceptions = useExceptions;
            r.betterC = betterC;
            r.addMain = addMain;
            r.allInst = allInst;
            r.check10378 = check10378;
            r.bug10378 = bug10378;
            r.fix16997 = fix16997;
            r.fixAliasThis = fixAliasThis;
            r.vsafe = vsafe;
            r.ehnogc = ehnogc;
            r.dtorFields = dtorFields;
            r.fieldwise = fieldwise;
            r.rvalueRefParam = rvalueRefParam;
            r.cplusplus = cplusplus;
            r.markdown = markdown;
            r.vmarkdown = vmarkdown;
            r.showGaggedErrors = showGaggedErrors;
            r.printErrorContext = printErrorContext;
            r.manual = manual;
            r.usage = usage;
            r.mcpuUsage = mcpuUsage;
            r.transitionUsage = transitionUsage;
            r.checkUsage = checkUsage;
            r.checkActionUsage = checkActionUsage;
            r.revertUsage = revertUsage;
            r.previewUsage = previewUsage;
            r.externStdUsage = externStdUsage;
            r.logo = logo;
            r.cpu = cpu;
            r.useInvariants = useInvariants;
            r.useIn = useIn;
            r.useOut = useOut;
            r.useArrayBounds = useArrayBounds;
            r.useAssert = useAssert;
            r.useSwitchError = useSwitchError;
            r.boundscheck = boundscheck;
            r.checkAction = checkAction;
            r.errorLimit = errorLimit;
            r.argv0 = argv0.copy();
            r.modFileAliasStrings = modFileAliasStrings.copy();
            r.imppath = imppath;
            r.fileImppath = fileImppath;
            r.objdir = objdir.copy();
            r.objname = objname.copy();
            r.libname = libname.copy();
            r.doDocComments = doDocComments;
            r.docdir = docdir;
            r.docname = docname;
            r.ddocfiles = ddocfiles.copy();
            r.doHdrGeneration = doHdrGeneration;
            r.hdrdir = hdrdir.copy();
            r.hdrname = hdrname.copy();
            r.hdrStripPlainFunctions = hdrStripPlainFunctions;
            r.doJsonGeneration = doJsonGeneration;
            r.jsonfilename = jsonfilename.copy();
            r.jsonFieldFlags = jsonFieldFlags;
            r.mixinOut = mixinOut;
            r.mixinFile = mixinFile;
            r.mixinLines = mixinLines;
            r.debuglevel = debuglevel;
            r.debugids = debugids;
            r.versionlevel = versionlevel;
            r.versionids = versionids;
            r.defaultlibname = defaultlibname.copy();
            r.debuglibname = debuglibname.copy();
            r.mscrtlib = mscrtlib.copy();
            r.moduleDepsFile = moduleDepsFile.copy();
            r.moduleDeps = moduleDeps;
            r.debugb = debugb;
            r.debugc = debugc;
            r.debugf = debugf;
            r.debugr = debugr;
            r.debugx = debugx;
            r.debugy = debugy;
            r.run = run;
            r.runargs = runargs.copy();
            r.objfiles = objfiles.copy();
            r.linkswitches = linkswitches.copy();
            r.libfiles = libfiles.copy();
            r.dllfiles = dllfiles.copy();
            r.deffile = deffile.copy();
            r.resfile = resfile.copy();
            r.exefile = exefile.copy();
            r.mapfile = mapfile.copy();
            return r;
        }
        public Param(boolean obj, boolean link, boolean dll, boolean lib, boolean multiobj, boolean oneobj, boolean trace, boolean tracegc, boolean verbose, boolean vcg_ast, boolean showColumns, boolean vtls, boolean vgc, boolean vfield, boolean vcomplex, byte symdebug, boolean symdebugref, boolean alwaysframe, boolean optimize, boolean map, boolean is64bit, boolean isLP64, boolean isLinux, boolean isOSX, boolean isWindows, boolean isFreeBSD, boolean isOpenBSD, boolean isDragonFlyBSD, boolean isSolaris, boolean hasObjectiveC, boolean mscoff, byte useDeprecated, boolean stackstomp, boolean useUnitTests, boolean useInline, boolean useDIP25, boolean noDIP25, boolean release, boolean preservePaths, byte warnings, byte pic, boolean color, boolean cov, byte covPercent, boolean nofloat, boolean ignoreUnsupportedPragmas, boolean useModuleInfo, boolean useTypeInfo, boolean useExceptions, boolean betterC, boolean addMain, boolean allInst, boolean check10378, boolean bug10378, boolean fix16997, boolean fixAliasThis, boolean vsafe, boolean ehnogc, boolean dtorFields, boolean fieldwise, boolean rvalueRefParam, int cplusplus, boolean markdown, boolean vmarkdown, boolean showGaggedErrors, boolean printErrorContext, boolean manual, boolean usage, boolean mcpuUsage, boolean transitionUsage, boolean checkUsage, boolean checkActionUsage, boolean revertUsage, boolean previewUsage, boolean externStdUsage, boolean logo, int cpu, byte useInvariants, byte useIn, byte useOut, byte useArrayBounds, byte useAssert, byte useSwitchError, byte boundscheck, byte checkAction, int errorLimit, ByteSlice argv0, DArray<BytePtr> modFileAliasStrings, DArray<BytePtr> imppath, DArray<BytePtr> fileImppath, ByteSlice objdir, ByteSlice objname, ByteSlice libname, boolean doDocComments, BytePtr docdir, BytePtr docname, DArray<BytePtr> ddocfiles, boolean doHdrGeneration, ByteSlice hdrdir, ByteSlice hdrname, boolean hdrStripPlainFunctions, boolean doJsonGeneration, ByteSlice jsonfilename, int jsonFieldFlags, OutBuffer mixinOut, BytePtr mixinFile, int mixinLines, int debuglevel, DArray<BytePtr> debugids, int versionlevel, DArray<BytePtr> versionids, ByteSlice defaultlibname, ByteSlice debuglibname, ByteSlice mscrtlib, ByteSlice moduleDepsFile, OutBuffer moduleDeps, boolean debugb, boolean debugc, boolean debugf, boolean debugr, boolean debugx, boolean debugy, boolean run, DArray<BytePtr> runargs, DArray<BytePtr> objfiles, DArray<BytePtr> linkswitches, DArray<BytePtr> libfiles, DArray<BytePtr> dllfiles, ByteSlice deffile, ByteSlice resfile, ByteSlice exefile, ByteSlice mapfile) {
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
            this.rvalueRefParam = rvalueRefParam;
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
            this.rvalueRefParam = that.rvalueRefParam;
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
        public ByteSlice mars_ext = new ByteSlice("d");
        public ByteSlice obj_ext;
        public ByteSlice lib_ext;
        public ByteSlice dll_ext;
        public ByteSlice doc_ext = new ByteSlice("html");
        public ByteSlice ddoc_ext = new ByteSlice("ddoc");
        public ByteSlice hdr_ext = new ByteSlice("di");
        public ByteSlice json_ext = new ByteSlice("json");
        public ByteSlice map_ext = new ByteSlice("map");
        public boolean run_noext = false;
        public ByteSlice copyright = new ByteSlice("Copyright (C) 1999-2019 by The D Language Foundation, All Rights Reserved");
        public ByteSlice written = new ByteSlice("written by Walter Bright");
        public DArray<BytePtr> path;
        public DArray<BytePtr> filePath;
        public ByteSlice _version;
        public ByteSlice vendor;
        public Param params = new Param();
        public int errors = 0;
        public int warnings = 0;
        public int gag = 0;
        public int gaggedErrors = 0;
        public int gaggedWarnings = 0;
        public Object console;
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
            if (this.gag != 0)
                this.gaggedErrors += 1;
            this.errors += 1;
        }

        public  void _init() {
            this.obj_ext = new ByteSlice("o").copy();
            this.lib_ext = new ByteSlice("a").copy();
            this.dll_ext = new ByteSlice("so").copy();
            this.run_noext = true;
            this._version = new ByteSlice("v2.087.0-beta.2\n\u0000").copy();
            this.vendor = new ByteSlice("Digital Mars D").copy();
            this.params.color = Console.detectTerminal();
        }

        public  void deinitialize() {
            this.opAssign(new Global(new ByteSlice(), new ByteSlice("d"), new ByteSlice(), new ByteSlice(), new ByteSlice(), new ByteSlice("html"), new ByteSlice("ddoc"), new ByteSlice("di"), new ByteSlice("json"), new ByteSlice("map"), false, new ByteSlice("Copyright (C) 1999-2019 by The D Language Foundation, All Rights Reserved"), new ByteSlice("written by Walter Bright"), null, null, new ByteSlice(), new ByteSlice(), new Param(true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, (byte)0, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, DiagnosticReporting.inform, false, false, false, false, false, false, false, DiagnosticReporting.off, PIC.fixed, false, false, (byte)0, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, CppStdRevision.cpp98, false, false, false, false, false, false, false, false, false, false, false, false, false, false, CPU.baseline, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKENABLE._default, CHECKACTION.D, 20, new ByteSlice(), new DArray<BytePtr>(), null, null, new ByteSlice(), new ByteSlice(), new ByteSlice(), false, null, null, new DArray<BytePtr>(), false, new ByteSlice(), new ByteSlice(), true, false, new ByteSlice(), JsonFieldFlags.none, null, null, 0, 0, null, 0, null, new ByteSlice(), new ByteSlice(), new ByteSlice(), new ByteSlice(), null, false, false, false, false, false, false, false, new DArray<BytePtr>(), new DArray<BytePtr>(), new DArray<BytePtr>(), new DArray<BytePtr>(), new DArray<BytePtr>(), new ByteSlice(), new ByteSlice(), new ByteSlice(), new ByteSlice()), 0, 0, 0, 0, 0, null, null, null));
        }

        public  int versionNumber() {
            if ((globals.versionNumbercached == 0))
            {
                int major = 0;
                int minor = 0;
                boolean point = false;
                {
                    BytePtr p = pcopy(toBytePtr(this._version).plus(1));
                    for (; ;p.postInc()){
                        byte c = p.get();
                        if (isdigit((c & 0xFF)) != 0)
                        {
                            minor = minor * 10 + (c & 0xFF) - 48;
                        }
                        else if (((c & 0xFF) == 46))
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
                globals.versionNumbercached = major * 1000 + minor;
            }
            return globals.versionNumbercached;
        }

        public  ByteSlice finalDefaultlibname() {
            return this.params.betterC ? new ByteSlice() : this.params.symdebug != 0 ? this.params.debuglibname : this.params.defaultlibname;
        }

        public Global(){
            params = new Param();
        }
        public Global copy(){
            Global r = new Global();
            r.inifilename = inifilename.copy();
            r.mars_ext = mars_ext.copy();
            r.obj_ext = obj_ext.copy();
            r.lib_ext = lib_ext.copy();
            r.dll_ext = dll_ext.copy();
            r.doc_ext = doc_ext.copy();
            r.ddoc_ext = ddoc_ext.copy();
            r.hdr_ext = hdr_ext.copy();
            r.json_ext = json_ext.copy();
            r.map_ext = map_ext.copy();
            r.run_noext = run_noext;
            r.copyright = copyright.copy();
            r.written = written.copy();
            r.path = path;
            r.filePath = filePath;
            r._version = _version.copy();
            r.vendor = vendor.copy();
            r.params = params.copy();
            r.errors = errors;
            r.warnings = warnings;
            r.gag = gag;
            r.gaggedErrors = gaggedErrors;
            r.gaggedWarnings = gaggedWarnings;
            r.console = console;
            r.versionids = versionids;
            r.debugids = debugids;
            return r;
        }
        public Global(ByteSlice inifilename, ByteSlice mars_ext, ByteSlice obj_ext, ByteSlice lib_ext, ByteSlice dll_ext, ByteSlice doc_ext, ByteSlice ddoc_ext, ByteSlice hdr_ext, ByteSlice json_ext, ByteSlice map_ext, boolean run_noext, ByteSlice copyright, ByteSlice written, DArray<BytePtr> path, DArray<BytePtr> filePath, ByteSlice _version, ByteSlice vendor, Param params, int errors, int warnings, int gag, int gaggedErrors, int gaggedWarnings, Object console, DArray<Identifier> versionids, DArray<Identifier> debugids) {
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
        public int linnum = 0;
        public int charnum = 0;
        public static Loc initial = new Loc();
        public  Loc(BytePtr filename, int linnum, int charnum) {
            this.linnum = linnum;
            this.charnum = charnum;
            this.filename = pcopy(filename);
        }

        public  BytePtr toChars(boolean showColumns) {
            OutBuffer buf = new OutBuffer();
            try {
                if (this.filename != null)
                {
                    buf.writestring(this.filename);
                }
                if (this.linnum != 0)
                {
                    buf.writeByte(40);
                    buf.print((long)this.linnum);
                    if (showColumns && (this.charnum != 0))
                    {
                        buf.writeByte(44);
                        buf.print((long)this.charnum);
                    }
                    buf.writeByte(41);
                }
                return buf.extractChars();
            }
            finally {
            }
        }

        // defaulted all parameters starting with #1
        public  BytePtr toChars() {
            toChars(global.params.showColumns);
        }

        public  boolean equals(Loc loc) {
            return !global.params.showColumns || (this.charnum == loc.charnum) && (this.linnum == loc.linnum) && FileName.equals(this.filename, loc.filename);
        }

        public  boolean opEquals(Loc loc) {
            return (this.charnum == loc.charnum) && (this.linnum == loc.linnum) && (this.filename == loc.filename) || (this.filename != null) && (loc.filename != null) && (strcmp(this.filename, loc.filename) == 0);
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

        public Loc(){
        }
        public Loc copy(){
            Loc r = new Loc();
            r.filename = filename;
            r.linnum = linnum;
            r.charnum = charnum;
            return r;
        }
        public Loc opAssign(Loc that) {
            this.filename = that.filename;
            this.linnum = that.linnum;
            this.charnum = that.charnum;
            return this;
        }
    }

    public static class LINK 
    {
        public static final int default_ = 0;
        public static final int d = 1;
        public static final int c = 2;
        public static final int cpp = 3;
        public static final int windows = 4;
        public static final int pascal = 5;
        public static final int objc = 6;
        public static final int system = 7;
    }


    public static class CPPMANGLE 
    {
        public static final int def = 0;
        public static final int asStruct = 1;
        public static final int asClass = 2;
    }


    public static class MATCH 
    {
        public static final int nomatch = 0;
        public static final int convert = 1;
        public static final int constant = 2;
        public static final int exact = 3;
    }


    public static class PINLINE 
    {
        public static final int default_ = 0;
        public static final int never = 1;
        public static final int always = 2;
    }

    static Global global = new Global();
}
