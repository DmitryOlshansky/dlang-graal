module to_kotlin;

import dmd.visitor : SemanticTimeTransitiveVisitor;
import dmd.declaration;
import dmd.func;
import dmd.mtype;

import std.array, std.format, std.range, std.regex;

alias fmt = formattedWrite;

extern (C++) class ToKotlinVisitor : SemanticTimeTransitiveVisitor {
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


    private void expressionToBuffer(Expression e)
    {
        scope v = new ExpressionPrettyPrintVisitor(buf);
        e.accept(v);
    }


    private void initializerToBuffer(Initializer inx)
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
                    buf.fmt("%s", id.toString());
                    buf.put(':');
                }
                if (auto iz = si.value[i])
                    initializerToBuffer(iz, buf, hgs);
            }
            buf.put('}');
        }

        void visitArray(ArrayInitializer ai)
        {
            buf.writeByte('[');
            foreach (i, ex; ai.index)
            {
                if (i)
                    buf.writestring(", ");
                if (ex)
                {
                    ex.expressionToBuffer(buf, hgs);
                    buf.writeByte(':');
                }
                if (auto iz = ai.value[i])
                    initializerToBuffer(iz, buf, hgs);
            }
            buf.writeByte(']');
        }

        void visitExp(ExpInitializer ei)
        {
            ei.exp.expressionToBuffer(buf, hgs);
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


    override void visit(VarDeclaration var) {
        super.visit(var);
        buf.fmt("%svar %s: %s", padding, var.ident.toString, typeConverter(var.type));
        buf.fmt(" = ");
        ExpInitializer ie = vd._init.isExpInitializer();
        if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit))
            (cast(AssignExp)ie.exp).e2.expressionToBuffer(buf, hgs);
        else
            vd._init.initializerToBuffer(buf, hgs);
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
