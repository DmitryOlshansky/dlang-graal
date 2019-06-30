package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.parsetimevisitor.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utils.*;

public class astbase {
    private static final byte[] initializer_0 = {(byte)12, (byte)13, (byte)14, (byte)15, (byte)16, (byte)17, (byte)18, (byte)19, (byte)20, (byte)42, (byte)43, (byte)21, (byte)22, (byte)23, (byte)24, (byte)25, (byte)26, (byte)27, (byte)28, (byte)29, (byte)30, (byte)31, (byte)32, (byte)33, (byte)34};
    private static final SCstring[] initializer_1 = {new SCstring(256L, TOK.auto_, null), new SCstring(524288L, TOK.scope_, null), new SCstring(1L, TOK.static_, null), new SCstring(2L, TOK.extern_, null), new SCstring(4L, TOK.const_, null), new SCstring(8L, TOK.final_, null), new SCstring(16L, TOK.abstract_, null), new SCstring(512L, TOK.synchronized_, null), new SCstring(1024L, TOK.deprecated_, null), new SCstring(128L, TOK.override_, null), new SCstring(8192L, TOK.lazy_, null), new SCstring(268435456L, TOK.alias_, null), new SCstring(4096L, TOK.out_, null), new SCstring(2048L, TOK.in_, null), new SCstring(8388608L, TOK.enum_, null), new SCstring(1048576L, TOK.immutable_, null), new SCstring(536870912L, TOK.shared_, null), new SCstring(33554432L, TOK.nothrow_, null), new SCstring(2147483648L, TOK.inout_, null), new SCstring(67108864L, TOK.pure_, null), new SCstring(2097152L, TOK.ref_, null), new SCstring(134217728L, TOK.reserved, null), new SCstring(1073741824L, TOK.gshared, null), new SCstring(4398046511104L, TOK.at, new BytePtr("@nogc")), new SCstring(4294967296L, TOK.at, new BytePtr("@property")), new SCstring(8589934592L, TOK.at, new BytePtr("@safe")), new SCstring(17179869184L, TOK.at, new BytePtr("@trusted")), new SCstring(34359738368L, TOK.at, new BytePtr("@system")), new SCstring(137438953472L, TOK.at, new BytePtr("@disable")), new SCstring(1125899906842624L, TOK.at, new BytePtr("@__future")), new SCstring(0L, TOK.reserved, null)};
    static int __ctorpackageTag;
    static BytePtr __ctormsg = new BytePtr("only object.d can define this reserved class name");
    static ByteSlice _initbasetab = slice(initializer_0);
    private static class SCstring
    {
        private long stc;
        private byte tok;
        private BytePtr id;
        public SCstring(){
        }
        public SCstring copy(){
            SCstring r = new SCstring();
            r.stc = stc;
            r.tok = tok;
            r.id = id;
            return r;
        }
        public SCstring(long stc, byte tok, BytePtr id) {
            this.stc = stc;
            this.tok = tok;
            this.id = id;
        }

        public SCstring opAssign(SCstring that) {
            this.stc = that.stc;
            this.tok = that.tok;
            this.id = that.id;
            return this;
        }
    }
    static Slice<SCstring> stcToCharstable = slice(initializer_1);

    public static class ASTBase
    {

        public static class Sizeok 
        {
            public static final int none = 0;
            public static final int fwd = 1;
            public static final int done = 2;
        }


        public static class Baseok 
        {
            public static final int none = 0;
            public static final int start = 1;
            public static final int done = 2;
            public static final int semanticdone = 3;
        }


        public static class MODFlags 
        {
            public static final int const_ = 1;
            public static final int immutable_ = 4;
            public static final int shared_ = 2;
            public static final int wild = 8;
            public static final int wildconst = 9;
            public static final int mutable = 16;
        }


        public static class STC 
        {
            public static final long undefined_ = 0L;
            public static final long static_ = 1L;
            public static final long extern_ = 2L;
            public static final long const_ = 4L;
            public static final long final_ = 8L;
            public static final long abstract_ = 16L;
            public static final long parameter = 32L;
            public static final long field = 64L;
            public static final long override_ = 128L;
            public static final long auto_ = 256L;
            public static final long synchronized_ = 512L;
            public static final long deprecated_ = 1024L;
            public static final long in_ = 2048L;
            public static final long out_ = 4096L;
            public static final long lazy_ = 8192L;
            public static final long foreach_ = 16384L;
            public static final long variadic = 65536L;
            public static final long ctorinit = 131072L;
            public static final long templateparameter = 262144L;
            public static final long scope_ = 524288L;
            public static final long immutable_ = 1048576L;
            public static final long ref_ = 2097152L;
            public static final long init = 4194304L;
            public static final long manifest = 8388608L;
            public static final long nodtor = 16777216L;
            public static final long nothrow_ = 33554432L;
            public static final long pure_ = 67108864L;
            public static final long tls = 134217728L;
            public static final long alias_ = 268435456L;
            public static final long shared_ = 536870912L;
            public static final long gshared = 1073741824L;
            public static final long wild = 2147483648L;
            public static final long property = 4294967296L;
            public static final long safe = 8589934592L;
            public static final long trusted = 17179869184L;
            public static final long system = 34359738368L;
            public static final long ctfe = 68719476736L;
            public static final long disable = 137438953472L;
            public static final long result = 274877906944L;
            public static final long nodefaultctor = 549755813888L;
            public static final long temp = 1099511627776L;
            public static final long rvalue = 2199023255552L;
            public static final long nogc = 4398046511104L;
            public static final long volatile_ = 8796093022208L;
            public static final long return_ = 17592186044416L;
            public static final long autoref = 35184372088832L;
            public static final long inference = 70368744177664L;
            public static final long exptemp = 140737488355328L;
            public static final long maybescope = 281474976710656L;
            public static final long scopeinferred = 562949953421312L;
            public static final long future = 1125899906842624L;
            public static final long local = 2251799813685248L;
            public static final long returninferred = 4503599627370496L;
            public static final long TYPECTOR = 2685403140L;
            public static final long FUNCATTR = 4462573780992L;
        }

        public static long STCStorageClass = 22196369506207L;

        public static class ENUMTY 
        {
            public static final int Tarray = 0;
            public static final int Tsarray = 1;
            public static final int Taarray = 2;
            public static final int Tpointer = 3;
            public static final int Treference = 4;
            public static final int Tfunction = 5;
            public static final int Tident = 6;
            public static final int Tclass = 7;
            public static final int Tstruct = 8;
            public static final int Tenum = 9;
            public static final int Tdelegate = 10;
            public static final int Tnone = 11;
            public static final int Tvoid = 12;
            public static final int Tint8 = 13;
            public static final int Tuns8 = 14;
            public static final int Tint16 = 15;
            public static final int Tuns16 = 16;
            public static final int Tint32 = 17;
            public static final int Tuns32 = 18;
            public static final int Tint64 = 19;
            public static final int Tuns64 = 20;
            public static final int Tfloat32 = 21;
            public static final int Tfloat64 = 22;
            public static final int Tfloat80 = 23;
            public static final int Timaginary32 = 24;
            public static final int Timaginary64 = 25;
            public static final int Timaginary80 = 26;
            public static final int Tcomplex32 = 27;
            public static final int Tcomplex64 = 28;
            public static final int Tcomplex80 = 29;
            public static final int Tbool = 30;
            public static final int Tchar = 31;
            public static final int Twchar = 32;
            public static final int Tdchar = 33;
            public static final int Terror = 34;
            public static final int Tinstance = 35;
            public static final int Ttypeof = 36;
            public static final int Ttuple = 37;
            public static final int Tslice = 38;
            public static final int Treturn = 39;
            public static final int Tnull = 40;
            public static final int Tvector = 41;
            public static final int Tint128 = 42;
            public static final int Tuns128 = 43;
            public static final int TMAX = 44;
        }

        public static final int Tarray = ENUMTY.Tarray;
        public static final int Tsarray = ENUMTY.Tsarray;
        public static final int Taarray = ENUMTY.Taarray;
        public static final int Tpointer = ENUMTY.Tpointer;
        public static final int Treference = ENUMTY.Treference;
        public static final int Tfunction = ENUMTY.Tfunction;
        public static final int Tident = ENUMTY.Tident;
        public static final int Tclass = ENUMTY.Tclass;
        public static final int Tstruct = ENUMTY.Tstruct;
        public static final int Tenum = ENUMTY.Tenum;
        public static final int Tdelegate = ENUMTY.Tdelegate;
        public static final int Tnone = ENUMTY.Tnone;
        public static final int Tvoid = ENUMTY.Tvoid;
        public static final int Tint8 = ENUMTY.Tint8;
        public static final int Tuns8 = ENUMTY.Tuns8;
        public static final int Tint16 = ENUMTY.Tint16;
        public static final int Tuns16 = ENUMTY.Tuns16;
        public static final int Tint32 = ENUMTY.Tint32;
        public static final int Tuns32 = ENUMTY.Tuns32;
        public static final int Tint64 = ENUMTY.Tint64;
        public static final int Tuns64 = ENUMTY.Tuns64;
        public static final int Tfloat32 = ENUMTY.Tfloat32;
        public static final int Tfloat64 = ENUMTY.Tfloat64;
        public static final int Tfloat80 = ENUMTY.Tfloat80;
        public static final int Timaginary32 = ENUMTY.Timaginary32;
        public static final int Timaginary64 = ENUMTY.Timaginary64;
        public static final int Timaginary80 = ENUMTY.Timaginary80;
        public static final int Tcomplex32 = ENUMTY.Tcomplex32;
        public static final int Tcomplex64 = ENUMTY.Tcomplex64;
        public static final int Tcomplex80 = ENUMTY.Tcomplex80;
        public static final int Tbool = ENUMTY.Tbool;
        public static final int Tchar = ENUMTY.Tchar;
        public static final int Twchar = ENUMTY.Twchar;
        public static final int Tdchar = ENUMTY.Tdchar;
        public static final int Terror = ENUMTY.Terror;
        public static final int Tinstance = ENUMTY.Tinstance;
        public static final int Ttypeof = ENUMTY.Ttypeof;
        public static final int Ttuple = ENUMTY.Ttuple;
        public static final int Tslice = ENUMTY.Tslice;
        public static final int Treturn = ENUMTY.Treturn;
        public static final int Tnull = ENUMTY.Tnull;
        public static final int Tvector = ENUMTY.Tvector;
        public static final int Tint128 = ENUMTY.Tint128;
        public static final int Tuns128 = ENUMTY.Tuns128;
        public static final int TMAX = ENUMTY.TMAX;

        public static class TFlags 
        {
            public static final int integral = 1;
            public static final int floating = 2;
            public static final int unsigned = 4;
            public static final int real_ = 8;
            public static final int imaginary = 16;
            public static final int complex = 32;
            public static final int char_ = 64;
        }


        public static class PKG 
        {
            public static final int unknown = 0;
            public static final int module_ = 1;
            public static final int package_ = 2;
        }


        public static class StructPOD 
        {
            public static final int no = 0;
            public static final int yes = 1;
            public static final int fwd = 2;
        }


        public static class TRUST 
        {
            public static final int default_ = 0;
            public static final int system = 1;
            public static final int trusted = 2;
            public static final int safe = 3;
        }


        public static class PURE 
        {
            public static final int impure = 0;
            public static final int fwdref = 1;
            public static final int weak = 2;
            public static final int const_ = 3;
            public static final int strong = 4;
        }


        public static class AliasThisRec 
        {
            public static final int no = 0;
            public static final int yes = 1;
            public static final int fwdref = 2;
            public static final int typeMask = 3;
            public static final int tracing = 4;
            public static final int tracingDT = 8;
        }


        public static class VarArg 
        {
            public static final int none = 0;
            public static final int variadic = 1;
            public static final int typesafe = 2;
        }

        public static abstract class ASTNode extends RootObject
        {
            public abstract void accept(ParseTimeVisitorASTBase v);
            public  ASTNode() {
                super();
            }


            public abstract ASTNode copy();
        }
        public static class Dsymbol extends ASTNode
        {
            public Loc loc = new Loc();
            public Identifier ident;
            public UnitTestDeclaration ddocUnittest;
            public UserAttributeDeclaration userAttribDecl;
            public Dsymbol parent;
            public BytePtr comment;
            public  Dsymbol() {
                super();
            }

            public  Dsymbol(Identifier ident) {
                super();
                this.ident = ident;
            }

            public  void addComment(BytePtr comment) {
                if (this.comment == null)
                    this.comment = pcopy(comment);
                else if (comment != null && strcmp(comment, this.comment) != 0)
                    this.comment = pcopy(Lexer.combineComments(this.comment, comment, true));
            }

            public  BytePtr toChars() {
                return this.ident != null ? this.ident.toChars() : new BytePtr("__anonymous");
            }

            public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
                ps.set(0, this);
                return true;
            }

            public static boolean oneMembers(DArray<Dsymbol> members, Ptr<Dsymbol> ps, Identifier ident) {
                Dsymbol s = null;
                {
                    int i = 0;
                    for (; i < members.length;i++){
                        Dsymbol sx = members.get(i);
                        boolean x = sx.oneMember(ps, ident);
                        if (!(x))
                        {
                            assert(ps.get() == null);
                            return false;
                        }
                        if (ps.get() != null)
                        {
                            assert(ident != null);
                            if (!((ps.get()).ident != null) || !((ps.get()).ident.equals(ident)))
                                continue;
                            if (!(s != null))
                                s = ps.get();
                            else if (s.isOverloadable() && (ps.get()).isOverloadable())
                            {
                                FuncDeclaration f1 = s.isFuncDeclaration();
                                FuncDeclaration f2 = (ps.get()).isFuncDeclaration();
                                if (f1 != null && f2 != null)
                                {
                                    for (; !f1.equals(f2);f1 = f1.overnext0){
                                        if (f1.overnext0 == null)
                                        {
                                            f1.overnext0 = f2;
                                            break;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                ps.set(0, null);
                                return false;
                            }
                        }
                    }
                }
                ps.set(0, s);
                return true;
            }

            public  boolean isOverloadable() {
                return false;
            }

            public  BytePtr kind() {
                return new BytePtr("symbol");
            }

            public  void error(BytePtr format, Object... ap) {
                verror(this.loc, format, new Slice<>(ap), this.kind(), new BytePtr(""), new BytePtr("Error: "));
            }

            public  AttribDeclaration isAttribDeclaration() {
                return null;
            }

            public  TemplateDeclaration isTemplateDeclaration() {
                return null;
            }

            public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
                return null;
            }

            public  FuncDeclaration isFuncDeclaration() {
                return null;
            }

            public  VarDeclaration isVarDeclaration() {
                return null;
            }

            public  TemplateInstance isTemplateInstance() {
                return null;
            }

            public  Declaration isDeclaration() {
                return null;
            }

            public  ClassDeclaration isClassDeclaration() {
                return null;
            }

            public  AggregateDeclaration isAggregateDeclaration() {
                return null;
            }

            public  Dsymbol syntaxCopy(Dsymbol s) {
                return null;
            }

            public  int dyncast() {
                return DYNCAST.dsymbol;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Dsymbol copy() {
                Dsymbol that = new Dsymbol();
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class AliasThis extends Dsymbol
        {
            public Identifier ident;
            public  AliasThis(Loc loc, Identifier ident) {
                super(null);
                this.loc = loc.copy();
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AliasThis() {}

            public AliasThis copy() {
                AliasThis that = new AliasThis();
                that.ident = this.ident;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static abstract class Declaration extends Dsymbol
        {
            public long storage_class;
            public Prot protection = new Prot();
            public int linkage;
            public Type type;
            public  Declaration(Identifier id) {
                super(id);
                this.storage_class = 0L;
                this.protection = new Prot(Prot.Kind.undefined, null).copy();
                this.linkage = LINK.default_;
            }

            public  Declaration isDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Declaration() {}

            public abstract Declaration copy();
        }
        public static class ScopeDsymbol extends Dsymbol
        {
            public DArray<Dsymbol> members;
            public  ScopeDsymbol() {
                super();
            }

            public  ScopeDsymbol(Identifier id) {
                super(id);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ScopeDsymbol copy() {
                ScopeDsymbol that = new ScopeDsymbol();
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class Import extends Dsymbol
        {
            public DArray<Identifier> packages;
            public Identifier id;
            public Identifier aliasId;
            public int isstatic;
            public Prot protection = new Prot();
            public DArray<Identifier> names = new DArray<Identifier>();
            public DArray<Identifier> aliases = new DArray<Identifier>();
            public  Import(Loc loc, DArray<Identifier> packages, Identifier id, Identifier aliasId, int isstatic) {
                super(null);
                this.loc = loc.copy();
                this.packages = packages;
                this.id = id;
                this.aliasId = aliasId;
                this.isstatic = isstatic;
                this.protection = new Prot(Prot.Kind.private_, null).copy();
                if (aliasId != null)
                {
                    this.ident = aliasId;
                }
                else if (packages != null && ((packages).length) != 0)
                {
                    this.ident = (packages).get(0);
                }
                else
                {
                    this.ident = id;
                }
            }

            public  void addAlias(Identifier name, Identifier _alias) {
                if ((this.isstatic) != 0)
                    this.error(new BytePtr("cannot have an import bind list"));
                if (!(this.aliasId != null))
                    this.ident = null;
                this.names.push(name);
                this.aliases.push(_alias);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Import() {}

            public Import copy() {
                Import that = new Import();
                that.packages = this.packages;
                that.id = this.id;
                that.aliasId = this.aliasId;
                that.isstatic = this.isstatic;
                that.protection = this.protection;
                that.names = this.names;
                that.aliases = this.aliases;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static abstract class AttribDeclaration extends Dsymbol
        {
            public DArray<Dsymbol> decl;
            public  AttribDeclaration(DArray<Dsymbol> decl) {
                super();
                this.decl = decl;
            }

            public  AttribDeclaration isAttribDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AttribDeclaration() {}

            public abstract AttribDeclaration copy();
        }
        public static class StaticAssert extends Dsymbol
        {
            public Expression exp;
            public Expression msg;
            public  StaticAssert(Loc loc, Expression exp, Expression msg) {
                super(Id.empty);
                this.loc = loc.copy();
                this.exp = exp;
                this.msg = msg;
            }


            public StaticAssert() {}

            public StaticAssert copy() {
                StaticAssert that = new StaticAssert();
                that.exp = this.exp;
                that.msg = this.msg;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class DebugSymbol extends Dsymbol
        {
            public int level;
            public  DebugSymbol(Loc loc, Identifier ident) {
                super(ident);
                this.loc = loc.copy();
            }

            public  DebugSymbol(Loc loc, int level) {
                super();
                this.level = level;
                this.loc = loc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DebugSymbol() {}

            public DebugSymbol copy() {
                DebugSymbol that = new DebugSymbol();
                that.level = this.level;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class VersionSymbol extends Dsymbol
        {
            public int level;
            public  VersionSymbol(Loc loc, Identifier ident) {
                super(ident);
                this.loc = loc.copy();
            }

            public  VersionSymbol(Loc loc, int level) {
                super();
                this.level = level;
                this.loc = loc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VersionSymbol() {}

            public VersionSymbol copy() {
                VersionSymbol that = new VersionSymbol();
                that.level = this.level;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class VarDeclaration extends Declaration
        {
            public Type type;
            public Initializer _init;
            public long storage_class;
            public int ctfeAdrOnStack;
            public int sequenceNumber;
            public static int nextSequenceNumber;
            public  VarDeclaration(Loc loc, Type type, Identifier id, Initializer _init, long st) {
                super(id);
                this.type = type;
                this._init = _init;
                this.loc = loc.copy();
                this.storage_class = st;
                this.sequenceNumber = (nextSequenceNumber += 1);
                this.ctfeAdrOnStack = -1;
            }

            public  VarDeclaration isVarDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VarDeclaration() {}

            public VarDeclaration copy() {
                VarDeclaration that = new VarDeclaration();
                that.type = this.type;
                that._init = this._init;
                that.storage_class = this.storage_class;
                that.ctfeAdrOnStack = this.ctfeAdrOnStack;
                that.sequenceNumber = this.sequenceNumber;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                return that;
            }
        }
        public static class Ensure
        {
            public Identifier id;
            public Statement ensure;
            public Ensure(){
            }
            public Ensure copy(){
                Ensure r = new Ensure();
                r.id = id;
                r.ensure = ensure;
                return r;
            }
            public Ensure(Identifier id, Statement ensure) {
                this.id = id;
                this.ensure = ensure;
            }

            public Ensure opAssign(Ensure that) {
                this.id = that.id;
                this.ensure = that.ensure;
                return this;
            }
        }
        public static class FuncDeclaration extends Declaration
        {
            public Statement fbody;
            public DArray<Statement> frequires;
            public DArray<Ensure> fensures;
            public Loc endloc = new Loc();
            public long storage_class;
            public Type type;
            public boolean inferRetType;
            public ForeachStatement fes;
            public FuncDeclaration overnext0;
            public  FuncDeclaration(Loc loc, Loc endloc, Identifier id, long storage_class, Type type) {
                super(id);
                this.storage_class = storage_class;
                this.type = type;
                if (type != null)
                {
                    this.storage_class &= -4465259184133L;
                }
                this.loc = loc.copy();
                this.endloc = endloc.copy();
                this.inferRetType = type != null && type.nextOf() == null;
            }

            public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
                return null;
            }

            public  boolean isOverloadable() {
                return true;
            }

            public  FuncDeclaration isFuncDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncDeclaration() {}

            public FuncDeclaration copy() {
                FuncDeclaration that = new FuncDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                return that;
            }
        }
        public static class AliasDeclaration extends Declaration
        {
            public Dsymbol aliassym;
            public  AliasDeclaration(Loc loc, Identifier id, Dsymbol s) {
                super(id);
                this.loc = loc.copy();
                this.aliassym = s;
            }

            public  AliasDeclaration(Loc loc, Identifier id, Type type) {
                super(id);
                this.loc = loc.copy();
                this.type = type;
            }

            public  boolean isOverloadable() {
                return true;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AliasDeclaration() {}

            public AliasDeclaration copy() {
                AliasDeclaration that = new AliasDeclaration();
                that.aliassym = this.aliassym;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                return that;
            }
        }
        public static class TupleDeclaration extends Declaration
        {
            public DArray<RootObject> objects;
            public  TupleDeclaration(Loc loc, Identifier id, DArray<RootObject> objects) {
                super(id);
                this.loc = loc.copy();
                this.objects = objects;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TupleDeclaration() {}

            public TupleDeclaration copy() {
                TupleDeclaration that = new TupleDeclaration();
                that.objects = this.objects;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                return that;
            }
        }
        public static class FuncLiteralDeclaration extends FuncDeclaration
        {
            public byte tok;
            public  FuncLiteralDeclaration(Loc loc, Loc endloc, Type type, byte tok, ForeachStatement fes, Identifier id) {
                super(loc, endloc, null, 0L, type);
                this.ident = id != null ? id : Id.empty;
                this.tok = tok;
                this.fes = fes;
            }

            public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncLiteralDeclaration() {}

            public FuncLiteralDeclaration copy() {
                FuncLiteralDeclaration that = new FuncLiteralDeclaration();
                that.tok = this.tok;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class PostBlitDeclaration extends FuncDeclaration
        {
            public  PostBlitDeclaration(Loc loc, Loc endloc, long stc, Identifier id) {
                super(loc, endloc, id, stc, null);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PostBlitDeclaration() {}

            public PostBlitDeclaration copy() {
                PostBlitDeclaration that = new PostBlitDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class CtorDeclaration extends FuncDeclaration
        {
            public  CtorDeclaration(Loc loc, Loc endloc, long stc, Type type, boolean isCopyCtor) {
                super(loc, endloc, Id.ctor, stc, type);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CtorDeclaration() {}

            public CtorDeclaration copy() {
                CtorDeclaration that = new CtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class DtorDeclaration extends FuncDeclaration
        {
            public  DtorDeclaration(Loc loc, Loc endloc) {
                super(loc, endloc, Id.dtor, 0L, null);
            }

            public  DtorDeclaration(Loc loc, Loc endloc, long stc, Identifier id) {
                super(loc, endloc, id, stc, null);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DtorDeclaration() {}

            public DtorDeclaration copy() {
                DtorDeclaration that = new DtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class InvariantDeclaration extends FuncDeclaration
        {
            public  InvariantDeclaration(Loc loc, Loc endloc, long stc, Identifier id, Statement fbody) {
                super(loc, endloc, id != null ? id : Identifier.generateId(new BytePtr("__invariant")), stc, null);
                this.fbody = fbody;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InvariantDeclaration() {}

            public InvariantDeclaration copy() {
                InvariantDeclaration that = new InvariantDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class UnitTestDeclaration extends FuncDeclaration
        {
            public BytePtr codedoc;
            public  UnitTestDeclaration(Loc loc, Loc endloc, long stc, BytePtr codedoc) {
                super(loc, endloc, Identifier.generateIdWithLoc( new ByteSlice("__unittest"), loc), stc, null);
                this.codedoc = pcopy(codedoc);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UnitTestDeclaration() {}

            public UnitTestDeclaration copy() {
                UnitTestDeclaration that = new UnitTestDeclaration();
                that.codedoc = this.codedoc;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class NewDeclaration extends FuncDeclaration
        {
            public DArray<Parameter> parameters;
            public int varargs;
            public  NewDeclaration(Loc loc, Loc endloc, long stc, DArray<Parameter> fparams, int varargs) {
                super(loc, endloc, Id.classNew, 1L | stc, null);
                this.parameters = fparams;
                this.varargs = varargs;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NewDeclaration() {}

            public NewDeclaration copy() {
                NewDeclaration that = new NewDeclaration();
                that.parameters = this.parameters;
                that.varargs = this.varargs;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class DeleteDeclaration extends FuncDeclaration
        {
            public DArray<Parameter> parameters;
            public  DeleteDeclaration(Loc loc, Loc endloc, long stc, DArray<Parameter> fparams) {
                super(loc, endloc, Id.classDelete, 1L | stc, null);
                this.parameters = fparams;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeleteDeclaration() {}

            public DeleteDeclaration copy() {
                DeleteDeclaration that = new DeleteDeclaration();
                that.parameters = this.parameters;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class StaticCtorDeclaration extends FuncDeclaration
        {
            public  StaticCtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc( new ByteSlice("_staticCtor"), loc), 1L | stc, null);
            }

            public  StaticCtorDeclaration(Loc loc, Loc endloc, ByteSlice name, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc(name, loc), 1L | stc, null);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticCtorDeclaration() {}

            public StaticCtorDeclaration copy() {
                StaticCtorDeclaration that = new StaticCtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class StaticDtorDeclaration extends FuncDeclaration
        {
            // from template __ctor!()
            public  StaticDtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc( new ByteSlice("__staticDtor"), loc), 1L | stc, null);
            }


            public  StaticDtorDeclaration(Loc loc, Loc endloc, ByteSlice name, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc(name, loc), 1L | stc, null);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticDtorDeclaration() {}

            public StaticDtorDeclaration copy() {
                StaticDtorDeclaration that = new StaticDtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                return that;
            }
        }
        public static class SharedStaticCtorDeclaration extends StaticCtorDeclaration
        {
            public  SharedStaticCtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc,  new ByteSlice("_sharedStaticCtor"), stc);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SharedStaticCtorDeclaration() {}

            public SharedStaticCtorDeclaration copy() {
                SharedStaticCtorDeclaration that = new SharedStaticCtorDeclaration();
                return that;
            }
        }
        public static class SharedStaticDtorDeclaration extends StaticDtorDeclaration
        {
            public  SharedStaticDtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc,  new ByteSlice("_sharedStaticDtor"), stc);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SharedStaticDtorDeclaration() {}

            public SharedStaticDtorDeclaration copy() {
                SharedStaticDtorDeclaration that = new SharedStaticDtorDeclaration();
                return that;
            }
        }
        public static class Package extends ScopeDsymbol
        {
            public int isPkgMod;
            public int tag;
            public  Package(Identifier ident) {
                super(ident);
                this.isPkgMod = PKG.unknown;
                this.tag = astbase.__ctorpackageTag++;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Package() {}

            public Package copy() {
                Package that = new Package();
                that.isPkgMod = this.isPkgMod;
                that.tag = this.tag;
                that.members = this.members;
                return that;
            }
        }
        public static class EnumDeclaration extends ScopeDsymbol
        {
            public Type type;
            public Type memtype;
            public Prot protection = new Prot();
            public  EnumDeclaration(Loc loc, Identifier id, Type memtype) {
                super(id);
                this.loc = loc.copy();
                this.type = new TypeEnum(this);
                this.memtype = memtype;
                this.protection = new Prot(Prot.Kind.undefined, null).copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public EnumDeclaration() {}

            public EnumDeclaration copy() {
                EnumDeclaration that = new EnumDeclaration();
                that.type = this.type;
                that.memtype = this.memtype;
                that.protection = this.protection;
                that.members = this.members;
                return that;
            }
        }
        public static abstract class AggregateDeclaration extends ScopeDsymbol
        {
            public Prot protection = new Prot();
            public int sizeok;
            public Type type;
            public  AggregateDeclaration(Loc loc, Identifier id) {
                super(id);
                this.loc = loc.copy();
                this.protection = new Prot(Prot.Kind.public_, null).copy();
                this.sizeok = Sizeok.none;
            }

            public  AggregateDeclaration isAggregateDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AggregateDeclaration() {}

            public abstract AggregateDeclaration copy();
        }
        public static class TemplateDeclaration extends ScopeDsymbol
        {
            public DArray<TemplateParameter> parameters;
            public DArray<TemplateParameter> origParameters;
            public Expression constraint;
            public boolean literal;
            public boolean ismixin;
            public boolean isstatic;
            public Prot protection = new Prot();
            public Dsymbol onemember;
            public  TemplateDeclaration(Loc loc, Identifier id, DArray<TemplateParameter> parameters, Expression constraint, DArray<Dsymbol> decldefs, boolean ismixin, boolean literal) {
                super(id);
                this.loc = loc.copy();
                this.parameters = parameters;
                this.origParameters = parameters;
                this.members = decldefs;
                this.literal = literal;
                this.ismixin = ismixin;
                this.isstatic = true;
                this.protection = new Prot(Prot.Kind.undefined, null).copy();
                if (this.members != null && this.ident != null)
                {
                    Ref<Dsymbol> s = ref(null);
                    if (Dsymbol.oneMembers(this.members, ptr(s), this.ident) && s.value != null)
                    {
                        this.onemember = s.value;
                        s.value.parent = this;
                    }
                }
            }

            public  boolean isOverloadable() {
                return true;
            }

            public  TemplateDeclaration isTemplateDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateDeclaration() {}

            public TemplateDeclaration copy() {
                TemplateDeclaration that = new TemplateDeclaration();
                that.parameters = this.parameters;
                that.origParameters = this.origParameters;
                that.constraint = this.constraint;
                that.literal = this.literal;
                that.ismixin = this.ismixin;
                that.isstatic = this.isstatic;
                that.protection = this.protection;
                that.onemember = this.onemember;
                that.members = this.members;
                return that;
            }
        }
        public static class TemplateInstance extends ScopeDsymbol
        {
            public Identifier name;
            public DArray<RootObject> tiargs;
            public Dsymbol tempdecl;
            public boolean semantictiargsdone;
            public boolean havetempdecl;
            public TemplateInstance inst;
            public  TemplateInstance(Loc loc, Identifier ident, DArray<RootObject> tiargs) {
                super(null);
                this.loc = loc.copy();
                this.name = ident;
                this.tiargs = tiargs;
            }

            public  TemplateInstance(Loc loc, TemplateDeclaration td, DArray<RootObject> tiargs) {
                super(null);
                this.loc = loc.copy();
                this.name = td.ident;
                this.tempdecl = td;
                this.semantictiargsdone = true;
                this.havetempdecl = true;
            }

            public  TemplateInstance isTemplateInstance() {
                return this;
            }

            public  DArray<RootObject> arraySyntaxCopy(DArray<RootObject> objs) {
                DArray<RootObject> a = null;
                if (objs != null)
                {
                    a = new DArray<RootObject>();
                    (a).setDim((objs).length);
                    {
                        int i = 0;
                        for (; i < (objs).length;i++) {
                            a.set(i, this.objectSyntaxCopy((objs).get(i)));
                        }
                    }
                }
                return a;
            }

            public  RootObject objectSyntaxCopy(RootObject o) {
                if (!(o != null))
                    return null;
                {
                    Type t = isType(o);
                    if (t != null)
                        return t.syntaxCopy();
                }
                {
                    Expression e = isExpression(o);
                    if (e != null)
                        return e.syntaxCopy();
                }
                return o;
            }

            public  Dsymbol syntaxCopy(Dsymbol s) {
                TemplateInstance ti = s != null ? (TemplateInstance)s : new TemplateInstance(this.loc, this.name, null);
                ti.tiargs = this.arraySyntaxCopy(this.tiargs);
                TemplateDeclaration td = null;
                if (this.inst != null && this.tempdecl != null && (td = this.tempdecl.isTemplateDeclaration()) != null)
                    td.syntaxCopy((Dsymbol)ti);
                else
                    this.syntaxCopy((Dsymbol)ti);
                return ti;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateInstance() {}

            public TemplateInstance copy() {
                TemplateInstance that = new TemplateInstance();
                that.name = this.name;
                that.tiargs = this.tiargs;
                that.tempdecl = this.tempdecl;
                that.semantictiargsdone = this.semantictiargsdone;
                that.havetempdecl = this.havetempdecl;
                that.inst = this.inst;
                that.members = this.members;
                return that;
            }
        }
        public static class Nspace extends ScopeDsymbol
        {
            public Expression identExp;
            public  Nspace(Loc loc, Identifier ident, Expression identExp, DArray<Dsymbol> members) {
                super(ident);
                this.loc = loc.copy();
                this.members = members;
                this.identExp = identExp;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Nspace() {}

            public Nspace copy() {
                Nspace that = new Nspace();
                that.identExp = this.identExp;
                that.members = this.members;
                return that;
            }
        }
        public static class CompileDeclaration extends AttribDeclaration
        {
            public DArray<Expression> exps;
            public  CompileDeclaration(Loc loc, DArray<Expression> exps) {
                super(null);
                this.loc = loc.copy();
                this.exps = exps;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompileDeclaration() {}

            public CompileDeclaration copy() {
                CompileDeclaration that = new CompileDeclaration();
                that.exps = this.exps;
                that.decl = this.decl;
                return that;
            }
        }
        public static class UserAttributeDeclaration extends AttribDeclaration
        {
            public DArray<Expression> atts;
            public  UserAttributeDeclaration(DArray<Expression> atts, DArray<Dsymbol> decl) {
                super(decl);
                this.atts = atts;
            }

            public static DArray<Expression> concat(DArray<Expression> udas1, DArray<Expression> udas2) {
                DArray<Expression> udas = null;
                if (udas1 == null || (udas1).length == 0)
                    udas = udas2;
                else if (udas2 == null || (udas2).length == 0)
                    udas = udas1;
                else
                {
                    udas = new DArray<Expression>();
                    udas.set(0, new TupleExp(Loc.initial, udas1));
                    udas.set(1, new TupleExp(Loc.initial, udas2));
                }
                return udas;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UserAttributeDeclaration() {}

            public UserAttributeDeclaration copy() {
                UserAttributeDeclaration that = new UserAttributeDeclaration();
                that.atts = this.atts;
                that.decl = this.decl;
                return that;
            }
        }
        public static class LinkDeclaration extends AttribDeclaration
        {
            public int linkage;
            public  LinkDeclaration(int p, DArray<Dsymbol> decl) {
                super(decl);
                this.linkage = p;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LinkDeclaration() {}

            public LinkDeclaration copy() {
                LinkDeclaration that = new LinkDeclaration();
                that.linkage = this.linkage;
                that.decl = this.decl;
                return that;
            }
        }
        public static class AnonDeclaration extends AttribDeclaration
        {
            public boolean isunion;
            public  AnonDeclaration(Loc loc, boolean isunion, DArray<Dsymbol> decl) {
                super(decl);
                this.loc = loc.copy();
                this.isunion = isunion;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AnonDeclaration() {}

            public AnonDeclaration copy() {
                AnonDeclaration that = new AnonDeclaration();
                that.isunion = this.isunion;
                that.decl = this.decl;
                return that;
            }
        }
        public static class AlignDeclaration extends AttribDeclaration
        {
            public Expression ealign;
            public  AlignDeclaration(Loc loc, Expression ealign, DArray<Dsymbol> decl) {
                super(decl);
                this.loc = loc.copy();
                this.ealign = ealign;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AlignDeclaration() {}

            public AlignDeclaration copy() {
                AlignDeclaration that = new AlignDeclaration();
                that.ealign = this.ealign;
                that.decl = this.decl;
                return that;
            }
        }
        public static class CPPMangleDeclaration extends AttribDeclaration
        {
            public int cppmangle;
            public  CPPMangleDeclaration(int p, DArray<Dsymbol> decl) {
                super(decl);
                this.cppmangle = p;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CPPMangleDeclaration() {}

            public CPPMangleDeclaration copy() {
                CPPMangleDeclaration that = new CPPMangleDeclaration();
                that.cppmangle = this.cppmangle;
                that.decl = this.decl;
                return that;
            }
        }
        public static class CPPNamespaceDeclaration extends AttribDeclaration
        {
            public Expression exp;
            public  CPPNamespaceDeclaration(Identifier ident, DArray<Dsymbol> decl) {
                super(decl);
                this.ident = ident;
            }

            public  CPPNamespaceDeclaration(Expression exp, DArray<Dsymbol> decl) {
                super(decl);
                this.exp = exp;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CPPNamespaceDeclaration() {}

            public CPPNamespaceDeclaration copy() {
                CPPNamespaceDeclaration that = new CPPNamespaceDeclaration();
                that.exp = this.exp;
                that.decl = this.decl;
                return that;
            }
        }
        public static class ProtDeclaration extends AttribDeclaration
        {
            public Prot protection = new Prot();
            public DArray<Identifier> pkg_identifiers;
            public  ProtDeclaration(Loc loc, Prot p, DArray<Dsymbol> decl) {
                super(decl);
                this.loc = loc.copy();
                this.protection = p.copy();
            }

            public  ProtDeclaration(Loc loc, DArray<Identifier> pkg_identifiers, DArray<Dsymbol> decl) {
                super(decl);
                this.loc = loc.copy();
                this.protection.kind = Prot.Kind.package_;
                this.protection.pkg = null;
                this.pkg_identifiers = pkg_identifiers;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ProtDeclaration() {}

            public ProtDeclaration copy() {
                ProtDeclaration that = new ProtDeclaration();
                that.protection = this.protection;
                that.pkg_identifiers = this.pkg_identifiers;
                that.decl = this.decl;
                return that;
            }
        }
        public static class PragmaDeclaration extends AttribDeclaration
        {
            public DArray<Expression> args;
            public  PragmaDeclaration(Loc loc, Identifier ident, DArray<Expression> args, DArray<Dsymbol> decl) {
                super(decl);
                this.loc = loc.copy();
                this.ident = ident;
                this.args = args;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PragmaDeclaration() {}

            public PragmaDeclaration copy() {
                PragmaDeclaration that = new PragmaDeclaration();
                that.args = this.args;
                that.decl = this.decl;
                return that;
            }
        }
        public static class StorageClassDeclaration extends AttribDeclaration
        {
            public long stc;
            public  StorageClassDeclaration(long stc, DArray<Dsymbol> decl) {
                super(decl);
                this.stc = stc;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StorageClassDeclaration() {}

            public StorageClassDeclaration copy() {
                StorageClassDeclaration that = new StorageClassDeclaration();
                that.stc = this.stc;
                that.decl = this.decl;
                return that;
            }
        }
        public static class ConditionalDeclaration extends AttribDeclaration
        {
            public Condition condition;
            public DArray<Dsymbol> elsedecl;
            public  ConditionalDeclaration(Condition condition, DArray<Dsymbol> decl, DArray<Dsymbol> elsedecl) {
                super(decl);
                this.condition = condition;
                this.elsedecl = elsedecl;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ConditionalDeclaration() {}

            public ConditionalDeclaration copy() {
                ConditionalDeclaration that = new ConditionalDeclaration();
                that.condition = this.condition;
                that.elsedecl = this.elsedecl;
                that.decl = this.decl;
                return that;
            }
        }
        public static class DeprecatedDeclaration extends StorageClassDeclaration
        {
            public Expression msg;
            public  DeprecatedDeclaration(Expression msg, DArray<Dsymbol> decl) {
                super(1024L, decl);
                this.msg = msg;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeprecatedDeclaration() {}

            public DeprecatedDeclaration copy() {
                DeprecatedDeclaration that = new DeprecatedDeclaration();
                that.msg = this.msg;
                that.stc = this.stc;
                return that;
            }
        }
        public static class StaticIfDeclaration extends ConditionalDeclaration
        {
            public  StaticIfDeclaration(Condition condition, DArray<Dsymbol> decl, DArray<Dsymbol> elsedecl) {
                super(condition, decl, elsedecl);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticIfDeclaration() {}

            public StaticIfDeclaration copy() {
                StaticIfDeclaration that = new StaticIfDeclaration();
                that.condition = this.condition;
                that.elsedecl = this.elsedecl;
                return that;
            }
        }
        public static class StaticForeachDeclaration extends AttribDeclaration
        {
            public StaticForeach sfe;
            public  StaticForeachDeclaration(StaticForeach sfe, DArray<Dsymbol> decl) {
                super(decl);
                this.sfe = sfe;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticForeachDeclaration() {}

            public StaticForeachDeclaration copy() {
                StaticForeachDeclaration that = new StaticForeachDeclaration();
                that.sfe = this.sfe;
                that.decl = this.decl;
                return that;
            }
        }
        public static class EnumMember extends VarDeclaration
        {
            public Expression origValue;
            public Type origType;
            public  Expression value() {
                return ((ExpInitializer)this._init).exp;
            }

            public  EnumMember(Loc loc, Identifier id, Expression value, Type origType) {
                super(loc, null, id != null ? id : Id.empty, new ExpInitializer(loc, value), 0L);
                this.origValue = value;
                this.origType = origType;
            }

            public  EnumMember(Loc loc, Identifier id, Expression value, Type memtype, long stc, UserAttributeDeclaration uad, DeprecatedDeclaration dd) {
                this(loc, id, value, memtype);
                this.storage_class = stc;
                this.userAttribDecl = uad;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public EnumMember() {}

            public EnumMember copy() {
                EnumMember that = new EnumMember();
                that.origValue = this.origValue;
                that.origType = this.origType;
                that.type = this.type;
                that._init = this._init;
                that.storage_class = this.storage_class;
                that.ctfeAdrOnStack = this.ctfeAdrOnStack;
                that.sequenceNumber = this.sequenceNumber;
                return that;
            }
        }
        public static class Module extends Package
        {
            public static AggregateDeclaration moduleinfo;
            public FileName srcfile = new FileName();
            public BytePtr arg;
            public  Module(BytePtr filename, Identifier ident, int doDocComment, int doHdrGen) {
                super(ident);
                this.arg = pcopy(filename);
                this.srcfile = new FileName(FileName.defaultExt(toDString(filename), toByteSlice(global.mars_ext)));
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Module() {}

            public Module copy() {
                Module that = new Module();
                that.srcfile = this.srcfile;
                that.arg = this.arg;
                that.isPkgMod = this.isPkgMod;
                that.tag = this.tag;
                return that;
            }
        }
        public static class StructDeclaration extends AggregateDeclaration
        {
            public int zeroInit;
            public int ispod;
            public  StructDeclaration(Loc loc, Identifier id, boolean inObject) {
                super(loc, id);
                this.zeroInit = 0;
                this.ispod = StructPOD.fwd;
                this.type = new TypeStruct(this);
                if (inObject)
                {
                    if (id.equals(Id.ModuleInfo) && !(Module.moduleinfo != null))
                        Module.moduleinfo = this;
                }
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StructDeclaration() {}

            public StructDeclaration copy() {
                StructDeclaration that = new StructDeclaration();
                that.zeroInit = this.zeroInit;
                that.ispod = this.ispod;
                that.protection = this.protection;
                that.sizeok = this.sizeok;
                that.type = this.type;
                return that;
            }
        }
        public static class UnionDeclaration extends StructDeclaration
        {
            public  UnionDeclaration(Loc loc, Identifier id) {
                super(loc, id, false);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UnionDeclaration() {}

            public UnionDeclaration copy() {
                UnionDeclaration that = new UnionDeclaration();
                that.zeroInit = this.zeroInit;
                that.ispod = this.ispod;
                return that;
            }
        }
        public static class ClassDeclaration extends AggregateDeclaration
        {
            public static ClassDeclaration object;
            public static ClassDeclaration throwable;
            public static ClassDeclaration exception;
            public static ClassDeclaration errorException;
            public static ClassDeclaration cpp_type_info_ptr;
            public DArray<BaseClass> baseclasses;
            public int baseok;
            public  ClassDeclaration(Loc loc, Identifier id, DArray<BaseClass> baseclasses, DArray<Dsymbol> members, boolean inObject) {
                super(loc, id == null ? Identifier.generateId(new BytePtr("__anonclass")) : id);
                if (baseclasses != null)
                {
                    this.baseclasses = baseclasses;
                }
                else
                    this.baseclasses = new DArray<BaseClass>();
                this.members = members;
                this.type = new TypeClass(this);
                if (id != null)
                {
                    if (id.equals(Id.__sizeof) || id.equals(Id.__xalignof) || id.equals(Id._mangleof))
                        this.error(new BytePtr("illegal class name"));
                    if ((id.toChars().get(0) & 0xFF) == 84)
                    {
                        if (id.equals(Id.TypeInfo))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.dtypeinfo = this;
                        }
                        if (id.equals(Id.TypeInfo_Class))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfoclass = this;
                        }
                        if (id.equals(Id.TypeInfo_Interface))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfointerface = this;
                        }
                        if (id.equals(Id.TypeInfo_Struct))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfostruct = this;
                        }
                        if (id.equals(Id.TypeInfo_Pointer))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfopointer = this;
                        }
                        if (id.equals(Id.TypeInfo_Array))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfoarray = this;
                        }
                        if (id.equals(Id.TypeInfo_StaticArray))
                        {
                            Type.typeinfostaticarray = this;
                        }
                        if (id.equals(Id.TypeInfo_AssociativeArray))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfoassociativearray = this;
                        }
                        if (id.equals(Id.TypeInfo_Enum))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfoenum = this;
                        }
                        if (id.equals(Id.TypeInfo_Function))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfofunction = this;
                        }
                        if (id.equals(Id.TypeInfo_Delegate))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfodelegate = this;
                        }
                        if (id.equals(Id.TypeInfo_Tuple))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfotypelist = this;
                        }
                        if (id.equals(Id.TypeInfo_Const))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfoconst = this;
                        }
                        if (id.equals(Id.TypeInfo_Invariant))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfoinvariant = this;
                        }
                        if (id.equals(Id.TypeInfo_Shared))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfoshared = this;
                        }
                        if (id.equals(Id.TypeInfo_Wild))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfowild = this;
                        }
                        if (id.equals(Id.TypeInfo_Vector))
                        {
                            if (!(inObject))
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            Type.typeinfovector = this;
                        }
                    }
                    if (id.equals(Id.Object))
                    {
                        if (!(inObject))
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        object = this;
                    }
                    if (id.equals(Id.Throwable))
                    {
                        if (!(inObject))
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        throwable = this;
                    }
                    if (id.equals(Id.Exception))
                    {
                        if (!(inObject))
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        exception = this;
                    }
                    if (id.equals(Id.Error))
                    {
                        if (!(inObject))
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        errorException = this;
                    }
                    if (id.equals(Id.cpp_type_info_ptr))
                    {
                        if (!(inObject))
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        cpp_type_info_ptr = this;
                    }
                }
                this.baseok = Baseok.none;
            }

            public  ClassDeclaration isClassDeclaration() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ClassDeclaration() {}

            public ClassDeclaration copy() {
                ClassDeclaration that = new ClassDeclaration();
                that.baseclasses = this.baseclasses;
                that.baseok = this.baseok;
                that.protection = this.protection;
                that.sizeok = this.sizeok;
                that.type = this.type;
                return that;
            }
        }
        public static class InterfaceDeclaration extends ClassDeclaration
        {
            public  InterfaceDeclaration(Loc loc, Identifier id, DArray<BaseClass> baseclasses) {
                super(loc, id, baseclasses, null, false);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InterfaceDeclaration() {}

            public InterfaceDeclaration copy() {
                InterfaceDeclaration that = new InterfaceDeclaration();
                that.baseclasses = this.baseclasses;
                that.baseok = this.baseok;
                return that;
            }
        }
        public static class TemplateMixin extends TemplateInstance
        {
            public TypeQualified tqual;
            public  TemplateMixin(Loc loc, Identifier ident, TypeQualified tqual, DArray<RootObject> tiargs) {
                super(loc, (tqual.idents.length) != 0 ? (Identifier)tqual.idents.get(tqual.idents.length - 1) : ((TypeIdentifier)tqual).ident, tiargs != null ? tiargs : new DArray<RootObject>());
                this.ident = ident;
                this.tqual = tqual;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateMixin() {}

            public TemplateMixin copy() {
                TemplateMixin that = new TemplateMixin();
                that.tqual = this.tqual;
                that.name = this.name;
                that.tiargs = this.tiargs;
                that.tempdecl = this.tempdecl;
                that.semantictiargsdone = this.semantictiargsdone;
                that.havetempdecl = this.havetempdecl;
                that.inst = this.inst;
                return that;
            }
        }
        public static class ParameterList
        {
            public DArray<Parameter> parameters;
            public int varargs = VarArg.none;
            public ParameterList(){
            }
            public ParameterList copy(){
                ParameterList r = new ParameterList();
                r.parameters = parameters;
                r.varargs = varargs;
                return r;
            }
            public ParameterList(DArray<Parameter> parameters, int varargs) {
                this.parameters = parameters;
                this.varargs = varargs;
            }

            public ParameterList opAssign(ParameterList that) {
                this.parameters = that.parameters;
                this.varargs = that.varargs;
                return this;
            }
        }
        public static class Parameter extends ASTNode
        {
            public long storageClass;
            public Type type;
            public Identifier ident;
            public Expression defaultArg;
            public UserAttributeDeclaration userAttribDecl;
            public  Parameter(long storageClass, Type type, Identifier ident, Expression defaultArg, UserAttributeDeclaration userAttribDecl) {
                super();
                this.storageClass = storageClass;
                this.type = type;
                this.ident = ident;
                this.defaultArg = defaultArg;
                this.userAttribDecl = userAttribDecl;
            }

            public static int dim(DArray<Parameter> parameters) {
                IntRef nargs = ref(0);
                Function2<Integer,Parameter,Integer> dimDg = new Function2<Integer,Parameter,Integer>(){
                    public Integer invoke(Integer n, Parameter p){
                        nargs.value += 1;
                        return 0;
                    }
                };
                _foreach(parameters, dimDg, null);
                return nargs.value;
            }

            public static Parameter getNth(DArray<Parameter> parameters, int nth, IntPtr pn) {
                IntRef nth_ref = ref(nth);
                Ref<Parameter> param = ref(null);
                Function2<Integer,Parameter,Integer> getNthParamDg = new Function2<Integer,Parameter,Integer>(){
                    public Integer invoke(Integer n, Parameter p){
                        if (n == nth_ref.value)
                        {
                            param.value = p;
                            return 1;
                        }
                        return 0;
                    }
                };
                int res = _foreach(parameters, getNthParamDg, null);
                return (res) != 0 ? param.value : null;
            }

            public static int _foreach(DArray<Parameter> parameters, Function2<Integer,Parameter,Integer> dg, IntPtr pn) {
                assert(dg != null);
                if (parameters == null)
                    return 0;
                IntRef n = ref(pn != null ? pn.get() : 0);
                int result = 0;
                {
                    int __key62 = 0;
                    int __limit63 = (parameters).length;
                    for (; __key62 < __limit63;__key62 += 1) {
                        int i = __key62;
                        Parameter p = (parameters).get(i);
                        Type t = p.type.toBasetype();
                        if ((t.ty & 0xFF) == ENUMTY.Ttuple)
                        {
                            TypeTuple tu = (TypeTuple)t;
                            result = _foreach(tu.arguments, dg, ptr(n));
                        }
                        else
                            result = dg.invoke(n.value++, p);
                        if ((result) != 0)
                            break;
                    }
                }
                if (pn != null)
                    pn.set(0, n.value);
                return result;
            }

            public  Parameter syntaxCopy() {
                return new Parameter(this.storageClass, this.type != null ? this.type.syntaxCopy() : null, this.ident, this.defaultArg != null ? this.defaultArg.syntaxCopy() : null, this.userAttribDecl != null ? (UserAttributeDeclaration)this.userAttribDecl.syntaxCopy(null) : null);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }

            public static DArray<Parameter> arraySyntaxCopy(DArray<Parameter> parameters) {
                DArray<Parameter> params = null;
                if (parameters != null)
                {
                    params = new DArray<Parameter>();
                    (params).setDim((parameters).length);
                    {
                        int i = 0;
                        for (; i < (params).length;i++) {
                            params.set(i, (parameters).get(i).syntaxCopy());
                        }
                    }
                }
                return params;
            }


            public Parameter() {}

            public Parameter copy() {
                Parameter that = new Parameter();
                that.storageClass = this.storageClass;
                that.type = this.type;
                that.ident = this.ident;
                that.defaultArg = this.defaultArg;
                that.userAttribDecl = this.userAttribDecl;
                return that;
            }
        }
        public static abstract class Statement extends ASTNode
        {
            public Loc loc = new Loc();
            public  Statement(Loc loc) {
                super();
                this.loc = loc.copy();
            }

            public  ExpStatement isExpStatement() {
                return null;
            }

            public  CompoundStatement isCompoundStatement() {
                return null;
            }

            public  ReturnStatement isReturnStatement() {
                return null;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Statement() {}

            public abstract Statement copy();
        }
        public static class ImportStatement extends Statement
        {
            public DArray<Dsymbol> imports;
            public  ImportStatement(Loc loc, DArray<Dsymbol> imports) {
                super(loc);
                this.imports = imports;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ImportStatement() {}

            public ImportStatement copy() {
                ImportStatement that = new ImportStatement();
                that.imports = this.imports;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ScopeStatement extends Statement
        {
            public Statement statement;
            public Loc endloc = new Loc();
            public  ScopeStatement(Loc loc, Statement s, Loc endloc) {
                super(loc);
                this.statement = s;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ScopeStatement() {}

            public ScopeStatement copy() {
                ScopeStatement that = new ScopeStatement();
                that.statement = this.statement;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ReturnStatement extends Statement
        {
            public Expression exp;
            public  ReturnStatement(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            public  ReturnStatement isReturnStatement() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ReturnStatement() {}

            public ReturnStatement copy() {
                ReturnStatement that = new ReturnStatement();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class LabelStatement extends Statement
        {
            public Identifier ident;
            public Statement statement;
            public  LabelStatement(Loc loc, Identifier ident, Statement statement) {
                super(loc);
                this.ident = ident;
                this.statement = statement;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LabelStatement() {}

            public LabelStatement copy() {
                LabelStatement that = new LabelStatement();
                that.ident = this.ident;
                that.statement = this.statement;
                that.loc = this.loc;
                return that;
            }
        }
        public static class StaticAssertStatement extends Statement
        {
            public StaticAssert sa;
            public  StaticAssertStatement(StaticAssert sa) {
                super(sa.loc);
                this.sa = sa;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticAssertStatement() {}

            public StaticAssertStatement copy() {
                StaticAssertStatement that = new StaticAssertStatement();
                that.sa = this.sa;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CompileStatement extends Statement
        {
            public DArray<Expression> exps;
            public  CompileStatement(Loc loc, DArray<Expression> exps) {
                super(loc);
                this.exps = exps;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompileStatement() {}

            public CompileStatement copy() {
                CompileStatement that = new CompileStatement();
                that.exps = this.exps;
                that.loc = this.loc;
                return that;
            }
        }
        public static class WhileStatement extends Statement
        {
            public Expression condition;
            public Statement _body;
            public Loc endloc = new Loc();
            public  WhileStatement(Loc loc, Expression c, Statement b, Loc endloc) {
                super(loc);
                this.condition = c;
                this._body = b;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public WhileStatement() {}

            public WhileStatement copy() {
                WhileStatement that = new WhileStatement();
                that.condition = this.condition;
                that._body = this._body;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ForStatement extends Statement
        {
            public Statement _init;
            public Expression condition;
            public Expression increment;
            public Statement _body;
            public Loc endloc = new Loc();
            public  ForStatement(Loc loc, Statement _init, Expression condition, Expression increment, Statement _body, Loc endloc) {
                super(loc);
                this._init = _init;
                this.condition = condition;
                this.increment = increment;
                this._body = _body;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ForStatement() {}

            public ForStatement copy() {
                ForStatement that = new ForStatement();
                that._init = this._init;
                that.condition = this.condition;
                that.increment = this.increment;
                that._body = this._body;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DoStatement extends Statement
        {
            public Statement _body;
            public Expression condition;
            public Loc endloc = new Loc();
            public  DoStatement(Loc loc, Statement b, Expression c, Loc endloc) {
                super(loc);
                this._body = b;
                this.condition = c;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DoStatement() {}

            public DoStatement copy() {
                DoStatement that = new DoStatement();
                that._body = this._body;
                that.condition = this.condition;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ForeachRangeStatement extends Statement
        {
            public byte op;
            public Parameter prm;
            public Expression lwr;
            public Expression upr;
            public Statement _body;
            public Loc endloc = new Loc();
            public  ForeachRangeStatement(Loc loc, byte op, Parameter prm, Expression lwr, Expression upr, Statement _body, Loc endloc) {
                super(loc);
                this.op = op;
                this.prm = prm;
                this.lwr = lwr;
                this.upr = upr;
                this._body = _body;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ForeachRangeStatement() {}

            public ForeachRangeStatement copy() {
                ForeachRangeStatement that = new ForeachRangeStatement();
                that.op = this.op;
                that.prm = this.prm;
                that.lwr = this.lwr;
                that.upr = this.upr;
                that._body = this._body;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ForeachStatement extends Statement
        {
            public byte op;
            public DArray<Parameter> parameters;
            public Expression aggr;
            public Statement _body;
            public Loc endloc = new Loc();
            public  ForeachStatement(Loc loc, byte op, DArray<Parameter> parameters, Expression aggr, Statement _body, Loc endloc) {
                super(loc);
                this.op = op;
                this.parameters = parameters;
                this.aggr = aggr;
                this._body = _body;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ForeachStatement() {}

            public ForeachStatement copy() {
                ForeachStatement that = new ForeachStatement();
                that.op = this.op;
                that.parameters = this.parameters;
                that.aggr = this.aggr;
                that._body = this._body;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IfStatement extends Statement
        {
            public Parameter prm;
            public Expression condition;
            public Statement ifbody;
            public Statement elsebody;
            public VarDeclaration match;
            public Loc endloc = new Loc();
            public  IfStatement(Loc loc, Parameter prm, Expression condition, Statement ifbody, Statement elsebody, Loc endloc) {
                super(loc);
                this.prm = prm;
                this.condition = condition;
                this.ifbody = ifbody;
                this.elsebody = elsebody;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IfStatement() {}

            public IfStatement copy() {
                IfStatement that = new IfStatement();
                that.prm = this.prm;
                that.condition = this.condition;
                that.ifbody = this.ifbody;
                that.elsebody = this.elsebody;
                that.match = this.match;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ScopeGuardStatement extends Statement
        {
            public byte tok;
            public Statement statement;
            public  ScopeGuardStatement(Loc loc, byte tok, Statement statement) {
                super(loc);
                this.tok = tok;
                this.statement = statement;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ScopeGuardStatement() {}

            public ScopeGuardStatement copy() {
                ScopeGuardStatement that = new ScopeGuardStatement();
                that.tok = this.tok;
                that.statement = this.statement;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ConditionalStatement extends Statement
        {
            public Condition condition;
            public Statement ifbody;
            public Statement elsebody;
            public  ConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
                super(loc);
                this.condition = condition;
                this.ifbody = ifbody;
                this.elsebody = elsebody;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ConditionalStatement() {}

            public ConditionalStatement copy() {
                ConditionalStatement that = new ConditionalStatement();
                that.condition = this.condition;
                that.ifbody = this.ifbody;
                that.elsebody = this.elsebody;
                that.loc = this.loc;
                return that;
            }
        }
        public static class StaticForeachStatement extends Statement
        {
            public StaticForeach sfe;
            public  StaticForeachStatement(Loc loc, StaticForeach sfe) {
                super(loc);
                this.sfe = sfe;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticForeachStatement() {}

            public StaticForeachStatement copy() {
                StaticForeachStatement that = new StaticForeachStatement();
                that.sfe = this.sfe;
                that.loc = this.loc;
                return that;
            }
        }
        public static class PragmaStatement extends Statement
        {
            public Identifier ident;
            public DArray<Expression> args;
            public Statement _body;
            public  PragmaStatement(Loc loc, Identifier ident, DArray<Expression> args, Statement _body) {
                super(loc);
                this.ident = ident;
                this.args = args;
                this._body = _body;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PragmaStatement() {}

            public PragmaStatement copy() {
                PragmaStatement that = new PragmaStatement();
                that.ident = this.ident;
                that.args = this.args;
                that._body = this._body;
                that.loc = this.loc;
                return that;
            }
        }
        public static class SwitchStatement extends Statement
        {
            public Expression condition;
            public Statement _body;
            public boolean isFinal;
            public  SwitchStatement(Loc loc, Expression c, Statement b, boolean isFinal) {
                super(loc);
                this.condition = c;
                this._body = b;
                this.isFinal = isFinal;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SwitchStatement() {}

            public SwitchStatement copy() {
                SwitchStatement that = new SwitchStatement();
                that.condition = this.condition;
                that._body = this._body;
                that.isFinal = this.isFinal;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CaseRangeStatement extends Statement
        {
            public Expression first;
            public Expression last;
            public Statement statement;
            public  CaseRangeStatement(Loc loc, Expression first, Expression last, Statement s) {
                super(loc);
                this.first = first;
                this.last = last;
                this.statement = s;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CaseRangeStatement() {}

            public CaseRangeStatement copy() {
                CaseRangeStatement that = new CaseRangeStatement();
                that.first = this.first;
                that.last = this.last;
                that.statement = this.statement;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CaseStatement extends Statement
        {
            public Expression exp;
            public Statement statement;
            public  CaseStatement(Loc loc, Expression exp, Statement s) {
                super(loc);
                this.exp = exp;
                this.statement = s;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CaseStatement() {}

            public CaseStatement copy() {
                CaseStatement that = new CaseStatement();
                that.exp = this.exp;
                that.statement = this.statement;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DefaultStatement extends Statement
        {
            public Statement statement;
            public  DefaultStatement(Loc loc, Statement s) {
                super(loc);
                this.statement = s;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DefaultStatement() {}

            public DefaultStatement copy() {
                DefaultStatement that = new DefaultStatement();
                that.statement = this.statement;
                that.loc = this.loc;
                return that;
            }
        }
        public static class BreakStatement extends Statement
        {
            public Identifier ident;
            public  BreakStatement(Loc loc, Identifier ident) {
                super(loc);
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public BreakStatement() {}

            public BreakStatement copy() {
                BreakStatement that = new BreakStatement();
                that.ident = this.ident;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ContinueStatement extends Statement
        {
            public Identifier ident;
            public  ContinueStatement(Loc loc, Identifier ident) {
                super(loc);
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ContinueStatement() {}

            public ContinueStatement copy() {
                ContinueStatement that = new ContinueStatement();
                that.ident = this.ident;
                that.loc = this.loc;
                return that;
            }
        }
        public static class GotoDefaultStatement extends Statement
        {
            public  GotoDefaultStatement(Loc loc) {
                super(loc);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GotoDefaultStatement() {}

            public GotoDefaultStatement copy() {
                GotoDefaultStatement that = new GotoDefaultStatement();
                that.loc = this.loc;
                return that;
            }
        }
        public static class GotoCaseStatement extends Statement
        {
            public Expression exp;
            public  GotoCaseStatement(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GotoCaseStatement() {}

            public GotoCaseStatement copy() {
                GotoCaseStatement that = new GotoCaseStatement();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class GotoStatement extends Statement
        {
            public Identifier ident;
            public  GotoStatement(Loc loc, Identifier ident) {
                super(loc);
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GotoStatement() {}

            public GotoStatement copy() {
                GotoStatement that = new GotoStatement();
                that.ident = this.ident;
                that.loc = this.loc;
                return that;
            }
        }
        public static class SynchronizedStatement extends Statement
        {
            public Expression exp;
            public Statement _body;
            public  SynchronizedStatement(Loc loc, Expression exp, Statement _body) {
                super(loc);
                this.exp = exp;
                this._body = _body;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SynchronizedStatement() {}

            public SynchronizedStatement copy() {
                SynchronizedStatement that = new SynchronizedStatement();
                that.exp = this.exp;
                that._body = this._body;
                that.loc = this.loc;
                return that;
            }
        }
        public static class WithStatement extends Statement
        {
            public Expression exp;
            public Statement _body;
            public Loc endloc = new Loc();
            public  WithStatement(Loc loc, Expression exp, Statement _body, Loc endloc) {
                super(loc);
                this.exp = exp;
                this._body = _body;
                this.endloc = endloc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public WithStatement() {}

            public WithStatement copy() {
                WithStatement that = new WithStatement();
                that.exp = this.exp;
                that._body = this._body;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TryCatchStatement extends Statement
        {
            public Statement _body;
            public DArray<Catch> catches;
            public  TryCatchStatement(Loc loc, Statement _body, DArray<Catch> catches) {
                super(loc);
                this._body = _body;
                this.catches = catches;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TryCatchStatement() {}

            public TryCatchStatement copy() {
                TryCatchStatement that = new TryCatchStatement();
                that._body = this._body;
                that.catches = this.catches;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TryFinallyStatement extends Statement
        {
            public Statement _body;
            public Statement finalbody;
            public  TryFinallyStatement(Loc loc, Statement _body, Statement finalbody) {
                super(loc);
                this._body = _body;
                this.finalbody = finalbody;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TryFinallyStatement() {}

            public TryFinallyStatement copy() {
                TryFinallyStatement that = new TryFinallyStatement();
                that._body = this._body;
                that.finalbody = this.finalbody;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ThrowStatement extends Statement
        {
            public Expression exp;
            public  ThrowStatement(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ThrowStatement() {}

            public ThrowStatement copy() {
                ThrowStatement that = new ThrowStatement();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AsmStatement extends Statement
        {
            public Token tokens;
            public  AsmStatement(Loc loc, Token tokens) {
                super(loc);
                this.tokens = tokens;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AsmStatement() {}

            public AsmStatement copy() {
                AsmStatement that = new AsmStatement();
                that.tokens = this.tokens;
                that.loc = this.loc;
                return that;
            }
        }
        public static class InlineAsmStatement extends AsmStatement
        {
            public  InlineAsmStatement(Loc loc, Token tokens) {
                super(loc, tokens);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InlineAsmStatement() {}

            public InlineAsmStatement copy() {
                InlineAsmStatement that = new InlineAsmStatement();
                that.tokens = this.tokens;
                return that;
            }
        }
        public static class GccAsmStatement extends AsmStatement
        {
            public  GccAsmStatement(Loc loc, Token tokens) {
                super(loc, tokens);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GccAsmStatement() {}

            public GccAsmStatement copy() {
                GccAsmStatement that = new GccAsmStatement();
                that.tokens = this.tokens;
                return that;
            }
        }
        public static class ExpStatement extends Statement
        {
            public Expression exp;
            public  ExpStatement(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            public  ExpStatement(Loc loc, Dsymbol declaration) {
                super(loc);
                this.exp = new DeclarationExp(loc, declaration);
            }

            public  ExpStatement isExpStatement() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ExpStatement() {}

            public ExpStatement copy() {
                ExpStatement that = new ExpStatement();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CompoundStatement extends Statement
        {
            public DArray<Statement> statements;
            public  CompoundStatement(Loc loc, DArray<Statement> statements) {
                super(loc);
                this.statements = statements;
            }

            public  CompoundStatement(Loc loc, Slice<Statement> sts) {
                super(loc);
                this.statements = new DArray<Statement>();
                (this.statements).reserve(sts.getLength());
                {
                    Slice<Statement> __r64 = sts.copy();
                    int __key65 = 0;
                    for (; __key65 < __r64.getLength();__key65 += 1) {
                        Statement s = __r64.get(__key65);
                        (this.statements).push(s);
                    }
                }
            }

            public  CompoundStatement isCompoundStatement() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompoundStatement() {}

            public CompoundStatement copy() {
                CompoundStatement that = new CompoundStatement();
                that.statements = this.statements;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CompoundDeclarationStatement extends CompoundStatement
        {
            public  CompoundDeclarationStatement(Loc loc, DArray<Statement> statements) {
                super(loc, statements);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompoundDeclarationStatement() {}

            public CompoundDeclarationStatement copy() {
                CompoundDeclarationStatement that = new CompoundDeclarationStatement();
                that.statements = this.statements;
                return that;
            }
        }
        public static class CompoundAsmStatement extends CompoundStatement
        {
            public long stc;
            public  CompoundAsmStatement(Loc loc, DArray<Statement> s, long stc) {
                super(loc, s);
                this.stc = stc;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompoundAsmStatement() {}

            public CompoundAsmStatement copy() {
                CompoundAsmStatement that = new CompoundAsmStatement();
                that.stc = this.stc;
                that.statements = this.statements;
                return that;
            }
        }
        public static class Catch extends RootObject
        {
            public Loc loc = new Loc();
            public Type type;
            public Identifier ident;
            public Statement handler;
            public  Catch(Loc loc, Type t, Identifier id, Statement handler) {
                super();
                this.loc = loc.copy();
                this.type = t;
                this.ident = id;
                this.handler = handler;
            }


            public Catch() {}

            public Catch copy() {
                Catch that = new Catch();
                that.loc = this.loc;
                that.type = this.type;
                that.ident = this.ident;
                that.handler = this.handler;
                return that;
            }
        }
        public static abstract class Type extends ASTNode
        {
            public byte ty;
            public byte mod;
            public BytePtr deco;
            public static Type tvoid;
            public static Type tint8;
            public static Type tuns8;
            public static Type tint16;
            public static Type tuns16;
            public static Type tint32;
            public static Type tuns32;
            public static Type tint64;
            public static Type tuns64;
            public static Type tint128;
            public static Type tuns128;
            public static Type tfloat32;
            public static Type tfloat64;
            public static Type tfloat80;
            public static Type timaginary32;
            public static Type timaginary64;
            public static Type timaginary80;
            public static Type tcomplex32;
            public static Type tcomplex64;
            public static Type tcomplex80;
            public static Type tbool;
            public static Type tchar;
            public static Type twchar;
            public static Type tdchar;
            public static Slice<Type> basic = new Slice<Type>(new Type[44]);
            public static Type tshiftcnt;
            public static Type tvoidptr;
            public static Type tstring;
            public static Type twstring;
            public static Type tdstring;
            public static Type tvalist;
            public static Type terror;
            public static Type tnull;
            public static Type tsize_t;
            public static Type tptrdiff_t;
            public static Type thash_t;
            public static ClassDeclaration dtypeinfo;
            public static ClassDeclaration typeinfoclass;
            public static ClassDeclaration typeinfointerface;
            public static ClassDeclaration typeinfostruct;
            public static ClassDeclaration typeinfopointer;
            public static ClassDeclaration typeinfoarray;
            public static ClassDeclaration typeinfostaticarray;
            public static ClassDeclaration typeinfoassociativearray;
            public static ClassDeclaration typeinfovector;
            public static ClassDeclaration typeinfoenum;
            public static ClassDeclaration typeinfofunction;
            public static ClassDeclaration typeinfodelegate;
            public static ClassDeclaration typeinfotypelist;
            public static ClassDeclaration typeinfoconst;
            public static ClassDeclaration typeinfoinvariant;
            public static ClassDeclaration typeinfoshared;
            public static ClassDeclaration typeinfowild;
            public static StringTable stringtable = new StringTable();
            public static ByteSlice sizeTy = slice(new byte[]{(byte)60, (byte)64, (byte)76, (byte)60, (byte)60, (byte)96, (byte)88, (byte)64, (byte)64, (byte)60, (byte)60, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)56, (byte)88, (byte)88, (byte)60, (byte)68, (byte)84, (byte)56, (byte)60, (byte)64, (byte)64});
            public Type cto;
            public Type ito;
            public Type sto;
            public Type scto;
            public Type wto;
            public Type wcto;
            public Type swto;
            public Type swcto;
            public Type pto;
            public Type rto;
            public Type arrayof;
            public  Type(byte ty) {
                super();
                this.ty = ty;
            }

            public  BytePtr toChars() {
                return new BytePtr("type");
            }

            public static void _init() {
                stringtable._init(14000);
                {
                    int i = 0;
                    for (; (astbase._initbasetab.get(i) & 0xFF) != ENUMTY.Terror;i++){
                        Type t = new TypeBasic(astbase._initbasetab.get(i));
                        t = t.merge();
                        basic.set(((astbase._initbasetab.get(i) & 0xFF)), t);
                    }
                }
                basic.set(34, new TypeError());
                tvoid = basic.get(12);
                tint8 = basic.get(13);
                tuns8 = basic.get(14);
                tint16 = basic.get(15);
                tuns16 = basic.get(16);
                tint32 = basic.get(17);
                tuns32 = basic.get(18);
                tint64 = basic.get(19);
                tuns64 = basic.get(20);
                tint128 = basic.get(42);
                tuns128 = basic.get(43);
                tfloat32 = basic.get(21);
                tfloat64 = basic.get(22);
                tfloat80 = basic.get(23);
                timaginary32 = basic.get(24);
                timaginary64 = basic.get(25);
                timaginary80 = basic.get(26);
                tcomplex32 = basic.get(27);
                tcomplex64 = basic.get(28);
                tcomplex80 = basic.get(29);
                tbool = basic.get(30);
                tchar = basic.get(31);
                twchar = basic.get(32);
                tdchar = basic.get(33);
                tshiftcnt = tint32;
                terror = basic.get(34);
                tnull = basic.get(40);
                tnull = new TypeNull();
                tnull.deco = pcopy(tnull.merge().deco);
                tvoidptr = tvoid.pointerTo();
                tstring = tchar.immutableOf().arrayOf();
                twstring = twchar.immutableOf().arrayOf();
                tdstring = tdchar.immutableOf().arrayOf();
                tvalist = Target.va_listType();
                boolean isLP64 = global.params.isLP64;
                tsize_t = basic.get(isLP64 ? 20 : 18);
                tptrdiff_t = basic.get(isLP64 ? 19 : 17);
                thash_t = tsize_t;
            }

            public  Type pointerTo() {
                if ((this.ty & 0xFF) == ENUMTY.Terror)
                    return this;
                if (!(this.pto != null))
                {
                    Type t = new TypePointer(this);
                    if ((this.ty & 0xFF) == ENUMTY.Tfunction)
                    {
                        t.deco = pcopy(t.merge().deco);
                        this.pto = t;
                    }
                    else
                        this.pto = t.merge();
                }
                return this.pto;
            }

            public  Type arrayOf() {
                if ((this.ty & 0xFF) == ENUMTY.Terror)
                    return this;
                if (!(this.arrayof != null))
                {
                    Type t = new TypeDArray(this);
                    this.arrayof = t.merge();
                }
                return this.arrayof;
            }

            public  boolean isImmutable() {
                return ((this.mod & 0xFF) & MODFlags.immutable_) != 0;
            }

            public  Type nullAttributes() {
                int sz = (sizeTy.get((this.ty & 0xFF)) & 0xFF);
                Type t = null;
                (t) = (this).copy();
                t.deco = null;
                t.arrayof = null;
                t.pto = null;
                t.rto = null;
                t.cto = null;
                t.ito = null;
                t.sto = null;
                t.scto = null;
                t.wto = null;
                t.wcto = null;
                t.swto = null;
                t.swcto = null;
                if ((t.ty & 0xFF) == ENUMTY.Tstruct)
                    ((TypeStruct)t).att = AliasThisRec.fwdref;
                if ((t.ty & 0xFF) == ENUMTY.Tclass)
                    ((TypeClass)t).att = AliasThisRec.fwdref;
                return t;
            }

            public  Type makeConst() {
                if (this.cto != null)
                    return this.cto;
                Type t = this.nullAttributes();
                t.mod = (byte)1;
                return t;
            }

            public  Type makeWildConst() {
                if (this.wcto != null)
                    return this.wcto;
                Type t = this.nullAttributes();
                t.mod = (byte)9;
                return t;
            }

            public  Type makeShared() {
                if (this.sto != null)
                    return this.sto;
                Type t = this.nullAttributes();
                t.mod = (byte)2;
                return t;
            }

            public  Type makeSharedConst() {
                if (this.scto != null)
                    return this.scto;
                Type t = this.nullAttributes();
                t.mod = (byte)3;
                return t;
            }

            public  Type makeImmutable() {
                if (this.ito != null)
                    return this.ito;
                Type t = this.nullAttributes();
                t.mod = (byte)4;
                return t;
            }

            public  Type makeWild() {
                if (this.wto != null)
                    return this.wto;
                Type t = this.nullAttributes();
                t.mod = (byte)8;
                return t;
            }

            public  Type makeSharedWildConst() {
                if (this.swcto != null)
                    return this.swcto;
                Type t = this.nullAttributes();
                t.mod = (byte)11;
                return t;
            }

            public  Type makeSharedWild() {
                if (this.swto != null)
                    return this.swto;
                Type t = this.nullAttributes();
                t.mod = (byte)10;
                return t;
            }

            public  Type merge() {
                if ((this.ty & 0xFF) == ENUMTY.Terror)
                    return this;
                if ((this.ty & 0xFF) == ENUMTY.Ttypeof)
                    return this;
                if ((this.ty & 0xFF) == ENUMTY.Tident)
                    return this;
                if ((this.ty & 0xFF) == ENUMTY.Tinstance)
                    return this;
                if ((this.ty & 0xFF) == ENUMTY.Taarray && ((TypeAArray)this).index.merge().deco == null)
                    return this;
                if ((this.ty & 0xFF) != ENUMTY.Tenum && this.nextOf() != null && this.nextOf().deco == null)
                    return this;
                Type t = this;
                assert(t != null);
                return t;
            }

            public  Type addSTC(long stc) {
                Type t = this;
                if (t.isImmutable())
                {
                }
                else if ((stc & 1048576L) != 0)
                {
                    t = t.makeImmutable();
                }
                else
                {
                    if ((stc & 536870912L) != 0 && !(t.isShared()))
                    {
                        if (t.isWild())
                        {
                            if (t.isConst())
                                t = t.makeSharedWildConst();
                            else
                                t = t.makeSharedWild();
                        }
                        else
                        {
                            if (t.isConst())
                                t = t.makeSharedConst();
                            else
                                t = t.makeShared();
                        }
                    }
                    if ((stc & 4L) != 0 && !(t.isConst()))
                    {
                        if (t.isShared())
                        {
                            if (t.isWild())
                                t = t.makeSharedWildConst();
                            else
                                t = t.makeSharedConst();
                        }
                        else
                        {
                            if (t.isWild())
                                t = t.makeWildConst();
                            else
                                t = t.makeConst();
                        }
                    }
                    if ((stc & 2147483648L) != 0 && !(t.isWild()))
                    {
                        if (t.isShared())
                        {
                            if (t.isConst())
                                t = t.makeSharedWildConst();
                            else
                                t = t.makeSharedWild();
                        }
                        else
                        {
                            if (t.isConst())
                                t = t.makeWildConst();
                            else
                                t = t.makeWild();
                        }
                    }
                }
                return t;
            }

            public  Expression toExpression() {
                return null;
            }

            public  Type syntaxCopy() {
                return null;
            }

            public  Type sharedWildConstOf() {
                if ((this.mod & 0xFF) == 11)
                    return this;
                if (this.swcto != null)
                {
                    assert((this.swcto.mod & 0xFF) == 11);
                    return this.swcto;
                }
                Type t = this.makeSharedWildConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  Type sharedConstOf() {
                if ((this.mod & 0xFF) == 3)
                    return this;
                if (this.scto != null)
                {
                    assert((this.scto.mod & 0xFF) == 3);
                    return this.scto;
                }
                Type t = this.makeSharedConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  Type wildConstOf() {
                if ((this.mod & 0xFF) == MODFlags.wildconst)
                    return this;
                if (this.wcto != null)
                {
                    assert((this.wcto.mod & 0xFF) == MODFlags.wildconst);
                    return this.wcto;
                }
                Type t = this.makeWildConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  Type constOf() {
                if ((this.mod & 0xFF) == MODFlags.const_)
                    return this;
                if (this.cto != null)
                {
                    assert((this.cto.mod & 0xFF) == MODFlags.const_);
                    return this.cto;
                }
                Type t = this.makeConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  Type sharedWildOf() {
                if ((this.mod & 0xFF) == 10)
                    return this;
                if (this.swto != null)
                {
                    assert((this.swto.mod & 0xFF) == 10);
                    return this.swto;
                }
                Type t = this.makeSharedWild();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  Type wildOf() {
                if ((this.mod & 0xFF) == MODFlags.wild)
                    return this;
                if (this.wto != null)
                {
                    assert((this.wto.mod & 0xFF) == MODFlags.wild);
                    return this.wto;
                }
                Type t = this.makeWild();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  Type sharedOf() {
                if ((this.mod & 0xFF) == MODFlags.shared_)
                    return this;
                if (this.sto != null)
                {
                    assert((this.sto.mod & 0xFF) == MODFlags.shared_);
                    return this.sto;
                }
                Type t = this.makeShared();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  Type immutableOf() {
                if (this.isImmutable())
                    return this;
                if (this.ito != null)
                {
                    assert(this.ito.isImmutable());
                    return this.ito;
                }
                Type t = this.makeImmutable();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            public  void fixTo(Type t) {
                Type mto = null;
                Type tn = this.nextOf();
                if (!(tn != null) || (this.ty & 0xFF) != ENUMTY.Tsarray && (tn.mod & 0xFF) == (t.nextOf().mod & 0xFF))
                {
                    switch ((t.mod & 0xFF))
                    {
                        case 0:
                            mto = t;
                            break;
                        case 1:
                            this.cto = t;
                            break;
                        case 8:
                            this.wto = t;
                            break;
                        case 9:
                            this.wcto = t;
                            break;
                        case 2:
                            this.sto = t;
                            break;
                        case 3:
                            this.scto = t;
                            break;
                        case 10:
                            this.swto = t;
                            break;
                        case 11:
                            this.swcto = t;
                            break;
                        case 4:
                            this.ito = t;
                            break;
                        default:
                        break;
                    }
                }
                assert((this.mod & 0xFF) != (t.mod & 0xFF));
                switch ((this.mod & 0xFF))
                {
                    case 0:
                        break;
                    case 1:
                        this.cto = mto;
                        t.cto = this;
                        break;
                    case 8:
                        this.wto = mto;
                        t.wto = this;
                        break;
                    case 9:
                        this.wcto = mto;
                        t.wcto = this;
                        break;
                    case 2:
                        this.sto = mto;
                        t.sto = this;
                        break;
                    case 3:
                        this.scto = mto;
                        t.scto = this;
                        break;
                    case 10:
                        this.swto = mto;
                        t.swto = this;
                        break;
                    case 11:
                        this.swcto = mto;
                        t.swcto = this;
                        break;
                    case 4:
                        t.ito = this;
                        if (t.cto != null)
                            t.cto.ito = this;
                        if (t.sto != null)
                            t.sto.ito = this;
                        if (t.scto != null)
                            t.scto.ito = this;
                        if (t.wto != null)
                            t.wto.ito = this;
                        if (t.wcto != null)
                            t.wcto.ito = this;
                        if (t.swto != null)
                            t.swto.ito = this;
                        if (t.swcto != null)
                            t.swcto.ito = this;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }

            public  Type addMod(byte mod) {
                Type t = this;
                if (!(t.isImmutable()))
                {
                    switch ((mod & 0xFF))
                    {
                        case 0:
                            break;
                        case 1:
                            if (this.isShared())
                            {
                                if (this.isWild())
                                    t = this.sharedWildConstOf();
                                else
                                    t = this.sharedConstOf();
                            }
                            else
                            {
                                if (this.isWild())
                                    t = this.wildConstOf();
                                else
                                    t = this.constOf();
                            }
                            break;
                        case 8:
                            if (this.isShared())
                            {
                                if (this.isConst())
                                    t = this.sharedWildConstOf();
                                else
                                    t = this.sharedWildOf();
                            }
                            else
                            {
                                if (this.isConst())
                                    t = this.wildConstOf();
                                else
                                    t = this.wildOf();
                            }
                            break;
                        case 9:
                            if (this.isShared())
                                t = this.sharedWildConstOf();
                            else
                                t = this.wildConstOf();
                            break;
                        case 2:
                            if (this.isWild())
                            {
                                if (this.isConst())
                                    t = this.sharedWildConstOf();
                                else
                                    t = this.sharedWildOf();
                            }
                            else
                            {
                                if (this.isConst())
                                    t = this.sharedConstOf();
                                else
                                    t = this.sharedOf();
                            }
                            break;
                        case 3:
                            if (this.isWild())
                                t = this.sharedWildConstOf();
                            else
                                t = this.sharedConstOf();
                            break;
                        case 10:
                            if (this.isConst())
                                t = this.sharedWildConstOf();
                            else
                                t = this.sharedWildOf();
                            break;
                        case 11:
                            t = this.sharedWildConstOf();
                            break;
                        case 4:
                            t = this.immutableOf();
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                }
                return t;
            }

            public  Type nextOf() {
                return null;
            }

            public  boolean isscalar() {
                return false;
            }

            public  boolean isConst() {
                return ((this.mod & 0xFF) & MODFlags.const_) != 0;
            }

            public  boolean isWild() {
                return ((this.mod & 0xFF) & MODFlags.wild) != 0;
            }

            public  boolean isShared() {
                return ((this.mod & 0xFF) & MODFlags.shared_) != 0;
            }

            public  Type toBasetype() {
                return this;
            }

            public  Dsymbol toDsymbol(Scope sc) {
                return null;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Type() {}

            public abstract Type copy();
        }
        public static class TypeBasic extends Type
        {
            public BytePtr dstring;
            public int flags;
            public  TypeBasic(byte ty) {
                super(ty);
                BytePtr d = null;
                int flags = 0;
                switch ((ty & 0xFF))
                {
                    case 12:
                        d = pcopy(Token.toChars(TOK.void_));
                        break;
                    case 13:
                        d = pcopy(Token.toChars(TOK.int8));
                        flags |= 1;
                        break;
                    case 14:
                        d = pcopy(Token.toChars(TOK.uns8));
                        flags |= 5;
                        break;
                    case 15:
                        d = pcopy(Token.toChars(TOK.int16));
                        flags |= 1;
                        break;
                    case 16:
                        d = pcopy(Token.toChars(TOK.uns16));
                        flags |= 5;
                        break;
                    case 17:
                        d = pcopy(Token.toChars(TOK.int32));
                        flags |= 1;
                        break;
                    case 18:
                        d = pcopy(Token.toChars(TOK.uns32));
                        flags |= 5;
                        break;
                    case 21:
                        d = pcopy(Token.toChars(TOK.float32));
                        flags |= 10;
                        break;
                    case 19:
                        d = pcopy(Token.toChars(TOK.int64));
                        flags |= 1;
                        break;
                    case 20:
                        d = pcopy(Token.toChars(TOK.uns64));
                        flags |= 5;
                        break;
                    case 42:
                        d = pcopy(Token.toChars(TOK.int128));
                        flags |= 1;
                        break;
                    case 43:
                        d = pcopy(Token.toChars(TOK.uns128));
                        flags |= 5;
                        break;
                    case 22:
                        d = pcopy(Token.toChars(TOK.float64));
                        flags |= 10;
                        break;
                    case 23:
                        d = pcopy(Token.toChars(TOK.float80));
                        flags |= 10;
                        break;
                    case 24:
                        d = pcopy(Token.toChars(TOK.imaginary32));
                        flags |= 18;
                        break;
                    case 25:
                        d = pcopy(Token.toChars(TOK.imaginary64));
                        flags |= 18;
                        break;
                    case 26:
                        d = pcopy(Token.toChars(TOK.imaginary80));
                        flags |= 18;
                        break;
                    case 27:
                        d = pcopy(Token.toChars(TOK.complex32));
                        flags |= 34;
                        break;
                    case 28:
                        d = pcopy(Token.toChars(TOK.complex64));
                        flags |= 34;
                        break;
                    case 29:
                        d = pcopy(Token.toChars(TOK.complex80));
                        flags |= 34;
                        break;
                    case 30:
                        d = pcopy(new BytePtr("bool"));
                        flags |= 5;
                        break;
                    case 31:
                        d = pcopy(Token.toChars(TOK.char_));
                        flags |= 69;
                        break;
                    case 32:
                        d = pcopy(Token.toChars(TOK.wchar_));
                        flags |= 69;
                        break;
                    case 33:
                        d = pcopy(Token.toChars(TOK.dchar_));
                        flags |= 69;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                this.dstring = pcopy(d);
                this.flags = flags;
                this.merge();
            }

            public  boolean isscalar() {
                return (this.flags & 3) != 0;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeBasic() {}

            public TypeBasic copy() {
                TypeBasic that = new TypeBasic();
                that.dstring = this.dstring;
                that.flags = this.flags;
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeError extends Type
        {
            public  TypeError() {
                super((byte)34);
            }

            public  Type syntaxCopy() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeError copy() {
                TypeError that = new TypeError();
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeNull extends Type
        {
            public  TypeNull() {
                super((byte)40);
            }

            public  Type syntaxCopy() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeNull copy() {
                TypeNull that = new TypeNull();
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeVector extends Type
        {
            public Type basetype;
            public  TypeVector(Type baseType) {
                super((byte)41);
                this.basetype = this.basetype;
            }

            public  Type syntaxCopy() {
                return new TypeVector(this.basetype.syntaxCopy());
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeVector() {}

            public TypeVector copy() {
                TypeVector that = new TypeVector();
                that.basetype = this.basetype;
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeEnum extends Type
        {
            public EnumDeclaration sym;
            public  TypeEnum(EnumDeclaration sym) {
                super((byte)9);
                this.sym = sym;
            }

            public  Type syntaxCopy() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeEnum() {}

            public TypeEnum copy() {
                TypeEnum that = new TypeEnum();
                that.sym = this.sym;
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeTuple extends Type
        {
            public DArray<Parameter> arguments;
            public  TypeTuple(DArray<Parameter> arguments) {
                super((byte)37);
                this.arguments = arguments;
            }

            public  TypeTuple(DArray<Expression> exps, int tag) {
                super((byte)37);
                DArray<Parameter> arguments = new DArray<Parameter>();
                if (exps != null)
                {
                    (arguments).setDim((exps).length);
                    {
                        int i = 0;
                        for (; i < (exps).length;i++){
                            Expression e = (exps).get(i);
                            if ((e.type.ty & 0xFF) == ENUMTY.Ttuple)
                                e.error(new BytePtr("cannot form tuple of tuples"));
                            Parameter arg = new Parameter(0L, e.type, null, null, null);
                            arguments.set(i, arg);
                        }
                    }
                }
                this.arguments = arguments;
            }

            public  Type syntaxCopy() {
                DArray<Parameter> args = Parameter.arraySyntaxCopy(this.arguments);
                Type t = new TypeTuple(args);
                t.mod = this.mod;
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeTuple() {}

            public TypeTuple copy() {
                TypeTuple that = new TypeTuple();
                that.arguments = this.arguments;
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeClass extends Type
        {
            public ClassDeclaration sym;
            public int att = AliasThisRec.fwdref;
            public  TypeClass(ClassDeclaration sym) {
                super((byte)7);
                this.sym = sym;
            }

            public  Type syntaxCopy() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeClass() {}

            public TypeClass copy() {
                TypeClass that = new TypeClass();
                that.sym = this.sym;
                that.att = this.att;
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeStruct extends Type
        {
            public StructDeclaration sym;
            public int att = AliasThisRec.fwdref;
            public  TypeStruct(StructDeclaration sym) {
                super((byte)8);
                this.sym = sym;
            }

            public  Type syntaxCopy() {
                return this;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeStruct() {}

            public TypeStruct copy() {
                TypeStruct that = new TypeStruct();
                that.sym = this.sym;
                that.att = this.att;
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeReference extends TypeNext implements LinkedNode<Type>
        {
            public  TypeReference(Type t) {
                super((byte)4, t);
            }

            public  Type syntaxCopy() {
                Type t = this.next.syntaxCopy();
                if (t.equals(this.next))
                    t = this;
                else
                {
                    t = new TypeReference(t);
                    t.mod = this.mod;
                }
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeReference() {}

            public TypeReference copy() {
                TypeReference that = new TypeReference();
                that.next = this.next;
                return that;
            }
            public void setNext(Type value) { next = value; }
            public Type getNext() { return next; }
        }
        public static abstract class TypeNext extends Type implements LinkedNode<Type>
        {
            public Type next;
            public  TypeNext(byte ty, Type next) {
                super(ty);
                this.next = next;
            }

            public  Type nextOf() {
                return this.next;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeNext() {}

            public abstract TypeNext copy();
            public void setNext(Type value) { next = value; }
            public Type getNext() { return next; }
        }
        public static class TypeSlice extends TypeNext implements LinkedNode<Type>
        {
            public Expression lwr;
            public Expression upr;
            public  TypeSlice(Type next, Expression lwr, Expression upr) {
                super((byte)38, next);
                this.lwr = lwr;
                this.upr = upr;
            }

            public  Type syntaxCopy() {
                Type t = new TypeSlice(this.next.syntaxCopy(), this.lwr.syntaxCopy(), this.upr.syntaxCopy());
                t.mod = this.mod;
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeSlice() {}

            public TypeSlice copy() {
                TypeSlice that = new TypeSlice();
                that.lwr = this.lwr;
                that.upr = this.upr;
                that.next = this.next;
                return that;
            }
            public void setNext(Type value) { next = value; }
            public Type getNext() { return next; }
        }
        public static class TypeDelegate extends TypeNext implements LinkedNode<Type>
        {
            public  TypeDelegate(Type t) {
                super((byte)5, t);
                this.ty = (byte)10;
            }

            public  Type syntaxCopy() {
                Type t = this.next.syntaxCopy();
                if (t.equals(this.next))
                    t = this;
                else
                {
                    t = new TypeDelegate(t);
                    t.mod = this.mod;
                }
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeDelegate() {}

            public TypeDelegate copy() {
                TypeDelegate that = new TypeDelegate();
                that.next = this.next;
                return that;
            }
            public void setNext(Type value) { next = value; }
            public Type getNext() { return next; }
        }
        public static class TypePointer extends TypeNext implements LinkedNode<Type>
        {
            public  TypePointer(Type t) {
                super((byte)3, t);
            }

            public  Type syntaxCopy() {
                Type t = this.next.syntaxCopy();
                if (t.equals(this.next))
                    t = this;
                else
                {
                    t = new TypePointer(t);
                    t.mod = this.mod;
                }
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypePointer() {}

            public TypePointer copy() {
                TypePointer that = new TypePointer();
                that.next = this.next;
                return that;
            }
            public void setNext(Type value) { next = value; }
            public Type getNext() { return next; }
        }
        public static class TypeFunction extends TypeNext implements LinkedNode<Type>
        {
            public ParameterList parameterList = new ParameterList();
            public boolean isnothrow;
            public boolean isnogc;
            public boolean isproperty;
            public boolean isref;
            public boolean isreturn;
            public boolean isscope;
            public int linkage;
            public int trust;
            public int purity = PURE.impure;
            public byte iswild;
            public DArray<Expression> fargs;
            public  TypeFunction(ParameterList pl, Type treturn, int linkage, long stc) {
                super((byte)5, treturn);
                assert(VarArg.none <= pl.varargs && pl.varargs <= VarArg.typesafe);
                this.parameterList = pl.copy();
                this.linkage = linkage;
                if ((stc & 67108864L) != 0)
                    this.purity = PURE.fwdref;
                if ((stc & 33554432L) != 0)
                    this.isnothrow = true;
                if ((stc & 4398046511104L) != 0)
                    this.isnogc = true;
                if ((stc & 4294967296L) != 0)
                    this.isproperty = true;
                if ((stc & 2097152L) != 0)
                    this.isref = true;
                if ((stc & 17592186044416L) != 0)
                    this.isreturn = true;
                if ((stc & 524288L) != 0)
                    this.isscope = true;
                this.trust = TRUST.default_;
                if ((stc & 8589934592L) != 0)
                    this.trust = TRUST.safe;
                if ((stc & 34359738368L) != 0)
                    this.trust = TRUST.system;
                if ((stc & 17179869184L) != 0)
                    this.trust = TRUST.trusted;
            }

            public  Type syntaxCopy() {
                Type treturn = this.next != null ? this.next.syntaxCopy() : null;
                DArray<Parameter> params = Parameter.arraySyntaxCopy(this.parameterList.parameters);
                TypeFunction t = new TypeFunction(new ParameterList(params, this.parameterList.varargs), treturn, this.linkage, 0L);
                t.mod = this.mod;
                t.isnothrow = this.isnothrow;
                t.isnogc = this.isnogc;
                t.purity = this.purity;
                t.isproperty = this.isproperty;
                t.isref = this.isref;
                t.isreturn = this.isreturn;
                t.isscope = this.isscope;
                t.iswild = this.iswild;
                t.trust = this.trust;
                t.fargs = this.fargs;
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeFunction() {}

            public TypeFunction copy() {
                TypeFunction that = new TypeFunction();
                that.parameterList = this.parameterList;
                that.isnothrow = this.isnothrow;
                that.isnogc = this.isnogc;
                that.isproperty = this.isproperty;
                that.isref = this.isref;
                that.isreturn = this.isreturn;
                that.isscope = this.isscope;
                that.linkage = this.linkage;
                that.trust = this.trust;
                that.purity = this.purity;
                that.iswild = this.iswild;
                that.fargs = this.fargs;
                that.next = this.next;
                return that;
            }
            public void setNext(Type value) { next = value; }
            public Type getNext() { return next; }
        }
        public static class TypeArray extends TypeNext implements LinkedNode<Type>
        {
            public  TypeArray(byte ty, Type next) {
                super(ty, next);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeArray() {}

            public TypeArray copy() {
                TypeArray that = new TypeArray();
                that.next = this.next;
                return that;
            }
            public void setNext(Type value) { next = value; }
            public Type getNext() { return next; }
        }
        public static class TypeDArray extends TypeArray
        {
            public  TypeDArray(Type t) {
                super((byte)0, t);
            }

            public  Type syntaxCopy() {
                Type t = this.next.syntaxCopy();
                if (t.equals(this.next))
                    t = this;
                else
                {
                    t = new TypeDArray(t);
                    t.mod = this.mod;
                }
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeDArray() {}

            public TypeDArray copy() {
                TypeDArray that = new TypeDArray();
                return that;
            }
        }
        public static class TypeAArray extends TypeArray
        {
            public Type index;
            public Loc loc = new Loc();
            public  TypeAArray(Type t, Type index) {
                super((byte)2, t);
                this.index = index;
            }

            public  Type syntaxCopy() {
                Type t = this.next.syntaxCopy();
                Type ti = this.index.syntaxCopy();
                if (t.equals(this.next) && ti.equals(this.index))
                    t = this;
                else
                {
                    t = new TypeAArray(t, ti);
                    t.mod = this.mod;
                }
                return t;
            }

            public  Expression toExpression() {
                Expression e = this.next.toExpression();
                if (e != null)
                {
                    Expression ei = this.index.toExpression();
                    if (ei != null)
                        return new ArrayExp(this.loc, e, ei);
                }
                return null;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeAArray() {}

            public TypeAArray copy() {
                TypeAArray that = new TypeAArray();
                that.index = this.index;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeSArray extends TypeArray
        {
            public Expression dim;
            public  TypeSArray(Type t, Expression dim) {
                super((byte)1, t);
                this.dim = dim;
            }

            public  Type syntaxCopy() {
                Type t = this.next.syntaxCopy();
                Expression e = this.dim.syntaxCopy();
                t = new TypeSArray(t, e);
                t.mod = this.mod;
                return t;
            }

            public  Expression toExpression() {
                Expression e = this.next.toExpression();
                if (e != null)
                    e = new ArrayExp(this.dim.loc, e, this.dim);
                return e;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeSArray() {}

            public TypeSArray copy() {
                TypeSArray that = new TypeSArray();
                that.dim = this.dim;
                return that;
            }
        }
        public static abstract class TypeQualified extends Type
        {
            public DArray<RootObject> idents = new DArray<RootObject>();
            public Loc loc = new Loc();
            public  TypeQualified(byte ty, Loc loc) {
                super(ty);
                this.loc = loc.copy();
            }

            public  void addIdent(Identifier id) {
                this.idents.push((RootObject)id);
            }

            public  void addInst(TemplateInstance ti) {
                this.idents.push((RootObject)ti);
            }

            public  void addIndex(RootObject e) {
                this.idents.push(e);
            }

            public  void syntaxCopyHelper(TypeQualified t) {
                this.idents.setDim(t.idents.length);
                {
                    int i = 0;
                    for (; i < this.idents.length;i++){
                        RootObject id = t.idents.get(i);
                        if (id.dyncast() == DYNCAST.dsymbol)
                        {
                            TemplateInstance ti = (TemplateInstance)id;
                            ti = (TemplateInstance)ti.syntaxCopy(null);
                            id = ti;
                        }
                        else if (id.dyncast() == DYNCAST.expression)
                        {
                            Expression e = (Expression)id;
                            e = e.syntaxCopy();
                            id = e;
                        }
                        else if (id.dyncast() == DYNCAST.type)
                        {
                            Type tx = (Type)id;
                            tx = tx.syntaxCopy();
                            id = tx;
                        }
                        this.idents.set(i, id);
                    }
                }
            }

            public  Expression toExpressionHelper(Expression e, int i) {
                for (; i < this.idents.length;i++){
                    RootObject id = this.idents.get(i);
                    switch (id.dyncast())
                    {
                        case DYNCAST.identifier:
                            e = new DotIdExp(e.loc, e, (Identifier)id);
                            break;
                        case DYNCAST.dsymbol:
                            TemplateInstance ti = ((Dsymbol)id).isTemplateInstance();
                            assert(ti != null);
                            e = new DotTemplateInstanceExp(e.loc, e, ti.name, ti.tiargs);
                            break;
                        case DYNCAST.type:
                            e = new ArrayExp(this.loc, e, new TypeExp(this.loc, (Type)id));
                            break;
                        case DYNCAST.expression:
                            e = new ArrayExp(this.loc, e, (Expression)id);
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                }
                return e;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeQualified() {}

            public abstract TypeQualified copy();
        }
        public static class TypeTraits extends Type
        {
            public TraitsExp exp;
            public Loc loc = new Loc();
            public boolean inAliasDeclaration;
            public  TypeTraits(Loc loc, TraitsExp exp) {
                super((byte)6);
                this.loc = loc.copy();
                this.exp = exp;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }

            public  Type syntaxCopy() {
                TraitsExp te = (TraitsExp)this.exp.syntaxCopy();
                TypeTraits tt = new TypeTraits(this.loc, te);
                tt.mod = this.mod;
                return tt;
            }


            public TypeTraits() {}

            public TypeTraits copy() {
                TypeTraits that = new TypeTraits();
                that.exp = this.exp;
                that.loc = this.loc;
                that.inAliasDeclaration = this.inAliasDeclaration;
                that.ty = this.ty;
                that.mod = this.mod;
                that.deco = this.deco;
                that.cto = this.cto;
                that.ito = this.ito;
                that.sto = this.sto;
                that.scto = this.scto;
                that.wto = this.wto;
                that.wcto = this.wcto;
                that.swto = this.swto;
                that.swcto = this.swcto;
                that.pto = this.pto;
                that.rto = this.rto;
                that.arrayof = this.arrayof;
                return that;
            }
        }
        public static class TypeIdentifier extends TypeQualified
        {
            public Identifier ident;
            public  TypeIdentifier(Loc loc, Identifier ident) {
                super((byte)6, loc);
                this.ident = ident;
            }

            public  Type syntaxCopy() {
                TypeIdentifier t = new TypeIdentifier(this.loc, this.ident);
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            public  Expression toExpression() {
                return this.toExpressionHelper(new IdentifierExp(this.loc, this.ident), 0);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeIdentifier() {}

            public TypeIdentifier copy() {
                TypeIdentifier that = new TypeIdentifier();
                that.ident = this.ident;
                that.idents = this.idents;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeReturn extends TypeQualified
        {
            public  TypeReturn(Loc loc) {
                super((byte)39, loc);
            }

            public  Type syntaxCopy() {
                TypeReturn t = new TypeReturn(this.loc);
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeReturn() {}

            public TypeReturn copy() {
                TypeReturn that = new TypeReturn();
                that.idents = this.idents;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeTypeof extends TypeQualified
        {
            public Expression exp;
            public  TypeTypeof(Loc loc, Expression exp) {
                super((byte)36, loc);
                this.exp = exp;
            }

            public  Type syntaxCopy() {
                TypeTypeof t = new TypeTypeof(this.loc, this.exp.syntaxCopy());
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeTypeof() {}

            public TypeTypeof copy() {
                TypeTypeof that = new TypeTypeof();
                that.exp = this.exp;
                that.idents = this.idents;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeInstance extends TypeQualified
        {
            public TemplateInstance tempinst;
            public  TypeInstance(Loc loc, TemplateInstance tempinst) {
                super((byte)35, loc);
                this.tempinst = tempinst;
            }

            public  Type syntaxCopy() {
                TypeInstance t = new TypeInstance(this.loc, (TemplateInstance)this.tempinst.syntaxCopy(null));
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            public  Expression toExpression() {
                return this.toExpressionHelper(new ScopeExp(this.loc, this.tempinst), 0);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeInstance() {}

            public TypeInstance copy() {
                TypeInstance that = new TypeInstance();
                that.tempinst = this.tempinst;
                that.idents = this.idents;
                that.loc = this.loc;
                return that;
            }
        }
        public static abstract class Expression extends ASTNode
        {
            public byte op;
            public byte size;
            public byte parens;
            public Type type;
            public Loc loc = new Loc();
            public  Expression(Loc loc, byte op, int size) {
                super();
                this.loc = loc.copy();
                this.op = op;
                this.size = (byte)size;
            }

            public  Expression syntaxCopy() {
                return this.copy();
            }

            public  void error(BytePtr format, Object... ap) {
                if (!this.type.equals(Type.terror))
                {
                    verror(this.loc, format, new Slice<>(ap), null, null, new BytePtr("Error: "));
                }
            }

            public  int dyncast() {
                return DYNCAST.expression;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Expression() {}

            public abstract Expression copy();
        }
        public static class DeclarationExp extends Expression
        {
            public Dsymbol declaration;
            public  DeclarationExp(Loc loc, Dsymbol declaration) {
                super(loc, TOK.declaration, 28);
                this.declaration = declaration;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeclarationExp() {}

            public DeclarationExp copy() {
                DeclarationExp that = new DeclarationExp();
                that.declaration = this.declaration;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IntegerExp extends Expression
        {
            public long value;
            public  IntegerExp(Loc loc, long value, Type type) {
                super(loc, TOK.int64, 32);
                assert(type != null);
                if (!(type.isscalar()))
                {
                    if ((type.ty & 0xFF) != ENUMTY.Terror)
                        this.error(new BytePtr("integral constant must be scalar type, not %s"), type.toChars());
                    type = Type.terror;
                }
                this.type = type;
                this.setInteger(value);
            }

            public  void setInteger(long value) {
                this.value = value;
                this.normalize();
            }

            public  void normalize() {
                {
                    int __dispatch5 = 0;
                    dispatched_5:
                    do {
                        switch (__dispatch5 != 0 ? __dispatch5 : (this.type.toBasetype().ty & 0xFF))
                        {
                            case 30:
                                this.value = ((this.value != 0L) ? 1 : 0);
                                break;
                            case 13:
                                this.value = (long)(byte)this.value;
                                break;
                            case 31:
                            case 14:
                                this.value = (long)(byte)this.value;
                                break;
                            case 15:
                                this.value = (long)(short)this.value;
                                break;
                            case 32:
                            case 16:
                                this.value = (long)(short)this.value;
                                break;
                            case 17:
                                this.value = (long)(int)this.value;
                                break;
                            case 33:
                            case 18:
                                this.value = (long)(int)this.value;
                                break;
                            case 19:
                                this.value = (long)(long)this.value;
                                break;
                            case 20:
                                this.value = this.value;
                                break;
                            case 3:
                                if (Target.ptrsize == 8)
                                    /*goto case*/{ __dispatch5 = 20; continue dispatched_5; }
                                if (Target.ptrsize == 4)
                                    /*goto case*/{ __dispatch5 = 18; continue dispatched_5; }
                                if (Target.ptrsize == 2)
                                    /*goto case*/{ __dispatch5 = 16; continue dispatched_5; }
                                throw new AssertionError("Unreachable code!");
                            default:
                            break;
                        }
                    } while(__dispatch5 != 0);
                }
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IntegerExp() {}

            public IntegerExp copy() {
                IntegerExp that = new IntegerExp();
                that.value = this.value;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NewAnonClassExp extends Expression
        {
            public Expression thisexp;
            public DArray<Expression> newargs;
            public ClassDeclaration cd;
            public DArray<Expression> arguments;
            public  NewAnonClassExp(Loc loc, Expression thisexp, DArray<Expression> newargs, ClassDeclaration cd, DArray<Expression> arguments) {
                super(loc, TOK.newAnonymousClass, 40);
                this.thisexp = thisexp;
                this.newargs = newargs;
                this.cd = cd;
                this.arguments = arguments;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NewAnonClassExp() {}

            public NewAnonClassExp copy() {
                NewAnonClassExp that = new NewAnonClassExp();
                that.thisexp = this.thisexp;
                that.newargs = this.newargs;
                that.cd = this.cd;
                that.arguments = this.arguments;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IsExp extends Expression
        {
            public Type targ;
            public Identifier id;
            public Type tspec;
            public DArray<TemplateParameter> parameters;
            public byte tok;
            public byte tok2;
            public  IsExp(Loc loc, Type targ, Identifier id, byte tok, Type tspec, byte tok2, DArray<TemplateParameter> parameters) {
                super(loc, TOK.is_, 42);
                this.targ = targ;
                this.id = id;
                this.tok = tok;
                this.tspec = tspec;
                this.tok2 = tok2;
                this.parameters = parameters;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IsExp() {}

            public IsExp copy() {
                IsExp that = new IsExp();
                that.targ = this.targ;
                that.id = this.id;
                that.tspec = this.tspec;
                that.parameters = this.parameters;
                that.tok = this.tok;
                that.tok2 = this.tok2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class RealExp extends Expression
        {
            public double value;
            public  RealExp(Loc loc, double value, Type type) {
                super(loc, TOK.float64, 40);
                this.value = value;
                this.type = type;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public RealExp() {}

            public RealExp copy() {
                RealExp that = new RealExp();
                that.value = this.value;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NullExp extends Expression
        {
            public  NullExp(Loc loc, Type type) {
                super(loc, TOK.null_, 24);
                this.type = type;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NullExp() {}

            public NullExp copy() {
                NullExp that = new NullExp();
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeidExp extends Expression
        {
            public RootObject obj;
            public  TypeidExp(Loc loc, RootObject o) {
                super(loc, TOK.typeid_, 28);
                this.obj = o;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeidExp() {}

            public TypeidExp copy() {
                TypeidExp that = new TypeidExp();
                that.obj = this.obj;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TraitsExp extends Expression
        {
            public Identifier ident;
            public DArray<RootObject> args;
            public  TraitsExp(Loc loc, Identifier ident, DArray<RootObject> args) {
                super(loc, TOK.traits, 32);
                this.ident = ident;
                this.args = args;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TraitsExp() {}

            public TraitsExp copy() {
                TraitsExp that = new TraitsExp();
                that.ident = this.ident;
                that.args = this.args;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class StringExp extends Expression
        {
            public BytePtr string;
            public CharPtr wstring;
            public IntPtr dstring;
            public int len;
            public byte sz = (byte)1;
            public byte postfix = (byte)0;
            public  StringExp(Loc loc, BytePtr string) {
                super(loc, TOK.string_, 34);
                this.string = pcopy(string);
                this.len = strlen(string);
                this.sz = (byte)1;
            }

            public  StringExp(Loc loc, Object string, int len) {
                super(loc, TOK.string_, 34);
                this.string = pcopy((toBytePtr(string)));
                this.len = len;
                this.sz = (byte)1;
            }

            public  StringExp(Loc loc, Object string, int len, byte postfix) {
                super(loc, TOK.string_, 34);
                this.string = pcopy((toBytePtr(string)));
                this.len = len;
                this.postfix = postfix;
                this.sz = (byte)1;
            }

            public  void writeTo(Object dest, boolean zero, int tyto) {
                int encSize = 0;
                switch (tyto)
                {
                    case 0:
                        encSize = (this.sz & 0xFF);
                        break;
                    case 31:
                        encSize = 1;
                        break;
                    case 32:
                        encSize = 2;
                        break;
                    case 33:
                        encSize = 4;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                if ((this.sz & 0xFF) == encSize)
                {
                    memcpy((BytePtr)dest, (this.string), (this.len * (this.sz & 0xFF)));
                    if (zero)
                        memset(((BytePtr)dest).plus((this.len * (this.sz & 0xFF))), 0, (this.sz & 0xFF));
                }
                else
                    throw new AssertionError("Unreachable code!");
            }

            public  ByteSlice toStringz() {
                int nbytes = this.len * (this.sz & 0xFF);
                BytePtr s = pcopy(toBytePtr(Mem.xmalloc(nbytes + (this.sz & 0xFF))));
                this.writeTo(s, true, 0);
                return s.slice(0,nbytes);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StringExp() {}

            public StringExp copy() {
                StringExp that = new StringExp();
                that.string = this.string;
                that.wstring = this.wstring;
                that.dstring = this.dstring;
                that.len = this.len;
                that.sz = this.sz;
                that.postfix = this.postfix;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NewExp extends Expression
        {
            public Expression thisexp;
            public DArray<Expression> newargs;
            public Type newtype;
            public DArray<Expression> arguments;
            public  NewExp(Loc loc, Expression thisexp, DArray<Expression> newargs, Type newtype, DArray<Expression> arguments) {
                super(loc, TOK.new_, 40);
                this.thisexp = thisexp;
                this.newargs = newargs;
                this.newtype = newtype;
                this.arguments = arguments;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NewExp() {}

            public NewExp copy() {
                NewExp that = new NewExp();
                that.thisexp = this.thisexp;
                that.newargs = this.newargs;
                that.newtype = this.newtype;
                that.arguments = this.arguments;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AssocArrayLiteralExp extends Expression
        {
            public DArray<Expression> keys;
            public DArray<Expression> values;
            public  AssocArrayLiteralExp(Loc loc, DArray<Expression> keys, DArray<Expression> values) {
                super(loc, TOK.assocArrayLiteral, 32);
                assert((keys).length == (values).length);
                this.keys = keys;
                this.values = values;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AssocArrayLiteralExp() {}

            public AssocArrayLiteralExp copy() {
                AssocArrayLiteralExp that = new AssocArrayLiteralExp();
                that.keys = this.keys;
                that.values = this.values;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ArrayLiteralExp extends Expression
        {
            public Expression basis;
            public DArray<Expression> elements;
            public  ArrayLiteralExp(Loc loc, DArray<Expression> elements) {
                super(loc, TOK.arrayLiteral, 32);
                this.elements = elements;
            }

            public  ArrayLiteralExp(Loc loc, Expression e) {
                super(loc, TOK.arrayLiteral, 32);
                this.elements = new DArray<Expression>();
                (this.elements).push(e);
            }

            public  ArrayLiteralExp(Loc loc, Expression basis, DArray<Expression> elements) {
                super(loc, TOK.arrayLiteral, 32);
                this.basis = basis;
                this.elements = elements;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ArrayLiteralExp() {}

            public ArrayLiteralExp copy() {
                ArrayLiteralExp that = new ArrayLiteralExp();
                that.basis = this.basis;
                that.elements = this.elements;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class FuncExp extends Expression
        {
            public FuncLiteralDeclaration fd;
            public TemplateDeclaration td;
            public byte tok;
            public  FuncExp(Loc loc, Dsymbol s) {
                super(loc, TOK.function_, 33);
                this.td = s.isTemplateDeclaration();
                this.fd = s.isFuncLiteralDeclaration();
                if (this.td != null)
                {
                    assert(this.td.literal);
                    assert(this.td.members != null && (this.td.members).length == 1);
                    this.fd = (this.td.members).get(0).isFuncLiteralDeclaration();
                }
                this.tok = this.fd.tok;
                assert(this.fd.fbody != null);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncExp() {}

            public FuncExp copy() {
                FuncExp that = new FuncExp();
                that.fd = this.fd;
                that.td = this.td;
                that.tok = this.tok;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IntervalExp extends Expression
        {
            public Expression lwr;
            public Expression upr;
            public  IntervalExp(Loc loc, Expression lwr, Expression upr) {
                super(loc, TOK.interval, 32);
                this.lwr = lwr;
                this.upr = upr;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IntervalExp() {}

            public IntervalExp copy() {
                IntervalExp that = new IntervalExp();
                that.lwr = this.lwr;
                that.upr = this.upr;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeExp extends Expression
        {
            public  TypeExp(Loc loc, Type type) {
                super(loc, TOK.type, 24);
                this.type = type;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeExp() {}

            public TypeExp copy() {
                TypeExp that = new TypeExp();
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ScopeExp extends Expression
        {
            public ScopeDsymbol sds;
            public  ScopeExp(Loc loc, ScopeDsymbol sds) {
                super(loc, TOK.scope_, 28);
                this.sds = sds;
                assert(!(sds.isTemplateDeclaration() != null));
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ScopeExp() {}

            public ScopeExp copy() {
                ScopeExp that = new ScopeExp();
                that.sds = this.sds;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IdentifierExp extends Expression
        {
            public Identifier ident;
            public  IdentifierExp(Loc loc, Identifier ident) {
                super(loc, TOK.identifier, 28);
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IdentifierExp() {}

            public IdentifierExp copy() {
                IdentifierExp that = new IdentifierExp();
                that.ident = this.ident;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class UnaExp extends Expression
        {
            public Expression e1;
            public  UnaExp(Loc loc, byte op, int size, Expression e1) {
                super(loc, op, size);
                this.e1 = e1;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UnaExp() {}

            public UnaExp copy() {
                UnaExp that = new UnaExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DefaultInitExp extends Expression
        {
            public byte subop;
            public  DefaultInitExp(Loc loc, byte subop, int size) {
                super(loc, TOK.default_, size);
                this.subop = subop;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DefaultInitExp() {}

            public DefaultInitExp copy() {
                DefaultInitExp that = new DefaultInitExp();
                that.subop = this.subop;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static abstract class BinExp extends Expression
        {
            public Expression e1;
            public Expression e2;
            public  BinExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
                super(loc, op, size);
                this.e1 = e1;
                this.e2 = e2;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public BinExp() {}

            public abstract BinExp copy();
        }
        public static class DsymbolExp extends Expression
        {
            public Dsymbol s;
            public boolean hasOverloads;
            public  DsymbolExp(Loc loc, Dsymbol s, boolean hasOverloads) {
                super(loc, TOK.dSymbol, 29);
                this.s = s;
                this.hasOverloads = hasOverloads;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DsymbolExp() {}

            public DsymbolExp copy() {
                DsymbolExp that = new DsymbolExp();
                that.s = this.s;
                that.hasOverloads = this.hasOverloads;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TemplateExp extends Expression
        {
            public TemplateDeclaration td;
            public FuncDeclaration fd;
            public  TemplateExp(Loc loc, TemplateDeclaration td, FuncDeclaration fd) {
                super(loc, TOK.template_, 32);
                this.td = td;
                this.fd = fd;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateExp() {}

            public TemplateExp copy() {
                TemplateExp that = new TemplateExp();
                that.td = this.td;
                that.fd = this.fd;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class SymbolExp extends Expression
        {
            public Declaration var;
            public boolean hasOverloads;
            public  SymbolExp(Loc loc, byte op, int size, Declaration var, boolean hasOverloads) {
                super(loc, op, size);
                assert(var != null);
                this.var = var;
                this.hasOverloads = hasOverloads;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SymbolExp() {}

            public SymbolExp copy() {
                SymbolExp that = new SymbolExp();
                that.var = this.var;
                that.hasOverloads = this.hasOverloads;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class VarExp extends SymbolExp
        {
            public  VarExp(Loc loc, Declaration var, boolean hasOverloads) {
                super(loc, TOK.variable, 29, var, var.isVarDeclaration() == null && hasOverloads);
                this.type = var.type;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VarExp() {}

            public VarExp copy() {
                VarExp that = new VarExp();
                that.var = this.var;
                that.hasOverloads = this.hasOverloads;
                return that;
            }
        }
        public static class TupleExp extends Expression
        {
            public Expression e0;
            public DArray<Expression> exps;
            public  TupleExp(Loc loc, Expression e0, DArray<Expression> exps) {
                super(loc, TOK.tuple, 32);
                this.e0 = e0;
                this.exps = exps;
            }

            public  TupleExp(Loc loc, DArray<Expression> exps) {
                super(loc, TOK.tuple, 32);
                this.exps = exps;
            }

            public  TupleExp(Loc loc, TupleDeclaration tup) {
                super(loc, TOK.tuple, 32);
                this.exps = new DArray<Expression>();
                (this.exps).reserve((tup.objects).length);
                {
                    int i = 0;
                    for (; i < (tup.objects).length;i++){
                        RootObject o = (tup.objects).get(i);
                        {
                            Dsymbol s = this.getDsymbol(o);
                            if (s != null)
                            {
                                Expression e = new DsymbolExp(loc, s, true);
                                (this.exps).push(e);
                            }
                            else if (o.dyncast() == DYNCAST.expression)
                            {
                                Expression e = ((Expression)o).copy();
                                e.loc = loc.copy();
                                (this.exps).push(e);
                            }
                            else if (o.dyncast() == DYNCAST.type)
                            {
                                Type t = (Type)o;
                                Expression e = new TypeExp(loc, t);
                                (this.exps).push(e);
                            }
                            else
                            {
                                this.error(new BytePtr("%s is not an expression"), o.toChars());
                            }
                        }
                    }
                }
            }

            public  Dsymbol isDsymbol(RootObject o) {
                if (!(o != null) || (o.dyncast()) != 0 || (DYNCAST.dsymbol) != 0)
                    return null;
                return (Dsymbol)o;
            }

            public  Dsymbol getDsymbol(RootObject oarg) {
                Dsymbol sa = null;
                Expression ea = isExpression(oarg);
                if (ea != null)
                {
                    if ((ea.op & 0xFF) == 26)
                        sa = ((VarExp)ea).var;
                    else if ((ea.op & 0xFF) == 161)
                    {
                        if (((FuncExp)ea).td != null)
                            sa = ((FuncExp)ea).td;
                        else
                            sa = ((FuncExp)ea).fd;
                    }
                    else if ((ea.op & 0xFF) == 36)
                        sa = ((TemplateExp)ea).td;
                    else
                        sa = null;
                }
                else
                {
                    Type ta = isType(oarg);
                    if (ta != null)
                        sa = ta.toDsymbol(null);
                    else
                        sa = this.isDsymbol(oarg);
                }
                return sa;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TupleExp() {}

            public TupleExp copy() {
                TupleExp that = new TupleExp();
                that.e0 = this.e0;
                that.exps = this.exps;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DollarExp extends IdentifierExp
        {
            public  DollarExp(Loc loc) {
                super(loc, Id.dollar);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DollarExp() {}

            public DollarExp copy() {
                DollarExp that = new DollarExp();
                that.ident = this.ident;
                return that;
            }
        }
        public static class ThisExp extends Expression
        {
            public  ThisExp(Loc loc) {
                super(loc, TOK.this_, 24);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ThisExp() {}

            public ThisExp copy() {
                ThisExp that = new ThisExp();
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class SuperExp extends ThisExp
        {
            public  SuperExp(Loc loc) {
                super(loc);
                this.op = TOK.super_;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SuperExp() {}

            public SuperExp copy() {
                SuperExp that = new SuperExp();
                return that;
            }
        }
        public static class AddrExp extends UnaExp
        {
            public  AddrExp(Loc loc, Expression e) {
                super(loc, TOK.address, 28, e);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AddrExp() {}

            public AddrExp copy() {
                AddrExp that = new AddrExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class PreExp extends UnaExp
        {
            public  PreExp(byte op, Loc loc, Expression e) {
                super(loc, op, 28, e);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PreExp() {}

            public PreExp copy() {
                PreExp that = new PreExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class PtrExp extends UnaExp
        {
            public  PtrExp(Loc loc, Expression e) {
                super(loc, TOK.star, 28, e);
            }

            public  PtrExp(Loc loc, Expression e, Type t) {
                super(loc, TOK.star, 28, e);
                this.type = t;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PtrExp() {}

            public PtrExp copy() {
                PtrExp that = new PtrExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class NegExp extends UnaExp
        {
            public  NegExp(Loc loc, Expression e) {
                super(loc, TOK.negate, 28, e);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NegExp() {}

            public NegExp copy() {
                NegExp that = new NegExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class UAddExp extends UnaExp
        {
            public  UAddExp(Loc loc, Expression e) {
                super(loc, TOK.uadd, 28, e);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UAddExp() {}

            public UAddExp copy() {
                UAddExp that = new UAddExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class NotExp extends UnaExp
        {
            public  NotExp(Loc loc, Expression e) {
                super(loc, TOK.not, 28, e);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NotExp() {}

            public NotExp copy() {
                NotExp that = new NotExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class ComExp extends UnaExp
        {
            public  ComExp(Loc loc, Expression e) {
                super(loc, TOK.tilde, 28, e);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ComExp() {}

            public ComExp copy() {
                ComExp that = new ComExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class DeleteExp extends UnaExp
        {
            public boolean isRAII;
            public  DeleteExp(Loc loc, Expression e, boolean isRAII) {
                super(loc, TOK.delete_, 29, e);
                this.isRAII = isRAII;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeleteExp() {}

            public DeleteExp copy() {
                DeleteExp that = new DeleteExp();
                that.isRAII = this.isRAII;
                that.e1 = this.e1;
                return that;
            }
        }
        public static class CastExp extends UnaExp
        {
            public Type to;
            public byte mod = (byte)255;
            public  CastExp(Loc loc, Expression e, Type t) {
                super(loc, TOK.cast_, 33, e);
                this.to = t;
            }

            public  CastExp(Loc loc, Expression e, byte mod) {
                super(loc, TOK.cast_, 33, e);
                this.mod = mod;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CastExp() {}

            public CastExp copy() {
                CastExp that = new CastExp();
                that.to = this.to;
                that.mod = this.mod;
                that.e1 = this.e1;
                return that;
            }
        }
        public static class CallExp extends UnaExp
        {
            public DArray<Expression> arguments;
            public  CallExp(Loc loc, Expression e, DArray<Expression> exps) {
                super(loc, TOK.call, 32, e);
                this.arguments = exps;
            }

            public  CallExp(Loc loc, Expression e) {
                super(loc, TOK.call, 32, e);
            }

            public  CallExp(Loc loc, Expression e, Expression earg1) {
                super(loc, TOK.call, 32, e);
                DArray<Expression> arguments = new DArray<Expression>();
                if (earg1 != null)
                {
                    (arguments).setDim(1);
                    arguments.set(0, earg1);
                }
                this.arguments = arguments;
            }

            public  CallExp(Loc loc, Expression e, Expression earg1, Expression earg2) {
                super(loc, TOK.call, 32, e);
                DArray<Expression> arguments = new DArray<Expression>();
                (arguments).setDim(2);
                arguments.set(0, earg1);
                arguments.set(1, earg2);
                this.arguments = arguments;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CallExp() {}

            public CallExp copy() {
                CallExp that = new CallExp();
                that.arguments = this.arguments;
                that.e1 = this.e1;
                return that;
            }
        }
        public static class DotIdExp extends UnaExp
        {
            public Identifier ident;
            public  DotIdExp(Loc loc, Expression e, Identifier ident) {
                super(loc, TOK.dotIdentifier, 32, e);
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DotIdExp() {}

            public DotIdExp copy() {
                DotIdExp that = new DotIdExp();
                that.ident = this.ident;
                that.e1 = this.e1;
                return that;
            }
        }
        public static class AssertExp extends UnaExp
        {
            public Expression msg;
            public  AssertExp(Loc loc, Expression e, Expression msg) {
                super(loc, TOK.assert_, 32, e);
                this.msg = msg;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AssertExp() {}

            public AssertExp copy() {
                AssertExp that = new AssertExp();
                that.msg = this.msg;
                that.e1 = this.e1;
                return that;
            }
        }
        public static class CompileExp extends Expression
        {
            public DArray<Expression> exps;
            public  CompileExp(Loc loc, DArray<Expression> exps) {
                super(loc, TOK.mixin_, 28);
                this.exps = exps;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompileExp() {}

            public CompileExp copy() {
                CompileExp that = new CompileExp();
                that.exps = this.exps;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ImportExp extends UnaExp
        {
            public  ImportExp(Loc loc, Expression e) {
                super(loc, TOK.import_, 28, e);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ImportExp() {}

            public ImportExp copy() {
                ImportExp that = new ImportExp();
                that.e1 = this.e1;
                return that;
            }
        }
        public static class DotTemplateInstanceExp extends UnaExp
        {
            public TemplateInstance ti;
            public  DotTemplateInstanceExp(Loc loc, Expression e, Identifier name, DArray<RootObject> tiargs) {
                super(loc, TOK.dotTemplateInstance, 32, e);
                this.ti = new TemplateInstance(loc, name, tiargs);
            }

            public  DotTemplateInstanceExp(Loc loc, Expression e, TemplateInstance ti) {
                super(loc, TOK.dotTemplateInstance, 32, e);
                this.ti = ti;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DotTemplateInstanceExp() {}

            public DotTemplateInstanceExp copy() {
                DotTemplateInstanceExp that = new DotTemplateInstanceExp();
                that.ti = this.ti;
                that.e1 = this.e1;
                return that;
            }
        }
        public static class ArrayExp extends UnaExp
        {
            public DArray<Expression> arguments;
            public  ArrayExp(Loc loc, Expression e1, Expression index) {
                super(loc, TOK.array, 32, e1);
                this.arguments = new DArray<Expression>();
                if (index != null)
                    (this.arguments).push(index);
            }

            public  ArrayExp(Loc loc, Expression e1, DArray<Expression> args) {
                super(loc, TOK.array, 32, e1);
                this.arguments = args;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ArrayExp() {}

            public ArrayExp copy() {
                ArrayExp that = new ArrayExp();
                that.arguments = this.arguments;
                that.e1 = this.e1;
                return that;
            }
        }
        public static class FuncInitExp extends DefaultInitExp
        {
            public  FuncInitExp(Loc loc) {
                super(loc, TOK.functionString, 25);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncInitExp() {}

            public FuncInitExp copy() {
                FuncInitExp that = new FuncInitExp();
                that.subop = this.subop;
                return that;
            }
        }
        public static class PrettyFuncInitExp extends DefaultInitExp
        {
            public  PrettyFuncInitExp(Loc loc) {
                super(loc, TOK.prettyFunction, 25);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PrettyFuncInitExp() {}

            public PrettyFuncInitExp copy() {
                PrettyFuncInitExp that = new PrettyFuncInitExp();
                that.subop = this.subop;
                return that;
            }
        }
        public static class FileInitExp extends DefaultInitExp
        {
            public  FileInitExp(Loc loc, byte tok) {
                super(loc, tok, 25);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FileInitExp() {}

            public FileInitExp copy() {
                FileInitExp that = new FileInitExp();
                that.subop = this.subop;
                return that;
            }
        }
        public static class LineInitExp extends DefaultInitExp
        {
            public  LineInitExp(Loc loc) {
                super(loc, TOK.line, 25);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LineInitExp() {}

            public LineInitExp copy() {
                LineInitExp that = new LineInitExp();
                that.subop = this.subop;
                return that;
            }
        }
        public static class ModuleInitExp extends DefaultInitExp
        {
            public  ModuleInitExp(Loc loc) {
                super(loc, TOK.moduleString, 25);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ModuleInitExp() {}

            public ModuleInitExp copy() {
                ModuleInitExp that = new ModuleInitExp();
                that.subop = this.subop;
                return that;
            }
        }
        public static class CommaExp extends BinExp
        {
            public boolean isGenerated;
            public boolean allowCommaExp;
            public  CommaExp(Loc loc, Expression e1, Expression e2, boolean generated) {
                super(loc, TOK.comma, 34, e1, e2);
                this.allowCommaExp = (this.isGenerated = generated);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CommaExp() {}

            public CommaExp copy() {
                CommaExp that = new CommaExp();
                that.isGenerated = this.isGenerated;
                that.allowCommaExp = this.allowCommaExp;
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class PostExp extends BinExp
        {
            public  PostExp(byte op, Loc loc, Expression e) {
                super(loc, op, 32, e, new IntegerExp(loc, 1L, Type.tint32));
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PostExp() {}

            public PostExp copy() {
                PostExp that = new PostExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class PowExp extends BinExp
        {
            public  PowExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.pow, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PowExp() {}

            public PowExp copy() {
                PowExp that = new PowExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class MulExp extends BinExp
        {
            public  MulExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.mul, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MulExp() {}

            public MulExp copy() {
                MulExp that = new MulExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class DivExp extends BinExp
        {
            public  DivExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.div, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DivExp() {}

            public DivExp copy() {
                DivExp that = new DivExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class ModExp extends BinExp
        {
            public  ModExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.mod, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ModExp() {}

            public ModExp copy() {
                ModExp that = new ModExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class AddExp extends BinExp
        {
            public  AddExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.add, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AddExp() {}

            public AddExp copy() {
                AddExp that = new AddExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class MinExp extends BinExp
        {
            public  MinExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.min, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MinExp() {}

            public MinExp copy() {
                MinExp that = new MinExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class CatExp extends BinExp
        {
            public  CatExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.concatenate, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CatExp() {}

            public CatExp copy() {
                CatExp that = new CatExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class ShlExp extends BinExp
        {
            public  ShlExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.leftShift, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShlExp() {}

            public ShlExp copy() {
                ShlExp that = new ShlExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class ShrExp extends BinExp
        {
            public  ShrExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.rightShift, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShrExp() {}

            public ShrExp copy() {
                ShrExp that = new ShrExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class UshrExp extends BinExp
        {
            public  UshrExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.unsignedRightShift, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UshrExp() {}

            public UshrExp copy() {
                UshrExp that = new UshrExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class EqualExp extends BinExp
        {
            public  EqualExp(byte op, Loc loc, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
                assert((op & 0xFF) == 58 || (op & 0xFF) == 59);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public EqualExp() {}

            public EqualExp copy() {
                EqualExp that = new EqualExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class InExp extends BinExp
        {
            public  InExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.in_, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InExp() {}

            public InExp copy() {
                InExp that = new InExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class IdentityExp extends BinExp
        {
            public  IdentityExp(byte op, Loc loc, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IdentityExp() {}

            public IdentityExp copy() {
                IdentityExp that = new IdentityExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class CmpExp extends BinExp
        {
            public  CmpExp(byte op, Loc loc, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CmpExp() {}

            public CmpExp copy() {
                CmpExp that = new CmpExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class AndExp extends BinExp
        {
            public  AndExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.and, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AndExp() {}

            public AndExp copy() {
                AndExp that = new AndExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class XorExp extends BinExp
        {
            public  XorExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.xor, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public XorExp() {}

            public XorExp copy() {
                XorExp that = new XorExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class OrExp extends BinExp
        {
            public  OrExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.or, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public OrExp() {}

            public OrExp copy() {
                OrExp that = new OrExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class LogicalExp extends BinExp
        {
            public  LogicalExp(Loc loc, byte op, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LogicalExp() {}

            public LogicalExp copy() {
                LogicalExp that = new LogicalExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class CondExp extends BinExp
        {
            public Expression econd;
            public  CondExp(Loc loc, Expression econd, Expression e1, Expression e2) {
                super(loc, TOK.question, 36, e1, e2);
                this.econd = econd;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CondExp() {}

            public CondExp copy() {
                CondExp that = new CondExp();
                that.econd = this.econd;
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class AssignExp extends BinExp
        {
            public  AssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.assign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AssignExp() {}

            public AssignExp copy() {
                AssignExp that = new AssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class BinAssignExp extends BinExp
        {
            public  BinAssignExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
                super(loc, op, size, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public BinAssignExp() {}

            public BinAssignExp copy() {
                BinAssignExp that = new BinAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                return that;
            }
        }
        public static class AddAssignExp extends BinAssignExp
        {
            public  AddAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.addAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AddAssignExp() {}

            public AddAssignExp copy() {
                AddAssignExp that = new AddAssignExp();
                return that;
            }
        }
        public static class MinAssignExp extends BinAssignExp
        {
            public  MinAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.minAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MinAssignExp() {}

            public MinAssignExp copy() {
                MinAssignExp that = new MinAssignExp();
                return that;
            }
        }
        public static class MulAssignExp extends BinAssignExp
        {
            public  MulAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.mulAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MulAssignExp() {}

            public MulAssignExp copy() {
                MulAssignExp that = new MulAssignExp();
                return that;
            }
        }
        public static class DivAssignExp extends BinAssignExp
        {
            public  DivAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.divAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DivAssignExp() {}

            public DivAssignExp copy() {
                DivAssignExp that = new DivAssignExp();
                return that;
            }
        }
        public static class ModAssignExp extends BinAssignExp
        {
            public  ModAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.modAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ModAssignExp() {}

            public ModAssignExp copy() {
                ModAssignExp that = new ModAssignExp();
                return that;
            }
        }
        public static class PowAssignExp extends BinAssignExp
        {
            public  PowAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.powAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PowAssignExp() {}

            public PowAssignExp copy() {
                PowAssignExp that = new PowAssignExp();
                return that;
            }
        }
        public static class AndAssignExp extends BinAssignExp
        {
            public  AndAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.andAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AndAssignExp() {}

            public AndAssignExp copy() {
                AndAssignExp that = new AndAssignExp();
                return that;
            }
        }
        public static class OrAssignExp extends BinAssignExp
        {
            public  OrAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.orAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public OrAssignExp() {}

            public OrAssignExp copy() {
                OrAssignExp that = new OrAssignExp();
                return that;
            }
        }
        public static class XorAssignExp extends BinAssignExp
        {
            public  XorAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.xorAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public XorAssignExp() {}

            public XorAssignExp copy() {
                XorAssignExp that = new XorAssignExp();
                return that;
            }
        }
        public static class ShlAssignExp extends BinAssignExp
        {
            public  ShlAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.leftShiftAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShlAssignExp() {}

            public ShlAssignExp copy() {
                ShlAssignExp that = new ShlAssignExp();
                return that;
            }
        }
        public static class ShrAssignExp extends BinAssignExp
        {
            public  ShrAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.rightShiftAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShrAssignExp() {}

            public ShrAssignExp copy() {
                ShrAssignExp that = new ShrAssignExp();
                return that;
            }
        }
        public static class UshrAssignExp extends BinAssignExp
        {
            public  UshrAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.unsignedRightShiftAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UshrAssignExp() {}

            public UshrAssignExp copy() {
                UshrAssignExp that = new UshrAssignExp();
                return that;
            }
        }
        public static class CatAssignExp extends BinAssignExp
        {
            public  CatAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.concatenateAssign, 32, e1, e2);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CatAssignExp() {}

            public CatAssignExp copy() {
                CatAssignExp that = new CatAssignExp();
                return that;
            }
        }
        public static class TemplateParameter extends ASTNode
        {
            public Loc loc = new Loc();
            public Identifier ident;
            public  TemplateParameter(Loc loc, Identifier ident) {
                super();
                this.loc = loc.copy();
                this.ident = ident;
            }

            public  TemplateParameter syntaxCopy() {
                return null;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateParameter() {}

            public TemplateParameter copy() {
                TemplateParameter that = new TemplateParameter();
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateAliasParameter extends TemplateParameter
        {
            public Type specType;
            public RootObject specAlias;
            public RootObject defaultAlias;
            public  TemplateAliasParameter(Loc loc, Identifier ident, Type specType, RootObject specAlias, RootObject defaultAlias) {
                super(loc, ident);
                this.ident = ident;
                this.specType = specType;
                this.specAlias = specAlias;
                this.defaultAlias = defaultAlias;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateAliasParameter() {}

            public TemplateAliasParameter copy() {
                TemplateAliasParameter that = new TemplateAliasParameter();
                that.specType = this.specType;
                that.specAlias = this.specAlias;
                that.defaultAlias = this.defaultAlias;
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateTypeParameter extends TemplateParameter
        {
            public Type specType;
            public Type defaultType;
            public  TemplateTypeParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
                super(loc, ident);
                this.ident = ident;
                this.specType = specType;
                this.defaultType = defaultType;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateTypeParameter() {}

            public TemplateTypeParameter copy() {
                TemplateTypeParameter that = new TemplateTypeParameter();
                that.specType = this.specType;
                that.defaultType = this.defaultType;
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateTupleParameter extends TemplateParameter
        {
            public  TemplateTupleParameter(Loc loc, Identifier ident) {
                super(loc, ident);
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateTupleParameter() {}

            public TemplateTupleParameter copy() {
                TemplateTupleParameter that = new TemplateTupleParameter();
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateValueParameter extends TemplateParameter
        {
            public Type valType;
            public Expression specValue;
            public Expression defaultValue;
            public  TemplateValueParameter(Loc loc, Identifier ident, Type valType, Expression specValue, Expression defaultValue) {
                super(loc, ident);
                this.ident = ident;
                this.valType = valType;
                this.specValue = specValue;
                this.defaultValue = defaultValue;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateValueParameter() {}

            public TemplateValueParameter copy() {
                TemplateValueParameter that = new TemplateValueParameter();
                that.valType = this.valType;
                that.specValue = this.specValue;
                that.defaultValue = this.defaultValue;
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateThisParameter extends TemplateTypeParameter
        {
            public  TemplateThisParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
                super(loc, ident, specType, defaultType);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateThisParameter() {}

            public TemplateThisParameter copy() {
                TemplateThisParameter that = new TemplateThisParameter();
                that.specType = this.specType;
                that.defaultType = this.defaultType;
                return that;
            }
        }
        public static abstract class Condition extends ASTNode
        {
            public Loc loc = new Loc();
            public  Condition(Loc loc) {
                super();
                this.loc = loc.copy();
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Condition() {}

            public abstract Condition copy();
        }
        public static class StaticForeach extends RootObject
        {
            public Loc loc = new Loc();
            public ForeachStatement aggrfe;
            public ForeachRangeStatement rangefe;
            public  StaticForeach(Loc loc, ForeachStatement aggrfe, ForeachRangeStatement rangefe) {
                super();
                this.loc = loc.copy();
                this.aggrfe = aggrfe;
                this.rangefe = rangefe;
            }


            public StaticForeach() {}

            public StaticForeach copy() {
                StaticForeach that = new StaticForeach();
                that.loc = this.loc;
                that.aggrfe = this.aggrfe;
                that.rangefe = this.rangefe;
                return that;
            }
        }
        public static class StaticIfCondition extends Condition
        {
            public Expression exp;
            public  StaticIfCondition(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticIfCondition() {}

            public StaticIfCondition copy() {
                StaticIfCondition that = new StaticIfCondition();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DVCondition extends Condition
        {
            public int level;
            public Identifier ident;
            public Module mod;
            public  DVCondition(Module mod, int level, Identifier ident) {
                super(Loc.initial);
                this.mod = mod;
                this.ident = ident;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DVCondition() {}

            public DVCondition copy() {
                DVCondition that = new DVCondition();
                that.level = this.level;
                that.ident = this.ident;
                that.mod = this.mod;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DebugCondition extends DVCondition
        {
            public  DebugCondition(Module mod, int level, Identifier ident) {
                super(mod, level, ident);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DebugCondition() {}

            public DebugCondition copy() {
                DebugCondition that = new DebugCondition();
                that.level = this.level;
                that.ident = this.ident;
                that.mod = this.mod;
                return that;
            }
        }
        public static class VersionCondition extends DVCondition
        {
            public  VersionCondition(Module mod, int level, Identifier ident) {
                super(mod, level, ident);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VersionCondition() {}

            public VersionCondition copy() {
                VersionCondition that = new VersionCondition();
                that.level = this.level;
                that.ident = this.ident;
                that.mod = this.mod;
                return that;
            }
        }

        public static class InitKind 
        {
            public static final byte void_ = (byte)0;
            public static final byte error = (byte)1;
            public static final byte struct_ = (byte)2;
            public static final byte array = (byte)3;
            public static final byte exp = (byte)4;
        }

        public static class Initializer extends ASTNode
        {
            public Loc loc = new Loc();
            public byte kind;
            public  Initializer(Loc loc, byte kind) {
                super();
                this.loc = loc.copy();
                this.kind = kind;
            }

            public  Expression toExpression(Type t) {
                return null;
            }

            public  ExpInitializer isExpInitializer() {
                return (this.kind & 0xFF) == 4 ? (ExpInitializer)this : null;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Initializer() {}

            public Initializer copy() {
                Initializer that = new Initializer();
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class ExpInitializer extends Initializer
        {
            public Expression exp;
            public  ExpInitializer(Loc loc, Expression exp) {
                super(loc, InitKind.exp);
                this.exp = exp;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ExpInitializer() {}

            public ExpInitializer copy() {
                ExpInitializer that = new ExpInitializer();
                that.exp = this.exp;
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class StructInitializer extends Initializer
        {
            public DArray<Identifier> field = new DArray<Identifier>();
            public DArray<Initializer> value = new DArray<Initializer>();
            public  StructInitializer(Loc loc) {
                super(loc, InitKind.struct_);
            }

            public  void addInit(Identifier field, Initializer value) {
                this.field.push(field);
                this.value.push(value);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StructInitializer() {}

            public StructInitializer copy() {
                StructInitializer that = new StructInitializer();
                that.field = this.field;
                that.value = this.value;
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class ArrayInitializer extends Initializer
        {
            public DArray<Expression> index = new DArray<Expression>();
            public DArray<Initializer> value = new DArray<Initializer>();
            public int dim;
            public Type type;
            public  ArrayInitializer(Loc loc) {
                super(loc, InitKind.array);
            }

            public  void addInit(Expression index, Initializer value) {
                this.index.push(index);
                this.value.push(value);
                this.dim = 0;
                this.type = null;
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ArrayInitializer() {}

            public ArrayInitializer copy() {
                ArrayInitializer that = new ArrayInitializer();
                that.index = this.index;
                that.value = this.value;
                that.dim = this.dim;
                that.type = this.type;
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class VoidInitializer extends Initializer
        {
            public  VoidInitializer(Loc loc) {
                super(loc, InitKind.void_);
            }

            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VoidInitializer() {}

            public VoidInitializer copy() {
                VoidInitializer that = new VoidInitializer();
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class Tuple extends RootObject
        {
            public DArray<RootObject> objects = new DArray<RootObject>();
            public  int dyncast() {
                return DYNCAST.tuple;
            }

            public  BytePtr toChars() {
                return this.objects.toChars();
            }

            public  Tuple() {
                super();
            }


            public Tuple copy() {
                Tuple that = new Tuple();
                that.objects = this.objects;
                return that;
            }
        }
        public static class BaseClass
        {
            public Type type;
            public BaseClass(){
            }
            public BaseClass copy(){
                BaseClass r = new BaseClass();
                r.type = type;
                return r;
            }
            public BaseClass(Type type) {
                this.type = type;
            }

            public BaseClass opAssign(BaseClass that) {
                this.type = that.type;
                return this;
            }
        }
        public static class ModuleDeclaration
        {
            public Loc loc = new Loc();
            public Identifier id;
            public DArray<Identifier> packages;
            public boolean isdeprecated;
            public Expression msg;
            public  ModuleDeclaration(Loc loc, DArray<Identifier> packages, Identifier id, Expression msg, boolean isdeprecated) {
                this.loc = loc.copy();
                this.packages = packages;
                this.id = id;
                this.msg = msg;
                this.isdeprecated = isdeprecated;
            }

            public  BytePtr toChars() {
                OutBuffer buf = new OutBuffer();
                try {
                    if (this.packages != null && ((this.packages).length) != 0)
                    {
                        {
                            int i = 0;
                            for (; i < (this.packages).length;i++){
                                Identifier pid = (this.packages).get(i);
                                buf.writestring(pid.asString());
                                buf.writeByte(46);
                            }
                        }
                    }
                    buf.writestring(this.id.asString());
                    return buf.extractChars();
                }
                finally {
                }
            }

            public ModuleDeclaration(){
                loc = new Loc();
            }
            public ModuleDeclaration copy(){
                ModuleDeclaration r = new ModuleDeclaration();
                r.loc = loc.copy();
                r.id = id;
                r.packages = packages;
                r.isdeprecated = isdeprecated;
                r.msg = msg;
                return r;
            }
            public ModuleDeclaration opAssign(ModuleDeclaration that) {
                this.loc = that.loc;
                this.id = that.id;
                this.packages = that.packages;
                this.isdeprecated = that.isdeprecated;
                this.msg = that.msg;
                return this;
            }
        }
        public static class Prot
        {

            public static class Kind 
            {
                public static final int undefined = 0;
                public static final int none = 1;
                public static final int private_ = 2;
                public static final int package_ = 3;
                public static final int protected_ = 4;
                public static final int public_ = 5;
                public static final int export_ = 6;
            }

            public int kind;
            public Package pkg;
            public Prot(){
            }
            public Prot copy(){
                Prot r = new Prot();
                r.kind = kind;
                r.pkg = pkg;
                return r;
            }
            public Prot(int kind, Package pkg) {
                this.kind = kind;
                this.pkg = pkg;
            }

            public Prot opAssign(Prot that) {
                this.kind = that.kind;
                this.pkg = that.pkg;
                return this;
            }
        }
        public static class Scope
        {
            public Scope(){
            }
            public Scope copy(){
                Scope r = new Scope();
                return r;
            }
            public Scope opAssign(Scope that) {
                return this;
            }
        }
        public static Tuple isTuple(RootObject o) {
            if (!(o != null) || o.dyncast() != DYNCAST.tuple)
                return null;
            return (Tuple)o;
        }

        public static Type isType(RootObject o) {
            if (!(o != null) || o.dyncast() != DYNCAST.type)
                return null;
            return (Type)o;
        }

        public static Expression isExpression(RootObject o) {
            if (!(o != null) || o.dyncast() != DYNCAST.expression)
                return null;
            return (Expression)o;
        }

        public static TemplateParameter isTemplateParameter(RootObject o) {
            if (!(o != null) || o.dyncast() != DYNCAST.templateparameter)
                return null;
            return (TemplateParameter)o;
        }

        public static BytePtr protectionToChars(int kind) {
            switch (kind)
            {
                case Prot.Kind.undefined:
                    return null;
                case Prot.Kind.none:
                    return new BytePtr("none");
                case Prot.Kind.private_:
                    return new BytePtr("private");
                case Prot.Kind.package_:
                    return new BytePtr("package");
                case Prot.Kind.protected_:
                    return new BytePtr("protected");
                case Prot.Kind.public_:
                    return new BytePtr("public");
                case Prot.Kind.export_:
                    return new BytePtr("export");
                default:
                throw SwitchError.INSTANCE;
            }
        }

        public static boolean stcToBuffer(OutBuffer buf, long stc) {
            Ref<Long> stc_ref = ref(stc);
            boolean result = false;
            if ((stc_ref.value & 17592186568704L) == 17592186568704L)
                stc_ref.value &= -524289L;
            for (; (stc_ref.value) != 0;){
                BytePtr p = pcopy(stcToChars(stc_ref));
                if (p == null)
                    break;
                if (!(result))
                    result = true;
                else
                    (buf).writeByte(32);
                (buf).writestring(p);
            }
            return result;
        }

        public static Expression typeToExpression(Type t) {
            return t.toExpression();
        }

        public static BytePtr stcToChars(Ref<Long> stc) {
            {
                int i = 0;
                for (; (astbase.stcToCharstable.get(i).stc) != 0;i++){
                    long tbl = astbase.stcToCharstable.get(i).stc;
                    assert((tbl & 22196369506207L) != 0);
                    if ((stc.value & tbl) != 0)
                    {
                        stc.value &= ~tbl;
                        if (tbl == 134217728L)
                            return new BytePtr("__thread");
                        byte tok = astbase.stcToCharstable.get(i).tok;
                        if ((tok & 0xFF) == 225)
                            return astbase.stcToCharstable.get(i).id;
                        else
                            return Token.toChars(tok);
                    }
                }
            }
            return null;
        }

        public static BytePtr linkageToChars(int linkage) {
            switch (linkage)
            {
                case LINK.default_:
                case LINK.system:
                    return null;
                case LINK.d:
                    return new BytePtr("D");
                case LINK.c:
                    return new BytePtr("C");
                case LINK.cpp:
                    return new BytePtr("C++");
                case LINK.windows:
                    return new BytePtr("Windows");
                case LINK.pascal:
                    return new BytePtr("Pascal");
                case LINK.objc:
                    return new BytePtr("Objective-C");
                default:
                throw SwitchError.INSTANCE;
            }
        }

        public static class Target
        {
            public static int ptrsize;
            public static Type va_listType() {
                if (global.params.isWindows)
                {
                    return Type.tchar.pointerTo();
                }
                else if (global.params.isLinux || global.params.isFreeBSD || global.params.isOpenBSD || global.params.isDragonFlyBSD || global.params.isSolaris || global.params.isOSX)
                {
                    if (global.params.is64bit)
                    {
                        return (new TypeIdentifier(Loc.initial, Identifier.idPool( new ByteSlice("__va_list_tag")))).pointerTo();
                    }
                    else
                    {
                        return Type.tchar.pointerTo();
                    }
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
            }

            public Target(){
            }
            public Target copy(){
                Target r = new Target();
                return r;
            }
            public Target opAssign(Target that) {
                return this;
            }
        }
        public ASTBase(){
        }
        public ASTBase copy(){
            ASTBase r = new ASTBase();
            return r;
        }
        public ASTBase opAssign(ASTBase that) {
            return this;
        }
    }
}
