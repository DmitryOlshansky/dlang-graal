module visitors.declaration;


import dmd.dmodule;
import dmd.expression;
import dmd.declaration;
import dmd.func;
import dmd.init;
import dmd.mtype;
import dmd.tokens;
import dmd.visitor : SemanticTimeTransitiveVisitor;

import std.array, std.format, std.range, std.regex;

import visitors.expression : toKotlin;

alias fmt = formattedWrite;

extern (C++) class ToKotlinDeclarationVisitor : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    Appender!(char[]) buf;
    int indent = 0;
    auto re = regex(`immutable\((.+?)\)`);
    
    const(char)[] typeConverter(Type dType) {
        const(char)[] type = dType.toString.replaceAll(re, "$1");
        switch(type) {
            case "char*": return "BytePtr";
            case "uint": return "Int";
            default: return type;
        }
    }
    

    const(char)[] padding() { 
        auto pad = new char[](indent);
        pad[] = ' ';
        return pad;
    }

    import std.stdio;

    this() {
        buf = appender!(char[])();
    }

    override void visit(VarDeclaration var) {
        super.visit(var);
        buf.fmt("%svar %s: %s", padding, var.ident.toString, typeConverter(var.type));
        buf.fmt(" = ");
        ExpInitializer ie = var._init.isExpInitializer();
        if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit))
            buf.put((cast(AssignExp)ie.exp).e2.toKotlin());
        else
            var._init.initializerToBuffer(buf);
    }

    override void visit(FuncDeclaration func)  {
        buf.fmt("fun %s(", func.ident.toString);
        if (func.parameters)
            foreach(i, p; (*func.parameters)[]) {
                if (i != 0) buf.fmt(", ");
                buf.fmt("%s: %s", p.ident.toString, typeConverter(p.type));
            }
        buf.fmt(")");
        if (!func.inferRetType) buf.fmt(": %s {\n", typeConverter(func.type.nextOf()));
        else buf.fmt("\n");
        indent += 4;
        super.visit(func);
        indent -= 4;
        buf.fmt("}\n");
    }

    string result() { return cast(string)buf.data; }
}


private void initializerToBuffer(Initializer inx, ref Appender!(char[]) buf)
{
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
                initializerToBuffer(iz, buf);
        }
        buf.put('}');
    }

    void visitArray(ArrayInitializer ai)
    {
        buf.put('arrayOf(');
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
                initializerToBuffer(iz, buf);
        }
        buf.put(')');
    }

    void visitExp(ExpInitializer ei)
    {
        buf.put(ei.exp.toKotlin());
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

///
string toKotlin(Module mod) {
    scope v = new ToKotlinDeclarationVisitor();
    mod.accept(v);
    return v.result;
}