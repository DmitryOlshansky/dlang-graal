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
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;

public class initsem {

    public static Expression toAssocArrayLiteral(ArrayInitializer ai) {
        Expression e = null;
        int dim = ai.value.length;
        DArray<Expression> keys = new DArray<Expression>(dim);
        DArray<Expression> values = new DArray<Expression>(dim);
        try {
            {
                int i = 0;
            L_outer1:
                for (; (i < dim);i++){
                    e = ai.index.get(i);
                    if (e == null)
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    keys.set(i, e);
                    Initializer iz = ai.value.get(i);
                    if (iz == null)
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    e = initializerToExpression(iz, null);
                    if (e == null)
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    values.set(i, e);
                }
            }
            e = new AssocArrayLiteralExp(ai.loc, keys, values);
            return e;
        }
        catch(Dispatch0 __d){}
    /*Lno:*/
        error(ai.loc, new BytePtr("not an associative array initializer"));
        return new ErrorExp();
    }

    public static Initializer initializerSemantic(Initializer init, Scope sc, Type t, int needInterpret) {
        Ref<Scope> sc_ref = ref(sc);
        Ref<Type> t_ref = ref(t);
        IntRef needInterpret_ref = ref(needInterpret);
        Function1<VoidInitializer,Initializer> visitVoid = new Function1<VoidInitializer,Initializer>(){
            public Initializer invoke(VoidInitializer i){
                i.type = t_ref.value;
                return i;
            }
        };
        Function1<ErrorInitializer,Initializer> visitError = new Function1<ErrorInitializer,Initializer>(){
            public Initializer invoke(ErrorInitializer i){
                return i;
            }
        };
        Function1<StructInitializer,Initializer> visitStruct = new Function1<StructInitializer,Initializer>(){
            public Initializer invoke(StructInitializer i){
                t_ref.value = t_ref.value.toBasetype();
                if (((t_ref.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t_ref.value.nextOf().toBasetype().ty & 0xFF) == ENUMTY.Tstruct))
                    t_ref.value = t_ref.value.nextOf().toBasetype();
                if (((t_ref.value.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    StructDeclaration sd = ((TypeStruct)t_ref.value).sym;
                    if (sd.ctor != null)
                    {
                        error(i.loc, new BytePtr("%s `%s` has constructors, cannot use `{ initializers }`, use `%s( initializers )` instead"), sd.kind(), sd.toChars(), sd.toChars());
                        return new ErrorInitializer();
                    }
                    sd.size(i.loc);
                    if ((sd.sizeok != Sizeok.done))
                    {
                        return new ErrorInitializer();
                    }
                    int nfields = sd.nonHiddenFields();
                    DArray<Expression> elements = new DArray<Expression>(nfields);
                    {
                        int j = 0;
                        for (; (j < (elements).length);j++) {
                            elements.set(j, null);
                        }
                    }
                    boolean errors = false;
                    {
                        int fieldi = 0;
                        int j = 0;
                        for (; (j < i.field.length);j++){
                            {
                                Identifier id = i.field.get(j);
                                if ((id) != null)
                                {
                                    Dsymbol s = sd.search(i.loc, id, 8);
                                    if (s == null)
                                    {
                                        s = sd.search_correct(id);
                                        Loc initLoc = i.value.get(j).loc.copy();
                                        if (s != null)
                                            error(initLoc, new BytePtr("`%s` is not a member of `%s`, did you mean %s `%s`?"), id.toChars(), sd.toChars(), s.kind(), s.toChars());
                                        else
                                            error(initLoc, new BytePtr("`%s` is not a member of `%s`"), id.toChars(), sd.toChars());
                                        return new ErrorInitializer();
                                    }
                                    s = s.toAlias();
                                    {
                                        fieldi = 0;
                                        for (; 1 != 0;fieldi++){
                                            if ((fieldi >= nfields))
                                            {
                                                error(i.loc, new BytePtr("`%s.%s` is not a per-instance initializable field"), sd.toChars(), s.toChars());
                                                return new ErrorInitializer();
                                            }
                                            if ((pequals(s, sd.fields.get(fieldi))))
                                                break;
                                        }
                                    }
                                }
                                else if ((fieldi >= nfields))
                                {
                                    error(i.loc, new BytePtr("too many initializers for `%s`"), sd.toChars());
                                    return new ErrorInitializer();
                                }
                            }
                            VarDeclaration vd = sd.fields.get(fieldi);
                            if ((elements).get(fieldi) != null)
                            {
                                error(i.loc, new BytePtr("duplicate initializer for field `%s`"), vd.toChars());
                                expr(errors = true);
                                continue;
                            }
                            if (vd.type.hasPointers())
                            {
                                if ((t_ref.value.alignment() < target.ptrsize) || ((vd.offset & target.ptrsize - 1) != 0) && ((sc_ref.value).func != null) && (sc_ref.value).func.setUnsafe())
                                {
                                    error(i.loc, new BytePtr("field `%s.%s` cannot assign to misaligned pointers in `@safe` code"), sd.toChars(), vd.toChars());
                                    expr(errors = true);
                                }
                            }
                            {
                                int k = 0;
                                for (; (k < nfields);k++){
                                    VarDeclaration v2 = sd.fields.get(k);
                                    if (vd.isOverlappedWith(v2) && ((elements).get(k) != null))
                                    {
                                        error(i.loc, new BytePtr("overlapping initialization for field `%s` and `%s`"), v2.toChars(), vd.toChars());
                                        expr(errors = true);
                                        continue;
                                    }
                                }
                            }
                            assert(sc_ref.value != null);
                            Initializer iz = i.value.get(j);
                            iz = initializerSemantic(iz, sc_ref.value, vd.type.addMod(t_ref.value.mod), needInterpret_ref.value);
                            Expression ex = initializerToExpression(iz, null);
                            if (((ex.op & 0xFF) == 127))
                            {
                                expr(errors = true);
                                continue;
                            }
                            i.value.set(j, iz);
                            elements.set(fieldi, doCopyOrMove(sc_ref.value, ex, null));
                            fieldi += 1;
                        }
                    }
                    if (errors)
                    {
                        return new ErrorInitializer();
                    }
                    StructLiteralExp sle = new StructLiteralExp(i.loc, sd, elements, t_ref.value);
                    if (!sd.fill(i.loc, elements, false))
                    {
                        return new ErrorInitializer();
                    }
                    sle.type = t_ref.value;
                    ExpInitializer ie = new ExpInitializer(i.loc, sle);
                    return initializerSemantic(ie, sc_ref.value, t_ref.value, needInterpret_ref.value);
                }
                else if (((t_ref.value.ty & 0xFF) == ENUMTY.Tdelegate) || ((t_ref.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t_ref.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && (i.value.length == 0))
                {
                    byte tok = ((t_ref.value.ty & 0xFF) == ENUMTY.Tdelegate) ? TOK.delegate_ : TOK.function_;
                    Type tf = new TypeFunction(new ParameterList(null, VarArg.none), null, LINK.d, 0L);
                    FuncLiteralDeclaration fd = new FuncLiteralDeclaration(i.loc, Loc.initial, tf, tok, null, null);
                    fd.fbody = new CompoundStatement(i.loc, new DArray<Statement>());
                    fd.endloc = i.loc.copy();
                    Expression e = new FuncExp(i.loc, fd);
                    ExpInitializer ie = new ExpInitializer(i.loc, e);
                    return initializerSemantic(ie, sc_ref.value, t_ref.value, needInterpret_ref.value);
                }
                if (((t_ref.value.ty & 0xFF) != ENUMTY.Terror))
                    error(i.loc, new BytePtr("a struct is not a valid initializer for a `%s`"), t_ref.value.toChars());
                return new ErrorInitializer();
            }
        };
        Function1<ArrayInitializer,Initializer> visitArray = new Function1<ArrayInitializer,Initializer>(){
            public Initializer invoke(ArrayInitializer i){
                int length = 0;
                int amax = -2147483648;
                boolean errors = false;
                if (i.sem)
                {
                    return i;
                }
                expr(i.sem = true);
                t_ref.value = t_ref.value.toBasetype();
                try {
                    {
                        int __dispatch0 = 0;
                        dispatched_0:
                        do {
                            switch (__dispatch0 != 0 ? __dispatch0 : (t_ref.value.ty & 0xFF))
                            {
                                case 1:
                                case 0:
                                    break;
                                case 41:
                                    t_ref.value = ((TypeVector)t_ref.value).basetype;
                                    break;
                                case 2:
                                case 8:
                                    Expression e = null;
                                    if (((t_ref.value.ty & 0xFF) == ENUMTY.Taarray) || i.isAssociativeArray())
                                        e = toAssocArrayLiteral(i);
                                    else
                                        e = initializerToExpression(i, null);
                                    if (e == null)
                                    {
                                        error(i.loc, new BytePtr("cannot use array to initialize `%s`"), t_ref.value.toChars());
                                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                                    }
                                    ExpInitializer ei = new ExpInitializer(e.loc, e);
                                    return initializerSemantic(ei, sc_ref.value, t_ref.value, needInterpret_ref.value);
                                case 3:
                                    if (((t_ref.value.nextOf().ty & 0xFF) != ENUMTY.Tfunction))
                                        break;
                                    /*goto default*/ { __dispatch0 = -2; continue dispatched_0; }
                                default:
                                __dispatch0 = 0;
                                error(i.loc, new BytePtr("cannot use array to initialize `%s`"), t_ref.value.toChars());
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch0 != 0);
                    }
                    i.type = t_ref.value;
                    length = 0;
                    {
                        int j = 0;
                    L_outer2:
                        for (; (j < i.index.length);j++){
                            Expression idx = i.index.get(j);
                            if (idx != null)
                            {
                                sc_ref.value = (sc_ref.value).startCTFE();
                                idx = expressionSemantic(idx, sc_ref.value);
                                sc_ref.value = (sc_ref.value).endCTFE();
                                idx = idx.ctfeInterpret();
                                i.index.set(j, idx);
                                long idxvalue = idx.toInteger();
                                if ((idxvalue >= 2147483648L))
                                {
                                    error(i.loc, new BytePtr("array index %llu overflow"), idxvalue);
                                    expr(errors = true);
                                }
                                length = (int)idxvalue;
                                if (((idx.op & 0xFF) == 127))
                                    expr(errors = true);
                            }
                            Initializer val = i.value.get(j);
                            ExpInitializer ei = val.isExpInitializer();
                            if ((ei != null) && (idx == null))
                                expr(ei.expandTuples = true);
                            val = initializerSemantic(val, sc_ref.value, t_ref.value.nextOf(), needInterpret_ref.value);
                            if (val.isErrorInitializer() != null)
                                expr(errors = true);
                            ei = val.isExpInitializer();
                            if ((ei != null) && ((ei.exp.op & 0xFF) == 126))
                            {
                                TupleExp te = (TupleExp)ei.exp;
                                i.index.remove(j);
                                i.value.remove(j);
                                {
                                    int k = 0;
                                    for (; (k < (te.exps).length);k += 1){
                                        Expression e = (te.exps).get(k);
                                        i.index.insert(j + k, null);
                                        i.value.insert(j + k, new ExpInitializer(e.loc, e));
                                    }
                                }
                                j--;
                                continue L_outer2;
                            }
                            else
                            {
                                i.value.set(j, val);
                            }
                            length++;
                            if ((length == 0))
                            {
                                error(i.loc, new BytePtr("array dimension overflow"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            if ((length > i.dim))
                                i.dim = length;
                        }
                    }
                    if (((t_ref.value.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        long edim = ((TypeSArray)t_ref.value).dim.toInteger();
                        if (((long)i.dim > edim))
                        {
                            error(i.loc, new BytePtr("array initializer has %u elements, but array length is %llu"), i.dim, edim);
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if (errors)
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                    {
                        long sz = t_ref.value.nextOf().size();
                        Ref<Boolean> overflow = ref(false);
                        long max = mulu((long)i.dim, sz, overflow);
                        if (overflow.value || (max >= 2147483648L))
                        {
                            error(i.loc, new BytePtr("array dimension %llu exceeds max of %llu"), (long)i.dim, 2147483648L / sz);
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                        return i;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lerr:*/
                return new ErrorInitializer();
            }
        };
        Function1<ExpInitializer,Initializer> visitExp = new Function1<ExpInitializer,Initializer>(){
            public Initializer invoke(ExpInitializer i){
                if (needInterpret_ref.value != 0)
                    sc_ref.value = (sc_ref.value).startCTFE();
                i.exp = expressionSemantic(i.exp, sc_ref.value);
                i.exp = resolveProperties(sc_ref.value, i.exp);
                if (needInterpret_ref.value != 0)
                    sc_ref.value = (sc_ref.value).endCTFE();
                if (((i.exp.op & 0xFF) == 127))
                {
                    return new ErrorInitializer();
                }
                int olderrors = global.errors;
                if (needInterpret_ref.value != 0)
                {
                    if (i.exp.implicitConvTo(t_ref.value) != 0)
                    {
                        i.exp = i.exp.implicitCastTo(sc_ref.value, t_ref.value);
                    }
                    if ((global.gag == 0) && (olderrors != global.errors))
                    {
                        return i;
                    }
                    i.exp = i.exp.ctfeInterpret();
                    if (((i.exp.op & 0xFF) == 232))
                        error(i.loc, new BytePtr("variables cannot be initialized with an expression of type `void`. Use `void` initialization instead."));
                }
                else
                {
                    i.exp = i.exp.optimize(0, false);
                }
                if ((global.gag == 0) && (olderrors != global.errors))
                {
                    return i;
                }
                if (((i.exp.type.ty & 0xFF) == ENUMTY.Ttuple) && ((((TypeTuple)i.exp.type).arguments).length == 0))
                {
                    Type et = i.exp.type;
                    i.exp = new TupleExp(i.exp.loc, new DArray<Expression>());
                    i.exp.type = et;
                }
                if (((i.exp.op & 0xFF) == 20))
                {
                    i.exp.error(new BytePtr("initializer must be an expression, not `%s`"), i.exp.toChars());
                    return new ErrorInitializer();
                }
                if ((needInterpret_ref.value != 0) && hasNonConstPointers(i.exp))
                {
                    i.exp.error(new BytePtr("cannot use non-constant CTFE pointer in an initializer `%s`"), i.exp.toChars());
                    return new ErrorInitializer();
                }
                Type tb = t_ref.value.toBasetype();
                Type ti = i.exp.type.toBasetype();
                if (((i.exp.op & 0xFF) == 126) && i.expandTuples && (i.exp.implicitConvTo(t_ref.value) == 0))
                {
                    return new ExpInitializer(i.loc, i.exp);
                }
                try {
                    if (((i.exp.op & 0xFF) == 121) && ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        StringExp se = (StringExp)i.exp;
                        Type typeb = se.type.toBasetype();
                        byte tynto = tb.nextOf().ty;
                        if ((se.committed == 0) && ((typeb.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.ty & 0xFF) == ENUMTY.Tsarray) && ((tynto & 0xFF) == ENUMTY.Tchar) || ((tynto & 0xFF) == ENUMTY.Twchar) || ((tynto & 0xFF) == ENUMTY.Tdchar) && ((long)se.numberOfCodeUnits((tynto & 0xFF)) < ((TypeSArray)tb).dim.toInteger()))
                        {
                            i.exp = se.castTo(sc_ref.value, t_ref.value);
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if (((tb.ty & 0xFF) == ENUMTY.Tstruct) && !(((ti.ty & 0xFF) == ENUMTY.Tstruct) && (pequals(tb.toDsymbol(sc_ref.value), ti.toDsymbol(sc_ref.value)))) && (i.exp.implicitConvTo(t_ref.value) == 0))
                    {
                        StructDeclaration sd = ((TypeStruct)tb).sym;
                        if (sd.ctor != null)
                        {
                            Expression e = null;
                            e = new StructLiteralExp(i.loc, sd, null, null);
                            e = new DotIdExp(i.loc, e, Id.ctor);
                            e = new CallExp(i.loc, e, i.exp);
                            e = expressionSemantic(e, sc_ref.value);
                            if (needInterpret_ref.value != 0)
                                i.exp = e.ctfeInterpret();
                            else
                                i.exp = e.optimize(0, false);
                        }
                    }
                    if (((tb.ty & 0xFF) == ENUMTY.Tsarray) && !tb.nextOf().equals(ti.toBasetype().nextOf()) && (i.exp.implicitConvTo(tb.nextOf()) != 0))
                    {
                        t_ref.value = tb.nextOf();
                    }
                    if (i.exp.implicitConvTo(t_ref.value) != 0)
                    {
                        i.exp = i.exp.implicitCastTo(sc_ref.value, t_ref.value);
                    }
                    else
                    {
                        if (((tb.ty & 0xFF) == ENUMTY.Tsarray) && (i.exp.implicitConvTo(tb.nextOf().arrayOf()) > MATCH.nomatch))
                        {
                            long dim1 = ((TypeSArray)tb).dim.toInteger();
                            long dim2 = dim1;
                            if (((i.exp.op & 0xFF) == 47))
                            {
                                ArrayLiteralExp ale = (ArrayLiteralExp)i.exp;
                                dim2 = ale.elements != null ? (long)(ale.elements).length : 0L;
                            }
                            else if (((i.exp.op & 0xFF) == 31))
                            {
                                Type tx = toStaticArrayType((SliceExp)i.exp);
                                if (tx != null)
                                    dim2 = ((TypeSArray)tx).dim.toInteger();
                            }
                            if ((dim1 != dim2))
                            {
                                i.exp.error(new BytePtr("mismatched array lengths, %d and %d"), (int)dim1, (int)dim2);
                                i.exp = new ErrorExp();
                            }
                        }
                        i.exp = i.exp.implicitCastTo(sc_ref.value, t_ref.value);
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                if (((i.exp.op & 0xFF) == 127))
                {
                    return i;
                }
                if (needInterpret_ref.value != 0)
                    i.exp = i.exp.ctfeInterpret();
                else
                    i.exp = i.exp.optimize(0, false);
                return i;
            }
        };
        switch ((init.kind & 0xFF))
        {
            case 0:
                return visitVoid.invoke((VoidInitializer)init);
            case 1:
                return visitError.invoke((ErrorInitializer)init);
            case 2:
                return visitStruct.invoke((StructInitializer)init);
            case 3:
                return visitArray.invoke((ArrayInitializer)init);
            case 4:
                return visitExp.invoke((ExpInitializer)init);
            default:
            throw SwitchError.INSTANCE;
        }
    }

    public static Initializer inferType(Initializer init, Scope sc) {
        Ref<Scope> sc_ref = ref(sc);
        Function1<VoidInitializer,Initializer> visitVoid = new Function1<VoidInitializer,Initializer>(){
            public Initializer invoke(VoidInitializer i){
                error(i.loc, new BytePtr("cannot infer type from void initializer"));
                return new ErrorInitializer();
            }
        };
        Function1<ErrorInitializer,Initializer> visitError = new Function1<ErrorInitializer,Initializer>(){
            public Initializer invoke(ErrorInitializer i){
                return i;
            }
        };
        Function1<StructInitializer,Initializer> visitStruct = new Function1<StructInitializer,Initializer>(){
            public Initializer invoke(StructInitializer i){
                error(i.loc, new BytePtr("cannot infer type from struct initializer"));
                return new ErrorInitializer();
            }
        };
        Function1<ArrayInitializer,Initializer> visitArray = new Function1<ArrayInitializer,Initializer>(){
            public Initializer invoke(ArrayInitializer init){
                DArray<Expression> keys = null;
                DArray<Expression> values = null;
                try {
                    if (init.isAssociativeArray())
                    {
                        keys = new DArray<Expression>(init.value.length);
                        values = new DArray<Expression>(init.value.length);
                        {
                            int i = 0;
                        L_outer3:
                            for (; (i < init.value.length);i++){
                                Expression e = init.index.get(i);
                                if (e == null)
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                keys.set(i, e);
                                Initializer iz = init.value.get(i);
                                if (iz == null)
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                iz = inferType(iz, sc_ref.value);
                                if (iz.isErrorInitializer() != null)
                                {
                                    return iz;
                                }
                                assert(iz.isExpInitializer() != null);
                                values.set(i, ((ExpInitializer)iz).exp);
                                assert((((values).get(i).op & 0xFF) != 127));
                            }
                        }
                        Expression e = new AssocArrayLiteralExp(init.loc, keys, values);
                        ExpInitializer ei = new ExpInitializer(init.loc, e);
                        return inferType(ei, sc_ref.value);
                    }
                    else
                    {
                        DArray<Expression> elements = new DArray<Expression>(init.value.length);
                        (elements).zero();
                        {
                            int i = 0;
                        L_outer4:
                            for (; (i < init.value.length);i++){
                                assert(init.index.get(i) == null);
                                Initializer iz = init.value.get(i);
                                if (iz == null)
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                iz = inferType(iz, sc_ref.value);
                                if (iz.isErrorInitializer() != null)
                                {
                                    return iz;
                                }
                                assert(iz.isExpInitializer() != null);
                                elements.set(i, ((ExpInitializer)iz).exp);
                                assert((((elements).get(i).op & 0xFF) != 127));
                            }
                        }
                        Expression e = new ArrayLiteralExp(init.loc, null, elements);
                        ExpInitializer ei = new ExpInitializer(init.loc, e);
                        return inferType(ei, sc_ref.value);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lno:*/
                if (keys != null)
                {
                    error(init.loc, new BytePtr("not an associative array initializer"));
                }
                else
                {
                    error(init.loc, new BytePtr("cannot infer type from array initializer"));
                }
                return new ErrorInitializer();
            }
        };
        Function1<ExpInitializer,Initializer> visitExp = new Function1<ExpInitializer,Initializer>(){
            public Initializer invoke(ExpInitializer init){
                init.exp = expressionSemantic(init.exp, sc_ref.value);
                if (((init.exp.op & 0xFF) == 20))
                    init.exp = resolveAliasThis(sc_ref.value, init.exp, false);
                init.exp = resolveProperties(sc_ref.value, init.exp);
                if (((init.exp.op & 0xFF) == 203))
                {
                    ScopeExp se = (ScopeExp)init.exp;
                    TemplateInstance ti = se.sds.isTemplateInstance();
                    if ((ti != null) && (ti.semanticRun == PASS.semantic) && (ti.aliasdecl == null))
                        se.error(new BytePtr("cannot infer type from %s `%s`, possible circular dependency"), se.sds.kind(), se.toChars());
                    else
                        se.error(new BytePtr("cannot infer type from %s `%s`"), se.sds.kind(), se.toChars());
                    return new ErrorInitializer();
                }
                Ref<Boolean> hasOverloads = ref(false);
                {
                    FuncDeclaration f = isFuncAddress(init.exp, ptr(hasOverloads));
                    if ((f) != null)
                    {
                        if (f.checkForwardRef(init.loc))
                        {
                            return new ErrorInitializer();
                        }
                        if (hasOverloads.value && !f.isUnique())
                        {
                            init.exp.error(new BytePtr("cannot infer type from overloaded function symbol `%s`"), init.exp.toChars());
                            return new ErrorInitializer();
                        }
                    }
                }
                if (((init.exp.op & 0xFF) == 19))
                {
                    AddrExp ae = (AddrExp)init.exp;
                    if (((ae.e1.op & 0xFF) == 214))
                    {
                        init.exp.error(new BytePtr("cannot infer type from overloaded function symbol `%s`"), init.exp.toChars());
                        return new ErrorInitializer();
                    }
                }
                if (((init.exp.op & 0xFF) == 127))
                {
                    return new ErrorInitializer();
                }
                if (init.exp.type == null)
                {
                    return new ErrorInitializer();
                }
                return init;
            }
        };
        switch ((init.kind & 0xFF))
        {
            case 0:
                return visitVoid.invoke((VoidInitializer)init);
            case 1:
                return visitError.invoke((ErrorInitializer)init);
            case 2:
                return visitStruct.invoke((StructInitializer)init);
            case 3:
                return visitArray.invoke((ArrayInitializer)init);
            case 4:
                return visitExp.invoke((ExpInitializer)init);
            default:
            throw SwitchError.INSTANCE;
        }
    }

    public static Expression initializerToExpression(Initializer init, Type itype) {
        Ref<Type> itype_ref = ref(itype);
        Function1<VoidInitializer,Expression> visitVoid = new Function1<VoidInitializer,Expression>(){
            public Expression invoke(VoidInitializer _param_0){
                return null;
            }
        };
        Function1<ErrorInitializer,Expression> visitError = new Function1<ErrorInitializer,Expression>(){
            public Expression invoke(ErrorInitializer _param_0){
                return new ErrorExp();
            }
        };
        Function1<StructInitializer,Expression> visitStruct = new Function1<StructInitializer,Expression>(){
            public Expression invoke(StructInitializer _param_0){
                return null;
            }
        };
        Function1<ArrayInitializer,Expression> visitArray = new Function1<ArrayInitializer,Expression>(){
            public Expression invoke(ArrayInitializer init){
                DArray<Expression> elements = null;
                int edim = 0;
                int amax = -2147483648;
                Type t = null;
                try {
                    if (init.type != null)
                    {
                        if ((pequals(init.type, Type.terror)))
                        {
                            return new ErrorExp();
                        }
                        t = init.type.toBasetype();
                        {
                            int __dispatch3 = 0;
                            dispatched_3:
                            do {
                                switch (__dispatch3 != 0 ? __dispatch3 : (t.ty & 0xFF))
                                {
                                    case 41:
                                        t = ((TypeVector)t).basetype;
                                        /*goto case*/{ __dispatch3 = 1; continue dispatched_3; }
                                    case 1:
                                        __dispatch3 = 0;
                                        long adim = ((TypeSArray)t).dim.toInteger();
                                        if ((adim >= 2147483648L))
                                            /*goto Lno*/throw Dispatch0.INSTANCE;
                                        edim = (int)adim;
                                        break;
                                    case 3:
                                    case 0:
                                        edim = init.dim;
                                        break;
                                    default:
                                    throw new AssertionError("Unreachable code!");
                                }
                            } while(__dispatch3 != 0);
                        }
                    }
                    else
                    {
                        edim = init.value.length;
                        {
                            int i = 0;
                            int j = 0;
                        L_outer5:
                            for (; (i < init.value.length);comma(i++, j++)){
                                if (init.index.get(i) != null)
                                {
                                    if (((init.index.get(i).op & 0xFF) == 135))
                                    {
                                        long idxval = init.index.get(i).toInteger();
                                        if ((idxval >= 2147483648L))
                                            /*goto Lno*/throw Dispatch0.INSTANCE;
                                        j = (int)idxval;
                                    }
                                    else
                                        /*goto Lno*/throw Dispatch0.INSTANCE;
                                }
                                if ((j >= edim))
                                    edim = j + 1;
                            }
                        }
                    }
                    elements = new DArray<Expression>(edim);
                    (elements).zero();
                    {
                        int i = 0;
                        int j = 0;
                    L_outer6:
                        for (; (i < init.value.length);comma(i++, j++)){
                            if (init.index.get(i) != null)
                                j = (int)init.index.get(i).toInteger();
                            assert((j < edim));
                            Initializer iz = init.value.get(i);
                            if (iz == null)
                                /*goto Lno*/throw Dispatch0.INSTANCE;
                            Expression ex = initializerToExpression(iz, null);
                            if (ex == null)
                            {
                                /*goto Lno*/throw Dispatch0.INSTANCE;
                            }
                            elements.set(j, ex);
                        }
                    }
                    {
                        Expression _init = null;
                        {
                            int i = 0;
                        L_outer7:
                            for (; (i < edim);i++){
                                if ((elements).get(i) == null)
                                {
                                    if (init.type == null)
                                        /*goto Lno*/throw Dispatch0.INSTANCE;
                                    if (_init == null)
                                        _init = defaultInit(((TypeNext)t).next, Loc.initial);
                                    elements.set(i, _init);
                                }
                            }
                        }
                        if (t != null)
                        {
                            Type tn = t.nextOf().toBasetype();
                            if (((tn.ty & 0xFF) == ENUMTY.Tsarray))
                            {
                                int dim = (int)((TypeSArray)tn).dim.toInteger();
                                Type te = tn.nextOf().toBasetype();
                                {
                                    Slice<Expression> __r1503 = (elements).opSlice().copy();
                                    int __key1504 = 0;
                                    for (; (__key1504 < __r1503.getLength());__key1504 += 1) {
                                        Expression e = __r1503.get(__key1504);
                                        if (te.equals(e.type))
                                        {
                                            DArray<Expression> elements2 = new DArray<Expression>(dim);
                                            {
                                                Slice<Expression> __r1505 = (elements2).opSlice().copy();
                                                int __key1506 = 0;
                                                for (; (__key1506 < __r1505.getLength());__key1506 += 1) {
                                                    Expression e2 = __r1505.get(__key1506);
                                                    e2 = e;
                                                }
                                            }
                                            e = new ArrayLiteralExp(e.loc, tn, elements2);
                                        }
                                    }
                                }
                            }
                        }
                        {
                            int i = 0;
                            for (; (i < edim);i++){
                                Expression e = (elements).get(i);
                                if (((e.op & 0xFF) == 127))
                                {
                                    return e;
                                }
                            }
                        }
                        Expression e = new ArrayLiteralExp(init.loc, init.type, elements);
                        return e;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lno:*/
                return null;
            }
        };
        Function1<ExpInitializer,Expression> visitExp = new Function1<ExpInitializer,Expression>(){
            public Expression invoke(ExpInitializer i){
                if (itype_ref.value != null)
                {
                    Type tb = itype_ref.value.toBasetype();
                    Expression e = ((i.exp.op & 0xFF) == 95) || ((i.exp.op & 0xFF) == 96) ? ((AssignExp)i.exp).e2 : i.exp;
                    if (((tb.ty & 0xFF) == ENUMTY.Tsarray) && (e.implicitConvTo(tb.nextOf()) != 0))
                    {
                        TypeSArray tsa = (TypeSArray)tb;
                        int d = (int)tsa.dim.toInteger();
                        DArray<Expression> elements = new DArray<Expression>(d);
                        {
                            int j = 0;
                            for (; (j < d);j++) {
                                elements.set(j, e);
                            }
                        }
                        ArrayLiteralExp ae = new ArrayLiteralExp(e.loc, itype_ref.value, elements);
                        return ae;
                    }
                }
                return i.exp;
            }
        };
        switch ((init.kind & 0xFF))
        {
            case 0:
                return visitVoid.invoke((VoidInitializer)init);
            case 1:
                return visitError.invoke((ErrorInitializer)init);
            case 2:
                return visitStruct.invoke((StructInitializer)init);
            case 3:
                return visitArray.invoke((ArrayInitializer)init);
            case 4:
                return visitExp.invoke((ExpInitializer)init);
            default:
            throw SwitchError.INSTANCE;
        }
    }

}
