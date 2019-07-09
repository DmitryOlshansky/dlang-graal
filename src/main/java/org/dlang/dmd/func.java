package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.access.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.blockexit.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.delegatize.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.statement_rewrite_walker.*;
import static org.dlang.dmd.statementsem.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class func {
    static DsymbolTable genCfuncst = null;
    private static class Mismatches
    {
        private boolean isNotShared = false;
        private boolean isMutable = false;
        public Mismatches(){
        }
        public Mismatches copy(){
            Mismatches r = new Mismatches();
            r.isNotShared = isNotShared;
            r.isMutable = isMutable;
            return r;
        }
        public Mismatches(boolean isNotShared, boolean isMutable) {
            this.isNotShared = isNotShared;
            this.isMutable = isMutable;
        }

        public Mismatches opAssign(Mismatches that) {
            this.isNotShared = that.isNotShared;
            this.isMutable = that.isMutable;
            return this;
        }
    }
    private static class Ctxt
    {
        private Ctxt prev;
        private Type type;
        public Ctxt(){
        }
        public Ctxt copy(){
            Ctxt r = new Ctxt();
            r.prev = prev;
            r.type = type;
            return r;
        }
        public Ctxt(Ctxt prev, Type type) {
            this.prev = prev;
            this.type = type;
        }

        public Ctxt opAssign(Ctxt that) {
            this.prev = that.prev;
            this.type = that.type;
            return this;
        }
    }
    private static class PrevSibling
    {
        private PrevSibling p;
        private FuncDeclaration f;
        public PrevSibling(){
        }
        public PrevSibling copy(){
            PrevSibling r = new PrevSibling();
            r.p = p;
            r.f = f;
            return r;
        }
        public PrevSibling(PrevSibling p, FuncDeclaration f) {
            this.p = p;
            this.f = f;
        }

        public PrevSibling opAssign(PrevSibling that) {
            this.p = that.p;
            this.f = that.f;
            return this;
        }
    }
    private static class RetWalker extends StatementRewriteWalker
    {
        private Scope sc;
        private Type tret;
        private FuncLiteralDeclaration fld;
        public  void visit(ReturnStatement s) {
            Expression exp = s.exp;
            if ((exp != null) && !exp.type.equals(this.tret))
            {
                s.exp = exp.castTo(this.sc, this.tret);
            }
        }


        public RetWalker() {}
    }


    public static class ILS 
    {
        public static final int uninitialized = 0;
        public static final int no = 1;
        public static final int yes = 2;
    }


    public static class BUILTIN 
    {
        public static final int unknown = -1;
        public static final int no = 0;
        public static final int yes = 1;
    }

    public static class NrvoWalker extends StatementRewriteWalker
    {
        public FuncDeclaration fd;
        public Scope sc;
        public  void visit(ReturnStatement s) {
            if (this.fd.returnLabel != null)
            {
                GotoStatement gs = new GotoStatement(s.loc, Id.returnLabel);
                gs.label = this.fd.returnLabel;
                Statement s1 = gs;
                if (s.exp != null)
                    s1 = new CompoundStatement(s.loc, slice(new Statement[]{new ExpStatement(s.loc, s.exp), gs}));
                this.replaceCurrent(s1);
            }
        }

        public  void visit(TryFinallyStatement s) {
            DtorExpStatement des = null;
            if (this.fd.nrvo_can && (s.finalbody != null) && ((des = s.finalbody.isDtorExpStatement()) != null) && (pequals(this.fd.nrvo_var, des.var)))
            {
                if (!(global.params.useExceptions && (ClassDeclaration.throwable != null)))
                {
                    this.replaceCurrent(s._body);
                    s._body.accept(this);
                    return ;
                }
                Statement sexception = new DtorExpStatement(Loc.initial, this.fd.nrvo_var.edtor, this.fd.nrvo_var);
                Identifier id = Identifier.generateId(new BytePtr("__o"));
                Statement handler = new PeelStatement(sexception);
                if ((blockExit(sexception, this.fd, false) & BE.fallthru) != 0)
                {
                    ThrowStatement ts = new ThrowStatement(Loc.initial, new IdentifierExp(Loc.initial, id));
                    ts.internalThrow = true;
                    handler = new CompoundStatement(Loc.initial, slice(new Statement[]{handler, ts}));
                }
                DArray<Catch> catches = new DArray<Catch>();
                Catch ctch = new Catch(Loc.initial, getThrowable(), id, handler);
                ctch.internalCatch = true;
                catchSemantic(ctch, this.sc);
                (catches).push(ctch);
                Statement s2 = new TryCatchStatement(Loc.initial, s._body, catches);
                this.fd.eh_none = false;
                this.replaceCurrent(s2);
                s2.accept(this);
            }
            else
                this.visit(s);
        }


        public NrvoWalker() {}

        public NrvoWalker copy() {
            NrvoWalker that = new NrvoWalker();
            that.fd = this.fd;
            that.sc = this.sc;
            that.ps = this.ps;
            return that;
        }
    }

    public static class FUNCFLAG 
    {
        public static final int purityInprocess = 1;
        public static final int safetyInprocess = 2;
        public static final int nothrowInprocess = 4;
        public static final int nogcInprocess = 8;
        public static final int returnInprocess = 16;
        public static final int inlineScanned = 32;
        public static final int inferScope = 64;
        public static final int hasCatches = 128;
        public static final int compileTimeOnly = 256;
    }

    public static class Ensure
    {
        public Identifier id;
        public Statement ensure;
        public  Ensure syntaxCopy() {
            return new Ensure(this.id, this.ensure.syntaxCopy());
        }

        public static DArray<Ensure> arraySyntaxCopy(DArray<Ensure> a) {
            DArray<Ensure> b = null;
            if (a != null)
            {
                b = (a).copy();
                {
                    Slice<Ensure> __r1387 = (a).opSlice().copy();
                    int __key1386 = 0;
                    for (; (__key1386 < __r1387.getLength());__key1386 += 1) {
                        Ensure e = __r1387.get(__key1386).copy();
                        int i = __key1386;
                        b.set(i, e.syntaxCopy());
                    }
                }
            }
            return b;
        }

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
        public static class HiddenParameters
        {
            public VarDeclaration vthis;
            public boolean isThis2 = false;
            public VarDeclaration selectorParameter;
            public HiddenParameters(){
            }
            public HiddenParameters copy(){
                HiddenParameters r = new HiddenParameters();
                r.vthis = vthis;
                r.isThis2 = isThis2;
                r.selectorParameter = selectorParameter;
                return r;
            }
            public HiddenParameters(VarDeclaration vthis, boolean isThis2, VarDeclaration selectorParameter) {
                this.vthis = vthis;
                this.isThis2 = isThis2;
                this.selectorParameter = selectorParameter;
            }

            public HiddenParameters opAssign(HiddenParameters that) {
                this.vthis = that.vthis;
                this.isThis2 = that.isThis2;
                this.selectorParameter = that.selectorParameter;
                return this;
            }
        }
        public DArray<Statement> frequires;
        public DArray<Ensure> fensures;
        public Statement frequire;
        public Statement fensure;
        public Statement fbody;
        public DArray<FuncDeclaration> foverrides = new DArray<FuncDeclaration>();
        public FuncDeclaration fdrequire;
        public FuncDeclaration fdensure;
        public DArray<Expression> fdrequireParams;
        public DArray<Expression> fdensureParams;
        public BytePtr mangleString;
        public VarDeclaration vresult;
        public LabelDsymbol returnLabel;
        public DsymbolTable localsymtab;
        public VarDeclaration vthis;
        public boolean isThis2 = false;
        public VarDeclaration v_arguments;
        public ObjcSelector selector;
        public VarDeclaration selectorParameter;
        public VarDeclaration v_argptr;
        public DArray<VarDeclaration> parameters;
        public DsymbolTable labtab;
        public Dsymbol overnext;
        public FuncDeclaration overnext0;
        public Loc endloc = new Loc();
        public int vtblIndex = -1;
        public boolean naked = false;
        public boolean generated = false;
        public byte isCrtCtorDtor = 0;
        public int inlineStatusStmt = ILS.uninitialized;
        public int inlineStatusExp = ILS.uninitialized;
        public int inlining = PINLINE.default_;
        public CompiledCtfeFunctionPimpl ctfeCode = new CompiledCtfeFunctionPimpl();
        public int inlineNest = 0;
        public boolean isArrayOp = false;
        public boolean eh_none = false;
        public boolean semantic3Errors = false;
        public ForeachStatement fes;
        public BaseClass interfaceVirtual;
        public boolean introducing = false;
        public Type tintro;
        public boolean inferRetType = false;
        public long storage_class2 = 0;
        public int hasReturnExp = 0;
        public boolean nrvo_can = true;
        public VarDeclaration nrvo_var;
        public Symbol shidden;
        public DArray<ReturnStatement> returns;
        public DArray<GotoStatement> gotos;
        public int builtin = BUILTIN.unknown;
        public int tookAddressOf = 0;
        public boolean requiresClosure = false;
        public DArray<VarDeclaration> closureVars = new DArray<VarDeclaration>();
        public DArray<FuncDeclaration> siblingCallers = new DArray<FuncDeclaration>();
        public DArray<FuncDeclaration> inlinedNestedCallees;
        public int flags = 0;
        public  FuncDeclaration(Loc loc, Loc endloc, Identifier ident, long storage_class, Type type) {
            super(loc, ident);
            this.storage_class = storage_class;
            this.type = type;
            if (type != null)
            {
                this.storage_class &= -4465259184133L;
            }
            this.endloc = endloc.copy();
            this.inferRetType = (type != null) && (type.nextOf() == null);
        }

        public static FuncDeclaration create(Loc loc, Loc endloc, Identifier id, long storage_class, Type type) {
            return new FuncDeclaration(loc, endloc, id, storage_class, type);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            FuncDeclaration f = s != null ? (FuncDeclaration)s : new FuncDeclaration(this.loc, this.endloc, this.ident, this.storage_class, this.type.syntaxCopy());
            f.frequires = this.frequires != null ? Statement.arraySyntaxCopy(this.frequires) : null;
            f.fensures = this.fensures != null ? Ensure.arraySyntaxCopy(this.fensures) : null;
            f.fbody = this.fbody != null ? this.fbody.syntaxCopy() : null;
            return f;
        }

        public  boolean functionSemantic() {
            if (this._scope == null)
                return !this.errors;
            if (this.originalType == null)
            {
                TemplateInstance spec = this.isSpeculative();
                int olderrs = global.errors;
                int oldgag = global.gag;
                if ((global.gag != 0) && (spec == null))
                    global.gag = 0;
                dsymbolSemantic(this, this._scope);
                global.gag = oldgag;
                if ((spec != null) && (global.errors != olderrs))
                    spec.errors = global.errors - olderrs != 0;
                if ((olderrs != global.errors))
                    return false;
            }
            this.namespace = (this._scope).namespace;
            if (this.inferRetType && (this.type != null) && (this.type.nextOf() == null))
                return this.functionSemantic3();
            TemplateInstance ti = null;
            if ((this.isInstantiated() != null) && !this.isVirtualMethod() && ((ti = this.parent.isTemplateInstance()) == null) || (ti.isTemplateMixin() != null) || (pequals(ti.tempdecl.ident, this.ident)))
            {
                AggregateDeclaration ad = this.isMemberLocal();
                if ((ad != null) && (ad.sizeok != Sizeok.done))
                {
                }
                else
                    return this.functionSemantic3() || !this.errors;
            }
            if ((this.storage_class & 70368744177664L) != 0)
                return this.functionSemantic3() || !this.errors;
            return !this.errors;
        }

        public  boolean functionSemantic3() {
            if ((this.semanticRun < PASS.semantic3) && (this._scope != null))
            {
                TemplateInstance spec = this.isSpeculative();
                int olderrs = global.errors;
                int oldgag = global.gag;
                if ((global.gag != 0) && (spec == null))
                    global.gag = 0;
                semantic3(this, this._scope);
                global.gag = oldgag;
                if ((spec != null) && (global.errors != olderrs))
                    spec.errors = global.errors - olderrs != 0;
                if ((olderrs != global.errors))
                    return false;
            }
            return !this.errors && !this.semantic3Errors;
        }

        public  boolean checkForwardRef(Loc loc) {
            if (!this.functionSemantic())
                return true;
            if (this.type.deco == null)
            {
                boolean inSemantic3 = this.inferRetType && (this.semanticRun >= PASS.semantic3);
                error(loc, new BytePtr("forward reference to %s`%s`"), inSemantic3 ? new BytePtr("inferred return type of function ") : new BytePtr(""), this.toChars());
                return true;
            }
            return false;
        }

        public  HiddenParameters declareThis(Scope sc, AggregateDeclaration ad) {
            if ((!pequals(this.toParent2(), this.toParentLocal())))
            {
                Type tthis2 = Type.tvoidptr.sarrayOf(2L).pointerTo();
                tthis2 = tthis2.addMod(this.type.mod).addStorageClass(this.storage_class);
                VarDeclaration v2 = new VarDeclaration(this.loc, tthis2, Id.this2, null, 0L);
                v2.storage_class |= 16777248L;
                if (((this.type.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    TypeFunction tf = (TypeFunction)this.type;
                    if (tf.isreturn)
                        v2.storage_class |= 17592186044416L;
                    if (tf.isscope)
                        v2.storage_class |= 524288L;
                    if (((tf.iswild & 0xFF) & 2) != 0)
                        v2.storage_class |= 17592186044416L;
                }
                if (((this.flags & FUNCFLAG.inferScope) != 0) && ((v2.storage_class & 524288L) == 0))
                    v2.storage_class |= 281474976710656L;
                dsymbolSemantic(v2, sc);
                if ((sc).insert(v2) == null)
                    throw new AssertionError("Unreachable code!");
                v2.parent = this;
                return new HiddenParameters(v2, true, null);
            }
            if (ad != null)
            {
                Type thandle = ad.handleType();
                assert(thandle != null);
                thandle = thandle.addMod(this.type.mod);
                thandle = thandle.addStorageClass(this.storage_class);
                VarDeclaration v = new ThisDeclaration(this.loc, thandle);
                v.storage_class |= 32L;
                if (((thandle.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    v.storage_class |= 2097152L;
                    if (((this.type.ty & 0xFF) == ENUMTY.Tfunction) && (((((TypeFunction)this.type).iswild & 0xFF) & 2) != 0))
                        v.storage_class |= 17592186044416L;
                }
                if (((this.type.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    TypeFunction tf = (TypeFunction)this.type;
                    if (tf.isreturn)
                        v.storage_class |= 17592186044416L;
                    if (tf.isscope)
                        v.storage_class |= 524288L;
                }
                if (((this.flags & FUNCFLAG.inferScope) != 0) && ((v.storage_class & 524288L) == 0))
                    v.storage_class |= 281474976710656L;
                dsymbolSemantic(v, sc);
                if ((sc).insert(v) == null)
                    throw new AssertionError("Unreachable code!");
                v.parent = this;
                return new HiddenParameters(v, false, objc().createSelectorParameter(this, sc));
            }
            if (this.isNested())
            {
                VarDeclaration v = new VarDeclaration(this.loc, Type.tvoid.pointerTo(), Id.capture, null, 0L);
                v.storage_class |= 16777248L;
                if (((this.type.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    TypeFunction tf = (TypeFunction)this.type;
                    if (tf.isreturn)
                        v.storage_class |= 17592186044416L;
                    if (tf.isscope)
                        v.storage_class |= 524288L;
                }
                if (((this.flags & FUNCFLAG.inferScope) != 0) && ((v.storage_class & 524288L) == 0))
                    v.storage_class |= 281474976710656L;
                dsymbolSemantic(v, sc);
                if ((sc).insert(v) == null)
                    throw new AssertionError("Unreachable code!");
                v.parent = this;
                return new HiddenParameters(v, false, null);
            }
            return new HiddenParameters(null, false, null);
        }

        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
                return true;
            {
                Dsymbol s = isDsymbol(o);
                if ((s) != null)
                {
                    FuncDeclaration fd1 = this;
                    FuncDeclaration fd2 = s.isFuncDeclaration();
                    if (fd2 == null)
                        return false;
                    FuncAliasDeclaration fa1 = fd1.isFuncAliasDeclaration();
                    FuncDeclaration faf1 = fa1 != null ? fa1.toAliasFunc() : fd1;
                    FuncAliasDeclaration fa2 = fd2.isFuncAliasDeclaration();
                    FuncDeclaration faf2 = fa2 != null ? fa2.toAliasFunc() : fd2;
                    if ((fa1 != null) && (fa2 != null))
                    {
                        return faf1.equals(faf2) && ((fa1.hasOverloads ? 1 : 0) == (fa2.hasOverloads ? 1 : 0));
                    }
                    boolean b1 = fa1 != null;
                    if (b1 && faf1.isUnique() && !fa1.hasOverloads)
                        b1 = false;
                    boolean b2 = fa2 != null;
                    if (b2 && faf2.isUnique() && !fa2.hasOverloads)
                        b2 = false;
                    if (((b1 ? 1 : 0) != (b2 ? 1 : 0)))
                        return false;
                    return faf1.toParent().equals(faf2.toParent()) && faf1.ident.equals(faf2.ident) && faf1.type.equals(faf2.type);
                }
            }
            return false;
        }

        public  int overrides(FuncDeclaration fd) {
            int result = 0;
            if ((pequals(fd.ident, this.ident)))
            {
                int cov = this.type.covariant(fd.type, null, true);
                if (cov != 0)
                {
                    ClassDeclaration cd1 = this.toParent().isClassDeclaration();
                    ClassDeclaration cd2 = fd.toParent().isClassDeclaration();
                    if ((cd1 != null) && (cd2 != null) && cd2.isBaseOf(cd1, null))
                        result = 1;
                }
            }
            return result;
        }

        public  int findVtblIndex(DArray<Dsymbol> vtbl, int dim, boolean fix17349) {
            FuncDeclaration mismatch = null;
            long mismatchstc = 0L;
            int mismatchvi = -1;
            int exactvi = -1;
            int bestvi = -1;
            {
                int vi = 0;
                for (; (vi < dim);vi++){
                    FuncDeclaration fdv = (vtbl).get(vi).isFuncDeclaration();
                    if ((fdv != null) && (pequals(fdv.ident, this.ident)))
                    {
                        if (this.type.equals(fdv.type))
                        {
                            if (fdv.parent.isClassDeclaration() != null)
                            {
                                if (fdv.isFuture())
                                {
                                    bestvi = vi;
                                    continue;
                                }
                                return vi;
                            }
                            if ((exactvi >= 0))
                            {
                                this.error(new BytePtr("cannot determine overridden function"));
                                return exactvi;
                            }
                            exactvi = vi;
                            bestvi = vi;
                            continue;
                        }
                        Ref<Long> stc = ref(0L);
                        int cov = this.type.covariant(fdv.type, ptr(stc), fix17349);
                        switch (cov)
                        {
                            case 0:
                                break;
                            case 1:
                                bestvi = vi;
                                break;
                            case 2:
                                mismatchvi = vi;
                                mismatchstc = stc.value;
                                mismatch = fdv;
                                break;
                            case 3:
                                return -2;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                    }
                }
            }
            if ((bestvi == -1) && (mismatch != null))
            {
                if (mismatchstc != 0)
                {
                    this.type = this.type.addStorageClass(mismatchstc);
                    bestvi = mismatchvi;
                }
            }
            return bestvi;
        }

        public  BaseClass overrideInterface() {
            {
                ClassDeclaration cd = this.toParent2().isClassDeclaration();
                if ((cd) != null)
                {
                    {
                        Slice<BaseClass> __r1388 = cd.interfaces.copy();
                        int __key1389 = 0;
                        for (; (__key1389 < __r1388.getLength());__key1389 += 1) {
                            BaseClass b = __r1388.get(__key1389);
                            int v = this.findVtblIndex((b).sym.vtbl, (b).sym.vtbl.length, true);
                            if ((v >= 0))
                                return b;
                        }
                    }
                }
            }
            return null;
        }

        public  boolean overloadInsert(Dsymbol s) {
            assert((!pequals(s, this)));
            AliasDeclaration ad = s.isAliasDeclaration();
            if (ad != null)
            {
                if (this.overnext != null)
                    return this.overnext.overloadInsert(ad);
                if ((ad.aliassym == null) && ((ad.type.ty & 0xFF) != ENUMTY.Tident) && ((ad.type.ty & 0xFF) != ENUMTY.Tinstance) && ((ad.type.ty & 0xFF) != ENUMTY.Ttypeof))
                {
                    return false;
                }
                this.overnext = ad;
                return true;
            }
            TemplateDeclaration td = s.isTemplateDeclaration();
            if (td != null)
            {
                if (td.funcroot == null)
                    td.funcroot = this;
                if (this.overnext != null)
                    return this.overnext.overloadInsert(td);
                this.overnext = td;
                return true;
            }
            FuncDeclaration fd = s.isFuncDeclaration();
            if (fd == null)
                return false;
            if (this.overnext != null)
            {
                td = this.overnext.isTemplateDeclaration();
                if (td != null)
                    fd.overloadInsert(td);
                else
                    return this.overnext.overloadInsert(fd);
            }
            this.overnext = fd;
            return true;
        }

        public  FuncDeclaration overloadExactMatch(Type t) {
            FuncDeclaration fd = null;
            Function1<Dsymbol,Integer> __lambda2 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f == null)
                        return 0;
                    if (t.equals(f.type))
                    {
                        fd = f;
                        return 1;
                    }
                    if (((t.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        TypeFunction tf = (TypeFunction)f.type;
                        if ((tf.covariant(t, null, true) == 1) && (tf.nextOf().implicitConvTo(t.nextOf()) >= MATCH.constant))
                        {
                            fd = f;
                            return 1;
                        }
                    }
                    return 0;
                }
            };
            overloadApply(this, __lambda2, null);
            return fd;
        }

        public  FuncDeclaration overloadModMatch(Loc loc, Type tthis, Ref<Boolean> hasOverloads) {
            MatchAccumulator m = new MatchAccumulator();
            Function1<Dsymbol,Integer> __lambda4 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    FuncDeclaration f = s.isFuncDeclaration();
                    if ((f == null) || (pequals(f, m.lastf)))
                        return 0;
                    TypeFunction tf = f.type.toTypeFunction();
                    int match = MATCH.nomatch;
                    if (tthis != null)
                    {
                        if (f.needThis())
                            match = f.isCtorDeclaration() != null ? MATCH.exact : MODmethodConv(tthis.mod, tf.mod);
                        else
                            match = MATCH.constant;
                    }
                    else
                    {
                        if (f.needThis())
                            match = MATCH.convert;
                        else
                            match = MATCH.exact;
                    }
                    if ((match == MATCH.nomatch))
                        return 0;
                    try {
                        try {
                            if ((match > m.last))
                                /*goto LcurrIsBetter*/throw Dispatch1.INSTANCE;
                            if ((match < m.last))
                                /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            if (m.lastf.overrides(f) != 0)
                                /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            if (f.overrides(m.lastf) != 0)
                                /*goto LcurrIsBetter*/throw Dispatch1.INSTANCE;
                            m.nextf = f;
                            m.count++;
                            return 0;
                        }
                        catch(Dispatch0 __d){}
                    /*LlastIsBetter:*/
                        m.count++;
                        return 0;
                    }
                    catch(Dispatch1 __d){}
                /*LcurrIsBetter:*/
                    if ((m.last <= MATCH.convert))
                    {
                        m.nextf = null;
                        m.count = 0;
                    }
                    m.last = match;
                    m.lastf = f;
                    m.count++;
                    return 0;
                }
            };
            overloadApply(this, __lambda4, null);
            if ((m.count == 1))
            {
                hasOverloads.value = false;
            }
            else if ((m.count > 1))
            {
                hasOverloads.value = true;
            }
            else
            {
                hasOverloads.value = true;
                TypeFunction tf = this.type.toTypeFunction();
                assert(tthis != null);
                assert(!MODimplicitConv(tthis.mod, tf.mod));
                {
                    OutBuffer thisBuf = new OutBuffer();
                    try {
                        OutBuffer funcBuf = new OutBuffer();
                        try {
                            MODMatchToBuffer(thisBuf, tthis.mod, tf.mod);
                            MODMatchToBuffer(funcBuf, tf.mod, tthis.mod);
                            error(loc, new BytePtr("%smethod %s is not callable using a %sobject"), funcBuf.peekChars(), this.toPrettyChars(false), thisBuf.peekChars());
                        }
                        finally {
                        }
                    }
                    finally {
                    }
                }
            }
            return m.lastf;
        }

        public  TemplateDeclaration findTemplateDeclRoot() {
            FuncDeclaration f = this;
            for (; (f != null) && (f.overnext != null);){
                TemplateDeclaration td = f.overnext.isTemplateDeclaration();
                if (td != null)
                    return td;
                f = f.overnext.isFuncDeclaration();
            }
            return null;
        }

        public  boolean inUnittest() {
            Dsymbol f = this;
            do {
                {
                    if (f.isUnitTestDeclaration() != null)
                        return true;
                    f = f.toParent();
                }
            } while (f != null);
            return false;
        }

        public  int leastAsSpecialized(FuncDeclaration g) {
            int LOG_LEASTAS = 0;
            TypeFunction tf = this.type.toTypeFunction();
            TypeFunction tg = g.type.toTypeFunction();
            int nfparams = tf.parameterList.length();
            if (this.needThis() && g.needThis() && ((tf.mod & 0xFF) != (tg.mod & 0xFF)))
            {
                if (this.isCtorDeclaration() != null)
                {
                    if (!MODimplicitConv(tg.mod, tf.mod))
                        return MATCH.nomatch;
                }
                else
                {
                    if (!MODimplicitConv(tf.mod, tg.mod))
                        return MATCH.nomatch;
                }
            }
            DArray<Expression> args = args = new DArray<Expression>(nfparams);
            try {
                {
                    int u = 0;
                    for (; (u < nfparams);u++){
                        Parameter p = tf.parameterList.get(u);
                        Expression e = null;
                        if ((p.storageClass & 2101248L) != 0)
                        {
                            e = new IdentifierExp(Loc.initial, p.ident);
                            e.type = p.type;
                        }
                        else
                            e = p.type.defaultInitLiteral(Loc.initial);
                        args.set(u, e);
                    }
                }
                int m = tg.callMatch(null, args.opSlice(), 1, null, null);
                try {
                    if ((m > MATCH.nomatch))
                    {
                        if ((tf.parameterList.varargs != 0) && (tg.parameterList.varargs == 0))
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        return m;
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                return MATCH.nomatch;
            }
            finally {
            }
        }

        public  LabelDsymbol searchLabel(Identifier ident) {
            Dsymbol s = null;
            if (this.labtab == null)
                this.labtab = new DsymbolTable();
            s = this.labtab.lookup(ident);
            if (s == null)
            {
                s = new LabelDsymbol(ident);
                this.labtab.insert(s);
            }
            return (LabelDsymbol)s;
        }

        public  int getLevel(FuncDeclaration fd, int intypeof) {
            Dsymbol fdparent = fd.toParent2();
            if ((pequals(fdparent, this)))
                return -1;
            Dsymbol s = this;
            int level = 0;
            for (; (!pequals(fd, s)) && (!pequals(fdparent, s.toParent2()));){
                {
                    FuncDeclaration thisfd = s.isFuncDeclaration();
                    if ((thisfd) != null)
                    {
                        if (!thisfd.isNested() && (thisfd.vthis == null) && (intypeof == 0))
                            return -2;
                    }
                    else
                    {
                        {
                            AggregateDeclaration thiscd = s.isAggregateDeclaration();
                            if ((thiscd) != null)
                            {
                                if (!thiscd.isNested() && (intypeof == 0))
                                    return -2;
                            }
                            else
                                return -2;
                        }
                    }
                }
                s = toParentPFuncDeclaration(s, fd);
                assert(s != null);
                level++;
            }
            return level;
        }

        public  int getLevelAndCheck(Loc loc, Scope sc, FuncDeclaration fd) {
            int level = this.getLevel(fd, (sc).intypeof);
            if ((level != -2))
                return level;
            if (((sc).flags & 16) == 0)
            {
                BytePtr xstatic = pcopy(this.isStatic() ? new BytePtr("static ") : new BytePtr(""));
                error(loc, new BytePtr("%s%s %s cannot access frame of function %s"), xstatic, this.kind(), this.toPrettyChars(false), fd.toPrettyChars(false));
                return -2;
            }
            return 1;
        }

        public int LevelError = -2;
        public  BytePtr toPrettyChars(boolean QualifyTypes) {
            if (this.isMain())
                return new BytePtr("D main");
            else
                return this.toPrettyChars(QualifyTypes);
        }

        public  BytePtr toFullSignature() {
            OutBuffer buf = new OutBuffer();
            try {
                functionToBufferWithIdent(this.type.toTypeFunction(), buf, this.toChars());
                return buf.extractChars();
            }
            finally {
            }
        }

        public  boolean isMain() {
            return (pequals(this.ident, Id.main)) && (this.linkage != LINK.c) && (this.isMember() == null) && !this.isNested();
        }

        public  boolean isCMain() {
            return (pequals(this.ident, Id.main)) && (this.linkage == LINK.c) && (this.isMember() == null) && !this.isNested();
        }

        public  boolean isWinMain() {
            return (pequals(this.ident, Id.WinMain)) && (this.linkage != LINK.c) && (this.isMember() == null);
        }

        public  boolean isDllMain() {
            return (pequals(this.ident, Id.DllMain)) && (this.linkage != LINK.c) && (this.isMember() == null);
        }

        public  boolean isRtInit() {
            return (pequals(this.ident, Id.rt_init)) && (this.linkage == LINK.c) && (this.isMember() == null) && !this.isNested();
        }

        public  boolean isExport() {
            return this.protection.kind == Prot.Kind.export_;
        }

        public  boolean isImportedSymbol() {
            return (this.protection.kind == Prot.Kind.export_) && (this.fbody == null);
        }

        public  boolean isCodeseg() {
            return true;
        }

        public  boolean isOverloadable() {
            return true;
        }

        public  boolean isAbstract() {
            if ((this.storage_class & 16L) != 0)
                return true;
            if ((this.semanticRun >= PASS.semanticdone))
                return false;
            if (this._scope != null)
            {
                if (((this._scope).stc & 16L) != 0)
                    return true;
                this.parent = (this._scope).parent;
                Dsymbol parent = this.toParent();
                if (parent.isInterfaceDeclaration() != null)
                    return true;
            }
            return false;
        }

        public  boolean canInferAttributes(Scope sc) {
            if (this.fbody == null)
                return false;
            if (this.isVirtualMethod())
                return false;
            if (((sc).func != null) && (this.isMember() == null) || (sc).func.isSafeBypassingInference() && (this.isInstantiated() == null))
                return true;
            if ((this.isFuncLiteralDeclaration() != null) || ((this.storage_class & 70368744177664L) != 0) || this.inferRetType && (this.isCtorDeclaration() == null))
                return true;
            if (this.isInstantiated() != null)
            {
                TemplateInstance ti = this.parent.isTemplateInstance();
                if ((ti == null) || (ti.isTemplateMixin() != null) || (pequals(ti.tempdecl.ident, this.ident)))
                    return true;
            }
            return false;
        }

        public  void initInferAttributes() {
            TypeFunction tf = this.type.toTypeFunction();
            if ((tf.purity == PURE.impure))
                this.flags |= FUNCFLAG.purityInprocess;
            if ((tf.trust == TRUST.default_))
                this.flags |= FUNCFLAG.safetyInprocess;
            if (!tf.isnothrow)
                this.flags |= FUNCFLAG.nothrowInprocess;
            if (!tf.isnogc)
                this.flags |= FUNCFLAG.nogcInprocess;
            if (!this.isVirtual() || this.introducing)
                this.flags |= FUNCFLAG.returnInprocess;
            if (global.params.vsafe)
                this.flags |= FUNCFLAG.inferScope;
        }

        public  int isPure() {
            TypeFunction tf = this.type.toTypeFunction();
            if ((this.flags & FUNCFLAG.purityInprocess) != 0)
                this.setImpure();
            if ((tf.purity == PURE.fwdref))
                tf.purityLevel();
            int purity = tf.purity;
            if ((purity > PURE.weak) && this.isNested())
                purity = PURE.weak;
            if ((purity > PURE.weak) && this.needThis())
            {
                if (((this.type.mod & 0xFF) & MODFlags.immutable_) != 0)
                {
                }
                else if ((((this.type.mod & 0xFF) & MODFlags.wildconst) != 0) && (purity >= PURE.const_))
                    purity = PURE.const_;
                else
                    purity = PURE.weak;
            }
            tf.purity = purity;
            return purity;
        }

        public  int isPureBypassingInference() {
            if ((this.flags & FUNCFLAG.purityInprocess) != 0)
                return PURE.fwdref;
            else
                return this.isPure();
        }

        public  boolean setImpure() {
            if ((this.flags & FUNCFLAG.purityInprocess) != 0)
            {
                this.flags &= -2;
                if (this.fes != null)
                    this.fes.func.setImpure();
            }
            else if (this.isPure() != 0)
                return true;
            return false;
        }

        public  boolean isSafe() {
            if ((this.flags & FUNCFLAG.safetyInprocess) != 0)
                this.setUnsafe();
            return this.type.toTypeFunction().trust == TRUST.safe;
        }

        public  boolean isSafeBypassingInference() {
            return ((this.flags & FUNCFLAG.safetyInprocess) == 0) && this.isSafe();
        }

        public  boolean isTrusted() {
            if ((this.flags & FUNCFLAG.safetyInprocess) != 0)
                this.setUnsafe();
            return this.type.toTypeFunction().trust == TRUST.trusted;
        }

        public  boolean setUnsafe() {
            if ((this.flags & FUNCFLAG.safetyInprocess) != 0)
            {
                this.flags &= -3;
                this.type.toTypeFunction().trust = TRUST.system;
                if (this.fes != null)
                    this.fes.func.setUnsafe();
            }
            else if (this.isSafe())
                return true;
            return false;
        }

        public  boolean isNogc() {
            if ((this.flags & FUNCFLAG.nogcInprocess) != 0)
                this.setGC();
            return this.type.toTypeFunction().isnogc;
        }

        public  boolean isNogcBypassingInference() {
            return ((this.flags & FUNCFLAG.nogcInprocess) == 0) && this.isNogc();
        }

        public  boolean setGC() {
            if (((this.flags & FUNCFLAG.nogcInprocess) != 0) && (this.semanticRun < PASS.semantic3) && (this._scope != null))
            {
                semantic2(this, this._scope);
                semantic3(this, this._scope);
            }
            if ((this.flags & FUNCFLAG.nogcInprocess) != 0)
            {
                this.flags &= -9;
                this.type.toTypeFunction().isnogc = false;
                if (this.fes != null)
                    this.fes.func.setGC();
            }
            else if (this.isNogc())
                return true;
            return false;
        }

        public  void printGCUsage(Loc loc, BytePtr warn) {
            if (!global.params.vgc)
                return ;
            dmodule.Module m = this.getModule();
            if ((m != null) && m.isRoot() && !this.inUnittest())
            {
                message(loc, new BytePtr("vgc: %s"), warn);
            }
        }

        public  boolean isReturnIsolated() {
            TypeFunction tf = this.type.toTypeFunction();
            assert(tf.next != null);
            Type treti = tf.next;
            if (tf.isref)
                return this.isTypeIsolatedIndirect(treti);
            return this.isTypeIsolated(treti);
        }

        public  boolean isTypeIsolated(Type t) {
            t = t.baseElemOf();
            switch ((t.ty & 0xFF))
            {
                case 0:
                case 3:
                    return this.isTypeIsolatedIndirect(t.nextOf());
                case 2:
                case 7:
                    return this.isTypeIsolatedIndirect(t);
                case 8:
                    StructDeclaration sym = t.toDsymbol(null).isStructDeclaration();
                    {
                        Slice<VarDeclaration> __r1393 = sym.fields.opSlice().copy();
                        int __key1394 = 0;
                        for (; (__key1394 < __r1393.getLength());__key1394 += 1) {
                            VarDeclaration v = __r1393.get(__key1394);
                            Type tmi = v.type.addMod(t.mod);
                            if (!this.isTypeIsolated(tmi))
                                return false;
                        }
                    }
                    return true;
                default:
                return true;
            }
        }

        public  boolean isTypeIsolatedIndirect(Type t) {
            assert(t != null);
            if ((this.isPureBypassingInference() == 0) || this.isNested())
                return false;
            TypeFunction tf = this.type.toTypeFunction();
            int dim = tf.parameterList.length();
            {
                int i = 0;
                for (; (i < dim);i++){
                    Parameter fparam = tf.parameterList.get(i);
                    Type tp = fparam.type;
                    if (tp == null)
                        continue;
                    if ((fparam.storageClass & 2109440L) != 0)
                    {
                        if (!traverseIndirections(tp, t))
                            return false;
                        continue;
                    }
                    Function2<Type,Type,Boolean> traverse = new Function2<Type,Type,Boolean>(){
                        public Boolean invoke(Type tp, Type t){
                            tp = tp.baseElemOf();
                            switch ((tp.ty & 0xFF))
                            {
                                case 0:
                                case 3:
                                    return traverseIndirections(tp.nextOf(), t);
                                case 2:
                                case 7:
                                    return traverseIndirections(tp, t);
                                case 8:
                                    StructDeclaration sym = tp.toDsymbol(null).isStructDeclaration();
                                    {
                                        Slice<VarDeclaration> __r1395 = sym.fields.opSlice().copy();
                                        int __key1396 = 0;
                                        for (; (__key1396 < __r1395.getLength());__key1396 += 1) {
                                            VarDeclaration v = __r1395.get(__key1396);
                                            Type tprmi = v.type.addMod(tp.mod);
                                            if (!traverse.invoke(tprmi, t))
                                                return false;
                                        }
                                    }
                                    return true;
                                default:
                                return true;
                            }
                        }
                    };
                    if (!traverse.invoke(tp, t))
                        return false;
                }
            }
            {
                AggregateDeclaration ad = this.isCtorDeclaration() != null ? null : this.isThis();
                if ((ad) != null)
                {
                    Type tthis = ad.getType().addMod(tf.mod);
                    if (!traverseIndirections(tthis, t))
                        return false;
                }
            }
            return true;
        }

        public  boolean isNested() {
            FuncDeclaration f = this.toAliasFunc();
            return ((f.storage_class & 1L) == 0L) && (f.linkage == LINK.d) && (f.toParent2().isFuncDeclaration() != null) || (f.toParent2() != f.toParentLocal());
        }

        public  AggregateDeclaration isThis() {
            AggregateDeclaration ad = (this.storage_class & 1L) != 0 ? objc().isThis(this) : this.isMemberLocal();
            return ad;
        }

        public  boolean needThis() {
            return this.toAliasFunc().isThis() != null;
        }

        public  boolean isVirtualMethod() {
            if ((!pequals(this.toAliasFunc(), this)))
                return this.toAliasFunc().isVirtualMethod();
            if (!this.isVirtual())
                return false;
            if (this.isFinalFunc() && (this.foverrides.length == 0))
            {
                return false;
            }
            return true;
        }

        public  boolean isVirtual() {
            if ((!pequals(this.toAliasFunc(), this)))
                return this.toAliasFunc().isVirtual();
            Dsymbol p = this.toParent();
            if ((this.isMember() == null) || (p.isClassDeclaration() == null))
                return false;
            if ((p.isClassDeclaration().classKind == ClassKind.objc) && (p.isInterfaceDeclaration() == null))
                return objc().isVirtual(this);
            return !(this.isStatic() || (this.protection.kind == Prot.Kind.private_) || (this.protection.kind == Prot.Kind.package_)) && !((p.isInterfaceDeclaration() != null) && this.isFinalFunc());
        }

        public  boolean isFinalFunc() {
            if ((!pequals(this.toAliasFunc(), this)))
                return this.toAliasFunc().isFinalFunc();
            if (this.isMember() == null)
                return false;
            if (this.isFinal())
                return true;
            ClassDeclaration cd = this.toParent().isClassDeclaration();
            return (cd != null) && ((cd.storage_class & 8L) != 0);
        }

        public  boolean addPreInvariant() {
            AggregateDeclaration ad = this.isThis();
            ClassDeclaration cd = ad != null ? ad.isClassDeclaration() : null;
            return (ad != null) && !((cd != null) && cd.isCPPclass()) && ((global.params.useInvariants & 0xFF) == 2) && (this.protection.kind == Prot.Kind.protected_) || (this.protection.kind == Prot.Kind.public_) || (this.protection.kind == Prot.Kind.export_) && !this.naked;
        }

        public  boolean addPostInvariant() {
            AggregateDeclaration ad = this.isThis();
            ClassDeclaration cd = ad != null ? ad.isClassDeclaration() : null;
            return (ad != null) && !((cd != null) && cd.isCPPclass()) && (ad.inv != null) && ((global.params.useInvariants & 0xFF) == 2) && (this.protection.kind == Prot.Kind.protected_) || (this.protection.kind == Prot.Kind.public_) || (this.protection.kind == Prot.Kind.export_) && !this.naked;
        }

        public  BytePtr kind() {
            return this.generated ? new BytePtr("generated function") : new BytePtr("function");
        }

        public  boolean isUnique() {
            boolean result = false;
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f == null)
                        return 0;
                    if (result)
                    {
                        result = false;
                        return 1;
                    }
                    else
                    {
                        result = true;
                        return 0;
                    }
                }
            };
            overloadApply(this, __lambda1, null);
            return result;
        }

        public  boolean checkNestedReference(Scope sc, Loc loc) {
            Ref<Scope> sc_ref = ref(sc);
            {
                FuncLiteralDeclaration fld = this.isFuncLiteralDeclaration();
                if ((fld) != null)
                {
                    if (((fld.tok & 0xFF) == 0))
                    {
                        fld.tok = TOK.function_;
                        fld.vthis = null;
                    }
                }
            }
            if ((this.parent == null) || (pequals(this.parent, (sc_ref.value).parent)))
                return false;
            if ((pequals(this.ident, Id.require)) || (pequals(this.ident, Id.ensure)))
                return false;
            if ((this.isThis() == null) && !this.isNested())
                return false;
            Ref<FuncDeclaration> fdthis = ref((sc_ref.value).parent.isFuncDeclaration());
            if (fdthis.value == null)
                return false;
            Dsymbol p = this.toParentLocal();
            Dsymbol p2 = this.toParent2();
            ensureStaticLinkTo(fdthis.value, p);
            if ((!pequals(p, p2)))
                ensureStaticLinkTo(fdthis.value, p2);
            if (this.isNested())
            {
                Function1<FuncDeclaration,Boolean> checkEnclosing = new Function1<FuncDeclaration,Boolean>(){
                    public Boolean invoke(FuncDeclaration fdv){
                        if (fdv == null)
                            return false;
                        if ((pequals(fdv, fdthis.value)))
                            return false;
                        if ((!pequals(fdthis.value, this)))
                        {
                            boolean found = false;
                            {
                                int i = 0;
                                for (; (i < siblingCallers.length);i += 1){
                                    if ((pequals(siblingCallers.get(i), fdthis.value)))
                                        found = true;
                                }
                            }
                            if (!found)
                            {
                                if (((sc_ref.value).intypeof == 0) && (((sc_ref.value).flags & 256) == 0))
                                    siblingCallers.push(fdthis.value);
                            }
                        }
                        int lv = fdthis.value.getLevelAndCheck(loc, sc_ref.value, fdv);
                        if ((lv == -2))
                            return true;
                        if ((lv == -1))
                            return false;
                        if ((lv == 0))
                            return false;
                        return false;
                    }
                };
                if (checkEnclosing.invoke(p.isFuncDeclaration()))
                    return true;
                if (checkEnclosing.invoke((pequals(p, p2)) ? null : p2.isFuncDeclaration()))
                    return true;
            }
            return false;
        }

        public  boolean needsClosure() {
            try {
                if (this.requiresClosure)
                    /*goto Lyes*/throw Dispatch0.INSTANCE;
                {
                    int i = 0;
                    for (; (i < this.closureVars.length);i++){
                        VarDeclaration v = this.closureVars.get(i);
                        {
                            int j = 0;
                            for (; (j < v.nestedrefs.length);j++){
                                FuncDeclaration f = v.nestedrefs.get(j);
                                assert((!pequals(f, this)));
                                {
                                    Dsymbol s = f;
                                    for (; (s != null) && (!pequals(s, this));s = toParentPFuncDeclaration(s, this)){
                                        FuncDeclaration fx = s.isFuncDeclaration();
                                        if (fx == null)
                                            continue;
                                        if ((fx.isThis() != null) || (fx.tookAddressOf != 0))
                                        {
                                            markAsNeedingClosure((pequals(fx, f)) ? toParentPFuncDeclaration(fx, this) : fx, this);
                                            this.requiresClosure = true;
                                        }
                                        if (checkEscapingSiblings(fx, this, null))
                                            this.requiresClosure = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (this.requiresClosure)
                    /*goto Lyes*/throw Dispatch0.INSTANCE;
                return false;
            }
            catch(Dispatch0 __d){}
        /*Lyes:*/
            return true;
        }

        public  boolean checkClosure() {
            if (!this.needsClosure())
                return false;
            if (this.setGC())
            {
                this.error(new BytePtr("is `@nogc` yet allocates closures with the GC"));
                if (global.gag != 0)
                    return true;
            }
            else
            {
                this.printGCUsage(this.loc, new BytePtr("using closure causes GC allocation"));
                return false;
            }
            DArray<FuncDeclaration> a = new DArray<FuncDeclaration>();
            try {
                {
                    Slice<VarDeclaration> __r1397 = this.closureVars.opSlice().copy();
                    int __key1398 = 0;
                    for (; (__key1398 < __r1397.getLength());__key1398 += 1) {
                        VarDeclaration v = __r1397.get(__key1398);
                        {
                            Slice<FuncDeclaration> __r1399 = v.nestedrefs.opSlice().copy();
                            int __key1400 = 0;
                            for (; (__key1400 < __r1399.getLength());__key1400 += 1) {
                                FuncDeclaration f = __r1399.get(__key1400);
                                assert((f != this));
                            /*LcheckAncestorsOfANestedRef:*/
                                {
                                    Dsymbol s = f;
                                    for (; (s != null) && (s != this);s = toParentPFuncDeclaration(s, this)){
                                        FuncDeclaration fx = s.isFuncDeclaration();
                                        if (fx == null)
                                            continue;
                                        if ((fx.isThis() != null) || (fx.tookAddressOf != 0) || checkEscapingSiblings(fx, this, null))
                                        {
                                            {
                                                Slice<FuncDeclaration> __r1401 = a.opSlice().copy();
                                                int __key1402 = 0;
                                                for (; (__key1402 < __r1401.getLength());__key1402 += 1) {
                                                    FuncDeclaration f2 = __r1401.get(__key1402);
                                                    if ((pequals(f2, f)))
                                                        break LcheckAncestorsOfANestedRef;
                                                }
                                            }
                                            a.push(f);
                                            errorSupplemental(f.loc, new BytePtr("%s closes over variable %s at %s"), f.toPrettyChars(false), v.toChars(), v.loc.toChars(global.params.showColumns));
                                            break LcheckAncestorsOfANestedRef;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
            finally {
            }
        }

        public  boolean hasNestedFrameRefs() {
            if (this.closureVars.length != 0)
                return true;
            if ((this.fdrequire != null) || (this.fdensure != null))
                return true;
            if ((this.foverrides.length != 0) && this.isVirtualMethod())
            {
                {
                    int i = 0;
                    for (; (i < this.foverrides.length);i++){
                        FuncDeclaration fdv = this.foverrides.get(i);
                        if (fdv.hasNestedFrameRefs())
                            return true;
                    }
                }
            }
            return false;
        }

        public  boolean canBuildResultVar() {
            TypeFunction f = (TypeFunction)this.type;
            return (f != null) && (f.nextOf() != null) && ((f.nextOf().toBasetype().ty & 0xFF) != ENUMTY.Tvoid);
        }

        public  void buildResultVar(Scope sc, Type tret) {
            if (this.vresult == null)
            {
                Loc loc = this.fensure != null ? this.fensure.loc : this.loc.copy();
                this.vresult = new VarDeclaration(loc, tret, Id.result, null, 0L);
                this.vresult.storage_class |= 1099528404992L;
                if (!this.isVirtual())
                    this.vresult.storage_class |= 4L;
                this.vresult.storage_class |= 274877906944L;
                this.vresult.parent = this;
            }
            if ((sc != null) && (this.vresult.semanticRun == PASS.init))
            {
                TypeFunction tf = this.type.toTypeFunction();
                if (tf.isref)
                    this.vresult.storage_class |= 2097152L;
                this.vresult.type = tret;
                dsymbolSemantic(this.vresult, sc);
                if ((sc).insert(this.vresult) == null)
                    this.error(new BytePtr("out result %s is already defined"), this.vresult.toChars());
                assert((pequals(this.vresult.parent, this)));
            }
        }

        public  Statement mergeFrequire(Statement sf, DArray<Expression> params) {
            {
                Slice<FuncDeclaration> __r1403 = this.foverrides.opSlice().copy();
                int __key1404 = 0;
                for (; (__key1404 < __r1403.getLength());__key1404 += 1) {
                    FuncDeclaration fdv = __r1403.get(__key1404);
                    if ((fdv.frequires != null) && (fdv.semanticRun != PASS.semantic3done))
                    {
                        assert(fdv._scope != null);
                        Scope sc = (fdv._scope).push();
                        (sc).stc &= -129L;
                        semantic3(fdv, sc);
                        (sc).pop();
                    }
                    sf = fdv.mergeFrequire(sf, params);
                    if ((sf != null) && (fdv.fdrequire != null))
                    {
                        params = Expression.arraySyntaxCopy(params);
                        Expression e = new CallExp(this.loc, new VarExp(this.loc, fdv.fdrequire, false), params);
                        Statement s2 = new ExpStatement(this.loc, e);
                        Catch c = new Catch(this.loc, getThrowable(), null, sf);
                        c.internalCatch = true;
                        DArray<Catch> catches = new DArray<Catch>();
                        (catches).push(c);
                        sf = new TryCatchStatement(this.loc, s2, catches);
                    }
                    else
                        return null;
                }
            }
            return sf;
        }

        public static boolean needsFensure(FuncDeclaration fd) {
            if (fd.fensures != null)
                return true;
            {
                Slice<FuncDeclaration> __r1405 = fd.foverrides.opSlice().copy();
                int __key1406 = 0;
                for (; (__key1406 < __r1405.getLength());__key1406 += 1) {
                    FuncDeclaration fdv = __r1405.get(__key1406);
                    if (needsFensure(fdv))
                        return true;
                }
            }
            return false;
        }

        public  void buildEnsureRequire() {
            if (this.frequires != null)
            {
                assert((this.frequires).length != 0);
                Loc loc = (this.frequires).get(0).loc.copy();
                DArray<Statement> s = new DArray<Statement>();
                {
                    Slice<Statement> __r1407 = (this.frequires).opSlice().copy();
                    int __key1408 = 0;
                    for (; (__key1408 < __r1407.getLength());__key1408 += 1) {
                        Statement r = __r1407.get(__key1408);
                        (s).push(new ScopeStatement(r.loc, r, r.loc));
                    }
                }
                this.frequire = new CompoundStatement(loc, s);
            }
            if (this.fensures != null)
            {
                assert((this.fensures).length != 0);
                Loc loc = (this.fensures).get(0).ensure.loc.copy();
                DArray<Statement> s = new DArray<Statement>();
                {
                    Slice<Ensure> __r1409 = (this.fensures).opSlice().copy();
                    int __key1410 = 0;
                    for (; (__key1410 < __r1409.getLength());__key1410 += 1) {
                        Ensure r = __r1409.get(__key1410).copy();
                        if ((r.id != null) && this.canBuildResultVar())
                        {
                            Loc rloc = r.ensure.loc.copy();
                            IdentifierExp resultId = new IdentifierExp(rloc, Id.result);
                            ExpInitializer init = new ExpInitializer(rloc, resultId);
                            long stc = 1374391631872L;
                            VarDeclaration decl = new VarDeclaration(rloc, null, r.id, init, stc);
                            ExpStatement sdecl = new ExpStatement(rloc, decl);
                            (s).push(new ScopeStatement(rloc, new CompoundStatement(rloc, slice(new Statement[]{sdecl, r.ensure})), rloc));
                        }
                        else
                        {
                            (s).push(r.ensure);
                        }
                    }
                }
                this.fensure = new CompoundStatement(loc, s);
            }
            if (!this.isVirtual())
                return ;
            TypeFunction f = (TypeFunction)this.type;
            Function1<DArray<Parameter>,DArray<Parameter>> toRefCopy = new Function1<DArray<Parameter>,DArray<Parameter>>(){
                public DArray<Parameter> invoke(DArray<Parameter> params){
                    Ref<DArray<Parameter>> result = ref(new DArray<Parameter>());
                    Function2<Integer,Parameter,Integer> toRefDg = new Function2<Integer,Parameter,Integer>(){
                        public Integer invoke(Integer n, Parameter p){
                            p = p.syntaxCopy();
                            if ((p.storageClass & 8192L) == 0)
                                p.storageClass = (p.storageClass | 2097152L) & -4097L;
                            p.defaultArg = null;
                            (result.value).push(p);
                            return 0;
                        }
                    };
                    Parameter._foreach(params, toRefDg, null);
                    return result.value;
                }
            };
            if (this.frequire != null)
            {
                Loc loc = this.frequire.loc.copy();
                this.fdrequireParams = new DArray<Expression>();
                if (this.parameters != null)
                {
                    {
                        Slice<VarDeclaration> __r1411 = (this.parameters).opSlice().copy();
                        int __key1412 = 0;
                        for (; (__key1412 < __r1411.getLength());__key1412 += 1) {
                            VarDeclaration vd = __r1411.get(__key1412);
                            (this.fdrequireParams).push(new VarExp(loc, vd, true));
                        }
                    }
                }
                TypeFunction fo = this.originalType != null ? (TypeFunction)this.originalType : (TypeFunction)f;
                DArray<Parameter> fparams = toRefCopy.invoke(fo.parameterList.parameters);
                TypeFunction tf = new TypeFunction(new ParameterList(fparams, VarArg.none), Type.tvoid, LINK.d, 0L);
                tf.isnothrow = f.isnothrow;
                tf.isnogc = f.isnogc;
                tf.purity = f.purity;
                tf.trust = f.trust;
                FuncDeclaration fd = new FuncDeclaration(loc, loc, Id.require, 0L, tf);
                fd.fbody = this.frequire;
                Statement s1 = new ExpStatement(loc, fd);
                Expression e = new CallExp(loc, new VarExp(loc, fd, false), this.fdrequireParams);
                Statement s2 = new ExpStatement(loc, e);
                this.frequire = new CompoundStatement(loc, slice(new Statement[]{s1, s2}));
                this.fdrequire = fd;
            }
            this.fdensureParams = new DArray<Expression>();
            if (this.canBuildResultVar())
                (this.fdensureParams).push(new IdentifierExp(this.loc, Id.result));
            if (this.parameters != null)
            {
                {
                    Slice<VarDeclaration> __r1413 = (this.parameters).opSlice().copy();
                    int __key1414 = 0;
                    for (; (__key1414 < __r1413.getLength());__key1414 += 1) {
                        VarDeclaration vd = __r1413.get(__key1414);
                        (this.fdensureParams).push(new VarExp(this.loc, vd, true));
                    }
                }
            }
            if (this.fensure != null)
            {
                Loc loc = this.fensure.loc.copy();
                DArray<Parameter> fparams = new DArray<Parameter>();
                if (this.canBuildResultVar())
                {
                    Parameter p = new Parameter(2097156L, f.nextOf(), Id.result, null, null);
                    (fparams).push(p);
                }
                TypeFunction fo = this.originalType != null ? (TypeFunction)this.originalType : (TypeFunction)f;
                (fparams).pushSlice((toRefCopy.invoke(fo.parameterList.parameters)).opSlice());
                TypeFunction tf = new TypeFunction(new ParameterList(fparams, VarArg.none), Type.tvoid, LINK.d, 0L);
                tf.isnothrow = f.isnothrow;
                tf.isnogc = f.isnogc;
                tf.purity = f.purity;
                tf.trust = f.trust;
                FuncDeclaration fd = new FuncDeclaration(loc, loc, Id.ensure, 0L, tf);
                fd.fbody = this.fensure;
                Statement s1 = new ExpStatement(loc, fd);
                Expression e = new CallExp(loc, new VarExp(loc, fd, false), this.fdensureParams);
                Statement s2 = new ExpStatement(loc, e);
                this.fensure = new CompoundStatement(loc, slice(new Statement[]{s1, s2}));
                this.fdensure = fd;
            }
        }

        public  Statement mergeFensure(Statement sf, Identifier oid, DArray<Expression> params) {
            {
                Slice<FuncDeclaration> __r1415 = this.foverrides.opSlice().copy();
                int __key1416 = 0;
                for (; (__key1416 < __r1415.getLength());__key1416 += 1) {
                    FuncDeclaration fdv = __r1415.get(__key1416);
                    if (needsFensure(fdv) && (fdv.semanticRun != PASS.semantic3done))
                    {
                        assert(fdv._scope != null);
                        Scope sc = (fdv._scope).push();
                        (sc).stc &= -129L;
                        semantic3(fdv, sc);
                        (sc).pop();
                    }
                    sf = fdv.mergeFensure(sf, oid, params);
                    if (fdv.fdensure != null)
                    {
                        params = Expression.arraySyntaxCopy(params);
                        if (this.canBuildResultVar())
                        {
                            Type t1 = fdv.type.nextOf().toBasetype();
                            Type t2 = this.type.nextOf().toBasetype();
                            if (t1.isBaseOf(t2, null))
                            {
                                Ptr<Expression> eresult = pcopy((params).get(0));
                                ExpInitializer ei = new ExpInitializer(Loc.initial, eresult.get());
                                VarDeclaration v = new VarDeclaration(Loc.initial, t1, Identifier.generateId(new BytePtr("__covres")), ei, 0L);
                                v.storage_class |= 1099511627776L;
                                DeclarationExp de = new DeclarationExp(Loc.initial, v);
                                VarExp ve = new VarExp(Loc.initial, v, true);
                                eresult.set(0, (new CommaExp(Loc.initial, de, ve, true)));
                            }
                        }
                        Expression e = new CallExp(this.loc, new VarExp(this.loc, fdv.fdensure, false), params);
                        Statement s2 = new ExpStatement(this.loc, e);
                        if (sf != null)
                        {
                            sf = new CompoundStatement(sf.loc, slice(new Statement[]{s2, sf}));
                        }
                        else
                            sf = s2;
                    }
                }
            }
            return sf;
        }

        public  ParameterList getParameterList() {
            if (this.type != null)
            {
                TypeFunction fdtype = this.type.isTypeFunction();
                return fdtype.parameterList;
            }
            return new ParameterList(null, VarArg.none);
        }

        public static FuncDeclaration genCfunc(DArray<Parameter> fparams, Type treturn, BytePtr name, long stc) {
            return genCfunc(fparams, treturn, Identifier.idPool(name, strlen(name)), stc);
        }

        public static FuncDeclaration genCfunc(DArray<Parameter> fparams, Type treturn, Identifier id, long stc) {
            FuncDeclaration fd = null;
            TypeFunction tf = null;
            Dsymbol s = null;
            if (func.genCfuncst == null)
                func.genCfuncst = new DsymbolTable();
            s = func.genCfuncst.lookup(id);
            if (s != null)
            {
                fd = s.isFuncDeclaration();
                assert(fd != null);
                assert(fd.type.nextOf().equals(treturn));
            }
            else
            {
                tf = new TypeFunction(new ParameterList(fparams, VarArg.none), treturn, LINK.c, stc);
                fd = new FuncDeclaration(Loc.initial, Loc.initial, id, 1L, tf);
                fd.protection = new Prot(Prot.Kind.public_).copy();
                fd.linkage = LINK.c;
                func.genCfuncst.insert((Dsymbol)fd);
            }
            return fd;
        }

        public  void checkDmain() {
            TypeFunction tf = this.type.toTypeFunction();
            int nparams = tf.parameterList.length();
            boolean argerr = false;
            if ((nparams == 1))
            {
                Parameter fparam0 = tf.parameterList.get(0);
                Type t = fparam0.type.toBasetype();
                if (((t.ty & 0xFF) != ENUMTY.Tarray) || ((t.nextOf().ty & 0xFF) != ENUMTY.Tarray) || ((t.nextOf().nextOf().ty & 0xFF) != ENUMTY.Tchar) || ((fparam0.storageClass & 2109440L) != 0))
                {
                    argerr = true;
                }
            }
            if (tf.nextOf() == null)
                this.error(new BytePtr("must return `int` or `void`"));
            else if (((tf.nextOf().ty & 0xFF) != ENUMTY.Tint32) && ((tf.nextOf().ty & 0xFF) != ENUMTY.Tvoid))
                this.error(new BytePtr("must return `int` or `void`, not `%s`"), tf.nextOf().toChars());
            else if ((tf.parameterList.varargs != 0) || (nparams >= 2) || argerr)
                this.error(new BytePtr("parameters must be `main()` or `main(string[] args)`"));
        }

        public  FuncDeclaration isFuncDeclaration() {
            return this;
        }

        public  FuncDeclaration toAliasFunc() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FuncDeclaration() {}

        public FuncDeclaration copy() {
            FuncDeclaration that = new FuncDeclaration();
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static Expression addInvariant(Loc loc, Scope sc, AggregateDeclaration ad, VarDeclaration vthis) {
        Expression e = null;
        FuncDeclaration inv = ad.inv;
        ClassDeclaration cd = ad.isClassDeclaration();
        for (; (inv == null) && (cd != null);){
            cd = cd.baseClass;
            if (cd == null)
                break;
            inv = cd.inv;
        }
        if (inv != null)
        {
            inv.functionSemantic();
            e = new ThisExp(Loc.initial);
            e.type = ad.type.addMod(vthis.type.mod);
            e = new DotVarExp(Loc.initial, e, inv, false);
            e.type = inv.type;
            e = new CallExp(Loc.initial, e);
            e.type = Type.tvoid;
        }
        return e;
    }

    public static int overloadApply(Dsymbol fstart, Function1<Dsymbol,Integer> dg, Scope sc) {
        Dsymbol next = null;
        {
            Dsymbol d = fstart;
            for (; d != null;d = next){
                {
                    OverDeclaration od = d.isOverDeclaration();
                    if ((od) != null)
                    {
                        if (od.hasOverloads)
                        {
                            if (sc != null)
                            {
                                if (checkSymbolAccess(sc, od))
                                {
                                    {
                                        int r = overloadApply(od.aliassym, dg, sc);
                                        if ((r) != 0)
                                            return r;
                                    }
                                }
                            }
                            else {
                                int r = overloadApply(od.aliassym, dg, sc);
                                if ((r) != 0)
                                    return r;
                            }
                        }
                        else
                        {
                            {
                                int r = dg.invoke(od.aliassym);
                                if ((r) != 0)
                                    return r;
                            }
                        }
                        next = od.overnext;
                    }
                    else {
                        FuncAliasDeclaration fa = d.isFuncAliasDeclaration();
                        if ((fa) != null)
                        {
                            if (fa.hasOverloads)
                            {
                                {
                                    int r = overloadApply(fa.funcalias, dg, sc);
                                    if ((r) != 0)
                                        return r;
                                }
                            }
                            else {
                                FuncDeclaration fd = fa.toAliasFunc();
                                if ((fd) != null)
                                {
                                    {
                                        int r = dg.invoke(fd);
                                        if ((r) != 0)
                                            return r;
                                    }
                                }
                                else
                                {
                                    d.error(new BytePtr("is aliased to a function"));
                                    break;
                                }
                            }
                            next = fa.overnext;
                        }
                        else {
                            AliasDeclaration ad = d.isAliasDeclaration();
                            if ((ad) != null)
                            {
                                if (sc != null)
                                {
                                    if (checkSymbolAccess(sc, ad))
                                        next = ad.toAlias();
                                }
                                else
                                    next = ad.toAlias();
                                if ((pequals(next, ad)))
                                    break;
                                if ((pequals(next, fstart)))
                                    break;
                            }
                            else {
                                TemplateDeclaration td = d.isTemplateDeclaration();
                                if ((td) != null)
                                {
                                    {
                                        int r = dg.invoke(td);
                                        if ((r) != 0)
                                            return r;
                                    }
                                    next = td.overnext;
                                }
                                else {
                                    FuncDeclaration fd = d.isFuncDeclaration();
                                    if ((fd) != null)
                                    {
                                        {
                                            int r = dg.invoke(fd);
                                            if ((r) != 0)
                                                return r;
                                        }
                                        next = fd.overnext;
                                    }
                                    else
                                    {
                                        d.error(new BytePtr("is aliased to a function"));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static Mismatches MODMatchToBuffer(OutBuffer buf, byte lhsMod, byte rhsMod) {
        Mismatches mismatches = new Mismatches();
        boolean bothMutable = ((lhsMod & 0xFF) & (rhsMod & 0xFF)) == 0;
        boolean sharedMismatch = (((lhsMod & 0xFF) ^ (rhsMod & 0xFF)) & MODFlags.shared_) != 0;
        boolean sharedMismatchOnly = ((lhsMod & 0xFF) ^ (rhsMod & 0xFF)) == MODFlags.shared_;
        if (((lhsMod & 0xFF) & MODFlags.shared_) != 0)
            (buf).writestring(new ByteSlice("`shared` "));
        else if (sharedMismatch && (((lhsMod & 0xFF) & MODFlags.immutable_) == 0))
        {
            (buf).writestring(new ByteSlice("non-shared "));
            mismatches.isNotShared = true;
        }
        if (bothMutable && sharedMismatchOnly)
        {
        }
        else if (((lhsMod & 0xFF) & MODFlags.immutable_) != 0)
            (buf).writestring(new ByteSlice("`immutable` "));
        else if (((lhsMod & 0xFF) & MODFlags.const_) != 0)
            (buf).writestring(new ByteSlice("`const` "));
        else if (((lhsMod & 0xFF) & MODFlags.wild) != 0)
            (buf).writestring(new ByteSlice("`inout` "));
        else
        {
            (buf).writestring(new ByteSlice("mutable "));
            mismatches.isMutable = true;
        }
        return mismatches;
    }

    public static BytePtr prependSpace(BytePtr str) {
        if ((str == null) || (str.get() == 0))
            return new BytePtr("");
        return toBytePtr((new ByteSlice(" ").concat(str.slice(0,strlen(str))).concat(new ByteSlice("\u0000"))));
    }


    public static class FuncResolveFlag 
    {
        public static final byte standard = (byte)0;
        public static final byte quiet = (byte)1;
        public static final byte overloadOnly = (byte)2;
    }

    public static FuncDeclaration resolveFuncCall(Loc loc, Scope sc, Dsymbol s, DArray<RootObject> tiargs, Type tthis, DArray<Expression> fargs, byte flags) {
        if (s == null)
            return null;
        if ((tiargs != null) && arrayObjectIsError(tiargs) || (fargs != null) && arrayObjectIsError(((DArray<RootObject>)fargs)))
        {
            return null;
        }
        MatchAccumulator m = new MatchAccumulator();
        functionResolve(m, s, loc, sc, tiargs, tthis, fargs, null);
        Dsymbol orig_s = s;
        if ((m.last > MATCH.nomatch) && (m.lastf != null))
        {
            if ((m.count == 1))
            {
                if (((flags & 0xFF) & 1) == 0)
                    m.lastf.functionSemantic();
                return m.lastf;
            }
            if ((((flags & 0xFF) & 2) != 0) && (tthis == null) && m.lastf.needThis())
            {
                return m.lastf;
            }
        }
        if ((m.last <= MATCH.nomatch))
        {
            if ((m.count == 1))
                return m.lastf;
            if (((flags & 0xFF) & 1) != 0)
                return null;
        }
        FuncDeclaration fd = s.isFuncDeclaration();
        OverDeclaration od = s.isOverDeclaration();
        TemplateDeclaration td = s.isTemplateDeclaration();
        if ((td != null) && (td.funcroot != null))
            s = (fd = td.funcroot);
        OutBuffer tiargsBuf = new OutBuffer();
        try {
            arrayObjectsToBuffer(tiargsBuf, tiargs);
            OutBuffer fargsBuf = new OutBuffer();
            try {
                fargsBuf.writeByte(40);
                argExpTypesToCBuffer(fargsBuf, fargs);
                fargsBuf.writeByte(41);
                if (tthis != null)
                    tthis.modToBuffer(fargsBuf);
                int numOverloadsDisplay = 5;
                if ((m.lastf == null) && (((flags & 0xFF) & 1) == 0))
                {
                    if ((fd == null) && (td == null) && (od == null))
                    {
                    }
                    else if ((td != null) && (fd == null))
                    {
                        error(loc, new BytePtr("%s `%s.%s` cannot deduce function from argument types `!(%s)%s`, candidates are:"), td.kind(), td.parent.toPrettyChars(false), td.ident.toChars(), tiargsBuf.peekChars(), fargsBuf.peekChars());
                        printCandidatesTemplateDeclaration(loc, td);
                    }
                    else if (od != null)
                    {
                        error(loc, new BytePtr("none of the overloads of `%s` are callable using argument types `!(%s)%s`"), od.ident.toChars(), tiargsBuf.peekChars(), fargsBuf.peekChars());
                    }
                    else
                    {
                        assert(fd != null);
                        if ((fd.isNewDeclaration() != null) && fd.checkDisabled(loc, sc, false))
                            return null;
                        boolean hasOverloads = fd.overnext != null;
                        TypeFunction tf = fd.type.toTypeFunction();
                        if ((tthis != null) && !MODimplicitConv(tthis.mod, tf.mod))
                        {
                            OutBuffer thisBuf = new OutBuffer();
                            try {
                                OutBuffer funcBuf = new OutBuffer();
                                try {
                                    MODMatchToBuffer(thisBuf, tthis.mod, tf.mod);
                                    Mismatches mismatches = MODMatchToBuffer(funcBuf, tf.mod, tthis.mod).copy();
                                    if (hasOverloads)
                                    {
                                        error(loc, new BytePtr("none of the overloads of `%s` are callable using a %sobject, candidates are:"), fd.ident.toChars(), thisBuf.peekChars());
                                    }
                                    else
                                    {
                                        Ref<BytePtr> failMessage = ref(null);
                                        functionResolve(m, orig_s, loc, sc, tiargs, tthis, fargs, ptr(failMessage));
                                        if (failMessage.value != null)
                                        {
                                            error(loc, new BytePtr("%s `%s%s%s` is not callable using argument types `%s`"), fd.kind(), fd.toPrettyChars(false), parametersTypeToChars(tf.parameterList), tf.modToChars(), fargsBuf.peekChars());
                                            errorSupplemental(loc, failMessage.value);
                                        }
                                        else
                                        {
                                            BytePtr fullFdPretty = pcopy(fd.toPrettyChars(false));
                                            error(loc, new BytePtr("%smethod `%s` is not callable using a %sobject"), funcBuf.peekChars(), fullFdPretty, thisBuf.peekChars());
                                            if (mismatches.isNotShared)
                                                errorSupplemental(loc, new BytePtr("Consider adding `shared` to %s"), fullFdPretty);
                                            else if (mismatches.isMutable)
                                                errorSupplemental(loc, new BytePtr("Consider adding `const` or `inout` to %s"), fullFdPretty);
                                        }
                                    }
                                }
                                finally {
                                }
                            }
                            finally {
                            }
                        }
                        else
                        {
                            if (hasOverloads)
                            {
                                error(loc, new BytePtr("none of the overloads of `%s` are callable using argument types `%s`, candidates are:"), fd.toChars(), fargsBuf.peekChars());
                            }
                            else
                            {
                                error(loc, new BytePtr("%s `%s%s%s` is not callable using argument types `%s`"), fd.kind(), fd.toPrettyChars(false), parametersTypeToChars(tf.parameterList), tf.modToChars(), fargsBuf.peekChars());
                                Ref<BytePtr> failMessage = ref(null);
                                functionResolve(m, orig_s, loc, sc, tiargs, tthis, fargs, ptr(failMessage));
                                if (failMessage.value != null)
                                    errorSupplemental(loc, failMessage.value);
                            }
                        }
                        if (hasOverloads)
                            printCandidatesFuncDeclaration(loc, fd);
                    }
                }
                else if (m.nextf != null)
                {
                    TypeFunction tf1 = m.lastf.type.toTypeFunction();
                    TypeFunction tf2 = m.nextf.type.toTypeFunction();
                    BytePtr lastprms = pcopy(parametersTypeToChars(tf1.parameterList));
                    BytePtr nextprms = pcopy(parametersTypeToChars(tf2.parameterList));
                    BytePtr mod1 = pcopy(prependSpace(MODtoChars(tf1.mod)));
                    BytePtr mod2 = pcopy(prependSpace(MODtoChars(tf2.mod)));
                    error(loc, new BytePtr("`%s.%s` called with argument types `%s` matches both:\n%s:     `%s%s%s`\nand:\n%s:     `%s%s%s`"), s.parent.toPrettyChars(false), s.ident.toChars(), fargsBuf.peekChars(), m.lastf.loc.toChars(global.params.showColumns), m.lastf.toPrettyChars(false), lastprms, mod1, m.nextf.loc.toChars(global.params.showColumns), m.nextf.toPrettyChars(false), nextprms, mod2);
                }
                return null;
            }
            finally {
            }
        }
        finally {
        }
    }

    // from template printCandidates!(FuncDeclaration)
    public static void printCandidatesFuncDeclaration(Loc loc, FuncDeclaration declaration) {
        int numToDisplay = 5;
        Function1<Dsymbol,Integer> __lambda3FuncDeclaration = new Function1<Dsymbol,Integer>(){
            public Integer invoke(Dsymbol s){
                Dsymbol nextOverload = null;
                {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        if (fd.errors || ((fd.type.ty & 0xFF) == ENUMTY.Terror))
                            return 0;
                        TypeFunction tf = (TypeFunction)fd.type;
                        errorSupplemental(fd.loc, new BytePtr("`%s%s`"), fd.toPrettyChars(false), parametersTypeToChars(tf.parameterList));
                        nextOverload = fd.overnext;
                    }
                    else {
                        TemplateDeclaration td = s.isTemplateDeclaration();
                        if ((td) != null)
                        {
                            errorSupplemental(td.loc, new BytePtr("`%s`"), td.toPrettyChars(false));
                            nextOverload = td.overnext;
                        }
                    }
                }
                if (global.params.verbose || ((numToDisplay -= 1) != 0))
                    return 0;
                int num = 0;
                Function1<Dsymbol,Integer> __lambda2FuncDeclaration = new Function1<Dsymbol,Integer>(){
                    public Integer invoke(Dsymbol s){
                        num += 1;
                        return 0;
                    }
                };
                overloadApply(nextOverload, __lambda2, null);
                if ((num > 0))
                    errorSupplemental(loc, new BytePtr("... (%d more, -v to show) ..."), num);
                return 1;
            }
        };
        overloadApply(declaration, __lambda3, null);
    }


    // from template printCandidates!(TemplateDeclaration)
    public static void printCandidatesTemplateDeclaration(Loc loc, TemplateDeclaration declaration) {
        int numToDisplay = 5;
        Function1<Dsymbol,Integer> __lambda3TemplateDeclaration = new Function1<Dsymbol,Integer>(){
            public Integer invoke(Dsymbol s){
                Dsymbol nextOverload = null;
                {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        if (fd.errors || ((fd.type.ty & 0xFF) == ENUMTY.Terror))
                            return 0;
                        TypeFunction tf = (TypeFunction)fd.type;
                        errorSupplemental(fd.loc, new BytePtr("`%s%s`"), fd.toPrettyChars(false), parametersTypeToChars(tf.parameterList));
                        nextOverload = fd.overnext;
                    }
                    else {
                        TemplateDeclaration td = s.isTemplateDeclaration();
                        if ((td) != null)
                        {
                            errorSupplemental(td.loc, new BytePtr("`%s`"), td.toPrettyChars(false));
                            nextOverload = td.overnext;
                        }
                    }
                }
                if (global.params.verbose || ((numToDisplay -= 1) != 0))
                    return 0;
                int num = 0;
                Function1<Dsymbol,Integer> __lambda2TemplateDeclaration = new Function1<Dsymbol,Integer>(){
                    public Integer invoke(Dsymbol s){
                        num += 1;
                        return 0;
                    }
                };
                overloadApply(nextOverload, __lambda2, null);
                if ((num > 0))
                    errorSupplemental(loc, new BytePtr("... (%d more, -v to show) ..."), num);
                return 1;
            }
        };
        overloadApply(declaration, __lambda3, null);
    }


    public static Type getIndirection(Type t) {
        t = t.baseElemOf();
        if (((t.ty & 0xFF) == ENUMTY.Tarray) || ((t.ty & 0xFF) == ENUMTY.Tpointer))
            return t.nextOf().toBasetype();
        if (((t.ty & 0xFF) == ENUMTY.Taarray) || ((t.ty & 0xFF) == ENUMTY.Tclass))
            return t;
        if (((t.ty & 0xFF) == ENUMTY.Tstruct))
            return t.hasPointers() ? t : null;
        return null;
    }

    public static boolean traverseIndirections(Type ta, Type tb) {
        Function4<Type,Type,Ctxt,Boolean,Boolean> traverse = new Function4<Type,Type,Ctxt,Boolean,Boolean>(){
            public Boolean invoke(Type ta, Type tb, Ctxt ctxt, Boolean reversePass){
                ta = ta.baseElemOf();
                tb = tb.baseElemOf();
                Function2<Type,Type,Boolean> mayAliasDirect = new Function2<Type,Type,Boolean>(){
                    public Boolean invoke(Type source, Type target){
                        return (source.constConv(target) != MATCH.nomatch) || ((target.ty & 0xFF) == ENUMTY.Tvoid) && MODimplicitConv(source.mod, target.mod);
                    }
                };
                if (mayAliasDirect.invoke(reversePass ? tb : ta, reversePass ? ta : tb))
                {
                    return false;
                }
                if ((ta.nextOf() != null) && (pequals(ta.nextOf(), tb.nextOf())))
                {
                    return true;
                }
                if (((tb.ty & 0xFF) == ENUMTY.Tclass) || ((tb.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    {
                        Ctxt c = ctxt;
                        for (; c != null;c = (c).prev) {
                            if ((pequals(tb, (c).type)))
                                return true;
                        }
                    }
                    Ctxt c = new Ctxt();
                    c.prev = ctxt;
                    c.type = tb;
                    AggregateDeclaration sym = tb.toDsymbol(null).isAggregateDeclaration();
                    {
                        Slice<VarDeclaration> __r1417 = sym.fields.opSlice().copy();
                        int __key1418 = 0;
                        for (; (__key1418 < __r1417.getLength());__key1418 += 1) {
                            VarDeclaration v = __r1417.get(__key1418);
                            Type tprmi = v.type.addMod(tb.mod);
                            if (!traverse.invoke(ta, tprmi, c, reversePass))
                                return false;
                        }
                    }
                }
                else if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Taarray) || ((tb.ty & 0xFF) == ENUMTY.Tpointer))
                {
                    Type tind = tb.nextOf();
                    if (!traverse.invoke(ta, tind, ctxt, reversePass))
                        return false;
                }
                else if (tb.hasPointers())
                {
                    return false;
                }
                if (!reversePass)
                    return traverse.invoke(tb, ta, ctxt, true);
                return true;
            }
        };
        boolean result = traverse.invoke(ta, tb, null, false);
        return result;
    }

    public static void markAsNeedingClosure(Dsymbol f, FuncDeclaration outerFunc) {
        {
            Dsymbol sx = f;
            for (; (sx != null) && (!pequals(sx, outerFunc));sx = toParentPFuncDeclaration(sx, outerFunc)){
                FuncDeclaration fy = sx.isFuncDeclaration();
                if ((fy != null) && (fy.closureVars.length != 0))
                {
                    fy.requiresClosure = true;
                }
            }
        }
    }

    public static boolean checkEscapingSiblings(FuncDeclaration f, FuncDeclaration outerFunc, Object p) {
        PrevSibling ps = new PrevSibling();
        ps.p = ((PrevSibling)p);
        ps.f = f;
        boolean bAnyClosures = false;
        {
            int i = 0;
            for (; (i < f.siblingCallers.length);i += 1){
                FuncDeclaration g = f.siblingCallers.get(i);
                if ((g.isThis() != null) || (g.tookAddressOf != 0))
                {
                    markAsNeedingClosure(g, outerFunc);
                    bAnyClosures = true;
                }
                {
                    Dsymbol parent = toParentPFuncDeclaration(g, outerFunc);
                    for (; (parent != null) && (parent != outerFunc);parent = toParentPFuncDeclaration(parent, outerFunc)){
                        FuncDeclaration parentFunc = parent.isFuncDeclaration();
                        if ((parentFunc != null) && (parentFunc.tookAddressOf != 0))
                        {
                            markAsNeedingClosure(parentFunc, outerFunc);
                            bAnyClosures = true;
                        }
                    }
                }
                PrevSibling prev = ((PrevSibling)p);
                for (; 1 != 0;){
                    if (prev == null)
                    {
                        (bAnyClosures ? 1 : 0) |= (checkEscapingSiblings(g, outerFunc, ps) ? 1 : 0);
                        break;
                    }
                    if ((pequals((prev).f, g)))
                        break;
                    prev = (prev).p;
                }
            }
        }
        return bAnyClosures;
    }

    // from template followInstantiationContext!(AggregateDeclaration)
    public static boolean followInstantiationContextAggregateDeclaration(Dsymbol s, AggregateDeclaration _param_1) {
        Function1<Dsymbol,Boolean> has2ThisAggregateDeclaration = new Function1<Dsymbol,Boolean>(){
            public Boolean invoke(Dsymbol s){
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if ((f) != null)
                        return f.isThis2;
                }
                {
                    AggregateDeclaration ad = s.isAggregateDeclaration();
                    if ((ad) != null)
                        return ad.vthis2 != null;
                }
                return false;
            }
        };
        assert(s != null);
        if (has2ThisAggregateDeclaration.invoke(s))
        {
            assert(1 != 0);
            Dsymbol parent = s.toParent();
            for (; parent != null;){
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti == null)
                    break;
                {
                    Slice<RootObject> __r1364 = (ti.tiargs).opSlice().copy();
                    int __key1365 = 0;
                    for (; (__key1365 < __r1364.getLength());__key1365 += 1) {
                        RootObject oarg = __r1364.get(__key1365);
                        Dsymbol sa = getDsymbol(oarg);
                        if (sa == null)
                            continue;
                        sa = sa.toAlias().toParent2();
                        if (sa == null)
                            continue;
                        {
                            AggregateDeclaration ps = _param_1;
                            if ((pequals(sa, ps)))
                                return true;
                        }
                    }
                }
                parent = ti.tempdecl.toParent();
            }
            return false;
        }
        return false;
    }


    // from template followInstantiationContext!(Dsymbol)
    public static boolean followInstantiationContextDsymbol(Dsymbol s, Dsymbol _param_1) {
        Function1<Dsymbol,Boolean> has2ThisDsymbol = new Function1<Dsymbol,Boolean>(){
            public Boolean invoke(Dsymbol s){
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if ((f) != null)
                        return f.isThis2;
                }
                {
                    AggregateDeclaration ad = s.isAggregateDeclaration();
                    if ((ad) != null)
                        return ad.vthis2 != null;
                }
                return false;
            }
        };
        assert(s != null);
        if (has2ThisDsymbol.invoke(s))
        {
            assert(1 != 0);
            Dsymbol parent = s.toParent();
            for (; parent != null;){
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti == null)
                    break;
                {
                    Slice<RootObject> __r921 = (ti.tiargs).opSlice().copy();
                    int __key922 = 0;
                    for (; (__key922 < __r921.getLength());__key922 += 1) {
                        RootObject oarg = __r921.get(__key922);
                        Dsymbol sa = getDsymbol(oarg);
                        if (sa == null)
                            continue;
                        sa = sa.toAlias().toParent2();
                        if (sa == null)
                            continue;
                        {
                            Dsymbol ps = _param_1;
                            if ((pequals(sa, ps)))
                                return true;
                        }
                    }
                }
                parent = ti.tempdecl.toParent();
            }
            return false;
        }
        return false;
    }


    // from template followInstantiationContext!(FuncDeclaration)
    public static boolean followInstantiationContextFuncDeclaration(Dsymbol s, FuncDeclaration _param_1) {
        Function1<Dsymbol,Boolean> has2ThisFuncDeclaration = new Function1<Dsymbol,Boolean>(){
            public Boolean invoke(Dsymbol s){
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if ((f) != null)
                        return f.isThis2;
                }
                {
                    AggregateDeclaration ad = s.isAggregateDeclaration();
                    if ((ad) != null)
                        return ad.vthis2 != null;
                }
                return false;
            }
        };
        assert(s != null);
        if (has2ThisFuncDeclaration.invoke(s))
        {
            assert(1 != 0);
            Dsymbol parent = s.toParent();
            for (; parent != null;){
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti == null)
                    break;
                {
                    Slice<RootObject> __r1391 = (ti.tiargs).opSlice().copy();
                    int __key1392 = 0;
                    for (; (__key1392 < __r1391.getLength());__key1392 += 1) {
                        RootObject oarg = __r1391.get(__key1392);
                        Dsymbol sa = getDsymbol(oarg);
                        if (sa == null)
                            continue;
                        sa = sa.toAlias().toParent2();
                        if (sa == null)
                            continue;
                        {
                            FuncDeclaration ps = _param_1;
                            if ((pequals(sa, ps)))
                                return true;
                        }
                    }
                }
                parent = ti.tempdecl.toParent();
            }
            return false;
        }
        return false;
    }


    // from template toParentP!(AggregateDeclaration)
    public static Dsymbol toParentPAggregateDeclaration(Dsymbol s, AggregateDeclaration _param_1) {
        return followInstantiationContextAggregateDeclaration(s, _param_1) ? s.toParent2() : s.toParentLocal();
    }


    // from template toParentP!(Dsymbol)
    public static Dsymbol toParentPDsymbol(Dsymbol s, Dsymbol _param_1) {
        return followInstantiationContextDsymbol(s, _param_1) ? s.toParent2() : s.toParentLocal();
    }


    // from template toParentP!(FuncDeclaration)
    public static Dsymbol toParentPFuncDeclaration(Dsymbol s, FuncDeclaration _param_1) {
        return followInstantiationContextFuncDeclaration(s, _param_1) ? s.toParent2() : s.toParentLocal();
    }


    public static class FuncAliasDeclaration extends FuncDeclaration
    {
        public FuncDeclaration funcalias;
        public boolean hasOverloads = false;
        public  FuncAliasDeclaration(Identifier ident, FuncDeclaration funcalias, boolean hasOverloads) {
            super(funcalias.loc, funcalias.endloc, ident, funcalias.storage_class, funcalias.type);
            assert((!pequals(funcalias, this)));
            this.funcalias = funcalias;
            this.hasOverloads = hasOverloads;
            if (hasOverloads)
            {
                {
                    FuncAliasDeclaration fad = funcalias.isFuncAliasDeclaration();
                    if ((fad) != null)
                        this.hasOverloads = fad.hasOverloads;
                }
            }
            else
            {
                assert(funcalias.isFuncAliasDeclaration() == null);
                this.hasOverloads = false;
            }
            this.userAttribDecl = funcalias.userAttribDecl;
        }

        public  FuncAliasDeclaration isFuncAliasDeclaration() {
            return this;
        }

        public  BytePtr kind() {
            return new BytePtr("function alias");
        }

        public  FuncDeclaration toAliasFunc() {
            return this.funcalias.toAliasFunc();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FuncAliasDeclaration() {}

        public FuncAliasDeclaration copy() {
            FuncAliasDeclaration that = new FuncAliasDeclaration();
            that.funcalias = this.funcalias;
            that.hasOverloads = this.hasOverloads;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class FuncLiteralDeclaration extends FuncDeclaration
    {
        public byte tok = 0;
        public Type treq;
        public boolean deferToObj = false;
        public  FuncLiteralDeclaration(Loc loc, Loc endloc, Type type, byte tok, ForeachStatement fes, Identifier id) {
            super(loc, endloc, null, 0L, type);
            this.ident = id != null ? id : Id.empty;
            this.tok = tok;
            this.fes = fes;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            FuncLiteralDeclaration f = new FuncLiteralDeclaration(this.loc, this.endloc, this.type.syntaxCopy(), this.tok, this.fes, this.ident);
            f.treq = this.treq;
            return this.syntaxCopy(f);
        }

        public  boolean isNested() {
            return ((this.tok & 0xFF) != 161) && (this.isThis() == null);
        }

        public  AggregateDeclaration isThis() {
            return ((this.tok & 0xFF) == 160) ? super.isThis() : null;
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  void modifyReturns(Scope sc, Type tret) {
            if ((this.semanticRun < PASS.semantic3done))
                return ;
            if (this.fes != null)
                return ;
            RetWalker w = new RetWalker();
            w.sc = sc;
            w.tret = tret;
            w.fld = this;
            this.fbody.accept(w);
            if (this.inferRetType && (!pequals(this.type.nextOf(), tret)))
                this.type.toTypeFunction().next = tret;
        }

        public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
            return this;
        }

        public  BytePtr kind() {
            return ((this.tok & 0xFF) != 161) ? new BytePtr("delegate") : new BytePtr("function");
        }

        public  BytePtr toPrettyChars(boolean QualifyTypes) {
            if (this.parent != null)
            {
                TemplateInstance ti = this.parent.isTemplateInstance();
                if (ti != null)
                    return ti.tempdecl.toPrettyChars(QualifyTypes);
            }
            return this.toPrettyChars(QualifyTypes);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FuncLiteralDeclaration() {}

        public FuncLiteralDeclaration copy() {
            FuncLiteralDeclaration that = new FuncLiteralDeclaration();
            that.tok = this.tok;
            that.treq = this.treq;
            that.deferToObj = this.deferToObj;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class CtorDeclaration extends FuncDeclaration
    {
        public boolean isCpCtor = false;
        public  CtorDeclaration(Loc loc, Loc endloc, long stc, Type type, boolean isCpCtor) {
            super(loc, endloc, Id.ctor, stc, type);
            this.isCpCtor = isCpCtor;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            CtorDeclaration f = new CtorDeclaration(this.loc, this.endloc, this.storage_class, this.type.syntaxCopy(), false);
            return this.syntaxCopy(f);
        }

        public  BytePtr kind() {
            return this.isCpCtor ? new BytePtr("copy constructor") : new BytePtr("constructor");
        }

        public  BytePtr toChars() {
            return new BytePtr("this");
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return (this.isThis() != null) && (this.vthis != null) && ((global.params.useInvariants & 0xFF) == 2);
        }

        public  CtorDeclaration isCtorDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CtorDeclaration() {}

        public CtorDeclaration copy() {
            CtorDeclaration that = new CtorDeclaration();
            that.isCpCtor = this.isCpCtor;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class PostBlitDeclaration extends FuncDeclaration
    {
        public  PostBlitDeclaration(Loc loc, Loc endloc, long stc, Identifier id) {
            super(loc, endloc, id, stc, null);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            PostBlitDeclaration dd = new PostBlitDeclaration(this.loc, this.endloc, this.storage_class, this.ident);
            return this.syntaxCopy(dd);
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return (this.isThis() != null) && (this.vthis != null) && ((global.params.useInvariants & 0xFF) == 2);
        }

        public  boolean overloadInsert(Dsymbol s) {
            return false;
        }

        public  PostBlitDeclaration isPostBlitDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PostBlitDeclaration() {}

        public PostBlitDeclaration copy() {
            PostBlitDeclaration that = new PostBlitDeclaration();
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
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

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            DtorDeclaration dd = new DtorDeclaration(this.loc, this.endloc, this.storage_class, this.ident);
            return this.syntaxCopy(dd);
        }

        public  BytePtr kind() {
            return new BytePtr("destructor");
        }

        public  BytePtr toChars() {
            return new BytePtr("~this");
        }

        public  boolean isVirtual() {
            return this.vtblIndex != -1;
        }

        public  boolean addPreInvariant() {
            return (this.isThis() != null) && (this.vthis != null) && ((global.params.useInvariants & 0xFF) == 2);
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  boolean overloadInsert(Dsymbol s) {
            return false;
        }

        public  DtorDeclaration isDtorDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DtorDeclaration() {}

        public DtorDeclaration copy() {
            DtorDeclaration that = new DtorDeclaration();
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class StaticCtorDeclaration extends FuncDeclaration
    {
        public  StaticCtorDeclaration(Loc loc, Loc endloc, long stc) {
            super(loc, endloc, Identifier.generateIdWithLoc(new ByteSlice("_staticCtor"), loc), 1L | stc, null);
        }

        public  StaticCtorDeclaration(Loc loc, Loc endloc, ByteSlice name, long stc) {
            super(loc, endloc, Identifier.generateIdWithLoc(name, loc), 1L | stc, null);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            StaticCtorDeclaration scd = new StaticCtorDeclaration(this.loc, this.endloc, this.storage_class);
            return this.syntaxCopy(scd);
        }

        public  AggregateDeclaration isThis() {
            return null;
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  boolean hasStaticCtorOrDtor() {
            return true;
        }

        public  StaticCtorDeclaration isStaticCtorDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticCtorDeclaration() {}

        public StaticCtorDeclaration copy() {
            StaticCtorDeclaration that = new StaticCtorDeclaration();
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class SharedStaticCtorDeclaration extends StaticCtorDeclaration
    {
        public  SharedStaticCtorDeclaration(Loc loc, Loc endloc, long stc) {
            super(loc, endloc, new ByteSlice("_sharedStaticCtor"), stc);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            SharedStaticCtorDeclaration scd = new SharedStaticCtorDeclaration(this.loc, this.endloc, this.storage_class);
            return this.syntaxCopy(scd);
        }

        public  SharedStaticCtorDeclaration isSharedStaticCtorDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SharedStaticCtorDeclaration() {}

        public SharedStaticCtorDeclaration copy() {
            SharedStaticCtorDeclaration that = new SharedStaticCtorDeclaration();
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class StaticDtorDeclaration extends FuncDeclaration
    {
        public VarDeclaration vgate;
        public  StaticDtorDeclaration(Loc loc, Loc endloc, long stc) {
            super(loc, endloc, Identifier.generateIdWithLoc(new ByteSlice("_staticDtor"), loc), 1L | stc, null);
        }

        public  StaticDtorDeclaration(Loc loc, Loc endloc, ByteSlice name, long stc) {
            super(loc, endloc, Identifier.generateIdWithLoc(name, loc), 1L | stc, null);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            StaticDtorDeclaration sdd = new StaticDtorDeclaration(this.loc, this.endloc, this.storage_class);
            return this.syntaxCopy(sdd);
        }

        public  AggregateDeclaration isThis() {
            return null;
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean hasStaticCtorOrDtor() {
            return true;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  StaticDtorDeclaration isStaticDtorDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticDtorDeclaration() {}

        public StaticDtorDeclaration copy() {
            StaticDtorDeclaration that = new StaticDtorDeclaration();
            that.vgate = this.vgate;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class SharedStaticDtorDeclaration extends StaticDtorDeclaration
    {
        public  SharedStaticDtorDeclaration(Loc loc, Loc endloc, long stc) {
            super(loc, endloc, new ByteSlice("_sharedStaticDtor"), stc);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            SharedStaticDtorDeclaration sdd = new SharedStaticDtorDeclaration(this.loc, this.endloc, this.storage_class);
            return this.syntaxCopy(sdd);
        }

        public  SharedStaticDtorDeclaration isSharedStaticDtorDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SharedStaticDtorDeclaration() {}

        public SharedStaticDtorDeclaration copy() {
            SharedStaticDtorDeclaration that = new SharedStaticDtorDeclaration();
            that.vgate = this.vgate;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class InvariantDeclaration extends FuncDeclaration
    {
        public  InvariantDeclaration(Loc loc, Loc endloc, long stc, Identifier id, Statement fbody) {
            super(loc, endloc, id != null ? id : Identifier.generateId(new BytePtr("__invariant")), stc, null);
            this.fbody = fbody;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            InvariantDeclaration id = new InvariantDeclaration(this.loc, this.endloc, this.storage_class, null, null);
            return this.syntaxCopy(id);
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  InvariantDeclaration isInvariantDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public InvariantDeclaration() {}

        public InvariantDeclaration copy() {
            InvariantDeclaration that = new InvariantDeclaration();
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class UnitTestDeclaration extends FuncDeclaration
    {
        public BytePtr codedoc;
        public DArray<FuncDeclaration> deferredNested = new DArray<FuncDeclaration>();
        public  UnitTestDeclaration(Loc loc, Loc endloc, long stc, BytePtr codedoc) {
            super(loc, endloc, Identifier.generateIdWithLoc(new ByteSlice("__unittest"), loc), stc, null);
            this.codedoc = pcopy(codedoc);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            UnitTestDeclaration utd = new UnitTestDeclaration(this.loc, this.endloc, this.storage_class, this.codedoc);
            return this.syntaxCopy(utd);
        }

        public  AggregateDeclaration isThis() {
            return null;
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  UnitTestDeclaration isUnitTestDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UnitTestDeclaration() {}

        public UnitTestDeclaration copy() {
            UnitTestDeclaration that = new UnitTestDeclaration();
            that.codedoc = this.codedoc;
            that.deferredNested = this.deferredNested;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class NewDeclaration extends FuncDeclaration
    {
        public DArray<Parameter> parameters;
        public int varargs = 0;
        public  NewDeclaration(Loc loc, Loc endloc, long stc, DArray<Parameter> fparams, int varargs) {
            super(loc, endloc, Id.classNew, 1L | stc, null);
            this.parameters = fparams;
            this.varargs = varargs;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            NewDeclaration f = new NewDeclaration(this.loc, this.endloc, this.storage_class, Parameter.arraySyntaxCopy(this.parameters), this.varargs);
            return this.syntaxCopy(f);
        }

        public  BytePtr kind() {
            return new BytePtr("allocator");
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  NewDeclaration isNewDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NewDeclaration() {}

        public NewDeclaration copy() {
            NewDeclaration that = new NewDeclaration();
            that.parameters = this.parameters;
            that.varargs = this.varargs;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
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

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            DeleteDeclaration f = new DeleteDeclaration(this.loc, this.endloc, this.storage_class, Parameter.arraySyntaxCopy(this.parameters));
            return this.syntaxCopy(f);
        }

        public  BytePtr kind() {
            return new BytePtr("deallocator");
        }

        public  boolean isDelete() {
            return true;
        }

        public  boolean isVirtual() {
            return false;
        }

        public  boolean addPreInvariant() {
            return false;
        }

        public  boolean addPostInvariant() {
            return false;
        }

        public  DeleteDeclaration isDeleteDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DeleteDeclaration() {}

        public DeleteDeclaration copy() {
            DeleteDeclaration that = new DeleteDeclaration();
            that.parameters = this.parameters;
            that.frequires = this.frequires;
            that.fensures = this.fensures;
            that.frequire = this.frequire;
            that.fensure = this.fensure;
            that.fbody = this.fbody;
            that.foverrides = this.foverrides;
            that.fdrequire = this.fdrequire;
            that.fdensure = this.fdensure;
            that.fdrequireParams = this.fdrequireParams;
            that.fdensureParams = this.fdensureParams;
            that.mangleString = this.mangleString;
            that.vresult = this.vresult;
            that.returnLabel = this.returnLabel;
            that.localsymtab = this.localsymtab;
            that.vthis = this.vthis;
            that.isThis2 = this.isThis2;
            that.v_arguments = this.v_arguments;
            that.selector = this.selector;
            that.selectorParameter = this.selectorParameter;
            that.v_argptr = this.v_argptr;
            that.parameters = this.parameters;
            that.labtab = this.labtab;
            that.overnext = this.overnext;
            that.overnext0 = this.overnext0;
            that.endloc = this.endloc;
            that.vtblIndex = this.vtblIndex;
            that.naked = this.naked;
            that.generated = this.generated;
            that.isCrtCtorDtor = this.isCrtCtorDtor;
            that.inlineStatusStmt = this.inlineStatusStmt;
            that.inlineStatusExp = this.inlineStatusExp;
            that.inlining = this.inlining;
            that.ctfeCode = this.ctfeCode;
            that.inlineNest = this.inlineNest;
            that.isArrayOp = this.isArrayOp;
            that.eh_none = this.eh_none;
            that.semantic3Errors = this.semantic3Errors;
            that.fes = this.fes;
            that.interfaceVirtual = this.interfaceVirtual;
            that.introducing = this.introducing;
            that.tintro = this.tintro;
            that.inferRetType = this.inferRetType;
            that.storage_class2 = this.storage_class2;
            that.hasReturnExp = this.hasReturnExp;
            that.nrvo_can = this.nrvo_can;
            that.nrvo_var = this.nrvo_var;
            that.shidden = this.shidden;
            that.returns = this.returns;
            that.gotos = this.gotos;
            that.builtin = this.builtin;
            that.tookAddressOf = this.tookAddressOf;
            that.requiresClosure = this.requiresClosure;
            that.closureVars = this.closureVars;
            that.siblingCallers = this.siblingCallers;
            that.inlinedNestedCallees = this.inlinedNestedCallees;
            that.flags = this.flags;
            that.LevelError = this.LevelError;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
}
