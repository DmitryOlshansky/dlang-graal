module visitors.declaration;

import core.stdc.string;

import ds.buffer, ds.stack, ds.identity_map;

import dmd.aggregate;
import dmd.attrib;
import dmd.cond;
import dmd.dclass;
import dmd.denum;
import dmd.dimport;
import dmd.dmodule;
import dmd.dstruct;
import dmd.dtemplate;
import dmd.expression;
import dmd.declaration;
import dmd.dsymbol;
import dmd.func;
import dmd.id;
import dmd.identifier;
import dmd.init;
import dmd.mtype;
import dmd.statement;
import dmd.staticassert;
import dmd.tokens;
import dmd.visitor : Visitor, SemanticTimeTransitiveVisitor;
import dmd.root.array;

import std.array, std.algorithm, std.format, std.string, std.range, std.stdio;

import visitors.expression, visitors.members, visitors.passed_by_ref, visitors.templates;

alias toJava = visitors.expression.toJava; 

///
string toJava(Module mod, ToJavaModuleVisitor v) {
    auto id = mod.ident.toString.idup;
    v.moduleName = id.endsWith(".d") ? id[0..$-2] : id;
    v.onModuleStart(mod);
    mod.accept(v);
    v.onModuleEnd();
    return v.result;
}

struct Goto {
    Expression case_;
    bool default_;
    LabelDsymbol label;
    bool local;
}

Goto[] collectGotos(Statement s) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        Goto[] gotos;

        override void visit(ConditionalDeclaration ver) {
            if (ver.condition.inc == Include.yes) {
                if (ver.decl) {
                    foreach(d; *ver.decl){
                        d.accept(this);
                    }
                }
            }
            else if(ver.elsedecl) {
                foreach(d; *ver.elsedecl){
                    d.accept(this);
                }
            }
        }

        override void visit(GotoDefaultStatement ){
            gotos ~= Goto(null, true, null);
        }

        override void visit(GotoCaseStatement case_) {
            if (case_.exp && !gotos.canFind!(c => c.case_ is case_.exp)) {
                gotos ~= Goto(case_.exp, false, null);
            }
        }

        override void visit(ExpStatement e){}

        override void visit(GotoStatement goto_) {
            if (!gotos.canFind!(g => g.label is goto_.label))
                gotos ~= Goto(null,false,goto_.label);
        }
    }
    extern(C++) class MarkLocals : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        Goto[] gotos;
        override void visit(LabelStatement label){
            foreach(ref g; gotos) {
                if (g.label  && g.label.ident == label.ident)
                    g.local = true;
            }
            //stderr.writefln("%s %s", label.ident.toString, r);
            //if (!r.empty) r[0].local = true;
        }
    }
    scope v = new Collector();
    s.accept(v);
    scope v2 = new MarkLocals();
    v2.gotos = v.gotos;
    s.accept(v2);
    v2.gotos.sort!((a,b) => cast(int)a.local > cast(int)b.local);
    return v2.gotos;
}

bool hasCtor(AggregateDeclaration agg) {
    extern(C++) static class HasCtorVisitor : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        bool hasCtor = false;

        override void visit(CtorDeclaration) {
            hasCtor = true;
        }

        override void visit(Statement) {} // do shallow visit
    }
    scope v = new HasCtorVisitor();
    agg.accept(v);
    return v.hasCtor;
}

FuncExp[] collectLambdas(Statement s) {
    extern(C++) static class Lambdas : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        IdentityMap!bool exps;

        override void visit(FuncExp e) {
            exps[e] = true;
        }
    }
    scope v = new Lambdas();
    s.accept(v);
    return cast(FuncExp[])v.exps.keys();
}

AggregateDeclaration[] collectNestedAggregates(FuncDeclaration f) {
    extern(C++) static class Aggregates : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        AggregateDeclaration[] decls;

        override void visit(ClassDeclaration cd) {
            decls ~= cd;
        }

        override void visit(StructDeclaration sd) {
            decls ~= sd;
        }
    }
    scope v = new Aggregates();
    f.accept(v);
    return v.decls;
}

extern(C) void foobar(int) {}

VarDeclaration varargVarDecl(FuncDeclaration decl) {
    extern(C++) static class VarArg : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        VarDeclaration var;

        override void visit(CallExp e) {
            if (e.f && e.f.ident.symbol == "va_start") {
                auto ve = (*e.arguments)[0].isVarExp();
                var = ve.var.isVarDeclaration();
            }
        }
    }
    scope v = new VarArg();
    decl.accept(v);
    return v.var;
}

extern (C++) class ToJavaModuleVisitor : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    TextBuffer buf;
    TextBuffer header;
    string defAccess = "public";
    bool[string] generatedLambdas;
    Stack!(bool[string]) generatedFunctions;
    string moduleName;
    string[] constants; // all local static vars are collected here
    string[] imports; // all imports for deduplication
    Module currentMod;

    ExprOpts opts; // packs all information required for exp visitor

    int testCounter;
    
    Stack!int dispatch;
    int dispatchCount;

    Stack!int forLoop;
    int forCount;

    bool hasEmptyCtor;

    Stack!(Goto[]) gotos;
    Stack!(IdentityMap!int) labelGotoNums;
    
    int inInitializer; // to avoid recursive decomposition of arrays
    string[] arrayInitializers;

    bool noTiargs; // used for mixin templates
    Stack!TemplateInstance currentInst;

    string nameOf(AggregateDeclaration agg) {
        auto tmpl = agg in opts.templates;
        return format("%s%s", agg.ident.symbol, tmpl ? tmpl.tiArgs : "");
    }

    string tiArgs() {
        return currentInst.length && !noTiargs ? .tiArgs(currentInst.top, opts) : "";
    }

    void addImport(Array!Identifier* packages, Identifier id) {
        if (id == Id.object)
            return; // object is imported by default
        scope temp = new TextBuffer();
        if (packages && packages.dim)
        {
            foreach (const pid; *packages) 
            {
                if (pid.toString() == "root") return;
            }
            if((*packages)[0].toString() != "dmd") return;
            temp.put("import static org.dlang.");
            foreach (const pid; *packages)
            {
                temp.fmt("%s.", pid.toString());
            }
        }
        temp.fmt("%s.*;\n", id.toString(), id.toString());
        imports ~= temp.data.dup;
    }

    string funcSig(FuncDeclaration func) {
        auto b = new TextBuffer();
        b.put(func.type.nextOf.toJava(opts));
        b.put(" ");
        b.put(func.ident.symbol);
        auto tf = func.type.isTypeFunction();
        if(tf.parameterList)
            foreach(i, p; *tf.parameterList){
                if (i) b.put(", ");
                auto box = p.storageClass & (STC.ref_ | STC.out_);
                if (box && !p.type.isTypeStruct) {
                    b.fmt("%s", refType(p.type, opts));
                }
                else b.fmt("%s", toJava(p.type, opts));
            }
        if (auto ti = func in opts.templates) b.put(ti.tiArgs);
        return b.data.dup;
    }

    VarDeclaration hoistVarFromIf(Statement st) {
        if (auto ifs = st.isIfStatement()) {
            if (auto c = ifs.condition.isCommaExp()) {
                auto var = c.e1.isDeclarationExp().declaration.isVarDeclaration();
                return var;
            }
        }
        return null;
    }

    void onModuleStart(Module mod){
        buf = new TextBuffer();
        header = new TextBuffer();
        generatedLambdas = null;
        currentMod = mod;
        constants = null;
        arrayInitializers = null;
        imports = null;
        testCounter = 0;
        forCount = 0;
        dispatchCount = 0;
        if (generatedFunctions.length != 1) generatedFunctions.push(null);
        if (gotos.length != 1) gotos.push(null);
        opts.templates = registerTemplates(mod, opts);

        header.put("package org.dlang.dmd;\n");
        header.put("\nimport kotlin.jvm.functions.*;\n");
        header.put("\nimport org.dlang.dmd.root.*;\n");
        header.put("\nimport static org.dlang.dmd.root.filename.*;\n");
        header.put("\nimport static org.dlang.dmd.root.File.*;\n");
        header.put("\nimport static org.dlang.dmd.root.ShimsKt.*;\n");
        header.put("import static org.dlang.dmd.root.SliceKt.*;\n");
        header.put("import static org.dlang.dmd.root.DArrayKt.*;\n");
        buf.indent;
        buf.put("\n");
    }

    ///
    void onModuleEnd() {
        imports = uniq(sort(imports)).array;
        if (imports.length)  {
            foreach(imp; imports) {
                header.put(imp);
            }
        }
        header.fmt("\npublic class %s {\n", moduleName);
        header.indent;
        foreach (i, v; arrayInitializers) {
            header.fmt("%s;\n", v);
        }
        if (constants.length)  {
            foreach(var; constants) {
                header.put(var);
            }
        }
        header.outdent;
        buf.outdent;
        buf.fmt("}\n");
    }

    override void visit(ConditionalDeclaration ver) {
        if (ver.condition.inc == Include.yes) {
            if (ver.decl) {
                buf.put("\n");
                foreach(d; *ver.decl){
                    d.accept(this);
                }
            }
        }
        else if(ver.elsedecl) {
            foreach(d; *ver.elsedecl){
                d.accept(this);
            }
        }
    }

    void printSArray(Type type, TextBuffer sink) {
        auto st = cast(TypeSArray)type;
        sink.fmt("new %s(new %s[%s])", type.toJava(opts), type.nextOf.toJava(opts), st.dim.toJava(opts));
    }
   
    extern(D) private void printVar(VarDeclaration var, const(char)[] ident, TextBuffer sink) {
        // remove var-args decls
        if (opts.funcs.length && opts.vararg) {
            if (var.ident.symbol == "_arguments") return;
            if (var is opts.vararg) return;
        }
        bool staticInit = var.isStatic() || (var.storage_class & STC.gshared) || (opts.funcs.empty && opts.aggregates.empty);
        bool refVar = opts.funcs.length && passedByRef(var, opts.funcs.top) && !staticInit;
        if (refVar) opts.refParams[var] = true;
        Type t = var.type;
        if(var._init && var._init.kind == InitKind.array && var.type.ty == Tpointer)
            t = var.type.nextOf.arrayOf;
        auto type = refVar ? refType(var.type, opts) : toJava(t, opts);
        auto access = "";
        if (opts.aggregates.length && !opts.funcs.length) access = defAccess ~ " ";
        auto ti = opts.funcs.length ? "" : tiArgs;
        sink.fmt("%s%s%s %s%s",  access, staticInit ? "static " : "", type, ident, ti);
        bool oldWantChar = opts.wantCharPtr;
        scope(exit) opts.wantCharPtr = oldWantChar;
        if (var.type.ty == Tpointer && var.type.nextOf.ty == Tchar) {
            opts.wantCharPtr = true;
        }
        if (var._init) {
            ExpInitializer ie = var._init.isExpInitializer();
            if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit)) {
                sink.fmt(" = ");
                auto assign = (cast(AssignExp)ie.exp);
                auto integer = assign.e2.isIntegerExp();
                auto isNull = assign.e2.isNullExp();
                //stderr.writefln("Init1 %s integer = %s null = %s", var, integer, isNull);
                if (integer && integer.toInteger() == 0 && var.type.ty == Tstruct){
                    sink.fmt("new %s()", var.type.toJava(opts));
                }
                else if(integer && integer.toInteger() == 0 && var.type.ty == Tsarray) {
                    sink.fmt(" = ");
                    printSArray(var.type, sink);
                }
                else if(var.type.ty == Tarray && isNull) {
                    if (refVar) sink.fmt("ref(new %s())", var.type.toJava(opts));
                    else sink.fmt("new %s()", var.type.toJava(opts));
                }
                else {
                    bool needPCopy(Expression e) {
                        return e.type.ty == Tpointer && !e.isNullExp && e.type.nextOf.ty != Tstruct;
                    }
                    bool needCopy(Expression e) {
                        return e.type.ty == Tstruct || e.type.ty == Tarray;
                    }
                    //fprintf(stderr, "init %s with %s\n", var.toChars, assign.e2.toChars);
                    if (refVar) sink.fmt("ref(");
                    if (needPCopy(assign.e2)) sink.put("pcopy(");
                    sink.put(assign.e2.toJava(opts));
                    if (needCopy(assign.e2))  sink.put(".copy()");
                    if (needPCopy(assign.e2)) sink.put(")");
                    if (refVar) sink.fmt(")");
                }
            }
            else {
                //stderr.writefln( "Init2 %s", var);
                sink.fmt(" = ");
                auto old = opts.wantCharPtr;
                scope(exit) opts.wantCharPtr = old;
                opts.wantCharPtr = var.type.ty == Tpointer && var.type.nextOf().ty == Tchar;
                initializerToBuffer(var._init, sink, opts);
            }
        }
        else if(var.type.ty == Tsarray) {
            sink.fmt(" = ");
            printSArray(var.type, sink);
        }
        else if (var.type.ty == Tstruct) {
            sink.fmt(" = new %s()", var.type.toJava(opts));
        }
        sink.fmt(";\n");
    }

    override void visit(AnonDeclaration anon)
    {
        auto members = collectMembers(anon);
        VarDeclaration[string] visited;
        stderr.writefln("UNION members: %s", members.all);
        foreach (m; members.all) {
            if (auto root = m.type.toJava(opts) in visited) {
                opts.aliasedUnion[m] = *root;
            }
            else {
                visited[m.type.toJava(opts)] = m;
            }
        }
        foreach (m; members.all) {
            if (m !in opts.aliasedUnion)
                (cast(VarDeclaration)m).accept(this);
        }
    }

    override void visit(VarDeclaration var) {
        if (var.type is null) {
            stderr.writefln("NULL TYPE VAR: %s", var.ident.symbol);
            return;
        }
        addImportForType(var.type);
        if (var.type.toJava(opts).startsWith("TypeInfo_")) return;
        bool pushToGlobal = (var.isStatic() || (var.storage_class & STC.gshared)) && !opts.funcs.empty;
        if (pushToGlobal) {
            auto temp = new TextBuffer();
            const(char)[] id = opts.funcs.top.funcName ~ var.ident.symbol;
            printVar(var, id, temp);
            constants ~= temp.data.idup;
            opts.renamed[var] = format("%s.%s", moduleName, id);
        }
        else {
            if (auto name = var in opts.renamed)
                printVar(var, *name, buf);
            else 
                printVar(var, var.ident.symbol, buf);
        }
    }

    override void visit(ExpStatement s)
    {
        if (s.exp && s.exp.op == TOK.declaration &&
            (cast(DeclarationExp)s.exp).declaration)
        {
            (cast(DeclarationExp)s.exp).declaration.accept(this);
        }
        else if (s.exp) {
            auto text = s.exp.toJava(opts);
            if (text.length) {
                buf.put(text);
                buf.put(";\n");
            }
        }
    }

    override void visit(ScopeStatement s)
    {
        if (s.statement)  {
            buf.put("{\n");
            buf.indent;
                s.statement.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
    }

    override void visit(CompoundStatement s)
    {
        static struct Range {
            long first = int.max;
            long last = int.min;
            bool reversed = false;
        }
        auto labels = s.statements ? 
            (*s.statements)[]
                .map!(s => s ? s.isLabelStatement() : null)
                .filter!(x => x).array 
            : null;
        auto range = new Range[labels.length];
        if (gotos.top.length == 0) { // do not use try/catch mechanism inside of switches
            if (s.statements)
                foreach(k, lbl; labels) {
                    labelGotoNums.top[lbl.ident] = cast(int)k;
                    foreach (i, st; *s.statements) if (st) {
                        auto gs = collectGotos(st);
                        auto nonLocalGotos = gs.filter!(g => !g.local);
                        if (nonLocalGotos.canFind!(g => g.label && g.label.ident == lbl.ident)) {
                            auto target = (*s.statements)[].countUntil!(x => x is lbl);
                            if (range[k].first > i) range[k].first = i;
                            if (range[k].last < target) range[k].last = target;
                        }
                    }
                    if (range[k].first > range[k].last) range[k].reversed = true;
                }
        }
        if (s.statements) {
            ptrdiff_t[] totalRevs = [];
            foreach (idx, st; *s.statements) if (st) {
                auto starts = range.filter!(x => x.first == idx && !x.reversed);
                foreach (start; starts) {
                    buf.put("try {\n");
                    buf.indent;
                }
                auto revEnds = range.filter!(x => x.last == idx && x.reversed);
                foreach (rev; revEnds) {
                    buf.put("while(true) try {\n");
                    buf.indent;
                    totalRevs ~= range.countUntil(rev);
                }
                // try to find target on this level, if fails we are too deep
                // some other (upper) check will eventually succeed
                if (!st.isCompoundStatement() && !st.isScopeStatement()) {
                    auto lambdas = collectLambdas(st);
                    foreach (i, v; lambdas)  {
                        stderr.writefln("lambda: %d", i);
                        if (v.fd.ident.symbol !in generatedLambdas) {
                            auto _ = pushed(opts.funcs, v.fd);
                            printLocalFunction(v.fd, true);
                            generatedLambdas[v.fd.ident.symbol] = true;
                        }
                    }
                }
                auto end = range.countUntil!(x => x.last == idx && !x.reversed);
                if (end >= 0) {
                    buf.outdent;
                    buf.fmt("}\ncatch(Dispatch%d __d){}\n", end);
                }
                if (idx == s.statements.length-1 && st.isBreakStatement) {
                    foreach (rev; totalRevs) {
                        buf.put("break;\n");
                        buf.outdent;
                        buf.fmt("} catch(Dispatch%d __d){}\n", rev);
                    }
                }
                st.accept(this);
                if (idx == s.statements.length-1 && !st.isBreakStatement) 
                    foreach (rev; totalRevs) {
                        buf.put("break;\n");
                        buf.outdent;
                        buf.fmt("} catch(Dispatch%d __d){}\n", rev);
                    }
                //TODO: for?
            }
        }
    }

    override void visit(WhileStatement s)
    {
        assert(0);
        /*buf.put("while (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(")\n");
        if (s._body)
            s._body.accept(this);*/
    }

    override void visit(DoStatement s)
    {
        if (collectGotos(s._body).length > 0) {
            buf.outdent;
            forLoop.push(++forCount);
            buf.fmt("L_outer%d:\n", forCount);
            buf.indent;
        }
        else 
            forLoop.push(0); // no gotos, let it continue this inner for
        scope(exit) forLoop.pop();
        buf.put("do {\n");
        buf.indent;
        if (s._body)
            s._body.accept(this);
        buf.outdent;
        buf.put("} while (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(");\n");
    }

    override void visit(ForStatement s)
    {
        if (collectGotos(s._body).length > 0) {
            buf.outdent;
            forLoop.push(++forCount);
            buf.fmt("L_outer%d:\n", forCount);
            buf.indent;
        }
        else 
            forLoop.push(0); // no gotos, let it continue this inner for
        scope(exit) forLoop.pop();
        buf.put("for (");
        if (s._init)
        {
            s._init.accept(this);
        }
        buf.put("; ");
        if (s.condition)
        {
            buf.put(s.condition.toJavaBool(opts));
        }
        buf.put(";");
        if (s.increment)
        {
            buf.put(s.increment.toJava(opts));
        }
        buf.put(')');
        if (s._body && !s._body.isScopeStatement())
        {
            buf.put(" {\n");
            buf.indent;
            if (s._body) {
                s._body.accept(this);
            }
            buf.outdent;
            buf.put("}\n");
        }
        else if (s._body) {
            s._body.accept(this);
        }
    }

    override void visit(ForeachStatement s)
    {
        assert(false); // has been lowered
    }

    override void visit(IfStatement s)
    {
        auto var = hoistVarFromIf(s);
        if (var) {
            buf.put("{\n");
            buf.indent;
            var.accept(this);
        }
        scope(exit) if (var) {
            buf.outdent;
            buf.put("}\n");
        }
        buf.put("if (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(")\n");
        if (s.ifbody.isScopeStatement())
        {
            s.ifbody.accept(this);
        }
        else
        {
            buf.indent;
            s.ifbody.accept(this);
            buf.outdent;
        }
        if (s.elsebody)
        {
            buf.put("else");
            if (!s.elsebody.isIfStatement())
            {
                buf.put('\n');
            }
            else
            {
                buf.put(' ');
            }
            if (s.elsebody.isScopeStatement() || s.elsebody.isIfStatement())
            {
                s.elsebody.accept(this);
            }
            else
            {
                buf.indent;
                s.elsebody.accept(this);
                buf.outdent;
            }
        }
    }

    override void visit(TemplateMixin mix) {
        buf.fmt("// from template mixin %s", mix.toString);
        noTiargs = true;
        scope(exit) noTiargs = false;
        auto _ = pushed(currentInst, mix);
        visit(cast(TemplateInstance)mix);
    }

    override void visit(TemplateDeclaration td) {
        if (td.ident.symbol.startsWith("RTInfo")) return;
        foreach(inst; td.instances.values) {
            auto _ = pushed(currentInst, inst);
            inst.accept(this);
        }
    }

    override void visit(TemplateInstance ti) {
        if (currentInst.empty || !currentInst.top) return;
        if (ti.tiargs) {
            auto decl = ti.tempdecl.isTemplateDeclaration();
            foreach(m; *ti.members) {
                buf.fmt("// from template %s!(%s)\n", ti.name.symbol, .tiArgs(ti, opts));
                m.accept(this);
                buf.put("\n");
            }
        }
    }

    override void visit(StaticAssert s)
    {
        // stderr.writefln("StaticAssert: %s\n", s.toString());
        // ignore and do not recurse into
    }

    override void visit(StaticCtorDeclaration ctor)
    {
        buf.put("static {\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
    }

    override void visit(SharedStaticCtorDeclaration ctor)
    {
        auto _ = pushed(opts.funcs, ctor);
        buf.put("static {\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
    }

    override void visit(Import imp)
    {
        addImport(imp.packages, imp.id);
    }

    override void visit(CompileDeclaration compile)
    {
        foreach (e; *compile.exps) {
            auto se = cast(StringExp)e.ctfeInterpret();
            auto s = se.string[0..se.len];
            s = s.replace("Identifier", "static Identifier"); //hack
            buf.put(s);
            buf.put("\n");
        }
    }

    override void visit(EnumDeclaration d)
    {
        auto old = buf;
        auto oldAccess = defAccess;
        if (opts.funcs.length) {
            buf = new TextBuffer();
            defAccess = "private";
        }
        auto oldInEnumDecl = opts.inEnumDecl;
        scope(exit) opts.inEnumDecl = oldInEnumDecl;
        opts.inEnumDecl = d;
        buf.fmt("\n%s static class ", defAccess);
        if (d.ident)
        {
            buf.put(symbol(d.ident));
            buf.put(' ');
        }
        if (!d.members)
        {
            buf.put(';');
            buf.put('\n');
            return;
        }
        buf.put('\n');
        buf.put('{');
        buf.put('\n');
        buf.indent;
        foreach (em; *d.members)
        {
            if (!em)
                continue;
            em.accept(this);
        }
        buf.outdent;
        buf.put("}\n\n");
        if (opts.funcs.length) {
            constants ~= buf.data.dup;
            buf = old;
            defAccess = oldAccess;
        }
    }

    override void visit(EnumMember em)
    {
        if (em.value)
        {
            buf.fmt("public static final %s %s = %s;\n", 
                em.type.toJava(opts), em.ident.symbol, em.value.toJava(opts));
        }
    }

    override void visit(SwitchStatement s)
    {
        auto gt = pushed(gotos, collectGotos(s));
        //if (gotos.length) stderr.writefln("GOTOS: %s", gotos);
        auto _d = pushed(dispatch, dispatchCount++);
        if (gotos.top.length) {
            buf.put("{\n");
            buf.indent;
            buf.fmt("int __dispatch%d = 0;\n", dispatch.top);
            buf.fmt("dispatched_%d:\n", dispatch.top);
            buf.put("do {\n");
            buf.indent;
        }
        disambiguateVars(s, opts.renamed);
        auto cond = s.condition.toJava(opts);
        if (s.condition.type.toJava(opts) == "byte") {
            cond = "(" ~ cond ~" & 0xFF)";
        }
        buf.put("switch (");
        if (gotos.top.length) {
            buf.fmt("__dispatch%d != 0 ? __dispatch%d : %s", 
                dispatch.top, dispatch.top, cond);
        }
        else
            buf.put(cond);
        buf.put(')');
        buf.put('\n');
        if (s._body)
        {
            if (!s._body.isScopeStatement())
            {
                //stderr.writefln("SWITCH2 %s \n", cond);
                buf.put('{');
                buf.put('\n');
                buf.indent;
                if (auto comp = s._body.isCompoundStatement()) {
                    foreach(st; *comp.statements) {
                        if (auto scst = st.isScopeStatement()) scst.statement.accept(this);
                        else st.accept(this);
                    }
                }
                else
                    s._body.accept(this);
                buf.outdent;
                buf.put('}');
                buf.put('\n');
            }
            else
            {
                //stderr.writefln("SWITCH %s \n", cond);
                s._body.accept(this);
            }
        }
        if (gotos.top.length) {
            buf.outdent;
            buf.fmt("} while(__dispatch%d != 0);\n", dispatch.top);
            buf.outdent;
            buf.put("}\n");
        }
    }

    override void visit(WithStatement w)
    {
        if (auto ss = w._body.isScopeStatement) {
            if (dispatch.length > 0 && ss.statement)
                return ss.statement.accept(this);
        }
        w._body.accept(this);
    }

    override void visit(CaseStatement s)
    {
        buf.put("case ");
        buf.put(s.exp.toJava(opts));
        buf.put(':');
        buf.put('\n');
        buf.indent;
        Statement st = s.statement;
        ScopeStatement ss;
        while (st && ((ss = st.isScopeStatement()) !is null)) {
            st = ss.statement;
        }
        if (st) st.accept(this);
        buf.outdent;
    }

    override void visit(CaseRangeStatement s)
    {
        buf.put("case ");
        buf.put(s.first.toJava(opts));
        buf.put(": .. case ");
        buf.put(s.last.toJava(opts));
        buf.put(':');
        buf.put('\n');
        s.statement.accept(this);
    }

    override void visit(DefaultStatement s)
    {
        buf.put("default:\n");
        Statement st = s.statement;
        ScopeStatement ss;
        while (st && ((ss = st.isScopeStatement()) !is null)) {
            st = ss.statement;
        }
        if (st) st.accept(this);
    }

    override void visit(LabelStatement label) {
        buf.outdent;
        buf.fmt("/*%s:*/\n", label.ident.symbol);
        long myIndex = gotos.top.countUntil!(c => c.label && c.label.ident == label.ident);
        if (myIndex >= 0 && gotos.top[myIndex].local && dispatch.top > 0) {
            buf.fmt("case %d:\n__dispatch%d = 0;\n", -1-myIndex, dispatch.top);
        }
        buf.indent;
        super.visit(label);
    }

    override void visit(GotoStatement g) {
        buf.fmt("/*goto %s*/", g.label.toString);
        auto myIndex = map!(gs => gs.countUntil!(c => c.label is g.label))(gotos[]);
        auto stackIndex = myIndex.countUntil!(x => x >= 0);
        auto idx = stackIndex >= 0 ? myIndex[stackIndex] : -1;
        if (idx >= 0 && gotos[][stackIndex][idx].local) {
            //stderr.writefln("StackIdx = %d idx = %d dispatch = %s gotos = %s", stackIndex, idx, dispatch[], gotos[]);
            // gotos have empty array added at start so -1
            buf.fmt("{ __dispatch%d = %d; continue dispatched_%d; }\n",
                dispatch[][stackIndex - 1], -1-idx, dispatch[][stackIndex - 1]);
        }
        else if (auto count = g.label.ident in labelGotoNums.top){
            buf.fmt("throw Dispatch%d.INSTANCE;\n", *count);
        }
        else
            buf.put("throw Dispatch.INSTANCE;\n");
    }

    override void visit(GotoDefaultStatement s)
    {
        long myIndex = gotos.top.countUntil!(c => c.default_);
        buf.put("/*goto default*/ ");
        if (myIndex >= 0) {
            buf.fmt("{ __dispatch%d = %d; continue dispatched_%d; }\n", 
                dispatch.top, -1-myIndex, dispatch.top);
        }
        else {
            buf.put("throw Dispatch.INSTANCE;\n");
        }
    }

    override void visit(GotoCaseStatement s)
    {
        if (!s.exp) {
            // fallthrough
        }
        else {
            buf.put("/*goto case*/");
            buf.fmt("{ __dispatch%d = %s; continue dispatched_%d; }\n",
                dispatch.top, s.exp.toJava(opts), dispatch.top);
        }
    }

    override void visit(SwitchErrorStatement s)
    {
        buf.fmt("throw SwitchError.INSTANCE;\n");
    }
    
    override void visit(BreakStatement s)
    {
        buf.put("break");
        if (s.ident)
        {
            buf.put(' ');
            buf.put(s.ident.toString());
        }
        buf.put(';');
        buf.put('\n');
    }

    override void visit(ContinueStatement s)
    {
        buf.put("continue");
        if (s.ident)
        {
            buf.put(' ');
            buf.put(s.ident.toString());
        }
        else if(forLoop.top != 0) {
            buf.put(' ');
            buf.fmt("L_outer%d", forLoop.top);
        }
        buf.put(';');
        buf.put('\n');
    }

    override void visit(SynchronizedStatement s)
    {
        buf.put("synchronized");
        if (s.exp)
        {
            buf.put('(');
            buf.put(s.exp.toJava(opts));
            buf.put(')');
        }
        if (s._body)
        {
            buf.put(' ');
            s._body.accept(this);
        }
    }

    override void visit(ReturnStatement s)
    {
        if (opts.funcs.length && !opts.funcs.top.isCtorDeclaration()) {
            if(opts.funcs.top.ident.symbol == "main" && opts.aggregates.empty) {
                buf.put("exit(");
                if (s.exp) {
                    buf.put(s.exp.toJava(opts));
                }
                buf.put(");\n");
            }
            else if (opts.funcs.length > 1 && opts.funcs.top.type.nextOf.ty == Tvoid) {
                buf.put("return null;\n");
            }
            else {
                buf.put("return ");
                if (s.exp) {
                    auto retType = opts.funcs.top.type.nextOf();
                    auto oldOpts = opts;
                    scope(exit) opts = oldOpts;
                    opts.wantCharPtr = retType.ty == Tpointer && retType.nextOf().ty == Tchar;
                    buf.put(s.exp.toJava(opts));
                }
                buf.put(";\n");
            }
        }
    }

    void addImportForType(Type t) {
        // stderr.writefln("import for %s %s\n", t.toString, t.kind[0..strlen(t.kind)]);
        auto tc = t.isTypeClass();
        // if (tc) 
        if (tc && tc.sym.getModule() !is currentMod) {
            addImport(tc.sym.getModule.md.packages, tc.sym.getModule.ident);
        }
        auto ts = t.isTypeStruct();
        if (ts && ts.sym.getModule() !is currentMod) {
            addImport(ts.sym.getModule.md.packages, ts.sym.getModule.ident);
        }
    }

    auto handleTiAggregate(AggregateDeclaration d) { 
        if (!currentInst.empty) {
            auto tiargs = currentInst.top ? tiArgs() : "";
            if (currentInst.top) {
                if (currentInst.top.tiargs)
                    foreach (arg; *currentInst.top.tiargs) {
                        if (auto t = arg.isType()) {
                            addImportForType(t);
                        }
                    }
            }
        }
        return pushed(currentInst, null);
    }

    override void visit(StructDeclaration d)
    {
        if (opts.funcs.length) return; // inner structs are done separately
        auto _ = pushed(opts.aggregates, d);
        auto gf = pushed(generatedFunctions, null);
        auto guard = handleTiAggregate(d);

        stderr.writefln("Struct %s", d);
        auto members = collectMembers(d);
        auto linkedNode = members.all.countUntil!(var => var.ident.symbol == "next" && (var.type.ty == Tpointer || var.type.ty == Tclass));
        buf.fmt("%s static class ", defAccess);
        if (!d.isAnonymous()) {
            buf.put(nameOf(d));
        }
        if (linkedNode >= 0) {
            buf.fmt(" implements LinkedNode<%s>", nameOf(d));
        }
        if (!d.members)
        {
            buf.put(';');
            buf.put('\n');
            return;
        }
        buf.put('\n');
        buf.put('{');
        buf.put('\n');
        buf.indent;
        foreach (s; *d.members)
            s.accept(this);
        // .init ctor
        buf.fmt("public %s(){\n", nameOf(d));
        buf.indent;
        foreach(m; members.all) {
            if (auto ts = m.type.isTypeStruct()) {
                buf.fmt("%s = new %s();\n", m.ident.symbol, toJava(ts, opts));
            }
        }
        buf.outdent;
        buf.put("}\n");
        // default shallow copy for structs
        buf.fmt("public %s copy(){\n", nameOf(d));
        buf.indent;
        buf.fmt("%s r = new %s();\n", nameOf(d), nameOf(d));
        foreach(m; members.all) {
            if (m !in opts.aliasedUnion) {
                if (m.type.ty == Tstruct || m.type.ty == Tarray) {
                    buf.fmt("r.%s = %s.copy();\n", m.ident.symbol, m.ident.symbol);
                }
                else
                    buf.fmt("r.%s = %s;\n", m.ident.symbol, m.ident.symbol);
            }
        }
        buf.put("return r;\n");
        buf.outdent;
        buf.put("}\n");
        bool hasCtor = hasCtor(d);
        if (!hasCtor) {
            if (members.all.length) {
                //Generate ctors
                // all fields ctor
                if (!members.hasUnion) {
                    buf.fmt("public %s(", nameOf(d));
                    foreach(i, m; members.all) {
                        if(i) buf.put(", ");
                        buf.fmt("%s %s", m.type.toJava(opts), m.ident.toString);
                    }
                    buf.put(") {\n");
                    buf.indent;
                    foreach(i,m; members.all){
                        if (m !in opts.aliasedUnion)
                            buf.fmt("this.%s = %s;\n", m.ident.toString, m.ident.toString);
                    }
                    buf.outdent;
                    buf.put("}\n\n");
                }
            }
        }
        // generate opAssign
        buf.fmt("public %s opAssign(%s that) {\n", nameOf(d), nameOf(d));
        buf.indent;
        foreach(i,m; members.all){
            if (m !in opts.aliasedUnion)
                buf.fmt("this.%s = that.%s;\n", m.ident.toString, m.ident.toString);
        }
        buf.put("return this;\n");
        buf.outdent;
        buf.put("}\n");
        if (linkedNode >= 0) {
            buf.fmt("public void setNext(%s value) { next = value; }\n", members.all[linkedNode].type.toJava(opts));
            buf.fmt("public %s getNext() { return next; }\n", members.all[linkedNode].type.toJava(opts));
        }
        buf.outdent;
        buf.put('}');
        buf.put('\n');
    }
    
    override void visit(ClassDeclaration d)
    {
        if (opts.funcs.length) return; // inner classes are done separately
        auto gf = pushed(generatedFunctions, null);
        auto agg = pushed(opts.aggregates, d);

        auto guard = handleTiAggregate(d);

        stderr.writefln("Class %s", d);
        auto members = collectMembers(d, true);
        auto linkedNode = members.all.countUntil!(var => var.ident.symbol == "next" && (var.type.ty == Tpointer || var.type.ty == Tclass));
        if (!d.isAnonymous())
        {
            auto abs =  d.isAbstract ? "abstract " : "";
            buf.fmt("%s static %sclass %s", defAccess, abs, nameOf(d));
        }
        visitBase(d);
        if (linkedNode >= 0) {
            buf.fmt(" implements LinkedNode<%s>", members.all[linkedNode].type.toJava(opts));
        }
        if (d.members)
        {
            buf.put("\n{\n");
            buf.indent;

            auto oldHasEmptyCtor = hasEmptyCtor;
            scope(exit) hasEmptyCtor = oldHasEmptyCtor;
            hasEmptyCtor = false;
            
            foreach (s; *d.members) {
                s.accept(this);
            }
            if (!hasEmptyCtor) buf.fmt("\npublic %s() {}\n", nameOf(d));

            // generate copy
            buf.fmt("\npublic %s%s copy()", d.isAbstract ? "abstract " : "", nameOf(d));
            if (d.isAbstract) buf.put(";\n");
            else {
                buf.put(" {\n");
                buf.indent;
                buf.fmt("%s that = new %s();\n", nameOf(d), nameOf(d));
                foreach(m; members.all) {
                    buf.fmt("that.%s = this.%s;\n", m.ident.symbol, m.ident.symbol);
                }
                buf.put("return that;\n");
                buf.outdent;
                buf.fmt("}\n");
            }

            // linked node getters/setters
            if (linkedNode >= 0) {
                buf.fmt("public void setNext(%s value) { next = value; }\n", members.all[linkedNode].type.toJava(opts));
                buf.fmt("public %s getNext() { return next; }\n", members.all[linkedNode].type.toJava(opts));
            }

            buf.outdent;
            buf.put('}');
            
        }
        else
            buf.put(';');
        buf.put('\n');
    }

    private void visitBase(ClassDeclaration d)
    {
        if (!d || !d.baseclasses.dim)
            return;
        if (!d.isAnonymous())
            buf.put(" extends ");
        foreach (i, b; *d.baseclasses)
        {
            if (i) buf.put(", ");
            buf.put(b.type.toJava(opts));
        }
    }

    override void visit(UnitTestDeclaration func)  {
        hoistLocalAggregates(func);
        auto _ = pushed(opts.funcs, func);
        if (func.fbody) {
            buf.fmt("public static void test_%d() {\n", testCounter++);
            buf.indent;    
            func.fbody.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
    }

    void printLocalFunction(FuncDeclaration func, bool isLambda = false) {
        auto t = func.type.isTypeFunction();
        //stderr.writefln("\tLocal function %s", func.ident.toString);
        buf.fmt("%s %s%s = new %s(){\n", t.toJavaFunc(opts), func.funcName, tiArgs, t.toJavaFunc(opts));
        buf.indent;
        buf.fmt("public %s invoke(", t.nextOf.toJava(opts, Boxing.yes));
        VarDeclaration[] renamedVars;
        if (func.parameters) {
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !isLambda && !p.type.isTypeStruct && p.type.ty != Tarray) {
                    opts.refParams[p] = true;
                    buf.fmt("%s %s", refType(p.type, opts), p.ident.symbol);
                }
                else {
                    buf.fmt("%s %s", toJava(p.type, opts, Boxing.yes), p.ident.symbol);
                    if (passedByRef(p, func)) {
                        renamedVars ~= p;
                        opts.refParams[p] = true;
                        opts.renamed[p] = (p.ident.symbol ~ "_ref").dup;
                    }
                }
            }
        }
        buf.put("){\n");
        buf.indent;
        foreach (var; renamedVars) {
            buf.fmt("%s %s = ref(%s);\n", refType(var.type, opts), opts.renamed[var], var.ident.symbol);
        }
        super.visit(func);
        if (t.nextOf.ty == Tvoid) buf.put("return null;\n");
        buf.outdent;
        buf.fmt("}\n");
        buf.outdent;
        buf.fmt("};\n");
    }

    void printGlobalFunction(FuncDeclaration func) {
        opts.vararg = null;
        if (func.fbody is null && !func.isAbstract) return;
        //stderr.writefln("\tFunction %s", func.ident.toString);
        auto storage = (func.isStatic()  || opts.aggregates.length == 0) ? "static" : "";
        if (func.isAbstract && func.fbody is null) storage = "abstract";
        if (auto ctor = func.isCtorDeclaration())
            buf.fmt("public %s %s%s(", storage, toJava(func.type.nextOf(), opts), tiArgs);
        else if(func.funcName == "main" && opts.aggregates.length == 0) {
            buf.fmt("public %s void %s%s(", storage, func.funcName, tiArgs);
        }
        else
            buf.fmt("public %s %s %s%s(", storage, toJava(func.type.nextOf(), opts), func.funcName, tiArgs);
        VarDeclaration[] renamedVars;
        if (func.parameters) {
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !p.type.isTypeStruct && p.type.ty != Tarray) {
                    opts.refParams[p] = true;
                    buf.fmt("%s %s", refType(p.type, opts), p.ident.toString);
                }
                else {
                    buf.fmt("%s %s", toJava(p.type, opts), p.ident.toString);
                    if (passedByRef(p, func)) {
                        renamedVars ~= p;
                        opts.refParams[p] = true;
                        opts.renamed[p] = (p.ident.symbol ~ "_ref").dup;
                    }
                }
            }
            if (auto var = varargVarDecl(func)) {
                opts.vararg = var;
                buf.fmt(", Object... %s", var.ident.symbol);
            }
        }
        else if (auto ft = func.type.isTypeFunction()){
            if (ft.parameterList)
                foreach(i, p; *ft.parameterList) {
                   if (i != 0) buf.fmt(", ");
                    auto box = p.storageClass & (STC.ref_ | STC.out_);
                    auto name = p.ident ? p.ident.toString : format("arg%d",i);
                    if (box && !p.type.isTypeStruct && p.type.ty != Tarray) {
                        opts.refParams[p] = true;
                        buf.fmt("%s %s", refType(p.type, opts), name);
                    }
                    else buf.fmt("%s %s", toJava(p.type, opts), name);
                }
        }
        buf.put(")");
        if (func.fbody is null) 
            buf.put(";\n");
        else {
            buf.put(" {\n");
            buf.indent;
            foreach (var; renamedVars) {
                buf.fmt("%s %s = ref(%s);\n", refType(var.type, opts), opts.renamed[var], var.ident.symbol);
            }
            func.fbody.accept(this);
            buf.outdent;
            buf.put('}');
            buf.put("\n\n");
        }
    }

    void hoistLocalAggregates(FuncDeclaration func) {
        auto nested = collectNestedAggregates(func);
        auto save = buf;
        buf = new TextBuffer();
        auto oldDefAccess = defAccess;
        defAccess = "private";
        scope(exit) defAccess = oldDefAccess;
        foreach(agg; nested) agg.accept(this);
        constants ~= buf.data.dup;
        buf = save;
    }

    override void visit(TryFinallyStatement statement) {
        buf.put("try {\n");
        buf.indent;
        if (statement._body) statement._body.accept(this);
        buf.outdent;
        buf.put("}\n");
        if (statement.finalbody) {
            buf.put("finally {\n");
            buf.indent;
            statement.finalbody.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
    }

    override void visit(DtorDeclaration ) { }

    override void visit(FuncDeclaration func)  {
        if (func.funcName == "destroy") return;
        if (func.funcName == "opAssign") return;
        if (func.funcName == "copy" && opts.aggregates.length > 0) return;
        if (func.isCtorDeclaration() && !func.parameters) hasEmptyCtor = true;
        if (opts.funcs.length > 0) opts.localFuncs[func] = true;
        // check for duplicates
        auto sig = funcSig(func);
        if (sig in generatedFunctions.top) return;
        generatedFunctions.top[sig] = true;
        auto lgn = pushed(labelGotoNums);
        // hoist nested structs/classes to top level, mark them private
        if (opts.funcs.length == 0) {
            hoistLocalAggregates(func);
        }
        auto _ = pushed(opts.funcs, func);

        auto oldRefParams = opts.refParams.dup;
        scope(exit) opts.refParams = oldRefParams;

        if (opts.funcs.length > 1)
            printLocalFunction(func);
        else {
            auto gf = pushed(generatedFunctions);
            printGlobalFunction(func);
        }
    }

    private void initializerToBuffer(Initializer inx, TextBuffer buf, ExprOpts opts)
    {
        void visitError(ErrorInitializer iz)
        {
            buf.fmt("__error__");
        }

        void visitVoid(VoidInitializer iz)
        {
            if (iz.type.ty == Tsarray) printSArray(iz.type, buf);
            else if(iz.type.ty == Tint32 || iz.type.ty == Tuns32) {
                buf.put("0");
            }
            else buf.fmt("null");
        }

        void visitStruct(StructInitializer si)
        {
            //printf("StructInitializer::toCBuffer()\n");
            buf.put('{');
            foreach (i, const id; si.field)
            {
                if (i)
                    buf.put(", ");
                if (id)
                {
                    buf.put(id.toString());
                    buf.put(':');
                }
                if (auto iz = si.value[i])
                    initializerToBuffer(iz, buf, opts);
            }
            buf.put('}');
        }

        void visitArray(ArrayInitializer ai)
        {
            TextBuffer tmp = buf;
            inInitializer++;
            Initializer[] arr = new Initializer[ai.index.length];
            bool strings = true;
            foreach (i, ex; ai.index)
            {
                if (ex)
                {
                    auto ie = ex.isIntegerExp();
                    if (arr.length <= ie.toInteger) arr.length = ie.toInteger + 1;
                    arr[ie.toInteger] = ai.value[i];
                }
                else {
                    arr[i] = ai.value[i];
                    if (auto e = arr[i].isArrayInitializer()) {
                        strings = false;
                    }
                }
            }
            if (inInitializer == 1) {
                tmp = new TextBuffer();
                auto t = ai.type;
                string suffix = "";
                opts.rawArrayLiterals = true;
                // string literals are byte arrays, exclude them
                while((t.ty == Tarray || t.ty == Tsarray || t.ty == Tpointer) && (t.nextOf.ty != Tchar || !strings)) {
                    suffix ~= "[]";
                    t = t.nextOf();
                }
                tmp.fmt("private static final %s%s initializer_%d = ", t.toJava(opts), suffix, arrayInitializers.length);
            }
            
            tmp.put("{");
            foreach (i, iz; arr[])
            {
                if (i)
                    tmp.put(", ");
                if (iz) initializerToBuffer(iz, tmp, opts);
                else if (ai.type.nextOf().ty == Tenum) tmp.put("0");
                else tmp.put("null");
            }
            tmp.put("}");
            if (inInitializer == 1) {
                opts.rawArrayLiterals = false;
                arrayInitializers ~= tmp.data.idup;
                buf.fmt("slice(initializer_%d)", arrayInitializers.length-1);
            }
            inInitializer--;
        }

        void visitExp(ExpInitializer ei)
        {
            //stderr.writefln("Initializer is %s %s\n", ei.exp, ei.exp.type);
            buf.put(ei.exp.toJava(opts));
        }

        final switch (inx.kind)
        {
            case InitKind.error:   return visitError (inx.isErrorInitializer ());
            case InitKind.void_:   return visitVoid  (inx.isVoidInitializer  ());
            case InitKind.struct_: return visitStruct(inx.isStructInitializer());
            case InitKind.array:   return visitArray (inx.isArrayInitializer ());
            case InitKind.exp:     return visitExp   (inx.isExpInitializer   ());
        }
    }

    string result() { return cast(string)header.data ~ cast(string)buf.data; }
}

