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
        private Ref<Ptr<EscapeByResults>> er = ref(null);
        public  EscapeVisitor(Ptr<EscapeByResults> er) {
            Ref<Ptr<EscapeByResults>> er_ref = ref(er);
            this.er.value = er_ref.value;
        }

        public  void visit(Expression e) {
        }

        public  void visit(AddrExp e) {
            if (((e.e1.value.op.value & 0xFF) != 49))
            {
                escapeByRef(e.e1.value, this.er.value);
            }
        }

        public  void visit(SymOffExp e) {
            Ref<VarDeclaration> v = ref(e.var.value.isVarDeclaration());
            if (v.value != null)
            {
                (this.er.value.get()).byref.push(v.value);
            }
        }

        public  void visit(VarExp e) {
            Ref<VarDeclaration> v = ref(e.var.value.isVarDeclaration());
            if (v.value != null)
            {
                (this.er.value.get()).byvalue.push(v.value);
            }
        }

        public  void visit(ThisExp e) {
            if (e.var.value != null)
            {
                (this.er.value.get()).byvalue.push(e.var.value);
            }
        }

        public  void visit(DotVarExp e) {
            Type t = e.e1.value.type.value.toBasetype();
            if (((t.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(DelegateExp e) {
            Type t = e.e1.value.type.value.toBasetype();
            if (((t.ty.value & 0xFF) == ENUMTY.Tclass) || ((t.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                escapeByValue(e.e1.value, this.er.value);
            }
            else
            {
                escapeByRef(e.e1.value, this.er.value);
            }
            (this.er.value.get()).byfunc.push(e.func.value);
        }

        public  void visit(FuncExp e) {
            if (((e.fd.value.tok.value & 0xFF) == 160))
            {
                (this.er.value.get()).byfunc.push(e.fd.value);
            }
        }

        public  void visit(TupleExp e) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ArrayLiteralExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                if (e.basis.value != null)
                {
                    e.basis.value.accept(this);
                }
                {
                    Ref<Slice<Expression>> __r1302 = ref((e.elements.value.get()).opSlice().copy());
                    IntRef __key1303 = ref(0);
                    for (; (__key1303.value < __r1302.value.getLength());__key1303.value += 1) {
                        Ref<Expression> el = ref(__r1302.value.get(__key1303.value));
                        if (el.value != null)
                        {
                            el.value.accept(this);
                        }
                    }
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            if (e.elements.value != null)
            {
                {
                    Ref<Slice<Expression>> __r1304 = ref((e.elements.value.get()).opSlice().copy());
                    IntRef __key1305 = ref(0);
                    for (; (__key1305.value < __r1304.value.getLength());__key1305.value += 1) {
                        Ref<Expression> ex = ref(__r1304.value.get(__key1305.value));
                        if (ex.value != null)
                        {
                            ex.value.accept(this);
                        }
                    }
                }
            }
        }

        public  void visit(NewExp e) {
            Type tb = e.newtype.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct) && (e.member.value == null) && (e.arguments.value != null))
            {
                {
                    Ref<Slice<Expression>> __r1306 = ref((e.arguments.value.get()).opSlice().copy());
                    IntRef __key1307 = ref(0);
                    for (; (__key1307.value < __r1306.value.getLength());__key1307.value += 1) {
                        Ref<Expression> ex = ref(__r1306.value.get(__key1307.value));
                        if (ex.value != null)
                        {
                            ex.value.accept(this);
                        }
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) && ((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                escapeByRef(e.e1.value, this.er.value);
            }
            else
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(SliceExp e) {
            if (((e.e1.value.op.value & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e.e1.value).var.value.isVarDeclaration());
                Type tb = e.type.value.toBasetype();
                if (v.value != null)
                {
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                    {
                        return ;
                    }
                    if ((v.value.storage_class.value & 65536L) != 0)
                    {
                        (this.er.value.get()).byvalue.push(v.value);
                        return ;
                    }
                }
            }
            Type t1b = e.e1.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                Type tb = e.type.value.toBasetype();
                if (((tb.ty.value & 0xFF) != ENUMTY.Tsarray))
                {
                    escapeByRef(e.e1.value, this.er.value);
                }
            }
            else
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(IndexExp e) {
            if (((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(BinExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                e.e1.value.accept(this);
                e.e2.value.accept(this);
            }
        }

        public  void visit(BinAssignExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(AssignExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(CommaExp e) {
            e.e2.value.accept(this);
        }

        public  void visit(CondExp e) {
            e.e1.value.accept(this);
            e.e2.value.accept(this);
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            Ref<Type> t1 = ref(e_ref.value.e1.value.type.value.toBasetype());
            Ref<TypeFunction> tf = ref(null);
            Ref<TypeDelegate> dg = ref(null);
            if (((t1.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
            {
                dg.value = (TypeDelegate)t1.value;
                tf.value = (TypeFunction)((TypeDelegate)t1.value).next.value;
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                tf.value = (TypeFunction)t1.value;
            }
            else
            {
                return ;
            }
            if ((e_ref.value.arguments.value != null) && ((e_ref.value.arguments.value.get()).length.value != 0))
            {
                IntRef j = ref((((tf.value.linkage.value == LINK.d) && (tf.value.parameterList.varargs.value == VarArg.variadic)) ? 1 : 0));
                {
                    IntRef i = ref(j.value);
                    for (; (i.value < (e_ref.value.arguments.value.get()).length.value);i.value += 1){
                        Ref<Expression> arg = ref((e_ref.value.arguments.value.get()).get(i.value));
                        IntRef nparams = ref(tf.value.parameterList.length());
                        if ((i.value - j.value < nparams.value) && (i.value >= j.value))
                        {
                            Ref<Parameter> p = ref(tf.value.parameterList.get(i.value - j.value));
                            Ref<Long> stc = ref(tf.value.parameterStorageClass(null, p.value));
                            if (((stc.value & 524288L) != 0) && ((stc.value & 17592186044416L) != 0))
                            {
                                arg.value.accept(this);
                            }
                            else if (((stc.value & 2097152L) != 0) && ((stc.value & 17592186044416L) != 0))
                            {
                                escapeByRef(arg.value, this.er.value);
                            }
                        }
                    }
                }
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 27) && ((t1.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                DotVarExp dve = (DotVarExp)e_ref.value.e1.value;
                Ref<FuncDeclaration> fd = ref(dve.var.value.isFuncDeclaration());
                Ref<AggregateDeclaration> ad = ref(null);
                if (global.params.vsafe.value && tf.value.isreturn.value && (fd.value != null) && ((ad.value = fd.value.isThis()) != null))
                {
                    if ((ad.value.isClassDeclaration() != null) || tf.value.isscope.value)
                    {
                        dve.e1.value.accept(this);
                    }
                    else if (ad.value.isStructDeclaration() != null)
                    {
                        escapeByRef(dve.e1.value, this.er.value);
                    }
                }
                else if (((dve.var.value.storage_class.value & 17592186044416L) != 0) || tf.value.isreturn.value)
                {
                    if ((dve.var.value.storage_class.value & 524288L) != 0)
                    {
                        dve.e1.value.accept(this);
                    }
                    else if ((dve.var.value.storage_class.value & 2097152L) != 0)
                    {
                        escapeByRef(dve.e1.value, this.er.value);
                    }
                }
                if ((fd.value != null) && fd.value.isNested())
                {
                    if (tf.value.isreturn.value && tf.value.isscope.value)
                    {
                        (this.er.value.get()).byexp.push(e_ref.value);
                    }
                }
            }
            if (dg.value != null)
            {
                if (tf.value.isreturn.value)
                {
                    e_ref.value.e1.value.accept(this);
                }
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 26))
            {
                VarExp ve = (VarExp)e_ref.value.e1.value;
                Ref<FuncDeclaration> fd = ref(ve.var.value.isFuncDeclaration());
                if ((fd.value != null) && fd.value.isNested())
                {
                    if (tf.value.isreturn.value && tf.value.isscope.value)
                    {
                        (this.er.value.get()).byexp.push(e_ref.value);
                    }
                }
            }
        }


        public EscapeVisitor() {}
    }
    private static class EscapeRefVisitor extends Visitor
    {
        private Ref<Ptr<EscapeByResults>> er = ref(null);
        public  EscapeRefVisitor(Ptr<EscapeByResults> er) {
            Ref<Ptr<EscapeByResults>> er_ref = ref(er);
            this.er.value = er_ref.value;
        }

        public  void visit(Expression e) {
        }

        public  void visit(VarExp e) {
            Ref<VarDeclaration> v = ref(e.var.value.isVarDeclaration());
            if (v.value != null)
            {
                if (((v.value.storage_class.value & 2097152L) != 0) && ((v.value.storage_class.value & 1099511644160L) != 0) && (v.value._init.value != null))
                {
                    {
                        Ref<ExpInitializer> ez = ref(v.value._init.value.isExpInitializer());
                        if ((ez.value) != null)
                        {
                            assert((ez.value.exp.value != null) && ((ez.value.exp.value.op.value & 0xFF) == 95));
                            Expression ex = ((ConstructExp)ez.value.exp.value).e2.value;
                            ex.accept(this);
                        }
                    }
                }
                else
                {
                    (this.er.value.get()).byref.push(v.value);
                }
            }
        }

        public  void visit(ThisExp e) {
            Ref<ThisExp> e_ref = ref(e);
            if ((e_ref.value.var.value != null) && e_ref.value.var.value.toParent2().isFuncDeclaration().isThis2.value)
            {
                escapeByValue(e_ref.value, this.er.value);
            }
            else if (e_ref.value.var.value != null)
            {
                (this.er.value.get()).byref.push(e_ref.value.var.value);
            }
        }

        public  void visit(PtrExp e) {
            escapeByValue(e.e1.value, this.er.value);
        }

        public  void visit(IndexExp e) {
            Type tb = e.e1.value.type.value.toBasetype();
            if (((e.e1.value.op.value & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e.e1.value).var.value.isVarDeclaration());
                if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    if ((v.value != null) && ((v.value.storage_class.value & 65536L) != 0))
                    {
                        (this.er.value.get()).byref.push(v.value);
                        return ;
                    }
                }
            }
            if (((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e.e1.value.accept(this);
            }
            else if (((tb.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                escapeByValue(e.e1.value, this.er.value);
            }
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            if (e_ref.value.elements.value != null)
            {
                {
                    Ref<Slice<Expression>> __r1308 = ref((e_ref.value.elements.value.get()).opSlice().copy());
                    IntRef __key1309 = ref(0);
                    for (; (__key1309.value < __r1308.value.getLength());__key1309.value += 1) {
                        Ref<Expression> ex = ref(__r1308.value.get(__key1309.value));
                        if (ex.value != null)
                        {
                            ex.value.accept(this);
                        }
                    }
                }
            }
            (this.er.value.get()).byexp.push(e_ref.value);
        }

        public  void visit(DotVarExp e) {
            Type t1b = e.e1.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                escapeByValue(e.e1.value, this.er.value);
            }
            else
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(BinAssignExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(AssignExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(CommaExp e) {
            e.e2.value.accept(this);
        }

        public  void visit(CondExp e) {
            e.e1.value.accept(this);
            e.e2.value.accept(this);
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            Ref<Type> t1 = ref(e_ref.value.e1.value.type.value.toBasetype());
            Ref<TypeFunction> tf = ref(null);
            if (((t1.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
            {
                tf.value = (TypeFunction)((TypeDelegate)t1.value).next.value;
            }
            else if (((t1.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                tf.value = (TypeFunction)t1.value;
            }
            else
            {
                return ;
            }
            if (tf.value.isref.value)
            {
                if ((e_ref.value.arguments.value != null) && ((e_ref.value.arguments.value.get()).length.value != 0))
                {
                    IntRef j = ref((((tf.value.linkage.value == LINK.d) && (tf.value.parameterList.varargs.value == VarArg.variadic)) ? 1 : 0));
                    {
                        IntRef i = ref(j.value);
                        for (; (i.value < (e_ref.value.arguments.value.get()).length.value);i.value += 1){
                            Ref<Expression> arg = ref((e_ref.value.arguments.value.get()).get(i.value));
                            IntRef nparams = ref(tf.value.parameterList.length());
                            if ((i.value - j.value < nparams.value) && (i.value >= j.value))
                            {
                                Ref<Parameter> p = ref(tf.value.parameterList.get(i.value - j.value));
                                Ref<Long> stc = ref(tf.value.parameterStorageClass(null, p.value));
                                if (((stc.value & 2101248L) != 0) && ((stc.value & 17592186044416L) != 0))
                                {
                                    arg.value.accept(this);
                                }
                                else if (((stc.value & 524288L) != 0) && ((stc.value & 17592186044416L) != 0))
                                {
                                    if (((arg.value.op.value & 0xFF) == 160))
                                    {
                                        Ref<DelegateExp> de = ref((DelegateExp)arg.value);
                                        if (de.value.func.value.isNested())
                                        {
                                            (this.er.value.get()).byexp.push(de.value);
                                        }
                                    }
                                    else
                                    {
                                        escapeByValue(arg.value, this.er.value);
                                    }
                                }
                            }
                        }
                    }
                }
                if (((e_ref.value.e1.value.op.value & 0xFF) == 27) && ((t1.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                {
                    DotVarExp dve = (DotVarExp)e_ref.value.e1.value;
                    if (((dve.var.value.storage_class.value & 17592186044416L) != 0) || tf.value.isreturn.value)
                    {
                        if (((dve.var.value.storage_class.value & 524288L) != 0) || tf.value.isscope.value)
                        {
                            escapeByValue(dve.e1.value, this.er.value);
                        }
                        else if (((dve.var.value.storage_class.value & 2097152L) != 0) || tf.value.isref.value)
                        {
                            dve.e1.value.accept(this);
                        }
                    }
                    Ref<FuncDeclaration> fd = ref(dve.var.value.isFuncDeclaration());
                    if ((fd.value != null) && fd.value.isNested())
                    {
                        if (tf.value.isreturn.value)
                        {
                            (this.er.value.get()).byexp.push(e_ref.value);
                        }
                    }
                }
                if (((e_ref.value.e1.value.op.value & 0xFF) == 26) && ((t1.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                {
                    escapeByValue(e_ref.value.e1.value, this.er.value);
                }
                if (((e_ref.value.e1.value.op.value & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)e_ref.value.e1.value;
                    Ref<FuncDeclaration> fd = ref(ve.var.value.isFuncDeclaration());
                    if ((fd.value != null) && fd.value.isNested())
                    {
                        if (tf.value.isreturn.value)
                        {
                            (this.er.value.get()).byexp.push(e_ref.value);
                        }
                    }
                }
            }
            else
            {
                (this.er.value.get()).byexp.push(e_ref.value);
            }
        }


        public EscapeRefVisitor() {}
    }

    public static boolean checkArrayLiteralEscape(Ptr<Scope> sc, ArrayLiteralExp ae, boolean gag) {
        boolean errors = false;
        if (ae.basis.value != null)
        {
            errors = checkNewEscape(sc, ae.basis.value, gag);
        }
        {
            Slice<Expression> __r1258 = (ae.elements.value.get()).opSlice().copy();
            int __key1259 = 0;
            for (; (__key1259 < __r1258.getLength());__key1259 += 1) {
                Expression ex = __r1258.get(__key1259);
                if (ex != null)
                {
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
                }
            }
        }
        return errors;
    }

    public static boolean checkAssocArrayLiteralEscape(Ptr<Scope> sc, AssocArrayLiteralExp ae, boolean gag) {
        boolean errors = false;
        {
            Slice<Expression> __r1260 = (ae.keys.value.get()).opSlice().copy();
            int __key1261 = 0;
            for (; (__key1261 < __r1260.getLength());__key1261 += 1) {
                Expression ex = __r1260.get(__key1261);
                if (ex != null)
                {
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
                }
            }
        }
        {
            Slice<Expression> __r1262 = (ae.values.value.get()).opSlice().copy();
            int __key1263 = 0;
            for (; (__key1263 < __r1262.getLength());__key1263 += 1) {
                Expression ex = __r1262.get(__key1263);
                if (ex != null)
                {
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
                }
            }
        }
        return errors;
    }

    public static boolean checkParamArgumentEscape(Ptr<Scope> sc, FuncDeclaration fdc, Parameter par, Expression arg, boolean gag) {
        Ref<FuncDeclaration> fdc_ref = ref(fdc);
        Ref<Parameter> par_ref = ref(par);
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("checkParamArgumentEscape(arg: %s par: %s)\n"), arg != null ? arg.toChars() : new BytePtr("null"), par_ref.value != null ? par_ref.value.toChars() : new BytePtr("this"));
        }
        if (!arg.type.value.hasPointers())
        {
            return false;
        }
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(arg, ptr(er));
            if ((er.value.byref.length.value == 0) && (er.value.byvalue.length.value == 0) && (er.value.byfunc.length.value == 0) && (er.value.byexp.length.value == 0))
            {
                return false;
            }
            Ref<Boolean> result = ref(false);
            Function2<VarDeclaration,BytePtr,Void> unsafeAssign = new Function2<VarDeclaration,BytePtr,Void>(){
                public Void invoke(VarDeclaration v, BytePtr desc) {
                    if (global.params.vsafe.value && (sc.get()).func.value.setUnsafe())
                    {
                        if (!gag_ref.value)
                        {
                            error(arg.loc.value, new BytePtr("%s `%s` assigned to non-scope parameter `%s` calling %s"), desc, v.toChars(), par_ref.value != null ? par_ref.value.toChars() : new BytePtr("this"), fdc_ref.value != null ? fdc_ref.value.toPrettyChars(false) : new BytePtr("indirectly"));
                        }
                        result.value = true;
                    }
                    return null;
                }
            };
            {
                Slice<VarDeclaration> __r1264 = er.value.byvalue.opSlice().copy();
                int __key1265 = 0;
                for (; (__key1265 < __r1264.getLength());__key1265 += 1) {
                    VarDeclaration v = __r1264.get(__key1265);
                    if (false)
                    {
                        printf(new BytePtr("byvalue %s\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    notMaybeScope(v);
                    if (v.isScope())
                    {
                        unsafeAssign.invoke(v, new BytePtr("scope variable"));
                    }
                    else if (((v.storage_class.value & 65536L) != 0) && (pequals(p, (sc.get()).func.value)))
                    {
                        Type tb = v.type.value.toBasetype();
                        if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            unsafeAssign.invoke(v, new BytePtr("variadic variable"));
                        }
                    }
                    else
                    {
                        if (false)
                        {
                            printf(new BytePtr("no infer for %s in %s loc %s, fdc %s, %d\n"), v.toChars(), (sc.get()).func.value.ident.value.toChars(), (sc.get()).func.value.loc.value.toChars(global.params.showColumns.value), fdc_ref.value.ident.value.toChars(), 162);
                        }
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1266 = er.value.byref.opSlice().copy();
                int __key1267 = 0;
                for (; (__key1267 < __r1266.getLength());__key1267 += 1) {
                    VarDeclaration v = __r1266.get(__key1267);
                    if (false)
                    {
                        printf(new BytePtr("byref %s\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    notMaybeScope(v);
                    if (((v.storage_class.value & 2101248L) == 0L) && (pequals(p, (sc.get()).func.value)))
                    {
                        if ((par_ref.value != null) && ((par_ref.value.storageClass.value & 17592186568704L) == 524288L))
                        {
                            continue;
                        }
                        unsafeAssign.invoke(v, new BytePtr("reference to local variable"));
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1268 = er.value.byfunc.opSlice().copy();
                int __key1269 = 0;
                for (; (__key1269 < __r1268.getLength());__key1269 += 1) {
                    FuncDeclaration fd = __r1268.get(__key1269);
                    Ref<DArray<VarDeclaration>> vars = ref(new DArray<VarDeclaration>());
                    try {
                        findAllOuterAccessedVariables(fd, ptr(vars));
                        {
                            Slice<VarDeclaration> __r1270 = vars.value.opSlice().copy();
                            int __key1271 = 0;
                            for (; (__key1271 < __r1270.getLength());__key1271 += 1) {
                                VarDeclaration v = __r1270.get(__key1271);
                                assert(!v.isDataseg());
                                Dsymbol p = v.toParent2();
                                notMaybeScope(v);
                                if (((v.storage_class.value & 2625536L) != 0) && (pequals(p, (sc.get()).func.value)))
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
                Slice<Expression> __r1272 = er.value.byexp.opSlice().copy();
                int __key1273 = 0;
                for (; (__key1273 < __r1272.getLength());__key1273 += 1) {
                    Expression ee = __r1272.get(__key1273);
                    if ((sc.get()).func.value.setUnsafe())
                    {
                        if (!gag_ref.value)
                        {
                            error(ee.loc.value, new BytePtr("reference to stack allocated value returned by `%s` assigned to non-scope parameter `%s`"), ee.toChars(), par_ref.value != null ? par_ref.value.toChars() : new BytePtr("this"));
                        }
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
        {
            printf(new BytePtr("checkParamArgumentReturn(firstArg: %s arg: %s)\n"), firstArg.toChars(), arg.toChars());
        }
        if (!arg.type.value.hasPointers())
        {
            return false;
        }
        AssignExp e = new AssignExp(arg.loc.value, firstArg, arg);
        return checkAssignEscape(sc, e, gag);
    }

    public static boolean checkConstructorEscape(Ptr<Scope> sc, CallExp ce, boolean gag) {
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("checkConstructorEscape(%s, %s)\n"), ce.toChars(), ce.type.value.toChars());
        }
        Type tthis = ce.type.value.toBasetype();
        assert(((tthis.ty.value & 0xFF) == ENUMTY.Tstruct));
        if (!tthis.hasPointers())
        {
            return false;
        }
        if ((ce.arguments.value == null) && ((ce.arguments.value.get()).length.value != 0))
        {
            return false;
        }
        assert(((ce.e1.value.op.value & 0xFF) == 27));
        DotVarExp dve = (DotVarExp)ce.e1.value;
        CtorDeclaration ctor = dve.var.value.isCtorDeclaration();
        assert(ctor != null);
        assert(((ctor.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
        TypeFunction tf = (TypeFunction)ctor.type.value;
        int nparams = tf.parameterList.length();
        int n = (ce.arguments.value.get()).length.value;
        boolean j = (tf.linkage.value == LINK.d) && (tf.parameterList.varargs.value == VarArg.variadic);
        {
            int __key1274 = 0;
            int __limit1275 = n;
            for (; (__key1274 < __limit1275);__key1274 += 1) {
                int i = __key1274;
                Expression arg = (ce.arguments.value.get()).get(i);
                if (!arg.type.value.hasPointers())
                {
                    return false;
                }
                if ((i - (j ? 1 : 0) < nparams) && (i >= (j ? 1 : 0)))
                {
                    Parameter p = tf.parameterList.get(i - (j ? 1 : 0));
                    if ((p.storageClass.value & 17592186044416L) != 0)
                    {
                        AssignExp e = new AssignExp(arg.loc.value, dve.e1.value, arg);
                        if (checkAssignEscape(sc, e, gag))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkAssignEscape(Ptr<Scope> sc, Expression e, boolean gag) {
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("checkAssignEscape(e: %s)\n"), e.toChars());
        }
        if (((e.op.value & 0xFF) != 90) && ((e.op.value & 0xFF) != 96) && ((e.op.value & 0xFF) != 95) && ((e.op.value & 0xFF) != 71) && ((e.op.value & 0xFF) != 72) && ((e.op.value & 0xFF) != 73))
        {
            return false;
        }
        BinExp ae = (BinExp)e;
        Expression e1 = ae.e1.value;
        Expression e2 = ae.e2.value;
        if (!e1.type.value.hasPointers())
        {
            return false;
        }
        if (((e1.op.value & 0xFF) == 31))
        {
            return false;
        }
        if (((e1.op.value & 0xFF) == 49))
        {
            return false;
        }
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(e2, ptr(er));
            if ((er.value.byref.length.value == 0) && (er.value.byvalue.length.value == 0) && (er.value.byfunc.length.value == 0) && (er.value.byexp.length.value == 0))
            {
                return false;
            }
            Ref<VarDeclaration> va = ref(expToVariable(e1));
            if ((va.value != null) && ((e.op.value & 0xFF) == 72))
            {
                va.value = null;
            }
            if ((va.value != null) && ((e1.op.value & 0xFF) == 27) && ((va.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tclass))
            {
                va.value = null;
            }
            if (false)
            {
                printf(new BytePtr("va: %s\n"), va.value.toChars());
            }
            boolean inferScope = false;
            if ((va.value != null) && ((sc.get()).func.value != null) && ((sc.get()).func.value.type.value != null) && (((sc.get()).func.value.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                inferScope = ((TypeFunction)(sc.get()).func.value.type.value).trust.value != TRUST.system;
            }
            boolean vaIsRef = (va.value != null) && ((va.value.storage_class.value & 32L) != 0) && ((va.value.storage_class.value & 2101248L) != 0) || ((va.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tclass);
            if (false)
            {
                printf(new BytePtr("va is ref `%s`\n"), va.value.toChars());
            }
            Function0<Boolean> isFirstRef = new Function0<Boolean>(){
                public Boolean invoke() {
                    if (!vaIsRef)
                    {
                        return false;
                    }
                    Ref<Dsymbol> p = ref(va.value.toParent2());
                    Ref<FuncDeclaration> fd = ref((sc.get()).func.value);
                    if ((pequals(p.value, fd.value)) && (fd.value.type.value != null) && ((fd.value.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                    {
                        TypeFunction tf = (TypeFunction)fd.value.type.value;
                        if ((tf.nextOf() == null) || ((tf.nextOf().ty.value & 0xFF) != ENUMTY.Tvoid) && (fd.value.isCtorDeclaration() == null))
                        {
                            return false;
                        }
                        if ((pequals(va.value, fd.value.vthis.value)))
                        {
                            return true;
                        }
                        if ((fd.value.parameters.value != null) && ((fd.value.parameters.value.get()).length.value != 0) && (pequals((fd.value.parameters.value.get()).get(0), va.value)))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            };
            boolean vaIsFirstRef = isFirstRef.invoke();
            if (false)
            {
                printf(new BytePtr("va is first ref `%s`\n"), va.value.toChars());
            }
            boolean result = false;
            {
                Slice<VarDeclaration> __r1276 = er.value.byvalue.opSlice().copy();
                int __key1277 = 0;
                for (; (__key1277 < __r1276.getLength());__key1277 += 1) {
                    VarDeclaration v = __r1276.get(__key1277);
                    if (false)
                    {
                        printf(new BytePtr("byvalue: %s\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    if ((pequals(v, va.value)))
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    if ((va.value != null) && !vaIsRef && !va.value.isScope() && !v.isScope() && ((va.value.storage_class.value & v.storage_class.value & 281474976776192L) == 281474976710656L) && (pequals(p, (sc.get()).func.value)))
                    {
                        va.value.addMaybe(v);
                        continue;
                    }
                    if (vaIsFirstRef && v.isScope() || ((v.storage_class.value & 281474976710656L) != 0) && ((v.storage_class.value & 17592186044416L) == 0) && v.isParameter() && (((sc.get()).func.value.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc.get()).func.value)))
                    {
                        if (false)
                        {
                            printf(new BytePtr("inferring 'return' for parameter %s in function %s\n"), v.toChars(), (sc.get()).func.value.toChars());
                        }
                        inferReturn((sc.get()).func.value, v);
                    }
                    if (!((va.value != null) && va.value.isScope()) || vaIsRef)
                    {
                        notMaybeScope(v);
                    }
                    if (v.isScope())
                    {
                        if (vaIsFirstRef && v.isParameter() && ((v.storage_class.value & 17592186044416L) != 0))
                        {
                            if (va.value.isScope())
                            {
                                continue;
                            }
                            if (inferScope && !va.value.doNotInferScope)
                            {
                                if (false)
                                {
                                    printf(new BytePtr("inferring scope for lvalue %s\n"), va.value.toChars());
                                }
                                va.value.storage_class.value |= 562949953945600L;
                                continue;
                            }
                        }
                        if ((va.value != null) && va.value.isScope() && ((va.value.storage_class.value & 17592186044416L) != 0) && ((v.storage_class.value & 17592186044416L) == 0) && (sc.get()).func.value.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc.value, new BytePtr("scope variable `%s` assigned to return scope `%s`"), v.toChars(), va.value.toChars());
                            }
                            result = true;
                            continue;
                        }
                        if ((va.value != null) && va.value.enclosesLifetimeOf(v) && ((v.storage_class.value & 1099511627808L) == 0) || ((ae.e1.value.op.value & 0xFF) == 27) && ((va.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tclass) && va.value.enclosesLifetimeOf(v) || !va.value.isScope() || vaIsRef || ((va.value.storage_class.value & 2101248L) != 0) && ((v.storage_class.value & 1099511627808L) == 0) && (sc.get()).func.value.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc.value, new BytePtr("scope variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                            }
                            result = true;
                            continue;
                        }
                        if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                        {
                            if (!va.value.isScope() && inferScope)
                            {
                                va.value.storage_class.value |= 562949953945600L;
                                if (((v.storage_class.value & 17592186044416L) != 0) && ((va.value.storage_class.value & 17592186044416L) == 0))
                                {
                                    va.value.storage_class.value |= 4521191813414912L;
                                }
                            }
                            continue;
                        }
                        if ((sc.get()).func.value.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc.value, new BytePtr("scope variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            }
                            result = true;
                        }
                    }
                    else if (((v.storage_class.value & 65536L) != 0) && (pequals(p, (sc.get()).func.value)))
                    {
                        Type tb = v.type.value.toBasetype();
                        if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                            {
                                if (!va.value.isScope() && inferScope)
                                {
                                    va.value.storage_class.value |= 562949953945600L;
                                }
                                continue;
                            }
                            if ((sc.get()).func.value.setUnsafe())
                            {
                                if (!gag)
                                {
                                    error(ae.loc.value, new BytePtr("variadic variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                                }
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
                Slice<VarDeclaration> __r1278 = er.value.byref.opSlice().copy();
                int __key1279 = 0;
                for (; (__key1279 < __r1278.getLength());__key1279 += 1) {
                    VarDeclaration v = __r1278.get(__key1279);
                    if (false)
                    {
                        printf(new BytePtr("byref: %s\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    if (global.params.vsafe.value)
                    {
                        if ((va.value != null) && va.value.isScope() && ((va.value.storage_class.value & 17592186044416L) != 0) && ((v.storage_class.value & 2101248L) == 0L) && (sc.get()).func.value.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc.value, new BytePtr("address of local variable `%s` assigned to return scope `%s`"), v.toChars(), va.value.toChars());
                            }
                            result = true;
                            continue;
                        }
                    }
                    Dsymbol p = v.toParent2();
                    if ((va.value != null) && va.value.enclosesLifetimeOf(v) && ((v.storage_class.value & 32L) == 0) || ((va.value.storage_class.value & 2097152L) != 0) || va.value.isDataseg() && (sc.get()).func.value.setUnsafe())
                    {
                        if (!gag)
                        {
                            error(ae.loc.value, new BytePtr("address of variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                        }
                        result = true;
                        continue;
                    }
                    if ((va.value != null) && ((v.storage_class.value & 2101248L) != 0))
                    {
                        Dsymbol pva = va.value.toParent2();
                        {
                            Dsymbol pv = p;
                            for (; pv != null;){
                                pv = pv.toParent2();
                                if ((pequals(pva, pv)))
                                {
                                    if ((sc.get()).func.value.setUnsafe())
                                    {
                                        if (!gag)
                                        {
                                            error(ae.loc.value, new BytePtr("reference `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                                        }
                                        result = true;
                                        continue ByRef;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (!((va.value != null) && va.value.isScope()))
                    {
                        notMaybeScope(v);
                    }
                    if (((v.storage_class.value & 2101248L) == 0L) && (pequals(p, (sc.get()).func.value)))
                    {
                        if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                        {
                            if (!va.value.isScope() && inferScope)
                            {
                                va.value.storage_class.value |= 562949953945600L;
                            }
                            continue;
                        }
                        if (((e1.op.value & 0xFF) == 49))
                        {
                            continue;
                        }
                        if ((sc.get()).func.value.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc.value, new BytePtr("reference to local variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            }
                            result = true;
                        }
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1280 = er.value.byfunc.opSlice().copy();
                int __key1281 = 0;
                for (; (__key1281 < __r1280.getLength());__key1281 += 1) {
                    FuncDeclaration fd = __r1280.get(__key1281);
                    if (false)
                    {
                        printf(new BytePtr("byfunc: %s, %d\n"), fd.toChars(), fd.tookAddressOf.value);
                    }
                    Ref<DArray<VarDeclaration>> vars = ref(new DArray<VarDeclaration>());
                    try {
                        findAllOuterAccessedVariables(fd, ptr(vars));
                        if ((va.value != null) && va.value.isScope() && (fd.tookAddressOf.value != 0) && global.params.vsafe.value)
                        {
                            fd.tookAddressOf.value -= 1;
                        }
                        {
                            Slice<VarDeclaration> __r1282 = vars.value.opSlice().copy();
                            int __key1283 = 0;
                            for (; (__key1283 < __r1282.getLength());__key1283 += 1) {
                                VarDeclaration v = __r1282.get(__key1283);
                                assert(!v.isDataseg());
                                Dsymbol p = v.toParent2();
                                if (!((va.value != null) && va.value.isScope()))
                                {
                                    notMaybeScope(v);
                                }
                                if (((v.storage_class.value & 2625536L) != 0) && (pequals(p, (sc.get()).func.value)))
                                {
                                    if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                                    {
                                        continue;
                                    }
                                    if ((sc.get()).func.value.setUnsafe())
                                    {
                                        if (!gag)
                                        {
                                            error(ae.loc.value, new BytePtr("reference to local `%s` assigned to non-scope `%s` in @safe code"), v.toChars(), e1.toChars());
                                        }
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
                Slice<Expression> __r1284 = er.value.byexp.opSlice().copy();
                int __key1285 = 0;
                for (; (__key1285 < __r1284.getLength());__key1285 += 1) {
                    Expression ee = __r1284.get(__key1285);
                    if (false)
                    {
                        printf(new BytePtr("byexp: %s\n"), ee.toChars());
                    }
                    if ((va.value != null) && ((ee.op.value & 0xFF) == 18) && ((ee.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray) && ((va.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray) && ((va.value.storage_class.value & 1099511627776L) == 0))
                    {
                        if (!gag)
                        {
                            deprecation(ee.loc.value, new BytePtr("slice of static array temporary returned by `%s` assigned to longer lived variable `%s`"), ee.toChars(), va.value.toChars());
                        }
                        continue;
                    }
                    if ((va.value != null) && !va.value.isDataseg() && !va.value.doNotInferScope)
                    {
                        if (!va.value.isScope() && inferScope)
                        {
                            va.value.storage_class.value |= 562949953945600L;
                        }
                        continue;
                    }
                    if ((sc.get()).func.value.setUnsafe())
                    {
                        if (!gag)
                        {
                            error(ee.loc.value, new BytePtr("reference to stack allocated value returned by `%s` assigned to non-scope `%s`"), ee.toChars(), e1.toChars());
                        }
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
            if ((er.value.byref.length.value == 0) && (er.value.byvalue.length.value == 0) && (er.value.byexp.length.value == 0))
            {
                return false;
            }
            boolean result = false;
            {
                Slice<VarDeclaration> __r1286 = er.value.byvalue.opSlice().copy();
                int __key1287 = 0;
                for (; (__key1287 < __r1286.getLength());__key1287 += 1) {
                    VarDeclaration v = __r1286.get(__key1287);
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    if (v.isScope() && !v.iscatchvar)
                    {
                        if (((sc.get())._module.value != null) && (sc.get())._module.value.isRoot())
                        {
                            if (global.params.vsafe.value)
                            {
                                if (!gag)
                                {
                                    error(e.loc.value, new BytePtr("scope variable `%s` may not be thrown"), v.toChars());
                                }
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
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("[%s] checkNewEscape, e: `%s`\n"), e.loc.value.toChars(global.params.showColumns.value), e.toChars());
        }
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(e, ptr(er));
            if ((er.value.byref.length.value == 0) && (er.value.byvalue.length.value == 0) && (er.value.byexp.length.value == 0))
            {
                return false;
            }
            Ref<Boolean> result = ref(false);
            {
                Slice<VarDeclaration> __r1288 = er.value.byvalue.opSlice().copy();
                int __key1289 = 0;
                for (; (__key1289 < __r1288.getLength());__key1289 += 1) {
                    VarDeclaration v = __r1288.get(__key1289);
                    if (false)
                    {
                        printf(new BytePtr("byvalue `%s`\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    if (v.isScope())
                    {
                        if (((sc.get())._module.value != null) && (sc.get())._module.value.isRoot() && !(pequals(p.parent.value, (sc.get()).func.value)))
                        {
                            if (global.params.vsafe.value)
                            {
                                if (!gag_ref.value)
                                {
                                    error(e.loc.value, new BytePtr("scope variable `%s` may not be copied into allocated memory"), v.toChars());
                                }
                                result.value = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class.value & 65536L) != 0) && (pequals(p, (sc.get()).func.value)))
                    {
                        Type tb = v.type.value.toBasetype();
                        if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!gag_ref.value)
                            {
                                error(e.loc.value, new BytePtr("copying `%s` into allocated memory escapes a reference to variadic parameter `%s`"), e.toChars(), v.toChars());
                            }
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
                Slice<VarDeclaration> __r1290 = er.value.byref.opSlice().copy();
                int __key1291 = 0;
                for (; (__key1291 < __r1290.getLength());__key1291 += 1) {
                    VarDeclaration v = __r1290.get(__key1291);
                    if (false)
                    {
                        printf(new BytePtr("byref `%s`\n"), v.toChars());
                    }
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v) {
                            if (!gag_ref.value)
                            {
                                Ref<BytePtr> kind = ref(pcopy((v.storage_class.value & 32L) != 0 ? new BytePtr("parameter") : new BytePtr("local")));
                                error(e.loc.value, new BytePtr("copying `%s` into allocated memory escapes a reference to %s variable `%s`"), e.toChars(), kind.value, v.toChars());
                            }
                            result.value = true;
                            return null;
                        }
                    };
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    if (((v.storage_class.value & 2101248L) == 0L))
                    {
                        if ((pequals(p, (sc.get()).func.value)))
                        {
                            escapingRef.invoke(v);
                            continue;
                        }
                    }
                    if ((v.storage_class.value & 2101248L) != 0)
                    {
                        if (global.params.useDIP25 && ((sc.get())._module.value != null) && (sc.get())._module.value.isRoot())
                        {
                            if ((pequals(p, (sc.get()).func.value)))
                            {
                                escapingRef.invoke(v);
                                continue;
                            }
                            FuncDeclaration fd = p.isFuncDeclaration();
                            if ((fd != null) && (fd.type.value != null) && ((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                            {
                                TypeFunction tf = (TypeFunction)fd.type.value;
                                if (tf.isref.value)
                                {
                                    if (!gag_ref.value)
                                    {
                                        error(e.loc.value, new BytePtr("storing reference to outer local variable `%s` into allocated memory causes it to escape"), v.toChars());
                                    }
                                    result.value = true;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            {
                Slice<Expression> __r1292 = er.value.byexp.opSlice().copy();
                int __key1293 = 0;
                for (; (__key1293 < __r1292.getLength());__key1293 += 1) {
                    Expression ee = __r1292.get(__key1293);
                    if (false)
                    {
                        printf(new BytePtr("byexp %s\n"), ee.toChars());
                    }
                    if (!gag_ref.value)
                    {
                        error(ee.loc.value, new BytePtr("storing reference to stack allocated value returned by `%s` into allocated memory causes it to escape"), ee.toChars());
                    }
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
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("[%s] checkReturnEscapeImpl, refs: %d e: `%s`\n"), e.loc.value.toChars(global.params.showColumns.value), (refs ? 1 : 0), e.toChars());
        }
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            if (refs)
            {
                escapeByRef(e, ptr(er));
            }
            else
            {
                escapeByValue(e, ptr(er));
            }
            if ((er.value.byref.length.value == 0) && (er.value.byvalue.length.value == 0) && (er.value.byexp.length.value == 0))
            {
                return false;
            }
            Ref<Boolean> result = ref(false);
            {
                Slice<VarDeclaration> __r1294 = er.value.byvalue.opSlice().copy();
                int __key1295 = 0;
                for (; (__key1295 < __r1294.getLength());__key1295 += 1) {
                    VarDeclaration v = __r1294.get(__key1295);
                    if (false)
                    {
                        printf(new BytePtr("byvalue `%s`\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    if (v.isScope() || ((v.storage_class.value & 281474976710656L) != 0) && ((v.storage_class.value & 17592186044416L) == 0) && v.isParameter() && (((sc.get()).func.value.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc.get()).func.value)))
                    {
                        inferReturn((sc.get()).func.value, v);
                        continue;
                    }
                    if (v.isScope())
                    {
                        if ((v.storage_class.value & 17592186044416L) != 0)
                        {
                            continue;
                        }
                        if (((sc.get())._module.value != null) && (sc.get())._module.value.isRoot() && !(!refs && (pequals(p.parent.value, (sc.get()).func.value)) && (p.isFuncDeclaration() != null) && (p.isFuncDeclaration().fes.value != null)) && !(!refs && (p.isFuncDeclaration() != null) && ((sc.get()).func.value.isFuncDeclaration().getLevel(p.isFuncDeclaration(), (sc.get()).intypeof.value) > 0)))
                        {
                            if (global.params.vsafe.value)
                            {
                                if (!gag_ref.value)
                                {
                                    error(e.loc.value, new BytePtr("scope variable `%s` may not be returned"), v.toChars());
                                }
                                result.value = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class.value & 65536L) != 0) && (pequals(p, (sc.get()).func.value)))
                    {
                        Type tb = v.type.value.toBasetype();
                        if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!gag_ref.value)
                            {
                                error(e.loc.value, new BytePtr("returning `%s` escapes a reference to variadic parameter `%s`"), e.toChars(), v.toChars());
                            }
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
                Slice<VarDeclaration> __r1296 = er.value.byref.opSlice().copy();
                int __key1297 = 0;
                for (; (__key1297 < __r1296.getLength());__key1297 += 1) {
                    VarDeclaration v = __r1296.get(__key1297);
                    if (false)
                    {
                        printf(new BytePtr("byref `%s`\n"), v.toChars());
                    }
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v) {
                            if (!gag_ref.value)
                            {
                                Ref<BytePtr> msg = ref(null);
                                if ((v.storage_class.value & 32L) != 0)
                                {
                                    msg.value = pcopy(new BytePtr("returning `%s` escapes a reference to parameter `%s`, perhaps annotate with `return`"));
                                }
                                else
                                {
                                    msg.value = pcopy(new BytePtr("returning `%s` escapes a reference to local variable `%s`"));
                                }
                                error(e.loc.value, msg.value, e.toChars(), v.toChars());
                            }
                            result.value = true;
                            return null;
                        }
                    };
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    if (((v.storage_class.value & 2101248L) == 0L))
                    {
                        if ((pequals(p, (sc.get()).func.value)))
                        {
                            escapingRef.invoke(v);
                            continue;
                        }
                        FuncDeclaration fd = p.isFuncDeclaration();
                        if ((fd != null) && (((sc.get()).func.value.flags & FUNCFLAG.returnInprocess) != 0))
                        {
                            if (global.params.vsafe.value)
                            {
                                (sc.get()).func.value.storage_class.value |= 4521191813414912L;
                            }
                        }
                    }
                    if (((v.storage_class.value & 2101248L) != 0) && ((v.storage_class.value & 17592186060800L) == 0))
                    {
                        if ((((sc.get()).func.value.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc.get()).func.value)))
                        {
                            inferReturn((sc.get()).func.value, v);
                        }
                        else if (global.params.useDIP25 && ((sc.get())._module.value != null) && (sc.get())._module.value.isRoot())
                        {
                            if ((pequals(p, (sc.get()).func.value)))
                            {
                                escapingRef.invoke(v);
                                continue;
                            }
                            FuncDeclaration fd = p.isFuncDeclaration();
                            if ((fd != null) && (fd.type.value != null) && ((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                            {
                                TypeFunction tf = (TypeFunction)fd.type.value;
                                if (tf.isref.value)
                                {
                                    if (!gag_ref.value)
                                    {
                                        error(e.loc.value, new BytePtr("escaping reference to outer local variable `%s`"), v.toChars());
                                    }
                                    result.value = true;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            {
                Slice<Expression> __r1298 = er.value.byexp.opSlice().copy();
                int __key1299 = 0;
                for (; (__key1299 < __r1298.getLength());__key1299 += 1) {
                    Expression ee = __r1298.get(__key1299);
                    if (false)
                    {
                        printf(new BytePtr("byexp %s\n"), ee.toChars());
                    }
                    if (!gag_ref.value)
                    {
                        error(ee.loc.value, new BytePtr("escaping reference to stack allocated value returned by `%s`"), ee.toChars());
                    }
                    result.value = true;
                }
            }
            return result.value;
        }
        finally {
        }
    }

    public static void inferReturn(FuncDeclaration fd, VarDeclaration v) {
        v.storage_class.value |= 4521191813414912L;
        TypeFunction tf = (TypeFunction)fd.type.value;
        if ((pequals(v, fd.vthis.value)))
        {
            fd.storage_class.value |= 4521191813414912L;
            if (((tf.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                tf.isreturn.value = true;
                tf.isreturninferred.value = true;
            }
        }
        else
        {
            if (((tf.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                int dim = tf.parameterList.length();
                {
                    int __key1300 = 0;
                    int __limit1301 = dim;
                    for (; (__key1300 < __limit1301);__key1300 += 1) {
                        int i = __key1300;
                        Parameter p = tf.parameterList.get(i);
                        if ((pequals(p.ident.value, v.ident.value)))
                        {
                            p.storageClass.value |= 4521191813414912L;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void escapeByValue(Expression e, Ptr<EscapeByResults> er) {
        // skipping duplicate class EscapeVisitor
        EscapeVisitor v = new EscapeVisitor(er);
        e.accept(v);
    }

    public static void escapeByRef(Expression e, Ptr<EscapeByResults> er) {
        // skipping duplicate class EscapeRefVisitor
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
                        Slice<VarDeclaration> __r1310 = fdp.closureVars.opSlice().copy();
                        int __key1311 = 0;
                        for (; (__key1311 < __r1310.getLength());__key1311 += 1) {
                            VarDeclaration v = __r1310.get(__key1311);
                            {
                                Slice<FuncDeclaration> __r1312 = v.nestedrefs.opSlice().copy();
                                int __key1313 = 0;
                                for (; (__key1313 < __r1312.getLength());__key1313 += 1) {
                                    FuncDeclaration fdv = __r1312.get(__key1313);
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
        v.storage_class.value &= -281474976710657L;
    }

    public static void eliminateMaybeScopes(Slice<VarDeclaration> array) {
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("eliminateMaybeScopes()\n"));
        }
        boolean changes = false;
        do {
            {
                changes = false;
                {
                    Slice<VarDeclaration> __r1314 = array.copy();
                    int __key1315 = 0;
                    for (; (__key1315 < __r1314.getLength());__key1315 += 1) {
                        VarDeclaration va = __r1314.get(__key1315);
                        if (false)
                        {
                            printf(new BytePtr("  va = %s\n"), va.toChars());
                        }
                        if ((va.storage_class.value & 281474977234944L) == 0)
                        {
                            if (va.maybes != null)
                            {
                                {
                                    Slice<VarDeclaration> __r1316 = (va.maybes.get()).opSlice().copy();
                                    int __key1317 = 0;
                                    for (; (__key1317 < __r1316.getLength());__key1317 += 1) {
                                        VarDeclaration v = __r1316.get(__key1317);
                                        if (false)
                                        {
                                            printf(new BytePtr("    v = %s\n"), v.toChars());
                                        }
                                        if ((v.storage_class.value & 281474976710656L) != 0)
                                        {
                                            notMaybeScope(v);
                                            if ((v.storage_class.value & 2101248L) == 0)
                                            {
                                                v.storage_class.value &= -4521191813414913L;
                                            }
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
