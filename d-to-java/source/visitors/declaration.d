module visitors.declaration;

import core.stdc.stdio;

import ds.buffer;

import dmd.aggregate;
import dmd.attrib;
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
import dmd.visitor : SemanticTimeTransitiveVisitor;

import std.array, std.format, std.string, std.range;

import visitors.expression : Boxing, ExprOpts, toJava;

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

extern (C++) class toJavaModuleVisitor : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    TextBuffer buf;
    TextBuffer header;
    FuncDeclaration[] stack;
    AggregateDeclaration[] aggregates;
    string moduleName;
    string[] constants; // all local static vars are collected here
    ExprOpts opts;

    int inInitializer; // to avoid recursive decomposition of arrays
    string[] arrayInitializers;
    
    this() {
        buf = new TextBuffer();
        header = new TextBuffer();
        header.put("package org.dlang.dmd;\n");
        header.put("\nimport kotlin.jvm.functions.*;\n");
        header.put("\nimport org.dlang.dmd.root.*;\n");
        header.put("\nimport static org.dlang.dmd.root.UtilsKt.*;\n");
        header.put("import static org.dlang.dmd.root.SliceKt.*;\n");
    }

    void onModuleStart(Module mod){
        buf.fmt("\npublic class %s {\n", moduleName);
        buf.indent;
    }

    ///
    void onModuleEnd() {
        if (constants.length)  {
            fprintf(stderr, "Arrays on module end %lld\n", arrayInitializers.length);
            foreach (i, v; arrayInitializers) {
                buf.fmt("%s;\n", v);
            }
            foreach(var; constants) {
                buf.put(var);
            }
        }
        buf.outdent;
        buf.fmt("}\n");
    }

    override void visit(VarDeclaration var) {
        bool pushToGlobal = var.isStatic() || (var.storage_class & STC.gshared) || (stack.empty && aggregates.empty);
        bool printVar(VarDeclaration var, const(char)[] ident, TextBuffer buf) {
            string storage;
            bool hadInitializer = false;
            if (pushToGlobal) storage = "static ";
            buf.fmt("%s%s %s",  storage, toJava(var.type), ident);
            if (var._init) {
                ExpInitializer ie = var._init.isExpInitializer();
                if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit)) {
                    buf.fmt(" = ");
                    buf.put((cast(AssignExp)ie.exp).e2.toJava(opts));
                }
                else {
                    buf.fmt(" = ");
                    initializerToBuffer(var._init, buf, var.type.ty == Tpointer && var.type.nextOf().ty == Tchar);
                }
                hadInitializer = true;
            }
            else if(var.type.ty == Tsarray && pushToGlobal) {
                auto st = cast(TypeSArray)var.type;
                if (pushToGlobal) buf.fmt(" = new %s(new %s[%s])", var.type.toJava, var.type.nextOf.toJava, st.dim.toJava(opts));
                hadInitializer = true;
            }
            buf.fmt(";\n");
            return hadInitializer;
        }
        if (var.type.toJava.startsWith("TypeInfo_")) return;
        if (pushToGlobal) {
            auto temp = new TextBuffer();
            bool forwardVar = true;
            const(char)[] id;
            if (stack.length)
                id = stack[$-1].ident.toString ~ var.ident.toString;
            else if(aggregates.length)
                id = aggregates[$-1].ident.toString ~ var.ident.toString;
            else {
                id = var.ident.toString;
                forwardVar = false;
            }
            forwardVar &= printVar(var, id, temp);
            constants ~= temp.data.idup;
            if (forwardVar) {
                string storage = stack.empty && (var.isStatic() || (var.storage_class & STC.gshared)) ? "static " : "";
                buf.fmt("%s%s %s = %s.%s;\n", storage, toJava(var.type), var.ident.toString, moduleName, id);
            }
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
            buf.put(s.exp.toJava(opts));
            buf.put(";\n");
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
        buf.put(s.condition.toJava(opts));
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
        buf.put(s.condition.toJava(opts));
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
            buf.put(s.condition.toJava(opts));
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
        buf.put(s.condition.toJava(opts));
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
        buf.put("{\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
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
            auto s = cast(StringExp)e.ctfeInterpret();
            buf.put(s.string[0..s.len]);
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
            buf.put(d.ident.toString());
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
        buf.put(em.ident.toString());
        if (em.value)
        {
            buf.put("(");
            buf.put(em.value.toJava(opts));
            buf.put(")");
        }
    }

    override void visit(SwitchStatement s)
    {
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
    }

    override void visit(CaseStatement s)
    {
        buf.put("case ");
        buf.put(s.exp.toJava(opts));
        buf.put(':');
        buf.put('\n');
        s.statement.accept(this);
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
        buf.put("goto default;\n");
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
        buf.put("return ");
        if (s.exp) {
            auto retType = stack[$-1].type.nextOf();
            ExprOpts opts;
            opts.wantCharPtr = retType.ty == Tpointer && retType.nextOf().ty == Tchar;
            buf.put(s.exp.toJava(opts));
        }
        buf.put(";\n");
    }

    override void visit(StructDeclaration d)
    {
        aggregates ~= d;
        buf.put("static class ");
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

    override void visit(FuncDeclaration func)  {
        stack ~= func;
        opts.refParams = null;
        fprintf(stderr, "%s\n", func.ident.toChars());
        buf.fmt("public %s %s(", toJava(func.type.nextOf()), func.ident.toString);
        if (func.parameters)
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !isAggregate(p.type)) {
                    opts.refParams[p.ident.toString] = true;
                    buf.fmt("%s %s", refType(p.type), p.ident.toString);
                }
                else buf.fmt("%s %s", toJava(p.type), p.ident.toString);
            }
        buf.fmt(") {\n");
        buf.indent;
        super.visit(func);
        buf.outdent;
        buf.put('}');
        buf.put("\n\n");
        stack = stack[0..$-1];
    }

    private void initializerToBuffer(Initializer inx, TextBuffer buf, bool wantCharPtr)
    {
        auto opts = ExprOpts(wantCharPtr, null);
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
                fprintf(stderr, "Array init %lld\n", arrayInitializers.length);
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
                fprintf(stderr, "Array init end %lld\n", arrayInitializers.length);
                arrayInitializers ~= tmp.data.idup;
                buf.fmt("slice(initializer_%d)", arrayInitializers.length-1);
            }
            inInitializer--;
        }

        void visitExp(ExpInitializer ei)
        {
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

