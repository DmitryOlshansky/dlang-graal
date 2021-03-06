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
        private int int32value = 0;
        private long int64value = 0L;
        private float float32value = 0.0;
        private double float64value = 0.0;
        public U(){ }
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
        // Erasure: genCmain<Ptr>
        public static void genCmain(Ptr<Scope> sc) {
            if (entrypoint != null)
            {
                return ;
            }
            ByteSlice cmaincode = new ByteSlice("\n            extern(C)\n            {\n                int _d_run_main(int argc, char **argv, void* mainFunc);\n                int _Dmain(char[][] args);\n                int main(int argc, char **argv)\n                {\n                    return _d_run_main(argc, argv, &_Dmain);\n                }\n                version (Solaris) int _main(int argc, char** argv) { return main(argc, argv); }\n            }\n        ").copy();
            Identifier id = Id.entrypoint;
            dmodule.Module m = new dmodule.Module(new ByteSlice("__entrypoint.d"), id, 0, 0);
            StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
            try {
                ParserASTCodegen p = new ParserASTCodegen(m, toByteSlice(cmaincode), false, diagnosticReporter);
                try {
                    p.scanloc.value.opAssign(Loc.initial.copy());
                    p.nextToken();
                    m.members = pcopy(p.parseModule());
                    assert(((p.token.value.value & 0xFF) == 11));
                    assert(!p.errors());
                    boolean v = global.params.verbose;
                    global.params.verbose = false;
                    m.importedFrom = m;
                    m.importAll(null);
                    dsymbolSemantic(m, null);
                    semantic2(m, null);
                    semantic3(m, null);
                    global.params.verbose = v;
                    entrypoint = m;
                    rootHasMain = (sc.get())._module;
                }
                finally {
                }
            }
            finally {
            }
        }

        // Erasure: paintAsType<Ptr, Expression, Type>
        public static Expression paintAsType(Ptr<UnionExp> pue, Expression e, Type type) {
            U u = null;
            assert((e.type.value.size() == type.size()));
            {
                int __dispatch0 = 0;
                dispatched_0:
                do {
                    switch (__dispatch0 != 0 ? __dispatch0 : (e.type.value.ty & 0xFF))
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
                            assert((e.type.value.size() == 8L));
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
                            (pue) = new UnionExp(new IntegerExp(e.loc, u.int32value, type));
                            break;
                        case 19:
                        case 20:
                            (pue) = new UnionExp(new IntegerExp(e.loc, u.int64value, type));
                            break;
                        case 21:
                            r = (double)u.float32value;
                            (pue) = new UnionExp(new RealExp(e.loc, r, type));
                            break;
                        case 22:
                            __dispatch1 = 0;
                            r = (double)u.float64value;
                            (pue) = new UnionExp(new RealExp(e.loc, r, type));
                            break;
                        case 23:
                            assert((type.size() == 8L));
                            /*goto case*/{ __dispatch1 = 22; continue dispatched_1; }
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                } while(__dispatch1 != 0);
            }
            return (pue.get()).exp();
        }

        // Erasure: loadModule<Module>
        public static void loadModule(dmodule.Module m) {
        }

        // Erasure: onImport<Module>
        public static boolean onImport(dmodule.Module m) {
            if (includeImports)
            {
                Ref<DArray<Identifier>> empty = ref(new DArray<Identifier>());
                try {
                    if (includeImportedModuleCheck(new ModuleComponentRange((m.md != null) && ((m.md.get()).packages != null) ? (m.md.get()).packages : empty.value, m.ident, m.isPackageFile, 0)))
                    {
                        if (global.params.verbose)
                        {
                            message(new BytePtr("compileimport (%s)"), m.srcfile.toChars());
                        }
                        compiledImports.push(m);
                        return true;
                    }
                }
                finally {
                }
            }
            return false;
        }

        public Compiler(){ }
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
        public DArray<Identifier> packages = null;
        public Identifier name = null;
        public boolean isPackageFile = false;
        public int index = 0;
        // Erasure: totalLength<>
        public  int totalLength() {
            return (this.packages).length + 1 + (this.isPackageFile ? 1 : 0);
        }

        // Erasure: empty<>
        public  boolean empty() {
            return this.index >= this.totalLength();
        }

        // Erasure: front<>
        public  Identifier front() {
            if ((this.index < (this.packages).length))
            {
                return (this.packages).get(this.index);
            }
            if ((this.index == (this.packages).length))
            {
                return this.name;
            }
            else
            {
                return Identifier.idPool(new ByteSlice("package"));
            }
        }

        // Erasure: popFront<>
        public  void popFront() {
            this.index++;
        }

        public ModuleComponentRange(){ }
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
    // Erasure: includeImportedModuleCheck<ModuleComponentRange>
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
        public int depth = 0;
        public boolean isExclude = false;
        public Identifier id = null;
        // Erasure: __ctor<Identifier>
        public  MatcherNode(Identifier id) {
            this.id = id;
        }

        // Erasure: __ctor<boolean, int>
        public  MatcherNode(boolean isExclude, int depth) {
            this.depth = depth;
            this.isExclude = isExclude;
        }

        public MatcherNode(){ }
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
    // Erasure: createMatchNodes<>
    public static void createMatchNodes() {
        Function1<Integer,Integer> findSortedIndexToAddForDepth = new Function1<Integer,Integer>() {
            public Integer invoke(Integer depth) {
             {
                Ref<Integer> index = ref(0);
                for (; (index.value < matchNodes.value.length);){
                    MatcherNode info = matchNodes.value.get(index.value).copy();
                    if ((depth > (int)info.depth))
                    {
                        break;
                    }
                    index.value += (1 + (int)info.depth);
                }
                return index.value;
            }}

        };
        if ((matchNodes.value.length == 0))
        {
            {
                Slice<BytePtr> __r833 = includeModulePatterns.opSlice().copy();
                int __key834 = 0;
                for (; (__key834 < __r833.getLength());__key834 += 1) {
                    BytePtr modulePattern = pcopy(__r833.get(__key834));
                    int depth = parseModulePatternDepth(modulePattern);
                    int entryIndex = findSortedIndexToAddForDepth.invoke((int)depth);
                    split(matchNodes, entryIndex, ((int)depth + 1));
                    parseModulePattern(modulePattern, ptr(matchNodes.value.get(entryIndex)), depth);
                    if (includeByDefault && !matchNodes.value.get(entryIndex).isExclude)
                    {
                        includeByDefault = false;
                    }
                }
            }
            Slice<MatcherNode> defaultDepth1MatchNodes = slice(new MatcherNode[]{new MatcherNode(true, (int)1), new MatcherNode(Id.std), new MatcherNode(true, (int)1), new MatcherNode(Id.core), new MatcherNode(true, (int)1), new MatcherNode(Id.etc), new MatcherNode(true, (int)1), new MatcherNode(Id.object)});
            {
                int index = findSortedIndexToAddForDepth.invoke(1);
                split(matchNodes, index, 8);
                matchNodes.value.data.slice(index,index + 8) = defaultDepth1MatchNodes.copy();
            }
        }
    }

    // Erasure: parseModulePatternDepth<Ptr>
    public static int parseModulePatternDepth(BytePtr modulePattern) {
        if (((modulePattern.get(0) & 0xFF) == 45))
        {
            modulePattern.postInc();
        }
        if (((modulePattern.get(0) & 0xFF) == 46) && ((modulePattern.get(1) & 0xFF) == 0))
        {
            return (int)0;
        }
        int depth = (int)1;
        for (; ;modulePattern.postInc()){
            byte c = modulePattern.get();
            if (((c & 0xFF) == 46))
            {
                depth++;
            }
            if (((c & 0xFF) == 0))
            {
                return depth;
            }
        }
    }

    // Erasure: parseModulePattern<Ptr, Ptr, int>
    public static void parseModulePattern(BytePtr modulePattern, Ptr<MatcherNode> dst, int depth) {
        boolean isExclude = false;
        if (((modulePattern.get(0) & 0xFF) == 45))
        {
            isExclude = true;
            modulePattern.postInc();
        }
        dst.set(0, new MatcherNode(isExclude, depth));
        dst.postInc();
        if (((int)depth > 0))
        {
            BytePtr idStart = pcopy(modulePattern);
            Ptr<MatcherNode> lastNode = dst.plus((int)depth * 4).minus(4);
            for (; (dst.lessThan(lastNode));dst.postInc()){
                for (; ;modulePattern.postInc()){
                    if (((modulePattern.get() & 0xFF) == 46))
                    {
                        assertMsg((modulePattern.greaterThan(idStart)), new ByteSlice("empty module pattern"));
                        dst.set(0, new MatcherNode(Identifier.idPool(idStart, ((modulePattern.minus(idStart))))));
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
                    lastNode.set(0, new MatcherNode(Identifier.idPool(idStart, ((modulePattern.minus(idStart))))));
                    break;
                }
            }
        }
    }

}
