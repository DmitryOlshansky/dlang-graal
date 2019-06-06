module visitors.declaration;


import dmd.dmodule;
import dmd.expression;
import dmd.declaration;
import dmd.func;
import dmd.init;
import dmd.mtype;
import dmd.statement;
import dmd.tokens;
import dmd.visitor : SemanticTimeTransitiveVisitor;

import std.array, std.format, std.range;

import visitors.expression : ExprOpts, toKotlin;

alias fmt = formattedWrite;

///
string toKotlin(Module mod) {
    scope v = new ToKotlinModuleVisitor();
    v.moduleName = mod.ident.toString.idup;
    mod.accept(v);
    v.onModuleEnd();
    return v.result;
}

extern (C++) class ToKotlinModuleVisitor : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    Appender!(char[]) buf;
    int indent = 0;
    FuncDeclaration[] stack;
    Expression[] increments; // used in for loops to augment `continue`
    string moduleName;
    string[] constants;
    bool[] needDecorateScope = [true] ; // need to use 'run' to run scope?
    
    const(char)[] padding() { 
        auto pad = new char[](4*indent);
        pad[] = ' ';
        return pad;
    }

    void pushDecorateScope(bool value) {
        needDecorateScope ~= value;
    }

    void popDecorateScope(){ 
        needDecorateScope = needDecorateScope[0..$-1];
    }
    
    this() {
        buf = appender!(char[])();
    }

    ///
    void onModuleEnd() {
        if (constants.length)  {
            buf.fmt("object %sConstants {\n", moduleName);
            foreach(var; constants) 
                buf.fmt("%s%s", padding, var);
            buf.fmt("}\n");
        }
    }

    override void visit(VarDeclaration var) {
        void printVar(VarDeclaration var, string kind, const(char)[] ident, ref Appender!(char[]) buf) {
            buf.fmt("%s %s: %s", kind, ident, toKotlin(var.type));
            if (var._init) {
                ExpInitializer ie = var._init.isExpInitializer();
                if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit)) {
                    buf.fmt(" = ");
                    buf.put((cast(AssignExp)ie.exp).e2.toKotlin());
                }
                else {
                    buf.fmt(" = ");
                    var._init.initializerToBuffer(buf, var.type.ty == Tpointer && var.type.nextOf().ty == Tchar);
                }
            }
            buf.fmt(";\n");
        }
        if (var.type.isImmutable() && (var.storage_class & STC.static_)) {
            auto id = stack.length ? stack[$-1].ident.toString ~ var.ident.toString : var.ident.toString;
            auto temp = appender!(char[]);
            temp.fmt("    ");
            printVar(var, "val", id, temp);
            constants ~= temp.data.idup;
            buf.fmt("%sval %s: %s = %sConstants.%s\n", padding, var.ident.toString, toKotlin(var.type), moduleName, id);            
        }
        else {
            buf.fmt(padding);
            printVar(var, "var", var.ident.toString, buf);
        }
    }

    override void visit(ExpStatement s)
    {
        if (s.exp && s.exp.op == TOK.declaration &&
            (cast(DeclarationExp)s.exp).declaration)
        {
            // bypass visit(DeclarationExp)
            return super.visit((cast(DeclarationExp)s.exp).declaration);
            
        }
        if (s.exp)
            buf.put(s.exp.toKotlin);
        buf.put(";\n");
    }

    override void visit(ScopeStatement s)
    {
        if (needDecorateScope[$-1]) buf.put("run ");
        pushDecorateScope(true);
        buf.put("{\n");
        if (s.statement) {
            super.visit(s);
            s.statement.accept(this);
        }
        buf.put("}\n");
        popDecorateScope();
    }

    override void visit(WhileStatement s)
    {
        buf.put("while (");
        buf.put(s.condition.toKotlin);
        buf.put(")\n");
        pushDecorateScope(false);
        if (s._body)
            s._body.accept(this);
        popDecorateScope();
    }

    override void visit(DoStatement s)
    {
        buf.put("do\n");
        pushDecorateScope(false);
        if (s._body)
            s._body.accept(this);
        popDecorateScope();
        buf.put("while (");
        buf.put(s.condition.toKotlin);
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
            buf.put(s.condition.toKotlin);
        }
        increments ~= s.increment;
        buf.put(')');
        buf.put("{\n");
        indent++;
        if (s._body) {
            pushDecorateScope(false);
            s._body.accept(this);
            popDecorateScope();
            if (s.increment)
            {
                buf.put(s.increment.toKotlin);
            }
        }
        increments = increments[0..$-1];
        indent--;
        buf.put("}\n");
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
                buf.put(toKotlin(p.type, p.ident));
            else
                buf.put(p.ident.toString());
            buf.put(" = ");
        }
        buf.put(s.condition.toKotlin);
        buf.put(")\n");
        pushDecorateScope(false);
        scope(exit) popDecorateScope();
        if (s.ifbody.isScopeStatement())
        {
            s.ifbody.accept(this);
        }
        else
        {
            indent++;
            s.ifbody.accept(this);
            indent--;
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
                indent++;
                s.elsebody.accept(this);
                indent--;
            }
        }
    }

    override void visit(ReturnStatement s)
    {
        buf.put("return ");
        if (s.exp)
            buf.put(s.exp.toKotlin);
        buf.put(';');
        buf.put('\n');
    }

    override void visit(FuncDeclaration func)  {
        stack ~= func;
        buf.fmt("%sfun %s(", padding, func.ident.toString);
        if (func.parameters)
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                buf.fmt("%s: %s", p.ident.toString, toKotlin(p.type));
            }
        buf.fmt(")");
        if (!func.inferRetType) buf.fmt(": %s {\n", toKotlin(func.type.nextOf()));
        else buf.fmt("\n");
        indent++;
        super.visit(func);
        indent--;
        buf.fmt("%s}\n", padding);
        stack = stack[0..$-1];
    }

    string result() { return cast(string)buf.data; }
}


private void initializerToBuffer(Initializer inx, ref Appender!(char[]) buf, bool wantCharPtr)
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
        buf.fmt("arrayOf<%s>(", toKotlin(ai.type.nextOf()));
        foreach (i, ex; ai.index)
        {
            if (i)
                buf.put(", ");
            if (ex)
            {
                buf.put(ex.toKotlin);
                buf.put(':');
            }
            if (auto iz = ai.value[i])
                initializerToBuffer(iz, buf, false);
        }
        buf.put(')');
    }

    void visitExp(ExpInitializer ei)
    {
        buf.put(ei.exp.toKotlin(opts));
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
