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
        {
            return null;
        }
        Ref<Long> stc = ref(4406737108992L);
        Loc declLoc = sd.postblits.length != 0 ? sd.postblits.get(0).loc : sd.loc.copy();
        Loc loc = new Loc();
        {
            int i = 0;
            for (; (i < sd.postblits.length);i++){
                stc.value |= sd.postblits.get(i).storage_class & 137438953472L;
            }
        }
        Slice<VarDeclaration> fieldsToDestroy = new Slice<VarDeclaration>().copy();
        Ptr<DArray<Statement>> postblitCalls = refPtr(new DArray<Statement>());
        {
            int i = 0;
            for (; (i < sd.fields.length) && ((stc.value & 137438953472L) == 0);i++){
                VarDeclaration structField = sd.fields.get(i);
                if ((structField.storage_class & 2097152L) != 0)
                {
                    continue;
                }
                if (structField.overlapped)
                {
                    continue;
                }
                Type tv = structField.type.baseElemOf();
                if (((tv.ty & 0xFF) != ENUMTY.Tstruct))
                {
                    continue;
                }
                StructDeclaration sdv = ((TypeStruct)tv).sym;
                if (sdv.postblit == null)
                {
                    continue;
                }
                assert(sdv.isUnionDeclaration() == null);
                if ((fieldsToDestroy.getLength() > 0) && !((TypeFunction)sdv.postblit.type).isnothrow)
                {
                    Slice<Expression> dtorCalls = new Slice<Expression>().copy();
                    {
                        Slice<VarDeclaration> __r1152 = fieldsToDestroy.copy();
                        int __key1153 = 0;
                        for (; (__key1153 < __r1152.getLength());__key1153 += 1) {
                            VarDeclaration sf = __r1152.get(__key1153);
                            Expression ex = null;
                            tv = sf.type.toBasetype();
                            if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
                            {
                                ex = new ThisExp(loc);
                                ex = new DotVarExp(loc, ex, sf, true);
                                ex = new AddrExp(loc, ex);
                                ex = new CastExp(loc, ex, sf.type.mutableOf().pointerTo());
                                ex = new PtrExp(loc, ex);
                                if ((stc.value & 8589934592L) != 0)
                                {
                                    stc.value = stc.value & -8589934593L | 17179869184L;
                                }
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
                                {
                                    stc.value = stc.value & -8589934593L | 17179869184L;
                                }
                                SliceExp se = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t), new IntegerExp(loc, (long)length, Type.tsize_t));
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
                        Slice<Expression> __r1154 = dtorCalls.copy();
                        int __key1155 = __r1154.getLength();
                        for (; __key1155-- != 0;) {
                            Expression dc = __r1154.get(__key1155);
                            (dtors.get()).push(new ExpStatement(loc, dc));
                        }
                    }
                    (postblitCalls.get()).push(new ScopeGuardStatement(loc, TOK.onScopeFailure, new CompoundStatement(loc, dtors)));
                }
                sdv.postblit.functionSemantic();
                stc.value = mergeFuncAttrs(stc.value, sdv.postblit);
                stc.value = mergeFuncAttrs(stc.value, sdv.dtor);
                if ((stc.value & 137438953472L) != 0)
                {
                    (postblitCalls.get()).setDim(0);
                    break;
                }
                Expression ex = null;
                tv = structField.type.toBasetype();
                if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, structField, true);
                    ex = new AddrExp(loc, ex);
                    ex = new CastExp(loc, ex, structField.type.mutableOf().pointerTo());
                    ex = new PtrExp(loc, ex);
                    if ((stc.value & 8589934592L) != 0)
                    {
                        stc.value = stc.value & -8589934593L | 17179869184L;
                    }
                    ex = new DotVarExp(loc, ex, sdv.postblit, false);
                    ex = new CallExp(loc, ex);
                }
                else
                {
                    int length = tv.numberOfElems(loc);
                    if ((length == 0))
                    {
                        continue;
                    }
                    ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, structField, true);
                    ex = new DotIdExp(loc, ex, Id.ptr);
                    ex = new CastExp(loc, ex, sdv.type.pointerTo());
                    if ((stc.value & 8589934592L) != 0)
                    {
                        stc.value = stc.value & -8589934593L | 17179869184L;
                    }
                    SliceExp se = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t), new IntegerExp(loc, (long)length, Type.tsize_t));
                    se.upperIsInBounds = true;
                    se.lowerIsLessThanUpper = true;
                    ex = new CallExp(loc, new IdentifierExp(loc, Id.__ArrayPostblit), se);
                }
                (postblitCalls.get()).push(new ExpStatement(loc, ex));
                if (sdv.dtor != null)
                {
                    sdv.dtor.functionSemantic();
                    fieldsToDestroy.append(structField);
                }
            }
        }
        Function0<Void> checkShared = new Function0<Void>() {
            public Void invoke() {
             {
                if (sd.type.isShared())
                {
                    stc.value |= 536870912L;
                }
                return null;
            }}

        };
        if (((postblitCalls.get()).length != 0) || ((stc.value & 137438953472L) != 0))
        {
            checkShared.invoke();
            PostBlitDeclaration dd = new PostBlitDeclaration(declLoc, Loc.initial, stc.value, Id.__fieldPostblit);
            dd.generated = true;
            dd.storage_class |= 70368744177664L;
            dd.fbody = (stc.value & 137438953472L) != 0 ? null : new CompoundStatement(loc, postblitCalls);
            sd.postblits.shift(dd);
            (sd.members.get()).push(dd);
            dsymbolSemantic(dd, sc);
        }
        FuncDeclaration xpostblit = null;
        switch (sd.postblits.length)
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
                for (; (i < sd.postblits.length);i++){
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
            PostBlitDeclaration dd = new PostBlitDeclaration(declLoc, Loc.initial, stc.value, Id.__aggrPostblit);
            dd.generated = true;
            dd.storage_class |= 70368744177664L;
            dd.fbody = new ExpStatement(loc, e);
            (sd.members.get()).push(dd);
            dsymbolSemantic(dd, sc);
            xpostblit = dd;
            break;
        }
        if (xpostblit != null)
        {
            AliasDeclaration _alias = new AliasDeclaration(Loc.initial, Id.__xpostblit, xpostblit);
            dsymbolSemantic(_alias, sc);
            (sd.members.get()).push(_alias);
            _alias.addMember(sc, sd);
        }
        return xpostblit;
    }

    public static CtorDeclaration generateCopyCtorDeclaration(StructDeclaration sd, long paramStc, long funcStc) {
        Ptr<DArray<Parameter>> fparams = refPtr(new DArray<Parameter>());
        Type structType = sd.type;
        (fparams.get()).push(new Parameter(paramStc | 2097152L | 17592186044416L | 524288L, structType, Id.p, null, null));
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
            Slice<VarDeclaration> __r1156 = sd.fields.opSlice().copy();
            int __key1157 = 0;
            for (; (__key1157 < __r1156.getLength());__key1157 += 1) {
                VarDeclaration v = __r1156.get(__key1157);
                AssignExp ec = new AssignExp(loc, new DotVarExp(loc, new ThisExp(loc), v, true), new DotVarExp(loc, new IdentifierExp(loc, Id.p), v, true));
                e = Expression.combine(e, (Expression)ec);
            }
        }
        Statement s1 = new ExpStatement(loc, e);
        return new CompoundStatement(loc, slice(new Statement[]{s1}));
    }

    public static boolean buildCopyCtor(StructDeclaration sd, Ptr<Scope> sc) {
        if (global.errors != 0)
        {
            return false;
        }
        boolean hasPostblit = false;
        if (sd.postblit != null)
        {
            hasPostblit = true;
        }
        Dsymbol ctor = sd.search(sd.loc, Id.ctor, 8);
        CtorDeclaration cpCtor = null;
        CtorDeclaration rvalueCtor = null;
        if (ctor != null)
        {
            if (ctor.isOverloadSet() != null)
            {
                return false;
            }
            {
                TemplateDeclaration td = ctor.isTemplateDeclaration();
                if ((td) != null)
                {
                    ctor = td.funcroot;
                }
            }
        }
        try {
            if (ctor == null)
            {
                /*goto LcheckFields*/throw Dispatch0.INSTANCE;
            }
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>() {
                public Integer invoke(Dsymbol s) {
                 {
                    if (s.isTemplateDeclaration() != null)
                    {
                        return 0;
                    }
                    CtorDeclaration ctorDecl = s.isCtorDeclaration();
                    assert(ctorDecl != null);
                    if (ctorDecl.isCpCtor)
                    {
                        if (cpCtor == null)
                        {
                            cpCtor = ctorDecl;
                        }
                        return 0;
                    }
                    TypeFunction tf = ctorDecl.type.toTypeFunction();
                    int dim = Parameter.dim(tf.parameterList.parameters);
                    if ((dim == 1))
                    {
                        Parameter param = Parameter.getNth(tf.parameterList.parameters, 0, null);
                        if ((pequals(param.type.mutableOf().unSharedOf(), sd.type.mutableOf().unSharedOf())))
                        {
                            rvalueCtor = ctorDecl;
                        }
                    }
                    return 0;
                }}

            };
            overloadApply(ctor, __lambda3, null);
            if ((cpCtor != null) && (rvalueCtor != null))
            {
                error(sd.loc, new BytePtr("`struct %s` may not define both a rvalue constructor and a copy constructor"), sd.toChars());
                errorSupplemental(rvalueCtor.loc, new BytePtr("rvalue constructor defined here"));
                errorSupplemental(cpCtor.loc, new BytePtr("copy constructor defined here"));
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
            Slice<VarDeclaration> __r1158 = sd.fields.opSlice().copy();
            int __key1159 = 0;
            for (; (__key1159 < __r1158.getLength());__key1159 += 1) {
                VarDeclaration v = __r1158.get(__key1159);
                if ((v.storage_class & 2097152L) != 0)
                {
                    continue;
                }
                if (v.overlapped)
                {
                    continue;
                }
                TypeStruct ts = v.type.baseElemOf().isTypeStruct();
                if (ts == null)
                {
                    continue;
                }
                if (ts.sym.hasCopyCtor)
                {
                    fieldWithCpCtor = v;
                    break;
                }
            }
        }
        if ((fieldWithCpCtor != null) && (rvalueCtor != null))
        {
            error(sd.loc, new BytePtr("`struct %s` may not define a rvalue constructor and have fields with copy constructors"), sd.toChars());
            errorSupplemental(rvalueCtor.loc, new BytePtr("rvalue constructor defined here"));
            errorSupplemental(fieldWithCpCtor.loc, new BytePtr("field with copy constructor defined here"));
            return false;
        }
        else if (fieldWithCpCtor == null)
        {
            return false;
        }
        if (hasPostblit)
        {
            return false;
        }
        byte paramMod = (byte)8;
        byte funcMod = (byte)8;
        CtorDeclaration ccd = generateCopyCtorDeclaration(sd, ModToStc(8), ModToStc(8));
        Statement copyCtorBody = generateCopyCtorBody(sd);
        ccd.fbody = copyCtorBody;
        (sd.members.get()).push(ccd);
        ccd.addMember(sc, sd);
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc = 0L;
        (sc2.get()).linkage = LINK.d;
        dsymbolSemantic(ccd, sc2);
        semantic2(ccd, sc2);
        semantic3(ccd, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            ccd.storage_class |= 137438953472L;
            ccd.fbody = null;
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
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        nestedCount += setMangleOverride(s, sym);
                        return null;
                    }}

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
        {
            return ad.salign;
        }
        if (ad.ealign == null)
        {
            return ad.salign = -1;
        }
        sc = pcopy((sc.get()).startCTFE());
        ad.ealign = expressionSemantic(ad.ealign, sc);
        ad.ealign = resolveProperties(sc, ad.ealign);
        sc = pcopy((sc.get()).endCTFE());
        ad.ealign = ad.ealign.ctfeInterpret();
        if (((ad.ealign.op & 0xFF) == 127))
        {
            return ad.salign = -1;
        }
        Type tb = ad.ealign.type.value.toBasetype();
        long n = ad.ealign.toInteger();
        if ((n < 1L) || ((n & n - 1L) != 0) || (4294967295L < n) || !tb.isintegral())
        {
            error(ad.loc, new BytePtr("alignment must be an integer positive power of 2, not %s"), ad.ealign.toChars());
            return ad.salign = -1;
        }
        return ad.salign = (int)n;
    }

    public static BytePtr getMessage(DeprecatedDeclaration dd) {
        {
            Ptr<Scope> sc = dd._scope;
            if ((sc) != null)
            {
                dd._scope = null;
                sc = pcopy((sc.get()).startCTFE());
                dd.msg = expressionSemantic(dd.msg, sc);
                dd.msg = resolveProperties(sc, dd.msg);
                sc = pcopy((sc.get()).endCTFE());
                dd.msg = dd.msg.ctfeInterpret();
                {
                    StringExp se = dd.msg.toStringExp();
                    if ((se) != null)
                    {
                        dd.msgstr = pcopy((toBytePtr(se.toStringz())));
                    }
                    else
                    {
                        dd.msg.error(new BytePtr("compile time constant expected, not `%s`"), dd.msg.toChars());
                    }
                }
            }
        }
        return dd.msgstr;
    }

    public static boolean allowsContractWithoutBody(FuncDeclaration funcdecl) {
        assert(funcdecl.fbody == null);
        Dsymbol parent = funcdecl.toParent();
        InterfaceDeclaration id = parent.isInterfaceDeclaration();
        if (!funcdecl.isAbstract() && (funcdecl.fensures != null) || (funcdecl.frequires != null) && !((id != null) && funcdecl.isVirtual()))
        {
            ClassDeclaration cd = parent.isClassDeclaration();
            if (!((cd != null) && cd.isAbstract()))
            {
                return false;
            }
        }
        return true;
    }

    public static class DsymbolSemanticVisitor extends Visitor
    {
        public Ptr<Scope> sc = null;
        public  DsymbolSemanticVisitor(Ptr<Scope> sc) {
            this.sc = pcopy(sc);
        }

        public  void visit(Dsymbol dsym) {
            dsym.error(new BytePtr("%p has no semantic routine"), dsym);
        }

        public  void visit(ScopeDsymbol _param_0) {
        }

        public  void visit(Declaration _param_0) {
        }

        public  void visit(AliasThis dsym) {
            if ((dsym.semanticRun != PASS.init))
            {
                return ;
            }
            if (dsym._scope != null)
            {
                this.sc = pcopy(dsym._scope);
                dsym._scope = null;
            }
            if (this.sc == null)
            {
                return ;
            }
            dsym.semanticRun = PASS.semantic;
            Dsymbol p = (this.sc.get()).parent.value.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(dsym.loc, new BytePtr("alias this can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                return ;
            }
            assert(ad.members != null);
            Dsymbol s = ad.search(dsym.loc, dsym.ident, 8);
            if (s == null)
            {
                s = (this.sc.get()).search(dsym.loc, dsym.ident, null, 0);
                if (s != null)
                {
                    error(dsym.loc, new BytePtr("`%s` is not a member of `%s`"), s.toChars(), ad.toChars());
                }
                else
                {
                    error(dsym.loc, new BytePtr("undefined identifier `%s`"), dsym.ident.toChars());
                }
                return ;
            }
            if ((ad.aliasthis != null) && (!pequals(s, ad.aliasthis)))
            {
                error(dsym.loc, new BytePtr("there can be only one alias this"));
                return ;
            }
            ad.aliasthis = null;
            Dsymbol sx = s;
            if (sx.isAliasDeclaration() != null)
            {
                sx = sx.toAlias();
            }
            Declaration d = sx.isDeclaration();
            if ((d != null) && (d.isTupleDeclaration() == null))
            {
                if (d.type == null)
                {
                    dsymbolSemantic(d, this.sc);
                }
                Type t = d.type;
                assert(t != null);
                if ((ad.type.implicitConvTo(t) > MATCH.nomatch))
                {
                    error(dsym.loc, new BytePtr("alias this is not reachable as `%s` already converts to `%s`"), ad.toChars(), t.toChars());
                }
            }
            ad.aliasthis = s;
            dsym.semanticRun = PASS.semanticdone;
        }

        public  void visit(AliasDeclaration dsym) {
            if ((dsym.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            assert((dsym.semanticRun <= PASS.semantic));
            dsym.storage_class |= (this.sc.get()).stc & 1024L;
            dsym.protection.opAssign((this.sc.get()).protection.copy());
            dsym.userAttribDecl = (this.sc.get()).userAttribDecl;
            if (((this.sc.get()).func == null) && dsym.inNonRoot())
            {
                return ;
            }
            aliasSemantic(dsym, this.sc);
        }

        public  void visit(VarDeclaration dsym) {
            if ((dsym.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if ((this.sc != null) && ((this.sc.get()).inunion != null) && ((this.sc.get()).inunion.isAnonDeclaration() != null))
            {
                dsym.overlapped = true;
            }
            Ptr<Scope> scx = null;
            if (dsym._scope != null)
            {
                this.sc = pcopy(dsym._scope);
                scx = pcopy(this.sc);
                dsym._scope = null;
            }
            if (this.sc == null)
            {
                return ;
            }
            dsym.semanticRun = PASS.semantic;
            dsym.storage_class |= (this.sc.get()).stc & -665L;
            if (((dsym.storage_class & 2L) != 0) && (dsym._init != null))
            {
                dsym.error(new BytePtr("extern symbols cannot have initializers"));
            }
            dsym.userAttribDecl = (this.sc.get()).userAttribDecl;
            dsym.namespace = (this.sc.get()).namespace;
            AggregateDeclaration ad = dsym.isThis();
            if (ad != null)
            {
                dsym.storage_class |= ad.storage_class & 2685403140L;
            }
            int inferred = 0;
            if (dsym.type == null)
            {
                dsym.inuse++;
                boolean needctfe = (dsym.storage_class & 8388609L) != 0L;
                if (needctfe)
                {
                    this.sc = pcopy((this.sc.get()).startCTFE());
                }
                dsym._init = inferType(dsym._init, this.sc);
                dsym.type = initializerToExpression(dsym._init, null).type.value;
                if (needctfe)
                {
                    this.sc = pcopy((this.sc.get()).endCTFE());
                }
                dsym.inuse--;
                inferred = 1;
                dsym.storage_class &= -257L;
                dsym.originalType = dsym.type.syntaxCopy();
            }
            else
            {
                if (dsym.originalType == null)
                {
                    dsym.originalType = dsym.type.syntaxCopy();
                }
                Ptr<Scope> sc2 = (this.sc.get()).push();
                (sc2.get()).stc |= dsym.storage_class & 4462573780992L;
                dsym.inuse++;
                dsym.type = typeSemantic(dsym.type, dsym.loc, sc2);
                dsym.inuse--;
                (sc2.get()).pop();
            }
            if (((dsym.type.ty & 0xFF) == ENUMTY.Terror))
            {
                dsym.errors = true;
            }
            dsym.type.checkDeprecated(dsym.loc, this.sc);
            dsym.linkage = (this.sc.get()).linkage;
            dsym.parent.value = (this.sc.get()).parent.value;
            dsym.protection.opAssign((this.sc.get()).protection.copy());
            dsym.alignment = (this.sc.get()).alignment();
            if ((dsym.alignment == -1))
            {
                dsym.alignment = dsym.type.alignment();
            }
            if (global.params.vcomplex)
            {
                dsym.type.checkComplexTransition(dsym.loc, this.sc);
            }
            if (((this.sc.get()).func != null) && ((this.sc.get()).intypeof == 0))
            {
                if (((dsym.storage_class & 1073741824L) != 0) && (dsym.isMember() == null))
                {
                    if ((this.sc.get()).func.setUnsafe())
                    {
                        dsym.error(new BytePtr("__gshared not allowed in safe functions; use shared"));
                    }
                }
            }
            Dsymbol parent = dsym.toParent();
            Type tb = dsym.type.toBasetype();
            Type tbn = tb.baseElemOf();
            if (((tb.ty & 0xFF) == ENUMTY.Tvoid) && ((dsym.storage_class & 8192L) == 0))
            {
                if (inferred != 0)
                {
                    dsym.error(new BytePtr("type `%s` is inferred from initializer `%s`, and variables cannot be of type `void`"), dsym.type.toChars(), dsym._init.toChars());
                }
                else
                {
                    dsym.error(new BytePtr("variables cannot be of type `void`"));
                }
                dsym.type = Type.terror;
                tb = dsym.type;
            }
            if (((tb.ty & 0xFF) == ENUMTY.Tfunction))
            {
                dsym.error(new BytePtr("cannot be declared to be a function"));
                dsym.type = Type.terror;
                tb = dsym.type;
            }
            {
                TypeStruct ts = tb.isTypeStruct();
                if ((ts) != null)
                {
                    if (ts.sym.members == null)
                    {
                        dsym.error(new BytePtr("no definition of struct `%s`"), ts.toChars());
                    }
                }
            }
            if (((dsym.storage_class & 256L) != 0) && (inferred == 0))
            {
                dsym.error(new BytePtr("storage class `auto` has no effect if type is not inferred, did you mean `scope`?"));
            }
            {
                TypeTuple tt = tb.isTypeTuple();
                if ((tt) != null)
                {
                    int nelems = Parameter.dim(tt.arguments);
                    Expression ie = (dsym._init != null) && (dsym._init.isVoidInitializer() == null) ? initializerToExpression(dsym._init, null) : null;
                    if (ie != null)
                    {
                        ie = expressionSemantic(ie, this.sc);
                    }
                    try {
                        if ((nelems > 0) && (ie != null))
                        {
                            Ptr<DArray<Expression>> iexps = refPtr(new DArray<Expression>());
                            (iexps.get()).push(ie);
                            Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                            {
                                int pos = 0;
                            L_outer1:
                                for (; (pos < (iexps.get()).length);pos++){
                                    while(true) try {
                                    /*Lexpand1:*/
                                        Expression e = (iexps.get()).get(pos);
                                        Parameter arg = Parameter.getNth(tt.arguments, pos, null);
                                        arg.type = typeSemantic(arg.type, dsym.loc, this.sc);
                                        if ((!pequals(e, ie)))
                                        {
                                            if (((iexps.get()).length > nelems))
                                            {
                                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                            }
                                            if (e.type.value.implicitConvTo(arg.type) != 0)
                                            {
                                                continue L_outer1;
                                            }
                                        }
                                        if (((e.op & 0xFF) == 126))
                                        {
                                            TupleExp te = (TupleExp)e;
                                            if (((iexps.get()).length - 1 + (te.exps.get()).length > nelems))
                                            {
                                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                            }
                                            (iexps.get()).remove(pos);
                                            (iexps.get()).insert(pos, te.exps);
                                            iexps.get().set(pos, Expression.combine(te.e0.value, (iexps.get()).get(pos)));
                                            /*goto Lexpand1*/throw Dispatch0.INSTANCE;
                                        }
                                        else if (isAliasThisTuple(e) != null)
                                        {
                                            VarDeclaration v = copyToTemp(0L, new BytePtr("__tup"), e);
                                            dsymbolSemantic(v, this.sc);
                                            VarExp ve = new VarExp(dsym.loc, v, true);
                                            ve.type.value = e.type.value;
                                            (exps.get()).setDim(1);
                                            exps.get().set(0, ve);
                                            expandAliasThisTuples(exps, 0);
                                            {
                                                int u = 0;
                                            L_outer2:
                                                for (; (u < (exps.get()).length);u++){
                                                    while(true) try {
                                                    /*Lexpand2:*/
                                                        Expression ee = (exps.get()).get(u);
                                                        arg = Parameter.getNth(tt.arguments, pos + u, null);
                                                        arg.type = typeSemantic(arg.type, dsym.loc, this.sc);
                                                        int iexps_dim = (iexps.get()).length - 1 + (exps.get()).length;
                                                        if ((iexps_dim > nelems))
                                                        {
                                                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                                        }
                                                        if (ee.type.value.implicitConvTo(arg.type) != 0)
                                                        {
                                                            continue L_outer2;
                                                        }
                                                        if ((expandAliasThisTuples(exps, u) != -1))
                                                        {
                                                            /*goto Lexpand2*/throw Dispatch0.INSTANCE;
                                                        }
                                                        break;
                                                    } catch(Dispatch0 __d){}
                                                }
                                            }
                                            if ((!pequals((exps.get()).get(0), ve)))
                                            {
                                                Expression e0 = (exps.get()).get(0);
                                                exps.get().set(0, new CommaExp(dsym.loc, new DeclarationExp(dsym.loc, v), e0, true));
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
                            if (((iexps.get()).length < nelems))
                            {
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            ie = new TupleExp(dsym._init.loc, iexps);
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lnomatch:*/
                    if ((ie != null) && ((ie.op & 0xFF) == 126))
                    {
                        TupleExp te = (TupleExp)ie;
                        int tedim = (te.exps.get()).length;
                        if ((tedim != nelems))
                        {
                            error(dsym.loc, new BytePtr("tuple of %d elements cannot be assigned to tuple of %d elements"), tedim, nelems);
                            {
                                int u = tedim;
                                for (; (u < nelems);u++) {
                                    (te.exps.get()).push(new ErrorExp());
                                }
                            }
                        }
                    }
                    Ptr<DArray<RootObject>> exps = refPtr(new DArray<RootObject>(nelems));
                    {
                        int i = 0;
                        for (; (i < nelems);i++){
                            Parameter arg = Parameter.getNth(tt.arguments, i, null);
                            OutBuffer buf = new OutBuffer();
                            try {
                                buf.printf(new BytePtr("__%s_field_%llu"), dsym.ident.toChars(), (long)i);
                                Identifier id = Identifier.idPool(buf.peekSlice());
                                Initializer ti = null;
                                if (ie != null)
                                {
                                    Expression einit = ie;
                                    if (((ie.op & 0xFF) == 126))
                                    {
                                        TupleExp te = (TupleExp)ie;
                                        einit = (te.exps.get()).get(i);
                                        if ((i == 0))
                                        {
                                            einit = Expression.combine(te.e0.value, einit);
                                        }
                                    }
                                    ti = new ExpInitializer(einit.loc, einit);
                                }
                                else
                                {
                                    ti = dsym._init != null ? syntaxCopy(dsym._init) : null;
                                }
                                long storage_class = 1099511627776L | dsym.storage_class;
                                if ((arg.storageClass & 32L) != 0)
                                {
                                    storage_class |= arg.storageClass;
                                }
                                VarDeclaration v = new VarDeclaration(dsym.loc, arg.type, id, ti, storage_class);
                                dsymbolSemantic(v, this.sc);
                                if ((this.sc.get()).scopesym != null)
                                {
                                    if ((this.sc.get()).scopesym.members != null)
                                    {
                                        ((this.sc.get()).scopesym.members.get()).push(v);
                                    }
                                }
                                Expression e = new DsymbolExp(dsym.loc, v, true);
                                exps.get().set(i, e);
                            }
                            finally {
                            }
                        }
                    }
                    TupleDeclaration v2 = new TupleDeclaration(dsym.loc, dsym.ident, exps);
                    v2.parent.value = dsym.parent.value;
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
                {
                    dsym.storage_class |= 536870912L;
                }
            }
            else if (dsym.type.isImmutable())
            {
                dsym.storage_class |= 1048576L;
            }
            else if (dsym.type.isShared())
            {
                dsym.storage_class |= 536870912L;
            }
            else if (dsym.type.isWild())
            {
                dsym.storage_class |= 2147483648L;
            }
            {
                long stc = dsym.storage_class & 664L;
                if ((stc) != 0)
                {
                    if ((stc == 8L))
                    {
                        dsym.error(new BytePtr("cannot be `final`, perhaps you meant `const`?"));
                    }
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
                    dsym.storage_class &= ~stc;
                }
            }
            if ((dsym.storage_class & 524288L) != 0)
            {
                long stc = dsym.storage_class & 1216348163L;
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
                else if (!dsym.type.hasPointers())
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
                    if (global.params.vfield && ((dsym.storage_class & 1048580L) != 0) && (dsym._init != null) && (dsym._init.isVoidInitializer() == null))
                    {
                        BytePtr s = pcopy((dsym.storage_class & 1048576L) != 0 ? new BytePtr("immutable") : new BytePtr("const"));
                        message(dsym.loc, new BytePtr("`%s.%s` is `%s` field"), ad.toPrettyChars(false), dsym.toChars(), s);
                    }
                    dsym.storage_class |= 64L;
                    {
                        TypeStruct ts = tbn.isTypeStruct();
                        if ((ts) != null)
                        {
                            if (ts.sym.noDefaultCtor)
                            {
                                if ((dsym.isThisDeclaration() == null) && (dsym._init == null))
                                {
                                    aad.noDefaultCtor = true;
                                }
                            }
                        }
                    }
                }
                InterfaceDeclaration id = parent.isInterfaceDeclaration();
                if (id != null)
                {
                    dsym.error(new BytePtr("field not allowed in interface"));
                }
                else if ((aad != null) && (aad.sizeok == Sizeok.done))
                {
                    dsym.error(new BytePtr("cannot be further field because it will change the determined %s size"), aad.toChars());
                }
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                {
                    for (; 1 != 0;){
                        TemplateInstance ti2 = ti.tempdecl.parent.value.isTemplateInstance();
                        if (ti2 == null)
                        {
                            break;
                        }
                        ti = ti2;
                    }
                    AggregateDeclaration ad2 = ti.tempdecl.isMember();
                    if ((ad2 != null) && (dsym.storage_class != 0L))
                    {
                        dsym.error(new BytePtr("cannot use template to add field to aggregate `%s`"), ad2.toChars());
                    }
                }
            }
            if (((dsym.storage_class & 1374391648288L) == 2097152L) && (!pequals(dsym.ident, Id.This)))
            {
                dsym.error(new BytePtr("only parameters or `foreach` declarations can be `ref`"));
            }
            if (dsym.type.hasWild() != 0)
            {
                if (((dsym.storage_class & 1216348227L) != 0) || dsym.isDataseg())
                {
                    dsym.error(new BytePtr("only parameters or stack based variables can be `inout`"));
                }
                FuncDeclaration func = (this.sc.get()).func;
                if (func != null)
                {
                    if (func.fes != null)
                    {
                        func = func.fes.func;
                    }
                    boolean isWild = false;
                    {
                        FuncDeclaration fd = func;
                        for (; fd != null;fd = fd.toParentDecl().isFuncDeclaration()){
                            if (((TypeFunction)fd.type).iswild != 0)
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
            if (((dsym.storage_class & 343599480832L) == 0) && ((tbn.ty & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tbn).sym.noDefaultCtor)
            {
                if (dsym._init == null)
                {
                    if (dsym.isField())
                    {
                        dsym.storage_class |= 549755813888L;
                    }
                    else if ((dsym.storage_class & 32L) != 0)
                    {
                    }
                    else
                    {
                        dsym.error(new BytePtr("default construction is disabled for type `%s`"), dsym.type.toChars());
                    }
                }
            }
            FuncDeclaration fd = parent.isFuncDeclaration();
            if (dsym.type.isscope() && ((dsym.storage_class & 16777216L) == 0))
            {
                if (((dsym.storage_class & 1218449473L) != 0) || (fd == null))
                {
                    dsym.error(new BytePtr("globals, statics, fields, manifest constants, ref and out parameters cannot be `scope`"));
                }
                if ((dsym.storage_class & 524288L) == 0)
                {
                    if (((dsym.storage_class & 32L) == 0) && (!pequals(dsym.ident, Id.withSym)))
                    {
                        dsym.error(new BytePtr("reference to `scope class` must be `scope`"));
                    }
                }
            }
            if (((this.sc.get()).func != null) && ((this.sc.get()).intypeof == 0))
            {
                if ((dsym._init != null) && (dsym._init.isVoidInitializer() != null) && dsym.type.hasPointers())
                {
                    if ((this.sc.get()).func.setUnsafe())
                    {
                        dsym.error(new BytePtr("`void` initializers for pointers not allowed in safe functions"));
                    }
                }
                else if ((dsym._init == null) && ((dsym.storage_class & 1216348259L) == 0) && dsym.type.hasVoidInitPointers())
                {
                    if ((this.sc.get()).func.setUnsafe())
                    {
                        dsym.error(new BytePtr("`void` initializers for pointers not allowed in safe functions"));
                    }
                }
            }
            if ((dsym._init == null) || (dsym._init.isVoidInitializer() != null) && (fd == null))
            {
                dsym.storage_class |= 131072L;
            }
            if (dsym._init != null)
            {
                dsym.storage_class |= 4194304L;
            }
            else if ((dsym.storage_class & 8388608L) != 0)
            {
                dsym.error(new BytePtr("manifest constants must have initializers"));
            }
            boolean isBlit = false;
            long sz = 0L;
            try {
                if ((dsym._init == null) && ((dsym.storage_class & 1073741827L) == 0) && (fd != null) && ((dsym.storage_class & 274877925472L) == 0) || ((dsym.storage_class & 4096L) != 0) && ((sz = dsym.type.size()) != 0L))
                {
                    if ((sz == -1L) && ((dsym.type.ty & 0xFF) != ENUMTY.Terror))
                    {
                        dsym.error(new BytePtr("size of type `%s` is invalid"), dsym.type.toChars());
                    }
                    Type tv = dsym.type;
                    for (; ((tv.ty & 0xFF) == ENUMTY.Tsarray);) {
                        tv = tv.nextOf();
                    }
                    if (tv.needsNested())
                    {
                        assert(((tbn.ty & 0xFF) == ENUMTY.Tstruct));
                        checkFrameAccess(dsym.loc, this.sc, tbn.isTypeStruct().sym, 0);
                        Expression e = tv.defaultInitLiteral(dsym.loc);
                        e = new BlitExp(dsym.loc, new VarExp(dsym.loc, dsym, true), e);
                        e = expressionSemantic(e, this.sc);
                        dsym._init = new ExpInitializer(dsym.loc, e);
                        /*goto Ldtor*/throw Dispatch0.INSTANCE;
                    }
                    if (((tv.ty & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tv).sym.zeroInit)
                    {
                        Expression e = new IntegerExp(dsym.loc, 0L, Type.tint32);
                        e = new BlitExp(dsym.loc, new VarExp(dsym.loc, dsym, true), e);
                        e.type.value = dsym.type;
                        dsym._init = new ExpInitializer(dsym.loc, e);
                        /*goto Ldtor*/throw Dispatch0.INSTANCE;
                    }
                    if (((dsym.type.baseElemOf().ty & 0xFF) == ENUMTY.Tvoid))
                    {
                        dsym.error(new BytePtr("`%s` does not have a default initializer"), dsym.type.toChars());
                    }
                    else {
                        Expression e = defaultInit(dsym.type, dsym.loc);
                        if ((e) != null)
                        {
                            dsym._init = new ExpInitializer(dsym.loc, e);
                        }
                    }
                    isBlit = true;
                }
                if (dsym._init != null)
                {
                    this.sc = pcopy((this.sc.get()).push());
                    (this.sc.get()).stc &= -4538273628165L;
                    ExpInitializer ei = dsym._init.isExpInitializer();
                    if (ei != null)
                    {
                        ei.exp = inferType(ei.exp, dsym.type, 0);
                    }
                    if (((this.sc.get()).func != null) || ((this.sc.get()).intypeof == 1))
                    {
                        if ((fd != null) && ((dsym.storage_class & 1216348163L) == 0) && (dsym._init.isVoidInitializer() == null))
                        {
                            if (ei == null)
                            {
                                ArrayInitializer ai = dsym._init.isArrayInitializer();
                                Expression e = null;
                                if ((ai != null) && ((tb.ty & 0xFF) == ENUMTY.Taarray))
                                {
                                    e = toAssocArrayLiteral(ai);
                                }
                                else
                                {
                                    e = initializerToExpression(dsym._init, null);
                                }
                                if (e == null)
                                {
                                    dsym._init = initializerSemantic(dsym._init, this.sc, dsym.type, NeedInterpret.INITnointerpret);
                                    e = initializerToExpression(dsym._init, null);
                                    if (e == null)
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
                            {
                                exp = new BlitExp(dsym.loc, e1, exp);
                            }
                            else
                            {
                                exp = new ConstructExp(dsym.loc, e1, exp);
                            }
                            dsym.canassign++;
                            exp = expressionSemantic(exp, this.sc);
                            dsym.canassign--;
                            exp = exp.optimize(0, false);
                            if (((exp.op & 0xFF) == 127))
                            {
                                dsym._init = new ErrorInitializer();
                                ei = null;
                            }
                            else
                            {
                                ei.exp = exp;
                            }
                            if ((ei != null) && dsym.isScope())
                            {
                                Expression ex = ei.exp;
                                for (; ((ex.op & 0xFF) == 99);) {
                                    ex = ((CommaExp)ex).e2.value;
                                }
                                if (((ex.op & 0xFF) == 96) || ((ex.op & 0xFF) == 95))
                                {
                                    ex = ((AssignExp)ex).e2.value;
                                }
                                if (((ex.op & 0xFF) == 22))
                                {
                                    NewExp ne = (NewExp)ex;
                                    if (((dsym.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass))
                                    {
                                        if ((ne.newargs != null) && ((ne.newargs.get()).length > 1))
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
                                else if (((ex.op & 0xFF) == 161))
                                {
                                    FuncDeclaration f = ((FuncExp)ex).fd;
                                    f.tookAddressOf--;
                                }
                            }
                        }
                        else
                        {
                            dsym._init = initializerSemantic(dsym._init, this.sc, dsym.type, ((this.sc.get()).intypeof == 1) ? NeedInterpret.INITnointerpret : NeedInterpret.INITinterpret);
                            ExpInitializer init_err = dsym._init.isExpInitializer();
                            if ((init_err != null) && ((init_err.exp.op & 0xFF) == 234))
                            {
                                errorSupplemental(dsym.loc, new BytePtr("compile time context created here"));
                            }
                        }
                    }
                    else if (parent.isAggregateDeclaration() != null)
                    {
                        dsym._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                        (dsym._scope.get()).setNoFree();
                    }
                    else if (((dsym.storage_class & 9437188L) != 0) || dsym.type.isConst() || dsym.type.isImmutable())
                    {
                        if (inferred == 0)
                        {
                            int errors = global.errors;
                            dsym.inuse++;
                            if (ei != null)
                            {
                                Expression exp = ei.exp.syntaxCopy();
                                boolean needctfe = dsym.isDataseg() || ((dsym.storage_class & 8388608L) != 0);
                                if (needctfe)
                                {
                                    this.sc = pcopy((this.sc.get()).startCTFE());
                                }
                                exp = expressionSemantic(exp, this.sc);
                                exp = resolveProperties(this.sc, exp);
                                if (needctfe)
                                {
                                    this.sc = pcopy((this.sc.get()).endCTFE());
                                }
                                Type tb2 = dsym.type.toBasetype();
                                Type ti = exp.type.value.toBasetype();
                                {
                                    TypeStruct ts = ti.isTypeStruct();
                                    if ((ts) != null)
                                    {
                                        StructDeclaration sd = ts.sym;
                                        if ((sd.postblit != null) && (pequals(tb2.toDsymbol(null), sd)))
                                        {
                                            if (exp.isLvalue())
                                            {
                                                dsym.error(new BytePtr("of type struct `%s` uses `this(this)`, which is not allowed in static initialization"), tb2.toChars());
                                            }
                                        }
                                    }
                                }
                                ei.exp = exp;
                            }
                            dsym._init = initializerSemantic(dsym._init, this.sc, dsym.type, NeedInterpret.INITinterpret);
                            dsym.inuse--;
                            if ((global.errors > errors))
                            {
                                dsym._init = new ErrorInitializer();
                                dsym.type = Type.terror;
                            }
                        }
                        else
                        {
                            dsym._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                            (dsym._scope.get()).setNoFree();
                        }
                    }
                    this.sc = pcopy((this.sc.get()).pop());
                }
            }
            catch(Dispatch0 __d){}
        /*Ldtor:*/
            dsym.edtor = dsym.callScopeDtor(this.sc);
            if (dsym.edtor != null)
            {
                if (global.params.vsafe && ((dsym.storage_class & 1374397941856L) == 0) && !dsym.isDataseg() && !dsym.doNotInferScope && dsym.type.hasPointers())
                {
                    Type tv = dsym.type.baseElemOf();
                    if (((tv.ty & 0xFF) == ENUMTY.Tstruct) && ((((TypeStruct)tv).sym.dtor.storage_class & 524288L) != 0))
                    {
                        dsym.storage_class |= 524288L;
                    }
                }
                if (((this.sc.get()).func != null) && ((dsym.storage_class & 1073741825L) != 0))
                {
                    dsym.edtor = expressionSemantic(dsym.edtor, (this.sc.get())._module._scope);
                }
                else
                {
                    dsym.edtor = expressionSemantic(dsym.edtor, this.sc);
                }
            }
            dsym.semanticRun = PASS.semanticdone;
            if (((dsym.type.toBasetype().ty & 0xFF) == ENUMTY.Terror))
            {
                dsym.errors = true;
            }
            if (((this.sc.get()).scopesym != null) && ((this.sc.get()).scopesym.isAggregateDeclaration() == null))
            {
                {
                    ScopeDsymbol sym = (this.sc.get()).scopesym;
                    for (; (sym != null) && (dsym.endlinnum == 0);sym = sym.parent.value != null ? sym.parent.value.isScopeDsymbol() : null) {
                        dsym.endlinnum = sym.endlinnum;
                    }
                }
            }
        }

        public  void visit(TypeInfoDeclaration dsym) {
            assert((dsym.linkage == LINK.c));
        }

        public  void visit(Import imp) {
            if ((imp.semanticRun > PASS.init))
            {
                return ;
            }
            if (imp._scope != null)
            {
                this.sc = pcopy(imp._scope);
                imp._scope = null;
            }
            if (this.sc == null)
            {
                return ;
            }
            imp.semanticRun = PASS.semantic;
            boolean loadErrored = false;
            if (imp.mod == null)
            {
                loadErrored = imp.load(this.sc);
                if (imp.mod != null)
                {
                    imp.mod.importAll(null);
                }
            }
            if (imp.mod != null)
            {
                if (((this.sc.get()).minst != null) && ((this.sc.get()).tinst != null))
                {
                    (this.sc.get()).tinst.importedModules.value.push(imp.mod);
                    (this.sc.get()).minst.aimports.push(imp.mod);
                }
                else
                {
                    (this.sc.get())._module.aimports.push(imp.mod);
                }
                if ((this.sc.get()).explicitProtection != 0)
                {
                    imp.protection.opAssign((this.sc.get()).protection.copy());
                }
                if ((imp.aliasId == null) && (imp.names.length == 0))
                {
                    ScopeDsymbol scopesym = null;
                    {
                        Ptr<Scope> scd = this.sc;
                        for (; scd != null;scd = pcopy((scd.get()).enclosing)){
                            if ((scd.get()).scopesym == null)
                            {
                                continue;
                            }
                            scopesym = (scd.get()).scopesym;
                            break;
                        }
                    }
                    if (imp.isstatic == 0)
                    {
                        scopesym.importScope(imp.mod, imp.protection);
                    }
                    if (imp.packages != null)
                    {
                        dmodule.Package p = imp.pkg.value;
                        scopesym.addAccessiblePackage(p, imp.protection);
                        {
                            Slice<Identifier> __r1160 = (imp.packages.get()).opSlice(1, (imp.packages.get()).length).copy();
                            int __key1161 = 0;
                            for (; (__key1161 < __r1160.getLength());__key1161 += 1) {
                                Identifier id = __r1160.get(__key1161);
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
                    (this.sc.get())._module.needmoduleinfo = 1;
                }
                this.sc = pcopy((this.sc.get()).push(imp.mod));
                (this.sc.get()).protection.opAssign(imp.protection.copy());
                {
                    int i = 0;
                    for (; (i < imp.aliasdecls.length);i++){
                        AliasDeclaration ad = imp.aliasdecls.get(i);
                        Dsymbol sym = imp.mod.search(imp.loc, imp.names.get(i), 1);
                        if (sym != null)
                        {
                            if (!symbolIsVisible(this.sc, sym))
                            {
                                imp.mod.error(imp.loc, new BytePtr("member `%s` is not visible from module `%s`"), imp.names.get(i).toChars(), (this.sc.get())._module.toChars());
                            }
                            dsymbolSemantic(ad, this.sc);
                        }
                        else
                        {
                            Dsymbol s = imp.mod.search_correct(imp.names.get(i));
                            if (s != null)
                            {
                                imp.mod.error(imp.loc, new BytePtr("import `%s` not found, did you mean %s `%s`?"), imp.names.get(i).toChars(), s.kind(), s.toPrettyChars(false));
                            }
                            else
                            {
                                imp.mod.error(imp.loc, new BytePtr("import `%s` not found"), imp.names.get(i).toChars());
                            }
                            ad.type = Type.terror;
                        }
                    }
                }
                this.sc = pcopy((this.sc.get()).pop());
            }
            imp.semanticRun = PASS.semanticdone;
            if ((global.params.moduleDeps != null) && !((pequals(imp.id, Id.object)) && (pequals((this.sc.get())._module.ident, Id.object))) && (!pequals((this.sc.get())._module.ident, Id.entrypoint)) && (strcmp((this.sc.get())._module.ident.toChars(), new BytePtr("__main")) != 0))
            {
                Ptr<OutBuffer> ob = global.params.moduleDeps;
                dmodule.Module imod = (this.sc.get()).instantiatingModule();
                if (global.params.moduleDepsFile.getLength() == 0)
                {
                    (ob.get()).writestring(new ByteSlice("depsImport "));
                }
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
                if (imp.packages != null)
                {
                    {
                        int i = 0;
                        for (; (i < (imp.packages.get()).length);i++){
                            Identifier pid = (imp.packages.get()).get(i);
                            (ob.get()).printf(new BytePtr("%s."), pid.toChars());
                        }
                    }
                }
                (ob.get()).writestring(imp.id.asString());
                (ob.get()).writestring(new ByteSlice(" ("));
                if (imp.mod != null)
                {
                    escapePath(ob, imp.mod.srcfile.toChars());
                }
                else
                {
                    (ob.get()).writestring(new ByteSlice("???"));
                }
                (ob.get()).writeByte(41);
                {
                    Slice<Identifier> __r1163 = imp.names.opSlice().copy();
                    int __key1162 = 0;
                    for (; (__key1162 < __r1163.getLength());__key1162 += 1) {
                        Identifier name = __r1163.get(__key1162);
                        int i = __key1162;
                        if ((i == 0))
                        {
                            (ob.get()).writeByte(58);
                        }
                        else
                        {
                            (ob.get()).writeByte(44);
                        }
                        Identifier _alias = imp.aliases.get(i);
                        if (_alias == null)
                        {
                            (ob.get()).printf(new BytePtr("%s"), name.toChars());
                            _alias = name;
                        }
                        else
                        {
                            (ob.get()).printf(new BytePtr("%s=%s"), _alias.toChars(), name.toChars());
                        }
                    }
                }
                if (imp.aliasId != null)
                {
                    (ob.get()).printf(new BytePtr(" -> %s"), imp.aliasId.toChars());
                }
                (ob.get()).writenl();
            }
        }

        public  void attribSemantic(AttribDeclaration ad) {
            if ((ad.semanticRun != PASS.init))
            {
                return ;
            }
            ad.semanticRun = PASS.semantic;
            Ptr<DArray<Dsymbol>> d = ad.include(this.sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = ad.newScope(this.sc);
                boolean errors = false;
                {
                    int i = 0;
                    for (; (i < (d.get()).length);i++){
                        Dsymbol s = (d.get()).get(i);
                        dsymbolSemantic(s, sc2);
                        (errors ? 1 : 0) |= (s.errors ? 1 : 0);
                    }
                }
                (ad.errors ? 1 : 0) |= (errors ? 1 : 0);
                if ((sc2 != this.sc))
                {
                    (sc2.get()).pop();
                }
            }
            ad.semanticRun = PASS.semanticdone;
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
                error(scd.loc, new BytePtr("%s can only be a part of an aggregate, not %s `%s`"), scd.kind(), p.kind(), p.toChars());
                scd.errors = true;
                return ;
            }
            if (scd.decl != null)
            {
                this.sc = pcopy((this.sc.get()).push());
                (this.sc.get()).stc &= -1208484098L;
                (this.sc.get()).inunion = scd.isunion ? scd : null;
                (this.sc.get()).flags = 0;
                {
                    int i = 0;
                    for (; (i < (scd.decl.get()).length);i++){
                        Dsymbol s = (scd.decl.get()).get(i);
                        dsymbolSemantic(s, this.sc);
                    }
                }
                this.sc = pcopy((this.sc.get()).pop());
            }
        }

        public  void visit(PragmaDeclaration pd) {
            try {
                try {
                    if (global.params.mscoff)
                    {
                        if ((pequals(pd.ident, Id.linkerDirective)))
                        {
                            if ((pd.args == null) || ((pd.args.get()).length != 1))
                            {
                                pd.error(new BytePtr("one string argument expected for pragma(linkerDirective)"));
                            }
                            else
                            {
                                StringExp se = semanticString(this.sc, (pd.args.get()).get(0), new BytePtr("linker directive"));
                                if (se == null)
                                {
                                    /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                                }
                                pd.args.get().set(0, se);
                                if (global.params.verbose)
                                {
                                    message(new BytePtr("linkopt   %.*s"), se.len, se.string);
                                }
                            }
                            /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                        }
                    }
                    if ((pequals(pd.ident, Id.msg)))
                    {
                        if (pd.args != null)
                        {
                            {
                                int i = 0;
                                for (; (i < (pd.args.get()).length);i++){
                                    Expression e = (pd.args.get()).get(i);
                                    this.sc = pcopy((this.sc.get()).startCTFE());
                                    e = expressionSemantic(e, this.sc);
                                    e = resolveProperties(this.sc, e);
                                    this.sc = pcopy((this.sc.get()).endCTFE());
                                    if ((e.type.value != null) && ((e.type.value.ty & 0xFF) == ENUMTY.Tvoid))
                                    {
                                        error(pd.loc, new BytePtr("Cannot pass argument `%s` to `pragma msg` because it is `void`"), e.toChars());
                                        return ;
                                    }
                                    e = ctfeInterpretForPragmaMsg(e);
                                    if (((e.op & 0xFF) == 127))
                                    {
                                        errorSupplemental(pd.loc, new BytePtr("while evaluating `pragma(msg, %s)`"), (pd.args.get()).get(i).toChars());
                                        return ;
                                    }
                                    StringExp se = e.toStringExp();
                                    if (se != null)
                                    {
                                        se = se.toUTF8(this.sc);
                                        fprintf(stderr, new BytePtr("%.*s"), se.len, se.string);
                                    }
                                    else
                                    {
                                        fprintf(stderr, new BytePtr("%s"), e.toChars());
                                    }
                                }
                            }
                            fprintf(stderr, new BytePtr("\n"));
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if ((pequals(pd.ident, Id.lib)))
                    {
                        if ((pd.args == null) || ((pd.args.get()).length != 1))
                        {
                            pd.error(new BytePtr("string expected for library name"));
                        }
                        else
                        {
                            StringExp se = semanticString(this.sc, (pd.args.get()).get(0), new BytePtr("library name"));
                            if (se == null)
                            {
                                /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                            }
                            pd.args.get().set(0, se);
                            ByteSlice name = xarraydup(se.string.slice(0,se.len)).copy();
                            if (global.params.verbose)
                            {
                                message(new BytePtr("library   %s"), toBytePtr(name));
                            }
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
                    else if ((pequals(pd.ident, Id.startaddress)))
                    {
                        if ((pd.args == null) || ((pd.args.get()).length != 1))
                        {
                            pd.error(new BytePtr("function name expected for start address"));
                        }
                        else
                        {
                            Expression e = (pd.args.get()).get(0);
                            this.sc = pcopy((this.sc.get()).startCTFE());
                            e = expressionSemantic(e, this.sc);
                            this.sc = pcopy((this.sc.get()).endCTFE());
                            pd.args.get().set(0, e);
                            Dsymbol sa = getDsymbol(e);
                            if ((sa == null) || (sa.isFuncDeclaration() == null))
                            {
                                pd.error(new BytePtr("function name expected for start address, not `%s`"), e.toChars());
                            }
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else if ((pequals(pd.ident, Id.Pinline)))
                    {
                        /*goto Ldecl*/throw Dispatch0.INSTANCE;
                    }
                    else if ((pequals(pd.ident, Id.mangle)))
                    {
                        if (pd.args == null)
                        {
                            pd.args = pcopy((refPtr(new DArray<Expression>())));
                        }
                        if (((pd.args.get()).length != 1))
                        {
                            pd.error(new BytePtr("string expected for mangled name"));
                            (pd.args.get()).setDim(1);
                            pd.args.get().set(0, new ErrorExp());
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        StringExp se = semanticString(this.sc, (pd.args.get()).get(0), new BytePtr("mangled name"));
                        if (se == null)
                        {
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        pd.args.get().set(0, se);
                        if (se.len == 0)
                        {
                            pd.error(new BytePtr("zero-length string not allowed for mangled name"));
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        if (((se.sz & 0xFF) != 1))
                        {
                            pd.error(new BytePtr("mangled name characters can only be of type `char`"));
                            /*goto Ldecl*/throw Dispatch0.INSTANCE;
                        }
                        {
                            int i = 0;
                            for (; (i < se.len);){
                                BytePtr p = pcopy(se.string);
                                int c = (p.get(i) & 0xFF);
                                if ((c < 128))
                                {
                                    if (isValidMangling(c))
                                    {
                                        i += 1;
                                        continue;
                                    }
                                    else
                                    {
                                        pd.error(new BytePtr("char 0x%02x not allowed in mangled name"), c);
                                        break;
                                    }
                                }
                                {
                                    BytePtr msg = pcopy(utf_decodeChar(se.string, se.len, i, c));
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
                    else if ((pequals(pd.ident, Id.crt_constructor)) || (pequals(pd.ident, Id.crt_destructor)))
                    {
                        if ((pd.args != null) && ((pd.args.get()).length != 0))
                        {
                            pd.error(new BytePtr("takes no argument"));
                        }
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
                                        for (; (i < (pd.args.get()).length);i++){
                                            Expression e = (pd.args.get()).get(i);
                                            this.sc = pcopy((this.sc.get()).startCTFE());
                                            e = expressionSemantic(e, this.sc);
                                            e = resolveProperties(this.sc, e);
                                            this.sc = pcopy((this.sc.get()).endCTFE());
                                            e = e.ctfeInterpret();
                                            if ((i == 0))
                                            {
                                                buf.writestring(new ByteSlice(" ("));
                                            }
                                            else
                                            {
                                                buf.writeByte(44);
                                            }
                                            buf.writestring(e.toChars());
                                        }
                                    }
                                    if ((pd.args.get()).length != 0)
                                    {
                                        buf.writeByte(41);
                                    }
                                }
                                message(new BytePtr("pragma    %s"), buf.peekChars());
                            }
                            finally {
                            }
                        }
                        /*goto Lnodecl*/throw Dispatch1.INSTANCE;
                    }
                    else
                    {
                        error(pd.loc, new BytePtr("unrecognized `pragma(%s)`"), pd.ident.toChars());
                    }
                }
                catch(Dispatch0 __d){}
            /*Ldecl:*/
                if (pd.decl != null)
                {
                    Ptr<Scope> sc2 = pd.newScope(this.sc);
                    {
                        int i = 0;
                        for (; (i < (pd.decl.get()).length);i++){
                            Dsymbol s = (pd.decl.get()).get(i);
                            dsymbolSemantic(s, sc2);
                            if ((pequals(pd.ident, Id.mangle)))
                            {
                                assert((pd.args != null) && ((pd.args.get()).length == 1));
                                {
                                    StringExp se = (pd.args.get()).get(0).toStringExp();
                                    if ((se) != null)
                                    {
                                        ByteSlice name = xarraydup(se.string.slice(0,se.len)).copy();
                                        int cnt = setMangleOverride(s, name);
                                        if ((cnt > 1))
                                        {
                                            pd.error(new BytePtr("can only apply to a single declaration"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if ((sc2 != this.sc))
                    {
                        (sc2.get()).pop();
                    }
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

        public  Ptr<DArray<Dsymbol>> compileIt(CompileDeclaration cd) {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                if (expressionsToString(buf, this.sc, cd.exps))
                {
                    return null;
                }
                int errors = global.errors;
                int len = buf.value.offset;
                ByteSlice str = buf.value.extractChars().slice(0,len).copy();
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
                try {
                    ParserASTCodegen p = new ParserASTCodegen(cd.loc, (this.sc.get())._module, str, false, diagnosticReporter);
                    try {
                        p.nextToken();
                        Ptr<DArray<Dsymbol>> d = p.parseDeclDefs(0, null, null);
                        if (p.errors())
                        {
                            assert((global.errors != errors));
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
                cd.decl = pcopy(this.compileIt(cd));
                cd.addMember(this.sc, cd.scopesym);
                cd.compiled = true;
                if ((cd._scope != null) && (cd.decl != null))
                {
                    {
                        int i = 0;
                        for (; (i < (cd.decl.get()).length);i++){
                            Dsymbol s = (cd.decl.get()).get(i);
                            s.setScope(cd._scope);
                        }
                    }
                }
            }
            this.attribSemantic(cd);
        }

        public  void visit(CPPNamespaceDeclaration ns) {
            Function1<StringExp,Identifier> identFromSE = new Function1<StringExp,Identifier>() {
                public Identifier invoke(StringExp se) {
                 {
                    ByteSlice sident = se.toStringz().copy();
                    if ((sident.getLength() == 0) || !Identifier.isValidIdentifier(sident))
                    {
                        ns.exp.error(new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), sident.getLength(), toBytePtr(sident));
                        return null;
                    }
                    else
                    {
                        return Identifier.idPool(sident);
                    }
                }}

            };
            if ((ns.ident == null))
            {
                ns.namespace = (this.sc.get()).namespace;
                this.sc = pcopy((this.sc.get()).startCTFE());
                ns.exp = expressionSemantic(ns.exp, this.sc);
                ns.exp = resolveProperties(this.sc, ns.exp);
                this.sc = pcopy((this.sc.get()).endCTFE());
                ns.exp = ns.exp.ctfeInterpret();
                {
                    TupleExp te = ns.exp.isTupleExp();
                    if ((te) != null)
                    {
                        expandTuples(te.exps);
                        CPPNamespaceDeclaration current = ns.namespace;
                        {
                            int d = 0;
                            for (; (d < (te.exps.get()).length);d += 1){
                                Expression exp = (te.exps.get()).get(d);
                                CPPNamespaceDeclaration prev = d != 0 ? current : ns.namespace;
                                current = (d + 1 != (te.exps.get()).length) ? new CPPNamespaceDeclaration(exp, null) : ns;
                                current.exp = exp;
                                current.namespace = prev;
                                {
                                    StringExp se = exp.toStringExp();
                                    if ((se) != null)
                                    {
                                        current.ident = identFromSE.invoke(se);
                                        if ((current.ident == null))
                                        {
                                            return ;
                                        }
                                    }
                                    else
                                    {
                                        ns.exp.error(new BytePtr("`%s`: index %d is not a string constant, it is a `%s`"), ns.exp.toChars(), d, ns.exp.type.value.toChars());
                                    }
                                }
                            }
                        }
                    }
                    else {
                        StringExp se = ns.exp.toStringExp();
                        if ((se) != null)
                        {
                            ns.ident = identFromSE.invoke(se);
                        }
                        else
                        {
                            ns.exp.error(new BytePtr("compile time string constant (or tuple) expected, not `%s`"), ns.exp.toChars());
                        }
                    }
                }
            }
            if (ns.ident != null)
            {
                this.attribSemantic(ns);
            }
        }

        public  void visit(UserAttributeDeclaration uad) {
            if ((uad.decl != null) && (uad._scope == null))
            {
                uad.setScope(this.sc);
            }
            this.attribSemantic(uad);
            return ;
        }

        public  void visit(StaticAssert sa) {
            if ((sa.semanticRun < PASS.semanticdone))
            {
                sa.semanticRun = PASS.semanticdone;
            }
        }

        public  void visit(DebugSymbol ds) {
            if ((ds.semanticRun < PASS.semanticdone))
            {
                ds.semanticRun = PASS.semanticdone;
            }
        }

        public  void visit(VersionSymbol vs) {
            if ((vs.semanticRun < PASS.semanticdone))
            {
                vs.semanticRun = PASS.semanticdone;
            }
        }

        public  void visit(dmodule.Package pkg) {
            if ((pkg.semanticRun < PASS.semanticdone))
            {
                pkg.semanticRun = PASS.semanticdone;
            }
        }

        public  void visit(dmodule.Module m) {
            if ((m.semanticRun != PASS.init))
            {
                return ;
            }
            m.semanticRun = PASS.semantic;
            Ptr<Scope> sc = m._scope;
            if (sc == null)
            {
                Scope.createGlobal(m);
            }
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>() {
                public Void invoke(Dsymbol s) {
                 {
                    dsymbolSemantic(s, sc);
                    dmodule.Module.runDeferredSemantic();
                    return null;
                }}

            };
            foreachDsymbol(m.members, __lambda2);
            if (m.userAttribDecl != null)
            {
                dsymbolSemantic(m.userAttribDecl, sc);
            }
            if (m._scope == null)
            {
                sc = pcopy((sc.get()).pop());
                (sc.get()).pop();
            }
            m.semanticRun = PASS.semanticdone;
        }

        public  void visit(EnumDeclaration ed) {
            if ((ed.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if ((ed.semanticRun == PASS.semantic))
            {
                assert(ed.memtype != null);
                error(ed.loc, new BytePtr("circular reference to enum base type `%s`"), ed.memtype.toChars());
                ed.errors = true;
                ed.semanticRun = PASS.semanticdone;
                return ;
            }
            int dprogress_save = dmodule.Module.dprogress;
            Ptr<Scope> scx = null;
            if (ed._scope != null)
            {
                this.sc = pcopy(ed._scope);
                scx = pcopy(ed._scope);
                ed._scope = null;
            }
            if (this.sc == null)
            {
                return ;
            }
            ed.parent.value = (this.sc.get()).parent.value;
            ed.type = typeSemantic(ed.type, ed.loc, this.sc);
            ed.protection.opAssign((this.sc.get()).protection.copy());
            if (((this.sc.get()).stc & 1024L) != 0)
            {
                ed.isdeprecated = true;
            }
            ed.userAttribDecl = (this.sc.get()).userAttribDecl;
            ed.semanticRun = PASS.semantic;
            if ((ed.members == null) && (ed.memtype == null))
            {
                ed.semanticRun = PASS.semanticdone;
                return ;
            }
            if (ed.symtab == null)
            {
                ed.symtab = new DsymbolTable();
            }
            if (ed.memtype != null)
            {
                ed.memtype = typeSemantic(ed.memtype, ed.loc, this.sc);
                {
                    TypeEnum te = ed.memtype.isTypeEnum();
                    if ((te) != null)
                    {
                        EnumDeclaration sym = (EnumDeclaration)te.toDsymbol(this.sc);
                        if ((sym.memtype == null) || (sym.members == null) || (sym.symtab == null) || (sym._scope != null))
                        {
                            ed._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                            (ed._scope.get()).setNoFree();
                            dmodule.Module.addDeferredSemantic(ed);
                            dmodule.Module.dprogress = dprogress_save;
                            ed.semanticRun = PASS.init;
                            return ;
                        }
                    }
                }
                if (((ed.memtype.ty & 0xFF) == ENUMTY.Tvoid))
                {
                    ed.error(new BytePtr("base type must not be `void`"));
                    ed.memtype = Type.terror;
                }
                if (((ed.memtype.ty & 0xFF) == ENUMTY.Terror))
                {
                    ed.errors = true;
                    Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>() {
                        public Void invoke(Dsymbol s) {
                         {
                            s.errors = true;
                            return null;
                        }}

                    };
                    foreachDsymbol(ed.members, __lambda2);
                    ed.semanticRun = PASS.semanticdone;
                    return ;
                }
            }
            ed.semanticRun = PASS.semanticdone;
            if (ed.members == null)
            {
                return ;
            }
            if (((ed.members.get()).length == 0))
            {
                ed.error(new BytePtr("enum `%s` must have at least one member"), ed.toChars());
                ed.errors = true;
                return ;
            }
            dmodule.Module.dprogress++;
            Ptr<Scope> sce = null;
            if (ed.isAnonymous())
            {
                sce = pcopy(this.sc);
            }
            else
            {
                sce = pcopy((this.sc.get()).push(ed));
                (sce.get()).parent.value = ed;
            }
            sce = pcopy((sce.get()).startCTFE());
            (sce.get()).setNoFree();
            Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>() {
                public Void invoke(Dsymbol s) {
                 {
                    EnumMember em = s.isEnumMember();
                    if (em != null)
                    {
                        em._scope = pcopy(sce);
                    }
                    return null;
                }}

            };
            foreachDsymbol(ed.members, __lambda3);
            if (!ed.added)
            {
                ScopeDsymbol scopesym = null;
                if (ed.isAnonymous())
                {
                    {
                        Ptr<Scope> sct = sce;
                        for (; 1 != 0;sct = pcopy((sct.get()).enclosing)){
                            assert(sct != null);
                            if ((sct.get()).scopesym != null)
                            {
                                scopesym = (sct.get()).scopesym;
                                if ((sct.get()).scopesym.symtab == null)
                                {
                                    (sct.get()).scopesym.symtab = new DsymbolTable();
                                }
                                break;
                            }
                        }
                    }
                }
                else
                {
                    scopesym = ed;
                }
                Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        EnumMember em = s.isEnumMember();
                        if (em != null)
                        {
                            em.ed = ed;
                            em.addMember(sc, scopesym);
                        }
                        return null;
                    }}

                };
                foreachDsymbol(ed.members, __lambda4);
            }
            Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>() {
                public Void invoke(Dsymbol s) {
                 {
                    EnumMember em = s.isEnumMember();
                    if (em != null)
                    {
                        dsymbolSemantic(em, em._scope);
                    }
                    return null;
                }}

            };
            foreachDsymbol(ed.members, __lambda5);
        }

        public  void visit(EnumMember em) {
            Function0<Void> errorReturn = new Function0<Void>() {
                public Void invoke() {
                 {
                    em.errors = true;
                    em.semanticRun = PASS.semanticdone;
                    return null;
                }}

            };
            if (em.errors || (em.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if ((em.semanticRun == PASS.semantic))
            {
                em.error(new BytePtr("circular reference to `enum` member"));
                errorReturn.invoke();
                return ;
            }
            assert(em.ed != null);
            dsymbolSemantic(em.ed, this.sc);
            if (em.ed.errors)
            {
                errorReturn.invoke();
                return ;
            }
            if (em.errors || (em.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (em._scope != null)
            {
                this.sc = pcopy(em._scope);
            }
            if (this.sc == null)
            {
                return ;
            }
            em.semanticRun = PASS.semantic;
            em.protection.opAssign((em.ed.isAnonymous() ? em.ed.protection : new Prot(Prot.Kind.public_)).copy());
            em.linkage = LINK.d;
            em.storage_class |= 8388608L;
            if (em.ed.isAnonymous())
            {
                if (em.userAttribDecl != null)
                {
                    em.userAttribDecl.userAttribDecl = em.ed.userAttribDecl;
                }
                else
                {
                    em.userAttribDecl = em.ed.userAttribDecl;
                }
            }
            boolean first = pequals(em, (em.ed.members.get()).get(0));
            if (em.origType != null)
            {
                em.origType = typeSemantic(em.origType, em.loc, this.sc);
                em.type = em.origType;
                assert(em.value() != null);
            }
            if (em.value() != null)
            {
                Expression e = em.value();
                assert((e.dyncast() == DYNCAST.expression));
                e = expressionSemantic(e, this.sc);
                e = resolveProperties(this.sc, e);
                e = e.ctfeInterpret();
                if (((e.op & 0xFF) == 127))
                {
                    errorReturn.invoke();
                    return ;
                }
                if (first && (em.ed.memtype == null) && !em.ed.isAnonymous())
                {
                    em.ed.memtype = e.type.value;
                    if (((em.ed.memtype.ty & 0xFF) == ENUMTY.Terror))
                    {
                        em.ed.errors = true;
                        errorReturn.invoke();
                        return ;
                    }
                    if (((em.ed.memtype.ty & 0xFF) != ENUMTY.Terror))
                    {
                        Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>() {
                            public Void invoke(Dsymbol s) {
                             {
                                EnumMember enm = s.isEnumMember();
                                if ((enm == null) || (pequals(enm, em)) || (enm.semanticRun < PASS.semanticdone) || (enm.origType != null))
                                {
                                    return null;
                                }
                                Expression ev = enm.value();
                                ev = ev.implicitCastTo(sc, em.ed.memtype);
                                ev = ev.ctfeInterpret();
                                ev = ev.castTo(sc, em.ed.type);
                                if (((ev.op & 0xFF) == 127))
                                {
                                    em.ed.errors = true;
                                }
                                enm.value() = ev;
                                return null;
                            }}

                        };
                        foreachDsymbol(em.ed.members, __lambda3);
                        if (em.ed.errors)
                        {
                            em.ed.memtype = Type.terror;
                            errorReturn.invoke();
                            return ;
                        }
                    }
                }
                if ((em.ed.memtype != null) && (em.origType == null))
                {
                    e = e.implicitCastTo(this.sc, em.ed.memtype);
                    e = e.ctfeInterpret();
                    em.origValue = e;
                    if (!em.ed.isAnonymous())
                    {
                        e = e.castTo(this.sc, em.ed.type.addMod(e.type.value.mod));
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
                if (em.ed.memtype != null)
                {
                    t = em.ed.memtype;
                }
                else
                {
                    t = Type.tint32;
                    if (!em.ed.isAnonymous())
                    {
                        em.ed.memtype = t;
                    }
                }
                Expression e = new IntegerExp(em.loc, 0L, t);
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
                Function1<Dsymbol,Integer> __lambda4 = new Function1<Dsymbol,Integer>() {
                    public Integer invoke(Dsymbol s) {
                     {
                        {
                            EnumMember enm = s.isEnumMember();
                            if ((enm) != null)
                            {
                                if ((pequals(enm, em)))
                                {
                                    return 1;
                                }
                                emprev = enm;
                            }
                        }
                        return 0;
                    }}

                };
                foreachDsymbol(em.ed.members, __lambda4);
                assert(emprev != null);
                if ((emprev.semanticRun < PASS.semanticdone))
                {
                    dsymbolSemantic(emprev, emprev._scope);
                }
                if (emprev.errors)
                {
                    errorReturn.invoke();
                    return ;
                }
                Expression eprev = emprev.value();
                Type tprev = eprev.type.value.toHeadMutable().equals(em.ed.type.toHeadMutable()) ? em.ed.memtype : eprev.type.value;
                Expression emax = getProperty(tprev, em.ed.loc, Id.max, 0);
                emax = expressionSemantic(emax, this.sc);
                emax = emax.ctfeInterpret();
                assert(eprev != null);
                Expression e = new EqualExp(TOK.equal, em.loc, eprev, emax);
                e = expressionSemantic(e, this.sc);
                e = e.ctfeInterpret();
                if (e.toInteger() != 0)
                {
                    em.error(new BytePtr("initialization with `%s.%s+1` causes overflow for type `%s`"), emprev.ed.toChars(), emprev.toChars(), em.ed.memtype.toChars());
                    errorReturn.invoke();
                    return ;
                }
                e = new AddExp(em.loc, eprev, new IntegerExp(em.loc, 1L, Type.tint32));
                e = expressionSemantic(e, this.sc);
                e = e.castTo(this.sc, eprev.type.value);
                e = e.ctfeInterpret();
                if (((e.op & 0xFF) != 127))
                {
                    assert(emprev.origValue != null);
                    em.origValue = new AddExp(em.loc, emprev.origValue, new IntegerExp(em.loc, 1L, Type.tint32));
                    em.origValue = expressionSemantic(em.origValue, this.sc);
                    em.origValue = em.origValue.ctfeInterpret();
                }
                if (((e.op & 0xFF) == 127))
                {
                    errorReturn.invoke();
                    return ;
                }
                if (e.type.value.isfloating())
                {
                    Expression etest = new EqualExp(TOK.equal, em.loc, e, eprev);
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
            {
                em.type = em.value().type.value;
            }
            assert(em.origValue != null);
            em.semanticRun = PASS.semanticdone;
        }

        public  void visit(TemplateDeclaration tempdecl) {
            if ((tempdecl.semanticRun != PASS.init))
            {
                return ;
            }
            if (tempdecl._scope != null)
            {
                this.sc = pcopy(tempdecl._scope);
                tempdecl._scope = null;
            }
            if (this.sc == null)
            {
                return ;
            }
            if (((this.sc.get())._module != null) && (pequals((this.sc.get())._module.ident, Id.object)))
            {
                if ((pequals(tempdecl.ident, Id.RTInfo)))
                {
                    Type.rtinfo = tempdecl;
                }
            }
            if (tempdecl._scope == null)
            {
                tempdecl._scope = pcopy((this.sc.get()).copy());
                (tempdecl._scope.get()).setNoFree();
            }
            tempdecl.semanticRun = PASS.semantic;
            tempdecl.parent.value = (this.sc.get()).parent.value;
            tempdecl.protection.opAssign((this.sc.get()).protection.copy());
            tempdecl.namespace = (this.sc.get()).namespace;
            tempdecl.isstatic = (tempdecl.toParent().isModule() != null) || (((tempdecl._scope.get()).stc & 1L) != 0);
            if (!tempdecl.isstatic)
            {
                {
                    AggregateDeclaration ad = tempdecl.parent.value.pastMixin().isAggregateDeclaration();
                    if ((ad) != null)
                    {
                        ad.makeNested();
                    }
                }
            }
            ScopeDsymbol paramsym = new ScopeDsymbol();
            paramsym.parent.value = tempdecl.parent.value;
            Ptr<Scope> paramscope = (this.sc.get()).push(paramsym);
            (paramscope.get()).stc = 0L;
            if (global.params.doDocComments)
            {
                tempdecl.origParameters = pcopy((refPtr(new DArray<TemplateParameter>((tempdecl.parameters.get()).length))));
                {
                    int i = 0;
                    for (; (i < (tempdecl.parameters.get()).length);i++){
                        TemplateParameter tp = (tempdecl.parameters.get()).get(i);
                        tempdecl.origParameters.get().set(i, tp.syntaxCopy());
                    }
                }
            }
            {
                int i = 0;
                for (; (i < (tempdecl.parameters.get()).length);i++){
                    TemplateParameter tp = (tempdecl.parameters.get()).get(i);
                    if (!tp.declareParameter(paramscope))
                    {
                        error(tp.loc, new BytePtr("parameter `%s` multiply defined"), tp.ident.toChars());
                        tempdecl.errors = true;
                    }
                    if (!tpsemantic(tp, paramscope, tempdecl.parameters))
                    {
                        tempdecl.errors = true;
                    }
                    if ((i + 1 != (tempdecl.parameters.get()).length) && (tp.isTemplateTupleParameter() != null))
                    {
                        tempdecl.error(new BytePtr("template tuple parameter must be last one"));
                        tempdecl.errors = true;
                    }
                }
            }
            Ref<DArray<TemplateParameter>> tparams = ref(tparams.value = new DArray<TemplateParameter>(1));
            try {
                {
                    int i = 0;
                    for (; (i < (tempdecl.parameters.get()).length);i++){
                        TemplateParameter tp = (tempdecl.parameters.get()).get(i);
                        tparams.value.set(0, tp);
                        {
                            int j = 0;
                            for (; (j < (tempdecl.parameters.get()).length);j++){
                                if ((i == j))
                                {
                                    continue;
                                }
                                {
                                    TemplateTypeParameter ttp = (tempdecl.parameters.get()).get(j).isTemplateTypeParameter();
                                    if ((ttp) != null)
                                    {
                                        if (reliesOnTident(ttp.specType, ptr(tparams), 0))
                                        {
                                            tp.dependent = true;
                                        }
                                    }
                                    else {
                                        TemplateAliasParameter tap = (tempdecl.parameters.get()).get(j).isTemplateAliasParameter();
                                        if ((tap) != null)
                                        {
                                            if (reliesOnTident(tap.specType, ptr(tparams), 0) || reliesOnTident(isType(tap.specAlias), ptr(tparams), 0))
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
                (paramscope.get()).pop();
                tempdecl.onemember = null;
                if (tempdecl.members != null)
                {
                    Dsymbol s = null;
                    if (Dsymbol.oneMembers(tempdecl.members, ptr(s), tempdecl.ident) && (s != null))
                    {
                        tempdecl.onemember = s;
                        s.parent.value = tempdecl;
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
            if ((tm.semanticRun != PASS.init))
            {
                return ;
            }
            tm.semanticRun = PASS.semantic;
            Ptr<Scope> scx = null;
            if (tm._scope != null)
            {
                this.sc = pcopy(tm._scope);
                scx = pcopy(tm._scope);
                tm._scope = null;
            }
            if (!tm.findTempDecl(this.sc) || !tm.semanticTiargs(this.sc) || !tm.findBestMatch(this.sc, null))
            {
                if ((tm.semanticRun == PASS.init))
                {
                    tm._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                    (tm._scope.get()).setNoFree();
                    dmodule.Module.addDeferredSemantic(tm);
                    return ;
                }
                tm.inst = tm;
                tm.errors = true;
                return ;
            }
            TemplateDeclaration tempdecl = tm.tempdecl.isTemplateDeclaration();
            assert(tempdecl != null);
            if (tm.ident == null)
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
                        tm.ident = Identifier.generateId(s, tm.symtab.len() + 1);
                        tm.symtab.insert((Dsymbol)tm);
                    }
                }
            }
            tm.inst = tm;
            tm.parent.value = (this.sc.get()).parent.value;
            {
                Dsymbol s = tm.parent.value;
            L_outer3:
                for (; s != null;s = s.parent.value){
                    TemplateMixin tmix = s.isTemplateMixin();
                    if ((tmix == null) || (!pequals(tempdecl, tmix.tempdecl)))
                    {
                        continue L_outer3;
                    }
                    if (((tm.tiargs.get()).length != (tmix.tiargs.get()).length))
                    {
                        continue L_outer3;
                    }
                    try {
                        {
                            int i = 0;
                        L_outer4:
                            for (; (i < (tm.tiargs.get()).length);i++){
                                RootObject o = (tm.tiargs.get()).get(i);
                                Type ta = isType(o);
                                Expression ea = isExpression(o);
                                Dsymbol sa = isDsymbol(o);
                                RootObject tmo = (tmix.tiargs.get()).get(i);
                                if (ta != null)
                                {
                                    Type tmta = isType(tmo);
                                    if (tmta == null)
                                    {
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    }
                                    if (!ta.equals(tmta))
                                    {
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                else if (ea != null)
                                {
                                    Expression tme = isExpression(tmo);
                                    if ((tme == null) || !ea.equals(tme))
                                    {
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                else if (sa != null)
                                {
                                    Dsymbol tmsa = isDsymbol(tmo);
                                    if ((!pequals(sa, tmsa)))
                                    {
                                        /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                else
                                {
                                    throw new AssertionError("Unreachable code!");
                                }
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
            tm.members = pcopy(Dsymbol.arraySyntaxCopy(tempdecl.members));
            if (tm.members == null)
            {
                return ;
            }
            tm.symtab = new DsymbolTable();
            {
                Ptr<Scope> sce = this.sc;
                for (; 1 != 0;sce = pcopy((sce.get()).enclosing)){
                    ScopeDsymbol sds = (sce.get()).scopesym;
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
            int errorsave = global.errors;
            tm.declareParameters(argscope);
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.addMember(argscope, tm);
                    return null;
                }}

            };
            foreachDsymbol(tm.members, __lambda2);
            Ptr<Scope> sc2 = (argscope.get()).push(tm);
            if (((dsymbolsem.visitnest += 1) > 500))
            {
                global.gag = 0;
                tm.error(new BytePtr("recursive expansion"));
                fatal();
            }
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.setScope(sc2);
                    return null;
                }}

            };
            foreachDsymbol(tm.members, __lambda4);
            Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.importAll(sc2);
                    return null;
                }}

            };
            foreachDsymbol(tm.members, __lambda5);
            Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>() {
                public Void invoke(Dsymbol s) {
                 {
                    dsymbolSemantic(s, sc2);
                    return null;
                }}

            };
            foreachDsymbol(tm.members, __lambda6);
            dsymbolsem.visitnest--;
            AggregateDeclaration ad = tm.toParent().isAggregateDeclaration();
            if (((this.sc.get()).func != null) && (ad == null))
            {
                semantic2(tm, sc2);
                semantic3(tm, sc2);
            }
            if ((global.errors != errorsave))
            {
                tm.error(new BytePtr("error instantiating"));
                tm.errors = true;
            }
            (sc2.get()).pop();
            (argscope.get()).pop();
            (scy.get()).pop();
        }

        public  void visit(Nspace ns) {
            if ((ns.semanticRun != PASS.init))
            {
                return ;
            }
            if (ns._scope != null)
            {
                this.sc = pcopy(ns._scope);
                ns._scope = null;
            }
            if (this.sc == null)
            {
                return ;
            }
            boolean repopulateMembers = false;
            if (ns.identExp != null)
            {
                this.sc = pcopy((this.sc.get()).startCTFE());
                Expression resolved = expressionSemantic(ns.identExp, this.sc);
                resolved = resolveProperties(this.sc, resolved);
                this.sc = pcopy((this.sc.get()).endCTFE());
                resolved = resolved.ctfeInterpret();
                StringExp name = resolved.toStringExp();
                TupleExp tup = name != null ? null : resolved.toTupleExp();
                if ((tup == null) && (name == null))
                {
                    error(ns.loc, new BytePtr("expected string expression for namespace name, got `%s`"), ns.identExp.toChars());
                    return ;
                }
                ns.identExp = resolved;
                if (name != null)
                {
                    ByteSlice ident = name.toStringz().copy();
                    if ((ident.getLength() == 0) || !Identifier.isValidIdentifier(ident))
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
                        Slice<Expression> __r1166 = (tup.exps.get()).opSlice().copy();
                        int __key1165 = 0;
                        for (; (__key1165 < __r1166.getLength());__key1165 += 1) {
                            Expression exp = __r1166.get(__key1165);
                            int i = __key1165;
                            name = exp.toStringExp();
                            if (name == null)
                            {
                                error(ns.loc, new BytePtr("expected string expression for namespace name, got `%s`"), exp.toChars());
                                return ;
                            }
                            ByteSlice ident = name.toStringz().copy();
                            if ((ident.getLength() == 0) || !Identifier.isValidIdentifier(ident))
                            {
                                error(ns.loc, new BytePtr("expected valid identifer for C++ namespace but got `%.*s`"), ident.getLength(), toBytePtr(ident));
                                return ;
                            }
                            if ((i == 0))
                            {
                                ns.ident = Identifier.idPool(ident);
                            }
                            else
                            {
                                Nspace childns = new Nspace(ns.loc, Identifier.idPool(ident), null, parentns.members);
                                parentns.members = pcopy((refPtr(new DArray<Dsymbol>())));
                                (parentns.members.get()).push(childns);
                                parentns = childns;
                                repopulateMembers = true;
                            }
                        }
                    }
                }
            }
            ns.semanticRun = PASS.semantic;
            ns.parent.value = (this.sc.get()).parent.value;
            if (ns.members != null)
            {
                assert(this.sc != null);
                this.sc = pcopy((this.sc.get()).push(ns));
                (this.sc.get()).linkage = LINK.cpp;
                (this.sc.get()).parent.value = ns;
                {
                    Slice<Dsymbol> __r1167 = (ns.members.get()).opSlice().copy();
                    int __key1168 = 0;
                    for (; (__key1168 < __r1167.getLength());__key1168 += 1) {
                        Dsymbol s = __r1167.get(__key1168);
                        if (repopulateMembers)
                        {
                            s.addMember(this.sc, (this.sc.get()).scopesym);
                            s.setScope(this.sc);
                        }
                        s.importAll(this.sc);
                    }
                }
                {
                    Slice<Dsymbol> __r1169 = (ns.members.get()).opSlice().copy();
                    int __key1170 = 0;
                    for (; (__key1170 < __r1169.getLength());__key1170 += 1) {
                        Dsymbol s = __r1169.get(__key1170);
                        dsymbolSemantic(s, this.sc);
                    }
                }
                (this.sc.get()).pop();
            }
            ns.semanticRun = PASS.semanticdone;
        }

        public  void funcDeclarationSemantic(FuncDeclaration funcdecl) {
            TypeFunction f = null;
            AggregateDeclaration ad = null;
            InterfaceDeclaration id = null;
            if ((funcdecl.semanticRun != PASS.init) && (funcdecl.isFuncLiteralDeclaration() != null))
            {
                return ;
            }
            if ((funcdecl.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            assert((funcdecl.semanticRun <= PASS.semantic));
            funcdecl.semanticRun = PASS.semantic;
            if (funcdecl._scope != null)
            {
                this.sc = pcopy(funcdecl._scope);
                funcdecl._scope = null;
            }
            if ((this.sc == null) || funcdecl.errors)
            {
                return ;
            }
            funcdecl.namespace = (this.sc.get()).namespace;
            funcdecl.parent.value = (this.sc.get()).parent.value;
            Dsymbol parent = funcdecl.toParent();
            funcdecl.foverrides.setDim(0);
            funcdecl.storage_class |= (this.sc.get()).stc & -2097153L;
            ad = funcdecl.isThis();
            if ((ad != null) && !funcdecl.generated)
            {
                funcdecl.storage_class |= ad.storage_class & 2685403652L;
                ad.makeNested();
            }
            if ((this.sc.get()).func != null)
            {
                funcdecl.storage_class |= (this.sc.get()).func.storage_class & 137438953472L;
            }
            if (((funcdecl.storage_class & 2685403140L) != 0) && !((ad != null) || funcdecl.isNested()))
            {
                funcdecl.storage_class &= -2685403141L;
            }
            if (((this.sc.get()).flags & 256) != 0)
            {
                funcdecl.flags |= FUNCFLAG.compileTimeOnly;
            }
            FuncLiteralDeclaration fld = funcdecl.isFuncLiteralDeclaration();
            if ((fld != null) && (fld.treq != null))
            {
                Type treq = fld.treq;
                assert(((treq.nextOf().ty & 0xFF) == ENUMTY.Tfunction));
                if (((treq.ty & 0xFF) == ENUMTY.Tdelegate))
                {
                    fld.tok = TOK.delegate_;
                }
                else if (((treq.ty & 0xFF) == ENUMTY.Tpointer) && ((treq.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
                {
                    fld.tok = TOK.function_;
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
                funcdecl.linkage = treq.nextOf().toTypeFunction().linkage;
            }
            else
            {
                funcdecl.linkage = (this.sc.get()).linkage;
            }
            funcdecl.inlining = (this.sc.get()).inlining;
            funcdecl.protection.opAssign((this.sc.get()).protection.copy());
            funcdecl.userAttribDecl = (this.sc.get()).userAttribDecl;
            if (funcdecl.originalType == null)
            {
                funcdecl.originalType = funcdecl.type.syntaxCopy();
            }
            if (((funcdecl.type.ty & 0xFF) != ENUMTY.Tfunction))
            {
                if (((funcdecl.type.ty & 0xFF) != ENUMTY.Terror))
                {
                    funcdecl.error(new BytePtr("`%s` must be a function instead of `%s`"), funcdecl.toChars(), funcdecl.type.toChars());
                    funcdecl.type = Type.terror;
                }
                funcdecl.errors = true;
                return ;
            }
            if (funcdecl.type.deco == null)
            {
                this.sc = pcopy((this.sc.get()).push());
                (this.sc.get()).stc |= funcdecl.storage_class & 137438954496L;
                TypeFunction tf = funcdecl.type.toTypeFunction();
                if ((this.sc.get()).func != null)
                {
                    if ((tf.purity == PURE.impure) && funcdecl.isNested() || (funcdecl.isThis() != null))
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
                                        {
                                            continue;
                                        }
                                        break;
                                    }
                                }
                                if (((fd = p.isFuncDeclaration()) != null))
                                {
                                    break;
                                }
                            }
                        }
                        if ((fd != null) && (fd.isPureBypassingInference() >= PURE.weak) && (funcdecl.isInstantiated() == null))
                        {
                            tf.purity = PURE.fwdref;
                        }
                    }
                }
                if (tf.isref)
                {
                    (this.sc.get()).stc |= 2097152L;
                }
                if (tf.isscope)
                {
                    (this.sc.get()).stc |= 524288L;
                }
                if (tf.isnothrow)
                {
                    (this.sc.get()).stc |= 33554432L;
                }
                if (tf.isnogc)
                {
                    (this.sc.get()).stc |= 4398046511104L;
                }
                if (tf.isproperty)
                {
                    (this.sc.get()).stc |= 4294967296L;
                }
                if ((tf.purity == PURE.fwdref))
                {
                    (this.sc.get()).stc |= 67108864L;
                }
                if ((tf.trust != TRUST.default_))
                {
                    (this.sc.get()).stc &= -60129542145L;
                }
                if ((tf.trust == TRUST.safe))
                {
                    (this.sc.get()).stc |= 8589934592L;
                }
                if ((tf.trust == TRUST.system))
                {
                    (this.sc.get()).stc |= 34359738368L;
                }
                if ((tf.trust == TRUST.trusted))
                {
                    (this.sc.get()).stc |= 17179869184L;
                }
                if (funcdecl.isCtorDeclaration() != null)
                {
                    (this.sc.get()).flags |= 1;
                    Type tret = ad.handleType();
                    assert(tret != null);
                    tret = tret.addStorageClass(funcdecl.storage_class | (this.sc.get()).stc);
                    tret = tret.addMod(funcdecl.type.mod);
                    tf.next.value = tret;
                    if (ad.isStructDeclaration() != null)
                    {
                        (this.sc.get()).stc |= 2097152L;
                    }
                }
                if ((ad != null) && (ad.isClassDeclaration() != null) && tf.isreturn || (((this.sc.get()).stc & 17592186044416L) != 0) && (((this.sc.get()).stc & 1L) == 0))
                {
                    (this.sc.get()).stc |= 524288L;
                }
                if ((((this.sc.get()).stc & 524288L) != 0) && (ad != null) && (ad.isStructDeclaration() != null) && !ad.type.hasPointers())
                {
                    (this.sc.get()).stc &= -524289L;
                    tf.isscope = false;
                }
                (this.sc.get()).linkage = funcdecl.linkage;
                if (!tf.isNaked() && !((funcdecl.isThis() != null) || funcdecl.isNested()))
                {
                    Ref<OutBuffer> buf = ref(new OutBuffer());
                    try {
                        MODtoBuffer(ptr(buf), tf.mod);
                        funcdecl.error(new BytePtr("without `this` cannot be `%s`"), buf.value.peekChars());
                        tf.mod = (byte)0;
                    }
                    finally {
                    }
                }
                long stc = funcdecl.storage_class;
                if (funcdecl.type.isImmutable())
                {
                    stc |= 1048576L;
                }
                if (funcdecl.type.isConst())
                {
                    stc |= 4L;
                }
                if (funcdecl.type.isShared() || ((funcdecl.storage_class & 512L) != 0))
                {
                    stc |= 536870912L;
                }
                if (funcdecl.type.isWild())
                {
                    stc |= 2147483648L;
                }
                funcdecl.type = funcdecl.type.addSTC(stc);
                funcdecl.type = typeSemantic(funcdecl.type, funcdecl.loc, this.sc);
                this.sc = pcopy((this.sc.get()).pop());
            }
            if (((funcdecl.type.ty & 0xFF) != ENUMTY.Tfunction))
            {
                if (((funcdecl.type.ty & 0xFF) != ENUMTY.Terror))
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
            if (((funcdecl.storage_class & 256L) != 0) && !f.isref && !funcdecl.inferRetType)
            {
                funcdecl.error(new BytePtr("storage class `auto` has no effect if return type is not inferred"));
            }
            if (f.isscope && !funcdecl.isNested() && (ad == null))
            {
                funcdecl.error(new BytePtr("functions cannot be `scope`"));
            }
            if (f.isreturn && !funcdecl.needThis() && !funcdecl.isNested())
            {
                if (((this.sc.get()).scopesym != null) && ((this.sc.get()).scopesym.isAggregateDeclaration() != null))
                {
                    funcdecl.error(new BytePtr("`static` member has no `this` to which `return` can apply"));
                }
                else
                {
                    error(funcdecl.loc, new BytePtr("Top-level function `%s` has no `this` to which `return` can apply"), funcdecl.toChars());
                }
            }
            if (funcdecl.isAbstract() && !funcdecl.isVirtual())
            {
                BytePtr sfunc = null;
                if (funcdecl.isStatic())
                {
                    sfunc = pcopy(new BytePtr("static"));
                }
                else if ((funcdecl.protection.kind == Prot.Kind.private_) || (funcdecl.protection.kind == Prot.Kind.package_))
                {
                    sfunc = pcopy(protectionToChars(funcdecl.protection.kind));
                }
                else
                {
                    sfunc = pcopy(new BytePtr("final"));
                }
                funcdecl.error(new BytePtr("`%s` functions cannot be `abstract`"), sfunc);
            }
            if (funcdecl.isOverride() && !funcdecl.isVirtual() && (funcdecl.isFuncLiteralDeclaration() == null))
            {
                int kind = funcdecl.prot().kind;
                if ((kind == Prot.Kind.private_) || (kind == Prot.Kind.package_) && (funcdecl.isMember() != null))
                {
                    funcdecl.error(new BytePtr("`%s` method is not virtual and cannot override"), protectionToChars(kind));
                }
                else
                {
                    funcdecl.error(new BytePtr("cannot override a non-virtual function"));
                }
            }
            if (funcdecl.isAbstract() && funcdecl.isFinalFunc())
            {
                funcdecl.error(new BytePtr("cannot be both `final` and `abstract`"));
            }
            id = parent.isInterfaceDeclaration();
            if (id != null)
            {
                funcdecl.storage_class |= 16L;
                if ((funcdecl.isCtorDeclaration() != null) || (funcdecl.isPostBlitDeclaration() != null) || (funcdecl.isDtorDeclaration() != null) || (funcdecl.isInvariantDeclaration() != null) || (funcdecl.isNewDeclaration() != null) || funcdecl.isDelete())
                {
                    funcdecl.error(new BytePtr("constructors, destructors, postblits, invariants, new and delete functions are not allowed in interface `%s`"), id.toChars());
                }
                if ((funcdecl.fbody != null) && funcdecl.isVirtual())
                {
                    funcdecl.error(new BytePtr("function body only allowed in `final` functions in interface `%s`"), id.toChars());
                }
            }
            {
                UnionDeclaration ud = parent.isUnionDeclaration();
                if ((ud) != null)
                {
                    if ((funcdecl.isPostBlitDeclaration() != null) || (funcdecl.isDtorDeclaration() != null) || (funcdecl.isInvariantDeclaration() != null))
                    {
                        funcdecl.error(new BytePtr("destructors, postblits and invariants are not allowed in union `%s`"), ud.toChars());
                    }
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
                        if ((funcdecl.storage_class & 16L) != 0)
                        {
                            cd.isabstract = Abstract.yes;
                        }
                        if (!funcdecl.isVirtual())
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                        if ((pequals(funcdecl.type.nextOf(), Type.terror)))
                        {
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        }
                        boolean may_override = false;
                        {
                            int i = 0;
                        L_outer5:
                            for (; (i < (cd.baseclasses.get()).length);i++){
                                Ptr<BaseClass> b = (cd.baseclasses.get()).get(i);
                                ClassDeclaration cbd = (b.get()).type.toBasetype().isClassHandle();
                                if (cbd == null)
                                {
                                    continue L_outer5;
                                }
                                {
                                    int j = 0;
                                L_outer6:
                                    for (; (j < cbd.vtbl.value.length);j++){
                                        FuncDeclaration f2 = cbd.vtbl.value.get(j).isFuncDeclaration();
                                        if ((f2 == null) || (!pequals(f2.ident, funcdecl.ident)))
                                        {
                                            continue L_outer6;
                                        }
                                        if ((cbd.parent.value != null) && (cbd.parent.value.isTemplateInstance() != null))
                                        {
                                            if (!f2.functionSemantic())
                                            {
                                                /*goto Ldone*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        may_override = true;
                                    }
                                }
                            }
                        }
                        if (may_override && (funcdecl.type.nextOf() == null))
                        {
                            funcdecl.error(new BytePtr("return type inference is not supported if may override base class function"));
                        }
                        int vi = cd.baseClass != null ? funcdecl.findVtblIndex(ptr(cd.baseClass.vtbl), cd.baseClass.vtbl.value.length, true) : -1;
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
                                                            if ((f2 != null) && f2.isFinalFunc() && (f2.prot().kind != Prot.Kind.private_))
                                                            {
                                                                funcdecl.error(new BytePtr("cannot override `final` function `%s`"), f2.toPrettyChars(false));
                                                            }
                                                        }
                                                    }
                                                }
                                                if (global.params.mscoff && (cd.classKind == ClassKind.cpp) && (cd.baseClass != null) && (cd.baseClass.vtbl.value.length != 0))
                                                {
                                                    funcdecl.interfaceVirtual = pcopy(funcdecl.overrideInterface());
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
                                                    if ((cd.classKind == ClassKind.cpp) && target.reverseCppOverloads)
                                                    {
                                                        funcdecl.vtblIndex = cd.vtbl.value.length;
                                                        boolean found = false;
                                                        {
                                                            Slice<Dsymbol> __r1172 = cd.vtbl.value.opSlice().copy();
                                                            int __key1171 = 0;
                                                            for (; (__key1171 < __r1172.getLength());__key1171 += 1) {
                                                                Dsymbol s_1 = __r1172.get(__key1171);
                                                                int i = __key1171;
                                                                if (found)
                                                                {
                                                                    s_1.isFuncDeclaration().vtblIndex += 1;
                                                                }
                                                                else if ((pequals(s_1.ident, funcdecl.ident)) && (pequals(s_1.parent.value, parent)))
                                                                {
                                                                    funcdecl.vtblIndex = i;
                                                                    found = true;
                                                                    s_1.isFuncDeclaration().vtblIndex += 1;
                                                                }
                                                            }
                                                        }
                                                        cd.vtbl.value.insert(funcdecl.vtblIndex, funcdecl);
                                                    }
                                                    else
                                                    {
                                                        vi = cd.vtbl.value.length;
                                                        cd.vtbl.value.push(funcdecl);
                                                        funcdecl.vtblIndex = vi;
                                                    }
                                                }
                                                break;
                                            case -2:
                                                funcdecl.errors = true;
                                                return ;
                                            default:
                                            FuncDeclaration fdv = cd.baseClass.vtbl.value.get(vi).isFuncDeclaration();
                                            FuncDeclaration fdc = cd.vtbl.value.get(vi).isFuncDeclaration();
                                            if ((pequals(fdc, funcdecl)))
                                            {
                                                doesoverride = true;
                                                break;
                                            }
                                            if ((pequals(fdc.toParent(), parent)))
                                            {
                                                if (((fdc.type.mod & 0xFF) == (fdv.type.mod & 0xFF)) && ((funcdecl.type.mod & 0xFF) != (fdv.type.mod & 0xFF)))
                                                {
                                                    /*goto Lintro*/{ __dispatch1 = -1; continue dispatched_1; }
                                                }
                                            }
                                            if (fdv.isDeprecated())
                                            {
                                                deprecation(funcdecl.loc, new BytePtr("`%s` is overriding the deprecated method `%s`"), funcdecl.toPrettyChars(false), fdv.toPrettyChars(false));
                                            }
                                            if (fdv.isFinalFunc())
                                            {
                                                funcdecl.error(new BytePtr("cannot override `final` function `%s`"), fdv.toPrettyChars(false));
                                            }
                                            if (!funcdecl.isOverride())
                                            {
                                                if (fdv.isFuture())
                                                {
                                                    deprecation(funcdecl.loc, new BytePtr("`@__future` base class method `%s` is being overridden by `%s`; rename the latter"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                    /*goto Lintro*/{ __dispatch1 = -1; continue dispatched_1; }
                                                }
                                                else
                                                {
                                                    int vi2 = funcdecl.findVtblIndex(ptr(cd.baseClass.vtbl), cd.baseClass.vtbl.value.length, false);
                                                    if ((vi2 < 0))
                                                    {
                                                        deprecation(funcdecl.loc, new BytePtr("cannot implicitly override base class method `%s` with `%s`; add `override` attribute"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                    }
                                                    else
                                                    {
                                                        error(funcdecl.loc, new BytePtr("cannot implicitly override base class method `%s` with `%s`; add `override` attribute"), fdv.toPrettyChars(false), funcdecl.toPrettyChars(false));
                                                    }
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
                                                    int vitmp = cd.vtbl.value.length;
                                                    cd.vtbl.value.push(fdc);
                                                    fdc.vtblIndex = vitmp;
                                                }
                                                else if (fdcmixin)
                                                {
                                                    int vitmp_1 = cd.vtbl.value.length;
                                                    cd.vtbl.value.push(funcdecl);
                                                    funcdecl.vtblIndex = vitmp_1;
                                                    break;
                                                }
                                                else
                                                {
                                                    break;
                                                }
                                            }
                                            cd.vtbl.value.set(vi, funcdecl);
                                            funcdecl.vtblIndex = vi;
                                            funcdecl.foverrides.push(fdv);
                                            if (fdv.tintro != null)
                                            {
                                                funcdecl.tintro = fdv.tintro;
                                            }
                                            else if (!funcdecl.type.equals(fdv.type))
                                            {
                                                Ref<Integer> offset = ref(0);
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
                                Slice<Ptr<BaseClass>> __r1173 = cd.interfaces.copy();
                                int __key1174 = 0;
                                for (; (__key1174 < __r1173.getLength());__key1174 += 1) {
                                    Ptr<BaseClass> b = __r1173.get(__key1174);
                                    vi = funcdecl.findVtblIndex(ptr((b.get()).sym.vtbl), (b.get()).sym.vtbl.value.length, true);
                                    switch (vi)
                                    {
                                        case -1:
                                            break;
                                        case -2:
                                            funcdecl.errors = true;
                                            return ;
                                        default:
                                        FuncDeclaration fdv = (FuncDeclaration)(b.get()).sym.vtbl.value.get(vi);
                                        Type ti = null;
                                        foundVtblMatch = true;
                                        funcdecl.foverrides.push(fdv);
                                        if (fdv.tintro != null)
                                        {
                                            ti = fdv.tintro;
                                        }
                                        else if (!funcdecl.type.equals(fdv.type))
                                        {
                                            Ref<Integer> offset = ref(0);
                                            if (fdv.type.nextOf().isBaseOf(funcdecl.type.nextOf(), ptr(offset)))
                                            {
                                                ti = fdv.type;
                                            }
                                        }
                                        if (ti != null)
                                        {
                                            if (funcdecl.tintro != null)
                                            {
                                                if (!funcdecl.tintro.nextOf().equals(ti.nextOf()) && !funcdecl.tintro.nextOf().isBaseOf(ti.nextOf(), null) && !ti.nextOf().isBaseOf(funcdecl.tintro.nextOf(), null))
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
                            if (!doesoverride && funcdecl.isOverride() && (funcdecl.type.nextOf() != null) || !may_override)
                            {
                                Ptr<BaseClass> bc = null;
                                Dsymbol s = null;
                                {
                                    int i = 0;
                                    for (; (i < (cd.baseclasses.get()).length);i++){
                                        bc = pcopy((cd.baseclasses.get()).get(i));
                                        s = (bc.get()).sym.search_correct(funcdecl.ident);
                                        if (s != null)
                                        {
                                            break;
                                        }
                                    }
                                }
                                if (s != null)
                                {
                                    Ref<HdrGenState> hgs = ref(new HdrGenState());
                                    Ref<OutBuffer> buf = ref(new OutBuffer());
                                    try {
                                        FuncDeclaration fd = s.isFuncDeclaration();
                                        functionToBufferFull((TypeFunction)funcdecl.type, ptr(buf), new Identifier(funcdecl.toPrettyChars(false)), ptr(hgs), null);
                                        BytePtr funcdeclToChars = pcopy(buf.value.peekChars());
                                        if (fd != null)
                                        {
                                            Ref<OutBuffer> buf1 = ref(new OutBuffer());
                                            try {
                                                functionToBufferFull((TypeFunction)fd.type, ptr(buf1), new Identifier(fd.toPrettyChars(false)), ptr(hgs), null);
                                                error(funcdecl.loc, new BytePtr("function `%s` does not override any function, did you mean to override `%s`?"), funcdeclToChars, buf1.value.peekChars());
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
                                {
                                    funcdecl.error(new BytePtr("does not override any function"));
                                }
                            }
                        }
                        catch(Dispatch1 __d){}
                    /*L2:*/
                        objc().setSelector(funcdecl, this.sc);
                        objc().checkLinkage(funcdecl);
                        objc().addToClassMethodList(funcdecl, cd);
                        {
                            Slice<Ptr<BaseClass>> __r1175 = cd.interfaces.copy();
                            int __key1176 = 0;
                            for (; (__key1176 < __r1175.getLength());__key1176 += 1) {
                                Ptr<BaseClass> b = __r1175.get(__key1176);
                                if ((b.get()).sym != null)
                                {
                                    Dsymbol s = search_function((b.get()).sym, funcdecl.ident);
                                    if (s != null)
                                    {
                                        FuncDeclaration f2 = s.isFuncDeclaration();
                                        if (f2 != null)
                                        {
                                            f2 = f2.overloadExactMatch(funcdecl.type);
                                            if ((f2 != null) && f2.isFinalFunc() && (f2.prot().kind != Prot.Kind.private_))
                                            {
                                                funcdecl.error(new BytePtr("cannot override `final` function `%s.%s`"), (b.get()).sym.toChars(), f2.toPrettyChars(false));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (funcdecl.isOverride())
                        {
                            if ((funcdecl.storage_class & 137438953472L) != 0)
                            {
                                deprecation(funcdecl.loc, new BytePtr("`%s` cannot be annotated with `@disable` because it is overriding a function in the base class"), funcdecl.toPrettyChars(false));
                            }
                            if (funcdecl.isDeprecated())
                            {
                                deprecation(funcdecl.loc, new BytePtr("`%s` cannot be marked as `deprecated` because it is overriding a function in the base class"), funcdecl.toPrettyChars(false));
                            }
                        }
                    }
                    else if (funcdecl.isOverride() && (parent.isTemplateInstance() == null))
                    {
                        funcdecl.error(new BytePtr("`override` only applies to class member functions"));
                    }
                }
                {
                    TemplateInstance ti = parent.isTemplateInstance();
                    if ((ti) != null)
                    {
                        objc().setSelector(funcdecl, this.sc);
                    }
                }
                objc().validateSelector(funcdecl);
                f = funcdecl.type.toTypeFunction();
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            if ((funcdecl.fbody == null) && !allowsContractWithoutBody(funcdecl))
            {
                funcdecl.error(new BytePtr("`in` and `out` contracts can only appear without a body when they are virtual interface functions or abstract"));
            }
            if (funcdecl.isVirtual())
            {
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                {
                    for (; 1 != 0;){
                        TemplateInstance ti2 = ti.tempdecl.parent.value.isTemplateInstance();
                        if (ti2 == null)
                        {
                            break;
                        }
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
            {
                funcdecl.checkDmain();
            }
            if (funcdecl.canInferAttributes(this.sc))
            {
                funcdecl.initInferAttributes();
            }
            dmodule.Module.dprogress++;
            funcdecl.semanticRun = PASS.semanticdone;
            funcdecl._scope = pcopy((this.sc.get()).copy());
            (funcdecl._scope.get()).setNoFree();
            if (global.params.verbose && !dsymbolsem.funcDeclarationSemanticprintedMain)
            {
                BytePtr type = pcopy(funcdecl.isMain() ? new BytePtr("main") : funcdecl.isWinMain() ? new BytePtr("winmain") : funcdecl.isDllMain() ? new BytePtr("dllmain") : null);
                dmodule.Module mod = (this.sc.get())._module;
                if ((type != null) && (mod != null))
                {
                    dsymbolsem.funcDeclarationSemanticprintedMain = true;
                    BytePtr name = pcopy(FileName.searchPath(global.path, mod.srcfile.toChars(), true));
                    message(new BytePtr("entry     %-10s\u0009%s"), type, name);
                }
            }
            if ((funcdecl.fbody != null) && funcdecl.isMain() && (this.sc.get())._module.isRoot())
            {
                Compiler.genCmain(this.sc);
            }
            assert(((funcdecl.type.ty & 0xFF) != ENUMTY.Terror) || funcdecl.errors);
            {
                int __key1177 = 0;
                int __limit1178 = f.parameterList.length();
                for (; (__key1177 < __limit1178);__key1177 += 1) {
                    int i = __key1177;
                    Parameter param = f.parameterList.get(i);
                    if ((param != null) && (param.userAttribDecl != null))
                    {
                        dsymbolSemantic(param.userAttribDecl, this.sc);
                    }
                }
            }
        }

        public  void visit(FuncDeclaration funcdecl) {
            this.funcDeclarationSemantic(funcdecl);
        }

        public  void visit(CtorDeclaration ctd) {
            if ((ctd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (ctd._scope != null)
            {
                this.sc = pcopy(ctd._scope);
                ctd._scope = null;
            }
            ctd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = ctd.toParentDecl();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(ctd.loc, new BytePtr("constructor can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                ctd.type = Type.terror;
                ctd.errors = true;
                return ;
            }
            this.sc = pcopy((this.sc.get()).push());
            if (((this.sc.get()).stc & 1L) != 0)
            {
                if (((this.sc.get()).stc & 536870912L) != 0)
                {
                    deprecation(ctd.loc, new BytePtr("`shared static` has no effect on a constructor inside a `shared static` block. Use `shared static this()`"));
                }
                else
                {
                    deprecation(ctd.loc, new BytePtr("`static` has no effect on a constructor inside a `static` block. Use `static this()`"));
                }
            }
            (this.sc.get()).stc &= -2L;
            (this.sc.get()).flags |= 1;
            this.funcDeclarationSemantic(ctd);
            (this.sc.get()).pop();
            if (ctd.errors)
            {
                return ;
            }
            TypeFunction tf = ctd.type.toTypeFunction();
            if ((ad != null) && (ctd.parent.value.isTemplateInstance() == null) || (ctd.parent.value.isTemplateMixin() != null))
            {
                int dim = tf.parameterList.length();
                {
                    StructDeclaration sd = ad.isStructDeclaration();
                    if ((sd) != null)
                    {
                        if ((dim == 0) && (tf.parameterList.varargs == VarArg.none))
                        {
                            if ((ctd.fbody != null) || ((ctd.storage_class & 137438953472L) == 0))
                            {
                                ctd.error(new BytePtr("default constructor for structs only allowed with `@disable`, no body, and no parameters"));
                                ctd.storage_class |= 137438953472L;
                                ctd.fbody = null;
                            }
                            sd.noDefaultCtor = true;
                        }
                        else if ((dim == 0) && (tf.parameterList.varargs != VarArg.none))
                        {
                        }
                        else if ((dim != 0) && (tf.parameterList.get(0).defaultArg != null))
                        {
                            if ((ctd.storage_class & 137438953472L) != 0)
                            {
                                ctd.error(new BytePtr("is marked `@disable`, so it cannot have default arguments for all parameters."));
                                errorSupplemental(ctd.loc, new BytePtr("Use `@disable this();` if you want to disable default initialization."));
                            }
                            else
                            {
                                ctd.error(new BytePtr("all parameters have default arguments, but structs cannot have default constructors."));
                            }
                        }
                        else if ((dim == 1) || (dim > 1) && (tf.parameterList.get(1).defaultArg != null))
                        {
                            Parameter param = Parameter.getNth(tf.parameterList.parameters, 0, null);
                            if (((param.storageClass & 2097152L) != 0) && (pequals(param.type.mutableOf().unSharedOf(), sd.type.mutableOf().unSharedOf())))
                            {
                                ctd.isCpCtor = true;
                            }
                        }
                    }
                    else if ((dim == 0) && (tf.parameterList.varargs == VarArg.none))
                    {
                        ad.defaultCtor = ctd;
                    }
                }
            }
        }

        public  void visit(PostBlitDeclaration pbd) {
            if ((pbd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (pbd._scope != null)
            {
                this.sc = pcopy(pbd._scope);
                pbd._scope = null;
            }
            pbd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = pbd.toParent2();
            StructDeclaration ad = p.isStructDeclaration();
            if (ad == null)
            {
                error(pbd.loc, new BytePtr("postblit can only be a member of struct, not %s `%s`"), p.kind(), p.toChars());
                pbd.type = Type.terror;
                pbd.errors = true;
                return ;
            }
            if ((pequals(pbd.ident, Id.postblit)) && (pbd.semanticRun < PASS.semantic))
            {
                ad.postblits.push(pbd);
            }
            if (pbd.type == null)
            {
                pbd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, pbd.storage_class);
            }
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).stc &= -2L;
            (this.sc.get()).linkage = LINK.d;
            this.funcDeclarationSemantic(pbd);
            (this.sc.get()).pop();
        }

        public  void visit(DtorDeclaration dd) {
            if ((dd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (dd._scope != null)
            {
                this.sc = pcopy(dd._scope);
                dd._scope = null;
            }
            dd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = dd.toParent2();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(dd.loc, new BytePtr("destructor can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                dd.type = Type.terror;
                dd.errors = true;
                return ;
            }
            if ((pequals(dd.ident, Id.dtor)) && (dd.semanticRun < PASS.semantic))
            {
                ad.dtors.push(dd);
            }
            if (dd.type == null)
            {
                dd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, dd.storage_class);
                if ((ad.classKind == ClassKind.cpp) && (pequals(dd.ident, Id.dtor)))
                {
                    {
                        ClassDeclaration cldec = ad.isClassDeclaration();
                        if ((cldec) != null)
                        {
                            assert((cldec.cppDtorVtblIndex == -1));
                            if ((cldec.baseClass != null) && (cldec.baseClass.cppDtorVtblIndex != -1))
                            {
                                cldec.cppDtorVtblIndex = cldec.baseClass.cppDtorVtblIndex;
                            }
                            else if (!dd.isFinal())
                            {
                                cldec.cppDtorVtblIndex = cldec.vtbl.value.length;
                                cldec.vtbl.value.push(dd);
                                if (target.twoDtorInVtable)
                                {
                                    cldec.vtbl.value.push(dd);
                                }
                            }
                        }
                    }
                }
            }
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).stc &= -2L;
            if (((this.sc.get()).linkage != LINK.cpp))
            {
                (this.sc.get()).linkage = LINK.d;
            }
            this.funcDeclarationSemantic(dd);
            (this.sc.get()).pop();
        }

        public  void visit(StaticCtorDeclaration scd) {
            if ((scd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (scd._scope != null)
            {
                this.sc = pcopy(scd._scope);
                scd._scope = null;
            }
            scd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = scd.parent.value.pastMixin();
            if (p.isScopeDsymbol() == null)
            {
                BytePtr s = pcopy(scd.isSharedStaticCtorDeclaration() != null ? new BytePtr("shared ") : new BytePtr(""));
                error(scd.loc, new BytePtr("`%sstatic` constructor can only be member of module/aggregate/template, not %s `%s`"), s, p.kind(), p.toChars());
                scd.type = Type.terror;
                scd.errors = true;
                return ;
            }
            if (scd.type == null)
            {
                scd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, scd.storage_class);
            }
            if ((scd.isInstantiated() != null) && (scd.semanticRun < PASS.semantic))
            {
                VarDeclaration v = new VarDeclaration(Loc.initial, Type.tint32, Id.gate, null, 0L);
                v.storage_class = (STC.temp | (scd.isSharedStaticCtorDeclaration() != null ? STC.static_ : STC.tls));
                Ptr<DArray<Statement>> sa = refPtr(new DArray<Statement>());
                Statement s = new ExpStatement(Loc.initial, v);
                (sa.get()).push(s);
                Expression e = new IdentifierExp(Loc.initial, v.ident);
                e = new AddAssignExp(Loc.initial, e, literal_356A192B7913B04C());
                e = new EqualExp(TOK.notEqual, Loc.initial, e, literal_356A192B7913B04C());
                s = new IfStatement(Loc.initial, null, e, new ReturnStatement(Loc.initial, null), null, Loc.initial);
                (sa.get()).push(s);
                if (scd.fbody != null)
                {
                    (sa.get()).push(scd.fbody);
                }
                scd.fbody = new CompoundStatement(Loc.initial, sa);
            }
            this.funcDeclarationSemantic(scd);
            dmodule.Module m = scd.getModule();
            if (m == null)
            {
                m = (this.sc.get())._module;
            }
            if (m != null)
            {
                m.needmoduleinfo = 1;
            }
        }

        public  void visit(StaticDtorDeclaration sdd) {
            if ((sdd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (sdd._scope != null)
            {
                this.sc = pcopy(sdd._scope);
                sdd._scope = null;
            }
            sdd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = sdd.parent.value.pastMixin();
            if (p.isScopeDsymbol() == null)
            {
                BytePtr s = pcopy(sdd.isSharedStaticDtorDeclaration() != null ? new BytePtr("shared ") : new BytePtr(""));
                error(sdd.loc, new BytePtr("`%sstatic` destructor can only be member of module/aggregate/template, not %s `%s`"), s, p.kind(), p.toChars());
                sdd.type = Type.terror;
                sdd.errors = true;
                return ;
            }
            if (sdd.type == null)
            {
                sdd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, sdd.storage_class);
            }
            if ((sdd.isInstantiated() != null) && (sdd.semanticRun < PASS.semantic))
            {
                VarDeclaration v = new VarDeclaration(Loc.initial, Type.tint32, Id.gate, null, 0L);
                v.storage_class = (STC.temp | (sdd.isSharedStaticDtorDeclaration() != null ? STC.static_ : STC.tls));
                Ptr<DArray<Statement>> sa = refPtr(new DArray<Statement>());
                Statement s = new ExpStatement(Loc.initial, v);
                (sa.get()).push(s);
                Expression e = new IdentifierExp(Loc.initial, v.ident);
                e = new AddAssignExp(Loc.initial, e, literal_7984B0A0E139CABA());
                e = new EqualExp(TOK.notEqual, Loc.initial, e, literal_B6589FC6AB0DC82C());
                s = new IfStatement(Loc.initial, null, e, new ReturnStatement(Loc.initial, null), null, Loc.initial);
                (sa.get()).push(s);
                if (sdd.fbody != null)
                {
                    (sa.get()).push(sdd.fbody);
                }
                sdd.fbody = new CompoundStatement(Loc.initial, sa);
                sdd.vgate = v;
            }
            this.funcDeclarationSemantic(sdd);
            dmodule.Module m = sdd.getModule();
            if (m == null)
            {
                m = (this.sc.get())._module;
            }
            if (m != null)
            {
                m.needmoduleinfo = 1;
            }
        }

        public  void visit(InvariantDeclaration invd) {
            if ((invd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (invd._scope != null)
            {
                this.sc = pcopy(invd._scope);
                invd._scope = null;
            }
            invd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = invd.parent.value.pastMixin();
            AggregateDeclaration ad = p.isAggregateDeclaration();
            if (ad == null)
            {
                error(invd.loc, new BytePtr("`invariant` can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                invd.type = Type.terror;
                invd.errors = true;
                return ;
            }
            if ((!pequals(invd.ident, Id.classInvariant)) && (invd.semanticRun < PASS.semantic) && (ad.isUnionDeclaration() == null))
            {
                ad.invs.push(invd);
            }
            if (invd.type == null)
            {
                invd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, invd.storage_class);
            }
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).stc &= -2L;
            (this.sc.get()).stc |= 4L;
            (this.sc.get()).flags = (this.sc.get()).flags & -97 | 32;
            (this.sc.get()).linkage = LINK.d;
            this.funcDeclarationSemantic(invd);
            (this.sc.get()).pop();
        }

        public  void visit(UnitTestDeclaration utd) {
            if ((utd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (utd._scope != null)
            {
                this.sc = pcopy(utd._scope);
                utd._scope = null;
            }
            utd.protection.opAssign((this.sc.get()).protection.copy());
            utd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = utd.parent.value.pastMixin();
            if (p.isScopeDsymbol() == null)
            {
                error(utd.loc, new BytePtr("`unittest` can only be a member of module/aggregate/template, not %s `%s`"), p.kind(), p.toChars());
                utd.type = Type.terror;
                utd.errors = true;
                return ;
            }
            if (global.params.useUnitTests)
            {
                if (utd.type == null)
                {
                    utd.type = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, utd.storage_class);
                }
                Ptr<Scope> sc2 = (this.sc.get()).push();
                (sc2.get()).linkage = LINK.d;
                this.funcDeclarationSemantic(utd);
                (sc2.get()).pop();
            }
        }

        public  void visit(NewDeclaration nd) {
            if (!nd.isDisabled())
            {
                error(nd.loc, new BytePtr("class allocators are obsolete, consider moving the allocation strategy outside of the class"));
            }
            if ((nd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (nd._scope != null)
            {
                this.sc = pcopy(nd._scope);
                nd._scope = null;
            }
            nd.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = nd.parent.value.pastMixin();
            if (p.isAggregateDeclaration() == null)
            {
                error(nd.loc, new BytePtr("allocator can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                nd.type = Type.terror;
                nd.errors = true;
                return ;
            }
            Type tret = Type.tvoid.pointerTo();
            if (nd.type == null)
            {
                nd.type = new TypeFunction(new ParameterList(nd.parameters, nd.varargs), tret, LINK.d, nd.storage_class);
            }
            nd.type = typeSemantic(nd.type, nd.loc, this.sc);
            if (!nd.isDisabled())
            {
                TypeFunction tf = nd.type.toTypeFunction();
                if ((tf.parameterList.length() < 1))
                {
                    nd.error(new BytePtr("at least one argument of type `size_t` expected"));
                }
                else
                {
                    Parameter fparam = tf.parameterList.get(0);
                    if (!fparam.type.equals(Type.tsize_t))
                    {
                        nd.error(new BytePtr("first argument must be type `size_t`, not `%s`"), fparam.type.toChars());
                    }
                }
            }
            this.funcDeclarationSemantic(nd);
        }

        public  void visit(DeleteDeclaration deld) {
            error(deld.loc, new BytePtr("class deallocators are obsolete, consider moving the deallocation strategy outside of the class"));
            if ((deld.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            if (deld._scope != null)
            {
                this.sc = pcopy(deld._scope);
                deld._scope = null;
            }
            deld.parent.value = (this.sc.get()).parent.value;
            Dsymbol p = deld.parent.value.pastMixin();
            if (p.isAggregateDeclaration() == null)
            {
                error(deld.loc, new BytePtr("deallocator can only be a member of aggregate, not %s `%s`"), p.kind(), p.toChars());
                deld.type = Type.terror;
                deld.errors = true;
                return ;
            }
            if (deld.type == null)
            {
                deld.type = new TypeFunction(new ParameterList(deld.parameters, VarArg.none), Type.tvoid, LINK.d, deld.storage_class);
            }
            deld.type = typeSemantic(deld.type, deld.loc, this.sc);
            TypeFunction tf = deld.type.toTypeFunction();
            if ((tf.parameterList.length() != 1))
            {
                deld.error(new BytePtr("one argument of type `void*` expected"));
            }
            else
            {
                Parameter fparam = tf.parameterList.get(0);
                if (!fparam.type.equals(Type.tvoid.pointerTo()))
                {
                    deld.error(new BytePtr("one argument of type `void*` expected, not `%s`"), fparam.type.toChars());
                }
            }
            this.funcDeclarationSemantic(deld);
        }

        public  void reinforceInvariant(AggregateDeclaration ad, Ptr<Scope> sc) {
            {
                int i = 0;
                for (; (i < (ad.members.get()).length);i++){
                    if ((ad.members.get()).get(i) == null)
                    {
                        continue;
                    }
                    FuncDeclaration fd = (ad.members.get()).get(i).isFuncDeclaration();
                    if ((fd == null) || fd.generated || (fd.semanticRun != PASS.semantic3done))
                    {
                        continue;
                    }
                    FuncDeclaration fd_temp = fd.syntaxCopy(null).isFuncDeclaration();
                    fd_temp.storage_class &= -257L;
                    {
                        ClassDeclaration cd = ad.isClassDeclaration();
                        if ((cd) != null)
                        {
                            cd.vtbl.value.remove(fd.vtblIndex);
                        }
                    }
                    dsymbolSemantic(fd_temp, sc);
                    ad.members.get().set(i, fd_temp);
                }
            }
        }

        public  void visit(StructDeclaration sd) {
            if ((sd.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            int errors = global.errors;
            Ptr<Scope> scx = null;
            if (sd._scope != null)
            {
                this.sc = pcopy(sd._scope);
                scx = pcopy(sd._scope);
                sd._scope = null;
            }
            if (sd.parent.value == null)
            {
                assert(((this.sc.get()).parent.value != null) && ((this.sc.get()).func != null));
                sd.parent.value = (this.sc.get()).parent.value;
            }
            assert((sd.parent.value != null) && !sd.isAnonymous());
            if (sd.errors)
            {
                sd.type = Type.terror;
            }
            if ((sd.semanticRun == PASS.init))
            {
                sd.type = sd.type.addSTC((this.sc.get()).stc | sd.storage_class);
            }
            sd.type = typeSemantic(sd.type, sd.loc, this.sc);
            {
                TypeStruct ts = sd.type.isTypeStruct();
                if ((ts) != null)
                {
                    if ((!pequals(ts.sym, sd)))
                    {
                        TemplateInstance ti = ts.sym.isInstantiated();
                        if ((ti != null) && isError(ti))
                        {
                            ts.sym = sd;
                        }
                    }
                }
            }
            Ungag ungag = sd.ungagSpeculative().copy();
            try {
                if ((sd.semanticRun == PASS.init))
                {
                    sd.protection.opAssign((this.sc.get()).protection.copy());
                    sd.alignment = (this.sc.get()).alignment();
                    sd.storage_class |= (this.sc.get()).stc;
                    if ((sd.storage_class & 1024L) != 0)
                    {
                        sd.isdeprecated = true;
                    }
                    if ((sd.storage_class & 16L) != 0)
                    {
                        sd.error(new BytePtr("structs, unions cannot be `abstract`"));
                    }
                    sd.userAttribDecl = (this.sc.get()).userAttribDecl;
                    if (((this.sc.get()).linkage == LINK.cpp))
                    {
                        sd.classKind = ClassKind.cpp;
                    }
                    sd.namespace = (this.sc.get()).namespace;
                }
                else if ((sd.symtab != null) && (scx == null))
                {
                    return ;
                }
                sd.semanticRun = PASS.semantic;
                if (sd.members == null)
                {
                    sd.semanticRun = PASS.semanticdone;
                    return ;
                }
                if (sd.symtab == null)
                {
                    sd.symtab = new DsymbolTable();
                    Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>() {
                        public Void invoke(Dsymbol s) {
                         {
                            s.addMember(sc, sd);
                            return null;
                        }}

                    };
                    foreachDsymbol(sd.members, __lambda2);
                }
                Ptr<Scope> sc2 = sd.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.setScope(sc2);
                        return null;
                    }}

                };
                foreachDsymbol(sd.members, __lambda3);
                Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.importAll(sc2);
                        return null;
                    }}

                };
                foreachDsymbol(sd.members, __lambda4);
                Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        dsymbolSemantic(s, sc2);
                        (sd.errors ? 1 : 0) |= (s.errors ? 1 : 0);
                        return null;
                    }}

                };
                foreachDsymbol(sd.members, __lambda5);
                if (sd.errors)
                {
                    sd.type = Type.terror;
                }
                if (!sd.determineFields())
                {
                    if (((sd.type.ty & 0xFF) != ENUMTY.Terror))
                    {
                        sd.error(sd.loc, new BytePtr("circular or forward reference"));
                        sd.errors = true;
                        sd.type = Type.terror;
                    }
                    (sc2.get()).pop();
                    sd.semanticRun = PASS.semanticdone;
                    return ;
                }
                {
                    Slice<VarDeclaration> __r1179 = sd.fields.opSlice().copy();
                    int __key1180 = 0;
                    for (; (__key1180 < __r1179.getLength());__key1180 += 1) {
                        VarDeclaration v = __r1179.get(__key1180);
                        Type tb = v.type.baseElemOf();
                        if (((tb.ty & 0xFF) != ENUMTY.Tstruct))
                        {
                            continue;
                        }
                        StructDeclaration sdec = ((TypeStruct)tb).sym;
                        if ((sdec.semanticRun >= PASS.semanticdone))
                        {
                            continue;
                        }
                        (sc2.get()).pop();
                        sd._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                        (sd._scope.get()).setNoFree();
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
                if (global.params.useTypeInfo && (Type.dtypeinfo != null))
                {
                    sd.xeq = buildXopEquals(sd, sc2);
                    sd.xcmp = buildXopCmp(sd, sc2);
                    sd.xhash = buildXtoHash(sd, sc2);
                }
                sd.inv = buildInv(sd, sc2);
                if (sd.inv != null)
                {
                    this.reinforceInvariant(sd, sc2);
                }
                dmodule.Module.dprogress++;
                sd.semanticRun = PASS.semanticdone;
                (sc2.get()).pop();
                if (sd.ctor != null)
                {
                    Dsymbol scall = sd.search(Loc.initial, Id.call, 8);
                    if (scall != null)
                    {
                        int xerrors = global.startGagging();
                        this.sc = pcopy((this.sc.get()).push());
                        (this.sc.get()).tinst = null;
                        (this.sc.get()).minst = null;
                        FuncDeclaration fcall = resolveFuncCall(sd.loc, this.sc, scall, null, null, null, FuncResolveFlag.quiet);
                        this.sc = pcopy((this.sc.get()).pop());
                        global.endGagging(xerrors);
                        if ((fcall != null) && fcall.isStatic())
                        {
                            sd.error(fcall.loc, new BytePtr("`static opCall` is hidden by constructors and can never be called"));
                            errorSupplemental(fcall.loc, new BytePtr("Please use a factory method instead, or replace all constructors with `static opCall`."));
                        }
                    }
                }
                if (((sd.type.ty & 0xFF) == ENUMTY.Tstruct) && (!pequals(((TypeStruct)sd.type).sym, sd)))
                {
                    StructDeclaration sym = ((TypeStruct)sd.type).sym;
                    sd.error(new BytePtr("already exists at %s. Perhaps in another function with the same name?"), sym.loc.toChars(global.params.showColumns));
                }
                if ((global.errors != errors))
                {
                    sd.type = Type.terror;
                    sd.errors = true;
                    if (sd.deferred != null)
                    {
                        sd.deferred.errors = true;
                    }
                }
                if ((sd.deferred != null) && (global.gag == 0))
                {
                    semantic2(sd.deferred, this.sc);
                    semantic3(sd.deferred, this.sc);
                }
            }
            finally {
            }
        }

        public  void interfaceSemantic(ClassDeclaration cd) {
            cd.vtblInterfaces = pcopy((refPtr(new DArray<Ptr<BaseClass>>())));
            (cd.vtblInterfaces.get()).reserve(cd.interfaces.getLength());
            {
                Slice<Ptr<BaseClass>> __r1181 = cd.interfaces.copy();
                int __key1182 = 0;
                for (; (__key1182 < __r1181.getLength());__key1182 += 1) {
                    Ptr<BaseClass> b = __r1181.get(__key1182);
                    (cd.vtblInterfaces.get()).push(b);
                    (b.get()).copyBaseInterfaces(cd.vtblInterfaces);
                }
            }
        }

        public  void visit(ClassDeclaration cldec) {
            if ((cldec.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            int errors = global.errors;
            Ptr<Scope> scx = null;
            if (cldec._scope != null)
            {
                this.sc = pcopy(cldec._scope);
                scx = pcopy(cldec._scope);
                cldec._scope = null;
            }
            if (cldec.parent.value == null)
            {
                assert((this.sc.get()).parent.value != null);
                cldec.parent.value = (this.sc.get()).parent.value;
            }
            if (cldec.errors)
            {
                cldec.type = Type.terror;
            }
            cldec.type = typeSemantic(cldec.type, cldec.loc, this.sc);
            {
                TypeClass tc = cldec.type.isTypeClass();
                if ((tc) != null)
                {
                    if ((!pequals(tc.sym, cldec)))
                    {
                        TemplateInstance ti = tc.sym.isInstantiated();
                        if ((ti != null) && isError(ti))
                        {
                            tc.sym = cldec;
                        }
                    }
                }
            }
            Ungag ungag = cldec.ungagSpeculative().copy();
            try {
                if ((cldec.semanticRun == PASS.init))
                {
                    cldec.protection.opAssign((this.sc.get()).protection.copy());
                    cldec.storage_class |= (this.sc.get()).stc;
                    if ((cldec.storage_class & 1024L) != 0)
                    {
                        cldec.isdeprecated = true;
                    }
                    if ((cldec.storage_class & 256L) != 0)
                    {
                        cldec.error(new BytePtr("storage class `auto` is invalid when declaring a class, did you mean to use `scope`?"));
                    }
                    if ((cldec.storage_class & 524288L) != 0)
                    {
                        cldec.stack = true;
                    }
                    if ((cldec.storage_class & 16L) != 0)
                    {
                        cldec.isabstract = Abstract.yes;
                    }
                    cldec.userAttribDecl = (this.sc.get()).userAttribDecl;
                    if (((this.sc.get()).linkage == LINK.cpp))
                    {
                        cldec.classKind = ClassKind.cpp;
                    }
                    cldec.namespace = (this.sc.get()).namespace;
                    if (((this.sc.get()).linkage == LINK.objc))
                    {
                        objc().setObjc(cldec);
                    }
                }
                else if ((cldec.symtab != null) && (scx == null))
                {
                    return ;
                }
                cldec.semanticRun = PASS.semantic;
                try {
                    if ((cldec.baseok < Baseok.done))
                    {
                        // from template resolveBase!(Type)
                        Function1<Type,Type> resolveBaseType = new Function1<Type,Type>() {
                            public Type invoke(Type exp) {
                             {
                                if (scx == null)
                                {
                                    scx = pcopy((sc.get()).copy());
                                    (scx.get()).setNoFree();
                                }
                                cldec._scope = pcopy(scx);
                                Type r = exp.invoke();
                                cldec._scope = null;
                                return r;
                            }}

                        };

                        // from template resolveBase!(Void)
                        Function1<Void,Void> resolveBaseVoid = new Function1<Void,Void>() {
                            public Void invoke(Void exp) {
                             {
                                if (scx == null)
                                {
                                    scx = pcopy((sc.get()).copy());
                                    (scx.get()).setNoFree();
                                }
                                cldec._scope = pcopy(scx);
                                exp.invoke();
                                cldec._scope = null;
                                return null;
                            }}

                        };

                        cldec.baseok = Baseok.start;
                        {
                            int i = 0;
                            for (; (i < (cldec.baseclasses.get()).length);){
                                Ptr<BaseClass> b = (cldec.baseclasses.get()).get(i);
                                Function0<Type> __dgliteral2 = new Function0<Type>() {
                                    public Type invoke() {
                                     {
                                        return typeSemantic((b.get()).type, cldec.loc, sc);
                                    }}

                                };
                                (b.get()).type = resolveBaseType.invoke(__dgliteral2);
                                Type tb = (b.get()).type.toBasetype();
                                {
                                    TypeTuple tup = tb.isTypeTuple();
                                    if ((tup) != null)
                                    {
                                        (cldec.baseclasses.get()).remove(i);
                                        int dim = Parameter.dim(tup.arguments);
                                        {
                                            int j = 0;
                                            for (; (j < dim);j++){
                                                Parameter arg = Parameter.getNth(tup.arguments, j, null);
                                                b = pcopy((refPtr(new BaseClass(arg.type))));
                                                (cldec.baseclasses.get()).insert(i + j, b);
                                            }
                                        }
                                    }
                                    else
                                    {
                                        i++;
                                    }
                                }
                            }
                        }
                        if ((cldec.baseok >= Baseok.done))
                        {
                            if ((cldec.semanticRun >= PASS.semanticdone))
                            {
                                return ;
                            }
                            /*goto Lancestorsdone*/throw Dispatch0.INSTANCE;
                        }
                        if ((cldec.baseclasses.get()).length != 0)
                        {
                            Ptr<BaseClass> b = (cldec.baseclasses.get()).get(0);
                            Type tb = (b.get()).type.toBasetype();
                            TypeClass tc = tb.isTypeClass();
                            try {
                                if (tc == null)
                                {
                                    if ((!pequals((b.get()).type, Type.terror)))
                                    {
                                        cldec.error(new BytePtr("base type must be `class` or `interface`, not `%s`"), (b.get()).type.toChars());
                                    }
                                    (cldec.baseclasses.get()).remove(0);
                                    /*goto L7*/throw Dispatch0.INSTANCE;
                                }
                                if (tc.sym.isDeprecated())
                                {
                                    if (!cldec.isDeprecated())
                                    {
                                        cldec.isdeprecated = true;
                                        tc.checkDeprecated(cldec.loc, this.sc);
                                    }
                                }
                                if (tc.sym.isInterfaceDeclaration() != null)
                                {
                                    /*goto L7*/throw Dispatch0.INSTANCE;
                                }
                                {
                                    ClassDeclaration cdb = tc.sym;
                                L_outer7:
                                    for (; cdb != null;cdb = cdb.baseClass){
                                        if ((pequals(cdb, cldec)))
                                        {
                                            cldec.error(new BytePtr("circular inheritance"));
                                            (cldec.baseclasses.get()).remove(0);
                                            /*goto L7*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                                cldec.baseClass = tc.sym;
                                (b.get()).sym = cldec.baseClass;
                                if ((tc.sym.baseok < Baseok.done))
                                {
                                    Function0<Void> __dgliteral3 = new Function0<Void>() {
                                        public Void invoke() {
                                         {
                                            dsymbolSemantic(tc.sym, null);
                                            return null;
                                        }}

                                    };
                                    resolveBaseVoid.invoke(__dgliteral3);
                                }
                                if ((tc.sym.baseok < Baseok.done))
                                {
                                    if (tc.sym._scope != null)
                                    {
                                        dmodule.Module.addDeferredSemantic(tc.sym);
                                    }
                                    cldec.baseok = Baseok.none;
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*L7:*/
                        }
                        boolean multiClassError = false;
                        {
                            int i = cldec.baseClass != null ? 1 : 0;
                            for (; (i < (cldec.baseclasses.get()).length);){
                                Ptr<BaseClass> b = (cldec.baseclasses.get()).get(i);
                                Type tb = (b.get()).type.toBasetype();
                                TypeClass tc = tb.isTypeClass();
                                if ((tc == null) || (tc.sym.isInterfaceDeclaration() == null))
                                {
                                    if (tc != null)
                                    {
                                        if (!multiClassError)
                                        {
                                            error(cldec.loc, new BytePtr("`%s`: multiple class inheritance is not supported. Use multiple interface inheritance and/or composition."), cldec.toPrettyChars(false));
                                            multiClassError = true;
                                        }
                                        if (tc.sym.fields.length != 0)
                                        {
                                            errorSupplemental(cldec.loc, new BytePtr("`%s` has fields, consider making it a member of `%s`"), (b.get()).type.toChars(), cldec.type.toChars());
                                        }
                                        else
                                        {
                                            errorSupplemental(cldec.loc, new BytePtr("`%s` has no fields, consider making it an `interface`"), (b.get()).type.toChars());
                                        }
                                    }
                                    else if ((!pequals((b.get()).type, Type.terror)))
                                    {
                                        error(cldec.loc, new BytePtr("`%s`: base type must be `interface`, not `%s`"), cldec.toPrettyChars(false), (b.get()).type.toChars());
                                    }
                                    (cldec.baseclasses.get()).remove(i);
                                    continue;
                                }
                                {
                                    int j = cldec.baseClass != null ? 1 : 0;
                                    for (; (j < i);j++){
                                        Ptr<BaseClass> b2 = (cldec.baseclasses.get()).get(j);
                                        if ((pequals((b2.get()).sym, tc.sym)))
                                        {
                                            cldec.error(new BytePtr("inherits from duplicate interface `%s`"), (b2.get()).sym.toChars());
                                            (cldec.baseclasses.get()).remove(i);
                                            continue;
                                        }
                                    }
                                }
                                if (tc.sym.isDeprecated())
                                {
                                    if (!cldec.isDeprecated())
                                    {
                                        cldec.isdeprecated = true;
                                        tc.checkDeprecated(cldec.loc, this.sc);
                                    }
                                }
                                (b.get()).sym = tc.sym;
                                if ((tc.sym.baseok < Baseok.done))
                                {
                                    Function0<Void> __dgliteral4 = new Function0<Void>() {
                                        public Void invoke() {
                                         {
                                            dsymbolSemantic(tc.sym, null);
                                            return null;
                                        }}

                                    };
                                    resolveBaseVoid.invoke(__dgliteral4);
                                }
                                if ((tc.sym.baseok < Baseok.done))
                                {
                                    if (tc.sym._scope != null)
                                    {
                                        dmodule.Module.addDeferredSemantic(tc.sym);
                                    }
                                    cldec.baseok = Baseok.none;
                                }
                                i++;
                            }
                        }
                        if ((cldec.baseok == Baseok.none))
                        {
                            cldec._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                            (cldec._scope.get()).setNoFree();
                            dmodule.Module.addDeferredSemantic(cldec);
                            return ;
                        }
                        cldec.baseok = Baseok.done;
                        if ((cldec.classKind == ClassKind.objc) || (cldec.baseClass != null) && (cldec.baseClass.classKind == ClassKind.objc))
                        {
                            cldec.classKind = ClassKind.objc;
                        }
                        if ((cldec.baseClass == null) && (!pequals(cldec.ident, Id.Object)) && (ClassDeclaration.object != null) && (cldec.classKind == ClassKind.d))
                        {
                            Function0<Void> badObjectDotD = new Function0<Void>() {
                                public Void invoke() {
                                 {
                                    cldec.error(new BytePtr("missing or corrupt object.d"));
                                    fatal();
                                    return null;
                                }}

                            };
                            if ((ClassDeclaration.object == null) || ClassDeclaration.object.errors)
                            {
                                badObjectDotD.invoke();
                            }
                            Type t = ClassDeclaration.object.type;
                            t = typeSemantic(t, cldec.loc, this.sc).toBasetype();
                            if (((t.ty & 0xFF) == ENUMTY.Terror))
                            {
                                badObjectDotD.invoke();
                            }
                            TypeClass tc = t.isTypeClass();
                            assert(tc != null);
                            Ptr<BaseClass> b = refPtr(new BaseClass(tc));
                            (cldec.baseclasses.get()).shift(b);
                            cldec.baseClass = tc.sym;
                            assert(cldec.baseClass.isInterfaceDeclaration() == null);
                            (b.get()).sym = cldec.baseClass;
                        }
                        if (cldec.baseClass != null)
                        {
                            if ((cldec.baseClass.storage_class & 8L) != 0)
                            {
                                cldec.error(new BytePtr("cannot inherit from class `%s` because it is `final`"), cldec.baseClass.toChars());
                            }
                            if (cldec.baseClass.isCOMclass())
                            {
                                cldec.com = true;
                            }
                            if (cldec.baseClass.isCPPclass())
                            {
                                cldec.classKind = ClassKind.cpp;
                            }
                            if (cldec.baseClass.stack)
                            {
                                cldec.stack = true;
                            }
                            cldec.enclosing = cldec.baseClass.enclosing;
                            cldec.storage_class |= cldec.baseClass.storage_class & 2685403140L;
                        }
                        cldec.interfaces = (cldec.baseclasses.get()).tdata().slice(cldec.baseClass != null ? 1 : 0,(cldec.baseclasses.get()).length).copy();
                        {
                            Slice<Ptr<BaseClass>> __r1183 = cldec.interfaces.copy();
                            int __key1184 = 0;
                            for (; (__key1184 < __r1183.getLength());__key1184 += 1) {
                                Ptr<BaseClass> b = __r1183.get(__key1184);
                                if ((b.get()).sym.isCOMinterface())
                                {
                                    cldec.com = true;
                                }
                                if ((cldec.classKind == ClassKind.cpp) && !(b.get()).sym.isCPPinterface())
                                {
                                    error(cldec.loc, new BytePtr("C++ class `%s` cannot implement D interface `%s`"), cldec.toPrettyChars(false), (b.get()).sym.toPrettyChars(false));
                                }
                            }
                        }
                        this.interfaceSemantic(cldec);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lancestorsdone:*/
                if (cldec.members == null)
                {
                    cldec.semanticRun = PASS.semanticdone;
                    return ;
                }
                if (cldec.symtab == null)
                {
                    cldec.symtab = new DsymbolTable();
                    Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>() {
                        public Void invoke(Dsymbol s) {
                         {
                            s.addMember(sc, cldec);
                            return null;
                        }}

                    };
                    foreachDsymbol(cldec.members, __lambda6);
                    Ptr<Scope> sc2 = cldec.newScope(this.sc);
                    Function1<Dsymbol,Void> __lambda7 = new Function1<Dsymbol,Void>() {
                        public Void invoke(Dsymbol s) {
                         {
                            s.setScope(sc2);
                            return null;
                        }}

                    };
                    foreachDsymbol(cldec.members, __lambda7);
                    (sc2.get()).pop();
                }
                {
                    int i = 0;
                    for (; (i < (cldec.baseclasses.get()).length);i++){
                        Ptr<BaseClass> b = (cldec.baseclasses.get()).get(i);
                        Type tb = (b.get()).type.toBasetype();
                        TypeClass tc = tb.isTypeClass();
                        if ((tc.sym.semanticRun < PASS.semanticdone))
                        {
                            cldec._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                            (cldec._scope.get()).setNoFree();
                            if (tc.sym._scope != null)
                            {
                                dmodule.Module.addDeferredSemantic(tc.sym);
                            }
                            dmodule.Module.addDeferredSemantic(cldec);
                            return ;
                        }
                    }
                }
                if ((cldec.baseok == Baseok.done))
                {
                    cldec.baseok = Baseok.semanticdone;
                    objc().setMetaclass(cldec, this.sc);
                    if (cldec.baseClass != null)
                    {
                        if ((cldec.classKind == ClassKind.cpp) && (cldec.baseClass.vtbl.value.length == 0))
                        {
                            cldec.error(new BytePtr("C++ base class `%s` needs at least one virtual function"), cldec.baseClass.toChars());
                        }
                        cldec.vtbl.value.setDim(cldec.baseClass.vtbl.value.length);
                        memcpy((BytePtr)(cldec.vtbl.value.tdata()), (cldec.baseClass.vtbl.value.tdata()), (4 * cldec.vtbl.value.length));
                        cldec.vthis = cldec.baseClass.vthis;
                        cldec.vthis2 = cldec.baseClass.vthis2;
                    }
                    else
                    {
                        cldec.vtbl.value.setDim(0);
                        if (cldec.vtblOffset() != 0)
                        {
                            cldec.vtbl.value.push(cldec);
                        }
                    }
                    if (cldec.vthis != null)
                    {
                        if ((cldec.storage_class & 1L) != 0)
                        {
                            cldec.error(new BytePtr("static class cannot inherit from nested class `%s`"), cldec.baseClass.toChars());
                        }
                        if ((!pequals(cldec.toParentLocal(), cldec.baseClass.toParentLocal())) && (cldec.toParentLocal() == null) || (cldec.baseClass.toParentLocal().getType() == null) || !cldec.baseClass.toParentLocal().getType().isBaseOf(cldec.toParentLocal().getType(), null))
                        {
                            if (cldec.toParentLocal() != null)
                            {
                                cldec.error(new BytePtr("is nested within `%s`, but super class `%s` is nested within `%s`"), cldec.toParentLocal().toChars(), cldec.baseClass.toChars(), cldec.baseClass.toParentLocal().toChars());
                            }
                            else
                            {
                                cldec.error(new BytePtr("is not nested, but super class `%s` is nested within `%s`"), cldec.baseClass.toChars(), cldec.baseClass.toParentLocal().toChars());
                            }
                            cldec.enclosing = null;
                        }
                        if (cldec.vthis2 != null)
                        {
                            if ((!pequals(cldec.toParent2(), cldec.baseClass.toParent2())) && (cldec.toParent2() == null) || (cldec.baseClass.toParent2().getType() == null) || !cldec.baseClass.toParent2().getType().isBaseOf(cldec.toParent2().getType(), null))
                            {
                                if ((cldec.toParent2() != null) && (!pequals(cldec.toParent2(), cldec.toParentLocal())))
                                {
                                    cldec.error(new BytePtr("needs the frame pointer of `%s`, but super class `%s` needs the frame pointer of `%s`"), cldec.toParent2().toChars(), cldec.baseClass.toChars(), cldec.baseClass.toParent2().toChars());
                                }
                                else
                                {
                                    cldec.error(new BytePtr("doesn't need a frame pointer, but super class `%s` needs the frame pointer of `%s`"), cldec.baseClass.toChars(), cldec.baseClass.toParent2().toChars());
                                }
                            }
                        }
                        else
                        {
                            cldec.makeNested2();
                        }
                    }
                    else
                    {
                        cldec.makeNested();
                    }
                }
                Ptr<Scope> sc2 = cldec.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda8 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.importAll(sc2);
                        return null;
                    }}

                };
                foreachDsymbol(cldec.members, __lambda8);
                Function1<Dsymbol,Void> __lambda9 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        dsymbolSemantic(s, sc2);
                        return null;
                    }}

                };
                foreachDsymbol(cldec.members, __lambda9);
                if (!cldec.determineFields())
                {
                    assert((pequals(cldec.type, Type.terror)));
                    (sc2.get()).pop();
                    return ;
                }
                {
                    Slice<VarDeclaration> __r1185 = cldec.fields.opSlice().copy();
                    int __key1186 = 0;
                    for (; (__key1186 < __r1185.getLength());__key1186 += 1) {
                        VarDeclaration v = __r1185.get(__key1186);
                        Type tb = v.type.baseElemOf();
                        if (((tb.ty & 0xFF) != ENUMTY.Tstruct))
                        {
                            continue;
                        }
                        StructDeclaration sd = ((TypeStruct)tb).sym;
                        if ((sd.semanticRun >= PASS.semanticdone))
                        {
                            continue;
                        }
                        (sc2.get()).pop();
                        cldec._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                        (cldec._scope.get()).setNoFree();
                        dmodule.Module.addDeferredSemantic(cldec);
                        return ;
                    }
                }
                cldec.aggNew = (NewDeclaration)cldec.search(Loc.initial, Id.classNew, 8);
                cldec.aggDelete = (DeleteDeclaration)cldec.search(Loc.initial, Id.classDelete, 8);
                cldec.ctor = cldec.searchCtor();
                if ((cldec.ctor == null) && cldec.noDefaultCtor)
                {
                    {
                        Slice<VarDeclaration> __r1187 = cldec.fields.opSlice().copy();
                        int __key1188 = 0;
                        for (; (__key1188 < __r1187.getLength());__key1188 += 1) {
                            VarDeclaration v = __r1187.get(__key1188);
                            if ((v.storage_class & 549755813888L) != 0)
                            {
                                error(v.loc, new BytePtr("field `%s` must be initialized in constructor"), v.toChars());
                            }
                        }
                    }
                }
                if ((cldec.ctor == null) && (cldec.baseClass != null) && (cldec.baseClass.ctor != null))
                {
                    FuncDeclaration fd = resolveFuncCall(cldec.loc, sc2, cldec.baseClass.ctor, null, cldec.type, null, FuncResolveFlag.quiet);
                    if (fd == null)
                    {
                        fd = resolveFuncCall(cldec.loc, sc2, cldec.baseClass.ctor, null, cldec.type.sharedOf(), null, FuncResolveFlag.quiet);
                    }
                    if ((fd != null) && !fd.errors)
                    {
                        TypeFunction btf = fd.type.toTypeFunction();
                        TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), null, LINK.d, fd.storage_class);
                        tf.mod = btf.mod;
                        tf.purity = btf.purity;
                        tf.isnothrow = btf.isnothrow;
                        tf.isnogc = btf.isnogc;
                        tf.trust = btf.trust;
                        CtorDeclaration ctor = new CtorDeclaration(cldec.loc, Loc.initial, 0L, tf, false);
                        ctor.fbody = new CompoundStatement(Loc.initial, refPtr(new DArray<Statement>()));
                        (cldec.members.get()).push(ctor);
                        ctor.addMember(this.sc, cldec);
                        dsymbolSemantic(ctor, sc2);
                        cldec.ctor = ctor;
                        cldec.defaultCtor = ctor;
                    }
                    else
                    {
                        cldec.error(new BytePtr("cannot implicitly generate a default constructor when base class `%s` is missing a default constructor"), cldec.baseClass.toPrettyChars(false));
                    }
                }
                cldec.dtor = buildDtor(cldec, sc2);
                cldec.tidtor = buildExternDDtor(cldec, sc2);
                if ((cldec.classKind == ClassKind.cpp) && (cldec.cppDtorVtblIndex != -1))
                {
                    cldec.dtor.vtblIndex = cldec.cppDtorVtblIndex;
                    cldec.vtbl.value.set(cldec.cppDtorVtblIndex, cldec.dtor);
                    if (target.twoDtorInVtable)
                    {
                        cldec.vtbl.value.set((cldec.cppDtorVtblIndex + 1), cldec.dtor);
                    }
                }
                {
                    FuncDeclaration f = hasIdentityOpAssign(cldec, sc2);
                    if ((f) != null)
                    {
                        if ((f.storage_class & 137438953472L) == 0)
                        {
                            cldec.error(f.loc, new BytePtr("identity assignment operator overload is illegal"));
                        }
                    }
                }
                cldec.inv = buildInv(cldec, sc2);
                if (cldec.inv != null)
                {
                    this.reinforceInvariant(cldec, sc2);
                }
                dmodule.Module.dprogress++;
                cldec.semanticRun = PASS.semanticdone;
                (sc2.get()).pop();
                if ((cldec.isabstract != Abstract.fwdref))
                {
                    int isabstractsave = cldec.isabstract;
                    cldec.isabstract = Abstract.fwdref;
                    cldec.isAbstract();
                    if ((cldec.isabstract != isabstractsave))
                    {
                        cldec.error(new BytePtr("cannot infer `abstract` attribute due to circular dependencies"));
                    }
                }
                if (((cldec.type.ty & 0xFF) == ENUMTY.Tclass) && (!pequals(((TypeClass)cldec.type).sym, cldec)))
                {
                    ClassDeclaration cd = ((TypeClass)cldec.type).sym;
                    cldec.error(new BytePtr("already exists at %s. Perhaps in another function with the same name?"), cd.loc.toChars(global.params.showColumns));
                }
                if ((global.errors != errors))
                {
                    cldec.type = Type.terror;
                    cldec.errors = true;
                    if (cldec.deferred != null)
                    {
                        cldec.deferred.errors = true;
                    }
                }
                if ((cldec.storage_class & 512L) != 0)
                {
                    {
                        Slice<VarDeclaration> __r1189 = cldec.fields.opSlice().copy();
                        int __key1190 = 0;
                        for (; (__key1190 < __r1189.getLength());__key1190 += 1) {
                            VarDeclaration vd = __r1189.get(__key1190);
                            if ((vd.isThisDeclaration() == null) && !vd.prot().isMoreRestrictiveThan(new Prot(Prot.Kind.public_)))
                            {
                                vd.error(new BytePtr("Field members of a `synchronized` class cannot be `%s`"), protectionToChars(vd.prot().kind));
                            }
                        }
                    }
                }
                if ((cldec.deferred != null) && (global.gag == 0))
                {
                    semantic2(cldec.deferred, this.sc);
                    semantic3(cldec.deferred, this.sc);
                }
                if ((cldec.storage_class & 524288L) != 0)
                {
                    deprecation(cldec.loc, new BytePtr("`scope` as a type constraint is deprecated.  Use `scope` at the usage site."));
                }
            }
            finally {
            }
        }

        public  void visit(InterfaceDeclaration idec) {
            Function1<InterfaceDeclaration,Boolean> isAnonymousMetaclass = new Function1<InterfaceDeclaration,Boolean>() {
                public Boolean invoke(InterfaceDeclaration idec) {
                 {
                    return (idec.classKind == ClassKind.objc) && idec.objc.isMeta && idec.isAnonymous();
                }}

            };
            if ((idec.semanticRun >= PASS.semanticdone))
            {
                return ;
            }
            int errors = global.errors;
            Ptr<Scope> scx = null;
            if (idec._scope != null)
            {
                this.sc = pcopy(idec._scope);
                scx = pcopy(idec._scope);
                idec._scope = null;
            }
            if (idec.parent.value == null)
            {
                assert(((this.sc.get()).parent.value != null) && ((this.sc.get()).func != null));
                idec.parent.value = (this.sc.get()).parent.value;
            }
            assert((idec.parent.value != null) && !idec.isAnonymous() || isAnonymousMetaclass.invoke(idec));
            if (idec.errors)
            {
                idec.type = Type.terror;
            }
            idec.type = typeSemantic(idec.type, idec.loc, this.sc);
            if (((idec.type.ty & 0xFF) == ENUMTY.Tclass) && (!pequals(((TypeClass)idec.type).sym, idec)))
            {
                TemplateInstance ti = ((TypeClass)idec.type).sym.isInstantiated();
                if ((ti != null) && isError(ti))
                {
                    ((TypeClass)idec.type).sym = idec;
                }
            }
            Ungag ungag = idec.ungagSpeculative().copy();
            try {
                if ((idec.semanticRun == PASS.init))
                {
                    idec.protection.opAssign((this.sc.get()).protection.copy());
                    idec.storage_class |= (this.sc.get()).stc;
                    if ((idec.storage_class & 1024L) != 0)
                    {
                        idec.isdeprecated = true;
                    }
                    idec.userAttribDecl = (this.sc.get()).userAttribDecl;
                }
                else if (idec.symtab != null)
                {
                    if ((idec.sizeok == Sizeok.done) || (scx == null))
                    {
                        idec.semanticRun = PASS.semanticdone;
                        return ;
                    }
                }
                idec.semanticRun = PASS.semantic;
                try {
                    if ((idec.baseok < Baseok.done))
                    {
                        // from template resolveBase!(Type)
                        Function1<Type,Type> resolveBaseType = new Function1<Type,Type>() {
                            public Type invoke(Type exp) {
                             {
                                if (scx == null)
                                {
                                    scx = pcopy((sc.get()).copy());
                                    (scx.get()).setNoFree();
                                }
                                idec._scope = pcopy(scx);
                                Type r = exp.invoke();
                                idec._scope = null;
                                return r;
                            }}

                        };

                        // from template resolveBase!(Void)
                        Function1<Void,Void> resolveBaseVoid = new Function1<Void,Void>() {
                            public Void invoke(Void exp) {
                             {
                                if (scx == null)
                                {
                                    scx = pcopy((sc.get()).copy());
                                    (scx.get()).setNoFree();
                                }
                                idec._scope = pcopy(scx);
                                exp.invoke();
                                idec._scope = null;
                                return null;
                            }}

                        };

                        idec.baseok = Baseok.start;
                        {
                            int i = 0;
                            for (; (i < (idec.baseclasses.get()).length);){
                                Ptr<BaseClass> b = (idec.baseclasses.get()).get(i);
                                Function0<Type> __dgliteral3 = new Function0<Type>() {
                                    public Type invoke() {
                                     {
                                        return typeSemantic((b.get()).type, idec.loc, sc);
                                    }}

                                };
                                (b.get()).type = resolveBaseType.invoke(__dgliteral3);
                                Type tb = (b.get()).type.toBasetype();
                                {
                                    TypeTuple tup = tb.isTypeTuple();
                                    if ((tup) != null)
                                    {
                                        (idec.baseclasses.get()).remove(i);
                                        int dim = Parameter.dim(tup.arguments);
                                        {
                                            int j = 0;
                                            for (; (j < dim);j++){
                                                Parameter arg = Parameter.getNth(tup.arguments, j, null);
                                                b = pcopy((refPtr(new BaseClass(arg.type))));
                                                (idec.baseclasses.get()).insert(i + j, b);
                                            }
                                        }
                                    }
                                    else
                                    {
                                        i++;
                                    }
                                }
                            }
                        }
                        if ((idec.baseok >= Baseok.done))
                        {
                            if ((idec.semanticRun >= PASS.semanticdone))
                            {
                                return ;
                            }
                            /*goto Lancestorsdone*/throw Dispatch0.INSTANCE;
                        }
                        if (((idec.baseclasses.get()).length == 0) && ((this.sc.get()).linkage == LINK.cpp))
                        {
                            idec.classKind = ClassKind.cpp;
                        }
                        idec.namespace = (this.sc.get()).namespace;
                        if (((this.sc.get()).linkage == LINK.objc))
                        {
                            objc().setObjc(idec);
                            objc().deprecate(idec);
                        }
                        {
                            int i = 0;
                            for (; (i < (idec.baseclasses.get()).length);){
                                Ptr<BaseClass> b = (idec.baseclasses.get()).get(i);
                                Type tb = (b.get()).type.toBasetype();
                                TypeClass tc = ((tb.ty & 0xFF) == ENUMTY.Tclass) ? (TypeClass)tb : null;
                                if ((tc == null) || (tc.sym.isInterfaceDeclaration() == null))
                                {
                                    if ((!pequals((b.get()).type, Type.terror)))
                                    {
                                        idec.error(new BytePtr("base type must be `interface`, not `%s`"), (b.get()).type.toChars());
                                    }
                                    (idec.baseclasses.get()).remove(i);
                                    continue;
                                }
                                {
                                    int j = 0;
                                    for (; (j < i);j++){
                                        Ptr<BaseClass> b2 = (idec.baseclasses.get()).get(j);
                                        if ((pequals((b2.get()).sym, tc.sym)))
                                        {
                                            idec.error(new BytePtr("inherits from duplicate interface `%s`"), (b2.get()).sym.toChars());
                                            (idec.baseclasses.get()).remove(i);
                                            continue;
                                        }
                                    }
                                }
                                if ((pequals(tc.sym, idec)) || idec.isBaseOf2(tc.sym))
                                {
                                    idec.error(new BytePtr("circular inheritance of interface"));
                                    (idec.baseclasses.get()).remove(i);
                                    continue;
                                }
                                if (tc.sym.isDeprecated())
                                {
                                    if (!idec.isDeprecated())
                                    {
                                        idec.isdeprecated = true;
                                        tc.checkDeprecated(idec.loc, this.sc);
                                    }
                                }
                                (b.get()).sym = tc.sym;
                                if ((tc.sym.baseok < Baseok.done))
                                {
                                    Function0<Void> __dgliteral4 = new Function0<Void>() {
                                        public Void invoke() {
                                         {
                                            dsymbolSemantic(tc.sym, null);
                                            return null;
                                        }}

                                    };
                                    resolveBaseVoid.invoke(__dgliteral4);
                                }
                                if ((tc.sym.baseok < Baseok.done))
                                {
                                    if (tc.sym._scope != null)
                                    {
                                        dmodule.Module.addDeferredSemantic(tc.sym);
                                    }
                                    idec.baseok = Baseok.none;
                                }
                                i++;
                            }
                        }
                        if ((idec.baseok == Baseok.none))
                        {
                            idec._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                            (idec._scope.get()).setNoFree();
                            dmodule.Module.addDeferredSemantic(idec);
                            return ;
                        }
                        idec.baseok = Baseok.done;
                        idec.interfaces = (idec.baseclasses.get()).tdata().slice(0,(idec.baseclasses.get()).length).copy();
                        {
                            Slice<Ptr<BaseClass>> __r1191 = idec.interfaces.copy();
                            int __key1192 = 0;
                            for (; (__key1192 < __r1191.getLength());__key1192 += 1) {
                                Ptr<BaseClass> b = __r1191.get(__key1192);
                                if ((b.get()).sym.isCOMinterface())
                                {
                                    idec.com = true;
                                }
                                if ((b.get()).sym.isCPPinterface())
                                {
                                    idec.classKind = ClassKind.cpp;
                                }
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
                if (idec.symtab == null)
                {
                    idec.symtab = new DsymbolTable();
                }
                {
                    int i = 0;
                    for (; (i < (idec.baseclasses.get()).length);i++){
                        Ptr<BaseClass> b = (idec.baseclasses.get()).get(i);
                        Type tb = (b.get()).type.toBasetype();
                        TypeClass tc = tb.isTypeClass();
                        if ((tc.sym.semanticRun < PASS.semanticdone))
                        {
                            idec._scope = pcopy((scx != null ? scx : (this.sc.get()).copy()));
                            (idec._scope.get()).setNoFree();
                            if (tc.sym._scope != null)
                            {
                                dmodule.Module.addDeferredSemantic(tc.sym);
                            }
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
                    {
                        idec.vtbl.value.push(idec);
                    }
                    {
                        Slice<Ptr<BaseClass>> __r1194 = idec.interfaces.copy();
                        int __key1193 = 0;
                    L_outer8:
                        for (; (__key1193 < __r1194.getLength());__key1193 += 1) {
                            Ptr<BaseClass> b = __r1194.get(__key1193);
                            int i = __key1193;
                            try {
                                {
                                    int k = 0;
                                L_outer9:
                                    for (; (k < i);k++){
                                        if ((b == idec.interfaces.get(k)))
                                        {
                                            /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                                if ((b.get()).sym.vtblOffset() != 0)
                                {
                                    int d = (b.get()).sym.vtbl.value.length;
                                    if ((d > 1))
                                    {
                                        idec.vtbl.value.pushSlice((b.get()).sym.vtbl.value.opSlice(1, d));
                                    }
                                }
                                else
                                {
                                    idec.vtbl.value.append(ptr((b.get()).sym.vtbl));
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*Lcontinue:*/
                        }
                    }
                }
                Function1<Dsymbol,Void> __lambda5 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.addMember(sc, idec);
                        return null;
                    }}

                };
                foreachDsymbol(idec.members, __lambda5);
                Ptr<Scope> sc2 = idec.newScope(this.sc);
                Function1<Dsymbol,Void> __lambda6 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.setScope(sc2);
                        return null;
                    }}

                };
                foreachDsymbol(idec.members, __lambda6);
                Function1<Dsymbol,Void> __lambda7 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.importAll(sc2);
                        return null;
                    }}

                };
                foreachDsymbol(idec.members, __lambda7);
                Function1<Dsymbol,Void> __lambda8 = new Function1<Dsymbol,Void>() {
                    public Void invoke(Dsymbol s) {
                     {
                        dsymbolSemantic(s, sc2);
                        return null;
                    }}

                };
                foreachDsymbol(idec.members, __lambda8);
                dmodule.Module.dprogress++;
                idec.semanticRun = PASS.semanticdone;
                (sc2.get()).pop();
                if ((global.errors != errors))
                {
                    idec.type = Type.terror;
                }
                assert(((idec.type.ty & 0xFF) != ENUMTY.Tclass) || (pequals(((TypeClass)idec.type).sym, idec)));
                if ((idec.storage_class & 524288L) != 0)
                {
                    deprecation(idec.loc, new BytePtr("`scope` as a type constraint is deprecated.  Use `scope` at the usage site."));
                }
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
        if (tempinst.inst != null)
        {
            return ;
        }
        if ((tempinst.semanticRun != PASS.init))
        {
            Ungag ungag = ungag = new Ungag(global.gag);
            try {
                if (!tempinst.gagged)
                {
                    global.gag = 0;
                }
                tempinst.error(tempinst.loc, new BytePtr("recursive template expansion"));
                if (tempinst.gagged)
                {
                    tempinst.semanticRun = PASS.init;
                }
                else
                {
                    tempinst.inst = tempinst;
                }
                tempinst.errors = true;
                return ;
            }
            finally {
            }
        }
        tempinst.tinst = (sc.get()).tinst;
        tempinst.minst = (sc.get()).minst;
        if ((tempinst.tinst == null) && ((sc.get()).func != null) && (sc.get()).func.inNonRoot())
        {
            tempinst.minst = null;
        }
        tempinst.gagged = global.gag > 0;
        tempinst.semanticRun = PASS.semantic;
        if (!tempinst.findTempDecl(sc, null) || !tempinst.semanticTiargs(sc) || !tempinst.findBestMatch(sc, fargs))
        {
        /*Lerror:*/
            if (tempinst.gagged)
            {
                tempinst.semanticRun = PASS.init;
            }
            else
            {
                tempinst.inst = tempinst;
            }
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
        {
            /*goto Lerror*/throw Dispatch0.INSTANCE;
        }
        tempinst.namespace = tempdecl.namespace;
        tempinst.inst = tempdecl.findExistingInstance(tempinst, fargs);
        TemplateInstance errinst = null;
        if (tempinst.inst == null)
        {
        }
        else if (tempinst.inst.gagged && !tempinst.gagged && tempinst.inst.errors)
        {
            errinst = tempinst.inst;
        }
        else
        {
            tempinst.parent.value = tempinst.inst.parent.value;
            tempinst.errors = tempinst.inst.errors;
            global.errors += (tempinst.errors ? 1 : 0);
            global.gaggedErrors += (tempinst.errors ? 1 : 0);
            if (tempinst.inst.gagged)
            {
                tempinst.inst.gagged = tempinst.gagged;
            }
            tempinst.tnext = tempinst.inst.tnext;
            tempinst.inst.tnext = tempinst;
            if ((tempinst.minst != null) && tempinst.minst.isRoot() && !((tempinst.inst.minst != null) && tempinst.inst.minst.isRoot()))
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
            {
                tempinst.minst.aimports.append(ptr(tempinst.inst.importedModules));
            }
            return ;
        }
        int errorsave = global.errors;
        tempinst.inst = tempinst;
        tempinst.parent.value = tempinst.enclosing != null ? tempinst.enclosing : tempdecl.parent.value;
        TemplateInstance tempdecl_instance_idx = tempdecl.addInstance(tempinst);
        Ptr<DArray<Dsymbol>> target_symbol_list = tempinst.appendToModuleMember();
        int target_symbol_list_idx = target_symbol_list != null ? (target_symbol_list.get()).length - 1 : 0;
        tempinst.members = pcopy(Dsymbol.arraySyntaxCopy(tempdecl.members));
        {
            int i = 0;
            for (; (i < (tempdecl.parameters.get()).length);i++){
                if (((tempdecl.parameters.get()).get(i).isTemplateThisParameter() == null))
                {
                    continue;
                }
                Type t = isType((tempinst.tiargs.get()).get(i));
                assert(t != null);
                {
                    long stc = ModToStc((t.mod & 0xFF));
                    if ((stc) != 0)
                    {
                        Ptr<DArray<Dsymbol>> s = refPtr(new DArray<Dsymbol>());
                        (s.get()).push(new StorageClassDeclaration(stc, tempinst.members));
                        tempinst.members = pcopy(s);
                    }
                }
                break;
            }
        }
        Ptr<Scope> _scope = tempdecl._scope;
        if ((tempdecl.semanticRun == PASS.init))
        {
            tempinst.error(new BytePtr("template instantiation `%s` forward references template declaration `%s`"), tempinst.toChars(), tempdecl.toChars());
            return ;
        }
        tempinst.argsym = new ScopeDsymbol();
        tempinst.argsym.parent.value = (_scope.get()).parent.value;
        _scope = pcopy((_scope.get()).push(tempinst.argsym));
        (_scope.get()).tinst = tempinst;
        (_scope.get()).minst = tempinst.minst;
        Ptr<Scope> paramscope = (_scope.get()).push();
        (paramscope.get()).stc = 0L;
        (paramscope.get()).protection.opAssign(new Prot(Prot.Kind.public_).copy());
        tempinst.declareParameters(paramscope);
        (paramscope.get()).pop();
        tempinst.symtab = new DsymbolTable();
        Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>() {
            public Void invoke(Dsymbol s) {
             {
                s.addMember(_scope, tempinst);
                return null;
            }}

        };
        foreachDsymbol(tempinst.members, __lambda4);
        if ((tempinst.members.get()).length != 0)
        {
            Dsymbol s = null;
            if (Dsymbol.oneMembers(tempinst.members, ptr(s), tempdecl.ident) && (s != null))
            {
                tempinst.aliasdecl = s;
            }
        }
        if ((fargs != null) && (tempinst.aliasdecl != null))
        {
            {
                FuncDeclaration fd = tempinst.aliasdecl.isFuncDeclaration();
                if ((fd) != null)
                {
                    if (fd.type != null)
                    {
                        {
                            TypeFunction tf = fd.type.isTypeFunction();
                            if ((tf) != null)
                            {
                                tf.fargs = pcopy(fargs);
                            }
                        }
                    }
                }
            }
        }
        Ptr<Scope> sc2 = null;
        sc2 = pcopy((_scope.get()).push(tempinst));
        (sc2.get()).parent.value = tempinst;
        (sc2.get()).tinst = tempinst;
        (sc2.get()).minst = tempinst.minst;
        tempinst.tryExpandMembers(sc2);
        tempinst.semanticRun = PASS.semanticdone;
        if ((tempinst.members.get()).length != 0)
        {
            Dsymbol s = null;
            if (Dsymbol.oneMembers(tempinst.members, ptr(s), tempdecl.ident) && (s != null))
            {
                if ((tempinst.aliasdecl == null) || (!pequals(tempinst.aliasdecl, s)))
                {
                    tempinst.aliasdecl = s;
                }
            }
        }
        try {
            if ((global.errors != errorsave))
            {
                /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            }
            {
                boolean found_deferred_ad = false;
                {
                    int i = 0;
                    for (; (i < dmodule.Module.deferred.length);i++){
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
                if (found_deferred_ad || (dmodule.Module.deferred.length != 0))
                {
                    /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
                }
            }
            {
                semantic2(tempinst, sc2);
            }
            if ((global.errors != errorsave))
            {
                /*goto Laftersemantic*/throw Dispatch0.INSTANCE;
            }
            if (((sc.get()).func != null) || (((sc.get()).flags & 65536) != 0) && (tempinst.tinst == null))
            {
                DArray<TemplateInstance> deferred = new DArray<TemplateInstance>();
                try {
                    tempinst.deferred = pcopy(ptr(deferred));
                    tempinst.trySemantic3(sc2);
                    {
                        int i = 0;
                        for (; (i < deferred.length);i++){
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
                {
                    fd = tempinst.aliasdecl.toAlias2().isFuncDeclaration();
                }
                if (fd != null)
                {
                    FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                    if ((fld != null) && ((fld.tok & 0xFF) == 0))
                    {
                        doSemantic3 = true;
                    }
                    else if ((sc.get()).func != null)
                    {
                        doSemantic3 = true;
                    }
                }
                else if ((sc.get()).func != null)
                {
                    {
                        Slice<RootObject> __r1196 = tempinst.tdtypes.value.opSlice().copy();
                        int __key1197 = 0;
                        for (; (__key1197 < __r1196.getLength());__key1197 += 1) {
                            RootObject oarg = __r1196.get(__key1197);
                            Dsymbol s = getDsymbol(oarg);
                            if (s == null)
                            {
                                continue;
                            }
                            {
                                TemplateDeclaration td = s.isTemplateDeclaration();
                                if ((td) != null)
                                {
                                    if (!td.literal)
                                    {
                                        continue;
                                    }
                                    assert((td.members != null) && ((td.members.get()).length == 1));
                                    s = (td.members.get()).get(0);
                                }
                            }
                            {
                                FuncLiteralDeclaration fld = s.isFuncLiteralDeclaration();
                                if ((fld) != null)
                                {
                                    if (((fld.tok & 0xFF) == 0))
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
                {
                    tempinst.trySemantic3(sc2);
                }
                TemplateInstance ti = tempinst.tinst;
                int nest = 0;
                for (; (ti != null) && (ti.deferred == null) && (ti.tinst != null);){
                    ti = ti.tinst;
                    if (((nest += 1) > 500))
                    {
                        global.gag = 0;
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
                            {
                                break;
                            }
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
        (sc2.get()).pop();
        (_scope.get()).pop();
        if ((global.errors != errorsave))
        {
            if (!tempinst.errors)
            {
                if (!tempdecl.literal)
                {
                    tempinst.error(tempinst.loc, new BytePtr("error instantiating"));
                }
                if (tempinst.tinst != null)
                {
                    tempinst.tinst.printInstantiationTrace();
                }
            }
            tempinst.errors = true;
            if (tempinst.gagged)
            {
                tempdecl.removeInstance(tempdecl_instance_idx);
                if (target_symbol_list != null)
                {
                    assert((pequals((target_symbol_list.get()).get(target_symbol_list_idx), tempinst)));
                    (target_symbol_list.get()).remove(target_symbol_list_idx);
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
            tempdecl.instances.set(ti2, __aaval1198);
        }
    }

    public static void aliasSemantic(AliasDeclaration ds, Ptr<Scope> sc) {
        if ((ds.type != null) && ((ds.type.ty & 0xFF) == ENUMTY.TTraits))
        {
            TypeTraits tt = (TypeTraits)ds.type;
            tt.inAliasDeclaration = true;
            {
                Type t = typeSemantic(tt, tt.loc, sc);
                if ((t) != null)
                {
                    ds.type = t;
                }
                else if (tt.sym != null)
                {
                    ds.aliassym = tt.sym;
                }
            }
            tt.inAliasDeclaration = false;
        }
        if (ds.aliassym != null)
        {
            FuncLiteralDeclaration fd = ds.aliassym.isFuncLiteralDeclaration();
            TemplateDeclaration td = ds.aliassym.isTemplateDeclaration();
            if ((fd != null) || (td != null) && td.literal)
            {
                if ((fd != null) && (fd.semanticRun >= PASS.semanticdone))
                {
                    return ;
                }
                Expression e = new FuncExp(ds.loc, ds.aliassym);
                e = expressionSemantic(e, sc);
                if (((e.op & 0xFF) == 161))
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
            {
                dsymbolSemantic(ds.aliassym, sc);
            }
            return ;
        }
        ds.inuse = 1;
        int errors = global.errors;
        Type oldtype = ds.type;
        Ungag ungag = ungag = new Ungag(global.gag);
        try {
            if ((ds.parent.value != null) && (global.gag != 0) && (ds.isInstantiated() == null) && (ds.toParent2().isFuncDeclaration() == null))
            {
                global.gag = 0;
            }
            if (((ds.type.ty & 0xFF) == ENUMTY.Tident) && (ds._import == null))
            {
                TypeIdentifier tident = (TypeIdentifier)ds.type;
                if ((tident.ident == ds.ident) && (tident.idents.length == 0))
                {
                    error(ds.loc, new BytePtr("`alias %s = %s;` cannot alias itself, use a qualified name to create an overload set"), ds.ident.toChars(), tident.ident.toChars());
                    ds.type = Type.terror;
                }
            }
            Ref<Dsymbol> s = ref(ds.type.toDsymbol(sc));
            if ((errors != global.errors))
            {
                s.value = null;
                ds.type = Type.terror;
            }
            if ((s.value != null) && (pequals(s.value, ds)))
            {
                ds.error(new BytePtr("cannot resolve"));
                s.value = null;
                ds.type = Type.terror;
            }
            if ((s.value == null) || (s.value.isEnumMember() == null))
            {
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ptr<Scope> sc2 = sc;
                if ((ds.storage_class & 4535588225024L) != 0)
                {
                    sc2 = pcopy((sc.get()).push());
                    (sc2.get()).stc |= ds.storage_class & 4536125095936L;
                }
                ds.type = ds.type.addSTC(ds.storage_class);
                resolve(ds.type, ds.loc, sc2, ptr(e), ptr(t), ptr(s), false);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
                if (e.value != null)
                {
                    s.value = getDsymbol(e.value);
                    if (s.value == null)
                    {
                        if (((e.value.op & 0xFF) != 127))
                        {
                            ds.error(new BytePtr("cannot alias an expression `%s`"), e.value.toChars());
                        }
                        t.value = Type.terror;
                    }
                }
                ds.type = t.value;
            }
            if ((pequals(s.value, ds)))
            {
                assert(global.errors != 0);
                ds.type = Type.terror;
                s.value = null;
            }
            if (s.value == null)
            {
                ds.type = typeSemantic(ds.type, ds.loc, sc);
                ds.aliassym = null;
            }
            else
            {
                ds.type = null;
                ds.aliassym = s.value;
            }
            if ((global.gag != 0) && (errors != global.errors))
            {
                ds.type = oldtype;
                ds.aliassym = null;
            }
            ds.inuse = 0;
            ds.semanticRun = PASS.semanticdone;
            {
                Dsymbol sx = ds.overnext;
                if ((sx) != null)
                {
                    ds.overnext = null;
                    if (!ds.overloadInsert(sx))
                    {
                        ScopeDsymbol.multiplyDefined(Loc.initial, sx, ds);
                    }
                }
            }
        }
        finally {
        }
    }

}
