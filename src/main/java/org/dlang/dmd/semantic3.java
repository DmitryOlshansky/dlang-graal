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
            if ((tempinst.semanticRun >= PASS.semantic3))
                return ;
            tempinst.semanticRun = PASS.semantic3;
            if (!tempinst.errors && (tempinst.members != null))
            {
                TemplateDeclaration tempdecl = tempinst.tempdecl.isTemplateDeclaration();
                assert(tempdecl != null);
                this.sc = tempdecl._scope;
                this.sc = (this.sc.get()).push(tempinst.argsym);
                this.sc = (this.sc.get()).push(tempinst);
                (this.sc.get()).tinst = tempinst;
                (this.sc.get()).minst = tempinst.minst;
                int needGagging = ((tempinst.gagged && (global.value.gag == 0)) ? 1 : 0);
                int olderrors = global.value.errors;
                int oldGaggedErrors = -1;
                if (needGagging != 0)
                    oldGaggedErrors = global.value.startGagging();
                {
                    int i = 0;
                    for (; (i < (tempinst.members.get()).length);i++){
                        Dsymbol s = (tempinst.members.get()).get(i);
                        semantic3(s, this.sc);
                        if (tempinst.gagged && (global.value.errors != olderrors))
                            break;
                    }
                }
                if ((global.value.errors != olderrors))
                {
                    if (!tempinst.errors)
                    {
                        if (!tempdecl.literal)
                            tempinst.error(tempinst.loc, new BytePtr("error instantiating"));
                        if (tempinst.tinst != null)
                            tempinst.tinst.printInstantiationTrace();
                    }
                    tempinst.errors = true;
                }
                if (needGagging != 0)
                    global.value.endGagging(oldGaggedErrors);
                this.sc = (this.sc.get()).pop();
                (this.sc.get()).pop();
            }
        }

        public  void visit(TemplateMixin tmix) {
            if ((tmix.semanticRun >= PASS.semantic3))
                return ;
            tmix.semanticRun = PASS.semantic3;
            if (tmix.members != null)
            {
                this.sc = (this.sc.get()).push(tmix.argsym);
                this.sc = (this.sc.get()).push(tmix);
                {
                    int i = 0;
                    for (; (i < (tmix.members.get()).length);i++){
                        Dsymbol s = (tmix.members.get()).get(i);
                        semantic3(s, this.sc);
                    }
                }
                this.sc = (this.sc.get()).pop();
                (this.sc.get()).pop();
            }
        }

        public  void visit(dmodule.Module mod) {
            if ((mod.semanticRun != PASS.semantic2done))
                return ;
            mod.semanticRun = PASS.semantic3;
            Ptr<Scope> sc = Scope.createGlobal(mod);
            {
                int i = 0;
                for (; (i < (mod.members.get()).length);i++){
                    Dsymbol s = (mod.members.get()).get(i);
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
            mod.semanticRun = PASS.semantic3done;
        }

        public  void visit(FuncDeclaration funcdecl) {
            Ref<FuncDeclaration> funcdecl_ref = ref(funcdecl);
            Function0<Boolean> addReturn0 = new Function0<Boolean>(){
                public Boolean invoke() {
                    Ref<TypeFunction> f = ref((TypeFunction)funcdecl_ref.value.type);
                    return ((f.value.next.ty & 0xFF) == ENUMTY.Tvoid) && funcdecl_ref.value.isMain() || global.value.params.betterC && funcdecl_ref.value.isCMain();
                }
            };
            VarDeclaration _arguments = null;
            if (funcdecl_ref.value.parent.value == null)
            {
                if (global.value.errors != 0)
                    return ;
                throw new AssertionError("Unreachable code!");
            }
            if (funcdecl_ref.value.errors || isError(funcdecl_ref.value.parent.value))
            {
                funcdecl_ref.value.errors = true;
                return ;
            }
            if ((pequals(funcdecl_ref.value.ident, Id.assign.value)) && (funcdecl_ref.value.inuse == 0))
            {
                if ((funcdecl_ref.value.storage_class & 70368744177664L) != 0)
                {
                    int oldErrors = global.value.startGagging();
                    funcdecl_ref.value.inuse += 1;
                    semantic3(funcdecl_ref.value, this.sc);
                    funcdecl_ref.value.inuse -= 1;
                    if (global.value.endGagging(oldErrors))
                    {
                        funcdecl_ref.value.storage_class |= 137438953472L;
                        funcdecl_ref.value.fbody = null;
                        funcdecl_ref.value.semantic3Errors = false;
                    }
                    return ;
                }
            }
            if ((funcdecl_ref.value.semanticRun >= PASS.semantic3))
                return ;
            funcdecl_ref.value.semanticRun = PASS.semantic3;
            funcdecl_ref.value.semantic3Errors = false;
            if ((funcdecl_ref.value.type == null) || ((funcdecl_ref.value.type.ty & 0xFF) != ENUMTY.Tfunction))
                return ;
            TypeFunction f = (TypeFunction)funcdecl_ref.value.type;
            if (!funcdecl_ref.value.inferRetType && ((f.next.ty & 0xFF) == ENUMTY.Terror))
                return ;
            if ((funcdecl_ref.value.fbody == null) && funcdecl_ref.value.inferRetType && (f.next == null))
            {
                funcdecl_ref.value.error(new BytePtr("has no function body with return type inference"));
                return ;
            }
            int oldErrors = global.value.errors;
            FuncDeclSem3 fds = fds = new FuncDeclSem3(funcdecl_ref.value, this.sc);
            fds.checkInContractOverrides();
            boolean needEnsure = FuncDeclaration.needsFensure(funcdecl_ref.value);
            if ((funcdecl_ref.value.fbody != null) || (funcdecl_ref.value.frequires != null) || needEnsure)
            {
                funcdecl_ref.value.localsymtab = new DsymbolTable();
                ScopeDsymbol ss = new ScopeDsymbol(funcdecl_ref.value.loc, null);
                {
                    Ptr<Scope> scx = this.sc;
                    for (; ;scx = (scx.get()).enclosing){
                        if ((scx.get()).scopesym != null)
                        {
                            ss.parent.value = (scx.get()).scopesym;
                            break;
                        }
                    }
                }
                ss.endlinnum = funcdecl_ref.value.endloc.linnum;
                Ptr<Scope> sc2 = (this.sc.get()).push(ss);
                (sc2.get()).func = funcdecl_ref.value;
                (sc2.get()).parent.value = funcdecl_ref.value;
                (sc2.get()).ctorflow.callSuper = CSX.none;
                (sc2.get()).sbreak = null;
                (sc2.get()).scontinue = null;
                (sc2.get()).sw = null;
                (sc2.get()).fes = funcdecl_ref.value.fes;
                (sc2.get()).linkage = LINK.d;
                (sc2.get()).stc &= -17660607202720L;
                (sc2.get()).protection = new Prot(Prot.Kind.public_).copy();
                (sc2.get()).explicitProtection = 0;
                (sc2.get()).aligndecl = null;
                if ((!pequals(funcdecl_ref.value.ident, Id.require)) && (!pequals(funcdecl_ref.value.ident, Id.ensure)))
                    (sc2.get()).flags = (this.sc.get()).flags & -97;
                (sc2.get()).flags &= -257;
                (sc2.get()).tf = null;
                (sc2.get()).os = null;
                (sc2.get()).inLoop = false;
                (sc2.get()).userAttribDecl = null;
                if (((sc2.get()).intypeof == 1))
                    (sc2.get()).intypeof = 2;
                (sc2.get()).ctorflow.fieldinit = new Slice<FieldInit>().copy();
                {
                    FuncLiteralDeclaration fld = funcdecl_ref.value.isFuncLiteralDeclaration();
                    if ((fld) != null)
                    {
                        {
                            AggregateDeclaration ad = funcdecl_ref.value.isMember2();
                            if ((ad) != null)
                            {
                                if ((this.sc.get()).intypeof == 0)
                                {
                                    if (((fld.tok & 0xFF) == 160))
                                        funcdecl_ref.value.error(new BytePtr("cannot be %s members"), ad.kind());
                                    else
                                        fld.tok = TOK.function_;
                                }
                                else
                                {
                                    if (((fld.tok & 0xFF) != 161))
                                        fld.tok = TOK.delegate_;
                                }
                            }
                        }
                    }
                }
                AggregateDeclaration ad = funcdecl_ref.value.isThis();
                FuncDeclaration.HiddenParameters hiddenParams = funcdecl_ref.value.declareThis(sc2, ad).copy();
                funcdecl_ref.value.vthis = hiddenParams.vthis;
                funcdecl_ref.value.isThis2 = hiddenParams.isThis2;
                funcdecl_ref.value.selectorParameter = hiddenParams.selectorParameter;
                if ((f.parameterList.varargs == VarArg.variadic))
                {
                    if ((f.linkage == LINK.d))
                    {
                        if (!global.value.params.useTypeInfo || (Type.dtypeinfo.value == null) || (Type.typeinfotypelist == null))
                        {
                            if (!global.value.params.useTypeInfo)
                                funcdecl_ref.value.error(new BytePtr("D-style variadic functions cannot be used with -betterC"));
                            else if (Type.typeinfotypelist == null)
                                funcdecl_ref.value.error(new BytePtr("`object.TypeInfo_Tuple` could not be found, but is implicitly used in D-style variadic functions"));
                            else
                                funcdecl_ref.value.error(new BytePtr("`object.TypeInfo` could not be found, but is implicitly used in D-style variadic functions"));
                            fatal();
                        }
                        funcdecl_ref.value.v_arguments = new VarDeclaration(funcdecl_ref.value.loc, Type.typeinfotypelist.type, Id._arguments_typeinfo, null, 0L);
                        funcdecl_ref.value.v_arguments.storage_class |= 1099511627808L;
                        dsymbolSemantic(funcdecl_ref.value.v_arguments, sc2);
                        (sc2.get()).insert(funcdecl_ref.value.v_arguments);
                        funcdecl_ref.value.v_arguments.parent.value = funcdecl_ref.value;
                        Type t = Type.dtypeinfo.value.type.arrayOf();
                        _arguments = new VarDeclaration(funcdecl_ref.value.loc, t, Id._arguments, null, 0L);
                        _arguments.storage_class |= 1099511627776L;
                        dsymbolSemantic(_arguments, sc2);
                        (sc2.get()).insert(_arguments);
                        _arguments.parent.value = funcdecl_ref.value;
                    }
                    if ((f.linkage == LINK.d) || (f.parameterList.length() != 0))
                    {
                        Type t = Type.tvalist;
                        funcdecl_ref.value.v_argptr = new VarDeclaration(funcdecl_ref.value.loc, t, Id._argptr, new VoidInitializer(funcdecl_ref.value.loc), 0L);
                        funcdecl_ref.value.v_argptr.storage_class |= 1099511627776L;
                        dsymbolSemantic(funcdecl_ref.value.v_argptr, sc2);
                        (sc2.get()).insert(funcdecl_ref.value.v_argptr);
                        funcdecl_ref.value.v_argptr.parent.value = funcdecl_ref.value;
                    }
                }
                int nparams = f.parameterList.length();
                if (nparams != 0)
                {
                    funcdecl_ref.value.parameters = new DArray<VarDeclaration>();
                    (funcdecl_ref.value.parameters.get()).reserve(nparams);
                    {
                        int i = 0;
                        for (; (i < nparams);i++){
                            Parameter fparam = f.parameterList.get(i);
                            Identifier id = fparam.ident;
                            long stc = 0L;
                            if (id == null)
                            {
                                fparam.ident = (id = Identifier.generateId(new BytePtr("_param_"), i));
                                stc |= 1099511627776L;
                            }
                            Type vtype = fparam.type;
                            VarDeclaration v = new VarDeclaration(funcdecl_ref.value.loc, vtype, id, null, 0L);
                            stc |= 32L;
                            if ((f.parameterList.varargs == VarArg.typesafe) && (i + 1 == nparams))
                            {
                                stc |= 65536L;
                                Type vtypeb = vtype.toBasetype();
                                if (((vtypeb.ty & 0xFF) == ENUMTY.Tarray))
                                {
                                    stc |= 524288L;
                                }
                            }
                            if (((funcdecl_ref.value.flags & FUNCFLAG.inferScope) != 0) && ((fparam.storageClass & 524288L) == 0))
                                stc |= 281474976710656L;
                            stc |= fparam.storageClass & 17594890860556L;
                            v.storage_class = stc;
                            dsymbolSemantic(v, sc2);
                            if ((sc2.get()).insert(v) == null)
                            {
                                funcdecl_ref.value.error(new BytePtr("parameter `%s.%s` is already defined"), funcdecl_ref.value.toChars(), v.toChars());
                                funcdecl_ref.value.errors = true;
                            }
                            else
                                (funcdecl_ref.value.parameters.get()).push(v);
                            funcdecl_ref.value.localsymtab.insert((Dsymbol)v);
                            v.parent.value = funcdecl_ref.value;
                            if (fparam.userAttribDecl != null)
                                v.userAttribDecl = fparam.userAttribDecl;
                        }
                    }
                }
                if (f.parameterList.parameters != null)
                {
                    {
                        int i = 0;
                        for (; (i < (f.parameterList.parameters.get()).length);i++){
                            Parameter fparam = (f.parameterList.parameters.get()).get(i);
                            if (fparam.ident == null)
                                continue;
                            if (((fparam.type.ty & 0xFF) == ENUMTY.Ttuple))
                            {
                                TypeTuple t = (TypeTuple)fparam.type;
                                int dim = Parameter.dim(t.arguments);
                                Ptr<DArray<RootObject>> exps = new DArray<RootObject>(dim);
                                {
                                    int j = 0;
                                    for (; (j < dim);j++){
                                        Parameter narg = Parameter.getNth(t.arguments, j, null);
                                        assert(narg.ident != null);
                                        VarDeclaration v = (sc2.get()).search(Loc.initial.value, narg.ident, null, 0).isVarDeclaration();
                                        assert(v != null);
                                        Expression e = new VarExp(v.loc, v, true);
                                        exps.get().set(j, e);
                                    }
                                }
                                assert(fparam.ident != null);
                                TupleDeclaration v = new TupleDeclaration(funcdecl_ref.value.loc, fparam.ident, exps);
                                v.isexp = true;
                                if ((sc2.get()).insert(v) == null)
                                    funcdecl_ref.value.error(new BytePtr("parameter `%s.%s` is already defined"), funcdecl_ref.value.toChars(), v.toChars());
                                funcdecl_ref.value.localsymtab.insert((Dsymbol)v);
                                v.parent.value = funcdecl_ref.value;
                            }
                        }
                    }
                }
                Statement fpreinv = null;
                if (funcdecl_ref.value.addPreInvariant())
                {
                    Expression e = addInvariant(funcdecl_ref.value.loc, this.sc, ad, funcdecl_ref.value.vthis);
                    if (e != null)
                        fpreinv = new ExpStatement(Loc.initial.value, e);
                }
                Statement fpostinv = null;
                if (funcdecl_ref.value.addPostInvariant())
                {
                    Expression e = addInvariant(funcdecl_ref.value.loc, this.sc, ad, funcdecl_ref.value.vthis);
                    if (e != null)
                        fpostinv = new ExpStatement(Loc.initial.value, e);
                }
                if (funcdecl_ref.value.fbody == null)
                    funcdecl_ref.value.buildEnsureRequire();
                Ptr<Scope> scout = null;
                if (needEnsure || funcdecl_ref.value.addPostInvariant())
                {
                    int fensure_endlin = funcdecl_ref.value.endloc.linnum;
                    if (funcdecl_ref.value.fensure != null)
                        {
                            ScopeStatement s = funcdecl_ref.value.fensure.isScopeStatement();
                            if ((s) != null)
                                fensure_endlin = s.endloc.linnum;
                        }
                    if (needEnsure && ((global.value.params.useOut & 0xFF) == 2) || (fpostinv != null))
                    {
                        funcdecl_ref.value.returnLabel = new LabelDsymbol(Id.returnLabel);
                    }
                    ScopeDsymbol sym = new ScopeDsymbol(funcdecl_ref.value.loc, null);
                    sym.parent.value = (sc2.get()).scopesym;
                    sym.endlinnum = fensure_endlin;
                    scout = (sc2.get()).push(sym);
                }
                if (funcdecl_ref.value.fbody != null)
                {
                    ScopeDsymbol sym = new ScopeDsymbol(funcdecl_ref.value.loc, null);
                    sym.parent.value = (sc2.get()).scopesym;
                    sym.endlinnum = funcdecl_ref.value.endloc.linnum;
                    sc2 = (sc2.get()).push(sym);
                    AggregateDeclaration ad2 = funcdecl_ref.value.isMemberLocal();
                    if ((ad2 != null) && (funcdecl_ref.value.isCtorDeclaration() != null))
                    {
                        (sc2.get()).ctorflow.allocFieldinit(ad2.fields.length);
                        {
                            Slice<VarDeclaration> __r1545 = ad2.fields.opSlice().copy();
                            int __key1546 = 0;
                            for (; (__key1546 < __r1545.getLength());__key1546 += 1) {
                                VarDeclaration v = __r1545.get(__key1546);
                                v.ctorinit = false;
                            }
                        }
                    }
                    if (!funcdecl_ref.value.inferRetType && !target.value.isReturnOnStack(f, funcdecl_ref.value.needThis()))
                        funcdecl_ref.value.nrvo_can = false;
                    boolean inferRef = f.isref && ((funcdecl_ref.value.storage_class & 256L) != 0);
                    funcdecl_ref.value.fbody = statementSemantic(funcdecl_ref.value.fbody, sc2);
                    if (funcdecl_ref.value.fbody == null)
                        funcdecl_ref.value.fbody = new CompoundStatement(Loc.initial.value, new DArray<Statement>());
                    if (funcdecl_ref.value.naked)
                    {
                        fpreinv = null;
                        fpostinv = null;
                    }
                    assert((pequals(funcdecl_ref.value.type, f)) || ((funcdecl_ref.value.type.ty & 0xFF) == ENUMTY.Tfunction) && (f.purity == PURE.impure) && (((TypeFunction)funcdecl_ref.value.type).purity >= PURE.fwdref));
                    f = (TypeFunction)funcdecl_ref.value.type;
                    if (funcdecl_ref.value.inferRetType)
                    {
                        if (f.next == null)
                            f.next = Type.tvoid.value;
                        if (f.checkRetType(funcdecl_ref.value.loc))
                            funcdecl_ref.value.fbody = new ErrorStatement();
                    }
                    if (global.value.params.vcomplex && (f.next != null))
                        f.next.checkComplexTransition(funcdecl_ref.value.loc, this.sc);
                    if ((funcdecl_ref.value.returns != null) && (funcdecl_ref.value.fbody.isErrorStatement() == null))
                    {
                        {
                            int i = 0;
                            for (; (i < (funcdecl_ref.value.returns.get()).length);){
                                Expression exp = (funcdecl_ref.value.returns.get()).get(i).exp;
                                if (((exp.op & 0xFF) == 26) && (pequals(((VarExp)exp).var, funcdecl_ref.value.vresult)))
                                {
                                    if (addReturn0.invoke())
                                        exp.type.value = Type.tint32.value;
                                    else
                                        exp.type.value = f.next;
                                    (funcdecl_ref.value.returns.get()).remove(i);
                                    continue;
                                }
                                if (inferRef && f.isref && (exp.type.value.constConv(f.next) == 0))
                                    f.isref = false;
                                i++;
                            }
                        }
                    }
                    if (f.isref)
                    {
                        if ((funcdecl_ref.value.storage_class & 256L) != 0)
                            funcdecl_ref.value.storage_class &= -257L;
                    }
                    if (!target.value.isReturnOnStack(f, funcdecl_ref.value.needThis()))
                        funcdecl_ref.value.nrvo_can = false;
                    if (funcdecl_ref.value.fbody.isErrorStatement() != null)
                    {
                    }
                    else if (funcdecl_ref.value.isStaticCtorDeclaration() != null)
                    {
                        ScopeDsymbol pd = funcdecl_ref.value.toParent().isScopeDsymbol();
                        {
                            int i = 0;
                            for (; (i < (pd.members.get()).length);i++){
                                Dsymbol s = (pd.members.get()).get(i);
                                s.checkCtorConstInit();
                            }
                        }
                    }
                    else if ((ad2 != null) && (funcdecl_ref.value.isCtorDeclaration() != null))
                    {
                        ClassDeclaration cd = ad2.isClassDeclaration();
                        if (((sc2.get()).ctorflow.callSuper & 1) == 0)
                        {
                            {
                                Slice<VarDeclaration> __r1548 = ad2.fields.opSlice().copy();
                                int __key1547 = 0;
                                for (; (__key1547 < __r1548.getLength());__key1547 += 1) {
                                    VarDeclaration v = __r1548.get(__key1547);
                                    int i = __key1547;
                                    if (v.isThisDeclaration() != null)
                                        continue;
                                    if (((v.ctorinit ? 1 : 0) == 0))
                                    {
                                        if (v.isCtorinit() && !v.type.isMutable() && (cd != null))
                                            funcdecl_ref.value.error(new BytePtr("missing initializer for %s field `%s`"), MODtoChars(v.type.mod), v.toChars());
                                        else if ((v.storage_class & 549755813888L) != 0)
                                            error(funcdecl_ref.value.loc, new BytePtr("field `%s` must be initialized in constructor"), v.toChars());
                                        else if (v.type.needsNested())
                                            error(funcdecl_ref.value.loc, new BytePtr("field `%s` must be initialized in constructor, because it is nested struct"), v.toChars());
                                    }
                                    else
                                    {
                                        boolean mustInit = ((v.storage_class & 549755813888L) != 0) || v.type.needsNested();
                                        if (mustInit && (((sc2.get()).ctorflow.fieldinit.get(i).csx & 1) == 0))
                                        {
                                            funcdecl_ref.value.error(new BytePtr("field `%s` must be initialized but skipped"), v.toChars());
                                        }
                                    }
                                }
                            }
                        }
                        (sc2.get()).ctorflow.freeFieldinit();
                        if ((cd != null) && (((sc2.get()).ctorflow.callSuper & 16) == 0) && (cd.baseClass != null) && (cd.baseClass.ctor != null))
                        {
                            (sc2.get()).ctorflow.callSuper = CSX.none;
                            Type tthis = ad2.type.addMod(funcdecl_ref.value.vthis.type.mod);
                            FuncDeclaration fd = resolveFuncCall(Loc.initial.value, sc2, cd.baseClass.ctor, null, tthis, null, FuncResolveFlag.quiet);
                            if (fd == null)
                            {
                                funcdecl_ref.value.error(new BytePtr("no match for implicit `super()` call in constructor"));
                            }
                            else if ((fd.storage_class & 137438953472L) != 0)
                            {
                                funcdecl_ref.value.error(new BytePtr("cannot call `super()` implicitly because it is annotated with `@disable`"));
                            }
                            else
                            {
                                Expression e1 = new SuperExp(Loc.initial.value);
                                Expression e = new CallExp(Loc.initial.value, e1);
                                e = expressionSemantic(e, sc2);
                                Statement s = new ExpStatement(Loc.initial.value, e);
                                funcdecl_ref.value.fbody = new CompoundStatement(Loc.initial.value, slice(new Statement[]{s, funcdecl_ref.value.fbody}));
                            }
                        }
                    }
                    funcdecl_ref.value.buildEnsureRequire();
                    int blockexit = blockExit(funcdecl_ref.value.fbody, funcdecl_ref.value, f.isnothrow);
                    if (f.isnothrow && ((blockexit & 2) != 0))
                        error(funcdecl_ref.value.loc, new BytePtr("`nothrow` %s `%s` may throw"), funcdecl_ref.value.kind(), funcdecl_ref.value.toPrettyChars(false));
                    if (!(((blockexit & 18) != 0) || ((funcdecl_ref.value.flags & FUNCFLAG.hasCatches) != 0)))
                    {
                        funcdecl_ref.value.eh_none = true;
                    }
                    if ((funcdecl_ref.value.flags & FUNCFLAG.nothrowInprocess) != 0)
                    {
                        if ((pequals(funcdecl_ref.value.type, f)))
                            f = (TypeFunction)f.copy();
                        f.isnothrow = (blockexit & 2) == 0;
                    }
                    if (funcdecl_ref.value.fbody.isErrorStatement() != null)
                    {
                    }
                    else if ((ad2 != null) && (funcdecl_ref.value.isCtorDeclaration() != null))
                    {
                        if ((blockexit & 1) != 0)
                        {
                            Statement s = new ReturnStatement(funcdecl_ref.value.loc, null);
                            s = statementSemantic(s, sc2);
                            funcdecl_ref.value.fbody = new CompoundStatement(funcdecl_ref.value.loc, slice(new Statement[]{funcdecl_ref.value.fbody, s}));
                            funcdecl_ref.value.hasReturnExp |= (funcdecl_ref.value.hasReturnExp & 1) != 0 ? 16 : 1;
                        }
                    }
                    else if (funcdecl_ref.value.fes != null)
                    {
                        if ((blockexit & 1) != 0)
                        {
                            Expression e = literal_B6589FC6AB0DC82C();
                            Statement s = new ReturnStatement(Loc.initial.value, e);
                            funcdecl_ref.value.fbody = new CompoundStatement(Loc.initial.value, slice(new Statement[]{funcdecl_ref.value.fbody, s}));
                            funcdecl_ref.value.hasReturnExp |= (funcdecl_ref.value.hasReturnExp & 1) != 0 ? 16 : 1;
                        }
                        assert(funcdecl_ref.value.returnLabel == null);
                    }
                    else
                    {
                        boolean inlineAsm = (funcdecl_ref.value.hasReturnExp & 8) != 0;
                        if (((blockexit & 1) != 0) && ((f.next.ty & 0xFF) != ENUMTY.Tvoid) && !inlineAsm)
                        {
                            Expression e = null;
                            if (funcdecl_ref.value.hasReturnExp == 0)
                                funcdecl_ref.value.error(new BytePtr("has no `return` statement, but is expected to return a value of type `%s`"), f.next.toChars());
                            else
                                funcdecl_ref.value.error(new BytePtr("no `return exp;` or `assert(0);` at end of function"));
                            if (((global.value.params.useAssert & 0xFF) == 2) && !global.value.params.useInline)
                            {
                                e = new AssertExp(funcdecl_ref.value.endloc, literal_B6589FC6AB0DC82C(), new StringExp(funcdecl_ref.value.loc, new BytePtr("missing return expression")));
                            }
                            else
                                e = new HaltExp(funcdecl_ref.value.endloc);
                            e = new CommaExp(Loc.initial.value, e, defaultInit(f.next, Loc.initial.value), true);
                            e = expressionSemantic(e, sc2);
                            Statement s = new ExpStatement(Loc.initial.value, e);
                            funcdecl_ref.value.fbody = new CompoundStatement(Loc.initial.value, slice(new Statement[]{funcdecl_ref.value.fbody, s}));
                        }
                    }
                    if (funcdecl_ref.value.returns != null)
                    {
                        boolean implicit0 = addReturn0.invoke();
                        Type tret = implicit0 ? Type.tint32.value : f.next;
                        assert(((tret.ty & 0xFF) != ENUMTY.Tvoid));
                        if ((funcdecl_ref.value.vresult != null) || (funcdecl_ref.value.returnLabel != null))
                            funcdecl_ref.value.buildResultVar(scout != null ? scout : sc2, tret);
                        {
                            int i = 0;
                            for (; (i < (funcdecl_ref.value.returns.get()).length);i++){
                                ReturnStatement rs = (funcdecl_ref.value.returns.get()).get(i);
                                Expression exp = rs.exp;
                                if (((exp.op & 0xFF) == 127))
                                    continue;
                                if (((tret.ty & 0xFF) == ENUMTY.Terror))
                                {
                                    exp = checkGC(sc2, exp);
                                    continue;
                                }
                                if ((exp.implicitConvTo(tret) == 0) && funcdecl_ref.value.isTypeIsolated(exp.type.value))
                                {
                                    if (exp.type.value.immutableOf().implicitConvTo(tret) != 0)
                                        exp = exp.castTo(sc2, exp.type.value.immutableOf());
                                    else if (exp.type.value.wildOf().implicitConvTo(tret) != 0)
                                        exp = exp.castTo(sc2, exp.type.value.wildOf());
                                }
                                boolean hasCopyCtor = ((exp.type.value.ty & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)exp.type.value).sym.hasCopyCtor;
                                if (!hasCopyCtor)
                                {
                                    if (f.isref && !MODimplicitConv(exp.type.value.mod, tret.mod) && (tret.isTypeSArray() == null))
                                        error(exp.loc, new BytePtr("expression `%s` of type `%s` is not implicitly convertible to return type `ref %s`"), exp.toChars(), exp.type.value.toChars(), tret.toChars());
                                    else
                                        exp = exp.implicitCastTo(sc2, tret);
                                }
                                if (f.isref)
                                {
                                    exp = exp.toLvalue(sc2, exp);
                                    checkReturnEscapeRef(sc2, exp, false);
                                }
                                else
                                {
                                    exp = exp.optimize(0, false);
                                    if (!funcdecl_ref.value.nrvo_can)
                                        exp = doCopyOrMove(sc2, exp, f.next);
                                    if (tret.hasPointers())
                                        checkReturnEscape(sc2, exp, false);
                                }
                                exp = checkGC(sc2, exp);
                                if (funcdecl_ref.value.vresult != null)
                                {
                                    exp = new BlitExp(rs.loc, funcdecl_ref.value.vresult, exp);
                                    exp.type.value = funcdecl_ref.value.vresult.type;
                                    if (rs.caseDim != 0)
                                        exp = Expression.combine(exp, new IntegerExp((long)rs.caseDim));
                                }
                                else if ((funcdecl_ref.value.tintro != null) && !tret.equals(funcdecl_ref.value.tintro.nextOf()))
                                {
                                    exp = exp.implicitCastTo(sc2, funcdecl_ref.value.tintro.nextOf());
                                }
                                rs.exp = exp;
                            }
                        }
                    }
                    if ((funcdecl_ref.value.nrvo_var != null) || (funcdecl_ref.value.returnLabel != null))
                    {
                        NrvoWalker nw = new NrvoWalker();
                        nw.fd = funcdecl_ref.value;
                        nw.sc = sc2;
                        nw.visitStmt(funcdecl_ref.value.fbody);
                    }
                    sc2 = (sc2.get()).pop();
                }
                funcdecl_ref.value.frequire = funcdecl_ref.value.mergeFrequire(funcdecl_ref.value.frequire, funcdecl_ref.value.fdrequireParams);
                funcdecl_ref.value.fensure = funcdecl_ref.value.mergeFensure(funcdecl_ref.value.fensure, Id.result, funcdecl_ref.value.fdensureParams);
                Statement freq = funcdecl_ref.value.frequire;
                Statement fens = funcdecl_ref.value.fensure;
                if (freq != null)
                {
                    ScopeDsymbol sym = new ScopeDsymbol(funcdecl_ref.value.loc, null);
                    sym.parent.value = (sc2.get()).scopesym;
                    sym.endlinnum = funcdecl_ref.value.endloc.linnum;
                    sc2 = (sc2.get()).push(sym);
                    (sc2.get()).flags = (sc2.get()).flags & -97 | 64;
                    freq = statementSemantic(freq, sc2);
                    blockExit(freq, funcdecl_ref.value, false);
                    funcdecl_ref.value.eh_none = false;
                    sc2 = (sc2.get()).pop();
                    if (((global.value.params.useIn & 0xFF) == 1))
                        freq = null;
                }
                if (fens != null)
                {
                    if (((f.next.ty & 0xFF) == ENUMTY.Tvoid) && (funcdecl_ref.value.fensures != null))
                    {
                        {
                            Slice<Ensure> __r1549 = (funcdecl_ref.value.fensures.get()).opSlice().copy();
                            int __key1550 = 0;
                            for (; (__key1550 < __r1549.getLength());__key1550 += 1) {
                                Ensure e = __r1549.get(__key1550).copy();
                                if (e.id != null)
                                {
                                    funcdecl_ref.value.error(e.ensure.loc, new BytePtr("`void` functions have no result"));
                                }
                            }
                        }
                    }
                    sc2 = scout;
                    (sc2.get()).flags = (sc2.get()).flags & -97 | 96;
                    if ((funcdecl_ref.value.fensure != null) && ((f.next.ty & 0xFF) != ENUMTY.Tvoid))
                        funcdecl_ref.value.buildResultVar(scout, f.next);
                    fens = statementSemantic(fens, sc2);
                    blockExit(fens, funcdecl_ref.value, false);
                    funcdecl_ref.value.eh_none = false;
                    sc2 = (sc2.get()).pop();
                    if (((global.value.params.useOut & 0xFF) == 1))
                        fens = null;
                }
                if ((funcdecl_ref.value.fbody != null) && (funcdecl_ref.value.fbody.isErrorStatement() != null))
                {
                }
                else
                {
                    Ptr<DArray<Statement>> a = new DArray<Statement>();
                    if (funcdecl_ref.value.parameters != null)
                    {
                        {
                            int i = 0;
                            for (; (i < (funcdecl_ref.value.parameters.get()).length);i++){
                                VarDeclaration v = (funcdecl_ref.value.parameters.get()).get(i);
                                if ((v.storage_class & 4096L) != 0)
                                {
                                    if (v._init == null)
                                    {
                                        v.error(new BytePtr("Zero-length `out` parameters are not allowed."));
                                        return ;
                                    }
                                    ExpInitializer ie = v._init.isExpInitializer();
                                    assert(ie != null);
                                    {
                                        ConstructExp iec = ie.exp.isConstructExp();
                                        if ((iec) != null)
                                        {
                                            AssignExp ec = new AssignExp(iec.loc, iec.e1.value, iec.e2.value);
                                            ec.type.value = iec.type.value;
                                            ie.exp = ec;
                                        }
                                    }
                                    (a.get()).push(new ExpStatement(Loc.initial.value, ie.exp));
                                }
                            }
                        }
                    }
                    if (_arguments != null)
                    {
                        Expression e = new VarExp(Loc.initial.value, funcdecl_ref.value.v_arguments, true);
                        e = new DotIdExp(Loc.initial.value, e, Id.elements);
                        e = new ConstructExp(Loc.initial.value, _arguments, e);
                        e = expressionSemantic(e, sc2);
                        _arguments._init = new ExpInitializer(Loc.initial.value, e);
                        DeclarationExp de = new DeclarationExp(Loc.initial.value, _arguments);
                        (a.get()).push(new ExpStatement(Loc.initial.value, de));
                    }
                    if ((freq != null) || (fpreinv != null))
                    {
                        if (freq == null)
                            freq = fpreinv;
                        else if (fpreinv != null)
                            freq = new CompoundStatement(Loc.initial.value, slice(new Statement[]{freq, fpreinv}));
                        (a.get()).push(freq);
                    }
                    if (funcdecl_ref.value.fbody != null)
                        (a.get()).push(funcdecl_ref.value.fbody);
                    if ((fens != null) || (fpostinv != null))
                    {
                        if (fens == null)
                            fens = fpostinv;
                        else if (fpostinv != null)
                            fens = new CompoundStatement(Loc.initial.value, slice(new Statement[]{fpostinv, fens}));
                        LabelStatement ls = new LabelStatement(Loc.initial.value, Id.returnLabel, fens);
                        funcdecl_ref.value.returnLabel.statement = ls;
                        (a.get()).push(funcdecl_ref.value.returnLabel.statement);
                        if (((f.next.ty & 0xFF) != ENUMTY.Tvoid) && (funcdecl_ref.value.vresult != null))
                        {
                            Expression e = new VarExp(Loc.initial.value, funcdecl_ref.value.vresult, true);
                            if (funcdecl_ref.value.tintro != null)
                            {
                                e = e.implicitCastTo(this.sc, funcdecl_ref.value.tintro.nextOf());
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
                    if (funcdecl_ref.value.parameters != null)
                    {
                        {
                            Slice<VarDeclaration> __r1551 = (funcdecl_ref.value.parameters.get()).opSlice().copy();
                            int __key1552 = 0;
                            for (; (__key1552 < __r1551.getLength());__key1552 += 1) {
                                VarDeclaration v = __r1551.get(__key1552);
                                if ((v.storage_class & 2109440L) != 0)
                                    continue;
                                if (v.needsScopeDtor())
                                {
                                    Statement s = new DtorExpStatement(Loc.initial.value, v.edtor, v);
                                    v.storage_class |= 16777216L;
                                    s = statementSemantic(s, sc2);
                                    boolean isnothrow = f.isnothrow & (funcdecl_ref.value.flags & FUNCFLAG.nothrowInprocess) == 0;
                                    int blockexit = blockExit(s, funcdecl_ref.value, isnothrow);
                                    if ((blockexit & 2) != 0)
                                        funcdecl_ref.value.eh_none = false;
                                    if (f.isnothrow && isnothrow && ((blockexit & 2) != 0))
                                        error(funcdecl_ref.value.loc, new BytePtr("`nothrow` %s `%s` may throw"), funcdecl_ref.value.kind(), funcdecl_ref.value.toPrettyChars(false));
                                    if (((funcdecl_ref.value.flags & FUNCFLAG.nothrowInprocess) != 0) && ((blockexit & 2) != 0))
                                        f.isnothrow = false;
                                    if ((blockExit(sbody, funcdecl_ref.value, f.isnothrow) == BE.fallthru))
                                        sbody = new CompoundStatement(Loc.initial.value, slice(new Statement[]{sbody, s}));
                                    else
                                        sbody = new TryFinallyStatement(Loc.initial.value, sbody, s);
                                }
                            }
                        }
                    }
                    funcdecl_ref.value.flags &= -5;
                    if (funcdecl_ref.value.isSynchronized())
                    {
                        ClassDeclaration cd = funcdecl_ref.value.toParentDecl().isClassDeclaration();
                        if (cd != null)
                        {
                            if (!global.value.params.is64bit && global.value.params.isWindows && !funcdecl_ref.value.isStatic() && !sbody.usesEH() && !global.value.params.trace)
                            {
                            }
                            else
                            {
                                Expression vsync = null;
                                if (funcdecl_ref.value.isStatic())
                                {
                                    vsync = new DotIdExp(funcdecl_ref.value.loc, symbolToExp(cd, funcdecl_ref.value.loc, sc2, false), Id.classinfo.value);
                                }
                                else
                                {
                                    vsync = new VarExp(funcdecl_ref.value.loc, funcdecl_ref.value.vthis, true);
                                    if (funcdecl_ref.value.isThis2)
                                    {
                                        vsync = new PtrExp(funcdecl_ref.value.loc, vsync);
                                        vsync = new IndexExp(funcdecl_ref.value.loc, vsync, literal_B6589FC6AB0DC82C());
                                    }
                                }
                                sbody = new PeelStatement(sbody);
                                sbody = new SynchronizedStatement(funcdecl_ref.value.loc, vsync, sbody);
                                sbody = statementSemantic(sbody, sc2);
                            }
                        }
                        else
                        {
                            funcdecl_ref.value.error(new BytePtr("synchronized function `%s` must be a member of a class"), funcdecl_ref.value.toChars());
                        }
                    }
                    if ((funcdecl_ref.value.fbody != null) || allowsContractWithoutBody(funcdecl_ref.value))
                        funcdecl_ref.value.fbody = sbody;
                }
                if (funcdecl_ref.value.gotos != null)
                {
                    {
                        int i = 0;
                        for (; (i < (funcdecl_ref.value.gotos.get()).length);i += 1){
                            (funcdecl_ref.value.gotos.get()).get(i).checkLabel();
                        }
                    }
                }
                if (funcdecl_ref.value.naked && (funcdecl_ref.value.fensures != null) || (funcdecl_ref.value.frequires != null))
                    funcdecl_ref.value.error(new BytePtr("naked assembly functions with contracts are not supported"));
                (sc2.get()).ctorflow.callSuper = CSX.none;
                (sc2.get()).pop();
            }
            if (funcdecl_ref.value.checkClosure())
            {
            }
            if ((funcdecl_ref.value.flags & FUNCFLAG.purityInprocess) != 0)
            {
                funcdecl_ref.value.flags &= -2;
                if ((pequals(funcdecl_ref.value.type, f)))
                    f = (TypeFunction)f.copy();
                f.purity = PURE.fwdref;
            }
            if ((funcdecl_ref.value.flags & FUNCFLAG.safetyInprocess) != 0)
            {
                funcdecl_ref.value.flags &= -3;
                if ((pequals(funcdecl_ref.value.type, f)))
                    f = (TypeFunction)f.copy();
                f.trust = TRUST.safe;
            }
            if ((funcdecl_ref.value.flags & FUNCFLAG.nogcInprocess) != 0)
            {
                funcdecl_ref.value.flags &= -9;
                if ((pequals(funcdecl_ref.value.type, f)))
                    f = (TypeFunction)f.copy();
                f.isnogc = true;
            }
            if ((funcdecl_ref.value.flags & FUNCFLAG.returnInprocess) != 0)
            {
                funcdecl_ref.value.flags &= -17;
                if ((funcdecl_ref.value.storage_class & 17592186044416L) != 0)
                {
                    if ((pequals(funcdecl_ref.value.type, f)))
                        f = (TypeFunction)f.copy();
                    f.isreturn = true;
                    if ((funcdecl_ref.value.storage_class & 4503599627370496L) != 0)
                        f.isreturninferred = true;
                }
            }
            funcdecl_ref.value.flags &= -65;
            {
                Slice<VarDeclaration> array = null;
                Slice<VarDeclaration> tmp = new Slice<VarDeclaration>(new VarDeclaration[10]);
                int dim = ((funcdecl_ref.value.vthis != null) ? 1 : 0) + (funcdecl_ref.value.parameters != null ? (funcdecl_ref.value.parameters.get()).length : 0);
                if ((dim <= 10))
                    array = tmp.slice(0,dim).copy();
                else
                {
                    Ptr<VarDeclaration> ptr = pcopy(((Ptr<VarDeclaration>)Mem.xmalloc(dim * 4)));
                    array = ptr.slice(0,dim).copy();
                }
                int n = 0;
                if (funcdecl_ref.value.vthis != null)
                    array.set(n++, funcdecl_ref.value.vthis);
                if (funcdecl_ref.value.parameters != null)
                {
                    {
                        Slice<VarDeclaration> __r1553 = (funcdecl_ref.value.parameters.get()).opSlice().copy();
                        int __key1554 = 0;
                        for (; (__key1554 < __r1553.getLength());__key1554 += 1) {
                            VarDeclaration v = __r1553.get(__key1554);
                            array.set(n++, v);
                        }
                    }
                }
                eliminateMaybeScopes(array.slice(0,n));
                if ((dim > 10))
                    Mem.xfree(toPtr<VarDeclaration>(array));
            }
            if ((funcdecl_ref.value.parameters != null) && !funcdecl_ref.value.errors)
            {
                int nfparams = f.parameterList.length();
                assert((nfparams == (funcdecl_ref.value.parameters.get()).length));
                {
                    Slice<VarDeclaration> __r1556 = (funcdecl_ref.value.parameters.get()).opSlice().copy();
                    int __key1555 = 0;
                    for (; (__key1555 < __r1556.getLength());__key1555 += 1) {
                        VarDeclaration v = __r1556.get(__key1555);
                        int u = __key1555;
                        if ((v.storage_class & 281474976710656L) != 0)
                        {
                            Parameter p = f.parameterList.get(u);
                            notMaybeScope(v);
                            v.storage_class |= 562949953945600L;
                            p.storageClass |= 562949953945600L;
                            assert((p.storageClass & 281474976710656L) == 0);
                        }
                    }
                }
            }
            if ((funcdecl_ref.value.vthis != null) && ((funcdecl_ref.value.vthis.storage_class & 281474976710656L) != 0))
            {
                notMaybeScope(funcdecl_ref.value.vthis);
                funcdecl_ref.value.vthis.storage_class |= 562949953945600L;
                f.isscope = true;
                f.isscopeinferred = true;
            }
            if ((!pequals(f, funcdecl_ref.value.type)))
                f.deco = null;
            if ((f.deco == null) && (!pequals(funcdecl_ref.value.ident, Id.xopEquals)) && (!pequals(funcdecl_ref.value.ident, Id.xopCmp)))
            {
                this.sc = (this.sc.get()).push();
                if (funcdecl_ref.value.isCtorDeclaration() != null)
                    (this.sc.get()).flags |= 1;
                (this.sc.get()).stc = 0L;
                (this.sc.get()).linkage = funcdecl_ref.value.linkage;
                funcdecl_ref.value.type = typeSemantic(f, funcdecl_ref.value.loc, this.sc);
                this.sc = (this.sc.get()).pop();
            }
            funcdecl_ref.value.semanticRun = PASS.semantic3done;
            funcdecl_ref.value.semantic3Errors = (global.value.errors != oldErrors) || (funcdecl_ref.value.fbody != null) && (funcdecl_ref.value.fbody.isErrorStatement() != null);
            if (((funcdecl_ref.value.type.ty & 0xFF) == ENUMTY.Terror))
                funcdecl_ref.value.errors = true;
        }

        public  void visit(CtorDeclaration ctor) {
            if ((ctor.semanticRun >= PASS.semantic3))
                return ;
            AggregateDeclaration ad = ctor.isMemberDecl();
            if ((ad != null) && (ad.fieldDtor != null) && global.value.params.dtorFields)
            {
                Expression e = new ThisExp(ctor.loc);
                e.type.value = ad.type.mutableOf();
                e = new DotVarExp(ctor.loc, e, ad.fieldDtor, false);
                e = new CallExp(ctor.loc, e);
                ExpStatement sexp = new ExpStatement(ctor.loc, e);
                ScopeStatement ss = new ScopeStatement(ctor.loc, sexp, ctor.loc);
                Identifier id = Identifier.generateId(new BytePtr("__o"));
                ThrowStatement ts = new ThrowStatement(ctor.loc, new IdentifierExp(ctor.loc, id));
                CompoundStatement handler = new CompoundStatement(ctor.loc, slice(new Statement[]{ss, ts}));
                Ptr<DArray<Catch>> catches = new DArray<Catch>();
                Catch ctch = new Catch(ctor.loc, getException(), id, handler);
                (catches.get()).push(ctch);
                ctor.fbody = new TryCatchStatement(ctor.loc, ctor.fbody, catches);
            }
            this.visit((FuncDeclaration)ctor);
        }

        public  void visit(Nspace ns) {
            if ((ns.semanticRun >= PASS.semantic3))
                return ;
            ns.semanticRun = PASS.semantic3;
            if (ns.members != null)
            {
                this.sc = (this.sc.get()).push(ns);
                (this.sc.get()).linkage = LINK.cpp;
                {
                    Slice<Dsymbol> __r1557 = (ns.members.get()).opSlice().copy();
                    int __key1558 = 0;
                    for (; (__key1558 < __r1557.getLength());__key1558 += 1) {
                        Dsymbol s = __r1557.get(__key1558);
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
                    for (; (i < (d.get()).length);i++){
                        Dsymbol s = (d.get()).get(i);
                        semantic3(s, sc2);
                    }
                }
                if ((sc2 != this.sc))
                    (sc2.get()).pop();
            }
        }

        public  void visit(AggregateDeclaration ad) {
            if (ad.members == null)
                return ;
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
                for (; (i < (ad.members.get()).length);i++){
                    Dsymbol s = (ad.members.get()).get(i);
                    semantic3(s, sc2);
                }
            }
            (sc2.get()).pop();
            if ((ad.getRTInfo == null) && (Type.rtinfo != null) && !ad.isDeprecated() || ((global.value.params.useDeprecated & 0xFF) != 0) && (ad.type != null) && ((ad.type.ty & 0xFF) != ENUMTY.Terror))
            {
                Ptr<DArray<RootObject>> tiargs = new DArray<RootObject>();
                (tiargs.get()).push(ad.type);
                TemplateInstance ti = new TemplateInstance(ad.loc, Type.rtinfo, tiargs);
                Ptr<Scope> sc3 = (ti.tempdecl._scope.get()).startCTFE();
                (sc3.get()).tinst = (this.sc.get()).tinst;
                (sc3.get()).minst = (this.sc.get()).minst;
                if (ad.isDeprecated())
                    (sc3.get()).stc |= 1024L;
                dsymbolSemantic(ti, sc3);
                semantic2(ti, sc3);
                semantic3(ti, sc3);
                Expression e = symbolToExp(ti.toAlias(), Loc.initial.value, sc3, false);
                (sc3.get()).endCTFE();
                e = e.ctfeInterpret();
                ad.getRTInfo = e;
            }
            if (sd != null)
                sd.semanticTypeInfoMembers();
            ad.semanticRun = PASS.semantic3done;
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
                    for (; (i < this.funcdecl.foverrides.length);i++){
                        FuncDeclaration fdv = this.funcdecl.foverrides.get(i);
                        if ((fdv.fbody != null) && (fdv.frequires == null))
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
