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
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;

public class clone {
    static TypeFunction buildXtoHashtftohash = null;

    // Erasure: mergeFuncAttrs<long, FuncDeclaration>
    public static long mergeFuncAttrs(long s1, FuncDeclaration f) {
        if (f == null)
        {
            return s1;
        }
        long s2 = f.storage_class & 137438953472L;
        TypeFunction tf = ((TypeFunction)f.type);
        if ((tf.trust == TRUST.safe))
        {
            s2 |= 8589934592L;
        }
        else if ((tf.trust == TRUST.system))
        {
            s2 |= 34359738368L;
        }
        else if ((tf.trust == TRUST.trusted))
        {
            s2 |= 17179869184L;
        }
        if ((tf.purity != PURE.impure))
        {
            s2 |= 67108864L;
        }
        if (tf.isnothrow)
        {
            s2 |= 33554432L;
        }
        if (tf.isnogc)
        {
            s2 |= 4398046511104L;
        }
        long sa = s1 & s2;
        long so = s1 | s2;
        long stc = sa & 4398147174400L | so & 137438953472L;
        if ((so & 34359738368L) != 0)
        {
            stc |= 34359738368L;
        }
        else if ((sa & 17179869184L) != 0)
        {
            stc |= 17179869184L;
        }
        else if (((so & 25769803776L) == 25769803776L))
        {
            stc |= 17179869184L;
        }
        else if ((sa & 8589934592L) != 0)
        {
            stc |= 8589934592L;
        }
        return stc;
    }

    // Erasure: hasIdentityOpAssign<AggregateDeclaration, Ptr>
    public static FuncDeclaration hasIdentityOpAssign(AggregateDeclaration ad, Ptr<Scope> sc) {
        Dsymbol assign = search_function(ad, Id.assign);
        if (assign != null)
        {
            NullExp er = new NullExp(ad.loc, ad.type);
            IdentifierExp el = new IdentifierExp(ad.loc, Id.p);
            el.type.value = ad.type;
            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
            try {
                a.value.setDim(1);
                int errors = global.startGagging();
                sc = pcopy((sc.get()).push());
                (sc.get()).tinst = null;
                (sc.get()).minst = null;
                a.value.set(0, er);
                FuncDeclaration f = resolveFuncCall(ad.loc, sc, assign, null, ad.type, a.value, FuncResolveFlag.quiet);
                if (f == null)
                {
                    a.value.set(0, el);
                    f = resolveFuncCall(ad.loc, sc, assign, null, ad.type, a.value, FuncResolveFlag.quiet);
                }
                sc = pcopy((sc.get()).pop());
                global.endGagging(errors);
                if (f != null)
                {
                    if (f.errors)
                    {
                        return null;
                    }
                    ParameterList fparams = f.getParameterList().copy();
                    if (fparams.length() != 0)
                    {
                        Parameter fparam0 = fparams.get(0);
                        if ((!pequals(fparam0.type.toDsymbol(null), ad)))
                        {
                            f = null;
                        }
                    }
                }
                return f;
            }
            finally {
            }
        }
        return null;
    }

    // Erasure: needOpAssign<StructDeclaration>
    public static boolean needOpAssign(StructDeclaration sd) {
        Function0<Boolean> isNeeded = new Function0<Boolean>() {
            public Boolean invoke() {
             {
                return true;
            }}

        };
        if (sd.isUnionDeclaration() != null)
        {
            return !isNeeded.invoke();
        }
        if (sd.hasIdentityAssign || (sd.dtor != null) || (sd.postblit != null))
        {
            return isNeeded.invoke();
        }
        {
            Slice<VarDeclaration> __r803 = sd.fields.opSlice().copy();
            int __key804 = 0;
            for (; (__key804 < __r803.getLength());__key804 += 1) {
                VarDeclaration v = __r803.get(__key804);
                if ((v.storage_class & 2097152L) != 0)
                {
                    continue;
                }
                if (v.overlapped)
                {
                    continue;
                }
                Type tv = v.type.baseElemOf();
                if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    TypeStruct ts = ((TypeStruct)tv);
                    if (ts.sym.isUnionDeclaration() != null)
                    {
                        continue;
                    }
                    if (needOpAssign(ts.sym))
                    {
                        return isNeeded.invoke();
                    }
                }
            }
        }
        return !isNeeded.invoke();
    }

    // Erasure: buildOpAssign<StructDeclaration, Ptr>
    public static FuncDeclaration buildOpAssign(StructDeclaration sd, Ptr<Scope> sc) {
        {
            FuncDeclaration f = hasIdentityOpAssign(sd, sc);
            if ((f) != null)
            {
                sd.hasIdentityAssign = true;
                return f;
            }
        }
        if (!needOpAssign(sd))
        {
            return null;
        }
        long stc = 4406737108992L;
        Loc declLoc = sd.loc.copy();
        Loc loc = new Loc();
        {
            Slice<VarDeclaration> __r805 = sd.fields.opSlice().copy();
            int __key806 = 0;
            for (; (__key806 < __r805.getLength());__key806 += 1) {
                VarDeclaration v = __r805.get(__key806);
                if ((v.storage_class & 2097152L) != 0)
                {
                    continue;
                }
                if (v.overlapped)
                {
                    continue;
                }
                Type tv = v.type.baseElemOf();
                if (((tv.ty & 0xFF) != ENUMTY.Tstruct))
                {
                    continue;
                }
                StructDeclaration sdv = (((TypeStruct)tv)).sym;
                stc = mergeFuncAttrs(stc, hasIdentityOpAssign(sdv, sc));
            }
        }
        if ((sd.dtor != null) || (sd.postblit != null))
        {
            if (!sd.type.isAssignable())
            {
                return null;
            }
            stc = mergeFuncAttrs(stc, sd.dtor);
            if ((stc & 8589934592L) != 0)
            {
                stc = stc & -8589934593L | 17179869184L;
            }
        }
        DArray<Parameter> fparams = new DArray<Parameter>();
        (fparams).push(new Parameter(16777216L, sd.type, Id.p, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(fparams, VarArg.none), sd.handleType(), LINK.d, stc | 2097152L);
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial, Id.assign, stc, tf);
        fop.storage_class |= 70368744177664L;
        fop.generated = true;
        Expression e = null;
        if ((stc & 137438953472L) != 0)
        {
            e = null;
        }
        else if (sd.dtor != null)
        {
            TypeFunction tdtor = ((TypeFunction)sd.dtor.type);
            assert(((tdtor.ty & 0xFF) == ENUMTY.Tfunction));
            Identifier idswap = Identifier.generateId(new BytePtr("__swap"));
            VarDeclaration swap = new VarDeclaration(loc, sd.type, idswap, new VoidInitializer(loc), 0L);
            swap.storage_class |= 1168247881728L;
            if (tdtor.isscope)
            {
                swap.storage_class |= 524288L;
            }
            DeclarationExp e1 = new DeclarationExp(loc, swap);
            BlitExp e2 = new BlitExp(loc, new VarExp(loc, swap, true), new ThisExp(loc));
            BlitExp e3 = new BlitExp(loc, new ThisExp(loc), new IdentifierExp(loc, Id.p));
            CallExp e4 = new CallExp(loc, new DotVarExp(loc, new VarExp(loc, swap, true), sd.dtor, false));
            e = Expression.combine(e1, e2, e3, e4);
        }
        else if (sd.postblit != null)
        {
            e = new BlitExp(loc, new ThisExp(loc), new IdentifierExp(loc, Id.p));
        }
        else
        {
            e = null;
            {
                Slice<VarDeclaration> __r807 = sd.fields.opSlice().copy();
                int __key808 = 0;
                for (; (__key808 < __r807.getLength());__key808 += 1) {
                    VarDeclaration v = __r807.get(__key808);
                    AssignExp ec = new AssignExp(loc, new DotVarExp(loc, new ThisExp(loc), v, true), new DotVarExp(loc, new IdentifierExp(loc, Id.p), v, true));
                    e = Expression.combine(e, (Expression)ec);
                }
            }
        }
        if (e != null)
        {
            Statement s1 = new ExpStatement(loc, e);
            ThisExp er = new ThisExp(loc);
            Statement s2 = new ReturnStatement(loc, er);
            fop.fbody.value = new CompoundStatement(loc, slice(new Statement[]{s1, s2}));
            tf.isreturn = true;
        }
        (sd.members).push(fop);
        fop.addMember(sc, sd);
        sd.hasIdentityAssign = true;
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc = 0L;
        (sc2.get()).linkage = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            fop.storage_class |= 137438953472L;
            fop.fbody.value = null;
        }
        return fop;
    }

    // Erasure: needOpEquals<StructDeclaration>
    public static boolean needOpEquals(StructDeclaration sd) {
        try {
            try {
                if (sd.isUnionDeclaration() != null)
                {
                    /*goto Ldontneed*/throw Dispatch0.INSTANCE;
                }
                if (sd.hasIdentityEquals)
                {
                    /*goto Lneed*/throw Dispatch1.INSTANCE;
                }
                {
                    int i = 0;
                L_outer1:
                    for (; (i < sd.fields.length);i++){
                        VarDeclaration v = sd.fields.get(i);
                        if ((v.storage_class & 2097152L) != 0)
                        {
                            continue L_outer1;
                        }
                        if (v.overlapped)
                        {
                            continue L_outer1;
                        }
                        Type tv = v.type.toBasetype();
                        Type tvbase = tv.baseElemOf();
                        if (((tvbase.ty & 0xFF) == ENUMTY.Tstruct))
                        {
                            TypeStruct ts = ((TypeStruct)tvbase);
                            if (ts.sym.isUnionDeclaration() != null)
                            {
                                continue L_outer1;
                            }
                            if (needOpEquals(ts.sym))
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                            if (ts.sym.aliasthis != null)
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                        }
                        if (tv.isfloating())
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty & 0xFF) == ENUMTY.Tarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty & 0xFF) == ENUMTY.Taarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty & 0xFF) == ENUMTY.Tclass))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Ldontneed:*/
            return false;
        }
        catch(Dispatch1 __d){}
    /*Lneed:*/
        return true;
    }

    // Erasure: hasIdentityOpEquals<AggregateDeclaration, Ptr>
    public static FuncDeclaration hasIdentityOpEquals(AggregateDeclaration ad, Ptr<Scope> sc) {
        Dsymbol eq = search_function(ad, Id.eq);
        if (eq != null)
        {
            NullExp er = new NullExp(ad.loc, null);
            IdentifierExp el = new IdentifierExp(ad.loc, Id.p);
            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
            try {
                a.value.setDim(1);
                {
                    int __key809 = 0;
                    int __limit810 = 5;
                    for (; (__key809 < __limit810);__key809 += 1) {
                        int i = __key809;
                        Type tthis = null;
                        switch (i)
                        {
                            case 0:
                                tthis = ad.type;
                                break;
                            case 1:
                                tthis = ad.type.constOf();
                                break;
                            case 2:
                                tthis = ad.type.immutableOf();
                                break;
                            case 3:
                                tthis = ad.type.sharedOf();
                                break;
                            case 4:
                                tthis = ad.type.sharedConstOf();
                                break;
                            default:
                            throw SwitchError.INSTANCE;
                        }
                        FuncDeclaration f = null;
                        int errors = global.startGagging();
                        sc = pcopy((sc.get()).push());
                        (sc.get()).tinst = null;
                        (sc.get()).minst = null;
                        {
                            int __key811 = 0;
                            int __limit812 = 2;
                            for (; (__key811 < __limit812);__key811 += 1) {
                                int j = __key811;
                                a.value.set(0, (j == 0) ? er : el);
                                a.value.get(0).type.value = tthis;
                                f = resolveFuncCall(ad.loc, sc, eq, null, tthis, a.value, FuncResolveFlag.quiet);
                                if (f != null)
                                {
                                    break;
                                }
                            }
                        }
                        sc = pcopy((sc.get()).pop());
                        global.endGagging(errors);
                        if (f != null)
                        {
                            if (f.errors)
                            {
                                return null;
                            }
                            return f;
                        }
                    }
                }
            }
            finally {
            }
        }
        return null;
    }

    // Erasure: buildOpEquals<StructDeclaration, Ptr>
    public static FuncDeclaration buildOpEquals(StructDeclaration sd, Ptr<Scope> sc) {
        if (hasIdentityOpEquals(sd, sc) != null)
        {
            sd.hasIdentityEquals = true;
        }
        return null;
    }

    // Erasure: buildXopEquals<StructDeclaration, Ptr>
    public static FuncDeclaration buildXopEquals(StructDeclaration sd, Ptr<Scope> sc) {
        if (!needOpEquals(sd))
        {
            return null;
        }
        {
            Dsymbol eq = search_function(sd, Id.eq);
            if ((eq) != null)
            {
                {
                    FuncDeclaration fd = eq.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        TypeFunction tfeqptr = null;
                        {
                            Ref<Scope> scx = ref(new Scope().copy());
                            DArray<Parameter> parameters = new DArray<Parameter>();
                            (parameters).push(new Parameter(2097156L, sd.type, null, null, null));
                            tfeqptr = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tbool, LINK.d, 0L);
                            tfeqptr.mod = (byte)1;
                            tfeqptr = ((TypeFunction)typeSemantic(tfeqptr, Loc.initial, ptr(scx)));
                        }
                        fd = fd.overloadExactMatch(tfeqptr);
                        if (fd != null)
                        {
                            return fd;
                        }
                    }
                }
            }
        }
        if (StructDeclaration.xerreq == null)
        {
            Identifier id = Identifier.idPool(new ByteSlice("_xopEquals"));
            Expression e = new IdentifierExp(sd.loc, Id.empty);
            e = new DotIdExp(sd.loc, e, Id.object);
            e = new DotIdExp(sd.loc, e, id);
            e = expressionSemantic(e, sc);
            Dsymbol s = getDsymbol(e);
            assert(s != null);
            StructDeclaration.xerreq = s.isFuncDeclaration();
        }
        Loc declLoc = new Loc();
        Loc loc = new Loc();
        DArray<Parameter> parameters = new DArray<Parameter>();
        (parameters).push(new Parameter(2097156L, sd.type, Id.p, null, null)).push(new Parameter(2097156L, sd.type, Id.q, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tbool, LINK.d, 0L);
        Identifier id = Id.xopEquals;
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial, id, 1L, tf);
        fop.generated = true;
        Expression e1 = new IdentifierExp(loc, Id.p);
        Expression e2 = new IdentifierExp(loc, Id.q);
        Expression e = new EqualExp(TOK.equal, loc, e1, e2);
        fop.fbody.value = new ReturnStatement(loc, e);
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc = 0L;
        (sc2.get()).linkage = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            fop = StructDeclaration.xerreq;
        }
        return fop;
    }

    // Erasure: buildXopCmp<StructDeclaration, Ptr>
    public static FuncDeclaration buildXopCmp(StructDeclaration sd, Ptr<Scope> sc) {
        {
            Dsymbol cmp = search_function(sd, Id.cmp);
            if ((cmp) != null)
            {
                {
                    FuncDeclaration fd = cmp.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        TypeFunction tfcmpptr = null;
                        {
                            Ref<Scope> scx = ref(new Scope().copy());
                            DArray<Parameter> parameters = new DArray<Parameter>();
                            (parameters).push(new Parameter(2097156L, sd.type, null, null, null));
                            tfcmpptr = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tint32, LINK.d, 0L);
                            tfcmpptr.mod = (byte)1;
                            tfcmpptr = ((TypeFunction)typeSemantic(tfcmpptr, Loc.initial, ptr(scx)));
                        }
                        fd = fd.overloadExactMatch(tfcmpptr);
                        if (fd != null)
                        {
                            return fd;
                        }
                    }
                }
            }
            else
            {
                return null;
            }
        }
        if (StructDeclaration.xerrcmp == null)
        {
            Identifier id = Identifier.idPool(new ByteSlice("_xopCmp"));
            Expression e = new IdentifierExp(sd.loc, Id.empty);
            e = new DotIdExp(sd.loc, e, Id.object);
            e = new DotIdExp(sd.loc, e, id);
            e = expressionSemantic(e, sc);
            Dsymbol s = getDsymbol(e);
            assert(s != null);
            StructDeclaration.xerrcmp = s.isFuncDeclaration();
        }
        Loc declLoc = new Loc();
        Loc loc = new Loc();
        DArray<Parameter> parameters = new DArray<Parameter>();
        (parameters).push(new Parameter(2097156L, sd.type, Id.p, null, null));
        (parameters).push(new Parameter(2097156L, sd.type, Id.q, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tint32, LINK.d, 0L);
        Identifier id = Id.xopCmp;
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial, id, 1L, tf);
        fop.generated = true;
        Expression e1 = new IdentifierExp(loc, Id.p);
        Expression e2 = new IdentifierExp(loc, Id.q);
        Expression e = new CallExp(loc, new DotIdExp(loc, e2, Id.cmp), e1);
        fop.fbody.value = new ReturnStatement(loc, e);
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc = 0L;
        (sc2.get()).linkage = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            fop = StructDeclaration.xerrcmp;
        }
        return fop;
    }

    // Erasure: needToHash<StructDeclaration>
    public static boolean needToHash(StructDeclaration sd) {
        try {
            try {
                if (sd.isUnionDeclaration() != null)
                {
                    /*goto Ldontneed*/throw Dispatch0.INSTANCE;
                }
                if (sd.xhash != null)
                {
                    /*goto Lneed*/throw Dispatch1.INSTANCE;
                }
                {
                    int i = 0;
                L_outer2:
                    for (; (i < sd.fields.length);i++){
                        VarDeclaration v = sd.fields.get(i);
                        if ((v.storage_class & 2097152L) != 0)
                        {
                            continue L_outer2;
                        }
                        if (v.overlapped)
                        {
                            continue L_outer2;
                        }
                        Type tv = v.type.toBasetype();
                        Type tvbase = tv.baseElemOf();
                        if (((tvbase.ty & 0xFF) == ENUMTY.Tstruct))
                        {
                            TypeStruct ts = ((TypeStruct)tvbase);
                            if (ts.sym.isUnionDeclaration() != null)
                            {
                                continue L_outer2;
                            }
                            if (needToHash(ts.sym))
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                            if (ts.sym.aliasthis != null)
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                        }
                        if (tv.isfloating())
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty & 0xFF) == ENUMTY.Tarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty & 0xFF) == ENUMTY.Taarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty & 0xFF) == ENUMTY.Tclass))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Ldontneed:*/
            return false;
        }
        catch(Dispatch1 __d){}
    /*Lneed:*/
        return true;
    }

    // Erasure: buildXtoHash<StructDeclaration, Ptr>
    public static FuncDeclaration buildXtoHash(StructDeclaration sd, Ptr<Scope> sc) {
        {
            Dsymbol s = search_function(sd, Id.tohash);
            if ((s) != null)
            {
                if (clone.buildXtoHashtftohash == null)
                {
                    clone.buildXtoHashtftohash = new TypeFunction(new ParameterList(null, VarArg.none), Type.thash_t, LINK.d, 0L);
                    clone.buildXtoHashtftohash.mod = (byte)1;
                    clone.buildXtoHashtftohash = ((TypeFunction)merge(clone.buildXtoHashtftohash));
                }
                {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        fd = fd.overloadExactMatch(clone.buildXtoHashtftohash);
                        if (fd != null)
                        {
                            return fd;
                        }
                    }
                }
            }
        }
        if (!needToHash(sd))
        {
            return null;
        }
        Loc declLoc = new Loc();
        Loc loc = new Loc();
        DArray<Parameter> parameters = new DArray<Parameter>();
        (parameters).push(new Parameter(2097156L, sd.type, Id.p, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.thash_t, LINK.d, 17213423616L);
        Identifier id = Id.xtoHash;
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial, id, 1L, tf);
        fop.generated = true;
        BytePtr code = pcopy(new BytePtr("size_t h = 0;foreach (i, T; typeof(p.tupleof))    static if(is(T* : const(.object.Object)*))         h = h * 33 + typeid(const(.object.Object)).getHash(cast(const void*)&p.tupleof[i]);    else         h = h * 33 + typeid(T).getHash(cast(const void*)&p.tupleof[i]);return h;"));
        fop.fbody.value = new CompileStatement(loc, new StringExp(loc, code));
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc = 0L;
        (sc2.get()).linkage = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        return fop;
    }

    // Erasure: buildDtor<AggregateDeclaration, Ptr>
    public static DtorDeclaration buildDtor(AggregateDeclaration ad, Ptr<Scope> sc) {
        if (ad.isUnionDeclaration() != null)
        {
            return null;
        }
        long stc = 4406737108992L;
        Loc declLoc = ad.dtors.length != 0 ? ad.dtors.get(0).loc : ad.loc.copy();
        Loc loc = new Loc();
        boolean dtorIsCppPrototype = (ad.dtors.length == 1) && (ad.dtors.get(0).linkage == LINK.cpp) && (ad.dtors.get(0).fbody.value == null);
        if (!dtorIsCppPrototype)
        {
            Expression e = null;
            {
                int i = 0;
                for (; (i < ad.fields.length);i++){
                    VarDeclaration v = ad.fields.get(i);
                    if ((v.storage_class & 2097152L) != 0)
                    {
                        continue;
                    }
                    if (v.overlapped)
                    {
                        continue;
                    }
                    Type tv = v.type.baseElemOf();
                    if (((tv.ty & 0xFF) != ENUMTY.Tstruct))
                    {
                        continue;
                    }
                    StructDeclaration sdv = (((TypeStruct)tv)).sym;
                    if (sdv.dtor == null)
                    {
                        continue;
                    }
                    sdv.dtor.functionSemantic();
                    stc = mergeFuncAttrs(stc, sdv.dtor);
                    if ((stc & 137438953472L) != 0)
                    {
                        e = null;
                        break;
                    }
                    Expression ex = null;
                    tv = v.type.toBasetype();
                    if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
                    {
                        ex = new ThisExp(loc);
                        ex = new DotVarExp(loc, ex, v, true);
                        ex = new CastExp(loc, ex, v.type.mutableOf());
                        if ((stc & 8589934592L) != 0)
                        {
                            stc = stc & -8589934593L | 17179869184L;
                        }
                        ex = new DotVarExp(loc, ex, sdv.dtor, false);
                        ex = new CallExp(loc, ex);
                    }
                    else
                    {
                        int n = tv.numberOfElems(loc);
                        if ((n == 0))
                        {
                            continue;
                        }
                        ex = new ThisExp(loc);
                        ex = new DotVarExp(loc, ex, v, true);
                        ex = new DotIdExp(loc, ex, Id.ptr);
                        ex = new CastExp(loc, ex, sdv.type.pointerTo());
                        if ((stc & 8589934592L) != 0)
                        {
                            stc = stc & -8589934593L | 17179869184L;
                        }
                        ex = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t), new IntegerExp(loc, (long)n, Type.tsize_t));
                        (((SliceExp)ex)).upperIsInBounds = true;
                        (((SliceExp)ex)).lowerIsLessThanUpper = true;
                        ex = new CallExp(loc, new IdentifierExp(loc, Id.__ArrayDtor), ex);
                    }
                    e = Expression.combine(ex, e);
                }
            }
            ClassDeclaration cldec = ad.isClassDeclaration();
            if ((cldec != null) && (cldec.classKind == ClassKind.cpp) && (cldec.baseClass != null) && (cldec.baseClass.primaryDtor != null))
            {
                cldec.baseClass.dtor.functionSemantic();
                stc = mergeFuncAttrs(stc, cldec.baseClass.primaryDtor);
                if ((stc & 137438953472L) == 0)
                {
                    Expression ex = new SuperExp(loc);
                    ex = new CastExp(loc, ex, cldec.baseClass.type.mutableOf());
                    if ((stc & 8589934592L) != 0)
                    {
                        stc = stc & -8589934593L | 17179869184L;
                    }
                    ex = new DotVarExp(loc, ex, cldec.baseClass.primaryDtor, false);
                    ex = new CallExp(loc, ex);
                    e = Expression.combine(e, ex);
                }
            }
            if ((e != null) || ((stc & 137438953472L) != 0))
            {
                DtorDeclaration dd = new DtorDeclaration(declLoc, Loc.initial, stc, Id.__fieldDtor);
                dd.generated = true;
                dd.storage_class |= 70368744177664L;
                dd.fbody.value = new ExpStatement(loc, e);
                ad.dtors.shift(dd);
                (ad.members).push(dd);
                dsymbolSemantic(dd, sc);
                ad.fieldDtor = dd;
            }
        }
        DtorDeclaration xdtor = null;
        switch (ad.dtors.length)
        {
            case 0:
                break;
            case 1:
                xdtor = ad.dtors.get(0);
                break;
            default:
            assert(!dtorIsCppPrototype);
            Expression e = null;
            e = null;
            stc = 4406737108992L;
            {
                int i = 0;
                for (; (i < ad.dtors.length);i++){
                    FuncDeclaration fd = ad.dtors.get(i);
                    stc = mergeFuncAttrs(stc, fd);
                    if ((stc & 137438953472L) != 0)
                    {
                        e = null;
                        break;
                    }
                    Expression ex = new ThisExp(loc);
                    ex = new DotVarExp(loc, ex, fd, false);
                    ex = new CallExp(loc, ex);
                    e = Expression.combine(ex, e);
                }
            }
            DtorDeclaration dd = new DtorDeclaration(declLoc, Loc.initial, stc, Id.__aggrDtor);
            dd.generated = true;
            dd.storage_class |= 70368744177664L;
            dd.fbody.value = new ExpStatement(loc, e);
            (ad.members).push(dd);
            dsymbolSemantic(dd, sc);
            xdtor = dd;
            break;
        }
        ad.primaryDtor = xdtor;
        if ((xdtor != null) && (xdtor.linkage == LINK.cpp) && !target.twoDtorInVtable)
        {
            xdtor = buildWindowsCppDtor(ad, xdtor, sc);
        }
        if (xdtor != null)
        {
            AliasDeclaration _alias = new AliasDeclaration(Loc.initial, Id.__xdtor, xdtor);
            dsymbolSemantic(_alias, sc);
            (ad.members).push(_alias);
            _alias.addMember(sc, ad);
        }
        return xdtor;
    }

    // Erasure: buildWindowsCppDtor<AggregateDeclaration, DtorDeclaration, Ptr>
    public static DtorDeclaration buildWindowsCppDtor(AggregateDeclaration ad, DtorDeclaration dtor, Ptr<Scope> sc) {
        ClassDeclaration cldec = ad.isClassDeclaration();
        if ((cldec == null) || (cldec.cppDtorVtblIndex == -1))
        {
            return dtor;
        }
        Parameter delparam = new Parameter(0L, Type.tuns32, Identifier.idPool(new ByteSlice("del")), new IntegerExp(dtor.loc, 0L, Type.tuns32), null);
        DArray<Parameter> params = new DArray<Parameter>();
        (params).push(delparam);
        TypeFunction ftype = new TypeFunction(new ParameterList(params, VarArg.none), Type.tvoidptr, LINK.cpp, dtor.storage_class);
        DtorDeclaration func = new DtorDeclaration(dtor.loc, dtor.loc, dtor.storage_class, Id.cppdtor);
        func.type = ftype;
        if (dtor.fbody.value != null)
        {
            Loc loc = dtor.loc.copy();
            DArray<Statement> stmts = new DArray<Statement>();
            CallExp call = new CallExp(loc, dtor, null);
            call.directcall = true;
            (stmts).push(new ExpStatement(loc, call));
            (stmts).push(new ReturnStatement(loc, new CastExp(loc, new ThisExp(loc), Type.tvoidptr)));
            func.fbody.value = new CompoundStatement(loc, stmts);
            func.generated = true;
        }
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc &= -2L;
        (sc2.get()).linkage = LINK.cpp;
        (ad.members).push(func);
        func.addMember(sc2, ad);
        dsymbolSemantic(func, sc2);
        (sc2.get()).pop();
        return func;
    }

    // Erasure: buildExternDDtor<AggregateDeclaration, Ptr>
    public static DtorDeclaration buildExternDDtor(AggregateDeclaration ad, Ptr<Scope> sc) {
        DtorDeclaration dtor = ad.primaryDtor;
        if (dtor == null)
        {
            return null;
        }
        if ((ad.classKind != ClassKind.cpp) || global.params.is64bit)
        {
            return dtor;
        }
        TypeFunction ftype = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid, LINK.d, dtor.storage_class);
        DtorDeclaration func = new DtorDeclaration(dtor.loc, dtor.loc, dtor.storage_class, Id.ticppdtor);
        func.type = ftype;
        CallExp call = new CallExp(dtor.loc, dtor, null);
        call.directcall = true;
        func.fbody.value = new ExpStatement(dtor.loc, call);
        func.generated = true;
        func.storage_class |= 70368744177664L;
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc &= -2L;
        (sc2.get()).linkage = LINK.d;
        (ad.members).push(func);
        func.addMember(sc2, ad);
        dsymbolSemantic(func, sc2);
        func.functionSemantic();
        (sc2.get()).pop();
        return func;
    }

    // Erasure: buildInv<AggregateDeclaration, Ptr>
    public static FuncDeclaration buildInv(AggregateDeclaration ad, Ptr<Scope> sc) {
        {
            int __dispatch2 = 0;
            dispatched_2:
            do {
                switch (__dispatch2 != 0 ? __dispatch2 : ad.invs.length)
                {
                    case 0:
                        return null;
                    case 1:
                        /*goto default*/ { __dispatch2 = -1; continue dispatched_2; }
                    default:
                    __dispatch2 = 0;
                    Expression e = null;
                    long stcx = 0L;
                    long stc = 4406737108992L;
                    {
                        Slice<FuncDeclaration> __r814 = ad.invs.opSlice().copy();
                        int __key813 = 0;
                        for (; (__key813 < __r814.getLength());__key813 += 1) {
                            FuncDeclaration inv = __r814.get(__key813);
                            int i = __key813;
                            stc = mergeFuncAttrs(stc, inv);
                            if ((stc & 137438953472L) != 0)
                            {
                            }
                            long stcy = inv.storage_class & 512L | (((inv.type.mod & 0xFF) & MODFlags.shared_) != 0 ? 536870912L : 0L);
                            if ((i == 0))
                            {
                                stcx = stcy;
                            }
                            else if ((stcx ^ stcy) != 0)
                            {
                                ad.error(inv.loc, new BytePtr("mixing invariants with different `shared`/`synchronized` qualifiers is not supported"));
                                e = null;
                                break;
                            }
                            e = Expression.combine(e, new CallExp(Loc.initial, new VarExp(Loc.initial, inv, false)));
                        }
                    }
                    InvariantDeclaration inv_1 = new InvariantDeclaration(ad.loc, Loc.initial, stc | stcx, Id.classInvariant, new ExpStatement(Loc.initial, e));
                    (ad.members).push(inv_1);
                    dsymbolSemantic(inv_1, sc);
                    return inv_1;
                }
            } while(__dispatch2 != 0);
        }
    }

}
