module visitors.declaration;

import ds.buffer;

import dmd.dmodule;
import dmd.expression;
import dmd.declaration;
import dmd.func;
import dmd.init;
import dmd.mtype;
import dmd.statement;
import dmd.tokens;
import dmd.visitor : SemanticTimeTransitiveVisitor;

import std.array, std.format, std.string, std.range;

import visitors.expression : ExprOpts, toJava;

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
    FuncDeclaration[] stack;
    Expression[] increments; // used in for loops to augment `continue`
    string moduleName;
    string[] constants;

    int inInitializer; // to avoid recursive decomposition of arrays
    string[] arrayInitializers;
    
    this() {
        buf = new TextBuffer();
        buf.put("package org.dlang.dmd;\n");
        buf.put("\nimport org.dlang.dmd.root.*;\n");
        buf.put("\nimport static org.dlang.dmd.root.UtilsKt.*;\n");
        buf.put("import static org.dlang.dmd.root.SliceKt.*;\n");
    }

    void onModuleStart(Module mod){
        buf.fmt("\nclass %s {\n", moduleName);
        buf.indent;
    }

    ///
    void onModuleEnd() {
        if (constants.length)  {
            foreach (i, v; arrayInitializers) {
                buf.fmt("%s;\n", v);
            }
            foreach(var; constants) {  
                buf.put("static ");
                buf.put(var);
            }
            buf.outdent;
            buf.fmt("}\n");
        }
    }

    override void visit(VarDeclaration var) {
        void printVar(VarDeclaration var, string kind, const(char)[] ident, TextBuffer buf) {
            buf.fmt("%s%s %s", kind, toJava(var.type), ident);
            if (var._init) {
                ExpInitializer ie = var._init.isExpInitializer();
                if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit)) {
                    buf.fmt(" = ");
                    buf.put((cast(AssignExp)ie.exp).e2.toJava());
                }
                else {
                    buf.fmt(" = ");
                    initializerToBuffer(var._init, buf, var.type.ty == Tpointer && var.type.nextOf().ty == Tchar);
                }
            }
            buf.fmt(";\n");
        }
        if (var.type.isImmutable() && (var.storage_class & STC.static_)) {
            auto id = stack.length ? stack[$-1].ident.toString ~ var.ident.toString : var.ident.toString;
            auto temp = new TextBuffer();
            printVar(var, "final ", id, temp);
            constants ~= temp.data.idup;
            buf.fmt("%s %s = %s.%s;\n",  toJava(var.type), var.ident.toString, moduleName, id);
        }
        else {
            printVar(var, "", var.ident.toString, buf);
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
            buf.put(s.exp.toJava);
            buf.put(";\n");
        }
    }

    override void visit(ScopeStatement s)
    {
        buf.put("{\n");
        buf.indent;
        if (s.statement) 
            s.statement.accept(this);
        buf.outdent;
        buf.put("}\n");
    }

    override void visit(WhileStatement s)
    {
        buf.put("while (");
        buf.put(s.condition.toJava);
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
        buf.put(s.condition.toJava);
        buf.put(");\n");
    }

    override void visit(ForStatement s)
    {
        if (s._init)
        {
            s._init.accept(this);
        }
        buf.put("while (");
        if (s.condition)
        {
            buf.put(s.condition.toJava);
        }
        increments ~= s.increment;
        buf.put(')');
        //buf.indent;
        if (s._body) {
            s._body.accept(this);
            if (s.increment)
            {
                buf.put(s.increment.toJava);
                buf.put(";")
            }
        }
        increments = increments[0..$-1];
        //buf.outdent;
    }

    override void visit(ForeachStatement s)
    {/*
        foreachWithoutBody(s);
        buf.writeByte('{');
        buf.writenl();
        buf.level++;
        if (s._body)
            s._body.accept(this);
        buf.level--;
        buf.writeByte('}');
        buf.writenl();*/
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
        buf.put(s.condition.toJava);
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

    override void visit(ReturnStatement s)
    {
        buf.put("return ");
        if (s.exp) {
            import core.stdc.stdio;
            auto retType = stack[$-1].type.nextOf();
            ExprOpts opts;
            opts.wantCharPtr = retType.ty == Tpointer && retType.nextOf().ty == Tchar;
            buf.put(s.exp.toJava(opts));
        }
        buf.put(";\n");
    }

    override void visit(FuncDeclaration func)  {
        stack ~= func;
        buf.fmt("%s %s(", toJava(func.type.nextOf()), func.ident.toString);
        if (func.parameters)
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                buf.fmt("%s %s", toJava(p.type), p.ident.toString);
            }
        buf.fmt(") {\n");
        buf.indent;
        super.visit(func);
        buf.outdent;
        buf.put('}');
        buf.put('\n');
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
                while(t.ty == Tarray || t.ty == Tsarray) {
                    suffix ~= "[]";
                    t = t.nextOf();
                }
                tmp.fmt("private static final %s%s initializer_%d = ", t.toJava, suffix, arrayInitializers.length);
            }
            tmp.fmt("{", ai.type.nextOf());
            foreach (i, ex; ai.index)
            {
                if (i)
                    tmp.put(", ");
                if (ex)
                {
                    tmp.put(ex.toJava);
                    tmp.put(':');
                }
                if (auto iz = ai.value[i])
                    initializerToBuffer(iz, tmp, false);
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

    string result() { return cast(string)buf.data; }
}

