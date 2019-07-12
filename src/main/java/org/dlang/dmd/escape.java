package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.printast.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class escape {
    private static class EscapeVisitor extends Visitor
    {
        private Ptr<EscapeByResults> er = null;
        public  EscapeVisitor(Ptr<EscapeByResults> er) {
            Ref<Ptr<EscapeByResults>> er_ref = ref(er);
            this.er = er_ref.value;
        }

        public  void visit(Expression e) {
        }

        public  void visit(AddrExp e) {
            Ref<AddrExp> e_ref = ref(e);
            if (((e_ref.value.e1.op & 0xFF) != 49))
                escapeByRef(e_ref.value.e1, this.er);
        }

        public  void visit(SymOffExp e) {
            Ref<SymOffExp> e_ref = ref(e);
            Ref<VarDeclaration> v = ref(e_ref.value.var.isVarDeclaration());
            if (v.value != null)
                (this.er.get()).byref.push(v.value);
        }

        public  void visit(VarExp e) {
            Ref<VarExp> e_ref = ref(e);
            Ref<VarDeclaration> v = ref(e_ref.value.var.isVarDeclaration());
            if (v.value != null)
                (this.er.get()).byvalue.push(v.value);
        }

        public  void visit(ThisExp e) {
            Ref<ThisExp> e_ref = ref(e);
            if (e_ref.value.var != null)
                (this.er.get()).byvalue.push(e_ref.value.var);
        }

        public  void visit(DotVarExp e) {
            Ref<DotVarExp> e_ref = ref(e);
            Ref<Type> t = ref(e_ref.value.e1.type.value.toBasetype());
            if (((t.value.ty & 0xFF) == ENUMTY.Tstruct))
                e_ref.value.e1.accept(this);
        }

        public  void visit(DelegateExp e) {
            Ref<DelegateExp> e_ref = ref(e);
            Ref<Type> t = ref(e_ref.value.e1.type.value.toBasetype());
            if (((t.value.ty & 0xFF) == ENUMTY.Tclass) || ((t.value.ty & 0xFF) == ENUMTY.Tpointer))
                escapeByValue(e_ref.value.e1, this.er);
            else
                escapeByRef(e_ref.value.e1, this.er);
            (this.er.get()).byfunc.push(e_ref.value.func);
        }

        public  void visit(FuncExp e) {
            Ref<FuncExp> e_ref = ref(e);
            if (((e_ref.value.fd.tok & 0xFF) == 160))
                (this.er.get()).byfunc.push(e_ref.value.fd);
        }

        public  void visit(TupleExp e) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            Ref<Type> tb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray) || ((tb.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                if (e_ref.value.basis != null)
                    e_ref.value.basis.accept(this);
                {
                    Ref<Slice<Expression>> __r1300 = ref((e_ref.value.elements.get()).opSlice().copy());
                    IntRef __key1301 = ref(0);
                    for (; (__key1301.value < __r1300.value.getLength());__key1301.value += 1) {
                        Ref<Expression> el = ref(__r1300.value.get(__key1301.value));
                        if (el.value != null)
                            el.value.accept(this);
                    }
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            if (e_ref.value.elements != null)
            {
                {
                    Ref<Slice<Expression>> __r1302 = ref((e_ref.value.elements.get()).opSlice().copy());
                    IntRef __key1303 = ref(0);
                    for (; (__key1303.value < __r1302.value.getLength());__key1303.value += 1) {
                        Ref<Expression> ex = ref(__r1302.value.get(__key1303.value));
                        if (ex.value != null)
                            ex.value.accept(this);
                    }
                }
            }
        }

        public  void visit(NewExp e) {
            Ref<NewExp> e_ref = ref(e);
            Ref<Type> tb = ref(e_ref.value.newtype.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tstruct) && (e_ref.value.member == null) && (e_ref.value.arguments != null))
            {
                {
                    Ref<Slice<Expression>> __r1304 = ref((e_ref.value.arguments.get()).opSlice().copy());
                    IntRef __key1305 = ref(0);
                    for (; (__key1305.value < __r1304.value.getLength());__key1305.value += 1) {
                        Ref<Expression> ex = ref(__r1304.value.get(__key1305.value));
                        if (ex.value != null)
                            ex.value.accept(this);
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            Ref<CastExp> e_ref = ref(e);
            Ref<Type> tb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) && ((e_ref.value.e1.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                escapeByRef(e_ref.value.e1, this.er);
            }
            else
                e_ref.value.e1.accept(this);
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            if (((e_ref.value.e1.op & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e_ref.value.e1).var.isVarDeclaration());
                Ref<Type> tb = ref(e_ref.value.type.value.toBasetype());
                if (v.value != null)
                {
                    if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                        return ;
                    if ((v.value.storage_class & 65536L) != 0)
                    {
                        (this.er.get()).byvalue.push(v.value);
                        return ;
                    }
                }
            }
            Ref<Type> t1b = ref(e_ref.value.e1.type.value.toBasetype());
            if (((t1b.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Ref<Type> tb = ref(e_ref.value.type.value.toBasetype());
                if (((tb.value.ty & 0xFF) != ENUMTY.Tsarray))
                    escapeByRef(e_ref.value.e1, this.er);
            }
            else
                e_ref.value.e1.accept(this);
        }

        public  void visit(IndexExp e) {
            Ref<IndexExp> e_ref = ref(e);
            if (((e_ref.value.e1.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                e_ref.value.e1.value.accept(this);
            }
        }

        public  void visit(BinExp e) {
            Ref<BinExp> e_ref = ref(e);
            Ref<Type> tb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tpointer))
            {
                e_ref.value.e1.value.accept(this);
                e_ref.value.e2.value.accept(this);
            }
        }

        public  void visit(BinAssignExp e) {
            Ref<BinAssignExp> e_ref = ref(e);
            e_ref.value.e1.value.accept(this);
        }

        public  void visit(AssignExp e) {
            Ref<AssignExp> e_ref = ref(e);
            e_ref.value.e1.value.accept(this);
        }

        public  void visit(CommaExp e) {
            Ref<CommaExp> e_ref = ref(e);
            e_ref.value.e2.value.accept(this);
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            e_ref.value.e1.value.accept(this);
            e_ref.value.e2.value.accept(this);
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            Ref<Type> t1 = ref(e_ref.value.e1.type.value.toBasetype());
            Ref<TypeFunction> tf = ref(null);
            Ref<TypeDelegate> dg = ref(null);
            if (((t1.value.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                dg.value = (TypeDelegate)t1.value;
                tf.value = (TypeFunction)((TypeDelegate)t1.value).next;
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tfunction))
                tf.value = (TypeFunction)t1.value;
            else
                return ;
            if ((e_ref.value.arguments != null) && ((e_ref.value.arguments.get()).length != 0))
            {
                IntRef j = ref((((tf.value.linkage == LINK.d) && (tf.value.parameterList.varargs == VarArg.variadic)) ? 1 : 0));
                {
                    IntRef i = ref(j.value);
                    for (; (i.value < (e_ref.value.arguments.get()).length);i.value += 1){
                        Ref<Expression> arg = ref((e_ref.value.arguments.get()).get(i.value));
                        IntRef nparams = ref(tf.value.parameterList.length());
                        if ((i.value - j.value < nparams.value) && (i.value >= j.value))
                        {
                            Ref<Parameter> p = ref(tf.value.parameterList.get(i.value - j.value));
                            Ref<Long> stc = ref(tf.value.parameterStorageClass(null, p.value));
                            if (((stc.value & 524288L) != 0) && ((stc.value & 17592186044416L) != 0))
                                arg.value.accept(this);
                            else if (((stc.value & 2097152L) != 0) && ((stc.value & 17592186044416L) != 0))
                                escapeByRef(arg.value, this.er);
                        }
                    }
                }
            }
            if (((e_ref.value.e1.op & 0xFF) == 27) && ((t1.value.ty & 0xFF) == ENUMTY.Tfunction))
            {
                Ref<DotVarExp> dve = ref((DotVarExp)e_ref.value.e1);
                Ref<FuncDeclaration> fd = ref(dve.value.var.isFuncDeclaration());
                Ref<AggregateDeclaration> ad = ref(null);
                if (global.value.params.vsafe && tf.value.isreturn && (fd.value != null) && ((ad.value = fd.value.isThis()) != null))
                {
                    if ((ad.value.isClassDeclaration() != null) || tf.value.isscope)
                        dve.value.e1.accept(this);
                    else if (ad.value.isStructDeclaration() != null)
                        escapeByRef(dve.value.e1, this.er);
                }
                else if (((dve.value.var.storage_class & 17592186044416L) != 0) || tf.value.isreturn)
                {
                    if ((dve.value.var.storage_class & 524288L) != 0)
                        dve.value.e1.accept(this);
                    else if ((dve.value.var.storage_class & 2097152L) != 0)
                        escapeByRef(dve.value.e1, this.er);
                }
                if ((fd.value != null) && fd.value.isNested())
                {
                    if (tf.value.isreturn && tf.value.isscope)
                        (this.er.get()).byexp.push(e_ref.value);
                }
            }
            if (dg.value != null)
            {
                if (tf.value.isreturn)
                    e_ref.value.e1.accept(this);
            }
            if (((e_ref.value.e1.op & 0xFF) == 26))
            {
                Ref<VarExp> ve = ref((VarExp)e_ref.value.e1);
                Ref<FuncDeclaration> fd = ref(ve.value.var.isFuncDeclaration());
                if ((fd.value != null) && fd.value.isNested())
                {
                    if (tf.value.isreturn && tf.value.isscope)
                        (this.er.get()).byexp.push(e_ref.value);
                }
            }
        }


        public EscapeVisitor() {}
    }
    private static class EscapeRefVisitor extends Visitor
    {
        private Ptr<EscapeByResults> er = null;
        public  EscapeRefVisitor(Ptr<EscapeByResults> er) {
            Ref<Ptr<EscapeByResults>> er_ref = ref(er);
            this.er = er_ref.value;
        }

        public  void visit(Expression e) {
        }

        public  void visit(VarExp e) {
            Ref<VarExp> e_ref = ref(e);
            Ref<VarDeclaration> v = ref(e_ref.value.var.isVarDeclaration());
            if (v.value != null)
            {
                if (((v.value.storage_class & 2097152L) != 0) && ((v.value.storage_class & 1099511644160L) != 0) && (v.value._init != null))
                {
                    {
                        Ref<ExpInitializer> ez = ref(v.value._init.isExpInitializer());
                        if ((ez.value) != null)
                        {
                            assert((ez.value.exp != null) && ((ez.value.exp.op & 0xFF) == 95));
                            Ref<Expression> ex = ref(((ConstructExp)ez.value.exp).e2.value);
                            ex.value.accept(this);
                        }
                    }
                }
                else
                    (this.er.get()).byref.push(v.value);
            }
        }

        public  void visit(ThisExp e) {
            Ref<ThisExp> e_ref = ref(e);
            if ((e_ref.value.var != null) && e_ref.value.var.toParent2().isFuncDeclaration().isThis2)
                escapeByValue(e_ref.value, this.er);
            else if (e_ref.value.var != null)
                (this.er.get()).byref.push(e_ref.value.var);
        }

        public  void visit(PtrExp e) {
            Ref<PtrExp> e_ref = ref(e);
            escapeByValue(e_ref.value.e1, this.er);
        }

        public  void visit(IndexExp e) {
            Ref<IndexExp> e_ref = ref(e);
            Ref<Type> tb = ref(e_ref.value.e1.value.type.value.toBasetype());
            if (((e_ref.value.e1.value.op & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e_ref.value.e1.value).var.isVarDeclaration());
                if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) || ((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    if ((v.value != null) && ((v.value.storage_class & 65536L) != 0))
                    {
                        (this.er.get()).byref.push(v.value);
                        return ;
                    }
                }
            }
            if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                e_ref.value.e1.value.accept(this);
            }
            else if (((tb.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                escapeByValue(e_ref.value.e1.value, this.er);
            }
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            if (e_ref.value.elements != null)
            {
                {
                    Ref<Slice<Expression>> __r1306 = ref((e_ref.value.elements.get()).opSlice().copy());
                    IntRef __key1307 = ref(0);
                    for (; (__key1307.value < __r1306.value.getLength());__key1307.value += 1) {
                        Ref<Expression> ex = ref(__r1306.value.get(__key1307.value));
                        if (ex.value != null)
                            ex.value.accept(this);
                    }
                }
            }
            (this.er.get()).byexp.push(e_ref.value);
        }

        public  void visit(DotVarExp e) {
            Ref<DotVarExp> e_ref = ref(e);
            Ref<Type> t1b = ref(e_ref.value.e1.type.value.toBasetype());
            if (((t1b.value.ty & 0xFF) == ENUMTY.Tclass))
                escapeByValue(e_ref.value.e1, this.er);
            else
                e_ref.value.e1.accept(this);
        }

        public  void visit(BinAssignExp e) {
            Ref<BinAssignExp> e_ref = ref(e);
            e_ref.value.e1.value.accept(this);
        }

        public  void visit(AssignExp e) {
            Ref<AssignExp> e_ref = ref(e);
            e_ref.value.e1.value.accept(this);
        }

        public  void visit(CommaExp e) {
            Ref<CommaExp> e_ref = ref(e);
            e_ref.value.e2.value.accept(this);
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            e_ref.value.e1.value.accept(this);
            e_ref.value.e2.value.accept(this);
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            Ref<Type> t1 = ref(e_ref.value.e1.type.value.toBasetype());
            Ref<TypeFunction> tf = ref(null);
            if (((t1.value.ty & 0xFF) == ENUMTY.Tdelegate))
                tf.value = (TypeFunction)((TypeDelegate)t1.value).next;
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tfunction))
                tf.value = (TypeFunction)t1.value;
            else
                return ;
            if (tf.value.isref)
            {
                if ((e_ref.value.arguments != null) && ((e_ref.value.arguments.get()).length != 0))
                {
                    IntRef j = ref((((tf.value.linkage == LINK.d) && (tf.value.parameterList.varargs == VarArg.variadic)) ? 1 : 0));
                    {
                        IntRef i = ref(j.value);
                        for (; (i.value < (e_ref.value.arguments.get()).length);i.value += 1){
                            Ref<Expression> arg = ref((e_ref.value.arguments.get()).get(i.value));
                            IntRef nparams = ref(tf.value.parameterList.length());
                            if ((i.value - j.value < nparams.value) && (i.value >= j.value))
                            {
                                Ref<Parameter> p = ref(tf.value.parameterList.get(i.value - j.value));
                                Ref<Long> stc = ref(tf.value.parameterStorageClass(null, p.value));
                                if (((stc.value & 2101248L) != 0) && ((stc.value & 17592186044416L) != 0))
                                    arg.value.accept(this);
                                else if (((stc.value & 524288L) != 0) && ((stc.value & 17592186044416L) != 0))
                                {
                                    if (((arg.value.op & 0xFF) == 160))
                                    {
                                        Ref<DelegateExp> de = ref((DelegateExp)arg.value);
                                        if (de.value.func.isNested())
                                            (this.er.get()).byexp.push(de.value);
                                    }
                                    else
                                        escapeByValue(arg.value, this.er);
                                }
                            }
                        }
                    }
                }
                if (((e_ref.value.e1.op & 0xFF) == 27) && ((t1.value.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    Ref<DotVarExp> dve = ref((DotVarExp)e_ref.value.e1);
                    if (((dve.value.var.storage_class & 17592186044416L) != 0) || tf.value.isreturn)
                    {
                        if (((dve.value.var.storage_class & 524288L) != 0) || tf.value.isscope)
                            escapeByValue(dve.value.e1, this.er);
                        else if (((dve.value.var.storage_class & 2097152L) != 0) || tf.value.isref)
                            dve.value.e1.accept(this);
                    }
                    Ref<FuncDeclaration> fd = ref(dve.value.var.isFuncDeclaration());
                    if ((fd.value != null) && fd.value.isNested())
                    {
                        if (tf.value.isreturn)
                            (this.er.get()).byexp.push(e_ref.value);
                    }
                }
                if (((e_ref.value.e1.op & 0xFF) == 26) && ((t1.value.ty & 0xFF) == ENUMTY.Tdelegate))
                {
                    escapeByValue(e_ref.value.e1, this.er);
                }
                if (((e_ref.value.e1.op & 0xFF) == 26))
                {
                    Ref<VarExp> ve = ref((VarExp)e_ref.value.e1);
                    Ref<FuncDeclaration> fd = ref(ve.value.var.isFuncDeclaration());
                    if ((fd.value != null) && fd.value.isNested())
                    {
                        if (tf.value.isreturn)
                            (this.er.get()).byexp.push(e_ref.value);
                    }
                }
            }
            else
                (this.er.get()).byexp.push(e_ref.value);
        }


        public EscapeRefVisitor() {}
    }

    public static boolean checkArrayLiteralEscape(Ptr<Scope> sc, ArrayLiteralExp ae, boolean gag) {
        boolean errors = false;
        if (ae.basis != null)
            errors = checkNewEscape(sc, ae.basis, gag);
        {
            Slice<Expression> __r1256 = (ae.elements.get()).opSlice().copy();
            int __key1257 = 0;
            for (; (__key1257 < __r1256.getLength());__key1257 += 1) {
                Expression ex = __r1256.get(__key1257);
                if (ex != null)
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
            }
        }
        return errors;
    }

    public static boolean checkAssocArrayLiteralEscape(Ptr<Scope> sc, AssocArrayLiteralExp ae, boolean gag) {
        boolean errors = false;
        {
            Slice<Expression> __r1258 = (ae.keys.get()).opSlice().copy();
            int __key1259 = 0;
            for (; (__key1259 < __r1258.getLength());__key1259 += 1) {
                Expression ex = __r1258.get(__key1259);
                if (ex != null)
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
            }
        }
        {
            Slice<Expression> __r1260 = (ae.values.get()).opSlice().copy();
            int __key1261 = 0;
            for (; (__key1261 < __r1260.getLength());__key1261 += 1) {
                Expression ex = __r1260.get(__key1261);
                if (ex != null)
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
            }
        }
        return errors;
    }

    public static boolean checkParamArgumentEscape(Ptr<Scope> sc, FuncDeclaration fdc, Parameter par, Expression arg, boolean gag) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<FuncDeclaration> fdc_ref = ref(fdc);
        Ref<Parameter> par_ref = ref(par);
        Ref<Expression> arg_ref = ref(arg);
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
            printf(new BytePtr("checkParamArgumentEscape(arg: %s par: %s)\n"), arg_ref.value != null ? arg_ref.value.toChars() : new BytePtr("null"), par_ref.value != null ? par_ref.value.toChars() : new BytePtr("this"));
        if (!arg_ref.value.type.value.hasPointers())
            return false;
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(arg_ref.value, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byfunc.length == 0) && (er.value.byexp.length == 0))
                return false;
            Ref<Boolean> result = ref(false);
            Function2<VarDeclaration,BytePtr,Void> unsafeAssign = new Function2<VarDeclaration,BytePtr,Void>(){
                public Void invoke(VarDeclaration v, BytePtr desc) {
                    Ref<VarDeclaration> v_ref = ref(v);
                    if (global.value.params.vsafe && (sc_ref.value.get()).func.setUnsafe())
                    {
                        if (!gag_ref.value)
                            error(arg_ref.value.loc, new BytePtr("%s `%s` assigned to non-scope parameter `%s` calling %s"), desc, v_ref.value.toChars(), par_ref.value != null ? par_ref.value.toChars() : new BytePtr("this"), fdc_ref.value != null ? fdc_ref.value.toPrettyChars(false) : new BytePtr("indirectly"));
                        result.value = true;
                    }
                }
            };
            {
                Slice<VarDeclaration> __r1262 = er.value.byvalue.opSlice().copy();
                int __key1263 = 0;
                for (; (__key1263 < __r1262.getLength());__key1263 += 1) {
                    VarDeclaration v = __r1262.get(__key1263);
                    if (false)
                        printf(new BytePtr("byvalue %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    notMaybeScope(v);
                    if (v.isScope())
                    {
                        unsafeAssign.invoke(v, new BytePtr("scope variable"));
                    }
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc_ref.value.get()).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            unsafeAssign.invoke(v, new BytePtr("variadic variable"));
                        }
                    }
                    else
                    {
                        if (false)
                            printf(new BytePtr("no infer for %s in %s loc %s, fdc %s, %d\n"), v.toChars(), (sc_ref.value.get()).func.ident.toChars(), (sc_ref.value.get()).func.loc.toChars(global.value.params.showColumns), fdc_ref.value.ident.toChars(), 162);
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1264 = er.value.byref.opSlice().copy();
                int __key1265 = 0;
                for (; (__key1265 < __r1264.getLength());__key1265 += 1) {
                    VarDeclaration v = __r1264.get(__key1265);
                    if (false)
                        printf(new BytePtr("byref %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    notMaybeScope(v);
                    if (((v.storage_class & 2101248L) == 0L) && (pequals(p, (sc_ref.value.get()).func)))
                    {
                        if ((par_ref.value != null) && ((par_ref.value.storageClass & 17592186568704L) == 524288L))
                            continue;
                        unsafeAssign.invoke(v, new BytePtr("reference to local variable"));
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1266 = er.value.byfunc.opSlice().copy();
                int __key1267 = 0;
                for (; (__key1267 < __r1266.getLength());__key1267 += 1) {
                    FuncDeclaration fd = __r1266.get(__key1267);
                    Ref<DArray<VarDeclaration>> vars = ref(new DArray<VarDeclaration>());
                    try {
                        findAllOuterAccessedVariables(fd, ptr(vars));
                        {
                            Slice<VarDeclaration> __r1268 = vars.value.opSlice().copy();
                            int __key1269 = 0;
                            for (; (__key1269 < __r1268.getLength());__key1269 += 1) {
                                VarDeclaration v = __r1268.get(__key1269);
                                assert(!v.isDataseg());
                                Dsymbol p = v.toParent2();
                                notMaybeScope(v);
                                if (((v.storage_class & 2625536L) != 0) && (pequals(p, (sc_ref.value.get()).func)))
                                {
                                    unsafeAssign.invoke(v, new BytePtr("reference to local"));
                                    continue;
                                }
                            }
                        }
                    }
                    finally {
                    }
                }
            }
            {
                Slice<Expression> __r1270 = er.value.byexp.opSlice().copy();
                int __key1271 = 0;
                for (; (__key1271 < __r1270.getLength());__key1271 += 1) {
                    Expression ee = __r1270.get(__key1271);
                    if ((sc_ref.value.get()).func.setUnsafe())
                    {
                        if (!gag_ref.value)
                            error(ee.loc, new BytePtr("reference to stack allocated value returned by `%s` assigned to non-scope parameter `%s`"), ee.toChars(), par_ref.value != null ? par_ref.value.toChars() : new BytePtr("this"));
                        result.value = true;
                    }
                }
            }
            return result.value;
        }
        finally {
        }
    }

    public static boolean checkParamArgumentReturn(Ptr<Scope> sc, Expression firstArg, Expression arg, boolean gag) {
        boolean log = false;
        if (false)
            printf(new BytePtr("checkParamArgumentReturn(firstArg: %s arg: %s)\n"), firstArg.toChars(), arg.toChars());
        if (!arg.type.value.hasPointers())
            return false;
        AssignExp e = new AssignExp(arg.loc, firstArg, arg);
        return checkAssignEscape(sc, e, gag);
    }

    public static boolean checkConstructorEscape(Ptr<Scope> sc, CallExp ce, boolean gag) {
        boolean log = false;
        if (false)
            printf(new BytePtr("checkConstructorEscape(%s, %s)\n"), ce.toChars(), ce.type.value.toChars());
        Type tthis = ce.type.value.toBasetype();
        assert(((tthis.ty & 0xFF) == ENUMTY.Tstruct));
        if (!tthis.hasPointers())
            return false;
        if ((ce.arguments == null) && ((ce.arguments.get()).length != 0))
            return false;
        assert(((ce.e1.op & 0xFF) == 27));
        DotVarExp dve = (DotVarExp)ce.e1;
        CtorDeclaration ctor = dve.var.isCtorDeclaration();
        assert(ctor != null);
        assert(((ctor.type.ty & 0xFF) == ENUMTY.Tfunction));
        TypeFunction tf = (TypeFunction)ctor.type;
        int nparams = tf.parameterList.length();
        int n = (ce.arguments.get()).length;
        boolean j = (tf.linkage == LINK.d) && (tf.parameterList.varargs == VarArg.variadic);
        {
            int __key1272 = 0;
            int __limit1273 = n;
            for (; (__key1272 < __limit1273);__key1272 += 1) {
                int i = __key1272;
                Expression arg = (ce.arguments.get()).get(i);
                if (!arg.type.value.hasPointers())
                    return false;
                if ((i - (j ? 1 : 0) < nparams) && (i >= (j ? 1 : 0)))
                {
                    Parameter p = tf.parameterList.get(i - (j ? 1 : 0));
                    if ((p.storageClass & 17592186044416L) != 0)
                    {
                        AssignExp e = new AssignExp(arg.loc, dve.e1, arg);
                        if (checkAssignEscape(sc, e, gag))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkAssignEscape(Ptr<Scope> sc, Expression e, boolean gag) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        boolean log = false;
        if (false)
            printf(new BytePtr("checkAssignEscape(e: %s)\n"), e.toChars());
        if (((e.op & 0xFF) != 90) && ((e.op & 0xFF) != 96) && ((e.op & 0xFF) != 95) && ((e.op & 0xFF) != 71) && ((e.op & 0xFF) != 72) && ((e.op & 0xFF) != 73))
            return false;
        BinExp ae = (BinExp)e;
        Expression e1 = ae.e1.value;
        Expression e2 = ae.e2.value;
        if (!e1.type.value.hasPointers())
            return false;
        if (((e1.op & 0xFF) == 31))
            return false;
        if (((e1.op & 0xFF) == 49))
            return false;
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(e2, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byfunc.length == 0) && (er.value.byexp.length == 0))
                return false;
            Ref<VarDeclaration> va = ref(expToVariable(e1));
            if ((va.value != null) && ((e.op & 0xFF) == 72))
            {
                va.value = null;
            }
            if ((va.value != null) && ((e1.op & 0xFF) == 27) && ((va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass))
            {
                va.value = null;
            }
            if (false)
                printf(new BytePtr("va: %s\n"), va.value.toChars());
            boolean inferScope = false;
            if ((va.value != null) && ((sc_ref.value.get()).func != null) && ((sc_ref.value.get()).func.type != null) && (((sc_ref.value.get()).func.type.ty & 0xFF) == ENUMTY.Tfunction))
                inferScope = ((TypeFunction)(sc_ref.value.get()).func.type).trust != TRUST.system;
            boolean vaIsRef = (va.value != null) && ((va.value.storage_class & 32L) != 0) && ((va.value.storage_class & 2101248L) != 0) || ((va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass);
            if (false)
                printf(new BytePtr("va is ref `%s`\n"), va.value.toChars());
            Function0<Boolean> isFirstRef = new Function0<Boolean>(){
                public Boolean invoke() {
                    if (!vaIsRef)
                        return false;
                    Ref<Dsymbol> p = ref(va.value.toParent2());
                    Ref<FuncDeclaration> fd = ref((sc_ref.value.get()).func);
                    if ((pequals(p.value, fd.value)) && (fd.value.type != null) && ((fd.value.type.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        Ref<TypeFunction> tf = ref((TypeFunction)fd.value.type);
                        if ((tf.value.nextOf() == null) || ((tf.value.nextOf().ty & 0xFF) != ENUMTY.Tvoid) && (fd.value.isCtorDeclaration() == null))
                            return false;
                        if ((pequals(va.value, fd.value.vthis)))
                            return true;
                        if ((fd.value.parameters != null) && ((fd.value.parameters.get()).length != 0) && (pequals((fd.value.parameters.get()).get(0), va.value)))
                            return true;
                    }
                    return false;
                }
            };
            boolean vaIsFirstRef = isFirstRef.invoke();
            if (false)
                printf(new BytePtr("va is first ref `%s`\n"), va.value.toChars());
            boolean result = false;
            {
                Slice<VarDeclaration> __r1274 = er.value.byvalue.opSlice().copy();
                int __key1275 = 0;
                for (; (__key1275 < __r1274.getLength());__key1275 += 1) {
                    VarDeclaration v = __r1274.get(__key1275);
                    if (false)
                        printf(new BytePtr("byvalue: %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    if ((pequals(v, va.value)))
                        continue;
                    Dsymbol p = v.toParent2();
                    if ((va.value != null) && !vaIsRef && !va.value.isScope() && !v.isScope() && ((va.value.storage_class & v.storage_class & 281474976776192L) == 281474976710656L) && (pequals(p, (sc_ref.value.get()).func)))
                    {
                        va.value.addMaybe(v);
                        continue;
                    }
                    if (vaIsFirstRef && v.isScope() || ((v.storage_class & 281474976710656L) != 0) && ((v.storage_class & 17592186044416L) == 0) && v.isParameter() && (((sc_ref.value.get()).func.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc_ref.value.get()).func)))
                    {
                        if (false)
                            printf(new BytePtr("inferring 'return' for parameter %s in function %s\n"), v.toChars(), (sc_ref.value.get()).func.toChars());
                        inferReturn((sc_ref.value.get()).func, v);
                    }
                    if (!((va.value != null) && va.value.isScope()) || vaIsRef)
                        notMaybeScope(v);
                    if (v.isScope())
                    {
                        if (vaIsFirstRef && v.isParameter() && ((v.storage_class & 17592186044416L) != 0))
                        {
                            if (va.value.isScope())
                                continue;
                            if (inferScope && !va.value.doNotInferScope)
                            {
                                if (false)
                                    printf(new BytePtr("inferring scope for lvalue %s\n"), va.value.toChars());
                                va.value.storage_class |= 562949953945600L;
                                continue;
                            }
                        }
                        if ((va.value != null) && va.value.isScope() && ((va.value.storage_class & 17592186044416L) != 0) && ((v.storage_class & 17592186044416L) == 0) && (sc_ref.value.get()).func.setUnsafe())
                        {
                            if (!gag)
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to return scope `%s`"), v.toChars(), va.value.toChars());
                            result = true;
                            continue;
                        }
                        if ((va.value != null) && va.value.enclosesLifetimeOf(v) && ((v.storage_class & 1099511627808L) == 0) || ((ae.e1.value.op & 0xFF) == 27) && ((va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass) && va.value.enclosesLifetimeOf(v) || !va.value.isScope() || vaIsRef || ((va.value.storage_class & 2101248L) != 0) && ((v.storage_class & 1099511627808L) == 0) && (sc_ref.value.get()).func.setUnsafe())
                        {
                            if (!gag)
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                            result = true;
                            continue;
                        }
                        if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                        {
                            if (!va.value.isScope() && inferScope)
                            {
                                va.value.storage_class |= 562949953945600L;
                                if (((v.storage_class & 17592186044416L) != 0) && ((va.value.storage_class & 17592186044416L) == 0))
                                {
                                    va.value.storage_class |= 4521191813414912L;
                                }
                            }
                            continue;
                        }
                        if ((sc_ref.value.get()).func.setUnsafe())
                        {
                            if (!gag)
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            result = true;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc_ref.value.get()).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                            {
                                if (!va.value.isScope() && inferScope)
                                {
                                    va.value.storage_class |= 562949953945600L;
                                }
                                continue;
                            }
                            if ((sc_ref.value.get()).func.setUnsafe())
                            {
                                if (!gag)
                                    error(ae.loc, new BytePtr("variadic variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                                result = true;
                            }
                        }
                    }
                    else
                    {
                        v.doNotInferScope = true;
                    }
                }
            }
        /*ByRef:*/
            {
                Slice<VarDeclaration> __r1276 = er.value.byref.opSlice().copy();
                int __key1277 = 0;
                for (; (__key1277 < __r1276.getLength());__key1277 += 1) {
                    VarDeclaration v = __r1276.get(__key1277);
                    if (false)
                        printf(new BytePtr("byref: %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    if (global.value.params.vsafe)
                    {
                        if ((va.value != null) && va.value.isScope() && ((va.value.storage_class & 17592186044416L) != 0) && ((v.storage_class & 2101248L) == 0L) && (sc_ref.value.get()).func.setUnsafe())
                        {
                            if (!gag)
                                error(ae.loc, new BytePtr("address of local variable `%s` assigned to return scope `%s`"), v.toChars(), va.value.toChars());
                            result = true;
                            continue;
                        }
                    }
                    Dsymbol p = v.toParent2();
                    if ((va.value != null) && va.value.enclosesLifetimeOf(v) && ((v.storage_class & 32L) == 0) || ((va.value.storage_class & 2097152L) != 0) || va.value.isDataseg() && (sc_ref.value.get()).func.setUnsafe())
                    {
                        if (!gag)
                            error(ae.loc, new BytePtr("address of variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                        result = true;
                        continue;
                    }
                    if ((va.value != null) && ((v.storage_class & 2101248L) != 0))
                    {
                        Dsymbol pva = va.value.toParent2();
                        {
                            Dsymbol pv = p;
                            for (; pv != null;){
                                pv = pv.toParent2();
                                if ((pequals(pva, pv)))
                                {
                                    if ((sc_ref.value.get()).func.setUnsafe())
                                    {
                                        if (!gag)
                                            error(ae.loc, new BytePtr("reference `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                                        result = true;
                                        continue ByRef;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (!((va.value != null) && va.value.isScope()))
                        notMaybeScope(v);
                    if (((v.storage_class & 2101248L) == 0L) && (pequals(p, (sc_ref.value.get()).func)))
                    {
                        if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                        {
                            if (!va.value.isScope() && inferScope)
                            {
                                va.value.storage_class |= 562949953945600L;
                            }
                            continue;
                        }
                        if (((e1.op & 0xFF) == 49))
                            continue;
                        if ((sc_ref.value.get()).func.setUnsafe())
                        {
                            if (!gag)
                                error(ae.loc, new BytePtr("reference to local variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            result = true;
                        }
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1278 = er.value.byfunc.opSlice().copy();
                int __key1279 = 0;
                for (; (__key1279 < __r1278.getLength());__key1279 += 1) {
                    FuncDeclaration fd = __r1278.get(__key1279);
                    if (false)
                        printf(new BytePtr("byfunc: %s, %d\n"), fd.toChars(), fd.tookAddressOf);
                    Ref<DArray<VarDeclaration>> vars = ref(new DArray<VarDeclaration>());
                    try {
                        findAllOuterAccessedVariables(fd, ptr(vars));
                        if ((va.value != null) && va.value.isScope() && (fd.tookAddressOf != 0) && global.value.params.vsafe)
                            fd.tookAddressOf -= 1;
                        {
                            Slice<VarDeclaration> __r1280 = vars.value.opSlice().copy();
                            int __key1281 = 0;
                            for (; (__key1281 < __r1280.getLength());__key1281 += 1) {
                                VarDeclaration v = __r1280.get(__key1281);
                                assert(!v.isDataseg());
                                Dsymbol p = v.toParent2();
                                if (!((va.value != null) && va.value.isScope()))
                                    notMaybeScope(v);
                                if (((v.storage_class & 2625536L) != 0) && (pequals(p, (sc_ref.value.get()).func)))
                                {
                                    if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                                    {
                                        continue;
                                    }
                                    if ((sc_ref.value.get()).func.setUnsafe())
                                    {
                                        if (!gag)
                                            error(ae.loc, new BytePtr("reference to local `%s` assigned to non-scope `%s` in @safe code"), v.toChars(), e1.toChars());
                                        result = true;
                                    }
                                    continue;
                                }
                            }
                        }
                    }
                    finally {
                    }
                }
            }
            {
                Slice<Expression> __r1282 = er.value.byexp.opSlice().copy();
                int __key1283 = 0;
                for (; (__key1283 < __r1282.getLength());__key1283 += 1) {
                    Expression ee = __r1282.get(__key1283);
                    if (false)
                        printf(new BytePtr("byexp: %s\n"), ee.toChars());
                    if ((va.value != null) && ((ee.op & 0xFF) == 18) && ((ee.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) && ((va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray) && ((va.value.storage_class & 1099511627776L) == 0))
                    {
                        if (!gag)
                            deprecation(ee.loc, new BytePtr("slice of static array temporary returned by `%s` assigned to longer lived variable `%s`"), ee.toChars(), va.value.toChars());
                        continue;
                    }
                    if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                    {
                        if (!va.value.isScope() && inferScope)
                        {
                            va.value.storage_class |= 562949953945600L;
                        }
                        continue;
                    }
                    if ((sc_ref.value.get()).func.setUnsafe())
                    {
                        if (!gag)
                            error(ee.loc, new BytePtr("reference to stack allocated value returned by `%s` assigned to non-scope `%s`"), ee.toChars(), e1.toChars());
                        result = true;
                    }
                }
            }
            return result;
        }
        finally {
        }
    }

    public static boolean checkThrowEscape(Ptr<Scope> sc, Expression e, boolean gag) {
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(e, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byexp.length == 0))
                return false;
            boolean result = false;
            {
                Slice<VarDeclaration> __r1284 = er.value.byvalue.opSlice().copy();
                int __key1285 = 0;
                for (; (__key1285 < __r1284.getLength());__key1285 += 1) {
                    VarDeclaration v = __r1284.get(__key1285);
                    if (v.isDataseg())
                        continue;
                    if (v.isScope() && !v.iscatchvar)
                    {
                        if (((sc.get())._module != null) && (sc.get())._module.isRoot())
                        {
                            if (global.value.params.vsafe)
                            {
                                if (!gag)
                                    error(e.loc, new BytePtr("scope variable `%s` may not be thrown"), v.toChars());
                                result = true;
                            }
                            continue;
                        }
                    }
                    else
                    {
                        v.doNotInferScope = true;
                    }
                }
            }
            return result;
        }
        finally {
        }
    }

    public static boolean checkNewEscape(Ptr<Scope> sc, Expression e, boolean gag) {
        Ref<Expression> e_ref = ref(e);
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
            printf(new BytePtr("[%s] checkNewEscape, e: `%s`\n"), e_ref.value.loc.toChars(global.value.params.showColumns), e_ref.value.toChars());
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(e_ref.value, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byexp.length == 0))
                return false;
            Ref<Boolean> result = ref(false);
            {
                Slice<VarDeclaration> __r1286 = er.value.byvalue.opSlice().copy();
                int __key1287 = 0;
                for (; (__key1287 < __r1286.getLength());__key1287 += 1) {
                    VarDeclaration v = __r1286.get(__key1287);
                    if (false)
                        printf(new BytePtr("byvalue `%s`\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if (v.isScope())
                    {
                        if (((sc.get())._module != null) && (sc.get())._module.isRoot() && !(pequals(p.parent.value, (sc.get()).func)))
                        {
                            if (global.value.params.vsafe)
                            {
                                if (!gag_ref.value)
                                    error(e_ref.value.loc, new BytePtr("scope variable `%s` may not be copied into allocated memory"), v.toChars());
                                result.value = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!gag_ref.value)
                                error(e_ref.value.loc, new BytePtr("copying `%s` into allocated memory escapes a reference to variadic parameter `%s`"), e_ref.value.toChars(), v.toChars());
                            result.value = false;
                        }
                    }
                    else
                    {
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1288 = er.value.byref.opSlice().copy();
                int __key1289 = 0;
                for (; (__key1289 < __r1288.getLength());__key1289 += 1) {
                    VarDeclaration v = __r1288.get(__key1289);
                    if (false)
                        printf(new BytePtr("byref `%s`\n"), v.toChars());
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v) {
                            Ref<VarDeclaration> v_ref = ref(v);
                            if (!gag_ref.value)
                            {
                                Ref<BytePtr> kind = ref(pcopy((v_ref.value.storage_class & 32L) != 0 ? new BytePtr("parameter") : new BytePtr("local")));
                                error(e_ref.value.loc, new BytePtr("copying `%s` into allocated memory escapes a reference to %s variable `%s`"), e_ref.value.toChars(), kind.value, v_ref.value.toChars());
                            }
                            result.value = true;
                        }
                    };
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if (((v.storage_class & 2101248L) == 0L))
                    {
                        if ((pequals(p, (sc.get()).func)))
                        {
                            escapingRef.invoke(v);
                            continue;
                        }
                    }
                    if ((v.storage_class & 2101248L) != 0)
                    {
                        if (global.value.params.useDIP25 && ((sc.get())._module != null) && (sc.get())._module.isRoot())
                        {
                            if ((pequals(p, (sc.get()).func)))
                            {
                                escapingRef.invoke(v);
                                continue;
                            }
                            FuncDeclaration fd = p.isFuncDeclaration();
                            if ((fd != null) && (fd.type != null) && ((fd.type.ty & 0xFF) == ENUMTY.Tfunction))
                            {
                                TypeFunction tf = (TypeFunction)fd.type;
                                if (tf.isref)
                                {
                                    if (!gag_ref.value)
                                        error(e_ref.value.loc, new BytePtr("storing reference to outer local variable `%s` into allocated memory causes it to escape"), v.toChars());
                                    result.value = true;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            {
                Slice<Expression> __r1290 = er.value.byexp.opSlice().copy();
                int __key1291 = 0;
                for (; (__key1291 < __r1290.getLength());__key1291 += 1) {
                    Expression ee = __r1290.get(__key1291);
                    if (false)
                        printf(new BytePtr("byexp %s\n"), ee.toChars());
                    if (!gag_ref.value)
                        error(ee.loc, new BytePtr("storing reference to stack allocated value returned by `%s` into allocated memory causes it to escape"), ee.toChars());
                    result.value = true;
                }
            }
            return result.value;
        }
        finally {
        }
    }

    public static boolean checkReturnEscape(Ptr<Scope> sc, Expression e, boolean gag) {
        return checkReturnEscapeImpl(sc, e, false, gag);
    }

    public static boolean checkReturnEscapeRef(Ptr<Scope> sc, Expression e, boolean gag) {
        return checkReturnEscapeImpl(sc, e, true, gag);
    }

    public static boolean checkReturnEscapeImpl(Ptr<Scope> sc, Expression e, boolean refs, boolean gag) {
        Ref<Expression> e_ref = ref(e);
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
            printf(new BytePtr("[%s] checkReturnEscapeImpl, refs: %d e: `%s`\n"), e_ref.value.loc.toChars(global.value.params.showColumns), (refs ? 1 : 0), e_ref.value.toChars());
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            if (refs)
                escapeByRef(e_ref.value, ptr(er));
            else
                escapeByValue(e_ref.value, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byexp.length == 0))
                return false;
            Ref<Boolean> result = ref(false);
            {
                Slice<VarDeclaration> __r1292 = er.value.byvalue.opSlice().copy();
                int __key1293 = 0;
                for (; (__key1293 < __r1292.getLength());__key1293 += 1) {
                    VarDeclaration v = __r1292.get(__key1293);
                    if (false)
                        printf(new BytePtr("byvalue `%s`\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if (v.isScope() || ((v.storage_class & 281474976710656L) != 0) && ((v.storage_class & 17592186044416L) == 0) && v.isParameter() && (((sc.get()).func.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        inferReturn((sc.get()).func, v);
                        continue;
                    }
                    if (v.isScope())
                    {
                        if ((v.storage_class & 17592186044416L) != 0)
                            continue;
                        if (((sc.get())._module != null) && (sc.get())._module.isRoot() && !(!refs && (pequals(p.parent.value, (sc.get()).func)) && (p.isFuncDeclaration() != null) && (p.isFuncDeclaration().fes != null)) && !(!refs && (p.isFuncDeclaration() != null) && ((sc.get()).func.isFuncDeclaration().getLevel(p.isFuncDeclaration(), (sc.get()).intypeof) > 0)))
                        {
                            if (global.value.params.vsafe)
                            {
                                if (!gag_ref.value)
                                    error(e_ref.value.loc, new BytePtr("scope variable `%s` may not be returned"), v.toChars());
                                result.value = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!gag_ref.value)
                                error(e_ref.value.loc, new BytePtr("returning `%s` escapes a reference to variadic parameter `%s`"), e_ref.value.toChars(), v.toChars());
                            result.value = false;
                        }
                    }
                    else
                    {
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1294 = er.value.byref.opSlice().copy();
                int __key1295 = 0;
                for (; (__key1295 < __r1294.getLength());__key1295 += 1) {
                    VarDeclaration v = __r1294.get(__key1295);
                    if (false)
                        printf(new BytePtr("byref `%s`\n"), v.toChars());
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v) {
                            Ref<VarDeclaration> v_ref = ref(v);
                            if (!gag_ref.value)
                            {
                                Ref<BytePtr> msg = ref(null);
                                if ((v_ref.value.storage_class & 32L) != 0)
                                    msg.value = pcopy(new BytePtr("returning `%s` escapes a reference to parameter `%s`, perhaps annotate with `return`"));
                                else
                                    msg.value = pcopy(new BytePtr("returning `%s` escapes a reference to local variable `%s`"));
                                error(e_ref.value.loc, msg.value, e_ref.value.toChars(), v_ref.value.toChars());
                            }
                            result.value = true;
                        }
                    };
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if (((v.storage_class & 2101248L) == 0L))
                    {
                        if ((pequals(p, (sc.get()).func)))
                        {
                            escapingRef.invoke(v);
                            continue;
                        }
                        FuncDeclaration fd = p.isFuncDeclaration();
                        if ((fd != null) && (((sc.get()).func.flags & FUNCFLAG.returnInprocess) != 0))
                        {
                            if (global.value.params.vsafe)
                            {
                                (sc.get()).func.storage_class |= 4521191813414912L;
                            }
                        }
                    }
                    if (((v.storage_class & 2101248L) != 0) && ((v.storage_class & 17592186060800L) == 0))
                    {
                        if ((((sc.get()).func.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc.get()).func)))
                        {
                            inferReturn((sc.get()).func, v);
                        }
                        else if (global.value.params.useDIP25 && ((sc.get())._module != null) && (sc.get())._module.isRoot())
                        {
                            if ((pequals(p, (sc.get()).func)))
                            {
                                escapingRef.invoke(v);
                                continue;
                            }
                            FuncDeclaration fd = p.isFuncDeclaration();
                            if ((fd != null) && (fd.type != null) && ((fd.type.ty & 0xFF) == ENUMTY.Tfunction))
                            {
                                TypeFunction tf = (TypeFunction)fd.type;
                                if (tf.isref)
                                {
                                    if (!gag_ref.value)
                                        error(e_ref.value.loc, new BytePtr("escaping reference to outer local variable `%s`"), v.toChars());
                                    result.value = true;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            {
                Slice<Expression> __r1296 = er.value.byexp.opSlice().copy();
                int __key1297 = 0;
                for (; (__key1297 < __r1296.getLength());__key1297 += 1) {
                    Expression ee = __r1296.get(__key1297);
                    if (false)
                        printf(new BytePtr("byexp %s\n"), ee.toChars());
                    if (!gag_ref.value)
                        error(ee.loc, new BytePtr("escaping reference to stack allocated value returned by `%s`"), ee.toChars());
                    result.value = true;
                }
            }
            return result.value;
        }
        finally {
        }
    }

    public static void inferReturn(FuncDeclaration fd, VarDeclaration v) {
        v.storage_class |= 4521191813414912L;
        TypeFunction tf = (TypeFunction)fd.type;
        if ((pequals(v, fd.vthis)))
        {
            fd.storage_class |= 4521191813414912L;
            if (((tf.ty & 0xFF) == ENUMTY.Tfunction))
            {
                tf.isreturn = true;
                tf.isreturninferred = true;
            }
        }
        else
        {
            if (((tf.ty & 0xFF) == ENUMTY.Tfunction))
            {
                int dim = tf.parameterList.length();
                {
                    int __key1298 = 0;
                    int __limit1299 = dim;
                    for (; (__key1298 < __limit1299);__key1298 += 1) {
                        int i = __key1298;
                        Parameter p = tf.parameterList.get(i);
                        if ((pequals(p.ident, v.ident)))
                        {
                            p.storageClass |= 4521191813414912L;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void escapeByValue(Expression e, Ptr<EscapeByResults> er) {
        EscapeVisitor v = new EscapeVisitor(er);
        e.accept(v);
    }

    public static void escapeByRef(Expression e, Ptr<EscapeByResults> er) {
        EscapeRefVisitor v = new EscapeRefVisitor(er);
        e.accept(v);
    }

    public static class EscapeByResults
    {
        public DArray<VarDeclaration> byref = new DArray<VarDeclaration>();
        public DArray<VarDeclaration> byvalue = new DArray<VarDeclaration>();
        public DArray<FuncDeclaration> byfunc = new DArray<FuncDeclaration>();
        public DArray<Expression> byexp = new DArray<Expression>();
        public EscapeByResults(){
            byref = new DArray<VarDeclaration>();
            byvalue = new DArray<VarDeclaration>();
            byfunc = new DArray<FuncDeclaration>();
            byexp = new DArray<Expression>();
        }
        public EscapeByResults copy(){
            EscapeByResults r = new EscapeByResults();
            r.byref = byref.copy();
            r.byvalue = byvalue.copy();
            r.byfunc = byfunc.copy();
            r.byexp = byexp.copy();
            return r;
        }
        public EscapeByResults(DArray<VarDeclaration> byref, DArray<VarDeclaration> byvalue, DArray<FuncDeclaration> byfunc, DArray<Expression> byexp) {
            this.byref = byref;
            this.byvalue = byvalue;
            this.byfunc = byfunc;
            this.byexp = byexp;
        }

        public EscapeByResults opAssign(EscapeByResults that) {
            this.byref = that.byref;
            this.byvalue = that.byvalue;
            this.byfunc = that.byfunc;
            this.byexp = that.byexp;
            return this;
        }
    }
    public static void findAllOuterAccessedVariables(FuncDeclaration fd, Ptr<DArray<VarDeclaration>> vars) {
        {
            Dsymbol p = fd.parent.value;
            for (; p != null;p = p.parent.value){
                FuncDeclaration fdp = p.isFuncDeclaration();
                if (fdp != null)
                {
                    {
                        Slice<VarDeclaration> __r1308 = fdp.closureVars.opSlice().copy();
                        int __key1309 = 0;
                        for (; (__key1309 < __r1308.getLength());__key1309 += 1) {
                            VarDeclaration v = __r1308.get(__key1309);
                            {
                                Slice<FuncDeclaration> __r1310 = v.nestedrefs.opSlice().copy();
                                int __key1311 = 0;
                                for (; (__key1311 < __r1310.getLength());__key1311 += 1) {
                                    FuncDeclaration fdv = __r1310.get(__key1311);
                                    if ((pequals(fdv, fd)))
                                    {
                                        (vars.get()).push(v);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void notMaybeScope(VarDeclaration v) {
        v.storage_class &= -281474976710657L;
    }

    public static void eliminateMaybeScopes(Slice<VarDeclaration> array) {
        boolean log = false;
        if (false)
            printf(new BytePtr("eliminateMaybeScopes()\n"));
        boolean changes = false;
        do {
            {
                changes = false;
                {
                    Slice<VarDeclaration> __r1312 = array.copy();
                    int __key1313 = 0;
                    for (; (__key1313 < __r1312.getLength());__key1313 += 1) {
                        VarDeclaration va = __r1312.get(__key1313);
                        if (false)
                            printf(new BytePtr("  va = %s\n"), va.toChars());
                        if ((va.storage_class & 281474977234944L) == 0)
                        {
                            if (va.maybes != null)
                            {
                                {
                                    Slice<VarDeclaration> __r1314 = (va.maybes.get()).opSlice().copy();
                                    int __key1315 = 0;
                                    for (; (__key1315 < __r1314.getLength());__key1315 += 1) {
                                        VarDeclaration v = __r1314.get(__key1315);
                                        if (false)
                                            printf(new BytePtr("    v = %s\n"), v.toChars());
                                        if ((v.storage_class & 281474976710656L) != 0)
                                        {
                                            notMaybeScope(v);
                                            if ((v.storage_class & 2101248L) == 0)
                                                v.storage_class &= -4521191813414913L;
                                            changes = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (changes);
    }

}
