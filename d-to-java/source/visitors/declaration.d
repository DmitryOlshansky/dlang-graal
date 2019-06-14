module visitors.declaration;

import core.stdc.stdio;

import ds.buffer;

import dmd.aggregate;
import dmd.attrib;
import dmd.cond;
import dmd.dclass;
import dmd.denum;
import dmd.dimport;
import dmd.dmodule;
import dmd.dstruct;
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

import std.array, std.algorithm, std.format, std.string, std.range;

import visitors.expression : Boxing, ExprOpts, toJava, toJavaBool, toJavaFunc, isByteSized, symbol;
import visitors.passed_by_ref;

string refType(Type t) {
    if(t.ty == Tint32 || t.ty == Tuns32 || t.ty == Tdchar) {
        return "IntRef";
    }
    else {
        return "Ref<" ~ toJava(t, null, Boxing.yes) ~ ">";
    }
}

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

bool terminates(Statement s) {
    extern(C++) static class TerminatesVisitor : Visitor {
        alias visit = typeof(super).visit;
        bool terminates = false;

        override void visit(CompoundStatement) {} // do shallow visit
        override void visit(ScopeStatement) {} // do shallow visit

        override void visit(ContinueStatement ){
            terminates = true;
        }

        override void visit(BreakStatement ) {
            terminates = true;
        }

        override void visit(ReturnStatement) {
            terminates = true;
        }
    }
    scope v = new TerminatesVisitor();
    s.accept(v);
    return v.terminates;
}

VarDeclaration[] collectMembers(AggregateDeclaration agg) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        VarDeclaration[] decls = [];

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
        override void visit(FuncDeclaration ){}
        override void visit(StaticCtorDeclaration){}
        override void visit(SharedStaticCtorDeclaration){}
        override void visit(StaticAssert ) {}
        override void visit(VarDeclaration v) {
            if (!v.isStatic && !(v.storage_class & STC.gshared) && !v.ident.toString.startsWith("__"))
                decls ~= v;
        }
    }
    scope v = new Collector();
    agg.accept(v);
    return v.decls;
}

extern (C++) class toJavaModuleVisitor : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    TextBuffer buf;
    TextBuffer header;
    bool bytesInSwitch = false;
    FuncDeclaration[] stack;
    AggregateDeclaration[] aggregates;
    string moduleName;
    string[] constants; // all local static vars are collected here
    ExprOpts opts;

    int testCounter;
    
    int inInitializer; // to avoid recursive decomposition of arrays
    string[] arrayInitializers;
    
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

    extern(D) private void printVar(VarDeclaration var, const(char)[] ident, TextBuffer sink) {
        bool staticInit = var.isStatic() || (var.storage_class & STC.gshared) || (stack.empty && aggregates.empty);
        bool refVar = stack.length && passedByRef(var, stack[$-1]);
        if (refVar) opts.refParams[cast(void*)var] = true;
        auto type = refVar ? refType(var.type) : toJava(var.type);
        auto access = "";
        if (aggregates.length && !stack.length) access = "public ";
        sink.fmt("%s%s%s %s",  access, staticInit ? "static " : "", type, ident);
        if (var._init) {
            ExpInitializer ie = var._init.isExpInitializer();
            if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit)) {
                sink.fmt(" = ");
                auto assign = (cast(AssignExp)ie.exp);
                auto integer = assign.e2.isIntegerExp();
                auto isNull = assign.e2.isNullExp();
                //fprintf(stderr, "Init1 %s\n", var.toChars());
                if (integer && integer.toInteger() == 0 && var.type.ty == Tstruct){
                    sink.fmt("new %s()", var.type.toJava);
                }
                else if(var.type.ty == Tarray && isNull) {
                    if (refVar) sink.fmt("ref(new %s())", var.type.toJava);
                    else sink.fmt("new %s()", var.type.toJava);
                }
                else {
                    if (refVar) sink.fmt("ref(");
                    sink.put(assign.e2.toJava(opts));
                    if (refVar) sink.fmt(")");
                }
            }
            else {
                //fprintf(stderr, "Init2 %s\n", var.toChars());
                sink.fmt(" = ");
                initializerToBuffer(var._init, sink, var.type.ty == Tpointer && var.type.nextOf().ty == Tchar);
            }
        }
        else if(var.type.ty == Tsarray) {
            auto st = cast(TypeSArray)var.type;
            sink.fmt(" = new %s(new %s[%s])", var.type.toJava, var.type.nextOf.toJava, st.dim.toJava(opts));
        }
        sink.fmt(";\n");
    }


    override void visit(VarDeclaration var) {
        if (var.type is null) {
            fprintf(stderr, "NULL TYPE VAR: %s\n", var.ident.toChars());
            return;
        }
        if (var.type.toJava.startsWith("TypeInfo_")) return;
        bool pushToGlobal = (var.isStatic() || (var.storage_class & STC.gshared)) && !stack.empty;
        if (pushToGlobal) {
            auto temp = new TextBuffer();
            const(char)[] id = stack[$-1].ident.toString ~ var.ident.toString;
            printVar(var, id, temp);
            constants ~= temp.data.idup;
            buf.fmt("%s %s = %s.%s;\n", toJava(var.type), var.ident.toString, moduleName, id);
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
        buf.put("if (");
        if (Parameter p = s.prm)
        {
            if (p.type)
                buf.put(toJava(p.type, p.ident));
            else
                buf.put(p.ident.toString());
            buf.put(" = ");
        }
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

    override void visit(StaticAssert s)
    {
        fprintf(stderr, "%s\n", s.toChars());
        // ignore and do not recurse into
    }

    override void visit(StaticCtorDeclaration ctor)
    {
        buf.put("{\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
    }

    override void visit(SharedStaticCtorDeclaration ctor)
    {
        stack ~= ctor;
        buf.put("{\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
        stack = stack[0..$-1];
    }

    override void visit(Import imp)
    {
        if (imp.id == Id.object)
            return; // object is imported by default
        if (imp.packages && imp.packages.dim)
        {
            foreach (const pid; *imp.packages) 
            {
                if (pid.toString() == "root") return;
            }
            if((*imp.packages)[0].toString() != "dmd") return;
            header.put("import static org.dlang.");
            foreach (const pid; *imp.packages)
            {
                header.fmt("%s.", pid.toString());
            }
            header.fmt("%s.*;\n", imp.id.toString(), imp.id.toString());
        }
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
        auto oldInEnumDecl = opts.inEnumDecl;
        scope(exit) opts.inEnumDecl = oldInEnumDecl;
        opts.inEnumDecl = d;
        buf.put("\npublic enum ");
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
            buf.put(',');
            buf.put('\n');
        }
        buf.put(";\n");
        if (d.memtype)
        {
            buf.fmt("public %s value;",  d.memtype.toJava);
            buf.fmt("\n%s(%s value){ this.value = value; }\n", d.ident.toString, d.memtype.toJava);
        }
        buf.outdent;
        buf.put("}\n\n");
    }

    override void visit(EnumMember em)
    {
        buf.put(symbol(em.ident));
        if (em.value)
        {
            buf.put("(");
            buf.put(em.value.toJava(opts));
            buf.put(")");
        }
    }

    override void visit(SwitchStatement s)
    {
        bool oldBytesInSwitch = bytesInSwitch;
        if (isByteSized(s.condition))
            bytesInSwitch = true;
        else 
            bytesInSwitch = false;
        buf.put("switch (");
        buf.put(s.condition.toJava(opts));
        buf.put(')');
        buf.put('\n');
        if (s._body)
        {
            if (!s._body.isScopeStatement())
            {
                buf.put('{');
                buf.put('\n');
                buf.indent;
                s._body.accept(this);
                buf.outdent;
                buf.put('}');
                buf.put('\n');
            }
            else
            {
                s._body.accept(this);
            }
        }
        bytesInSwitch = oldBytesInSwitch;
    }

    override void visit(CaseStatement s)
    {
        buf.put("case ");
        if (bytesInSwitch) buf.put("(byte)");
        buf.put(s.exp.toJava(opts));
        buf.put(':');
        buf.put('\n');
        buf.indent;
        if (auto ss = s.statement.isScopeStatement()){
            ss.statement.accept(this);
        }
        else
            s.statement.accept(this);
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

    override void visit(GotoDefaultStatement s)
    {
        buf.put("//goto default;\n");
        auto sc = s.sw._body.isScopeStatement();
        auto stmts = sc.statement.isCompoundStatement().statements;
        bool needBreak = true;
        if (stmts) {
            foreach (i, st; *stmts) {
                if (auto case_ = st.isDefaultStatement()) {
                    auto toExpand = case_.statement.isScopeStatement().statement.isCompoundStatement();
                    if(toExpand.statements) {
                        auto last = (*toExpand.statements)[$-1];
                        needBreak = !terminates(last);
                    }
                    case_.statement.accept(this); // expand default's code here
                }
            }
        }
        if (needBreak) buf.put("break;\n");
    }

    override void visit(GotoCaseStatement s)
    {
        if (!s.exp) {
            // fallthrough
        }
        else {
            buf.put("goto case");
            if (s.exp)
            {
                buf.put(' ');
                buf.put(s.exp.toJava(opts));
            }
            buf.put(';');
            buf.put('\n');
        }
    }

    override void visit(SwitchErrorStatement s)
    {
        assert(false);
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
        if (stack.length) {
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
        aggregates ~= d;
        buf.put("public static class ");
        if (!d.isAnonymous())
            buf.put(d.toString());
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
        bool hasCtor = (*d.members)[].any!(x => x.isCtorDeclaration());
        if (!hasCtor) {
            auto members = collectMembers(d);
            if (members.length) {
                //Generate ctors
                // empty ctor
                buf.fmt("public %s(){}\n", d.ident.toString);
                // all fields ctor
                buf.fmt("public %s(", d.ident.toString);
                foreach(i, m; members) {
                    if(i) buf.put(", ");
                    buf.fmt("%s %s", m.type.toJava, m.ident.toString);
                }
                buf.put(") {\n");
                buf.indent;
                foreach(i,m; members){
                    buf.fmt("this.%s = %s;\n", m.ident.toString, m.ident.toString);
                }
                buf.outdent;
                buf.put("}\n\n");
                // generate opAssign
                buf.fmt("public %s opAssign(%s that) {\n", d.ident.toString, d.ident.toString);
                buf.indent;
                foreach(i,m; members){
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
        aggregates ~= d;
        if (!d.isAnonymous())
        {
            buf.fmt("static class %s", d.ident.toString());
        }
        visitBase(d);
        if (d.members)
        {
            buf.put("\n{\n");
            buf.indent;
            foreach (s; *d.members)
                s.accept(this);
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
            buf.put(b.type.toJava);
        }
    }

    override void visit(UnitTestDeclaration func)  {
        stack ~= func;
        buf.fmt("public static void test_%d() {\n", testCounter++);
        buf.indent;
        if (func.fbody)
            func.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
        stack = stack[0..$-1];
    }

    void printLocalFunction(FuncDeclaration func) {
        auto t = func.type.isTypeFunction();
        fprintf(stderr, "Local function %s\n", func.ident.toChars);
        buf.fmt("%s %s = new %s(){\n", t.toJavaFunc, func.ident.symbol, t.toJavaFunc);
        buf.indent;
        buf.fmt("public %s invoke(", t.nextOf.toJava(null, Boxing.yes));
        if (func.parameters) {
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !isAggregate(p.type)) {
                    opts.refParams[cast(void*)p] = true;
                    buf.fmt("%s %s", refType(p.type), p.ident.toString);
                }
                else buf.fmt("%s %s", toJava(p.type, null, Boxing.yes), p.ident.toString);
            }
        }
        buf.put("){\n");
        buf.indent;
        super.visit(func);
        buf.outdent;
        buf.fmt("}\n");
        buf.outdent;
        buf.fmt("};\n");
    }

    void printGlobalFunction(FuncDeclaration func) {
        fprintf(stderr, "%s\n", func.ident.toChars());
        auto storage = (func.isStatic()  || aggregates.length == 0) ? "static" : "";
        buf.fmt("public %s %s %s(", storage, toJava(func.type.nextOf()), func.ident.symbol);
        if (func.parameters) {
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !isAggregate(p.type)) {
                    opts.refParams[cast(void*)p] = true;
                    buf.fmt("%s %s", refType(p.type), p.ident.toString);
                }
                else buf.fmt("%s %s", toJava(p.type), p.ident.toString);
            }
        }
        buf.fmt(") {\n");
        buf.indent;
        super.visit(func);
        buf.outdent;
        buf.put('}');
        buf.put("\n\n");
    }

    override void visit(FuncDeclaration func)  {
        if (func.fbody is null) return;
        if (func.ident.toString == "opAssign") return;
        stack ~= func;
        auto oldFunc = opts.inFuncDecl;
        scope(exit) opts.inFuncDecl = oldFunc;
        opts.inFuncDecl = func;

        auto oldRefParams = opts.refParams.dup;
        scope(exit) opts.refParams = oldRefParams;

        if (stack.length > 1)
            printLocalFunction(func);
        else 
            printGlobalFunction(func);
        stack = stack[0..$-1];
    }

    private void initializerToBuffer(Initializer inx, TextBuffer buf, bool wantCharPtr)
    {
        auto opts = ExprOpts(wantCharPtr, false, null);
        void visitError(ErrorInitializer iz)
        {
            buf.fmt("__error__");
        }

        void visitVoid(VoidInitializer iz)
        {
            buf.fmt("void");
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
                    initializerToBuffer(iz, buf, false);
            }
            buf.put('}');
        }

        void visitArray(ArrayInitializer ai)
        {
            TextBuffer tmp = buf;
            inInitializer++;
            if (inInitializer == 1) {
                tmp = new TextBuffer();
                auto t = ai.type;
                string suffix = "";
                // string literals are byte arrays, exclude them
                while((t.ty == Tarray || t.ty == Tsarray) && t.nextOf.ty != Tchar) {
                    suffix ~= "[]";
                    t = t.nextOf();
                }
                tmp.fmt("private static final %s%s initializer_%d = ", t.toJava, suffix, arrayInitializers.length);
            }
            tmp.fmt("{", ai.type.nextOf());
            Initializer[] arr = new Initializer[ai.index.length];
            foreach (i, ex; ai.index) // assume dense packing
            {
                if (ex)
                {
                    auto ie = ex.isIntegerExp();
                    if (arr.length < ie.toInteger) arr.length = ie.toInteger + 1;
                    arr[ie.toInteger] = ai.value[i];
                }
                else
                    arr[i] = ai.value[i];
            }
            foreach (i, iz; arr[])
            {
                if (i)
                    tmp.put(", ");
                if (iz) initializerToBuffer(iz, tmp, false);
                else tmp.put("null");
            }
            tmp.put("}");
            if (inInitializer == 1) {
                arrayInitializers ~= tmp.data.idup;
                buf.fmt("slice(initializer_%d)", arrayInitializers.length-1);
            }
            inInitializer--;
        }

        void visitExp(ExpInitializer ei)
        {
            //fprintf(stderr, "Initializer is %s %s\n", ei.exp.toChars(), ei.exp.type.toChars());
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

