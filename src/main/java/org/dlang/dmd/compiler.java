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
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.tokens.*;

public class compiler {
    private static class U
    {
        private int int32value;
        private long int64value;
        private float float32value;
        private double float64value;
        public U(){
        }
        public U copy(){
            U r = new U();
            r.int32value = int32value;
            r.int64value = int64value;
            r.float32value = float32value;
            r.float64value = float64value;
            return r;
        }
        public U opAssign(U that) {
            this.int32value = that.int32value;
            this.int64value = that.int64value;
            this.float32value = that.float32value;
            this.float64value = that.float64value;
            return this;
        }
    }

    static dmodule.Module entrypoint = null;
    static dmodule.Module rootHasMain = null;
    static boolean includeImports = false;
    static DArray<BytePtr> includeModulePatterns = new DArray<BytePtr>();
    static DArray<dmodule.Module> compiledImports = new DArray<dmodule.Module>();
    public static class Compiler
    {
        public static void genCmain(Scope sc) {
            if (entrypoint != null)
                return ;
            ByteSlice cmaincode = new ByteSlice("\n            extern(C)\n            {\n                int _d_run_main(int argc, char **argv, void* mainFunc);\n                int _Dmain(char[][] args);\n                int main(int argc, char **argv)\n                {\n                    return _d_run_main(argc, argv, &_Dmain);\n                }\n                version (Solaris) int _main(int argc, char** argv) { return main(argc, argv); }\n            }\n        ").copy();
            Identifier id = Id.entrypoint;
            dmodule.Module m = new dmodule.Module(new ByteSlice("__entrypoint.d"), id, 0, 0);
            StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
            try {
                ParserASTCodegen p = new ParserASTCodegen(m, toByteSlice(cmaincode), false, diagnosticReporter);
                try {
                    p.scanloc = Loc.initial.copy();
                    p.nextToken();
                    m.members = p.parseModule();
                    assert(((p.token.value & 0xFF) == 11));
                    assert(!p.errors());
                    boolean v = global.params.verbose;
                    expr(global.params.verbose = false);
                    m.importedFrom = m;
                    m.importAll(null);
                    dsymbolSemantic(m, null);
                    semantic2(m, null);
                    semantic3(m, null);
                    expr(global.params.verbose = v);
                    entrypoint = m;
                    rootHasMain = (sc)._module;
                }
                finally {
                }
            }
            finally {
            }
        }

        public static Expression paintAsType(UnionExp pue, Expression e, Type type) {
            U u = null;
            assert((e.type.size() == type.size()));
            {
                int __dispatch0 = 0;
                dispatched_0:
                do {
                    switch (__dispatch0 != 0 ? __dispatch0 : (e.type.ty & 0xFF))
                    {
                        case 17:
                        case 18:
                            u.int32value = (int)e.toInteger();
                            break;
                        case 19:
                        case 20:
                            u.int64value = (long)e.toInteger();
                            break;
                        case 21:
                            u.float32value = (float)e.toReal();
                            break;
                        case 22:
                            __dispatch0 = 0;
                            u.float64value = (double)e.toReal();
                            break;
                        case 23:
                            assert((e.type.size() == 8L));
                            /*goto case*/{ __dispatch0 = 22; continue dispatched_0; }
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                } while(__dispatch0 != 0);
            }
            double r = null;
            {
                int __dispatch1 = 0;
                dispatched_1:
                do {
                    switch (__dispatch1 != 0 ? __dispatch1 : (type.ty & 0xFF))
                    {
                        case 17:
                        case 18:
                            emplaceExpIntegerExpLocIntegerType(pue, e.loc, u.int32value, type);
                            break;
                        case 19:
                        case 20:
                            emplaceExpIntegerExpLocLongType(pue, e.loc, u.int64value, type);
                            break;
                        case 21:
                            r = (double)u.float32value;
                            emplaceExpRealExpLocDoubleType(pue, e.loc, r, type);
                            break;
                        case 22:
                            __dispatch1 = 0;
                            r = (double)u.float64value;
                            emplaceExpRealExpLocDoubleType(pue, e.loc, r, type);
                            break;
                        case 23:
                            assert((type.size() == 8L));
                            /*goto case*/{ __dispatch1 = 22; continue dispatched_1; }
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                } while(__dispatch1 != 0);
            }
            return (pue).exp();
        }

        public static void loadModule(dmodule.Module m) {
        }

        public static boolean onImport(dmodule.Module m) {
            if (includeImports)
            {
                DArray<Identifier> empty = new DArray<Identifier>();
                try {
                    if (includeImportedModuleCheck(new ModuleComponentRange((m.md != null) && ((m.md).packages != null) ? (m.md).packages : empty, m.ident, m.isPackageFile, 0)))
                    {
                        if (global.params.verbose)
                            message(new BytePtr("compileimport (%s)"), m.srcfile.toChars());
                        compiledImports.push(m);
                        return true;
                    }
                }
                finally {
                }
            }
            return false;
        }

        public Compiler(){
        }
        public Compiler copy(){
            Compiler r = new Compiler();
            return r;
        }
        public Compiler opAssign(Compiler that) {
            return this;
        }
    }
    public static class ModuleComponentRange
    {
        public DArray<Identifier> packages;
        public Identifier name;
        public boolean isPackageFile;
        public int index;
        public  int totalLength() {
            return (this.packages).length + 1 + (this.isPackageFile ? 1 : 0);
        }

        public  boolean empty() {
            return this.index >= this.totalLength();
        }

        public  Identifier front() {
            if ((this.index < (this.packages).length))
                return (this.packages).get(this.index);
            if ((this.index == (this.packages).length))
                return this.name;
            else
                return Identifier.idPool(new ByteSlice("package"));
        }

        public  void popFront() {
            this.index++;
        }

        public ModuleComponentRange(){
        }
        public ModuleComponentRange copy(){
            ModuleComponentRange r = new ModuleComponentRange();
            r.packages = packages;
            r.name = name;
            r.isPackageFile = isPackageFile;
            r.index = index;
            return r;
        }
        public ModuleComponentRange(DArray<Identifier> packages, Identifier name, boolean isPackageFile, int index) {
            this.packages = packages;
            this.name = name;
            this.isPackageFile = isPackageFile;
            this.index = index;
        }

        public ModuleComponentRange opAssign(ModuleComponentRange that) {
            this.packages = that.packages;
            this.name = that.name;
            this.isPackageFile = that.isPackageFile;
            this.index = that.index;
            return this;
        }
    }
    public static boolean includeImportedModuleCheck(ModuleComponentRange components) {
        {
            {
                assert(includeImports);
            }
        }
        createMatchNodes();
        int nodeIndex = 0;
        for (; (nodeIndex < matchNodes.length);){
            MatcherNode info = matchNodes.get(nodeIndex++).copy();
            if (((int)info.depth <= components.totalLength()))
            {
                int nodeOffset = 0;
                {
                    ModuleComponentRange range = components.copy();
                    for (; ;range.popFront()){
                        if (range.empty() || (nodeOffset >= (int)info.depth))
                        {
                            return !info.isExclude;
                        }
                        if (!(range.front() == matchNodes.get(nodeIndex + nodeOffset).id))
                        {
                            break;
                        }
                        nodeOffset++;
                    }
                }
            }
            nodeIndex += (int)info.depth;
        }
        assertMsg((nodeIndex == matchNodes.length), new ByteSlice("code bug"));
        return includeByDefault;
    }

    public static class MatcherNode
    {
        public int depth;
        public boolean isExclude;
        public Identifier id;
        public  MatcherNode(Identifier id) {
            this.id = id;
        }

        public  MatcherNode(boolean isExclude, int depth) {
            this.depth = depth;
            expr(this.isExclude = isExclude);
        }

        public MatcherNode(){
        }
        public MatcherNode copy(){
            MatcherNode r = new MatcherNode();
            r.depth = depth;
            r.isExclude = isExclude;
            r.id = id;
            return r;
        }
        public MatcherNode opAssign(MatcherNode that) {
            this.depth = that.depth;
            this.isExclude = that.isExclude;
            this.id = that.id;
            return this;
        }
    }
    static boolean includeByDefault = true;
    static DArray<MatcherNode> matchNodes = new DArray<MatcherNode>();
    public static void createMatchNodes() {
        Function1<Integer,Integer> findSortedIndexToAddForDepth = new Function1<Integer,Integer>(){
            public Integer invoke(Integer depth){
                int index = 0;
                for (; (index < matchNodes.length);){
                    MatcherNode info = matchNodes.get(index).copy();
                    if ((depth > (int)info.depth))
                        break;
                    index += (1 + (int)info.depth);
                }
                return index;
            }
        };
        if ((matchNodes.length == 0))
        {
            {
                Slice<BytePtr> __r837 = includeModulePatterns.opSlice().copy();
                int __key838 = 0;
                for (; (__key838 < __r837.getLength());__key838 += 1) {
                    BytePtr modulePattern = pcopy(__r837.get(__key838));
                    int depth = parseModulePatternDepth(modulePattern);
                    int entryIndex = findSortedIndexToAddForDepth.invoke((int)depth);
                    split(matchNodes, entryIndex, ((int)depth + 1));
                    parseModulePattern(modulePattern, matchNodes.get(entryIndex), depth);
                    if (includeByDefault && !matchNodes.get(entryIndex).isExclude)
                    {
                        expr(includeByDefault = false);
                    }
                }
            }
            Slice<MatcherNode> defaultDepth1MatchNodes = slice(new MatcherNode[]{new MatcherNode(true, (int)1), new MatcherNode(Id.std), new MatcherNode(true, (int)1), new MatcherNode(Id.core), new MatcherNode(true, (int)1), new MatcherNode(Id.etc), new MatcherNode(true, (int)1), new MatcherNode(Id.object)});
            {
                int index = findSortedIndexToAddForDepth.invoke(1);
                split(matchNodes, index, 8);
                matchNodes.data.slice(index,index + 8) = defaultDepth1MatchNodes.copy();
            }
        }
    }

    public static int parseModulePatternDepth(BytePtr modulePattern) {
        if (((modulePattern.get(0) & 0xFF) == 45))
            modulePattern.postInc();
        if (((modulePattern.get(0) & 0xFF) == 46) && ((modulePattern.get(1) & 0xFF) == 0))
            return (int)0;
        int depth = (int)1;
        for (; ;modulePattern.postInc()){
            byte c = modulePattern.get();
            if (((c & 0xFF) == 46))
                depth++;
            if (((c & 0xFF) == 0))
                return depth;
        }
    }

    public static void parseModulePattern(BytePtr modulePattern, MatcherNode dst, int depth) {
        boolean isExclude = false;
        if (((modulePattern.get(0) & 0xFF) == 45))
        {
            expr(isExclude = true);
            modulePattern.postInc();
        }
        dst.opAssign(new MatcherNode(isExclude, depth));
        dst.postInc();
        if (((int)depth > 0))
        {
            BytePtr idStart = pcopy(modulePattern);
            MatcherNode lastNode = dst.plus((int)depth * 4).minus(4);
            for (; (dst.lessThan(lastNode));dst.postInc()){
                for (; ;modulePattern.postInc()){
                    if (((modulePattern.get() & 0xFF) == 46))
                    {
                        assertMsg((modulePattern.greaterThan(idStart)), new ByteSlice("empty module pattern"));
                        dst.opAssign(new MatcherNode(Identifier.idPool(idStart, ((modulePattern.minus(idStart))))));
                        modulePattern.postInc();
                        idStart = pcopy(modulePattern);
                        break;
                    }
                }
            }
            for (; ;modulePattern.postInc()){
                if (((modulePattern.get() & 0xFF) == 0))
                {
                    assertMsg((modulePattern.greaterThan(idStart)), new ByteSlice("empty module pattern"));
                    lastNode.opAssign(new MatcherNode(Identifier.idPool(idStart, ((modulePattern.minus(idStart))))));
                    break;
                }
            }
        }
    }

}
