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
        private EscapeByResults er;
        public  EscapeVisitor(EscapeByResults er) {
            this.er = er;
        }

        public  void visit(Expression e) {
        }

        public  void visit(AddrExp e) {
            if ((e.e1.op & 0xFF) != 49)
                escapeByRef(e.e1, this.er);
        }

        public  void visit(SymOffExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
                (this.er).byref.push(v);
        }

        public  void visit(VarExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
                (this.er).byvalue.push(v);
        }

        public  void visit(ThisExp e) {
            if (e.var != null)
                (this.er).byvalue.push(e.var);
        }

        public  void visit(DotVarExp e) {
            Type t = e.e1.type.toBasetype();
            if ((t.ty & 0xFF) == ENUMTY.Tstruct)
                e.e1.accept(this);
        }

        public  void visit(DelegateExp e) {
            Type t = e.e1.type.toBasetype();
            if (((t.ty & 0xFF) == ENUMTY.Tclass || (t.ty & 0xFF) == ENUMTY.Tpointer))
                escapeByValue(e.e1, this.er);
            else
                escapeByRef(e.e1, this.er);
            (this.er).byfunc.push(e.func);
        }

        public  void visit(FuncExp e) {
            if ((e.fd.tok & 0xFF) == 160)
                (this.er).byfunc.push(e.fd);
        }

        public  void visit(TupleExp e) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ArrayLiteralExp e) {
            Type tb = e.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tsarray || (tb.ty & 0xFF) == ENUMTY.Tarray))
            {
                if (e.basis != null)
                    e.basis.accept(this);
                {
                    Slice<Expression> __r1302 = (e.elements).opSlice().copy();
                    int __key1303 = 0;
                    for (; __key1303 < __r1302.getLength();__key1303 += 1) {
                        Expression el = __r1302.get(__key1303);
                        if (el != null)
                            el.accept(this);
                    }
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            if (e.elements != null)
            {
                {
                    Slice<Expression> __r1304 = (e.elements).opSlice().copy();
                    int __key1305 = 0;
                    for (; __key1305 < __r1304.getLength();__key1305 += 1) {
                        Expression ex = __r1304.get(__key1305);
                        if (ex != null)
                            ex.accept(this);
                    }
                }
            }
        }

        public  void visit(NewExp e) {
            Type tb = e.newtype.toBasetype();
            if ((((tb.ty & 0xFF) == ENUMTY.Tstruct && !(e.member != null)) && e.arguments != null))
            {
                {
                    Slice<Expression> __r1306 = (e.arguments).opSlice().copy();
                    int __key1307 = 0;
                    for (; __key1307 < __r1306.getLength();__key1307 += 1) {
                        Expression ex = __r1306.get(__key1307);
                        if (ex != null)
                            ex.accept(this);
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            Type tb = e.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray && (e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                escapeByRef(e.e1, this.er);
            }
            else
                e.e1.accept(this);
        }

        public  void visit(SliceExp e) {
            if ((e.e1.op & 0xFF) == 26)
            {
                VarDeclaration v = ((VarExp)e.e1).var.isVarDeclaration();
                Type tb = e.type.toBasetype();
                if (v != null)
                {
                    if ((tb.ty & 0xFF) == ENUMTY.Tsarray)
                        return ;
                    if ((v.storage_class & 65536L) != 0)
                    {
                        (this.er).byvalue.push(v);
                        return ;
                    }
                }
            }
            Type t1b = e.e1.type.toBasetype();
            if ((t1b.ty & 0xFF) == ENUMTY.Tsarray)
            {
                Type tb = e.type.toBasetype();
                if ((tb.ty & 0xFF) != ENUMTY.Tsarray)
                    escapeByRef(e.e1, this.er);
            }
            else
                e.e1.accept(this);
        }

        public  void visit(IndexExp e) {
            if ((e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray)
            {
                e.e1.accept(this);
            }
        }

        public  void visit(BinExp e) {
            Type tb = e.type.toBasetype();
            if ((tb.ty & 0xFF) == ENUMTY.Tpointer)
            {
                e.e1.accept(this);
                e.e2.accept(this);
            }
        }

        public  void visit(BinAssignExp e) {
            e.e1.accept(this);
        }

        public  void visit(AssignExp e) {
            e.e1.accept(this);
        }

        public  void visit(CommaExp e) {
            e.e2.accept(this);
        }

        public  void visit(CondExp e) {
            e.e1.accept(this);
            e.e2.accept(this);
        }

        public  void visit(CallExp e) {
            Type t1 = e.e1.type.toBasetype();
            TypeFunction tf = null;
            TypeDelegate dg = null;
            if ((t1.ty & 0xFF) == ENUMTY.Tdelegate)
            {
                dg = (TypeDelegate)t1;
                tf = (TypeFunction)((TypeDelegate)t1).next;
            }
            else if ((t1.ty & 0xFF) == ENUMTY.Tfunction)
                tf = (TypeFunction)t1;
            else
                return ;
            if ((e.arguments != null && ((e.arguments).length) != 0))
            {
                int j = (((tf.linkage == LINK.d && tf.parameterList.varargs == VarArg.variadic)) ? 1 : 0);
                {
                    int i = j;
                    for (; i < (e.arguments).length;i += 1){
                        Expression arg = (e.arguments).get(i);
                        int nparams = tf.parameterList.length();
                        if ((i - j < nparams && i >= j))
                        {
                            Parameter p = tf.parameterList.get(i - j);
                            long stc = tf.parameterStorageClass(null, p);
                            if (((stc & 524288L) != 0 && (stc & 17592186044416L) != 0))
                                arg.accept(this);
                            else if (((stc & 2097152L) != 0 && (stc & 17592186044416L) != 0))
                                escapeByRef(arg, this.er);
                        }
                    }
                }
            }
            if (((e.e1.op & 0xFF) == 27 && (t1.ty & 0xFF) == ENUMTY.Tfunction))
            {
                DotVarExp dve = (DotVarExp)e.e1;
                FuncDeclaration fd = dve.var.isFuncDeclaration();
                AggregateDeclaration ad = null;
                if ((((global.params.vsafe && tf.isreturn) && fd != null) && (ad = fd.isThis()) != null))
                {
                    if ((ad.isClassDeclaration() != null || tf.isscope))
                        dve.e1.accept(this);
                    else if (ad.isStructDeclaration() != null)
                        escapeByRef(dve.e1, this.er);
                }
                else if (((dve.var.storage_class & 17592186044416L) != 0 || tf.isreturn))
                {
                    if ((dve.var.storage_class & 524288L) != 0)
                        dve.e1.accept(this);
                    else if ((dve.var.storage_class & 2097152L) != 0)
                        escapeByRef(dve.e1, this.er);
                }
                if ((fd != null && fd.isNested()))
                {
                    if ((tf.isreturn && tf.isscope))
                        (this.er).byexp.push(e);
                }
            }
            if (dg != null)
            {
                if (tf.isreturn)
                    e.e1.accept(this);
            }
            if ((e.e1.op & 0xFF) == 26)
            {
                VarExp ve = (VarExp)e.e1;
                FuncDeclaration fd = ve.var.isFuncDeclaration();
                if ((fd != null && fd.isNested()))
                {
                    if ((tf.isreturn && tf.isscope))
                        (this.er).byexp.push(e);
                }
            }
        }

        private Object this;

        public EscapeVisitor() {}

        public EscapeVisitor copy() {
            EscapeVisitor that = new EscapeVisitor();
            that.er = this.er;
            that.this = this.this;
            return that;
        }
    }
    private static class EscapeRefVisitor extends Visitor
    {
        private EscapeByResults er;
        public  EscapeRefVisitor(EscapeByResults er) {
            this.er = er;
        }

        public  void visit(Expression e) {
        }

        public  void visit(VarExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
            {
                if ((((v.storage_class & 2097152L) != 0 && (v.storage_class & 1099511644160L) != 0) && v._init != null))
                {
                    {
                        ExpInitializer ez = v._init.isExpInitializer();
                        if (ez != null)
                        {
                            assert((ez.exp != null && (ez.exp.op & 0xFF) == 95));
                            Expression ex = ((ConstructExp)ez.exp).e2;
                            ex.accept(this);
                        }
                    }
                }
                else
                    (this.er).byref.push(v);
            }
        }

        public  void visit(ThisExp e) {
            if ((e.var != null && e.var.toParent2().isFuncDeclaration().isThis2))
                escapeByValue(e, this.er);
            else if (e.var != null)
                (this.er).byref.push(e.var);
        }

        public  void visit(PtrExp e) {
            escapeByValue(e.e1, this.er);
        }

        public  void visit(IndexExp e) {
            Type tb = e.e1.type.toBasetype();
            if ((e.e1.op & 0xFF) == 26)
            {
                VarDeclaration v = ((VarExp)e.e1).var.isVarDeclaration();
                if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    if ((v != null && (v.storage_class & 65536L) != 0))
                    {
                        (this.er).byref.push(v);
                        return ;
                    }
                }
            }
            if ((tb.ty & 0xFF) == ENUMTY.Tsarray)
            {
                e.e1.accept(this);
            }
            else if ((tb.ty & 0xFF) == ENUMTY.Tarray)
            {
                escapeByValue(e.e1, this.er);
            }
        }

        public  void visit(StructLiteralExp e) {
            if (e.elements != null)
            {
                {
                    Slice<Expression> __r1308 = (e.elements).opSlice().copy();
                    int __key1309 = 0;
                    for (; __key1309 < __r1308.getLength();__key1309 += 1) {
                        Expression ex = __r1308.get(__key1309);
                        if (ex != null)
                            ex.accept(this);
                    }
                }
            }
            (this.er).byexp.push(e);
        }

        public  void visit(DotVarExp e) {
            Type t1b = e.e1.type.toBasetype();
            if ((t1b.ty & 0xFF) == ENUMTY.Tclass)
                escapeByValue(e.e1, this.er);
            else
                e.e1.accept(this);
        }

        public  void visit(BinAssignExp e) {
            e.e1.accept(this);
        }

        public  void visit(AssignExp e) {
            e.e1.accept(this);
        }

        public  void visit(CommaExp e) {
            e.e2.accept(this);
        }

        public  void visit(CondExp e) {
            e.e1.accept(this);
            e.e2.accept(this);
        }

        public  void visit(CallExp e) {
            Type t1 = e.e1.type.toBasetype();
            TypeFunction tf = null;
            if ((t1.ty & 0xFF) == ENUMTY.Tdelegate)
                tf = (TypeFunction)((TypeDelegate)t1).next;
            else if ((t1.ty & 0xFF) == ENUMTY.Tfunction)
                tf = (TypeFunction)t1;
            else
                return ;
            if (tf.isref)
            {
                if ((e.arguments != null && ((e.arguments).length) != 0))
                {
                    int j = (((tf.linkage == LINK.d && tf.parameterList.varargs == VarArg.variadic)) ? 1 : 0);
                    {
                        int i = j;
                        for (; i < (e.arguments).length;i += 1){
                            Expression arg = (e.arguments).get(i);
                            int nparams = tf.parameterList.length();
                            if ((i - j < nparams && i >= j))
                            {
                                Parameter p = tf.parameterList.get(i - j);
                                long stc = tf.parameterStorageClass(null, p);
                                if (((stc & 2101248L) != 0 && (stc & 17592186044416L) != 0))
                                    arg.accept(this);
                                else if (((stc & 524288L) != 0 && (stc & 17592186044416L) != 0))
                                {
                                    if ((arg.op & 0xFF) == 160)
                                    {
                                        DelegateExp de = (DelegateExp)arg;
                                        if (de.func.isNested())
                                            (this.er).byexp.push(de);
                                    }
                                    else
                                        escapeByValue(arg, this.er);
                                }
                            }
                        }
                    }
                }
                if (((e.e1.op & 0xFF) == 27 && (t1.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    DotVarExp dve = (DotVarExp)e.e1;
                    if (((dve.var.storage_class & 17592186044416L) != 0 || tf.isreturn))
                    {
                        if (((dve.var.storage_class & 524288L) != 0 || tf.isscope))
                            escapeByValue(dve.e1, this.er);
                        else if (((dve.var.storage_class & 2097152L) != 0 || tf.isref))
                            dve.e1.accept(this);
                    }
                    FuncDeclaration fd = dve.var.isFuncDeclaration();
                    if ((fd != null && fd.isNested()))
                    {
                        if (tf.isreturn)
                            (this.er).byexp.push(e);
                    }
                }
                if (((e.e1.op & 0xFF) == 26 && (t1.ty & 0xFF) == ENUMTY.Tdelegate))
                {
                    escapeByValue(e.e1, this.er);
                }
                if ((e.e1.op & 0xFF) == 26)
                {
                    VarExp ve = (VarExp)e.e1;
                    FuncDeclaration fd = ve.var.isFuncDeclaration();
                    if ((fd != null && fd.isNested()))
                    {
                        if (tf.isreturn)
                            (this.er).byexp.push(e);
                    }
                }
            }
            else
                (this.er).byexp.push(e);
        }

        private Object this;

        public EscapeRefVisitor() {}

        public EscapeRefVisitor copy() {
            EscapeRefVisitor that = new EscapeRefVisitor();
            that.er = this.er;
            that.this = this.this;
            return that;
        }
    }

    public static boolean checkArrayLiteralEscape(Scope sc, ArrayLiteralExp ae, boolean gag) {
        boolean errors = false;
        if (ae.basis != null)
            errors = checkNewEscape(sc, ae.basis, gag);
        {
            Slice<Expression> __r1258 = (ae.elements).opSlice().copy();
            int __key1259 = 0;
            for (; __key1259 < __r1258.getLength();__key1259 += 1) {
                Expression ex = __r1258.get(__key1259);
                if (ex != null)
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
            }
        }
        return errors;
    }

    public static boolean checkAssocArrayLiteralEscape(Scope sc, AssocArrayLiteralExp ae, boolean gag) {
        boolean errors = false;
        {
            Slice<Expression> __r1260 = (ae.keys).opSlice().copy();
            int __key1261 = 0;
            for (; __key1261 < __r1260.getLength();__key1261 += 1) {
                Expression ex = __r1260.get(__key1261);
                if (ex != null)
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
            }
        }
        {
            Slice<Expression> __r1262 = (ae.values).opSlice().copy();
            int __key1263 = 0;
            for (; __key1263 < __r1262.getLength();__key1263 += 1) {
                Expression ex = __r1262.get(__key1263);
                if (ex != null)
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
            }
        }
        return errors;
    }

    public static boolean checkParamArgumentEscape(Scope sc, FuncDeclaration fdc, Parameter par, Expression arg, boolean gag) {
        Ref<Scope> sc_ref = ref(sc);
        Ref<FuncDeclaration> fdc_ref = ref(fdc);
        Ref<Parameter> par_ref = ref(par);
        Ref<Expression> arg_ref = ref(arg);
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
            printf( new ByteSlice("checkParamArgumentEscape(arg: %s par: %s)\n"), arg_ref.value != null ? arg_ref.value.toChars() :  new ByteSlice("null"), par_ref.value != null ? par_ref.value.toChars() :  new ByteSlice("this"));
        if (!(arg_ref.value.type.hasPointers()))
            return false;
        EscapeByResults er = new EscapeByResults();
        try {
            escapeByValue(arg_ref.value, er);
            if ((((!((er.byref.length) != 0) && !((er.byvalue.length) != 0)) && !((er.byfunc.length) != 0)) && !((er.byexp.length) != 0)))
                return false;
            Ref<Boolean> result = ref(false);
            Function2<VarDeclaration,BytePtr,Void> unsafeAssign = new Function2<VarDeclaration,BytePtr,Void>(){
                public Void invoke(VarDeclaration v, BytePtr desc){
                    if ((global.params.vsafe && (sc_ref.value).func.setUnsafe()))
                    {
                        if (!(gag_ref.value))
                            error(arg_ref.value.loc, new BytePtr("%s `%s` assigned to non-scope parameter `%s` calling %s"), desc, v.toChars(), par_ref.value != null ? par_ref.value.toChars() : new BytePtr("this"), fdc_ref.value != null ? fdc_ref.value.toPrettyChars(false) : new BytePtr("indirectly"));
                        result.value = true;
                    }
                    return null;
                }
            };
            {
                Slice<VarDeclaration> __r1264 = er.byvalue.opSlice().copy();
                int __key1265 = 0;
                for (; __key1265 < __r1264.getLength();__key1265 += 1) {
                    VarDeclaration v = __r1264.get(__key1265);
                    if (false)
                        printf( new ByteSlice("byvalue %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    notMaybeScope(v);
                    if (v.isScope())
                    {
                        unsafeAssign.invoke(v, new BytePtr("scope variable"));
                    }
                    else if (((v.storage_class & 65536L) != 0 && pequals(p, (sc_ref.value).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            unsafeAssign.invoke(v, new BytePtr("variadic variable"));
                        }
                    }
                    else
                    {
                        if (false)
                            printf( new ByteSlice("no infer for %s in %s loc %s, fdc %s, %d\n"), v.toChars(), (sc_ref.value).func.ident.toChars(), (sc_ref.value).func.loc.toChars(global.params.showColumns), fdc_ref.value.ident.toChars(), 162);
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1266 = er.byref.opSlice().copy();
                int __key1267 = 0;
                for (; __key1267 < __r1266.getLength();__key1267 += 1) {
                    VarDeclaration v = __r1266.get(__key1267);
                    if (false)
                        printf( new ByteSlice("byref %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    notMaybeScope(v);
                    if (((v.storage_class & 2101248L) == 0L && pequals(p, (sc_ref.value).func)))
                    {
                        if ((par_ref.value != null && (par_ref.value.storageClass & 17592186568704L) == 524288L))
                            continue;
                        unsafeAssign.invoke(v, new BytePtr("reference to local variable"));
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1268 = er.byfunc.opSlice().copy();
                int __key1269 = 0;
                for (; __key1269 < __r1268.getLength();__key1269 += 1) {
                    FuncDeclaration fd = __r1268.get(__key1269);
                    DArray<VarDeclaration> vars = new DArray<VarDeclaration>();
                    try {
                        findAllOuterAccessedVariables(fd, vars);
                        {
                            Slice<VarDeclaration> __r1270 = vars.opSlice().copy();
                            int __key1271 = 0;
                            for (; __key1271 < __r1270.getLength();__key1271 += 1) {
                                VarDeclaration v = __r1270.get(__key1271);
                                assert(!(v.isDataseg()));
                                Dsymbol p = v.toParent2();
                                notMaybeScope(v);
                                if (((v.storage_class & 2625536L) != 0 && pequals(p, (sc_ref.value).func)))
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
                Slice<Expression> __r1272 = er.byexp.opSlice().copy();
                int __key1273 = 0;
                for (; __key1273 < __r1272.getLength();__key1273 += 1) {
                    Expression ee = __r1272.get(__key1273);
                    if ((sc_ref.value).func.setUnsafe())
                    {
                        if (!(gag_ref.value))
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

    public static boolean checkParamArgumentReturn(Scope sc, Expression firstArg, Expression arg, boolean gag) {
        boolean log = false;
        if (false)
            printf( new ByteSlice("checkParamArgumentReturn(firstArg: %s arg: %s)\n"), firstArg.toChars(), arg.toChars());
        if (!(arg.type.hasPointers()))
            return false;
        AssignExp e = new AssignExp(arg.loc, firstArg, arg);
        return checkAssignEscape(sc, e, gag);
    }

    public static boolean checkConstructorEscape(Scope sc, CallExp ce, boolean gag) {
        boolean log = false;
        if (false)
            printf( new ByteSlice("checkConstructorEscape(%s, %s)\n"), ce.toChars(), ce.type.toChars());
        Type tthis = ce.type.toBasetype();
        assert((tthis.ty & 0xFF) == ENUMTY.Tstruct);
        if (!(tthis.hasPointers()))
            return false;
        if ((ce.arguments == null && ((ce.arguments).length) != 0))
            return false;
        assert((ce.e1.op & 0xFF) == 27);
        DotVarExp dve = (DotVarExp)ce.e1;
        CtorDeclaration ctor = dve.var.isCtorDeclaration();
        assert(ctor != null);
        assert((ctor.type.ty & 0xFF) == ENUMTY.Tfunction);
        TypeFunction tf = (TypeFunction)ctor.type;
        int nparams = tf.parameterList.length();
        int n = (ce.arguments).length;
        boolean j = (tf.linkage == LINK.d && tf.parameterList.varargs == VarArg.variadic);
        {
            int __key1274 = 0;
            int __limit1275 = n;
            for (; __key1274 < __limit1275;__key1274 += 1) {
                int i = __key1274;
                Expression arg = (ce.arguments).get(i);
                if (!(arg.type.hasPointers()))
                    return false;
                if ((i - (j ? 1 : 0) < nparams && i >= (j ? 1 : 0)))
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

    public static boolean checkAssignEscape(Scope sc, Expression e, boolean gag) {
        Ref<Scope> sc_ref = ref(sc);
        boolean log = false;
        if (false)
            printf( new ByteSlice("checkAssignEscape(e: %s)\n"), e.toChars());
        if (((((((e.op & 0xFF) != 90 && (e.op & 0xFF) != 96) && (e.op & 0xFF) != 95) && (e.op & 0xFF) != 71) && (e.op & 0xFF) != 72) && (e.op & 0xFF) != 73))
            return false;
        BinExp ae = (BinExp)e;
        Expression e1 = ae.e1;
        Expression e2 = ae.e2;
        if (!(e1.type.hasPointers()))
            return false;
        if ((e1.op & 0xFF) == 31)
            return false;
        if ((e1.op & 0xFF) == 49)
            return false;
        EscapeByResults er = new EscapeByResults();
        try {
            escapeByValue(e2, er);
            if ((((!((er.byref.length) != 0) && !((er.byvalue.length) != 0)) && !((er.byfunc.length) != 0)) && !((er.byexp.length) != 0)))
                return false;
            Ref<VarDeclaration> va = ref(expToVariable(e1));
            if ((va.value != null && (e.op & 0xFF) == 72))
            {
                va.value = null;
            }
            if (((va.value != null && (e1.op & 0xFF) == 27) && (va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass))
            {
                va.value = null;
            }
            if (false)
                printf( new ByteSlice("va: %s\n"), va.value.toChars());
            boolean inferScope = false;
            if ((((va.value != null && (sc_ref.value).func != null) && (sc_ref.value).func.type != null) && ((sc_ref.value).func.type.ty & 0xFF) == ENUMTY.Tfunction))
                inferScope = ((TypeFunction)(sc_ref.value).func.type).trust != TRUST.system;
            Ref<Boolean> vaIsRef = ref(((va.value != null && (va.value.storage_class & 32L) != 0) && ((va.value.storage_class & 2101248L) != 0 || (va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass)));
            if (false)
                printf( new ByteSlice("va is ref `%s`\n"), va.value.toChars());
            Function0<Boolean> isFirstRef = new Function0<Boolean>(){
                public Boolean invoke(){
                    if (!(vaIsRef.value))
                        return false;
                    Dsymbol p = va.value.toParent2();
                    FuncDeclaration fd = (sc_ref.value).func;
                    if (((pequals(p, fd) && fd.type != null) && (fd.type.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        TypeFunction tf = (TypeFunction)fd.type;
                        if ((!(tf.nextOf() != null) || ((tf.nextOf().ty & 0xFF) != ENUMTY.Tvoid && !(fd.isCtorDeclaration() != null))))
                            return false;
                        if (pequals(va.value, fd.vthis))
                            return true;
                        if (((fd.parameters != null && ((fd.parameters).length) != 0) && pequals((fd.parameters).get(0), va.value)))
                            return true;
                    }
                    return false;
                }
            };
            boolean vaIsFirstRef = isFirstRef.invoke();
            if (false)
                printf( new ByteSlice("va is first ref `%s`\n"), va.value.toChars());
            boolean result = false;
            {
                Slice<VarDeclaration> __r1276 = er.byvalue.opSlice().copy();
                int __key1277 = 0;
                for (; __key1277 < __r1276.getLength();__key1277 += 1) {
                    VarDeclaration v = __r1276.get(__key1277);
                    if (false)
                        printf( new ByteSlice("byvalue: %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    if (pequals(v, va.value))
                        continue;
                    Dsymbol p = v.toParent2();
                    if ((((((va.value != null && !(vaIsRef.value)) && !(va.value.isScope())) && !(v.isScope())) && (va.value.storage_class & v.storage_class & 281474976776192L) == 281474976710656L) && pequals(p, (sc_ref.value).func)))
                    {
                        va.value.addMaybe(v);
                        continue;
                    }
                    if ((((((vaIsFirstRef && (v.isScope() || (v.storage_class & 281474976710656L) != 0)) && !((v.storage_class & 17592186044416L) != 0)) && v.isParameter()) && ((sc_ref.value).func.flags & FUNCFLAG.returnInprocess) != 0) && pequals(p, (sc_ref.value).func)))
                    {
                        if (false)
                            printf( new ByteSlice("inferring 'return' for parameter %s in function %s\n"), v.toChars(), (sc_ref.value).func.toChars());
                        inferReturn((sc_ref.value).func, v);
                    }
                    if ((!((va.value != null && va.value.isScope())) || vaIsRef.value))
                        notMaybeScope(v);
                    if (v.isScope())
                    {
                        if (((vaIsFirstRef && v.isParameter()) && (v.storage_class & 17592186044416L) != 0))
                        {
                            if (va.value.isScope())
                                continue;
                            if ((inferScope && !(va.value.doNotInferScope)))
                            {
                                if (false)
                                    printf( new ByteSlice("inferring scope for lvalue %s\n"), va.value.toChars());
                                va.value.storage_class |= 562949953945600L;
                                continue;
                            }
                        }
                        if (((((va.value != null && va.value.isScope()) && (va.value.storage_class & 17592186044416L) != 0) && !((v.storage_class & 17592186044416L) != 0)) && (sc_ref.value).func.setUnsafe()))
                        {
                            if (!(gag))
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to return scope `%s`"), v.toChars(), va.value.toChars());
                            result = true;
                            continue;
                        }
                        if (((va.value != null && ((((va.value.enclosesLifetimeOf(v) && !((v.storage_class & 1099511627808L) != 0)) || (((ae.e1.op & 0xFF) == 27 && (va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass) && (va.value.enclosesLifetimeOf(v) || !(va.value.isScope())))) || vaIsRef.value) || ((va.value.storage_class & 2101248L) != 0 && !((v.storage_class & 1099511627808L) != 0)))) && (sc_ref.value).func.setUnsafe()))
                        {
                            if (!(gag))
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                            result = true;
                            continue;
                        }
                        if (((va.value != null && !(va.value.isDataseg())) && !(va.value.doNotInferScope)))
                        {
                            if ((!(va.value.isScope()) && inferScope))
                            {
                                va.value.storage_class |= 562949953945600L;
                                if (((v.storage_class & 17592186044416L) != 0 && !((va.value.storage_class & 17592186044416L) != 0)))
                                {
                                    va.value.storage_class |= 4521191813414912L;
                                }
                            }
                            continue;
                        }
                        if ((sc_ref.value).func.setUnsafe())
                        {
                            if (!(gag))
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            result = true;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0 && pequals(p, (sc_ref.value).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (((va.value != null && !(va.value.isDataseg())) && !(va.value.doNotInferScope)))
                            {
                                if ((!(va.value.isScope()) && inferScope))
                                {
                                    va.value.storage_class |= 562949953945600L;
                                }
                                continue;
                            }
                            if ((sc_ref.value).func.setUnsafe())
                            {
                                if (!(gag))
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
                Slice<VarDeclaration> __r1278 = er.byref.opSlice().copy();
                int __key1279 = 0;
                for (; __key1279 < __r1278.getLength();__key1279 += 1) {
                    VarDeclaration v = __r1278.get(__key1279);
                    if (false)
                        printf( new ByteSlice("byref: %s\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    if (global.params.vsafe)
                    {
                        if (((((va.value != null && va.value.isScope()) && (va.value.storage_class & 17592186044416L) != 0) && (v.storage_class & 2101248L) == 0L) && (sc_ref.value).func.setUnsafe()))
                        {
                            if (!(gag))
                                error(ae.loc, new BytePtr("address of local variable `%s` assigned to return scope `%s`"), v.toChars(), va.value.toChars());
                            result = true;
                            continue;
                        }
                    }
                    Dsymbol p = v.toParent2();
                    if (((va.value != null && (((va.value.enclosesLifetimeOf(v) && !((v.storage_class & 32L) != 0)) || (va.value.storage_class & 2097152L) != 0) || va.value.isDataseg())) && (sc_ref.value).func.setUnsafe()))
                    {
                        if (!(gag))
                            error(ae.loc, new BytePtr("address of variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                        result = true;
                        continue;
                    }
                    if ((va.value != null && (v.storage_class & 2101248L) != 0))
                    {
                        Dsymbol pva = va.value.toParent2();
                        {
                            Dsymbol pv = p;
                            for (; pv != null;){
                                pv = pv.toParent2();
                                if (pequals(pva, pv))
                                {
                                    if ((sc_ref.value).func.setUnsafe())
                                    {
                                        if (!(gag))
                                            error(ae.loc, new BytePtr("reference `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.value.toChars());
                                        result = true;
                                        continue ByRef;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (!((va.value != null && va.value.isScope())))
                        notMaybeScope(v);
                    if (((v.storage_class & 2101248L) == 0L && pequals(p, (sc_ref.value).func)))
                    {
                        if (((va.value != null && !(va.value.isDataseg())) && !(va.value.doNotInferScope)))
                        {
                            if ((!(va.value.isScope()) && inferScope))
                            {
                                va.value.storage_class |= 562949953945600L;
                            }
                            continue;
                        }
                        if ((e1.op & 0xFF) == 49)
                            continue;
                        if ((sc_ref.value).func.setUnsafe())
                        {
                            if (!(gag))
                                error(ae.loc, new BytePtr("reference to local variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            result = true;
                        }
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1280 = er.byfunc.opSlice().copy();
                int __key1281 = 0;
                for (; __key1281 < __r1280.getLength();__key1281 += 1) {
                    FuncDeclaration fd = __r1280.get(__key1281);
                    if (false)
                        printf( new ByteSlice("byfunc: %s, %d\n"), fd.toChars(), fd.tookAddressOf);
                    DArray<VarDeclaration> vars = new DArray<VarDeclaration>();
                    try {
                        findAllOuterAccessedVariables(fd, vars);
                        if ((((va.value != null && va.value.isScope()) && (fd.tookAddressOf) != 0) && global.params.vsafe))
                            fd.tookAddressOf -= 1;
                        {
                            Slice<VarDeclaration> __r1282 = vars.opSlice().copy();
                            int __key1283 = 0;
                            for (; __key1283 < __r1282.getLength();__key1283 += 1) {
                                VarDeclaration v = __r1282.get(__key1283);
                                assert(!(v.isDataseg()));
                                Dsymbol p = v.toParent2();
                                if (!((va.value != null && va.value.isScope())))
                                    notMaybeScope(v);
                                if (((v.storage_class & 2625536L) != 0 && pequals(p, (sc_ref.value).func)))
                                {
                                    if (((va.value != null && !(va.value.isDataseg())) && !(va.value.doNotInferScope)))
                                    {
                                        continue;
                                    }
                                    if ((sc_ref.value).func.setUnsafe())
                                    {
                                        if (!(gag))
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
                Slice<Expression> __r1284 = er.byexp.opSlice().copy();
                int __key1285 = 0;
                for (; __key1285 < __r1284.getLength();__key1285 += 1) {
                    Expression ee = __r1284.get(__key1285);
                    if (false)
                        printf( new ByteSlice("byexp: %s\n"), ee.toChars());
                    if (((((va.value != null && (ee.op & 0xFF) == 18) && (ee.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) && (va.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray) && !((va.value.storage_class & 1099511627776L) != 0)))
                    {
                        if (!(gag))
                            deprecation(ee.loc, new BytePtr("slice of static array temporary returned by `%s` assigned to longer lived variable `%s`"), ee.toChars(), va.value.toChars());
                        continue;
                    }
                    if (((va.value != null && !(va.value.isDataseg())) && !(va.value.doNotInferScope)))
                    {
                        if ((!(va.value.isScope()) && inferScope))
                        {
                            va.value.storage_class |= 562949953945600L;
                        }
                        continue;
                    }
                    if ((sc_ref.value).func.setUnsafe())
                    {
                        if (!(gag))
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

    public static boolean checkThrowEscape(Scope sc, Expression e, boolean gag) {
        EscapeByResults er = new EscapeByResults();
        try {
            escapeByValue(e, er);
            if (((!((er.byref.length) != 0) && !((er.byvalue.length) != 0)) && !((er.byexp.length) != 0)))
                return false;
            boolean result = false;
            {
                Slice<VarDeclaration> __r1286 = er.byvalue.opSlice().copy();
                int __key1287 = 0;
                for (; __key1287 < __r1286.getLength();__key1287 += 1) {
                    VarDeclaration v = __r1286.get(__key1287);
                    if (v.isDataseg())
                        continue;
                    if ((v.isScope() && !(v.iscatchvar)))
                    {
                        if (((sc)._module != null && (sc)._module.isRoot()))
                        {
                            if (global.params.vsafe)
                            {
                                if (!(gag))
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

    public static boolean checkNewEscape(Scope sc, Expression e, boolean gag) {
        Ref<Expression> e_ref = ref(e);
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
            printf( new ByteSlice("[%s] checkNewEscape, e: `%s`\n"), e_ref.value.loc.toChars(global.params.showColumns), e_ref.value.toChars());
        EscapeByResults er = new EscapeByResults();
        try {
            escapeByValue(e_ref.value, er);
            if (((!((er.byref.length) != 0) && !((er.byvalue.length) != 0)) && !((er.byexp.length) != 0)))
                return false;
            Ref<Boolean> result = ref(false);
            {
                Slice<VarDeclaration> __r1288 = er.byvalue.opSlice().copy();
                int __key1289 = 0;
                for (; __key1289 < __r1288.getLength();__key1289 += 1) {
                    VarDeclaration v = __r1288.get(__key1289);
                    if (false)
                        printf( new ByteSlice("byvalue `%s`\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if (v.isScope())
                    {
                        if ((((sc)._module != null && (sc)._module.isRoot()) && !(pequals(p.parent, (sc).func))))
                        {
                            if (global.params.vsafe)
                            {
                                if (!(gag_ref.value))
                                    error(e_ref.value.loc, new BytePtr("scope variable `%s` may not be copied into allocated memory"), v.toChars());
                                result.value = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0 && pequals(p, (sc).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!(gag_ref.value))
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
                Slice<VarDeclaration> __r1290 = er.byref.opSlice().copy();
                int __key1291 = 0;
                for (; __key1291 < __r1290.getLength();__key1291 += 1) {
                    VarDeclaration v = __r1290.get(__key1291);
                    if (false)
                        printf( new ByteSlice("byref `%s`\n"), v.toChars());
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v){
                            if (!(gag_ref.value))
                            {
                                BytePtr kind = pcopy((v.storage_class & 32L) != 0 ? new BytePtr("parameter") : new BytePtr("local"));
                                error(e_ref.value.loc, new BytePtr("copying `%s` into allocated memory escapes a reference to %s variable `%s`"), e_ref.value.toChars(), kind, v.toChars());
                            }
                            result.value = true;
                            return null;
                        }
                    };
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if ((v.storage_class & 2101248L) == 0L)
                    {
                        if (pequals(p, (sc).func))
                        {
                            escapingRef.invoke(v);
                            continue;
                        }
                    }
                    if ((v.storage_class & 2101248L) != 0)
                    {
                        if (((global.params.useDIP25 && (sc)._module != null) && (sc)._module.isRoot()))
                        {
                            if (pequals(p, (sc).func))
                            {
                                escapingRef.invoke(v);
                                continue;
                            }
                            FuncDeclaration fd = p.isFuncDeclaration();
                            if (((fd != null && fd.type != null) && (fd.type.ty & 0xFF) == ENUMTY.Tfunction))
                            {
                                TypeFunction tf = (TypeFunction)fd.type;
                                if (tf.isref)
                                {
                                    if (!(gag_ref.value))
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
                Slice<Expression> __r1292 = er.byexp.opSlice().copy();
                int __key1293 = 0;
                for (; __key1293 < __r1292.getLength();__key1293 += 1) {
                    Expression ee = __r1292.get(__key1293);
                    if (false)
                        printf( new ByteSlice("byexp %s\n"), ee.toChars());
                    if (!(gag_ref.value))
                        error(ee.loc, new BytePtr("storing reference to stack allocated value returned by `%s` into allocated memory causes it to escape"), ee.toChars());
                    result.value = true;
                }
            }
            return result.value;
        }
        finally {
        }
    }

    public static boolean checkReturnEscape(Scope sc, Expression e, boolean gag) {
        return checkReturnEscapeImpl(sc, e, false, gag);
    }

    public static boolean checkReturnEscapeRef(Scope sc, Expression e, boolean gag) {
        return checkReturnEscapeImpl(sc, e, true, gag);
    }

    public static boolean checkReturnEscapeImpl(Scope sc, Expression e, boolean refs, boolean gag) {
        Ref<Expression> e_ref = ref(e);
        Ref<Boolean> gag_ref = ref(gag);
        boolean log = false;
        if (false)
            printf( new ByteSlice("[%s] checkReturnEscapeImpl, refs: %d e: `%s`\n"), e_ref.value.loc.toChars(global.params.showColumns), (refs ? 1 : 0), e_ref.value.toChars());
        EscapeByResults er = new EscapeByResults();
        try {
            if (refs)
                escapeByRef(e_ref.value, er);
            else
                escapeByValue(e_ref.value, er);
            if (((!((er.byref.length) != 0) && !((er.byvalue.length) != 0)) && !((er.byexp.length) != 0)))
                return false;
            Ref<Boolean> result = ref(false);
            {
                Slice<VarDeclaration> __r1294 = er.byvalue.opSlice().copy();
                int __key1295 = 0;
                for (; __key1295 < __r1294.getLength();__key1295 += 1) {
                    VarDeclaration v = __r1294.get(__key1295);
                    if (false)
                        printf( new ByteSlice("byvalue `%s`\n"), v.toChars());
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if ((((((v.isScope() || (v.storage_class & 281474976710656L) != 0) && !((v.storage_class & 17592186044416L) != 0)) && v.isParameter()) && ((sc).func.flags & FUNCFLAG.returnInprocess) != 0) && pequals(p, (sc).func)))
                    {
                        inferReturn((sc).func, v);
                        continue;
                    }
                    if (v.isScope())
                    {
                        if ((v.storage_class & 17592186044416L) != 0)
                            continue;
                        if (((((sc)._module != null && (sc)._module.isRoot()) && !((((!(refs) && pequals(p.parent, (sc).func)) && p.isFuncDeclaration() != null) && p.isFuncDeclaration().fes != null))) && !(((!(refs) && p.isFuncDeclaration() != null) && (sc).func.isFuncDeclaration().getLevel(p.isFuncDeclaration(), (sc).intypeof) > 0))))
                        {
                            if (global.params.vsafe)
                            {
                                if (!(gag_ref.value))
                                    error(e_ref.value.loc, new BytePtr("scope variable `%s` may not be returned"), v.toChars());
                                result.value = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0 && pequals(p, (sc).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!(gag_ref.value))
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
                Slice<VarDeclaration> __r1296 = er.byref.opSlice().copy();
                int __key1297 = 0;
                for (; __key1297 < __r1296.getLength();__key1297 += 1) {
                    VarDeclaration v = __r1296.get(__key1297);
                    if (false)
                        printf( new ByteSlice("byref `%s`\n"), v.toChars());
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v){
                            if (!(gag_ref.value))
                            {
                                BytePtr msg = null;
                                if ((v.storage_class & 32L) != 0)
                                    msg = pcopy(new BytePtr("returning `%s` escapes a reference to parameter `%s`, perhaps annotate with `return`"));
                                else
                                    msg = pcopy(new BytePtr("returning `%s` escapes a reference to local variable `%s`"));
                                error(e_ref.value.loc, msg, e_ref.value.toChars(), v.toChars());
                            }
                            result.value = true;
                            return null;
                        }
                    };
                    if (v.isDataseg())
                        continue;
                    Dsymbol p = v.toParent2();
                    if ((v.storage_class & 2101248L) == 0L)
                    {
                        if (pequals(p, (sc).func))
                        {
                            escapingRef.invoke(v);
                            continue;
                        }
                        FuncDeclaration fd = p.isFuncDeclaration();
                        if ((fd != null && ((sc).func.flags & FUNCFLAG.returnInprocess) != 0))
                        {
                            if (global.params.vsafe)
                            {
                                (sc).func.storage_class |= 4521191813414912L;
                            }
                        }
                    }
                    if (((v.storage_class & 2101248L) != 0 && !((v.storage_class & 17592186060800L) != 0)))
                    {
                        if ((((sc).func.flags & FUNCFLAG.returnInprocess) != 0 && pequals(p, (sc).func)))
                        {
                            inferReturn((sc).func, v);
                        }
                        else if (((global.params.useDIP25 && (sc)._module != null) && (sc)._module.isRoot()))
                        {
                            if (pequals(p, (sc).func))
                            {
                                escapingRef.invoke(v);
                                continue;
                            }
                            FuncDeclaration fd = p.isFuncDeclaration();
                            if (((fd != null && fd.type != null) && (fd.type.ty & 0xFF) == ENUMTY.Tfunction))
                            {
                                TypeFunction tf = (TypeFunction)fd.type;
                                if (tf.isref)
                                {
                                    if (!(gag_ref.value))
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
                Slice<Expression> __r1298 = er.byexp.opSlice().copy();
                int __key1299 = 0;
                for (; __key1299 < __r1298.getLength();__key1299 += 1) {
                    Expression ee = __r1298.get(__key1299);
                    if (false)
                        printf( new ByteSlice("byexp %s\n"), ee.toChars());
                    if (!(gag_ref.value))
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
        if (pequals(v, fd.vthis))
        {
            fd.storage_class |= 4521191813414912L;
            if ((tf.ty & 0xFF) == ENUMTY.Tfunction)
            {
                tf.isreturn = true;
                tf.isreturninferred = true;
            }
        }
        else
        {
            if ((tf.ty & 0xFF) == ENUMTY.Tfunction)
            {
                int dim = tf.parameterList.length();
                {
                    int __key1300 = 0;
                    int __limit1301 = dim;
                    for (; __key1300 < __limit1301;__key1300 += 1) {
                        int i = __key1300;
                        Parameter p = tf.parameterList.get(i);
                        if (pequals(p.ident, v.ident))
                        {
                            p.storageClass |= 4521191813414912L;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void escapeByValue(Expression e, EscapeByResults er) {
        EscapeVisitor v = new EscapeVisitor(er);
        e.accept(v);
    }

    public static void escapeByRef(Expression e, EscapeByResults er) {
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
    public static void findAllOuterAccessedVariables(FuncDeclaration fd, DArray<VarDeclaration> vars) {
        {
            Dsymbol p = fd.parent;
            for (; p != null;p = p.parent){
                FuncDeclaration fdp = p.isFuncDeclaration();
                if (fdp != null)
                {
                    {
                        Slice<VarDeclaration> __r1310 = fdp.closureVars.opSlice().copy();
                        int __key1311 = 0;
                        for (; __key1311 < __r1310.getLength();__key1311 += 1) {
                            VarDeclaration v = __r1310.get(__key1311);
                            {
                                Slice<FuncDeclaration> __r1312 = v.nestedrefs.opSlice().copy();
                                int __key1313 = 0;
                                for (; __key1313 < __r1312.getLength();__key1313 += 1) {
                                    FuncDeclaration fdv = __r1312.get(__key1313);
                                    if (pequals(fdv, fd))
                                    {
                                        (vars).push(v);
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
            printf( new ByteSlice("eliminateMaybeScopes()\n"));
        boolean changes = false;
        do {
            {
                changes = false;
                {
                    Slice<VarDeclaration> __r1314 = array.copy();
                    int __key1315 = 0;
                    for (; __key1315 < __r1314.getLength();__key1315 += 1) {
                        VarDeclaration va = __r1314.get(__key1315);
                        if (false)
                            printf( new ByteSlice("  va = %s\n"), va.toChars());
                        if (!((va.storage_class & 281474977234944L) != 0))
                        {
                            if (va.maybes != null)
                            {
                                {
                                    Slice<VarDeclaration> __r1316 = (va.maybes).opSlice().copy();
                                    int __key1317 = 0;
                                    for (; __key1317 < __r1316.getLength();__key1317 += 1) {
                                        VarDeclaration v = __r1316.get(__key1317);
                                        if (false)
                                            printf( new ByteSlice("    v = %s\n"), v.toChars());
                                        if ((v.storage_class & 281474976710656L) != 0)
                                        {
                                            notMaybeScope(v);
                                            if (!((v.storage_class & 2101248L) != 0))
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
