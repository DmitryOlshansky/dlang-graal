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
        Ptr<DArray<Expression>> keys = refPtr(new DArray<Expression>(dim));
        Ptr<DArray<Expression>> values = refPtr(new DArray<Expression>(dim));
        try {
            {
                int i = 0;
            L_outer1:
                for (; (i < dim);i++){
                    e = ai.index.get(i);
                    if (e == null)
                    {
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    }
                    keys.get().set(i, e);
                    Initializer iz = ai.value.get(i);
                    if (iz == null)
                    {
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    }
                    e = initializerToExpression(iz, null);
                    if (e == null)
                    {
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    }
                    values.get().set(i, e);
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

    public static Initializer initializerSemantic(Initializer init, Ptr<Scope> sc, Type t, int needInterpret) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<Type> t_ref = ref(t);
        Function1<VoidInitializer,Initializer> visitVoid = (i) -> {
         {
            i.type = t_ref.value;
            return i;
        }
        };
        Function1<ErrorInitializer,Initializer> visitError = (i) -> {
         {
            return i;
        }
        };
        Function1<StructInitializer,Initializer> visitStruct = (i) -> {
         {
            t_ref.value = t_ref.value.toBasetype();
            if (((t_ref.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t_ref.value.nextOf().toBasetype().ty & 0xFF) == ENUMTY.Tstruct))
            {
                t_ref.value = t_ref.value.nextOf().toBasetype();
            }
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
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(nfields));
                {
                    int j = 0;
                    for (; (j < (elements.get()).length);j++) {
                        elements.get().set(j, null);
                    }
                }
                Ref<Boolean> errors = ref(false);
                {
                    Ref<Integer> fieldi = ref(0);
                    int j = 0;
                    for (; (j < i.field.length);j++){
                        {
                            Identifier id = i.field.get(j);
                            if ((id) != null)
                            {
                                Ref<Dsymbol> s = ref(sd.search(i.loc, id, 8));
                                if (s.value == null)
                                {
                                    s.value = sd.search_correct(id);
                                    Loc initLoc = i.value.get(j).loc.copy();
                                    if (s.value != null)
                                    {
                                        error(initLoc, new BytePtr("`%s` is not a member of `%s`, did you mean %s `%s`?"), id.toChars(), sd.toChars(), s.value.kind(), s.value.toChars());
                                    }
                                    else
                                    {
                                        error(initLoc, new BytePtr("`%s` is not a member of `%s`"), id.toChars(), sd.toChars());
                                    }
                                    return new ErrorInitializer();
                                }
                                s.value = s.value.toAlias();
                                {
                                    fieldi.value = 0;
                                    for (; 1 != 0;fieldi.value++){
                                        if ((fieldi.value >= nfields))
                                        {
                                            error(i.loc, new BytePtr("`%s.%s` is not a per-instance initializable field"), sd.toChars(), s.value.toChars());
                                            return new ErrorInitializer();
                                        }
                                        if ((pequals(s.value, sd.fields.get(fieldi.value))))
                                        {
                                            break;
                                        }
                                    }
                                }
                            }
                            else if ((fieldi.value >= nfields))
                            {
                                error(i.loc, new BytePtr("too many initializers for `%s`"), sd.toChars());
                                return new ErrorInitializer();
                            }
                        }
                        VarDeclaration vd = sd.fields.get(fieldi.value);
                        if ((elements.get()).get(fieldi.value) != null)
                        {
                            error(i.loc, new BytePtr("duplicate initializer for field `%s`"), vd.toChars());
                            errors.value = true;
                            continue;
                        }
                        if (vd.type.hasPointers())
                        {
                            if ((t_ref.value.alignment() < target.ptrsize) || ((vd.offset & target.ptrsize - 1) != 0) && ((sc_ref.value.get()).func != null) && (sc_ref.value.get()).func.setUnsafe())
                            {
                                error(i.loc, new BytePtr("field `%s.%s` cannot assign to misaligned pointers in `@safe` code"), sd.toChars(), vd.toChars());
                                errors.value = true;
                            }
                        }
                        {
                            int k = 0;
                            for (; (k < nfields);k++){
                                VarDeclaration v2 = sd.fields.get(k);
                                if (vd.isOverlappedWith(v2) && ((elements.get()).get(k) != null))
                                {
                                    error(i.loc, new BytePtr("overlapping initialization for field `%s` and `%s`"), v2.toChars(), vd.toChars());
                                    errors.value = true;
                                    continue;
                                }
                            }
                        }
                        assert(sc_ref.value != null);
                        Ref<Initializer> iz = ref(i.value.get(j));
                        iz.value = initializerSemantic(iz.value, sc_ref.value, vd.type.addMod(t_ref.value.mod), needInterpret);
                        Expression ex = initializerToExpression(iz.value, null);
                        if (((ex.op & 0xFF) == 127))
                        {
                            errors.value = true;
                            continue;
                        }
                        i.value.set(j, iz.value);
                        elements.get().set(fieldi.value, doCopyOrMove(sc_ref.value, ex, null));
                        fieldi.value += 1;
                    }
                }
                if (errors.value)
                {
                    return new ErrorInitializer();
                }
                StructLiteralExp sle = new StructLiteralExp(i.loc, sd, elements, t_ref.value);
                if (!sd.fill(i.loc, elements, false))
                {
                    return new ErrorInitializer();
                }
                sle.type.value = t_ref.value;
                ExpInitializer ie = new ExpInitializer(i.loc, sle);
                return initializerSemantic(ie, sc_ref.value, t_ref.value, needInterpret);
            }
            else if (((t_ref.value.ty & 0xFF) == ENUMTY.Tdelegate) || ((t_ref.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t_ref.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && (i.value.length == 0))
            {
                byte tok = ((t_ref.value.ty & 0xFF) == ENUMTY.Tdelegate) ? TOK.delegate_ : TOK.function_;
                Type tf = new TypeFunction(new ParameterList(null, VarArg.none), null, LINK.d, 0L);
                FuncLiteralDeclaration fd = new FuncLiteralDeclaration(i.loc, Loc.initial, tf, tok, null, null);
                fd.fbody = new CompoundStatement(i.loc, refPtr(new DArray<Statement>()));
                fd.endloc = i.loc.copy();
                Expression e = new FuncExp(i.loc, fd);
                ExpInitializer ie = new ExpInitializer(i.loc, e);
                return initializerSemantic(ie, sc_ref.value, t_ref.value, needInterpret);
            }
            if (((t_ref.value.ty & 0xFF) != ENUMTY.Terror))
            {
                error(i.loc, new BytePtr("a struct is not a valid initializer for a `%s`"), t_ref.value.toChars());
            }
            return new ErrorInitializer();
        }
        };
        Function1<ArrayInitializer,Initializer> visitArray = (i) -> {
         {
            Ref<Integer> length = ref(0);
            int amax = -2147483648;
            Ref<Boolean> errors = ref(false);
            if (i.sem)
            {
                return i;
            }
            i.sem = true;
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
                                Ref<Expression> e = ref(null);
                                if (((t_ref.value.ty & 0xFF) == ENUMTY.Taarray) || i.isAssociativeArray())
                                {
                                    e.value = toAssocArrayLiteral(i);
                                }
                                else
                                {
                                    e.value = initializerToExpression(i, null);
                                }
                                if (e.value == null)
                                {
                                    error(i.loc, new BytePtr("cannot use array to initialize `%s`"), t_ref.value.toChars());
                                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                                }
                                ExpInitializer ei = new ExpInitializer(e.value.loc, e.value);
                                return initializerSemantic(ei, sc_ref.value, t_ref.value, needInterpret);
                            case 3:
                                if (((t_ref.value.nextOf().ty & 0xFF) != ENUMTY.Tfunction))
                                {
                                    break;
                                }
                                /*goto default*/ { __dispatch0 = -2; continue dispatched_0; }
                            default:
                            __dispatch0 = 0;
                            error(i.loc, new BytePtr("cannot use array to initialize `%s`"), t_ref.value.toChars());
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                    } while(__dispatch0 != 0);
                }
                i.type = t_ref.value;
                length.value = 0;
                {
                    int j = 0;
                L_outer2:
                    for (; (j < i.index.length);j++){
                        Ref<Expression> idx = ref(i.index.get(j));
                        if (idx.value != null)
                        {
                            sc_ref.value = (sc_ref.value.get()).startCTFE();
                            idx.value = expressionSemantic(idx.value, sc_ref.value);
                            sc_ref.value = (sc_ref.value.get()).endCTFE();
                            idx.value = idx.value.ctfeInterpret();
                            i.index.set(j, idx.value);
                            long idxvalue = idx.value.toInteger();
                            if ((idxvalue >= 2147483648L))
                            {
                                error(i.loc, new BytePtr("array index %llu overflow"), idxvalue);
                                errors.value = true;
                            }
                            length.value = (int)idxvalue;
                            if (((idx.value.op & 0xFF) == 127))
                            {
                                errors.value = true;
                            }
                        }
                        Ref<Initializer> val = ref(i.value.get(j));
                        Ref<ExpInitializer> ei = ref(val.value.isExpInitializer());
                        if ((ei.value != null) && (idx.value == null))
                        {
                            ei.value.expandTuples = true;
                        }
                        val.value = initializerSemantic(val.value, sc_ref.value, t_ref.value.nextOf(), needInterpret);
                        if (val.value.isErrorInitializer() != null)
                        {
                            errors.value = true;
                        }
                        ei.value = val.value.isExpInitializer();
                        if ((ei.value != null) && ((ei.value.exp.op & 0xFF) == 126))
                        {
                            TupleExp te = (TupleExp)ei.value.exp;
                            i.index.remove(j);
                            i.value.remove(j);
                            {
                                Ref<Integer> k = ref(0);
                                for (; (k.value < (te.exps.get()).length);k.value += 1){
                                    Expression e = (te.exps.get()).get(k.value);
                                    i.index.insert(j + k.value, null);
                                    i.value.insert(j + k.value, new ExpInitializer(e.loc, e));
                                }
                            }
                            j--;
                            continue L_outer2;
                        }
                        else
                        {
                            i.value.set(j, val.value);
                        }
                        length.value++;
                        if ((length.value == 0))
                        {
                            error(i.loc, new BytePtr("array dimension overflow"));
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                        if ((length.value > i.dim))
                        {
                            i.dim = length.value;
                        }
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
                if (errors.value)
                {
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
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
        Function1<ExpInitializer,Initializer> visitExp = (i) -> {
         {
            if (needInterpret != 0)
            {
                sc_ref.value = (sc_ref.value.get()).startCTFE();
            }
            i.exp = expressionSemantic(i.exp, sc_ref.value);
            i.exp = resolveProperties(sc_ref.value, i.exp);
            if (needInterpret != 0)
            {
                sc_ref.value = (sc_ref.value.get()).endCTFE();
            }
            if (((i.exp.op & 0xFF) == 127))
            {
                return new ErrorInitializer();
            }
            int olderrors = global.errors;
            if (needInterpret != 0)
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
                {
                    error(i.loc, new BytePtr("variables cannot be initialized with an expression of type `void`. Use `void` initialization instead."));
                }
            }
            else
            {
                i.exp = i.exp.optimize(0, false);
            }
            if ((global.gag == 0) && (olderrors != global.errors))
            {
                return i;
            }
            if (((i.exp.type.value.ty & 0xFF) == ENUMTY.Ttuple) && ((((TypeTuple)i.exp.type.value).arguments.get()).length == 0))
            {
                Type et = i.exp.type.value;
                i.exp = new TupleExp(i.exp.loc, refPtr(new DArray<Expression>()));
                i.exp.type.value = et;
            }
            if (((i.exp.op & 0xFF) == 20))
            {
                i.exp.error(new BytePtr("initializer must be an expression, not `%s`"), i.exp.toChars());
                return new ErrorInitializer();
            }
            if ((needInterpret != 0) && hasNonConstPointers(i.exp))
            {
                i.exp.error(new BytePtr("cannot use non-constant CTFE pointer in an initializer `%s`"), i.exp.toChars());
                return new ErrorInitializer();
            }
            Type tb = t_ref.value.toBasetype();
            Type ti = i.exp.type.value.toBasetype();
            if (((i.exp.op & 0xFF) == 126) && i.expandTuples && (i.exp.implicitConvTo(t_ref.value) == 0))
            {
                return new ExpInitializer(i.loc, i.exp);
            }
            try {
                if (((i.exp.op & 0xFF) == 121) && ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    StringExp se = (StringExp)i.exp;
                    Type typeb = se.type.value.toBasetype();
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
                        Ref<Expression> e = ref(null);
                        e.value = new StructLiteralExp(i.loc, sd, null, null);
                        e.value = new DotIdExp(i.loc, e.value, Id.ctor);
                        e.value = new CallExp(i.loc, e.value, i.exp);
                        e.value = expressionSemantic(e.value, sc_ref.value);
                        if (needInterpret != 0)
                        {
                            i.exp = e.value.ctfeInterpret();
                        }
                        else
                        {
                            i.exp = e.value.optimize(0, false);
                        }
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
                        Ref<Long> dim2 = ref(dim1);
                        if (((i.exp.op & 0xFF) == 47))
                        {
                            ArrayLiteralExp ale = (ArrayLiteralExp)i.exp;
                            dim2.value = ale.elements != null ? (long)(ale.elements.get()).length : 0L;
                        }
                        else if (((i.exp.op & 0xFF) == 31))
                        {
                            Type tx = toStaticArrayType((SliceExp)i.exp);
                            if (tx != null)
                            {
                                dim2.value = ((TypeSArray)tx).dim.toInteger();
                            }
                        }
                        if ((dim1 != dim2.value))
                        {
                            i.exp.error(new BytePtr("mismatched array lengths, %d and %d"), (int)dim1, (int)dim2.value);
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
            if (needInterpret != 0)
            {
                i.exp = i.exp.ctfeInterpret();
            }
            else
            {
                i.exp = i.exp.optimize(0, false);
            }
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

    public static Initializer inferType(Initializer init, Ptr<Scope> sc) {
        Function1<VoidInitializer,Initializer> visitVoid = (i) -> {
         {
            error(i.loc, new BytePtr("cannot infer type from void initializer"));
            return new ErrorInitializer();
        }
        };
        Function1<ErrorInitializer,Initializer> visitError = (i) -> {
         {
            return i;
        }
        };
        Function1<StructInitializer,Initializer> visitStruct = (i) -> {
         {
            error(i.loc, new BytePtr("cannot infer type from struct initializer"));
            return new ErrorInitializer();
        }
        };
        Function1<ArrayInitializer,Initializer> visitArray = (init) -> {
         {
            Ref<Ptr<DArray<Expression>>> keys = ref(null);
            Ref<Ptr<DArray<Expression>>> values = ref(null);
            try {
                if (init.isAssociativeArray())
                {
                    keys.value = refPtr(new DArray<Expression>(init.value.length));
                    values.value = refPtr(new DArray<Expression>(init.value.length));
                    {
                        int i = 0;
                    L_outer3:
                        for (; (i < init.value.length);i++){
                            Expression e = init.index.get(i);
                            if (e == null)
                            {
                                /*goto Lno*/throw Dispatch0.INSTANCE;
                            }
                            keys.value.get().set(i, e);
                            Ref<Initializer> iz = ref(init.value.get(i));
                            if (iz.value == null)
                            {
                                /*goto Lno*/throw Dispatch0.INSTANCE;
                            }
                            iz.value = inferType(iz.value, sc);
                            if (iz.value.isErrorInitializer() != null)
                            {
                                return iz.value;
                            }
                            assert(iz.value.isExpInitializer() != null);
                            values.value.get().set(i, ((ExpInitializer)iz.value).exp);
                            assert((((values.value.get()).get(i).op & 0xFF) != 127));
                        }
                    }
                    Expression e = new AssocArrayLiteralExp(init.loc, keys.value, values.value);
                    ExpInitializer ei = new ExpInitializer(init.loc, e);
                    return inferType(ei, sc);
                }
                else
                {
                    Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(init.value.length));
                    (elements.get()).zero();
                    {
                        int i = 0;
                    L_outer4:
                        for (; (i < init.value.length);i++){
                            assert(init.index.get(i) == null);
                            Ref<Initializer> iz = ref(init.value.get(i));
                            if (iz.value == null)
                            {
                                /*goto Lno*/throw Dispatch0.INSTANCE;
                            }
                            iz.value = inferType(iz.value, sc);
                            if (iz.value.isErrorInitializer() != null)
                            {
                                return iz.value;
                            }
                            assert(iz.value.isExpInitializer() != null);
                            elements.get().set(i, ((ExpInitializer)iz.value).exp);
                            assert((((elements.get()).get(i).op & 0xFF) != 127));
                        }
                    }
                    Expression e = new ArrayLiteralExp(init.loc, null, elements);
                    ExpInitializer ei = new ExpInitializer(init.loc, e);
                    return inferType(ei, sc);
                }
            }
            catch(Dispatch0 __d){}
        /*Lno:*/
            if (keys.value != null)
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
        Function1<ExpInitializer,Initializer> visitExp = (init) -> {
         {
            init.exp = expressionSemantic(init.exp, sc);
            if (((init.exp.op & 0xFF) == 20))
            {
                init.exp = resolveAliasThis(sc, init.exp, false);
            }
            init.exp = resolveProperties(sc, init.exp);
            if (((init.exp.op & 0xFF) == 203))
            {
                ScopeExp se = (ScopeExp)init.exp;
                TemplateInstance ti = se.sds.isTemplateInstance();
                if ((ti != null) && (ti.semanticRun == PASS.semantic) && (ti.aliasdecl == null))
                {
                    se.error(new BytePtr("cannot infer type from %s `%s`, possible circular dependency"), se.sds.kind(), se.toChars());
                }
                else
                {
                    se.error(new BytePtr("cannot infer type from %s `%s`"), se.sds.kind(), se.toChars());
                }
                return new ErrorInitializer();
            }
            boolean hasOverloads = false;
            {
                FuncDeclaration f = isFuncAddress(init.exp, ptr(hasOverloads));
                if ((f) != null)
                {
                    if (f.checkForwardRef(init.loc))
                    {
                        return new ErrorInitializer();
                    }
                    if (hasOverloads && !f.isUnique())
                    {
                        init.exp.error(new BytePtr("cannot infer type from overloaded function symbol `%s`"), init.exp.toChars());
                        return new ErrorInitializer();
                    }
                }
            }
            if (((init.exp.op & 0xFF) == 19))
            {
                AddrExp ae = (AddrExp)init.exp;
                if (((ae.e1.value.op & 0xFF) == 214))
                {
                    init.exp.error(new BytePtr("cannot infer type from overloaded function symbol `%s`"), init.exp.toChars());
                    return new ErrorInitializer();
                }
            }
            if (((init.exp.op & 0xFF) == 127))
            {
                return new ErrorInitializer();
            }
            if (init.exp.type.value == null)
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
        Function1<VoidInitializer,Expression> visitVoid = (_param_0) -> {
         {
            return null;
        }
        };
        Function1<ErrorInitializer,Expression> visitError = (_param_0) -> {
         {
            return new ErrorExp();
        }
        };
        Function1<StructInitializer,Expression> visitStruct = (_param_0) -> {
         {
            return null;
        }
        };
        Function1<ArrayInitializer,Expression> visitArray = (init) -> {
         {
            Ref<Ptr<DArray<Expression>>> elements = ref(null);
            Ref<Integer> edim = ref(0);
            int amax = -2147483648;
            Ref<Type> t = ref(null);
            try {
                if (init.type != null)
                {
                    if ((pequals(init.type, Type.terror)))
                    {
                        return new ErrorExp();
                    }
                    t.value = init.type.toBasetype();
                    {
                        int __dispatch3 = 0;
                        dispatched_3:
                        do {
                            switch (__dispatch3 != 0 ? __dispatch3 : (t.value.ty & 0xFF))
                            {
                                case 41:
                                    t.value = ((TypeVector)t.value).basetype;
                                    /*goto case*/{ __dispatch3 = 1; continue dispatched_3; }
                                case 1:
                                    __dispatch3 = 0;
                                    long adim = ((TypeSArray)t.value).dim.toInteger();
                                    if ((adim >= 2147483648L))
                                    {
                                        /*goto Lno*/throw Dispatch0.INSTANCE;
                                    }
                                    edim.value = (int)adim;
                                    break;
                                case 3:
                                case 0:
                                    edim.value = init.dim;
                                    break;
                                default:
                                throw new AssertionError("Unreachable code!");
                            }
                        } while(__dispatch3 != 0);
                    }
                }
                else
                {
                    edim.value = init.value.length;
                    {
                        int i = 0;
                        Ref<Integer> j = ref(0);
                    L_outer5:
                        for (; (i < init.value.length);comma(i++, j.value++)){
                            if (init.index.get(i) != null)
                            {
                                if (((init.index.get(i).op & 0xFF) == 135))
                                {
                                    long idxval = init.index.get(i).toInteger();
                                    if ((idxval >= 2147483648L))
                                    {
                                        /*goto Lno*/throw Dispatch0.INSTANCE;
                                    }
                                    j.value = (int)idxval;
                                }
                                else
                                {
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                }
                            }
                            if ((j.value >= edim.value))
                            {
                                edim.value = j.value + 1;
                            }
                        }
                    }
                }
                elements.value = refPtr(new DArray<Expression>(edim.value));
                (elements.value.get()).zero();
                {
                    int i = 0;
                    Ref<Integer> j = ref(0);
                L_outer6:
                    for (; (i < init.value.length);comma(i++, j.value++)){
                        if (init.index.get(i) != null)
                        {
                            j.value = (int)init.index.get(i).toInteger();
                        }
                        assert((j.value < edim.value));
                        Initializer iz = init.value.get(i);
                        if (iz == null)
                        {
                            /*goto Lno*/throw Dispatch0.INSTANCE;
                        }
                        Expression ex = initializerToExpression(iz, null);
                        if (ex == null)
                        {
                            /*goto Lno*/throw Dispatch0.INSTANCE;
                        }
                        elements.value.get().set(j.value, ex);
                    }
                }
                {
                    Ref<Expression> _init = ref(null);
                    {
                        int i = 0;
                    L_outer7:
                        for (; (i < edim.value);i++){
                            if ((elements.value.get()).get(i) == null)
                            {
                                if (init.type == null)
                                {
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                }
                                if (_init.value == null)
                                {
                                    _init.value = defaultInit(((TypeNext)t.value).next.value, Loc.initial);
                                }
                                elements.value.get().set(i, _init.value);
                            }
                        }
                    }
                    if (t.value != null)
                    {
                        Type tn = t.value.nextOf().toBasetype();
                        if (((tn.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            int dim = (int)((TypeSArray)tn).dim.toInteger();
                            Type te = tn.nextOf().toBasetype();
                            {
                                Slice<Expression> __r1499 = (elements.value.get()).opSlice().copy();
                                Ref<Integer> __key1500 = ref(0);
                                for (; (__key1500.value < __r1499.getLength());__key1500.value += 1) {
                                    Ref<Expression> e = ref(__r1499.get(__key1500.value));
                                    if (te.equals(e.value.type.value))
                                    {
                                        Ptr<DArray<Expression>> elements2 = refPtr(new DArray<Expression>(dim));
                                        {
                                            Slice<Expression> __r1501 = (elements2.get()).opSlice().copy();
                                            Ref<Integer> __key1502 = ref(0);
                                            for (; (__key1502.value < __r1501.getLength());__key1502.value += 1) {
                                                Ref<Expression> e2 = ref(__r1501.get(__key1502.value));
                                                e2.value = e.value;
                                            }
                                        }
                                        e.value = new ArrayLiteralExp(e.value.loc, tn, elements2);
                                    }
                                }
                            }
                        }
                    }
                    {
                        int i = 0;
                        for (; (i < edim.value);i++){
                            Expression e = (elements.value.get()).get(i);
                            if (((e.op & 0xFF) == 127))
                            {
                                return e;
                            }
                        }
                    }
                    Expression e = new ArrayLiteralExp(init.loc, init.type, elements.value);
                    return e;
                }
            }
            catch(Dispatch0 __d){}
        /*Lno:*/
            return null;
        }
        };
        Function1<ExpInitializer,Expression> visitExp = (i) -> {
         {
            if (itype != null)
            {
                Type tb = itype.toBasetype();
                Expression e = ((i.exp.op & 0xFF) == 95) || ((i.exp.op & 0xFF) == 96) ? ((AssignExp)i.exp).e2.value : i.exp;
                if (((tb.ty & 0xFF) == ENUMTY.Tsarray) && (e.implicitConvTo(tb.nextOf()) != 0))
                {
                    TypeSArray tsa = (TypeSArray)tb;
                    int d = (int)tsa.dim.toInteger();
                    Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(d));
                    {
                        int j = 0;
                        for (; (j < d);j++) {
                            elements.get().set(j, e);
                        }
                    }
                    ArrayLiteralExp ae = new ArrayLiteralExp(e.loc, itype, elements);
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

    // defaulted all parameters starting with #2
    public static Expression initializerToExpression(Initializer init) {
        return initializerToExpression(init, null);
    }

}
