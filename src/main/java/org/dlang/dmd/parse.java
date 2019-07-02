package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.lexer.*;
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
        public long storageClass;
        public ASTBase.Expression depmsg;
        public int link;
        public ASTBase.Prot protection = new ASTBase.Prot();
        public boolean setAlignment;
        public ASTBase.Expression ealign;
        public DArray<ASTBase.Expression> udas;
        public BytePtr comment;
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
        public PrefixAttributesASTBase(long storageClass, ASTBase.Expression depmsg, int link, ASTBase.Prot protection, boolean setAlignment, ASTBase.Expression ealign, DArray<ASTBase.Expression> udas, BytePtr comment) {
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

    // from template getStorageClass!(ASTBase)
    public static long getStorageClassASTBase(PrefixAttributesASTBase pAttrs) {
        long stc = 0L;
        if (pAttrs != null)
        {
            stc = (pAttrs).storageClass;
            (pAttrs).storageClass = 0L;
        }
        return stc;
    }


    public static boolean writeMixin(ByteSlice s, Loc loc) {
        if (global.params.mixinOut == null)
            return false;
        OutBuffer ob = global.params.mixinOut;
        (ob).writestring( new ByteSlice("// expansion at "));
        (ob).writestring(loc.toChars(global.params.showColumns));
        (ob).writenl();
        global.params.mixinLines++;
        loc = new Loc(global.params.mixinFile, (global.params.mixinLines + 1), loc.charnum).copy();
        int lastpos = 0;
        {
            int i = 0;
            for (; i < s.getLength();i += 1){
                byte c = s.get(i);
                if (((c & 0xFF) == 10 || (((c & 0xFF) == 13 && i + 1 < s.getLength()) && (s.get(i + 1) & 0xFF) == 10)))
                {
                    (ob).writestring(s.slice(lastpos,i));
                    (ob).writenl();
                    global.params.mixinLines++;
                    if ((c & 0xFF) == 13)
                        i += 1;
                    lastpos = i + 1;
                }
            }
        }
        if (lastpos < s.getLength())
            (ob).writestring(s.slice(lastpos,s.getLength()));
        if ((s.getLength() == 0 || (s.get(s.getLength() - 1) & 0xFF) != 10))
        {
            (ob).writenl();
            global.params.mixinLines++;
        }
        (ob).writenl();
        global.params.mixinLines++;
        return true;
    }

    // from template Parser!(ASTBase)
    public static class ParserASTBase extends Lexer
    {
        public ASTBase.ModuleDeclaration md;
        public ASTBase.Module mod;
        public int linkage;
        public int cppmangle;
        public Loc endloc = new Loc();
        public int inBrackets;
        public Loc lookingForElse = new Loc();
        public  ParserASTBase(Loc loc, ASTBase.Module _module, ByteSlice input, boolean doDocComment, DiagnosticReporter diagnosticReporter) {
            super(_module != null ? _module.srcfile.toChars() : null, toBytePtr(input), 0, input.getLength(), doDocComment, false, diagnosticReporter);
            this.scanloc = loc.copy();
            if ((!(writeMixin(input, this.scanloc)) && loc.filename != null))
            {
                BytePtr filename = pcopy(toBytePtr(Mem.xmalloc(strlen(loc.filename) + 7 + 12 + 1)));
                sprintf(filename,  new ByteSlice("%s-mixin-%d"), loc.filename, loc.linnum);
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

        public  DArray<ASTBase.Dsymbol> parseModule() {
            BytePtr comment = pcopy(this.token.blockComment);
            boolean isdeprecated = false;
            ASTBase.Expression msg = null;
            DArray<ASTBase.Expression> udas = null;
            DArray<ASTBase.Dsymbol> decldefs = null;
            Ref<ASTBase.Dsymbol> lastDecl = ref(this.mod);
            Ref<Token> tk = ref(null);
            if ((this.skipAttributes(this.token, ptr(tk)) && ((tk.value).value & 0xFF) == 34))
            {
                for (; (this.token.value & 0xFF) != 34;){
                    switch ((this.token.value & 0xFF))
                    {
                        case 174:
                            if (isdeprecated)
                                this.error(new BytePtr("there is only one deprecation attribute allowed for module declaration"));
                            isdeprecated = true;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 1)
                            {
                                this.check(TOK.leftParentheses);
                                msg = this.parseAssignExp();
                                this.check(TOK.rightParentheses);
                            }
                            break;
                        case 225:
                            Ref<DArray<ASTBase.Expression>> exps = ref(null);
                            long stc = this.parseAttribute(ptr(exps));
                            if ((((((stc == 4294967296L || stc == 4398046511104L) || stc == 137438953472L) || stc == 8589934592L) || stc == 17179869184L) || stc == 34359738368L))
                            {
                                this.error(new BytePtr("`@%s` attribute for module declaration is not supported"), this.token.toChars());
                            }
                            else
                            {
                                udas = ASTBase.UserAttributeDeclaration.concat(udas, exps.value);
                            }
                            if ((stc) != 0)
                                this.nextToken();
                            break;
                        default:
                        this.error(new BytePtr("`module` expected instead of `%s`"), this.token.toChars());
                        this.nextToken();
                        break;
                    }
                }
            }
            if (udas != null)
            {
                DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                ASTBase.UserAttributeDeclaration udad = new ASTBase.UserAttributeDeclaration(udas, a);
                this.mod.userAttribDecl = udad;
            }
            try {
                if ((this.token.value & 0xFF) == 34)
                {
                    Loc loc = this.token.loc.copy();
                    this.nextToken();
                    if ((this.token.value & 0xFF) != 120)
                    {
                        this.error(new BytePtr("identifier expected following `module`"));
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                    DArray<Identifier> a = null;
                    Identifier id = this.token.ident;
                L_outer1:
                    for (; (this.nextToken() & 0xFF) == 97;){
                        if (a == null)
                            a = new DArray<Identifier>();
                        (a).push(id);
                        this.nextToken();
                        if ((this.token.value & 0xFF) != 120)
                        {
                            this.error(new BytePtr("identifier expected following `package`"));
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                        id = this.token.ident;
                    }
                    this.md = new ASTBase.ModuleDeclaration(loc, a, id, msg, isdeprecated);
                    if ((this.token.value & 0xFF) != 9)
                        this.error(new BytePtr("`;` expected following module declaration instead of `%s`"), this.token.toChars());
                    this.nextToken();
                    this.addComment(this.mod, comment);
                }
                decldefs = this.parseDeclDefs(0, ptr(lastDecl), null);
                if ((this.token.value & 0xFF) != 11)
                {
                    this.error(this.token.loc, new BytePtr("unrecognized declaration"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                return decldefs;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            for (; ((this.token.value & 0xFF) != 9 && (this.token.value & 0xFF) != 11);) {
                this.nextToken();
            }
            this.nextToken();
            return new DArray<ASTBase.Dsymbol>();
        }

        public  long parseDeprecatedAttribute(Ref<ASTBase.Expression> msg) {
            if (((this.peek(this.token)).value & 0xFF) != 1)
                return 1024L;
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

        public  DArray<ASTBase.Dsymbol> parseDeclDefs(int once, Ptr<ASTBase.Dsymbol> pLastDecl, PrefixAttributesASTBase pAttrs) {
            Ref<ASTBase.Dsymbol> lastDecl = ref(null);
            if (pLastDecl == null)
                pLastDecl = pcopy(ptr(lastDecl));
            int linksave = this.linkage;
            DArray<ASTBase.Dsymbol> decldefs = new DArray<ASTBase.Dsymbol>();
        L_outer2:
            do {
                {
                    ASTBase.Dsymbol s = null;
                    DArray<ASTBase.Dsymbol> a = null;
                    PrefixAttributesASTBase attrs = new PrefixAttributesASTBase();
                    if ((!((once) != 0) || pAttrs == null))
                    {
                        pAttrs = attrs;
                        (pAttrs).comment = pcopy(this.token.blockComment);
                    }
                    int prot = ASTBase.Prot.Kind.undefined;
                    long stc = 0L;
                    ASTBase.Condition condition = null;
                    this.linkage = linksave;
                    {
                        int __dispatch1 = 0;
                        dispatched_1:
                        do {
                            switch (__dispatch1 != 0 ? __dispatch1 : (this.token.value & 0xFF))
                            {
                                case 156:
                                    Token t = this.peek(this.token);
                                    if ((((t).value & 0xFF) == 5 || ((t).value & 0xFF) == 7))
                                        s = this.parseEnum();
                                    else if (((t).value & 0xFF) != 120)
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    else
                                    {
                                        t = this.peek(t);
                                        if (((((t).value & 0xFF) == 5 || ((t).value & 0xFF) == 7) || ((t).value & 0xFF) == 9))
                                            s = this.parseEnum();
                                        else
                                            /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                    break;
                                case 157:
                                    a = this.parseImport();
                                    break;
                                case 36:
                                    s = this.parseTemplateDeclaration(false);
                                    break;
                                case 162:
                                    Loc loc = this.token.loc.copy();
                                    switch ((this.peekNext() & 0xFF))
                                    {
                                        case 1:
                                            this.nextToken();
                                            DArray<ASTBase.Expression> exps = this.parseArguments();
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
                                    a = this.parseDeclarations(false, pAttrs, (pAttrs).comment);
                                    if ((a != null && ((a).length) != 0))
                                        pLastDecl.set(0, (a).get((a).length - 1));
                                    break;
                                case 123:
                                    if ((this.peekNext() & 0xFF) == 97)
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    s = this.parseCtor(pAttrs);
                                    break;
                                case 92:
                                    s = this.parseDtor(pAttrs);
                                    break;
                                case 207:
                                    Token t_1 = this.peek(this.token);
                                    if ((((t_1).value & 0xFF) == 1 || ((t_1).value & 0xFF) == 5))
                                    {
                                        s = this.parseInvariant(pAttrs);
                                        break;
                                    }
                                    this.error(new BytePtr("invariant body expected, not `%s`"), this.token.toChars());
                                    /*goto Lerror*/{ __dispatch1 = -2; continue dispatched_1; }
                                case 208:
                                    if (((global.params.useUnitTests || global.params.doDocComments) || global.params.doHdrGeneration))
                                    {
                                        s = this.parseUnitTest(pAttrs);
                                        if (pLastDecl.get() != null)
                                            (pLastDecl.get()).ddocUnittest = (ASTBase.UnitTestDeclaration)s;
                                    }
                                    else
                                    {
                                        Loc loc_1 = this.token.loc.copy();
                                        int braces = 0;
                                    L_outer3:
                                        for (; (1) != 0;){
                                            this.nextToken();
                                            {
                                                int __dispatch3 = 0;
                                                dispatched_3:
                                                do {
                                                    switch (__dispatch3 != 0 ? __dispatch3 : (this.token.value & 0xFF))
                                                    {
                                                        case 5:
                                                            braces += 1;
                                                            continue L_outer3;
                                                        case 6:
                                                            if ((braces -= 1) != 0)
                                                                continue L_outer3;
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
                                        s = new ASTBase.UnitTestDeclaration(loc_1, this.token.loc, 0L, null);
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
                                    this.error(new BytePtr("declaration expected, not `%s`"), this.token.toChars());
                                    /*goto Lerror*/{ __dispatch1 = -2; continue dispatched_1; }
                                case 6:
                                case 11:
                                    if ((once) != 0)
                                        this.error(new BytePtr("declaration expected, not `%s`"), this.token.toChars());
                                    return decldefs;
                                case 169:
                                    byte next = this.peekNext();
                                    if ((next & 0xFF) == 123)
                                        s = this.parseStaticCtor(pAttrs);
                                    else if ((next & 0xFF) == 92)
                                        s = this.parseStaticDtor(pAttrs);
                                    else if ((next & 0xFF) == 14)
                                        s = this.parseStaticAssert();
                                    else if ((next & 0xFF) == 183)
                                    {
                                        condition = this.parseStaticIfCondition();
                                        DArray<ASTBase.Dsymbol> athen = null;
                                        if ((this.token.value & 0xFF) == 7)
                                            athen = this.parseBlock(pLastDecl, null);
                                        else
                                        {
                                            Loc lookingForElseSave = this.lookingForElse.copy();
                                            this.lookingForElse = this.token.loc.copy();
                                            athen = this.parseBlock(pLastDecl, null);
                                            this.lookingForElse = lookingForElseSave.copy();
                                        }
                                        DArray<ASTBase.Dsymbol> aelse = null;
                                        if ((this.token.value & 0xFF) == 184)
                                        {
                                            Loc elseloc = this.token.loc.copy();
                                            this.nextToken();
                                            aelse = this.parseBlock(pLastDecl, null);
                                            this.checkDanglingElse(elseloc);
                                        }
                                        s = new ASTBase.StaticIfDeclaration(condition, athen, aelse);
                                    }
                                    else if ((next & 0xFF) == 157)
                                    {
                                        a = this.parseImport();
                                    }
                                    else if (((next & 0xFF) == 201 || (next & 0xFF) == 202))
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
                                    if ((this.peekNext() & 0xFF) == 1)
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    stc = 4L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 182:
                                    if ((this.peekNext() & 0xFF) == 1)
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    stc = 1048576L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 224:
                                    byte next_1 = this.peekNext();
                                    if ((next_1 & 0xFF) == 1)
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
                                    if ((next_1 & 0xFF) == 169)
                                    {
                                        byte next2 = this.peekNext2();
                                        if ((next2 & 0xFF) == 123)
                                        {
                                            s = this.parseSharedStaticCtor(pAttrs);
                                            break;
                                        }
                                        if ((next2 & 0xFF) == 92)
                                        {
                                            s = this.parseSharedStaticDtor(pAttrs);
                                            break;
                                        }
                                    }
                                    stc = 536870912L;
                                    /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                case 177:
                                    if ((this.peekNext() & 0xFF) == 1)
                                        /*goto Ldeclaration*/{ __dispatch1 = -1; continue dispatched_1; }
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
                                        Ref<DArray<ASTBase.Expression>> exps_1 = ref(null);
                                        stc = this.parseAttribute(ptr(exps_1));
                                        if ((stc) != 0)
                                            /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                        (pAttrs).udas = ASTBase.UserAttributeDeclaration.concat((pAttrs).udas, exps_1.value);
                                        /*goto Lautodecl*/{ __dispatch1 = -4; continue dispatched_1; }
                                    }
                                /*Lstc:*/
                                case -3:
                                __dispatch1 = 0;
                                    (pAttrs).storageClass = this.appendStorageClass((pAttrs).storageClass, stc);
                                    this.nextToken();
                                /*Lautodecl:*/
                                case -4:
                                __dispatch1 = 0;
                                    if (((this.token.value & 0xFF) == 120 && this.hasOptionalParensThen(this.peek(this.token), TOK.assign)))
                                    {
                                        a = this.parseAutoDeclarations(getStorageClassASTBase(pAttrs), (pAttrs).comment);
                                        if ((a != null && ((a).length) != 0))
                                            pLastDecl.set(0, (a).get((a).length - 1));
                                        if ((pAttrs).udas != null)
                                        {
                                            s = new ASTBase.UserAttributeDeclaration((pAttrs).udas, a);
                                            (pAttrs).udas = null;
                                        }
                                        break;
                                    }
                                    Ref<Token> tk = ref(null);
                                    if (((((this.token.value & 0xFF) == 120 && this.skipParens(this.peek(this.token), ptr(tk))) && this.skipAttributes(tk.value, ptr(tk))) && (((((((tk.value).value & 0xFF) == 1 || ((tk.value).value & 0xFF) == 5) || ((tk.value).value & 0xFF) == 175) || ((tk.value).value & 0xFF) == 176) || ((tk.value).value & 0xFF) == 187) || (((tk.value).value & 0xFF) == 120 && pequals((tk.value).ident, Id._body)))))
                                    {
                                        a = this.parseDeclarations(true, pAttrs, (pAttrs).comment);
                                        if ((a != null && ((a).length) != 0))
                                            pLastDecl.set(0, (a).get((a).length - 1));
                                        if ((pAttrs).udas != null)
                                        {
                                            s = new ASTBase.UserAttributeDeclaration((pAttrs).udas, a);
                                            (pAttrs).udas = null;
                                        }
                                        break;
                                    }
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    long stc2 = getStorageClassASTBase(pAttrs);
                                    if (stc2 != 0L)
                                    {
                                        s = new ASTBase.StorageClassDeclaration(stc2, a);
                                    }
                                    if ((pAttrs).udas != null)
                                    {
                                        if (s != null)
                                        {
                                            a = new DArray<ASTBase.Dsymbol>();
                                            (a).push(s);
                                        }
                                        s = new ASTBase.UserAttributeDeclaration((pAttrs).udas, a);
                                        (pAttrs).udas = null;
                                    }
                                    break;
                                case 174:
                                    ASTBase.Expression e = null;
                                    {
                                        Ref<ASTBase.Expression> depmsg_ref = ref((pAttrs).depmsg);
                                        long _stc = this.parseDeprecatedAttribute(depmsg_ref);
                                        pAttrs.depmsg = depmsg_ref.value;
                                        if ((_stc) != 0)
                                        {
                                            stc = _stc;
                                            /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                        }
                                    }
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs).depmsg != null)
                                    {
                                        s = new ASTBase.DeprecatedDeclaration((pAttrs).depmsg, a);
                                        (pAttrs).depmsg = null;
                                    }
                                    break;
                                case 3:
                                    if ((this.peekNext() & 0xFF) == 4)
                                        this.error(new BytePtr("empty attribute list is not allowed"));
                                    this.error(new BytePtr("use `@(attributes)` instead of `[attributes]`"));
                                    DArray<ASTBase.Expression> exps_2 = this.parseArguments();
                                    (pAttrs).udas = ASTBase.UserAttributeDeclaration.concat((pAttrs).udas, exps_2);
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs).udas != null)
                                    {
                                        s = new ASTBase.UserAttributeDeclaration((pAttrs).udas, a);
                                        (pAttrs).udas = null;
                                    }
                                    break;
                                case 164:
                                    if (((this.peek(this.token)).value & 0xFF) != 1)
                                    {
                                        stc = 2L;
                                        /*goto Lstc*/{ __dispatch1 = -3; continue dispatched_1; }
                                    }
                                    Loc linkLoc = this.token.loc.copy();
                                    Ref<DArray<Identifier>> idents = ref(null);
                                    Ref<DArray<ASTBase.Expression>> identExps = ref(null);
                                    IntRef cppmangle = ref(CPPMANGLE.def);
                                    Ref<Boolean> cppMangleOnly = ref(false);
                                    int link = this.parseLinkage(ptr(idents), ptr(identExps), cppmangle, cppMangleOnly);
                                    if ((pAttrs).link != LINK.default_)
                                    {
                                        if ((pAttrs).link != link)
                                        {
                                            this.error(new BytePtr("conflicting linkage `extern (%s)` and `extern (%s)`"), ASTBase.linkageToChars((pAttrs).link), ASTBase.linkageToChars(link));
                                        }
                                        else if (((idents.value != null || identExps.value != null) || cppmangle.value != CPPMANGLE.def))
                                        {
                                        }
                                        else
                                            this.error(new BytePtr("redundant linkage `extern (%s)`"), ASTBase.linkageToChars((pAttrs).link));
                                    }
                                    (pAttrs).link = link;
                                    this.linkage = link;
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if (idents.value != null)
                                    {
                                        assert(link == LINK.cpp);
                                        assert(((idents.value).length) != 0);
                                        {
                                            int i = (idents.value).length;
                                            for (; (i) != 0;){
                                                Identifier id = (idents.value).get(i -= 1);
                                                if (s != null)
                                                {
                                                    a = new DArray<ASTBase.Dsymbol>();
                                                    (a).push(s);
                                                }
                                                if (cppMangleOnly.value)
                                                    s = new ASTBase.CPPNamespaceDeclaration(id, a);
                                                else
                                                    s = new ASTBase.Nspace(linkLoc, id, null, a);
                                            }
                                        }
                                        (pAttrs).link = LINK.default_;
                                    }
                                    else if (identExps.value != null)
                                    {
                                        assert(link == LINK.cpp);
                                        assert(((identExps.value).length) != 0);
                                        {
                                            int i_1 = (identExps.value).length;
                                            for (; (i_1) != 0;){
                                                ASTBase.Expression exp = (identExps.value).get(i_1 -= 1);
                                                if (s != null)
                                                {
                                                    a = new DArray<ASTBase.Dsymbol>();
                                                    (a).push(s);
                                                }
                                                if (cppMangleOnly.value)
                                                    s = new ASTBase.CPPNamespaceDeclaration(exp, a);
                                                else
                                                    s = new ASTBase.Nspace(linkLoc, null, exp, a);
                                            }
                                        }
                                        (pAttrs).link = LINK.default_;
                                    }
                                    else if (cppmangle.value != CPPMANGLE.def)
                                    {
                                        assert(link == LINK.cpp);
                                        s = new ASTBase.CPPMangleDeclaration(cppmangle.value, a);
                                    }
                                    else if ((pAttrs).link != LINK.default_)
                                    {
                                        s = new ASTBase.LinkDeclaration((pAttrs).link, a);
                                        (pAttrs).link = LINK.default_;
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
                                        if ((pAttrs).protection.kind != ASTBase.Prot.Kind.undefined)
                                        {
                                            if ((pAttrs).protection.kind != prot)
                                                this.error(new BytePtr("conflicting protection attribute `%s` and `%s`"), ASTBase.protectionToChars((pAttrs).protection.kind), ASTBase.protectionToChars(prot));
                                            else
                                                this.error(new BytePtr("redundant protection attribute `%s`"), ASTBase.protectionToChars(prot));
                                        }
                                        (pAttrs).protection.kind = prot;
                                        this.nextToken();
                                        DArray<Identifier> pkg_prot_idents = null;
                                        if (((pAttrs).protection.kind == ASTBase.Prot.Kind.package_ && (this.token.value & 0xFF) == 1))
                                        {
                                            pkg_prot_idents = this.parseQualifiedIdentifier(new BytePtr("protection package"));
                                            if (pkg_prot_idents != null)
                                                this.check(TOK.rightParentheses);
                                            else
                                            {
                                                for (; ((this.token.value & 0xFF) != 9 && (this.token.value & 0xFF) != 11);) {
                                                    this.nextToken();
                                                }
                                                this.nextToken();
                                                break;
                                            }
                                        }
                                        Loc attrloc = this.token.loc.copy();
                                        a = this.parseBlock(pLastDecl, pAttrs);
                                        if ((pAttrs).protection.kind != ASTBase.Prot.Kind.undefined)
                                        {
                                            if (((pAttrs).protection.kind == ASTBase.Prot.Kind.package_ && pkg_prot_idents != null))
                                                s = new ASTBase.ProtDeclaration(attrloc, pkg_prot_idents, a);
                                            else
                                                s = new ASTBase.ProtDeclaration(attrloc, (pAttrs).protection, a);
                                            (pAttrs).protection = new ASTBase.Prot(ASTBase.Prot.Kind.undefined, null).copy();
                                        }
                                        break;
                                    }
                                case 163:
                                    Loc attrLoc = this.token.loc.copy();
                                    this.nextToken();
                                    ASTBase.Expression e_1 = null;
                                    if ((this.token.value & 0xFF) == 1)
                                    {
                                        this.nextToken();
                                        e_1 = this.parseAssignExp();
                                        this.check(TOK.rightParentheses);
                                    }
                                    if ((pAttrs).setAlignment)
                                    {
                                        if (e_1 != null)
                                            this.error(new BytePtr("redundant alignment attribute `align(%s)`"), e_1.toChars());
                                        else
                                            this.error(new BytePtr("redundant alignment attribute `align`"));
                                    }
                                    (pAttrs).setAlignment = true;
                                    (pAttrs).ealign = e_1;
                                    a = this.parseBlock(pLastDecl, pAttrs);
                                    if ((pAttrs).setAlignment)
                                    {
                                        s = new ASTBase.AlignDeclaration(attrLoc, (pAttrs).ealign, a);
                                        (pAttrs).setAlignment = false;
                                        (pAttrs).ealign = null;
                                    }
                                    break;
                                case 40:
                                    DArray<ASTBase.Expression> args = null;
                                    Loc loc_2 = this.token.loc.copy();
                                    this.nextToken();
                                    this.check(TOK.leftParentheses);
                                    if ((this.token.value & 0xFF) != 120)
                                    {
                                        this.error(new BytePtr("`pragma(identifier)` expected"));
                                        /*goto Lerror*/{ __dispatch1 = -2; continue dispatched_1; }
                                    }
                                    Identifier ident = this.token.ident;
                                    this.nextToken();
                                    if (((this.token.value & 0xFF) == 99 && (this.peekNext() & 0xFF) != 2))
                                        args = this.parseArguments();
                                    else
                                        this.check(TOK.rightParentheses);
                                    DArray<ASTBase.Dsymbol> a2 = null;
                                    if ((this.token.value & 0xFF) == 9)
                                    {
                                        this.nextToken();
                                    }
                                    else
                                        a2 = this.parseBlock(pLastDecl, null);
                                    s = new ASTBase.PragmaDeclaration(loc_2, ident, args, a2);
                                    break;
                                case 173:
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) == 90)
                                    {
                                        this.nextToken();
                                        if ((this.token.value & 0xFF) == 120)
                                            s = new ASTBase.DebugSymbol(this.token.loc, this.token.ident);
                                        else if (((this.token.value & 0xFF) == 105 || (this.token.value & 0xFF) == 107))
                                            s = new ASTBase.DebugSymbol(this.token.loc, (int)this.token.intvalue);
                                        else
                                        {
                                            this.error(new BytePtr("identifier or integer expected, not `%s`"), this.token.toChars());
                                            s = null;
                                        }
                                        this.nextToken();
                                        if ((this.token.value & 0xFF) != 9)
                                            this.error(new BytePtr("semicolon expected"));
                                        this.nextToken();
                                        break;
                                    }
                                    condition = this.parseDebugCondition();
                                    /*goto Lcondition*/{ __dispatch1 = -6; continue dispatched_1; }
                                case 33:
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) == 90)
                                    {
                                        this.nextToken();
                                        if ((this.token.value & 0xFF) == 120)
                                            s = new ASTBase.VersionSymbol(this.token.loc, this.token.ident);
                                        else if (((this.token.value & 0xFF) == 105 || (this.token.value & 0xFF) == 107))
                                            s = new ASTBase.VersionSymbol(this.token.loc, (int)this.token.intvalue);
                                        else
                                        {
                                            this.error(new BytePtr("identifier or integer expected, not `%s`"), this.token.toChars());
                                            s = null;
                                        }
                                        this.nextToken();
                                        if ((this.token.value & 0xFF) != 9)
                                            this.error(new BytePtr("semicolon expected"));
                                        this.nextToken();
                                        break;
                                    }
                                    condition = this.parseVersionCondition();
                                    /*goto Lcondition*/{ __dispatch1 = -6; continue dispatched_1; }
                                /*Lcondition:*/
                                case -6:
                                __dispatch1 = 0;
                                    {
                                        DArray<ASTBase.Dsymbol> athen_1 = null;
                                        if ((this.token.value & 0xFF) == 7)
                                            athen_1 = this.parseBlock(pLastDecl, null);
                                        else
                                        {
                                            Loc lookingForElseSave_1 = this.lookingForElse.copy();
                                            this.lookingForElse = this.token.loc.copy();
                                            athen_1 = this.parseBlock(pLastDecl, null);
                                            this.lookingForElse = lookingForElseSave_1.copy();
                                        }
                                        DArray<ASTBase.Dsymbol> aelse_1 = null;
                                        if ((this.token.value & 0xFF) == 184)
                                        {
                                            Loc elseloc_1 = this.token.loc.copy();
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
                                this.error(new BytePtr("declaration expected, not `%s`"), this.token.toChars());
                            /*Lerror:*/
                            case -2:
                            __dispatch1 = 0;
                                for (; ((this.token.value & 0xFF) != 9 && (this.token.value & 0xFF) != 11);) {
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
                        if (!(s.isAttribDeclaration() != null))
                            pLastDecl.set(0, s);
                        (decldefs).push(s);
                        this.addComment(s, (pAttrs).comment);
                    }
                    else if ((a != null && ((a).length) != 0))
                    {
                        (decldefs).append(a);
                    }
                }
            } while (!((once) != 0));
            this.linkage = linksave;
            return decldefs;
        }

        public  DArray<ASTBase.Dsymbol> parseAutoDeclarations(long storageClass, BytePtr comment) {
            DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
            for (; (1) != 0;){
                Loc loc = this.token.loc.copy();
                Identifier ident = this.token.ident;
                this.nextToken();
                DArray<ASTBase.TemplateParameter> tpl = null;
                if ((this.token.value & 0xFF) == 1)
                    tpl = this.parseTemplateParameterList(0);
                this.check(TOK.assign);
                ASTBase.Initializer _init = this.parseInitializer();
                ASTBase.VarDeclaration v = new ASTBase.VarDeclaration(loc, null, ident, _init, storageClass);
                ASTBase.Dsymbol s = v;
                if (tpl != null)
                {
                    DArray<ASTBase.Dsymbol> a2 = new DArray<ASTBase.Dsymbol>();
                    (a2).push(v);
                    ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                    s = tempdecl;
                }
                (a).push(s);
                switch ((this.token.value & 0xFF))
                {
                    case 9:
                        this.nextToken();
                        this.addComment(s, comment);
                        break;
                    case 99:
                        this.nextToken();
                        if (!(((this.token.value & 0xFF) == 120 && this.hasOptionalParensThen(this.peek(this.token), TOK.assign))))
                        {
                            this.error(new BytePtr("identifier expected following comma"));
                            break;
                        }
                        this.addComment(s, comment);
                        continue;
                    default:
                    this.error(new BytePtr("semicolon expected following auto declaration, not `%s`"), this.token.toChars());
                    break;
                }
                break;
            }
            return a;
        }

        public  DArray<ASTBase.Dsymbol> parseBlock(Ptr<ASTBase.Dsymbol> pLastDecl, PrefixAttributesASTBase pAttrs) {
            DArray<ASTBase.Dsymbol> a = null;
            switch ((this.token.value & 0xFF))
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
                    if ((this.token.value & 0xFF) != 6)
                    {
                        this.error(new BytePtr("matching `}` expected, not `%s`"), this.token.toChars());
                    }
                    else
                        this.nextToken();
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

        public  long appendStorageClass(long storageClass, long stc) {
            if ((((storageClass & stc) != 0 || ((storageClass & 2048L) != 0 && (stc & 524292L) != 0)) || ((stc & 2048L) != 0 && (storageClass & 524292L) != 0)))
            {
                OutBuffer buf = new OutBuffer();
                try {
                    ASTBase.stcToBuffer(buf, stc);
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
                    this.error(new BytePtr("conflicting attribute `%s`"), Token.toChars(this.token.value));
            }
            if ((stc & 1744830464L) != 0)
            {
                long u = storageClass & 1744830464L;
                if ((u & u - 1L) != 0)
                    this.error(new BytePtr("conflicting attribute `%s`"), Token.toChars(this.token.value));
            }
            if ((stc & 60129542144L) != 0)
            {
                long u = storageClass & 60129542144L;
                if ((u & u - 1L) != 0)
                    this.error(new BytePtr("conflicting attribute `@%s`"), this.token.toChars());
            }
            return storageClass;
        }

        public  long parseAttribute(Ptr<DArray<ASTBase.Expression>> pudas) {
            this.nextToken();
            DArray<ASTBase.Expression> udas = null;
            long stc = 0L;
            if ((this.token.value & 0xFF) == 120)
            {
                if (pequals(this.token.ident, Id.property))
                    stc = 4294967296L;
                else if (pequals(this.token.ident, Id.nogc))
                    stc = 4398046511104L;
                else if (pequals(this.token.ident, Id.safe))
                    stc = 8589934592L;
                else if (pequals(this.token.ident, Id.trusted))
                    stc = 17179869184L;
                else if (pequals(this.token.ident, Id.system))
                    stc = 34359738368L;
                else if (pequals(this.token.ident, Id.disable))
                    stc = 137438953472L;
                else if (pequals(this.token.ident, Id.future))
                    stc = 1125899906842624L;
                else
                {
                    ASTBase.Expression exp = this.parsePrimaryExp();
                    if ((this.token.value & 0xFF) == 1)
                    {
                        Loc loc = this.token.loc.copy();
                        exp = new ASTBase.CallExp(loc, exp, this.parseArguments());
                    }
                    udas = new DArray<ASTBase.Expression>();
                    (udas).push(exp);
                }
            }
            else if ((this.token.value & 0xFF) == 1)
            {
                if ((this.peekNext() & 0xFF) == 2)
                    this.error(new BytePtr("empty attribute list is not allowed"));
                udas = this.parseArguments();
            }
            else
            {
                this.error(new BytePtr("@identifier or @(ArgumentList) expected, not `@%s`"), this.token.toChars());
            }
            if ((stc) != 0)
            {
            }
            else if (udas != null)
            {
                pudas.set(0, ASTBase.UserAttributeDeclaration.concat(pudas.get(), udas));
            }
            else
                this.error(new BytePtr("valid attributes are `@property`, `@safe`, `@trusted`, `@system`, `@disable`, `@nogc`"));
            return stc;
        }

        public  long parsePostfix(long storageClass, Ptr<DArray<ASTBase.Expression>> pudas) {
            for (; (1) != 0;){
                long stc = 0L;
                switch ((this.token.value & 0xFF))
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
                        Ref<DArray<ASTBase.Expression>> udas = ref(null);
                        stc = this.parseAttribute(ptr(udas));
                        if (udas.value != null)
                        {
                            if (pudas != null)
                                pudas.set(0, ASTBase.UserAttributeDeclaration.concat(pudas.get(), udas.value));
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
            for (; (1) != 0;){
                if (((this.peek(this.token)).value & 0xFF) == 1)
                    return storageClass;
                long stc = 0L;
                switch ((this.token.value & 0xFF))
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
            if ((this.token.value & 0xFF) == 183)
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
            DArray<ASTBase.TemplateParameter> tpl = null;
            DArray<ASTBase.Dsymbol> decldefs = null;
            ASTBase.Expression constraint = null;
            Loc loc = this.token.loc.copy();
            this.nextToken();
            try {
                if ((this.token.value & 0xFF) != 120)
                {
                    this.error(new BytePtr("identifier expected following `template`"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                id = this.token.ident;
                this.nextToken();
                tpl = this.parseTemplateParameterList(0);
                if (tpl == null)
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                constraint = this.parseConstraint();
                if ((this.token.value & 0xFF) != 5)
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

        public  DArray<ASTBase.TemplateParameter> parseTemplateParameterList(int flag) {
            DArray<ASTBase.TemplateParameter> tpl = new DArray<ASTBase.TemplateParameter>();
            try {
                if ((!((flag) != 0) && (this.token.value & 0xFF) != 1))
                {
                    this.error(new BytePtr("parenthesized template parameter list expected following template identifier"));
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                this.nextToken();
                if (((flag) != 0 || (this.token.value & 0xFF) != 2))
                {
                    int isvariadic = 0;
                L_outer4:
                    for (; (this.token.value & 0xFF) != 2;){
                        ASTBase.TemplateParameter tp = null;
                        Loc loc = new Loc();
                        Ref<Identifier> tp_ident = ref(null);
                        ASTBase.Type tp_spectype = null;
                        ASTBase.Type tp_valtype = null;
                        ASTBase.Type tp_defaulttype = null;
                        ASTBase.Expression tp_specvalue = null;
                        ASTBase.Expression tp_defaultvalue = null;
                        Token t = null;
                        t = this.peek(this.token);
                        if ((this.token.value & 0xFF) == 158)
                        {
                            this.nextToken();
                            loc = this.token.loc.copy();
                            ASTBase.Type spectype = null;
                            if (this.isDeclaration(this.token, NeedDeclaratorId.must, TOK.reserved, null))
                            {
                                spectype = this.parseType(ptr(tp_ident), null);
                            }
                            else
                            {
                                if ((this.token.value & 0xFF) != 120)
                                {
                                    this.error(new BytePtr("identifier expected for template alias parameter"));
                                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                                }
                                tp_ident.value = this.token.ident;
                                this.nextToken();
                            }
                            RootObject spec = null;
                            if ((this.token.value & 0xFF) == 7)
                            {
                                this.nextToken();
                                if (this.isDeclaration(this.token, NeedDeclaratorId.no, TOK.reserved, null))
                                    spec = this.parseType(null, null);
                                else
                                    spec = this.parseCondExp();
                            }
                            RootObject def = null;
                            if ((this.token.value & 0xFF) == 90)
                            {
                                this.nextToken();
                                if (this.isDeclaration(this.token, NeedDeclaratorId.no, TOK.reserved, null))
                                    def = this.parseType(null, null);
                                else
                                    def = this.parseCondExp();
                            }
                            tp = new ASTBase.TemplateAliasParameter(loc, tp_ident.value, spectype, spec, def);
                        }
                        else if ((((((t).value & 0xFF) == 7 || ((t).value & 0xFF) == 90) || ((t).value & 0xFF) == 99) || ((t).value & 0xFF) == 2))
                        {
                            if ((this.token.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("identifier expected for template type parameter"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            loc = this.token.loc.copy();
                            tp_ident.value = this.token.ident;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 7)
                            {
                                this.nextToken();
                                tp_spectype = this.parseType(null, null);
                            }
                            if ((this.token.value & 0xFF) == 90)
                            {
                                this.nextToken();
                                tp_defaulttype = this.parseType(null, null);
                            }
                            tp = new ASTBase.TemplateTypeParameter(loc, tp_ident.value, tp_spectype, tp_defaulttype);
                        }
                        else if (((this.token.value & 0xFF) == 120 && ((t).value & 0xFF) == 10))
                        {
                            if ((isvariadic) != 0)
                                this.error(new BytePtr("variadic template parameter must be last"));
                            isvariadic = 1;
                            loc = this.token.loc.copy();
                            tp_ident.value = this.token.ident;
                            this.nextToken();
                            this.nextToken();
                            tp = new ASTBase.TemplateTupleParameter(loc, tp_ident.value);
                        }
                        else if ((this.token.value & 0xFF) == 123)
                        {
                            this.nextToken();
                            if ((this.token.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("identifier expected for template this parameter"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            loc = this.token.loc.copy();
                            tp_ident.value = this.token.ident;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 7)
                            {
                                this.nextToken();
                                tp_spectype = this.parseType(null, null);
                            }
                            if ((this.token.value & 0xFF) == 90)
                            {
                                this.nextToken();
                                tp_defaulttype = this.parseType(null, null);
                            }
                            tp = new ASTBase.TemplateThisParameter(loc, tp_ident.value, tp_spectype, tp_defaulttype);
                        }
                        else
                        {
                            loc = this.token.loc.copy();
                            tp_valtype = this.parseType(ptr(tp_ident), null);
                            if (!(tp_ident.value != null))
                            {
                                this.error(new BytePtr("identifier expected for template value parameter"));
                                tp_ident.value = Identifier.idPool( new ByteSlice("error"));
                            }
                            if ((this.token.value & 0xFF) == 7)
                            {
                                this.nextToken();
                                tp_specvalue = this.parseCondExp();
                            }
                            if ((this.token.value & 0xFF) == 90)
                            {
                                this.nextToken();
                                tp_defaultvalue = this.parseDefaultInitExp();
                            }
                            tp = new ASTBase.TemplateValueParameter(loc, tp_ident.value, tp_valtype, tp_specvalue, tp_defaultvalue);
                        }
                        (tpl).push(tp);
                        if ((this.token.value & 0xFF) != 99)
                            break;
                        this.nextToken();
                    }
                }
                this.check(TOK.rightParentheses);
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            return tpl;
        }

        public  ASTBase.Dsymbol parseMixin() {
            ASTBase.TemplateMixin tm = null;
            Identifier id = null;
            DArray<RootObject> tiargs = null;
            Loc locMixin = this.token.loc.copy();
            this.nextToken();
            Loc loc = this.token.loc.copy();
            ASTBase.TypeQualified tqual = null;
            if ((this.token.value & 0xFF) == 97)
            {
                id = Id.empty;
            }
            else
            {
                if ((this.token.value & 0xFF) == 39)
                {
                    tqual = this.parseTypeof();
                    this.check(TOK.dot);
                }
                if ((this.token.value & 0xFF) != 120)
                {
                    this.error(new BytePtr("identifier expected, not `%s`"), this.token.toChars());
                    id = Id.empty;
                }
                else
                    id = this.token.ident;
                this.nextToken();
            }
            for (; (1) != 0;){
                tiargs = null;
                if ((this.token.value & 0xFF) == 91)
                {
                    tiargs = this.parseTemplateArguments();
                }
                if ((tiargs != null && (this.token.value & 0xFF) == 97))
                {
                    ASTBase.TemplateInstance tempinst = new ASTBase.TemplateInstance(loc, id, tiargs);
                    if (!(tqual != null))
                        tqual = new ASTBase.TypeInstance(loc, tempinst);
                    else
                        tqual.addInst(tempinst);
                    tiargs = null;
                }
                else
                {
                    if (!(tqual != null))
                        tqual = new ASTBase.TypeIdentifier(loc, id);
                    else
                        tqual.addIdent(id);
                }
                if ((this.token.value & 0xFF) != 97)
                    break;
                this.nextToken();
                if ((this.token.value & 0xFF) != 120)
                {
                    this.error(new BytePtr("identifier expected following `.` instead of `%s`"), this.token.toChars());
                    break;
                }
                loc = this.token.loc.copy();
                id = this.token.ident;
                this.nextToken();
            }
            id = null;
            if ((this.token.value & 0xFF) == 120)
            {
                id = this.token.ident;
                this.nextToken();
            }
            tm = new ASTBase.TemplateMixin(locMixin, id, tqual, tiargs);
            if ((this.token.value & 0xFF) != 9)
                this.error(new BytePtr("`;` expected after mixin"));
            this.nextToken();
            return tm;
        }

        public  DArray<RootObject> parseTemplateArguments() {
            DArray<RootObject> tiargs = null;
            this.nextToken();
            if ((this.token.value & 0xFF) == 1)
            {
                tiargs = this.parseTemplateArgumentList();
            }
            else
            {
                tiargs = this.parseTemplateSingleArgument();
            }
            if ((this.token.value & 0xFF) == 91)
            {
                byte tok = this.peekNext();
                if (((tok & 0xFF) != 63 && (tok & 0xFF) != 175))
                {
                    this.error(new BytePtr("multiple ! arguments are not allowed"));
                    while(true) try {
                    /*Lagain:*/
                        this.nextToken();
                        if ((this.token.value & 0xFF) == 1)
                            this.parseTemplateArgumentList();
                        else
                            this.parseTemplateSingleArgument();
                        if ((((this.token.value & 0xFF) == 91 && ((tok = this.peekNext()) & 0xFF) != 63) && (tok & 0xFF) != 175))
                            /*goto Lagain*/throw Dispatch0.INSTANCE;
                        break;
                    } catch(Dispatch0 __d){}
                }
            }
            return tiargs;
        }

        public  DArray<RootObject> parseTemplateArgumentList() {
            DArray<RootObject> tiargs = new DArray<RootObject>();
            byte endtok = TOK.rightParentheses;
            assert(((this.token.value & 0xFF) == 1 || (this.token.value & 0xFF) == 99));
            this.nextToken();
            for (; (this.token.value & 0xFF) != (endtok & 0xFF);){
                if (this.isDeclaration(this.token, NeedDeclaratorId.no, TOK.reserved, null))
                {
                    ASTBase.Type ta = this.parseType(null, null);
                    (tiargs).push(ta);
                }
                else
                {
                    ASTBase.Expression ea = this.parseAssignExp();
                    (tiargs).push(ea);
                }
                if ((this.token.value & 0xFF) != 99)
                    break;
                this.nextToken();
            }
            this.check(endtok, new BytePtr("template argument list"));
            return tiargs;
        }

        public  DArray<RootObject> parseTemplateSingleArgument() {
            DArray<RootObject> tiargs = new DArray<RootObject>();
            ASTBase.Type ta = null;
            {
                int __dispatch8 = 0;
                dispatched_8:
                do {
                    switch (__dispatch8 != 0 ? __dispatch8 : (this.token.value & 0xFF))
                    {
                        case 120:
                            ta = new ASTBase.TypeIdentifier(this.token.loc, this.token.ident);
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
                            (tiargs).push(ta);
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
                            (tiargs).push(ea);
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
            Loc loc = this.token.loc.copy();
            ASTBase.Expression exp = null;
            ASTBase.Expression msg = null;
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            exp = this.parseAssignExp();
            if ((this.token.value & 0xFF) == 99)
            {
                this.nextToken();
                if ((this.token.value & 0xFF) != 2)
                {
                    msg = this.parseAssignExp();
                    if ((this.token.value & 0xFF) == 99)
                        this.nextToken();
                }
            }
            this.check(TOK.rightParentheses);
            this.check(TOK.semicolon);
            return new ASTBase.StaticAssert(loc, exp, msg);
        }

        public  ASTBase.TypeQualified parseTypeof() {
            ASTBase.TypeQualified t = null;
            Loc loc = this.token.loc.copy();
            this.nextToken();
            this.check(TOK.leftParentheses);
            if ((this.token.value & 0xFF) == 195)
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

        public  int parseLinkage(Ptr<DArray<Identifier>> pidents, Ptr<DArray<ASTBase.Expression>> pIdentExps, IntRef cppmangle, Ref<Boolean> cppMangleOnly) {
            cppmangle.value = CPPMANGLE.def;
            cppMangleOnly.value = false;
            DArray<Identifier> idents = null;
            DArray<ASTBase.Expression> identExps = null;
            cppmangle.value = CPPMANGLE.def;
            int link = LINK.d;
            this.nextToken();
            assert((this.token.value & 0xFF) == 1);
            this.nextToken();
            if ((this.token.value & 0xFF) == 120)
            {
                Identifier id = this.token.ident;
                this.nextToken();
                if (pequals(id, Id.Windows))
                    link = LINK.windows;
                else if (pequals(id, Id.Pascal))
                {
                    this.deprecation(new BytePtr("`extern(Pascal)` is deprecated. You might want to use `extern(Windows)` instead."));
                    link = LINK.pascal;
                }
                else if (pequals(id, Id.D))
                {
                }
                else if (pequals(id, Id.C))
                {
                    link = LINK.c;
                    if ((this.token.value & 0xFF) == 93)
                    {
                        link = LINK.cpp;
                        this.nextToken();
                        if ((this.token.value & 0xFF) == 99)
                        {
                            this.nextToken();
                            if (((this.token.value & 0xFF) == 153 || (this.token.value & 0xFF) == 152))
                            {
                                cppmangle.value = (this.token.value & 0xFF) == 153 ? CPPMANGLE.asClass : CPPMANGLE.asStruct;
                                this.nextToken();
                            }
                            else if ((this.token.value & 0xFF) == 120)
                            {
                                idents = new DArray<Identifier>();
                                for (; (1) != 0;){
                                    Identifier idn = this.token.ident;
                                    (idents).push(idn);
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) == 97)
                                    {
                                        this.nextToken();
                                        if ((this.token.value & 0xFF) == 120)
                                            continue;
                                        this.error(new BytePtr("identifier expected for C++ namespace"));
                                        idents = null;
                                    }
                                    break;
                                }
                            }
                            else
                            {
                                cppMangleOnly.value = true;
                                identExps = new DArray<ASTBase.Expression>();
                                for (; (1) != 0;){
                                    (identExps).push(this.parseCondExp());
                                    if ((this.token.value & 0xFF) != 99)
                                        break;
                                    this.nextToken();
                                }
                            }
                        }
                    }
                }
                else if (pequals(id, Id.Objective))
                {
                    if ((this.token.value & 0xFF) == 75)
                    {
                        this.nextToken();
                        if (pequals(this.token.ident, Id.C))
                        {
                            link = LINK.objc;
                            this.nextToken();
                        }
                        else {
                            /*goto LinvalidLinkage*/
                            this.error(new BytePtr("valid linkage identifiers are `D`, `C`, `C++`, `Objective-C`, `Pascal`, `Windows`, `System`"));
                            link = LINK.d;
                        }
                    }
                    else {
                        /*goto LinvalidLinkage*/
                        this.error(new BytePtr("valid linkage identifiers are `D`, `C`, `C++`, `Objective-C`, `Pascal`, `Windows`, `System`"));
                        link = LINK.d;
                    }
                }
                else if (pequals(id, Id.System))
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

        public  DArray<Identifier> parseQualifiedIdentifier(BytePtr entity) {
            DArray<Identifier> qualified = null;
            do {
                {
                    this.nextToken();
                    if ((this.token.value & 0xFF) != 120)
                    {
                        this.error(new BytePtr("`%s` expected as dot-separated identifiers, got `%s`"), entity, this.token.toChars());
                        return null;
                    }
                    Identifier id = this.token.ident;
                    if (qualified == null)
                        qualified = new DArray<Identifier>();
                    (qualified).push(id);
                    this.nextToken();
                }
            } while ((this.token.value & 0xFF) == 97);
            return qualified;
        }

        public  ASTBase.Condition parseDebugCondition() {
            int level = 1;
            Identifier id = null;
            if ((this.token.value & 0xFF) == 1)
            {
                this.nextToken();
                if ((this.token.value & 0xFF) == 120)
                    id = this.token.ident;
                else if (((this.token.value & 0xFF) == 105 || (this.token.value & 0xFF) == 107))
                    level = (int)this.token.intvalue;
                else
                    this.error(new BytePtr("identifier or integer expected inside debug(...), not `%s`"), this.token.toChars());
                this.nextToken();
                this.check(TOK.rightParentheses);
            }
            return new ASTBase.DebugCondition(this.mod, level, id);
        }

        public  ASTBase.Condition parseVersionCondition() {
            int level = 1;
            Identifier id = null;
            if ((this.token.value & 0xFF) == 1)
            {
                this.nextToken();
                if ((this.token.value & 0xFF) == 120)
                    id = this.token.ident;
                else if (((this.token.value & 0xFF) == 105 || (this.token.value & 0xFF) == 107))
                    level = (int)this.token.intvalue;
                else if ((this.token.value & 0xFF) == 208)
                    id = Identifier.idPool(Token.asString(TOK.unittest_));
                else if ((this.token.value & 0xFF) == 14)
                    id = Identifier.idPool(Token.asString(TOK.assert_));
                else
                    this.error(new BytePtr("identifier or integer expected inside version(...), not `%s`"), this.token.toChars());
                this.nextToken();
                this.check(TOK.rightParentheses);
            }
            else
                this.error(new BytePtr("(condition) expected following `version`"));
            return new ASTBase.VersionCondition(this.mod, level, id);
        }

        public  ASTBase.Condition parseStaticIfCondition() {
            ASTBase.Expression exp = null;
            ASTBase.Condition condition = null;
            Loc loc = this.token.loc.copy();
            this.nextToken();
            this.nextToken();
            if ((this.token.value & 0xFF) == 1)
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

        public  ASTBase.Dsymbol parseCtor(PrefixAttributesASTBase pAttrs) {
            Ref<DArray<ASTBase.Expression>> udas = ref(null);
            Loc loc = this.token.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            if ((((this.token.value & 0xFF) == 1 && (this.peekNext() & 0xFF) == 123) && (this.peekNext2() & 0xFF) == 2))
            {
                this.nextToken();
                this.nextToken();
                this.check(TOK.rightParentheses);
                stc = this.parsePostfix(stc, ptr(udas));
                if ((stc & 1048576L) != 0)
                    this.deprecation(new BytePtr("`immutable` postblit is deprecated. Please use an unqualified postblit."));
                if ((stc & 536870912L) != 0)
                    this.deprecation(new BytePtr("`shared` postblit is deprecated. Please use an unqualified postblit."));
                if ((stc & 4L) != 0)
                    this.deprecation(new BytePtr("`const` postblit is deprecated. Please use an unqualified postblit."));
                if ((stc & 1L) != 0)
                    this.error(loc, new BytePtr("postblit cannot be `static`"));
                ASTBase.PostBlitDeclaration f = new ASTBase.PostBlitDeclaration(loc, Loc.initial, stc, Id.postblit);
                ASTBase.Dsymbol s = this.parseContracts(f);
                if (udas.value != null)
                {
                    DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                    (a).push(f);
                    s = new ASTBase.UserAttributeDeclaration(udas.value, a);
                }
                return s;
            }
            DArray<ASTBase.TemplateParameter> tpl = null;
            if (((this.token.value & 0xFF) == 1 && ((this.peekPastParen(this.token)).value & 0xFF) == 1))
            {
                tpl = this.parseTemplateParameterList(0);
            }
            IntRef varargs = ref(ASTBase.VarArg.none);
            DArray<ASTBase.Parameter> parameters = this.parseParameters(ptr(varargs), null);
            stc = this.parsePostfix(stc, ptr(udas));
            if ((varargs.value != ASTBase.VarArg.none || ASTBase.Parameter.dim(parameters) != 0))
            {
                if ((stc & 1L) != 0)
                    this.error(loc, new BytePtr("constructor cannot be static"));
            }
            else {
                long ss = stc & 536870913L;
                if ((ss) != 0)
                {
                    if (ss == 1L)
                        this.error(loc, new BytePtr("use `static this()` to declare a static constructor"));
                    else if (ss == 536870913L)
                        this.error(loc, new BytePtr("use `shared static this()` to declare a shared static constructor"));
                }
            }
            ASTBase.Expression constraint = tpl != null ? this.parseConstraint() : null;
            ASTBase.Type tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs.value), null, this.linkage, stc);
            tf = tf.addSTC(stc);
            ASTBase.CtorDeclaration f = new ASTBase.CtorDeclaration(loc, Loc.initial, stc, tf, false);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas.value != null)
            {
                DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                (a).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas.value, a);
            }
            if (tpl != null)
            {
                DArray<ASTBase.Dsymbol> decldefs = new DArray<ASTBase.Dsymbol>();
                (decldefs).push(s);
                s = new ASTBase.TemplateDeclaration(loc, f.ident, tpl, constraint, decldefs, false, false);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseDtor(PrefixAttributesASTBase pAttrs) {
            Ref<DArray<ASTBase.Expression>> udas = ref(null);
            Loc loc = this.token.loc.copy();
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
                    if (ss == 1L)
                        this.error(loc, new BytePtr("use `static ~this()` to declare a static destructor"));
                    else if (ss == 536870913L)
                        this.error(loc, new BytePtr("use `shared static ~this()` to declare a shared static destructor"));
                }
            }
            ASTBase.DtorDeclaration f = new ASTBase.DtorDeclaration(loc, Loc.initial, stc, Id.dtor);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas.value != null)
            {
                DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                (a).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas.value, a);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseStaticCtor(PrefixAttributesASTBase pAttrs) {
            Loc loc = this.token.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            this.nextToken();
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, null) | stc;
            if ((stc & 536870912L) != 0)
                this.error(loc, new BytePtr("use `shared static this()` to declare a shared static constructor"));
            else if ((stc & 1L) != 0)
                this.appendStorageClass(stc, 1L);
            else {
                long modStc = stc & 2685403140L;
                if ((modStc) != 0)
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        ASTBase.stcToBuffer(buf, modStc);
                        this.error(loc, new BytePtr("static constructor cannot be `%s`"), buf.peekChars());
                    }
                    finally {
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.StaticCtorDeclaration f = new ASTBase.StaticCtorDeclaration(loc, Loc.initial, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  ASTBase.Dsymbol parseStaticDtor(PrefixAttributesASTBase pAttrs) {
            Ref<DArray<ASTBase.Expression>> udas = ref(null);
            Loc loc = this.token.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            this.nextToken();
            this.check(TOK.this_);
            this.check(TOK.leftParentheses);
            this.check(TOK.rightParentheses);
            stc = this.parsePostfix(stc & -2685403141L, ptr(udas)) | stc;
            if ((stc & 536870912L) != 0)
                this.error(loc, new BytePtr("use `shared static ~this()` to declare a shared static destructor"));
            else if ((stc & 1L) != 0)
                this.appendStorageClass(stc, 1L);
            else {
                long modStc = stc & 2685403140L;
                if ((modStc) != 0)
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        ASTBase.stcToBuffer(buf, modStc);
                        this.error(loc, new BytePtr("static destructor cannot be `%s`"), buf.peekChars());
                    }
                    finally {
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.StaticDtorDeclaration f = new ASTBase.StaticDtorDeclaration(loc, Loc.initial, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas.value != null)
            {
                DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                (a).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas.value, a);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseSharedStaticCtor(PrefixAttributesASTBase pAttrs) {
            Loc loc = this.token.loc.copy();
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
                    this.appendStorageClass(stc, ss);
                else {
                    long modStc = stc & 2685403140L;
                    if ((modStc) != 0)
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            ASTBase.stcToBuffer(buf, modStc);
                            this.error(loc, new BytePtr("shared static constructor cannot be `%s`"), buf.peekChars());
                        }
                        finally {
                        }
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.SharedStaticCtorDeclaration f = new ASTBase.SharedStaticCtorDeclaration(loc, Loc.initial, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  ASTBase.Dsymbol parseSharedStaticDtor(PrefixAttributesASTBase pAttrs) {
            Ref<DArray<ASTBase.Expression>> udas = ref(null);
            Loc loc = this.token.loc.copy();
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
                    this.appendStorageClass(stc, ss);
                else {
                    long modStc = stc & 2685403140L;
                    if ((modStc) != 0)
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            ASTBase.stcToBuffer(buf, modStc);
                            this.error(loc, new BytePtr("shared static destructor cannot be `%s`"), buf.peekChars());
                        }
                        finally {
                        }
                    }
                }
            }
            stc &= -2685403142L;
            ASTBase.SharedStaticDtorDeclaration f = new ASTBase.SharedStaticDtorDeclaration(loc, Loc.initial, stc);
            ASTBase.Dsymbol s = this.parseContracts(f);
            if (udas.value != null)
            {
                DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                (a).push(f);
                s = new ASTBase.UserAttributeDeclaration(udas.value, a);
            }
            return s;
        }

        public  ASTBase.Dsymbol parseInvariant(PrefixAttributesASTBase pAttrs) {
            Loc loc = this.token.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            if ((this.token.value & 0xFF) == 1)
            {
                this.nextToken();
                if ((this.token.value & 0xFF) != 2)
                {
                    ASTBase.Expression e = this.parseAssignExp();
                    ASTBase.Expression msg = null;
                    if ((this.token.value & 0xFF) == 99)
                    {
                        this.nextToken();
                        if ((this.token.value & 0xFF) != 2)
                        {
                            msg = this.parseAssignExp();
                            if ((this.token.value & 0xFF) == 99)
                                this.nextToken();
                        }
                    }
                    this.check(TOK.rightParentheses);
                    this.check(TOK.semicolon);
                    e = new ASTBase.AssertExp(loc, e, msg);
                    ASTBase.ExpStatement fbody = new ASTBase.ExpStatement(loc, e);
                    ASTBase.InvariantDeclaration f = new ASTBase.InvariantDeclaration(loc, this.token.loc, stc, null, fbody);
                    return f;
                }
                this.nextToken();
            }
            ASTBase.Statement fbody = this.parseStatement(4, null, null);
            ASTBase.InvariantDeclaration f = new ASTBase.InvariantDeclaration(loc, this.token.loc, stc, null, fbody);
            return f;
        }

        public  ASTBase.Dsymbol parseUnitTest(PrefixAttributesASTBase pAttrs) {
            Loc loc = this.token.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            BytePtr begPtr = pcopy(this.token.ptr.plus(1));
            Ref<BytePtr> endPtr = ref(null);
            ASTBase.Statement sbody = this.parseStatement(4, ptr(endPtr), null);
            BytePtr docline = null;
            if ((global.params.doDocComments && endPtr.value.greaterThan(begPtr)))
            {
                {
                    BytePtr p = pcopy(endPtr.value.minus(1));
                    for (; (begPtr.lessOrEqual(p) && ((((p.get() & 0xFF) == 32 || (p.get() & 0xFF) == 13) || (p.get() & 0xFF) == 10) || (p.get() & 0xFF) == 9));p.minusAssign(1)){
                        endPtr.value = pcopy(p);
                    }
                }
                int len = ((endPtr.value.minus(begPtr)));
                if (len > 0)
                {
                    docline = pcopy((toBytePtr(Mem.xmalloc(len + 2))));
                    memcpy((BytePtr)(docline), (begPtr), len);
                    docline.set(len, (byte)10);
                    docline.set((len + 1), (byte)0);
                }
            }
            ASTBase.UnitTestDeclaration f = new ASTBase.UnitTestDeclaration(loc, this.token.loc, stc, docline);
            f.fbody = sbody;
            return f;
        }

        public  ASTBase.Dsymbol parseNew(PrefixAttributesASTBase pAttrs) {
            Loc loc = this.token.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            IntRef varargs = ref(ASTBase.VarArg.none);
            DArray<ASTBase.Parameter> parameters = this.parseParameters(ptr(varargs), null);
            ASTBase.NewDeclaration f = new ASTBase.NewDeclaration(loc, Loc.initial, stc, parameters, varargs.value);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  ASTBase.Dsymbol parseDelete(PrefixAttributesASTBase pAttrs) {
            Loc loc = this.token.loc.copy();
            long stc = getStorageClassASTBase(pAttrs);
            this.nextToken();
            IntRef varargs = ref(ASTBase.VarArg.none);
            DArray<ASTBase.Parameter> parameters = this.parseParameters(ptr(varargs), null);
            if (varargs.value != ASTBase.VarArg.none)
                this.error(new BytePtr("`...` not allowed in delete function parameter list"));
            ASTBase.DeleteDeclaration f = new ASTBase.DeleteDeclaration(loc, Loc.initial, stc, parameters);
            ASTBase.Dsymbol s = this.parseContracts(f);
            return s;
        }

        public  DArray<ASTBase.Parameter> parseParameters(IntPtr pvarargs, Ptr<DArray<ASTBase.TemplateParameter>> tpl) {
            DArray<ASTBase.Parameter> parameters = new DArray<ASTBase.Parameter>();
            int varargs = ASTBase.VarArg.none;
            int hasdefault = 0;
            this.check(TOK.leftParentheses);
        L_outer5:
            for (; (1) != 0;){
                Ref<Identifier> ai = ref(null);
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                ASTBase.Expression ae = null;
                DArray<ASTBase.Expression> udas = null;
                try {
                L_outer6:
                    for (; (1) != 0;this.nextToken()){
                    /*L3:*/
                        {
                            int __dispatch9 = 0;
                            dispatched_9:
                            do {
                                switch (__dispatch9 != 0 ? __dispatch9 : (this.token.value & 0xFF))
                                {
                                    case 2:
                                        if ((storageClass != 0L || udas != null))
                                            this.error(new BytePtr("basic type expected, not `)`"));
                                        break;
                                    case 10:
                                        varargs = ASTBase.VarArg.variadic;
                                        this.nextToken();
                                        break;
                                    case 171:
                                        if (((this.peek(this.token)).value & 0xFF) == 1)
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        stc = 4L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 182:
                                        if (((this.peek(this.token)).value & 0xFF) == 1)
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        stc = 1048576L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 224:
                                        if (((this.peek(this.token)).value & 0xFF) == 1)
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        stc = 536870912L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 177:
                                        if (((this.peek(this.token)).value & 0xFF) == 1)
                                            /*goto default*/ { __dispatch9 = -3; continue dispatched_9; }
                                        stc = 2147483648L;
                                        /*goto L2*/{ __dispatch9 = -1; continue dispatched_9; }
                                    case 225:
                                        Ref<DArray<ASTBase.Expression>> exps = ref(null);
                                        long stc2 = this.parseAttribute(ptr(exps));
                                        if ((((((stc2 == 4294967296L || stc2 == 4398046511104L) || stc2 == 137438953472L) || stc2 == 8589934592L) || stc2 == 17179869184L) || stc2 == 34359738368L))
                                        {
                                            this.error(new BytePtr("`@%s` attribute for function parameter is not supported"), this.token.toChars());
                                        }
                                        else
                                        {
                                            udas = ASTBase.UserAttributeDeclaration.concat(udas, exps.value);
                                        }
                                        if ((this.token.value & 0xFF) == 10)
                                            this.error(new BytePtr("variadic parameter cannot have user-defined attributes"));
                                        if ((stc2) != 0)
                                            this.nextToken();
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
                                    stc = storageClass & 2111488L;
                                    if (((stc & stc - 1L) != 0 && !(stc == 2099200L)))
                                        this.error(new BytePtr("incompatible parameter storage classes"));
                                    if ((tpl != null && (this.token.value & 0xFF) == 120))
                                    {
                                        Token t = this.peek(this.token);
                                        if (((((t).value & 0xFF) == 99 || ((t).value & 0xFF) == 2) || ((t).value & 0xFF) == 10))
                                        {
                                            Identifier id = Identifier.generateId(new BytePtr("__T"));
                                            Loc loc = this.token.loc.copy();
                                            at = new ASTBase.TypeIdentifier(loc, id);
                                            if (tpl.get() == null)
                                                tpl.set(0, (new DArray<ASTBase.TemplateParameter>()));
                                            ASTBase.TemplateParameter tp = new ASTBase.TemplateTypeParameter(loc, id, null, null);
                                            (tpl.get()).push(tp);
                                            ai.value = this.token.ident;
                                            this.nextToken();
                                        }
                                        else
                                            /*goto _else*/ {
                                            __dispatch9 = 0;
                                                at = this.parseType(ptr(ai), null);
                                            }
                                    }
                                    else
                                    {
                                    /*_else:*/
                                    //case -2:
                                    __dispatch9 = 0;
                                        at = this.parseType(ptr(ai), null);
                                    }
                                    ae = null;
                                    if ((this.token.value & 0xFF) == 90)
                                    {
                                        this.nextToken();
                                        ae = this.parseDefaultInitExp();
                                        hasdefault = 1;
                                    }
                                    else
                                    {
                                        if ((hasdefault) != 0)
                                            this.error(new BytePtr("default argument expected for `%s`"), ai.value != null ? ai.value.toChars() : at.toChars());
                                    }
                                    ASTBase.Parameter param = new ASTBase.Parameter(storageClass, at, ai.value, ae, null);
                                    if (udas != null)
                                    {
                                        DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                                        ASTBase.UserAttributeDeclaration udad = new ASTBase.UserAttributeDeclaration(udas, a);
                                        param.userAttribDecl = udad;
                                    }
                                    if ((this.token.value & 0xFF) == 225)
                                    {
                                        Ref<DArray<ASTBase.Expression>> exps_1 = ref(null);
                                        long stc2_1 = this.parseAttribute(ptr(exps_1));
                                        if ((((((stc2_1 == 4294967296L || stc2_1 == 4398046511104L) || stc2_1 == 137438953472L) || stc2_1 == 8589934592L) || stc2_1 == 17179869184L) || stc2_1 == 34359738368L))
                                        {
                                            this.error(new BytePtr("`@%s` attribute for function parameter is not supported"), this.token.toChars());
                                        }
                                        else
                                        {
                                            this.error(new BytePtr("user-defined attributes cannot appear as postfixes"), this.token.toChars());
                                        }
                                        if ((stc2_1) != 0)
                                            this.nextToken();
                                    }
                                    if ((this.token.value & 0xFF) == 10)
                                    {
                                        if ((storageClass & 2101248L) != 0)
                                            this.error(new BytePtr("variadic argument cannot be `out` or `ref`"));
                                        varargs = ASTBase.VarArg.typesafe;
                                        (parameters).push(param);
                                        this.nextToken();
                                        break;
                                    }
                                    (parameters).push(param);
                                    if ((this.token.value & 0xFF) == 99)
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

        public  ASTBase.EnumDeclaration parseEnum() {
            ASTBase.EnumDeclaration e = null;
            Identifier id = null;
            ASTBase.Type memtype = null;
            Loc loc = this.token.loc.copy();
            this.nextToken();
            id = null;
            if ((this.token.value & 0xFF) == 120)
            {
                id = this.token.ident;
                this.nextToken();
            }
            memtype = null;
            if ((this.token.value & 0xFF) == 7)
            {
                this.nextToken();
                IntRef alt = ref(0);
                Loc typeLoc = this.token.loc.copy();
                memtype = this.parseBasicType(false);
                memtype = this.parseDeclarator(memtype, ptr(alt), null, null, 0L, null, null);
                this.checkCstyleTypeSyntax(typeLoc, memtype, alt.value, null);
            }
            e = new ASTBase.EnumDeclaration(loc, id, memtype);
            if (((this.token.value & 0xFF) == 9 && id != null))
                this.nextToken();
            else if ((this.token.value & 0xFF) == 5)
            {
                boolean isAnonymousEnum = !(id != null);
                e.members = new DArray<ASTBase.Dsymbol>();
                this.nextToken();
                BytePtr comment = pcopy(this.token.blockComment);
            L_outer7:
                for (; (this.token.value & 0xFF) != 6;){
                    loc = this.token.loc.copy();
                    ASTBase.Type type = null;
                    Ref<Identifier> ident = ref(null);
                    Ref<DArray<ASTBase.Expression>> udas = ref(null);
                    long stc = 0L;
                    Ref<ASTBase.Expression> deprecationMessage = ref(null);
                    ByteSlice attributeErrorMessage =  new ByteSlice("`%s` is not a valid attribute for enum members");
                L_outer8:
                    for (; (((this.token.value & 0xFF) != 6 && (this.token.value & 0xFF) != 99) && (this.token.value & 0xFF) != 90);){
                        {
                            int __dispatch10 = 0;
                            dispatched_10:
                            do {
                                switch (__dispatch10 != 0 ? __dispatch10 : (this.token.value & 0xFF))
                                {
                                    case 225:
                                        {
                                            long _stc = this.parseAttribute(ptr(udas));
                                            if ((_stc) != 0)
                                            {
                                                if (_stc == 137438953472L)
                                                    stc |= _stc;
                                                else
                                                {
                                                    OutBuffer buf = new OutBuffer();
                                                    try {
                                                        ASTBase.stcToBuffer(buf, _stc);
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
                                        Token tp = this.peek(this.token);
                                        if (((((tp).value & 0xFF) == 90 || ((tp).value & 0xFF) == 99) || ((tp).value & 0xFF) == 6))
                                        {
                                            ident.value = this.token.ident;
                                            type = null;
                                            this.nextToken();
                                        }
                                        else
                                        {
                                            /*goto default*/ { __dispatch10 = -1; continue dispatched_10; }
                                        }
                                        break;
                                    default:
                                    if (isAnonymousEnum)
                                    {
                                        type = this.parseType(ptr(ident), null);
                                        if (pequals(type, ASTBase.Type.terror))
                                        {
                                            type = null;
                                            this.nextToken();
                                        }
                                    }
                                    else
                                    {
                                        this.error(new BytePtr("`%s` is not a valid attribute for enum members"), this.token.toChars());
                                        this.nextToken();
                                    }
                                    break;
                                }
                            } while(__dispatch10 != 0);
                        }
                    }
                    if ((type != null && !pequals(type, ASTBase.Type.terror)))
                    {
                        if (!(ident.value != null))
                            this.error(new BytePtr("no identifier for declarator `%s`"), type.toChars());
                        if (!(isAnonymousEnum))
                            this.error(new BytePtr("type only allowed if anonymous enum and no enum type"));
                    }
                    ASTBase.Expression value = null;
                    if ((this.token.value & 0xFF) == 90)
                    {
                        this.nextToken();
                        value = this.parseAssignExp();
                    }
                    else
                    {
                        value = null;
                        if (((type != null && !pequals(type, ASTBase.Type.terror)) && isAnonymousEnum))
                            this.error(new BytePtr("if type, there must be an initializer"));
                    }
                    ASTBase.UserAttributeDeclaration uad = null;
                    if (udas.value != null)
                        uad = new ASTBase.UserAttributeDeclaration(udas.value, null);
                    ASTBase.DeprecatedDeclaration dd = null;
                    if (deprecationMessage.value != null)
                    {
                        dd = new ASTBase.DeprecatedDeclaration(deprecationMessage.value, null);
                        stc |= 1024L;
                    }
                    ASTBase.EnumMember em = new ASTBase.EnumMember(loc, ident.value, value, type, stc, uad, dd);
                    (e.members).push(em);
                    if ((this.token.value & 0xFF) == 6)
                    {
                    }
                    else
                    {
                        this.addComment(em, comment);
                        comment = null;
                        this.check(TOK.comma);
                    }
                    this.addComment(em, comment);
                    comment = pcopy(this.token.blockComment);
                    if ((this.token.value & 0xFF) == 11)
                    {
                        this.error(new BytePtr("premature end of file"));
                        break;
                    }
                }
                this.nextToken();
            }
            else
                this.error(new BytePtr("enum declaration is invalid"));
            return e;
        }

        public  ASTBase.Dsymbol parseAggregate() {
            DArray<ASTBase.TemplateParameter> tpl = null;
            ASTBase.Expression constraint = null;
            Loc loc = this.token.loc.copy();
            byte tok = this.token.value;
            this.nextToken();
            Identifier id = null;
            if ((this.token.value & 0xFF) != 120)
            {
                id = null;
            }
            else
            {
                id = this.token.ident;
                this.nextToken();
                if ((this.token.value & 0xFF) == 1)
                {
                    tpl = this.parseTemplateParameterList(0);
                    constraint = this.parseConstraint();
                }
            }
            DArray<ASTBase.BaseClass> baseclasses = null;
            if ((this.token.value & 0xFF) == 7)
            {
                if (((tok & 0xFF) != 154 && (tok & 0xFF) != 153))
                    this.error(new BytePtr("base classes are not allowed for `%s`, did you mean `;`?"), Token.toChars(tok));
                this.nextToken();
                baseclasses = this.parseBaseClasses();
            }
            if ((this.token.value & 0xFF) == 183)
            {
                if (constraint != null)
                    this.error(new BytePtr("template constraints appear both before and after BaseClassList, put them before"));
                constraint = this.parseConstraint();
            }
            if (constraint != null)
            {
                if (!(id != null))
                    this.error(new BytePtr("template constraints not allowed for anonymous `%s`"), Token.toChars(tok));
                if (tpl == null)
                    this.error(new BytePtr("template constraints only allowed for templates"));
            }
            DArray<ASTBase.Dsymbol> members = null;
            if ((this.token.value & 0xFF) == 5)
            {
                Loc lookingForElseSave = this.lookingForElse.copy();
                this.lookingForElse = new Loc(null, 0, 0).copy();
                this.nextToken();
                members = this.parseDeclDefs(0, null, null);
                this.lookingForElse = lookingForElseSave.copy();
                if ((this.token.value & 0xFF) != 6)
                {
                    this.error(new BytePtr("`}` expected following members in `%s` declaration at %s"), Token.toChars(tok), loc.toChars(global.params.showColumns));
                }
                this.nextToken();
            }
            else if (((this.token.value & 0xFF) == 9 && id != null))
            {
                if ((baseclasses != null || constraint != null))
                    this.error(new BytePtr("members expected"));
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
                    if (!(id != null))
                        this.error(loc, new BytePtr("anonymous interfaces not allowed"));
                    a = new ASTBase.InterfaceDeclaration(loc, id, baseclasses);
                    a.members = members;
                    break;
                case 153:
                    if (!(id != null))
                        this.error(loc, new BytePtr("anonymous classes not allowed"));
                    boolean inObject = ((this.md != null && (this.md).packages == null) && pequals((this.md).id, Id.object));
                    a = new ASTBase.ClassDeclaration(loc, id, baseclasses, members, inObject);
                    break;
                case 152:
                    if (id != null)
                    {
                        boolean inObject_1 = ((this.md != null && (this.md).packages == null) && pequals((this.md).id, Id.object));
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
                DArray<ASTBase.Dsymbol> decldefs = new DArray<ASTBase.Dsymbol>();
                (decldefs).push(a);
                ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, id, tpl, constraint, decldefs, false, false);
                return tempdecl;
            }
            return a;
        }

        public  DArray<ASTBase.BaseClass> parseBaseClasses() {
            DArray<ASTBase.BaseClass> baseclasses = new DArray<ASTBase.BaseClass>();
            for (; (1) != 0;this.nextToken()){
                ASTBase.BaseClass b = new ASTBase.BaseClass(this.parseBasicType(false));
                (baseclasses).push(b);
                if ((this.token.value & 0xFF) != 99)
                    break;
            }
            return baseclasses;
        }

        public  DArray<ASTBase.Dsymbol> parseImport() {
            DArray<ASTBase.Dsymbol> decldefs = new DArray<ASTBase.Dsymbol>();
            Identifier aliasid = null;
            int isstatic = (((this.token.value & 0xFF) == 169) ? 1 : 0);
            if ((isstatic) != 0)
                this.nextToken();
        L_outer9:
            do {
                {
                    while(true) try {
                    /*L1:*/
                        this.nextToken();
                        if ((this.token.value & 0xFF) != 120)
                        {
                            this.error(new BytePtr("identifier expected following `import`"));
                            break;
                        }
                        Loc loc = this.token.loc.copy();
                        Identifier id = this.token.ident;
                        DArray<Identifier> a = null;
                        this.nextToken();
                        if ((!(aliasid != null) && (this.token.value & 0xFF) == 90))
                        {
                            aliasid = id;
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        for (; (this.token.value & 0xFF) == 97;){
                            if (a == null)
                                a = new DArray<Identifier>();
                            (a).push(id);
                            this.nextToken();
                            if ((this.token.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("identifier expected following `package`"));
                                break;
                            }
                            id = this.token.ident;
                            this.nextToken();
                        }
                        ASTBase.Import s = new ASTBase.Import(loc, a, id, aliasid, isstatic);
                        (decldefs).push(s);
                        if ((this.token.value & 0xFF) == 7)
                        {
                            do {
                                {
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) != 120)
                                    {
                                        this.error(new BytePtr("identifier expected following `:`"));
                                        break;
                                    }
                                    Identifier _alias = this.token.ident;
                                    Identifier name = null;
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) == 90)
                                    {
                                        this.nextToken();
                                        if ((this.token.value & 0xFF) != 120)
                                        {
                                            this.error(new BytePtr("identifier expected following `%s=`"), _alias.toChars());
                                            break;
                                        }
                                        name = this.token.ident;
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        name = _alias;
                                        _alias = null;
                                    }
                                    s.addAlias(name, _alias);
                                }
                            } while ((this.token.value & 0xFF) == 99);
                            break;
                        }
                        aliasid = null;
                        break;
                    } catch(Dispatch0 __d){}
                }
            } while ((this.token.value & 0xFF) == 99);
            if ((this.token.value & 0xFF) == 9)
                this.nextToken();
            else
            {
                this.error(new BytePtr("`;` expected"));
                this.nextToken();
            }
            return decldefs;
        }

        public  ASTBase.Type parseType(Ptr<Identifier> pident, Ptr<DArray<ASTBase.TemplateParameter>> ptpl) {
            long stc = 0L;
            for (; (1) != 0;){
                switch ((this.token.value & 0xFF))
                {
                    case 171:
                        if ((this.peekNext() & 0xFF) == 1)
                            break;
                        stc |= 4L;
                        this.nextToken();
                        continue;
                    case 182:
                        if ((this.peekNext() & 0xFF) == 1)
                            break;
                        stc |= 1048576L;
                        this.nextToken();
                        continue;
                    case 224:
                        if ((this.peekNext() & 0xFF) == 1)
                            break;
                        stc |= 536870912L;
                        this.nextToken();
                        continue;
                    case 177:
                        if ((this.peekNext() & 0xFF) == 1)
                            break;
                        stc |= 2147483648L;
                        this.nextToken();
                        continue;
                    default:
                    break;
                }
                break;
            }
            Loc typeLoc = this.token.loc.copy();
            ASTBase.Type t = null;
            t = this.parseBasicType(false);
            IntRef alt = ref(0);
            t = this.parseDeclarator(t, ptr(alt), pident, ptpl, 0L, null, null);
            this.checkCstyleTypeSyntax(typeLoc, t, alt.value, pident != null ? pident.get() : null);
            t = t.addSTC(stc);
            return t;
        }

        public  ASTBase.Type parseBasicType(boolean dontLookDotIdents) {
            ASTBase.Type t = null;
            Loc loc = new Loc();
            Identifier id = null;
            {
                int __dispatch13 = 0;
                dispatched_13:
                do {
                    switch (__dispatch13 != 0 ? __dispatch13 : (this.token.value & 0xFF))
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
                            if ((this.token.value & 0xFF) == 135)
                            {
                                this.error(new BytePtr("use `long` for a 64 bit integer instead of `long long`"));
                                this.nextToken();
                            }
                            else if ((this.token.value & 0xFF) == 140)
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
                            loc = this.token.loc.copy();
                            id = this.token.ident;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 91)
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
                            t = this.parseBasicTypeStartingAt(new ASTBase.TypeIdentifier(this.token.loc, Id.empty), dontLookDotIdents);
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
                                if (te != null)
                                    if ((te.ident != null && te.args != null))
                                    {
                                        t = new ASTBase.TypeTraits(this.token.loc, te);
                                        break;
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
                        this.error(new BytePtr("basic type expected, not `%s`"), this.token.toChars());
                        if ((this.token.value & 0xFF) == 184)
                            this.errorSupplemental(this.token.loc, new BytePtr("There's no `static else`, use `else` instead."));
                        t = ASTBase.Type.terror;
                        break;
                    }
                } while(__dispatch13 != 0);
            }
            return t;
        }

        public  ASTBase.Type parseBasicTypeStartingAt(ASTBase.TypeQualified tid, boolean dontLookDotIdents) {
            ASTBase.Type maybeArray = null;
            try {
            L_outer10:
                for (; (1) != 0;){
                    {
                        int __dispatch14 = 0;
                        dispatched_14:
                        do {
                            switch (__dispatch14 != 0 ? __dispatch14 : (this.token.value & 0xFF))
                            {
                                case 97:
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) != 120)
                                    {
                                        this.error(new BytePtr("identifier expected following `.` instead of `%s`"), this.token.toChars());
                                        break;
                                    }
                                    if (maybeArray != null)
                                    {
                                        DArray<RootObject> dimStack = new DArray<RootObject>();
                                        try {
                                            ASTBase.Type t = maybeArray;
                                            for (; true;){
                                                if ((t.ty & 0xFF) == ASTBase.ENUMTY.Tsarray)
                                                {
                                                    ASTBase.TypeSArray a = (ASTBase.TypeSArray)t;
                                                    dimStack.push(a.dim.syntaxCopy());
                                                    t = a.next.syntaxCopy();
                                                }
                                                else if ((t.ty & 0xFF) == ASTBase.ENUMTY.Taarray)
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
                                            assert(dimStack.length > 0);
                                            tid = (ASTBase.TypeQualified)t;
                                            for (; (dimStack.length) != 0;){
                                                tid.addIndex(dimStack.pop());
                                            }
                                            maybeArray = null;
                                        }
                                        finally {
                                        }
                                    }
                                    Loc loc = this.token.loc.copy();
                                    Identifier id = this.token.ident;
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) == 91)
                                    {
                                        ASTBase.TemplateInstance tempinst = new ASTBase.TemplateInstance(loc, id, this.parseTemplateArguments());
                                        tid.addInst(tempinst);
                                    }
                                    else
                                        tid.addIdent(id);
                                    continue L_outer10;
                                case 3:
                                    if (dontLookDotIdents)
                                        /*goto Lend*/throw Dispatch0.INSTANCE;
                                    this.nextToken();
                                    ASTBase.Type t_1 = maybeArray != null ? maybeArray : tid;
                                    if ((this.token.value & 0xFF) == 4)
                                    {
                                        t_1 = new ASTBase.TypeDArray(t_1);
                                        this.nextToken();
                                        return t_1;
                                    }
                                    else if (this.isDeclaration(this.token, NeedDeclaratorId.no, TOK.rightBracket, null))
                                    {
                                        ASTBase.Type index = this.parseType(null, null);
                                        maybeArray = new ASTBase.TypeAArray(t_1, index);
                                        this.check(TOK.rightBracket);
                                    }
                                    else
                                    {
                                        this.inBrackets++;
                                        ASTBase.Expression e = this.parseAssignExp();
                                        if ((this.token.value & 0xFF) == 31)
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
            for (; (1) != 0;){
                switch ((this.token.value & 0xFF))
                {
                    case 78:
                        t = new ASTBase.TypePointer(t);
                        this.nextToken();
                        continue;
                    case 3:
                        this.nextToken();
                        if ((this.token.value & 0xFF) == 4)
                        {
                            t = new ASTBase.TypeDArray(t);
                            this.nextToken();
                        }
                        else if (this.isDeclaration(this.token, NeedDeclaratorId.no, TOK.rightBracket, null))
                        {
                            ASTBase.Type index = this.parseType(null, null);
                            t = new ASTBase.TypeAArray(t, index);
                            this.check(TOK.rightBracket);
                        }
                        else
                        {
                            this.inBrackets++;
                            ASTBase.Expression e = this.parseAssignExp();
                            if ((this.token.value & 0xFF) == 31)
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
                        byte save = this.token.value;
                        this.nextToken();
                        IntRef varargs = ref(ASTBase.VarArg.none);
                        DArray<ASTBase.Parameter> parameters = this.parseParameters(ptr(varargs), null);
                        long stc = this.parsePostfix(0L, null);
                        ASTBase.TypeFunction tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs.value), t, this.linkage, stc);
                        if ((stc & 17594871447556L) != 0)
                        {
                            if ((save & 0xFF) == 161)
                                this.error(new BytePtr("`const`/`immutable`/`shared`/`inout`/`return` attributes are only valid for non-static member functions"));
                            else
                                tf = (ASTBase.TypeFunction)tf.addSTC(stc);
                        }
                        t = (save & 0xFF) == 160 ? new ASTBase.TypeDelegate(tf) : new ASTBase.TypePointer(tf);
                        continue;
                    default:
                    return t;
                }
                //throw new AssertionError("Unreachable code!");
            }
            //throw new AssertionError("Unreachable code!");
        }

        public  ASTBase.Type parseDeclarator(ASTBase.Type t, IntPtr palt, Ptr<Identifier> pident, Ptr<DArray<ASTBase.TemplateParameter>> tpl, long storageClass, IntPtr pdisable, Ptr<DArray<ASTBase.Expression>> pudas) {
            t = this.parseBasicType2(t);
            Ref<ASTBase.Type> ts = ref(null);
            switch ((this.token.value & 0xFF))
            {
                case 120:
                    if (pident != null)
                        pident.set(0, this.token.ident);
                    else
                        this.error(new BytePtr("unexpected identifier `%s` in declarator"), this.token.ident.toChars());
                    ts.value = t;
                    this.nextToken();
                    break;
                case 1:
                    if (((this.peekNext() & 0xFF) == 78 || (this.peekNext() & 0xFF) == 1))
                    {
                        palt.set(0, palt.get() | 1);
                        this.nextToken();
                        ts.value = this.parseDeclarator(t, palt, pident, null, 0L, null, null);
                        this.check(TOK.rightParentheses);
                        break;
                    }
                    ts.value = t;
                    Ref<Token> peekt = ref(this.token);
                    if (this.isParameters(ptr(peekt)))
                    {
                        this.error(new BytePtr("function declaration without return type. (Note that constructors are always named `this`)"));
                    }
                    else
                        this.error(new BytePtr("unexpected `(` in declarator"));
                    break;
                default:
                ts.value = t;
                break;
            }
            for (; (1) != 0;){
                switch ((this.token.value & 0xFF))
                {
                    case 3:
                        ASTBase.TypeNext ta = null;
                        this.nextToken();
                        if ((this.token.value & 0xFF) == 4)
                        {
                            ta = new ASTBase.TypeDArray(t);
                            this.nextToken();
                            palt.set(0, palt.get() | 2);
                        }
                        else if (this.isDeclaration(this.token, NeedDeclaratorId.no, TOK.rightBracket, null))
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
                            for (; !pequals(pt.get(), t);pt = pcopy((new PtrToNext((ASTBase.TypeNext)pt.get())))){
                            }
                        }
                        pt.set(0, ta);
                        continue;
                    case 1:
                        if (tpl != null)
                        {
                            Token tk = this.peekPastParen(this.token);
                            if (((tk).value & 0xFF) == 1)
                            {
                                tpl.set(0, this.parseTemplateParameterList(0));
                            }
                            else if (((tk).value & 0xFF) == 90)
                            {
                                tpl.set(0, this.parseTemplateParameterList(0));
                                break;
                            }
                        }
                        IntRef varargs = ref(ASTBase.VarArg.none);
                        DArray<ASTBase.Parameter> parameters = this.parseParameters(ptr(varargs), null);
                        long stc = this.parsePostfix(storageClass, pudas);
                        ASTBase.Type tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs.value), t, this.linkage, stc);
                        tf = tf.addSTC(stc);
                        if (pdisable != null)
                            pdisable.set(0, ((stc & 137438953472L) != 0 ? 1 : 0));
                        Ptr<ASTBase.Type> pt_1 = null;
                        {
                            pt_1 = pcopy(ptr(ts));
                            for (; !pequals(pt_1.get(), t);pt_1 = pcopy((new PtrToNext((ASTBase.TypeNext)pt_1.get())))){
                            }
                        }
                        pt_1.set(0, tf);
                        break;
                    default:
                    break;
                }
                break;
            }
            return ts.value;
        }

        public  void parseStorageClasses(Ref<Long> storage_class, IntRef link, Ref<Boolean> setAlignment, Ref<ASTBase.Expression> ealign, Ref<DArray<ASTBase.Expression>> udas) {
            long stc = 0L;
            boolean sawLinkage = false;
        L_outer11:
            for (; (1) != 0;){
                {
                    int __dispatch18 = 0;
                    dispatched_18:
                    do {
                        switch (__dispatch18 != 0 ? __dispatch18 : (this.token.value & 0xFF))
                        {
                            case 171:
                                if (((this.peek(this.token)).value & 0xFF) == 1)
                                    break;
                                stc = 4L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 182:
                                if (((this.peek(this.token)).value & 0xFF) == 1)
                                    break;
                                stc = 1048576L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 224:
                                if (((this.peek(this.token)).value & 0xFF) == 1)
                                    break;
                                stc = 536870912L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 177:
                                if (((this.peek(this.token)).value & 0xFF) == 1)
                                    break;
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
                                Token t = this.peek(this.token);
                                if ((((t).value & 0xFF) == 5 || ((t).value & 0xFF) == 7))
                                    break;
                                if (((t).value & 0xFF) == 120)
                                {
                                    t = this.peek(t);
                                    if (((((t).value & 0xFF) == 5 || ((t).value & 0xFF) == 7) || ((t).value & 0xFF) == 9))
                                        break;
                                }
                                stc = 8388608L;
                                /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                            case 225:
                                {
                                    stc = this.parseAttribute(ptr(udas));
                                    if ((stc) != 0)
                                        /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                                    continue L_outer11;
                                }
                            /*L1:*/
                            case -1:
                            __dispatch18 = 0;
                                storage_class.value = this.appendStorageClass(storage_class.value, stc);
                                this.nextToken();
                                continue L_outer11;
                            case 164:
                                if (((this.peek(this.token)).value & 0xFF) != 1)
                                {
                                    stc = 2L;
                                    /*goto L1*/{ __dispatch18 = -1; continue dispatched_18; }
                                }
                                if (sawLinkage)
                                    this.error(new BytePtr("redundant linkage declaration"));
                                sawLinkage = true;
                                Ref<DArray<Identifier>> idents = ref(null);
                                Ref<DArray<ASTBase.Expression>> identExps = ref(null);
                                IntRef cppmangle = ref(CPPMANGLE.def);
                                Ref<Boolean> cppMangleOnly = ref(false);
                                link.value = this.parseLinkage(ptr(idents), ptr(identExps), cppmangle, cppMangleOnly);
                                if ((idents.value != null || identExps.value != null))
                                {
                                    this.error(new BytePtr("C++ name spaces not allowed here"));
                                }
                                if (cppmangle.value != CPPMANGLE.def)
                                {
                                    this.error(new BytePtr("C++ mangle declaration not allowed here"));
                                }
                                continue L_outer11;
                            case 163:
                                this.nextToken();
                                setAlignment.value = true;
                                if ((this.token.value & 0xFF) == 1)
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

        public  DArray<ASTBase.Dsymbol> parseDeclarations(boolean autodecl, PrefixAttributesASTBase pAttrs, BytePtr comment) {
            Ref<Long> storage_class = ref(0L);
            byte tok = TOK.reserved;
            IntRef link = ref(this.linkage);
            Ref<Boolean> setAlignment = ref(false);
            Ref<ASTBase.Expression> ealign = ref(null);
            Ref<DArray<ASTBase.Expression>> udas = ref(null);
            if (comment == null)
                comment = pcopy(this.token.blockComment);
            if ((this.token.value & 0xFF) == 158)
            {
                Loc loc = this.token.loc.copy();
                tok = this.token.value;
                this.nextToken();
                if (((this.token.value & 0xFF) == 120 && (this.peekNext() & 0xFF) == 123))
                {
                    ASTBase.AliasThis s = new ASTBase.AliasThis(loc, this.token.ident);
                    this.nextToken();
                    this.check(TOK.this_);
                    this.check(TOK.semicolon);
                    DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                    (a).push(s);
                    this.addComment(s, comment);
                    return a;
                }
                if (((this.token.value & 0xFF) == 120 && this.hasOptionalParensThen(this.peek(this.token), TOK.assign)))
                {
                    DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                    for (; (1) != 0;){
                        Identifier ident = this.token.ident;
                        this.nextToken();
                        DArray<ASTBase.TemplateParameter> tpl = null;
                        if ((this.token.value & 0xFF) == 1)
                            tpl = this.parseTemplateParameterList(0);
                        this.check(TOK.assign);
                        Ref<Boolean> hasParsedAttributes = ref(false);
                        Function0<Void> parseAttributes = new Function0<Void>(){
                            public Void invoke(){
                                if (hasParsedAttributes.value)
                                    return null;
                                hasParsedAttributes.value = true;
                                udas.value = null;
                                storage_class.value = 0L;
                                link.value = linkage;
                                setAlignment.value = false;
                                ealign.value = null;
                                parseStorageClasses(storage_class, link, setAlignment, ealign, udas);
                                return null;
                            }
                        };
                        if ((this.token.value & 0xFF) == 225)
                            parseAttributes.invoke();
                        ASTBase.Declaration v = null;
                        ASTBase.Dsymbol s = null;
                        boolean attributesAppended = false;
                        long funcStc = this.parseTypeCtor();
                        Ref<Token> tlu = ref(this.token);
                        Ref<Token> tk = ref(null);
                        if ((((((this.token.value & 0xFF) != 161 && (this.token.value & 0xFF) != 160) && this.isBasicType(ptr(tlu))) && tlu.value != null) && ((tlu.value).value & 0xFF) == 1))
                        {
                            IntRef vargs = ref(ASTBase.VarArg.none);
                            ASTBase.Type tret = this.parseBasicType(false);
                            DArray<ASTBase.Parameter> prms = this.parseParameters(ptr(vargs), null);
                            ASTBase.ParameterList pl = new ASTBase.ParameterList(prms, vargs.value).copy();
                            parseAttributes.invoke();
                            if (udas.value != null)
                                this.error(new BytePtr("user-defined attributes not allowed for `alias` declarations"));
                            attributesAppended = true;
                            storage_class.value = this.appendStorageClass(storage_class.value, funcStc);
                            ASTBase.Type tf = new ASTBase.TypeFunction(pl, tret, link.value, storage_class.value);
                            v = new ASTBase.AliasDeclaration(loc, ident, tf);
                        }
                        else if (((((((this.token.value & 0xFF) == 161 || (this.token.value & 0xFF) == 160) || (((this.token.value & 0xFF) == 1 && this.skipAttributes(this.peekPastParen(this.token), ptr(tk))) && (((tk.value).value & 0xFF) == 228 || ((tk.value).value & 0xFF) == 5))) || (this.token.value & 0xFF) == 5) || ((this.token.value & 0xFF) == 120 && (this.peekNext() & 0xFF) == 228)) || ((((this.token.value & 0xFF) == 210 && (this.peekNext() & 0xFF) == 1) && this.skipAttributes(this.peekPastParen(this.peek(this.token)), ptr(tk))) && (((tk.value).value & 0xFF) == 228 || ((tk.value).value & 0xFF) == 5))))
                        {
                            s = this.parseFunctionLiteral();
                            if (udas.value != null)
                            {
                                if (storage_class.value != 0L)
                                    this.error(new BytePtr("Cannot put a storage-class in an alias declaration."));
                                assert(((link.value == this.linkage && !(setAlignment.value)) && ealign.value == null));
                                ASTBase.TemplateDeclaration tpl_ = (ASTBase.TemplateDeclaration)s;
                                assert((tpl_ != null && (tpl_.members).length == 1));
                                ASTBase.FuncLiteralDeclaration fd = (ASTBase.FuncLiteralDeclaration)(tpl_.members).get(0);
                                ASTBase.TypeFunction tf = (ASTBase.TypeFunction)fd.type;
                                assert((tf.parameterList.parameters).length > 0);
                                DArray<ASTBase.Dsymbol> as = new DArray<ASTBase.Dsymbol>();
                                (tf.parameterList.parameters).get(0).userAttribDecl = new ASTBase.UserAttributeDeclaration(udas.value, as);
                            }
                            v = new ASTBase.AliasDeclaration(loc, ident, s);
                        }
                        else
                        {
                            parseAttributes.invoke();
                            if (udas.value != null)
                                this.error(new BytePtr("user-defined attributes not allowed for `%s` declarations"), Token.toChars(tok));
                            ASTBase.Type t = this.parseType(null, null);
                            v = new ASTBase.AliasDeclaration(loc, ident, t);
                        }
                        if (!(attributesAppended))
                            storage_class.value = this.appendStorageClass(storage_class.value, funcStc);
                        v.storage_class = storage_class.value;
                        s = v;
                        if (tpl != null)
                        {
                            DArray<ASTBase.Dsymbol> a2 = new DArray<ASTBase.Dsymbol>();
                            (a2).push(s);
                            ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, ident, tpl, null, a2, false, false);
                            s = tempdecl;
                        }
                        if (link.value != this.linkage)
                        {
                            DArray<ASTBase.Dsymbol> a2 = new DArray<ASTBase.Dsymbol>();
                            (a2).push(s);
                            s = new ASTBase.LinkDeclaration(link.value, a2);
                        }
                        (a).push(s);
                        switch ((this.token.value & 0xFF))
                        {
                            case 9:
                                this.nextToken();
                                this.addComment(s, comment);
                                break;
                            case 99:
                                this.nextToken();
                                this.addComment(s, comment);
                                if ((this.token.value & 0xFF) != 120)
                                {
                                    this.error(new BytePtr("identifier expected following comma, not `%s`"), this.token.toChars());
                                    break;
                                }
                                if (((this.peekNext() & 0xFF) != 90 && (this.peekNext() & 0xFF) != 1))
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
            if (!(autodecl))
            {
                this.parseStorageClasses(storage_class, link, setAlignment, ealign, udas);
                if ((this.token.value & 0xFF) == 156)
                {
                    ASTBase.Dsymbol d = this.parseEnum();
                    DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                    (a).push(d);
                    if (udas.value != null)
                    {
                        d = new ASTBase.UserAttributeDeclaration(udas.value, a);
                        a = new DArray<ASTBase.Dsymbol>();
                        (a).push(d);
                    }
                    this.addComment(d, comment);
                    return a;
                }
                if (((((this.token.value & 0xFF) == 152 || (this.token.value & 0xFF) == 155) || (this.token.value & 0xFF) == 153) || (this.token.value & 0xFF) == 154))
                {
                    ASTBase.Dsymbol s = this.parseAggregate();
                    DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
                    (a).push(s);
                    if ((storage_class.value) != 0)
                    {
                        s = new ASTBase.StorageClassDeclaration(storage_class.value, a);
                        a = new DArray<ASTBase.Dsymbol>();
                        (a).push(s);
                    }
                    if (setAlignment.value)
                    {
                        s = new ASTBase.AlignDeclaration(s.loc, ealign.value, a);
                        a = new DArray<ASTBase.Dsymbol>();
                        (a).push(s);
                    }
                    if (link.value != this.linkage)
                    {
                        s = new ASTBase.LinkDeclaration(link.value, a);
                        a = new DArray<ASTBase.Dsymbol>();
                        (a).push(s);
                    }
                    if (udas.value != null)
                    {
                        s = new ASTBase.UserAttributeDeclaration(udas.value, a);
                        a = new DArray<ASTBase.Dsymbol>();
                        (a).push(s);
                    }
                    this.addComment(s, comment);
                    return a;
                }
                if (((((storage_class.value) != 0 || udas.value != null) && (this.token.value & 0xFF) == 120) && this.hasOptionalParensThen(this.peek(this.token), TOK.assign)))
                {
                    DArray<ASTBase.Dsymbol> a = this.parseAutoDeclarations(storage_class.value, comment);
                    if (udas.value != null)
                    {
                        ASTBase.Dsymbol s = new ASTBase.UserAttributeDeclaration(udas.value, a);
                        a = new DArray<ASTBase.Dsymbol>();
                        (a).push(s);
                    }
                    return a;
                }
                {
                    Ref<Token> tk = ref(null);
                    if (((((((storage_class.value) != 0 || udas.value != null) && (this.token.value & 0xFF) == 120) && this.skipParens(this.peek(this.token), ptr(tk))) && this.skipAttributes(tk.value, ptr(tk))) && (((((((tk.value).value & 0xFF) == 1 || ((tk.value).value & 0xFF) == 5) || ((tk.value).value & 0xFF) == 175) || ((tk.value).value & 0xFF) == 176) || ((tk.value).value & 0xFF) == 187) || (((tk.value).value & 0xFF) == 120 && pequals((tk.value).ident, Id._body)))))
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
                storage_class.value |= (pAttrs).storageClass;
            }
            ASTBase.Type tfirst = null;
            DArray<ASTBase.Dsymbol> a = new DArray<ASTBase.Dsymbol>();
            for (; (1) != 0;){
                Ref<DArray<ASTBase.TemplateParameter>> tpl = ref(null);
                IntRef disable = ref(0);
                IntRef alt = ref(0);
                Loc loc = this.token.loc.copy();
                Ref<Identifier> ident = ref(null);
                ASTBase.Type t = this.parseDeclarator(ts, ptr(alt), ptr(ident), ptr(tpl), storage_class.value, ptr(disable), ptr(udas));
                assert(t != null);
                if (!(tfirst != null))
                    tfirst = t;
                else if (!pequals(t, tfirst))
                    this.error(new BytePtr("multiple declarations must have the same type, not `%s` and `%s`"), tfirst.toChars(), t.toChars());
                boolean isThis = (((t.ty & 0xFF) == ASTBase.ENUMTY.Tident && pequals(((ASTBase.TypeIdentifier)t).ident, Id.This)) && (this.token.value & 0xFF) == 90);
                if (ident.value != null)
                    this.checkCstyleTypeSyntax(loc, t, alt.value, ident.value);
                else if ((!(isThis) && !pequals(t, ASTBase.Type.terror)))
                    this.error(new BytePtr("no identifier for declarator `%s`"), t.toChars());
                if ((tok & 0xFF) == 158)
                {
                    ASTBase.Declaration v = null;
                    ASTBase.Initializer _init = null;
                    if (udas.value != null)
                        this.error(new BytePtr("user-defined attributes not allowed for `%s` declarations"), Token.toChars(tok));
                    if ((this.token.value & 0xFF) == 90)
                    {
                        this.nextToken();
                        _init = this.parseInitializer();
                    }
                    if (_init != null)
                    {
                        if (isThis)
                            this.error(new BytePtr("cannot use syntax `alias this = %s`, use `alias %s this` instead"), _init.toChars(), _init.toChars());
                        else
                            this.error(new BytePtr("alias cannot have initializer"));
                    }
                    v = new ASTBase.AliasDeclaration(loc, ident.value, t);
                    v.storage_class = storage_class.value;
                    if (pAttrs != null)
                    {
                        (pAttrs).storageClass &= 60129542144L;
                    }
                    ASTBase.Dsymbol s = v;
                    if (link.value != this.linkage)
                    {
                        DArray<ASTBase.Dsymbol> ax = new DArray<ASTBase.Dsymbol>();
                        (ax).push(v);
                        s = new ASTBase.LinkDeclaration(link.value, ax);
                    }
                    (a).push(s);
                    switch ((this.token.value & 0xFF))
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
                else if ((t.ty & 0xFF) == ASTBase.ENUMTY.Tfunction)
                {
                    ASTBase.Expression constraint = null;
                    ASTBase.FuncDeclaration f = new ASTBase.FuncDeclaration(loc, Loc.initial, ident.value, storage_class.value | ((disable.value) != 0 ? 137438953472L : 0L), t);
                    if (pAttrs != null)
                        (pAttrs).storageClass = 0L;
                    if (tpl.value != null)
                        constraint = this.parseConstraint();
                    ASTBase.Dsymbol s = this.parseContracts(f);
                    Identifier tplIdent = s.ident;
                    if (link.value != this.linkage)
                    {
                        DArray<ASTBase.Dsymbol> ax = new DArray<ASTBase.Dsymbol>();
                        (ax).push(s);
                        s = new ASTBase.LinkDeclaration(link.value, ax);
                    }
                    if (udas.value != null)
                    {
                        DArray<ASTBase.Dsymbol> ax = new DArray<ASTBase.Dsymbol>();
                        (ax).push(s);
                        s = new ASTBase.UserAttributeDeclaration(udas.value, ax);
                    }
                    if (tpl.value != null)
                    {
                        DArray<ASTBase.Dsymbol> decldefs = new DArray<ASTBase.Dsymbol>();
                        (decldefs).push(s);
                        ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, tplIdent, tpl.value, constraint, decldefs, false, false);
                        s = tempdecl;
                        if ((storage_class.value & 1L) != 0)
                        {
                            assert((f.storage_class & 1L) != 0);
                            f.storage_class &= -2L;
                            DArray<ASTBase.Dsymbol> ax = new DArray<ASTBase.Dsymbol>();
                            (ax).push(s);
                            s = new ASTBase.StorageClassDeclaration(1L, ax);
                        }
                    }
                    (a).push(s);
                    this.addComment(s, comment);
                }
                else if (ident.value != null)
                {
                    ASTBase.Initializer _init = null;
                    if ((this.token.value & 0xFF) == 90)
                    {
                        this.nextToken();
                        _init = this.parseInitializer();
                    }
                    ASTBase.VarDeclaration v = new ASTBase.VarDeclaration(loc, t, ident.value, _init, 0L);
                    v.storage_class = storage_class.value;
                    if (pAttrs != null)
                        (pAttrs).storageClass = 0L;
                    ASTBase.Dsymbol s = v;
                    if ((tpl.value != null && _init != null))
                    {
                        DArray<ASTBase.Dsymbol> a2 = new DArray<ASTBase.Dsymbol>();
                        (a2).push(s);
                        ASTBase.TemplateDeclaration tempdecl = new ASTBase.TemplateDeclaration(loc, ident.value, tpl.value, null, a2, false, false);
                        s = tempdecl;
                    }
                    if (setAlignment.value)
                    {
                        DArray<ASTBase.Dsymbol> ax = new DArray<ASTBase.Dsymbol>();
                        (ax).push(s);
                        s = new ASTBase.AlignDeclaration(v.loc, ealign.value, ax);
                    }
                    if (link.value != this.linkage)
                    {
                        DArray<ASTBase.Dsymbol> ax = new DArray<ASTBase.Dsymbol>();
                        (ax).push(s);
                        s = new ASTBase.LinkDeclaration(link.value, ax);
                    }
                    if (udas.value != null)
                    {
                        DArray<ASTBase.Dsymbol> ax = new DArray<ASTBase.Dsymbol>();
                        (ax).push(s);
                        s = new ASTBase.UserAttributeDeclaration(udas.value, ax);
                    }
                    (a).push(s);
                    switch ((this.token.value & 0xFF))
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
                        this.error(new BytePtr("semicolon expected, not `%s`"), this.token.toChars());
                        break;
                    }
                }
                break;
            }
            return a;
        }

        public  ASTBase.Dsymbol parseFunctionLiteral() {
            Loc loc = this.token.loc.copy();
            Ref<DArray<ASTBase.TemplateParameter>> tpl = ref(null);
            DArray<ASTBase.Parameter> parameters = null;
            IntRef varargs = ref(ASTBase.VarArg.none);
            ASTBase.Type tret = null;
            long stc = 0L;
            byte save = TOK.reserved;
            {
                int __dispatch22 = 0;
                dispatched_22:
                do {
                    switch (__dispatch22 != 0 ? __dispatch22 : (this.token.value & 0xFF))
                    {
                        case 161:
                        case 160:
                            save = this.token.value;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 210)
                            {
                                stc = 2097152L;
                                this.nextToken();
                            }
                            if (((this.token.value & 0xFF) != 1 && (this.token.value & 0xFF) != 5))
                            {
                                tret = this.parseBasicType(false);
                                tret = this.parseBasicType2(tret);
                            }
                            if ((this.token.value & 0xFF) == 1)
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
                            parameters = this.parseParameters(ptr(varargs), ptr(tpl));
                            stc = this.parsePostfix(stc, null);
                            {
                                long modStc = stc & 2685403140L;
                                if ((modStc) != 0)
                                {
                                    if ((save & 0xFF) == 161)
                                    {
                                        OutBuffer buf = new OutBuffer();
                                        try {
                                            ASTBase.stcToBuffer(buf, modStc);
                                            this.error(new BytePtr("function literal cannot be `%s`"), buf.peekChars());
                                        }
                                        finally {
                                        }
                                    }
                                    else
                                        save = TOK.delegate_;
                                }
                            }
                            break;
                        case 5:
                            break;
                        case 120:
                            parameters = new DArray<ASTBase.Parameter>();
                            Identifier id = Identifier.generateId(new BytePtr("__T"));
                            ASTBase.Type t = new ASTBase.TypeIdentifier(loc, id);
                            (parameters).push(new ASTBase.Parameter(0L, t, this.token.ident, null, null));
                            tpl.value = new DArray<ASTBase.TemplateParameter>();
                            ASTBase.TemplateParameter tp = new ASTBase.TemplateTypeParameter(loc, id, null, null);
                            (tpl.value).push(tp);
                            this.nextToken();
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                } while(__dispatch22 != 0);
            }
            ASTBase.TypeFunction tf = new ASTBase.TypeFunction(new ASTBase.ParameterList(parameters, varargs.value), tret, this.linkage, stc);
            tf = (ASTBase.TypeFunction)tf.addSTC(stc);
            ASTBase.FuncLiteralDeclaration fd = new ASTBase.FuncLiteralDeclaration(loc, Loc.initial, tf, save, null, null);
            if ((this.token.value & 0xFF) == 228)
            {
                this.check(TOK.goesTo);
                Loc returnloc = this.token.loc.copy();
                ASTBase.Expression ae = this.parseAssignExp();
                fd.fbody = new ASTBase.ReturnStatement(returnloc, ae);
                fd.endloc = this.token.loc.copy();
            }
            else
            {
                this.parseContracts(fd);
            }
            if (tpl.value != null)
            {
                DArray<ASTBase.Dsymbol> decldefs = new DArray<ASTBase.Dsymbol>();
                (decldefs).push(fd);
                return new ASTBase.TemplateDeclaration(fd.loc, fd.ident, tpl.value, null, decldefs, false, true);
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
                    switch (__dispatch23 != 0 ? __dispatch23 : (this.token.value & 0xFF))
                    {
                        case 5:
                            if (requireDo)
                                this.error(new BytePtr("missing `do { ... }` after `in` or `out`"));
                            f.fbody = this.parseStatement(1, null, null);
                            f.endloc = this.endloc.copy();
                            break;
                        case 120:
                            if (pequals(this.token.ident, Id._body))
                                /*goto case*/{ __dispatch23 = 187; continue dispatched_23; }
                            /*goto default*/ { __dispatch23 = -2; continue dispatched_23; }
                        case 187:
                            this.nextToken();
                            f.fbody = this.parseStatement(4, null, null);
                            f.endloc = this.endloc.copy();
                            break;
                        case 175:
                            Loc loc = this.token.loc.copy();
                            this.nextToken();
                            if (f.frequires == null)
                            {
                                f.frequires = new DArray<ASTBase.Statement>();
                            }
                            if ((this.token.value & 0xFF) == 1)
                            {
                                this.nextToken();
                                ASTBase.Expression e = this.parseAssignExp();
                                ASTBase.Expression msg = null;
                                if ((this.token.value & 0xFF) == 99)
                                {
                                    this.nextToken();
                                    if ((this.token.value & 0xFF) != 2)
                                    {
                                        msg = this.parseAssignExp();
                                        if ((this.token.value & 0xFF) == 99)
                                            this.nextToken();
                                    }
                                }
                                this.check(TOK.rightParentheses);
                                e = new ASTBase.AssertExp(loc, e, msg);
                                (f.frequires).push(new ASTBase.ExpStatement(loc, e));
                                requireDo = false;
                            }
                            else
                            {
                                (f.frequires).push(this.parseStatement(6, null, null));
                                requireDo = true;
                            }
                            /*goto L1*/continue L1;
                        case 176:
                            Loc loc_1 = this.token.loc.copy();
                            this.nextToken();
                            if (f.fensures == null)
                            {
                                f.fensures = new DArray<ASTBase.Ensure>();
                            }
                            Identifier id = null;
                            if ((this.token.value & 0xFF) != 5)
                            {
                                this.check(TOK.leftParentheses);
                                if (((this.token.value & 0xFF) != 120 && (this.token.value & 0xFF) != 9))
                                    this.error(new BytePtr("`(identifier) { ... }` or `(identifier; expression)` following `out` expected, not `%s`"), this.token.toChars());
                                if ((this.token.value & 0xFF) != 9)
                                {
                                    id = this.token.ident;
                                    this.nextToken();
                                }
                                if ((this.token.value & 0xFF) == 9)
                                {
                                    this.nextToken();
                                    ASTBase.Expression e_1 = this.parseAssignExp();
                                    ASTBase.Expression msg_1 = null;
                                    if ((this.token.value & 0xFF) == 99)
                                    {
                                        this.nextToken();
                                        if ((this.token.value & 0xFF) != 2)
                                        {
                                            msg_1 = this.parseAssignExp();
                                            if ((this.token.value & 0xFF) == 99)
                                                this.nextToken();
                                        }
                                    }
                                    this.check(TOK.rightParentheses);
                                    e_1 = new ASTBase.AssertExp(loc_1, e_1, msg_1);
                                    (f.fensures).push(new ASTBase.Ensure(id, new ASTBase.ExpStatement(loc_1, e_1)));
                                    requireDo = false;
                                    /*goto L1*/continue L1;
                                }
                                this.check(TOK.rightParentheses);
                            }
                            (f.fensures).push(new ASTBase.Ensure(id, this.parseStatement(6, null, null)));
                            requireDo = true;
                            /*goto L1*/continue L1;
                        case 9:
                            if (!(literal))
                            {
                                if (!(requireDo))
                                    this.nextToken();
                                break;
                            }
                            /*goto default*/ { __dispatch23 = -2; continue dispatched_23; }
                        default:
                        if (literal)
                        {
                            BytePtr sbody = pcopy(requireDo ? new BytePtr("do ") : new BytePtr(""));
                            this.error(new BytePtr("missing `%s{ ... }` for function literal"), sbody);
                        }
                        else if (!(requireDo))
                        {
                            byte t = this.token.value;
                            if ((((((((t & 0xFF) == 171 || (t & 0xFF) == 182) || (t & 0xFF) == 177) || (t & 0xFF) == 195) || (t & 0xFF) == 224) || (t & 0xFF) == 216) || (t & 0xFF) == 215))
                                this.error(new BytePtr("'%s' cannot be placed after a template constraint"), this.token.toChars());
                            else if ((t & 0xFF) == 225)
                                this.error(new BytePtr("attributes cannot be placed after a template constraint"));
                            else if ((t & 0xFF) == 183)
                                this.error(new BytePtr("cannot use function constraints for non-template functions. Use `static if` instead"));
                            else
                                this.error(new BytePtr("semicolon expected following function declaration"));
                        }
                        break;
                    }
                } while(__dispatch23 != 0);
                break;
            }
            if ((literal && !(f.fbody != null)))
            {
                f.fbody = new ASTBase.CompoundStatement(Loc.initial, slice(new ASTBase.Statement[]{null}));
            }
            this.linkage = linksave;
            return f;
        }

        public  void checkDanglingElse(Loc elseloc) {
            if (((((this.token.value & 0xFF) != 184 && (this.token.value & 0xFF) != 198) && (this.token.value & 0xFF) != 199) && this.lookingForElse.linnum != 0))
            {
                this.warning(elseloc, new BytePtr("else is dangling, add { } after condition at %s"), this.lookingForElse.toChars(global.params.showColumns));
            }
        }

        public  void checkCstyleTypeSyntax(Loc loc, ASTBase.Type t, int alt, Identifier ident) {
            if (!((alt) != 0))
                return ;
            BytePtr sp = pcopy(!(ident != null) ? new BytePtr("") : new BytePtr(" "));
            BytePtr s = pcopy(!(ident != null) ? new BytePtr("") : ident.toChars());
            this.error(loc, new BytePtr("instead of C-style syntax, use D-style `%s%s%s`"), t.toChars(), sp, s);
        }

        // from template ParseForeachArgs!(11)
        // from template Seq!(Ptr<ASTBase.Dsymbol>)


        // from template ParseForeachArgs!(11)


        // from template ParseForeachArgs!(00)
        // from template Seq!()


        // from template ParseForeachArgs!(00)

        // from template ParseForeachArgs!(10)
        // from template Seq!()


        // from template ParseForeachArgs!(10)

        // from template ParseForeachRet!(11)


        // from template ParseForeachRet!(00)


        // from template ParseForeachRet!(10)

        // from template parseForeach!(11)
        public  ASTBase.StaticForeachDeclaration parseForeach11(Loc loc, Ptr<ASTBase.Dsymbol> _param_1) {
            this.nextToken();
            Ptr<ASTBase.Dsymbol> pLastDecl = pcopy(_param_1);
            byte op = this.token.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            DArray<ASTBase.Parameter> parameters = new DArray<ASTBase.Parameter>();
        L_outer12:
            for (; (1) != 0;){
                Ref<Identifier> ai = ref(null);
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if ((stc) != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch24 = 0;
                        dispatched_24:
                        do {
                            switch (__dispatch24 != 0 ? __dispatch24 : (this.token.value & 0xFF))
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
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if ((this.peekNext() & 0xFF) != 1)
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
                        if ((this.token.value & 0xFF) == 120)
                        {
                            Token t = this.peek(this.token);
                            if ((((t).value & 0xFF) == 99 || ((t).value & 0xFF) == 9))
                            {
                                ai.value = this.token.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (!(ai.value != null))
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    ASTBase.Parameter p = new ASTBase.Parameter(storageClass, at, ai.value, null, null);
                    (parameters).push(p);
                    if ((this.token.value & 0xFF) == 99)
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
            if (((this.token.value & 0xFF) == 31 && (parameters).length == 1))
            {
                ASTBase.Parameter p = (parameters).get(0);
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


        // from template parseForeach!(00)
        public  ASTBase.Statement parseForeach00(Loc loc) {
            byte op = this.token.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            DArray<ASTBase.Parameter> parameters = new DArray<ASTBase.Parameter>();
        L_outer13:
            for (; (1) != 0;){
                Ref<Identifier> ai = ref(null);
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if ((stc) != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch25 = 0;
                        dispatched_25:
                        do {
                            switch (__dispatch25 != 0 ? __dispatch25 : (this.token.value & 0xFF))
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
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if ((this.peekNext() & 0xFF) != 1)
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
                        if ((this.token.value & 0xFF) == 120)
                        {
                            Token t = this.peek(this.token);
                            if ((((t).value & 0xFF) == 99 || ((t).value & 0xFF) == 9))
                            {
                                ai.value = this.token.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (!(ai.value != null))
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    ASTBase.Parameter p = new ASTBase.Parameter(storageClass, at, ai.value, null, null);
                    (parameters).push(p);
                    if ((this.token.value & 0xFF) == 99)
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
            if (((this.token.value & 0xFF) == 31 && (parameters).length == 1))
            {
                ASTBase.Parameter p = (parameters).get(0);
                this.nextToken();
                ASTBase.Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, endloc);
                ASTBase.ForeachRangeStatement rangefe = new ASTBase.ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return rangefe;
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, endloc);
                ASTBase.ForeachStatement aggrfe = new ASTBase.ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return aggrfe;
            }
        }


        // from template parseForeach!(10)
        public  ASTBase.StaticForeachStatement parseForeach10(Loc loc) {
            this.nextToken();
            byte op = this.token.value;
            this.nextToken();
            this.check(TOK.leftParentheses);
            DArray<ASTBase.Parameter> parameters = new DArray<ASTBase.Parameter>();
        L_outer14:
            for (; (1) != 0;){
                Ref<Identifier> ai = ref(null);
                ASTBase.Type at = null;
                long storageClass = 0L;
                long stc = 0L;
                while(true) try {
                /*Lagain:*/
                    if ((stc) != 0)
                    {
                        storageClass = this.appendStorageClass(storageClass, stc);
                        this.nextToken();
                    }
                    {
                        int __dispatch26 = 0;
                        dispatched_26:
                        do {
                            switch (__dispatch26 != 0 ? __dispatch26 : (this.token.value & 0xFF))
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
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 4L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 182:
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 1048576L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 224:
                                    if ((this.peekNext() & 0xFF) != 1)
                                    {
                                        stc = 536870912L;
                                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 177:
                                    if ((this.peekNext() & 0xFF) != 1)
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
                        if ((this.token.value & 0xFF) == 120)
                        {
                            Token t = this.peek(this.token);
                            if ((((t).value & 0xFF) == 99 || ((t).value & 0xFF) == 9))
                            {
                                ai.value = this.token.ident;
                                at = null;
                                this.nextToken();
                                /*goto Larg*/throw Dispatch1.INSTANCE;
                            }
                        }
                        at = this.parseType(ptr(ai), null);
                        if (!(ai.value != null))
                            this.error(new BytePtr("no identifier for declarator `%s`"), at.toChars());
                    }
                    catch(Dispatch1 __d){}
                /*Larg:*/
                    ASTBase.Parameter p = new ASTBase.Parameter(storageClass, at, ai.value, null, null);
                    (parameters).push(p);
                    if ((this.token.value & 0xFF) == 99)
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
            if (((this.token.value & 0xFF) == 31 && (parameters).length == 1))
            {
                ASTBase.Parameter p = (parameters).get(0);
                this.nextToken();
                ASTBase.Expression upr = this.parseExpression();
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, endloc);
                ASTBase.ForeachRangeStatement rangefe = new ASTBase.ForeachRangeStatement(loc, op, p, aggr, upr, _body, endloc);
                return new ASTBase.StaticForeachStatement(loc, new ASTBase.StaticForeach(loc, null, rangefe));
            }
            else
            {
                this.check(TOK.rightParentheses);
                Loc endloc = new Loc();
                ASTBase.Statement _body = this.parseStatement(0, null, endloc);
                ASTBase.ForeachStatement aggrfe = new ASTBase.ForeachStatement(loc, op, parameters, aggr, _body, endloc);
                return new ASTBase.StaticForeachStatement(loc, new ASTBase.StaticForeach(loc, aggrfe, null));
            }
        }


        public  ASTBase.Statement parseStatement(int flags, Ptr<BytePtr> endPtr, Loc pEndloc) {
            ASTBase.Statement s = null;
            ASTBase.Condition cond = null;
            ASTBase.Statement ifbody = null;
            ASTBase.Statement elsebody = null;
            boolean isfinal = false;
            long stc = 0;
            long storageClass = 0;
            ASTBase.Parameter param = null;
            Loc loc = this.token.loc.copy();
            if (((flags & ParseStatementFlags.curly) != 0 && (this.token.value & 0xFF) != 5))
                this.error(new BytePtr("statement expected to be `{ }`, not `%s`"), this.token.toChars());
            {
                int __dispatch27 = 0;
                dispatched_27:
                do {
                    switch (__dispatch27 != 0 ? __dispatch27 : (this.token.value & 0xFF))
                    {
                        case 120:
                            Token t = this.peek(this.token);
                            if (((t).value & 0xFF) == 7)
                            {
                                Token nt = this.peek(t);
                                if (((nt).value & 0xFF) == 7)
                                {
                                    this.nextToken();
                                    this.nextToken();
                                    this.nextToken();
                                    this.error(new BytePtr("use `.` for member lookup, not `::`"));
                                    break;
                                }
                                Identifier ident = this.token.ident;
                                this.nextToken();
                                this.nextToken();
                                if ((this.token.value & 0xFF) == 6)
                                    s = null;
                                else if ((this.token.value & 0xFF) == 5)
                                    s = this.parseStatement(6, null, null);
                                else
                                    s = this.parseStatement(16, null, null);
                                s = new ASTBase.LabelStatement(loc, ident, s);
                                break;
                            }
                            /*goto case*/{ __dispatch27 = 97; continue dispatched_27; }
                        case 97:
                        case 39:
                        case 229:
                        case 213:
                            if (this.isDeclaration(this.token, NeedDeclaratorId.mustIfDstyle, TOK.reserved, null))
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
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
                            Token t_1 = this.peek(this.token);
                            if (((t_1).value & 0xFF) == 14)
                            {
                                s = new ASTBase.StaticAssertStatement(this.parseStaticAssert());
                                break;
                            }
                            if (((t_1).value & 0xFF) == 183)
                            {
                                cond = this.parseStaticIfCondition();
                                /*goto Lcondition*/{ __dispatch27 = -3; continue dispatched_27; }
                            }
                            if ((((t_1).value & 0xFF) == 201 || ((t_1).value & 0xFF) == 202))
                            {
                                s = this.parseForeach10(loc);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                                break;
                            }
                            if (((t_1).value & 0xFF) == 157)
                            {
                                DArray<ASTBase.Dsymbol> imports = this.parseImport();
                                s = new ASTBase.ImportStatement(loc, imports);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                                break;
                            }
                            /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                        case 170:
                            if ((this.peekNext() & 0xFF) == 188)
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
                            if ((this.peekNext() & 0xFF) == 97)
                                /*goto Lexp*/{ __dispatch27 = -2; continue dispatched_27; }
                            if ((this.peekNext() & 0xFF) == 1)
                                /*goto Lexp*/{ __dispatch27 = -2; continue dispatched_27; }
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
                                DArray<ASTBase.Dsymbol> a = this.parseDeclarations(false, null, null);
                                if ((a).length > 1)
                                {
                                    DArray<ASTBase.Statement> as = new DArray<ASTBase.Statement>();
                                    (as).reserve((a).length);
                                    {
                                        int __key427 = 0;
                                        int __limit428 = (a).length;
                                        for (; __key427 < __limit428;__key427 += 1) {
                                            int i = __key427;
                                            ASTBase.Dsymbol d = (a).get(i);
                                            s = new ASTBase.ExpStatement(loc, d);
                                            (as).push(s);
                                        }
                                    }
                                    s = new ASTBase.CompoundDeclarationStatement(loc, as);
                                }
                                else if ((a).length == 1)
                                {
                                    ASTBase.Dsymbol d_1 = (a).get(0);
                                    s = new ASTBase.ExpStatement(loc, d_1);
                                }
                                else
                                    s = new ASTBase.ExpStatement(loc, (ASTBase.Dsymbol)null);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                                break;
                            }
                        case 156:
                            ASTBase.Dsymbol d_2 = null;
                            Token t_2 = this.peek(this.token);
                            if ((((t_2).value & 0xFF) == 5 || ((t_2).value & 0xFF) == 7))
                                d_2 = this.parseEnum();
                            else if (((t_2).value & 0xFF) != 120)
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            else
                            {
                                t_2 = this.peek(t_2);
                                if (((((t_2).value & 0xFF) == 5 || ((t_2).value & 0xFF) == 7) || ((t_2).value & 0xFF) == 9))
                                    d_2 = this.parseEnum();
                                else
                                    /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            }
                            s = new ASTBase.ExpStatement(loc, d_2);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                                s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                            break;
                        case 162:
                            Token t_3 = this.peek(this.token);
                            if (((t_3).value & 0xFF) == 1)
                            {
                                ASTBase.Expression e = this.parseAssignExp();
                                this.check(TOK.semicolon);
                                if ((e.op & 0xFF) == 162)
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
                                s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                            break;
                        case 5:
                            Loc lookingForElseSave = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.copy();
                            this.nextToken();
                            DArray<ASTBase.Statement> statements = new DArray<ASTBase.Statement>();
                            for (; ((this.token.value & 0xFF) != 6 && (this.token.value & 0xFF) != 11);){
                                (statements).push(this.parseStatement(9, null, null));
                            }
                            if (endPtr != null)
                                endPtr.set(0, this.token.ptr);
                            this.endloc = this.token.loc.copy();
                            if (pEndloc != null)
                            {
                                pEndloc.opAssign(this.token.loc);
                                pEndloc = null;
                            }
                            s = new ASTBase.CompoundStatement(loc, statements);
                            if ((flags & 10) != 0)
                                s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                            this.check(TOK.rightCurly, new BytePtr("compound statement"));
                            this.lookingForElse = lookingForElseSave.copy();
                            break;
                        case 185:
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            ASTBase.Expression condition = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            Loc endloc = new Loc();
                            ASTBase.Statement _body = this.parseStatement(2, null, endloc);
                            s = new ASTBase.WhileStatement(loc, condition, _body, endloc);
                            break;
                        case 9:
                            if (!((flags & ParseStatementFlags.semiOk) != 0))
                            {
                                if ((flags & ParseStatementFlags.semi) != 0)
                                    this.deprecation(new BytePtr("use `{ }` for an empty statement, not `;`"));
                                else
                                    this.error(new BytePtr("use `{ }` for an empty statement, not `;`"));
                            }
                            this.nextToken();
                            s = new ASTBase.ExpStatement(loc, (ASTBase.Expression)null);
                            break;
                        case 187:
                            ASTBase.Statement _body_1 = null;
                            ASTBase.Expression condition_1 = null;
                            this.nextToken();
                            Loc lookingForElseSave_1 = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.copy();
                            _body_1 = this.parseStatement(2, null, null);
                            this.lookingForElse = lookingForElseSave_1.copy();
                            this.check(TOK.while_);
                            this.check(TOK.leftParentheses);
                            condition_1 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            if ((this.token.value & 0xFF) == 9)
                                this.nextToken();
                            else
                                this.error(new BytePtr("terminating `;` required after do-while statement"));
                            s = new ASTBase.DoStatement(loc, _body_1, condition_1, this.token.loc);
                            break;
                        case 186:
                            ASTBase.Statement _init = null;
                            ASTBase.Expression condition_2 = null;
                            ASTBase.Expression increment = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if ((this.token.value & 0xFF) == 9)
                            {
                                _init = null;
                                this.nextToken();
                            }
                            else
                            {
                                Loc lookingForElseSave_2 = this.lookingForElse.copy();
                                this.lookingForElse = Loc.initial.copy();
                                _init = this.parseStatement(0, null, null);
                                this.lookingForElse = lookingForElseSave_2.copy();
                            }
                            if ((this.token.value & 0xFF) == 9)
                            {
                                condition_2 = null;
                                this.nextToken();
                            }
                            else
                            {
                                condition_2 = this.parseExpression();
                                this.check(TOK.semicolon, new BytePtr("`for` condition"));
                            }
                            if ((this.token.value & 0xFF) == 2)
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
                            ASTBase.Statement _body_2 = this.parseStatement(2, null, endloc_1);
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
                            if ((stc) != 0)
                            {
                                storageClass = this.appendStorageClass(storageClass, stc);
                                this.nextToken();
                            }
                            {
                                int __dispatch28 = 0;
                                dispatched_28:
                                do {
                                    switch (__dispatch28 != 0 ? __dispatch28 : (this.token.value & 0xFF))
                                    {
                                        case 210:
                                            stc = 2097152L;
                                            /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                        case 179:
                                            stc = 256L;
                                            /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                        case 171:
                                            if ((this.peekNext() & 0xFF) != 1)
                                            {
                                                stc = 4L;
                                                /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                            }
                                            break;
                                        case 182:
                                            if ((this.peekNext() & 0xFF) != 1)
                                            {
                                                stc = 1048576L;
                                                /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                            }
                                            break;
                                        case 224:
                                            if ((this.peekNext() & 0xFF) != 1)
                                            {
                                                stc = 536870912L;
                                                /*goto LagainStc*/{ __dispatch27 = -5; continue dispatched_27; }
                                            }
                                            break;
                                        case 177:
                                            if ((this.peekNext() & 0xFF) != 1)
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
                            Token n = this.peek(this.token);
                            if ((((storageClass != 0L && (this.token.value & 0xFF) == 120) && ((n).value & 0xFF) != 90) && ((n).value & 0xFF) != 120))
                            {
                                this.error(new BytePtr("found `%s` while expecting `=` or identifier"), (n).toChars());
                            }
                            else if (((storageClass != 0L && (this.token.value & 0xFF) == 120) && ((n).value & 0xFF) == 90))
                            {
                                Identifier ai = this.token.ident;
                                ASTBase.Type at = null;
                                this.nextToken();
                                this.check(TOK.assign);
                                param = new ASTBase.Parameter(storageClass, at, ai, null, null);
                            }
                            else if (this.isDeclaration(this.token, NeedDeclaratorId.must, TOK.assign, null))
                            {
                                Ref<Identifier> ai_1 = ref(null);
                                ASTBase.Type at_1 = this.parseType(ptr(ai_1), null);
                                this.check(TOK.assign);
                                param = new ASTBase.Parameter(storageClass, at_1, ai_1.value, null, null);
                            }
                            condition_3 = this.parseExpression();
                            this.check(TOK.rightParentheses);
                            {
                                Loc lookingForElseSave_3 = this.lookingForElse.copy();
                                this.lookingForElse = loc.copy();
                                ifbody = this.parseStatement(2, null, null);
                                this.lookingForElse = lookingForElseSave_3.copy();
                            }
                            if ((this.token.value & 0xFF) == 184)
                            {
                                Loc elseloc = this.token.loc.copy();
                                this.nextToken();
                                elsebody = this.parseStatement(2, null, null);
                                this.checkDanglingElse(elseloc);
                            }
                            else
                                elsebody = null;
                            if ((condition_3 != null && ifbody != null))
                                s = new ASTBase.IfStatement(loc, param, condition_3, ifbody, elsebody, this.token.loc);
                            else
                                s = null;
                            break;
                        case 184:
                            this.error(new BytePtr("found `else` without a corresponding `if`, `version` or `debug` statement"));
                            /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                        case 203:
                            if (((this.peek(this.token)).value & 0xFF) != 1)
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if ((this.token.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("scope identifier expected"));
                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                            }
                            else
                            {
                                byte t_4 = TOK.onScopeExit;
                                Identifier id = this.token.ident;
                                if (pequals(id, Id.exit))
                                    t_4 = TOK.onScopeExit;
                                else if (pequals(id, Id.failure))
                                    t_4 = TOK.onScopeFailure;
                                else if (pequals(id, Id.success))
                                    t_4 = TOK.onScopeSuccess;
                                else
                                    this.error(new BytePtr("valid scope identifiers are `exit`, `failure`, or `success`, not `%s`"), id.toChars());
                                this.nextToken();
                                this.check(TOK.rightParentheses);
                                ASTBase.Statement st = this.parseStatement(2, null, null);
                                s = new ASTBase.ScopeGuardStatement(loc, t_4, st);
                                break;
                            }
                        case 173:
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 90)
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
                            if ((this.token.value & 0xFF) == 90)
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
                            if ((this.token.value & 0xFF) == 184)
                            {
                                Loc elseloc_1 = this.token.loc.copy();
                                this.nextToken();
                                elsebody = this.parseStatement(0, null, null);
                                this.checkDanglingElse(elseloc_1);
                            }
                            s = new ASTBase.ConditionalStatement(loc, cond, ifbody, elsebody);
                            if ((flags & ParseStatementFlags.scope_) != 0)
                                s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                            break;
                        case 40:
                            Identifier ident_1 = null;
                            DArray<ASTBase.Expression> args = null;
                            ASTBase.Statement _body_3 = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if ((this.token.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("`pragma(identifier)` expected"));
                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                            }
                            ident_1 = this.token.ident;
                            this.nextToken();
                            if (((this.token.value & 0xFF) == 99 && (this.peekNext() & 0xFF) != 2))
                                args = this.parseArguments();
                            else
                                this.check(TOK.rightParentheses);
                            if ((this.token.value & 0xFF) == 9)
                            {
                                this.nextToken();
                                _body_3 = null;
                            }
                            else
                                _body_3 = this.parseStatement(1, null, null);
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
                                for (; (1) != 0;){
                                    this.nextToken();
                                    exp_1 = this.parseAssignExp();
                                    cases.push(exp_1);
                                    if ((this.token.value & 0xFF) != 99)
                                        break;
                                }
                                this.check(TOK.colon);
                                if ((this.token.value & 0xFF) == 31)
                                {
                                    if (cases.length > 1)
                                        this.error(new BytePtr("only one `case` allowed for start of case range"));
                                    this.nextToken();
                                    this.check(TOK.case_);
                                    last = this.parseAssignExp();
                                    this.check(TOK.colon);
                                }
                                if ((flags & ParseStatementFlags.curlyScope) != 0)
                                {
                                    DArray<ASTBase.Statement> statements_1 = new DArray<ASTBase.Statement>();
                                    for (; ((((this.token.value & 0xFF) != 189 && (this.token.value & 0xFF) != 190) && (this.token.value & 0xFF) != 11) && (this.token.value & 0xFF) != 6);){
                                        (statements_1).push(this.parseStatement(9, null, null));
                                    }
                                    s = new ASTBase.CompoundStatement(loc, statements_1);
                                }
                                else
                                {
                                    s = this.parseStatement(1, null, null);
                                }
                                s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                                if (last != null)
                                {
                                    s = new ASTBase.CaseRangeStatement(loc, exp_1, last, s);
                                }
                                else
                                {
                                    {
                                        int i_1 = cases.length;
                                        for (; (i_1) != 0;i_1--){
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
                                DArray<ASTBase.Statement> statements_2 = new DArray<ASTBase.Statement>();
                                for (; ((((this.token.value & 0xFF) != 189 && (this.token.value & 0xFF) != 190) && (this.token.value & 0xFF) != 11) && (this.token.value & 0xFF) != 6);){
                                    (statements_2).push(this.parseStatement(9, null, null));
                                }
                                s = new ASTBase.CompoundStatement(loc, statements_2);
                            }
                            else
                                s = this.parseStatement(1, null, null);
                            s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                            s = new ASTBase.DefaultStatement(loc, s);
                            break;
                        case 195:
                            ASTBase.Expression exp_2 = null;
                            this.nextToken();
                            exp_2 = (this.token.value & 0xFF) == 9 ? null : this.parseExpression();
                            this.check(TOK.semicolon, new BytePtr("`return` statement"));
                            s = new ASTBase.ReturnStatement(loc, exp_2);
                            break;
                        case 191:
                            Identifier ident_2 = null;
                            this.nextToken();
                            ident_2 = null;
                            if ((this.token.value & 0xFF) == 120)
                            {
                                ident_2 = this.token.ident;
                                this.nextToken();
                            }
                            this.check(TOK.semicolon, new BytePtr("`break` statement"));
                            s = new ASTBase.BreakStatement(loc, ident_2);
                            break;
                        case 192:
                            Identifier ident_3 = null;
                            this.nextToken();
                            ident_3 = null;
                            if ((this.token.value & 0xFF) == 120)
                            {
                                ident_3 = this.token.ident;
                                this.nextToken();
                            }
                            this.check(TOK.semicolon, new BytePtr("`continue` statement"));
                            s = new ASTBase.ContinueStatement(loc, ident_3);
                            break;
                        case 196:
                            Identifier ident_4 = null;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 190)
                            {
                                this.nextToken();
                                s = new ASTBase.GotoDefaultStatement(loc);
                            }
                            else if ((this.token.value & 0xFF) == 189)
                            {
                                ASTBase.Expression exp_3 = null;
                                this.nextToken();
                                if ((this.token.value & 0xFF) != 9)
                                    exp_3 = this.parseExpression();
                                s = new ASTBase.GotoCaseStatement(loc, exp_3);
                            }
                            else
                            {
                                if ((this.token.value & 0xFF) != 120)
                                {
                                    this.error(new BytePtr("identifier expected following `goto`"));
                                    ident_4 = null;
                                }
                                else
                                {
                                    ident_4 = this.token.ident;
                                    this.nextToken();
                                }
                                s = new ASTBase.GotoStatement(loc, ident_4);
                            }
                            this.check(TOK.semicolon, new BytePtr("`goto` statement"));
                            break;
                        case 194:
                            ASTBase.Expression exp_4 = null;
                            ASTBase.Statement _body_5 = null;
                            Ref<Token> t_5 = ref(this.peek(this.token));
                            if ((this.skipAttributes(t_5.value, ptr(t_5)) && ((t_5.value).value & 0xFF) == 153))
                                /*goto Ldeclaration*/{ __dispatch27 = -1; continue dispatched_27; }
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 1)
                            {
                                this.nextToken();
                                exp_4 = this.parseExpression();
                                this.check(TOK.rightParentheses);
                            }
                            else
                                exp_4 = null;
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
                            _body_6 = this.parseStatement(2, null, endloc_2);
                            s = new ASTBase.WithStatement(loc, exp_5, _body_6, endloc_2);
                            break;
                        case 197:
                            ASTBase.Statement _body_7 = null;
                            DArray<ASTBase.Catch> catches = null;
                            ASTBase.Statement finalbody = null;
                            this.nextToken();
                            Loc lookingForElseSave_5 = this.lookingForElse.copy();
                            this.lookingForElse = Loc.initial.copy();
                            _body_7 = this.parseStatement(2, null, null);
                            this.lookingForElse = lookingForElseSave_5.copy();
                            for (; (this.token.value & 0xFF) == 198;){
                                ASTBase.Statement handler = null;
                                ASTBase.Catch c = null;
                                ASTBase.Type t_6 = null;
                                Ref<Identifier> id_1 = ref(null);
                                Loc catchloc = this.token.loc.copy();
                                this.nextToken();
                                if (((this.token.value & 0xFF) == 5 || (this.token.value & 0xFF) != 1))
                                {
                                    t_6 = null;
                                    id_1.value = null;
                                }
                                else
                                {
                                    this.check(TOK.leftParentheses);
                                    id_1.value = null;
                                    t_6 = this.parseType(ptr(id_1), null);
                                    this.check(TOK.rightParentheses);
                                }
                                handler = this.parseStatement(0, null, null);
                                c = new ASTBase.Catch(catchloc, t_6, id_1.value, handler);
                                if (catches == null)
                                    catches = new DArray<ASTBase.Catch>();
                                (catches).push(c);
                            }
                            if ((this.token.value & 0xFF) == 199)
                            {
                                this.nextToken();
                                finalbody = this.parseStatement(2, null, null);
                            }
                            s = _body_7;
                            if ((catches == null && !(finalbody != null)))
                                this.error(new BytePtr("`catch` or `finally` expected following `try`"));
                            else
                            {
                                if (catches != null)
                                    s = new ASTBase.TryCatchStatement(loc, _body_7, catches);
                                if (finalbody != null)
                                    s = new ASTBase.TryFinallyStatement(loc, s, finalbody);
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
                                this.error(new BytePtr("`const`/`immutable`/`shared`/`inout` attributes are not allowed on `asm` blocks"));
                            this.check(TOK.leftCurly);
                            Ref<Token> toklist = ref(null);
                            Ptr<Token> ptoklist = pcopy(ptr(toklist));
                            Identifier label = null;
                            DArray<ASTBase.Statement> statements_3 = new DArray<ASTBase.Statement>();
                            int nestlevel = 0;
                        L_outer15:
                            for (; (1) != 0;){
                                {
                                    int __dispatch29 = 0;
                                    dispatched_29:
                                    do {
                                        switch (__dispatch29 != 0 ? __dispatch29 : (this.token.value & 0xFF))
                                        {
                                            case 120:
                                                if (toklist.value == null)
                                                {
                                                    Token t_7 = this.peek(this.token);
                                                    if (((t_7).value & 0xFF) == 7)
                                                    {
                                                        label = this.token.ident;
                                                        labelloc = this.token.loc.copy();
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
                                                if (nestlevel > 0)
                                                {
                                                    nestlevel -= 1;
                                                    /*goto default*/ { __dispatch29 = -1; continue dispatched_29; }
                                                }
                                                if ((toklist.value != null || label != null))
                                                {
                                                    this.error(new BytePtr("`asm` statements must end in `;`"));
                                                }
                                                break;
                                            case 9:
                                                if (nestlevel != 0)
                                                    this.error(new BytePtr("mismatched number of curly brackets"));
                                                s = null;
                                                if ((toklist.value != null || label != null))
                                                {
                                                    s = new ASTBase.AsmStatement(this.token.loc, toklist.value);
                                                    toklist.value = null;
                                                    ptoklist = pcopy(ptr(toklist));
                                                    if (label != null)
                                                    {
                                                        s = new ASTBase.LabelStatement(labelloc, label, s);
                                                        label = null;
                                                    }
                                                    (statements_3).push(s);
                                                }
                                                this.nextToken();
                                                continue L_outer15;
                                            case 11:
                                                this.error(new BytePtr("matching `}` expected, not end of file"));
                                                /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                                            default:
                                            ptoklist.set(0, this.allocateToken());
                                            (ptoklist.get()).opAssign((this.token));
                                            ptoklist = pcopy((new PtrToNext(ptoklist.get())));
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
                            if ((this.peekNext() & 0xFF) == 1)
                            {
                                ASTBase.Expression e_1 = this.parseExpression();
                                this.check(TOK.semicolon);
                                s = new ASTBase.ExpStatement(loc, e_1);
                            }
                            else
                            {
                                DArray<ASTBase.Dsymbol> imports_1 = this.parseImport();
                                s = new ASTBase.ImportStatement(loc, imports_1);
                                if ((flags & ParseStatementFlags.scope_) != 0)
                                    s = new ASTBase.ScopeStatement(loc, s, this.token.loc);
                            }
                            break;
                        case 36:
                            ASTBase.Dsymbol d_4 = this.parseTemplateDeclaration(false);
                            s = new ASTBase.ExpStatement(loc, d_4);
                            break;
                        default:
                        this.error(new BytePtr("found `%s` instead of statement"), this.token.toChars());
                        /*goto Lerror*/{ __dispatch27 = -6; continue dispatched_27; }
                    /*Lerror:*/
                    case -6:
                    __dispatch27 = 0;
                        for (; (((this.token.value & 0xFF) != 6 && (this.token.value & 0xFF) != 9) && (this.token.value & 0xFF) != 11);) {
                            this.nextToken();
                        }
                        if ((this.token.value & 0xFF) == 9)
                            this.nextToken();
                        s = null;
                        break;
                    }
                } while(__dispatch27 != 0);
            }
            if (pEndloc != null)
                pEndloc.opAssign(this.prevloc);
            return s;
        }

        public  ASTBase.Initializer parseInitializer() {
            ASTBase.StructInitializer _is = null;
            ASTBase.ArrayInitializer ia = null;
            ASTBase.ExpInitializer ie = null;
            ASTBase.Expression e = null;
            Identifier id = null;
            ASTBase.Initializer value = null;
            int comma = 0;
            Loc loc = this.token.loc.copy();
            Token t = null;
            int braces = 0;
            int brackets = 0;
            {
                int __dispatch30 = 0;
                dispatched_30:
                do {
                    switch (__dispatch30 != 0 ? __dispatch30 : (this.token.value & 0xFF))
                    {
                        case 5:
                            braces = 1;
                            {
                                t = this.peek(this.token);
                            L_outer16:
                                for (; (1) != 0;t = this.peek(t)){
                                    {
                                        int __dispatch31 = 0;
                                        dispatched_31:
                                        do {
                                            switch (__dispatch31 != 0 ? __dispatch31 : ((t).value & 0xFF))
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
                                                    if (braces == 1)
                                                        /*goto Lexpression*/{ __dispatch30 = -1; continue dispatched_30; }
                                                    continue L_outer16;
                                                case 5:
                                                    braces++;
                                                    continue L_outer16;
                                                case 6:
                                                    if ((braces -= 1) == 0)
                                                        break;
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
                            for (; (1) != 0;){
                                switch ((this.token.value & 0xFF))
                                {
                                    case 120:
                                        if (comma == 1)
                                            this.error(new BytePtr("comma expected separating field initializers"));
                                        t = this.peek(this.token);
                                        if (((t).value & 0xFF) == 7)
                                        {
                                            id = this.token.ident;
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
                                        if (comma == 2)
                                            this.error(new BytePtr("expression expected, not `,`"));
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
                                    if (comma == 1)
                                        this.error(new BytePtr("comma expected separating field initializers"));
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
                                t = this.peek(this.token);
                            L_outer17:
                                for (; (1) != 0;t = this.peek(t)){
                                    {
                                        int __dispatch33 = 0;
                                        dispatched_33:
                                        do {
                                            switch (__dispatch33 != 0 ? __dispatch33 : ((t).value & 0xFF))
                                            {
                                                case 3:
                                                    brackets++;
                                                    continue L_outer17;
                                                case 4:
                                                    if ((brackets -= 1) == 0)
                                                    {
                                                        t = this.peek(t);
                                                        if ((((((t).value & 0xFF) != 9 && ((t).value & 0xFF) != 99) && ((t).value & 0xFF) != 4) && ((t).value & 0xFF) != 6))
                                                            /*goto Lexpression*/{ __dispatch30 = -1; continue dispatched_30; }
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
                            for (; (1) != 0;){
                                switch ((this.token.value & 0xFF))
                                {
                                    default:
                                    if (comma == 1)
                                    {
                                        this.error(new BytePtr("comma expected separating array initializers, not `%s`"), this.token.toChars());
                                        this.nextToken();
                                        break;
                                    }
                                    e = this.parseAssignExp();
                                    if (!(e != null))
                                        break;
                                    if ((this.token.value & 0xFF) == 7)
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
                                        if (comma == 1)
                                            this.error(new BytePtr("comma expected separating array initializers, not `%s`"), this.token.toChars());
                                        value = this.parseInitializer();
                                        if ((this.token.value & 0xFF) == 7)
                                        {
                                            this.nextToken();
                                            ASTBase.ExpInitializer expInit = value.isExpInitializer();
                                            assert(expInit != null);
                                            e = expInit.exp;
                                            value = this.parseInitializer();
                                        }
                                        else
                                            e = null;
                                        ia.addInit(e, value);
                                        comma = 1;
                                        continue;
                                    case 99:
                                        if (comma == 2)
                                            this.error(new BytePtr("expression expected, not `,`"));
                                        this.nextToken();
                                        comma = 2;
                                        continue;
                                    case 4:
                                        this.nextToken();
                                        break;
                                    case 11:
                                        this.error(new BytePtr("found `%s` instead of array initializer"), this.token.toChars());
                                        break;
                                }
                                break;
                            }
                            return ia;
                        case 128:
                            t = this.peek(this.token);
                            if ((((t).value & 0xFF) == 9 || ((t).value & 0xFF) == 99))
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
            Token t = this.peek(this.token);
            try {
                if ((((t).value & 0xFF) == 99 || ((t).value & 0xFF) == 2))
                {
                    {
                        int __dispatch35 = 0;
                        dispatched_35:
                        do {
                            switch (__dispatch35 != 0 ? __dispatch35 : (this.token.value & 0xFF))
                            {
                                case 219:
                                    e = new ASTBase.FileInitExp(this.token.loc, TOK.file);
                                    break;
                                case 220:
                                    e = new ASTBase.FileInitExp(this.token.loc, TOK.fileFullPath);
                                    break;
                                case 218:
                                    e = new ASTBase.LineInitExp(this.token.loc);
                                    break;
                                case 221:
                                    e = new ASTBase.ModuleInitExp(this.token.loc);
                                    break;
                                case 222:
                                    e = new ASTBase.FuncInitExp(this.token.loc);
                                    break;
                                case 223:
                                    e = new ASTBase.PrettyFuncInitExp(this.token.loc);
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
            if ((this.token.value & 0xFF) != (value & 0xFF))
                this.error(loc, new BytePtr("found `%s` when expecting `%s`"), this.token.toChars(), Token.toChars(value));
            this.nextToken();
        }

        public  void check(byte value) {
            this.check(this.token.loc, value);
        }

        public  void check(byte value, BytePtr string) {
            if ((this.token.value & 0xFF) != (value & 0xFF))
                this.error(new BytePtr("found `%s` when expecting `%s` following %s"), this.token.toChars(), Token.toChars(value), string);
            this.nextToken();
        }

        public  void checkParens(byte value, ASTBase.Expression e) {
            if ((precedence.get((e.op & 0xFF)) == PREC.rel && !((e.parens) != 0)))
                this.error(e.loc, new BytePtr("`%s` must be surrounded by parentheses when next to operator `%s`"), e.toChars(), Token.toChars(value));
        }


        public static class NeedDeclaratorId
        {
            public static final int no = 0;
            public static final int opt = 1;
            public static final int must = 2;
            public static final int mustIfDstyle = 3;
        }

        public  boolean isDeclaration(Token t, int needId, byte endtok, Ptr<Token> pt) {
            Ref<Token> t_ref = ref(t);
            IntRef haveId = ref(0);
            IntRef haveTpl = ref(0);
            for (; (1) != 0;){
                if (((((((t_ref.value).value & 0xFF) == 171 || ((t_ref.value).value & 0xFF) == 182) || ((t_ref.value).value & 0xFF) == 177) || ((t_ref.value).value & 0xFF) == 224) && ((this.peek(t_ref.value)).value & 0xFF) != 1))
                {
                    t_ref.value = this.peek(t_ref.value);
                    continue;
                }
                break;
            }
            try {
                if (!(this.isBasicType(ptr(t_ref))))
                {
                    /*goto Lisnot*/throw Dispatch1.INSTANCE;
                }
                if (!(this.isDeclarator(ptr(t_ref), ptr(haveId), ptr(haveTpl), endtok, needId != NeedDeclaratorId.mustIfDstyle)))
                    /*goto Lisnot*/throw Dispatch1.INSTANCE;
                try {
                    if (((((needId == NeedDeclaratorId.no && !((haveId.value) != 0)) || needId == NeedDeclaratorId.opt) || (needId == NeedDeclaratorId.must && (haveId.value) != 0)) || (needId == NeedDeclaratorId.mustIfDstyle && (haveId.value) != 0)))
                    {
                        if (pt != null)
                            pt.set(0, t_ref.value);
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

        public  boolean isBasicType(Ptr<Token> pt) {
            Ref<Token> t = ref(pt.get());
            try {
                {
                    int __dispatch36 = 0;
                    dispatched_36:
                    do {
                        switch (__dispatch36 != 0 ? __dispatch36 : ((t.value).value & 0xFF))
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
                                t.value = this.peek(t.value);
                                break;
                            case 120:
                            /*L5:*/
                            case -1:
                            __dispatch36 = 0;
                                t.value = this.peek(t.value);
                                if (((t.value).value & 0xFF) == 91)
                                {
                                    /*goto L4*/{ __dispatch36 = -4; continue dispatched_36; }
                                }
                                /*goto L3*/{ __dispatch36 = -3; continue dispatched_36; }
                                /*L2:*/
                            case -2:
                                __dispatch36 = 0;
                                    t.value = this.peek(t.value);
                                /*L3:*/
                            case -3:
                                __dispatch36 = 0;
                                if (((t.value).value & 0xFF) != 97) break;
                            /*Ldot:*/
                            case -5:
                                    t.value = this.peek(t.value);
                                    if (((t.value).value & 0xFF) != 120)
                                        /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                    t.value = this.peek(t.value);
                                    if (((t.value).value & 0xFF) != 91)
                                        /*goto L3*/{ __dispatch36 = -3; continue dispatched_36; }
                                /*L4:*/
                            case -4:
                                    t.value = this.peek(t.value);
                                    {
                                        int __dispatch37 = 0;
                                        dispatched_37:
                                        do {
                                            switch (__dispatch37 != 0 ? __dispatch37 : ((t.value).value & 0xFF))
                                            {
                                                case 120:
                                                    /*goto L5*/{ __dispatch36 = -1; continue dispatched_36; }
                                                case 1:
                                                    if (!(this.skipParens(t.value, ptr(t))))
                                                        /*goto Lfalse*/throw Dispatch0.INSTANCE;
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
                            case 97:
                                /*goto Ldot*/{ __dispatch36 = -5; continue dispatched_36; }
                            case 39:
                            case 229:
                                t.value = this.peek(t.value);
                                if (!(this.skipParens(t.value, ptr(t))))
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                /*goto L3*/{ __dispatch36 = -3; continue dispatched_36; }
                            case 213:
                                t.value = this.peek(t.value);
                                if (((t.value).value & 0xFF) != 1)
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                Ref<Token> lp = ref(t.value);
                                t.value = this.peek(t.value);
                                if ((((t.value).value & 0xFF) != 120 || !pequals((t.value).ident, Id.getMember)))
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                if (!(this.skipParens(lp.value, ptr(lp))))
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                if (((lp.value).value & 0xFF) != 120)
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                break;
                            case 171:
                            case 182:
                            case 224:
                            case 177:
                                t.value = this.peek(t.value);
                                if (((t.value).value & 0xFF) != 1)
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                t.value = this.peek(t.value);
                                if (!(this.isDeclaration(t.value, NeedDeclaratorId.no, TOK.rightParentheses, ptr(t))))
                                {
                                    /*goto Lfalse*/throw Dispatch0.INSTANCE;
                                }
                                t.value = this.peek(t.value);
                                break;
                            default:
                            /*goto Lfalse*/throw Dispatch0.INSTANCE;
                        }
                    } while(__dispatch36 != 0);
                }
                pt.set(0, t.value);
                return true;
            }
            catch(Dispatch0 __d){}
        /*Lfalse:*/
            return false;
        }

        public  boolean isDeclarator(Ptr<Token> pt, IntPtr haveId, IntPtr haveTpl, byte endtok, boolean allowAltSyntax) {
            Ref<Token> t = ref(pt.get());
            int parens = 0;
            if (((t.value).value & 0xFF) == 90)
                return false;
            for (; (1) != 0;){
                parens = 0;
                switch (((t.value).value & 0xFF))
                {
                    case 78:
                        t.value = this.peek(t.value);
                        continue;
                    case 3:
                        t.value = this.peek(t.value);
                        if (((t.value).value & 0xFF) == 4)
                        {
                            t.value = this.peek(t.value);
                        }
                        else if (this.isDeclaration(t.value, NeedDeclaratorId.no, TOK.rightBracket, ptr(t)))
                        {
                            t.value = this.peek(t.value);
                            if ((((t.value).value & 0xFF) == 97 && ((this.peek(t.value)).value & 0xFF) == 120))
                            {
                                t.value = this.peek(t.value);
                                t.value = this.peek(t.value);
                            }
                        }
                        else
                        {
                            if (!(this.isExpression(ptr(t))))
                                return false;
                            if (((t.value).value & 0xFF) == 31)
                            {
                                t.value = this.peek(t.value);
                                if (!(this.isExpression(ptr(t))))
                                    return false;
                                if (((t.value).value & 0xFF) != 4)
                                    return false;
                                t.value = this.peek(t.value);
                            }
                            else
                            {
                                if (((t.value).value & 0xFF) != 4)
                                    return false;
                                t.value = this.peek(t.value);
                                if ((((t.value).value & 0xFF) == 97 && ((this.peek(t.value)).value & 0xFF) == 120))
                                {
                                    t.value = this.peek(t.value);
                                    t.value = this.peek(t.value);
                                }
                            }
                        }
                        continue;
                    case 120:
                        if ((haveId.get()) != 0)
                            return false;
                        haveId.set(0, 1);
                        t.value = this.peek(t.value);
                        break;
                    case 1:
                        if (!(allowAltSyntax))
                            return false;
                        t.value = this.peek(t.value);
                        if (((t.value).value & 0xFF) == 2)
                            return false;
                        if (((t.value).value & 0xFF) == 120)
                        {
                            Token t2 = this.peek(t.value);
                            if (((t2).value & 0xFF) == 2)
                                return false;
                        }
                        if (!(this.isDeclarator(ptr(t), haveId, null, TOK.rightParentheses, true)))
                            return false;
                        t.value = this.peek(t.value);
                        parens = 1;
                        break;
                    case 160:
                    case 161:
                        t.value = this.peek(t.value);
                        if (!(this.isParameters(ptr(t))))
                            return false;
                        this.skipAttributes(t.value, ptr(t));
                        continue;
                    default:
                    break;
                }
                break;
            }
        L_outer19:
            for (; (1) != 0;){
                {
                    int __dispatch39 = 0;
                    dispatched_39:
                    do {
                        switch (__dispatch39 != 0 ? __dispatch39 : ((t.value).value & 0xFF))
                        {
                            case 3:
                                parens = 0;
                                t.value = this.peek(t.value);
                                if (((t.value).value & 0xFF) == 4)
                                {
                                    t.value = this.peek(t.value);
                                }
                                else if (this.isDeclaration(t.value, NeedDeclaratorId.no, TOK.rightBracket, ptr(t)))
                                {
                                    t.value = this.peek(t.value);
                                }
                                else
                                {
                                    if (!(this.isExpression(ptr(t))))
                                        return false;
                                    if (((t.value).value & 0xFF) != 4)
                                        return false;
                                    t.value = this.peek(t.value);
                                }
                                continue L_outer19;
                            case 1:
                                parens = 0;
                                {
                                    Token tk = this.peekPastParen(t.value);
                                    if (tk != null)
                                    {
                                        if (((tk).value & 0xFF) == 1)
                                        {
                                            if (haveTpl == null)
                                                return false;
                                            haveTpl.set(0, 1);
                                            t.value = tk;
                                        }
                                        else if (((tk).value & 0xFF) == 90)
                                        {
                                            if (haveTpl == null)
                                                return false;
                                            haveTpl.set(0, 1);
                                            pt.set(0, tk);
                                            return true;
                                        }
                                    }
                                }
                                if (!(this.isParameters(ptr(t))))
                                    return false;
                                for (; (1) != 0;){
                                    switch (((t.value).value & 0xFF))
                                    {
                                        case 171:
                                        case 182:
                                        case 224:
                                        case 177:
                                        case 215:
                                        case 216:
                                        case 195:
                                        case 203:
                                            t.value = this.peek(t.value);
                                            continue;
                                        case 225:
                                            t.value = this.peek(t.value);
                                            t.value = this.peek(t.value);
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
                                if ((!((parens) != 0) && ((endtok & 0xFF) == 0 || (endtok & 0xFF) == ((t.value).value & 0xFF))))
                                {
                                    pt.set(0, t.value);
                                    return true;
                                }
                                return false;
                            case 120:
                                if (pequals((t.value).ident, Id._body))
                                    /*goto case*/{ __dispatch39 = 187; continue dispatched_39; }
                                /*goto default*/ { __dispatch39 = -2; continue dispatched_39; }
                            case 183:
                                return haveTpl != null ? true : false;
                            default:
                            return false;
                        }
                    } while(__dispatch39 != 0);
                }
            }
            //throw new AssertionError("Unreachable code!");
        }

        public  boolean isParameters(Ptr<Token> pt) {
            Ref<Token> t = ref(pt.get());
            if (((t.value).value & 0xFF) != 1)
                return false;
            t.value = this.peek(t.value);
        L_outer20:
            for (; (1) != 0;t.value = this.peek(t.value)){
            /*L1:*/
                while (true) try {
                    int __dispatch41 = 0;
                    dispatched_41:
                    do {
                        switch (__dispatch41 != 0 ? __dispatch41 : ((t.value).value & 0xFF))
                        {
                            case 2:
                                break;
                            case 10:
                                t.value = this.peek(t.value);
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
                                t.value = this.peek(t.value);
                                if (((t.value).value & 0xFF) == 1)
                                {
                                    t.value = this.peek(t.value);
                                    if (!(this.isDeclaration(t.value, NeedDeclaratorId.no, TOK.rightParentheses, ptr(t))))
                                        return false;
                                    t.value = this.peek(t.value);
                                    /*goto L2*/{ __dispatch41 = -1; continue dispatched_41; }
                                }
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            default:

                                if (!(this.isBasicType(ptr(t))))
                                    return false;
                            /*L2:*/
                            case -1:
                            __dispatch41 = 0;
                                IntRef tmp = ref(0);
                                if ((((t.value).value & 0xFF) != 10 && !(this.isDeclarator(ptr(t), ptr(tmp), null, TOK.reserved, true))))
                                    return false;
                                if (((t.value).value & 0xFF) == 90)
                                {
                                    t.value = this.peek(t.value);
                                    if (!(this.isExpression(ptr(t))))
                                        return false;
                                }
                                if (((t.value).value & 0xFF) == 10)
                                {
                                    t.value = this.peek(t.value);
                                    break;
                                }

                            if (((t.value).value & 0xFF) == 99)
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
            if (((t.value).value & 0xFF) != 2)
                return false;
            t.value = this.peek(t.value);
            pt.set(0, t.value);
            return true;
        }

        public  boolean isExpression(Ptr<Token> pt) {
            Token t = pt.get();
            int brnest = 0;
            int panest = 0;
            int curlynest = 0;
            for (; ;t = this.peek(t)){
                switch (((t).value & 0xFF))
                {
                    case 3:
                        brnest++;
                        continue;
                    case 4:
                        if ((brnest -= 1) >= 0)
                            continue;
                        break;
                    case 1:
                        panest++;
                        continue;
                    case 99:
                        if (((brnest) != 0 || (panest) != 0))
                            continue;
                        break;
                    case 2:
                        if ((panest -= 1) >= 0)
                            continue;
                        break;
                    case 5:
                        curlynest++;
                        continue;
                    case 6:
                        if ((curlynest -= 1) >= 0)
                            continue;
                        return false;
                    case 31:
                        if ((brnest) != 0)
                            continue;
                        break;
                    case 9:
                        if ((curlynest) != 0)
                            continue;
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

        public  boolean skipParens(Token t, Ptr<Token> pt) {
            if (((t).value & 0xFF) != 1)
                return false;
            int parens = 0;
            try {
                try {
                L_outer21:
                    for (; (1) != 0;){
                        {
                            int __dispatch43 = 0;
                            dispatched_43:
                            do {
                                switch (__dispatch43 != 0 ? __dispatch43 : ((t).value & 0xFF))
                                {
                                    case 1:
                                        parens++;
                                        break;
                                    case 2:
                                        parens--;
                                        if (parens < 0)
                                            /*goto Lfalse*/throw Dispatch1.INSTANCE;
                                        if (parens == 0)
                                            /*goto Ldone*/throw Dispatch0.INSTANCE;
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
                    pt.set(0, this.peek(t));
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lfalse:*/
            return false;
        }

        public  boolean skipParensIf(Token t, Ptr<Token> pt) {
            if (((t).value & 0xFF) != 1)
            {
                if (pt != null)
                    pt.set(0, t);
                return true;
            }
            return this.skipParens(t, pt);
        }

        public  boolean hasOptionalParensThen(Token t, byte expected) {
            Ref<Token> tk = ref(null);
            if (!(this.skipParensIf(t, ptr(tk))))
                return false;
            return ((tk.value).value & 0xFF) == (expected & 0xFF);
        }

        public  boolean skipAttributes(Token t, Ptr<Token> pt) {
            Ref<Token> t_ref = ref(t);
            try {
                try {
                L_outer22:
                    for (; (1) != 0;){
                        {
                            int __dispatch44 = 0;
                            dispatched_44:
                            do {
                                switch (__dispatch44 != 0 ? __dispatch44 : ((t_ref.value).value & 0xFF))
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
                                        if (((this.peek(t_ref.value)).value & 0xFF) == 1)
                                        {
                                            t_ref.value = this.peek(t_ref.value);
                                            if (!(this.skipParens(t_ref.value, ptr(t_ref))))
                                                /*goto Lerror*/throw Dispatch1.INSTANCE;
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
                                        t_ref.value = this.peek(t_ref.value);
                                        if (((t_ref.value).value & 0xFF) == 120)
                                        {
                                            if ((((((pequals((t_ref.value).ident, Id.property) || pequals((t_ref.value).ident, Id.nogc)) || pequals((t_ref.value).ident, Id.safe)) || pequals((t_ref.value).ident, Id.trusted)) || pequals((t_ref.value).ident, Id.system)) || pequals((t_ref.value).ident, Id.disable)))
                                                break;
                                            t_ref.value = this.peek(t_ref.value);
                                            if (((t_ref.value).value & 0xFF) == 91)
                                            {
                                                t_ref.value = this.peek(t_ref.value);
                                                if (((t_ref.value).value & 0xFF) == 1)
                                                {
                                                    if (!(this.skipParens(t_ref.value, ptr(t_ref))))
                                                        /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                }
                                                else
                                                {
                                                    if (((t_ref.value).value & 0xFF) == 229)
                                                    {
                                                        t_ref.value = this.peek(t_ref.value);
                                                        if (!(this.skipParens(t_ref.value, ptr(t_ref))))
                                                            /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                    }
                                                    else
                                                        t_ref.value = this.peek(t_ref.value);
                                                }
                                            }
                                            if (((t_ref.value).value & 0xFF) == 1)
                                            {
                                                if (!(this.skipParens(t_ref.value, ptr(t_ref))))
                                                    /*goto Lerror*/throw Dispatch1.INSTANCE;
                                                continue L_outer22;
                                            }
                                            continue L_outer22;
                                        }
                                        if (((t_ref.value).value & 0xFF) == 1)
                                        {
                                            if (!(this.skipParens(t_ref.value, ptr(t_ref))))
                                                /*goto Lerror*/throw Dispatch1.INSTANCE;
                                            continue L_outer22;
                                        }
                                        /*goto Lerror*/throw Dispatch1.INSTANCE;
                                    default:
                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                }
                            } while(__dispatch44 != 0);
                        }
                        t_ref.value = this.peek(t_ref.value);
                    }
                }
                catch(Dispatch0 __d){}
            /*Ldone:*/
                if (pt != null)
                    pt.set(0, t_ref.value);
                return true;
            }
            catch(Dispatch1 __d){}
        /*Lerror:*/
            return false;
        }

        public  ASTBase.Expression parseExpression() {
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseAssignExp();
            for (; (this.token.value & 0xFF) == 99;){
                this.nextToken();
                ASTBase.Expression e2 = this.parseAssignExp();
                e = new ASTBase.CommaExp(loc, e, e2, false);
                loc = this.token.loc.copy();
            }
            return e;
        }

        public  ASTBase.Expression parsePrimaryExp() {
            ASTBase.Expression e = null;
            ASTBase.Type t = null;
            Identifier id = null;
            Loc loc = this.token.loc.copy();
            {
                int __dispatch45 = 0;
                dispatched_45:
                do {
                    switch (__dispatch45 != 0 ? __dispatch45 : (this.token.value & 0xFF))
                    {
                        case 120:
                            Token t1 = this.peek(this.token);
                            Token t2 = this.peek(t1);
                            if ((((t1).value & 0xFF) == 75 && ((t2).value & 0xFF) == 55))
                            {
                                this.nextToken();
                                this.nextToken();
                                this.nextToken();
                                this.error(new BytePtr("use `.` for member lookup, not `->`"));
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            if ((this.peekNext() & 0xFF) == 228)
                                /*goto case_delegate*/{ __dispatch45 = -2; continue dispatched_45; }
                            id = this.token.ident;
                            this.nextToken();
                            byte save = TOK.reserved;
                            if ((((this.token.value & 0xFF) == 91 && ((save = this.peekNext()) & 0xFF) != 63) && (save & 0xFF) != 175))
                            {
                                ASTBase.TemplateInstance tempinst = new ASTBase.TemplateInstance(loc, id, this.parseTemplateArguments());
                                e = new ASTBase.ScopeExp(loc, tempinst);
                            }
                            else
                                e = new ASTBase.IdentifierExp(loc, id);
                            break;
                        case 35:
                            if (!((this.inBrackets) != 0))
                                this.error(new BytePtr("`$` is valid only inside [] of index or slice"));
                            e = new ASTBase.DollarExp(loc);
                            this.nextToken();
                            break;
                        case 97:
                            e = new ASTBase.IdentifierExp(loc, Id.empty);
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
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.intvalue, ASTBase.Type.tint32);
                            this.nextToken();
                            break;
                        case 106:
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.intvalue, ASTBase.Type.tuns32);
                            this.nextToken();
                            break;
                        case 107:
                            e = new ASTBase.IntegerExp(loc, (long)this.token.intvalue, ASTBase.Type.tint64);
                            this.nextToken();
                            break;
                        case 108:
                            e = new ASTBase.IntegerExp(loc, this.token.intvalue, ASTBase.Type.tuns64);
                            this.nextToken();
                            break;
                        case 111:
                            e = new ASTBase.RealExp(loc, this.token.floatvalue, ASTBase.Type.tfloat32);
                            this.nextToken();
                            break;
                        case 112:
                            e = new ASTBase.RealExp(loc, this.token.floatvalue, ASTBase.Type.tfloat64);
                            this.nextToken();
                            break;
                        case 113:
                            e = new ASTBase.RealExp(loc, this.token.floatvalue, ASTBase.Type.tfloat80);
                            this.nextToken();
                            break;
                        case 114:
                            e = new ASTBase.RealExp(loc, this.token.floatvalue, ASTBase.Type.timaginary32);
                            this.nextToken();
                            break;
                        case 115:
                            e = new ASTBase.RealExp(loc, this.token.floatvalue, ASTBase.Type.timaginary64);
                            this.nextToken();
                            break;
                        case 116:
                            e = new ASTBase.RealExp(loc, this.token.floatvalue, ASTBase.Type.timaginary80);
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
                            assertMsg(loc.isValid(),  new ByteSlice("__FILE_FULL_PATH__ does not work with an invalid location"));
                            e = new ASTBase.StringExp(loc, FileName.toAbsolute(loc.filename, null));
                            this.nextToken();
                            break;
                        case 218:
                            e = new ASTBase.IntegerExp(loc, (long)loc.linnum, ASTBase.Type.tint32);
                            this.nextToken();
                            break;
                        case 221:
                            BytePtr s_1 = pcopy(this.md != null ? (this.md).toChars() : this.mod.toChars());
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
                            e = new ASTBase.IntegerExp(loc, (long)(byte)this.token.intvalue, ASTBase.Type.tchar);
                            this.nextToken();
                            break;
                        case 118:
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.intvalue, ASTBase.Type.twchar);
                            this.nextToken();
                            break;
                        case 119:
                            e = new ASTBase.IntegerExp(loc, (long)(int)this.token.intvalue, ASTBase.Type.tdchar);
                            this.nextToken();
                            break;
                        case 121:
                        case 122:
                            BytePtr s_2 = pcopy(this.token.ustring);
                            int len = this.token.len;
                            byte postfix = this.token.postfix;
                            for (; (1) != 0;){
                                Token prev = this.token.copy();
                                this.nextToken();
                                if (((this.token.value & 0xFF) == 121 || (this.token.value & 0xFF) == 122))
                                {
                                    if ((this.token.postfix) != 0)
                                    {
                                        if ((this.token.postfix & 0xFF) != (postfix & 0xFF))
                                            this.error(new BytePtr("mismatched string literal postfixes `'%c'` and `'%c'`"), postfix, this.token.postfix);
                                        postfix = this.token.postfix;
                                    }
                                    this.error(new BytePtr("Implicit string concatenation is deprecated, use %s ~ %s instead"), prev.toChars(), this.token.toChars());
                                    int len1 = len;
                                    int len2 = this.token.len;
                                    len = len1 + len2;
                                    BytePtr s2 = pcopy(toBytePtr(Mem.xmalloc(len)));
                                    memcpy((BytePtr)(s2), (s_2), (len1));
                                    memcpy((BytePtr)((s2.plus(len1))), (this.token.ustring), (len2));
                                    s_2 = pcopy(s2);
                                }
                                else
                                    break;
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
                            if ((this.token.value & 0xFF) == 1)
                            {
                                e = new ASTBase.TypeExp(loc, t);
                                e = new ASTBase.CallExp(loc, e, this.parseArguments());
                                break;
                            }
                            this.check(TOK.dot, t.toChars());
                            if ((this.token.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("found `%s` when expecting identifier following `%s`."), this.token.toChars(), t.toChars());
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            e = new ASTBase.DotIdExp(loc, new ASTBase.TypeExp(loc, t), this.token.ident);
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
                            if (this.isDeclaration(this.token, NeedDeclaratorId.no, TOK.reserved, null))
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
                            DArray<RootObject> args = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses);
                            if ((this.token.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("`__traits(identifier, args...)` expected"));
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            ident = this.token.ident;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 99)
                                args = this.parseTemplateArgumentList();
                            else
                                this.check(TOK.rightParentheses);
                            e = new ASTBase.TraitsExp(loc, ident, args);
                            break;
                        case 63:
                            ASTBase.Type targ = null;
                            Ref<Identifier> ident_1 = ref(null);
                            ASTBase.Type tspec = null;
                            byte tok = TOK.reserved;
                            byte tok2 = TOK.reserved;
                            DArray<ASTBase.TemplateParameter> tpl = null;
                            this.nextToken();
                            if ((this.token.value & 0xFF) == 1)
                            {
                                this.nextToken();
                                if (((this.token.value & 0xFF) == 120 && (this.peekNext() & 0xFF) == 1))
                                {
                                    this.error(loc, new BytePtr("unexpected `(` after `%s`, inside `is` expression. Try enclosing the contents of `is` with a `typeof` expression"), this.token.toChars());
                                    this.nextToken();
                                    Token tempTok = this.peekPastParen(this.token);
                                    (this.token).opAssign((tempTok));
                                    /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                                }
                                targ = this.parseType(ptr(ident_1), null);
                                if (((this.token.value & 0xFF) == 7 || (this.token.value & 0xFF) == 58))
                                {
                                    tok = this.token.value;
                                    this.nextToken();
                                    if (((tok & 0xFF) == 58 && ((((((((((((((((((this.token.value & 0xFF) == 152 || (this.token.value & 0xFF) == 155) || (this.token.value & 0xFF) == 153) || (this.token.value & 0xFF) == 124) || (this.token.value & 0xFF) == 156) || (this.token.value & 0xFF) == 154) || (this.token.value & 0xFF) == 180) || (this.token.value & 0xFF) == 34) || (this.token.value & 0xFF) == 209) || (this.token.value & 0xFF) == 212) || ((this.token.value & 0xFF) == 171 && ((this.peek(this.token)).value & 0xFF) == 2)) || ((this.token.value & 0xFF) == 182 && ((this.peek(this.token)).value & 0xFF) == 2)) || ((this.token.value & 0xFF) == 224 && ((this.peek(this.token)).value & 0xFF) == 2)) || ((this.token.value & 0xFF) == 177 && ((this.peek(this.token)).value & 0xFF) == 2)) || (this.token.value & 0xFF) == 161) || (this.token.value & 0xFF) == 160) || (this.token.value & 0xFF) == 195) || ((this.token.value & 0xFF) == 229 && ((this.peek(this.token)).value & 0xFF) == 2))))
                                    {
                                        tok2 = this.token.value;
                                        this.nextToken();
                                    }
                                    else
                                    {
                                        tspec = this.parseType(null, null);
                                    }
                                }
                                if (tspec != null)
                                {
                                    if ((this.token.value & 0xFF) == 99)
                                        tpl = this.parseTemplateParameterList(1);
                                    else
                                    {
                                        tpl = new DArray<ASTBase.TemplateParameter>();
                                        this.check(TOK.rightParentheses);
                                    }
                                }
                                else
                                    this.check(TOK.rightParentheses);
                            }
                            else
                            {
                                this.error(new BytePtr("`type identifier : specialization` expected following `is`"));
                                /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                            }
                            e = new ASTBase.IsExp(loc, targ, ident_1.value, tok, tspec, tok2, tpl);
                            break;
                        case 14:
                            ASTBase.Expression msg = null;
                            this.nextToken();
                            this.check(TOK.leftParentheses, new BytePtr("`assert`"));
                            e = this.parseAssignExp();
                            if ((this.token.value & 0xFF) == 99)
                            {
                                this.nextToken();
                                if ((this.token.value & 0xFF) != 2)
                                {
                                    msg = this.parseAssignExp();
                                    if ((this.token.value & 0xFF) == 99)
                                        this.nextToken();
                                }
                            }
                            this.check(TOK.rightParentheses);
                            e = new ASTBase.AssertExp(loc, e, msg);
                            break;
                        case 162:
                            this.nextToken();
                            if ((this.token.value & 0xFF) != 1)
                                this.error(new BytePtr("found `%s` when expecting `%s` following %s"), this.token.toChars(), Token.toChars(TOK.leftParentheses), new BytePtr("`mixin`"));
                            DArray<ASTBase.Expression> exps = this.parseArguments();
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
                            if ((this.peekNext() & 0xFF) == 1)
                            {
                                Ref<Token> tk = ref(this.peekPastParen(this.peek(this.token)));
                                if ((this.skipAttributes(tk.value, ptr(tk)) && (((tk.value).value & 0xFF) == 228 || ((tk.value).value & 0xFF) == 5)))
                                {
                                    /*goto case_delegate*/{ __dispatch45 = -2; continue dispatched_45; }
                                }
                            }
                            this.nextToken();
                            this.error(new BytePtr("found `%s` when expecting function literal following `ref`"), this.token.toChars());
                            /*goto Lerr*/{ __dispatch45 = -1; continue dispatched_45; }
                        case 1:
                            Ref<Token> tk_1 = ref(this.peekPastParen(this.token));
                            if ((this.skipAttributes(tk_1.value, ptr(tk_1)) && (((tk_1.value).value & 0xFF) == 228 || ((tk_1.value).value & 0xFF) == 5)))
                            {
                                /*goto case_delegate*/{ __dispatch45 = -2; continue dispatched_45; }
                            }
                            this.nextToken();
                            e = this.parseExpression();
                            e.parens = (byte)1;
                            this.check(loc, TOK.rightParentheses);
                            break;
                        case 3:
                            DArray<ASTBase.Expression> values = new DArray<ASTBase.Expression>();
                            DArray<ASTBase.Expression> keys = null;
                            this.nextToken();
                            for (; ((this.token.value & 0xFF) != 4 && (this.token.value & 0xFF) != 11);){
                                e = this.parseAssignExp();
                                if (((this.token.value & 0xFF) == 7 && (keys != null || (values).length == 0)))
                                {
                                    this.nextToken();
                                    if (keys == null)
                                        keys = new DArray<ASTBase.Expression>();
                                    (keys).push(e);
                                    e = this.parseAssignExp();
                                }
                                else if (keys != null)
                                {
                                    this.error(new BytePtr("`key:value` expected for associative array literal"));
                                    keys = null;
                                }
                                (values).push(e);
                                if ((this.token.value & 0xFF) == 4)
                                    break;
                                this.check(TOK.comma);
                            }
                            this.check(loc, TOK.rightBracket);
                            if (keys != null)
                                e = new ASTBase.AssocArrayLiteralExp(loc, keys, values);
                            else
                                e = new ASTBase.ArrayLiteralExp(loc, null, values);
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
                        this.error(new BytePtr("expression expected, not `%s`"), this.token.toChars());
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
            Loc loc = this.token.loc.copy();
            switch ((this.token.value & 0xFF))
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
                    for (; (1) != 0;){
                        switch ((this.token.value & 0xFF))
                        {
                            case 171:
                                if ((this.peekNext() & 0xFF) == 1)
                                    break;
                                m |= ASTBase.MODFlags.const_;
                                this.nextToken();
                                continue;
                            case 182:
                                if ((this.peekNext() & 0xFF) == 1)
                                    break;
                                m |= ASTBase.MODFlags.immutable_;
                                this.nextToken();
                                continue;
                            case 224:
                                if ((this.peekNext() & 0xFF) == 1)
                                    break;
                                m |= ASTBase.MODFlags.shared_;
                                this.nextToken();
                                continue;
                            case 177:
                                if ((this.peekNext() & 0xFF) == 1)
                                    break;
                                m |= ASTBase.MODFlags.wild;
                                this.nextToken();
                                continue;
                            default:
                            break;
                        }
                        break;
                    }
                    if ((this.token.value & 0xFF) == 2)
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
                    if ((stc == 0L && (this.token.value & 0xFF) == 97))
                    {
                        this.nextToken();
                        if ((this.token.value & 0xFF) != 120)
                        {
                            this.error(new BytePtr("identifier expected following `(type)`."));
                            return null;
                        }
                        e = new ASTBase.DotIdExp(loc, new ASTBase.TypeExp(loc, t_1), this.token.ident);
                        this.nextToken();
                        e = this.parsePostExp(e);
                    }
                    else
                    {
                        e = new ASTBase.TypeExp(loc, t_1);
                        if ((this.token.value & 0xFF) != 1)
                        {
                            this.error(new BytePtr("`(arguments)` expected following `%s`"), t_1.toChars());
                            return e;
                        }
                        e = new ASTBase.CallExp(loc, e, this.parseArguments());
                    }
                    break;
                case 1:
                    Ref<Token> tk = ref(this.peek(this.token));
                    if (this.isDeclaration(tk.value, NeedDeclaratorId.no, TOK.rightParentheses, ptr(tk)))
                    {
                        tk.value = this.peek(tk.value);
                        switch (((tk.value).value & 0xFF))
                        {
                            case 91:
                                tk.value = this.peek(tk.value);
                                if ((((tk.value).value & 0xFF) == 63 || ((tk.value).value & 0xFF) == 175))
                                    break;
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
                                if ((this.token.value & 0xFF) == 97)
                                {
                                    if (((this.peekNext() & 0xFF) != 120 && (this.peekNext() & 0xFF) != 22))
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
            for (; (this.token.value & 0xFF) == 226;){
                this.nextToken();
                ASTBase.Expression e2 = this.parseUnaryExp();
                e = new ASTBase.PowExp(loc, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parsePostExp(ASTBase.Expression e) {
            for (; (1) != 0;){
                Loc loc = this.token.loc.copy();
                switch ((this.token.value & 0xFF))
                {
                    case 97:
                        this.nextToken();
                        if ((this.token.value & 0xFF) == 120)
                        {
                            Identifier id = this.token.ident;
                            this.nextToken();
                            if ((((this.token.value & 0xFF) == 91 && (this.peekNext() & 0xFF) != 63) && (this.peekNext() & 0xFF) != 175))
                            {
                                DArray<RootObject> tiargs = this.parseTemplateArguments();
                                e = new ASTBase.DotTemplateInstanceExp(loc, e, id, tiargs);
                            }
                            else
                                e = new ASTBase.DotIdExp(loc, e, id);
                            continue;
                        }
                        if ((this.token.value & 0xFF) == 22)
                        {
                            e = this.parseNewExp(e);
                            continue;
                        }
                        this.error(new BytePtr("identifier or `new` expected following `.`, not `%s`"), this.token.toChars());
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
                        DArray<ASTBase.Expression> arguments = new DArray<ASTBase.Expression>();
                        this.inBrackets++;
                        this.nextToken();
                        for (; ((this.token.value & 0xFF) != 4 && (this.token.value & 0xFF) != 11);){
                            index = this.parseAssignExp();
                            if ((this.token.value & 0xFF) == 31)
                            {
                                this.nextToken();
                                upr = this.parseAssignExp();
                                (arguments).push(new ASTBase.IntervalExp(loc, index, upr));
                            }
                            else
                                (arguments).push(index);
                            if ((this.token.value & 0xFF) == 4)
                                break;
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
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseUnaryExp();
            for (; (1) != 0;){
                switch ((this.token.value & 0xFF))
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
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseMulExp();
            for (; (1) != 0;){
                switch ((this.token.value & 0xFF))
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
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseAddExp();
            for (; (1) != 0;){
                switch ((this.token.value & 0xFF))
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
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseShiftExp();
            byte op = this.token.value;
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
                                Token t = this.peek(this.token);
                                if (((t).value & 0xFF) == 175)
                                {
                                    this.nextToken();
                                    this.nextToken();
                                    ASTBase.Expression e2_3 = this.parseShiftExp();
                                    e = new ASTBase.InExp(loc, e, e2_3);
                                    e = new ASTBase.NotExp(loc, e);
                                    break;
                                }
                                if (((t).value & 0xFF) != 63)
                                    break;
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
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseCmpExp();
            for (; (this.token.value & 0xFF) == 84;){
                this.checkParens(TOK.and, e);
                this.nextToken();
                ASTBase.Expression e2 = this.parseCmpExp();
                this.checkParens(TOK.and, e2);
                e = new ASTBase.AndExp(loc, e, e2);
                loc = this.token.loc.copy();
            }
            return e;
        }

        public  ASTBase.Expression parseXorExp() {
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseAndExp();
            for (; (this.token.value & 0xFF) == 86;){
                this.checkParens(TOK.xor, e);
                this.nextToken();
                ASTBase.Expression e2 = this.parseAndExp();
                this.checkParens(TOK.xor, e2);
                e = new ASTBase.XorExp(loc, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseOrExp() {
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseXorExp();
            for (; (this.token.value & 0xFF) == 85;){
                this.checkParens(TOK.or, e);
                this.nextToken();
                ASTBase.Expression e2 = this.parseXorExp();
                this.checkParens(TOK.or, e2);
                e = new ASTBase.OrExp(loc, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseAndAndExp() {
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseOrExp();
            for (; (this.token.value & 0xFF) == 101;){
                this.nextToken();
                ASTBase.Expression e2 = this.parseOrExp();
                e = new ASTBase.LogicalExp(loc, TOK.andAnd, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseOrOrExp() {
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseAndAndExp();
            for (; (this.token.value & 0xFF) == 102;){
                this.nextToken();
                ASTBase.Expression e2 = this.parseAndAndExp();
                e = new ASTBase.LogicalExp(loc, TOK.orOr, e, e2);
            }
            return e;
        }

        public  ASTBase.Expression parseCondExp() {
            Loc loc = this.token.loc.copy();
            ASTBase.Expression e = this.parseOrOrExp();
            if ((this.token.value & 0xFF) == 100)
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
            if (e == null)
                return e;
            if ((((e.op & 0xFF) == 100 && !((e.parens) != 0)) && precedence.get((this.token.value & 0xFF)) == PREC.assign))
                deprecation(e.loc, new BytePtr("`%s` must be surrounded by parentheses when next to operator `%s`"), e.toChars(), Token.toChars(this.token.value));
            Loc loc = this.token.loc.copy();
            switch ((this.token.value & 0xFF))
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

        public  DArray<ASTBase.Expression> parseArguments() {
            DArray<ASTBase.Expression> arguments = null;
            byte endtok = TOK.reserved;
            arguments = new DArray<ASTBase.Expression>();
            endtok = (this.token.value & 0xFF) == 3 ? TOK.rightBracket : TOK.rightParentheses;
            this.nextToken();
            for (; ((this.token.value & 0xFF) != (endtok & 0xFF) && (this.token.value & 0xFF) != 11);){
                ASTBase.Expression arg = this.parseAssignExp();
                (arguments).push(arg);
                if ((this.token.value & 0xFF) == (endtok & 0xFF))
                    break;
                this.check(TOK.comma);
            }
            this.check(endtok);
            return arguments;
        }

        public  ASTBase.Expression parseNewExp(ASTBase.Expression thisexp) {
            Loc loc = this.token.loc.copy();
            this.nextToken();
            DArray<ASTBase.Expression> newargs = null;
            DArray<ASTBase.Expression> arguments = null;
            if ((this.token.value & 0xFF) == 1)
            {
                newargs = this.parseArguments();
            }
            if ((this.token.value & 0xFF) == 153)
            {
                this.nextToken();
                if ((this.token.value & 0xFF) == 1)
                    arguments = this.parseArguments();
                DArray<ASTBase.BaseClass> baseclasses = null;
                if ((this.token.value & 0xFF) != 5)
                    baseclasses = this.parseBaseClasses();
                Identifier id = null;
                DArray<ASTBase.Dsymbol> members = null;
                if ((this.token.value & 0xFF) != 5)
                {
                    this.error(new BytePtr("`{ members }` expected for anonymous class"));
                }
                else
                {
                    this.nextToken();
                    members = this.parseDeclDefs(0, null, null);
                    if ((this.token.value & 0xFF) != 6)
                        this.error(new BytePtr("class member expected"));
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
            if ((t.ty & 0xFF) == ASTBase.ENUMTY.Taarray)
            {
                ASTBase.TypeAArray taa = (ASTBase.TypeAArray)t;
                ASTBase.Type index = taa.index;
                ASTBase.Expression edim = ASTBase.typeToExpression(index);
                if (!(edim != null))
                {
                    this.error(new BytePtr("need size of rightmost array, not type `%s`"), index.toChars());
                    return new ASTBase.NullExp(loc, null);
                }
                t = new ASTBase.TypeSArray(taa.next, edim);
            }
            else if (((this.token.value & 0xFF) == 1 && (t.ty & 0xFF) != ASTBase.ENUMTY.Tsarray))
            {
                arguments = this.parseArguments();
            }
            ASTBase.NewExp e = new ASTBase.NewExp(loc, thisexp, newargs, t, arguments);
            return e;
        }

        public  void addComment(ASTBase.Dsymbol s, BytePtr blockComment) {
            if (s != null)
            {
                s.addComment(Lexer.combineComments(blockComment, this.token.lineComment, true));
                this.token.lineComment = null;
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
