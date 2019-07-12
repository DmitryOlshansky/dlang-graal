package org.dlang.dmd;

import org.dlang.dmd.root.BytePtr;
import org.dlang.dmd.root.ByteSlice;
import org.dlang.dmd.root.DArray;
import org.dlang.dmd.root.Dispatch0;

import static org.dlang.dmd.cond.VersionCondition;
import static org.dlang.dmd.errors.error;
import static org.dlang.dmd.errors.fatal;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.Identifier;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.filename.FileName;
import static org.dlang.dmd.utils.toDString;

public class mars {

    private static class CheckOptions 
    {
        public static final int success = 0;
        public static final int error = 1;
        public static final int help = 2;
    }

    public static void setTarget(Param params) {
        params.isLinux = true;
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
                    if (name.getLength() == 0) {
                        /*goto Linvalid*/
                        error(Loc.initial, new BytePtr("invalid file name '%s'"), files.get(i));
                        fatal();
                    }
                }
                Identifier id = Identifier.idPool(name);
                dmodule.Module m = new dmodule.Module(toDString(files.get(i)), id, (global.params.doDocComments ? 1 : 0), (global.params.doHdrGeneration ? 1 : 0));
                modules.push(m);
                if (firstmodule)
                {
                    global.params.objfiles.push(m.objfile.toChars());
                    firstmodule = false;
                }
            }
        }
        return modules;
    }

}
