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
            this.er = er;
        }

        public  void visit(Expression e) {
        }

        public  void visit(AddrExp e) {
            if (((e.e1.value.op & 0xFF) != 49))
            {
                escapeByRef(e.e1.value, this.er);
            }
        }

        public  void visit(SymOffExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
            {
                (this.er.get()).byref.push(v);
            }
        }

        public  void visit(VarExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
            {
                (this.er.get()).byvalue.push(v);
            }
        }

        public  void visit(ThisExp e) {
            if (e.var != null)
            {
                (this.er.get()).byvalue.push(e.var);
            }
        }

        public  void visit(DotVarExp e) {
            Type t = e.e1.value.type.value.toBasetype();
            if (((t.ty & 0xFF) == ENUMTY.Tstruct))
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(DelegateExp e) {
            Type t = e.e1.value.type.value.toBasetype();
            if (((t.ty & 0xFF) == ENUMTY.Tclass) || ((t.ty & 0xFF) == ENUMTY.Tpointer))
            {
                escapeByValue(e.e1.value, this.er);
            }
            else
            {
                escapeByRef(e.e1.value, this.er);
            }
            (this.er.get()).byfunc.push(e.func);
        }

        public  void visit(FuncExp e) {
            if (((e.fd.tok & 0xFF) == 160))
            {
                (this.er.get()).byfunc.push(e.fd);
            }
        }

        public  void visit(TupleExp e) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ArrayLiteralExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tsarray) || ((tb.ty & 0xFF) == ENUMTY.Tarray))
            {
                if (e.basis.value != null)
                {
                    e.basis.value.accept(this);
                }
                {
                    Slice<Expression> __r1298 = (e.elements.get()).opSlice().copy();
                    int __key1299 = 0;
                    for (; (__key1299 < __r1298.getLength());__key1299 += 1) {
                        Expression el = __r1298.get(__key1299);
                        if (el != null)
                        {
                            el.accept(this);
                        }
                    }
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            if (e.elements != null)
            {
                {
                    Slice<Expression> __r1300 = (e.elements.get()).opSlice().copy();
                    int __key1301 = 0;
                    for (; (__key1301 < __r1300.getLength());__key1301 += 1) {
                        Expression ex = __r1300.get(__key1301);
                        if (ex != null)
                        {
                            ex.accept(this);
                        }
                    }
                }
            }
        }

        public  void visit(NewExp e) {
            Type tb = e.newtype.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tstruct) && (e.member == null) && (e.arguments != null))
            {
                {
                    Slice<Expression> __r1302 = (e.arguments.get()).opSlice().copy();
                    int __key1303 = 0;
                    for (; (__key1303 < __r1302.getLength());__key1303 += 1) {
                        Expression ex = __r1302.get(__key1303);
                        if (ex != null)
                        {
                            ex.accept(this);
                        }
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) && ((e.e1.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                escapeByRef(e.e1.value, this.er);
            }
            else
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(SliceExp e) {
            if (((e.e1.value.op & 0xFF) == 26))
            {
                VarDeclaration v = ((VarExp)e.e1.value).var.isVarDeclaration();
                Type tb = e.type.value.toBasetype();
                if (v != null)
                {
                    if (((tb.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        return ;
                    }
                    if ((v.storage_class & 65536L) != 0)
                    {
                        (this.er.get()).byvalue.push(v);
                        return ;
                    }
                }
            }
            Type t1b = e.e1.value.type.value.toBasetype();
            if (((t1b.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Type tb = e.type.value.toBasetype();
                if (((tb.ty & 0xFF) != ENUMTY.Tsarray))
                {
                    escapeByRef(e.e1.value, this.er);
                }
            }
            else
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(IndexExp e) {
            if (((e.e1.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                e.e1.value.accept(this);
            }
        }

        public  void visit(BinExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tpointer))
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
            Type t1 = e.e1.value.type.value.toBasetype();
            TypeFunction tf = null;
            TypeDelegate dg = null;
            if (((t1.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                dg = (TypeDelegate)t1;
                tf = (TypeFunction)((TypeDelegate)t1).next.value;
            }
            else if (((t1.ty & 0xFF) == ENUMTY.Tfunction))
            {
                tf = (TypeFunction)t1;
            }
            else
            {
                return ;
            }
            if ((e.arguments != null) && ((e.arguments.get()).length != 0))
            {
                int j = (((tf.linkage == LINK.d) && (tf.parameterList.varargs == VarArg.variadic)) ? 1 : 0);
                {
                    int i = j;
                    for (; (i < (e.arguments.get()).length);i += 1){
                        Expression arg = (e.arguments.get()).get(i);
                        int nparams = tf.parameterList.length();
                        if ((i - j < nparams) && (i >= j))
                        {
                            Parameter p = tf.parameterList.get(i - j);
                            long stc = tf.parameterStorageClass(null, p);
                            if (((stc & 524288L) != 0) && ((stc & 17592186044416L) != 0))
                            {
                                arg.accept(this);
                            }
                            else if (((stc & 2097152L) != 0) && ((stc & 17592186044416L) != 0))
                            {
                                escapeByRef(arg, this.er);
                            }
                        }
                    }
                }
            }
            if (((e.e1.value.op & 0xFF) == 27) && ((t1.ty & 0xFF) == ENUMTY.Tfunction))
            {
                DotVarExp dve = (DotVarExp)e.e1.value;
                FuncDeclaration fd = dve.var.isFuncDeclaration();
                AggregateDeclaration ad = null;
                if (global.value.params.vsafe && tf.isreturn && (fd != null) && ((ad = fd.isThis()) != null))
                {
                    if ((ad.isClassDeclaration() != null) || tf.isscope)
                    {
                        dve.e1.value.accept(this);
                    }
                    else if (ad.isStructDeclaration() != null)
                    {
                        escapeByRef(dve.e1.value, this.er);
                    }
                }
                else if (((dve.var.storage_class & 17592186044416L) != 0) || tf.isreturn)
                {
                    if ((dve.var.storage_class & 524288L) != 0)
                    {
                        dve.e1.value.accept(this);
                    }
                    else if ((dve.var.storage_class & 2097152L) != 0)
                    {
                        escapeByRef(dve.e1.value, this.er);
                    }
                }
                if ((fd != null) && fd.isNested())
                {
                    if (tf.isreturn && tf.isscope)
                    {
                        (this.er.get()).byexp.push(e);
                    }
                }
            }
            if (dg != null)
            {
                if (tf.isreturn)
                {
                    e.e1.value.accept(this);
                }
            }
            if (((e.e1.value.op & 0xFF) == 26))
            {
                VarExp ve = (VarExp)e.e1.value;
                FuncDeclaration fd = ve.var.isFuncDeclaration();
                if ((fd != null) && fd.isNested())
                {
                    if (tf.isreturn && tf.isscope)
                    {
                        (this.er.get()).byexp.push(e);
                    }
                }
            }
        }


        public EscapeVisitor() {}
    }
    private static class EscapeRefVisitor extends Visitor
    {
        private Ptr<EscapeByResults> er = null;
        public  EscapeRefVisitor(Ptr<EscapeByResults> er) {
            this.er = er;
        }

        public  void visit(Expression e) {
        }

        public  void visit(VarExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
            {
                if (((v.storage_class & 2097152L) != 0) && ((v.storage_class & 1099511644160L) != 0) && (v._init != null))
                {
                    {
                        ExpInitializer ez = v._init.isExpInitializer();
                        if ((ez) != null)
                        {
                            assert((ez.exp != null) && ((ez.exp.op & 0xFF) == 95));
                            Expression ex = ((ConstructExp)ez.exp).e2.value;
                            ex.accept(this);
                        }
                    }
                }
                else
                {
                    (this.er.get()).byref.push(v);
                }
            }
        }

        public  void visit(ThisExp e) {
            if ((e.var != null) && e.var.toParent2().isFuncDeclaration().isThis2)
            {
                escapeByValue(e, this.er);
            }
            else if (e.var != null)
            {
                (this.er.get()).byref.push(e.var);
            }
        }

        public  void visit(PtrExp e) {
            escapeByValue(e.e1.value, this.er);
        }

        public  void visit(IndexExp e) {
            Type tb = e.e1.value.type.value.toBasetype();
            if (((e.e1.value.op & 0xFF) == 26))
            {
                VarDeclaration v = ((VarExp)e.e1.value).var.isVarDeclaration();
                if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    if ((v != null) && ((v.storage_class & 65536L) != 0))
                    {
                        (this.er.get()).byref.push(v);
                        return ;
                    }
                }
            }
            if (((tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                e.e1.value.accept(this);
            }
            else if (((tb.ty & 0xFF) == ENUMTY.Tarray))
            {
                escapeByValue(e.e1.value, this.er);
            }
        }

        public  void visit(StructLiteralExp e) {
            if (e.elements != null)
            {
                {
                    Slice<Expression> __r1304 = (e.elements.get()).opSlice().copy();
                    int __key1305 = 0;
                    for (; (__key1305 < __r1304.getLength());__key1305 += 1) {
                        Expression ex = __r1304.get(__key1305);
                        if (ex != null)
                        {
                            ex.accept(this);
                        }
                    }
                }
            }
            (this.er.get()).byexp.push(e);
        }

        public  void visit(DotVarExp e) {
            Type t1b = e.e1.value.type.value.toBasetype();
            if (((t1b.ty & 0xFF) == ENUMTY.Tclass))
            {
                escapeByValue(e.e1.value, this.er);
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
            Type t1 = e.e1.value.type.value.toBasetype();
            TypeFunction tf = null;
            if (((t1.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                tf = (TypeFunction)((TypeDelegate)t1).next.value;
            }
            else if (((t1.ty & 0xFF) == ENUMTY.Tfunction))
            {
                tf = (TypeFunction)t1;
            }
            else
            {
                return ;
            }
            if (tf.isref)
            {
                if ((e.arguments != null) && ((e.arguments.get()).length != 0))
                {
                    int j = (((tf.linkage == LINK.d) && (tf.parameterList.varargs == VarArg.variadic)) ? 1 : 0);
                    {
                        int i = j;
                        for (; (i < (e.arguments.get()).length);i += 1){
                            Expression arg = (e.arguments.get()).get(i);
                            int nparams = tf.parameterList.length();
                            if ((i - j < nparams) && (i >= j))
                            {
                                Parameter p = tf.parameterList.get(i - j);
                                long stc = tf.parameterStorageClass(null, p);
                                if (((stc & 2101248L) != 0) && ((stc & 17592186044416L) != 0))
                                {
                                    arg.accept(this);
                                }
                                else if (((stc & 524288L) != 0) && ((stc & 17592186044416L) != 0))
                                {
                                    if (((arg.op & 0xFF) == 160))
                                    {
                                        DelegateExp de = (DelegateExp)arg;
                                        if (de.func.isNested())
                                        {
                                            (this.er.get()).byexp.push(de);
                                        }
                                    }
                                    else
                                    {
                                        escapeByValue(arg, this.er);
                                    }
                                }
                            }
                        }
                    }
                }
                if (((e.e1.value.op & 0xFF) == 27) && ((t1.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    DotVarExp dve = (DotVarExp)e.e1.value;
                    if (((dve.var.storage_class & 17592186044416L) != 0) || tf.isreturn)
                    {
                        if (((dve.var.storage_class & 524288L) != 0) || tf.isscope)
                        {
                            escapeByValue(dve.e1.value, this.er);
                        }
                        else if (((dve.var.storage_class & 2097152L) != 0) || tf.isref)
                        {
                            dve.e1.value.accept(this);
                        }
                    }
                    FuncDeclaration fd = dve.var.isFuncDeclaration();
                    if ((fd != null) && fd.isNested())
                    {
                        if (tf.isreturn)
                        {
                            (this.er.get()).byexp.push(e);
                        }
                    }
                }
                if (((e.e1.value.op & 0xFF) == 26) && ((t1.ty & 0xFF) == ENUMTY.Tdelegate))
                {
                    escapeByValue(e.e1.value, this.er);
                }
                if (((e.e1.value.op & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)e.e1.value;
                    FuncDeclaration fd = ve.var.isFuncDeclaration();
                    if ((fd != null) && fd.isNested())
                    {
                        if (tf.isreturn)
                        {
                            (this.er.get()).byexp.push(e);
                        }
                    }
                }
            }
            else
            {
                (this.er.get()).byexp.push(e);
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
            Slice<Expression> __r1254 = (ae.elements.get()).opSlice().copy();
            int __key1255 = 0;
            for (; (__key1255 < __r1254.getLength());__key1255 += 1) {
                Expression ex = __r1254.get(__key1255);
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
            Slice<Expression> __r1256 = (ae.keys.get()).opSlice().copy();
            int __key1257 = 0;
            for (; (__key1257 < __r1256.getLength());__key1257 += 1) {
                Expression ex = __r1256.get(__key1257);
                if (ex != null)
                {
                    (errors ? 1 : 0) |= (checkNewEscape(sc, ex, gag) ? 1 : 0);
                }
            }
        }
        {
            Slice<Expression> __r1258 = (ae.values.get()).opSlice().copy();
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

    public static boolean checkParamArgumentEscape(Ptr<Scope> sc, FuncDeclaration fdc, Parameter par, Expression arg, boolean gag) {
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("checkParamArgumentEscape(arg: %s par: %s)\n"), arg != null ? arg.toChars() : new BytePtr("null"), par != null ? par.toChars() : new BytePtr("this"));
        }
        if (!arg.type.value.hasPointers())
        {
            return false;
        }
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(arg, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byfunc.length == 0) && (er.value.byexp.length == 0))
            {
                return false;
            }
            boolean result = false;
            Function2<VarDeclaration,BytePtr,Void> unsafeAssign = new Function2<VarDeclaration,BytePtr,Void>(){
                public Void invoke(VarDeclaration v, BytePtr desc) {
                    if (global.value.params.vsafe && (sc.get()).func.setUnsafe())
                    {
                        if (!gag)
                        {
                            error(arg.loc, new BytePtr("%s `%s` assigned to non-scope parameter `%s` calling %s"), desc, v.toChars(), par != null ? par.toChars() : new BytePtr("this"), fdc != null ? fdc.toPrettyChars(false) : new BytePtr("indirectly"));
                        }
                        result = true;
                    }
                    return null;
                }
            };
            {
                Slice<VarDeclaration> __r1260 = er.value.byvalue.opSlice().copy();
                int __key1261 = 0;
                for (; (__key1261 < __r1260.getLength());__key1261 += 1) {
                    VarDeclaration v = __r1260.get(__key1261);
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
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc.get()).func)))
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
                        {
                            printf(new BytePtr("no infer for %s in %s loc %s, fdc %s, %d\n"), v.toChars(), (sc.get()).func.ident.toChars(), (sc.get()).func.loc.toChars(global.value.params.showColumns), fdc.ident.toChars(), 162);
                        }
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1262 = er.value.byref.opSlice().copy();
                int __key1263 = 0;
                for (; (__key1263 < __r1262.getLength());__key1263 += 1) {
                    VarDeclaration v = __r1262.get(__key1263);
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
                    if (((v.storage_class & 2101248L) == 0L) && (pequals(p, (sc.get()).func)))
                    {
                        if ((par != null) && ((par.storageClass & 17592186568704L) == 524288L))
                        {
                            continue;
                        }
                        unsafeAssign.invoke(v, new BytePtr("reference to local variable"));
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1264 = er.value.byfunc.opSlice().copy();
                int __key1265 = 0;
                for (; (__key1265 < __r1264.getLength());__key1265 += 1) {
                    FuncDeclaration fd = __r1264.get(__key1265);
                    Ref<DArray<VarDeclaration>> vars = ref(new DArray<VarDeclaration>());
                    try {
                        findAllOuterAccessedVariables(fd, ptr(vars));
                        {
                            Slice<VarDeclaration> __r1266 = vars.value.opSlice().copy();
                            int __key1267 = 0;
                            for (; (__key1267 < __r1266.getLength());__key1267 += 1) {
                                VarDeclaration v = __r1266.get(__key1267);
                                assert(!v.isDataseg());
                                Dsymbol p = v.toParent2();
                                notMaybeScope(v);
                                if (((v.storage_class & 2625536L) != 0) && (pequals(p, (sc.get()).func)))
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
                Slice<Expression> __r1268 = er.value.byexp.opSlice().copy();
                int __key1269 = 0;
                for (; (__key1269 < __r1268.getLength());__key1269 += 1) {
                    Expression ee = __r1268.get(__key1269);
                    if ((sc.get()).func.setUnsafe())
                    {
                        if (!gag)
                        {
                            error(ee.loc, new BytePtr("reference to stack allocated value returned by `%s` assigned to non-scope parameter `%s`"), ee.toChars(), par != null ? par.toChars() : new BytePtr("this"));
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
        AssignExp e = new AssignExp(arg.loc, firstArg, arg);
        return checkAssignEscape(sc, e, gag);
    }

    public static boolean checkConstructorEscape(Ptr<Scope> sc, CallExp ce, boolean gag) {
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("checkConstructorEscape(%s, %s)\n"), ce.toChars(), ce.type.value.toChars());
        }
        Type tthis = ce.type.value.toBasetype();
        assert(((tthis.ty & 0xFF) == ENUMTY.Tstruct));
        if (!tthis.hasPointers())
        {
            return false;
        }
        if ((ce.arguments == null) && ((ce.arguments.get()).length != 0))
        {
            return false;
        }
        assert(((ce.e1.value.op & 0xFF) == 27));
        DotVarExp dve = (DotVarExp)ce.e1.value;
        CtorDeclaration ctor = dve.var.isCtorDeclaration();
        assert(ctor != null);
        assert(((ctor.type.ty & 0xFF) == ENUMTY.Tfunction));
        TypeFunction tf = (TypeFunction)ctor.type;
        int nparams = tf.parameterList.length();
        int n = (ce.arguments.get()).length;
        boolean j = (tf.linkage == LINK.d) && (tf.parameterList.varargs == VarArg.variadic);
        {
            int __key1270 = 0;
            int __limit1271 = n;
            for (; (__key1270 < __limit1271);__key1270 += 1) {
                int i = __key1270;
                Expression arg = (ce.arguments.get()).get(i);
                if (!arg.type.value.hasPointers())
                {
                    return false;
                }
                if ((i - (j ? 1 : 0) < nparams) && (i >= (j ? 1 : 0)))
                {
                    Parameter p = tf.parameterList.get(i - (j ? 1 : 0));
                    if ((p.storageClass & 17592186044416L) != 0)
                    {
                        AssignExp e = new AssignExp(arg.loc, dve.e1.value, arg);
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
        if (((e.op & 0xFF) != 90) && ((e.op & 0xFF) != 96) && ((e.op & 0xFF) != 95) && ((e.op & 0xFF) != 71) && ((e.op & 0xFF) != 72) && ((e.op & 0xFF) != 73))
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
        if (((e1.op & 0xFF) == 31))
        {
            return false;
        }
        if (((e1.op & 0xFF) == 49))
        {
            return false;
        }
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(e2, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byfunc.length == 0) && (er.value.byexp.length == 0))
            {
                return false;
            }
            VarDeclaration va = expToVariable(e1);
            if ((va != null) && ((e.op & 0xFF) == 72))
            {
                va = null;
            }
            if ((va != null) && ((e1.op & 0xFF) == 27) && ((va.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass))
            {
                va = null;
            }
            if (false)
            {
                printf(new BytePtr("va: %s\n"), va.toChars());
            }
            boolean inferScope = false;
            if ((va != null) && ((sc.get()).func != null) && ((sc.get()).func.type != null) && (((sc.get()).func.type.ty & 0xFF) == ENUMTY.Tfunction))
            {
                inferScope = ((TypeFunction)(sc.get()).func.type).trust != TRUST.system;
            }
            boolean vaIsRef = (va != null) && ((va.storage_class & 32L) != 0) && ((va.storage_class & 2101248L) != 0) || ((va.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass);
            if (false)
            {
                printf(new BytePtr("va is ref `%s`\n"), va.toChars());
            }
            Function0<Boolean> isFirstRef = new Function0<Boolean>(){
                public Boolean invoke() {
                    if (!vaIsRef)
                    {
                        return false;
                    }
                    Dsymbol p = va.toParent2();
                    FuncDeclaration fd = (sc.get()).func;
                    if ((pequals(p, fd)) && (fd.type != null) && ((fd.type.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        TypeFunction tf = (TypeFunction)fd.type;
                        if ((tf.nextOf() == null) || ((tf.nextOf().ty & 0xFF) != ENUMTY.Tvoid) && (fd.isCtorDeclaration() == null))
                        {
                            return false;
                        }
                        if ((pequals(va, fd.vthis)))
                        {
                            return true;
                        }
                        if ((fd.parameters != null) && ((fd.parameters.get()).length != 0) && (pequals((fd.parameters.get()).get(0), va)))
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
                printf(new BytePtr("va is first ref `%s`\n"), va.toChars());
            }
            boolean result = false;
            {
                Slice<VarDeclaration> __r1272 = er.value.byvalue.opSlice().copy();
                int __key1273 = 0;
                for (; (__key1273 < __r1272.getLength());__key1273 += 1) {
                    VarDeclaration v = __r1272.get(__key1273);
                    if (false)
                    {
                        printf(new BytePtr("byvalue: %s\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    if ((pequals(v, va)))
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    if ((va != null) && !vaIsRef && !va.isScope() && !v.isScope() && ((va.storage_class & v.storage_class & 281474976776192L) == 281474976710656L) && (pequals(p, (sc.get()).func)))
                    {
                        va.addMaybe(v);
                        continue;
                    }
                    if (vaIsFirstRef && v.isScope() || ((v.storage_class & 281474976710656L) != 0) && ((v.storage_class & 17592186044416L) == 0) && v.isParameter() && (((sc.get()).func.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        if (false)
                        {
                            printf(new BytePtr("inferring 'return' for parameter %s in function %s\n"), v.toChars(), (sc.get()).func.toChars());
                        }
                        inferReturn((sc.get()).func, v);
                    }
                    if (!((va != null) && va.isScope()) || vaIsRef)
                    {
                        notMaybeScope(v);
                    }
                    if (v.isScope())
                    {
                        if (vaIsFirstRef && v.isParameter() && ((v.storage_class & 17592186044416L) != 0))
                        {
                            if (va.isScope())
                            {
                                continue;
                            }
                            if (inferScope && !va.doNotInferScope)
                            {
                                if (false)
                                {
                                    printf(new BytePtr("inferring scope for lvalue %s\n"), va.toChars());
                                }
                                va.storage_class |= 562949953945600L;
                                continue;
                            }
                        }
                        if ((va != null) && va.isScope() && ((va.storage_class & 17592186044416L) != 0) && ((v.storage_class & 17592186044416L) == 0) && (sc.get()).func.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to return scope `%s`"), v.toChars(), va.toChars());
                            }
                            result = true;
                            continue;
                        }
                        if ((va != null) && va.enclosesLifetimeOf(v) && ((v.storage_class & 1099511627808L) == 0) || ((ae.e1.value.op & 0xFF) == 27) && ((va.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass) && va.enclosesLifetimeOf(v) || !va.isScope() || vaIsRef || ((va.storage_class & 2101248L) != 0) && ((v.storage_class & 1099511627808L) == 0) && (sc.get()).func.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.toChars());
                            }
                            result = true;
                            continue;
                        }
                        if ((va != null) && !va.isDataseg() && !va.doNotInferScope)
                        {
                            if (!va.isScope() && inferScope)
                            {
                                va.storage_class |= 562949953945600L;
                                if (((v.storage_class & 17592186044416L) != 0) && ((va.storage_class & 17592186044416L) == 0))
                                {
                                    va.storage_class |= 4521191813414912L;
                                }
                            }
                            continue;
                        }
                        if ((sc.get()).func.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc, new BytePtr("scope variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            }
                            result = true;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if ((va != null) && !va.isDataseg() && !va.doNotInferScope)
                            {
                                if (!va.isScope() && inferScope)
                                {
                                    va.storage_class |= 562949953945600L;
                                }
                                continue;
                            }
                            if ((sc.get()).func.setUnsafe())
                            {
                                if (!gag)
                                {
                                    error(ae.loc, new BytePtr("variadic variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
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
                Slice<VarDeclaration> __r1274 = er.value.byref.opSlice().copy();
                int __key1275 = 0;
                for (; (__key1275 < __r1274.getLength());__key1275 += 1) {
                    VarDeclaration v = __r1274.get(__key1275);
                    if (false)
                    {
                        printf(new BytePtr("byref: %s\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    if (global.value.params.vsafe)
                    {
                        if ((va != null) && va.isScope() && ((va.storage_class & 17592186044416L) != 0) && ((v.storage_class & 2101248L) == 0L) && (sc.get()).func.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc, new BytePtr("address of local variable `%s` assigned to return scope `%s`"), v.toChars(), va.toChars());
                            }
                            result = true;
                            continue;
                        }
                    }
                    Dsymbol p = v.toParent2();
                    if ((va != null) && va.enclosesLifetimeOf(v) && ((v.storage_class & 32L) == 0) || ((va.storage_class & 2097152L) != 0) || va.isDataseg() && (sc.get()).func.setUnsafe())
                    {
                        if (!gag)
                        {
                            error(ae.loc, new BytePtr("address of variable `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.toChars());
                        }
                        result = true;
                        continue;
                    }
                    if ((va != null) && ((v.storage_class & 2101248L) != 0))
                    {
                        Dsymbol pva = va.toParent2();
                        {
                            Dsymbol pv = p;
                            for (; pv != null;){
                                pv = pv.toParent2();
                                if ((pequals(pva, pv)))
                                {
                                    if ((sc.get()).func.setUnsafe())
                                    {
                                        if (!gag)
                                        {
                                            error(ae.loc, new BytePtr("reference `%s` assigned to `%s` with longer lifetime"), v.toChars(), va.toChars());
                                        }
                                        result = true;
                                        continue ByRef;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (!((va != null) && va.isScope()))
                    {
                        notMaybeScope(v);
                    }
                    if (((v.storage_class & 2101248L) == 0L) && (pequals(p, (sc.get()).func)))
                    {
                        if ((va != null) && !va.isDataseg() && !va.doNotInferScope)
                        {
                            if (!va.isScope() && inferScope)
                            {
                                va.storage_class |= 562949953945600L;
                            }
                            continue;
                        }
                        if (((e1.op & 0xFF) == 49))
                        {
                            continue;
                        }
                        if ((sc.get()).func.setUnsafe())
                        {
                            if (!gag)
                            {
                                error(ae.loc, new BytePtr("reference to local variable `%s` assigned to non-scope `%s`"), v.toChars(), e1.toChars());
                            }
                            result = true;
                        }
                        continue;
                    }
                }
            }
            {
                Slice<FuncDeclaration> __r1276 = er.value.byfunc.opSlice().copy();
                int __key1277 = 0;
                for (; (__key1277 < __r1276.getLength());__key1277 += 1) {
                    FuncDeclaration fd = __r1276.get(__key1277);
                    if (false)
                    {
                        printf(new BytePtr("byfunc: %s, %d\n"), fd.toChars(), fd.tookAddressOf);
                    }
                    Ref<DArray<VarDeclaration>> vars = ref(new DArray<VarDeclaration>());
                    try {
                        findAllOuterAccessedVariables(fd, ptr(vars));
                        if ((va != null) && va.isScope() && (fd.tookAddressOf != 0) && global.value.params.vsafe)
                        {
                            fd.tookAddressOf -= 1;
                        }
                        {
                            Slice<VarDeclaration> __r1278 = vars.value.opSlice().copy();
                            int __key1279 = 0;
                            for (; (__key1279 < __r1278.getLength());__key1279 += 1) {
                                VarDeclaration v = __r1278.get(__key1279);
                                assert(!v.isDataseg());
                                Dsymbol p = v.toParent2();
                                if (!((va != null) && va.isScope()))
                                {
                                    notMaybeScope(v);
                                }
                                if (((v.storage_class & 2625536L) != 0) && (pequals(p, (sc.get()).func)))
                                {
                                    if ((va != null) && !va.isDataseg() && !va.doNotInferScope)
                                    {
                                        continue;
                                    }
                                    if ((sc.get()).func.setUnsafe())
                                    {
                                        if (!gag)
                                        {
                                            error(ae.loc, new BytePtr("reference to local `%s` assigned to non-scope `%s` in @safe code"), v.toChars(), e1.toChars());
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
                Slice<Expression> __r1280 = er.value.byexp.opSlice().copy();
                int __key1281 = 0;
                for (; (__key1281 < __r1280.getLength());__key1281 += 1) {
                    Expression ee = __r1280.get(__key1281);
                    if (false)
                    {
                        printf(new BytePtr("byexp: %s\n"), ee.toChars());
                    }
                    if ((va != null) && ((ee.op & 0xFF) == 18) && ((ee.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) && ((va.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray) && ((va.storage_class & 1099511627776L) == 0))
                    {
                        if (!gag)
                        {
                            deprecation(ee.loc, new BytePtr("slice of static array temporary returned by `%s` assigned to longer lived variable `%s`"), ee.toChars(), va.toChars());
                        }
                        continue;
                    }
                    if ((va != null) && !va.isDataseg() && !va.doNotInferScope)
                    {
                        if (!va.isScope() && inferScope)
                        {
                            va.storage_class |= 562949953945600L;
                        }
                        continue;
                    }
                    if ((sc.get()).func.setUnsafe())
                    {
                        if (!gag)
                        {
                            error(ee.loc, new BytePtr("reference to stack allocated value returned by `%s` assigned to non-scope `%s`"), ee.toChars(), e1.toChars());
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
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byexp.length == 0))
            {
                return false;
            }
            boolean result = false;
            {
                Slice<VarDeclaration> __r1282 = er.value.byvalue.opSlice().copy();
                int __key1283 = 0;
                for (; (__key1283 < __r1282.getLength());__key1283 += 1) {
                    VarDeclaration v = __r1282.get(__key1283);
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    if (v.isScope() && !v.iscatchvar)
                    {
                        if (((sc.get())._module != null) && (sc.get())._module.isRoot())
                        {
                            if (global.value.params.vsafe)
                            {
                                if (!gag)
                                {
                                    error(e.loc, new BytePtr("scope variable `%s` may not be thrown"), v.toChars());
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
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("[%s] checkNewEscape, e: `%s`\n"), e.loc.toChars(global.value.params.showColumns), e.toChars());
        }
        Ref<EscapeByResults> er = ref(new EscapeByResults());
        try {
            escapeByValue(e, ptr(er));
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byexp.length == 0))
            {
                return false;
            }
            boolean result = false;
            {
                Slice<VarDeclaration> __r1284 = er.value.byvalue.opSlice().copy();
                int __key1285 = 0;
                for (; (__key1285 < __r1284.getLength());__key1285 += 1) {
                    VarDeclaration v = __r1284.get(__key1285);
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
                        if (((sc.get())._module != null) && (sc.get())._module.isRoot() && !(pequals(p.parent.value, (sc.get()).func)))
                        {
                            if (global.value.params.vsafe)
                            {
                                if (!gag)
                                {
                                    error(e.loc, new BytePtr("scope variable `%s` may not be copied into allocated memory"), v.toChars());
                                }
                                result = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!gag)
                            {
                                error(e.loc, new BytePtr("copying `%s` into allocated memory escapes a reference to variadic parameter `%s`"), e.toChars(), v.toChars());
                            }
                            result = false;
                        }
                    }
                    else
                    {
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1286 = er.value.byref.opSlice().copy();
                int __key1287 = 0;
                for (; (__key1287 < __r1286.getLength());__key1287 += 1) {
                    VarDeclaration v = __r1286.get(__key1287);
                    if (false)
                    {
                        printf(new BytePtr("byref `%s`\n"), v.toChars());
                    }
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v) {
                            if (!gag)
                            {
                                BytePtr kind = pcopy((v.storage_class & 32L) != 0 ? new BytePtr("parameter") : new BytePtr("local"));
                                error(e.loc, new BytePtr("copying `%s` into allocated memory escapes a reference to %s variable `%s`"), e.toChars(), kind, v.toChars());
                            }
                            result = true;
                            return null;
                        }
                    };
                    if (v.isDataseg())
                    {
                        continue;
                    }
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
                                    if (!gag)
                                    {
                                        error(e.loc, new BytePtr("storing reference to outer local variable `%s` into allocated memory causes it to escape"), v.toChars());
                                    }
                                    result = true;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            {
                Slice<Expression> __r1288 = er.value.byexp.opSlice().copy();
                int __key1289 = 0;
                for (; (__key1289 < __r1288.getLength());__key1289 += 1) {
                    Expression ee = __r1288.get(__key1289);
                    if (false)
                    {
                        printf(new BytePtr("byexp %s\n"), ee.toChars());
                    }
                    if (!gag)
                    {
                        error(ee.loc, new BytePtr("storing reference to stack allocated value returned by `%s` into allocated memory causes it to escape"), ee.toChars());
                    }
                    result = true;
                }
            }
            return result;
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
        boolean log = false;
        if (false)
        {
            printf(new BytePtr("[%s] checkReturnEscapeImpl, refs: %d e: `%s`\n"), e.loc.toChars(global.value.params.showColumns), (refs ? 1 : 0), e.toChars());
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
            if ((er.value.byref.length == 0) && (er.value.byvalue.length == 0) && (er.value.byexp.length == 0))
            {
                return false;
            }
            boolean result = false;
            {
                Slice<VarDeclaration> __r1290 = er.value.byvalue.opSlice().copy();
                int __key1291 = 0;
                for (; (__key1291 < __r1290.getLength());__key1291 += 1) {
                    VarDeclaration v = __r1290.get(__key1291);
                    if (false)
                    {
                        printf(new BytePtr("byvalue `%s`\n"), v.toChars());
                    }
                    if (v.isDataseg())
                    {
                        continue;
                    }
                    Dsymbol p = v.toParent2();
                    if (v.isScope() || ((v.storage_class & 281474976710656L) != 0) && ((v.storage_class & 17592186044416L) == 0) && v.isParameter() && (((sc.get()).func.flags & FUNCFLAG.returnInprocess) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        inferReturn((sc.get()).func, v);
                        continue;
                    }
                    if (v.isScope())
                    {
                        if ((v.storage_class & 17592186044416L) != 0)
                        {
                            continue;
                        }
                        if (((sc.get())._module != null) && (sc.get())._module.isRoot() && !(!refs && (pequals(p.parent.value, (sc.get()).func)) && (p.isFuncDeclaration() != null) && (p.isFuncDeclaration().fes != null)) && !(!refs && (p.isFuncDeclaration() != null) && ((sc.get()).func.isFuncDeclaration().getLevel(p.isFuncDeclaration(), (sc.get()).intypeof) > 0)))
                        {
                            if (global.value.params.vsafe)
                            {
                                if (!gag)
                                {
                                    error(e.loc, new BytePtr("scope variable `%s` may not be returned"), v.toChars());
                                }
                                result = true;
                            }
                            continue;
                        }
                    }
                    else if (((v.storage_class & 65536L) != 0) && (pequals(p, (sc.get()).func)))
                    {
                        Type tb = v.type.toBasetype();
                        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if (!gag)
                            {
                                error(e.loc, new BytePtr("returning `%s` escapes a reference to variadic parameter `%s`"), e.toChars(), v.toChars());
                            }
                            result = false;
                        }
                    }
                    else
                    {
                        v.doNotInferScope = true;
                    }
                }
            }
            {
                Slice<VarDeclaration> __r1292 = er.value.byref.opSlice().copy();
                int __key1293 = 0;
                for (; (__key1293 < __r1292.getLength());__key1293 += 1) {
                    VarDeclaration v = __r1292.get(__key1293);
                    if (false)
                    {
                        printf(new BytePtr("byref `%s`\n"), v.toChars());
                    }
                    Function1<VarDeclaration,Void> escapingRef = new Function1<VarDeclaration,Void>(){
                        public Void invoke(VarDeclaration v) {
                            if (!gag)
                            {
                                BytePtr msg = null;
                                if ((v.storage_class & 32L) != 0)
                                {
                                    msg = pcopy(new BytePtr("returning `%s` escapes a reference to parameter `%s`, perhaps annotate with `return`"));
                                }
                                else
                                {
                                    msg = pcopy(new BytePtr("returning `%s` escapes a reference to local variable `%s`"));
                                }
                                error(e.loc, msg, e.toChars(), v.toChars());
                            }
                            result = true;
                            return null;
                        }
                    };
                    if (v.isDataseg())
                    {
                        continue;
                    }
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
                                    if (!gag)
                                    {
                                        error(e.loc, new BytePtr("escaping reference to outer local variable `%s`"), v.toChars());
                                    }
                                    result = true;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            {
                Slice<Expression> __r1294 = er.value.byexp.opSlice().copy();
                int __key1295 = 0;
                for (; (__key1295 < __r1294.getLength());__key1295 += 1) {
                    Expression ee = __r1294.get(__key1295);
                    if (false)
                    {
                        printf(new BytePtr("byexp %s\n"), ee.toChars());
                    }
                    if (!gag)
                    {
                        error(ee.loc, new BytePtr("escaping reference to stack allocated value returned by `%s`"), ee.toChars());
                    }
                    result = true;
                }
            }
            return result;
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
                    int __key1296 = 0;
                    int __limit1297 = dim;
                    for (; (__key1296 < __limit1297);__key1296 += 1) {
                        int i = __key1296;
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
                        Slice<VarDeclaration> __r1306 = fdp.closureVars.opSlice().copy();
                        int __key1307 = 0;
                        for (; (__key1307 < __r1306.getLength());__key1307 += 1) {
                            VarDeclaration v = __r1306.get(__key1307);
                            {
                                Slice<FuncDeclaration> __r1308 = v.nestedrefs.opSlice().copy();
                                int __key1309 = 0;
                                for (; (__key1309 < __r1308.getLength());__key1309 += 1) {
                                    FuncDeclaration fdv = __r1308.get(__key1309);
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
        {
            printf(new BytePtr("eliminateMaybeScopes()\n"));
        }
        boolean changes = false;
        do {
            {
                changes = false;
                {
                    Slice<VarDeclaration> __r1310 = array.copy();
                    int __key1311 = 0;
                    for (; (__key1311 < __r1310.getLength());__key1311 += 1) {
                        VarDeclaration va = __r1310.get(__key1311);
                        if (false)
                        {
                            printf(new BytePtr("  va = %s\n"), va.toChars());
                        }
                        if ((va.storage_class & 281474977234944L) == 0)
                        {
                            if (va.maybes != null)
                            {
                                {
                                    Slice<VarDeclaration> __r1312 = (va.maybes.get()).opSlice().copy();
                                    int __key1313 = 0;
                                    for (; (__key1313 < __r1312.getLength());__key1313 += 1) {
                                        VarDeclaration v = __r1312.get(__key1313);
                                        if (false)
                                        {
                                            printf(new BytePtr("    v = %s\n"), v.toChars());
                                        }
                                        if ((v.storage_class & 281474976710656L) != 0)
                                        {
                                            notMaybeScope(v);
                                            if ((v.storage_class & 2101248L) == 0)
                                            {
                                                v.storage_class &= -4521191813414913L;
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
