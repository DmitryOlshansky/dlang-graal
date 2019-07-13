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

    public static long mergeFuncAttrs(long s1, FuncDeclaration f) {
        if (f == null)
        {
            return s1;
        }
        long s2 = f.storage_class.value & 137438953472L;
        TypeFunction tf = (TypeFunction)f.type.value;
        if ((tf.trust.value == TRUST.safe))
        {
            s2 |= 8589934592L;
        }
        else if ((tf.trust.value == TRUST.system))
        {
            s2 |= 34359738368L;
        }
        else if ((tf.trust.value == TRUST.trusted))
        {
            s2 |= 17179869184L;
        }
        if ((tf.purity.value != PURE.impure))
        {
            s2 |= 67108864L;
        }
        if (tf.isnothrow.value)
        {
            s2 |= 33554432L;
        }
        if (tf.isnogc.value)
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

    public static FuncDeclaration hasIdentityOpAssign(AggregateDeclaration ad, Ptr<Scope> sc) {
        Dsymbol assign = search_function(ad, Id.assign.value);
        if (assign != null)
        {
            NullExp er = new NullExp(ad.loc.value, ad.type.value);
            IdentifierExp el = new IdentifierExp(ad.loc.value, Id.p.value);
            el.type.value = ad.type.value;
            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
            try {
                a.value.setDim(1);
                int errors = global.startGagging();
                sc = (sc.get()).push();
                (sc.get()).tinst = null;
                (sc.get()).minst.value = null;
                a.value.set(0, er);
                FuncDeclaration f = resolveFuncCall(ad.loc.value, sc, assign, null, ad.type.value, ptr(a), FuncResolveFlag.quiet);
                if (f == null)
                {
                    a.value.set(0, el);
                    f = resolveFuncCall(ad.loc.value, sc, assign, null, ad.type.value, ptr(a), FuncResolveFlag.quiet);
                }
                sc = (sc.get()).pop();
                global.endGagging(errors);
                if (f != null)
                {
                    if (f.errors.value)
                    {
                        return null;
                    }
                    ParameterList fparams = f.getParameterList().copy();
                    if (fparams.length() != 0)
                    {
                        Parameter fparam0 = fparams.get(0);
                        if ((!pequals(fparam0.type.value.toDsymbol(null), ad)))
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

    public static boolean needOpAssign(StructDeclaration sd) {
        Function0<Boolean> isNeeded = new Function0<Boolean>(){
            public Boolean invoke() {
                return true;
            }
        };
        if (sd.isUnionDeclaration() != null)
        {
            return !isNeeded.invoke();
        }
        if (sd.hasIdentityAssign.value || (sd.dtor.value != null) || (sd.postblit.value != null))
        {
            return isNeeded.invoke();
        }
        {
            Slice<VarDeclaration> __r807 = sd.fields.opSlice().copy();
            int __key808 = 0;
            for (; (__key808 < __r807.getLength());__key808 += 1) {
                VarDeclaration v = __r807.get(__key808);
                if ((v.storage_class.value & 2097152L) != 0)
                {
                    continue;
                }
                if (v.overlapped.value)
                {
                    continue;
                }
                Type tv = v.type.value.baseElemOf();
                if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    TypeStruct ts = (TypeStruct)tv;
                    if (ts.sym.value.isUnionDeclaration() != null)
                    {
                        continue;
                    }
                    if (needOpAssign(ts.sym.value))
                    {
                        return isNeeded.invoke();
                    }
                }
            }
        }
        return !isNeeded.invoke();
    }

    public static FuncDeclaration buildOpAssign(StructDeclaration sd, Ptr<Scope> sc) {
        {
            FuncDeclaration f = hasIdentityOpAssign(sd, sc);
            if ((f) != null)
            {
                sd.hasIdentityAssign.value = true;
                return f;
            }
        }
        if (!needOpAssign(sd))
        {
            return null;
        }
        long stc = 4406737108992L;
        Loc declLoc = sd.loc.value.copy();
        Loc loc = new Loc();
        {
            Slice<VarDeclaration> __r809 = sd.fields.opSlice().copy();
            int __key810 = 0;
            for (; (__key810 < __r809.getLength());__key810 += 1) {
                VarDeclaration v = __r809.get(__key810);
                if ((v.storage_class.value & 2097152L) != 0)
                {
                    continue;
                }
                if (v.overlapped.value)
                {
                    continue;
                }
                Type tv = v.type.value.baseElemOf();
                if (((tv.ty.value & 0xFF) != ENUMTY.Tstruct))
                {
                    continue;
                }
                StructDeclaration sdv = ((TypeStruct)tv).sym.value;
                stc = mergeFuncAttrs(stc, hasIdentityOpAssign(sdv, sc));
            }
        }
        if ((sd.dtor.value != null) || (sd.postblit.value != null))
        {
            if (!sd.type.value.isAssignable())
            {
                return null;
            }
            stc = mergeFuncAttrs(stc, sd.dtor.value);
            if ((stc & 8589934592L) != 0)
            {
                stc = stc & -8589934593L | 17179869184L;
            }
        }
        Ptr<DArray<Parameter>> fparams = refPtr(new DArray<Parameter>());
        (fparams.get()).push(new Parameter(16777216L, sd.type.value, Id.p.value, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(fparams, VarArg.none), sd.handleType(), LINK.d, stc | 2097152L);
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial.value, Id.assign.value, stc, tf);
        fop.storage_class.value |= 70368744177664L;
        fop.generated = true;
        Expression e = null;
        if ((stc & 137438953472L) != 0)
        {
            e = null;
        }
        else if (sd.dtor.value != null)
        {
            TypeFunction tdtor = (TypeFunction)sd.dtor.value.type.value;
            assert(((tdtor.ty.value & 0xFF) == ENUMTY.Tfunction));
            Identifier idswap = Identifier.generateId(new BytePtr("__swap"));
            VarDeclaration swap = new VarDeclaration(loc, sd.type.value, idswap, new VoidInitializer(loc), 0L);
            swap.storage_class.value |= 1168247881728L;
            if (tdtor.isscope.value)
            {
                swap.storage_class.value |= 524288L;
            }
            DeclarationExp e1 = new DeclarationExp(loc, swap);
            BlitExp e2 = new BlitExp(loc, new VarExp(loc, swap, true), new ThisExp(loc));
            BlitExp e3 = new BlitExp(loc, new ThisExp(loc), new IdentifierExp(loc, Id.p.value));
            CallExp e4 = new CallExp(loc, new DotVarExp(loc, new VarExp(loc, swap, true), sd.dtor.value, false));
            e = Expression.combine(e1, e2, e3, e4);
        }
        else if (sd.postblit.value != null)
        {
            e = new BlitExp(loc, new ThisExp(loc), new IdentifierExp(loc, Id.p.value));
        }
        else
        {
            e = null;
            {
                Slice<VarDeclaration> __r811 = sd.fields.opSlice().copy();
                int __key812 = 0;
                for (; (__key812 < __r811.getLength());__key812 += 1) {
                    VarDeclaration v = __r811.get(__key812);
                    AssignExp ec = new AssignExp(loc, new DotVarExp(loc, new ThisExp(loc), v, true), new DotVarExp(loc, new IdentifierExp(loc, Id.p.value), v, true));
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
            tf.isreturn.value = true;
        }
        (sd.members.value.get()).push(fop);
        fop.addMember(sc, sd);
        sd.hasIdentityAssign.value = true;
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc.value = 0L;
        (sc2.get()).linkage.value = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            fop.storage_class.value |= 137438953472L;
            fop.fbody.value = null;
        }
        return fop;
    }

    public static boolean needOpEquals(StructDeclaration sd) {
        try {
            try {
                if (sd.isUnionDeclaration() != null)
                {
                    /*goto Ldontneed*/throw Dispatch0.INSTANCE;
                }
                if (sd.hasIdentityEquals.value)
                {
                    /*goto Lneed*/throw Dispatch1.INSTANCE;
                }
                {
                    int i = 0;
                L_outer1:
                    for (; (i < sd.fields.length.value);i++){
                        VarDeclaration v = sd.fields.get(i);
                        if ((v.storage_class.value & 2097152L) != 0)
                        {
                            continue L_outer1;
                        }
                        if (v.overlapped.value)
                        {
                            continue L_outer1;
                        }
                        Type tv = v.type.value.toBasetype();
                        Type tvbase = tv.baseElemOf();
                        if (((tvbase.ty.value & 0xFF) == ENUMTY.Tstruct))
                        {
                            TypeStruct ts = (TypeStruct)tvbase;
                            if (ts.sym.value.isUnionDeclaration() != null)
                            {
                                continue L_outer1;
                            }
                            if (needOpEquals(ts.sym.value))
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                            if (ts.sym.value.aliasthis.value != null)
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                        }
                        if (tv.isfloating())
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty.value & 0xFF) == ENUMTY.Tarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty.value & 0xFF) == ENUMTY.Taarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty.value & 0xFF) == ENUMTY.Tclass))
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

    public static FuncDeclaration hasIdentityOpEquals(AggregateDeclaration ad, Ptr<Scope> sc) {
        Dsymbol eq = search_function(ad, Id.eq.value);
        if (eq != null)
        {
            NullExp er = new NullExp(ad.loc.value, null);
            IdentifierExp el = new IdentifierExp(ad.loc.value, Id.p.value);
            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
            try {
                a.value.setDim(1);
                {
                    int __key813 = 0;
                    int __limit814 = 5;
                    for (; (__key813 < __limit814);__key813 += 1) {
                        int i = __key813;
                        Type tthis = null;
                        switch (i)
                        {
                            case 0:
                                tthis = ad.type.value;
                                break;
                            case 1:
                                tthis = ad.type.value.constOf();
                                break;
                            case 2:
                                tthis = ad.type.value.immutableOf();
                                break;
                            case 3:
                                tthis = ad.type.value.sharedOf();
                                break;
                            case 4:
                                tthis = ad.type.value.sharedConstOf();
                                break;
                            default:
                            throw SwitchError.INSTANCE;
                        }
                        FuncDeclaration f = null;
                        int errors = global.startGagging();
                        sc = (sc.get()).push();
                        (sc.get()).tinst = null;
                        (sc.get()).minst.value = null;
                        {
                            int __key815 = 0;
                            int __limit816 = 2;
                            for (; (__key815 < __limit816);__key815 += 1) {
                                int j = __key815;
                                a.value.set(0, (j == 0) ? er : el);
                                a.value.get(0).type.value = tthis;
                                f = resolveFuncCall(ad.loc.value, sc, eq, null, tthis, ptr(a), FuncResolveFlag.quiet);
                                if (f != null)
                                {
                                    break;
                                }
                            }
                        }
                        sc = (sc.get()).pop();
                        global.endGagging(errors);
                        if (f != null)
                        {
                            if (f.errors.value)
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

    public static FuncDeclaration buildOpEquals(StructDeclaration sd, Ptr<Scope> sc) {
        if (hasIdentityOpEquals(sd, sc) != null)
        {
            sd.hasIdentityEquals.value = true;
        }
        return null;
    }

    public static FuncDeclaration buildXopEquals(StructDeclaration sd, Ptr<Scope> sc) {
        if (!needOpEquals(sd))
        {
            return null;
        }
        {
            Dsymbol eq = search_function(sd, Id.eq.value);
            if ((eq) != null)
            {
                {
                    FuncDeclaration fd = eq.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        TypeFunction tfeqptr = null;
                        {
                            Ref<Scope> scx = ref(new Scope().copy());
                            Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
                            (parameters.get()).push(new Parameter(2097156L, sd.type.value, null, null, null));
                            tfeqptr = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tbool.value, LINK.d, 0L);
                            tfeqptr.mod.value = (byte)1;
                            tfeqptr = (TypeFunction)typeSemantic(tfeqptr, Loc.initial.value, ptr(scx));
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
        if (StructDeclaration.xerreq.value == null)
        {
            Identifier id = Identifier.idPool(new ByteSlice("_xopEquals"));
            Expression e = new IdentifierExp(sd.loc.value, Id.empty.value);
            e = new DotIdExp(sd.loc.value, e, Id.object.value);
            e = new DotIdExp(sd.loc.value, e, id);
            e = expressionSemantic(e, sc);
            Dsymbol s = getDsymbol(e);
            assert(s != null);
            StructDeclaration.xerreq.value = s.isFuncDeclaration();
        }
        Loc declLoc = new Loc();
        Loc loc = new Loc();
        Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
        (parameters.get()).push(new Parameter(2097156L, sd.type.value, Id.p.value, null, null)).push(new Parameter(2097156L, sd.type.value, Id.q, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tbool.value, LINK.d, 0L);
        Identifier id = Id.xopEquals;
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial.value, id, 1L, tf);
        fop.generated = true;
        Expression e1 = new IdentifierExp(loc, Id.p.value);
        Expression e2 = new IdentifierExp(loc, Id.q);
        Expression e = new EqualExp(TOK.equal, loc, e1, e2);
        fop.fbody.value = new ReturnStatement(loc, e);
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc.value = 0L;
        (sc2.get()).linkage.value = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            fop = StructDeclaration.xerreq.value;
        }
        return fop;
    }

    public static FuncDeclaration buildXopCmp(StructDeclaration sd, Ptr<Scope> sc) {
        {
            Dsymbol cmp = search_function(sd, Id.cmp.value);
            if ((cmp) != null)
            {
                {
                    FuncDeclaration fd = cmp.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        TypeFunction tfcmpptr = null;
                        {
                            Ref<Scope> scx = ref(new Scope().copy());
                            Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
                            (parameters.get()).push(new Parameter(2097156L, sd.type.value, null, null, null));
                            tfcmpptr = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tint32.value, LINK.d, 0L);
                            tfcmpptr.mod.value = (byte)1;
                            tfcmpptr = (TypeFunction)typeSemantic(tfcmpptr, Loc.initial.value, ptr(scx));
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
            Expression e = new IdentifierExp(sd.loc.value, Id.empty.value);
            e = new DotIdExp(sd.loc.value, e, Id.object.value);
            e = new DotIdExp(sd.loc.value, e, id);
            e = expressionSemantic(e, sc);
            Dsymbol s = getDsymbol(e);
            assert(s != null);
            StructDeclaration.xerrcmp = s.isFuncDeclaration();
        }
        Loc declLoc = new Loc();
        Loc loc = new Loc();
        Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
        (parameters.get()).push(new Parameter(2097156L, sd.type.value, Id.p.value, null, null));
        (parameters.get()).push(new Parameter(2097156L, sd.type.value, Id.q, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.tint32.value, LINK.d, 0L);
        Identifier id = Id.xopCmp;
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial.value, id, 1L, tf);
        fop.generated = true;
        Expression e1 = new IdentifierExp(loc, Id.p.value);
        Expression e2 = new IdentifierExp(loc, Id.q);
        Expression e = new CallExp(loc, new DotIdExp(loc, e2, Id.cmp.value), e1);
        fop.fbody.value = new ReturnStatement(loc, e);
        int errors = global.startGagging();
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc.value = 0L;
        (sc2.get()).linkage.value = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        if (global.endGagging(errors))
        {
            fop = StructDeclaration.xerrcmp;
        }
        return fop;
    }

    public static boolean needToHash(StructDeclaration sd) {
        try {
            try {
                if (sd.isUnionDeclaration() != null)
                {
                    /*goto Ldontneed*/throw Dispatch0.INSTANCE;
                }
                if (sd.xhash.value != null)
                {
                    /*goto Lneed*/throw Dispatch1.INSTANCE;
                }
                {
                    int i = 0;
                L_outer2:
                    for (; (i < sd.fields.length.value);i++){
                        VarDeclaration v = sd.fields.get(i);
                        if ((v.storage_class.value & 2097152L) != 0)
                        {
                            continue L_outer2;
                        }
                        if (v.overlapped.value)
                        {
                            continue L_outer2;
                        }
                        Type tv = v.type.value.toBasetype();
                        Type tvbase = tv.baseElemOf();
                        if (((tvbase.ty.value & 0xFF) == ENUMTY.Tstruct))
                        {
                            TypeStruct ts = (TypeStruct)tvbase;
                            if (ts.sym.value.isUnionDeclaration() != null)
                            {
                                continue L_outer2;
                            }
                            if (needToHash(ts.sym.value))
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                            if (ts.sym.value.aliasthis.value != null)
                            {
                                /*goto Lneed*/throw Dispatch1.INSTANCE;
                            }
                        }
                        if (tv.isfloating())
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty.value & 0xFF) == ENUMTY.Tarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty.value & 0xFF) == ENUMTY.Taarray))
                        {
                            /*goto Lneed*/throw Dispatch1.INSTANCE;
                        }
                        if (((tv.ty.value & 0xFF) == ENUMTY.Tclass))
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

    public static FuncDeclaration buildXtoHash(StructDeclaration sd, Ptr<Scope> sc) {
        {
            Dsymbol s = search_function(sd, Id.tohash);
            if ((s) != null)
            {
                if (clone.buildXtoHashtftohash == null)
                {
                    clone.buildXtoHashtftohash = new TypeFunction(new ParameterList(null, VarArg.none), Type.thash_t, LINK.d, 0L);
                    clone.buildXtoHashtftohash.mod.value = (byte)1;
                    clone.buildXtoHashtftohash = (TypeFunction)merge(clone.buildXtoHashtftohash);
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
        Ptr<DArray<Parameter>> parameters = refPtr(new DArray<Parameter>());
        (parameters.get()).push(new Parameter(2097156L, sd.type.value, Id.p.value, null, null));
        TypeFunction tf = new TypeFunction(new ParameterList(parameters, VarArg.none), Type.thash_t, LINK.d, 17213423616L);
        Identifier id = Id.xtoHash;
        FuncDeclaration fop = new FuncDeclaration(declLoc, Loc.initial.value, id, 1L, tf);
        fop.generated = true;
        BytePtr code = pcopy(new BytePtr("size_t h = 0;foreach (i, T; typeof(p.tupleof))    static if(is(T* : const(.object.Object)*))         h = h * 33 + typeid(const(.object.Object)).getHash(cast(const void*)&p.tupleof[i]);    else         h = h * 33 + typeid(T).getHash(cast(const void*)&p.tupleof[i]);return h;"));
        fop.fbody.value = new CompileStatement(loc, new StringExp(loc, code));
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc.value = 0L;
        (sc2.get()).linkage.value = LINK.d;
        dsymbolSemantic(fop, sc2);
        semantic2(fop, sc2);
        (sc2.get()).pop();
        return fop;
    }

    public static DtorDeclaration buildDtor(AggregateDeclaration ad, Ptr<Scope> sc) {
        if (ad.isUnionDeclaration() != null)
        {
            return null;
        }
        long stc = 4406737108992L;
        Loc declLoc = ad.dtors.length != 0 ? ad.dtors.get(0).loc.value : ad.loc.value.copy();
        Loc loc = new Loc();
        boolean dtorIsCppPrototype = (ad.dtors.length == 1) && (ad.dtors.get(0).linkage.value == LINK.cpp) && (ad.dtors.get(0).fbody.value == null);
        if (!dtorIsCppPrototype)
        {
            Expression e = null;
            {
                int i = 0;
                for (; (i < ad.fields.length.value);i++){
                    VarDeclaration v = ad.fields.get(i);
                    if ((v.storage_class.value & 2097152L) != 0)
                    {
                        continue;
                    }
                    if (v.overlapped.value)
                    {
                        continue;
                    }
                    Type tv = v.type.value.baseElemOf();
                    if (((tv.ty.value & 0xFF) != ENUMTY.Tstruct))
                    {
                        continue;
                    }
                    StructDeclaration sdv = ((TypeStruct)tv).sym.value;
                    if (sdv.dtor.value == null)
                    {
                        continue;
                    }
                    sdv.dtor.value.functionSemantic();
                    stc = mergeFuncAttrs(stc, sdv.dtor.value);
                    if ((stc & 137438953472L) != 0)
                    {
                        e = null;
                        break;
                    }
                    Expression ex = null;
                    tv = v.type.value.toBasetype();
                    if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        ex = new ThisExp(loc);
                        ex = new DotVarExp(loc, ex, v, true);
                        ex = new CastExp(loc, ex, v.type.value.mutableOf());
                        if ((stc & 8589934592L) != 0)
                        {
                            stc = stc & -8589934593L | 17179869184L;
                        }
                        ex = new DotVarExp(loc, ex, sdv.dtor.value, false);
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
                        ex = new DotIdExp(loc, ex, Id.ptr.value);
                        ex = new CastExp(loc, ex, sdv.type.value.pointerTo());
                        if ((stc & 8589934592L) != 0)
                        {
                            stc = stc & -8589934593L | 17179869184L;
                        }
                        ex = new SliceExp(loc, ex, new IntegerExp(loc, 0L, Type.tsize_t.value), new IntegerExp(loc, (long)n, Type.tsize_t.value));
                        ((SliceExp)ex).upperIsInBounds = true;
                        ((SliceExp)ex).lowerIsLessThanUpper = true;
                        ex = new CallExp(loc, new IdentifierExp(loc, Id.__ArrayDtor), ex);
                    }
                    e = Expression.combine(ex, e);
                }
            }
            ClassDeclaration cldec = ad.isClassDeclaration();
            if ((cldec != null) && (cldec.classKind.value == ClassKind.cpp) && (cldec.baseClass.value != null) && (cldec.baseClass.value.primaryDtor != null))
            {
                cldec.baseClass.value.dtor.value.functionSemantic();
                stc = mergeFuncAttrs(stc, cldec.baseClass.value.primaryDtor);
                if ((stc & 137438953472L) == 0)
                {
                    Expression ex = new SuperExp(loc);
                    ex = new CastExp(loc, ex, cldec.baseClass.value.type.value.mutableOf());
                    if ((stc & 8589934592L) != 0)
                    {
                        stc = stc & -8589934593L | 17179869184L;
                    }
                    ex = new DotVarExp(loc, ex, cldec.baseClass.value.primaryDtor, false);
                    ex = new CallExp(loc, ex);
                    e = Expression.combine(e, ex);
                }
            }
            if ((e != null) || ((stc & 137438953472L) != 0))
            {
                DtorDeclaration dd = new DtorDeclaration(declLoc, Loc.initial.value, stc, Id.__fieldDtor);
                dd.generated = true;
                dd.storage_class.value |= 70368744177664L;
                dd.fbody.value = new ExpStatement(loc, e);
                ad.dtors.shift(dd);
                (ad.members.value.get()).push(dd);
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
            DtorDeclaration dd = new DtorDeclaration(declLoc, Loc.initial.value, stc, Id.__aggrDtor);
            dd.generated = true;
            dd.storage_class.value |= 70368744177664L;
            dd.fbody.value = new ExpStatement(loc, e);
            (ad.members.value.get()).push(dd);
            dsymbolSemantic(dd, sc);
            xdtor = dd;
            break;
        }
        ad.primaryDtor = xdtor;
        if ((xdtor != null) && (xdtor.linkage.value == LINK.cpp) && !target.twoDtorInVtable)
        {
            xdtor = buildWindowsCppDtor(ad, xdtor, sc);
        }
        if (xdtor != null)
        {
            AliasDeclaration _alias = new AliasDeclaration(Loc.initial.value, Id.__xdtor.value, xdtor);
            dsymbolSemantic(_alias, sc);
            (ad.members.value.get()).push(_alias);
            _alias.addMember(sc, ad);
        }
        return xdtor;
    }

    public static DtorDeclaration buildWindowsCppDtor(AggregateDeclaration ad, DtorDeclaration dtor, Ptr<Scope> sc) {
        ClassDeclaration cldec = ad.isClassDeclaration();
        if ((cldec == null) || (cldec.cppDtorVtblIndex == -1))
        {
            return dtor;
        }
        Parameter delparam = new Parameter(0L, Type.tuns32.value, Identifier.idPool(new ByteSlice("del")), new IntegerExp(dtor.loc.value, 0L, Type.tuns32.value), null);
        Ptr<DArray<Parameter>> params = refPtr(new DArray<Parameter>());
        (params.get()).push(delparam);
        TypeFunction ftype = new TypeFunction(new ParameterList(params, VarArg.none), Type.tvoidptr.value, LINK.cpp, dtor.storage_class.value);
        DtorDeclaration func = new DtorDeclaration(dtor.loc.value, dtor.loc.value, dtor.storage_class.value, Id.cppdtor);
        func.type.value = ftype;
        if (dtor.fbody.value != null)
        {
            Loc loc = dtor.loc.value.copy();
            Ptr<DArray<Statement>> stmts = refPtr(new DArray<Statement>());
            CallExp call = new CallExp(loc, dtor, null);
            call.directcall = true;
            (stmts.get()).push(new ExpStatement(loc, call));
            (stmts.get()).push(new ReturnStatement(loc, new CastExp(loc, new ThisExp(loc), Type.tvoidptr.value)));
            func.fbody.value = new CompoundStatement(loc, stmts);
            func.generated = true;
        }
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc.value &= -2L;
        (sc2.get()).linkage.value = LINK.cpp;
        (ad.members.value.get()).push(func);
        func.addMember(sc2, ad);
        dsymbolSemantic(func, sc2);
        (sc2.get()).pop();
        return func;
    }

    public static DtorDeclaration buildExternDDtor(AggregateDeclaration ad, Ptr<Scope> sc) {
        DtorDeclaration dtor = ad.primaryDtor;
        if (dtor == null)
        {
            return null;
        }
        if ((ad.classKind.value != ClassKind.cpp) || global.params.is64bit)
        {
            return dtor;
        }
        TypeFunction ftype = new TypeFunction(new ParameterList(null, VarArg.none), Type.tvoid.value, LINK.d, dtor.storage_class.value);
        DtorDeclaration func = new DtorDeclaration(dtor.loc.value, dtor.loc.value, dtor.storage_class.value, Id.ticppdtor);
        func.type.value = ftype;
        CallExp call = new CallExp(dtor.loc.value, dtor, null);
        call.directcall = true;
        func.fbody.value = new ExpStatement(dtor.loc.value, call);
        func.generated = true;
        func.storage_class.value |= 70368744177664L;
        Ptr<Scope> sc2 = (sc.get()).push();
        (sc2.get()).stc.value &= -2L;
        (sc2.get()).linkage.value = LINK.d;
        (ad.members.value.get()).push(func);
        func.addMember(sc2, ad);
        dsymbolSemantic(func, sc2);
        func.functionSemantic();
        (sc2.get()).pop();
        return func;
    }

    public static FuncDeclaration buildInv(AggregateDeclaration ad, Ptr<Scope> sc) {
        {
            int __dispatch2 = 0;
            dispatched_2:
            do {
                switch (__dispatch2 != 0 ? __dispatch2 : ad.invs.length.value)
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
                        Slice<FuncDeclaration> __r818 = ad.invs.opSlice().copy();
                        int __key817 = 0;
                        for (; (__key817 < __r818.getLength());__key817 += 1) {
                            FuncDeclaration inv = __r818.get(__key817);
                            int i = __key817;
                            stc = mergeFuncAttrs(stc, inv);
                            if ((stc & 137438953472L) != 0)
                            {
                            }
                            long stcy = inv.storage_class.value & 512L | (((inv.type.value.mod.value & 0xFF) & MODFlags.shared_) != 0 ? 536870912L : 0L);
                            if ((i == 0))
                            {
                                stcx = stcy;
                            }
                            else if ((stcx ^ stcy) != 0)
                            {
                                ad.error(inv.loc.value, new BytePtr("mixing invariants with different `shared`/`synchronized` qualifiers is not supported"));
                                e = null;
                                break;
                            }
                            e = Expression.combine(e, new CallExp(Loc.initial.value, new VarExp(Loc.initial.value, inv, false)));
                        }
                    }
                    InvariantDeclaration inv_1 = new InvariantDeclaration(ad.loc.value, Loc.initial.value, stc | stcx, Id.classInvariant, new ExpStatement(Loc.initial.value, e));
                    (ad.members.value.get()).push(inv_1);
                    dsymbolSemantic(inv_1, sc);
                    return inv_1;
                }
            } while(__dispatch2 != 0);
        }
    }

}
