package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.utf.*;

public class tokens {
    private static final TOK[] initializer_0 = {TOK.this_, TOK.super_, TOK.assert_, TOK.null_, TOK.true_, TOK.false_, TOK.cast_, TOK.new_, TOK.delete_, TOK.throw_, TOK.module_, TOK.pragma_, TOK.typeof_, TOK.typeid_, TOK.template_, TOK.void_, TOK.int8, TOK.uns8, TOK.int16, TOK.uns16, TOK.int32, TOK.uns32, TOK.int64, TOK.uns64, TOK.int128, TOK.uns128, TOK.float32, TOK.float64, TOK.float80, TOK.bool_, TOK.char_, TOK.wchar_, TOK.dchar_, TOK.imaginary32, TOK.imaginary64, TOK.imaginary80, TOK.complex32, TOK.complex64, TOK.complex80, TOK.delegate_, TOK.function_, TOK.is_, TOK.if_, TOK.else_, TOK.while_, TOK.for_, TOK.do_, TOK.switch_, TOK.case_, TOK.default_, TOK.break_, TOK.continue_, TOK.synchronized_, TOK.return_, TOK.goto_, TOK.try_, TOK.catch_, TOK.finally_, TOK.with_, TOK.asm_, TOK.foreach_, TOK.foreach_reverse_, TOK.scope_, TOK.struct_, TOK.class_, TOK.interface_, TOK.union_, TOK.enum_, TOK.import_, TOK.mixin_, TOK.static_, TOK.final_, TOK.const_, TOK.alias_, TOK.override_, TOK.abstract_, TOK.debug_, TOK.deprecated_, TOK.in_, TOK.out_, TOK.inout_, TOK.lazy_, TOK.auto_, TOK.align_, TOK.extern_, TOK.private_, TOK.package_, TOK.protected_, TOK.public_, TOK.export_, TOK.invariant_, TOK.unittest_, TOK.version_, TOK.argumentTypes, TOK.parameters, TOK.ref_, TOK.macro_, TOK.pure_, TOK.nothrow_, TOK.gshared, TOK.traits, TOK.vector, TOK.overloadSet, TOK.file, TOK.fileFullPath, TOK.line, TOK.moduleString, TOK.functionString, TOK.prettyFunction, TOK.shared_, TOK.immutable_};
    private static final ByteSlice[] initializer_1 = { new ByteSlice("reserved"),  new ByteSlice("("),  new ByteSlice(")"),  new ByteSlice("["),  new ByteSlice("]"),  new ByteSlice("{"),  new ByteSlice("}"),  new ByteSlice(":"),  new ByteSlice("-"),  new ByteSlice(";"),  new ByteSlice("..."),  new ByteSlice("End of File"),  new ByteSlice("cast"),  new ByteSlice("null"),  new ByteSlice("assert"),  new ByteSlice("true"),  new ByteSlice("false"),  new ByteSlice("[]"),  new ByteSlice("call"),  new ByteSlice("&"),  new ByteSlice("type"),  new ByteSlice("throw"),  new ByteSlice("new"),  new ByteSlice("delete"),  new ByteSlice("*"),  new ByteSlice("symoff"),  new ByteSlice("var"),  new ByteSlice("dotvar"),  new ByteSlice("dotid"),  new ByteSlice("dotti"),  new ByteSlice("dottype"),  new ByteSlice(".."),  new ByteSlice("arraylength"),  new ByteSlice("version"),  new ByteSlice("module"),  new ByteSlice("$"),  new ByteSlice("template"),  new ByteSlice("dottd"),  new ByteSlice("declaration"),  new ByteSlice("typeof"),  new ByteSlice("pragma"),  new ByteSlice("symbol"),  new ByteSlice("typeid"),  new ByteSlice("+"),  new ByteSlice("remove"),  new ByteSlice("newanonclass"),  new ByteSlice("comment"),  new ByteSlice("arrayliteral"),  new ByteSlice("assocarrayliteral"),  new ByteSlice("structliteral"),  new ByteSlice("classreference"),  new ByteSlice("thrownexception"),  new ByteSlice("delegateptr"),  new ByteSlice("delegatefuncptr"),  new ByteSlice("<"),  new ByteSlice(">"),  new ByteSlice("<="),  new ByteSlice(">="),  new ByteSlice("=="),  new ByteSlice("!="),  new ByteSlice("is"),  new ByteSlice("!is"),  new ByteSlice("[i]"),  new ByteSlice("is"),  new ByteSlice("<<"),  new ByteSlice(">>"),  new ByteSlice("<<="),  new ByteSlice(">>="),  new ByteSlice(">>>"),  new ByteSlice(">>>="),  new ByteSlice("~"),  new ByteSlice("~="),  new ByteSlice("~="),  new ByteSlice("~="),  new ByteSlice("+"),  new ByteSlice("-"),  new ByteSlice("+="),  new ByteSlice("-="),  new ByteSlice("*"),  new ByteSlice("/"),  new ByteSlice("%"),  new ByteSlice("*="),  new ByteSlice("/="),  new ByteSlice("%="),  new ByteSlice("&"),  new ByteSlice("|"),  new ByteSlice("^"),  new ByteSlice("&="),  new ByteSlice("|="),  new ByteSlice("^="),  new ByteSlice("="),  new ByteSlice("!"),  new ByteSlice("~"),  new ByteSlice("++"),  new ByteSlice("--"),  new ByteSlice("="),  new ByteSlice("="),  new ByteSlice("."),  new ByteSlice("arrow"),  new ByteSlice(","),  new ByteSlice("?"),  new ByteSlice("&&"),  new ByteSlice("||"),  new ByteSlice("++"),  new ByteSlice("--"),  new ByteSlice("int32v"),  new ByteSlice("uns32v"),  new ByteSlice("int64v"),  new ByteSlice("uns64v"),  new ByteSlice("int128v"),  new ByteSlice("uns128v"),  new ByteSlice("float32v"),  new ByteSlice("float64v"),  new ByteSlice("float80v"),  new ByteSlice("imaginary32v"),  new ByteSlice("imaginary64v"),  new ByteSlice("imaginary80v"),  new ByteSlice("charv"),  new ByteSlice("wcharv"),  new ByteSlice("dcharv"),  new ByteSlice("identifier"),  new ByteSlice("string"),  new ByteSlice("xstring"),  new ByteSlice("this"),  new ByteSlice("super"),  new ByteSlice("halt"),  new ByteSlice("tuple"),  new ByteSlice("error"),  new ByteSlice("void"),  new ByteSlice("byte"),  new ByteSlice("ubyte"),  new ByteSlice("short"),  new ByteSlice("ushort"),  new ByteSlice("int"),  new ByteSlice("uint"),  new ByteSlice("long"),  new ByteSlice("ulong"),  new ByteSlice("cent"),  new ByteSlice("ucent"),  new ByteSlice("float"),  new ByteSlice("double"),  new ByteSlice("real"),  new ByteSlice("ifloat"),  new ByteSlice("idouble"),  new ByteSlice("ireal"),  new ByteSlice("cfloat"),  new ByteSlice("cdouble"),  new ByteSlice("creal"),  new ByteSlice("char"),  new ByteSlice("wchar"),  new ByteSlice("dchar"),  new ByteSlice("bool"),  new ByteSlice("struct"),  new ByteSlice("class"),  new ByteSlice("interface"),  new ByteSlice("union"),  new ByteSlice("enum"),  new ByteSlice("import"),  new ByteSlice("alias"),  new ByteSlice("override"),  new ByteSlice("delegate"),  new ByteSlice("function"),  new ByteSlice("mixin"),  new ByteSlice("align"),  new ByteSlice("extern"),  new ByteSlice("private"),  new ByteSlice("protected"),  new ByteSlice("public"),  new ByteSlice("export"),  new ByteSlice("static"),  new ByteSlice("final"),  new ByteSlice("const"),  new ByteSlice("abstract"),  new ByteSlice("debug"),  new ByteSlice("deprecated"),  new ByteSlice("in"),  new ByteSlice("out"),  new ByteSlice("inout"),  new ByteSlice("lazy"),  new ByteSlice("auto"),  new ByteSlice("package"),  new ByteSlice("manifest"),  new ByteSlice("immutable"),  new ByteSlice("if"),  new ByteSlice("else"),  new ByteSlice("while"),  new ByteSlice("for"),  new ByteSlice("do"),  new ByteSlice("switch"),  new ByteSlice("case"),  new ByteSlice("default"),  new ByteSlice("break"),  new ByteSlice("continue"),  new ByteSlice("with"),  new ByteSlice("synchronized"),  new ByteSlice("return"),  new ByteSlice("goto"),  new ByteSlice("try"),  new ByteSlice("catch"),  new ByteSlice("finally"),  new ByteSlice("asm"),  new ByteSlice("foreach"),  new ByteSlice("foreach_reverse"),  new ByteSlice("scope"),  new ByteSlice("scope(exit)"),  new ByteSlice("scope(failure)"),  new ByteSlice("scope(success)"),  new ByteSlice("invariant"),  new ByteSlice("unittest"),  new ByteSlice("__argTypes"),  new ByteSlice("ref"),  new ByteSlice("macro"),  new ByteSlice("__parameters"),  new ByteSlice("__traits"),  new ByteSlice("__overloadset"),  new ByteSlice("pure"),  new ByteSlice("nothrow"),  new ByteSlice("__gshared"),  new ByteSlice("__LINE__"),  new ByteSlice("__FILE__"),  new ByteSlice("__FILE_FULL_PATH__"),  new ByteSlice("__MODULE__"),  new ByteSlice("__FUNCTION__"),  new ByteSlice("__PRETTY_FUNCTION__"),  new ByteSlice("shared"),  new ByteSlice("@"),  new ByteSlice("^^"),  new ByteSlice("^^="),  new ByteSlice("=>"),  new ByteSlice("__vector"),  new ByteSlice("#"),  new ByteSlice("interval"),  new ByteSlice("voidexp"),  new ByteSlice("cantexp"),  new ByteSlice("showCtfeContext"),  new ByteSlice("class"),  new ByteSlice("vectorarray")};
    static ByteSlice toCharsbuffer = new ByteSlice(new byte[52]);


    public enum TOK 
    {
        reserved((byte)0),
        leftParentheses((byte)1),
        rightParentheses((byte)2),
        leftBracket((byte)3),
        rightBracket((byte)4),
        leftCurly((byte)5),
        rightCurly((byte)6),
        colon((byte)7),
        negate((byte)8),
        semicolon((byte)9),
        dotDotDot((byte)10),
        endOfFile((byte)11),
        cast_((byte)12),
        null_((byte)13),
        assert_((byte)14),
        true_((byte)15),
        false_((byte)16),
        array((byte)17),
        call((byte)18),
        address((byte)19),
        type((byte)20),
        throw_((byte)21),
        new_((byte)22),
        delete_((byte)23),
        star((byte)24),
        symbolOffset((byte)25),
        variable((byte)26),
        dotVariable((byte)27),
        dotIdentifier((byte)28),
        dotTemplateInstance((byte)29),
        dotType((byte)30),
        slice((byte)31),
        arrayLength((byte)32),
        version_((byte)33),
        module_((byte)34),
        dollar((byte)35),
        template_((byte)36),
        dotTemplateDeclaration((byte)37),
        declaration((byte)38),
        typeof_((byte)39),
        pragma_((byte)40),
        dSymbol((byte)41),
        typeid_((byte)42),
        uadd((byte)43),
        remove((byte)44),
        newAnonymousClass((byte)45),
        comment((byte)46),
        arrayLiteral((byte)47),
        assocArrayLiteral((byte)48),
        structLiteral((byte)49),
        classReference((byte)50),
        thrownException((byte)51),
        delegatePointer((byte)52),
        delegateFunctionPointer((byte)53),
        lessThan((byte)54),
        greaterThan((byte)55),
        lessOrEqual((byte)56),
        greaterOrEqual((byte)57),
        equal((byte)58),
        notEqual((byte)59),
        identity((byte)60),
        notIdentity((byte)61),
        index((byte)62),
        is_((byte)63),
        leftShift((byte)64),
        rightShift((byte)65),
        leftShiftAssign((byte)66),
        rightShiftAssign((byte)67),
        unsignedRightShift((byte)68),
        unsignedRightShiftAssign((byte)69),
        concatenate((byte)70),
        concatenateAssign((byte)71),
        concatenateElemAssign((byte)72),
        concatenateDcharAssign((byte)73),
        add((byte)74),
        min((byte)75),
        addAssign((byte)76),
        minAssign((byte)77),
        mul((byte)78),
        div((byte)79),
        mod((byte)80),
        mulAssign((byte)81),
        divAssign((byte)82),
        modAssign((byte)83),
        and((byte)84),
        or((byte)85),
        xor((byte)86),
        andAssign((byte)87),
        orAssign((byte)88),
        xorAssign((byte)89),
        assign((byte)90),
        not((byte)91),
        tilde((byte)92),
        plusPlus((byte)93),
        minusMinus((byte)94),
        construct((byte)95),
        blit((byte)96),
        dot((byte)97),
        arrow((byte)98),
        comma((byte)99),
        question((byte)100),
        andAnd((byte)101),
        orOr((byte)102),
        prePlusPlus((byte)103),
        preMinusMinus((byte)104),
        int32Literal((byte)105),
        uns32Literal((byte)106),
        int64Literal((byte)107),
        uns64Literal((byte)108),
        int128Literal((byte)109),
        uns128Literal((byte)110),
        float32Literal((byte)111),
        float64Literal((byte)112),
        float80Literal((byte)113),
        imaginary32Literal((byte)114),
        imaginary64Literal((byte)115),
        imaginary80Literal((byte)116),
        charLiteral((byte)117),
        wcharLiteral((byte)118),
        dcharLiteral((byte)119),
        identifier((byte)120),
        string_((byte)121),
        hexadecimalString((byte)122),
        this_((byte)123),
        super_((byte)124),
        halt((byte)125),
        tuple((byte)126),
        error((byte)127),
        void_((byte)128),
        int8((byte)129),
        uns8((byte)130),
        int16((byte)131),
        uns16((byte)132),
        int32((byte)133),
        uns32((byte)134),
        int64((byte)135),
        uns64((byte)136),
        int128((byte)137),
        uns128((byte)138),
        float32((byte)139),
        float64((byte)140),
        float80((byte)141),
        imaginary32((byte)142),
        imaginary64((byte)143),
        imaginary80((byte)144),
        complex32((byte)145),
        complex64((byte)146),
        complex80((byte)147),
        char_((byte)148),
        wchar_((byte)149),
        dchar_((byte)150),
        bool_((byte)151),
        struct_((byte)152),
        class_((byte)153),
        interface_((byte)154),
        union_((byte)155),
        enum_((byte)156),
        import_((byte)157),
        alias_((byte)158),
        override_((byte)159),
        delegate_((byte)160),
        function_((byte)161),
        mixin_((byte)162),
        align_((byte)163),
        extern_((byte)164),
        private_((byte)165),
        protected_((byte)166),
        public_((byte)167),
        export_((byte)168),
        static_((byte)169),
        final_((byte)170),
        const_((byte)171),
        abstract_((byte)172),
        debug_((byte)173),
        deprecated_((byte)174),
        in_((byte)175),
        out_((byte)176),
        inout_((byte)177),
        lazy_((byte)178),
        auto_((byte)179),
        package_((byte)180),
        manifest((byte)181),
        immutable_((byte)182),
        if_((byte)183),
        else_((byte)184),
        while_((byte)185),
        for_((byte)186),
        do_((byte)187),
        switch_((byte)188),
        case_((byte)189),
        default_((byte)190),
        break_((byte)191),
        continue_((byte)192),
        with_((byte)193),
        synchronized_((byte)194),
        return_((byte)195),
        goto_((byte)196),
        try_((byte)197),
        catch_((byte)198),
        finally_((byte)199),
        asm_((byte)200),
        foreach_((byte)201),
        foreach_reverse_((byte)202),
        scope_((byte)203),
        onScopeExit((byte)204),
        onScopeFailure((byte)205),
        onScopeSuccess((byte)206),
        invariant_((byte)207),
        unittest_((byte)208),
        argumentTypes((byte)209),
        ref_((byte)210),
        macro_((byte)211),
        parameters((byte)212),
        traits((byte)213),
        overloadSet((byte)214),
        pure_((byte)215),
        nothrow_((byte)216),
        gshared((byte)217),
        line((byte)218),
        file((byte)219),
        fileFullPath((byte)220),
        moduleString((byte)221),
        functionString((byte)222),
        prettyFunction((byte)223),
        shared_((byte)224),
        at((byte)225),
        pow((byte)226),
        powAssign((byte)227),
        goesTo((byte)228),
        vector((byte)229),
        pound((byte)230),
        interval((byte)231),
        voidExpression((byte)232),
        cantExpression((byte)233),
        showCtfeContext((byte)234),
        objcClassReference((byte)235),
        vectorArray((byte)236),
        max_((byte)237),
        ;
        public byte value;
        TOK(byte value){ this.value = value; }
    }

    static Slice<TOK> keywords = slice(initializer_0);
    public static class Token
    {
        public Token next;
        public Loc loc;
        public BytePtr ptr;
        public TOK value;
        public BytePtr blockComment;
        public BytePtr lineComment;
        public long intvalue;
        public long unsvalue;
        public double floatvalue;
        public BytePtr ustring;
        public int len;
        public byte postfix;
        public Identifier ident;
        public static Slice<ByteSlice> tochars = slice(initializer_1);
        {
            Identifier.initTable();
            {
                Slice<TOK> __r51 = keywords;
                int __key52 = 0;
                for (; __key52 < __r51.getLength();__key52 += 1) {
                    TOK kw = __r51.get(__key52);
                    Identifier.idPool(tochars.get(kw.value).toBytePtr(), tochars.get(kw.value).getLength(), kw.value);
                }
            }
        }
        public  int isKeyword() {
            {
                Slice<TOK> __r53 = keywords;
                int __key54 = 0;
                for (; __key54 < __r53.getLength();__key54 += 1) {
                    TOK kw = __r53.get(__key54);
                    if (kw.value == this.value.value)
                        return 1;
                }
            }
            return 0;
        }

        public  void setString(BytePtr ptr, int length) {
            BytePtr s = Mem.xmalloc(length + 1).toBytePtr();
            memcpy(s, ptr, length);
            s.set(length, (byte)0);
            this.ustring = s;
            this.len = length;
            this.postfix = (byte)0;
        }

        public  void setString(OutBuffer buf) {
            this.setString(buf.data.toBytePtr(), buf.offset);
        }

        public  void setString() {
            this.ustring = new BytePtr("");
            this.len = 0;
            this.postfix = (byte)0;
        }

        public  BytePtr toChars() {
            ByteSlice buffer = tokens.toCharsbuffer;
            BytePtr p = buffer.ptr();
            switch (this.value.value)
            {
                case (byte)105:
                    sprintf(buffer.ptr(),  new ByteSlice("%d"), (int)this.intvalue);
                    break;
                case (byte)106:
                case (byte)117:
                case (byte)118:
                case (byte)119:
                    sprintf(buffer.ptr(),  new ByteSlice("%uU"), (int)this.unsvalue);
                    break;
                case (byte)107:
                    sprintf(buffer.ptr(),  new ByteSlice("%lldL"), this.intvalue);
                    break;
                case (byte)108:
                    sprintf(buffer.ptr(),  new ByteSlice("%lluUL"), this.unsvalue);
                    break;
                case (byte)111:
                    CTFloat.sprint(buffer.ptr(), (byte)103, this.floatvalue);
                    strcat(buffer.ptr(),  new ByteSlice("f"));
                    break;
                case (byte)112:
                    CTFloat.sprint(buffer.ptr(), (byte)103, this.floatvalue);
                    break;
                case (byte)113:
                    CTFloat.sprint(buffer.ptr(), (byte)103, this.floatvalue);
                    strcat(buffer.ptr(),  new ByteSlice("L"));
                    break;
                case (byte)114:
                    CTFloat.sprint(buffer.ptr(), (byte)103, this.floatvalue);
                    strcat(buffer.ptr(),  new ByteSlice("fi"));
                    break;
                case (byte)115:
                    CTFloat.sprint(buffer.ptr(), (byte)103, this.floatvalue);
                    strcat(buffer.ptr(),  new ByteSlice("i"));
                    break;
                case (byte)116:
                    CTFloat.sprint(buffer.ptr(), (byte)103, this.floatvalue);
                    strcat(buffer.ptr(),  new ByteSlice("Li"));
                    break;
                case (byte)121:
                    {
                        OutBuffer buf = new OutBuffer();
                        buf.writeByte(34);
                        {
                            IntRef i = ref(0);
                            for (; i.value < this.len;){
                                IntRef c = ref('\uffff');
                                utf_decodeChar(this.ustring, this.len, i, c);
                                switch (c.value)
                                {
                                    case 0:
                                        break;
                                    case 34:
                                    case 92:
                                        buf.writeByte(92);
                                        //goto default;
                                        {
                                            if (c.value <= 127)
                                            {
                                                if ((isprint(c.value)) != 0)
                                                    buf.writeByte(c.value);
                                                else
                                                    buf.printf( new ByteSlice("\\x%02x"), c.value);
                                            }
                                            else if (c.value <= 65535)
                                                buf.printf( new ByteSlice("\\u%04x"), c.value);
                                            else
                                                buf.printf( new ByteSlice("\\U%08x"), c.value);
                                            continue;
                                        }
                                    default:
                                    {
                                        if (c.value <= 127)
                                        {
                                            if ((isprint(c.value)) != 0)
                                                buf.writeByte(c.value);
                                            else
                                                buf.printf( new ByteSlice("\\x%02x"), c.value);
                                        }
                                        else if (c.value <= 65535)
                                            buf.printf( new ByteSlice("\\u%04x"), c.value);
                                        else
                                            buf.printf( new ByteSlice("\\U%08x"), c.value);
                                        continue;
                                    }
                                }
                                break;
                            }
                        }
                        buf.writeByte(34);
                        if ((this.postfix) != 0)
                            buf.writeByte((int)this.postfix);
                        p = buf.extractChars();
                    }
                    break;
                case (byte)122:
                    {
                        OutBuffer buf = new OutBuffer();
                        buf.writeByte(120);
                        buf.writeByte(34);
                        {
                            int __key55 = 0;
                            int __limit56 = this.len;
                            for (; __key55 < __limit56;__key55 += 1) {
                                IntRef i = ref(__key55);
                                if ((i.value) != 0)
                                    buf.writeByte(32);
                                buf.printf( new ByteSlice("%02x"), (int)this.ustring.get(i.value));
                            }
                        }
                        buf.writeByte(34);
                        if ((this.postfix) != 0)
                            buf.writeByte((int)this.postfix);
                        buf.writeByte(0);
                        p = buf.extractData();
                        break;
                    }
                case (byte)120:
                case (byte)156:
                case (byte)152:
                case (byte)157:
                case (byte)149:
                case (byte)150:
                case (byte)151:
                case (byte)148:
                case (byte)129:
                case (byte)130:
                case (byte)131:
                case (byte)132:
                case (byte)133:
                case (byte)134:
                case (byte)135:
                case (byte)136:
                case (byte)137:
                case (byte)138:
                case (byte)139:
                case (byte)140:
                case (byte)141:
                case (byte)142:
                case (byte)143:
                case (byte)144:
                case (byte)145:
                case (byte)146:
                case (byte)147:
                case (byte)128:
                    p = this.ident.toChars();
                    break;
                default:
                {
                    p = Token.toChars(this.value);
                    break;
                }
            }
            return p;
        }

        public static BytePtr toChars(TOK value) {
            return Token.asString(value).toBytePtr();
        }

        public static ByteSlice asString(TOK value) {
            return tochars.get(value.value);
        }

        public Token(){}
        public Token(Token next, Loc loc, BytePtr ptr, TOK value, BytePtr blockComment, BytePtr lineComment, long intvalue, long unsvalue, double floatvalue, BytePtr ustring, int len, byte postfix, Identifier ident) {
            this.next = next;
            this.loc = loc;
            this.ptr = ptr;
            this.value = value;
            this.blockComment = blockComment;
            this.lineComment = lineComment;
            this.intvalue = intvalue;
            this.unsvalue = unsvalue;
            this.floatvalue = floatvalue;
            this.ustring = ustring;
            this.len = len;
            this.postfix = postfix;
            this.ident = ident;
        }

        public Token opAssign(Token that) {
            this.next = that.next;
            this.loc = that.loc;
            this.ptr = that.ptr;
            this.value = that.value;
            this.blockComment = that.blockComment;
            this.lineComment = that.lineComment;
            this.intvalue = that.intvalue;
            this.unsvalue = that.unsvalue;
            this.floatvalue = that.floatvalue;
            this.ustring = that.ustring;
            this.len = that.len;
            this.postfix = that.postfix;
            this.ident = that.ident;
            return this;
        }
    }
}
