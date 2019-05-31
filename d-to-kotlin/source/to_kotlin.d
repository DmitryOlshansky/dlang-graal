module to_kotlin;

import dmd.visitor : SemanticTimeTransitiveVisitor;
import dmd.declaration;
import dmd.func;
import dmd.mtype;

import std.array, std.format, std.range, std.regex;

alias fmt = formattedWrite;

private extern (C++) final class ExpressionPrettyPrintVisitor : Visitor
{
    alias visit = Visitor.visit;
public:
    OutBuffer* buf;


    extern (D) this(OutBuffer* buf, HdrGenState* hgs)
    {
        this.buf = buf;
        this.hgs = hgs;
    }

    ////////////////////////////////////////////////////////////////////////////
    override void visit(Expression e)
    {
        buf.writestring(Token.toString(e.op));
    }

    override void visit(IntegerExp e)
    {
        const dinteger_t v = e.toInteger();
        if (e.type)
        {
            Type t = e.type;
        L1:
            switch (t.ty)
            {
            case Tenum:
                {
                    TypeEnum te = cast(TypeEnum)t;
                    auto sym = te.sym;
                    if (hgs.inEnumDecl != sym)  foreach(i;0 .. sym.members.dim)
                    {
                        EnumMember em = cast(EnumMember) (*sym.members)[i];
                        if (em.value.toInteger == v)
                        {
                            buf.printf("%s.%s", sym.toChars(), em.ident.toChars());
                            return ;
                        }
                    }
                    //assert(0, "We could not find the EmumMember");// for some reason it won't append char* ~ e.toChars() ~ " in " ~ sym.toChars() );
                    buf.printf("cast(%s)", te.sym.toChars());
                    t = te.sym.memtype;
                    goto L1;
                }
            case Twchar:
                // BUG: need to cast(wchar)
            case Tdchar:
                // BUG: need to cast(dchar)
                if (cast(uinteger_t)v > 0xFF)
                {
                    buf.printf("'\\U%08x'", v);
                    break;
                }
                goto case;
            case Tchar:
                {
                    size_t o = buf.offset;
                    if (v == '\'')
                        buf.writestring("'\\''");
                    else if (isprint(cast(int)v) && v != '\\')
                        buf.printf("'%c'", cast(int)v);
                    else
                        buf.printf("'\\x%02x'", cast(int)v);
                    if (hgs.ddoc)
                        escapeDdocString(buf, o);
                    break;
                }
            case Tint8:
                buf.writestring("cast(byte)");
                goto L2;
            case Tint16:
                buf.writestring("cast(short)");
                goto L2;
            case Tint32:
            L2:
                buf.printf("%d", cast(int)v);
                break;
            case Tuns8:
                buf.writestring("cast(ubyte)");
                goto L3;
            case Tuns16:
                buf.writestring("cast(ushort)");
                goto L3;
            case Tuns32:
            L3:
                buf.printf("%uu", cast(uint)v);
                break;
            case Tint64:
                buf.printf("%lldL", v);
                break;
            case Tuns64:
            L4:
                buf.printf("%lluLU", v);
                break;
            case Tbool:
                buf.writestring(v ? "true" : "false");
                break;
            case Tpointer:
                buf.writestring("cast(");
                buf.writestring(t.toChars());
                buf.writeByte(')');
                if (target.ptrsize == 4)
                    goto L3;
                else if (target.ptrsize == 8)
                    goto L4;
                else
                    assert(0);
            default:
                /* This can happen if errors, such as
                 * the type is painted on like in fromConstInitializer().
                 */
                if (!global.errors)
                {
                    assert(0);
                }
                break;
            }
        }
        else if (v & 0x8000000000000000L)
            buf.printf("0x%llx", v);
        else
            buf.print(v);
    }

    override void visit(ErrorExp e)
    {
        buf.writestring("__error");
    }

    override void visit(IdentifierExp e)
    {
        buf.writestring(e.ident.toString());
    }

    override void visit(DsymbolExp e)
    {
        buf.writestring(e.s.toChars());
    }

    override void visit(ThisExp e)
    {
        buf.writestring("this");
    }

    override void visit(SuperExp e)
    {
        buf.writestring("super");
    }

    override void visit(NullExp e)
    {
        buf.writestring("null");
    }

    override void visit(StringExp e)
    {
        buf.writeByte('"');
        const o = buf.offset;
        for (size_t i = 0; i < e.len; i++)
        {
            const c = e.charAt(i);
            switch (c)
            {
            case '"':
            case '\\':
                buf.writeByte('\\');
                goto default;
            default:
                if (c <= 0xFF)
                {
                    if (c <= 0x7F && isprint(c))
                        buf.writeByte(c);
                    else
                        buf.printf("\\x%02x", c);
                }
                else if (c <= 0xFFFF)
                    buf.printf("\\x%02x\\x%02x", c & 0xFF, c >> 8);
                else
                    buf.printf("\\x%02x\\x%02x\\x%02x\\x%02x", c & 0xFF, (c >> 8) & 0xFF, (c >> 16) & 0xFF, c >> 24);
                break;
            }
        }
        buf.writeByte('"');
    }

    override void visit(ArrayLiteralExp e)
    {
        buf.writeByte('[');
        argsToBuffer(e.elements, buf, hgs, e.basis);
        buf.writeByte(']');
    }

    override void visit(AssocArrayLiteralExp e)
    {
        buf.writeByte('[');
        foreach (i, key; *e.keys)
        {
            if (i)
                buf.writestring(", ");
            expToBuffer(key, PREC.assign, buf, hgs);
            buf.writeByte(':');
            auto value = (*e.values)[i];
            expToBuffer(value, PREC.assign, buf, hgs);
        }
        buf.writeByte(']');
    }

    override void visit(StructLiteralExp e)
    {
        buf.writestring(e.sd.toChars());
        buf.writeByte('(');
        // CTFE can generate struct literals that contain an AddrExp pointing
        // to themselves, need to avoid infinite recursion:
        // struct S { this(int){ this.s = &this; } S* s; }
        // const foo = new S(0);
        if (e.stageflags & stageToCBuffer)
            buf.writestring("<recursion>");
        else
        {
            const old = e.stageflags;
            e.stageflags |= stageToCBuffer;
            argsToBuffer(e.elements, buf, hgs);
            e.stageflags = old;
        }
        buf.writeByte(')');
    }

    override void visit(TypeExp e)
    {
        typeToBuffer(e.type, null, buf, hgs);
    }

    override void visit(ScopeExp e)
    {
        if (e.sds.isTemplateInstance())
        {
            e.sds.dsymbolToBuffer(buf, hgs);
        }
        else if (hgs !is null && hgs.ddoc)
        {
            // fixes bug 6491
            if (auto m = e.sds.isModule())
                buf.writestring(m.md.toChars());
            else
                buf.writestring(e.sds.toChars());
        }
        else
        {
            buf.writestring(e.sds.kind());
            buf.writeByte(' ');
            buf.writestring(e.sds.toChars());
        }
    }

    override void visit(TemplateExp e)
    {
        buf.writestring(e.td.toChars());
    }

    override void visit(NewExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, hgs);
            buf.writeByte('.');
        }
        buf.writestring("new ");
        if (e.newargs && e.newargs.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.newargs, buf, hgs);
            buf.writeByte(')');
        }
        typeToBuffer(e.newtype, null, buf, hgs);
        if (e.arguments && e.arguments.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.arguments, buf, hgs);
            buf.writeByte(')');
        }
    }

    override void visit(NewAnonClassExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, hgs);
            buf.writeByte('.');
        }
        buf.writestring("new");
        if (e.newargs && e.newargs.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.newargs, buf, hgs);
            buf.writeByte(')');
        }
        buf.writestring(" class ");
        if (e.arguments && e.arguments.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.arguments, buf, hgs);
            buf.writeByte(')');
        }
        if (e.cd)
            e.cd.dsymbolToBuffer(buf, hgs);
    }

    override void visit(SymOffExp e)
    {
        if (e.offset)
            buf.printf("(& %s+%u)", e.var.toChars(), e.offset);
        else if (e.var.isTypeInfoDeclaration())
            buf.writestring(e.var.toChars());
        else
            buf.printf("& %s", e.var.toChars());
    }

    override void visit(VarExp e)
    {
        buf.writestring(e.var.toChars());
    }

    override void visit(OverExp e)
    {
        buf.writestring(e.vars.ident.toString());
    }

    override void visit(TupleExp e)
    {
        if (e.e0)
        {
            buf.writeByte('(');
            e.e0.accept(this);
            buf.writestring(", tuple(");
            argsToBuffer(e.exps, buf);
            buf.writestring("))");
        }
        else
        {
            buf.writestring("tuple(");
            argsToBuffer(e.exps, buf);
            buf.writeByte(')');
        }
    }

    override void visit(FuncExp e)
    {
        e.fd.dsymbolToBuffer(buf);
        //buf.writestring(e.fd.toChars());
    }

    override void visit(DeclarationExp e)
    {
        /* Normal dmd execution won't reach here - regular variable declarations
         * are handled in visit(ExpStatement), so here would be used only when
         * we'll directly call Expression.toChars() for debugging.
         */
        if (e.declaration)
        {
            if (auto var = e.declaration.isVarDeclaration())
            {
            // For debugging use:
            // - Avoid printing newline.
            // - Intentionally use the format (Type var;)
            //   which isn't correct as regular D code.
                buf.writeByte('(');

                scope v = new DsymbolPrettyPrintVisitor(buf, hgs);
                v.visitVarDecl(var, false);

                buf.writeByte(';');
                buf.writeByte(')');
            }
            else e.declaration.dsymbolToBuffer(buf, hgs);
        }
    }

    override void visit(TypeidExp e)
    {
        buf.writestring("typeid(");
        objectToBuffer(e.obj, buf, hgs);
        buf.writeByte(')');
    }

    override void visit(TraitsExp e)
    {
        buf.writestring("__traits(");
        if (e.ident)
            buf.writestring(e.ident.toString());
        if (e.args)
        {
            foreach (arg; *e.args)
            {
                buf.writestring(", ");
                objectToBuffer(arg, buf, hgs);
            }
        }
        buf.writeByte(')');
    }

    override void visit(HaltExp e)
    {
        buf.writestring("halt");
    }

    override void visit(IsExp e)
    {
        buf.writestring("is(");
        typeToBuffer(e.targ, e.id, buf, hgs);
        if (e.tok2 != TOK.reserved)
        {
            buf.printf(" %s %s", Token.toChars(e.tok), Token.toChars(e.tok2));
        }
        else if (e.tspec)
        {
            if (e.tok == TOK.colon)
                buf.writestring(" : ");
            else
                buf.writestring(" == ");
            typeToBuffer(e.tspec, null, buf, hgs);
        }
        if (e.parameters && e.parameters.dim)
        {
            buf.writestring(", ");
            scope v = new DsymbolPrettyPrintVisitor(buf, hgs);
            v.visitTemplateParameters(e.parameters);
        }
        buf.writeByte(')');
    }

    override void visit(UnaExp e)
    {
        buf.writestring(Token.toString(e.op));
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
    }

    override void visit(BinExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
        buf.writeByte(' ');
        buf.writestring(Token.toString(e.op));
        buf.writeByte(' ');
        expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, hgs);
    }

    override void visit(CompileExp e)
    {
        buf.writestring("mixin(");
        argsToBuffer(e.exps, buf, hgs, null);
        buf.writeByte(')');
    }

    override void visit(ImportExp e)
    {
        buf.writestring("import(");
        expToBuffer(e.e1, PREC.assign, buf, hgs);
        buf.writeByte(')');
    }

    override void visit(AssertExp e)
    {
        buf.writestring("assert(");
        expToBuffer(e.e1, PREC.assign, buf, hgs);
        if (e.msg)
        {
            buf.writestring(", ");
            expToBuffer(e.msg, PREC.assign, buf, hgs);
        }
        buf.writeByte(')');
    }

    override void visit(DotIdExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('.');
        buf.writestring(e.ident.toString());
    }

    override void visit(DotTemplateExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('.');
        buf.writestring(e.td.toChars());
    }

    override void visit(DotVarExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('.');
        buf.writestring(e.var.toChars());
    }

    override void visit(DotTemplateInstanceExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('.');
        e.ti.dsymbolToBuffer(buf, hgs);
    }

    override void visit(DelegateExp e)
    {
        buf.writeByte('&');
        if (!e.func.isNested() || e.func.needThis())
        {
            expToBuffer(e.e1, PREC.primary, buf, hgs);
            buf.writeByte('.');
        }
        buf.writestring(e.func.toChars());
    }

    override void visit(DotTypeExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('.');
        buf.writestring(e.sym.toChars());
    }

    override void visit(CallExp e)
    {
        if (e.e1.op == TOK.type)
        {
            /* Avoid parens around type to prevent forbidden cast syntax:
             *   (sometype)(arg1)
             * This is ok since types in constructor calls
             * can never depend on parens anyway
             */
            e.e1.accept(this);
        }
        else
            expToBuffer(e.e1, precedence[e.op], buf, hgs);
        buf.writeByte('(');
        argsToBuffer(e.arguments, buf, hgs);
        buf.writeByte(')');
    }

    override void visit(PtrExp e)
    {
        buf.writeByte('*');
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
    }

    override void visit(DeleteExp e)
    {
        buf.writestring("delete ");
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
    }

    override void visit(CastExp e)
    {
        buf.writestring("cast(");
        if (e.to)
            typeToBuffer(e.to, null, buf, hgs);
        else
        {
            MODtoBuffer(buf, e.mod);
        }
        buf.writeByte(')');
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
    }

    override void visit(VectorExp e)
    {
        buf.writestring("cast(");
        typeToBuffer(e.to, null, buf, hgs);
        buf.writeByte(')');
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
    }

    override void visit(VectorArrayExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writestring(".array");
    }

    override void visit(SliceExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
        buf.writeByte('[');
        if (e.upr || e.lwr)
        {
            if (e.lwr)
                sizeToBuffer(e.lwr, buf, hgs);
            else
                buf.writeByte('0');
            buf.writestring("..");
            if (e.upr)
                sizeToBuffer(e.upr, buf, hgs);
            else
                buf.writeByte('$');
        }
        buf.writeByte(']');
    }

    override void visit(ArrayLengthExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writestring(".length");
    }

    override void visit(IntervalExp e)
    {
        expToBuffer(e.lwr, PREC.assign, buf, hgs);
        buf.writestring("..");
        expToBuffer(e.upr, PREC.assign, buf, hgs);
    }

    override void visit(DelegatePtrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writestring(".ptr");
    }

    override void visit(DelegateFuncptrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writestring(".funcptr");
    }

    override void visit(ArrayExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('[');
        argsToBuffer(e.arguments, buf, hgs);
        buf.writeByte(']');
    }

    override void visit(DotExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('.');
        expToBuffer(e.e2, PREC.primary, buf, hgs);
    }

    override void visit(IndexExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writeByte('[');
        sizeToBuffer(e.e2, buf, hgs);
        buf.writeByte(']');
    }

    override void visit(PostExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
        buf.writestring(Token.toString(e.op));
    }

    override void visit(PreExp e)
    {
        buf.writestring(Token.toString(e.op));
        expToBuffer(e.e1, precedence[e.op], buf, hgs);
    }

    override void visit(RemoveExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, hgs);
        buf.writestring(".remove(");
        expToBuffer(e.e2, PREC.assign, buf, hgs);
        buf.writeByte(')');
    }

    override void visit(CondExp e)
    {
        expToBuffer(e.econd, PREC.oror, buf, hgs);
        buf.writestring(" ? ");
        expToBuffer(e.e1, PREC.expr, buf, hgs);
        buf.writestring(" : ");
        expToBuffer(e.e2, PREC.cond, buf, hgs);
    }

    override void visit(DefaultInitExp e)
    {
        buf.writestring(Token.toString(e.subop));
    }

    override void visit(ClassReferenceExp e)
    {
        buf.writestring(e.value.toChars());
    }
}


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
        scope v = new ExpressionPrettyPrintVisitor(buf, hgs);
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