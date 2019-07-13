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
        public CtfeStatus(){
        }
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
        public  ClassReferenceExp(Loc loc, StructLiteralExp lit, Type type) {
            super(loc, TOK.classReference, 28);
            assert((lit != null) && (lit.sd != null) && (lit.sd.isClassDeclaration() != null));
            this.value = lit;
            this.type.value = type;
        }

        public  ClassDeclaration originalClass() {
            return this.value.sd.isClassDeclaration();
        }

        public  int getFieldIndex(Type fieldtype, int fieldoffset) {
            ClassDeclaration cd = this.originalClass();
            int fieldsSoFar = 0;
            {
                int j = 0;
                for (; (j < (this.value.elements.value.get()).length.value);j++){
                    for (; (j - fieldsSoFar >= cd.fields.length.value);){
                        fieldsSoFar += cd.fields.length.value;
                        cd = cd.baseClass.value;
                    }
                    VarDeclaration v2 = cd.fields.get(j - fieldsSoFar);
                    if ((fieldoffset == v2.offset.value) && (fieldtype.size() == v2.type.value.size()))
                    {
                        return ((this.value.elements.value.get()).length.value - fieldsSoFar - cd.fields.length.value + (j - fieldsSoFar));
                    }
                }
            }
            return -1;
        }

        public  int findFieldIndexByName(VarDeclaration v) {
            ClassDeclaration cd = this.originalClass();
            int fieldsSoFar = 0;
            {
                int j = 0;
                for (; (j < (this.value.elements.value.get()).length.value);j++){
                    for (; (j - fieldsSoFar >= cd.fields.length.value);){
                        fieldsSoFar += cd.fields.length.value;
                        cd = cd.baseClass.value;
                    }
                    VarDeclaration v2 = cd.fields.get(j - fieldsSoFar);
                    if ((pequals(v, v2)))
                    {
                        return ((this.value.elements.value.get()).length.value - fieldsSoFar - cd.fields.length.value + (j - fieldsSoFar));
                    }
                }
            }
            return -1;
        }

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
    public static int findFieldIndexByName(StructDeclaration sd, VarDeclaration v) {
        {
            Slice<VarDeclaration> __r851 = sd.fields.opSlice().copy();
            int __key850 = 0;
            for (; (__key850 < __r851.getLength());__key850 += 1) {
                VarDeclaration field = __r851.get(__key850);
                int i = __key850;
                if ((pequals(field, v)))
                    return i;
            }
        }
        return -1;
    }

    public static class ThrownExceptionExp extends Expression
    {
        public ClassReferenceExp thrown = null;
        public  ThrownExceptionExp(Loc loc, ClassReferenceExp victim) {
            super(loc, TOK.thrownException, 28);
            this.thrown = victim;
            this.type.value = victim.type.value;
        }

        public  BytePtr toChars() {
            return new BytePtr("CTFE ThrownException");
        }

        public  void generateUncaughtError() {
            Ref<UnionExp> ue = ref(null);
            Expression e = resolveSlice((this.thrown.value.elements.value.get()).get(0), ptr(ue));
            StringExp se = e.toStringExp();
            this.thrown.error(new BytePtr("uncaught CTFE exception `%s(%s)`"), this.thrown.type.value.toChars(), se != null ? se.toChars() : e.toChars());
            if (this.loc.value.isValid() && !this.loc.value.equals(this.thrown.loc.value))
                errorSupplemental(this.loc.value, new BytePtr("thrown from here"));
        }

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
        public  CTFEExp(byte tok) {
            super(Loc.initial.value, tok, 24);
            this.type.value = Type.tvoid.value;
        }

        public  BytePtr toChars() {
            switch ((this.op.value & 0xFF))
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

        public static Ref<CTFEExp> cantexp = ref(null);
        public static CTFEExp voidexp = null;
        public static CTFEExp breakexp = null;
        public static CTFEExp continueexp = null;
        public static CTFEExp gotoexp = null;
        public static CTFEExp showcontext = null;
        public static boolean isCantExp(Expression e) {
            return (e != null) && ((e.op.value & 0xFF) == 233);
        }

        public static boolean isGotoExp(Expression e) {
            return (e != null) && ((e.op.value & 0xFF) == 196);
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
    public static boolean exceptionOrCantInterpret(Expression e) {
        return (e != null) && ((e.op.value & 0xFF) == 233) || ((e.op.value & 0xFF) == 51) || ((e.op.value & 0xFF) == 234);
    }

    public static boolean needToCopyLiteral(Expression expr) {
        Expression e = expr;
        for (; ;){
            switch ((e.op.value & 0xFF))
            {
                case 47:
                    return (((ArrayLiteralExp)e).ownedByCtfe & 0xFF) == 0;
                case 48:
                    return (((AssocArrayLiteralExp)e).ownedByCtfe & 0xFF) == 0;
                case 49:
                    return (((StructLiteralExp)e).ownedByCtfe.value & 0xFF) == 0;
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
                    e = ((UnaExp)e).e1.value;
                    continue;
                case 70:
                    return needToCopyLiteral(((BinExp)e).e1.value) || needToCopyLiteral(((BinExp)e).e2.value);
                case 71:
                case 72:
                case 73:
                    e = ((BinExp)e).e2.value;
                    continue;
                default:
                return false;
            }
        }
    }

    public static Ptr<DArray<Expression>> copyLiteralArray(Ptr<DArray<Expression>> oldelems, Expression basis) {
        if (oldelems == null)
            return oldelems;
        CtfeStatus.numArrayAllocs++;
        Ptr<DArray<Expression>> newelems = refPtr(new DArray<Expression>((oldelems.get()).length.value));
        {
            Slice<Expression> __r853 = (oldelems.get()).opSlice().copy();
            int __key852 = 0;
            for (; (__key852 < __r853.getLength());__key852 += 1) {
                Expression el = __r853.get(__key852);
                int i = __key852;
                newelems.get().set(i, copyLiteral(el != null ? el : basis).copy());
            }
        }
        return newelems;
    }

    // defaulted all parameters starting with #2
    public static Ptr<DArray<Expression>> copyLiteralArray(Ptr<DArray<Expression>> oldelems) {
        return copyLiteralArray(oldelems, null);
    }

    public static UnionExp copyLiteral(Expression e) {
        Ref<UnionExp> ue = ref(null);
        if (((e.op.value & 0xFF) == 121))
        {
            StringExp se = (StringExp)e;
            BytePtr s = pcopy(ptr(new byte[(se.sz.value & 0xFF)]));
            memcpy((BytePtr)(s), (se.string.value), (se.len.value * (se.sz.value & 0xFF)));
            ptr(ue) = new UnionExp(new StringExp(se.loc.value, s, se.len.value));
            StringExp se2 = (StringExp)ue.value.exp();
            se2.committed.value = se.committed.value;
            se2.postfix.value = se.postfix.value;
            se2.type.value = se.type.value;
            se2.sz.value = se.sz.value;
            se2.ownedByCtfe = OwnedBy.ctfe;
            return ue.value;
        }
        if (((e.op.value & 0xFF) == 47))
        {
            ArrayLiteralExp ale = (ArrayLiteralExp)e;
            Ptr<DArray<Expression>> elements = copyLiteralArray(ale.elements.value, ale.basis.value);
            ptr(ue) = new UnionExp(new ArrayLiteralExp(e.loc.value, e.type.value, elements));
            ArrayLiteralExp r = (ArrayLiteralExp)ue.value.exp();
            r.ownedByCtfe = OwnedBy.ctfe;
            return ue.value;
        }
        if (((e.op.value & 0xFF) == 48))
        {
            AssocArrayLiteralExp aae = (AssocArrayLiteralExp)e;
            ptr(ue) = new UnionExp(new AssocArrayLiteralExp(e.loc.value, copyLiteralArray(aae.keys.value, null), copyLiteralArray(aae.values.value, null)));
            AssocArrayLiteralExp r = (AssocArrayLiteralExp)ue.value.exp();
            r.type.value = e.type.value;
            r.ownedByCtfe = OwnedBy.ctfe;
            return ue.value;
        }
        if (((e.op.value & 0xFF) == 49))
        {
            StructLiteralExp sle = (StructLiteralExp)e;
            Ptr<DArray<Expression>> oldelems = sle.elements.value;
            Ptr<DArray<Expression>> newelems = refPtr(new DArray<Expression>((oldelems.get()).length.value));
            {
                Slice<Expression> __r855 = (newelems.get()).opSlice().copy();
                int __key854 = 0;
                for (; (__key854 < __r855.getLength());__key854 += 1) {
                    Expression el = __r855.get(__key854);
                    int i = __key854;
                    VarDeclaration v = sle.sd.fields.get(i);
                    Expression m = (oldelems.get()).get(i);
                    if (m == null)
                        m = voidInitLiteral(v.type.value, v).copy();
                    if (((v.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((v.type.value.ty.value & 0xFF) == ENUMTY.Taarray))
                    {
                    }
                    else
                    {
                        m = copyLiteral(m).copy();
                        if (((v.type.value.ty.value & 0xFF) != (m.type.value.ty.value & 0xFF)) && ((v.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            TypeSArray tsa = (TypeSArray)v.type.value;
                            int len = (int)tsa.dim.value.toInteger();
                            Ref<UnionExp> uex = ref(null);
                            m = createBlockDuplicatedArrayLiteral(ptr(uex), e.loc.value, v.type.value, m, len);
                            if ((pequals(m, uex.value.exp())))
                                m = uex.value.copy();
                        }
                    }
                    el = m;
                }
            }
            ptr(ue) = new UnionExp(new StructLiteralExp(e.loc.value, sle.sd, newelems, sle.stype.value));
            StructLiteralExp r = (StructLiteralExp)ue.value.exp();
            r.type.value = e.type.value;
            r.ownedByCtfe.value = OwnedBy.ctfe;
            r.origin = ((StructLiteralExp)e).origin;
            return ue.value;
        }
        if (((e.op.value & 0xFF) == 161) || ((e.op.value & 0xFF) == 160) || ((e.op.value & 0xFF) == 25) || ((e.op.value & 0xFF) == 13) || ((e.op.value & 0xFF) == 26) || ((e.op.value & 0xFF) == 27) || ((e.op.value & 0xFF) == 135) || ((e.op.value & 0xFF) == 140) || ((e.op.value & 0xFF) == 148) || ((e.op.value & 0xFF) == 147) || ((e.op.value & 0xFF) == 128) || ((e.op.value & 0xFF) == 229) || ((e.op.value & 0xFF) == 42))
        {
            ptr(ue) = new UnionExp(new UnionExp(e));
            Expression r = ue.value.exp();
            r.type.value = e.type.value;
            return ue.value;
        }
        if (((e.op.value & 0xFF) == 31))
        {
            SliceExp se = (SliceExp)e;
            if (((se.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (((se.e1.value.op.value & 0xFF) == 13))
                {
                    ptr(ue) = new UnionExp(new NullExp(se.loc.value, se.type.value));
                    return ue.value;
                }
                ue.value = Slice(se.type.value, se.e1.value, se.lwr.value, se.upr.value).copy();
                assert(((ue.value.exp().op.value & 0xFF) == 47));
                ArrayLiteralExp r = (ArrayLiteralExp)ue.value.exp();
                r.elements.value = copyLiteralArray(r.elements.value, null);
                r.ownedByCtfe = OwnedBy.ctfe;
                return ue.value;
            }
            else
            {
                ptr(ue) = new UnionExp(new SliceExp(e.loc.value, se.e1.value, se.lwr.value, se.upr.value));
                Expression r = ue.value.exp();
                r.type.value = e.type.value;
                return ue.value;
            }
        }
        if (isPointer(e.type.value))
        {
            if (((e.op.value & 0xFF) == 19))
                ptr(ue) = new UnionExp(new AddrExp(e.loc.value, ((AddrExp)e).e1.value));
            else if (((e.op.value & 0xFF) == 62))
                ptr(ue) = new UnionExp(new IndexExp(e.loc.value, ((IndexExp)e).e1.value, ((IndexExp)e).e2.value));
            else if (((e.op.value & 0xFF) == 27))
            {
                ptr(ue) = new UnionExp(new DotVarExp(e.loc.value, ((DotVarExp)e).e1.value, ((DotVarExp)e).var.value, ((DotVarExp)e).hasOverloads));
            }
            else
                throw new AssertionError("Unreachable code!");
            Expression r = ue.value.exp();
            r.type.value = e.type.value;
            return ue.value;
        }
        if (((e.op.value & 0xFF) == 50))
        {
            ptr(ue) = new UnionExp(new ClassReferenceExp(e.loc.value, ((ClassReferenceExp)e).value, e.type.value));
            return ue.value;
        }
        if (((e.op.value & 0xFF) == 127))
        {
            ptr(ue) = new UnionExp(new UnionExp(e));
            return ue.value;
        }
        e.error(new BytePtr("CTFE internal error: literal `%s`"), e.toChars());
        throw new AssertionError("Unreachable code!");
    }

    public static Expression paintTypeOntoLiteral(Type type, Expression lit) {
        if (lit.type.value.equals(type))
            return lit;
        return paintTypeOntoLiteralCopy(type, lit).copy();
    }

    public static Expression paintTypeOntoLiteral(Ptr<UnionExp> pue, Type type, Expression lit) {
        if (lit.type.value.equals(type))
            return lit;
        pue.set(0, paintTypeOntoLiteralCopy(type, lit));
        return (pue.get()).exp();
    }

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
        if (((lit.op.value & 0xFF) == 31))
        {
            SliceExp se = (SliceExp)lit;
            ptr(ue) = new UnionExp(new SliceExp(lit.loc.value, se.e1.value, se.lwr.value, se.upr.value));
        }
        else if (((lit.op.value & 0xFF) == 62))
        {
            IndexExp ie = (IndexExp)lit;
            ptr(ue) = new UnionExp(new IndexExp(lit.loc.value, ie.e1.value, ie.e2.value));
        }
        else if (((lit.op.value & 0xFF) == 47))
        {
            ptr(ue) = new UnionExp(new SliceExp(lit.loc.value, lit, new IntegerExp(Loc.initial.value, 0L, Type.tsize_t.value), ArrayLength(Type.tsize_t.value, lit).copy()));
        }
        else if (((lit.op.value & 0xFF) == 121))
        {
            ptr(ue) = new UnionExp(new SliceExp(lit.loc.value, lit, new IntegerExp(Loc.initial.value, 0L, Type.tsize_t.value), ArrayLength(Type.tsize_t.value, lit).copy()));
        }
        else if (((lit.op.value & 0xFF) == 48))
        {
            AssocArrayLiteralExp aae = (AssocArrayLiteralExp)lit;
            byte wasOwned = aae.ownedByCtfe;
            ptr(ue) = new UnionExp(new AssocArrayLiteralExp(lit.loc.value, aae.keys.value, aae.values.value));
            aae = (AssocArrayLiteralExp)ue.value.exp();
            aae.ownedByCtfe = wasOwned;
        }
        else
        {
            if (((lit.op.value & 0xFF) == 49) && isPointer(type))
                lit.error(new BytePtr("CTFE internal error: painting `%s`"), type.toChars());
            ue.value = copyLiteral(lit).copy();
        }
        ue.value.exp().type.value = type;
        return ue.value;
    }

    public static Expression resolveSlice(Expression e, Ptr<UnionExp> pue) {
        if (((e.op.value & 0xFF) != 31))
            return e;
        SliceExp se = (SliceExp)e;
        if (((se.e1.value.op.value & 0xFF) == 13))
            return se.e1.value;
        if (pue != null)
        {
            pue.set(0, Slice(e.type.value, se.e1.value, se.lwr.value, se.upr.value));
            return (pue.get()).exp();
        }
        else
            return Slice(e.type.value, se.e1.value, se.lwr.value, se.upr.value).copy();
    }

    // defaulted all parameters starting with #2
    public static Expression resolveSlice(Expression e) {
        return resolveSlice(e, null);
    }

    public static long resolveArrayLength(Expression e) {
        switch ((e.op.value & 0xFF))
        {
            case 229:
                return (long)((VectorExp)e).dim.value;
            case 13:
                return 0L;
            case 31:
                long ilo = ((SliceExp)e).lwr.value.toInteger();
                long iup = ((SliceExp)e).upr.value.toInteger();
                return iup - ilo;
            case 121:
                return (long)((StringExp)e).len.value;
            case 47:
                ArrayLiteralExp ale = (ArrayLiteralExp)e;
                return ale.elements.value != null ? (long)(ale.elements.value.get()).length.value : 0L;
            case 48:
                AssocArrayLiteralExp ale_1 = (AssocArrayLiteralExp)e;
                return (long)(ale_1.keys.value.get()).length.value;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }

    public static ArrayLiteralExp createBlockDuplicatedArrayLiteral(Ptr<UnionExp> pue, Loc loc, Type type, Expression elem, int dim) {
        if (((type.ty.value & 0xFF) == ENUMTY.Tsarray) && ((type.nextOf().ty.value & 0xFF) == ENUMTY.Tsarray) && ((elem.type.value.ty.value & 0xFF) != ENUMTY.Tsarray))
        {
            TypeSArray tsa = (TypeSArray)type.nextOf();
            int len = (int)tsa.dim.value.toInteger();
            Ref<UnionExp> ue = ref(null);
            elem = createBlockDuplicatedArrayLiteral(ptr(ue), loc, type.nextOf(), elem, len);
            if ((pequals(elem, ue.value.exp())))
                elem = ue.value.copy();
        }
        Type tb = elem.type.value.toBasetype();
        boolean mustCopy = ((tb.ty.value & 0xFF) == ENUMTY.Tstruct) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray);
        Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(dim));
        {
            Slice<Expression> __r857 = (elements.get()).opSlice().copy();
            int __key856 = 0;
            for (; (__key856 < __r857.getLength());__key856 += 1) {
                Expression el = __r857.get(__key856);
                int i = __key856;
                el = mustCopy && (i != 0) ? copyLiteral(elem).copy() : elem;
            }
        }
        (pue) = new UnionExp(new ArrayLiteralExp(loc, type, elements));
        ArrayLiteralExp ale = (ArrayLiteralExp)(pue.get()).exp();
        ale.ownedByCtfe = OwnedBy.ctfe;
        return ale;
    }

    public static StringExp createBlockDuplicatedStringLiteral(Ptr<UnionExp> pue, Loc loc, Type type, int value, int dim, byte sz) {
        BytePtr s = pcopy(ptr(new byte[(sz & 0xFF)]));
        {
            int __key858 = 0;
            int __limit859 = dim;
            for (; (__key858 < __limit859);__key858 += 1) {
                int elemi = __key858;
                switch ((sz & 0xFF))
                {
                    case 1:
                        s.set(elemi, (byte)value);
                        break;
                    case 2:
                        (toCharPtr(s)).set(elemi, (char)value);
                        break;
                    case 4:
                        (toIntPtr(s)).set(elemi, value);
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
        }
        (pue) = new UnionExp(new StringExp(loc, s, dim));
        StringExp se = (StringExp)(pue.get()).exp();
        se.type.value = type;
        se.sz.value = sz;
        se.committed.value = (byte)1;
        se.ownedByCtfe = OwnedBy.ctfe;
        return se;
    }

    public static boolean isAssocArray(Type t) {
        t = t.toBasetype();
        if (((t.ty.value & 0xFF) == ENUMTY.Taarray))
            return true;
        return false;
    }

    public static TypeAArray toBuiltinAAType(Type t) {
        t = t.toBasetype();
        if (((t.ty.value & 0xFF) == ENUMTY.Taarray))
            return (TypeAArray)t;
        throw new AssertionError("Unreachable code!");
    }

    public static boolean isTypeInfo_Class(Type type) {
        return ((type.ty.value & 0xFF) == ENUMTY.Tclass) && (pequals(Type.dtypeinfo.value, ((TypeClass)type).sym.value)) || Type.dtypeinfo.value.isBaseOf(((TypeClass)type).sym.value, null);
    }

    public static boolean isPointer(Type t) {
        Type tb = t.toBasetype();
        return ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) && ((tb.nextOf().ty.value & 0xFF) != ENUMTY.Tfunction);
    }

    public static boolean isTrueBool(Expression e) {
        return e.isBool(true) || ((e.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((e.type.value.ty.value & 0xFF) == ENUMTY.Tclass) && ((e.op.value & 0xFF) != 13);
    }

    public static boolean isSafePointerCast(Type srcPointee, Type destPointee) {
        for (; ((srcPointee.ty.value & 0xFF) == ENUMTY.Tpointer) && ((destPointee.ty.value & 0xFF) == ENUMTY.Tpointer);){
            srcPointee = srcPointee.nextOf();
            destPointee = destPointee.nextOf();
        }
        if (srcPointee.constConv(destPointee) != 0)
            return true;
        if (((srcPointee.ty.value & 0xFF) == ENUMTY.Tfunction) && ((destPointee.ty.value & 0xFF) == ENUMTY.Tfunction))
            return srcPointee.covariant(destPointee, null, true) == 1;
        if (((destPointee.ty.value & 0xFF) == ENUMTY.Tvoid))
            return true;
        if (((srcPointee.ty.value & 0xFF) == ENUMTY.Taarray) && (pequals(destPointee, Type.tvoidptr)))
            return true;
        if (((srcPointee.ty.value & 0xFF) == ENUMTY.Tsarray) && ((destPointee.ty.value & 0xFF) == ENUMTY.Tsarray))
        {
            if ((srcPointee.size() != destPointee.size()))
                return false;
            srcPointee = srcPointee.baseElemOf();
            destPointee = destPointee.baseElemOf();
        }
        return srcPointee.isintegral() && destPointee.isintegral() && (srcPointee.size() == destPointee.size());
    }

    public static Expression getAggregateFromPointer(Expression e, Ptr<Long> ofs) {
        ofs.set(0, 0L);
        if (((e.op.value & 0xFF) == 19))
            e = ((AddrExp)e).e1.value;
        if (((e.op.value & 0xFF) == 25))
            ofs.set(0, ((SymOffExp)e).offset.value);
        if (((e.op.value & 0xFF) == 27))
        {
            Expression ex = ((DotVarExp)e).e1.value;
            VarDeclaration v = ((DotVarExp)e).var.value.isVarDeclaration();
            assert(v != null);
            StructLiteralExp se = ((ex.op.value & 0xFF) == 50) ? ((ClassReferenceExp)ex).value : (StructLiteralExp)ex;
            int i = ((ex.op.value & 0xFF) == 50) ? ((ClassReferenceExp)ex).getFieldIndex(e.type.value, v.offset.value) : se.getFieldIndex(e.type.value, v.offset.value);
            e = (se.elements.value.get()).get(i);
        }
        if (((e.op.value & 0xFF) == 62))
        {
            IndexExp ie = (IndexExp)e;
            if (((ie.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((ie.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((ie.e1.value.op.value & 0xFF) == 121) || ((ie.e1.value.op.value & 0xFF) == 47) && ((ie.e2.value.op.value & 0xFF) == 135))
            {
                ofs.set(0, ie.e2.value.toInteger());
                return ie.e1.value;
            }
        }
        if (((e.op.value & 0xFF) == 31) && ((e.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
        {
            SliceExp se = (SliceExp)e;
            if (((se.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((se.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((se.e1.value.op.value & 0xFF) == 121) || ((se.e1.value.op.value & 0xFF) == 47) && ((se.lwr.value.op.value & 0xFF) == 135))
            {
                ofs.set(0, se.lwr.value.toInteger());
                return se.e1.value;
            }
        }
        return e;
    }

    public static boolean pointToSameMemoryBlock(Expression agg1, Expression agg2) {
        if ((pequals(agg1, agg2)))
            return true;
        if (((agg1.op.value & 0xFF) == 135) && ((agg2.op.value & 0xFF) == 135) && (agg1.toInteger() == agg2.toInteger()))
        {
            return true;
        }
        if (((agg1.op.value & 0xFF) == 26) && ((agg2.op.value & 0xFF) == 26) && (pequals(((VarExp)agg1).var.value, ((VarExp)agg2).var.value)))
        {
            return true;
        }
        if (((agg1.op.value & 0xFF) == 25) && ((agg2.op.value & 0xFF) == 25) && (pequals(((SymOffExp)agg1).var.value, ((SymOffExp)agg2).var.value)))
        {
            return true;
        }
        return false;
    }

    public static UnionExp pointerDifference(Loc loc, Type type, Expression e1, Expression e2) {
        Ref<UnionExp> ue = ref(null);
        Ref<Long> ofs1 = ref(0L);
        Ref<Long> ofs2 = ref(0L);
        Expression agg1 = getAggregateFromPointer(e1, ptr(ofs1));
        Expression agg2 = getAggregateFromPointer(e2, ptr(ofs2));
        if ((pequals(agg1, agg2)))
        {
            Type pointee = ((TypePointer)agg1.type.value).next.value;
            long sz = pointee.size();
            ptr(ue) = new UnionExp(new IntegerExp(loc, (ofs1.value - ofs2.value) * sz, type));
        }
        else if (((agg1.op.value & 0xFF) == 121) && ((agg2.op.value & 0xFF) == 121) && (((StringExp)agg1).string.value == ((StringExp)agg2).string.value))
        {
            Type pointee = ((TypePointer)agg1.type.value).next.value;
            long sz = pointee.size();
            ptr(ue) = new UnionExp(new IntegerExp(loc, (ofs1.value - ofs2.value) * sz, type));
        }
        else if (((agg1.op.value & 0xFF) == 25) && ((agg2.op.value & 0xFF) == 25) && (pequals(((SymOffExp)agg1).var.value, ((SymOffExp)agg2).var.value)))
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

    public static UnionExp pointerArithmetic(Loc loc, byte op, Type type, Expression eptr, Expression e2) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (((eptr.type.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid))
        {
            error(loc, new BytePtr("cannot perform arithmetic on `void*` pointers at compile time"));
        /*Lcant:*/
            ptr(ue) = new UnionExp(new CTFEExp(TOK.cantExpression));
            return ue.value;
        }
        if (((eptr.op.value & 0xFF) == 19))
            eptr = ((AddrExp)eptr).e1.value;
        Ref<Long> ofs1 = ref(0L);
        Expression agg1 = getAggregateFromPointer(eptr, ptr(ofs1));
        if (((agg1.op.value & 0xFF) == 25))
        {
            if (((((SymOffExp)agg1).var.value.type.value.ty.value & 0xFF) != ENUMTY.Tsarray))
            {
                error(loc, new BytePtr("cannot perform pointer arithmetic on arrays of unknown length at compile time"));
                /*goto Lcant*/throw Dispatch0.INSTANCE;
            }
        }
        else if (((agg1.op.value & 0xFF) != 121) && ((agg1.op.value & 0xFF) != 47))
        {
            error(loc, new BytePtr("cannot perform pointer arithmetic on non-arrays at compile time"));
            /*goto Lcant*/throw Dispatch0.INSTANCE;
        }
        long ofs2 = e2.toInteger();
        Type pointee = ((TypeNext)agg1.type.value.toBasetype()).next.value;
        long sz = pointee.size();
        long indx = 0L;
        long len = 0L;
        if (((agg1.op.value & 0xFF) == 25))
        {
            indx = (long)(ofs1.value / sz);
            len = ((TypeSArray)((SymOffExp)agg1).var.value.type.value).dim.value.toInteger();
        }
        else
        {
            Expression dollar = ArrayLength(Type.tsize_t.value, agg1).copy();
            assert(!CTFEExp.isCantExp(dollar));
            indx = (long)ofs1.value;
            len = dollar.toInteger();
        }
        if (((op & 0xFF) == 74) || ((op & 0xFF) == 76) || ((op & 0xFF) == 93))
            (long)indx += ofs2 / sz;
        else if (((op & 0xFF) == 75) || ((op & 0xFF) == 77) || ((op & 0xFF) == 94))
            (long)indx -= ofs2 / sz;
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
        if (((agg1.op.value & 0xFF) == 25))
        {
            ptr(ue) = new UnionExp(new SymOffExp(loc, ((SymOffExp)agg1).var.value, (long)indx * sz));
            SymOffExp se = (SymOffExp)ue.value.exp();
            se.type.value = type;
            return ue.value;
        }
        if (((agg1.op.value & 0xFF) != 47) && ((agg1.op.value & 0xFF) != 121))
        {
            error(loc, new BytePtr("CTFE internal error: pointer arithmetic `%s`"), agg1.toChars());
            /*goto Lcant*/throw Dispatch0.INSTANCE;
        }
        if (((eptr.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
        {
            long dim = ((TypeSArray)eptr.type.value.toBasetype()).dim.value.toInteger();
            SliceExp se = new SliceExp(loc, agg1, new IntegerExp(loc, (long)indx, Type.tsize_t.value), new IntegerExp(loc, (long)indx + dim, Type.tsize_t.value));
            se.type.value = type.toBasetype().nextOf();
            ptr(ue) = new UnionExp(new AddrExp(loc, se));
            ue.value.exp().type.value = type;
            return ue.value;
        }
        IntegerExp ofs = new IntegerExp(loc, (long)indx, Type.tsize_t.value);
        Expression ie = new IndexExp(loc, agg1, ofs);
        ie.type.value = type.toBasetype().nextOf();
        ptr(ue) = new UnionExp(new AddrExp(loc, ie));
        ue.value.exp().type.value = type;
        return ue.value;
    }

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
        boolean null1 = (agg1.op.value & 0xFF) == 13;
        boolean null2 = (agg2.op.value & 0xFF) == 13;
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
            cmp ^= 1;
        return cmp;
    }

    public static boolean isFloatIntPaint(Type to, Type from) {
        return (from.size() == to.size()) && from.isintegral() && to.isfloating() || from.isfloating() && to.isintegral();
    }

    public static Expression paintFloatInt(Ptr<UnionExp> pue, Expression fromVal, Type to) {
        if (exceptionOrCantInterpret(fromVal))
            return fromVal;
        assert((to.size() == 4L) || (to.size() == 8L));
        return Compiler.paintAsType(pue, fromVal, to);
    }

    public static boolean isCtfeComparable(Expression e) {
        if (((e.op.value & 0xFF) == 31))
            e = ((SliceExp)e).e1.value;
        if ((e.isConst() != 1))
        {
            if (((e.op.value & 0xFF) == 13) || ((e.op.value & 0xFF) == 121) || ((e.op.value & 0xFF) == 161) || ((e.op.value & 0xFF) == 160) || ((e.op.value & 0xFF) == 47) || ((e.op.value & 0xFF) == 49) || ((e.op.value & 0xFF) == 48) || ((e.op.value & 0xFF) == 50))
            {
                return true;
            }
            if (((e.op.value & 0xFF) == 42))
                return true;
            return false;
        }
        return true;
    }

    // from template numCmp!(Double)
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
    // removed duplicate function, [["Expression eval_bswapLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Ptr<DArray<Expression>> copyLiteralArrayPtr<DArray<Expression>>, Expression", "DtorDeclaration buildExternDDtorAggregateDeclaration, Ptr<Scope>", "StringExp createBlockDuplicatedStringLiteralPtr<UnionExp>, Loc, Type, int, int, byte", "TypeAArray toBuiltinAATypeType", "void createMatchNodes", "UnionExp ArrayLengthType, Expression", "void writeHighlightsPtr<Console>, Ptr<OutBuffer>", "boolean isFloatIntPaintType, Type", "Function3<Loc,FuncDeclaration,Ptr<DArray<Expression>>,Expression> builtin_lookupBytePtr", "int mainSlice<ByteSlice>", "void printDepsConditionalPtr<Scope>, DVCondition, ByteSlice", "Expression paintFloatIntPtr<UnionExp>, Expression, Type", "Expression eval_bsfLoc, FuncDeclaration, Ptr<DArray<Expression>>", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "boolean exceptionOrCantInterpretExpression", "void deprecationLoc, BytePtr", "void messageLoc, BytePtr", "boolean checkSymbolAccessPtr<Scope>, Dsymbol", "FuncDeclaration buildOpAssignStructDeclaration, Ptr<Scope>", "UnionExp ModLoc, Type, Expression, Expression", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "double creallcomplex_t", "boolean checkAccessLoc, Ptr<Scope>, Expression, Declaration", "void warningSupplementalLoc, BytePtr", "boolean canThrowExpression, FuncDeclaration, boolean", "Expression eval_ceilLoc, FuncDeclaration, Ptr<DArray<Expression>>", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,Ptr<DArray<Expression>>,Expression>", "UnionExp DivLoc, Type, Expression, Expression", "long getStorageClassPtr<PrefixAttributesASTBase>ASTBase", "Expression eval_isnanLoc, FuncDeclaration, Ptr<DArray<Expression>>", "void cantExpUnionExp", "boolean pointToSameMemoryBlockExpression, Expression", "UnionExp ComType, Expression", "ByteSlice initializerMsgtable", "ErrorExp arrayOpInvalidErrorExpression", "boolean isDigitSecondbyte", "Expression eval_fmaLoc, FuncDeclaration, Ptr<DArray<Expression>>", "UnionExp MulLoc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_24239CC9FAA32FB7", "boolean isTrueBoolExpression", "void builtin_init", "void errorSupplementalLoc, BytePtr", "long resolveArrayLengthExpression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "void builtinDeinitialize", "void parseModulePatternBytePtr, Ptr<MatcherNode>, int", "boolean checkAccessLoc, Ptr<Scope>, dmodule.Package", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "void errorLoc, BytePtr", "boolean utf_isValidDcharint", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void fatal", "UnionExp NegType, Expression", "boolean isidcharbyte", "int utf_codeLengthWcharint", "int isConstExpression", "boolean isBinArrayOpbyte", "BytePtr linkToCharsint", "void colorSyntaxHighlightPtr<OutBuffer>", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "boolean isUniAlphaint", "Expression eval_powLoc, FuncDeclaration, Ptr<DArray<Expression>>", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void errorBytePtr, int, int, BytePtr", "Expression eval_log10Loc, FuncDeclaration, Ptr<DArray<Expression>>", "UnionExp PtrType, Expression", "void utf_encodeWcharCharPtr, int", "void vwarningSupplementalLoc, BytePtr, Ptr<Slice<Object>>", "Expression eval_sqrtLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_builtinLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_roundLoc, FuncDeclaration, Ptr<DArray<Expression>>", "UnionExp ShrLoc, Type, Expression, Expression", "Expression eval_isinfinityLoc, FuncDeclaration, Ptr<DArray<Expression>>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralPtr<UnionExp>, Loc, Type, Expression, int", "boolean needOpEqualsStructDeclaration", "Expression getAggregateFromPointerExpression, Ptr<Long>", "UnionExp CastLoc, Type, Type, Expression", "UnionExp ShlLoc, Type, Expression, Expression", "UnionExp IndexType, Expression, Expression", "Expression eval_fabsLoc, FuncDeclaration, Ptr<DArray<Expression>>", "boolean symbolIsVisibledmodule.Module, Dsymbol", "void utf_encodeint, Object, int", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "Expression eval_copysignLoc, FuncDeclaration, Ptr<DArray<Expression>>", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "FuncDeclaration buildXopCmpStructDeclaration, Ptr<Scope>", "Expression eval_logLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_fminLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression arrayOpBinAssignExp, Ptr<Scope>", "boolean isCtfeComparableExpression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "long getStorageClassPtr<PrefixAttributesASTCodegen>ASTCodegen", "ByteSlice lexBytePtr, ByteSlice", "boolean isPointerType", "boolean needToCopyLiteralExpression", "boolean checkNonAssignmentArrayOpExpression, boolean", "void vwarningLoc, BytePtr, Ptr<Slice<Object>>", "int utf_codeLengthCharint", "Expression eval_yl2xLoc, FuncDeclaration, Ptr<DArray<Expression>>", "long mergeFuncAttrslong, FuncDeclaration", "Expression eval_yl2xp1Loc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_isfiniteLoc, FuncDeclaration, Ptr<DArray<Expression>>", "void deprecationSupplementalLoc, BytePtr", "UnionExp UshrLoc, Type, Expression, Expression", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "BytePtr modToCharsint", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "FuncDeclaration buildOpEqualsStructDeclaration, Ptr<Scope>", "boolean isBinAssignArrayOpbyte", "boolean findConditionPtr<DArray<Identifier>>, Identifier", "void vdeprecationLoc, BytePtr, Ptr<Slice<Object>>, BytePtr, BytePtr", "void vdeprecationSupplementalLoc, BytePtr, Ptr<Slice<Object>>", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Ptr<Scope>", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Ptr<Scope>", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "Expression eval_bsrLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_expLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_expm1Loc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_unimpLoc, FuncDeclaration, Ptr<DArray<Expression>>", "boolean c_isalnumint", "void verrorSupplementalLoc, BytePtr, Ptr<Slice<Object>>", "Expression eval_exp2Loc, FuncDeclaration, Ptr<DArray<Expression>>", "boolean symbolIsVisibleDsymbol, Dsymbol", "boolean isNonAssignmentArrayOpExpression", "Expression resolveSliceExpression, Ptr<UnionExp>", "boolean isArrayOpValidExpression", "void warningLoc, BytePtr", "UnionExp BoolType, Expression", "Expression paintTypeOntoLiteralType, Expression", "boolean isoctalbyte", "Expression eval_floorLoc, FuncDeclaration, Ptr<DArray<Expression>>", "boolean needToHashStructDeclaration", "Ptr<DArray<Expression>> copyElementsExpression, Expression", "Expression eval_cosLoc, FuncDeclaration, Ptr<DArray<Expression>>", "UnionExp AddLoc, Type, Expression, Expression", "UnionExp XorLoc, Type, Expression, Expression", "UnionExp MinLoc, Type, Expression, Expression", "boolean checkAccessAggregateDeclaration, Loc, Ptr<Scope>, Dsymbol", "void colorHighlightCodePtr<OutBuffer>", "double cimaglcomplex_t", "Expression resolveAliasThisPtr<Scope>, Expression, boolean", "void buildArrayOpPtr<Scope>, Expression, Ptr<DArray<RootObject>>, Ptr<DArray<Expression>>", "UnionExp AndLoc, Type, Expression, Expression", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "boolean hasProtectedAccessPtr<Scope>, Dsymbol", "boolean isTypeInfo_ClassType", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression arrayOpBinExp, Ptr<Scope>", "int isBuiltinFuncDeclaration", "void sliceAssignStringFromStringStringExp, StringExp, int", "Expression eval_log2Loc, FuncDeclaration, Ptr<DArray<Expression>>", "void vmessageLoc, BytePtr, Ptr<Slice<Object>>", "void messageBytePtr", "boolean needOpAssignStructDeclaration", "UnionExp NotType, Expression", "DtorDeclaration buildDtorAggregateDeclaration, Ptr<Scope>", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void verrorLoc, BytePtr, Ptr<Slice<Object>>, BytePtr, BytePtr, BytePtr", "ByteSlice lispyBytePtr, ByteSlice", "boolean walkPostorderExpression, StoppableVisitor", "Expression paintTypeOntoLiteralPtr<UnionExp>, Type, Expression", "Expression expTypeType, Expression", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Ptr<Scope>", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "boolean c_isxdigitint", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "ByteSlice deinitializerMsgtable", "void processFileByteSlice, ByteSlice, BytePtr_D1F3732A9A6A6D5A", "void utf_encodeCharBytePtr, int", "UnionExp copyLiteralExpression", "UnionExp paintTypeOntoLiteralCopyType, Expression", "Expression eval_ldexpLoc, FuncDeclaration, Ptr<DArray<Expression>>", "FuncDeclaration buildXtoHashStructDeclaration, Ptr<Scope>", "Expression eval_popcntLoc, FuncDeclaration, Ptr<DArray<Expression>>", "UnionExp CatType, Expression, Expression", "int comparePointersbyte, Expression, long, Expression, long", "FuncDeclaration buildXopEqualsStructDeclaration, Ptr<Scope>", "boolean isAssocArrayType", "boolean numCmpbyte, double, doubleDouble", "boolean isSafePointerCastType, Type", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void halt", "boolean symbolIsVisiblePtr<Scope>, Dsymbol", "Expression eval_fmaxLoc, FuncDeclaration, Ptr<DArray<Expression>>", "boolean numCmpbyte, long, longLong", "Expression eval_truncLoc, FuncDeclaration, Ptr<DArray<Expression>>", "FuncDeclaration buildInvAggregateDeclaration, Ptr<Scope>", "Expression eval_tanLoc, FuncDeclaration, Ptr<DArray<Expression>>", "Expression eval_sinLoc, FuncDeclaration, Ptr<DArray<Expression>>", "boolean hasPackageAccessPtr<Scope>, Dsymbol", "void verrorPrintLoc, int, BytePtr, BytePtr, Ptr<Slice<Object>>, BytePtr, BytePtr", "boolean includeImportedModuleCheckModuleComponentRange", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "boolean writeMixinByteSlice, Loc"]] signature: boolean numCmpbyte, long, longLong

    public static int specificCmp(byte op, int rawCmp) {
        return (numCmpInteger(op, rawCmp, 0) ? 1 : 0);
    }

    public static int intUnsignedCmp(byte op, long n1, long n2) {
        return (numCmpLong(op, n1, n2) ? 1 : 0);
    }

    public static int intSignedCmp(byte op, long n1, long n2) {
        return (numCmpLong(op, n1, n2) ? 1 : 0);
    }

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

    public static int ctfeCmpArrays(Loc loc, Expression e1, Expression e2, long len) {
        long lo1 = 0L;
        long lo2 = 0L;
        Expression x = e1;
        if (((x.op.value & 0xFF) == 31))
        {
            lo1 = ((SliceExp)x).lwr.value.toInteger();
            x = ((SliceExp)x).e1.value;
        }
        StringExp se1 = ((x.op.value & 0xFF) == 121) ? (StringExp)x : null;
        ArrayLiteralExp ae1 = ((x.op.value & 0xFF) == 47) ? (ArrayLiteralExp)x : null;
        x = e2;
        if (((x.op.value & 0xFF) == 31))
        {
            lo2 = ((SliceExp)x).lwr.value.toInteger();
            x = ((SliceExp)x).e1.value;
        }
        StringExp se2 = ((x.op.value & 0xFF) == 121) ? (StringExp)x : null;
        ArrayLiteralExp ae2 = ((x.op.value & 0xFF) == 47) ? (ArrayLiteralExp)x : null;
        if ((se1 != null) && (se2 != null))
            return sliceCmpStringWithString(se1, se2, (int)lo1, (int)lo2, (int)len);
        if ((se1 != null) && (ae2 != null))
            return sliceCmpStringWithArray(se1, ae2, (int)lo1, (int)lo2, (int)len);
        if ((se2 != null) && (ae1 != null))
            return -sliceCmpStringWithArray(se2, ae1, (int)lo2, (int)lo1, (int)len);
        assert((ae1 != null) && (ae2 != null));
        boolean needCmp = ae1.type.value.nextOf().isintegral();
        {
            int __key860 = 0;
            int __limit861 = (int)len;
            for (; (__key860 < __limit861);__key860 += 1) {
                int i = __key860;
                Expression ee1 = (ae1.elements.value.get()).get((int)(lo1 + (long)i));
                Expression ee2 = (ae2.elements.value.get()).get((int)(lo2 + (long)i));
                if (needCmp)
                {
                    long c = (long)(ee1.toInteger() - ee2.toInteger());
                    if ((c > 0L))
                        return 1;
                    if ((c < 0L))
                        return -1;
                }
                else
                {
                    if (ctfeRawCmp(loc, ee1, ee2, false) != 0)
                        return 1;
                }
            }
        }
        return 0;
    }

    public static FuncDeclaration funcptrOf(Expression e) {
        assert(((e.type.value.ty.value & 0xFF) == ENUMTY.Tdelegate));
        if (((e.op.value & 0xFF) == 160))
            return ((DelegateExp)e).func.value;
        if (((e.op.value & 0xFF) == 161))
            return ((FuncExp)e).fd.value;
        assert(((e.op.value & 0xFF) == 13));
        return null;
    }

    public static boolean isArray(Expression e) {
        return ((e.op.value & 0xFF) == 47) || ((e.op.value & 0xFF) == 121) || ((e.op.value & 0xFF) == 31) || ((e.op.value & 0xFF) == 13);
    }

    public static int ctfeRawCmp(Loc loc, Expression e1, Expression e2, boolean identity) {
        if (((e1.op.value & 0xFF) == 50) || ((e2.op.value & 0xFF) == 50))
        {
            if (((e1.op.value & 0xFF) == 50) && ((e2.op.value & 0xFF) == 50) && (pequals(((ClassReferenceExp)e1).value, ((ClassReferenceExp)e2).value)))
                return 0;
            return 1;
        }
        if (((e1.op.value & 0xFF) == 42) && ((e2.op.value & 0xFF) == 42))
        {
            Type t1 = isType(((TypeidExp)e1).obj.value);
            Type t2 = isType(((TypeidExp)e2).obj.value);
            assert(t1 != null);
            assert(t2 != null);
            return ((!pequals(t1, t2)) ? 1 : 0);
        }
        if (((e1.op.value & 0xFF) == 13) && ((e2.op.value & 0xFF) == 13))
            return 0;
        if (((e1.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e2.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
        {
            Ref<Long> ofs1 = ref(0L);
            Ref<Long> ofs2 = ref(0L);
            Expression agg1 = getAggregateFromPointer(e1, ptr(ofs1));
            Expression agg2 = getAggregateFromPointer(e2, ptr(ofs2));
            if ((pequals(agg1, agg2)) || ((agg1.op.value & 0xFF) == 26) && ((agg2.op.value & 0xFF) == 26) && (pequals(((VarExp)agg1).var.value, ((VarExp)agg2).var.value)))
            {
                if ((ofs1.value == ofs2.value))
                    return 0;
            }
            return 1;
        }
        if (((e1.type.value.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((e2.type.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
        {
            if ((!pequals(funcptrOf(e1), funcptrOf(e2))))
                return 1;
            if (((e1.op.value & 0xFF) == 161) && ((e2.op.value & 0xFF) == 161))
                return 0;
            assert(((e1.op.value & 0xFF) == 160) && ((e2.op.value & 0xFF) == 160));
            Expression ptr1 = ((DelegateExp)e1).e1.value;
            Expression ptr2 = ((DelegateExp)e2).e1.value;
            Ref<Long> ofs1 = ref(0L);
            Ref<Long> ofs2 = ref(0L);
            Expression agg1 = getAggregateFromPointer(ptr1, ptr(ofs1));
            Expression agg2 = getAggregateFromPointer(ptr2, ptr(ofs2));
            if ((pequals(agg1, agg2)) && (ofs1.value == ofs2.value) || ((agg1.op.value & 0xFF) == 26) && ((agg2.op.value & 0xFF) == 26) && (pequals(((VarExp)agg1).var.value, ((VarExp)agg2).var.value)))
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
                    return res;
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
                return (RealIdentical(r1, r2) == 0 ? 1 : 0);
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
        if (((e1.op.value & 0xFF) == 49) && ((e2.op.value & 0xFF) == 49))
        {
            StructLiteralExp es1 = (StructLiteralExp)e1;
            StructLiteralExp es2 = (StructLiteralExp)e2;
            if ((!pequals(es1.sd, es2.sd)))
                return 1;
            else if ((es1.elements.value == null) || ((es1.elements.value.get()).length.value == 0) && (es2.elements.value == null) || ((es2.elements.value.get()).length.value == 0))
                return 0;
            else if ((es1.elements.value == null) || (es2.elements.value == null))
                return 1;
            else if (((es1.elements.value.get()).length.value != (es2.elements.value.get()).length.value))
                return 1;
            else
            {
                {
                    int __key862 = 0;
                    int __limit863 = (es1.elements.value.get()).length.value;
                    for (; (__key862 < __limit863);__key862 += 1) {
                        int i = __key862;
                        Expression ee1 = (es1.elements.value.get()).get(i);
                        Expression ee2 = (es2.elements.value.get()).get(i);
                        if (((ee1.op.value & 0xFF) == 128) && ((ee2.op.value & 0xFF) == 128))
                            continue;
                        if ((pequals(ee1, ee2)))
                            continue;
                        if ((ee1 == null) || (ee2 == null))
                            return 1;
                        int cmp = ctfeRawCmp(loc, ee1, ee2, identity);
                        if (cmp != 0)
                            return 1;
                    }
                }
                return 0;
            }
        }
        if (((e1.op.value & 0xFF) == 48) && ((e2.op.value & 0xFF) == 48))
        {
            AssocArrayLiteralExp es1 = (AssocArrayLiteralExp)e1;
            AssocArrayLiteralExp es2 = (AssocArrayLiteralExp)e2;
            int dim = (es1.keys.value.get()).length.value;
            if (((es2.keys.value.get()).length.value != dim))
                return 1;
            Ptr<Boolean> used = pcopy(((Ptr<Boolean>)Mem.xmalloc(1 * dim)));
            memset(used, 0, 1 * dim);
            {
                int __key864 = 0;
                int __limit865 = dim;
                for (; (__key864 < __limit865);__key864 += 1) {
                    int i = __key864;
                    Expression k1 = (es1.keys.value.get()).get(i);
                    Expression v1 = (es1.values.value.get()).get(i);
                    Expression v2 = null;
                    {
                        int __key866 = 0;
                        int __limit867 = dim;
                        for (; (__key866 < __limit867);__key866 += 1) {
                            int j = __key866;
                            if (used.get(j))
                                continue;
                            Expression k2 = (es2.keys.value.get()).get(j);
                            if (ctfeRawCmp(loc, k1, k2, identity) != 0)
                                continue;
                            used.set(j, true);
                            v2 = (es2.values.value.get()).get(j);
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

    public static int ctfeEqual(Loc loc, byte op, Expression e1, Expression e2) {
        return ((ctfeRawCmp(loc, e1, e2, false) == 0 ^ ((op & 0xFF) == 59)) ? 1 : 0);
    }

    public static int ctfeIdentity(Loc loc, byte op, Expression e1, Expression e2) {
        int cmp = 0;
        if (((e1.op.value & 0xFF) == 13))
        {
            cmp = (((e2.op.value & 0xFF) == 13) ? 1 : 0);
        }
        else if (((e2.op.value & 0xFF) == 13))
        {
            cmp = 0;
        }
        else if (((e1.op.value & 0xFF) == 25) && ((e2.op.value & 0xFF) == 25))
        {
            SymOffExp es1 = (SymOffExp)e1;
            SymOffExp es2 = (SymOffExp)e2;
            cmp = (((pequals(es1.var.value, es2.var.value)) && (es1.offset.value == es2.offset.value)) ? 1 : 0);
        }
        else if (e1.type.value.isreal())
            cmp = RealIdentical(e1.toReal(), e2.toReal());
        else if (e1.type.value.isimaginary())
            cmp = RealIdentical(e1.toImaginary(), e2.toImaginary());
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
            cmp ^= 1;
        return cmp;
    }

    public static int ctfeCmp(Loc loc, byte op, Expression e1, Expression e2) {
        Type t1 = e1.type.value.toBasetype();
        Type t2 = e2.type.value.toBasetype();
        if (t1.isString() && t2.isString())
            return specificCmp(op, ctfeRawCmp(loc, e1, e2, false));
        else if (t1.isreal())
            return realCmp(op, e1.toReal(), e2.toReal());
        else if (t1.isimaginary())
            return realCmp(op, e1.toImaginary(), e2.toImaginary());
        else if (t1.isunsigned() || t2.isunsigned())
            return intUnsignedCmp(op, e1.toInteger(), e2.toInteger());
        else
            return intSignedCmp(op, (long)e1.toInteger(), (long)e2.toInteger());
    }

    public static UnionExp ctfeCat(Loc loc, Type type, Expression e1, Expression e2) {
        Type t1 = e1.type.value.toBasetype();
        Type t2 = e2.type.value.toBasetype();
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (((e2.op.value & 0xFF) == 121) && ((e1.op.value & 0xFF) == 47) && t1.nextOf().isintegral())
        {
            StringExp es1 = (StringExp)e2;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e1;
            int len = es1.len.value + (es2.elements.value.get()).length.value;
            byte sz = es1.sz.value;
            Object s = pcopy(Mem.xmalloc((len + 1) * (sz & 0xFF)));
            memcpy((BytePtr)((((BytePtr)s).plus(((sz & 0xFF) * (es2.elements.value.get()).length.value)))), (es1.string.value), (es1.len.value * (sz & 0xFF)));
            {
                int __key868 = 0;
                int __limit869 = (es2.elements.value.get()).length.value;
                for (; (__key868 < __limit869);__key868 += 1) {
                    int i = __key868;
                    Expression es2e = (es2.elements.value.get()).get(i);
                    if (((es2e.op.value & 0xFF) != 135))
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
            StringExp es = (StringExp)ue.value.exp();
            es.sz.value = sz;
            es.committed.value = (byte)0;
            es.type.value = type;
            return ue.value;
        }
        if (((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 47) && t2.nextOf().isintegral())
        {
            StringExp es1 = (StringExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            int len = es1.len.value + (es2.elements.value.get()).length.value;
            byte sz = es1.sz.value;
            Object s = pcopy(Mem.xmalloc((len + 1) * (sz & 0xFF)));
            memcpy((BytePtr)s, (es1.string.value), (es1.len.value * (sz & 0xFF)));
            {
                int __key870 = 0;
                int __limit871 = (es2.elements.value.get()).length.value;
                for (; (__key870 < __limit871);__key870 += 1) {
                    int i = __key870;
                    Expression es2e = (es2.elements.value.get()).get(i);
                    if (((es2e.op.value & 0xFF) != 135))
                    {
                        ptr(ue) = new UnionExp(new CTFEExp(TOK.cantExpression));
                        return ue.value;
                    }
                    long v = es2e.toInteger();
                    Port.valcpy((((BytePtr)s).plus(((es1.len.value + i) * (sz & 0xFF)))), v, (sz & 0xFF));
                }
            }
            memset((((BytePtr)s).plus((len * (sz & 0xFF)))), 0, (sz & 0xFF));
            ptr(ue) = new UnionExp(new StringExp(loc, s, len));
            StringExp es = (StringExp)ue.value.exp();
            es.sz.value = sz;
            es.committed.value = (byte)0;
            es.type.value = type;
            return ue.value;
        }
        if (((e1.op.value & 0xFF) == 47) && ((e2.op.value & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            ArrayLiteralExp es1 = (ArrayLiteralExp)e1;
            ArrayLiteralExp es2 = (ArrayLiteralExp)e2;
            ptr(ue) = new UnionExp(new ArrayLiteralExp(es1.loc.value, type, copyLiteralArray(es1.elements.value, null)));
            es1 = (ArrayLiteralExp)ue.value.exp();
            (es1.elements.value.get()).insert((es1.elements.value.get()).length.value, copyLiteralArray(es2.elements.value, null));
            return ue.value;
        }
        if (((e1.op.value & 0xFF) == 47) && ((e2.op.value & 0xFF) == 13) && t1.nextOf().equals(t2.nextOf()))
        {
            ue.value = paintTypeOntoLiteralCopy(type, copyLiteral(e1).copy()).copy();
            return ue.value;
        }
        if (((e1.op.value & 0xFF) == 13) && ((e2.op.value & 0xFF) == 47) && t1.nextOf().equals(t2.nextOf()))
        {
            ue.value = paintTypeOntoLiteralCopy(type, copyLiteral(e2).copy()).copy();
            return ue.value;
        }
        ue.value = Cat(type, e1, e2).copy();
        return ue.value;
    }

    public static Expression findKeyInAA(Loc loc, AssocArrayLiteralExp ae, Expression e2) {
        {
            int i = (ae.keys.value.get()).length.value;
            for (; i != 0;){
                i -= 1;
                Expression ekey = (ae.keys.value.get()).get(i);
                int eq = ctfeEqual(loc, TOK.equal, ekey, e2);
                if (eq != 0)
                {
                    return (ae.values.value.get()).get(i);
                }
            }
        }
        return null;
    }

    public static Expression ctfeIndex(Loc loc, Type type, Expression e1, long indx) {
        assert(e1.type.value != null);
        if (((e1.op.value & 0xFF) == 121))
        {
            StringExp es1 = (StringExp)e1;
            if ((indx >= (long)es1.len.value))
            {
                error(loc, new BytePtr("string index %llu is out of bounds `[0 .. %llu]`"), indx, (long)es1.len.value);
                return CTFEExp.cantexp.value;
            }
            return new IntegerExp(loc, (long)es1.charAt(indx), type);
        }
        assert(((e1.op.value & 0xFF) == 47));
        {
            ArrayLiteralExp ale = (ArrayLiteralExp)e1;
            if ((indx >= (long)(ale.elements.value.get()).length.value))
            {
                error(loc, new BytePtr("array index %llu is out of bounds `%s[0 .. %llu]`"), indx, e1.toChars(), (long)(ale.elements.value.get()).length.value);
                return CTFEExp.cantexp.value;
            }
            Expression e = (ale.elements.value.get()).get((int)indx);
            return paintTypeOntoLiteral(type, e);
        }
    }

    public static Expression ctfeCast(Ptr<UnionExp> pue, Loc loc, Type type, Type to, Expression e) {
        Ref<Ptr<UnionExp>> pue_ref = ref(pue);
        Ref<Type> to_ref = ref(to);
        Ref<Expression> e_ref = ref(e);
        Function0<Expression> paint = new Function0<Expression>(){
            public Expression invoke() {
                return paintTypeOntoLiteral(pue_ref.value, to_ref.value, e_ref.value);
            }
        };
        if (((e_ref.value.op.value & 0xFF) == 13))
            return paint.invoke();
        if (((e_ref.value.op.value & 0xFF) == 50))
        {
            ClassDeclaration originalClass = ((ClassReferenceExp)e_ref.value).originalClass();
            if (originalClass.type.value.implicitConvTo(to_ref.value.mutableOf()) != 0)
                return paint.invoke();
            else
            {
                (pue_ref.value) = new UnionExp(new NullExp(loc, to_ref.value));
                return (pue_ref.value.get()).exp();
            }
        }
        if (isTypeInfo_Class(e_ref.value.type.value) && (e_ref.value.type.value.implicitConvTo(to_ref.value) != 0))
            return paint.invoke();
        if (((e_ref.value.op.value & 0xFF) == 49) && (pequals(e_ref.value.type.value.toBasetype().castMod((byte)0), to_ref.value.toBasetype().castMod((byte)0))))
            return paint.invoke();
        Expression r = null;
        if (e_ref.value.type.value.equals(type) && type.equals(to_ref.value))
        {
            r = e_ref.value;
        }
        else if (((to_ref.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray) && ((type.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray) && (to_ref.value.toBasetype().nextOf().size() == type.toBasetype().nextOf().size()))
        {
            return paint.invoke();
        }
        else
        {
            pue_ref.value.set(0, Cast(loc, type, to_ref.value, e_ref.value));
            r = (pue_ref.value.get()).exp();
        }
        if (CTFEExp.isCantExp(r))
            error(loc, new BytePtr("cannot cast `%s` to `%s` at compile time"), e_ref.value.toChars(), to_ref.value.toChars());
        {
            ArrayLiteralExp ae = e_ref.value.isArrayLiteralExp();
            if ((ae) != null)
                ae.ownedByCtfe = OwnedBy.ctfe;
        }
        {
            StringExp se = e_ref.value.isStringExp();
            if ((se) != null)
                se.ownedByCtfe = OwnedBy.ctfe;
        }
        return r;
    }

    public static void assignInPlace(Expression dest, Expression src) {
        assert(((dest.op.value & 0xFF) == 49) || ((dest.op.value & 0xFF) == 47) || ((dest.op.value & 0xFF) == 121));
        Ptr<DArray<Expression>> oldelems = null;
        Ptr<DArray<Expression>> newelems = null;
        if (((dest.op.value & 0xFF) == 49))
        {
            assert(((dest.op.value & 0xFF) == (src.op.value & 0xFF)));
            oldelems = ((StructLiteralExp)dest).elements.value;
            newelems = ((StructLiteralExp)src).elements.value;
            StructDeclaration sd = ((StructLiteralExp)dest).sd;
            int nfields = sd.nonHiddenFields();
            int nvthis = sd.fields.length.value - nfields;
            if ((nvthis != 0) && ((oldelems.get()).length.value >= nfields) && ((oldelems.get()).length.value < (newelems.get()).length.value))
            {
                int __key872 = 0;
                int __limit873 = (newelems.get()).length.value - (oldelems.get()).length.value;
                for (; (__key872 < __limit873);__key872 += 1) {
                    int __ = __key872;
                    (oldelems.get()).push(null);
                }
            }
        }
        else if (((dest.op.value & 0xFF) == 47) && ((src.op.value & 0xFF) == 47))
        {
            oldelems = ((ArrayLiteralExp)dest).elements.value;
            newelems = ((ArrayLiteralExp)src).elements.value;
        }
        else if (((dest.op.value & 0xFF) == 121) && ((src.op.value & 0xFF) == 121))
        {
            sliceAssignStringFromString((StringExp)dest, (StringExp)src, 0);
            return ;
        }
        else if (((dest.op.value & 0xFF) == 47) && ((src.op.value & 0xFF) == 121))
        {
            sliceAssignArrayLiteralFromString((ArrayLiteralExp)dest, (StringExp)src, 0);
            return ;
        }
        else if (((src.op.value & 0xFF) == 47) && ((dest.op.value & 0xFF) == 121))
        {
            sliceAssignStringFromArrayLiteral((StringExp)dest, (ArrayLiteralExp)src, 0);
            return ;
        }
        else
            throw new AssertionError("Unreachable code!");
        assert(((oldelems.get()).length.value == (newelems.get()).length.value));
        {
            int __key874 = 0;
            int __limit875 = (oldelems.get()).length.value;
            for (; (__key874 < __limit875);__key874 += 1) {
                int i = __key874;
                Expression e = (newelems.get()).get(i);
                Expression o = (oldelems.get()).get(i);
                if (((e.op.value & 0xFF) == 49))
                {
                    assert(((o.op.value & 0xFF) == (e.op.value & 0xFF)));
                    assignInPlace(o, e);
                }
                else if (((e.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((e.op.value & 0xFF) != 128) && ((o.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    assignInPlace(o, e);
                }
                else
                {
                    oldelems.get().set(i, (newelems.get()).get(i));
                }
            }
        }
    }

    public static Expression assignAssocArrayElement(Loc loc, AssocArrayLiteralExp aae, Expression index, Expression newval) {
        Ptr<DArray<Expression>> keysx = aae.keys.value;
        Ptr<DArray<Expression>> valuesx = aae.values.value;
        int updated = 0;
        {
            int j = (valuesx.get()).length.value;
            for (; j != 0;){
                j--;
                Expression ekey = (aae.keys.value.get()).get(j);
                int eq = ctfeEqual(loc, TOK.equal, ekey, index);
                if (eq != 0)
                {
                    valuesx.get().set(j, newval);
                    updated = 1;
                }
            }
        }
        if (updated == 0)
        {
            (valuesx.get()).push(newval);
            (keysx.get()).push(index);
        }
        return newval;
    }

    public static UnionExp changeArrayLiteralLength(Loc loc, TypeArray arrayType, Expression oldval, int oldlen, int newlen) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        Type elemType = arrayType.next.value;
        assert(elemType != null);
        Expression defaultElem = elemType.defaultInitLiteral(loc);
        Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(newlen));
        int indxlo = 0;
        if (((oldval.op.value & 0xFF) == 31))
        {
            indxlo = (int)((SliceExp)oldval).lwr.value.toInteger();
            oldval = ((SliceExp)oldval).e1.value;
        }
        int copylen = (oldlen < newlen) ? oldlen : newlen;
        if (((oldval.op.value & 0xFF) == 121))
        {
            StringExp oldse = (StringExp)oldval;
            Object s = pcopy(Mem.xcalloc(newlen + 1, (oldse.sz.value & 0xFF)));
            memcpy((BytePtr)s, (oldse.string.value), (copylen * (oldse.sz.value & 0xFF)));
            int defaultValue = (int)defaultElem.toInteger();
            {
                int __key876 = copylen;
                int __limit877 = newlen;
                for (; (__key876 < __limit877);__key876 += 1) {
                    int elemi = __key876;
                    switch ((oldse.sz.value & 0xFF))
                    {
                        case 1:
                            (((BytePtr)s)).set((indxlo + elemi), (byte)defaultValue);
                            break;
                        case 2:
                            (((CharPtr)s)).set((indxlo + elemi), (char)defaultValue);
                            break;
                        case 4:
                            (((IntPtr)s)).set((indxlo + elemi), defaultValue);
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
            ptr(ue) = new UnionExp(new StringExp(loc, s, newlen));
            StringExp se = (StringExp)ue.value.exp();
            se.type.value = arrayType;
            se.sz.value = oldse.sz.value;
            se.committed.value = oldse.committed.value;
            se.ownedByCtfe = OwnedBy.ctfe;
        }
        else
        {
            if ((oldlen != 0))
            {
                assert(((oldval.op.value & 0xFF) == 47));
                ArrayLiteralExp ae = (ArrayLiteralExp)oldval;
                {
                    int __key878 = 0;
                    int __limit879 = copylen;
                    for (; (__key878 < __limit879);__key878 += 1) {
                        int i = __key878;
                        elements.get().set(i, (ae.elements.value.get()).get(indxlo + i));
                    }
                }
            }
            if (((elemType.ty.value & 0xFF) == ENUMTY.Tstruct) || ((elemType.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                {
                    int __key880 = copylen;
                    int __limit881 = newlen;
                    for (; (__key880 < __limit881);__key880 += 1) {
                        int i = __key880;
                        elements.get().set(i, copyLiteral(defaultElem).copy());
                    }
                }
            }
            else
            {
                {
                    int __key882 = copylen;
                    int __limit883 = newlen;
                    for (; (__key882 < __limit883);__key882 += 1) {
                        int i = __key882;
                        elements.get().set(i, defaultElem);
                    }
                }
            }
            ptr(ue) = new UnionExp(new ArrayLiteralExp(loc, arrayType, elements));
            ArrayLiteralExp aae = (ArrayLiteralExp)ue.value.exp();
            aae.ownedByCtfe = OwnedBy.ctfe;
        }
        return ue.value;
    }

    public static boolean isCtfeValueValid(Expression newval) {
        Type tb = newval.type.value.toBasetype();
        if (((newval.op.value & 0xFF) == 135) || ((newval.op.value & 0xFF) == 140) || ((newval.op.value & 0xFF) == 148) || ((newval.op.value & 0xFF) == 147))
        {
            return tb.isscalar();
        }
        if (((newval.op.value & 0xFF) == 13))
        {
            return ((tb.ty.value & 0xFF) == ENUMTY.Tnull) || ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) || ((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Taarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tclass) || ((tb.ty.value & 0xFF) == ENUMTY.Tdelegate);
        }
        if (((newval.op.value & 0xFF) == 121))
            return true;
        if (((newval.op.value & 0xFF) == 47))
            return true;
        if (((newval.op.value & 0xFF) == 48))
            return true;
        if (((newval.op.value & 0xFF) == 49))
            return true;
        if (((newval.op.value & 0xFF) == 50))
            return true;
        if (((newval.op.value & 0xFF) == 229))
            return true;
        if (((newval.op.value & 0xFF) == 161))
            return true;
        if (((newval.op.value & 0xFF) == 160))
        {
            Expression ethis = ((DelegateExp)newval).e1.value;
            return ((ethis.op.value & 0xFF) == 49) || ((ethis.op.value & 0xFF) == 50) || ((ethis.op.value & 0xFF) == 26) && (pequals(((VarExp)ethis).var.value, ((DelegateExp)newval).func.value));
        }
        if (((newval.op.value & 0xFF) == 25))
        {
            Declaration d = ((SymOffExp)newval).var.value;
            return (d.isFuncDeclaration() != null) || d.isDataseg();
        }
        if (((newval.op.value & 0xFF) == 42))
        {
            return true;
        }
        if (((newval.op.value & 0xFF) == 19))
        {
            Expression e1 = ((AddrExp)newval).e1.value;
            return ((tb.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e1.op.value & 0xFF) == 49) || ((e1.op.value & 0xFF) == 47) && isCtfeValueValid(e1) || ((e1.op.value & 0xFF) == 26) || ((e1.op.value & 0xFF) == 27) && isCtfeReferenceValid(e1) || ((e1.op.value & 0xFF) == 62) && isCtfeReferenceValid(e1) || ((e1.op.value & 0xFF) == 31) && ((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray);
        }
        if (((newval.op.value & 0xFF) == 31))
        {
            SliceExp se = (SliceExp)newval;
            assert((se.lwr.value != null) && ((se.lwr.value.op.value & 0xFF) == 135));
            assert((se.upr.value != null) && ((se.upr.value.op.value & 0xFF) == 135));
            return ((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray) && ((se.e1.value.op.value & 0xFF) == 121) || ((se.e1.value.op.value & 0xFF) == 47);
        }
        if (((newval.op.value & 0xFF) == 128))
            return true;
        newval.error(new BytePtr("CTFE internal error: illegal CTFE value `%s`"), newval.toChars());
        return false;
    }

    public static boolean isCtfeReferenceValid(Expression newval) {
        if (((newval.op.value & 0xFF) == 123))
            return true;
        if (((newval.op.value & 0xFF) == 26))
        {
            VarDeclaration v = ((VarExp)newval).var.value.isVarDeclaration();
            assert(v != null);
            return true;
        }
        if (((newval.op.value & 0xFF) == 62))
        {
            Expression eagg = ((IndexExp)newval).e1.value;
            return ((eagg.op.value & 0xFF) == 121) || ((eagg.op.value & 0xFF) == 47) || ((eagg.op.value & 0xFF) == 48);
        }
        if (((newval.op.value & 0xFF) == 27))
        {
            Expression eagg = ((DotVarExp)newval).e1.value;
            return ((eagg.op.value & 0xFF) == 49) || ((eagg.op.value & 0xFF) == 50) && isCtfeValueValid(eagg);
        }
        return isCtfeValueValid(newval);
    }

    public static void showCtfeExpr(Expression e, int level) {
        {
            int i = level;
            for (; (i > 0);i -= 1) {
                printf(new BytePtr(" "));
            }
        }
        Ptr<DArray<Expression>> elements = null;
        StructDeclaration sd = null;
        ClassDeclaration cd = null;
        if (((e.op.value & 0xFF) == 49))
        {
            elements = ((StructLiteralExp)e).elements.value;
            sd = ((StructLiteralExp)e).sd;
            printf(new BytePtr("STRUCT type = %s %p:\n"), e.type.value.toChars(), e);
        }
        else if (((e.op.value & 0xFF) == 50))
        {
            elements = ((ClassReferenceExp)e).value.elements.value;
            cd = ((ClassReferenceExp)e).originalClass();
            printf(new BytePtr("CLASS type = %s %p:\n"), e.type.value.toChars(), ((ClassReferenceExp)e).value);
        }
        else if (((e.op.value & 0xFF) == 47))
        {
            elements = ((ArrayLiteralExp)e).elements.value;
            printf(new BytePtr("ARRAY LITERAL type=%s %p:\n"), e.type.value.toChars(), e);
        }
        else if (((e.op.value & 0xFF) == 48))
        {
            printf(new BytePtr("AA LITERAL type=%s %p:\n"), e.type.value.toChars(), e);
        }
        else if (((e.op.value & 0xFF) == 121))
        {
            printf(new BytePtr("STRING %s %p\n"), e.toChars(), ((StringExp)e).string.value);
        }
        else if (((e.op.value & 0xFF) == 31))
        {
            printf(new BytePtr("SLICE %p: %s\n"), e, e.toChars());
            showCtfeExpr(((SliceExp)e).e1.value, level + 1);
        }
        else if (((e.op.value & 0xFF) == 26))
        {
            printf(new BytePtr("VAR %p %s\n"), e, e.toChars());
            VarDeclaration v = ((VarExp)e).var.value.isVarDeclaration();
            if ((v != null) && (getValue(v) != null))
                showCtfeExpr(getValue(v), level + 1);
        }
        else if (((e.op.value & 0xFF) == 19))
        {
            printf(new BytePtr("POINTER %p to %p: %s\n"), e, ((AddrExp)e).e1.value, e.toChars());
        }
        else
            printf(new BytePtr("VALUE %p: %s\n"), e, e.toChars());
        if (elements != null)
        {
            int fieldsSoFar = 0;
            {
                int i = 0;
                for (; (i < (elements.get()).length.value);i++){
                    Expression z = null;
                    VarDeclaration v = null;
                    if ((i > 15))
                    {
                        printf(new BytePtr("...(total %d elements)\n"), (elements.get()).length.value);
                        return ;
                    }
                    if (sd != null)
                    {
                        v = sd.fields.get(i);
                        z = (elements.get()).get(i);
                    }
                    else if (cd != null)
                    {
                        for (; (i - fieldsSoFar >= cd.fields.length.value);){
                            fieldsSoFar += cd.fields.length.value;
                            cd = cd.baseClass.value;
                            {
                                int j = level;
                                for (; (j > 0);j -= 1) {
                                    printf(new BytePtr(" "));
                                }
                            }
                            printf(new BytePtr(" BASE CLASS: %s\n"), cd.toChars());
                        }
                        v = cd.fields.get(i - fieldsSoFar);
                        assert(((elements.get()).length.value + i >= fieldsSoFar + cd.fields.length.value));
                        int indx = (elements.get()).length.value - fieldsSoFar - cd.fields.length.value + i;
                        assert((indx < (elements.get()).length.value));
                        z = (elements.get()).get(indx);
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
                        if (((v.type.value.ty.value & 0xFF) != (z.type.value.ty.value & 0xFF)) && ((v.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
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
        return showCtfeExpr(e, 0);
    }

    public static UnionExp voidInitLiteral(Type t, VarDeclaration var) {
        Ref<UnionExp> ue = ref(new UnionExp().copy());
        if (((t.ty.value & 0xFF) == ENUMTY.Tsarray))
        {
            TypeSArray tsa = (TypeSArray)t;
            Expression elem = voidInitLiteral(tsa.next.value, var).copy();
            boolean mustCopy = ((elem.op.value & 0xFF) == 47) || ((elem.op.value & 0xFF) == 49);
            int d = (int)tsa.dim.value.toInteger();
            Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(d));
            {
                int __key884 = 0;
                int __limit885 = d;
                for (; (__key884 < __limit885);__key884 += 1) {
                    int i = __key884;
                    if (mustCopy && (i > 0))
                        elem = copyLiteral(elem).copy();
                    elements.get().set(i, elem);
                }
            }
            ptr(ue) = new UnionExp(new ArrayLiteralExp(var.loc.value, tsa, elements));
            ArrayLiteralExp ae = (ArrayLiteralExp)ue.value.exp();
            ae.ownedByCtfe = OwnedBy.ctfe;
        }
        else if (((t.ty.value & 0xFF) == ENUMTY.Tstruct))
        {
            TypeStruct ts = (TypeStruct)t;
            Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>(ts.sym.value.fields.length.value));
            {
                int __key886 = 0;
                int __limit887 = ts.sym.value.fields.length.value;
                for (; (__key886 < __limit887);__key886 += 1) {
                    int i = __key886;
                    exps.get().set(i, voidInitLiteral(ts.sym.value.fields.get(i).type.value, ts.sym.value.fields.get(i)).copy());
                }
            }
            ptr(ue) = new UnionExp(new StructLiteralExp(var.loc.value, ts.sym.value, exps));
            StructLiteralExp se = (StructLiteralExp)ue.value.exp();
            se.type.value = ts;
            se.ownedByCtfe.value = OwnedBy.ctfe;
        }
        else
            ptr(ue) = new UnionExp(new VoidInitExp(var));
        return ue.value;
    }

}
