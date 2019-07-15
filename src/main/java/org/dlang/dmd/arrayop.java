package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class arrayop {
    static TemplateDeclaration arrayOparrayOp = null;
    private static class BuildArrayOpVisitor extends Visitor
    {
        private Ptr<Scope> sc = null;
        private Ptr<DArray<RootObject>> tiargs = null;
        private Ptr<DArray<Expression>> args = null;
        // Erasure: __ctor<Ptr, Ptr, Ptr>
        public  BuildArrayOpVisitor(Ptr<Scope> sc, Ptr<DArray<RootObject>> tiargs, Ptr<DArray<Expression>> args) {
            this.sc = pcopy(sc);
            this.tiargs = pcopy(tiargs);
            this.args = pcopy(args);
        }

        // Erasure: visit<Expression>
        public  void visit(Expression e) {
            (this.tiargs.get()).push(e.type.value);
            (this.args.get()).push(e);
        }

        // Erasure: visit<SliceExp>
        public  void visit(SliceExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<CastExp>
        public  void visit(CastExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<UnaExp>
        public  void visit(UnaExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty & 0xFF) != ENUMTY.Tarray) && ((tb.ty & 0xFF) != ENUMTY.Tsarray))
            {
                this.visit((Expression)e);
            }
            else
            {
                OutBuffer buf = new OutBuffer();
                try {
                    buf.writestring(new ByteSlice("u"));
                    buf.writestring(Token.asString(e.op));
                    e.e1.value.accept(this);
                    (this.tiargs.get()).push(expressionSemantic(new StringExp(Loc.initial, buf.extractChars()), this.sc));
                }
                finally {
                }
            }
        }

        // Erasure: visit<BinExp>
        public  void visit(BinExp e) {
            Type tb = e.type.value.toBasetype();
            if (((tb.ty & 0xFF) != ENUMTY.Tarray) && ((tb.ty & 0xFF) != ENUMTY.Tsarray))
            {
                this.visit((Expression)e);
            }
            else
            {
                e.e1.value.accept(this);
                e.e2.value.accept(this);
                (this.tiargs.get()).push(expressionSemantic(new StringExp(Loc.initial, Token.toChars(e.op)), this.sc));
            }
        }


        public BuildArrayOpVisitor() {}
    }

    // Erasure: isArrayOpValid<Expression>
    public static boolean isArrayOpValid(Expression e) {
        if (((e.op & 0xFF) == 31))
        {
            return true;
        }
        if (((e.op & 0xFF) == 47))
        {
            Type t = e.type.value.toBasetype();
            for (; ((t.ty & 0xFF) == ENUMTY.Tarray) || ((t.ty & 0xFF) == ENUMTY.Tsarray);) {
                t = t.nextOf().toBasetype();
            }
            return (t.ty & 0xFF) != ENUMTY.Tvoid;
        }
        Type tb = e.type.value.toBasetype();
        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
        {
            if (isUnaArrayOp(e.op))
            {
                return isArrayOpValid(((UnaExp)e).e1.value);
            }
            if (isBinArrayOp(e.op) || isBinAssignArrayOp(e.op) || ((e.op & 0xFF) == 90))
            {
                BinExp be = (BinExp)e;
                return isArrayOpValid(be.e1.value) && isArrayOpValid(be.e2.value);
            }
            if (((e.op & 0xFF) == 95))
            {
                BinExp be = (BinExp)e;
                return ((be.e1.value.op & 0xFF) == 31) && isArrayOpValid(be.e2.value);
            }
            return false;
        }
        return true;
    }

    // Erasure: isNonAssignmentArrayOp<Expression>
    public static boolean isNonAssignmentArrayOp(Expression e) {
        if (((e.op & 0xFF) == 31))
        {
            return isNonAssignmentArrayOp(((SliceExp)e).e1.value);
        }
        Type tb = e.type.value.toBasetype();
        if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
        {
            return isUnaArrayOp(e.op) || isBinArrayOp(e.op);
        }
        return false;
    }

    // Erasure: checkNonAssignmentArrayOp<Expression, boolean>
    public static boolean checkNonAssignmentArrayOp(Expression e, boolean suggestion) {
        if (isNonAssignmentArrayOp(e))
        {
            BytePtr s = pcopy(new BytePtr(""));
            if (suggestion)
            {
                s = pcopy(new BytePtr(" (possible missing [])"));
            }
            e.error(new BytePtr("array operation `%s` without destination memory not allowed%s"), e.toChars(), s);
            return true;
        }
        return false;
    }

    // defaulted all parameters starting with #2
    public static boolean checkNonAssignmentArrayOp(Expression e) {
        return checkNonAssignmentArrayOp(e, false);
    }

    // Erasure: arrayOp<BinExp, Ptr>
    public static Expression arrayOp(BinExp e, Ptr<Scope> sc) {
        Type tb = e.type.value.toBasetype();
        assert(((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray));
        Type tbn = tb.nextOf().toBasetype();
        if (((tbn.ty & 0xFF) == ENUMTY.Tvoid))
        {
            e.error(new BytePtr("cannot perform array operations on `void[]` arrays"));
            return new ErrorExp();
        }
        if (!isArrayOpValid(e))
        {
            return arrayOpInvalidError(e);
        }
        Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
        Ptr<DArray<Expression>> args = refPtr(new DArray<Expression>());
        buildArrayOp(sc, e, tiargs, args);
        if ((arrayop.arrayOparrayOp == null))
        {
            Expression id = new IdentifierExp(e.loc, Id.empty);
            id = new DotIdExp(e.loc, id, Id.object);
            id = new DotIdExp(e.loc, id, Identifier.idPool(new ByteSlice("_arrayOp")));
            id = expressionSemantic(id, sc);
            if (((id.op & 0xFF) != 36))
            {
                ObjectNotFound(Identifier.idPool(new ByteSlice("_arrayOp")));
            }
            arrayop.arrayOparrayOp = ((TemplateExp)id).td;
        }
        FuncDeclaration fd = resolveFuncCall(e.loc, sc, arrayop.arrayOparrayOp, tiargs, null, args, FuncResolveFlag.standard);
        if ((fd == null) || fd.errors)
        {
            return new ErrorExp();
        }
        return expressionSemantic(new CallExp(e.loc, new VarExp(e.loc, fd, false), args), sc);
    }

    // Erasure: arrayOp<BinAssignExp, Ptr>
    public static Expression arrayOp(BinAssignExp e, Ptr<Scope> sc) {
        Type tn = e.e1.value.type.value.toBasetype().nextOf();
        if ((tn != null) && !tn.isMutable() || !tn.isAssignable())
        {
            e.error(new BytePtr("slice `%s` is not mutable"), e.e1.value.toChars());
            if (((e.op & 0xFF) == 76))
            {
                checkPossibleAddCatErrorAddAssignExpCatAssignExp(e.isAddAssignExp());
            }
            return new ErrorExp();
        }
        if (((e.e1.value.op & 0xFF) == 47))
        {
            return e.e1.value.modifiableLvalue(sc, e.e1.value);
        }
        return arrayOp((BinExp)e, sc);
    }

    // Erasure: buildArrayOp<Ptr, Expression, Ptr, Ptr>
    public static void buildArrayOp(Ptr<Scope> sc, Expression e, Ptr<DArray<RootObject>> tiargs, Ptr<DArray<Expression>> args) {
        // skipping duplicate class BuildArrayOpVisitor
        BuildArrayOpVisitor v = new BuildArrayOpVisitor(sc, tiargs, args);
        e.accept(v);
    }

    // Erasure: isUnaArrayOp<byte>
    public static boolean isUnaArrayOp(byte op) {
        switch ((op & 0xFF))
        {
            case 8:
            case 92:
                return true;
            default:
            break;
        }
        return false;
    }

    // Erasure: isBinArrayOp<byte>
    public static boolean isBinArrayOp(byte op) {
        switch ((op & 0xFF))
        {
            case 74:
            case 75:
            case 78:
            case 79:
            case 80:
            case 86:
            case 84:
            case 85:
            case 226:
                return true;
            default:
            break;
        }
        return false;
    }

    // Erasure: isBinAssignArrayOp<byte>
    public static boolean isBinAssignArrayOp(byte op) {
        switch ((op & 0xFF))
        {
            case 76:
            case 77:
            case 81:
            case 82:
            case 83:
            case 89:
            case 87:
            case 88:
            case 227:
                return true;
            default:
            break;
        }
        return false;
    }

    // Erasure: isArrayOpOperand<Expression>
    public static boolean isArrayOpOperand(Expression e) {
        if (((e.op & 0xFF) == 31))
        {
            return true;
        }
        if (((e.op & 0xFF) == 47))
        {
            Type t = e.type.value.toBasetype();
            for (; ((t.ty & 0xFF) == ENUMTY.Tarray) || ((t.ty & 0xFF) == ENUMTY.Tsarray);) {
                t = t.nextOf().toBasetype();
            }
            return (t.ty & 0xFF) != ENUMTY.Tvoid;
        }
        Type tb = e.type.value.toBasetype();
        if (((tb.ty & 0xFF) == ENUMTY.Tarray))
        {
            return isUnaArrayOp(e.op) || isBinArrayOp(e.op) || isBinAssignArrayOp(e.op) || ((e.op & 0xFF) == 90);
        }
        return false;
    }

    // Erasure: arrayOpInvalidError<Expression>
    public static ErrorExp arrayOpInvalidError(Expression e) {
        e.error(new BytePtr("invalid array operation `%s` (possible missing [])"), e.toChars());
        if (((e.op & 0xFF) == 74))
        {
            checkPossibleAddCatErrorAddExpCatExp(e.isAddExp());
        }
        else if (((e.op & 0xFF) == 76))
        {
            checkPossibleAddCatErrorAddAssignExpCatAssignExp(e.isAddAssignExp());
        }
        return new ErrorExp();
    }

    // from template checkPossibleAddCatError!(AddAssignExpCatAssignExp)
    // Erasure: checkPossibleAddCatErrorAddAssignExpCatAssignExp<AddAssignExp>
    public static void checkPossibleAddCatErrorAddAssignExpCatAssignExp(AddAssignExp ae) {
        if ((ae.e2.value.type.value == null) || ((ae.e2.value.type.value.ty & 0xFF) != ENUMTY.Tarray) || (ae.e2.value.type.value.implicitConvTo(ae.e1.value.type.value) == 0))
        {
            return ;
        }
        CatAssignExp ce = new CatAssignExp(ae.loc, ae.e1.value, ae.e2.value);
        ae.errorSupplemental(new BytePtr("did you mean to concatenate (`%s`) instead ?"), ce.toChars());
    }


    // from template checkPossibleAddCatError!(AddExpCatExp)
    // Erasure: checkPossibleAddCatErrorAddExpCatExp<AddExp>
    public static void checkPossibleAddCatErrorAddExpCatExp(AddExp ae) {
        if ((ae.e2.value.type.value == null) || ((ae.e2.value.type.value.ty & 0xFF) != ENUMTY.Tarray) || (ae.e2.value.type.value.implicitConvTo(ae.e1.value.type.value) == 0))
        {
            return ;
        }
        CatExp ce = new CatExp(ae.loc, ae.e1.value, ae.e2.value);
        ae.errorSupplemental(new BytePtr("did you mean to concatenate (`%s`) instead ?"), ce.toChars());
    }


}
