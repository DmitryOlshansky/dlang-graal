module visitors.expression;

import core.stdc.ctype;
import core.stdc.string;

import ds.buffer, ds.stack, ds.identity_map;

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
import dmd.statement;
import dmd.target;
import dmd.tokens;
import dmd.utils;
import dmd.visitor;
import dmd.root.rootobject;

import std.algorithm, std.format, std.stdio, std.string;

import visitors.members, visitors.templates;

struct ExprOpts {
    bool wantCharPtr = false;
    bool rawArrayLiterals = false;
    EnumDeclaration inEnumDecl = null;
    Stack!FuncDeclaration funcs; // chain of nested functions for current scope
    Stack!AggregateDeclaration aggregates; // chain of aggregates for current scope
    Expression dollarValue = null; // expression that is referenced by dollar
    VarDeclaration vararg = null; // var decl of vararg parameter
    IdentityMap!bool refParams; //out and ref params, they must be boxed
    IdentityMap!string renamed; // renamed or static vars pushed to global scope
    IdentityMap!bool localFuncs; // functions that are local to current scope
    IdentityMap!Template templates; // tiArg strings of template vars and funcs
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
    scope TextBuffer buf = new TextBuffer();
    visitFuncIdentWithPostfix(t, buf, opts);
    return buf.data.dup;
}

///
string toJava(Type t, ExprOpts opts, Boxing boxing = Boxing.no) {
    scope TextBuffer buf = new TextBuffer();
    typeToBuffer(t, buf, opts, boxing);
    return buf.data.dup;
}

///
string toJava(Expression e, ExprOpts opts) {
    scope TextBuffer buf = new TextBuffer();
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
    return buf.data.dup;
}

///
string toJavaBool(Expression e, ExprOpts opts) {
    scope TextBuffer buf = new TextBuffer();
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
    string op = "!=";
    if (e.type && e.type.ty != Tbool) switch(e.type.ty){
        case Tpointer:
        case Tclass:
        case Tdelegate:
            buf.fmt(" %s null", op);
            break;
        case Tarray:
            buf.fmt(".getLength() %s 0", op);
            break;
        default:
            return format("(%s) %s 0", buf.data, op);
    }
    return buf.data.dup;
}

string refType(Type at, ExprOpts opts) {
    auto t = at;
    if (auto et = at.isTypeEnum) {
        t = et.memType;
    }
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
    TextBuffer buf;
    private ExprOpts opts;


    extern (D) this(TextBuffer buf, ExprOpts opts)
    {
        this.buf = buf;
        this.opts  = opts;
    }

    string printParent(Dsymbol var)
    {
        TextBuffer buf = new TextBuffer;
        AggregateDeclaration[] chain;
        if (var.isThis) return "";
        while(var) {
            auto ds = var.parent.isAggregateDeclaration();
            if (ds && !opts.aggregates[].canFind!(agg => agg is ds)) 
                chain ~= ds;
            else
                break;
            var = var.parent;
        }
        foreach_reverse (p; chain)
            buf.fmt("%s.", p.ident.symbol);
        return buf.data.dup;
    }

    ////////////////////////////////////////////////////////////////////////////
    override void visit(Expression e)
    {
        buf.put(Token.toString(e.op));
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
                                buf.put(printParent(sym));
                                //if (auto agg = sym.parent.isAggregateDeclaration())
                                //    buf.fmt("%s.", agg.ident.symbol);
                                buf.fmt("%s.%s", s, em.ident.symbol);
                                return ;
                            }
                        }
                    t = sym.memtype;
                    goto L1;
                }
            case Tdchar:
                buf.fmt("0x%05x", v);
                break;
            case Twchar:
                if(v == '\n')
                    buf.fmt("'\\n'");
                else
                    buf.fmt("'\\u%04x'", v);
                break;
            case Tchar:
                {
                    buf.fmt("(byte)%d", cast(int)v);
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
                    buf.fmt("(%s)%d", castTarget, cast(int)v);
                else
                    buf.fmt("%d", cast(int)v);
                break;
            case Tint64:
            case Tuns64:
                buf.fmt("%dL", cast(long)v);
                break;
            case Tbool:
                buf.put(v ? "true" : "false");
                break;
            case Tclass:
            case Tpointer:
                if (v == 0) buf.put("null");
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
            buf.fmt("0x%x", cast(long)v);
        else {
            stderr.writefln("No type for %d\n", cast(int)v);
            buf.fmt("%s", v);
        }
    }

    override void visit(ErrorExp e)
    {
        buf.put("__error");
    }

    override void visit(DollarExp e)
    {
        //stderr.writefln("D %s\n", e.toString());
        buf.put(e.ident.symbol);
    }

    override void visit(IdentifierExp e)
    {
        //stderr.writefln("ID %s\n", e.toString());
        buf.put(e.ident.symbol);
    }

    override void visit(DsymbolExp e)
    {
        //stderr.writefln("DSymbol %s\n", e.toString());
        buf.put(e.s.toString());
    }

    override void visit(ThisExp e)
    {
        buf.put("this");
    }

    override void visit(SuperExp e)
    {
        buf.put("super");
    }

    override void visit(NullExp e)
    {
        auto t = e.type.toJava(opts);
        if (e.type.ty == Tarray) buf.fmt("new %s()", t);
        else buf.put("null");
    }

    override void visit(StringExp e)
    {
        //stderr.writefln("WantChar = %d\n", opts.wantCharPtr ? 1 : 0);
        if (opts.wantCharPtr) buf.put("new BytePtr(");
        else buf.put(" new ByteSlice(");
        buf.put('"');
        for (size_t i = 0; i < e.len; i++)
        {
            const c = e.charAt(i);
            switch (c)
            {
            case '"':
            case '\\':
                buf.put('\\');
                goto default;
            default:
                if (c <= 0x7F && isprint(c))
                    buf.put(c);
                else if(c == '\n')
                    buf.put("\\n");
                else if(c == '\r')
                    buf.put("\\r");
                else
                    buf.fmt("\\u%04x", c);
                break;
            }
        }
        buf.put('"');
        buf.put(')');
    }

    override void visit(ArrayLiteralExp e)
    {
        auto type = e.type.nextOf.toJava(opts);
        if (!opts.rawArrayLiterals) buf.put("slice(");
        buf.fmt("new %s[]{", type);
        argsToBuffer(e.elements, buf, opts, null, e.basis);
        buf.put("}");
        if (!opts.rawArrayLiterals) buf.put(")");
    }

    override void visit(AssocArrayLiteralExp e)
    {
        buf.put('[');
        foreach (i, key; *e.keys)
        {
            if (i)
                buf.put(", ");
            expToBuffer(key, PREC.assign, buf, opts);
            buf.put(':');
            auto value = (*e.values)[i];
            expToBuffer(value, PREC.assign, buf, opts);
        }
        buf.put(']');
    }

    override void visit(StructLiteralExp e)
    {
        buf.put("new ");
        buf.put(e.type.toJava(opts));
        buf.put('(');
        if (e.type.toString.indexOf("Array!") < 0 && !collectMembers(e.sd).hasUnion) {
            // CTFE can generate struct literals that contain an AddrExp pointing
            // to themselves, need to avoid infinite recursion:
            // struct S { this(int){ this.s = &this; } S* s; }
            // const foo = new S(0);
            if (e.stageflags & stageToCBuffer)
                buf.put("<recursion>");
            else
            {
                const old = e.stageflags;
                e.stageflags |= stageToCBuffer;
                structLiteralArgs(e.elements, buf, opts, e.sd);
                e.stageflags = old;
            }
        }
        buf.put(')');
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
            buf.put(e.sds.kind()[0..strlen(e.sds.kind())]);
            buf.put(' ');
            buf.put(e.sds.toString());
        }
    }

    override void visit(TemplateExp e)
    {
        stderr.writefln("TEMPLATE EXP %s\n", e.toString);
        buf.put(e.td.toString());
    }

    override void visit(NewExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, opts);
            buf.put('.');
        }
        buf.put("new ");
        typeToBuffer(e.newtype, buf, opts);
        buf.put('(');
        auto struc = e.newtype.isTypeStruct();
        if (e.type.toString.indexOf("Array!") < 0 && (!struc || !collectMembers(struc.sym).hasUnion)) { 
            if (e.arguments && e.arguments.dim)
            {
                argsToBuffer(e.arguments, buf, opts, null);
            }
        }
        buf.put(')');
    }

    override void visit(NewAnonClassExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, opts);
            buf.put('.');
        }
        buf.put("new");
        if (e.newargs && e.newargs.dim)
        {
            buf.put('(');
            argsToBuffer(e.newargs, buf, opts, null);
            buf.put(')');
        }
        buf.put(" class ");
        if (e.arguments && e.arguments.dim)
        {
            buf.put('(');
            argsToBuffer(e.arguments, buf, opts, null);
            buf.put(')');
        }
        if (e.cd)
            e.cd.dsymbolToBuffer(buf);
    }

    override void visit(SymOffExp e)
    {
        if (e.offset)
            buf.fmt("(%s.ptr().plus(%u))", e.var.ident.symbol, e.offset);
        else if (e.var.isTypeInfoDeclaration())
            buf.put(e.var.ident.symbol);
        else if(e.var.type.ty == Tstruct)
            buf.fmt("%s", e.var.ident.symbol);
        else {
            if (auto name = e.var in opts.renamed)
                buf.fmt("ptr(%s)", *name);
            else
                buf.fmt("ptr(%s)", e.var.ident.symbol);
        }
    }

    override void visit(VarExp e)
    {
        if(e.var.ident.symbol == "__dollar") {
            opts.dollarValue.accept(this);
            buf.put(".getLength()");
        }
        else if(e.var.ident.symbol == e.type.toJava(opts)) {
            buf.fmt("new %s()", e.var.ident.symbol);
        }
        else if(e.var is opts.vararg) {
            buf.put("new Slice<>(");
            buf.put(e.var.ident.symbol);
            buf.put(")");
        }
        else {
            buf.put(printParent(e.var));
            if (auto name = e.var in opts.renamed)
                buf.put(*name);
            else
                buf.put(e.var.ident.symbol);
            if (e.var in opts.refParams)
                buf.put(".value");
        }
    }

    override void visit(OverExp e)
    {
        buf.put(e.vars.ident.symbol);
    }

    override void visit(TupleExp e)
    {
        if (e.e0)
        {
            buf.put('(');
            e.e0.accept(this);
            buf.put(", tuple(");
            argsToBuffer(e.exps, buf, opts, null);
            buf.put("))");
        }
        else
        {
            buf.put("tuple(");
            argsToBuffer(e.exps, buf, opts, null);
            buf.put(')');
        }
    }

    override void visit(FuncExp e)
    {
        stderr.writefln("Func exp %s\n", e.toString());
        buf.put(e.fd.funcName);
    }
    
    override void visit(CommaExp c)
    {
        auto left = c.e1.toJava(opts);
        //stderr.writefln("comma e2 = %s\n", c.e2.toString());
        auto right = c.e2.toJava(opts);
        if(left == "") buf.put(right);
        else if (auto blit = c.e1.isBlitExp()) {
            buf.put(blit.e1.toJava(opts));
            buf.put(" = ");
            buf.put(right);
        }
        else {
            buf.put("comma(");
            buf.put(left);
            buf.put(", ");
            buf.put(right);
            buf.put(")");
        }
    }

    override void visit(DeclarationExp e)
    {
        /* Normal dmd execution won't reach here - regular variable declarations
         * are handled in visit(ExpStatement), so here would be used only when
         * we'll directly call Expression.toString() for debugging.
         */
        //stderr.writefln("DeclarationExp %s:%d", e.loc.filename[0..strlen(e.loc.filename)], e.loc.linnum);
    }

    override void visit(TypeidExp e)
    {
        //TODO: do we even need it?
        // buf.put(e.toString);
        // assert(false);
    }

    override void visit(TraitsExp e)
    {
        // must handle TraitsExp by pattern-matching on 
        // the full expression
        assert(0);
        version(none) {
            buf.put("__traits(");
            if (e.ident)
                buf.put(e.ident.toString());
            if (e.args)
            {
                foreach (arg; *e.args)
                {
                    buf.put(", ");
                    objectToBuffer(arg, buf);
                }
            }
            buf.put(')');
        }
    }

    override void visit(HaltExp e)
    {
        buf.put("halt");
    }

    override void visit(IsExp e)
    {
        assert(false);
        version(none) {
            buf.put("is(");
            typeToBuffer(e.targ, e.id, buf);
            if (e.tok2 != TOK.reserved)
            {
                buf.fmt(" %s %s", Token.toString(e.tok), Token.toString(e.tok2));
            }
            else if (e.tspec)
            {
                if (e.tok == TOK.colon)
                    buf.put(" : ");
                else
                    buf.put(" == ");
                typeToBuffer(e.tspec, null, buf);
            }
            if (e.parameters && e.parameters.dim)
            {
                buf.put(", ");
                scope v = new DsymbolPrettyPrintVisitor(buf);
                v.visitTemplateParameters(e.parameters);
            }
            buf.put(')');
        }
    }

    override void visit(UnaExp e)
    {
        if (e.e1.type.ty == Tpointer) {
            if(e.op == TOK.not) {
                expToBuffer(e.e1, precedence[e.op], buf, opts);
                buf.put(" == null");
            }
            else if (e.op == TOK.address) {
                if (auto var = e.e1.isDotVarExp) {
                    if (var.var.ident.symbol == "next") {
                        //stderr.writefln("Took address of %s %s", e.e1.toString, var.var);
                        buf.put("new PtrToNext(");
                        expToBuffer(var.e1, precedence[e.op], buf, opts);
                        buf.put(")");
                        return;
                    }
                }
                buf.put("ptr(");
                if (auto var = e.e1.isVarExp) {
                    if (auto name = var.var in opts.renamed)
                        buf.put(*name);
                    else
                        buf.put(var.var.ident.symbol);
                }
                else
                    expToBuffer(e.e1, precedence[e.op], buf, opts);
                buf.put(")");
            }
            else {
                stderr.writefln("Unsupported unary pointer arithmetic: %s", e.toString());
                assert(0);
            }
        }
        else if(e.op == TOK.address) {
            if (auto var = e.e1.isDotVarExp) {
                if (var.var.ident.symbol == "next") {
                    //stderr.writefln("Took address of %s %s", e.e1.toString, var.var);
                    buf.put("new PtrToNext(");
                    expToBuffer(var.e1, precedence[e.op], buf, opts);
                    buf.put(")");
                    return;
                }
            }
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
        else if(e.op == TOK.not) {
            buf.put(Token.toString(e.op));
            buf.put('(');
            buf.put(e.e1.toJavaBool(opts));
            buf.put(')');
        }
        else {
            buf.put(Token.toString(e.op));
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(BinExp e)
    {
        if (e.e1.type && (e.op == TOK.equal || e.op == TOK.notEqual) && !e.e1.type.isTypeBasic && !e.e1.type.isTypeEnum) {
            if (e.op == TOK.notEqual) buf.put("!");
            expToBuffer(e.e1, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.put(".equals(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.put(")");
            return;
        }
        else if(e.op == TOK.andAnd || e.op == TOK.orOr) {
            buf.put(e.e1.toJavaBool(opts));
            buf.put(' ');
            buf.put(Token.toString(e.op));
            buf.put(' ');
            buf.put(e.e2.toJavaBool(opts));
            return;
        }
        else if(e.op == TOK.concatenate) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.put(".concat(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.put(")");
            return;
        }
        else if(e.op == TOK.concatenateAssign || e.op == TOK.concatenateElemAssign) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.put(".append(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.put(")");
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
        else if(auto ptr = e.e1.isPtrExp) {
            switch(e.op) {
                case TOK.orAssign:
                    expToBuffer(ptr.e1, precedence[e.e1.op], buf, opts);
                    buf.put(".set(0, ");
                    expToBuffer(e.e1, precedence[e.e1.op], buf, opts);
                    buf.put(" | ");
                    expToBuffer(e.e2, precedence[e.e2.op], buf, opts);
                    buf.put(")");
                    return;
                case TOK.andAssign:
                    expToBuffer(ptr.e1, precedence[e.e1.op], buf, opts);
                    buf.put(".set(0, ");
                    expToBuffer(e.e1, precedence[e.e1.op], buf, opts);
                    buf.put(" & ");
                    expToBuffer(e.e2, precedence[e.e2.op], buf, opts);
                    buf.put(")");
                    return;
                default:
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
                    buf.put("((BytePtr)");
                expToBuffer(e.e1, precedence[e.op], buf, opts);
                if (e.e1.type.nextOf.ty == Tvoid)
                    buf.put(")");
                buf.fmt(".%s(", opName);
                expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
                buf.put(")");
                return;
            }
        }
        else if (auto c = e.e1.isCastExp) {    
            auto var = c.e1.isVarExp();        
            if (var && var.type.isJavaByte && e.e2.type.toJava(opts) == "int") {
                //stderr.writefln("%s %s %s", e.e1.toString, Token.toString(e.op), e.e2.toString);
                switch(e.op) {
                    case TOK.orAssign:
                    case TOK.andAssign:
                        if (auto name = var.var in opts.renamed)
                            buf.put(*name);
                        else
                            buf.put(var.var.ident.symbol);
                        if (var.var in opts.refParams)
                            buf.put(".value");
                        buf.fmt(" %s ", Token.toString(e.op));
                        expToBuffer(e.e2, precedence[e.e2.op], buf, opts);
                        return;
                    default:
                }
            }
        }
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        buf.put(' ');
        if (e.op == TOK.identity) buf.put("==");
        else if (e.op == TOK.notIdentity)  buf.put("!=");
        else buf.put(Token.toString(e.op));
        buf.put(' ');
        expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
    }

    override void visit(CompileExp e)
    {
        buf.put("mixin(");
        argsToBuffer(e.exps, buf, opts, null);
        buf.put(')');
    }

    override void visit(ImportExp e)
    {
        buf.put("import(");
        expToBuffer(e.e1, PREC.assign, buf, opts);
        buf.put(')');
    }

    override void visit(AssertExp e)
    {
        if (e.e1.isIntegerExp()) {
            auto i = e.e1.isIntegerExp();
            if (i.toInteger() == 0) {
                buf.put("throw new AssertionError(\"Unreachable code!\")");
                return;
            }
        }
        buf.fmt("assert%s(", e.msg ? "Msg" : "");
        buf.put(e.e1.toJavaBool(opts));
        if (e.msg)
        {
            buf.put(", "); 
            expToBuffer(e.msg, PREC.assign, buf, opts);
        }
        buf.put(')');
    }

    override void visit(DotIdExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put('.');
        buf.put(e.ident.toString());
    }

    override void visit(DotTemplateExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put('.');
        buf.put(e.td.toString());
        stderr.writefln("DOT TEMPLATE %s\n", e.td.toString());
    }

    override void visit(DotVarExp e)
    {
        buf.put(printParent(e.var));
        if (!(e.e1.isThisExp && opts.funcs.length > 1)) {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.put('.');
        }
        buf.put(e.var.ident.symbol);
    }

    override void visit(DotTemplateInstanceExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put('.');
        e.ti.dsymbolToBuffer(buf);
        printTiArgs(e.ti, buf, opts);
    }

    override void visit(DelegateExp e)
    {
        //stderr.writefln("Delegate exp %s\n", e.toString());
        if (!e.func.isNested() || e.func.needThis())
        {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.put('.');
        }
        buf.put(e.func.funcName);
    }

    override void visit(DotTypeExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put('.');
        buf.put(e.sym.toString());
    }

    override void visit(CallExp e)
    {
        //stderr.writefln("call exp %s\n", e.e1.toString());
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
                // stderr.writefln("MEMCPY %s %s\n", e.arguments[0][0].toString, e.arguments[0][1].toString);
                if (c1 && c2 && c1.e1.type.ty == Tpointer && c2.e1.type.ty == Tpointer
                && c1.e1.type.nextOf.ty == Tstruct && c2.e1.type.nextOf.ty == Tstruct) {
                    expToBuffer(e.arguments[0][0], precedence[e.op], buf, opts);
                    buf.put(".opAssign(");
                    expToBuffer(e.arguments[0][1], precedence[e.op], buf, opts);
                    buf.put(")");
                    return;
                }
                else if (c1 && c2 && c1.e1.type.ty == Tclass && c2.e1.type.ty == Tclass) {
                    expToBuffer(e.arguments[0][0], precedence[e.op], buf, opts);
                    buf.put(" = ");
                    expToBuffer(e.arguments[0][1], precedence[e.op], buf, opts);
                    buf.put(".copy()");
                    return;
                }
                else if (c2 && e.arguments[0][0].type.ty == Tpointer && e.arguments[0][0].type.nextOf.ty == Tvoid) {
                    buf.put("memcpy((BytePtr)");
                    expToBuffer(e.arguments[0][0], precedence[e.op], buf, opts);
                    buf.put(", ");
                    expToBuffer(e.arguments[0][1], precedence[e.op], buf, opts);
                    buf.put(", ");
                    expToBuffer(e.arguments[0][2], precedence[e.op], buf, opts);
                    buf.put(")");
                    return;
                }
            }
            if (e.f && e.f.isCtorDeclaration()) {
                auto ctorCall = e.e1.isDotVarExp();
                auto isThis = ctorCall.e1.isThisExp();
                auto isSuper = ctorCall.e1.isSuperExp();
                //stderr.writefln("CTOR %s this = %d super = %d\n", e.f.toString(), isThis ? 1:0, isSuper ? 1:0);
                if (isThis || isSuper) {
                    expToBuffer(ctorCall.e1, precedence[e.op], buf, opts);
                }
                else {
                    buf.put("new ");
                    buf.put(e.type.toJava(opts));
                }
            }
            else if (e.f && e.f.ident.symbol == "opIndex") {
                auto var = e.e1.isDotVarExp();
                if (var) expToBuffer(var.e1, PREC.primary, buf, opts);
                else expToBuffer(e.e1, PREC.primary, buf, opts);
                if (e.f.parameters.length == 1)
                    buf.put(".get");
                else
                    buf.put(".set");
            }
            else if(e.f) {
                auto var = e.e1.isDotVarExp();
                if (var) {
                    if (!(var.e1.isThisExp && opts.funcs.length > 1)) {
                        expToBuffer(var.e1, PREC.primary, buf, opts);
                        buf.put('.');
                    }
                    buf.put(e.f.funcName);
                }
                else 
                    expToBuffer(e.e1, precedence[e.op], buf, opts);
                if (auto tmpl = e.f in opts.templates) {
                    buf.put(tmpl.tiArgs);
                }
            }
            else
                expToBuffer(e.e1, precedence[e.op], buf, opts);
            //stderr.writefln("Calling %x %s type %s\n", e.f, e.e1.toString(), e.e1.type.toString());
            if (!e.f || e.f.isNested() || e.f in opts.localFuncs) {
                buf.put(".invoke");
            }
        }
        buf.put('(');
        argsToBuffer(e.arguments, buf, opts, e.f);
        buf.put(')');
    }

    override void visit(PtrExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.e1.type.nextOf.ty != Tstruct && e.e1.type.nextOf.ty != Tfunction) 
            buf.put(".get()");
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
            buf.put("(");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.put(" ? 1 : 0)");
        }
        else if(fromByte && toInt) {
            buf.put("(");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.put(" & 0xFF)");
        }
        else if (fromInt && toInt) expToBuffer(e.e1, precedence[e.op], buf, opts);
        else if(fromEnum) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
        else if(fromVoidPtr && toClass) {
            if (auto call = e.e1.isCallExp) {
                if (call.f && call.f.funcName == "xmalloc")
                    buf.put("null");
            }
            else { // simple casts
                buf.put("(");
                typeToBuffer(e.to, buf, opts);
                buf.put(")");
                expToBuffer(e.e1, precedence[e.op], buf, opts);
            }
        }
        else if(complexTarget && fromClass) {
            buf.put("(Object)");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
        else if (complexTarget) { // rely on toTypeName(x)
            buf.put("to");
            typeToBuffer(e.to, buf, opts);
            buf.put("(");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.put(")");
        }
        else { // simple casts
            buf.put("(");
            typeToBuffer(e.to, buf, opts);
            buf.put(")");
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
            buf.put(".slice(");
            if (e.lwr)
                sizeToBuffer(e.lwr, buf, opts);
            else
                buf.put('0');
            
            if (e.upr) {
                auto old = opts.dollarValue;
                scope(exit) opts.dollarValue = old;
                opts.dollarValue = e.e1;
                buf.put(",");
                sizeToBuffer(e.upr, buf, opts);
            }
            buf.put(')');
        }
    }

    override void visit(ArrayLengthExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put(".getLength()");
    }

    override void visit(IntervalExp e)
    {
        expToBuffer(e.lwr, PREC.assign, buf, opts);
        buf.put("..");
        expToBuffer(e.upr, PREC.assign, buf, opts);
    }

    override void visit(DelegatePtrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put(".ptr");
    }

    override void visit(DelegateFuncptrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put(".funcptr");
    }

    override void visit(ArrayExp e)
    {
        buf.put("new ");
        typeToBuffer(e.type, buf, opts);
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put('{');
        argsToBuffer(e.arguments, buf, opts, null);
        buf.put('}');
    }

    override void visit(DotExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put('.');
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
                    buf.put(".set(");
                    argsToBuffer(call.arguments, buf, opts, call.f);
                    if (call.arguments.length != 2) {
                        buf.put(", ");
                        expToBuffer(e.e2, precedence[e.op], buf, opts);
                    }
                    buf.put(")");
                    return;
                }
            }
        }
        if (auto assign = e.e1.isIndexExp()) {
            auto oldDollar = opts.dollarValue;
            scope(exit) opts.dollarValue = oldDollar;
            opts.dollarValue = e.e1;
            expToBuffer(assign.e1, PREC.primary, buf, opts);
            buf.put(".set(");
            expToBuffer(assign.e2, PREC.primary, buf, opts);
            buf.put(", ");
            sizeToBuffer(e.e2, buf, opts);
            buf.put(')');
        }
        else if(auto pt = e.e1.isPtrExp()) {
            if (pt.e1.type.nextOf.ty == Tstruct) {
                expToBuffer(pt.e1, PREC.primary, buf, opts);
                buf.put(".opAssign(");
                expToBuffer(e.e2, PREC.primary, buf, opts);
                buf.put(')');
                return;
            }
            expToBuffer(pt.e1, PREC.primary, buf, opts);
            buf.put(".set(0, ");
            expToBuffer(e.e2, PREC.primary, buf, opts);
            buf.put(')');
        }
        else if ((e.e1.type.ty == Tpointer && e.e1.type.nextOf.ty != Tstruct) && e.e2.type.ty == Tpointer) {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.put(" = ");
            if (e.e2.isNullExp()) expToBuffer(e.e2, PREC.primary, buf, opts);
            else {
                buf.put("pcopy(");
                auto old = opts.wantCharPtr;
                scope(exit) opts.wantCharPtr = old;
                if (e.e1.type.nextOf.ty == Tchar) {
                    opts.wantCharPtr = true;
                }
                expToBuffer(e.e2, PREC.primary, buf, opts);
                buf.put(")");
            }
        }
        else if(e.e1.type.ty == Tstruct && e.e2.type.ty == Tint32) {
            buf.put("null");
        }
        else if ((e.e1.type.ty == Tstruct || e.e1.type.ty == Tarray) 
        && (e.e2.type.ty == Tstruct || e.e2.type.ty == Tarray)) {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.put(" = ");
            expToBuffer(e.e2, PREC.primary, buf, opts);
            buf.put(".copy()");
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
        buf.put(".get(");
        sizeToBuffer(e.e2, buf, opts);
        buf.put(')');
    }

    override void visit(PostExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.e1.type.ty == Tpointer) {
            if(e.op == TOK.plusPlus) {
                buf.put(".postInc()");
            }
            else if(e.op == TOK.minusMinus) {
                buf.put(".postDec()");
            }
            else 
                stderr.writefln("Pointer arithmetic: %s\n", e.toString());
        }
        else 
            buf.put(Token.toString(e.op));
    }

    override void visit(PreExp e)
    {
        if (e.e1.type.ty == Tpointer) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            if(e.op == TOK.prePlusPlus) {
                buf.put(".inc()");
            }
            else if(e.op == TOK.preMinusMinus) {
                buf.put(".dec()");
            }
            else 
                stderr.writefln("Pointer arithmetic: %s\n", e.toString());
        }
        else {
            buf.put(Token.toString(e.op));
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(RemoveExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.put(".remove(");
        expToBuffer(e.e2, PREC.assign, buf, opts);
        buf.put(')');
    }

    override void visit(CondExp e)
    {
        buf.put(e.econd.toJavaBool(opts));
        buf.put(" ? ");
        expToBuffer(e.e1, PREC.expr, buf, opts);
        buf.put(" : ");
        expToBuffer(e.e2, PREC.cond, buf, opts);
    }

    override void visit(DefaultInitExp e)
    {
        buf.put(Token.toString(e.subop));
    }

    override void visit(ClassReferenceExp e)
    {
        buf.put(e.value.toString());
    }
}


private void expressionToBuffer(Expression e, TextBuffer buf, ExprOpts opts)
{
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
}

/**************************************************
 * Write expression out to buf, but wrap it
 * in ( ) if its precedence is less than pr.
 */
private void expToBuffer(Expression e, PREC pr, TextBuffer buf, ExprOpts opts)
{
    {
        if (precedence[e.op] == PREC.zero)
            stderr.writefln("precedence not defined for token '%s'\n", Token.toString(e.op));
    }
    assert(precedence[e.op] != PREC.zero);
    assert(pr != PREC.zero);
    /* Despite precedence, we don't allow a<b<c expressions.
     * They must be parenthesized.
     */
    if (precedence[e.op] < pr || (pr == PREC.rel && precedence[e.op] == pr)
        || (pr >= PREC.or && pr <= PREC.and && precedence[e.op] == PREC.rel))
    {
        buf.put('(');
        e.expressionToBuffer(buf, opts);
        buf.put(')');
    }
    else
    {
        e.expressionToBuffer(buf, opts);
    }
}

private void structLiteralArgs(Expressions* expressions, TextBuffer buf, ExprOpts opts, StructDeclaration sd)
{
    if (!expressions || !expressions.dim)
        return;
    bool first = true;
    foreach (i, el; *expressions)
    {
        scope tmp = new TextBuffer();
        if (el) {
            auto n = el.isNullExp();
            auto members = collectMembers(sd);

            const wantChar = members.all[i].type.ty == Tpointer
                && members.all[i].type.nextOf.ty == Tchar;
            
            if(n && n.type.ty == Tarray) {
                tmp.put("new ");
                tmp.put(n.type.toJava(opts));
                tmp.put("()");
            }
            else if(wantChar) {
                // stderr.writefln("WantChar func = %s args=%d\n", fd.ident.toString, i);
                opts.wantCharPtr = true;
                expToBuffer(el, PREC.assign, tmp, opts);
            }
            else {
                expToBuffer(el, PREC.assign, tmp, opts);
            }
        }
        if (tmp.data.length > 0) {
            if (!first) buf.fmt(", ");
            else first = false;
            buf.fmt("%s", tmp.data);
        }
    }
}

/**************************************************
 * Write out argument list to buf.
 */
private void argsToBuffer(Expressions* expressions, TextBuffer buf, ExprOpts opts, FuncDeclaration fd, Expression basis = null)
{
    if (!expressions || !expressions.dim)
        return;
    bool first = true;
    foreach (i, el; *expressions)
    {
        scope tmp = new TextBuffer();
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
                tmp.put("(");
                tmp.put((*fd.parameters)[i].type.toJava(opts));
                tmp.put(")");
                tmp.put(var.var.ident.symbol);
            }
            else if (var && var.var in opts.refParams && refParam) {
                string* renamed = var.var in opts.renamed;
                tmp.put(renamed ? *renamed : var.var.ident.symbol);
            }
            else if(n && n.type.ty == Tarray) {
                tmp.put("new ");
                tmp.put(n.type.toJava(opts));
                tmp.put("()");
            }
            else if(wantChar) {
                // stderr.writefln("WantChar func = %s args=%d\n", fd.ident.toString, i);
                opts.wantCharPtr = true;
                expToBuffer(el, PREC.assign, tmp, opts);
            }
            else {
                expToBuffer(el, PREC.assign, tmp, opts);
            }
        }
        if (tmp.data.length > 0) {
            if (!first) buf.fmt(", ");
            else first = false;
            buf.fmt("%s", tmp.data);
        }
    }
}

private void sizeToBuffer(Expression e, TextBuffer buf, ExprOpts opts)
{
    if (e.type == Type.tsize_t)
    {
        Expression ex = (e.op == TOK.cast_ ? (cast(CastExp)e).e1 : e);
        ex = ex.optimize(WANTvalue);
        //stderr.writefln("SIZE TO BUF %s \n", ex.toString());
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
                buf.fmt("%d", cast(int)uval); // Java's size_t is int
                return;
            }
        }
    }
    expToBuffer(e, PREC.assign, buf, opts);
}

/**************************************************
 * An entry point to pretty-print type.
 */
private void typeToBuffer(Type t, TextBuffer buf, ExprOpts opts, Boxing boxing = Boxing.no)
{
    if (t is null) {
        buf.put("nothing");
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

private void typeToBufferx(Type t, TextBuffer buf, ExprOpts opts, Boxing boxing = Boxing.no)
{
    void visitType(Type t)
    {
        stderr.writefln("t = %x, ty = %d\n", t, t.ty);
        assert(0);
    }

    void visitError(TypeError t)
    {
        buf.put("_error_");
    }

    void visitBasic(TypeBasic t)
    {
        //fmt("TypeBasic::toCBuffer2(t.mod = %d)\n", t.mod);
        switch (t.ty) with(Boxing)
        {
        case Tvoid:
            if (boxing == yes) buf.put("Void");
            else buf.put("void");
            break;

        case Tint8:
        case Tuns8:
            if (boxing == yes) buf.put("Byte");
            else buf.put("byte");
            break;

        case Tint16:
        case Tuns16:
            if (boxing == yes) buf.put("Short");
            else buf.put("short");
            break;

        case Tint32:
        case Tuns32:
            if (boxing == yes) buf.put("Integer");
            else buf.put("int");
            break;

        case Tfloat32:
            if (boxing == yes) buf.put("Float");
            else buf.put("float");
            break;

        case Tfloat64:
        case Tfloat80:
            if (boxing == yes) buf.put("Double");
            else  buf.put("double");
            break;

        case Tint64:
        case Tuns64:
            if (boxing == yes) buf.put("Long");
            else buf.put("long");
            break;

        case Tbool:
            if (boxing == yes) buf.put("Boolean");
            else buf.put("boolean");
            break;

        case Tchar:
            if (boxing == yes) buf.put("Byte");
            else buf.put("byte");
            break;

        case Twchar:
            if (boxing == yes) buf.put("Character");
            else buf.put("char");
            break;

        case Tdchar:
            if (boxing == yes) buf.put("Integer");
            else buf.put("int");
            break;

        default:
            stderr.writefln("%s\n", t.toString());
            assert(0, "Unexpected type in type-conversion ");
        }
    }

    void visitTraits(TypeTraits t)
    {
        //fmt("TypeBasic::toCBuffer2(t.mod = %d)\n", t.mod);
        t.exp.expressionToBuffer(buf, ExprOpts.init);
    }

    void visitVector(TypeVector t)
    {
        //fmt("TypeVector::toCBuffer2(t.mod = %d)\n", t.mod);
        assert(0);
        version(none) {
            buf.put("__vector(");
            typeToBufferx(t.basetype, buf, opts);
            buf.put(")");
        }
    }

    void visitSArray(TypeSArray t)
    {
        auto et = t.next;
        if (auto e = et.isTypeEnum()) et = e.memType;
        if (et.ty == Tchar || et.ty == Tvoid || et.ty == Tuns8 || et.ty == Tint8)
            buf.put("ByteSlice");
        else if (et.ty == Twchar)
            buf.put("CharSlice");
        else if (et.ty == Tdchar)
            buf.put("IntSlice");
        else if(et.ty == Tint32 || t.next.ty == Tuns32)
            buf.put("IntSlice");
        else {
            buf.put("Slice<");
            typeToBufferx(et, buf, opts, Boxing.yes);
            buf.put(">");
        }
    }

    void visitDArray(TypeDArray t)
    {
        auto et = t.next;
        if (auto e = et.isTypeEnum()) et = e.memType;
        if (et.ty == Tchar || et.ty == Tvoid || et.ty == Tuns8 || et.ty == Tint8)
            buf.put("ByteSlice");
        else if (et.ty == Twchar)
            buf.put("CharSlice");
        else if (et.ty == Tdchar)
            buf.put("IntSlice");
        else if(et.ty == Tint32 || et.ty == Tuns32)
            buf.put("IntSlice");
        else
        {
            buf.put("Slice<");
            typeToBufferx(et, buf, opts, Boxing.yes);
            buf.put(">");
        }
    }

    void visitAArray(TypeAArray t)
    {
        buf.put("AA<");
        typeToBufferx(t.index, buf, opts, Boxing.yes);
        buf.put(',');
        typeToBufferx(t.next, buf, opts, Boxing.yes);
        buf.put('>');
    }

    void visitPointer(TypePointer tp)
    {
        //fmt("TypePointer::toCBuffer2() next = %d\n", t.next.ty);
        if (tp.next.ty == Tfunction)
            visitFuncIdentWithPostfix(cast(TypeFunction)tp.next, buf, opts);
        else
        {
            Type t = tp.next;
            if (auto et = t.isTypeEnum)
                t = et.memType;
            if (t.ty == Tvoid) 
                buf.put("Object");
            else if (t.ty == Tchar || t.ty == Tuns8 || t.ty == Tint8)
                buf.put("BytePtr");
            else if (t.ty == Twchar)
                buf.put("CharPtr");
            else if (t.ty == Tdchar) 
                buf.put("IntPtr");
            else if (t.ty == Tint32 || t.ty == Tuns32)
                buf.put("IntPtr");
            else if (t.ty == Tstruct)
                typeToBufferx(t, buf, opts, Boxing.yes);
            else {
                buf.put("Ptr<");
                typeToBufferx(t, buf, opts, Boxing.yes);
                buf.put(">");
            }
        }
    }

    void visitReference(TypeReference t)
    {
        assert(0);
        version(none) {
            typeToBufferx(t.next, buf);
            buf.put('&');
        }
    }

    void visitFunction(TypeFunction t)
    {
        //fmt("TypeFunction::toCBuffer2() t = %p, ref = %d\n", t, t.isref);
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
                    buf.put('.');
                    TemplateInstance ti = cast(TemplateInstance)id;
                    ti.dsymbolToBuffer(buf);
                }
            }
            else if (id.dyncast() == DYNCAST.expression)
            {
                buf.put('[');
                (cast(Expression)id).expressionToBuffer(buf, opts);
                buf.put(']');
            }
            else if (id.dyncast() == DYNCAST.type)
            {
                buf.put('[');
                typeToBufferx(cast(Type)id, buf, opts);
                buf.put(']');
            }
            else
            {
                buf.put('.');
                buf.put(id.toString());
            }
        }
    }

    void visitIdentifier(TypeIdentifier t)
    {
        buf.put(t.ident.toString());
        visitTypeQualifiedHelper(t);
    }

    void visitInstance(TypeInstance t)
    {
        t.tempinst.dsymbolToBuffer(buf);
        visitTypeQualifiedHelper(t);
    }

    void visitTypeof(TypeTypeof t)
    {
        buf.put("typeof(");
        t.exp.expressionToBuffer(buf, opts);
        buf.put(')');
        visitTypeQualifiedHelper(t);
    }

    void visitReturn(TypeReturn t)
    {
        buf.put("typeof(return)");
        visitTypeQualifiedHelper(t);
    }

    void visitEnum(TypeEnum t)
    {
        buf.put(t.memType.toJava(opts, boxing));
    }

    void visitStruct(TypeStruct t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent ? t.sym.parent.isTemplateInstance() : null;
        if (ti && ti.aliasdecl == t.sym && t.sym.ident.symbol == "DArray") {
            buf.put(ti.name.symbol);
            buf.put("<");
            foreach(i, arg; (*ti.tiargs)[]) {
                if(i) buf.put(",");
                if (auto atype = isType(arg)) buf.put(atype.toJava(opts));
                else buf.put(arg.toString());
            }
            buf.put(">");
        }
        else if(t.sym.ident.symbol == "__va_list_tag")
            buf.put("Slice<Object>");
        else {
            if (ti && ti.aliasdecl == t.sym) {
                buf.put(t.sym.ident.symbol);
                printTiArgs(ti, buf, opts);
                return;
            }
            auto ds = t.sym.parent.isAggregateDeclaration();
            if (ds && !opts.aggregates[].canFind!(agg => agg is ds))  {
                buf.put(ds.ident.symbol);
                buf.put(".");
                buf.put(t.sym.ident.symbol);
                return;
            }
            buf.put(t.sym.ident.symbol);
        }
    }

    void visitClass(TypeClass t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent.isTemplateInstance();
        if (ti && ti.aliasdecl == t.sym) {
            buf.put(t.sym.ident.symbol);
            printTiArgs(ti, buf, opts);
            return;
        }
        auto ds = t.sym.parent.isAggregateDeclaration();
        if (ds && !opts.aggregates[].canFind!(agg => agg is ds))  {
            buf.put(ds.ident.symbol);
            buf.put(".");
            buf.put(t.sym.ident.symbol);
            return;
        }
        buf.put(t.sym.ident.symbol);
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
        buf.put("Object");
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

private void parametersToBuffer(ParameterList pl, TextBuffer buf, ExprOpts opts, Boxing boxing = Boxing.no)
{
    foreach (i; 0 .. pl.length)
    {
        if (i)
            buf.put(", ");
        pl[i].parameterToBuffer(buf, opts, boxing);
    }
    final switch (pl.varargs)
    {
        case VarArg.none:
            break;

        case VarArg.variadic:
            if (pl.length == 0)
                goto case VarArg.typesafe;
            buf.put(", ...");
            break;

        case VarArg.typesafe:
            buf.put("...");
            break;
    }
}


private void visitFuncIdentWithPostfix(TypeFunction t, TextBuffer buf, ExprOpts opts)
{
    if (t.inuse)
    {
        t.inuse = 2; // flag error to caller
        return;
    }
    t.inuse++;
    buf.fmt("Function%d<", t.parameterList.length);
    foreach(i, p; *t.parameterList) {
        if (i) buf.put(",");
        typeToBuffer(p.type, buf, opts, Boxing.yes);
    }
    if (t.parameterList && t.parameterList.length > 0) buf.put(",");
    if (t.next)
    {
        typeToBuffer(t.next, buf, opts, Boxing.yes);
    }
    else 
        buf.put("Void");
    buf.put(">");
    t.inuse--;
}


private void visitFuncIdentWithPrefix(TypeFunction t, TemplateDeclaration td, TextBuffer buf, ExprOpts opts)
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
            buf.put('(');
            foreach (i, p; *td.origParameters)
            {
                if (i)
                    buf.put(", ");
                p.templateParameterToBuffer(buf, hgs);
            }
            buf.put(')');
        }
    }
    parametersToBuffer(t.parameterList, buf, opts);
    t.inuse--;
}

private void dsymbolToBuffer(Dsymbol s, TextBuffer buf) {
    buf.put(s.toString());
}

private void printTiArgs(TemplateInstance ti, TextBuffer buf, ExprOpts opts)
{
    if (ti.tiargs)
        foreach(arg; *ti.tiargs) {
            auto t = arg.isType();
            if (t is null) stderr.writefln("NON-TYPE Template parameter!\n");
            buf.put(t.toJava(opts, Boxing.yes));
        }
}

/***********************************************************
 * Write parameter `p` to buffer `buf`.
 * Params:
 *      p = parameter to serialize
 *      buf = buffer to write it to
 *      hgs = context
 */
private void parameterToBuffer(Parameter p, TextBuffer buf, ExprOpts opts, Boxing boxing = Boxing.no)
{
    if (p.type.ty == Tident &&
             (cast(TypeIdentifier)p.type).ident.toString().startsWith("__T"))
    {
        // print parameter name, instead of undetermined type parameter
        buf.put(p.ident.toString());
    }
    else
    {
        typeToBuffer(p.type, buf, opts, boxing);
        if (p.ident) {
            buf.put(" ");
            buf.put(p.ident.symbol);
        }
    }
    opts.wantCharPtr = p.type.ty == Tpointer && p.type.nextOf().ty == Tchar;

    if (p.defaultArg)
    {
        buf.put(" = ");
        p.defaultArg.expToBuffer(PREC.assign, buf, opts);
    }
}
