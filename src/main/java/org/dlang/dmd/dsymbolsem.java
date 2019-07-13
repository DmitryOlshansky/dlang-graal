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
    static int visitnest = 0;
    static boolean funcDeclarationSemanticprintedMain = false;

    static boolean LOG = false;
    public static FuncDeclaration buildPostBlit(StructDeclaration sd, Ptr<Scope> sc) {
        if (sd.isUnionDeclaration() != null)
            return null;
        Ref<Long> stc = ref(4406737108992L);
        Loc declLoc = sd.postblits.length.value != 0 ? sd.postblits.get(0).loc.value : sd.loc.value.copy();
        Loc loc = new Loc();
        {
            int i = 0;
            for (; (i < sd.postblits.length.value);i++){
                stc.value |= sd.postblits.get(i).storage_class.value & 137438953472L;
            }
        }
        Slice<VarDeclaration> fieldsToDestroy = new Slice<VarDeclaration>().copy();
        Ptr<DArray<Statement>> postblitCalls = refPtr(new DArray<Statement>());
        {
            int i = 0;
            for (; (i < sd.fields.length.value) && ((stc.value & 137438953472L) == 0);i++){
                VarDeclaration structField = sd.fields.get(i);
                if ((structField.storage_class.value & 2097152L) != 0)
                    continue;
                if (structField.overlapped.value)
                    continue;
                Type tv = structField.type.value.baseElemOf();
                if (((tv.ty.value & 0xFF) != ENUMTY.Tstruct))
                    continue;
                StructDeclaration sdv = ((TypeStruct)tv).sym.value;
                if (sdv.postblit.value == null)
                    continue;
                assert(sdv.isUnionDeclaration() == null);
                if ((fieldsToDestroy.getLength() > 0) && !((TypeFunction)sdv.postblit.value.type.value).isnothrow.value)
                {
                    Slice<Expression> dtorCalls = new Slice<Expression>().copy();
                    {
                        Slice<VarDeclaration> __r1136 = fieldsToDestroy.copy();
                        int __key1137 = 0;
                        for (; (__key1137 < __r1136.getLength());__key1137 += 1) {
                            VarDeclaration sf = __r1136.get(__key1137);
                            Expression ex = null;
                            tv = sf.type.value.toBasetype();
                            if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                            {
                                ex = new ThisExp(loc);
                                ex = new DotVarExp(loc, ex, sf, true);
                                ex = new AddrExp(loc, ex);
                                ex = new CastExp(loc, ex, sf.type.value.mutableOf().pointerTo());
                                ex = new PtrExp(loc, ex);
                                if ((stc.value & 8589934592L) != 0)
                                    stc.value = stc.value & -8589934593L | 17179869184L;
                                StructDeclaration sfv = ((TypeStruct)sf.type.value.baseElemOf()).sym.value;
                                ex = new DotVarExp(loc, ex, sfv.dtor.value, false);
                                ex = new CallExp(loc, ex);
                                dtorCalls.append(ex);
                            }
                            else
                            {
                                int length = tv.numberOfElems(loc);
                                ex = new ThisExp(loc);
                                ex = new DotVarExp(loc, ex, sf, true);
                                ex = new DotIdExp(loc, ex, Id.ptr.value);
                                ex = new CastExp(loc, ex, sdv.type.value.pointerTo());
                                if ((stc.value & 8589934592L) != 0)
                                    stc.value = stc.value & -8589934593L | 17179869184L;
                                SliceExp se = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t.value), new IntegerExp(loc, (long)length, Type.tsize_t.value));
                                se.upperIsInBounds = true;
                                se.lowerIsLessThanUpper = true;
                                ex = new CallExp(loc, new IdentifierExp(loc, Id.__ArrayDtor), se);
                                dtorCalls.append(ex);
                            }
                        }
                    }
                    fieldsToDestroy = slice(new VarDeclaration[]{}).copy();
                    Ptr<DArray<Statement>> dtors = refPtr(new DArray<Statement>());
                    {
                        Slice<Expression> __r1138 = dtorCalls.copy();
                        int __key1139 = __r1138.getLength();
                        for (; __key1139-- != 0;) {
                            Expression dc = __r1138.get(__key1139);
                            (dtors.get()).push(new ExpStatement(loc, dc));
                        }
                    }
                    (postblitCalls.get()).push(new ScopeGuardStatement(loc, TOK.onScopeFailure, new CompoundStatement(loc, dtors)));
                }
                sdv.postblit.value.functionSemantic();
                stc.value = mergeFuncAttrs(stc.value, sdv.postblit.value);
                stc.value = mergeFuncAttrs(stc.value, sdv.dtor.value);
                if ((stc.value & 137438953472L) != 0)
                {
                    (postblitCalls.get()).setDim(0);
                    break;
                }
                Expression ex = null;
                tv = structField.type.value.toBasetype();
                if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, structField, true);
                    ex = new AddrExp(loc, ex);
                    ex = new CastExp(loc, ex, structField.type.value.mutableOf().pointerTo());
                    ex = new PtrExp(loc, ex);
                    if ((stc.value & 8589934592L) != 0)
                        stc.value = stc.value & -8589934593L | 17179869184L;
                    ex = new DotVarExp(loc, ex, sdv.postblit.value, false);
                    ex = new CallExp(loc, ex);
                }
                else
                {
                    int length = tv.numberOfElems(loc);
                    if ((length == 0))
                        continue;
                    ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, structField, true);
                    ex = new DotIdExp(loc, ex, Id.ptr.value);
                    ex = new CastExp(loc, ex, sdv.type.value.pointerTo());
                    if ((stc.value & 8589934592L) != 0)
                        stc.value = stc.value & -8589934593L | 17179869184L;
                    SliceExp se = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t.value), new IntegerExp(loc, (long)length, Type.tsize_t.value));
                    se.upperIsInBounds = true;
                    se.lowerIsLessThanUpper = true;
                    ex = new CallExp(loc, new IdentifierExp(loc, Id.__ArrayPostblit), se);
                }
                (postblitCalls.get()).push(new ExpStatement(loc, ex));
                if (sdv.dtor.value != null)
                {
                    sdv.dtor.value.functionSemantic();
                    fieldsToDestroy.append(structField);
                }
            }
        }
        Function0<Void> checkShared = new Function0<Void>(){
            public Void invoke() {
                if (sd.type.value.isShared())
                    stc.value |= 536870912L;
            }
        };
        if (((postblitCalls.get()).length.value != 0) || ((stc.value & 137438953472L) != 0))
        {
            checkShared.invoke();
            PostBlitDeclaration dd = new PostBlitDeclaration(declLoc, Loc.initial.value, stc.value, Id.__fieldPostblit);
            dd.generated = true;
            dd.storage_class.value |= 70368744177664L;
            dd.fbody.value = (stc.value & 137438953472L) != 0 ? null : new CompoundStatement(loc, postblitCalls);
            sd.postblits.shift(dd);
            (sd.members.value.get()).push(dd);
            dsymbolSemantic(dd, sc);
        }
        FuncDeclaration xpostblit = null;
        switch (sd.postblits.length.value)
        {
            case 0:
                break;
            case 1:
                xpostblit = sd.postblits.get(0);
                break;
            default:
            Expression e = null;
            stc.value = 4406737108992L;
            {
                int i = 0;
                for (; (i < sd.postblits.length.value);i++){
                    FuncDeclaration fd = sd.postblits.get(i);
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
            PostBlitDeclaration dd = new PostBlitDeclaration(declLoc, Loc.initial.value, stc.value, Id.__aggrPostblit);
            dd.generated = true;
            dd.storage_class.value |= 70368744177664L;
            dd.fbody.value = new ExpStatement(loc, e);
            (sd.members.value.get()).push(dd);
            dsymbolSemantic(dd, sc);
            xpostblit = dd;
            break;
        }
        if (xpostblit != null)
        {
            AliasDeclaration _alias = new AliasDeclaration(Loc.initial.value, Id.__xpostblit.value, xpostblit);
            dsymbolSemantic(_alias, sc);
            (sd.members.value.get()).push(_alias);
            _alias.addMember(sc, sd);
        }
        return xpostblit;
    }

    public static CtorDeclaration generateCopyCtorDeclaration(StructDeclaration sd, long paramStc, long funcStc) {
        Ptr<DArray<Parameter>> fparams = refPtr(new DArray<Parameter>());
        Type structType = sd.type.value;
        (fparams.get()).push(new Parameter(paramStc | 2097152L | 17592186044416L | 524288L, structType, Id.p.value, null, null));
        ParameterList pList = new ParameterList(fparams, VarArg.none).copy();
        TypeFunction tf = new TypeFunction(pList, structType, LINK.d, 2097152L);
        CtorDeclaration ccd = new CtorDeclaration(sd.loc.value, Loc.initial.value, 2097152L, tf, true);
        ccd.storage_class.value |= funcStc;
        ccd.storage_class.value |= 70368744177664L;
        ccd.generated = true;
        return ccd;
    }

    public static Statement generateCopyCtorBody(StructDeclaration sd) {
        Loc loc = new Loc();
        Expression e = null;
        {
            Slice<VarDeclaration> __r1140 = sd.fields.opSlice().copy();
            int __key1141 = 0;
            for (; (__key1141 < __r1140.getLength());__key1141 += 1) {
                VarDeclaration v = __r1140.get(__key1141);
                AssignExp ec = new AssignExp(loc, new DotVarExp(loc, new ThisExp(loc), v, true), new DotVarExp(loc, new IdentifierExp(loc, Id.p.value), v, true));
                e = Expression.combine(e, (Expression)ec);
            }
        }
        Statement s1 = new ExpStatement(loc, e);
        return new CompoundStatement(loc, slice(new Statement[]{s1}));
    }

    public static boolean buildCopyCtor(StructDeclaration sd, Ptr<Scope> sc) {
        if (global.errors.value != 0)
            return false;
        boolean hasPostblit = false;
        if (sd.postblit.value != null)
            hasPostblit = true;
        Dsymbol ctor = sd.search(sd.loc.value, Id.ctor.value, 8);
        CtorDeclaration cpCtor = null;
        CtorDeclaration rvalueCtor = null;
        if (ctor != null)
        {
            if (ctor.isOverloadSet() != null)
                return false;
            {
                TemplateDeclaration td = ctor.isTemplateDeclaration();
                if ((td) != null)
                    ctor = td.funcroot;
            }
        }
        try {
            if (ctor == null)
                /*goto LcheckFields*/throw Dispatch0.INSTANCE;
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    if (s.isTemplateDeclaration() != null)
                        return 0;
                    CtorDeclaration ctorDecl = s.isCtorDeclaration();
                    assert(ctorDecl != null);
                    if (ctorDecl.isCpCtor)
                    {
                        if (cpCtor == null)
                            cpCtor = ctorDecl;
                        return 0;
                    }
                    TypeFunction tf = ctorDecl.type.value.toTypeFunction();
                    int dim = Parameter.dim(tf.parameterList.parameters.value);
                    if ((dim == 1))
                    {
                        Parameter param = Parameter.getNth(tf.parameterList.parameters.value, 0, null);
                        if ((pequals(param.type.value.mutableOf().unSharedOf(), sd.type.value.mutableOf().unSharedOf())))
                        {
                            rvalueCtor = ctorDecl;
                        }
                    }
                    return 0;
                }
            };
            overloadApply(ctor, __lambda3, null);
            if ((cpCtor != null) && (rvalueCtor != null))
            {
                error(sd.loc.value, new BytePtr("`struct %s` may not define both a rvalue constructor and a copy constructor"), sd.toChars());
                errorSupplemental(rvalueCtor.loc.value, new BytePtr("rvalue constructor defined here"));
                errorSupplemental(cpCtor.loc.value, new BytePtr("copy constructor defined here"));
                return true;
            }
            else if (cpCtor != null)
            {
                return !hasPostblit;
            }
        }
        catch(Dispatch0 __d){}
    /*LcheckFields:*/
        VarDeclaration fieldWithCpCtor = null;
        {
            Slice<VarDeclaration> __r1142 = sd.fields.opSlice().copy();
            int __key1143 = 0;
            for (; (__key1143 < __r1142.getLength());__key1143 += 1) {
                VarDeclaration v = __r1142.get(__key1143);
                if ((v.storage_class.value & 2097152L) != 0)
                    continue;
                if (v.overlapped.value)
                    continue;
                TypeStruct ts = v.type.value.baseElemOf().isTypeStruct();
                if (ts == null)
                    continue;
                if (ts.sym.value.hasCopyCtor)
                {
                    fieldWithCpCtor = v;
                    break;
                }
            }
        }
        if ((fieldWithCpCtor != null) && (rvalueCtor != null))
        {
            error(sd.loc.value, new BytePtr("`struct %s` may not define a rvalue constructor and have fields with copy constructors"), sd.toChars());
            errorSupplemental(rvalueCtor.loc.value, new BytePtr("rvalue constructor defined here"));
            errorSupplemental(fieldWithCpCtor.loc.value, new BytePtr("field with copy constructor defined here"));
            return false;
        }
        else if (fieldWithCpCtor == null)
            return false;
        if (hasPostblit)
            return false;
        byte paramMod = (byte)8;
        byte funcMod = (byte)8;
        CtorDeclaration ccd = generateCopyCtorDeclaration(sd, ModToStc(8), ModToStc(8));
        Statement copyCtorBody = generateCopyCtorBody(sd);
        ccd.fbody.value = copyCtorBody;
        (sd.members.value.get()).push(ccd);
        ccd.addMember(sc, sd);
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc.value = 0L;
        (sc2.get()).linkage.value = LINK.d;
        dsymbolSemantic(ccd, sc2);
        semantic2(ccd, sc2);
        semantic3(ccd, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            ccd.storage_class.value |= 137438953472L;
            ccd.fbody.value = null;
        }
        return true;
    }

    public static int setMangleOverride(Dsymbol s, ByteSlice sym) {
        if ((s.isFuncDeclaration() != null) || (s.isVarDeclaration() != null))
        {
            s.isDeclaration().mangleOverride = sym.copy();
            return 1;
        }
        {
            AttribDeclaration ad = s.isAttribDeclaration();
            if ((ad) != null)
            {
                int nestedCount = 0;
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        nestedCount += setMangleOverride(s, sym);
                    }
                };
                foreachDsymbol(ad.include(null), __lambda3);
                return nestedCount;
            }
        }
        return 0;
    }

    public static void dsymbolSemantic(Dsymbol dsym, Ptr<Scope> sc) {
        DsymbolSemanticVisitor v = new DsymbolSemanticVisitor(sc);
        dsym.accept(v);
    }

    public static int getAlignment(AlignDeclaration ad, Ptr<Scope> sc) {
        if ((ad.salign != 0))
            return ad.salign;
        if (ad.ealign == null)
            return ad.salign = -1;
        sc = (sc.get()).startCTFE();
        ad.ealign = expressionSemantic(ad.ealign, sc);
        ad.ealign = resolveProperties(sc, ad.ealign);
        sc = (sc.get()).endCTFE();
        ad.ealign = ad.ealign.ctfeInterpret();
        if (((ad.ealign.op.value & 0xFF) == 127))
            return ad.salign = -1;
        Type tb = ad.ealign.type.value.toBasetype();
        long n = ad.ealign.toInteger();
        if ((n < 1L) || ((n & n - 1L) != 0) || (4294967295L < n) || !tb.isintegral())
        {
            error(ad.loc.value, new BytePtr("alignment must be an integer positive power of 2, not %s"), ad.ealign.toChars());
            return ad.salign = -1;
        }
        return ad.salign = (int)n;
    }

    public static BytePtr getMessage(DeprecatedDeclaration dd) {
        {
            Ptr<Scope> sc = dd._scope.value;
            if ((sc) != null)
            {
                dd._scope.value = null;
                sc = (sc.get()).startCTFE();
                dd.msg = expressionSemantic(dd.msg, sc);
                dd.msg = resolveProperties(sc, dd.msg);
                sc = (sc.get()).endCTFE();
                dd.msg = dd.msg.ctfeInterpret();
                {
                    StringExp se = dd.msg.toStringExp();
                    if ((se) != null)
                        dd.msgstr = pcopy((toBytePtr(se.toStringz())));
                    else
                        dd.msg.error(new BytePtr("compile time constant expected, not `%s`"), dd.msg.toChars());
                }
            }
        }
        return dd.msgstr;
    }

    public static boolean allowsContractWithoutBody(FuncDeclaration funcdecl) {
        assert(funcdecl.fbody.value == null);
        Dsymbol parent = funcdecl.toParent();
        InterfaceDeclaration id = parent.isInterfaceDeclaration();
        if (!funcdecl.isAbstract() && (funcdecl.fensures != null) || (funcdecl.frequires != null) && !((id != null) && funcdecl.isVirtual()))
        {
            ClassDeclaration cd = parent.isClassDeclaration();
            if (!((cd != null) && cd.isAbstract()))
                return false;
        }
        return true;
    }

    public static class DsymbolSemanticVisitor extends Visitor
    {
        public Ptr<Scope> sc = null;
        public  DsymbolSemanticVisitor(Ptr<Scope> sc) {
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
            if ((dsym.semanticRun.value != PASS.init))
                return ;
            if (dsym._scope.value != null)
            {
                this.sc = dsym._scope.value;
                dsym._scope.value = null;
            }
            if (this.sc == null)
                return ;
            dsym.semanticRun.value = PASS.semantic;
            Dsymbol p = (this.sc.get()).parent.value.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(dsym.loc.value, new BytePtr("alias this can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                return ;
            }
            assert(ad.members.value != null);
            Dsymbol s = ad.search(dsym.loc.value, dsym.ident, 8);
            if (s == null)
            {
                s = (this.sc.get()).search(dsym.loc.value, dsym.ident, null, 0);
                if (s != null)
                    error(dsym.loc.value, new BytePtr("`%s` is not a member of `%s`"), s.toChars(), ad.toChars());
                else
                    error(dsym.loc.value, new BytePtr("undefined identifier `%s`"), dsym.ident.toChars());
                return ;
            }
            if ((ad.aliasthis.value != null) && (!pequals(s, ad.aliasthis.value)))
            {
                error(dsym.loc.value, new BytePtr("there can be only one alias this"));
                return ;
            }
            ad.aliasthis.value = null;
            Dsymbol sx = s;
            if (sx.isAliasDeclaration() != null)
                sx = sx.toAlias();
            Declaration d = sx.isDeclaration();
            if ((d != null) && (d.isTupleDeclaration() == null))
            {
                if (d.type.value == null)
                    dsymbolSemantic(d, this.sc);
                Type t = d.type.value;
                assert(t != null);
                if ((ad.type.value.implicitConvTo(t) > MATCH.nomatch))
                {
                    error(dsym.loc.value, new BytePtr("alias this is not reachable as `%s` already converts to `%s`"), ad.toChars(), t.toChars());
                }
            }
            ad.aliasthis.value = s;
            dsym.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(AliasDeclaration dsym) {
            if ((dsym.semanticRun.value >= PASS.semanticdone))
                return ;
            assert((dsym.semanticRun.value <= PASS.semantic));
            dsym.storage_class.value |= (this.sc.get()).stc.value & 1024L;
            dsym.protection = (this.sc.get()).protection.value.copy();
            dsym.userAttribDecl = (this.sc.get()).userAttribDecl;
            if (((this.sc.get()).func.value == null) && dsym.inNonRoot())
                return ;
            aliasSemantic(dsym, this.sc);
        }

        public  void visit(VarDeclaration dsym) {
            if ((dsym.semanticRun.value >= PASS.semanticdone))
                return ;
            if ((this.sc != null) && ((this.sc.get()).inunion != null) && ((this.sc.get()).inunion.isAnonDeclaration() != null))
                dsym.overlapped.value = true;
            Ptr<Scope> scx = null;
            if (dsym._scope.value != null)
            {
                this.sc = dsym._scope.value;
                scx = this.sc;
                dsym._scope.value = null;
            }
            if (this.sc == null)
                return ;
            dsym.semanticRun.value = PASS.semantic;
            dsym.storage_class.value |= (this.sc.get()).stc.value & -665L;
            if (((dsym.storage_class.value & 2L) != 0) && (dsym._init.value != null))
                dsym.error(new BytePtr("extern symbols cannot have initializers"));
            dsym.userAttribDecl = (this.sc.get()).userAttribDecl;
            dsym.namespace = (this.sc.get()).namespace;
            AggregateDeclaration ad = dsym.isThis();
            if (ad != null)
                dsym.storage_class.value |= ad.storage_class & 2685403140L;
            int inferred = 0;
            if (dsym.type.value == null)
            {
                dsym.inuse.value++;
                boolean needctfe = (dsym.storage_class.value & 8388609L) != 0L;
                if (needctfe)
                    this.sc = (this.sc.get()).startCTFE();
                dsym._init.value = inferType(dsym._init.value, this.sc);
                dsym.type.value = initializerToExpression(dsym._init.value, null).type.value;
                if (needctfe)
                    this.sc = (this.sc.get()).endCTFE();
                dsym.inuse.value--;
                inferred = 1;
                dsym.storage_class.value &= -257L;
                dsym.originalType.value = dsym.type.value.syntaxCopy();
            }
            else
            {
                if (dsym.originalType.value == null)
                    dsym.originalType.value = dsym.type.value.syntaxCopy();
                Ptr<Scope> sc2 = (this.sc.get()).push();
                (sc2.get()).stc.value |= dsym.storage_class.value & 4462573780992L;
                dsym.inuse.value++;
                dsym.type.value = typeSemantic(dsym.type.value, dsym.loc.value, sc2);
                dsym.inuse.value--;
                (sc2.get()).pop();
            }
            if (((dsym.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                dsym.errors.value = true;
            dsym.type.value.checkDeprecated(dsym.loc.value, this.sc);
            dsym.linkage.value = (this.sc.get()).linkage.value;
            dsym.parent.value = (this.sc.get()).parent.value;
            dsym.protection = (this.sc.get()).protection.value.copy();
            dsym.alignment = (this.sc.get()).alignment();
            if ((dsym.alignment == -1))
                dsym.alignment = dsym.type.value.alignment();
            if (global.params.vcomplex)
                dsym.type.value.checkComplexTransition(dsym.loc.value, this.sc);
            if (((this.sc.get()).func.value != null) && ((this.sc.get()).intypeof.value == 0))
            {
                if (((dsym.storage_class.value & 1073741824L) != 0) && (dsym.isMember() == null))
                {
                    if ((this.sc.get()).func.value.setUnsafe())
                        dsym.error(new BytePtr("__gshared not allowed in safe functions; use shared"));
                }
            }
            Dsymbol parent = dsym.toParent();
            Type tb = dsym.type.value.toBasetype();
            Type tbn = tb.baseElemOf();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tvoid) && ((dsym.storage_class.value & 8192L) == 0))
            {
                if (inferred != 0)
                {
                    dsym.error(new BytePtr("type `%s` is inferred from initializer `%s`, and variables cannot be of type `void`"), dsym.type.value.toChars(), dsym._init.value.toChars());
                }
                else
                    dsym.error(new BytePtr("variables cannot be of type `void`"));
                dsym.type.value = Type.terror.value;
                tb = dsym.type.value;
            }
            if (((tb.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                dsym.error(new BytePtr("cannot be declared to be a function"));
                dsym.type.value = Type.terror.value;
                tb = dsym.type.value;
            }
            {
                TypeStruct ts = tb.isTypeStruct();
                if ((ts) != null)
                {
                    if (ts.sym.value.members.value == null)
                    {
                        dsym.error(new BytePtr("no definition of struct `%s`"), ts.toChars());
                    }
                }
            }
            if (((dsym.storage_class.value & 256L) != 0) && (inferred == 0))
                dsym.error(new BytePtr("storage class `auto` has no effect if type is not inferred, did you mean `scope`?"));
            {
                TypeTuple tt = tb.isTypeTuple();
                if ((tt) != null)
                {
                    int nelems = Parameter.dim(tt.arguments.value);
                    Expression ie = (dsym._init.value != null) && (dsym._init.value.isVoidInitializer() == null) ? initializerToExpression(dsym._init.value, null) : null;
                    if (ie != null)
                        ie = expressionSemantic(ie, this.sc);
                    try {
                        if ((nelems > 0) && (ie != null))
                        {
                            Ptr<DArray<Expression>> iexps = refPtr(new DArray<Expression>());
                            (iexps.get()).push(ie);
                            Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                            {
                                int pos = 0;
                            L_outer1:
                                for (; (pos < (iexps.get()).length.value);pos++){
                                    while(true) try {
                                    /*Lexpand1:*/
                                        Expression e = (iexps.get()).get(pos);
                                        Parameter arg = Parameter.getNth(tt.arguments.value, pos, null);
                                        arg.type.value = typeSemantic(arg.type.value, dsym.loc.value, this.sc);
                                        if ((!pequals(e, ie)))
                                        {
                                            if (((iexps.get()).length.value > nelems))
                                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                            if (e.type.value.implicitConvTo(arg.type.value) != 0)
                                                continue L_outer1;
                                        }
                                        if (((e.op.value & 0xFF) == 126))
                                        {
                                            TupleExp te = (TupleExp)e;
                                            if (((iexps.get()).length.value - 1 + (te.exps.value.get()).length.value > nelems))
                                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                            (iexps.get()).remove(pos);
                                            (iexps.get()).insert(pos, te.exps.value);
                                            iexps.get().set(pos, Expression.combine(te.e0.value, (iexps.get()).get(pos)));
                                            /*goto Lexpand1*/throw Dispatch0.INSTANCE;
                                        }
                                        else if (isAliasThisTuple(e) != null)
                                        {
                                            VarDeclaration v = copyToTemp(0L, new BytePtr("__tup"), e);
                                            dsymbolSemantic(v, this.sc);
                                            VarExp ve = new VarExp(dsym.loc.value, v, true);
                                            ve.type.value = e.type.value;
                                            (exps.get()).setDim(1);
                                            exps.get().set(0, ve);
                                            expandAliasThisTuples(exps, 0);
                                            {
                                                int u = 0;
                                            L_outer2:
                                                for (; (u < (exps.get()).length.value);u++){
                                                    while(true) try {
                                                    /*Lexpand2:*/
                                                        Expression ee = (exps.get()).get(u);
                                                        arg = Parameter.getNth(tt.arguments.value, pos + u, null);
                                                        arg.type.value = typeSemantic(arg.type.value, dsym.loc.value, this.sc);
                                                        int iexps_dim = (iexps.get()).length.value - 1 + (exps.get()).length.value;
                                                        if ((iexps_dim > nelems))
                                                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                                        if (ee.type.value.implicitConvTo(arg.type.value) != 0)
                                                            continue L_outer2;
                                                        if ((expandAliasThisTuples(exps, u) != -1))
                                                            /*goto Lexpand2*/throw Dispatch0.INSTANCE;
                                                        break;
                                                    } catch(Dispatch0 __d){}
                                                }
                                            }
                                            if ((!pequals((exps.get()).get(0), ve)))
                                            {
                                                Expression e0 = (exps.get()).get(0);
                                                exps.get().set(0, new CommaExp(dsym.loc.value, new DeclarationExp(dsym.loc.value, v), e0, true));
                                                (exps.get()).get(0).type.value = e0.type.value;
                                                (iexps.get()).remove(pos);
                                                (iexps.get()).insert(pos, exps);
                                                /*goto Lexpand1*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        break;
                                    } catch(Dispatch0 __d){}
                                }
                            }
                            if (((iexps.get()).length.value < nelems))
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            ie = new TupleExp(dsym._init.value.loc.value, iexps);
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lnomatch:*/
                    if ((ie != null) && ((ie.op.value & 0xFF) == 126))
                    {
                        TupleExp te = (TupleExp)ie;
                        int tedim = (te.exps.value.get()).length.value;
                        if ((tedim != nelems))
                        {
                            error(dsym.loc.value, new BytePtr("tuple of %d elements cannot be assigned to tuple of %d elements"), tedim, nelems);
                            {
                                int u = tedim;
                                for (; (u < nelems);u++) {
                                    (te.exps.value.get()).push(new ErrorExp());
                                }
                            }
                        }
                    }
                    Ptr<DArray<RootObject>> exps = refPtr(new DArray<RootObject>(nelems));
                    {
                        int i = 0;
                        for (; (i < nelems);i++){
                            Parameter arg = Parameter.getNth(tt.arguments.value, i, null);
                            OutBuffer buf = new OutBuffer();
                            try {
                                buf.printf(new BytePtr("__%s_field_%llu"), dsym.ident.value.toChars(), (long)i);
                                Identifier id = Identifier.idPool(buf.peekSlice());
                                Initializer ti = null;
                                if (ie != null)
                                {
                                    Expression einit = ie;
                                    if (((ie.op.value & 0xFF) == 126))
                                    {
                                        TupleExp te = (TupleExp)ie;
                                        einit = (te.exps.value.get()).get(i);
                                        if ((i == 0))
                                            einit = Expression.combine(te.e0.value, einit);
                                    }
                                    ti = new ExpInitializer(einit.loc.value, einit);
                                }
                                else
                                    ti = dsym._init.value != null ? syntaxCopy(dsym._init.value) : null;
                                long storage_class = 1099511627776L | dsym.storage_class.value;
                                if ((arg.storageClass.value & 32L) != 0)
                                    storage_class |= arg.storageClass.value;
                                VarDeclaration v = new VarDeclaration(dsym.loc.value, arg.type.value, id, ti, storage_class);
                                dsymbolSemantic(v, this.sc);
                                if ((this.sc.get()).scopesym.value != null)
                                {
                                    if ((this.sc.get()).scopesym.value.members.value != null)
                                        ((this.sc.get()).scopesym.value.members.value.get()).push(v);
                                }
                                Expression e = new DsymbolExp(dsym.loc.value, v, true);
                                exps.get().set(i, e);
                            }
                            finally {
                            }
                        }
                    }
                    TupleDeclaration v2 = new TupleDeclaration(dsym.loc.value, dsym.ident.value, exps);
                    v2.parent.value = dsym.parent.value;
                    v2.isexp = true;
                    dsym.aliassym.value = v2;
                    dsym.semanticRun.value = PASS.semanticdone;
                    return ;
                }
            }
            dsym.type.value = dsym.type.value.addStorageClass(dsym.storage_class.value);
            if (dsym.type.value.isConst())
            {
                dsym.storage_class.value |= 4L;
                if (dsym.type.value.isShared())
                    dsym.storage_class.value |= 536870912L;
            }
            else if (dsym.type.value.isImmutable())
                dsym.storage_class.value |= 1048576L;
            else if (dsym.type.value.isShared())
                dsym.storage_class.value |= 536870912L;
            else if (dsym.type.value.isWild())
                dsym.storage_class.value |= 2147483648L;
            {
                long stc = dsym.storage_class.value & 664L;
                if ((stc) != 0)
                {
                    if ((stc == 8L))
                        dsym.error(new BytePtr("cannot be `final`, perhaps you meant `const`?"));
                    else
                    {
                        Ref<OutBuffer> buf = ref(new OutBuffer());
                        try {
                            stcToBuffer(ptr(buf), stc);
                            dsym.error(new BytePtr("cannot be `%s`"), buf.value.peekChars());
                        }
                        finally {
                        }
                    }
                    dsym.storage_class.value &= ~stc;
                }
            }
            if ((dsym.storage_class.value & 524288L) != 0)
            {
                long stc = dsym.storage_class.value & 1216348163L;
                if (stc != 0)
                {
                    Ref<OutBuffer> buf = ref(new OutBuffer());
                    try {
                        stcToBuffer(ptr(buf), stc);
                        dsym.error(new BytePtr("cannot be `scope` and `%s`"), buf.value.peekChars());
                    }
                    finally {
                    }
                }
                else if (dsym.isMember() != null)
                {
                    dsym.error(new BytePtr("field cannot be `scope`"));
                }
                else if (!dsym.type.value.hasPointers())
                {
                    dsym.storage_class.value &= -524289L;
                }
            }
            if ((dsym.storage_class.value & 69936087043L) != 0)
            {
            }
            else
            {
                AggregateDeclaration aad = parent.isAggregateDeclaration();
                if (aad != null)
                {
                    if (global.params.vfield && ((dsym.storage_class.value & 1048580L) != 0) && (dsym._init.value != null) && (dsym._init.value.isVoidInitializer() == null))
                    {
                        BytePtr s = pcopy((dsym.storage_class.value & 1048576L) != 0 ? new BytePtr("immutable") : new BytePtr("const"));
                        message(dsym.loc.value, new BytePtr("`%s.%s` is `%s` field"), ad.toPrettyChars(false), dsym.toChars(), s);
                    }
                    dsym.storage_class.value |= 64L;
                    {
                        TypeStruct ts = tbn.isTypeStruct();
                        if ((ts) != null)
                            if (ts.sym.value.noDefaultCtor.value)
                            {
                                if ((dsym.isThisDeclaration() == null) && (dsym._init.value == null))
                                    aad.noDefaultCtor.value = true;
                            }
                    }
                }
                InterfaceDeclaration id = parent.isInterfaceDeclaration();
                if (id != null)
                {
                    dsym.error(new BytePtr("field not allowed in interface"));
                }
                else if ((aad != null) && (aad.sizeok.value == Sizeok.done))
                {
                    dsym.error(new BytePtr("cannot be further field because it will change the determined %s size"), aad.toChars());
                }
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                {
                    for (; 1 != 0;){
                        TemplateInstance ti2 = ti.tempdecl.value.parent.value.isTemplateInstance();
                        if (ti2 == null)
                            break;
                        ti = ti2;
                    }
                    AggregateDeclaration ad2 = ti.tempdecl.value.isMember();
                    if ((ad2 != null) && (dsym.storage_class.value != 0L))
                    {
                        dsym.error(new BytePtr("cannot use template to add field to aggregate `%s`"), ad2.toChars());
                    }
                }
            }
            if (((dsym.storage_class.value & 1374391648288L) == 2097152L) && (!pequals(dsym.ident.value, Id.This.value)))
            {
                dsym.error(new BytePtr("only parameters or `foreach` declarations can be `ref`"));
            }
            if (dsym.type.value.hasWild() != 0)
            {
                if (((dsym.storage_class.value & 1216348227L) != 0) || dsym.isDataseg())
                {
                    dsym.error(new BytePtr("only parameters or stack based variables can be `inout`"));
                }
                FuncDeclaration func = (this.sc.get()).func.value;
                if (func != null)
                {
                    if (func.fes.value != null)
                        func = func.fes.value.func.value;
                    boolean isWild = false;
                    {
                        FuncDeclaration fd = func;
                        for (; fd != null;fd = fd.toParentDecl().isFuncDeclaration()){
                            if (((TypeFunction)fd.type.value).iswild.value != 0)
                            {
                                isWild = true;
                                break;
                            }
                        }
                    }
                    if (!isWild)
                    {
                        dsym.error(new BytePtr("`inout` variables can only be declared inside `inout` functions"));
                    }
                }
            }
            if (((dsym.storage_class.value & 343599480832L) == 0) && ((tbn.ty.value & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tbn).sym.value.noDefaultCtor.value)
            {
                if (dsym._init.value == null)
                {
                    if (dsym.isField())
                    {
                        dsym.storage_class.value |= 549755813888L;
                    }
                    else if ((dsym.storage_class.value & 32L) != 0)
                    {
                    }
                    else
                        dsym.error(new BytePtr("default construction is disabled for type `%s`"), dsym.type.value.toChars());
                }
            }
            FuncDeclaration fd = parent.isFuncDeclaration();
            if (dsym.type.value.isscope() && ((dsym.storage_class.value & 16777216L) == 0))
            {
                if (((dsym.storage_class.value & 1218449473L) != 0) || (fd == null))
                {
                    dsym.error(new BytePtr("globals, statics, fields, manifest constants, ref and out parameters cannot be `scope`"));
                }
                if ((dsym.storage_class.value & 524288L) == 0)
                {
                    if (((dsym.storage_class.value & 32L) == 0) && (!pequals(dsym.ident.value, Id.withSym.value)))
                        dsym.error(new BytePtr("reference to `scope class` must be `scope`"));
                }
            }
            if (((this.sc.get()).func.value != null) && ((this.sc.get()).intypeof.value == 0))
            {
                if ((dsym._init.value != null) && (dsym._init.value.isVoidInitializer() != null) && dsym.type.value.hasPointers())
                {
                    if ((this.sc.get()).func.value.setUnsafe())
                        dsym.error(new BytePtr("`void` initializers for pointers not allowed in safe functions"));
                }
                else if ((dsym._init.value == null) && ((dsym.storage_class.value & 1216348259L) == 0) && dsym.type.value.hasVoidInitPointers())
                {
                    if ((this.sc.get()).func.value.setUnsafe())
                        dsym.error(new BytePtr("`void` initializers for pointers not allowed in safe functions"));
                }
            }
            if ((dsym._init.value == null) || (dsym._init.value.isVoidInitializer() != null) && (fd == null))
            {
                dsym.storage_class.value |= 131072L;
            }
            if (dsym._init.value != null)
                dsym.storage_class.value |= 4194304L;
            else if ((dsym.storage_class.value & 8388608L) != 0)
                dsym.error(new BytePtr("manifest constants must have initializers"));
            boolean isBlit = false;
            long sz = 0L;
            try {
                if ((dsym._init.value == null) && ((dsym.storage_class.value & 1073741827L) == 0) && (fd != null) && ((dsym.storage_class.value & 274877925472L) == 0) || ((dsym.storage_class.value & 4096L) != 0) && ((sz = dsym.type.value.size()) != 0L))
                {
                    if ((sz == -1L) && ((dsym.type.value.ty.value & 0xFF) != ENUMTY.Terror))
                        dsym.error(new BytePtr("size of type `%s` is invalid"), dsym.type.value.toChars());
                    Type tv = dsym.type.value;
                    for (; ((tv.ty.value & 0xFF) == ENUMTY.Tsarray);) {
                        tv = tv.nextOf();
                    }
                    if (tv.needsNested())
                    {
                        assert(((tbn.ty.value & 0xFF) == ENUMTY.Tstruct));
                        checkFrameAccess(dsym.loc.value, this.sc, tbn.isTypeStruct().sym.value, 0);
                        Expression e = tv.defaultInitLiteral(dsym.loc.value);
                        e = new BlitExp(dsym.loc.value, new VarExp(dsym.loc.value, dsym, true), e);
                        e = expressionSemantic(e, this.sc);
                        dsym._init.value = new ExpInitializer(dsym.loc.value, e);
                        /*goto Ldtor*/throw Dispatch0.INSTANCE;
                    }
                    if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tv).sym.value.zeroInit)
                    {
                        Expression e = new IntegerExp(dsym.loc.value, 0L, Type.tint32.value);
                        e = new BlitExp(dsym.loc.value, new VarExp(dsym.loc.value, dsym, true), e);
                        e.type.value = dsym.type.value;
                        dsym._init.value = new ExpInitializer(dsym.loc.value, e);
                        /*goto Ldtor*/throw Dispatch0.INSTANCE;
                    }
                    if (((dsym.type.value.baseElemOf().ty.value & 0xFF) == ENUMTY.Tvoid))
                    {
                        dsym.error(new BytePtr("`%s` does not have a default initializer"), dsym.type.value.toChars());
                    }
                    else {
                        Expression e = defaultInit(dsym.type.value, dsym.loc.value);
                        if ((e) != null)
                        {
                            dsym._init.value = new ExpInitializer(dsym.loc.value, e);
                        }
                    }
                    isBlit = true;
                }
                if (dsym._init.value != null)
                {
                    this.sc = (this.sc.get()).push();
                    (this.sc.get()).stc.value &= -4538273628165L;
                    ExpInitializer ei = dsym._init.value.isExpInitializer();
                    if (ei != null)
                        ei.exp.value = inferType(ei.exp.value, dsym.type.value, 0);
                    if (((this.sc.get()).func.value != null) || ((this.sc.get()).intypeof.value == 1))
                    {
                        if ((fd != null) && ((dsym.storage_class.value & 1216348163L) == 0) && (dsym._init.value.isVoidInitializer() == null))
                        {
                            if (ei == null)
                            {
                                ArrayInitializer ai = dsym._init.value.isArrayInitializer();
                                Expression e = null;
                                if ((ai != null) && ((tb.ty.value & 0xFF) == ENUMTY.Taarray))
                                    e = toAssocArrayLiteral(ai);
                                else
                                    e = initializerToExpression(dsym._init.value, null);
                                if (e == null)
                                {
                                    dsym._init.value = initializerSemantic(dsym._init.value, this.sc, dsym.type.value, NeedInterpret.INITnointerpret);
                                    e = initializerToExpression(dsym._init.value, null);
                                    if (e == null)
                                    {
                                        dsym.error(new BytePtr("is not a static and cannot have static initializer"));
                                        e = new ErrorExp();
                                    }
                                }
                                ei = new ExpInitializer(dsym._init.value.loc.value, e);
                                dsym._init.value = ei;
                            }
                            Expression exp = ei.exp.value;
                            Expression e1 = new VarExp(dsym.loc.value, dsym, true);
                            if (isBlit)
                                exp = new BlitExp(dsym.loc.value, e1, exp);
                            else
                                exp = new ConstructExp(dsym.loc.value, e1, exp);
                            dsym.canassign++;
                            exp = expressionSemantic(exp, this.sc);
                            dsym.canassign--;
                            exp = exp.optimize(0, false);
                            if (((exp.op.value & 0xFF) == 127))
                            {
                                dsym._init.value = new ErrorInitializer();
                                ei = null;
                            }
                            else
                                ei.exp.value = exp;
                            if ((ei != null) && dsym.isScope())
                            {
                                Expression ex = ei.exp.value;
                                for (; ((ex.op.value & 0xFF) == 99);) {
                                    ex = ((CommaExp)ex).e2.value;
                                }
                                if (((ex.op.value & 0xFF) == 96) || ((ex.op.value & 0xFF) == 95))
                                    ex = ((AssignExp)ex).e2.value;
                                if (((ex.op.value & 0xFF) == 22))
                                {
                                    NewExp ne = (NewExp)ex;
                                    if (((dsym.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tclass))
                                    {
                                        if ((ne.newargs.value != null) && ((ne.newargs.value.get()).length.value > 1))
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
                                else if (((ex.op.value & 0xFF) == 161))
                                {
                                    FuncDeclaration f = ((FuncExp)ex).fd.value;
                                    f.tookAddressOf.value--;
                                }
                            }
                        }
                        else
                        {
                            dsym._init.value = initializerSemantic(dsym._init.value, this.sc, dsym.type.value, ((this.sc.get()).intypeof.value == 1) ? NeedInterpret.INITnointerpret : NeedInterpret.INITinterpret);
                            ExpInitializer init_err = dsym._init.value.isExpInitializer();
                            if ((init_err != null) && ((init_err.exp.value.op.value & 0xFF) == 234))
                            {
                                errorSupplemental(dsym.loc.value, new BytePtr("compile time context created here"));
                            }
                        }
                    }
                    else if (parent.isAggregateDeclaration() != null)
                    {
                        dsym._scope.value = scx != null ? scx : (this.sc.get()).copy();
                        (dsym._scope.value.get()).setNoFree();
                    }
                    else if (((dsym.storage_class.value & 9437188L) != 0) || dsym.type.value.isConst() || dsym.type.value.isImmutable())
                    {
                        if (inferred == 0)
                        {
                            int errors = global.errors.value;
                            dsym.inuse.value++;
                            if (ei != null)
                            {
                                Expression exp = ei.exp.value.syntaxCopy();
                                boolean needctfe = dsym.isDataseg() || ((dsym.storage_class.value & 8388608L) != 0);
                                if (needctfe)
                                    this.sc = (this.sc.get()).startCTFE();
                                exp = expressionSemantic(exp, this.sc);
                                exp = resolveProperties(this.sc, exp);
                                if (needctfe)
                                    this.sc = (this.sc.get()).endCTFE();
                                Type tb2 = dsym.type.value.toBasetype();
                                Type ti = exp.type.value.toBasetype();
                                {
                                    TypeStruct ts = ti.isTypeStruct();
                                    if ((ts) != null)
                                    {
                                        StructDeclaration sd = ts.sym.value;
                                        if ((sd.postblit.value != null) && (pequals(tb2.toDsymbol(null), sd)))
                                        {
                                            if (exp.isLvalue())
                                                dsym.error(new BytePtr("of type struct `%s` uses `this(this)`, which is not allowed in static initialization"), tb2.toChars());
                                        }
                                    }
                                }
                                ei.exp.value = exp;
                            }
                            dsym._init.value = initializerSemantic(dsym._init.value, this.sc, dsym.type.value, NeedInterpret.INITinterpret);
                            dsym.inuse.value--;
                            if ((global.errors.value > errors))
                            {
                                dsym._init.value = new ErrorInitializer();
                                dsym.type.value = Type.terror.value;
                            }
                        }
                        else
                        {
                            dsym._scope.value = scx != null ? scx : (this.sc.get()).copy();
                            (dsym._scope.value.get()).setNoFree();
                        }
                    }
                    this.sc = (this.sc.get()).pop();
                }
            }
            catch(Dispatch0 __d){}
        /*Ldtor:*/
            dsym.edtor.value = dsym.callScopeDtor(this.sc);
            if (dsym.edtor.value != null)
            {
                if (global.params.vsafe.value && ((dsym.storage_class.value & 1374397941856L) == 0) && !dsym.isDataseg() && !dsym.doNotInferScope && dsym.type.value.hasPointers())
                {
                    Type tv = dsym.type.value.baseElemOf();
                    if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct) && ((((TypeStruct)tv).sym.value.dtor.value.storage_class.value & 524288L) != 0))
                    {
                        dsym.storage_class.value |= 524288L;
                    }
                }
                if (((this.sc.get()).func.value != null) && ((dsym.storage_class.value & 1073741825L) != 0))
                    dsym.edtor.value = expressionSemantic(dsym.edtor.value, (this.sc.get())._module.value._scope.value);
                else
                    dsym.edtor.value = expressionSemantic(dsym.edtor.value, this.sc);
            }
            dsym.semanticRun.value = PASS.semanticdone;
            if (((dsym.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Terror))
                dsym.errors.value = true;
            if (((this.sc.get()).scopesym.value != null) && ((this.sc.get()).scopesym.value.isAggregateDeclaration() == null))
            {
                {
                    ScopeDsymbol sym = (this.sc.get()).scopesym.value;
                    for (; (sym != null) && (dsym.endlinnum == 0);sym = sym.parent.value != null ? sym.parent.value.isScopeDsymbol() : null) {
                        dsym.endlinnum = sym.endlinnum;
                    }
                }
            }
        }

        public  void visit(TypeInfoDeclaration dsym) {
            assert((dsym.linkage.value == LINK.c));
        }

        public  void visit(Import imp) {
            if ((imp.semanticRun.value > PASS.init))
                return ;
            if (imp._scope.value != null)
            {
                this.sc = imp._scope.value;
                imp._scope.value = null;
            }
            if (this.sc == null)
                return ;
            imp.semanticRun.value = PASS.semantic;
            boolean loadErrored = false;
            if (imp.mod == null)
            {
                loadErrored = imp.load(this.sc);
                if (imp.mod != null)
                    imp.mod.importAll(null);
            }
            if (imp.mod != null)
            {
                if (((this.sc.get()).minst.value != null) && ((this.sc.get()).tinst != null))
                {
                    (this.sc.get()).tinst.importedModules.value.push(imp.mod);
                    (this.sc.get()).minst.value.aimports.push(imp.mod);
                }
                else
                {
                    (this.sc.get())._module.value.aimports.push(imp.mod);
                }
                if ((this.sc.get()).explicitProtection != 0)
                    imp.protection = (this.sc.get()).protection.value.copy();
                if ((imp.aliasId == null) && (imp.names.length.value == 0))
                {
                    ScopeDsymbol scopesym = null;
                    {
                        Ptr<Scope> scd = this.sc;
                        for (; scd != null;scd = (scd.get()).enclosing.value){
                            if ((scd.get()).scopesym.value == null)
                                continue;
                            scopesym = (scd.get()).scopesym.value;
                            break;
                        }
                    }
                    if (imp.isstatic == 0)
                    {
                        scopesym.importScope(imp.mod, imp.protection);
                    }
                    if (imp.packages.value != null)
                    {
                        dmodule.Package p = imp.pkg.value;
                        scopesym.addAccessiblePackage(p, imp.protection);
                        {
                            Slice<Identifier> __r1144 = (imp.packages.value.get()).opSlice(1, (imp.packages.value.get()).length.value).copy();
                            int __key1145 = 0;
                            for (; (__key1145 < __r1144.getLength());__key1145 += 1) {
                                Identifier id = __r1144.get(__key1145);
                                p = (dmodule.Package)p.symtab.lookup(id);
                                scopesym.addAccessiblePackage(p, imp.protection);
                            }
                        }
                    }
                    scopesym.addAccessiblePackage(imp.mod, imp.protection);
                }
                if (!loadErrored)
                {
                    dsymbolSemantic(imp.mod, null);
                }
                if (imp.mod.needmoduleinfo != 0)
                {
                    (this.sc.get())._module.value.needmoduleinfo = 1;
                }
                this.sc = (this.sc.get()).push(imp.mod);
                (this.sc.get()).protection.value = imp.protection.copy();
                {
                    int i = 0;
                    for (; (i < imp.aliasdecls.length);i++){
                        AliasDeclaration ad = imp.aliasdecls.get(i);
                        Dsymbol sym = imp.mod.search(imp.loc.value, imp.names.get(i), 1);
                        if (sym != null)
                        {
                            if (!symbolIsVisible(this.sc, sym))
                                imp.mod.error(imp.loc.value, new BytePtr("member `%s` is not visible from module `%s`"), imp.names.get(i).toChars(), (this.sc.get())._module.value.toChars());
                            dsymbolSemantic(ad, this.sc);
                        }
                        else
                        {
                            Dsymbol s = imp.mod.search_correct(imp.names.get(i));
                            if (s != null)
                                imp.mod.error(imp.loc.value, new BytePtr("import `%s` not found, did you mean %s `%s`?"), imp.names.get(i).toChars(), s.kind(), s.toPrettyChars(false));
                            else
                                imp.mod.error(imp.loc.value, new BytePtr("import `%s` not found"), imp.names.get(i).toChars());
                            ad.type.value = Type.terror.value;
                        }
                    }
                }
                this.sc = (this.sc.get()).pop();
            }
            imp.semanticRun.value = PASS.semanticdone;
            if ((global.params.moduleDeps != null) && !((pequals(imp.id, Id.object.value)) && (pequals((this.sc.get())._module.value.ident.value, Id.object.value))) && (!pequals((this.sc.get())._module.value.ident.value, Id.entrypoint)) && (strcmp((this.sc.get())._module.value.ident.value.toChars(), new BytePtr("__main")) != 0))
            {
                Ptr<OutBuffer> ob = global.params.moduleDeps;
                dmodule.Module imod = (this.sc.get()).instantiatingModule();
                if (global.params.moduleDepsFile.getLength() == 0)
                    (ob.get()).writestring(new ByteSlice("depsImport "));
                (ob.get()).writestring(imod.toPrettyChars(false));
                (ob.get()).writestring(new ByteSlice(" ("));
                escapePath(ob, imod.srcfile.toChars());
                (ob.get()).writestring(new ByteSlice(") : "));
                protectionToBuffer(ob, imp.protection);
                (ob.get()).writeByte(32);
                if (imp.isstatic != 0)
                {
                    stcToBuffer(ob, 1L);
                    (ob.get()).writeByte(32);
                }
                (ob.get()).writestring(new ByteSlice(": "));
                if (imp.packages.value != null)
                {
                    {
                        int i = 0;
                        for (; (i < (imp.packages.value.get()).length.value);i++){
                            Identifier pid = (imp.packages.value.get()).get(i);
                            (ob.get()).printf(new BytePtr("%s."), pid.toChars());
                        }
                    }
                }
                (ob.get()).writestring(imp.id.asString());
                (ob.get()).writestring(new ByteSlice(" ("));
                if (imp.mod != null)
                    escapePath(ob, imp.mod.srcfile.toChars());
                else
                    (ob.get()).writestring(new ByteSlice("???"));
                (ob.get()).writeByte(41);
                {
                    Slice<Identifier> __r1147 = imp.names.opSlice().copy();
                    int __key1146 = 0;
                    for (; (__key1146 < __r1147.getLength());__key1146 += 1) {
                        Identifier name = __r1147.get(__key1146);
                        int i = __key1146;
                        if ((i == 0))
                            (ob.get()).writeByte(58);
                        else
                            (ob.get()).writeByte(44);
                        Identifier _alias = imp.aliases.get(i);
                        if (_alias == null)
                        {
                            (ob.get()).printf(new BytePtr("%s"), name.toChars());
                            _alias = name;
                        }
                        else
                            (ob.get()).printf(new BytePtr("%s=%s"), _alias.toChars(), name.toChars());
                    }
                }
                if (imp.aliasId != null)
                    (ob.get()).printf(new BytePtr(" -> %s"), imp.aliasId.toChars());
                (ob.get()).writenl();
            }
        }

        public  void attribSemantic(AttribDeclaration ad) {
            if ((ad.semanticRun.value != PASS.init))
                return ;
            ad.semanticRun.value = PASS.semantic;
            Ptr<DArray<Dsymbol>> d = ad.include(this.sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = ad.newScope(this.sc);
                boolean errors = false;
                {
                    int i = 0;
                    for (; (i < (d.get()).length.value);i++){
                        Dsymbol s = (d.get()).get(i);
                        dsymbolSemantic(s, sc2);
                        (errors ? 1 : 0) |= (s.errors.value ? 1 : 0);
                    }
                }
                (ad.errors.value ? 1 : 0) |= (errors ? 1 : 0);
                if ((sc2 != this.sc))
                    (sc2.get()).pop();
            }
            ad.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(AttribDeclaration atd) {
            this.attribSemantic(atd);
        }

        public  void visit(AnonDeclaration scd) {
            assert((this.sc.get()).parent.value != null);
            Dsymbol p = (this.sc.get()).parent.value.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(scd.loc.value, new BytePtr("%s can only be a part of an aggregate, not %s `%s`"), scd.kind(), p.kind(), p.toChars());
                scd.errors.value = true;
                return ;
            }
            if (scd.decl.value != null)
            {
                this.sc = (this.sc.get()).push();
                (this.sc.get()).stc.value &= -1208484098L;
                (this.sc.get()).inunion = scd.isunion ? scd : null;
                (this.sc.get()).flags.value = 0;
                {
                    int i = 0;
                    for (; (i < (scd.decl.value.get()).length.value);i++){
                        Dsymbol s = (scd.decl.value.get()).get(i);
                        dsymbolSemantic(s, this.sc);
                    }
                }
                this.sc = (this.sc.get()).pop();
            }
        }

        public  void visit(PragmaDeclaration pd) {
            try {
                try {
                    if (global.params.mscoff)
                    {
                        if ((pequals(pd.ident.value, Id.linkerDirective)))
                        {
                            if ((pd.args == null) || ((pd.args.get()).length.value != 1))
                                pd.error(new BytePtr("one string argument expected for pragma(linkerDirective)"));
                            else
                            {
                                StringExp se = semanticString(this.sc, (pd.args.get()).get(0), new BytePtr("linker directive"));
                                if (se == null)
                                    /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                                pd.args.get().set(0, se);
                                if (global.params.verbose)
                                    message(new BytePtr("linkopt   %.*s"), se.len.value, se.string.value);
                            }
                            /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                        }
                    }
                    if ((pequals(pd.ident.value, Id.msg)))
                    {
                        if (pd.args != null)
                        {
                            {
                                int i = 0;
                                for (; (i < (pd.args.get()).length.value);i++){
                                    Expression e = (pd.args.get()).get(i);
                                    this.sc = (this.sc.get()).startCTFE();
                                    e = expressionSemantic(e, this.sc);
                                    e = resolveProperties(this.sc, e);
                                    this.sc = (this.sc.get()).endCTFE();
                                    if ((e.type.value != null) && ((e.type.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                                    {
                                        error(pd.loc.value, new BytePtr("Cannot pass argument `%s` to `pragma msg` because it is `void`"), e.toChars());
                                        return ;
                                    }
                                    e = ctfeInterpretForPragmaMsg(e);
                                    if (((e.op.value & 0xFF) == 127))
                                    {
                                        errorSupplemental(pd.loc.value, new BytePtr("while evaluating `pragma(msg, %s)`"), (pd.args.get()).get(i).toChars());
                                        return ;
                                    }
                                    StringExp se = e.toStringExp();
                                    if (se != null)
                                    {
                                        se = se.toUTF8(this.sc);
                                        fprintf(stderr, new BytePtr("%.*s"), se.len.value, se.string.value);
                                    }
                                    else
                                        fprintf(stderr, new BytePtr("%s"), e.toChars());
                                }
                            }
                            fprintf(stderr, new BytePtr("\n"));
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if ((pequals(pd.ident.value, Id.lib)))
                    {
                        if ((pd.args == null) || ((pd.args.get()).length.value != 1))
                            pd.error(new BytePtr("string expected for library name"));
                        else
                        {
                            StringExp se = semanticString(this.sc, (pd.args.get()).get(0), new BytePtr("library name"));
                            if (se == null)
                                /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                            pd.args.get().set(0, se);
                            ByteSlice name = xarraydup(se.string.value.slice(0,se.len.value)).copy();
                            if (global.params.verbose)
                                message(new BytePtr("library   %s"), toBytePtr(name));
                            if ((global.params.moduleDeps != null) && (global.params.moduleDepsFile.getLength() == 0))
                            {
                                Ptr<OutBuffer> ob = global.params.moduleDeps;
                                dmodule.Module imod = (this.sc.get()).instantiatingModule();
                                (ob.get()).writestring(new ByteSlice("depsLib "));
                                (ob.get()).writestring(imod.toPrettyChars(false));
                                (ob.get()).writestring(new ByteSlice(" ("));
                                escapePath(ob, imod.srcfile.toChars());
                                (ob.get()).writestring(new ByteSlice(") : "));
                                (ob.get()).writestring(name);
                                (ob.get()).writenl();
                            }
                            Mem.xfree(toBytePtr(name));
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if ((pequals(pd.ident.value, Id.startaddress)))
                    {
                        if ((pd.args == null) || ((pd.args.get()).length.value != 1))
                            pd.error(new BytePtr("function name expected for start address"));
                        else
                        {
                            Expression e = (pd.args.get()).get(0);
                            this.sc = (this.sc.get()).startCTFE();
                            e = expressionSemantic(e, this.sc);
                            this.sc = (this.sc.get()).endCTFE();
                            pd.args.get().set(0, e);
                            Dsymbol sa = getDsymbol(e);
                            if ((sa == null) || (sa.isFuncDeclaration() == null))
                                pd.error(new BytePtr("function name expected for start address, not `%s`"), e.toChars());
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if ((pequals(pd.ident.value, Id.Pinline)))
                    {
                        /*goto Ldecl*/throw Dispatch0.INSTANCE;
                    }
                    else if ((pequals(pd.ident.value, Id.mangle)))
                    {
                        if (pd.args == null)
                            pd.args = refPtr(new DArray<Expression>());
                        if (((pd.args.get()).length.value != 1))
                        {
                            pd.error(new BytePtr("string expected for mangled name"));
                            (pd.args.get()).setDim(1);
                            pd.args.get().set(0, new ErrorExp());
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        StringExp se = semanticString(this.sc, (pd.args.get()).get(0), new BytePtr("mangled name"));
                        if (se == null)
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        pd.args.get().set(0, se);
                        if (se.len.value == 0)
                        {
                            pd.error(new BytePtr("zero-length string not allowed for mangled name"));
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        if (((se.sz.value & 0xFF) != 1))
                        {
                            pd.error(new BytePtr("mangled name characters can only be of type `char`"));
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        {
                            IntRef i = ref(0);
                            for (; (i.value < se.len.value);){
                                BytePtr p = pcopy(se.string.value);
                                int c = (p.get(i.value) & 0xFF);
                                if ((c < 128))
                                {
                                    if (isValidMangling(c))
                                    {
                                        i.value += 1;
                                        continue;
                                    }
                                    else
                                    {
                                        pd.error(new BytePtr("char 0x%02x not allowed in mangled name"), c);
                                        break;
                                    }
                                }
                                {
                                    BytePtr msg = pcopy(utf_decodeChar(se.string.value, se.len.value, i, c));
                                    if ((msg) != null)
                                    {
                                        pd.error(new BytePtr("%s"), msg);
                                        break;
                                    }
                                }
                                if (!isUniAlpha(c))
                                {
                                    pd.error(new BytePtr("char `0x%04x` not allowed in mangled name"), c);
                                    break;
                                }
                            }
                        }
                    }
                    else if ((pequals(pd.ident.value, Id.crt_constructor)) || (pequals(pd.ident.value, Id.crt_destructor)))
                    {
                        if ((pd.args != null) && ((pd.args.get()).length.value != 0))
                            pd.error(new BytePtr("takes no argument"));
                        /*goto Ldecl*/throw Dispatch0.INSTANCE;
                    }
                    else if (global.params.ignoreUnsupportedPragmas)
                    {
                        if (global.params.verbose)
                        {
                            OutBuffer buf = new OutBuffer();
                            try {
                                buf.writestring(pd.ident.value.asString());
                                if (pd.args != null)
                                {
                                    {
                                        int i = 0;
                                        for (; (i < (pd.args.get()).length.value);i++){
                                            Expression e = (pd.args.get()).get(i);
                                            this.sc = (this.sc.get()).startCTFE();
                                            e = expressionSemantic(e, this.sc);
                                            e = resolveProperties(this.sc, e);
                                            this.sc = (this.sc.get()).endCTFE();
                                            e = e.ctfeInterpret();
                                            if ((i == 0))
                                                buf.writestring(new ByteSlice(" ("));
                                            else
                                                buf.writeByte(44);
                                            buf.writestring(e.toChars());
                                        }
                                    }
                                    if ((pd.args.get()).length.value != 0)
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
                        error(pd.loc.value, new BytePtr("unrecognized `pragma(%s)`"), pd.ident.value.toChars());
                }
                catch(Dispatch0 __d){}
            /*Ldecl:*/
                if (pd.decl.value != null)
                {
                    Ptr<Scope> sc2 = pd.newScope(this.sc);
                    {
                        int i = 0;
                        for (; (i < (pd.decl.value.get()).length.value);i++){
                            Dsymbol s = (pd.decl.value.get()).get(i);
                            dsymbolSemantic(s, sc2);
                            if ((pequals(pd.ident.value, Id.mangle)))
                            {
                                assert((pd.args != null) && ((pd.args.get()).length.value == 1));
                                {
                                    StringExp se = (pd.args.get()).get(0).toStringExp();
                                    if ((se) != null)
                                    {
                                        ByteSlice name = xarraydup(se.string.value.slice(0,se.len.value)).copy();
                                        int cnt = setMangleOverride(s, name);
                                        if ((cnt > 1))
                                            pd.error(new BytePtr("can only apply to a single declaration"));
                                    }
                                }
                            }
                        }
                    }
                    if ((sc2 != this.sc))
                        (sc2.get()).pop();
                }
                return ;
            }
            catch(Dispatch1 __d){}
        /*Lnodecl:*/
            if (pd.decl.value != null)
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

        public  Ptr<DArray<Dsymbol>> compileIt(CompileDeclaration cd) {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                if (expressionsToString(buf, this.sc, cd.exps))
                    return null;
                int errors = global.errors.value;
                int len = buf.value.offset.value;
                ByteSlice str = buf.value.extractChars().slice(0,len).copy();
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
                try {
                    ParserASTCodegen p = new ParserASTCodegen(cd.loc.value, (this.sc.get())._module.value, str, false, diagnosticReporter);
                    try {
                        p.nextToken();
                        Ptr<DArray<Dsymbol>> d = p.parseDeclDefs(0, null, null);
                        if (p.errors())
                        {
                            assert((global.errors.value != errors));
                            return null;
                        }
                        if (((p.token.value.value & 0xFF) != 11))
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
            if (!cd.compiled)
            {
                cd.decl.value = this.compileIt(cd);
                cd.addMember(this.sc, cd.scopesym);
                cd.compiled = true;
                if ((cd._scope.value != null) && (cd.decl.value != null))
                {
                    {
                        int i = 0;
                        for (; (i < (cd.decl.value.get()).length.value);i++){
                            Dsymbol s = (cd.decl.value.get()).get(i);
                            s.setScope(cd._scope.value);
                        }
                    }
                }
            }
            this.attribSemantic(cd);
        }

        public  void visit(CPPNamespaceDeclaration ns) {
            Function1<StringExp,Identifier> identFromSE = new Function1<StringExp,Identifier>(){
                public Identifier invoke(StringExp se) {
                    Ref<ByteSlice> sident = ref(se.toStringz().copy());
                    if ((sident.value.getLength() == 0) || !Identifier.isValidIdentifier(sident.value))
                    {
                        ns.exp.error(new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), sident.value.getLength(), toBytePtr(sident));
                        return null;
                    }
                    else
                        return Identifier.idPool(sident.value);
                }
            };
            if ((ns.ident.value == null))
            {
                ns.namespace = (this.sc.get()).namespace;
                this.sc = (this.sc.get()).startCTFE();
                ns.exp = expressionSemantic(ns.exp, this.sc);
                ns.exp = resolveProperties(this.sc, ns.exp);
                this.sc = (this.sc.get()).endCTFE();
                ns.exp = ns.exp.ctfeInterpret();
                {
                    TupleExp te = ns.exp.isTupleExp();
                    if ((te) != null)
                    {
                        expandTuples(te.exps.value);
                        CPPNamespaceDeclaration current = ns.namespace;
                        {
                            int d = 0;
                            for (; (d < (te.exps.value.get()).length.value);d += 1){
                                Expression exp = (te.exps.value.get()).get(d);
                                CPPNamespaceDeclaration prev = d != 0 ? current : ns.namespace;
                                current = (d + 1 != (te.exps.value.get()).length.value) ? new CPPNamespaceDeclaration(exp, null) : ns;
                                current.exp = exp;
                                current.namespace = prev;
                                {
                                    StringExp se = exp.toStringExp();
                                    if ((se) != null)
                                    {
                                        current.ident.value = identFromSE.invoke(se);
                                        if ((current.ident.value == null))
                                            return ;
                                    }
                                    else
                                        ns.exp.error(new BytePtr("`%s`: index %d is not a string constant, it is a `%s`"), ns.exp.toChars(), d, ns.exp.type.value.toChars());
                                }
                            }
                        }
                    }
                    else {
                        StringExp se = ns.exp.toStringExp();
                        if ((se) != null)
                            ns.ident.value = identFromSE.invoke(se);
                        else
                            ns.exp.error(new BytePtr("compile time string constant (or tuple) expected, not `%s`"), ns.exp.toChars());
                    }
                }
            }
            if (ns.ident.value != null)
                this.attribSemantic(ns);
        }

        public  void visit(UserAttributeDeclaration uad) {
            if ((uad.decl.value != null) && (uad._scope.value == null))
                uad.setScope(this.sc);
            this.attribSemantic(uad);
            return ;
        }

        public  void visit(StaticAssert sa) {
            if ((sa.semanticRun.value < PASS.semanticdone))
                sa.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(DebugSymbol ds) {
            if ((ds.semanticRun.value < PASS.semanticdone))
                ds.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(VersionSymbol vs) {
            if ((vs.semanticRun.value < PASS.semanticdone))
                vs.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(dmodule.Package pkg) {
            if ((pkg.semanticRun.value < PASS.semanticdone))
                pkg.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(dmodule.Module m) {
            if ((m.semanticRun.value != PASS.init))
                return ;
            m.semanticRun.value = PASS.semantic;
            Ptr<Scope> sc = m._scope.value;
            if (sc == null)
            {
                Scope.createGlobal(m);
            }
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    dsymbolSemantic(s, sc);
                    dmodule.Module.runDeferredSemantic();
                }
            };
            foreachDsymbol(m.members.value, __lambda2);
            if (m.userAttribDecl != null)
            {
                dsymbolSemantic(m.userAttribDecl, sc);
            }
            if (m._scope.value == null)
            {
                sc = (sc.get()).pop();
                (sc.get()).pop();
            }
            m.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(EnumDeclaration ed) {
            if ((ed.semanticRun.value >= PASS.semanticdone))
                return ;
            if ((ed.semanticRun.value == PASS.semantic))
            {
                assert(ed.memtype.value != null);
                error(ed.loc.value, new BytePtr("circular reference to enum base type `%s`"), ed.memtype.value.toChars());
                ed.errors.value = true;
                ed.semanticRun.value = PASS.semanticdone;
                return ;
            }
            int dprogress_save = dmodule.Module.dprogress;
            Ptr<Scope> scx = null;
            if (ed._scope.value != null)
            {
                this.sc = ed._scope.value;
                scx = ed._scope.value;
                ed._scope.value = null;
            }
            if (this.sc == null)
                return ;
            ed.parent.value = (this.sc.get()).parent.value;
            ed.type = typeSemantic(ed.type, ed.loc.value, this.sc);
            ed.protection = (this.sc.get()).protection.value.copy();
            if (((this.sc.get()).stc.value & 1024L) != 0)
                ed.isdeprecated = true;
            ed.userAttribDecl = (this.sc.get()).userAttribDecl;
            ed.semanticRun.value = PASS.semantic;
            if ((ed.members.value == null) && (ed.memtype.value == null))
            {
                ed.semanticRun.value = PASS.semanticdone;
                return ;
            }
            if (ed.symtab == null)
                ed.symtab = new DsymbolTable();
            if (ed.memtype.value != null)
            {
                ed.memtype.value = typeSemantic(ed.memtype.value, ed.loc.value, this.sc);
                {
                    TypeEnum te = ed.memtype.value.isTypeEnum();
                    if ((te) != null)
                    {
                        EnumDeclaration sym = (EnumDeclaration)te.toDsymbol(this.sc);
                        if ((sym.memtype.value == null) || (sym.members.value == null) || (sym.symtab == null) || (sym._scope.value != null))
                        {
                            ed._scope.value = scx != null ? scx : (this.sc.get()).copy();
                            (ed._scope.value.get()).setNoFree();
                            dmodule.Module.addDeferredSemantic(ed);
                            dmodule.Module.dprogress = dprogress_save;
                            ed.semanticRun.value = PASS.init;
                            return ;
                        }
                    }
                }
                if (((ed.memtype.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    ed.error(new BytePtr("base type must not be `void`"));
                    ed.memtype.value = Type.terror.value;
                }
                if (((ed.memtype.value.ty.value & 0xFF) == ENUMTY.Terror))
                {
                    ed.errors.value = true;
                    Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s) {
                            s.errors.value = true;
                        }
                    };
                    foreachDsymbol(ed.members.value, __lambda2);
                    ed.semanticRun.value = PASS.semanticdone;
                    return ;
                }
            }
            ed.semanticRun.value = PASS.semanticdone;
            if (ed.members.value == null)
                return ;
            if (((ed.members.value.get()).length.value == 0))
            {
                ed.error(new BytePtr("enum `%s` must have at least one member"), ed.toChars());
                ed.errors.value = true;
                return ;
            }
            dmodule.Module.dprogress++;
            Ptr<Scope> sce = null;
            if (ed.isAnonymous())
                sce = this.sc;
            else
            {
                sce = (this.sc.get()).push(ed);
                (sce.get()).parent.value = ed;
            }
            sce = (sce.get()).startCTFE();
            (sce.get()).setNoFree();
            Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    EnumMember em = s.isEnumMember();
                    if (em != null)
                        em._scope.value = sce;
                }
            };
            foreachDsymbol(ed.members.value, __lambda3);
            if (!ed.added)
            {
                ScopeDsymbol scopesym = null;
                if (ed.isAnonymous())
                {
                    {
                        Ptr<Scope> sct = sce;
                        for (; 1 != 0;sct = (sct.get()).enclosing.value){
                            assert(sct != null);
                            if ((sct.get()).scopesym.value != null)
                            {
                                scopesym = (sct.get()).scopesym.value;
                                if ((sct.get()).scopesym.value.symtab == null)
                                    (sct.get()).scopesym.value.symtab = new DsymbolTable();
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
                    public Void invoke(Dsymbol s) {
                        EnumMember em = s.isEnumMember();
                        if (em != null)
                        {
                            em.ed = ed;
                            em.addMember(sc, scopesym);
                        }
                    }
                };
                foreachDsymbol(ed.members.value, __lambda4);
            }
            Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    EnumMember em = s.isEnumMember();
                    if (em != null)
                        dsymbolSemantic(em, em._scope.value);
                }
            };
            foreachDsymbol(ed.members.value, __lambda5);
        }

        public  void visit(EnumMember em) {
            Function0<Void> errorReturn = new Function0<Void>(){
                public Void invoke() {
                    em.errors.value = true;
                    em.semanticRun.value = PASS.semanticdone;
                }
            };
            if (em.errors.value || (em.semanticRun.value >= PASS.semanticdone))
                return ;
            if ((em.semanticRun.value == PASS.semantic))
            {
                em.error(new BytePtr("circular reference to `enum` member"));
                errorReturn.invoke();
                return ;
            }
            assert(em.ed != null);
            dsymbolSemantic(em.ed, this.sc);
            if (em.ed.errors.value)
                errorReturn.invoke();
                return ;
            if (em.errors.value || (em.semanticRun.value >= PASS.semanticdone))
                return ;
            if (em._scope.value != null)
                this.sc = em._scope.value;
            if (this.sc == null)
                return ;
            em.semanticRun.value = PASS.semantic;
            em.protection = (em.ed.isAnonymous() ? em.ed.protection : new Prot(Prot.Kind.public_)).copy();
            em.linkage.value = LINK.d;
            em.storage_class.value |= 8388608L;
            if (em.ed.isAnonymous())
            {
                if (em.userAttribDecl != null)
                    em.userAttribDecl.userAttribDecl = em.ed.userAttribDecl;
                else
                    em.userAttribDecl = em.ed.userAttribDecl;
            }
            boolean first = pequals(em, (em.ed.members.value.get()).get(0));
            if (em.origType != null)
            {
                em.origType = typeSemantic(em.origType, em.loc.value, this.sc);
                em.type.value = em.origType;
                assert(em.value() != null);
            }
            if (em.value() != null)
            {
                Expression e = em.value();
                assert((e.dyncast() == DYNCAST.expression));
                e = expressionSemantic(e, this.sc);
                e = resolveProperties(this.sc, e);
                e = e.ctfeInterpret();
                if (((e.op.value & 0xFF) == 127))
                    errorReturn.invoke();
                    return ;
                if (first && (em.ed.memtype.value == null) && !em.ed.isAnonymous())
                {
                    em.ed.memtype.value = e.type.value;
                    if (((em.ed.memtype.value.ty.value & 0xFF) == ENUMTY.Terror))
                    {
                        em.ed.errors.value = true;
                        errorReturn.invoke();
                        return ;
                    }
                    if (((em.ed.memtype.value.ty.value & 0xFF) != ENUMTY.Terror))
                    {
                        Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol s) {
                                EnumMember enm = s.isEnumMember();
                                if ((enm == null) || (pequals(enm, em)) || (enm.semanticRun.value < PASS.semanticdone) || (enm.origType != null))
                                    return null;
                                Expression ev = enm.value();
                                ev = ev.implicitCastTo(sc, em.ed.memtype.value);
                                ev = ev.ctfeInterpret();
                                ev = ev.castTo(sc, em.ed.type);
                                if (((ev.op.value & 0xFF) == 127))
                                    em.ed.errors.value = true;
                                enm.value() = ev;
                            }
                        };
                        foreachDsymbol(em.ed.members.value, __lambda3);
                        if (em.ed.errors.value)
                        {
                            em.ed.memtype.value = Type.terror.value;
                            errorReturn.invoke();
                            return ;
                        }
                    }
                }
                if ((em.ed.memtype.value != null) && (em.origType == null))
                {
                    e = e.implicitCastTo(this.sc, em.ed.memtype.value);
                    e = e.ctfeInterpret();
                    em.origValue = e;
                    if (!em.ed.isAnonymous())
                    {
                        e = e.castTo(this.sc, em.ed.type.addMod(e.type.value.mod.value));
                        e = e.ctfeInterpret();
                    }
                }
                else if (em.origType != null)
                {
                    e = e.implicitCastTo(this.sc, em.origType);
                    e = e.ctfeInterpret();
                    assert(em.ed.isAnonymous());
                    em.origValue = e;
                }
                em.value() = e;
            }
            else if (first)
            {
                Type t = null;
                if (em.ed.memtype.value != null)
                    t = em.ed.memtype.value;
                else
                {
                    t = Type.tint32.value;
                    if (!em.ed.isAnonymous())
                        em.ed.memtype.value = t;
                }
                Expression e = new IntegerExp(em.loc.value, 0L, t);
                e = e.ctfeInterpret();
                em.origValue = e;
                if (!em.ed.isAnonymous())
                {
                    e = e.castTo(this.sc, em.ed.type);
                    e = e.ctfeInterpret();
                }
                em.value() = e;
            }
            else
            {
                EnumMember emprev = null;
                Function1<Dsymbol,Integer> __lambda4 = new Function1<Dsymbol,Integer>(){
                    public Integer invoke(Dsymbol s) {
                        {
                            EnumMember enm = s.isEnumMember();
                            if ((enm) != null)
                            {
                                if ((pequals(enm, em)))
                                    return 1;
                                emprev = enm;
                            }
                        }
                        return 0;
                    }
                };
                foreachDsymbol(em.ed.members.value, __lambda4);
                assert(emprev != null);
                if ((emprev.semanticRun.value < PASS.semanticdone))
                    dsymbolSemantic(emprev, emprev._scope.value);
                if (emprev.errors.value)
                    errorReturn.invoke();
                    return ;
                Expression eprev = emprev.value();
                Type tprev = eprev.type.value.toHeadMutable().equals(em.ed.type.toHeadMutable()) ? em.ed.memtype.value : eprev.type.value;
                Expression emax = getProperty(tprev, em.ed.loc.value, Id.max.value, 0);
                emax = expressionSemantic(emax, this.sc);
                emax = emax.ctfeInterpret();
                assert(eprev != null);
                Expression e = new EqualExp(TOK.equal, em.loc.value, eprev, emax);
                e = expressionSemantic(e, this.sc);
                e = e.ctfeInterpret();
                if (e.toInteger() != 0)
                {
                    em.error(new BytePtr("initialization with `%s.%s+1` causes overflow for type `%s`"), emprev.ed.toChars(), emprev.toChars(), em.ed.memtype.value.toChars());
                    errorReturn.invoke();
                    return ;
                }
                e = new AddExp(em.loc.value, eprev, new IntegerExp(em.loc.value, 1L, Type.tint32.value));
                e = expressionSemantic(e, this.sc);
                e = e.castTo(this.sc, eprev.type.value);
                e = e.ctfeInterpret();
                if (((e.op.value & 0xFF) != 127))
                {
                    assert(emprev.origValue != null);
                    em.origValue = new AddExp(em.loc.value, emprev.origValue, new IntegerExp(em.loc.value, 1L, Type.tint32.value));
                    em.origValue = expressionSemantic(em.origValue, this.sc);
                    em.origValue = em.origValue.ctfeInterpret();
                }
                if (((e.op.value & 0xFF) == 127))
                    errorReturn.invoke();
                    return ;
                if (e.type.value.isfloating())
                {
                    Expression etest = new EqualExp(TOK.equal, em.loc.value, e, eprev);
                    etest = expressionSemantic(etest, this.sc);
                    etest = etest.ctfeInterpret();
                    if (etest.toInteger() != 0)
                    {
                        em.error(new BytePtr("has inexact value due to loss of precision"));
                        errorReturn.invoke();
                        return ;
                    }
                }
                em.value() = e;
            }
            if (em.origType == null)
                em.type.value = em.value().type.value;
            assert(em.origValue != null);
            em.semanticRun.value = PASS.semanticdone;
        }

        public  void visit(TemplateDeclaration tempdecl) {
            if ((tempdecl.semanticRun.value != PASS.init))
                return ;
            if (tempdecl._scope.value != null)
            {
                this.sc = tempdecl._scope.value;
                tempdecl._scope.value = null;
            }
            if (this.sc == null)
                return ;
            if (((this.sc.get())._module.value != null) && (pequals((this.sc.get())._module.value.ident.value, Id.object.value)))
            {
                if ((pequals(tempdecl.ident.value, Id.RTInfo)))
                    Type.rtinfo = tempdecl;
            }
            if (tempdecl._scope.value == null)
            {
                tempdecl._scope.value = (this.sc.get()).copy();
                (tempdecl._scope.value.get()).setNoFree();
            }
            tempdecl.semanticRun.value = PASS.semantic;
            tempdecl.parent.value = (this.sc.get()).parent.value;
            tempdecl.protection = (this.sc.get()).protection.value.copy();
            tempdecl.namespace = (this.sc.get()).namespace;
            tempdecl.isstatic = (tempdecl.toParent().isModule() != null) || (((tempdecl._scope.value.get()).stc.value & 1L) != 0);
            if (!tempdecl.isstatic)
            {
                {
                    AggregateDeclaration ad = tempdecl.parent.value.pastMixin().isAggregateDeclaration();
                    if ((ad) != null)
                        ad.makeNested();
                }
            }
            ScopeDsymbol paramsym = new ScopeDsymbol();
            paramsym.parent.value = tempdecl.parent.value;
            Ptr<Scope> paramscope = (this.sc.get()).push(paramsym);
            (paramscope.get()).stc.value = 0L;
            if (global.params.doDocComments)
            {
                tempdecl.origParameters.value = refPtr(new DArray<TemplateParameter>((tempdecl.parameters.get()).length.value));
                {
                    int i = 0;
                    for (; (i < (tempdecl.parameters.get()).length.value);i++){
                        TemplateParameter tp = (tempdecl.parameters.get()).get(i);
                        tempdecl.origParameters.value.get().set(i, tp.syntaxCopy());
                    }
                }
            }
            {
                int i = 0;
                for (; (i < (tempdecl.parameters.get()).length.value);i++){
                    TemplateParameter tp = (tempdecl.parameters.get()).get(i);
                    if (!tp.declareParameter(paramscope))
                    {
                        error(tp.loc.value, new BytePtr("parameter `%s` multiply defined"), tp.ident.value.toChars());
                        tempdecl.errors.value = true;
                    }
                    if (!tpsemantic(tp, paramscope, tempdecl.parameters))
                    {
                        tempdecl.errors.value = true;
                    }
                    if ((i + 1 != (tempdecl.parameters.get()).length.value) && (tp.isTemplateTupleParameter() != null))
                    {
                        tempdecl.error(new BytePtr("template tuple parameter must be last one"));
                        tempdecl.errors.value = true;
                    }
                }
            }
            Ref<DArray<TemplateParameter>> tparams = ref(tparams.value = new DArray<TemplateParameter>(1));
            try {
                {
                    int i = 0;
                    for (; (i < (tempdecl.parameters.get()).length.value);i++){
                        TemplateParameter tp = (tempdecl.parameters.get()).get(i);
                        tparams.value.set(0, tp);
                        {
                            int j = 0;
                            for (; (j < (tempdecl.parameters.get()).length.value);j++){
                                if ((i == j))
                                    continue;
                                {
                                    TemplateTypeParameter ttp = (tempdecl.parameters.get()).get(j).isTemplateTypeParameter();
                                    if ((ttp) != null)
                                    {
                                        if (reliesOnTident(ttp.specType, ptr(tparams), 0))
                                            tp.dependent.value = true;
                                    }
                                    else {
                                        TemplateAliasParameter tap = (tempdecl.parameters.get()).get(j).isTemplateAliasParameter();
                                        if ((tap) != null)
                                        {
                                            if (reliesOnTident(tap.specType, ptr(tparams), 0) || reliesOnTident(isType(tap.specAlias), ptr(tparams), 0))
                                            {
                                                tp.dependent.value = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                (paramscope.get()).pop();
                tempdecl.onemember.value = null;
                if (tempdecl.members.value != null)
                {
                    Ref<Dsymbol> s = ref(null);
                    if (Dsymbol.oneMembers(tempdecl.members.value, ptr(s), tempdecl.ident.value) && (s.value != null))
                    {
                        tempdecl.onemember.value = s.value;
                        s.value.parent.value = tempdecl;
                    }
                }
                tempdecl.semanticRun.value = PASS.semanticdone;
            }
            finally {
            }
        }

        public  void visit(TemplateInstance ti) {
            templateInstanceSemantic(ti, this.sc, null);
        }

        public  void visit(TemplateMixin tm) {
            if ((tm.semanticRun.value != PASS.init))
            {
                return ;
            }
            tm.semanticRun.value = PASS.semantic;
            Ptr<Scope> scx = null;
            if (tm._scope.value != null)
            {
                this.sc = tm._scope.value;
                scx = tm._scope.value;
                tm._scope.value = null;
            }
            if (!tm.findTempDecl(this.sc) || !tm.semanticTiargs(this.sc) || !tm.findBestMatch(this.sc, null))
            {
                if ((tm.semanticRun.value == PASS.init))
                {
                    tm._scope.value = scx != null ? scx : (this.sc.get()).copy();
                    (tm._scope.value.get()).setNoFree();
                    dmodule.Module.addDeferredSemantic(tm);
                    return ;
                }
                tm.inst.value = tm;
                tm.errors.value = true;
                return ;
            }
            TemplateDeclaration tempdecl = tm.tempdecl.value.isTemplateDeclaration();
            assert(tempdecl != null);
            if (tm.ident.value == null)
            {
                BytePtr s = pcopy(new BytePtr("__mixin"));
                {
                    FuncDeclaration func = (this.sc.get()).parent.value.isFuncDeclaration();
                    if ((func) != null)
                    {
                        tm.symtab = func.localsymtab;
                        if (tm.symtab != null)
                        {
                            /*goto L1*//*unrolled goto*/
                        }
                    }
                    else
                    {
                        tm.symtab = (this.sc.get()).parent.value.isScopeDsymbol().symtab;
                    /*L1:*/
                        assert(tm.symtab != null);
                        tm.ident.value = Identifier.generateId(s, tm.symtab.len() + 1);
                        tm.symtab.insert((Dsymbol)tm);
                    }
                }
            }
            tm.inst.value = tm;
            tm.parent.value = (this.sc.get()).parent.value;
            {
                Dsymbol s = tm.parent.value;
            L_outer3:
                for (; s != null;s = s.parent.value){
                    TemplateMixin tmix = s.isTemplateMixin();
                    if ((tmix == null) || (!pequals(tempdecl, tmix.tempdecl.value)))
                        continue L_outer3;
                    if (((tm.tiargs.value.get()).length.value != (tmix.tiargs.value.get()).length.value))
                        continue L_outer3;
                    try {
                        {
                            int i = 0;
                        L_outer4:
                            for (; (i < (tm.tiargs.value.get()).length.value);i++){
                                RootObject o = (tm.tiargs.value.get()).get(i);
                                Type ta = isType(o);
                                Expression ea = isExpression(o);
                                Dsymbol sa = isDsymbol(o);
                                RootObject tmo = (tmix.tiargs.value.get()).get(i);
                                if (ta != null)
                                {
                                    Type tmta = isType(tmo);
                                    if (tmta == null)
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    if (!ta.equals(tmta))
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                }
                                else if (ea != null)
                                {
                                    Expression tme = isExpression(tmo);
                                    if ((tme == null) || !ea.equals(tme))
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                }
                                else if (sa != null)
                                {
                                    Dsymbol tmsa = isDsymbol(tmo);
                                    if ((!pequals(sa, tmsa)))
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
            tm.members.value = Dsymbol.arraySyntaxCopy(tempdecl.members.value);
            if (tm.members.value == null)
                return ;
            tm.symtab = new DsymbolTable();
            {
                Ptr<Scope> sce = this.sc;
                for (; 1 != 0;sce = (sce.get()).enclosing.value){
                    ScopeDsymbol sds = (sce.get()).scopesym.value;
                    if (sds != null)
                    {
                        sds.importScope(tm, new Prot(Prot.Kind.public_));
                        break;
                    }
                }
            }
            Ptr<Scope> scy = (this.sc.get()).push(tm);
            (scy.get()).parent.value = tm;
            tm.argsym = new ScopeDsymbol();
            tm.argsym.parent.value = (scy.get()).parent.value;
            Ptr<Scope> argscope = (scy.get()).push(tm.argsym);
            int errorsave = global.errors.value;
            tm.declareParameters(argscope);
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.addMember(argscope, tm);
                    return null;
                }
            };
            foreachDsymbol(tm.members.value, __lambda2);
            Ptr<Scope> sc2 = (argscope.get()).push(tm);
            if (((dsymbolsem.visitnest += 1) > 500))
            {
                global.gag.value = 0;
                tm.error(new BytePtr("recursive expansion"));
                fatal();
            }
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.setScope(sc2);
                    return null;
                }
            };
            foreachDsymbol(tm.members.value, __lambda4);
            Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.importAll(sc2);
                    return null;
                }
            };
            foreachDsymbol(tm.members.value, __lambda5);
            Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    dsymbolSemantic(s, sc2);
                    return null;
                }
            };
            foreachDsymbol(tm.members.value, __lambda6);
            dsymbolsem.visitnest--;
            AggregateDeclaration ad = tm.toParent().isAggregateDeclaration();
            if (((this.sc.get()).func.value != null) && (ad == null))
            {
                semantic2(tm, sc2);
                semantic3(tm, sc2);
            }
            if ((global.errors.value != errorsave))
            {
                tm.error(new BytePtr("error instantiating"));
                tm.errors.value = true;
            }
            (sc2.get()).pop();
            (argscope.get()).pop();
            (scy.get()).pop();
        }

        public  void visit(Nspace ns) {
            if ((ns.semanticRun.value != PASS.init))
                return ;
            if (ns._scope.value != null)
            {
                this.sc = ns._scope.value;
                ns._scope.value = null;
            }
            if (this.sc == null)
                return ;
            boolean repopulateMembers = false;
            if (ns.identExp != null)
            {
                this.sc = (this.sc.get()).startCTFE();
                Expression resolved = expressionSemantic(ns.identExp, this.sc);
                resolved = resolveProperties(this.sc, resolved);
                this.sc = (this.sc.get()).endCTFE();
                resolved = resolved.ctfeInterpret();
                StringExp name = resolved.toStringExp();
                TupleExp tup = name != null ? null : resolved.toTupleExp();
                if ((tup == null) && (name == null))
                {
                    error(ns.loc.value, new BytePtr("expected string expression for namespace name, got `%s`"), ns.identExp.toChars());
                    return ;
                }
                ns.identExp = resolved;
                if (name != null)
                {
                    ByteSlice ident = name.toStringz().copy();
                    if ((ident.getLength() == 0) || !Identifier.isValidIdentifier(ident))
                    {
                        error(ns.loc.value, new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), ident.getLength(), toBytePtr(ident));
                        return ;
                    }
                    ns.ident.value = Identifier.idPool(ident);
                }
                else
                {
                    Nspace parentns = ns;
                    {
                        Slice<Expression> __r1150 = (tup.exps.value.get()).opSlice().copy();
                        int __key1149 = 0;
                        for (; (__key1149 < __r1150.getLength());__key1149 += 1) {
                            Expression exp = __r1150.get(__key1149);
                            int i = __key1149;
                            name = exp.toStringExp();
                            if (name == null)
                            {
                                error(ns.loc.value, new BytePtr("expected string expression for namespace name, got `%s`"), exp.toChars());
                                return ;
                            }
                            ByteSlice ident = name.toStringz().copy();
                            if ((ident.getLength() == 0) || !Identifier.isValidIdentifier(ident))
                            {
                                error(ns.loc.value, new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), ident.getLength(), toBytePtr(ident));
                                return ;
                            }
                            if ((i == 0))
                            {
                                ns.ident.value = Identifier.idPool(ident);
                            }
                            else
                            {
                                Nspace childns = new Nspace(ns.loc.value, Identifier.idPool(ident), null, parentns.members.value);
                                parentns.members.value = refPtr(new DArray<Dsymbol>());
                                (parentns.members.value.get()).push(childns);
                                parentns = childns;
                                repopulateMembers = true;
                            }
                        }
                    }
                }
            }
            ns.semanticRun.value = PASS.semantic;
            ns.parent.value = (this.sc.get()).parent.value;
            if (ns.members.value != null)
            {
                assert(this.sc != null);
                this.sc = (this.sc.get()).push(ns);
                (this.sc.get()).linkage.value = LINK.cpp;
                (this.sc.get()).parent.value = ns;
                {
                    Slice<Dsymbol> __r1151 = (ns.members.value.get()).opSlice().copy();
                    int __key1152 = 0;
                    for (; (__key1152 < __r1151.getLength());__key1152 += 1) {
                        Dsymbol s = __r1151.get(__key1152);
                        if (repopulateMembers)
                        {
                            s.addMember(this.sc, (this.sc.get()).scopesym.value);
                            s.setScope(this.sc);
                        }
                        s.importAll(this.sc);
                    }
                }
                {
                    Slice<Dsymbol> __r1153 = (ns.members.value.get()).opSlice().copy();
                    int __key1154 = 0;
                    for (; (__key1154 < __r1153.getLength());__key1154 += 1) {
                        Dsymbol s = __r1153.get(__key1154);
                        dsymbolSemantic(s, this.sc);
                    }
                }
                (this.sc.get()).pop();
            }
            ns.semanticRun.value = PASS.semanticdone;
        }

        public  void funcDeclarationSemantic(FuncDeclaration funcdecl) {
            TypeFunction f = null;
            AggregateDeclaration ad = null;
            InterfaceDeclaration id = null;
            if ((funcdecl.semanticRun.value != PASS.init) && (funcdecl.isFuncLiteralDeclaration() != null))
            {
                return ;
            }
            if ((funcdecl.semanticRun.value >= PASS.semanticdone))
                return ;
            assert((funcdecl.semanticRun.value <= PASS.semantic));
            funcdecl.semanticRun.value = PASS.semantic;
            if (funcdecl._scope.value != null)
            {
                this.sc = funcdecl._scope.value;
                funcdecl._scope.value = null;
            }
            if ((this.sc == null) || funcdecl.errors.value)
                return ;
            funcdecl.namespace = (this.sc.get()).namespace;
            funcdecl.parent.value = (this.sc.get()).parent.value;
            Dsymbol parent = funcdecl.toParent();
            funcdecl.foverrides.setDim(0);
            funcdecl.storage_class.value |= (this.sc.get()).stc.value & -2097153L;
            ad = funcdecl.isThis();
            if ((ad != null) && !funcdecl.generated)
            {
                funcdecl.storage_class.value |= ad.storage_class & 2685403652L;
                ad.makeNested();
            }
            if ((this.sc.get()).func.value != null)
                funcdecl.storage_class.value |= (this.sc.get()).func.value.storage_class.value & 137438953472L;
            if (((funcdecl.storage_class.value & 2685403140L) != 0) && !((ad != null) || funcdecl.isNested()))
                funcdecl.storage_class.value &= -2685403141L;
            if (((this.sc.get()).flags.value & 256) != 0)
                funcdecl.flags |= FUNCFLAG.compileTimeOnly;
            FuncLiteralDeclaration fld = funcdecl.isFuncLiteralDeclaration();
            if ((fld != null) && (fld.treq.value != null))
            {
                Type treq = fld.treq.value;
                assert(((treq.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction));
                if (((treq.ty.value & 0xFF) == ENUMTY.Tdelegate))
                    fld.tok.value = TOK.delegate_;
                else if (((treq.ty.value & 0xFF) == ENUMTY.Tpointer) && ((treq.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
                    fld.tok.value = TOK.function_;
                else
                    throw new AssertionError("Unreachable code!");
                funcdecl.linkage.value = treq.nextOf().toTypeFunction().linkage.value;
            }
            else
                funcdecl.linkage.value = (this.sc.get()).linkage.value;
            funcdecl.inlining = (this.sc.get()).inlining;
            funcdecl.protection = (this.sc.get()).protection.value.copy();
            funcdecl.userAttribDecl = (this.sc.get()).userAttribDecl;
            if (funcdecl.originalType.value == null)
                funcdecl.originalType.value = funcdecl.type.value.syntaxCopy();
            if (((funcdecl.type.value.ty.value & 0xFF) != ENUMTY.Tfunction))
            {
                if (((funcdecl.type.value.ty.value & 0xFF) != ENUMTY.Terror))
                {
                    funcdecl.error(new BytePtr("`%s` must be a function instead of `%s`"), funcdecl.toChars(), funcdecl.type.value.toChars());
                    funcdecl.type.value = Type.terror.value;
                }
                funcdecl.errors.value = true;
                return ;
            }
            if (funcdecl.type.value.deco.value == null)
            {
                this.sc = (this.sc.get()).push();
                (this.sc.get()).stc.value |= funcdecl.storage_class.value & 137438954496L;
                TypeFunction tf = funcdecl.type.value.toTypeFunction();
                if ((this.sc.get()).func.value != null)
                {
                    if ((tf.purity.value == PURE.impure) && funcdecl.isNested() || (funcdecl.isThis() != null))
                    {
                        FuncDeclaration fd = null;
                        {
                            Dsymbol p = funcdecl.toParent2();
                            for (; p != null;p = p.toParent2()){
                                {
                                    AggregateDeclaration adx = p.isAggregateDeclaration();
                                    if ((adx) != null)
                                    {
                                        if (adx.isNested())
                                            continue;
                                        break;
                                    }
                                }
                                if (((fd = p.isFuncDeclaration()) != null))
                                    break;
                            }
                        }
                        if ((fd != null) && (fd.isPureBypassingInference() >= PURE.weak) && (funcdecl.isInstantiated() == null))
                        {
                            tf.purity.value = PURE.fwdref;
                        }
                    }
                }
                if (tf.isref.value)
                    (this.sc.get()).stc.value |= 2097152L;
                if (tf.isscope.value)
                    (this.sc.get()).stc.value |= 524288L;
                if (tf.isnothrow.value)
                    (this.sc.get()).stc.value |= 33554432L;
                if (tf.isnogc.value)
                    (this.sc.get()).stc.value |= 4398046511104L;
                if (tf.isproperty.value)
                    (this.sc.get()).stc.value |= 4294967296L;
                if ((tf.purity.value == PURE.fwdref))
                    (this.sc.get()).stc.value |= 67108864L;
                if ((tf.trust.value != TRUST.default_))
                    (this.sc.get()).stc.value &= -60129542145L;
                if ((tf.trust.value == TRUST.safe))
                    (this.sc.get()).stc.value |= 8589934592L;
                if ((tf.trust.value == TRUST.system))
                    (this.sc.get()).stc.value |= 34359738368L;
                if ((tf.trust.value == TRUST.trusted))
                    (this.sc.get()).stc.value |= 17179869184L;
                if (funcdecl.isCtorDeclaration() != null)
                {
                    (this.sc.get()).flags.value |= 1;
                    Type tret = ad.handleType();
                    assert(tret != null);
                    tret = tret.addStorageClass(funcdecl.storage_class.value | (this.sc.get()).stc.value);
                    tret = tret.addMod(funcdecl.type.value.mod.value);
                    tf.next.value = tret;
                    if (ad.isStructDeclaration() != null)
                        (this.sc.get()).stc.value |= 2097152L;
                }
                if ((ad != null) && (ad.isClassDeclaration() != null) && tf.isreturn.value || (((this.sc.get()).stc.value & 17592186044416L) != 0) && (((this.sc.get()).stc.value & 1L) == 0))
                    (this.sc.get()).stc.value |= 524288L;
                if ((((this.sc.get()).stc.value & 524288L) != 0) && (ad != null) && (ad.isStructDeclaration() != null) && !ad.type.value.hasPointers())
                {
                    (this.sc.get()).stc.value &= -524289L;
                    tf.isscope.value = false;
                }
                (this.sc.get()).linkage.value = funcdecl.linkage.value;
                if (!tf.isNaked() && !((funcdecl.isThis() != null) || funcdecl.isNested()))
                {
                    Ref<OutBuffer> buf = ref(new OutBuffer());
                    try {
                        MODtoBuffer(ptr(buf), tf.mod.value);
                        funcdecl.error(new BytePtr("without `this` cannot be `%s`"), buf.value.peekChars());
                        tf.mod.value = (byte)0;
                    }
                    finally {
                    }
                }
                long stc = funcdecl.storage_class.value;
                if (funcdecl.type.value.isImmutable())
                    stc |= 1048576L;
                if (funcdecl.type.value.isConst())
                    stc |= 4L;
                if (funcdecl.type.value.isShared() || ((funcdecl.storage_class.value & 512L) != 0))
                    stc |= 536870912L;
                if (funcdecl.type.value.isWild())
                    stc |= 2147483648L;
                funcdecl.type.value = funcdecl.type.value.addSTC(stc);
                funcdecl.type.value = typeSemantic(funcdecl.type.value, funcdecl.loc.value, this.sc);
                this.sc = (this.sc.get()).pop();
            }
            if (((funcdecl.type.value.ty.value & 0xFF) != ENUMTY.Tfunction))
            {
                if (((funcdecl.type.value.ty.value & 0xFF) != ENUMTY.Terror))
                {
                    funcdecl.error(new BytePtr("`%s` must be a function instead of `%s`"), funcdecl.toChars(), funcdecl.type.value.toChars());
                    funcdecl.type.value = Type.terror.value;
                }
                funcdecl.errors.value = true;
                return ;
            }
            else
            {
                TypeFunction tfo = funcdecl.originalType.value.toTypeFunction();
                TypeFunction tfx = funcdecl.type.value.toTypeFunction();
                tfo.mod.value = tfx.mod.value;
                tfo.isscope.value = tfx.isscope.value;
                tfo.isreturninferred.value = tfx.isreturninferred.value;
                tfo.isscopeinferred.value = tfx.isscopeinferred.value;
                tfo.isref.value = tfx.isref.value;
                tfo.isnothrow.value = tfx.isnothrow.value;
                tfo.isnogc.value = tfx.isnogc.value;
                tfo.isproperty.value = tfx.isproperty.value;
                tfo.purity.value = tfx.purity.value;
                tfo.trust.value = tfx.trust.value;
                funcdecl.storage_class.value &= -4465259184133L;
            }
            f = (TypeFunction)funcdecl.type.value;
            if (((funcdecl.storage_class.value & 256L) != 0) && !f.isref.value && !funcdecl.inferRetType)
                funcdecl.error(new BytePtr("storage class `auto` has no effect if return type is not inferred"));
            if (f.isscope.value && !funcdecl.isNested() && (ad == null))
            {
                funcdecl.error(new BytePtr("functions cannot be `scope`"));
            }
            if (f.isreturn.value && !funcdecl.needThis() && !funcdecl.isNested())
            {
                if (((this.sc.get()).scopesym.value != null) && ((this.sc.get()).scopesym.value.isAggregateDeclaration() != null))
                    funcdecl.error(new BytePtr("`static` member has no `this` to which `return` can apply"));
                else
                    error(funcdecl.loc.value, new BytePtr("Top-level function `%s` has no `this` to which `return` can apply"), funcdecl.toChars());
            }
            if (funcdecl.isAbstract() && !funcdecl.isVirtual())
            {
                BytePtr sfunc = null;
                if (funcdecl.isStatic())
                    sfunc = pcopy(new BytePtr("static"));
                else if ((funcdecl.protection.kind.value == Prot.Kind.private_) || (funcdecl.protection.kind.value == Prot.Kind.package_))
                    sfunc = pcopy(protectionToChars(funcdecl.protection.kind.value));
                else
                    sfunc = pcopy(new BytePtr("final"));
                funcdecl.error(new BytePtr("`%s` functions cannot be `abstract`"), sfunc);
            }
            if (funcdecl.isOverride() && !funcdecl.isVirtual() && (funcdecl.isFuncLiteralDeclaration() == null))
            {
                int kind = funcdecl.prot().kind.value;
                if ((kind == Prot.Kind.private_) || (kind == Prot.Kind.package_) && (funcdecl.isMember() != null))
                    funcdecl.error(new BytePtr("`%s` method is not virtual and cannot override"), protectionToChars(kind));
                else
                    funcdecl.error(new BytePtr("cannot override a non-virtual function"));
            }
            if (funcdecl.isAbstract() && funcdecl.isFinalFunc())
                funcdecl.error(new BytePtr("cannot be both `final` and `abstract`"));
            id = parent.isInterfaceDeclaration();
            if (id != null)
            {
                funcdecl.storage_class.value |= 16L;
                if ((funcdecl.isCtorDeclaration() != null) || (funcdecl.isPostBlitDeclaration() != null) || (funcdecl.isDtorDeclaration() != null) || (funcdecl.isInvariantDeclaration() != null) || (funcdecl.isNewDeclaration() != null) || funcdecl.isDelete())
                    funcdecl.error(new BytePtr("constructors, destructors, postblits, invariants, new and delete functions are not allowed in interface `%s`"), id.toChars());
                if ((funcdecl.fbody.value != null) && funcdecl.isVirtual())
                    funcdecl.error(new BytePtr("function body only allowed in `final` functions in interface `%s`"), id.toChars());
            }
            {
                UnionDeclaration ud = parent.isUnionDeclaration();
                if ((ud) != null)
                {
                    if ((funcdecl.isPostBlitDeclaration() != null) || (funcdecl.isDtorDeclaration() != null) || (funcdecl.isInvariantDeclaration() != null))
                        funcdecl.error(new BytePtr("destructors, postblits and invariants are not allowed in union `%s`"), ud.toChars());
                }
            }
            try {
                {
                    StructDeclaration sd = parent.isStructDeclaration();
                    if ((sd) != null)
                    {
                        if (funcdecl.isCtorDeclaration() != null)
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                {
                    ClassDeclaration cd = parent.isClassDeclaration();
                    if ((cd) != null)
                    {
                        parent = (cd = objc().getParent(funcdecl, cd));
                        if (funcdecl.isCtorDeclaration() != null)
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                        if ((funcdecl.storage_class.value & 16L) != 0)
                            cd.isabstract.value = Abstract.yes;
                        if (!funcdecl.isVirtual())
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                        if ((pequals(funcdecl.type.value.nextOf(), Type.terror.value)))
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        boolean may_override = false;
                        {
                            int i = 0;
                        L_outer5:
                            for (; (i < (cd.baseclasses.get()).length.value);i++){
                                Ptr<BaseClass> b = (cd.baseclasses.get()).get(i);
                                ClassDeclaration cbd = (b.get()).type.value.toBasetype().isClassHandle();
                                if (cbd == null)
                                    continue L_outer5;
                                {
                                    int j = 0;
                                L_outer6:
                                    for (; (j < cbd.vtbl.value.length.value);j++){
                                        FuncDeclaration f2 = cbd.vtbl.value.get(j).isFuncDeclaration();
                                        if ((f2 == null) || (!pequals(f2.ident.value, funcdecl.ident.value)))
                                            continue L_outer6;
                                        if ((cbd.parent.value != null) && (cbd.parent.value.isTemplateInstance() != null))
                                        {
                                            if (!f2.functionSemantic())
                                                /*goto Ldone*/throw Dispatch0.INSTANCE;
                                        }
                                        may_override = true;
                                    }
                                }
                            }
                        }
                        if (may_override && (funcdecl.type.value.nextOf() == null))
                        {
                            funcdecl.error(new BytePtr("return type inference is not supported if may override base class function"));
                        }
                        int vi = cd.baseClass.value != null ? funcdecl.findVtblIndex(ptr(cd.baseClass.value.vtbl), cd.baseClass.value.vtbl.value.length.value, true) : -1;
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
                                                if (cd.baseClass.value != null)
                                                {
                                                    Dsymbol s = cd.baseClass.value.search(funcdecl.loc.value, funcdecl.ident.value, 8);
                                                    if (s != null)
                                                    {
                                                        FuncDeclaration f2 = s.isFuncDeclaration();
                                                        if (f2 != null)
                                                        {
                                                            f2 = f2.overloadExactMatch(funcdecl.type.value);
                                                            if ((f2 != null) && f2.isFinalFunc() && (f2.prot().kind.value != Prot.Kind.private_))
                                                                funcdecl.error(new BytePtr("cannot override `final` function `%s`"), f2.toPrettyChars(false));
                                                        }
                                                    }
                                                }
                                                if (global.params.mscoff && (cd.classKind.value == ClassKind.cpp) && (cd.baseClass.value != null) && (cd.baseClass.value.vtbl.value.length.value != 0))
                                                {
                                                    funcdecl.interfaceVirtual = funcdecl.overrideInterface();
                                                    if (funcdecl.interfaceVirtual != null)
                                                    {
                                                        cd.vtblFinal.value.push(funcdecl);
                                                        /*goto Linterfaces*/throw Dispatch0.INSTANCE;
                                                    }
                                                }
                                                if (funcdecl.isFinalFunc())
                                                {
                                                    cd.vtblFinal.value.push(funcdecl);
                                                }
                                                else
                                                {
                                                    funcdecl.introducing = true;
                                                    if ((cd.classKind.value == ClassKind.cpp) && target.reverseCppOverloads)
                                                    {
                                                        funcdecl.vtblIndex.value = cd.vtbl.value.length.value;
                                                        boolean found = false;
                                                        {
                                                            Slice<Dsymbol> __r1156 = cd.vtbl.value.opSlice().copy();
                                                            int __key1155 = 0;
                                                            for (; (__key1155 < __r1156.getLength());__key1155 += 1) {
                                                                Dsymbol s_1 = __r1156.get(__key1155);
                                                                int i = __key1155;
                                                                if (found)
                                                                    s_1.isFuncDeclaration().vtblIndex.value += 1;
                                                                else if ((pequals(s_1.ident.value, funcdecl.ident.value)) && (pequals(s_1.parent.value, parent)))
                                                                {
                                                                    funcdecl.vtblIndex.value = i;
                                                                    found = true;
                                                                    s_1.isFuncDeclaration().vtblIndex.value += 1;
                                                                }
                                                            }
                                                        }
                                                        cd.vtbl.value.insert(funcdecl.vtblIndex.value, funcdecl);
                                                    }
                                                    else
                                                    {
                                                        vi = cd.vtbl.value.length.value;
                                                        cd.vtbl.value.push(funcdecl);
                                                        funcdecl.vtblIndex.value = vi;
                                                    }
                                                }
                                                break;
                                            case -2:
                                                funcdecl.errors.value = true;
                                                return ;
                                            default:
                                            FuncDeclaration fdv = cd.baseClass.value.vtbl.value.get(vi).isFuncDeclaration();
                                            FuncDeclaration fdc = cd.vtbl.value.get(vi).isFuncDeclaration();
                                            if ((pequals(fdc, funcdecl)))
                                            {
                                                doesoverride = true;
                                                break;
                                            }
                                            if ((pequals(fdc.toParent(), parent)))
                                            {
                                                if (((fdc.type.value.mod.value & 0xFF) == (fdv.type.value.mod.value & 0xFF)) && ((funcdecl.type.value.mod.value & 0xFF) != (fdv.type.value.mod.value & 0xFF)))
                                                    /*goto Lintro*/{ __dispatch1 = -1; continue dispatched_1; }
                                            }
                                            if (fdv.isDeprecated())
                                                deprecation(funcdecl.loc.value, new BytePtr("`%s` is overriding the deprecated method `%s`"), funcdecl.toPrettyChars(false), fdv.toPrettyChars(false));
                                            if (fdv.isFinalFunc())
                                                funcdecl.error(new BytePtr("cannot override `final` function `%s`"), fdv.toPrettyChars(false));
                                            if (!funcdecl.isOverride())
                                            {
                                                if (fdv.isFuture())
                                                {
                                                    deprecation(funcdecl.loc.value, new BytePtr("`@__future` base class method `%s` is being overridden by `%s`; rename the latter"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                    /*goto Lintro*/{ __dispatch1 = -1; continue dispatched_1; }
                                                }
                                                else
                                                {
                                                    int vi2 = funcdecl.findVtblIndex(ptr(cd.baseClass.value.vtbl), cd.baseClass.value.vtbl.value.length.value, false);
                                                    if ((vi2 < 0))
                                                        deprecation(funcdecl.loc.value, new BytePtr("cannot implicitly override base class method `%s` with `%s`; add `override` attribute"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                    else
                                                        error(funcdecl.loc.value, new BytePtr("cannot implicitly override base class method `%s` with `%s`; add `override` attribute"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                }
                                            }
                                            doesoverride = true;
                                            if ((pequals(fdc.toParent(), parent)))
                                            {
                                                boolean thismixin = funcdecl.parent.value.isClassDeclaration() != null;
                                                boolean fdcmixin = fdc.parent.value.isClassDeclaration() != null;
                                                if (((thismixin ? 1 : 0) == (fdcmixin ? 1 : 0)))
                                                {
                                                    funcdecl.error(new BytePtr("multiple overrides of same function"));
                                                }
                                                else if (thismixin)
                                                {
                                                    int vitmp = cd.vtbl.value.length.value;
                                                    cd.vtbl.value.push(fdc);
                                                    fdc.vtblIndex.value = vitmp;
                                                }
                                                else if (fdcmixin)
                                                {
                                                    int vitmp_1 = cd.vtbl.value.length.value;
                                                    cd.vtbl.value.push(funcdecl);
                                                    funcdecl.vtblIndex.value = vitmp_1;
                                                    break;
                                                }
                                                else
                                                {
                                                    break;
                                                }
                                            }
                                            cd.vtbl.value.set(vi, funcdecl);
                                            funcdecl.vtblIndex.value = vi;
                                            funcdecl.foverrides.push(fdv);
                                            if (fdv.tintro.value != null)
                                                funcdecl.tintro.value = fdv.tintro.value;
                                            else if (!funcdecl.type.value.equals(fdv.type.value))
                                            {
                                                IntRef offset = ref(0);
                                                if (fdv.type.value.nextOf().isBaseOf(funcdecl.type.value.nextOf(), ptr(offset)))
                                                {
                                                    funcdecl.tintro.value = fdv.type.value;
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
                                Slice<Ptr<BaseClass>> __r1157 = cd.interfaces.value.copy();
                                int __key1158 = 0;
                                for (; (__key1158 < __r1157.getLength());__key1158 += 1) {
                                    Ptr<BaseClass> b = __r1157.get(__key1158);
                                    vi = funcdecl.findVtblIndex(ptr((b.get()).sym.value.vtbl), (b.get()).sym.value.vtbl.value.length.value, true);
                                    switch (vi)
                                    {
                                        case -1:
                                            break;
                                        case -2:
                                            funcdecl.errors.value = true;
                                            return ;
                                        default:
                                        FuncDeclaration fdv = (FuncDeclaration)(b.get()).sym.value.vtbl.value.get(vi);
                                        Type ti = null;
                                        foundVtblMatch = true;
                                        funcdecl.foverrides.push(fdv);
                                        if (fdv.tintro.value != null)
                                            ti = fdv.tintro.value;
                                        else if (!funcdecl.type.value.equals(fdv.type.value))
                                        {
                                            IntRef offset = ref(0);
                                            if (fdv.type.value.nextOf().isBaseOf(funcdecl.type.value.nextOf(), ptr(offset)))
                                            {
                                                ti = fdv.type.value;
                                            }
                                        }
                                        if (ti != null)
                                        {
                                            if (funcdecl.tintro.value != null)
                                            {
                                                if (!funcdecl.tintro.value.nextOf().equals(ti.nextOf()) && !funcdecl.tintro.value.nextOf().isBaseOf(ti.nextOf(), null) && !ti.nextOf().isBaseOf(funcdecl.tintro.value.nextOf(), null))
                                                {
                                                    funcdecl.error(new BytePtr("incompatible covariant types `%s` and `%s`"), funcdecl.tintro.value.toChars(), ti.toChars());
                                                }
                                            }
                                            else
                                            {
                                                funcdecl.tintro.value = ti;
                                            }
                                        }
                                    }
                                }
                            }
                            if (foundVtblMatch)
                            {
                                /*goto L2*/throw Dispatch1.INSTANCE;
                            }
                            if (!doesoverride && funcdecl.isOverride() && (funcdecl.type.value.nextOf() != null) || !may_override)
                            {
                                Ptr<BaseClass> bc = null;
                                Dsymbol s = null;
                                {
                                    int i = 0;
                                    for (; (i < (cd.baseclasses.get()).length.value);i++){
                                        bc = (cd.baseclasses.get()).get(i);
                                        s = (bc.get()).sym.value.search_correct(funcdecl.ident.value);
                                        if (s != null)
                                            break;
                                    }
                                }
                                if (s != null)
                                {
                                    Ref<HdrGenState> hgs = ref(new HdrGenState());
                                    Ref<OutBuffer> buf = ref(new OutBuffer());
                                    try {
                                        FuncDeclaration fd = s.isFuncDeclaration();
                                        functionToBufferFull((TypeFunction)funcdecl.type.value, ptr(buf), new Identifier(funcdecl.toPrettyChars(false)), ptr(hgs), null);
                                        BytePtr funcdeclToChars = pcopy(buf.value.peekChars());
                                        if (fd != null)
                                        {
                                            Ref<OutBuffer> buf1 = ref(new OutBuffer());
                                            try {
                                                functionToBufferFull((TypeFunction)fd.type.value, ptr(buf1), new Identifier(fd.toPrettyChars(false)), ptr(hgs), null);
                                                error(funcdecl.loc.value, new BytePtr("function `%s` does not override any function, did you mean to override `%s`?"), funcdeclToChars, buf1.value.peekChars());
                                            }
                                            finally {
                                            }
                                        }
                                        else
                                        {
                                            error(funcdecl.loc.value, new BytePtr("function `%s` does not override any function, did you mean to override %s `%s`?"), funcdeclToChars, s.kind(), s.toPrettyChars(false));
                                            errorSupplemental(funcdecl.loc.value, new BytePtr("Functions are the only declarations that may be overriden"));
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
                            Slice<Ptr<BaseClass>> __r1159 = cd.interfaces.value.copy();
                            int __key1160 = 0;
                            for (; (__key1160 < __r1159.getLength());__key1160 += 1) {
                                Ptr<BaseClass> b = __r1159.get(__key1160);
                                if ((b.get()).sym.value != null)
                                {
                                    Dsymbol s = search_function((b.get()).sym.value, funcdecl.ident.value);
                                    if (s != null)
                                    {
                                        FuncDeclaration f2 = s.isFuncDeclaration();
                                        if (f2 != null)
                                        {
                                            f2 = f2.overloadExactMatch(funcdecl.type.value);
                                            if ((f2 != null) && f2.isFinalFunc() && (f2.prot().kind.value != Prot.Kind.private_))
                                                funcdecl.error(new BytePtr("cannot override `final` function `%s.%s`"), (b.get()).sym.value.toChars(), f2.toPrettyChars(false));
                                        }
                                    }
                                }
                            }
                        }
                        if (funcdecl.isOverride())
                        {
                            if ((funcdecl.storage_class.value & 137438953472L) != 0)
                                deprecation(funcdecl.loc.value, new BytePtr("`%s` cannot be annotated with `@disable` because it is overriding a function in the base class"), funcdecl.toPrettyChars(false));
                            if (funcdecl.isDeprecated())
                                deprecation(funcdecl.loc.value, new BytePtr("`%s` cannot be marked as `deprecated` because it is overriding a function in the base class"), funcdecl.toPrettyChars(false));
                        }
                    }
                    else if (funcdecl.isOverride() && (parent.isTemplateInstance() == null))
                        funcdecl.error(new BytePtr("`override` only applies to class member functions"));
                }
                {
                    TemplateInstance ti = parent.isTemplateInstance();
                    if ((ti) != null)
                        objc().setSelector(funcdecl, this.sc);
                }
                objc().validateSelector(funcdecl);
                f = funcdecl.type.value.toTypeFunction();
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            if ((funcdecl.fbody.value == null) && !allowsContractWithoutBody(funcdecl))
                funcdecl.error(new BytePtr("`in` and `out` contracts can only appear without a body when they are virtual interface functions or abstract"));
            if (funcdecl.isVirtual())
            {
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                {
                    for (; 1 != 0;){
                        TemplateInstance ti2 = ti.tempdecl.value.parent.value.isTemplateInstance();
                        if (ti2 == null)
                            break;
                        ti = ti2;
                    }
                    ClassDeclaration cd = ti.tempdecl.value.isClassMember();
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
            funcdecl.semanticRun.value = PASS.semanticdone;
            funcdecl._scope.value = (this.sc.get()).copy();
            (funcdecl._scope.value.get()).setNoFree();
            if (global.params.verbose && !dsymbolsem.funcDeclarationSemanticprintedMain)
            {
                BytePtr type = pcopy(funcdecl.isMain() ? new BytePtr("main") : funcdecl.isWinMain() ? new BytePtr("winmain") : funcdecl.isDllMain() ? new BytePtr("dllmain") : null);
                dmodule.Module mod = (this.sc.get())._module.value;
                if ((type != null) && (mod != null))
                {
                    dsymbolsem.funcDeclarationSemanticprintedMain = true;
                    BytePtr name = pcopy(FileName.searchPath(global.path, mod.srcfile.toChars(), true));
                    message(new BytePtr("entry     %-10s\u0009%s"), type, name);
                }
            }
            if ((funcdecl.fbody.value != null) && funcdecl.isMain() && (this.sc.get())._module.value.isRoot())
                Compiler.genCmain(this.sc);
            assert(((funcdecl.type.value.ty.value & 0xFF) != ENUMTY.Terror) || funcdecl.errors.value);
            {
                int __key1161 = 0;
                int __limit1162 = f.parameterList.length();
                for (; (__key1161 < __limit1162);__key1161 += 1) {
                    int i = __key1161;
                    Parameter param = f.parameterList.get(i);
                    if ((param != null) && (param.userAttribDecl.value != null))
                        dsymbolSemantic(param.userAttribDecl.value, this.sc);
                }
            }
        }

        public  void visit(FuncDeclaration funcdecl) {
            this.funcDeclarationSemantic(funcdecl);
        }

        public  void visit(CtorDeclaration ctd) {
            if ((ctd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (ctd._scope.value != null)
            {
                this.sc = ctd._scope.value;
                ctd._scope.value = null;
            }
            ctd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = ctd.toParentDecl();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(ctd.loc.value, new BytePtr("constructor can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                ctd.type.value = Type.terror.value;
                ctd.errors.value = true;
                return ;
            }
            this.sc = (this.sc.get()).push();
            if (((this.sc.get()).stc.value & 1L) != 0)
            {
                if (((this.sc.get()).stc.value & 536870912L) != 0)
                    deprecation(ctd.loc.value, new BytePtr("`shared static` has no effect on a constructor inside a `shared static` block. Use `shared static this()`"));
                else
                    deprecation(ctd.loc.value, new BytePtr("`static` has no effect on a constructor inside a `static` block. Use `static this()`"));
            }
            (this.sc.get()).stc.value &= -2L;
            (this.sc.get()).flags.value |= 1;
            this.funcDeclarationSemantic(ctd);
            (this.sc.get()).pop();
            if (ctd.errors.value)
                return ;
            TypeFunction tf = ctd.type.value.toTypeFunction();
            if ((ad != null) && (ctd.parent.value.isTemplateInstance() == null) || (ctd.parent.value.isTemplateMixin() != null))
            {
                int dim = tf.parameterList.length();
                {
                    StructDeclaration sd = ad.isStructDeclaration();
                    if ((sd) != null)
                    {
                        if ((dim == 0) && (tf.parameterList.varargs.value == VarArg.none))
                        {
                            if ((ctd.fbody.value != null) || ((ctd.storage_class.value & 137438953472L) == 0))
                            {
                                ctd.error(new BytePtr("default constructor for structs only allowed with `@disable`, no body, and no parameters"));
                                ctd.storage_class.value |= 137438953472L;
                                ctd.fbody.value = null;
                            }
                            sd.noDefaultCtor.value = true;
                        }
                        else if ((dim == 0) && (tf.parameterList.varargs.value != VarArg.none))
                        {
                        }
                        else if ((dim != 0) && (tf.parameterList.get(0).defaultArg.value != null))
                        {
                            if ((ctd.storage_class.value & 137438953472L) != 0)
                            {
                                ctd.error(new BytePtr("is marked `@disable`, so it cannot have default arguments for all parameters."));
                                errorSupplemental(ctd.loc.value, new BytePtr("Use `@disable this();` if you want to disable default initialization."));
                            }
                            else
                                ctd.error(new BytePtr("all parameters have default arguments, but structs cannot have default constructors."));
                        }
                        else if ((dim == 1) || (dim > 1) && (tf.parameterList.get(1).defaultArg.value != null))
                        {
                            Parameter param = Parameter.getNth(tf.parameterList.parameters.value, 0, null);
                            if (((param.storageClass.value & 2097152L) != 0) && (pequals(param.type.value.mutableOf().unSharedOf(), sd.type.value.mutableOf().unSharedOf())))
                            {
                                ctd.isCpCtor = true;
                            }
                        }
                    }
                    else if ((dim == 0) && (tf.parameterList.varargs.value == VarArg.none))
                    {
                        ad.defaultCtor = ctd;
                    }
                }
            }
        }

        public  void visit(PostBlitDeclaration pbd) {
            if ((pbd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (pbd._scope.value != null)
            {
                this.sc = pbd._scope.value;
                pbd._scope.value = null;
            }
            pbd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = pbd.toParent2();
            StructDeclaration ad = p.isStructDeclaration();
            if (ad == null)
            {
                error(pbd.loc.value, new BytePtr("postblit can only be a member of struct, not %s `%s`"), p.kind(), p.toChars());
                pbd.type.value = Type.terror.value;
                pbd.errors.value = true;
                return ;
            }
            if ((pequals(pbd.ident.value, Id.postblit.value)) && (pbd.semanticRun.value < PASS.semantic))
                ad.postblits.push(pbd);
            if (pbd.type.value == null)
                pbd.type.value = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid.value, LINK.d, pbd.storage_class.value);
            this.sc = (this.sc.get()).push();
            (this.sc.get()).stc.value &= -2L;
            (this.sc.get()).linkage.value = LINK.d;
            this.funcDeclarationSemantic(pbd);
            (this.sc.get()).pop();
        }

        public  void visit(DtorDeclaration dd) {
            if ((dd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (dd._scope.value != null)
            {
                this.sc = dd._scope.value;
                dd._scope.value = null;
            }
            dd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = dd.toParent2();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(dd.loc.value, new BytePtr("destructor can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                dd.type.value = Type.terror.value;
                dd.errors.value = true;
                return ;
            }
            if ((pequals(dd.ident.value, Id.dtor.value)) && (dd.semanticRun.value < PASS.semantic))
                ad.dtors.push(dd);
            if (dd.type.value == null)
            {
                dd.type.value = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid.value, LINK.d, dd.storage_class.value);
                if ((ad.classKind.value == ClassKind.cpp) && (pequals(dd.ident.value, Id.dtor.value)))
                {
                    {
                        ClassDeclaration cldec = ad.isClassDeclaration();
                        if ((cldec) != null)
                        {
                            assert((cldec.cppDtorVtblIndex == -1));
                            if ((cldec.baseClass.value != null) && (cldec.baseClass.value.cppDtorVtblIndex != -1))
                            {
                                cldec.cppDtorVtblIndex = cldec.baseClass.value.cppDtorVtblIndex;
                            }
                            else if (!dd.isFinal())
                            {
                                cldec.cppDtorVtblIndex = cldec.vtbl.value.length.value;
                                cldec.vtbl.value.push(dd);
                                if (target.twoDtorInVtable)
                                    cldec.vtbl.value.push(dd);
                            }
                        }
                    }
                }
            }
            this.sc = (this.sc.get()).push();
            (this.sc.get()).stc.value &= -2L;
            if (((this.sc.get()).linkage.value != LINK.cpp))
                (this.sc.get()).linkage.value = LINK.d;
            this.funcDeclarationSemantic(dd);
            (this.sc.get()).pop();
        }

        public  void visit(StaticCtorDeclaration scd) {
            if ((scd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (scd._scope.value != null)
            {
                this.sc = scd._scope.value;
                scd._scope.value = null;
            }
            scd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = scd.parent.value.pastMixin();
            if (p.isScopeDsymbol() == null)
            {
                BytePtr s = pcopy(scd.isSharedStaticCtorDeclaration() != null ? new BytePtr("shared ") : new BytePtr(""));
                error(scd.loc.value, new BytePtr("`%sstatic` constructor can only be member of module/aggregate/template, not %s `%s`"), s, p.kind(), p.toChars());
                scd.type.value = Type.terror.value;
                scd.errors.value = true;
                return ;
            }
            if (scd.type.value == null)
                scd.type.value = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid.value, LINK.d, scd.storage_class.value);
            if ((scd.isInstantiated() != null) && (scd.semanticRun.value < PASS.semantic))
            {
                VarDeclaration v = new VarDeclaration(Loc.initial.value, Type.tint32.value, Id.gate, null, 0L);
                v.storage_class.value = (STC.temp | (scd.isSharedStaticCtorDeclaration() != null ? STC.static_ : STC.tls));
                Ptr<DArray<Statement>> sa = refPtr(new DArray<Statement>());
                Statement s = new ExpStatement(Loc.initial.value, v);
                (sa.get()).push(s);
                Expression e = new IdentifierExp(Loc.initial.value, v.ident.value);
                e = new AddAssignExp(Loc.initial.value, e, literal_356A192B7913B04C());
                e = new EqualExp(TOK.notEqual, Loc.initial.value, e, literal_356A192B7913B04C());
                s = new IfStatement(Loc.initial.value, null, e, new ReturnStatement(Loc.initial.value, null), null, Loc.initial.value);
                (sa.get()).push(s);
                if (scd.fbody.value != null)
                    (sa.get()).push(scd.fbody.value);
                scd.fbody.value = new CompoundStatement(Loc.initial.value, sa);
            }
            this.funcDeclarationSemantic(scd);
            dmodule.Module m = scd.getModule();
            if (m == null)
                m = (this.sc.get())._module.value;
            if (m != null)
            {
                m.needmoduleinfo = 1;
            }
        }

        public  void visit(StaticDtorDeclaration sdd) {
            if ((sdd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (sdd._scope.value != null)
            {
                this.sc = sdd._scope.value;
                sdd._scope.value = null;
            }
            sdd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = sdd.parent.value.pastMixin();
            if (p.isScopeDsymbol() == null)
            {
                BytePtr s = pcopy(sdd.isSharedStaticDtorDeclaration() != null ? new BytePtr("shared ") : new BytePtr(""));
                error(sdd.loc.value, new BytePtr("`%sstatic` destructor can only be member of module/aggregate/template, not %s `%s`"), s, p.kind(), p.toChars());
                sdd.type.value = Type.terror.value;
                sdd.errors.value = true;
                return ;
            }
            if (sdd.type.value == null)
                sdd.type.value = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid.value, LINK.d, sdd.storage_class.value);
            if ((sdd.isInstantiated() != null) && (sdd.semanticRun.value < PASS.semantic))
            {
                VarDeclaration v = new VarDeclaration(Loc.initial.value, Type.tint32.value, Id.gate, null, 0L);
                v.storage_class.value = (STC.temp | (sdd.isSharedStaticDtorDeclaration() != null ? STC.static_ : STC.tls));
                Ptr<DArray<Statement>> sa = refPtr(new DArray<Statement>());
                Statement s = new ExpStatement(Loc.initial.value, v);
                (sa.get()).push(s);
                Expression e = new IdentifierExp(Loc.initial.value, v.ident.value);
                e = new AddAssignExp(Loc.initial.value, e, literal_7984B0A0E139CABA());
                e = new EqualExp(TOK.notEqual, Loc.initial.value, e, literal_B6589FC6AB0DC82C());
                s = new IfStatement(Loc.initial.value, null, e, new ReturnStatement(Loc.initial.value, null), null, Loc.initial.value);
                (sa.get()).push(s);
                if (sdd.fbody.value != null)
                    (sa.get()).push(sdd.fbody.value);
                sdd.fbody.value = new CompoundStatement(Loc.initial.value, sa);
                sdd.vgate = v;
            }
            this.funcDeclarationSemantic(sdd);
            dmodule.Module m = sdd.getModule();
            if (m == null)
                m = (this.sc.get())._module.value;
            if (m != null)
            {
                m.needmoduleinfo = 1;
            }
        }

        public  void visit(InvariantDeclaration invd) {
            if ((invd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (invd._scope.value != null)
            {
                this.sc = invd._scope.value;
                invd._scope.value = null;
            }
            invd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = invd.parent.value.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(invd.loc.value, new BytePtr("`invariant` can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                invd.type.value = Type.terror.value;
                invd.errors.value = true;
                return ;
            }
            if ((!pequals(invd.ident.value, Id.classInvariant)) && (invd.semanticRun.value < PASS.semantic) && (ad.isUnionDeclaration() == null))
                ad.invs.push(invd);
            if (invd.type.value == null)
                invd.type.value = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid.value, LINK.d, invd.storage_class.value);
            this.sc = (this.sc.get()).push();
            (this.sc.get()).stc.value &= -2L;
            (this.sc.get()).stc.value |= 4L;
            (this.sc.get()).flags.value = (this.sc.get()).flags.value & -97 | 32;
            (this.sc.get()).linkage.value = LINK.d;
            this.funcDeclarationSemantic(invd);
            (this.sc.get()).pop();
        }

        public  void visit(UnitTestDeclaration utd) {
            if ((utd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (utd._scope.value != null)
            {
                this.sc = utd._scope.value;
                utd._scope.value = null;
            }
            utd.protection = (this.sc.get()).protection.value.copy();
            utd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = utd.parent.value.pastMixin();
            if (p.isScopeDsymbol() == null)
            {
                error(utd.loc.value, new BytePtr("`unittest` can only be a member of module/aggregate/template, not %s `%s`"), p.kind(), p.toChars());
                utd.type.value = Type.terror.value;
                utd.errors.value = true;
                return ;
            }
            if (global.params.useUnitTests)
            {
                if (utd.type.value == null)
                    utd.type.value = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid.value, LINK.d, utd.storage_class.value);
                Ptr<Scope> sc2 = (this.sc.get()).push();
                (sc2.get()).linkage.value = LINK.d;
                this.funcDeclarationSemantic(utd);
                (sc2.get()).pop();
            }
        }

        public  void visit(NewDeclaration nd) {
            if (!nd.isDisabled())
            {
                error(nd.loc.value, new BytePtr("class allocators are obsolete, consider moving the allocation strategy outside of the class"));
            }
            if ((nd.semanticRun.value >= PASS.semanticdone))
                return ;
            if (nd._scope.value != null)
            {
                this.sc = nd._scope.value;
                nd._scope.value = null;
            }
            nd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = nd.parent.value.pastMixin();
            if (p.isAggregateDeclaration() == null)
            {
                error(nd.loc.value, new BytePtr("allocator can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                nd.type.value = Type.terror.value;
                nd.errors.value = true;
                return ;
            }
            Type tret = Type.tvoid.value.pointerTo();
            if (nd.type.value == null)
                nd.type.value = new TypeFunction(new ParameterList(nd.parameters, nd.varargs), tret, LINK.d, nd.storage_class.value);
            nd.type.value = typeSemantic(nd.type.value, nd.loc.value, this.sc);
            if (!nd.isDisabled())
            {
                TypeFunction tf = nd.type.value.toTypeFunction();
                if ((tf.parameterList.length() < 1))
                {
                    nd.error(new BytePtr("at least one argument of type `size_t` expected"));
                }
                else
                {
                    Parameter fparam = tf.parameterList.get(0);
                    if (!fparam.type.value.equals(Type.tsize_t.value))
                        nd.error(new BytePtr("first argument must be type `size_t`, not `%s`"), fparam.type.value.toChars());
                }
            }
            this.funcDeclarationSemantic(nd);
        }

        public  void visit(DeleteDeclaration deld) {
            error(deld.loc.value, new BytePtr("class deallocators are obsolete, consider moving the deallocation strategy outside of the class"));
            if ((deld.semanticRun.value >= PASS.semanticdone))
                return ;
            if (deld._scope.value != null)
            {
                this.sc = deld._scope.value;
                deld._scope.value = null;
            }
            deld.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = deld.parent.value.pastMixin();
            if (p.isAggregateDeclaration() == null)
            {
                error(deld.loc.value, new BytePtr("deallocator can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                deld.type.value = Type.terror.value;
                deld.errors.value = true;
                return ;
            }
            if (deld.type.value == null)
                deld.type.value = new TypeFunction(new ParameterList(deld.parameters, VarArg.none), Type.tvoid.value, LINK.d, deld.storage_class.value);
            deld.type.value = typeSemantic(deld.type.value, deld.loc.value, this.sc);
            TypeFunction tf = deld.type.value.toTypeFunction();
            if ((tf.parameterList.length() != 1))
            {
                deld.error(new BytePtr("one argument of type `void*` expected"));
            }
            else
            {
                Parameter fparam = tf.parameterList.get(0);
                if (!fparam.type.value.equals(Type.tvoid.value.pointerTo()))
                    deld.error(new BytePtr("one argument of type `void*` expected, not `%s`"), fparam.type.value.toChars());
            }
            this.funcDeclarationSemantic(deld);
        }

        public  void reinforceInvariant(AggregateDeclaration ad, Ptr<Scope> sc) {
            {
                int i = 0;
                for (; (i < (ad.members.value.get()).length.value);i++){
                    if ((ad.members.value.get()).get(i) == null)
                        continue;
                    FuncDeclaration fd = (ad.members.value.get()).get(i).isFuncDeclaration();
                    if ((fd == null) || fd.generated || (fd.semanticRun.value != PASS.semantic3done))
                        continue;
                    FuncDeclaration fd_temp = fd.syntaxCopy(null).isFuncDeclaration();
                    fd_temp.storage_class.value &= -257L;
                    {
                        ClassDeclaration cd = ad.isClassDeclaration();
                        if ((cd) != null)
                            cd.vtbl.value.remove(fd.vtblIndex.value);
                    }
                    dsymbolSemantic(fd_temp, sc);
                    ad.members.value.get().set(i, fd_temp);
                }
            }
        }

        public  void visit(StructDeclaration sd) {
            if ((sd.semanticRun.value >= PASS.semanticdone))
                return ;
            int errors = global.errors.value;
            Ptr<Scope> scx = null;
            if (sd._scope.value != null)
            {
                this.sc = sd._scope.value;
                scx = sd._scope.value;
                sd._scope.value = null;
            }
            if (sd.parent.value == null)
            {
                assert(((this.sc.get()).parent.value != null) && ((this.sc.get()).func.value != null));
                sd.parent.value = (this.sc.get()).parent.value;
            }
            assert((sd.parent.value != null) && !sd.isAnonymous());
            if (sd.errors.value)
                sd.type.value = Type.terror.value;
            if ((sd.semanticRun.value == PASS.init))
                sd.type.value = sd.type.value.addSTC((this.sc.get()).stc.value | sd.storage_class);
            sd.type.value = typeSemantic(sd.type.value, sd.loc.value, this.sc);
            {
                TypeStruct ts = sd.type.value.isTypeStruct();
                if ((ts) != null)
                    if ((!pequals(ts.sym.value, sd)))
                    {
                        TemplateInstance ti = ts.sym.value.isInstantiated();
                        if ((ti != null) && isError(ti))
                            ts.sym.value = sd;
                    }
            }
            Ungag ungag = sd.ungagSpeculative().copy();
            try {
                if ((sd.semanticRun.value == PASS.init))
                {
                    sd.protection = (this.sc.get()).protection.value.copy();
                    sd.alignment = (this.sc.get()).alignment();
                    sd.storage_class |= (this.sc.get()).stc.value;
                    if ((sd.storage_class & 1024L) != 0)
                        sd.isdeprecated = true;
                    if ((sd.storage_class & 16L) != 0)
                        sd.error(new BytePtr("structs, unions cannot be `abstract`"));
                    sd.userAttribDecl = (this.sc.get()).userAttribDecl;
                    if (((this.sc.get()).linkage.value == LINK.cpp))
                        sd.classKind.value = ClassKind.cpp;
                    sd.namespace = (this.sc.get()).namespace;
                }
                else if ((sd.symtab != null) && (scx == null))
                    return ;
                sd.semanticRun.value = PASS.semantic;
                if (sd.members.value == null)
                {
                    sd.semanticRun.value = PASS.semanticdone;
                    return ;
                }
                if (sd.symtab == null)
                {
                    sd.symtab = new DsymbolTable();
                    Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s) {
                            s.addMember(sc, sd);
                            return null;
                        }
                    };
                    foreachDsymbol(sd.members.value, __lambda2);
                }
                Ptr<Scope> sc2 = sd.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.setScope(sc2);
                        return null;
                    }
                };
                foreachDsymbol(sd.members.value, __lambda3);
                Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.importAll(sc2);
                        return null;
                    }
                };
                foreachDsymbol(sd.members.value, __lambda4);
                Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        dsymbolSemantic(s, sc2);
                        (sd.errors.value ? 1 : 0) |= (s.errors.value ? 1 : 0);
                    }
                };
                foreachDsymbol(sd.members.value, __lambda5);
                if (sd.errors.value)
                    sd.type.value = Type.terror.value;
                if (!sd.determineFields())
                {
                    if (((sd.type.value.ty.value & 0xFF) != ENUMTY.Terror))
                    {
                        sd.error(sd.loc.value, new BytePtr("circular or forward reference"));
                        sd.errors.value = true;
                        sd.type.value = Type.terror.value;
                    }
                    (sc2.get()).pop();
                    sd.semanticRun.value = PASS.semanticdone;
                    return ;
                }
                {
                    Slice<VarDeclaration> __r1163 = sd.fields.opSlice().copy();
                    int __key1164 = 0;
                    for (; (__key1164 < __r1163.getLength());__key1164 += 1) {
                        VarDeclaration v = __r1163.get(__key1164);
                        Type tb = v.type.value.baseElemOf();
                        if (((tb.ty.value & 0xFF) != ENUMTY.Tstruct))
                            continue;
                        StructDeclaration sdec = ((TypeStruct)tb).sym.value;
                        if ((sdec.semanticRun.value >= PASS.semanticdone))
                            continue;
                        (sc2.get()).pop();
                        sd._scope.value = scx != null ? scx : (this.sc.get()).copy();
                        (sd._scope.value.get()).setNoFree();
                        dmodule.Module.addDeferredSemantic(sd);
                        return ;
                    }
                }
                sd.aggNew = (NewDeclaration)sd.search(Loc.initial.value, Id.classNew, 8);
                sd.aggDelete.value = (DeleteDeclaration)sd.search(Loc.initial.value, Id.classDelete, 8);
                sd.ctor.value = sd.searchCtor();
                sd.dtor.value = buildDtor(sd, sc2);
                sd.tidtor = buildExternDDtor(sd, sc2);
                sd.postblit.value = buildPostBlit(sd, sc2);
                sd.hasCopyCtor = buildCopyCtor(sd, sc2);
                buildOpAssign(sd, sc2);
                buildOpEquals(sd, sc2);
                if (global.params.useTypeInfo.value && (Type.dtypeinfo.value != null))
                {
                    sd.xeq.value = buildXopEquals(sd, sc2);
                    sd.xcmp.value = buildXopCmp(sd, sc2);
                    sd.xhash.value = buildXtoHash(sd, sc2);
                }
                sd.inv = buildInv(sd, sc2);
                if (sd.inv != null)
                    this.reinforceInvariant(sd, sc2);
                dmodule.Module.dprogress++;
                sd.semanticRun.value = PASS.semanticdone;
                (sc2.get()).pop();
                if (sd.ctor.value != null)
                {
                    Dsymbol scall = sd.search(Loc.initial.value, Id.call.value, 8);
                    if (scall != null)
                    {
                        int xerrors = global.startGagging();
                        this.sc = (this.sc.get()).push();
                        (this.sc.get()).tinst = null;
                        (this.sc.get()).minst.value = null;
                        FuncDeclaration fcall = resolveFuncCall(sd.loc.value, this.sc, scall, null, null, null, FuncResolveFlag.quiet);
                        this.sc = (this.sc.get()).pop();
                        global.endGagging(xerrors);
                        if ((fcall != null) && fcall.isStatic())
                        {
                            sd.error(fcall.loc.value, new BytePtr("`static opCall` is hidden by constructors and can never be called"));
                            errorSupplemental(fcall.loc.value, new BytePtr("Please use a factory method instead, or replace all constructors with `static opCall`."));
                        }
                    }
                }
                if (((sd.type.value.ty.value & 0xFF) == ENUMTY.Tstruct) && (!pequals(((TypeStruct)sd.type.value).sym.value, sd)))
                {
                    StructDeclaration sym = ((TypeStruct)sd.type.value).sym.value;
                    sd.error(new BytePtr("already exists at %s. Perhaps in another function with the same name?"), sym.loc.value.toChars(global.params.showColumns.value));
                }
                if ((global.errors.value != errors))
                {
                    sd.type.value = Type.terror.value;
                    sd.errors.value = true;
                    if (sd.deferred != null)
                        sd.deferred.errors.value = true;
                }
                if ((sd.deferred != null) && (global.gag.value == 0))
                {
                    semantic2(sd.deferred, this.sc);
                    semantic3(sd.deferred, this.sc);
                }
            }
            finally {
            }
        }

        public  void interfaceSemantic(ClassDeclaration cd) {
            cd.vtblInterfaces = refPtr(new DArray<Ptr<BaseClass>>());
            (cd.vtblInterfaces.get()).reserve(cd.interfaces.value.getLength());
            {
                Slice<Ptr<BaseClass>> __r1165 = cd.interfaces.value.copy();
                int __key1166 = 0;
                for (; (__key1166 < __r1165.getLength());__key1166 += 1) {
                    Ptr<BaseClass> b = __r1165.get(__key1166);
                    (cd.vtblInterfaces.get()).push(b);
                    (b.get()).copyBaseInterfaces(cd.vtblInterfaces);
                }
            }
        }

        public  void visit(ClassDeclaration cldec) {
            if ((cldec.semanticRun.value >= PASS.semanticdone))
                return ;
            int errors = global.errors.value;
            Ptr<Scope> scx = null;
            if (cldec._scope.value != null)
            {
                this.sc = cldec._scope.value;
                scx = cldec._scope.value;
                cldec._scope.value = null;
            }
            if (cldec.parent.value == null)
            {
                assert((this.sc.get()).parent.value != null);
                cldec.parent.value = (this.sc.get()).parent.value;
            }
            if (cldec.errors.value)
                cldec.type.value = Type.terror.value;
            cldec.type.value = typeSemantic(cldec.type.value, cldec.loc.value, this.sc);
            {
                TypeClass tc = cldec.type.value.isTypeClass();
                if ((tc) != null)
                    if ((!pequals(tc.sym.value, cldec)))
                    {
                        TemplateInstance ti = tc.sym.value.isInstantiated();
                        if ((ti != null) && isError(ti))
                            tc.sym.value = cldec;
                    }
            }
            Ungag ungag = cldec.ungagSpeculative().copy();
            try {
                if ((cldec.semanticRun.value == PASS.init))
                {
                    cldec.protection = (this.sc.get()).protection.value.copy();
                    cldec.storage_class |= (this.sc.get()).stc.value;
                    if ((cldec.storage_class & 1024L) != 0)
                        cldec.isdeprecated = true;
                    if ((cldec.storage_class & 256L) != 0)
                        cldec.error(new BytePtr("storage class `auto` is invalid when declaring a class, did you mean to use `scope`?"));
                    if ((cldec.storage_class & 524288L) != 0)
                        cldec.stack = true;
                    if ((cldec.storage_class & 16L) != 0)
                        cldec.isabstract.value = Abstract.yes;
                    cldec.userAttribDecl = (this.sc.get()).userAttribDecl;
                    if (((this.sc.get()).linkage.value == LINK.cpp))
                        cldec.classKind.value = ClassKind.cpp;
                    cldec.namespace = (this.sc.get()).namespace;
                    if (((this.sc.get()).linkage.value == LINK.objc))
                        objc().setObjc(cldec);
                }
                else if ((cldec.symtab != null) && (scx == null))
                {
                    return ;
                }
                cldec.semanticRun.value = PASS.semantic;
                try {
                    if ((cldec.baseok < Baseok.done))
                    {
                        // from template resolveBase!(Type)
                        Function1<Type,Type> resolveBaseType = new Function1<Type,Type>(){
                            public Type invoke(Type exp) {
                                if (scx == null)
                                {
                                    scx = (sc.get()).copy();
                                    (scx.get()).setNoFree();
                                }
                                cldec._scope.value = scx;
                                Type r = exp.invoke();
                                cldec._scope.value = null;
                                return r;
                            }
                        };

                        // from template resolveBase!(Void)
                        Function1<Void,Void> resolveBaseVoid = new Function1<Void,Void>(){
                            public Void invoke(Void exp) {
                                if (scx == null)
                                {
                                    scx = (sc.get()).copy();
                                    (scx.get()).setNoFree();
                                }
                                cldec._scope.value = scx;
                                exp.invoke();
                                cldec._scope.value = null;
                            }
                        };

                        cldec.baseok = Baseok.start;
                        {
                            int i = 0;
                            for (; (i < (cldec.baseclasses.get()).length.value);){
                                Ptr<BaseClass> b = (cldec.baseclasses.get()).get(i);
                                Function0<Type> __dgliteral2 = new Function0<Type>(){
                                    public Type invoke() {
                                        return typeSemantic((b.get()).type.value, cldec.loc.value, sc);
                                    }
                                };
                                (b.get()).type.value = resolveBaseType.invoke(__dgliteral2);
                                Type tb = (b.get()).type.value.toBasetype();
                                {
                                    TypeTuple tup = tb.isTypeTuple();
                                    if ((tup) != null)
                                    {
                                        (cldec.baseclasses.get()).remove(i);
                                        int dim = Parameter.dim(tup.arguments.value);
                                        {
                                            int j = 0;
                                            for (; (j < dim);j++){
                                                Parameter arg = Parameter.getNth(tup.arguments.value, j, null);
                                                b = refPtr(new BaseClass(arg.type.value));
                                                (cldec.baseclasses.get()).insert(i + j, b);
                                            }
                                        }
                                    }
                                    else
                                        i++;
                                }
                            }
                        }
                        if ((cldec.baseok >= Baseok.done))
                        {
                            if ((cldec.semanticRun.value >= PASS.semanticdone))
                                return ;
                            /*goto Lancestorsdone*/throw Dispatch0.INSTANCE;
                        }
                        if ((cldec.baseclasses.get()).length.value != 0)
                        {
                            Ptr<BaseClass> b = (cldec.baseclasses.get()).get(0);
                            Type tb = (b.get()).type.value.toBasetype();
                            TypeClass tc = tb.isTypeClass();
                            try {
                                if (tc == null)
                                {
                                    if ((!pequals((b.get()).type.value, Type.terror.value)))
                                        cldec.error(new BytePtr("base type must be `class` or `interface`, not `%s`"), (b.get()).type.value.toChars());
                                    (cldec.baseclasses.get()).remove(0);
                                    /*goto L7*/throw Dispatch0.INSTANCE;
                                }
                                if (tc.sym.value.isDeprecated())
                                {
                                    if (!cldec.isDeprecated())
                                    {
                                        cldec.isdeprecated = true;
                                        tc.checkDeprecated(cldec.loc.value, this.sc);
                                    }
                                }
                                if (tc.sym.value.isInterfaceDeclaration() != null)
                                    /*goto L7*/throw Dispatch0.INSTANCE;
                                {
                                    ClassDeclaration cdb = tc.sym.value;
                                L_outer7:
                                    for (; cdb != null;cdb = cdb.baseClass.value){
                                        if ((pequals(cdb, cldec)))
                                        {
                                            cldec.error(new BytePtr("circular inheritance"));
                                            (cldec.baseclasses.get()).remove(0);
                                            /*goto L7*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                                cldec.baseClass.value = tc.sym.value;
                                (b.get()).sym.value = cldec.baseClass.value;
                                if ((tc.sym.value.baseok < Baseok.done)) {
                                    Function0<Void> __dgliteral3 = new Function0<Void>() {
                                        public Void invoke() {
                                            dsymbolSemantic(tc.sym.value, null);
                                        }
                                    };
                                    resolveBaseVoid.invoke(__dgliteral3);
                                }
                                if ((tc.sym.value.baseok < Baseok.done))
                                {
                                    if (tc.sym.value._scope.value != null)
                                        dmodule.Module.addDeferredSemantic(tc.sym.value);
                                    cldec.baseok = Baseok.none;
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*L7:*/
                        }
                        boolean multiClassError = false;
                        {
                            int i = cldec.baseClass.value != null ? 1 : 0;
                            for (; (i < (cldec.baseclasses.get()).length.value);){
                                Ptr<BaseClass> b = (cldec.baseclasses.get()).get(i);
                                Type tb = (b.get()).type.value.toBasetype();
                                TypeClass tc = tb.isTypeClass();
                                if ((tc == null) || (tc.sym.value.isInterfaceDeclaration() == null))
                                {
                                    if (tc != null)
                                    {
                                        if (!multiClassError)
                                        {
                                            error(cldec.loc.value, new BytePtr("`%s`: multiple class inheritance is not supported. Use multiple interface inheritance and/or composition."), cldec.toPrettyChars(false));
                                            multiClassError = true;
                                        }
                                        if (tc.sym.value.fields.length.value != 0)
                                            errorSupplemental(cldec.loc.value, new BytePtr("`%s` has fields, consider making it a member of `%s`"), (b.get()).type.value.toChars(), cldec.type.value.toChars());
                                        else
                                            errorSupplemental(cldec.loc.value, new BytePtr("`%s` has no fields, consider making it an `interface`"), (b.get()).type.value.toChars());
                                    }
                                    else if ((!pequals((b.get()).type.value, Type.terror.value)))
                                    {
                                        error(cldec.loc.value, new BytePtr("`%s`: base type must be `interface`, not `%s`"), cldec.toPrettyChars(false), (b.get()).type.value.toChars());
                                    }
                                    (cldec.baseclasses.get()).remove(i);
                                    continue;
                                }
                                {
                                    int j = cldec.baseClass.value != null ? 1 : 0;
                                    for (; (j < i);j++){
                                        Ptr<BaseClass> b2 = (cldec.baseclasses.get()).get(j);
                                        if ((pequals((b2.get()).sym.value, tc.sym.value)))
                                        {
                                            cldec.error(new BytePtr("inherits from duplicate interface `%s`"), (b2.get()).sym.value.toChars());
                                            (cldec.baseclasses.get()).remove(i);
                                            continue;
                                        }
                                    }
                                }
                                if (tc.sym.value.isDeprecated())
                                {
                                    if (!cldec.isDeprecated())
                                    {
                                        cldec.isdeprecated = true;
                                        tc.checkDeprecated(cldec.loc.value, this.sc);
                                    }
                                }
                                (b.get()).sym.value = tc.sym.value;
                                if ((tc.sym.value.baseok < Baseok.done))
                                    Function0<Void> __dgliteral4 = new Function0<Void>(){
                                        public Void invoke() {
                                            dsymbolSemantic(tc.sym.value, null);
                                        }
                                    };
                                    resolveBaseVoid.invoke(__dgliteral4);
                                if ((tc.sym.value.baseok < Baseok.done))
                                {
                                    if (tc.sym.value._scope.value != null)
                                        dmodule.Module.addDeferredSemantic(tc.sym.value);
                                    cldec.baseok = Baseok.none;
                                }
                                i++;
                            }
                        }
                        if ((cldec.baseok == Baseok.none))
                        {
                            cldec._scope.value = scx != null ? scx : (this.sc.get()).copy();
                            (cldec._scope.value.get()).setNoFree();
                            dmodule.Module.addDeferredSemantic(cldec);
                            return ;
                        }
                        cldec.baseok = Baseok.done;
                        if ((cldec.classKind.value == ClassKind.objc) || (cldec.baseClass.value != null) && (cldec.baseClass.value.classKind.value == ClassKind.objc))
                            cldec.classKind.value = ClassKind.objc;
                        if ((cldec.baseClass.value == null) && (!pequals(cldec.ident.value, Id.Object.value)) && (ClassDeclaration.object.value != null) && (cldec.classKind.value == ClassKind.d))
                        {
                            Function0<Void> badObjectDotD = new Function0<Void>(){
                                public Void invoke() {
                                    cldec.error(new BytePtr("missing or corrupt object.d"));
                                    fatal();
                                }
                            };
                            if ((ClassDeclaration.object.value == null) || ClassDeclaration.object.value.errors.value)
                                badObjectDotD.invoke();
                            Type t = ClassDeclaration.object.value.type.value;
                            t = typeSemantic(t, cldec.loc.value, this.sc).toBasetype();
                            if (((t.ty.value & 0xFF) == ENUMTY.Terror))
                                badObjectDotD.invoke();
                            TypeClass tc = t.isTypeClass();
                            assert(tc != null);
                            Ptr<BaseClass> b = refPtr(new BaseClass(tc));
                            (cldec.baseclasses.get()).shift(b);
                            cldec.baseClass.value = tc.sym.value;
                            assert(cldec.baseClass.value.isInterfaceDeclaration() == null);
                            (b.get()).sym.value = cldec.baseClass.value;
                        }
                        if (cldec.baseClass.value != null)
                        {
                            if ((cldec.baseClass.value.storage_class & 8L) != 0)
                                cldec.error(new BytePtr("cannot inherit from class `%s` because it is `final`"), cldec.baseClass.value.toChars());
                            if (cldec.baseClass.value.isCOMclass())
                                cldec.com = true;
                            if (cldec.baseClass.value.isCPPclass())
                                cldec.classKind.value = ClassKind.cpp;
                            if (cldec.baseClass.value.stack)
                                cldec.stack = true;
                            cldec.enclosing = cldec.baseClass.value.enclosing;
                            cldec.storage_class |= cldec.baseClass.value.storage_class & 2685403140L;
                        }
                        cldec.interfaces.value = (cldec.baseclasses.get()).tdata().slice(cldec.baseClass.value != null ? 1 : 0,(cldec.baseclasses.get()).length.value).copy();
                        {
                            Slice<Ptr<BaseClass>> __r1167 = cldec.interfaces.value.copy();
                            int __key1168 = 0;
                            for (; (__key1168 < __r1167.getLength());__key1168 += 1) {
                                Ptr<BaseClass> b = __r1167.get(__key1168);
                                if ((b.get()).sym.value.isCOMinterface())
                                    cldec.com = true;
                                if ((cldec.classKind.value == ClassKind.cpp) && !(b.get()).sym.value.isCPPinterface())
                                {
                                    error(cldec.loc.value, new BytePtr("C++ class `%s` cannot implement D interface `%s`"), cldec.toPrettyChars(false), (b.get()).sym.value.toPrettyChars(false));
                                }
                            }
                        }
                        this.interfaceSemantic(cldec);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lancestorsdone:*/
                if (cldec.members.value == null)
                {
                    cldec.semanticRun.value = PASS.semanticdone;
                    return ;
                }
                if (cldec.symtab == null)
                {
                    cldec.symtab = new DsymbolTable();
                    Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s) {
                            s.addMember(sc, cldec);
                            return null;
                        }
                    };
                    foreachDsymbol(cldec.members.value, __lambda6);
                    Ptr<Scope> sc2 = cldec.newScope(this.sc);
                    Function1<Dsymbol,Void> __lambda7 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s) {
                            s.setScope(sc2);
                            return null;
                        }
                    };
                    foreachDsymbol(cldec.members.value, __lambda7);
                    (sc2.get()).pop();
                }
                {
                    int i = 0;
                    for (; (i < (cldec.baseclasses.get()).length.value);i++){
                        Ptr<BaseClass> b = (cldec.baseclasses.get()).get(i);
                        Type tb = (b.get()).type.value.toBasetype();
                        TypeClass tc = tb.isTypeClass();
                        if ((tc.sym.value.semanticRun.value < PASS.semanticdone))
                        {
                            cldec._scope.value = scx != null ? scx : (this.sc.get()).copy();
                            (cldec._scope.value.get()).setNoFree();
                            if (tc.sym.value._scope.value != null)
                                dmodule.Module.addDeferredSemantic(tc.sym.value);
                            dmodule.Module.addDeferredSemantic(cldec);
                            return ;
                        }
                    }
                }
                if ((cldec.baseok == Baseok.done))
                {
                    cldec.baseok = Baseok.semanticdone;
                    objc().setMetaclass(cldec, this.sc);
                    if (cldec.baseClass.value != null)
                    {
                        if ((cldec.classKind.value == ClassKind.cpp) && (cldec.baseClass.value.vtbl.value.length.value == 0))
                        {
                            cldec.error(new BytePtr("C++ base class `%s` needs at least one virtual function"), cldec.baseClass.value.toChars());
                        }
                        cldec.vtbl.value.setDim(cldec.baseClass.value.vtbl.value.length.value);
                        memcpy((BytePtr)(cldec.vtbl.value.tdata()), (cldec.baseClass.value.vtbl.value.tdata()), (4 * cldec.vtbl.value.length.value));
                        cldec.vthis.value = cldec.baseClass.value.vthis.value;
                        cldec.vthis2.value = cldec.baseClass.value.vthis2.value;
                    }
                    else
                    {
                        cldec.vtbl.value.setDim(0);
                        if (cldec.vtblOffset() != 0)
                            cldec.vtbl.value.push(cldec);
                    }
                    if (cldec.vthis.value != null)
                    {
                        if ((cldec.storage_class & 1L) != 0)
                            cldec.error(new BytePtr("static class cannot inherit from nested class `%s`"), cldec.baseClass.value.toChars());
                        if ((!pequals(cldec.toParentLocal(), cldec.baseClass.value.toParentLocal())) && (cldec.toParentLocal() == null) || (cldec.baseClass.value.toParentLocal().getType() == null) || !cldec.baseClass.value.toParentLocal().getType().isBaseOf(cldec.toParentLocal().getType(), null))
                        {
                            if (cldec.toParentLocal() != null)
                            {
                                cldec.error(new BytePtr("is nested within `%s`, but super class `%s` is nested within `%s`"), cldec.toParentLocal().toChars(), cldec.baseClass.value.toChars(), cldec.baseClass.value.toParentLocal().toChars());
                            }
                            else
                            {
                                cldec.error(new BytePtr("is not nested, but super class `%s` is nested within `%s`"), cldec.baseClass.value.toChars(), cldec.baseClass.value.toParentLocal().toChars());
                            }
                            cldec.enclosing = null;
                        }
                        if (cldec.vthis2.value != null)
                        {
                            if ((!pequals(cldec.toParent2(), cldec.baseClass.value.toParent2())) && (cldec.toParent2() == null) || (cldec.baseClass.value.toParent2().getType() == null) || !cldec.baseClass.value.toParent2().getType().isBaseOf(cldec.toParent2().getType(), null))
                            {
                                if ((cldec.toParent2() != null) && (!pequals(cldec.toParent2(), cldec.toParentLocal())))
                                {
                                    cldec.error(new BytePtr("needs the frame pointer of `%s`, but super class `%s` needs the frame pointer of `%s`"), cldec.toParent2().toChars(), cldec.baseClass.value.toChars(), cldec.baseClass.value.toParent2().toChars());
                                }
                                else
                                {
                                    cldec.error(new BytePtr("doesn't need a frame pointer, but super class `%s` needs the frame pointer of `%s`"), cldec.baseClass.value.toChars(), cldec.baseClass.value.toParent2().toChars());
                                }
                            }
                        }
                        else
                            cldec.makeNested2();
                    }
                    else
                        cldec.makeNested();
                }
                Ptr<Scope> sc2 = cldec.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda8 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.importAll(sc2);
                        return null;
                    }
                };
                foreachDsymbol(cldec.members.value, __lambda8);
                Function1<Dsymbol,Void> __lambda9 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        dsymbolSemantic(s, sc2);
                        return null;
                    }
                };
                foreachDsymbol(cldec.members.value, __lambda9);
                if (!cldec.determineFields())
                {
                    assert((pequals(cldec.type.value, Type.terror.value)));
                    (sc2.get()).pop();
                    return ;
                }
                {
                    Slice<VarDeclaration> __r1169 = cldec.fields.opSlice().copy();
                    int __key1170 = 0;
                    for (; (__key1170 < __r1169.getLength());__key1170 += 1) {
                        VarDeclaration v = __r1169.get(__key1170);
                        Type tb = v.type.value.baseElemOf();
                        if (((tb.ty.value & 0xFF) != ENUMTY.Tstruct))
                            continue;
                        StructDeclaration sd = ((TypeStruct)tb).sym.value;
                        if ((sd.semanticRun.value >= PASS.semanticdone))
                            continue;
                        (sc2.get()).pop();
                        cldec._scope.value = scx != null ? scx : (this.sc.get()).copy();
                        (cldec._scope.value.get()).setNoFree();
                        dmodule.Module.addDeferredSemantic(cldec);
                        return ;
                    }
                }
                cldec.aggNew = (NewDeclaration)cldec.search(Loc.initial.value, Id.classNew, 8);
                cldec.aggDelete.value = (DeleteDeclaration)cldec.search(Loc.initial.value, Id.classDelete, 8);
                cldec.ctor.value = cldec.searchCtor();
                if ((cldec.ctor.value == null) && cldec.noDefaultCtor.value)
                {
                    {
                        Slice<VarDeclaration> __r1171 = cldec.fields.opSlice().copy();
                        int __key1172 = 0;
                        for (; (__key1172 < __r1171.getLength());__key1172 += 1) {
                            VarDeclaration v = __r1171.get(__key1172);
                            if ((v.storage_class.value & 549755813888L) != 0)
                                error(v.loc.value, new BytePtr("field `%s` must be initialized in constructor"), v.toChars());
                        }
                    }
                }
                if ((cldec.ctor.value == null) && (cldec.baseClass.value != null) && (cldec.baseClass.value.ctor.value != null))
                {
                    FuncDeclaration fd = resolveFuncCall(cldec.loc.value, sc2, cldec.baseClass.value.ctor.value, null, cldec.type.value, null, FuncResolveFlag.quiet);
                    if (fd == null)
                        fd = resolveFuncCall(cldec.loc.value, sc2, cldec.baseClass.value.ctor.value, null, cldec.type.value.sharedOf(), null, FuncResolveFlag.quiet);
                    if ((fd != null) && !fd.errors.value)
                    {
                        TypeFunction btf = fd.type.value.toTypeFunction();
                        TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), null, LINK.d, fd.storage_class.value);
                        tf.mod.value = btf.mod.value;
                        tf.purity.value = btf.purity.value;
                        tf.isnothrow.value = btf.isnothrow.value;
                        tf.isnogc.value = btf.isnogc.value;
                        tf.trust.value = btf.trust.value;
                        CtorDeclaration ctor = new CtorDeclaration(cldec.loc.value, Loc.initial.value, 0L, tf, false);
                        ctor.fbody.value = new CompoundStatement(Loc.initial.value, refPtr(new DArray<Statement>()));
                        (cldec.members.value.get()).push(ctor);
                        ctor.addMember(this.sc, cldec);
                        dsymbolSemantic(ctor, sc2);
                        cldec.ctor.value = ctor;
                        cldec.defaultCtor = ctor;
                    }
                    else
                    {
                        cldec.error(new BytePtr("cannot implicitly generate a default constructor when base class `%s` is missing a default constructor"), cldec.baseClass.value.toPrettyChars(false));
                    }
                }
                cldec.dtor.value = buildDtor(cldec, sc2);
                cldec.tidtor = buildExternDDtor(cldec, sc2);
                if ((cldec.classKind.value == ClassKind.cpp) && (cldec.cppDtorVtblIndex != -1))
                {
                    cldec.dtor.value.vtblIndex.value = cldec.cppDtorVtblIndex;
                    cldec.vtbl.value.set(cldec.cppDtorVtblIndex, cldec.dtor.value);
                    if (target.twoDtorInVtable)
                    {
                        cldec.vtbl.value.set((cldec.cppDtorVtblIndex + 1), cldec.dtor.value);
                    }
                }
                {
                    FuncDeclaration f = hasIdentityOpAssign(cldec, sc2);
                    if ((f) != null)
                    {
                        if ((f.storage_class.value & 137438953472L) == 0)
                            cldec.error(f.loc.value, new BytePtr("identity assignment operator overload is illegal"));
                    }
                }
                cldec.inv = buildInv(cldec, sc2);
                if (cldec.inv != null)
                    this.reinforceInvariant(cldec, sc2);
                dmodule.Module.dprogress++;
                cldec.semanticRun.value = PASS.semanticdone;
                (sc2.get()).pop();
                if ((cldec.isabstract.value != Abstract.fwdref))
                {
                    int isabstractsave = cldec.isabstract.value;
                    cldec.isabstract.value = Abstract.fwdref;
                    cldec.isAbstract();
                    if ((cldec.isabstract.value != isabstractsave))
                    {
                        cldec.error(new BytePtr("cannot infer `abstract` attribute due to circular dependencies"));
                    }
                }
                if (((cldec.type.value.ty.value & 0xFF) == ENUMTY.Tclass) && (!pequals(((TypeClass)cldec.type.value).sym.value, cldec)))
                {
                    ClassDeclaration cd = ((TypeClass)cldec.type.value).sym.value;
                    cldec.error(new BytePtr("already exists at %s. Perhaps in another function with the same name?"), cd.loc.value.toChars(global.params.showColumns.value));
                }
                if ((global.errors.value != errors))
                {
                    cldec.type.value = Type.terror.value;
                    cldec.errors.value = true;
                    if (cldec.deferred != null)
                        cldec.deferred.errors.value = true;
                }
                if ((cldec.storage_class & 512L) != 0)
                {
                    {
                        Slice<VarDeclaration> __r1173 = cldec.fields.opSlice().copy();
                        int __key1174 = 0;
                        for (; (__key1174 < __r1173.getLength());__key1174 += 1) {
                            VarDeclaration vd = __r1173.get(__key1174);
                            if ((vd.isThisDeclaration() == null) && !vd.prot().isMoreRestrictiveThan(new Prot(Prot.Kind.public_)))
                            {
                                vd.error(new BytePtr("Field members of a `synchronized` class cannot be `%s`"), protectionToChars(vd.prot().kind.value));
                            }
                        }
                    }
                }
                if ((cldec.deferred != null) && (global.gag.value == 0))
                {
                    semantic2(cldec.deferred, this.sc);
                    semantic3(cldec.deferred, this.sc);
                }
                if ((cldec.storage_class & 524288L) != 0)
                    deprecation(cldec.loc.value, new BytePtr("`scope` as a type constraint is deprecated.  Use `scope` at the usage site."));
            }
            finally {
            }
        }

        public  void visit(InterfaceDeclaration idec) {
            Function1<InterfaceDeclaration,Boolean> isAnonymousMetaclass = new Function1<InterfaceDeclaration,Boolean>(){
                public Boolean invoke(InterfaceDeclaration idec) {
                    return (idec.classKind.value == ClassKind.objc) && idec.objc.isMeta.value && idec.isAnonymous();
                }
            };
            if ((idec.semanticRun.value >= PASS.semanticdone))
                return ;
            int errors = global.errors.value;
            Ptr<Scope> scx = null;
            if (idec._scope.value != null)
            {
                this.sc = idec._scope.value;
                scx = idec._scope.value;
                idec._scope.value = null;
            }
            if (idec.parent.value == null)
            {
                assert(((this.sc.get()).parent.value != null) && ((this.sc.get()).func.value != null));
                idec.parent.value = (this.sc.get()).parent.value;
            }
            assert((idec.parent.value != null) && !idec.isAnonymous() || isAnonymousMetaclass.invoke(idec));
            if (idec.errors.value)
                idec.type.value = Type.terror.value;
            idec.type.value = typeSemantic(idec.type.value, idec.loc.value, this.sc);
            if (((idec.type.value.ty.value & 0xFF) == ENUMTY.Tclass) && (!pequals(((TypeClass)idec.type.value).sym.value, idec)))
            {
                TemplateInstance ti = ((TypeClass)idec.type.value).sym.value.isInstantiated();
                if ((ti != null) && isError(ti))
                    ((TypeClass)idec.type.value).sym.value = idec;
            }
            Ungag ungag = idec.ungagSpeculative().copy();
            try {
                if ((idec.semanticRun.value == PASS.init))
                {
                    idec.protection = (this.sc.get()).protection.value.copy();
                    idec.storage_class |= (this.sc.get()).stc.value;
                    if ((idec.storage_class & 1024L) != 0)
                        idec.isdeprecated = true;
                    idec.userAttribDecl = (this.sc.get()).userAttribDecl;
                }
                else if (idec.symtab != null)
                {
                    if ((idec.sizeok.value == Sizeok.done) || (scx == null))
                    {
                        idec.semanticRun.value = PASS.semanticdone;
                        return ;
                    }
                }
                idec.semanticRun.value = PASS.semantic;
                try {
                    if ((idec.baseok < Baseok.done))
                    {
                        // from template resolveBase!(Type)
                        Function1<Type,Type> resolveBaseType = new Function1<Type,Type>(){
                            public Type invoke(Type exp) {
                                if (scx == null)
                                {
                                    scx = (sc.get()).copy();
                                    (scx.get()).setNoFree();
                                }
                                idec._scope.value = scx;
                                Type r = exp.invoke();
                                idec._scope.value = null;
                                return r;
                            }
                        };

                        // from template resolveBase!(Void)
                        Function1<Void,Void> resolveBaseVoid = new Function1<Void,Void>(){
                            public Void invoke(Void exp) {
                                if (scx == null)
                                {
                                    scx = (sc.get()).copy();
                                    (scx.get()).setNoFree();
                                }
                                idec._scope.value = scx;
                                exp.invoke();
                                idec._scope.value = null;
                            }
                        };

                        idec.baseok = Baseok.start;
                        {
                            int i = 0;
                            for (; (i < (idec.baseclasses.get()).length.value);){
                                Ptr<BaseClass> b = (idec.baseclasses.get()).get(i);
                                Function0<Type> __dgliteral3 = new Function0<Type>(){
                                    public Type invoke() {
                                        return typeSemantic((b.get()).type.value, idec.loc.value, sc);
                                    }
                                };
                                (b.get()).type.value = resolveBaseType.invoke(__dgliteral3);
                                Type tb = (b.get()).type.value.toBasetype();
                                {
                                    TypeTuple tup = tb.isTypeTuple();
                                    if ((tup) != null)
                                    {
                                        (idec.baseclasses.get()).remove(i);
                                        int dim = Parameter.dim(tup.arguments.value);
                                        {
                                            int j = 0;
                                            for (; (j < dim);j++){
                                                Parameter arg = Parameter.getNth(tup.arguments.value, j, null);
                                                b = refPtr(new BaseClass(arg.type.value));
                                                (idec.baseclasses.get()).insert(i + j, b);
                                            }
                                        }
                                    }
                                    else
                                        i++;
                                }
                            }
                        }
                        if ((idec.baseok >= Baseok.done))
                        {
                            if ((idec.semanticRun.value >= PASS.semanticdone))
                                return ;
                            /*goto Lancestorsdone*/throw Dispatch0.INSTANCE;
                        }
                        if (((idec.baseclasses.get()).length.value == 0) && ((this.sc.get()).linkage.value == LINK.cpp))
                            idec.classKind.value = ClassKind.cpp;
                        idec.namespace = (this.sc.get()).namespace;
                        if (((this.sc.get()).linkage.value == LINK.objc))
                        {
                            objc().setObjc(idec);
                            objc().deprecate(idec);
                        }
                        {
                            int i = 0;
                            for (; (i < (idec.baseclasses.get()).length.value);){
                                Ptr<BaseClass> b = (idec.baseclasses.get()).get(i);
                                Type tb = (b.get()).type.value.toBasetype();
                                TypeClass tc = ((tb.ty.value & 0xFF) == ENUMTY.Tclass) ? (TypeClass)tb : null;
                                if ((tc == null) || (tc.sym.value.isInterfaceDeclaration() == null))
                                {
                                    if ((!pequals((b.get()).type.value, Type.terror.value)))
                                        idec.error(new BytePtr("base type must be `interface`, not `%s`"), (b.get()).type.value.toChars());
                                    (idec.baseclasses.get()).remove(i);
                                    continue;
                                }
                                {
                                    int j = 0;
                                    for (; (j < i);j++){
                                        Ptr<BaseClass> b2 = (idec.baseclasses.get()).get(j);
                                        if ((pequals((b2.get()).sym.value, tc.sym.value)))
                                        {
                                            idec.error(new BytePtr("inherits from duplicate interface `%s`"), (b2.get()).sym.value.toChars());
                                            (idec.baseclasses.get()).remove(i);
                                            continue;
                                        }
                                    }
                                }
                                if ((pequals(tc.sym.value, idec)) || idec.isBaseOf2(tc.sym.value))
                                {
                                    idec.error(new BytePtr("circular inheritance of interface"));
                                    (idec.baseclasses.get()).remove(i);
                                    continue;
                                }
                                if (tc.sym.value.isDeprecated())
                                {
                                    if (!idec.isDeprecated())
                                    {
                                        idec.isdeprecated = true;
                                        tc.checkDeprecated(idec.loc.value, this.sc);
                                    }
                                }
                                (b.get()).sym.value = tc.sym.value;
                                if ((tc.sym.value.baseok < Baseok.done))
                                    Function0<Void> __dgliteral4 = new Function0<Void>(){
                                        public Void invoke() {
                                            dsymbolSemantic(tc.sym.value, null);
                                        }
                                    };
                                    resolveBaseVoid.invoke(__dgliteral4);
                                if ((tc.sym.value.baseok < Baseok.done))
                                {
                                    if (tc.sym.value._scope.value != null)
                                        dmodule.Module.addDeferredSemantic(tc.sym.value);
                                    idec.baseok = Baseok.none;
                                }
                                i++;
                            }
                        }
                        if ((idec.baseok == Baseok.none))
                        {
                            idec._scope.value = scx != null ? scx : (this.sc.get()).copy();
                            (idec._scope.value.get()).setNoFree();
                            dmodule.Module.addDeferredSemantic(idec);
                            return ;
                        }
                        idec.baseok = Baseok.done;
                        idec.interfaces.value = (idec.baseclasses.get()).tdata().slice(0,(idec.baseclasses.get()).length.value).copy();
                        {
                            Slice<Ptr<BaseClass>> __r1175 = idec.interfaces.value.copy();
                            int __key1176 = 0;
                            for (; (__key1176 < __r1175.getLength());__key1176 += 1) {
                                Ptr<BaseClass> b = __r1175.get(__key1176);
                                if ((b.get()).sym.value.isCOMinterface())
                                    idec.com = true;
                                if ((b.get()).sym.value.isCPPinterface())
                                    idec.classKind.value = ClassKind.cpp;
                            }
                        }
                        this.interfaceSemantic(idec);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lancestorsdone:*/
                if (idec.members.value == null)
                {
                    idec.semanticRun.value = PASS.semanticdone;
                    return ;
                }
                if (idec.symtab == null)
                    idec.symtab = new DsymbolTable();
                {
                    int i = 0;
                    for (; (i < (idec.baseclasses.get()).length.value);i++){
                        Ptr<BaseClass> b = (idec.baseclasses.get()).get(i);
                        Type tb = (b.get()).type.value.toBasetype();
                        TypeClass tc = tb.isTypeClass();
                        if ((tc.sym.value.semanticRun.value < PASS.semanticdone))
                        {
                            idec._scope.value = scx != null ? scx : (this.sc.get()).copy();
                            (idec._scope.value.get()).setNoFree();
                            if (tc.sym.value._scope.value != null)
                                dmodule.Module.addDeferredSemantic(tc.sym.value);
                            dmodule.Module.addDeferredSemantic(idec);
                            return ;
                        }
                    }
                }
                if ((idec.baseok == Baseok.done))
                {
                    idec.baseok = Baseok.semanticdone;
                    objc().setMetaclass(idec, this.sc);
                    if (idec.vtblOffset() != 0)
                        idec.vtbl.value.push(idec);
                    {
                        Slice<Ptr<BaseClass>> __r1178 = idec.interfaces.value.copy();
                        int __key1177 = 0;
                    L_outer8:
                        for (; (__key1177 < __r1178.getLength());__key1177 += 1) {
                            Ptr<BaseClass> b = __r1178.get(__key1177);
                            int i = __key1177;
                            try {
                                {
                                    int k = 0;
                                L_outer9:
                                    for (; (k < i);k++){
                                        if ((b == idec.interfaces.value.get(k)))
                                            /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                if ((b.get()).sym.value.vtblOffset() != 0)
                                {
                                    int d = (b.get()).sym.value.vtbl.value.length.value;
                                    if ((d > 1))
                                    {
                                        idec.vtbl.value.pushSlice((b.get()).sym.value.vtbl.value.opSlice(1, d));
                                    }
                                }
                                else
                                {
                                    idec.vtbl.value.append(ptr((b.get()).sym.value.vtbl));
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*Lcontinue:*/
                        }
                    }
                }
                Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.addMember(sc, idec);
                        return null;
                    }
                };
                foreachDsymbol(idec.members.value, __lambda5);
                Ptr<Scope> sc2 = idec.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.setScope(sc2);
                        return null;
                    }
                };
                foreachDsymbol(idec.members.value, __lambda6);
                Function1<Dsymbol,Void> __lambda7 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.importAll(sc2);
                        return null;
                    }
                };
                foreachDsymbol(idec.members.value, __lambda7);
                Function1<Dsymbol,Void> __lambda8 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        dsymbolSemantic(s, sc2);
                        return null;
                    }
                };
                foreachDsymbol(idec.members.value, __lambda8);
                dmodule.Module.dprogress++;
                idec.semanticRun.value = PASS.semanticdone;
                (sc2.get()).pop();
                if ((global.errors.value != errors))
                {
                    idec.type.value = Type.terror.value;
                }
                assert(((idec.type.value.ty.value & 0xFF) != ENUMTY.Tclass) || (pequals(((TypeClass)idec.type.value).sym.value, idec)));
                if ((idec.storage_class & 524288L) != 0)
                    deprecation(idec.loc.value, new BytePtr("`scope` as a type constraint is deprecated.  Use `scope` at the usage site."));
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
    public static void templateInstanceSemantic(TemplateInstance tempinst, Ptr<Scope> sc, Ptr<DArray<Expression>> fargs) {
        if (tempinst.inst.value != null)
        {
            return ;
        }
        if ((tempinst.semanticRun.value != PASS.init))
        {
            Ungag ungag = ungag = new Ungag(global.gag.value);
            try {
                if (!tempinst.gagged)
                    global.gag.value = 0;
                tempinst.error(tempinst.loc.value, new BytePtr("recursive template expansion"));
                if (tempinst.gagged)
                    tempinst.semanticRun.value = PASS.init;
                else
                    tempinst.inst.value = tempinst;
                tempinst.errors.value = true;
                return ;
            }
            finally {
            }
        }
        tempinst.tinst = (sc.get()).tinst;
        tempinst.minst.value = (sc.get()).minst.value;
        if ((tempinst.tinst == null) && ((sc.get()).func.value != null) && (sc.get()).func.value.inNonRoot())
        {
            tempinst.minst.value = null;
        }
        tempinst.gagged = global.gag.value > 0;
        tempinst.semanticRun.value = PASS.semantic;
        if (!tempinst.findTempDecl(sc, null) || !tempinst.semanticTiargs(sc) || !tempinst.findBestMatch(sc, fargs))
        {
        /*Lerror:*/
            if (tempinst.gagged)
            {
                tempinst.semanticRun.value = PASS.init;
            }
            else
                tempinst.inst.value = tempinst;
            tempinst.errors.value = true;
            return ;
        }
        TemplateDeclaration tempdecl = tempinst.tempdecl.value.isTemplateDeclaration();
        assert(tempdecl != null);
        if (tempdecl.ismixin)
        {
            tempinst.error(new BytePtr("mixin templates are not regular templates"));
            /*goto Lerror*/throw Dispatch0.INSTANCE;
        }
        tempinst.hasNestedArgs(tempinst.tiargs.value, tempdecl.isstatic);
        if (tempinst.errors.value)
            /*goto Lerror*/throw Dispatch0.INSTANCE;
        tempinst.namespace = tempdecl.namespace;
        tempinst.inst.value = tempdecl.findExistingInstance(tempinst, fargs);
        TemplateInstance errinst = null;
        if (tempinst.inst.value == null)
        {
        }
        else if (tempinst.inst.value.gagged && !tempinst.gagged && tempinst.inst.value.errors.value)
        {
            errinst = tempinst.inst.value;
        }
        else
        {
            tempinst.parent.value = tempinst.inst.value.parent.value;
            tempinst.errors.value = tempinst.inst.value.errors.value;
            global.errors.value += (tempinst.errors.value ? 1 : 0);
            global.gaggedErrors += (tempinst.errors.value ? 1 : 0);
            if (tempinst.inst.value.gagged)
            {
                tempinst.inst.value.gagged = tempinst.gagged;
            }
            tempinst.tnext = tempinst.inst.value.tnext;
            tempinst.inst.value.tnext = tempinst;
            if ((tempinst.minst.value != null) && tempinst.minst.value.isRoot() && !((tempinst.inst.value.minst.value != null) && tempinst.inst.value.minst.value.isRoot()))
            {
                dmodule.Module mi = tempinst.minst.value;
                TemplateInstance ti = tempinst.tinst;
                tempinst.minst.value = tempinst.inst.value.minst.value;
                tempinst.tinst = tempinst.inst.value.tinst;
                tempinst.inst.value.minst.value = mi;
                tempinst.inst.value.tinst = ti;
                if (tempinst.minst.value != null)
                {
                    tempinst.inst.value.appendToModuleMember();
                }
            }
            if (tempinst.minst.value != null)
                tempinst.minst.value.aimports.append(ptr(tempinst.inst.value.importedModules));
            return ;
        }
        int errorsave = global.errors.value;
        tempinst.inst.value = tempinst;
        tempinst.parent.value = tempinst.enclosing.value != null ? tempinst.enclosing.value : tempdecl.parent.value;
        TemplateInstance tempdecl_instance_idx = tempdecl.addInstance(tempinst);
        Ptr<DArray<Dsymbol>> target_symbol_list = tempinst.appendToModuleMember();
        int target_symbol_list_idx = target_symbol_list != null ? (target_symbol_list.get()).length.value - 1 : 0;
        tempinst.members.value = Dsymbol.arraySyntaxCopy(tempdecl.members.value);
        {
            int i = 0;
            for (; (i < (tempdecl.parameters.get()).length.value);i++){
                if (((tempdecl.parameters.get()).get(i).isTemplateThisParameter() == null))
                    continue;
                Type t = isType((tempinst.tiargs.value.get()).get(i));
                assert(t != null);
                {
                    long stc = ModToStc((t.mod.value & 0xFF));
                    if ((stc) != 0)
                    {
                        Ptr<DArray<Dsymbol>> s = refPtr(new DArray<Dsymbol>());
                        (s.get()).push(new StorageClassDeclaration(stc, tempinst.members.value));
                        tempinst.members.value = s;
                    }
                }
                break;
            }
        }
        Ptr<Scope> _scope = tempdecl._scope.value;
        if ((tempdecl.semanticRun.value == PASS.init))
        {
            tempinst.error(new BytePtr("template instantiation `%s` forward references template declaration `%s`"), tempinst.toChars(), tempdecl.toChars());
            return ;
        }
        tempinst.argsym = new ScopeDsymbol();
        tempinst.argsym.parent.value = (_scope.get()).parent.value;
        _scope = (_scope.get()).push(tempinst.argsym);
        (_scope.get()).tinst = tempinst;
        (_scope.get()).minst.value = tempinst.minst.value;
        Ptr<Scope> paramscope = (_scope.get()).push();
        (paramscope.get()).stc.value = 0L;
        (paramscope.get()).protection.value = new Prot(Prot.Kind.public_).copy();
        tempinst.declareParameters(paramscope);
        (paramscope.get()).pop();
        tempinst.symtab = new DsymbolTable();
        Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s) {
                s.addMember(_scope, tempinst);
            }
        };
        foreachDsymbol(tempinst.members.value, __lambda4);
        if ((tempinst.members.value.get()).length.value != 0)
        {
            Ref<Dsymbol> s = ref(null);
            if (Dsymbol.oneMembers(tempinst.members.value, ptr(s), tempdecl.ident.value) && (s.value != null))
            {
                tempinst.aliasdecl.value = s.value;
            }
        }
        if ((fargs != null) && (tempinst.aliasdecl.value != null))
        {
            {
                FuncDeclaration fd = tempinst.aliasdecl.value.isFuncDeclaration();
                if ((fd) != null)
                {
                    if (fd.type.value != null)
                        {
                            TypeFunction tf = fd.type.value.isTypeFunction();
                            if ((tf) != null)
                                tf.fargs.value = fargs;
                        }
                }
            }
        }
        Ptr<Scope> sc2 = null;
        sc2 = (_scope.get()).push(tempinst);
        (sc2.get()).parent.value = tempinst;
        (sc2.get()).tinst = tempinst;
        (sc2.get()).minst.value = tempinst.minst.value;
        tempinst.tryExpandMembers(sc2);
        tempinst.semanticRun.value = PASS.semanticdone;
        if ((tempinst.members.value.get()).length.value != 0)
        {
            Ref<Dsymbol> s = ref(null);
            if (Dsymbol.oneMembers(tempinst.members.value, ptr(s), tempdecl.ident.value) && (s.value != null))
            {
                if ((tempinst.aliasdecl.value == null) || (!pequals(tempinst.aliasdecl.value, s.value)))
                {
                    tempinst.aliasdecl.value = s.value;
                }
            }
        }
        try {
            if ((global.errors.value != errorsave))
                /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            {
                boolean found_deferred_ad = false;
                {
                    int i = 0;
                    for (; (i < dmodule.Module.deferred.length.value);i++){
                        Dsymbol sd = dmodule.Module.deferred.get(i);
                        AggregateDeclaration ad = sd.isAggregateDeclaration();
                        if ((ad != null) && (ad.parent.value != null) && (ad.parent.value.isTemplateInstance() != null))
                        {
                            found_deferred_ad = true;
                            if ((pequals(ad.parent.value, tempinst)))
                            {
                                ad.deferred = tempinst;
                                break;
                            }
                        }
                    }
                }
                if (found_deferred_ad || (dmodule.Module.deferred.length.value != 0))
                    /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            }
            {
                semantic2(tempinst, sc2);
            }
            if ((global.errors.value != errorsave))
                /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            if (((sc.get()).func.value != null) || (((sc.get()).flags.value & 65536) != 0) && (tempinst.tinst == null))
            {
                Ref<DArray<TemplateInstance>> deferred = ref(new DArray<TemplateInstance>());
                try {
                    tempinst.deferred = ptr(deferred);
                    tempinst.trySemantic3(sc2);
                    {
                        int i = 0;
                        for (; (i < deferred.value.length);i++){
                            semantic3(deferred.value.get(i), null);
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
                if (tempinst.aliasdecl.value != null)
                    fd = tempinst.aliasdecl.value.toAlias2().isFuncDeclaration();
                if (fd != null)
                {
                    FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                    if ((fld != null) && ((fld.tok.value & 0xFF) == 0))
                        doSemantic3 = true;
                    else if ((sc.get()).func.value != null)
                        doSemantic3 = true;
                }
                else if ((sc.get()).func.value != null)
                {
                    {
                        Slice<RootObject> __r1180 = tempinst.tdtypes.value.opSlice().copy();
                        int __key1181 = 0;
                        for (; (__key1181 < __r1180.getLength());__key1181 += 1) {
                            RootObject oarg = __r1180.get(__key1181);
                            Dsymbol s = getDsymbol(oarg);
                            if (s == null)
                                continue;
                            {
                                TemplateDeclaration td = s.isTemplateDeclaration();
                                if ((td) != null)
                                {
                                    if (!td.literal)
                                        continue;
                                    assert((td.members.value != null) && ((td.members.value.get()).length.value == 1));
                                    s = (td.members.value.get()).get(0);
                                }
                            }
                            {
                                FuncLiteralDeclaration fld = s.isFuncLiteralDeclaration();
                                if ((fld) != null)
                                {
                                    if (((fld.tok.value & 0xFF) == 0))
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
                for (; (ti != null) && (ti.deferred == null) && (ti.tinst != null);){
                    ti = ti.tinst;
                    if (((nest += 1) > 500))
                    {
                        global.gag.value = 0;
                        tempinst.error(new BytePtr("recursive expansion"));
                        fatal();
                    }
                }
                if ((ti != null) && (ti.deferred != null))
                {
                    {
                        int i = 0;
                        for (; ;i++){
                            if ((i == (ti.deferred.get()).length))
                            {
                                (ti.deferred.get()).push(tempinst);
                                break;
                            }
                            if ((pequals((ti.deferred.get()).get(i), tempinst)))
                                break;
                        }
                    }
                }
            }
            if (tempinst.aliasdecl.value != null)
            {
                tempinst.aliasdecl.value = tempinst.aliasdecl.value.toAlias2();
            }
        }
        catch(Dispatch0 __d){}
    /*Laftersemantic:*/
        (sc2.get()).pop();
        (_scope.get()).pop();
        if ((global.errors.value != errorsave))
        {
            if (!tempinst.errors.value)
            {
                if (!tempdecl.literal)
                    tempinst.error(tempinst.loc.value, new BytePtr("error instantiating"));
                if (tempinst.tinst != null)
                    tempinst.tinst.printInstantiationTrace();
            }
            tempinst.errors.value = true;
            if (tempinst.gagged)
            {
                tempdecl.removeInstance(tempdecl_instance_idx);
                if (target_symbol_list != null)
                {
                    assert((pequals((target_symbol_list.get()).get(target_symbol_list_idx), tempinst)));
                    (target_symbol_list.get()).remove(target_symbol_list_idx);
                    tempinst.memberOf = null;
                }
                tempinst.semanticRun.value = PASS.init;
                tempinst.inst.value = null;
                tempinst.symtab = null;
            }
        }
        else if (errinst != null)
        {
            assert(errinst.errors.value);
            TemplateInstanceBox ti1 = ti1 = new TemplateInstanceBox(errinst);
            tempdecl.instances.remove(ti1);
            TemplateInstanceBox ti2 = ti2 = new TemplateInstanceBox(tempinst);
            tempdecl.instances.set(ti2, __aaval1182);
        }
    }

    public static void aliasSemantic(AliasDeclaration ds, Ptr<Scope> sc) {
        if ((ds.type.value != null) && ((ds.type.value.ty.value & 0xFF) == ENUMTY.TTraits))
        {
            TypeTraits tt = (TypeTraits)ds.type.value;
            tt.inAliasDeclaration.value = true;
            {
                Type t = typeSemantic(tt, tt.loc, sc);
                if ((t) != null)
                    ds.type.value = t;
                else if (tt.sym.value != null)
                    ds.aliassym.value = tt.sym.value;
            }
            tt.inAliasDeclaration.value = false;
        }
        if (ds.aliassym.value != null)
        {
            FuncLiteralDeclaration fd = ds.aliassym.value.isFuncLiteralDeclaration();
            TemplateDeclaration td = ds.aliassym.value.isTemplateDeclaration();
            if ((fd != null) || (td != null) && td.literal)
            {
                if ((fd != null) && (fd.semanticRun.value >= PASS.semanticdone))
                    return ;
                Expression e = new FuncExp(ds.loc.value, ds.aliassym.value);
                e = expressionSemantic(e, sc);
                if (((e.op.value & 0xFF) == 161))
                {
                    FuncExp fe = (FuncExp)e;
                    ds.aliassym.value = fe.td.value != null ? fe.td.value : fe.fd.value;
                }
                else
                {
                    ds.aliassym.value = null;
                    ds.type.value = Type.terror.value;
                }
                return ;
            }
            if (ds.aliassym.value.isTemplateInstance() != null)
                dsymbolSemantic(ds.aliassym.value, sc);
            return ;
        }
        ds.inuse.value = 1;
        int errors = global.errors.value;
        Type oldtype = ds.type.value;
        Ungag ungag = ungag = new Ungag(global.gag.value);
        try {
            if ((ds.parent.value != null) && (global.gag.value != 0) && (ds.isInstantiated() == null) && (ds.toParent2().isFuncDeclaration() == null))
            {
                global.gag.value = 0;
            }
            if (((ds.type.value.ty.value & 0xFF) == ENUMTY.Tident) && (ds._import.value == null))
            {
                TypeIdentifier tident = (TypeIdentifier)ds.type.value;
                if ((tident.ident.value == ds.ident.value) && (tident.idents.length.value == 0))
                {
                    error(ds.loc.value, new BytePtr("`alias %s = %s;` cannot alias itself, use a qualified name to create an overload set"), ds.ident.value.toChars(), tident.ident.value.toChars());
                    ds.type.value = Type.terror.value;
                }
            }
            Ref<Dsymbol> s = ref(ds.type.value.toDsymbol(sc));
            if ((errors != global.errors.value))
            {
                s.value = null;
                ds.type.value = Type.terror.value;
            }
            if ((s.value != null) && (pequals(s.value, ds)))
            {
                ds.error(new BytePtr("cannot resolve"));
                s.value = null;
                ds.type.value = Type.terror.value;
            }
            if ((s.value == null) || (s.value.isEnumMember() == null))
            {
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ptr<Scope> sc2 = sc;
                if ((ds.storage_class.value & 4535588225024L) != 0)
                {
                    sc2 = (sc.get()).push();
                    (sc2.get()).stc.value |= ds.storage_class.value & 4536125095936L;
                }
                ds.type.value = ds.type.value.addSTC(ds.storage_class.value);
                resolve(ds.type.value, ds.loc.value, sc2, ptr(e), ptr(t), ptr(s), false);
                if ((sc2 != sc))
                    (sc2.get()).pop();
                if (e.value != null)
                {
                    s.value = getDsymbol(e.value);
                    if (s.value == null)
                    {
                        if (((e.value.op.value & 0xFF) != 127))
                            ds.error(new BytePtr("cannot alias an expression `%s`"), e.value.toChars());
                        t.value = Type.terror.value;
                    }
                }
                ds.type.value = t.value;
            }
            if ((pequals(s.value, ds)))
            {
                assert(global.errors.value != 0);
                ds.type.value = Type.terror.value;
                s.value = null;
            }
            if (s.value == null)
            {
                ds.type.value = typeSemantic(ds.type.value, ds.loc.value, sc);
                ds.aliassym.value = null;
            }
            else
            {
                ds.type.value = null;
                ds.aliassym.value = s.value;
            }
            if ((global.gag.value != 0) && (errors != global.errors.value))
            {
                ds.type.value = oldtype;
                ds.aliassym.value = null;
            }
            ds.inuse.value = 0;
            ds.semanticRun.value = PASS.semanticdone;
            {
                Dsymbol sx = ds.overnext;
                if ((sx) != null)
                {
                    ds.overnext = null;
                    if (!ds.overloadInsert(sx))
                        ScopeDsymbol.multiplyDefined(Loc.initial.value, sx, ds);
                }
            }
        }
        finally {
        }
    }

}
