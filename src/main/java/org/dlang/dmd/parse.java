package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.staticassert.*;
import static org.dlang.dmd.tokens.*;

public class parse {
    private static final int[] initializer_0 = {0, 0, 0, 0, 0, 0, 0, 0, PREC.unary, 0, 0, 0, PREC.unary, PREC.primary, PREC.primary, 0, 0, PREC.primary, PREC.primary, PREC.unary, PREC.expr, 0, PREC.unary, PREC.unary, PREC.unary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, 0, 0, 0, PREC.primary, PREC.primary, PREC.expr, PREC.primary, 0, PREC.primary, PREC.primary, PREC.unary, PREC.primary, PREC.unary, 0, PREC.primary, PREC.primary, PREC.primary, PREC.primary, 0, PREC.primary, PREC.primary, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.primary, PREC.primary, PREC.shift, PREC.shift, PREC.assign, PREC.assign, PREC.shift, PREC.assign, PREC.add, PREC.assign, PREC.assign, PREC.assign, PREC.add, PREC.add, PREC.assign, PREC.assign, PREC.mul, PREC.mul, PREC.mul, PREC.assign, PREC.assign, PREC.assign, PREC.and, PREC.or, PREC.xor, PREC.assign, PREC.assign, PREC.assign, PREC.assign, PREC.unary, PREC.unary, PREC.primary, PREC.primary, PREC.assign, PREC.assign, PREC.primary, 0, PREC.expr, PREC.cond, PREC.andand, PREC.oror, PREC.primary, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, PREC.primary, 0, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.expr, PREC.primary, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, PREC.unary, PREC.primary, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.rel, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, PREC.primary, 0, 0, 0, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, 0, 0, PREC.pow, PREC.assign, 0, PREC.unary, 0, PREC.assign, 0, 0, 0, PREC.expr, PREC.primary};

    static int CDECLSYNTAX = 0;
    static int CCASTSYNTAX = 1;
    static int CARRAYDECL = 1;
    static IntSlice precedence = slice(initializer_0);

    public static class ParseStatementFlags 
    {
        public static final int semi = 1;
        public static final int scope_ = 2;
        public static final int curly = 4;
        public static final int curlyScope = 8;
        public static final int semiOk = 16;
    }

    // from template PrefixAttributes!(ASTBase)
    public static class PrefixAttributesASTBase
    {
        public long storageClass = 0L;
        public ASTBase.Expression depmsg = null;
        public int link = 0;
        public ASTBase.Prot protection = new ASTBase.Prot();
        public boolean setAlignment = false;
        public ASTBase.Expression ealign = null;
        public Ptr<DArray<ASTBase.Expression>> udas = null;
        public BytePtr comment = null;
        public PrefixAttributesASTBase(){
            protection = new ASTBase.Prot();
        }
        public PrefixAttributesASTBase copy(){
            PrefixAttributesASTBase r = new PrefixAttributesASTBase();
            r.storageClass = storageClass;
            r.depmsg = depmsg;
            r.link = link;
            r.protection = protection.copy();
            r.setAlignment = setAlignment;
            r.ealign = ealign;
            r.udas = udas;
            r.comment = comment;
            return r;
        }
        public PrefixAttributesASTBase(long storageClass, ASTBase.Expression depmsg, int link, ASTBase.Prot protection, boolean setAlignment, ASTBase.Expression ealign, Ptr<DArray<ASTBase.Expression>> udas, BytePtr comment) {
            this.storageClass = storageClass;
            this.depmsg = depmsg;
            this.link = link;
            this.protection = protection;
            this.setAlignment = setAlignment;
            this.ealign = ealign;
            this.udas = udas;
            this.comment = comment;
        }

        public PrefixAttributesASTBase opAssign(PrefixAttributesASTBase that) {
            this.storageClass = that.storageClass;
            this.depmsg = that.depmsg;
            this.link = that.link;
            this.protection = that.protection;
            this.setAlignment = that.setAlignment;
            this.ealign = that.ealign;
            this.udas = that.udas;
            this.comment = that.comment;
            return this;
        }
    }

    // from template PrefixAttributes!(ASTCodegen)
    public static class PrefixAttributesASTCodegen
    {
        public long storageClass = 0L;
        public Expression depmsg = null;
        public int link = 0;
        public Prot protection = new Prot();
        public boolean setAlignment = false;
        public Expression ealign = null;
        public Ptr<DArray<Expression>> udas = null;
        public BytePtr comment = null;
        public PrefixAttributesASTCodegen(){
            protection = new Prot();
        }
        public PrefixAttributesASTCodegen copy(){
            PrefixAttributesASTCodegen r = new PrefixAttributesASTCodegen();
            r.storageClass = storageClass;
            r.depmsg = depmsg;
            r.link = link;
            r.protection = protection.copy();
            r.setAlignment = setAlignment;
            r.ealign = ealign;
            r.udas = udas;
            r.comment = comment;
            return r;
        }
        public PrefixAttributesASTCodegen(long storageClass, Expression depmsg, int link, Prot protection, boolean setAlignment, Expression ealign, Ptr<DArray<Expression>> udas, BytePtr comment) {
            this.storageClass = storageClass;
            this.depmsg = depmsg;
            this.link = link;
            this.protection = protection;
            this.setAlignment = setAlignment;
            this.ealign = ealign;
            this.udas = udas;
            this.comment = comment;
        }

        public PrefixAttributesASTCodegen opAssign(PrefixAttributesASTCodegen that) {
            this.storageClass = that.storageClass;
            this.depmsg = that.depmsg;
            this.link = that.link;
            this.protection = that.protection;
            this.setAlignment = that.setAlignment;
            this.ealign = that.ealign;
            this.udas = that.udas;
            this.comment = that.comment;
            return this;
        }
    }

    // from template getStorageClass!(ASTBase)
    public static long getStorageClassASTBase(Ptr<PrefixAttributesASTBase> pAttrs) {
        long stc = 0L;
        if (pAttrs != null)
        {
            stc = (pAttrs.get()).storageClass;
            (pAttrs.get()).storageClass = 0L;
        }
        return stc;
    }


    // from template getStorageClass!(ASTCodegen)
    public static long getStorageClassASTCodegen(Ptr<PrefixAttributesASTCodegen> pAttrs) {
        long stc = 0L;
        if (pAttrs != null)
        {
            stc = (pAttrs.get()).storageClass;
            (pAttrs.get()).storageClass = 0L;
        }
        return stc;
    }


    public static boolean writeMixin(ByteSlice s, Loc loc) {
        if (global.params.mixinOut == null)
        {
            return false;
        }
        Ptr<OutBuffer> ob = global.params.mixinOut;
        (ob.get()).writestring(new ByteSlice("// expansion at "));
        (ob.get()).writestring(loc.toChars(global.params.showColumns.value));
        (ob.get()).writenl();
        global.params.mixinLines++;
        loc = new Loc(global.params.mixinFile, (global.params.mixinLines + 1), loc.charnum).copy();
        int lastpos = 0;
        {
            int i = 0;
            for (; (i < s.getLength());i += 1){
                byte c = s.get(i);
                if (((c & 0xFF) == 10) || ((c & 0xFF) == 13) && (i + 1 < s.getLength()) && ((s.get(i + 1) & 0xFF) == 10))
                {
                    (ob.get()).writestring(s.slice(lastpos,i));
                    (ob.get()).writenl();
                    global.params.mixinLines++;
                    if (((c & 0xFF) == 13))
                    {
                        i += 1;
                    }
                    lastpos = i + 1;
                }
            }
        }
        if ((lastpos < s.getLength()))
        {
            (ob.get()).writestring(s.slice(lastpos,s.getLength()));
        }
        if ((s.getLength() == 0) || ((s.get(s.getLength() - 1) & 0xFF) != 10))
        {
            (ob.get()).writenl();
            global.params.mixinLines++;
        }
        (ob.get()).writenl();
        global.params.mixinLines++;
        return true;
    }

    // from template Parser!(ASTBase)
    public static class ParserASTBase extends Lexer
    {
        public Ptr<ASTBase.ModuleDeclaration> md = null;
        public ASTBase.Module mod = null;
        public int linkage = 0;
        public int cppmangle = 0;
        public Loc endloc = new Loc();
        public int inBrackets = 0;
        public Loc lookingForElse = new Loc();
        public  ParserASTBase(Loc loc, ASTBase.Module _module, ByteSlice input, boolean doDocComment, DiagnosticReporter diagnosticReporter) {
            super(_module != null ? _module.srcfile.toChars() : null, toBytePtr(input), 0, input.getLength(), doDocComment, false, diagnosticReporter);
            this.scanloc = loc.copy();
            if (!writeMixin(input, this.scanloc) && (loc.filename != null))
            {
                BytePtr filename = pcopy(((BytePtr)Mem.xmalloc(strlen(loc.filename) + 7 + 12 + 1)));
                sprintf(filename, new BytePtr("%s-mixin-%d"), loc.filename, loc.linnum);
                this.scanloc.filename = pcopy(filename);
            }
            this.mod = _module;
            this.linkage = LINK.d;
        }

        public  ParserASTBase(ASTBase.Module _module, ByteSlice input, boolean doDocComment, DiagnosticReporter diagnosticReporter) {
            super(_module != null ? _module.srcfile.toChars() : null, toBytePtr(input), 0, input.getLength(), doDocComment, false, diagnosticReporter);
            this.mod = _module;
            this.linkage = LINK.d;
        }

        public  Ptr<DArray<ASTBase.Dsymbol>> parseModule() {
            BytePtr comment = pcopy(this.token.value.blockComment.value);
            boolean isdeprecated = false;
            ASTBase.Expression msg = null;
            Ptr<DArray<ASTBase.Expression>> udas = null;
            Ptr<DArray<ASTBase.Dsymbol>> decldefs = null;
            ASTBase.Dsymbol lastDecl = this.mod;
            Ptr<Token> tk = null;
            if (this.skipAttributes(ptr(this.token), ptr(tk)) && (((tk.get()).value & 0xFF) == 34))
            {
                for (; ((this.token.value.value & 0xFF) != 34);){
                    switch ((this.token.value.value & 0xFF))
                    {
                        case 174:
                            if (isdeprecated)
                            {
                                this.error(new BytePtr("there is only one deprecation attribute allowed for module declaration"));
                            }
                            isdeprecated = true;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.check(TOK.leftParentheses);
                                msg = this.parseAssignExp();
                                this.check(TOK.rightParentheses);
                            }
                            break;
                        case 225:
                            Ptr<DArray<ASTBase.Expression>> exps = null;
                            long stc = this.parseAttribute(ptr(exps));
                            if ((stc == 4294967296L) || (stc == 4398046511104L) || (stc == 137438953472L) || (stc == 8589934592L) || (stc == 17179869184L) || (stc == 34359738368L))
                            {
                                this.error(new BytePtr("`@%s` attribute for module declaration is not supported"), this.token.value.toChars());
                            }
                            else
                            {
                                udas = ASTBase.UserAttributeDeclaration.concat(udas, exps);
                            }
                            if (stc != 0)
                            {
                                this.nextToken();
                            }
                            break;
                        default:
                        this.error(new BytePtr("`module` expected instead of `%s`"), this.token.value.toChars());
                        this.nextToken();
                        break;
                    }
                }
            }
            if (udas != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                ASTBase.UserAttributeDeclaration udad = new ASTBase.UserAttributeDeclaration(udas, a);
                this.mod.userAttribDecl = udad;
            }
            try {
                if (((this.token.value.value & 0xFF) == 34))
                {
                    Loc loc = this.token.value.loc.copy();
                    this.nextToken();
                    if (((this.token.value.value & 0xFF) != 120))
                    {
                        this.error(new BytePtr("identifier expected following `module`"));
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                    Ptr<DArray<Identifier>> a = null;
                    Identifier id = this.token.value.ident;
                L_outer1:
                    for (; ((this.nextToken() & 0xFF) == 97);){
                        if (a == null)
                        {
                            a = refPtr(new DArray<Identifier>());
                        }
                        (a.get()).push(id);
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 120))
                        {
                            this.error(new BytePtr("identifier expected following `package`"));
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                        id = this.token.value.ident;
                    }
                    this.md = refPtr(new ASTBase.ModuleDeclaration(loc, a, id, msg, isdeprecated));
                    if (((this.token.value.value & 0xFF) != 9))
                    {
                        this.error(new BytePtr("`;` expected following module declaration instead of `%s`"), this.token.value.toChars());
                    }
                    this.nextToken();
                    this.addComment(this.mod, comment);
                }
                decldefs = this.parseDeclDefs(0, ptr(lastDecl), null);
                if (((this.token.value.value & 0xFF) != 11))
                {
                    this.error(this.token.value.loc, new BytePtr("unrecognized declaration"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                return decldefs;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            for (; ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                this.nextToken();
            }
            this.nextToken();
            return refPtr(new DArray<ASTBase.Dsymbol>());
        }

        public  long parseDeprecatedAttribute(Ref<ASTBase.Expression> msg) {
            if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
            {
                return 1024L;
            }
            this.nextToken();
            this.check(TOK.leftParentheses);
            ASTBase.Expression e = this.parseAssignExp();
            this.check(TOK.rightParentheses);
            if (msg.value != null)
            {
                this.error(new BytePtr("conflicting storage class `deprecated(%s)` and `deprecated(%s)`"), msg.value.toChars(), e.toChars());
            }
            msg.value = e;
            return 0L;
        }

        public  Ptr<DArray<ASTBase.Dsymbol>> parseDeclDefs(int once, Ptr<ASTBase.Dsymbol> pLastDecl, Ptr<PrefixAttributesASTBase> pAttrs) {
            ASTBase.Dsymbol lastDecl = null;
            if (pLastDecl == null)
            {
                pLastDecl = pcopy(ptr(lastDecl));
            }
            int linksave = this.linkage;
            Ptr<DArray<ASTBase.Dsymbol>> decldefs = refPtr(new DArray<ASTBase.Dsymbol>());
        L_outer2:
            do {
                {
                    ASTBase.Dsymbol s = null;
                    Ptr<DArray<ASTBase.Dsymbol>> a = null;
                    PrefixAttributesASTBase attrs = new PrefixAttributesASTBase();
                    if ((once == 0) || (pAttrs == null))
                    {
                        pAttrs = ptr(attrs);
                        (pAttrs.get()).comment = pcopy(this.token.value.blockComment.value);
                    }
                    int prot = ASTBase.Prot.Kind.undefined;
                    long stc = 0L;
                    ASTBase.Condition condition = null;
                    this.linkage = linksave;
                    {
                        int __dispatch1 = 0;
                        dispatched_1:
                        do {
                            switch (__dispatch1 != 0 ? __dispatch1 : (this.token.value.value & 0xFF))
                            {
                                case 156:
                                    Ptr<Token> t = this.peek(ptr(this.token));
                                    if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7))
                                    {
                                        s = this.parseEnum();
                                    }
                                    else if ((((t.get()).value & 0xFF) != 120))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                    else
                                    {
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7) || (((t.get()).value & 0xFF) == 9))
                                        {
                                            s = this.parseEnum();
                                        }
                                        else
                                        {
                                            /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                        }
                                    }
                                    break;
                                case 157:
                                    a = this.parseImport();
                                    break;
                                case 36:
                                    s = this.parseTemplateDeclaration(false);
                                    break;
                                case 162:
                                    Loc loc = this.token.value.loc.copy();
                                    switch ((this.peekNext() & 0xFF))
                                    {
                                        case 1:
                                            this.nextToken();
                                            Ptr<DArray<ASTBase.Expression>> exps = this.parseArguments();
                                            this.check(TOK.semicolon);
                                            s = new ASTBase.CompileDeclaration(loc, exps);
                                            break;
                                        case 36:
                                            this.nextToken();
                                            s = this.parseTemplateDeclaration(true);
                                            break;
                                        default:
                                        s = this.parseMixin();
                                        break;
                                    }
                                    break;
                                case 149:
                                case 150:
                                case 151:
                                case 148:
                                case 129:
                                case 130:
                                case 131:
                                case 132:
                                case 133:
                                case 134:
                                case 135:
                                case 136:
                                case 137:
                                case 138:
                                case 139:
                                case 140:
                                case 141:
                                case 142:
                                case 143:
                                case 144:
                                case 145:
                                case 146:
                                case 147:
                                case 128:
                                case 158:
                                case 120:
                                case 124:
                                case 39:
                                case 97:
                                case 229:
                                case 152:
                                case 155:
                                case 153:
                                case 154:
                                case 213:
                                /*Ldeclaration:*/
                                case -1:
                                __dispatch1 = 0;
                                    a = this.parseDeclarations(false, pAttrs, (pAttrs.get()).comment);
                                    if ((a != null) && ((a.get()).length != 0))
                                    {
                                        pLastDecl.set(0, (a.get()).get((a.get()).length - 1));
                                    }
                                    break;
                                case 123:
                                    if (((this.peekNext() & 0xFF) == 97))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                    s = this.parseCtor(pAttrs);
                                    break;
                                case 92:
                                    s = this.parseDtor(pAttrs);
                                    break;
                                case 207:
                                    Ptr<Token> t_1 = this.peek(ptr(this.token));
                                    if ((((t_1.get()).value & 0xFF) == 1) || (((t_1.get()).value & 0xFF) == 5))
                                    {
                                        s = this.parseInvariant(pAttrs);
                                        break;
                                    }
                                    this.error(new BytePtr("invariant body expected, not `%s`"), this.token.value.toChars());
                                    /*goto Lerror*/{ __dispatch1 = -2; continue dispatched_1; }
                                case 208:
                                    if (global.params.useUnitTests || global.params.doDocComments || global.params.doHdrGeneration)
                                    {
                                        s = this.parseUnitTest(pAttrs);
                                        if (pLastDecl.get() != null)
                                        {
                                            (pLastDecl.get()).ddocUnittest = (ASTBase.UnitTestDeclaration)s;
                                        }
                                    }
                                    else
                                    {
                                        Loc loc_1 = this.token.value.loc.copy();
                                        int braces = 0;
                                    L_outer3:
                                        for (; 1 != 0;){
                                            this.nextToken();
                                            {
                                                int __dispatch3 = 0;
                                                dispatched_3:
                                                do {
                                                    switch (__dispatch3 != 0 ? __dispatch3 : (this.token.value.value & 0xFF))
                                                    {
                                                        case 5:
                                                            braces += 1;
                                                            continue L_outer3;
                                                        case 6:
                                                            if ((braces -= 1) != 0)
                                                            {
                                                                continue L_outer3;
                                                            }
                                                            this.nextToken();
                                                            break;
                                                        case 11:
                                                            this.error(loc_1, new BytePtr("closing `}` of unittest not found before end of file"));
                                                            /*goto Lerror*/{ __dispatch1 = -2; continue dispatched_1; }
                                                        default:
                                                        continue L_outer3;
                                                    }
                                                } while(__dispatch3 != 0);
                                            }
                                            break;
                                        }
                                        s = new ASTBase.UnitTestDeclaration(loc_1, this.token.value.loc, 0L, null);
                                    }
                                    break;
                                case 22:
                                    s = this.parseNew(pAttrs);
                                    break;
                                case 23:
                                    s = this.parseDelete(pAttrs);
                                    break;
                                case 7:
                                case 5:
                                    this.error(new BytePtr("declaration expected, not `%s`"), this.token.value.toChars());
                                    /*goto Lerror*/{ __dispatch1 = -2; continue dispatched_1; }
                                case 6:
                                case 11:
                                    if (once != 0)
                                    {
                                        this.error(new BytePtr("declaration expected, not `%s`"), this.token.value.toChars());
                                    }
                                    return decldefs;
                                case 169:
                                    byte next = this.peekNext();
                                    if (((next & 0xFF) == 123))
                                    {
                                        s = this.parseStaticCtor(pAttrs);
                                    }
                                    else if (((next & 0xFF) == 92))
                                    {
                                        s = this.parseStaticDtor(pAttrs);
                                    }
                                    else if (((next & 0xFF) == 14))
                                    {
                                        s = this.parseStaticAssert();
                                    }
                                    else if (((next & 0xFF) == 183))
                                    {
                                        condition = this.parseStaticIfCondition();
                                        Ptr<DArray<ASTBase.Dsymbol>> athen = null;
                                        if (((this.token.value.value & 0xFF) == 7))
                                        {
                                            athen = this.parseBlock(pLastDecl, null);
                                        }
                                        else
                                        {
                                            Loc lookingForElseSave = this.lookingForElse.copy();
                                            this.lookingForElse = this.token.value.loc.copy();
                                            athen = this.parseBlock(pLastDecl, null);
                                            this.lookingForElse = lookingForElseSave.copy();
                                        }
                                        Ptr<DArray<ASTBase.Dsymbol>> aelse = null;
                                        if (((this.token.value.value & 0xFF) == 184))
                                        {
                                            Loc elseloc = this.token.value.loc.copy();
                                            this.nextToken();
                                            aelse = this.parseBlock(pLastDecl, null);
                                            this.checkDanglingElse(elseloc);
                                        }
                                        s = new ASTBase.StaticIfDeclaration(condition, athen, aelse);
                                    }
                                    else if (((next & 0xFF) == 157))
                                    {
                                        a = this.parseImport();
                                    }
                                    else if (((next & 0xFF) == 201) || ((next & 0xFF) == 202))
                                    {
                                        s = this.parseForeach11(this.loc(), pLastDecl);
                                    }
                                    else
                                    {
                                        stc = 1L;
                                        /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                    }
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                    stc = 4L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 182:
                                    if (((this.peekNext() & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                    stc = 1048576L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 224:
                                    byte next_1 = this.peekNext();
                                    if (((next_1 & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                    if (((next_1 & 0xFF) == 169))
                                    {
                                        byte next2 = this.peekNext2();
                                        if (((next2 & 0xFF) == 123))
                                        {
                                            s = this.parseSharedStaticCtor(pAttrs);
                                            break;
                                        }
                                        if (((next2 & 0xFF) == 92))
                                        {
                                            s = this.parseSharedStaticDtor(pAttrs);
                                            break;
                                        }
                                    }
                                    stc = 536870912L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 177:
                                    if (((this.peekNext() & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                    stc = 2147483648L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 170:
                                    stc = 8L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 179:
                                    stc = 256L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 203:
                                    stc = 524288L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 159:
                                    stc = 128L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 172:
                                    stc = 16L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 194:
                                    stc = 512L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 216:
                                    stc = 33554432L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 215:
                                    stc = 67108864L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 217:
                                    stc = 1073741824L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 225:
                                    {
                                        Ptr<DArray<ASTBase.Expression>> exps_1 = null;
                                        stc = this.parseAttribute(ptr(exps_1));
                                        if (stc != 0)
                                        {
                                            /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                        }
                                        (pAttrs.get()).udas = ASTBase.UserAttributeDeclaration.concat((pAttrs.get()).udas, exps_1);
                                        /*goto Lautodecl*/{ __dispatch1 = -4; continue dispatched_1; }
                                    }
                                /*Lstc:*/
                                case -3:
                                __dispatch1 = 0;
                                    (pAttrs.get()).storageClass = this.appendStorageClass((pAttrs.get()).storageClass, stc);
                                    this.nextToken();
                                /*Lautodecl:*/
                                case -4:
                                __dispatch1 = 0;
                                    if (((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign))
                                    {
                                        a = this.parseAutoDeclarations(getStorageClassASTBase(pAttrs), (pAttrs.get()).comment);
                                        if ((a != null) && ((a.get()).length != 0))
                                        {
                                            pLastDecl.set(0, (a.get()).get((a.get()).length - 1));
                                        }
                                        if ((pAttrs.get()).udas != null)
                                        {
                                            s = new ASTBase.UserAttributeDeclaration((pAttrs.get()).udas, a);
                                            (pAttrs.get()).udas = null;
                                        }
                                        break;
                                    }
                                    Ptr<Token> tk = null;
                                    if (((this.token.value.value & 0xFF) == 120) && this.skipParens(this.peek(ptr(this.token)), ptr(tk)) && this.skipAttributes(tk, ptr(tk)) && (((tk.get()).value & 0xFF) == 1) || (((tk.get()).value & 0xFF) == 5) || (((tk.get()).value & 0xFF) == 175) || (((tk.get()).value & 0xFF) == 176) || (((tk.get()).value & 0xFF) == 187) || (((tk.get()).value & 0xFF) == 120) && (pequals((tk.get()).ident, Id._body)))
                                    {
                                        a = this.parseDeclarations(true, pAttrs, (pAttrs.get()).comment);
                                        if ((a != null) && ((a.get()).length != 0))
                                        {
                                            pLastDecl.set(0, (a.get()).get((a.get()).length - 1));
                                        }
                                        if ((pAttrs.get()).udas != null)
                                        {
                                            s = new ASTBase.UserAttributeDeclaration((pAttrs.get()).udas, a);
                                            (pAttrs.get()).udas = null;
                                        }
                                        break;
                                    }
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    long stc2 = getStorageClassASTBase(pAttrs);
                                    if ((stc2 != 0L))
                                    {
                                        s = new ASTBase.StorageClassDeclaration(stc2, a);
                                    }
                                    if ((pAttrs.get()).udas != null)
                                    {
                                        if (s != null)
                                        {
                                            a = refPtr(new DArray<ASTBase.Dsymbol>());
                                            (a.get()).push(s);
                                        }
                                        s = new ASTBase.UserAttributeDeclaration((pAttrs.get()).udas, a);
                                        (pAttrs.get()).udas = null;
                                    }
                                    break;
                                case 174:
                                    ASTBase.Expression e = null;
                                    {
                                        long _stc = this.parseDeprecatedAttribute((pAttrs.get()).depmsg);
                                        if ((_stc) != 0)
                                        {
                                            stc = _stc;
                                            /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                        }
                                    }
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs.get()).depmsg != null)
                                    {
                                        s = new ASTBase.DeprecatedDeclaration((pAttrs.get()).depmsg, a);
                                        (pAttrs.get()).depmsg = null;
                                    }
                                    break;
                                case 3:
                                    if (((this.peekNext() & 0xFF) == 4))
                                    {
                                        this.error(new BytePtr("empty attribute list is not allowed"));
                                    }
                                    this.error(new BytePtr("use `@(attributes)` instead of `[attributes]`"));
                                    Ptr<DArray<ASTBase.Expression>> exps_2 = this.parseArguments();
                                    (pAttrs.get()).udas = ASTBase.UserAttributeDeclaration.concat((pAttrs.get()).udas, exps_2);
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs.get()).udas != null)
                                    {
                                        s = new ASTBase.UserAttributeDeclaration((pAttrs.get()).udas, a);
                                        (pAttrs.get()).udas = null;
                                    }
                                    break;
                                case 164:
                                    if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
                                    {
                                        stc = 2L;
                                        /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                    }
                                    Loc linkLoc = this.token.value.loc.copy();
                                    Ptr<DArray<Identifier>> idents = null;
                                    Ptr<DArray<ASTBase.Expression>> identExps = null;
                                    int cppmangle = CPPMANGLE.def;
                                    boolean cppMangleOnly = false;
                                    int link = this.parseLinkage(ptr(idents), ptr(identExps), cppmangle, cppMangleOnly);
                                    if (((pAttrs.get()).link != LINK.default_))
                                    {
                                        if (((pAttrs.get()).link != link))
                                        {
                                            this.error(new BytePtr("conflicting linkage `extern (%s)` and `extern (%s)`"), ASTBase.linkageToChars((pAttrs.get()).link), ASTBase.linkageToChars(link));
                                        }
                                        else if ((idents != null) || (identExps != null) || (cppmangle != CPPMANGLE.def))
                                        {
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("redundant linkage `extern (%s)`"), ASTBase.linkageToChars((pAttrs.get()).link));
                                        }
                                    }
                                    (pAttrs.get()).link = link;
                                    this.linkage = link;
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if (idents != null)
                                    {
                                        assert((link == LINK.cpp));
                                        assert((idents.get()).length.value != 0);
                                        {
                                            int i = (idents.get()).length.value;
                                            for (; i != 0;){
                                                Identifier id = (idents.get()).get(i -= 1);
                                                if (s != null)
                                                {
                                                    a = refPtr(new DArray<ASTBase.Dsymbol>());
                                                    (a.get()).push(s);
                                                }
                                                if (cppMangleOnly)
                                                {
                                                    s = new ASTBase.CPPNamespaceDeclaration(id, a);
                                                }
                                                else
                                                {
                                                    s = new ASTBase.Nspace(linkLoc, id, null, a);
                                                }
                                            }
                                        }
                                        (pAttrs.get()).link = LINK.default_;
                                    }
                                    else if (identExps != null)
                                    {
                                        assert((link == LINK.cpp));
                                        assert((identExps.get()).length != 0);
                                        {
                                            int i_1 = (identExps.get()).length;
                                            for (; i_1 != 0;){
                                                ASTBase.Expression exp = (identExps.get()).get(i_1 -= 1);
                                                if (s != null)
                                                {
                                                    a = refPtr(new DArray<ASTBase.Dsymbol>());
                                                    (a.get()).push(s);
                                                }
                                                if (cppMangleOnly)
                                                {
                                                    s = new ASTBase.CPPNamespaceDeclaration(exp, a);
                                                }
                                                else
                                                {
                                                    s = new ASTBase.Nspace(linkLoc, null, exp, a);
                                                }
                                            }
                                        }
                                        (pAttrs.get()).link = LINK.default_;
                                    }
                                    else if ((cppmangle != CPPMANGLE.def))
                                    {
                                        assert((link == LINK.cpp));
                                        s = new ASTBase.CPPMangleDeclaration(cppmangle, a);
                                    }
                                    else if (((pAttrs.get()).link != LINK.default_))
                                    {
                                        s = new ASTBase.LinkDeclaration((pAttrs.get()).link, a);
                                        (pAttrs.get()).link = LINK.default_;
                                    }
                                    break;
                                case 165:
                                    prot = ASTBase.Prot.Kind.private_;
                                    /*goto Lprot*/{ __dispatch1 = -5; continue dispatched_1; }
                                case 180:
                                    prot = ASTBase.Prot.Kind.package_;
                                    /*goto Lprot*/{ __dispatch1 = -5; continue dispatched_1; }
                                case 166:
                                    prot = ASTBase.Prot.Kind.protected_;
                                    /*goto Lprot*/{ __dispatch1 = -5; continue dispatched_1; }
                                case 167:
                                    prot = ASTBase.Prot.Kind.public_;
                                    /*goto Lprot*/{ __dispatch1 = -5; continue dispatched_1; }
                                case 168:
                                    prot = ASTBase.Prot.Kind.export_;
                                    /*goto Lprot*/{ __dispatch1 = -5; continue dispatched_1; }
                                /*Lprot:*/
                                case -5:
                                __dispatch1 = 0;
                                    {
                                        if (((pAttrs.get()).protection.kind != ASTBase.Prot.Kind.undefined))
                                        {
                                            if (((pAttrs.get()).protection.kind != prot))
                                            {
                                                this.error(new BytePtr("conflicting protection attribute `%s` and `%s`"), ASTBase.protectionToChars((pAttrs.get()).protection.kind), ASTBase.protectionToChars(prot));
                                            }
                                            else
                                            {
                                                this.error(new BytePtr("redundant protection attribute `%s`"), ASTBase.protectionToChars(prot));
                                            }
                                        }
                                        (pAttrs.get()).protection.kind = prot;
                                        this.nextToken();
                                        Ptr<DArray<Identifier>> pkg_prot_idents = null;
                                        if (((pAttrs.get()).protection.kind == ASTBase.Prot.Kind.package_) && ((this.token.value.value & 0xFF) == 1))
                                        {
                                            pkg_prot_idents = this.parseQualifiedIdentifier(new BytePtr("protection package"));
                                            if (pkg_prot_idents != null)
                                            {
                                                this.check(TOK.rightParentheses);
                                            }
                                            else
                                            {
                                                for (; ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                                                    this.nextToken();
                                                }
                                                this.nextToken();
                                                break;
                                            }
                                        }
                                        Loc attrloc = this.token.value.loc.copy();
                                        a = this.parseBlock(pLastDecl, pAttrs);
                                        if (((pAttrs.get()).protection.kind != ASTBase.Prot.Kind.undefined))
                                        {
                                            if (((pAttrs.get()).protection.kind == ASTBase.Prot.Kind.package_) && (pkg_prot_idents != null))
                                            {
                                                s = new ASTBase.ProtDeclaration(attrloc, pkg_prot_idents, a);
                                            }
                                            else
                                            {
                                                s = new ASTBase.ProtDeclaration(attrloc, (pAttrs.get()).protection, a);
                                            }
                                            (pAttrs.get()).protection = new ASTBase.Prot(ASTBase.Prot.Kind.undefined, null).copy();
                                        }
                                        break;
                                    }
                                case 163:
                                    Loc attrLoc = this.token.value.loc.copy();
                                    this.nextToken();
                                    ASTBase.Expression e_1 = null;
                                    if (((this.token.value.value & 0xFF) == 1))
                                    {
                                        this.nextToken();
                                        e_1 = this.parseAssignExp();
                                        this.check(TOK.rightParentheses);
                                    }
                                    if ((pAttrs.get()).setAlignment)
                                    {
                                        if (e_1 != null)
                                        {
                                            this.error(new BytePtr("redundant alignment attribute `align(%s)`"), e_1.toChars());
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("redundant alignment attribute `align`"));
                                        }
                                    }
                                    (pAttrs.get()).setAlignment = true;
                                    (pAttrs.get()).ealign = e_1;
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs.get()).setAlignment)
                                    {
                                        s = new ASTBase.AlignDeclaration(attrLoc, (pAttrs.get()).ealign, a);
                                        (pAttrs.get()).setAlignment = false;
                                        (pAttrs.get()).ealign = null;
                                    }
                                    break;
                                case 40:
                                    Ptr<DArray<ASTBase.Expression>> args = null;
                                    Loc loc_2 = this.token.value.loc.copy();
                                    this.nextToken();
                                    this.check(TOK.leftParentheses);
                                    if (((this.token.value.value & 0xFF) != 120))
                                    {
                                        this.error(new BytePtr("`pragma(identifier)` expected"));
                                        /*goto Lerror*/{ __dispatch1 = -2; continue dispatched_1; }
                                    }
                                    Identifier ident = this.token.value.ident;
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 99) && ((this.peekNext() & 0xFF) != 2))
                                    {
                                        args = this.parseArguments();
                                    }
                                    else
                                    {
                                        this.check(TOK.rightParentheses);
                                    }
                                    Ptr<DArray<ASTBase.Dsymbol>> a2 = null;
                                    if (((this.token.value.value & 0xFF) == 9))
                                    {
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        a2 = this.parseBlock(pLastDecl, null);
                                    }
                                    s = new ASTBase.PragmaDeclaration(loc_2, ident, args, a2);
                                    break;
                                case 173:
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) == 120))
                                        {
                                            s = new ASTBase.DebugSymbol(this.token.value.loc, this.token.value.ident);
                                        }
                                        else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                                        {
                                            s = new ASTBase.DebugSymbol(this.token.value.loc, (int)this.token.value.intvalue);
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("identifier or integer expected, not `%s`"), this.token.value.toChars());
                                            s = null;
                                        }
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 9))
                                        {
                                            this.error(new BytePtr("semicolon expected"));
                                        }
                                        this.nextToken();
                                        break;
                                    }
                                    condition = this.parseDebugCondition();
                                    /*goto Lcondition*/{ __dispatch1 = -6; continue dispatched_1; }
                                case 33:
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) == 120))
                                        {
                                            s = new ASTBase.VersionSymbol(this.token.value.loc, this.token.value.ident);
                                        }
                                        else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                                        {
                                            s = new ASTBase.VersionSymbol(this.token.value.loc, (int)this.token.value.intvalue);
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("identifier or integer expected, not `%s`"), this.token.value.toChars());
                                            s = null;
                                        }
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 9))
                                        {
                                            this.error(new BytePtr("semicolon expected"));
                                        }
                                        this.nextToken();
                                        break;
                                    }
                                    condition = this.parseVersionCondition();
                                    /*goto Lcondition*/{ __dispatch1 = -6; continue dispatched_1; }
                                /*Lcondition:*/
                                case -6:
                                __dispatch1 = 0;
                                    {
                                        Ptr<DArray<ASTBase.Dsymbol>> athen_1 = null;
                                        if (((this.token.value.value & 0xFF) == 7))
                                        {
                                            athen_1 = this.parseBlock(pLastDecl, null);
                                        }
                                        else
                                        {
                                            Loc lookingForElseSave_1 = this.lookingForElse.copy();
                                            this.lookingForElse = this.token.value.loc.copy();
                                            athen_1 = this.parseBlock(pLastDecl, null);
                                            this.lookingForElse = lookingForElseSave_1.copy();
                                        }
                                        Ptr<DArray<ASTBase.Dsymbol>> aelse_1 = null;
                                        if (((this.token.value.value & 0xFF) == 184))
                                        {
                                            Loc elseloc_1 = this.token.value.loc.copy();
                                            this.nextToken();
                                            aelse_1 = this.parseBlock(pLastDecl, null);
                                            this.checkDanglingElse(elseloc_1);
                                        }
                                        s = new ASTBase.ConditionalDeclaration(condition, athen_1, aelse_1);
                                        break;
                                    }
                                case 9:
                                    this.nextToken();
                                    continue L_outer2;
                                default:
                                this.error(new BytePtr("declaration expected, not `%s`"), this.token.value.toChars());
                            /*Lerror:*/
                            case -2:
                            __dispatch1 = 0;
                                for (; ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                                    this.nextToken();
                                }
                                this.nextToken();
                                s = null;
                                continue L_outer2;
                            }
                        } while(__dispatch1 != 0);
                    }
                    if (s != null)
                    {
                        if (s.isAttribDeclaration() == null)
                        {
                            pLastDecl.set(0, s);
                        }
                        (decldefs.get()).push(s);
                        this.addComment(s, (pAttrs.get()).comment);
                    }
                    else if ((a != null) && ((a.get()).length != 0))
                    {
                        (decldefs.get()).append(a);
                    }
                }
            } while (once == 0);
            this.linkage = linksave;
            return decldefs;
        }

        // defaulted all parameters starting with #3
        public  Ptr<DArray<ASTBase.Dsymbol>> parseDeclDefs(int once, Ptr<ASTBase.Dsymbol> pLastDecl) {
            return parseDeclDefs(once, pLastDecl, null);
        }

        // defaulted all parameters starting with #2
        public  Ptr<DArray<ASTBase.Dsymbol>> parseDeclDefs(int once) {
            return parseDeclDefs(once, null, null);
        }

        public  Ptr<DArray<ASTBase.Dsymbol>> parseAutoDeclarations(long storageClass, BytePtr comment) {
            Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
            for (; 1 != 0;){
                Loc loc = this.token.value.loc.copy();
                Identifier ident = this.token.value.ident;
                this.nextToken();
                Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
                if (((this.token.value.value & 0xFF) == 1))
                {
                    tpl = this.parseTemplateParameterList(0);
                }
                this.check(TOK.assign);
                ASTBase.Initializer _init = this.parseInitializer();
                ASTBase.VarDeclaration v = new ASTBase.VarDeclaration(loc, null, ident, _init, storageClass);
                ASTBase.Dsymbol s = v;
                if (tpl != null)
                {
                    Ptr<DArray<ASTBase.Dsymbol>> a2 = refPtr(new DArray<ASTBase.Dsymbol>());
                    (a2.get()).push(v);
                    ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                    s = tempdecl;
                }
                (a.get()).push(s);
                switch ((this.token.value.value & 0xFF))
                {
                    case 9:
                        this.nextToken();
                        this.addComment(s, comment);
                        break;
                    case 99:
                        this.nextToken();
                        if (!(((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign)))
                        {
                            this.error(new BytePtr("identifier expected following comma"));
                            break;
                        }
                        this.addComment(s, comment);
                        continue;
                    default:
                    this.error(new BytePtr("semicolon expected following auto declaration, not `%s`"), this.token.value.toChars());
                    break;
                }
                break;
            }
            return a;
        }

        public  Ptr<DArray<ASTBase.Dsymbol>> parseBlock(Ptr<ASTBase.Dsymbol> pLastDecl, Ptr<PrefixAttributesASTBase> pAttrs) {
            Ptr<DArray<ASTBase.Dsymbol>> a = null;
            switch ((this.token.value.value & 0xFF))
            {
                case 9:
                    this.error(new BytePtr("declaration expected following attribute, not `;`"));
                    this.nextToken();
                    break;
                case 11:
                    this.error(new BytePtr("declaration expected following attribute, not end of file"));
                    break;
                case 5:
                    Loc lookingForElseSave = this.lookingForElse.copy();
                    this.lookingForElse = new Loc(null, 0, 0).copy();
                    this.nextToken();
                    a = this.parseDeclDefs(0, pLastDecl, null);
                    if (((this.token.value.value & 0xFF) != 6))
                    {
                        this.error(new BytePtr("matching `}` expected, not `%s`"), this.token.value.toChars());
                    }
                    else
                    {
                        this.nextToken();
                    }
                    this.lookingForElse = lookingForElseSave.copy();
                    break;
                case 7:
                    this.nextToken();
                    a = this.parseDeclDefs(0, pLastDecl, null);
                    break;
                default:
                a = this.parseDeclDefs(1, pLastDecl, pAttrs);
                break;
            }
            return a;
        }

        // defaulted all parameters starting with #2
        public  Ptr<DArray<ASTBase.Dsymbol>> parseBlock(Ptr<ASTBase.Dsymbol> pLastDecl) {
            return parseBlock(pLastDecl, null);
        }

        public  long appendStorageClass(long storageClass, long stc) {
            if (((storageClass & stc) != 0) || ((storageClass & 2048L) != 0) && ((stc & 524292L) != 0) || ((stc & 2048L) != 0) && ((storageClass & 524292L) != 0))
            {
                OutBuffer buf = new OutBuffer();
                try {
                    ASTBase.stcToBuffer(ptr(buf), stc);
                    this.error(new BytePtr("redundant attribute `%s`"), buf.peekChars());
                    return storageClass | stc;
                }
                finally {
                }
            }
            storageClass |= stc;
            if ((stc & 9437188L) != 0)
            {
                long u = storageClass & 9437188L;
                if ((u & u - 1L) != 0)
                {
                    this.error(new BytePtr("conflicting attribute `%s`"), Token.toChars(this.token.value.value));
                }
            }
            if ((stc & 1744830464L) != 0)
            {
                long u = storageClass & 1744830464L;
                if ((u & u - 1L) != 0)
                {
                    this.error(new BytePtr("conflicting attribute `%s`"), Token.toChars(this.token.value.value));
                }
            }
            if ((stc & 60129542144L) != 0)
            {
                long u = storageClass & 60129542144L;
                if ((u & u - 1L) != 0)
                {
                    this.error(new BytePtr("conflicting attribute `@%s`"), this.token.value.toChars());
                }
            }
            return storageClass;
        }

        public  long parseAttribute(Ptr<Ptr<DArray<ASTBase.Expression>>> pudas) {
            this.nextToken();
            Ptr<DArray<ASTBase.Expression>> udas = null;
            long stc = 0L;
            if (((this.token.value.value & 0xFF) == 120))
            {
                if ((pequals(this.token.value.ident, Id.property)))
                {
                    stc = 4294967296L;
                }
                else if ((pequals(this.token.value.ident, Id.nogc)))
                {
                    stc = 4398046511104L;
                }
                else if ((pequals(this.token.value.ident, Id.safe)))
                {
                    stc = 8589934592L;
                }
                else if ((pequals(this.token.value.ident, Id.trusted)))
                {
                    stc = 17179869184L;
                }
                else if ((pequals(this.token.value.ident, Id.system)))
                {
                    stc = 34359738368L;
                }
                else if ((pequals(this.token.value.ident, Id.disable)))
                {
                    stc = 137438953472L;
                }
                else if ((pequals(this.token.value.ident, Id.future)))
                {
                    stc = 1125899906842624L;
                }
                else
                {
                    ASTBase.Expression exp = this.parsePrimaryExp();
                    if (((this.token.value.value & 0xFF) == 1))
                    {
                        Loc loc = this.token.value.loc.copy();
                        exp = new ASTBase.CallExp(loc, exp, this.parseArguments());
                    }
                    udas = refPtr(new DArray<ASTBase.Expression>());
                    (udas.get()).push(exp);
                }
            }
            else if (((this.token.value.value & 0xFF) == 1))
            {
                if (((this.peekNext() & 0xFF) == 2))
                {
                    this.error(new BytePtr("empty attribute list is not allowed"));
                }
                udas = this.parseArguments();
            }
            else
            {
                this.error(new BytePtr("@identifier or @(ArgumentList) expected, not `@%s`"), this.token.value.toChars());
            }
            if (stc != 0)
            {
            }
            else if (udas != null)
            {
                pudas.set(0, ASTBase.UserAttributeDeclaration.concat(pudas.get(), udas));
            }
            else
            {
                this.error(new BytePtr("valid attributes are `@property`, `@safe`, `@trusted`, `@system`, `@disable`, `@nogc`"));
            }
            return stc;
        }

        public  long parsePostfix(long storageClass, Ptr<Ptr<DArray<ASTBase.Expression>>> pudas) {
            for (; 1 != 0;){
                long stc = 0L;
                switch ((this.token.value.value & 0xFF))
                {
                    case 171:
                        stc = 4L;
                        break;
                    case 182:
                        stc = 1048576L;
                        break;
                    case 224:
                        stc = 536870912L;
                        break;
                    case 177:
                        stc = 2147483648L;
                        break;
                    case 216:
                        stc = 33554432L;
                        break;
                    case 215:
                        stc = 67108864L;
                        break;
                    case 195:
                        stc = 17592186044416L;
                        break;
                    case 203:
                        stc = 524288L;
                        break;
                    case 225:
                        Ptr<DArray<ASTBase.Expression>> udas = null;
                        stc = this.parseAttribute(ptr(udas));
                        if (udas != null)
                        {
                            if (pudas != null)
                            {
                                pudas.set(0, ASTBase.UserAttributeDeclaration.concat(pudas.get(), udas));
                            }
                            else
                            {
                                this.error(new BytePtr("user-defined attributes cannot appear as postfixes"));
                            }
                            continue;
                        }
                        break;
                    default:
                    return storageClass;
                }
                storageClass = this.appendStorageClass(storageClass, stc);
                this.nextToken();
            }
        }

        public  long parseTypeCtor() {
            long storageClass = 0L;
            for (; 1 != 0;){
                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                {
                    return storageClass;
                }
                long stc = 0L;
                switch ((this.token.value.value & 0xFF))
                {
                    case 171:
                        stc = 4L;
                        break;
                    case 182:
                        stc = 1048576L;
                        break;
                    case 224:
                        stc = 536870912L;
                        break;
                    case 177:
                        stc = 2147483648L;
                        break;
                    default:
                    return storageClass;
                }
                storageClass = this.appendStorageClass(storageClass, stc);
                this.nextToken();
            }
        }

        public  ASTBase.Expression parseConstraint() {
            ASTBase.Expression e = null;
            if (((this.token.value.value & 0xFF) == 183))
            {
                this.nextToken();
                this.check(TOK.leftParentheses);
                e = this.parseExpression();
                this.check(TOK.rightParentheses);
            }
            return e;
        }

        public  ASTBase.TemplateDeclaration parseTemplateDeclaration(boolean ismixin) {
            ASTBase.TemplateDeclaration tempdecl = null;
            Identifier id = null;
            Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
            Ptr<DArray<ASTBase.Dsymbol>> decldefs = null;
            ASTBase.Expression constraint = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            try {
                if (((this.token.value.value & 0xFF) != 120))
                {
                    this.error(new BytePtr("identifier expected following `template`"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                id = this.token.value.ident;
                this.nextToken();
                tpl = this.parseTemplateParameterList(0);
                if (tpl == null)
                {
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                constraint = this.parseConstraint();
                if (((this.token.value.value & 0xFF) != 5))
                {
                    this.error(new BytePtr("members of template declaration expected"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                decldefs = this.parseBlock(null, null);
                tempdecl = new ASTBase.TemplateDeclaration(loc, id, tpl, constraint, decldefs, ismixin, false);
                return tempdecl;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            return null;
        }

        // defaulted all parameters starting with #1
        public  ASTBase.TemplateDeclaration parseTemplateDeclaration() {
            return parseTemplateDeclaration(false);
        }

        public  Ptr<DArray<ASTBase.TemplateParameter>> parseTemplateParameterList(int flag) {
            Ptr<DArray<ASTBase.TemplateParameter>> tpl = refPtr(new DArray<ASTBase.TemplateParameter>());
            try {
                if ((flag == 0) && ((this.token.value.value & 0xFF) != 1))
                {
                    this.error(new BytePtr("parenthesized template parameter list expected following template identifier"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                this.nextToken();
                if ((flag != 0) || ((this.token.value.value & 0xFF) != 2))
                {
                    int isvariadic = 0;
                L_outer4:
                    for (; ((this.token.value.value & 0xFF) != 2);){
                        ASTBase.TemplateParameter tp = null;
                        Loc loc = new Loc();
                        Identifier tp_ident = null;
                        ASTBase.Type tp_spectype = null;
                        ASTBase.Type tp_valtype = null;
                        ASTBase.Type tp_defaulttype = null;
                        ASTBase.Expression tp_specvalue = null;
                        ASTBase.Expression tp_defaultvalue = null;
                        Ptr<Token> t = null;
                        t = this.peek(ptr(this.token));
                        if (((this.token.value.value & 0xFF) == 158))
                        {
                            this.nextToken();
                            loc = this.token.value.loc.copy();
                            ASTBase.Type spectype = null;
                            if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.must, TOK.reserved, null))
                            {
                                spectype = this.parseType(ptr(tp_ident), null);
                            }
                            else
                            {
                                if (((this.token.value.value & 0xFF) != 120))
                                {
                                    this.error(new BytePtr("identifier expected for template alias parameter"));
                                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                                }
                                tp_ident = this.token.value.ident;
                                this.nextToken();
                            }
                            RootObject spec = null;
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                                {
                                    spec = this.parseType(null, null);
                                }
                                else
                                {
                                    spec = this.parseCondExp();
                                }
                            }
                            RootObject def = null;
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                                {
                                    def = this.parseType(null, null);
                                }
                                else
                                {
                                    def = this.parseCondExp();
                                }
                            }
                            tp = new ASTBase.TemplateAliasParameter(loc, tp_ident, spectype, spec, def);
                        }
                        else if ((((t.get()).value & 0xFF) == 7) || (((t.get()).value & 0xFF) == 90) || (((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 2))
                        {
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("identifier expected for template type parameter"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            loc = this.token.value.loc.copy();
                            tp_ident = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                tp_spectype = this.parseType(null, null);
                            }
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                tp_defaulttype = this.parseType(null, null);
                            }
                            tp = new ASTBase.TemplateTypeParameter(loc, tp_ident, tp_spectype, tp_defaulttype);
                        }
                        else if (((this.token.value.value & 0xFF) == 120) && (((t.get()).value & 0xFF) == 10))
                        {
                            if (isvariadic != 0)
                            {
                                this.error(new BytePtr("variadic template parameter must be last"));
                            }
                            isvariadic = 1;
                            loc = this.token.value.loc.copy();
                            tp_ident = this.token.value.ident;
                            this.nextToken();
                            this.nextToken();
                            tp = new ASTBase.TemplateTupleParameter(loc, tp_ident);
                        }
                        else if (((this.token.value.value & 0xFF) == 123))
                        {
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("identifier expected for template this parameter"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            loc = this.token.value.loc.copy();
                            tp_ident = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                tp_spectype = this.parseType(null, null);
                            }
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                tp_defaulttype = this.parseType(null, null);
                            }
                            tp = new ASTBase.TemplateThisParameter(loc, tp_ident, tp_spectype, tp_defaulttype);
                        }
                        else
                        {
                            loc = this.token.value.loc.copy();
                            tp_valtype = this.parseType(ptr(tp_ident), null);
                            if (tp_ident == null)
                            {
                                this.error(new BytePtr("identifier expected for template value parameter"));
                                tp_ident = Identifier.idPool(new ByteSlice("error"));
                            }
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                tp_specvalue = this.parseCondExp();
                            }
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                tp_defaultvalue = this.parseDefaultInitExp();
                            }
                            tp = new ASTBase.TemplateValueParameter(loc, tp_ident, tp_valtype, tp_specvalue, tp_defaultvalue);
                        }
                        (tpl.get()).push(tp);
                        if (((this.token.value.value & 0xFF) != 99))
                        {
                            break;
                        }
                        this.nextToken();
                    }
                }
                this.check(TOK.rightParentheses);
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            return tpl;
        }

        // defaulted all parameters starting with #1
        public  Ptr<DArray<ASTBase.TemplateParameter>> parseTemplateParameterList() {
            return parseTemplateParameterList(0);
        }

        public  ASTBase.Dsymbol parseMixin() {
            ASTBase.TemplateMixin tm = null;
            Identifier id = null;
            Ptr<DArray<RootObject>> tiargs = null;
            Loc locMixin = this.token.value.loc.copy();
            this.nextToken();
            Loc loc = this.token.value.loc.copy();
            ASTBase.TypeQualified tqual = null;
            if (((this.token.value.value & 0xFF) == 97))
            {
                id = Id.empty.value;
            }
            else
            {
                if (((this.token.value.value & 0xFF) == 39))
                {
                    tqual = this.parseTypeof();
                    this.check(TOK.dot);
                }
                if (((this.token.value.value & 0xFF) != 120))
                {
                    this.error(new BytePtr("identifier expected, not `%s`"), this.token.value.toChars());
                    id = Id.empty.value;
                }
                else
                {
                    id = this.token.value.ident;
                }
                this.nextToken();
            }
            for (; 1 != 0;){
                tiargs = null;
                if (((this.token.value.value & 0xFF) == 91))
                {
                    tiargs = this.parseTemplateArguments();
                }
                if ((tiargs != null) && ((this.token.value.value & 0xFF) == 97))
                {
                    ASTBase.TemplateInstance tempinst = new ASTBase.TemplateInstance(loc, id, tiargs);
                    if (tqual == null)
                    {
                        tqual = new ASTBase.TypeInstance(loc, tempinst);
                    }
                    else
                    {
                        tqual.addInst(tempinst);
                    }
                    tiargs = null;
                }
                else
                {
                    if (tqual == null)
                    {
                        tqual = new ASTBase.TypeIdentifier(loc, id);
                    }
                    else
                    {
                        tqual.addIdent(id);
                    }
                }
                if (((this.token.value.value & 0xFF) != 97))
                {
                    break;
                }
                this.nextToken();
                if (((this.token.value.value & 0xFF) != 120))
                {
                    this.error(new BytePtr("identifier expected following `.` instead of `%s`"), this.token.value.toChars());
                    break;
                }
                loc = this.token.value.loc.copy();
                id = this.token.value.ident;
                this.nextToken();
            }
            id = null;
            if (((this.token.value.value & 0xFF) == 120))
            {
                id = this.token.value.ident;
                this.nextToken();
            }
            tm = new ASTBase.TemplateMixin(locMixin, id, tqual, tiargs);
            if (((this.token.value.value & 0xFF) != 9))
            {
                this.error(new BytePtr("`;` expected after mixin"));
            }
            this.nextToken();
            return tm;
        }

        public  Ptr<DArray<RootObject>> parseTemplateArguments() {
            Ptr<DArray<RootObject>> tiargs = null;
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1))
            {
                tiargs = this.parseTemplateArgumentList();
            }
            else
            {
                tiargs = this.parseTemplateSingleArgument();
            }
            if (((this.token.value.value & 0xFF) == 91))
            {
                byte tok = this.peekNext();
                if (((tok & 0xFF) != 63) && ((tok & 0xFF) != 175))
                {
                    this.error(new BytePtr("multiple ! arguments are not allowed"));
                    while(true) try {
                    /*Lagain:*/
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 1))
                        {
                            this.parseTemplateArgumentList();
                        }
                        else
                        {
                            this.parseTemplateSingleArgument();
                        }
                        if (((this.token.value.value & 0xFF) == 91) && (((tok = this.peekNext()) & 0xFF) != 63) && ((tok & 0xFF) != 175))
                        {
                            /*goto Lagain*/throw Dispatch0.INSTANCE;
                        }
                        break;
                    } catch(Dispatch0 __d){}
                }
            }
            return tiargs;
        }

        public  Ptr<DArray<RootObject>> parseTemplateArgumentList() {
            Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
            byte endtok = TOK.rightParentheses;
            assert(((this.token.value.value & 0xFF) == 1) || ((this.token.value.value & 0xFF) == 99));
            this.nextToken();
            for (; ((this.token.value.value & 0xFF) != (endtok & 0xFF));){
                if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                {
                    ASTBase.Type ta = this.parseType(null, null);
                    (tiargs.get()).push(ta);
                }
                else
                {
                    ASTBase.Expression ea = this.parseAssignExp();
                    (tiargs.get()).push(ea);
                }
                if (((this.token.value.value & 0xFF) != 99))
                {
                    break;
                }
                this.nextToken();
            }
            this.check(endtok, new BytePtr("template argument list"));
            return tiargs;
        }

        public  Ptr<DArray<RootObject>> parseTemplateSingleArgument() {
            Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
            ASTBase.Type ta = null;
            {
                int __dispatch8 = 0;
                dispatched_8:
                do {
                    switch (__dispatch8 != 0 ? __dispatch8 : (this.token.value.value & 0xFF))
                    {
                        case 120:
                            ta = new ASTBase.TypeIdentifier(this.token.value.loc, this.token.value.ident);
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 229:
                            ta = this.parseVector();
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 128:
                            ta = ASTBase.Type.tvoid;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 129:
                            ta = ASTBase.Type.tint8;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 130:
                            ta = ASTBase.Type.tuns8;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 131:
                            ta = ASTBase.Type.tint16;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 132:
                            ta = ASTBase.Type.tuns16;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 133:
                            ta = ASTBase.Type.tint32;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 134:
                            ta = ASTBase.Type.tuns32;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 135:
                            ta = ASTBase.Type.tint64;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 136:
                            ta = ASTBase.Type.tuns64;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 137:
                            ta = ASTBase.Type.tint128;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 138:
                            ta = ASTBase.Type.tuns128;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 139:
                            ta = ASTBase.Type.tfloat32;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 140:
                            ta = ASTBase.Type.tfloat64;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 141:
                            ta = ASTBase.Type.tfloat80;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 142:
                            ta = ASTBase.Type.timaginary32;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 143:
                            ta = ASTBase.Type.timaginary64;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 144:
                            ta = ASTBase.Type.timaginary80;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 145:
                            ta = ASTBase.Type.tcomplex32;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 146:
                            ta = ASTBase.Type.tcomplex64;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 147:
                            ta = ASTBase.Type.tcomplex80;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 151:
                            ta = ASTBase.Type.tbool;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 148:
                            ta = ASTBase.Type.tchar;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 149:
                            ta = ASTBase.Type.twchar;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        case 150:
                            ta = ASTBase.Type.tdchar;
                            /*goto LabelX*/{ __dispatch8 = -1; continue dispatched_8; }
                        /*LabelX:*/
                        case -1:
                        __dispatch8 = 0;
                            (tiargs.get()).push(ta);
                            this.nextToken();
                            break;
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 13:
                        case 15:
                        case 16:
                        case 117:
                        case 118:
                        case 119:
                        case 121:
                        case 122:
                        case 219:
                        case 220:
                        case 218:
                        case 221:
                        case 222:
                        case 223:
                        case 123:
                            ASTBase.Expression ea = this.parsePrimaryExp();
                            (tiargs.get()).push(ea);
                            break;
                        default:
                        this.error(new BytePtr("template argument expected following `!`"));
                        break;
                    }
                } while(__dispatch8 != 0);
            }
            return tiargs;
        }

        public  ASTBase.StaticAssert parseStaticAssert() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression exp = null;
            ASTBase.Expression msg = null;
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            exp = this.parseAssignExp();
            if (((this.token.value.value & 0xFF) == 99))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) != 2))
                {
                    msg = this.parseAssignExp();
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                    }
                }
            }
            this.check(TOK.rightParentheses);
            this.check(TOK.semicolon);
            return new ASTBase.StaticAssert(loc, exp, msg);
        }

        public  ASTBase.TypeQualified parseTypeof() {
            ASTBase.TypeQualified t = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            this.check(TOK.leftParentheses);
            if (((this.token.value.value & 0xFF) == 195))
            {
                this.nextToken();
                t = new ASTBase.TypeReturn(loc);
            }
            else
            {
                ASTBase.Expression exp = this.parseExpression();
                t = new ASTBase.TypeTypeof(loc, exp);
            }
            this.check(TOK.rightParentheses);
            return t;
        }

        public  ASTBase.Type parseVector() {
            this.nextToken();
            this.check(TOK.leftParentheses);
            ASTBase.Type tb = this.parseType(null, null);
            this.check(TOK.rightParentheses);
            return new ASTBase.TypeVector(tb);
        }

        public  int parseLinkage(Ptr<Ptr<DArray<Identifier>>> pidents, Ptr<Ptr<DArray<ASTBase.Expression>>> pIdentExps, IntRef cppmangle, Ref<Boolean> cppMangleOnly) {
            cppmangle.value = CPPMANGLE.def;
            cppMangleOnly.value = false;
            Ptr<DArray<Identifier>> idents = null;
            Ptr<DArray<ASTBase.Expression>> identExps = null;
            cppmangle.value = CPPMANGLE.def;
            int link = LINK.d;
            this.nextToken();
            assert(((this.token.value.value & 0xFF) == 1));
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 120))
            {
                Identifier id = this.token.value.ident;
                this.nextToken();
                if ((pequals(id, Id.Windows)))
                {
                    link = LINK.windows;
                }
                else if ((pequals(id, Id.Pascal)))
                {
                    this.deprecation(new BytePtr("`extern(Pascal)` is deprecated. You might want to use `extern(Windows)` instead."));
                    link = LINK.pascal;
                }
                else if ((pequals(id, Id.D)))
                {
                }
                else if ((pequals(id, Id.C)))
                {
                    link = LINK.c;
                    if (((this.token.value.value & 0xFF) == 93))
                    {
                        link = LINK.cpp;
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 99))
                        {
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 153) || ((this.token.value.value & 0xFF) == 152))
                            {
                                cppmangle.value = ((this.token.value.value & 0xFF) == 153) ? CPPMANGLE.asClass : CPPMANGLE.asStruct;
                                this.nextToken();
                            }
                            else if (((this.token.value.value & 0xFF) == 120))
                            {
                                idents = refPtr(new DArray<Identifier>());
                                for (; 1 != 0;){
                                    Identifier idn = this.token.value.ident;
                                    (idents.get()).push(idn);
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 97))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) == 120))
                                        {
                                            continue;
                                        }
                                        this.error(new BytePtr("identifier expected for C++ namespace"));
                                        idents = null;
                                    }
                                    break;
                                }
                            }
                            else
                            {
                                cppMangleOnly.value = true;
                                identExps = refPtr(new DArray<ASTBase.Expression>());
                                for (; 1 != 0;){
                                    (identExps.get()).push(this.parseCondExp());
                                    if (((this.token.value.value & 0xFF) != 99))
                                    {
                                        break;
                                    }
                                    this.nextToken();
                                }
                            }
                        }
                    }
                }
                else if ((pequals(id, Id.Objective)))
                {
                    if (((this.token.value.value & 0xFF) == 75))
                    {
                        this.nextToken();
                        if ((pequals(this.token.value.ident, Id.C)))
                        {
                            link = LINK.objc;
                            this.nextToken();
                        }
                        else
                        {
                            /*goto LinvalidLinkage*//*unrolled goto*/
                            link = LINK.system;
                        }
                    }
                    else
                    {
                        /*goto LinvalidLinkage*//*unrolled goto*/
                        link = LINK.system;
                    }
                }
                else if ((pequals(id, Id.System)))
                {
                    link = LINK.system;
                }
                else
                {
                /*LinvalidLinkage:*/
                    this.error(new BytePtr("valid linkage identifiers are `D`, `C`, `C++`, `Objective-C`, `Pascal`, `Windows`, `System`"));
                    link = LINK.d;
                }
            }
            this.check(TOK.rightParentheses);
            pidents.set(0, idents);
            pIdentExps.set(0, identExps);
            return link;
        }

        public  Ptr<DArray<Identifier>> parseQualifiedIdentifier(BytePtr entity) {
            Ptr<DArray<Identifier>> qualified = null;
            do {
                {
                    this.nextToken();
                    if (((this.token.value.value & 0xFF) != 120))
                    {
                        this.error(new BytePtr("`%s` expected as dot-separated identifiers, got `%s`"), entity, this.token.value.toChars());
                        return null;
                    }
                    Identifier id = this.token.value.ident;
                    if (qualified == null)
                    {
                        qualified = refPtr(new DArray<Identifier>());
                    }
                    (qualified.get()).push(id);
                    this.nextToken();
                }
            } while (((this.token.value.value & 0xFF) == 97));
            return qualified;
        }

        public  ASTBase.Condition parseDebugCondition() {
            int level = 1;
            Identifier id = null;
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 120))
                {
                    id = this.token.value.ident;
                }
                else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                {
                    level = (int)this.token.value.intvalue;
                }
                else
                {
                    this.error(new BytePtr("identifier or integer expected inside debug(...), not `%s`"), this.token.value.toChars());
                }
                this.nextToken();
                this.check(TOK.rightParentheses);
            }
            return new ASTBase.DebugCondition(this.mod, level, id);
        }

        public  ASTBase.Condition parseVersionCondition() {
            int level = 1;
            Identifier id = null;
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 120))
                {
                    id = this.token.value.ident;
                }
                else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                {
                    level = (int)this.token.value.intvalue;
                }
                else if (((this.token.value.value & 0xFF) == 208))
                {
                    id = Identifier.idPool(Token.asString(TOK.unittest_));
                }
                else if (((this.token.value.value & 0xFF) == 14))
                {
                    id = Identifier.idPool(Token.asString(TOK.assert_));
                }
                else
                {
                    this.error(new BytePtr("identifier or integer expected inside version(...), not `%s`"), this.token.value.toChars());
                }
                this.nextToken();
                this.check(TOK.rightParentheses);
            }
            else
            {
                this.error(new BytePtr("(condition) expected following `version`"));
            }
            return new ASTBase.VersionCondition(this.mod, level, id);
        }

        public  ASTBase.Condition parseStaticIfCondition() {
            ASTBase.Expression exp = null;
            ASTBase.Condition condition = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                exp = this.parseAssignExp();
                this.check(TOK.rightParentheses);
            }
            else
            {
                this.error(new BytePtr("(expression) expected following `static if`"));
                exp = null;
            }
            condition = new ASTBase.StaticIfCondition(loc, exp);
            return condition;
        }

        public  ASTBase.Dsymbol parseCtor(Ptr<PrefixAttributesASTBase> pAttrs) {
            Ptr<DArray<ASTBase.Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1) && ((this.peekNext() & 0xFF) == 123) && ((this.peekNext2() & 0xFF) == 2))
            {
                this.nextToken();
                this.nextToken();
                this.check(TOK.rightParentheses);
                stc = this.parsePostfix(stc, ptr(udas));
                if ((stc & 1048576L) != 0)
                {
                    this.deprecation(new BytePtr("`immutable` postblit is deprecated. Please use an unqualified postblit."));
                }
                if ((stc & 536870912L) != 0)
                {
                    this.deprecation(new BytePtr("`shared` postblit is deprecated. Please use an unqualified postblit."));
                }
                if ((stc & 4L) != 0)
                {
                    this.deprecation(new BytePtr("`const` postblit is deprecated. Please use an unqualified postblit."));
                }
                if ((stc & 1L) != 0)
                {
                    this.error(loc, new BytePtr("postblit cannot be `static`"));
                }
                ASTBase.PostBlitDeclaration f = new ASTBase.PostBlitDeclaration(loc, Loc.initial.value, stc, Id.postblit.value);
                ASTBase.Dsymbol s = this.parseContracts(f);
                if (udas != null)
                {
                    Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                    (a.get()).push(f);
                    s = new ASTBase.UserAttributeDeclaration(udas, a);
                }
                return s;
            }
            Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
            if (((this.token.value.value & 0xFF) == 1) && (((this.peekPastParen(ptr(this.token)).get()).value & 0xFF) == 1))
            {
                tpl = this.parseTemplateParameterList(0);
            }
            int varargs = ASTBase.VarArg.none;
            Ptr<DArray<ASTBase.Parameter>> parameters = this.parseParameters(ptr(varargs), null);
            stc = this.parsePostfix(stc, ptr(udas));
            if ((varargs != ASTBase.VarArg.none) || (ASTBase.Parameter.dim(parameters) != 0))
            {
                if ((stc & 1L) != 0)
                {
                    this.error(loc, new BytePtr("constructor cannot be static"));
                }
            }
            else {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    if ((ss == 1L))
                    {
                        this.error(loc, new BytePtr("use `static this()` to declare a static constructor"));
                    }
                    else if ((ss == 536870913L))
                    {
                        this.error(loc, new BytePtr("use `shared static this()` to declare a shared static constructor"));
                    }
                }
            }
            ASTBase.Expression constraint = tpl != null ? this.parseConstraint() : null;
            ASTBase.Type tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs), null, this.linkage, stc);
            tf = tf.addSTC(stc);
            ASTBase.CtorDeclaration f = new ASTBase.CtorDeclaration(loc, Loc.initial.value, stc, tf, false);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                (a.get()).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas, a);
            }
            if (tpl != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> decldefs = refPtr(new DArray<ASTBase.Dsymbol>());
                (decldefs.get()).push(s);
                s = new ASTBase.TemplateDeclaration(loc, f.ident, tpl, constraint, decldefs, false, false);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseDtor(Ptr<PrefixAttributesASTBase> pAttrs) {
            Ptr<DArray<ASTBase.Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            this.check(TOK.this_);
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc, ptr(udas));
            {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    if ((ss == 1L))
                    {
                        this.error(loc, new BytePtr("use `static ~this()` to declare a static destructor"));
                    }
                    else if ((ss == 536870913L))
                    {
                        this.error(loc, new BytePtr("use `shared static ~this()` to declare a shared static destructor"));
                    }
                }
            }
            ASTBase.DtorDeclaration f = new ASTBase.DtorDeclaration(loc, Loc.initial.value, stc, Id.dtor.value);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                (a.get()).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas, a);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseStaticCtor(Ptr<PrefixAttributesASTBase> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, null) | stc;
            if ((stc & 536870912L) != 0)
            {
                this.error(loc, new BytePtr("use `shared static this()` to declare a shared static constructor"));
            }
            else if ((stc & 1L) != 0)
            {
                this.appendStorageClass(stc, 1L);
            }
            else {
                long modStc = stc & 2685403140L;
                if ((modStc) != 0)
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        ASTBase.stcToBuffer(ptr(buf), modStc);
                        this.error(loc, new BytePtr("static constructor cannot be `%s`"), buf.peekChars());
                    }
                    finally {
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.StaticCtorDeclaration f = new ASTBase.StaticCtorDeclaration(loc, Loc.initial.value, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  ASTBase.Dsymbol parseStaticDtor(Ptr<PrefixAttributesASTBase> pAttrs) {
            Ptr<DArray<ASTBase.Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            this.nextToken();
            this.check(TOK.this_);
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, ptr(udas)) | stc;
            if ((stc & 536870912L) != 0)
            {
                this.error(loc, new BytePtr("use `shared static ~this()` to declare a shared static destructor"));
            }
            else if ((stc & 1L) != 0)
            {
                this.appendStorageClass(stc, 1L);
            }
            else {
                long modStc = stc & 2685403140L;
                if ((modStc) != 0)
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        ASTBase.stcToBuffer(ptr(buf), modStc);
                        this.error(loc, new BytePtr("static destructor cannot be `%s`"), buf.peekChars());
                    }
                    finally {
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.StaticDtorDeclaration f = new ASTBase.StaticDtorDeclaration(loc, Loc.initial.value, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                (a.get()).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas, a);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseSharedStaticCtor(Ptr<PrefixAttributesASTBase> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, null) | stc;
            {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    this.appendStorageClass(stc, ss);
                }
                else {
                    long modStc = stc & 2685403140L;
                    if ((modStc) != 0)
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            ASTBase.stcToBuffer(ptr(buf), modStc);
                            this.error(loc, new BytePtr("shared static constructor cannot be `%s`"), buf.peekChars());
                        }
                        finally {
                        }
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.SharedStaticCtorDeclaration f = new ASTBase.SharedStaticCtorDeclaration(loc, Loc.initial.value, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  ASTBase.Dsymbol parseSharedStaticDtor(Ptr<PrefixAttributesASTBase> pAttrs) {
            Ptr<DArray<ASTBase.Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            this.nextToken();
            this.nextToken();
            this.check(TOK.this_);
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, ptr(udas)) | stc;
            {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    this.appendStorageClass(stc, ss);
                }
                else {
                    long modStc = stc & 2685403140L;
                    if ((modStc) != 0)
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            ASTBase.stcToBuffer(ptr(buf), modStc);
                            this.error(loc, new BytePtr("shared static destructor cannot be `%s`"), buf.peekChars());
                        }
                        finally {
                        }
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.SharedStaticDtorDeclaration f = new ASTBase.SharedStaticDtorDeclaration(loc, Loc.initial.value, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                (a.get()).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas, a);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseInvariant(Ptr<PrefixAttributesASTBase> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) != 2))
                {
                    ASTBase.Expression e = this.parseAssignExp();
                    ASTBase.Expression msg = null;
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 2))
                        {
                            msg = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 99))
                            {
                                this.nextToken();
                            }
                        }
                    }
                    this.check(TOK.rightParentheses);
                    this.check(TOK.semicolon);
                    e = new ASTBase.AssertExp(loc, e, msg);
                    ASTBase.ExpStatement fbody = new ASTBase.ExpStatement(loc, e);
                    ASTBase.InvariantDeclaration f = new ASTBase.InvariantDeclaration(loc, this.token.value.loc, stc, null, fbody);
                    return f;
                }
                this.nextToken();
            }
            ASTBase.Statement fbody = this.parseStatement(4, null, null);
            ASTBase.InvariantDeclaration f = new ASTBase.InvariantDeclaration(loc, this.token.value.loc, stc, null, fbody);
            return f;
        }

        public  ASTBase.Dsymbol parseUnitTest(Ptr<PrefixAttributesASTBase> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            BytePtr begPtr = pcopy(this.token.value.ptr.plus(1));
            BytePtr endPtr = null;
            ASTBase.Statement sbody = this.parseStatement(4, ptr(endPtr), null);
            BytePtr docline = null;
            if (global.params.doDocComments && (endPtr.greaterThan(begPtr)))
            {
                {
                    BytePtr p = pcopy(endPtr.minus(1));
                    for (; (begPtr.lessOrEqual(p)) && ((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 13) || ((p.get() & 0xFF) == 10) || ((p.get() & 0xFF) == 9);p.minusAssign(1)){
                        endPtr = pcopy(p);
                    }
                }
                int len = ((endPtr.minus(begPtr)));
                if ((len > 0))
                {
                    docline = pcopy((((BytePtr)Mem.xmalloc(len + 2))));
                    memcpy((BytePtr)(docline), (begPtr), len);
                    docline.set(len, (byte)10);
                    docline.set((len + 1), (byte)0);
                }
            }
            ASTBase.UnitTestDeclaration f = new ASTBase.UnitTestDeclaration(loc, this.token.value.loc, stc, docline);
            f.fbody = sbody;
            return f;
        }

        public  ASTBase.Dsymbol parseNew(Ptr<PrefixAttributesASTBase> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            int varargs = ASTBase.VarArg.none;
            Ptr<DArray<ASTBase.Parameter>> parameters = this.parseParameters(ptr(varargs), null);
            ASTBase.NewDeclaration f = new ASTBase.NewDeclaration(loc, Loc.initial.value, stc, parameters, varargs);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  ASTBase.Dsymbol parseDelete(Ptr<PrefixAttributesASTBase> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            int varargs = ASTBase.VarArg.none;
            Ptr<DArray<ASTBase.Parameter>> parameters = this.parseParameters(ptr(varargs), null);
            if ((varargs != ASTBase.VarArg.none))
            {
                this.error(new BytePtr("`...` not allowed in delete function parameter list"));
            }
            ASTBase.DeleteDeclaration f = new ASTBase.DeleteDeclaration(loc, Loc.initial.value, stc, parameters);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  Ptr<DArray<ASTBase.Parameter>> parseParameters(IntPtr pvarargs, Ptr<Ptr<DArray<ASTBase.TemplateParameter>>> tpl) {
            Ptr<DArray<ASTBase.Parameter>> parameters = refPtr(new DArray<ASTBase.Parameter>());
            int varargs = ASTBase.VarArg.none;
            int hasdefault = 0;
            this.check(TOK.leftParentheses);
        L_outer5:
            for (; 1 != 0;){
                Identifier ai = null;
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                ASTBase.Expression ae = null;
                Ptr<DArray<ASTBase.Expression>> udas = null;
                try {
                L_outer6:
                    for (; 1 != 0;this.nextToken()){
                    /*L3:*/
                        {
                            int __dispatch9 = 0;
                            dispatched_9:
                            do {
                                switch (__dispatch9 != 0 ? __dispatch9 : (this.token.value.value & 0xFF))
                                {
                                    case 2:
                                        if ((storageClass != 0L) || (udas != null))
                                        {
                                            this.error(new BytePtr("basic type expected, not `)`"));
                                        }
                                        break;
                                    case 10:
                                        varargs = ASTBase.VarArg.variadic;
                                        this.nextToken();
                                        break;
                                    case 171:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        }
                                        stc = 4L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 182:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        }
                                        stc = 1048576L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 224:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        }
                                        stc = 536870912L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 177:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        }
                                        stc = 2147483648L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 225:
                                        Ptr<DArray<ASTBase.Expression>> exps = null;
                                        long stc2 = this.parseAttribute(ptr(exps));
                                        if ((stc2 == 4294967296L) || (stc2 == 4398046511104L) || (stc2 == 137438953472L) || (stc2 == 8589934592L) || (stc2 == 17179869184L) || (stc2 == 34359738368L))
                                        {
                                            this.error(new BytePtr("`@%s` attribute for function parameter is not supported"), this.token.value.toChars());
                                        }
                                        else
                                        {
                                            udas = ASTBase.UserAttributeDeclaration.concat(udas, exps);
                                        }
                                        if (((this.token.value.value & 0xFF) == 10))
                                        {
                                            this.error(new BytePtr("variadic parameter cannot have user-defined attributes"));
                                        }
                                        if (stc2 != 0)
                                        {
                                            this.nextToken();
                                        }
                                        /*goto L3*/throw Dispatch0.INSTANCE;
                                    case 175:
                                        stc = 2048L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 176:
                                        stc = 4096L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 210:
                                        stc = 2097152L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 178:
                                        stc = 8192L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 203:
                                        stc = 524288L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 170:
                                        stc = 8L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 179:
                                        stc = 256L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 195:
                                        stc = 17592186044416L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    /*L2:*/
                                    case -1:
                                    __dispatch9 = 0;
                                        storageClass = this.appendStorageClass(storageClass, stc);
                                        continue L_outer6;
                                    default:
                                    __dispatch9 = 0;
                                    stc = storageClass & 2111488L;
                                    if (((stc & stc - 1L) != 0) && !(stc == 2099200L))
                                    {
                                        this.error(new BytePtr("incompatible parameter storage classes"));
                                    }
                                    if ((tpl != null) && ((this.token.value.value & 0xFF) == 120))
                                    {
                                        Ptr<Token> t = this.peek(ptr(this.token));
                                        if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 2) || (((t.get()).value & 0xFF) == 10))
                                        {
                                            Identifier id = Identifier.generateId(new BytePtr("__T"));
                                            Loc loc = this.token.value.loc.copy();
                                            at = new ASTBase.TypeIdentifier(loc, id);
                                            if (tpl.get() == null)
                                            {
                                                tpl.set(0, (refPtr(new DArray<ASTBase.TemplateParameter>())));
                                            }
                                            ASTBase.TemplateParameter tp = new ASTBase.TemplateTypeParameter(loc, id, null, null);
                                            (tpl.get().get()).push(tp);
                                            ai = this.token.value.ident;
                                            this.nextToken();
                                        }
                                        else
                                        {
                                            /*goto _else*/{ __dispatch9 = -2; continue dispatched_9; }
                                        }
                                    }
                                    else
                                    {
                                    /*_else:*/
                                    case -2:
                                    __dispatch9 = 0;
                                        at = this.parseType(ptr(ai), null);
                                    }
                                    ae = null;
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        ae = this.parseDefaultInitExp();
                                        hasdefault = 1;
                                    }
                                    else
                                    {
                                        if (hasdefault != 0)
                                        {
                                            this.error(new BytePtr("default argument expected for `%s`"), ai != null ? ai.toChars() : at.toChars());
                                        }
                                    }
                                    ASTBase.Parameter param = new ASTBase.Parameter(storageClass, at, ai, ae, null);
                                    if (udas != null)
                                    {
                                        Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                                        ASTBase.UserAttributeDeclaration udad = new ASTBase.UserAttributeDeclaration(udas, a);
                                        param.userAttribDecl = udad;
                                    }
                                    if (((this.token.value.value & 0xFF) == 225))
                                    {
                                        Ptr<DArray<ASTBase.Expression>> exps_1 = null;
                                        long stc2_1 = this.parseAttribute(ptr(exps_1));
                                        if ((stc2_1 == 4294967296L) || (stc2_1 == 4398046511104L) || (stc2_1 == 137438953472L) || (stc2_1 == 8589934592L) || (stc2_1 == 17179869184L) || (stc2_1 == 34359738368L))
                                        {
                                            this.error(new BytePtr("`@%s` attribute for function parameter is not supported"), this.token.value.toChars());
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("user-defined attributes cannot appear as postfixes"), this.token.value.toChars());
                                        }
                                        if (stc2_1 != 0)
                                        {
                                            this.nextToken();
                                        }
                                    }
                                    if (((this.token.value.value & 0xFF) == 10))
                                    {
                                        if ((storageClass & 2101248L) != 0)
                                        {
                                            this.error(new BytePtr("variadic argument cannot be `out` or `ref`"));
                                        }
                                        varargs = ASTBase.VarArg.typesafe;
                                        (parameters.get()).push(param);
                                        this.nextToken();
                                        break;
                                    }
                                    (parameters.get()).push(param);
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        this.nextToken();
                                        /*goto L1*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                }
                            } while(__dispatch9 != 0);
                        }
                        break;
                    }
                    break;
                }
                catch(Dispatch0 __d){}
            /*L1:*/
            }
            this.check(TOK.rightParentheses);
            pvarargs.set(0, varargs);
            return parameters;
        }

        // defaulted all parameters starting with #2
        public  Ptr<DArray<ASTBase.Parameter>> parseParameters(IntPtr pvarargs) {
            return parseParameters(pvarargs, null);
        }

        public  ASTBase.EnumDeclaration parseEnum() {
            ASTBase.EnumDeclaration e = null;
            Identifier id = null;
            ASTBase.Type memtype = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            id = null;
            if (((this.token.value.value & 0xFF) == 120))
            {
                id = this.token.value.ident;
                this.nextToken();
            }
            memtype = null;
            if (((this.token.value.value & 0xFF) == 7))
            {
                this.nextToken();
                int alt = 0;
                Loc typeLoc = this.token.value.loc.copy();
                memtype = this.parseBasicType(false);
                memtype = this.parseDeclarator(memtype, ptr(alt), null, null, 0L, null, null);
                this.checkCstyleTypeSyntax(typeLoc, memtype, alt, null);
            }
            e = new ASTBase.EnumDeclaration(loc, id, memtype);
            if (((this.token.value.value & 0xFF) == 9) && (id != null))
            {
                this.nextToken();
            }
            else if (((this.token.value.value & 0xFF) == 5))
            {
                boolean isAnonymousEnum = id == null;
                e.members = refPtr(new DArray<ASTBase.Dsymbol>());
                this.nextToken();
                BytePtr comment = pcopy(this.token.value.blockComment.value);
            L_outer7:
                for (; ((this.token.value.value & 0xFF) != 6);){
                    loc = this.token.value.loc.copy();
                    ASTBase.Type type = null;
                    Identifier ident = null;
                    Ptr<DArray<ASTBase.Expression>> udas = null;
                    long stc = 0L;
                    ASTBase.Expression deprecationMessage = null;
                    ByteSlice attributeErrorMessage = new ByteSlice("`%s` is not a valid attribute for enum members");
                L_outer8:
                    for (; ((this.token.value.value & 0xFF) != 6) && ((this.token.value.value & 0xFF) != 99) && ((this.token.value.value & 0xFF) != 90);){
                        {
                            int __dispatch10 = 0;
                            dispatched_10:
                            do {
                                switch (__dispatch10 != 0 ? __dispatch10 : (this.token.value.value & 0xFF))
                                {
                                    case 225:
                                        {
                                            long _stc = this.parseAttribute(ptr(udas));
                                            if ((_stc) != 0)
                                            {
                                                if ((_stc == 137438953472L))
                                                {
                                                    stc |= _stc;
                                                }
                                                else
                                                {
                                                    OutBuffer buf = new OutBuffer();
                                                    try {
                                                        ASTBase.stcToBuffer(ptr(buf), _stc);
                                                        this.error(new BytePtr("`%s` is not a valid attribute for enum members"), buf.peekChars());
                                                    }
                                                    finally {
                                                    }
                                                }
                                                this.nextToken();
                                            }
                                        }
                                        break;
                                    case 174:
                                        {
                                            long _stc = this.parseDeprecatedAttribute(deprecationMessage);
                                            if ((_stc) != 0)
                                            {
                                                stc |= _stc;
                                                this.nextToken();
                                            }
                                        }
                                        break;
                                    case 120:
                                        Ptr<Token> tp = this.peek(ptr(this.token));
                                        if ((((tp.get()).value & 0xFF) == 90) || (((tp.get()).value & 0xFF) == 99) || (((tp.get()).value & 0xFF) == 6))
                                        {
                                            ident = this.token.value.ident;
                                            type = null;
                                            this.nextToken();
                                        }
                                        else
                                        {
                                            /*goto default*/ { __dispatch10 = -1; continue dispatched_10; }
                                        }
                                        break;
                                    default:
                                    __dispatch10 = 0;
                                    if (isAnonymousEnum)
                                    {
                                        type = this.parseType(ptr(ident), null);
                                        if ((pequals(type, ASTBase.Type.terror)))
                                        {
                                            type = null;
                                            this.nextToken();
                                        }
                                    }
                                    else
                                    {
                                        this.error(new BytePtr("`%s` is not a valid attribute for enum members"), this.token.value.toChars());
                                        this.nextToken();
                                    }
                                    break;
                                }
                            } while(__dispatch10 != 0);
                        }
                    }
                    if ((type != null) && (!pequals(type, ASTBase.Type.terror)))
                    {
                        if (ident == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), type.toChars());
                        }
                        if (!isAnonymousEnum)
                        {
                            this.error(new BytePtr("type only allowed if anonymous enum and no enum type"));
                        }
                    }
                    ASTBase.Expression value = null;
                    if (((this.token.value.value & 0xFF) == 90))
                    {
                        this.nextToken();
                        value = this.parseAssignExp();
                    }
                    else
                    {
                        value = null;
                        if ((type != null) && (!pequals(type, ASTBase.Type.terror)) && isAnonymousEnum)
                        {
                            this.error(new BytePtr("if type, there must be an initializer"));
                        }
                    }
                    ASTBase.UserAttributeDeclaration uad = null;
                    if (udas != null)
                    {
                        uad = new ASTBase.UserAttributeDeclaration(udas, null);
                    }
                    ASTBase.DeprecatedDeclaration dd = null;
                    if (deprecationMessage != null)
                    {
                        dd = new ASTBase.DeprecatedDeclaration(deprecationMessage, null);
                        stc |= 1024L;
                    }
                    ASTBase.EnumMember em = new ASTBase.EnumMember(loc, ident, value, type, stc, uad, dd);
                    (e.members.get()).push(em);
                    if (((this.token.value.value & 0xFF) == 6))
                    {
                    }
                    else
                    {
                        this.addComment(em, comment);
                        comment = null;
                        this.check(TOK.comma);
                    }
                    this.addComment(em, comment);
                    comment = pcopy(this.token.value.blockComment.value);
                    if (((this.token.value.value & 0xFF) == 11))
                    {
                        this.error(new BytePtr("premature end of file"));
                        break;
                    }
                }
                this.nextToken();
            }
            else
            {
                this.error(new BytePtr("enum declaration is invalid"));
            }
            return e;
        }

        public  ASTBase.Dsymbol parseAggregate() {
            Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
            ASTBase.Expression constraint = null;
            Loc loc = this.token.value.loc.copy();
            byte tok = this.token.value.value;
            this.nextToken();
            Identifier id = null;
            if (((this.token.value.value & 0xFF) != 120))
            {
                id = null;
            }
            else
            {
                id = this.token.value.ident;
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 1))
                {
                    tpl = this.parseTemplateParameterList(0);
                    constraint = this.parseConstraint();
                }
            }
            Ptr<DArray<Ptr<ASTBase.BaseClass>>> baseclasses = null;
            if (((this.token.value.value & 0xFF) == 7))
            {
                if (((tok & 0xFF) != 154) && ((tok & 0xFF) != 153))
                {
                    this.error(new BytePtr("base classes are not allowed for `%s`, did you mean `;`?"), Token.toChars(tok));
                }
                this.nextToken();
                baseclasses = this.parseBaseClasses();
            }
            if (((this.token.value.value & 0xFF) == 183))
            {
                if (constraint != null)
                {
                    this.error(new BytePtr("template constraints appear both before and after BaseClassList, put them before"));
                }
                constraint = this.parseConstraint();
            }
            if (constraint != null)
            {
                if (id == null)
                {
                    this.error(new BytePtr("template constraints not allowed for anonymous `%s`"), Token.toChars(tok));
                }
                if (tpl == null)
                {
                    this.error(new BytePtr("template constraints only allowed for templates"));
                }
            }
            Ptr<DArray<ASTBase.Dsymbol>> members = null;
            if (((this.token.value.value & 0xFF) == 5))
            {
                Loc lookingForElseSave = this.lookingForElse.copy();
                this.lookingForElse = new Loc(null, 0, 0).copy();
                this.nextToken();
                members = this.parseDeclDefs(0, null, null);
                this.lookingForElse = lookingForElseSave.copy();
                if (((this.token.value.value & 0xFF) != 6))
                {
                    this.error(new BytePtr("`}` expected following members in `%s` declaration at %s"), Token.toChars(tok), loc.toChars(global.params.showColumns.value));
                }
                this.nextToken();
            }
            else if (((this.token.value.value & 0xFF) == 9) && (id != null))
            {
                if ((baseclasses != null) || (constraint != null))
                {
                    this.error(new BytePtr("members expected"));
                }
                this.nextToken();
            }
            else
            {
                this.error(new BytePtr("{ } expected following `%s` declaration"), Token.toChars(tok));
            }
            ASTBase.AggregateDeclaration a = null;
            switch ((tok & 0xFF))
            {
                case 154:
                    if (id == null)
                    {
                        this.error(loc, new BytePtr("anonymous interfaces not allowed"));
                    }
                    a = new ASTBase.InterfaceDeclaration(loc, id, baseclasses);
                    a.members = members;
                    break;
                case 153:
                    if (id == null)
                    {
                        this.error(loc, new BytePtr("anonymous classes not allowed"));
                    }
                    boolean inObject = (this.md != null) && ((this.md.get()).packages == null) && (pequals((this.md.get()).id, Id.object.value));
                    a = new ASTBase.ClassDeclaration(loc, id, baseclasses, members, inObject);
                    break;
                case 152:
                    if (id != null)
                    {
                        boolean inObject_1 = (this.md != null) && ((this.md.get()).packages == null) && (pequals((this.md.get()).id, Id.object.value));
                        a = new ASTBase.StructDeclaration(loc, id, inObject_1);
                        a.members = members;
                    }
                    else
                    {
                        assert(tpl == null);
                        return new ASTBase.AnonDeclaration(loc, false, members);
                    }
                    break;
                case 155:
                    if (id != null)
                    {
                        a = new ASTBase.UnionDeclaration(loc, id);
                        a.members = members;
                    }
                    else
                    {
                        assert(tpl == null);
                        return new ASTBase.AnonDeclaration(loc, true, members);
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            if (tpl != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> decldefs = refPtr(new DArray<ASTBase.Dsymbol>());
                (decldefs.get()).push(a);
                ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, id, tpl, constraint, decldefs, false, false);
                return tempdecl;
            }
            return a;
        }

        public  Ptr<DArray<Ptr<ASTBase.BaseClass>>> parseBaseClasses() {
            Ptr<DArray<Ptr<ASTBase.BaseClass>>> baseclasses = refPtr(new DArray<Ptr<ASTBase.BaseClass>>());
            for (; 1 != 0;this.nextToken()){
                Ptr<ASTBase.BaseClass> b = refPtr(new ASTBase.BaseClass(this.parseBasicType(false)));
                (baseclasses.get()).push(b);
                if (((this.token.value.value & 0xFF) != 99))
                {
                    break;
                }
            }
            return baseclasses;
        }

        public  Ptr<DArray<ASTBase.Dsymbol>> parseImport() {
            Ptr<DArray<ASTBase.Dsymbol>> decldefs = refPtr(new DArray<ASTBase.Dsymbol>());
            Identifier aliasid = null;
            int isstatic = (((this.token.value.value & 0xFF) == 169) ? 1 : 0);
            if (isstatic != 0)
            {
                this.nextToken();
            }
        L_outer9:
            do {
                {
                    while(true) try {
                    /*L1:*/
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 120))
                        {
                            this.error(new BytePtr("identifier expected following `import`"));
                            break;
                        }
                        Loc loc = this.token.value.loc.copy();
                        Identifier id = this.token.value.ident;
                        Ptr<DArray<Identifier>> a = null;
                        this.nextToken();
                        if ((aliasid == null) && ((this.token.value.value & 0xFF) == 90))
                        {
                            aliasid = id;
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        for (; ((this.token.value.value & 0xFF) == 97);){
                            if (a == null)
                            {
                                a = refPtr(new DArray<Identifier>());
                            }
                            (a.get()).push(id);
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("identifier expected following `package`"));
                                break;
                            }
                            id = this.token.value.ident;
                            this.nextToken();
                        }
                        ASTBase.Import s = new ASTBase.Import(loc, a, id, aliasid, isstatic);
                        (decldefs.get()).push(s);
                        if (((this.token.value.value & 0xFF) == 7))
                        {
                            do {
                                {
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) != 120))
                                    {
                                        this.error(new BytePtr("identifier expected following `:`"));
                                        break;
                                    }
                                    Identifier _alias = this.token.value.ident;
                                    Identifier name = null;
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 120))
                                        {
                                            this.error(new BytePtr("identifier expected following `%s=`"), _alias.toChars());
                                            break;
                                        }
                                        name = this.token.value.ident;
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        name = _alias;
                                        _alias = null;
                                    }
                                    s.addAlias(name, _alias);
                                }
                            } while (((this.token.value.value & 0xFF) == 99));
                            break;
                        }
                        aliasid = null;
                        break;
                    } catch(Dispatch0 __d){}
                }
            } while (((this.token.value.value & 0xFF) == 99));
            if (((this.token.value.value & 0xFF) == 9))
            {
                this.nextToken();
            }
            else
            {
                this.error(new BytePtr("`;` expected"));
                this.nextToken();
            }
            return decldefs;
        }

        public  ASTBase.Type parseType(Ptr<Identifier> pident, Ptr<Ptr<DArray<ASTBase.TemplateParameter>>> ptpl) {
            long stc = 0L;
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 171:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 4L;
                        this.nextToken();
                        continue;
                    case 182:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 1048576L;
                        this.nextToken();
                        continue;
                    case 224:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 536870912L;
                        this.nextToken();
                        continue;
                    case 177:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 2147483648L;
                        this.nextToken();
                        continue;
                    default:
                    break;
                }
                break;
            }
            Loc typeLoc = this.token.value.loc.copy();
            ASTBase.Type t = null;
            t = this.parseBasicType(false);
            int alt = 0;
            t = this.parseDeclarator(t, ptr(alt), pident, ptpl, 0L, null, null);
            this.checkCstyleTypeSyntax(typeLoc, t, alt, pident != null ? pident.get() : null);
            t = t.addSTC(stc);
            return t;
        }

        // defaulted all parameters starting with #2
        public  ASTBase.Type parseType(Ptr<Identifier> pident) {
            return parseType(pident, null);
        }

        // defaulted all parameters starting with #1
        public  ASTBase.Type parseType() {
            return parseType(null, null);
        }

        public  ASTBase.Type parseBasicType(boolean dontLookDotIdents) {
            ASTBase.Type t = null;
            Loc loc = new Loc();
            Identifier id = null;
            {
                int __dispatch13 = 0;
                dispatched_13:
                do {
                    switch (__dispatch13 != 0 ? __dispatch13 : (this.token.value.value & 0xFF))
                    {
                        case 128:
                            t = ASTBase.Type.tvoid;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 129:
                            t = ASTBase.Type.tint8;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 130:
                            t = ASTBase.Type.tuns8;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 131:
                            t = ASTBase.Type.tint16;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 132:
                            t = ASTBase.Type.tuns16;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 133:
                            t = ASTBase.Type.tint32;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 134:
                            t = ASTBase.Type.tuns32;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 135:
                            t = ASTBase.Type.tint64;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 135))
                            {
                                this.error(new BytePtr("use `long` for a 64 bit integer instead of `long long`"));
                                this.nextToken();
                            }
                            else if (((this.token.value.value & 0xFF) == 140))
                            {
                                this.error(new BytePtr("use `real` instead of `long double`"));
                                t = ASTBase.Type.tfloat80;
                                this.nextToken();
                            }
                            break;
                        case 136:
                            t = ASTBase.Type.tuns64;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 137:
                            t = ASTBase.Type.tint128;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 138:
                            t = ASTBase.Type.tuns128;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 139:
                            t = ASTBase.Type.tfloat32;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 140:
                            t = ASTBase.Type.tfloat64;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 141:
                            t = ASTBase.Type.tfloat80;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 142:
                            t = ASTBase.Type.timaginary32;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 143:
                            t = ASTBase.Type.timaginary64;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 144:
                            t = ASTBase.Type.timaginary80;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 145:
                            t = ASTBase.Type.tcomplex32;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 146:
                            t = ASTBase.Type.tcomplex64;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 147:
                            t = ASTBase.Type.tcomplex80;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 151:
                            t = ASTBase.Type.tbool;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 148:
                            t = ASTBase.Type.tchar;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 149:
                            t = ASTBase.Type.twchar;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        case 150:
                            t = ASTBase.Type.tdchar;
                            /*goto LabelX*/{ __dispatch13 = -1; continue dispatched_13; }
                        /*LabelX:*/
                        case -1:
                        __dispatch13 = 0;
                            this.nextToken();
                            break;
                        case 123:
                        case 124:
                        case 120:
                            loc = this.token.value.loc.copy();
                            id = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 91))
                            {
                                ASTBase.TemplateInstance tempinst = new ASTBase.TemplateInstance(loc, id, this.parseTemplateArguments());
                                t = this.parseBasicTypeStartingAt(new ASTBase.TypeInstance(loc, tempinst), dontLookDotIdents);
                            }
                            else
                            {
                                t = this.parseBasicTypeStartingAt(new ASTBase.TypeIdentifier(loc, id), dontLookDotIdents);
                            }
                            break;
                        case 97:
                            t = this.parseBasicTypeStartingAt(new ASTBase.TypeIdentifier(this.token.value.loc, Id.empty.value), dontLookDotIdents);
                            break;
                        case 39:
                            t = this.parseBasicTypeStartingAt(this.parseTypeof(), dontLookDotIdents);
                            break;
                        case 229:
                            t = this.parseVector();
                            break;
                        case 213:
                            {
                                ASTBase.TraitsExp te = (ASTBase.TraitsExp)this.parsePrimaryExp();
                                if ((te) != null)
                                {
                                    if ((te.ident != null) && (te.args != null))
                                    {
                                        t = new ASTBase.TypeTraits(this.token.value.loc, te);
                                        break;
                                    }
                                }
                            }
                            t = new ASTBase.TypeError();
                            break;
                        case 171:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(4L);
                            this.check(TOK.rightParentheses);
                            break;
                        case 182:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(1048576L);
                            this.check(TOK.rightParentheses);
                            break;
                        case 224:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(536870912L);
                            this.check(TOK.rightParentheses);
                            break;
                        case 177:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(2147483648L);
                            this.check(TOK.rightParentheses);
                            break;
                        default:
                        this.error(new BytePtr("basic type expected, not `%s`"), this.token.value.toChars());
                        if (((this.token.value.value & 0xFF) == 184))
                        {
                            this.errorSupplemental(this.token.value.loc, new BytePtr("There's no `static else`, use `else` instead."));
                        }
                        t = ASTBase.Type.terror;
                        break;
                    }
                } while(__dispatch13 != 0);
            }
            return t;
        }

        // defaulted all parameters starting with #1
        public  ASTBase.Type parseBasicType() {
            return parseBasicType(false);
        }

        public  ASTBase.Type parseBasicTypeStartingAt(ASTBase.TypeQualified tid, boolean dontLookDotIdents) {
            ASTBase.Type maybeArray = null;
            try {
            L_outer10:
                for (; 1 != 0;){
                    {
                        int __dispatch14 = 0;
                        dispatched_14:
                        do {
                            switch (__dispatch14 != 0 ? __dispatch14 : (this.token.value.value & 0xFF))
                            {
                                case 97:
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) != 120))
                                    {
                                        this.error(new BytePtr("identifier expected following `.` instead of `%s`"), this.token.value.toChars());
                                        break;
                                    }
                                    if (maybeArray != null)
                                    {
                                        DArray<RootObject> dimStack = new DArray<RootObject>();
                                        try {
                                            ASTBase.Type t = maybeArray;
                                            for (; true;){
                                                if (((t.ty & 0xFF) == ASTBase.ENUMTY.Tsarray))
                                                {
                                                    ASTBase.TypeSArray a = (ASTBase.TypeSArray)t;
                                                    dimStack.push(a.dim.syntaxCopy());
                                                    t = a.next.syntaxCopy();
                                                }
                                                else if (((t.ty & 0xFF) == ASTBase.ENUMTY.Taarray))
                                                {
                                                    ASTBase.TypeAArray a_1 = (ASTBase.TypeAArray)t;
                                                    dimStack.push(a_1.index.syntaxCopy());
                                                    t = a_1.next.syntaxCopy();
                                                }
                                                else
                                                {
                                                    break;
                                                }
                                            }
                                            assert((dimStack.length.value > 0));
                                            tid = (ASTBase.TypeQualified)t;
                                            for (; dimStack.length.value != 0;){
                                                tid.addIndex(dimStack.pop());
                                            }
                                            maybeArray = null;
                                        }
                                        finally {
                                        }
                                    }
                                    Loc loc = this.token.value.loc.copy();
                                    Identifier id = this.token.value.ident;
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 91))
                                    {
                                        ASTBase.TemplateInstance tempinst = new ASTBase.TemplateInstance(loc, id, this.parseTemplateArguments());
                                        tid.addInst(tempinst);
                                    }
                                    else
                                    {
                                        tid.addIdent(id);
                                    }
                                    continue L_outer10;
                                case 3:
                                    if (dontLookDotIdents)
                                    {
                                        /*goto Lend*/throw Dispatch0.INSTANCE;
                                    }
                                    this.nextToken();
                                    ASTBase.Type t_1 = maybeArray != null ? maybeArray : tid;
                                    if (((this.token.value.value & 0xFF) == 4))
                                    {
                                        t_1 = new ASTBase.TypeDArray(t_1);
                                        this.nextToken();
                                        return t_1;
                                    }
                                    else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.rightBracket, null))
                                    {
                                        ASTBase.Type index = this.parseType(null, null);
                                        maybeArray = new ASTBase.TypeAArray(t_1, index);
                                        this.check(TOK.rightBracket);
                                    }
                                    else
                                    {
                                        this.inBrackets++;
                                        ASTBase.Expression e = this.parseAssignExp();
                                        if (((this.token.value.value & 0xFF) == 31))
                                        {
                                            this.nextToken();
                                            ASTBase.Expression e2 = this.parseAssignExp();
                                            t_1 = new ASTBase.TypeSlice(t_1, e, e2);
                                            this.inBrackets--;
                                            this.check(TOK.rightBracket);
                                            return t_1;
                                        }
                                        else
                                        {
                                            maybeArray = new ASTBase.TypeSArray(t_1, e);
                                            this.inBrackets--;
                                            this.check(TOK.rightBracket);
                                            continue L_outer10;
                                        }
                                    }
                                    break;
                                default:
                                /*goto Lend*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch14 != 0);
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Lend:*/
            return maybeArray != null ? maybeArray : tid;
        }

        public  ASTBase.Type parseBasicType2(ASTBase.Type t) {
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 78:
                        t = new ASTBase.TypePointer(t);
                        this.nextToken();
                        continue;
                    case 3:
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 4))
                        {
                            t = new ASTBase.TypeDArray(t);
                            this.nextToken();
                        }
                        else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.rightBracket, null))
                        {
                            ASTBase.Type index = this.parseType(null, null);
                            t = new ASTBase.TypeAArray(t, index);
                            this.check(TOK.rightBracket);
                        }
                        else
                        {
                            this.inBrackets++;
                            ASTBase.Expression e = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 31))
                            {
                                this.nextToken();
                                ASTBase.Expression e2 = this.parseAssignExp();
                                t = new ASTBase.TypeSlice(t, e, e2);
                            }
                            else
                            {
                                t = new ASTBase.TypeSArray(t, e);
                            }
                            this.inBrackets--;
                            this.check(TOK.rightBracket);
                        }
                        continue;
                    case 160:
                    case 161:
                        byte save = this.token.value.value;
                        this.nextToken();
                        int varargs = ASTBase.VarArg.none;
                        Ptr<DArray<ASTBase.Parameter>> parameters = this.parseParameters(ptr(varargs), null);
                        long stc = this.parsePostfix(0L, null);
                        ASTBase.TypeFunction tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs), t, this.linkage, stc);
                        if ((stc & 17594871447556L) != 0)
                        {
                            if (((save & 0xFF) == 161))
                            {
                                this.error(new BytePtr("`const`/`immutable`/`shared`/`inout`/`return` attributes are only valid for non-static member functions"));
                            }
                            else
                            {
                                tf = (ASTBase.TypeFunction)tf.addSTC(stc);
                            }
                        }
                        t = ((save & 0xFF) == 160) ? new ASTBase.TypeDelegate(tf) : new ASTBase.TypePointer(tf);
                        continue;
                    default:
                    return t;
                }
                //throw new AssertionError("Unreachable code!");
            }
            //throw new AssertionError("Unreachable code!");
        }

        public  ASTBase.Type parseDeclarator(ASTBase.Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<ASTBase.TemplateParameter>>> tpl, long storageClass, IntPtr pdisable, Ptr<Ptr<DArray<ASTBase.Expression>>> pudas) {
            t = this.parseBasicType2(t);
            ASTBase.Type ts = null;
            switch ((this.token.value.value & 0xFF))
            {
                case 120:
                    if (pident != null)
                    {
                        pident.set(0, this.token.value.ident);
                    }
                    else
                    {
                        this.error(new BytePtr("unexpected identifier `%s` in declarator"), this.token.value.ident.toChars());
                    }
                    ts = t;
                    this.nextToken();
                    break;
                case 1:
                    if (((this.peekNext() & 0xFF) == 78) || ((this.peekNext() & 0xFF) == 1))
                    {
                        palt.set(0, palt.get() | 1);
                        this.nextToken();
                        ts = this.parseDeclarator(t, palt, pident, null, 0L, null, null);
                        this.check(TOK.rightParentheses);
                        break;
                    }
                    ts = t;
                    Ptr<Token> peekt = ptr(this.token);
                    if (this.isParameters(ptr(peekt)))
                    {
                        this.error(new BytePtr("function declaration without return type. (Note that constructors are always named `this`)"));
                    }
                    else
                    {
                        this.error(new BytePtr("unexpected `(` in declarator"));
                    }
                    break;
                default:
                ts = t;
                break;
            }
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 3:
                        ASTBase.TypeNext ta = null;
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 4))
                        {
                            ta = new ASTBase.TypeDArray(t);
                            this.nextToken();
                            palt.set(0, palt.get() | 2);
                        }
                        else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.rightBracket, null))
                        {
                            ASTBase.Type index = this.parseType(null, null);
                            this.check(TOK.rightBracket);
                            ta = new ASTBase.TypeAArray(t, index);
                            palt.set(0, palt.get() | 2);
                        }
                        else
                        {
                            ASTBase.Expression e = this.parseAssignExp();
                            ta = new ASTBase.TypeSArray(t, e);
                            this.check(TOK.rightBracket);
                            palt.set(0, palt.get() | 2);
                        }
                        Ptr<ASTBase.Type> pt = null;
                        {
                            pt = pcopy(ptr(ts));
                            for (; (!pequals(pt.get(), t));pt = pcopy((ptr((ASTBase.TypeNext)pt.get().next)))){
                            }
                        }
                        pt.set(0, ta);
                        continue;
                    case 1:
                        if (tpl != null)
                        {
                            Ptr<Token> tk = this.peekPastParen(ptr(this.token));
                            if ((((tk.get()).value & 0xFF) == 1))
                            {
                                tpl.set(0, this.parseTemplateParameterList(0));
                            }
                            else if ((((tk.get()).value & 0xFF) == 90))
                            {
                                tpl.set(0, this.parseTemplateParameterList(0));
                                break;
                            }
                        }
                        int varargs = ASTBase.VarArg.none;
                        Ptr<DArray<ASTBase.Parameter>> parameters = this.parseParameters(ptr(varargs), null);
                        long stc = this.parsePostfix(storageClass, pudas);
                        ASTBase.Type tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs), t, this.linkage, stc);
                        tf = tf.addSTC(stc);
                        if (pdisable != null)
                        {
                            pdisable.set(0, ((stc & 137438953472L) != 0 ? 1 : 0));
                        }
                        Ptr<ASTBase.Type> pt_1 = null;
                        {
                            pt_1 = pcopy(ptr(ts));
                            for (; (!pequals(pt_1.get(), t));pt_1 = pcopy((ptr((ASTBase.TypeNext)pt_1.get().next)))){
                            }
                        }
                        pt_1.set(0, tf);
                        break;
                    default:
                    break;
                }
                break;
            }
            return ts;
        }

        // defaulted all parameters starting with #7
        public  ASTBase.Type parseDeclarator(ASTBase.Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<ASTBase.TemplateParameter>>> tpl, long storageClass, IntPtr pdisable) {
            return parseDeclarator(t, palt, pident, tpl, storageClass, pdisable, null);
        }

        // defaulted all parameters starting with #6
        public  ASTBase.Type parseDeclarator(ASTBase.Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<ASTBase.TemplateParameter>>> tpl, long storageClass) {
            return parseDeclarator(t, palt, pident, tpl, storageClass, null, null);
        }

        // defaulted all parameters starting with #5
        public  ASTBase.Type parseDeclarator(ASTBase.Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<ASTBase.TemplateParameter>>> tpl) {
            return parseDeclarator(t, palt, pident, tpl, 0L, null, null);
        }

        // defaulted all parameters starting with #4
        public  ASTBase.Type parseDeclarator(ASTBase.Type t, IntPtr palt, Ptr<Identifier> pident) {
            return parseDeclarator(t, palt, pident, null, 0L, null, null);
        }

        public  void parseStorageClasses(Ref<Long> storage_class, IntRef link, Ref<Boolean> setAlignment, Ref<ASTBase.Expression> ealign, Ref<Ptr<DArray<ASTBase.Expression>>> udas) {
            long stc = 0L;
            boolean sawLinkage = false;
        L_outer11:
            for (; 1 != 0;){
                {
                    int __dispatch18 = 0;
                    dispatched_18:
                    do {
                        switch (__dispatch18 != 0 ? __dispatch18 : (this.token.value.value & 0xFF))
                        {
                            case 171:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 4L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 182:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 1048576L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 224:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 536870912L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 177:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 2147483648L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 169:
                                stc = 1L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 170:
                                stc = 8L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 179:
                                stc = 256L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 203:
                                stc = 524288L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 159:
                                stc = 128L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 172:
                                stc = 16L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 194:
                                stc = 512L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 174:
                                stc = 1024L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 216:
                                stc = 33554432L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 215:
                                stc = 67108864L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 210:
                                stc = 2097152L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 217:
                                stc = 1073741824L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 156:
                                Ptr<Token> t = this.peek(ptr(this.token));
                                if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7))
                                {
                                    break;
                                }
                                if ((((t.get()).value & 0xFF) == 120))
                                {
                                    t = this.peek(t);
                                    if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7) || (((t.get()).value & 0xFF) == 9))
                                    {
                                        break;
                                    }
                                }
                                stc = 8388608L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 225:
                                {
                                    stc = this.parseAttribute(ptr(udas));
                                    if (stc != 0)
                                    {
                                        /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                                    }
                                    continue L_outer11;
                                }
                            /*L1:*/
                            case -1:
                            __dispatch18 = 0;
                                storage_class.value = this.appendStorageClass(storage_class.value, stc);
                                this.nextToken();
                                continue L_outer11;
                            case 164:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
                                {
                                    stc = 2L;
                                    /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                                }
                                if (sawLinkage)
                                {
                                    this.error(new BytePtr("redundant linkage declaration"));
                                }
                                sawLinkage = true;
                                Ptr<DArray<Identifier>> idents = null;
                                Ptr<DArray<ASTBase.Expression>> identExps = null;
                                int cppmangle = CPPMANGLE.def;
                                boolean cppMangleOnly = false;
                                link.value = this.parseLinkage(ptr(idents), ptr(identExps), cppmangle, cppMangleOnly);
                                if ((idents != null) || (identExps != null))
                                {
                                    this.error(new BytePtr("C++ name spaces not allowed here"));
                                }
                                if ((cppmangle != CPPMANGLE.def))
                                {
                                    this.error(new BytePtr("C++ mangle declaration not allowed here"));
                                }
                                continue L_outer11;
                            case 163:
                                this.nextToken();
                                setAlignment.value = true;
                                if (((this.token.value.value & 0xFF) == 1))
                                {
                                    this.nextToken();
                                    ealign.value = this.parseExpression();
                                    this.check(TOK.rightParentheses);
                                }
                                continue L_outer11;
                            default:
                            break;
                        }
                    } while(__dispatch18 != 0);
                }
                break;
            }
        }

        public  Ptr<DArray<ASTBase.Dsymbol>> parseDeclarations(boolean autodecl, Ptr<PrefixAttributesASTBase> pAttrs, BytePtr comment) {
            long storage_class = 0L;
            byte tok = TOK.reserved;
            int link = this.linkage;
            boolean setAlignment = false;
            ASTBase.Expression ealign = null;
            Ptr<DArray<ASTBase.Expression>> udas = null;
            if (comment == null)
            {
                comment = pcopy(this.token.value.blockComment.value);
            }
            if (((this.token.value.value & 0xFF) == 158))
            {
                Loc loc = this.token.value.loc.copy();
                tok = this.token.value.value;
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 120) && ((this.peekNext() & 0xFF) == 123))
                {
                    ASTBase.AliasThis s = new ASTBase.AliasThis(loc, this.token.value.ident);
                    this.nextToken();
                    this.check(TOK.this_);
                    this.check(TOK.semicolon);
                    Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                    (a.get()).push(s);
                    this.addComment(s, comment);
                    return a;
                }
                if (((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign))
                {
                    Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                    for (; 1 != 0;){
                        Identifier ident = this.token.value.ident;
                        this.nextToken();
                        Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
                        if (((this.token.value.value & 0xFF) == 1))
                        {
                            tpl = this.parseTemplateParameterList(0);
                        }
                        this.check(TOK.assign);
                        boolean hasParsedAttributes = false;
                        Function0<Void> parseAttributes = new Function0<Void>(){
                            public Void invoke() {
                                if (hasParsedAttributes)
                                {
                                    return null;
                                }
                                hasParsedAttributes = true;
                                udas = null;
                                storage_class = 0L;
                                link = linkage;
                                setAlignment = false;
                                ealign = null;
                                parseStorageClasses(storage_class, link, setAlignment, ealign, udas);
                                return null;
                            }
                        };
                        if (((this.token.value.value & 0xFF) == 225))
                        {
                            parseAttributes.invoke();
                        }
                        ASTBase.Declaration v = null;
                        ASTBase.Dsymbol s = null;
                        boolean attributesAppended = false;
                        long funcStc = this.parseTypeCtor();
                        Ptr<Token> tlu = ptr(this.token);
                        Ptr<Token> tk = null;
                        if (((this.token.value.value & 0xFF) != 161) && ((this.token.value.value & 0xFF) != 160) && this.isBasicType(ptr(tlu)) && (tlu != null) && (((tlu.get()).value & 0xFF) == 1))
                        {
                            int vargs = ASTBase.VarArg.none;
                            ASTBase.Type tret = this.parseBasicType(false);
                            Ptr<DArray<ASTBase.Parameter>> prms = this.parseParameters(ptr(vargs), null);
                            ASTBase.ParameterList pl = new ASTBase.ParameterList(prms, vargs).copy();
                            parseAttributes.invoke();
                            if (udas != null)
                            {
                                this.error(new BytePtr("user-defined attributes not allowed for `alias` declarations"));
                            }
                            attributesAppended = true;
                            storage_class = this.appendStorageClass(storage_class, funcStc);
                            ASTBase.Type tf = new ASTBase.TypeFunction(pl, tret, link, storage_class);
                            v = new ASTBase.AliasDeclaration(loc, ident, tf);
                        }
                        else if (((this.token.value.value & 0xFF) == 161) || ((this.token.value.value & 0xFF) == 160) || ((this.token.value.value & 0xFF) == 1) && this.skipAttributes(this.peekPastParen(ptr(this.token)), ptr(tk)) && (((tk.get()).value & 0xFF) == 228) || (((tk.get()).value & 0xFF) == 5) || ((this.token.value.value & 0xFF) == 5) || ((this.token.value.value & 0xFF) == 120) && ((this.peekNext() & 0xFF) == 228) || ((this.token.value.value & 0xFF) == 210) && ((this.peekNext() & 0xFF) == 1) && this.skipAttributes(this.peekPastParen(this.peek(ptr(this.token))), ptr(tk)) && (((tk.get()).value & 0xFF) == 228) || (((tk.get()).value & 0xFF) == 5))
                        {
                            s = this.parseFunctionLiteral();
                            if ((udas != null))
                            {
                                if ((storage_class != 0L))
                                {
                                    this.error(new BytePtr("Cannot put a storage-class in an alias declaration."));
                                }
                                assert((link == this.linkage) && !setAlignment && (ealign == null));
                                ASTBase.TemplateDeclaration tpl_ = (ASTBase.TemplateDeclaration)s;
                                assert((tpl_ != null) && ((tpl_.members.get()).length == 1));
                                ASTBase.FuncLiteralDeclaration fd = (ASTBase.FuncLiteralDeclaration)(tpl_.members.get()).get(0);
                                ASTBase.TypeFunction tf = (ASTBase.TypeFunction)fd.type;
                                assert(((tf.parameterList.parameters.get()).length > 0));
                                Ptr<DArray<ASTBase.Dsymbol>> as = refPtr(new DArray<ASTBase.Dsymbol>());
                                (tf.parameterList.parameters.get()).get(0).userAttribDecl = new ASTBase.UserAttributeDeclaration(udas, as);
                            }
                            v = new ASTBase.AliasDeclaration(loc, ident, s);
                        }
                        else
                        {
                            parseAttributes.invoke();
                            if (udas != null)
                            {
                                this.error(new BytePtr("user-defined attributes not allowed for `%s` declarations"), Token.toChars(tok));
                            }
                            ASTBase.Type t = this.parseType(null, null);
                            v = new ASTBase.AliasDeclaration(loc, ident, t);
                        }
                        if (!attributesAppended)
                        {
                            storage_class = this.appendStorageClass(storage_class, funcStc);
                        }
                        v.storage_class = storage_class;
                        s = v;
                        if (tpl != null)
                        {
                            Ptr<DArray<ASTBase.Dsymbol>> a2 = refPtr(new DArray<ASTBase.Dsymbol>());
                            (a2.get()).push(s);
                            ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                            s = tempdecl;
                        }
                        if ((link != this.linkage))
                        {
                            Ptr<DArray<ASTBase.Dsymbol>> a2 = refPtr(new DArray<ASTBase.Dsymbol>());
                            (a2.get()).push(s);
                            s = new ASTBase.LinkDeclaration(link, a2);
                        }
                        (a.get()).push(s);
                        switch ((this.token.value.value & 0xFF))
                        {
                            case 9:
                                this.nextToken();
                                this.addComment(s, comment);
                                break;
                            case 99:
                                this.nextToken();
                                this.addComment(s, comment);
                                if (((this.token.value.value & 0xFF) != 120))
                                {
                                    this.error(new BytePtr("identifier expected following comma, not `%s`"), this.token.value.toChars());
                                    break;
                                }
                                if (((this.peekNext() & 0xFF) != 90) && ((this.peekNext() & 0xFF) != 1))
                                {
                                    this.error(new BytePtr("`=` expected following identifier"));
                                    this.nextToken();
                                    break;
                                }
                                continue;
                            default:
                            this.error(new BytePtr("semicolon expected to close `%s` declaration"), Token.toChars(tok));
                            break;
                        }
                        break;
                    }
                    return a;
                }
            }
            ASTBase.Type ts = null;
            if (!autodecl)
            {
                this.parseStorageClasses(storage_class, link, setAlignment, ealign, udas);
                if (((this.token.value.value & 0xFF) == 156))
                {
                    ASTBase.Dsymbol d = this.parseEnum();
                    Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                    (a.get()).push(d);
                    if (udas != null)
                    {
                        d = new ASTBase.UserAttributeDeclaration(udas, a);
                        a = refPtr(new DArray<ASTBase.Dsymbol>());
                        (a.get()).push(d);
                    }
                    this.addComment(d, comment);
                    return a;
                }
                if (((this.token.value.value & 0xFF) == 152) || ((this.token.value.value & 0xFF) == 155) || ((this.token.value.value & 0xFF) == 153) || ((this.token.value.value & 0xFF) == 154))
                {
                    ASTBase.Dsymbol s = this.parseAggregate();
                    Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
                    (a.get()).push(s);
                    if (storage_class != 0)
                    {
                        s = new ASTBase.StorageClassDeclaration(storage_class, a);
                        a = refPtr(new DArray<ASTBase.Dsymbol>());
                        (a.get()).push(s);
                    }
                    if (setAlignment)
                    {
                        s = new ASTBase.AlignDeclaration(s.loc, ealign, a);
                        a = refPtr(new DArray<ASTBase.Dsymbol>());
                        (a.get()).push(s);
                    }
                    if ((link != this.linkage))
                    {
                        s = new ASTBase.LinkDeclaration(link, a);
                        a = refPtr(new DArray<ASTBase.Dsymbol>());
                        (a.get()).push(s);
                    }
                    if (udas != null)
                    {
                        s = new ASTBase.UserAttributeDeclaration(udas, a);
                        a = refPtr(new DArray<ASTBase.Dsymbol>());
                        (a.get()).push(s);
                    }
                    this.addComment(s, comment);
                    return a;
                }
                if ((storage_class != 0) || (udas != null) && ((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign))
                {
                    Ptr<DArray<ASTBase.Dsymbol>> a = this.parseAutoDeclarations(storage_class, comment);
                    if (udas != null)
                    {
                        ASTBase.Dsymbol s = new ASTBase.UserAttributeDeclaration(udas, a);
                        a = refPtr(new DArray<ASTBase.Dsymbol>());
                        (a.get()).push(s);
                    }
                    return a;
                }
                {
                    Ptr<Token> tk = null;
                    if ((storage_class != 0) || (udas != null) && ((this.token.value.value & 0xFF) == 120) && this.skipParens(this.peek(ptr(this.token)), ptr(tk)) && this.skipAttributes(tk, ptr(tk)) && (((tk.get()).value & 0xFF) == 1) || (((tk.get()).value & 0xFF) == 5) || (((tk.get()).value & 0xFF) == 175) || (((tk.get()).value & 0xFF) == 176) || (((tk.get()).value & 0xFF) == 187) || (((tk.get()).value & 0xFF) == 120) && (pequals((tk.get()).ident, Id._body)))
                    {
                        ts = null;
                    }
                    else
                    {
                        ts = this.parseBasicType(false);
                        ts = this.parseBasicType2(ts);
                    }
                }
            }
            if (pAttrs != null)
            {
                storage_class |= (pAttrs.get()).storageClass;
            }
            ASTBase.Type tfirst = null;
            Ptr<DArray<ASTBase.Dsymbol>> a = refPtr(new DArray<ASTBase.Dsymbol>());
            for (; 1 != 0;){
                Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
                int disable = 0;
                int alt = 0;
                Loc loc = this.token.value.loc.copy();
                Identifier ident = null;
                ASTBase.Type t = this.parseDeclarator(ts, ptr(alt), ptr(ident), ptr(tpl), storage_class, ptr(disable), ptr(udas));
                assert(t != null);
                if (tfirst == null)
                {
                    tfirst = t;
                }
                else if ((!pequals(t, tfirst)))
                {
                    this.error(new BytePtr("multiple declarations must have the same type, not `%s` and `%s`"), tfirst.toChars(), t.toChars());
                }
                boolean isThis = ((t.ty & 0xFF) == ASTBase.ENUMTY.Tident) && (pequals(((ASTBase.TypeIdentifier)t).ident, Id.This.value)) && ((this.token.value.value & 0xFF) == 90);
                if (ident != null)
                {
                    this.checkCstyleTypeSyntax(loc, t, alt, ident);
                }
                else if (!isThis && (!pequals(t, ASTBase.Type.terror)))
                {
                    this.error(new BytePtr("no identifier for declarator `%s`"), t.toChars());
                }
                if (((tok & 0xFF) == 158))
                {
                    ASTBase.Declaration v = null;
                    ASTBase.Initializer _init = null;
                    if (udas != null)
                    {
                        this.error(new BytePtr("user-defined attributes not allowed for `%s` declarations"), Token.toChars(tok));
                    }
                    if (((this.token.value.value & 0xFF) == 90))
                    {
                        this.nextToken();
                        _init = this.parseInitializer();
                    }
                    if (_init != null)
                    {
                        if (isThis)
                        {
                            this.error(new BytePtr("cannot use syntax `alias this = %s`, use `alias %s this` instead"), _init.toChars(), _init.toChars());
                        }
                        else
                        {
                            this.error(new BytePtr("alias cannot have initializer"));
                        }
                    }
                    v = new ASTBase.AliasDeclaration(loc, ident, t);
                    v.storage_class = storage_class;
                    if (pAttrs != null)
                    {
                        (pAttrs.get()).storageClass &= 60129542144L;
                    }
                    ASTBase.Dsymbol s = v;
                    if ((link != this.linkage))
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> ax = refPtr(new DArray<ASTBase.Dsymbol>());
                        (ax.get()).push(v);
                        s = new ASTBase.LinkDeclaration(link, ax);
                    }
                    (a.get()).push(s);
                    switch ((this.token.value.value & 0xFF))
                    {
                        case 9:
                            this.nextToken();
                            this.addComment(s, comment);
                            break;
                        case 99:
                            this.nextToken();
                            this.addComment(s, comment);
                            continue;
                        default:
                        this.error(new BytePtr("semicolon expected to close `%s` declaration"), Token.toChars(tok));
                        break;
                    }
                }
                else if (((t.ty & 0xFF) == ASTBase.ENUMTY.Tfunction))
                {
                    ASTBase.Expression constraint = null;
                    ASTBase.FuncDeclaration f = new ASTBase.FuncDeclaration(loc, Loc.initial.value, ident, storage_class | (disable != 0 ? 137438953472L : 0L), t);
                    if (pAttrs != null)
                    {
                        (pAttrs.get()).storageClass = 0L;
                    }
                    if (tpl != null)
                    {
                        constraint = this.parseConstraint();
                    }
                    ASTBase.Dsymbol s = this.parseContracts(f);
                    Identifier tplIdent = s.ident;
                    if ((link != this.linkage))
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> ax = refPtr(new DArray<ASTBase.Dsymbol>());
                        (ax.get()).push(s);
                        s = new ASTBase.LinkDeclaration(link, ax);
                    }
                    if (udas != null)
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> ax = refPtr(new DArray<ASTBase.Dsymbol>());
                        (ax.get()).push(s);
                        s = new ASTBase.UserAttributeDeclaration(udas, ax);
                    }
                    if (tpl != null)
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> decldefs = refPtr(new DArray<ASTBase.Dsymbol>());
                        (decldefs.get()).push(s);
                        ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, tplIdent, tpl, constraint, decldefs, false, false);
                        s = tempdecl;
                        if ((storage_class & 1L) != 0)
                        {
                            assert((f.storage_class & 1L) != 0);
                            f.storage_class &= -2L;
                            Ptr<DArray<ASTBase.Dsymbol>> ax = refPtr(new DArray<ASTBase.Dsymbol>());
                            (ax.get()).push(s);
                            s = new ASTBase.StorageClassDeclaration(1L, ax);
                        }
                    }
                    (a.get()).push(s);
                    this.addComment(s, comment);
                }
                else if (ident != null)
                {
                    ASTBase.Initializer _init = null;
                    if (((this.token.value.value & 0xFF) == 90))
                    {
                        this.nextToken();
                        _init = this.parseInitializer();
                    }
                    ASTBase.VarDeclaration v = new ASTBase.VarDeclaration(loc, t, ident, _init, 0L);
                    v.storage_class = storage_class;
                    if (pAttrs != null)
                    {
                        (pAttrs.get()).storageClass = 0L;
                    }
                    ASTBase.Dsymbol s = v;
                    if ((tpl != null) && (_init != null))
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> a2 = refPtr(new DArray<ASTBase.Dsymbol>());
                        (a2.get()).push(s);
                        ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                        s = tempdecl;
                    }
                    if (setAlignment)
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> ax = refPtr(new DArray<ASTBase.Dsymbol>());
                        (ax.get()).push(s);
                        s = new ASTBase.AlignDeclaration(v.loc, ealign, ax);
                    }
                    if ((link != this.linkage))
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> ax = refPtr(new DArray<ASTBase.Dsymbol>());
                        (ax.get()).push(s);
                        s = new ASTBase.LinkDeclaration(link, ax);
                    }
                    if (udas != null)
                    {
                        Ptr<DArray<ASTBase.Dsymbol>> ax = refPtr(new DArray<ASTBase.Dsymbol>());
                        (ax.get()).push(s);
                        s = new ASTBase.UserAttributeDeclaration(udas, ax);
                    }
                    (a.get()).push(s);
                    switch ((this.token.value.value & 0xFF))
                    {
                        case 9:
                            this.nextToken();
                            this.addComment(s, comment);
                            break;
                        case 99:
                            this.nextToken();
                            this.addComment(s, comment);
                            continue;
                        default:
                        this.error(new BytePtr("semicolon expected, not `%s`"), this.token.value.toChars());
                        break;
                    }
                }
                break;
            }
            return a;
        }

        public  ASTBase.Dsymbol parseFunctionLiteral() {
            Loc loc = this.token.value.loc.copy();
            Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
            Ptr<DArray<ASTBase.Parameter>> parameters = null;
            int varargs = ASTBase.VarArg.none;
            ASTBase.Type tret = null;
            long stc = 0L;
            byte save = TOK.reserved;
            {
                int __dispatch22 = 0;
                dispatched_22:
                do {
                    switch (__dispatch22 != 0 ? __dispatch22 : (this.token.value.value & 0xFF))
                    {
                        case 161:
                        case 160:
                            save = this.token.value.value;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 210))
                            {
                                stc = 2097152L;
                                this.nextToken();
                            }
                            if (((this.token.value.value & 0xFF) != 1) && ((this.token.value.value & 0xFF) != 5))
                            {
                                tret = this.parseBasicType(false);
                                tret = this.parseBasicType2(tret);
                            }
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                            }
                            else
                            {
                                break;
                            }
                            /*goto case*/{ __dispatch22 = 1; continue dispatched_22; }
                        case 210:
                            stc = 2097152L;
                            this.nextToken();
                            /*goto case*/{ __dispatch22 = 1; continue dispatched_22; }
                        case 1:
                            __dispatch22 = 0;
                            parameters = this.parseParameters(ptr(varargs), ptr(tpl));
                            stc = this.parsePostfix(stc, null);
                            {
                                long modStc = stc & 2685403140L;
                                if ((modStc) != 0)
                                {
                                    if (((save & 0xFF) == 161))
                                    {
                                        OutBuffer buf = new OutBuffer();
                                        try {
                                            ASTBase.stcToBuffer(ptr(buf), modStc);
                                            this.error(new BytePtr("function literal cannot be `%s`"), buf.peekChars());
                                        }
                                        finally {
                                        }
                                    }
                                    else
                                    {
                                        save = TOK.delegate_;
                                    }
                                }
                            }
                            break;
                        case 5:
                            break;
                        case 120:
                            parameters = refPtr(new DArray<ASTBase.Parameter>());
                            Identifier id = Identifier.generateId(new BytePtr("__T"));
                            ASTBase.Type t = new ASTBase.TypeIdentifier(loc, id);
                            (parameters.get()).push(new ASTBase.Parameter(0L, t, this.token.value.ident, null, null));
                            tpl = refPtr(new DArray<ASTBase.TemplateParameter>());
                            ASTBase.TemplateParameter tp = new ASTBase.TemplateTypeParameter(loc, id, null, null);
                            (tpl.get()).push(tp);
                            this.nextToken();
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                } while(__dispatch22 != 0);
            }
            ASTBase.TypeFunction tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs), tret, this.linkage, stc);
            tf = (ASTBase.TypeFunction)tf.addSTC(stc);
            ASTBase.FuncLiteralDeclaration fd = new ASTBase.FuncLiteralDeclaration(loc, Loc.initial.value, tf, save, null, null);
            if (((this.token.value.value & 0xFF) == 228))
            {
                this.check(TOK.goesTo);
                Loc returnloc = this.token.value.loc.copy();
                ASTBase.Expression ae = this.parseAssignExp();
                fd.fbody = new ASTBase.ReturnStatement(returnloc, ae);
                fd.endloc = this.token.value.loc.copy();
            }
            else
            {
                this.parseContracts(fd);
            }
            if (tpl != null)
            {
                Ptr<DArray<ASTBase.Dsymbol>> decldefs = refPtr(new DArray<ASTBase.Dsymbol>());
                (decldefs.get()).push(fd);
                return new ASTBase.TemplateDeclaration(fd.loc, fd.ident, tpl, null, decldefs, false, true);
            }
            return fd;
        }

        public  ASTBase.FuncDeclaration parseContracts(ASTBase.FuncDeclaration f) {
            int linksave = this.linkage;
            boolean literal = f.isFuncLiteralDeclaration() != null;
            this.linkage = LINK.d;
            boolean requireDo = false;
        L1:
            while(true)
            {
                int __dispatch23 = 0;
                dispatched_23:
                do {
                    switch (__dispatch23 != 0 ? __dispatch23 : (this.token.value.value & 0xFF))
                    {
                        case 5:
                            if (requireDo)
                            {
                                this.error(new BytePtr("missing `do { ... }` after `in` or `out`"));
                            }
                            f.fbody = this.parseStatement(1, null, null);
                            f.endloc = this.endloc.copy();
                            break;
                        case 120:
                            if ((pequals(this.token.value.ident, Id._body)))
                            {
                                /*goto case*/{ __dispatch23 = 187; continue dispatched_23; }
                            }
                            /*goto default*/ { __dispatch23 = -2; continue dispatched_23; }
                        case 187:
                            __dispatch23 = 0;
                            this.nextToken();
                            f.fbody = this.parseStatement(4, null, null);
                            f.endloc = this.endloc.copy();
                            break;
                        case 175:
                            Loc loc = this.token.value.loc.copy();
                            this.nextToken();
                            if (f.frequires == null)
                            {
                                f.frequires = refPtr(new DArray<ASTBase.Statement>());
                            }
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.nextToken();
                                ASTBase.Expression e = this.parseAssignExp();
                                ASTBase.Expression msg = null;
                                if (((this.token.value.value & 0xFF) == 99))
                                {
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) != 2))
                                    {
                                        msg = this.parseAssignExp();
                                        if (((this.token.value.value & 0xFF) == 99))
                                        {
                                            this.nextToken();
                                        }
                                    }
                                }
                                this.check(TOK.rightParentheses);
                                e = new ASTBase.AssertExp(loc, e, msg);
                                (f.frequires.get()).push(new ASTBase.ExpStatement(loc, e));
                                requireDo = false;
                            }
                            else
                            {
                                (f.frequires.get()).push(this.parseStatement(6, null, null));
                                requireDo = true;
                            }
                            /*goto L1*/continue L1;
                        case 176:
                            Loc loc_1 = this.token.value.loc.copy();
                            this.nextToken();
                            if (f.fensures == null)
                            {
                                f.fensures = refPtr(new DArray<ASTBase.Ensure>());
                            }
                            Identifier id = null;
                            if (((this.token.value.value & 0xFF) != 5))
                            {
                                this.check(TOK.leftParentheses);
                                if (((this.token.value.value & 0xFF) != 120) && ((this.token.value.value & 0xFF) != 9))
                                {
                                    this.error(new BytePtr("`(identifier) { ... }` or `(identifier; expression)` following `out` expected, not `%s`"), this.token.value.toChars());
                                }
                                if (((this.token.value.value & 0xFF) != 9))
                                {
                                    id = this.token.value.ident;
                                    this.nextToken();
                                }
                                if (((this.token.value.value & 0xFF) == 9))
                                {
                                    this.nextToken();
                                    ASTBase.Expression e_1 = this.parseAssignExp();
                                    ASTBase.Expression msg_1 = null;
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 2))
                                        {
                                            msg_1 = this.parseAssignExp();
                                            if (((this.token.value.value & 0xFF) == 99))
                                            {
                                                this.nextToken();
                                            }
                                        }
                                    }
                                    this.check(TOK.rightParentheses);
                                    e_1 = new ASTBase.AssertExp(loc_1, e_1, msg_1);
                                    (f.fensures.get()).push(new ASTBase.Ensure(id, new ASTBase.ExpStatement(loc_1, e_1)));
                                    requireDo = false;
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                }
                                this.check(TOK.rightParentheses);
                            }
                            (f.fensures.get()).push(new ASTBase.Ensure(id, this.parseStatement(6, null, null)));
                            requireDo = true;
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        case 9:
                            if (!literal)
                            {
                                if (!requireDo)
                                {
                                    this.nextToken();
                                }
                                break;
                            }
                            /*goto default*/ { __dispatch23 = -2; continue dispatched_23; }
                        default:
                        __dispatch23 = 0;
                        if (literal)
                        {
                            BytePtr sbody = pcopy(requireDo ? new BytePtr("do ") : new BytePtr(""));
                            this.error(new BytePtr("missing `%s{ ... }` for function literal"), sbody);
                        }
                        else if (!requireDo)
                        {
                            byte t = this.token.value.value;
                            if (((t & 0xFF) == 171) || ((t & 0xFF) == 182) || ((t & 0xFF) == 177) || ((t & 0xFF) == 195) || ((t & 0xFF) == 224) || ((t & 0xFF) == 216) || ((t & 0xFF) == 215))
                            {
                                this.error(new BytePtr("'%s' cannot be placed after a template constraint"), this.token.value.toChars());
                            }
                            else if (((t & 0xFF) == 225))
                            {
                                this.error(new BytePtr("attributes cannot be placed after a template constraint"));
                            }
                            else if (((t & 0xFF) == 183))
                            {
                                this.error(new BytePtr("cannot use function constraints for non-template functions. Use `static if` instead"));
                            }
                            else
                            {
                                this.error(new BytePtr("semicolon expected following function declaration"));
                            }
                        }
                        break;
                    }
                } while(__dispatch23 != 0);
                break;
            }
            if (literal && (f.fbody == null))
            {
                f.fbody = new ASTBase.CompoundStatement(Loc.initial.value, slice(new ASTBase.Statement[]{null}));
            }
            this.linkage = linksave;
            return f;
        }

        public  void checkDanglingElse(Loc elseloc) {
            if (((this.token.value.value & 0xFF) != 184) && ((this.token.value.value & 0xFF) != 198) && ((this.token.value.value & 0xFF) != 199) && (this.lookingForElse.linnum != 0))
            {
                this.warning(elseloc, new BytePtr("else is dangling, add { } after condition at %s"), this.lookingForElse.toChars(global.params.showColumns.value));
            }
        }

        public  void checkCstyleTypeSyntax(Loc loc, ASTBase.Type t, int alt, Identifier ident) {
            if (alt == 0)
            {
                return ;
            }
            BytePtr sp = pcopy(ident == null ? new BytePtr("") : new BytePtr(" "));
            BytePtr s = pcopy(ident == null ? new BytePtr("") : ident.toChars());
            this.error(loc, new BytePtr("instead of C-style syntax, use D-style `%s%s%s`"), t.toChars(), sp, s);
        }

        // from template ParseForeachArgs!(00)
        // from template Seq!()


        // from template ParseForeachArgs!(00)

        // from template ParseForeachArgs!(10)
        // from template Seq!()


        // from template ParseForeachArgs!(10)

        // from template ParseForeachArgs!(11)
        // from template Seq!(Ptr<ASTBase.Dsymbol>)


        // from template ParseForeachArgs!(11)


        // from template ParseForeachRet!(00)


        // from template ParseForeachRet!(10)

        // from template ParseForeachRet!(11)


        // from template parseForeach!(00)
        public  ASTBase.Statement parseForeach00(Loc loc) {
            byte op = this.token.value.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            Ptr<DArray<ASTBase.Parameter>> parameters = refPtr(new DArray<ASTBase.Parameter>());
        L_outer12:
            for (; 1 != 0;){
                Identifier ai = null;
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if (stc != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch24 = 0;
                        dispatched_24:
                        do {
                            switch (__dispatch24 != 0 ? __dispatch24 : (this.token.value.value & 0xFF))
                            {
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 156:
                                    stc = 8388608L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 158:
                                    storageClass = this.appendStorageClass(storageClass, 268435456L);
                                    this.nextToken();
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 2147483648L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                default:
                                break;
                            }
                        } while(__dispatch24 != 0);
                    }
                    try {
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 9))
                            {
                                ai = this.token.value.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (ai == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                        }
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    ASTBase.Parameter p = new ASTBase.Parameter(storageClass, at, ai, null, null);
                    (parameters.get()).push(p);
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        continue L_outer12;
                    }
                    break;
                } catch(Dispatch0 __d){}
                break;
            }
            this.check(TOK.semicolon);
            ASTBase.Expression aggr = this.parseExpression();
            if (((this.token.value.value & 0xFF) == 31) && ((parameters.get()).length == 1))
            {
                ASTBase.Parameter p = (parameters.get()).get(0);
                this.nextToken();
                ASTBase.Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, ptr(endloc));
                ASTBase.ForeachRangeStatement rangefe = new ASTBase.ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return rangefe;
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, ptr(endloc));
                ASTBase.ForeachStatement aggrfe = new ASTBase.ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return aggrfe;
            }
        }


        // from template parseForeach!(10)
        public  ASTBase.StaticForeachStatement parseForeach10(Loc loc) {
            this.nextToken();
            byte op = this.token.value.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            Ptr<DArray<ASTBase.Parameter>> parameters = refPtr(new DArray<ASTBase.Parameter>());
        L_outer13:
            for (; 1 != 0;){
                Identifier ai = null;
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if (stc != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch25 = 0;
                        dispatched_25:
                        do {
                            switch (__dispatch25 != 0 ? __dispatch25 : (this.token.value.value & 0xFF))
                            {
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 156:
                                    stc = 8388608L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 158:
                                    storageClass = this.appendStorageClass(storageClass, 268435456L);
                                    this.nextToken();
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 2147483648L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                default:
                                break;
                            }
                        } while(__dispatch25 != 0);
                    }
                    try {
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 9))
                            {
                                ai = this.token.value.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (ai == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                        }
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    ASTBase.Parameter p = new ASTBase.Parameter(storageClass, at, ai, null, null);
                    (parameters.get()).push(p);
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        continue L_outer13;
                    }
                    break;
                } catch(Dispatch0 __d){}
                break;
            }
            this.check(TOK.semicolon);
            ASTBase.Expression aggr = this.parseExpression();
            if (((this.token.value.value & 0xFF) == 31) && ((parameters.get()).length == 1))
            {
                ASTBase.Parameter p = (parameters.get()).get(0);
                this.nextToken();
                ASTBase.Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, ptr(endloc));
                ASTBase.ForeachRangeStatement rangefe = new ASTBase.ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return new ASTBase.StaticForeachStatement(loc, new ASTBase.StaticForeach(loc, null, rangefe));
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, ptr(endloc));
                ASTBase.ForeachStatement aggrfe = new ASTBase.ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return new ASTBase.StaticForeachStatement(loc, new ASTBase.StaticForeach(loc, aggrfe, null));
            }
        }


        // from template parseForeach!(11)
        public  ASTBase.StaticForeachDeclaration parseForeach11(Loc loc, Ptr<ASTBase.Dsymbol> _param_1) {
            this.nextToken();
            Ptr<ASTBase.Dsymbol> pLastDecl = pcopy(_param_1);
            byte op = this.token.value.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            Ptr<DArray<ASTBase.Parameter>> parameters = refPtr(new DArray<ASTBase.Parameter>());
        L_outer14:
            for (; 1 != 0;){
                Identifier ai = null;
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if (stc != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch26 = 0;
                        dispatched_26:
                        do {
                            switch (__dispatch26 != 0 ? __dispatch26 : (this.token.value.value & 0xFF))
                            {
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 156:
                                    stc = 8388608L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 158:
                                    storageClass = this.appendStorageClass(storageClass, 268435456L);
                                    this.nextToken();
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 2147483648L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                default:
                                break;
                            }
                        } while(__dispatch26 != 0);
                    }
                    try {
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 9))
                            {
                                ai = this.token.value.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (ai == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                        }
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    ASTBase.Parameter p = new ASTBase.Parameter(storageClass, at, ai, null, null);
                    (parameters.get()).push(p);
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        continue L_outer14;
                    }
                    break;
                } catch(Dispatch0 __d){}
                break;
            }
            this.check(TOK.semicolon);
            ASTBase.Expression aggr = this.parseExpression();
            if (((this.token.value.value & 0xFF) == 31) && ((parameters.get()).length == 1))
            {
                ASTBase.Parameter p = (parameters.get()).get(0);
                this.nextToken();
                ASTBase.Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = null;
                ASTBase.ForeachRangeStatement rangefe = new ASTBase.ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return new ASTBase.StaticForeachDeclaration(new ASTBase.StaticForeach(loc, null, rangefe), this.parseBlock(pLastDecl, null));
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = null;
                ASTBase.ForeachStatement aggrfe = new ASTBase.ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return new ASTBase.StaticForeachDeclaration(new ASTBase.StaticForeach(loc, aggrfe, null), this.parseBlock(pLastDecl, null));
            }
        }
            long stc = 0;
            long storageClass = 0;
            ASTBase.Parameter param = null;


        public  ASTBase.Statement parseStatement(int flags, Ptr<BytePtr> endPtr, Ptr<Loc> pEndloc) {
            ASTBase.Statement s = null;
            ASTBase.Condition cond = null;
            ASTBase.Statement ifbody = null;
            ASTBase.Statement elsebody = null;
            boolean isfinal = false;
            Loc loc = this.token.value.loc.copy();
            if (((flags & ParseStatementFlags.curly) != 0) && ((this.token.value.value & 0xFF) != 5))
            {
                this.error(new BytePtr("statement expected to be `{ }`, not `%s`"), this.token.value.toChars());
            }
            {
                int __dispatch27 = 0;
                dispatched_27:
                do {
                    switch (__dispatch27 != 0 ? __dispatch27 : (this.token.value.value & 0xFF))
                    {
                        case 120:
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 7))
                            {
                                Ptr<Token> nt = this.peek(t);
                                if ((((nt.get()).value & 0xFF) == 7))
                                {
                                    this.nextToken();
                                    this.nextToken();
                                    this.nextToken();
                                    this.error(new BytePtr("use `.` for member lookup, not `::`"));
                                    break;
                                }
                                Identifier ident = this.token.value.ident;
                                this.nextToken();
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 6))
                                {
                                    s = null;
                                }
                                else if (((this.token.value.value & 0xFF) == 5))
                                {
                                    s = this.parseStatement(6, null, null);
                                }
                                else
                                {
                                    s = this.parseStatement(16, null, null);
                                }
                                s = new ASTBase.LabelStatement(loc, ident, s);
                                break;
                            }
                            /*goto case*/{ __dispatch27 = 97; continue dispatched_27; }
                        case 97:
                            __dispatch27 = 0;
                        case 39:
                        case 229:
                        case 213:
                            if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.mustIfDstyle, TOK.reserved, null))
                            {
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            }
                            /*goto Lexp*/{ __dispatch27 = -2; continue dispatched_27; }
                        case 14:
                        case 123:
                        case 124:
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 117:
                        case 118:
                        case 119:
                        case 13:
                        case 15:
                        case 16:
                        case 121:
                        case 122:
                        case 1:
                        case 12:
                        case 78:
                        case 75:
                        case 74:
                        case 92:
                        case 91:
                        case 93:
                        case 94:
                        case 22:
                        case 23:
                        case 160:
                        case 161:
                        case 42:
                        case 63:
                        case 3:
                        case 219:
                        case 220:
                        case 218:
                        case 221:
                        case 222:
                        case 223:
                        /*Lexp:*/
                        case -2:
                        __dispatch27 = 0;
                            {
                                ASTBase.Expression exp = this.parseExpression();
                                this.check(TOK.semicolon, new BytePtr("statement"));
                                s = new ASTBase.ExpStatement(loc, exp);
                                break;
                            }
                        case 169:
                            Ptr<Token> t_1 = this.peek(ptr(this.token));
                            if ((((t_1.get()).value & 0xFF) == 14))
                            {
                                s = new ASTBase.StaticAssertStatement(this.parseStaticAssert());
                                break;
                            }
                            if ((((t_1.get()).value & 0xFF) == 183))
                            {
                                cond = this.parseStaticIfCondition();
                                /*goto Lcondition*/{ __dispatch27 = -3; continue dispatched_27; }
                            }
                            if ((((t_1.get()).value & 0xFF) == 201) || (((t_1.get()).value & 0xFF) == 202))
                            {
                                s = this.parseForeach10(loc);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                                }
                                break;
                            }
                            if ((((t_1.get()).value & 0xFF) == 157))
                            {
                                Ptr<DArray<ASTBase.Dsymbol>> imports = this.parseImport();
                                s = new ASTBase.ImportStatement(loc, imports);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                                }
                                break;
                            }
                            /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                        case 170:
                            if (((this.peekNext() & 0xFF) == 188))
                            {
                                this.nextToken();
                                isfinal = true;
                                /*goto Lswitch*/{ __dispatch27 = -4; continue dispatched_27; }
                            }
                            /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                        case 149:
                        case 150:
                        case 151:
                        case 148:
                        case 129:
                        case 130:
                        case 131:
                        case 132:
                        case 133:
                        case 134:
                        case 135:
                        case 136:
                        case 137:
                        case 138:
                        case 139:
                        case 140:
                        case 141:
                        case 142:
                        case 143:
                        case 144:
                        case 145:
                        case 146:
                        case 147:
                        case 128:
                            if (((this.peekNext() & 0xFF) == 97))
                            {
                                /*goto Lexp*/{ __dispatch27 = -2; continue dispatched_27; }
                            }
                            if (((this.peekNext() & 0xFF) == 1))
                            {
                                /*goto Lexp*/{ __dispatch27 = -2; continue dispatched_27; }
                            }
                        case 158:
                        case 171:
                        case 179:
                        case 172:
                        case 164:
                        case 163:
                        case 182:
                        case 224:
                        case 177:
                        case 174:
                        case 216:
                        case 215:
                        case 210:
                        case 217:
                        case 225:
                        case 152:
                        case 155:
                        case 153:
                        case 154:
                        /*Ldeclaration:*/
                        case -1:
                        __dispatch27 = 0;
                            {
                                Ptr<DArray<ASTBase.Dsymbol>> a = this.parseDeclarations(false, null, null);
                                if (((a.get()).length > 1))
                                {
                                    Ptr<DArray<ASTBase.Statement>> as = refPtr(new DArray<ASTBase.Statement>());
                                    (as.get()).reserve((a.get()).length);
                                    {
                                        int __key489 = 0;
                                        int __limit490 = (a.get()).length;
                                        for (; (__key489 < __limit490);__key489 += 1) {
                                            int i = __key489;
                                            ASTBase.Dsymbol d = (a.get()).get(i);
                                            s = new ASTBase.ExpStatement(loc, d);
                                            (as.get()).push(s);
                                        }
                                    }
                                    s = new ASTBase.CompoundDeclarationStatement(loc, as);
                                }
                                else if (((a.get()).length == 1))
                                {
                                    ASTBase.Dsymbol d_1 = (a.get()).get(0);
                                    s = new ASTBase.ExpStatement(loc, d_1);
                                }
                                else
                                {
                                    s = new ASTBase.ExpStatement(loc, (ASTBase.Dsymbol)null);
                                }
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                                }
                                break;
                            }
                        case 156:
                            ASTBase.Dsymbol d_2 = null;
                            Ptr<Token> t_2 = this.peek(ptr(this.token));
                            if ((((t_2.get()).value & 0xFF) == 5) || (((t_2.get()).value & 0xFF) == 7))
                            {
                                d_2 = this.parseEnum();
                            }
                            else if ((((t_2.get()).value & 0xFF) != 120))
                            {
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            }
                            else
                            {
                                t_2 = this.peek(t_2);
                                if ((((t_2.get()).value & 0xFF) == 5) || (((t_2.get()).value & 0xFF) == 7) || (((t_2.get()).value & 0xFF) == 9))
                                {
                                    d_2 = this.parseEnum();
                                }
                                else
                                {
                                    /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                                }
                            }
                            s = new ASTBase.ExpStatement(loc, d_2);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                            {
                                s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                            }
                            break;
                        case 162:
                            Ptr<Token> t_3 = this.peek(ptr(this.token));
                            if ((((t_3.get()).value & 0xFF) == 1))
                            {
                                ASTBase.Expression e = this.parseAssignExp();
                                this.check(TOK.semicolon);
                                if (((e.op & 0xFF) == 162))
                                {
                                    ASTBase.CompileExp cpe = (ASTBase.CompileExp)e;
                                    s = new ASTBase.CompileStatement(loc, cpe.exps);
                                }
                                else
                                {
                                    s = new ASTBase.ExpStatement(loc, e);
                                }
                                break;
                            }
                            ASTBase.Dsymbol d_3 = this.parseMixin();
                            s = new ASTBase.ExpStatement(loc, d_3);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                            {
                                s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                            }
                            break;
                        case 5:
                            Loc lookingForElseSave = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.value.copy();
                            this.nextToken();
                            Ptr<DArray<ASTBase.Statement>> statements = refPtr(new DArray<ASTBase.Statement>());
                            for (; ((this.token.value.value & 0xFF) != 6) && ((this.token.value.value & 0xFF) != 11);){
                                (statements.get()).push(this.parseStatement(9, null, null));
                            }
                            if (endPtr != null)
                            {
                                endPtr.set(0, this.token.value.ptr);
                            }
                            this.endloc = this.token.value.loc.copy();
                            if (pEndloc != null)
                            {
                                pEndloc.set(0, this.token.value.loc);
                                pEndloc = null;
                            }
                            s = new ASTBase.CompoundStatement(loc, statements);
                            if ((flags & 10) != 0)
                            {
                                s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                            }
                            this.check(TOK.rightCurly, new BytePtr("compound statement"));
                            this.lookingForElse = lookingForElseSave.copy();
                            break;
                        case 185:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            ASTBase.Expression condition = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            Loc endloc = new Loc();
                            ASTBase.Statement _body = this.parseStatement(2, null, ptr(endloc));
                            s = new ASTBase.WhileStatement(loc, condition, _body, endloc);
                            break;
                        case 9:
                            if ((flags & ParseStatementFlags.semiOk) == 0)
                            {
                                if ((flags & ParseStatementFlags.semi) != 0)
                                {
                                    this.deprecation(new BytePtr("use `{ }` for an empty statement, not `;`"));
                                }
                                else
                                {
                                    this.error(new BytePtr("use `{ }` for an empty statement, not `;`"));
                                }
                            }
                            this.nextToken();
                            s = new ASTBase.ExpStatement(loc, (ASTBase.Expression)null);
                            break;
                        case 187:
                            ASTBase.Statement _body_1 = null;
                            ASTBase.Expression condition_1 = null;
                            this.nextToken();
                            Loc lookingForElseSave_1 = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.value.copy();
                            _body_1 = this.parseStatement(2, null, null);
                            this.lookingForElse = lookingForElseSave_1.copy();
                            this.check(TOK.while_);
                            this.check(TOK.leftParentheses);
                            condition_1 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                this.nextToken();
                            }
                            else
                            {
                                this.error(new BytePtr("terminating `;` required after do-while statement"));
                            }
                            s = new ASTBase.DoStatement(loc, _body_1, condition_1, this.token.value.loc);
                            break;
                        case 186:
                            ASTBase.Statement _init = null;
                            ASTBase.Expression condition_2 = null;
                            ASTBase.Expression increment = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                _init = null;
                                this.nextToken();
                            }
                            else
                            {
                                Loc lookingForElseSave_2 = this.lookingForElse.copy();
                                this.lookingForElse = Loc.initial.value.copy();
                                _init = this.parseStatement(0, null, null);
                                this.lookingForElse = lookingForElseSave_2.copy();
                            }
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                condition_2 = null;
                                this.nextToken();
                            }
                            else
                            {
                                condition_2 = this.parseExpression();
                                this.check(TOK.semicolon, new BytePtr("`for` condition"));
                            }
                            if (((this.token.value.value & 0xFF) == 2))
                            {
                                increment = null;
                                this.nextToken();
                            }
                            else
                            {
                                increment = this.parseExpression();
                                this.check(TOK.rightParentheses);
                            }
                            Loc endloc_1 = new Loc();
                            ASTBase.Statement _body_2 = this.parseStatement(2, null, ptr(endloc_1));
                            s = new ASTBase.ForStatement(loc, _init, condition_2, increment, _body_2, endloc_1);
                            break;
                        case 201:
                        case 202:
                            s = this.parseForeach00(loc);
                            break;
                        case 183:
                            ASTBase.Expression condition_3 = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            storageClass = 0L;
                            stc = 0L;
                        /*LagainStc:*/
                        case -5:
                        __dispatch27 = 0;
                            if (stc != 0)
                            {
                                storageClass = this.appendStorageClass(storageClass, stc);
                                this.nextToken();
                            }
                            {
                                int __dispatch28 = 0;
                                dispatched_28:
                                do {
                                    switch (__dispatch28 != 0 ? __dispatch28 : (this.token.value.value & 0xFF))
                                    {
                                        case 210:
                                            stc = 2097152L;
                                            /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                        case 179:
                                            stc = 256L;
                                            /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                        case 171:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 4L;
                                                /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                            }
                                            break;
                                        case 182:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 1048576L;
                                                /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                            }
                                            break;
                                        case 224:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 536870912L;
                                                /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                            }
                                            break;
                                        case 177:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 2147483648L;
                                                /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                            }
                                            break;
                                        default:
                                        break;
                                    }
                                } while(__dispatch28 != 0);
                            }
                            Ptr<Token> n = this.peek(ptr(this.token));
                            if ((storageClass != 0L) && ((this.token.value.value & 0xFF) == 120) && (((n.get()).value & 0xFF) != 90) && (((n.get()).value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("found `%s` while expecting `=` or identifier"), (n.get()).toChars());
                            }
                            else if ((storageClass != 0L) && ((this.token.value.value & 0xFF) == 120) && (((n.get()).value & 0xFF) == 90))
                            {
                                Identifier ai = this.token.value.ident;
                                ASTBase.Type at = null;
                                this.nextToken();
                                this.check(TOK.assign);
                                param = new ASTBase.Parameter(storageClass, at, ai, null, null);
                            }
                            else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.must, TOK.assign, null))
                            {
                                Identifier ai_1 = null;
                                ASTBase.Type at_1 = this.parseType(ptr(ai_1), null);
                                this.check(TOK.assign);
                                param = new ASTBase.Parameter(storageClass, at_1, ai_1, null, null);
                            }
                            condition_3 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            {
                                Loc lookingForElseSave_3 = this.lookingForElse.copy();
                                this.lookingForElse = loc.copy();
                                ifbody = this.parseStatement(2, null, null);
                                this.lookingForElse = lookingForElseSave_3.copy();
                            }
                            if (((this.token.value.value & 0xFF) == 184))
                            {
                                Loc elseloc = this.token.value.loc.copy();
                                this.nextToken();
                                elsebody = this.parseStatement(2, null, null);
                                this.checkDanglingElse(elseloc);
                            }
                            else
                            {
                                elsebody = null;
                            }
                            if ((condition_3 != null) && (ifbody != null))
                            {
                                s = new ASTBase.IfStatement(loc, param, condition_3, ifbody, elsebody, this.token.value.loc);
                            }
                            else
                            {
                                s = null;
                            }
                            break;
                        case 184:
                            this.error(new BytePtr("found `else` without a corresponding `if`, `version` or `debug` statement"));
                            /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                        case 203:
                            if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
                            {
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            }
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("scope identifier expected"));
                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                            }
                            else
                            {
                                byte t_4 = TOK.onScopeExit;
                                Identifier id = this.token.value.ident;
                                if ((pequals(id, Id.exit)))
                                {
                                    t_4 = TOK.onScopeExit;
                                }
                                else if ((pequals(id, Id.failure)))
                                {
                                    t_4 = TOK.onScopeFailure;
                                }
                                else if ((pequals(id, Id.success)))
                                {
                                    t_4 = TOK.onScopeSuccess;
                                }
                                else
                                {
                                    this.error(new BytePtr("valid scope identifiers are `exit`, `failure`, or `success`, not `%s`"), id.toChars());
                                }
                                this.nextToken();
                                this.check(TOK.rightParentheses);
                                ASTBase.Statement st = this.parseStatement(2, null, null);
                                s = new ASTBase.ScopeGuardStatement(loc, t_4, st);
                                break;
                            }
                        case 173:
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.error(new BytePtr("debug conditions can only be declared at module scope"));
                                this.nextToken();
                                this.nextToken();
                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                            }
                            cond = this.parseDebugCondition();
                            /*goto Lcondition*/{ __dispatch27 = -3; continue dispatched_27; }
                        case 33:
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.error(new BytePtr("version conditions can only be declared at module scope"));
                                this.nextToken();
                                this.nextToken();
                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                            }
                            cond = this.parseVersionCondition();
                            /*goto Lcondition*/{ __dispatch27 = -3; continue dispatched_27; }
                        /*Lcondition:*/
                        case -3:
                        __dispatch27 = 0;
                            {
                                Loc lookingForElseSave_4 = this.lookingForElse.copy();
                                this.lookingForElse = loc.copy();
                                ifbody = this.parseStatement(0, null, null);
                                this.lookingForElse = lookingForElseSave_4.copy();
                            }
                            elsebody = null;
                            if (((this.token.value.value & 0xFF) == 184))
                            {
                                Loc elseloc_1 = this.token.value.loc.copy();
                                this.nextToken();
                                elsebody = this.parseStatement(0, null, null);
                                this.checkDanglingElse(elseloc_1);
                            }
                            s = new ASTBase.ConditionalStatement(loc, cond, ifbody, elsebody);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                            {
                                s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                            }
                            break;
                        case 40:
                            Identifier ident_1 = null;
                            Ptr<DArray<ASTBase.Expression>> args = null;
                            ASTBase.Statement _body_3 = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("`pragma(identifier)` expected"));
                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                            }
                            ident_1 = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 99) && ((this.peekNext() & 0xFF) != 2))
                            {
                                args = this.parseArguments();
                            }
                            else
                            {
                                this.check(TOK.rightParentheses);
                            }
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                this.nextToken();
                                _body_3 = null;
                            }
                            else
                            {
                                _body_3 = this.parseStatement(1, null, null);
                            }
                            s = new ASTBase.PragmaStatement(loc, ident_1, args, _body_3);
                            break;
                        case 188:
                            isfinal = false;
                            /*goto Lswitch*/{ __dispatch27 = -4; continue dispatched_27; }
                        /*Lswitch:*/
                        case -4:
                        __dispatch27 = 0;
                            {
                                this.nextToken();
                                this.check(TOK.leftParentheses);
                                ASTBase.Expression condition_4 = this.parseExpression();
                                this.check(TOK.rightParentheses);
                                ASTBase.Statement _body_4 = this.parseStatement(2, null, null);
                                s = new ASTBase.SwitchStatement(loc, condition_4, _body_4, isfinal);
                                break;
                            }
                        case 189:
                            ASTBase.Expression exp_1 = null;
                            DArray<ASTBase.Expression> cases = new DArray<ASTBase.Expression>();
                            try {
                                ASTBase.Expression last = null;
                                for (; 1 != 0;){
                                    this.nextToken();
                                    exp_1 = this.parseAssignExp();
                                    cases.push(exp_1);
                                    if (((this.token.value.value & 0xFF) != 99))
                                    {
                                        break;
                                    }
                                }
                                this.check(TOK.colon);
                                if (((this.token.value.value & 0xFF) == 31))
                                {
                                    if ((cases.length > 1))
                                    {
                                        this.error(new BytePtr("only one `case` allowed for start of case range"));
                                    }
                                    this.nextToken();
                                    this.check(TOK.case_);
                                    last = this.parseAssignExp();
                                    this.check(TOK.colon);
                                }
                                if ((flags & ParseStatementFlags.curlyScope) != 0)
                                {
                                    Ptr<DArray<ASTBase.Statement>> statements_1 = refPtr(new DArray<ASTBase.Statement>());
                                    for (; ((this.token.value.value & 0xFF) != 189) && ((this.token.value.value & 0xFF) != 190) && ((this.token.value.value & 0xFF) != 11) && ((this.token.value.value & 0xFF) != 6);){
                                        (statements_1.get()).push(this.parseStatement(9, null, null));
                                    }
                                    s = new ASTBase.CompoundStatement(loc, statements_1);
                                }
                                else
                                {
                                    s = this.parseStatement(1, null, null);
                                }
                                s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                                if (last != null)
                                {
                                    s = new ASTBase.CaseRangeStatement(loc, exp_1, last, s);
                                }
                                else
                                {
                                    {
                                        int i_1 = cases.length;
                                        for (; i_1 != 0;i_1--){
                                            exp_1 = cases.get(i_1 - 1);
                                            s = new ASTBase.CaseStatement(loc, exp_1, s);
                                        }
                                    }
                                }
                                break;
                            }
                            finally {
                            }
                        case 190:
                            this.nextToken();
                            this.check(TOK.colon);
                            if ((flags & ParseStatementFlags.curlyScope) != 0)
                            {
                                Ptr<DArray<ASTBase.Statement>> statements_2 = refPtr(new DArray<ASTBase.Statement>());
                                for (; ((this.token.value.value & 0xFF) != 189) && ((this.token.value.value & 0xFF) != 190) && ((this.token.value.value & 0xFF) != 11) && ((this.token.value.value & 0xFF) != 6);){
                                    (statements_2.get()).push(this.parseStatement(9, null, null));
                                }
                                s = new ASTBase.CompoundStatement(loc, statements_2);
                            }
                            else
                            {
                                s = this.parseStatement(1, null, null);
                            }
                            s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                            s = new ASTBase.DefaultStatement(loc, s);
                            break;
                        case 195:
                            ASTBase.Expression exp_2 = null;
                            this.nextToken();
                            exp_2 = ((this.token.value.value & 0xFF) == 9) ? null : this.parseExpression();
                            this.check(TOK.semicolon, new BytePtr("`return` statement"));
                            s = new ASTBase.ReturnStatement(loc, exp_2);
                            break;
                        case 191:
                            Identifier ident_2 = null;
                            this.nextToken();
                            ident_2 = null;
                            if (((this.token.value.value & 0xFF) == 120))
                            {
                                ident_2 = this.token.value.ident;
                                this.nextToken();
                            }
                            this.check(TOK.semicolon, new BytePtr("`break` statement"));
                            s = new ASTBase.BreakStatement(loc, ident_2);
                            break;
                        case 192:
                            Identifier ident_3 = null;
                            this.nextToken();
                            ident_3 = null;
                            if (((this.token.value.value & 0xFF) == 120))
                            {
                                ident_3 = this.token.value.ident;
                                this.nextToken();
                            }
                            this.check(TOK.semicolon, new BytePtr("`continue` statement"));
                            s = new ASTBase.ContinueStatement(loc, ident_3);
                            break;
                        case 196:
                            Identifier ident_4 = null;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 190))
                            {
                                this.nextToken();
                                s = new ASTBase.GotoDefaultStatement(loc);
                            }
                            else if (((this.token.value.value & 0xFF) == 189))
                            {
                                ASTBase.Expression exp_3 = null;
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) != 9))
                                {
                                    exp_3 = this.parseExpression();
                                }
                                s = new ASTBase.GotoCaseStatement(loc, exp_3);
                            }
                            else
                            {
                                if (((this.token.value.value & 0xFF) != 120))
                                {
                                    this.error(new BytePtr("identifier expected following `goto`"));
                                    ident_4 = null;
                                }
                                else
                                {
                                    ident_4 = this.token.value.ident;
                                    this.nextToken();
                                }
                                s = new ASTBase.GotoStatement(loc, ident_4);
                            }
                            this.check(TOK.semicolon, new BytePtr("`goto` statement"));
                            break;
                        case 194:
                            ASTBase.Expression exp_4 = null;
                            ASTBase.Statement _body_5 = null;
                            Ptr<Token> t_5 = this.peek(ptr(this.token));
                            if (this.skipAttributes(t_5, ptr(t_5)) && (((t_5.get()).value & 0xFF) == 153))
                            {
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            }
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.nextToken();
                                exp_4 = this.parseExpression();
                                this.check(TOK.rightParentheses);
                            }
                            else
                            {
                                exp_4 = null;
                            }
                            _body_5 = this.parseStatement(2, null, null);
                            s = new ASTBase.SynchronizedStatement(loc, exp_4, _body_5);
                            break;
                        case 193:
                            ASTBase.Expression exp_5 = null;
                            ASTBase.Statement _body_6 = null;
                            Loc endloc_2 = loc.copy();
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            exp_5 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            _body_6 = this.parseStatement(2, null, ptr(endloc_2));
                            s = new ASTBase.WithStatement(loc, exp_5, _body_6, endloc_2);
                            break;
                        case 197:
                            ASTBase.Statement _body_7 = null;
                            Ptr<DArray<ASTBase.Catch>> catches = null;
                            ASTBase.Statement finalbody = null;
                            this.nextToken();
                            Loc lookingForElseSave_5 = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.value.copy();
                            _body_7 = this.parseStatement(2, null, null);
                            this.lookingForElse = lookingForElseSave_5.copy();
                            for (; ((this.token.value.value & 0xFF) == 198);){
                                ASTBase.Statement handler = null;
                                ASTBase.Catch c = null;
                                ASTBase.Type t_6 = null;
                                Identifier id_1 = null;
                                Loc catchloc = this.token.value.loc.copy();
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 5) || ((this.token.value.value & 0xFF) != 1))
                                {
                                    t_6 = null;
                                    id_1 = null;
                                }
                                else
                                {
                                    this.check(TOK.leftParentheses);
                                    id_1 = null;
                                    t_6 = this.parseType(ptr(id_1), null);
                                    this.check(TOK.rightParentheses);
                                }
                                handler = this.parseStatement(0, null, null);
                                c = new ASTBase.Catch(catchloc, t_6, id_1, handler);
                                if (catches == null)
                                {
                                    catches = refPtr(new DArray<ASTBase.Catch>());
                                }
                                (catches.get()).push(c);
                            }
                            if (((this.token.value.value & 0xFF) == 199))
                            {
                                this.nextToken();
                                finalbody = this.parseStatement(2, null, null);
                            }
                            s = _body_7;
                            if ((catches == null) && (finalbody == null))
                            {
                                this.error(new BytePtr("`catch` or `finally` expected following `try`"));
                            }
                            else
                            {
                                if (catches != null)
                                {
                                    s = new ASTBase.TryCatchStatement(loc, _body_7, catches);
                                }
                                if (finalbody != null)
                                {
                                    s = new ASTBase.TryFinallyStatement(loc, s, finalbody);
                                }
                            }
                            break;
                        case 21:
                            ASTBase.Expression exp_6 = null;
                            this.nextToken();
                            exp_6 = this.parseExpression();
                            this.check(TOK.semicolon, new BytePtr("`throw` statement"));
                            s = new ASTBase.ThrowStatement(loc, exp_6);
                            break;
                        case 200:
                            Loc labelloc = new Loc();
                            this.nextToken();
                            long stc_1 = this.parsePostfix(0L, null);
                            if ((stc_1 & 2685403140L) != 0)
                            {
                                this.error(new BytePtr("`const`/`immutable`/`shared`/`inout` attributes are not allowed on `asm` blocks"));
                            }
                            this.check(TOK.leftCurly);
                            Ptr<Token> toklist = null;
                            Ptr<Ptr<Token>> ptoklist = pcopy(ptr(toklist));
                            Identifier label = null;
                            Ptr<DArray<ASTBase.Statement>> statements_3 = refPtr(new DArray<ASTBase.Statement>());
                            int nestlevel = 0;
                        L_outer15:
                            for (; 1 != 0;){
                                {
                                    int __dispatch29 = 0;
                                    dispatched_29:
                                    do {
                                        switch (__dispatch29 != 0 ? __dispatch29 : (this.token.value.value & 0xFF))
                                        {
                                            case 120:
                                                if (toklist == null)
                                                {
                                                    Ptr<Token> t_7 = this.peek(ptr(this.token));
                                                    if ((((t_7.get()).value & 0xFF) == 7))
                                                    {
                                                        label = this.token.value.ident;
                                                        labelloc = this.token.value.loc.copy();
                                                        this.nextToken();
                                                        this.nextToken();
                                                        continue L_outer15;
                                                    }
                                                }
                                                /*goto default*/ { __dispatch29 = -1; continue dispatched_29; }
                                            case 5:
                                                nestlevel += 1;
                                                /*goto default*/ { __dispatch29 = -1; continue dispatched_29; }
                                            case 6:
                                                if ((nestlevel > 0))
                                                {
                                                    nestlevel -= 1;
                                                    /*goto default*/ { __dispatch29 = -1; continue dispatched_29; }
                                                }
                                                if ((toklist != null) || (label != null))
                                                {
                                                    this.error(new BytePtr("`asm` statements must end in `;`"));
                                                }
                                                break;
                                            case 9:
                                                if ((nestlevel != 0))
                                                {
                                                    this.error(new BytePtr("mismatched number of curly brackets"));
                                                }
                                                s = null;
                                                if ((toklist != null) || (label != null))
                                                {
                                                    s = new ASTBase.AsmStatement(this.token.value.loc, toklist);
                                                    toklist = null;
                                                    ptoklist = pcopy(ptr(toklist));
                                                    if (label != null)
                                                    {
                                                        s = new ASTBase.LabelStatement(labelloc, label, s);
                                                        label = null;
                                                    }
                                                    (statements_3.get()).push(s);
                                                }
                                                this.nextToken();
                                                continue L_outer15;
                                            case 11:
                                                this.error(new BytePtr("matching `}` expected, not end of file"));
                                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                                            default:
                                            __dispatch29 = 0;
                                            ptoklist.set(0, this.allocateToken());
                                            (ptoklist.get()).set(0, (ptr(this.token)));
                                            ptoklist = pcopy((ptr(ptoklist.get().get().next)));
                                            ptoklist.set(0, null);
                                            this.nextToken();
                                            continue L_outer15;
                                        }
                                    } while(__dispatch29 != 0);
                                }
                                break;
                            }
                            s = new ASTBase.CompoundAsmStatement(loc, statements_3, stc_1);
                            this.nextToken();
                            break;
                        case 157:
                            if (((this.peekNext() & 0xFF) == 1))
                            {
                                ASTBase.Expression e_1 = this.parseExpression();
                                this.check(TOK.semicolon);
                                s = new ASTBase.ExpStatement(loc, e_1);
                            }
                            else
                            {
                                Ptr<DArray<ASTBase.Dsymbol>> imports_1 = this.parseImport();
                                s = new ASTBase.ImportStatement(loc, imports_1);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.value.loc);
                                }
                            }
                            break;
                        case 36:
                            ASTBase.Dsymbol d_4 = this.parseTemplateDeclaration(false);
                            s = new ASTBase.ExpStatement(loc, d_4);
                            break;
                        default:
                        __dispatch27 = 0;
                        this.error(new BytePtr("found `%s` instead of statement"), this.token.value.toChars());
                        /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                    /*Lerror:*/
                    case -6:
                    __dispatch27 = 0;
                        for (; ((this.token.value.value & 0xFF) != 6) && ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                            this.nextToken();
                        }
                        if (((this.token.value.value & 0xFF) == 9))
                        {
                            this.nextToken();
                        }
                        s = null;
                        break;
                    }
                } while(__dispatch27 != 0);
            }
            if (pEndloc != null)
            {
                pEndloc.set(0, this.prevloc);
            }
            return s;
        }

        // defaulted all parameters starting with #3
        public  ASTBase.Statement parseStatement(int flags, Ptr<BytePtr> endPtr) {
            return parseStatement(flags, endPtr, null);
        }

        // defaulted all parameters starting with #2
        public  ASTBase.Statement parseStatement(int flags) {
            return parseStatement(flags, null, null);
        }

        public  ASTBase.Initializer parseInitializer() {
            ASTBase.StructInitializer _is = null;
            ASTBase.ArrayInitializer ia = null;
            ASTBase.ExpInitializer ie = null;
            ASTBase.Expression e = null;
            Identifier id = null;
            ASTBase.Initializer value = null;
            int comma = 0;
            Loc loc = this.token.value.loc.copy();
            Ptr<Token> t = null;
            int braces = 0;
            int brackets = 0;
            {
                int __dispatch30 = 0;
                dispatched_30:
                do {
                    switch (__dispatch30 != 0 ? __dispatch30 : (this.token.value.value & 0xFF))
                    {
                        case 5:
                            braces = 1;
                            {
                                t = this.peek(ptr(this.token));
                            L_outer16:
                                for (; 1 != 0;t = this.peek(t)){
                                    {
                                        int __dispatch31 = 0;
                                        dispatched_31:
                                        do {
                                            switch (__dispatch31 != 0 ? __dispatch31 : ((t.get()).value & 0xFF))
                                            {
                                                case 200:
                                                case 153:
                                                case 173:
                                                case 156:
                                                case 183:
                                                case 154:
                                                case 40:
                                                case 203:
                                                case 9:
                                                case 152:
                                                case 188:
                                                case 194:
                                                case 197:
                                                case 155:
                                                case 33:
                                                case 185:
                                                case 193:
                                                    if ((braces == 1))
                                                    {
                                                        /*goto Lexpression*/{ __dispatch30 = -1; continue dispatched_30; }
                                                    }
                                                    continue L_outer16;
                                                case 5:
                                                    braces++;
                                                    continue L_outer16;
                                                case 6:
                                                    if (((braces -= 1) == 0))
                                                    {
                                                        break;
                                                    }
                                                    continue L_outer16;
                                                case 11:
                                                    break;
                                                default:
                                                continue L_outer16;
                                            }
                                        } while(__dispatch31 != 0);
                                    }
                                    break;
                                }
                            }
                            _is = new ASTBase.StructInitializer(loc);
                            this.nextToken();
                            comma = 2;
                            for (; 1 != 0;){
                                switch ((this.token.value.value & 0xFF))
                                {
                                    case 120:
                                        if ((comma == 1))
                                        {
                                            this.error(new BytePtr("comma expected separating field initializers"));
                                        }
                                        t = this.peek(ptr(this.token));
                                        if ((((t.get()).value & 0xFF) == 7))
                                        {
                                            id = this.token.value.ident;
                                            this.nextToken();
                                            this.nextToken();
                                        }
                                        else
                                        {
                                            id = null;
                                        }
                                        value = this.parseInitializer();
                                        _is.addInit(id, value);
                                        comma = 1;
                                        continue;
                                    case 99:
                                        if ((comma == 2))
                                        {
                                            this.error(new BytePtr("expression expected, not `,`"));
                                        }
                                        this.nextToken();
                                        comma = 2;
                                        continue;
                                    case 6:
                                        this.nextToken();
                                        break;
                                    case 11:
                                        this.error(new BytePtr("found end of file instead of initializer"));
                                        break;
                                    default:
                                    if ((comma == 1))
                                    {
                                        this.error(new BytePtr("comma expected separating field initializers"));
                                    }
                                    value = this.parseInitializer();
                                    _is.addInit(null, value);
                                    comma = 1;
                                    continue;
                                }
                                break;
                            }
                            return _is;
                        case 3:
                            brackets = 1;
                            {
                                t = this.peek(ptr(this.token));
                            L_outer17:
                                for (; 1 != 0;t = this.peek(t)){
                                    {
                                        int __dispatch33 = 0;
                                        dispatched_33:
                                        do {
                                            switch (__dispatch33 != 0 ? __dispatch33 : ((t.get()).value & 0xFF))
                                            {
                                                case 3:
                                                    brackets++;
                                                    continue L_outer17;
                                                case 4:
                                                    if (((brackets -= 1) == 0))
                                                    {
                                                        t = this.peek(t);
                                                        if ((((t.get()).value & 0xFF) != 9) && (((t.get()).value & 0xFF) != 99) && (((t.get()).value & 0xFF) != 4) && (((t.get()).value & 0xFF) != 6))
                                                        {
                                                            /*goto Lexpression*/{ __dispatch30 = -1; continue dispatched_30; }
                                                        }
                                                        break;
                                                    }
                                                    continue L_outer17;
                                                case 11:
                                                    break;
                                                default:
                                                continue L_outer17;
                                            }
                                        } while(__dispatch33 != 0);
                                    }
                                    break;
                                }
                            }
                            ia = new ASTBase.ArrayInitializer(loc);
                            this.nextToken();
                            comma = 2;
                            for (; 1 != 0;){
                                switch ((this.token.value.value & 0xFF))
                                {
                                    default:
                                    if ((comma == 1))
                                    {
                                        this.error(new BytePtr("comma expected separating array initializers, not `%s`"), this.token.value.toChars());
                                        this.nextToken();
                                        break;
                                    }
                                    e = this.parseAssignExp();
                                    if (e == null)
                                    {
                                        break;
                                    }
                                    if (((this.token.value.value & 0xFF) == 7))
                                    {
                                        this.nextToken();
                                        value = this.parseInitializer();
                                    }
                                    else
                                    {
                                        value = new ASTBase.ExpInitializer(e.loc, e);
                                        e = null;
                                    }
                                    ia.addInit(e, value);
                                    comma = 1;
                                    continue;
                                    case 5:
                                    case 3:
                                        if ((comma == 1))
                                        {
                                            this.error(new BytePtr("comma expected separating array initializers, not `%s`"), this.token.value.toChars());
                                        }
                                        value = this.parseInitializer();
                                        if (((this.token.value.value & 0xFF) == 7))
                                        {
                                            this.nextToken();
                                            ASTBase.ExpInitializer expInit = value.isExpInitializer();
                                            assert(expInit != null);
                                            e = expInit.exp;
                                            value = this.parseInitializer();
                                        }
                                        else
                                        {
                                            e = null;
                                        }
                                        ia.addInit(e, value);
                                        comma = 1;
                                        continue;
                                    case 99:
                                        if ((comma == 2))
                                        {
                                            this.error(new BytePtr("expression expected, not `,`"));
                                        }
                                        this.nextToken();
                                        comma = 2;
                                        continue;
                                    case 4:
                                        this.nextToken();
                                        break;
                                    case 11:
                                        this.error(new BytePtr("found `%s` instead of array initializer"), this.token.value.toChars());
                                        break;
                                }
                                break;
                            }
                            return ia;
                        case 128:
                            t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 9) || (((t.get()).value & 0xFF) == 99))
                            {
                                this.nextToken();
                                return new ASTBase.VoidInitializer(loc);
                            }
                            /*goto Lexpression*/{ __dispatch30 = -1; continue dispatched_30; }
                        default:
                    /*Lexpression:*/
                    case -1:
                    __dispatch30 = 0;
                        e = this.parseAssignExp();
                        ie = new ASTBase.ExpInitializer(loc, e);
                        return ie;
                    }
                } while(__dispatch30 != 0);
            }
            return null;
        }

        public  ASTBase.Expression parseDefaultInitExp() {
            ASTBase.Expression e = null;
            Ptr<Token> t = this.peek(ptr(this.token));
            try {
                if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 2))
                {
                    {
                        int __dispatch35 = 0;
                        dispatched_35:
                        do {
                            switch (__dispatch35 != 0 ? __dispatch35 : (this.token.value.value & 0xFF))
                            {
                                case 219:
                                    e = new ASTBase.FileInitExp(this.token.value.loc, TOK.file);
                                    break;
                                case 220:
                                    e = new ASTBase.FileInitExp(this.token.value.loc, TOK.fileFullPath);
                                    break;
                                case 218:
                                    e = new ASTBase.LineInitExp(this.token.value.loc);
                                    break;
                                case 221:
                                    e = new ASTBase.ModuleInitExp(this.token.value.loc);
                                    break;
                                case 222:
                                    e = new ASTBase.FuncInitExp(this.token.value.loc);
                                    break;
                                case 223:
                                    e = new ASTBase.PrettyFuncInitExp(this.token.value.loc);
                                    break;
                                default:
                                /*goto LExp*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch35 != 0);
                    }
                    this.nextToken();
                    return e;
                }
            }
            catch(Dispatch0 __d){}
        /*LExp:*/
            return this.parseAssignExp();
        }

        public  void check(Loc loc, byte value) {
            if (((this.token.value.value & 0xFF) != (value & 0xFF)))
            {
                this.error(loc, new BytePtr("found `%s` when expecting `%s`"), this.token.value.toChars(), Token.toChars(value));
            }
            this.nextToken();
        }

        public  void check(byte value) {
            this.check(this.token.value.loc, value);
        }

        public  void check(byte value, BytePtr string) {
            if (((this.token.value.value & 0xFF) != (value & 0xFF)))
            {
                this.error(new BytePtr("found `%s` when expecting `%s` following %s"), this.token.value.toChars(), Token.toChars(value), string);
            }
            this.nextToken();
        }

        public  void checkParens(byte value, ASTBase.Expression e) {
            if ((precedence.get((e.op & 0xFF)) == PREC.rel) && (e.parens == 0))
            {
                this.error(e.loc, new BytePtr("`%s` must be surrounded by parentheses when next to operator `%s`"), e.toChars(), Token.toChars(value));
            }
        }


        public static class NeedDeclaratorId
        {
            public static final int no = 0;
            public static final int opt = 1;
            public static final int must = 2;
            public static final int mustIfDstyle = 3;
        }

        public  boolean isDeclaration(Ptr<Token> t, int needId, byte endtok, Ptr<Ptr<Token>> pt) {
            int haveId = 0;
            int haveTpl = 0;
            for (; 1 != 0;){
                if ((((t.get()).value & 0xFF) == 171) || (((t.get()).value & 0xFF) == 182) || (((t.get()).value & 0xFF) == 177) || (((t.get()).value & 0xFF) == 224) && (((this.peek(t).get()).value & 0xFF) != 1))
                {
                    t = this.peek(t);
                    continue;
                }
                break;
            }
            try {
                try {
                    if (!this.isBasicType(ptr(t)))
                    {
                        /*goto Lisnot*/throw Dispatch1.INSTANCE;
                    }
                    if (!this.isDeclarator(ptr(t), ptr(haveId), ptr(haveTpl), endtok, needId != NeedDeclaratorId.mustIfDstyle))
                    {
                        /*goto Lisnot*/throw Dispatch1.INSTANCE;
                    }
                    if ((needId == NeedDeclaratorId.no) && (haveId == 0) || (needId == NeedDeclaratorId.opt) || (needId == NeedDeclaratorId.must) && (haveId != 0) || (needId == NeedDeclaratorId.mustIfDstyle) && (haveId != 0))
                    {
                        if (pt != null)
                        {
                            pt.set(0, t);
                        }
                        /*goto Lis*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lisnot*/throw Dispatch1.INSTANCE;
                }
                catch(Dispatch0 __d){}
            /*Lis:*/
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lisnot:*/
            return false;
        }

        public  boolean isBasicType(Ptr<Ptr<Token>> pt) {
            Ptr<Token> t = pt.get();
            try {
                {
                    int __dispatch36 = 0;
                    dispatched_36:
                    do {
                        switch (__dispatch36 != 0 ? __dispatch36 : ((t.get()).value & 0xFF))
                        {
                            case 149:
                            case 150:
                            case 151:
                            case 148:
                            case 129:
                            case 130:
                            case 131:
                            case 132:
                            case 133:
                            case 134:
                            case 135:
                            case 136:
                            case 137:
                            case 138:
                            case 139:
                            case 140:
                            case 141:
                            case 142:
                            case 143:
                            case 144:
                            case 145:
                            case 146:
                            case 147:
                            case 128:
                                t = this.peek(t);
                                break;
                            case 120:
                            /*L5:*/
                            case -1:
                            __dispatch36 = 0;
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 91))
                                {
                                    /*goto L4*/throw Dispatch.INSTANCE;
                                }
                                /*goto L3*/{ __dispatch36 = -3; continue dispatched_36; }
                            L_outer18:
                                for (; 1 != 0;){
                                /*L2:*/
                                case -2:
                                __dispatch36 = 0;
                                    t = this.peek(t);
                                /*L3:*/
                                case -3:
                                __dispatch36 = 0;
                                    if ((((t.get()).value & 0xFF) == 97))
                                    {
                                    /*Ldot:*/
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) != 120))
                                        {
                                            /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                        }
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) != 91))
                                        {
                                            /*goto L3*/{ __dispatch36 = -3; continue dispatched_36; }
                                        }
                                    /*L4:*/
                                        t = this.peek(t);
                                        {
                                            int __dispatch37 = 0;
                                            dispatched_37:
                                            do {
                                                switch (__dispatch37 != 0 ? __dispatch37 : ((t.get()).value & 0xFF))
                                                {
                                                    case 120:
                                                        /*goto L5*/{ __dispatch36 = -1; continue dispatched_36; }
                                                    case 1:
                                                        if (!this.skipParens(t, ptr(t)))
                                                        {
                                                            /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                                        }
                                                        /*goto L3*/{ __dispatch36 = -3; continue dispatched_36; }
                                                    case 149:
                                                    case 150:
                                                    case 151:
                                                    case 148:
                                                    case 129:
                                                    case 130:
                                                    case 131:
                                                    case 132:
                                                    case 133:
                                                    case 134:
                                                    case 135:
                                                    case 136:
                                                    case 137:
                                                    case 138:
                                                    case 139:
                                                    case 140:
                                                    case 141:
                                                    case 142:
                                                    case 143:
                                                    case 144:
                                                    case 145:
                                                    case 146:
                                                    case 147:
                                                    case 128:
                                                    case 105:
                                                    case 106:
                                                    case 107:
                                                    case 108:
                                                    case 109:
                                                    case 110:
                                                    case 111:
                                                    case 112:
                                                    case 113:
                                                    case 114:
                                                    case 115:
                                                    case 116:
                                                    case 13:
                                                    case 15:
                                                    case 16:
                                                    case 117:
                                                    case 118:
                                                    case 119:
                                                    case 121:
                                                    case 122:
                                                    case 219:
                                                    case 220:
                                                    case 218:
                                                    case 221:
                                                    case 222:
                                                    case 223:
                                                        /*goto L2*/{ __dispatch36 = -2; continue dispatched_36; }
                                                    default:
                                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                                }
                                            } while(__dispatch37 != 0);
                                        }
                                    }
                                    break;
                                }
                                break;
                            case 97:
                                /*goto Ldot*/throw Dispatch.INSTANCE;
                            case 39:
                            case 229:
                                t = this.peek(t);
                                if (!this.skipParens(t, ptr(t)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                /*goto L3*/{ __dispatch36 = -3; continue dispatched_36; }
                            case 213:
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) != 1))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                Ptr<Token> lp = t;
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) != 120) || (!pequals((t.get()).ident, Id.getMember.value)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                if (!this.skipParens(lp, ptr(lp)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                if ((((lp.get()).value & 0xFF) != 120))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                break;
                            case 171:
                            case 182:
                            case 224:
                            case 177:
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) != 1))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                t = this.peek(t);
                                if (!this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightParentheses, ptr(t)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                t = this.peek(t);
                                break;
                            default:
                            /*goto Lfalse*/throw Dispatch0.INSTANCE;
                        }
                    } while(__dispatch36 != 0);
                }
                pt.set(0, t);
                return true;
            }
            catch(Dispatch0 __d){}
        /*Lfalse:*/
            return false;
        }

        public  boolean isDeclarator(Ptr<Ptr<Token>> pt, IntPtr haveId, IntPtr haveTpl, byte endtok, boolean allowAltSyntax) {
            Ptr<Token> t = pt.get();
            int parens = 0;
            if ((((t.get()).value & 0xFF) == 90))
            {
                return false;
            }
            for (; 1 != 0;){
                parens = 0;
                switch (((t.get()).value & 0xFF))
                {
                    case 78:
                        t = this.peek(t);
                        continue;
                    case 3:
                        t = this.peek(t);
                        if ((((t.get()).value & 0xFF) == 4))
                        {
                            t = this.peek(t);
                        }
                        else if (this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightBracket, ptr(t)))
                        {
                            t = this.peek(t);
                            if ((((t.get()).value & 0xFF) == 97) && (((this.peek(t).get()).value & 0xFF) == 120))
                            {
                                t = this.peek(t);
                                t = this.peek(t);
                            }
                        }
                        else
                        {
                            if (!this.isExpression(ptr(t)))
                            {
                                return false;
                            }
                            if ((((t.get()).value & 0xFF) == 31))
                            {
                                t = this.peek(t);
                                if (!this.isExpression(ptr(t)))
                                {
                                    return false;
                                }
                                if ((((t.get()).value & 0xFF) != 4))
                                {
                                    return false;
                                }
                                t = this.peek(t);
                            }
                            else
                            {
                                if ((((t.get()).value & 0xFF) != 4))
                                {
                                    return false;
                                }
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 97) && (((this.peek(t).get()).value & 0xFF) == 120))
                                {
                                    t = this.peek(t);
                                    t = this.peek(t);
                                }
                            }
                        }
                        continue;
                    case 120:
                        if (haveId.get() != 0)
                        {
                            return false;
                        }
                        haveId.set(0, 1);
                        t = this.peek(t);
                        break;
                    case 1:
                        if (!allowAltSyntax)
                        {
                            return false;
                        }
                        t = this.peek(t);
                        if ((((t.get()).value & 0xFF) == 2))
                        {
                            return false;
                        }
                        if ((((t.get()).value & 0xFF) == 120))
                        {
                            Ptr<Token> t2 = this.peek(t);
                            if ((((t2.get()).value & 0xFF) == 2))
                            {
                                return false;
                            }
                        }
                        if (!this.isDeclarator(ptr(t), haveId, null, TOK.rightParentheses, true))
                        {
                            return false;
                        }
                        t = this.peek(t);
                        parens = 1;
                        break;
                    case 160:
                    case 161:
                        t = this.peek(t);
                        if (!this.isParameters(ptr(t)))
                        {
                            return false;
                        }
                        this.skipAttributes(t, ptr(t));
                        continue;
                    default:
                    break;
                }
                break;
            }
        L_outer19:
            for (; 1 != 0;){
                {
                    int __dispatch39 = 0;
                    dispatched_39:
                    do {
                        switch (__dispatch39 != 0 ? __dispatch39 : ((t.get()).value & 0xFF))
                        {
                            case 3:
                                parens = 0;
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 4))
                                {
                                    t = this.peek(t);
                                }
                                else if (this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightBracket, ptr(t)))
                                {
                                    t = this.peek(t);
                                }
                                else
                                {
                                    if (!this.isExpression(ptr(t)))
                                    {
                                        return false;
                                    }
                                    if ((((t.get()).value & 0xFF) != 4))
                                    {
                                        return false;
                                    }
                                    t = this.peek(t);
                                }
                                continue L_outer19;
                            case 1:
                                parens = 0;
                                {
                                    Ptr<Token> tk = this.peekPastParen(t);
                                    if ((tk) != null)
                                    {
                                        if ((((tk.get()).value & 0xFF) == 1))
                                        {
                                            if (haveTpl == null)
                                            {
                                                return false;
                                            }
                                            haveTpl.set(0, 1);
                                            t = tk;
                                        }
                                        else if ((((tk.get()).value & 0xFF) == 90))
                                        {
                                            if (haveTpl == null)
                                            {
                                                return false;
                                            }
                                            haveTpl.set(0, 1);
                                            pt.set(0, tk);
                                            return true;
                                        }
                                    }
                                }
                                if (!this.isParameters(ptr(t)))
                                {
                                    return false;
                                }
                                for (; 1 != 0;){
                                    switch (((t.get()).value & 0xFF))
                                    {
                                        case 171:
                                        case 182:
                                        case 224:
                                        case 177:
                                        case 215:
                                        case 216:
                                        case 195:
                                        case 203:
                                            t = this.peek(t);
                                            continue;
                                        case 225:
                                            t = this.peek(t);
                                            t = this.peek(t);
                                            continue;
                                        default:
                                        break;
                                    }
                                    break;
                                }
                                continue L_outer19;
                            case 2:
                            case 4:
                            case 90:
                            case 99:
                            case 10:
                            case 9:
                            case 5:
                            case 175:
                            case 176:
                            case 187:
                                __dispatch39 = 0;
                                if ((parens == 0) && ((endtok & 0xFF) == 0) || ((endtok & 0xFF) == ((t.get()).value & 0xFF)))
                                {
                                    pt.set(0, t);
                                    return true;
                                }
                                return false;
                            case 120:
                                if ((pequals((t.get()).ident, Id._body)))
                                {
                                    /*goto case*/{ __dispatch39 = 187; continue dispatched_39; }
                                }
                                /*goto default*/ { __dispatch39 = -2; continue dispatched_39; }
                            case 183:
                                return haveTpl != null ? true : false;
                            default:
                            __dispatch39 = 0;
                            return false;
                        }
                    } while(__dispatch39 != 0);
                }
            }
            //throw new AssertionError("Unreachable code!");
        }

        // defaulted all parameters starting with #5
        public  boolean isDeclarator(Ptr<Ptr<Token>> pt, IntPtr haveId, IntPtr haveTpl, byte endtok) {
            return isDeclarator(pt, haveId, haveTpl, endtok, true);
        }

        public  boolean isParameters(Ptr<Ptr<Token>> pt) {
            Ptr<Token> t = pt.get();
            if ((((t.get()).value & 0xFF) != 1))
            {
                return false;
            }
            t = this.peek(t);
        L_outer20:
            for (; 1 != 0;t = this.peek(t)){
            /*L1:*/
                while (true) try {
                    int __dispatch41 = 0;
                    dispatched_41:
                    do {
                        switch (__dispatch41 != 0 ? __dispatch41 : ((t.get()).value & 0xFF))
                        {
                            case 2:
                                break;
                            case 10:
                                t = this.peek(t);
                                break;
                            case 175:
                            case 176:
                            case 210:
                            case 178:
                            case 203:
                            case 170:
                            case 179:
                            case 195:
                                continue L_outer20;
                            case 171:
                            case 182:
                            case 224:
                            case 177:
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 1))
                                {
                                    t = this.peek(t);
                                    if (!this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightParentheses, ptr(t)))
                                    {
                                        return false;
                                    }
                                    t = this.peek(t);
                                    /*goto L2*/{ __dispatch41 = -1; continue dispatched_41; }
                                }
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            default:

                                if (!this.isBasicType(ptr(t)))
                                {
                                    return false;
                                }
                            /*L2:*/
                            case -1:
                            __dispatch41 = 0;
                                int tmp = 0;
                                if ((((t.get()).value & 0xFF) != 10) && !this.isDeclarator(ptr(t), ptr(tmp), null, TOK.reserved, true))
                                {
                                    return false;
                                }
                                if ((((t.get()).value & 0xFF) == 90))
                                {
                                    t = this.peek(t);
                                    if (!this.isExpression(ptr(t)))
                                    {
                                        return false;
                                    }

                                if ((((t.get()).value & 0xFF) == 10))
                                {
                                    t = this.peek(t);
                                    break;
                                }
                            }
                            if ((((t.get()).value & 0xFF) == 99))
                            {
                                continue L_outer20;
                            }
                            break;
                        }
                    } while(__dispatch41 != 0);
                    break;
                } catch(Dispatch0 __d0) {}
                break;
            }
            if ((((t.get()).value & 0xFF) != 2))
            {
                return false;
            }
            t = this.peek(t);
            pt.set(0, t);
            return true;
        }

        public  boolean isExpression(Ptr<Ptr<Token>> pt) {
            Ptr<Token> t = pt.get();
            int brnest = 0;
            int panest = 0;
            int curlynest = 0;
            for (; ;t = this.peek(t)){
                switch (((t.get()).value & 0xFF))
                {
                    case 3:
                        brnest++;
                        continue;
                    case 4:
                        if (((brnest -= 1) >= 0))
                        {
                            continue;
                        }
                        break;
                    case 1:
                        panest++;
                        continue;
                    case 99:
                        if ((brnest != 0) || (panest != 0))
                        {
                            continue;
                        }
                        break;
                    case 2:
                        if (((panest -= 1) >= 0))
                        {
                            continue;
                        }
                        break;
                    case 5:
                        curlynest++;
                        continue;
                    case 6:
                        if (((curlynest -= 1) >= 0))
                        {
                            continue;
                        }
                        return false;
                    case 31:
                        if (brnest != 0)
                        {
                            continue;
                        }
                        break;
                    case 9:
                        if (curlynest != 0)
                        {
                            continue;
                        }
                        return false;
                    case 11:
                        return false;
                    default:
                    continue;
                }
                break;
            }
            pt.set(0, t);
            return true;
        }

        public  boolean skipParens(Ptr<Token> t, Ptr<Ptr<Token>> pt) {
            if ((((t.get()).value & 0xFF) != 1))
            {
                return false;
            }
            int parens = 0;
            try {
                try {
                L_outer21:
                    for (; 1 != 0;){
                        {
                            int __dispatch43 = 0;
                            dispatched_43:
                            do {
                                switch (__dispatch43 != 0 ? __dispatch43 : ((t.get()).value & 0xFF))
                                {
                                    case 1:
                                        parens++;
                                        break;
                                    case 2:
                                        parens--;
                                        if ((parens < 0))
                                        {
                                            /*goto Lfalse*/throw Dispatch1.INSTANCE;
                                        }
                                        if ((parens == 0))
                                        {
                                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                                        }
                                        break;
                                    case 11:
                                        /*goto Lfalse*/throw Dispatch1.INSTANCE;
                                    default:
                                    break;
                                }
                            } while(__dispatch43 != 0);
                        }
                        t = this.peek(t);
                    }
                }
                catch(Dispatch0 __d){}
            /*Ldone:*/
                if (pt != null)
                {
                    pt.set(0, this.peek(t));
                }
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lfalse:*/
            return false;
        }

        public  boolean skipParensIf(Ptr<Token> t, Ptr<Ptr<Token>> pt) {
            if ((((t.get()).value & 0xFF) != 1))
            {
                if (pt != null)
                {
                    pt.set(0, t);
                }
                return true;
            }
            return this.skipParens(t, pt);
        }

        public  boolean hasOptionalParensThen(Ptr<Token> t, byte expected) {
            Ptr<Token> tk = null;
            if (!this.skipParensIf(t, ptr(tk)))
            {
                return false;
            }
            return ((tk.get()).value & 0xFF) == (expected & 0xFF);
        }

        public  boolean skipAttributes(Ptr<Token> t, Ptr<Ptr<Token>> pt) {
            try {
                try {
                L_outer22:
                    for (; 1 != 0;){
                        {
                            int __dispatch44 = 0;
                            dispatched_44:
                            do {
                                switch (__dispatch44 != 0 ? __dispatch44 : ((t.get()).value & 0xFF))
                                {
                                    case 171:
                                    case 182:
                                    case 224:
                                    case 177:
                                    case 170:
                                    case 179:
                                    case 203:
                                    case 159:
                                    case 172:
                                    case 194:
                                        break;
                                    case 174:
                                        if ((((this.peek(t).get()).value & 0xFF) == 1))
                                        {
                                            t = this.peek(t);
                                            if (!this.skipParens(t, ptr(t)))
                                            {
                                                /*goto Lerror*/throw Dispatch1.INSTANCE;
                                            }
                                            continue L_outer22;
                                        }
                                        break;
                                    case 216:
                                    case 215:
                                    case 210:
                                    case 217:
                                    case 195:
                                        break;
                                    case 225:
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) == 120))
                                        {
                                            if ((pequals((t.get()).ident, Id.property)) || (pequals((t.get()).ident, Id.nogc)) || (pequals((t.get()).ident, Id.safe)) || (pequals((t.get()).ident, Id.trusted)) || (pequals((t.get()).ident, Id.system)) || (pequals((t.get()).ident, Id.disable)))
                                            {
                                                break;
                                            }
                                            t = this.peek(t);
                                            if ((((t.get()).value & 0xFF) == 91))
                                            {
                                                t = this.peek(t);
                                                if ((((t.get()).value & 0xFF) == 1))
                                                {
                                                    if (!this.skipParens(t, ptr(t)))
                                                    {
                                                        /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                    }
                                                }
                                                else
                                                {
                                                    if ((((t.get()).value & 0xFF) == 229))
                                                    {
                                                        t = this.peek(t);
                                                        if (!this.skipParens(t, ptr(t)))
                                                        {
                                                            /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                        }
                                                    }
                                                    else
                                                    {
                                                        t = this.peek(t);
                                                    }
                                                }
                                            }
                                            if ((((t.get()).value & 0xFF) == 1))
                                            {
                                                if (!this.skipParens(t, ptr(t)))
                                                {
                                                    /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                }
                                                continue L_outer22;
                                            }
                                            continue L_outer22;
                                        }
                                        if ((((t.get()).value & 0xFF) == 1))
                                        {
                                            if (!this.skipParens(t, ptr(t)))
                                            {
                                                /*goto Lerror*/throw Dispatch1.INSTANCE;
                                            }
                                            continue L_outer22;
                                        }
                                        /*goto Lerror*/throw Dispatch1.INSTANCE;
                                    default:
                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                }
                            } while(__dispatch44 != 0);
                        }
                        t = this.peek(t);
                    }
                }
                catch(Dispatch0 __d){}
            /*Ldone:*/
                if (pt != null)
                {
                    pt.set(0, t);
                }
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lerror:*/
            return false;
        }

        public  ASTBase.Expression parseExpression() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseAssignExp();
            for (; ((this.token.value.value & 0xFF) == 99);){
                this.nextToken();
                ASTBase.Expression e2 = this.parseAssignExp();
                e = new ASTBase.CommaExp(loc, e, e2, false);
                loc = this.token.value.loc.copy();
            }
            return e;
        }

        public  ASTBase.Expression parsePrimaryExp() {
            ASTBase.Expression e = null;
            ASTBase.Type t = null;
            Identifier id = null;
            Loc loc = this.token.value.loc.copy();
            {
                int __dispatch45 = 0;
                dispatched_45:
                do {
                    switch (__dispatch45 != 0 ? __dispatch45 : (this.token.value.value & 0xFF))
                    {
                        case 120:
                            Ptr<Token> t1 = this.peek(ptr(this.token));
                            Ptr<Token> t2 = this.peek(t1);
                            if ((((t1.get()).value & 0xFF) == 75) && (((t2.get()).value & 0xFF) == 55))
                            {
                                this.nextToken();
                                this.nextToken();
                                this.nextToken();
                                this.error(new BytePtr("use `.` for member lookup, not `->`"));
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            if (((this.peekNext() & 0xFF) == 228))
                            {
                                /*goto case_delegate*/{ __dispatch45 = -2; continue dispatched_45; }
                            }
                            id = this.token.value.ident;
                            this.nextToken();
                            byte save = TOK.reserved;
                            if (((this.token.value.value & 0xFF) == 91) && (((save = this.peekNext()) & 0xFF) != 63) && ((save & 0xFF) != 175))
                            {
                                ASTBase.TemplateInstance tempinst = new ASTBase.TemplateInstance(loc, id, this.parseTemplateArguments());
                                e = new ASTBase.ScopeExp(loc, tempinst);
                            }
                            else
                            {
                                e = new ASTBase.IdentifierExp(loc, id);
                            }
                            break;
                        case 35:
                            if (this.inBrackets == 0)
                            {
                                this.error(new BytePtr("`$` is valid only inside [] of index or slice"));
                            }
                            e = new ASTBase.DollarExp(loc);
                            this.nextToken();
                            break;
                        case 97:
                            e = new ASTBase.IdentifierExp(loc, Id.empty.value);
                            break;
                        case 123:
                            e = new ASTBase.ThisExp(loc);
                            this.nextToken();
                            break;
                        case 124:
                            e = new ASTBase.SuperExp(loc);
                            this.nextToken();
                            break;
                        case 105:
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.value.intvalue, ASTBase.Type.tint32);
                            this.nextToken();
                            break;
                        case 106:
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.value.intvalue, ASTBase.Type.tuns32);
                            this.nextToken();
                            break;
                        case 107:
                            e = new ASTBase.IntegerExp(loc, (long)this.token.value.intvalue, ASTBase.Type.tint64);
                            this.nextToken();
                            break;
                        case 108:
                            e = new ASTBase.IntegerExp(loc, this.token.value.intvalue, ASTBase.Type.tuns64);
                            this.nextToken();
                            break;
                        case 111:
                            e = new ASTBase.RealExp(loc, this.token.value.floatvalue, ASTBase.Type.tfloat32);
                            this.nextToken();
                            break;
                        case 112:
                            e = new ASTBase.RealExp(loc, this.token.value.floatvalue, ASTBase.Type.tfloat64);
                            this.nextToken();
                            break;
                        case 113:
                            e = new ASTBase.RealExp(loc, this.token.value.floatvalue, ASTBase.Type.tfloat80);
                            this.nextToken();
                            break;
                        case 114:
                            e = new ASTBase.RealExp(loc, this.token.value.floatvalue, ASTBase.Type.timaginary32);
                            this.nextToken();
                            break;
                        case 115:
                            e = new ASTBase.RealExp(loc, this.token.value.floatvalue, ASTBase.Type.timaginary64);
                            this.nextToken();
                            break;
                        case 116:
                            e = new ASTBase.RealExp(loc, this.token.value.floatvalue, ASTBase.Type.timaginary80);
                            this.nextToken();
                            break;
                        case 13:
                            e = new ASTBase.NullExp(loc, null);
                            this.nextToken();
                            break;
                        case 219:
                            BytePtr s = pcopy(loc.filename != null ? loc.filename : this.mod.ident.toChars());
                            e = new ASTBase.StringExp(loc, s);
                            this.nextToken();
                            break;
                        case 220:
                            assertMsg(loc.isValid(), new ByteSlice("__FILE_FULL_PATH__ does not work with an invalid location"));
                            e = new ASTBase.StringExp(loc, FileName.toAbsolute(loc.filename, null));
                            this.nextToken();
                            break;
                        case 218:
                            e = new ASTBase.IntegerExp(loc, (long)loc.linnum, ASTBase.Type.tint32);
                            this.nextToken();
                            break;
                        case 221:
                            BytePtr s_1 = pcopy(this.md != null ? (this.md.get()).toChars() : this.mod.toChars());
                            e = new ASTBase.StringExp(loc, s_1);
                            this.nextToken();
                            break;
                        case 222:
                            e = new ASTBase.FuncInitExp(loc);
                            this.nextToken();
                            break;
                        case 223:
                            e = new ASTBase.PrettyFuncInitExp(loc);
                            this.nextToken();
                            break;
                        case 15:
                            e = new ASTBase.IntegerExp(loc, 1L, ASTBase.Type.tbool);
                            this.nextToken();
                            break;
                        case 16:
                            e = new ASTBase.IntegerExp(loc, 0L, ASTBase.Type.tbool);
                            this.nextToken();
                            break;
                        case 117:
                            e = new ASTBase.IntegerExp(loc, (long)(byte)this.token.value.intvalue, ASTBase.Type.tchar);
                            this.nextToken();
                            break;
                        case 118:
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.value.intvalue, ASTBase.Type.twchar);
                            this.nextToken();
                            break;
                        case 119:
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.value.intvalue, ASTBase.Type.tdchar);
                            this.nextToken();
                            break;
                        case 121:
                        case 122:
                            BytePtr s_2 = pcopy(this.token.value.ustring);
                            int len = this.token.value.len;
                            byte postfix = this.token.value.postfix;
                            for (; 1 != 0;){
                                Token prev = this.token.value.copy();
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 121) || ((this.token.value.value & 0xFF) == 122))
                                {
                                    if (this.token.value.postfix != 0)
                                    {
                                        if (((this.token.value.postfix & 0xFF) != (postfix & 0xFF)))
                                        {
                                            this.error(new BytePtr("mismatched string literal postfixes `'%c'` and `'%c'`"), postfix, this.token.value.postfix);
                                        }
                                        postfix = this.token.value.postfix;
                                    }
                                    this.error(new BytePtr("Implicit string concatenation is deprecated, use %s ~ %s instead"), prev.toChars(), this.token.value.toChars());
                                    int len1 = len;
                                    int len2 = this.token.value.len;
                                    len = len1 + len2;
                                    BytePtr s2 = pcopy(((BytePtr)Mem.xmalloc(len)));
                                    memcpy((BytePtr)(s2), (s_2), (len1));
                                    memcpy((BytePtr)((s2.plus(len1))), (this.token.value.ustring), (len2));
                                    s_2 = pcopy(s2);
                                }
                                else
                                {
                                    break;
                                }
                            }
                            e = new ASTBase.StringExp(loc, s_2, len, (byte)postfix);
                            break;
                        case 128:
                            t = ASTBase.Type.tvoid;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 129:
                            t = ASTBase.Type.tint8;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 130:
                            t = ASTBase.Type.tuns8;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 131:
                            t = ASTBase.Type.tint16;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 132:
                            t = ASTBase.Type.tuns16;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 133:
                            t = ASTBase.Type.tint32;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 134:
                            t = ASTBase.Type.tuns32;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 135:
                            t = ASTBase.Type.tint64;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 136:
                            t = ASTBase.Type.tuns64;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 137:
                            t = ASTBase.Type.tint128;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 138:
                            t = ASTBase.Type.tuns128;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 139:
                            t = ASTBase.Type.tfloat32;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 140:
                            t = ASTBase.Type.tfloat64;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 141:
                            t = ASTBase.Type.tfloat80;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 142:
                            t = ASTBase.Type.timaginary32;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 143:
                            t = ASTBase.Type.timaginary64;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 144:
                            t = ASTBase.Type.timaginary80;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 145:
                            t = ASTBase.Type.tcomplex32;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 146:
                            t = ASTBase.Type.tcomplex64;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 147:
                            t = ASTBase.Type.tcomplex80;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 151:
                            t = ASTBase.Type.tbool;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 148:
                            t = ASTBase.Type.tchar;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 149:
                            t = ASTBase.Type.twchar;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        case 150:
                            t = ASTBase.Type.tdchar;
                            /*goto LabelX*/{ __dispatch45 = -3; continue dispatched_45; }
                        /*LabelX:*/
                        case -3:
                        __dispatch45 = 0;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                e = new ASTBase.TypeExp(loc, t);
                                e = new ASTBase.CallExp(loc, e, this.parseArguments());
                                break;
                            }
                            this.check(TOK.dot, t.toChars());
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("found `%s` when expecting identifier following `%s`."), this.token.value.toChars(), t.toChars());
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            e = new ASTBase.DotIdExp(loc, new ASTBase.TypeExp(loc, t), this.token.value.ident);
                            this.nextToken();
                            break;
                        case 39:
                            t = this.parseTypeof();
                            e = new ASTBase.TypeExp(loc, t);
                            break;
                        case 229:
                            t = this.parseVector();
                            e = new ASTBase.TypeExp(loc, t);
                            break;
                        case 42:
                            this.nextToken();
                            this.check(TOK.leftParentheses, new BytePtr("`typeid`"));
                            RootObject o = null;
                            if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                            {
                                o = this.parseType(null, null);
                            }
                            else
                            {
                                o = this.parseAssignExp();
                            }
                            this.check(TOK.rightParentheses);
                            e = new ASTBase.TypeidExp(loc, o);
                            break;
                        case 213:
                            Identifier ident = null;
                            Ptr<DArray<RootObject>> args = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("`__traits(identifier, args...)` expected"));
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            ident = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 99))
                            {
                                args = this.parseTemplateArgumentList();
                            }
                            else
                            {
                                this.check(TOK.rightParentheses);
                            }
                            e = new ASTBase.TraitsExp(loc, ident, args);
                            break;
                        case 63:
                            ASTBase.Type targ = null;
                            Identifier ident_1 = null;
                            ASTBase.Type tspec = null;
                            byte tok = TOK.reserved;
                            byte tok2 = TOK.reserved;
                            Ptr<DArray<ASTBase.TemplateParameter>> tpl = null;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 120) && ((this.peekNext() & 0xFF) == 1))
                                {
                                    this.error(loc, new BytePtr("unexpected `(` after `%s`, inside `is` expression. Try enclosing the contents of `is` with a `typeof` expression"), this.token.value.toChars());
                                    this.nextToken();
                                    Ptr<Token> tempTok = this.peekPastParen(ptr(this.token));
                                    (ptr(this.token)).set(0, (tempTok));
                                    /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                                }
                                targ = this.parseType(ptr(ident_1), null);
                                if (((this.token.value.value & 0xFF) == 7) || ((this.token.value.value & 0xFF) == 58))
                                {
                                    tok = this.token.value.value;
                                    this.nextToken();
                                    if (((tok & 0xFF) == 58) && ((this.token.value.value & 0xFF) == 152) || ((this.token.value.value & 0xFF) == 155) || ((this.token.value.value & 0xFF) == 153) || ((this.token.value.value & 0xFF) == 124) || ((this.token.value.value & 0xFF) == 156) || ((this.token.value.value & 0xFF) == 154) || ((this.token.value.value & 0xFF) == 180) || ((this.token.value.value & 0xFF) == 34) || ((this.token.value.value & 0xFF) == 209) || ((this.token.value.value & 0xFF) == 212) || ((this.token.value.value & 0xFF) == 171) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 182) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 224) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 177) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 161) || ((this.token.value.value & 0xFF) == 160) || ((this.token.value.value & 0xFF) == 195) || ((this.token.value.value & 0xFF) == 229) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2))
                                    {
                                        tok2 = this.token.value.value;
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        tspec = this.parseType(null, null);
                                    }
                                }
                                if (tspec != null)
                                {
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        tpl = this.parseTemplateParameterList(1);
                                    }
                                    else
                                    {
                                        tpl = refPtr(new DArray<ASTBase.TemplateParameter>());
                                        this.check(TOK.rightParentheses);
                                    }
                                }
                                else
                                {
                                    this.check(TOK.rightParentheses);
                                }
                            }
                            else
                            {
                                this.error(new BytePtr("`type identifier : specialization` expected following `is`"));
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            e = new ASTBase.IsExp(loc, targ, ident_1, tok, tspec, tok2, tpl);
                            break;
                        case 14:
                            ASTBase.Expression msg = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses, new BytePtr("`assert`"));
                            e = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 99))
                            {
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) != 2))
                                {
                                    msg = this.parseAssignExp();
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        this.nextToken();
                                    }
                                }
                            }
                            this.check(TOK.rightParentheses);
                            e = new ASTBase.AssertExp(loc, e, msg);
                            break;
                        case 162:
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) != 1))
                            {
                                this.error(new BytePtr("found `%s` when expecting `%s` following %s"), this.token.value.toChars(), Token.toChars(TOK.leftParentheses), new BytePtr("`mixin`"));
                            }
                            Ptr<DArray<ASTBase.Expression>> exps = this.parseArguments();
                            e = new ASTBase.CompileExp(loc, exps);
                            break;
                        case 157:
                            this.nextToken();
                            this.check(TOK.leftParentheses, new BytePtr("`import`"));
                            e = this.parseAssignExp();
                            this.check(TOK.rightParentheses);
                            e = new ASTBase.ImportExp(loc, e);
                            break;
                        case 22:
                            e = this.parseNewExp(null);
                            break;
                        case 210:
                            if (((this.peekNext() & 0xFF) == 1))
                            {
                                Ptr<Token> tk = this.peekPastParen(this.peek(ptr(this.token)));
                                if (this.skipAttributes(tk, ptr(tk)) && (((tk.get()).value & 0xFF) == 228) || (((tk.get()).value & 0xFF) == 5))
                                {
                                    /*goto case_delegate*/{ __dispatch45 = -2; continue dispatched_45; }
                                }
                            }
                            this.nextToken();
                            this.error(new BytePtr("found `%s` when expecting function literal following `ref`"), this.token.value.toChars());
                            /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                        case 1:
                            Ptr<Token> tk_1 = this.peekPastParen(ptr(this.token));
                            if (this.skipAttributes(tk_1, ptr(tk_1)) && (((tk_1.get()).value & 0xFF) == 228) || (((tk_1.get()).value & 0xFF) == 5))
                            {
                                /*goto case_delegate*/{ __dispatch45 = -2; continue dispatched_45; }
                            }
                            this.nextToken();
                            e = this.parseExpression();
                            e.parens = (byte)1;
                            this.check(loc, TOK.rightParentheses);
                            break;
                        case 3:
                            Ptr<DArray<ASTBase.Expression>> values = refPtr(new DArray<ASTBase.Expression>());
                            Ptr<DArray<ASTBase.Expression>> keys = null;
                            this.nextToken();
                            for (; ((this.token.value.value & 0xFF) != 4) && ((this.token.value.value & 0xFF) != 11);){
                                e = this.parseAssignExp();
                                if (((this.token.value.value & 0xFF) == 7) && (keys != null) || ((values.get()).length == 0))
                                {
                                    this.nextToken();
                                    if (keys == null)
                                    {
                                        keys = refPtr(new DArray<ASTBase.Expression>());
                                    }
                                    (keys.get()).push(e);
                                    e = this.parseAssignExp();
                                }
                                else if (keys != null)
                                {
                                    this.error(new BytePtr("`key:value` expected for associative array literal"));
                                    keys = null;
                                }
                                (values.get()).push(e);
                                if (((this.token.value.value & 0xFF) == 4))
                                {
                                    break;
                                }
                                this.check(TOK.comma);
                            }
                            this.check(loc, TOK.rightBracket);
                            if (keys != null)
                            {
                                e = new ASTBase.AssocArrayLiteralExp(loc, keys, values);
                            }
                            else
                            {
                                e = new ASTBase.ArrayLiteralExp(loc, null, values);
                            }
                            break;
                        case 5:
                        case 161:
                        case 160:
                        /*case_delegate:*/
                        case -2:
                        __dispatch45 = 0;
                            {
                                ASTBase.Dsymbol s_3 = this.parseFunctionLiteral();
                                e = new ASTBase.FuncExp(loc, s_3);
                                break;
                            }
                        default:
                        this.error(new BytePtr("expression expected, not `%s`"), this.token.value.toChars());
                    /*Lerr:*/
                    case -1:
                    __dispatch45 = 0;
                        e = new ASTBase.IntegerExp(loc, 0L, ASTBase.Type.tint32);
                        this.nextToken();
                        break;
                    }
                } while(__dispatch45 != 0);
            }
            return e;
        }

        public  ASTBase.Expression parseUnaryExp() {
            ASTBase.Expression e = null;
            Loc loc = this.token.value.loc.copy();
            switch ((this.token.value.value & 0xFF))
            {
                case 84:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.AddrExp(loc, e);
                    break;
                case 93:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.PreExp(TOK.prePlusPlus, loc, e);
                    break;
                case 94:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.PreExp(TOK.preMinusMinus, loc, e);
                    break;
                case 78:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.PtrExp(loc, e);
                    break;
                case 75:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.NegExp(loc, e);
                    break;
                case 74:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.UAddExp(loc, e);
                    break;
                case 91:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.NotExp(loc, e);
                    break;
                case 92:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.ComExp(loc, e);
                    break;
                case 23:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ASTBase.DeleteExp(loc, e, false);
                    break;
                case 12:
                    this.nextToken();
                    this.check(TOK.leftParentheses);
                    byte m = (byte)0;
                    for (; 1 != 0;){
                        switch ((this.token.value.value & 0xFF))
                        {
                            case 171:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= ASTBase.MODFlags.const_;
                                this.nextToken();
                                continue;
                            case 182:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= ASTBase.MODFlags.immutable_;
                                this.nextToken();
                                continue;
                            case 224:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= ASTBase.MODFlags.shared_;
                                this.nextToken();
                                continue;
                            case 177:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= ASTBase.MODFlags.wild;
                                this.nextToken();
                                continue;
                            default:
                            break;
                        }
                        break;
                    }
                    if (((this.token.value.value & 0xFF) == 2))
                    {
                        this.nextToken();
                        e = this.parseUnaryExp();
                        e = new ASTBase.CastExp(loc, e, m);
                    }
                    else
                    {
                        ASTBase.Type t = this.parseType(null, null);
                        t = t.addMod(m);
                        this.check(TOK.rightParentheses);
                        e = this.parseUnaryExp();
                        e = new ASTBase.CastExp(loc, e, t);
                    }
                    break;
                case 177:
                case 224:
                case 171:
                case 182:
                    long stc = this.parseTypeCtor();
                    ASTBase.Type t_1 = this.parseBasicType(false);
                    t_1 = t_1.addSTC(stc);
                    if ((stc == 0L) && ((this.token.value.value & 0xFF) == 97))
                    {
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 120))
                        {
                            this.error(new BytePtr("identifier expected following `(type)`."));
                            return null;
                        }
                        e = new ASTBase.DotIdExp(loc, new ASTBase.TypeExp(loc, t_1), this.token.value.ident);
                        this.nextToken();
                        e = this.parsePostExp(e);
                    }
                    else
                    {
                        e = new ASTBase.TypeExp(loc, t_1);
                        if (((this.token.value.value & 0xFF) != 1))
                        {
                            this.error(new BytePtr("`(arguments)` expected following `%s`"), t_1.toChars());
                            return e;
                        }
                        e = new ASTBase.CallExp(loc, e, this.parseArguments());
                    }
                    break;
                case 1:
                    Ptr<Token> tk = this.peek(ptr(this.token));
                    if (this.isDeclaration(tk, NeedDeclaratorId.no, TOK.rightParentheses, ptr(tk)))
                    {
                        tk = this.peek(tk);
                        switch (((tk.get()).value & 0xFF))
                        {
                            case 91:
                                tk = this.peek(tk);
                                if ((((tk.get()).value & 0xFF) == 63) || (((tk.get()).value & 0xFF) == 175))
                                {
                                    break;
                                }
                            case 97:
                            case 93:
                            case 94:
                            case 23:
                            case 22:
                            case 1:
                            case 120:
                            case 123:
                            case 124:
                            case 105:
                            case 106:
                            case 107:
                            case 108:
                            case 109:
                            case 110:
                            case 111:
                            case 112:
                            case 113:
                            case 114:
                            case 115:
                            case 116:
                            case 13:
                            case 15:
                            case 16:
                            case 117:
                            case 118:
                            case 119:
                            case 121:
                            case 161:
                            case 160:
                            case 39:
                            case 213:
                            case 229:
                            case 219:
                            case 220:
                            case 218:
                            case 221:
                            case 222:
                            case 223:
                            case 149:
                            case 150:
                            case 151:
                            case 148:
                            case 129:
                            case 130:
                            case 131:
                            case 132:
                            case 133:
                            case 134:
                            case 135:
                            case 136:
                            case 137:
                            case 138:
                            case 139:
                            case 140:
                            case 141:
                            case 142:
                            case 143:
                            case 144:
                            case 145:
                            case 146:
                            case 147:
                            case 128:
                                this.nextToken();
                                ASTBase.Type t_2 = this.parseType(null, null);
                                this.check(TOK.rightParentheses);
                                if (((this.token.value.value & 0xFF) == 97))
                                {
                                    if (((this.peekNext() & 0xFF) != 120) && ((this.peekNext() & 0xFF) != 22))
                                    {
                                        this.error(new BytePtr("identifier or new keyword expected following `(...)`."));
                                        return null;
                                    }
                                    e = new ASTBase.TypeExp(loc, t_2);
                                    e = this.parsePostExp(e);
                                }
                                else
                                {
                                    e = this.parseUnaryExp();
                                    e = new ASTBase.CastExp(loc, e, t_2);
                                    this.error(new BytePtr("C style cast illegal, use `%s`"), e.toChars());
                                }
                                return e;
                            default:
                            break;
                        }
                    }
                    e = this.parsePrimaryExp();
                    e = this.parsePostExp(e);
                    break;
                default:
                e = this.parsePrimaryExp();
                e = this.parsePostExp(e);
                break;
            }
            assert(e != null);
            for (; ((this.token.value.value & 0xFF) == 226);){
                this.nextToken();
                ASTBase.Expression e2 = this.parseUnaryExp();
                e = new ASTBase.PowExp(loc, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parsePostExp(ASTBase.Expression e) {
            for (; 1 != 0;){
                Loc loc = this.token.value.loc.copy();
                switch ((this.token.value.value & 0xFF))
                {
                    case 97:
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Identifier id = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 91) && ((this.peekNext() & 0xFF) != 63) && ((this.peekNext() & 0xFF) != 175))
                            {
                                Ptr<DArray<RootObject>> tiargs = this.parseTemplateArguments();
                                e = new ASTBase.DotTemplateInstanceExp(loc, e, id, tiargs);
                            }
                            else
                            {
                                e = new ASTBase.DotIdExp(loc, e, id);
                            }
                            continue;
                        }
                        if (((this.token.value.value & 0xFF) == 22))
                        {
                            e = this.parseNewExp(e);
                            continue;
                        }
                        this.error(new BytePtr("identifier or `new` expected following `.`, not `%s`"), this.token.value.toChars());
                        break;
                    case 93:
                        e = new ASTBase.PostExp(TOK.plusPlus, loc, e);
                        break;
                    case 94:
                        e = new ASTBase.PostExp(TOK.minusMinus, loc, e);
                        break;
                    case 1:
                        e = new ASTBase.CallExp(loc, e, this.parseArguments());
                        continue;
                    case 3:
                        ASTBase.Expression index = null;
                        ASTBase.Expression upr = null;
                        Ptr<DArray<ASTBase.Expression>> arguments = refPtr(new DArray<ASTBase.Expression>());
                        this.inBrackets++;
                        this.nextToken();
                        for (; ((this.token.value.value & 0xFF) != 4) && ((this.token.value.value & 0xFF) != 11);){
                            index = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 31))
                            {
                                this.nextToken();
                                upr = this.parseAssignExp();
                                (arguments.get()).push(new ASTBase.IntervalExp(loc, index, upr));
                            }
                            else
                            {
                                (arguments.get()).push(index);
                            }
                            if (((this.token.value.value & 0xFF) == 4))
                            {
                                break;
                            }
                            this.check(TOK.comma);
                        }
                        this.check(TOK.rightBracket);
                        this.inBrackets--;
                        e = new ASTBase.ArrayExp(loc, e, arguments);
                        continue;
                    default:
                    return e;
                }
                this.nextToken();
            }
        }

        public  ASTBase.Expression parseMulExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseUnaryExp();
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 78:
                        this.nextToken();
                        ASTBase.Expression e2 = this.parseUnaryExp();
                        e = new ASTBase.MulExp(loc, e, e2);
                        continue;
                    case 79:
                        this.nextToken();
                        ASTBase.Expression e2_1 = this.parseUnaryExp();
                        e = new ASTBase.DivExp(loc, e, e2_1);
                        continue;
                    case 80:
                        this.nextToken();
                        ASTBase.Expression e2_2 = this.parseUnaryExp();
                        e = new ASTBase.ModExp(loc, e, e2_2);
                        continue;
                    default:
                    break;
                }
                break;
            }
            return e;
        }

        public  ASTBase.Expression parseAddExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseMulExp();
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 74:
                        this.nextToken();
                        ASTBase.Expression e2 = this.parseMulExp();
                        e = new ASTBase.AddExp(loc, e, e2);
                        continue;
                    case 75:
                        this.nextToken();
                        ASTBase.Expression e2_1 = this.parseMulExp();
                        e = new ASTBase.MinExp(loc, e, e2_1);
                        continue;
                    case 92:
                        this.nextToken();
                        ASTBase.Expression e2_2 = this.parseMulExp();
                        e = new ASTBase.CatExp(loc, e, e2_2);
                        continue;
                    default:
                    break;
                }
                break;
            }
            return e;
        }

        public  ASTBase.Expression parseShiftExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseAddExp();
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 64:
                        this.nextToken();
                        ASTBase.Expression e2 = this.parseAddExp();
                        e = new ASTBase.ShlExp(loc, e, e2);
                        continue;
                    case 65:
                        this.nextToken();
                        ASTBase.Expression e2_1 = this.parseAddExp();
                        e = new ASTBase.ShrExp(loc, e, e2_1);
                        continue;
                    case 68:
                        this.nextToken();
                        ASTBase.Expression e2_2 = this.parseAddExp();
                        e = new ASTBase.UshrExp(loc, e, e2_2);
                        continue;
                    default:
                    break;
                }
                break;
            }
            return e;
        }

        public  ASTBase.Expression parseCmpExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseShiftExp();
            byte op = this.token.value.value;
            {
                int __dispatch53 = 0;
                dispatched_53:
                do {
                    switch (__dispatch53 != 0 ? __dispatch53 : (op & 0xFF))
                    {
                        case 58:
                        case 59:
                            this.nextToken();
                            ASTBase.Expression e2 = this.parseShiftExp();
                            e = new ASTBase.EqualExp(op, loc, e, e2);
                            break;
                        case 63:
                            op = TOK.identity;
                            /*goto L1*/{ __dispatch53 = -1; continue dispatched_53; }
                        case 91:
                            {
                                Ptr<Token> t = this.peek(ptr(this.token));
                                if ((((t.get()).value & 0xFF) == 175))
                                {
                                    this.nextToken();
                                    this.nextToken();
                                    ASTBase.Expression e2_3 = this.parseShiftExp();
                                    e = new ASTBase.InExp(loc, e, e2_3);
                                    e = new ASTBase.NotExp(loc, e);
                                    break;
                                }
                                if ((((t.get()).value & 0xFF) != 63))
                                {
                                    break;
                                }
                                this.nextToken();
                                op = TOK.notIdentity;
                                /*goto L1*/{ __dispatch53 = -1; continue dispatched_53; }
                            }
                        /*L1:*/
                        case -1:
                        __dispatch53 = 0;
                            this.nextToken();
                            ASTBase.Expression e2_1 = this.parseShiftExp();
                            e = new ASTBase.IdentityExp(op, loc, e, e2_1);
                            break;
                        case 54:
                        case 56:
                        case 55:
                        case 57:
                            this.nextToken();
                            ASTBase.Expression e2_2 = this.parseShiftExp();
                            e = new ASTBase.CmpExp(op, loc, e, e2_2);
                            break;
                        case 175:
                            this.nextToken();
                            ASTBase.Expression e2_4 = this.parseShiftExp();
                            e = new ASTBase.InExp(loc, e, e2_4);
                            break;
                        default:
                        break;
                    }
                } while(__dispatch53 != 0);
            }
            return e;
        }

        public  ASTBase.Expression parseAndExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseCmpExp();
            for (; ((this.token.value.value & 0xFF) == 84);){
                this.checkParens(TOK.and, e);
                this.nextToken();
                ASTBase.Expression e2 = this.parseCmpExp();
                this.checkParens(TOK.and, e2);
                e = new ASTBase.AndExp(loc, e, e2);
                loc = this.token.value.loc.copy();
            }
            return e;
        }

        public  ASTBase.Expression parseXorExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseAndExp();
            for (; ((this.token.value.value & 0xFF) == 86);){
                this.checkParens(TOK.xor, e);
                this.nextToken();
                ASTBase.Expression e2 = this.parseAndExp();
                this.checkParens(TOK.xor, e2);
                e = new ASTBase.XorExp(loc, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseOrExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseXorExp();
            for (; ((this.token.value.value & 0xFF) == 85);){
                this.checkParens(TOK.or, e);
                this.nextToken();
                ASTBase.Expression e2 = this.parseXorExp();
                this.checkParens(TOK.or, e2);
                e = new ASTBase.OrExp(loc, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseAndAndExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseOrExp();
            for (; ((this.token.value.value & 0xFF) == 101);){
                this.nextToken();
                ASTBase.Expression e2 = this.parseOrExp();
                e = new ASTBase.LogicalExp(loc, TOK.andAnd, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseOrOrExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseAndAndExp();
            for (; ((this.token.value.value & 0xFF) == 102);){
                this.nextToken();
                ASTBase.Expression e2 = this.parseAndAndExp();
                e = new ASTBase.LogicalExp(loc, TOK.orOr, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseCondExp() {
            Loc loc = this.token.value.loc.copy();
            ASTBase.Expression e = this.parseOrOrExp();
            if (((this.token.value.value & 0xFF) == 100))
            {
                this.nextToken();
                ASTBase.Expression e1 = this.parseExpression();
                this.check(TOK.colon);
                ASTBase.Expression e2 = this.parseCondExp();
                e = new ASTBase.CondExp(loc, e, e1, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseAssignExp() {
            ASTBase.Expression e = null;
            e = this.parseCondExp();
            if ((e == null))
            {
                return e;
            }
            if (((e.op & 0xFF) == 100) && (e.parens == 0) && (precedence.get((this.token.value.value & 0xFF)) == PREC.assign))
            {
                deprecation(e.loc, new BytePtr("`%s` must be surrounded by parentheses when next to operator `%s`"), e.toChars(), Token.toChars(this.token.value.value));
            }
            Loc loc = this.token.value.loc.copy();
            switch ((this.token.value.value & 0xFF))
            {
                case 90:
                    this.nextToken();
                    ASTBase.Expression e2 = this.parseAssignExp();
                    e = new ASTBase.AssignExp(loc, e, e2);
                    break;
                case 76:
                    this.nextToken();
                    ASTBase.Expression e2_1 = this.parseAssignExp();
                    e = new ASTBase.AddAssignExp(loc, e, e2_1);
                    break;
                case 77:
                    this.nextToken();
                    ASTBase.Expression e2_2 = this.parseAssignExp();
                    e = new ASTBase.MinAssignExp(loc, e, e2_2);
                    break;
                case 81:
                    this.nextToken();
                    ASTBase.Expression e2_3 = this.parseAssignExp();
                    e = new ASTBase.MulAssignExp(loc, e, e2_3);
                    break;
                case 82:
                    this.nextToken();
                    ASTBase.Expression e2_4 = this.parseAssignExp();
                    e = new ASTBase.DivAssignExp(loc, e, e2_4);
                    break;
                case 83:
                    this.nextToken();
                    ASTBase.Expression e2_5 = this.parseAssignExp();
                    e = new ASTBase.ModAssignExp(loc, e, e2_5);
                    break;
                case 227:
                    this.nextToken();
                    ASTBase.Expression e2_6 = this.parseAssignExp();
                    e = new ASTBase.PowAssignExp(loc, e, e2_6);
                    break;
                case 87:
                    this.nextToken();
                    ASTBase.Expression e2_7 = this.parseAssignExp();
                    e = new ASTBase.AndAssignExp(loc, e, e2_7);
                    break;
                case 88:
                    this.nextToken();
                    ASTBase.Expression e2_8 = this.parseAssignExp();
                    e = new ASTBase.OrAssignExp(loc, e, e2_8);
                    break;
                case 89:
                    this.nextToken();
                    ASTBase.Expression e2_9 = this.parseAssignExp();
                    e = new ASTBase.XorAssignExp(loc, e, e2_9);
                    break;
                case 66:
                    this.nextToken();
                    ASTBase.Expression e2_10 = this.parseAssignExp();
                    e = new ASTBase.ShlAssignExp(loc, e, e2_10);
                    break;
                case 67:
                    this.nextToken();
                    ASTBase.Expression e2_11 = this.parseAssignExp();
                    e = new ASTBase.ShrAssignExp(loc, e, e2_11);
                    break;
                case 69:
                    this.nextToken();
                    ASTBase.Expression e2_12 = this.parseAssignExp();
                    e = new ASTBase.UshrAssignExp(loc, e, e2_12);
                    break;
                case 71:
                    this.nextToken();
                    ASTBase.Expression e2_13 = this.parseAssignExp();
                    e = new ASTBase.CatAssignExp(loc, e, e2_13);
                    break;
                default:
                break;
            }
            return e;
        }

        public  Ptr<DArray<ASTBase.Expression>> parseArguments() {
            Ptr<DArray<ASTBase.Expression>> arguments = null;
            byte endtok = TOK.reserved;
            arguments = refPtr(new DArray<ASTBase.Expression>());
            endtok = ((this.token.value.value & 0xFF) == 3) ? TOK.rightBracket : TOK.rightParentheses;
            this.nextToken();
            for (; ((this.token.value.value & 0xFF) != (endtok & 0xFF)) && ((this.token.value.value & 0xFF) != 11);){
                ASTBase.Expression arg = this.parseAssignExp();
                (arguments.get()).push(arg);
                if (((this.token.value.value & 0xFF) == (endtok & 0xFF)))
                {
                    break;
                }
                this.check(TOK.comma);
            }
            this.check(endtok);
            return arguments;
        }

        public  ASTBase.Expression parseNewExp(ASTBase.Expression thisexp) {
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            Ptr<DArray<ASTBase.Expression>> newargs = null;
            Ptr<DArray<ASTBase.Expression>> arguments = null;
            if (((this.token.value.value & 0xFF) == 1))
            {
                newargs = this.parseArguments();
            }
            if (((this.token.value.value & 0xFF) == 153))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 1))
                {
                    arguments = this.parseArguments();
                }
                Ptr<DArray<Ptr<ASTBase.BaseClass>>> baseclasses = null;
                if (((this.token.value.value & 0xFF) != 5))
                {
                    baseclasses = this.parseBaseClasses();
                }
                Identifier id = null;
                Ptr<DArray<ASTBase.Dsymbol>> members = null;
                if (((this.token.value.value & 0xFF) != 5))
                {
                    this.error(new BytePtr("`{ members }` expected for anonymous class"));
                }
                else
                {
                    this.nextToken();
                    members = this.parseDeclDefs(0, null, null);
                    if (((this.token.value.value & 0xFF) != 6))
                    {
                        this.error(new BytePtr("class member expected"));
                    }
                    this.nextToken();
                }
                ASTBase.ClassDeclaration cd = new ASTBase.ClassDeclaration(loc, id, baseclasses, members, false);
                ASTBase.NewAnonClassExp e = new ASTBase.NewAnonClassExp(loc, thisexp, newargs, cd, arguments);
                return e;
            }
            long stc = this.parseTypeCtor();
            ASTBase.Type t = this.parseBasicType(true);
            t = this.parseBasicType2(t);
            t = t.addSTC(stc);
            if (((t.ty & 0xFF) == ASTBase.ENUMTY.Taarray))
            {
                ASTBase.TypeAArray taa = (ASTBase.TypeAArray)t;
                ASTBase.Type index = taa.index;
                ASTBase.Expression edim = ASTBase.typeToExpression(index);
                if (edim == null)
                {
                    this.error(new BytePtr("need size of rightmost array, not type `%s`"), index.toChars());
                    return new ASTBase.NullExp(loc, null);
                }
                t = new ASTBase.TypeSArray(taa.next, edim);
            }
            else if (((this.token.value.value & 0xFF) == 1) && ((t.ty & 0xFF) != ASTBase.ENUMTY.Tsarray))
            {
                arguments = this.parseArguments();
            }
            ASTBase.NewExp e = new ASTBase.NewExp(loc, thisexp, newargs, t, arguments);
            return e;
        }

        public  void addComment(ASTBase.Dsymbol s, BytePtr blockComment) {
            if ((s != null))
            {
                s.addComment(Lexer.combineComments(blockComment, this.token.value.lineComment.value, true));
                this.token.value.lineComment.value = null;
            }
        }


        public ParserASTBase() {}

        public ParserASTBase copy() {
            ParserASTBase that = new ParserASTBase();
            that.md = this.md;
            that.mod = this.mod;
            that.linkage = this.linkage;
            that.cppmangle = this.cppmangle;
            that.endloc = this.endloc;
            that.inBrackets = this.inBrackets;
            that.lookingForElse = this.lookingForElse;
            that.scanloc = this.scanloc;
            that.prevloc = this.prevloc;
            that.p = this.p;
            that.token = this.token;
            that.base = this.base;
            that.end = this.end;
            that.line = this.line;
            that.doDocComment = this.doDocComment;
            that.anyToken = this.anyToken;
            that.commentToken = this.commentToken;
            that.inTokenStringConstant = this.inTokenStringConstant;
            that.lastDocLine = this.lastDocLine;
            that.diagnosticReporter = this.diagnosticReporter;
            that.tokenFreelist = this.tokenFreelist;
            return that;
        }
    }

    // from template Parser!(ASTCodegen)
    public static class ParserASTCodegen extends Lexer
    {
        public Ptr<ModuleDeclaration> md = null;
        public dmodule.Module mod = null;
        public int linkage = 0;
        public int cppmangle = 0;
        public Loc endloc = new Loc();
        public int inBrackets = 0;
        public Loc lookingForElse = new Loc();
        public  ParserASTCodegen(Loc loc, dmodule.Module _module, ByteSlice input, boolean doDocComment, DiagnosticReporter diagnosticReporter) {
            super(_module != null ? _module.srcfile.toChars() : null, toBytePtr(input), 0, input.getLength(), doDocComment, false, diagnosticReporter);
            this.scanloc = loc.copy();
            if (!writeMixin(input, this.scanloc) && (loc.filename != null))
            {
                BytePtr filename = pcopy(((BytePtr)Mem.xmalloc(strlen(loc.filename) + 7 + 12 + 1)));
                sprintf(filename, new BytePtr("%s-mixin-%d"), loc.filename, loc.linnum);
                this.scanloc.filename = pcopy(filename);
            }
            this.mod = _module;
            this.linkage = LINK.d;
        }

        public  ParserASTCodegen(dmodule.Module _module, ByteSlice input, boolean doDocComment, DiagnosticReporter diagnosticReporter) {
            super(_module != null ? _module.srcfile.toChars() : null, toBytePtr(input), 0, input.getLength(), doDocComment, false, diagnosticReporter);
            this.mod = _module;
            this.linkage = LINK.d;
        }

        public  Ptr<DArray<Dsymbol>> parseModule() {
            BytePtr comment = pcopy(this.token.value.blockComment.value);
            boolean isdeprecated = false;
            Expression msg = null;
            Ptr<DArray<Expression>> udas = null;
            Ptr<DArray<Dsymbol>> decldefs = null;
            Dsymbol lastDecl = this.mod;
            Ptr<Token> tk = null;
            if (this.skipAttributes(ptr(this.token), ptr(tk)) && (((tk.get()).value & 0xFF) == 34))
            {
                for (; ((this.token.value.value & 0xFF) != 34);){
                    switch ((this.token.value.value & 0xFF))
                    {
                        case 174:
                            if (isdeprecated)
                            {
                                this.error(new BytePtr("there is only one deprecation attribute allowed for module declaration"));
                            }
                            isdeprecated = true;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.check(TOK.leftParentheses);
                                msg = this.parseAssignExp();
                                this.check(TOK.rightParentheses);
                            }
                            break;
                        case 225:
                            Ptr<DArray<Expression>> exps = null;
                            long stc = this.parseAttribute(ptr(exps));
                            if ((stc == 4294967296L) || (stc == 4398046511104L) || (stc == 137438953472L) || (stc == 8589934592L) || (stc == 17179869184L) || (stc == 34359738368L))
                            {
                                this.error(new BytePtr("`@%s` attribute for module declaration is not supported"), this.token.value.toChars());
                            }
                            else
                            {
                                udas = UserAttributeDeclaration.concat(udas, exps);
                            }
                            if (stc != 0)
                            {
                                this.nextToken();
                            }
                            break;
                        default:
                        this.error(new BytePtr("`module` expected instead of `%s`"), this.token.value.toChars());
                        this.nextToken();
                        break;
                    }
                }
            }
            if (udas != null)
            {
                Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                UserAttributeDeclaration udad = new UserAttributeDeclaration(udas, a);
                this.mod.userAttribDecl = udad;
            }
            try {
                if (((this.token.value.value & 0xFF) == 34))
                {
                    Loc loc = this.token.value.loc.copy();
                    this.nextToken();
                    if (((this.token.value.value & 0xFF) != 120))
                    {
                        this.error(new BytePtr("identifier expected following `module`"));
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                    Ptr<DArray<Identifier>> a = null;
                    Identifier id = this.token.value.ident;
                L_outer23:
                    for (; ((this.nextToken() & 0xFF) == 97);){
                        if (a == null)
                        {
                            a = refPtr(new DArray<Identifier>());
                        }
                        (a.get()).push(id);
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 120))
                        {
                            this.error(new BytePtr("identifier expected following `package`"));
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                        id = this.token.value.ident;
                    }
                    this.md = refPtr(new ModuleDeclaration(loc, a, id, msg, isdeprecated));
                    if (((this.token.value.value & 0xFF) != 9))
                    {
                        this.error(new BytePtr("`;` expected following module declaration instead of `%s`"), this.token.value.toChars());
                    }
                    this.nextToken();
                    this.addComment(this.mod, comment);
                }
                decldefs = this.parseDeclDefs(0, ptr(lastDecl), null);
                if (((this.token.value.value & 0xFF) != 11))
                {
                    this.error(this.token.value.loc, new BytePtr("unrecognized declaration"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                return decldefs;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            for (; ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                this.nextToken();
            }
            this.nextToken();
            return refPtr(new DArray<Dsymbol>());
        }

        public  long parseDeprecatedAttribute(Ref<Expression> msg) {
            if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
            {
                return 1024L;
            }
            this.nextToken();
            this.check(TOK.leftParentheses);
            Expression e = this.parseAssignExp();
            this.check(TOK.rightParentheses);
            if (msg.value != null)
            {
                this.error(new BytePtr("conflicting storage class `deprecated(%s)` and `deprecated(%s)`"), msg.value.toChars(), e.toChars());
            }
            msg.value = e;
            return 0L;
        }

        public  Ptr<DArray<Dsymbol>> parseDeclDefs(int once, Ptr<Dsymbol> pLastDecl, Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Dsymbol lastDecl = null;
            if (pLastDecl == null)
            {
                pLastDecl = pcopy(ptr(lastDecl));
            }
            int linksave = this.linkage;
            Ptr<DArray<Dsymbol>> decldefs = refPtr(new DArray<Dsymbol>());
        L_outer24:
            do {
                {
                    Dsymbol s = null;
                    Ptr<DArray<Dsymbol>> a = null;
                    PrefixAttributesASTCodegen attrs = new PrefixAttributesASTCodegen();
                    if ((once == 0) || (pAttrs == null))
                    {
                        pAttrs = ptr(attrs);
                        (pAttrs.get()).comment = pcopy(this.token.value.blockComment.value);
                    }
                    int prot = Prot.Kind.undefined;
                    long stc = 0L;
                    Condition condition = null;
                    this.linkage = linksave;
                    {
                        int __dispatch56 = 0;
                        dispatched_56:
                        do {
                            switch (__dispatch56 != 0 ? __dispatch56 : (this.token.value.value & 0xFF))
                            {
                                case 156:
                                    Ptr<Token> t = this.peek(ptr(this.token));
                                    if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7))
                                    {
                                        s = this.parseEnum();
                                    }
                                    else if ((((t.get()).value & 0xFF) != 120))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch56 = -1; continue dispatched_56; }
                                    }
                                    else
                                    {
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7) || (((t.get()).value & 0xFF) == 9))
                                        {
                                            s = this.parseEnum();
                                        }
                                        else
                                        {
                                            /*goto Ldeclaration*/{ __dispatch56 = -1; continue dispatched_56; }
                                        }
                                    }
                                    break;
                                case 157:
                                    a = this.parseImport();
                                    break;
                                case 36:
                                    s = this.parseTemplateDeclaration(false);
                                    break;
                                case 162:
                                    Loc loc = this.token.value.loc.copy();
                                    switch ((this.peekNext() & 0xFF))
                                    {
                                        case 1:
                                            this.nextToken();
                                            Ptr<DArray<Expression>> exps = this.parseArguments();
                                            this.check(TOK.semicolon);
                                            s = new CompileDeclaration(loc, exps);
                                            break;
                                        case 36:
                                            this.nextToken();
                                            s = this.parseTemplateDeclaration(true);
                                            break;
                                        default:
                                        s = this.parseMixin();
                                        break;
                                    }
                                    break;
                                case 149:
                                case 150:
                                case 151:
                                case 148:
                                case 129:
                                case 130:
                                case 131:
                                case 132:
                                case 133:
                                case 134:
                                case 135:
                                case 136:
                                case 137:
                                case 138:
                                case 139:
                                case 140:
                                case 141:
                                case 142:
                                case 143:
                                case 144:
                                case 145:
                                case 146:
                                case 147:
                                case 128:
                                case 158:
                                case 120:
                                case 124:
                                case 39:
                                case 97:
                                case 229:
                                case 152:
                                case 155:
                                case 153:
                                case 154:
                                case 213:
                                /*Ldeclaration:*/
                                case -1:
                                __dispatch56 = 0;
                                    a = this.parseDeclarations(false, pAttrs, (pAttrs.get()).comment);
                                    if ((a != null) && ((a.get()).length.value != 0))
                                    {
                                        pLastDecl.set(0, (a.get()).get((a.get()).length.value - 1));
                                    }
                                    break;
                                case 123:
                                    if (((this.peekNext() & 0xFF) == 97))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch56 = -1; continue dispatched_56; }
                                    }
                                    s = this.parseCtor(pAttrs);
                                    break;
                                case 92:
                                    s = this.parseDtor(pAttrs);
                                    break;
                                case 207:
                                    Ptr<Token> t_1 = this.peek(ptr(this.token));
                                    if ((((t_1.get()).value & 0xFF) == 1) || (((t_1.get()).value & 0xFF) == 5))
                                    {
                                        s = this.parseInvariant(pAttrs);
                                        break;
                                    }
                                    this.error(new BytePtr("invariant body expected, not `%s`"), this.token.value.toChars());
                                    /*goto Lerror*/{ __dispatch56 = -2; continue dispatched_56; }
                                case 208:
                                    if (global.params.useUnitTests || global.params.doDocComments || global.params.doHdrGeneration)
                                    {
                                        s = this.parseUnitTest(pAttrs);
                                        if (pLastDecl.get() != null)
                                        {
                                            (pLastDecl.get()).ddocUnittest = (UnitTestDeclaration)s;
                                        }
                                    }
                                    else
                                    {
                                        Loc loc_1 = this.token.value.loc.copy();
                                        int braces = 0;
                                    L_outer25:
                                        for (; 1 != 0;){
                                            this.nextToken();
                                            {
                                                int __dispatch58 = 0;
                                                dispatched_58:
                                                do {
                                                    switch (__dispatch58 != 0 ? __dispatch58 : (this.token.value.value & 0xFF))
                                                    {
                                                        case 5:
                                                            braces += 1;
                                                            continue L_outer25;
                                                        case 6:
                                                            if ((braces -= 1) != 0)
                                                            {
                                                                continue L_outer25;
                                                            }
                                                            this.nextToken();
                                                            break;
                                                        case 11:
                                                            this.error(loc_1, new BytePtr("closing `}` of unittest not found before end of file"));
                                                            /*goto Lerror*/{ __dispatch56 = -2; continue dispatched_56; }
                                                        default:
                                                        continue L_outer25;
                                                    }
                                                } while(__dispatch58 != 0);
                                            }
                                            break;
                                        }
                                        s = new UnitTestDeclaration(loc_1, this.token.value.loc, 0L, null);
                                    }
                                    break;
                                case 22:
                                    s = this.parseNew(pAttrs);
                                    break;
                                case 23:
                                    s = this.parseDelete(pAttrs);
                                    break;
                                case 7:
                                case 5:
                                    this.error(new BytePtr("declaration expected, not `%s`"), this.token.value.toChars());
                                    /*goto Lerror*/{ __dispatch56 = -2; continue dispatched_56; }
                                case 6:
                                case 11:
                                    if (once != 0)
                                    {
                                        this.error(new BytePtr("declaration expected, not `%s`"), this.token.value.toChars());
                                    }
                                    return decldefs;
                                case 169:
                                    byte next = this.peekNext();
                                    if (((next & 0xFF) == 123))
                                    {
                                        s = this.parseStaticCtor(pAttrs);
                                    }
                                    else if (((next & 0xFF) == 92))
                                    {
                                        s = this.parseStaticDtor(pAttrs);
                                    }
                                    else if (((next & 0xFF) == 14))
                                    {
                                        s = this.parseStaticAssert();
                                    }
                                    else if (((next & 0xFF) == 183))
                                    {
                                        condition = this.parseStaticIfCondition();
                                        Ptr<DArray<Dsymbol>> athen = null;
                                        if (((this.token.value.value & 0xFF) == 7))
                                        {
                                            athen = this.parseBlock(pLastDecl, null);
                                        }
                                        else
                                        {
                                            Loc lookingForElseSave = this.lookingForElse.copy();
                                            this.lookingForElse = this.token.value.loc.copy();
                                            athen = this.parseBlock(pLastDecl, null);
                                            this.lookingForElse = lookingForElseSave.copy();
                                        }
                                        Ptr<DArray<Dsymbol>> aelse = null;
                                        if (((this.token.value.value & 0xFF) == 184))
                                        {
                                            Loc elseloc = this.token.value.loc.copy();
                                            this.nextToken();
                                            aelse = this.parseBlock(pLastDecl, null);
                                            this.checkDanglingElse(elseloc);
                                        }
                                        s = new StaticIfDeclaration(condition, athen, aelse);
                                    }
                                    else if (((next & 0xFF) == 157))
                                    {
                                        a = this.parseImport();
                                    }
                                    else if (((next & 0xFF) == 201) || ((next & 0xFF) == 202))
                                    {
                                        s = this.parseForeach11(this.loc(), pLastDecl);
                                    }
                                    else
                                    {
                                        stc = 1L;
                                        /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                    }
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch56 = -1; continue dispatched_56; }
                                    }
                                    stc = 4L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 182:
                                    if (((this.peekNext() & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch56 = -1; continue dispatched_56; }
                                    }
                                    stc = 1048576L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 224:
                                    byte next_1 = this.peekNext();
                                    if (((next_1 & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch56 = -1; continue dispatched_56; }
                                    }
                                    if (((next_1 & 0xFF) == 169))
                                    {
                                        byte next2 = this.peekNext2();
                                        if (((next2 & 0xFF) == 123))
                                        {
                                            s = this.parseSharedStaticCtor(pAttrs);
                                            break;
                                        }
                                        if (((next2 & 0xFF) == 92))
                                        {
                                            s = this.parseSharedStaticDtor(pAttrs);
                                            break;
                                        }
                                    }
                                    stc = 536870912L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 177:
                                    if (((this.peekNext() & 0xFF) == 1))
                                    {
                                        /*goto Ldeclaration*/{ __dispatch56 = -1; continue dispatched_56; }
                                    }
                                    stc = 2147483648L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 170:
                                    stc = 8L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 179:
                                    stc = 256L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 203:
                                    stc = 524288L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 159:
                                    stc = 128L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 172:
                                    stc = 16L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 194:
                                    stc = 512L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 216:
                                    stc = 33554432L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 215:
                                    stc = 67108864L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 217:
                                    stc = 1073741824L;
                                    /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                case 225:
                                    {
                                        Ptr<DArray<Expression>> exps_1 = null;
                                        stc = this.parseAttribute(ptr(exps_1));
                                        if (stc != 0)
                                        {
                                            /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                        }
                                        (pAttrs.get()).udas = UserAttributeDeclaration.concat((pAttrs.get()).udas, exps_1);
                                        /*goto Lautodecl*/{ __dispatch56 = -4; continue dispatched_56; }
                                    }
                                /*Lstc:*/
                                case -3:
                                __dispatch56 = 0;
                                    (pAttrs.get()).storageClass = this.appendStorageClass((pAttrs.get()).storageClass, stc);
                                    this.nextToken();
                                /*Lautodecl:*/
                                case -4:
                                __dispatch56 = 0;
                                    if (((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign))
                                    {
                                        a = this.parseAutoDeclarations(getStorageClassASTCodegen(pAttrs), (pAttrs.get()).comment);
                                        if ((a != null) && ((a.get()).length.value != 0))
                                        {
                                            pLastDecl.set(0, (a.get()).get((a.get()).length.value - 1));
                                        }
                                        if ((pAttrs.get()).udas != null)
                                        {
                                            s = new UserAttributeDeclaration((pAttrs.get()).udas, a);
                                            (pAttrs.get()).udas = null;
                                        }
                                        break;
                                    }
                                    Ptr<Token> tk = null;
                                    if (((this.token.value.value & 0xFF) == 120) && this.skipParens(this.peek(ptr(this.token)), ptr(tk)) && this.skipAttributes(tk, ptr(tk)) && (((tk.get()).value & 0xFF) == 1) || (((tk.get()).value & 0xFF) == 5) || (((tk.get()).value & 0xFF) == 175) || (((tk.get()).value & 0xFF) == 176) || (((tk.get()).value & 0xFF) == 187) || (((tk.get()).value & 0xFF) == 120) && (pequals((tk.get()).ident, Id._body)))
                                    {
                                        a = this.parseDeclarations(true, pAttrs, (pAttrs.get()).comment);
                                        if ((a != null) && ((a.get()).length.value != 0))
                                        {
                                            pLastDecl.set(0, (a.get()).get((a.get()).length.value - 1));
                                        }
                                        if ((pAttrs.get()).udas != null)
                                        {
                                            s = new UserAttributeDeclaration((pAttrs.get()).udas, a);
                                            (pAttrs.get()).udas = null;
                                        }
                                        break;
                                    }
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    long stc2 = getStorageClassASTCodegen(pAttrs);
                                    if ((stc2 != 0L))
                                    {
                                        s = new StorageClassDeclaration(stc2, a);
                                    }
                                    if ((pAttrs.get()).udas != null)
                                    {
                                        if (s != null)
                                        {
                                            a = refPtr(new DArray<Dsymbol>());
                                            (a.get()).push(s);
                                        }
                                        s = new UserAttributeDeclaration((pAttrs.get()).udas, a);
                                        (pAttrs.get()).udas = null;
                                    }
                                    break;
                                case 174:
                                    Expression e = null;
                                    {
                                        long _stc = this.parseDeprecatedAttribute((pAttrs.get()).depmsg);
                                        if ((_stc) != 0)
                                        {
                                            stc = _stc;
                                            /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                        }
                                    }
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs.get()).depmsg != null)
                                    {
                                        s = new DeprecatedDeclaration((pAttrs.get()).depmsg, a);
                                        (pAttrs.get()).depmsg = null;
                                    }
                                    break;
                                case 3:
                                    if (((this.peekNext() & 0xFF) == 4))
                                    {
                                        this.error(new BytePtr("empty attribute list is not allowed"));
                                    }
                                    this.error(new BytePtr("use `@(attributes)` instead of `[attributes]`"));
                                    Ptr<DArray<Expression>> exps_2 = this.parseArguments();
                                    (pAttrs.get()).udas = UserAttributeDeclaration.concat((pAttrs.get()).udas, exps_2);
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs.get()).udas != null)
                                    {
                                        s = new UserAttributeDeclaration((pAttrs.get()).udas, a);
                                        (pAttrs.get()).udas = null;
                                    }
                                    break;
                                case 164:
                                    if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
                                    {
                                        stc = 2L;
                                        /*goto Lstc*/{ __dispatch56 = -3; continue dispatched_56; }
                                    }
                                    Loc linkLoc = this.token.value.loc.copy();
                                    Ptr<DArray<Identifier>> idents = null;
                                    Ptr<DArray<Expression>> identExps = null;
                                    int cppmangle = CPPMANGLE.def;
                                    boolean cppMangleOnly = false;
                                    int link = this.parseLinkage(ptr(idents), ptr(identExps), cppmangle, cppMangleOnly);
                                    if (((pAttrs.get()).link != LINK.default_))
                                    {
                                        if (((pAttrs.get()).link != link))
                                        {
                                            this.error(new BytePtr("conflicting linkage `extern (%s)` and `extern (%s)`"), linkageToChars((pAttrs.get()).link), linkageToChars(link));
                                        }
                                        else if ((idents != null) || (identExps != null) || (cppmangle != CPPMANGLE.def))
                                        {
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("redundant linkage `extern (%s)`"), linkageToChars((pAttrs.get()).link));
                                        }
                                    }
                                    (pAttrs.get()).link = link;
                                    this.linkage = link;
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if (idents != null)
                                    {
                                        assert((link == LINK.cpp));
                                        assert((idents.get()).length.value != 0);
                                        {
                                            int i = (idents.get()).length.value;
                                            for (; i != 0;){
                                                Identifier id = (idents.get()).get(i -= 1);
                                                if (s != null)
                                                {
                                                    a = refPtr(new DArray<Dsymbol>());
                                                    (a.get()).push(s);
                                                }
                                                if (cppMangleOnly)
                                                {
                                                    s = new CPPNamespaceDeclaration(id, a);
                                                }
                                                else
                                                {
                                                    s = new Nspace(linkLoc, id, null, a);
                                                }
                                            }
                                        }
                                        (pAttrs.get()).link = LINK.default_;
                                    }
                                    else if (identExps != null)
                                    {
                                        assert((link == LINK.cpp));
                                        assert((identExps.get()).length.value != 0);
                                        {
                                            int i_1 = (identExps.get()).length.value;
                                            for (; i_1 != 0;){
                                                Expression exp = (identExps.get()).get(i_1 -= 1);
                                                if (s != null)
                                                {
                                                    a = refPtr(new DArray<Dsymbol>());
                                                    (a.get()).push(s);
                                                }
                                                if (cppMangleOnly)
                                                {
                                                    s = new CPPNamespaceDeclaration(exp, a);
                                                }
                                                else
                                                {
                                                    s = new Nspace(linkLoc, null, exp, a);
                                                }
                                            }
                                        }
                                        (pAttrs.get()).link = LINK.default_;
                                    }
                                    else if ((cppmangle != CPPMANGLE.def))
                                    {
                                        assert((link == LINK.cpp));
                                        s = new CPPMangleDeclaration(cppmangle, a);
                                    }
                                    else if (((pAttrs.get()).link != LINK.default_))
                                    {
                                        s = new LinkDeclaration((pAttrs.get()).link, a);
                                        (pAttrs.get()).link = LINK.default_;
                                    }
                                    break;
                                case 165:
                                    prot = Prot.Kind.private_;
                                    /*goto Lprot*/{ __dispatch56 = -5; continue dispatched_56; }
                                case 180:
                                    prot = Prot.Kind.package_;
                                    /*goto Lprot*/{ __dispatch56 = -5; continue dispatched_56; }
                                case 166:
                                    prot = Prot.Kind.protected_;
                                    /*goto Lprot*/{ __dispatch56 = -5; continue dispatched_56; }
                                case 167:
                                    prot = Prot.Kind.public_;
                                    /*goto Lprot*/{ __dispatch56 = -5; continue dispatched_56; }
                                case 168:
                                    prot = Prot.Kind.export_;
                                    /*goto Lprot*/{ __dispatch56 = -5; continue dispatched_56; }
                                /*Lprot:*/
                                case -5:
                                __dispatch56 = 0;
                                    {
                                        if (((pAttrs.get()).protection.kind.value != Prot.Kind.undefined))
                                        {
                                            if (((pAttrs.get()).protection.kind.value != prot))
                                            {
                                                this.error(new BytePtr("conflicting protection attribute `%s` and `%s`"), protectionToChars((pAttrs.get()).protection.kind.value), protectionToChars(prot));
                                            }
                                            else
                                            {
                                                this.error(new BytePtr("redundant protection attribute `%s`"), protectionToChars(prot));
                                            }
                                        }
                                        (pAttrs.get()).protection.kind.value = prot;
                                        this.nextToken();
                                        Ptr<DArray<Identifier>> pkg_prot_idents = null;
                                        if (((pAttrs.get()).protection.kind.value == Prot.Kind.package_) && ((this.token.value.value & 0xFF) == 1))
                                        {
                                            pkg_prot_idents = this.parseQualifiedIdentifier(new BytePtr("protection package"));
                                            if (pkg_prot_idents != null)
                                            {
                                                this.check(TOK.rightParentheses);
                                            }
                                            else
                                            {
                                                for (; ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                                                    this.nextToken();
                                                }
                                                this.nextToken();
                                                break;
                                            }
                                        }
                                        Loc attrloc = this.token.value.loc.copy();
                                        a = this.parseBlock(pLastDecl, pAttrs);
                                        if (((pAttrs.get()).protection.kind.value != Prot.Kind.undefined))
                                        {
                                            if (((pAttrs.get()).protection.kind.value == Prot.Kind.package_) && (pkg_prot_idents != null))
                                            {
                                                s = new ProtDeclaration(attrloc, pkg_prot_idents, a);
                                            }
                                            else
                                            {
                                                s = new ProtDeclaration(attrloc, (pAttrs.get()).protection, a);
                                            }
                                            (pAttrs.get()).protection = new Prot(Prot.Kind.undefined).copy();
                                        }
                                        break;
                                    }
                                case 163:
                                    Loc attrLoc = this.token.value.loc.copy();
                                    this.nextToken();
                                    Expression e_1 = null;
                                    if (((this.token.value.value & 0xFF) == 1))
                                    {
                                        this.nextToken();
                                        e_1 = this.parseAssignExp();
                                        this.check(TOK.rightParentheses);
                                    }
                                    if ((pAttrs.get()).setAlignment)
                                    {
                                        if (e_1 != null)
                                        {
                                            this.error(new BytePtr("redundant alignment attribute `align(%s)`"), e_1.toChars());
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("redundant alignment attribute `align`"));
                                        }
                                    }
                                    (pAttrs.get()).setAlignment = true;
                                    (pAttrs.get()).ealign = e_1;
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs.get()).setAlignment)
                                    {
                                        s = new AlignDeclaration(attrLoc, (pAttrs.get()).ealign, a);
                                        (pAttrs.get()).setAlignment = false;
                                        (pAttrs.get()).ealign = null;
                                    }
                                    break;
                                case 40:
                                    Ptr<DArray<Expression>> args = null;
                                    Loc loc_2 = this.token.value.loc.copy();
                                    this.nextToken();
                                    this.check(TOK.leftParentheses);
                                    if (((this.token.value.value & 0xFF) != 120))
                                    {
                                        this.error(new BytePtr("`pragma(identifier)` expected"));
                                        /*goto Lerror*/{ __dispatch56 = -2; continue dispatched_56; }
                                    }
                                    Identifier ident = this.token.value.ident;
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 99) && ((this.peekNext() & 0xFF) != 2))
                                    {
                                        args = this.parseArguments();
                                    }
                                    else
                                    {
                                        this.check(TOK.rightParentheses);
                                    }
                                    Ptr<DArray<Dsymbol>> a2 = null;
                                    if (((this.token.value.value & 0xFF) == 9))
                                    {
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        a2 = this.parseBlock(pLastDecl, null);
                                    }
                                    s = new PragmaDeclaration(loc_2, ident, args, a2);
                                    break;
                                case 173:
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) == 120))
                                        {
                                            s = new DebugSymbol(this.token.value.loc, this.token.value.ident);
                                        }
                                        else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                                        {
                                            s = new DebugSymbol(this.token.value.loc, (int)this.token.value.intvalue);
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("identifier or integer expected, not `%s`"), this.token.value.toChars());
                                            s = null;
                                        }
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 9))
                                        {
                                            this.error(new BytePtr("semicolon expected"));
                                        }
                                        this.nextToken();
                                        break;
                                    }
                                    condition = this.parseDebugCondition();
                                    /*goto Lcondition*/{ __dispatch56 = -6; continue dispatched_56; }
                                case 33:
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) == 120))
                                        {
                                            s = new VersionSymbol(this.token.value.loc, this.token.value.ident);
                                        }
                                        else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                                        {
                                            s = new VersionSymbol(this.token.value.loc, (int)this.token.value.intvalue);
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("identifier or integer expected, not `%s`"), this.token.value.toChars());
                                            s = null;
                                        }
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 9))
                                        {
                                            this.error(new BytePtr("semicolon expected"));
                                        }
                                        this.nextToken();
                                        break;
                                    }
                                    condition = this.parseVersionCondition();
                                    /*goto Lcondition*/{ __dispatch56 = -6; continue dispatched_56; }
                                /*Lcondition:*/
                                case -6:
                                __dispatch56 = 0;
                                    {
                                        Ptr<DArray<Dsymbol>> athen_1 = null;
                                        if (((this.token.value.value & 0xFF) == 7))
                                        {
                                            athen_1 = this.parseBlock(pLastDecl, null);
                                        }
                                        else
                                        {
                                            Loc lookingForElseSave_1 = this.lookingForElse.copy();
                                            this.lookingForElse = this.token.value.loc.copy();
                                            athen_1 = this.parseBlock(pLastDecl, null);
                                            this.lookingForElse = lookingForElseSave_1.copy();
                                        }
                                        Ptr<DArray<Dsymbol>> aelse_1 = null;
                                        if (((this.token.value.value & 0xFF) == 184))
                                        {
                                            Loc elseloc_1 = this.token.value.loc.copy();
                                            this.nextToken();
                                            aelse_1 = this.parseBlock(pLastDecl, null);
                                            this.checkDanglingElse(elseloc_1);
                                        }
                                        s = new ConditionalDeclaration(condition, athen_1, aelse_1);
                                        break;
                                    }
                                case 9:
                                    this.nextToken();
                                    continue L_outer24;
                                default:
                                this.error(new BytePtr("declaration expected, not `%s`"), this.token.value.toChars());
                            /*Lerror:*/
                            case -2:
                            __dispatch56 = 0;
                                for (; ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                                    this.nextToken();
                                }
                                this.nextToken();
                                s = null;
                                continue L_outer24;
                            }
                        } while(__dispatch56 != 0);
                    }
                    if (s != null)
                    {
                        if (s.isAttribDeclaration() == null)
                        {
                            pLastDecl.set(0, s);
                        }
                        (decldefs.get()).push(s);
                        this.addComment(s, (pAttrs.get()).comment);
                    }
                    else if ((a != null) && ((a.get()).length.value != 0))
                    {
                        (decldefs.get()).append(a);
                    }
                }
            } while (once == 0);
            this.linkage = linksave;
            return decldefs;
        }

        // defaulted all parameters starting with #3
        public  Ptr<DArray<Dsymbol>> parseDeclDefs(int once, Ptr<Dsymbol> pLastDecl) {
            return parseDeclDefs(once, pLastDecl, null);
        }

        // defaulted all parameters starting with #2
        public  Ptr<DArray<Dsymbol>> parseDeclDefs(int once) {
            return parseDeclDefs(once, null, null);
        }

        public  Ptr<DArray<Dsymbol>> parseAutoDeclarations(long storageClass, BytePtr comment) {
            Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
            for (; 1 != 0;){
                Loc loc = this.token.value.loc.copy();
                Identifier ident = this.token.value.ident;
                this.nextToken();
                Ptr<DArray<TemplateParameter>> tpl = null;
                if (((this.token.value.value & 0xFF) == 1))
                {
                    tpl = this.parseTemplateParameterList(0);
                }
                this.check(TOK.assign);
                Initializer _init = this.parseInitializer();
                VarDeclaration v = new VarDeclaration(loc, null, ident, _init, storageClass);
                Dsymbol s = v;
                if (tpl != null)
                {
                    Ptr<DArray<Dsymbol>> a2 = refPtr(new DArray<Dsymbol>());
                    (a2.get()).push(v);
                    TemplateDeclaration tempdecl = new TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                    s = tempdecl;
                }
                (a.get()).push(s);
                switch ((this.token.value.value & 0xFF))
                {
                    case 9:
                        this.nextToken();
                        this.addComment(s, comment);
                        break;
                    case 99:
                        this.nextToken();
                        if (!(((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign)))
                        {
                            this.error(new BytePtr("identifier expected following comma"));
                            break;
                        }
                        this.addComment(s, comment);
                        continue;
                    default:
                    this.error(new BytePtr("semicolon expected following auto declaration, not `%s`"), this.token.value.toChars());
                    break;
                }
                break;
            }
            return a;
        }

        public  Ptr<DArray<Dsymbol>> parseBlock(Ptr<Dsymbol> pLastDecl, Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Ptr<DArray<Dsymbol>> a = null;
            switch ((this.token.value.value & 0xFF))
            {
                case 9:
                    this.error(new BytePtr("declaration expected following attribute, not `;`"));
                    this.nextToken();
                    break;
                case 11:
                    this.error(new BytePtr("declaration expected following attribute, not end of file"));
                    break;
                case 5:
                    Loc lookingForElseSave = this.lookingForElse.copy();
                    this.lookingForElse = new Loc(null, 0, 0).copy();
                    this.nextToken();
                    a = this.parseDeclDefs(0, pLastDecl, null);
                    if (((this.token.value.value & 0xFF) != 6))
                    {
                        this.error(new BytePtr("matching `}` expected, not `%s`"), this.token.value.toChars());
                    }
                    else
                    {
                        this.nextToken();
                    }
                    this.lookingForElse = lookingForElseSave.copy();
                    break;
                case 7:
                    this.nextToken();
                    a = this.parseDeclDefs(0, pLastDecl, null);
                    break;
                default:
                a = this.parseDeclDefs(1, pLastDecl, pAttrs);
                break;
            }
            return a;
        }

        // defaulted all parameters starting with #2
        public  Ptr<DArray<Dsymbol>> parseBlock(Ptr<Dsymbol> pLastDecl) {
            return parseBlock(pLastDecl, null);
        }

        public  long appendStorageClass(long storageClass, long stc) {
            if (((storageClass & stc) != 0) || ((storageClass & 2048L) != 0) && ((stc & 524292L) != 0) || ((stc & 2048L) != 0) && ((storageClass & 524292L) != 0))
            {
                OutBuffer buf = new OutBuffer();
                try {
                    stcToBuffer(ptr(buf), stc);
                    this.error(new BytePtr("redundant attribute `%s`"), buf.peekChars());
                    return storageClass | stc;
                }
                finally {
                }
            }
            storageClass |= stc;
            if ((stc & 9437188L) != 0)
            {
                long u = storageClass & 9437188L;
                if ((u & u - 1L) != 0)
                {
                    this.error(new BytePtr("conflicting attribute `%s`"), Token.toChars(this.token.value.value));
                }
            }
            if ((stc & 1744830464L) != 0)
            {
                long u = storageClass & 1744830464L;
                if ((u & u - 1L) != 0)
                {
                    this.error(new BytePtr("conflicting attribute `%s`"), Token.toChars(this.token.value.value));
                }
            }
            if ((stc & 60129542144L) != 0)
            {
                long u = storageClass & 60129542144L;
                if ((u & u - 1L) != 0)
                {
                    this.error(new BytePtr("conflicting attribute `@%s`"), this.token.value.toChars());
                }
            }
            return storageClass;
        }

        public  long parseAttribute(Ptr<Ptr<DArray<Expression>>> pudas) {
            this.nextToken();
            Ptr<DArray<Expression>> udas = null;
            long stc = 0L;
            if (((this.token.value.value & 0xFF) == 120))
            {
                if ((pequals(this.token.value.ident, Id.property)))
                {
                    stc = 4294967296L;
                }
                else if ((pequals(this.token.value.ident, Id.nogc)))
                {
                    stc = 4398046511104L;
                }
                else if ((pequals(this.token.value.ident, Id.safe)))
                {
                    stc = 8589934592L;
                }
                else if ((pequals(this.token.value.ident, Id.trusted)))
                {
                    stc = 17179869184L;
                }
                else if ((pequals(this.token.value.ident, Id.system)))
                {
                    stc = 34359738368L;
                }
                else if ((pequals(this.token.value.ident, Id.disable)))
                {
                    stc = 137438953472L;
                }
                else if ((pequals(this.token.value.ident, Id.future)))
                {
                    stc = 1125899906842624L;
                }
                else
                {
                    Expression exp = this.parsePrimaryExp();
                    if (((this.token.value.value & 0xFF) == 1))
                    {
                        Loc loc = this.token.value.loc.copy();
                        exp = new CallExp(loc, exp, this.parseArguments());
                    }
                    udas = refPtr(new DArray<Expression>());
                    (udas.get()).push(exp);
                }
            }
            else if (((this.token.value.value & 0xFF) == 1))
            {
                if (((this.peekNext() & 0xFF) == 2))
                {
                    this.error(new BytePtr("empty attribute list is not allowed"));
                }
                udas = this.parseArguments();
            }
            else
            {
                this.error(new BytePtr("@identifier or @(ArgumentList) expected, not `@%s`"), this.token.value.toChars());
            }
            if (stc != 0)
            {
            }
            else if (udas != null)
            {
                pudas.set(0, UserAttributeDeclaration.concat(pudas.get(), udas));
            }
            else
            {
                this.error(new BytePtr("valid attributes are `@property`, `@safe`, `@trusted`, `@system`, `@disable`, `@nogc`"));
            }
            return stc;
        }

        public  long parsePostfix(long storageClass, Ptr<Ptr<DArray<Expression>>> pudas) {
            for (; 1 != 0;){
                long stc = 0L;
                switch ((this.token.value.value & 0xFF))
                {
                    case 171:
                        stc = 4L;
                        break;
                    case 182:
                        stc = 1048576L;
                        break;
                    case 224:
                        stc = 536870912L;
                        break;
                    case 177:
                        stc = 2147483648L;
                        break;
                    case 216:
                        stc = 33554432L;
                        break;
                    case 215:
                        stc = 67108864L;
                        break;
                    case 195:
                        stc = 17592186044416L;
                        break;
                    case 203:
                        stc = 524288L;
                        break;
                    case 225:
                        Ptr<DArray<Expression>> udas = null;
                        stc = this.parseAttribute(ptr(udas));
                        if (udas != null)
                        {
                            if (pudas != null)
                            {
                                pudas.set(0, UserAttributeDeclaration.concat(pudas.get(), udas));
                            }
                            else
                            {
                                this.error(new BytePtr("user-defined attributes cannot appear as postfixes"));
                            }
                            continue;
                        }
                        break;
                    default:
                    return storageClass;
                }
                storageClass = this.appendStorageClass(storageClass, stc);
                this.nextToken();
            }
        }

        public  long parseTypeCtor() {
            long storageClass = 0L;
            for (; 1 != 0;){
                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                {
                    return storageClass;
                }
                long stc = 0L;
                switch ((this.token.value.value & 0xFF))
                {
                    case 171:
                        stc = 4L;
                        break;
                    case 182:
                        stc = 1048576L;
                        break;
                    case 224:
                        stc = 536870912L;
                        break;
                    case 177:
                        stc = 2147483648L;
                        break;
                    default:
                    return storageClass;
                }
                storageClass = this.appendStorageClass(storageClass, stc);
                this.nextToken();
            }
        }

        public  Expression parseConstraint() {
            Expression e = null;
            if (((this.token.value.value & 0xFF) == 183))
            {
                this.nextToken();
                this.check(TOK.leftParentheses);
                e = this.parseExpression();
                this.check(TOK.rightParentheses);
            }
            return e;
        }

        public  TemplateDeclaration parseTemplateDeclaration(boolean ismixin) {
            TemplateDeclaration tempdecl = null;
            Identifier id = null;
            Ptr<DArray<TemplateParameter>> tpl = null;
            Ptr<DArray<Dsymbol>> decldefs = null;
            Expression constraint = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            try {
                if (((this.token.value.value & 0xFF) != 120))
                {
                    this.error(new BytePtr("identifier expected following `template`"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                id = this.token.value.ident;
                this.nextToken();
                tpl = this.parseTemplateParameterList(0);
                if (tpl == null)
                {
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                constraint = this.parseConstraint();
                if (((this.token.value.value & 0xFF) != 5))
                {
                    this.error(new BytePtr("members of template declaration expected"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                decldefs = this.parseBlock(null, null);
                tempdecl = new TemplateDeclaration(loc, id, tpl, constraint, decldefs, ismixin, false);
                return tempdecl;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            return null;
        }

        // defaulted all parameters starting with #1
        public  TemplateDeclaration parseTemplateDeclaration() {
            return parseTemplateDeclaration(false);
        }

        public  Ptr<DArray<TemplateParameter>> parseTemplateParameterList(int flag) {
            Ptr<DArray<TemplateParameter>> tpl = refPtr(new DArray<TemplateParameter>());
            try {
                if ((flag == 0) && ((this.token.value.value & 0xFF) != 1))
                {
                    this.error(new BytePtr("parenthesized template parameter list expected following template identifier"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                this.nextToken();
                if ((flag != 0) || ((this.token.value.value & 0xFF) != 2))
                {
                    int isvariadic = 0;
                L_outer26:
                    for (; ((this.token.value.value & 0xFF) != 2);){
                        TemplateParameter tp = null;
                        Loc loc = new Loc();
                        Identifier tp_ident = null;
                        Type tp_spectype = null;
                        Type tp_valtype = null;
                        Type tp_defaulttype = null;
                        Expression tp_specvalue = null;
                        Expression tp_defaultvalue = null;
                        Ptr<Token> t = null;
                        t = this.peek(ptr(this.token));
                        if (((this.token.value.value & 0xFF) == 158))
                        {
                            this.nextToken();
                            loc = this.token.value.loc.copy();
                            Type spectype = null;
                            if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.must, TOK.reserved, null))
                            {
                                spectype = this.parseType(ptr(tp_ident), null);
                            }
                            else
                            {
                                if (((this.token.value.value & 0xFF) != 120))
                                {
                                    this.error(new BytePtr("identifier expected for template alias parameter"));
                                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                                }
                                tp_ident = this.token.value.ident;
                                this.nextToken();
                            }
                            RootObject spec = null;
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                                {
                                    spec = this.parseType(null, null);
                                }
                                else
                                {
                                    spec = this.parseCondExp();
                                }
                            }
                            RootObject def = null;
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                                {
                                    def = this.parseType(null, null);
                                }
                                else
                                {
                                    def = this.parseCondExp();
                                }
                            }
                            tp = new TemplateAliasParameter(loc, tp_ident, spectype, spec, def);
                        }
                        else if ((((t.get()).value & 0xFF) == 7) || (((t.get()).value & 0xFF) == 90) || (((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 2))
                        {
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("identifier expected for template type parameter"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            loc = this.token.value.loc.copy();
                            tp_ident = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                tp_spectype = this.parseType(null, null);
                            }
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                tp_defaulttype = this.parseType(null, null);
                            }
                            tp = new TemplateTypeParameter(loc, tp_ident, tp_spectype, tp_defaulttype);
                        }
                        else if (((this.token.value.value & 0xFF) == 120) && (((t.get()).value & 0xFF) == 10))
                        {
                            if (isvariadic != 0)
                            {
                                this.error(new BytePtr("variadic template parameter must be last"));
                            }
                            isvariadic = 1;
                            loc = this.token.value.loc.copy();
                            tp_ident = this.token.value.ident;
                            this.nextToken();
                            this.nextToken();
                            tp = new TemplateTupleParameter(loc, tp_ident);
                        }
                        else if (((this.token.value.value & 0xFF) == 123))
                        {
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("identifier expected for template this parameter"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            loc = this.token.value.loc.copy();
                            tp_ident = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                tp_spectype = this.parseType(null, null);
                            }
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                tp_defaulttype = this.parseType(null, null);
                            }
                            tp = new TemplateThisParameter(loc, tp_ident, tp_spectype, tp_defaulttype);
                        }
                        else
                        {
                            loc = this.token.value.loc.copy();
                            tp_valtype = this.parseType(ptr(tp_ident), null);
                            if (tp_ident == null)
                            {
                                this.error(new BytePtr("identifier expected for template value parameter"));
                                tp_ident = Identifier.idPool(new ByteSlice("error"));
                            }
                            if (((this.token.value.value & 0xFF) == 7))
                            {
                                this.nextToken();
                                tp_specvalue = this.parseCondExp();
                            }
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.nextToken();
                                tp_defaultvalue = this.parseDefaultInitExp();
                            }
                            tp = new TemplateValueParameter(loc, tp_ident, tp_valtype, tp_specvalue, tp_defaultvalue);
                        }
                        (tpl.get()).push(tp);
                        if (((this.token.value.value & 0xFF) != 99))
                        {
                            break;
                        }
                        this.nextToken();
                    }
                }
                this.check(TOK.rightParentheses);
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            return tpl;
        }

        // defaulted all parameters starting with #1
        public  Ptr<DArray<TemplateParameter>> parseTemplateParameterList() {
            return parseTemplateParameterList(0);
        }

        public  Dsymbol parseMixin() {
            TemplateMixin tm = null;
            Identifier id = null;
            Ptr<DArray<RootObject>> tiargs = null;
            Loc locMixin = this.token.value.loc.copy();
            this.nextToken();
            Loc loc = this.token.value.loc.copy();
            TypeQualified tqual = null;
            if (((this.token.value.value & 0xFF) == 97))
            {
                id = Id.empty.value;
            }
            else
            {
                if (((this.token.value.value & 0xFF) == 39))
                {
                    tqual = this.parseTypeof();
                    this.check(TOK.dot);
                }
                if (((this.token.value.value & 0xFF) != 120))
                {
                    this.error(new BytePtr("identifier expected, not `%s`"), this.token.value.toChars());
                    id = Id.empty.value;
                }
                else
                {
                    id = this.token.value.ident;
                }
                this.nextToken();
            }
            for (; 1 != 0;){
                tiargs = null;
                if (((this.token.value.value & 0xFF) == 91))
                {
                    tiargs = this.parseTemplateArguments();
                }
                if ((tiargs != null) && ((this.token.value.value & 0xFF) == 97))
                {
                    TemplateInstance tempinst = new TemplateInstance(loc, id, tiargs);
                    if (tqual == null)
                    {
                        tqual = new TypeInstance(loc, tempinst);
                    }
                    else
                    {
                        tqual.addInst(tempinst);
                    }
                    tiargs = null;
                }
                else
                {
                    if (tqual == null)
                    {
                        tqual = new TypeIdentifier(loc, id);
                    }
                    else
                    {
                        tqual.addIdent(id);
                    }
                }
                if (((this.token.value.value & 0xFF) != 97))
                {
                    break;
                }
                this.nextToken();
                if (((this.token.value.value & 0xFF) != 120))
                {
                    this.error(new BytePtr("identifier expected following `.` instead of `%s`"), this.token.value.toChars());
                    break;
                }
                loc = this.token.value.loc.copy();
                id = this.token.value.ident;
                this.nextToken();
            }
            id = null;
            if (((this.token.value.value & 0xFF) == 120))
            {
                id = this.token.value.ident;
                this.nextToken();
            }
            tm = new TemplateMixin(locMixin, id, tqual, tiargs);
            if (((this.token.value.value & 0xFF) != 9))
            {
                this.error(new BytePtr("`;` expected after mixin"));
            }
            this.nextToken();
            return tm;
        }

        public  Ptr<DArray<RootObject>> parseTemplateArguments() {
            Ptr<DArray<RootObject>> tiargs = null;
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1))
            {
                tiargs = this.parseTemplateArgumentList();
            }
            else
            {
                tiargs = this.parseTemplateSingleArgument();
            }
            if (((this.token.value.value & 0xFF) == 91))
            {
                byte tok = this.peekNext();
                if (((tok & 0xFF) != 63) && ((tok & 0xFF) != 175))
                {
                    this.error(new BytePtr("multiple ! arguments are not allowed"));
                    while(true) try {
                    /*Lagain:*/
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 1))
                        {
                            this.parseTemplateArgumentList();
                        }
                        else
                        {
                            this.parseTemplateSingleArgument();
                        }
                        if (((this.token.value.value & 0xFF) == 91) && (((tok = this.peekNext()) & 0xFF) != 63) && ((tok & 0xFF) != 175))
                        {
                            /*goto Lagain*/throw Dispatch0.INSTANCE;
                        }
                        break;
                    } catch(Dispatch0 __d){}
                }
            }
            return tiargs;
        }

        public  Ptr<DArray<RootObject>> parseTemplateArgumentList() {
            Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
            byte endtok = TOK.rightParentheses;
            assert(((this.token.value.value & 0xFF) == 1) || ((this.token.value.value & 0xFF) == 99));
            this.nextToken();
            for (; ((this.token.value.value & 0xFF) != (endtok & 0xFF));){
                if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                {
                    Type ta = this.parseType(null, null);
                    (tiargs.get()).push(ta);
                }
                else
                {
                    Expression ea = this.parseAssignExp();
                    (tiargs.get()).push(ea);
                }
                if (((this.token.value.value & 0xFF) != 99))
                {
                    break;
                }
                this.nextToken();
            }
            this.check(endtok, new BytePtr("template argument list"));
            return tiargs;
        }

        public  Ptr<DArray<RootObject>> parseTemplateSingleArgument() {
            Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
            Type ta = null;
            {
                int __dispatch63 = 0;
                dispatched_63:
                do {
                    switch (__dispatch63 != 0 ? __dispatch63 : (this.token.value.value & 0xFF))
                    {
                        case 120:
                            ta = new TypeIdentifier(this.token.value.loc, this.token.value.ident);
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 229:
                            ta = this.parseVector();
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 128:
                            ta = Type.tvoid.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 129:
                            ta = Type.tint8.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 130:
                            ta = Type.tuns8.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 131:
                            ta = Type.tint16.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 132:
                            ta = Type.tuns16.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 133:
                            ta = Type.tint32.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 134:
                            ta = Type.tuns32.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 135:
                            ta = Type.tint64.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 136:
                            ta = Type.tuns64;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 137:
                            ta = Type.tint128;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 138:
                            ta = Type.tuns128;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 139:
                            ta = Type.tfloat32.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 140:
                            ta = Type.tfloat64.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 141:
                            ta = Type.tfloat80.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 142:
                            ta = Type.timaginary32.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 143:
                            ta = Type.timaginary64.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 144:
                            ta = Type.timaginary80.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 145:
                            ta = Type.tcomplex32;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 146:
                            ta = Type.tcomplex64;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 147:
                            ta = Type.tcomplex80;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 151:
                            ta = Type.tbool.value;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 148:
                            ta = Type.tchar;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 149:
                            ta = Type.twchar;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        case 150:
                            ta = Type.tdchar;
                            /*goto LabelX*/{ __dispatch63 = -1; continue dispatched_63; }
                        /*LabelX:*/
                        case -1:
                        __dispatch63 = 0;
                            (tiargs.get()).push(ta);
                            this.nextToken();
                            break;
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 13:
                        case 15:
                        case 16:
                        case 117:
                        case 118:
                        case 119:
                        case 121:
                        case 122:
                        case 219:
                        case 220:
                        case 218:
                        case 221:
                        case 222:
                        case 223:
                        case 123:
                            Expression ea = this.parsePrimaryExp();
                            (tiargs.get()).push(ea);
                            break;
                        default:
                        this.error(new BytePtr("template argument expected following `!`"));
                        break;
                    }
                } while(__dispatch63 != 0);
            }
            return tiargs;
        }

        public  StaticAssert parseStaticAssert() {
            Loc loc = this.token.value.loc.copy();
            Expression exp = null;
            Expression msg = null;
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            exp = this.parseAssignExp();
            if (((this.token.value.value & 0xFF) == 99))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) != 2))
                {
                    msg = this.parseAssignExp();
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                    }
                }
            }
            this.check(TOK.rightParentheses);
            this.check(TOK.semicolon);
            return new StaticAssert(loc, exp, msg);
        }

        public  TypeQualified parseTypeof() {
            TypeQualified t = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            this.check(TOK.leftParentheses);
            if (((this.token.value.value & 0xFF) == 195))
            {
                this.nextToken();
                t = new TypeReturn(loc);
            }
            else
            {
                Expression exp = this.parseExpression();
                t = new TypeTypeof(loc, exp);
            }
            this.check(TOK.rightParentheses);
            return t;
        }

        public  Type parseVector() {
            this.nextToken();
            this.check(TOK.leftParentheses);
            Type tb = this.parseType(null, null);
            this.check(TOK.rightParentheses);
            return new TypeVector(tb);
        }

        public  int parseLinkage(Ptr<Ptr<DArray<Identifier>>> pidents, Ptr<Ptr<DArray<Expression>>> pIdentExps, IntRef cppmangle, Ref<Boolean> cppMangleOnly) {
            cppmangle.value = CPPMANGLE.def;
            cppMangleOnly.value = false;
            Ptr<DArray<Identifier>> idents = null;
            Ptr<DArray<Expression>> identExps = null;
            cppmangle.value = CPPMANGLE.def;
            int link = LINK.d;
            this.nextToken();
            assert(((this.token.value.value & 0xFF) == 1));
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 120))
            {
                Identifier id = this.token.value.ident;
                this.nextToken();
                if ((pequals(id, Id.Windows)))
                {
                    link = LINK.windows;
                }
                else if ((pequals(id, Id.Pascal)))
                {
                    this.deprecation(new BytePtr("`extern(Pascal)` is deprecated. You might want to use `extern(Windows)` instead."));
                    link = LINK.pascal;
                }
                else if ((pequals(id, Id.D)))
                {
                }
                else if ((pequals(id, Id.C)))
                {
                    link = LINK.c;
                    if (((this.token.value.value & 0xFF) == 93))
                    {
                        link = LINK.cpp;
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 99))
                        {
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 153) || ((this.token.value.value & 0xFF) == 152))
                            {
                                cppmangle.value = ((this.token.value.value & 0xFF) == 153) ? CPPMANGLE.asClass : CPPMANGLE.asStruct;
                                this.nextToken();
                            }
                            else if (((this.token.value.value & 0xFF) == 120))
                            {
                                idents = refPtr(new DArray<Identifier>());
                                for (; 1 != 0;){
                                    Identifier idn = this.token.value.ident;
                                    (idents.get()).push(idn);
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 97))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) == 120))
                                        {
                                            continue;
                                        }
                                        this.error(new BytePtr("identifier expected for C++ namespace"));
                                        idents = null;
                                    }
                                    break;
                                }
                            }
                            else
                            {
                                cppMangleOnly.value = true;
                                identExps = refPtr(new DArray<Expression>());
                                for (; 1 != 0;){
                                    (identExps.get()).push(this.parseCondExp());
                                    if (((this.token.value.value & 0xFF) != 99))
                                    {
                                        break;
                                    }
                                    this.nextToken();
                                }
                            }
                        }
                    }
                }
                else if ((pequals(id, Id.Objective)))
                {
                    if (((this.token.value.value & 0xFF) == 75))
                    {
                        this.nextToken();
                        if ((pequals(this.token.value.ident, Id.C)))
                        {
                            link = LINK.objc;
                            this.nextToken();
                        }
                        else
                        {
                            /*goto LinvalidLinkage*//*unrolled goto*/
                            link = LINK.system;
                        }
                    }
                    else
                    {
                        /*goto LinvalidLinkage*//*unrolled goto*/
                        link = LINK.system;
                    }
                }
                else if ((pequals(id, Id.System)))
                {
                    link = LINK.system;
                }
                else
                {
                /*LinvalidLinkage:*/
                    this.error(new BytePtr("valid linkage identifiers are `D`, `C`, `C++`, `Objective-C`, `Pascal`, `Windows`, `System`"));
                    link = LINK.d;
                }
            }
            this.check(TOK.rightParentheses);
            pidents.set(0, idents);
            pIdentExps.set(0, identExps);
            return link;
        }

        public  Ptr<DArray<Identifier>> parseQualifiedIdentifier(BytePtr entity) {
            Ptr<DArray<Identifier>> qualified = null;
            do {
                {
                    this.nextToken();
                    if (((this.token.value.value & 0xFF) != 120))
                    {
                        this.error(new BytePtr("`%s` expected as dot-separated identifiers, got `%s`"), entity, this.token.value.toChars());
                        return null;
                    }
                    Identifier id = this.token.value.ident;
                    if (qualified == null)
                    {
                        qualified = refPtr(new DArray<Identifier>());
                    }
                    (qualified.get()).push(id);
                    this.nextToken();
                }
            } while (((this.token.value.value & 0xFF) == 97));
            return qualified;
        }

        public  Condition parseDebugCondition() {
            int level = 1;
            Identifier id = null;
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 120))
                {
                    id = this.token.value.ident;
                }
                else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                {
                    level = (int)this.token.value.intvalue;
                }
                else
                {
                    this.error(new BytePtr("identifier or integer expected inside debug(...), not `%s`"), this.token.value.toChars());
                }
                this.nextToken();
                this.check(TOK.rightParentheses);
            }
            return new DebugCondition(this.mod, level, id);
        }

        public  Condition parseVersionCondition() {
            int level = 1;
            Identifier id = null;
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 120))
                {
                    id = this.token.value.ident;
                }
                else if (((this.token.value.value & 0xFF) == 105) || ((this.token.value.value & 0xFF) == 107))
                {
                    level = (int)this.token.value.intvalue;
                }
                else if (((this.token.value.value & 0xFF) == 208))
                {
                    id = Identifier.idPool(Token.asString(TOK.unittest_));
                }
                else if (((this.token.value.value & 0xFF) == 14))
                {
                    id = Identifier.idPool(Token.asString(TOK.assert_));
                }
                else
                {
                    this.error(new BytePtr("identifier or integer expected inside version(...), not `%s`"), this.token.value.toChars());
                }
                this.nextToken();
                this.check(TOK.rightParentheses);
            }
            else
            {
                this.error(new BytePtr("(condition) expected following `version`"));
            }
            return new VersionCondition(this.mod, level, id);
        }

        public  Condition parseStaticIfCondition() {
            Expression exp = null;
            Condition condition = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                exp = this.parseAssignExp();
                this.check(TOK.rightParentheses);
            }
            else
            {
                this.error(new BytePtr("(expression) expected following `static if`"));
                exp = null;
            }
            condition = new StaticIfCondition(loc, exp);
            return condition;
        }

        public  Dsymbol parseCtor(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Ptr<DArray<Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1) && ((this.peekNext() & 0xFF) == 123) && ((this.peekNext2() & 0xFF) == 2))
            {
                this.nextToken();
                this.nextToken();
                this.check(TOK.rightParentheses);
                stc = this.parsePostfix(stc, ptr(udas));
                if ((stc & 1048576L) != 0)
                {
                    this.deprecation(new BytePtr("`immutable` postblit is deprecated. Please use an unqualified postblit."));
                }
                if ((stc & 536870912L) != 0)
                {
                    this.deprecation(new BytePtr("`shared` postblit is deprecated. Please use an unqualified postblit."));
                }
                if ((stc & 4L) != 0)
                {
                    this.deprecation(new BytePtr("`const` postblit is deprecated. Please use an unqualified postblit."));
                }
                if ((stc & 1L) != 0)
                {
                    this.error(loc, new BytePtr("postblit cannot be `static`"));
                }
                PostBlitDeclaration f = new PostBlitDeclaration(loc, Loc.initial.value, stc, Id.postblit.value);
                Dsymbol s = this.parseContracts(f);
                if (udas != null)
                {
                    Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                    (a.get()).push(f);
                    s = new UserAttributeDeclaration(udas, a);
                }
                return s;
            }
            Ptr<DArray<TemplateParameter>> tpl = null;
            if (((this.token.value.value & 0xFF) == 1) && (((this.peekPastParen(ptr(this.token)).get()).value & 0xFF) == 1))
            {
                tpl = this.parseTemplateParameterList(0);
            }
            int varargs = VarArg.none;
            Ptr<DArray<Parameter>> parameters = this.parseParameters(ptr(varargs), null);
            stc = this.parsePostfix(stc, ptr(udas));
            if ((varargs != VarArg.none) || (Parameter.dim(parameters) != 0))
            {
                if ((stc & 1L) != 0)
                {
                    this.error(loc, new BytePtr("constructor cannot be static"));
                }
            }
            else {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    if ((ss == 1L))
                    {
                        this.error(loc, new BytePtr("use `static this()` to declare a static constructor"));
                    }
                    else if ((ss == 536870913L))
                    {
                        this.error(loc, new BytePtr("use `shared static this()` to declare a shared static constructor"));
                    }
                }
            }
            Expression constraint = tpl != null ? this.parseConstraint() : null;
            Type tf = new TypeFunction(new ParameterList(parameters, varargs), null, this.linkage, stc);
            tf = tf.addSTC(stc);
            CtorDeclaration f = new CtorDeclaration(loc, Loc.initial.value, stc, tf, false);
            Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                (a.get()).push(f);
                s = new UserAttributeDeclaration(udas, a);
            }
            if (tpl != null)
            {
                Ptr<DArray<Dsymbol>> decldefs = refPtr(new DArray<Dsymbol>());
                (decldefs.get()).push(s);
                s = new TemplateDeclaration(loc, f.ident.value, tpl, constraint, decldefs, false, false);
            }
            return s;
        }

        public  Dsymbol parseDtor(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Ptr<DArray<Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            this.check(TOK.this_);
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc, ptr(udas));
            {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    if ((ss == 1L))
                    {
                        this.error(loc, new BytePtr("use `static ~this()` to declare a static destructor"));
                    }
                    else if ((ss == 536870913L))
                    {
                        this.error(loc, new BytePtr("use `shared static ~this()` to declare a shared static destructor"));
                    }
                }
            }
            DtorDeclaration f = new DtorDeclaration(loc, Loc.initial.value, stc, Id.dtor.value);
            Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                (a.get()).push(f);
                s = new UserAttributeDeclaration(udas, a);
            }
            return s;
        }

        public  Dsymbol parseStaticCtor(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, null) | stc;
            if ((stc & 536870912L) != 0)
            {
                this.error(loc, new BytePtr("use `shared static this()` to declare a shared static constructor"));
            }
            else if ((stc & 1L) != 0)
            {
                this.appendStorageClass(stc, 1L);
            }
            else {
                long modStc = stc & 2685403140L;
                if ((modStc) != 0)
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        stcToBuffer(ptr(buf), modStc);
                        this.error(loc, new BytePtr("static constructor cannot be `%s`"), buf.peekChars());
                    }
                    finally {
                    }
                }
            }
            stc &= -2685403142L;
            StaticCtorDeclaration f = new StaticCtorDeclaration(loc, Loc.initial.value, stc);
            Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  Dsymbol parseStaticDtor(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Ptr<DArray<Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            this.nextToken();
            this.check(TOK.this_);
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, ptr(udas)) | stc;
            if ((stc & 536870912L) != 0)
            {
                this.error(loc, new BytePtr("use `shared static ~this()` to declare a shared static destructor"));
            }
            else if ((stc & 1L) != 0)
            {
                this.appendStorageClass(stc, 1L);
            }
            else {
                long modStc = stc & 2685403140L;
                if ((modStc) != 0)
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        stcToBuffer(ptr(buf), modStc);
                        this.error(loc, new BytePtr("static destructor cannot be `%s`"), buf.peekChars());
                    }
                    finally {
                    }
                }
            }
            stc &= -2685403142L;
            StaticDtorDeclaration f = new StaticDtorDeclaration(loc, Loc.initial.value, stc);
            Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                (a.get()).push(f);
                s = new UserAttributeDeclaration(udas, a);
            }
            return s;
        }

        public  Dsymbol parseSharedStaticCtor(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, null) | stc;
            {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    this.appendStorageClass(stc, ss);
                }
                else {
                    long modStc = stc & 2685403140L;
                    if ((modStc) != 0)
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            stcToBuffer(ptr(buf), modStc);
                            this.error(loc, new BytePtr("shared static constructor cannot be `%s`"), buf.peekChars());
                        }
                        finally {
                        }
                    }
                }
            }
            stc &= -2685403142L;
            SharedStaticCtorDeclaration f = new SharedStaticCtorDeclaration(loc, Loc.initial.value, stc);
            Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  Dsymbol parseSharedStaticDtor(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Ptr<DArray<Expression>> udas = null;
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            this.nextToken();
            this.nextToken();
            this.check(TOK.this_);
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, ptr(udas)) | stc;
            {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    this.appendStorageClass(stc, ss);
                }
                else {
                    long modStc = stc & 2685403140L;
                    if ((modStc) != 0)
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            stcToBuffer(ptr(buf), modStc);
                            this.error(loc, new BytePtr("shared static destructor cannot be `%s`"), buf.peekChars());
                        }
                        finally {
                        }
                    }
                }
            }
            stc &= -2685403142L;
            SharedStaticDtorDeclaration f = new SharedStaticDtorDeclaration(loc, Loc.initial.value, stc);
            Dsymbol s = this.parseContracts(f);
            if (udas != null)
            {
                Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                (a.get()).push(f);
                s = new UserAttributeDeclaration(udas, a);
            }
            return s;
        }

        public  Dsymbol parseInvariant(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            if (((this.token.value.value & 0xFF) == 1))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) != 2))
                {
                    Expression e = this.parseAssignExp();
                    Expression msg = null;
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 2))
                        {
                            msg = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 99))
                            {
                                this.nextToken();
                            }
                        }
                    }
                    this.check(TOK.rightParentheses);
                    this.check(TOK.semicolon);
                    e = new AssertExp(loc, e, msg);
                    ExpStatement fbody = new ExpStatement(loc, e);
                    InvariantDeclaration f = new InvariantDeclaration(loc, this.token.value.loc, stc, null, fbody);
                    return f;
                }
                this.nextToken();
            }
            Statement fbody = this.parseStatement(4, null, null);
            InvariantDeclaration f = new InvariantDeclaration(loc, this.token.value.loc, stc, null, fbody);
            return f;
        }

        public  Dsymbol parseUnitTest(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            BytePtr begPtr = pcopy(this.token.value.ptr.plus(1));
            BytePtr endPtr = null;
            Statement sbody = this.parseStatement(4, ptr(endPtr), null);
            BytePtr docline = null;
            if (global.params.doDocComments && (endPtr.greaterThan(begPtr)))
            {
                {
                    BytePtr p = pcopy(endPtr.minus(1));
                    for (; (begPtr.lessOrEqual(p)) && ((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 13) || ((p.get() & 0xFF) == 10) || ((p.get() & 0xFF) == 9);p.minusAssign(1)){
                        endPtr = pcopy(p);
                    }
                }
                int len = ((endPtr.minus(begPtr)));
                if ((len > 0))
                {
                    docline = pcopy((((BytePtr)Mem.xmalloc(len + 2))));
                    memcpy((BytePtr)(docline), (begPtr), len);
                    docline.set(len, (byte)10);
                    docline.set((len + 1), (byte)0);
                }
            }
            UnitTestDeclaration f = new UnitTestDeclaration(loc, this.token.value.loc, stc, docline);
            f.fbody.value = sbody;
            return f;
        }

        public  Dsymbol parseNew(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            int varargs = VarArg.none;
            Ptr<DArray<Parameter>> parameters = this.parseParameters(ptr(varargs), null);
            NewDeclaration f = new NewDeclaration(loc, Loc.initial.value, stc, parameters, varargs);
            Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  Dsymbol parseDelete(Ptr<PrefixAttributesASTCodegen> pAttrs) {
            Loc loc = this.token.value.loc.copy();
            long stc = getStorageClassASTCodegen(pAttrs);
            this.nextToken();
            int varargs = VarArg.none;
            Ptr<DArray<Parameter>> parameters = this.parseParameters(ptr(varargs), null);
            if ((varargs != VarArg.none))
            {
                this.error(new BytePtr("`...` not allowed in delete function parameter list"));
            }
            DeleteDeclaration f = new DeleteDeclaration(loc, Loc.initial.value, stc, parameters);
            Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  Ptr<DArray<Parameter>> parseParameters(IntPtr pvarargs, Ptr<Ptr<DArray<TemplateParameter>>> tpl) {
            Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
            int varargs = VarArg.none;
            int hasdefault = 0;
            this.check(TOK.leftParentheses);
        L_outer27:
            for (; 1 != 0;){
                Identifier ai = null;
                Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                Expression ae = null;
                Ptr<DArray<Expression>> udas = null;
                try {
                L_outer28:
                    for (; 1 != 0;this.nextToken()){
                    /*L3:*/
                        {
                            int __dispatch64 = 0;
                            dispatched_64:
                            do {
                                switch (__dispatch64 != 0 ? __dispatch64 : (this.token.value.value & 0xFF))
                                {
                                    case 2:
                                        if ((storageClass != 0L) || (udas != null))
                                        {
                                            this.error(new BytePtr("basic type expected, not `)`"));
                                        }
                                        break;
                                    case 10:
                                        varargs = VarArg.variadic;
                                        this.nextToken();
                                        break;
                                    case 171:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch64 = -3; continue dispatched_64; }
                                        }
                                        stc = 4L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 182:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch64 = -3; continue dispatched_64; }
                                        }
                                        stc = 1048576L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 224:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch64 = -3; continue dispatched_64; }
                                        }
                                        stc = 536870912L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 177:
                                        if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                        {
                                            /*goto default*/ { __dispatch64 = -3; continue dispatched_64; }
                                        }
                                        stc = 2147483648L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 225:
                                        Ptr<DArray<Expression>> exps = null;
                                        long stc2 = this.parseAttribute(ptr(exps));
                                        if ((stc2 == 4294967296L) || (stc2 == 4398046511104L) || (stc2 == 137438953472L) || (stc2 == 8589934592L) || (stc2 == 17179869184L) || (stc2 == 34359738368L))
                                        {
                                            this.error(new BytePtr("`@%s` attribute for function parameter is not supported"), this.token.value.toChars());
                                        }
                                        else
                                        {
                                            udas = UserAttributeDeclaration.concat(udas, exps);
                                        }
                                        if (((this.token.value.value & 0xFF) == 10))
                                        {
                                            this.error(new BytePtr("variadic parameter cannot have user-defined attributes"));
                                        }
                                        if (stc2 != 0)
                                        {
                                            this.nextToken();
                                        }
                                        /*goto L3*/throw Dispatch0.INSTANCE;
                                    case 175:
                                        stc = 2048L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 176:
                                        stc = 4096L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 210:
                                        stc = 2097152L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 178:
                                        stc = 8192L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 203:
                                        stc = 524288L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 170:
                                        stc = 8L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 179:
                                        stc = 256L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    case 195:
                                        stc = 17592186044416L;
                                        /*goto L2*/{ __dispatch64 = -1; continue dispatched_64; }
                                    /*L2:*/
                                    case -1:
                                    __dispatch64 = 0;
                                        storageClass = this.appendStorageClass(storageClass, stc);
                                        continue L_outer28;
                                    default:
                                    __dispatch64 = 0;
                                    stc = storageClass & 2111488L;
                                    if (((stc & stc - 1L) != 0) && !(stc == 2099200L))
                                    {
                                        this.error(new BytePtr("incompatible parameter storage classes"));
                                    }
                                    if ((tpl != null) && ((this.token.value.value & 0xFF) == 120))
                                    {
                                        Ptr<Token> t = this.peek(ptr(this.token));
                                        if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 2) || (((t.get()).value & 0xFF) == 10))
                                        {
                                            Identifier id = Identifier.generateId(new BytePtr("__T"));
                                            Loc loc = this.token.value.loc.copy();
                                            at = new TypeIdentifier(loc, id);
                                            if (tpl.get() == null)
                                            {
                                                tpl.set(0, (refPtr(new DArray<TemplateParameter>())));
                                            }
                                            TemplateParameter tp = new TemplateTypeParameter(loc, id, null, null);
                                            (tpl.get().get()).push(tp);
                                            ai = this.token.value.ident;
                                            this.nextToken();
                                        }
                                        else
                                        {
                                            /*goto _else*/{ __dispatch64 = -2; continue dispatched_64; }
                                        }
                                    }
                                    else
                                    {
                                    /*_else:*/
                                    case -2:
                                    __dispatch64 = 0;
                                        at = this.parseType(ptr(ai), null);
                                    }
                                    ae = null;
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        ae = this.parseDefaultInitExp();
                                        hasdefault = 1;
                                    }
                                    else
                                    {
                                        if (hasdefault != 0)
                                        {
                                            this.error(new BytePtr("default argument expected for `%s`"), ai != null ? ai.toChars() : at.toChars());
                                        }
                                    }
                                    Parameter param = new Parameter(storageClass, at, ai, ae, null);
                                    if (udas != null)
                                    {
                                        Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                                        UserAttributeDeclaration udad = new UserAttributeDeclaration(udas, a);
                                        param.userAttribDecl.value = udad;
                                    }
                                    if (((this.token.value.value & 0xFF) == 225))
                                    {
                                        Ptr<DArray<Expression>> exps_1 = null;
                                        long stc2_1 = this.parseAttribute(ptr(exps_1));
                                        if ((stc2_1 == 4294967296L) || (stc2_1 == 4398046511104L) || (stc2_1 == 137438953472L) || (stc2_1 == 8589934592L) || (stc2_1 == 17179869184L) || (stc2_1 == 34359738368L))
                                        {
                                            this.error(new BytePtr("`@%s` attribute for function parameter is not supported"), this.token.value.toChars());
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("user-defined attributes cannot appear as postfixes"), this.token.value.toChars());
                                        }
                                        if (stc2_1 != 0)
                                        {
                                            this.nextToken();
                                        }
                                    }
                                    if (((this.token.value.value & 0xFF) == 10))
                                    {
                                        if ((storageClass & 2101248L) != 0)
                                        {
                                            this.error(new BytePtr("variadic argument cannot be `out` or `ref`"));
                                        }
                                        varargs = VarArg.typesafe;
                                        (parameters.get()).push(param);
                                        this.nextToken();
                                        break;
                                    }
                                    (parameters.get()).push(param);
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        this.nextToken();
                                        /*goto L1*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                }
                            } while(__dispatch64 != 0);
                        }
                        break;
                    }
                    break;
                }
                catch(Dispatch0 __d){}
            /*L1:*/
            }
            this.check(TOK.rightParentheses);
            pvarargs.set(0, varargs);
            return parameters;
        }

        // defaulted all parameters starting with #2
        public  Ptr<DArray<Parameter>> parseParameters(IntPtr pvarargs) {
            return parseParameters(pvarargs, null);
        }

        public  EnumDeclaration parseEnum() {
            EnumDeclaration e = null;
            Identifier id = null;
            Type memtype = null;
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            id = null;
            if (((this.token.value.value & 0xFF) == 120))
            {
                id = this.token.value.ident;
                this.nextToken();
            }
            memtype = null;
            if (((this.token.value.value & 0xFF) == 7))
            {
                this.nextToken();
                int alt = 0;
                Loc typeLoc = this.token.value.loc.copy();
                memtype = this.parseBasicType(false);
                memtype = this.parseDeclarator(memtype, ptr(alt), null, null, 0L, null, null);
                this.checkCstyleTypeSyntax(typeLoc, memtype, alt, null);
            }
            e = new EnumDeclaration(loc, id, memtype);
            if (((this.token.value.value & 0xFF) == 9) && (id != null))
            {
                this.nextToken();
            }
            else if (((this.token.value.value & 0xFF) == 5))
            {
                boolean isAnonymousEnum = id == null;
                e.members.value = refPtr(new DArray<Dsymbol>());
                this.nextToken();
                BytePtr comment = pcopy(this.token.value.blockComment.value);
            L_outer29:
                for (; ((this.token.value.value & 0xFF) != 6);){
                    loc = this.token.value.loc.copy();
                    Type type = null;
                    Identifier ident = null;
                    Ptr<DArray<Expression>> udas = null;
                    long stc = 0L;
                    Expression deprecationMessage = null;
                    ByteSlice attributeErrorMessage = new ByteSlice("`%s` is not a valid attribute for enum members");
                L_outer30:
                    for (; ((this.token.value.value & 0xFF) != 6) && ((this.token.value.value & 0xFF) != 99) && ((this.token.value.value & 0xFF) != 90);){
                        {
                            int __dispatch65 = 0;
                            dispatched_65:
                            do {
                                switch (__dispatch65 != 0 ? __dispatch65 : (this.token.value.value & 0xFF))
                                {
                                    case 225:
                                        {
                                            long _stc = this.parseAttribute(ptr(udas));
                                            if ((_stc) != 0)
                                            {
                                                if ((_stc == 137438953472L))
                                                {
                                                    stc |= _stc;
                                                }
                                                else
                                                {
                                                    OutBuffer buf = new OutBuffer();
                                                    try {
                                                        stcToBuffer(ptr(buf), _stc);
                                                        this.error(new BytePtr("`%s` is not a valid attribute for enum members"), buf.peekChars());
                                                    }
                                                    finally {
                                                    }
                                                }
                                                this.nextToken();
                                            }
                                        }
                                        break;
                                    case 174:
                                        {
                                            long _stc = this.parseDeprecatedAttribute(deprecationMessage);
                                            if ((_stc) != 0)
                                            {
                                                stc |= _stc;
                                                this.nextToken();
                                            }
                                        }
                                        break;
                                    case 120:
                                        Ptr<Token> tp = this.peek(ptr(this.token));
                                        if ((((tp.get()).value & 0xFF) == 90) || (((tp.get()).value & 0xFF) == 99) || (((tp.get()).value & 0xFF) == 6))
                                        {
                                            ident = this.token.value.ident;
                                            type = null;
                                            this.nextToken();
                                        }
                                        else
                                        {
                                            /*goto default*/ { __dispatch65 = -1; continue dispatched_65; }
                                        }
                                        break;
                                    default:
                                    __dispatch65 = 0;
                                    if (isAnonymousEnum)
                                    {
                                        type = this.parseType(ptr(ident), null);
                                        if ((pequals(type, Type.terror.value)))
                                        {
                                            type = null;
                                            this.nextToken();
                                        }
                                    }
                                    else
                                    {
                                        this.error(new BytePtr("`%s` is not a valid attribute for enum members"), this.token.value.toChars());
                                        this.nextToken();
                                    }
                                    break;
                                }
                            } while(__dispatch65 != 0);
                        }
                    }
                    if ((type != null) && (!pequals(type, Type.terror.value)))
                    {
                        if (ident == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), type.toChars());
                        }
                        if (!isAnonymousEnum)
                        {
                            this.error(new BytePtr("type only allowed if anonymous enum and no enum type"));
                        }
                    }
                    Expression value = null;
                    if (((this.token.value.value & 0xFF) == 90))
                    {
                        this.nextToken();
                        value = this.parseAssignExp();
                    }
                    else
                    {
                        value = null;
                        if ((type != null) && (!pequals(type, Type.terror.value)) && isAnonymousEnum)
                        {
                            this.error(new BytePtr("if type, there must be an initializer"));
                        }
                    }
                    UserAttributeDeclaration uad = null;
                    if (udas != null)
                    {
                        uad = new UserAttributeDeclaration(udas, null);
                    }
                    DeprecatedDeclaration dd = null;
                    if (deprecationMessage != null)
                    {
                        dd = new DeprecatedDeclaration(deprecationMessage, null);
                        stc |= 1024L;
                    }
                    EnumMember em = new EnumMember(loc, ident, value, type, stc, uad, dd);
                    (e.members.value.get()).push(em);
                    if (((this.token.value.value & 0xFF) == 6))
                    {
                    }
                    else
                    {
                        this.addComment(em, comment);
                        comment = null;
                        this.check(TOK.comma);
                    }
                    this.addComment(em, comment);
                    comment = pcopy(this.token.value.blockComment.value);
                    if (((this.token.value.value & 0xFF) == 11))
                    {
                        this.error(new BytePtr("premature end of file"));
                        break;
                    }
                }
                this.nextToken();
            }
            else
            {
                this.error(new BytePtr("enum declaration is invalid"));
            }
            return e;
        }

        public  Dsymbol parseAggregate() {
            Ptr<DArray<TemplateParameter>> tpl = null;
            Expression constraint = null;
            Loc loc = this.token.value.loc.copy();
            byte tok = this.token.value.value;
            this.nextToken();
            Identifier id = null;
            if (((this.token.value.value & 0xFF) != 120))
            {
                id = null;
            }
            else
            {
                id = this.token.value.ident;
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 1))
                {
                    tpl = this.parseTemplateParameterList(0);
                    constraint = this.parseConstraint();
                }
            }
            Ptr<DArray<Ptr<BaseClass>>> baseclasses = null;
            if (((this.token.value.value & 0xFF) == 7))
            {
                if (((tok & 0xFF) != 154) && ((tok & 0xFF) != 153))
                {
                    this.error(new BytePtr("base classes are not allowed for `%s`, did you mean `;`?"), Token.toChars(tok));
                }
                this.nextToken();
                baseclasses = this.parseBaseClasses();
            }
            if (((this.token.value.value & 0xFF) == 183))
            {
                if (constraint != null)
                {
                    this.error(new BytePtr("template constraints appear both before and after BaseClassList, put them before"));
                }
                constraint = this.parseConstraint();
            }
            if (constraint != null)
            {
                if (id == null)
                {
                    this.error(new BytePtr("template constraints not allowed for anonymous `%s`"), Token.toChars(tok));
                }
                if (tpl == null)
                {
                    this.error(new BytePtr("template constraints only allowed for templates"));
                }
            }
            Ptr<DArray<Dsymbol>> members = null;
            if (((this.token.value.value & 0xFF) == 5))
            {
                Loc lookingForElseSave = this.lookingForElse.copy();
                this.lookingForElse = new Loc(null, 0, 0).copy();
                this.nextToken();
                members = this.parseDeclDefs(0, null, null);
                this.lookingForElse = lookingForElseSave.copy();
                if (((this.token.value.value & 0xFF) != 6))
                {
                    this.error(new BytePtr("`}` expected following members in `%s` declaration at %s"), Token.toChars(tok), loc.toChars(global.params.showColumns.value));
                }
                this.nextToken();
            }
            else if (((this.token.value.value & 0xFF) == 9) && (id != null))
            {
                if ((baseclasses != null) || (constraint != null))
                {
                    this.error(new BytePtr("members expected"));
                }
                this.nextToken();
            }
            else
            {
                this.error(new BytePtr("{ } expected following `%s` declaration"), Token.toChars(tok));
            }
            AggregateDeclaration a = null;
            switch ((tok & 0xFF))
            {
                case 154:
                    if (id == null)
                    {
                        this.error(loc, new BytePtr("anonymous interfaces not allowed"));
                    }
                    a = new InterfaceDeclaration(loc, id, baseclasses);
                    a.members.value = members;
                    break;
                case 153:
                    if (id == null)
                    {
                        this.error(loc, new BytePtr("anonymous classes not allowed"));
                    }
                    boolean inObject = (this.md != null) && ((this.md.get()).packages == null) && (pequals((this.md.get()).id, Id.object.value));
                    a = new ClassDeclaration(loc, id, baseclasses, members, inObject);
                    break;
                case 152:
                    if (id != null)
                    {
                        boolean inObject_1 = (this.md != null) && ((this.md.get()).packages == null) && (pequals((this.md.get()).id, Id.object.value));
                        a = new StructDeclaration(loc, id, inObject_1);
                        a.members.value = members;
                    }
                    else
                    {
                        assert(tpl == null);
                        return new AnonDeclaration(loc, false, members);
                    }
                    break;
                case 155:
                    if (id != null)
                    {
                        a = new UnionDeclaration(loc, id);
                        a.members.value = members;
                    }
                    else
                    {
                        assert(tpl == null);
                        return new AnonDeclaration(loc, true, members);
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            if (tpl != null)
            {
                Ptr<DArray<Dsymbol>> decldefs = refPtr(new DArray<Dsymbol>());
                (decldefs.get()).push(a);
                TemplateDeclaration tempdecl = new TemplateDeclaration(loc, id, tpl, constraint, decldefs, false, false);
                return tempdecl;
            }
            return a;
        }

        public  Ptr<DArray<Ptr<BaseClass>>> parseBaseClasses() {
            Ptr<DArray<Ptr<BaseClass>>> baseclasses = refPtr(new DArray<Ptr<BaseClass>>());
            for (; 1 != 0;this.nextToken()){
                Ptr<BaseClass> b = refPtr(new BaseClass(this.parseBasicType(false)));
                (baseclasses.get()).push(b);
                if (((this.token.value.value & 0xFF) != 99))
                {
                    break;
                }
            }
            return baseclasses;
        }

        public  Ptr<DArray<Dsymbol>> parseImport() {
            Ptr<DArray<Dsymbol>> decldefs = refPtr(new DArray<Dsymbol>());
            Identifier aliasid = null;
            int isstatic = (((this.token.value.value & 0xFF) == 169) ? 1 : 0);
            if (isstatic != 0)
            {
                this.nextToken();
            }
        L_outer31:
            do {
                {
                    while(true) try {
                    /*L1:*/
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 120))
                        {
                            this.error(new BytePtr("identifier expected following `import`"));
                            break;
                        }
                        Loc loc = this.token.value.loc.copy();
                        Identifier id = this.token.value.ident;
                        Ptr<DArray<Identifier>> a = null;
                        this.nextToken();
                        if ((aliasid == null) && ((this.token.value.value & 0xFF) == 90))
                        {
                            aliasid = id;
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        for (; ((this.token.value.value & 0xFF) == 97);){
                            if (a == null)
                            {
                                a = refPtr(new DArray<Identifier>());
                            }
                            (a.get()).push(id);
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("identifier expected following `package`"));
                                break;
                            }
                            id = this.token.value.ident;
                            this.nextToken();
                        }
                        Import s = new Import(loc, a, id, aliasid, isstatic);
                        (decldefs.get()).push(s);
                        if (((this.token.value.value & 0xFF) == 7))
                        {
                            do {
                                {
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) != 120))
                                    {
                                        this.error(new BytePtr("identifier expected following `:`"));
                                        break;
                                    }
                                    Identifier _alias = this.token.value.ident;
                                    Identifier name = null;
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 90))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 120))
                                        {
                                            this.error(new BytePtr("identifier expected following `%s=`"), _alias.toChars());
                                            break;
                                        }
                                        name = this.token.value.ident;
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        name = _alias;
                                        _alias = null;
                                    }
                                    s.addAlias(name, _alias);
                                }
                            } while (((this.token.value.value & 0xFF) == 99));
                            break;
                        }
                        aliasid = null;
                        break;
                    } catch(Dispatch0 __d){}
                }
            } while (((this.token.value.value & 0xFF) == 99));
            if (((this.token.value.value & 0xFF) == 9))
            {
                this.nextToken();
            }
            else
            {
                this.error(new BytePtr("`;` expected"));
                this.nextToken();
            }
            return decldefs;
        }

        public  Type parseType(Ptr<Identifier> pident, Ptr<Ptr<DArray<TemplateParameter>>> ptpl) {
            long stc = 0L;
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 171:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 4L;
                        this.nextToken();
                        continue;
                    case 182:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 1048576L;
                        this.nextToken();
                        continue;
                    case 224:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 536870912L;
                        this.nextToken();
                        continue;
                    case 177:
                        if (((this.peekNext() & 0xFF) == 1))
                        {
                            break;
                        }
                        stc |= 2147483648L;
                        this.nextToken();
                        continue;
                    default:
                    break;
                }
                break;
            }
            Loc typeLoc = this.token.value.loc.copy();
            Type t = null;
            t = this.parseBasicType(false);
            int alt = 0;
            t = this.parseDeclarator(t, ptr(alt), pident, ptpl, 0L, null, null);
            this.checkCstyleTypeSyntax(typeLoc, t, alt, pident != null ? pident.get() : null);
            t = t.addSTC(stc);
            return t;
        }

        // defaulted all parameters starting with #2
        public  Type parseType(Ptr<Identifier> pident) {
            return parseType(pident, null);
        }

        // defaulted all parameters starting with #1
        public  Type parseType() {
            return parseType(null, null);
        }

        public  Type parseBasicType(boolean dontLookDotIdents) {
            Type t = null;
            Loc loc = new Loc();
            Identifier id = null;
            {
                int __dispatch68 = 0;
                dispatched_68:
                do {
                    switch (__dispatch68 != 0 ? __dispatch68 : (this.token.value.value & 0xFF))
                    {
                        case 128:
                            t = Type.tvoid.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 129:
                            t = Type.tint8.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 130:
                            t = Type.tuns8.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 131:
                            t = Type.tint16.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 132:
                            t = Type.tuns16.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 133:
                            t = Type.tint32.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 134:
                            t = Type.tuns32.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 135:
                            t = Type.tint64.value;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 135))
                            {
                                this.error(new BytePtr("use `long` for a 64 bit integer instead of `long long`"));
                                this.nextToken();
                            }
                            else if (((this.token.value.value & 0xFF) == 140))
                            {
                                this.error(new BytePtr("use `real` instead of `long double`"));
                                t = Type.tfloat80.value;
                                this.nextToken();
                            }
                            break;
                        case 136:
                            t = Type.tuns64;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 137:
                            t = Type.tint128;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 138:
                            t = Type.tuns128;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 139:
                            t = Type.tfloat32.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 140:
                            t = Type.tfloat64.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 141:
                            t = Type.tfloat80.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 142:
                            t = Type.timaginary32.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 143:
                            t = Type.timaginary64.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 144:
                            t = Type.timaginary80.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 145:
                            t = Type.tcomplex32;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 146:
                            t = Type.tcomplex64;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 147:
                            t = Type.tcomplex80;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 151:
                            t = Type.tbool.value;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 148:
                            t = Type.tchar;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 149:
                            t = Type.twchar;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        case 150:
                            t = Type.tdchar;
                            /*goto LabelX*/{ __dispatch68 = -1; continue dispatched_68; }
                        /*LabelX:*/
                        case -1:
                        __dispatch68 = 0;
                            this.nextToken();
                            break;
                        case 123:
                        case 124:
                        case 120:
                            loc = this.token.value.loc.copy();
                            id = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 91))
                            {
                                TemplateInstance tempinst = new TemplateInstance(loc, id, this.parseTemplateArguments());
                                t = this.parseBasicTypeStartingAt(new TypeInstance(loc, tempinst), dontLookDotIdents);
                            }
                            else
                            {
                                t = this.parseBasicTypeStartingAt(new TypeIdentifier(loc, id), dontLookDotIdents);
                            }
                            break;
                        case 97:
                            t = this.parseBasicTypeStartingAt(new TypeIdentifier(this.token.value.loc, Id.empty.value), dontLookDotIdents);
                            break;
                        case 39:
                            t = this.parseBasicTypeStartingAt(this.parseTypeof(), dontLookDotIdents);
                            break;
                        case 229:
                            t = this.parseVector();
                            break;
                        case 213:
                            {
                                TraitsExp te = (TraitsExp)this.parsePrimaryExp();
                                if ((te) != null)
                                {
                                    if ((te.ident.value != null) && (te.args.value != null))
                                    {
                                        t = new TypeTraits(this.token.value.loc, te);
                                        break;
                                    }
                                }
                            }
                            t = new TypeError();
                            break;
                        case 171:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(4L);
                            this.check(TOK.rightParentheses);
                            break;
                        case 182:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(1048576L);
                            this.check(TOK.rightParentheses);
                            break;
                        case 224:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(536870912L);
                            this.check(TOK.rightParentheses);
                            break;
                        case 177:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            t = this.parseType(null, null).addSTC(2147483648L);
                            this.check(TOK.rightParentheses);
                            break;
                        default:
                        this.error(new BytePtr("basic type expected, not `%s`"), this.token.value.toChars());
                        if (((this.token.value.value & 0xFF) == 184))
                        {
                            this.errorSupplemental(this.token.value.loc, new BytePtr("There's no `static else`, use `else` instead."));
                        }
                        t = Type.terror.value;
                        break;
                    }
                } while(__dispatch68 != 0);
            }
            return t;
        }

        // defaulted all parameters starting with #1
        public  Type parseBasicType() {
            return parseBasicType(false);
        }

        public  Type parseBasicTypeStartingAt(TypeQualified tid, boolean dontLookDotIdents) {
            Type maybeArray = null;
            try {
            L_outer32:
                for (; 1 != 0;){
                    {
                        int __dispatch69 = 0;
                        dispatched_69:
                        do {
                            switch (__dispatch69 != 0 ? __dispatch69 : (this.token.value.value & 0xFF))
                            {
                                case 97:
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) != 120))
                                    {
                                        this.error(new BytePtr("identifier expected following `.` instead of `%s`"), this.token.value.toChars());
                                        break;
                                    }
                                    if (maybeArray != null)
                                    {
                                        DArray<RootObject> dimStack = new DArray<RootObject>();
                                        try {
                                            Type t = maybeArray;
                                            for (; true;){
                                                if (((t.ty.value & 0xFF) == ENUMTY.Tsarray))
                                                {
                                                    TypeSArray a = (TypeSArray)t;
                                                    dimStack.push(a.dim.value.syntaxCopy());
                                                    t = a.next.value.syntaxCopy();
                                                }
                                                else if (((t.ty.value & 0xFF) == ENUMTY.Taarray))
                                                {
                                                    TypeAArray a_1 = (TypeAArray)t;
                                                    dimStack.push(a_1.index.value.syntaxCopy());
                                                    t = a_1.next.value.syntaxCopy();
                                                }
                                                else
                                                {
                                                    break;
                                                }
                                            }
                                            assert((dimStack.length.value > 0));
                                            tid = (TypeQualified)t;
                                            for (; dimStack.length.value != 0;){
                                                tid.addIndex(dimStack.pop());
                                            }
                                            maybeArray = null;
                                        }
                                        finally {
                                        }
                                    }
                                    Loc loc = this.token.value.loc.copy();
                                    Identifier id = this.token.value.ident;
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) == 91))
                                    {
                                        TemplateInstance tempinst = new TemplateInstance(loc, id, this.parseTemplateArguments());
                                        tid.addInst(tempinst);
                                    }
                                    else
                                    {
                                        tid.addIdent(id);
                                    }
                                    continue L_outer32;
                                case 3:
                                    if (dontLookDotIdents)
                                    {
                                        /*goto Lend*/throw Dispatch0.INSTANCE;
                                    }
                                    this.nextToken();
                                    Type t_1 = maybeArray != null ? maybeArray : tid;
                                    if (((this.token.value.value & 0xFF) == 4))
                                    {
                                        t_1 = new TypeDArray(t_1);
                                        this.nextToken();
                                        return t_1;
                                    }
                                    else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.rightBracket, null))
                                    {
                                        Type index = this.parseType(null, null);
                                        maybeArray = new TypeAArray(t_1, index);
                                        this.check(TOK.rightBracket);
                                    }
                                    else
                                    {
                                        this.inBrackets++;
                                        Expression e = this.parseAssignExp();
                                        if (((this.token.value.value & 0xFF) == 31))
                                        {
                                            this.nextToken();
                                            Expression e2 = this.parseAssignExp();
                                            t_1 = new TypeSlice(t_1, e, e2);
                                            this.inBrackets--;
                                            this.check(TOK.rightBracket);
                                            return t_1;
                                        }
                                        else
                                        {
                                            maybeArray = new TypeSArray(t_1, e);
                                            this.inBrackets--;
                                            this.check(TOK.rightBracket);
                                            continue L_outer32;
                                        }
                                    }
                                    break;
                                default:
                                /*goto Lend*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch69 != 0);
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Lend:*/
            return maybeArray != null ? maybeArray : tid;
        }

        public  Type parseBasicType2(Type t) {
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 78:
                        t = new TypePointer(t);
                        this.nextToken();
                        continue;
                    case 3:
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 4))
                        {
                            t = new TypeDArray(t);
                            this.nextToken();
                        }
                        else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.rightBracket, null))
                        {
                            Type index = this.parseType(null, null);
                            t = new TypeAArray(t, index);
                            this.check(TOK.rightBracket);
                        }
                        else
                        {
                            this.inBrackets++;
                            Expression e = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 31))
                            {
                                this.nextToken();
                                Expression e2 = this.parseAssignExp();
                                t = new TypeSlice(t, e, e2);
                            }
                            else
                            {
                                t = new TypeSArray(t, e);
                            }
                            this.inBrackets--;
                            this.check(TOK.rightBracket);
                        }
                        continue;
                    case 160:
                    case 161:
                        byte save = this.token.value.value;
                        this.nextToken();
                        int varargs = VarArg.none;
                        Ptr<DArray<Parameter>> parameters = this.parseParameters(ptr(varargs), null);
                        long stc = this.parsePostfix(0L, null);
                        TypeFunction tf = new TypeFunction(new ParameterList(parameters, varargs), t, this.linkage, stc);
                        if ((stc & 17594871447556L) != 0)
                        {
                            if (((save & 0xFF) == 161))
                            {
                                this.error(new BytePtr("`const`/`immutable`/`shared`/`inout`/`return` attributes are only valid for non-static member functions"));
                            }
                            else
                            {
                                tf = (TypeFunction)tf.addSTC(stc);
                            }
                        }
                        t = ((save & 0xFF) == 160) ? new TypeDelegate(tf) : new TypePointer(tf);
                        continue;
                    default:
                    return t;
                }
                throw new AssertionError("Unreachable code!");
            }
            throw new AssertionError("Unreachable code!");
        }

        public  Type parseDeclarator(Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<TemplateParameter>>> tpl, long storageClass, IntPtr pdisable, Ptr<Ptr<DArray<Expression>>> pudas) {
            t = this.parseBasicType2(t);
            Type ts = null;
            switch ((this.token.value.value & 0xFF))
            {
                case 120:
                    if (pident != null)
                    {
                        pident.set(0, this.token.value.ident);
                    }
                    else
                    {
                        this.error(new BytePtr("unexpected identifier `%s` in declarator"), this.token.value.ident.toChars());
                    }
                    ts = t;
                    this.nextToken();
                    break;
                case 1:
                    if (((this.peekNext() & 0xFF) == 78) || ((this.peekNext() & 0xFF) == 1))
                    {
                        palt.set(0, palt.get() | 1);
                        this.nextToken();
                        ts = this.parseDeclarator(t, palt, pident, null, 0L, null, null);
                        this.check(TOK.rightParentheses);
                        break;
                    }
                    ts = t;
                    Ptr<Token> peekt = ptr(this.token);
                    if (this.isParameters(ptr(peekt)))
                    {
                        this.error(new BytePtr("function declaration without return type. (Note that constructors are always named `this`)"));
                    }
                    else
                    {
                        this.error(new BytePtr("unexpected `(` in declarator"));
                    }
                    break;
                default:
                ts = t;
                break;
            }
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 3:
                        TypeNext ta = null;
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 4))
                        {
                            ta = new TypeDArray(t);
                            this.nextToken();
                            palt.set(0, palt.get() | 2);
                        }
                        else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.rightBracket, null))
                        {
                            Type index = this.parseType(null, null);
                            this.check(TOK.rightBracket);
                            ta = new TypeAArray(t, index);
                            palt.set(0, palt.get() | 2);
                        }
                        else
                        {
                            Expression e = this.parseAssignExp();
                            ta = new TypeSArray(t, e);
                            this.check(TOK.rightBracket);
                            palt.set(0, palt.get() | 2);
                        }
                        Ptr<Type> pt = null;
                        {
                            pt = pcopy(ptr(ts));
                            for (; (!pequals(pt.get(), t));pt = pcopy((ptr((TypeNext)pt.get().next)))){
                            }
                        }
                        pt.set(0, ta);
                        continue;
                    case 1:
                        if (tpl != null)
                        {
                            Ptr<Token> tk = this.peekPastParen(ptr(this.token));
                            if ((((tk.get()).value & 0xFF) == 1))
                            {
                                tpl.set(0, this.parseTemplateParameterList(0));
                            }
                            else if ((((tk.get()).value & 0xFF) == 90))
                            {
                                tpl.set(0, this.parseTemplateParameterList(0));
                                break;
                            }
                        }
                        int varargs = VarArg.none;
                        Ptr<DArray<Parameter>> parameters = this.parseParameters(ptr(varargs), null);
                        long stc = this.parsePostfix(storageClass, pudas);
                        Type tf = new TypeFunction(new ParameterList(parameters, varargs), t, this.linkage, stc);
                        tf = tf.addSTC(stc);
                        if (pdisable != null)
                        {
                            pdisable.set(0, ((stc & 137438953472L) != 0 ? 1 : 0));
                        }
                        Ptr<Type> pt_1 = null;
                        {
                            pt_1 = pcopy(ptr(ts));
                            for (; (!pequals(pt_1.get(), t));pt_1 = pcopy((ptr((TypeNext)pt_1.get().next)))){
                            }
                        }
                        pt_1.set(0, tf);
                        break;
                    default:
                    break;
                }
                break;
            }
            return ts;
        }

        // defaulted all parameters starting with #7
        public  Type parseDeclarator(Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<TemplateParameter>>> tpl, long storageClass, IntPtr pdisable) {
            return parseDeclarator(t, palt, pident, tpl, storageClass, pdisable, null);
        }

        // defaulted all parameters starting with #6
        public  Type parseDeclarator(Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<TemplateParameter>>> tpl, long storageClass) {
            return parseDeclarator(t, palt, pident, tpl, storageClass, null, null);
        }

        // defaulted all parameters starting with #5
        public  Type parseDeclarator(Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<Ptr<DArray<TemplateParameter>>> tpl) {
            return parseDeclarator(t, palt, pident, tpl, 0L, null, null);
        }

        // defaulted all parameters starting with #4
        public  Type parseDeclarator(Type t, IntPtr palt, Ptr<Identifier> pident) {
            return parseDeclarator(t, palt, pident, null, 0L, null, null);
        }

        public  void parseStorageClasses(Ref<Long> storage_class, IntRef link, Ref<Boolean> setAlignment, Ref<Expression> ealign, Ref<Ptr<DArray<Expression>>> udas) {
            long stc = 0L;
            boolean sawLinkage = false;
        L_outer33:
            for (; 1 != 0;){
                {
                    int __dispatch73 = 0;
                    dispatched_73:
                    do {
                        switch (__dispatch73 != 0 ? __dispatch73 : (this.token.value.value & 0xFF))
                        {
                            case 171:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 4L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 182:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 1048576L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 224:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 536870912L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 177:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) == 1))
                                {
                                    break;
                                }
                                stc = 2147483648L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 169:
                                stc = 1L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 170:
                                stc = 8L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 179:
                                stc = 256L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 203:
                                stc = 524288L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 159:
                                stc = 128L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 172:
                                stc = 16L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 194:
                                stc = 512L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 174:
                                stc = 1024L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 216:
                                stc = 33554432L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 215:
                                stc = 67108864L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 210:
                                stc = 2097152L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 217:
                                stc = 1073741824L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 156:
                                Ptr<Token> t = this.peek(ptr(this.token));
                                if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7))
                                {
                                    break;
                                }
                                if ((((t.get()).value & 0xFF) == 120))
                                {
                                    t = this.peek(t);
                                    if ((((t.get()).value & 0xFF) == 5) || (((t.get()).value & 0xFF) == 7) || (((t.get()).value & 0xFF) == 9))
                                    {
                                        break;
                                    }
                                }
                                stc = 8388608L;
                                /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                            case 225:
                                {
                                    stc = this.parseAttribute(ptr(udas));
                                    if (stc != 0)
                                    {
                                        /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                                    }
                                    continue L_outer33;
                                }
                            /*L1:*/
                            case -1:
                            __dispatch73 = 0;
                                storage_class.value = this.appendStorageClass(storage_class.value, stc);
                                this.nextToken();
                                continue L_outer33;
                            case 164:
                                if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
                                {
                                    stc = 2L;
                                    /*goto L1*/{ __dispatch73 = -1; continue dispatched_73; }
                                }
                                if (sawLinkage)
                                {
                                    this.error(new BytePtr("redundant linkage declaration"));
                                }
                                sawLinkage = true;
                                Ptr<DArray<Identifier>> idents = null;
                                Ptr<DArray<Expression>> identExps = null;
                                int cppmangle = CPPMANGLE.def;
                                boolean cppMangleOnly = false;
                                link.value = this.parseLinkage(ptr(idents), ptr(identExps), cppmangle, cppMangleOnly);
                                if ((idents != null) || (identExps != null))
                                {
                                    this.error(new BytePtr("C++ name spaces not allowed here"));
                                }
                                if ((cppmangle != CPPMANGLE.def))
                                {
                                    this.error(new BytePtr("C++ mangle declaration not allowed here"));
                                }
                                continue L_outer33;
                            case 163:
                                this.nextToken();
                                setAlignment.value = true;
                                if (((this.token.value.value & 0xFF) == 1))
                                {
                                    this.nextToken();
                                    ealign.value = this.parseExpression();
                                    this.check(TOK.rightParentheses);
                                }
                                continue L_outer33;
                            default:
                            break;
                        }
                    } while(__dispatch73 != 0);
                }
                break;
            }
        }

        public  Ptr<DArray<Dsymbol>> parseDeclarations(boolean autodecl, Ptr<PrefixAttributesASTCodegen> pAttrs, BytePtr comment) {
            long storage_class = 0L;
            byte tok = TOK.reserved;
            int link = this.linkage;
            boolean setAlignment = false;
            Expression ealign = null;
            Ptr<DArray<Expression>> udas = null;
            if (comment == null)
            {
                comment = pcopy(this.token.value.blockComment.value);
            }
            if (((this.token.value.value & 0xFF) == 158))
            {
                Loc loc = this.token.value.loc.copy();
                tok = this.token.value.value;
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 120) && ((this.peekNext() & 0xFF) == 123))
                {
                    AliasThis s = new AliasThis(loc, this.token.value.ident);
                    this.nextToken();
                    this.check(TOK.this_);
                    this.check(TOK.semicolon);
                    Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                    (a.get()).push(s);
                    this.addComment(s, comment);
                    return a;
                }
                if (((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign))
                {
                    Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                    for (; 1 != 0;){
                        Identifier ident = this.token.value.ident;
                        this.nextToken();
                        Ptr<DArray<TemplateParameter>> tpl = null;
                        if (((this.token.value.value & 0xFF) == 1))
                        {
                            tpl = this.parseTemplateParameterList(0);
                        }
                        this.check(TOK.assign);
                        boolean hasParsedAttributes = false;
                        Function0<Void> parseAttributes = new Function0<Void>(){
                            public Void invoke() {
                                if (hasParsedAttributes)
                                {
                                    return null;
                                }
                                hasParsedAttributes = true;
                                udas = null;
                                storage_class = 0L;
                                link = linkage;
                                setAlignment = false;
                                ealign = null;
                                parseStorageClasses(storage_class, link, setAlignment, ealign, udas);
                                return null;
                            }
                        };
                        if (((this.token.value.value & 0xFF) == 225))
                        {
                            parseAttributes.invoke();
                        }
                        Declaration v = null;
                        Dsymbol s = null;
                        boolean attributesAppended = false;
                        long funcStc = this.parseTypeCtor();
                        Ptr<Token> tlu = ptr(this.token);
                        Ptr<Token> tk = null;
                        if (((this.token.value.value & 0xFF) != 161) && ((this.token.value.value & 0xFF) != 160) && this.isBasicType(ptr(tlu)) && (tlu != null) && (((tlu.get()).value & 0xFF) == 1))
                        {
                            int vargs = VarArg.none;
                            Type tret = this.parseBasicType(false);
                            Ptr<DArray<Parameter>> prms = this.parseParameters(ptr(vargs), null);
                            ParameterList pl = new ParameterList(prms, vargs).copy();
                            parseAttributes.invoke();
                            if (udas != null)
                            {
                                this.error(new BytePtr("user-defined attributes not allowed for `alias` declarations"));
                            }
                            attributesAppended = true;
                            storage_class = this.appendStorageClass(storage_class, funcStc);
                            Type tf = new TypeFunction(pl, tret, link, storage_class);
                            v = new AliasDeclaration(loc, ident, tf);
                        }
                        else if (((this.token.value.value & 0xFF) == 161) || ((this.token.value.value & 0xFF) == 160) || ((this.token.value.value & 0xFF) == 1) && this.skipAttributes(this.peekPastParen(ptr(this.token)), ptr(tk)) && (((tk.get()).value & 0xFF) == 228) || (((tk.get()).value & 0xFF) == 5) || ((this.token.value.value & 0xFF) == 5) || ((this.token.value.value & 0xFF) == 120) && ((this.peekNext() & 0xFF) == 228) || ((this.token.value.value & 0xFF) == 210) && ((this.peekNext() & 0xFF) == 1) && this.skipAttributes(this.peekPastParen(this.peek(ptr(this.token))), ptr(tk)) && (((tk.get()).value & 0xFF) == 228) || (((tk.get()).value & 0xFF) == 5))
                        {
                            s = this.parseFunctionLiteral();
                            if ((udas != null))
                            {
                                if ((storage_class != 0L))
                                {
                                    this.error(new BytePtr("Cannot put a storage-class in an alias declaration."));
                                }
                                assert((link == this.linkage) && !setAlignment && (ealign == null));
                                TemplateDeclaration tpl_ = (TemplateDeclaration)s;
                                assert((tpl_ != null) && ((tpl_.members.value.get()).length.value == 1));
                                FuncLiteralDeclaration fd = (FuncLiteralDeclaration)(tpl_.members.value.get()).get(0);
                                TypeFunction tf = (TypeFunction)fd.type.value;
                                assert(((tf.parameterList.parameters.value.get()).length.value > 0));
                                Ptr<DArray<Dsymbol>> as = refPtr(new DArray<Dsymbol>());
                                (tf.parameterList.parameters.value.get()).get(0).userAttribDecl.value = new UserAttributeDeclaration(udas, as);
                            }
                            v = new AliasDeclaration(loc, ident, s);
                        }
                        else
                        {
                            parseAttributes.invoke();
                            if (udas != null)
                            {
                                this.error(new BytePtr("user-defined attributes not allowed for `%s` declarations"), Token.toChars(tok));
                            }
                            Type t = this.parseType(null, null);
                            v = new AliasDeclaration(loc, ident, t);
                        }
                        if (!attributesAppended)
                        {
                            storage_class = this.appendStorageClass(storage_class, funcStc);
                        }
                        v.storage_class.value = storage_class;
                        s = v;
                        if (tpl != null)
                        {
                            Ptr<DArray<Dsymbol>> a2 = refPtr(new DArray<Dsymbol>());
                            (a2.get()).push(s);
                            TemplateDeclaration tempdecl = new TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                            s = tempdecl;
                        }
                        if ((link != this.linkage))
                        {
                            Ptr<DArray<Dsymbol>> a2 = refPtr(new DArray<Dsymbol>());
                            (a2.get()).push(s);
                            s = new LinkDeclaration(link, a2);
                        }
                        (a.get()).push(s);
                        switch ((this.token.value.value & 0xFF))
                        {
                            case 9:
                                this.nextToken();
                                this.addComment(s, comment);
                                break;
                            case 99:
                                this.nextToken();
                                this.addComment(s, comment);
                                if (((this.token.value.value & 0xFF) != 120))
                                {
                                    this.error(new BytePtr("identifier expected following comma, not `%s`"), this.token.value.toChars());
                                    break;
                                }
                                if (((this.peekNext() & 0xFF) != 90) && ((this.peekNext() & 0xFF) != 1))
                                {
                                    this.error(new BytePtr("`=` expected following identifier"));
                                    this.nextToken();
                                    break;
                                }
                                continue;
                            default:
                            this.error(new BytePtr("semicolon expected to close `%s` declaration"), Token.toChars(tok));
                            break;
                        }
                        break;
                    }
                    return a;
                }
            }
            Type ts = null;
            if (!autodecl)
            {
                this.parseStorageClasses(storage_class, link, setAlignment, ealign, udas);
                if (((this.token.value.value & 0xFF) == 156))
                {
                    Dsymbol d = this.parseEnum();
                    Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                    (a.get()).push(d);
                    if (udas != null)
                    {
                        d = new UserAttributeDeclaration(udas, a);
                        a = refPtr(new DArray<Dsymbol>());
                        (a.get()).push(d);
                    }
                    this.addComment(d, comment);
                    return a;
                }
                if (((this.token.value.value & 0xFF) == 152) || ((this.token.value.value & 0xFF) == 155) || ((this.token.value.value & 0xFF) == 153) || ((this.token.value.value & 0xFF) == 154))
                {
                    Dsymbol s = this.parseAggregate();
                    Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
                    (a.get()).push(s);
                    if (storage_class != 0)
                    {
                        s = new StorageClassDeclaration(storage_class, a);
                        a = refPtr(new DArray<Dsymbol>());
                        (a.get()).push(s);
                    }
                    if (setAlignment)
                    {
                        s = new AlignDeclaration(s.loc.value, ealign, a);
                        a = refPtr(new DArray<Dsymbol>());
                        (a.get()).push(s);
                    }
                    if ((link != this.linkage))
                    {
                        s = new LinkDeclaration(link, a);
                        a = refPtr(new DArray<Dsymbol>());
                        (a.get()).push(s);
                    }
                    if (udas != null)
                    {
                        s = new UserAttributeDeclaration(udas, a);
                        a = refPtr(new DArray<Dsymbol>());
                        (a.get()).push(s);
                    }
                    this.addComment(s, comment);
                    return a;
                }
                if ((storage_class != 0) || (udas != null) && ((this.token.value.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(ptr(this.token)), TOK.assign))
                {
                    Ptr<DArray<Dsymbol>> a = this.parseAutoDeclarations(storage_class, comment);
                    if (udas != null)
                    {
                        Dsymbol s = new UserAttributeDeclaration(udas, a);
                        a = refPtr(new DArray<Dsymbol>());
                        (a.get()).push(s);
                    }
                    return a;
                }
                {
                    Ptr<Token> tk = null;
                    if ((storage_class != 0) || (udas != null) && ((this.token.value.value & 0xFF) == 120) && this.skipParens(this.peek(ptr(this.token)), ptr(tk)) && this.skipAttributes(tk, ptr(tk)) && (((tk.get()).value & 0xFF) == 1) || (((tk.get()).value & 0xFF) == 5) || (((tk.get()).value & 0xFF) == 175) || (((tk.get()).value & 0xFF) == 176) || (((tk.get()).value & 0xFF) == 187) || (((tk.get()).value & 0xFF) == 120) && (pequals((tk.get()).ident, Id._body)))
                    {
                        ts = null;
                    }
                    else
                    {
                        ts = this.parseBasicType(false);
                        ts = this.parseBasicType2(ts);
                    }
                }
            }
            if (pAttrs != null)
            {
                storage_class |= (pAttrs.get()).storageClass;
            }
            Type tfirst = null;
            Ptr<DArray<Dsymbol>> a = refPtr(new DArray<Dsymbol>());
            for (; 1 != 0;){
                Ptr<DArray<TemplateParameter>> tpl = null;
                int disable = 0;
                int alt = 0;
                Loc loc = this.token.value.loc.copy();
                Identifier ident = null;
                Type t = this.parseDeclarator(ts, ptr(alt), ptr(ident), ptr(tpl), storage_class, ptr(disable), ptr(udas));
                assert(t != null);
                if (tfirst == null)
                {
                    tfirst = t;
                }
                else if ((!pequals(t, tfirst)))
                {
                    this.error(new BytePtr("multiple declarations must have the same type, not `%s` and `%s`"), tfirst.toChars(), t.toChars());
                }
                boolean isThis = ((t.ty.value & 0xFF) == ENUMTY.Tident) && (pequals(((TypeIdentifier)t).ident.value, Id.This.value)) && ((this.token.value.value & 0xFF) == 90);
                if (ident != null)
                {
                    this.checkCstyleTypeSyntax(loc, t, alt, ident);
                }
                else if (!isThis && (!pequals(t, Type.terror.value)))
                {
                    this.error(new BytePtr("no identifier for declarator `%s`"), t.toChars());
                }
                if (((tok & 0xFF) == 158))
                {
                    Declaration v = null;
                    Initializer _init = null;
                    if (udas != null)
                    {
                        this.error(new BytePtr("user-defined attributes not allowed for `%s` declarations"), Token.toChars(tok));
                    }
                    if (((this.token.value.value & 0xFF) == 90))
                    {
                        this.nextToken();
                        _init = this.parseInitializer();
                    }
                    if (_init != null)
                    {
                        if (isThis)
                        {
                            this.error(new BytePtr("cannot use syntax `alias this = %s`, use `alias %s this` instead"), _init.toChars(), _init.toChars());
                        }
                        else
                        {
                            this.error(new BytePtr("alias cannot have initializer"));
                        }
                    }
                    v = new AliasDeclaration(loc, ident, t);
                    v.storage_class.value = storage_class;
                    if (pAttrs != null)
                    {
                        (pAttrs.get()).storageClass &= 60129542144L;
                    }
                    Dsymbol s = v;
                    if ((link != this.linkage))
                    {
                        Ptr<DArray<Dsymbol>> ax = refPtr(new DArray<Dsymbol>());
                        (ax.get()).push(v);
                        s = new LinkDeclaration(link, ax);
                    }
                    (a.get()).push(s);
                    switch ((this.token.value.value & 0xFF))
                    {
                        case 9:
                            this.nextToken();
                            this.addComment(s, comment);
                            break;
                        case 99:
                            this.nextToken();
                            this.addComment(s, comment);
                            continue;
                        default:
                        this.error(new BytePtr("semicolon expected to close `%s` declaration"), Token.toChars(tok));
                        break;
                    }
                }
                else if (((t.ty.value & 0xFF) == ENUMTY.Tfunction))
                {
                    Expression constraint = null;
                    FuncDeclaration f = new FuncDeclaration(loc, Loc.initial.value, ident, storage_class | (disable != 0 ? 137438953472L : 0L), t);
                    if (pAttrs != null)
                    {
                        (pAttrs.get()).storageClass = 0L;
                    }
                    if (tpl != null)
                    {
                        constraint = this.parseConstraint();
                    }
                    Dsymbol s = this.parseContracts(f);
                    Identifier tplIdent = s.ident.value;
                    if ((link != this.linkage))
                    {
                        Ptr<DArray<Dsymbol>> ax = refPtr(new DArray<Dsymbol>());
                        (ax.get()).push(s);
                        s = new LinkDeclaration(link, ax);
                    }
                    if (udas != null)
                    {
                        Ptr<DArray<Dsymbol>> ax = refPtr(new DArray<Dsymbol>());
                        (ax.get()).push(s);
                        s = new UserAttributeDeclaration(udas, ax);
                    }
                    if (tpl != null)
                    {
                        Ptr<DArray<Dsymbol>> decldefs = refPtr(new DArray<Dsymbol>());
                        (decldefs.get()).push(s);
                        TemplateDeclaration tempdecl = new TemplateDeclaration(loc, tplIdent, tpl, constraint, decldefs, false, false);
                        s = tempdecl;
                        if ((storage_class & 1L) != 0)
                        {
                            assert((f.storage_class.value & 1L) != 0);
                            f.storage_class.value &= -2L;
                            Ptr<DArray<Dsymbol>> ax = refPtr(new DArray<Dsymbol>());
                            (ax.get()).push(s);
                            s = new StorageClassDeclaration(1L, ax);
                        }
                    }
                    (a.get()).push(s);
                    this.addComment(s, comment);
                }
                else if (ident != null)
                {
                    Initializer _init = null;
                    if (((this.token.value.value & 0xFF) == 90))
                    {
                        this.nextToken();
                        _init = this.parseInitializer();
                    }
                    VarDeclaration v = new VarDeclaration(loc, t, ident, _init, 0L);
                    v.storage_class.value = storage_class;
                    if (pAttrs != null)
                    {
                        (pAttrs.get()).storageClass = 0L;
                    }
                    Dsymbol s = v;
                    if ((tpl != null) && (_init != null))
                    {
                        Ptr<DArray<Dsymbol>> a2 = refPtr(new DArray<Dsymbol>());
                        (a2.get()).push(s);
                        TemplateDeclaration tempdecl = new TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                        s = tempdecl;
                    }
                    if (setAlignment)
                    {
                        Ptr<DArray<Dsymbol>> ax = refPtr(new DArray<Dsymbol>());
                        (ax.get()).push(s);
                        s = new AlignDeclaration(v.loc.value, ealign, ax);
                    }
                    if ((link != this.linkage))
                    {
                        Ptr<DArray<Dsymbol>> ax = refPtr(new DArray<Dsymbol>());
                        (ax.get()).push(s);
                        s = new LinkDeclaration(link, ax);
                    }
                    if (udas != null)
                    {
                        Ptr<DArray<Dsymbol>> ax = refPtr(new DArray<Dsymbol>());
                        (ax.get()).push(s);
                        s = new UserAttributeDeclaration(udas, ax);
                    }
                    (a.get()).push(s);
                    switch ((this.token.value.value & 0xFF))
                    {
                        case 9:
                            this.nextToken();
                            this.addComment(s, comment);
                            break;
                        case 99:
                            this.nextToken();
                            this.addComment(s, comment);
                            continue;
                        default:
                        this.error(new BytePtr("semicolon expected, not `%s`"), this.token.value.toChars());
                        break;
                    }
                }
                break;
            }
            return a;
        }

        public  Dsymbol parseFunctionLiteral() {
            Loc loc = this.token.value.loc.copy();
            Ptr<DArray<TemplateParameter>> tpl = null;
            Ptr<DArray<Parameter>> parameters = null;
            int varargs = VarArg.none;
            Type tret = null;
            long stc = 0L;
            byte save = TOK.reserved;
            {
                int __dispatch77 = 0;
                dispatched_77:
                do {
                    switch (__dispatch77 != 0 ? __dispatch77 : (this.token.value.value & 0xFF))
                    {
                        case 161:
                        case 160:
                            save = this.token.value.value;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 210))
                            {
                                stc = 2097152L;
                                this.nextToken();
                            }
                            if (((this.token.value.value & 0xFF) != 1) && ((this.token.value.value & 0xFF) != 5))
                            {
                                tret = this.parseBasicType(false);
                                tret = this.parseBasicType2(tret);
                            }
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                            }
                            else
                            {
                                break;
                            }
                            /*goto case*/{ __dispatch77 = 1; continue dispatched_77; }
                        case 210:
                            stc = 2097152L;
                            this.nextToken();
                            /*goto case*/{ __dispatch77 = 1; continue dispatched_77; }
                        case 1:
                            __dispatch77 = 0;
                            parameters = this.parseParameters(ptr(varargs), ptr(tpl));
                            stc = this.parsePostfix(stc, null);
                            {
                                long modStc = stc & 2685403140L;
                                if ((modStc) != 0)
                                {
                                    if (((save & 0xFF) == 161))
                                    {
                                        OutBuffer buf = new OutBuffer();
                                        try {
                                            stcToBuffer(ptr(buf), modStc);
                                            this.error(new BytePtr("function literal cannot be `%s`"), buf.peekChars());
                                        }
                                        finally {
                                        }
                                    }
                                    else
                                    {
                                        save = TOK.delegate_;
                                    }
                                }
                            }
                            break;
                        case 5:
                            break;
                        case 120:
                            parameters = refPtr(new DArray<Parameter>());
                            Identifier id = Identifier.generateId(new BytePtr("__T"));
                            Type t = new TypeIdentifier(loc, id);
                            (parameters.get()).push(new Parameter(0L, t, this.token.value.ident, null, null));
                            tpl = refPtr(new DArray<TemplateParameter>());
                            TemplateParameter tp = new TemplateTypeParameter(loc, id, null, null);
                            (tpl.get()).push(tp);
                            this.nextToken();
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                } while(__dispatch77 != 0);
            }
            TypeFunction tf = new TypeFunction(new ParameterList(parameters, varargs), tret, this.linkage, stc);
            tf = (TypeFunction)tf.addSTC(stc);
            FuncLiteralDeclaration fd = new FuncLiteralDeclaration(loc, Loc.initial.value, tf, save, null, null);
            if (((this.token.value.value & 0xFF) == 228))
            {
                this.check(TOK.goesTo);
                Loc returnloc = this.token.value.loc.copy();
                Expression ae = this.parseAssignExp();
                fd.fbody.value = new ReturnStatement(returnloc, ae);
                fd.endloc.value = this.token.value.loc.copy();
            }
            else
            {
                this.parseContracts(fd);
            }
            if (tpl != null)
            {
                Ptr<DArray<Dsymbol>> decldefs = refPtr(new DArray<Dsymbol>());
                (decldefs.get()).push(fd);
                return new TemplateDeclaration(fd.loc.value, fd.ident.value, tpl, null, decldefs, false, true);
            }
            return fd;
        }

        public  FuncDeclaration parseContracts(FuncDeclaration f) {
            int linksave = this.linkage;
            boolean literal = f.isFuncLiteralDeclaration() != null;
            this.linkage = LINK.d;
            boolean requireDo = false;
        /*L1:*/
            {
                int __dispatch78 = 0;
                dispatched_78:
                do {
                    switch (__dispatch78 != 0 ? __dispatch78 : (this.token.value.value & 0xFF))
                    {
                        case 5:
                            if (requireDo)
                            {
                                this.error(new BytePtr("missing `do { ... }` after `in` or `out`"));
                            }
                            f.fbody.value = this.parseStatement(1, null, null);
                            f.endloc.value = this.endloc.copy();
                            break;
                        case 120:
                            if ((pequals(this.token.value.ident, Id._body)))
                            {
                                /*goto case*/{ __dispatch78 = 187; continue dispatched_78; }
                            }
                            /*goto default*/ { __dispatch78 = -2; continue dispatched_78; }
                        case 187:
                            __dispatch78 = 0;
                            this.nextToken();
                            f.fbody.value = this.parseStatement(4, null, null);
                            f.endloc.value = this.endloc.copy();
                            break;
                        case 175:
                            Loc loc = this.token.value.loc.copy();
                            this.nextToken();
                            if (f.frequires == null)
                            {
                                f.frequires = refPtr(new DArray<Statement>());
                            }
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.nextToken();
                                Expression e = this.parseAssignExp();
                                Expression msg = null;
                                if (((this.token.value.value & 0xFF) == 99))
                                {
                                    this.nextToken();
                                    if (((this.token.value.value & 0xFF) != 2))
                                    {
                                        msg = this.parseAssignExp();
                                        if (((this.token.value.value & 0xFF) == 99))
                                        {
                                            this.nextToken();
                                        }
                                    }
                                }
                                this.check(TOK.rightParentheses);
                                e = new AssertExp(loc, e, msg);
                                (f.frequires.get()).push(new ExpStatement(loc, e));
                                requireDo = false;
                            }
                            else
                            {
                                (f.frequires.get()).push(this.parseStatement(6, null, null));
                                requireDo = true;
                            }
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        case 176:
                            Loc loc_1 = this.token.value.loc.copy();
                            this.nextToken();
                            if (f.fensures == null)
                            {
                                f.fensures = refPtr(new DArray<Ensure>());
                            }
                            Identifier id = null;
                            if (((this.token.value.value & 0xFF) != 5))
                            {
                                this.check(TOK.leftParentheses);
                                if (((this.token.value.value & 0xFF) != 120) && ((this.token.value.value & 0xFF) != 9))
                                {
                                    this.error(new BytePtr("`(identifier) { ... }` or `(identifier; expression)` following `out` expected, not `%s`"), this.token.value.toChars());
                                }
                                if (((this.token.value.value & 0xFF) != 9))
                                {
                                    id = this.token.value.ident;
                                    this.nextToken();
                                }
                                if (((this.token.value.value & 0xFF) == 9))
                                {
                                    this.nextToken();
                                    Expression e_1 = this.parseAssignExp();
                                    Expression msg_1 = null;
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        this.nextToken();
                                        if (((this.token.value.value & 0xFF) != 2))
                                        {
                                            msg_1 = this.parseAssignExp();
                                            if (((this.token.value.value & 0xFF) == 99))
                                            {
                                                this.nextToken();
                                            }
                                        }
                                    }
                                    this.check(TOK.rightParentheses);
                                    e_1 = new AssertExp(loc_1, e_1, msg_1);
                                    (f.fensures.get()).push(new Ensure(id, new ExpStatement(loc_1, e_1)));
                                    requireDo = false;
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                }
                                this.check(TOK.rightParentheses);
                            }
                            (f.fensures.get()).push(new Ensure(id, this.parseStatement(6, null, null)));
                            requireDo = true;
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        case 9:
                            if (!literal)
                            {
                                if (!requireDo)
                                {
                                    this.nextToken();
                                }
                                break;
                            }
                            /*goto default*/ { __dispatch78 = -2; continue dispatched_78; }
                        default:
                        __dispatch78 = 0;
                        if (literal)
                        {
                            BytePtr sbody = pcopy(requireDo ? new BytePtr("do ") : new BytePtr(""));
                            this.error(new BytePtr("missing `%s{ ... }` for function literal"), sbody);
                        }
                        else if (!requireDo)
                        {
                            byte t = this.token.value.value;
                            if (((t & 0xFF) == 171) || ((t & 0xFF) == 182) || ((t & 0xFF) == 177) || ((t & 0xFF) == 195) || ((t & 0xFF) == 224) || ((t & 0xFF) == 216) || ((t & 0xFF) == 215))
                            {
                                this.error(new BytePtr("'%s' cannot be placed after a template constraint"), this.token.value.toChars());
                            }
                            else if (((t & 0xFF) == 225))
                            {
                                this.error(new BytePtr("attributes cannot be placed after a template constraint"));
                            }
                            else if (((t & 0xFF) == 183))
                            {
                                this.error(new BytePtr("cannot use function constraints for non-template functions. Use `static if` instead"));
                            }
                            else
                            {
                                this.error(new BytePtr("semicolon expected following function declaration"));
                            }
                        }
                        break;
                    }
                } while(__dispatch78 != 0);
            }
            if (literal && (f.fbody.value == null))
            {
                f.fbody.value = new CompoundStatement(Loc.initial.value, slice(new Statement[]{null}));
            }
            this.linkage = linksave;
            return f;
        }

        public  void checkDanglingElse(Loc elseloc) {
            if (((this.token.value.value & 0xFF) != 184) && ((this.token.value.value & 0xFF) != 198) && ((this.token.value.value & 0xFF) != 199) && (this.lookingForElse.linnum != 0))
            {
                this.warning(elseloc, new BytePtr("else is dangling, add { } after condition at %s"), this.lookingForElse.toChars(global.params.showColumns.value));
            }
        }

        public  void checkCstyleTypeSyntax(Loc loc, Type t, int alt, Identifier ident) {
            if (alt == 0)
            {
                return ;
            }
            BytePtr sp = pcopy(ident == null ? new BytePtr("") : new BytePtr(" "));
            BytePtr s = pcopy(ident == null ? new BytePtr("") : ident.toChars());
            this.error(loc, new BytePtr("instead of C-style syntax, use D-style `%s%s%s`"), t.toChars(), sp, s);
        }

        // from template ParseForeachArgs!(00)
        // from template Seq!()


        // from template ParseForeachArgs!(00)

        // from template ParseForeachArgs!(10)
        // from template Seq!()


        // from template ParseForeachArgs!(10)

        // from template ParseForeachArgs!(11)
        // from template Seq!(Ptr<Dsymbol>)


        // from template ParseForeachArgs!(11)


        // from template ParseForeachRet!(00)


        // from template ParseForeachRet!(10)

        // from template ParseForeachRet!(11)


        // from template parseForeach!(00)
        public  Statement parseForeach00(Loc loc) {
            byte op = this.token.value.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
        L_outer34:
            for (; 1 != 0;){
                Identifier ai = null;
                Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if (stc != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch79 = 0;
                        dispatched_79:
                        do {
                            switch (__dispatch79 != 0 ? __dispatch79 : (this.token.value.value & 0xFF))
                            {
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 156:
                                    stc = 8388608L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 158:
                                    storageClass = this.appendStorageClass(storageClass, 268435456L);
                                    this.nextToken();
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 2147483648L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                default:
                                break;
                            }
                        } while(__dispatch79 != 0);
                    }
                    try {
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 9))
                            {
                                ai = this.token.value.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (ai == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                        }
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    Parameter p = new Parameter(storageClass, at, ai, null, null);
                    (parameters.get()).push(p);
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        continue L_outer34;
                    }
                    break;
                } catch(Dispatch0 __d){}
                break;
            }
            this.check(TOK.semicolon);
            Expression aggr = this.parseExpression();
            if (((this.token.value.value & 0xFF) == 31) && ((parameters.get()).length.value == 1))
            {
                Parameter p = (parameters.get()).get(0);
                this.nextToken();
                Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                Statement _body = this.parseStatement(0, null, ptr(endloc));
                ForeachRangeStatement rangefe = new ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return rangefe;
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                Statement _body = this.parseStatement(0, null, ptr(endloc));
                ForeachStatement aggrfe = new ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return aggrfe;
            }
        }


        // from template parseForeach!(10)
        public  StaticForeachStatement parseForeach10(Loc loc) {
            this.nextToken();
            byte op = this.token.value.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
        L_outer35:
            for (; 1 != 0;){
                Identifier ai = null;
                Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if (stc != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch80 = 0;
                        dispatched_80:
                        do {
                            switch (__dispatch80 != 0 ? __dispatch80 : (this.token.value.value & 0xFF))
                            {
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 156:
                                    stc = 8388608L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 158:
                                    storageClass = this.appendStorageClass(storageClass, 268435456L);
                                    this.nextToken();
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 2147483648L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                default:
                                break;
                            }
                        } while(__dispatch80 != 0);
                    }
                    try {
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 9))
                            {
                                ai = this.token.value.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (ai == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                        }
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    Parameter p = new Parameter(storageClass, at, ai, null, null);
                    (parameters.get()).push(p);
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        continue L_outer35;
                    }
                    break;
                } catch(Dispatch0 __d){}
                break;
            }
            this.check(TOK.semicolon);
            Expression aggr = this.parseExpression();
            if (((this.token.value.value & 0xFF) == 31) && ((parameters.get()).length.value == 1))
            {
                Parameter p = (parameters.get()).get(0);
                this.nextToken();
                Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                Statement _body = this.parseStatement(0, null, ptr(endloc));
                ForeachRangeStatement rangefe = new ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return new StaticForeachStatement(loc, new StaticForeach(loc, null, rangefe));
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                Statement _body = this.parseStatement(0, null, ptr(endloc));
                ForeachStatement aggrfe = new ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return new StaticForeachStatement(loc, new StaticForeach(loc, aggrfe, null));
            }
        }


        // from template parseForeach!(11)
        public  StaticForeachDeclaration parseForeach11(Loc loc, Ptr<Dsymbol> _param_1) {
            this.nextToken();
            Ptr<Dsymbol> pLastDecl = pcopy(_param_1);
            byte op = this.token.value.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
        L_outer36:
            for (; 1 != 0;){
                Identifier ai = null;
                Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if (stc != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch81 = 0;
                        dispatched_81:
                        do {
                            switch (__dispatch81 != 0 ? __dispatch81 : (this.token.value.value & 0xFF))
                            {
                                case 210:
                                    stc = 2097152L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 156:
                                    stc = 8388608L;
                                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                                case 158:
                                    storageClass = this.appendStorageClass(storageClass, 268435456L);
                                    this.nextToken();
                                    break;
                                case 171:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if (((this.peekNext() & 0xFF) != 1))
                                    {
                                        stc = 2147483648L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                default:
                                break;
                            }
                        } while(__dispatch81 != 0);
                    }
                    try {
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 9))
                            {
                                ai = this.token.value.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (ai == null)
                        {
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                        }
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    Parameter p = new Parameter(storageClass, at, ai, null, null);
                    (parameters.get()).push(p);
                    if (((this.token.value.value & 0xFF) == 99))
                    {
                        this.nextToken();
                        continue L_outer36;
                    }
                    break;
                } catch(Dispatch0 __d){}
                break;
            }
            this.check(TOK.semicolon);
            Expression aggr = this.parseExpression();
            if (((this.token.value.value & 0xFF) == 31) && ((parameters.get()).length.value == 1))
            {
                Parameter p = (parameters.get()).get(0);
                this.nextToken();
                Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                Statement _body = null;
                ForeachRangeStatement rangefe = new ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return new StaticForeachDeclaration(new StaticForeach(loc, null, rangefe), this.parseBlock(pLastDecl, null));
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                Statement _body = null;
                ForeachStatement aggrfe = new ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return new StaticForeachDeclaration(new StaticForeach(loc, aggrfe, null), this.parseBlock(pLastDecl, null));
            }
        }


        public  Statement parseStatement(int flags, Ptr<BytePtr> endPtr, Ptr<Loc> pEndloc) {
            Statement s = null;
            Condition cond = null;
            Statement ifbody = null;
            Statement elsebody = null;
            boolean isfinal = false;
            Loc loc = this.token.value.loc.copy();
            if (((flags & ParseStatementFlags.curly) != 0) && ((this.token.value.value & 0xFF) != 5))
            {
                this.error(new BytePtr("statement expected to be `{ }`, not `%s`"), this.token.value.toChars());
            }
            {
                int __dispatch82 = 0;
                dispatched_82:
                do {
                    switch (__dispatch82 != 0 ? __dispatch82 : (this.token.value.value & 0xFF))
                    {
                        case 120:
                            Ptr<Token> t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 7))
                            {
                                Ptr<Token> nt = this.peek(t);
                                if ((((nt.get()).value & 0xFF) == 7))
                                {
                                    this.nextToken();
                                    this.nextToken();
                                    this.nextToken();
                                    this.error(new BytePtr("use `.` for member lookup, not `::`"));
                                    break;
                                }
                                Identifier ident = this.token.value.ident;
                                this.nextToken();
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 6))
                                {
                                    s = null;
                                }
                                else if (((this.token.value.value & 0xFF) == 5))
                                {
                                    s = this.parseStatement(6, null, null);
                                }
                                else
                                {
                                    s = this.parseStatement(16, null, null);
                                }
                                s = new LabelStatement(loc, ident, s);
                                break;
                            }
                            /*goto case*/{ __dispatch82 = 97; continue dispatched_82; }
                        case 97:
                            __dispatch82 = 0;
                        case 39:
                        case 229:
                        case 213:
                            if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.mustIfDstyle, TOK.reserved, null))
                            {
                                /*goto Ldeclaration*/{ __dispatch82 = -1; continue dispatched_82; }
                            }
                            /*goto Lexp*/{ __dispatch82 = -2; continue dispatched_82; }
                        case 14:
                        case 123:
                        case 124:
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 117:
                        case 118:
                        case 119:
                        case 13:
                        case 15:
                        case 16:
                        case 121:
                        case 122:
                        case 1:
                        case 12:
                        case 78:
                        case 75:
                        case 74:
                        case 92:
                        case 91:
                        case 93:
                        case 94:
                        case 22:
                        case 23:
                        case 160:
                        case 161:
                        case 42:
                        case 63:
                        case 3:
                        case 219:
                        case 220:
                        case 218:
                        case 221:
                        case 222:
                        case 223:
                        /*Lexp:*/
                        case -2:
                        __dispatch82 = 0;
                            {
                                Expression exp = this.parseExpression();
                                this.check(TOK.semicolon, new BytePtr("statement"));
                                s = new ExpStatement(loc, exp);
                                break;
                            }
                        case 169:
                            Ptr<Token> t_1 = this.peek(ptr(this.token));
                            if ((((t_1.get()).value & 0xFF) == 14))
                            {
                                s = new StaticAssertStatement(this.parseStaticAssert());
                                break;
                            }
                            if ((((t_1.get()).value & 0xFF) == 183))
                            {
                                cond = this.parseStaticIfCondition();
                                /*goto Lcondition*/{ __dispatch82 = -3; continue dispatched_82; }
                            }
                            if ((((t_1.get()).value & 0xFF) == 201) || (((t_1.get()).value & 0xFF) == 202))
                            {
                                s = this.parseForeach10(loc);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ScopeStatement(loc, s, this.token.value.loc);
                                }
                                break;
                            }
                            if ((((t_1.get()).value & 0xFF) == 157))
                            {
                                Ptr<DArray<Dsymbol>> imports = this.parseImport();
                                s = new ImportStatement(loc, imports);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ScopeStatement(loc, s, this.token.value.loc);
                                }
                                break;
                            }
                            /*goto Ldeclaration*/{ __dispatch82 = -1; continue dispatched_82; }
                        case 170:
                            if (((this.peekNext() & 0xFF) == 188))
                            {
                                this.nextToken();
                                isfinal = true;
                                /*goto Lswitch*/{ __dispatch82 = -4; continue dispatched_82; }
                            }
                            /*goto Ldeclaration*/{ __dispatch82 = -1; continue dispatched_82; }
                        case 149:
                        case 150:
                        case 151:
                        case 148:
                        case 129:
                        case 130:
                        case 131:
                        case 132:
                        case 133:
                        case 134:
                        case 135:
                        case 136:
                        case 137:
                        case 138:
                        case 139:
                        case 140:
                        case 141:
                        case 142:
                        case 143:
                        case 144:
                        case 145:
                        case 146:
                        case 147:
                        case 128:
                            if (((this.peekNext() & 0xFF) == 97))
                            {
                                /*goto Lexp*/{ __dispatch82 = -2; continue dispatched_82; }
                            }
                            if (((this.peekNext() & 0xFF) == 1))
                            {
                                /*goto Lexp*/{ __dispatch82 = -2; continue dispatched_82; }
                            }
                        case 158:
                        case 171:
                        case 179:
                        case 172:
                        case 164:
                        case 163:
                        case 182:
                        case 224:
                        case 177:
                        case 174:
                        case 216:
                        case 215:
                        case 210:
                        case 217:
                        case 225:
                        case 152:
                        case 155:
                        case 153:
                        case 154:
                        /*Ldeclaration:*/
                        case -1:
                        __dispatch82 = 0;
                            {
                                Ptr<DArray<Dsymbol>> a = this.parseDeclarations(false, null, null);
                                if (((a.get()).length.value > 1))
                                {
                                    Ptr<DArray<Statement>> as = refPtr(new DArray<Statement>());
                                    (as.get()).reserve((a.get()).length.value);
                                    {
                                        int __key835 = 0;
                                        int __limit836 = (a.get()).length.value;
                                        for (; (__key835 < __limit836);__key835 += 1) {
                                            int i = __key835;
                                            Dsymbol d = (a.get()).get(i);
                                            s = new ExpStatement(loc, d);
                                            (as.get()).push(s);
                                        }
                                    }
                                    s = new CompoundDeclarationStatement(loc, as);
                                }
                                else if (((a.get()).length.value == 1))
                                {
                                    Dsymbol d_1 = (a.get()).get(0);
                                    s = new ExpStatement(loc, d_1);
                                }
                                else
                                {
                                    s = new ExpStatement(loc, null);
                                }
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ScopeStatement(loc, s, this.token.value.loc);
                                }
                                break;
                            }
                        case 156:
                            Dsymbol d_2 = null;
                            Ptr<Token> t_2 = this.peek(ptr(this.token));
                            if ((((t_2.get()).value & 0xFF) == 5) || (((t_2.get()).value & 0xFF) == 7))
                            {
                                d_2 = this.parseEnum();
                            }
                            else if ((((t_2.get()).value & 0xFF) != 120))
                            {
                                /*goto Ldeclaration*/{ __dispatch82 = -1; continue dispatched_82; }
                            }
                            else
                            {
                                t_2 = this.peek(t_2);
                                if ((((t_2.get()).value & 0xFF) == 5) || (((t_2.get()).value & 0xFF) == 7) || (((t_2.get()).value & 0xFF) == 9))
                                {
                                    d_2 = this.parseEnum();
                                }
                                else
                                {
                                    /*goto Ldeclaration*/{ __dispatch82 = -1; continue dispatched_82; }
                                }
                            }
                            s = new ExpStatement(loc, d_2);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                            {
                                s = new ScopeStatement(loc, s, this.token.value.loc);
                            }
                            break;
                        case 162:
                            Ptr<Token> t_3 = this.peek(ptr(this.token));
                            if ((((t_3.get()).value & 0xFF) == 1))
                            {
                                Expression e = this.parseAssignExp();
                                this.check(TOK.semicolon);
                                if (((e.op.value & 0xFF) == 162))
                                {
                                    CompileExp cpe = (CompileExp)e;
                                    s = new CompileStatement(loc, cpe.exps);
                                }
                                else
                                {
                                    s = new ExpStatement(loc, e);
                                }
                                break;
                            }
                            Dsymbol d_3 = this.parseMixin();
                            s = new ExpStatement(loc, d_3);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                            {
                                s = new ScopeStatement(loc, s, this.token.value.loc);
                            }
                            break;
                        case 5:
                            Loc lookingForElseSave = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.value.copy();
                            this.nextToken();
                            Ptr<DArray<Statement>> statements = refPtr(new DArray<Statement>());
                            for (; ((this.token.value.value & 0xFF) != 6) && ((this.token.value.value & 0xFF) != 11);){
                                (statements.get()).push(this.parseStatement(9, null, null));
                            }
                            if (endPtr != null)
                            {
                                endPtr.set(0, this.token.value.ptr);
                            }
                            this.endloc = this.token.value.loc.copy();
                            if (pEndloc != null)
                            {
                                pEndloc.set(0, this.token.value.loc);
                                pEndloc = null;
                            }
                            s = new CompoundStatement(loc, statements);
                            if ((flags & 10) != 0)
                            {
                                s = new ScopeStatement(loc, s, this.token.value.loc);
                            }
                            this.check(TOK.rightCurly, new BytePtr("compound statement"));
                            this.lookingForElse = lookingForElseSave.copy();
                            break;
                        case 185:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            Expression condition = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            Loc endloc = new Loc();
                            Statement _body = this.parseStatement(2, null, ptr(endloc));
                            s = new WhileStatement(loc, condition, _body, endloc);
                            break;
                        case 9:
                            if ((flags & ParseStatementFlags.semiOk) == 0)
                            {
                                if ((flags & ParseStatementFlags.semi) != 0)
                                {
                                    this.deprecation(new BytePtr("use `{ }` for an empty statement, not `;`"));
                                }
                                else
                                {
                                    this.error(new BytePtr("use `{ }` for an empty statement, not `;`"));
                                }
                            }
                            this.nextToken();
                            s = new ExpStatement(loc, null);
                            break;
                        case 187:
                            Statement _body_1 = null;
                            Expression condition_1 = null;
                            this.nextToken();
                            Loc lookingForElseSave_1 = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.value.copy();
                            _body_1 = this.parseStatement(2, null, null);
                            this.lookingForElse = lookingForElseSave_1.copy();
                            this.check(TOK.while_);
                            this.check(TOK.leftParentheses);
                            condition_1 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                this.nextToken();
                            }
                            else
                            {
                                this.error(new BytePtr("terminating `;` required after do-while statement"));
                            }
                            s = new DoStatement(loc, _body_1, condition_1, this.token.value.loc);
                            break;
                        case 186:
                            Statement _init = null;
                            Expression condition_2 = null;
                            Expression increment = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                _init = null;
                                this.nextToken();
                            }
                            else
                            {
                                Loc lookingForElseSave_2 = this.lookingForElse.copy();
                                this.lookingForElse = Loc.initial.value.copy();
                                _init = this.parseStatement(0, null, null);
                                this.lookingForElse = lookingForElseSave_2.copy();
                            }
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                condition_2 = null;
                                this.nextToken();
                            }
                            else
                            {
                                condition_2 = this.parseExpression();
                                this.check(TOK.semicolon, new BytePtr("`for` condition"));
                            }
                            if (((this.token.value.value & 0xFF) == 2))
                            {
                                increment = null;
                                this.nextToken();
                            }
                            else
                            {
                                increment = this.parseExpression();
                                this.check(TOK.rightParentheses);
                            }
                            Loc endloc_1 = new Loc();
                            Statement _body_2 = this.parseStatement(2, null, ptr(endloc_1));
                            s = new ForStatement(loc, _init, condition_2, increment, _body_2, endloc_1);
                            break;
                        case 201:
                        case 202:
                            s = this.parseForeach00(loc);
                            break;
                        case 183:
                            Parameter param = null;
                            Expression condition_3 = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            long storageClass = 0L;
                            long stc = 0L;
                        /*LagainStc:*/
                        case -5:
                        __dispatch82 = 0;
                            if (stc != 0)
                            {
                                storageClass = this.appendStorageClass(storageClass, stc);
                                this.nextToken();
                            }
                            {
                                int __dispatch83 = 0;
                                dispatched_83:
                                do {
                                    switch (__dispatch83 != 0 ? __dispatch83 : (this.token.value.value & 0xFF))
                                    {
                                        case 210:
                                            stc = 2097152L;
                                            /*goto LagainStc*/{ __dispatch82 = -5; continue dispatched_82; }
                                        case 179:
                                            stc = 256L;
                                            /*goto LagainStc*/{ __dispatch82 = -5; continue dispatched_82; }
                                        case 171:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 4L;
                                                /*goto LagainStc*/{ __dispatch82 = -5; continue dispatched_82; }
                                            }
                                            break;
                                        case 182:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 1048576L;
                                                /*goto LagainStc*/{ __dispatch82 = -5; continue dispatched_82; }
                                            }
                                            break;
                                        case 224:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 536870912L;
                                                /*goto LagainStc*/{ __dispatch82 = -5; continue dispatched_82; }
                                            }
                                            break;
                                        case 177:
                                            if (((this.peekNext() & 0xFF) != 1))
                                            {
                                                stc = 2147483648L;
                                                /*goto LagainStc*/{ __dispatch82 = -5; continue dispatched_82; }
                                            }
                                            break;
                                        default:
                                        break;
                                    }
                                } while(__dispatch83 != 0);
                            }
                            Ptr<Token> n = this.peek(ptr(this.token));
                            if ((storageClass != 0L) && ((this.token.value.value & 0xFF) == 120) && (((n.get()).value & 0xFF) != 90) && (((n.get()).value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("found `%s` while expecting `=` or identifier"), (n.get()).toChars());
                            }
                            else if ((storageClass != 0L) && ((this.token.value.value & 0xFF) == 120) && (((n.get()).value & 0xFF) == 90))
                            {
                                Identifier ai = this.token.value.ident;
                                Type at = null;
                                this.nextToken();
                                this.check(TOK.assign);
                                param = new Parameter(storageClass, at, ai, null, null);
                            }
                            else if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.must, TOK.assign, null))
                            {
                                Identifier ai_1 = null;
                                Type at_1 = this.parseType(ptr(ai_1), null);
                                this.check(TOK.assign);
                                param = new Parameter(storageClass, at_1, ai_1, null, null);
                            }
                            condition_3 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            {
                                Loc lookingForElseSave_3 = this.lookingForElse.copy();
                                this.lookingForElse = loc.copy();
                                ifbody = this.parseStatement(2, null, null);
                                this.lookingForElse = lookingForElseSave_3.copy();
                            }
                            if (((this.token.value.value & 0xFF) == 184))
                            {
                                Loc elseloc = this.token.value.loc.copy();
                                this.nextToken();
                                elsebody = this.parseStatement(2, null, null);
                                this.checkDanglingElse(elseloc);
                            }
                            else
                            {
                                elsebody = null;
                            }
                            if ((condition_3 != null) && (ifbody != null))
                            {
                                s = new IfStatement(loc, param, condition_3, ifbody, elsebody, this.token.value.loc);
                            }
                            else
                            {
                                s = null;
                            }
                            break;
                        case 184:
                            this.error(new BytePtr("found `else` without a corresponding `if`, `version` or `debug` statement"));
                            /*goto Lerror*/{ __dispatch82 = -6; continue dispatched_82; }
                        case 203:
                            if ((((this.peek(ptr(this.token)).get()).value & 0xFF) != 1))
                            {
                                /*goto Ldeclaration*/{ __dispatch82 = -1; continue dispatched_82; }
                            }
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("scope identifier expected"));
                                /*goto Lerror*/{ __dispatch82 = -6; continue dispatched_82; }
                            }
                            else
                            {
                                byte t_4 = TOK.onScopeExit;
                                Identifier id = this.token.value.ident;
                                if ((pequals(id, Id.exit)))
                                {
                                    t_4 = TOK.onScopeExit;
                                }
                                else if ((pequals(id, Id.failure)))
                                {
                                    t_4 = TOK.onScopeFailure;
                                }
                                else if ((pequals(id, Id.success)))
                                {
                                    t_4 = TOK.onScopeSuccess;
                                }
                                else
                                {
                                    this.error(new BytePtr("valid scope identifiers are `exit`, `failure`, or `success`, not `%s`"), id.toChars());
                                }
                                this.nextToken();
                                this.check(TOK.rightParentheses);
                                Statement st = this.parseStatement(2, null, null);
                                s = new ScopeGuardStatement(loc, t_4, st);
                                break;
                            }
                        case 173:
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.error(new BytePtr("debug conditions can only be declared at module scope"));
                                this.nextToken();
                                this.nextToken();
                                /*goto Lerror*/{ __dispatch82 = -6; continue dispatched_82; }
                            }
                            cond = this.parseDebugCondition();
                            /*goto Lcondition*/{ __dispatch82 = -3; continue dispatched_82; }
                        case 33:
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 90))
                            {
                                this.error(new BytePtr("version conditions can only be declared at module scope"));
                                this.nextToken();
                                this.nextToken();
                                /*goto Lerror*/{ __dispatch82 = -6; continue dispatched_82; }
                            }
                            cond = this.parseVersionCondition();
                            /*goto Lcondition*/{ __dispatch82 = -3; continue dispatched_82; }
                        /*Lcondition:*/
                        case -3:
                        __dispatch82 = 0;
                            {
                                Loc lookingForElseSave_4 = this.lookingForElse.copy();
                                this.lookingForElse = loc.copy();
                                ifbody = this.parseStatement(0, null, null);
                                this.lookingForElse = lookingForElseSave_4.copy();
                            }
                            elsebody = null;
                            if (((this.token.value.value & 0xFF) == 184))
                            {
                                Loc elseloc_1 = this.token.value.loc.copy();
                                this.nextToken();
                                elsebody = this.parseStatement(0, null, null);
                                this.checkDanglingElse(elseloc_1);
                            }
                            s = new ConditionalStatement(loc, cond, ifbody, elsebody);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                            {
                                s = new ScopeStatement(loc, s, this.token.value.loc);
                            }
                            break;
                        case 40:
                            Identifier ident_1 = null;
                            Ptr<DArray<Expression>> args = null;
                            Statement _body_3 = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("`pragma(identifier)` expected"));
                                /*goto Lerror*/{ __dispatch82 = -6; continue dispatched_82; }
                            }
                            ident_1 = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 99) && ((this.peekNext() & 0xFF) != 2))
                            {
                                args = this.parseArguments();
                            }
                            else
                            {
                                this.check(TOK.rightParentheses);
                            }
                            if (((this.token.value.value & 0xFF) == 9))
                            {
                                this.nextToken();
                                _body_3 = null;
                            }
                            else
                            {
                                _body_3 = this.parseStatement(1, null, null);
                            }
                            s = new PragmaStatement(loc, ident_1, args, _body_3);
                            break;
                        case 188:
                            isfinal = false;
                            /*goto Lswitch*/{ __dispatch82 = -4; continue dispatched_82; }
                        /*Lswitch:*/
                        case -4:
                        __dispatch82 = 0;
                            {
                                this.nextToken();
                                this.check(TOK.leftParentheses);
                                Expression condition_4 = this.parseExpression();
                                this.check(TOK.rightParentheses);
                                Statement _body_4 = this.parseStatement(2, null, null);
                                s = new SwitchStatement(loc, condition_4, _body_4, isfinal);
                                break;
                            }
                        case 189:
                            Expression exp_1 = null;
                            DArray<Expression> cases = new DArray<Expression>();
                            try {
                                Expression last = null;
                                for (; 1 != 0;){
                                    this.nextToken();
                                    exp_1 = this.parseAssignExp();
                                    cases.push(exp_1);
                                    if (((this.token.value.value & 0xFF) != 99))
                                    {
                                        break;
                                    }
                                }
                                this.check(TOK.colon);
                                if (((this.token.value.value & 0xFF) == 31))
                                {
                                    if ((cases.length.value > 1))
                                    {
                                        this.error(new BytePtr("only one `case` allowed for start of case range"));
                                    }
                                    this.nextToken();
                                    this.check(TOK.case_);
                                    last = this.parseAssignExp();
                                    this.check(TOK.colon);
                                }
                                if ((flags & ParseStatementFlags.curlyScope) != 0)
                                {
                                    Ptr<DArray<Statement>> statements_1 = refPtr(new DArray<Statement>());
                                    for (; ((this.token.value.value & 0xFF) != 189) && ((this.token.value.value & 0xFF) != 190) && ((this.token.value.value & 0xFF) != 11) && ((this.token.value.value & 0xFF) != 6);){
                                        (statements_1.get()).push(this.parseStatement(9, null, null));
                                    }
                                    s = new CompoundStatement(loc, statements_1);
                                }
                                else
                                {
                                    s = this.parseStatement(1, null, null);
                                }
                                s = new ScopeStatement(loc, s, this.token.value.loc);
                                if (last != null)
                                {
                                    s = new CaseRangeStatement(loc, exp_1, last, s);
                                }
                                else
                                {
                                    {
                                        int i_1 = cases.length.value;
                                        for (; i_1 != 0;i_1--){
                                            exp_1 = cases.get(i_1 - 1);
                                            s = new CaseStatement(loc, exp_1, s);
                                        }
                                    }
                                }
                                break;
                            }
                            finally {
                            }
                        case 190:
                            this.nextToken();
                            this.check(TOK.colon);
                            if ((flags & ParseStatementFlags.curlyScope) != 0)
                            {
                                Ptr<DArray<Statement>> statements_2 = refPtr(new DArray<Statement>());
                                for (; ((this.token.value.value & 0xFF) != 189) && ((this.token.value.value & 0xFF) != 190) && ((this.token.value.value & 0xFF) != 11) && ((this.token.value.value & 0xFF) != 6);){
                                    (statements_2.get()).push(this.parseStatement(9, null, null));
                                }
                                s = new CompoundStatement(loc, statements_2);
                            }
                            else
                            {
                                s = this.parseStatement(1, null, null);
                            }
                            s = new ScopeStatement(loc, s, this.token.value.loc);
                            s = new DefaultStatement(loc, s);
                            break;
                        case 195:
                            Expression exp_2 = null;
                            this.nextToken();
                            exp_2 = ((this.token.value.value & 0xFF) == 9) ? null : this.parseExpression();
                            this.check(TOK.semicolon, new BytePtr("`return` statement"));
                            s = new ReturnStatement(loc, exp_2);
                            break;
                        case 191:
                            Identifier ident_2 = null;
                            this.nextToken();
                            ident_2 = null;
                            if (((this.token.value.value & 0xFF) == 120))
                            {
                                ident_2 = this.token.value.ident;
                                this.nextToken();
                            }
                            this.check(TOK.semicolon, new BytePtr("`break` statement"));
                            s = new BreakStatement(loc, ident_2);
                            break;
                        case 192:
                            Identifier ident_3 = null;
                            this.nextToken();
                            ident_3 = null;
                            if (((this.token.value.value & 0xFF) == 120))
                            {
                                ident_3 = this.token.value.ident;
                                this.nextToken();
                            }
                            this.check(TOK.semicolon, new BytePtr("`continue` statement"));
                            s = new ContinueStatement(loc, ident_3);
                            break;
                        case 196:
                            Identifier ident_4 = null;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 190))
                            {
                                this.nextToken();
                                s = new GotoDefaultStatement(loc);
                            }
                            else if (((this.token.value.value & 0xFF) == 189))
                            {
                                Expression exp_3 = null;
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) != 9))
                                {
                                    exp_3 = this.parseExpression();
                                }
                                s = new GotoCaseStatement(loc, exp_3);
                            }
                            else
                            {
                                if (((this.token.value.value & 0xFF) != 120))
                                {
                                    this.error(new BytePtr("identifier expected following `goto`"));
                                    ident_4 = null;
                                }
                                else
                                {
                                    ident_4 = this.token.value.ident;
                                    this.nextToken();
                                }
                                s = new GotoStatement(loc, ident_4);
                            }
                            this.check(TOK.semicolon, new BytePtr("`goto` statement"));
                            break;
                        case 194:
                            Expression exp_4 = null;
                            Statement _body_5 = null;
                            Ptr<Token> t_5 = this.peek(ptr(this.token));
                            if (this.skipAttributes(t_5, ptr(t_5)) && (((t_5.get()).value & 0xFF) == 153))
                            {
                                /*goto Ldeclaration*/{ __dispatch82 = -1; continue dispatched_82; }
                            }
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.nextToken();
                                exp_4 = this.parseExpression();
                                this.check(TOK.rightParentheses);
                            }
                            else
                            {
                                exp_4 = null;
                            }
                            _body_5 = this.parseStatement(2, null, null);
                            s = new SynchronizedStatement(loc, exp_4, _body_5);
                            break;
                        case 193:
                            Expression exp_5 = null;
                            Statement _body_6 = null;
                            Loc endloc_2 = loc.copy();
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            exp_5 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            _body_6 = this.parseStatement(2, null, ptr(endloc_2));
                            s = new WithStatement(loc, exp_5, _body_6, endloc_2);
                            break;
                        case 197:
                            Statement _body_7 = null;
                            Ptr<DArray<Catch>> catches = null;
                            Statement finalbody = null;
                            this.nextToken();
                            Loc lookingForElseSave_5 = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.value.copy();
                            _body_7 = this.parseStatement(2, null, null);
                            this.lookingForElse = lookingForElseSave_5.copy();
                            for (; ((this.token.value.value & 0xFF) == 198);){
                                Statement handler = null;
                                Catch c = null;
                                Type t_6 = null;
                                Identifier id_1 = null;
                                Loc catchloc = this.token.value.loc.copy();
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 5) || ((this.token.value.value & 0xFF) != 1))
                                {
                                    t_6 = null;
                                    id_1 = null;
                                }
                                else
                                {
                                    this.check(TOK.leftParentheses);
                                    id_1 = null;
                                    t_6 = this.parseType(ptr(id_1), null);
                                    this.check(TOK.rightParentheses);
                                }
                                handler = this.parseStatement(0, null, null);
                                c = new Catch(catchloc, t_6, id_1, handler);
                                if (catches == null)
                                {
                                    catches = refPtr(new DArray<Catch>());
                                }
                                (catches.get()).push(c);
                            }
                            if (((this.token.value.value & 0xFF) == 199))
                            {
                                this.nextToken();
                                finalbody = this.parseStatement(2, null, null);
                            }
                            s = _body_7;
                            if ((catches == null) && (finalbody == null))
                            {
                                this.error(new BytePtr("`catch` or `finally` expected following `try`"));
                            }
                            else
                            {
                                if (catches != null)
                                {
                                    s = new TryCatchStatement(loc, _body_7, catches);
                                }
                                if (finalbody != null)
                                {
                                    s = new TryFinallyStatement(loc, s, finalbody);
                                }
                            }
                            break;
                        case 21:
                            Expression exp_6 = null;
                            this.nextToken();
                            exp_6 = this.parseExpression();
                            this.check(TOK.semicolon, new BytePtr("`throw` statement"));
                            s = new ThrowStatement(loc, exp_6);
                            break;
                        case 200:
                            Loc labelloc = new Loc();
                            this.nextToken();
                            long stc_1 = this.parsePostfix(0L, null);
                            if ((stc_1 & 2685403140L) != 0)
                            {
                                this.error(new BytePtr("`const`/`immutable`/`shared`/`inout` attributes are not allowed on `asm` blocks"));
                            }
                            this.check(TOK.leftCurly);
                            Ptr<Token> toklist = null;
                            Ptr<Ptr<Token>> ptoklist = pcopy(ptr(toklist));
                            Identifier label = null;
                            Ptr<DArray<Statement>> statements_3 = refPtr(new DArray<Statement>());
                            int nestlevel = 0;
                        L_outer37:
                            for (; 1 != 0;){
                                {
                                    int __dispatch84 = 0;
                                    dispatched_84:
                                    do {
                                        switch (__dispatch84 != 0 ? __dispatch84 : (this.token.value.value & 0xFF))
                                        {
                                            case 120:
                                                if (toklist == null)
                                                {
                                                    Ptr<Token> t_7 = this.peek(ptr(this.token));
                                                    if ((((t_7.get()).value & 0xFF) == 7))
                                                    {
                                                        label = this.token.value.ident;
                                                        labelloc = this.token.value.loc.copy();
                                                        this.nextToken();
                                                        this.nextToken();
                                                        continue L_outer37;
                                                    }
                                                }
                                                /*goto default*/ { __dispatch84 = -1; continue dispatched_84; }
                                            case 5:
                                                nestlevel += 1;
                                                /*goto default*/ { __dispatch84 = -1; continue dispatched_84; }
                                            case 6:
                                                if ((nestlevel > 0))
                                                {
                                                    nestlevel -= 1;
                                                    /*goto default*/ { __dispatch84 = -1; continue dispatched_84; }
                                                }
                                                if ((toklist != null) || (label != null))
                                                {
                                                    this.error(new BytePtr("`asm` statements must end in `;`"));
                                                }
                                                break;
                                            case 9:
                                                if ((nestlevel != 0))
                                                {
                                                    this.error(new BytePtr("mismatched number of curly brackets"));
                                                }
                                                s = null;
                                                if ((toklist != null) || (label != null))
                                                {
                                                    s = new AsmStatement(this.token.value.loc, toklist);
                                                    toklist = null;
                                                    ptoklist = pcopy(ptr(toklist));
                                                    if (label != null)
                                                    {
                                                        s = new LabelStatement(labelloc, label, s);
                                                        label = null;
                                                    }
                                                    (statements_3.get()).push(s);
                                                }
                                                this.nextToken();
                                                continue L_outer37;
                                            case 11:
                                                this.error(new BytePtr("matching `}` expected, not end of file"));
                                                /*goto Lerror*/{ __dispatch82 = -6; continue dispatched_82; }
                                            default:
                                            __dispatch84 = 0;
                                            ptoklist.set(0, this.allocateToken());
                                            (ptoklist.get()).set(0, (ptr(this.token)));
                                            ptoklist = pcopy((ptr(ptoklist.get().get().next)));
                                            ptoklist.set(0, null);
                                            this.nextToken();
                                            continue L_outer37;
                                        }
                                    } while(__dispatch84 != 0);
                                }
                                break;
                            }
                            s = new CompoundAsmStatement(loc, statements_3, stc_1);
                            this.nextToken();
                            break;
                        case 157:
                            if (((this.peekNext() & 0xFF) == 1))
                            {
                                Expression e_1 = this.parseExpression();
                                this.check(TOK.semicolon);
                                s = new ExpStatement(loc, e_1);
                            }
                            else
                            {
                                Ptr<DArray<Dsymbol>> imports_1 = this.parseImport();
                                s = new ImportStatement(loc, imports_1);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                {
                                    s = new ScopeStatement(loc, s, this.token.value.loc);
                                }
                            }
                            break;
                        case 36:
                            Dsymbol d_4 = this.parseTemplateDeclaration(false);
                            s = new ExpStatement(loc, d_4);
                            break;
                        default:
                        __dispatch82 = 0;
                        this.error(new BytePtr("found `%s` instead of statement"), this.token.value.toChars());
                        /*goto Lerror*/{ __dispatch82 = -6; continue dispatched_82; }
                    /*Lerror:*/
                    case -6:
                    __dispatch82 = 0;
                        for (; ((this.token.value.value & 0xFF) != 6) && ((this.token.value.value & 0xFF) != 9) && ((this.token.value.value & 0xFF) != 11);) {
                            this.nextToken();
                        }
                        if (((this.token.value.value & 0xFF) == 9))
                        {
                            this.nextToken();
                        }
                        s = null;
                        break;
                    }
                } while(__dispatch82 != 0);
            }
            if (pEndloc != null)
            {
                pEndloc.set(0, this.prevloc);
            }
            return s;
        }

        // defaulted all parameters starting with #3
        public  Statement parseStatement(int flags, Ptr<BytePtr> endPtr) {
            return parseStatement(flags, endPtr, null);
        }

        // defaulted all parameters starting with #2
        public  Statement parseStatement(int flags) {
            return parseStatement(flags, null, null);
        }

        public  Initializer parseInitializer() {
            StructInitializer _is = null;
            ArrayInitializer ia = null;
            ExpInitializer ie = null;
            Expression e = null;
            Identifier id = null;
            Initializer value = null;
            int comma = 0;
            Loc loc = this.token.value.loc.copy();
            Ptr<Token> t = null;
            int braces = 0;
            int brackets = 0;
            {
                int __dispatch85 = 0;
                dispatched_85:
                do {
                    switch (__dispatch85 != 0 ? __dispatch85 : (this.token.value.value & 0xFF))
                    {
                        case 5:
                            braces = 1;
                            {
                                t = this.peek(ptr(this.token));
                            L_outer38:
                                for (; 1 != 0;t = this.peek(t)){
                                    {
                                        int __dispatch86 = 0;
                                        dispatched_86:
                                        do {
                                            switch (__dispatch86 != 0 ? __dispatch86 : ((t.get()).value & 0xFF))
                                            {
                                                case 200:
                                                case 153:
                                                case 173:
                                                case 156:
                                                case 183:
                                                case 154:
                                                case 40:
                                                case 203:
                                                case 9:
                                                case 152:
                                                case 188:
                                                case 194:
                                                case 197:
                                                case 155:
                                                case 33:
                                                case 185:
                                                case 193:
                                                    if ((braces == 1))
                                                    {
                                                        /*goto Lexpression*/{ __dispatch85 = -1; continue dispatched_85; }
                                                    }
                                                    continue L_outer38;
                                                case 5:
                                                    braces++;
                                                    continue L_outer38;
                                                case 6:
                                                    if (((braces -= 1) == 0))
                                                    {
                                                        break;
                                                    }
                                                    continue L_outer38;
                                                case 11:
                                                    break;
                                                default:
                                                continue L_outer38;
                                            }
                                        } while(__dispatch86 != 0);
                                    }
                                    break;
                                }
                            }
                            _is = new StructInitializer(loc);
                            this.nextToken();
                            comma = 2;
                            for (; 1 != 0;){
                                switch ((this.token.value.value & 0xFF))
                                {
                                    case 120:
                                        if ((comma == 1))
                                        {
                                            this.error(new BytePtr("comma expected separating field initializers"));
                                        }
                                        t = this.peek(ptr(this.token));
                                        if ((((t.get()).value & 0xFF) == 7))
                                        {
                                            id = this.token.value.ident;
                                            this.nextToken();
                                            this.nextToken();
                                        }
                                        else
                                        {
                                            id = null;
                                        }
                                        value = this.parseInitializer();
                                        _is.addInit(id, value);
                                        comma = 1;
                                        continue;
                                    case 99:
                                        if ((comma == 2))
                                        {
                                            this.error(new BytePtr("expression expected, not `,`"));
                                        }
                                        this.nextToken();
                                        comma = 2;
                                        continue;
                                    case 6:
                                        this.nextToken();
                                        break;
                                    case 11:
                                        this.error(new BytePtr("found end of file instead of initializer"));
                                        break;
                                    default:
                                    if ((comma == 1))
                                    {
                                        this.error(new BytePtr("comma expected separating field initializers"));
                                    }
                                    value = this.parseInitializer();
                                    _is.addInit(null, value);
                                    comma = 1;
                                    continue;
                                }
                                break;
                            }
                            return _is;
                        case 3:
                            brackets = 1;
                            {
                                t = this.peek(ptr(this.token));
                            L_outer39:
                                for (; 1 != 0;t = this.peek(t)){
                                    {
                                        int __dispatch88 = 0;
                                        dispatched_88:
                                        do {
                                            switch (__dispatch88 != 0 ? __dispatch88 : ((t.get()).value & 0xFF))
                                            {
                                                case 3:
                                                    brackets++;
                                                    continue L_outer39;
                                                case 4:
                                                    if (((brackets -= 1) == 0))
                                                    {
                                                        t = this.peek(t);
                                                        if ((((t.get()).value & 0xFF) != 9) && (((t.get()).value & 0xFF) != 99) && (((t.get()).value & 0xFF) != 4) && (((t.get()).value & 0xFF) != 6))
                                                        {
                                                            /*goto Lexpression*/{ __dispatch85 = -1; continue dispatched_85; }
                                                        }
                                                        break;
                                                    }
                                                    continue L_outer39;
                                                case 11:
                                                    break;
                                                default:
                                                continue L_outer39;
                                            }
                                        } while(__dispatch88 != 0);
                                    }
                                    break;
                                }
                            }
                            ia = new ArrayInitializer(loc);
                            this.nextToken();
                            comma = 2;
                            for (; 1 != 0;){
                                switch ((this.token.value.value & 0xFF))
                                {
                                    default:
                                    if ((comma == 1))
                                    {
                                        this.error(new BytePtr("comma expected separating array initializers, not `%s`"), this.token.value.toChars());
                                        this.nextToken();
                                        break;
                                    }
                                    e = this.parseAssignExp();
                                    if (e == null)
                                    {
                                        break;
                                    }
                                    if (((this.token.value.value & 0xFF) == 7))
                                    {
                                        this.nextToken();
                                        value = this.parseInitializer();
                                    }
                                    else
                                    {
                                        value = new ExpInitializer(e.loc.value, e);
                                        e = null;
                                    }
                                    ia.addInit(e, value);
                                    comma = 1;
                                    continue;
                                    case 5:
                                    case 3:
                                        if ((comma == 1))
                                        {
                                            this.error(new BytePtr("comma expected separating array initializers, not `%s`"), this.token.value.toChars());
                                        }
                                        value = this.parseInitializer();
                                        if (((this.token.value.value & 0xFF) == 7))
                                        {
                                            this.nextToken();
                                            ExpInitializer expInit = value.isExpInitializer();
                                            assert(expInit != null);
                                            e = expInit.exp.value;
                                            value = this.parseInitializer();
                                        }
                                        else
                                        {
                                            e = null;
                                        }
                                        ia.addInit(e, value);
                                        comma = 1;
                                        continue;
                                    case 99:
                                        if ((comma == 2))
                                        {
                                            this.error(new BytePtr("expression expected, not `,`"));
                                        }
                                        this.nextToken();
                                        comma = 2;
                                        continue;
                                    case 4:
                                        this.nextToken();
                                        break;
                                    case 11:
                                        this.error(new BytePtr("found `%s` instead of array initializer"), this.token.value.toChars());
                                        break;
                                }
                                break;
                            }
                            return ia;
                        case 128:
                            t = this.peek(ptr(this.token));
                            if ((((t.get()).value & 0xFF) == 9) || (((t.get()).value & 0xFF) == 99))
                            {
                                this.nextToken();
                                return new VoidInitializer(loc);
                            }
                            /*goto Lexpression*/{ __dispatch85 = -1; continue dispatched_85; }
                        default:
                    /*Lexpression:*/
                    case -1:
                    __dispatch85 = 0;
                        e = this.parseAssignExp();
                        ie = new ExpInitializer(loc, e);
                        return ie;
                    }
                } while(__dispatch85 != 0);
            }
        }

        public  Expression parseDefaultInitExp() {
            Expression e = null;
            Ptr<Token> t = this.peek(ptr(this.token));
            try {
                if ((((t.get()).value & 0xFF) == 99) || (((t.get()).value & 0xFF) == 2))
                {
                    {
                        int __dispatch90 = 0;
                        dispatched_90:
                        do {
                            switch (__dispatch90 != 0 ? __dispatch90 : (this.token.value.value & 0xFF))
                            {
                                case 219:
                                    e = new FileInitExp(this.token.value.loc, TOK.file);
                                    break;
                                case 220:
                                    e = new FileInitExp(this.token.value.loc, TOK.fileFullPath);
                                    break;
                                case 218:
                                    e = new LineInitExp(this.token.value.loc);
                                    break;
                                case 221:
                                    e = new ModuleInitExp(this.token.value.loc);
                                    break;
                                case 222:
                                    e = new FuncInitExp(this.token.value.loc);
                                    break;
                                case 223:
                                    e = new PrettyFuncInitExp(this.token.value.loc);
                                    break;
                                default:
                                /*goto LExp*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch90 != 0);
                    }
                    this.nextToken();
                    return e;
                }
            }
            catch(Dispatch0 __d){}
        /*LExp:*/
            return this.parseAssignExp();
        }

        public  void check(Loc loc, byte value) {
            if (((this.token.value.value & 0xFF) != (value & 0xFF)))
            {
                this.error(loc, new BytePtr("found `%s` when expecting `%s`"), this.token.value.toChars(), Token.toChars(value));
            }
            this.nextToken();
        }

        public  void check(byte value) {
            this.check(this.token.value.loc, value);
        }

        public  void check(byte value, BytePtr string) {
            if (((this.token.value.value & 0xFF) != (value & 0xFF)))
            {
                this.error(new BytePtr("found `%s` when expecting `%s` following %s"), this.token.value.toChars(), Token.toChars(value), string);
            }
            this.nextToken();
        }

        public  void checkParens(byte value, Expression e) {
            if ((precedence.get((e.op.value & 0xFF)) == PREC.rel) && (e.parens == 0))
            {
                this.error(e.loc.value, new BytePtr("`%s` must be surrounded by parentheses when next to operator `%s`"), e.toChars(), Token.toChars(value));
            }
        }


        public static class NeedDeclaratorId 
        {
            public static final int no = 0;
            public static final int opt = 1;
            public static final int must = 2;
            public static final int mustIfDstyle = 3;
        }

        public  boolean isDeclaration(Ptr<Token> t, int needId, byte endtok, Ptr<Ptr<Token>> pt) {
            int haveId = 0;
            int haveTpl = 0;
            for (; 1 != 0;){
                if ((((t.get()).value & 0xFF) == 171) || (((t.get()).value & 0xFF) == 182) || (((t.get()).value & 0xFF) == 177) || (((t.get()).value & 0xFF) == 224) && (((this.peek(t).get()).value & 0xFF) != 1))
                {
                    t = this.peek(t);
                    continue;
                }
                break;
            }
            try {
                try {
                    if (!this.isBasicType(ptr(t)))
                    {
                        /*goto Lisnot*/throw Dispatch1.INSTANCE;
                    }
                    if (!this.isDeclarator(ptr(t), ptr(haveId), ptr(haveTpl), endtok, needId != NeedDeclaratorId.mustIfDstyle))
                    {
                        /*goto Lisnot*/throw Dispatch1.INSTANCE;
                    }
                    if ((needId == NeedDeclaratorId.no) && (haveId == 0) || (needId == NeedDeclaratorId.opt) || (needId == NeedDeclaratorId.must) && (haveId != 0) || (needId == NeedDeclaratorId.mustIfDstyle) && (haveId != 0))
                    {
                        if (pt != null)
                        {
                            pt.set(0, t);
                        }
                        /*goto Lis*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lisnot*/throw Dispatch1.INSTANCE;
                }
                catch(Dispatch0 __d){}
            /*Lis:*/
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lisnot:*/
            return false;
        }

        public  boolean isBasicType(Ptr<Ptr<Token>> pt) {
            Ptr<Token> t = pt.get();
            try {
                {
                    int __dispatch91 = 0;
                    dispatched_91:
                    do {
                        switch (__dispatch91 != 0 ? __dispatch91 : ((t.get()).value & 0xFF))
                        {
                            case 149:
                            case 150:
                            case 151:
                            case 148:
                            case 129:
                            case 130:
                            case 131:
                            case 132:
                            case 133:
                            case 134:
                            case 135:
                            case 136:
                            case 137:
                            case 138:
                            case 139:
                            case 140:
                            case 141:
                            case 142:
                            case 143:
                            case 144:
                            case 145:
                            case 146:
                            case 147:
                            case 128:
                                t = this.peek(t);
                                break;
                            case 120:
                            /*L5:*/
                            case -1:
                            __dispatch91 = 0;
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 91))
                                {
                                    /*goto L4*/throw Dispatch.INSTANCE;
                                }
                                /*goto L3*/{ __dispatch91 = -3; continue dispatched_91; }
                            L_outer40:
                                for (; 1 != 0;){
                                /*L2:*/
                                case -2:
                                __dispatch91 = 0;
                                    t = this.peek(t);
                                /*L3:*/
                                case -3:
                                __dispatch91 = 0;
                                    if ((((t.get()).value & 0xFF) == 97))
                                    {
                                    /*Ldot:*/
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) != 120))
                                        {
                                            /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                        }
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) != 91))
                                        {
                                            /*goto L3*/{ __dispatch91 = -3; continue dispatched_91; }
                                        }
                                    /*L4:*/
                                        t = this.peek(t);
                                        {
                                            int __dispatch92 = 0;
                                            dispatched_92:
                                            do {
                                                switch (__dispatch92 != 0 ? __dispatch92 : ((t.get()).value & 0xFF))
                                                {
                                                    case 120:
                                                        /*goto L5*/{ __dispatch91 = -1; continue dispatched_91; }
                                                    case 1:
                                                        if (!this.skipParens(t, ptr(t)))
                                                        {
                                                            /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                                        }
                                                        /*goto L3*/{ __dispatch91 = -3; continue dispatched_91; }
                                                    case 149:
                                                    case 150:
                                                    case 151:
                                                    case 148:
                                                    case 129:
                                                    case 130:
                                                    case 131:
                                                    case 132:
                                                    case 133:
                                                    case 134:
                                                    case 135:
                                                    case 136:
                                                    case 137:
                                                    case 138:
                                                    case 139:
                                                    case 140:
                                                    case 141:
                                                    case 142:
                                                    case 143:
                                                    case 144:
                                                    case 145:
                                                    case 146:
                                                    case 147:
                                                    case 128:
                                                    case 105:
                                                    case 106:
                                                    case 107:
                                                    case 108:
                                                    case 109:
                                                    case 110:
                                                    case 111:
                                                    case 112:
                                                    case 113:
                                                    case 114:
                                                    case 115:
                                                    case 116:
                                                    case 13:
                                                    case 15:
                                                    case 16:
                                                    case 117:
                                                    case 118:
                                                    case 119:
                                                    case 121:
                                                    case 122:
                                                    case 219:
                                                    case 220:
                                                    case 218:
                                                    case 221:
                                                    case 222:
                                                    case 223:
                                                        /*goto L2*/{ __dispatch91 = -2; continue dispatched_91; }
                                                    default:
                                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                                }
                                            } while(__dispatch92 != 0);
                                        }
                                    }
                                    break;
                                }
                                break;
                            case 97:
                                /*goto Ldot*/throw Dispatch.INSTANCE;
                            case 39:
                            case 229:
                                t = this.peek(t);
                                if (!this.skipParens(t, ptr(t)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                /*goto L3*/{ __dispatch91 = -3; continue dispatched_91; }
                            case 213:
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) != 1))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                Ptr<Token> lp = t;
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) != 120) || (!pequals((t.get()).ident, Id.getMember.value)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                if (!this.skipParens(lp, ptr(lp)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                if ((((lp.get()).value & 0xFF) != 120))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                break;
                            case 171:
                            case 182:
                            case 224:
                            case 177:
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) != 1))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                t = this.peek(t);
                                if (!this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightParentheses, ptr(t)))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                t = this.peek(t);
                                break;
                            default:
                            /*goto Lfalse*/throw Dispatch0.INSTANCE;
                        }
                    } while(__dispatch91 != 0);
                }
                pt.set(0, t);
                return true;
            }
            catch(Dispatch0 __d){}
        /*Lfalse:*/
            return false;
        }

        public  boolean isDeclarator(Ptr<Ptr<Token>> pt, IntPtr haveId, IntPtr haveTpl, byte endtok, boolean allowAltSyntax) {
            Ptr<Token> t = pt.get();
            int parens = 0;
            if ((((t.get()).value & 0xFF) == 90))
            {
                return false;
            }
            for (; 1 != 0;){
                parens = 0;
                switch (((t.get()).value & 0xFF))
                {
                    case 78:
                        t = this.peek(t);
                        continue;
                    case 3:
                        t = this.peek(t);
                        if ((((t.get()).value & 0xFF) == 4))
                        {
                            t = this.peek(t);
                        }
                        else if (this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightBracket, ptr(t)))
                        {
                            t = this.peek(t);
                            if ((((t.get()).value & 0xFF) == 97) && (((this.peek(t).get()).value & 0xFF) == 120))
                            {
                                t = this.peek(t);
                                t = this.peek(t);
                            }
                        }
                        else
                        {
                            if (!this.isExpression(ptr(t)))
                            {
                                return false;
                            }
                            if ((((t.get()).value & 0xFF) == 31))
                            {
                                t = this.peek(t);
                                if (!this.isExpression(ptr(t)))
                                {
                                    return false;
                                }
                                if ((((t.get()).value & 0xFF) != 4))
                                {
                                    return false;
                                }
                                t = this.peek(t);
                            }
                            else
                            {
                                if ((((t.get()).value & 0xFF) != 4))
                                {
                                    return false;
                                }
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 97) && (((this.peek(t).get()).value & 0xFF) == 120))
                                {
                                    t = this.peek(t);
                                    t = this.peek(t);
                                }
                            }
                        }
                        continue;
                    case 120:
                        if (haveId.get() != 0)
                        {
                            return false;
                        }
                        haveId.set(0, 1);
                        t = this.peek(t);
                        break;
                    case 1:
                        if (!allowAltSyntax)
                        {
                            return false;
                        }
                        t = this.peek(t);
                        if ((((t.get()).value & 0xFF) == 2))
                        {
                            return false;
                        }
                        if ((((t.get()).value & 0xFF) == 120))
                        {
                            Ptr<Token> t2 = this.peek(t);
                            if ((((t2.get()).value & 0xFF) == 2))
                            {
                                return false;
                            }
                        }
                        if (!this.isDeclarator(ptr(t), haveId, null, TOK.rightParentheses, true))
                        {
                            return false;
                        }
                        t = this.peek(t);
                        parens = 1;
                        break;
                    case 160:
                    case 161:
                        t = this.peek(t);
                        if (!this.isParameters(ptr(t)))
                        {
                            return false;
                        }
                        this.skipAttributes(t, ptr(t));
                        continue;
                    default:
                    break;
                }
                break;
            }
        L_outer41:
            for (; 1 != 0;){
                {
                    int __dispatch94 = 0;
                    dispatched_94:
                    do {
                        switch (__dispatch94 != 0 ? __dispatch94 : ((t.get()).value & 0xFF))
                        {
                            case 3:
                                parens = 0;
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 4))
                                {
                                    t = this.peek(t);
                                }
                                else if (this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightBracket, ptr(t)))
                                {
                                    t = this.peek(t);
                                }
                                else
                                {
                                    if (!this.isExpression(ptr(t)))
                                    {
                                        return false;
                                    }
                                    if ((((t.get()).value & 0xFF) != 4))
                                    {
                                        return false;
                                    }
                                    t = this.peek(t);
                                }
                                continue L_outer41;
                            case 1:
                                parens = 0;
                                {
                                    Ptr<Token> tk = this.peekPastParen(t);
                                    if ((tk) != null)
                                    {
                                        if ((((tk.get()).value & 0xFF) == 1))
                                        {
                                            if (haveTpl == null)
                                            {
                                                return false;
                                            }
                                            haveTpl.set(0, 1);
                                            t = tk;
                                        }
                                        else if ((((tk.get()).value & 0xFF) == 90))
                                        {
                                            if (haveTpl == null)
                                            {
                                                return false;
                                            }
                                            haveTpl.set(0, 1);
                                            pt.set(0, tk);
                                            return true;
                                        }
                                    }
                                }
                                if (!this.isParameters(ptr(t)))
                                {
                                    return false;
                                }
                                for (; 1 != 0;){
                                    switch (((t.get()).value & 0xFF))
                                    {
                                        case 171:
                                        case 182:
                                        case 224:
                                        case 177:
                                        case 215:
                                        case 216:
                                        case 195:
                                        case 203:
                                            t = this.peek(t);
                                            continue;
                                        case 225:
                                            t = this.peek(t);
                                            t = this.peek(t);
                                            continue;
                                        default:
                                        break;
                                    }
                                    break;
                                }
                                continue L_outer41;
                            case 2:
                            case 4:
                            case 90:
                            case 99:
                            case 10:
                            case 9:
                            case 5:
                            case 175:
                            case 176:
                            case 187:
                                __dispatch94 = 0;
                                if ((parens == 0) && ((endtok & 0xFF) == 0) || ((endtok & 0xFF) == ((t.get()).value & 0xFF)))
                                {
                                    pt.set(0, t);
                                    return true;
                                }
                                return false;
                            case 120:
                                if ((pequals((t.get()).ident, Id._body)))
                                {
                                    /*goto case*/{ __dispatch94 = 187; continue dispatched_94; }
                                }
                                /*goto default*/ { __dispatch94 = -2; continue dispatched_94; }
                            case 183:
                                return haveTpl != null ? true : false;
                            default:
                            __dispatch94 = 0;
                            return false;
                        }
                    } while(__dispatch94 != 0);
                }
            }
            throw new AssertionError("Unreachable code!");
        }

        // defaulted all parameters starting with #5
        public  boolean isDeclarator(Ptr<Ptr<Token>> pt, IntPtr haveId, IntPtr haveTpl, byte endtok) {
            return isDeclarator(pt, haveId, haveTpl, endtok, true);
        }

        public  boolean isParameters(Ptr<Ptr<Token>> pt) {
            Ptr<Token> t = pt.get();
            if ((((t.get()).value & 0xFF) != 1))
            {
                return false;
            }
            t = this.peek(t);
        L_outer42:
            for (; 1 != 0;t = this.peek(t)){
            /*L1:*/
                {
                    int __dispatch96 = 0;
                    dispatched_96:
                    do {
                        switch (__dispatch96 != 0 ? __dispatch96 : ((t.get()).value & 0xFF))
                        {
                            case 2:
                                break;
                            case 10:
                                t = this.peek(t);
                                break;
                            case 175:
                            case 176:
                            case 210:
                            case 178:
                            case 203:
                            case 170:
                            case 179:
                            case 195:
                                continue L_outer42;
                            case 171:
                            case 182:
                            case 224:
                            case 177:
                                t = this.peek(t);
                                if ((((t.get()).value & 0xFF) == 1))
                                {
                                    t = this.peek(t);
                                    if (!this.isDeclaration(t, NeedDeclaratorId.no, TOK.rightParentheses, ptr(t)))
                                    {
                                        return false;
                                    }
                                    t = this.peek(t);
                                    /*goto L2*/{ __dispatch96 = -1; continue dispatched_96; }
                                }
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            default:
                            {
                                if (!this.isBasicType(ptr(t)))
                                {
                                    return false;
                                }
                            /*L2:*/
                            case -1:
                            __dispatch96 = 0;
                                int tmp = 0;
                                if ((((t.get()).value & 0xFF) != 10) && !this.isDeclarator(ptr(t), ptr(tmp), null, TOK.reserved, true))
                                {
                                    return false;
                                }
                                if ((((t.get()).value & 0xFF) == 90))
                                {
                                    t = this.peek(t);
                                    if (!this.isExpression(ptr(t)))
                                    {
                                        return false;
                                    }
                                }
                                if ((((t.get()).value & 0xFF) == 10))
                                {
                                    t = this.peek(t);
                                    break;
                                }
                            }
                            if ((((t.get()).value & 0xFF) == 99))
                            {
                                continue L_outer42;
                            }
                            break;
                        }
                    } while(__dispatch96 != 0);
                }
                break;
            }
            if ((((t.get()).value & 0xFF) != 2))
            {
                return false;
            }
            t = this.peek(t);
            pt.set(0, t);
            return true;
        }

        public  boolean isExpression(Ptr<Ptr<Token>> pt) {
            Ptr<Token> t = pt.get();
            int brnest = 0;
            int panest = 0;
            int curlynest = 0;
            for (; ;t = this.peek(t)){
                switch (((t.get()).value & 0xFF))
                {
                    case 3:
                        brnest++;
                        continue;
                    case 4:
                        if (((brnest -= 1) >= 0))
                        {
                            continue;
                        }
                        break;
                    case 1:
                        panest++;
                        continue;
                    case 99:
                        if ((brnest != 0) || (panest != 0))
                        {
                            continue;
                        }
                        break;
                    case 2:
                        if (((panest -= 1) >= 0))
                        {
                            continue;
                        }
                        break;
                    case 5:
                        curlynest++;
                        continue;
                    case 6:
                        if (((curlynest -= 1) >= 0))
                        {
                            continue;
                        }
                        return false;
                    case 31:
                        if (brnest != 0)
                        {
                            continue;
                        }
                        break;
                    case 9:
                        if (curlynest != 0)
                        {
                            continue;
                        }
                        return false;
                    case 11:
                        return false;
                    default:
                    continue;
                }
                break;
            }
            pt.set(0, t);
            return true;
        }

        public  boolean skipParens(Ptr<Token> t, Ptr<Ptr<Token>> pt) {
            if ((((t.get()).value & 0xFF) != 1))
            {
                return false;
            }
            int parens = 0;
            try {
                try {
                L_outer43:
                    for (; 1 != 0;){
                        {
                            int __dispatch98 = 0;
                            dispatched_98:
                            do {
                                switch (__dispatch98 != 0 ? __dispatch98 : ((t.get()).value & 0xFF))
                                {
                                    case 1:
                                        parens++;
                                        break;
                                    case 2:
                                        parens--;
                                        if ((parens < 0))
                                        {
                                            /*goto Lfalse*/throw Dispatch1.INSTANCE;
                                        }
                                        if ((parens == 0))
                                        {
                                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                                        }
                                        break;
                                    case 11:
                                        /*goto Lfalse*/throw Dispatch1.INSTANCE;
                                    default:
                                    break;
                                }
                            } while(__dispatch98 != 0);
                        }
                        t = this.peek(t);
                    }
                }
                catch(Dispatch0 __d){}
            /*Ldone:*/
                if (pt != null)
                {
                    pt.set(0, this.peek(t));
                }
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lfalse:*/
            return false;
        }

        public  boolean skipParensIf(Ptr<Token> t, Ptr<Ptr<Token>> pt) {
            if ((((t.get()).value & 0xFF) != 1))
            {
                if (pt != null)
                {
                    pt.set(0, t);
                }
                return true;
            }
            return this.skipParens(t, pt);
        }

        public  boolean hasOptionalParensThen(Ptr<Token> t, byte expected) {
            Ptr<Token> tk = null;
            if (!this.skipParensIf(t, ptr(tk)))
            {
                return false;
            }
            return ((tk.get()).value & 0xFF) == (expected & 0xFF);
        }

        public  boolean skipAttributes(Ptr<Token> t, Ptr<Ptr<Token>> pt) {
            try {
                try {
                L_outer44:
                    for (; 1 != 0;){
                        {
                            int __dispatch99 = 0;
                            dispatched_99:
                            do {
                                switch (__dispatch99 != 0 ? __dispatch99 : ((t.get()).value & 0xFF))
                                {
                                    case 171:
                                    case 182:
                                    case 224:
                                    case 177:
                                    case 170:
                                    case 179:
                                    case 203:
                                    case 159:
                                    case 172:
                                    case 194:
                                        break;
                                    case 174:
                                        if ((((this.peek(t).get()).value & 0xFF) == 1))
                                        {
                                            t = this.peek(t);
                                            if (!this.skipParens(t, ptr(t)))
                                            {
                                                /*goto Lerror*/throw Dispatch1.INSTANCE;
                                            }
                                            continue L_outer44;
                                        }
                                        break;
                                    case 216:
                                    case 215:
                                    case 210:
                                    case 217:
                                    case 195:
                                        break;
                                    case 225:
                                        t = this.peek(t);
                                        if ((((t.get()).value & 0xFF) == 120))
                                        {
                                            if ((pequals((t.get()).ident, Id.property)) || (pequals((t.get()).ident, Id.nogc)) || (pequals((t.get()).ident, Id.safe)) || (pequals((t.get()).ident, Id.trusted)) || (pequals((t.get()).ident, Id.system)) || (pequals((t.get()).ident, Id.disable)))
                                            {
                                                break;
                                            }
                                            t = this.peek(t);
                                            if ((((t.get()).value & 0xFF) == 91))
                                            {
                                                t = this.peek(t);
                                                if ((((t.get()).value & 0xFF) == 1))
                                                {
                                                    if (!this.skipParens(t, ptr(t)))
                                                    {
                                                        /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                    }
                                                }
                                                else
                                                {
                                                    if ((((t.get()).value & 0xFF) == 229))
                                                    {
                                                        t = this.peek(t);
                                                        if (!this.skipParens(t, ptr(t)))
                                                        {
                                                            /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                        }
                                                    }
                                                    else
                                                    {
                                                        t = this.peek(t);
                                                    }
                                                }
                                            }
                                            if ((((t.get()).value & 0xFF) == 1))
                                            {
                                                if (!this.skipParens(t, ptr(t)))
                                                {
                                                    /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                }
                                                continue L_outer44;
                                            }
                                            continue L_outer44;
                                        }
                                        if ((((t.get()).value & 0xFF) == 1))
                                        {
                                            if (!this.skipParens(t, ptr(t)))
                                            {
                                                /*goto Lerror*/throw Dispatch1.INSTANCE;
                                            }
                                            continue L_outer44;
                                        }
                                        /*goto Lerror*/throw Dispatch1.INSTANCE;
                                    default:
                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                }
                            } while(__dispatch99 != 0);
                        }
                        t = this.peek(t);
                    }
                }
                catch(Dispatch0 __d){}
            /*Ldone:*/
                if (pt != null)
                {
                    pt.set(0, t);
                }
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lerror:*/
            return false;
        }

        public  Expression parseExpression() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseAssignExp();
            for (; ((this.token.value.value & 0xFF) == 99);){
                this.nextToken();
                Expression e2 = this.parseAssignExp();
                e = new CommaExp(loc, e, e2, false);
                loc = this.token.value.loc.copy();
            }
            return e;
        }

        public  Expression parsePrimaryExp() {
            Expression e = null;
            Type t = null;
            Identifier id = null;
            Loc loc = this.token.value.loc.copy();
            {
                int __dispatch100 = 0;
                dispatched_100:
                do {
                    switch (__dispatch100 != 0 ? __dispatch100 : (this.token.value.value & 0xFF))
                    {
                        case 120:
                            Ptr<Token> t1 = this.peek(ptr(this.token));
                            Ptr<Token> t2 = this.peek(t1);
                            if ((((t1.get()).value & 0xFF) == 75) && (((t2.get()).value & 0xFF) == 55))
                            {
                                this.nextToken();
                                this.nextToken();
                                this.nextToken();
                                this.error(new BytePtr("use `.` for member lookup, not `->`"));
                                /*goto Lerr*/{ __dispatch100 = -1; continue dispatched_100; }
                            }
                            if (((this.peekNext() & 0xFF) == 228))
                            {
                                /*goto case_delegate*/{ __dispatch100 = -2; continue dispatched_100; }
                            }
                            id = this.token.value.ident;
                            this.nextToken();
                            byte save = TOK.reserved;
                            if (((this.token.value.value & 0xFF) == 91) && (((save = this.peekNext()) & 0xFF) != 63) && ((save & 0xFF) != 175))
                            {
                                TemplateInstance tempinst = new TemplateInstance(loc, id, this.parseTemplateArguments());
                                e = new ScopeExp(loc, tempinst);
                            }
                            else
                            {
                                e = new IdentifierExp(loc, id);
                            }
                            break;
                        case 35:
                            if (this.inBrackets == 0)
                            {
                                this.error(new BytePtr("`$` is valid only inside [] of index or slice"));
                            }
                            e = new DollarExp(loc);
                            this.nextToken();
                            break;
                        case 97:
                            e = new IdentifierExp(loc, Id.empty.value);
                            break;
                        case 123:
                            e = new ThisExp(loc);
                            this.nextToken();
                            break;
                        case 124:
                            e = new SuperExp(loc);
                            this.nextToken();
                            break;
                        case 105:
                            e = new IntegerExp(loc, (long)(int)this.token.value.intvalue, Type.tint32.value);
                            this.nextToken();
                            break;
                        case 106:
                            e = new IntegerExp(loc, (long)(int)this.token.value.intvalue, Type.tuns32.value);
                            this.nextToken();
                            break;
                        case 107:
                            e = new IntegerExp(loc, (long)this.token.value.intvalue, Type.tint64.value);
                            this.nextToken();
                            break;
                        case 108:
                            e = new IntegerExp(loc, this.token.value.intvalue, Type.tuns64);
                            this.nextToken();
                            break;
                        case 111:
                            e = new RealExp(loc, this.token.value.floatvalue, Type.tfloat32.value);
                            this.nextToken();
                            break;
                        case 112:
                            e = new RealExp(loc, this.token.value.floatvalue, Type.tfloat64.value);
                            this.nextToken();
                            break;
                        case 113:
                            e = new RealExp(loc, this.token.value.floatvalue, Type.tfloat80.value);
                            this.nextToken();
                            break;
                        case 114:
                            e = new RealExp(loc, this.token.value.floatvalue, Type.timaginary32.value);
                            this.nextToken();
                            break;
                        case 115:
                            e = new RealExp(loc, this.token.value.floatvalue, Type.timaginary64.value);
                            this.nextToken();
                            break;
                        case 116:
                            e = new RealExp(loc, this.token.value.floatvalue, Type.timaginary80.value);
                            this.nextToken();
                            break;
                        case 13:
                            e = new NullExp(loc, null);
                            this.nextToken();
                            break;
                        case 219:
                            BytePtr s = pcopy(loc.filename != null ? loc.filename : this.mod.ident.value.toChars());
                            e = new StringExp(loc, s);
                            this.nextToken();
                            break;
                        case 220:
                            assertMsg(loc.isValid(), new ByteSlice("__FILE_FULL_PATH__ does not work with an invalid location"));
                            e = new StringExp(loc, FileName.toAbsolute(loc.filename, null));
                            this.nextToken();
                            break;
                        case 218:
                            e = new IntegerExp(loc, (long)loc.linnum, Type.tint32.value);
                            this.nextToken();
                            break;
                        case 221:
                            BytePtr s_1 = pcopy(this.md != null ? (this.md.get()).toChars() : this.mod.toChars());
                            e = new StringExp(loc, s_1);
                            this.nextToken();
                            break;
                        case 222:
                            e = new FuncInitExp(loc);
                            this.nextToken();
                            break;
                        case 223:
                            e = new PrettyFuncInitExp(loc);
                            this.nextToken();
                            break;
                        case 15:
                            e = new IntegerExp(loc, 1L, Type.tbool.value);
                            this.nextToken();
                            break;
                        case 16:
                            e = new IntegerExp(loc, 0L, Type.tbool.value);
                            this.nextToken();
                            break;
                        case 117:
                            e = new IntegerExp(loc, (long)(byte)this.token.value.intvalue, Type.tchar);
                            this.nextToken();
                            break;
                        case 118:
                            e = new IntegerExp(loc, (long)(int)this.token.value.intvalue, Type.twchar);
                            this.nextToken();
                            break;
                        case 119:
                            e = new IntegerExp(loc, (long)(int)this.token.value.intvalue, Type.tdchar);
                            this.nextToken();
                            break;
                        case 121:
                        case 122:
                            BytePtr s_2 = pcopy(this.token.value.ustring);
                            int len = this.token.value.len;
                            byte postfix = this.token.value.postfix;
                            for (; 1 != 0;){
                                Token prev = this.token.value.copy();
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 121) || ((this.token.value.value & 0xFF) == 122))
                                {
                                    if (this.token.value.postfix != 0)
                                    {
                                        if (((this.token.value.postfix & 0xFF) != (postfix & 0xFF)))
                                        {
                                            this.error(new BytePtr("mismatched string literal postfixes `'%c'` and `'%c'`"), postfix, this.token.value.postfix);
                                        }
                                        postfix = this.token.value.postfix;
                                    }
                                    this.error(new BytePtr("Implicit string concatenation is deprecated, use %s ~ %s instead"), prev.toChars(), this.token.value.toChars());
                                    int len1 = len;
                                    int len2 = this.token.value.len;
                                    len = len1 + len2;
                                    BytePtr s2 = pcopy(((BytePtr)Mem.xmalloc(len)));
                                    memcpy((BytePtr)(s2), (s_2), (len1));
                                    memcpy((BytePtr)((s2.plus(len1))), (this.token.value.ustring), (len2));
                                    s_2 = pcopy(s2);
                                }
                                else
                                {
                                    break;
                                }
                            }
                            e = new StringExp(loc, s_2, len, (byte)postfix);
                            break;
                        case 128:
                            t = Type.tvoid.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 129:
                            t = Type.tint8.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 130:
                            t = Type.tuns8.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 131:
                            t = Type.tint16.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 132:
                            t = Type.tuns16.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 133:
                            t = Type.tint32.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 134:
                            t = Type.tuns32.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 135:
                            t = Type.tint64.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 136:
                            t = Type.tuns64;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 137:
                            t = Type.tint128;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 138:
                            t = Type.tuns128;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 139:
                            t = Type.tfloat32.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 140:
                            t = Type.tfloat64.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 141:
                            t = Type.tfloat80.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 142:
                            t = Type.timaginary32.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 143:
                            t = Type.timaginary64.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 144:
                            t = Type.timaginary80.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 145:
                            t = Type.tcomplex32;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 146:
                            t = Type.tcomplex64;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 147:
                            t = Type.tcomplex80;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 151:
                            t = Type.tbool.value;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 148:
                            t = Type.tchar;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 149:
                            t = Type.twchar;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        case 150:
                            t = Type.tdchar;
                            /*goto LabelX*/{ __dispatch100 = -3; continue dispatched_100; }
                        /*LabelX:*/
                        case -3:
                        __dispatch100 = 0;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                e = new TypeExp(loc, t);
                                e = new CallExp(loc, e, this.parseArguments());
                                break;
                            }
                            this.check(TOK.dot, t.toChars());
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("found `%s` when expecting identifier following `%s`."), this.token.value.toChars(), t.toChars());
                                /*goto Lerr*/{ __dispatch100 = -1; continue dispatched_100; }
                            }
                            e = new DotIdExp(loc, new TypeExp(loc, t), this.token.value.ident);
                            this.nextToken();
                            break;
                        case 39:
                            t = this.parseTypeof();
                            e = new TypeExp(loc, t);
                            break;
                        case 229:
                            t = this.parseVector();
                            e = new TypeExp(loc, t);
                            break;
                        case 42:
                            this.nextToken();
                            this.check(TOK.leftParentheses, new BytePtr("`typeid`"));
                            RootObject o = null;
                            if (this.isDeclaration(ptr(this.token), NeedDeclaratorId.no, TOK.reserved, null))
                            {
                                o = this.parseType(null, null);
                            }
                            else
                            {
                                o = this.parseAssignExp();
                            }
                            this.check(TOK.rightParentheses);
                            e = new TypeidExp(loc, o);
                            break;
                        case 213:
                            Identifier ident = null;
                            Ptr<DArray<RootObject>> args = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if (((this.token.value.value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("`__traits(identifier, args...)` expected"));
                                /*goto Lerr*/{ __dispatch100 = -1; continue dispatched_100; }
                            }
                            ident = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 99))
                            {
                                args = this.parseTemplateArgumentList();
                            }
                            else
                            {
                                this.check(TOK.rightParentheses);
                            }
                            e = new TraitsExp(loc, ident, args);
                            break;
                        case 63:
                            Type targ = null;
                            Identifier ident_1 = null;
                            Type tspec = null;
                            byte tok = TOK.reserved;
                            byte tok2 = TOK.reserved;
                            Ptr<DArray<TemplateParameter>> tpl = null;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 1))
                            {
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) == 120) && ((this.peekNext() & 0xFF) == 1))
                                {
                                    this.error(loc, new BytePtr("unexpected `(` after `%s`, inside `is` expression. Try enclosing the contents of `is` with a `typeof` expression"), this.token.value.toChars());
                                    this.nextToken();
                                    Ptr<Token> tempTok = this.peekPastParen(ptr(this.token));
                                    (ptr(this.token)).set(0, (tempTok));
                                    /*goto Lerr*/{ __dispatch100 = -1; continue dispatched_100; }
                                }
                                targ = this.parseType(ptr(ident_1), null);
                                if (((this.token.value.value & 0xFF) == 7) || ((this.token.value.value & 0xFF) == 58))
                                {
                                    tok = this.token.value.value;
                                    this.nextToken();
                                    if (((tok & 0xFF) == 58) && ((this.token.value.value & 0xFF) == 152) || ((this.token.value.value & 0xFF) == 155) || ((this.token.value.value & 0xFF) == 153) || ((this.token.value.value & 0xFF) == 124) || ((this.token.value.value & 0xFF) == 156) || ((this.token.value.value & 0xFF) == 154) || ((this.token.value.value & 0xFF) == 180) || ((this.token.value.value & 0xFF) == 34) || ((this.token.value.value & 0xFF) == 209) || ((this.token.value.value & 0xFF) == 212) || ((this.token.value.value & 0xFF) == 171) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 182) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 224) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 177) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2) || ((this.token.value.value & 0xFF) == 161) || ((this.token.value.value & 0xFF) == 160) || ((this.token.value.value & 0xFF) == 195) || ((this.token.value.value & 0xFF) == 229) && (((this.peek(ptr(this.token)).get()).value & 0xFF) == 2))
                                    {
                                        tok2 = this.token.value.value;
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        tspec = this.parseType(null, null);
                                    }
                                }
                                if (tspec != null)
                                {
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        tpl = this.parseTemplateParameterList(1);
                                    }
                                    else
                                    {
                                        tpl = refPtr(new DArray<TemplateParameter>());
                                        this.check(TOK.rightParentheses);
                                    }
                                }
                                else
                                {
                                    this.check(TOK.rightParentheses);
                                }
                            }
                            else
                            {
                                this.error(new BytePtr("`type identifier : specialization` expected following `is`"));
                                /*goto Lerr*/{ __dispatch100 = -1; continue dispatched_100; }
                            }
                            e = new IsExp(loc, targ, ident_1, tok, tspec, tok2, tpl);
                            break;
                        case 14:
                            Expression msg = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses, new BytePtr("`assert`"));
                            e = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 99))
                            {
                                this.nextToken();
                                if (((this.token.value.value & 0xFF) != 2))
                                {
                                    msg = this.parseAssignExp();
                                    if (((this.token.value.value & 0xFF) == 99))
                                    {
                                        this.nextToken();
                                    }
                                }
                            }
                            this.check(TOK.rightParentheses);
                            e = new AssertExp(loc, e, msg);
                            break;
                        case 162:
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) != 1))
                            {
                                this.error(new BytePtr("found `%s` when expecting `%s` following %s"), this.token.value.toChars(), Token.toChars(TOK.leftParentheses), new BytePtr("`mixin`"));
                            }
                            Ptr<DArray<Expression>> exps = this.parseArguments();
                            e = new CompileExp(loc, exps);
                            break;
                        case 157:
                            this.nextToken();
                            this.check(TOK.leftParentheses, new BytePtr("`import`"));
                            e = this.parseAssignExp();
                            this.check(TOK.rightParentheses);
                            e = new ImportExp(loc, e);
                            break;
                        case 22:
                            e = this.parseNewExp(null);
                            break;
                        case 210:
                            if (((this.peekNext() & 0xFF) == 1))
                            {
                                Ptr<Token> tk = this.peekPastParen(this.peek(ptr(this.token)));
                                if (this.skipAttributes(tk, ptr(tk)) && (((tk.get()).value & 0xFF) == 228) || (((tk.get()).value & 0xFF) == 5))
                                {
                                    /*goto case_delegate*/{ __dispatch100 = -2; continue dispatched_100; }
                                }
                            }
                            this.nextToken();
                            this.error(new BytePtr("found `%s` when expecting function literal following `ref`"), this.token.value.toChars());
                            /*goto Lerr*/{ __dispatch100 = -1; continue dispatched_100; }
                        case 1:
                            Ptr<Token> tk_1 = this.peekPastParen(ptr(this.token));
                            if (this.skipAttributes(tk_1, ptr(tk_1)) && (((tk_1.get()).value & 0xFF) == 228) || (((tk_1.get()).value & 0xFF) == 5))
                            {
                                /*goto case_delegate*/{ __dispatch100 = -2; continue dispatched_100; }
                            }
                            this.nextToken();
                            e = this.parseExpression();
                            e.parens = (byte)1;
                            this.check(loc, TOK.rightParentheses);
                            break;
                        case 3:
                            Ptr<DArray<Expression>> values = refPtr(new DArray<Expression>());
                            Ptr<DArray<Expression>> keys = null;
                            this.nextToken();
                            for (; ((this.token.value.value & 0xFF) != 4) && ((this.token.value.value & 0xFF) != 11);){
                                e = this.parseAssignExp();
                                if (((this.token.value.value & 0xFF) == 7) && (keys != null) || ((values.get()).length.value == 0))
                                {
                                    this.nextToken();
                                    if (keys == null)
                                    {
                                        keys = refPtr(new DArray<Expression>());
                                    }
                                    (keys.get()).push(e);
                                    e = this.parseAssignExp();
                                }
                                else if (keys != null)
                                {
                                    this.error(new BytePtr("`key:value` expected for associative array literal"));
                                    keys = null;
                                }
                                (values.get()).push(e);
                                if (((this.token.value.value & 0xFF) == 4))
                                {
                                    break;
                                }
                                this.check(TOK.comma);
                            }
                            this.check(loc, TOK.rightBracket);
                            if (keys != null)
                            {
                                e = new AssocArrayLiteralExp(loc, keys, values);
                            }
                            else
                            {
                                e = new ArrayLiteralExp(loc, null, values);
                            }
                            break;
                        case 5:
                        case 161:
                        case 160:
                        /*case_delegate:*/
                        case -2:
                        __dispatch100 = 0;
                            {
                                Dsymbol s_3 = this.parseFunctionLiteral();
                                e = new FuncExp(loc, s_3);
                                break;
                            }
                        default:
                        this.error(new BytePtr("expression expected, not `%s`"), this.token.value.toChars());
                    /*Lerr:*/
                    case -1:
                    __dispatch100 = 0;
                        e = new IntegerExp(loc, 0L, Type.tint32.value);
                        this.nextToken();
                        break;
                    }
                } while(__dispatch100 != 0);
            }
            return e;
        }

        public  Expression parseUnaryExp() {
            Expression e = null;
            Loc loc = this.token.value.loc.copy();
            switch ((this.token.value.value & 0xFF))
            {
                case 84:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new AddrExp(loc, e);
                    break;
                case 93:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new PreExp(TOK.prePlusPlus, loc, e);
                    break;
                case 94:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new PreExp(TOK.preMinusMinus, loc, e);
                    break;
                case 78:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new PtrExp(loc, e);
                    break;
                case 75:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new NegExp(loc, e);
                    break;
                case 74:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new UAddExp(loc, e);
                    break;
                case 91:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new NotExp(loc, e);
                    break;
                case 92:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new ComExp(loc, e);
                    break;
                case 23:
                    this.nextToken();
                    e = this.parseUnaryExp();
                    e = new DeleteExp(loc, e, false);
                    break;
                case 12:
                    this.nextToken();
                    this.check(TOK.leftParentheses);
                    byte m = (byte)0;
                    for (; 1 != 0;){
                        switch ((this.token.value.value & 0xFF))
                        {
                            case 171:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= MODFlags.const_;
                                this.nextToken();
                                continue;
                            case 182:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= MODFlags.immutable_;
                                this.nextToken();
                                continue;
                            case 224:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= MODFlags.shared_;
                                this.nextToken();
                                continue;
                            case 177:
                                if (((this.peekNext() & 0xFF) == 1))
                                {
                                    break;
                                }
                                m |= MODFlags.wild;
                                this.nextToken();
                                continue;
                            default:
                            break;
                        }
                        break;
                    }
                    if (((this.token.value.value & 0xFF) == 2))
                    {
                        this.nextToken();
                        e = this.parseUnaryExp();
                        e = new CastExp(loc, e, m);
                    }
                    else
                    {
                        Type t = this.parseType(null, null);
                        t = t.addMod(m);
                        this.check(TOK.rightParentheses);
                        e = this.parseUnaryExp();
                        e = new CastExp(loc, e, t);
                    }
                    break;
                case 177:
                case 224:
                case 171:
                case 182:
                    long stc = this.parseTypeCtor();
                    Type t_1 = this.parseBasicType(false);
                    t_1 = t_1.addSTC(stc);
                    if ((stc == 0L) && ((this.token.value.value & 0xFF) == 97))
                    {
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) != 120))
                        {
                            this.error(new BytePtr("identifier expected following `(type)`."));
                            return null;
                        }
                        e = new DotIdExp(loc, new TypeExp(loc, t_1), this.token.value.ident);
                        this.nextToken();
                        e = this.parsePostExp(e);
                    }
                    else
                    {
                        e = new TypeExp(loc, t_1);
                        if (((this.token.value.value & 0xFF) != 1))
                        {
                            this.error(new BytePtr("`(arguments)` expected following `%s`"), t_1.toChars());
                            return e;
                        }
                        e = new CallExp(loc, e, this.parseArguments());
                    }
                    break;
                case 1:
                    Ptr<Token> tk = this.peek(ptr(this.token));
                    if (this.isDeclaration(tk, NeedDeclaratorId.no, TOK.rightParentheses, ptr(tk)))
                    {
                        tk = this.peek(tk);
                        switch (((tk.get()).value & 0xFF))
                        {
                            case 91:
                                tk = this.peek(tk);
                                if ((((tk.get()).value & 0xFF) == 63) || (((tk.get()).value & 0xFF) == 175))
                                {
                                    break;
                                }
                            case 97:
                            case 93:
                            case 94:
                            case 23:
                            case 22:
                            case 1:
                            case 120:
                            case 123:
                            case 124:
                            case 105:
                            case 106:
                            case 107:
                            case 108:
                            case 109:
                            case 110:
                            case 111:
                            case 112:
                            case 113:
                            case 114:
                            case 115:
                            case 116:
                            case 13:
                            case 15:
                            case 16:
                            case 117:
                            case 118:
                            case 119:
                            case 121:
                            case 161:
                            case 160:
                            case 39:
                            case 213:
                            case 229:
                            case 219:
                            case 220:
                            case 218:
                            case 221:
                            case 222:
                            case 223:
                            case 149:
                            case 150:
                            case 151:
                            case 148:
                            case 129:
                            case 130:
                            case 131:
                            case 132:
                            case 133:
                            case 134:
                            case 135:
                            case 136:
                            case 137:
                            case 138:
                            case 139:
                            case 140:
                            case 141:
                            case 142:
                            case 143:
                            case 144:
                            case 145:
                            case 146:
                            case 147:
                            case 128:
                                this.nextToken();
                                Type t_2 = this.parseType(null, null);
                                this.check(TOK.rightParentheses);
                                if (((this.token.value.value & 0xFF) == 97))
                                {
                                    if (((this.peekNext() & 0xFF) != 120) && ((this.peekNext() & 0xFF) != 22))
                                    {
                                        this.error(new BytePtr("identifier or new keyword expected following `(...)`."));
                                        return null;
                                    }
                                    e = new TypeExp(loc, t_2);
                                    e = this.parsePostExp(e);
                                }
                                else
                                {
                                    e = this.parseUnaryExp();
                                    e = new CastExp(loc, e, t_2);
                                    this.error(new BytePtr("C style cast illegal, use `%s`"), e.toChars());
                                }
                                return e;
                            default:
                            break;
                        }
                    }
                    e = this.parsePrimaryExp();
                    e = this.parsePostExp(e);
                    break;
                default:
                e = this.parsePrimaryExp();
                e = this.parsePostExp(e);
                break;
            }
            assert(e != null);
            for (; ((this.token.value.value & 0xFF) == 226);){
                this.nextToken();
                Expression e2 = this.parseUnaryExp();
                e = new PowExp(loc, e, e2);
            }
            return e;
        }

        public  Expression parsePostExp(Expression e) {
            for (; 1 != 0;){
                Loc loc = this.token.value.loc.copy();
                switch ((this.token.value.value & 0xFF))
                {
                    case 97:
                        this.nextToken();
                        if (((this.token.value.value & 0xFF) == 120))
                        {
                            Identifier id = this.token.value.ident;
                            this.nextToken();
                            if (((this.token.value.value & 0xFF) == 91) && ((this.peekNext() & 0xFF) != 63) && ((this.peekNext() & 0xFF) != 175))
                            {
                                Ptr<DArray<RootObject>> tiargs = this.parseTemplateArguments();
                                e = new DotTemplateInstanceExp(loc, e, id, tiargs);
                            }
                            else
                            {
                                e = new DotIdExp(loc, e, id);
                            }
                            continue;
                        }
                        if (((this.token.value.value & 0xFF) == 22))
                        {
                            e = this.parseNewExp(e);
                            continue;
                        }
                        this.error(new BytePtr("identifier or `new` expected following `.`, not `%s`"), this.token.value.toChars());
                        break;
                    case 93:
                        e = new PostExp(TOK.plusPlus, loc, e);
                        break;
                    case 94:
                        e = new PostExp(TOK.minusMinus, loc, e);
                        break;
                    case 1:
                        e = new CallExp(loc, e, this.parseArguments());
                        continue;
                    case 3:
                        Expression index = null;
                        Expression upr = null;
                        Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>());
                        this.inBrackets++;
                        this.nextToken();
                        for (; ((this.token.value.value & 0xFF) != 4) && ((this.token.value.value & 0xFF) != 11);){
                            index = this.parseAssignExp();
                            if (((this.token.value.value & 0xFF) == 31))
                            {
                                this.nextToken();
                                upr = this.parseAssignExp();
                                (arguments.get()).push(new IntervalExp(loc, index, upr));
                            }
                            else
                            {
                                (arguments.get()).push(index);
                            }
                            if (((this.token.value.value & 0xFF) == 4))
                            {
                                break;
                            }
                            this.check(TOK.comma);
                        }
                        this.check(TOK.rightBracket);
                        this.inBrackets--;
                        e = new ArrayExp(loc, e, arguments);
                        continue;
                    default:
                    return e;
                }
                this.nextToken();
            }
        }

        public  Expression parseMulExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseUnaryExp();
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 78:
                        this.nextToken();
                        Expression e2 = this.parseUnaryExp();
                        e = new MulExp(loc, e, e2);
                        continue;
                    case 79:
                        this.nextToken();
                        Expression e2_1 = this.parseUnaryExp();
                        e = new DivExp(loc, e, e2_1);
                        continue;
                    case 80:
                        this.nextToken();
                        Expression e2_2 = this.parseUnaryExp();
                        e = new ModExp(loc, e, e2_2);
                        continue;
                    default:
                    break;
                }
                break;
            }
            return e;
        }

        public  Expression parseAddExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseMulExp();
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 74:
                        this.nextToken();
                        Expression e2 = this.parseMulExp();
                        e = new AddExp(loc, e, e2);
                        continue;
                    case 75:
                        this.nextToken();
                        Expression e2_1 = this.parseMulExp();
                        e = new MinExp(loc, e, e2_1);
                        continue;
                    case 92:
                        this.nextToken();
                        Expression e2_2 = this.parseMulExp();
                        e = new CatExp(loc, e, e2_2);
                        continue;
                    default:
                    break;
                }
                break;
            }
            return e;
        }

        public  Expression parseShiftExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseAddExp();
            for (; 1 != 0;){
                switch ((this.token.value.value & 0xFF))
                {
                    case 64:
                        this.nextToken();
                        Expression e2 = this.parseAddExp();
                        e = new ShlExp(loc, e, e2);
                        continue;
                    case 65:
                        this.nextToken();
                        Expression e2_1 = this.parseAddExp();
                        e = new ShrExp(loc, e, e2_1);
                        continue;
                    case 68:
                        this.nextToken();
                        Expression e2_2 = this.parseAddExp();
                        e = new UshrExp(loc, e, e2_2);
                        continue;
                    default:
                    break;
                }
                break;
            }
            return e;
        }

        public  Expression parseCmpExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseShiftExp();
            byte op = this.token.value.value;
            {
                int __dispatch108 = 0;
                dispatched_108:
                do {
                    switch (__dispatch108 != 0 ? __dispatch108 : (op & 0xFF))
                    {
                        case 58:
                        case 59:
                            this.nextToken();
                            Expression e2 = this.parseShiftExp();
                            e = new EqualExp(op, loc, e, e2);
                            break;
                        case 63:
                            op = TOK.identity;
                            /*goto L1*/{ __dispatch108 = -1; continue dispatched_108; }
                        case 91:
                            {
                                Ptr<Token> t = this.peek(ptr(this.token));
                                if ((((t.get()).value & 0xFF) == 175))
                                {
                                    this.nextToken();
                                    this.nextToken();
                                    Expression e2_3 = this.parseShiftExp();
                                    e = new InExp(loc, e, e2_3);
                                    e = new NotExp(loc, e);
                                    break;
                                }
                                if ((((t.get()).value & 0xFF) != 63))
                                {
                                    break;
                                }
                                this.nextToken();
                                op = TOK.notIdentity;
                                /*goto L1*/{ __dispatch108 = -1; continue dispatched_108; }
                            }
                        /*L1:*/
                        case -1:
                        __dispatch108 = 0;
                            this.nextToken();
                            Expression e2_1 = this.parseShiftExp();
                            e = new IdentityExp(op, loc, e, e2_1);
                            break;
                        case 54:
                        case 56:
                        case 55:
                        case 57:
                            this.nextToken();
                            Expression e2_2 = this.parseShiftExp();
                            e = new CmpExp(op, loc, e, e2_2);
                            break;
                        case 175:
                            this.nextToken();
                            Expression e2_4 = this.parseShiftExp();
                            e = new InExp(loc, e, e2_4);
                            break;
                        default:
                        break;
                    }
                } while(__dispatch108 != 0);
            }
            return e;
        }

        public  Expression parseAndExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseCmpExp();
            for (; ((this.token.value.value & 0xFF) == 84);){
                this.checkParens(TOK.and, e);
                this.nextToken();
                Expression e2 = this.parseCmpExp();
                this.checkParens(TOK.and, e2);
                e = new AndExp(loc, e, e2);
                loc = this.token.value.loc.copy();
            }
            return e;
        }

        public  Expression parseXorExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseAndExp();
            for (; ((this.token.value.value & 0xFF) == 86);){
                this.checkParens(TOK.xor, e);
                this.nextToken();
                Expression e2 = this.parseAndExp();
                this.checkParens(TOK.xor, e2);
                e = new XorExp(loc, e, e2);
            }
            return e;
        }

        public  Expression parseOrExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseXorExp();
            for (; ((this.token.value.value & 0xFF) == 85);){
                this.checkParens(TOK.or, e);
                this.nextToken();
                Expression e2 = this.parseXorExp();
                this.checkParens(TOK.or, e2);
                e = new OrExp(loc, e, e2);
            }
            return e;
        }

        public  Expression parseAndAndExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseOrExp();
            for (; ((this.token.value.value & 0xFF) == 101);){
                this.nextToken();
                Expression e2 = this.parseOrExp();
                e = new LogicalExp(loc, TOK.andAnd, e, e2);
            }
            return e;
        }

        public  Expression parseOrOrExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseAndAndExp();
            for (; ((this.token.value.value & 0xFF) == 102);){
                this.nextToken();
                Expression e2 = this.parseAndAndExp();
                e = new LogicalExp(loc, TOK.orOr, e, e2);
            }
            return e;
        }

        public  Expression parseCondExp() {
            Loc loc = this.token.value.loc.copy();
            Expression e = this.parseOrOrExp();
            if (((this.token.value.value & 0xFF) == 100))
            {
                this.nextToken();
                Expression e1 = this.parseExpression();
                this.check(TOK.colon);
                Expression e2 = this.parseCondExp();
                e = new CondExp(loc, e, e1, e2);
            }
            return e;
        }

        public  Expression parseAssignExp() {
            Expression e = null;
            e = this.parseCondExp();
            if ((e == null))
            {
                return e;
            }
            if (((e.op.value & 0xFF) == 100) && (e.parens == 0) && (precedence.get((this.token.value.value & 0xFF)) == PREC.assign))
            {
                deprecation(e.loc.value, new BytePtr("`%s` must be surrounded by parentheses when next to operator `%s`"), e.toChars(), Token.toChars(this.token.value.value));
            }
            Loc loc = this.token.value.loc.copy();
            switch ((this.token.value.value & 0xFF))
            {
                case 90:
                    this.nextToken();
                    Expression e2 = this.parseAssignExp();
                    e = new AssignExp(loc, e, e2);
                    break;
                case 76:
                    this.nextToken();
                    Expression e2_1 = this.parseAssignExp();
                    e = new AddAssignExp(loc, e, e2_1);
                    break;
                case 77:
                    this.nextToken();
                    Expression e2_2 = this.parseAssignExp();
                    e = new MinAssignExp(loc, e, e2_2);
                    break;
                case 81:
                    this.nextToken();
                    Expression e2_3 = this.parseAssignExp();
                    e = new MulAssignExp(loc, e, e2_3);
                    break;
                case 82:
                    this.nextToken();
                    Expression e2_4 = this.parseAssignExp();
                    e = new DivAssignExp(loc, e, e2_4);
                    break;
                case 83:
                    this.nextToken();
                    Expression e2_5 = this.parseAssignExp();
                    e = new ModAssignExp(loc, e, e2_5);
                    break;
                case 227:
                    this.nextToken();
                    Expression e2_6 = this.parseAssignExp();
                    e = new PowAssignExp(loc, e, e2_6);
                    break;
                case 87:
                    this.nextToken();
                    Expression e2_7 = this.parseAssignExp();
                    e = new AndAssignExp(loc, e, e2_7);
                    break;
                case 88:
                    this.nextToken();
                    Expression e2_8 = this.parseAssignExp();
                    e = new OrAssignExp(loc, e, e2_8);
                    break;
                case 89:
                    this.nextToken();
                    Expression e2_9 = this.parseAssignExp();
                    e = new XorAssignExp(loc, e, e2_9);
                    break;
                case 66:
                    this.nextToken();
                    Expression e2_10 = this.parseAssignExp();
                    e = new ShlAssignExp(loc, e, e2_10);
                    break;
                case 67:
                    this.nextToken();
                    Expression e2_11 = this.parseAssignExp();
                    e = new ShrAssignExp(loc, e, e2_11);
                    break;
                case 69:
                    this.nextToken();
                    Expression e2_12 = this.parseAssignExp();
                    e = new UshrAssignExp(loc, e, e2_12);
                    break;
                case 71:
                    this.nextToken();
                    Expression e2_13 = this.parseAssignExp();
                    e = new CatAssignExp(loc, e, e2_13);
                    break;
                default:
                break;
            }
            return e;
        }

        public  Ptr<DArray<Expression>> parseArguments() {
            Ptr<DArray<Expression>> arguments = null;
            byte endtok = TOK.reserved;
            arguments = refPtr(new DArray<Expression>());
            endtok = ((this.token.value.value & 0xFF) == 3) ? TOK.rightBracket : TOK.rightParentheses;
            this.nextToken();
            for (; ((this.token.value.value & 0xFF) != (endtok & 0xFF)) && ((this.token.value.value & 0xFF) != 11);){
                Expression arg = this.parseAssignExp();
                (arguments.get()).push(arg);
                if (((this.token.value.value & 0xFF) == (endtok & 0xFF)))
                {
                    break;
                }
                this.check(TOK.comma);
            }
            this.check(endtok);
            return arguments;
        }

        public  Expression parseNewExp(Expression thisexp) {
            Loc loc = this.token.value.loc.copy();
            this.nextToken();
            Ptr<DArray<Expression>> newargs = null;
            Ptr<DArray<Expression>> arguments = null;
            if (((this.token.value.value & 0xFF) == 1))
            {
                newargs = this.parseArguments();
            }
            if (((this.token.value.value & 0xFF) == 153))
            {
                this.nextToken();
                if (((this.token.value.value & 0xFF) == 1))
                {
                    arguments = this.parseArguments();
                }
                Ptr<DArray<Ptr<BaseClass>>> baseclasses = null;
                if (((this.token.value.value & 0xFF) != 5))
                {
                    baseclasses = this.parseBaseClasses();
                }
                Identifier id = null;
                Ptr<DArray<Dsymbol>> members = null;
                if (((this.token.value.value & 0xFF) != 5))
                {
                    this.error(new BytePtr("`{ members }` expected for anonymous class"));
                }
                else
                {
                    this.nextToken();
                    members = this.parseDeclDefs(0, null, null);
                    if (((this.token.value.value & 0xFF) != 6))
                    {
                        this.error(new BytePtr("class member expected"));
                    }
                    this.nextToken();
                }
                ClassDeclaration cd = new ClassDeclaration(loc, id, baseclasses, members, false);
                NewAnonClassExp e = new NewAnonClassExp(loc, thisexp, newargs, cd, arguments);
                return e;
            }
            long stc = this.parseTypeCtor();
            Type t = this.parseBasicType(true);
            t = this.parseBasicType2(t);
            t = t.addSTC(stc);
            if (((t.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                TypeAArray taa = (TypeAArray)t;
                Type index = taa.index.value;
                Expression edim = typeToExpression(index);
                if (edim == null)
                {
                    this.error(new BytePtr("need size of rightmost array, not type `%s`"), index.toChars());
                    return new NullExp(loc, null);
                }
                t = new TypeSArray(taa.next.value, edim);
            }
            else if (((this.token.value.value & 0xFF) == 1) && ((t.ty.value & 0xFF) != ENUMTY.Tsarray))
            {
                arguments = this.parseArguments();
            }
            NewExp e = new NewExp(loc, thisexp, newargs, t, arguments);
            return e;
        }

        public  void addComment(Dsymbol s, BytePtr blockComment) {
            if ((s != null))
            {
                s.addComment(Lexer.combineComments(blockComment, this.token.value.lineComment.value, true));
                this.token.value.lineComment.value = null;
            }
        }


        public ParserASTCodegen() {}

        public ParserASTCodegen copy() {
            ParserASTCodegen that = new ParserASTCodegen();
            that.md = this.md;
            that.mod = this.mod;
            that.linkage = this.linkage;
            that.cppmangle = this.cppmangle;
            that.endloc = this.endloc;
            that.inBrackets = this.inBrackets;
            that.lookingForElse = this.lookingForElse;
            that.scanloc = this.scanloc;
            that.prevloc = this.prevloc;
            that.p = this.p;
            that.token = this.token;
            that.base = this.base;
            that.end = this.end;
            that.line = this.line;
            that.doDocComment = this.doDocComment;
            that.anyToken = this.anyToken;
            that.commentToken = this.commentToken;
            that.inTokenStringConstant = this.inTokenStringConstant;
            that.lastDocLine = this.lastDocLine;
            that.diagnosticReporter = this.diagnosticReporter;
            that.tokenFreelist = this.tokenFreelist;
            return that;
        }
    }


    public static class PREC 
    {
        public static final int zero = 0;
        public static final int expr = 1;
        public static final int assign = 2;
        public static final int cond = 3;
        public static final int oror = 4;
        public static final int andand = 5;
        public static final int or = 6;
        public static final int xor = 7;
        public static final int and = 8;
        public static final int equal = 9;
        public static final int rel = 10;
        public static final int shift = 11;
        public static final int add = 12;
        public static final int mul = 13;
        public static final int pow = 14;
        public static final int unary = 15;
        public static final int primary = 16;
    }

}
