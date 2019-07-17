package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.compiler.*;
import static org.dlang.dmd.complex.*;
import static org.dlang.dmd.constfold.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class ctfeexpr {

    public static class CtfeStatus
    {
        public static int callDepth = 0;
        public static int stackTraceCallsToSuppress = 0;
        public static int maxCallDepth = 0;
        public static int numArrayAllocs = 0;
        public static int numAssignments = 0;
        public CtfeStatus(){ }
        public CtfeStatus copy(){
            CtfeStatus r = new CtfeStatus();
            return r;
        }
        public CtfeStatus opAssign(CtfeStatus that) {
            return this;
        }
    }
    public static class ClassReferenceExp extends Expression
    {
        public StructLiteralExp value = null;
        // Erasure: __ctor<Loc, StructLiteralExp, Type>
        public  ClassReferenceExp(Loc loc, StructLiteralExp lit, Type type) {
            super(loc, TOK.classReference, 28);
            assert((lit != null) && (lit.sd != null) && (lit.sd.isClassDeclaration() != null));
            this.value = lit;
            this.type.value = type;
        }

        // Erasure: originalClass<>
        public  ClassDeclaration originalClass() {
            return this.value.sd.isClassDeclaration();
        }

        // Erasure: getFieldIndex<Type, int>
        public  int getFieldIndex(Type fieldtype, int fieldoffset) {
            ClassDeclaration cd = this.originalClass();
            int fieldsSoFar = 0;
            {
                int j = 0;
                for (; (j < (this.value.elements).length);j++){
                    for (; (j - fieldsSoFar >= cd.fields.length);){
                        fieldsSoFar += cd.fields.length;
                        cd = cd.baseClass;
                    }
                    VarDeclaration v2 = cd.fields.get(j - fieldsSoFar);
                    if ((fieldoffset == v2.offset) && (fieldtype.size() == v2.type.size()))
                    {
                        return ((this.value.elements).length - fieldsSoFar - cd.fields.length + (j - fieldsSoFar));
                    }
                }
            }
            return -1;
        }

        // Erasure: findFieldIndexByName<VarDeclaration>
        public  int findFieldIndexByName(VarDeclaration v) {
            ClassDeclaration cd = this.originalClass();
            int fieldsSoFar = 0;
            {
                int j = 0;
                for (; (j < (this.value.elements).length);j++){
                    for (; (j - fieldsSoFar >= cd.fields.length);){
                        fieldsSoFar += cd.fields.length;
                        cd = cd.baseClass;
                    }
                    VarDeclaration v2 = cd.fields.get(j - fieldsSoFar);
                    if ((pequals(v, v2)))
                    {
                        return ((this.value.elements).length - fieldsSoFar - cd.fields.length + (j - fieldsSoFar));
                    }
                }
            }
            return -1;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ClassReferenceExp() {}

        public ClassReferenceExp copy() {
            ClassReferenceExp that = new ClassReferenceExp();
            that.value = this.value;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    // Erasure: findFieldIndexByName<StructDeclaration, VarDeclaration>
    public static int findFieldIndexByName(StructDeclaration sd, VarDeclaration v) {
        {
            Slice<VarDeclaration> __r867 = sd.fields.opSlice().copy();
            int __key866 = 0;
            for (; (__key866 < __r867.getLength());__key866 += 1) {
                VarDeclaration field = __r867.get(__key866);
                int i = __key866;
                if ((pequals(field, v)))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public static class ThrownExceptionExp extends Expression
    {
        public ClassReferenceExp thrown = null;
        // Erasure: __ctor<Loc, ClassReferenceExp>
        public  ThrownExceptionExp(Loc loc, ClassReferenceExp victim) {
            super(loc, TOK.thrownException, 28);
            this.thrown = victim;
            this.type.value = victim.type.value;
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return new BytePtr("CTFE ThrownException");
        }

        // Erasure: generateUncaughtError<>
        public  void generateUncaughtError() {
            Ref<UnionExp> ue = ref(null);
            Expression e = resolveSlice((this.thrown.value.elements).get(0), ptr(ue));
            StringExp se = e.toStringExp();
            this.thrown.error(new BytePtr("uncaught CTFE exception `%s(%s)`"), this.thrown.type.value.toChars(), se != null ? se.toChars() : e.toChars());
            if (this.loc.isValid() && !this.loc.equals(this.thrown.loc))
            {
                errorSupplemental(this.loc, new BytePtr("thrown from here"));
            }
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ThrownExceptionExp() {}

        public ThrownExceptionExp copy() {
            ThrownExceptionExp that = new ThrownExceptionExp();
            that.thrown = this.thrown;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CTFEExp extends Expression
    {
        // Erasure: __ctor<byte>
        public  CTFEExp(byte tok) {
            super(Loc.initial, tok, 24);
            this.type.value = Type.tvoid;
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            switch ((this.op & 0xFF))
            {
                case 233:
                    return new BytePtr("<cant>");
                case 232:
                    return new BytePtr("<void>");
                case 234:
                    return new BytePtr("<error>");
                case 191:
                    return new BytePtr("<break>");
                case 192:
                    return new BytePtr("<continue>");
                case 196:
                    return new BytePtr("<goto>");
                default:
                throw new AssertionError("Unreachable code!");
            }
        }

        public static CTFEExp cantexp = null;
        public static CTFEExp voidexp = null;
        public static CTFEExp breakexp = null;
        public static CTFEExp continueexp = null;
        public static CTFEExp gotoexp = null;
        public static CTFEExp showcontext = null;
        // Erasure: isCantExp<Expression>
        public static boolean isCantExp(Expression e) {
            return (e != null) && ((e.op & 0xFF) == 233);
        }

        // Erasure: isGotoExp<Expression>
        public static boolean isGotoExp(Expression e) {
            return (e != null) && ((e.op & 0xFF) == 196);
        }


        public CTFEExp() {}

        public CTFEExp copy() {
            CTFEExp that = new CTFEExp();
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    // Erasure: exceptionOrCantInterpret<Expression>
    public static boolean exceptionOrCantInterpret(Expression e) {
        return (e != null) && ((e.op & 0xFF) == 233) || ((e.op & 0xFF) == 51) || ((e.op & 0xFF) == 234);
    }

    // Erasure: needToCopyLiteral<Expression>
    public static boolean needToCopyLiteral(Expression expr) {
        Expression e = expr;
        for (; ;){
            switch ((e.op & 0xFF))
            {
                case 47:
                    return ((((ArrayLiteralExp)e)).ownedByCtfe & 0xFF) == 0;
                case 48:
                    return ((((AssocArrayLiteralExp)e)).ownedByCtfe & 0xFF) == 0;
                case 49:
                    return ((((StructLiteralExp)e)).ownedByCtfe & 0xFF) == 0;
                case 121:
                case 123:
                case 26:
                    return false;
                case 90:
                    return false;
                case 62:
                case 27:
                case 31:
                case 12:
                    e = (((UnaExp)e)).e1.value;
                    continue;
                case 70:
                    return needToCopyLiteral((((BinExp)e)).e1.value) || needToCopyLiteral((((BinExp)e)).e2.value);
                case 71:
                case 72:
                case 73:
                    e = (((BinExp)e)).e2.value;
                    continue;
                default:
                return false;
            }
        }
    }

    // Erasure: copyLiteralArray<Ptr, Expression>
    public static DArray<Expression> copyLiteralArray(DArray<Expression> oldelems, Expression basis) {
        if (oldelems == null)
        {
            return oldelems;
        }
        CtfeStatus.numArrayAllocs++;
        DArray<Expression> newelems = new DArray<Expression>((oldelems).length);
        {
            Slice<Expression> __r869 = (oldelems).opSlice().copy();
            int __key868 = 0;
            for (; (__key868 < __r869.getLength());__key868 += 1) {
                Expression el = __r869.get(__key868);
                int i = __key868;
                newelems.set(i, copyLiteral(el != null ? el : basis).copy());
            }
        }
        return newelems;
    }

    // defaulted all parameters starting with #2
    public static DArray<Expression> copyLiteralArray(DArray<Expression> oldelems) {
        return copyLiteralArray(oldelems, (Expression)null);
    }

    // Erasure: copyLiteral<Expression>
    public static UnionExp copyLiteral(Expression e) {
        Ref<UnionExp> ue = ref(null);
        if (((e.op & 0xFF) == 121))
        {
            StringExp se = ((StringExp)e);
            BytePtr s = pcopy(ptr(new byte[(se.sz & 0xFF)]));
            memcpy((BytePtr)(s), (se.string), (se.len * (se.sz & 0xFF)));
            ptr(ue) = new UnionExp(new StringExp(se.loc, s, se.len));
            StringExp se2 = ((StringExp)ue.value.exp());
            se2.committed = se.committed;
            se2.postfix = se.postfix;
            se2.type.value = se.type.value;
            se2.sz = se.sz;
            se2.ownedByCtfe = OwnedBy.ctfe;
            return ue.value;
        }
        if (((e.op & 0xFF) == 47))
        {
            ArrayLiteralExp ale = ((ArrayLiteralExp)e);
            DArray<Expression> elements = copyLiteralArray(ale.elements, ale.basis.value);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e.loc, e.type.value, elements));
            ArrayLiteralExp r = ((ArrayLiteralExp)ue.value.exp());
            r.ownedByCtfe = OwnedBy.ctfe;
            return ue.value;
        }
        if (((e.op & 0xFF) == 48))
        {
            AssocArrayLiteralExp aae = ((AssocArrayLiteralExp)e);
            ptr(ue) = new UnionExp(new AssocArrayLiteralExp(e.loc, copyLiteralArray(aae.keys, null), copyLiteralArray(aae.values, null)));
            AssocArrayLiteralExp r = ((AssocArrayLiteralExp)ue.value.exp());
            r.type.value = e.type.value;
            r.ownedByCtfe = OwnedBy.ctfe;
            return ue.value;
        }
        if (((e.op & 0xFF) == 49))
        {
            StructLiteralExp sle = ((StructLiteralExp)e);
            DArray<Expression> oldelems = sle.elements;
            DArray<Expression> newelems = new DArray<Expression>((oldelems).length);
            {
                Slice<Expression> __r871 = (newelems).opSlice().copy();
                int __key870 = 0;
                for (; (__key870 < __r871.getLength());__key870 += 1) {
                    Expression el = __r871.get(__key870);
                    int i = __key870;
                    VarDeclaration v = sle.sd.fields.get(i);
                    Expression m = (oldelems).get(i);
                    if (m == null)
                    {
                        m = voidInitLiteral(v.type, v).copy();
                    }
                    if (((v.type.ty & 0xFF) == ENUMTY.Tarray) || ((v.type.ty & 0xFF) == ENUMTY.Taarray))
                    {
                    }
                    else
                    {
                        m = copyLiteral(m).copy();
                        if (((v.type.ty & 0xFF) != (m.type.value.ty & 0xFF)) && ((v.type.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            TypeSArray tsa = ((TypeSArray)v.type);
                            int len = (int)tsa.dim.toInteger();
                            Ref<UnionExp> uex = ref(null);
                            m = createBlockDuplicatedArrayLiteral(ptr(uex), e.loc, v.type, m, len);
                            if ((pequals(m, uex.value.exp())))
                            {
                                m = uex.value.copy();
                            }
                        }
                    }
                    el = m;
                }
            }
            ptr(ue) = new UnionExp(new StructLiteralExp(e.loc, sle.sd, newelems, sle.stype));
            StructLiteralExp r = ((StructLiteralExp)ue.value.exp());
            r.type.value = e.type.value;
            r.ownedByCtfe = OwnedBy.ctfe;
            r.origin = (((StructLiteralExp)e)).origin;
            return ue.value;
        }
        if (((e.op & 0xFF) == 161) || ((e.op & 0xFF) == 160) || ((e.op & 0xFF) == 25) || ((e.op & 0xFF) == 13) || ((e.op & 0xFF) == 26) || ((e.op & 0xFF) == 27) || ((e.op & 0xFF) == 135) || ((e.op & 0xFF) == 140) || ((e.op & 0xFF) == 148) || ((e.op & 0xFF) == 147) || ((e.op & 0xFF) == 128) || ((e.op & 0xFF) == 229) || ((e.op & 0xFF) == 42))
        {
            ptr(ue) = new UnionExp(new UnionExp(e));
            Expression r = ue.value.exp();
            r.type.value = e.type.value;
            return ue.value;
        }
        if (((e.op & 0xFF) == 31))
        {
            SliceExp se = ((SliceExp)e);
            if (((se.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (((se.e1.value.op & 0xFF) == 13))
                {
                    ptr(ue) = new UnionExp(new NullExp(se.loc, se.type.value));
                    return ue.value;
                }
                ue.value.opAssign(Slice(se.type.value, se.e1.value, se.lwr.value, se.upr.value).copy());
                assert(((ue.value.exp().op & 0xFF) == 47));
                ArrayLiteralExp r = ((ArrayLiteralExp)ue.value.exp());
                r.elements = pcopy(copyLiteralArray(r.elements, null));
                r.ownedByCtfe = OwnedBy.ctfe;
                return ue.value;
            }
            else
            {
                ptr(ue) = new UnionExp(new SliceExp(e.loc, se.e1.value, se.lwr.value, se.upr.value));
                Expression r = ue.value.exp();
                r.type.value = e.type.value;
                return ue.value;
            }
        }
        if (isPointer(e.type.value))
        {
            if (((e.op & 0xFF) == 19))
            {
                ptr(ue) = new UnionExp(new AddrExp(e.loc, (((AddrExp)e)).e1.value));
            }
            else if (((e.op & 0xFF) == 62))
            {
                ptr(ue) = new UnionExp(new IndexExp(e.loc, (((IndexExp)e)).e1.value, (((IndexExp)e)).e2.value));
            }
            else if (((e.op & 0xFF) == 27))
            {
                ptr(ue) = new UnionExp(new DotVarExp(e.loc, (((DotVarExp)e)).e1.value, (((DotVarExp)e)).var, (((DotVarExp)e)).hasOverloads));
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            Expression r = ue.value.exp();
            r.type.value = e.type.value;
            return ue.value;
        }
        if (((e.op & 0xFF) == 50))
        {
            ptr(ue) = new UnionExp(new ClassReferenceExp(e.loc, (((ClassReferenceExp)e)).value, e.type.value));
            return ue.value;
        }
        if (((e.op & 0xFF) == 127))
        {
            ptr(ue) = new UnionExp(new UnionExp(e));
            return ue.value;
        }
        e.error(new BytePtr("CTFE internal error: literal `%s`"), e.toChars());
        throw new AssertionError("Unreachable code!");
    }

    // Erasure: paintTypeOntoLiteral<Type, Expression>
    public static Expression paintTypeOntoLiteral(Type type, Expression lit) {
        if (lit.type.value.equals(type))
        {
            return lit;
        }
        return paintTypeOntoLiteralCopy(type, lit).copy();
    }

    // Erasure: paintTypeOntoLiteral<Ptr, Type, Expression>
    public static Expression paintTypeOntoLiteral(Ptr<UnionExp> pue, Type type, Expression lit) {
        if (lit.type.value.equals(type))
        {
            return lit;
        }
        pue.set(0, paintTypeOntoLiteralCopy(type, lit));
        return (pue.get()).exp();
    }

    // Erasure: paintTypeOntoLiteralCopy<Type, Expression>
    public static UnionExp paintTypeOntoLiteralCopy(Type type, Expression lit) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (lit.type.value.equals(type))
        {
            ptr(ue) = new UnionExp(new UnionExp(lit));
            return ue.value;
        }
        if ((type.hasWild() != 0) && type.hasPointers())
        {
            ptr(ue) = new UnionExp(new UnionExp(lit));
            ue.value.exp().type.value = type;
            return ue.value;
        }
        if (((lit.op & 0xFF) == 31))
        {
            SliceExp se = ((SliceExp)lit);
            ptr(ue) = new UnionExp(new SliceExp(lit.loc, se.e1.value, se.lwr.value, se.upr.value));
        }
        else if (((lit.op & 0xFF) == 62))
        {
            IndexExp ie = ((IndexExp)lit);
            ptr(ue) = new UnionExp(new IndexExp(lit.loc, ie.e1.value, ie.e2.value));
        }
        else if (((lit.op & 0xFF) == 47))
        {
            ptr(ue) = new UnionExp(new SliceExp(lit.loc, lit, new IntegerExp(Loc.initial, 0L, Type.tsize_t), ArrayLength(Type.tsize_t, lit).copy()));
        }
        else if (((lit.op & 0xFF) == 121))
        {
            ptr(ue) = new UnionExp(new SliceExp(lit.loc, lit, new IntegerExp(Loc.initial, 0L, Type.tsize_t), ArrayLength(Type.tsize_t, lit).copy()));
        }
        else if (((lit.op & 0xFF) == 48))
        {
            AssocArrayLiteralExp aae = ((AssocArrayLiteralExp)lit);
            byte wasOwned = aae.ownedByCtfe;
            ptr(ue) = new UnionExp(new AssocArrayLiteralExp(lit.loc, aae.keys, aae.values));
            aae = ((AssocArrayLiteralExp)ue.value.exp());
            aae.ownedByCtfe = wasOwned;
        }
        else
        {
            if (((lit.op & 0xFF) == 49) && isPointer(type))
            {
                lit.error(new BytePtr("CTFE internal error: painting `%s`"), type.toChars());
            }
            ue.value.opAssign(copyLiteral(lit).copy());
        }
        ue.value.exp().type.value = type;
        return ue.value;
    }

    // Erasure: resolveSlice<Expression, Ptr>
    public static Expression resolveSlice(Expression e, Ptr<UnionExp> pue) {
        if (((e.op & 0xFF) != 31))
        {
            return e;
        }
        SliceExp se = ((SliceExp)e);
        if (((se.e1.value.op & 0xFF) == 13))
        {
            return se.e1.value;
        }
        if (pue != null)
        {
            pue.set(0, Slice(e.type.value, se.e1.value, se.lwr.value, se.upr.value));
            return (pue.get()).exp();
        }
        else
        {
            return Slice(e.type.value, se.e1.value, se.lwr.value, se.upr.value).copy();
        }
    }

    // defaulted all parameters starting with #2
    public static Expression resolveSlice(Expression e) {
        return resolveSlice(e, (Ptr<UnionExp>)null);
    }

    // Erasure: resolveArrayLength<Expression>
    public static long resolveArrayLength(Expression e) {
        switch ((e.op & 0xFF))
        {
            case 229:
                return (long)(((VectorExp)e)).dim;
            case 13:
                return 0L;
            case 31:
                long ilo = (((SliceExp)e)).lwr.value.toInteger();
                long iup = (((SliceExp)e)).upr.value.toInteger();
                return iup - ilo;
            case 121:
                return (long)(((StringExp)e)).len;
            case 47:
                ArrayLiteralExp ale = ((ArrayLiteralExp)e);
                return ale.elements != null ? (long)(ale.elements).length : 0L;
            case 48:
                AssocArrayLiteralExp ale_1 = ((AssocArrayLiteralExp)e);
                return (long)(ale_1.keys).length;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }

    // Erasure: createBlockDuplicatedArrayLiteral<Ptr, Loc, Type, Expression, int>
    public static ArrayLiteralExp createBlockDuplicatedArrayLiteral(Ptr<UnionExp> pue, Loc loc, Type type, Expression elem, int dim) {
        if (((type.ty & 0xFF) == ENUMTY.Tsarray) && ((type.nextOf().ty & 0xFF) == ENUMTY.Tsarray) && ((elem.type.value.ty & 0xFF) != ENUMTY.Tsarray))
        {
            TypeSArray tsa = ((TypeSArray)type.nextOf());
            int len = (int)tsa.dim.toInteger();
            Ref<UnionExp> ue = ref(null);
            elem = createBlockDuplicatedArrayLiteral(ptr(ue), loc, type.nextOf(), elem, len);
            if ((pequals(elem, ue.value.exp())))
            {
                elem = ue.value.copy();
            }
        }
        Type tb = elem.type.value.toBasetype();
        boolean mustCopy = ((tb.ty & 0xFF) == ENUMTY.Tstruct) || ((tb.ty & 0xFF) == ENUMTY.Tsarray);
        DArray<Expression> elements = new DArray<Expression>(dim);
        {
            Slice<Expression> __r873 = (elements).opSlice().copy();
            int __key872 = 0;
            for (; (__key872 < __r873.getLength());__key872 += 1) {
                Expression el = __r873.get(__key872);
                int i = __key872;
                el = mustCopy && (i != 0) ? copyLiteral(elem).copy() : elem;
            }
        }
        (pue) = new UnionExp(new ArrayLiteralExp(loc, type, elements));
        ArrayLiteralExp ale = ((ArrayLiteralExp)(pue.get()).exp());
        ale.ownedByCtfe = OwnedBy.ctfe;
        return ale;
    }

    // Erasure: createBlockDuplicatedStringLiteral<Ptr, Loc, Type, int, int, byte>
    public static StringExp createBlockDuplicatedStringLiteral(Ptr<UnionExp> pue, Loc loc, Type type, int value, int dim, byte sz) {
        BytePtr s = pcopy(ptr(new byte[(sz & 0xFF)]));
        {
            int __key874 = 0;
            int __limit875 = dim;
            for (; (__key874 < __limit875);__key874 += 1) {
                int elemi = __key874;
                switch ((sz & 0xFF))
                {
                    case 1:
                        s.set(elemi, (byte)value);
                        break;
                    case 2:
                        (toCharPtr(s)).set(elemi, (char)value);
                        break;
                    case 4:
                        (toPtr<Integer>(s)).set(elemi, value);
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
        }
        (pue) = new UnionExp(new StringExp(loc, s, dim));
        StringExp se = ((StringExp)(pue.get()).exp());
        se.type.value = type;
        se.sz = sz;
        se.committed = (byte)1;
        se.ownedByCtfe = OwnedBy.ctfe;
        return se;
    }

    // Erasure: isAssocArray<Type>
    public static boolean isAssocArray(Type t) {
        t = t.toBasetype();
        if (((t.ty & 0xFF) == ENUMTY.Taarray))
        {
            return true;
        }
        return false;
    }

    // Erasure: toBuiltinAAType<Type>
    public static TypeAArray toBuiltinAAType(Type t) {
        t = t.toBasetype();
        if (((t.ty & 0xFF) == ENUMTY.Taarray))
        {
            return ((TypeAArray)t);
        }
        throw new AssertionError("Unreachable code!");
    }

    // Erasure: isTypeInfo_Class<Type>
    public static boolean isTypeInfo_Class(Type type) {
        return ((type.ty & 0xFF) == ENUMTY.Tclass) && (pequals(Type.dtypeinfo, (((TypeClass)type)).sym)) || Type.dtypeinfo.isBaseOf((((TypeClass)type)).sym, null);
    }

    // Erasure: isPointer<Type>
    public static boolean isPointer(Type t) {
        Type tb = t.toBasetype();
        return ((tb.ty & 0xFF) == ENUMTY.Tpointer) && ((tb.nextOf().ty & 0xFF) != ENUMTY.Tfunction);
    }

    // Erasure: isTrueBool<Expression>
    public static boolean isTrueBool(Expression e) {
        return e.isBool(true) || ((e.type.value.ty & 0xFF) == ENUMTY.Tpointer) || ((e.type.value.ty & 0xFF) == ENUMTY.Tclass) && ((e.op & 0xFF) != 13);
    }

    // Erasure: isSafePointerCast<Type, Type>
    public static boolean isSafePointerCast(Type srcPointee, Type destPointee) {
        for (; ((srcPointee.ty & 0xFF) == ENUMTY.Tpointer) && ((destPointee.ty & 0xFF) == ENUMTY.Tpointer);){
            srcPointee = srcPointee.nextOf();
            destPointee = destPointee.nextOf();
        }
        if (srcPointee.constConv(destPointee) != 0)
        {
            return true;
        }
        if (((srcPointee.ty & 0xFF) == ENUMTY.Tfunction) && ((destPointee.ty & 0xFF) == ENUMTY.Tfunction))
        {
            return srcPointee.covariant(destPointee, null, true) == 1;
        }
        if (((destPointee.ty & 0xFF) == ENUMTY.Tvoid))
        {
            return true;
        }
        if (((srcPointee.ty & 0xFF) == ENUMTY.Taarray) && (pequals(destPointee, Type.tvoidptr)))
        {
            return true;
        }
        if (((srcPointee.ty & 0xFF) == ENUMTY.Tsarray) && ((destPointee.ty & 0xFF) == ENUMTY.Tsarray))
        {
            if ((srcPointee.size() != destPointee.size()))
            {
                return false;
            }
            srcPointee = srcPointee.baseElemOf();
            destPointee = destPointee.baseElemOf();
        }
        return srcPointee.isintegral() && destPointee.isintegral() && (srcPointee.size() == destPointee.size());
    }

    // Erasure: getAggregateFromPointer<Expression, Ptr>
    public static Expression getAggregateFromPointer(Expression e, Ptr<Long> ofs) {
        ofs.set(0, 0L);
        if (((e.op & 0xFF) == 19))
        {
            e = (((AddrExp)e)).e1.value;
        }
        if (((e.op & 0xFF) == 25))
        {
            ofs.set(0, (((SymOffExp)e)).offset);
        }
        if (((e.op & 0xFF) == 27))
        {
            Expression ex = (((DotVarExp)e)).e1.value;
            VarDeclaration v = (((DotVarExp)e)).var.isVarDeclaration();
            assert(v != null);
            StructLiteralExp se = ((ex.op & 0xFF) == 50) ? (((ClassReferenceExp)ex)).value : ((StructLiteralExp)ex);
            int i = ((ex.op & 0xFF) == 50) ? (((ClassReferenceExp)ex)).getFieldIndex(e.type.value, v.offset) : se.getFieldIndex(e.type.value, v.offset);
            e = (se.elements).get(i);
        }
        if (((e.op & 0xFF) == 62))
        {
            IndexExp ie = ((IndexExp)e);
            if (((ie.e1.value.type.value.ty & 0xFF) == ENUMTY.Tarray) || ((ie.e1.value.type.value.ty & 0xFF) == ENUMTY.Tsarray) || ((ie.e1.value.op & 0xFF) == 121) || ((ie.e1.value.op & 0xFF) == 47) && ((ie.e2.value.op & 0xFF) == 135))
            {
                ofs.set(0, ie.e2.value.toInteger());
                return ie.e1.value;
            }
        }
        if (((e.op & 0xFF) == 31) && ((e.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
        {
            SliceExp se = ((SliceExp)e);
            if (((se.e1.value.type.value.ty & 0xFF) == ENUMTY.Tarray) || ((se.e1.value.type.value.ty & 0xFF) == ENUMTY.Tsarray) || ((se.e1.value.op & 0xFF) == 121) || ((se.e1.value.op & 0xFF) == 47) && ((se.lwr.value.op & 0xFF) == 135))
            {
                ofs.set(0, se.lwr.value.toInteger());
                return se.e1.value;
            }
        }
        return e;
    }

    // Erasure: pointToSameMemoryBlock<Expression, Expression>
    public static boolean pointToSameMemoryBlock(Expression agg1, Expression agg2) {
        if ((pequals(agg1, agg2)))
        {
            return true;
        }
        if (((agg1.op & 0xFF) == 135) && ((agg2.op & 0xFF) == 135) && (agg1.toInteger() == agg2.toInteger()))
        {
            return true;
        }
        if (((agg1.op & 0xFF) == 26) && ((agg2.op & 0xFF) == 26) && (pequals((((VarExp)agg1)).var, (((VarExp)agg2)).var)))
        {
            return true;
        }
        if (((agg1.op & 0xFF) == 25) && ((agg2.op & 0xFF) == 25) && (pequals((((SymOffExp)agg1)).var, (((SymOffExp)agg2)).var)))
        {
            return true;
        }
        return false;
    }

    // Erasure: pointerDifference<Loc, Type, Expression, Expression>
    public static UnionExp pointerDifference(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        Ref<Long> ofs1 = ref(0L);
        Ref<Long> ofs2 = ref(0L);
        Expression agg1 = getAggregateFromPointer(e1, ptr(ofs1));
        Expression agg2 = getAggregateFromPointer(e2, ptr(ofs2));
        if ((pequals(agg1, agg2)))
        {
            Type pointee = (((TypePointer)agg1.type.value)).next.value;
            long sz = pointee.size();
            ptr(ue) = new UnionExp(new IntegerExp(loc, (ofs1.value - ofs2.value) * sz, type));
        }
        else if (((agg1.op & 0xFF) == 121) && ((agg2.op & 0xFF) == 121) && ((((StringExp)agg1)).string == (((StringExp)agg2)).string))
        {
            Type pointee = (((TypePointer)agg1.type.value)).next.value;
            long sz = pointee.size();
            ptr(ue) = new UnionExp(new IntegerExp(loc, (ofs1.value - ofs2.value) * sz, type));
        }
        else if (((agg1.op & 0xFF) == 25) && ((agg2.op & 0xFF) == 25) && (pequals((((SymOffExp)agg1)).var, (((SymOffExp)agg2)).var)))
        {
            ptr(ue) = new UnionExp(new IntegerExp(loc, ofs1.value - ofs2.value, type));
        }
        else
        {
            error(loc, new BytePtr("`%s - %s` cannot be interpreted at compile time: cannot subtract pointers to two different memory blocks"), e1.toChars(), e2.toChars());
            ptr(ue) = new UnionExp(new CTFEExp(TOK.cantExpression));
        }
        return ue.value;
    }

    // Erasure: pointerArithmetic<Loc, byte, Type, Expression, Expression>
    public static UnionExp pointerArithmetic(Loc loc, byte op, Type type, Expression eptr, Expression e2) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (((eptr.type.value.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
        {
            error(loc, new BytePtr("cannot perform arithmetic on `void*` pointers at compile time"));
        /*Lcant:*/
            ptr(ue) = new UnionExp(new CTFEExp(TOK.cantExpression));
            return ue.value;
        }
        if (((eptr.op & 0xFF) == 19))
        {
            eptr = (((AddrExp)eptr)).e1.value;
        }
        Ref<Long> ofs1 = ref(0L);
        Expression agg1 = getAggregateFromPointer(eptr, ptr(ofs1));
        if (((agg1.op & 0xFF) == 25))
        {
            if ((((((SymOffExp)agg1)).var.type.ty & 0xFF) != ENUMTY.Tsarray))
            {
                error(loc, new BytePtr("cannot perform pointer arithmetic on arrays of unknown length at compile time"));
                /*goto Lcant*/throw Dispatch0.INSTANCE;
            }
        }
        else if (((agg1.op & 0xFF) != 121) && ((agg1.op & 0xFF) != 47))
        {
            error(loc, new BytePtr("cannot perform pointer arithmetic on non-arrays at compile time"));
            /*goto Lcant*/throw Dispatch0.INSTANCE;
        }
        long ofs2 = e2.toInteger();
        Type pointee = (((TypeNext)agg1.type.value.toBasetype())).next.value;
        long sz = pointee.size();
        long indx = 0L;
        long len = 0L;
        if (((agg1.op & 0xFF) == 25))
        {
            indx = (long)(ofs1.value / sz);
            len = (((TypeSArray)(((SymOffExp)agg1)).var.type)).dim.toInteger();
        }
        else
        {
            Expression dollar = ArrayLength(Type.tsize_t, agg1).copy();
            assert(!CTFEExp.isCantExp(dollar));
            indx = (long)ofs1.value;
            len = dollar.toInteger();
        }
        if (((op & 0xFF) == 74) || ((op & 0xFF) == 76) || ((op & 0xFF) == 93))
        {
            (long)indx += ofs2 / sz;
        }
        else if (((op & 0xFF) == 75) || ((op & 0xFF) == 77) || ((op & 0xFF) == 94))
        {
            (long)indx -= ofs2 / sz;
        }
        else
        {
            error(loc, new BytePtr("CTFE internal error: bad pointer operation"));
            /*goto Lcant*/throw Dispatch0.INSTANCE;
        }
        if ((indx < 0L) || (len < (long)indx))
        {
            error(loc, new BytePtr("cannot assign pointer to index %lld inside memory block `[0..%lld]`"), indx, len);
            /*goto Lcant*/throw Dispatch0.INSTANCE;
        }
        if (((agg1.op & 0xFF) == 25))
        {
            ptr(ue) = new UnionExp(new SymOffExp(loc, (((SymOffExp)agg1)).var, (long)indx * sz));
            SymOffExp se = ((SymOffExp)ue.value.exp());
            se.type.value = type;
            return ue.value;
        }
        if (((agg1.op & 0xFF) != 47) && ((agg1.op & 0xFF) != 121))
        {
            error(loc, new BytePtr("CTFE internal error: pointer arithmetic `%s`"), agg1.toChars());
            /*goto Lcant*/throw Dispatch0.INSTANCE;
        }
        if (((eptr.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
        {
            long dim = (((TypeSArray)eptr.type.value.toBasetype())).dim.toInteger();
            SliceExp se = new SliceExp(loc, agg1, new IntegerExp(loc, (long)indx, Type.tsize_t), new IntegerExp(loc, (long)indx + dim, Type.tsize_t));
            se.type.value = type.toBasetype().nextOf();
            ptr(ue) = new UnionExp(new AddrExp(loc, se));
            ue.value.exp().type.value = type;
            return ue.value;
        }
        IntegerExp ofs = new IntegerExp(loc, (long)indx, Type.tsize_t);
        Expression ie = new IndexExp(loc, agg1, ofs);
        ie.type.value = type.toBasetype().nextOf();
        ptr(ue) = new UnionExp(new AddrExp(loc, ie));
        ue.value.exp().type.value = type;
        return ue.value;
    }

    // Erasure: comparePointers<byte, Expression, long, Expression, long>
    public static int comparePointers(byte op, Expression agg1, long ofs1, Expression agg2, long ofs2) {
        if (pointToSameMemoryBlock(agg1, agg2))
        {
            int n = 0;
            switch ((op & 0xFF))
            {
                case 54:
                    n = ((ofs1 < ofs2) ? 1 : 0);
                    break;
                case 56:
                    n = ((ofs1 <= ofs2) ? 1 : 0);
                    break;
                case 55:
                    n = ((ofs1 > ofs2) ? 1 : 0);
                    break;
                case 57:
                    n = ((ofs1 >= ofs2) ? 1 : 0);
                    break;
                case 60:
                case 58:
                    n = ((ofs1 == ofs2) ? 1 : 0);
                    break;
                case 61:
                case 59:
                    n = ((ofs1 != ofs2) ? 1 : 0);
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return n;
        }
        boolean null1 = (agg1.op & 0xFF) == 13;
        boolean null2 = (agg2.op & 0xFF) == 13;
        int cmp = 0;
        if (null1 || null2)
        {
            switch ((op & 0xFF))
            {
                case 54:
                    cmp = ((null1 && !null2) ? 1 : 0);
                    break;
                case 55:
                    cmp = ((!null1 && null2) ? 1 : 0);
                    break;
                case 56:
                    cmp = (null1 ? 1 : 0);
                    break;
                case 57:
                    cmp = (null2 ? 1 : 0);
                    break;
                case 60:
                case 58:
                case 61:
                case 59:
                    cmp = (((null1 ? 1 : 0) == (null2 ? 1 : 0)) ? 1 : 0);
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
        }
        else
        {
            switch ((op & 0xFF))
            {
                case 60:
                case 58:
                case 61:
                case 59:
                    cmp = 0;
                    break;
                default:
                return -1;
            }
        }
        if (((op & 0xFF) == 61) || ((op & 0xFF) == 59))
        {
            cmp ^= 1;
        }
        return cmp;
    }

    // Erasure: isFloatIntPaint<Type, Type>
    public static boolean isFloatIntPaint(Type to, Type from) {
        return (from.size() == to.size()) && from.isintegral() && to.isfloating() || from.isfloating() && to.isintegral();
    }

    // Erasure: paintFloatInt<Ptr, Expression, Type>
    public static Expression paintFloatInt(Ptr<UnionExp> pue, Expression fromVal, Type to) {
        if (exceptionOrCantInterpret(fromVal))
        {
            return fromVal;
        }
        assert((to.size() == 4L) || (to.size() == 8L));
        return Compiler.paintAsType(pue, fromVal, to);
    }

    // Erasure: isCtfeComparable<Expression>
    public static boolean isCtfeComparable(Expression e) {
        if (((e.op & 0xFF) == 31))
        {
            e = (((SliceExp)e)).e1.value;
        }
        if ((e.isConst() != 1))
        {
            if (((e.op & 0xFF) == 13) || ((e.op & 0xFF) == 121) || ((e.op & 0xFF) == 161) || ((e.op & 0xFF) == 160) || ((e.op & 0xFF) == 47) || ((e.op & 0xFF) == 49) || ((e.op & 0xFF) == 48) || ((e.op & 0xFF) == 50))
            {
                return true;
            }
            if (((e.op & 0xFF) == 42))
            {
                return true;
            }
            return false;
        }
        return true;
    }

    // from template numCmp!(Double)
    // Erasure: numCmpDouble<byte, double, double>
    public static boolean numCmpDouble(byte op, double n1, double n2) {
        switch ((op & 0xFF))
        {
            case 54:
                return n1 < n2;
            case 56:
                return n1 <= n2;
            case 55:
                return n1 > n2;
            case 57:
                return n1 >= n2;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }


    // from template numCmp!(Integer)
    // Erasure: numCmpInteger<byte, int, int>
    public static boolean numCmpInteger(byte op, int n1, int n2) {
        switch ((op & 0xFF))
        {
            case 54:
                return n1 < n2;
            case 56:
                return n1 <= n2;
            case 55:
                return n1 > n2;
            case 57:
                return n1 >= n2;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }


    // from template numCmp!(Long)
    // Erasure: numCmpLong<byte, long, long>
    public static boolean numCmpLong(byte op, long n1, long n2) {
        switch ((op & 0xFF))
        {
            case 54:
                return n1 < n2;
            case 56:
                return n1 <= n2;
            case 55:
                return n1 > n2;
            case 57:
                return n1 >= n2;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }


    // from template numCmp!(Long)
    // removed duplicate function, [["Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "TypeAArray toBuiltinAATypeType", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "DtorDeclaration buildExternDDtorAggregateDeclaration, Ptr<Scope>", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "StringExp createBlockDuplicatedStringLiteralPtr<UnionExp>, Loc, Type, int, int, byte", "void createMatchNodes", "UnionExp ArrayLengthType, Expression", "void buildArrayOpPtr<Scope>, Expression, DArray<RootObject>, DArray<Expression>", "boolean isFloatIntPaintType, Type", "int mainSlice<ByteSlice>", "void vmessageLoc, BytePtr, Slice<Object>", "void printDepsConditionalPtr<Scope>, DVCondition, ByteSlice", "Expression paintFloatIntPtr<UnionExp>, Expression, Type", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "boolean exceptionOrCantInterpretExpression", "void deprecationLoc, BytePtr", "void messageLoc, BytePtr", "boolean checkSymbolAccessPtr<Scope>, Dsymbol", "FuncDeclaration buildOpAssignStructDeclaration, Ptr<Scope>", "UnionExp ModLoc, Type, Expression, Expression", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "boolean checkAccessLoc, Ptr<Scope>, Expression, Declaration", "void warningSupplementalLoc, BytePtr", "double creallcomplex_t", "boolean canThrowExpression, FuncDeclaration, boolean", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "UnionExp DivLoc, Type, Expression, Expression", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "long getStorageClassPtr<PrefixAttributesASTBase>ASTBase", "void cantExpUnionExp", "boolean pointToSameMemoryBlockExpression, Expression", "UnionExp ComType, Expression", "ByteSlice initializerMsgtable", "ErrorExp arrayOpInvalidErrorExpression", "boolean isDigitSecondbyte", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "void processFileByteSlice, ByteSlice, BytePtr_24239CC9FAA32FB7", "boolean isTrueBoolExpression", "void builtin_init", "void errorSupplementalLoc, BytePtr", "long resolveArrayLengthExpression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "void parseModulePatternBytePtr, Ptr<MatcherNode>, int", "boolean checkAccessLoc, Ptr<Scope>, dmodule.Package", "int isCppOperatorIdentifier", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "void errorLoc, BytePtr", "boolean utf_isValidDcharint", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void fatal", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "int utf_codeLengthWcharint", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "boolean isBinArrayOpbyte", "BytePtr linkToCharsint", "int isConstExpression", "UnionExp NegType, Expression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "TypeTuple toArgTypesType", "boolean isUniAlphaint", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "boolean isNamespaceEqualNspace, Nspace", "void errorBytePtr, int, int, BytePtr", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "UnionExp PtrType, Expression", "void utf_encodeWcharCharPtr, int", "boolean needOpEqualsStructDeclaration", "boolean findConditionDArray<Identifier>, Identifier", "UnionExp ShlLoc, Type, Expression, Expression", "UnionExp ShrLoc, Type, Expression, Expression", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression getAggregateFromPointerExpression, Ptr<Long>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralPtr<UnionExp>, Loc, Type, Expression, int", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp CastLoc, Type, Type, Expression", "UnionExp IndexType, Expression, Expression", "DArray<Expression> copyElementsExpression, Expression", "boolean symbolIsVisibledmodule.Module, Dsymbol", "void utf_encodeint, Object, int", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "FuncDeclaration buildXopCmpStructDeclaration, Ptr<Scope>", "void vwarningLoc, BytePtr, Slice<Object>", "boolean needToCopyLiteralExpression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "Expression arrayOpBinAssignExp, Ptr<Scope>", "boolean isCtfeComparableExpression", "long getStorageClassPtr<PrefixAttributesASTCodegen>ASTCodegen", "ByteSlice lexBytePtr, ByteSlice", "boolean isPointerType", "boolean checkNonAssignmentArrayOpExpression, boolean", "int utf_codeLengthCharint", "long mergeFuncAttrslong, FuncDeclaration", "BytePtr cppTypeInfoMangleItaniumDsymbol", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "void deprecationSupplementalLoc, BytePtr", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "FuncDeclaration buildOpEqualsStructDeclaration, Ptr<Scope>", "boolean isBinAssignArrayOpbyte", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Ptr<Scope>", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Ptr<Scope>", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean isNamespaceEqualNspace, CPPNamespaceDeclaration", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "BytePtr cppTypeInfoMangleMSVCDsymbol", "boolean symbolIsVisibleDsymbol, Dsymbol", "boolean c_isalnumint", "boolean checkImmutableSharedType", "BytePtr toCppMangleItaniumDsymbol", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "Expression resolveSliceExpression, Ptr<UnionExp>", "boolean isArrayOpValidExpression", "void warningLoc, BytePtr", "UnionExp BoolType, Expression", "void colorSyntaxHighlightOutBuffer", "Expression paintTypeOntoLiteralType, Expression", "BytePtr toCppMangleMSVCDsymbol", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean needToHashStructDeclaration", "void visitObjectRootObject, ComponentVisitorComponentVisitor", "boolean isNamespaceEqualCPPNamespaceDeclaration, Nspace, int", "boolean isDMDx64Target", "UnionExp AddLoc, Type, Expression, Expression", "UnionExp XorLoc, Type, Expression, Expression", "UnionExp MinLoc, Type, Expression, Expression", "boolean checkAccessAggregateDeclaration, Loc, Ptr<Scope>, Dsymbol", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "void visitObjectRootObject, CppMangleVisitorCppMangleVisitor", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "Expression resolveAliasThisPtr<Scope>, Expression, boolean", "UnionExp AndLoc, Type, Expression, Expression", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "BytePtr utf_decodeWcharCharPtr, int, Ref<Integer>, Ref<Integer>", "boolean hasProtectedAccessPtr<Scope>, Dsymbol", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "boolean isPrimaryDtorDsymbol", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "Expression arrayOpBinExp, Ptr<Scope>", "int isBuiltinFuncDeclaration", "void sliceAssignStringFromStringStringExp, StringExp, int", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "Type asTypeRootObject", "void messageBytePtr", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "UnionExp NotType, Expression", "DtorDeclaration buildDtorAggregateDeclaration, Ptr<Scope>", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "ByteSlice lispyBytePtr, ByteSlice", "boolean walkPostorderExpression, StoppableVisitor", "Expression paintTypeOntoLiteralPtr<UnionExp>, Type, Expression", "Expression expTypeType, Expression", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Ptr<Scope>", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "boolean c_isxdigitint", "ByteSlice deinitializerMsgtable", "void processFileByteSlice, ByteSlice, BytePtr_D1F3732A9A6A6D5A", "void utf_encodeCharBytePtr, int", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "UnionExp copyLiteralExpression", "UnionExp paintTypeOntoLiteralCopyType, Expression", "FuncDeclaration asFuncDeclRootObject", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration buildXtoHashStructDeclaration, Ptr<Scope>", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "FuncDeclaration buildXopEqualsStructDeclaration, Ptr<Scope>", "boolean isAssocArrayType", "BytePtr utf_decodeCharBytePtr, int, Ref<Integer>, Ref<Integer>", "boolean isSafePointerCastType, Type", "boolean isNamespaceEqualCPPNamespaceDeclaration, CPPNamespaceDeclaration", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "void halt", "boolean symbolIsVisiblePtr<Scope>, Dsymbol", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, long, longLong", "void writeHighlightsPtr<Console>, OutBuffer", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "FuncDeclaration buildInvAggregateDeclaration, Ptr<Scope>", "boolean hasPackageAccessPtr<Scope>, Dsymbol", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "boolean writeMixinByteSlice, Loc"]] signature: boolean numCmpbyte, long, longLong

    // Erasure: specificCmp<byte, int>
    public static int specificCmp(byte op, int rawCmp) {
        return (numCmpInteger(op, rawCmp, 0) ? 1 : 0);
    }

    // Erasure: intUnsignedCmp<byte, long, long>
    public static int intUnsignedCmp(byte op, long n1, long n2) {
        return (numCmpLong(op, n1, n2) ? 1 : 0);
    }

    // Erasure: intSignedCmp<byte, long, long>
    public static int intSignedCmp(byte op, long n1, long n2) {
        return (numCmpLong(op, n1, n2) ? 1 : 0);
    }

    // Erasure: realCmp<byte, double, double>
    public static int realCmp(byte op, double r1, double r2) {
        if (CTFloat.isNaN(r1) || CTFloat.isNaN(r2))
        {
            switch ((op & 0xFF))
            {
                case 54:
                case 56:
                case 55:
                case 57:
                    return 0;
                default:
                throw new AssertionError("Unreachable code!");
            }
        }
        else
        {
            return (numCmpDouble(op, r1, r2) ? 1 : 0);
        }
    }

    // Erasure: ctfeCmpArrays<Loc, Expression, Expression, long>
    public static int ctfeCmpArrays(Loc loc, Expression e1, Expression e2, long len) {
        long lo1 = 0L;
        long lo2 = 0L;
        Expression x = e1;
        if (((x.op & 0xFF) == 31))
        {
            lo1 = (((SliceExp)x)).lwr.value.toInteger();
            x = (((SliceExp)x)).e1.value;
        }
        StringExp se1 = ((x.op & 0xFF) == 121) ? ((StringExp)x) : null;
        ArrayLiteralExp ae1 = ((x.op & 0xFF) == 47) ? ((ArrayLiteralExp)x) : null;
        x = e2;
        if (((x.op & 0xFF) == 31))
        {
            lo2 = (((SliceExp)x)).lwr.value.toInteger();
            x = (((SliceExp)x)).e1.value;
        }
        StringExp se2 = ((x.op & 0xFF) == 121) ? ((StringExp)x) : null;
        ArrayLiteralExp ae2 = ((x.op & 0xFF) == 47) ? ((ArrayLiteralExp)x) : null;
        if ((se1 != null) && (se2 != null))
        {
            return sliceCmpStringWithString(se1, se2, (int)lo1, (int)lo2, (int)len);
        }
        if ((se1 != null) && (ae2 != null))
        {
            return sliceCmpStringWithArray(se1, ae2, (int)lo1, (int)lo2, (int)len);
        }
        if ((se2 != null) && (ae1 != null))
        {
            return -sliceCmpStringWithArray(se2, ae1, (int)lo2, (int)lo1, (int)len);
        }
        assert((ae1 != null) && (ae2 != null));
        boolean needCmp = ae1.type.value.nextOf().isintegral();
        {
            int __key876 = 0;
            int __limit877 = (int)len;
            for (; (__key876 < __limit877);__key876 += 1) {
                int i = __key876;
                Expression ee1 = (ae1.elements).get((int)(lo1 + (long)i));
                Expression ee2 = (ae2.elements).get((int)(lo2 + (long)i));
                if (needCmp)
                {
                    long c = (long)(ee1.toInteger() - ee2.toInteger());
                    if ((c > 0L))
                    {
                        return 1;
                    }
                    if ((c < 0L))
                    {
                        return -1;
                    }
                }
                else
                {
                    if (ctfeRawCmp(loc, ee1, ee2, false) != 0)
                    {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    // Erasure: funcptrOf<Expression>
    public static FuncDeclaration funcptrOf(Expression e) {
        assert(((e.type.value.ty & 0xFF) == ENUMTY.Tdelegate));
        if (((e.op & 0xFF) == 160))
        {
            return (((DelegateExp)e)).func;
        }
        if (((e.op & 0xFF) == 161))
        {
            return (((FuncExp)e)).fd;
        }
        assert(((e.op & 0xFF) == 13));
        return null;
    }

    // Erasure: isArray<Expression>
    public static boolean isArray(Expression e) {
        return ((e.op & 0xFF) == 47) || ((e.op & 0xFF) == 121) || ((e.op & 0xFF) == 31) || ((e.op & 0xFF) == 13);
    }

    // Erasure: ctfeRawCmp<Loc, Expression, Expression, boolean>
    public static int ctfeRawCmp(Loc loc, Expression e1, Expression e2, boolean identity) {
        if (((e1.op & 0xFF) == 50) || ((e2.op & 0xFF) == 50))
        {
            if (((e1.op & 0xFF) == 50) && ((e2.op & 0xFF) == 50) && (pequals((((ClassReferenceExp)e1)).value, (((ClassReferenceExp)e2)).value)))
            {
                return 0;
            }
            return 1;
        }
        if (((e1.op & 0xFF) == 42) && ((e2.op & 0xFF) == 42))
        {
            Type t1 = isType((((TypeidExp)e1)).obj);
            Type t2 = isType((((TypeidExp)e2)).obj);
            assert(t1 != null);
            assert(t2 != null);
            return ((!pequals(t1, t2)) ? 1 : 0);
        }
        if (((e1.op & 0xFF) == 13) && ((e2.op & 0xFF) == 13))
        {
            return 0;
        }
        if (((e1.type.value.ty & 0xFF) == ENUMTY.Tpointer) && ((e2.type.value.ty & 0xFF) == ENUMTY.Tpointer))
        {
            Ref<Long> ofs1 = ref(0L);
            Ref<Long> ofs2 = ref(0L);
            Expression agg1 = getAggregateFromPointer(e1, ptr(ofs1));
            Expression agg2 = getAggregateFromPointer(e2, ptr(ofs2));
            if ((pequals(agg1, agg2)) || ((agg1.op & 0xFF) == 26) && ((agg2.op & 0xFF) == 26) && (pequals((((VarExp)agg1)).var, (((VarExp)agg2)).var)))
            {
                if ((ofs1.value == ofs2.value))
                {
                    return 0;
                }
            }
            return 1;
        }
        if (((e1.type.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((e2.type.value.ty & 0xFF) == ENUMTY.Tdelegate))
        {
            if ((!pequals(funcptrOf(e1), funcptrOf(e2))))
            {
                return 1;
            }
            if (((e1.op & 0xFF) == 161) && ((e2.op & 0xFF) == 161))
            {
                return 0;
            }
            assert(((e1.op & 0xFF) == 160) && ((e2.op & 0xFF) == 160));
            Expression ptr1 = (((DelegateExp)e1)).e1.value;
            Expression ptr2 = (((DelegateExp)e2)).e1.value;
            Ref<Long> ofs1 = ref(0L);
            Ref<Long> ofs2 = ref(0L);
            Expression agg1 = getAggregateFromPointer(ptr1, ptr(ofs1));
            Expression agg2 = getAggregateFromPointer(ptr2, ptr(ofs2));
            if ((pequals(agg1, agg2)) && (ofs1.value == ofs2.value) || ((agg1.op & 0xFF) == 26) && ((agg2.op & 0xFF) == 26) && (pequals((((VarExp)agg1)).var, (((VarExp)agg2)).var)))
            {
                return 0;
            }
            return 1;
        }
        if (isArray(e1) && isArray(e2))
        {
            long len1 = resolveArrayLength(e1);
            long len2 = resolveArrayLength(e2);
            if ((len1 > 0L) && (len2 > 0L))
            {
                long len = (len1 < len2) ? len1 : len2;
                int res = ctfeCmpArrays(loc, e1, e2, len);
                if ((res != 0))
                {
                    return res;
                }
            }
            return (int)(len1 - len2);
        }
        if (e1.type.value.isintegral())
        {
            return ((e1.toInteger() != e2.toInteger()) ? 1 : 0);
        }
        if (e1.type.value.isreal() || e1.type.value.isimaginary())
        {
            double r1 = e1.type.value.isreal() ? e1.toReal() : e1.toImaginary();
            double r2 = e1.type.value.isreal() ? e2.toReal() : e2.toImaginary();
            if (identity)
            {
                return (RealIdentical(r1, r2) == 0 ? 1 : 0);
            }
            if (CTFloat.isNaN(r1) || CTFloat.isNaN(r2))
            {
                return 1;
            }
            else
            {
                return ((r1 != r2) ? 1 : 0);
            }
        }
        else if (e1.type.value.iscomplex())
        {
            complex_t c1 = e1.toComplex().copy();
            complex_t c2 = e2.toComplex().copy();
            if (identity)
            {
                return (((RealIdentical(c1.re, c2.re) == 0) && (RealIdentical(c1.im, c2.im) == 0)) ? 1 : 0);
            }
            return (c1.opEquals(c2) == 0 ? 1 : 0);
        }
        if (((e1.op & 0xFF) == 49) && ((e2.op & 0xFF) == 49))
        {
            StructLiteralExp es1 = ((StructLiteralExp)e1);
            StructLiteralExp es2 = ((StructLiteralExp)e2);
            if ((!pequals(es1.sd, es2.sd)))
            {
                return 1;
            }
            else if ((es1.elements == null) || ((es1.elements).length == 0) && (es2.elements == null) || ((es2.elements).length == 0))
            {
                return 0;
            }
            else if ((es1.elements == null) || (es2.elements == null))
            {
                return 1;
            }
            else if (((es1.elements).length != (es2.elements).length))
            {
                return 1;
            }
            else
            {
                {
                    int __key878 = 0;
                    int __limit879 = (es1.elements).length;
                    for (; (__key878 < __limit879);__key878 += 1) {
                        int i = __key878;
                        Expression ee1 = (es1.elements).get(i);
                        Expression ee2 = (es2.elements).get(i);
                        if (((ee1.op & 0xFF) == 128) && ((ee2.op & 0xFF) == 128))
                        {
                            continue;
                        }
                        if ((pequals(ee1, ee2)))
                        {
                            continue;
                        }
                        if ((ee1 == null) || (ee2 == null))
                        {
                            return 1;
                        }
                        int cmp = ctfeRawCmp(loc, ee1, ee2, identity);
                        if (cmp != 0)
                        {
                            return 1;
                        }
                    }
                }
                return 0;
            }
        }
        if (((e1.op & 0xFF) == 48) && ((e2.op & 0xFF) == 48))
        {
            AssocArrayLiteralExp es1 = ((AssocArrayLiteralExp)e1);
            AssocArrayLiteralExp es2 = ((AssocArrayLiteralExp)e2);
            int dim = (es1.keys).length;
            if (((es2.keys).length != dim))
            {
                return 1;
            }
            Ptr<Boolean> used = pcopy(((Ptr<Boolean>)Mem.xmalloc(1 * dim)));
            memset(used, 0, 1 * dim);
            {
                int __key880 = 0;
                int __limit881 = dim;
                for (; (__key880 < __limit881);__key880 += 1) {
                    int i = __key880;
                    Expression k1 = (es1.keys).get(i);
                    Expression v1 = (es1.values).get(i);
                    Expression v2 = null;
                    {
                        int __key882 = 0;
                        int __limit883 = dim;
                        for (; (__key882 < __limit883);__key882 += 1) {
                            int j = __key882;
                            if (used.get(j))
                            {
                                continue;
                            }
                            Expression k2 = (es2.keys).get(j);
                            if (ctfeRawCmp(loc, k1, k2, identity) != 0)
                            {
                                continue;
                            }
                            used.set(j, true);
                            v2 = (es2.values).get(j);
                            break;
                        }
                    }
                    if ((v2 == null) || (ctfeRawCmp(loc, v1, v2, identity) != 0))
                    {
                        Mem.xfree(used);
                        return 1;
                    }
                }
            }
            Mem.xfree(used);
            return 0;
        }
        error(loc, new BytePtr("CTFE internal error: bad compare of `%s` and `%s`"), e1.toChars(), e2.toChars());
        throw new AssertionError("Unreachable code!");
    }

    // defaulted all parameters starting with #4
    public static int ctfeRawCmp(Loc loc, Expression e1, Expression e2) {
        return ctfeRawCmp(loc, e1, e2, false);
    }

    // Erasure: ctfeEqual<Loc, byte, Expression, Expression>
    public static int ctfeEqual(Loc loc, byte op, Expression e1, Expression e2) {
        return ((ctfeRawCmp(loc, e1, e2, false) == 0 ^ ((op & 0xFF) == 59)) ? 1 : 0);
    }

    // Erasure: ctfeIdentity<Loc, byte, Expression, Expression>
    public static int ctfeIdentity(Loc loc, byte op, Expression e1, Expression e2) {
        int cmp = 0;
        if (((e1.op & 0xFF) == 13))
        {
            cmp = (((e2.op & 0xFF) == 13) ? 1 : 0);
        }
        else if (((e2.op & 0xFF) == 13))
        {
            cmp = 0;
        }
        else if (((e1.op & 0xFF) == 25) && ((e2.op & 0xFF) == 25))
        {
            SymOffExp es1 = ((SymOffExp)e1);
            SymOffExp es2 = ((SymOffExp)e2);
            cmp = (((pequals(es1.var, es2.var)) && (es1.offset == es2.offset)) ? 1 : 0);
        }
        else if (e1.type.value.isreal())
        {
            cmp = RealIdentical(e1.toReal(), e2.toReal());
        }
        else if (e1.type.value.isimaginary())
        {
            cmp = RealIdentical(e1.toImaginary(), e2.toImaginary());
        }
        else if (e1.type.value.iscomplex())
        {
            complex_t v1 = e1.toComplex().copy();
            complex_t v2 = e2.toComplex().copy();
            cmp = (((RealIdentical(creall(v1), creall(v2)) != 0) && (RealIdentical(cimagl(v1), cimagl(v1)) != 0)) ? 1 : 0);
        }
        else
        {
            cmp = (ctfeRawCmp(loc, e1, e2, true) == 0 ? 1 : 0);
        }
        if (((op & 0xFF) == 61) || ((op & 0xFF) == 59))
        {
            cmp ^= 1;
        }
        return cmp;
    }

    // Erasure: ctfeCmp<Loc, byte, Expression, Expression>
    public static int ctfeCmp(Loc loc, byte op, Expression e1, Expression e2) {
        Type t1 = e1.type.value.toBasetype();
        Type t2 = e2.type.value.toBasetype();
        if (t1.isString() && t2.isString())
        {
            return specificCmp(op, ctfeRawCmp(loc, e1, e2, false));
        }
        else if (t1.isreal())
        {
            return realCmp(op, e1.toReal(), e2.toReal());
        }
        else if (t1.isimaginary())
        {
            return realCmp(op, e1.toImaginary(), e2.toImaginary());
        }
        else if (t1.isunsigned() || t2.isunsigned())
        {
            return intUnsignedCmp(op, e1.toInteger(), e2.toInteger());
        }
        else
        {
            return intSignedCmp(op, (long)e1.toInteger(), (long)e2.toInteger());
        }
    }

    // Erasure: ctfeCat<Loc, Type, Expression, Expression>
    public static UnionExp ctfeCat(Loc loc, Type type, Expression e1, Expression e2) {
        Type t1 = e1.type.value.toBasetype();
        Type t2 = e2.type.value.toBasetype();
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (((e2.op & 0xFF) == 121) && ((e1.op & 0xFF) == 47) && t1.nextOf().isintegral())
        {
            StringExp es1 = ((StringExp)e2);
            ArrayLiteralExp es2 = ((ArrayLiteralExp)e1);
            int len = es1.len + (es2.elements).length;
            byte sz = es1.sz;
            Object s = pcopy(Mem.xmalloc((len + 1) * (sz & 0xFF)));
            memcpy((BytePtr)((((BytePtr)s).plus(((sz & 0xFF) * (es2.elements).length)))), (es1.string), (es1.len * (sz & 0xFF)));
            {
                int __key884 = 0;
                int __limit885 = (es2.elements).length;
                for (; (__key884 < __limit885);__key884 += 1) {
                    int i = __key884;
                    Expression es2e = (es2.elements).get(i);
                    if (((es2e.op & 0xFF) != 135))
                    {
                        ptr(ue) = new UnionExp(new CTFEExp(TOK.cantExpression));
                        return ue.value;
                    }
                    long v = es2e.toInteger();
                    Port.valcpy((((BytePtr)s).plus((i * (sz & 0xFF)))), v, (sz & 0xFF));
                }
            }
            memset((((BytePtr)s).plus((len * (sz & 0xFF)))), 0, (sz & 0xFF));
            ptr(ue) = new UnionExp(new StringExp(loc, s, len));
            StringExp es = ((StringExp)ue.value.exp());
            es.sz = sz;
            es.committed = (byte)0;
            es.type.value = type;
            return ue.value;
        }
        if (((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 47) && t2.nextOf().isintegral())
        {
            StringExp es1 = ((StringExp)e1);
            ArrayLiteralExp es2 = ((ArrayLiteralExp)e2);
            int len = es1.len + (es2.elements).length;
            byte sz = es1.sz;
            Object s = pcopy(Mem.xmalloc((len + 1) * (sz & 0xFF)));
            memcpy((BytePtr)s, (es1.string), (es1.len * (sz & 0xFF)));
            {
                int __key886 = 0;
                int __limit887 = (es2.elements).length;
                for (; (__key886 < __limit887);__key886 += 1) {
                    int i = __key886;
                    Expression es2e = (es2.elements).get(i);
                    if (((es2e.op & 0xFF) != 135))
                    {
                        ptr(ue) = new UnionExp(new CTFEExp(TOK.cantExpression));
                        return ue.value;
                    }
                    long v = es2e.toInteger();
                    Port.valcpy((((BytePtr)s).plus(((es1.len + i) * (sz & 0xFF)))), v, (sz & 0xFF));
                }
            }
            memset((((BytePtr)s).plus((len * (sz & 0xFF)))), 0, (sz & 0xFF));
            ptr(ue) = new UnionExp(new StringExp(loc, s, len));
            StringExp es = ((StringExp)ue.value.exp());
            es.sz = sz;
            es.committed = (byte)0;
            es.type.value = type;
            return ue.value;
        }
        if (((e1.op & 0xFF) == 47) && ((e2.op & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            ArrayLiteralExp es1 = ((ArrayLiteralExp)e1);
            ArrayLiteralExp es2 = ((ArrayLiteralExp)e2);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(es1.loc, type, copyLiteralArray(es1.elements, null)));
            es1 = ((ArrayLiteralExp)ue.value.exp());
            (es1.elements).insert((es1.elements).length, copyLiteralArray(es2.elements, null));
            return ue.value;
        }
        if (((e1.op & 0xFF) == 47) && ((e2.op & 0xFF) == 13) && t1.nextOf().equals(t2.nextOf()))
        {
            ue.value.opAssign(paintTypeOntoLiteralCopy(type, copyLiteral(e1).copy()).copy());
            return ue.value;
        }
        if (((e1.op & 0xFF) == 13) && ((e2.op & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            ue.value.opAssign(paintTypeOntoLiteralCopy(type, copyLiteral(e2).copy()).copy());
            return ue.value;
        }
        ue.value.opAssign(Cat(type, e1, e2).copy());
        return ue.value;
    }

    // Erasure: findKeyInAA<Loc, AssocArrayLiteralExp, Expression>
    public static Expression findKeyInAA(Loc loc, AssocArrayLiteralExp ae, Expression e2) {
        {
            int i = (ae.keys).length;
            for (; i != 0;){
                i -= 1;
                Expression ekey = (ae.keys).get(i);
                int eq = ctfeEqual(loc, TOK.equal, ekey, e2);
                if (eq != 0)
                {
                    return (ae.values).get(i);
                }
            }
        }
        return null;
    }

    // Erasure: ctfeIndex<Loc, Type, Expression, long>
    public static Expression ctfeIndex(Loc loc, Type type, Expression e1, long indx) {
        assert(e1.type.value != null);
        if (((e1.op & 0xFF) == 121))
        {
            StringExp es1 = ((StringExp)e1);
            if ((indx >= (long)es1.len))
            {
                error(loc, new BytePtr("string index %llu is out of bounds `[0 .. %llu]`"), indx, (long)es1.len);
                return CTFEExp.cantexp;
            }
            return new IntegerExp(loc, (long)es1.charAt(indx), type);
        }
        assert(((e1.op & 0xFF) == 47));
        {
            ArrayLiteralExp ale = ((ArrayLiteralExp)e1);
            if ((indx >= (long)(ale.elements).length))
            {
                error(loc, new BytePtr("array index %llu is out of bounds `%s[0 .. %llu]`"), indx, e1.toChars(), (long)(ale.elements).length);
                return CTFEExp.cantexp;
            }
            Expression e = (ale.elements).get((int)indx);
            return paintTypeOntoLiteral(type, e);
        }
    }

    // Erasure: ctfeCast<Ptr, Loc, Type, Type, Expression>
    public static Expression ctfeCast(Ptr<UnionExp> pue, Loc loc, Type type, Type to, Expression e) {
        Function0<Expression> paint = new Function0<Expression>() {
            public Expression invoke() {
             {
                return paintTypeOntoLiteral(pue, to, e);
            }}

        };
        if (((e.op & 0xFF) == 13))
        {
            return paint.invoke();
        }
        if (((e.op & 0xFF) == 50))
        {
            ClassDeclaration originalClass = (((ClassReferenceExp)e)).originalClass();
            if (originalClass.type.implicitConvTo(to.mutableOf()) != 0)
            {
                return paint.invoke();
            }
            else
            {
                (pue) = new UnionExp(new NullExp(loc, to));
                return (pue.get()).exp();
            }
        }
        if (isTypeInfo_Class(e.type.value) && (e.type.value.implicitConvTo(to) != 0))
        {
            return paint.invoke();
        }
        if (((e.op & 0xFF) == 49) && (pequals(e.type.value.toBasetype().castMod((byte)0), to.toBasetype().castMod((byte)0))))
        {
            return paint.invoke();
        }
        Expression r = null;
        if (e.type.value.equals(type) && type.equals(to))
        {
            r = e;
        }
        else if (((to.toBasetype().ty & 0xFF) == ENUMTY.Tarray) && ((type.toBasetype().ty & 0xFF) == ENUMTY.Tarray) && (to.toBasetype().nextOf().size() == type.toBasetype().nextOf().size()))
        {
            return paint.invoke();
        }
        else
        {
            pue.set(0, Cast(loc, type, to, e));
            r = (pue.get()).exp();
        }
        if (CTFEExp.isCantExp(r))
        {
            error(loc, new BytePtr("cannot cast `%s` to `%s` at compile time"), e.toChars(), to.toChars());
        }
        {
            ArrayLiteralExp ae = e.isArrayLiteralExp();
            if ((ae) != null)
            {
                ae.ownedByCtfe = OwnedBy.ctfe;
            }
        }
        {
            StringExp se = e.isStringExp();
            if ((se) != null)
            {
                se.ownedByCtfe = OwnedBy.ctfe;
            }
        }
        return r;
    }

    // Erasure: assignInPlace<Expression, Expression>
    public static void assignInPlace(Expression dest, Expression src) {
        assert(((dest.op & 0xFF) == 49) || ((dest.op & 0xFF) == 47) || ((dest.op & 0xFF) == 121));
        DArray<Expression> oldelems = null;
        DArray<Expression> newelems = null;
        if (((dest.op & 0xFF) == 49))
        {
            assert(((dest.op & 0xFF) == (src.op & 0xFF)));
            oldelems = pcopy((((StructLiteralExp)dest)).elements);
            newelems = pcopy((((StructLiteralExp)src)).elements);
            StructDeclaration sd = (((StructLiteralExp)dest)).sd;
            int nfields = sd.nonHiddenFields();
            int nvthis = sd.fields.length - nfields;
            if ((nvthis != 0) && ((oldelems).length >= nfields) && ((oldelems).length < (newelems).length))
            {
                int __key888 = 0;
                int __limit889 = (newelems).length - (oldelems).length;
                for (; (__key888 < __limit889);__key888 += 1) {
                    int __ = __key888;
                    (oldelems).push(null);
                }
            }
        }
        else if (((dest.op & 0xFF) == 47) && ((src.op & 0xFF) == 47))
        {
            oldelems = pcopy((((ArrayLiteralExp)dest)).elements);
            newelems = pcopy((((ArrayLiteralExp)src)).elements);
        }
        else if (((dest.op & 0xFF) == 121) && ((src.op & 0xFF) == 121))
        {
            sliceAssignStringFromString(((StringExp)dest), ((StringExp)src), 0);
            return ;
        }
        else if (((dest.op & 0xFF) == 47) && ((src.op & 0xFF) == 121))
        {
            sliceAssignArrayLiteralFromString(((ArrayLiteralExp)dest), ((StringExp)src), 0);
            return ;
        }
        else if (((src.op & 0xFF) == 47) && ((dest.op & 0xFF) == 121))
        {
            sliceAssignStringFromArrayLiteral(((StringExp)dest), ((ArrayLiteralExp)src), 0);
            return ;
        }
        else
        {
            throw new AssertionError("Unreachable code!");
        }
        assert(((oldelems).length == (newelems).length));
        {
            int __key890 = 0;
            int __limit891 = (oldelems).length;
            for (; (__key890 < __limit891);__key890 += 1) {
                int i = __key890;
                Expression e = (newelems).get(i);
                Expression o = (oldelems).get(i);
                if (((e.op & 0xFF) == 49))
                {
                    assert(((o.op & 0xFF) == (e.op & 0xFF)));
                    assignInPlace(o, e);
                }
                else if (((e.type.value.ty & 0xFF) == ENUMTY.Tsarray) && ((e.op & 0xFF) != 128) && ((o.type.value.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    assignInPlace(o, e);
                }
                else
                {
                    oldelems.set(i, (newelems).get(i));
                }
            }
        }
    }

    // Erasure: assignAssocArrayElement<Loc, AssocArrayLiteralExp, Expression, Expression>
    public static Expression assignAssocArrayElement(Loc loc, AssocArrayLiteralExp aae, Expression index, Expression newval) {
        DArray<Expression> keysx = aae.keys;
        DArray<Expression> valuesx = aae.values;
        int updated = 0;
        {
            int j = (valuesx).length;
            for (; j != 0;){
                j--;
                Expression ekey = (aae.keys).get(j);
                int eq = ctfeEqual(loc, TOK.equal, ekey, index);
                if (eq != 0)
                {
                    valuesx.set(j, newval);
                    updated = 1;
                }
            }
        }
        if (updated == 0)
        {
            (valuesx).push(newval);
            (keysx).push(index);
        }
        return newval;
    }

    // Erasure: changeArrayLiteralLength<Loc, TypeArray, Expression, int, int>
    public static UnionExp changeArrayLiteralLength(Loc loc, TypeArray arrayType, Expression oldval, int oldlen, int newlen) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        Type elemType = arrayType.next.value;
        assert(elemType != null);
        Expression defaultElem = elemType.defaultInitLiteral(loc);
        DArray<Expression> elements = new DArray<Expression>(newlen);
        int indxlo = 0;
        if (((oldval.op & 0xFF) == 31))
        {
            indxlo = (int)(((SliceExp)oldval)).lwr.value.toInteger();
            oldval = (((SliceExp)oldval)).e1.value;
        }
        int copylen = (oldlen < newlen) ? oldlen : newlen;
        if (((oldval.op & 0xFF) == 121))
        {
            StringExp oldse = ((StringExp)oldval);
            Object s = pcopy(Mem.xcalloc(newlen + 1, (oldse.sz & 0xFF)));
            memcpy((BytePtr)s, (oldse.string), (copylen * (oldse.sz & 0xFF)));
            int defaultValue = (int)defaultElem.toInteger();
            {
                int __key892 = copylen;
                int __limit893 = newlen;
                for (; (__key892 < __limit893);__key892 += 1) {
                    int elemi = __key892;
                    switch ((oldse.sz & 0xFF))
                    {
                        case 1:
                            (((BytePtr)s)).set((indxlo + elemi), (byte)defaultValue);
                            break;
                        case 2:
                            (((CharPtr)s)).set((indxlo + elemi), (char)defaultValue);
                            break;
                        case 4:
                            (((Ptr<Integer>)s)).set((indxlo + elemi), defaultValue);
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
            ptr(ue) = new UnionExp(new StringExp(loc, s, newlen));
            StringExp se = ((StringExp)ue.value.exp());
            se.type.value = arrayType;
            se.sz = oldse.sz;
            se.committed = oldse.committed;
            se.ownedByCtfe = OwnedBy.ctfe;
        }
        else
        {
            if ((oldlen != 0))
            {
                assert(((oldval.op & 0xFF) == 47));
                ArrayLiteralExp ae = ((ArrayLiteralExp)oldval);
                {
                    int __key894 = 0;
                    int __limit895 = copylen;
                    for (; (__key894 < __limit895);__key894 += 1) {
                        int i = __key894;
                        elements.set(i, (ae.elements).get(indxlo + i));
                    }
                }
            }
            if (((elemType.ty & 0xFF) == ENUMTY.Tstruct) || ((elemType.ty & 0xFF) == ENUMTY.Tsarray))
            {
                {
                    int __key896 = copylen;
                    int __limit897 = newlen;
                    for (; (__key896 < __limit897);__key896 += 1) {
                        int i = __key896;
                        elements.set(i, copyLiteral(defaultElem).copy());
                    }
                }
            }
            else
            {
                {
                    int __key898 = copylen;
                    int __limit899 = newlen;
                    for (; (__key898 < __limit899);__key898 += 1) {
                        int i = __key898;
                        elements.set(i, defaultElem);
                    }
                }
            }
            ptr(ue) = new UnionExp(new ArrayLiteralExp(loc, arrayType, elements));
            ArrayLiteralExp aae = ((ArrayLiteralExp)ue.value.exp());
            aae.ownedByCtfe = OwnedBy.ctfe;
        }
        return ue.value;
    }

    // Erasure: isCtfeValueValid<Expression>
    public static boolean isCtfeValueValid(Expression newval) {
        Type tb = newval.type.value.toBasetype();
        if (((newval.op & 0xFF) == 135) || ((newval.op & 0xFF) == 140) || ((newval.op & 0xFF) == 148) || ((newval.op & 0xFF) == 147))
        {
            return tb.isscalar();
        }
        if (((newval.op & 0xFF) == 13))
        {
            return ((tb.ty & 0xFF) == ENUMTY.Tnull) || ((tb.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Taarray) || ((tb.ty & 0xFF) == ENUMTY.Tclass) || ((tb.ty & 0xFF) == ENUMTY.Tdelegate);
        }
        if (((newval.op & 0xFF) == 121))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 47))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 48))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 49))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 50))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 229))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 161))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 160))
        {
            Expression ethis = (((DelegateExp)newval)).e1.value;
            return ((ethis.op & 0xFF) == 49) || ((ethis.op & 0xFF) == 50) || ((ethis.op & 0xFF) == 26) && (pequals((((VarExp)ethis)).var, (((DelegateExp)newval)).func));
        }
        if (((newval.op & 0xFF) == 25))
        {
            Declaration d = (((SymOffExp)newval)).var;
            return (d.isFuncDeclaration() != null) || d.isDataseg();
        }
        if (((newval.op & 0xFF) == 42))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 19))
        {
            Expression e1 = (((AddrExp)newval)).e1.value;
            return ((tb.ty & 0xFF) == ENUMTY.Tpointer) && ((e1.op & 0xFF) == 49) || ((e1.op & 0xFF) == 47) && isCtfeValueValid(e1) || ((e1.op & 0xFF) == 26) || ((e1.op & 0xFF) == 27) && isCtfeReferenceValid(e1) || ((e1.op & 0xFF) == 62) && isCtfeReferenceValid(e1) || ((e1.op & 0xFF) == 31) && ((e1.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray);
        }
        if (((newval.op & 0xFF) == 31))
        {
            SliceExp se = ((SliceExp)newval);
            assert((se.lwr.value != null) && ((se.lwr.value.op & 0xFF) == 135));
            assert((se.upr.value != null) && ((se.upr.value.op & 0xFF) == 135));
            return ((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray) && ((se.e1.value.op & 0xFF) == 121) || ((se.e1.value.op & 0xFF) == 47);
        }
        if (((newval.op & 0xFF) == 128))
        {
            return true;
        }
        newval.error(new BytePtr("CTFE internal error: illegal CTFE value `%s`"), newval.toChars());
        return false;
    }

    // Erasure: isCtfeReferenceValid<Expression>
    public static boolean isCtfeReferenceValid(Expression newval) {
        if (((newval.op & 0xFF) == 123))
        {
            return true;
        }
        if (((newval.op & 0xFF) == 26))
        {
            VarDeclaration v = (((VarExp)newval)).var.isVarDeclaration();
            assert(v != null);
            return true;
        }
        if (((newval.op & 0xFF) == 62))
        {
            Expression eagg = (((IndexExp)newval)).e1.value;
            return ((eagg.op & 0xFF) == 121) || ((eagg.op & 0xFF) == 47) || ((eagg.op & 0xFF) == 48);
        }
        if (((newval.op & 0xFF) == 27))
        {
            Expression eagg = (((DotVarExp)newval)).e1.value;
            return ((eagg.op & 0xFF) == 49) || ((eagg.op & 0xFF) == 50) && isCtfeValueValid(eagg);
        }
        return isCtfeValueValid(newval);
    }

    // Erasure: showCtfeExpr<Expression, int>
    public static void showCtfeExpr(Expression e, int level) {
        {
            int i = level;
            for (; (i > 0);i -= 1) {
                printf(new BytePtr(" "));
            }
        }
        DArray<Expression> elements = null;
        StructDeclaration sd = null;
        ClassDeclaration cd = null;
        if (((e.op & 0xFF) == 49))
        {
            elements = pcopy((((StructLiteralExp)e)).elements);
            sd = (((StructLiteralExp)e)).sd;
            printf(new BytePtr("STRUCT type = %s %p:\n"), e.type.value.toChars(), e);
        }
        else if (((e.op & 0xFF) == 50))
        {
            elements = pcopy((((ClassReferenceExp)e)).value.elements);
            cd = (((ClassReferenceExp)e)).originalClass();
            printf(new BytePtr("CLASS type = %s %p:\n"), e.type.value.toChars(), (((ClassReferenceExp)e)).value);
        }
        else if (((e.op & 0xFF) == 47))
        {
            elements = pcopy((((ArrayLiteralExp)e)).elements);
            printf(new BytePtr("ARRAY LITERAL type=%s %p:\n"), e.type.value.toChars(), e);
        }
        else if (((e.op & 0xFF) == 48))
        {
            printf(new BytePtr("AA LITERAL type=%s %p:\n"), e.type.value.toChars(), e);
        }
        else if (((e.op & 0xFF) == 121))
        {
            printf(new BytePtr("STRING %s %p\n"), e.toChars(), (((StringExp)e)).string);
        }
        else if (((e.op & 0xFF) == 31))
        {
            printf(new BytePtr("SLICE %p: %s\n"), e, e.toChars());
            showCtfeExpr((((SliceExp)e)).e1.value, level + 1);
        }
        else if (((e.op & 0xFF) == 26))
        {
            printf(new BytePtr("VAR %p %s\n"), e, e.toChars());
            VarDeclaration v = (((VarExp)e)).var.isVarDeclaration();
            if ((v != null) && (getValue(v) != null))
            {
                showCtfeExpr(getValue(v), level + 1);
            }
        }
        else if (((e.op & 0xFF) == 19))
        {
            printf(new BytePtr("POINTER %p to %p: %s\n"), e, (((AddrExp)e)).e1.value, e.toChars());
        }
        else
        {
            printf(new BytePtr("VALUE %p: %s\n"), e, e.toChars());
        }
        if (elements != null)
        {
            int fieldsSoFar = 0;
            {
                int i = 0;
                for (; (i < (elements).length);i++){
                    Expression z = null;
                    VarDeclaration v = null;
                    if ((i > 15))
                    {
                        printf(new BytePtr("...(total %d elements)\n"), (elements).length);
                        return ;
                    }
                    if (sd != null)
                    {
                        v = sd.fields.get(i);
                        z = (elements).get(i);
                    }
                    else if (cd != null)
                    {
                        for (; (i - fieldsSoFar >= cd.fields.length);){
                            fieldsSoFar += cd.fields.length;
                            cd = cd.baseClass;
                            {
                                int j = level;
                                for (; (j > 0);j -= 1) {
                                    printf(new BytePtr(" "));
                                }
                            }
                            printf(new BytePtr(" BASE CLASS: %s\n"), cd.toChars());
                        }
                        v = cd.fields.get(i - fieldsSoFar);
                        assert(((elements).length + i >= fieldsSoFar + cd.fields.length));
                        int indx = (elements).length - fieldsSoFar - cd.fields.length + i;
                        assert((indx < (elements).length));
                        z = (elements).get(indx);
                    }
                    if (z == null)
                    {
                        {
                            int j = level;
                            for (; (j > 0);j -= 1) {
                                printf(new BytePtr(" "));
                            }
                        }
                        printf(new BytePtr(" void\n"));
                        continue;
                    }
                    if (v != null)
                    {
                        if (((v.type.ty & 0xFF) != (z.type.value.ty & 0xFF)) && ((v.type.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            {
                                int j = level;
                                for (; (j -= 1) != 0;) {
                                    printf(new BytePtr(" "));
                                }
                            }
                            printf(new BytePtr(" field: block initialized static array\n"));
                            continue;
                        }
                    }
                    showCtfeExpr(z, level + 1);
                }
            }
        }
    }

    // defaulted all parameters starting with #2
    public static void showCtfeExpr(Expression e) {
        showCtfeExpr(e, 0);
    }

    // Erasure: voidInitLiteral<Type, VarDeclaration>
    public static UnionExp voidInitLiteral(Type t, VarDeclaration var) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (((t.ty & 0xFF) == ENUMTY.Tsarray))
        {
            TypeSArray tsa = ((TypeSArray)t);
            Expression elem = voidInitLiteral(tsa.next.value, var).copy();
            boolean mustCopy = ((elem.op & 0xFF) == 47) || ((elem.op & 0xFF) == 49);
            int d = (int)tsa.dim.toInteger();
            DArray<Expression> elements = new DArray<Expression>(d);
            {
                int __key900 = 0;
                int __limit901 = d;
                for (; (__key900 < __limit901);__key900 += 1) {
                    int i = __key900;
                    if (mustCopy && (i > 0))
                    {
                        elem = copyLiteral(elem).copy();
                    }
                    elements.set(i, elem);
                }
            }
            ptr(ue) = new UnionExp(new ArrayLiteralExp(var.loc, tsa, elements));
            ArrayLiteralExp ae = ((ArrayLiteralExp)ue.value.exp());
            ae.ownedByCtfe = OwnedBy.ctfe;
        }
        else if (((t.ty & 0xFF) == ENUMTY.Tstruct))
        {
            TypeStruct ts = ((TypeStruct)t);
            DArray<Expression> exps = new DArray<Expression>(ts.sym.fields.length);
            {
                int __key902 = 0;
                int __limit903 = ts.sym.fields.length;
                for (; (__key902 < __limit903);__key902 += 1) {
                    int i = __key902;
                    exps.set(i, voidInitLiteral(ts.sym.fields.get(i).type, ts.sym.fields.get(i)).copy());
                }
            }
            ptr(ue) = new UnionExp(new StructLiteralExp(var.loc, ts.sym, exps));
            StructLiteralExp se = ((StructLiteralExp)ue.value.exp());
            se.type.value = ts;
            se.ownedByCtfe = OwnedBy.ctfe;
        }
        else
        {
            ptr(ue) = new UnionExp(new VoidInitExp(var));
        }
        return ue.value;
    }

}
