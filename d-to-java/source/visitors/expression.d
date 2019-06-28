module visitors.expression;

import core.stdc.ctype;
import core.stdc.stdio;
import core.stdc.string;

import dmd.aggregate;
import dmd.apply;
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

import std.algorithm, std.format, std.string;

import visitors.members;

struct ExprOpts {
    bool wantCharPtr = false;
    bool rawArrayLiterals = false;
    EnumDeclaration inEnumDecl = null;
    AggregateDeclaration[] inAggregate = null;
    Expression dollarValue = null;
    VarDeclaration vararg = null;
    bool[void*] refParams; //out and ref params, they must be boxed
    string[void*] globals; // basic type static vars pushed to global scope
    bool[void*] localFuncs; // functions that are local to current scope
    Template[void*] templates; // tiArg strings of template vars and funcs
}

struct Template {
    string tiArgs;
    bool local; // = true for nested function
}

///
const(char)[] funcName(FuncDeclaration f) {
    return f.ident.symbol;
}

///
const(char)[] symbol(const(char)[] s) {
    if (s == "native") return "native_";
    else if(s == "toString") return "asString";
    else if (s == "Array") return "DArray";
    else if (s == "_") return "__";
    else return s;
}

///
const(char)[] symbol(const(char)* s) {
    return symbol(s[0..strlen(s)]);
}

///
const(char)[] symbol(Identifier s) {
    return symbol(s.toString);
}

///
string toJavaFunc(TypeFunction t, ExprOpts opts)
{
    scope OutBuffer* buf = new OutBuffer();
    visitFuncIdentWithPostfix(t, buf, opts);
    buf.writeByte(0);
    char* p = buf.extractData;
    return cast(string)p[0..strlen(p)];
}

///
string toJava(Type t, ExprOpts opts, Boxing boxing = Boxing.no) {
    scope OutBuffer* buf = new OutBuffer();
    typeToBuffer(t, buf, opts, boxing);
    buf.writeByte(0);
    char* p = buf.extractData;
    auto type = cast(string)p[0..strlen(p)];
    return type;
}

///
string toJava(Expression e, ExprOpts opts) {
    scope OutBuffer* buf = new OutBuffer();
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
    buf.writeByte(0);
    char* p = v.buf.extractData;
    return cast(string)p[0..strlen(p)];
}

///
string toJavaBool(Expression e, ExprOpts opts) {
    scope OutBuffer* buf = new OutBuffer();
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
    const(char)* op = "!=";
    if (e.type && e.type.ty != Tbool) switch(e.type.ty){
        case Tpointer:
        case Tclass:
        case Tdelegate:
            buf.printf(" %s null", op);
            break;
        case Tarray:
            buf.printf(".getLength() %s 0", op);
            break;
        default:
            buf.prependbyte('(');
            buf.printf(") %s 0", op);
    }
    buf.writeByte(0);
    char* p = v.buf.extractData;
    return cast(string)p[0..strlen(p)];
}


string refType(Type t, ExprOpts opts) {
    if(t.ty == Tint32 || t.ty == Tuns32 || t.ty == Tdchar) {
        return "IntRef";
    }
    else {
        return "Ref<" ~ toJava(t, opts, Boxing.yes) ~ ">";
    }
}

private bool isJavaByte(Type t) {
    return t.ty == Tchar || t.ty == Tint8 || t.ty == Tuns8;
}


///
extern (C++) final class toJavaExpressionVisitor : Visitor
{
    alias visit = Visitor.visit;
    
public:
    OutBuffer* buf;
    private ExprOpts opts;


    extern (D) this(OutBuffer* buf, ExprOpts opts)
    {
        this.buf = buf;
        this.opts  = opts;
    }

    void printParent(Declaration var)
    {
        if (var.isMember() && (var.isStatic() || (var.storage_class & STC.gshared)))
            buf.printf("%s.", var.parent.ident.toChars());
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
            string castTarget = "";
        L1:
            switch (t.ty)
            {
            case Tenum:
                {
                    TypeEnum te = cast(TypeEnum)t;
                    auto sym = te.sym;
                    if (opts.inEnumDecl != sym)
                        foreach(i;0 .. sym.members.dim)
                        {
                            EnumMember em = cast(EnumMember) (*sym.members)[i];
                            if (em.value.toInteger == v)
                            {
                                auto s = symbol(sym.ident);
                                if (auto agg = sym.parent.isAggregateDeclaration())
                                    buf.printf("%s.", agg.ident.toChars);
                                buf.printf("%.*s.%s", s.length, s.ptr, em.ident.toChars());
                                return ;
                            }
                        }
                    t = sym.memtype;
                    goto L1;
                }
            case Tdchar:
                buf.printf("0x%05x", v);
                break;
            case Twchar:
                if(v == '\n')
                    buf.printf("'\\n'");
                else
                    buf.printf("'\\u%04x'", v);
                break;
            case Tchar:
                {
                    buf.printf("(byte)%d", cast(int)v);
                    break;
                }
            case Tint8:
                castTarget = "byte";
                goto L2;
            case Tint16:
                castTarget = "short";
                goto L2;
            case Tuns8:
                castTarget = "byte";
                goto L2;
            case Tuns16:
                castTarget = "short";
                goto L2;
            case Tint32:
            case Tuns32:
            L2:
                if (castTarget.length)
                    buf.printf("(%.*s)%d", castTarget.length, castTarget.ptr, cast(int)v);
                else
                    buf.printf("%d", cast(int)v);
                break;
            case Tint64:
            case Tuns64:
                buf.printf("%lldL", v);
                break;
            case Tbool:
                buf.writestring(v ? "true" : "false");
                break;
            case Tclass:
            case Tpointer:
                if (v == 0) buf.writestring("null");
                else assert(false);
                break;
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
        else {
            fprintf(stderr, "No type for %d\n", cast(int)v);
            buf.print(v);
        }
    }

    override void visit(ErrorExp e)
    {
        buf.writestring("__error");
    }

    override void visit(DollarExp e)
    {
        fprintf(stderr, "D %s\n", e.toChars());
        buf.writestring(e.ident.toString());
    }

    override void visit(IdentifierExp e)
    {
        fprintf(stderr, "ID %s\n", e.toChars());
        buf.writestring(e.ident.toString());
    }

    override void visit(DsymbolExp e)
    {
        fprintf(stderr, "DSymbol %s\n", e.toChars());
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
        auto t = e.type.toJava(opts);
        if (e.type.ty == Tarray) buf.printf("new %.*s()", t.length, t.ptr);
        else buf.writestring("null");
    }

    override void visit(StringExp e)
    {
        //fprintf(stderr, "WantChar = %d\n", opts.wantCharPtr ? 1 : 0);
        if (opts.wantCharPtr) buf.writestring("new BytePtr(");
        else buf.writestring(" new ByteSlice(");
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
                if (c <= 0x7F && isprint(c))
                    buf.writeByte(c);
                else if(c == '\n')
                    buf.writestring("\\n");
                else if(c == '\r')
                    buf.writestring("\\r");
                else
                    buf.printf("\\u%04x", c);
                break;
            }
        }
        buf.writeByte('"');
        buf.writeByte(')');
    }

    override void visit(ArrayLiteralExp e)
    {
        auto type = e.type.nextOf.toJava(opts);
        if (!opts.rawArrayLiterals) buf.writestring("slice(");
        buf.printf("new %.*s[]{", type.length, type.ptr);
        argsToBuffer(e.elements, buf, opts, null, e.basis);
        buf.writestring("}");
        if (!opts.rawArrayLiterals) buf.writestring(")");
    }

    override void visit(AssocArrayLiteralExp e)
    {
        buf.writeByte('[');
        foreach (i, key; *e.keys)
        {
            if (i)
                buf.writestring(", ");
            expToBuffer(key, PREC.assign, buf, opts);
            buf.writeByte(':');
            auto value = (*e.values)[i];
            expToBuffer(value, PREC.assign, buf, opts);
        }
        buf.writeByte(']');
    }

    override void visit(StructLiteralExp e)
    {
        buf.writestring("new ");
        buf.writestring(e.type.toJava(opts));
        buf.writeByte('(');
        if (e.type.toString.indexOf("Array!") < 0 && !collectMembers(e.sd).hasUnion) {
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
                structLiteralArgs(e.elements, buf, opts, e.sd);
                e.stageflags = old;
            }
        }
        buf.writeByte(')');
    }

    override void visit(TypeExp e)
    {
        typeToBuffer(e.type, buf, opts);
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
        fprintf(stderr, "TEMPLATE EXP %s\n", e.toChars);
        buf.writestring(e.td.toChars());
    }

    override void visit(NewExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, opts);
            buf.writeByte('.');
        }
        buf.writestring("new ");
        typeToBuffer(e.newtype, buf, opts);
        buf.writeByte('(');
        auto struc = e.newtype.isTypeStruct();
        if (e.type.toString.indexOf("Array!") < 0 && (!struc || !collectMembers(struc.sym).hasUnion)) { 
            if (e.arguments && e.arguments.dim)
            {
                argsToBuffer(e.arguments, buf, opts, null);
            }
        }
        buf.writeByte(')');
    }

    override void visit(NewAnonClassExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, opts);
            buf.writeByte('.');
        }
        buf.writestring("new");
        if (e.newargs && e.newargs.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.newargs, buf, opts, null);
            buf.writeByte(')');
        }
        buf.writestring(" class ");
        if (e.arguments && e.arguments.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.arguments, buf, opts, null);
            buf.writeByte(')');
        }
        if (e.cd)
            e.cd.dsymbolToBuffer(buf);
    }

    override void visit(SymOffExp e)
    {
        if (e.offset)
            buf.printf("(%s.ptr().plus(%u))", e.var.toChars(), e.offset);
        else if (e.var.isTypeInfoDeclaration())
            buf.writestring(e.var.toChars());
        else if(e.var.type.ty == Tstruct)
            buf.printf("%s", e.var.toChars());
        else {
            if (auto name = cast(void*)e.var in opts.globals)
                buf.printf("ptr(%.*s)", name.length, name.ptr);
            else
                buf.printf("ptr(%s)", e.var.toChars());
        }
    }

    override void visit(VarExp e)
    {
        if(e.var.ident.symbol == "__dollar") {
            opts.dollarValue.accept(this);
            buf.writestring(".getLength()");
        }
        else if(e.var.ident.symbol == e.type.toJava(opts)) {
            buf.printf("new %.*s()", e.var.ident.symbol.length, e.var.ident.symbol.ptr);
        }
        else if(e.var is opts.vararg) {
            buf.writestring("new Slice<>(");
            buf.writestring(e.var.ident.symbol);
            buf.writestring(")");
        }
        else {
            printParent(e.var);
            if (auto name = cast(void*)e.var in opts.globals)
                buf.writestring(*name);
            else
                buf.writestring(e.var.ident.symbol);
            if (cast(void*)e.var in opts.refParams)
                buf.writestring(".value");
        }
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
            argsToBuffer(e.exps, buf, opts, null);
            buf.writestring("))");
        }
        else
        {
            buf.writestring("tuple(");
            argsToBuffer(e.exps, buf, opts, null);
            buf.writeByte(')');
        }
    }

    override void visit(FuncExp e)
    {
        fprintf(stderr, "Func exp %s\n", e.toChars());
        buf.writestring(e.fd.funcName);
        //buf.writestring(e.fd.toChars());
    }
    
    override void visit(CommaExp c)
    {
        auto left = c.e1.toJava(opts);
        //fprintf(stderr, "comma e2 = %s\n", c.e2.toChars());
        auto right = c.e2.toJava(opts);
        if(left == "") buf.writestring(right);
        else if (auto blit = c.e1.isBlitExp()) {
            buf.writestring(blit.e1.toJava(opts));
            buf.writestring(" = ");
            buf.writestring(right);
        }
        else {
            buf.writestring("comma(");
            buf.writestring(left);
            buf.writestring(", ");
            buf.writestring(right);
            buf.writestring(")");
        }
    }

    override void visit(DeclarationExp e)
    {
        /* Normal dmd execution won't reach here - regular variable declarations
         * are handled in visit(ExpStatement), so here would be used only when
         * we'll directly call Expression.toChars() for debugging.
         */
        fprintf(stderr, "DeclarationExp");
    }

    override void visit(TypeidExp e)
    {
        //TODO: do we even need it?
        // buf.writestring(e.toChars);
        // assert(false);
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
        if (e.e1.type.ty == Tpointer) {
            if(e.op == TOK.not) {
                expToBuffer(e.e1, precedence[e.op], buf, opts);
                buf.writestring(" == null");
            }
            else if (e.op == TOK.address) {
                buf.writestring("ptr(");
                expToBuffer(e.e1, precedence[e.op], buf, opts);
                buf.writestring(")");
            }
            else {
                fprintf(stderr,"Unsupported unary pointer arithmetic: %s", e.toChars());
                assert(0);
            }
        }
        else if(e.op == TOK.address) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
        else if(e.op == TOK.not) {
            buf.writestring(Token.toString(e.op));
            buf.writeByte('(');
            buf.writestring(e.e1.toJavaBool(opts));
            buf.writeByte(')');
        }
        else {
            buf.writestring(Token.toString(e.op));
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(BinExp e)
    {
        if (e.e1.type && (e.op == TOK.equal || e.op == TOK.notEqual) && !e.e1.type.isTypeBasic && !e.e1.type.isTypeEnum) {
            expToBuffer(e.e1, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(".equals(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(")");
            return;
        }
        else if(e.op == TOK.andAnd || e.op == TOK.orOr) {
            buf.writestring(e.e1.toJavaBool(opts));
            buf.writeByte(' ');
            buf.writestring(Token.toString(e.op));
            buf.writeByte(' ');
            buf.writestring(e.e2.toJavaBool(opts));
            return;
        }
        else if(e.op == TOK.concatenate) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(".concat(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(")");
            return;
        }
        else if(e.op == TOK.concatenateAssign || e.op == TOK.concatenateElemAssign) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(".append(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(")");
            return;
        }
        // optimize away * 1 and / 1
        else if (e.op == TOK.div || e.op == TOK.mul) {
            if (auto integer = e.e2.isIntegerExp()) {
                if (integer.toInteger() == 1) {
                     expToBuffer(e.e1, precedence[e.op], buf, opts);
                    return;
                }
            }
        }
        else if(e.e1.type.ty == Tpointer && (e.e2.type.isTypeBasic() || e.e2.type.ty == Tpointer)) {
            string opName = "";
            switch(e.op) {
                case TOK.add:
                    opName = "plus";
                    break;
                case TOK.addAssign: 
                    opName = "plusAssign";
                    break;
                case TOK.min:
                    opName = "minus";
                    break;
                case TOK.minAssign:
                    opName = "minusAssign";
                    break;
                case TOK.greaterThan:
                    opName = "greaterThan";
                    break;
                case TOK.greaterOrEqual:
                    opName = "greaterOrEqual";
                    break;
                case TOK.lessThan:
                    opName = "lessThan";
                    break;
                case TOK.lessOrEqual:
                    opName = "lessOrEqual";
                    break;
                default:
            } 
            if (opName != "") {
                if (e.e1.type.nextOf.ty == Tvoid)
                    buf.writestring("((BytePtr)");
                expToBuffer(e.e1, precedence[e.op], buf, opts);
                if (e.e1.type.nextOf.ty == Tvoid)
                    buf.writestring(")");
                buf.printf(".%.*s(", opName.length, opName.ptr);
                expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
                buf.writestring(")");
                return;
            }
        }
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        buf.writeByte(' ');
        if (e.op == TOK.identity) buf.writestring("==");
        else if (e.op == TOK.notIdentity)  buf.writestring("!=");
        else buf.writestring(Token.toString(e.op));
        buf.writeByte(' ');
        expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
    }

    override void visit(CompileExp e)
    {
        buf.writestring("mixin(");
        argsToBuffer(e.exps, buf, opts, null);
        buf.writeByte(')');
    }

    override void visit(ImportExp e)
    {
        buf.writestring("import(");
        expToBuffer(e.e1, PREC.assign, buf, opts);
        buf.writeByte(')');
    }

    override void visit(AssertExp e)
    {
        if (e.e1.isIntegerExp()) {
            auto i = e.e1.isIntegerExp();
            if (i.toInteger() == 0) {
                buf.writestring("throw new AssertionError(\"Unreachable code!\")");
                return;
            }
        }
        buf.writestring("assert(");
        buf.writestring(e.e1.toJavaBool(opts));
        if (e.msg)
        {
            buf.writestring(", "); 
            expToBuffer(e.msg, PREC.assign, buf, opts);
        }
        buf.writeByte(')');
    }

    override void visit(DotIdExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.ident.toString());
    }

    override void visit(DotTemplateExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.td.toChars());
        fprintf(stderr, "DOT TEMPLATE %s\n", e.td.toChars());
    }

    override void visit(DotVarExp e)
    {
        printParent(e.var);
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.var.ident.symbol);
    }

    override void visit(DotTemplateInstanceExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        e.ti.dsymbolToBuffer(buf);
        printTiArgs(e.ti, buf, opts);
    }

    override void visit(DelegateExp e)
    {
        //fprintf(stderr, "Delegate exp %s\n", e.toChars());
        if (!e.func.isNested() || e.func.needThis())
        {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.writeByte('.');
        }
        buf.writestring(e.func.funcName);
    }

    override void visit(DotTypeExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.sym.toChars());
    }

    override void visit(CallExp e)
    {
        //fprintf(stderr, "call exp %s\n", e.e1.toChars());
        if (e.e1.op == TOK.type)
        {
            /* Avoid parens around type to prevent forbidden cast syntax:
             *   (sometype)(arg1)
             * This is ok since types in constructor calls
             * can never depend on parens anyway
             */
            e.e1.accept(this);
        }
        else {
            if (e.f && e.f.isDtorDeclaration())
                return;
            if (e.f && (e.f.ident.symbol == "va_start" || e.f.ident.symbol == "va_end"))
                return;
            if (e.f && e.f.ident.symbol == "memcpy" ) {
                auto c1 = e.arguments[0][0].isCastExp();
                auto c2 = e.arguments[0][1].isCastExp();
                // fprintf(stderr, "MEMCPY %s %s\n", e.arguments[0][0].toChars, e.arguments[0][1].toChars);
                if (c1 && c2 && c1.e1.type.ty == Tpointer && c2.e1.type.ty == Tpointer
                && c1.e1.type.nextOf.ty == Tstruct && c2.e1.type.nextOf.ty == Tstruct) {
                    expToBuffer(e.arguments[0][0], precedence[e.op], buf, opts);
                    buf.writestring(".opAssign(");
                    expToBuffer(e.arguments[0][1], precedence[e.op], buf, opts);
                    buf.writestring(")");
                    return;
                }
                else if (c1 && c2 && c1.e1.type.ty == Tclass && c2.e1.type.ty == Tclass) {
                    expToBuffer(e.arguments[0][0], precedence[e.op], buf, opts);
                    buf.writestring(" = ");
                    expToBuffer(e.arguments[0][1], precedence[e.op], buf, opts);
                    buf.writestring(".copy()");
                    return;
                }
                else if (c2 && e.arguments[0][0].type.ty == Tpointer && e.arguments[0][0].type.nextOf.ty == Tvoid) {
                    buf.writestring("memcpy((BytePtr)");
                    expToBuffer(e.arguments[0][0], precedence[e.op], buf, opts);
                    buf.writestring(", ");
                    expToBuffer(e.arguments[0][1], precedence[e.op], buf, opts);
                    buf.writestring(", ");
                    expToBuffer(e.arguments[0][2], precedence[e.op], buf, opts);
                    buf.writestring(")");
                    return;
                }
            }
            if (e.f && e.f.isCtorDeclaration()) {
                auto ctorCall = e.e1.isDotVarExp();
                auto isThis = ctorCall.e1.isThisExp();
                auto isSuper = ctorCall.e1.isSuperExp();
                //fprintf(stderr, "CTOR %s this = %d super = %d\n", e.f.toChars(), isThis ? 1:0, isSuper ? 1:0);
                if (isThis || isSuper) {
                    expToBuffer(ctorCall.e1, precedence[e.op], buf, opts);
                }
                else {
                    buf.writestring("new ");
                    buf.writestring(e.type.toJava(opts));
                }
            }
            else if (e.f && e.f.ident.symbol == "opIndex") {
                auto var = e.e1.isDotVarExp();
                if (var) expToBuffer(var.e1, PREC.primary, buf, opts);
                else expToBuffer(e.e1, PREC.primary, buf, opts);
                if (e.f.parameters.length == 1)
                    buf.writestring(".get");
                else
                    buf.writestring(".set");
            }
            else if(e.f) {
                auto var = e.e1.isDotVarExp();
                if (var) {
                    expToBuffer(var.e1, PREC.primary, buf, opts);
                    buf.writeByte('.');
                    buf.writestring(e.f.funcName);
                }
                else 
                    expToBuffer(e.e1, precedence[e.op], buf, opts);
                if (auto tmpl = cast(void*)e.f in opts.templates) {
                    buf.writestring(tmpl.tiArgs);
                }
            }
            else
                expToBuffer(e.e1, precedence[e.op], buf, opts);
            //fprintf(stderr, "Calling %x %s type %s\n", e.f, e.e1.toChars(), e.e1.type.toChars());
            if (!e.f || e.f.isNested() || cast(void*)e.f in opts.localFuncs) {
                buf.writestring(".invoke");
            }
        }
        buf.writeByte('(');
        argsToBuffer(e.arguments, buf, opts, e.f);
        buf.writeByte(')');
    }

    override void visit(PtrExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.e1.type.nextOf.ty != Tstruct && e.e1.type.nextOf.ty != Tfunction) 
            buf.writestring(".get()");
    }

    override void visit(DeleteExp e)
    {
        // no-op
    }

    override void visit(CastExp e)
    {
        if (!e.to) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            return; 
        }
        bool toInt, toLong;
        bool fromInt;
        bool complexTarget = false;
        bool fromBool = false;
        bool fromEnum = false;
        bool fromClass = false;
        bool fromVoidPtr = false;
        bool toVoidPtr = false;
        bool toVoid = false;
        bool toClass = false;
        bool fromByte = false;
        if (e.to) switch(e.to.ty) {
            case Tvoid:
                toVoid = true;
                break;
            case Tpointer:
                if (e.to.nextOf.ty == Tvoid)  {
                    toVoidPtr = true;
                    break;
                }
                if (e.to.nextOf.ty == Tstruct)
                    break;
                goto case;
            case Tarray:
                complexTarget = true;
                break;
            case Tint32:
            case Tuns32:
            case Tdchar:
                toInt = true;
                break;
            case Tint64:
            case Tuns64:
                toLong = true;
                break;
            case Tclass:
                toClass = true;
                break;
            default:
        }
        if (e.e1.type) switch(e.e1.type.ty) {
            case Tpointer:
                if (e.e1.type.nextOf.ty == Tvoid)
                    fromVoidPtr = true;
                break;
            case Tchar:
            case Tint8:
            case Tuns8:
                fromByte = true;
                break;
            case Tbool:
                fromBool = true;
                break;
            case Tclass:
                fromClass = true;
                break;
            case Tint32:
            case Tuns32:
            case Tdchar:
                fromInt = true;
                break;
            case Tenum:
                fromEnum = true;
                if (e.e1.type.isTypeEnum().memType.isJavaByte)
                    fromByte = true;
                break;
            default:
        }
        if (toVoid || toVoidPtr) expToBuffer(e.e1, precedence[e.op], buf, opts);
        else if(fromBool && (toInt || toLong)) {
            buf.writestring("(");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(" ? 1 : 0)");
        }
        else if(fromByte && toInt) {
            buf.writestring("(");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(" & 0xFF)");
        }
        else if (fromInt && toInt) expToBuffer(e.e1, precedence[e.op], buf, opts);
        else if(fromEnum) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
        else if(fromVoidPtr && toClass) {
            if (auto call = e.e1.isCallExp) {
                if (call.f && call.f.funcName == "xmalloc")
                    buf.writestring("null");
            }
            else { // simple casts
                buf.writestring("(");
                typeToBuffer(e.to, buf, opts);
                buf.writestring(")");
                expToBuffer(e.e1, precedence[e.op], buf, opts);
            }
        }
        else if(complexTarget && fromClass) {
            buf.writestring("(Object)");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
        else if (complexTarget) { // rely on toTypeName(x)
            buf.writestring("to");
            typeToBuffer(e.to, buf, opts);
            buf.writestring("(");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(")");
        }
        else { // simple casts
            buf.writestring("(");
            typeToBuffer(e.to, buf, opts);
            buf.writestring(")");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(VectorExp e)
    {
        assert(false);
    }

    override void visit(VectorArrayExp e)
    {
        assert(false);
    }

    override void visit(SliceExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.upr || e.lwr)
        {
            buf.writestring(".slice(");
            if (e.lwr)
                sizeToBuffer(e.lwr, buf, opts);
            else
                buf.writeByte('0');
            
            if (e.upr) {
                auto old = opts.dollarValue;
                scope(exit) opts.dollarValue = old;
                opts.dollarValue = e.e1;
                buf.writestring(",");
                sizeToBuffer(e.upr, buf, opts);
            }
            buf.writeByte(')');
        }
    }

    override void visit(ArrayLengthExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".getLength()");
    }

    override void visit(IntervalExp e)
    {
        expToBuffer(e.lwr, PREC.assign, buf, opts);
        buf.writestring("..");
        expToBuffer(e.upr, PREC.assign, buf, opts);
    }

    override void visit(DelegatePtrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".ptr");
    }

    override void visit(DelegateFuncptrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".funcptr");
    }

    override void visit(ArrayExp e)
    {
        buf.writestring("new ");
        typeToBuffer(e.type, buf, opts);
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('{');
        argsToBuffer(e.arguments, buf, opts, null);
        buf.writeByte('}');
    }

    override void visit(DotExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        expToBuffer(e.e2, PREC.primary, buf, opts);
    }

    override void visit(AssignExp e)
    {
        if(auto call = e.e1.isCallExp()) {
            if (call.f && call.f.ident && call.f.ident.symbol == "opIndex") {
                auto una = call.e1.isPtrExp();
                auto dotVar = una ? una : call.e1.isDotVarExp();
                if (dotVar) {
                    expToBuffer(dotVar.e1, precedence[e.op], buf, opts);
                    buf.writestring(".set(");
                    argsToBuffer(call.arguments, buf, opts, call.f);
                    if (call.arguments.length != 2) {
                        buf.writestring(", ");
                        expToBuffer(e.e2, precedence[e.op], buf, opts);
                    }
                    buf.writestring(")");
                    return;
                }
            }
        }
        if (auto assign = e.e1.isIndexExp()) {
            auto oldDollar = opts.dollarValue;
            scope(exit) opts.dollarValue = oldDollar;
            opts.dollarValue = e.e1;
            expToBuffer(assign.e1, PREC.primary, buf, opts);
            buf.writestring(".set(");
            expToBuffer(assign.e2, PREC.primary, buf, opts);
            buf.writestring(", ");
            sizeToBuffer(e.e2, buf, opts);
            buf.writeByte(')');
        }
        else if(auto pt = e.e1.isPtrExp()) {
            expToBuffer(pt.e1, PREC.primary, buf, opts);
            buf.writestring(".set(0, ");
            expToBuffer(e.e2, PREC.primary, buf, opts);
            buf.writeByte(')');
        }
        else if ((e.e1.type.ty == Tpointer && e.e1.type.nextOf.ty != Tstruct) && e.e2.type.ty == Tpointer) {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.writestring(" = ");
            if (e.e2.isNullExp()) expToBuffer(e.e2, PREC.primary, buf, opts);
            else {
                buf.writestring("pcopy(");
                auto old = opts.wantCharPtr;
                scope(exit) opts.wantCharPtr = old;
                if (e.e1.type.nextOf.ty == Tchar) {
                    opts.wantCharPtr = true;
                }
                expToBuffer(e.e2, PREC.primary, buf, opts);
                buf.writestring(")");
            }
        }
        else if(e.e1.type.ty == Tstruct && e.e2.type.ty == Tint32) {
            buf.writestring("null");
        }
        else if ((e.e1.type.ty == Tstruct || e.e1.type.ty == Tarray) 
        && (e.e2.type.ty == Tstruct || e.e2.type.ty == Tarray)) {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.writestring(" = ");
            expToBuffer(e.e2, PREC.primary, buf, opts);
            buf.writestring(".copy()");
        }
        else
            visit(cast(BinExp)e);
    }

    override void visit(IndexExp e)
    {
        auto oldDollar = opts.dollarValue;
        scope(exit) opts.dollarValue = oldDollar;
        opts.dollarValue = e.e1;
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".get(");
        sizeToBuffer(e.e2, buf, opts);
        buf.writeByte(')');
    }

    override void visit(PostExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.e1.type.ty == Tpointer) {
            if(e.op == TOK.plusPlus) {
                buf.writestring(".postInc()");
            }
            else if(e.op == TOK.minusMinus) {
                buf.writestring(".postDec()");
            }
            else 
                fprintf(stderr, "Pointer arithmetic: %s\n", e.toChars());
        }
        else 
            buf.writestring(Token.toString(e.op));
    }

    override void visit(PreExp e)
    {
        if (e.e1.type.ty == Tpointer) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            if(e.op == TOK.prePlusPlus) {
                buf.writestring(".inc()");
            }
            else if(e.op == TOK.preMinusMinus) {
                buf.writestring(".dec()");
            }
            else 
                fprintf(stderr, "Pointer arithmetic: %s\n", e.toChars());
        }
        else {
            buf.writestring(Token.toString(e.op));
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(RemoveExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".remove(");
        expToBuffer(e.e2, PREC.assign, buf, opts);
        buf.writeByte(')');
    }

    override void visit(CondExp e)
    {
        buf.writestring(e.econd.toJavaBool(opts));
        buf.writestring(" ? ");
        expToBuffer(e.e1, PREC.expr, buf, opts);
        buf.writestring(" : ");
        expToBuffer(e.e2, PREC.cond, buf, opts);
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


private void expressionToBuffer(Expression e, OutBuffer* buf, ExprOpts opts)
{
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
}

/**************************************************
 * Write expression out to buf, but wrap it
 * in ( ) if its precedence is less than pr.
 */
private void expToBuffer(Expression e, PREC pr, OutBuffer* buf, ExprOpts opts)
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
        e.expressionToBuffer(buf, opts);
        buf.writeByte(')');
    }
    else
    {
        e.expressionToBuffer(buf, opts);
    }
}

private void structLiteralArgs(Expressions* expressions, OutBuffer* buf, ExprOpts opts, StructDeclaration sd)
{
    if (!expressions || !expressions.dim)
        return;
    size_t pos = buf.offset;
    foreach (i, el; *expressions)
    {
        if (i && buf.offset != pos)
            buf.writestring(", ");
        pos = buf.offset;
        if (el) {
            auto n = el.isNullExp();
            auto members = collectMembers(sd);

            const wantChar = members.all[i].type.ty == Tpointer
                && members.all[i].type.nextOf.ty == Tchar;
            
            if(n && n.type.ty == Tarray) {
                buf.writestring("new ");
                buf.writestring(n.type.toJava(opts));
                buf.writestring("()");
            }
            else if(wantChar) {
                // fprintf(stderr, "WantChar func = %s args=%d\n", fd.ident.toChars, i);
                opts.wantCharPtr = true;
                expToBuffer(el, PREC.assign, buf, opts);
            }
            else {
                expToBuffer(el, PREC.assign, buf, opts);
            }
        }
    }
    if (expressions && buf.offset == pos) buf.offset -= 2;
}

/**************************************************
 * Write out argument list to buf.
 */
private void argsToBuffer(Expressions* expressions, OutBuffer* buf, ExprOpts opts, FuncDeclaration fd, Expression basis = null)
{
    if (!expressions || !expressions.dim)
        return;
    size_t pos = buf.offset;
    foreach (i, el; *expressions)
    {
        if (i && buf.offset != pos)
            buf.writestring(", ");
        pos = buf.offset;
        if (!el)
            el = basis;
        if (el) {
            auto var = el.isVarExp();
            auto n = el.isNullExp();

            auto refParam = fd && fd.parameters && i < fd.parameters.length
                && ((*fd.parameters)[i].isRef() || (*fd.parameters)[i].isOut());

            auto wantChar = fd && fd.parameters && i < fd.parameters.length
                && (*fd.parameters)[i].type.ty == Tpointer
                && (*fd.parameters)[i].type.nextOf.ty == Tchar;

            if (fd && var && var.type.isTypeClass() && fd.parameters && i < fd.parameters.length
            && (*fd.parameters)[i].type != var.var.type) {
                buf.writestring("(");
                buf.writestring((*fd.parameters)[i].type.toJava(opts));
                buf.writestring(")");
                buf.writestring(var.var.ident.symbol);
            }
            else if (var && (cast(void*)var.var in opts.refParams) && refParam) {
                buf.writestring(var.var.ident.toString);
            }
            else if(n && n.type.ty == Tarray) {
                buf.writestring("new ");
                buf.writestring(n.type.toJava(opts));
                buf.writestring("()");
            }
            else if(wantChar) {
                // fprintf(stderr, "WantChar func = %s args=%d\n", fd.ident.toChars, i);
                opts.wantCharPtr = true;
                expToBuffer(el, PREC.assign, buf, opts);
            }
            else {
                expToBuffer(el, PREC.assign, buf, opts);
            }
        }
    }
    if (expressions && buf.offset == pos) buf.offset -= 2;
}

private void sizeToBuffer(Expression e, OutBuffer* buf, ExprOpts opts)
{
    if (e.type == Type.tsize_t)
    {
        Expression ex = (e.op == TOK.cast_ ? (cast(CastExp)e).e1 : e);
        ex = ex.optimize(WANTvalue);
        //fprintf(stderr, "SIZE TO BUF %s \n", ex.toChars());
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
                buf.printf("%d", cast(int)uval); // Java's size_t is int
                return;
            }
        }
    }
    expToBuffer(e, PREC.assign, buf, opts);
}

/**************************************************
 * An entry point to pretty-print type.
 */
private void typeToBuffer(Type t, OutBuffer* buf, ExprOpts opts, Boxing boxing = Boxing.no)
{
    if (t is null) {
        buf.writestring("nothing");
        return;
    }
    if (auto tf = t.isTypeFunction())
    {
        visitFuncIdentWithPrefix(tf, null, buf, opts);
        return;
    }
    typeToBufferx(t, buf, opts, boxing);
}

enum Boxing {
    no = 0,
    yes
}

private void typeToBufferx(Type t, OutBuffer* buf, ExprOpts opts, Boxing boxing = Boxing.no)
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
        switch (t.ty) with(Boxing)
        {
        case Tvoid:
            if (boxing == yes) buf.writestring("Void");
            else buf.writestring("void");
            break;

        case Tint8:
        case Tuns8:
            if (boxing == yes) buf.writestring("Byte");
            else buf.writestring("byte");
            break;

        case Tint16:
        case Tuns16:
            if (boxing == yes) buf.writestring("Short");
            else buf.writestring("short");
            break;

        case Tint32:
        case Tuns32:
            if (boxing == yes) buf.writestring("Integer");
            else buf.writestring("int");
            break;

        case Tfloat32:
            if (boxing == yes) buf.writestring("Float");
            else buf.writestring("float");
            break;

        case Tfloat64:
        case Tfloat80:
            if (boxing == yes) buf.writestring("Double");
            else  buf.writestring("double");
            break;

        case Tint64:
        case Tuns64:
            if (boxing == yes) buf.writestring("Long");
            else buf.writestring("long");
            break;

        case Tbool:
            if (boxing == yes) buf.writestring("Boolean");
            else buf.writestring("boolean");
            break;

        case Tchar:
            if (boxing == yes) buf.writestring("Byte");
            else buf.writestring("byte");
            break;

        case Twchar:
            if (boxing == yes) buf.writestring("Character");
            else buf.writestring("char");
            break;

        case Tdchar:
            if (boxing == yes) buf.writestring("Integer");
            else buf.writestring("int");
            break;

        default:
            import core.stdc.stdio;
            fprintf(stderr, "%s\n", t.toChars());
            assert(0, "Unexpected type in type-conversion ");
        }
    }

    void visitTraits(TypeTraits t)
    {
        //printf("TypeBasic::toCBuffer2(t.mod = %d)\n", t.mod);
        t.exp.expressionToBuffer(buf, ExprOpts.init);
    }

    void visitVector(TypeVector t)
    {
        //printf("TypeVector::toCBuffer2(t.mod = %d)\n", t.mod);
        assert(0);
        version(none) {
            buf.writestring("__vector(");
            typeToBufferx(t.basetype, buf, opts);
            buf.writestring(")");
        }
    }

    void visitSArray(TypeSArray t)
    {
        auto et = t.next;
        if (auto e = et.isTypeEnum()) et = e.memType;
        if (et.ty == Tchar || et.ty == Tvoid || et.ty == Tuns8 || et.ty == Tint8)
            buf.writestring("ByteSlice");
        else if (et.ty == Twchar)
            buf.writestring("CharSlice");
        else if (et.ty == Tdchar)
            buf.writestring("IntSlice");
        else if(et.ty == Tint32 || t.next.ty == Tuns32)
            buf.writestring("IntSlice");
        else {
            buf.writestring("Slice<");
            typeToBufferx(et, buf, opts, Boxing.yes);
            buf.writestring(">");
        }
    }

    void visitDArray(TypeDArray t)
    {
        auto et = t.next;
        if (auto e = et.isTypeEnum()) et = e.memType;
        if (et.ty == Tchar || et.ty == Tvoid || et.ty == Tuns8 || et.ty == Tint8)
            buf.writestring("ByteSlice");
        else if (et.ty == Twchar)
            buf.writestring("CharSlice");
        else if (et.ty == Tdchar)
            buf.writestring("IntSlice");
        else if(et.ty == Tint32 || et.ty == Tuns32)
            buf.writestring("IntSlice");
        else
        {
            buf.writestring("Slice<");
            typeToBufferx(et, buf, opts, Boxing.yes);
            buf.writestring(">");
        }
    }

    void visitAArray(TypeAArray t)
    {
        buf.writestring("AA<");
        typeToBufferx(t.index, buf, opts, Boxing.yes);
        buf.writeByte(',');
        typeToBufferx(t.next, buf, opts, Boxing.yes);
        buf.writeByte('>');
    }

    void visitPointer(TypePointer t)
    {
        //printf("TypePointer::toCBuffer2() next = %d\n", t.next.ty);
        if (t.next.ty == Tfunction)
            visitFuncIdentWithPostfix(cast(TypeFunction)t.next, buf, opts);
        else
        {
            if (t.next.ty == Tvoid) 
                buf.writestring("Object");
            else if (t.next.ty == Tchar || t.next.ty == Tuns8 || t.next.ty == Tint8)
                buf.writestring("BytePtr");
            else if (t.next.ty == Twchar)
                buf.writestring("CharPtr");
            else if (t.next.ty == Tdchar) 
                buf.writestring("IntPtr");
            else if (t.next.ty == Tint32 || t.next.ty == Tuns32)
                buf.writestring("IntPtr");
            else if (t.next.ty == Tstruct)
                typeToBufferx(t.next, buf, opts, Boxing.yes);
            else {
                buf.writestring("Ptr<");
                typeToBufferx(t.next, buf, opts, Boxing.yes);
                buf.writestring(">");
            }
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
        visitFuncIdentWithPostfix(t, buf, opts);
    }

    void visitDelegate(TypeDelegate t)
    {
        visitFuncIdentWithPostfix(cast(TypeFunction)t.next, buf, opts);
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
                (cast(Expression)id).expressionToBuffer(buf, opts);
                buf.writeByte(']');
            }
            else if (id.dyncast() == DYNCAST.type)
            {
                buf.writeByte('[');
                typeToBufferx(cast(Type)id, buf, opts);
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
        t.exp.expressionToBuffer(buf, opts);
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
        buf.writestring(t.memType.toJava(opts, boxing));
    }

    void visitStruct(TypeStruct t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent ? t.sym.parent.isTemplateInstance() : null;
        if (ti && ti.aliasdecl == t.sym && t.sym.ident.symbol == "DArray") {
            buf.writestring(ti.name.symbol);
            buf.writestring("<");
            foreach(i, arg; (*ti.tiargs)[]) {
                if(i) buf.writestring(",");
                if (auto atype = isType(arg)) buf.writestring(atype.toJava(opts));
                else buf.writestring(arg.toChars());
            }
            buf.writestring(">");
        }
        else if(t.sym.ident.symbol == "__va_list_tag")
            buf.writestring("Slice<Object>");
        else {
            if (ti && ti.aliasdecl == t.sym) {
                buf.writestring(t.sym.ident.symbol);
                printTiArgs(ti, buf, opts);
                return;
            }
            auto ds = t.sym.parent.isAggregateDeclaration();
            if (ds && !opts.inAggregate.canFind!(agg => agg is ds))  {
                buf.writestring(ds.ident.symbol);
                buf.writestring(".");
                buf.writestring(t.sym.ident.symbol);
                return;
            }
            buf.writestring(t.sym.ident.symbol);
        }
    }

    void visitClass(TypeClass t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent.isTemplateInstance();
        if (ti && ti.aliasdecl == t.sym) {
            buf.writestring(t.sym.ident.symbol);
            printTiArgs(ti, buf, opts);
            return;
        }
        auto ds = t.sym.parent.isAggregateDeclaration();
        if (ds && !opts.inAggregate.canFind!(agg => agg is ds))  {
            buf.writestring(ds.ident.symbol);
            buf.writestring(".");
            buf.writestring(t.sym.ident.symbol);
            return;
        }
        buf.writestring(t.sym.ident.symbol);
    }

    void visitTuple(TypeTuple t)
    {
        parametersToBuffer(ParameterList(t.arguments, VarArg.none), buf, opts);
    }

    void visitSlice(TypeSlice t)
    {
        assert(false);
    }

    void visitNull(TypeNull t)
    {
        buf.writestring("Object");
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

private void parametersToBuffer(ParameterList pl, OutBuffer* buf, ExprOpts opts, Boxing boxing = Boxing.no)
{
    foreach (i; 0 .. pl.length)
    {
        if (i)
            buf.writestring(", ");
        pl[i].parameterToBuffer(buf, opts, boxing);
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
}


private void visitFuncIdentWithPostfix(TypeFunction t, OutBuffer* buf, ExprOpts opts)
{
    if (t.inuse)
    {
        t.inuse = 2; // flag error to caller
        return;
    }
    t.inuse++;
    buf.printf("Function%d<", t.parameterList.length);
    foreach(i, p; *t.parameterList) {
        if (i) buf.writestring(",");
        typeToBuffer(p.type, buf, opts, Boxing.yes);
    }
    if (t.parameterList && t.parameterList.length > 0) buf.writestring(",");
    if (t.next)
    {
        typeToBuffer(t.next, buf, opts, Boxing.yes);
    }
    else 
        buf.writestring("Void");
    buf.writestring(">");
    t.inuse--;
}


private void visitFuncIdentWithPrefix(TypeFunction t, TemplateDeclaration td, OutBuffer* buf, ExprOpts opts)
{
    if (t.inuse)
    {
        t.inuse = 2; // flag error to caller
        return;
    }
    t.inuse++;

    if (t.next)
    {
        typeToBuffer(t.next, buf, opts);
    }
    if (td)
    {
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
    parametersToBuffer(t.parameterList, buf, opts);
    t.inuse--;
}

private void dsymbolToBuffer(Dsymbol s, OutBuffer* buf) {
    buf.writestring(s.toChars());
}

private void printTiArgs(TemplateInstance ti, OutBuffer* buf, ExprOpts opts)
{
    if (ti.tiargs)
        foreach(arg; *ti.tiargs) {
            auto t = arg.isType();
            if (t is null) fprintf(stderr, "NON-TYPE Template parameter!\n");
            buf.writestring(t.toJava(opts, Boxing.yes));
        }
}

/***********************************************************
 * Write parameter `p` to buffer `buf`.
 * Params:
 *      p = parameter to serialize
 *      buf = buffer to write it to
 *      hgs = context
 */
private void parameterToBuffer(Parameter p, OutBuffer* buf, ExprOpts opts, Boxing boxing = Boxing.no)
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
        typeToBuffer(p.type, buf, opts, boxing);
        if (p.ident) {
            buf.writestring(" ");
            buf.writestring(p.ident.symbol);
        }
    }
    opts.wantCharPtr = p.type.ty == Tpointer && p.type.nextOf().ty == Tchar;

    if (p.defaultArg)
    {
        buf.writestring(" = ");
        p.defaultArg.expToBuffer(PREC.assign, buf, opts);
    }
}
