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
    private static final byte[] initializer_0 = {TOK.this_, TOK.super_, TOK.assert_, TOK.null_, TOK.true_, TOK.false_, TOK.cast_, TOK.new_, TOK.delete_, TOK.throw_, TOK.module_, TOK.pragma_, TOK.typeof_, TOK.typeid_, TOK.template_, TOK.void_, TOK.int8, TOK.uns8, TOK.int16, TOK.uns16, TOK.int32, TOK.uns32, TOK.int64, TOK.uns64, TOK.int128, TOK.uns128, TOK.float32, TOK.float64, TOK.float80, TOK.bool_, TOK.char_, TOK.wchar_, TOK.dchar_, TOK.imaginary32, TOK.imaginary64, TOK.imaginary80, TOK.complex32, TOK.complex64, TOK.complex80, TOK.delegate_, TOK.function_, TOK.is_, TOK.if_, TOK.else_, TOK.while_, TOK.for_, TOK.do_, TOK.switch_, TOK.case_, TOK.default_, TOK.break_, TOK.continue_, TOK.synchronized_, TOK.return_, TOK.goto_, TOK.try_, TOK.catch_, TOK.finally_, TOK.with_, TOK.asm_, TOK.foreach_, TOK.foreach_reverse_, TOK.scope_, TOK.struct_, TOK.class_, TOK.interface_, TOK.union_, TOK.enum_, TOK.import_, TOK.mixin_, TOK.static_, TOK.final_, TOK.const_, TOK.alias_, TOK.override_, TOK.abstract_, TOK.debug_, TOK.deprecated_, TOK.in_, TOK.out_, TOK.inout_, TOK.lazy_, TOK.auto_, TOK.align_, TOK.extern_, TOK.private_, TOK.package_, TOK.protected_, TOK.public_, TOK.export_, TOK.invariant_, TOK.unittest_, TOK.version_, TOK.argumentTypes, TOK.parameters, TOK.ref_, TOK.macro_, TOK.pure_, TOK.nothrow_, TOK.gshared, TOK.traits, TOK.vector, TOK.overloadSet, TOK.file, TOK.fileFullPath, TOK.line, TOK.moduleString, TOK.functionString, TOK.prettyFunction, TOK.shared_, TOK.immutable_};
    private static final ByteSlice[] initializer_1 = {new ByteSlice("reserved"), new ByteSlice("("), new ByteSlice(")"), new ByteSlice("["), new ByteSlice("]"), new ByteSlice("{"), new ByteSlice("}"), new ByteSlice(":"), new ByteSlice("-"), new ByteSlice(";"), new ByteSlice("..."), new ByteSlice("End of File"), new ByteSlice("cast"), new ByteSlice("null"), new ByteSlice("assert"), new ByteSlice("true"), new ByteSlice("false"), new ByteSlice("[]"), new ByteSlice("call"), new ByteSlice("&"), new ByteSlice("type"), new ByteSlice("throw"), new ByteSlice("new"), new ByteSlice("delete"), new ByteSlice("*"), new ByteSlice("symoff"), new ByteSlice("var"), new ByteSlice("dotvar"), new ByteSlice("dotid"), new ByteSlice("dotti"), new ByteSlice("dottype"), new ByteSlice(".."), new ByteSlice("arraylength"), new ByteSlice("version"), new ByteSlice("module"), new ByteSlice("$"), new ByteSlice("template"), new ByteSlice("dottd"), new ByteSlice("declaration"), new ByteSlice("typeof"), new ByteSlice("pragma"), new ByteSlice("symbol"), new ByteSlice("typeid"), new ByteSlice("+"), new ByteSlice("remove"), new ByteSlice("newanonclass"), new ByteSlice("comment"), new ByteSlice("arrayliteral"), new ByteSlice("assocarrayliteral"), new ByteSlice("structliteral"), new ByteSlice("classreference"), new ByteSlice("thrownexception"), new ByteSlice("delegateptr"), new ByteSlice("delegatefuncptr"), new ByteSlice("<"), new ByteSlice(">"), new ByteSlice("<="), new ByteSlice(">="), new ByteSlice("=="), new ByteSlice("!="), new ByteSlice("is"), new ByteSlice("!is"), new ByteSlice("[i]"), new ByteSlice("is"), new ByteSlice("<<"), new ByteSlice(">>"), new ByteSlice("<<="), new ByteSlice(">>="), new ByteSlice(">>>"), new ByteSlice(">>>="), new ByteSlice("~"), new ByteSlice("~="), new ByteSlice("~="), new ByteSlice("~="), new ByteSlice("+"), new ByteSlice("-"), new ByteSlice("+="), new ByteSlice("-="), new ByteSlice("*"), new ByteSlice("/"), new ByteSlice("%"), new ByteSlice("*="), new ByteSlice("/="), new ByteSlice("%="), new ByteSlice("&"), new ByteSlice("|"), new ByteSlice("^"), new ByteSlice("&="), new ByteSlice("|="), new ByteSlice("^="), new ByteSlice("="), new ByteSlice("!"), new ByteSlice("~"), new ByteSlice("++"), new ByteSlice("--"), new ByteSlice("="), new ByteSlice("="), new ByteSlice("."), new ByteSlice("arrow"), new ByteSlice(","), new ByteSlice("?"), new ByteSlice("&&"), new ByteSlice("||"), new ByteSlice("++"), new ByteSlice("--"), new ByteSlice("int32v"), new ByteSlice("uns32v"), new ByteSlice("int64v"), new ByteSlice("uns64v"), new ByteSlice("int128v"), new ByteSlice("uns128v"), new ByteSlice("float32v"), new ByteSlice("float64v"), new ByteSlice("float80v"), new ByteSlice("imaginary32v"), new ByteSlice("imaginary64v"), new ByteSlice("imaginary80v"), new ByteSlice("charv"), new ByteSlice("wcharv"), new ByteSlice("dcharv"), new ByteSlice("identifier"), new ByteSlice("string"), new ByteSlice("xstring"), new ByteSlice("this"), new ByteSlice("super"), new ByteSlice("halt"), new ByteSlice("tuple"), new ByteSlice("error"), new ByteSlice("void"), new ByteSlice("byte"), new ByteSlice("ubyte"), new ByteSlice("short"), new ByteSlice("ushort"), new ByteSlice("int"), new ByteSlice("uint"), new ByteSlice("long"), new ByteSlice("ulong"), new ByteSlice("cent"), new ByteSlice("ucent"), new ByteSlice("float"), new ByteSlice("double"), new ByteSlice("real"), new ByteSlice("ifloat"), new ByteSlice("idouble"), new ByteSlice("ireal"), new ByteSlice("cfloat"), new ByteSlice("cdouble"), new ByteSlice("creal"), new ByteSlice("char"), new ByteSlice("wchar"), new ByteSlice("dchar"), new ByteSlice("bool"), new ByteSlice("struct"), new ByteSlice("class"), new ByteSlice("interface"), new ByteSlice("union"), new ByteSlice("enum"), new ByteSlice("import"), new ByteSlice("alias"), new ByteSlice("override"), new ByteSlice("delegate"), new ByteSlice("function"), new ByteSlice("mixin"), new ByteSlice("align"), new ByteSlice("extern"), new ByteSlice("private"), new ByteSlice("protected"), new ByteSlice("public"), new ByteSlice("export"), new ByteSlice("static"), new ByteSlice("final"), new ByteSlice("const"), new ByteSlice("abstract"), new ByteSlice("debug"), new ByteSlice("deprecated"), new ByteSlice("in"), new ByteSlice("out"), new ByteSlice("inout"), new ByteSlice("lazy"), new ByteSlice("auto"), new ByteSlice("package"), new ByteSlice("manifest"), new ByteSlice("immutable"), new ByteSlice("if"), new ByteSlice("else"), new ByteSlice("while"), new ByteSlice("for"), new ByteSlice("do"), new ByteSlice("switch"), new ByteSlice("case"), new ByteSlice("default"), new ByteSlice("break"), new ByteSlice("continue"), new ByteSlice("with"), new ByteSlice("synchronized"), new ByteSlice("return"), new ByteSlice("goto"), new ByteSlice("try"), new ByteSlice("catch"), new ByteSlice("finally"), new ByteSlice("asm"), new ByteSlice("foreach"), new ByteSlice("foreach_reverse"), new ByteSlice("scope"), new ByteSlice("scope(exit)"), new ByteSlice("scope(failure)"), new ByteSlice("scope(success)"), new ByteSlice("invariant"), new ByteSlice("unittest"), new ByteSlice("__argTypes"), new ByteSlice("ref"), new ByteSlice("macro"), new ByteSlice("__parameters"), new ByteSlice("__traits"), new ByteSlice("__overloadset"), new ByteSlice("pure"), new ByteSlice("nothrow"), new ByteSlice("__gshared"), new ByteSlice("__LINE__"), new ByteSlice("__FILE__"), new ByteSlice("__FILE_FULL_PATH__"), new ByteSlice("__MODULE__"), new ByteSlice("__FUNCTION__"), new ByteSlice("__PRETTY_FUNCTION__"), new ByteSlice("shared"), new ByteSlice("@"), new ByteSlice("^^"), new ByteSlice("^^="), new ByteSlice("=>"), new ByteSlice("__vector"), new ByteSlice("#"), new ByteSlice("interval"), new ByteSlice("voidexp"), new ByteSlice("cantexp"), new ByteSlice("showCtfeContext"), new ByteSlice("class"), new ByteSlice("vectorarray")};
    static ByteSlice toCharsbuffer = new ByteSlice(new byte[52]);


    public static class TOK 
    {
        public static final byte reserved = (byte)0;
        public static final byte leftParentheses = (byte)1;
        public static final byte rightParentheses = (byte)2;
        public static final byte leftBracket = (byte)3;
        public static final byte rightBracket = (byte)4;
        public static final byte leftCurly = (byte)5;
        public static final byte rightCurly = (byte)6;
        public static final byte colon = (byte)7;
        public static final byte negate = (byte)8;
        public static final byte semicolon = (byte)9;
        public static final byte dotDotDot = (byte)10;
        public static final byte endOfFile = (byte)11;
        public static final byte cast_ = (byte)12;
        public static final byte null_ = (byte)13;
        public static final byte assert_ = (byte)14;
        public static final byte true_ = (byte)15;
        public static final byte false_ = (byte)16;
        public static final byte array = (byte)17;
        public static final byte call = (byte)18;
        public static final byte address = (byte)19;
        public static final byte type = (byte)20;
        public static final byte throw_ = (byte)21;
        public static final byte new_ = (byte)22;
        public static final byte delete_ = (byte)23;
        public static final byte star = (byte)24;
        public static final byte symbolOffset = (byte)25;
        public static final byte variable = (byte)26;
        public static final byte dotVariable = (byte)27;
        public static final byte dotIdentifier = (byte)28;
        public static final byte dotTemplateInstance = (byte)29;
        public static final byte dotType = (byte)30;
        public static final byte slice = (byte)31;
        public static final byte arrayLength = (byte)32;
        public static final byte version_ = (byte)33;
        public static final byte module_ = (byte)34;
        public static final byte dollar = (byte)35;
        public static final byte template_ = (byte)36;
        public static final byte dotTemplateDeclaration = (byte)37;
        public static final byte declaration = (byte)38;
        public static final byte typeof_ = (byte)39;
        public static final byte pragma_ = (byte)40;
        public static final byte dSymbol = (byte)41;
        public static final byte typeid_ = (byte)42;
        public static final byte uadd = (byte)43;
        public static final byte remove = (byte)44;
        public static final byte newAnonymousClass = (byte)45;
        public static final byte comment = (byte)46;
        public static final byte arrayLiteral = (byte)47;
        public static final byte assocArrayLiteral = (byte)48;
        public static final byte structLiteral = (byte)49;
        public static final byte classReference = (byte)50;
        public static final byte thrownException = (byte)51;
        public static final byte delegatePointer = (byte)52;
        public static final byte delegateFunctionPointer = (byte)53;
        public static final byte lessThan = (byte)54;
        public static final byte greaterThan = (byte)55;
        public static final byte lessOrEqual = (byte)56;
        public static final byte greaterOrEqual = (byte)57;
        public static final byte equal = (byte)58;
        public static final byte notEqual = (byte)59;
        public static final byte identity = (byte)60;
        public static final byte notIdentity = (byte)61;
        public static final byte index = (byte)62;
        public static final byte is_ = (byte)63;
        public static final byte leftShift = (byte)64;
        public static final byte rightShift = (byte)65;
        public static final byte leftShiftAssign = (byte)66;
        public static final byte rightShiftAssign = (byte)67;
        public static final byte unsignedRightShift = (byte)68;
        public static final byte unsignedRightShiftAssign = (byte)69;
        public static final byte concatenate = (byte)70;
        public static final byte concatenateAssign = (byte)71;
        public static final byte concatenateElemAssign = (byte)72;
        public static final byte concatenateDcharAssign = (byte)73;
        public static final byte add = (byte)74;
        public static final byte min = (byte)75;
        public static final byte addAssign = (byte)76;
        public static final byte minAssign = (byte)77;
        public static final byte mul = (byte)78;
        public static final byte div = (byte)79;
        public static final byte mod = (byte)80;
        public static final byte mulAssign = (byte)81;
        public static final byte divAssign = (byte)82;
        public static final byte modAssign = (byte)83;
        public static final byte and = (byte)84;
        public static final byte or = (byte)85;
        public static final byte xor = (byte)86;
        public static final byte andAssign = (byte)87;
        public static final byte orAssign = (byte)88;
        public static final byte xorAssign = (byte)89;
        public static final byte assign = (byte)90;
        public static final byte not = (byte)91;
        public static final byte tilde = (byte)92;
        public static final byte plusPlus = (byte)93;
        public static final byte minusMinus = (byte)94;
        public static final byte construct = (byte)95;
        public static final byte blit = (byte)96;
        public static final byte dot = (byte)97;
        public static final byte arrow = (byte)98;
        public static final byte comma = (byte)99;
        public static final byte question = (byte)100;
        public static final byte andAnd = (byte)101;
        public static final byte orOr = (byte)102;
        public static final byte prePlusPlus = (byte)103;
        public static final byte preMinusMinus = (byte)104;
        public static final byte int32Literal = (byte)105;
        public static final byte uns32Literal = (byte)106;
        public static final byte int64Literal = (byte)107;
        public static final byte uns64Literal = (byte)108;
        public static final byte int128Literal = (byte)109;
        public static final byte uns128Literal = (byte)110;
        public static final byte float32Literal = (byte)111;
        public static final byte float64Literal = (byte)112;
        public static final byte float80Literal = (byte)113;
        public static final byte imaginary32Literal = (byte)114;
        public static final byte imaginary64Literal = (byte)115;
        public static final byte imaginary80Literal = (byte)116;
        public static final byte charLiteral = (byte)117;
        public static final byte wcharLiteral = (byte)118;
        public static final byte dcharLiteral = (byte)119;
        public static final byte identifier = (byte)120;
        public static final byte string_ = (byte)121;
        public static final byte hexadecimalString = (byte)122;
        public static final byte this_ = (byte)123;
        public static final byte super_ = (byte)124;
        public static final byte halt = (byte)125;
        public static final byte tuple = (byte)126;
        public static final byte error = (byte)127;
        public static final byte void_ = (byte)128;
        public static final byte int8 = (byte)129;
        public static final byte uns8 = (byte)130;
        public static final byte int16 = (byte)131;
        public static final byte uns16 = (byte)132;
        public static final byte int32 = (byte)133;
        public static final byte uns32 = (byte)134;
        public static final byte int64 = (byte)135;
        public static final byte uns64 = (byte)136;
        public static final byte int128 = (byte)137;
        public static final byte uns128 = (byte)138;
        public static final byte float32 = (byte)139;
        public static final byte float64 = (byte)140;
        public static final byte float80 = (byte)141;
        public static final byte imaginary32 = (byte)142;
        public static final byte imaginary64 = (byte)143;
        public static final byte imaginary80 = (byte)144;
        public static final byte complex32 = (byte)145;
        public static final byte complex64 = (byte)146;
        public static final byte complex80 = (byte)147;
        public static final byte char_ = (byte)148;
        public static final byte wchar_ = (byte)149;
        public static final byte dchar_ = (byte)150;
        public static final byte bool_ = (byte)151;
        public static final byte struct_ = (byte)152;
        public static final byte class_ = (byte)153;
        public static final byte interface_ = (byte)154;
        public static final byte union_ = (byte)155;
        public static final byte enum_ = (byte)156;
        public static final byte import_ = (byte)157;
        public static final byte alias_ = (byte)158;
        public static final byte override_ = (byte)159;
        public static final byte delegate_ = (byte)160;
        public static final byte function_ = (byte)161;
        public static final byte mixin_ = (byte)162;
        public static final byte align_ = (byte)163;
        public static final byte extern_ = (byte)164;
        public static final byte private_ = (byte)165;
        public static final byte protected_ = (byte)166;
        public static final byte public_ = (byte)167;
        public static final byte export_ = (byte)168;
        public static final byte static_ = (byte)169;
        public static final byte final_ = (byte)170;
        public static final byte const_ = (byte)171;
        public static final byte abstract_ = (byte)172;
        public static final byte debug_ = (byte)173;
        public static final byte deprecated_ = (byte)174;
        public static final byte in_ = (byte)175;
        public static final byte out_ = (byte)176;
        public static final byte inout_ = (byte)177;
        public static final byte lazy_ = (byte)178;
        public static final byte auto_ = (byte)179;
        public static final byte package_ = (byte)180;
        public static final byte manifest = (byte)181;
        public static final byte immutable_ = (byte)182;
        public static final byte if_ = (byte)183;
        public static final byte else_ = (byte)184;
        public static final byte while_ = (byte)185;
        public static final byte for_ = (byte)186;
        public static final byte do_ = (byte)187;
        public static final byte switch_ = (byte)188;
        public static final byte case_ = (byte)189;
        public static final byte default_ = (byte)190;
        public static final byte break_ = (byte)191;
        public static final byte continue_ = (byte)192;
        public static final byte with_ = (byte)193;
        public static final byte synchronized_ = (byte)194;
        public static final byte return_ = (byte)195;
        public static final byte goto_ = (byte)196;
        public static final byte try_ = (byte)197;
        public static final byte catch_ = (byte)198;
        public static final byte finally_ = (byte)199;
        public static final byte asm_ = (byte)200;
        public static final byte foreach_ = (byte)201;
        public static final byte foreach_reverse_ = (byte)202;
        public static final byte scope_ = (byte)203;
        public static final byte onScopeExit = (byte)204;
        public static final byte onScopeFailure = (byte)205;
        public static final byte onScopeSuccess = (byte)206;
        public static final byte invariant_ = (byte)207;
        public static final byte unittest_ = (byte)208;
        public static final byte argumentTypes = (byte)209;
        public static final byte ref_ = (byte)210;
        public static final byte macro_ = (byte)211;
        public static final byte parameters = (byte)212;
        public static final byte traits = (byte)213;
        public static final byte overloadSet = (byte)214;
        public static final byte pure_ = (byte)215;
        public static final byte nothrow_ = (byte)216;
        public static final byte gshared = (byte)217;
        public static final byte line = (byte)218;
        public static final byte file = (byte)219;
        public static final byte fileFullPath = (byte)220;
        public static final byte moduleString = (byte)221;
        public static final byte functionString = (byte)222;
        public static final byte prettyFunction = (byte)223;
        public static final byte shared_ = (byte)224;
        public static final byte at = (byte)225;
        public static final byte pow = (byte)226;
        public static final byte powAssign = (byte)227;
        public static final byte goesTo = (byte)228;
        public static final byte vector = (byte)229;
        public static final byte pound = (byte)230;
        public static final byte interval = (byte)231;
        public static final byte voidExpression = (byte)232;
        public static final byte cantExpression = (byte)233;
        public static final byte showCtfeContext = (byte)234;
        public static final byte objcClassReference = (byte)235;
        public static final byte vectorArray = (byte)236;
        public static final byte max_ = (byte)237;
    }

    static ByteSlice keywords = slice(initializer_0);
    public static class Token implements LinkedNode<Token>
    {
        public Token next;
        public Loc loc = new Loc();
        public BytePtr ptr;
        public byte value;
        public BytePtr blockComment;
        public BytePtr lineComment;
        public long intvalue;
        public double floatvalue;
        public BytePtr ustring;
        public int len;
        public byte postfix;
        public Identifier ident;
        public static Slice<ByteSlice> tochars = slice(initializer_1);
        static {
            Identifier.initTable();
            {
                ByteSlice __r110 = keywords.copy();
                int __key111 = 0;
                for (; __key111 < __r110.getLength();__key111 += 1) {
                    byte kw = __r110.get(__key111);
                    Identifier.idPool(toBytePtr(tochars.get((kw & 0xFF))), tochars.get((kw & 0xFF)).getLength(), (kw & 0xFF));
                }
            }
        }
        public  int isKeyword() {
            {
                ByteSlice __r112 = keywords.copy();
                int __key113 = 0;
                for (; __key113 < __r112.getLength();__key113 += 1) {
                    byte kw = __r112.get(__key113);
                    if ((kw & 0xFF) == (this.value & 0xFF))
                        return 1;
                }
            }
            return 0;
        }

        public  void setString(BytePtr ptr, int length) {
            BytePtr s = pcopy(toBytePtr(Mem.xmalloc(length + 1)));
            memcpy((BytePtr)(s), (ptr), length);
            s.set(length, (byte)0);
            this.ustring = pcopy(s);
            this.len = length;
            this.postfix = (byte)0;
        }

        public  void setString(OutBuffer buf) {
            this.setString(toBytePtr(buf.data), buf.offset);
        }

        public  void setString() {
            this.ustring = pcopy(new BytePtr(""));
            this.len = 0;
            this.postfix = (byte)0;
        }

        public  BytePtr toChars() {
            BytePtr p = pcopy(ptr(tokens.toCharsbuffer));
            {
                int __dispatch0 = 0;
                dispatched_0:
                do {
                    switch (__dispatch0 != 0 ? __dispatch0 : (this.value & 0xFF))
                    {
                        case 105:
                            sprintf(ptr(tokens.toCharsbuffer), new BytePtr("%d"), (int)this.intvalue);
                            break;
                        case 106:
                        case 117:
                        case 118:
                        case 119:
                            sprintf(ptr(tokens.toCharsbuffer), new BytePtr("%uU"), (int)this.intvalue);
                            break;
                        case 107:
                            sprintf(ptr(tokens.toCharsbuffer), new BytePtr("%lldL"), this.intvalue);
                            break;
                        case 108:
                            sprintf(ptr(tokens.toCharsbuffer), new BytePtr("%lluUL"), this.intvalue);
                            break;
                        case 111:
                            CTFloat.sprint(ptr(tokens.toCharsbuffer), (byte)103, this.floatvalue);
                            strcat(ptr(tokens.toCharsbuffer), new BytePtr("f"));
                            break;
                        case 112:
                            CTFloat.sprint(ptr(tokens.toCharsbuffer), (byte)103, this.floatvalue);
                            break;
                        case 113:
                            CTFloat.sprint(ptr(tokens.toCharsbuffer), (byte)103, this.floatvalue);
                            strcat(ptr(tokens.toCharsbuffer), new BytePtr("L"));
                            break;
                        case 114:
                            CTFloat.sprint(ptr(tokens.toCharsbuffer), (byte)103, this.floatvalue);
                            strcat(ptr(tokens.toCharsbuffer), new BytePtr("fi"));
                            break;
                        case 115:
                            CTFloat.sprint(ptr(tokens.toCharsbuffer), (byte)103, this.floatvalue);
                            strcat(ptr(tokens.toCharsbuffer), new BytePtr("i"));
                            break;
                        case 116:
                            CTFloat.sprint(ptr(tokens.toCharsbuffer), (byte)103, this.floatvalue);
                            strcat(ptr(tokens.toCharsbuffer), new BytePtr("Li"));
                            break;
                        case 121:
                            {
                                OutBuffer buf_1 = new OutBuffer();
                                try {
                                    buf_1.writeByte(34);
                                    {
                                        IntRef i = ref(0);
                                    L_outer1:
                                        for (; i.value < this.len;){
                                            IntRef c = ref(0x0ffff);
                                            utf_decodeChar(this.ustring, this.len, i, c);
                                            {
                                                int __dispatch1 = 0;
                                                dispatched_1:
                                                do {
                                                    switch (__dispatch1 != 0 ? __dispatch1 : c.value)
                                                    {
                                                        case 0:
                                                            break;
                                                        case 34:
                                                        case 92:
                                                            buf_1.writeByte(92);
                                                            /*goto default*/ { __dispatch1 = -1; continue dispatched_1; }
                                                        default:
                                                        if (c.value <= 127)
                                                        {
                                                            if ((isprint(c.value)) != 0)
                                                                buf_1.writeByte(c.value);
                                                            else
                                                                buf_1.printf(new BytePtr("\\x%02x"), c.value);
                                                        }
                                                        else if (c.value <= 65535)
                                                            buf_1.printf(new BytePtr("\\u%04x"), c.value);
                                                        else
                                                            buf_1.printf(new BytePtr("\\U%08x"), c.value);
                                                        continue L_outer1;
                                                    }
                                                } while(__dispatch1 != 0);
                                            }
                                            break;
                                        }
                                    }
                                    buf_1.writeByte(34);
                                    if ((this.postfix) != 0)
                                        buf_1.writeByte((this.postfix & 0xFF));
                                    p = pcopy(buf_1.extractChars());
                                }
                                finally {
                                }
                            }
                            break;
                        case 122:
                            OutBuffer buf = new OutBuffer();
                            try {
                                buf.writeByte(120);
                                buf.writeByte(34);
                                {
                                    int __key114 = 0;
                                    int __limit115 = this.len;
                                    for (; __key114 < __limit115;__key114 += 1) {
                                        int i_1 = __key114;
                                        if ((i_1) != 0)
                                            buf.writeByte(32);
                                        buf.printf(new BytePtr("%02x"), (this.ustring.get(i_1) & 0xFF));
                                    }
                                }
                                buf.writeByte(34);
                                if ((this.postfix) != 0)
                                    buf.writeByte((this.postfix & 0xFF));
                                buf.writeByte(0);
                                p = pcopy(buf.extractData());
                                break;
                            }
                            finally {
                            }
                        case 120:
                        case 156:
                        case 152:
                        case 157:
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
                            p = pcopy(this.ident.toChars());
                            break;
                        default:
                        p = pcopy(toChars(this.value));
                        break;
                    }
                } while(__dispatch0 != 0);
            }
            return p;
        }

        public static BytePtr toChars(byte value) {
            return toBytePtr(asString(value));
        }

        public static ByteSlice asString(byte value) {
            return tochars.get((value & 0xFF));
        }

        public Token(){
            loc = new Loc();
        }
        public Token copy(){
            Token r = new Token();
            r.next = next;
            r.loc = loc.copy();
            r.ptr = ptr;
            r.value = value;
            r.blockComment = blockComment;
            r.lineComment = lineComment;
            r.intvalue = intvalue;
            r.floatvalue = floatvalue;
            r.ustring = ustring;
            r.len = len;
            r.postfix = postfix;
            r.ident = ident;
            return r;
        }
        public Token opAssign(Token that) {
            this.next = that.next;
            this.loc = that.loc;
            this.ptr = that.ptr;
            this.value = that.value;
            this.blockComment = that.blockComment;
            this.lineComment = that.lineComment;
            this.intvalue = that.intvalue;
            this.floatvalue = that.floatvalue;
            this.ustring = that.ustring;
            this.len = that.len;
            this.postfix = that.postfix;
            this.ident = that.ident;
            return this;
        }
        public void setNext(Token value) { next = value; }
        public Token getNext() { return next; }
    }
}
