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
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.blockexit.*;
import static org.dlang.dmd.clone.*;
import static org.dlang.dmd.compiler.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.dversion.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nogc.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.statementsem.*;
import static org.dlang.dmd.staticassert.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.templateparamsem.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.visitor.*;

public class dsymbolsem {
    static int visitnest;
    static boolean funcDeclarationSemanticprintedMain = false;

    static boolean LOG = false;
    public static FuncDeclaration buildPostBlit(StructDeclaration sd, Scope sc) {
        Ref<StructDeclaration> sd_ref = ref(sd);
        if (sd_ref.value.isUnionDeclaration() != null)
            return null;
        Ref<Long> stc = ref(4406737108992L);
        Loc declLoc = (sd_ref.value.postblits.length) != 0 ? sd_ref.value.postblits.get(0).loc : sd_ref.value.loc.copy();
        Loc loc = new Loc();
        {
            int i = 0;
            for (; i < sd_ref.value.postblits.length;i++){
                stc.value |= sd_ref.value.postblits.get(i).storage_class & 137438953472L;
            }
        }
        Slice<VarDeclaration> fieldsToDestroy = new Slice<VarDeclaration>();
        DArray<Statement> postblitCalls = new DArray<Statement>();
        {
            int i = 0;
            for (; (i < sd_ref.value.fields.length && !((stc.value & 137438953472L) != 0));i++){
                VarDeclaration structField = sd_ref.value.fields.get(i);
                if ((structField.storage_class & 2097152L) != 0)
                    continue;
                if (structField.overlapped)
                    continue;
                Type tv = structField.type.baseElemOf();
                if ((tv.ty & 0xFF) != ENUMTY.Tstruct)
                    continue;
                StructDeclaration sdv = ((TypeStruct)tv).sym;
                if (!(sdv.postblit != null))
                    continue;
                assert(!(sdv.isUnionDeclaration() != null));
                if ((fieldsToDestroy.getLength() > 0 && !(((TypeFunction)sdv.postblit.type).isnothrow)))
                {
                    Slice<Expression> dtorCalls = new Slice<Expression>();
                    {
                        Slice<VarDeclaration> __r1138 = fieldsToDestroy.copy();
                        int __key1139 = 0;
                        for (; __key1139 < __r1138.getLength();__key1139 += 1) {
                            VarDeclaration sf = __r1138.get(__key1139);
                            Expression ex = null;
                            tv = sf.type.toBasetype();
                            if ((tv.ty & 0xFF) == ENUMTY.Tstruct)
                            {
                                ex = new ThisExp(loc);
                                ex = new DotVarExp(loc, ex, sf, true);
                                ex = new AddrExp(loc, ex);
                                ex = new CastExp(loc, ex, sf.type.mutableOf().pointerTo());
                                ex = new PtrExp(loc, ex);
                                if ((stc.value & 8589934592L) != 0)
                                    stc.value = stc.value & -8589934593L | 17179869184L;
                                StructDeclaration sfv = ((TypeStruct)sf.type.baseElemOf()).sym;
                                ex = new DotVarExp(loc, ex, sfv.dtor, false);
                                ex = new CallExp(loc, ex);
                                dtorCalls.append(ex);
                            }
                            else
                            {
                                int length = tv.numberOfElems(loc);
                                ex = new ThisExp(loc);
                                ex = new DotVarExp(loc, ex, sf, true);
                                ex = new DotIdExp(loc, ex, Id.ptr);
                                ex = new CastExp(loc, ex, sdv.type.pointerTo());
                                if ((stc.value & 8589934592L) != 0)
                                    stc.value = stc.value & -8589934593L | 17179869184L;
                                SliceExp se = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t), new IntegerExp(loc, (long)length, Type.tsize_t));
                                se.upperIsInBounds = true;
                                se.lowerIsLessThanUpper = true;
                                ex = new CallExp(loc, new IdentifierExp(loc, Id.__ArrayDtor), se);
                                dtorCalls.append(ex);
                            }
                        }
                    }
                    fieldsToDestroy = slice(new VarDeclaration[]{}).copy();
                    DArray<Statement> dtors = new DArray<Statement>();
                    {
                        Slice<Expression> __r1140 = dtorCalls.copy();
                        int __key1141 = __r1140.getLength();
                        for (; (__key1141--) != 0;) {
                            Expression dc = __r1140.get(__key1141);
                            (dtors).push(new ExpStatement(loc, dc));
                        }
                    }
                    (postblitCalls).push(new ScopeGuardStatement(loc, TOK.onScopeFailure, new CompoundStatement(loc, dtors)));
                }
                sdv.postblit.functionSemantic();
                stc.value = mergeFuncAttrs(stc.value, sdv.postblit);
                stc.value = mergeFuncAttrs(stc.value, sdv.dtor);
                if ((stc.value & 137438953472L) != 0)
                {
                    (postblitCalls).setDim(0);
                    break;
                }
                Expression ex = null;
                tv = structField.type.toBasetype();
                if ((tv.ty & 0xFF) == ENUMTY.Tstruct)
                {
                    ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, structField, true);
                    ex = new AddrExp(loc, ex);
                    ex = new CastExp(loc, ex, structField.type.mutableOf().pointerTo());
                    ex = new PtrExp(loc, ex);
                    if ((stc.value & 8589934592L) != 0)
                        stc.value = stc.value & -8589934593L | 17179869184L;
                    ex = new DotVarExp(loc, ex, sdv.postblit, false);
                    ex = new CallExp(loc, ex);
                }
                else
                {
                    int length = tv.numberOfElems(loc);
                    if (length == 0)
                        continue;
                    ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, structField, true);
                    ex = new DotIdExp(loc, ex, Id.ptr);
                    ex = new CastExp(loc, ex, sdv.type.pointerTo());
                    if ((stc.value & 8589934592L) != 0)
                        stc.value = stc.value & -8589934593L | 17179869184L;
                    SliceExp se = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t), new IntegerExp(loc, (long)length, Type.tsize_t));
                    se.upperIsInBounds = true;
                    se.lowerIsLessThanUpper = true;
                    ex = new CallExp(loc, new IdentifierExp(loc, Id.__ArrayPostblit), se);
                }
                (postblitCalls).push(new ExpStatement(loc, ex));
                if (sdv.dtor != null)
                {
                    sdv.dtor.functionSemantic();
                    fieldsToDestroy.append(structField);
                }
            }
        }
        Function0<Void> checkShared = new Function0<Void>(){
            public Void invoke(){
                if (sd_ref.value.type.isShared())
                    stc.value |= 536870912L;
            }
        };
        if ((((postblitCalls).length) != 0 || (stc.value & 137438953472L) != 0))
        {
            checkShared.invoke();
            PostBlitDeclaration dd = new PostBlitDeclaration(declLoc, Loc.initial, stc.value, Id.__fieldPostblit);
            dd.generated = true;
            dd.storage_class |= 70368744177664L;
            dd.fbody = (stc.value & 137438953472L) != 0 ? null : new CompoundStatement(loc, postblitCalls);
            sd_ref.value.postblits.shift(dd);
            (sd_ref.value.members).push(dd);
            dsymbolSemantic(dd, sc);
        }
        FuncDeclaration xpostblit = null;
        switch (sd_ref.value.postblits.length)
        {
            case 0:
                break;
            case 1:
                xpostblit = sd_ref.value.postblits.get(0);
                break;
            default:
            Expression e = null;
            stc.value = 4406737108992L;
            {
                int i = 0;
                for (; i < sd_ref.value.postblits.length;i++){
                    FuncDeclaration fd = sd_ref.value.postblits.get(i);
                    stc.value = mergeFuncAttrs(stc.value, fd);
                    if ((stc.value & 137438953472L) != 0)
                    {
                        e = null;
                        break;
                    }
                    Expression ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, fd, false);
                    ex = new CallExp(loc, ex);
                    e = Expression.combine(e, ex);
                }
            }
            checkShared.invoke();
            PostBlitDeclaration dd = new PostBlitDeclaration(declLoc, Loc.initial, stc.value, Id.__aggrPostblit);
            dd.generated = true;
            dd.storage_class |= 70368744177664L;
            dd.fbody = new ExpStatement(loc, e);
            (sd_ref.value.members).push(dd);
            dsymbolSemantic(dd, sc);
            xpostblit = dd;
            break;
        }
        if (xpostblit != null)
        {
            AliasDeclaration _alias = new AliasDeclaration(Loc.initial, Id.__xpostblit, xpostblit);
            dsymbolSemantic(_alias, sc);
            (sd_ref.value.members).push(_alias);
            _alias.addMember(sc, sd_ref.value);
        }
        return xpostblit;
    }

    public static CtorDeclaration generateCopyCtorDeclaration(StructDeclaration sd, long paramStc, long funcStc) {
        DArray<Parameter> fparams = new DArray<Parameter>();
        Type structType = sd.type;
        (fparams).push(new Parameter(paramStc | 2097152L | 17592186044416L | 524288L, structType, Id.p, null, null));
        ParameterList pList = new ParameterList(fparams, VarArg.none).copy();
        TypeFunction tf = new TypeFunction(pList, structType, LINK.d, 2097152L);
        CtorDeclaration ccd = new CtorDeclaration(sd.loc, Loc.initial, 2097152L, tf, true);
        ccd.storage_class |= funcStc;
        ccd.storage_class |= 70368744177664L;
        ccd.generated = true;
        return ccd;
    }

    public static Statement generateCopyCtorBody(StructDeclaration sd) {
        Loc loc = new Loc();
        Expression e = null;
        {
            Slice<VarDeclaration> __r1142 = sd.fields.opSlice().copy();
            int __key1143 = 0;
            for (; __key1143 < __r1142.getLength();__key1143 += 1) {
                VarDeclaration v = __r1142.get(__key1143);
                AssignExp ec = new AssignExp(loc, new DotVarExp(loc, new ThisExp(loc), v, true), new DotVarExp(loc, new IdentifierExp(loc, Id.p), v, true));
                e = Expression.combine(e, (Expression)ec);
            }
        }
        Statement s1 = new ExpStatement(loc, e);
        return new CompoundStatement(loc, slice(new Statement[]{s1}));
    }

    public static boolean buildCopyCtor(StructDeclaration sd, Scope sc) {
        if ((global.errors) != 0)
            return false;
        boolean hasPostblit = false;
        if (sd.postblit != null)
            hasPostblit = true;
        Dsymbol ctor = sd.search(sd.loc, Id.ctor, 8);
        CtorDeclaration cpCtor = null;
        CtorDeclaration rvalueCtor = null;
        if (ctor != null)
        {
            if (ctor.isOverloadSet() != null)
                return false;
            {
                TemplateDeclaration td = ctor.isTemplateDeclaration();
                if (td != null)
                    ctor = td.funcroot;
            }
        }
        try {
            if (!(ctor != null))
                /*goto LcheckFields*/throw Dispatch0.INSTANCE;
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    if (s.isTemplateDeclaration() != null)
                        return 0;
                    CtorDeclaration ctorDecl = s.isCtorDeclaration();
                    assert(ctorDecl != null);
                    if (ctorDecl.isCpCtor)
                    {
                        if (!(cpCtor != null))
                            cpCtor = ctorDecl;
                        return 0;
                    }
                    TypeFunction tf = ctorDecl.type.toTypeFunction();
                    int dim = Parameter.dim(tf.parameterList.parameters);
                    if (dim == 1)
                    {
                        Parameter param = Parameter.getNth(tf.parameterList.parameters, 0, null);
                        if (pequals(param.type.mutableOf().unSharedOf(), sd.type.mutableOf().unSharedOf()))
                        {
                            rvalueCtor = ctorDecl;
                        }
                    }
                    return 0;
                }
            };
            overloadApply(ctor, __lambda3, null);
            if ((cpCtor != null && rvalueCtor != null))
            {
                error(sd.loc, new BytePtr("`struct %s` may not define both a rvalue constructor and a copy constructor"), sd.toChars());
                errorSupplemental(rvalueCtor.loc, new BytePtr("rvalue constructor defined here"));
                errorSupplemental(cpCtor.loc, new BytePtr("copy constructor defined here"));
                return true;
            }
            else if (cpCtor != null)
            {
                return !(hasPostblit);
            }
        }
        catch(Dispatch0 __d){}
    /*LcheckFields:*/
        VarDeclaration fieldWithCpCtor = null;
        {
            Slice<VarDeclaration> __r1144 = sd.fields.opSlice().copy();
            int __key1145 = 0;
            for (; __key1145 < __r1144.getLength();__key1145 += 1) {
                VarDeclaration v = __r1144.get(__key1145);
                if ((v.storage_class & 2097152L) != 0)
                    continue;
                if (v.overlapped)
                    continue;
                TypeStruct ts = v.type.baseElemOf().isTypeStruct();
                if (!(ts != null))
                    continue;
                if (ts.sym.hasCopyCtor)
                {
                    fieldWithCpCtor = v;
                    break;
                }
            }
        }
        if ((fieldWithCpCtor != null && rvalueCtor != null))
        {
            error(sd.loc, new BytePtr("`struct %s` may not define a rvalue constructor and have fields with copy constructors"), sd.toChars());
            errorSupplemental(rvalueCtor.loc, new BytePtr("rvalue constructor defined here"));
            errorSupplemental(fieldWithCpCtor.loc, new BytePtr("field with copy constructor defined here"));
            return false;
        }
        else if (!(fieldWithCpCtor != null))
            return false;
        if (hasPostblit)
            return false;
        byte paramMod = (byte)8;
        byte funcMod = (byte)8;
        CtorDeclaration ccd = generateCopyCtorDeclaration(sd, ModToStc(8), ModToStc(8));
        Statement copyCtorBody = generateCopyCtorBody(sd);
        ccd.fbody = copyCtorBody;
        (sd.members).push(ccd);
        ccd.addMember(sc, sd);
        int errors = global.startGagging();
        Scope sc2 = (sc).push();
        (sc2).stc = 0L;
        (sc2).linkage = LINK.d;
        dsymbolSemantic(ccd, sc2);
        semantic2(ccd, sc2);
        semantic3(ccd, sc2);
        (sc2).pop();
        if (global.endGagging(errors))
        {
            ccd.storage_class |= 137438953472L;
            ccd.fbody = null;
        }
        return true;
    }

    public static int setMangleOverride(Dsymbol s, ByteSlice sym) {
        if ((s.isFuncDeclaration() != null || s.isVarDeclaration() != null))
        {
            s.isDeclaration().mangleOverride = sym.copy();
            return 1;
        }
        {
            AttribDeclaration ad = s.isAttribDeclaration();
            if (ad != null)
            {
                int nestedCount = 0;
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        nestedCount += setMangleOverride(s, sym);
                    }
                };
                foreachDsymbol(ad.include(null), __lambda3);
                return nestedCount;
            }
        }
        return 0;
    }

    public static void dsymbolSemantic(Dsymbol dsym, Scope sc) {
        DsymbolSemanticVisitor v = new DsymbolSemanticVisitor(sc);
        dsym.accept(v);
    }

    public static int getAlignment(AlignDeclaration ad, Scope sc) {
        if (ad.salign != 0)
            return ad.salign;
        if (!(ad.ealign != null))
            return ad.salign = -1;
        sc = (sc).startCTFE();
        ad.ealign = expressionSemantic(ad.ealign, sc);
        ad.ealign = resolveProperties(sc, ad.ealign);
        sc = (sc).endCTFE();
        ad.ealign = ad.ealign.ctfeInterpret();
        if ((ad.ealign.op & 0xFF) == 127)
            return ad.salign = -1;
        Type tb = ad.ealign.type.toBasetype();
        long n = ad.ealign.toInteger();
        if ((((n < 1L || (n & n - 1L) != 0) || 4294967295L < n) || !(tb.isintegral())))
        {
            error(ad.loc, new BytePtr("alignment must be an integer positive power of 2, not %s"), ad.ealign.toChars());
            return ad.salign = -1;
        }
        return ad.salign = (int)n;
    }

    public static BytePtr getMessage(DeprecatedDeclaration dd) {
        {
            Scope sc = dd._scope;
            if (sc != null)
            {
                dd._scope = null;
                sc = (sc).startCTFE();
                dd.msg = expressionSemantic(dd.msg, sc);
                dd.msg = resolveProperties(sc, dd.msg);
                sc = (sc).endCTFE();
                dd.msg = dd.msg.ctfeInterpret();
                {
                    StringExp se = dd.msg.toStringExp();
                    if (se != null)
                        dd.msgstr = pcopy((toBytePtr(se.toStringz())));
                    else
                        dd.msg.error(new BytePtr("compile time constant expected, not `%s`"), dd.msg.toChars());
                }
            }
        }
        return dd.msgstr;
    }

    public static boolean allowsContractWithoutBody(FuncDeclaration funcdecl) {
        assert(!(funcdecl.fbody != null));
        Dsymbol parent = funcdecl.toParent();
        InterfaceDeclaration id = parent.isInterfaceDeclaration();
        if (((!(funcdecl.isAbstract()) && (funcdecl.fensures != null || funcdecl.frequires != null)) && !((id != null && funcdecl.isVirtual()))))
        {
            ClassDeclaration cd = parent.isClassDeclaration();
            if (!((cd != null && cd.isAbstract())))
                return false;
        }
        return true;
    }

    public static class DsymbolSemanticVisitor extends Visitor
    {
        public Scope sc;
        public  DsymbolSemanticVisitor(Scope sc) {
            this.sc = sc;
        }

        public  void visit(Dsymbol dsym) {
            dsym.error(new BytePtr("%p has no semantic routine"), dsym);
        }

        public  void visit(ScopeDsymbol _param_0) {
        }

        public  void visit(Declaration _param_0) {
        }

        public  void visit(AliasThis dsym) {
            if (dsym.semanticRun != PASS.init)
                return ;
            if (dsym._scope != null)
            {
                this.sc = dsym._scope;
                dsym._scope = null;
            }
            if (this.sc == null)
                return ;
            dsym.semanticRun = PASS.semantic;
            Dsymbol p = (this.sc).parent.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (!(ad != null))
            {
                error(dsym.loc, new BytePtr("alias this can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                return ;
            }
            assert(ad.members != null);
            Dsymbol s = ad.search(dsym.loc, dsym.ident, 8);
            if (!(s != null))
            {
                s = (this.sc).search(dsym.loc, dsym.ident, null, 0);
                if (s != null)
                    error(dsym.loc, new BytePtr("`%s` is not a member of `%s`"), s.toChars(), ad.toChars());
                else
                    error(dsym.loc, new BytePtr("undefined identifier `%s`"), dsym.ident.toChars());
                return ;
            }
            if ((ad.aliasthis != null && !pequals(s, ad.aliasthis)))
            {
                error(dsym.loc, new BytePtr("there can be only one alias this"));
                return ;
            }
            ad.aliasthis = null;
            Dsymbol sx = s;
            if (sx.isAliasDeclaration() != null)
                sx = sx.toAlias();
            Declaration d = sx.isDeclaration();
            if ((d != null && !(d.isTupleDeclaration() != null)))
            {
                if (!(d.type != null))
                    dsymbolSemantic(d, this.sc);
                Type t = d.type;
                assert(t != null);
                if (ad.type.implicitConvTo(t) > MATCH.nomatch)
                {
                    error(dsym.loc, new BytePtr("alias this is not reachable as `%s` already converts to `%s`"), ad.toChars(), t.toChars());
                }
            }
            ad.aliasthis = s;
            dsym.semanticRun = PASS.semanticdone;
        }

        public  void visit(AliasDeclaration dsym) {
            if (dsym.semanticRun >= PASS.semanticdone)
                return ;
            assert(dsym.semanticRun <= PASS.semantic);
            dsym.storage_class |= (this.sc).stc & 1024L;
            dsym.protection = (this.sc).protection.copy();
            dsym.userAttribDecl = (this.sc).userAttribDecl;
            if ((!((this.sc).func != null) && dsym.inNonRoot()))
                return ;
            aliasSemantic(dsym, this.sc);
        }

        public  void visit(VarDeclaration dsym) {
            if (dsym.semanticRun >= PASS.semanticdone)
                return ;
            if (((this.sc != null && (this.sc).inunion != null) && (this.sc).inunion.isAnonDeclaration() != null))
                dsym.overlapped = true;
            Scope scx = null;
            if (dsym._scope != null)
            {
                this.sc = dsym._scope;
                scx = this.sc;
                dsym._scope = null;
            }
            if (this.sc == null)
                return ;
            dsym.semanticRun = PASS.semantic;
            dsym.storage_class |= (this.sc).stc & -665L;
            if (((dsym.storage_class & 2L) != 0 && dsym._init != null))
                dsym.error(new BytePtr("extern symbols cannot have initializers"));
            dsym.userAttribDecl = (this.sc).userAttribDecl;
            dsym.namespace = (this.sc).namespace;
            AggregateDeclaration ad = dsym.isThis();
            if (ad != null)
                dsym.storage_class |= ad.storage_class & 2685403140L;
            int inferred = 0;
            if (!(dsym.type != null))
            {
                dsym.inuse++;
                boolean needctfe = (dsym.storage_class & 8388609L) != 0L;
                if (needctfe)
                    this.sc = (this.sc).startCTFE();
                dsym._init = inferType(dsym._init, this.sc);
                dsym.type = initializerToExpression(dsym._init, null).type;
                if (needctfe)
                    this.sc = (this.sc).endCTFE();
                dsym.inuse--;
                inferred = 1;
                dsym.storage_class &= -257L;
                dsym.originalType = dsym.type.syntaxCopy();
            }
            else
            {
                if (!(dsym.originalType != null))
                    dsym.originalType = dsym.type.syntaxCopy();
                Scope sc2 = (this.sc).push();
                (sc2).stc |= dsym.storage_class & 4462573780992L;
                dsym.inuse++;
                dsym.type = typeSemantic(dsym.type, dsym.loc, sc2);
                dsym.inuse--;
                (sc2).pop();
            }
            if ((dsym.type.ty & 0xFF) == ENUMTY.Terror)
                dsym.errors = true;
            dsym.type.checkDeprecated(dsym.loc, this.sc);
            dsym.linkage = (this.sc).linkage;
            dsym.parent = (this.sc).parent;
            dsym.protection = (this.sc).protection.copy();
            dsym.alignment = (this.sc).alignment();
            if (dsym.alignment == -1)
                dsym.alignment = dsym.type.alignment();
            if (global.params.vcomplex)
                dsym.type.checkComplexTransition(dsym.loc, this.sc);
            if (((this.sc).func != null && !(((this.sc).intypeof) != 0)))
            {
                if (((dsym.storage_class & 1073741824L) != 0 && !(dsym.isMember() != null)))
                {
                    if ((this.sc).func.setUnsafe())
                        dsym.error(new BytePtr("__gshared not allowed in safe functions; use shared"));
                }
            }
            Dsymbol parent = dsym.toParent();
            Type tb = dsym.type.toBasetype();
            Type tbn = tb.baseElemOf();
            if (((tb.ty & 0xFF) == ENUMTY.Tvoid && !((dsym.storage_class & 8192L) != 0)))
            {
                if ((inferred) != 0)
                {
                    dsym.error(new BytePtr("type `%s` is inferred from initializer `%s`, and variables cannot be of type `void`"), dsym.type.toChars(), dsym._init.toChars());
                }
                else
                    dsym.error(new BytePtr("variables cannot be of type `void`"));
                dsym.type = Type.terror;
                tb = dsym.type;
            }
            if ((tb.ty & 0xFF) == ENUMTY.Tfunction)
            {
                dsym.error(new BytePtr("cannot be declared to be a function"));
                dsym.type = Type.terror;
                tb = dsym.type;
            }
            {
                TypeStruct ts = tb.isTypeStruct();
                if (ts != null)
                {
                    if (ts.sym.members == null)
                    {
                        dsym.error(new BytePtr("no definition of struct `%s`"), ts.toChars());
                    }
                }
            }
            if (((dsym.storage_class & 256L) != 0 && !((inferred) != 0)))
                dsym.error(new BytePtr("storage class `auto` has no effect if type is not inferred, did you mean `scope`?"));
            {
                TypeTuple tt = tb.isTypeTuple();
                if (tt != null)
                {
                    int nelems = Parameter.dim(tt.arguments);
                    Expression ie = (dsym._init != null && !(dsym._init.isVoidInitializer() != null)) ? initializerToExpression(dsym._init, null) : null;
                    if (ie != null)
                        ie = expressionSemantic(ie, this.sc);
                    try {
                        if ((nelems > 0 && ie != null))
                        {
                            DArray<Expression> iexps = new DArray<Expression>();
                            (iexps).push(ie);
                            DArray<Expression> exps = new DArray<Expression>();
                            {
                                int pos = 0;
                            L_outer1:
                                for (; pos < (iexps).length;pos++){
                                    while(true) try {
                                    /*Lexpand1:*/
                                        Expression e = (iexps).get(pos);
                                        Parameter arg = Parameter.getNth(tt.arguments, pos, null);
                                        arg.type = typeSemantic(arg.type, dsym.loc, this.sc);
                                        if (!pequals(e, ie))
                                        {
                                            if ((iexps).length > nelems)
                                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                            if ((e.type.implicitConvTo(arg.type)) != 0)
                                                continue L_outer1;
                                        }
                                        if ((e.op & 0xFF) == 126)
                                        {
                                            TupleExp te = (TupleExp)e;
                                            if ((iexps).length - 1 + (te.exps).length > nelems)
                                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                            (iexps).remove(pos);
                                            (iexps).insert(pos, te.exps);
                                            iexps.set(pos, Expression.combine(te.e0, (iexps).get(pos)));
                                            /*goto Lexpand1*/throw Dispatch0.INSTANCE;
                                        }
                                        else if (isAliasThisTuple(e) != null)
                                        {
                                            VarDeclaration v = copyToTemp(0L, new BytePtr("__tup"), e);
                                            dsymbolSemantic(v, this.sc);
                                            VarExp ve = new VarExp(dsym.loc, v, true);
                                            ve.type = e.type;
                                            (exps).setDim(1);
                                            exps.set(0, ve);
                                            expandAliasThisTuples(exps, 0);
                                            {
                                                int u = 0;
                                            L_outer2:
                                                for (; u < (exps).length;u++){
                                                    while(true) try {
                                                    /*Lexpand2:*/
                                                        Expression ee = (exps).get(u);
                                                        arg = Parameter.getNth(tt.arguments, pos + u, null);
                                                        arg.type = typeSemantic(arg.type, dsym.loc, this.sc);
                                                        int iexps_dim = (iexps).length - 1 + (exps).length;
                                                        if (iexps_dim > nelems)
                                                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                                        if ((ee.type.implicitConvTo(arg.type)) != 0)
                                                            continue L_outer2;
                                                        if (expandAliasThisTuples(exps, u) != -1)
                                                            /*goto Lexpand2*/throw Dispatch0.INSTANCE;
                                                        break;
                                                    } catch(Dispatch0 __d){}
                                                }
                                            }
                                            if (!pequals((exps).get(0), ve))
                                            {
                                                Expression e0 = (exps).get(0);
                                                exps.set(0, new CommaExp(dsym.loc, new DeclarationExp(dsym.loc, v), e0, true));
                                                (exps).get(0).type = e0.type;
                                                (iexps).remove(pos);
                                                (iexps).insert(pos, exps);
                                                /*goto Lexpand1*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        break;
                                    } catch(Dispatch0 __d){}
                                }
                            }
                            if ((iexps).length < nelems)
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            ie = new TupleExp(dsym._init.loc, iexps);
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lnomatch:*/
                    if ((ie != null && (ie.op & 0xFF) == 126))
                    {
                        TupleExp te = (TupleExp)ie;
                        int tedim = (te.exps).length;
                        if (tedim != nelems)
                        {
                            error(dsym.loc, new BytePtr("tuple of %d elements cannot be assigned to tuple of %d elements"), tedim, nelems);
                            {
                                int u = tedim;
                                for (; u < nelems;u++) {
                                    (te.exps).push(new ErrorExp());
                                }
                            }
                        }
                    }
                    DArray<RootObject> exps = new DArray<RootObject>(nelems);
                    {
                        int i = 0;
                        for (; i < nelems;i++){
                            Parameter arg = Parameter.getNth(tt.arguments, i, null);
                            OutBuffer buf = new OutBuffer();
                            try {
                                buf.printf(new BytePtr("__%s_field_%llu"), dsym.ident.toChars(), (long)i);
                                Identifier id = Identifier.idPool(buf.peekSlice());
                                Initializer ti = null;
                                if (ie != null)
                                {
                                    Expression einit = ie;
                                    if ((ie.op & 0xFF) == 126)
                                    {
                                        TupleExp te = (TupleExp)ie;
                                        einit = (te.exps).get(i);
                                        if (i == 0)
                                            einit = Expression.combine(te.e0, einit);
                                    }
                                    ti = new ExpInitializer(einit.loc, einit);
                                }
                                else
                                    ti = dsym._init != null ? syntaxCopy(dsym._init) : null;
                                long storage_class = 1099511627776L | dsym.storage_class;
                                if ((arg.storageClass & 32L) != 0)
                                    storage_class |= arg.storageClass;
                                VarDeclaration v = new VarDeclaration(dsym.loc, arg.type, id, ti, storage_class);
                                dsymbolSemantic(v, this.sc);
                                if ((this.sc).scopesym != null)
                                {
                                    if ((this.sc).scopesym.members != null)
                                        ((this.sc).scopesym.members).push(v);
                                }
                                Expression e = new DsymbolExp(dsym.loc, v, true);
                                exps.set(i, e);
                            }
                            finally {
                            }
                        }
                    }
                    TupleDeclaration v2 = new TupleDeclaration(dsym.loc, dsym.ident, exps);
                    v2.parent = dsym.parent;
                    v2.isexp = true;
                    dsym.aliassym = v2;
                    dsym.semanticRun = PASS.semanticdone;
                    return ;
                }
            }
            dsym.type = dsym.type.addStorageClass(dsym.storage_class);
            if (dsym.type.isConst())
            {
                dsym.storage_class |= 4L;
                if (dsym.type.isShared())
                    dsym.storage_class |= 536870912L;
            }
            else if (dsym.type.isImmutable())
                dsym.storage_class |= 1048576L;
            else if (dsym.type.isShared())
                dsym.storage_class |= 536870912L;
            else if (dsym.type.isWild())
                dsym.storage_class |= 2147483648L;
            {
                long stc = dsym.storage_class & 664L;
                if ((stc) != 0)
                {
                    if (stc == 8L)
                        dsym.error(new BytePtr("cannot be `final`, perhaps you meant `const`?"));
                    else
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            stcToBuffer(buf, stc);
                            dsym.error(new BytePtr("cannot be `%s`"), buf.peekChars());
                        }
                        finally {
                        }
                    }
                    dsym.storage_class &= ~stc;
                }
            }
            if ((dsym.storage_class & 524288L) != 0)
            {
                long stc = dsym.storage_class & 1216348163L;
                if ((stc) != 0)
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        stcToBuffer(buf, stc);
                        dsym.error(new BytePtr("cannot be `scope` and `%s`"), buf.peekChars());
                    }
                    finally {
                    }
                }
                else if (dsym.isMember() != null)
                {
                    dsym.error(new BytePtr("field cannot be `scope`"));
                }
                else if (!(dsym.type.hasPointers()))
                {
                    dsym.storage_class &= -524289L;
                }
            }
            if ((dsym.storage_class & 69936087043L) != 0)
            {
            }
            else
            {
                AggregateDeclaration aad = parent.isAggregateDeclaration();
                if (aad != null)
                {
                    if ((((global.params.vfield && (dsym.storage_class & 1048580L) != 0) && dsym._init != null) && !(dsym._init.isVoidInitializer() != null)))
                    {
                        BytePtr s = pcopy((dsym.storage_class & 1048576L) != 0 ? new BytePtr("immutable") : new BytePtr("const"));
                        message(dsym.loc, new BytePtr("`%s.%s` is `%s` field"), ad.toPrettyChars(false), dsym.toChars(), s);
                    }
                    dsym.storage_class |= 64L;
                    {
                        TypeStruct ts = tbn.isTypeStruct();
                        if (ts != null)
                            if (ts.sym.noDefaultCtor)
                            {
                                if ((!(dsym.isThisDeclaration() != null) && !(dsym._init != null)))
                                    aad.noDefaultCtor = true;
                            }
                    }
                }
                InterfaceDeclaration id = parent.isInterfaceDeclaration();
                if (id != null)
                {
                    dsym.error(new BytePtr("field not allowed in interface"));
                }
                else if ((aad != null && aad.sizeok == Sizeok.done))
                {
                    dsym.error(new BytePtr("cannot be further field because it will change the determined %s size"), aad.toChars());
                }
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                {
                    for (; (1) != 0;){
                        TemplateInstance ti2 = ti.tempdecl.parent.isTemplateInstance();
                        if (!(ti2 != null))
                            break;
                        ti = ti2;
                    }
                    AggregateDeclaration ad2 = ti.tempdecl.isMember();
                    if ((ad2 != null && dsym.storage_class != 0L))
                    {
                        dsym.error(new BytePtr("cannot use template to add field to aggregate `%s`"), ad2.toChars());
                    }
                }
            }
            if (((dsym.storage_class & 1374391648288L) == 2097152L && !pequals(dsym.ident, Id.This)))
            {
                dsym.error(new BytePtr("only parameters or `foreach` declarations can be `ref`"));
            }
            if ((dsym.type.hasWild()) != 0)
            {
                if (((dsym.storage_class & 1216348227L) != 0 || dsym.isDataseg()))
                {
                    dsym.error(new BytePtr("only parameters or stack based variables can be `inout`"));
                }
                FuncDeclaration func = (this.sc).func;
                if (func != null)
                {
                    if (func.fes != null)
                        func = func.fes.func;
                    boolean isWild = false;
                    {
                        FuncDeclaration fd = func;
                        for (; fd != null;fd = fd.toParentDecl().isFuncDeclaration()){
                            if ((((TypeFunction)fd.type).iswild) != 0)
                            {
                                isWild = true;
                                break;
                            }
                        }
                    }
                    if (!(isWild))
                    {
                        dsym.error(new BytePtr("`inout` variables can only be declared inside `inout` functions"));
                    }
                }
            }
            if (((!((dsym.storage_class & 343599480832L) != 0) && (tbn.ty & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tbn).sym.noDefaultCtor))
            {
                if (!(dsym._init != null))
                {
                    if (dsym.isField())
                    {
                        dsym.storage_class |= 549755813888L;
                    }
                    else if ((dsym.storage_class & 32L) != 0)
                    {
                    }
                    else
                        dsym.error(new BytePtr("default construction is disabled for type `%s`"), dsym.type.toChars());
                }
            }
            FuncDeclaration fd = parent.isFuncDeclaration();
            if ((dsym.type.isscope() && !((dsym.storage_class & 16777216L) != 0)))
            {
                if (((dsym.storage_class & 1218449473L) != 0 || !(fd != null)))
                {
                    dsym.error(new BytePtr("globals, statics, fields, manifest constants, ref and out parameters cannot be `scope`"));
                }
                if (!((dsym.storage_class & 524288L) != 0))
                {
                    if ((!((dsym.storage_class & 32L) != 0) && !pequals(dsym.ident, Id.withSym)))
                        dsym.error(new BytePtr("reference to `scope class` must be `scope`"));
                }
            }
            if (((this.sc).func != null && !(((this.sc).intypeof) != 0)))
            {
                if (((dsym._init != null && dsym._init.isVoidInitializer() != null) && dsym.type.hasPointers()))
                {
                    if ((this.sc).func.setUnsafe())
                        dsym.error(new BytePtr("`void` initializers for pointers not allowed in safe functions"));
                }
                else if (((!(dsym._init != null) && !((dsym.storage_class & 1216348259L) != 0)) && dsym.type.hasVoidInitPointers()))
                {
                    if ((this.sc).func.setUnsafe())
                        dsym.error(new BytePtr("`void` initializers for pointers not allowed in safe functions"));
                }
            }
            if (((!(dsym._init != null) || dsym._init.isVoidInitializer() != null) && !(fd != null)))
            {
                dsym.storage_class |= 131072L;
            }
            if (dsym._init != null)
                dsym.storage_class |= 4194304L;
            else if ((dsym.storage_class & 8388608L) != 0)
                dsym.error(new BytePtr("manifest constants must have initializers"));
            boolean isBlit = false;
            long sz = 0L;
            try {
                if (((((!(dsym._init != null) && !((dsym.storage_class & 1073741827L) != 0)) && fd != null) && (!((dsym.storage_class & 274877925472L) != 0) || (dsym.storage_class & 4096L) != 0)) && (sz = dsym.type.size()) != 0L))
                {
                    if ((sz == -1L && (dsym.type.ty & 0xFF) != ENUMTY.Terror))
                        dsym.error(new BytePtr("size of type `%s` is invalid"), dsym.type.toChars());
                    Type tv = dsym.type;
                    for (; (tv.ty & 0xFF) == ENUMTY.Tsarray;) {
                        tv = tv.nextOf();
                    }
                    if (tv.needsNested())
                    {
                        assert((tbn.ty & 0xFF) == ENUMTY.Tstruct);
                        checkFrameAccess(dsym.loc, this.sc, tbn.isTypeStruct().sym, 0);
                        Expression e = tv.defaultInitLiteral(dsym.loc);
                        e = new BlitExp(dsym.loc, new VarExp(dsym.loc, dsym, true), e);
                        e = expressionSemantic(e, this.sc);
                        dsym._init = new ExpInitializer(dsym.loc, e);
                        /*goto Ldtor*/throw Dispatch0.INSTANCE;
                    }
                    if (((tv.ty & 0xFF) == ENUMTY.Tstruct && ((TypeStruct)tv).sym.zeroInit))
                    {
                        Expression e = new IntegerExp(dsym.loc, 0L, Type.tint32);
                        e = new BlitExp(dsym.loc, new VarExp(dsym.loc, dsym, true), e);
                        e.type = dsym.type;
                        dsym._init = new ExpInitializer(dsym.loc, e);
                        /*goto Ldtor*/throw Dispatch0.INSTANCE;
                    }
                    if ((dsym.type.baseElemOf().ty & 0xFF) == ENUMTY.Tvoid)
                    {
                        dsym.error(new BytePtr("`%s` does not have a default initializer"), dsym.type.toChars());
                    }
                    else {
                        Expression e = defaultInit(dsym.type, dsym.loc);
                        if (e != null)
                        {
                            dsym._init = new ExpInitializer(dsym.loc, e);
                        }
                    }
                    isBlit = true;
                }
                if (dsym._init != null)
                {
                    this.sc = (this.sc).push();
                    (this.sc).stc &= -4538273628165L;
                    ExpInitializer ei = dsym._init.isExpInitializer();
                    if (ei != null)
                        ei.exp = inferType(ei.exp, dsym.type, 0);
                    if (((this.sc).func != null || (this.sc).intypeof == 1))
                    {
                        if (((fd != null && !((dsym.storage_class & 1216348163L) != 0)) && !(dsym._init.isVoidInitializer() != null)))
                        {
                            if (!(ei != null))
                            {
                                ArrayInitializer ai = dsym._init.isArrayInitializer();
                                Expression e = null;
                                if ((ai != null && (tb.ty & 0xFF) == ENUMTY.Taarray))
                                    e = toAssocArrayLiteral(ai);
                                else
                                    e = initializerToExpression(dsym._init, null);
                                if (!(e != null))
                                {
                                    dsym._init = initializerSemantic(dsym._init, this.sc, dsym.type, NeedInterpret.INITnointerpret);
                                    e = initializerToExpression(dsym._init, null);
                                    if (!(e != null))
                                    {
                                        dsym.error(new BytePtr("is not a static and cannot have static initializer"));
                                        e = new ErrorExp();
                                    }
                                }
                                ei = new ExpInitializer(dsym._init.loc, e);
                                dsym._init = ei;
                            }
                            Expression exp = ei.exp;
                            Expression e1 = new VarExp(dsym.loc, dsym, true);
                            if (isBlit)
                                exp = new BlitExp(dsym.loc, e1, exp);
                            else
                                exp = new ConstructExp(dsym.loc, e1, exp);
                            dsym.canassign++;
                            exp = expressionSemantic(exp, this.sc);
                            dsym.canassign--;
                            exp = exp.optimize(0, false);
                            if ((exp.op & 0xFF) == 127)
                            {
                                dsym._init = new ErrorInitializer();
                                ei = null;
                            }
                            else
                                ei.exp = exp;
                            if ((ei != null && dsym.isScope()))
                            {
                                Expression ex = ei.exp;
                                for (; (ex.op & 0xFF) == 99;) {
                                    ex = ((CommaExp)ex).e2;
                                }
                                if (((ex.op & 0xFF) == 96 || (ex.op & 0xFF) == 95))
                                    ex = ((AssignExp)ex).e2;
                                if ((ex.op & 0xFF) == 22)
                                {
                                    NewExp ne = (NewExp)ex;
                                    if ((dsym.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass)
                                    {
                                        if ((ne.newargs != null && (ne.newargs).length > 1))
                                        {
                                            dsym.mynew = true;
                                        }
                                        else
                                        {
                                            ne.onstack = true;
                                            dsym.onstack = true;
                                        }
                                    }
                                }
                                else if ((ex.op & 0xFF) == 161)
                                {
                                    FuncDeclaration f = ((FuncExp)ex).fd;
                                    f.tookAddressOf--;
                                }
                            }
                        }
                        else
                        {
                            dsym._init = initializerSemantic(dsym._init, this.sc, dsym.type, (this.sc).intypeof == 1 ? NeedInterpret.INITnointerpret : NeedInterpret.INITinterpret);
                            ExpInitializer init_err = dsym._init.isExpInitializer();
                            if ((init_err != null && (init_err.exp.op & 0xFF) == 234))
                            {
                                errorSupplemental(dsym.loc, new BytePtr("compile time context created here"));
                            }
                        }
                    }
                    else if (parent.isAggregateDeclaration() != null)
                    {
                        dsym._scope = scx != null ? scx : (this.sc).copy();
                        (dsym._scope).setNoFree();
                    }
                    else if ((((dsym.storage_class & 9437188L) != 0 || dsym.type.isConst()) || dsym.type.isImmutable()))
                    {
                        if (!((inferred) != 0))
                        {
                            int errors = global.errors;
                            dsym.inuse++;
                            if (ei != null)
                            {
                                Expression exp = ei.exp.syntaxCopy();
                                boolean needctfe = (dsym.isDataseg() || (dsym.storage_class & 8388608L) != 0);
                                if (needctfe)
                                    this.sc = (this.sc).startCTFE();
                                exp = expressionSemantic(exp, this.sc);
                                exp = resolveProperties(this.sc, exp);
                                if (needctfe)
                                    this.sc = (this.sc).endCTFE();
                                Type tb2 = dsym.type.toBasetype();
                                Type ti = exp.type.toBasetype();
                                {
                                    TypeStruct ts = ti.isTypeStruct();
                                    if (ts != null)
                                    {
                                        StructDeclaration sd = ts.sym;
                                        if ((sd.postblit != null && pequals(tb2.toDsymbol(null), sd)))
                                        {
                                            if (exp.isLvalue())
                                                dsym.error(new BytePtr("of type struct `%s` uses `this(this)`, which is not allowed in static initialization"), tb2.toChars());
                                        }
                                    }
                                }
                                ei.exp = exp;
                            }
                            dsym._init = initializerSemantic(dsym._init, this.sc, dsym.type, NeedInterpret.INITinterpret);
                            dsym.inuse--;
                            if (global.errors > errors)
                            {
                                dsym._init = new ErrorInitializer();
                                dsym.type = Type.terror;
                            }
                        }
                        else
                        {
                            dsym._scope = scx != null ? scx : (this.sc).copy();
                            (dsym._scope).setNoFree();
                        }
                    }
                    this.sc = (this.sc).pop();
                }
            }
            catch(Dispatch0 __d){}
        /*Ldtor:*/
            dsym.edtor = dsym.callScopeDtor(this.sc);
            if (dsym.edtor != null)
            {
                if (((((global.params.vsafe && !((dsym.storage_class & 1374397941856L) != 0)) && !(dsym.isDataseg())) && !(dsym.doNotInferScope)) && dsym.type.hasPointers()))
                {
                    Type tv = dsym.type.baseElemOf();
                    if (((tv.ty & 0xFF) == ENUMTY.Tstruct && (((TypeStruct)tv).sym.dtor.storage_class & 524288L) != 0))
                    {
                        dsym.storage_class |= 524288L;
                    }
                }
                if (((this.sc).func != null && (dsym.storage_class & 1073741825L) != 0))
                    dsym.edtor = expressionSemantic(dsym.edtor, (this.sc)._module._scope);
                else
                    dsym.edtor = expressionSemantic(dsym.edtor, this.sc);
            }
            dsym.semanticRun = PASS.semanticdone;
            if ((dsym.type.toBasetype().ty & 0xFF) == ENUMTY.Terror)
                dsym.errors = true;
            if (((this.sc).scopesym != null && !((this.sc).scopesym.isAggregateDeclaration() != null)))
            {
                {
                    ScopeDsymbol sym = (this.sc).scopesym;
                    for (; (sym != null && dsym.endlinnum == 0);sym = sym.parent != null ? sym.parent.isScopeDsymbol() : null) {
                        dsym.endlinnum = sym.endlinnum;
                    }
                }
            }
        }

        public  void visit(TypeInfoDeclaration dsym) {
            assert(dsym.linkage == LINK.c);
        }

        public  void visit(Import imp) {
            if (imp.semanticRun > PASS.init)
                return ;
            if (imp._scope != null)
            {
                this.sc = imp._scope;
                imp._scope = null;
            }
            if (this.sc == null)
                return ;
            imp.semanticRun = PASS.semantic;
            boolean loadErrored = false;
            if (!(imp.mod != null))
            {
                loadErrored = imp.load(this.sc);
                if (imp.mod != null)
                    imp.mod.importAll(null);
            }
            if (imp.mod != null)
            {
                if (((this.sc).minst != null && (this.sc).tinst != null))
                {
                    (this.sc).tinst.importedModules.push(imp.mod);
                    (this.sc).minst.aimports.push(imp.mod);
                }
                else
                {
                    (this.sc)._module.aimports.push(imp.mod);
                }
                if (((this.sc).explicitProtection) != 0)
                    imp.protection = (this.sc).protection.copy();
                if ((!(imp.aliasId != null) && !((imp.names.length) != 0)))
                {
                    ScopeDsymbol scopesym = null;
                    {
                        Scope scd = this.sc;
                        for (; scd != null;scd = (scd).enclosing){
                            if (!((scd).scopesym != null))
                                continue;
                            scopesym = (scd).scopesym;
                            break;
                        }
                    }
                    if (!((imp.isstatic) != 0))
                    {
                        scopesym.importScope(imp.mod, imp.protection);
                    }
                    if (imp.packages != null)
                    {
                        dmodule.Package p = imp.pkg;
                        scopesym.addAccessiblePackage(p, imp.protection);
                        {
                            Slice<Identifier> __r1146 = (imp.packages).opSlice(1, (imp.packages).length).copy();
                            int __key1147 = 0;
                            for (; __key1147 < __r1146.getLength();__key1147 += 1) {
                                Identifier id = __r1146.get(__key1147);
                                p = (dmodule.Package)p.symtab.lookup(id);
                                scopesym.addAccessiblePackage(p, imp.protection);
                            }
                        }
                    }
                    scopesym.addAccessiblePackage(imp.mod, imp.protection);
                }
                if (!(loadErrored))
                {
                    dsymbolSemantic(imp.mod, null);
                }
                if ((imp.mod.needmoduleinfo) != 0)
                {
                    (this.sc)._module.needmoduleinfo = 1;
                }
                this.sc = (this.sc).push(imp.mod);
                (this.sc).protection = imp.protection.copy();
                {
                    int i = 0;
                    for (; i < imp.aliasdecls.length;i++){
                        AliasDeclaration ad = imp.aliasdecls.get(i);
                        Dsymbol sym = imp.mod.search(imp.loc, imp.names.get(i), 1);
                        if (sym != null)
                        {
                            if (!(symbolIsVisible(this.sc, sym)))
                                imp.mod.error(imp.loc, new BytePtr("member `%s` is not visible from module `%s`"), imp.names.get(i).toChars(), (this.sc)._module.toChars());
                            dsymbolSemantic(ad, this.sc);
                        }
                        else
                        {
                            Dsymbol s = imp.mod.search_correct(imp.names.get(i));
                            if (s != null)
                                imp.mod.error(imp.loc, new BytePtr("import `%s` not found, did you mean %s `%s`?"), imp.names.get(i).toChars(), s.kind(), s.toPrettyChars(false));
                            else
                                imp.mod.error(imp.loc, new BytePtr("import `%s` not found"), imp.names.get(i).toChars());
                            ad.type = Type.terror;
                        }
                    }
                }
                this.sc = (this.sc).pop();
            }
            imp.semanticRun = PASS.semanticdone;
            if ((((global.params.moduleDeps != null && !((pequals(imp.id, Id.object) && pequals((this.sc)._module.ident, Id.object)))) && !pequals((this.sc)._module.ident, Id.entrypoint)) && strcmp((this.sc)._module.ident.toChars(), new BytePtr("__main")) != 0))
            {
                OutBuffer ob = global.params.moduleDeps;
                dmodule.Module imod = (this.sc).instantiatingModule();
                if (!(global.params.moduleDepsFile.getLength() != 0))
                    (ob).writestring(new ByteSlice("depsImport "));
                (ob).writestring(imod.toPrettyChars(false));
                (ob).writestring(new ByteSlice(" ("));
                escapePath(ob, imod.srcfile.toChars());
                (ob).writestring(new ByteSlice(") : "));
                protectionToBuffer(ob, imp.protection);
                (ob).writeByte(32);
                if ((imp.isstatic) != 0)
                {
                    stcToBuffer(ob, 1L);
                    (ob).writeByte(32);
                }
                (ob).writestring(new ByteSlice(": "));
                if (imp.packages != null)
                {
                    {
                        int i = 0;
                        for (; i < (imp.packages).length;i++){
                            Identifier pid = (imp.packages).get(i);
                            (ob).printf(new BytePtr("%s."), pid.toChars());
                        }
                    }
                }
                (ob).writestring(imp.id.asString());
                (ob).writestring(new ByteSlice(" ("));
                if (imp.mod != null)
                    escapePath(ob, imp.mod.srcfile.toChars());
                else
                    (ob).writestring(new ByteSlice("???"));
                (ob).writeByte(41);
                {
                    Slice<Identifier> __r1149 = imp.names.opSlice().copy();
                    int __key1148 = 0;
                    for (; __key1148 < __r1149.getLength();__key1148 += 1) {
                        Identifier name = __r1149.get(__key1148);
                        int i = __key1148;
                        if (i == 0)
                            (ob).writeByte(58);
                        else
                            (ob).writeByte(44);
                        Identifier _alias = imp.aliases.get(i);
                        if (!(_alias != null))
                        {
                            (ob).printf(new BytePtr("%s"), name.toChars());
                            _alias = name;
                        }
                        else
                            (ob).printf(new BytePtr("%s=%s"), _alias.toChars(), name.toChars());
                    }
                }
                if (imp.aliasId != null)
                    (ob).printf(new BytePtr(" -> %s"), imp.aliasId.toChars());
                (ob).writenl();
            }
        }

        public  void attribSemantic(AttribDeclaration ad) {
            if (ad.semanticRun != PASS.init)
                return ;
            ad.semanticRun = PASS.semantic;
            DArray<Dsymbol> d = ad.include(this.sc);
            if (d != null)
            {
                Scope sc2 = ad.newScope(this.sc);
                boolean errors = false;
                {
                    int i = 0;
                    for (; i < (d).length;i++){
                        Dsymbol s = (d).get(i);
                        dsymbolSemantic(s, sc2);
                        (errors ? 1 : 0) |= (s.errors ? 1 : 0);
                    }
                }
                (ad.errors ? 1 : 0) |= (errors ? 1 : 0);
                if (sc2 != this.sc)
                    (sc2).pop();
            }
            ad.semanticRun = PASS.semanticdone;
        }

        public  void visit(AttribDeclaration atd) {
            this.attribSemantic(atd);
        }

        public  void visit(AnonDeclaration scd) {
            assert((this.sc).parent != null);
            Dsymbol p = (this.sc).parent.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (!(ad != null))
            {
                error(scd.loc, new BytePtr("%s can only be a part of an aggregate, not %s `%s`"), scd.kind(), p.kind(), p.toChars());
                scd.errors = true;
                return ;
            }
            if (scd.decl != null)
            {
                this.sc = (this.sc).push();
                (this.sc).stc &= -1208484098L;
                (this.sc).inunion = scd.isunion ? scd : null;
                (this.sc).flags = 0;
                {
                    int i = 0;
                    for (; i < (scd.decl).length;i++){
                        Dsymbol s = (scd.decl).get(i);
                        dsymbolSemantic(s, this.sc);
                    }
                }
                this.sc = (this.sc).pop();
            }
        }

        public  void visit(PragmaDeclaration pd) {
            try {
                try {
                    if (global.params.mscoff)
                    {
                        if (pequals(pd.ident, Id.linkerDirective))
                        {
                            if ((pd.args == null || (pd.args).length != 1))
                                pd.error(new BytePtr("one string argument expected for pragma(linkerDirective)"));
                            else
                            {
                                StringExp se = semanticString(this.sc, (pd.args).get(0), new BytePtr("linker directive"));
                                if (!(se != null))
                                    /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                                pd.args.set(0, se);
                                if (global.params.verbose)
                                    message(new BytePtr("linkopt   %.*s"), se.len, se.string);
                            }
                            /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                        }
                    }
                    if (pequals(pd.ident, Id.msg))
                    {
                        if (pd.args != null)
                        {
                            {
                                int i = 0;
                                for (; i < (pd.args).length;i++){
                                    Expression e = (pd.args).get(i);
                                    this.sc = (this.sc).startCTFE();
                                    e = expressionSemantic(e, this.sc);
                                    e = resolveProperties(this.sc, e);
                                    this.sc = (this.sc).endCTFE();
                                    if ((e.type != null && (e.type.ty & 0xFF) == ENUMTY.Tvoid))
                                    {
                                        error(pd.loc, new BytePtr("Cannot pass argument `%s` to `pragma msg` because it is `void`"), e.toChars());
                                        return ;
                                    }
                                    e = ctfeInterpretForPragmaMsg(e);
                                    if ((e.op & 0xFF) == 127)
                                    {
                                        errorSupplemental(pd.loc, new BytePtr("while evaluating `pragma(msg, %s)`"), (pd.args).get(i).toChars());
                                        return ;
                                    }
                                    StringExp se = e.toStringExp();
                                    if (se != null)
                                    {
                                        se = se.toUTF8(this.sc);
                                        fprintf(stderr, new BytePtr("%.*s"), se.len, se.string);
                                    }
                                    else
                                        fprintf(stderr, new BytePtr("%s"), e.toChars());
                                }
                            }
                            fprintf(stderr, new BytePtr("\n"));
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if (pequals(pd.ident, Id.lib))
                    {
                        if ((pd.args == null || (pd.args).length != 1))
                            pd.error(new BytePtr("string expected for library name"));
                        else
                        {
                            StringExp se = semanticString(this.sc, (pd.args).get(0), new BytePtr("library name"));
                            if (!(se != null))
                                /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                            pd.args.set(0, se);
                            ByteSlice name = xarraydup(se.string.slice(0,se.len)).copy();
                            if (global.params.verbose)
                                message(new BytePtr("library   %s"), toBytePtr(name));
                            if ((global.params.moduleDeps != null && !(global.params.moduleDepsFile.getLength() != 0)))
                            {
                                OutBuffer ob = global.params.moduleDeps;
                                dmodule.Module imod = (this.sc).instantiatingModule();
                                (ob).writestring(new ByteSlice("depsLib "));
                                (ob).writestring(imod.toPrettyChars(false));
                                (ob).writestring(new ByteSlice(" ("));
                                escapePath(ob, imod.srcfile.toChars());
                                (ob).writestring(new ByteSlice(") : "));
                                (ob).writestring(name);
                                (ob).writenl();
                            }
                            Mem.xfree(toBytePtr(name));
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if (pequals(pd.ident, Id.startaddress))
                    {
                        if ((pd.args == null || (pd.args).length != 1))
                            pd.error(new BytePtr("function name expected for start address"));
                        else
                        {
                            Expression e = (pd.args).get(0);
                            this.sc = (this.sc).startCTFE();
                            e = expressionSemantic(e, this.sc);
                            this.sc = (this.sc).endCTFE();
                            pd.args.set(0, e);
                            Dsymbol sa = getDsymbol(e);
                            if ((!(sa != null) || !(sa.isFuncDeclaration() != null)))
                                pd.error(new BytePtr("function name expected for start address, not `%s`"), e.toChars());
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if (pequals(pd.ident, Id.Pinline))
                    {
                        /*goto Ldecl*/throw Dispatch0.INSTANCE;
                    }
                    else if (pequals(pd.ident, Id.mangle))
                    {
                        if (pd.args == null)
                            pd.args = new DArray<Expression>();
                        if ((pd.args).length != 1)
                        {
                            pd.error(new BytePtr("string expected for mangled name"));
                            (pd.args).setDim(1);
                            pd.args.set(0, new ErrorExp());
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        StringExp se = semanticString(this.sc, (pd.args).get(0), new BytePtr("mangled name"));
                        if (!(se != null))
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        pd.args.set(0, se);
                        if (!((se.len) != 0))
                        {
                            pd.error(new BytePtr("zero-length string not allowed for mangled name"));
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        if ((se.sz & 0xFF) != 1)
                        {
                            pd.error(new BytePtr("mangled name characters can only be of type `char`"));
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        {
                            IntRef i = ref(0);
                            for (; i.value < se.len;){
                                BytePtr p = pcopy(se.string);
                                IntRef c = ref((p.get(i.value) & 0xFF));
                                if (c.value < 128)
                                {
                                    if (isValidMangling(c.value))
                                    {
                                        i.value += 1;
                                        continue;
                                    }
                                    else
                                    {
                                        pd.error(new BytePtr("char 0x%02x not allowed in mangled name"), c.value);
                                        break;
                                    }
                                }
                                {
                                    BytePtr msg = pcopy(utf_decodeChar(se.string, se.len, i, c));
                                    if (msg != null)
                                    {
                                        pd.error(new BytePtr("%s"), msg);
                                        break;
                                    }
                                }
                                if (!(isUniAlpha(c.value)))
                                {
                                    pd.error(new BytePtr("char `0x%04x` not allowed in mangled name"), c.value);
                                    break;
                                }
                            }
                        }
                    }
                    else if ((pequals(pd.ident, Id.crt_constructor) || pequals(pd.ident, Id.crt_destructor)))
                    {
                        if ((pd.args != null && (pd.args).length != 0))
                            pd.error(new BytePtr("takes no argument"));
                        /*goto Ldecl*/throw Dispatch0.INSTANCE;
                    }
                    else if (global.params.ignoreUnsupportedPragmas)
                    {
                        if (global.params.verbose)
                        {
                            OutBuffer buf = new OutBuffer();
                            try {
                                buf.writestring(pd.ident.asString());
                                if (pd.args != null)
                                {
                                    {
                                        int i = 0;
                                        for (; i < (pd.args).length;i++){
                                            Expression e = (pd.args).get(i);
                                            this.sc = (this.sc).startCTFE();
                                            e = expressionSemantic(e, this.sc);
                                            e = resolveProperties(this.sc, e);
                                            this.sc = (this.sc).endCTFE();
                                            e = e.ctfeInterpret();
                                            if (i == 0)
                                                buf.writestring(new ByteSlice(" ("));
                                            else
                                                buf.writeByte(44);
                                            buf.writestring(e.toChars());
                                        }
                                    }
                                    if (((pd.args).length) != 0)
                                        buf.writeByte(41);
                                }
                                message(new BytePtr("pragma    %s"), buf.peekChars());
                            }
                            finally {
                            }
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else
                        error(pd.loc, new BytePtr("unrecognized `pragma(%s)`"), pd.ident.toChars());
                }
                catch(Dispatch0 __d){}
            /*Ldecl:*/
                if (pd.decl != null)
                {
                    Scope sc2 = pd.newScope(this.sc);
                    {
                        int i = 0;
                        for (; i < (pd.decl).length;i++){
                            Dsymbol s = (pd.decl).get(i);
                            dsymbolSemantic(s, sc2);
                            if (pequals(pd.ident, Id.mangle))
                            {
                                assert((pd.args != null && (pd.args).length == 1));
                                {
                                    StringExp se = (pd.args).get(0).toStringExp();
                                    if (se != null)
                                    {
                                        ByteSlice name = xarraydup(se.string.slice(0,se.len)).copy();
                                        int cnt = setMangleOverride(s, name);
                                        if (cnt > 1)
                                            pd.error(new BytePtr("can only apply to a single declaration"));
                                    }
                                }
                            }
                        }
                    }
                    if (sc2 != this.sc)
                        (sc2).pop();
                }
                return ;
            }
            catch(Dispatch1 __d){}
        /*Lnodecl:*/
            if (pd.decl != null)
            {
                pd.error(new BytePtr("is missing a terminating `;`"));
                /*goto Ldecl*/throw Dispatch0.INSTANCE;
            }
        }

        public  void visit(StaticIfDeclaration sid) {
            this.attribSemantic(sid);
        }

        public  void visit(StaticForeachDeclaration sfd) {
            this.attribSemantic(sfd);
        }

        public  DArray<Dsymbol> compileIt(CompileDeclaration cd) {
            OutBuffer buf = new OutBuffer();
            try {
                if (expressionsToString(buf, this.sc, cd.exps))
                    return null;
                int errors = global.errors;
                int len = buf.offset;
                ByteSlice str = buf.extractChars().slice(0,len).copy();
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
                try {
                    ParserASTCodegen p = new ParserASTCodegen(cd.loc, (this.sc)._module, str, false, diagnosticReporter);
                    try {
                        p.nextToken();
                        DArray<Dsymbol> d = p.parseDeclDefs(0, null, null);
                        if (p.errors())
                        {
                            assert(global.errors != errors);
                            return null;
                        }
                        if ((p.token.value & 0xFF) != 11)
                        {
                            cd.error(new BytePtr("incomplete mixin declaration `%s`"), toBytePtr(str));
                            return null;
                        }
                        return d;
                    }
                    finally {
                    }
                }
                finally {
                }
            }
            finally {
            }
        }

        public  void visit(CompileDeclaration cd) {
            if (!(cd.compiled))
            {
                cd.decl = this.compileIt(cd);
                cd.addMember(this.sc, cd.scopesym);
                cd.compiled = true;
                if ((cd._scope != null && cd.decl != null))
                {
                    {
                        int i = 0;
                        for (; i < (cd.decl).length;i++){
                            Dsymbol s = (cd.decl).get(i);
                            s.setScope(cd._scope);
                        }
                    }
                }
            }
            this.attribSemantic(cd);
        }

        public  void visit(CPPNamespaceDeclaration ns) {
            Ref<CPPNamespaceDeclaration> ns_ref = ref(ns);
            Function1<StringExp,Identifier> identFromSE = new Function1<StringExp,Identifier>(){
                public Identifier invoke(StringExp se){
                    ByteSlice sident = se.toStringz().copy();
                    if ((!((sident.getLength()) != 0) || !(Identifier.isValidIdentifier(sident))))
                    {
                        ns_ref.value.exp.error(new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), sident.getLength(), toBytePtr(sident));
                        return null;
                    }
                    else
                        return Identifier.idPool(sident);
                }
            };
            if (ns_ref.value.ident == null)
            {
                ns_ref.value.namespace = (this.sc).namespace;
                this.sc = (this.sc).startCTFE();
                ns_ref.value.exp = expressionSemantic(ns_ref.value.exp, this.sc);
                ns_ref.value.exp = resolveProperties(this.sc, ns_ref.value.exp);
                this.sc = (this.sc).endCTFE();
                ns_ref.value.exp = ns_ref.value.exp.ctfeInterpret();
                {
                    TupleExp te = ns_ref.value.exp.isTupleExp();
                    if (te != null)
                    {
                        expandTuples(te.exps);
                        CPPNamespaceDeclaration current = ns_ref.value.namespace;
                        {
                            int d = 0;
                            for (; d < (te.exps).length;d += 1){
                                Expression exp = (te.exps).get(d);
                                CPPNamespaceDeclaration prev = (d) != 0 ? current : ns_ref.value.namespace;
                                current = d + 1 != (te.exps).length ? new CPPNamespaceDeclaration(exp, null) : ns_ref.value;
                                current.exp = exp;
                                current.namespace = prev;
                                {
                                    StringExp se = exp.toStringExp();
                                    if (se != null)
                                    {
                                        current.ident = identFromSE.invoke(se);
                                        if (current.ident == null)
                                            return ;
                                    }
                                    else
                                        ns_ref.value.exp.error(new BytePtr("`%s`: index %d is not a string constant, it is a `%s`"), ns_ref.value.exp.toChars(), d, ns_ref.value.exp.type.toChars());
                                }
                            }
                        }
                    }
                    else {
                        StringExp se = ns_ref.value.exp.toStringExp();
                        if (se != null)
                            ns_ref.value.ident = identFromSE.invoke(se);
                        else
                            ns_ref.value.exp.error(new BytePtr("compile time string constant (or tuple) expected, not `%s`"), ns_ref.value.exp.toChars());
                    }
                }
            }
            if (ns_ref.value.ident != null)
                this.attribSemantic(ns_ref.value);
        }

        public  void visit(UserAttributeDeclaration uad) {
            if ((uad.decl != null && uad._scope == null))
                uad.setScope(this.sc);
            this.attribSemantic(uad);
            return ;
        }

        public  void visit(StaticAssert sa) {
            if (sa.semanticRun < PASS.semanticdone)
                sa.semanticRun = PASS.semanticdone;
        }

        public  void visit(DebugSymbol ds) {
            if (ds.semanticRun < PASS.semanticdone)
                ds.semanticRun = PASS.semanticdone;
        }

        public  void visit(VersionSymbol vs) {
            if (vs.semanticRun < PASS.semanticdone)
                vs.semanticRun = PASS.semanticdone;
        }

        public  void visit(dmodule.Package pkg) {
            if (pkg.semanticRun < PASS.semanticdone)
                pkg.semanticRun = PASS.semanticdone;
        }

        public  void visit(dmodule.Module m) {
            if (m.semanticRun != PASS.init)
                return ;
            m.semanticRun = PASS.semantic;
            Scope sc = m._scope;
            if (sc == null)
            {
                Scope.createGlobal(m);
            }
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    dsymbolSemantic(s, sc);
                    dmodule.Module.runDeferredSemantic();
                }
            };
            foreachDsymbol(m.members, __lambda2);
            if (m.userAttribDecl != null)
            {
                dsymbolSemantic(m.userAttribDecl, sc);
            }
            if (m._scope == null)
            {
                sc = (sc).pop();
                (sc).pop();
            }
            m.semanticRun = PASS.semanticdone;
        }

        public  void visit(EnumDeclaration ed) {
            if (ed.semanticRun >= PASS.semanticdone)
                return ;
            if (ed.semanticRun == PASS.semantic)
            {
                assert(ed.memtype != null);
                error(ed.loc, new BytePtr("circular reference to enum base type `%s`"), ed.memtype.toChars());
                ed.errors = true;
                ed.semanticRun = PASS.semanticdone;
                return ;
            }
            int dprogress_save = dmodule.Module.dprogress;
            Scope scx = null;
            if (ed._scope != null)
            {
                this.sc = ed._scope;
                scx = ed._scope;
                ed._scope = null;
            }
            if (this.sc == null)
                return ;
            ed.parent = (this.sc).parent;
            ed.type = typeSemantic(ed.type, ed.loc, this.sc);
            ed.protection = (this.sc).protection.copy();
            if (((this.sc).stc & 1024L) != 0)
                ed.isdeprecated = true;
            ed.userAttribDecl = (this.sc).userAttribDecl;
            ed.semanticRun = PASS.semantic;
            if ((ed.members == null && !(ed.memtype != null)))
            {
                ed.semanticRun = PASS.semanticdone;
                return ;
            }
            if (!(ed.symtab != null))
                ed.symtab = new DsymbolTable();
            if (ed.memtype != null)
            {
                ed.memtype = typeSemantic(ed.memtype, ed.loc, this.sc);
                {
                    TypeEnum te = ed.memtype.isTypeEnum();
                    if (te != null)
                    {
                        EnumDeclaration sym = (EnumDeclaration)te.toDsymbol(this.sc);
                        if ((((!(sym.memtype != null) || sym.members == null) || !(sym.symtab != null)) || sym._scope != null))
                        {
                            ed._scope = scx != null ? scx : (this.sc).copy();
                            (ed._scope).setNoFree();
                            dmodule.Module.addDeferredSemantic(ed);
                            dmodule.Module.dprogress = dprogress_save;
                            ed.semanticRun = PASS.init;
                            return ;
                        }
                    }
                }
                if ((ed.memtype.ty & 0xFF) == ENUMTY.Tvoid)
                {
                    ed.error(new BytePtr("base type must not be `void`"));
                    ed.memtype = Type.terror;
                }
                if ((ed.memtype.ty & 0xFF) == ENUMTY.Terror)
                {
                    ed.errors = true;
                    Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s){
                            s.errors = true;
                        }
                    };
                    foreachDsymbol(ed.members, __lambda2);
                    ed.semanticRun = PASS.semanticdone;
                    return ;
                }
            }
            ed.semanticRun = PASS.semanticdone;
            if (ed.members == null)
                return ;
            if ((ed.members).length == 0)
            {
                ed.error(new BytePtr("enum `%s` must have at least one member"), ed.toChars());
                ed.errors = true;
                return ;
            }
            dmodule.Module.dprogress++;
            Scope sce = null;
            if (ed.isAnonymous())
                sce = this.sc;
            else
            {
                sce = (this.sc).push(ed);
                (sce).parent = ed;
            }
            sce = (sce).startCTFE();
            (sce).setNoFree();
            Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    EnumMember em = s.isEnumMember();
                    if (em != null)
                        em._scope = sce;
                }
            };
            foreachDsymbol(ed.members, __lambda3);
            if (!(ed.added))
            {
                ScopeDsymbol scopesym = null;
                if (ed.isAnonymous())
                {
                    {
                        Scope sct = sce;
                        for (; (1) != 0;sct = (sct).enclosing){
                            assert(sct != null);
                            if ((sct).scopesym != null)
                            {
                                scopesym = (sct).scopesym;
                                if (!((sct).scopesym.symtab != null))
                                    (sct).scopesym.symtab = new DsymbolTable();
                                break;
                            }
                        }
                    }
                }
                else
                {
                    scopesym = ed;
                }
                Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        EnumMember em = s.isEnumMember();
                        if (em != null)
                        {
                            em.ed = ed;
                            em.addMember(sc, scopesym);
                        }
                    }
                };
                foreachDsymbol(ed.members, __lambda4);
            }
            Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    EnumMember em = s.isEnumMember();
                    if (em != null)
                        dsymbolSemantic(em, em._scope);
                }
            };
            foreachDsymbol(ed.members, __lambda5);
        }

        public  void visit(EnumMember em) {
            Ref<EnumMember> em_ref = ref(em);
            Function0<Void> errorReturn = new Function0<Void>(){
                public Void invoke(){
                    em_ref.value.errors = true;
                    em_ref.value.semanticRun = PASS.semanticdone;
                }
            };
            if ((em_ref.value.errors || em_ref.value.semanticRun >= PASS.semanticdone))
                return ;
            if (em_ref.value.semanticRun == PASS.semantic)
            {
                em_ref.value.error(new BytePtr("circular reference to `enum` member"));
                errorReturn.invoke();
                return ;
            }
            assert(em_ref.value.ed != null);
            dsymbolSemantic(em_ref.value.ed, this.sc);
            if (em_ref.value.ed.errors)
                errorReturn.invoke();
                return ;
            if ((em_ref.value.errors || em_ref.value.semanticRun >= PASS.semanticdone))
                return ;
            if (em_ref.value._scope != null)
                this.sc = em_ref.value._scope;
            if (this.sc == null)
                return ;
            em_ref.value.semanticRun = PASS.semantic;
            em_ref.value.protection = (em_ref.value.ed.isAnonymous() ? em_ref.value.ed.protection : new Prot(Prot.Kind.public_)).copy();
            em_ref.value.linkage = LINK.d;
            em_ref.value.storage_class |= 8388608L;
            if (em_ref.value.ed.isAnonymous())
            {
                if (em_ref.value.userAttribDecl != null)
                    em_ref.value.userAttribDecl.userAttribDecl = em_ref.value.ed.userAttribDecl;
                else
                    em_ref.value.userAttribDecl = em_ref.value.ed.userAttribDecl;
            }
            boolean first = pequals(em_ref.value, (em_ref.value.ed.members).get(0));
            if (em_ref.value.origType != null)
            {
                em_ref.value.origType = typeSemantic(em_ref.value.origType, em_ref.value.loc, this.sc);
                em_ref.value.type = em_ref.value.origType;
                assert(em_ref.value.value() != null);
            }
            if (em_ref.value.value() != null)
            {
                Expression e = em_ref.value.value();
                assert(e.dyncast() == DYNCAST.expression);
                e = expressionSemantic(e, this.sc);
                e = resolveProperties(this.sc, e);
                e = e.ctfeInterpret();
                if ((e.op & 0xFF) == 127)
                    errorReturn.invoke();
                    return ;
                if (((first && !(em_ref.value.ed.memtype != null)) && !(em_ref.value.ed.isAnonymous())))
                {
                    em_ref.value.ed.memtype = e.type;
                    if ((em_ref.value.ed.memtype.ty & 0xFF) == ENUMTY.Terror)
                    {
                        em_ref.value.ed.errors = true;
                        errorReturn.invoke();
                        return ;
                    }
                    if ((em_ref.value.ed.memtype.ty & 0xFF) != ENUMTY.Terror)
                    {
                        Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol s){
                                EnumMember enm = s.isEnumMember();
                                if ((((!(enm != null) || pequals(enm, em_ref.value)) || enm.semanticRun < PASS.semanticdone) || enm.origType != null))
                                    return null;
                                Expression ev = enm.value();
                                ev = ev.implicitCastTo(sc, em_ref.value.ed.memtype);
                                ev = ev.ctfeInterpret();
                                ev = ev.castTo(sc, em_ref.value.ed.type);
                                if ((ev.op & 0xFF) == 127)
                                    em_ref.value.ed.errors = true;
                                enm.value() = ev;
                            }
                        };
                        foreachDsymbol(em_ref.value.ed.members, __lambda3);
                        if (em_ref.value.ed.errors)
                        {
                            em_ref.value.ed.memtype = Type.terror;
                            errorReturn.invoke();
                            return ;
                        }
                    }
                }
                if ((em_ref.value.ed.memtype != null && !(em_ref.value.origType != null)))
                {
                    e = e.implicitCastTo(this.sc, em_ref.value.ed.memtype);
                    e = e.ctfeInterpret();
                    em_ref.value.origValue = e;
                    if (!(em_ref.value.ed.isAnonymous()))
                    {
                        e = e.castTo(this.sc, em_ref.value.ed.type.addMod(e.type.mod));
                        e = e.ctfeInterpret();
                    }
                }
                else if (em_ref.value.origType != null)
                {
                    e = e.implicitCastTo(this.sc, em_ref.value.origType);
                    e = e.ctfeInterpret();
                    assert(em_ref.value.ed.isAnonymous());
                    em_ref.value.origValue = e;
                }
                em_ref.value.value() = e;
            }
            else if (first)
            {
                Type t = null;
                if (em_ref.value.ed.memtype != null)
                    t = em_ref.value.ed.memtype;
                else
                {
                    t = Type.tint32;
                    if (!(em_ref.value.ed.isAnonymous()))
                        em_ref.value.ed.memtype = t;
                }
                Expression e = new IntegerExp(em_ref.value.loc, 0L, t);
                e = e.ctfeInterpret();
                em_ref.value.origValue = e;
                if (!(em_ref.value.ed.isAnonymous()))
                {
                    e = e.castTo(this.sc, em_ref.value.ed.type);
                    e = e.ctfeInterpret();
                }
                em_ref.value.value() = e;
            }
            else
            {
                EnumMember emprev = null;
                Function1<Dsymbol,Integer> __lambda4 = new Function1<Dsymbol,Integer>(){
                    public Integer invoke(Dsymbol s){
                        {
                            EnumMember enm = s.isEnumMember();
                            if (enm != null)
                            {
                                if (pequals(enm, em_ref.value))
                                    return 1;
                                emprev = enm;
                            }
                        }
                        return 0;
                    }
                };
                foreachDsymbol(em_ref.value.ed.members, __lambda4);
                assert(emprev != null);
                if (emprev.semanticRun < PASS.semanticdone)
                    dsymbolSemantic(emprev, emprev._scope);
                if (emprev.errors)
                    errorReturn.invoke();
                    return ;
                Expression eprev = emprev.value();
                Type tprev = eprev.type.toHeadMutable().equals(em_ref.value.ed.type.toHeadMutable()) ? em_ref.value.ed.memtype : eprev.type;
                Expression emax = getProperty(tprev, em_ref.value.ed.loc, Id.max, 0);
                emax = expressionSemantic(emax, this.sc);
                emax = emax.ctfeInterpret();
                assert(eprev != null);
                Expression e = new EqualExp(TOK.equal, em_ref.value.loc, eprev, emax);
                e = expressionSemantic(e, this.sc);
                e = e.ctfeInterpret();
                if ((e.toInteger()) != 0)
                {
                    em_ref.value.error(new BytePtr("initialization with `%s.%s+1` causes overflow for type `%s`"), emprev.ed.toChars(), emprev.toChars(), em_ref.value.ed.memtype.toChars());
                    errorReturn.invoke();
                    return ;
                }
                e = new AddExp(em_ref.value.loc, eprev, new IntegerExp(em_ref.value.loc, 1L, Type.tint32));
                e = expressionSemantic(e, this.sc);
                e = e.castTo(this.sc, eprev.type);
                e = e.ctfeInterpret();
                if ((e.op & 0xFF) != 127)
                {
                    assert(emprev.origValue != null);
                    em_ref.value.origValue = new AddExp(em_ref.value.loc, emprev.origValue, new IntegerExp(em_ref.value.loc, 1L, Type.tint32));
                    em_ref.value.origValue = expressionSemantic(em_ref.value.origValue, this.sc);
                    em_ref.value.origValue = em_ref.value.origValue.ctfeInterpret();
                }
                if ((e.op & 0xFF) == 127)
                    errorReturn.invoke();
                    return ;
                if (e.type.isfloating())
                {
                    Expression etest = new EqualExp(TOK.equal, em_ref.value.loc, e, eprev);
                    etest = expressionSemantic(etest, this.sc);
                    etest = etest.ctfeInterpret();
                    if ((etest.toInteger()) != 0)
                    {
                        em_ref.value.error(new BytePtr("has inexact value due to loss of precision"));
                        errorReturn.invoke();
                        return ;
                    }
                }
                em_ref.value.value() = e;
            }
            if (!(em_ref.value.origType != null))
                em_ref.value.type = em_ref.value.value().type;
            assert(em_ref.value.origValue != null);
            em_ref.value.semanticRun = PASS.semanticdone;
        }

        public  void visit(TemplateDeclaration tempdecl) {
            if (tempdecl.semanticRun != PASS.init)
                return ;
            if (tempdecl._scope != null)
            {
                this.sc = tempdecl._scope;
                tempdecl._scope = null;
            }
            if (this.sc == null)
                return ;
            if (((this.sc)._module != null && pequals((this.sc)._module.ident, Id.object)))
            {
                if (pequals(tempdecl.ident, Id.RTInfo))
                    Type.rtinfo = tempdecl;
            }
            if (tempdecl._scope == null)
            {
                tempdecl._scope = (this.sc).copy();
                (tempdecl._scope).setNoFree();
            }
            tempdecl.semanticRun = PASS.semantic;
            tempdecl.parent = (this.sc).parent;
            tempdecl.protection = (this.sc).protection.copy();
            tempdecl.namespace = (this.sc).namespace;
            tempdecl.isstatic = (tempdecl.toParent().isModule() != null || ((tempdecl._scope).stc & 1L) != 0);
            if (!(tempdecl.isstatic))
            {
                {
                    AggregateDeclaration ad = tempdecl.parent.pastMixin().isAggregateDeclaration();
                    if (ad != null)
                        ad.makeNested();
                }
            }
            ScopeDsymbol paramsym = new ScopeDsymbol();
            paramsym.parent = tempdecl.parent;
            Scope paramscope = (this.sc).push(paramsym);
            (paramscope).stc = 0L;
            if (global.params.doDocComments)
            {
                tempdecl.origParameters = new DArray<TemplateParameter>((tempdecl.parameters).length);
                {
                    int i = 0;
                    for (; i < (tempdecl.parameters).length;i++){
                        TemplateParameter tp = (tempdecl.parameters).get(i);
                        tempdecl.origParameters.set(i, tp.syntaxCopy());
                    }
                }
            }
            {
                int i = 0;
                for (; i < (tempdecl.parameters).length;i++){
                    TemplateParameter tp = (tempdecl.parameters).get(i);
                    if (!(tp.declareParameter(paramscope)))
                    {
                        error(tp.loc, new BytePtr("parameter `%s` multiply defined"), tp.ident.toChars());
                        tempdecl.errors = true;
                    }
                    if (!(tpsemantic(tp, paramscope, tempdecl.parameters)))
                    {
                        tempdecl.errors = true;
                    }
                    if ((i + 1 != (tempdecl.parameters).length && tp.isTemplateTupleParameter() != null))
                    {
                        tempdecl.error(new BytePtr("template tuple parameter must be last one"));
                        tempdecl.errors = true;
                    }
                }
            }
            DArray<TemplateParameter> tparams = tparams = new DArray<TemplateParameter>(1);
            try {
                {
                    int i = 0;
                    for (; i < (tempdecl.parameters).length;i++){
                        TemplateParameter tp = (tempdecl.parameters).get(i);
                        tparams.set(0, tp);
                        {
                            int j = 0;
                            for (; j < (tempdecl.parameters).length;j++){
                                if (i == j)
                                    continue;
                                {
                                    TemplateTypeParameter ttp = (tempdecl.parameters).get(j).isTemplateTypeParameter();
                                    if (ttp != null)
                                    {
                                        if (reliesOnTident(ttp.specType, tparams, 0))
                                            tp.dependent = true;
                                    }
                                    else {
                                        TemplateAliasParameter tap = (tempdecl.parameters).get(j).isTemplateAliasParameter();
                                        if (tap != null)
                                        {
                                            if ((reliesOnTident(tap.specType, tparams, 0) || reliesOnTident(isType(tap.specAlias), tparams, 0)))
                                            {
                                                tp.dependent = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                (paramscope).pop();
                tempdecl.onemember = null;
                if (tempdecl.members != null)
                {
                    Ref<Dsymbol> s = ref(null);
                    if ((Dsymbol.oneMembers(tempdecl.members, ptr(s), tempdecl.ident) && s.value != null))
                    {
                        tempdecl.onemember = s.value;
                        s.value.parent = tempdecl;
                    }
                }
                tempdecl.semanticRun = PASS.semanticdone;
            }
            finally {
            }
        }

        public  void visit(TemplateInstance ti) {
            templateInstanceSemantic(ti, this.sc, null);
        }

        public  void visit(TemplateMixin tm) {
            if (tm.semanticRun != PASS.init)
            {
                return ;
            }
            tm.semanticRun = PASS.semantic;
            Scope scx = null;
            if (tm._scope != null)
            {
                this.sc = tm._scope;
                scx = tm._scope;
                tm._scope = null;
            }
            if (((!(tm.findTempDecl(this.sc)) || !(tm.semanticTiargs(this.sc))) || !(tm.findBestMatch(this.sc, null))))
            {
                if (tm.semanticRun == PASS.init)
                {
                    tm._scope = scx != null ? scx : (this.sc).copy();
                    (tm._scope).setNoFree();
                    dmodule.Module.addDeferredSemantic(tm);
                    return ;
                }
                tm.inst = tm;
                tm.errors = true;
                return ;
            }
            TemplateDeclaration tempdecl = tm.tempdecl.isTemplateDeclaration();
            assert(tempdecl != null);
            if (!(tm.ident != null))
            {
                BytePtr s = pcopy(new BytePtr("__mixin"));
                {
                    FuncDeclaration func = (this.sc).parent.isFuncDeclaration();
                    if (func != null)
                    {
                        tm.symtab = func.localsymtab;
                        if (tm.symtab != null)
                        {
                            /*goto L1*//*unrolled goto*/
                        }
                    }
                    else
                    {
                        tm.symtab = (this.sc).parent.isScopeDsymbol().symtab;
                    /*L1:*/
                        assert(tm.symtab != null);
                        tm.ident = Identifier.generateId(s, tm.symtab.len() + 1);
                        tm.symtab.insert((Dsymbol)tm);
                    }
                }
            }
            tm.inst = tm;
            tm.parent = (this.sc).parent;
            {
                Dsymbol s = tm.parent;
            L_outer3:
                for (; s != null;s = s.parent){
                    TemplateMixin tmix = s.isTemplateMixin();
                    if ((!(tmix != null) || !pequals(tempdecl, tmix.tempdecl)))
                        continue L_outer3;
                    if ((tm.tiargs).length != (tmix.tiargs).length)
                        continue L_outer3;
                    try {
                        {
                            int i = 0;
                        L_outer4:
                            for (; i < (tm.tiargs).length;i++){
                                RootObject o = (tm.tiargs).get(i);
                                Type ta = isType(o);
                                Expression ea = isExpression(o);
                                Dsymbol sa = isDsymbol(o);
                                RootObject tmo = (tmix.tiargs).get(i);
                                if (ta != null)
                                {
                                    Type tmta = isType(tmo);
                                    if (!(tmta != null))
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    if (!(ta.equals(tmta)))
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                }
                                else if (ea != null)
                                {
                                    Expression tme = isExpression(tmo);
                                    if ((!(tme != null) || !(ea.equals(tme))))
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                }
                                else if (sa != null)
                                {
                                    Dsymbol tmsa = isDsymbol(tmo);
                                    if (!pequals(sa, tmsa))
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                }
                                else
                                    throw new AssertionError("Unreachable code!");
                            }
                        }
                        tm.error(new BytePtr("recursive mixin instantiation"));
                        return ;
                    }
                    catch(Dispatch0 __d){}
                /*Lcontinue:*/
                    continue L_outer3;
                }
            }
            tm.members = Dsymbol.arraySyntaxCopy(tempdecl.members);
            if (tm.members == null)
                return ;
            tm.symtab = new DsymbolTable();
            {
                Scope sce = this.sc;
                for (; (1) != 0;sce = (sce).enclosing){
                    ScopeDsymbol sds = (sce).scopesym;
                    if (sds != null)
                    {
                        sds.importScope(tm, new Prot(Prot.Kind.public_));
                        break;
                    }
                }
            }
            Scope scy = (this.sc).push(tm);
            (scy).parent = tm;
            tm.argsym = new ScopeDsymbol();
            tm.argsym.parent = (scy).parent;
            Scope argscope = (scy).push(tm.argsym);
            int errorsave = global.errors;
            tm.declareParameters(argscope);
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.addMember(argscope, tm);
                    return null;
                }
            };
            foreachDsymbol(tm.members, __lambda2);
            Scope sc2 = (argscope).push(tm);
            if ((dsymbolsem.visitnest += 1) > 500)
            {
                global.gag = 0;
                tm.error(new BytePtr("recursive expansion"));
                fatal();
            }
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.setScope(sc2);
                    return null;
                }
            };
            foreachDsymbol(tm.members, __lambda4);
            Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.importAll(sc2);
                    return null;
                }
            };
            foreachDsymbol(tm.members, __lambda5);
            Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    dsymbolSemantic(s, sc2);
                    return null;
                }
            };
            foreachDsymbol(tm.members, __lambda6);
            dsymbolsem.visitnest--;
            AggregateDeclaration ad = tm.toParent().isAggregateDeclaration();
            if (((this.sc).func != null && !(ad != null)))
            {
                semantic2(tm, sc2);
                semantic3(tm, sc2);
            }
            if (global.errors != errorsave)
            {
                tm.error(new BytePtr("error instantiating"));
                tm.errors = true;
            }
            (sc2).pop();
            (argscope).pop();
            (scy).pop();
        }

        public  void visit(Nspace ns) {
            if (ns.semanticRun != PASS.init)
                return ;
            if (ns._scope != null)
            {
                this.sc = ns._scope;
                ns._scope = null;
            }
            if (this.sc == null)
                return ;
            boolean repopulateMembers = false;
            if (ns.identExp != null)
            {
                this.sc = (this.sc).startCTFE();
                Expression resolved = expressionSemantic(ns.identExp, this.sc);
                resolved = resolveProperties(this.sc, resolved);
                this.sc = (this.sc).endCTFE();
                resolved = resolved.ctfeInterpret();
                StringExp name = resolved.toStringExp();
                TupleExp tup = name != null ? null : resolved.toTupleExp();
                if ((!(tup != null) && !(name != null)))
                {
                    error(ns.loc, new BytePtr("expected string expression for namespace name, got `%s`"), ns.identExp.toChars());
                    return ;
                }
                ns.identExp = resolved;
                if (name != null)
                {
                    ByteSlice ident = name.toStringz().copy();
                    if ((ident.getLength() == 0 || !(Identifier.isValidIdentifier(ident))))
                    {
                        error(ns.loc, new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), ident.getLength(), toBytePtr(ident));
                        return ;
                    }
                    ns.ident = Identifier.idPool(ident);
                }
                else
                {
                    Nspace parentns = ns;
                    {
                        Slice<Expression> __r1152 = (tup.exps).opSlice().copy();
                        int __key1151 = 0;
                        for (; __key1151 < __r1152.getLength();__key1151 += 1) {
                            Expression exp = __r1152.get(__key1151);
                            int i = __key1151;
                            name = exp.toStringExp();
                            if (!(name != null))
                            {
                                error(ns.loc, new BytePtr("expected string expression for namespace name, got `%s`"), exp.toChars());
                                return ;
                            }
                            ByteSlice ident = name.toStringz().copy();
                            if ((ident.getLength() == 0 || !(Identifier.isValidIdentifier(ident))))
                            {
                                error(ns.loc, new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), ident.getLength(), toBytePtr(ident));
                                return ;
                            }
                            if (i == 0)
                            {
                                ns.ident = Identifier.idPool(ident);
                            }
                            else
                            {
                                Nspace childns = new Nspace(ns.loc, Identifier.idPool(ident), null, parentns.members);
                                parentns.members = new DArray<Dsymbol>();
                                (parentns.members).push(childns);
                                parentns = childns;
                                repopulateMembers = true;
                            }
                        }
                    }
                }
            }
            ns.semanticRun = PASS.semantic;
            ns.parent = (this.sc).parent;
            if (ns.members != null)
            {
                assert(this.sc != null);
                this.sc = (this.sc).push(ns);
                (this.sc).linkage = LINK.cpp;
                (this.sc).parent = ns;
                {
                    Slice<Dsymbol> __r1153 = (ns.members).opSlice().copy();
                    int __key1154 = 0;
                    for (; __key1154 < __r1153.getLength();__key1154 += 1) {
                        Dsymbol s = __r1153.get(__key1154);
                        if (repopulateMembers)
                        {
                            s.addMember(this.sc, (this.sc).scopesym);
                            s.setScope(this.sc);
                        }
                        s.importAll(this.sc);
                    }
                }
                {
                    Slice<Dsymbol> __r1155 = (ns.members).opSlice().copy();
                    int __key1156 = 0;
                    for (; __key1156 < __r1155.getLength();__key1156 += 1) {
                        Dsymbol s = __r1155.get(__key1156);
                        dsymbolSemantic(s, this.sc);
                    }
                }
                (this.sc).pop();
            }
            ns.semanticRun = PASS.semanticdone;
        }

        public  void funcDeclarationSemantic(FuncDeclaration funcdecl) {
            TypeFunction f = null;
            AggregateDeclaration ad = null;
            InterfaceDeclaration id = null;
            if ((funcdecl.semanticRun != PASS.init && funcdecl.isFuncLiteralDeclaration() != null))
            {
                return ;
            }
            if (funcdecl.semanticRun >= PASS.semanticdone)
                return ;
            assert(funcdecl.semanticRun <= PASS.semantic);
            funcdecl.semanticRun = PASS.semantic;
            if (funcdecl._scope != null)
            {
                this.sc = funcdecl._scope;
                funcdecl._scope = null;
            }
            if ((this.sc == null || funcdecl.errors))
                return ;
            funcdecl.namespace = (this.sc).namespace;
            funcdecl.parent = (this.sc).parent;
            Dsymbol parent = funcdecl.toParent();
            funcdecl.foverrides.setDim(0);
            funcdecl.storage_class |= (this.sc).stc & -2097153L;
            ad = funcdecl.isThis();
            if ((ad != null && !(funcdecl.generated)))
            {
                funcdecl.storage_class |= ad.storage_class & 2685403652L;
                ad.makeNested();
            }
            if ((this.sc).func != null)
                funcdecl.storage_class |= (this.sc).func.storage_class & 137438953472L;
            if (((funcdecl.storage_class & 2685403140L) != 0 && !((ad != null || funcdecl.isNested()))))
                funcdecl.storage_class &= -2685403141L;
            if (((this.sc).flags & 256) != 0)
                funcdecl.flags |= FUNCFLAG.compileTimeOnly;
            FuncLiteralDeclaration fld = funcdecl.isFuncLiteralDeclaration();
            if ((fld != null && fld.treq != null))
            {
                Type treq = fld.treq;
                assert((treq.nextOf().ty & 0xFF) == ENUMTY.Tfunction);
                if ((treq.ty & 0xFF) == ENUMTY.Tdelegate)
                    fld.tok = TOK.delegate_;
                else if (((treq.ty & 0xFF) == ENUMTY.Tpointer && (treq.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
                    fld.tok = TOK.function_;
                else
                    throw new AssertionError("Unreachable code!");
                funcdecl.linkage = treq.nextOf().toTypeFunction().linkage;
            }
            else
                funcdecl.linkage = (this.sc).linkage;
            funcdecl.inlining = (this.sc).inlining;
            funcdecl.protection = (this.sc).protection.copy();
            funcdecl.userAttribDecl = (this.sc).userAttribDecl;
            if (!(funcdecl.originalType != null))
                funcdecl.originalType = funcdecl.type.syntaxCopy();
            if ((funcdecl.type.ty & 0xFF) != ENUMTY.Tfunction)
            {
                if ((funcdecl.type.ty & 0xFF) != ENUMTY.Terror)
                {
                    funcdecl.error(new BytePtr("`%s` must be a function instead of `%s`"), funcdecl.toChars(), funcdecl.type.toChars());
                    funcdecl.type = Type.terror;
                }
                funcdecl.errors = true;
                return ;
            }
            if (funcdecl.type.deco == null)
            {
                this.sc = (this.sc).push();
                (this.sc).stc |= funcdecl.storage_class & 137438954496L;
                TypeFunction tf = funcdecl.type.toTypeFunction();
                if ((this.sc).func != null)
                {
                    if ((tf.purity == PURE.impure && (funcdecl.isNested() || funcdecl.isThis() != null)))
                    {
                        FuncDeclaration fd = null;
                        {
                            Dsymbol p = funcdecl.toParent2();
                            for (; p != null;p = p.toParent2()){
                                {
                                    AggregateDeclaration adx = p.isAggregateDeclaration();
                                    if (adx != null)
                                    {
                                        if (adx.isNested())
                                            continue;
                                        break;
                                    }
                                }
                                if ((fd = p.isFuncDeclaration()) != null)
                                    break;
                            }
                        }
                        if (((fd != null && fd.isPureBypassingInference() >= PURE.weak) && !(funcdecl.isInstantiated() != null)))
                        {
                            tf.purity = PURE.fwdref;
                        }
                    }
                }
                if (tf.isref)
                    (this.sc).stc |= 2097152L;
                if (tf.isscope)
                    (this.sc).stc |= 524288L;
                if (tf.isnothrow)
                    (this.sc).stc |= 33554432L;
                if (tf.isnogc)
                    (this.sc).stc |= 4398046511104L;
                if (tf.isproperty)
                    (this.sc).stc |= 4294967296L;
                if (tf.purity == PURE.fwdref)
                    (this.sc).stc |= 67108864L;
                if (tf.trust != TRUST.default_)
                    (this.sc).stc &= -60129542145L;
                if (tf.trust == TRUST.safe)
                    (this.sc).stc |= 8589934592L;
                if (tf.trust == TRUST.system)
                    (this.sc).stc |= 34359738368L;
                if (tf.trust == TRUST.trusted)
                    (this.sc).stc |= 17179869184L;
                if (funcdecl.isCtorDeclaration() != null)
                {
                    (this.sc).flags |= 1;
                    Type tret = ad.handleType();
                    assert(tret != null);
                    tret = tret.addStorageClass(funcdecl.storage_class | (this.sc).stc);
                    tret = tret.addMod(funcdecl.type.mod);
                    tf.next = tret;
                    if (ad.isStructDeclaration() != null)
                        (this.sc).stc |= 2097152L;
                }
                if ((((ad != null && ad.isClassDeclaration() != null) && (tf.isreturn || ((this.sc).stc & 17592186044416L) != 0)) && !(((this.sc).stc & 1L) != 0)))
                    (this.sc).stc |= 524288L;
                if ((((((this.sc).stc & 524288L) != 0 && ad != null) && ad.isStructDeclaration() != null) && !(ad.type.hasPointers())))
                {
                    (this.sc).stc &= -524289L;
                    tf.isscope = false;
                }
                (this.sc).linkage = funcdecl.linkage;
                if ((!(tf.isNaked()) && !((funcdecl.isThis() != null || funcdecl.isNested()))))
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        MODtoBuffer(buf, tf.mod);
                        funcdecl.error(new BytePtr("without `this` cannot be `%s`"), buf.peekChars());
                        tf.mod = (byte)0;
                    }
                    finally {
                    }
                }
                long stc = funcdecl.storage_class;
                if (funcdecl.type.isImmutable())
                    stc |= 1048576L;
                if (funcdecl.type.isConst())
                    stc |= 4L;
                if ((funcdecl.type.isShared() || (funcdecl.storage_class & 512L) != 0))
                    stc |= 536870912L;
                if (funcdecl.type.isWild())
                    stc |= 2147483648L;
                funcdecl.type = funcdecl.type.addSTC(stc);
                funcdecl.type = typeSemantic(funcdecl.type, funcdecl.loc, this.sc);
                this.sc = (this.sc).pop();
            }
            if ((funcdecl.type.ty & 0xFF) != ENUMTY.Tfunction)
            {
                if ((funcdecl.type.ty & 0xFF) != ENUMTY.Terror)
                {
                    funcdecl.error(new BytePtr("`%s` must be a function instead of `%s`"), funcdecl.toChars(), funcdecl.type.toChars());
                    funcdecl.type = Type.terror;
                }
                funcdecl.errors = true;
                return ;
            }
            else
            {
                TypeFunction tfo = funcdecl.originalType.toTypeFunction();
                TypeFunction tfx = funcdecl.type.toTypeFunction();
                tfo.mod = tfx.mod;
                tfo.isscope = tfx.isscope;
                tfo.isreturninferred = tfx.isreturninferred;
                tfo.isscopeinferred = tfx.isscopeinferred;
                tfo.isref = tfx.isref;
                tfo.isnothrow = tfx.isnothrow;
                tfo.isnogc = tfx.isnogc;
                tfo.isproperty = tfx.isproperty;
                tfo.purity = tfx.purity;
                tfo.trust = tfx.trust;
                funcdecl.storage_class &= -4465259184133L;
            }
            f = (TypeFunction)funcdecl.type;
            if ((((funcdecl.storage_class & 256L) != 0 && !(f.isref)) && !(funcdecl.inferRetType)))
                funcdecl.error(new BytePtr("storage class `auto` has no effect if return type is not inferred"));
            if (((f.isscope && !(funcdecl.isNested())) && !(ad != null)))
            {
                funcdecl.error(new BytePtr("functions cannot be `scope`"));
            }
            if (((f.isreturn && !(funcdecl.needThis())) && !(funcdecl.isNested())))
            {
                if (((this.sc).scopesym != null && (this.sc).scopesym.isAggregateDeclaration() != null))
                    funcdecl.error(new BytePtr("`static` member has no `this` to which `return` can apply"));
                else
                    error(funcdecl.loc, new BytePtr("Top-level function `%s` has no `this` to which `return` can apply"), funcdecl.toChars());
            }
            if ((funcdecl.isAbstract() && !(funcdecl.isVirtual())))
            {
                BytePtr sfunc = null;
                if (funcdecl.isStatic())
                    sfunc = pcopy(new BytePtr("static"));
                else if ((funcdecl.protection.kind == Prot.Kind.private_ || funcdecl.protection.kind == Prot.Kind.package_))
                    sfunc = pcopy(protectionToChars(funcdecl.protection.kind));
                else
                    sfunc = pcopy(new BytePtr("final"));
                funcdecl.error(new BytePtr("`%s` functions cannot be `abstract`"), sfunc);
            }
            if (((funcdecl.isOverride() && !(funcdecl.isVirtual())) && !(funcdecl.isFuncLiteralDeclaration() != null)))
            {
                int kind = funcdecl.prot().kind;
                if (((kind == Prot.Kind.private_ || kind == Prot.Kind.package_) && funcdecl.isMember() != null))
                    funcdecl.error(new BytePtr("`%s` method is not virtual and cannot override"), protectionToChars(kind));
                else
                    funcdecl.error(new BytePtr("cannot override a non-virtual function"));
            }
            if ((funcdecl.isAbstract() && funcdecl.isFinalFunc()))
                funcdecl.error(new BytePtr("cannot be both `final` and `abstract`"));
            id = parent.isInterfaceDeclaration();
            if (id != null)
            {
                funcdecl.storage_class |= 16L;
                if ((((((funcdecl.isCtorDeclaration() != null || funcdecl.isPostBlitDeclaration() != null) || funcdecl.isDtorDeclaration() != null) || funcdecl.isInvariantDeclaration() != null) || funcdecl.isNewDeclaration() != null) || funcdecl.isDelete()))
                    funcdecl.error(new BytePtr("constructors, destructors, postblits, invariants, new and delete functions are not allowed in interface `%s`"), id.toChars());
                if ((funcdecl.fbody != null && funcdecl.isVirtual()))
                    funcdecl.error(new BytePtr("function body only allowed in `final` functions in interface `%s`"), id.toChars());
            }
            {
                UnionDeclaration ud = parent.isUnionDeclaration();
                if (ud != null)
                {
                    if (((funcdecl.isPostBlitDeclaration() != null || funcdecl.isDtorDeclaration() != null) || funcdecl.isInvariantDeclaration() != null))
                        funcdecl.error(new BytePtr("destructors, postblits and invariants are not allowed in union `%s`"), ud.toChars());
                }
            }
            try {
                {
                    StructDeclaration sd = parent.isStructDeclaration();
                    if (sd != null)
                    {
                        if (funcdecl.isCtorDeclaration() != null)
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                {
                    ClassDeclaration cd = parent.isClassDeclaration();
                    if (cd != null)
                    {
                        parent = (cd = objc().getParent(funcdecl, cd));
                        if (funcdecl.isCtorDeclaration() != null)
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                        if ((funcdecl.storage_class & 16L) != 0)
                            cd.isabstract = Abstract.yes;
                        if (!(funcdecl.isVirtual()))
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                        if (pequals(funcdecl.type.nextOf(), Type.terror))
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        boolean may_override = false;
                        {
                            int i = 0;
                        L_outer5:
                            for (; i < (cd.baseclasses).length;i++){
                                BaseClass b = (cd.baseclasses).get(i);
                                ClassDeclaration cbd = (b).type.toBasetype().isClassHandle();
                                if (!(cbd != null))
                                    continue L_outer5;
                                {
                                    int j = 0;
                                L_outer6:
                                    for (; j < cbd.vtbl.length;j++){
                                        FuncDeclaration f2 = cbd.vtbl.get(j).isFuncDeclaration();
                                        if ((!(f2 != null) || !pequals(f2.ident, funcdecl.ident)))
                                            continue L_outer6;
                                        if ((cbd.parent != null && cbd.parent.isTemplateInstance() != null))
                                        {
                                            if (!(f2.functionSemantic()))
                                                /*goto Ldone*/throw Dispatch0.INSTANCE;
                                        }
                                        may_override = true;
                                    }
                                }
                            }
                        }
                        if ((may_override && funcdecl.type.nextOf() == null))
                        {
                            funcdecl.error(new BytePtr("return type inference is not supported if may override base class function"));
                        }
                        int vi = cd.baseClass != null ? funcdecl.findVtblIndex(cd.baseClass.vtbl, cd.baseClass.vtbl.length, true) : -1;
                        boolean doesoverride = false;
                        try {
                            try {
                                {
                                    int __dispatch1 = 0;
                                    dispatched_1:
                                    do {
                                        switch (__dispatch1 != 0 ? __dispatch1 : vi)
                                        {
                                            case -1:
                                            /*Lintro:*/
                                            case -1:
                                            __dispatch1 = 0;
                                                if (cd.baseClass != null)
                                                {
                                                    Dsymbol s = cd.baseClass.search(funcdecl.loc, funcdecl.ident, 8);
                                                    if (s != null)
                                                    {
                                                        FuncDeclaration f2 = s.isFuncDeclaration();
                                                        if (f2 != null)
                                                        {
                                                            f2 = f2.overloadExactMatch(funcdecl.type);
                                                            if (((f2 != null && f2.isFinalFunc()) && f2.prot().kind != Prot.Kind.private_))
                                                                funcdecl.error(new BytePtr("cannot override `final` function `%s`"), f2.toPrettyChars(false));
                                                        }
                                                    }
                                                }
                                                if ((((global.params.mscoff && cd.classKind == ClassKind.cpp) && cd.baseClass != null) && (cd.baseClass.vtbl.length) != 0))
                                                {
                                                    funcdecl.interfaceVirtual = funcdecl.overrideInterface();
                                                    if (funcdecl.interfaceVirtual != null)
                                                    {
                                                        cd.vtblFinal.push(funcdecl);
                                                        /*goto Linterfaces*/throw Dispatch0.INSTANCE;
                                                    }
                                                }
                                                if (funcdecl.isFinalFunc())
                                                {
                                                    cd.vtblFinal.push(funcdecl);
                                                }
                                                else
                                                {
                                                    funcdecl.introducing = true;
                                                    if ((cd.classKind == ClassKind.cpp && target.reverseCppOverloads))
                                                    {
                                                        funcdecl.vtblIndex = cd.vtbl.length;
                                                        boolean found = false;
                                                        {
                                                            Slice<Dsymbol> __r1158 = cd.vtbl.opSlice().copy();
                                                            int __key1157 = 0;
                                                            for (; __key1157 < __r1158.getLength();__key1157 += 1) {
                                                                Dsymbol s_1 = __r1158.get(__key1157);
                                                                int i = __key1157;
                                                                if (found)
                                                                    s_1.isFuncDeclaration().vtblIndex += 1;
                                                                else if ((pequals(s_1.ident, funcdecl.ident) && pequals(s_1.parent, parent)))
                                                                {
                                                                    funcdecl.vtblIndex = i;
                                                                    found = true;
                                                                    s_1.isFuncDeclaration().vtblIndex += 1;
                                                                }
                                                            }
                                                        }
                                                        cd.vtbl.insert(funcdecl.vtblIndex, funcdecl);
                                                    }
                                                    else
                                                    {
                                                        vi = cd.vtbl.length;
                                                        cd.vtbl.push(funcdecl);
                                                        funcdecl.vtblIndex = vi;
                                                    }
                                                }
                                                break;
                                            case -2:
                                                funcdecl.errors = true;
                                                return ;
                                            default:
                                            FuncDeclaration fdv = cd.baseClass.vtbl.get(vi).isFuncDeclaration();
                                            FuncDeclaration fdc = cd.vtbl.get(vi).isFuncDeclaration();
                                            if (pequals(fdc, funcdecl))
                                            {
                                                doesoverride = true;
                                                break;
                                            }
                                            if (pequals(fdc.toParent(), parent))
                                            {
                                                if (((fdc.type.mod & 0xFF) == (fdv.type.mod & 0xFF) && (funcdecl.type.mod & 0xFF) != (fdv.type.mod & 0xFF)))
                                                    /*goto Lintro*/{ __dispatch1 = -1; continue dispatched_1; }
                                            }
                                            if (fdv.isDeprecated())
                                                deprecation(funcdecl.loc, new BytePtr("`%s` is overriding the deprecated method `%s`"), funcdecl.toPrettyChars(false), fdv.toPrettyChars(false));
                                            if (fdv.isFinalFunc())
                                                funcdecl.error(new BytePtr("cannot override `final` function `%s`"), fdv.toPrettyChars(false));
                                            if (!(funcdecl.isOverride()))
                                            {
                                                if (fdv.isFuture())
                                                {
                                                    deprecation(funcdecl.loc, new BytePtr("`@__future` base class method `%s` is being overridden by `%s`; rename the latter"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                    /*goto Lintro*/{ __dispatch1 = -1; continue dispatched_1; }
                                                }
                                                else
                                                {
                                                    int vi2 = funcdecl.findVtblIndex(cd.baseClass.vtbl, cd.baseClass.vtbl.length, false);
                                                    if (vi2 < 0)
                                                        deprecation(funcdecl.loc, new BytePtr("cannot implicitly override base class method `%s` with `%s`; add `override` attribute"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                    else
                                                        error(funcdecl.loc, new BytePtr("cannot implicitly override base class method `%s` with `%s`; add `override` attribute"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                }
                                            }
                                            doesoverride = true;
                                            if (pequals(fdc.toParent(), parent))
                                            {
                                                boolean thismixin = funcdecl.parent.isClassDeclaration() != null;
                                                boolean fdcmixin = fdc.parent.isClassDeclaration() != null;
                                                if ((thismixin ? 1 : 0) == (fdcmixin ? 1 : 0))
                                                {
                                                    funcdecl.error(new BytePtr("multiple overrides of same function"));
                                                }
                                                else if (thismixin)
                                                {
                                                    int vitmp = cd.vtbl.length;
                                                    cd.vtbl.push(fdc);
                                                    fdc.vtblIndex = vitmp;
                                                }
                                                else if (fdcmixin)
                                                {
                                                    int vitmp_1 = cd.vtbl.length;
                                                    cd.vtbl.push(funcdecl);
                                                    funcdecl.vtblIndex = vitmp_1;
                                                    break;
                                                }
                                                else
                                                {
                                                    break;
                                                }
                                            }
                                            cd.vtbl.set(vi, funcdecl);
                                            funcdecl.vtblIndex = vi;
                                            funcdecl.foverrides.push(fdv);
                                            if (fdv.tintro != null)
                                                funcdecl.tintro = fdv.tintro;
                                            else if (!(funcdecl.type.equals(fdv.type)))
                                            {
                                                IntRef offset = ref(0);
                                                if (fdv.type.nextOf().isBaseOf(funcdecl.type.nextOf(), ptr(offset)))
                                                {
                                                    funcdecl.tintro = fdv.type;
                                                }
                                            }
                                            break;
                                        }
                                    } while(__dispatch1 != 0);
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*Linterfaces:*/
                            boolean foundVtblMatch = false;
                            {
                                Slice<BaseClass> __r1159 = cd.interfaces.copy();
                                int __key1160 = 0;
                                for (; __key1160 < __r1159.getLength();__key1160 += 1) {
                                    BaseClass b = __r1159.get(__key1160);
                                    vi = funcdecl.findVtblIndex((b).sym.vtbl, (b).sym.vtbl.length, true);
                                    switch (vi)
                                    {
                                        case -1:
                                            break;
                                        case -2:
                                            funcdecl.errors = true;
                                            return ;
                                        default:
                                        FuncDeclaration fdv = (FuncDeclaration)(b).sym.vtbl.get(vi);
                                        Type ti = null;
                                        foundVtblMatch = true;
                                        funcdecl.foverrides.push(fdv);
                                        if (fdv.tintro != null)
                                            ti = fdv.tintro;
                                        else if (!(funcdecl.type.equals(fdv.type)))
                                        {
                                            IntRef offset = ref(0);
                                            if (fdv.type.nextOf().isBaseOf(funcdecl.type.nextOf(), ptr(offset)))
                                            {
                                                ti = fdv.type;
                                            }
                                        }
                                        if (ti != null)
                                        {
                                            if (funcdecl.tintro != null)
                                            {
                                                if (((!(funcdecl.tintro.nextOf().equals(ti.nextOf())) && !(funcdecl.tintro.nextOf().isBaseOf(ti.nextOf(), null))) && !(ti.nextOf().isBaseOf(funcdecl.tintro.nextOf(), null))))
                                                {
                                                    funcdecl.error(new BytePtr("incompatible covariant types `%s` and `%s`"), funcdecl.tintro.toChars(), ti.toChars());
                                                }
                                            }
                                            else
                                            {
                                                funcdecl.tintro = ti;
                                            }
                                        }
                                    }
                                }
                            }
                            if (foundVtblMatch)
                            {
                                /*goto L2*/throw Dispatch1.INSTANCE;
                            }
                            if (((!(doesoverride) && funcdecl.isOverride()) && (funcdecl.type.nextOf() != null || !(may_override))))
                            {
                                BaseClass bc = null;
                                Dsymbol s = null;
                                {
                                    int i = 0;
                                    for (; i < (cd.baseclasses).length;i++){
                                        bc = (cd.baseclasses).get(i);
                                        s = (bc).sym.search_correct(funcdecl.ident);
                                        if (s != null)
                                            break;
                                    }
                                }
                                if (s != null)
                                {
                                    HdrGenState hgs = new HdrGenState();
                                    OutBuffer buf = new OutBuffer();
                                    try {
                                        FuncDeclaration fd = s.isFuncDeclaration();
                                        functionToBufferFull((TypeFunction)funcdecl.type, buf, new Identifier(funcdecl.toPrettyChars(false)), hgs, null);
                                        BytePtr funcdeclToChars = pcopy(buf.peekChars());
                                        if (fd != null)
                                        {
                                            OutBuffer buf1 = new OutBuffer();
                                            try {
                                                functionToBufferFull((TypeFunction)fd.type, buf1, new Identifier(fd.toPrettyChars(false)), hgs, null);
                                                error(funcdecl.loc, new BytePtr("function `%s` does not override any function, did you mean to override `%s`?"), funcdeclToChars, buf1.peekChars());
                                            }
                                            finally {
                                            }
                                        }
                                        else
                                        {
                                            error(funcdecl.loc, new BytePtr("function `%s` does not override any function, did you mean to override %s `%s`?"), funcdeclToChars, s.kind(), s.toPrettyChars(false));
                                            errorSupplemental(funcdecl.loc, new BytePtr("Functions are the only declarations that may be overriden"));
                                        }
                                    }
                                    finally {
                                    }
                                }
                                else
                                    funcdecl.error(new BytePtr("does not override any function"));
                            }
                        }
                        catch(Dispatch1 __d){}
                    /*L2:*/
                        objc().setSelector(funcdecl, this.sc);
                        objc().checkLinkage(funcdecl);
                        objc().addToClassMethodList(funcdecl, cd);
                        {
                            Slice<BaseClass> __r1161 = cd.interfaces.copy();
                            int __key1162 = 0;
                            for (; __key1162 < __r1161.getLength();__key1162 += 1) {
                                BaseClass b = __r1161.get(__key1162);
                                if ((b).sym != null)
                                {
                                    Dsymbol s = search_function((b).sym, funcdecl.ident);
                                    if (s != null)
                                    {
                                        FuncDeclaration f2 = s.isFuncDeclaration();
                                        if (f2 != null)
                                        {
                                            f2 = f2.overloadExactMatch(funcdecl.type);
                                            if (((f2 != null && f2.isFinalFunc()) && f2.prot().kind != Prot.Kind.private_))
                                                funcdecl.error(new BytePtr("cannot override `final` function `%s.%s`"), (b).sym.toChars(), f2.toPrettyChars(false));
                                        }
                                    }
                                }
                            }
                        }
                        if (funcdecl.isOverride())
                        {
                            if ((funcdecl.storage_class & 137438953472L) != 0)
                                deprecation(funcdecl.loc, new BytePtr("`%s` cannot be annotated with `@disable` because it is overriding a function in the base class"), funcdecl.toPrettyChars(false));
                            if (funcdecl.isDeprecated())
                                deprecation(funcdecl.loc, new BytePtr("`%s` cannot be marked as `deprecated` because it is overriding a function in the base class"), funcdecl.toPrettyChars(false));
                        }
                    }
                    else if ((funcdecl.isOverride() && !(parent.isTemplateInstance() != null)))
                        funcdecl.error(new BytePtr("`override` only applies to class member functions"));
                }
                {
                    TemplateInstance ti = parent.isTemplateInstance();
                    if (ti != null)
                        objc().setSelector(funcdecl, this.sc);
                }
                objc().validateSelector(funcdecl);
                f = funcdecl.type.toTypeFunction();
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            if ((!(funcdecl.fbody != null) && !(allowsContractWithoutBody(funcdecl))))
                funcdecl.error(new BytePtr("`in` and `out` contracts can only appear without a body when they are virtual interface functions or abstract"));
            if (funcdecl.isVirtual())
            {
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                {
                    for (; (1) != 0;){
                        TemplateInstance ti2 = ti.tempdecl.parent.isTemplateInstance();
                        if (!(ti2 != null))
                            break;
                        ti = ti2;
                    }
                    ClassDeclaration cd = ti.tempdecl.isClassMember();
                    if (cd != null)
                    {
                        funcdecl.error(new BytePtr("cannot use template to add virtual function to class `%s`"), cd.toChars());
                    }
                }
            }
            if (funcdecl.isMain())
                funcdecl.checkDmain();
            if (funcdecl.canInferAttributes(this.sc))
                funcdecl.initInferAttributes();
            dmodule.Module.dprogress++;
            funcdecl.semanticRun = PASS.semanticdone;
            funcdecl._scope = (this.sc).copy();
            (funcdecl._scope).setNoFree();
            if ((global.params.verbose && !(dsymbolsem.funcDeclarationSemanticprintedMain)))
            {
                BytePtr type = pcopy(funcdecl.isMain() ? new BytePtr("main") : funcdecl.isWinMain() ? new BytePtr("winmain") : funcdecl.isDllMain() ? new BytePtr("dllmain") : null);
                dmodule.Module mod = (this.sc)._module;
                if ((type != null && mod != null))
                {
                    dsymbolsem.funcDeclarationSemanticprintedMain = true;
                    BytePtr name = pcopy(FileName.searchPath(global.path, mod.srcfile.toChars(), true));
                    message(new BytePtr("entry     %-10s\u0009%s"), type, name);
                }
            }
            if (((funcdecl.fbody != null && funcdecl.isMain()) && (this.sc)._module.isRoot()))
                Compiler.genCmain(this.sc);
            assert(((funcdecl.type.ty & 0xFF) != ENUMTY.Terror || funcdecl.errors));
            {
                int __key1163 = 0;
                int __limit1164 = f.parameterList.length();
                for (; __key1163 < __limit1164;__key1163 += 1) {
                    int i = __key1163;
                    Parameter param = f.parameterList.get(i);
                    if ((param != null && param.userAttribDecl != null))
                        dsymbolSemantic(param.userAttribDecl, this.sc);
                }
            }
        }

        public  void visit(FuncDeclaration funcdecl) {
            this.funcDeclarationSemantic(funcdecl);
        }

        public  void visit(CtorDeclaration ctd) {
            if (ctd.semanticRun >= PASS.semanticdone)
                return ;
            if (ctd._scope != null)
            {
                this.sc = ctd._scope;
                ctd._scope = null;
            }
            ctd.parent = (this.sc).parent;
            Dsymbol p = ctd.toParentDecl();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (!(ad != null))
            {
                error(ctd.loc, new BytePtr("constructor can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                ctd.type = Type.terror;
                ctd.errors = true;
                return ;
            }
            this.sc = (this.sc).push();
            if (((this.sc).stc & 1L) != 0)
            {
                if (((this.sc).stc & 536870912L) != 0)
                    deprecation(ctd.loc, new BytePtr("`shared static` has no effect on a constructor inside a `shared static` block. Use `shared static this()`"));
                else
                    deprecation(ctd.loc, new BytePtr("`static` has no effect on a constructor inside a `static` block. Use `static this()`"));
            }
            (this.sc).stc &= -2L;
            (this.sc).flags |= 1;
            this.funcDeclarationSemantic(ctd);
            (this.sc).pop();
            if (ctd.errors)
                return ;
            TypeFunction tf = ctd.type.toTypeFunction();
            if ((ad != null && (!(ctd.parent.isTemplateInstance() != null) || ctd.parent.isTemplateMixin() != null)))
            {
                int dim = tf.parameterList.length();
                {
                    StructDeclaration sd = ad.isStructDeclaration();
                    if (sd != null)
                    {
                        if ((dim == 0 && tf.parameterList.varargs == VarArg.none))
                        {
                            if ((ctd.fbody != null || !((ctd.storage_class & 137438953472L) != 0)))
                            {
                                ctd.error(new BytePtr("default constructor for structs only allowed with `@disable`, no body, and no parameters"));
                                ctd.storage_class |= 137438953472L;
                                ctd.fbody = null;
                            }
                            sd.noDefaultCtor = true;
                        }
                        else if ((dim == 0 && tf.parameterList.varargs != VarArg.none))
                        {
                        }
                        else if (((dim) != 0 && tf.parameterList.get(0).defaultArg != null))
                        {
                            if ((ctd.storage_class & 137438953472L) != 0)
                            {
                                ctd.error(new BytePtr("is marked `@disable`, so it cannot have default arguments for all parameters."));
                                errorSupplemental(ctd.loc, new BytePtr("Use `@disable this();` if you want to disable default initialization."));
                            }
                            else
                                ctd.error(new BytePtr("all parameters have default arguments, but structs cannot have default constructors."));
                        }
                        else if ((dim == 1 || (dim > 1 && tf.parameterList.get(1).defaultArg != null)))
                        {
                            Parameter param = Parameter.getNth(tf.parameterList.parameters, 0, null);
                            if (((param.storageClass & 2097152L) != 0 && pequals(param.type.mutableOf().unSharedOf(), sd.type.mutableOf().unSharedOf())))
                            {
                                ctd.isCpCtor = true;
                            }
                        }
                    }
                    else if ((dim == 0 && tf.parameterList.varargs == VarArg.none))
                    {
                        ad.defaultCtor = ctd;
                    }
                }
            }
        }

        public  void visit(PostBlitDeclaration pbd) {
            if (pbd.semanticRun >= PASS.semanticdone)
                return ;
            if (pbd._scope != null)
            {
                this.sc = pbd._scope;
                pbd._scope = null;
            }
            pbd.parent = (this.sc).parent;
            Dsymbol p = pbd.toParent2();
            StructDeclaration ad = p.isStructDeclaration();
            if (!(ad != null))
            {
                error(pbd.loc, new BytePtr("postblit can only be a member of struct, not %s `%s`"), p.kind(), p.toChars());
                pbd.type = Type.terror;
                pbd.errors = true;
                return ;
            }
            if ((pequals(pbd.ident, Id.postblit) && pbd.semanticRun < PASS.semantic))
                ad.postblits.push(pbd);
            if (!(pbd.type != null))
                pbd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, pbd.storage_class);
            this.sc = (this.sc).push();
            (this.sc).stc &= -2L;
            (this.sc).linkage = LINK.d;
            this.funcDeclarationSemantic(pbd);
            (this.sc).pop();
        }

        public  void visit(DtorDeclaration dd) {
            if (dd.semanticRun >= PASS.semanticdone)
                return ;
            if (dd._scope != null)
            {
                this.sc = dd._scope;
                dd._scope = null;
            }
            dd.parent = (this.sc).parent;
            Dsymbol p = dd.toParent2();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (!(ad != null))
            {
                error(dd.loc, new BytePtr("destructor can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                dd.type = Type.terror;
                dd.errors = true;
                return ;
            }
            if ((pequals(dd.ident, Id.dtor) && dd.semanticRun < PASS.semantic))
                ad.dtors.push(dd);
            if (!(dd.type != null))
            {
                dd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, dd.storage_class);
                if ((ad.classKind == ClassKind.cpp && pequals(dd.ident, Id.dtor)))
                {
                    {
                        ClassDeclaration cldec = ad.isClassDeclaration();
                        if (cldec != null)
                        {
                            assert(cldec.cppDtorVtblIndex == -1);
                            if ((cldec.baseClass != null && cldec.baseClass.cppDtorVtblIndex != -1))
                            {
                                cldec.cppDtorVtblIndex = cldec.baseClass.cppDtorVtblIndex;
                            }
                            else if (!(dd.isFinal()))
                            {
                                cldec.cppDtorVtblIndex = cldec.vtbl.length;
                                cldec.vtbl.push(dd);
                                if (target.twoDtorInVtable)
                                    cldec.vtbl.push(dd);
                            }
                        }
                    }
                }
            }
            this.sc = (this.sc).push();
            (this.sc).stc &= -2L;
            if ((this.sc).linkage != LINK.cpp)
                (this.sc).linkage = LINK.d;
            this.funcDeclarationSemantic(dd);
            (this.sc).pop();
        }

        public  void visit(StaticCtorDeclaration scd) {
            if (scd.semanticRun >= PASS.semanticdone)
                return ;
            if (scd._scope != null)
            {
                this.sc = scd._scope;
                scd._scope = null;
            }
            scd.parent = (this.sc).parent;
            Dsymbol p = scd.parent.pastMixin();
            if (!(p.isScopeDsymbol() != null))
            {
                BytePtr s = pcopy(scd.isSharedStaticCtorDeclaration() != null ? new BytePtr("shared ") : new BytePtr(""));
                error(scd.loc, new BytePtr("`%sstatic` constructor can only be member of module/aggregate/template, not %s `%s`"), s, p.kind(), p.toChars());
                scd.type = Type.terror;
                scd.errors = true;
                return ;
            }
            if (!(scd.type != null))
                scd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, scd.storage_class);
            if ((scd.isInstantiated() != null && scd.semanticRun < PASS.semantic))
            {
                VarDeclaration v = new VarDeclaration(Loc.initial, Type.tint32, Id.gate, null, 0L);
                v.storage_class = (STC.temp | (scd.isSharedStaticCtorDeclaration() != null ? STC.static_ : STC.tls));
                DArray<Statement> sa = new DArray<Statement>();
                Statement s = new ExpStatement(Loc.initial, v);
                (sa).push(s);
                Expression e = new IdentifierExp(Loc.initial, v.ident);
                e = new AddAssignExp(Loc.initial, e, literal1());
                e = new EqualExp(TOK.notEqual, Loc.initial, e, literal1());
                s = new IfStatement(Loc.initial, null, e, new ReturnStatement(Loc.initial, null), null, Loc.initial);
                (sa).push(s);
                if (scd.fbody != null)
                    (sa).push(scd.fbody);
                scd.fbody = new CompoundStatement(Loc.initial, sa);
            }
            this.funcDeclarationSemantic(scd);
            dmodule.Module m = scd.getModule();
            if (!(m != null))
                m = (this.sc)._module;
            if (m != null)
            {
                m.needmoduleinfo = 1;
            }
        }

        public  void visit(StaticDtorDeclaration sdd) {
            if (sdd.semanticRun >= PASS.semanticdone)
                return ;
            if (sdd._scope != null)
            {
                this.sc = sdd._scope;
                sdd._scope = null;
            }
            sdd.parent = (this.sc).parent;
            Dsymbol p = sdd.parent.pastMixin();
            if (!(p.isScopeDsymbol() != null))
            {
                BytePtr s = pcopy(sdd.isSharedStaticDtorDeclaration() != null ? new BytePtr("shared ") : new BytePtr(""));
                error(sdd.loc, new BytePtr("`%sstatic` destructor can only be member of module/aggregate/template, not %s `%s`"), s, p.kind(), p.toChars());
                sdd.type = Type.terror;
                sdd.errors = true;
                return ;
            }
            if (!(sdd.type != null))
                sdd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, sdd.storage_class);
            if ((sdd.isInstantiated() != null && sdd.semanticRun < PASS.semantic))
            {
                VarDeclaration v = new VarDeclaration(Loc.initial, Type.tint32, Id.gate, null, 0L);
                v.storage_class = (STC.temp | (sdd.isSharedStaticDtorDeclaration() != null ? STC.static_ : STC.tls));
                DArray<Statement> sa = new DArray<Statement>();
                Statement s = new ExpStatement(Loc.initial, v);
                (sa).push(s);
                Expression e = new IdentifierExp(Loc.initial, v.ident);
                e = new AddAssignExp(Loc.initial, e, literal-1());
                e = new EqualExp(TOK.notEqual, Loc.initial, e, literal0());
                s = new IfStatement(Loc.initial, null, e, new ReturnStatement(Loc.initial, null), null, Loc.initial);
                (sa).push(s);
                if (sdd.fbody != null)
                    (sa).push(sdd.fbody);
                sdd.fbody = new CompoundStatement(Loc.initial, sa);
                sdd.vgate = v;
            }
            this.funcDeclarationSemantic(sdd);
            dmodule.Module m = sdd.getModule();
            if (!(m != null))
                m = (this.sc)._module;
            if (m != null)
            {
                m.needmoduleinfo = 1;
            }
        }

        public  void visit(InvariantDeclaration invd) {
            if (invd.semanticRun >= PASS.semanticdone)
                return ;
            if (invd._scope != null)
            {
                this.sc = invd._scope;
                invd._scope = null;
            }
            invd.parent = (this.sc).parent;
            Dsymbol p = invd.parent.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (!(ad != null))
            {
                error(invd.loc, new BytePtr("`invariant` can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                invd.type = Type.terror;
                invd.errors = true;
                return ;
            }
            if (((!pequals(invd.ident, Id.classInvariant) && invd.semanticRun < PASS.semantic) && !(ad.isUnionDeclaration() != null)))
                ad.invs.push(invd);
            if (!(invd.type != null))
                invd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, invd.storage_class);
            this.sc = (this.sc).push();
            (this.sc).stc &= -2L;
            (this.sc).stc |= 4L;
            (this.sc).flags = (this.sc).flags & -97 | 32;
            (this.sc).linkage = LINK.d;
            this.funcDeclarationSemantic(invd);
            (this.sc).pop();
        }

        public  void visit(UnitTestDeclaration utd) {
            if (utd.semanticRun >= PASS.semanticdone)
                return ;
            if (utd._scope != null)
            {
                this.sc = utd._scope;
                utd._scope = null;
            }
            utd.protection = (this.sc).protection.copy();
            utd.parent = (this.sc).parent;
            Dsymbol p = utd.parent.pastMixin();
            if (!(p.isScopeDsymbol() != null))
            {
                error(utd.loc, new BytePtr("`unittest` can only be a member of module/aggregate/template, not %s `%s`"), p.kind(), p.toChars());
                utd.type = Type.terror;
                utd.errors = true;
                return ;
            }
            if (global.params.useUnitTests)
            {
                if (!(utd.type != null))
                    utd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, utd.storage_class);
                Scope sc2 = (this.sc).push();
                (sc2).linkage = LINK.d;
                this.funcDeclarationSemantic(utd);
                (sc2).pop();
            }
        }

        public  void visit(NewDeclaration nd) {
            if (!(nd.isDisabled()))
            {
                error(nd.loc, new BytePtr("class allocators are obsolete, consider moving the allocation strategy outside of the class"));
            }
            if (nd.semanticRun >= PASS.semanticdone)
                return ;
            if (nd._scope != null)
            {
                this.sc = nd._scope;
                nd._scope = null;
            }
            nd.parent = (this.sc).parent;
            Dsymbol p = nd.parent.pastMixin();
            if (!(p.isAggregateDeclaration() != null))
            {
                error(nd.loc, new BytePtr("allocator can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                nd.type = Type.terror;
                nd.errors = true;
                return ;
            }
            Type tret = Type.tvoid.pointerTo();
            if (!(nd.type != null))
                nd.type = new TypeFunction(new ParameterList(nd.parameters, nd.varargs), tret, LINK.d, nd.storage_class);
            nd.type = typeSemantic(nd.type, nd.loc, this.sc);
            if (!(nd.isDisabled()))
            {
                TypeFunction tf = nd.type.toTypeFunction();
                if (tf.parameterList.length() < 1)
                {
                    nd.error(new BytePtr("at least one argument of type `size_t` expected"));
                }
                else
                {
                    Parameter fparam = tf.parameterList.get(0);
                    if (!(fparam.type.equals(Type.tsize_t)))
                        nd.error(new BytePtr("first argument must be type `size_t`, not `%s`"), fparam.type.toChars());
                }
            }
            this.funcDeclarationSemantic(nd);
        }

        public  void visit(DeleteDeclaration deld) {
            error(deld.loc, new BytePtr("class deallocators are obsolete, consider moving the deallocation strategy outside of the class"));
            if (deld.semanticRun >= PASS.semanticdone)
                return ;
            if (deld._scope != null)
            {
                this.sc = deld._scope;
                deld._scope = null;
            }
            deld.parent = (this.sc).parent;
            Dsymbol p = deld.parent.pastMixin();
            if (!(p.isAggregateDeclaration() != null))
            {
                error(deld.loc, new BytePtr("deallocator can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                deld.type = Type.terror;
                deld.errors = true;
                return ;
            }
            if (!(deld.type != null))
                deld.type = new TypeFunction(new ParameterList(deld.parameters, VarArg.none), Type.tvoid, LINK.d, deld.storage_class);
            deld.type = typeSemantic(deld.type, deld.loc, this.sc);
            TypeFunction tf = deld.type.toTypeFunction();
            if (tf.parameterList.length() != 1)
            {
                deld.error(new BytePtr("one argument of type `void*` expected"));
            }
            else
            {
                Parameter fparam = tf.parameterList.get(0);
                if (!(fparam.type.equals(Type.tvoid.pointerTo())))
                    deld.error(new BytePtr("one argument of type `void*` expected, not `%s`"), fparam.type.toChars());
            }
            this.funcDeclarationSemantic(deld);
        }

        public  void reinforceInvariant(AggregateDeclaration ad, Scope sc) {
            {
                int i = 0;
                for (; i < (ad.members).length;i++){
                    if (!((ad.members).get(i) != null))
                        continue;
                    FuncDeclaration fd = (ad.members).get(i).isFuncDeclaration();
                    if (((!(fd != null) || fd.generated) || fd.semanticRun != PASS.semantic3done))
                        continue;
                    FuncDeclaration fd_temp = fd.syntaxCopy(null).isFuncDeclaration();
                    fd_temp.storage_class &= -257L;
                    {
                        ClassDeclaration cd = ad.isClassDeclaration();
                        if (cd != null)
                            cd.vtbl.remove(fd.vtblIndex);
                    }
                    dsymbolSemantic(fd_temp, sc);
                    ad.members.set(i, fd_temp);
                }
            }
        }

        public  void visit(StructDeclaration sd) {
            if (sd.semanticRun >= PASS.semanticdone)
                return ;
            int errors = global.errors;
            Scope scx = null;
            if (sd._scope != null)
            {
                this.sc = sd._scope;
                scx = sd._scope;
                sd._scope = null;
            }
            if (!(sd.parent != null))
            {
                assert(((this.sc).parent != null && (this.sc).func != null));
                sd.parent = (this.sc).parent;
            }
            assert((sd.parent != null && !(sd.isAnonymous())));
            if (sd.errors)
                sd.type = Type.terror;
            if (sd.semanticRun == PASS.init)
                sd.type = sd.type.addSTC((this.sc).stc | sd.storage_class);
            sd.type = typeSemantic(sd.type, sd.loc, this.sc);
            {
                TypeStruct ts = sd.type.isTypeStruct();
                if (ts != null)
                    if (!pequals(ts.sym, sd))
                    {
                        TemplateInstance ti = ts.sym.isInstantiated();
                        if ((ti != null && isError(ti)))
                            ts.sym = sd;
                    }
            }
            Ungag ungag = sd.ungagSpeculative().copy();
            try {
                if (sd.semanticRun == PASS.init)
                {
                    sd.protection = (this.sc).protection.copy();
                    sd.alignment = (this.sc).alignment();
                    sd.storage_class |= (this.sc).stc;
                    if ((sd.storage_class & 1024L) != 0)
                        sd.isdeprecated = true;
                    if ((sd.storage_class & 16L) != 0)
                        sd.error(new BytePtr("structs, unions cannot be `abstract`"));
                    sd.userAttribDecl = (this.sc).userAttribDecl;
                    if ((this.sc).linkage == LINK.cpp)
                        sd.classKind = ClassKind.cpp;
                    sd.namespace = (this.sc).namespace;
                }
                else if ((sd.symtab != null && scx == null))
                    return ;
                sd.semanticRun = PASS.semantic;
                if (sd.members == null)
                {
                    sd.semanticRun = PASS.semanticdone;
                    return ;
                }
                if (!(sd.symtab != null))
                {
                    sd.symtab = new DsymbolTable();
                    Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s){
                            s.addMember(sc, sd);
                            return null;
                        }
                    };
                    foreachDsymbol(sd.members, __lambda2);
                }
                Scope sc2 = sd.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        s.setScope(sc2);
                        return null;
                    }
                };
                foreachDsymbol(sd.members, __lambda3);
                Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        s.importAll(sc2);
                        return null;
                    }
                };
                foreachDsymbol(sd.members, __lambda4);
                Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        dsymbolSemantic(s, sc2);
                        (sd.errors ? 1 : 0) |= (s.errors ? 1 : 0);
                    }
                };
                foreachDsymbol(sd.members, __lambda5);
                if (sd.errors)
                    sd.type = Type.terror;
                if (!(sd.determineFields()))
                {
                    if ((sd.type.ty & 0xFF) != ENUMTY.Terror)
                    {
                        sd.error(sd.loc, new BytePtr("circular or forward reference"));
                        sd.errors = true;
                        sd.type = Type.terror;
                    }
                    (sc2).pop();
                    sd.semanticRun = PASS.semanticdone;
                    return ;
                }
                {
                    Slice<VarDeclaration> __r1165 = sd.fields.opSlice().copy();
                    int __key1166 = 0;
                    for (; __key1166 < __r1165.getLength();__key1166 += 1) {
                        VarDeclaration v = __r1165.get(__key1166);
                        Type tb = v.type.baseElemOf();
                        if ((tb.ty & 0xFF) != ENUMTY.Tstruct)
                            continue;
                        StructDeclaration sdec = ((TypeStruct)tb).sym;
                        if (sdec.semanticRun >= PASS.semanticdone)
                            continue;
                        (sc2).pop();
                        sd._scope = scx != null ? scx : (this.sc).copy();
                        (sd._scope).setNoFree();
                        dmodule.Module.addDeferredSemantic(sd);
                        return ;
                    }
                }
                sd.aggNew = (NewDeclaration)sd.search(Loc.initial, Id.classNew, 8);
                sd.aggDelete = (DeleteDeclaration)sd.search(Loc.initial, Id.classDelete, 8);
                sd.ctor = sd.searchCtor();
                sd.dtor = buildDtor(sd, sc2);
                sd.tidtor = buildExternDDtor(sd, sc2);
                sd.postblit = buildPostBlit(sd, sc2);
                sd.hasCopyCtor = buildCopyCtor(sd, sc2);
                buildOpAssign(sd, sc2);
                buildOpEquals(sd, sc2);
                if ((global.params.useTypeInfo && Type.dtypeinfo != null))
                {
                    sd.xeq = buildXopEquals(sd, sc2);
                    sd.xcmp = buildXopCmp(sd, sc2);
                    sd.xhash = buildXtoHash(sd, sc2);
                }
                sd.inv = buildInv(sd, sc2);
                if (sd.inv != null)
                    this.reinforceInvariant(sd, sc2);
                dmodule.Module.dprogress++;
                sd.semanticRun = PASS.semanticdone;
                (sc2).pop();
                if (sd.ctor != null)
                {
                    Dsymbol scall = sd.search(Loc.initial, Id.call, 8);
                    if (scall != null)
                    {
                        int xerrors = global.startGagging();
                        this.sc = (this.sc).push();
                        (this.sc).tinst = null;
                        (this.sc).minst = null;
                        FuncDeclaration fcall = resolveFuncCall(sd.loc, this.sc, scall, null, null, null, FuncResolveFlag.quiet);
                        this.sc = (this.sc).pop();
                        global.endGagging(xerrors);
                        if ((fcall != null && fcall.isStatic()))
                        {
                            sd.error(fcall.loc, new BytePtr("`static opCall` is hidden by constructors and can never be called"));
                            errorSupplemental(fcall.loc, new BytePtr("Please use a factory method instead, or replace all constructors with `static opCall`."));
                        }
                    }
                }
                if (((sd.type.ty & 0xFF) == ENUMTY.Tstruct && !pequals(((TypeStruct)sd.type).sym, sd)))
                {
                    StructDeclaration sym = ((TypeStruct)sd.type).sym;
                    sd.error(new BytePtr("already exists at %s. Perhaps in another function with the same name?"), sym.loc.toChars(global.params.showColumns));
                }
                if (global.errors != errors)
                {
                    sd.type = Type.terror;
                    sd.errors = true;
                    if (sd.deferred != null)
                        sd.deferred.errors = true;
                }
                if ((sd.deferred != null && !((global.gag) != 0)))
                {
                    semantic2(sd.deferred, this.sc);
                    semantic3(sd.deferred, this.sc);
                }
            }
            finally {
            }
        }

        public  void interfaceSemantic(ClassDeclaration cd) {
            cd.vtblInterfaces = new DArray<BaseClass>();
            (cd.vtblInterfaces).reserve(cd.interfaces.getLength());
            {
                Slice<BaseClass> __r1167 = cd.interfaces.copy();
                int __key1168 = 0;
                for (; __key1168 < __r1167.getLength();__key1168 += 1) {
                    BaseClass b = __r1167.get(__key1168);
                    (cd.vtblInterfaces).push(b);
                    (b).copyBaseInterfaces(cd.vtblInterfaces);
                }
            }
        }

        public  void visit(ClassDeclaration cldec) {
            Ref<ClassDeclaration> cldec_ref = ref(cldec);
            if (cldec_ref.value.semanticRun >= PASS.semanticdone)
                return ;
            int errors = global.errors;
            Scope scx = null;
            if (cldec_ref.value._scope != null)
            {
                this.sc = cldec_ref.value._scope;
                scx = cldec_ref.value._scope;
                cldec_ref.value._scope = null;
            }
            if (!(cldec_ref.value.parent != null))
            {
                assert((this.sc).parent != null);
                cldec_ref.value.parent = (this.sc).parent;
            }
            if (cldec_ref.value.errors)
                cldec_ref.value.type = Type.terror;
            cldec_ref.value.type = typeSemantic(cldec_ref.value.type, cldec_ref.value.loc, this.sc);
            {
                TypeClass tc = cldec_ref.value.type.isTypeClass();
                if (tc != null)
                    if (!pequals(tc.sym, cldec_ref.value))
                    {
                        TemplateInstance ti = tc.sym.isInstantiated();
                        if ((ti != null && isError(ti)))
                            tc.sym = cldec_ref.value;
                    }
            }
            Ungag ungag = cldec_ref.value.ungagSpeculative().copy();
            try {
                if (cldec_ref.value.semanticRun == PASS.init)
                {
                    cldec_ref.value.protection = (this.sc).protection.copy();
                    cldec_ref.value.storage_class |= (this.sc).stc;
                    if ((cldec_ref.value.storage_class & 1024L) != 0)
                        cldec_ref.value.isdeprecated = true;
                    if ((cldec_ref.value.storage_class & 256L) != 0)
                        cldec_ref.value.error(new BytePtr("storage class `auto` is invalid when declaring a class, did you mean to use `scope`?"));
                    if ((cldec_ref.value.storage_class & 524288L) != 0)
                        cldec_ref.value.stack = true;
                    if ((cldec_ref.value.storage_class & 16L) != 0)
                        cldec_ref.value.isabstract = Abstract.yes;
                    cldec_ref.value.userAttribDecl = (this.sc).userAttribDecl;
                    if ((this.sc).linkage == LINK.cpp)
                        cldec_ref.value.classKind = ClassKind.cpp;
                    cldec_ref.value.namespace = (this.sc).namespace;
                    if ((this.sc).linkage == LINK.objc)
                        objc().setObjc(cldec_ref.value);
                }
                else if ((cldec_ref.value.symtab != null && scx == null))
                {
                    return ;
                }
                cldec_ref.value.semanticRun = PASS.semantic;
                try {
                    if (cldec_ref.value.baseok < Baseok.done)
                    {
                        // from template resolveBase!(Type)
                        Function1<Type,Type> resolveBaseType = new Function1<Type,Type>(){
                            public Type invoke(Type exp){
                                if (scx == null)
                                {
                                    scx = (sc).copy();
                                    (scx).setNoFree();
                                }
                                cldec_ref.value._scope = scx;
                                Type r = exp.invoke();
                                cldec_ref.value._scope = null;
                                return r;
                            }
                        };

                        // from template resolveBase!(Void)
                        Function1<Void,Void> resolveBaseVoid = new Function1<Void,Void>(){
                            public Void invoke(Void exp){
                                if (scx == null)
                                {
                                    scx = (sc).copy();
                                    (scx).setNoFree();
                                }
                                cldec_ref.value._scope = scx;
                                exp.invoke();
                                cldec_ref.value._scope = null;
                            }
                        };

                        cldec_ref.value.baseok = Baseok.start;
                        {
                            int i = 0;
                            for (; i < (cldec_ref.value.baseclasses).length;){
                                BaseClass b = (cldec_ref.value.baseclasses).get(i);
                                Function0<Type> __dgliteral2 = new Function0<Type>(){
                                    public Type invoke(){
                                        return typeSemantic((b).type, cldec_ref.value.loc, sc);
                                    }
                                };
                                (b).type = resolveBaseType.invoke(__dgliteral2);
                                Type tb = (b).type.toBasetype();
                                {
                                    TypeTuple tup = tb.isTypeTuple();
                                    if (tup != null)
                                    {
                                        (cldec_ref.value.baseclasses).remove(i);
                                        int dim = Parameter.dim(tup.arguments);
                                        {
                                            int j = 0;
                                            for (; j < dim;j++){
                                                Parameter arg = Parameter.getNth(tup.arguments, j, null);
                                                b = new BaseClass(arg.type);
                                                (cldec_ref.value.baseclasses).insert(i + j, b);
                                            }
                                        }
                                    }
                                    else
                                        i++;
                                }
                            }
                        }
                        if (cldec_ref.value.baseok >= Baseok.done)
                        {
                            if (cldec_ref.value.semanticRun >= PASS.semanticdone)
                                return ;
                            /*goto Lancestorsdone*/throw Dispatch0.INSTANCE;
                        }
                        if (((cldec_ref.value.baseclasses).length) != 0)
                        {
                            BaseClass b = (cldec_ref.value.baseclasses).get(0);
                            Type tb = (b).type.toBasetype();
                            TypeClass tc = tb.isTypeClass();
                            try {
                                if (!(tc != null))
                                {
                                    if (!pequals((b).type, Type.terror))
                                        cldec_ref.value.error(new BytePtr("base type must be `class` or `interface`, not `%s`"), (b).type.toChars());
                                    (cldec_ref.value.baseclasses).remove(0);
                                    /*goto L7*/throw Dispatch0.INSTANCE;
                                }
                                if (tc.sym.isDeprecated())
                                {
                                    if (!(cldec_ref.value.isDeprecated()))
                                    {
                                        cldec_ref.value.isdeprecated = true;
                                        tc.checkDeprecated(cldec_ref.value.loc, this.sc);
                                    }
                                }
                                if (tc.sym.isInterfaceDeclaration() != null)
                                    /*goto L7*/throw Dispatch0.INSTANCE;
                                {
                                    ClassDeclaration cdb = tc.sym;
                                L_outer7:
                                    for (; cdb != null;cdb = cdb.baseClass){
                                        if (pequals(cdb, cldec_ref.value))
                                        {
                                            cldec_ref.value.error(new BytePtr("circular inheritance"));
                                            (cldec_ref.value.baseclasses).remove(0);
                                            /*goto L7*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                                cldec_ref.value.baseClass = tc.sym;
                                (b).sym = cldec_ref.value.baseClass;
                                if (tc.sym.baseok < Baseok.done)
                                    Function0<Void> __dgliteral3 = new Function0<Void>(){
                                        public Void invoke(){
                                            dsymbolSemantic(tc.sym, null);
                                        }
                                    };
                                    resolveBaseVoid.invoke(__dgliteral3);
                                if (tc.sym.baseok < Baseok.done)
                                {
                                    if (tc.sym._scope != null)
                                        dmodule.Module.addDeferredSemantic(tc.sym);
                                    cldec_ref.value.baseok = Baseok.none;
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*L7:*/
                        }
                        boolean multiClassError = false;
                        {
                            int i = cldec_ref.value.baseClass != null ? 1 : 0;
                            for (; i < (cldec_ref.value.baseclasses).length;){
                                BaseClass b = (cldec_ref.value.baseclasses).get(i);
                                Type tb = (b).type.toBasetype();
                                TypeClass tc = tb.isTypeClass();
                                if ((!(tc != null) || !(tc.sym.isInterfaceDeclaration() != null)))
                                {
                                    if (tc != null)
                                    {
                                        if (!(multiClassError))
                                        {
                                            error(cldec_ref.value.loc, new BytePtr("`%s`: multiple class inheritance is not supported. Use multiple interface inheritance and/or composition."), cldec_ref.value.toPrettyChars(false));
                                            multiClassError = true;
                                        }
                                        if ((tc.sym.fields.length) != 0)
                                            errorSupplemental(cldec_ref.value.loc, new BytePtr("`%s` has fields, consider making it a member of `%s`"), (b).type.toChars(), cldec_ref.value.type.toChars());
                                        else
                                            errorSupplemental(cldec_ref.value.loc, new BytePtr("`%s` has no fields, consider making it an `interface`"), (b).type.toChars());
                                    }
                                    else if (!pequals((b).type, Type.terror))
                                    {
                                        error(cldec_ref.value.loc, new BytePtr("`%s`: base type must be `interface`, not `%s`"), cldec_ref.value.toPrettyChars(false), (b).type.toChars());
                                    }
                                    (cldec_ref.value.baseclasses).remove(i);
                                    continue;
                                }
                                {
                                    int j = cldec_ref.value.baseClass != null ? 1 : 0;
                                    for (; j < i;j++){
                                        BaseClass b2 = (cldec_ref.value.baseclasses).get(j);
                                        if (pequals((b2).sym, tc.sym))
                                        {
                                            cldec_ref.value.error(new BytePtr("inherits from duplicate interface `%s`"), (b2).sym.toChars());
                                            (cldec_ref.value.baseclasses).remove(i);
                                            continue;
                                        }
                                    }
                                }
                                if (tc.sym.isDeprecated())
                                {
                                    if (!(cldec_ref.value.isDeprecated()))
                                    {
                                        cldec_ref.value.isdeprecated = true;
                                        tc.checkDeprecated(cldec_ref.value.loc, this.sc);
                                    }
                                }
                                (b).sym = tc.sym;
                                if (tc.sym.baseok < Baseok.done)
                                    Function0<Void> __dgliteral4 = new Function0<Void>(){
                                        public Void invoke(){
                                            dsymbolSemantic(tc.sym, null);
                                        }
                                    };
                                    resolveBaseVoid.invoke(__dgliteral4);
                                if (tc.sym.baseok < Baseok.done)
                                {
                                    if (tc.sym._scope != null)
                                        dmodule.Module.addDeferredSemantic(tc.sym);
                                    cldec_ref.value.baseok = Baseok.none;
                                }
                                i++;
                            }
                        }
                        if (cldec_ref.value.baseok == Baseok.none)
                        {
                            cldec_ref.value._scope = scx != null ? scx : (this.sc).copy();
                            (cldec_ref.value._scope).setNoFree();
                            dmodule.Module.addDeferredSemantic(cldec_ref.value);
                            return ;
                        }
                        cldec_ref.value.baseok = Baseok.done;
                        if ((cldec_ref.value.classKind == ClassKind.objc || (cldec_ref.value.baseClass != null && cldec_ref.value.baseClass.classKind == ClassKind.objc)))
                            cldec_ref.value.classKind = ClassKind.objc;
                        if ((((!(cldec_ref.value.baseClass != null) && !pequals(cldec_ref.value.ident, Id.Object)) && ClassDeclaration.object != null) && cldec_ref.value.classKind == ClassKind.d))
                        {
                            Function0<Void> badObjectDotD = new Function0<Void>(){
                                public Void invoke(){
                                    cldec_ref.value.error(new BytePtr("missing or corrupt object.d"));
                                    fatal();
                                }
                            };
                            if ((!(ClassDeclaration.object != null) || ClassDeclaration.object.errors))
                                badObjectDotD.invoke();
                            Type t = ClassDeclaration.object.type;
                            t = typeSemantic(t, cldec_ref.value.loc, this.sc).toBasetype();
                            if ((t.ty & 0xFF) == ENUMTY.Terror)
                                badObjectDotD.invoke();
                            TypeClass tc = t.isTypeClass();
                            assert(tc != null);
                            BaseClass b = new BaseClass(tc);
                            (cldec_ref.value.baseclasses).shift(b);
                            cldec_ref.value.baseClass = tc.sym;
                            assert(!(cldec_ref.value.baseClass.isInterfaceDeclaration() != null));
                            (b).sym = cldec_ref.value.baseClass;
                        }
                        if (cldec_ref.value.baseClass != null)
                        {
                            if ((cldec_ref.value.baseClass.storage_class & 8L) != 0)
                                cldec_ref.value.error(new BytePtr("cannot inherit from class `%s` because it is `final`"), cldec_ref.value.baseClass.toChars());
                            if (cldec_ref.value.baseClass.isCOMclass())
                                cldec_ref.value.com = true;
                            if (cldec_ref.value.baseClass.isCPPclass())
                                cldec_ref.value.classKind = ClassKind.cpp;
                            if (cldec_ref.value.baseClass.stack)
                                cldec_ref.value.stack = true;
                            cldec_ref.value.enclosing = cldec_ref.value.baseClass.enclosing;
                            cldec_ref.value.storage_class |= cldec_ref.value.baseClass.storage_class & 2685403140L;
                        }
                        cldec_ref.value.interfaces = (cldec_ref.value.baseclasses).tdata().slice(cldec_ref.value.baseClass != null ? 1 : 0,(cldec_ref.value.baseclasses).length).copy();
                        {
                            Slice<BaseClass> __r1169 = cldec_ref.value.interfaces.copy();
                            int __key1170 = 0;
                            for (; __key1170 < __r1169.getLength();__key1170 += 1) {
                                BaseClass b = __r1169.get(__key1170);
                                if ((b).sym.isCOMinterface())
                                    cldec_ref.value.com = true;
                                if ((cldec_ref.value.classKind == ClassKind.cpp && !((b).sym.isCPPinterface())))
                                {
                                    error(cldec_ref.value.loc, new BytePtr("C++ class `%s` cannot implement D interface `%s`"), cldec_ref.value.toPrettyChars(false), (b).sym.toPrettyChars(false));
                                }
                            }
                        }
                        this.interfaceSemantic(cldec_ref.value);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lancestorsdone:*/
                if (cldec_ref.value.members == null)
                {
                    cldec_ref.value.semanticRun = PASS.semanticdone;
                    return ;
                }
                if (!(cldec_ref.value.symtab != null))
                {
                    cldec_ref.value.symtab = new DsymbolTable();
                    Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s){
                            s.addMember(sc, cldec_ref.value);
                            return null;
                        }
                    };
                    foreachDsymbol(cldec_ref.value.members, __lambda6);
                    Scope sc2 = cldec_ref.value.newScope(this.sc);
                    Function1<Dsymbol,Void> __lambda7 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s){
                            s.setScope(sc2);
                            return null;
                        }
                    };
                    foreachDsymbol(cldec_ref.value.members, __lambda7);
                    (sc2).pop();
                }
                {
                    int i = 0;
                    for (; i < (cldec_ref.value.baseclasses).length;i++){
                        BaseClass b = (cldec_ref.value.baseclasses).get(i);
                        Type tb = (b).type.toBasetype();
                        TypeClass tc = tb.isTypeClass();
                        if (tc.sym.semanticRun < PASS.semanticdone)
                        {
                            cldec_ref.value._scope = scx != null ? scx : (this.sc).copy();
                            (cldec_ref.value._scope).setNoFree();
                            if (tc.sym._scope != null)
                                dmodule.Module.addDeferredSemantic(tc.sym);
                            dmodule.Module.addDeferredSemantic(cldec_ref.value);
                            return ;
                        }
                    }
                }
                if (cldec_ref.value.baseok == Baseok.done)
                {
                    cldec_ref.value.baseok = Baseok.semanticdone;
                    objc().setMetaclass(cldec_ref.value, this.sc);
                    if (cldec_ref.value.baseClass != null)
                    {
                        if ((cldec_ref.value.classKind == ClassKind.cpp && cldec_ref.value.baseClass.vtbl.length == 0))
                        {
                            cldec_ref.value.error(new BytePtr("C++ base class `%s` needs at least one virtual function"), cldec_ref.value.baseClass.toChars());
                        }
                        cldec_ref.value.vtbl.setDim(cldec_ref.value.baseClass.vtbl.length);
                        memcpy((BytePtr)(cldec_ref.value.vtbl.tdata()), (cldec_ref.value.baseClass.vtbl.tdata()), (4 * cldec_ref.value.vtbl.length));
                        cldec_ref.value.vthis = cldec_ref.value.baseClass.vthis;
                        cldec_ref.value.vthis2 = cldec_ref.value.baseClass.vthis2;
                    }
                    else
                    {
                        cldec_ref.value.vtbl.setDim(0);
                        if ((cldec_ref.value.vtblOffset()) != 0)
                            cldec_ref.value.vtbl.push(cldec_ref.value);
                    }
                    if (cldec_ref.value.vthis != null)
                    {
                        if ((cldec_ref.value.storage_class & 1L) != 0)
                            cldec_ref.value.error(new BytePtr("static class cannot inherit from nested class `%s`"), cldec_ref.value.baseClass.toChars());
                        if ((!pequals(cldec_ref.value.toParentLocal(), cldec_ref.value.baseClass.toParentLocal()) && ((!(cldec_ref.value.toParentLocal() != null) || !(cldec_ref.value.baseClass.toParentLocal().getType() != null)) || !(cldec_ref.value.baseClass.toParentLocal().getType().isBaseOf(cldec_ref.value.toParentLocal().getType(), null)))))
                        {
                            if (cldec_ref.value.toParentLocal() != null)
                            {
                                cldec_ref.value.error(new BytePtr("is nested within `%s`, but super class `%s` is nested within `%s`"), cldec_ref.value.toParentLocal().toChars(), cldec_ref.value.baseClass.toChars(), cldec_ref.value.baseClass.toParentLocal().toChars());
                            }
                            else
                            {
                                cldec_ref.value.error(new BytePtr("is not nested, but super class `%s` is nested within `%s`"), cldec_ref.value.baseClass.toChars(), cldec_ref.value.baseClass.toParentLocal().toChars());
                            }
                            cldec_ref.value.enclosing = null;
                        }
                        if (cldec_ref.value.vthis2 != null)
                        {
                            if ((!pequals(cldec_ref.value.toParent2(), cldec_ref.value.baseClass.toParent2()) && ((!(cldec_ref.value.toParent2() != null) || !(cldec_ref.value.baseClass.toParent2().getType() != null)) || !(cldec_ref.value.baseClass.toParent2().getType().isBaseOf(cldec_ref.value.toParent2().getType(), null)))))
                            {
                                if ((cldec_ref.value.toParent2() != null && !pequals(cldec_ref.value.toParent2(), cldec_ref.value.toParentLocal())))
                                {
                                    cldec_ref.value.error(new BytePtr("needs the frame pointer of `%s`, but super class `%s` needs the frame pointer of `%s`"), cldec_ref.value.toParent2().toChars(), cldec_ref.value.baseClass.toChars(), cldec_ref.value.baseClass.toParent2().toChars());
                                }
                                else
                                {
                                    cldec_ref.value.error(new BytePtr("doesn't need a frame pointer, but super class `%s` needs the frame pointer of `%s`"), cldec_ref.value.baseClass.toChars(), cldec_ref.value.baseClass.toParent2().toChars());
                                }
                            }
                        }
                        else
                            cldec_ref.value.makeNested2();
                    }
                    else
                        cldec_ref.value.makeNested();
                }
                Scope sc2 = cldec_ref.value.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda8 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        s.importAll(sc2);
                        return null;
                    }
                };
                foreachDsymbol(cldec_ref.value.members, __lambda8);
                Function1<Dsymbol,Void> __lambda9 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        dsymbolSemantic(s, sc2);
                        return null;
                    }
                };
                foreachDsymbol(cldec_ref.value.members, __lambda9);
                if (!(cldec_ref.value.determineFields()))
                {
                    assert(pequals(cldec_ref.value.type, Type.terror));
                    (sc2).pop();
                    return ;
                }
                {
                    Slice<VarDeclaration> __r1171 = cldec_ref.value.fields.opSlice().copy();
                    int __key1172 = 0;
                    for (; __key1172 < __r1171.getLength();__key1172 += 1) {
                        VarDeclaration v = __r1171.get(__key1172);
                        Type tb = v.type.baseElemOf();
                        if ((tb.ty & 0xFF) != ENUMTY.Tstruct)
                            continue;
                        StructDeclaration sd = ((TypeStruct)tb).sym;
                        if (sd.semanticRun >= PASS.semanticdone)
                            continue;
                        (sc2).pop();
                        cldec_ref.value._scope = scx != null ? scx : (this.sc).copy();
                        (cldec_ref.value._scope).setNoFree();
                        dmodule.Module.addDeferredSemantic(cldec_ref.value);
                        return ;
                    }
                }
                cldec_ref.value.aggNew = (NewDeclaration)cldec_ref.value.search(Loc.initial, Id.classNew, 8);
                cldec_ref.value.aggDelete = (DeleteDeclaration)cldec_ref.value.search(Loc.initial, Id.classDelete, 8);
                cldec_ref.value.ctor = cldec_ref.value.searchCtor();
                if ((!(cldec_ref.value.ctor != null) && cldec_ref.value.noDefaultCtor))
                {
                    {
                        Slice<VarDeclaration> __r1173 = cldec_ref.value.fields.opSlice().copy();
                        int __key1174 = 0;
                        for (; __key1174 < __r1173.getLength();__key1174 += 1) {
                            VarDeclaration v = __r1173.get(__key1174);
                            if ((v.storage_class & 549755813888L) != 0)
                                error(v.loc, new BytePtr("field `%s` must be initialized in constructor"), v.toChars());
                        }
                    }
                }
                if (((!(cldec_ref.value.ctor != null) && cldec_ref.value.baseClass != null) && cldec_ref.value.baseClass.ctor != null))
                {
                    FuncDeclaration fd = resolveFuncCall(cldec_ref.value.loc, sc2, cldec_ref.value.baseClass.ctor, null, cldec_ref.value.type, null, FuncResolveFlag.quiet);
                    if (!(fd != null))
                        fd = resolveFuncCall(cldec_ref.value.loc, sc2, cldec_ref.value.baseClass.ctor, null, cldec_ref.value.type.sharedOf(), null, FuncResolveFlag.quiet);
                    if ((fd != null && !(fd.errors)))
                    {
                        TypeFunction btf = fd.type.toTypeFunction();
                        TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), null, LINK.d, fd.storage_class);
                        tf.mod = btf.mod;
                        tf.purity = btf.purity;
                        tf.isnothrow = btf.isnothrow;
                        tf.isnogc = btf.isnogc;
                        tf.trust = btf.trust;
                        CtorDeclaration ctor = new CtorDeclaration(cldec_ref.value.loc, Loc.initial, 0L, tf, false);
                        ctor.fbody = new CompoundStatement(Loc.initial, new DArray<Statement>());
                        (cldec_ref.value.members).push(ctor);
                        ctor.addMember(this.sc, cldec_ref.value);
                        dsymbolSemantic(ctor, sc2);
                        cldec_ref.value.ctor = ctor;
                        cldec_ref.value.defaultCtor = ctor;
                    }
                    else
                    {
                        cldec_ref.value.error(new BytePtr("cannot implicitly generate a default constructor when base class `%s` is missing a default constructor"), cldec_ref.value.baseClass.toPrettyChars(false));
                    }
                }
                cldec_ref.value.dtor = buildDtor(cldec_ref.value, sc2);
                cldec_ref.value.tidtor = buildExternDDtor(cldec_ref.value, sc2);
                if ((cldec_ref.value.classKind == ClassKind.cpp && cldec_ref.value.cppDtorVtblIndex != -1))
                {
                    cldec_ref.value.dtor.vtblIndex = cldec_ref.value.cppDtorVtblIndex;
                    cldec_ref.value.vtbl.set(cldec_ref.value.cppDtorVtblIndex, cldec_ref.value.dtor);
                    if (target.twoDtorInVtable)
                    {
                        cldec_ref.value.vtbl.set((cldec_ref.value.cppDtorVtblIndex + 1), cldec_ref.value.dtor);
                    }
                }
                {
                    FuncDeclaration f = hasIdentityOpAssign(cldec_ref.value, sc2);
                    if (f != null)
                    {
                        if (!((f.storage_class & 137438953472L) != 0))
                            cldec_ref.value.error(f.loc, new BytePtr("identity assignment operator overload is illegal"));
                    }
                }
                cldec_ref.value.inv = buildInv(cldec_ref.value, sc2);
                if (cldec_ref.value.inv != null)
                    this.reinforceInvariant(cldec_ref.value, sc2);
                dmodule.Module.dprogress++;
                cldec_ref.value.semanticRun = PASS.semanticdone;
                (sc2).pop();
                if (cldec_ref.value.isabstract != Abstract.fwdref)
                {
                    int isabstractsave = cldec_ref.value.isabstract;
                    cldec_ref.value.isabstract = Abstract.fwdref;
                    cldec_ref.value.isAbstract();
                    if (cldec_ref.value.isabstract != isabstractsave)
                    {
                        cldec_ref.value.error(new BytePtr("cannot infer `abstract` attribute due to circular dependencies"));
                    }
                }
                if (((cldec_ref.value.type.ty & 0xFF) == ENUMTY.Tclass && !pequals(((TypeClass)cldec_ref.value.type).sym, cldec_ref.value)))
                {
                    ClassDeclaration cd = ((TypeClass)cldec_ref.value.type).sym;
                    cldec_ref.value.error(new BytePtr("already exists at %s. Perhaps in another function with the same name?"), cd.loc.toChars(global.params.showColumns));
                }
                if (global.errors != errors)
                {
                    cldec_ref.value.type = Type.terror;
                    cldec_ref.value.errors = true;
                    if (cldec_ref.value.deferred != null)
                        cldec_ref.value.deferred.errors = true;
                }
                if ((cldec_ref.value.storage_class & 512L) != 0)
                {
                    {
                        Slice<VarDeclaration> __r1175 = cldec_ref.value.fields.opSlice().copy();
                        int __key1176 = 0;
                        for (; __key1176 < __r1175.getLength();__key1176 += 1) {
                            VarDeclaration vd = __r1175.get(__key1176);
                            if ((!(vd.isThisDeclaration() != null) && !(vd.prot().isMoreRestrictiveThan(new Prot(Prot.Kind.public_)))))
                            {
                                vd.error(new BytePtr("Field members of a `synchronized` class cannot be `%s`"), protectionToChars(vd.prot().kind));
                            }
                        }
                    }
                }
                if ((cldec_ref.value.deferred != null && !((global.gag) != 0)))
                {
                    semantic2(cldec_ref.value.deferred, this.sc);
                    semantic3(cldec_ref.value.deferred, this.sc);
                }
                if ((cldec_ref.value.storage_class & 524288L) != 0)
                    deprecation(cldec_ref.value.loc, new BytePtr("`scope` as a type constraint is deprecated.  Use `scope` at the usage site."));
            }
            finally {
            }
        }

        public  void visit(InterfaceDeclaration idec) {
            Function1<InterfaceDeclaration,Boolean> isAnonymousMetaclass = new Function1<InterfaceDeclaration,Boolean>(){
                public Boolean invoke(InterfaceDeclaration idec){
                    return ((idec.classKind == ClassKind.objc && idec.objc.isMeta) && idec.isAnonymous());
                }
            };
            if (idec.semanticRun >= PASS.semanticdone)
                return ;
            int errors = global.errors;
            Scope scx = null;
            if (idec._scope != null)
            {
                this.sc = idec._scope;
                scx = idec._scope;
                idec._scope = null;
            }
            if (!(idec.parent != null))
            {
                assert(((this.sc).parent != null && (this.sc).func != null));
                idec.parent = (this.sc).parent;
            }
            assert(((idec.parent != null && !(idec.isAnonymous())) || isAnonymousMetaclass.invoke(idec)));
            if (idec.errors)
                idec.type = Type.terror;
            idec.type = typeSemantic(idec.type, idec.loc, this.sc);
            if (((idec.type.ty & 0xFF) == ENUMTY.Tclass && !pequals(((TypeClass)idec.type).sym, idec)))
            {
                TemplateInstance ti = ((TypeClass)idec.type).sym.isInstantiated();
                if ((ti != null && isError(ti)))
                    ((TypeClass)idec.type).sym = idec;
            }
            Ungag ungag = idec.ungagSpeculative().copy();
            try {
                if (idec.semanticRun == PASS.init)
                {
                    idec.protection = (this.sc).protection.copy();
                    idec.storage_class |= (this.sc).stc;
                    if ((idec.storage_class & 1024L) != 0)
                        idec.isdeprecated = true;
                    idec.userAttribDecl = (this.sc).userAttribDecl;
                }
                else if (idec.symtab != null)
                {
                    if ((idec.sizeok == Sizeok.done || scx == null))
                    {
                        idec.semanticRun = PASS.semanticdone;
                        return ;
                    }
                }
                idec.semanticRun = PASS.semantic;
                try {
                    if (idec.baseok < Baseok.done)
                    {
                        // from template resolveBase!(Type)
                        Function1<Type,Type> resolveBaseType = new Function1<Type,Type>(){
                            public Type invoke(Type exp){
                                if (scx == null)
                                {
                                    scx = (sc).copy();
                                    (scx).setNoFree();
                                }
                                idec._scope = scx;
                                Type r = exp.invoke();
                                idec._scope = null;
                                return r;
                            }
                        };

                        // from template resolveBase!(Void)
                        Function1<Void,Void> resolveBaseVoid = new Function1<Void,Void>(){
                            public Void invoke(Void exp){
                                if (scx == null)
                                {
                                    scx = (sc).copy();
                                    (scx).setNoFree();
                                }
                                idec._scope = scx;
                                exp.invoke();
                                idec._scope = null;
                            }
                        };

                        idec.baseok = Baseok.start;
                        {
                            int i = 0;
                            for (; i < (idec.baseclasses).length;){
                                BaseClass b = (idec.baseclasses).get(i);
                                Function0<Type> __dgliteral3 = new Function0<Type>(){
                                    public Type invoke(){
                                        return typeSemantic((b).type, idec.loc, sc);
                                    }
                                };
                                (b).type = resolveBaseType.invoke(__dgliteral3);
                                Type tb = (b).type.toBasetype();
                                {
                                    TypeTuple tup = tb.isTypeTuple();
                                    if (tup != null)
                                    {
                                        (idec.baseclasses).remove(i);
                                        int dim = Parameter.dim(tup.arguments);
                                        {
                                            int j = 0;
                                            for (; j < dim;j++){
                                                Parameter arg = Parameter.getNth(tup.arguments, j, null);
                                                b = new BaseClass(arg.type);
                                                (idec.baseclasses).insert(i + j, b);
                                            }
                                        }
                                    }
                                    else
                                        i++;
                                }
                            }
                        }
                        if (idec.baseok >= Baseok.done)
                        {
                            if (idec.semanticRun >= PASS.semanticdone)
                                return ;
                            /*goto Lancestorsdone*/throw Dispatch0.INSTANCE;
                        }
                        if ((!(((idec.baseclasses).length) != 0) && (this.sc).linkage == LINK.cpp))
                            idec.classKind = ClassKind.cpp;
                        idec.namespace = (this.sc).namespace;
                        if ((this.sc).linkage == LINK.objc)
                        {
                            objc().setObjc(idec);
                            objc().deprecate(idec);
                        }
                        {
                            int i = 0;
                            for (; i < (idec.baseclasses).length;){
                                BaseClass b = (idec.baseclasses).get(i);
                                Type tb = (b).type.toBasetype();
                                TypeClass tc = (tb.ty & 0xFF) == ENUMTY.Tclass ? (TypeClass)tb : null;
                                if ((!(tc != null) || !(tc.sym.isInterfaceDeclaration() != null)))
                                {
                                    if (!pequals((b).type, Type.terror))
                                        idec.error(new BytePtr("base type must be `interface`, not `%s`"), (b).type.toChars());
                                    (idec.baseclasses).remove(i);
                                    continue;
                                }
                                {
                                    int j = 0;
                                    for (; j < i;j++){
                                        BaseClass b2 = (idec.baseclasses).get(j);
                                        if (pequals((b2).sym, tc.sym))
                                        {
                                            idec.error(new BytePtr("inherits from duplicate interface `%s`"), (b2).sym.toChars());
                                            (idec.baseclasses).remove(i);
                                            continue;
                                        }
                                    }
                                }
                                if ((pequals(tc.sym, idec) || idec.isBaseOf2(tc.sym)))
                                {
                                    idec.error(new BytePtr("circular inheritance of interface"));
                                    (idec.baseclasses).remove(i);
                                    continue;
                                }
                                if (tc.sym.isDeprecated())
                                {
                                    if (!(idec.isDeprecated()))
                                    {
                                        idec.isdeprecated = true;
                                        tc.checkDeprecated(idec.loc, this.sc);
                                    }
                                }
                                (b).sym = tc.sym;
                                if (tc.sym.baseok < Baseok.done)
                                    Function0<Void> __dgliteral4 = new Function0<Void>(){
                                        public Void invoke(){
                                            dsymbolSemantic(tc.sym, null);
                                        }
                                    };
                                    resolveBaseVoid.invoke(__dgliteral4);
                                if (tc.sym.baseok < Baseok.done)
                                {
                                    if (tc.sym._scope != null)
                                        dmodule.Module.addDeferredSemantic(tc.sym);
                                    idec.baseok = Baseok.none;
                                }
                                i++;
                            }
                        }
                        if (idec.baseok == Baseok.none)
                        {
                            idec._scope = scx != null ? scx : (this.sc).copy();
                            (idec._scope).setNoFree();
                            dmodule.Module.addDeferredSemantic(idec);
                            return ;
                        }
                        idec.baseok = Baseok.done;
                        idec.interfaces = (idec.baseclasses).tdata().slice(0,(idec.baseclasses).length).copy();
                        {
                            Slice<BaseClass> __r1177 = idec.interfaces.copy();
                            int __key1178 = 0;
                            for (; __key1178 < __r1177.getLength();__key1178 += 1) {
                                BaseClass b = __r1177.get(__key1178);
                                if ((b).sym.isCOMinterface())
                                    idec.com = true;
                                if ((b).sym.isCPPinterface())
                                    idec.classKind = ClassKind.cpp;
                            }
                        }
                        this.interfaceSemantic(idec);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lancestorsdone:*/
                if (idec.members == null)
                {
                    idec.semanticRun = PASS.semanticdone;
                    return ;
                }
                if (!(idec.symtab != null))
                    idec.symtab = new DsymbolTable();
                {
                    int i = 0;
                    for (; i < (idec.baseclasses).length;i++){
                        BaseClass b = (idec.baseclasses).get(i);
                        Type tb = (b).type.toBasetype();
                        TypeClass tc = tb.isTypeClass();
                        if (tc.sym.semanticRun < PASS.semanticdone)
                        {
                            idec._scope = scx != null ? scx : (this.sc).copy();
                            (idec._scope).setNoFree();
                            if (tc.sym._scope != null)
                                dmodule.Module.addDeferredSemantic(tc.sym);
                            dmodule.Module.addDeferredSemantic(idec);
                            return ;
                        }
                    }
                }
                if (idec.baseok == Baseok.done)
                {
                    idec.baseok = Baseok.semanticdone;
                    objc().setMetaclass(idec, this.sc);
                    if ((idec.vtblOffset()) != 0)
                        idec.vtbl.push(idec);
                    {
                        Slice<BaseClass> __r1180 = idec.interfaces.copy();
                        int __key1179 = 0;
                    L_outer8:
                        for (; __key1179 < __r1180.getLength();__key1179 += 1) {
                            BaseClass b = __r1180.get(__key1179);
                            int i = __key1179;
                            try {
                                {
                                    int k = 0;
                                L_outer9:
                                    for (; k < i;k++){
                                        if (b == idec.interfaces.get(k))
                                            /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                if (((b).sym.vtblOffset()) != 0)
                                {
                                    int d = (b).sym.vtbl.length;
                                    if (d > 1)
                                    {
                                        idec.vtbl.pushSlice((b).sym.vtbl.opSlice(1, d));
                                    }
                                }
                                else
                                {
                                    idec.vtbl.append((b).sym.vtbl);
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*Lcontinue:*/
                        }
                    }
                }
                Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        s.addMember(sc, idec);
                        return null;
                    }
                };
                foreachDsymbol(idec.members, __lambda5);
                Scope sc2 = idec.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        s.setScope(sc2);
                        return null;
                    }
                };
                foreachDsymbol(idec.members, __lambda6);
                Function1<Dsymbol,Void> __lambda7 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        s.importAll(sc2);
                        return null;
                    }
                };
                foreachDsymbol(idec.members, __lambda7);
                Function1<Dsymbol,Void> __lambda8 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s){
                        dsymbolSemantic(s, sc2);
                        return null;
                    }
                };
                foreachDsymbol(idec.members, __lambda8);
                dmodule.Module.dprogress++;
                idec.semanticRun = PASS.semanticdone;
                (sc2).pop();
                if (global.errors != errors)
                {
                    idec.type = Type.terror;
                }
                assert(((idec.type.ty & 0xFF) != ENUMTY.Tclass || pequals(((TypeClass)idec.type).sym, idec)));
                if ((idec.storage_class & 524288L) != 0)
                    deprecation(idec.loc, new BytePtr("`scope` as a type constraint is deprecated.  Use `scope` at the usage site."));
            }
            finally {
            }
        }


        public DsymbolSemanticVisitor() {}

        public DsymbolSemanticVisitor copy() {
            DsymbolSemanticVisitor that = new DsymbolSemanticVisitor();
            that.sc = this.sc;
            return that;
        }
    }
    public static void templateInstanceSemantic(TemplateInstance tempinst, Scope sc, DArray<Expression> fargs) {
        if (tempinst.inst != null)
        {
            return ;
        }
        if (tempinst.semanticRun != PASS.init)
        {
            Ungag ungag = ungag = new Ungag(global.gag);
            try {
                if (!(tempinst.gagged))
                    global.gag = 0;
                tempinst.error(tempinst.loc, new BytePtr("recursive template expansion"));
                if (tempinst.gagged)
                    tempinst.semanticRun = PASS.init;
                else
                    tempinst.inst = tempinst;
                tempinst.errors = true;
                return ;
            }
            finally {
            }
        }
        tempinst.tinst = (sc).tinst;
        tempinst.minst = (sc).minst;
        if (((!(tempinst.tinst != null) && (sc).func != null) && (sc).func.inNonRoot()))
        {
            tempinst.minst = null;
        }
        tempinst.gagged = global.gag > 0;
        tempinst.semanticRun = PASS.semantic;
        if (((!(tempinst.findTempDecl(sc, null)) || !(tempinst.semanticTiargs(sc))) || !(tempinst.findBestMatch(sc, fargs))))
        {
        /*Lerror:*/
            if (tempinst.gagged)
            {
                tempinst.semanticRun = PASS.init;
            }
            else
                tempinst.inst = tempinst;
            tempinst.errors = true;
            return ;
        }
        TemplateDeclaration tempdecl = tempinst.tempdecl.isTemplateDeclaration();
        assert(tempdecl != null);
        if (tempdecl.ismixin)
        {
            tempinst.error(new BytePtr("mixin templates are not regular templates"));
            /*goto Lerror*/throw Dispatch0.INSTANCE;
        }
        tempinst.hasNestedArgs(tempinst.tiargs, tempdecl.isstatic);
        if (tempinst.errors)
            /*goto Lerror*/throw Dispatch0.INSTANCE;
        tempinst.namespace = tempdecl.namespace;
        tempinst.inst = tempdecl.findExistingInstance(tempinst, fargs);
        TemplateInstance errinst = null;
        if (!(tempinst.inst != null))
        {
        }
        else if (((tempinst.inst.gagged && !(tempinst.gagged)) && tempinst.inst.errors))
        {
            errinst = tempinst.inst;
        }
        else
        {
            tempinst.parent = tempinst.inst.parent;
            tempinst.errors = tempinst.inst.errors;
            global.errors += (tempinst.errors ? 1 : 0);
            global.gaggedErrors += (tempinst.errors ? 1 : 0);
            if (tempinst.inst.gagged)
            {
                tempinst.inst.gagged = tempinst.gagged;
            }
            tempinst.tnext = tempinst.inst.tnext;
            tempinst.inst.tnext = tempinst;
            if (((tempinst.minst != null && tempinst.minst.isRoot()) && !((tempinst.inst.minst != null && tempinst.inst.minst.isRoot()))))
            {
                dmodule.Module mi = tempinst.minst;
                TemplateInstance ti = tempinst.tinst;
                tempinst.minst = tempinst.inst.minst;
                tempinst.tinst = tempinst.inst.tinst;
                tempinst.inst.minst = mi;
                tempinst.inst.tinst = ti;
                if (tempinst.minst != null)
                {
                    tempinst.inst.appendToModuleMember();
                }
            }
            if (tempinst.minst != null)
                tempinst.minst.aimports.append(tempinst.inst.importedModules);
            return ;
        }
        int errorsave = global.errors;
        tempinst.inst = tempinst;
        tempinst.parent = tempinst.enclosing != null ? tempinst.enclosing : tempdecl.parent;
        TemplateInstance tempdecl_instance_idx = tempdecl.addInstance(tempinst);
        DArray<Dsymbol> target_symbol_list = tempinst.appendToModuleMember();
        int target_symbol_list_idx = target_symbol_list != null ? (target_symbol_list).length - 1 : 0;
        tempinst.members = Dsymbol.arraySyntaxCopy(tempdecl.members);
        {
            int i = 0;
            for (; i < (tempdecl.parameters).length;i++){
                if ((tempdecl.parameters).get(i).isTemplateThisParameter() == null)
                    continue;
                Type t = isType((tempinst.tiargs).get(i));
                assert(t != null);
                {
                    long stc = ModToStc((t.mod & 0xFF));
                    if ((stc) != 0)
                    {
                        DArray<Dsymbol> s = new DArray<Dsymbol>();
                        (s).push(new StorageClassDeclaration(stc, tempinst.members));
                        tempinst.members = s;
                    }
                }
                break;
            }
        }
        Scope _scope = tempdecl._scope;
        if (tempdecl.semanticRun == PASS.init)
        {
            tempinst.error(new BytePtr("template instantiation `%s` forward references template declaration `%s`"), tempinst.toChars(), tempdecl.toChars());
            return ;
        }
        tempinst.argsym = new ScopeDsymbol();
        tempinst.argsym.parent = (_scope).parent;
        _scope = (_scope).push(tempinst.argsym);
        (_scope).tinst = tempinst;
        (_scope).minst = tempinst.minst;
        Scope paramscope = (_scope).push();
        (paramscope).stc = 0L;
        (paramscope).protection = new Prot(Prot.Kind.public_).copy();
        tempinst.declareParameters(paramscope);
        (paramscope).pop();
        tempinst.symtab = new DsymbolTable();
        Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s){
                s.addMember(_scope, tempinst);
            }
        };
        foreachDsymbol(tempinst.members, __lambda4);
        if (((tempinst.members).length) != 0)
        {
            Ref<Dsymbol> s = ref(null);
            if ((Dsymbol.oneMembers(tempinst.members, ptr(s), tempdecl.ident) && s.value != null))
            {
                tempinst.aliasdecl = s.value;
            }
        }
        if ((fargs != null && tempinst.aliasdecl != null))
        {
            {
                FuncDeclaration fd = tempinst.aliasdecl.isFuncDeclaration();
                if (fd != null)
                {
                    if (fd.type != null)
                        {
                            TypeFunction tf = fd.type.isTypeFunction();
                            if (tf != null)
                                tf.fargs = fargs;
                        }
                }
            }
        }
        Scope sc2 = null;
        sc2 = (_scope).push(tempinst);
        (sc2).parent = tempinst;
        (sc2).tinst = tempinst;
        (sc2).minst = tempinst.minst;
        tempinst.tryExpandMembers(sc2);
        tempinst.semanticRun = PASS.semanticdone;
        if (((tempinst.members).length) != 0)
        {
            Ref<Dsymbol> s = ref(null);
            if ((Dsymbol.oneMembers(tempinst.members, ptr(s), tempdecl.ident) && s.value != null))
            {
                if ((!(tempinst.aliasdecl != null) || !pequals(tempinst.aliasdecl, s.value)))
                {
                    tempinst.aliasdecl = s.value;
                }
            }
        }
        try {
            if (global.errors != errorsave)
                /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            {
                boolean found_deferred_ad = false;
                {
                    int i = 0;
                    for (; i < dmodule.Module.deferred.length;i++){
                        Dsymbol sd = dmodule.Module.deferred.get(i);
                        AggregateDeclaration ad = sd.isAggregateDeclaration();
                        if (((ad != null && ad.parent != null) && ad.parent.isTemplateInstance() != null))
                        {
                            found_deferred_ad = true;
                            if (pequals(ad.parent, tempinst))
                            {
                                ad.deferred = tempinst;
                                break;
                            }
                        }
                    }
                }
                if ((found_deferred_ad || (dmodule.Module.deferred.length) != 0))
                    /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            }
            {
                semantic2(tempinst, sc2);
            }
            if (global.errors != errorsave)
                /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            if ((((sc).func != null || ((sc).flags & 65536) != 0) && !(tempinst.tinst != null)))
            {
                DArray<TemplateInstance> deferred = new DArray<TemplateInstance>();
                try {
                    tempinst.deferred = deferred;
                    tempinst.trySemantic3(sc2);
                    {
                        int i = 0;
                        for (; i < deferred.length;i++){
                            semantic3(deferred.get(i), null);
                        }
                    }
                    tempinst.deferred = null;
                }
                finally {
                }
            }
            else if (tempinst.tinst != null)
            {
                boolean doSemantic3 = false;
                FuncDeclaration fd = null;
                if (tempinst.aliasdecl != null)
                    fd = tempinst.aliasdecl.toAlias2().isFuncDeclaration();
                if (fd != null)
                {
                    FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                    if ((fld != null && (fld.tok & 0xFF) == 0))
                        doSemantic3 = true;
                    else if ((sc).func != null)
                        doSemantic3 = true;
                }
                else if ((sc).func != null)
                {
                    {
                        Slice<RootObject> __r1182 = tempinst.tdtypes.opSlice().copy();
                        int __key1183 = 0;
                        for (; __key1183 < __r1182.getLength();__key1183 += 1) {
                            RootObject oarg = __r1182.get(__key1183);
                            Dsymbol s = getDsymbol(oarg);
                            if (!(s != null))
                                continue;
                            {
                                TemplateDeclaration td = s.isTemplateDeclaration();
                                if (td != null)
                                {
                                    if (!(td.literal))
                                        continue;
                                    assert((td.members != null && (td.members).length == 1));
                                    s = (td.members).get(0);
                                }
                            }
                            {
                                FuncLiteralDeclaration fld = s.isFuncLiteralDeclaration();
                                if (fld != null)
                                {
                                    if ((fld.tok & 0xFF) == 0)
                                    {
                                        doSemantic3 = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (doSemantic3)
                    tempinst.trySemantic3(sc2);
                TemplateInstance ti = tempinst.tinst;
                int nest = 0;
                for (; ((ti != null && ti.deferred == null) && ti.tinst != null);){
                    ti = ti.tinst;
                    if ((nest += 1) > 500)
                    {
                        global.gag = 0;
                        tempinst.error(new BytePtr("recursive expansion"));
                        fatal();
                    }
                }
                if ((ti != null && ti.deferred != null))
                {
                    {
                        int i = 0;
                        for (; ;i++){
                            if (i == (ti.deferred).length)
                            {
                                (ti.deferred).push(tempinst);
                                break;
                            }
                            if (pequals((ti.deferred).get(i), tempinst))
                                break;
                        }
                    }
                }
            }
            if (tempinst.aliasdecl != null)
            {
                tempinst.aliasdecl = tempinst.aliasdecl.toAlias2();
            }
        }
        catch(Dispatch0 __d){}
    /*Laftersemantic:*/
        (sc2).pop();
        (_scope).pop();
        if (global.errors != errorsave)
        {
            if (!(tempinst.errors))
            {
                if (!(tempdecl.literal))
                    tempinst.error(tempinst.loc, new BytePtr("error instantiating"));
                if (tempinst.tinst != null)
                    tempinst.tinst.printInstantiationTrace();
            }
            tempinst.errors = true;
            if (tempinst.gagged)
            {
                tempdecl.removeInstance(tempdecl_instance_idx);
                if (target_symbol_list != null)
                {
                    assert(pequals((target_symbol_list).get(target_symbol_list_idx), tempinst));
                    (target_symbol_list).remove(target_symbol_list_idx);
                    tempinst.memberOf = null;
                }
                tempinst.semanticRun = PASS.init;
                tempinst.inst = null;
                tempinst.symtab = null;
            }
        }
        else if (errinst != null)
        {
            assert(errinst.errors);
            TemplateInstanceBox ti1 = ti1 = new TemplateInstanceBox(errinst);
            tempdecl.instances.remove(ti1);
            TemplateInstanceBox ti2 = ti2 = new TemplateInstanceBox(tempinst);
            tempdecl.instances.set(ti2, __aaval1184);
        }
    }

    public static void aliasSemantic(AliasDeclaration ds, Scope sc) {
        if ((ds.type != null && (ds.type.ty & 0xFF) == ENUMTY.TTraits))
        {
            TypeTraits tt = (TypeTraits)ds.type;
            tt.inAliasDeclaration = true;
            {
                Type t = typeSemantic(tt, tt.loc, sc);
                if (t != null)
                    ds.type = t;
                else if (tt.sym != null)
                    ds.aliassym = tt.sym;
            }
            tt.inAliasDeclaration = false;
        }
        if (ds.aliassym != null)
        {
            FuncLiteralDeclaration fd = ds.aliassym.isFuncLiteralDeclaration();
            TemplateDeclaration td = ds.aliassym.isTemplateDeclaration();
            if ((fd != null || (td != null && td.literal)))
            {
                if ((fd != null && fd.semanticRun >= PASS.semanticdone))
                    return ;
                Expression e = new FuncExp(ds.loc, ds.aliassym);
                e = expressionSemantic(e, sc);
                if ((e.op & 0xFF) == 161)
                {
                    FuncExp fe = (FuncExp)e;
                    ds.aliassym = fe.td != null ? fe.td : fe.fd;
                }
                else
                {
                    ds.aliassym = null;
                    ds.type = Type.terror;
                }
                return ;
            }
            if (ds.aliassym.isTemplateInstance() != null)
                dsymbolSemantic(ds.aliassym, sc);
            return ;
        }
        ds.inuse = 1;
        int errors = global.errors;
        Type oldtype = ds.type;
        Ungag ungag = ungag = new Ungag(global.gag);
        try {
            if ((((ds.parent != null && (global.gag) != 0) && !(ds.isInstantiated() != null)) && !(ds.toParent2().isFuncDeclaration() != null)))
            {
                global.gag = 0;
            }
            if (((ds.type.ty & 0xFF) == ENUMTY.Tident && !(ds._import != null)))
            {
                TypeIdentifier tident = (TypeIdentifier)ds.type;
                if ((tident.ident == ds.ident && !((tident.idents.length) != 0)))
                {
                    error(ds.loc, new BytePtr("`alias %s = %s;` cannot alias itself, use a qualified name to create an overload set"), ds.ident.toChars(), tident.ident.toChars());
                    ds.type = Type.terror;
                }
            }
            Ref<Dsymbol> s = ref(ds.type.toDsymbol(sc));
            if (errors != global.errors)
            {
                s.value = null;
                ds.type = Type.terror;
            }
            if ((s.value != null && pequals(s.value, ds)))
            {
                ds.error(new BytePtr("cannot resolve"));
                s.value = null;
                ds.type = Type.terror;
            }
            if ((!(s.value != null) || !(s.value.isEnumMember() != null)))
            {
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Scope sc2 = sc;
                if ((ds.storage_class & 4535588225024L) != 0)
                {
                    sc2 = (sc).push();
                    (sc2).stc |= ds.storage_class & 4536125095936L;
                }
                ds.type = ds.type.addSTC(ds.storage_class);
                resolve(ds.type, ds.loc, sc2, ptr(e), ptr(t), ptr(s), false);
                if (sc2 != sc)
                    (sc2).pop();
                if (e.value != null)
                {
                    s.value = getDsymbol(e.value);
                    if (!(s.value != null))
                    {
                        if ((e.value.op & 0xFF) != 127)
                            ds.error(new BytePtr("cannot alias an expression `%s`"), e.value.toChars());
                        t.value = Type.terror;
                    }
                }
                ds.type = t.value;
            }
            if (pequals(s.value, ds))
            {
                assert((global.errors) != 0);
                ds.type = Type.terror;
                s.value = null;
            }
            if (!(s.value != null))
            {
                ds.type = typeSemantic(ds.type, ds.loc, sc);
                ds.aliassym = null;
            }
            else
            {
                ds.type = null;
                ds.aliassym = s.value;
            }
            if (((global.gag) != 0 && errors != global.errors))
            {
                ds.type = oldtype;
                ds.aliassym = null;
            }
            ds.inuse = 0;
            ds.semanticRun = PASS.semanticdone;
            {
                Dsymbol sx = ds.overnext;
                if (sx != null)
                {
                    ds.overnext = null;
                    if (!(ds.overloadInsert(sx)))
                        ScopeDsymbol.multiplyDefined(Loc.initial, sx, ds);
                }
            }
        }
        finally {
        }
    }

}
