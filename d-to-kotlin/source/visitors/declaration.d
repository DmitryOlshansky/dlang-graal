module visitors.declaration;


import dmd.dmodule;
import dmd.expression;
import dmd.declaration;
import dmd.func;
import dmd.init;
import dmd.mtype;
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
    string moduleName;
    string[] constants;
    
    const(char)[] padding() { 
        auto pad = new char[](indent);
        pad[] = ' ';
        return pad;
    }

    import std.stdio;

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
            buf.fmt(" = ");
            ExpInitializer ie = var._init.isExpInitializer();
            if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit))
                buf.put((cast(AssignExp)ie.exp).e2.toKotlin());
            else {
                var._init.initializerToBuffer(buf, var.type.ty == Tpointer && var.type.nextOf().ty == Tchar);
            }
            buf.fmt("\n");
        }
        if (var.type.isImmutable() && (var.storage_class & STC.static_)) {
            auto id = stack[$-1].ident.toString ~ var.ident.toString;
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
        indent += 4;
        super.visit(func);
        indent -= 4;
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
