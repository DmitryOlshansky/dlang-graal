package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.compiler.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmacro.*;
import static org.dlang.dmd.doc.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.visitor.*;

public class dmodule {
    static int __ctorpackageTag;

    private static class Endian 
    {
        public static final int little = 0;
        public static final int big = 1;
    }


    private static class SourceEncoding 
    {
        public static final int utf16 = 0;
        public static final int utf32 = 1;
    }

    static int runDeferredSemanticnested;

    public static ByteSlice lookForSourceFile(ByteSlice filename) {
        ByteSlice sdi = FileName.forceExt(filename, toByteSlice(global.hdr_ext)).copy();
        if ((FileName.exists(sdi) == 1))
            return sdi;
        try {
            ByteSlice sd = FileName.forceExt(filename, toByteSlice(global.mars_ext)).copy();
            if ((FileName.exists(sd) == 1))
                return sd;
            try {
                if ((FileName.exists(filename) == 2))
                {
                    ByteSlice ni = FileName.combine(filename, new ByteSlice("package.di")).copy();
                    if ((FileName.exists(ni) == 1))
                        return ni;
                    FileName.free(toBytePtr(ni));
                    ByteSlice n = FileName.combine(filename, new ByteSlice("package.d")).copy();
                    if ((FileName.exists(n) == 1))
                        return n;
                    FileName.free(toBytePtr(n));
                }
                if (FileName.absolute(filename))
                    return new ByteSlice();
                if (global.path == null)
                    return new ByteSlice();
                {
                    int i = 0;
                    for (; (i < (global.path).length);i++){
                        ByteSlice p = toDString((global.path).get(i)).copy();
                        ByteSlice n = FileName.combine(p, sdi).copy();
                        if ((FileName.exists(n) == 1))
                        {
                            return n;
                        }
                        FileName.free(toBytePtr(n));
                        n = FileName.combine(p, sd).copy();
                        if ((FileName.exists(n) == 1))
                        {
                            return n;
                        }
                        FileName.free(toBytePtr(n));
                        ByteSlice b = FileName.removeExt(filename).copy();
                        n = FileName.combine(p, b).copy();
                        FileName.free(toBytePtr(b));
                        if ((FileName.exists(n) == 2))
                        {
                            ByteSlice n2i = FileName.combine(n, new ByteSlice("package.di")).copy();
                            if ((FileName.exists(n2i) == 1))
                                return n2i;
                            FileName.free(toBytePtr(n2i));
                            ByteSlice n2 = FileName.combine(n, new ByteSlice("package.d")).copy();
                            if ((FileName.exists(n2) == 1))
                            {
                                return n2;
                            }
                            FileName.free(toBytePtr(n2));
                        }
                        FileName.free(toBytePtr(n));
                    }
                }
                return new ByteSlice();
            }
            finally {
                FileName.free(toBytePtr(sd));
            }
        }
        finally {
            FileName.free(toBytePtr(sdi));
        }
    }

    public static void semantic3OnDependencies(Module m) {
        if (m == null)
            return ;
        if ((m.semanticRun > PASS.semantic3))
            return ;
        semantic3(m, null);
        {
            int __key1018 = 1;
            int __limit1019 = m.aimports.length;
            for (; (__key1018 < __limit1019);__key1018 += 1) {
                int i = __key1018;
                semantic3OnDependencies(m.aimports.get(i));
            }
        }
    }

    public static ByteSlice getFilename(DArray<Identifier> packages, Identifier ident) {
        ByteSlice filename = ident.asString().copy();
        if ((packages == null) || ((packages).length == 0))
            return filename;
        OutBuffer buf = new OutBuffer();
        try {
            OutBuffer dotmods = new OutBuffer();
            try {
                Ref<DArray<BytePtr>> modAliases = ref(global.params.modFileAliasStrings);
                Function1<ByteSlice,Void> checkModFileAlias = new Function1<ByteSlice,Void>(){
                    public Void invoke(ByteSlice p){
                        dotmods.writestring(p);
                        {
                            Slice<BytePtr> __r1020 = (modAliases.value).opSlice().copy();
                            int __key1021 = __r1020.getLength();
                            for (; __key1021-- != 0;) {
                                BytePtr m = pcopy(__r1020.get(__key1021));
                                BytePtr q = pcopy(strchr(m, 61));
                                assert(q != null);
                                if ((dotmods.offset == ((q.minus(m)))) && (memcmp(dotmods.peekChars(), m, ((q.minus(m)))) == 0))
                                {
                                    buf.reset();
                                    ByteSlice rhs = q.slice(1,strlen(q)).copy();
                                    if ((rhs.getLength() > 0) && ((rhs.get(rhs.getLength() - 1) & 0xFF) == 47) || ((rhs.get(rhs.getLength() - 1) & 0xFF) == 92))
                                        rhs = rhs.slice(0,rhs.getLength() - 1).copy();
                                    buf.writestring(rhs);
                                    break;
                                }
                            }
                        }
                        dotmods.writeByte(46);
                    }
                };
                {
                    Slice<Identifier> __r1022 = (packages).opSlice().copy();
                    int __key1023 = 0;
                    for (; (__key1023 < __r1022.getLength());__key1023 += 1) {
                        Identifier pid = __r1022.get(__key1023);
                        ByteSlice p = pid.asString().copy();
                        buf.writestring(p);
                        if ((modAliases.value).length != 0)
                            checkModFileAlias.invoke(p);
                        byte FileSeparator = (byte)47;
                        buf.writeByte(47);
                    }
                }
                buf.writestring(filename);
                if ((modAliases.value).length != 0)
                    checkModFileAlias.invoke(filename);
                buf.writeByte(0);
                filename = buf.extractSlice().slice(0,buf.extractSlice().getLength() - 1).copy();
                return filename;
            }
            finally {
            }
        }
        finally {
        }
    }


    public static class PKG 
    {
        public static final int unknown = 0;
        public static final int module_ = 1;
        public static final int package_ = 2;
    }

    public static class Package extends ScopeDsymbol
    {
        public int isPkgMod = PKG.unknown;
        public int tag;
        public Module mod;
        public  Package(Loc loc, Identifier ident) {
            super(loc, ident);
            this.tag = dmodule.__ctorpackageTag++;
        }

        public  BytePtr kind() {
            return new BytePtr("package");
        }

        public static DsymbolTable resolve(DArray<Identifier> packages, Ptr<Dsymbol> pparent, Ptr<Package> ppkg) {
            DsymbolTable dst = Module.modules;
            Dsymbol parent = null;
            if (ppkg != null)
                ppkg.set(0, null);
            if (packages != null)
            {
                {
                    int i = 0;
                    for (; (i < (packages).length);i++){
                        Identifier pid = (packages).get(i);
                        Package pkg = null;
                        Dsymbol p = dst.lookup(pid);
                        if (p == null)
                        {
                            pkg = new Package(Loc.initial, pid);
                            dst.insert((Dsymbol)pkg);
                            pkg.parent = parent;
                            pkg.symtab = new DsymbolTable();
                        }
                        else
                        {
                            pkg = p.isPackage();
                            assert(pkg != null);
                            if (pkg.symtab == null)
                                pkg.symtab = new DsymbolTable();
                        }
                        parent = pkg;
                        dst = pkg.symtab;
                        if ((ppkg != null) && (ppkg.get() == null))
                            ppkg.set(0, pkg);
                        if (pkg.isModule() != null)
                        {
                            if (ppkg != null)
                                ppkg.set(0, ((Package)p));
                            break;
                        }
                    }
                }
            }
            if (pparent != null)
                pparent.set(0, parent);
            return dst;
        }

        public  Package isPackage() {
            return this;
        }

        public  boolean isAncestorPackageOf(Package pkg) {
            if ((pequals(this, pkg)))
                return true;
            if ((pkg == null) || (pkg.parent == null))
                return false;
            return this.isAncestorPackageOf(pkg.parent.isPackage());
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            flags &= -9;
            if ((this.isModule() == null) && (this.mod != null))
            {
                Dsymbol s = this.symtab != null ? this.symtab.lookup(ident) : null;
                if (s != null)
                    return s;
                return this.mod.search(loc, ident, flags);
            }
            return this.search(loc, ident, flags);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  Module isPackageMod() {
            if ((this.isPkgMod == PKG.module_))
            {
                return this.mod;
            }
            return null;
        }

        public  void resolvePKGunknown() {
            if (this.isModule() != null)
                return ;
            if ((this.isPkgMod != PKG.unknown))
                return ;
            DArray<Identifier> packages = new DArray<Identifier>();
            try {
                {
                    Dsymbol s = this.parent;
                    for (; s != null;s = s.parent) {
                        packages.insert(0, s.ident);
                    }
                }
                if (lookForSourceFile(getFilename(packages, this.ident)).getLength() != 0)
                    Module.load(new Loc(null, 0, 0), packages, this.ident);
                else
                    this.isPkgMod = PKG.package_;
            }
            finally {
            }
        }


        public Package() {}

        public Package copy() {
            Package that = new Package();
            that.isPkgMod = this.isPkgMod;
            that.tag = this.tag;
            that.mod = this.mod;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class Module extends Package
    {
        public static Module rootModule;
        public static DsymbolTable modules;
        public static DArray<Module> amodules = new DArray<Module>();
        public static DArray<Dsymbol> deferred = new DArray<Dsymbol>();
        public static DArray<Dsymbol> deferred2 = new DArray<Dsymbol>();
        public static DArray<Dsymbol> deferred3 = new DArray<Dsymbol>();
        public static int dprogress;
        public static void _init() {
            modules = new DsymbolTable();
        }

        public static void deinitialize() {
            modules = null;
        }

        public static AggregateDeclaration moduleinfo;
        public ByteSlice arg;
        public ModuleDeclaration md;
        public FileName srcfile = new FileName();
        public FileName objfile = new FileName();
        public FileName hdrfile = new FileName();
        public FileName docfile = new FileName();
        public FileBuffer srcBuffer;
        public int errors;
        public int numlines;
        public boolean isHdrFile;
        public boolean isDocFile;
        public boolean isPackageFile;
        public DArray<BytePtr> contentImportedFiles = new DArray<BytePtr>();
        public int needmoduleinfo;
        public int selfimports;
        public  boolean selfImports() {
            if ((this.selfimports == 0))
            {
                {
                    int i = 0;
                    for (; (i < amodules.length);i++) {
                        amodules.get(i).insearch = 0;
                    }
                }
                this.selfimports = this.imports(this) + 1;
                {
                    int i = 0;
                    for (; (i < amodules.length);i++) {
                        amodules.get(i).insearch = 0;
                    }
                }
            }
            return this.selfimports == 2;
        }

        public int rootimports;
        public  boolean rootImports() {
            if ((this.rootimports == 0))
            {
                {
                    int i = 0;
                    for (; (i < amodules.length);i++) {
                        amodules.get(i).insearch = 0;
                    }
                }
                this.rootimports = 1;
                {
                    int i = 0;
                    for (; (i < amodules.length);i += 1){
                        Module m = amodules.get(i);
                        if (m.isRoot() && (this.imports(m) != 0))
                        {
                            this.rootimports = 2;
                            break;
                        }
                    }
                }
                {
                    int i = 0;
                    for (; (i < amodules.length);i++) {
                        amodules.get(i).insearch = 0;
                    }
                }
            }
            return this.rootimports == 2;
        }

        public int insearch;
        public Identifier searchCacheIdent;
        public Dsymbol searchCacheSymbol;
        public int searchCacheFlags;
        public Module importedFrom;
        public DArray<Dsymbol> decldefs;
        public DArray<Module> aimports = new DArray<Module>();
        public int debuglevel;
        public DArray<Identifier> debugids;
        public DArray<Identifier> debugidsNot;
        public int versionlevel;
        public DArray<Identifier> versionids;
        public DArray<Identifier> versionidsNot;
        public Macro macrotable;
        public Escape escapetable;
        public int nameoffset;
        public int namelen;
        public  Module(Loc loc, ByteSlice filename, Identifier ident, int doDocComment, int doHdrGen) {
            super(loc, ident);
            ByteSlice srcfilename = new ByteSlice();
            this.arg = filename.copy();
            srcfilename = FileName.defaultExt(filename, toByteSlice(global.mars_ext)).copy();
            if (global.run_noext && global.params.run && (FileName.ext(filename).getLength() == 0) && (FileName.exists(srcfilename) == 0) && (FileName.exists(filename) == 1))
            {
                FileName.free(toBytePtr(srcfilename));
                srcfilename = FileName.removeExt(filename).copy();
            }
            else if (!FileName.equalsExt(srcfilename, toByteSlice(global.mars_ext)) && !FileName.equalsExt(srcfilename, toByteSlice(global.hdr_ext)) && !FileName.equalsExt(srcfilename, new ByteSlice("dd")))
            {
                this.error(new BytePtr("source file name '%.*s' must have .%.*s extension"), srcfilename.getLength(), toBytePtr(srcfilename), global.mars_ext.getLength(), toBytePtr(global.mars_ext));
                fatal();
            }
            this.srcfile = new FileName(srcfilename);
            this.objfile = this.setOutfilename(global.params.objname, global.params.objdir, filename, global.obj_ext).copy();
            if (doDocComment != 0)
                this.setDocfile();
            if (doHdrGen != 0)
                this.hdrfile = this.setOutfilename(global.params.hdrname, global.params.hdrdir, this.arg, toByteSlice(global.hdr_ext)).copy();
            this.escapetable = new Escape(new ByteSlice());
        }

        public  Module(ByteSlice filename, Identifier ident, int doDocComment, int doHdrGen) {
            this(Loc.initial, filename, ident, doDocComment, doHdrGen);
        }

        public static Module create(BytePtr filename, Identifier ident, int doDocComment, int doHdrGen) {
            return create(toDString(filename), ident, doDocComment, doHdrGen);
        }

        public static Module create(ByteSlice filename, Identifier ident, int doDocComment, int doHdrGen) {
            return new Module(Loc.initial, filename, ident, doDocComment, doHdrGen);
        }

        public static Module load(Loc loc, DArray<Identifier> packages, Identifier ident) {
            ByteSlice filename = getFilename(packages, ident).copy();
            {
                ByteSlice result = lookForSourceFile(filename).copy();
                if ((result).getLength() != 0)
                    filename = result.copy();
            }
            Module m = new Module(loc, filename, ident, 0, 0);
            if (!m.read(loc))
                return null;
            if (global.params.verbose)
            {
                OutBuffer buf = new OutBuffer();
                if (packages != null)
                {
                    {
                        Slice<Identifier> __r1024 = (packages).opSlice().copy();
                        int __key1025 = 0;
                        for (; (__key1025 < __r1024.getLength());__key1025 += 1) {
                            Identifier pid = __r1024.get(__key1025);
                            buf.writestring(pid.asString());
                            buf.writeByte(46);
                        }
                    }
                }
                buf.printf(new BytePtr("%s\u0009(%s)"), ident.toChars(), m.srcfile.toChars());
                message(new BytePtr("import    %s"), buf.peekChars());
            }
            m = m.parse();
            if (!m.isRoot() && Compiler.onImport(m))
            {
                m.importedFrom = m;
                assert(m.isRoot());
            }
            Compiler.loadModule(m);
            return m;
        }

        public  BytePtr kind() {
            return new BytePtr("module");
        }

        public  FileName setOutfilename(BytePtr name, BytePtr dir, BytePtr arg, BytePtr ext) {
            return this.setOutfilename(toDString(name), toDString(dir), toDString(arg), toDString(ext));
        }

        public  FileName setOutfilename(ByteSlice name, ByteSlice dir, ByteSlice arg, ByteSlice ext) {
            ByteSlice docfilename = new ByteSlice();
            if (name.getLength() != 0)
            {
                docfilename = name.copy();
            }
            else
            {
                ByteSlice argdoc = new ByteSlice();
                OutBuffer buf = new OutBuffer();
                if (__equals(arg, new ByteSlice("__stdin.d")))
                {
                    buf.printf(new BytePtr("__stdin_%d.d"), getpid());
                    arg = buf.peekSlice().copy();
                }
                if (global.params.preservePaths)
                    argdoc = arg.copy();
                else
                    argdoc = FileName.name(arg).copy();
                if (!FileName.absolute(argdoc))
                {
                    argdoc = FileName.combine(dir, argdoc).copy();
                }
                docfilename = FileName.forceExt(argdoc, ext).copy();
            }
            if (FileName.equals(docfilename, this.srcfile.asString()))
            {
                this.error(new BytePtr("source file and output file have same name '%s'"), this.srcfile.toChars());
                fatal();
            }
            return new FileName(docfilename);
        }

        public  void setDocfile() {
            this.docfile = this.setOutfilename(toDString(global.params.docname), toDString(global.params.docdir), this.arg, toByteSlice(global.doc_ext)).copy();
        }

        public  boolean loadSourceBuffer(Loc loc, File.ReadResult readResult) {
            this.srcBuffer = new FileBuffer(readResult.extractData());
            if (readResult.success)
                return true;
            if (FileName.equals(this.srcfile.asString(), new ByteSlice("object.d")))
            {
                error(loc, new BytePtr("cannot find source code for runtime library file 'object.d'"));
                errorSupplemental(loc, new BytePtr("dmd might not be correctly installed. Run 'dmd -man' for installation instructions."));
                ByteSlice dmdConfFile = global.inifilename.getLength() != 0 ? FileName.canonicalName(global.inifilename) : new ByteSlice("not found").copy();
                errorSupplemental(loc, new BytePtr("config file: %.*s"), dmdConfFile.getLength(), toBytePtr(dmdConfFile));
            }
            else
            {
                boolean isPackageMod = (strcmp(this.toChars(), new BytePtr("package")) != 0) && (strcmp(this.srcfile.name(), new BytePtr("package.d")) == 0) || (strcmp(this.srcfile.name(), new BytePtr("package.di")) == 0);
                if (isPackageMod)
                    error(loc, new BytePtr("importing package '%s' requires a 'package.d' file which cannot be found in '%s'"), this.toChars(), this.srcfile.toChars());
                else
                    this.error(loc, new BytePtr("is in file '%s' which cannot be read"), this.srcfile.toChars());
            }
            if (global.gag == 0)
            {
                if (global.path != null)
                {
                    {
                        Slice<BytePtr> __r1027 = (global.path).opSlice().copy();
                        int __key1026 = 0;
                        for (; (__key1026 < __r1027.getLength());__key1026 += 1) {
                            BytePtr p = pcopy(__r1027.get(__key1026));
                            int i = __key1026;
                            fprintf(stderr, new BytePtr("import path[%llu] = %s\n"), (long)i, p);
                        }
                    }
                }
                else
                    fprintf(stderr, new BytePtr("Specify path to file '%s' with -I switch\n"), this.srcfile.toChars());
                fatal();
            }
            return false;
        }

        public  boolean read(Loc loc) {
            if (this.srcBuffer != null)
                return true;
            File.ReadResult readResult = File.read(this.srcfile.toChars()).copy();
            try {
                return this.loadSourceBuffer(loc, readResult);
            }
            finally {
            }
        }

        public  Module parse() {
            StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
            try {
                return this.parseASTCodegen(diagnosticReporter);
            }
            finally {
            }
        }

        // from template parse!(ASTCodegen)
        public  Module parseASTCodegen(DiagnosticReporter diagnosticReporter) {
            // from template UTF32ToUTF8!(0)
            Function1<ByteSlice,ByteSlice> UTF32ToUTF80 = new Function1<ByteSlice,ByteSlice>(){
                public ByteSlice invoke(ByteSlice buf){
                    if ((buf.getLength() & 3) != 0)
                    {
                        error(new BytePtr("odd length of UTF-32 char source %u"), buf.getLength());
                        fatal();
                    }
                    IntSlice eBuf = __ArrayCast(buf).copy();
                    OutBuffer dbuf = new OutBuffer();
                    try {
                        dbuf.reserve(eBuf.getLength());
                        {
                            int __key1038 = 0;
                            int __limit1039 = eBuf.getLength();
                            for (; (__key1038 < __limit1039);__key1038 += 1) {
                                int i = __key1038;
                                int u = Port.readlongLE0(eBuf.get(i));
                                if ((u & -128) != 0)
                                {
                                    if ((u > 1114111))
                                    {
                                        error(new BytePtr("UTF-32 value %08x greater than 0x10FFFF"), u);
                                        fatal();
                                    }
                                    dbuf.writeUTF8(u);
                                }
                                else
                                    dbuf.writeByte(u);
                            }
                        }
                        dbuf.writeByte(0);
                        return dbuf.extractSlice();
                    }
                    finally {
                    }
                }
            };

            // from template UTF32ToUTF8!(1)
            Function1<ByteSlice,ByteSlice> UTF32ToUTF81 = new Function1<ByteSlice,ByteSlice>(){
                public ByteSlice invoke(ByteSlice buf){
                    if ((buf.getLength() & 3) != 0)
                    {
                        error(new BytePtr("odd length of UTF-32 char source %u"), buf.getLength());
                        fatal();
                    }
                    IntSlice eBuf = __ArrayCast(buf).copy();
                    OutBuffer dbuf = new OutBuffer();
                    try {
                        dbuf.reserve(eBuf.getLength());
                        {
                            int __key1040 = 0;
                            int __limit1041 = eBuf.getLength();
                            for (; (__key1040 < __limit1041);__key1040 += 1) {
                                int i = __key1040;
                                int u = Port.readlongBE1(eBuf.get(i));
                                if ((u & -128) != 0)
                                {
                                    if ((u > 1114111))
                                    {
                                        error(new BytePtr("UTF-32 value %08x greater than 0x10FFFF"), u);
                                        fatal();
                                    }
                                    dbuf.writeUTF8(u);
                                }
                                else
                                    dbuf.writeByte(u);
                            }
                        }
                        dbuf.writeByte(0);
                        return dbuf.extractSlice();
                    }
                    finally {
                    }
                }
            };

            // from template UTF16ToUTF8!(0)
            Function1<ByteSlice,ByteSlice> UTF16ToUTF80 = new Function1<ByteSlice,ByteSlice>(){
                public ByteSlice invoke(ByteSlice buf){
                    if ((buf.getLength() & 1) != 0)
                    {
                        error(new BytePtr("odd length of UTF-16 char source %u"), buf.getLength());
                        fatal();
                    }
                    Slice<Integer> eBuf = __ArrayCast(buf).copy();
                    OutBuffer dbuf = new OutBuffer();
                    try {
                        dbuf.reserve(eBuf.getLength());
                        {
                            int __key1034 = 0;
                            int __limit1035 = eBuf.getLength();
                            for (; (__key1034 < __limit1035);__key1034 += 1) {
                                int u = Port.readwordLE0(eBuf.get(__key1034));
                                if ((u & -128) != 0)
                                {
                                    if ((55296 <= u) && (u < 56320))
                                    {
                                        __key1034++;
                                        if ((__key1034 >= eBuf.getLength()))
                                        {
                                            error(new BytePtr("surrogate UTF-16 high value %04x at end of file"), u);
                                            fatal();
                                        }
                                        int u2 = Port.readwordLE0(eBuf.get(__key1034));
                                        if ((u2 < 56320) || (57344 <= u2))
                                        {
                                            error(new BytePtr("surrogate UTF-16 low value %04x out of range"), u2);
                                            fatal();
                                        }
                                        u = u - 55232 << 10;
                                        u |= u2 - 56320;
                                    }
                                    else if ((u >= 56320) && (u <= 57343))
                                    {
                                        error(new BytePtr("unpaired surrogate UTF-16 value %04x"), u);
                                        fatal();
                                    }
                                    else if ((u == 65534) || (u == 65535))
                                    {
                                        error(new BytePtr("illegal UTF-16 value %04x"), u);
                                        fatal();
                                    }
                                    dbuf.writeUTF8(u);
                                }
                                else
                                    dbuf.writeByte(u);
                            }
                        }
                        dbuf.writeByte(0);
                        return dbuf.extractSlice();
                    }
                    finally {
                    }
                }
            };

            // from template UTF16ToUTF8!(1)
            Function1<ByteSlice,ByteSlice> UTF16ToUTF81 = new Function1<ByteSlice,ByteSlice>(){
                public ByteSlice invoke(ByteSlice buf){
                    if ((buf.getLength() & 1) != 0)
                    {
                        error(new BytePtr("odd length of UTF-16 char source %u"), buf.getLength());
                        fatal();
                    }
                    Slice<Integer> eBuf = __ArrayCast(buf).copy();
                    OutBuffer dbuf = new OutBuffer();
                    try {
                        dbuf.reserve(eBuf.getLength());
                        {
                            int __key1036 = 0;
                            int __limit1037 = eBuf.getLength();
                            for (; (__key1036 < __limit1037);__key1036 += 1) {
                                int u = Port.readwordBE1(eBuf.get(__key1036));
                                if ((u & -128) != 0)
                                {
                                    if ((55296 <= u) && (u < 56320))
                                    {
                                        __key1036++;
                                        if ((__key1036 >= eBuf.getLength()))
                                        {
                                            error(new BytePtr("surrogate UTF-16 high value %04x at end of file"), u);
                                            fatal();
                                        }
                                        int u2 = Port.readwordBE1(eBuf.get(__key1036));
                                        if ((u2 < 56320) || (57344 <= u2))
                                        {
                                            error(new BytePtr("surrogate UTF-16 low value %04x out of range"), u2);
                                            fatal();
                                        }
                                        u = u - 55232 << 10;
                                        u |= u2 - 56320;
                                    }
                                    else if ((u >= 56320) && (u <= 57343))
                                    {
                                        error(new BytePtr("unpaired surrogate UTF-16 value %04x"), u);
                                        fatal();
                                    }
                                    else if ((u == 65534) || (u == 65535))
                                    {
                                        error(new BytePtr("illegal UTF-16 value %04x"), u);
                                        fatal();
                                    }
                                    dbuf.writeUTF8(u);
                                }
                                else
                                    dbuf.writeByte(u);
                            }
                        }
                        dbuf.writeByte(0);
                        return dbuf.extractSlice();
                    }
                    finally {
                    }
                }
            };

            BytePtr srcname = pcopy(this.srcfile.toChars());
            this.isPackageFile = (strcmp(this.srcfile.name(), new BytePtr("package.d")) == 0) || (strcmp(this.srcfile.name(), new BytePtr("package.di")) == 0);
            ByteSlice buf = toByteSlice((this.srcBuffer).data).copy();
            boolean needsReencoding = true;
            boolean hasBOM = true;
            int endian = Endian.little;
            int sourceEncoding = SourceEncoding.utf16;
            if ((buf.getLength() >= 2))
            {
                if (((buf.get(0) & 0xFF) == 255) && ((buf.get(1) & 0xFF) == 254))
                {
                    endian = Endian.little;
                    sourceEncoding = (buf.getLength() >= 4) && ((buf.get(2) & 0xFF) == 0) && ((buf.get(3) & 0xFF) == 0) ? SourceEncoding.utf32 : SourceEncoding.utf16;
                }
                else if (((buf.get(0) & 0xFF) == 254) && ((buf.get(1) & 0xFF) == 255))
                {
                    endian = Endian.big;
                    sourceEncoding = SourceEncoding.utf16;
                }
                else if ((buf.getLength() >= 4) && ((buf.get(0) & 0xFF) == 0) && ((buf.get(1) & 0xFF) == 0) && ((buf.get(2) & 0xFF) == 254) && ((buf.get(3) & 0xFF) == 255))
                {
                    endian = Endian.big;
                    sourceEncoding = SourceEncoding.utf32;
                }
                else if ((buf.getLength() >= 3) && ((buf.get(0) & 0xFF) == 239) && ((buf.get(1) & 0xFF) == 187) && ((buf.get(2) & 0xFF) == 191))
                {
                    needsReencoding = false;
                }
                else
                {
                    hasBOM = false;
                    if ((buf.getLength() >= 4) && ((buf.get(1) & 0xFF) == 0) && ((buf.get(2) & 0xFF) == 0) && ((buf.get(3) & 0xFF) == 0))
                    {
                        endian = Endian.little;
                        sourceEncoding = SourceEncoding.utf32;
                    }
                    else if ((buf.getLength() >= 4) && ((buf.get(0) & 0xFF) == 0) && ((buf.get(1) & 0xFF) == 0) && ((buf.get(2) & 0xFF) == 0))
                    {
                        endian = Endian.big;
                        sourceEncoding = SourceEncoding.utf32;
                    }
                    else if ((buf.getLength() >= 2) && ((buf.get(1) & 0xFF) == 0))
                    {
                        endian = Endian.little;
                        sourceEncoding = SourceEncoding.utf16;
                    }
                    else if (((buf.get(0) & 0xFF) == 0))
                    {
                        endian = Endian.big;
                        sourceEncoding = SourceEncoding.utf16;
                    }
                    else
                    {
                        needsReencoding = false;
                        if (((buf.get(0) & 0xFF) >= 128))
                        {
                            this.error(new BytePtr("source file must start with BOM or ASCII character, not \\x%02X"), (buf.get(0) & 0xFF));
                            fatal();
                        }
                    }
                }
                if (hasBOM)
                {
                    if (!needsReencoding)
                        buf = buf.slice(3,buf.getLength()).copy();
                    else if ((sourceEncoding == SourceEncoding.utf32))
                        buf = buf.slice(4,buf.getLength()).copy();
                    else
                        buf = buf.slice(2,buf.getLength()).copy();
                }
            }
            else if ((buf.getLength() >= 1) && ((buf.get(0) & 0xFF) == 0) || ((buf.get(0) & 0xFF) == 26))
                needsReencoding = false;
            if (needsReencoding)
            {
                if ((sourceEncoding == SourceEncoding.utf16))
                {
                    buf = ((endian == Endian.little) ? UTF16ToUTF80.invoke(buf) : UTF16ToUTF81.invoke(buf)).copy();
                }
                else
                {
                    buf = ((endian == Endian.little) ? UTF32ToUTF80.invoke(buf) : UTF32ToUTF81.invoke(buf)).copy();
                }
            }
            if ((buf.getLength() >= 4) && __equals(buf.slice(0,4), new ByteSlice("Ddoc")))
            {
                this.comment = pcopy((toBytePtr(buf).plus(4)));
                this.isDocFile = true;
                if (!this.docfile.opCast())
                    this.setDocfile();
                return this;
            }
            if (FileName.equalsExt(this.arg, new ByteSlice("dd")))
            {
                this.comment = pcopy((toBytePtr(buf)));
                this.isDocFile = true;
                if (!this.docfile.opCast())
                    this.setDocfile();
                return this;
            }
            if (FileName.equalsExt(this.arg, new ByteSlice("di")))
            {
                this.isHdrFile = true;
            }
            {
                ParserASTCodegen p = new ParserASTCodegen(this, buf, this.docfile.opCast(), diagnosticReporter);
                try {
                    p.nextToken();
                    this.members = p.parseModule();
                    this.md = p.md;
                    this.numlines = p.scanloc.linnum;
                    if (p.errors())
                        global.errors += 1;
                }
                finally {
                }
            }
            destroy(this.srcBuffer);
            this.srcBuffer = null;
            DsymbolTable dst = null;
            if (this.md != null)
            {
                this.ident = (this.md).id;
                Ref<Package> ppack = ref(null);
                dst = Package.resolve((this.md).packages, this.parent, ptr(ppack));
                assert(dst != null);
                Module m = ppack.value != null ? ppack.value.isModule() : null;
                if ((m != null) && (strcmp(m.srcfile.name(), new BytePtr("package.d")) != 0) && (strcmp(m.srcfile.name(), new BytePtr("package.di")) != 0))
                {
                    error((this.md).loc, new BytePtr("package name '%s' conflicts with usage as a module name in file %s"), ppack.value.toPrettyChars(false), m.srcfile.toChars());
                }
            }
            else
            {
                dst = modules;
                if (!Identifier.isValidIdentifier(this.ident.toChars()))
                    this.error(new BytePtr("has non-identifier characters in filename, use module declaration instead"));
            }
            Dsymbol s = this;
            if (this.isPackageFile)
            {
                Package p = new Package(Loc.initial, this.ident);
                p.parent = this.parent;
                p.isPkgMod = PKG.module_;
                p.mod = this;
                p.tag = this.tag;
                p.symtab = new DsymbolTable();
                s = p;
            }
            if (dst.insert(s) == null)
            {
                Dsymbol prev = dst.lookup(this.ident);
                assert(prev != null);
                {
                    Module mprev = prev.isModule();
                    if ((mprev) != null)
                    {
                        if (!FileName.equals(srcname, mprev.srcfile.toChars()))
                            this.error(this.loc, new BytePtr("from file %s conflicts with another module %s from file %s"), srcname, mprev.toChars(), mprev.srcfile.toChars());
                        else if (this.isRoot() && mprev.isRoot())
                            this.error(this.loc, new BytePtr("from file %s is specified twice on the command line"), srcname);
                        else
                            this.error(this.loc, new BytePtr("from file %s must be imported with 'import %s;'"), srcname, this.toPrettyChars(false));
                        return mprev;
                    }
                    else {
                        Package pkg = prev.isPackage();
                        if ((pkg) != null)
                        {
                            if ((pkg.isPkgMod == PKG.unknown) && this.isPackageFile)
                            {
                                pkg.isPkgMod = PKG.module_;
                                pkg.mod = this;
                                pkg.tag = this.tag;
                                amodules.push(this);
                            }
                            else
                                this.error((this.md != null ? (this.md).loc : this.loc), new BytePtr("from file %s conflicts with package name %s"), srcname, pkg.toChars());
                        }
                        else
                            assert(global.errors != 0);
                    }
                }
            }
            else
            {
                amodules.push(this);
            }
            return this;
        }


        public  void importAll(Scope prevsc) {
            if (this._scope != null)
                return ;
            if (this.isDocFile)
            {
                this.error(new BytePtr("is a Ddoc file, cannot import it"));
                return ;
            }
            Scope sc = Scope.createGlobal(this);
            if ((this.md != null) && ((this.md).msg != null))
                (this.md).msg = semanticString(sc, (this.md).msg, new BytePtr("deprecation message"));
            if (((this.members).length == 0) || (!pequals((this.members).get(0).ident, Id.object)) || ((this.members).get(0).isImport() == null))
            {
                Import im = new Import(Loc.initial, null, Id.object, null, 0);
                (this.members).shift(im);
            }
            if (this.symtab == null)
            {
                this.symtab = new DsymbolTable();
                {
                    int i = 0;
                    for (; (i < (this.members).length);i++){
                        Dsymbol s = (this.members).get(i);
                        s.addMember(sc, (sc).scopesym);
                    }
                }
            }
            this.setScope(sc);
            {
                int i = 0;
                for (; (i < (this.members).length);i++){
                    Dsymbol s = (this.members).get(i);
                    s.setScope(sc);
                }
            }
            {
                int i = 0;
                for (; (i < (this.members).length);i++){
                    Dsymbol s = (this.members).get(i);
                    s.importAll(sc);
                }
            }
            sc = (sc).pop();
            (sc).pop();
        }

        public  int needModuleInfo() {
            return (((this.needmoduleinfo != 0) || global.params.cov) ? 1 : 0);
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if (this.insearch != 0)
                return null;
            if ((flags & 32) == 0)
                flags &= -41;
            if ((pequals(this.searchCacheIdent, ident)) && (this.searchCacheFlags == flags))
            {
                return this.searchCacheSymbol;
            }
            int errors = global.errors;
            this.insearch = 1;
            Dsymbol s = this.search(loc, ident, flags);
            this.insearch = 0;
            if ((errors == global.errors))
            {
                this.searchCacheIdent = ident;
                this.searchCacheSymbol = s;
                this.searchCacheFlags = flags;
            }
            return s;
        }

        public  boolean isPackageAccessible(Package p, Prot protection, int flags) {
            if (this.insearch != 0)
                return false;
            this.insearch = 1;
            try {
                if ((flags & 1) != 0)
                    protection = new Prot(Prot.Kind.public_).copy();
                return super.isPackageAccessible(p, protection, 0);
            }
            finally {
                this.insearch = 0;
            }
        }

        public  Dsymbol symtabInsert(Dsymbol s) {
            this.searchCacheIdent = null;
            return this.symtabInsert(s);
        }

        public  void deleteObjFile() {
            if (global.params.obj)
                File.remove(this.objfile.toChars());
            if (this.docfile.opCast())
                File.remove(this.docfile.toChars());
        }

        public static void addDeferredSemantic(Dsymbol s) {
            deferred.push(s);
        }

        public static void addDeferredSemantic2(Dsymbol s) {
            deferred2.push(s);
        }

        public static void addDeferredSemantic3(Dsymbol s) {
            deferred3.push(s);
        }

        public static void runDeferredSemantic() {
            if ((dprogress == 0))
                return ;
            if (dmodule.runDeferredSemanticnested != 0)
                return ;
            dmodule.runDeferredSemanticnested++;
            int len = 0;
            do {
                {
                    dprogress = 0;
                    len = deferred.length;
                    if (len == 0)
                        break;
                    Ptr<Dsymbol> todo = null;
                    Ptr<Dsymbol> todoalloc = null;
                    Ref<Dsymbol> tmp = ref(null);
                    if ((len == 1))
                    {
                        todo = pcopy(ptr(tmp));
                    }
                    else
                    {
                        todo = pcopy((((Ptr<Dsymbol>)malloc(len * 4))));
                        assert(todo != null);
                        todoalloc = pcopy(todo);
                    }
                    memcpy((BytePtr)(todo), (deferred.tdata()), (len * 4));
                    deferred.setDim(0);
                    {
                        int i = 0;
                        for (; (i < len);i++){
                            Dsymbol s = todo.get(i);
                            dsymbolSemantic(s, null);
                        }
                    }
                    if (todoalloc != null)
                        free(todoalloc);
                }
            } while ((deferred.length < len) || (dprogress != 0));
            dmodule.runDeferredSemanticnested--;
        }

        public static void runDeferredSemantic2() {
            runDeferredSemantic();
            DArray<Dsymbol> a = deferred2;
            {
                int i = 0;
                for (; (i < (a).length);i++){
                    Dsymbol s = (a).get(i);
                    semantic2(s, null);
                    if (global.errors != 0)
                        break;
                }
            }
            (a).setDim(0);
        }

        public static void runDeferredSemantic3() {
            runDeferredSemantic2();
            DArray<Dsymbol> a = deferred3;
            {
                int i = 0;
                for (; (i < (a).length);i++){
                    Dsymbol s = (a).get(i);
                    semantic3(s, null);
                    if (global.errors != 0)
                        break;
                }
            }
            (a).setDim(0);
        }

        public static void clearCache() {
            {
                int i = 0;
                for (; (i < amodules.length);i++){
                    Module m = amodules.get(i);
                    m.searchCacheIdent = null;
                }
            }
        }

        public  int imports(Module m) {
            {
                int i = 0;
                for (; (i < this.aimports.length);i++){
                    Module mi = this.aimports.get(i);
                    if ((pequals(mi, m)))
                        return 1;
                    if (mi.insearch == 0)
                    {
                        mi.insearch = 1;
                        int r = mi.imports(m);
                        if (r != 0)
                            return r;
                    }
                }
            }
            return 0;
        }

        public  boolean isRoot() {
            return pequals(this.importedFrom, this);
        }

        public  boolean isCoreModule(Identifier ident) {
            return (pequals(this.ident, ident)) && (this.parent != null) && (pequals(this.parent.ident, Id.core)) && (this.parent.parent == null);
        }

        public int doppelganger;
        public Symbol cov;
        public IntPtr covb;
        public Symbol sictor;
        public Symbol sctor;
        public Symbol sdtor;
        public Symbol ssharedctor;
        public Symbol sshareddtor;
        public Symbol stest;
        public Symbol sfilename;
        public  Module isModule() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  void fullyQualifiedName(OutBuffer buf) {
            buf.writestring(this.ident.asString());
            {
                Dsymbol package_ = this.parent;
                for (; (package_ != null);package_ = package_.parent){
                    buf.prependstring(new BytePtr("."));
                    buf.prependstring(package_.ident.toChars());
                }
            }
        }


        public Module() {}

        public Module copy() {
            Module that = new Module();
            that.arg = this.arg;
            that.md = this.md;
            that.srcfile = this.srcfile;
            that.objfile = this.objfile;
            that.hdrfile = this.hdrfile;
            that.docfile = this.docfile;
            that.srcBuffer = this.srcBuffer;
            that.errors = this.errors;
            that.numlines = this.numlines;
            that.isHdrFile = this.isHdrFile;
            that.isDocFile = this.isDocFile;
            that.isPackageFile = this.isPackageFile;
            that.contentImportedFiles = this.contentImportedFiles;
            that.needmoduleinfo = this.needmoduleinfo;
            that.selfimports = this.selfimports;
            that.rootimports = this.rootimports;
            that.insearch = this.insearch;
            that.searchCacheIdent = this.searchCacheIdent;
            that.searchCacheSymbol = this.searchCacheSymbol;
            that.searchCacheFlags = this.searchCacheFlags;
            that.importedFrom = this.importedFrom;
            that.decldefs = this.decldefs;
            that.aimports = this.aimports;
            that.debuglevel = this.debuglevel;
            that.debugids = this.debugids;
            that.debugidsNot = this.debugidsNot;
            that.versionlevel = this.versionlevel;
            that.versionids = this.versionids;
            that.versionidsNot = this.versionidsNot;
            that.macrotable = this.macrotable;
            that.escapetable = this.escapetable;
            that.nameoffset = this.nameoffset;
            that.namelen = this.namelen;
            that.doppelganger = this.doppelganger;
            that.cov = this.cov;
            that.covb = this.covb;
            that.sictor = this.sictor;
            that.sctor = this.sctor;
            that.sdtor = this.sdtor;
            that.ssharedctor = this.ssharedctor;
            that.sshareddtor = this.sshareddtor;
            that.stest = this.stest;
            that.sfilename = this.sfilename;
            that.isPkgMod = this.isPkgMod;
            that.tag = this.tag;
            that.mod = this.mod;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class ModuleDeclaration
    {
        public Loc loc = new Loc();
        public Identifier id;
        public DArray<Identifier> packages;
        public boolean isdeprecated;
        public Expression msg;
        public  ModuleDeclaration(Loc loc, DArray<Identifier> packages, Identifier id, Expression msg, boolean isdeprecated) {
            this.loc = loc.copy();
            this.packages = packages;
            this.id = id;
            this.msg = msg;
            this.isdeprecated = isdeprecated;
        }

        public  BytePtr toChars() {
            OutBuffer buf = new OutBuffer();
            try {
                if ((this.packages != null) && ((this.packages).length != 0))
                {
                    {
                        Slice<Identifier> __r1042 = (this.packages).opSlice().copy();
                        int __key1043 = 0;
                        for (; (__key1043 < __r1042.getLength());__key1043 += 1) {
                            Identifier pid = __r1042.get(__key1043);
                            buf.writestring(pid.asString());
                            buf.writeByte(46);
                        }
                    }
                }
                buf.writestring(this.id.asString());
                return buf.extractChars();
            }
            finally {
            }
        }

        public  ByteSlice asString() {
            return toDString(this.toChars());
        }

        public ModuleDeclaration(){
            loc = new Loc();
        }
        public ModuleDeclaration copy(){
            ModuleDeclaration r = new ModuleDeclaration();
            r.loc = loc.copy();
            r.id = id;
            r.packages = packages;
            r.isdeprecated = isdeprecated;
            r.msg = msg;
            return r;
        }
        public ModuleDeclaration opAssign(ModuleDeclaration that) {
            this.loc = that.loc;
            this.id = that.id;
            this.packages = that.packages;
            this.isdeprecated = that.isdeprecated;
            this.msg = that.msg;
            return this;
        }
    }
}
