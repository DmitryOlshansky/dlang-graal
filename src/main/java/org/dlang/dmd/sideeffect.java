package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.apply.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class sideeffect {
    private static class IsTrivialExp extends StoppableVisitor
    {
        public  IsTrivialExp() {
            super();
        }

        public  void visit(Expression e) {
            if (((e.op & 0xFF) == 18))
            {
                this.stop = true;
                return ;
            }
            this.stop = lambdaHasSideEffect(e);
        }

    }
    private static class LambdaHasSideEffect extends StoppableVisitor
    {
        public  LambdaHasSideEffect() {
            super();
        }

        public  void visit(Expression e) {
            this.stop = lambdaHasSideEffect(e);
        }

    }

    public static boolean isTrivialExp(Expression e) {
        IsTrivialExp v = new IsTrivialExp();
        return (walkPostorder(e, v) ? 1 : 0) == 0;
    }

    public static boolean hasSideEffect(Expression e) {
        LambdaHasSideEffect v = new LambdaHasSideEffect();
        return walkPostorder(e, v);
    }

    public static int callSideEffectLevel(FuncDeclaration f) {
        if (f.isCtorDeclaration() != null)
            return 0;
        assert(((f.type.ty & 0xFF) == ENUMTY.Tfunction));
        TypeFunction tf = (TypeFunction)f.type;
        if (tf.isnothrow)
        {
            int purity = f.isPure();
            if ((purity == PURE.strong))
                return 2;
            if ((purity == PURE.const_))
                return 1;
        }
        return 0;
    }

    public static int callSideEffectLevel(Type t) {
        t = t.toBasetype();
        TypeFunction tf = null;
        if (((t.ty & 0xFF) == ENUMTY.Tdelegate))
            tf = (TypeFunction)((TypeDelegate)t).next;
        else
        {
            assert(((t.ty & 0xFF) == ENUMTY.Tfunction));
            tf = (TypeFunction)t;
        }
        if (!tf.isnothrow)
            return 0;
        tf.purityLevel();
        int purity = tf.purity;
        if (((t.ty & 0xFF) == ENUMTY.Tdelegate) && (purity > PURE.weak))
        {
            if (tf.isMutable())
                purity = PURE.weak;
            else if (!tf.isImmutable())
                purity = PURE.const_;
        }
        if ((purity == PURE.strong))
            return 2;
        if ((purity == PURE.const_))
            return 1;
        return 0;
    }

    public static boolean lambdaHasSideEffect(Expression e) {
        switch ((e.op & 0xFF))
        {
            case 90:
            case 93:
            case 94:
            case 38:
            case 95:
            case 96:
            case 76:
            case 77:
            case 71:
            case 72:
            case 73:
            case 81:
            case 82:
            case 83:
            case 66:
            case 67:
            case 69:
            case 87:
            case 88:
            case 89:
            case 227:
            case 175:
            case 44:
            case 14:
            case 125:
            case 23:
            case 22:
            case 45:
                return true;
            case 18:
                CallExp ce = (CallExp)e;
                if (ce.e1.type != null)
                {
                    Type t = ce.e1.type.toBasetype();
                    if (((t.ty & 0xFF) == ENUMTY.Tdelegate))
                        t = ((TypeDelegate)t).next;
                    if (((t.ty & 0xFF) == ENUMTY.Tfunction) && ((ce.f != null ? callSideEffectLevel(ce.f) : callSideEffectLevel(ce.e1.type)) > 0))
                    {
                    }
                    else
                        return true;
                }
                break;
            case 12:
                CastExp ce_1 = (CastExp)e;
                if (((ce_1.to.ty & 0xFF) == ENUMTY.Tclass) && ((ce_1.e1.op & 0xFF) == 18) && ((ce_1.e1.type.ty & 0xFF) == ENUMTY.Tclass))
                    return true;
                break;
            default:
            break;
        }
        return false;
    }

    public static boolean discardValue(Expression e) {
        if (lambdaHasSideEffect(e))
            return false;
        switch ((e.op & 0xFF))
        {
            case 12:
                CastExp ce = (CastExp)e;
                if (ce.to.equals(Type.tvoid))
                {
                    return false;
                }
                break;
            case 127:
                return false;
            case 26:
                VarDeclaration v = ((VarExp)e).var.isVarDeclaration();
                if ((v != null) && ((v.storage_class & 1099511627776L) != 0))
                {
                    return false;
                }
                break;
            case 18:
                if (((global.params.warnings & 0xFF) != 2) && (global.gag == 0))
                {
                    CallExp ce_1 = (CallExp)e;
                    if (((e.type.ty & 0xFF) == ENUMTY.Tvoid))
                    {
                    }
                    else if (ce_1.e1.type != null)
                    {
                        Type t = ce_1.e1.type.toBasetype();
                        if (((t.ty & 0xFF) == ENUMTY.Tdelegate))
                            t = ((TypeDelegate)t).next;
                        if (((t.ty & 0xFF) == ENUMTY.Tfunction) && ((ce_1.f != null ? callSideEffectLevel(ce_1.f) : callSideEffectLevel(ce_1.e1.type)) > 0))
                        {
                            BytePtr s = null;
                            if (ce_1.f != null)
                                s = pcopy(ce_1.f.toPrettyChars(false));
                            else if (((ce_1.e1.op & 0xFF) == 24))
                            {
                                s = pcopy(((PtrExp)ce_1.e1).e1.toChars());
                            }
                            else
                                s = pcopy(ce_1.e1.toChars());
                            e.warning(new BytePtr("calling %s without side effects discards return value of type %s, prepend a cast(void) if intentional"), s, e.type.toChars());
                        }
                    }
                }
                return false;
            case 101:
            case 102:
                LogicalExp aae = (LogicalExp)e;
                return discardValue(aae.e2);
            case 100:
                CondExp ce_2 = (CondExp)e;
                if (!lambdaHasSideEffect(ce_2.e1) && !lambdaHasSideEffect(ce_2.e2))
                {
                    return discardValue(ce_2.e1) | discardValue(ce_2.e2);
                }
                return false;
            case 99:
                CommaExp ce_3 = (CommaExp)e;
                Expression fc = firstComma(ce_3);
                if (((fc.op & 0xFF) == 38) && ((ce_3.e2.op & 0xFF) == 26) && (pequals(((DeclarationExp)fc).declaration, ((VarExp)ce_3.e2).var)))
                {
                    return false;
                }
                return discardValue(ce_3.e2);
            case 126:
                if (!hasSideEffect(e))
                    break;
                return false;
            default:
            break;
        }
        e.error(new BytePtr("`%s` has no effect"), e.toChars());
        return true;
    }

    public static VarDeclaration copyToTemp(long stc, BytePtr name, Expression e) {
        assert(((name.get(0) & 0xFF) == 95) && ((name.get(1) & 0xFF) == 95));
        VarDeclaration vd = new VarDeclaration(e.loc, e.type, Identifier.generateId(name), new ExpInitializer(e.loc, e), 0L);
        vd.storage_class = stc | 1099511627776L | 68719476736L;
        return vd;
    }

    public static Expression extractSideEffect(Scope sc, BytePtr name, Ref<Expression> e0, Expression e, boolean alwaysCopy) {
        if (!alwaysCopy && isTrivialExp(e))
            return e;
        VarDeclaration vd = copyToTemp(0L, name, e);
        vd.storage_class |= e.isLvalue() ? 2097152L : 2199023255552L;
        e0.value = Expression.combine(e0.value, expressionSemantic(new DeclarationExp(vd.loc, vd), sc));
        return expressionSemantic(new VarExp(vd.loc, vd, true), sc);
    }

}
