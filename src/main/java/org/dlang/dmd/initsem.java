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
        Ptr<DArray<Expression>> keys = new DArray<Expression>(dim);
        Ptr<DArray<Expression>> values = new DArray<Expression>(dim);
        try {
            {
                int i = 0;
            L_outer1:
                for (; (i < dim);i++){
                    e = ai.index.get(i);
                    if (e == null)
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    keys.get().set(i, e);
                    Initializer iz = ai.value.get(i);
                    if (iz == null)
                        /*goto Lno*/throw Dispatch0.INSTANCE;
                    e = initializerToExpression(iz, null);
                    if (e == null)
                        /*goto Lno*/throw Dispatch0.INSTANCE;
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
        IntRef needInterpret_ref = ref(needInterpret);
        Function1<VoidInitializer,Initializer> visitVoid = new Function1<VoidInitializer,Initializer>(){
            public Initializer invoke(VoidInitializer i) {
                Ref<VoidInitializer> i_ref = ref(i);
                i_ref.value.type = t_ref.value;
                return i_ref.value;
            }
        };
        Function1<ErrorInitializer,Initializer> visitError = new Function1<ErrorInitializer,Initializer>(){
            public Initializer invoke(ErrorInitializer i) {
                Ref<ErrorInitializer> i_ref = ref(i);
                return i_ref.value;
            }
        };
        Function1<StructInitializer,Initializer> visitStruct = new Function1<StructInitializer,Initializer>(){
            public Initializer invoke(StructInitializer i) {
                Ref<StructInitializer> i_ref = ref(i);
                t_ref.value = t_ref.value.toBasetype();
                if (((t_ref.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t_ref.value.nextOf().toBasetype().ty & 0xFF) == ENUMTY.Tstruct))
                    t_ref.value = t_ref.value.nextOf().toBasetype();
                if (((t_ref.value.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    Ref<StructDeclaration> sd = ref(((TypeStruct)t_ref.value).sym);
                    if (sd.value.ctor != null)
                    {
                        error(i_ref.value.loc, new BytePtr("%s `%s` has constructors, cannot use `{ initializers }`, use `%s( initializers )` instead"), sd.value.kind(), sd.value.toChars(), sd.value.toChars());
                        return new ErrorInitializer();
                    }
                    sd.value.size(i_ref.value.loc);
                    if ((sd.value.sizeok != Sizeok.done))
                    {
                        return new ErrorInitializer();
                    }
                    IntRef nfields = ref(sd.value.nonHiddenFields());
                    Ref<Ptr<DArray<Expression>>> elements = ref(new DArray<Expression>(nfields.value));
                    {
                        IntRef j = ref(0);
                        for (; (j.value < (elements.value.get()).length);j.value++) {
                            elements.value.get().set(j.value, null);
                        }
                    }
                    Ref<Boolean> errors = ref(false);
                    {
                        IntRef fieldi = ref(0);
                        IntRef j = ref(0);
                        for (; (j.value < i_ref.value.field.length);j.value++){
                            {
                                Ref<Identifier> id = ref(i_ref.value.field.get(j.value));
                                if ((id.value) != null)
                                {
                                    Ref<Dsymbol> s = ref(sd.value.search(i_ref.value.loc, id.value, 8));
                                    if (s.value == null)
                                    {
                                        s.value = sd.value.search_correct(id.value);
                                        Loc initLoc = i_ref.value.value.get(j.value).loc.copy();
                                        if (s.value != null)
                                            error(initLoc, new BytePtr("`%s` is not a member of `%s`, did you mean %s `%s`?"), id.value.toChars(), sd.value.toChars(), s.value.kind(), s.value.toChars());
                                        else
                                            error(initLoc, new BytePtr("`%s` is not a member of `%s`"), id.value.toChars(), sd.value.toChars());
                                        return new ErrorInitializer();
                                    }
                                    s.value = s.value.toAlias();
                                    {
                                        fieldi.value = 0;
                                        for (; 1 != 0;fieldi.value++){
                                            if ((fieldi.value >= nfields.value))
                                            {
                                                error(i_ref.value.loc, new BytePtr("`%s.%s` is not a per-instance initializable field"), sd.value.toChars(), s.value.toChars());
                                                return new ErrorInitializer();
                                            }
                                            if ((pequals(s.value, sd.value.fields.get(fieldi.value))))
                                                break;
                                        }
                                    }
                                }
                                else if ((fieldi.value >= nfields.value))
                                {
                                    error(i_ref.value.loc, new BytePtr("too many initializers for `%s`"), sd.value.toChars());
                                    return new ErrorInitializer();
                                }
                            }
                            Ref<VarDeclaration> vd = ref(sd.value.fields.get(fieldi.value));
                            if ((elements.value.get()).get(fieldi.value) != null)
                            {
                                error(i_ref.value.loc, new BytePtr("duplicate initializer for field `%s`"), vd.value.toChars());
                                errors.value = true;
                                continue;
                            }
                            if (vd.value.type.hasPointers())
                            {
                                if ((t_ref.value.alignment() < target.value.ptrsize) || ((vd.value.offset & target.value.ptrsize - 1) != 0) && ((sc_ref.value.get()).func != null) && (sc_ref.value.get()).func.setUnsafe())
                                {
                                    error(i_ref.value.loc, new BytePtr("field `%s.%s` cannot assign to misaligned pointers in `@safe` code"), sd.value.toChars(), vd.value.toChars());
                                    errors.value = true;
                                }
                            }
                            {
                                IntRef k = ref(0);
                                for (; (k.value < nfields.value);k.value++){
                                    Ref<VarDeclaration> v2 = ref(sd.value.fields.get(k.value));
                                    if (vd.value.isOverlappedWith(v2.value) && ((elements.value.get()).get(k.value) != null))
                                    {
                                        error(i_ref.value.loc, new BytePtr("overlapping initialization for field `%s` and `%s`"), v2.value.toChars(), vd.value.toChars());
                                        errors.value = true;
                                        continue;
                                    }
                                }
                            }
                            assert(sc_ref.value != null);
                            Ref<Initializer> iz = ref(i_ref.value.value.get(j.value));
                            iz.value = initializerSemantic(iz.value, sc_ref.value, vd.value.type.addMod(t_ref.value.mod), needInterpret_ref.value);
                            Ref<Expression> ex = ref(initializerToExpression(iz.value, null));
                            if (((ex.value.op & 0xFF) == 127))
                            {
                                errors.value = true;
                                continue;
                            }
                            i_ref.value.value.set(j.value, iz.value);
                            elements.value.get().set(fieldi.value, doCopyOrMove(sc_ref.value, ex.value, null));
                            fieldi.value += 1;
                        }
                    }
                    if (errors.value)
                    {
                        return new ErrorInitializer();
                    }
                    Ref<StructLiteralExp> sle = ref(new StructLiteralExp(i_ref.value.loc, sd.value, elements.value, t_ref.value));
                    if (!sd.value.fill(i_ref.value.loc, elements.value, false))
                    {
                        return new ErrorInitializer();
                    }
                    sle.value.type.value = t_ref.value;
                    Ref<ExpInitializer> ie = ref(new ExpInitializer(i_ref.value.loc, sle.value));
                    return initializerSemantic(ie.value, sc_ref.value, t_ref.value, needInterpret_ref.value);
                }
                else if (((t_ref.value.ty & 0xFF) == ENUMTY.Tdelegate) || ((t_ref.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t_ref.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && (i_ref.value.value.length == 0))
                {
                    Ref<Byte> tok = ref(((t_ref.value.ty & 0xFF) == ENUMTY.Tdelegate) ? TOK.delegate_ : TOK.function_);
                    Ref<Type> tf = ref(new TypeFunction(new ParameterList(null, VarArg.none), null, LINK.d, 0L));
                    Ref<FuncLiteralDeclaration> fd = ref(new FuncLiteralDeclaration(i_ref.value.loc, Loc.initial.value, tf.value, tok.value, null, null));
                    fd.value.fbody = new CompoundStatement(i_ref.value.loc, new DArray<Statement>());
                    fd.value.endloc = i_ref.value.loc.copy();
                    Ref<Expression> e = ref(new FuncExp(i_ref.value.loc, fd.value));
                    Ref<ExpInitializer> ie = ref(new ExpInitializer(i_ref.value.loc, e.value));
                    return initializerSemantic(ie.value, sc_ref.value, t_ref.value, needInterpret_ref.value);
                }
                if (((t_ref.value.ty & 0xFF) != ENUMTY.Terror))
                    error(i_ref.value.loc, new BytePtr("a struct is not a valid initializer for a `%s`"), t_ref.value.toChars());
                return new ErrorInitializer();
            }
        };
        Function1<ArrayInitializer,Initializer> visitArray = new Function1<ArrayInitializer,Initializer>(){
            public Initializer invoke(ArrayInitializer i) {
                Ref<ArrayInitializer> i_ref = ref(i);
                IntRef length = ref(0);
                int amax = -2147483648;
                Ref<Boolean> errors = ref(false);
                if (i_ref.value.sem)
                {
                    return i_ref.value;
                }
                i_ref.value.sem = true;
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
                                    if (((t_ref.value.ty & 0xFF) == ENUMTY.Taarray) || i_ref.value.isAssociativeArray())
                                        e.value = toAssocArrayLiteral(i_ref.value);
                                    else
                                        e.value = initializerToExpression(i_ref.value, null);
                                    if (e.value == null)
                                    {
                                        error(i_ref.value.loc, new BytePtr("cannot use array to initialize `%s`"), t_ref.value.toChars());
                                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                                    }
                                    Ref<ExpInitializer> ei = ref(new ExpInitializer(e.value.loc, e.value));
                                    return initializerSemantic(ei.value, sc_ref.value, t_ref.value, needInterpret_ref.value);
                                case 3:
                                    if (((t_ref.value.nextOf().ty & 0xFF) != ENUMTY.Tfunction))
                                        break;
                                    /*goto default*/ { __dispatch0 = -2; continue dispatched_0; }
                                default:
                                __dispatch0 = 0;
                                error(i_ref.value.loc, new BytePtr("cannot use array to initialize `%s`"), t_ref.value.toChars());
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch0 != 0);
                    }
                    i_ref.value.type = t_ref.value;
                    length.value = 0;
                    {
                        IntRef j = ref(0);
                    L_outer2:
                        for (; (j.value < i_ref.value.index.length);j.value++){
                            Ref<Expression> idx = ref(i_ref.value.index.get(j.value));
                            if (idx.value != null)
                            {
                                sc_ref.value = (sc_ref.value.get()).startCTFE();
                                idx.value = expressionSemantic(idx.value, sc_ref.value);
                                sc_ref.value = (sc_ref.value.get()).endCTFE();
                                idx.value = idx.value.ctfeInterpret();
                                i_ref.value.index.set(j.value, idx.value);
                                Ref<Long> idxvalue = ref(idx.value.toInteger());
                                if ((idxvalue.value >= 2147483648L))
                                {
                                    error(i_ref.value.loc, new BytePtr("array index %llu overflow"), idxvalue.value);
                                    errors.value = true;
                                }
                                length.value = (int)idxvalue.value;
                                if (((idx.value.op & 0xFF) == 127))
                                    errors.value = true;
                            }
                            Ref<Initializer> val = ref(i_ref.value.value.get(j.value));
                            Ref<ExpInitializer> ei = ref(val.value.isExpInitializer());
                            if ((ei.value != null) && (idx.value == null))
                                ei.value.expandTuples = true;
                            val.value = initializerSemantic(val.value, sc_ref.value, t_ref.value.nextOf(), needInterpret_ref.value);
                            if (val.value.isErrorInitializer() != null)
                                errors.value = true;
                            ei.value = val.value.isExpInitializer();
                            if ((ei.value != null) && ((ei.value.exp.op & 0xFF) == 126))
                            {
                                Ref<TupleExp> te = ref((TupleExp)ei.value.exp);
                                i_ref.value.index.remove(j.value);
                                i_ref.value.value.remove(j.value);
                                {
                                    IntRef k = ref(0);
                                    for (; (k.value < (te.value.exps.get()).length);k.value += 1){
                                        Ref<Expression> e = ref((te.value.exps.get()).get(k.value));
                                        i_ref.value.index.insert(j.value + k.value, null);
                                        i_ref.value.value.insert(j.value + k.value, new ExpInitializer(e.value.loc, e.value));
                                    }
                                }
                                j.value--;
                                continue L_outer2;
                            }
                            else
                            {
                                i_ref.value.value.set(j.value, val.value);
                            }
                            length.value++;
                            if ((length.value == 0))
                            {
                                error(i_ref.value.loc, new BytePtr("array dimension overflow"));
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            if ((length.value > i_ref.value.dim))
                                i_ref.value.dim = length.value;
                        }
                    }
                    if (((t_ref.value.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        Ref<Long> edim = ref(((TypeSArray)t_ref.value).dim.toInteger());
                        if (((long)i_ref.value.dim > edim.value))
                        {
                            error(i_ref.value.loc, new BytePtr("array initializer has %u elements, but array length is %llu"), i_ref.value.dim, edim.value);
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if (errors.value)
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                    {
                        Ref<Long> sz = ref(t_ref.value.nextOf().size());
                        Ref<Boolean> overflow = ref(false);
                        Ref<Long> max = ref(mulu((long)i_ref.value.dim, sz.value, overflow));
                        if (overflow.value || (max.value >= 2147483648L))
                        {
                            error(i_ref.value.loc, new BytePtr("array dimension %llu exceeds max of %llu"), (long)i_ref.value.dim, 2147483648L / sz.value);
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                        return i_ref.value;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lerr:*/
                return new ErrorInitializer();
            }
        };
        Function1<ExpInitializer,Initializer> visitExp = new Function1<ExpInitializer,Initializer>(){
            public Initializer invoke(ExpInitializer i) {
                Ref<ExpInitializer> i_ref = ref(i);
                if (needInterpret_ref.value != 0)
                    sc_ref.value = (sc_ref.value.get()).startCTFE();
                i_ref.value.exp = expressionSemantic(i_ref.value.exp, sc_ref.value);
                i_ref.value.exp = resolveProperties(sc_ref.value, i_ref.value.exp);
                if (needInterpret_ref.value != 0)
                    sc_ref.value = (sc_ref.value.get()).endCTFE();
                if (((i_ref.value.exp.op & 0xFF) == 127))
                {
                    return new ErrorInitializer();
                }
                IntRef olderrors = ref(global.value.errors);
                if (needInterpret_ref.value != 0)
                {
                    if (i_ref.value.exp.implicitConvTo(t_ref.value) != 0)
                    {
                        i_ref.value.exp = i_ref.value.exp.implicitCastTo(sc_ref.value, t_ref.value);
                    }
                    if ((global.value.gag == 0) && (olderrors.value != global.value.errors))
                    {
                        return i_ref.value;
                    }
                    i_ref.value.exp = i_ref.value.exp.ctfeInterpret();
                    if (((i_ref.value.exp.op & 0xFF) == 232))
                        error(i_ref.value.loc, new BytePtr("variables cannot be initialized with an expression of type `void`. Use `void` initialization instead."));
                }
                else
                {
                    i_ref.value.exp = i_ref.value.exp.optimize(0, false);
                }
                if ((global.value.gag == 0) && (olderrors.value != global.value.errors))
                {
                    return i_ref.value;
                }
                if (((i_ref.value.exp.type.value.ty & 0xFF) == ENUMTY.Ttuple) && ((((TypeTuple)i_ref.value.exp.type.value).arguments.get()).length == 0))
                {
                    Ref<Type> et = ref(i_ref.value.exp.type.value);
                    i_ref.value.exp = new TupleExp(i_ref.value.exp.loc, new DArray<Expression>());
                    i_ref.value.exp.type.value = et.value;
                }
                if (((i_ref.value.exp.op & 0xFF) == 20))
                {
                    i_ref.value.exp.error(new BytePtr("initializer must be an expression, not `%s`"), i_ref.value.exp.toChars());
                    return new ErrorInitializer();
                }
                if ((needInterpret_ref.value != 0) && hasNonConstPointers(i_ref.value.exp))
                {
                    i_ref.value.exp.error(new BytePtr("cannot use non-constant CTFE pointer in an initializer `%s`"), i_ref.value.exp.toChars());
                    return new ErrorInitializer();
                }
                Ref<Type> tb = ref(t_ref.value.toBasetype());
                Ref<Type> ti = ref(i_ref.value.exp.type.value.toBasetype());
                if (((i_ref.value.exp.op & 0xFF) == 126) && i_ref.value.expandTuples && (i_ref.value.exp.implicitConvTo(t_ref.value) == 0))
                {
                    return new ExpInitializer(i_ref.value.loc, i_ref.value.exp);
                }
                try {
                    if (((i_ref.value.exp.op & 0xFF) == 121) && ((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        Ref<StringExp> se = ref((StringExp)i_ref.value.exp);
                        Ref<Type> typeb = ref(se.value.type.value.toBasetype());
                        Ref<Byte> tynto = ref(tb.value.nextOf().ty);
                        if ((se.value.committed == 0) && ((typeb.value.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.value.ty & 0xFF) == ENUMTY.Tsarray) && ((tynto.value & 0xFF) == ENUMTY.Tchar) || ((tynto.value & 0xFF) == ENUMTY.Twchar) || ((tynto.value & 0xFF) == ENUMTY.Tdchar) && ((long)se.value.numberOfCodeUnits((tynto.value & 0xFF)) < ((TypeSArray)tb.value).dim.toInteger()))
                        {
                            i_ref.value.exp = se.value.castTo(sc_ref.value, t_ref.value);
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if (((tb.value.ty & 0xFF) == ENUMTY.Tstruct) && !(((ti.value.ty & 0xFF) == ENUMTY.Tstruct) && (pequals(tb.value.toDsymbol(sc_ref.value), ti.value.toDsymbol(sc_ref.value)))) && (i_ref.value.exp.implicitConvTo(t_ref.value) == 0))
                    {
                        Ref<StructDeclaration> sd = ref(((TypeStruct)tb.value).sym);
                        if (sd.value.ctor != null)
                        {
                            Ref<Expression> e = ref(null);
                            e.value = new StructLiteralExp(i_ref.value.loc, sd.value, null, null);
                            e.value = new DotIdExp(i_ref.value.loc, e.value, Id.ctor.value);
                            e.value = new CallExp(i_ref.value.loc, e.value, i_ref.value.exp);
                            e.value = expressionSemantic(e.value, sc_ref.value);
                            if (needInterpret_ref.value != 0)
                                i_ref.value.exp = e.value.ctfeInterpret();
                            else
                                i_ref.value.exp = e.value.optimize(0, false);
                        }
                    }
                    if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray) && !tb.value.nextOf().equals(ti.value.toBasetype().nextOf()) && (i_ref.value.exp.implicitConvTo(tb.value.nextOf()) != 0))
                    {
                        t_ref.value = tb.value.nextOf();
                    }
                    if (i_ref.value.exp.implicitConvTo(t_ref.value) != 0)
                    {
                        i_ref.value.exp = i_ref.value.exp.implicitCastTo(sc_ref.value, t_ref.value);
                    }
                    else
                    {
                        if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray) && (i_ref.value.exp.implicitConvTo(tb.value.nextOf().arrayOf()) > MATCH.nomatch))
                        {
                            Ref<Long> dim1 = ref(((TypeSArray)tb.value).dim.toInteger());
                            Ref<Long> dim2 = ref(dim1.value);
                            if (((i_ref.value.exp.op & 0xFF) == 47))
                            {
                                Ref<ArrayLiteralExp> ale = ref((ArrayLiteralExp)i_ref.value.exp);
                                dim2.value = ale.value.elements != null ? (long)(ale.value.elements.get()).length : 0L;
                            }
                            else if (((i_ref.value.exp.op & 0xFF) == 31))
                            {
                                Ref<Type> tx = ref(toStaticArrayType((SliceExp)i_ref.value.exp));
                                if (tx.value != null)
                                    dim2.value = ((TypeSArray)tx.value).dim.toInteger();
                            }
                            if ((dim1.value != dim2.value))
                            {
                                i_ref.value.exp.error(new BytePtr("mismatched array lengths, %d and %d"), (int)dim1.value, (int)dim2.value);
                                i_ref.value.exp = new ErrorExp();
                            }
                        }
                        i_ref.value.exp = i_ref.value.exp.implicitCastTo(sc_ref.value, t_ref.value);
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                if (((i_ref.value.exp.op & 0xFF) == 127))
                {
                    return i_ref.value;
                }
                if (needInterpret_ref.value != 0)
                    i_ref.value.exp = i_ref.value.exp.ctfeInterpret();
                else
                    i_ref.value.exp = i_ref.value.exp.optimize(0, false);
                return i_ref.value;
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
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Function1<VoidInitializer,Initializer> visitVoid = new Function1<VoidInitializer,Initializer>(){
            public Initializer invoke(VoidInitializer i) {
                Ref<VoidInitializer> i_ref = ref(i);
                error(i_ref.value.loc, new BytePtr("cannot infer type from void initializer"));
                return new ErrorInitializer();
            }
        };
        Function1<ErrorInitializer,Initializer> visitError = new Function1<ErrorInitializer,Initializer>(){
            public Initializer invoke(ErrorInitializer i) {
                Ref<ErrorInitializer> i_ref = ref(i);
                return i_ref.value;
            }
        };
        Function1<StructInitializer,Initializer> visitStruct = new Function1<StructInitializer,Initializer>(){
            public Initializer invoke(StructInitializer i) {
                Ref<StructInitializer> i_ref = ref(i);
                error(i_ref.value.loc, new BytePtr("cannot infer type from struct initializer"));
                return new ErrorInitializer();
            }
        };
        Function1<ArrayInitializer,Initializer> visitArray = new Function1<ArrayInitializer,Initializer>(){
            public Initializer invoke(ArrayInitializer init) {
                Ref<ArrayInitializer> init_ref = ref(init);
                Ref<Ptr<DArray<Expression>>> keys = ref(null);
                Ref<Ptr<DArray<Expression>>> values = ref(null);
                try {
                    if (init_ref.value.isAssociativeArray())
                    {
                        keys.value = new DArray<Expression>(init_ref.value.value.length);
                        values.value = new DArray<Expression>(init_ref.value.value.length);
                        {
                            IntRef i = ref(0);
                        L_outer3:
                            for (; (i.value < init_ref.value.value.length);i.value++){
                                Ref<Expression> e = ref(init_ref.value.index.get(i.value));
                                if (e.value == null)
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                keys.value.get().set(i.value, e.value);
                                Ref<Initializer> iz = ref(init_ref.value.value.get(i.value));
                                if (iz.value == null)
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                iz.value = inferType(iz.value, sc_ref.value);
                                if (iz.value.isErrorInitializer() != null)
                                {
                                    return iz.value;
                                }
                                assert(iz.value.isExpInitializer() != null);
                                values.value.get().set(i.value, ((ExpInitializer)iz.value).exp);
                                assert((((values.value.get()).get(i.value).op & 0xFF) != 127));
                            }
                        }
                        Ref<Expression> e = ref(new AssocArrayLiteralExp(init_ref.value.loc, keys.value, values.value));
                        Ref<ExpInitializer> ei = ref(new ExpInitializer(init_ref.value.loc, e.value));
                        return inferType(ei.value, sc_ref.value);
                    }
                    else
                    {
                        Ref<Ptr<DArray<Expression>>> elements = ref(new DArray<Expression>(init_ref.value.value.length));
                        (elements.value.get()).zero();
                        {
                            IntRef i = ref(0);
                        L_outer4:
                            for (; (i.value < init_ref.value.value.length);i.value++){
                                assert(init_ref.value.index.get(i.value) == null);
                                Ref<Initializer> iz = ref(init_ref.value.value.get(i.value));
                                if (iz.value == null)
                                    /*goto Lno*/throw Dispatch0.INSTANCE;
                                iz.value = inferType(iz.value, sc_ref.value);
                                if (iz.value.isErrorInitializer() != null)
                                {
                                    return iz.value;
                                }
                                assert(iz.value.isExpInitializer() != null);
                                elements.value.get().set(i.value, ((ExpInitializer)iz.value).exp);
                                assert((((elements.value.get()).get(i.value).op & 0xFF) != 127));
                            }
                        }
                        Ref<Expression> e = ref(new ArrayLiteralExp(init_ref.value.loc, null, elements.value));
                        Ref<ExpInitializer> ei = ref(new ExpInitializer(init_ref.value.loc, e.value));
                        return inferType(ei.value, sc_ref.value);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lno:*/
                if (keys.value != null)
                {
                    error(init_ref.value.loc, new BytePtr("not an associative array initializer"));
                }
                else
                {
                    error(init_ref.value.loc, new BytePtr("cannot infer type from array initializer"));
                }
                return new ErrorInitializer();
            }
        };
        Function1<ExpInitializer,Initializer> visitExp = new Function1<ExpInitializer,Initializer>(){
            public Initializer invoke(ExpInitializer init) {
                Ref<ExpInitializer> init_ref = ref(init);
                init_ref.value.exp = expressionSemantic(init_ref.value.exp, sc_ref.value);
                if (((init_ref.value.exp.op & 0xFF) == 20))
                    init_ref.value.exp = resolveAliasThis(sc_ref.value, init_ref.value.exp, false);
                init_ref.value.exp = resolveProperties(sc_ref.value, init_ref.value.exp);
                if (((init_ref.value.exp.op & 0xFF) == 203))
                {
                    Ref<ScopeExp> se = ref((ScopeExp)init_ref.value.exp);
                    Ref<TemplateInstance> ti = ref(se.value.sds.isTemplateInstance());
                    if ((ti.value != null) && (ti.value.semanticRun == PASS.semantic) && (ti.value.aliasdecl == null))
                        se.value.error(new BytePtr("cannot infer type from %s `%s`, possible circular dependency"), se.value.sds.kind(), se.value.toChars());
                    else
                        se.value.error(new BytePtr("cannot infer type from %s `%s`"), se.value.sds.kind(), se.value.toChars());
                    return new ErrorInitializer();
                }
                Ref<Boolean> hasOverloads = ref(false);
                {
                    Ref<FuncDeclaration> f = ref(isFuncAddress(init_ref.value.exp, ptr(hasOverloads)));
                    if ((f.value) != null)
                    {
                        if (f.value.checkForwardRef(init_ref.value.loc))
                        {
                            return new ErrorInitializer();
                        }
                        if (hasOverloads.value && !f.value.isUnique())
                        {
                            init_ref.value.exp.error(new BytePtr("cannot infer type from overloaded function symbol `%s`"), init_ref.value.exp.toChars());
                            return new ErrorInitializer();
                        }
                    }
                }
                if (((init_ref.value.exp.op & 0xFF) == 19))
                {
                    Ref<AddrExp> ae = ref((AddrExp)init_ref.value.exp);
                    if (((ae.value.e1.op & 0xFF) == 214))
                    {
                        init_ref.value.exp.error(new BytePtr("cannot infer type from overloaded function symbol `%s`"), init_ref.value.exp.toChars());
                        return new ErrorInitializer();
                    }
                }
                if (((init_ref.value.exp.op & 0xFF) == 127))
                {
                    return new ErrorInitializer();
                }
                if (init_ref.value.exp.type.value == null)
                {
                    return new ErrorInitializer();
                }
                return init_ref.value;
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
            public Expression invoke(VoidInitializer _param_0) {
                return null;
            }
        };
        Function1<ErrorInitializer,Expression> visitError = new Function1<ErrorInitializer,Expression>(){
            public Expression invoke(ErrorInitializer _param_0) {
                return new ErrorExp();
            }
        };
        Function1<StructInitializer,Expression> visitStruct = new Function1<StructInitializer,Expression>(){
            public Expression invoke(StructInitializer _param_0) {
                return null;
            }
        };
        Function1<ArrayInitializer,Expression> visitArray = new Function1<ArrayInitializer,Expression>(){
            public Expression invoke(ArrayInitializer init) {
                Ref<ArrayInitializer> init_ref = ref(init);
                Ref<Ptr<DArray<Expression>>> elements = ref(null);
                IntRef edim = ref(0);
                int amax = -2147483648;
                Ref<Type> t = ref(null);
                try {
                    if (init_ref.value.type != null)
                    {
                        if ((pequals(init_ref.value.type, Type.terror.value)))
                        {
                            return new ErrorExp();
                        }
                        t.value = init_ref.value.type.toBasetype();
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
                                        Ref<Long> adim = ref(((TypeSArray)t.value).dim.toInteger());
                                        if ((adim.value >= 2147483648L))
                                            /*goto Lno*/throw Dispatch0.INSTANCE;
                                        edim.value = (int)adim.value;
                                        break;
                                    case 3:
                                    case 0:
                                        edim.value = init_ref.value.dim;
                                        break;
                                    default:
                                    throw new AssertionError("Unreachable code!");
                                }
                            } while(__dispatch3 != 0);
                        }
                    }
                    else
                    {
                        edim.value = init_ref.value.value.length;
                        {
                            IntRef i = ref(0);
                            IntRef j = ref(0);
                        L_outer5:
                            for (; (i.value < init_ref.value.value.length);comma(i.value++, j.value++)){
                                if (init_ref.value.index.get(i.value) != null)
                                {
                                    if (((init_ref.value.index.get(i.value).op & 0xFF) == 135))
                                    {
                                        Ref<Long> idxval = ref(init_ref.value.index.get(i.value).toInteger());
                                        if ((idxval.value >= 2147483648L))
                                            /*goto Lno*/throw Dispatch0.INSTANCE;
                                        j.value = (int)idxval.value;
                                    }
                                    else
                                        /*goto Lno*/throw Dispatch0.INSTANCE;
                                }
                                if ((j.value >= edim.value))
                                    edim.value = j.value + 1;
                            }
                        }
                    }
                    elements.value = new DArray<Expression>(edim.value);
                    (elements.value.get()).zero();
                    {
                        IntRef i = ref(0);
                        IntRef j = ref(0);
                    L_outer6:
                        for (; (i.value < init_ref.value.value.length);comma(i.value++, j.value++)){
                            if (init_ref.value.index.get(i.value) != null)
                                j.value = (int)init_ref.value.index.get(i.value).toInteger();
                            assert((j.value < edim.value));
                            Ref<Initializer> iz = ref(init_ref.value.value.get(i.value));
                            if (iz.value == null)
                                /*goto Lno*/throw Dispatch0.INSTANCE;
                            Ref<Expression> ex = ref(initializerToExpression(iz.value, null));
                            if (ex.value == null)
                            {
                                /*goto Lno*/throw Dispatch0.INSTANCE;
                            }
                            elements.value.get().set(j.value, ex.value);
                        }
                    }
                    {
                        Ref<Expression> _init = ref(null);
                        {
                            IntRef i = ref(0);
                        L_outer7:
                            for (; (i.value < edim.value);i.value++){
                                if ((elements.value.get()).get(i.value) == null)
                                {
                                    if (init_ref.value.type == null)
                                        /*goto Lno*/throw Dispatch0.INSTANCE;
                                    if (_init.value == null)
                                        _init.value = defaultInit(((TypeNext)t.value).next, Loc.initial.value);
                                    elements.value.get().set(i.value, _init.value);
                                }
                            }
                        }
                        if (t.value != null)
                        {
                            Ref<Type> tn = ref(t.value.nextOf().toBasetype());
                            if (((tn.value.ty & 0xFF) == ENUMTY.Tsarray))
                            {
                                IntRef dim = ref((int)((TypeSArray)tn.value).dim.toInteger());
                                Ref<Type> te = ref(tn.value.nextOf().toBasetype());
                                {
                                    Ref<Slice<Expression>> __r1501 = ref((elements.value.get()).opSlice().copy());
                                    IntRef __key1502 = ref(0);
                                    for (; (__key1502.value < __r1501.value.getLength());__key1502.value += 1) {
                                        Ref<Expression> e = ref(__r1501.value.get(__key1502.value));
                                        if (te.value.equals(e.value.type.value))
                                        {
                                            Ref<Ptr<DArray<Expression>>> elements2 = ref(new DArray<Expression>(dim.value));
                                            {
                                                Ref<Slice<Expression>> __r1503 = ref((elements2.value.get()).opSlice().copy());
                                                IntRef __key1504 = ref(0);
                                                for (; (__key1504.value < __r1503.value.getLength());__key1504.value += 1) {
                                                    Ref<Expression> e2 = ref(__r1503.value.get(__key1504.value));
                                                    e2.value = e.value;
                                                }
                                            }
                                            e.value = new ArrayLiteralExp(e.value.loc, tn.value, elements2.value);
                                        }
                                    }
                                }
                            }
                        }
                        {
                            IntRef i = ref(0);
                            for (; (i.value < edim.value);i.value++){
                                Ref<Expression> e = ref((elements.value.get()).get(i.value));
                                if (((e.value.op & 0xFF) == 127))
                                {
                                    return e.value;
                                }
                            }
                        }
                        Ref<Expression> e = ref(new ArrayLiteralExp(init_ref.value.loc, init_ref.value.type, elements.value));
                        return e.value;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lno:*/
                return null;
            }
        };
        Function1<ExpInitializer,Expression> visitExp = new Function1<ExpInitializer,Expression>(){
            public Expression invoke(ExpInitializer i) {
                Ref<ExpInitializer> i_ref = ref(i);
                if (itype_ref.value != null)
                {
                    Ref<Type> tb = ref(itype_ref.value.toBasetype());
                    Ref<Expression> e = ref(((i_ref.value.exp.op & 0xFF) == 95) || ((i_ref.value.exp.op & 0xFF) == 96) ? ((AssignExp)i_ref.value.exp).e2.value : i_ref.value.exp);
                    if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray) && (e.value.implicitConvTo(tb.value.nextOf()) != 0))
                    {
                        Ref<TypeSArray> tsa = ref((TypeSArray)tb.value);
                        IntRef d = ref((int)tsa.value.dim.toInteger());
                        Ref<Ptr<DArray<Expression>>> elements = ref(new DArray<Expression>(d.value));
                        {
                            IntRef j = ref(0);
                            for (; (j.value < d.value);j.value++) {
                                elements.value.get().set(j.value, e.value);
                            }
                        }
                        Ref<ArrayLiteralExp> ae = ref(new ArrayLiteralExp(e.value.loc, itype_ref.value, elements.value));
                        return ae.value;
                    }
                }
                return i_ref.value.exp;
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
