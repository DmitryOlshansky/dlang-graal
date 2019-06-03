module visitors.expression;

import core.stdc.ctype;
import core.stdc.stdio;
import core.stdc.string;

import dmd.aggregate;
import dmd.aliasthis;
import dmd.arraytypes;
import dmd.cond;
import dmd.ctfeexpr;
import dmd.dclass;
import dmd.declaration;
import dmd.denum;
import dmd.dimport;
import dmd.dmodule;
import dmd.dtemplate;
import dmd.doc;
import dmd.dstruct;
import dmd.dsymbol;
import dmd.expression;
import dmd.func;
import dmd.globals;
import dmd.id;
import dmd.identifier;
import dmd.init;
import dmd.mtype;
import dmd.nspace;
import dmd.parse;
import dmd.root.outbuffer;
import dmd.root.rootobject;
import dmd.statement;
import dmd.target;
import dmd.tokens;
import dmd.utils;
import dmd.visitor;

///
extern (C++) final class ToKotlinExpressionVisitor : Visitor
{
    alias visit = Visitor.visit;
    
public:
    OutBuffer* buf;
    private EnumDeclaration inEnumDecl;


    extern (D) this(OutBuffer* buf, EnumDeclaration inEnumDecl = null)
    {
        this.buf = buf;
        this.inEnumDecl = inEnumDecl;
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
                    if (inEnumDecl != sym)  foreach(i;0 .. sym.members.dim)
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
        argsToBuffer(e.elements, buf, e.basis);
        buf.writeByte(']');
    }

    override void visit(AssocArrayLiteralExp e)
    {
        buf.writeByte('[');
        foreach (i, key; *e.keys)
        {
            if (i)
                buf.writestring(", ");
            expToBuffer(key, PREC.assign, buf);
            buf.writeByte(':');
            auto value = (*e.values)[i];
            expToBuffer(value, PREC.assign, buf);
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
            argsToBuffer(e.elements, buf);
            e.stageflags = old;
        }
        buf.writeByte(')');
    }

    override void visit(TypeExp e)
    {
        typeToBuffer(e.type, null, buf);
    }

    override void visit(ScopeExp e)
    {
        if (e.sds.isTemplateInstance())
        {
            e.sds.dsymbolToBuffer(buf);
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
            expToBuffer(e.thisexp, PREC.primary, buf);
            buf.writeByte('.');
        }
        typeToBuffer(e.newtype, null, buf);
        if (e.arguments && e.arguments.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.arguments, buf);
            buf.writeByte(')');
        }
    }

    override void visit(NewAnonClassExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf);
            buf.writeByte('.');
        }
        buf.writestring("new");
        if (e.newargs && e.newargs.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.newargs, buf);
            buf.writeByte(')');
        }
        buf.writestring(" class ");
        if (e.arguments && e.arguments.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.arguments, buf);
            buf.writeByte(')');
        }
        if (e.cd)
            e.cd.dsymbolToBuffer(buf);
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
        assert(0);
    }

    override void visit(TypeidExp e)
    {
        assert(false);
        // not used in DMD sources
    }

    override void visit(TraitsExp e)
    {
        // must handle TraitsExp by pattern-matching on 
        // the full expression
        assert(0);
        version(none) {
            buf.writestring("__traits(");
            if (e.ident)
                buf.writestring(e.ident.toString());
            if (e.args)
            {
                foreach (arg; *e.args)
                {
                    buf.writestring(", ");
                    objectToBuffer(arg, buf);
                }
            }
            buf.writeByte(')');
        }
    }

    override void visit(HaltExp e)
    {
        buf.writestring("halt");
    }

    override void visit(IsExp e)
    {
        assert(false);
        version(none) {
            buf.writestring("is(");
            typeToBuffer(e.targ, e.id, buf);
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
                typeToBuffer(e.tspec, null, buf);
            }
            if (e.parameters && e.parameters.dim)
            {
                buf.writestring(", ");
                scope v = new DsymbolPrettyPrintVisitor(buf);
                v.visitTemplateParameters(e.parameters);
            }
            buf.writeByte(')');
        }
    }

    override void visit(UnaExp e)
    {
        buf.writestring(Token.toString(e.op));
        expToBuffer(e.e1, precedence[e.op], buf);
    }

    override void visit(BinExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf);
        buf.writeByte(' ');
        buf.writestring(Token.toString(e.op));
        buf.writeByte(' ');
        expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf);
    }

    override void visit(CompileExp e)
    {
        buf.writestring("mixin(");
        argsToBuffer(e.exps, buf, null);
        buf.writeByte(')');
    }

    override void visit(ImportExp e)
    {
        buf.writestring("import(");
        expToBuffer(e.e1, PREC.assign, buf);
        buf.writeByte(')');
    }

    override void visit(AssertExp e)
    {
        buf.writestring("assert(");
        expToBuffer(e.e1, PREC.assign, buf);
        if (e.msg)
        {
            buf.writestring(", ");
            expToBuffer(e.msg, PREC.assign, buf);
        }
        buf.writeByte(')');
    }

    override void visit(DotIdExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writeByte('.');
        buf.writestring(e.ident.toString());
    }

    override void visit(DotTemplateExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writeByte('.');
        buf.writestring(e.td.toChars());
    }

    override void visit(DotVarExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writeByte('.');
        buf.writestring(e.var.toChars());
    }

    override void visit(DotTemplateInstanceExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writeByte('.');
        e.ti.dsymbolToBuffer(buf);
    }

    override void visit(DelegateExp e)
    {
        buf.writeByte('&');
        if (!e.func.isNested() || e.func.needThis())
        {
            expToBuffer(e.e1, PREC.primary, buf);
            buf.writeByte('.');
        }
        buf.writestring(e.func.toChars());
    }

    override void visit(DotTypeExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
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
            expToBuffer(e.e1, precedence[e.op], buf);
        buf.writeByte('(');
        argsToBuffer(e.arguments, buf);
        buf.writeByte(')');
    }

    override void visit(PtrExp e)
    {
        buf.writeByte('*');
        expToBuffer(e.e1, precedence[e.op], buf);
    }

    override void visit(DeleteExp e)
    {
        buf.writestring("delete ");
        expToBuffer(e.e1, precedence[e.op], buf);
    }

    override void visit(CastExp e)
    {
        buf.writestring("cast(");
        if (e.to)
            typeToBuffer(e.to, null, buf);
        else
        {
            MODtoBuffer(buf, e.mod);
        }
        buf.writeByte(')');
        expToBuffer(e.e1, precedence[e.op], buf);
    }

    override void visit(VectorExp e)
    {
        buf.writestring("cast(");
        typeToBuffer(e.to, null, buf);
        buf.writeByte(')');
        expToBuffer(e.e1, precedence[e.op], buf);
    }

    override void visit(VectorArrayExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writestring(".array");
    }

    override void visit(SliceExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf);
        buf.writeByte('[');
        if (e.upr || e.lwr)
        {
            if (e.lwr)
                sizeToBuffer(e.lwr, buf);
            else
                buf.writeByte('0');
            buf.writestring("..");
            if (e.upr)
                sizeToBuffer(e.upr, buf);
            else
                buf.writeByte('$');
        }
        buf.writeByte(']');
    }

    override void visit(ArrayLengthExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writestring(".length");
    }

    override void visit(IntervalExp e)
    {
        expToBuffer(e.lwr, PREC.assign, buf);
        buf.writestring("..");
        expToBuffer(e.upr, PREC.assign, buf);
    }

    override void visit(DelegatePtrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writestring(".ptr");
    }

    override void visit(DelegateFuncptrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writestring(".funcptr");
    }

    override void visit(ArrayExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writestring("arrayOf<");
        typeToBuffer(e.type, null, buf);
        buf.writeByte('>');
        argsToBuffer(e.arguments, buf);
        buf.writeByte(')');
    }

    override void visit(DotExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writeByte('.');
        expToBuffer(e.e2, PREC.primary, buf);
    }

    override void visit(IndexExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writeByte('[');
        sizeToBuffer(e.e2, buf);
        buf.writeByte(']');
    }

    override void visit(PostExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf);
        buf.writestring(Token.toString(e.op));
    }

    override void visit(PreExp e)
    {
        buf.writestring(Token.toString(e.op));
        expToBuffer(e.e1, precedence[e.op], buf);
    }

    override void visit(RemoveExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf);
        buf.writestring(".remove(");
        expToBuffer(e.e2, PREC.assign, buf);
        buf.writeByte(')');
    }

    override void visit(CondExp e)
    {
        buf.writestring("if (");
        expToBuffer(e.econd, PREC.oror, buf);
        buf.writestring(")");
        expToBuffer(e.e1, PREC.expr, buf);
        buf.writestring(" else ");
        expToBuffer(e.e2, PREC.cond, buf);
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


private void expressionToBuffer(Expression e, OutBuffer* buf)
{
    scope v = new ToKotlinExpressionVisitor(buf);
    e.accept(v);
}

/**************************************************
 * Write expression out to buf, but wrap it
 * in ( ) if its precedence is less than pr.
 */
private void expToBuffer(Expression e, PREC pr, OutBuffer* buf)
{
    debug
    {
        if (precedence[e.op] == PREC.zero)
            printf("precedence not defined for token '%s'\n", Token.toChars(e.op));
    }
    assert(precedence[e.op] != PREC.zero);
    assert(pr != PREC.zero);
    /* Despite precedence, we don't allow a<b<c expressions.
     * They must be parenthesized.
     */
    if (precedence[e.op] < pr || (pr == PREC.rel && precedence[e.op] == pr)
        || (pr >= PREC.or && pr <= PREC.and && precedence[e.op] == PREC.rel))
    {
        buf.writeByte('(');
        e.expressionToBuffer(buf);
        buf.writeByte(')');
    }
    else
    {
        e.expressionToBuffer(buf);
    }
}

/**************************************************
 * Write out argument list to buf.
 */
private void argsToBuffer(Expressions* expressions, OutBuffer* buf, Expression basis = null)
{
    if (!expressions || !expressions.dim)
        return;
    foreach (i, el; *expressions)
    {
        if (i)
            buf.writestring(", ");
        if (!el)
            el = basis;
        if (el)
            expToBuffer(el, PREC.assign, buf);
    }
}

private void sizeToBuffer(Expression e, OutBuffer* buf)
{
    if (e.type == Type.tsize_t)
    {
        Expression ex = (e.op == TOK.cast_ ? (cast(CastExp)e).e1 : e);
        ex = ex.optimize(WANTvalue);
        const dinteger_t uval = ex.op == TOK.int64 ? ex.toInteger() : cast(dinteger_t)-1;
        if (cast(sinteger_t)uval >= 0)
        {
            dinteger_t sizemax;
            if (target.ptrsize == 4)
                sizemax = 0xFFFFFFFFU;
            else if (target.ptrsize == 8)
                sizemax = 0xFFFFFFFFFFFFFFFFUL;
            else
                assert(0);
            if (uval <= sizemax && uval <= 0x7FFFFFFFFFFFFFFFUL)
            {
                buf.print(uval);
                return;
            }
        }
    }
    expToBuffer(e, PREC.assign, buf);
}

/**************************************************
 * An entry point to pretty-print type.
 */
private void typeToBuffer(Type t, const Identifier ident, OutBuffer* buf)
{
    if (auto tf = t.isTypeFunction())
    {
        visitFuncIdentWithPrefix(tf, ident, null, buf);
        return;
    }
    typeToBufferx(t, buf);
    if (ident)
    {
        buf.writeByte(' ');
        buf.writestring(ident.toString());
    }
}

private void typeToBufferx(Type t, OutBuffer* buf)
{
    void visitType(Type t)
    {
        printf("t = %p, ty = %d\n", t, t.ty);
        assert(0);
    }

    void visitError(TypeError t)
    {
        buf.writestring("_error_");
    }

    void visitBasic(TypeBasic t)
    {
        //printf("TypeBasic::toCBuffer2(t.mod = %d)\n", t.mod);
        buf.writestring(t.dstring);
    }

    void visitTraits(TypeTraits t)
    {
        //printf("TypeBasic::toCBuffer2(t.mod = %d)\n", t.mod);
        t.exp.expressionToBuffer(buf);
    }

    void visitVector(TypeVector t)
    {
        //printf("TypeVector::toCBuffer2(t.mod = %d)\n", t.mod);
        assert(0);
        version(none) {
            buf.writestring("__vector(");
            typeToBufferx(t.basetype, buf);
            buf.writestring(")");
        }
    }

    void visitSArray(TypeSArray t)
    {
        assert(0);
        version(none) {
            typeToBufferx(t.next, buf);
            buf.writeByte('[');
            sizeToBuffer(t.dim, buf);
            buf.writeByte(']');
        }
    }

    void visitDArray(TypeDArray t)
    {
        Type ut = t.castMod(0);
        if (ut.equals(Type.tstring))
            buf.writestring("ByteSlice");
        else if (ut.equals(Type.twstring))
            assert(0);
        else if (ut.equals(Type.tdstring))
            assert(0);
        else
        {
        L1:
            buf.writestring("Array<");
            typeToBufferx(t.next, buf);
            buf.writestring(">");
        }
    }

    void visitAArray(TypeAArray t)
    {
        buf.writestring("AA<");
        typeToBufferx(t.next, buf);
        buf.writeByte(',');
        typeToBufferx(t.index, buf);
        buf.writeByte('>');
    }

    void visitPointer(TypePointer t)
    {
        //printf("TypePointer::toCBuffer2() next = %d\n", t.next.ty);
        if (t.next.ty == Tfunction)
            visitFuncIdentWithPostfix(cast(TypeFunction)t.next, "function", buf);
        else
        {
            typeToBufferx(t.next, buf);
            buf.writeByte('?');
        }
    }

    void visitReference(TypeReference t)
    {
        assert(0);
        version(none) {
            typeToBufferx(t.next, buf);
            buf.writeByte('&');
        }
    }

    void visitFunction(TypeFunction t)
    {
        //printf("TypeFunction::toCBuffer2() t = %p, ref = %d\n", t, t.isref);
        visitFuncIdentWithPostfix(t, null, buf);
    }

    void visitDelegate(TypeDelegate t)
    {
        visitFuncIdentWithPostfix(cast(TypeFunction)t.next, "delegate", buf);
    }

    void visitTypeQualifiedHelper(TypeQualified t)
    {
        foreach (id; t.idents)
        {
            if (id.dyncast() == DYNCAST.dsymbol)
            {
                assert(false);
                version(none) {
                    buf.writeByte('.');
                    TemplateInstance ti = cast(TemplateInstance)id;
                    ti.dsymbolToBuffer(buf);
                }
            }
            else if (id.dyncast() == DYNCAST.expression)
            {
                buf.writeByte('[');
                (cast(Expression)id).expressionToBuffer(buf);
                buf.writeByte(']');
            }
            else if (id.dyncast() == DYNCAST.type)
            {
                buf.writeByte('[');
                typeToBufferx(cast(Type)id, buf);
                buf.writeByte(']');
            }
            else
            {
                buf.writeByte('.');
                buf.writestring(id.toString());
            }
        }
    }

    void visitIdentifier(TypeIdentifier t)
    {
        buf.writestring(t.ident.toString());
        visitTypeQualifiedHelper(t);
    }

    void visitInstance(TypeInstance t)
    {
        t.tempinst.dsymbolToBuffer(buf);
        visitTypeQualifiedHelper(t);
    }

    void visitTypeof(TypeTypeof t)
    {
        buf.writestring("typeof(");
        t.exp.expressionToBuffer(buf);
        buf.writeByte(')');
        visitTypeQualifiedHelper(t);
    }

    void visitReturn(TypeReturn t)
    {
        buf.writestring("typeof(return)");
        visitTypeQualifiedHelper(t);
    }

    void visitEnum(TypeEnum t)
    {
        buf.writestring(t.sym.toPrettyChars());
    }

    void visitStruct(TypeStruct t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent ? t.sym.parent.isTemplateInstance() : null;
        if (ti && ti.aliasdecl == t.sym)
            buf.writestring(ti.toPrettyChars());
        else
            buf.writestring(t.sym.toPrettyChars());
    }

    void visitClass(TypeClass t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent.isTemplateInstance();
        if (ti && ti.aliasdecl == t.sym)
            buf.writestring(ti.toPrettyChars());
        else
            buf.writestring(t.sym.toPrettyChars());
    }

    void visitTuple(TypeTuple t)
    {
        parametersToBuffer(ParameterList(t.arguments, VarArg.none), buf);
    }

    void visitSlice(TypeSlice t)
    {
        typeToBufferx(t.next, buf);
        buf.writeByte('[');
        sizeToBuffer(t.lwr, buf);
        buf.writestring(" .. ");
        sizeToBuffer(t.upr, buf);
        buf.writeByte(']');
    }

    void visitNull(TypeNull t)
    {
        buf.writestring("Any");
    }

    switch (t.ty)
    {
        default:        return t.isTypeBasic() ?
                                visitBasic(cast(TypeBasic)t) :
                                visitType(t);

        case Terror:     return visitError(cast(TypeError)t);
        case Ttraits:    return visitTraits(cast(TypeTraits)t);
        case Tvector:    return visitVector(cast(TypeVector)t);
        case Tsarray:    return visitSArray(cast(TypeSArray)t);
        case Tarray:     return visitDArray(cast(TypeDArray)t);
        case Taarray:    return visitAArray(cast(TypeAArray)t);
        case Tpointer:   return visitPointer(cast(TypePointer)t);
        case Treference: return visitReference(cast(TypeReference)t);
        case Tfunction:  return visitFunction(cast(TypeFunction)t);
        case Tdelegate:  return visitDelegate(cast(TypeDelegate)t);
        case Tident:     return visitIdentifier(cast(TypeIdentifier)t);
        case Tinstance:  return visitInstance(cast(TypeInstance)t);
        case Ttypeof:    return visitTypeof(cast(TypeTypeof)t);
        case Treturn:    return visitReturn(cast(TypeReturn)t);
        case Tenum:      return visitEnum(cast(TypeEnum)t);
        case Tstruct:    return visitStruct(cast(TypeStruct)t);
        case Tclass:     return visitClass(cast(TypeClass)t);
        case Ttuple:     return visitTuple (cast(TypeTuple)t);
        case Tslice:     return visitSlice(cast(TypeSlice)t);
        case Tnull:      return visitNull(cast(TypeNull)t);
    }
}

private void parametersToBuffer(ParameterList pl, OutBuffer* buf)
{
    buf.writeByte('(');
    foreach (i; 0 .. pl.length)
    {
        if (i)
            buf.writestring(", ");
        pl[i].parameterToBuffer(buf);
    }
    final switch (pl.varargs)
    {
        case VarArg.none:
            break;

        case VarArg.variadic:
            if (pl.length == 0)
                goto case VarArg.typesafe;
            buf.writestring(", ...");
            break;

        case VarArg.typesafe:
            buf.writestring("...");
            break;
    }
    buf.writeByte(')');
}

private void visitFuncIdentWithPostfix(TypeFunction t, const char[] ident, OutBuffer* buf)
{
    if (t.inuse)
    {
        t.inuse = 2; // flag error to caller
        return;
    }
    t.inuse++;
    if (ident)
        buf.writestring(ident);
    if (t.next)
    {
        if (ident)
            buf.writestring(": ");
        typeToBuffer(t.next, null, buf);
    }
    parametersToBuffer(t.parameterList, buf);

    t.inuse--;
}


private void visitFuncIdentWithPrefix(TypeFunction t, const Identifier ident, TemplateDeclaration td, OutBuffer* buf)
{
    if (t.inuse)
    {
        t.inuse = 2; // flag error to caller
        return;
    }
    t.inuse++;

    if (ident)
        buf.writestring(ident.toHChars2());
    if (ident && ident.toHChars2() != ident.toChars())
    {
        // Don't print return type for ctor, dtor, unittest, etc
    }
    else if (t.next)
    {
        if (ident)
            buf.writestring(": ");
        typeToBuffer(t.next, null, buf);
    }
    if (td)
    {
        assert(false);
        version(none) {
            buf.writeByte('(');
            foreach (i, p; *td.origParameters)
            {
                if (i)
                    buf.writestring(", ");
                p.templateParameterToBuffer(buf, hgs);
            }
            buf.writeByte(')');
        }
    }
    parametersToBuffer(t.parameterList, buf);
    t.inuse--;
}

private void dsymbolToBuffer(Dsymbol s, OutBuffer* buf) {
    buf.writestring(s.toChars());
}


/***********************************************************
 * Write parameter `p` to buffer `buf`.
 * Params:
 *      p = parameter to serialize
 *      buf = buffer to write it to
 *      hgs = context
 */
private void parameterToBuffer(Parameter p, OutBuffer* buf)
{
    if (p.type.ty == Tident &&
             (cast(TypeIdentifier)p.type).ident.toString().length > 3 &&
             strncmp((cast(TypeIdentifier)p.type).ident.toChars(), "__T", 3) == 0)
    {
        // print parameter name, instead of undetermined type parameter
        buf.writestring(p.ident.toString());
    }
    else
    {
        typeToBuffer(p.type, p.ident, buf);
    }

    if (p.defaultArg)
    {
        buf.writestring(" = ");
        p.defaultArg.expToBuffer(PREC.assign, buf);
    }
}

string toKotlin(Expression e, EnumDeclaration inEnumDecl = null) {
    OutBuffer* buf = new OutBuffer();
    scope v = new ToKotlinExpressionVisitor(buf);
    e.accept(v);
    char* p = v.buf.extractChars;
    return cast(string)p[0..strlen(p)];
}