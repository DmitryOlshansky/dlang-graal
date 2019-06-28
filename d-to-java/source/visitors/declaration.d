module visitors.declaration;

import core.stdc.string;

import ds.buffer;

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

import visitors.expression : Boxing, ExprOpts, funcName, refType, Template, toJava, toJavaBool, toJavaFunc, symbol;
import visitors.members;
import visitors.passed_by_ref;

///
string toJava(Module mod) {
    scope v = new toJavaModuleVisitor();
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
        bool[void*] exps;

        override void visit(FuncExp e) {
            exps[cast(void*)e] = true;
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


extern (C++) class toJavaModuleVisitor : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    TextBuffer buf;
    TextBuffer header;
    string defAccess = "public";
    FuncDeclaration[] stack;
    bool[string] generatedLambdas;
    bool[string] generatedFunctions;
    AggregateDeclaration[] aggregates;
    string moduleName;
    string[] constants; // all local static vars are collected here
    string[] imports; // all imports for deduplication
    ExprOpts opts;

    int testCounter;
    
    int currentDispatch;
    int dispatchCount;

    int currentFor;
    int forCount;

    bool hasEmptyCtor;

    Goto[] gotos;
    int[void*] labelGotoNums;
    
    int inInitializer; // to avoid recursive decomposition of arrays
    string[] arrayInitializers;

    TemplateInstance currentInst;

    string nameOf(AggregateDeclaration agg) {
        auto tmpl = cast(void*)agg in opts.templates;
        return format("%s%s", agg.ident.symbol, tmpl ? tmpl.tiArgs : "");
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
                if (box && !p.type.isAggregate) {
                    b.fmt("%s", refType(p.type, opts));
                }
                else b.fmt("%s", toJava(p.type, opts));
            }
        return b.data.dup;
    }
    
    this() {
        buf = new TextBuffer();
        header = new TextBuffer();
        header.put("package org.dlang.dmd;\n");
        header.put("\nimport kotlin.jvm.functions.*;\n");
        header.put("\nimport org.dlang.dmd.root.*;\n");
        header.put("\nimport static org.dlang.dmd.root.filename.*;\n");
        header.put("\nimport static org.dlang.dmd.root.File.*;\n");
        header.put("\nimport static org.dlang.dmd.root.ShimsKt.*;\n");
        header.put("import static org.dlang.dmd.root.SliceKt.*;\n");
        header.put("import static org.dlang.dmd.root.DArrayKt.*;\n");
    }

    void onModuleStart(Module mod){
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
        if (stack.length && opts.vararg) {
            if (var.ident.symbol == "_arguments") return;
            if (var is opts.vararg) return;
        }
        bool staticInit = var.isStatic() || (var.storage_class & STC.gshared) || (stack.empty && aggregates.empty);
        bool refVar = stack.length && passedByRef(var, stack[$-1]) && !staticInit;
        if (refVar) opts.refParams[cast(void*)var] = true;
        Type t = var.type;
        if(var._init && var._init.kind == InitKind.array && var.type.ty == Tpointer)
            t = var.type.nextOf.arrayOf;
        auto type = refVar ? refType(var.type, opts) : toJava(t, opts);
        auto access = "";
        if (aggregates.length && !stack.length) access = defAccess ~ " ";
        auto ti = stack.length ? "" : tiArgs;
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


    override void visit(VarDeclaration var) {
        if (var.type is null) {
            stderr.writefln("NULL TYPE VAR: %s", var.ident.toString);
            return;
        }
        if (tiArgs && stack.length == 0) opts.templates[cast(void*)var] = Template(tiArgs, false);
        if (var.type.toJava(opts).startsWith("TypeInfo_")) return;
        bool pushToGlobal = (var.isStatic() || (var.storage_class & STC.gshared)) && !stack.empty;
        if (pushToGlobal) {
            auto temp = new TextBuffer();
            const(char)[] id = stack[$-1].ident.toString ~ var.ident.toString;
            printVar(var, id, temp);
            constants ~= temp.data.idup;
            opts.globals[cast(void*)var] = format("%s.%s", moduleName, id);
        }
        else {
            printVar(var, var.ident.toString, buf);
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
        }
        auto labels = s.statements ? 
            (*s.statements)[]
                .map!(s => s ? s.isLabelStatement() : null)
                .filter!(x => x).array 
            : null;
        auto range = new Range[labels.length];
        if (gotos.length == 0) { // do not use try/catch mechanism inside of switches
            if (s.statements)
                foreach(k, lbl; labels) {
                    labelGotoNums[cast(void*)lbl.ident] = cast(int)k;
                    foreach (i, st; *s.statements) if (st) {
                        auto gs = collectGotos(st);
                        auto nonLocalGotos = gs.filter!(g => !g.local);
                        if (nonLocalGotos.canFind!(g => g.label && g.label.ident == lbl.ident)) {
                            auto target = (*s.statements)[].countUntil!(x => x is lbl);
                            if (range[k].first > i) range[k].first = i;
                            if (range[k].last < target) range[k].last = target;
                        }

                    }
                }
        }
        if (s.statements)
            foreach (idx, st; *s.statements) if (st) {
                auto start = range.countUntil!(x => x.first == idx);
                if (start >= 0) {
                    buf.put("try {\n");
                    buf.indent;
                }
                // try to find target on this level, if fails we are too deep
                // some other (upper) check will eventually succeed
                if (auto ifs = st.isIfStatement()) {
                    if (auto c = ifs.condition.isCommaExp()) {
                        auto var = c.e1.isDeclarationExp().declaration.isVarDeclaration();
                        var.accept(this);
                    }
                }
                if (!st.isCompoundStatement() && !st.isScopeStatement()) {
                    auto lambdas = collectLambdas(st);
                    foreach (i, v; lambdas)  {
                        stderr.writefln("lambda: %d", i);
                        if (v.fd.ident.symbol !in generatedLambdas) {
                            stack ~= v.fd;
                            printLocalFunction(v.fd, true);
                            generatedLambdas[v.fd.ident.symbol] = true;
                            stack = stack[0..$-1];
                        }
                    }
                }
                auto end = range.countUntil!(x => x.last == idx);
                if (end >= 0) {
                    buf.outdent;
                    buf.fmt("}\ncatch(Dispatch%d __d){}\n", end);
                }
                st.accept(this);
                //TODO: for?
            }
    }

    override void visit(WhileStatement s)
    {
        buf.put("while (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(")\n");
        if (s._body)
            s._body.accept(this);
    }

    override void visit(DoStatement s)
    {
        buf.put("do\n");
        if (s._body)
            s._body.accept(this);
        buf.put("while (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(");\n");
    }

    override void visit(ForStatement s)
    {
        auto oldFor = currentFor;
        if (collectGotos(s._body).length > 0) {
            buf.outdent;
            currentFor = ++forCount; // start with 1, so that 0 is no for
            buf.fmt("L_outer%d:\n", forCount);
            buf.indent;
        }
        else 
            currentFor = 0; // no gotos, let it continue this inner for
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
        currentFor = oldFor;
    }

    override void visit(ForeachStatement s)
    {
        assert(false); // has been lowered
    }

    override void visit(IfStatement s)
    {
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

    override void visit(TemplateDeclaration td) {
        if (td.ident.symbol.startsWith("RTInfo")) return;
        auto old = currentInst;
        scope(exit) currentInst = old;
        foreach(inst; td.instances.values) {
            currentInst = inst;
            inst.accept(this);
        }
    }

    override void visit(TemplateInstance ti) {
        if (!currentInst) return;
        if (ti.tiargs) {
            auto decl = ti.tempdecl.isTemplateDeclaration();
            buf.indent;
            foreach(m; *ti.members) {
                buf.fmt("// from template %s!(", ti.name.symbol);
                foreach(i, arg; (*ti.tiargs)[]) {
                    if (i) buf.put(",");
                    auto t = arg.isType();
                    auto e = arg.isExpression();
                    if (t) buf.put(t.toJava(opts, Boxing.yes));
                    if (e && e.type.ty == Tarray && e.type.nextOf.ty == Tchar) buf.put(e.toString);
                }
                buf.put(")\n");
                m.accept(this);
                buf.put("\n");
            }
            buf.outdent;

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
        stack ~= ctor;
        buf.put("static {\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
        stack = stack[0..$-1];
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
        if (stack.length) {
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
        if (stack.length) {
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
        auto oldGotos = gotos;
        gotos = collectGotos(s);
        scope(exit) gotos = oldGotos;
        //if (gotos.length) stderr.writefln("GOTOS: %s", gotos);
        auto oldDispatchCount = currentDispatch;
        scope(exit) currentDispatch = oldDispatchCount;
        currentDispatch = dispatchCount++;
        if (gotos) {
            buf.put("{\n");
            buf.indent;
            buf.fmt("int __dispatch%d = 0;\n", currentDispatch);
            buf.fmt("dispatched_%d:\n", currentDispatch);
            buf.put("do {\n");
            buf.indent;
        }
        auto cond = s.condition.toJava(opts);
        if (s.condition.type.toJava(opts) == "byte") {
            cond = "(" ~ cond ~" & 0xFF)";
        }
        buf.put("switch (");
        if (gotos) {
            buf.fmt("__dispatch%d != 0 ? __dispatch%d : %s", 
                currentDispatch, currentDispatch, cond);
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
        if (gotos) {
            buf.outdent;
            buf.fmt("} while(__dispatch%d != 0);\n", currentDispatch);
            buf.outdent;
            buf.put("}\n");
        }
    }

    override void visit(CaseStatement s)
    {
        buf.put("case ");
        buf.put(s.exp.toJava(opts));
        buf.put(':');
        buf.put('\n');
        buf.indent;
        if (auto ss = s.statement.isScopeStatement()){
            if (ss.statement) ss.statement.accept(this);
        }
        else
            if (s.statement) s.statement.accept(this);
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
        s.statement.accept(this);
    }

    override void visit(LabelStatement label) {
        buf.outdent;
        buf.fmt("/*%s:*/\n", label.ident.symbol);
        long myIndex = gotos.countUntil!(c => c.label && c.label.ident == label.ident);
        if (myIndex >= 0 && gotos[myIndex].local && currentDispatch > 0) {
            buf.fmt("case %d:\n__dispatch%d = 0;\n", -1-myIndex, currentDispatch);
        }
        buf.indent;
        super.visit(label);
    }

    override void visit(GotoStatement g) {
        long myIndex = gotos.countUntil!(c => c.label is g.label);
        buf.fmt("/*goto %s*/", g.label.toString);
        if (myIndex >= 0 && gotos[myIndex].local) {
            buf.fmt("{ __dispatch%d = %d; continue dispatched_%d; }\n",
                currentDispatch, -1-myIndex, currentDispatch);
        }
        else if (auto count = cast(void*)g.label.ident in labelGotoNums){
            buf.fmt("throw Dispatch%d.INSTANCE;\n", *count);
        }
        else
            buf.put("throw Dispatch.INSTANCE;\n");
    }

    override void visit(GotoDefaultStatement s)
    {
        long myIndex = gotos.countUntil!(c => c.default_);
        buf.put("/*goto default*/ ");
        if (myIndex >= 0) {
            buf.fmt("{ __dispatch%d = %d; continue dispatched_%d; }\n", 
                currentDispatch, -1-myIndex, currentDispatch);
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
                currentDispatch, s.exp.toJava(opts), currentDispatch);
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
        else if(currentFor != 0) {
            buf.put(' ');
            buf.fmt("L_outer%d", currentFor);
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
        if (stack.length && !stack[$-1].isCtorDeclaration()) {
            buf.put("return ");
            if (s.exp) {
                auto retType = stack[$-1].type.nextOf();
                auto oldOpts = opts;
                scope(exit) opts = oldOpts;
                opts.wantCharPtr = retType.ty == Tpointer && retType.nextOf().ty == Tchar;
                buf.put(s.exp.toJava(opts));
            }
            buf.put(";\n");
        }
    }

    override void visit(StructDeclaration d)
    {
        if (stack.length) return; // inner structs are done separately
        aggregates ~= d;
        auto oldInAggregate = opts.inAggregate;
        scope(exit) opts.inAggregate = oldInAggregate;
        opts.inAggregate = aggregates;
        auto oldInst = currentInst;
        scope(exit) currentInst = oldInst; 
        auto tiargs = currentInst ? tiArgs() : "";
        if (currentInst) {
            if (currentInst.tiargs)
                foreach (arg; *currentInst.tiargs) {
                    if (auto t = arg.isType()) {
                        stderr.writefln("tiarg %s %s\n", t.toString, t.kind[0..strlen(t.kind)]);
                        auto tc = t.isTypeClass();
                        // if (tc) 
                        if (tc && tc.sym.getModule() !is d.getModule()) {
                            addImport(tc.sym.getModule.md.packages, tc.sym.getModule.ident);
                        }
                        auto ts = t.isTypeStruct();
                        if (ts && ts.sym.getModule() !is d.getModule()) {
                            addImport(ts.sym.getModule.md.packages, ts.sym.getModule.ident);
                        }
                    }
                }
        }
        currentInst = null;
        if (tiargs.length) opts.templates[cast(void*)d] = Template(tiargs, false);

        stderr.writefln("Struct %s", d);
        buf.fmt("%s static class ", defAccess);
        if (!d.isAnonymous()) {
            buf.put(d.ident.symbol);
            buf.put(tiargs);
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
        auto members = collectMembers(d);
        // .init ctor
        buf.fmt("public %s(){\n", d.ident.symbol);
        buf.indent;
        foreach(m; members.all) {
            if (auto ts = m.type.isTypeStruct()) {
                buf.fmt("%s = new %s();\n", m.ident.symbol, nameOf(ts.sym));
            }
        }
        buf.outdent;
        buf.put("}\n");
        // default shallow copy for structs
        buf.fmt("public %s copy(){\n", nameOf(d));
        buf.indent;
        buf.fmt("%s r = new %s();\n", nameOf(d), nameOf(d));
        foreach(m; members.all) {
            if (m.type.ty == Tstruct || m.type.ty == Tarray) {
                buf.fmt("r.%s = %s.copy();\n", m.ident.symbol, m.ident.symbol);
            }
            else
                buf.fmt("r.%s = %s;\n", m.ident.symbol, m.ident.symbol);
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
                        buf.fmt("this.%s = %s;\n", m.ident.toString, m.ident.toString);
                    }
                    buf.outdent;
                    buf.put("}\n\n");
                }
                // generate opAssign
                buf.fmt("public %s opAssign(%s that) {\n", nameOf(d), nameOf(d));
                buf.indent;
                foreach(i,m; members.all){
                    buf.fmt("this.%s = that.%s;\n", m.ident.toString, m.ident.toString);
                }
                buf.put("return this;\n");
                buf.outdent;
                buf.put("}\n");
                
            }
        }
        buf.outdent;
        buf.put('}');
        buf.put('\n');
        aggregates = aggregates[0..$-1];
    }
    
    override void visit(ClassDeclaration d)
    {
        auto old = generatedFunctions;
        scope(exit) generatedFunctions = old;
        generatedFunctions = null;
        if (stack.length) return; // inner classes are done separately
        aggregates ~= d;
        
        auto oldInAggregate = opts.inAggregate;
        scope(exit) opts.inAggregate = oldInAggregate;
        opts.inAggregate = aggregates;
        auto oldInst = currentInst;
        scope(exit) currentInst = oldInst; 
        auto tiargs = currentInst ? tiArgs() : "";
        if (currentInst) {
            if (currentInst.tiargs)
                foreach (arg; *currentInst.tiargs) {
                    if (auto t = arg.isType()) {
                        stderr.writefln("tiarg %s %s\n", t.toString, t.kind[0..strlen(t.kind)]);
                        auto tc = t.isTypeClass();
                        // if (tc) 
                        if (tc && tc.sym.getModule() !is d.getModule()) {
                            addImport(tc.sym.getModule.md.packages, tc.sym.getModule.ident);
                        }
                        auto ts = t.isTypeStruct();
                        if (ts && ts.sym.getModule() !is d.getModule()) {
                            addImport(ts.sym.getModule.md.packages, ts.sym.getModule.ident);
                        }
                    }
                }
        }
        currentInst = null;
        if (tiargs.length) opts.templates[cast(void*)d] = Template(tiargs, false);

        stderr.writefln("Class %s", d);
        if (!d.isAnonymous())
        {
            auto abs =  d.isAbstract ? "abstract " : "";
            buf.fmt("%s static %sclass %s", defAccess, abs, nameOf(d));
        }
        visitBase(d);
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
            if (!hasEmptyCtor) buf.fmt("\nprotected %s() {}\n", nameOf(d));
            // generate copy
            auto members = collectMembers(d, true);
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
            buf.outdent;
            buf.put('}');
        }
        else
            buf.put(';');
        buf.put('\n');
        aggregates = aggregates[0..$-1];
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
        stack ~= func;
        if (func.fbody) {
            buf.fmt("public static void test_%d() {\n", testCounter++);
            buf.indent;    
            func.fbody.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
        stack = stack[0..$-1];
    }

    string tiArgs() {
        if (currentInst is null || currentInst.tiargs is null) return "";
        else {
            auto temp = new TextBuffer();
            foreach (arg; *currentInst.tiargs) {
                auto t = arg.isType();
                auto e = arg.isExpression();
                if (!t) stderr.writefln("Non-type template arg: %s", arg.toString);
                if (e && e.type.toJava(opts) == "ByteSlice") temp.fmt("_%s", e.toString[1..$-1]);
                if (t) temp.put(t.toJava(opts, Boxing.yes));
            }
            return temp.data.dup;
        }
    }

    void printLocalFunction(FuncDeclaration func, bool isLambda = false) {
        auto t = func.type.isTypeFunction();
        //stderr.writefln("\tLocal function %s", func.ident.toString);
        buf.fmt("%s %s%s = new %s(){\n", t.toJavaFunc(opts), func.funcName, tiArgs, t.toJavaFunc(opts));
        buf.indent;
        buf.fmt("public %s invoke(", t.nextOf.toJava(opts, Boxing.yes));
        if (func.parameters) {
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !isLambda && !isAggregate(p.type) && p.type.ty != Tpointer && p.type.ty != Tarray) {
                    opts.refParams[cast(void*)p] = true;
                    buf.fmt("%s %s", refType(p.type, opts), p.ident.toString);
                }
                else buf.fmt("%s %s", toJava(p.type, opts, Boxing.yes), p.ident.toString);
            }
        }
        buf.put("){\n");
        buf.indent;
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
        auto storage = (func.isStatic()  || aggregates.length == 0) ? "static" : "";
        if (func.isAbstract && func.fbody is null) storage = "abstract";
        if (func.isCtorDeclaration())
            buf.fmt("public %s %s%s(", storage, toJava(func.type.nextOf(), opts), tiArgs);
        else
            buf.fmt("public %s %s %s%s(", storage, toJava(func.type.nextOf(), opts), func.funcName, tiArgs);
        if (func.parameters) {
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !p.type.isAggregate && p.type.ty != Tpointer && p.type.ty != Tarray) {
                    opts.refParams[cast(void*)p] = true;
                    buf.fmt("%s %s", refType(p.type, opts), p.ident.toString);
                }
                else buf.fmt("%s %s", toJava(p.type, opts), p.ident.toString);
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
                    if (box && !p.type.isAggregate && p.type.ty != Tpointer && p.type.ty != Tarray) {
                        opts.refParams[cast(void*)p] = true;
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
            super.visit(func);
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
        if (func.funcName == "copy" && aggregates.length > 0) return;
        if (func.isCtorDeclaration() && !func.parameters) hasEmptyCtor = true;
        // save tiargs before checking duplicates
        if (tiArgs.length) opts.templates[cast(void*)func] = Template(tiArgs, stack.length != 0);
        if (stack.length > 0) opts.localFuncs[cast(void*)func] = true;
        // check for duplicates
        auto sig = funcSig(func);
        if (sig in generatedFunctions) return;
        generatedFunctions[sig] = true;

        // hoist nested structs/classes to top level, mark them private
        if (stack.length == 0) {
            hoistLocalAggregates(func);
        }
        stack ~= func;

        auto oldRefParams = opts.refParams.dup;
        scope(exit) opts.refParams = oldRefParams;

        if (stack.length > 1)
            printLocalFunction(func);
        else {
            auto old = generatedFunctions;
            scope(exit) generatedFunctions = old;
            generatedFunctions = null;
            printGlobalFunction(func);
        }
        stack = stack[0..$-1];
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

