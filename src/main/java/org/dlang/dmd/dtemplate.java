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
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.impcnvtab.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.staticcond.*;
import static org.dlang.dmd.templateparamsem.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class dtemplate {
    private static class DeduceType extends Visitor
    {
        private Ref<Ptr<Scope>> sc = ref(null);
        private Ref<Type> tparam = ref(null);
        private Ref<Ptr<DArray<TemplateParameter>>> parameters = ref(null);
        private Ref<Ptr<DArray<RootObject>>> dedtypes = ref(null);
        private Ref<IntPtr> wm = ref(null);
        private IntRef inferStart = ref(0);
        private Ref<Boolean> ignoreAliasThis = ref(false);
        private IntRef result = ref(0);
        public  DeduceType(Ptr<Scope> sc, Type tparam, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, IntPtr wm, int inferStart, boolean ignoreAliasThis) {
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            Ref<Type> tparam_ref = ref(tparam);
            Ref<Ptr<DArray<TemplateParameter>>> parameters_ref = ref(parameters);
            Ref<Ptr<DArray<RootObject>>> dedtypes_ref = ref(dedtypes);
            Ref<IntPtr> wm_ref = ref(wm);
            IntRef inferStart_ref = ref(inferStart);
            Ref<Boolean> ignoreAliasThis_ref = ref(ignoreAliasThis);
            this.sc.value = sc_ref.value;
            this.tparam.value = tparam_ref.value;
            this.parameters.value = parameters_ref.value;
            this.dedtypes.value = dedtypes_ref.value;
            this.wm.value = pcopy(wm_ref.value);
            this.inferStart.value = inferStart_ref.value;
            this.ignoreAliasThis.value = ignoreAliasThis_ref.value;
            this.result.value = MATCH.nomatch;
        }

        public  void visit(Type t) {
            Ref<Type> t_ref = ref(t);
            try {
                try {
                    try {
                        if (this.tparam.value == null)
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if ((pequals(t_ref.value, this.tparam.value)))
                        {
                            /*goto Lexact*/throw Dispatch0.INSTANCE;
                        }
                        if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tident))
                        {
                            IntRef i = ref(templateParameterLookup(this.tparam.value, this.parameters.value));
                            if ((i.value == 305419896))
                            {
                                if (this.sc.value == null)
                                {
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                }
                                Ref<Loc> loc = ref(new Loc());
                                if ((this.parameters.value.get()).length.value != 0)
                                {
                                    TemplateParameter tp = (this.parameters.value.get()).get(0);
                                    loc.value = tp.loc.value.copy();
                                }
                                this.tparam.value = typeSemantic(this.tparam.value, loc.value, this.sc.value);
                                assert(((this.tparam.value.ty.value & 0xFF) != ENUMTY.Tident));
                                this.result.value = deduceType(t_ref.value, this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                                return ;
                            }
                            TemplateParameter tp = (this.parameters.value.get()).get(i.value);
                            TypeIdentifier tident = (TypeIdentifier)this.tparam.value;
                            if ((tident.idents.length.value > 0))
                            {
                                Ref<Dsymbol> s = ref(t_ref.value.toDsymbol(this.sc.value));
                                {
                                    IntRef j = ref(tident.idents.length.value);
                                L_outer15:
                                    for (; (j.value-- > 0);){
                                        Ref<RootObject> id = ref(tident.idents.get(j.value));
                                        if ((id.value.dyncast() == DYNCAST.identifier))
                                        {
                                            if ((s.value == null) || (s.value.parent.value == null))
                                            {
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                            Ref<Dsymbol> s2 = ref(s.value.parent.value.search(Loc.initial.value, (Identifier)id.value, 0));
                                            if (s2.value == null)
                                            {
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                            s2.value = s2.value.toAlias();
                                            if ((!pequals(s.value, s2.value)))
                                            {
                                                {
                                                    Ref<Type> tx = ref(s2.value.getType());
                                                    if ((tx.value) != null)
                                                    {
                                                        if ((!pequals(s.value, tx.value.toDsymbol(this.sc.value))))
                                                        {
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                    }
                                                    else
                                                    {
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                }
                                            }
                                            s.value = s.value.parent.value;
                                        }
                                        else
                                        {
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                    }
                                }
                                if (tp.isTemplateTypeParameter() != null)
                                {
                                    Ref<Type> tt = ref(s.value.getType());
                                    if (tt.value == null)
                                    {
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    }
                                    Ref<Type> at = ref((Type)(this.dedtypes.value.get()).get(i.value));
                                    if ((at.value != null) && ((at.value.ty.value & 0xFF) == ENUMTY.Tnone))
                                    {
                                        at.value = ((TypeDeduced)at.value).tded.value;
                                    }
                                    if ((at.value == null) || tt.value.equals(at.value))
                                    {
                                        this.dedtypes.value.get().set(i.value, tt.value);
                                        /*goto Lexact*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                if (tp.isTemplateAliasParameter() != null)
                                {
                                    Ref<Dsymbol> s2 = ref((Dsymbol)(this.dedtypes.value.get()).get(i.value));
                                    if ((s2.value == null) || (pequals(s.value, s2.value)))
                                    {
                                        this.dedtypes.value.get().set(i.value, s.value);
                                        /*goto Lexact*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            }
                            if (tp.isTemplateTypeParameter() == null)
                            {
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            }
                            Ref<Type> at = ref((Type)(this.dedtypes.value.get()).get(i.value));
                            Ref<Type> tt = ref(null);
                            {
                                Ref<Byte> wx = ref(this.wm.value != null ? (byte)(deduceWildHelper(t_ref.value, ptr(tt), this.tparam.value) & 0xFF) : (byte)0);
                                if ((wx.value) != 0)
                                {
                                    if (at.value == null)
                                    {
                                        this.dedtypes.value.get().set(i.value, tt.value);
                                        this.wm.value.set(0, this.wm.value.get() | (wx.value & 0xFF));
                                        this.result.value = MATCH.constant;
                                        return ;
                                    }
                                    if (((at.value.ty.value & 0xFF) == ENUMTY.Tnone))
                                    {
                                        TypeDeduced xt = (TypeDeduced)at.value;
                                        this.result.value = xt.matchAll(tt.value);
                                        if ((this.result.value > MATCH.nomatch))
                                        {
                                            this.dedtypes.value.get().set(i.value, tt.value);
                                            if ((this.result.value > MATCH.constant))
                                            {
                                                this.result.value = MATCH.constant;
                                            }
                                        }
                                        return ;
                                    }
                                    if (tt.value.equals(at.value))
                                    {
                                        this.dedtypes.value.get().set(i.value, tt.value);
                                        /*goto Lconst*/throw Dispatch2.INSTANCE;
                                    }
                                    if (tt.value.implicitConvTo(at.value.constOf()) != 0)
                                    {
                                        this.dedtypes.value.get().set(i.value, at.value.constOf().mutableOf());
                                        this.wm.value.set(0, this.wm.value.get() | 1);
                                        /*goto Lconst*/throw Dispatch2.INSTANCE;
                                    }
                                    if (at.value.implicitConvTo(tt.value.constOf()) != 0)
                                    {
                                        this.dedtypes.value.get().set(i.value, tt.value.constOf().mutableOf());
                                        this.wm.value.set(0, this.wm.value.get() | 1);
                                        /*goto Lconst*/throw Dispatch2.INSTANCE;
                                    }
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                }
                                else {
                                    IntRef m = ref(deduceTypeHelper(t_ref.value, ptr(tt), this.tparam.value));
                                    if ((m.value) != 0)
                                    {
                                        if (at.value == null)
                                        {
                                            this.dedtypes.value.get().set(i.value, tt.value);
                                            this.result.value = m.value;
                                            return ;
                                        }
                                        if (((at.value.ty.value & 0xFF) == ENUMTY.Tnone))
                                        {
                                            TypeDeduced xt = (TypeDeduced)at.value;
                                            this.result.value = xt.matchAll(tt.value);
                                            if ((this.result.value > MATCH.nomatch))
                                            {
                                                this.dedtypes.value.get().set(i.value, tt.value);
                                            }
                                            return ;
                                        }
                                        if (tt.value.equals(at.value))
                                        {
                                            /*goto Lexact*/throw Dispatch0.INSTANCE;
                                        }
                                        if (((tt.value.ty.value & 0xFF) == ENUMTY.Tclass) && ((at.value.ty.value & 0xFF) == ENUMTY.Tclass))
                                        {
                                            this.result.value = tt.value.implicitConvTo(at.value);
                                            return ;
                                        }
                                        if (((tt.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((at.value.ty.value & 0xFF) == ENUMTY.Tarray) && (tt.value.nextOf().implicitConvTo(at.value.nextOf()) >= MATCH.constant))
                                        {
                                            /*goto Lexact*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                            }
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Ttypeof))
                        {
                            Ref<Loc> loc = ref(new Loc());
                            if ((this.parameters.value.get()).length.value != 0)
                            {
                                TemplateParameter tp = (this.parameters.value.get()).get(0);
                                loc.value = tp.loc.value.copy();
                            }
                            this.tparam.value = typeSemantic(this.tparam.value, loc.value, this.sc.value);
                        }
                        if (((t_ref.value.ty.value & 0xFF) != (this.tparam.value.ty.value & 0xFF)))
                        {
                            {
                                Ref<Dsymbol> sym = ref(t_ref.value.toDsymbol(this.sc.value));
                                if ((sym.value) != null)
                                {
                                    if (sym.value.isforwardRef() && (this.tparam.value.deco.value == null))
                                    {
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    }
                                }
                            }
                            IntRef m = ref(t_ref.value.implicitConvTo(this.tparam.value));
                            if ((m.value == MATCH.nomatch) && !this.ignoreAliasThis.value)
                            {
                                if (((t_ref.value.ty.value & 0xFF) == ENUMTY.Tclass))
                                {
                                    TypeClass tc = (TypeClass)t_ref.value;
                                    if ((tc.sym.value.aliasthis.value != null) && ((tc.att.value & AliasThisRec.tracingDT) == 0))
                                    {
                                        {
                                            Ref<Type> ato = ref(t_ref.value.aliasthisOf());
                                            if ((ato.value) != null)
                                            {
                                                tc.att.value = tc.att.value | AliasThisRec.tracingDT;
                                                m.value = deduceType(ato.value, this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                                                tc.att.value = tc.att.value & -9;
                                            }
                                        }
                                    }
                                }
                                else if (((t_ref.value.ty.value & 0xFF) == ENUMTY.Tstruct))
                                {
                                    TypeStruct ts = (TypeStruct)t_ref.value;
                                    if ((ts.sym.value.aliasthis.value != null) && ((ts.att.value & AliasThisRec.tracingDT) == 0))
                                    {
                                        {
                                            Ref<Type> ato = ref(t_ref.value.aliasthisOf());
                                            if ((ato.value) != null)
                                            {
                                                ts.att.value = ts.att.value | AliasThisRec.tracingDT;
                                                m.value = deduceType(ato.value, this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                                                ts.att.value = ts.att.value & -9;
                                            }
                                        }
                                    }
                                }
                            }
                            this.result.value = m.value;
                            return ;
                        }
                        if (t_ref.value.nextOf() != null)
                        {
                            if ((this.tparam.value.deco.value != null) && (this.tparam.value.hasWild() == 0))
                            {
                                this.result.value = t_ref.value.implicitConvTo(this.tparam.value);
                                return ;
                            }
                            Ref<Type> tpn = ref(this.tparam.value.nextOf());
                            if ((this.wm.value != null) && ((t_ref.value.ty.value & 0xFF) == ENUMTY.Taarray) && this.tparam.value.isWild())
                            {
                                tpn.value = tpn.value.substWildTo(16);
                            }
                            this.result.value = deduceType(t_ref.value.nextOf(), this.sc.value, tpn.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                            return ;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lexact:*/
                    this.result.value = MATCH.exact;
                    return ;
                }
                catch(Dispatch1 __d){}
            /*Lnomatch:*/
                this.result.value = MATCH.nomatch;
                return ;
            }
            catch(Dispatch2 __d){}
        /*Lconst:*/
            this.result.value = MATCH.constant;
        }

        public  void visit(TypeVector t) {
            Ref<TypeVector> t_ref = ref(t);
            if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tvector))
            {
                TypeVector tp = (TypeVector)this.tparam.value;
                this.result.value = deduceType(t_ref.value.basetype.value, this.sc.value, tp.basetype.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                return ;
            }
            this.visit((Type)t_ref);
        }

        public  void visit(TypeDArray t) {
            Ref<TypeDArray> t_ref = ref(t);
            this.visit((Type)t_ref);
        }

        public  void visit(TypeSArray t) {
            Ref<TypeSArray> t_ref = ref(t);
            if (this.tparam.value != null)
            {
                if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    IntRef m = ref(deduceType(t_ref.value.next.value, this.sc.value, this.tparam.value.nextOf(), this.parameters.value, this.dedtypes.value, this.wm.value, 0, false));
                    this.result.value = (m.value >= MATCH.constant) ? MATCH.convert : MATCH.nomatch;
                    return ;
                }
                Ref<TemplateParameter> tp = ref(null);
                Ref<Expression> edim = ref(null);
                IntRef i = ref(0);
                if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    TypeSArray tsa = (TypeSArray)this.tparam.value;
                    if (((tsa.dim.value.op.value & 0xFF) == 26) && ((((VarExp)tsa.dim.value).var.value.storage_class.value & 262144L) != 0))
                    {
                        Ref<Identifier> id = ref(((VarExp)tsa.dim.value).var.value.ident.value);
                        i.value = templateIdentifierLookup(id.value, this.parameters.value);
                        assert((i.value != 305419896));
                        tp.value = (this.parameters.value.get()).get(i.value);
                    }
                    else
                    {
                        edim.value = tsa.dim.value;
                    }
                }
                else if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Taarray))
                {
                    TypeAArray taa = (TypeAArray)this.tparam.value;
                    i.value = templateParameterLookup(taa.index.value, this.parameters.value);
                    if ((i.value != 305419896))
                    {
                        tp.value = (this.parameters.value.get()).get(i.value);
                    }
                    else
                    {
                        Ref<Expression> e = ref(null);
                        Ref<Type> tx = ref(null);
                        Ref<Dsymbol> s = ref(null);
                        resolve(taa.index.value, Loc.initial.value, this.sc.value, ptr(e), ptr(tx), ptr(s), false);
                        edim.value = s.value != null ? getValue(s) : getValue(e.value);
                    }
                }
                if ((tp.value != null) && (tp.value.matchArg(this.sc.value, t_ref.value.dim.value, i.value, this.parameters.value, this.dedtypes.value, null) != 0) || (edim.value != null) && (edim.value.toInteger() == t_ref.value.dim.value.toInteger()))
                {
                    this.result.value = deduceType(t_ref.value.next.value, this.sc.value, this.tparam.value.nextOf(), this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                    return ;
                }
            }
            this.visit((Type)t_ref);
        }

        public  void visit(TypeAArray t) {
            Ref<TypeAArray> t_ref = ref(t);
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                TypeAArray tp = (TypeAArray)this.tparam.value;
                if (deduceType(t_ref.value.index.value, this.sc.value, tp.index.value, this.parameters.value, this.dedtypes.value, null, 0, false) == 0)
                {
                    this.result.value = MATCH.nomatch;
                    return ;
                }
            }
            this.visit((Type)t_ref);
        }

        public  void visit(TypeFunction t) {
            Ref<TypeFunction> t_ref = ref(t);
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                TypeFunction tp = (TypeFunction)this.tparam.value;
                if ((t_ref.value.parameterList.varargs.value != tp.parameterList.varargs.value) || (t_ref.value.linkage.value != tp.linkage.value))
                {
                    this.result.value = MATCH.nomatch;
                    return ;
                }
                {
                    Ref<Slice<Parameter>> __r1203 = ref((tp.parameterList.parameters.value.get()).opSlice().copy());
                    IntRef __key1204 = ref(0);
                    for (; (__key1204.value < __r1203.value.getLength());__key1204.value += 1) {
                        Parameter fparam = __r1203.value.get(__key1204.value);
                        fparam.type.value = fparam.type.value.addStorageClass(fparam.storageClass.value);
                        fparam.storageClass.value &= -2685405189L;
                        if (!reliesOnTemplateParameters(fparam.type.value, (this.parameters.value.get()).opSlice(this.inferStart.value, (this.parameters.value.get()).length.value)))
                        {
                            Ref<Type> tx = ref(typeSemantic(fparam.type.value, Loc.initial.value, this.sc.value));
                            if (((tx.value.ty.value & 0xFF) == ENUMTY.Terror))
                            {
                                this.result.value = MATCH.nomatch;
                                return ;
                            }
                            fparam.type.value = tx.value;
                        }
                    }
                }
                IntRef nfargs = ref(t_ref.value.parameterList.length());
                IntRef nfparams = ref(tp.parameterList.length());
                try {
                    try {
                        if ((nfparams.value > 0) && (nfargs.value >= nfparams.value - 1))
                        {
                            Ref<Parameter> fparam = ref(tp.parameterList.get(nfparams.value - 1));
                            assert(fparam.value != null);
                            assert(fparam.value.type.value != null);
                            if (((fparam.value.type.value.ty.value & 0xFF) != ENUMTY.Tident))
                            {
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                            TypeIdentifier tid = (TypeIdentifier)fparam.value.type.value;
                            if (tid.idents.length.value != 0)
                            {
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                            IntRef tupi = ref(0);
                        L_outer16:
                            for (; 1 != 0;tupi.value++){
                                if ((tupi.value == (this.parameters.value.get()).length.value))
                                {
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                }
                                TemplateParameter tx = (this.parameters.value.get()).get(tupi.value);
                                Ref<TemplateTupleParameter> tup = ref(tx.isTemplateTupleParameter());
                                if ((tup.value != null) && tup.value.ident.value.equals(tid.ident.value))
                                {
                                    break;
                                }
                            }
                            IntRef tuple_dim = ref(nfargs.value - (nfparams.value - 1));
                            Ref<RootObject> o = ref((this.dedtypes.value.get()).get(tupi.value));
                            if (o.value != null)
                            {
                                Ref<Tuple> tup = ref(isTuple(o.value));
                                if ((tup.value == null) || (tup.value.objects.value.length.value != tuple_dim.value))
                                {
                                    this.result.value = MATCH.nomatch;
                                    return ;
                                }
                                {
                                    IntRef i = ref(0);
                                    for (; (i.value < tuple_dim.value);i.value++){
                                        Parameter arg = t_ref.value.parameterList.get(nfparams.value - 1 + i.value);
                                        if (!arg.type.value.equals(tup.value.objects.value.get(i.value)))
                                        {
                                            this.result.value = MATCH.nomatch;
                                            return ;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                Ref<Tuple> tup = ref(new Tuple(tuple_dim.value));
                                {
                                    IntRef i = ref(0);
                                    for (; (i.value < tuple_dim.value);i.value++){
                                        Parameter arg = t_ref.value.parameterList.get(nfparams.value - 1 + i.value);
                                        tup.value.objects.value.set(i.value, arg.type.value);
                                    }
                                }
                                this.dedtypes.value.get().set(tupi.value, tup.value);
                            }
                            nfparams.value--;
                            /*goto L2*/throw Dispatch1.INSTANCE;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if ((nfargs.value != nfparams.value))
                    {
                        this.result.value = MATCH.nomatch;
                        return ;
                    }
                }
                catch(Dispatch1 __d){}
            /*L2:*/
                {
                    IntRef i = ref(0);
                    for (; (i.value < nfparams.value);i.value++){
                        Parameter a = t_ref.value.parameterList.get(i.value);
                        Parameter ap = tp.parameterList.get(i.value);
                        if (!a.isCovariant(t_ref.value.isref.value, ap) || (deduceType(a.type.value, this.sc.value, ap.type.value, this.parameters.value, this.dedtypes.value, null, 0, false) == 0))
                        {
                            this.result.value = MATCH.nomatch;
                            return ;
                        }
                    }
                }
            }
            this.visit((Type)t_ref);
        }

        public  void visit(TypeIdentifier t) {
            Ref<TypeIdentifier> t_ref = ref(t);
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tident))
            {
                TypeIdentifier tp = (TypeIdentifier)this.tparam.value;
                {
                    IntRef i = ref(0);
                    for (; (i.value < t_ref.value.idents.length.value);i.value++){
                        RootObject id1 = t_ref.value.idents.get(i.value);
                        Ref<RootObject> id2 = ref(tp.idents.get(i.value));
                        if (!id1.equals(id2.value))
                        {
                            this.result.value = MATCH.nomatch;
                            return ;
                        }
                    }
                }
            }
            this.visit((Type)t_ref);
        }

        public  void visit(TypeInstance t) {
            Ref<TypeInstance> t_ref = ref(t);
            try {
                if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tinstance) && (t_ref.value.tempinst.value.tempdecl.value != null))
                {
                    Ref<TemplateDeclaration> tempdecl = ref(t_ref.value.tempinst.value.tempdecl.value.isTemplateDeclaration());
                    assert(tempdecl.value != null);
                    TypeInstance tp = (TypeInstance)this.tparam.value;
                    try {
                        if (tp.tempinst.value.tempdecl.value == null)
                        {
                            IntRef i = ref(templateIdentifierLookup(tp.tempinst.value.name.value, this.parameters.value));
                            if ((i.value == 305419896))
                            {
                                Ref<TypeIdentifier> tid = ref(new TypeIdentifier(tp.loc, tp.tempinst.value.name.value));
                                Ref<Type> tx = ref(null);
                                Ref<Expression> e = ref(null);
                                Ref<Dsymbol> s = ref(null);
                                resolve(tid.value, tp.loc, this.sc.value, ptr(e), ptr(tx), ptr(s), false);
                                if (tx.value != null)
                                {
                                    s.value = tx.value.toDsymbol(this.sc.value);
                                    {
                                        Ref<TemplateInstance> ti = ref(s.value != null ? s.value.parent.value.isTemplateInstance() : null);
                                        if ((ti.value) != null)
                                        {
                                            Ref<Dsymbol> p = ref((this.sc.value.get()).parent.value);
                                            for (; (p.value != null) && (!pequals(p.value, ti.value));) {
                                                p.value = p.value.parent.value;
                                            }
                                            if (p.value != null)
                                            {
                                                s.value = ti.value.tempdecl.value;
                                            }
                                        }
                                    }
                                }
                                if (s.value != null)
                                {
                                    s.value = s.value.toAlias();
                                    Ref<TemplateDeclaration> td = ref(s.value.isTemplateDeclaration());
                                    if (td.value != null)
                                    {
                                        if (td.value.overroot.value != null)
                                        {
                                            td.value = td.value.overroot.value;
                                        }
                                    L_outer17:
                                        for (; td.value != null;td.value = td.value.overnext.value){
                                            if ((pequals(td.value, tempdecl.value)))
                                            {
                                                /*goto L2*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                    }
                                }
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            TemplateParameter tpx = (this.parameters.value.get()).get(i.value);
                            if (tpx.matchArg(this.sc.value, tempdecl.value, i.value, this.parameters.value, this.dedtypes.value, null) == 0)
                            {
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                        }
                        else if ((!pequals(tempdecl.value, tp.tempinst.value.tempdecl.value)))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*L2:*/
                    {
                        IntRef i = ref(0);
                    L_outer18:
                        for (; 1 != 0;i.value++){
                            Ref<RootObject> o1 = ref(null);
                            if ((i.value < (t_ref.value.tempinst.value.tiargs.value.get()).length.value))
                            {
                                o1.value = (t_ref.value.tempinst.value.tiargs.value.get()).get(i.value);
                            }
                            else if ((i.value < t_ref.value.tempinst.value.tdtypes.value.length.value) && (i.value < (tp.tempinst.value.tiargs.value.get()).length.value))
                            {
                                o1.value = t_ref.value.tempinst.value.tdtypes.value.get(i.value);
                            }
                            else if ((i.value >= (tp.tempinst.value.tiargs.value.get()).length.value))
                            {
                                break;
                            }
                            if ((i.value >= (tp.tempinst.value.tiargs.value.get()).length.value))
                            {
                                IntRef dim = ref((tempdecl.value.parameters.get()).length.value - (tempdecl.value.isVariadic() != null ? 1 : 0));
                                for (; (i.value < dim.value) && (tempdecl.value.parameters.get()).get(i.value).dependent.value || (tempdecl.value.parameters.get()).get(i.value).hasDefaultArg();){
                                    i.value++;
                                }
                                if ((i.value >= dim.value))
                                {
                                    break;
                                }
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            Ref<RootObject> o2 = ref((tp.tempinst.value.tiargs.value.get()).get(i.value));
                            Ref<Type> t2 = ref(isType(o2.value));
                            IntRef j = ref((t2.value != null) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tident) && (i.value == (tp.tempinst.value.tiargs.value.get()).length.value - 1) ? templateParameterLookup(t2.value, this.parameters.value) : 305419896);
                            if ((j.value != 305419896) && (j.value == (this.parameters.value.get()).length.value - 1) && ((this.parameters.value.get()).get(j.value).isTemplateTupleParameter() != null))
                            {
                                IntRef vtdim = ref((tempdecl.value.isVariadic() != null ? (t_ref.value.tempinst.value.tiargs.value.get()).length.value : t_ref.value.tempinst.value.tdtypes.value.length.value) - i.value);
                                Ref<Tuple> vt = ref(new Tuple(vtdim.value));
                                {
                                    IntRef k = ref(0);
                                    for (; (k.value < vtdim.value);k.value++){
                                        Ref<RootObject> o = ref(null);
                                        if ((k.value < (t_ref.value.tempinst.value.tiargs.value.get()).length.value))
                                        {
                                            o.value = (t_ref.value.tempinst.value.tiargs.value.get()).get(i.value + k.value);
                                        }
                                        else
                                        {
                                            o.value = t_ref.value.tempinst.value.tdtypes.value.get(i.value + k.value);
                                        }
                                        vt.value.objects.value.set(k.value, o.value);
                                    }
                                }
                                Ref<Tuple> v = ref((Tuple)(this.dedtypes.value.get()).get(j.value));
                                if (v.value != null)
                                {
                                    if (!match(v.value, vt.value))
                                    {
                                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                else
                                {
                                    this.dedtypes.value.get().set(j.value, vt.value);
                                }
                                break;
                            }
                            else if (o1.value == null)
                            {
                                break;
                            }
                            Ref<Type> t1 = ref(isType(o1.value));
                            Ref<Dsymbol> s1 = ref(isDsymbol(o1.value));
                            Ref<Dsymbol> s2 = ref(isDsymbol(o2.value));
                            Ref<Expression> e1 = ref(s1.value != null ? getValue(s1) : getValue(isExpression(o1.value)));
                            Ref<Expression> e2 = ref(isExpression(o2.value));
                            if ((t1.value != null) && (t2.value != null))
                            {
                                if (deduceType(t1.value, this.sc.value, t2.value, this.parameters.value, this.dedtypes.value, null, 0, false) == 0)
                                {
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                            }
                            else if ((e1.value != null) && (e2.value != null))
                            {
                            /*Le:*/
                                e1.value = e1.value.ctfeInterpret();
                                if (((e2.value.op.value & 0xFF) == 26) && ((((VarExp)e2.value).var.value.storage_class.value & 262144L) != 0))
                                {
                                    j.value = templateIdentifierLookup(((VarExp)e2.value).var.value.ident.value, this.parameters.value);
                                    if ((j.value != 305419896))
                                    {
                                        /*goto L1*//*unrolled goto*/
                                    /*L1:*/
                                        if ((j.value == 305419896))
                                        {
                                            resolve(t2.value, ((TypeIdentifier)t2.value).loc, this.sc.value, ptr(e2), ptr(t2), ptr(s2), false);
                                            if (e2.value != null)
                                            {
                                                /*goto Le*/throw Dispatch0.INSTANCE;
                                            }
                                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                        }
                                        if ((this.parameters.value.get()).get(j.value).matchArg(this.sc.value, e1.value, j.value, this.parameters.value, this.dedtypes.value, null) == 0)
                                        {
                                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                                e2.value = expressionSemantic(e2.value, this.sc.value);
                                e2.value = e2.value.ctfeInterpret();
                                if (!e1.value.equals(e2.value))
                                {
                                    if (e2.value.implicitConvTo(e1.value.type.value) == 0)
                                    {
                                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                    }
                                    e2.value = e2.value.implicitCastTo(this.sc.value, e1.value.type.value);
                                    e2.value = e2.value.ctfeInterpret();
                                    if (!e1.value.equals(e2.value))
                                    {
                                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                    }
                                }
                            }
                            else if ((e1.value != null) && (t2.value != null) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tident))
                            {
                                j.value = templateParameterLookup(t2.value, this.parameters.value);
                            /*L1:*/
                                if ((j.value == 305419896))
                                {
                                    resolve(t2.value, ((TypeIdentifier)t2.value).loc, this.sc.value, ptr(e2), ptr(t2), ptr(s2), false);
                                    if (e2.value != null)
                                    {
                                        /*goto Le*/throw Dispatch0.INSTANCE;
                                    }
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                                if ((this.parameters.value.get()).get(j.value).matchArg(this.sc.value, e1.value, j.value, this.parameters.value, this.dedtypes.value, null) == 0)
                                {
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                            }
                            else if ((s1.value != null) && (s2.value != null))
                            {
                            /*Ls:*/
                                if (!s1.value.equals(s2.value))
                                {
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                            }
                            else if ((s1.value != null) && (t2.value != null) && ((t2.value.ty.value & 0xFF) == ENUMTY.Tident))
                            {
                                j.value = templateParameterLookup(t2.value, this.parameters.value);
                                if ((j.value == 305419896))
                                {
                                    resolve(t2.value, ((TypeIdentifier)t2.value).loc, this.sc.value, ptr(e2), ptr(t2), ptr(s2), false);
                                    if (s2.value != null)
                                    {
                                        /*goto Ls*//*unrolled goto*/
                                    /*Ls:*/
                                        if (!s1.value.equals(s2.value))
                                        {
                                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                                if ((this.parameters.value.get()).get(j.value).matchArg(this.sc.value, s1.value, j.value, this.parameters.value, this.dedtypes.value, null) == 0)
                                {
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                            }
                            else
                            {
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                        }
                    }
                }
                this.visit((Type)t_ref);
                return ;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            this.result.value = MATCH.nomatch;
        }

        public  void visit(TypeStruct t) {
            Ref<TypeStruct> t_ref = ref(t);
            Ref<TemplateInstance> ti = ref(t_ref.value.sym.value.parent.value.isTemplateInstance());
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tinstance))
            {
                if ((ti.value != null) && (pequals(ti.value.toAlias(), t_ref.value.sym.value)))
                {
                    Ref<TypeInstance> tx = ref(new TypeInstance(Loc.initial.value, ti.value));
                    this.result.value = deduceType(tx.value, this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                    return ;
                }
                Ref<TypeInstance> tpi = ref((TypeInstance)this.tparam.value);
                if (tpi.value.idents.length.value != 0)
                {
                    Ref<RootObject> id = ref(tpi.value.idents.get(tpi.value.idents.length.value - 1));
                    if ((id.value.dyncast() == DYNCAST.identifier) && t_ref.value.sym.value.ident.value.equals((Identifier)id.value))
                    {
                        Ref<Type> tparent = ref(t_ref.value.sym.value.parent.value.getType());
                        if (tparent.value != null)
                        {
                            tpi.value.idents.length.value--;
                            this.result.value = deduceType(tparent.value, this.sc.value, tpi.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                            tpi.value.idents.length.value++;
                            return ;
                        }
                    }
                }
            }
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                Ref<TypeStruct> tp = ref((TypeStruct)this.tparam.value);
                if ((this.wm.value != null) && (t_ref.value.deduceWild(this.tparam.value, false) != 0))
                {
                    this.result.value = MATCH.constant;
                    return ;
                }
                this.result.value = t_ref.value.implicitConvTo(tp.value);
                return ;
            }
            this.visit((Type)t_ref);
        }

        public  void visit(TypeEnum t) {
            Ref<TypeEnum> t_ref = ref(t);
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tenum))
            {
                TypeEnum tp = (TypeEnum)this.tparam.value;
                if ((pequals(t_ref.value.sym.value, tp.sym.value)))
                {
                    this.visit((Type)t_ref);
                }
                else
                {
                    this.result.value = MATCH.nomatch;
                }
                return ;
            }
            Ref<Type> tb = ref(t_ref.value.toBasetype());
            if (((tb.value.ty.value & 0xFF) == (this.tparam.value.ty.value & 0xFF)) || ((tb.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                this.result.value = deduceType(tb.value, this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                return ;
            }
            this.visit((Type)t_ref);
        }

        public static void deduceBaseClassParameters(BaseClass b, Ptr<Scope> sc, Type tparam, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<DArray<RootObject>> best, IntRef numBaseClassMatches) {
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            Ref<Type> tparam_ref = ref(tparam);
            Ref<Ptr<DArray<TemplateParameter>>> parameters_ref = ref(parameters);
            Ref<Ptr<DArray<RootObject>>> dedtypes_ref = ref(dedtypes);
            Ref<Ptr<DArray<RootObject>>> best_ref = ref(best);
            Ref<TemplateInstance> parti = ref(b.sym.value != null ? b.sym.value.parent.value.isTemplateInstance() : null);
            if (parti.value != null)
            {
                Ref<Ptr<DArray<RootObject>>> tmpdedtypes = ref(refPtr(new DArray<RootObject>((dedtypes_ref.value.get()).length.value)));
                memcpy((BytePtr)((tmpdedtypes.value.get()).tdata()), ((dedtypes_ref.value.get()).tdata()), ((dedtypes_ref.value.get()).length.value * 4));
                Ref<TypeInstance> t = ref(new TypeInstance(Loc.initial.value, parti.value));
                IntRef m = ref(deduceType(t.value, sc_ref.value, tparam_ref.value, parameters_ref.value, tmpdedtypes.value, null, 0, false));
                if ((m.value > MATCH.nomatch))
                {
                    if ((numBaseClassMatches.value == 0))
                    {
                        memcpy((BytePtr)((best_ref.value.get()).tdata()), ((tmpdedtypes.value.get()).tdata()), ((tmpdedtypes.value.get()).length.value * 4));
                    }
                    else
                    {
                        IntRef k = ref(0);
                        for (; (k.value < (tmpdedtypes.value.get()).length.value);k.value += 1){
                            if ((!pequals((tmpdedtypes.value.get()).get(k.value), (best_ref.value.get()).get(k.value))))
                            {
                                best_ref.value.get().set(k.value, (dedtypes_ref.value.get()).get(k.value));
                            }
                        }
                    }
                    numBaseClassMatches.value += 1;
                }
            }
            {
                Ref<Slice<BaseClass>> __r1205 = ref(b.baseInterfaces.value.copy());
                IntRef __key1206 = ref(0);
                for (; (__key1206.value < __r1205.value.getLength());__key1206.value += 1) {
                    Ref<BaseClass> bi = ref(__r1205.value.get(__key1206.value).copy());
                    deduceBaseClassParameters(bi, sc_ref.value, tparam_ref.value, parameters_ref.value, dedtypes_ref.value, best_ref.value, numBaseClassMatches);
                }
            }
        }

        public  void visit(TypeClass t) {
            Ref<TypeClass> t_ref = ref(t);
            Ref<TemplateInstance> ti = ref(t_ref.value.sym.value.parent.value.isTemplateInstance());
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tinstance))
            {
                if ((ti.value != null) && (pequals(ti.value.toAlias(), t_ref.value.sym.value)))
                {
                    Ref<TypeInstance> tx = ref(new TypeInstance(Loc.initial.value, ti.value));
                    IntRef m = ref(deduceType(tx.value, this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false));
                    if ((m.value != MATCH.nomatch))
                    {
                        this.result.value = m.value;
                        return ;
                    }
                }
                Ref<TypeInstance> tpi = ref((TypeInstance)this.tparam.value);
                if (tpi.value.idents.length.value != 0)
                {
                    Ref<RootObject> id = ref(tpi.value.idents.get(tpi.value.idents.length.value - 1));
                    if ((id.value.dyncast() == DYNCAST.identifier) && t_ref.value.sym.value.ident.value.equals((Identifier)id.value))
                    {
                        Ref<Type> tparent = ref(t_ref.value.sym.value.parent.value.getType());
                        if (tparent.value != null)
                        {
                            tpi.value.idents.length.value--;
                            this.result.value = deduceType(tparent.value, this.sc.value, tpi.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                            tpi.value.idents.length.value++;
                            return ;
                        }
                    }
                }
                this.visit((Type)t_ref);
                if ((this.result.value != MATCH.nomatch))
                {
                    return ;
                }
                IntRef numBaseClassMatches = ref(0);
                Ref<Ptr<DArray<RootObject>>> best = ref(refPtr(new DArray<RootObject>((this.dedtypes.value.get()).length.value)));
                Ref<ClassDeclaration> s = ref(t_ref.value.sym.value);
                for (; (s.value != null) && ((s.value.baseclasses.get()).length.value > 0);){
                    deduceBaseClassParameters((s.value.baseclasses.get()).get(0).get(), this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, best.value, numBaseClassMatches);
                    {
                        Ref<Slice<Ptr<BaseClass>>> __r1207 = ref(s.value.interfaces.value.copy());
                        IntRef __key1208 = ref(0);
                        for (; (__key1208.value < __r1207.value.getLength());__key1208.value += 1) {
                            Ref<Ptr<BaseClass>> b = ref(__r1207.value.get(__key1208.value));
                            deduceBaseClassParameters(b.value.get(), this.sc.value, this.tparam.value, this.parameters.value, this.dedtypes.value, best.value, numBaseClassMatches);
                        }
                    }
                    s.value = ((s.value.baseclasses.get()).get(0).get()).sym.value;
                }
                if ((numBaseClassMatches.value == 0))
                {
                    this.result.value = MATCH.nomatch;
                    return ;
                }
                memcpy((BytePtr)((this.dedtypes.value.get()).tdata()), ((best.value.get()).tdata()), ((best.value.get()).length.value * 4));
                this.result.value = MATCH.convert;
                return ;
            }
            if ((this.tparam.value != null) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                Ref<TypeClass> tp = ref((TypeClass)this.tparam.value);
                if ((this.wm.value != null) && (t_ref.value.deduceWild(this.tparam.value, false) != 0))
                {
                    this.result.value = MATCH.constant;
                    return ;
                }
                this.result.value = t_ref.value.implicitConvTo(tp.value);
                return ;
            }
            this.visit((Type)t_ref);
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            IntRef i = ref(templateParameterLookup(this.tparam.value, this.parameters.value));
            if ((i.value == 305419896) || (((TypeIdentifier)this.tparam.value).idents.length.value > 0))
            {
                if ((pequals(e_ref.value, emptyArrayElement.value)) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    Ref<Type> tn = ref(((TypeNext)this.tparam.value).next.value);
                    this.result.value = deduceType(emptyArrayElement.value, this.sc.value, tn.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
                    return ;
                }
                e_ref.value.type.value.accept(this);
                return ;
            }
            Ref<TemplateTypeParameter> tp = ref((this.parameters.value.get()).get(i.value).isTemplateTypeParameter());
            if (tp.value == null)
            {
                return ;
            }
            if ((pequals(e_ref.value, emptyArrayElement.value)))
            {
                if ((this.dedtypes.value.get()).get(i.value) != null)
                {
                    this.result.value = MATCH.exact;
                    return ;
                }
                if (tp.value.defaultType.value != null)
                {
                    tp.value.defaultType.value.accept(this);
                    return ;
                }
            }
            Function1<Type,Boolean> isTopRef = new Function1<Type,Boolean>(){
                public Boolean invoke(Type t) {
                    Type tb = t.baseElemOf();
                    return ((tb.ty.value & 0xFF) == ENUMTY.Tclass) || ((tb.ty.value & 0xFF) == ENUMTY.Taarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tstruct) && tb.hasPointers();
                }
            };
            Ref<Type> at = ref((Type)(this.dedtypes.value.get()).get(i.value));
            Ref<Type> tt = ref(null);
            {
                Ref<Byte> wx = ref(deduceWildHelper(e_ref.value.type.value, ptr(tt), this.tparam.value));
                if ((wx.value) != 0)
                {
                    this.wm.value.set(0, this.wm.value.get() | (wx.value & 0xFF));
                    this.result.value = MATCH.constant;
                }
                else {
                    IntRef m = ref(deduceTypeHelper(e_ref.value.type.value, ptr(tt), this.tparam.value));
                    if ((m.value) != 0)
                    {
                        this.result.value = m.value;
                    }
                    else if (!isTopRef.invoke(e_ref.value.type.value))
                    {
                        tt.value = e_ref.value.type.value.mutableOf();
                        this.result.value = MATCH.convert;
                    }
                    else
                    {
                        return ;
                    }
                }
            }
            if (at.value == null)
            {
                this.dedtypes.value.get().set(i.value, new TypeDeduced(tt.value, e_ref.value, this.tparam.value));
                return ;
            }
            Ref<TypeDeduced> xt = ref(null);
            if (((at.value.ty.value & 0xFF) == ENUMTY.Tnone))
            {
                xt.value = (TypeDeduced)at.value;
                at.value = xt.value.tded.value;
            }
            IntRef match1 = ref(xt.value != null ? xt.value.matchAll(tt.value) : MATCH.nomatch);
            Ref<Type> pt = ref(at.value.addMod(this.tparam.value.mod.value));
            if (this.wm.value.get() != 0)
            {
                pt.value = pt.value.substWildTo(this.wm.value.get());
            }
            IntRef match2 = ref(e_ref.value.implicitConvTo(pt.value));
            if ((match1.value > MATCH.nomatch) && (match2.value > MATCH.nomatch))
            {
                if ((at.value.implicitConvTo(tt.value) <= MATCH.nomatch))
                {
                    match1.value = MATCH.nomatch;
                }
                else if ((tt.value.implicitConvTo(at.value) <= MATCH.nomatch))
                {
                    match2.value = MATCH.nomatch;
                }
                else if ((tt.value.isTypeBasic() != null) && ((tt.value.ty.value & 0xFF) == (at.value.ty.value & 0xFF)) && ((tt.value.mod.value & 0xFF) != (at.value.mod.value & 0xFF)))
                {
                    if (!tt.value.isMutable() && !at.value.isMutable())
                    {
                        tt.value = tt.value.mutableOf().addMod(MODmerge(tt.value.mod.value, at.value.mod.value));
                    }
                    else if (tt.value.isMutable())
                    {
                        if (((at.value.mod.value & 0xFF) == 0))
                        {
                            match1.value = MATCH.nomatch;
                        }
                        else
                        {
                            match2.value = MATCH.nomatch;
                        }
                    }
                    else if (at.value.isMutable())
                    {
                        if (((tt.value.mod.value & 0xFF) == 0))
                        {
                            match2.value = MATCH.nomatch;
                        }
                        else
                        {
                            match1.value = MATCH.nomatch;
                        }
                    }
                }
                else
                {
                    match1.value = MATCH.nomatch;
                    match2.value = MATCH.nomatch;
                }
            }
            if ((match1.value > MATCH.nomatch))
            {
                if (xt.value != null)
                {
                    xt.value.update(tt.value, e_ref.value, this.tparam.value);
                }
                else
                {
                    this.dedtypes.value.get().set(i.value, tt.value);
                }
                this.result.value = match1.value;
                return ;
            }
            if ((match2.value > MATCH.nomatch))
            {
                if (xt.value != null)
                {
                    xt.value.update(e_ref.value, this.tparam.value);
                }
                this.result.value = match2.value;
                return ;
            }
            {
                Ref<Type> t = ref(rawTypeMerge(at.value, tt.value));
                if ((t.value) != null)
                {
                    if (xt.value != null)
                    {
                        xt.value.update(t.value, e_ref.value, this.tparam.value);
                    }
                    else
                    {
                        this.dedtypes.value.get().set(i.value, t.value);
                    }
                    pt.value = tt.value.addMod(this.tparam.value.mod.value);
                    if (this.wm.value.get() != 0)
                    {
                        pt.value = pt.value.substWildTo(this.wm.value.get());
                    }
                    this.result.value = e_ref.value.implicitConvTo(pt.value);
                    return ;
                }
            }
            this.result.value = MATCH.nomatch;
        }

        public  int deduceEmptyArrayElement() {
            if (emptyArrayElement.value == null)
            {
                emptyArrayElement.value = new IdentifierExp(Loc.initial.value, Id.p.value);
                emptyArrayElement.value.type.value = Type.tvoid.value;
            }
            assert(((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tarray));
            Ref<Type> tn = ref(((TypeNext)this.tparam.value).next.value);
            return deduceType(emptyArrayElement.value, this.sc.value, tn.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false);
        }

        public  void visit(NullExp e) {
            Ref<NullExp> e_ref = ref(e);
            if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tnull))
            {
                this.result.value = this.deduceEmptyArrayElement();
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(StringExp e) {
            Ref<StringExp> e_ref = ref(e);
            Type taai = null;
            if (((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Taarray) && (((taai = ((TypeAArray)this.tparam.value).index.value).ty.value & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)taai).idents.length.value == 0))
            {
                e_ref.value.type.value.nextOf().sarrayOf((long)e_ref.value.len.value).accept(this);
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            if ((e_ref.value.elements.value == null) || ((e_ref.value.elements.value.get()).length.value == 0) && ((e_ref.value.type.value.toBasetype().nextOf().ty.value & 0xFF) == ENUMTY.Tvoid) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                this.result.value = this.deduceEmptyArrayElement();
                return ;
            }
            if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tarray) && (e_ref.value.elements.value != null) && ((e_ref.value.elements.value.get()).length.value != 0))
            {
                Ref<Type> tn = ref(((TypeDArray)this.tparam.value).next.value);
                this.result.value = MATCH.exact;
                if (e_ref.value.basis.value != null)
                {
                    IntRef m = ref(deduceType(e_ref.value.basis.value, this.sc.value, tn.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false));
                    if ((m.value < this.result.value))
                    {
                        this.result.value = m.value;
                    }
                }
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.elements.value.get()).length.value);i.value++){
                        if ((this.result.value <= MATCH.nomatch))
                        {
                            break;
                        }
                        Ref<Expression> el = ref((e_ref.value.elements.value.get()).get(i.value));
                        if (el.value == null)
                        {
                            continue;
                        }
                        IntRef m = ref(deduceType(el.value, this.sc.value, tn.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false));
                        if ((m.value < this.result.value))
                        {
                            this.result.value = m.value;
                        }
                    }
                }
                return ;
            }
            Type taai = null;
            if (((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Taarray) && (((taai = ((TypeAArray)this.tparam.value).index.value).ty.value & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)taai).idents.length.value == 0))
            {
                e_ref.value.type.value.nextOf().sarrayOf((long)(e_ref.value.elements.value.get()).length.value).accept(this);
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(AssocArrayLiteralExp e) {
            Ref<AssocArrayLiteralExp> e_ref = ref(e);
            if (((this.tparam.value.ty.value & 0xFF) == ENUMTY.Taarray) && (e_ref.value.keys.value != null) && ((e_ref.value.keys.value.get()).length.value != 0))
            {
                TypeAArray taa = (TypeAArray)this.tparam.value;
                this.result.value = MATCH.exact;
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.keys.value.get()).length.value);i.value++){
                        IntRef m1 = ref(deduceType((e_ref.value.keys.value.get()).get(i.value), this.sc.value, taa.index.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false));
                        if ((m1.value < this.result.value))
                        {
                            this.result.value = m1.value;
                        }
                        if ((this.result.value <= MATCH.nomatch))
                        {
                            break;
                        }
                        IntRef m2 = ref(deduceType((e_ref.value.values.value.get()).get(i.value), this.sc.value, taa.next.value, this.parameters.value, this.dedtypes.value, this.wm.value, 0, false));
                        if ((m2.value < this.result.value))
                        {
                            this.result.value = m2.value;
                        }
                        if ((this.result.value <= MATCH.nomatch))
                        {
                            break;
                        }
                    }
                }
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(FuncExp e) {
            if (e.td.value != null)
            {
                Type to = this.tparam.value;
                if ((to.nextOf() == null) || ((to.nextOf().ty.value & 0xFF) != ENUMTY.Tfunction))
                {
                    return ;
                }
                TypeFunction tof = (TypeFunction)to.nextOf();
                assert(e.td.value._scope.value != null);
                TypeFunction tf = (TypeFunction)e.fd.value.type.value;
                IntRef dim = ref(tf.parameterList.length());
                if ((tof.parameterList.length() != dim.value) || (tof.parameterList.varargs.value != tf.parameterList.varargs.value))
                {
                    return ;
                }
                Ref<Ptr<DArray<RootObject>>> tiargs = ref(refPtr(new DArray<RootObject>()));
                (tiargs.value.get()).reserve((e.td.value.parameters.get()).length.value);
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e.td.value.parameters.get()).length.value);i.value++){
                        TemplateParameter tp = (e.td.value.parameters.get()).get(i.value);
                        IntRef u = ref(0);
                        for (; (u.value < dim.value);u.value++){
                            Parameter p = tf.parameterList.get(u.value);
                            if (((p.type.value.ty.value & 0xFF) == ENUMTY.Tident) && (pequals(((TypeIdentifier)p.type.value).ident.value, tp.ident.value)))
                            {
                                break;
                            }
                        }
                        assert((u.value < dim.value));
                        Ref<Parameter> pto = ref(tof.parameterList.get(u.value));
                        if (pto.value == null)
                        {
                            break;
                        }
                        Ref<Type> t = ref(pto.value.type.value.syntaxCopy());
                        if (reliesOnTemplateParameters(t.value, (this.parameters.value.get()).opSlice(this.inferStart.value, (this.parameters.value.get()).length.value)))
                        {
                            return ;
                        }
                        t.value = typeSemantic(t.value, e.loc.value, this.sc.value);
                        if (((t.value.ty.value & 0xFF) == ENUMTY.Terror))
                        {
                            return ;
                        }
                        (tiargs.value.get()).push(t.value);
                    }
                }
                if ((tf.next.value == null) && (tof.next.value != null))
                {
                    e.fd.value.treq.value = this.tparam.value;
                }
                Ref<TemplateInstance> ti = ref(new TemplateInstance(e.loc.value, e.td.value, tiargs.value));
                Expression ex = expressionSemantic(new ScopeExp(e.loc.value, ti.value), e.td.value._scope.value);
                e.fd.value.treq.value = null;
                if (((ex.op.value & 0xFF) == 127))
                {
                    return ;
                }
                if (((ex.op.value & 0xFF) != 161))
                {
                    return ;
                }
                this.visit(ex.type.value);
                return ;
            }
            Ref<Type> t = ref(e.type.value);
            if (((t.value.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                return ;
            }
            if (((e.tok.value & 0xFF) == 0) && ((t.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
            {
                Ref<TypeFunction> tf = ref((TypeFunction)t.value.nextOf());
                t.value = merge(new TypeDelegate(tf.value));
            }
            this.visit(t.value);
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            Type taai = null;
            if (((e_ref.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((this.tparam.value.ty.value & 0xFF) == ENUMTY.Taarray) && (((taai = ((TypeAArray)this.tparam.value).index.value).ty.value & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)taai).idents.length.value == 0))
            {
                {
                    Ref<Type> tsa = ref(toStaticArrayType(e_ref.value));
                    if ((tsa.value) != null)
                    {
                        tsa.value.accept(this);
                        return ;
                    }
                }
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(CommaExp e) {
            e.e2.value.accept(this);
        }


        public DeduceType() {}
    }
    private static class ReliesOnTemplateParameters extends Visitor
    {
        private Ref<Slice<TemplateParameter>> tparams = ref(new Slice<TemplateParameter>());
        private Ref<Boolean> result = ref(false);
        public  ReliesOnTemplateParameters(Slice<TemplateParameter> tparams) {
            Ref<Slice<TemplateParameter>> tparams_ref = ref(tparams);
            this.tparams.value = tparams_ref.value.copy();
        }

        public  void visit(Expression e) {
        }

        public  void visit(IdentifierExp e) {
            {
                Ref<Slice<TemplateParameter>> __r1219 = ref(this.tparams.value.copy());
                IntRef __key1220 = ref(0);
                for (; (__key1220.value < __r1219.value.getLength());__key1220.value += 1) {
                    TemplateParameter tp = __r1219.value.get(__key1220.value);
                    if ((pequals(e.ident.value, tp.ident.value)))
                    {
                        this.result.value = true;
                        return ;
                    }
                }
            }
        }

        public  void visit(TupleExp e) {
            if (e.exps.value != null)
            {
                {
                    Ref<Slice<Expression>> __r1221 = ref((e.exps.value.get()).opSlice().copy());
                    IntRef __key1222 = ref(0);
                    for (; (__key1222.value < __r1221.value.getLength());__key1222.value += 1) {
                        Expression ea = __r1221.value.get(__key1222.value);
                        ea.accept(this);
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
        }

        public  void visit(ArrayLiteralExp e) {
            if (e.elements.value != null)
            {
                {
                    Ref<Slice<Expression>> __r1223 = ref((e.elements.value.get()).opSlice().copy());
                    IntRef __key1224 = ref(0);
                    for (; (__key1224.value < __r1223.value.getLength());__key1224.value += 1) {
                        Expression el = __r1223.value.get(__key1224.value);
                        el.accept(this);
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
        }

        public  void visit(AssocArrayLiteralExp e) {
            {
                Ref<Slice<Expression>> __r1225 = ref((e.keys.value.get()).opSlice().copy());
                IntRef __key1226 = ref(0);
                for (; (__key1226.value < __r1225.value.getLength());__key1226.value += 1) {
                    Expression ek = __r1225.value.get(__key1226.value);
                    ek.accept(this);
                    if (this.result.value)
                    {
                        return ;
                    }
                }
            }
            {
                Ref<Slice<Expression>> __r1227 = ref((e.values.value.get()).opSlice().copy());
                IntRef __key1228 = ref(0);
                for (; (__key1228.value < __r1227.value.getLength());__key1228.value += 1) {
                    Expression ev = __r1227.value.get(__key1228.value);
                    ev.accept(this);
                    if (this.result.value)
                    {
                        return ;
                    }
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            if (e.elements.value != null)
            {
                {
                    Ref<Slice<Expression>> __r1229 = ref((e.elements.value.get()).opSlice().copy());
                    IntRef __key1230 = ref(0);
                    for (; (__key1230.value < __r1229.value.getLength());__key1230.value += 1) {
                        Expression ea = __r1229.value.get(__key1230.value);
                        ea.accept(this);
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
        }

        public  void visit(TypeExp e) {
            this.result.value = reliesOnTemplateParameters(e.type.value, this.tparams.value);
        }

        public  void visit(NewExp e) {
            if (e.thisexp.value != null)
            {
                e.thisexp.value.accept(this);
            }
            if (!this.result.value && (e.newargs.value != null))
            {
                {
                    Ref<Slice<Expression>> __r1231 = ref((e.newargs.value.get()).opSlice().copy());
                    IntRef __key1232 = ref(0);
                    for (; (__key1232.value < __r1231.value.getLength());__key1232.value += 1) {
                        Expression ea = __r1231.value.get(__key1232.value);
                        ea.accept(this);
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
            this.result.value = reliesOnTemplateParameters(e.newtype.value, this.tparams.value);
            if (!this.result.value && (e.arguments.value != null))
            {
                {
                    Ref<Slice<Expression>> __r1233 = ref((e.arguments.value.get()).opSlice().copy());
                    IntRef __key1234 = ref(0);
                    for (; (__key1234.value < __r1233.value.getLength());__key1234.value += 1) {
                        Expression ea = __r1233.value.get(__key1234.value);
                        ea.accept(this);
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
        }

        public  void visit(NewAnonClassExp e) {
            this.result.value = true;
        }

        public  void visit(FuncExp e) {
            this.result.value = true;
        }

        public  void visit(TypeidExp e) {
            {
                Ref<Expression> ea = ref(isExpression(e.obj.value));
                if ((ea.value) != null)
                {
                    ea.value.accept(this);
                }
                else {
                    Ref<Type> ta = ref(isType(e.obj.value));
                    if ((ta.value) != null)
                    {
                        this.result.value = reliesOnTemplateParameters(ta.value, this.tparams.value);
                    }
                }
            }
        }

        public  void visit(TraitsExp e) {
            if (e.args.value != null)
            {
                {
                    Ref<Slice<RootObject>> __r1235 = ref((e.args.value.get()).opSlice().copy());
                    IntRef __key1236 = ref(0);
                    for (; (__key1236.value < __r1235.value.getLength());__key1236.value += 1) {
                        Ref<RootObject> oa = ref(__r1235.value.get(__key1236.value));
                        {
                            Ref<Expression> ea = ref(isExpression(oa.value));
                            if ((ea.value) != null)
                            {
                                ea.value.accept(this);
                            }
                            else {
                                Ref<Type> ta = ref(isType(oa.value));
                                if ((ta.value) != null)
                                {
                                    this.result.value = reliesOnTemplateParameters(ta.value, this.tparams.value);
                                }
                            }
                        }
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
        }

        public  void visit(IsExp e) {
            this.result.value = reliesOnTemplateParameters(e.targ.value, this.tparams.value);
        }

        public  void visit(UnaExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DotTemplateInstanceExp e) {
            Ref<DotTemplateInstanceExp> e_ref = ref(e);
            this.visit((UnaExp)e_ref);
            if (!this.result.value && (e_ref.value.ti.tiargs.value != null))
            {
                {
                    Ref<Slice<RootObject>> __r1237 = ref((e_ref.value.ti.tiargs.value.get()).opSlice().copy());
                    IntRef __key1238 = ref(0);
                    for (; (__key1238.value < __r1237.value.getLength());__key1238.value += 1) {
                        Ref<RootObject> oa = ref(__r1237.value.get(__key1238.value));
                        {
                            Ref<Expression> ea = ref(isExpression(oa.value));
                            if ((ea.value) != null)
                            {
                                ea.value.accept(this);
                            }
                            else {
                                Ref<Type> ta = ref(isType(oa.value));
                                if ((ta.value) != null)
                                {
                                    this.result.value = reliesOnTemplateParameters(ta.value, this.tparams.value);
                                }
                            }
                        }
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            this.visit((UnaExp)e_ref);
            if (!this.result.value && (e_ref.value.arguments.value != null))
            {
                {
                    Ref<Slice<Expression>> __r1239 = ref((e_ref.value.arguments.value.get()).opSlice().copy());
                    IntRef __key1240 = ref(0);
                    for (; (__key1240.value < __r1239.value.getLength());__key1240.value += 1) {
                        Expression ea = __r1239.value.get(__key1240.value);
                        ea.accept(this);
                        if (this.result.value)
                        {
                            return ;
                        }
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            Ref<CastExp> e_ref = ref(e);
            this.visit((UnaExp)e_ref);
            if (!this.result.value && (e_ref.value.to.value != null))
            {
                this.result.value = reliesOnTemplateParameters(e_ref.value.to.value, this.tparams.value);
            }
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            this.visit((UnaExp)e_ref);
            if (!this.result.value && (e_ref.value.lwr.value != null))
            {
                e_ref.value.lwr.value.accept(this);
            }
            if (!this.result.value && (e_ref.value.upr.value != null))
            {
                e_ref.value.upr.value.accept(this);
            }
        }

        public  void visit(IntervalExp e) {
            e.lwr.value.accept(this);
            if (!this.result.value)
            {
                e.upr.value.accept(this);
            }
        }

        public  void visit(ArrayExp e) {
            Ref<ArrayExp> e_ref = ref(e);
            this.visit((UnaExp)e_ref);
            if (!this.result.value && (e_ref.value.arguments.value != null))
            {
                {
                    Ref<Slice<Expression>> __r1241 = ref((e_ref.value.arguments.value.get()).opSlice().copy());
                    IntRef __key1242 = ref(0);
                    for (; (__key1242.value < __r1241.value.getLength());__key1242.value += 1) {
                        Expression ea = __r1241.value.get(__key1242.value);
                        ea.accept(this);
                    }
                }
            }
        }

        public  void visit(BinExp e) {
            e.e1.value.accept(this);
            if (!this.result.value)
            {
                e.e2.value.accept(this);
            }
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            e_ref.value.econd.value.accept(this);
            if (!this.result.value)
            {
                this.visit((BinExp)e_ref);
            }
        }


        public ReliesOnTemplateParameters() {}
    }
    static int tryExpandMembersnest = 0;
    static int trySemantic3nest = 0;

    static boolean LOG = false;
    static int IDX_NOTFOUND = 305419896;
    public static Expression isExpression(RootObject o) {
        if ((o == null) || (o.dyncast() != DYNCAST.expression))
        {
            return null;
        }
        return (Expression)o;
    }

    public static Dsymbol isDsymbol(RootObject o) {
        if ((o == null) || (o.dyncast() != DYNCAST.dsymbol))
        {
            return null;
        }
        return (Dsymbol)o;
    }

    public static Type isType(RootObject o) {
        if ((o == null) || (o.dyncast() != DYNCAST.type))
        {
            return null;
        }
        return (Type)o;
    }

    public static Tuple isTuple(RootObject o) {
        if ((o == null) || (o.dyncast() != DYNCAST.tuple))
        {
            return null;
        }
        return (Tuple)o;
    }

    public static Parameter isParameter(RootObject o) {
        if ((o == null) || (o.dyncast() != DYNCAST.parameter))
        {
            return null;
        }
        return (Parameter)o;
    }

    public static TemplateParameter isTemplateParameter(RootObject o) {
        if ((o == null) || (o.dyncast() != DYNCAST.templateparameter))
        {
            return null;
        }
        return (TemplateParameter)o;
    }

    public static boolean isError(RootObject o) {
        {
            Type t = isType(o);
            if ((t) != null)
            {
                return (t.ty.value & 0xFF) == ENUMTY.Terror;
            }
        }
        {
            Expression e = isExpression(o);
            if ((e) != null)
            {
                return ((e.op.value & 0xFF) == 127) || (e.type.value == null) || ((e.type.value.ty.value & 0xFF) == ENUMTY.Terror);
            }
        }
        {
            Tuple v = isTuple(o);
            if ((v) != null)
            {
                return arrayObjectIsError(ptr(v.objects));
            }
        }
        Dsymbol s = isDsymbol(o);
        assert(s != null);
        if (s.errors.value)
        {
            return true;
        }
        return s.parent.value != null ? isError(s.parent.value) : false;
    }

    public static boolean arrayObjectIsError(Ptr<DArray<RootObject>> args) {
        {
            Slice<RootObject> __r1186 = (args.get()).opSlice().copy();
            int __key1187 = 0;
            for (; (__key1187 < __r1186.getLength());__key1187 += 1) {
                RootObject o = __r1186.get(__key1187);
                if (isError(o))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static Type getType(RootObject o) {
        Type t = isType(o);
        if (t == null)
        {
            {
                Expression e = isExpression(o);
                if ((e) != null)
                {
                    return e.type.value;
                }
            }
        }
        return t;
    }

    public static Dsymbol getDsymbol(RootObject oarg) {
        {
            Expression ea = isExpression(oarg);
            if ((ea) != null)
            {
                {
                    VarExp ve = ea.isVarExp();
                    if ((ve) != null)
                    {
                        return ve.var.value;
                    }
                    else {
                        FuncExp fe = ea.isFuncExp();
                        if ((fe) != null)
                        {
                            return fe.td.value != null ? fe.td.value : fe.fd.value;
                        }
                        else {
                            TemplateExp te = ea.isTemplateExp();
                            if ((te) != null)
                            {
                                return te.td.value;
                            }
                            else
                            {
                                return null;
                            }
                        }
                    }
                }
            }
            else
            {
                {
                    Type ta = isType(oarg);
                    if ((ta) != null)
                    {
                        return ta.toDsymbol(null);
                    }
                    else
                    {
                        return isDsymbol(oarg);
                    }
                }
            }
        }
    }

    public static Expression getValue(Ref<Dsymbol> s) {
        if (s.value != null)
        {
            {
                VarDeclaration v = s.value.isVarDeclaration();
                if ((v) != null)
                {
                    if ((v.storage_class.value & 8388608L) != 0)
                    {
                        return v.getConstInitializer(true);
                    }
                }
            }
        }
        return null;
    }

    public static Expression getValue(Expression e) {
        if ((e != null) && ((e.op.value & 0xFF) == 26))
        {
            VarDeclaration v = ((VarExp)e).var.value.isVarDeclaration();
            if ((v != null) && ((v.storage_class.value & 8388608L) != 0))
            {
                e = v.getConstInitializer(true);
            }
        }
        return e;
    }

    public static Expression getExpression(RootObject o) {
        Ref<Dsymbol> s = ref(isDsymbol(o));
        return s.value != null ? getValue(s) : getValue(isExpression(o));
    }

    public static boolean match(RootObject o1, RootObject o2) {
        boolean log = false;
        try {
            try {
                {
                    Type t1 = isType(o1);
                    if ((t1) != null)
                    {
                        Type t2 = isType(o2);
                        if (t2 == null)
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if (!t1.equals(t2))
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Expression e1 = getExpression(o1);
                    if ((e1) != null)
                    {
                        Expression e2 = getExpression(o2);
                        if (e2 == null)
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if (!e1.type.value.equals(e2.type.value) || !e1.equals(e2))
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Dsymbol s1 = isDsymbol(o1);
                    if ((s1) != null)
                    {
                        Dsymbol s2 = isDsymbol(o2);
                        if (s2 == null)
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if (!s1.equals(s2))
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if ((!pequals(s1.parent.value, s2.parent.value)) && (s1.isFuncDeclaration() == null) && (s2.isFuncDeclaration() == null))
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Tuple u1 = isTuple(o1);
                    if ((u1) != null)
                    {
                        Tuple u2 = isTuple(o2);
                        if (u2 == null)
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if (!arrayObjectMatch(ptr(u1.objects), ptr(u2.objects)))
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Lmatch:*/
            return true;
        }
        catch(Dispatch1 __d){}
    /*Lnomatch:*/
        return false;
    }

    public static boolean arrayObjectMatch(Ptr<DArray<RootObject>> oa1, Ptr<DArray<RootObject>> oa2) {
        if ((oa1 == oa2))
        {
            return true;
        }
        if (((oa1.get()).length.value != (oa2.get()).length.value))
        {
            return false;
        }
        int oa1dim = (oa1.get()).length.value;
        Ptr<RootObject> oa1d = pcopy((oa1.get()).data);
        Ptr<RootObject> oa2d = pcopy((oa2.get()).data);
        {
            int __key1188 = 0;
            int __limit1189 = oa1dim;
            for (; (__key1188 < __limit1189);__key1188 += 1) {
                int j = __key1188;
                RootObject o1 = oa1d.get(j);
                RootObject o2 = oa2d.get(j);
                if (!match(o1, o2))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static int arrayObjectHash(Ptr<DArray<RootObject>> oa1) {
        int hash = 0;
        {
            Slice<RootObject> __r1190 = (oa1.get()).opSlice().copy();
            int __key1191 = 0;
            for (; (__key1191 < __r1190.getLength());__key1191 += 1) {
                RootObject o1 = __r1190.get(__key1191);
                {
                    Type t1 = isType(o1);
                    if ((t1) != null)
                    {
                        hash = mixHash(hash, ((int)t1.deco.value));
                    }
                    else {
                        Expression e1 = getExpression(o1);
                        if ((e1) != null)
                        {
                            hash = mixHash(hash, expressionHash(e1));
                        }
                        else {
                            Dsymbol s1 = isDsymbol(o1);
                            if ((s1) != null)
                            {
                                FuncAliasDeclaration fa1 = s1.isFuncAliasDeclaration();
                                if (fa1 != null)
                                {
                                    s1 = fa1.toAliasFunc();
                                }
                                hash = mixHash(hash, mixHash(((int)s1.getIdent()), ((int)s1.parent.value)));
                            }
                            else {
                                Tuple u1 = isTuple(o1);
                                if ((u1) != null)
                                {
                                    hash = mixHash(hash, arrayObjectHash(ptr(u1.objects)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return hash;
    }

    public static int expressionHash(Expression e) {
        switch ((e.op.value & 0xFF))
        {
            case 135:
                return (int)((IntegerExp)e).getInteger();
            case 140:
                return CTFloat.hash(((RealExp)e).value);
            case 147:
                ComplexExp ce = (ComplexExp)e;
                return mixHash(CTFloat.hash(ce.toReal()), CTFloat.hash(ce.toImaginary()));
            case 120:
                return ((int)((IdentifierExp)e).ident.value);
            case 13:
                return ((int)((NullExp)e).type.value);
            case 121:
                StringExp se = (StringExp)e;
                return calcHash(se.string.value, se.len.value * (se.sz.value & 0xFF));
            case 126:
                TupleExp te = (TupleExp)e;
                int hash = 0;
                hash += te.e0.value != null ? expressionHash(te.e0.value) : 0;
                {
                    Slice<Expression> __r1192 = (te.exps.value.get()).opSlice().copy();
                    int __key1193 = 0;
                    for (; (__key1193 < __r1192.getLength());__key1193 += 1) {
                        Expression elem = __r1192.get(__key1193);
                        hash = mixHash(hash, expressionHash(elem));
                    }
                }
                return hash;
            case 47:
                ArrayLiteralExp ae = (ArrayLiteralExp)e;
                int hash_1 = 0;
                {
                    int __key1194 = 0;
                    int __limit1195 = (ae.elements.value.get()).length.value;
                    for (; (__key1194 < __limit1195);__key1194 += 1) {
                        int i = __key1194;
                        hash_1 = mixHash(hash_1, expressionHash(ae.getElement(i)));
                    }
                }
                return hash_1;
            case 48:
                AssocArrayLiteralExp ae_1 = (AssocArrayLiteralExp)e;
                int hash_2 = 0;
                {
                    int __key1196 = 0;
                    int __limit1197 = (ae_1.keys.value.get()).length.value;
                    for (; (__key1196 < __limit1197);__key1196 += 1) {
                        int i_1 = __key1196;
                        hash_2 ^= mixHash(expressionHash((ae_1.keys.value.get()).get(i_1)), expressionHash((ae_1.values.value.get()).get(i_1)));
                    }
                }
                return hash_2;
            case 49:
                StructLiteralExp se_1 = (StructLiteralExp)e;
                int hash_3 = 0;
                {
                    Slice<Expression> __r1198 = (se_1.elements.value.get()).opSlice().copy();
                    int __key1199 = 0;
                    for (; (__key1199 < __r1198.getLength());__key1199 += 1) {
                        Expression elem_1 = __r1198.get(__key1199);
                        hash_3 = mixHash(hash_3, elem_1 != null ? expressionHash(elem_1) : 0);
                    }
                }
                return hash_3;
            case 26:
                return ((int)((VarExp)e).var.value);
            case 161:
                return ((int)((FuncExp)e).fd.value);
            default:
            assert(((e.equals).funcptr == equals));
            return ((int)e);
        }
    }

    public static RootObject objectSyntaxCopy(RootObject o) {
        if (o == null)
        {
            return null;
        }
        {
            Type t = isType(o);
            if ((t) != null)
            {
                return t.syntaxCopy();
            }
        }
        {
            Expression e = isExpression(o);
            if ((e) != null)
            {
                return e.syntaxCopy();
            }
        }
        return o;
    }

    public static class Tuple extends RootObject
    {
        public Ref<DArray<RootObject>> objects = ref(new DArray<RootObject>());
        public  Tuple() {
            super();
        }

        public  Tuple(int numObjects) {
            super();
            this.objects.value.setDim(numObjects);
        }

        public  int dyncast() {
            return DYNCAST.tuple;
        }

        public  BytePtr toChars() {
            return this.objects.value.toChars();
        }


        public Tuple copy() {
            Tuple that = new Tuple();
            that.objects = this.objects;
            return that;
        }
    }
    public static class TemplatePrevious
    {
        public Ref<Ptr<TemplatePrevious>> prev = ref(null);
        public Ref<Ptr<Scope>> sc = ref(null);
        public Ref<Ptr<DArray<RootObject>>> dedargs = ref(null);
        public TemplatePrevious(){
        }
        public TemplatePrevious copy(){
            TemplatePrevious r = new TemplatePrevious();
            r.prev = prev;
            r.sc = sc;
            r.dedargs = dedargs;
            return r;
        }
        public TemplatePrevious(Ptr<TemplatePrevious> prev, Ptr<Scope> sc, Ptr<DArray<RootObject>> dedargs) {
            this.prev = prev;
            this.sc = sc;
            this.dedargs = dedargs;
        }

        public TemplatePrevious opAssign(TemplatePrevious that) {
            this.prev = that.prev;
            this.sc = that.sc;
            this.dedargs = that.dedargs;
            return this;
        }
    }
    public static class TemplateDeclaration extends ScopeDsymbol
    {
        public Ptr<DArray<TemplateParameter>> parameters = null;
        public Ref<Ptr<DArray<TemplateParameter>>> origParameters = ref(null);
        public Ref<Expression> constraint = ref(null);
        public AA<TemplateInstanceBox,TemplateInstance> instances = new AA<TemplateInstanceBox,TemplateInstance>();
        public Ref<TemplateDeclaration> overnext = ref(null);
        public Ref<TemplateDeclaration> overroot = ref(null);
        public FuncDeclaration funcroot = null;
        public Ref<Dsymbol> onemember = ref(null);
        public boolean literal = false;
        public boolean ismixin = false;
        public boolean isstatic = false;
        public Prot protection = new Prot();
        public IntRef inuse = ref(0);
        public Ref<Ptr<TemplatePrevious>> previous = ref(null);
        public  TemplateDeclaration(Loc loc, Identifier ident, Ptr<DArray<TemplateParameter>> parameters, Expression constraint, Ptr<DArray<Dsymbol>> decldefs, boolean ismixin, boolean literal) {
            super(loc, ident);
            this.parameters = parameters;
            this.origParameters.value = parameters;
            this.constraint.value = constraint;
            this.members.value = decldefs;
            this.literal = literal;
            this.ismixin = ismixin;
            this.isstatic = true;
            this.protection = new Prot(Prot.Kind.undefined);
            if ((this.members.value != null) && (ident != null))
            {
                Ref<Dsymbol> s = ref(null);
                if (Dsymbol.oneMembers(this.members.value, ptr(s), ident) && (s.value != null))
                {
                    this.onemember.value = s.value;
                    s.value.parent.value = this;
                }
            }
        }

        // defaulted all parameters starting with #7
        public  TemplateDeclaration(Loc loc, Identifier ident, Ptr<DArray<TemplateParameter>> parameters, Expression constraint, Ptr<DArray<Dsymbol>> decldefs, boolean ismixin) {
            this(loc, ident, parameters, constraint, decldefs, ismixin, false);
        }

        // defaulted all parameters starting with #6
        public  TemplateDeclaration(Loc loc, Identifier ident, Ptr<DArray<TemplateParameter>> parameters, Expression constraint, Ptr<DArray<Dsymbol>> decldefs) {
            this(loc, ident, parameters, constraint, decldefs, false, false);
        }

        public  Dsymbol syntaxCopy(Dsymbol _param_0) {
            Ptr<DArray<TemplateParameter>> p = null;
            if (this.parameters != null)
            {
                p = refPtr(new DArray<TemplateParameter>((this.parameters.get()).length.value));
                {
                    int i = 0;
                    for (; (i < (p.get()).length.value);i++) {
                        p.get().set(i, (this.parameters.get()).get(i).syntaxCopy());
                    }
                }
            }
            return new TemplateDeclaration(this.loc.value, this.ident.value, p, this.constraint.value != null ? this.constraint.value.syntaxCopy() : null, Dsymbol.arraySyntaxCopy(this.members.value), this.ismixin, this.literal);
        }

        public  boolean overloadInsert(Dsymbol s) {
            FuncDeclaration fd = s.isFuncDeclaration();
            if (fd != null)
            {
                if (this.funcroot != null)
                {
                    return this.funcroot.overloadInsert(fd);
                }
                this.funcroot = fd;
                return this.funcroot.overloadInsert(this);
            }
            TemplateDeclaration td = s.isTemplateDeclaration();
            if (td == null)
            {
                return false;
            }
            Ref<TemplateDeclaration> pthis = ref(this);
            Ptr<TemplateDeclaration> ptd = null;
            {
                ptd = pcopy(ptr(pthis));
                for (; ptd.get() != null;ptd = pcopy((ptr(ptd.get().overnext)))){
                }
            }
            td.overroot.value = this;
            ptd.set(0, td);
            return true;
        }

        public  boolean hasStaticCtorOrDtor() {
            return false;
        }

        public  BytePtr kind() {
            return (this.onemember.value != null) && (this.onemember.value.isAggregateDeclaration() != null) ? this.onemember.value.kind() : new BytePtr("template");
        }

        public  BytePtr toChars() {
            if (this.literal)
            {
                return this.toChars();
            }
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                Ref<HdrGenState> hgs = ref(new HdrGenState());
                buf.value.writestring(this.ident.value.asString());
                buf.value.writeByte(40);
                {
                    int i = 0;
                    for (; (i < (this.parameters.get()).length.value);i++){
                        TemplateParameter tp = (this.parameters.get()).get(i);
                        if (i != 0)
                        {
                            buf.value.writestring(new ByteSlice(", "));
                        }
                        toCBuffer(tp, ptr(buf), ptr(hgs));
                    }
                }
                buf.value.writeByte(41);
                if (this.onemember.value != null)
                {
                    FuncDeclaration fd = this.onemember.value.isFuncDeclaration();
                    if ((fd != null) && (fd.type.value != null))
                    {
                        TypeFunction tf = (TypeFunction)fd.type.value;
                        buf.value.writestring(parametersTypeToChars(tf.parameterList));
                    }
                }
                if (this.constraint.value != null)
                {
                    buf.value.writestring(new ByteSlice(" if ("));
                    toCBuffer(this.constraint.value, ptr(buf), ptr(hgs));
                    buf.value.writeByte(41);
                }
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  Prot prot() {
            return this.protection;
        }

        public  boolean evaluateConstraint(TemplateInstance ti, Ptr<Scope> sc, Ptr<Scope> paramscope, Ptr<DArray<RootObject>> dedargs, FuncDeclaration fd) {
            {
                Ptr<TemplatePrevious> p = this.previous.value;
                for (; p != null;p = (p.get()).prev.value){
                    if (arrayObjectMatch((p.get()).dedargs.value, dedargs))
                    {
                        {
                            Ptr<Scope> scx = sc;
                            for (; scx != null;scx = (scx.get()).enclosing.value){
                                if ((scx == (p.get()).sc.value))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            Ref<TemplatePrevious> pr = ref(new TemplatePrevious());
            pr.value.prev.value = this.previous.value;
            pr.value.sc.value = paramscope;
            pr.value.dedargs.value = dedargs;
            this.previous.value = ptr(pr);
            Ptr<Scope> scx = (paramscope.get()).push(ti);
            (scx.get()).parent.value = ti;
            (scx.get()).tinst = null;
            (scx.get()).minst.value = null;
            assert(ti.symtab == null);
            if (fd != null)
            {
                TypeFunction tf = (TypeFunction)fd.type.value;
                assert(((tf.ty.value & 0xFF) == ENUMTY.Tfunction));
                (scx.get()).parent.value = fd;
                Ptr<DArray<Parameter>> fparameters = tf.parameterList.parameters.value;
                int nfparams = tf.parameterList.length();
                {
                    int i = 0;
                    for (; (i < nfparams);i++){
                        Parameter fparam = tf.parameterList.get(i);
                        fparam.storageClass.value &= 2704291852L;
                        fparam.storageClass.value |= 32L;
                        if ((tf.parameterList.varargs.value == VarArg.typesafe) && (i + 1 == nfparams))
                        {
                            fparam.storageClass.value |= 65536L;
                        }
                    }
                }
                {
                    int i = 0;
                    for (; (i < (fparameters.get()).length.value);i++){
                        Parameter fparam = (fparameters.get()).get(i);
                        if (fparam.ident.value == null)
                        {
                            continue;
                        }
                        VarDeclaration v = new VarDeclaration(this.loc.value, fparam.type.value, fparam.ident.value, null, 0L);
                        v.storage_class.value = fparam.storageClass.value;
                        dsymbolSemantic(v, scx);
                        if (ti.symtab == null)
                        {
                            ti.symtab = new DsymbolTable();
                        }
                        if ((scx.get()).insert(v) == null)
                        {
                            this.error(new BytePtr("parameter `%s.%s` is already defined"), this.toChars(), v.toChars());
                        }
                        else
                        {
                            v.parent.value = fd;
                        }
                    }
                }
                if (this.isstatic)
                {
                    fd.storage_class.value |= 1L;
                }
                FuncDeclaration.HiddenParameters hiddenParams = fd.declareThis(scx, fd.isThis()).copy();
                fd.vthis.value = hiddenParams.vthis;
                fd.isThis2.value = hiddenParams.isThis2;
                fd.selectorParameter = hiddenParams.selectorParameter;
            }
            Expression e = this.constraint.value.syntaxCopy();
            assert((ti.inst.value == null));
            ti.inst.value = ti;
            (scx.get()).flags.value |= 16;
            Ref<Boolean> errors = ref(false);
            boolean result = evalStaticCondition(scx, this.constraint.value, e, errors);
            ti.inst.value = null;
            ti.symtab = null;
            scx = (scx.get()).pop();
            this.previous.value = pr.value.prev.value;
            if (errors.value)
            {
                return false;
            }
            return result;
        }

        public  Ptr<Scope> scopeForTemplateParameters(TemplateInstance ti, Ptr<Scope> sc) {
            ScopeDsymbol paramsym = new ScopeDsymbol();
            paramsym.parent.value = (this._scope.value.get()).parent.value;
            Ptr<Scope> paramscope = (this._scope.value.get()).push(paramsym);
            (paramscope.get()).tinst = ti;
            (paramscope.get()).minst.value = (sc.get()).minst.value;
            (paramscope.get()).callsc = sc;
            (paramscope.get()).stc.value = 0L;
            return paramscope;
        }

        public  int matchWithInstance(Ptr<Scope> sc, TemplateInstance ti, Ptr<DArray<RootObject>> dedtypes, Ptr<DArray<Expression>> fargs, int flag) {
            int LOGM = 0;
            int m = MATCH.nomatch;
            int dedtypes_dim = (dedtypes.get()).length.value;
            (dedtypes.get()).zero();
            if (this.errors.value)
            {
                return MATCH.nomatch;
            }
            int parameters_dim = (this.parameters.get()).length.value;
            int variadic = ((this.isVariadic() != null) ? 1 : 0);
            if (((ti.tiargs.value.get()).length.value > parameters_dim) && (variadic == 0))
            {
                return MATCH.nomatch;
            }
            assert((dedtypes_dim == parameters_dim));
            assert((dedtypes_dim >= (ti.tiargs.value.get()).length.value) || (variadic != 0));
            assert(this._scope.value != null);
            Ptr<Scope> paramscope = this.scopeForTemplateParameters(ti, sc);
            m = MATCH.exact;
            try {
                try {
                    {
                        int i = 0;
                    L_outer1:
                        for (; (i < dedtypes_dim);i++){
                            int m2 = MATCH.nomatch;
                            TemplateParameter tp = (this.parameters.get()).get(i);
                            Ref<Declaration> sparam = ref(null);
                            this.inuse.value++;
                            m2 = tp.matchArg(ti.loc.value, paramscope, ti.tiargs.value, i, this.parameters, dedtypes, ptr(sparam));
                            this.inuse.value--;
                            if ((m2 == MATCH.nomatch))
                            {
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            if ((m2 < m))
                            {
                                m = m2;
                            }
                            if (flag == 0)
                            {
                                dsymbolSemantic(sparam.value, paramscope);
                            }
                            if ((paramscope.get()).insert(sparam.value) == null)
                            {
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                        }
                    }
                    if (flag == 0)
                    {
                        {
                            int i = 0;
                            for (; (i < dedtypes_dim);i++){
                                if ((dedtypes.get()).get(i) == null)
                                {
                                    assert((i < (ti.tiargs.value.get()).length.value));
                                    dedtypes.get().set(i, (Type)(ti.tiargs.value.get()).get(i));
                                }
                            }
                        }
                    }
                    if ((m > MATCH.nomatch) && (this.constraint.value != null) && (flag == 0))
                    {
                        if (ti.hasNestedArgs(ti.tiargs.value, this.isstatic))
                        {
                            ti.parent.value = ti.enclosing.value;
                        }
                        else
                        {
                            ti.parent.value = this.parent.value;
                        }
                        FuncDeclaration fd = this.onemember.value != null ? this.onemember.value.isFuncDeclaration() : null;
                        if (fd != null)
                        {
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            TypeFunction tf = (TypeFunction)fd.type.value.syntaxCopy();
                            fd = new FuncDeclaration(fd.loc.value, fd.endloc.value, fd.ident.value, fd.storage_class.value, tf);
                            fd.parent.value = ti;
                            fd.inferRetType = true;
                            {
                                int i = 0;
                                for (; (i < (tf.parameterList.parameters.value.get()).length.value);i++) {
                                    (tf.parameterList.parameters.value.get()).get(i).defaultArg.value = null;
                                }
                            }
                            tf.next.value = null;
                            tf.incomplete.value = true;
                            tf.fargs.value = fargs;
                            int olderrors = global.startGagging();
                            fd.type.value = typeSemantic(tf, this.loc.value, paramscope);
                            if (global.endGagging(olderrors))
                            {
                                assert(((fd.type.value.ty.value & 0xFF) != ENUMTY.Tfunction));
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            fd.originalType.value = fd.type.value;
                        }
                        if (!this.evaluateConstraint(ti, sc, paramscope, dedtypes, fd))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                    /*goto Lret*/throw Dispatch1.INSTANCE;
                }
                catch(Dispatch0 __d){}
            /*Lnomatch:*/
                m = MATCH.nomatch;
            }
            catch(Dispatch1 __d){}
        /*Lret:*/
            (paramscope.get()).pop();
            return m;
        }

        public  int leastAsSpecialized(Ptr<Scope> sc, TemplateDeclaration td2, Ptr<DArray<Expression>> fargs) {
            int LOG_LEASTAS = 0;
            Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
            (tiargs.get()).reserve((this.parameters.get()).length.value);
            {
                int i = 0;
                for (; (i < (this.parameters.get()).length.value);i++){
                    TemplateParameter tp = (this.parameters.get()).get(i);
                    if (tp.dependent.value)
                    {
                        break;
                    }
                    RootObject p = ((RootObject)tp.dummyArg());
                    if (p == null)
                    {
                        break;
                    }
                    (tiargs.get()).push(p);
                }
            }
            TemplateInstance ti = new TemplateInstance(Loc.initial.value, this.ident.value, tiargs);
            Ref<DArray<RootObject>> dedtypes = ref(dedtypes.value = new DArray<RootObject>((td2.parameters.get()).length.value));
            try {
                int m = td2.matchWithInstance(sc, ti, ptr(dedtypes), fargs, 1);
                try {
                    if ((m > MATCH.nomatch))
                    {
                        TemplateTupleParameter tp = this.isVariadic();
                        if ((tp != null) && !tp.dependent.value && (td2.isVariadic() == null))
                        {
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
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

        public  int deduceFunctionTemplateMatch(TemplateInstance ti, Ptr<Scope> sc, Ref<FuncDeclaration> fd, Type tthis, Ptr<DArray<Expression>> fargs) {
            int nfparams = 0;
            int nfargs = 0;
            int ntargs = 0;
            int fptupindex = 305419896;
            int match = MATCH.exact;
            int matchTiargs = MATCH.exact;
            ParameterList fparameters = new ParameterList();
            int fvarargs = VarArg.none;
            int wildmatch = 0;
            int inferStart = 0;
            Loc instLoc = ti.loc.value.copy();
            Ptr<DArray<RootObject>> tiargs = ti.tiargs.value;
            Ptr<DArray<RootObject>> dedargs = refPtr(new DArray<RootObject>());
            Ptr<DArray<RootObject>> dedtypes = ptr(ti.tdtypes);
            assert(this._scope.value != null);
            (dedargs.get()).setDim((this.parameters.get()).length.value);
            (dedargs.get()).zero();
            (dedtypes.get()).setDim((this.parameters.get()).length.value);
            (dedtypes.get()).zero();
            if (this.errors.value || fd.value.errors.value)
            {
                return MATCH.nomatch;
            }
            Ptr<Scope> paramscope = this.scopeForTemplateParameters(ti, sc);
            TemplateTupleParameter tp = this.isVariadic();
            Tuple declaredTuple = null;
            ntargs = 0;
            try {
                try {
                    try {
                        if (tiargs != null)
                        {
                            ntargs = (tiargs.get()).length.value;
                            int n = (this.parameters.get()).length.value;
                            if (tp != null)
                            {
                                n--;
                            }
                            if ((ntargs > n))
                            {
                                if (tp == null)
                                {
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                }
                                Tuple t = new Tuple(ntargs - n);
                                assert((this.parameters.get()).length.value != 0);
                                dedargs.get().set((this.parameters.get()).length.value - 1, t);
                                {
                                    int i = 0;
                                    for (; (i < t.objects.value.length.value);i++){
                                        t.objects.value.set(i, (tiargs.get()).get(n + i));
                                    }
                                }
                                this.declareParameter(paramscope, tp, t);
                                declaredTuple = t;
                            }
                            else
                            {
                                n = ntargs;
                            }
                            memcpy((BytePtr)((dedargs.get()).tdata()), ((tiargs.get()).tdata()), (n * 4));
                            {
                                int i = 0;
                            L_outer2:
                                for (; (i < n);i++){
                                    assert((i < (this.parameters.get()).length.value));
                                    Ref<Declaration> sparam = ref(null);
                                    int m = (this.parameters.get()).get(i).matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, ptr(sparam));
                                    if ((m <= MATCH.nomatch))
                                    {
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    }
                                    if ((m < matchTiargs))
                                    {
                                        matchTiargs = m;
                                    }
                                    dsymbolSemantic(sparam.value, paramscope);
                                    if ((paramscope.get()).insert(sparam.value) == null)
                                    {
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    }
                                }
                            }
                            if ((n < (this.parameters.get()).length.value) && (declaredTuple == null))
                            {
                                inferStart = n;
                            }
                            else
                            {
                                inferStart = (this.parameters.get()).length.value;
                            }
                        }
                        fparameters = fd.value.getParameterList().copy();
                        nfparams = fparameters.length();
                        nfargs = fargs != null ? (fargs.get()).length.value : 0;
                        if (tp != null)
                        {
                            matchTiargs = MATCH.convert;
                            if ((nfparams == 0) && (nfargs != 0))
                            {
                                if (declaredTuple == null)
                                {
                                    Tuple t = new Tuple();
                                    dedargs.get().set((this.parameters.get()).length.value - 1, t);
                                    this.declareParameter(paramscope, tp, t);
                                    declaredTuple = t;
                                }
                            }
                            else
                            {
                                try {
                                    {
                                        fptupindex = 0;
                                    L_outer3:
                                        for (; (fptupindex < nfparams);fptupindex++){
                                            Parameter fparam = (fparameters.parameters.value.get()).get(fptupindex);
                                            if (((fparam.type.value.ty.value & 0xFF) != ENUMTY.Tident))
                                            {
                                                continue L_outer3;
                                            }
                                            TypeIdentifier tid = (TypeIdentifier)fparam.type.value;
                                            if (!tp.ident.value.equals(tid.ident.value) || (tid.idents.length.value != 0))
                                            {
                                                continue L_outer3;
                                            }
                                            if ((fparameters.varargs.value != VarArg.none))
                                            {
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                            /*goto L1*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                    fptupindex = 305419896;
                                }
                                catch(Dispatch0 __d){}
                            /*L1:*/
                            }
                        }
                        if ((this.toParent().isModule() != null) || (((this._scope.value.get()).stc.value & 1L) != 0))
                        {
                            tthis = null;
                        }
                        if (tthis != null)
                        {
                            boolean hasttp = false;
                            {
                                int i = 0;
                            L_outer4:
                                for (; (i < (this.parameters.get()).length.value);i++){
                                    TemplateThisParameter ttp = (this.parameters.get()).get(i).isTemplateThisParameter();
                                    if (ttp != null)
                                    {
                                        hasttp = true;
                                        Type t = new TypeIdentifier(Loc.initial.value, ttp.ident.value);
                                        int m = deduceType(tthis, paramscope, t, this.parameters, dedtypes, null, 0, false);
                                        if ((m <= MATCH.nomatch))
                                        {
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                        if ((m < match))
                                        {
                                            match = m;
                                        }
                                    }
                                }
                            }
                            if ((fd.value.type.value != null) && (fd.value.isCtorDeclaration() == null))
                            {
                                long stc = (this._scope.value.get()).stc.value | fd.value.storage_class2;
                                Dsymbol p = this.parent.value;
                                for (; (p.isTemplateDeclaration() != null) || (p.isTemplateInstance() != null);) {
                                    p = p.parent.value;
                                }
                                AggregateDeclaration ad = p.isAggregateDeclaration();
                                if (ad != null)
                                {
                                    stc |= ad.storage_class;
                                }
                                byte mod = fd.value.type.value.mod.value;
                                if ((stc & 1048576L) != 0)
                                {
                                    mod = (byte)4;
                                }
                                else
                                {
                                    if ((stc & 536871424L) != 0)
                                    {
                                        mod |= MODFlags.shared_;
                                    }
                                    if ((stc & 4L) != 0)
                                    {
                                        mod |= MODFlags.const_;
                                    }
                                    if ((stc & 2147483648L) != 0)
                                    {
                                        mod |= MODFlags.wild;
                                    }
                                }
                                byte thismod = tthis.mod.value;
                                if (hasttp)
                                {
                                    mod = MODmerge(thismod, mod);
                                }
                                int m = MODmethodConv(thismod, mod);
                                if ((m <= MATCH.nomatch))
                                {
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                }
                                if ((m < match))
                                {
                                    match = m;
                                }
                            }
                        }
                        {
                            int argi = 0;
                            int nfargs2 = nfargs;
                            {
                                int parami = 0;
                            L_outer5:
                                for (; (parami < nfparams);parami++){
                                    Parameter fparam = fparameters.get(parami);
                                    Type prmtype = fparam.type.value.addStorageClass(fparam.storageClass.value);
                                    Expression farg = null;
                                    if ((fptupindex != 305419896) && (parami == fptupindex))
                                    {
                                        assert(((prmtype.ty.value & 0xFF) == ENUMTY.Tident));
                                        TypeIdentifier tid = (TypeIdentifier)prmtype;
                                        if (declaredTuple == null)
                                        {
                                            declaredTuple = new Tuple();
                                            dedargs.get().set((this.parameters.get()).length.value - 1, declaredTuple);
                                            int rem = 0;
                                            {
                                                int j = parami + 1;
                                                for (; (j < nfparams);j++){
                                                    Parameter p = fparameters.get(j);
                                                    if (p.defaultArg.value != null)
                                                    {
                                                        break;
                                                    }
                                                    if (!reliesOnTemplateParameters(p.type.value, (this.parameters.get()).opSlice(inferStart, (this.parameters.get()).length.value)))
                                                    {
                                                        Type pt = typeSemantic(p.type.value.syntaxCopy(), fd.value.loc.value, paramscope);
                                                        rem += ((pt.ty.value & 0xFF) == ENUMTY.Ttuple) ? (((TypeTuple)pt).arguments.value.get()).length.value : 1;
                                                    }
                                                    else
                                                    {
                                                        rem += 1;
                                                    }
                                                }
                                            }
                                            if ((nfargs2 - argi < rem))
                                            {
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                            declaredTuple.objects.value.setDim(nfargs2 - argi - rem);
                                            {
                                                int i = 0;
                                            L_outer6:
                                                for (; (i < declaredTuple.objects.value.length.value);i++){
                                                    farg = (fargs.get()).get(argi + i);
                                                    if (((farg.op.value & 0xFF) == 127) || ((farg.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                                                    {
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    if (((fparam.storageClass.value & 8192L) == 0) && ((farg.type.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                                                    {
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    Ref<Type> tt = ref(null);
                                                    int m = MATCH.nomatch;
                                                    {
                                                        byte wm = deduceWildHelper(farg.type.value, ptr(tt), tid);
                                                        if ((wm) != 0)
                                                        {
                                                            wildmatch |= (wm & 0xFF);
                                                            m = MATCH.constant;
                                                        }
                                                        else
                                                        {
                                                            m = deduceTypeHelper(farg.type.value, ptr(tt), tid);
                                                        }
                                                    }
                                                    if ((m <= MATCH.nomatch))
                                                    {
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    if ((m < match))
                                                    {
                                                        match = m;
                                                    }
                                                    if (((tt.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((tt.value.ty.value & 0xFF) == ENUMTY.Tpointer) && !tt.value.isMutable() && ((fparam.storageClass.value & 2097152L) == 0) || ((fparam.storageClass.value & 256L) != 0) && !farg.isLvalue())
                                                    {
                                                        tt.value = tt.value.mutableOf();
                                                    }
                                                    declaredTuple.objects.value.set(i, tt.value);
                                                }
                                            }
                                            this.declareParameter(paramscope, tp, declaredTuple);
                                        }
                                        else
                                        {
                                            {
                                                int i = 0;
                                            L_outer7:
                                                for (; (i < declaredTuple.objects.value.length.value);i++){
                                                    if (isType(declaredTuple.objects.value.get(i)) == null)
                                                    {
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                }
                                            }
                                        }
                                        assert(declaredTuple != null);
                                        argi += declaredTuple.objects.value.length.value;
                                        continue L_outer5;
                                    }
                                    try {
                                        if (!reliesOnTemplateParameters(prmtype, (this.parameters.get()).opSlice(inferStart, (this.parameters.get()).length.value)))
                                        {
                                            prmtype = typeSemantic(prmtype.syntaxCopy(), fd.value.loc.value, paramscope);
                                            if (((prmtype.ty.value & 0xFF) == ENUMTY.Ttuple))
                                            {
                                                TypeTuple tt = (TypeTuple)prmtype;
                                                int tt_dim = (tt.arguments.value.get()).length.value;
                                                {
                                                    int j = 0;
                                                L_outer8:
                                                    for (; (j < tt_dim);comma(j++, argi += 1)){
                                                        Parameter p = (tt.arguments.value.get()).get(j);
                                                        if ((j == tt_dim - 1) && (fparameters.varargs.value == VarArg.typesafe) && (parami + 1 == nfparams) && (argi < nfargs))
                                                        {
                                                            prmtype = p.type.value;
                                                            /*goto Lvarargs*/throw Dispatch0.INSTANCE;
                                                        }
                                                        if ((argi >= nfargs))
                                                        {
                                                            if (p.defaultArg.value != null)
                                                            {
                                                                continue L_outer8;
                                                            }
                                                            if (fparam.defaultArg.value != null)
                                                            {
                                                                break;
                                                            }
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                        farg = (fargs.get()).get(argi);
                                                        if (farg.implicitConvTo(p.type.value) == 0)
                                                        {
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                    }
                                                }
                                                continue L_outer5;
                                            }
                                        }
                                        if ((argi >= nfargs))
                                        {
                                            if (fparam.defaultArg.value == null)
                                            {
                                                /*goto Lvarargs*/throw Dispatch0.INSTANCE;
                                            }
                                            if ((argi == nfargs))
                                            {
                                                {
                                                    int i = 0;
                                                    for (; (i < (dedtypes.get()).length.value);i++){
                                                        Type at = isType((dedtypes.get()).get(i));
                                                        if ((at != null) && ((at.ty.value & 0xFF) == ENUMTY.Tnone))
                                                        {
                                                            TypeDeduced xt = (TypeDeduced)at;
                                                            dedtypes.get().set(i, xt.tded.value);
                                                        }
                                                    }
                                                }
                                                {
                                                    int i = ntargs;
                                                L_outer9:
                                                    for (; (i < (dedargs.get()).length.value);i++){
                                                        TemplateParameter tparam = (this.parameters.get()).get(i);
                                                        RootObject oarg = (dedargs.get()).get(i);
                                                        RootObject oded = (dedtypes.get()).get(i);
                                                        if (oarg == null)
                                                        {
                                                            if (oded != null)
                                                            {
                                                                if ((tparam.specialization() != null) || (tparam.isTemplateTypeParameter() == null))
                                                                {
                                                                    dedargs.get().set(i, oded);
                                                                    int m2 = tparam.matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, null);
                                                                    if ((m2 <= MATCH.nomatch))
                                                                    {
                                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                                    }
                                                                    if ((m2 < matchTiargs))
                                                                    {
                                                                        matchTiargs = m2;
                                                                    }
                                                                    if (!(dedtypes.get()).get(i).equals(oded))
                                                                    {
                                                                        this.error(new BytePtr("specialization not allowed for deduced parameter `%s`"), tparam.ident.value.toChars());
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    if ((MATCH.convert < matchTiargs))
                                                                    {
                                                                        matchTiargs = MATCH.convert;
                                                                    }
                                                                }
                                                                dedargs.get().set(i, this.declareParameter(paramscope, tparam, oded));
                                                            }
                                                            else
                                                            {
                                                                this.inuse.value++;
                                                                oded = tparam.defaultArg(instLoc, paramscope);
                                                                this.inuse.value--;
                                                                if (oded != null)
                                                                {
                                                                    dedargs.get().set(i, this.declareParameter(paramscope, tparam, oded));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            nfargs2 = argi + 1;
                                            if ((prmtype.deco.value != null) || (prmtype.syntaxCopy().trySemantic(this.loc.value, paramscope) != null))
                                            {
                                                argi += 1;
                                                continue L_outer5;
                                            }
                                            farg = fparam.defaultArg.value.syntaxCopy();
                                            farg = expressionSemantic(farg, paramscope);
                                            farg = resolveProperties(paramscope, farg);
                                        }
                                        else
                                        {
                                            farg = (fargs.get()).get(argi);
                                        }
                                        {
                                            if (((farg.op.value & 0xFF) == 127) || ((farg.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                                            {
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                            Type att = null;
                                            while(true) try {
                                            /*Lretry:*/
                                                Type argtype = farg.type.value;
                                                if (((fparam.storageClass.value & 8192L) == 0) && ((argtype.ty.value & 0xFF) == ENUMTY.Tvoid) && ((farg.op.value & 0xFF) != 161))
                                                {
                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                }
                                                farg = farg.optimize(0, (fparam.storageClass.value & 2101248L) != 0L);
                                                RootObject oarg = farg;
                                                if (((fparam.storageClass.value & 2097152L) != 0) && ((fparam.storageClass.value & 256L) == 0) || farg.isLvalue())
                                                {
                                                    Type taai = null;
                                                    if (((argtype.ty.value & 0xFF) == ENUMTY.Tarray) && ((prmtype.ty.value & 0xFF) == ENUMTY.Tsarray) || ((prmtype.ty.value & 0xFF) == ENUMTY.Taarray) && (((taai = ((TypeAArray)prmtype).index.value).ty.value & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)taai).idents.length.value == 0))
                                                    {
                                                        if (((farg.op.value & 0xFF) == 121))
                                                        {
                                                            StringExp se = (StringExp)farg;
                                                            argtype = se.type.value.nextOf().sarrayOf((long)se.len.value);
                                                        }
                                                        else if (((farg.op.value & 0xFF) == 47))
                                                        {
                                                            ArrayLiteralExp ae = (ArrayLiteralExp)farg;
                                                            argtype = ae.type.value.nextOf().sarrayOf((long)(ae.elements.value.get()).length.value);
                                                        }
                                                        else if (((farg.op.value & 0xFF) == 31))
                                                        {
                                                            SliceExp se = (SliceExp)farg;
                                                            {
                                                                Type tsa = toStaticArrayType(se);
                                                                if ((tsa) != null)
                                                                {
                                                                    argtype = tsa;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    oarg = argtype;
                                                }
                                                else if (((fparam.storageClass.value & 4096L) == 0L) && ((argtype.ty.value & 0xFF) == ENUMTY.Tarray) || ((argtype.ty.value & 0xFF) == ENUMTY.Tpointer) && (templateParameterLookup(prmtype, this.parameters) != 305419896) && (((TypeIdentifier)prmtype).idents.length.value == 0))
                                                {
                                                    Type ta = argtype.castMod(prmtype.mod.value != 0 ? (byte)(argtype.nextOf().mod.value & 0xFF) : (byte)0);
                                                    if ((!pequals(ta, argtype)))
                                                    {
                                                        Expression ea = farg.copy();
                                                        ea.type.value = ta;
                                                        oarg = ea;
                                                    }
                                                }
                                                if ((fparameters.varargs.value == VarArg.typesafe) && (parami + 1 == nfparams) && (argi + 1 < nfargs))
                                                {
                                                    /*goto Lvarargs*/throw Dispatch0.INSTANCE;
                                                }
                                                IntRef wm = ref(0);
                                                int m = deduceType(oarg, paramscope, prmtype, this.parameters, dedtypes, ptr(wm), inferStart, false);
                                                wildmatch |= wm.value;
                                                if ((m == MATCH.nomatch) && (prmtype.deco.value != null))
                                                {
                                                    m = farg.implicitConvTo(prmtype);
                                                }
                                                if ((m == MATCH.nomatch))
                                                {
                                                    AggregateDeclaration ad = isAggregate(farg.type.value);
                                                    if ((ad != null) && (ad.aliasthis.value != null) && (!pequals(argtype, att)))
                                                    {
                                                        if ((att == null) && argtype.checkAliasThisRec())
                                                        {
                                                            att = argtype;
                                                        }
                                                        {
                                                            Expression e = resolveAliasThis(sc, farg, true);
                                                            if ((e) != null)
                                                            {
                                                                farg = e;
                                                                /*goto Lretry*/throw Dispatch0.INSTANCE;
                                                            }
                                                        }
                                                    }
                                                }
                                                if ((m > MATCH.nomatch) && ((fparam.storageClass.value & 2097408L) == 2097152L))
                                                {
                                                    if (!farg.isLvalue())
                                                    {
                                                        if (((farg.op.value & 0xFF) == 121) || ((farg.op.value & 0xFF) == 31) && ((prmtype.ty.value & 0xFF) == ENUMTY.Tsarray) || ((prmtype.ty.value & 0xFF) == ENUMTY.Taarray))
                                                        {
                                                        }
                                                        else
                                                        {
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                    }
                                                }
                                                if ((m > MATCH.nomatch) && ((fparam.storageClass.value & 4096L) != 0))
                                                {
                                                    if (!farg.isLvalue())
                                                    {
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    if (!farg.type.value.isMutable())
                                                    {
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                }
                                                if ((m == MATCH.nomatch) && ((fparam.storageClass.value & 8192L) != 0) && ((prmtype.ty.value & 0xFF) == ENUMTY.Tvoid) && ((farg.type.value.ty.value & 0xFF) != ENUMTY.Tvoid))
                                                {
                                                    m = MATCH.convert;
                                                }
                                                if ((m != MATCH.nomatch))
                                                {
                                                    if ((m < match))
                                                    {
                                                        match = m;
                                                    }
                                                    argi++;
                                                    continue L_outer5;
                                                }
                                                break;
                                            } catch(Dispatch0 __d){}
                                        }
                                    }
                                    catch(Dispatch0 __d){}
                                /*Lvarargs:*/
                                    if (!((fparameters.varargs.value == VarArg.typesafe) && (parami + 1 == nfparams)))
                                    {
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    }
                                    Type tb = prmtype.toBasetype();
                                    {
                                        int __dispatch1 = 0;
                                        dispatched_1:
                                        do {
                                            switch (__dispatch1 != 0 ? __dispatch1 : (tb.ty.value & 0xFF))
                                            {
                                                case 1:
                                                case 2:
                                                    if (((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                                                    {
                                                        TypeSArray tsa = (TypeSArray)tb;
                                                        long sz = tsa.dim.value.toInteger();
                                                        if ((sz != (long)(nfargs - argi)))
                                                        {
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                    }
                                                    else if (((tb.ty.value & 0xFF) == ENUMTY.Taarray))
                                                    {
                                                        TypeAArray taa = (TypeAArray)tb;
                                                        Expression dim = new IntegerExp(instLoc, (long)(nfargs - argi), Type.tsize_t.value);
                                                        int i = templateParameterLookup(taa.index.value, this.parameters);
                                                        if ((i == 305419896))
                                                        {
                                                            Ref<Expression> e = ref(null);
                                                            Ref<Type> t = ref(null);
                                                            Ref<Dsymbol> s = ref(null);
                                                            Ptr<Scope> sco = null;
                                                            int errors = global.startGagging();
                                                            sco = sc;
                                                            resolve(taa.index.value, instLoc, sco, ptr(e), ptr(t), ptr(s), false);
                                                            if (e.value == null)
                                                            {
                                                                sco = paramscope;
                                                                resolve(taa.index.value, instLoc, sco, ptr(e), ptr(t), ptr(s), false);
                                                            }
                                                            global.endGagging(errors);
                                                            if (e.value == null)
                                                            {
                                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                            }
                                                            e.value = e.value.ctfeInterpret();
                                                            e.value = e.value.implicitCastTo(sco, Type.tsize_t.value);
                                                            e.value = e.value.optimize(0, false);
                                                            if (!dim.equals(e.value))
                                                            {
                                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                            }
                                                        }
                                                        else
                                                        {
                                                            TemplateParameter tprm = (this.parameters.get()).get(i);
                                                            TemplateValueParameter tvp = tprm.isTemplateValueParameter();
                                                            if (tvp == null)
                                                            {
                                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                            }
                                                            Expression e_1 = (Expression)(dedtypes.get()).get(i);
                                                            if (e_1 != null)
                                                            {
                                                                if (!dim.equals(e_1))
                                                                {
                                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                                }
                                                            }
                                                            else
                                                            {
                                                                Type vt = typeSemantic(tvp.valType, Loc.initial.value, sc);
                                                                int m = dim.implicitConvTo(vt);
                                                                if ((m <= MATCH.nomatch))
                                                                {
                                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                                }
                                                                dedtypes.get().set(i, dim);
                                                            }
                                                        }
                                                    }
                                                    /*goto case*/{ __dispatch1 = 0; continue dispatched_1; }
                                                case 0:
                                                    __dispatch1 = 0;
                                                    TypeArray ta = (TypeArray)tb;
                                                    Type tret = fparam.isLazyArray();
                                                L_outer10:
                                                    for (; (argi < nfargs);argi++){
                                                        Expression arg = (fargs.get()).get(argi);
                                                        assert(arg != null);
                                                        int m_1 = MATCH.nomatch;
                                                        if (tret != null)
                                                        {
                                                            if (ta.next.value.equals(arg.type.value))
                                                            {
                                                                m_1 = MATCH.exact;
                                                            }
                                                            else
                                                            {
                                                                m_1 = arg.implicitConvTo(tret);
                                                                if ((m_1 == MATCH.nomatch))
                                                                {
                                                                    if (((tret.toBasetype().ty.value & 0xFF) == ENUMTY.Tvoid))
                                                                    {
                                                                        m_1 = MATCH.convert;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
                                                            IntRef wm = ref(0);
                                                            m_1 = deduceType(arg, paramscope, ta.next.value, this.parameters, dedtypes, ptr(wm), inferStart, false);
                                                            wildmatch |= wm.value;
                                                        }
                                                        if ((m_1 == MATCH.nomatch))
                                                        {
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                        if ((m_1 < match))
                                                        {
                                                            match = m_1;
                                                        }
                                                    }
                                                    /*goto Lmatch*/throw Dispatch0.INSTANCE;
                                                case 7:
                                                case 6:
                                                    /*goto Lmatch*/throw Dispatch0.INSTANCE;
                                                default:
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                        } while(__dispatch1 != 0);
                                    }
                                    throw new AssertionError("Unreachable code!");
                                }
                            }
                            if ((argi != nfargs2) && (fparameters.varargs.value == VarArg.none))
                            {
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            }
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lmatch:*/
                    {
                        int i = 0;
                        for (; (i < (dedtypes.get()).length.value);i++){
                            Type at = isType((dedtypes.get()).get(i));
                            if (at != null)
                            {
                                if (((at.ty.value & 0xFF) == ENUMTY.Tnone))
                                {
                                    TypeDeduced xt = (TypeDeduced)at;
                                    at = xt.tded.value;
                                }
                                dedtypes.get().set(i, at.merge2());
                            }
                        }
                    }
                    {
                        int i = ntargs;
                    L_outer11:
                        for (; (i < (dedargs.get()).length.value);i++){
                            TemplateParameter tparam = (this.parameters.get()).get(i);
                            RootObject oarg = (dedargs.get()).get(i);
                            RootObject oded = (dedtypes.get()).get(i);
                            if (oarg == null)
                            {
                                if (oded != null)
                                {
                                    if ((tparam.specialization() != null) || (tparam.isTemplateTypeParameter() == null))
                                    {
                                        dedargs.get().set(i, oded);
                                        int m2 = tparam.matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, null);
                                        if ((m2 <= MATCH.nomatch))
                                        {
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                        if ((m2 < matchTiargs))
                                        {
                                            matchTiargs = m2;
                                        }
                                        if (!(dedtypes.get()).get(i).equals(oded))
                                        {
                                            this.error(new BytePtr("specialization not allowed for deduced parameter `%s`"), tparam.ident.value.toChars());
                                        }
                                    }
                                    else
                                    {
                                        if ((MATCH.convert < matchTiargs))
                                        {
                                            matchTiargs = MATCH.convert;
                                        }
                                    }
                                }
                                else
                                {
                                    this.inuse.value++;
                                    oded = tparam.defaultArg(instLoc, paramscope);
                                    this.inuse.value--;
                                    if (oded == null)
                                    {
                                        if ((pequals(tparam, tp)) && (fptupindex == 305419896) && (ntargs <= (dedargs.get()).length.value - 1))
                                        {
                                            oded = new Tuple();
                                        }
                                        else
                                        {
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                    }
                                    if (isError(oded))
                                    {
                                        /*goto Lerror*/throw Dispatch2.INSTANCE;
                                    }
                                    ntargs++;
                                    if (tparam.specialization() != null)
                                    {
                                        dedargs.get().set(i, oded);
                                        int m2 = tparam.matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, null);
                                        if ((m2 <= MATCH.nomatch))
                                        {
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                        if ((m2 < matchTiargs))
                                        {
                                            matchTiargs = m2;
                                        }
                                        if (!(dedtypes.get()).get(i).equals(oded))
                                        {
                                            this.error(new BytePtr("specialization not allowed for deduced parameter `%s`"), tparam.ident.value.toChars());
                                        }
                                    }
                                }
                                oded = this.declareParameter(paramscope, tparam, oded);
                                dedargs.get().set(i, oded);
                            }
                        }
                    }
                    {
                        int d = (dedargs.get()).length.value;
                        if ((d) != 0)
                        {
                            {
                                Tuple va = isTuple((dedargs.get()).get(d - 1));
                                if ((va) != null)
                                {
                                    (dedargs.get()).setDim(d - 1);
                                    (dedargs.get()).insert(d - 1, ptr(va.objects));
                                }
                            }
                        }
                    }
                    ti.tiargs.value = dedargs;
                    {
                        assert((paramscope.get()).scopesym.value != null);
                        Ptr<Scope> sc2 = this._scope.value;
                        sc2 = (sc2.get()).push((paramscope.get()).scopesym.value);
                        sc2 = (sc2.get()).push(ti);
                        (sc2.get()).parent.value = ti;
                        (sc2.get()).tinst = ti;
                        (sc2.get()).minst.value = (sc.get()).minst.value;
                        fd.value = this.doHeaderInstantiation(ti, sc2, fd.value, tthis, fargs);
                        sc2 = (sc2.get()).pop();
                        sc2 = (sc2.get()).pop();
                        if (fd.value == null)
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                    }
                    if (this.constraint.value != null)
                    {
                        if (!this.evaluateConstraint(ti, sc, paramscope, dedargs, fd.value))
                        {
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                    }
                    (paramscope.get()).pop();
                    return match | matchTiargs << 4;
                }
                catch(Dispatch1 __d){}
            /*Lnomatch:*/
                (paramscope.get()).pop();
                return MATCH.nomatch;
            }
            catch(Dispatch2 __d){}
        /*Lerror:*/
            (paramscope.get()).pop();
            return MATCH.nomatch;
        }

        public  RootObject declareParameter(Ptr<Scope> sc, TemplateParameter tp, RootObject o) {
            Type ta = isType(o);
            Expression ea = isExpression(o);
            Dsymbol sa = isDsymbol(o);
            Tuple va = isTuple(o);
            Declaration d = null;
            VarDeclaration v = null;
            if ((ea != null) && ((ea.op.value & 0xFF) == 20))
            {
                ta = ea.type.value;
            }
            else if ((ea != null) && ((ea.op.value & 0xFF) == 203))
            {
                sa = ((ScopeExp)ea).sds.value;
            }
            else if ((ea != null) && ((ea.op.value & 0xFF) == 123) || ((ea.op.value & 0xFF) == 124))
            {
                sa = ((ThisExp)ea).var.value;
            }
            else if ((ea != null) && ((ea.op.value & 0xFF) == 161))
            {
                if (((FuncExp)ea).td.value != null)
                {
                    sa = ((FuncExp)ea).td.value;
                }
                else
                {
                    sa = ((FuncExp)ea).fd.value;
                }
            }
            if (ta != null)
            {
                d = new AliasDeclaration(Loc.initial.value, tp.ident.value, ta);
            }
            else if (sa != null)
            {
                d = new AliasDeclaration(Loc.initial.value, tp.ident.value, sa);
            }
            else if (ea != null)
            {
                Initializer _init = new ExpInitializer(this.loc.value, ea);
                TemplateValueParameter tvp = tp.isTemplateValueParameter();
                Type t = tvp != null ? tvp.valType : null;
                v = new VarDeclaration(this.loc.value, t, tp.ident.value, _init, 0L);
                v.storage_class.value = 8650752L;
                d = v;
            }
            else if (va != null)
            {
                d = new TupleDeclaration(this.loc.value, tp.ident.value, ptr(va.objects));
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            d.storage_class.value |= 262144L;
            if (ta != null)
            {
                Type t = ta;
                for (; ((t.ty.value & 0xFF) != ENUMTY.Tenum);){
                    if (t.nextOf() == null)
                    {
                        break;
                    }
                    t = ((TypeNext)t).next.value;
                }
                {
                    Dsymbol s = t.toDsymbol(sc);
                    if ((s) != null)
                    {
                        if (s.isDeprecated())
                        {
                            d.storage_class.value |= 1024L;
                        }
                    }
                }
            }
            else if (sa != null)
            {
                if (sa.isDeprecated())
                {
                    d.storage_class.value |= 1024L;
                }
            }
            if ((sc.get()).insert(d) == null)
            {
                this.error(new BytePtr("declaration `%s` is already defined"), tp.ident.value.toChars());
            }
            dsymbolSemantic(d, sc);
            if (v != null)
            {
                o = initializerToExpression(v._init.value, null);
            }
            return o;
        }

        public  FuncDeclaration doHeaderInstantiation(TemplateInstance ti, Ptr<Scope> sc2, FuncDeclaration fd, Type tthis, Ptr<DArray<Expression>> fargs) {
            assert(fd != null);
            if (fd.isCtorDeclaration() != null)
            {
                fd = new CtorDeclaration(fd.loc.value, fd.endloc.value, fd.storage_class.value, fd.type.value.syntaxCopy(), false);
            }
            else
            {
                fd = new FuncDeclaration(fd.loc.value, fd.endloc.value, fd.ident.value, fd.storage_class.value, fd.type.value.syntaxCopy());
            }
            fd.parent.value = ti;
            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
            TypeFunction tf = (TypeFunction)fd.type.value;
            tf.fargs.value = fargs;
            if (tthis != null)
            {
                boolean hasttp = false;
                {
                    int i = 0;
                    for (; (i < (this.parameters.get()).length.value);i++){
                        TemplateParameter tp = (this.parameters.get()).get(i);
                        TemplateThisParameter ttp = tp.isTemplateThisParameter();
                        if (ttp != null)
                        {
                            hasttp = true;
                        }
                    }
                }
                if (hasttp)
                {
                    tf = (TypeFunction)tf.addSTC(ModToStc((tthis.mod.value & 0xFF)));
                    assert(tf.deco.value == null);
                }
            }
            Ptr<Scope> scx = (sc2.get()).push();
            {
                int i = 0;
                for (; (i < (tf.parameterList.parameters.value.get()).length.value);i++) {
                    (tf.parameterList.parameters.value.get()).get(i).defaultArg.value = null;
                }
            }
            tf.incomplete.value = true;
            if (fd.isCtorDeclaration() != null)
            {
                (scx.get()).flags.value |= 1;
                Dsymbol parent = this.toParentDecl();
                Type tret = null;
                AggregateDeclaration ad = parent.isAggregateDeclaration();
                if ((ad == null) || (parent.isUnionDeclaration() != null))
                {
                    tret = Type.tvoid.value;
                }
                else
                {
                    tret = ad.handleType();
                    assert(tret != null);
                    tret = tret.addStorageClass(fd.storage_class.value | (scx.get()).stc.value);
                    tret = tret.addMod(tf.mod.value);
                }
                tf.next.value = tret;
                if ((ad != null) && (ad.isStructDeclaration() != null))
                {
                    tf.isref.value = true;
                }
            }
            else
            {
                tf.next.value = null;
            }
            fd.type.value = tf;
            fd.type.value = fd.type.value.addSTC((scx.get()).stc.value);
            fd.type.value = typeSemantic(fd.type.value, fd.loc.value, scx);
            scx = (scx.get()).pop();
            if (((fd.type.value.ty.value & 0xFF) != ENUMTY.Tfunction))
            {
                return null;
            }
            fd.originalType.value = fd.type.value;
            return fd;
        }

        public  TemplateInstance findExistingInstance(TemplateInstance tithis, Ptr<DArray<Expression>> fargs) {
            tithis.fargs = fargs;
            TemplateInstanceBox tibox = tibox = new TemplateInstanceBox(tithis);
            Ptr<TemplateInstance> p = pcopy(tibox in this.instances);
            return p != null ? p.get() : null;
        }

        public  TemplateInstance addInstance(TemplateInstance ti) {
            TemplateInstanceBox tibox = tibox = new TemplateInstanceBox(ti);
            this.instances.set(tibox, __aaval1201);
            return ti;
        }

        public  void removeInstance(TemplateInstance ti) {
            TemplateInstanceBox tibox = tibox = new TemplateInstanceBox(ti);
            this.instances.remove(tibox);
        }

        public  TemplateDeclaration isTemplateDeclaration() {
            return this;
        }

        public  TemplateTupleParameter isVariadic() {
            int dim = (this.parameters.get()).length.value;
            if ((dim == 0))
            {
                return null;
            }
            return (this.parameters.get()).get(dim - 1).isTemplateTupleParameter();
        }

        public  boolean isOverloadable() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateDeclaration() {}

        public TemplateDeclaration copy() {
            TemplateDeclaration that = new TemplateDeclaration();
            that.parameters = this.parameters;
            that.origParameters = this.origParameters;
            that.constraint = this.constraint;
            that.instances = this.instances;
            that.overnext = this.overnext;
            that.overroot = this.overroot;
            that.funcroot = this.funcroot;
            that.onemember = this.onemember;
            that.literal = this.literal;
            that.ismixin = this.ismixin;
            that.isstatic = this.isstatic;
            that.protection = this.protection;
            that.inuse = this.inuse;
            that.previous = this.previous;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
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
    public static class TypeDeduced extends Type
    {
        public Ref<Type> tded = ref(null);
        public DArray<Expression> argexps = new DArray<Expression>();
        public DArray<Type> tparams = new DArray<Type>();
        public  TypeDeduced(Type tt, Expression e, Type tparam) {
            super((byte)11);
            this.tded.value = tt;
            this.argexps.push(e);
            this.tparams.push(tparam);
        }

        public  void update(Expression e, Type tparam) {
            this.argexps.push(e);
            this.tparams.push(tparam);
        }

        public  void update(Type tt, Expression e, Type tparam) {
            this.tded.value = tt;
            this.argexps.push(e);
            this.tparams.push(tparam);
        }

        public  int matchAll(Type tt) {
            int match = MATCH.exact;
            {
                int j = 0;
                for (; (j < this.argexps.length.value);j++){
                    Expression e = this.argexps.get(j);
                    assert(e != null);
                    if ((pequals(e, emptyArrayElement.value)))
                    {
                        continue;
                    }
                    Type t = tt.addMod(this.tparams.get(j).mod.value).substWildTo(1);
                    int m = e.implicitConvTo(t);
                    if ((match > m))
                    {
                        match = m;
                    }
                    if ((match <= MATCH.nomatch))
                    {
                        break;
                    }
                }
            }
            return match;
        }


        public TypeDeduced() {}

        public TypeDeduced copy() {
            TypeDeduced that = new TypeDeduced();
            that.tded = this.tded;
            that.argexps = this.argexps;
            that.tparams = this.tparams;
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
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static void functionResolve(MatchAccumulator m, Dsymbol dstart, Loc loc, Ptr<Scope> sc, Ptr<DArray<RootObject>> tiargs, Type tthis, Ptr<DArray<Expression>> fargs, Ptr<BytePtr> pMessage) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<Ptr<DArray<RootObject>>> tiargs_ref = ref(tiargs);
        Ref<Type> tthis_ref = ref(tthis);
        Ref<Ptr<DArray<Expression>>> fargs_ref = ref(fargs);
        Ref<Ptr<BytePtr>> pMessage_ref = ref(pMessage);
        Ref<Slice<Expression>> fargs_ = ref(peekSlice(fargs_ref.value).copy());
        IntRef property = ref(0);
        IntRef ov_index = ref(0);
        Ref<TemplateDeclaration> td_best = ref(null);
        Ref<TemplateInstance> ti_best = ref(null);
        IntRef ta_last = ref((m.last.value != MATCH.nomatch) ? MATCH.exact : MATCH.nomatch);
        Ref<Type> tthis_best = ref(null);
        Function1<FuncDeclaration,Integer> applyFunction = new Function1<FuncDeclaration,Integer>(){
            public Integer invoke(FuncDeclaration fd) {
                Ref<FuncDeclaration> fd_ref = ref(fd);
                if ((pequals(fd_ref.value, m.lastf.value)))
                {
                    return 0;
                }
                if ((tiargs_ref.value != null) && ((tiargs_ref.value.get()).length.value > 0))
                {
                    return 0;
                }
                if ((fd_ref.value.isCtorDeclaration() == null) && (fd_ref.value.semanticRun.value < PASS.semanticdone))
                {
                    Ungag ungag = fd_ref.value.ungagSpeculative().copy();
                    try {
                        dsymbolSemantic(fd_ref.value, null);
                    }
                    finally {
                    }
                }
                if ((fd_ref.value.semanticRun.value < PASS.semanticdone))
                {
                    error(loc, new BytePtr("forward reference to template `%s`"), fd_ref.value.toChars());
                    return 1;
                }
                TypeFunction tf = (TypeFunction)fd_ref.value.type.value;
                IntRef prop = ref(tf.isproperty.value ? 1 : 2);
                if ((property.value == 0))
                {
                    property.value = prop.value;
                }
                else if ((property.value != prop.value))
                {
                    error(fd_ref.value.loc.value, new BytePtr("cannot overload both property and non-property functions"));
                }
                Ref<Type> tthis_fd = ref(fd_ref.value.needThis() ? tthis_ref.value : null);
                Ref<Boolean> isCtorCall = ref((tthis_fd.value != null) && (fd_ref.value.isCtorDeclaration() != null));
                if (isCtorCall.value)
                {
                    if (MODimplicitConv(tf.mod.value, tthis_fd.value.mod.value) || tf.isWild() && ((tf.isShared() ? 1 : 0) == (tthis_fd.value.isShared() ? 1 : 0)) || fd_ref.value.isReturnIsolated())
                    {
                        tthis_fd.value = null;
                    }
                    else
                    {
                        return 0;
                    }
                }
                {
                    Ref<DtorDeclaration> dt = ref(fd_ref.value.isDtorDeclaration());
                    if ((dt.value) != null)
                    {
                        Ref<TypeFunction> dtmod = ref(dt.value.type.value.toTypeFunction());
                        IntRef shared_dtor = ref((dtmod.value.mod.value & 0xFF) & MODFlags.shared_);
                        IntRef shared_this = ref((tthis_fd.value != null) ? (tthis_fd.value.mod.value & 0xFF) & MODFlags.shared_ : 0);
                        if ((shared_dtor.value != 0) && (shared_this.value == 0))
                        {
                            tthis_fd.value = dtmod.value;
                        }
                        else if ((shared_this.value != 0) && (shared_dtor.value == 0) && (tthis_fd.value != null))
                        {
                            tf.mod.value = tthis_fd.value.mod.value;
                        }
                    }
                }
                IntRef mfa = ref(tf.callMatch(tthis_fd.value, fargs_.value, 0, pMessage_ref.value, sc_ref.value));
                if ((mfa.value > MATCH.nomatch))
                {
                    try {
                        try {
                            if ((mfa.value > m.last.value))
                            {
                                /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                            }
                            if ((mfa.value < m.last.value))
                            {
                                /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            }
                            assert(m.lastf.value != null);
                            if (m.lastf.value.overrides(fd_ref.value) != 0)
                            {
                                /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            }
                            if (fd_ref.value.overrides(m.lastf.value) != 0)
                            {
                                /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                            }
                            {
                                IntRef c1 = ref(fd_ref.value.leastAsSpecialized(m.lastf.value));
                                IntRef c2 = ref(m.lastf.value.leastAsSpecialized(fd_ref.value));
                                if ((c1.value > c2.value))
                                {
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                }
                                if ((c1.value < c2.value))
                                {
                                    /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                                }
                            }
                            if (!m.lastf.value.type.value.equals(fd_ref.value.type.value))
                            {
                                IntRef lastCovariant = ref(m.lastf.value.type.value.covariant(fd_ref.value.type.value, null, true));
                                IntRef firstCovariant = ref(fd_ref.value.type.value.covariant(m.lastf.value.type.value, null, true));
                                if ((lastCovariant.value == 1) || (lastCovariant.value == 2))
                                {
                                    if ((firstCovariant.value != 1) && (firstCovariant.value != 2))
                                    {
                                        /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                else if ((firstCovariant.value == 1) || (firstCovariant.value == 2))
                                {
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                }
                            }
                            if (tf.equals(m.lastf.value.type.value) && (fd_ref.value.storage_class.value == m.lastf.value.storage_class.value) && (pequals(fd_ref.value.parent.value, m.lastf.value.parent.value)) && fd_ref.value.protection.opEquals(m.lastf.value.protection) && (fd_ref.value.linkage.value == m.lastf.value.linkage.value))
                            {
                                if ((fd_ref.value.fbody.value != null) && (m.lastf.value.fbody.value == null))
                                {
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                }
                                if ((fd_ref.value.fbody.value == null) && (m.lastf.value.fbody.value != null))
                                {
                                    /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                                }
                            }
                            if (isCtorCall.value && ((tf.mod.value & 0xFF) != (m.lastf.value.type.value.mod.value & 0xFF)))
                            {
                                if (((tthis_ref.value.mod.value & 0xFF) == (tf.mod.value & 0xFF)))
                                {
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                }
                                if (((tthis_ref.value.mod.value & 0xFF) == (m.lastf.value.type.value.mod.value & 0xFF)))
                                {
                                    /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                                }
                            }
                            m.nextf.value = fd_ref.value;
                            m.count.value++;
                            return 0;
                        }
                        catch(Dispatch0 __d){}
                    /*LlastIsBetter:*/
                        return 0;
                    }
                    catch(Dispatch1 __d){}
                /*LfIsBetter:*/
                    td_best.value = null;
                    ti_best.value = null;
                    ta_last.value = MATCH.exact;
                    m.last.value = mfa.value;
                    m.lastf.value = fd_ref.value;
                    tthis_best.value = tthis_fd.value;
                    ov_index.value = 0;
                    m.count.value = 1;
                    return 0;
                }
                return 0;
            }
        };
        Function1<TemplateDeclaration,Integer> applyTemplate = new Function1<TemplateDeclaration,Integer>(){
            public Integer invoke(TemplateDeclaration td) {
                Ref<TemplateDeclaration> td_ref = ref(td);
                if (td_ref.value.inuse.value != 0)
                {
                    td_ref.value.error(loc, new BytePtr("recursive template expansion"));
                    return 1;
                }
                if ((pequals(td_ref.value, td_best.value)))
                {
                    return 0;
                }
                if (sc_ref.value == null)
                {
                    sc_ref.value = td_ref.value._scope.value;
                }
                if ((td_ref.value.semanticRun.value == PASS.init) && (td_ref.value._scope.value != null))
                {
                    Ungag ungag = td_ref.value.ungagSpeculative().copy();
                    try {
                        dsymbolSemantic(td_ref.value, td_ref.value._scope.value);
                    }
                    finally {
                    }
                }
                if ((td_ref.value.semanticRun.value == PASS.init))
                {
                    error(loc, new BytePtr("forward reference to template `%s`"), td_ref.value.toChars());
                /*Lerror:*/
                    m.lastf.value = null;
                    m.count.value = 0;
                    m.last.value = MATCH.nomatch;
                    return 1;
                }
                Ref<FuncDeclaration> f = ref(td_ref.value.onemember.value != null ? td_ref.value.onemember.value.isFuncDeclaration() : null);
                if (f.value == null)
                {
                    if (tiargs_ref.value == null)
                    {
                        tiargs_ref.value = refPtr(new DArray<RootObject>());
                    }
                    Ref<TemplateInstance> ti = ref(new TemplateInstance(loc, td_ref.value, tiargs_ref.value));
                    Ref<DArray<RootObject>> dedtypes = ref(dedtypes.value = new DArray<RootObject>((td_ref.value.parameters.get()).length.value));
                    try {
                        assert((td_ref.value.semanticRun.value != PASS.init));
                        IntRef mta = ref(td_ref.value.matchWithInstance(sc_ref.value, ti.value, ptr(dedtypes), fargs_ref.value, 0));
                        if ((mta.value <= MATCH.nomatch) || (mta.value < ta_last.value))
                        {
                            return 0;
                        }
                        templateInstanceSemantic(ti.value, sc_ref.value, fargs_ref.value);
                        if (ti.value.inst.value == null)
                        {
                            return 0;
                        }
                        Ref<Dsymbol> s = ref(ti.value.inst.value.toAlias());
                        Ref<FuncDeclaration> fd = ref(null);
                        {
                            Ref<TemplateDeclaration> tdx = ref(s.value.isTemplateDeclaration());
                            if ((tdx.value) != null)
                            {
                                Ref<DArray<RootObject>> dedtypesX = ref(new DArray<RootObject>());
                                try {
                                    {
                                        Ref<Ptr<TemplatePrevious>> p = ref(tdx.value.previous.value);
                                    L_outer12:
                                        for (; p.value != null;p.value = (p.value.get()).prev.value){
                                            if (arrayObjectMatch((p.value.get()).dedargs.value, ptr(dedtypesX)))
                                            {
                                                {
                                                    Ref<Ptr<Scope>> scx = ref(sc_ref.value);
                                                L_outer13:
                                                    for (; scx.value != null;scx.value = (scx.value.get()).enclosing.value){
                                                        if ((scx.value == (p.value.get()).sc.value))
                                                        {
                                                            error(loc, new BytePtr("recursive template expansion while looking for `%s.%s`"), ti.value.toChars(), tdx.value.toChars());
                                                            /*goto Lerror*/throw Dispatch0.INSTANCE;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Ref<TemplatePrevious> pr = ref(new TemplatePrevious());
                                    pr.value.prev.value = tdx.value.previous.value;
                                    pr.value.sc.value = sc_ref.value;
                                    pr.value.dedargs.value = ptr(dedtypesX);
                                    tdx.value.previous.value = ptr(pr);
                                    fd.value = resolveFuncCall(loc, sc_ref.value, s.value, null, tthis_ref.value, fargs_ref.value, FuncResolveFlag.quiet);
                                    tdx.value.previous.value = pr.value.prev.value;
                                }
                                finally {
                                }
                            }
                            else if (s.value.isFuncDeclaration() != null)
                            {
                                fd.value = resolveFuncCall(loc, sc_ref.value, s.value, null, tthis_ref.value, fargs_ref.value, FuncResolveFlag.quiet);
                            }
                            else
                            {
                                /*goto Lerror*/throw Dispatch0.INSTANCE;
                            }
                        }
                        if (fd.value == null)
                        {
                            return 0;
                        }
                        if (((fd.value.type.value.ty.value & 0xFF) != ENUMTY.Tfunction))
                        {
                            m.lastf.value = fd.value;
                            m.count.value = 1;
                            m.last.value = MATCH.nomatch;
                            return 1;
                        }
                        Ref<Type> tthis_fd = ref(fd.value.needThis() && (fd.value.isCtorDeclaration() == null) ? tthis_ref.value : null);
                        TypeFunction tf = (TypeFunction)fd.value.type.value;
                        IntRef mfa = ref(tf.callMatch(tthis_fd.value, fargs_.value, 0, null, sc_ref.value));
                        if ((mfa.value < m.last.value))
                        {
                            return 0;
                        }
                        try {
                            try {
                                if ((mta.value < ta_last.value))
                                {
                                    /*goto Ltd_best2*/throw Dispatch0.INSTANCE;
                                }
                                if ((mta.value > ta_last.value))
                                {
                                    /*goto Ltd2*/throw Dispatch1.INSTANCE;
                                }
                                if ((mfa.value < m.last.value))
                                {
                                    /*goto Ltd_best2*/throw Dispatch0.INSTANCE;
                                }
                                if ((mfa.value > m.last.value))
                                {
                                    /*goto Ltd2*/throw Dispatch1.INSTANCE;
                                }
                                m.nextf.value = fd.value;
                                m.count.value++;
                                return 0;
                            }
                            catch(Dispatch0 __d){}
                        /*Ltd_best2:*/
                            return 0;
                        }
                        catch(Dispatch1 __d){}
                    /*Ltd2:*/
                        assert(td_ref.value._scope.value != null);
                        td_best.value = td_ref.value;
                        ti_best.value = null;
                        property.value = 0;
                        ta_last.value = mta.value;
                        m.last.value = mfa.value;
                        m.lastf.value = fd.value;
                        tthis_best.value = tthis_fd.value;
                        ov_index.value = 0;
                        m.nextf.value = null;
                        m.count.value = 1;
                        return 0;
                    }
                    finally {
                    }
                }
                {
                    IntRef ovi = ref(0);
                L_outer14:
                    for (; f.value != null;comma(f.value = f.value.overnext0.value, ovi.value++)){
                        if (((f.value.type.value.ty.value & 0xFF) != ENUMTY.Tfunction) || f.value.errors.value)
                        {
                            /*goto Lerror*/throw Dispatch0.INSTANCE;
                        }
                        Ref<TemplateInstance> ti = ref(new TemplateInstance(loc, td_ref.value, tiargs_ref.value));
                        ti.value.parent.value = td_ref.value.parent.value;
                        Ref<FuncDeclaration> fd = ref(f.value);
                        IntRef x = ref(td_ref.value.deduceFunctionTemplateMatch(ti.value, sc_ref.value, fd, tthis_ref.value, fargs_ref.value));
                        IntRef mta = ref(x.value >> 4);
                        IntRef mfa = ref(x.value & 15);
                        if ((fd.value == null) || (mfa.value == MATCH.nomatch))
                        {
                            continue L_outer14;
                        }
                        Ref<Type> tthis_fd = ref(fd.value.needThis() ? tthis_ref.value : null);
                        Ref<Boolean> isCtorCall = ref((tthis_fd.value != null) && (fd.value.isCtorDeclaration() != null));
                        if (isCtorCall.value)
                        {
                            TypeFunction tf = (TypeFunction)fd.value.type.value;
                            assert(tf.next.value != null);
                            if (MODimplicitConv(tf.mod.value, tthis_fd.value.mod.value) || tf.isWild() && ((tf.isShared() ? 1 : 0) == (tthis_fd.value.isShared() ? 1 : 0)) || fd.value.isReturnIsolated())
                            {
                                tthis_fd.value = null;
                            }
                            else
                            {
                                continue L_outer14;
                            }
                        }
                        try {
                            try {
                                if ((mta.value < ta_last.value))
                                {
                                    /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                }
                                if ((mta.value > ta_last.value))
                                {
                                    /*goto Ltd*/throw Dispatch1.INSTANCE;
                                }
                                if ((mfa.value < m.last.value))
                                {
                                    /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                }
                                if ((mfa.value > m.last.value))
                                {
                                    /*goto Ltd*/throw Dispatch1.INSTANCE;
                                }
                                if (td_best.value != null)
                                {
                                    IntRef c1 = ref(td_ref.value.leastAsSpecialized(sc_ref.value, td_best.value, fargs_ref.value));
                                    IntRef c2 = ref(td_best.value.leastAsSpecialized(sc_ref.value, td_ref.value, fargs_ref.value));
                                    if ((c1.value > c2.value))
                                    {
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    }
                                    if ((c1.value < c2.value))
                                    {
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                assert((fd.value != null) && (m.lastf.value != null));
                                {
                                    TypeFunction tf1 = (TypeFunction)fd.value.type.value;
                                    assert(((tf1.ty.value & 0xFF) == ENUMTY.Tfunction));
                                    TypeFunction tf2 = (TypeFunction)m.lastf.value.type.value;
                                    assert(((tf2.ty.value & 0xFF) == ENUMTY.Tfunction));
                                    IntRef c1 = ref(tf1.callMatch(tthis_fd.value, fargs_.value, 0, null, sc_ref.value));
                                    IntRef c2 = ref(tf2.callMatch(tthis_best.value, fargs_.value, 0, null, sc_ref.value));
                                    if ((c1.value > c2.value))
                                    {
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    }
                                    if ((c1.value < c2.value))
                                    {
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                {
                                    IntRef c1 = ref(fd.value.leastAsSpecialized(m.lastf.value));
                                    IntRef c2 = ref(m.lastf.value.leastAsSpecialized(fd.value));
                                    if ((c1.value > c2.value))
                                    {
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    }
                                    if ((c1.value < c2.value))
                                    {
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                if (isCtorCall.value && ((fd.value.type.value.mod.value & 0xFF) != (m.lastf.value.type.value.mod.value & 0xFF)))
                                {
                                    if (((tthis_ref.value.mod.value & 0xFF) == (fd.value.type.value.mod.value & 0xFF)))
                                    {
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    }
                                    if (((tthis_ref.value.mod.value & 0xFF) == (m.lastf.value.type.value.mod.value & 0xFF)))
                                    {
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                m.nextf.value = fd.value;
                                m.count.value++;
                                continue L_outer14;
                            }
                            catch(Dispatch0 __d){}
                        /*Ltd_best:*/
                            continue L_outer14;
                        }
                        catch(Dispatch1 __d){}
                    /*Ltd:*/
                        assert(td_ref.value._scope.value != null);
                        td_best.value = td_ref.value;
                        ti_best.value = ti.value;
                        property.value = 0;
                        ta_last.value = mta.value;
                        m.last.value = mfa.value;
                        m.lastf.value = fd.value;
                        tthis_best.value = tthis_fd.value;
                        ov_index.value = ovi.value;
                        m.nextf.value = null;
                        m.count.value = 1;
                        continue L_outer14;
                    }
                }
                return 0;
            }
        };
        TemplateDeclaration td = dstart.isTemplateDeclaration();
        if ((td != null) && (td.funcroot != null))
        {
            dstart = td.funcroot;
        }
        Function1<Dsymbol,Integer> __lambda11 = new Function1<Dsymbol,Integer>(){
            public Integer invoke(Dsymbol s) {
                if (s.errors.value)
                {
                    return 0;
                }
                {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        return applyFunction.invoke(fd);
                    }
                }
                {
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if ((td) != null)
                    {
                        return applyTemplate.invoke(td);
                    }
                }
                return 0;
            }
        };
        overloadApply(dstart, __lambda11, sc_ref.value);
        if ((td_best.value != null) && (ti_best.value != null) && (m.count.value == 1))
        {
            assert((td_best.value.onemember.value != null) && (td_best.value.onemember.value.isFuncDeclaration() != null));
            assert(td_best.value._scope.value != null);
            if (sc_ref.value == null)
            {
                sc_ref.value = td_best.value._scope.value;
            }
            TemplateInstance ti = new TemplateInstance(loc, td_best.value, ti_best.value.tiargs.value);
            templateInstanceSemantic(ti, sc_ref.value, fargs_ref.value);
            m.lastf.value = ti.toAlias().isFuncDeclaration();
            if (m.lastf.value == null)
            {
                /*goto Lnomatch*//*unrolled goto*/
                assert((m.count.value >= 1));
            }
            if (ti.errors.value)
            {
            /*Lerror:*/
                m.count.value = 1;
                assert(m.lastf.value != null);
                m.last.value = MATCH.nomatch;
                return ;
            }
            for (; ov_index.value-- != 0;){
                m.lastf.value = m.lastf.value.overnext0.value;
                assert(m.lastf.value != null);
            }
            tthis_best.value = m.lastf.value.needThis() && (m.lastf.value.isCtorDeclaration() == null) ? tthis_ref.value : null;
            TypeFunction tf = (TypeFunction)m.lastf.value.type.value;
            if (((tf.ty.value & 0xFF) == ENUMTY.Terror))
            {
                /*goto Lerror*/throw Dispatch0.INSTANCE;
            }
            assert(((tf.ty.value & 0xFF) == ENUMTY.Tfunction));
            if (tf.callMatch(tthis_best.value, fargs_.value, 0, null, sc_ref.value) == 0)
            {
                /*goto Lnomatch*//*unrolled goto*/
                assert((m.count.value >= 1));
            }
            if ((tf.next.value != null) && !m.lastf.value.inferRetType)
            {
                m.lastf.value.type.value = typeSemantic(tf, loc, sc_ref.value);
            }
        }
        else if (m.lastf.value != null)
        {
            assert((m.count.value >= 1));
        }
        else
        {
        /*Lnomatch:*/
            m.count.value = 0;
            m.lastf.value = null;
            m.last.value = MATCH.nomatch;
        }
    }

    // defaulted all parameters starting with #8
    public static void functionResolve(MatchAccumulator m, Dsymbol dstart, Loc loc, Ptr<Scope> sc, Ptr<DArray<RootObject>> tiargs, Type tthis, Ptr<DArray<Expression>> fargs) {
        return functionResolve(m, dstart, loc, sc, tiargs, tthis, fargs, null);
    }

    public static int templateIdentifierLookup(Identifier id, Ptr<DArray<TemplateParameter>> parameters) {
        {
            int i = 0;
            for (; (i < (parameters.get()).length.value);i++){
                TemplateParameter tp = (parameters.get()).get(i);
                if (tp.ident.value.equals(id))
                {
                    return i;
                }
            }
        }
        return 305419896;
    }

    public static int templateParameterLookup(Type tparam, Ptr<DArray<TemplateParameter>> parameters) {
        if (((tparam.ty.value & 0xFF) == ENUMTY.Tident))
        {
            TypeIdentifier tident = (TypeIdentifier)tparam;
            return templateIdentifierLookup(tident.ident.value, parameters);
        }
        return 305419896;
    }

    public static byte deduceWildHelper(Type t, Ptr<Type> at, Type tparam) {
        if ((((tparam.mod.value & 0xFF) & MODFlags.wild) == 0))
        {
            return (byte)0;
        }
        at.set(0, null);
        // from template X!(ByteByte)
        Function2<Byte,Byte,Integer> XByteByte = new Function2<Byte,Byte,Integer>(){
            public Integer invoke(Byte U, Byte T) {
                return (U & 0xFF) << 4 | (T & 0xFF);
            }
        };

        // from template X!(IntegerInteger)
        Function2<Integer,Integer,Integer> XIntegerInteger = new Function2<Integer,Integer,Integer>(){
            public Integer invoke(Integer U, Integer T) {
                return U << 4 | T;
            }
        };

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xbyte, byteByteByte", "int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        switch (XByteByte.invoke(tparam.mod.value, t.mod.value))
        {
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 162:
            case 163:
            case 164:
            case 178:
            case 179:
            case 180:
                byte wm = (byte)((t.mod.value & 0xFF) & -3);
                if (((wm & 0xFF) == 0))
                {
                    wm = (byte)16;
                }
                byte m = (byte)((t.mod.value & 0xFF) & 5 | (tparam.mod.value & 0xFF) & (t.mod.value & 0xFF) & MODFlags.shared_);
                at.set(0, t.unqualify((m & 0xFF)));
                return wm;
            case 136:
            case 137:
            case 138:
            case 139:
            case 152:
            case 153:
            case 154:
            case 155:
            case 170:
            case 171:
            case 186:
            case 187:
                at.set(0, t.unqualify(((tparam.mod.value & 0xFF) & (t.mod.value & 0xFF))));
                return (byte)8;
            default:
            return (byte)0;
        }
    }

    public static Type rawTypeMerge(Type t1, Type t2) {
        if (t1.equals(t2))
        {
            return t1;
        }
        if (t1.equivalent(t2))
        {
            return t1.castMod(MODmerge(t1.mod.value, t2.mod.value));
        }
        Type t1b = t1.toBasetype();
        Type t2b = t2.toBasetype();
        if (t1b.equals(t2b))
        {
            return t1b;
        }
        if (t1b.equivalent(t2b))
        {
            return t1b.castMod(MODmerge(t1b.mod.value, t2b.mod.value));
        }
        byte ty = impcnvResult.get((t1b.ty.value & 0xFF)).get((t2b.ty.value & 0xFF));
        if (((ty & 0xFF) != ENUMTY.Terror))
        {
            return Type.basic.get((ty & 0xFF));
        }
        return null;
    }

    public static int deduceTypeHelper(Type t, Ptr<Type> at, Type tparam) {
        // from template X!(ByteByte)
        Function2<Byte,Byte,Integer> XByteByte = new Function2<Byte,Byte,Integer>(){
            public Integer invoke(Byte U, Byte T) {
                return (U & 0xFF) << 4 | (T & 0xFF);
            }
        };

        // from template X!(IntegerInteger)
        Function2<Integer,Integer,Integer> XIntegerInteger = new Function2<Integer,Integer,Integer>(){
            public Integer invoke(Integer U, Integer T) {
                return U << 4 | T;
            }
        };

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xbyte, byteByteByte", "int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xbyte, byteByteByte", "int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xbyte, byteByteByte", "int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        switch (XByteByte.invoke(tparam.mod.value, t.mod.value))
        {
            case 0:
            case 1:
            case 8:
            case 9:
            case 2:
            case 3:
            case 10:
            case 11:
            case 4:
                at.set(0, t);
                return MATCH.exact;
            case 17:
            case 136:
            case 153:
            case 34:
            case 51:
            case 170:
            case 187:
            case 68:
                at.set(0, t.mutableOf().unSharedOf());
                return MATCH.exact;
            case 16:
            case 24:
            case 25:
            case 19:
            case 26:
            case 27:
            case 20:
            case 138:
            case 155:
            case 52:
                at.set(0, t.mutableOf());
                return MATCH.constant;
            case 18:
                at.set(0, t);
                return MATCH.constant;
            case 35:
            case 42:
            case 43:
            case 50:
                at.set(0, t.unSharedOf());
                return MATCH.constant;
            case 148:
            case 59:
            case 180:
            case 186:
                at.set(0, t.unSharedOf().mutableOf());
                return MATCH.constant;
            case 58:
                at.set(0, t.unSharedOf().mutableOf());
                return MATCH.constant;
            case 128:
            case 129:
            case 137:
            case 132:
            case 130:
            case 131:
            case 139:
            case 144:
            case 145:
            case 152:
            case 146:
            case 147:
            case 154:
            case 32:
            case 33:
            case 40:
            case 41:
            case 36:
            case 48:
            case 49:
            case 56:
            case 57:
            case 160:
            case 161:
            case 168:
            case 169:
            case 164:
            case 162:
            case 163:
            case 171:
            case 176:
            case 177:
            case 184:
            case 185:
            case 178:
            case 179:
            case 64:
            case 65:
            case 72:
            case 73:
            case 66:
            case 67:
            case 74:
            case 75:
                return MATCH.nomatch;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }

    static Ref<Expression> emptyArrayElement = ref(null);
    public static int deduceType(RootObject o, Ptr<Scope> sc, Type tparam, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, IntPtr wm, int inferStart, boolean ignoreAliasThis) {
        // skipping duplicate class DeduceType
        DeduceType v = new DeduceType(sc, tparam, parameters, dedtypes, wm, inferStart, ignoreAliasThis);
        {
            Type t = isType(o);
            if ((t) != null)
            {
                t.accept(v);
            }
            else {
                Expression e = isExpression(o);
                if ((e) != null)
                {
                    assert(wm != null);
                    e.accept(v);
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
            }
        }
        return v.result.value;
    }

    // defaulted all parameters starting with #8
    public static int deduceType(RootObject o, Ptr<Scope> sc, Type tparam, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, IntPtr wm, int inferStart) {
        return deduceType(o, sc, tparam, parameters, dedtypes, wm, inferStart, false);
    }

    // defaulted all parameters starting with #7
    public static int deduceType(RootObject o, Ptr<Scope> sc, Type tparam, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, IntPtr wm) {
        return deduceType(o, sc, tparam, parameters, dedtypes, wm, 0, false);
    }

    // defaulted all parameters starting with #6
    public static int deduceType(RootObject o, Ptr<Scope> sc, Type tparam, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes) {
        return deduceType(o, sc, tparam, parameters, dedtypes, null, 0, false);
    }

    public static boolean reliesOnTident(Type t, Ptr<DArray<TemplateParameter>> tparams, int iStart) {
        return reliesOnTemplateParameters(t, (tparams.get()).opSlice(0, (tparams.get()).length.value));
    }

    // defaulted all parameters starting with #3
    public static boolean reliesOnTident(Type t, Ptr<DArray<TemplateParameter>> tparams) {
        return reliesOnTident(t, tparams, 0);
    }

    public static boolean reliesOnTemplateParameters(Type t, Slice<TemplateParameter> tparams) {
        Ref<Slice<TemplateParameter>> tparams_ref = ref(tparams);
        Function1<TypeVector,Boolean> visitVector = new Function1<TypeVector,Boolean>(){
            public Boolean invoke(TypeVector t) {
                return reliesOnTemplateParameters(t.basetype.value, tparams_ref.value);
            }
        };
        Function1<TypeAArray,Boolean> visitAArray = new Function1<TypeAArray,Boolean>(){
            public Boolean invoke(TypeAArray t) {
                return reliesOnTemplateParameters(t.next.value, tparams_ref.value) || reliesOnTemplateParameters(t.index.value, tparams_ref.value);
            }
        };
        Function1<TypeFunction,Boolean> visitFunction = new Function1<TypeFunction,Boolean>(){
            public Boolean invoke(TypeFunction t) {
                {
                    IntRef __key1209 = ref(0);
                    IntRef __limit1210 = ref(t.parameterList.length());
                    for (; (__key1209.value < __limit1210.value);__key1209.value += 1) {
                        IntRef i = ref(__key1209.value);
                        Parameter fparam = t.parameterList.get(i.value);
                        if (reliesOnTemplateParameters(fparam.type.value, tparams_ref.value))
                        {
                            return true;
                        }
                    }
                }
                return reliesOnTemplateParameters(t.next.value, tparams_ref.value);
            }
        };
        Function1<TypeIdentifier,Boolean> visitIdentifier = new Function1<TypeIdentifier,Boolean>(){
            public Boolean invoke(TypeIdentifier t) {
                {
                    Ref<Slice<TemplateParameter>> __r1211 = ref(tparams_ref.value.copy());
                    IntRef __key1212 = ref(0);
                    for (; (__key1212.value < __r1211.value.getLength());__key1212.value += 1) {
                        TemplateParameter tp = __r1211.value.get(__key1212.value);
                        if (tp.ident.value.equals(t.ident.value))
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        Function1<TypeInstance,Boolean> visitInstance = new Function1<TypeInstance,Boolean>(){
            public Boolean invoke(TypeInstance t) {
                {
                    Ref<Slice<TemplateParameter>> __r1213 = ref(tparams_ref.value.copy());
                    IntRef __key1214 = ref(0);
                    for (; (__key1214.value < __r1213.value.getLength());__key1214.value += 1) {
                        TemplateParameter tp = __r1213.value.get(__key1214.value);
                        if ((pequals(t.tempinst.value.name.value, tp.ident.value)))
                        {
                            return true;
                        }
                    }
                }
                if (t.tempinst.value.tiargs.value != null)
                {
                    Ref<Slice<RootObject>> __r1215 = ref((t.tempinst.value.tiargs.value.get()).opSlice().copy());
                    IntRef __key1216 = ref(0);
                    for (; (__key1216.value < __r1215.value.getLength());__key1216.value += 1) {
                        Ref<RootObject> arg = ref(__r1215.value.get(__key1216.value));
                        {
                            Ref<Type> ta = ref(isType(arg.value));
                            if ((ta.value) != null)
                            {
                                if (reliesOnTemplateParameters(ta.value, tparams_ref.value))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }
        };
        Function1<TypeTypeof,Boolean> visitTypeof = new Function1<TypeTypeof,Boolean>(){
            public Boolean invoke(TypeTypeof t) {
                return reliesOnTemplateParameters(t.exp.value, tparams_ref.value);
            }
        };
        Function1<TypeTuple,Boolean> visitTuple = new Function1<TypeTuple,Boolean>(){
            public Boolean invoke(TypeTuple t) {
                if (t.arguments.value != null)
                {
                    Ref<Slice<Parameter>> __r1217 = ref((t.arguments.value.get()).opSlice().copy());
                    IntRef __key1218 = ref(0);
                    for (; (__key1218.value < __r1217.value.getLength());__key1218.value += 1) {
                        Parameter arg = __r1217.value.get(__key1218.value);
                        if (reliesOnTemplateParameters(arg.type.value, tparams_ref.value))
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        if (t == null)
        {
            return false;
        }
        Type tb = t.toBasetype();
        switch ((tb.ty.value & 0xFF))
        {
            case 41:
                return visitVector.invoke(tb.isTypeVector());
            case 2:
                return visitAArray.invoke(tb.isTypeAArray());
            case 5:
                return visitFunction.invoke(tb.isTypeFunction());
            case 6:
                return visitIdentifier.invoke(tb.isTypeIdentifier());
            case 35:
                return visitInstance.invoke(tb.isTypeInstance());
            case 36:
                return visitTypeof.invoke(tb.isTypeTypeof());
            case 37:
                return visitTuple.invoke(tb.isTypeTuple());
            case 9:
                return false;
            default:
            return reliesOnTemplateParameters(tb.nextOf(), tparams_ref.value);
        }
    }

    public static boolean reliesOnTemplateParameters(Expression e, Slice<TemplateParameter> tparams) {
        // skipping duplicate class ReliesOnTemplateParameters
        ReliesOnTemplateParameters v = new ReliesOnTemplateParameters(tparams);
        e.accept(v);
        return v.result.value;
    }

    public static abstract class TemplateParameter extends ASTNode
    {
        public Ref<Loc> loc = ref(new Loc());
        public Ref<Identifier> ident = ref(null);
        public Ref<Boolean> dependent = ref(false);
        public  TemplateParameter(Loc loc, Identifier ident) {
            super();
            this.loc.value = loc.copy();
            this.ident.value = ident;
        }

        public  TemplateTypeParameter isTemplateTypeParameter() {
            return null;
        }

        public  TemplateValueParameter isTemplateValueParameter() {
            return null;
        }

        public  TemplateAliasParameter isTemplateAliasParameter() {
            return null;
        }

        public  TemplateThisParameter isTemplateThisParameter() {
            return null;
        }

        public  TemplateTupleParameter isTemplateTupleParameter() {
            return null;
        }

        public abstract TemplateParameter syntaxCopy();


        public abstract boolean declareParameter(Ptr<Scope> sc);


        public abstract void print(RootObject oarg, RootObject oded);


        public abstract RootObject specialization();


        public abstract RootObject defaultArg(Loc instLoc, Ptr<Scope> sc);


        public abstract boolean hasDefaultArg();


        public  BytePtr toChars() {
            return this.ident.value.toChars();
        }

        public  int dyncast() {
            return DYNCAST.templateparameter;
        }

        public  int matchArg(Loc instLoc, Ptr<Scope> sc, Ptr<DArray<RootObject>> tiargs, int i, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<Declaration> psparam) {
            RootObject oarg = null;
            try {
                if ((i < (tiargs.get()).length.value))
                {
                    oarg = (tiargs.get()).get(i);
                }
                else
                {
                    oarg = this.defaultArg(instLoc, sc);
                    if (oarg == null)
                    {
                        assert((i < (dedtypes.get()).length.value));
                        oarg = (dedtypes.get()).get(i);
                        if (oarg == null)
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                return this.matchArg(sc, oarg, i, parameters, dedtypes, psparam);
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
            {
                psparam.set(0, null);
            }
            return MATCH.nomatch;
        }

        public abstract int matchArg(Ptr<Scope> sc, RootObject oarg, int i, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<Declaration> psparam);


        public abstract Object dummyArg();


        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateParameter() {}

        public abstract TemplateParameter copy();
    }
    public static class TemplateTypeParameter extends TemplateParameter
    {
        public Type specType = null;
        public Ref<Type> defaultType = ref(null);
        public static Type tdummy = null;
        public  TemplateTypeParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
            super(loc, ident);
            this.specType = specType;
            this.defaultType.value = defaultType;
        }

        public  TemplateTypeParameter isTemplateTypeParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateTypeParameter(this.loc.value, this.ident.value, this.specType != null ? this.specType.syntaxCopy() : null, this.defaultType.value != null ? this.defaultType.value.syntaxCopy() : null);
        }

        public  boolean declareParameter(Ptr<Scope> sc) {
            TypeIdentifier ti = new TypeIdentifier(this.loc.value, this.ident.value);
            Declaration ad = new AliasDeclaration(this.loc.value, this.ident.value, ti);
            return (sc.get()).insert(ad) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s\n"), this.ident.value.toChars());
            Type t = isType(oarg);
            Type ta = isType(oded);
            assert(ta != null);
            if (this.specType != null)
            {
                printf(new BytePtr("\u0009Specialization: %s\n"), this.specType.toChars());
            }
            if (this.defaultType.value != null)
            {
                printf(new BytePtr("\u0009Default:        %s\n"), this.defaultType.value.toChars());
            }
            printf(new BytePtr("\u0009Parameter:       %s\n"), t != null ? t.toChars() : new BytePtr("NULL"));
            printf(new BytePtr("\u0009Deduced Type:   %s\n"), ta.toChars());
        }

        public  RootObject specialization() {
            return this.specType;
        }

        public  RootObject defaultArg(Loc instLoc, Ptr<Scope> sc) {
            Type t = this.defaultType.value;
            if (t != null)
            {
                t = t.syntaxCopy();
                t = typeSemantic(t, this.loc.value, sc);
            }
            return t;
        }

        public  boolean hasDefaultArg() {
            return this.defaultType.value != null;
        }

        public  int matchArg(Ptr<Scope> sc, RootObject oarg, int i, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<Declaration> psparam) {
            int m = MATCH.exact;
            Type ta = isType(oarg);
            try {
                if (ta == null)
                {
                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                }
                if (this.specType != null)
                {
                    if ((ta == null) || (pequals(ta, tdummy)))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    int m2 = deduceType(ta, sc, this.specType, parameters, dedtypes, null, 0, false);
                    if ((m2 <= MATCH.nomatch))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    if ((m2 < m))
                    {
                        m = m2;
                    }
                    if ((dedtypes.get()).get(i) != null)
                    {
                        Type t = (Type)(dedtypes.get()).get(i);
                        if (this.dependent.value && !t.equals(ta))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        ta = t;
                    }
                }
                else
                {
                    if ((dedtypes.get()).get(i) != null)
                    {
                        Type t = (Type)(dedtypes.get()).get(i);
                        if (!t.equals(ta))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                    else
                    {
                        m = MATCH.convert;
                    }
                }
                dedtypes.get().set(i, ta);
                if (psparam != null)
                {
                    psparam.set(0, (new AliasDeclaration(this.loc.value, this.ident.value, ta)));
                }
                return this.dependent.value ? MATCH.exact : m;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
            {
                psparam.set(0, null);
            }
            return MATCH.nomatch;
        }

        public  Object dummyArg() {
            Type t = this.specType;
            if (t == null)
            {
                if (tdummy == null)
                {
                    tdummy = new TypeIdentifier(this.loc.value, this.ident.value);
                }
                t = tdummy;
            }
            return t;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateTypeParameter() {}

        public TemplateTypeParameter copy() {
            TemplateTypeParameter that = new TemplateTypeParameter();
            that.specType = this.specType;
            that.defaultType = this.defaultType;
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateThisParameter extends TemplateTypeParameter
    {
        public  TemplateThisParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
            super(loc, ident, specType, defaultType);
        }

        public  TemplateThisParameter isTemplateThisParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateThisParameter(this.loc.value, this.ident.value, this.specType != null ? this.specType.syntaxCopy() : null, this.defaultType.value != null ? this.defaultType.value.syntaxCopy() : null);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateThisParameter() {}

        public TemplateThisParameter copy() {
            TemplateThisParameter that = new TemplateThisParameter();
            that.specType = this.specType;
            that.defaultType = this.defaultType;
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateValueParameter extends TemplateParameter
    {
        public Type valType = null;
        public Expression specValue = null;
        public Expression defaultValue = null;
        public static AA<Object,Expression> edummies = new AA<Object,Expression>();
        public  TemplateValueParameter(Loc loc, Identifier ident, Type valType, Expression specValue, Expression defaultValue) {
            super(loc, ident);
            this.valType = valType;
            this.specValue = specValue;
            this.defaultValue = defaultValue;
        }

        public  TemplateValueParameter isTemplateValueParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateValueParameter(this.loc.value, this.ident.value, this.valType.syntaxCopy(), this.specValue != null ? this.specValue.syntaxCopy() : null, this.defaultValue != null ? this.defaultValue.syntaxCopy() : null);
        }

        public  boolean declareParameter(Ptr<Scope> sc) {
            VarDeclaration v = new VarDeclaration(this.loc.value, this.valType, this.ident.value, null, 0L);
            v.storage_class.value = 262144L;
            return (sc.get()).insert(v) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s\n"), this.ident.value.toChars());
            Expression ea = isExpression(oded);
            if (this.specValue != null)
            {
                printf(new BytePtr("\u0009Specialization: %s\n"), this.specValue.toChars());
            }
            printf(new BytePtr("\u0009Parameter Value: %s\n"), ea != null ? ea.toChars() : new BytePtr("NULL"));
        }

        public  RootObject specialization() {
            return this.specValue;
        }

        public  RootObject defaultArg(Loc instLoc, Ptr<Scope> sc) {
            Expression e = this.defaultValue;
            if (e != null)
            {
                e = e.syntaxCopy();
                int olderrs = global.errors.value;
                if (((e = expressionSemantic(e, sc)) == null))
                {
                    return null;
                }
                if (((e = resolveProperties(sc, e)) == null))
                {
                    return null;
                }
                e = e.resolveLoc(instLoc, sc);
                e = e.optimize(0, false);
                if ((global.errors.value != olderrs))
                {
                    e = new ErrorExp();
                }
            }
            return e;
        }

        public  boolean hasDefaultArg() {
            return this.defaultValue != null;
        }

        public  int matchArg(Ptr<Scope> sc, RootObject oarg, int i, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<Declaration> psparam) {
            int m = MATCH.exact;
            Expression ei = isExpression(oarg);
            Type vt = null;
            try {
                if ((ei == null) && (oarg != null))
                {
                    Dsymbol si = isDsymbol(oarg);
                    FuncDeclaration f = si != null ? si.isFuncDeclaration() : null;
                    if ((f == null) || (f.fbody.value == null) || f.needThis())
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    ei = new VarExp(this.loc.value, f, true);
                    ei = expressionSemantic(ei, sc);
                    int olderrors = global.startGagging();
                    ei = resolveProperties(sc, ei);
                    ei = ei.ctfeInterpret();
                    if (global.endGagging(olderrors) || ((ei.op.value & 0xFF) == 127))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    m = MATCH.convert;
                }
                if ((ei != null) && ((ei.op.value & 0xFF) == 26))
                {
                    ei = ei.ctfeInterpret();
                }
                vt = typeSemantic(this.valType, this.loc.value, sc);
                if (ei.type.value != null)
                {
                    int m2 = ei.implicitConvTo(vt);
                    if ((m2 < m))
                    {
                        m = m2;
                    }
                    if ((m <= MATCH.nomatch))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    ei = ei.implicitCastTo(sc, vt);
                    ei = ei.ctfeInterpret();
                }
                if (this.specValue != null)
                {
                    if ((ei == null) || (ei.type.value in edummies != null) && (pequals(edummies.get(ei.type.value), ei)))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    Expression e = this.specValue;
                    sc = (sc.get()).startCTFE();
                    e = expressionSemantic(e, sc);
                    e = resolveProperties(sc, e);
                    sc = (sc.get()).endCTFE();
                    e = e.implicitCastTo(sc, vt);
                    e = e.ctfeInterpret();
                    ei = ei.syntaxCopy();
                    sc = (sc.get()).startCTFE();
                    ei = expressionSemantic(ei, sc);
                    sc = (sc.get()).endCTFE();
                    ei = ei.implicitCastTo(sc, vt);
                    ei = ei.ctfeInterpret();
                    if (!ei.equals(e))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                }
                else
                {
                    if ((dedtypes.get()).get(i) != null)
                    {
                        Expression e = (Expression)(dedtypes.get()).get(i);
                        if ((ei == null) || !ei.equals(e))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                dedtypes.get().set(i, ei);
                if (psparam != null)
                {
                    Initializer _init = new ExpInitializer(this.loc.value, ei);
                    Declaration sparam = new VarDeclaration(this.loc.value, vt, this.ident.value, _init, 0L);
                    sparam.storage_class.value = 8388608L;
                    psparam.set(0, sparam);
                }
                return this.dependent.value ? MATCH.exact : m;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
            {
                psparam.set(0, null);
            }
            return MATCH.nomatch;
        }

        public  Object dummyArg() {
            Expression e = this.specValue;
            if (e == null)
            {
                Ptr<Expression> pe = pcopy(this.valType in edummies);
                if (pe == null)
                {
                    e = defaultInit(this.valType, Loc.initial.value);
                    edummies.set((this.valType), __aaval1243);
                }
                else
                {
                    e = pe.get();
                }
            }
            return e;
        }

        public  void accept(Visitor v) {
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
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateAliasParameter extends TemplateParameter
    {
        public Type specType = null;
        public RootObject specAlias = null;
        public RootObject defaultAlias = null;
        public static Dsymbol sdummy = null;
        public  TemplateAliasParameter(Loc loc, Identifier ident, Type specType, RootObject specAlias, RootObject defaultAlias) {
            super(loc, ident);
            this.specType = specType;
            this.specAlias = specAlias;
            this.defaultAlias = defaultAlias;
        }

        public  TemplateAliasParameter isTemplateAliasParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateAliasParameter(this.loc.value, this.ident.value, this.specType != null ? this.specType.syntaxCopy() : null, objectSyntaxCopy(this.specAlias), objectSyntaxCopy(this.defaultAlias));
        }

        public  boolean declareParameter(Ptr<Scope> sc) {
            TypeIdentifier ti = new TypeIdentifier(this.loc.value, this.ident.value);
            Declaration ad = new AliasDeclaration(this.loc.value, this.ident.value, ti);
            return (sc.get()).insert(ad) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s\n"), this.ident.value.toChars());
            Dsymbol sa = isDsymbol(oded);
            assert(sa != null);
            printf(new BytePtr("\u0009Parameter alias: %s\n"), sa.toChars());
        }

        public  RootObject specialization() {
            return this.specAlias;
        }

        public  RootObject defaultArg(Loc instLoc, Ptr<Scope> sc) {
            RootObject da = this.defaultAlias;
            Type ta = isType(this.defaultAlias);
            if (ta != null)
            {
                if (((ta.ty.value & 0xFF) == ENUMTY.Tinstance))
                {
                    da = ta.syntaxCopy();
                }
            }
            RootObject o = aliasParameterSemantic(this.loc.value, sc, da, null);
            return o;
        }

        public  boolean hasDefaultArg() {
            return this.defaultAlias != null;
        }

        public  int matchArg(Ptr<Scope> sc, RootObject oarg, int i, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<Declaration> psparam) {
            int m = MATCH.exact;
            Type ta = isType(oarg);
            RootObject sa = (ta != null) && (ta.deco.value == null) ? null : getDsymbol(oarg);
            Expression ea = isExpression(oarg);
            if ((ea != null) && ((ea.op.value & 0xFF) == 123) || ((ea.op.value & 0xFF) == 124))
            {
                sa = ((ThisExp)ea).var.value;
            }
            else if ((ea != null) && ((ea.op.value & 0xFF) == 203))
            {
                sa = ((ScopeExp)ea).sds.value;
            }
            try {
                if (sa != null)
                {
                    if (((Dsymbol)sa).isAggregateDeclaration() != null)
                    {
                        m = MATCH.convert;
                    }
                    if (this.specType != null)
                    {
                        Declaration d = ((Dsymbol)sa).isDeclaration();
                        if (d == null)
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        if (!d.type.value.equals(this.specType))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                else
                {
                    sa = oarg;
                    if (ea != null)
                    {
                        if (this.specType != null)
                        {
                            if (!ea.type.value.equals(this.specType))
                            {
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                        }
                    }
                    else if ((ta != null) && ((ta.ty.value & 0xFF) == ENUMTY.Tinstance) && (this.specAlias == null))
                    {
                    }
                    else if ((sa != null) && (pequals(sa, TemplateTypeParameter.tdummy)))
                    {
                    }
                    else if (ta != null)
                    {
                        m = MATCH.convert;
                    }
                    else
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                }
                if (this.specAlias != null)
                {
                    if ((pequals(sa, sdummy)))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    Dsymbol sx = isDsymbol(sa);
                    if ((!pequals(sa, this.specAlias)) && (sx != null))
                    {
                        Type talias = isType(this.specAlias);
                        if (talias == null)
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        TemplateInstance ti = sx.isTemplateInstance();
                        if ((ti == null) && (sx.parent.value != null))
                        {
                            ti = sx.parent.value.isTemplateInstance();
                            if ((ti != null) && (!pequals(ti.name.value, sx.ident.value)))
                            {
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                        }
                        if (ti == null)
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        Type t = new TypeInstance(Loc.initial.value, ti);
                        int m2 = deduceType(t, sc, talias, parameters, dedtypes, null, 0, false);
                        if ((m2 <= MATCH.nomatch))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                else if ((dedtypes.get()).get(i) != null)
                {
                    RootObject si = (dedtypes.get()).get(i);
                    if ((sa == null) || (!pequals(si, sa)))
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                }
                dedtypes.get().set(i, sa);
                if (psparam != null)
                {
                    {
                        Dsymbol s = isDsymbol(sa);
                        if ((s) != null)
                        {
                            psparam.set(0, (new AliasDeclaration(this.loc.value, this.ident.value, s)));
                        }
                        else {
                            Type t = isType(sa);
                            if ((t) != null)
                            {
                                psparam.set(0, (new AliasDeclaration(this.loc.value, this.ident.value, t)));
                            }
                            else
                            {
                                assert(ea != null);
                                Initializer _init = new ExpInitializer(this.loc.value, ea);
                                VarDeclaration v = new VarDeclaration(this.loc.value, null, this.ident.value, _init, 0L);
                                v.storage_class.value = 8388608L;
                                dsymbolSemantic(v, sc);
                                psparam.set(0, v);
                            }
                        }
                    }
                }
                return this.dependent.value ? MATCH.exact : m;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
            {
                psparam.set(0, null);
            }
            return MATCH.nomatch;
        }

        public  Object dummyArg() {
            RootObject s = this.specAlias;
            if (s == null)
            {
                if (sdummy == null)
                {
                    sdummy = new Dsymbol();
                }
                s = sdummy;
            }
            return s;
        }

        public  void accept(Visitor v) {
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
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateTupleParameter extends TemplateParameter
    {
        public  TemplateTupleParameter(Loc loc, Identifier ident) {
            super(loc, ident);
        }

        public  TemplateTupleParameter isTemplateTupleParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateTupleParameter(this.loc.value, this.ident.value);
        }

        public  boolean declareParameter(Ptr<Scope> sc) {
            TypeIdentifier ti = new TypeIdentifier(this.loc.value, this.ident.value);
            Declaration ad = new AliasDeclaration(this.loc.value, this.ident.value, ti);
            return (sc.get()).insert(ad) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s... ["), this.ident.value.toChars());
            Tuple v = isTuple(oded);
            assert(v != null);
            {
                int i = 0;
                for (; (i < v.objects.value.length.value);i++){
                    if (i != 0)
                    {
                        printf(new BytePtr(", "));
                    }
                    RootObject o = v.objects.value.get(i);
                    Dsymbol sa = isDsymbol(o);
                    if (sa != null)
                    {
                        printf(new BytePtr("alias: %s"), sa.toChars());
                    }
                    Type ta = isType(o);
                    if (ta != null)
                    {
                        printf(new BytePtr("type: %s"), ta.toChars());
                    }
                    Expression ea = isExpression(o);
                    if (ea != null)
                    {
                        printf(new BytePtr("exp: %s"), ea.toChars());
                    }
                    assert(isTuple(o) == null);
                }
            }
            printf(new BytePtr("]\n"));
        }

        public  RootObject specialization() {
            return null;
        }

        public  RootObject defaultArg(Loc instLoc, Ptr<Scope> sc) {
            return null;
        }

        public  boolean hasDefaultArg() {
            return false;
        }

        public  int matchArg(Loc instLoc, Ptr<Scope> sc, Ptr<DArray<RootObject>> tiargs, int i, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<Declaration> psparam) {
            assert((i + 1 == (dedtypes.get()).length.value));
            Tuple ovar = null;
            {
                Tuple u = isTuple((dedtypes.get()).get(i));
                if ((u) != null)
                {
                    ovar = u;
                }
                else if ((i + 1 == (tiargs.get()).length.value) && (isTuple((tiargs.get()).get(i)) != null))
                {
                    ovar = isTuple((tiargs.get()).get(i));
                }
                else
                {
                    ovar = new Tuple();
                    if ((i < (tiargs.get()).length.value))
                    {
                        ovar.objects.value.setDim((tiargs.get()).length.value - i);
                        {
                            int j = 0;
                            for (; (j < ovar.objects.value.length.value);j++) {
                                ovar.objects.value.set(j, (tiargs.get()).get(i + j));
                            }
                        }
                    }
                }
            }
            return this.matchArg(sc, ovar, i, parameters, dedtypes, psparam);
        }

        public  int matchArg(Ptr<Scope> sc, RootObject oarg, int i, Ptr<DArray<TemplateParameter>> parameters, Ptr<DArray<RootObject>> dedtypes, Ptr<Declaration> psparam) {
            Tuple ovar = isTuple(oarg);
            if (ovar == null)
            {
                return MATCH.nomatch;
            }
            if ((dedtypes.get()).get(i) != null)
            {
                Tuple tup = isTuple((dedtypes.get()).get(i));
                if (tup == null)
                {
                    return MATCH.nomatch;
                }
                if (!match(tup, ovar))
                {
                    return MATCH.nomatch;
                }
            }
            dedtypes.get().set(i, ovar);
            if (psparam != null)
            {
                psparam.set(0, (new TupleDeclaration(this.loc.value, this.ident.value, ptr(ovar.objects))));
            }
            return this.dependent.value ? MATCH.exact : MATCH.convert;
        }

        public  Object dummyArg() {
            return null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateTupleParameter() {}

        public TemplateTupleParameter copy() {
            TemplateTupleParameter that = new TemplateTupleParameter();
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateInstance extends ScopeDsymbol
    {
        public Ref<Identifier> name = ref(null);
        public Ref<Ptr<DArray<RootObject>>> tiargs = ref(null);
        public Ref<DArray<RootObject>> tdtypes = ref(new DArray<RootObject>());
        public Ref<DArray<dmodule.Module>> importedModules = ref(new DArray<dmodule.Module>());
        public Ref<Dsymbol> tempdecl = ref(null);
        public Ref<Dsymbol> enclosing = ref(null);
        public Ref<Dsymbol> aliasdecl = ref(null);
        public Ref<TemplateInstance> inst = ref(null);
        public ScopeDsymbol argsym = null;
        public int inuse = 0;
        public int nest = 0;
        public boolean semantictiargsdone = false;
        public boolean havetempdecl = false;
        public boolean gagged = false;
        public int hash = 0;
        public Ptr<DArray<Expression>> fargs = null;
        public Ptr<DArray<TemplateInstance>> deferred = null;
        public dmodule.Module memberOf = null;
        public TemplateInstance tinst = null;
        public TemplateInstance tnext = null;
        public Ref<dmodule.Module> minst = ref(null);
        public  TemplateInstance(Loc loc, Identifier ident, Ptr<DArray<RootObject>> tiargs) {
            super(loc, null);
            this.name.value = ident;
            this.tiargs.value = tiargs;
        }

        public  TemplateInstance(Loc loc, TemplateDeclaration td, Ptr<DArray<RootObject>> tiargs) {
            super(loc, null);
            this.name.value = td.ident.value;
            this.tiargs.value = tiargs;
            this.tempdecl.value = td;
            this.semantictiargsdone = true;
            this.havetempdecl = true;
            assert(this.tempdecl.value._scope.value != null);
        }

        public static Ptr<DArray<RootObject>> arraySyntaxCopy(Ptr<DArray<RootObject>> objs) {
            Ptr<DArray<RootObject>> a = null;
            if (objs != null)
            {
                a = refPtr(new DArray<RootObject>((objs.get()).length.value));
                {
                    int i = 0;
                    for (; (i < (objs.get()).length.value);i++) {
                        a.get().set(i, objectSyntaxCopy((objs.get()).get(i)));
                    }
                }
            }
            return a;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            TemplateInstance ti = s != null ? (TemplateInstance)s : new TemplateInstance(this.loc.value, this.name.value, null);
            ti.tiargs.value = arraySyntaxCopy(this.tiargs.value);
            TemplateDeclaration td = null;
            if ((this.inst.value != null) && (this.tempdecl.value != null) && ((td = this.tempdecl.value.isTemplateDeclaration()) != null))
            {
                td.syntaxCopy(ti);
            }
            else
            {
                this.syntaxCopy(ti);
            }
            return ti;
        }

        public  Dsymbol toAlias() {
            if (this.inst.value == null)
            {
                if (this._scope.value != null)
                {
                    dsymbolSemantic(this, this._scope.value);
                }
                if (this.inst.value == null)
                {
                    this.error(new BytePtr("cannot resolve forward reference"));
                    this.errors.value = true;
                    return this;
                }
            }
            if ((!pequals(this.inst.value, this)))
            {
                return this.inst.value.toAlias();
            }
            if (this.aliasdecl.value != null)
            {
                return this.aliasdecl.value.toAlias();
            }
            return this.inst.value;
        }

        public  BytePtr kind() {
            return new BytePtr("template instance");
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            ps.set(0, null);
            return true;
        }

        public  BytePtr toChars() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                toCBufferInstance(this, ptr(buf), false);
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  BytePtr toPrettyCharsHelper() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                toCBufferInstance(this, ptr(buf), true);
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  void printInstantiationTrace() {
            if (global.gag.value != 0)
            {
                return ;
            }
            int max_shown = 6;
            BytePtr format = pcopy(new BytePtr("instantiated from here: `%s`"));
            int n_instantiations = 1;
            int n_totalrecursions = 0;
            {
                TemplateInstance cur = this;
                for (; cur != null;cur = cur.tinst){
                    n_instantiations += 1;
                    if ((cur.tinst != null) && (cur.tempdecl.value != null) && (cur.tinst.tempdecl.value != null) && cur.tempdecl.value.loc.value.equals(cur.tinst.tempdecl.value.loc.value))
                    {
                        n_totalrecursions += 1;
                    }
                }
            }
            if ((n_instantiations <= 6) || global.params.verbose)
            {
                {
                    TemplateInstance cur = this;
                    for (; cur != null;cur = cur.tinst){
                        cur.errors.value = true;
                        errorSupplemental(cur.loc.value, format, cur.toChars());
                    }
                }
            }
            else if (((n_instantiations - n_totalrecursions) <= 6))
            {
                int recursionDepth = 0;
                {
                    TemplateInstance cur = this;
                    for (; cur != null;cur = cur.tinst){
                        cur.errors.value = true;
                        if ((cur.tinst != null) && (cur.tempdecl.value != null) && (cur.tinst.tempdecl.value != null) && cur.tempdecl.value.loc.value.equals(cur.tinst.tempdecl.value.loc.value))
                        {
                            recursionDepth += 1;
                        }
                        else
                        {
                            if (recursionDepth != 0)
                            {
                                errorSupplemental(cur.loc.value, new BytePtr("%d recursive instantiations from here: `%s`"), recursionDepth + 2, cur.toChars());
                            }
                            else
                            {
                                errorSupplemental(cur.loc.value, format, cur.toChars());
                            }
                            recursionDepth = 0;
                        }
                    }
                }
            }
            else
            {
                int i = 0;
                {
                    TemplateInstance cur = this;
                    for (; cur != null;cur = cur.tinst){
                        cur.errors.value = true;
                        if ((i == 3))
                        {
                            errorSupplemental(cur.loc.value, new BytePtr("... (%d instantiations, -v to show) ..."), n_instantiations - 6);
                        }
                        if ((i < 3) || (i >= n_instantiations - 6 + 3))
                        {
                            errorSupplemental(cur.loc.value, format, cur.toChars());
                        }
                        i += 1;
                    }
                }
            }
        }

        public  Identifier getIdent() {
            if ((this.ident.value == null) && (this.inst.value != null) && !this.errors.value)
            {
                this.ident.value = this.genIdent(this.tiargs.value);
            }
            return this.ident.value;
        }

        public  boolean equalsx(TemplateInstance ti) {
            assert((this.tdtypes.value.length.value == ti.tdtypes.value.length.value));
            try {
                if ((!pequals(this.enclosing.value, ti.enclosing.value)))
                {
                    /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                }
                if (!arrayObjectMatch(ptr(this.tdtypes), ptr(ti.tdtypes)))
                {
                    /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                }
                {
                    FuncDeclaration fd = ti.toAlias().isFuncDeclaration();
                    if ((fd) != null)
                    {
                        if (!fd.errors.value)
                        {
                            ParameterList fparameters = fd.getParameterList().copy();
                            int nfparams = fparameters.length();
                            {
                                int j = 0;
                            L_outer19:
                                for (; (j < nfparams);j++){
                                    Parameter fparam = fparameters.get(j);
                                    if ((fparam.storageClass.value & 35184372088832L) != 0)
                                    {
                                        Expression farg = (this.fargs != null) && (j < (this.fargs.get()).length.value) ? (this.fargs.get()).get(j) : fparam.defaultArg.value;
                                        if (farg == null)
                                        {
                                            /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                                        }
                                        if (farg.isLvalue())
                                        {
                                            if ((fparam.storageClass.value & 2097152L) == 0)
                                            {
                                                /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        else
                                        {
                                            if ((fparam.storageClass.value & 2097152L) != 0)
                                            {
                                                /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
            catch(Dispatch0 __d){}
        /*Lnotequals:*/
            return false;
        }

        public  int toHash() {
            if (this.hash == 0)
            {
                this.hash = ((int)this.enclosing.value);
                this.hash += arrayObjectHash(ptr(this.tdtypes));
                this.hash += ((this.hash == 0) ? 1 : 0);
            }
            return this.hash;
        }

        public  boolean needsCodegen() {
            if (global.params.allInst)
            {
                if (this.enclosing.value != null)
                {
                    if (this.enclosing.value.isFuncDeclaration() == null)
                    {
                        return true;
                    }
                    {
                        TemplateInstance ti = this.enclosing.value.isInstantiated();
                        if ((ti) != null)
                        {
                            return ti.needsCodegen();
                        }
                    }
                    return !this.enclosing.value.inNonRoot();
                }
                return true;
            }
            if (this.minst.value == null)
            {
                TemplateInstance tnext = this.tnext;
                TemplateInstance tinst = this.tinst;
                this.tnext = null;
                this.tinst = null;
                if ((tinst != null) && tinst.needsCodegen())
                {
                    this.minst.value = tinst.minst.value;
                    assert(this.minst.value != null);
                    assert(this.minst.value.isRoot() || this.minst.value.rootImports());
                    return true;
                }
                if ((tnext != null) && tnext.needsCodegen() || (tnext.minst.value != null))
                {
                    this.minst.value = tnext.minst.value;
                    assert(this.minst.value != null);
                    return this.minst.value.isRoot() || this.minst.value.rootImports();
                }
                return false;
            }
            if ((this.enclosing.value != null) && this.enclosing.value.inNonRoot())
            {
                if (this.tinst != null)
                {
                    boolean r = this.tinst.needsCodegen();
                    this.minst.value = this.tinst.minst.value;
                    return r;
                }
                if (this.tnext != null)
                {
                    boolean r = this.tnext.needsCodegen();
                    this.minst.value = this.tnext.minst.value;
                    return r;
                }
                return false;
            }
            if (global.params.useUnitTests || (global.params.debuglevel != 0))
            {
                if (this.minst.value.isRoot())
                {
                    return true;
                }
                TemplateInstance tnext = this.tnext;
                TemplateInstance tinst = this.tinst;
                this.tnext = null;
                this.tinst = null;
                if ((tinst != null) && tinst.needsCodegen())
                {
                    this.minst.value = tinst.minst.value;
                    assert(this.minst.value != null);
                    assert(this.minst.value.isRoot() || this.minst.value.rootImports());
                    return true;
                }
                if ((tnext != null) && tnext.needsCodegen())
                {
                    this.minst.value = tnext.minst.value;
                    assert(this.minst.value != null);
                    assert(this.minst.value.isRoot() || this.minst.value.rootImports());
                    return true;
                }
                if (this.minst.value.rootImports())
                {
                    return true;
                }
                return false;
            }
            else
            {
                if (!this.minst.value.isRoot() && !this.minst.value.rootImports())
                {
                    return false;
                }
                TemplateInstance tnext = this.tnext;
                this.tnext = null;
                if ((tnext != null) && !tnext.needsCodegen() && (tnext.minst.value != null))
                {
                    this.minst.value = tnext.minst.value;
                    assert(!this.minst.value.isRoot());
                    return false;
                }
                return true;
            }
        }

        public  boolean findTempDecl(Ptr<Scope> sc, Ptr<WithScopeSymbol> pwithsym) {
            if (pwithsym != null)
            {
                pwithsym.set(0, null);
            }
            if (this.havetempdecl)
            {
                return true;
            }
            if (this.tempdecl.value == null)
            {
                Identifier id = this.name.value;
                Ref<Dsymbol> scopesym = ref(null);
                Dsymbol s = (sc.get()).search(this.loc.value, id, ptr(scopesym), 0);
                if (s == null)
                {
                    s = (sc.get()).search_correct(id);
                    if (s != null)
                    {
                        this.error(new BytePtr("template `%s` is not defined, did you mean %s?"), id.toChars(), s.toChars());
                    }
                    else
                    {
                        this.error(new BytePtr("template `%s` is not defined"), id.toChars());
                    }
                    return false;
                }
                if (pwithsym != null)
                {
                    pwithsym.set(0, scopesym.value.isWithScopeSymbol());
                }
                TemplateInstance ti = null;
                if ((s.parent.value != null) && ((ti = s.parent.value.isTemplateInstance()) != null))
                {
                    if ((ti.tempdecl.value != null) && (pequals(ti.tempdecl.value.ident.value, id)))
                    {
                        TemplateDeclaration td = ti.tempdecl.value.isTemplateDeclaration();
                        assert(td != null);
                        if (td.overroot.value != null)
                        {
                            td = td.overroot.value;
                        }
                        s = td;
                    }
                }
                if (!this.updateTempDecl(sc, s))
                {
                    return false;
                }
            }
            assert(this.tempdecl.value != null);
            OverloadSet tovers = this.tempdecl.value.isOverloadSet();
            {
                int __key1244 = 0;
                int __limit1245 = tovers != null ? tovers.a.length.value : 1;
                for (; (__key1244 < __limit1245);__key1244 += 1) {
                    int oi = __key1244;
                    Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl.value;
                    Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                        public Integer invoke(Dsymbol s) {
                            TemplateDeclaration td = s.isTemplateDeclaration();
                            if (td == null)
                            {
                                return 0;
                            }
                            if ((td.semanticRun.value == PASS.init))
                            {
                                if (td._scope.value != null)
                                {
                                    Ungag ungag = td.ungagSpeculative().copy();
                                    try {
                                        dsymbolSemantic(td, td._scope.value);
                                    }
                                    finally {
                                    }
                                }
                                if ((td.semanticRun.value == PASS.init))
                                {
                                    error(new BytePtr("`%s` forward references template declaration `%s`"), toChars(), td.toChars());
                                    return 1;
                                }
                            }
                            return 0;
                        }
                    };
                    int r = overloadApply(dstart, __lambda3, null);
                    if (r != 0)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public  boolean updateTempDecl(Ptr<Scope> sc, Dsymbol s) {
            if (s != null)
            {
                Identifier id = this.name.value;
                s = s.toAlias();
                OverloadSet os = s.isOverloadSet();
                if (os != null)
                {
                    s = null;
                    {
                        int i = 0;
                        for (; (i < os.a.length.value);i++){
                            Dsymbol s2 = os.a.get(i);
                            {
                                FuncDeclaration f = s2.isFuncDeclaration();
                                if ((f) != null)
                                {
                                    s2 = f.findTemplateDeclRoot();
                                }
                                else
                                {
                                    s2 = s2.isTemplateDeclaration();
                                }
                            }
                            if (s2 != null)
                            {
                                if (s != null)
                                {
                                    this.tempdecl.value = os;
                                    return true;
                                }
                                s = s2;
                            }
                        }
                    }
                    if (s == null)
                    {
                        this.error(new BytePtr("template `%s` is not defined"), id.toChars());
                        return false;
                    }
                }
                OverDeclaration od = s.isOverDeclaration();
                if (od != null)
                {
                    this.tempdecl.value = od;
                    return true;
                }
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if ((f) != null)
                    {
                        this.tempdecl.value = f.findTemplateDeclRoot();
                    }
                    else
                    {
                        this.tempdecl.value = s.isTemplateDeclaration();
                    }
                }
                if (this.tempdecl.value == null)
                {
                    if ((s.parent.value == null) && (global.errors.value != 0))
                    {
                        return false;
                    }
                    if ((s.parent.value == null) && (s.getType() != null))
                    {
                        Dsymbol s2 = s.getType().toDsymbol(sc);
                        if (s2 == null)
                        {
                            error(this.loc.value, new BytePtr("`%s` is not a valid template instance, because `%s` is not a template declaration but a type (`%s == %s`)"), this.toChars(), id.toChars(), id.toChars(), s.getType().kind());
                            return false;
                        }
                        s = s2;
                    }
                    TemplateInstance ti = s.parent.value != null ? s.parent.value.isTemplateInstance() : null;
                    if ((ti != null) && (pequals(ti.name.value, s.ident.value)) || (pequals(ti.toAlias().ident.value, s.ident.value)) && (ti.tempdecl.value != null))
                    {
                        TemplateDeclaration td = ti.tempdecl.value.isTemplateDeclaration();
                        assert(td != null);
                        if (td.overroot.value != null)
                        {
                            td = td.overroot.value;
                        }
                        this.tempdecl.value = td;
                    }
                    else
                    {
                        this.error(new BytePtr("`%s` is not a template declaration, it is a %s"), id.toChars(), s.kind());
                        return false;
                    }
                }
            }
            return this.tempdecl.value != null;
        }

        public static boolean semanticTiargs(Loc loc, Ptr<Scope> sc, Ptr<DArray<RootObject>> tiargs, int flags) {
            if (tiargs == null)
            {
                return true;
            }
            boolean err = false;
            {
                int j = 0;
            L_outer20:
                for (; (j < (tiargs.get()).length.value);j++){
                    RootObject o = (tiargs.get()).get(j);
                    Ref<Type> ta = ref(isType(o));
                    Ref<Expression> ea = ref(isExpression(o));
                    Ref<Dsymbol> sa = ref(isDsymbol(o));
                    if (ta.value != null)
                    {
                        resolve(ta.value, loc, sc, ptr(ea), ptr(ta), ptr(sa), (flags & 1) != 0);
                        if (ea.value != null)
                        {
                            /*goto Lexpr*//*unrolled goto*/
                        /*Lexpr:*/
                            if ((flags & 1) != 0)
                            {
                                ea.value = expressionSemantic(ea.value, sc);
                                if (((ea.value.op.value & 0xFF) != 26) || ((((VarExp)ea.value).var.value.storage_class.value & 262144L) != 0))
                                {
                                    ea.value = ea.value.optimize(0, false);
                                }
                            }
                            else
                            {
                                sc = (sc.get()).startCTFE();
                                ea.value = expressionSemantic(ea.value, sc);
                                sc = (sc.get()).endCTFE();
                                if (((ea.value.op.value & 0xFF) == 26))
                                {
                                }
                                else if (definitelyValueParameter(ea.value))
                                {
                                    if (ea.value.checkValue())
                                    {
                                        ea.value = new ErrorExp();
                                    }
                                    int olderrs = global.errors.value;
                                    ea.value = ea.value.ctfeInterpret();
                                    if ((global.errors.value != olderrs))
                                    {
                                        ea.value = new ErrorExp();
                                    }
                                }
                            }
                            if (((ea.value.op.value & 0xFF) == 126))
                            {
                                TupleExp te = (TupleExp)ea.value;
                                int dim = (te.exps.value.get()).length.value;
                                (tiargs.get()).remove(j);
                                if (dim != 0)
                                {
                                    (tiargs.get()).reserve(dim);
                                    {
                                        int i = 0;
                                        for (; (i < dim);i++) {
                                            (tiargs.get()).insert(j + i, (te.exps.value.get()).get(i));
                                        }
                                    }
                                }
                                j--;
                                continue L_outer20;
                            }
                            if (((ea.value.op.value & 0xFF) == 127))
                            {
                                err = true;
                                continue L_outer20;
                            }
                            tiargs.get().set(j, ea.value);
                            if (((ea.value.op.value & 0xFF) == 20))
                            {
                                ta.value = ea.value.type.value;
                                /*goto Ltype*/throw Dispatch0.INSTANCE;
                            }
                            if (((ea.value.op.value & 0xFF) == 203))
                            {
                                sa.value = ((ScopeExp)ea.value).sds.value;
                                /*goto Ldsym*//*unrolled goto*/
                            /*Ldsym:*/
                                if (sa.value.errors.value)
                                {
                                    err = true;
                                    continue L_outer20;
                                }
                                TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                                if (d != null)
                                {
                                    (tiargs.get()).remove(j);
                                    (tiargs.get()).insert(j, d.objects.value);
                                    j--;
                                    continue L_outer20;
                                }
                                {
                                    FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                    if ((fa) != null)
                                    {
                                        FuncDeclaration f = fa.toAliasFunc();
                                        if (!fa.hasOverloads && f.isUnique())
                                        {
                                            sa.value = f;
                                        }
                                    }
                                }
                                tiargs.get().set(j, sa.value);
                                TemplateDeclaration td = sa.value.isTemplateDeclaration();
                                if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                                {
                                    dsymbolSemantic(td, sc);
                                }
                                FuncDeclaration fd = sa.value.isFuncDeclaration();
                                if (fd != null)
                                {
                                    fd.functionSemantic();
                                }
                            }
                            if (((ea.value.op.value & 0xFF) == 161))
                            {
                                FuncExp fe = (FuncExp)ea.value;
                                if (((fe.fd.value.tok.value & 0xFF) == 0) && ((fe.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                                {
                                    fe.fd.value.tok.value = TOK.function_;
                                    fe.fd.value.vthis.value = null;
                                }
                                else if (fe.td.value != null)
                                {
                                }
                            }
                            if (((ea.value.op.value & 0xFF) == 27) && ((flags & 1) == 0))
                            {
                                sa.value = ((DotVarExp)ea.value).var.value;
                                /*goto Ldsym*//*unrolled goto*/
                            /*Ldsym:*/
                                if (sa.value.errors.value)
                                {
                                    err = true;
                                    continue L_outer20;
                                }
                                TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                                if (d != null)
                                {
                                    (tiargs.get()).remove(j);
                                    (tiargs.get()).insert(j, d.objects.value);
                                    j--;
                                    continue L_outer20;
                                }
                                {
                                    FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                    if ((fa) != null)
                                    {
                                        FuncDeclaration f = fa.toAliasFunc();
                                        if (!fa.hasOverloads && f.isUnique())
                                        {
                                            sa.value = f;
                                        }
                                    }
                                }
                                tiargs.get().set(j, sa.value);
                                TemplateDeclaration td = sa.value.isTemplateDeclaration();
                                if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                                {
                                    dsymbolSemantic(td, sc);
                                }
                                FuncDeclaration fd = sa.value.isFuncDeclaration();
                                if (fd != null)
                                {
                                    fd.functionSemantic();
                                }
                            }
                            if (((ea.value.op.value & 0xFF) == 36))
                            {
                                sa.value = ((TemplateExp)ea.value).td.value;
                                /*goto Ldsym*//*unrolled goto*/
                            /*Ldsym:*/
                                if (sa.value.errors.value)
                                {
                                    err = true;
                                    continue L_outer20;
                                }
                                TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                                if (d != null)
                                {
                                    (tiargs.get()).remove(j);
                                    (tiargs.get()).insert(j, d.objects.value);
                                    j--;
                                    continue L_outer20;
                                }
                                {
                                    FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                    if ((fa) != null)
                                    {
                                        FuncDeclaration f = fa.toAliasFunc();
                                        if (!fa.hasOverloads && f.isUnique())
                                        {
                                            sa.value = f;
                                        }
                                    }
                                }
                                tiargs.get().set(j, sa.value);
                                TemplateDeclaration td = sa.value.isTemplateDeclaration();
                                if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                                {
                                    dsymbolSemantic(td, sc);
                                }
                                FuncDeclaration fd = sa.value.isFuncDeclaration();
                                if (fd != null)
                                {
                                    fd.functionSemantic();
                                }
                            }
                            if (((ea.value.op.value & 0xFF) == 37) && ((flags & 1) == 0))
                            {
                                sa.value = ((DotTemplateExp)ea.value).td.value;
                                /*goto Ldsym*//*unrolled goto*/
                            /*Ldsym:*/
                                if (sa.value.errors.value)
                                {
                                    err = true;
                                    continue L_outer20;
                                }
                                TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                                if (d != null)
                                {
                                    (tiargs.get()).remove(j);
                                    (tiargs.get()).insert(j, d.objects.value);
                                    j--;
                                    continue L_outer20;
                                }
                                {
                                    FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                    if ((fa) != null)
                                    {
                                        FuncDeclaration f = fa.toAliasFunc();
                                        if (!fa.hasOverloads && f.isUnique())
                                        {
                                            sa.value = f;
                                        }
                                    }
                                }
                                tiargs.get().set(j, sa.value);
                                TemplateDeclaration td = sa.value.isTemplateDeclaration();
                                if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                                {
                                    dsymbolSemantic(td, sc);
                                }
                                FuncDeclaration fd = sa.value.isFuncDeclaration();
                                if (fd != null)
                                {
                                    fd.functionSemantic();
                                }
                            }
                        }
                        if (sa.value != null)
                        {
                            /*goto Ldsym*//*unrolled goto*/
                        /*Ldsym:*/
                            if (sa.value.errors.value)
                            {
                                err = true;
                                continue L_outer20;
                            }
                            TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                            if (d != null)
                            {
                                (tiargs.get()).remove(j);
                                (tiargs.get()).insert(j, d.objects.value);
                                j--;
                                continue L_outer20;
                            }
                            {
                                FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                if ((fa) != null)
                                {
                                    FuncDeclaration f = fa.toAliasFunc();
                                    if (!fa.hasOverloads && f.isUnique())
                                    {
                                        sa.value = f;
                                    }
                                }
                            }
                            tiargs.get().set(j, sa.value);
                            TemplateDeclaration td = sa.value.isTemplateDeclaration();
                            if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                            {
                                dsymbolSemantic(td, sc);
                            }
                            FuncDeclaration fd = sa.value.isFuncDeclaration();
                            if (fd != null)
                            {
                                fd.functionSemantic();
                            }
                        }
                        if ((ta.value == null))
                        {
                            assert(global.errors.value != 0);
                            ta.value = Type.terror.value;
                        }
                    /*Ltype:*/
                        if (((ta.value.ty.value & 0xFF) == ENUMTY.Ttuple))
                        {
                            TypeTuple tt = (TypeTuple)ta.value;
                            int dim = (tt.arguments.value.get()).length.value;
                            (tiargs.get()).remove(j);
                            if (dim != 0)
                            {
                                (tiargs.get()).reserve(dim);
                                {
                                    int i = 0;
                                    for (; (i < dim);i++){
                                        Parameter arg = (tt.arguments.value.get()).get(i);
                                        if (((flags & 2) != 0) && (arg.ident.value != null) || (arg.userAttribDecl.value != null))
                                        {
                                            (tiargs.get()).insert(j + i, arg);
                                        }
                                        else
                                        {
                                            (tiargs.get()).insert(j + i, arg.type.value);
                                        }
                                    }
                                }
                            }
                            j--;
                            continue L_outer20;
                        }
                        if (((ta.value.ty.value & 0xFF) == ENUMTY.Terror))
                        {
                            err = true;
                            continue L_outer20;
                        }
                        tiargs.get().set(j, ta.value.merge2());
                    }
                    else if (ea.value != null)
                    {
                    /*Lexpr:*/
                        if ((flags & 1) != 0)
                        {
                            ea.value = expressionSemantic(ea.value, sc);
                            if (((ea.value.op.value & 0xFF) != 26) || ((((VarExp)ea.value).var.value.storage_class.value & 262144L) != 0))
                            {
                                ea.value = ea.value.optimize(0, false);
                            }
                        }
                        else
                        {
                            sc = (sc.get()).startCTFE();
                            ea.value = expressionSemantic(ea.value, sc);
                            sc = (sc.get()).endCTFE();
                            if (((ea.value.op.value & 0xFF) == 26))
                            {
                            }
                            else if (definitelyValueParameter(ea.value))
                            {
                                if (ea.value.checkValue())
                                {
                                    ea.value = new ErrorExp();
                                }
                                int olderrs = global.errors.value;
                                ea.value = ea.value.ctfeInterpret();
                                if ((global.errors.value != olderrs))
                                {
                                    ea.value = new ErrorExp();
                                }
                            }
                        }
                        if (((ea.value.op.value & 0xFF) == 126))
                        {
                            TupleExp te = (TupleExp)ea.value;
                            int dim = (te.exps.value.get()).length.value;
                            (tiargs.get()).remove(j);
                            if (dim != 0)
                            {
                                (tiargs.get()).reserve(dim);
                                {
                                    int i = 0;
                                    for (; (i < dim);i++) {
                                        (tiargs.get()).insert(j + i, (te.exps.value.get()).get(i));
                                    }
                                }
                            }
                            j--;
                            continue L_outer20;
                        }
                        if (((ea.value.op.value & 0xFF) == 127))
                        {
                            err = true;
                            continue L_outer20;
                        }
                        tiargs.get().set(j, ea.value);
                        if (((ea.value.op.value & 0xFF) == 20))
                        {
                            ta.value = ea.value.type.value;
                            /*goto Ltype*/throw Dispatch0.INSTANCE;
                        }
                        if (((ea.value.op.value & 0xFF) == 203))
                        {
                            sa.value = ((ScopeExp)ea.value).sds.value;
                            /*goto Ldsym*//*unrolled goto*/
                        /*Ldsym:*/
                            if (sa.value.errors.value)
                            {
                                err = true;
                                continue L_outer20;
                            }
                            TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                            if (d != null)
                            {
                                (tiargs.get()).remove(j);
                                (tiargs.get()).insert(j, d.objects.value);
                                j--;
                                continue L_outer20;
                            }
                            {
                                FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                if ((fa) != null)
                                {
                                    FuncDeclaration f = fa.toAliasFunc();
                                    if (!fa.hasOverloads && f.isUnique())
                                    {
                                        sa.value = f;
                                    }
                                }
                            }
                            tiargs.get().set(j, sa.value);
                            TemplateDeclaration td = sa.value.isTemplateDeclaration();
                            if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                            {
                                dsymbolSemantic(td, sc);
                            }
                            FuncDeclaration fd = sa.value.isFuncDeclaration();
                            if (fd != null)
                            {
                                fd.functionSemantic();
                            }
                        }
                        if (((ea.value.op.value & 0xFF) == 161))
                        {
                            FuncExp fe = (FuncExp)ea.value;
                            if (((fe.fd.value.tok.value & 0xFF) == 0) && ((fe.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                            {
                                fe.fd.value.tok.value = TOK.function_;
                                fe.fd.value.vthis.value = null;
                            }
                            else if (fe.td.value != null)
                            {
                            }
                        }
                        if (((ea.value.op.value & 0xFF) == 27) && ((flags & 1) == 0))
                        {
                            sa.value = ((DotVarExp)ea.value).var.value;
                            /*goto Ldsym*//*unrolled goto*/
                        /*Ldsym:*/
                            if (sa.value.errors.value)
                            {
                                err = true;
                                continue L_outer20;
                            }
                            TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                            if (d != null)
                            {
                                (tiargs.get()).remove(j);
                                (tiargs.get()).insert(j, d.objects.value);
                                j--;
                                continue L_outer20;
                            }
                            {
                                FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                if ((fa) != null)
                                {
                                    FuncDeclaration f = fa.toAliasFunc();
                                    if (!fa.hasOverloads && f.isUnique())
                                    {
                                        sa.value = f;
                                    }
                                }
                            }
                            tiargs.get().set(j, sa.value);
                            TemplateDeclaration td = sa.value.isTemplateDeclaration();
                            if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                            {
                                dsymbolSemantic(td, sc);
                            }
                            FuncDeclaration fd = sa.value.isFuncDeclaration();
                            if (fd != null)
                            {
                                fd.functionSemantic();
                            }
                        }
                        if (((ea.value.op.value & 0xFF) == 36))
                        {
                            sa.value = ((TemplateExp)ea.value).td.value;
                            /*goto Ldsym*//*unrolled goto*/
                        /*Ldsym:*/
                            if (sa.value.errors.value)
                            {
                                err = true;
                                continue L_outer20;
                            }
                            TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                            if (d != null)
                            {
                                (tiargs.get()).remove(j);
                                (tiargs.get()).insert(j, d.objects.value);
                                j--;
                                continue L_outer20;
                            }
                            {
                                FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                if ((fa) != null)
                                {
                                    FuncDeclaration f = fa.toAliasFunc();
                                    if (!fa.hasOverloads && f.isUnique())
                                    {
                                        sa.value = f;
                                    }
                                }
                            }
                            tiargs.get().set(j, sa.value);
                            TemplateDeclaration td = sa.value.isTemplateDeclaration();
                            if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                            {
                                dsymbolSemantic(td, sc);
                            }
                            FuncDeclaration fd = sa.value.isFuncDeclaration();
                            if (fd != null)
                            {
                                fd.functionSemantic();
                            }
                        }
                        if (((ea.value.op.value & 0xFF) == 37) && ((flags & 1) == 0))
                        {
                            sa.value = ((DotTemplateExp)ea.value).td.value;
                            /*goto Ldsym*//*unrolled goto*/
                        /*Ldsym:*/
                            if (sa.value.errors.value)
                            {
                                err = true;
                                continue L_outer20;
                            }
                            TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                            if (d != null)
                            {
                                (tiargs.get()).remove(j);
                                (tiargs.get()).insert(j, d.objects.value);
                                j--;
                                continue L_outer20;
                            }
                            {
                                FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                                if ((fa) != null)
                                {
                                    FuncDeclaration f = fa.toAliasFunc();
                                    if (!fa.hasOverloads && f.isUnique())
                                    {
                                        sa.value = f;
                                    }
                                }
                            }
                            tiargs.get().set(j, sa.value);
                            TemplateDeclaration td = sa.value.isTemplateDeclaration();
                            if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                            {
                                dsymbolSemantic(td, sc);
                            }
                            FuncDeclaration fd = sa.value.isFuncDeclaration();
                            if (fd != null)
                            {
                                fd.functionSemantic();
                            }
                        }
                    }
                    else if (sa.value != null)
                    {
                    /*Ldsym:*/
                        if (sa.value.errors.value)
                        {
                            err = true;
                            continue L_outer20;
                        }
                        TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                        if (d != null)
                        {
                            (tiargs.get()).remove(j);
                            (tiargs.get()).insert(j, d.objects.value);
                            j--;
                            continue L_outer20;
                        }
                        {
                            FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                            if ((fa) != null)
                            {
                                FuncDeclaration f = fa.toAliasFunc();
                                if (!fa.hasOverloads && f.isUnique())
                                {
                                    sa.value = f;
                                }
                            }
                        }
                        tiargs.get().set(j, sa.value);
                        TemplateDeclaration td = sa.value.isTemplateDeclaration();
                        if ((td != null) && (td.semanticRun.value == PASS.init) && td.literal)
                        {
                            dsymbolSemantic(td, sc);
                        }
                        FuncDeclaration fd = sa.value.isFuncDeclaration();
                        if (fd != null)
                        {
                            fd.functionSemantic();
                        }
                    }
                    else if (isParameter(o) != null)
                    {
                    }
                    else
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
            return !err;
        }

        public  boolean semanticTiargs(Ptr<Scope> sc) {
            if (this.semantictiargsdone)
            {
                return true;
            }
            if (semanticTiargs(this.loc.value, sc, this.tiargs.value, 0))
            {
                this.semantictiargsdone = true;
                return true;
            }
            return false;
        }

        public  boolean findBestMatch(Ptr<Scope> sc, Ptr<DArray<Expression>> fargs) {
            if (this.havetempdecl)
            {
                TemplateDeclaration tempdecl = this.tempdecl.value.isTemplateDeclaration();
                assert(tempdecl != null);
                assert(tempdecl._scope.value != null);
                this.tdtypes.value.setDim((tempdecl.parameters.get()).length.value);
                if (tempdecl.matchWithInstance(sc, this, ptr(this.tdtypes), fargs, 2) == 0)
                {
                    this.error(new BytePtr("incompatible arguments for template instantiation"));
                    return false;
                }
                return true;
            }
            int errs = global.errors.value;
            TemplateDeclaration td_last = null;
            Ref<DArray<RootObject>> dedtypes = ref(new DArray<RootObject>());
            try {
                OverloadSet tovers = this.tempdecl.value.isOverloadSet();
                {
                    int __key1246 = 0;
                    int __limit1247 = tovers != null ? tovers.a.length.value : 1;
                    for (; (__key1246 < __limit1247);__key1246 += 1) {
                        int oi = __key1246;
                        TemplateDeclaration td_best = null;
                        TemplateDeclaration td_ambig = null;
                        int m_best = MATCH.nomatch;
                        Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl.value;
                        Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                            public Integer invoke(Dsymbol s) {
                                TemplateDeclaration td = s.isTemplateDeclaration();
                                if (td == null)
                                {
                                    return 0;
                                }
                                if (td.inuse.value != 0)
                                {
                                    td.error(loc.value, new BytePtr("recursive template expansion"));
                                    return 1;
                                }
                                if ((pequals(td, td_best)))
                                {
                                    return 0;
                                }
                                if (((td.parameters.get()).length.value < (tiargs.value.get()).length.value))
                                {
                                    if (td.isVariadic() == null)
                                    {
                                        return 0;
                                    }
                                }
                                dedtypes.value.setDim((td.parameters.get()).length.value);
                                dedtypes.value.zero();
                                assert((td.semanticRun.value != PASS.init));
                                int m = td.matchWithInstance(sc, this, ptr(dedtypes), fargs, 0);
                                if ((m <= MATCH.nomatch))
                                {
                                    return 0;
                                }
                                try {
                                    try {
                                        if ((m < m_best))
                                        {
                                            /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                        }
                                        if ((m > m_best))
                                        {
                                            /*goto Ltd*/throw Dispatch1.INSTANCE;
                                        }
                                        {
                                            int c1 = td.leastAsSpecialized(sc, td_best, fargs);
                                            int c2 = td_best.leastAsSpecialized(sc, td, fargs);
                                            if ((c1 > c2))
                                            {
                                                /*goto Ltd*/throw Dispatch1.INSTANCE;
                                            }
                                            if ((c1 < c2))
                                            {
                                                /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        td_ambig = td;
                                        return 0;
                                    }
                                    catch(Dispatch0 __d){}
                                /*Ltd_best:*/
                                    td_ambig = null;
                                    return 0;
                                }
                                catch(Dispatch1 __d){}
                            /*Ltd:*/
                                td_ambig = null;
                                td_best = td;
                                m_best = m;
                                tdtypes.value.setDim(dedtypes.value.length.value);
                                memcpy((BytePtr)(tdtypes.value.tdata()), (dedtypes.value.tdata()), (tdtypes.value.length.value * 4));
                                return 0;
                            }
                        };
                        overloadApply(dstart, __lambda3, null);
                        if (td_ambig != null)
                        {
                            error(this.loc.value, new BytePtr("%s `%s.%s` matches more than one template declaration:\n%s:     `%s`\nand\n%s:     `%s`"), td_best.kind(), td_best.parent.value.toPrettyChars(false), td_best.ident.value.toChars(), td_best.loc.value.toChars(global.params.showColumns.value), td_best.toChars(), td_ambig.loc.value.toChars(global.params.showColumns.value), td_ambig.toChars());
                            return false;
                        }
                        if (td_best != null)
                        {
                            if (td_last == null)
                            {
                                td_last = td_best;
                            }
                            else if ((!pequals(td_last, td_best)))
                            {
                                ScopeDsymbol.multiplyDefined(this.loc.value, td_last, td_best);
                                return false;
                            }
                        }
                    }
                }
                if (td_last != null)
                {
                    int dim = (td_last.parameters.get()).length.value - (td_last.isVariadic() != null ? 1 : 0);
                    {
                        int i = 0;
                        for (; (i < dim);i++){
                            if (((this.tiargs.value.get()).length.value <= i))
                            {
                                (this.tiargs.value.get()).push(this.tdtypes.value.get(i));
                            }
                            assert((i < (this.tiargs.value.get()).length.value));
                            TemplateValueParameter tvp = (td_last.parameters.get()).get(i).isTemplateValueParameter();
                            if (tvp == null)
                            {
                                continue;
                            }
                            assert(this.tdtypes.value.get(i) != null);
                            this.tiargs.value.get().set(i, this.tdtypes.value.get(i));
                        }
                    }
                    if ((td_last.isVariadic() != null) && ((this.tiargs.value.get()).length.value == dim) && (this.tdtypes.value.get(dim) != null))
                    {
                        Tuple va = isTuple(this.tdtypes.value.get(dim));
                        assert(va != null);
                        (this.tiargs.value.get()).pushSlice(va.objects.value.opSlice());
                    }
                }
                else if (this.errors.value && (this.inst.value != null))
                {
                    assert(global.errors.value != 0);
                    return false;
                }
                else
                {
                    TemplateDeclaration tdecl = this.tempdecl.value.isTemplateDeclaration();
                    if ((errs != global.errors.value))
                    {
                        errorSupplemental(this.loc.value, new BytePtr("while looking for match for `%s`"), this.toChars());
                    }
                    else if ((tdecl != null) && (tdecl.overnext.value == null))
                    {
                        this.error(new BytePtr("does not match template declaration `%s`"), tdecl.toChars());
                    }
                    else
                    {
                        error(this.loc.value, new BytePtr("%s `%s.%s` does not match any template declaration"), this.tempdecl.value.kind(), this.tempdecl.value.parent.value.toPrettyChars(false), this.tempdecl.value.ident.value.toChars());
                    }
                    return false;
                }
                this.tempdecl.value = td_last;
                return errs == global.errors.value;
            }
            finally {
            }
        }

        public  boolean needsTypeInference(Ptr<Scope> sc, int flag) {
            if ((this.semanticRun.value != PASS.init))
            {
                return false;
            }
            int olderrs = global.errors.value;
            Ref<DArray<RootObject>> dedtypes = ref(new DArray<RootObject>());
            try {
                int count = 0;
                OverloadSet tovers = this.tempdecl.value.isOverloadSet();
                {
                    int __key1248 = 0;
                    int __limit1249 = tovers != null ? tovers.a.length.value : 1;
                    for (; (__key1248 < __limit1249);__key1248 += 1) {
                        int oi = __key1248;
                        Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl.value;
                        Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                            public Integer invoke(Dsymbol s) {
                                TemplateDeclaration td = s.isTemplateDeclaration();
                                if (td == null)
                                {
                                    return 0;
                                }
                                if (td.inuse.value != 0)
                                {
                                    td.error(loc.value, new BytePtr("recursive template expansion"));
                                    return 1;
                                }
                                if (td.onemember.value == null)
                                {
                                    return 0;
                                }
                                {
                                    TemplateDeclaration td2 = td.onemember.value.isTemplateDeclaration();
                                    if ((td2) != null)
                                    {
                                        if ((td2.onemember.value == null) || (td2.onemember.value.isFuncDeclaration() == null))
                                        {
                                            return 0;
                                        }
                                        if (((tiargs.value.get()).length.value >= (td.parameters.get()).length.value - (td.isVariadic() != null ? 1 : 0)))
                                        {
                                            return 0;
                                        }
                                        return 1;
                                    }
                                }
                                FuncDeclaration fd = td.onemember.value.isFuncDeclaration();
                                if ((fd == null) || ((fd.type.value.ty.value & 0xFF) != ENUMTY.Tfunction))
                                {
                                    return 0;
                                }
                                {
                                    Slice<TemplateParameter> __r1250 = (td.parameters.get()).opSlice().copy();
                                    int __key1251 = 0;
                                    for (; (__key1251 < __r1250.getLength());__key1251 += 1) {
                                        TemplateParameter tp = __r1250.get(__key1251);
                                        if (tp.isTemplateThisParameter() != null)
                                        {
                                            return 1;
                                        }
                                    }
                                }
                                TypeFunction tf = (TypeFunction)fd.type.value;
                                {
                                    int dim = tf.parameterList.length();
                                    if ((dim) != 0)
                                    {
                                        TemplateTupleParameter tp = td.isVariadic();
                                        if ((tp != null) && ((td.parameters.get()).length.value > 1))
                                        {
                                            return 1;
                                        }
                                        if ((tp == null) && ((tiargs.value.get()).length.value < (td.parameters.get()).length.value))
                                        {
                                            {
                                                int __key1252 = (tiargs.value.get()).length.value;
                                                int __limit1253 = (td.parameters.get()).length.value;
                                                for (; (__key1252 < __limit1253);__key1252 += 1) {
                                                    int i = __key1252;
                                                    if (!(td.parameters.get()).get(i).hasDefaultArg())
                                                    {
                                                        return 1;
                                                    }
                                                }
                                            }
                                        }
                                        {
                                            int __key1254 = 0;
                                            int __limit1255 = dim;
                                            for (; (__key1254 < __limit1255);__key1254 += 1) {
                                                int i = __key1254;
                                                if ((tf.parameterList.get(i).storageClass.value & 256L) != 0)
                                                {
                                                    return 1;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (flag == 0)
                                {
                                    dedtypes.value.setDim((td.parameters.get()).length.value);
                                    dedtypes.value.zero();
                                    if ((td.semanticRun.value == PASS.init))
                                    {
                                        if (td._scope.value != null)
                                        {
                                            Ungag ungag = td.ungagSpeculative().copy();
                                            try {
                                                dsymbolSemantic(td, td._scope.value);
                                            }
                                            finally {
                                            }
                                        }
                                        if ((td.semanticRun.value == PASS.init))
                                        {
                                            error(new BytePtr("`%s` forward references template declaration `%s`"), toChars(), td.toChars());
                                            return 1;
                                        }
                                    }
                                    int m = td.matchWithInstance(sc, this, ptr(dedtypes), null, 0);
                                    if ((m <= MATCH.nomatch))
                                    {
                                        return 0;
                                    }
                                }
                                return ((count += 1) > 1) ? 1 : 0;
                            }
                        };
                        int r = overloadApply(dstart, __lambda3, null);
                        if (r != 0)
                        {
                            return true;
                        }
                    }
                }
                if ((olderrs != global.errors.value))
                {
                    if (global.gag.value == 0)
                    {
                        errorSupplemental(this.loc.value, new BytePtr("while looking for match for `%s`"), this.toChars());
                        this.semanticRun.value = PASS.semanticdone;
                        this.inst.value = this;
                    }
                    this.errors.value = true;
                }
                return false;
            }
            finally {
            }
        }

        // defaulted all parameters starting with #2
        public  boolean needsTypeInference(Ptr<Scope> sc) {
            return needsTypeInference(sc, 0);
        }

        public  boolean hasNestedArgs(Ptr<DArray<RootObject>> args, boolean isstatic) {
            int nested = 0;
            if (this.enclosing.value == null)
            {
                {
                    TemplateInstance ti = this.tempdecl.value.toParent().isTemplateInstance();
                    if ((ti) != null)
                    {
                        this.enclosing.value = ti.enclosing.value;
                    }
                }
            }
            {
                int i = 0;
            L_outer21:
                for (; (i < (args.get()).length.value);i++){
                    RootObject o = (args.get()).get(i);
                    Expression ea = isExpression(o);
                    Dsymbol sa = isDsymbol(o);
                    Tuple va = isTuple(o);
                    if (ea != null)
                    {
                        if (((ea.op.value & 0xFF) == 26))
                        {
                            sa = ((VarExp)ea).var.value;
                            /*goto Lsa*//*unrolled goto*/
                        /*Lsa:*/
                            sa = sa.toAlias();
                            TemplateDeclaration td = sa.isTemplateDeclaration();
                            if (td != null)
                            {
                                TemplateInstance ti = sa.toParent().isTemplateInstance();
                                if ((ti != null) && (ti.enclosing.value != null))
                                {
                                    sa = ti;
                                }
                            }
                            TemplateInstance ti = sa.isTemplateInstance();
                            Declaration d = sa.isDeclaration();
                            if ((td != null) && td.literal || (ti != null) && (ti.enclosing.value != null) || (d != null) && !d.isDataseg() && ((d.storage_class.value & 8388608L) == 0) && (d.isFuncDeclaration() == null) || d.isFuncDeclaration().isNested() && (this.isTemplateMixin() == null))
                            {
                                Dsymbol dparent = sa.toParent2();
                                try {
                                    if (dparent == null)
                                    {
                                        /*goto L1*/throw Dispatch0.INSTANCE;
                                    }
                                    else if (this.enclosing.value == null)
                                    {
                                        this.enclosing.value = dparent;
                                    }
                                    else if ((!pequals(this.enclosing.value, dparent)))
                                    {
                                        {
                                            Dsymbol p = this.enclosing.value;
                                        L_outer22:
                                            for (; p != null;p = p.parent.value){
                                                if ((pequals(p, dparent)))
                                                {
                                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                                }
                                            }
                                        }
                                        {
                                            Dsymbol p = dparent;
                                        L_outer23:
                                            for (; p != null;p = p.parent.value){
                                                if ((pequals(p, this.enclosing.value)))
                                                {
                                                    this.enclosing.value = dparent;
                                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                                }
                                            }
                                        }
                                        this.error(new BytePtr("`%s` is nested in both `%s` and `%s`"), this.toChars(), this.enclosing.value.toChars(), dparent.toChars());
                                        this.errors.value = true;
                                    }
                                }
                                catch(Dispatch0 __d){}
                            /*L1:*/
                                nested |= 1;
                            }
                        }
                        if (((ea.op.value & 0xFF) == 123))
                        {
                            sa = ((ThisExp)ea).var.value;
                            /*goto Lsa*//*unrolled goto*/
                        /*Lsa:*/
                            sa = sa.toAlias();
                            TemplateDeclaration td = sa.isTemplateDeclaration();
                            if (td != null)
                            {
                                TemplateInstance ti = sa.toParent().isTemplateInstance();
                                if ((ti != null) && (ti.enclosing.value != null))
                                {
                                    sa = ti;
                                }
                            }
                            TemplateInstance ti = sa.isTemplateInstance();
                            Declaration d = sa.isDeclaration();
                            if ((td != null) && td.literal || (ti != null) && (ti.enclosing.value != null) || (d != null) && !d.isDataseg() && ((d.storage_class.value & 8388608L) == 0) && (d.isFuncDeclaration() == null) || d.isFuncDeclaration().isNested() && (this.isTemplateMixin() == null))
                            {
                                Dsymbol dparent = sa.toParent2();
                                try {
                                    if (dparent == null)
                                    {
                                        /*goto L1*/throw Dispatch0.INSTANCE;
                                    }
                                    else if (this.enclosing.value == null)
                                    {
                                        this.enclosing.value = dparent;
                                    }
                                    else if ((!pequals(this.enclosing.value, dparent)))
                                    {
                                        {
                                            Dsymbol p = this.enclosing.value;
                                        L_outer24:
                                            for (; p != null;p = p.parent.value){
                                                if ((pequals(p, dparent)))
                                                {
                                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                                }
                                            }
                                        }
                                        {
                                            Dsymbol p = dparent;
                                        L_outer25:
                                            for (; p != null;p = p.parent.value){
                                                if ((pequals(p, this.enclosing.value)))
                                                {
                                                    this.enclosing.value = dparent;
                                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                                }
                                            }
                                        }
                                        this.error(new BytePtr("`%s` is nested in both `%s` and `%s`"), this.toChars(), this.enclosing.value.toChars(), dparent.toChars());
                                        this.errors.value = true;
                                    }
                                }
                                catch(Dispatch0 __d){}
                            /*L1:*/
                                nested |= 1;
                            }
                        }
                        if (((ea.op.value & 0xFF) == 161))
                        {
                            if (((FuncExp)ea).td.value != null)
                            {
                                sa = ((FuncExp)ea).td.value;
                            }
                            else
                            {
                                sa = ((FuncExp)ea).fd.value;
                            }
                            /*goto Lsa*//*unrolled goto*/
                        /*Lsa:*/
                            sa = sa.toAlias();
                            TemplateDeclaration td = sa.isTemplateDeclaration();
                            if (td != null)
                            {
                                TemplateInstance ti = sa.toParent().isTemplateInstance();
                                if ((ti != null) && (ti.enclosing.value != null))
                                {
                                    sa = ti;
                                }
                            }
                            TemplateInstance ti = sa.isTemplateInstance();
                            Declaration d = sa.isDeclaration();
                            if ((td != null) && td.literal || (ti != null) && (ti.enclosing.value != null) || (d != null) && !d.isDataseg() && ((d.storage_class.value & 8388608L) == 0) && (d.isFuncDeclaration() == null) || d.isFuncDeclaration().isNested() && (this.isTemplateMixin() == null))
                            {
                                Dsymbol dparent = sa.toParent2();
                                try {
                                    if (dparent == null)
                                    {
                                        /*goto L1*/throw Dispatch0.INSTANCE;
                                    }
                                    else if (this.enclosing.value == null)
                                    {
                                        this.enclosing.value = dparent;
                                    }
                                    else if ((!pequals(this.enclosing.value, dparent)))
                                    {
                                        {
                                            Dsymbol p = this.enclosing.value;
                                        L_outer26:
                                            for (; p != null;p = p.parent.value){
                                                if ((pequals(p, dparent)))
                                                {
                                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                                }
                                            }
                                        }
                                        {
                                            Dsymbol p = dparent;
                                        L_outer27:
                                            for (; p != null;p = p.parent.value){
                                                if ((pequals(p, this.enclosing.value)))
                                                {
                                                    this.enclosing.value = dparent;
                                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                                }
                                            }
                                        }
                                        this.error(new BytePtr("`%s` is nested in both `%s` and `%s`"), this.toChars(), this.enclosing.value.toChars(), dparent.toChars());
                                        this.errors.value = true;
                                    }
                                }
                                catch(Dispatch0 __d){}
                            /*L1:*/
                                nested |= 1;
                            }
                        }
                        if (((ea.op.value & 0xFF) != 135) && ((ea.op.value & 0xFF) != 140) && ((ea.op.value & 0xFF) != 147) && ((ea.op.value & 0xFF) != 13) && ((ea.op.value & 0xFF) != 121) && ((ea.op.value & 0xFF) != 47) && ((ea.op.value & 0xFF) != 48) && ((ea.op.value & 0xFF) != 49))
                        {
                            ea.error(new BytePtr("expression `%s` is not a valid template value argument"), ea.toChars());
                            this.errors.value = true;
                        }
                    }
                    else if (sa != null)
                    {
                    /*Lsa:*/
                        sa = sa.toAlias();
                        TemplateDeclaration td = sa.isTemplateDeclaration();
                        if (td != null)
                        {
                            TemplateInstance ti = sa.toParent().isTemplateInstance();
                            if ((ti != null) && (ti.enclosing.value != null))
                            {
                                sa = ti;
                            }
                        }
                        TemplateInstance ti = sa.isTemplateInstance();
                        Declaration d = sa.isDeclaration();
                        if ((td != null) && td.literal || (ti != null) && (ti.enclosing.value != null) || (d != null) && !d.isDataseg() && ((d.storage_class.value & 8388608L) == 0) && (d.isFuncDeclaration() == null) || d.isFuncDeclaration().isNested() && (this.isTemplateMixin() == null))
                        {
                            Dsymbol dparent = sa.toParent2();
                            try {
                                if (dparent == null)
                                {
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                }
                                else if (this.enclosing.value == null)
                                {
                                    this.enclosing.value = dparent;
                                }
                                else if ((!pequals(this.enclosing.value, dparent)))
                                {
                                    {
                                        Dsymbol p = this.enclosing.value;
                                    L_outer28:
                                        for (; p != null;p = p.parent.value){
                                            if ((pequals(p, dparent)))
                                            {
                                                /*goto L1*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                    }
                                    {
                                        Dsymbol p = dparent;
                                    L_outer29:
                                        for (; p != null;p = p.parent.value){
                                            if ((pequals(p, this.enclosing.value)))
                                            {
                                                this.enclosing.value = dparent;
                                                /*goto L1*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                    }
                                    this.error(new BytePtr("`%s` is nested in both `%s` and `%s`"), this.toChars(), this.enclosing.value.toChars(), dparent.toChars());
                                    this.errors.value = true;
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*L1:*/
                            nested |= 1;
                        }
                    }
                    else if (va != null)
                    {
                        nested |= (this.hasNestedArgs(ptr(va.objects), isstatic) ? 1 : 0);
                    }
                }
            }
            return nested != 0;
        }

        public  Ptr<DArray<Dsymbol>> appendToModuleMember() {
            dmodule.Module mi = this.minst.value;
            if (global.params.useUnitTests || (global.params.debuglevel != 0))
            {
                if ((mi != null) && !mi.isRoot())
                {
                    mi = null;
                }
            }
            if ((mi == null) || mi.isRoot())
            {
                Function1<TemplateInstance,Dsymbol> getStrictEnclosing = new Function1<TemplateInstance,Dsymbol>(){
                    public Dsymbol invoke(TemplateInstance ti) {
                        Ref<TemplateInstance> ti_ref = ref(ti);
                        do {
                            {
                                if (ti_ref.value.enclosing.value != null)
                                {
                                    return ti_ref.value.enclosing.value;
                                }
                                ti_ref.value = ti_ref.value.tempdecl.value.isInstantiated();
                            }
                        } while (ti_ref.value != null);
                        return null;
                    }
                };
                Dsymbol enc = getStrictEnclosing.invoke(this);
                mi = (enc != null ? enc : this.tempdecl.value).getModule();
                if (!mi.isRoot())
                {
                    mi = mi.importedFrom;
                }
                assert(mi.isRoot());
            }
            else
            {
            }
            if ((this.memberOf == mi))
            {
                return null;
            }
            Ptr<DArray<Dsymbol>> a = mi.members.value;
            (a.get()).push(this);
            this.memberOf = mi;
            if ((mi.semanticRun.value >= PASS.semantic2done) && mi.isRoot())
            {
                dmodule.Module.addDeferredSemantic2(this);
            }
            if ((mi.semanticRun.value >= PASS.semantic3done) && mi.isRoot())
            {
                dmodule.Module.addDeferredSemantic3(this);
            }
            return a;
        }

        public  void declareParameters(Ptr<Scope> sc) {
            TemplateDeclaration tempdecl = this.tempdecl.value.isTemplateDeclaration();
            assert(tempdecl != null);
            {
                int i = 0;
                for (; (i < this.tdtypes.value.length.value);i++){
                    TemplateParameter tp = (tempdecl.parameters.get()).get(i);
                    RootObject o = this.tdtypes.value.get(i);
                    tempdecl.declareParameter(sc, tp, o);
                }
            }
        }

        public  Identifier genIdent(Ptr<DArray<RootObject>> args) {
            assert((args == this.tiargs.value));
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                mangleToBuffer(this, ptr(buf));
                return Identifier.idPool(buf.value.peekSlice());
            }
            finally {
            }
        }

        public  void expandMembers(Ptr<Scope> sc2) {
            Ref<Ptr<Scope>> sc2_ref = ref(sc2);
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.setScope(sc2_ref.value);
                    return null;
                }
            };
            foreachDsymbol(this.members.value, __lambda2);
            Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.importAll(sc2_ref.value);
                    return null;
                }
            };
            foreachDsymbol(this.members.value, __lambda3);
            Function1<Dsymbol,Void> symbolDg = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    Ref<Dsymbol> s_ref = ref(s);
                    dsymbolSemantic(s_ref.value, sc2_ref.value);
                    dmodule.Module.runDeferredSemantic();
                    return null;
                }
            };
            foreachDsymbol(this.members.value, symbolDg);
        }

        public  void tryExpandMembers(Ptr<Scope> sc2) {
            if (((dtemplate.tryExpandMembersnest += 1) > 500))
            {
                global.gag.value = 0;
                this.error(new BytePtr("recursive expansion"));
                fatal();
            }
            this.expandMembers(sc2);
            dtemplate.tryExpandMembersnest--;
        }

        public  void trySemantic3(Ptr<Scope> sc2) {
            if (((dtemplate.trySemantic3nest += 1) > 300))
            {
                global.gag.value = 0;
                this.error(new BytePtr("recursive expansion"));
                fatal();
            }
            semantic3(this, sc2);
            dtemplate.trySemantic3nest -= 1;
        }

        public  TemplateInstance isTemplateInstance() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateInstance() {}

        public TemplateInstance copy() {
            TemplateInstance that = new TemplateInstance();
            that.name = this.name;
            that.tiargs = this.tiargs;
            that.tdtypes = this.tdtypes;
            that.importedModules = this.importedModules;
            that.tempdecl = this.tempdecl;
            that.enclosing = this.enclosing;
            that.aliasdecl = this.aliasdecl;
            that.inst = this.inst;
            that.argsym = this.argsym;
            that.inuse = this.inuse;
            that.nest = this.nest;
            that.semantictiargsdone = this.semantictiargsdone;
            that.havetempdecl = this.havetempdecl;
            that.gagged = this.gagged;
            that.hash = this.hash;
            that.fargs = this.fargs;
            that.deferred = this.deferred;
            that.memberOf = this.memberOf;
            that.tinst = this.tinst;
            that.tnext = this.tnext;
            that.minst = this.minst;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
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
    public static void unSpeculative(Ptr<Scope> sc, RootObject o) {
        if (o == null)
        {
            return ;
        }
        {
            Tuple tup = isTuple(o);
            if ((tup) != null)
            {
                {
                    int i = 0;
                    for (; (i < tup.objects.value.length.value);i++){
                        unSpeculative(sc, tup.objects.value.get(i));
                    }
                }
                return ;
            }
        }
        Dsymbol s = getDsymbol(o);
        if (s == null)
        {
            return ;
        }
        {
            Declaration d = s.isDeclaration();
            if ((d) != null)
            {
                {
                    VarDeclaration vd = d.isVarDeclaration();
                    if ((vd) != null)
                    {
                        o = vd.type.value;
                    }
                    else {
                        AliasDeclaration ad = d.isAliasDeclaration();
                        if ((ad) != null)
                        {
                            o = ad.getType();
                            if (o == null)
                            {
                                o = ad.toAlias();
                            }
                        }
                        else
                        {
                            o = d.toAlias();
                        }
                    }
                }
                s = getDsymbol(o);
                if (s == null)
                {
                    return ;
                }
            }
        }
        {
            TemplateInstance ti = s.isTemplateInstance();
            if ((ti) != null)
            {
                if ((ti.minst.value != null) || ((sc.get()).minst.value == null))
                {
                    return ;
                }
                ti.minst.value = (sc.get()).minst.value;
                if (ti.tinst == null)
                {
                    ti.tinst = (sc.get()).tinst;
                }
                unSpeculative(sc, ti.tempdecl.value);
            }
        }
        {
            TemplateInstance ti = s.isInstantiated();
            if ((ti) != null)
            {
                unSpeculative(sc, ti);
            }
        }
    }

    public static boolean definitelyValueParameter(Expression e) {
        if (((e.op.value & 0xFF) == 126) || ((e.op.value & 0xFF) == 203) || ((e.op.value & 0xFF) == 20) || ((e.op.value & 0xFF) == 30) || ((e.op.value & 0xFF) == 36) || ((e.op.value & 0xFF) == 37) || ((e.op.value & 0xFF) == 161) || ((e.op.value & 0xFF) == 127) || ((e.op.value & 0xFF) == 123) || ((e.op.value & 0xFF) == 124))
        {
            return false;
        }
        if (((e.op.value & 0xFF) != 27))
        {
            return true;
        }
        FuncDeclaration f = ((DotVarExp)e).var.value.isFuncDeclaration();
        if (f != null)
        {
            return false;
        }
        for (; ((e.op.value & 0xFF) == 27);){
            e = ((DotVarExp)e).e1.value;
        }
        if (((e.op.value & 0xFF) == 123) || ((e.op.value & 0xFF) == 124))
        {
            return false;
        }
        if (((e.op.value & 0xFF) == 30))
        {
            return false;
        }
        if (((e.op.value & 0xFF) != 26))
        {
            return true;
        }
        VarDeclaration v = ((VarExp)e).var.value.isVarDeclaration();
        if (v == null)
        {
            return true;
        }
        if ((v.storage_class.value & 8388608L) != 0)
        {
            return true;
        }
        return false;
    }

    public static class TemplateMixin extends TemplateInstance
    {
        public TypeQualified tqual = null;
        public  TemplateMixin(Loc loc, Identifier ident, TypeQualified tqual, Ptr<DArray<RootObject>> tiargs) {
            super(loc, tqual.idents.length.value != 0 ? (Identifier)tqual.idents.get(tqual.idents.length.value - 1) : ((TypeIdentifier)tqual).ident.value, tiargs != null ? tiargs : refPtr(new DArray<RootObject>()));
            this.ident.value = ident;
            this.tqual = tqual;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            TemplateMixin tm = new TemplateMixin(this.loc.value, this.ident.value, (TypeQualified)this.tqual.syntaxCopy(), this.tiargs.value);
            return this.syntaxCopy(tm);
        }

        public  BytePtr kind() {
            return new BytePtr("mixin");
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            return this.oneMember(ps, ident);
        }

        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            if (this._scope.value != null)
            {
                dsymbolSemantic(this, null);
            }
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    return (((s != null) && (s.apply(fp, param) != 0)) ? 1 : 0);
                }
            };
            return foreachDsymbol(this.members.value, __lambda3);
        }

        public  boolean hasPointers() {
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    return (s.hasPointers() ? 1 : 0);
                }
            };
            return foreachDsymbol(this.members.value, __lambda1) != 0;
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            if (this._scope.value != null)
            {
                dsymbolSemantic(this, null);
            }
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.setFieldOffset(ad, poffset, isunion);
                    return null;
                }
            };
            foreachDsymbol(this.members.value, __lambda4);
        }

        public  BytePtr toChars() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                toCBufferInstance(this, ptr(buf), false);
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  boolean findTempDecl(Ptr<Scope> sc) {
            if (this.tempdecl.value == null)
            {
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(this.tqual, this.loc.value, sc, ptr(e), ptr(t), ptr(s), false);
                if (s.value == null)
                {
                    this.error(new BytePtr("is not defined"));
                    return false;
                }
                s.value = s.value.toAlias();
                this.tempdecl.value = s.value.isTemplateDeclaration();
                OverloadSet os = s.value.isOverloadSet();
                if (os != null)
                {
                    Dsymbol ds = null;
                    {
                        int i = 0;
                        for (; (i < os.a.length.value);i++){
                            Dsymbol s2 = os.a.get(i).isTemplateDeclaration();
                            if (s2 != null)
                            {
                                if (ds != null)
                                {
                                    this.tempdecl.value = os;
                                    break;
                                }
                                ds = s2;
                            }
                        }
                    }
                }
                if (this.tempdecl.value == null)
                {
                    this.error(new BytePtr("`%s` isn't a template"), s.value.toChars());
                    return false;
                }
            }
            assert(this.tempdecl.value != null);
            OverloadSet tovers = this.tempdecl.value.isOverloadSet();
            {
                int __key1256 = 0;
                int __limit1257 = tovers != null ? tovers.a.length.value : 1;
                for (; (__key1256 < __limit1257);__key1256 += 1) {
                    int oi = __key1256;
                    Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl.value;
                    Function1<Dsymbol,Integer> __lambda2 = new Function1<Dsymbol,Integer>(){
                        public Integer invoke(Dsymbol s) {
                            TemplateDeclaration td = s.isTemplateDeclaration();
                            if (td == null)
                            {
                                return 0;
                            }
                            if ((td.semanticRun.value == PASS.init))
                            {
                                if (td._scope.value != null)
                                {
                                    dsymbolSemantic(td, td._scope.value);
                                }
                                else
                                {
                                    semanticRun.value = PASS.init;
                                    return 1;
                                }
                            }
                            return 0;
                        }
                    };
                    int r = overloadApply(dstart, __lambda2, null);
                    if (r != 0)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public  TemplateMixin isTemplateMixin() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateMixin() {}

        public TemplateMixin copy() {
            TemplateMixin that = new TemplateMixin();
            that.tqual = this.tqual;
            that.name = this.name;
            that.tiargs = this.tiargs;
            that.tdtypes = this.tdtypes;
            that.importedModules = this.importedModules;
            that.tempdecl = this.tempdecl;
            that.enclosing = this.enclosing;
            that.aliasdecl = this.aliasdecl;
            that.inst = this.inst;
            that.argsym = this.argsym;
            that.inuse = this.inuse;
            that.nest = this.nest;
            that.semantictiargsdone = this.semantictiargsdone;
            that.havetempdecl = this.havetempdecl;
            that.gagged = this.gagged;
            that.hash = this.hash;
            that.fargs = this.fargs;
            that.deferred = this.deferred;
            that.memberOf = this.memberOf;
            that.tinst = this.tinst;
            that.tnext = this.tnext;
            that.minst = this.minst;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
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
    public static class TemplateInstanceBox
    {
        public TemplateInstance ti = null;
        public  TemplateInstanceBox(TemplateInstance ti) {
            this.ti = ti;
            this.ti.toHash();
            assert(this.ti.hash != 0);
        }

        public  int toHash() {
            assert(this.ti.hash != 0);
            return this.ti.hash;
        }

        public  boolean opEquals(TemplateInstanceBox s) {
            boolean res = null;
            if ((this.ti.inst.value != null) && (s.ti.inst.value != null))
            {
                res = this.ti == s.ti;
            }
            else
            {
                res = s.ti.equalsx(this.ti);
            }
            return res;
        }

        public TemplateInstanceBox(){
        }
        public TemplateInstanceBox copy(){
            TemplateInstanceBox r = new TemplateInstanceBox();
            r.ti = ti;
            return r;
        }
        public TemplateInstanceBox opAssign(TemplateInstanceBox that) {
            this.ti = that.ti;
            return this;
        }
    }
}
