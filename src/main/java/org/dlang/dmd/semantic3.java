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
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.blockexit.*;
import static org.dlang.dmd.clone.*;
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
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

public class semantic3 {

    static boolean LOG = false;
    public static void semantic3(Dsymbol dsym, Ptr<Scope> sc) {
        Semantic3Visitor v = new Semantic3Visitor(sc);
        dsym.accept(v);
    }

    public static class Semantic3Visitor extends Visitor
    {
        public Ptr<Scope> sc = null;
        public  Semantic3Visitor(Ptr<Scope> sc) {
            this.sc = sc;
        }

        public  void visit(Dsymbol _param_0) {
        }

        public  void visit(TemplateInstance tempinst) {
            if ((tempinst.semanticRun.value >= PASS.semantic3))
            {
                return ;
            }
            tempinst.semanticRun.value = PASS.semantic3;
            if (!tempinst.errors.value && (tempinst.members.value != null))
            {
                TemplateDeclaration tempdecl = tempinst.tempdecl.value.isTemplateDeclaration();
                assert(tempdecl != null);
                this.sc = tempdecl._scope.value;
                this.sc = (this.sc.get()).push(tempinst.argsym);
                this.sc = (this.sc.get()).push(tempinst);
                (this.sc.get()).tinst = tempinst;
                (this.sc.get()).minst.value = tempinst.minst.value;
                int needGagging = ((tempinst.gagged && (global.gag.value == 0)) ? 1 : 0);
                int olderrors = global.errors.value;
                int oldGaggedErrors = -1;
                if (needGagging != 0)
                {
                    oldGaggedErrors = global.startGagging();
                }
                {
                    int i = 0;
                    for (; (i < (tempinst.members.value.get()).length.value);i++){
                        Dsymbol s = (tempinst.members.value.get()).get(i);
                        semantic3(s, this.sc);
                        if (tempinst.gagged && (global.errors.value != olderrors))
                        {
                            break;
                        }
                    }
                }
                if ((global.errors.value != olderrors))
                {
                    if (!tempinst.errors.value)
                    {
                        if (!tempdecl.literal)
                        {
                            tempinst.error(tempinst.loc.value, new BytePtr("error instantiating"));
                        }
                        if (tempinst.tinst != null)
                        {
                            tempinst.tinst.printInstantiationTrace();
                        }
                    }
                    tempinst.errors.value = true;
                }
                if (needGagging != 0)
                {
                    global.endGagging(oldGaggedErrors);
                }
                this.sc = (this.sc.get()).pop();
                (this.sc.get()).pop();
            }
        }

        public  void visit(TemplateMixin tmix) {
            if ((tmix.semanticRun.value >= PASS.semantic3))
            {
                return ;
            }
            tmix.semanticRun.value = PASS.semantic3;
            if (tmix.members.value != null)
            {
                this.sc = (this.sc.get()).push(tmix.argsym);
                this.sc = (this.sc.get()).push(tmix);
                {
                    int i = 0;
                    for (; (i < (tmix.members.value.get()).length.value);i++){
                        Dsymbol s = (tmix.members.value.get()).get(i);
                        semantic3(s, this.sc);
                    }
                }
                this.sc = (this.sc.get()).pop();
                (this.sc.get()).pop();
            }
        }

        public  void visit(dmodule.Module mod) {
            if ((mod.semanticRun.value != PASS.semantic2done))
            {
                return ;
            }
            mod.semanticRun.value = PASS.semantic3;
            Ptr<Scope> sc = Scope.createGlobal(mod);
            {
                int i = 0;
                for (; (i < (mod.members.value.get()).length.value);i++){
                    Dsymbol s = (mod.members.value.get()).get(i);
                    semantic3(s, sc);
                    dmodule.Module.runDeferredSemantic2();
                }
            }
            if (mod.userAttribDecl != null)
            {
                semantic3(mod.userAttribDecl, sc);
            }
            sc = (sc.get()).pop();
            (sc.get()).pop();
            mod.semanticRun.value = PASS.semantic3done;
        }

        public  void visit(FuncDeclaration funcdecl) {
            Function0<Boolean> addReturn0 = new Function0<Boolean>(){
                public Boolean invoke() {
                    TypeFunction f = (TypeFunction)funcdecl.type.value;
                    return ((f.next.value.ty.value & 0xFF) == ENUMTY.Tvoid) && funcdecl.isMain() || global.params.betterC.value && funcdecl.isCMain();
                }
            };
            VarDeclaration _arguments = null;
            if (funcdecl.parent.value == null)
            {
                if (global.errors.value != 0)
                {
                    return ;
                }
                throw new AssertionError("Unreachable code!");
            }
            if (funcdecl.errors.value || isError(funcdecl.parent.value))
            {
                funcdecl.errors.value = true;
                return ;
            }
            if ((pequals(funcdecl.ident.value, Id.assign.value)) && (funcdecl.inuse.value == 0))
            {
                if ((funcdecl.storage_class.value & 70368744177664L) != 0)
                {
                    int oldErrors = global.startGagging();
                    funcdecl.inuse.value += 1;
                    semantic3(funcdecl, this.sc);
                    funcdecl.inuse.value -= 1;
                    if (global.endGagging(oldErrors))
                    {
                        funcdecl.storage_class.value |= 137438953472L;
                        funcdecl.fbody.value = null;
                        funcdecl.semantic3Errors.value = false;
                    }
                    return ;
                }
            }
            if ((funcdecl.semanticRun.value >= PASS.semantic3))
            {
                return ;
            }
            funcdecl.semanticRun.value = PASS.semantic3;
            funcdecl.semantic3Errors.value = false;
            if ((funcdecl.type.value == null) || ((funcdecl.type.value.ty.value & 0xFF) != ENUMTY.Tfunction))
            {
                return ;
            }
            TypeFunction f = (TypeFunction)funcdecl.type.value;
            if (!funcdecl.inferRetType && ((f.next.value.ty.value & 0xFF) == ENUMTY.Terror))
            {
                return ;
            }
            if ((funcdecl.fbody.value == null) && funcdecl.inferRetType && (f.next.value == null))
            {
                funcdecl.error(new BytePtr("has no function body with return type inference"));
                return ;
            }
            int oldErrors = global.errors.value;
            FuncDeclSem3 fds = fds = new FuncDeclSem3(funcdecl, this.sc);
            fds.checkInContractOverrides();
            boolean needEnsure = FuncDeclaration.needsFensure(funcdecl);
            if ((funcdecl.fbody.value != null) || (funcdecl.frequires != null) || needEnsure)
            {
                funcdecl.localsymtab = new DsymbolTable();
                ScopeDsymbol ss = new ScopeDsymbol(funcdecl.loc.value, null);
                {
                    Ptr<Scope> scx = this.sc;
                    for (; ;scx = (scx.get()).enclosing.value){
                        if ((scx.get()).scopesym.value != null)
                        {
                            ss.parent.value = (scx.get()).scopesym.value;
                            break;
                        }
                    }
                }
                ss.endlinnum = funcdecl.endloc.value.linnum;
                Ptr<Scope> sc2 = (this.sc.get()).push(ss);
                (sc2.get()).func.value = funcdecl;
                (sc2.get()).parent.value = funcdecl;
                (sc2.get()).ctorflow.callSuper.value = CSX.none;
                (sc2.get()).sbreak = null;
                (sc2.get()).scontinue = null;
                (sc2.get()).sw = null;
                (sc2.get()).fes = funcdecl.fes.value;
                (sc2.get()).linkage.value = LINK.d;
                (sc2.get()).stc.value &= -17660607202720L;
                (sc2.get()).protection.value = new Prot(Prot.Kind.public_).copy();
                (sc2.get()).explicitProtection = 0;
                (sc2.get()).aligndecl = null;
                if ((!pequals(funcdecl.ident.value, Id.require)) && (!pequals(funcdecl.ident.value, Id.ensure)))
                {
                    (sc2.get()).flags.value = (this.sc.get()).flags.value & -97;
                }
                (sc2.get()).flags.value &= -257;
                (sc2.get()).tf = null;
                (sc2.get()).os = null;
                (sc2.get()).inLoop = false;
                (sc2.get()).userAttribDecl = null;
                if (((sc2.get()).intypeof.value == 1))
                {
                    (sc2.get()).intypeof.value = 2;
                }
                (sc2.get()).ctorflow.fieldinit = new Slice<FieldInit>().copy();
                {
                    FuncLiteralDeclaration fld = funcdecl.isFuncLiteralDeclaration();
                    if ((fld) != null)
                    {
                        {
                            AggregateDeclaration ad = funcdecl.isMember2();
                            if ((ad) != null)
                            {
                                if ((this.sc.get()).intypeof.value == 0)
                                {
                                    if (((fld.tok.value & 0xFF) == 160))
                                    {
                                        funcdecl.error(new BytePtr("cannot be %s members"), ad.kind());
                                    }
                                    else
                                    {
                                        fld.tok.value = TOK.function_;
                                    }
                                }
                                else
                                {
                                    if (((fld.tok.value & 0xFF) != 161))
                                    {
                                        fld.tok.value = TOK.delegate_;
                                    }
                                }
                            }
                        }
                    }
                }
                AggregateDeclaration ad = funcdecl.isThis();
                FuncDeclaration.HiddenParameters hiddenParams = funcdecl.declareThis(sc2, ad).copy();
                funcdecl.vthis.value = hiddenParams.vthis;
                funcdecl.isThis2.value = hiddenParams.isThis2;
                funcdecl.selectorParameter = hiddenParams.selectorParameter;
                if ((f.parameterList.varargs.value == VarArg.variadic))
                {
                    if ((f.linkage.value == LINK.d))
                    {
                        if (!global.params.useTypeInfo.value || (Type.dtypeinfo.value == null) || (Type.typeinfotypelist == null))
                        {
                            if (!global.params.useTypeInfo.value)
                            {
                                funcdecl.error(new BytePtr("D-style variadic functions cannot be used with -betterC"));
                            }
                            else if (Type.typeinfotypelist == null)
                            {
                                funcdecl.error(new BytePtr("`object.TypeInfo_Tuple` could not be found, but is implicitly used in D-style variadic functions"));
                            }
                            else
                            {
                                funcdecl.error(new BytePtr("`object.TypeInfo` could not be found, but is implicitly used in D-style variadic functions"));
                            }
                            fatal();
                        }
                        funcdecl.v_arguments = new VarDeclaration(funcdecl.loc.value, Type.typeinfotypelist.type.value, Id._arguments_typeinfo, null, 0L);
                        funcdecl.v_arguments.storage_class.value |= 1099511627808L;
                        dsymbolSemantic(funcdecl.v_arguments, sc2);
                        (sc2.get()).insert(funcdecl.v_arguments);
                        funcdecl.v_arguments.parent.value = funcdecl;
                        Type t = Type.dtypeinfo.value.type.value.arrayOf();
                        _arguments = new VarDeclaration(funcdecl.loc.value, t, Id._arguments, null, 0L);
                        _arguments.storage_class.value |= 1099511627776L;
                        dsymbolSemantic(_arguments, sc2);
                        (sc2.get()).insert(_arguments);
                        _arguments.parent.value = funcdecl;
                    }
                    if ((f.linkage.value == LINK.d) || (f.parameterList.length() != 0))
                    {
                        Type t = Type.tvalist;
                        funcdecl.v_argptr = new VarDeclaration(funcdecl.loc.value, t, Id._argptr, new VoidInitializer(funcdecl.loc.value), 0L);
                        funcdecl.v_argptr.storage_class.value |= 1099511627776L;
                        dsymbolSemantic(funcdecl.v_argptr, sc2);
                        (sc2.get()).insert(funcdecl.v_argptr);
                        funcdecl.v_argptr.parent.value = funcdecl;
                    }
                }
                int nparams = f.parameterList.length();
                if (nparams != 0)
                {
                    funcdecl.parameters.value = refPtr(new DArray<VarDeclaration>());
                    (funcdecl.parameters.value.get()).reserve(nparams);
                    {
                        int i = 0;
                        for (; (i < nparams);i++){
                            Parameter fparam = f.parameterList.get(i);
                            Identifier id = fparam.ident.value;
                            long stc = 0L;
                            if (id == null)
                            {
                                fparam.ident.value = (id = Identifier.generateId(new BytePtr("_param_"), i));
                                stc |= 1099511627776L;
                            }
                            Type vtype = fparam.type.value;
                            VarDeclaration v = new VarDeclaration(funcdecl.loc.value, vtype, id, null, 0L);
                            stc |= 32L;
                            if ((f.parameterList.varargs.value == VarArg.typesafe) && (i + 1 == nparams))
                            {
                                stc |= 65536L;
                                Type vtypeb = vtype.toBasetype();
                                if (((vtypeb.ty.value & 0xFF) == ENUMTY.Tarray))
                                {
                                    stc |= 524288L;
                                }
                            }
                            if (((funcdecl.flags & FUNCFLAG.inferScope) != 0) && ((fparam.storageClass.value & 524288L) == 0))
                            {
                                stc |= 281474976710656L;
                            }
                            stc |= fparam.storageClass.value & 17594890860556L;
                            v.storage_class.value = stc;
                            dsymbolSemantic(v, sc2);
                            if ((sc2.get()).insert(v) == null)
                            {
                                funcdecl.error(new BytePtr("parameter `%s.%s` is already defined"), funcdecl.toChars(), v.toChars());
                                funcdecl.errors.value = true;
                            }
                            else
                            {
                                (funcdecl.parameters.value.get()).push(v);
                            }
                            funcdecl.localsymtab.insert((Dsymbol)v);
                            v.parent.value = funcdecl;
                            if (fparam.userAttribDecl.value != null)
                            {
                                v.userAttribDecl = fparam.userAttribDecl.value;
                            }
                        }
                    }
                }
                if (f.parameterList.parameters.value != null)
                {
                    {
                        int i = 0;
                        for (; (i < (f.parameterList.parameters.value.get()).length.value);i++){
                            Parameter fparam = (f.parameterList.parameters.value.get()).get(i);
                            if (fparam.ident.value == null)
                            {
                                continue;
                            }
                            if (((fparam.type.value.ty.value & 0xFF) == ENUMTY.Ttuple))
                            {
                                TypeTuple t = (TypeTuple)fparam.type.value;
                                int dim = Parameter.dim(t.arguments.value);
                                Ptr<DArray<RootObject>> exps = refPtr(new DArray<RootObject>(dim));
                                {
                                    int j = 0;
                                    for (; (j < dim);j++){
                                        Parameter narg = Parameter.getNth(t.arguments.value, j, null);
                                        assert(narg.ident.value != null);
                                        VarDeclaration v = (sc2.get()).search(Loc.initial.value, narg.ident.value, null, 0).isVarDeclaration();
                                        assert(v != null);
                                        Expression e = new VarExp(v.loc.value, v, true);
                                        exps.get().set(j, e);
                                    }
                                }
                                assert(fparam.ident.value != null);
                                TupleDeclaration v = new TupleDeclaration(funcdecl.loc.value, fparam.ident.value, exps);
                                v.isexp = true;
                                if ((sc2.get()).insert(v) == null)
                                {
                                    funcdecl.error(new BytePtr("parameter `%s.%s` is already defined"), funcdecl.toChars(), v.toChars());
                                }
                                funcdecl.localsymtab.insert((Dsymbol)v);
                                v.parent.value = funcdecl;
                            }
                        }
                    }
                }
                Statement fpreinv = null;
                if (funcdecl.addPreInvariant())
                {
                    Expression e = addInvariant(funcdecl.loc.value, this.sc, ad, funcdecl.vthis.value);
                    if (e != null)
                    {
                        fpreinv = new ExpStatement(Loc.initial.value, e);
                    }
                }
                Statement fpostinv = null;
                if (funcdecl.addPostInvariant())
                {
                    Expression e = addInvariant(funcdecl.loc.value, this.sc, ad, funcdecl.vthis.value);
                    if (e != null)
                    {
                        fpostinv = new ExpStatement(Loc.initial.value, e);
                    }
                }
                if (funcdecl.fbody.value == null)
                {
                    funcdecl.buildEnsureRequire();
                }
                Ptr<Scope> scout = null;
                if (needEnsure || funcdecl.addPostInvariant())
                {
                    int fensure_endlin = funcdecl.endloc.value.linnum;
                    if (funcdecl.fensure != null)
                    {
                        {
                            ScopeStatement s = funcdecl.fensure.isScopeStatement();
                            if ((s) != null)
                            {
                                fensure_endlin = s.endloc.linnum;
                            }
                        }
                    }
                    if (needEnsure && ((global.params.useOut & 0xFF) == 2) || (fpostinv != null))
                    {
                        funcdecl.returnLabel = new LabelDsymbol(Id.returnLabel);
                    }
                    ScopeDsymbol sym = new ScopeDsymbol(funcdecl.loc.value, null);
                    sym.parent.value = (sc2.get()).scopesym.value;
                    sym.endlinnum = fensure_endlin;
                    scout = (sc2.get()).push(sym);
                }
                if (funcdecl.fbody.value != null)
                {
                    ScopeDsymbol sym = new ScopeDsymbol(funcdecl.loc.value, null);
                    sym.parent.value = (sc2.get()).scopesym.value;
                    sym.endlinnum = funcdecl.endloc.value.linnum;
                    sc2 = (sc2.get()).push(sym);
                    AggregateDeclaration ad2 = funcdecl.isMemberLocal();
                    if ((ad2 != null) && (funcdecl.isCtorDeclaration() != null))
                    {
                        (sc2.get()).ctorflow.allocFieldinit(ad2.fields.length.value);
                        {
                            Slice<VarDeclaration> __r1547 = ad2.fields.opSlice().copy();
                            int __key1548 = 0;
                            for (; (__key1548 < __r1547.getLength());__key1548 += 1) {
                                VarDeclaration v = __r1547.get(__key1548);
                                v.ctorinit = false;
                            }
                        }
                    }
                    if (!funcdecl.inferRetType && !target.isReturnOnStack(f, funcdecl.needThis()))
                    {
                        funcdecl.nrvo_can = false;
                    }
                    boolean inferRef = f.isref.value && ((funcdecl.storage_class.value & 256L) != 0);
                    funcdecl.fbody.value = statementSemantic(funcdecl.fbody.value, sc2);
                    if (funcdecl.fbody.value == null)
                    {
                        funcdecl.fbody.value = new CompoundStatement(Loc.initial.value, refPtr(new DArray<Statement>()));
                    }
                    if (funcdecl.naked)
                    {
                        fpreinv = null;
                        fpostinv = null;
                    }
                    assert((pequals(funcdecl.type.value, f)) || ((funcdecl.type.value.ty.value & 0xFF) == ENUMTY.Tfunction) && (f.purity.value == PURE.impure) && (((TypeFunction)funcdecl.type.value).purity.value >= PURE.fwdref));
                    f = (TypeFunction)funcdecl.type.value;
                    if (funcdecl.inferRetType)
                    {
                        if (f.next.value == null)
                        {
                            f.next.value = Type.tvoid.value;
                        }
                        if (f.checkRetType(funcdecl.loc.value))
                        {
                            funcdecl.fbody.value = new ErrorStatement();
                        }
                    }
                    if (global.params.vcomplex && (f.next.value != null))
                    {
                        f.next.value.checkComplexTransition(funcdecl.loc.value, this.sc);
                    }
                    if ((funcdecl.returns != null) && (funcdecl.fbody.value.isErrorStatement() == null))
                    {
                        {
                            int i = 0;
                            for (; (i < (funcdecl.returns.get()).length);){
                                Expression exp = (funcdecl.returns.get()).get(i).exp.value;
                                if (((exp.op.value & 0xFF) == 26) && (pequals(((VarExp)exp).var.value, funcdecl.vresult)))
                                {
                                    if (addReturn0.invoke())
                                    {
                                        exp.type.value = Type.tint32.value;
                                    }
                                    else
                                    {
                                        exp.type.value = f.next.value;
                                    }
                                    (funcdecl.returns.get()).remove(i);
                                    continue;
                                }
                                if (inferRef && f.isref.value && (exp.type.value.constConv(f.next.value) == 0))
                                {
                                    f.isref.value = false;
                                }
                                i++;
                            }
                        }
                    }
                    if (f.isref.value)
                    {
                        if ((funcdecl.storage_class.value & 256L) != 0)
                        {
                            funcdecl.storage_class.value &= -257L;
                        }
                    }
                    if (!target.isReturnOnStack(f, funcdecl.needThis()))
                    {
                        funcdecl.nrvo_can = false;
                    }
                    if (funcdecl.fbody.value.isErrorStatement() != null)
                    {
                    }
                    else if (funcdecl.isStaticCtorDeclaration() != null)
                    {
                        ScopeDsymbol pd = funcdecl.toParent().isScopeDsymbol();
                        {
                            int i = 0;
                            for (; (i < (pd.members.value.get()).length.value);i++){
                                Dsymbol s = (pd.members.value.get()).get(i);
                                s.checkCtorConstInit();
                            }
                        }
                    }
                    else if ((ad2 != null) && (funcdecl.isCtorDeclaration() != null))
                    {
                        ClassDeclaration cd = ad2.isClassDeclaration();
                        if (((sc2.get()).ctorflow.callSuper.value & 1) == 0)
                        {
                            {
                                Slice<VarDeclaration> __r1550 = ad2.fields.opSlice().copy();
                                int __key1549 = 0;
                                for (; (__key1549 < __r1550.getLength());__key1549 += 1) {
                                    VarDeclaration v = __r1550.get(__key1549);
                                    int i = __key1549;
                                    if (v.isThisDeclaration() != null)
                                    {
                                        continue;
                                    }
                                    if (((v.ctorinit ? 1 : 0) == 0))
                                    {
                                        if (v.isCtorinit() && !v.type.value.isMutable() && (cd != null))
                                        {
                                            funcdecl.error(new BytePtr("missing initializer for %s field `%s`"), MODtoChars(v.type.value.mod.value), v.toChars());
                                        }
                                        else if ((v.storage_class.value & 549755813888L) != 0)
                                        {
                                            error(funcdecl.loc.value, new BytePtr("field `%s` must be initialized in constructor"), v.toChars());
                                        }
                                        else if (v.type.value.needsNested())
                                        {
                                            error(funcdecl.loc.value, new BytePtr("field `%s` must be initialized in constructor, because it is nested struct"), v.toChars());
                                        }
                                    }
                                    else
                                    {
                                        boolean mustInit = ((v.storage_class.value & 549755813888L) != 0) || v.type.value.needsNested();
                                        if (mustInit && (((sc2.get()).ctorflow.fieldinit.get(i).csx.value & 1) == 0))
                                        {
                                            funcdecl.error(new BytePtr("field `%s` must be initialized but skipped"), v.toChars());
                                        }
                                    }
                                }
                            }
                        }
                        (sc2.get()).ctorflow.freeFieldinit();
                        if ((cd != null) && (((sc2.get()).ctorflow.callSuper.value & 16) == 0) && (cd.baseClass.value != null) && (cd.baseClass.value.ctor.value != null))
                        {
                            (sc2.get()).ctorflow.callSuper.value = CSX.none;
                            Type tthis = ad2.type.value.addMod(funcdecl.vthis.value.type.value.mod.value);
                            FuncDeclaration fd = resolveFuncCall(Loc.initial.value, sc2, cd.baseClass.value.ctor.value, null, tthis, null, FuncResolveFlag.quiet);
                            if (fd == null)
                            {
                                funcdecl.error(new BytePtr("no match for implicit `super()` call in constructor"));
                            }
                            else if ((fd.storage_class.value & 137438953472L) != 0)
                            {
                                funcdecl.error(new BytePtr("cannot call `super()` implicitly because it is annotated with `@disable`"));
                            }
                            else
                            {
                                Expression e1 = new SuperExp(Loc.initial.value);
                                Expression e = new CallExp(Loc.initial.value, e1);
                                e = expressionSemantic(e, sc2);
                                Statement s = new ExpStatement(Loc.initial.value, e);
                                funcdecl.fbody.value = new CompoundStatement(Loc.initial.value, slice(new Statement[]{s, funcdecl.fbody.value}));
                            }
                        }
                    }
                    funcdecl.buildEnsureRequire();
                    int blockexit = blockExit(funcdecl.fbody.value, funcdecl, f.isnothrow.value);
                    if (f.isnothrow.value && ((blockexit & 2) != 0))
                    {
                        error(funcdecl.loc.value, new BytePtr("`nothrow` %s `%s` may throw"), funcdecl.kind(), funcdecl.toPrettyChars(false));
                    }
                    if (!(((blockexit & 18) != 0) || ((funcdecl.flags & FUNCFLAG.hasCatches) != 0)))
                    {
                        funcdecl.eh_none = true;
                    }
                    if ((funcdecl.flags & FUNCFLAG.nothrowInprocess) != 0)
                    {
                        if ((pequals(funcdecl.type.value, f)))
                        {
                            f = (TypeFunction)f.copy();
                        }
                        f.isnothrow.value = (blockexit & 2) == 0;
                    }
                    if (funcdecl.fbody.value.isErrorStatement() != null)
                    {
                    }
                    else if ((ad2 != null) && (funcdecl.isCtorDeclaration() != null))
                    {
                        if ((blockexit & 1) != 0)
                        {
                            Statement s = new ReturnStatement(funcdecl.loc.value, null);
                            s = statementSemantic(s, sc2);
                            funcdecl.fbody.value = new CompoundStatement(funcdecl.loc.value, slice(new Statement[]{funcdecl.fbody.value, s}));
                            funcdecl.hasReturnExp |= (funcdecl.hasReturnExp & 1) != 0 ? 16 : 1;
                        }
                    }
                    else if (funcdecl.fes.value != null)
                    {
                        if ((blockexit & 1) != 0)
                        {
                            Expression e = literal_B6589FC6AB0DC82C();
                            Statement s = new ReturnStatement(Loc.initial.value, e);
                            funcdecl.fbody.value = new CompoundStatement(Loc.initial.value, slice(new Statement[]{funcdecl.fbody.value, s}));
                            funcdecl.hasReturnExp |= (funcdecl.hasReturnExp & 1) != 0 ? 16 : 1;
                        }
                        assert(funcdecl.returnLabel == null);
                    }
                    else
                    {
                        boolean inlineAsm = (funcdecl.hasReturnExp & 8) != 0;
                        if (((blockexit & 1) != 0) && ((f.next.value.ty.value & 0xFF) != ENUMTY.Tvoid) && !inlineAsm)
                        {
                            Expression e = null;
                            if (funcdecl.hasReturnExp == 0)
                            {
                                funcdecl.error(new BytePtr("has no `return` statement, but is expected to return a value of type `%s`"), f.next.value.toChars());
                            }
                            else
                            {
                                funcdecl.error(new BytePtr("no `return exp;` or `assert(0);` at end of function"));
                            }
                            if (((global.params.useAssert & 0xFF) == 2) && !global.params.useInline)
                            {
                                e = new AssertExp(funcdecl.endloc.value, literal_B6589FC6AB0DC82C(), new StringExp(funcdecl.loc.value, new BytePtr("missing return expression")));
                            }
                            else
                            {
                                e = new HaltExp(funcdecl.endloc.value);
                            }
                            e = new CommaExp(Loc.initial.value, e, defaultInit(f.next.value, Loc.initial.value), true);
                            e = expressionSemantic(e, sc2);
                            Statement s = new ExpStatement(Loc.initial.value, e);
                            funcdecl.fbody.value = new CompoundStatement(Loc.initial.value, slice(new Statement[]{funcdecl.fbody.value, s}));
                        }
                    }
                    if (funcdecl.returns != null)
                    {
                        boolean implicit0 = addReturn0.invoke();
                        Type tret = implicit0 ? Type.tint32.value : f.next.value;
                        assert(((tret.ty.value & 0xFF) != ENUMTY.Tvoid));
                        if ((funcdecl.vresult != null) || (funcdecl.returnLabel != null))
                        {
                            funcdecl.buildResultVar(scout != null ? scout : sc2, tret);
                        }
                        {
                            int i = 0;
                            for (; (i < (funcdecl.returns.get()).length);i++){
                                ReturnStatement rs = (funcdecl.returns.get()).get(i);
                                Expression exp = rs.exp.value;
                                if (((exp.op.value & 0xFF) == 127))
                                {
                                    continue;
                                }
                                if (((tret.ty.value & 0xFF) == ENUMTY.Terror))
                                {
                                    exp = checkGC(sc2, exp);
                                    continue;
                                }
                                if ((exp.implicitConvTo(tret) == 0) && funcdecl.isTypeIsolated(exp.type.value))
                                {
                                    if (exp.type.value.immutableOf().implicitConvTo(tret) != 0)
                                    {
                                        exp = exp.castTo(sc2, exp.type.value.immutableOf());
                                    }
                                    else if (exp.type.value.wildOf().implicitConvTo(tret) != 0)
                                    {
                                        exp = exp.castTo(sc2, exp.type.value.wildOf());
                                    }
                                }
                                boolean hasCopyCtor = ((exp.type.value.ty.value & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)exp.type.value).sym.value.hasCopyCtor;
                                if (!hasCopyCtor)
                                {
                                    if (f.isref.value && !MODimplicitConv(exp.type.value.mod.value, tret.mod.value) && (tret.isTypeSArray() == null))
                                    {
                                        error(exp.loc.value, new BytePtr("expression `%s` of type `%s` is not implicitly convertible to return type `ref %s`"), exp.toChars(), exp.type.value.toChars(), tret.toChars());
                                    }
                                    else
                                    {
                                        exp = exp.implicitCastTo(sc2, tret);
                                    }
                                }
                                if (f.isref.value)
                                {
                                    exp = exp.toLvalue(sc2, exp);
                                    checkReturnEscapeRef(sc2, exp, false);
                                }
                                else
                                {
                                    exp = exp.optimize(0, false);
                                    if (!funcdecl.nrvo_can)
                                    {
                                        exp = doCopyOrMove(sc2, exp, f.next.value);
                                    }
                                    if (tret.hasPointers())
                                    {
                                        checkReturnEscape(sc2, exp, false);
                                    }
                                }
                                exp = checkGC(sc2, exp);
                                if (funcdecl.vresult != null)
                                {
                                    exp = new BlitExp(rs.loc, funcdecl.vresult, exp);
                                    exp.type.value = funcdecl.vresult.type.value;
                                    if (rs.caseDim != 0)
                                    {
                                        exp = Expression.combine(exp, new IntegerExp((long)rs.caseDim));
                                    }
                                }
                                else if ((funcdecl.tintro.value != null) && !tret.equals(funcdecl.tintro.value.nextOf()))
                                {
                                    exp = exp.implicitCastTo(sc2, funcdecl.tintro.value.nextOf());
                                }
                                rs.exp.value = exp;
                            }
                        }
                    }
                    if ((funcdecl.nrvo_var != null) || (funcdecl.returnLabel != null))
                    {
                        NrvoWalker nw = new NrvoWalker();
                        nw.fd = funcdecl;
                        nw.sc = sc2;
                        nw.visitStmt(funcdecl.fbody.value);
                    }
                    sc2 = (sc2.get()).pop();
                }
                funcdecl.frequire = funcdecl.mergeFrequire(funcdecl.frequire, funcdecl.fdrequireParams);
                funcdecl.fensure = funcdecl.mergeFensure(funcdecl.fensure, Id.result, funcdecl.fdensureParams);
                Statement freq = funcdecl.frequire;
                Statement fens = funcdecl.fensure;
                if (freq != null)
                {
                    ScopeDsymbol sym = new ScopeDsymbol(funcdecl.loc.value, null);
                    sym.parent.value = (sc2.get()).scopesym.value;
                    sym.endlinnum = funcdecl.endloc.value.linnum;
                    sc2 = (sc2.get()).push(sym);
                    (sc2.get()).flags.value = (sc2.get()).flags.value & -97 | 64;
                    freq = statementSemantic(freq, sc2);
                    blockExit(freq, funcdecl, false);
                    funcdecl.eh_none = false;
                    sc2 = (sc2.get()).pop();
                    if (((global.params.useIn & 0xFF) == 1))
                    {
                        freq = null;
                    }
                }
                if (fens != null)
                {
                    if (((f.next.value.ty.value & 0xFF) == ENUMTY.Tvoid) && (funcdecl.fensures != null))
                    {
                        {
                            Slice<Ensure> __r1551 = (funcdecl.fensures.get()).opSlice().copy();
                            int __key1552 = 0;
                            for (; (__key1552 < __r1551.getLength());__key1552 += 1) {
                                Ensure e = __r1551.get(__key1552).copy();
                                if (e.id != null)
                                {
                                    funcdecl.error(e.ensure.loc, new BytePtr("`void` functions have no result"));
                                }
                            }
                        }
                    }
                    sc2 = scout;
                    (sc2.get()).flags.value = (sc2.get()).flags.value & -97 | 96;
                    if ((funcdecl.fensure != null) && ((f.next.value.ty.value & 0xFF) != ENUMTY.Tvoid))
                    {
                        funcdecl.buildResultVar(scout, f.next.value);
                    }
                    fens = statementSemantic(fens, sc2);
                    blockExit(fens, funcdecl, false);
                    funcdecl.eh_none = false;
                    sc2 = (sc2.get()).pop();
                    if (((global.params.useOut & 0xFF) == 1))
                    {
                        fens = null;
                    }
                }
                if ((funcdecl.fbody.value != null) && (funcdecl.fbody.value.isErrorStatement() != null))
                {
                }
                else
                {
                    Ptr<DArray<Statement>> a = refPtr(new DArray<Statement>());
                    if (funcdecl.parameters.value != null)
                    {
                        {
                            int i = 0;
                            for (; (i < (funcdecl.parameters.value.get()).length.value);i++){
                                VarDeclaration v = (funcdecl.parameters.value.get()).get(i);
                                if ((v.storage_class.value & 4096L) != 0)
                                {
                                    if (v._init.value == null)
                                    {
                                        v.error(new BytePtr("Zero-length `out` parameters are not allowed."));
                                        return ;
                                    }
                                    ExpInitializer ie = v._init.value.isExpInitializer();
                                    assert(ie != null);
                                    {
                                        ConstructExp iec = ie.exp.value.isConstructExp();
                                        if ((iec) != null)
                                        {
                                            AssignExp ec = new AssignExp(iec.loc.value, iec.e1.value, iec.e2.value);
                                            ec.type.value = iec.type.value;
                                            ie.exp.value = ec;
                                        }
                                    }
                                    (a.get()).push(new ExpStatement(Loc.initial.value, ie.exp.value));
                                }
                            }
                        }
                    }
                    if (_arguments != null)
                    {
                        Expression e = new VarExp(Loc.initial.value, funcdecl.v_arguments, true);
                        e = new DotIdExp(Loc.initial.value, e, Id.elements);
                        e = new ConstructExp(Loc.initial.value, _arguments, e);
                        e = expressionSemantic(e, sc2);
                        _arguments._init.value = new ExpInitializer(Loc.initial.value, e);
                        DeclarationExp de = new DeclarationExp(Loc.initial.value, _arguments);
                        (a.get()).push(new ExpStatement(Loc.initial.value, de));
                    }
                    if ((freq != null) || (fpreinv != null))
                    {
                        if (freq == null)
                        {
                            freq = fpreinv;
                        }
                        else if (fpreinv != null)
                        {
                            freq = new CompoundStatement(Loc.initial.value, slice(new Statement[]{freq, fpreinv}));
                        }
                        (a.get()).push(freq);
                    }
                    if (funcdecl.fbody.value != null)
                    {
                        (a.get()).push(funcdecl.fbody.value);
                    }
                    if ((fens != null) || (fpostinv != null))
                    {
                        if (fens == null)
                        {
                            fens = fpostinv;
                        }
                        else if (fpostinv != null)
                        {
                            fens = new CompoundStatement(Loc.initial.value, slice(new Statement[]{fpostinv, fens}));
                        }
                        LabelStatement ls = new LabelStatement(Loc.initial.value, Id.returnLabel, fens);
                        funcdecl.returnLabel.statement = ls;
                        (a.get()).push(funcdecl.returnLabel.statement);
                        if (((f.next.value.ty.value & 0xFF) != ENUMTY.Tvoid) && (funcdecl.vresult != null))
                        {
                            Expression e = new VarExp(Loc.initial.value, funcdecl.vresult, true);
                            if (funcdecl.tintro.value != null)
                            {
                                e = e.implicitCastTo(this.sc, funcdecl.tintro.value.nextOf());
                                e = expressionSemantic(e, this.sc);
                            }
                            ReturnStatement s = new ReturnStatement(Loc.initial.value, e);
                            (a.get()).push(s);
                        }
                    }
                    if (addReturn0.invoke())
                    {
                        Statement s = new ReturnStatement(Loc.initial.value, literal_B6589FC6AB0DC82C());
                        (a.get()).push(s);
                    }
                    Statement sbody = new CompoundStatement(Loc.initial.value, a);
                    if (funcdecl.parameters.value != null)
                    {
                        {
                            Slice<VarDeclaration> __r1553 = (funcdecl.parameters.value.get()).opSlice().copy();
                            int __key1554 = 0;
                            for (; (__key1554 < __r1553.getLength());__key1554 += 1) {
                                VarDeclaration v = __r1553.get(__key1554);
                                if ((v.storage_class.value & 2109440L) != 0)
                                {
                                    continue;
                                }
                                if (v.needsScopeDtor())
                                {
                                    Statement s = new DtorExpStatement(Loc.initial.value, v.edtor.value, v);
                                    v.storage_class.value |= 16777216L;
                                    s = statementSemantic(s, sc2);
                                    boolean isnothrow = f.isnothrow.value & (funcdecl.flags & FUNCFLAG.nothrowInprocess) == 0;
                                    int blockexit = blockExit(s, funcdecl, isnothrow);
                                    if ((blockexit & 2) != 0)
                                    {
                                        funcdecl.eh_none = false;
                                    }
                                    if (f.isnothrow.value && isnothrow && ((blockexit & 2) != 0))
                                    {
                                        error(funcdecl.loc.value, new BytePtr("`nothrow` %s `%s` may throw"), funcdecl.kind(), funcdecl.toPrettyChars(false));
                                    }
                                    if (((funcdecl.flags & FUNCFLAG.nothrowInprocess) != 0) && ((blockexit & 2) != 0))
                                    {
                                        f.isnothrow.value = false;
                                    }
                                    if ((blockExit(sbody, funcdecl, f.isnothrow.value) == BE.fallthru))
                                    {
                                        sbody = new CompoundStatement(Loc.initial.value, slice(new Statement[]{sbody, s}));
                                    }
                                    else
                                    {
                                        sbody = new TryFinallyStatement(Loc.initial.value, sbody, s);
                                    }
                                }
                            }
                        }
                    }
                    funcdecl.flags &= -5;
                    if (funcdecl.isSynchronized())
                    {
                        ClassDeclaration cd = funcdecl.toParentDecl().isClassDeclaration();
                        if (cd != null)
                        {
                            if (!global.params.is64bit && global.params.isWindows && !funcdecl.isStatic() && !sbody.usesEH() && !global.params.trace)
                            {
                            }
                            else
                            {
                                Expression vsync = null;
                                if (funcdecl.isStatic())
                                {
                                    vsync = new DotIdExp(funcdecl.loc.value, symbolToExp(cd, funcdecl.loc.value, sc2, false), Id.classinfo.value);
                                }
                                else
                                {
                                    vsync = new VarExp(funcdecl.loc.value, funcdecl.vthis.value, true);
                                    if (funcdecl.isThis2.value)
                                    {
                                        vsync = new PtrExp(funcdecl.loc.value, vsync);
                                        vsync = new IndexExp(funcdecl.loc.value, vsync, literal_B6589FC6AB0DC82C());
                                    }
                                }
                                sbody = new PeelStatement(sbody);
                                sbody = new SynchronizedStatement(funcdecl.loc.value, vsync, sbody);
                                sbody = statementSemantic(sbody, sc2);
                            }
                        }
                        else
                        {
                            funcdecl.error(new BytePtr("synchronized function `%s` must be a member of a class"), funcdecl.toChars());
                        }
                    }
                    if ((funcdecl.fbody.value != null) || allowsContractWithoutBody(funcdecl))
                    {
                        funcdecl.fbody.value = sbody;
                    }
                }
                if (funcdecl.gotos != null)
                {
                    {
                        int i = 0;
                        for (; (i < (funcdecl.gotos.get()).length);i += 1){
                            (funcdecl.gotos.get()).get(i).checkLabel();
                        }
                    }
                }
                if (funcdecl.naked && (funcdecl.fensures != null) || (funcdecl.frequires != null))
                {
                    funcdecl.error(new BytePtr("naked assembly functions with contracts are not supported"));
                }
                (sc2.get()).ctorflow.callSuper.value = CSX.none;
                (sc2.get()).pop();
            }
            if (funcdecl.checkClosure())
            {
            }
            if ((funcdecl.flags & FUNCFLAG.purityInprocess) != 0)
            {
                funcdecl.flags &= -2;
                if ((pequals(funcdecl.type.value, f)))
                {
                    f = (TypeFunction)f.copy();
                }
                f.purity.value = PURE.fwdref;
            }
            if ((funcdecl.flags & FUNCFLAG.safetyInprocess) != 0)
            {
                funcdecl.flags &= -3;
                if ((pequals(funcdecl.type.value, f)))
                {
                    f = (TypeFunction)f.copy();
                }
                f.trust.value = TRUST.safe;
            }
            if ((funcdecl.flags & FUNCFLAG.nogcInprocess) != 0)
            {
                funcdecl.flags &= -9;
                if ((pequals(funcdecl.type.value, f)))
                {
                    f = (TypeFunction)f.copy();
                }
                f.isnogc.value = true;
            }
            if ((funcdecl.flags & FUNCFLAG.returnInprocess) != 0)
            {
                funcdecl.flags &= -17;
                if ((funcdecl.storage_class.value & 17592186044416L) != 0)
                {
                    if ((pequals(funcdecl.type.value, f)))
                    {
                        f = (TypeFunction)f.copy();
                    }
                    f.isreturn.value = true;
                    if ((funcdecl.storage_class.value & 4503599627370496L) != 0)
                    {
                        f.isreturninferred.value = true;
                    }
                }
            }
            funcdecl.flags &= -65;
            {
                Slice<VarDeclaration> array = null;
                Slice<VarDeclaration> tmp = new Slice<VarDeclaration>(new VarDeclaration[10]);
                int dim = ((funcdecl.vthis.value != null) ? 1 : 0) + (funcdecl.parameters.value != null ? (funcdecl.parameters.value.get()).length.value : 0);
                if ((dim <= 10))
                {
                    array = tmp.slice(0,dim).copy();
                }
                else
                {
                    Ptr<VarDeclaration> ptr = pcopy(((Ptr<VarDeclaration>)Mem.xmalloc(dim * 4)));
                    array = ptr.slice(0,dim).copy();
                }
                int n = 0;
                if (funcdecl.vthis.value != null)
                {
                    array.set(n++, funcdecl.vthis.value);
                }
                if (funcdecl.parameters.value != null)
                {
                    {
                        Slice<VarDeclaration> __r1555 = (funcdecl.parameters.value.get()).opSlice().copy();
                        int __key1556 = 0;
                        for (; (__key1556 < __r1555.getLength());__key1556 += 1) {
                            VarDeclaration v = __r1555.get(__key1556);
                            array.set(n++, v);
                        }
                    }
                }
                eliminateMaybeScopes(array.slice(0,n));
                if ((dim > 10))
                {
                    Mem.xfree(toPtr<VarDeclaration>(array));
                }
            }
            if ((funcdecl.parameters.value != null) && !funcdecl.errors.value)
            {
                int nfparams = f.parameterList.length();
                assert((nfparams == (funcdecl.parameters.value.get()).length.value));
                {
                    Slice<VarDeclaration> __r1558 = (funcdecl.parameters.value.get()).opSlice().copy();
                    int __key1557 = 0;
                    for (; (__key1557 < __r1558.getLength());__key1557 += 1) {
                        VarDeclaration v = __r1558.get(__key1557);
                        int u = __key1557;
                        if ((v.storage_class.value & 281474976710656L) != 0)
                        {
                            Parameter p = f.parameterList.get(u);
                            notMaybeScope(v);
                            v.storage_class.value |= 562949953945600L;
                            p.storageClass.value |= 562949953945600L;
                            assert((p.storageClass.value & 281474976710656L) == 0);
                        }
                    }
                }
            }
            if ((funcdecl.vthis.value != null) && ((funcdecl.vthis.value.storage_class.value & 281474976710656L) != 0))
            {
                notMaybeScope(funcdecl.vthis.value);
                funcdecl.vthis.value.storage_class.value |= 562949953945600L;
                f.isscope.value = true;
                f.isscopeinferred.value = true;
            }
            if ((!pequals(f, funcdecl.type.value)))
            {
                f.deco.value = null;
            }
            if ((f.deco.value == null) && (!pequals(funcdecl.ident.value, Id.xopEquals)) && (!pequals(funcdecl.ident.value, Id.xopCmp)))
            {
                this.sc = (this.sc.get()).push();
                if (funcdecl.isCtorDeclaration() != null)
                {
                    (this.sc.get()).flags.value |= 1;
                }
                (this.sc.get()).stc.value = 0L;
                (this.sc.get()).linkage.value = funcdecl.linkage.value;
                funcdecl.type.value = typeSemantic(f, funcdecl.loc.value, this.sc);
                this.sc = (this.sc.get()).pop();
            }
            funcdecl.semanticRun.value = PASS.semantic3done;
            funcdecl.semantic3Errors.value = (global.errors.value != oldErrors) || (funcdecl.fbody.value != null) && (funcdecl.fbody.value.isErrorStatement() != null);
            if (((funcdecl.type.value.ty.value & 0xFF) == ENUMTY.Terror))
            {
                funcdecl.errors.value = true;
            }
        }

        public  void visit(CtorDeclaration ctor) {
            if ((ctor.semanticRun.value >= PASS.semantic3))
            {
                return ;
            }
            AggregateDeclaration ad = ctor.isMemberDecl();
            if ((ad != null) && (ad.fieldDtor != null) && global.params.dtorFields)
            {
                Expression e = new ThisExp(ctor.loc.value);
                e.type.value = ad.type.value.mutableOf();
                e = new DotVarExp(ctor.loc.value, e, ad.fieldDtor, false);
                e = new CallExp(ctor.loc.value, e);
                ExpStatement sexp = new ExpStatement(ctor.loc.value, e);
                ScopeStatement ss = new ScopeStatement(ctor.loc.value, sexp, ctor.loc.value);
                Identifier id = Identifier.generateId(new BytePtr("__o"));
                ThrowStatement ts = new ThrowStatement(ctor.loc.value, new IdentifierExp(ctor.loc.value, id));
                CompoundStatement handler = new CompoundStatement(ctor.loc.value, slice(new Statement[]{ss, ts}));
                Ptr<DArray<Catch>> catches = refPtr(new DArray<Catch>());
                Catch ctch = new Catch(ctor.loc.value, getException(), id, handler);
                (catches.get()).push(ctch);
                ctor.fbody.value = new TryCatchStatement(ctor.loc.value, ctor.fbody.value, catches);
            }
            this.visit((FuncDeclaration)ctor);
        }

        public  void visit(Nspace ns) {
            if ((ns.semanticRun.value >= PASS.semantic3))
            {
                return ;
            }
            ns.semanticRun.value = PASS.semantic3;
            if (ns.members.value != null)
            {
                this.sc = (this.sc.get()).push(ns);
                (this.sc.get()).linkage.value = LINK.cpp;
                {
                    Slice<Dsymbol> __r1559 = (ns.members.value.get()).opSlice().copy();
                    int __key1560 = 0;
                    for (; (__key1560 < __r1559.getLength());__key1560 += 1) {
                        Dsymbol s = __r1559.get(__key1560);
                        semantic3(s, this.sc);
                    }
                }
                (this.sc.get()).pop();
            }
        }

        public  void visit(AttribDeclaration ad) {
            Ptr<DArray<Dsymbol>> d = ad.include(this.sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = ad.newScope(this.sc);
                {
                    int i = 0;
                    for (; (i < (d.get()).length.value);i++){
                        Dsymbol s = (d.get()).get(i);
                        semantic3(s, sc2);
                    }
                }
                if ((sc2 != this.sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        public  void visit(AggregateDeclaration ad) {
            if (ad.members.value == null)
            {
                return ;
            }
            StructDeclaration sd = ad.isStructDeclaration();
            if (this.sc == null)
            {
                assert(sd != null);
                sd.semanticTypeInfoMembers();
                return ;
            }
            Ptr<Scope> sc2 = ad.newScope(this.sc);
            {
                int i = 0;
                for (; (i < (ad.members.value.get()).length.value);i++){
                    Dsymbol s = (ad.members.value.get()).get(i);
                    semantic3(s, sc2);
                }
            }
            (sc2.get()).pop();
            if ((ad.getRTInfo == null) && (Type.rtinfo != null) && !ad.isDeprecated() || ((global.params.useDeprecated & 0xFF) != 0) && (ad.type.value != null) && ((ad.type.value.ty.value & 0xFF) != ENUMTY.Terror))
            {
                Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                (tiargs.get()).push(ad.type.value);
                TemplateInstance ti = new TemplateInstance(ad.loc.value, Type.rtinfo, tiargs);
                Ptr<Scope> sc3 = (ti.tempdecl.value._scope.value.get()).startCTFE();
                (sc3.get()).tinst = (this.sc.get()).tinst;
                (sc3.get()).minst.value = (this.sc.get()).minst.value;
                if (ad.isDeprecated())
                {
                    (sc3.get()).stc.value |= 1024L;
                }
                dsymbolSemantic(ti, sc3);
                semantic2(ti, sc3);
                semantic3(ti, sc3);
                Expression e = symbolToExp(ti.toAlias(), Loc.initial.value, sc3, false);
                (sc3.get()).endCTFE();
                e = e.ctfeInterpret();
                ad.getRTInfo = e;
            }
            if (sd != null)
            {
                sd.semanticTypeInfoMembers();
            }
            ad.semanticRun.value = PASS.semantic3done;
        }


        public Semantic3Visitor() {}

        public Semantic3Visitor copy() {
            Semantic3Visitor that = new Semantic3Visitor();
            that.sc = this.sc;
            return that;
        }
    }
    public static class FuncDeclSem3
    {
        public FuncDeclaration funcdecl = null;
        public Ptr<Scope> sc = null;
        public  FuncDeclSem3(FuncDeclaration fd, Ptr<Scope> s) {
            this.funcdecl = fd;
            this.sc = s;
        }

        public  void checkInContractOverrides() {
            if (this.funcdecl.frequires != null)
            {
                {
                    int i = 0;
                    for (; (i < this.funcdecl.foverrides.length.value);i++){
                        FuncDeclaration fdv = this.funcdecl.foverrides.get(i);
                        if ((fdv.fbody.value != null) && (fdv.frequires == null))
                        {
                            this.funcdecl.error(new BytePtr("cannot have an in contract when overridden function `%s` does not have an in contract"), fdv.toPrettyChars(false));
                            break;
                        }
                    }
                }
            }
        }

        public FuncDeclSem3(){
        }
        public FuncDeclSem3 copy(){
            FuncDeclSem3 r = new FuncDeclSem3();
            r.funcdecl = funcdecl;
            r.sc = sc;
            return r;
        }
        public FuncDeclSem3 opAssign(FuncDeclSem3 that) {
            this.funcdecl = that.funcdecl;
            this.sc = that.sc;
            return this;
        }
    }
}
