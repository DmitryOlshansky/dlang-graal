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
import static org.dlang.dmd.apply.*;
import static org.dlang.dmd.arrayop.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.canthrow.*;
import static org.dlang.dmd.complex.*;
import static org.dlang.dmd.constfold.*;
import static org.dlang.dmd.ctfeexpr.*;
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.delegatize.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.inline.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.optimize.*;
import static org.dlang.dmd.safe.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.visitor.*;

public class expression {
    static IntegerExp literaltheConstant = null;
    static IntegerExp literaltheConstant = null;
    static IntegerExp literaltheConstant = null;
    private static class DtorVisitor extends StoppableVisitor
    {
        private Ptr<Scope> sc = null;
        private CondExp ce = null;
        private VarDeclaration vcond = null;
        private boolean isThen = false;
        // Erasure: __ctor<Ptr, CondExp>
        public  DtorVisitor(Ptr<Scope> sc, CondExp ce) {
            super();
            this.sc = pcopy(sc);
            this.ce = ce;
        }

        // Erasure: visit<Expression>
        public  void visit(Expression e) {
        }

        // Erasure: visit<DeclarationExp>
        public  void visit(DeclarationExp e) {
            VarDeclaration v = e.declaration.isVarDeclaration();
            if ((v != null) && !v.isDataseg())
            {
                if (v._init != null)
                {
                    {
                        ExpInitializer ei = v._init.isExpInitializer();
                        if ((ei) != null)
                        {
                            ei.exp.accept(this);
                        }
                    }
                }
                if (v.needsScopeDtor())
                {
                    if (this.vcond == null)
                    {
                        this.vcond = copyToTemp(8796093022208L, new BytePtr("__cond"), this.ce.econd.value);
                        dsymbolSemantic(this.vcond, this.sc);
                        Expression de = new DeclarationExp(this.ce.econd.value.loc, this.vcond);
                        de = expressionSemantic(de, this.sc);
                        Expression ve = new VarExp(this.ce.econd.value.loc, this.vcond, true);
                        this.ce.econd.value = Expression.combine(de, ve);
                    }
                    Expression ve = new VarExp(this.vcond.loc, this.vcond, true);
                    if (this.isThen)
                    {
                        v.edtor = new LogicalExp(v.edtor.loc, TOK.andAnd, ve, v.edtor);
                    }
                    else
                    {
                        v.edtor = new LogicalExp(v.edtor.loc, TOK.orOr, ve, v.edtor);
                    }
                    v.edtor = expressionSemantic(v.edtor, this.sc);
                }
            }
        }


        public DtorVisitor() {}
    }

    static boolean LOGSEMANTIC = false;
    // from template emplaceExp!(AddrExpLocExpression)

    // from template emplaceExp!(AddrExpLocExpression)

    // from template emplaceExp!(AddrExpLocExpressionType)

    // from template emplaceExp!(AddrExpLocIndexExpType)

    // from template emplaceExp!(AddrExpLocSliceExp)

    // from template emplaceExp!(AddrExpLocSliceExpType)

    // from template emplaceExp!(AddrExpLocVarExpType)

    // from template emplaceExp!(ArrayLiteralExpLocObjectPtr<DArray<Expression>>)

    // from template emplaceExp!(ArrayLiteralExpLocTypeArrayPtr<DArray<Expression>>)

    // from template emplaceExp!(ArrayLiteralExpLocTypeExpression)

    // from template emplaceExp!(ArrayLiteralExpLocTypeExpressionPtr<DArray<Expression>>)

    // from template emplaceExp!(ArrayLiteralExpLocTypePtr<DArray<Expression>>)

    // from template emplaceExp!(ArrayLiteralExpLocTypePtr<DArray<Expression>>)

    // from template emplaceExp!(ArrayLiteralExpLocTypeSArrayPtr<DArray<Expression>>)

    // from template emplaceExp!(AssocArrayLiteralExpLocPtr<DArray<Expression>>Ptr<DArray<Expression>>)

    // from template emplaceExp!(CTFEExpByte)

    // from template emplaceExp!(ClassReferenceExpLocStructLiteralExpType)

    // from template emplaceExp!(ComplexExpLoccomplex_tType)

    // from template emplaceExp!(ComplexExpLoccomplex_tType)

    // from template emplaceExp!(DelegateExpLocExpressionFuncDeclarationBoolean)

    // from template emplaceExp!(DotVarExpLocExpressionDeclarationBoolean)

    // from template emplaceExp!(DotVarExpLocExpressionFuncDeclarationBoolean)

    // from template emplaceExp!(DotVarExpLocExpressionVarDeclaration)

    // from template emplaceExp!(ErrorExp)

    // from template emplaceExp!(IndexExpLocExpressionExpression)

    // from template emplaceExp!(IndexExpLocExpressionIntegerExp)

    // from template emplaceExp!(IntegerExpInteger)

    // from template emplaceExp!(IntegerExpLocBooleanType)

    // from template emplaceExp!(IntegerExpLocBooleanType)

    // from template emplaceExp!(IntegerExpLocIntegerType)

    // from template emplaceExp!(IntegerExpLocIntegerType)

    // from template emplaceExp!(IntegerExpLocIntegerType)

    // from template emplaceExp!(IntegerExpLocIntegerType)

    // from template emplaceExp!(IntegerExpLocLongType)

    // from template emplaceExp!(IntegerExpLocLongType)

    // from template emplaceExp!(IntegerExpLocLongType)

    // from template emplaceExp!(IntegerExpLocLongType)

    // from template emplaceExp!(NullExpLocType)

    // from template emplaceExp!(NullExpLocType)

    // from template emplaceExp!(RealExpLocDoubleType)

    // from template emplaceExp!(RealExpLocDoubleType)

    // from template emplaceExp!(SliceExpLocExpressionExpressionExpression)

    // from template emplaceExp!(SliceExpLocExpressionIntegerExpExpression)

    // from template emplaceExp!(SliceExpLocExpressionIntegerExpIntegerExp)

    // from template emplaceExp!(StringExpLocBytePtr)

    // from template emplaceExp!(StringExpLocBytePtrInteger)

    // from template emplaceExp!(StringExpLocBytePtrInteger)

    // from template emplaceExp!(StringExpLocObjectInteger)

    // from template emplaceExp!(StringExpLocObjectInteger)

    // from template emplaceExp!(StringExpLocObjectInteger)

    // from template emplaceExp!(StringExpLocObjectIntegerByte)

    // from template emplaceExp!(StructLiteralExpLocStructDeclarationPtr<DArray<Expression>>)

    // from template emplaceExp!(StructLiteralExpLocStructDeclarationPtr<DArray<Expression>>)

    // from template emplaceExp!(StructLiteralExpLocStructDeclarationPtr<DArray<Expression>>Type)

    // from template emplaceExp!(SymOffExpLocDeclarationInteger)

    // from template emplaceExp!(SymOffExpLocDeclarationLong)

    // from template emplaceExp!(SymOffExpLocDeclarationLong)

    // from template emplaceExp!(TupleExpLocPtr<DArray<Expression>>)

    // from template emplaceExp!(TypeidExpLocType)

    // from template emplaceExp!(VarExpLocDeclaration)

    // from template emplaceExp!(VectorExpLocExpressionType)

    // from template emplaceExp!(VectorExpLocExpressionTypeVector)

    // from template emplaceExp!(VoidInitExpVarDeclaration)

    // from template emplaceExp!(UnionExp)


    public static class Modifiable 
    {
        public static final int no = 0;
        public static final int yes = 1;
        public static final int initialization = 2;
    }

    // Erasure: firstComma<Expression>
    public static Expression firstComma(Expression e) {
        Expression ex = e;
        for (; ((ex.op & 0xFF) == 99);) {
            ex = ((CommaExp)ex).e1.value;
        }
        return ex;
    }

    // Erasure: lastComma<Expression>
    public static Expression lastComma(Expression e) {
        Expression ex = e;
        for (; ((ex.op & 0xFF) == 99);) {
            ex = ((CommaExp)ex).e2.value;
        }
        return ex;
    }

    // Erasure: hasThis<Ptr>
    public static FuncDeclaration hasThis(Ptr<Scope> sc) {
        Dsymbol p = (sc.get()).parent.value;
        for (; (p != null) && (p.isTemplateMixin() != null);) {
            p = p.parent.value;
        }
        FuncDeclaration fdthis = p != null ? p.isFuncDeclaration() : null;
        FuncDeclaration fd = fdthis;
        for (; 1 != 0;){
            if (fd == null)
            {
                return null;
            }
            if (!fd.isNested() || (fd.isThis() != null) || fd.isThis2 && (fd.isMember2() != null))
            {
                break;
            }
            Dsymbol parent = fd.parent.value;
            for (; 1 != 0;){
                if (parent == null)
                {
                    return null;
                }
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                {
                    parent = ti.parent.value;
                }
                else
                {
                    break;
                }
            }
            fd = parent.isFuncDeclaration();
        }
        if ((fd.isThis() == null) && !(fd.isThis2 && (fd.isMember2() != null)))
        {
            return null;
        }
        assert(fd.vthis != null);
        return fd;
    }

    // Erasure: isNeedThisScope<Ptr, Declaration>
    public static boolean isNeedThisScope(Ptr<Scope> sc, Declaration d) {
        if (((sc.get()).intypeof == 1))
        {
            return false;
        }
        AggregateDeclaration ad = d.isThis();
        if (ad == null)
        {
            return false;
        }
        {
            Dsymbol s = (sc.get()).parent.value;
            for (; s != null;s = s.toParentLocal()){
                {
                    AggregateDeclaration ad2 = s.isAggregateDeclaration();
                    if ((ad2) != null)
                    {
                        if ((pequals(ad2, ad)))
                        {
                            return false;
                        }
                        else if (ad2.isNested())
                        {
                            continue;
                        }
                        else
                        {
                            return true;
                        }
                    }
                }
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if ((f) != null)
                    {
                        if (f.isMemberLocal() != null)
                        {
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    // Erasure: isDotOpDispatch<Expression>
    public static boolean isDotOpDispatch(Expression e) {
        {
            DotTemplateInstanceExp dtie = e.isDotTemplateInstanceExp();
            if ((dtie) != null)
            {
                return pequals(dtie.ti.name, Id.opDispatch);
            }
        }
        return false;
    }

    // Erasure: expandTuples<Ptr>
    public static void expandTuples(Ptr<DArray<Expression>> exps) {
        if ((exps == null))
        {
            return ;
        }
        {
            int i = 0;
            for (; (i < (exps.get()).length);i++){
                Expression arg = (exps.get()).get(i);
                if (arg == null)
                {
                    continue;
                }
                {
                    TypeExp e = arg.isTypeExp();
                    if ((e) != null)
                    {
                        {
                            TypeTuple tt = e.type.value.toBasetype().isTypeTuple();
                            if ((tt) != null)
                            {
                                if ((tt.arguments == null) || ((tt.arguments.get()).length == 0))
                                {
                                    (exps.get()).remove(i);
                                    if ((i == (exps.get()).length))
                                    {
                                        return ;
                                    }
                                    i--;
                                    continue;
                                }
                            }
                        }
                    }
                }
                for (; ((arg.op & 0xFF) == 126);){
                    TupleExp te = (TupleExp)arg;
                    (exps.get()).remove(i);
                    (exps.get()).insert(i, te.exps);
                    if ((i == (exps.get()).length))
                    {
                        return ;
                    }
                    exps.get().set(i, Expression.combine(te.e0.value, (exps.get()).get(i)));
                    arg = (exps.get()).get(i);
                }
            }
        }
    }

    // Erasure: isAliasThisTuple<Expression>
    public static TupleDeclaration isAliasThisTuple(Expression e) {
        if (e.type.value == null)
        {
            return null;
        }
        Type t = e.type.value.toBasetype();
        for (; true;){
            {
                Dsymbol s = t.toDsymbol(null);
                if ((s) != null)
                {
                    {
                        AggregateDeclaration ad = s.isAggregateDeclaration();
                        if ((ad) != null)
                        {
                            s = ad.aliasthis;
                            if ((s != null) && (s.isVarDeclaration() != null))
                            {
                                TupleDeclaration td = s.isVarDeclaration().toAlias().isTupleDeclaration();
                                if ((td != null) && td.isexp)
                                {
                                    return td;
                                }
                            }
                            {
                                Type att = t.aliasthisOf();
                                if ((att) != null)
                                {
                                    t = att;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    // Erasure: expandAliasThisTuples<Ptr, int>
    public static int expandAliasThisTuples(Ptr<DArray<Expression>> exps, int starti) {
        if ((exps == null) || ((exps.get()).length == 0))
        {
            return -1;
        }
        {
            int u = starti;
            for (; (u < (exps.get()).length);u++){
                Expression exp = (exps.get()).get(u);
                {
                    TupleDeclaration td = isAliasThisTuple(exp);
                    if ((td) != null)
                    {
                        (exps.get()).remove(u);
                        {
                            Slice<RootObject> __r1333 = (td.objects.get()).opSlice().copy();
                            int __key1332 = 0;
                            for (; (__key1332 < __r1333.getLength());__key1332 += 1) {
                                RootObject o = __r1333.get(__key1332);
                                int i = __key1332;
                                Declaration d = isExpression(o).isDsymbolExp().s.isDeclaration();
                                DotVarExp e = new DotVarExp(exp.loc, exp, d, true);
                                assert(d.type != null);
                                e.type.value = d.type;
                                (exps.get()).insert(u + i, e);
                            }
                        }
                        return u;
                    }
                }
            }
        }
        return -1;
    }

    // defaulted all parameters starting with #2
    public static int expandAliasThisTuples(Ptr<DArray<Expression>> exps) {
        return expandAliasThisTuples(exps, 0);
    }

    // Erasure: getFuncTemplateDecl<Dsymbol>
    public static TemplateDeclaration getFuncTemplateDecl(Dsymbol s) {
        FuncDeclaration f = s.isFuncDeclaration();
        if ((f != null) && (f.parent.value != null))
        {
            {
                TemplateInstance ti = f.parent.value.isTemplateInstance();
                if ((ti) != null)
                {
                    if ((ti.isTemplateMixin() == null) && (ti.tempdecl != null))
                    {
                        TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                        if ((td.onemember != null) && (pequals(td.ident, f.ident)))
                        {
                            return td;
                        }
                    }
                }
            }
        }
        return null;
    }

    // Erasure: valueNoDtor<Expression>
    public static Expression valueNoDtor(Expression e) {
        Expression ex = lastComma(e);
        {
            CallExp ce = ex.isCallExp();
            if ((ce) != null)
            {
                {
                    DotVarExp dve = ce.e1.value.isDotVarExp();
                    if ((dve) != null)
                    {
                        if (dve.var.isCtorDeclaration() != null)
                        {
                            {
                                CommaExp comma = dve.e1.value.isCommaExp();
                                if ((comma) != null)
                                {
                                    {
                                        VarExp ve = comma.e2.value.isVarExp();
                                        if ((ve) != null)
                                        {
                                            VarDeclaration ctmp = ve.var.isVarDeclaration();
                                            if (ctmp != null)
                                            {
                                                ctmp.storage_class |= 16777216L;
                                                assert(!ce.isLvalue());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                VarExp ve = ex.isVarExp();
                if ((ve) != null)
                {
                    VarDeclaration vtmp = ve.var.isVarDeclaration();
                    if ((vtmp != null) && ((vtmp.storage_class & 2199023255552L) != 0))
                    {
                        vtmp.storage_class |= 16777216L;
                    }
                }
            }
        }
        return e;
    }

    // Erasure: callCpCtor<Ptr, Expression, Type>
    public static Expression callCpCtor(Ptr<Scope> sc, Expression e, Type destinationType) {
        {
            TypeStruct ts = e.type.value.baseElemOf().isTypeStruct();
            if ((ts) != null)
            {
                StructDeclaration sd = ts.sym;
                if ((sd.postblit != null) || sd.hasCopyCtor)
                {
                    VarDeclaration tmp = copyToTemp(2199023255552L, new BytePtr("__copytmp"), e);
                    if (sd.hasCopyCtor && (destinationType != null))
                    {
                        tmp.type = destinationType;
                    }
                    tmp.storage_class |= 16777216L;
                    dsymbolSemantic(tmp, sc);
                    Expression de = new DeclarationExp(e.loc, tmp);
                    Expression ve = new VarExp(e.loc, tmp, true);
                    de.type.value = Type.tvoid;
                    ve.type.value = e.type.value;
                    return Expression.combine(de, ve);
                }
            }
        }
        return e;
    }

    // Erasure: doCopyOrMove<Ptr, Expression, Type>
    public static Expression doCopyOrMove(Ptr<Scope> sc, Expression e, Type t) {
        {
            CondExp ce = e.isCondExp();
            if ((ce) != null)
            {
                ce.e1.value = doCopyOrMove(sc, ce.e1.value, null);
                ce.e2.value = doCopyOrMove(sc, ce.e2.value, null);
            }
            else
            {
                e = e.isLvalue() ? callCpCtor(sc, e, t) : valueNoDtor(e);
            }
        }
        return e;
    }

    // defaulted all parameters starting with #3
    public static Expression doCopyOrMove(Ptr<Scope> sc, Expression e) {
        return doCopyOrMove(sc, e, (Type)null);
    }

    // Erasure: RealIdentical<double, double>
    public static int RealIdentical(double x1, double x2) {
        return ((CTFloat.isNaN(x1) && CTFloat.isNaN(x2) || CTFloat.isIdentical(x1, x2)) ? 1 : 0);
    }

    // Erasure: typeDotIdExp<Loc, Type, Identifier>
    public static DotIdExp typeDotIdExp(Loc loc, Type type, Identifier ident) {
        return new DotIdExp(loc, new TypeExp(loc, type), ident);
    }

    // Erasure: expToVariable<Expression>
    public static VarDeclaration expToVariable(Expression e) {
        for (; 1 != 0;){
            switch ((e.op & 0xFF))
            {
                case 26:
                    return ((VarExp)e).var.isVarDeclaration();
                case 27:
                    e = ((DotVarExp)e).e1.value;
                    continue;
                case 62:
                    IndexExp ei = (IndexExp)e;
                    e = ei.e1.value;
                    Type ti = e.type.value.toBasetype();
                    if (((ti.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        continue;
                    }
                    return null;
                case 31:
                    SliceExp ei_1 = (SliceExp)e;
                    e = ei_1.e1.value;
                    Type ti_1 = e.type.value.toBasetype();
                    if (((ti_1.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        continue;
                    }
                    return null;
                case 123:
                case 124:
                    return ((ThisExp)e).var.isVarDeclaration();
                default:
                return null;
            }
        }
    }


    public static class OwnedBy 
    {
        public static final byte code = (byte)0;
        public static final byte ctfe = (byte)1;
        public static final byte cache = (byte)2;
    }

    static int WANTvalue = 0;
    static int WANTexpand = 1;
    public static abstract class Expression extends ASTNode
    {
        public byte op = 0;
        public byte size = 0;
        public byte parens = 0;
        public Ref<Type> type = ref(null);
        public Loc loc = new Loc();
        // Erasure: __ctor<Loc, byte, int>
        public  Expression(Loc loc, byte op, int size) {
            super();
            this.loc.opAssign(loc.copy());
            this.op = op;
            this.size = (byte)size;
        }

        // Erasure: _init<>
        public static void _init() {
            CTFEExp.cantexp = new CTFEExp(TOK.cantExpression);
            CTFEExp.voidexp = new CTFEExp(TOK.voidExpression);
            CTFEExp.breakexp = new CTFEExp(TOK.break_);
            CTFEExp.continueexp = new CTFEExp(TOK.continue_);
            CTFEExp.gotoexp = new CTFEExp(TOK.goto_);
            CTFEExp.showcontext = new CTFEExp(TOK.showCtfeContext);
        }

        // Erasure: deinitialize<>
        public static void deinitialize() {
            CTFEExp.cantexp = null;
            CTFEExp.voidexp = null;
            CTFEExp.breakexp = null;
            CTFEExp.continueexp = null;
            CTFEExp.gotoexp = null;
            CTFEExp.showcontext = null;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return this.copy();
        }

        // Erasure: dyncast<>
        public  int dyncast() {
            return DYNCAST.expression;
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                Ref<HdrGenState> hgs = ref(new HdrGenState());
                toCBuffer(this, ptr(buf), ptr(hgs));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        // Erasure: error<Ptr>
        public  void error(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            if ((!pequals(this.type.value, Type.terror)))
            {
                verror(this.loc, format_ref.value, new RawSlice<>(ap), null, null, new BytePtr("Error: "));
            }
        }

        // Erasure: errorSupplemental<Ptr>
        public  void errorSupplemental(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            if ((pequals(this.type.value, Type.terror)))
            {
                return ;
            }
            verrorSupplemental(this.loc, format_ref.value, new RawSlice<>(ap));
        }

        // Erasure: warning<Ptr>
        public  void warning(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            if ((!pequals(this.type.value, Type.terror)))
            {
                vwarning(this.loc, format_ref.value, new RawSlice<>(ap));
            }
        }

        // Erasure: deprecation<Ptr>
        public  void deprecation(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            if ((!pequals(this.type.value, Type.terror)))
            {
                vdeprecation(this.loc, format_ref.value, new RawSlice<>(ap), null, null);
            }
        }

        // Erasure: combine<Expression, Expression>
        public static Expression combine(Expression e1, Expression e2) {
            if (e1 != null)
            {
                if (e2 != null)
                {
                    e1 = new CommaExp(e1.loc, e1, e2, true);
                    e1.type.value = e2.type.value;
                }
            }
            else
            {
                e1 = e2;
            }
            return e1;
        }

        // Erasure: combine<Expression, Expression, Expression>
        public static Expression combine(Expression e1, Expression e2, Expression e3) {
            return combine(combine(e1, e2), e3);
        }

        // Erasure: combine<Expression, Expression, Expression, Expression>
        public static Expression combine(Expression e1, Expression e2, Expression e3, Expression e4) {
            return combine(combine(e1, e2), combine(e3, e4));
        }

        // Erasure: extractLast<Expression, Expression>
        public static Expression extractLast(Expression e, Ref<Expression> e0) {
            e0.value = null;
            if (((e.op & 0xFF) != 99))
            {
                return e;
            }
            CommaExp ce = (CommaExp)e;
            if (((ce.e2.value.op & 0xFF) != 99))
            {
                e0.value = ce.e1.value;
                return ce.e2.value;
            }
            else
            {
                e0.value = e;
                Ptr<Expression> pce = pcopy(ptr(ce.e2));
                for (; ((((CommaExp)pce.get()).e2.value.op & 0xFF) == 99);){
                    pce = pcopy((ptr((CommaExp)pce.get().e2)));
                }
                assert((((pce.get()).op & 0xFF) == 99));
                ce = (CommaExp)pce.get();
                pce.set(0, ce.e1.value);
                return ce.e2.value;
            }
        }

        // Erasure: arraySyntaxCopy<Ptr>
        public static Ptr<DArray<Expression>> arraySyntaxCopy(Ptr<DArray<Expression>> exps) {
            Ptr<DArray<Expression>> a = null;
            if (exps != null)
            {
                a = pcopy((refPtr(new DArray<Expression>((exps.get()).length))));
                {
                    Slice<Expression> __r1335 = (exps.get()).opSlice().copy();
                    int __key1334 = 0;
                    for (; (__key1334 < __r1335.getLength());__key1334 += 1) {
                        Expression e = __r1335.get(__key1334);
                        int i = __key1334;
                        a.get().set(i, e != null ? e.syntaxCopy() : null);
                    }
                }
            }
            return a;
        }

        // Erasure: toInteger<>
        public  long toInteger() {
            this.error(new BytePtr("integer constant expression expected instead of `%s`"), this.toChars());
            return 0L;
        }

        // Erasure: toUInteger<>
        public  long toUInteger() {
            return this.toInteger();
        }

        // Erasure: toReal<>
        public  double toReal() {
            this.error(new BytePtr("floating point constant expression expected instead of `%s`"), this.toChars());
            return CTFloat.zero;
        }

        // Erasure: toImaginary<>
        public  double toImaginary() {
            this.error(new BytePtr("floating point constant expression expected instead of `%s`"), this.toChars());
            return CTFloat.zero;
        }

        // Erasure: toComplex<>
        public  complex_t toComplex() {
            this.error(new BytePtr("floating point constant expression expected instead of `%s`"), this.toChars());
            return new complex_t(CTFloat.zero);
        }

        // Erasure: toStringExp<>
        public  StringExp toStringExp() {
            return null;
        }

        // Erasure: toTupleExp<>
        public  TupleExp toTupleExp() {
            return null;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return false;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if (e == null)
            {
                e = this;
            }
            else if (!this.loc.isValid())
            {
                this.loc.opAssign(e.loc.copy());
            }
            if (((e.op & 0xFF) == 20))
            {
                this.error(new BytePtr("`%s` is a `%s` definition and cannot be modified"), e.type.value.toChars(), e.type.value.kind());
            }
            else
            {
                this.error(new BytePtr("`%s` is not an lvalue and cannot be modified"), e.toChars());
            }
            return new ErrorExp();
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            if ((this.checkModifiable(sc, 0) == Modifiable.yes))
            {
                assert(this.type.value != null);
                if (!this.type.value.isMutable())
                {
                    {
                        DotVarExp dve = this.isDotVarExp();
                        if ((dve) != null)
                        {
                            if (isNeedThisScope(sc, dve.var))
                            {
                                Dsymbol s = (sc.get()).func;
                                for (; s != null;s = s.toParentLocal()){
                                    FuncDeclaration ff = s.isFuncDeclaration();
                                    if (ff == null)
                                    {
                                        break;
                                    }
                                    if (!ff.type.isMutable())
                                    {
                                        this.error(new BytePtr("cannot modify `%s` in `%s` function"), this.toChars(), MODtoChars(this.type.value.mod));
                                        return new ErrorExp();
                                    }
                                }
                            }
                        }
                    }
                    this.error(new BytePtr("cannot modify `%s` expression `%s`"), MODtoChars(this.type.value.mod), this.toChars());
                    return new ErrorExp();
                }
                else if (!this.type.value.isAssignable())
                {
                    this.error(new BytePtr("cannot modify struct instance `%s` of type `%s` because it contains `const` or `immutable` members"), this.toChars(), this.type.value.toChars());
                    return new ErrorExp();
                }
            }
            return this.toLvalue(sc, e);
        }

        // Erasure: implicitCastTo<Ptr, Type>
        public  Expression implicitCastTo(Ptr<Scope> sc, Type t) {
            return implicitCastTo(this, sc, t);
        }

        // Erasure: implicitConvTo<Type>
        public  int implicitConvTo(Type t) {
            return implicitConvTo(this, t);
        }

        // Erasure: castTo<Ptr, Type>
        public  Expression castTo(Ptr<Scope> sc, Type t) {
            return castTo(this, sc, t);
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            this.loc.opAssign(loc.copy());
            return this;
        }

        // Erasure: checkType<>
        public  boolean checkType() {
            return false;
        }

        // Erasure: checkValue<>
        public  boolean checkValue() {
            if ((this.type.value != null) && ((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
            {
                this.error(new BytePtr("expression `%s` is `void` and has no value"), this.toChars());
                if (global.gag == 0)
                {
                    this.type.value = Type.terror;
                }
                return true;
            }
            return false;
        }

        // Erasure: checkScalar<>
        public  boolean checkScalar() {
            if (((this.op & 0xFF) == 127))
            {
                return true;
            }
            if (((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Terror))
            {
                return true;
            }
            if (!this.type.value.isscalar())
            {
                this.error(new BytePtr("`%s` is not a scalar, it is a `%s`"), this.toChars(), this.type.value.toChars());
                return true;
            }
            return this.checkValue();
        }

        // Erasure: checkNoBool<>
        public  boolean checkNoBool() {
            if (((this.op & 0xFF) == 127))
            {
                return true;
            }
            if (((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Terror))
            {
                return true;
            }
            if (((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tbool))
            {
                this.error(new BytePtr("operation not allowed on `bool` `%s`"), this.toChars());
                return true;
            }
            return false;
        }

        // Erasure: checkIntegral<>
        public  boolean checkIntegral() {
            if (((this.op & 0xFF) == 127))
            {
                return true;
            }
            if (((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Terror))
            {
                return true;
            }
            if (!this.type.value.isintegral())
            {
                this.error(new BytePtr("`%s` is not of integral type, it is a `%s`"), this.toChars(), this.type.value.toChars());
                return true;
            }
            return this.checkValue();
        }

        // Erasure: checkArithmetic<>
        public  boolean checkArithmetic() {
            if (((this.op & 0xFF) == 127))
            {
                return true;
            }
            if (((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Terror))
            {
                return true;
            }
            if (!this.type.value.isintegral() && !this.type.value.isfloating())
            {
                this.error(new BytePtr("`%s` is not of arithmetic type, it is a `%s`"), this.toChars(), this.type.value.toChars());
                return true;
            }
            return this.checkValue();
        }

        // Erasure: checkDeprecated<Ptr, Dsymbol>
        public  boolean checkDeprecated(Ptr<Scope> sc, Dsymbol s) {
            return s.checkDeprecated(this.loc, sc);
        }

        // Erasure: checkDisabled<Ptr, Dsymbol>
        public  boolean checkDisabled(Ptr<Scope> sc, Dsymbol s) {
            {
                Declaration d = s.isDeclaration();
                if ((d) != null)
                {
                    return d.checkDisabled(this.loc, sc, false);
                }
            }
            return false;
        }

        // Erasure: checkPurity<Ptr, FuncDeclaration>
        public  boolean checkPurity(Ptr<Scope> sc, FuncDeclaration f) {
            if ((sc.get()).func == null)
            {
                return false;
            }
            if ((pequals((sc.get()).func, f)))
            {
                return false;
            }
            if (((sc.get()).intypeof == 1))
            {
                return false;
            }
            if (((sc.get()).flags & 136) != 0)
            {
                return false;
            }
            FuncDeclaration outerfunc = (sc.get()).func;
            FuncDeclaration calledparent = f;
            if (outerfunc.isInstantiated() != null)
            {
            }
            else if (f.isInstantiated() != null)
            {
            }
            else if (f.isFuncLiteralDeclaration() != null)
            {
            }
            else
            {
                for (; (outerfunc.toParent2() != null) && (outerfunc.isPureBypassingInference() == PURE.impure) && (outerfunc.toParent2().isFuncDeclaration() != null);){
                    outerfunc = outerfunc.toParent2().isFuncDeclaration();
                    if (((outerfunc.type.ty & 0xFF) == ENUMTY.Terror))
                    {
                        return true;
                    }
                }
                for (; (calledparent.toParent2() != null) && (calledparent.isPureBypassingInference() == PURE.impure) && (calledparent.toParent2().isFuncDeclaration() != null);){
                    calledparent = calledparent.toParent2().isFuncDeclaration();
                    if (((calledparent.type.ty & 0xFF) == ENUMTY.Terror))
                    {
                        return true;
                    }
                }
            }
            if ((f.isPure() == 0) && (!pequals(calledparent, outerfunc)))
            {
                FuncDeclaration ff = outerfunc;
                if (((sc.get()).flags & 256) != 0 ? ff.isPureBypassingInference() >= PURE.weak : ff.setImpure())
                {
                    this.error(new BytePtr("`pure` %s `%s` cannot call impure %s `%s`"), ff.kind(), ff.toPrettyChars(false), f.kind(), f.toPrettyChars(false));
                    return true;
                }
            }
            return false;
        }

        // Erasure: checkPurity<Ptr, VarDeclaration>
        public  boolean checkPurity(Ptr<Scope> sc, VarDeclaration v) {
            if ((sc.get()).func == null)
            {
                return false;
            }
            if (((sc.get()).intypeof == 1))
            {
                return false;
            }
            if (((sc.get()).flags & 136) != 0)
            {
                return false;
            }
            if ((pequals(v.ident, Id.ctfe)))
            {
                return false;
            }
            if (v.isImmutable())
            {
                return false;
            }
            if (v.isConst() && !v.isRef() && v.isDataseg() || v.isParameter() && (v.type.implicitConvTo(v.type.immutableOf()) != 0))
            {
                return false;
            }
            if ((v.storage_class & 8388608L) != 0)
            {
                return false;
            }
            if (((v.type.ty & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = ((TypeStruct)v.type).sym;
                if (sd.hasNoFields)
                {
                    return false;
                }
            }
            boolean err = false;
            if (v.isDataseg())
            {
                if ((pequals(v.ident, Id.gate)))
                {
                    return false;
                }
                {
                    Dsymbol s = (sc.get()).func;
                    for (; s != null;s = s.toParent2()){
                        FuncDeclaration ff = s.isFuncDeclaration();
                        if (ff == null)
                        {
                            break;
                        }
                        if (((sc.get()).flags & 256) != 0 ? ff.isPureBypassingInference() >= PURE.weak : ff.setImpure())
                        {
                            this.error(new BytePtr("`pure` %s `%s` cannot access mutable static data `%s`"), ff.kind(), ff.toPrettyChars(false), v.toChars());
                            err = true;
                            break;
                        }
                        if (ff.isInstantiated() != null)
                        {
                            break;
                        }
                        if (ff.isFuncLiteralDeclaration() != null)
                        {
                            break;
                        }
                    }
                }
            }
            else
            {
                Dsymbol vparent = v.toParent2();
                {
                    Dsymbol s = (sc.get()).func;
                    for (; !err && (s != null);s = toParentPDsymbol(s, vparent)){
                        if ((pequals(s, vparent)))
                        {
                            break;
                        }
                        {
                            AggregateDeclaration ad = s.isAggregateDeclaration();
                            if ((ad) != null)
                            {
                                if (ad.isNested())
                                {
                                    continue;
                                }
                                break;
                            }
                        }
                        FuncDeclaration ff = s.isFuncDeclaration();
                        if (ff == null)
                        {
                            break;
                        }
                        if (ff.isNested() || (ff.isThis() != null))
                        {
                            if (ff.type.isImmutable() || ff.type.isShared() && !MODimplicitConv(ff.type.mod, v.type.mod))
                            {
                                Ref<OutBuffer> ffbuf = ref(new OutBuffer());
                                try {
                                    Ref<OutBuffer> vbuf = ref(new OutBuffer());
                                    try {
                                        MODMatchToBuffer(ptr(ffbuf), ff.type.mod, v.type.mod);
                                        MODMatchToBuffer(ptr(vbuf), v.type.mod, ff.type.mod);
                                        this.error(new BytePtr("%s%s `%s` cannot access %sdata `%s`"), ffbuf.value.peekChars(), ff.kind(), ff.toPrettyChars(false), vbuf.value.peekChars(), v.toChars());
                                        err = true;
                                        break;
                                    }
                                    finally {
                                    }
                                }
                                finally {
                                }
                            }
                            continue;
                        }
                        break;
                    }
                }
            }
            if ((v.storage_class & 1073741824L) != 0)
            {
                if ((sc.get()).func.setUnsafe())
                {
                    this.error(new BytePtr("`@safe` %s `%s` cannot access `__gshared` data `%s`"), (sc.get()).func.kind(), (sc.get()).func.toChars(), v.toChars());
                    err = true;
                }
            }
            return err;
        }

        // Erasure: checkSafety<Ptr, FuncDeclaration>
        public  boolean checkSafety(Ptr<Scope> sc, FuncDeclaration f) {
            if ((sc.get()).func == null)
            {
                return false;
            }
            if ((pequals((sc.get()).func, f)))
            {
                return false;
            }
            if (((sc.get()).intypeof == 1))
            {
                return false;
            }
            if (((sc.get()).flags & 128) != 0)
            {
                return false;
            }
            if (!f.isSafe() && !f.isTrusted())
            {
                if (((sc.get()).flags & 256) != 0 ? (sc.get()).func.isSafeBypassingInference() : (sc.get()).func.setUnsafe() && (((sc.get()).flags & 8) == 0))
                {
                    if (!this.loc.isValid())
                    {
                        this.loc.opAssign((sc.get()).func.loc.copy());
                    }
                    BytePtr prettyChars = pcopy(f.toPrettyChars(false));
                    this.error(new BytePtr("`@safe` %s `%s` cannot call `@system` %s `%s`"), (sc.get()).func.kind(), (sc.get()).func.toPrettyChars(false), f.kind(), prettyChars);
                    errorSupplemental(f.loc, new BytePtr("`%s` is declared here"), prettyChars);
                    return true;
                }
            }
            return false;
        }

        // Erasure: checkNogc<Ptr, FuncDeclaration>
        public  boolean checkNogc(Ptr<Scope> sc, FuncDeclaration f) {
            if ((sc.get()).func == null)
            {
                return false;
            }
            if ((pequals((sc.get()).func, f)))
            {
                return false;
            }
            if (((sc.get()).intypeof == 1))
            {
                return false;
            }
            if (((sc.get()).flags & 128) != 0)
            {
                return false;
            }
            if (!f.isNogc())
            {
                if (((sc.get()).flags & 256) != 0 ? (sc.get()).func.isNogcBypassingInference() : (sc.get()).func.setGC() && (((sc.get()).flags & 8) == 0))
                {
                    if ((this.loc.linnum == 0))
                    {
                        this.loc.opAssign((sc.get()).func.loc.copy());
                    }
                    this.error(new BytePtr("`@nogc` %s `%s` cannot call non-@nogc %s `%s`"), (sc.get()).func.kind(), (sc.get()).func.toPrettyChars(false), f.kind(), f.toPrettyChars(false));
                    return true;
                }
            }
            return false;
        }

        // Erasure: checkPostblit<Ptr, Type>
        public  boolean checkPostblit(Ptr<Scope> sc, Type t) {
            {
                TypeStruct ts = t.baseElemOf().isTypeStruct();
                if ((ts) != null)
                {
                    if (global.params.useTypeInfo)
                    {
                        semanticTypeInfo(sc, t);
                    }
                    StructDeclaration sd = ts.sym;
                    if (sd.postblit != null)
                    {
                        if (sd.postblit.checkDisabled(this.loc, sc, false))
                        {
                            return true;
                        }
                        this.checkPurity(sc, sd.postblit);
                        this.checkSafety(sc, sd.postblit);
                        this.checkNogc(sc, sd.postblit);
                        return false;
                    }
                }
            }
            return false;
        }

        // Erasure: checkRightThis<Ptr>
        public  boolean checkRightThis(Ptr<Scope> sc) {
            if (((this.op & 0xFF) == 127))
            {
                return true;
            }
            if (((this.op & 0xFF) == 26) && ((this.type.value.ty & 0xFF) != ENUMTY.Terror))
            {
                VarExp ve = (VarExp)this;
                if (isNeedThisScope(sc, ve.var))
                {
                    this.error(new BytePtr("need `this` for `%s` of type `%s`"), ve.var.toChars(), ve.var.type.toChars());
                    return true;
                }
            }
            return false;
        }

        // Erasure: checkReadModifyWrite<byte, Expression>
        public  boolean checkReadModifyWrite(byte rmwOp, Expression ex) {
            if ((this.type.value == null) || !this.type.value.isShared())
            {
                return false;
            }
            switch ((rmwOp & 0xFF))
            {
                case 93:
                case 103:
                    rmwOp = TOK.addAssign;
                    break;
                case 94:
                case 104:
                    rmwOp = TOK.minAssign;
                    break;
                default:
                break;
            }
            this.error(new BytePtr("read-modify-write operations are not allowed for `shared` variables. Use `core.atomic.atomicOp!\"%s\"(%s, %s)` instead."), Token.toChars(rmwOp), this.toChars(), ex != null ? ex.toChars() : new BytePtr("1"));
            return true;
        }

        // defaulted all parameters starting with #2
        public  boolean checkReadModifyWrite(byte rmwOp) {
            return checkReadModifyWrite(rmwOp, (Expression)null);
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            return this.type.value != null ? Modifiable.yes : Modifiable.no;
        }

        // defaulted all parameters starting with #2
        public  int checkModifiable(Ptr<Scope> sc) {
            return checkModifiable(sc, 0);
        }

        // Erasure: toBoolean<Ptr>
        public  Expression toBoolean(Ptr<Scope> sc) {
            Expression e = this;
            Type t = this.type.value;
            Type tb = this.type.value.toBasetype();
            Type att = null;
            for (; 1 != 0;){
                {
                    TypeStruct ts = tb.isTypeStruct();
                    if ((ts) != null)
                    {
                        AggregateDeclaration ad = ts.sym;
                        {
                            Dsymbol fd = search_function(ad, Id._cast);
                            if ((fd) != null)
                            {
                                e = new CastExp(this.loc, e, Type.tbool);
                                e = expressionSemantic(e, sc);
                                return e;
                            }
                        }
                        if ((ad.aliasthis != null) && (!pequals(tb, att)))
                        {
                            if ((att == null) && tb.checkAliasThisRec())
                            {
                                att = tb;
                            }
                            e = resolveAliasThis(sc, e, false);
                            t = e.type.value;
                            tb = e.type.value.toBasetype();
                            continue;
                        }
                    }
                }
                break;
            }
            if (!t.isBoolean())
            {
                if ((!pequals(tb, Type.terror)))
                {
                    this.error(new BytePtr("expression `%s` of type `%s` does not have a boolean value"), this.toChars(), t.toChars());
                }
                return new ErrorExp();
            }
            return e;
        }

        // Erasure: addDtorHook<Ptr>
        public  Expression addDtorHook(Ptr<Scope> sc) {
            return this;
        }

        // Erasure: addressOf<>
        public  Expression addressOf() {
            Expression e = new AddrExp(this.loc, this, this.type.value.pointerTo());
            return e;
        }

        // Erasure: deref<>
        public  Expression deref() {
            if (this.type.value != null)
            {
                {
                    TypeReference tr = this.type.value.isTypeReference();
                    if ((tr) != null)
                    {
                        Expression e = new PtrExp(this.loc, this, tr.next.value);
                        return e;
                    }
                }
            }
            return this;
        }

        // Erasure: optimize<int, boolean>
        public  Expression optimize(int result, boolean keepLvalue) {
            return Expression_optimize(this, result, keepLvalue);
        }

        // defaulted all parameters starting with #2
        public  Expression optimize(int result) {
            return optimize(result, false);
        }

        // Erasure: ctfeInterpret<>
        public  Expression ctfeInterpret() {
            return ctfeInterpret(this);
        }

        // Erasure: isConst<>
        public  int isConst() {
            return isConst(this);
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return false;
        }

        // Erasure: hasCode<>
        public  boolean hasCode() {
            return true;
        }

        // Erasure: isIntegerExp<>
        public  IntegerExp isIntegerExp() {
            return ((this.op & 0xFF) == 135) ? (IntegerExp)this : null;
        }

        // Erasure: isErrorExp<>
        public  ErrorExp isErrorExp() {
            return ((this.op & 0xFF) == 127) ? (ErrorExp)this : null;
        }

        // Erasure: isVoidInitExp<>
        public  VoidInitExp isVoidInitExp() {
            return ((this.op & 0xFF) == 128) ? (VoidInitExp)this : null;
        }

        // Erasure: isRealExp<>
        public  RealExp isRealExp() {
            return ((this.op & 0xFF) == 140) ? (RealExp)this : null;
        }

        // Erasure: isComplexExp<>
        public  ComplexExp isComplexExp() {
            return ((this.op & 0xFF) == 147) ? (ComplexExp)this : null;
        }

        // Erasure: isIdentifierExp<>
        public  IdentifierExp isIdentifierExp() {
            return ((this.op & 0xFF) == 120) ? (IdentifierExp)this : null;
        }

        // Erasure: isDollarExp<>
        public  DollarExp isDollarExp() {
            return ((this.op & 0xFF) == 35) ? (DollarExp)this : null;
        }

        // Erasure: isDsymbolExp<>
        public  DsymbolExp isDsymbolExp() {
            return ((this.op & 0xFF) == 41) ? (DsymbolExp)this : null;
        }

        // Erasure: isThisExp<>
        public  ThisExp isThisExp() {
            return ((this.op & 0xFF) == 123) ? (ThisExp)this : null;
        }

        // Erasure: isSuperExp<>
        public  SuperExp isSuperExp() {
            return ((this.op & 0xFF) == 124) ? (SuperExp)this : null;
        }

        // Erasure: isNullExp<>
        public  NullExp isNullExp() {
            return ((this.op & 0xFF) == 13) ? (NullExp)this : null;
        }

        // Erasure: isStringExp<>
        public  StringExp isStringExp() {
            return ((this.op & 0xFF) == 121) ? (StringExp)this : null;
        }

        // Erasure: isTupleExp<>
        public  TupleExp isTupleExp() {
            return ((this.op & 0xFF) == 126) ? (TupleExp)this : null;
        }

        // Erasure: isArrayLiteralExp<>
        public  ArrayLiteralExp isArrayLiteralExp() {
            return ((this.op & 0xFF) == 47) ? (ArrayLiteralExp)this : null;
        }

        // Erasure: isAssocArrayLiteralExp<>
        public  AssocArrayLiteralExp isAssocArrayLiteralExp() {
            return ((this.op & 0xFF) == 48) ? (AssocArrayLiteralExp)this : null;
        }

        // Erasure: isStructLiteralExp<>
        public  StructLiteralExp isStructLiteralExp() {
            return ((this.op & 0xFF) == 49) ? (StructLiteralExp)this : null;
        }

        // Erasure: isTypeExp<>
        public  TypeExp isTypeExp() {
            return ((this.op & 0xFF) == 20) ? (TypeExp)this : null;
        }

        // Erasure: isScopeExp<>
        public  ScopeExp isScopeExp() {
            return ((this.op & 0xFF) == 203) ? (ScopeExp)this : null;
        }

        // Erasure: isTemplateExp<>
        public  TemplateExp isTemplateExp() {
            return ((this.op & 0xFF) == 36) ? (TemplateExp)this : null;
        }

        // Erasure: isNewExp<>
        public  NewExp isNewExp() {
            return ((this.op & 0xFF) == 22) ? (NewExp)this : null;
        }

        // Erasure: isNewAnonClassExp<>
        public  NewAnonClassExp isNewAnonClassExp() {
            return ((this.op & 0xFF) == 45) ? (NewAnonClassExp)this : null;
        }

        // Erasure: isSymOffExp<>
        public  SymOffExp isSymOffExp() {
            return ((this.op & 0xFF) == 25) ? (SymOffExp)this : null;
        }

        // Erasure: isVarExp<>
        public  VarExp isVarExp() {
            return ((this.op & 0xFF) == 26) ? (VarExp)this : null;
        }

        // Erasure: isOverExp<>
        public  OverExp isOverExp() {
            return ((this.op & 0xFF) == 214) ? (OverExp)this : null;
        }

        // Erasure: isFuncExp<>
        public  FuncExp isFuncExp() {
            return ((this.op & 0xFF) == 161) ? (FuncExp)this : null;
        }

        // Erasure: isDeclarationExp<>
        public  DeclarationExp isDeclarationExp() {
            return ((this.op & 0xFF) == 38) ? (DeclarationExp)this : null;
        }

        // Erasure: isTypeidExp<>
        public  TypeidExp isTypeidExp() {
            return ((this.op & 0xFF) == 42) ? (TypeidExp)this : null;
        }

        // Erasure: isTraitsExp<>
        public  TraitsExp isTraitsExp() {
            return ((this.op & 0xFF) == 213) ? (TraitsExp)this : null;
        }

        // Erasure: isHaltExp<>
        public  HaltExp isHaltExp() {
            return ((this.op & 0xFF) == 125) ? (HaltExp)this : null;
        }

        // Erasure: isExp<>
        public  IsExp isExp() {
            return ((this.op & 0xFF) == 63) ? (IsExp)this : null;
        }

        // Erasure: isCompileExp<>
        public  CompileExp isCompileExp() {
            return ((this.op & 0xFF) == 162) ? (CompileExp)this : null;
        }

        // Erasure: isImportExp<>
        public  ImportExp isImportExp() {
            return ((this.op & 0xFF) == 157) ? (ImportExp)this : null;
        }

        // Erasure: isAssertExp<>
        public  AssertExp isAssertExp() {
            return ((this.op & 0xFF) == 14) ? (AssertExp)this : null;
        }

        // Erasure: isDotIdExp<>
        public  DotIdExp isDotIdExp() {
            return ((this.op & 0xFF) == 28) ? (DotIdExp)this : null;
        }

        // Erasure: isDotTemplateExp<>
        public  DotTemplateExp isDotTemplateExp() {
            return ((this.op & 0xFF) == 37) ? (DotTemplateExp)this : null;
        }

        // Erasure: isDotVarExp<>
        public  DotVarExp isDotVarExp() {
            return ((this.op & 0xFF) == 27) ? (DotVarExp)this : null;
        }

        // Erasure: isDotTemplateInstanceExp<>
        public  DotTemplateInstanceExp isDotTemplateInstanceExp() {
            return ((this.op & 0xFF) == 29) ? (DotTemplateInstanceExp)this : null;
        }

        // Erasure: isDelegateExp<>
        public  DelegateExp isDelegateExp() {
            return ((this.op & 0xFF) == 160) ? (DelegateExp)this : null;
        }

        // Erasure: isDotTypeExp<>
        public  DotTypeExp isDotTypeExp() {
            return ((this.op & 0xFF) == 30) ? (DotTypeExp)this : null;
        }

        // Erasure: isCallExp<>
        public  CallExp isCallExp() {
            return ((this.op & 0xFF) == 18) ? (CallExp)this : null;
        }

        // Erasure: isAddrExp<>
        public  AddrExp isAddrExp() {
            return ((this.op & 0xFF) == 19) ? (AddrExp)this : null;
        }

        // Erasure: isPtrExp<>
        public  PtrExp isPtrExp() {
            return ((this.op & 0xFF) == 24) ? (PtrExp)this : null;
        }

        // Erasure: isNegExp<>
        public  NegExp isNegExp() {
            return ((this.op & 0xFF) == 8) ? (NegExp)this : null;
        }

        // Erasure: isUAddExp<>
        public  UAddExp isUAddExp() {
            return ((this.op & 0xFF) == 43) ? (UAddExp)this : null;
        }

        // Erasure: isComExp<>
        public  ComExp isComExp() {
            return ((this.op & 0xFF) == 92) ? (ComExp)this : null;
        }

        // Erasure: isNotExp<>
        public  NotExp isNotExp() {
            return ((this.op & 0xFF) == 91) ? (NotExp)this : null;
        }

        // Erasure: isDeleteExp<>
        public  DeleteExp isDeleteExp() {
            return ((this.op & 0xFF) == 23) ? (DeleteExp)this : null;
        }

        // Erasure: isCastExp<>
        public  CastExp isCastExp() {
            return ((this.op & 0xFF) == 12) ? (CastExp)this : null;
        }

        // Erasure: isVectorExp<>
        public  VectorExp isVectorExp() {
            return ((this.op & 0xFF) == 229) ? (VectorExp)this : null;
        }

        // Erasure: isVectorArrayExp<>
        public  VectorArrayExp isVectorArrayExp() {
            return ((this.op & 0xFF) == 236) ? (VectorArrayExp)this : null;
        }

        // Erasure: isSliceExp<>
        public  SliceExp isSliceExp() {
            return ((this.op & 0xFF) == 31) ? (SliceExp)this : new RawSliceExp();
        }

        // Erasure: isArrayLengthExp<>
        public  ArrayLengthExp isArrayLengthExp() {
            return ((this.op & 0xFF) == 32) ? (ArrayLengthExp)this : null;
        }

        // Erasure: isArrayExp<>
        public  ArrayExp isArrayExp() {
            return ((this.op & 0xFF) == 17) ? (ArrayExp)this : null;
        }

        // Erasure: isDotExp<>
        public  DotExp isDotExp() {
            return ((this.op & 0xFF) == 97) ? (DotExp)this : null;
        }

        // Erasure: isCommaExp<>
        public  CommaExp isCommaExp() {
            return ((this.op & 0xFF) == 99) ? (CommaExp)this : null;
        }

        // Erasure: isIntervalExp<>
        public  IntervalExp isIntervalExp() {
            return ((this.op & 0xFF) == 231) ? (IntervalExp)this : null;
        }

        // Erasure: isDelegatePtrExp<>
        public  DelegatePtrExp isDelegatePtrExp() {
            return ((this.op & 0xFF) == 52) ? (DelegatePtrExp)this : null;
        }

        // Erasure: isDelegateFuncptrExp<>
        public  DelegateFuncptrExp isDelegateFuncptrExp() {
            return ((this.op & 0xFF) == 53) ? (DelegateFuncptrExp)this : null;
        }

        // Erasure: isIndexExp<>
        public  IndexExp isIndexExp() {
            return ((this.op & 0xFF) == 62) ? (IndexExp)this : null;
        }

        // Erasure: isPostExp<>
        public  PostExp isPostExp() {
            return ((this.op & 0xFF) == 93) || ((this.op & 0xFF) == 94) ? (PostExp)this : null;
        }

        // Erasure: isPreExp<>
        public  PreExp isPreExp() {
            return ((this.op & 0xFF) == 103) || ((this.op & 0xFF) == 104) ? (PreExp)this : null;
        }

        // Erasure: isAssignExp<>
        public  AssignExp isAssignExp() {
            return ((this.op & 0xFF) == 90) ? (AssignExp)this : null;
        }

        // Erasure: isConstructExp<>
        public  ConstructExp isConstructExp() {
            return ((this.op & 0xFF) == 95) ? (ConstructExp)this : null;
        }

        // Erasure: isBlitExp<>
        public  BlitExp isBlitExp() {
            return ((this.op & 0xFF) == 96) ? (BlitExp)this : null;
        }

        // Erasure: isAddAssignExp<>
        public  AddAssignExp isAddAssignExp() {
            return ((this.op & 0xFF) == 76) ? (AddAssignExp)this : null;
        }

        // Erasure: isMinAssignExp<>
        public  MinAssignExp isMinAssignExp() {
            return ((this.op & 0xFF) == 77) ? (MinAssignExp)this : null;
        }

        // Erasure: isMulAssignExp<>
        public  MulAssignExp isMulAssignExp() {
            return ((this.op & 0xFF) == 81) ? (MulAssignExp)this : null;
        }

        // Erasure: isDivAssignExp<>
        public  DivAssignExp isDivAssignExp() {
            return ((this.op & 0xFF) == 82) ? (DivAssignExp)this : null;
        }

        // Erasure: isModAssignExp<>
        public  ModAssignExp isModAssignExp() {
            return ((this.op & 0xFF) == 83) ? (ModAssignExp)this : null;
        }

        // Erasure: isAndAssignExp<>
        public  AndAssignExp isAndAssignExp() {
            return ((this.op & 0xFF) == 87) ? (AndAssignExp)this : null;
        }

        // Erasure: isOrAssignExp<>
        public  OrAssignExp isOrAssignExp() {
            return ((this.op & 0xFF) == 88) ? (OrAssignExp)this : null;
        }

        // Erasure: isXorAssignExp<>
        public  XorAssignExp isXorAssignExp() {
            return ((this.op & 0xFF) == 89) ? (XorAssignExp)this : null;
        }

        // Erasure: isPowAssignExp<>
        public  PowAssignExp isPowAssignExp() {
            return ((this.op & 0xFF) == 227) ? (PowAssignExp)this : null;
        }

        // Erasure: isShlAssignExp<>
        public  ShlAssignExp isShlAssignExp() {
            return ((this.op & 0xFF) == 66) ? (ShlAssignExp)this : null;
        }

        // Erasure: isShrAssignExp<>
        public  ShrAssignExp isShrAssignExp() {
            return ((this.op & 0xFF) == 67) ? (ShrAssignExp)this : null;
        }

        // Erasure: isUshrAssignExp<>
        public  UshrAssignExp isUshrAssignExp() {
            return ((this.op & 0xFF) == 69) ? (UshrAssignExp)this : null;
        }

        // Erasure: isCatAssignExp<>
        public  CatAssignExp isCatAssignExp() {
            return ((this.op & 0xFF) == 71) ? (CatAssignExp)this : null;
        }

        // Erasure: isCatElemAssignExp<>
        public  CatElemAssignExp isCatElemAssignExp() {
            return ((this.op & 0xFF) == 72) ? (CatElemAssignExp)this : null;
        }

        // Erasure: isCatDcharAssignExp<>
        public  CatDcharAssignExp isCatDcharAssignExp() {
            return ((this.op & 0xFF) == 73) ? (CatDcharAssignExp)this : null;
        }

        // Erasure: isAddExp<>
        public  AddExp isAddExp() {
            return ((this.op & 0xFF) == 74) ? (AddExp)this : null;
        }

        // Erasure: isMinExp<>
        public  MinExp isMinExp() {
            return ((this.op & 0xFF) == 75) ? (MinExp)this : null;
        }

        // Erasure: isCatExp<>
        public  CatExp isCatExp() {
            return ((this.op & 0xFF) == 70) ? (CatExp)this : null;
        }

        // Erasure: isMulExp<>
        public  MulExp isMulExp() {
            return ((this.op & 0xFF) == 78) ? (MulExp)this : null;
        }

        // Erasure: isDivExp<>
        public  DivExp isDivExp() {
            return ((this.op & 0xFF) == 79) ? (DivExp)this : null;
        }

        // Erasure: isModExp<>
        public  ModExp isModExp() {
            return ((this.op & 0xFF) == 80) ? (ModExp)this : null;
        }

        // Erasure: isPowExp<>
        public  PowExp isPowExp() {
            return ((this.op & 0xFF) == 226) ? (PowExp)this : null;
        }

        // Erasure: isShlExp<>
        public  ShlExp isShlExp() {
            return ((this.op & 0xFF) == 64) ? (ShlExp)this : null;
        }

        // Erasure: isShrExp<>
        public  ShrExp isShrExp() {
            return ((this.op & 0xFF) == 65) ? (ShrExp)this : null;
        }

        // Erasure: isUshrExp<>
        public  UshrExp isUshrExp() {
            return ((this.op & 0xFF) == 68) ? (UshrExp)this : null;
        }

        // Erasure: isAndExp<>
        public  AndExp isAndExp() {
            return ((this.op & 0xFF) == 84) ? (AndExp)this : null;
        }

        // Erasure: isOrExp<>
        public  OrExp isOrExp() {
            return ((this.op & 0xFF) == 85) ? (OrExp)this : null;
        }

        // Erasure: isXorExp<>
        public  XorExp isXorExp() {
            return ((this.op & 0xFF) == 86) ? (XorExp)this : null;
        }

        // Erasure: isLogicalExp<>
        public  LogicalExp isLogicalExp() {
            return ((this.op & 0xFF) == 101) || ((this.op & 0xFF) == 102) ? (LogicalExp)this : null;
        }

        // Erasure: isInExp<>
        public  InExp isInExp() {
            return ((this.op & 0xFF) == 175) ? (InExp)this : null;
        }

        // Erasure: isRemoveExp<>
        public  RemoveExp isRemoveExp() {
            return ((this.op & 0xFF) == 44) ? (RemoveExp)this : null;
        }

        // Erasure: isEqualExp<>
        public  EqualExp isEqualExp() {
            return ((this.op & 0xFF) == 58) || ((this.op & 0xFF) == 59) ? (EqualExp)this : null;
        }

        // Erasure: isIdentityExp<>
        public  IdentityExp isIdentityExp() {
            return ((this.op & 0xFF) == 60) || ((this.op & 0xFF) == 61) ? (IdentityExp)this : null;
        }

        // Erasure: isCondExp<>
        public  CondExp isCondExp() {
            return ((this.op & 0xFF) == 100) ? (CondExp)this : null;
        }

        // Erasure: isDefaultInitExp<>
        public  DefaultInitExp isDefaultInitExp() {
            return ((this.op & 0xFF) == 190) ? (DefaultInitExp)this : null;
        }

        // Erasure: isFileInitExp<>
        public  FileInitExp isFileInitExp() {
            return ((this.op & 0xFF) == 219) || ((this.op & 0xFF) == 220) ? (FileInitExp)this : null;
        }

        // Erasure: isLineInitExp<>
        public  LineInitExp isLineInitExp() {
            return ((this.op & 0xFF) == 218) ? (LineInitExp)this : null;
        }

        // Erasure: isModuleInitExp<>
        public  ModuleInitExp isModuleInitExp() {
            return ((this.op & 0xFF) == 221) ? (ModuleInitExp)this : null;
        }

        // Erasure: isFuncInitExp<>
        public  FuncInitExp isFuncInitExp() {
            return ((this.op & 0xFF) == 222) ? (FuncInitExp)this : null;
        }

        // Erasure: isPrettyFuncInitExp<>
        public  PrettyFuncInitExp isPrettyFuncInitExp() {
            return ((this.op & 0xFF) == 223) ? (PrettyFuncInitExp)this : null;
        }

        // Erasure: isClassReferenceExp<>
        public  ClassReferenceExp isClassReferenceExp() {
            return ((this.op & 0xFF) == 50) ? (ClassReferenceExp)this : null;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Expression() {}

        public abstract Expression copy();
    }
    public static class IntegerExp extends Expression
    {
        public long value = 0L;
        // Erasure: __ctor<Loc, long, Type>
        public  IntegerExp(Loc loc, long value, Type type) {
            super(loc, TOK.int64, 32);
            assert(type != null);
            if (!type.isscalar())
            {
                if (((type.ty & 0xFF) != ENUMTY.Terror))
                {
                    this.error(new BytePtr("integral constant must be scalar type, not `%s`"), type.toChars());
                }
                type = Type.terror;
            }
            this.type.value = type;
            this.value = normalize(type.toBasetype().ty, value);
        }

        // Erasure: __ctor<long>
        public  IntegerExp(long value) {
            super(Loc.initial, TOK.int64, 32);
            this.type.value = Type.tint32;
            this.value = (long)(int)value;
        }

        // Erasure: create<Loc, long, Type>
        public static IntegerExp create(Loc loc, long value, Type type) {
            return new IntegerExp(loc, value, type);
        }

        // Erasure: emplace<Ptr, Loc, long, Type>
        public static void emplace(Ptr<UnionExp> pue, Loc loc, long value, Type type) {
            (pue) = new UnionExp(new IntegerExp(loc, value, type));
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            {
                IntegerExp ne = ((Expression)o).isIntegerExp();
                if ((ne) != null)
                {
                    if (this.type.value.toHeadMutable().equals(ne.type.value.toHeadMutable()) && (this.value == ne.value))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        // Erasure: toInteger<>
        public  long toInteger() {
            return this.value = normalize(this.type.value.toBasetype().ty, this.value);
        }

        // Erasure: toReal<>
        public  double toReal() {
            byte ty = this.type.value.toBasetype().ty;
            long val = normalize(ty, this.value);
            this.value = val;
            return ((ty & 0xFF) == ENUMTY.Tuns64) ? (double)val : (double)(long)val;
        }

        // Erasure: toImaginary<>
        public  double toImaginary() {
            return CTFloat.zero;
        }

        // Erasure: toComplex<>
        public  complex_t toComplex() {
            return new complex_t(this.toReal());
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            boolean r = this.toInteger() != 0L;
            return result ? r : !r;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if (e == null)
            {
                e = this;
            }
            else if (!this.loc.isValid())
            {
                this.loc.opAssign(e.loc.copy());
            }
            e.error(new BytePtr("cannot modify constant `%s`"), e.toChars());
            return new ErrorExp();
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: getInteger<>
        public  long getInteger() {
            return this.value;
        }

        // Erasure: setInteger<long>
        public  void setInteger(long value) {
            this.value = normalize(this.type.value.toBasetype().ty, value);
        }

        // Erasure: normalize<byte, long>
        public static long normalize(byte ty, long value) {
            long result = 0L;
            {
                int __dispatch2 = 0;
                dispatched_2:
                do {
                    switch (__dispatch2 != 0 ? __dispatch2 : (ty & 0xFF))
                    {
                        case 30:
                            result = ((value != 0L) ? 1 : 0);
                            break;
                        case 13:
                            result = (long)(byte)value;
                            break;
                        case 31:
                        case 14:
                            result = (long)(byte)value;
                            break;
                        case 15:
                            result = (long)(int)value;
                            break;
                        case 32:
                        case 16:
                            __dispatch2 = 0;
                            result = (long)(int)value;
                            break;
                        case 17:
                            result = (long)(int)value;
                            break;
                        case 33:
                        case 18:
                            __dispatch2 = 0;
                            result = (long)(int)value;
                            break;
                        case 19:
                            result = (long)(long)value;
                            break;
                        case 20:
                            __dispatch2 = 0;
                            result = value;
                            break;
                        case 3:
                            if ((target.ptrsize == 8))
                            {
                                /*goto case*/{ __dispatch2 = 20; continue dispatched_2; }
                            }
                            if ((target.ptrsize == 4))
                            {
                                /*goto case*/{ __dispatch2 = 18; continue dispatched_2; }
                            }
                            if ((target.ptrsize == 2))
                            {
                                /*goto case*/{ __dispatch2 = 16; continue dispatched_2; }
                            }
                            throw new AssertionError("Unreachable code!");
                        default:
                        break;
                    }
                } while(__dispatch2 != 0);
            }
            return result;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return this;
        }

        // from template literal!(_356A192B7913B04C)
        // Erasure: literal_356A192B7913B04C<>
        public static IntegerExp literal_356A192B7913B04C() {
            if (expression.literaltheConstant == null)
            {
                expression.literaltheConstant = new IntegerExp(1L);
            }
            return expression.literaltheConstant;
        }


        // from template literal!(_7984B0A0E139CABA)
        // Erasure: literal_7984B0A0E139CABA<>
        public static IntegerExp literal_7984B0A0E139CABA() {
            if (expression.literaltheConstant == null)
            {
                expression.literaltheConstant = new IntegerExp(-1L);
            }
            return expression.literaltheConstant;
        }


        // from template literal!(_B6589FC6AB0DC82C)
        // Erasure: literal_B6589FC6AB0DC82C<>
        public static IntegerExp literal_B6589FC6AB0DC82C() {
            if (expression.literaltheConstant == null)
            {
                expression.literaltheConstant = new IntegerExp(0L);
            }
            return expression.literaltheConstant;
        }



        public IntegerExp() {}

        public IntegerExp copy() {
            IntegerExp that = new IntegerExp();
            that.value = this.value;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ErrorExp extends Expression
    {
        // Erasure: __ctor<>
        public  ErrorExp() {
            if ((global.errors == 0) && (global.gaggedErrors == 0))
            {
                this.error(new BytePtr("unknown, please file report on issues.dlang.org"));
            }
            super(Loc.initial, TOK.error, 24);
            this.type.value = Type.terror;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        public static ErrorExp errorexp = null;

        public ErrorExp copy() {
            ErrorExp that = new ErrorExp();
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VoidInitExp extends Expression
    {
        public VarDeclaration var = null;
        // Erasure: __ctor<VarDeclaration>
        public  VoidInitExp(VarDeclaration var) {
            super(var.loc, TOK.void_, 28);
            this.var = var;
            this.type.value = var.type;
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return new BytePtr("void");
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VoidInitExp() {}

        public VoidInitExp copy() {
            VoidInitExp that = new VoidInitExp();
            that.var = this.var;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class RealExp extends Expression
    {
        public double value = 0.0;
        // Erasure: __ctor<Loc, double, Type>
        public  RealExp(Loc loc, double value, Type type) {
            super(loc, TOK.float64, 40);
            this.value = value;
            this.type.value = type;
        }

        // Erasure: create<Loc, double, Type>
        public static RealExp create(Loc loc, double value, Type type) {
            return new RealExp(loc, value, type);
        }

        // Erasure: emplace<Ptr, Loc, double, Type>
        public static void emplace(Ptr<UnionExp> pue, Loc loc, double value, Type type) {
            (pue) = new UnionExp(new RealExp(loc, value, type));
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            {
                RealExp ne = ((Expression)o).isRealExp();
                if ((ne) != null)
                {
                    if (this.type.value.toHeadMutable().equals(ne.type.value.toHeadMutable()) && (RealIdentical(this.value, ne.value) != 0))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        // Erasure: toInteger<>
        public  long toInteger() {
            return (long)(long)this.toReal();
        }

        // Erasure: toUInteger<>
        public  long toUInteger() {
            return (long)this.toReal();
        }

        // Erasure: toReal<>
        public  double toReal() {
            return this.type.value.isreal() ? this.value : CTFloat.zero;
        }

        // Erasure: toImaginary<>
        public  double toImaginary() {
            return this.type.value.isreal() ? CTFloat.zero : this.value;
        }

        // Erasure: toComplex<>
        public  complex_t toComplex() {
            return new complex_t(this.toReal(), this.toImaginary());
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return result ? (this.value != 0) : !(this.value != 0);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public RealExp() {}

        public RealExp copy() {
            RealExp that = new RealExp();
            that.value = this.value;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ComplexExp extends Expression
    {
        public complex_t value = new complex_t();
        // Erasure: __ctor<Loc, complex_t, Type>
        public  ComplexExp(Loc loc, complex_t value, Type type) {
            super(loc, TOK.complex80, 56);
            this.value.opAssign(value.copy());
            this.type.value = type;
        }

        // Erasure: create<Loc, complex_t, Type>
        public static ComplexExp create(Loc loc, complex_t value, Type type) {
            return new ComplexExp(loc, value, type);
        }

        // Erasure: emplace<Ptr, Loc, complex_t, Type>
        public static void emplace(Ptr<UnionExp> pue, Loc loc, complex_t value, Type type) {
            (pue) = new UnionExp(new ComplexExp(loc, value, type));
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            {
                ComplexExp ne = ((Expression)o).isComplexExp();
                if ((ne) != null)
                {
                    if (this.type.value.toHeadMutable().equals(ne.type.value.toHeadMutable()) && (RealIdentical(creall(this.value), creall(ne.value)) != 0) && (RealIdentical(cimagl(this.value), cimagl(ne.value)) != 0))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        // Erasure: toInteger<>
        public  long toInteger() {
            return (long)(long)this.toReal();
        }

        // Erasure: toUInteger<>
        public  long toUInteger() {
            return (long)this.toReal();
        }

        // Erasure: toReal<>
        public  double toReal() {
            return creall(this.value);
        }

        // Erasure: toImaginary<>
        public  double toImaginary() {
            return cimagl(this.value);
        }

        // Erasure: toComplex<>
        public  complex_t toComplex() {
            return this.value;
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            if (result)
            {
                return this.value.opCastBoolean();
            }
            else
            {
                return !this.value.opCastBoolean();
            }
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ComplexExp() {}

        public ComplexExp copy() {
            ComplexExp that = new ComplexExp();
            that.value = this.value;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IdentifierExp extends Expression
    {
        public Identifier ident = null;
        // Erasure: __ctor<Loc, Identifier>
        public  IdentifierExp(Loc loc, Identifier ident) {
            super(loc, TOK.identifier, 28);
            this.ident = ident;
        }

        // Erasure: create<Loc, Identifier>
        public static IdentifierExp create(Loc loc, Identifier ident) {
            return new IdentifierExp(loc, ident);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IdentifierExp() {}

        public IdentifierExp copy() {
            IdentifierExp that = new IdentifierExp();
            that.ident = this.ident;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DollarExp extends IdentifierExp
    {
        // Erasure: __ctor<Loc>
        public  DollarExp(Loc loc) {
            super(loc, Id.dollar);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DollarExp() {}

        public DollarExp copy() {
            DollarExp that = new DollarExp();
            that.ident = this.ident;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DsymbolExp extends Expression
    {
        public Dsymbol s = null;
        public boolean hasOverloads = false;
        // Erasure: __ctor<Loc, Dsymbol, boolean>
        public  DsymbolExp(Loc loc, Dsymbol s, boolean hasOverloads) {
            super(loc, TOK.dSymbol, 29);
            this.s = s;
            this.hasOverloads = hasOverloads;
        }

        // defaulted all parameters starting with #3
        public  DsymbolExp(Loc loc, Dsymbol s) {
            this(loc, s, true);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DsymbolExp() {}

        public DsymbolExp copy() {
            DsymbolExp that = new DsymbolExp();
            that.s = this.s;
            that.hasOverloads = this.hasOverloads;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ThisExp extends Expression
    {
        public VarDeclaration var = null;
        // Erasure: __ctor<Loc>
        public  ThisExp(Loc loc) {
            super(loc, TOK.this_, 28);
        }

        // Erasure: __ctor<Loc, byte>
        public  ThisExp(Loc loc, byte tok) {
            super(loc, tok, 28);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            ThisExp r = (ThisExp)super.syntaxCopy();
            r.type.value = null;
            r.var = null;
            return r;
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return result;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return (this.type.value.toBasetype().ty & 0xFF) != ENUMTY.Tclass;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if (((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tclass))
            {
                return this.toLvalue(sc, e);
            }
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ThisExp() {}

        public ThisExp copy() {
            ThisExp that = new ThisExp();
            that.var = this.var;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SuperExp extends ThisExp
    {
        // Erasure: __ctor<Loc>
        public  SuperExp(Loc loc) {
            super(loc, TOK.super_);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SuperExp() {}

        public SuperExp copy() {
            SuperExp that = new SuperExp();
            that.var = this.var;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NullExp extends Expression
    {
        public byte committed = 0;
        // Erasure: __ctor<Loc, Type>
        public  NullExp(Loc loc, Type type) {
            super(loc, TOK.null_, 25);
            this.type.value = type;
        }

        // defaulted all parameters starting with #2
        public  NullExp(Loc loc) {
            this(loc, (Type)null);
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            {
                Expression e = isExpression(o);
                if ((e) != null)
                {
                    if (((e.op & 0xFF) == 13) && this.type.value.equals(e.type.value))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return result ? false : true;
        }

        // Erasure: toStringExp<>
        public  StringExp toStringExp() {
            if (this.implicitConvTo(Type.tstring) != 0)
            {
                StringExp se = new StringExp(this.loc, ptr(new byte[1]), 0);
                se.type.value = Type.tstring;
                return se;
            }
            return null;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NullExp() {}

        public NullExp copy() {
            NullExp that = new NullExp();
            that.committed = this.committed;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class StringExp extends Expression
    {
        public BytePtr string = null;
        public CharPtr wstring = null;
        public Ptr<Integer> dstring = null;
        public int len = 0;
        public byte sz = (byte)1;
        public byte committed = 0;
        public byte postfix = (byte)0;
        public byte ownedByCtfe = OwnedBy.code;
        // Erasure: __ctor<Loc, Ptr>
        public  StringExp(Loc loc, BytePtr string) {
            super(loc, TOK.string_, 36);
            this.string = pcopy(string);
            this.len = strlen(string);
            this.sz = (byte)1;
        }

        // Erasure: __ctor<Loc, Ptr, int>
        public  StringExp(Loc loc, Object string, int len) {
            super(loc, TOK.string_, 36);
            this.string = pcopy((((BytePtr)string)));
            this.len = len;
            this.sz = (byte)1;
        }

        // Erasure: __ctor<Loc, Ptr, int, byte>
        public  StringExp(Loc loc, Object string, int len, byte postfix) {
            super(loc, TOK.string_, 36);
            this.string = pcopy((((BytePtr)string)));
            this.len = len;
            this.postfix = postfix;
            this.sz = (byte)1;
        }

        // Erasure: create<Loc, Ptr>
        public static StringExp create(Loc loc, BytePtr s) {
            return new StringExp(loc, s);
        }

        // Erasure: create<Loc, Ptr, int>
        public static StringExp create(Loc loc, Object string, int len) {
            return new StringExp(loc, string, len);
        }

        // Erasure: emplace<Ptr, Loc, Ptr>
        public static void emplace(Ptr<UnionExp> pue, Loc loc, BytePtr s) {
            (pue) = new UnionExp(new StringExp(loc, s));
        }

        // Erasure: emplace<Ptr, Loc, Ptr, int>
        public static void emplace(Ptr<UnionExp> pue, Loc loc, Object string, int len) {
            (pue) = new UnionExp(new StringExp(loc, string, len));
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            {
                Expression e = isExpression(o);
                if ((e) != null)
                {
                    {
                        StringExp se = e.isStringExp();
                        if ((se) != null)
                        {
                            return this.comparex(se) == 0;
                        }
                    }
                }
            }
            return false;
        }

        // Erasure: numberOfCodeUnits<int>
        public  int numberOfCodeUnits(int tynto) {
            int encSize = 0;
            switch (tynto)
            {
                case 0:
                    return this.len;
                case 31:
                    encSize = 1;
                    break;
                case 32:
                    encSize = 2;
                    break;
                case 33:
                    encSize = 4;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            if (((this.sz & 0xFF) == encSize))
            {
                return this.len;
            }
            int result = 0;
            Ref<Integer> c = ref(0x0ffff);
            switch ((this.sz & 0xFF))
            {
                case 1:
                    {
                        Ref<Integer> u = ref(0);
                        for (; (u.value < this.len);){
                            {
                                BytePtr p = pcopy(utf_decodeChar(this.string, this.len, u, c));
                                if ((p) != null)
                                {
                                    this.error(new BytePtr("%s"), p);
                                    return 0;
                                }
                            }
                            result += utf_codeLength(encSize, c.value);
                        }
                    }
                    break;
                case 2:
                    {
                        Ref<Integer> u_1 = ref(0);
                        for (; (u_1.value < this.len);){
                            {
                                BytePtr p = pcopy(utf_decodeWchar(this.wstring, this.len, u_1, c));
                                if ((p) != null)
                                {
                                    this.error(new BytePtr("%s"), p);
                                    return 0;
                                }
                            }
                            result += utf_codeLength(encSize, c.value);
                        }
                    }
                    break;
                case 4:
                    {
                        int __key1336 = 0;
                        int __limit1337 = this.len;
                        for (; (__key1336 < __limit1337);__key1336 += 1) {
                            int u_2 = __key1336;
                            result += utf_codeLength(encSize, this.dstring.get(u_2));
                        }
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return result;
        }

        // defaulted all parameters starting with #1
        public  int numberOfCodeUnits() {
            return numberOfCodeUnits(0);
        }

        // Erasure: writeTo<Ptr, boolean, int>
        public  void writeTo(Object dest, boolean zero, int tyto) {
            int encSize = 0;
            switch (tyto)
            {
                case 0:
                    encSize = (this.sz & 0xFF);
                    break;
                case 31:
                    encSize = 1;
                    break;
                case 32:
                    encSize = 2;
                    break;
                case 33:
                    encSize = 4;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            if (((this.sz & 0xFF) == encSize))
            {
                memcpy((BytePtr)dest, (this.string), (this.len * (this.sz & 0xFF)));
                if (zero)
                {
                    memset(((BytePtr)dest).plus((this.len * (this.sz & 0xFF))), 0, (this.sz & 0xFF));
                }
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
        }

        // defaulted all parameters starting with #3
        public  void writeTo(Object dest, boolean zero) {
            writeTo(dest, zero, 0);
        }

        // Erasure: getCodeUnit<int>
        public  int getCodeUnit(int i) {
            assert((i < this.len));
            switch ((this.sz & 0xFF))
            {
                case 1:
                    return (this.string.get(i) & 0xFF);
                case 2:
                    return (int)this.wstring.get(i);
                case 4:
                    return this.dstring.get(i);
                default:
                throw SwitchError.INSTANCE;
            }
        }

        // Erasure: setCodeUnit<int, int>
        public  void setCodeUnit(int i, int c) {
            assert((i < this.len));
            switch ((this.sz & 0xFF))
            {
                case 1:
                    this.string.set(i, (byte)c);
                    break;
                case 2:
                    this.wstring.set(i, (char)c);
                    break;
                case 4:
                    this.dstring.set(i, c);
                    break;
                default:
                throw SwitchError.INSTANCE;
            }
        }

        // Erasure: toPtr<>
        public  BytePtr toPtr() {
            return ((this.sz & 0xFF) == 1) ? this.string : null;
        }

        // Erasure: toStringExp<>
        public  StringExp toStringExp() {
            return this;
        }

        // Erasure: toUTF8<Ptr>
        public  StringExp toUTF8(Ptr<Scope> sc) {
            if (((this.sz & 0xFF) != 1))
            {
                this.committed = (byte)0;
                Expression e = this.castTo(sc, Type.tchar.arrayOf());
                e = e.optimize(0, false);
                StringExp se = e.isStringExp();
                assert(((se.sz & 0xFF) == 1));
                return se;
            }
            return this;
        }

        // Erasure: comparex<StringExp>
        public  int comparex(StringExp se2) {
            int len1 = this.len;
            int len2 = se2.len;
            if ((len1 == len2))
            {
                switch ((this.sz & 0xFF))
                {
                    case 1:
                        return memcmp(this.string, se2.string, len1);
                    case 2:
                        {
                            CharPtr s1 = pcopy(toCharPtr(this.string));
                            CharPtr s2_1 = pcopy(toCharPtr(se2.string));
                            {
                                int __key1338 = 0;
                                int __limit1339 = this.len;
                                for (; (__key1338 < __limit1339);__key1338 += 1) {
                                    int u = __key1338;
                                    if (((int)s1.get(u) != (int)s2_1.get(u)))
                                    {
                                        return (int)s1.get(u) - (int)s2_1.get(u);
                                    }
                                }
                            }
                        }
                        break;
                    case 4:
                        {
                            Ptr<Integer> s1_1 = pcopy(toPtr<Integer>(this.string));
                            Ptr<Integer> s2 = pcopy(toPtr<Integer>(se2.string));
                            {
                                int __key1340 = 0;
                                int __limit1341 = this.len;
                                for (; (__key1340 < __limit1341);__key1340 += 1) {
                                    int u_1 = __key1340;
                                    if ((s1_1.get(u_1) != s2.get(u_1)))
                                    {
                                        return (s1_1.get(u_1) - s2.get(u_1));
                                    }
                                }
                            }
                        }
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
            return (len1 - len2);
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return result;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return (this.type.value != null) && ((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray);
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return (this.type.value != null) && ((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) ? this : this.toLvalue(sc, e);
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            this.error(new BytePtr("cannot modify string literal `%s`"), this.toChars());
            return new ErrorExp();
        }

        // Erasure: charAt<long>
        public  int charAt(long i) {
            int value = 0;
            switch ((this.sz & 0xFF))
            {
                case 1:
                    value = (this.string.get((int)i) & 0xFF);
                    break;
                case 2:
                    value = (int)(toPtr<Integer>(this.string)).get((int)i);
                    break;
                case 4:
                    value = (toPtr<Integer>(this.string)).get((int)i);
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return value;
        }

        // Erasure: toStringz<>
        public  ByteSlice toStringz() {
            int nbytes = this.len * (this.sz & 0xFF);
            BytePtr s = pcopy(((BytePtr)Mem.xmalloc(nbytes + (this.sz & 0xFF))));
            this.writeTo(s, true, 0);
            return s.slice(0,nbytes);
        }

        // Erasure: peekSlice<>
        public  ByteSlice peekSlice() {
            assert(((this.sz & 0xFF) == 1));
            return this.string.slice(0,this.len);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StringExp() {}

        public StringExp copy() {
            StringExp that = new StringExp();
            that.string = this.string;
            that.wstring = this.wstring;
            that.dstring = this.dstring;
            that.len = this.len;
            that.sz = this.sz;
            that.committed = this.committed;
            that.postfix = this.postfix;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TupleExp extends Expression
    {
        public Ref<Expression> e0 = ref(null);
        public Ptr<DArray<Expression>> exps = null;
        // Erasure: __ctor<Loc, Expression, Ptr>
        public  TupleExp(Loc loc, Expression e0, Ptr<DArray<Expression>> exps) {
            super(loc, TOK.tuple, 32);
            this.e0.value = e0;
            this.exps = pcopy(exps);
        }

        // Erasure: __ctor<Loc, Ptr>
        public  TupleExp(Loc loc, Ptr<DArray<Expression>> exps) {
            super(loc, TOK.tuple, 32);
            this.exps = pcopy(exps);
        }

        // Erasure: __ctor<Loc, TupleDeclaration>
        public  TupleExp(Loc loc, TupleDeclaration tup) {
            super(loc, TOK.tuple, 32);
            this.exps = pcopy((refPtr(new DArray<Expression>())));
            (this.exps.get()).reserve((tup.objects.get()).length);
            {
                Slice<RootObject> __r1342 = (tup.objects.get()).opSlice().copy();
                int __key1343 = 0;
                for (; (__key1343 < __r1342.getLength());__key1343 += 1) {
                    RootObject o = __r1342.get(__key1343);
                    {
                        Dsymbol s = getDsymbol(o);
                        if ((s) != null)
                        {
                            Expression e = new DsymbolExp(loc, s, true);
                            (this.exps.get()).push(e);
                        }
                        else {
                            Expression eo = isExpression(o);
                            if ((eo) != null)
                            {
                                Expression e = eo.copy();
                                e.loc.opAssign(loc.copy());
                                (this.exps.get()).push(e);
                            }
                            else {
                                Type t = isType(o);
                                if ((t) != null)
                                {
                                    Expression e = new TypeExp(loc, t);
                                    (this.exps.get()).push(e);
                                }
                                else
                                {
                                    this.error(new BytePtr("`%s` is not an expression"), o.toChars());
                                }
                            }
                        }
                    }
                }
            }
        }

        // Erasure: create<Loc, Ptr>
        public static TupleExp create(Loc loc, Ptr<DArray<Expression>> exps) {
            return new TupleExp(loc, exps);
        }

        // Erasure: toTupleExp<>
        public  TupleExp toTupleExp() {
            return this;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new TupleExp(this.loc, this.e0.value != null ? this.e0.value.syntaxCopy() : null, Expression.arraySyntaxCopy(this.exps));
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            {
                Expression e = isExpression(o);
                if ((e) != null)
                {
                    {
                        TupleExp te = e.isTupleExp();
                        if ((te) != null)
                        {
                            if (((this.exps.get()).length != (te.exps.get()).length))
                            {
                                return false;
                            }
                            if ((this.e0.value != null) && !this.e0.value.equals(te.e0.value) || (this.e0.value == null) && (te.e0.value != null))
                            {
                                return false;
                            }
                            {
                                Slice<Expression> __r1345 = (this.exps.get()).opSlice().copy();
                                int __key1344 = 0;
                                for (; (__key1344 < __r1345.getLength());__key1344 += 1) {
                                    Expression e1 = __r1345.get(__key1344);
                                    int i = __key1344;
                                    Expression e2 = (te.exps.get()).get(i);
                                    if (!e1.equals(e2))
                                    {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TupleExp() {}

        public TupleExp copy() {
            TupleExp that = new TupleExp();
            that.e0 = this.e0;
            that.exps = this.exps;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ArrayLiteralExp extends Expression
    {
        public Ref<Expression> basis = ref(null);
        public Ptr<DArray<Expression>> elements = null;
        public byte ownedByCtfe = OwnedBy.code;
        // Erasure: __ctor<Loc, Type, Ptr>
        public  ArrayLiteralExp(Loc loc, Type type, Ptr<DArray<Expression>> elements) {
            super(loc, TOK.arrayLiteral, 33);
            this.type.value = type;
            this.elements = pcopy(elements);
        }

        // Erasure: __ctor<Loc, Type, Expression>
        public  ArrayLiteralExp(Loc loc, Type type, Expression e) {
            super(loc, TOK.arrayLiteral, 33);
            this.type.value = type;
            this.elements = pcopy((refPtr(new DArray<Expression>())));
            (this.elements.get()).push(e);
        }

        // Erasure: __ctor<Loc, Type, Expression, Ptr>
        public  ArrayLiteralExp(Loc loc, Type type, Expression basis, Ptr<DArray<Expression>> elements) {
            super(loc, TOK.arrayLiteral, 33);
            this.type.value = type;
            this.basis.value = basis;
            this.elements = pcopy(elements);
        }

        // Erasure: create<Loc, Ptr>
        public static ArrayLiteralExp create(Loc loc, Ptr<DArray<Expression>> elements) {
            return new ArrayLiteralExp(loc, null, elements);
        }

        // Erasure: emplace<Ptr, Loc, Ptr>
        public static void emplace(Ptr<UnionExp> pue, Loc loc, Ptr<DArray<Expression>> elements) {
            (pue) = new UnionExp(new ArrayLiteralExp(loc, null, elements));
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new ArrayLiteralExp(this.loc, null, this.basis.value != null ? this.basis.value.syntaxCopy() : null, Expression.arraySyntaxCopy(this.elements));
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            Expression e = isExpression(o);
            if (e == null)
            {
                return false;
            }
            {
                ArrayLiteralExp ae = e.isArrayLiteralExp();
                if ((ae) != null)
                {
                    if (((this.elements.get()).length != (ae.elements.get()).length))
                    {
                        return false;
                    }
                    if (((this.elements.get()).length == 0) && !this.type.value.equals(ae.type.value))
                    {
                        return false;
                    }
                    {
                        Slice<Expression> __r1347 = (this.elements.get()).opSlice().copy();
                        int __key1346 = 0;
                        for (; (__key1346 < __r1347.getLength());__key1346 += 1) {
                            Expression e1 = __r1347.get(__key1346);
                            int i = __key1346;
                            Expression e2 = (ae.elements.get()).get(i);
                            if (e1 == null)
                            {
                                e1 = this.basis.value;
                            }
                            if (e2 == null)
                            {
                                e2 = ae.basis.value;
                            }
                            if ((!pequals(e1, e2)) && (e1 == null) || (e2 == null) || !e1.equals(e2))
                            {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        // Erasure: getElement<int>
        public  Expression getElement(int i) {
            Expression el = (this.elements.get()).get(i);
            return el != null ? el : this.basis.value;
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            int dim = this.elements != null ? (this.elements.get()).length : 0;
            return result ? dim != 0 : dim == 0;
        }

        // Erasure: toStringExp<>
        public  StringExp toStringExp() {
            byte telem = this.type.value.nextOf().toBasetype().ty;
            if (((telem & 0xFF) == ENUMTY.Tchar) || ((telem & 0xFF) == ENUMTY.Twchar) || ((telem & 0xFF) == ENUMTY.Tdchar) || ((telem & 0xFF) == ENUMTY.Tvoid) && (this.elements == null) || ((this.elements.get()).length == 0))
            {
                byte sz = (byte)1;
                if (((telem & 0xFF) == ENUMTY.Twchar))
                {
                    sz = (byte)2;
                }
                else if (((telem & 0xFF) == ENUMTY.Tdchar))
                {
                    sz = (byte)4;
                }
                OutBuffer buf = new OutBuffer();
                try {
                    if (this.elements != null)
                    {
                        {
                            int __key1348 = 0;
                            int __limit1349 = (this.elements.get()).length;
                            for (; (__key1348 < __limit1349);__key1348 += 1) {
                                int i = __key1348;
                                Expression ch = this.getElement(i);
                                if (((ch.op & 0xFF) != 135))
                                {
                                    return null;
                                }
                                if (((sz & 0xFF) == 1))
                                {
                                    buf.writeByte((int)ch.toInteger());
                                }
                                else if (((sz & 0xFF) == 2))
                                {
                                    buf.writeword((int)ch.toInteger());
                                }
                                else
                                {
                                    buf.write4((int)ch.toInteger());
                                }
                            }
                        }
                    }
                    byte prefix = (byte)255;
                    if (((sz & 0xFF) == 1))
                    {
                        prefix = (byte)99;
                        buf.writeByte(0);
                    }
                    else if (((sz & 0xFF) == 2))
                    {
                        prefix = (byte)119;
                        buf.writeword(0);
                    }
                    else
                    {
                        prefix = (byte)100;
                        buf.write4(0);
                    }
                    int len = buf.offset / (sz & 0xFF) - 1;
                    StringExp se = new StringExp(this.loc, buf.extractData(), len, prefix);
                    se.sz = sz;
                    se.type.value = this.type.value;
                    return se;
                }
                finally {
                }
            }
            return null;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayLiteralExp() {}

        public ArrayLiteralExp copy() {
            ArrayLiteralExp that = new ArrayLiteralExp();
            that.basis = this.basis;
            that.elements = this.elements;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AssocArrayLiteralExp extends Expression
    {
        public Ptr<DArray<Expression>> keys = null;
        public Ptr<DArray<Expression>> values = null;
        public byte ownedByCtfe = OwnedBy.code;
        // Erasure: __ctor<Loc, Ptr, Ptr>
        public  AssocArrayLiteralExp(Loc loc, Ptr<DArray<Expression>> keys, Ptr<DArray<Expression>> values) {
            super(loc, TOK.assocArrayLiteral, 33);
            assert(((keys.get()).length == (values.get()).length));
            this.keys = pcopy(keys);
            this.values = pcopy(values);
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            Expression e = isExpression(o);
            if (e == null)
            {
                return false;
            }
            {
                AssocArrayLiteralExp ae = e.isAssocArrayLiteralExp();
                if ((ae) != null)
                {
                    if (((this.keys.get()).length != (ae.keys.get()).length))
                    {
                        return false;
                    }
                    int count = 0;
                    {
                        Slice<Expression> __r1351 = (this.keys.get()).opSlice().copy();
                        int __key1350 = 0;
                        for (; (__key1350 < __r1351.getLength());__key1350 += 1) {
                            Expression key = __r1351.get(__key1350);
                            int i = __key1350;
                            {
                                Slice<Expression> __r1353 = (ae.keys.get()).opSlice().copy();
                                int __key1352 = 0;
                                for (; (__key1352 < __r1353.getLength());__key1352 += 1) {
                                    Expression akey = __r1353.get(__key1352);
                                    int j = __key1352;
                                    if (key.equals(akey))
                                    {
                                        if (!(this.values.get()).get(i).equals((ae.values.get()).get(j)))
                                        {
                                            return false;
                                        }
                                        count += 1;
                                    }
                                }
                            }
                        }
                    }
                    return count == (this.keys.get()).length;
                }
            }
            return false;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new AssocArrayLiteralExp(this.loc, Expression.arraySyntaxCopy(this.keys), Expression.arraySyntaxCopy(this.values));
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            int dim = (this.keys.get()).length;
            return result ? dim != 0 : dim == 0;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AssocArrayLiteralExp() {}

        public AssocArrayLiteralExp copy() {
            AssocArrayLiteralExp that = new AssocArrayLiteralExp();
            that.keys = this.keys;
            that.values = this.values;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    static int stageScrub = 1;
    static int stageSearchPointers = 2;
    static int stageOptimize = 4;
    static int stageApply = 8;
    static int stageInlineScan = 16;
    static int stageToCBuffer = 32;
    public static class StructLiteralExp extends Expression
    {
        public StructDeclaration sd = null;
        public Ptr<DArray<Expression>> elements = null;
        public Type stype = null;
        public Ptr<Symbol> sym = null;
        public StructLiteralExp origin = null;
        public StructLiteralExp inlinecopy = null;
        public int stageflags = 0;
        public boolean useStaticInit = false;
        public byte ownedByCtfe = OwnedBy.code;
        // Erasure: __ctor<Loc, StructDeclaration, Ptr, Type>
        public  StructLiteralExp(Loc loc, StructDeclaration sd, Ptr<DArray<Expression>> elements, Type stype) {
            super(loc, TOK.structLiteral, 54);
            this.sd = sd;
            if (elements == null)
            {
                elements = pcopy((refPtr(new DArray<Expression>())));
            }
            this.elements = pcopy(elements);
            this.stype = stype;
            this.origin = this;
        }

        // defaulted all parameters starting with #4
        public  StructLiteralExp(Loc loc, StructDeclaration sd, Ptr<DArray<Expression>> elements) {
            this(loc, sd, elements, (Type)null);
        }

        // Erasure: create<Loc, StructDeclaration, Ptr, Type>
        public static StructLiteralExp create(Loc loc, StructDeclaration sd, Object elements, Type stype) {
            return new StructLiteralExp(loc, sd, ((Ptr<DArray<Expression>>)elements), stype);
        }

        // defaulted all parameters starting with #4
        public static StructLiteralExp create(Loc loc, StructDeclaration sd, Object elements) {
            return create(loc, sd, elements, (Type)null);
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            Expression e = isExpression(o);
            if (e == null)
            {
                return false;
            }
            {
                StructLiteralExp se = e.isStructLiteralExp();
                if ((se) != null)
                {
                    if (!this.type.value.equals(se.type.value))
                    {
                        return false;
                    }
                    if (((this.elements.get()).length != (se.elements.get()).length))
                    {
                        return false;
                    }
                    {
                        Slice<Expression> __r1355 = (this.elements.get()).opSlice().copy();
                        int __key1354 = 0;
                        for (; (__key1354 < __r1355.getLength());__key1354 += 1) {
                            Expression e1 = __r1355.get(__key1354);
                            int i = __key1354;
                            Expression e2 = (se.elements.get()).get(i);
                            if ((!pequals(e1, e2)) && (e1 == null) || (e2 == null) || !e1.equals(e2))
                            {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            StructLiteralExp exp = new StructLiteralExp(this.loc, this.sd, Expression.arraySyntaxCopy(this.elements), this.type.value != null ? this.type.value : this.stype);
            exp.origin = this;
            return exp;
        }

        // Erasure: getField<Type, int>
        public  Expression getField(Type type, int offset) {
            Expression e = null;
            int i = this.getFieldIndex(type, offset);
            if ((i != -1))
            {
                if ((i >= this.sd.nonHiddenFields()))
                {
                    return null;
                }
                assert((i < (this.elements.get()).length));
                e = (this.elements.get()).get(i);
                if (e != null)
                {
                    TypeSArray tsa = type.isTypeSArray();
                    if ((tsa != null) && (!pequals(e.type.value.castMod((byte)0), type.castMod((byte)0))))
                    {
                        int length = (int)tsa.dim.toInteger();
                        Ptr<DArray<Expression>> z = refPtr(new DArray<Expression>(length));
                        {
                            Slice<Expression> __r1356 = (z.get()).opSlice().copy();
                            int __key1357 = 0;
                            for (; (__key1357 < __r1356.getLength());__key1357 += 1) {
                                Expression q = __r1356.get(__key1357);
                                q = e.copy();
                            }
                        }
                        e = new ArrayLiteralExp(this.loc, type, z);
                    }
                    else
                    {
                        e = e.copy();
                        e.type.value = type;
                    }
                    if (this.useStaticInit && e.type.value.needsNested())
                    {
                        {
                            StructLiteralExp se = e.isStructLiteralExp();
                            if ((se) != null)
                            {
                                se.useStaticInit = true;
                            }
                        }
                    }
                }
            }
            return e;
        }

        // Erasure: getFieldIndex<Type, int>
        public  int getFieldIndex(Type type, int offset) {
            if ((this.elements.get()).length != 0)
            {
                {
                    Slice<VarDeclaration> __r1359 = this.sd.fields.opSlice().copy();
                    int __key1358 = 0;
                    for (; (__key1358 < __r1359.getLength());__key1358 += 1) {
                        VarDeclaration v = __r1359.get(__key1358);
                        int i = __key1358;
                        if ((offset == v.offset) && (type.size() == v.type.size()))
                        {
                            if ((i >= this.sd.nonHiddenFields()))
                            {
                                return i;
                            }
                            {
                                Expression e = (this.elements.get()).get(i);
                                if ((e) != null)
                                {
                                    return i;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            return -1;
        }

        // Erasure: addDtorHook<Ptr>
        public  Expression addDtorHook(Ptr<Scope> sc) {
            if ((this.sd.dtor != null) && ((sc.get()).func != null))
            {
                int len = 10;
                ByteSlice buf = new ByteSlice(new byte[11]);
                buf.set(10, (byte)0);
                strcpy(buf.ptr(), new BytePtr("__sl"));
                strncat(buf.ptr(), this.sd.ident.toChars(), 5);
                assert(((buf.get(10) & 0xFF) == 0));
                VarDeclaration tmp = copyToTemp(0L, buf.ptr(), this);
                Expression ae = new DeclarationExp(this.loc, tmp);
                Expression e = new CommaExp(this.loc, ae, new VarExp(this.loc, tmp, true), true);
                e = expressionSemantic(e, sc);
                return e;
            }
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StructLiteralExp() {}

        public StructLiteralExp copy() {
            StructLiteralExp that = new StructLiteralExp();
            that.sd = this.sd;
            that.elements = this.elements;
            that.stype = this.stype;
            that.sym = this.sym;
            that.origin = this.origin;
            that.inlinecopy = this.inlinecopy;
            that.stageflags = this.stageflags;
            that.useStaticInit = this.useStaticInit;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TypeExp extends Expression
    {
        // Erasure: __ctor<Loc, Type>
        public  TypeExp(Loc loc, Type type) {
            super(loc, TOK.type, 24);
            this.type.value = type;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new TypeExp(this.loc, this.type.value.syntaxCopy());
        }

        // Erasure: checkType<>
        public  boolean checkType() {
            this.error(new BytePtr("type `%s` is not an expression"), this.toChars());
            return true;
        }

        // Erasure: checkValue<>
        public  boolean checkValue() {
            this.error(new BytePtr("type `%s` has no value"), this.toChars());
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeExp() {}

        public TypeExp copy() {
            TypeExp that = new TypeExp();
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ScopeExp extends Expression
    {
        public ScopeDsymbol sds = null;
        // Erasure: __ctor<Loc, ScopeDsymbol>
        public  ScopeExp(Loc loc, ScopeDsymbol sds) {
            super(loc, TOK.scope_, 28);
            this.sds = sds;
            assert(sds.isTemplateDeclaration() == null);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new ScopeExp(this.loc, (ScopeDsymbol)this.sds.syntaxCopy(null));
        }

        // Erasure: checkType<>
        public  boolean checkType() {
            if (this.sds.isPackage() != null)
            {
                this.error(new BytePtr("%s `%s` has no type"), this.sds.kind(), this.sds.toChars());
                return true;
            }
            {
                TemplateInstance ti = this.sds.isTemplateInstance();
                if ((ti) != null)
                {
                    if ((ti.tempdecl != null) && ti.semantictiargsdone && (ti.semanticRun == PASS.init))
                    {
                        this.error(new BytePtr("partial %s `%s` has no type"), this.sds.kind(), this.toChars());
                        return true;
                    }
                }
            }
            return false;
        }

        // Erasure: checkValue<>
        public  boolean checkValue() {
            this.error(new BytePtr("%s `%s` has no value"), this.sds.kind(), this.sds.toChars());
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ScopeExp() {}

        public ScopeExp copy() {
            ScopeExp that = new ScopeExp();
            that.sds = this.sds;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TemplateExp extends Expression
    {
        public TemplateDeclaration td = null;
        public FuncDeclaration fd = null;
        // Erasure: __ctor<Loc, TemplateDeclaration, FuncDeclaration>
        public  TemplateExp(Loc loc, TemplateDeclaration td, FuncDeclaration fd) {
            super(loc, TOK.template_, 32);
            this.td = td;
            this.fd = fd;
        }

        // defaulted all parameters starting with #3
        public  TemplateExp(Loc loc, TemplateDeclaration td) {
            this(loc, td, (FuncDeclaration)null);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return this.fd != null;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if (this.fd == null)
            {
                return this.toLvalue(sc, e);
            }
            assert(sc != null);
            return symbolToExp(this.fd, this.loc, sc, true);
        }

        // Erasure: checkType<>
        public  boolean checkType() {
            this.error(new BytePtr("%s `%s` has no type"), this.td.kind(), this.toChars());
            return true;
        }

        // Erasure: checkValue<>
        public  boolean checkValue() {
            this.error(new BytePtr("%s `%s` has no value"), this.td.kind(), this.toChars());
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateExp() {}

        public TemplateExp copy() {
            TemplateExp that = new TemplateExp();
            that.td = this.td;
            that.fd = this.fd;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NewExp extends Expression
    {
        public Ref<Expression> thisexp = ref(null);
        public Ptr<DArray<Expression>> newargs = null;
        public Type newtype = null;
        public Ptr<DArray<Expression>> arguments = null;
        public Ref<Expression> argprefix = ref(null);
        public CtorDeclaration member = null;
        public NewDeclaration allocator = null;
        public boolean onstack = false;
        public boolean thrownew = false;
        // Erasure: __ctor<Loc, Expression, Ptr, Type, Ptr>
        public  NewExp(Loc loc, Expression thisexp, Ptr<DArray<Expression>> newargs, Type newtype, Ptr<DArray<Expression>> arguments) {
            super(loc, TOK.new_, 54);
            this.thisexp.value = thisexp;
            this.newargs = pcopy(newargs);
            this.newtype = newtype;
            this.arguments = pcopy(arguments);
        }

        // Erasure: create<Loc, Expression, Ptr, Type, Ptr>
        public static NewExp create(Loc loc, Expression thisexp, Ptr<DArray<Expression>> newargs, Type newtype, Ptr<DArray<Expression>> arguments) {
            return new NewExp(loc, thisexp, newargs, newtype, arguments);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new NewExp(this.loc, this.thisexp.value != null ? this.thisexp.value.syntaxCopy() : null, Expression.arraySyntaxCopy(this.newargs), this.newtype.syntaxCopy(), Expression.arraySyntaxCopy(this.arguments));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NewExp() {}

        public NewExp copy() {
            NewExp that = new NewExp();
            that.thisexp = this.thisexp;
            that.newargs = this.newargs;
            that.newtype = this.newtype;
            that.arguments = this.arguments;
            that.argprefix = this.argprefix;
            that.member = this.member;
            that.allocator = this.allocator;
            that.onstack = this.onstack;
            that.thrownew = this.thrownew;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NewAnonClassExp extends Expression
    {
        public Expression thisexp = null;
        public Ptr<DArray<Expression>> newargs = null;
        public ClassDeclaration cd = null;
        public Ptr<DArray<Expression>> arguments = null;
        // Erasure: __ctor<Loc, Expression, Ptr, ClassDeclaration, Ptr>
        public  NewAnonClassExp(Loc loc, Expression thisexp, Ptr<DArray<Expression>> newargs, ClassDeclaration cd, Ptr<DArray<Expression>> arguments) {
            super(loc, TOK.newAnonymousClass, 40);
            this.thisexp = thisexp;
            this.newargs = pcopy(newargs);
            this.cd = cd;
            this.arguments = pcopy(arguments);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new NewAnonClassExp(this.loc, this.thisexp != null ? this.thisexp.syntaxCopy() : null, Expression.arraySyntaxCopy(this.newargs), (ClassDeclaration)this.cd.syntaxCopy(null), Expression.arraySyntaxCopy(this.arguments));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NewAnonClassExp() {}

        public NewAnonClassExp copy() {
            NewAnonClassExp that = new NewAnonClassExp();
            that.thisexp = this.thisexp;
            that.newargs = this.newargs;
            that.cd = this.cd;
            that.arguments = this.arguments;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SymbolExp extends Expression
    {
        public Declaration var = null;
        public boolean hasOverloads = false;
        public Dsymbol originalScope = null;
        // Erasure: __ctor<Loc, byte, int, Declaration, boolean>
        public  SymbolExp(Loc loc, byte op, int size, Declaration var, boolean hasOverloads) {
            super(loc, op, size);
            assert(var != null);
            this.var = var;
            this.hasOverloads = hasOverloads;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SymbolExp() {}

        public SymbolExp copy() {
            SymbolExp that = new SymbolExp();
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.originalScope = this.originalScope;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SymOffExp extends SymbolExp
    {
        public long offset = 0L;
        // Erasure: __ctor<Loc, Declaration, long, boolean>
        public  SymOffExp(Loc loc, Declaration var, long offset, boolean hasOverloads) {
            {
                VarDeclaration v = var.isVarDeclaration();
                if ((v) != null)
                {
                    if (v.needThis())
                    {
                        error(loc, new BytePtr("need `this` for address of `%s`"), v.toChars());
                    }
                    hasOverloads = false;
                }
            }
            super(loc, TOK.symbolOffset, 44, var, hasOverloads);
            this.offset = offset;
        }

        // defaulted all parameters starting with #4
        public  SymOffExp(Loc loc, Declaration var, long offset) {
            this(loc, var, offset, true);
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return result ? true : false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SymOffExp() {}

        public SymOffExp copy() {
            SymOffExp that = new SymOffExp();
            that.offset = this.offset;
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.originalScope = this.originalScope;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VarExp extends SymbolExp
    {
        // Erasure: __ctor<Loc, Declaration, boolean>
        public  VarExp(Loc loc, Declaration var, boolean hasOverloads) {
            if (var.isVarDeclaration() != null)
            {
                hasOverloads = false;
            }
            super(loc, TOK.variable, 36, var, hasOverloads);
            this.type.value = var.type;
        }

        // defaulted all parameters starting with #3
        public  VarExp(Loc loc, Declaration var) {
            this(loc, var, true);
        }

        // Erasure: create<Loc, Declaration, boolean>
        public static VarExp create(Loc loc, Declaration var, boolean hasOverloads) {
            return new VarExp(loc, var, hasOverloads);
        }

        // defaulted all parameters starting with #3
        public static VarExp create(Loc loc, Declaration var) {
            return create(loc, var, true);
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            {
                VarExp ne = isExpression(o).isVarExp();
                if ((ne) != null)
                {
                    if (this.type.value.toHeadMutable().equals(ne.type.value.toHeadMutable()) && (pequals(this.var, ne.var)))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            assert(this.type.value != null);
            return this.var.checkModify(this.loc, sc, null, flag);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            if ((this.var.storage_class & 2199031652352L) != 0)
            {
                return false;
            }
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if ((this.var.storage_class & 8388608L) != 0)
            {
                this.error(new BytePtr("manifest constant `%s` cannot be modified"), this.var.toChars());
                return new ErrorExp();
            }
            if ((this.var.storage_class & 8192L) != 0)
            {
                this.error(new BytePtr("lazy variable `%s` cannot be modified"), this.var.toChars());
                return new ErrorExp();
            }
            if ((pequals(this.var.ident, Id.ctfe)))
            {
                this.error(new BytePtr("cannot modify compiler-generated variable `__ctfe`"));
                return new ErrorExp();
            }
            if ((pequals(this.var.ident, Id.dollar)))
            {
                this.error(new BytePtr("cannot modify operator `$`"));
                return new ErrorExp();
            }
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            if ((this.var.storage_class & 8388608L) != 0)
            {
                this.error(new BytePtr("cannot modify manifest constant `%s`"), this.toChars());
                return new ErrorExp();
            }
            return this.modifiableLvalue(sc, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            Expression ret = super.syntaxCopy();
            return ret;
        }


        public VarExp() {}

        public VarExp copy() {
            VarExp that = new VarExp();
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.originalScope = this.originalScope;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class OverExp extends Expression
    {
        public OverloadSet vars = null;
        // Erasure: __ctor<Loc, OverloadSet>
        public  OverExp(Loc loc, OverloadSet s) {
            super(loc, TOK.overloadSet, 28);
            this.vars = s;
            this.type.value = Type.tvoid;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OverExp() {}

        public OverExp copy() {
            OverExp that = new OverExp();
            that.vars = this.vars;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class FuncExp extends Expression
    {
        public FuncLiteralDeclaration fd = null;
        public TemplateDeclaration td = null;
        public byte tok = 0;
        // Erasure: __ctor<Loc, Dsymbol>
        public  FuncExp(Loc loc, Dsymbol s) {
            super(loc, TOK.function_, 33);
            this.td = s.isTemplateDeclaration();
            this.fd = s.isFuncLiteralDeclaration();
            if (this.td != null)
            {
                assert(this.td.literal);
                assert((this.td.members != null) && ((this.td.members.get()).length == 1));
                this.fd = (this.td.members.get()).get(0).isFuncLiteralDeclaration();
            }
            this.tok = this.fd.tok;
            assert(this.fd.fbody.value != null);
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            Expression e = isExpression(o);
            if (e == null)
            {
                return false;
            }
            {
                FuncExp fe = e.isFuncExp();
                if ((fe) != null)
                {
                    return pequals(this.fd, fe.fd);
                }
            }
            return false;
        }

        // Erasure: genIdent<Ptr>
        public  void genIdent(Ptr<Scope> sc) {
            if ((pequals(this.fd.ident, Id.empty)))
            {
                BytePtr s = null;
                if (this.fd.fes != null)
                {
                    s = pcopy(new BytePtr("__foreachbody"));
                }
                else if (((this.fd.tok & 0xFF) == 0))
                {
                    s = pcopy(new BytePtr("__lambda"));
                }
                else if (((this.fd.tok & 0xFF) == 160))
                {
                    s = pcopy(new BytePtr("__dgliteral"));
                }
                else
                {
                    s = pcopy(new BytePtr("__funcliteral"));
                }
                DsymbolTable symtab = null;
                {
                    FuncDeclaration func = (sc.get()).parent.value.isFuncDeclaration();
                    if ((func) != null)
                    {
                        if ((func.localsymtab == null))
                        {
                            func.localsymtab = new DsymbolTable();
                        }
                        symtab = func.localsymtab;
                    }
                    else
                    {
                        ScopeDsymbol sds = (sc.get()).parent.value.isScopeDsymbol();
                        if (sds.symtab == null)
                        {
                            assert(sds.isTemplateInstance() != null);
                            sds.symtab = new DsymbolTable();
                        }
                        symtab = sds.symtab;
                    }
                }
                assert(symtab != null);
                Identifier id = Identifier.generateId(s, symtab.len() + 1);
                this.fd.ident = id;
                if (this.td != null)
                {
                    this.td.ident = id;
                }
                symtab.insert(this.td != null ? this.td : this.fd);
            }
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            if (this.td != null)
            {
                return new FuncExp(this.loc, this.td.syntaxCopy(null));
            }
            else if ((this.fd.semanticRun == PASS.init))
            {
                return new FuncExp(this.loc, this.fd.syntaxCopy(null));
            }
            else
            {
                return new FuncExp(this.loc, this.fd);
            }
        }

        // Erasure: matchType<Type, Ptr, Ptr, int>
        public  int matchType(Type to, Ptr<Scope> sc, Ptr<FuncExp> presult, int flag) {
            FuncExp __self = this;
            Function3<Expression,Type,Integer,Integer> cannotInfer = new Function3<Expression,Type,Integer,Integer>() {
                public Integer invoke(Expression e, Type to, Integer flag) {
                 {
                    if (flag == 0)
                    {
                        e.error(new BytePtr("cannot infer parameter types from `%s`"), to.toChars());
                    }
                    return MATCH.nomatch;
                }}

            };
            if (presult != null)
            {
                presult.set(0, null);
            }
            TypeFunction tof = null;
            if (((to.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                if (((this.tok & 0xFF) == 161))
                {
                    if (flag == 0)
                    {
                        this.error(new BytePtr("cannot match function literal to delegate type `%s`"), to.toChars());
                    }
                    return MATCH.nomatch;
                }
                tof = (TypeFunction)to.nextOf();
            }
            else if (((to.ty & 0xFF) == ENUMTY.Tpointer) && ((tof = to.nextOf().isTypeFunction()) != null))
            {
                if (((this.tok & 0xFF) == 160))
                {
                    if (flag == 0)
                    {
                        this.error(new BytePtr("cannot match delegate literal to function pointer type `%s`"), to.toChars());
                    }
                    return MATCH.nomatch;
                }
            }
            if (this.td != null)
            {
                if (tof == null)
                {
                    return cannotInfer.invoke(this, to, flag);
                }
                assert(this.td._scope != null);
                TypeFunction tf = this.fd.type.isTypeFunction();
                int dim = tf.parameterList.length();
                if ((tof.parameterList.length() != dim) || (tof.parameterList.varargs != tf.parameterList.varargs))
                {
                    return cannotInfer.invoke(this, to, flag);
                }
                Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                (tiargs.get()).reserve((this.td.parameters.get()).length);
                {
                    Slice<TemplateParameter> __r1360 = (this.td.parameters.get()).opSlice().copy();
                    int __key1361 = 0;
                    for (; (__key1361 < __r1360.getLength());__key1361 += 1) {
                        TemplateParameter tp = __r1360.get(__key1361);
                        int u = 0;
                        for (; (u < dim);u++){
                            Parameter p = tf.parameterList.get(u);
                            {
                                TypeIdentifier ti = p.type.isTypeIdentifier();
                                if ((ti) != null)
                                {
                                    if ((ti != null) && (pequals(ti.ident, tp.ident)))
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                        assert((u < dim));
                        Parameter pto = tof.parameterList.get(u);
                        Type t = pto.type;
                        if (((t.ty & 0xFF) == ENUMTY.Terror))
                        {
                            return cannotInfer.invoke(this, to, flag);
                        }
                        (tiargs.get()).push(t);
                    }
                }
                if ((tf.next.value == null) && (tof.next.value != null))
                {
                    this.fd.treq = to;
                }
                TemplateInstance ti = new TemplateInstance(this.loc, this.td, tiargs);
                Expression ex = expressionSemantic(new ScopeExp(this.loc, ti), this.td._scope);
                this.fd.treq = null;
                if (((ex.op & 0xFF) == 127))
                {
                    return MATCH.nomatch;
                }
                {
                    FuncExp ef = ex.isFuncExp();
                    if ((ef) != null)
                    {
                        return ef.matchType(to, sc, presult, flag);
                    }
                    else
                    {
                        return cannotInfer.invoke(this, to, flag);
                    }
                }
            }
            if ((tof == null) || (tof.next.value == null))
            {
                return MATCH.nomatch;
            }
            assert((this.type.value != null) && (!pequals(this.type.value, Type.tvoid)));
            if (((this.fd.type.ty & 0xFF) == ENUMTY.Terror))
            {
                return MATCH.nomatch;
            }
            TypeFunction tfx = this.fd.type.isTypeFunction();
            boolean convertMatch = (this.type.value.ty & 0xFF) != (to.ty & 0xFF);
            if (this.fd.inferRetType && (tfx.next.value.implicitConvTo(tof.next.value) == MATCH.convert))
            {
                convertMatch = true;
                TypeFunction tfy = new TypeFunction(tfx.parameterList, tof.next.value, tfx.linkage, 0L);
                tfy.mod = tfx.mod;
                tfy.isnothrow = tfx.isnothrow;
                tfy.isnogc = tfx.isnogc;
                tfy.purity = tfx.purity;
                tfy.isproperty = tfx.isproperty;
                tfy.isref = tfx.isref;
                tfy.iswild = tfx.iswild;
                tfy.deco = pcopy(merge(tfy).deco);
                tfx = tfy;
            }
            Type tx = null;
            if (((this.tok & 0xFF) == 160) || ((this.tok & 0xFF) == 0) && ((this.type.value.ty & 0xFF) == ENUMTY.Tdelegate) || ((this.type.value.ty & 0xFF) == ENUMTY.Tpointer) && ((to.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                tx = new TypeDelegate(tfx);
                tx.deco = pcopy(merge(tx).deco);
            }
            else
            {
                assert(((this.tok & 0xFF) == 161) || ((this.tok & 0xFF) == 0) && ((this.type.value.ty & 0xFF) == ENUMTY.Tpointer));
                tx = tfx.pointerTo();
            }
            int m = tx.implicitConvTo(to);
            if ((m > MATCH.nomatch))
            {
                m = convertMatch ? MATCH.convert : tx.equals(to) ? MATCH.exact : MATCH.constant;
                if (presult != null)
                {
                    presult.set(0, ((FuncExp)this.copy()));
                    (presult.get()).type.value = to;
                    (presult.get()).fd.modifyReturns(sc, tof.next.value);
                }
            }
            else if (flag == 0)
            {
                Slice<BytePtr> ts = toAutoQualChars(tx, to);
                this.error(new BytePtr("cannot implicitly convert expression `%s` of type `%s` to `%s`"), this.toChars(), ts.get(0), ts.get(1));
            }
            return m;
        }

        // defaulted all parameters starting with #4
        public  int matchType(Type to, Ptr<Scope> sc, Ptr<FuncExp> presult) {
            return matchType(to, sc, presult, 0);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.fd.toChars();
        }

        // Erasure: checkType<>
        public  boolean checkType() {
            if (this.td != null)
            {
                this.error(new BytePtr("template lambda has no type"));
                return true;
            }
            return false;
        }

        // Erasure: checkValue<>
        public  boolean checkValue() {
            if (this.td != null)
            {
                this.error(new BytePtr("template lambda has no value"));
                return true;
            }
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FuncExp() {}

        public FuncExp copy() {
            FuncExp that = new FuncExp();
            that.fd = this.fd;
            that.td = this.td;
            that.tok = this.tok;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DeclarationExp extends Expression
    {
        public Dsymbol declaration = null;
        // Erasure: __ctor<Loc, Dsymbol>
        public  DeclarationExp(Loc loc, Dsymbol declaration) {
            super(loc, TOK.declaration, 28);
            this.declaration = declaration;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new DeclarationExp(this.loc, this.declaration.syntaxCopy(null));
        }

        // Erasure: hasCode<>
        public  boolean hasCode() {
            {
                VarDeclaration vd = this.declaration.isVarDeclaration();
                if ((vd) != null)
                {
                    return (vd.storage_class & 8388609L) == 0;
                }
            }
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DeclarationExp() {}

        public DeclarationExp copy() {
            DeclarationExp that = new DeclarationExp();
            that.declaration = this.declaration;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TypeidExp extends Expression
    {
        public RootObject obj = null;
        // Erasure: __ctor<Loc, RootObject>
        public  TypeidExp(Loc loc, RootObject o) {
            super(loc, TOK.typeid_, 28);
            this.obj = o;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new TypeidExp(this.loc, objectSyntaxCopy(this.obj));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeidExp() {}

        public TypeidExp copy() {
            TypeidExp that = new TypeidExp();
            that.obj = this.obj;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TraitsExp extends Expression
    {
        public Identifier ident = null;
        public Ptr<DArray<RootObject>> args = null;
        // Erasure: __ctor<Loc, Identifier, Ptr>
        public  TraitsExp(Loc loc, Identifier ident, Ptr<DArray<RootObject>> args) {
            super(loc, TOK.traits, 32);
            this.ident = ident;
            this.args = pcopy(args);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new TraitsExp(this.loc, this.ident, TemplateInstance.arraySyntaxCopy(this.args));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TraitsExp() {}

        public TraitsExp copy() {
            TraitsExp that = new TraitsExp();
            that.ident = this.ident;
            that.args = this.args;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class HaltExp extends Expression
    {
        // Erasure: __ctor<Loc>
        public  HaltExp(Loc loc) {
            super(loc, TOK.halt, 24);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public HaltExp() {}

        public HaltExp copy() {
            HaltExp that = new HaltExp();
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IsExp extends Expression
    {
        public Type targ = null;
        public Identifier id = null;
        public Type tspec = null;
        public Ptr<DArray<TemplateParameter>> parameters = null;
        public byte tok = 0;
        public byte tok2 = 0;
        // Erasure: __ctor<Loc, Type, Identifier, byte, Type, byte, Ptr>
        public  IsExp(Loc loc, Type targ, Identifier id, byte tok, Type tspec, byte tok2, Ptr<DArray<TemplateParameter>> parameters) {
            super(loc, TOK.is_, 42);
            this.targ = targ;
            this.id = id;
            this.tok = tok;
            this.tspec = tspec;
            this.tok2 = tok2;
            this.parameters = pcopy(parameters);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            Ptr<DArray<TemplateParameter>> p = null;
            if (this.parameters != null)
            {
                p = pcopy((refPtr(new DArray<TemplateParameter>((this.parameters.get()).length))));
                {
                    Slice<TemplateParameter> __r1363 = (this.parameters.get()).opSlice().copy();
                    int __key1362 = 0;
                    for (; (__key1362 < __r1363.getLength());__key1362 += 1) {
                        TemplateParameter el = __r1363.get(__key1362);
                        int i = __key1362;
                        p.get().set(i, el.syntaxCopy());
                    }
                }
            }
            return new IsExp(this.loc, this.targ.syntaxCopy(), this.id, this.tok, this.tspec != null ? this.tspec.syntaxCopy() : null, this.tok2, p);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IsExp() {}

        public IsExp copy() {
            IsExp that = new IsExp();
            that.targ = this.targ;
            that.id = this.id;
            that.tspec = this.tspec;
            that.parameters = this.parameters;
            that.tok = this.tok;
            that.tok2 = this.tok2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static abstract class UnaExp extends Expression
    {
        public Ref<Expression> e1 = ref(null);
        public Type att1 = null;
        // Erasure: __ctor<Loc, byte, int, Expression>
        public  UnaExp(Loc loc, byte op, int size, Expression e1) {
            super(loc, op, size);
            this.e1.value = e1;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            UnaExp e = (UnaExp)this.copy();
            e.type.value = null;
            e.e1.value = e.e1.value.syntaxCopy();
            return e;
        }

        // Erasure: incompatibleTypes<>
        public  Expression incompatibleTypes() {
            if ((pequals(this.e1.value.type.value.toBasetype(), Type.terror)))
            {
                return this.e1.value;
            }
            if (((this.e1.value.op & 0xFF) == 20))
            {
                this.error(new BytePtr("incompatible type for `%s(%s)`: cannot use `%s` with types"), Token.toChars(this.op), this.e1.value.toChars(), Token.toChars(this.op));
            }
            else
            {
                this.error(new BytePtr("incompatible type for `%s(%s)`: `%s`"), Token.toChars(this.op), this.e1.value.toChars(), this.e1.value.type.value.toChars());
            }
            return new ErrorExp();
        }

        // Erasure: setNoderefOperand<>
        public  void setNoderefOperand() {
            {
                DotIdExp edi = this.e1.value.isDotIdExp();
                if ((edi) != null)
                {
                    edi.noderef = true;
                }
            }
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            this.e1.value = this.e1.value.resolveLoc(loc, sc);
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UnaExp() {}

        public abstract UnaExp copy();
    }
    public static abstract class BinExp extends Expression
    {
        public Ref<Expression> e1 = ref(null);
        public Ref<Expression> e2 = ref(null);
        public Type att1 = null;
        public Type att2 = null;
        // Erasure: __ctor<Loc, byte, int, Expression, Expression>
        public  BinExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
            super(loc, op, size);
            this.e1.value = e1;
            this.e2.value = e2;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            BinExp e = (BinExp)this.copy();
            e.type.value = null;
            e.e1.value = e.e1.value.syntaxCopy();
            e.e2.value = e.e2.value.syntaxCopy();
            return e;
        }

        // Erasure: incompatibleTypes<>
        public  Expression incompatibleTypes() {
            if ((pequals(this.e1.value.type.value.toBasetype(), Type.terror)))
            {
                return this.e1.value;
            }
            if ((pequals(this.e2.value.type.value.toBasetype(), Type.terror)))
            {
                return this.e2.value;
            }
            byte thisOp = ((this.op & 0xFF) == 100) ? TOK.colon : (byte)(this.op & 0xFF);
            if (((this.e1.value.op & 0xFF) == 20) || ((this.e2.value.op & 0xFF) == 20))
            {
                this.error(new BytePtr("incompatible types for `(%s) %s (%s)`: cannot use `%s` with types"), this.e1.value.toChars(), Token.toChars(thisOp), this.e2.value.toChars(), Token.toChars(this.op));
            }
            else if (this.e1.value.type.value.equals(this.e2.value.type.value))
            {
                this.error(new BytePtr("incompatible types for `(%s) %s (%s)`: both operands are of type `%s`"), this.e1.value.toChars(), Token.toChars(thisOp), this.e2.value.toChars(), this.e1.value.type.value.toChars());
            }
            else
            {
                Slice<BytePtr> ts = toAutoQualChars(this.e1.value.type.value, this.e2.value.type.value);
                this.error(new BytePtr("incompatible types for `(%s) %s (%s)`: `%s` and `%s`"), this.e1.value.toChars(), Token.toChars(thisOp), this.e2.value.toChars(), ts.get(0), ts.get(1));
            }
            return new ErrorExp();
        }

        // Erasure: checkOpAssignTypes<Ptr>
        public  Expression checkOpAssignTypes(Ptr<Scope> sc) {
            Type t1 = this.e1.value.type.value;
            Type t2 = this.e2.value.type.value;
            if (((this.op & 0xFF) == 76) || ((this.op & 0xFF) == 77) || ((this.op & 0xFF) == 81) || ((this.op & 0xFF) == 82) || ((this.op & 0xFF) == 83) || ((this.op & 0xFF) == 227))
            {
                if (this.type.value.isintegral() && t2.isfloating())
                {
                    this.warning(new BytePtr("`%s %s %s` is performing truncating conversion"), this.type.value.toChars(), Token.toChars(this.op), t2.toChars());
                }
            }
            if (((this.op & 0xFF) == 81) || ((this.op & 0xFF) == 82) || ((this.op & 0xFF) == 83))
            {
                BytePtr opstr = pcopy(Token.toChars(this.op));
                if (t1.isreal() && t2.iscomplex())
                {
                    this.error(new BytePtr("`%s %s %s` is undefined. Did you mean `%s %s %s.re`?"), t1.toChars(), opstr, t2.toChars(), t1.toChars(), opstr, t2.toChars());
                    return new ErrorExp();
                }
                else if (t1.isimaginary() && t2.iscomplex())
                {
                    this.error(new BytePtr("`%s %s %s` is undefined. Did you mean `%s %s %s.im`?"), t1.toChars(), opstr, t2.toChars(), t1.toChars(), opstr, t2.toChars());
                    return new ErrorExp();
                }
                else if (t1.isreal() || t1.isimaginary() && t2.isimaginary())
                {
                    this.error(new BytePtr("`%s %s %s` is an undefined operation"), t1.toChars(), opstr, t2.toChars());
                    return new ErrorExp();
                }
            }
            if (((this.op & 0xFF) == 76) || ((this.op & 0xFF) == 77))
            {
                if (t1.isreal() && t2.isimaginary() || t2.iscomplex() || t1.isimaginary() && t2.isreal() || t2.iscomplex())
                {
                    this.error(new BytePtr("`%s %s %s` is undefined (result is complex)"), t1.toChars(), Token.toChars(this.op), t2.toChars());
                    return new ErrorExp();
                }
                if (this.type.value.isreal() || this.type.value.isimaginary())
                {
                    assert((global.errors != 0) || t2.isfloating());
                    this.e2.value = this.e2.value.castTo(sc, t1);
                }
            }
            if (((this.op & 0xFF) == 81))
            {
                if (t2.isfloating())
                {
                    if (t1.isreal())
                    {
                        if (t2.isimaginary() || t2.iscomplex())
                        {
                            this.e2.value = this.e2.value.castTo(sc, t1);
                        }
                    }
                    else if (t1.isimaginary())
                    {
                        if (t2.isimaginary() || t2.iscomplex())
                        {
                            switch ((t1.ty & 0xFF))
                            {
                                case 24:
                                    t2 = Type.tfloat32;
                                    break;
                                case 25:
                                    t2 = Type.tfloat64;
                                    break;
                                case 26:
                                    t2 = Type.tfloat80;
                                    break;
                                default:
                                throw new AssertionError("Unreachable code!");
                            }
                            this.e2.value = this.e2.value.castTo(sc, t2);
                        }
                    }
                }
            }
            else if (((this.op & 0xFF) == 82))
            {
                if (t2.isimaginary())
                {
                    if (t1.isreal())
                    {
                        this.e2.value = new CommaExp(this.loc, this.e2.value, new RealExp(this.loc, CTFloat.zero, t1), true);
                        this.e2.value.type.value = t1;
                        Expression e = new AssignExp(this.loc, this.e1.value, this.e2.value);
                        e.type.value = t1;
                        return e;
                    }
                    else if (t1.isimaginary())
                    {
                        Type t3 = null;
                        switch ((t1.ty & 0xFF))
                        {
                            case 24:
                                t3 = Type.tfloat32;
                                break;
                            case 25:
                                t3 = Type.tfloat64;
                                break;
                            case 26:
                                t3 = Type.tfloat80;
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                        this.e2.value = this.e2.value.castTo(sc, t3);
                        Expression e = new AssignExp(this.loc, this.e1.value, this.e2.value);
                        e.type.value = t1;
                        return e;
                    }
                }
            }
            else if (((this.op & 0xFF) == 83))
            {
                if (t2.iscomplex())
                {
                    this.error(new BytePtr("cannot perform modulo complex arithmetic"));
                    return new ErrorExp();
                }
            }
            return this;
        }

        // Erasure: checkIntegralBin<>
        public  boolean checkIntegralBin() {
            boolean r1 = this.e1.value.checkIntegral();
            boolean r2 = this.e2.value.checkIntegral();
            return r1 || r2;
        }

        // Erasure: checkArithmeticBin<>
        public  boolean checkArithmeticBin() {
            boolean r1 = this.e1.value.checkArithmetic();
            boolean r2 = this.e2.value.checkArithmetic();
            return r1 || r2;
        }

        // Erasure: setNoderefOperands<>
        public  void setNoderefOperands() {
            {
                DotIdExp edi = this.e1.value.isDotIdExp();
                if ((edi) != null)
                {
                    edi.noderef = true;
                }
            }
            {
                DotIdExp edi = this.e2.value.isDotIdExp();
                if ((edi) != null)
                {
                    edi.noderef = true;
                }
            }
        }

        // Erasure: reorderSettingAAElem<Ptr>
        public  Expression reorderSettingAAElem(Ptr<Scope> sc) {
            BinExp be = this;
            IndexExp ie = be.e1.value.isIndexExp();
            if (ie == null)
            {
                return be;
            }
            if (((ie.e1.value.type.value.toBasetype().ty & 0xFF) != ENUMTY.Taarray))
            {
                return be;
            }
            Ref<Expression> e0 = ref(null);
            for (; 1 != 0;){
                Ref<Expression> de = ref(null);
                ie.e2.value = extractSideEffect(sc, new BytePtr("__aakey"), de, ie.e2.value, false);
                e0.value = Expression.combine(de.value, e0.value);
                IndexExp ie1 = ie.e1.value.isIndexExp();
                if ((ie1 == null) || ((ie1.e1.value.type.value.toBasetype().ty & 0xFF) != ENUMTY.Taarray))
                {
                    break;
                }
                ie = ie1;
            }
            assert(((ie.e1.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Taarray));
            Ref<Expression> de = ref(null);
            ie.e1.value = extractSideEffect(sc, new BytePtr("__aatmp"), de, ie.e1.value, false);
            e0.value = Expression.combine(de.value, e0.value);
            be.e2.value = extractSideEffect(sc, new BytePtr("__aaval"), e0, be.e2.value, true);
            return Expression.combine(e0.value, (Expression)be);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BinExp() {}

        public abstract BinExp copy();
    }
    public static class BinAssignExp extends BinExp
    {
        // Erasure: __ctor<Loc, byte, int, Expression, Expression>
        public  BinAssignExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
            super(loc, op, size, e1, e2);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression ex) {
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            return this.toLvalue(sc, this);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BinAssignExp() {}

        public BinAssignExp copy() {
            BinAssignExp that = new BinAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompileExp extends Expression
    {
        public Ptr<DArray<Expression>> exps = null;
        // Erasure: __ctor<Loc, Ptr>
        public  CompileExp(Loc loc, Ptr<DArray<Expression>> exps) {
            super(loc, TOK.mixin_, 28);
            this.exps = pcopy(exps);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new CompileExp(this.loc, Expression.arraySyntaxCopy(this.exps));
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            Expression e = isExpression(o);
            if (e == null)
            {
                return false;
            }
            {
                CompileExp ce = e.isCompileExp();
                if ((ce) != null)
                {
                    if (((this.exps.get()).length != (ce.exps.get()).length))
                    {
                        return false;
                    }
                    {
                        Slice<Expression> __r1365 = (this.exps.get()).opSlice().copy();
                        int __key1364 = 0;
                        for (; (__key1364 < __r1365.getLength());__key1364 += 1) {
                            Expression e1 = __r1365.get(__key1364);
                            int i = __key1364;
                            Expression e2 = (ce.exps.get()).get(i);
                            if ((!pequals(e1, e2)) && (e1 == null) || (e2 == null) || !e1.equals(e2))
                            {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompileExp() {}

        public CompileExp copy() {
            CompileExp that = new CompileExp();
            that.exps = this.exps;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ImportExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  ImportExp(Loc loc, Expression e) {
            super(loc, TOK.import_, 32, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ImportExp() {}

        public ImportExp copy() {
            ImportExp that = new ImportExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AssertExp extends UnaExp
    {
        public Expression msg = null;
        // Erasure: __ctor<Loc, Expression, Expression>
        public  AssertExp(Loc loc, Expression e, Expression msg) {
            super(loc, TOK.assert_, 36, e);
            this.msg = msg;
        }

        // defaulted all parameters starting with #3
        public  AssertExp(Loc loc, Expression e) {
            this(loc, e, (Expression)null);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new AssertExp(this.loc, this.e1.value.syntaxCopy(), this.msg != null ? this.msg.syntaxCopy() : null);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AssertExp() {}

        public AssertExp copy() {
            AssertExp that = new AssertExp();
            that.msg = this.msg;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotIdExp extends UnaExp
    {
        public Identifier ident = null;
        public boolean noderef = false;
        public boolean wantsym = false;
        // Erasure: __ctor<Loc, Expression, Identifier>
        public  DotIdExp(Loc loc, Expression e, Identifier ident) {
            super(loc, TOK.dotIdentifier, 38, e);
            this.ident = ident;
        }

        // Erasure: create<Loc, Expression, Identifier>
        public static DotIdExp create(Loc loc, Expression e, Identifier ident) {
            return new DotIdExp(loc, e, ident);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotIdExp() {}

        public DotIdExp copy() {
            DotIdExp that = new DotIdExp();
            that.ident = this.ident;
            that.noderef = this.noderef;
            that.wantsym = this.wantsym;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotTemplateExp extends UnaExp
    {
        public TemplateDeclaration td = null;
        // Erasure: __ctor<Loc, Expression, TemplateDeclaration>
        public  DotTemplateExp(Loc loc, Expression e, TemplateDeclaration td) {
            super(loc, TOK.dotTemplateDeclaration, 36, e);
            this.td = td;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotTemplateExp() {}

        public DotTemplateExp copy() {
            DotTemplateExp that = new DotTemplateExp();
            that.td = this.td;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotVarExp extends UnaExp
    {
        public Declaration var = null;
        public boolean hasOverloads = false;
        // Erasure: __ctor<Loc, Expression, Declaration, boolean>
        public  DotVarExp(Loc loc, Expression e, Declaration var, boolean hasOverloads) {
            if (var.isVarDeclaration() != null)
            {
                hasOverloads = false;
            }
            super(loc, TOK.dotVariable, 37, e);
            this.var = var;
            this.hasOverloads = hasOverloads;
        }

        // defaulted all parameters starting with #4
        public  DotVarExp(Loc loc, Expression e, Declaration var) {
            this(loc, e, var, true);
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            if (checkUnsafeAccess(sc, this, false, flag == 0))
            {
                return Modifiable.initialization;
            }
            if (((this.e1.value.op & 0xFF) == 123))
            {
                return this.var.checkModify(this.loc, sc, this.e1.value, flag);
            }
            if (((sc.get()).func != null) && ((sc.get()).func.isCtorDeclaration() != null))
            {
                {
                    DotVarExp dve = this.e1.value.isDotVarExp();
                    if ((dve) != null)
                    {
                        if (((dve.e1.value.op & 0xFF) == 123))
                        {
                            VarDeclaration v = dve.var.isVarDeclaration();
                            if ((v != null) && v.isField() && (v._init == null) && !v.ctorinit)
                            {
                                {
                                    TypeStruct ts = v.type.isTypeStruct();
                                    if ((ts) != null)
                                    {
                                        if (ts.sym.noDefaultCtor)
                                        {
                                            int modifyLevel = v.checkModify(this.loc, sc, dve.e1.value, flag);
                                            v.ctorinit = false;
                                            if ((modifyLevel == Modifiable.initialization))
                                            {
                                                return Modifiable.yes;
                                            }
                                            return modifyLevel;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return this.e1.value.checkModifiable(sc, flag);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if (((this.e1.value.op & 0xFF) == 123) && ((sc.get()).ctorflow.fieldinit.getLength() != 0) && (((sc.get()).ctorflow.callSuper.value & 16) == 0))
            {
                {
                    VarDeclaration vd = this.var.isVarDeclaration();
                    if ((vd) != null)
                    {
                        AggregateDeclaration ad = vd.isMember2();
                        if ((ad != null) && (ad.fields.length == (sc.get()).ctorflow.fieldinit.getLength()))
                        {
                            {
                                Slice<VarDeclaration> __r1367 = ad.fields.opSlice().copy();
                                int __key1366 = 0;
                                for (; (__key1366 < __r1367.getLength());__key1366 += 1) {
                                    VarDeclaration f = __r1367.get(__key1366);
                                    int i = __key1366;
                                    if ((pequals(f, vd)))
                                    {
                                        if (((sc.get()).ctorflow.fieldinit.get(i).csx.value & 1) == 0)
                                        {
                                            modifyFieldVar(this.loc, sc, vd, this.e1.value);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            return this.modifiableLvalue(sc, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotVarExp() {}

        public DotVarExp copy() {
            DotVarExp that = new DotVarExp();
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotTemplateInstanceExp extends UnaExp
    {
        public TemplateInstance ti = null;
        // Erasure: __ctor<Loc, Expression, Identifier, Ptr>
        public  DotTemplateInstanceExp(Loc loc, Expression e, Identifier name, Ptr<DArray<RootObject>> tiargs) {
            super(loc, TOK.dotTemplateInstance, 36, e);
            this.ti = new TemplateInstance(loc, name, tiargs);
        }

        // Erasure: __ctor<Loc, Expression, TemplateInstance>
        public  DotTemplateInstanceExp(Loc loc, Expression e, TemplateInstance ti) {
            super(loc, TOK.dotTemplateInstance, 36, e);
            this.ti = ti;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new DotTemplateInstanceExp(this.loc, this.e1.value.syntaxCopy(), this.ti.name, TemplateInstance.arraySyntaxCopy(this.ti.tiargs));
        }

        // Erasure: findTempDecl<Ptr>
        public  boolean findTempDecl(Ptr<Scope> sc) {
            if (this.ti.tempdecl != null)
            {
                return true;
            }
            Expression e = new DotIdExp(this.loc, this.e1.value, this.ti.name);
            e = expressionSemantic(e, sc);
            if (((e.op & 0xFF) == 97))
            {
                e = ((DotExp)e).e2.value;
            }
            Dsymbol s = null;
            switch ((e.op & 0xFF))
            {
                case 214:
                    s = ((OverExp)e).vars;
                    break;
                case 37:
                    s = ((DotTemplateExp)e).td;
                    break;
                case 203:
                    s = ((ScopeExp)e).sds;
                    break;
                case 27:
                    s = ((DotVarExp)e).var;
                    break;
                case 26:
                    s = ((VarExp)e).var;
                    break;
                default:
                return false;
            }
            return this.ti.updateTempDecl(sc, s);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotTemplateInstanceExp() {}

        public DotTemplateInstanceExp copy() {
            DotTemplateInstanceExp that = new DotTemplateInstanceExp();
            that.ti = this.ti;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DelegateExp extends UnaExp
    {
        public FuncDeclaration func = null;
        public boolean hasOverloads = false;
        public VarDeclaration vthis2 = null;
        // Erasure: __ctor<Loc, Expression, FuncDeclaration, boolean, VarDeclaration>
        public  DelegateExp(Loc loc, Expression e, FuncDeclaration f, boolean hasOverloads, VarDeclaration vthis2) {
            super(loc, TOK.delegate_, 44, e);
            this.func = f;
            this.hasOverloads = hasOverloads;
            this.vthis2 = vthis2;
        }

        // defaulted all parameters starting with #5
        public  DelegateExp(Loc loc, Expression e, FuncDeclaration f, boolean hasOverloads) {
            this(loc, e, f, hasOverloads, (VarDeclaration)null);
        }

        // defaulted all parameters starting with #4
        public  DelegateExp(Loc loc, Expression e, FuncDeclaration f) {
            this(loc, e, f, true, (VarDeclaration)null);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DelegateExp() {}

        public DelegateExp copy() {
            DelegateExp that = new DelegateExp();
            that.func = this.func;
            that.hasOverloads = this.hasOverloads;
            that.vthis2 = this.vthis2;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotTypeExp extends UnaExp
    {
        public Dsymbol sym = null;
        // Erasure: __ctor<Loc, Expression, Dsymbol>
        public  DotTypeExp(Loc loc, Expression e, Dsymbol s) {
            super(loc, TOK.dotType, 36, e);
            this.sym = s;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotTypeExp() {}

        public DotTypeExp copy() {
            DotTypeExp that = new DotTypeExp();
            that.sym = this.sym;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CallExp extends UnaExp
    {
        public Ptr<DArray<Expression>> arguments = null;
        public FuncDeclaration f = null;
        public boolean directcall = false;
        public VarDeclaration vthis2 = null;
        // Erasure: __ctor<Loc, Expression, Ptr>
        public  CallExp(Loc loc, Expression e, Ptr<DArray<Expression>> exps) {
            super(loc, TOK.call, 48, e);
            this.arguments = pcopy(exps);
        }

        // Erasure: __ctor<Loc, Expression>
        public  CallExp(Loc loc, Expression e) {
            super(loc, TOK.call, 48, e);
        }

        // Erasure: __ctor<Loc, Expression, Expression>
        public  CallExp(Loc loc, Expression e, Expression earg1) {
            super(loc, TOK.call, 48, e);
            this.arguments = pcopy((refPtr(new DArray<Expression>())));
            if (earg1 != null)
            {
                (this.arguments.get()).push(earg1);
            }
        }

        // Erasure: __ctor<Loc, Expression, Expression, Expression>
        public  CallExp(Loc loc, Expression e, Expression earg1, Expression earg2) {
            super(loc, TOK.call, 48, e);
            Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>(2));
            arguments.get().set(0, earg1);
            arguments.get().set(1, earg2);
            this.arguments = pcopy(arguments);
        }

        // Erasure: __ctor<Loc, FuncDeclaration, Expression>
        public  CallExp(Loc loc, FuncDeclaration fd, Expression earg1) {
            this(loc, new VarExp(loc, fd, false), earg1);
            this.f = fd;
        }

        // Erasure: create<Loc, Expression, Ptr>
        public static CallExp create(Loc loc, Expression e, Ptr<DArray<Expression>> exps) {
            return new CallExp(loc, e, exps);
        }

        // Erasure: create<Loc, Expression>
        public static CallExp create(Loc loc, Expression e) {
            return new CallExp(loc, e);
        }

        // Erasure: create<Loc, Expression, Expression>
        public static CallExp create(Loc loc, Expression e, Expression earg1) {
            return new CallExp(loc, e, earg1);
        }

        // Erasure: create<Loc, FuncDeclaration, Expression>
        public static CallExp create(Loc loc, FuncDeclaration fd, Expression earg1) {
            return new CallExp(loc, fd, earg1);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new CallExp(this.loc, this.e1.value.syntaxCopy(), Expression.arraySyntaxCopy(this.arguments));
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            Type tb = this.e1.value.type.value.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tdelegate) || ((tb.ty & 0xFF) == ENUMTY.Tpointer))
            {
                tb = tb.nextOf();
            }
            TypeFunction tf = tb.isTypeFunction();
            if ((tf != null) && tf.isref)
            {
                {
                    DotVarExp dve = this.e1.value.isDotVarExp();
                    if ((dve) != null)
                    {
                        if (dve.var.isCtorDeclaration() != null)
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if (this.isLvalue())
            {
                return this;
            }
            return this.toLvalue(sc, e);
        }

        // Erasure: addDtorHook<Ptr>
        public  Expression addDtorHook(Ptr<Scope> sc) {
            {
                TypeFunction tf = this.e1.value.type.value.isTypeFunction();
                if ((tf) != null)
                {
                    if (tf.isref)
                    {
                        return this;
                    }
                }
            }
            Type tv = this.type.value.baseElemOf();
            {
                TypeStruct ts = tv.isTypeStruct();
                if ((ts) != null)
                {
                    StructDeclaration sd = ts.sym;
                    if (sd.dtor != null)
                    {
                        VarDeclaration tmp = copyToTemp(0L, new BytePtr("__tmpfordtor"), this);
                        DeclarationExp de = new DeclarationExp(this.loc, tmp);
                        VarExp ve = new VarExp(this.loc, tmp, true);
                        Expression e = new CommaExp(this.loc, de, ve, true);
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                }
            }
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CallExp() {}

        public CallExp copy() {
            CallExp that = new CallExp();
            that.arguments = this.arguments;
            that.f = this.f;
            that.directcall = this.directcall;
            that.vthis2 = this.vthis2;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    // Erasure: isFuncAddress<Expression, Ptr>
    public static FuncDeclaration isFuncAddress(Expression e, Ptr<Boolean> hasOverloads) {
        {
            AddrExp ae = e.isAddrExp();
            if ((ae) != null)
            {
                Expression ae1 = ae.e1.value;
                {
                    VarExp ve = ae1.isVarExp();
                    if ((ve) != null)
                    {
                        if (hasOverloads != null)
                        {
                            hasOverloads.set(0, ve.hasOverloads);
                        }
                        return ve.var.isFuncDeclaration();
                    }
                }
                {
                    DotVarExp dve = ae1.isDotVarExp();
                    if ((dve) != null)
                    {
                        if (hasOverloads != null)
                        {
                            hasOverloads.set(0, dve.hasOverloads);
                        }
                        return dve.var.isFuncDeclaration();
                    }
                }
            }
            else
            {
                {
                    SymOffExp soe = e.isSymOffExp();
                    if ((soe) != null)
                    {
                        if (hasOverloads != null)
                        {
                            hasOverloads.set(0, soe.hasOverloads);
                        }
                        return soe.var.isFuncDeclaration();
                    }
                }
                {
                    DelegateExp dge = e.isDelegateExp();
                    if ((dge) != null)
                    {
                        if (hasOverloads != null)
                        {
                            hasOverloads.set(0, dge.hasOverloads);
                        }
                        return dge.func.isFuncDeclaration();
                    }
                }
            }
        }
        return null;
    }

    // defaulted all parameters starting with #2
    public static FuncDeclaration isFuncAddress(Expression e) {
        return isFuncAddress(e, (Ptr<Boolean>)null);
    }

    public static class AddrExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  AddrExp(Loc loc, Expression e) {
            super(loc, TOK.address, 32, e);
        }

        // Erasure: __ctor<Loc, Expression, Type>
        public  AddrExp(Loc loc, Expression e, Type t) {
            this(loc, e);
            this.type.value = t;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AddrExp() {}

        public AddrExp copy() {
            AddrExp that = new AddrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PtrExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  PtrExp(Loc loc, Expression e) {
            super(loc, TOK.star, 32, e);
        }

        // Erasure: __ctor<Loc, Expression, Type>
        public  PtrExp(Loc loc, Expression e, Type t) {
            super(loc, TOK.star, 32, e);
            this.type.value = t;
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            {
                SymOffExp se = this.e1.value.isSymOffExp();
                if ((se) != null)
                {
                    return se.var.checkModify(this.loc, sc, null, flag);
                }
                else {
                    AddrExp ae = this.e1.value.isAddrExp();
                    if ((ae) != null)
                    {
                        return ae.e1.value.checkModifiable(sc, flag);
                    }
                }
            }
            return Modifiable.yes;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            return this.modifiableLvalue(sc, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PtrExp() {}

        public PtrExp copy() {
            PtrExp that = new PtrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NegExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  NegExp(Loc loc, Expression e) {
            super(loc, TOK.negate, 32, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NegExp() {}

        public NegExp copy() {
            NegExp that = new NegExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UAddExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  UAddExp(Loc loc, Expression e) {
            super(loc, TOK.uadd, 32, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UAddExp() {}

        public UAddExp copy() {
            UAddExp that = new UAddExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ComExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  ComExp(Loc loc, Expression e) {
            super(loc, TOK.tilde, 32, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ComExp() {}

        public ComExp copy() {
            ComExp that = new ComExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NotExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  NotExp(Loc loc, Expression e) {
            super(loc, TOK.not, 32, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NotExp() {}

        public NotExp copy() {
            NotExp that = new NotExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DeleteExp extends UnaExp
    {
        public boolean isRAII = false;
        // Erasure: __ctor<Loc, Expression, boolean>
        public  DeleteExp(Loc loc, Expression e, boolean isRAII) {
            super(loc, TOK.delete_, 33, e);
            this.isRAII = isRAII;
        }

        // Erasure: toBoolean<Ptr>
        public  Expression toBoolean(Ptr<Scope> sc) {
            this.error(new BytePtr("`delete` does not give a boolean result"));
            return new ErrorExp();
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DeleteExp() {}

        public DeleteExp copy() {
            DeleteExp that = new DeleteExp();
            that.isRAII = this.isRAII;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CastExp extends UnaExp
    {
        public Type to = null;
        public byte mod = (byte)255;
        // Erasure: __ctor<Loc, Expression, Type>
        public  CastExp(Loc loc, Expression e, Type t) {
            super(loc, TOK.cast_, 37, e);
            this.to = t;
        }

        // Erasure: __ctor<Loc, Expression, byte>
        public  CastExp(Loc loc, Expression e, byte mod) {
            super(loc, TOK.cast_, 37, e);
            this.mod = mod;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return this.to != null ? new CastExp(this.loc, this.e1.value.syntaxCopy(), this.to.syntaxCopy()) : new CastExp(this.loc, this.e1.value.syntaxCopy(), this.mod);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return this.e1.value.isLvalue() && this.e1.value.type.value.mutableOf().unSharedOf().equals(this.to.mutableOf().unSharedOf());
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if (this.isLvalue())
            {
                return this;
            }
            return this.toLvalue(sc, e);
        }

        // Erasure: addDtorHook<Ptr>
        public  Expression addDtorHook(Ptr<Scope> sc) {
            if (((this.to.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
            {
                this.e1.value = this.e1.value.addDtorHook(sc);
            }
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CastExp() {}

        public CastExp copy() {
            CastExp that = new CastExp();
            that.to = this.to;
            that.mod = this.mod;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VectorExp extends UnaExp
    {
        public TypeVector to = null;
        public int dim = -1;
        public byte ownedByCtfe = OwnedBy.code;
        // Erasure: __ctor<Loc, Expression, Type>
        public  VectorExp(Loc loc, Expression e, Type t) {
            super(loc, TOK.vector, 41, e);
            assert(((t.ty & 0xFF) == ENUMTY.Tvector));
            this.to = (TypeVector)t;
        }

        // Erasure: create<Loc, Expression, Type>
        public static VectorExp create(Loc loc, Expression e, Type t) {
            return new VectorExp(loc, e, t);
        }

        // Erasure: emplace<Ptr, Loc, Expression, Type>
        public static void emplace(Ptr<UnionExp> pue, Loc loc, Expression e, Type type) {
            (pue) = new UnionExp(new VectorExp(loc, e, type));
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new VectorExp(this.loc, this.e1.value.syntaxCopy(), this.to.syntaxCopy());
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VectorExp() {}

        public VectorExp copy() {
            VectorExp that = new VectorExp();
            that.to = this.to;
            that.dim = this.dim;
            that.ownedByCtfe = this.ownedByCtfe;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VectorArrayExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  VectorArrayExp(Loc loc, Expression e1) {
            super(loc, TOK.vectorArray, 32, e1);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return this.e1.value.isLvalue();
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            this.e1.value = this.e1.value.toLvalue(sc, e);
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VectorArrayExp() {}

        public VectorArrayExp copy() {
            VectorArrayExp that = new VectorArrayExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SliceExp extends UnaExp
    {
        public Ref<Expression> upr = ref(null);
        public Ref<Expression> lwr = ref(null);
        public Ref<VarDeclaration> lengthVar = ref(null);
        public boolean upperIsInBounds = false;
        public boolean lowerIsLessThanUpper = false;
        public boolean arrayop = false;
        // Erasure: __ctor<Loc, Expression, IntervalExp>
        public  SliceExp(Loc loc, Expression e1, IntervalExp ie) {
            super(loc, TOK.slice, 47, e1);
            this.upr.value = ie != null ? ie.upr.value : null;
            this.lwr.value = ie != null ? ie.lwr.value : null;
        }

        // Erasure: __ctor<Loc, Expression, Expression, Expression>
        public  SliceExp(Loc loc, Expression e1, Expression lwr, Expression upr) {
            super(loc, TOK.slice, 47, e1);
            this.upr.value = upr;
            this.lwr.value = lwr;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            SliceExp se = new SliceExp(this.loc, this.e1.value.syntaxCopy(), this.lwr.value != null ? this.lwr.value.syntaxCopy() : null, this.upr.value != null ? this.upr.value.syntaxCopy() : null);
            se.lengthVar.value = this.lengthVar.value;
            return se;
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            if (((this.e1.value.type.value.ty & 0xFF) == ENUMTY.Tsarray) || ((this.e1.value.op & 0xFF) == 62) && ((this.e1.value.type.value.ty & 0xFF) != ENUMTY.Tarray) || ((this.e1.value.op & 0xFF) == 31))
            {
                return this.e1.value.checkModifiable(sc, flag);
            }
            return Modifiable.yes;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return (this.type.value != null) && ((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray);
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return (this.type.value != null) && ((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) ? this : this.toLvalue(sc, e);
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            this.error(new BytePtr("slice expression `%s` is not a modifiable lvalue"), this.toChars());
            return this;
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return this.e1.value.isBool(result);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SliceExp() {}

        public SliceExp copy() {
            SliceExp that = new SliceExp();
            that.upr = this.upr;
            that.lwr = this.lwr;
            that.lengthVar = this.lengthVar;
            that.upperIsInBounds = this.upperIsInBounds;
            that.lowerIsLessThanUpper = this.lowerIsLessThanUpper;
            that.arrayop = this.arrayop;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ArrayLengthExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  ArrayLengthExp(Loc loc, Expression e1) {
            super(loc, TOK.arrayLength, 32, e1);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayLengthExp() {}

        public ArrayLengthExp copy() {
            ArrayLengthExp that = new ArrayLengthExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ArrayExp extends UnaExp
    {
        public Ptr<DArray<Expression>> arguments = null;
        public int currentDimension = 0;
        public Ref<VarDeclaration> lengthVar = ref(null);
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ArrayExp(Loc loc, Expression e1, Expression index) {
            super(loc, TOK.array, 44, e1);
            this.arguments = pcopy((refPtr(new DArray<Expression>())));
            if (index != null)
            {
                (this.arguments.get()).push(index);
            }
        }

        // defaulted all parameters starting with #3
        public  ArrayExp(Loc loc, Expression e1) {
            this(loc, e1, (Expression)null);
        }

        // Erasure: __ctor<Loc, Expression, Ptr>
        public  ArrayExp(Loc loc, Expression e1, Ptr<DArray<Expression>> args) {
            super(loc, TOK.array, 44, e1);
            this.arguments = pcopy(args);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            ArrayExp ae = new ArrayExp(this.loc, this.e1.value.syntaxCopy(), Expression.arraySyntaxCopy(this.arguments));
            ae.lengthVar.value = this.lengthVar.value;
            return ae;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            if ((this.type.value != null) && ((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
            {
                return false;
            }
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            if ((this.type.value != null) && ((this.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
            {
                this.error(new BytePtr("`void`s have no value"));
            }
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayExp() {}

        public ArrayExp copy() {
            ArrayExp that = new ArrayExp();
            that.arguments = this.arguments;
            that.currentDimension = this.currentDimension;
            that.lengthVar = this.lengthVar;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  DotExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.dot, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotExp() {}

        public DotExp copy() {
            DotExp that = new DotExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CommaExp extends BinExp
    {
        public boolean isGenerated = false;
        public boolean allowCommaExp = false;
        // Erasure: __ctor<Loc, Expression, Expression, boolean>
        public  CommaExp(Loc loc, Expression e1, Expression e2, boolean generated) {
            super(loc, TOK.comma, 42, e1, e2);
            this.allowCommaExp = (this.isGenerated = generated);
        }

        // defaulted all parameters starting with #4
        public  CommaExp(Loc loc, Expression e1, Expression e2) {
            this(loc, e1, e2, true);
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            return this.e2.value.checkModifiable(sc, flag);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return this.e2.value.isLvalue();
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            this.e2.value = this.e2.value.toLvalue(sc, null);
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            this.e2.value = this.e2.value.modifiableLvalue(sc, e);
            return this;
        }

        // Erasure: isBool<boolean>
        public  boolean isBool(boolean result) {
            return this.e2.value.isBool(result);
        }

        // Erasure: toBoolean<Ptr>
        public  Expression toBoolean(Ptr<Scope> sc) {
            Expression ex2 = this.e2.value.toBoolean(sc);
            if (((ex2.op & 0xFF) == 127))
            {
                return ex2;
            }
            this.e2.value = ex2;
            this.type.value = this.e2.value.type.value;
            return this;
        }

        // Erasure: addDtorHook<Ptr>
        public  Expression addDtorHook(Ptr<Scope> sc) {
            this.e2.value = this.e2.value.addDtorHook(sc);
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: allow<Expression>
        public static void allow(Expression exp) {
            if (exp != null)
            {
                {
                    CommaExp ce = exp.isCommaExp();
                    if ((ce) != null)
                    {
                        ce.allowCommaExp = true;
                    }
                }
            }
        }


        public CommaExp() {}

        public CommaExp copy() {
            CommaExp that = new CommaExp();
            that.isGenerated = this.isGenerated;
            that.allowCommaExp = this.allowCommaExp;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IntervalExp extends Expression
    {
        public Ref<Expression> lwr = ref(null);
        public Ref<Expression> upr = ref(null);
        // Erasure: __ctor<Loc, Expression, Expression>
        public  IntervalExp(Loc loc, Expression lwr, Expression upr) {
            super(loc, TOK.interval, 32);
            this.lwr.value = lwr;
            this.upr.value = upr;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new IntervalExp(this.loc, this.lwr.value.syntaxCopy(), this.upr.value.syntaxCopy());
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IntervalExp() {}

        public IntervalExp copy() {
            IntervalExp that = new IntervalExp();
            that.lwr = this.lwr;
            that.upr = this.upr;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DelegatePtrExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  DelegatePtrExp(Loc loc, Expression e1) {
            super(loc, TOK.delegatePointer, 32, e1);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return this.e1.value.isLvalue();
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            this.e1.value = this.e1.value.toLvalue(sc, e);
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            if ((sc.get()).func.setUnsafe())
            {
                this.error(new BytePtr("cannot modify delegate pointer in `@safe` code `%s`"), this.toChars());
                return new ErrorExp();
            }
            return this.modifiableLvalue(sc, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DelegatePtrExp() {}

        public DelegatePtrExp copy() {
            DelegatePtrExp that = new DelegatePtrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DelegateFuncptrExp extends UnaExp
    {
        // Erasure: __ctor<Loc, Expression>
        public  DelegateFuncptrExp(Loc loc, Expression e1) {
            super(loc, TOK.delegateFunctionPointer, 32, e1);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return this.e1.value.isLvalue();
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            this.e1.value = this.e1.value.toLvalue(sc, e);
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            if ((sc.get()).func.setUnsafe())
            {
                this.error(new BytePtr("cannot modify delegate function pointer in `@safe` code `%s`"), this.toChars());
                return new ErrorExp();
            }
            return this.modifiableLvalue(sc, e);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DelegateFuncptrExp() {}

        public DelegateFuncptrExp copy() {
            DelegateFuncptrExp that = new DelegateFuncptrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IndexExp extends BinExp
    {
        public Ref<VarDeclaration> lengthVar = ref(null);
        public boolean modifiable = false;
        public boolean indexIsInBounds = false;
        // Erasure: __ctor<Loc, Expression, Expression>
        public  IndexExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.index, 46, e1, e2);
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            IndexExp ie = new IndexExp(this.loc, this.e1.value.syntaxCopy(), this.e2.value.syntaxCopy());
            ie.lengthVar.value = this.lengthVar.value;
            return ie;
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            if (((this.e1.value.type.value.ty & 0xFF) == ENUMTY.Tsarray) || ((this.e1.value.type.value.ty & 0xFF) == ENUMTY.Taarray) || ((this.e1.value.op & 0xFF) == 62) && ((this.e1.value.type.value.ty & 0xFF) != ENUMTY.Tarray) || ((this.e1.value.op & 0xFF) == 31))
            {
                return this.e1.value.checkModifiable(sc, flag);
            }
            return Modifiable.yes;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression e) {
            return this;
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            Expression ex = this.markSettingAAElem();
            if (((ex.op & 0xFF) == 127))
            {
                return ex;
            }
            return this.modifiableLvalue(sc, e);
        }

        // Erasure: markSettingAAElem<>
        public  Expression markSettingAAElem() {
            if (((this.e1.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Taarray))
            {
                Type t2b = this.e2.value.type.value.toBasetype();
                if (((t2b.ty & 0xFF) == ENUMTY.Tarray) && t2b.nextOf().isMutable())
                {
                    this.error(new BytePtr("associative arrays can only be assigned values with immutable keys, not `%s`"), this.e2.value.type.value.toChars());
                    return new ErrorExp();
                }
                this.modifiable = true;
                {
                    IndexExp ie = this.e1.value.isIndexExp();
                    if ((ie) != null)
                    {
                        Expression ex = ie.markSettingAAElem();
                        if (((ex.op & 0xFF) == 127))
                        {
                            return ex;
                        }
                        assert((pequals(ex, this.e1.value)));
                    }
                }
            }
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IndexExp() {}

        public IndexExp copy() {
            IndexExp that = new IndexExp();
            that.lengthVar = this.lengthVar;
            that.modifiable = this.modifiable;
            that.indexIsInBounds = this.indexIsInBounds;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PostExp extends BinExp
    {
        // Erasure: __ctor<byte, Loc, Expression>
        public  PostExp(byte op, Loc loc, Expression e) {
            super(loc, op, 40, e, new IntegerExp(loc, 1L, Type.tint32));
            assert(((op & 0xFF) == 94) || ((op & 0xFF) == 93));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PostExp() {}

        public PostExp copy() {
            PostExp that = new PostExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PreExp extends UnaExp
    {
        // Erasure: __ctor<byte, Loc, Expression>
        public  PreExp(byte op, Loc loc, Expression e) {
            super(loc, op, 32, e);
            assert(((op & 0xFF) == 104) || ((op & 0xFF) == 103));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PreExp() {}

        public PreExp copy() {
            PreExp that = new PreExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }

    public static class MemorySet 
    {
        public static final int blockAssign = 1;
        public static final int referenceInit = 2;
    }

    public static class AssignExp extends BinExp
    {
        public int memset = 0;
        // Erasure: __ctor<Loc, Expression, Expression>
        public  AssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.assign, 44, e1, e2);
        }

        // Erasure: __ctor<Loc, byte, Expression, Expression>
        public  AssignExp(Loc loc, byte tok, Expression e1, Expression e2) {
            super(loc, tok, 44, e1, e2);
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            if (((this.e1.value.op & 0xFF) == 31) || ((this.e1.value.op & 0xFF) == 32))
            {
                return false;
            }
            return true;
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression ex) {
            if (((this.e1.value.op & 0xFF) == 31) || ((this.e1.value.op & 0xFF) == 32))
            {
                return this.toLvalue(sc, ex);
            }
            return this;
        }

        // Erasure: toBoolean<Ptr>
        public  Expression toBoolean(Ptr<Scope> sc) {
            this.error(new BytePtr("assignment cannot be used as a condition, perhaps `==` was meant?"));
            return new ErrorExp();
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AssignExp() {}

        public AssignExp copy() {
            AssignExp that = new AssignExp();
            that.memset = this.memset;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ConstructExp extends AssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ConstructExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.construct, e1, e2);
        }

        // Erasure: __ctor<Loc, VarDeclaration, Expression>
        public  ConstructExp(Loc loc, VarDeclaration v, Expression e2) {
            VarExp ve = new VarExp(loc, v, true);
            assert((v.type != null) && (ve.type.value != null));
            super(loc, TOK.construct, ve, e2);
            if ((v.storage_class & 2101248L) != 0)
            {
                this.memset |= MemorySet.referenceInit;
            }
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ConstructExp() {}

        public ConstructExp copy() {
            ConstructExp that = new ConstructExp();
            that.memset = this.memset;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class BlitExp extends AssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  BlitExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.blit, e1, e2);
        }

        // Erasure: __ctor<Loc, VarDeclaration, Expression>
        public  BlitExp(Loc loc, VarDeclaration v, Expression e2) {
            VarExp ve = new VarExp(loc, v, true);
            assert((v.type != null) && (ve.type.value != null));
            super(loc, TOK.blit, ve, e2);
            if ((v.storage_class & 2101248L) != 0)
            {
                this.memset |= MemorySet.referenceInit;
            }
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BlitExp() {}

        public BlitExp copy() {
            BlitExp that = new BlitExp();
            that.memset = this.memset;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AddAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  AddAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.addAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AddAssignExp() {}

        public AddAssignExp copy() {
            AddAssignExp that = new AddAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MinAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  MinAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.minAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MinAssignExp() {}

        public MinAssignExp copy() {
            MinAssignExp that = new MinAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MulAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  MulAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.mulAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MulAssignExp() {}

        public MulAssignExp copy() {
            MulAssignExp that = new MulAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DivAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  DivAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.divAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DivAssignExp() {}

        public DivAssignExp copy() {
            DivAssignExp that = new DivAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ModAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ModAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.modAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ModAssignExp() {}

        public ModAssignExp copy() {
            ModAssignExp that = new ModAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AndAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  AndAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.andAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AndAssignExp() {}

        public AndAssignExp copy() {
            AndAssignExp that = new AndAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class OrAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  OrAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.orAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OrAssignExp() {}

        public OrAssignExp copy() {
            OrAssignExp that = new OrAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class XorAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  XorAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.xorAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public XorAssignExp() {}

        public XorAssignExp copy() {
            XorAssignExp that = new XorAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PowAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  PowAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.powAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PowAssignExp() {}

        public PowAssignExp copy() {
            PowAssignExp that = new PowAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShlAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ShlAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.leftShiftAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShlAssignExp() {}

        public ShlAssignExp copy() {
            ShlAssignExp that = new ShlAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShrAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ShrAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.rightShiftAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShrAssignExp() {}

        public ShrAssignExp copy() {
            ShrAssignExp that = new ShrAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UshrAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  UshrAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.unsignedRightShiftAssign, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UshrAssignExp() {}

        public UshrAssignExp copy() {
            UshrAssignExp that = new UshrAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatAssignExp extends BinAssignExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  CatAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.concatenateAssign, 40, e1, e2);
        }

        // Erasure: __ctor<Loc, byte, Expression, Expression>
        public  CatAssignExp(Loc loc, byte tok, Expression e1, Expression e2) {
            super(loc, tok, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatAssignExp() {}

        public CatAssignExp copy() {
            CatAssignExp that = new CatAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatElemAssignExp extends CatAssignExp
    {
        // Erasure: __ctor<Loc, Type, Expression, Expression>
        public  CatElemAssignExp(Loc loc, Type type, Expression e1, Expression e2) {
            super(loc, TOK.concatenateElemAssign, e1, e2);
            this.type.value = type;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatElemAssignExp() {}

        public CatElemAssignExp copy() {
            CatElemAssignExp that = new CatElemAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatDcharAssignExp extends CatAssignExp
    {
        // Erasure: __ctor<Loc, Type, Expression, Expression>
        public  CatDcharAssignExp(Loc loc, Type type, Expression e1, Expression e2) {
            super(loc, TOK.concatenateDcharAssign, e1, e2);
            this.type.value = type;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatDcharAssignExp() {}

        public CatDcharAssignExp copy() {
            CatDcharAssignExp that = new CatDcharAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AddExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  AddExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.add, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AddExp() {}

        public AddExp copy() {
            AddExp that = new AddExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MinExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  MinExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.min, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MinExp() {}

        public MinExp copy() {
            MinExp that = new MinExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  CatExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.concatenate, 40, e1, e2);
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            this.e1.value = this.e1.value.resolveLoc(loc, sc);
            this.e2.value = this.e2.value.resolveLoc(loc, sc);
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatExp() {}

        public CatExp copy() {
            CatExp that = new CatExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MulExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  MulExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.mul, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MulExp() {}

        public MulExp copy() {
            MulExp that = new MulExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DivExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  DivExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.div, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DivExp() {}

        public DivExp copy() {
            DivExp that = new DivExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ModExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ModExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.mod, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ModExp() {}

        public ModExp copy() {
            ModExp that = new ModExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PowExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  PowExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.pow, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PowExp() {}

        public PowExp copy() {
            PowExp that = new PowExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShlExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ShlExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.leftShift, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShlExp() {}

        public ShlExp copy() {
            ShlExp that = new ShlExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShrExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  ShrExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.rightShift, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShrExp() {}

        public ShrExp copy() {
            ShrExp that = new ShrExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UshrExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  UshrExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.unsignedRightShift, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UshrExp() {}

        public UshrExp copy() {
            UshrExp that = new UshrExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AndExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  AndExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.and, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AndExp() {}

        public AndExp copy() {
            AndExp that = new AndExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class OrExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  OrExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.or, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OrExp() {}

        public OrExp copy() {
            OrExp that = new OrExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class XorExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  XorExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.xor, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public XorExp() {}

        public XorExp copy() {
            XorExp that = new XorExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LogicalExp extends BinExp
    {
        // Erasure: __ctor<Loc, byte, Expression, Expression>
        public  LogicalExp(Loc loc, byte op, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((op & 0xFF) == 101) || ((op & 0xFF) == 102));
        }

        // Erasure: toBoolean<Ptr>
        public  Expression toBoolean(Ptr<Scope> sc) {
            Expression ex2 = this.e2.value.toBoolean(sc);
            if (((ex2.op & 0xFF) == 127))
            {
                return ex2;
            }
            this.e2.value = ex2;
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LogicalExp() {}

        public LogicalExp copy() {
            LogicalExp that = new LogicalExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CmpExp extends BinExp
    {
        // Erasure: __ctor<byte, Loc, Expression, Expression>
        public  CmpExp(byte op, Loc loc, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((op & 0xFF) == 54) || ((op & 0xFF) == 56) || ((op & 0xFF) == 55) || ((op & 0xFF) == 57));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CmpExp() {}

        public CmpExp copy() {
            CmpExp that = new CmpExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class InExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  InExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.in_, 40, e1, e2);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public InExp() {}

        public InExp copy() {
            InExp that = new InExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class RemoveExp extends BinExp
    {
        // Erasure: __ctor<Loc, Expression, Expression>
        public  RemoveExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.remove, 40, e1, e2);
            this.type.value = Type.tbool;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public RemoveExp() {}

        public RemoveExp copy() {
            RemoveExp that = new RemoveExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class EqualExp extends BinExp
    {
        // Erasure: __ctor<byte, Loc, Expression, Expression>
        public  EqualExp(byte op, Loc loc, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((op & 0xFF) == 58) || ((op & 0xFF) == 59));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public EqualExp() {}

        public EqualExp copy() {
            EqualExp that = new EqualExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IdentityExp extends BinExp
    {
        // Erasure: __ctor<byte, Loc, Expression, Expression>
        public  IdentityExp(byte op, Loc loc, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((op & 0xFF) == 60) || ((op & 0xFF) == 61));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IdentityExp() {}

        public IdentityExp copy() {
            IdentityExp that = new IdentityExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CondExp extends BinExp
    {
        public Ref<Expression> econd = ref(null);
        // Erasure: __ctor<Loc, Expression, Expression, Expression>
        public  CondExp(Loc loc, Expression econd, Expression e1, Expression e2) {
            super(loc, TOK.question, 44, e1, e2);
            this.econd.value = econd;
        }

        // Erasure: syntaxCopy<>
        public  Expression syntaxCopy() {
            return new CondExp(this.loc, this.econd.value.syntaxCopy(), this.e1.value.syntaxCopy(), this.e2.value.syntaxCopy());
        }

        // Erasure: checkModifiable<Ptr, int>
        public  int checkModifiable(Ptr<Scope> sc, int flag) {
            if ((this.e1.value.checkModifiable(sc, flag) != Modifiable.no) && (this.e2.value.checkModifiable(sc, flag) != Modifiable.no))
            {
                return Modifiable.yes;
            }
            return Modifiable.no;
        }

        // Erasure: isLvalue<>
        public  boolean isLvalue() {
            return this.e1.value.isLvalue() && this.e2.value.isLvalue();
        }

        // Erasure: toLvalue<Ptr, Expression>
        public  Expression toLvalue(Ptr<Scope> sc, Expression ex) {
            CondExp e = (CondExp)this.copy();
            e.e1.value = this.e1.value.toLvalue(sc, null).addressOf();
            e.e2.value = this.e2.value.toLvalue(sc, null).addressOf();
            e.type.value = this.type.value.pointerTo();
            return new PtrExp(this.loc, e, this.type.value);
        }

        // Erasure: modifiableLvalue<Ptr, Expression>
        public  Expression modifiableLvalue(Ptr<Scope> sc, Expression e) {
            this.e1.value = this.e1.value.modifiableLvalue(sc, this.e1.value);
            this.e2.value = this.e2.value.modifiableLvalue(sc, this.e2.value);
            return this.toLvalue(sc, this);
        }

        // Erasure: toBoolean<Ptr>
        public  Expression toBoolean(Ptr<Scope> sc) {
            Expression ex1 = this.e1.value.toBoolean(sc);
            Expression ex2 = this.e2.value.toBoolean(sc);
            if (((ex1.op & 0xFF) == 127))
            {
                return ex1;
            }
            if (((ex2.op & 0xFF) == 127))
            {
                return ex2;
            }
            this.e1.value = ex1;
            this.e2.value = ex2;
            return this;
        }

        // Erasure: hookDtors<Ptr>
        public  void hookDtors(Ptr<Scope> sc) {
            CondExp __self = this;
            // skipping duplicate class DtorVisitor
            DtorVisitor v = new DtorVisitor(sc, this);
            v.isThen = true;
            walkPostorder(this.e1.value, v);
            v.isThen = false;
            walkPostorder(this.e2.value, v);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CondExp() {}

        public CondExp copy() {
            CondExp that = new CondExp();
            that.econd = this.econd;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DefaultInitExp extends Expression
    {
        public byte subop = 0;
        // Erasure: __ctor<Loc, byte, int>
        public  DefaultInitExp(Loc loc, byte subop, int size) {
            super(loc, TOK.default_, size);
            this.subop = subop;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DefaultInitExp() {}

        public DefaultInitExp copy() {
            DefaultInitExp that = new DefaultInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class FileInitExp extends DefaultInitExp
    {
        // Erasure: __ctor<Loc, byte>
        public  FileInitExp(Loc loc, byte tok) {
            super(loc, tok, 25);
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            BytePtr s = null;
            if (((this.subop & 0xFF) == 220))
            {
                s = pcopy(FileName.toAbsolute(loc.isValid() ? loc.filename : (sc.get())._module.srcfile.toChars(), null));
            }
            else
            {
                s = pcopy((loc.isValid() ? loc.filename : (sc.get())._module.ident.toChars()));
            }
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e = e.castTo(sc, this.type.value);
            return e;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FileInitExp() {}

        public FileInitExp copy() {
            FileInitExp that = new FileInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LineInitExp extends DefaultInitExp
    {
        // Erasure: __ctor<Loc>
        public  LineInitExp(Loc loc) {
            super(loc, TOK.line, 25);
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            Expression e = new IntegerExp(loc, (long)loc.linnum, Type.tint32);
            e = e.castTo(sc, this.type.value);
            return e;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LineInitExp() {}

        public LineInitExp copy() {
            LineInitExp that = new LineInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ModuleInitExp extends DefaultInitExp
    {
        // Erasure: __ctor<Loc>
        public  ModuleInitExp(Loc loc) {
            super(loc, TOK.moduleString, 25);
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            BytePtr s = pcopy((((sc.get()).callsc != null ? (sc.get()).callsc : sc).get())._module.toPrettyChars(false));
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e = e.castTo(sc, this.type.value);
            return e;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ModuleInitExp() {}

        public ModuleInitExp copy() {
            ModuleInitExp that = new ModuleInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class FuncInitExp extends DefaultInitExp
    {
        // Erasure: __ctor<Loc>
        public  FuncInitExp(Loc loc) {
            super(loc, TOK.functionString, 25);
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            BytePtr s = null;
            if (((sc.get()).callsc != null) && (((sc.get()).callsc.get()).func != null))
            {
                s = pcopy(((sc.get()).callsc.get()).func.toPrettyChars(false));
            }
            else if ((sc.get()).func != null)
            {
                s = pcopy((sc.get()).func.toPrettyChars(false));
            }
            else
            {
                s = pcopy(new BytePtr(""));
            }
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e.type.value = Type.tstring;
            return e;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FuncInitExp() {}

        public FuncInitExp copy() {
            FuncInitExp that = new FuncInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PrettyFuncInitExp extends DefaultInitExp
    {
        // Erasure: __ctor<Loc>
        public  PrettyFuncInitExp(Loc loc) {
            super(loc, TOK.prettyFunction, 25);
        }

        // Erasure: resolveLoc<Loc, Ptr>
        public  Expression resolveLoc(Loc loc, Ptr<Scope> sc) {
            FuncDeclaration fd = ((sc.get()).callsc != null) && (((sc.get()).callsc.get()).func != null) ? ((sc.get()).callsc.get()).func : (sc.get()).func;
            BytePtr s = null;
            if (fd != null)
            {
                BytePtr funcStr = pcopy(fd.toPrettyChars(false));
                Ref<OutBuffer> buf = ref(new OutBuffer());
                try {
                    functionToBufferWithIdent(fd.type.isTypeFunction(), ptr(buf), funcStr);
                    s = pcopy(buf.value.extractChars());
                }
                finally {
                }
            }
            else
            {
                s = pcopy(new BytePtr(""));
            }
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e.type.value = Type.tstring;
            return e;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PrettyFuncInitExp() {}

        public PrettyFuncInitExp copy() {
            PrettyFuncInitExp that = new PrettyFuncInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ObjcClassReferenceExp extends Expression
    {
        public ClassDeclaration classDeclaration = null;
        // Erasure: __ctor<Loc, ClassDeclaration>
        public  ObjcClassReferenceExp(Loc loc, ClassDeclaration classDeclaration) {
            super(loc, TOK.objcClassReference, 28);
            this.classDeclaration = classDeclaration;
            this.type.value = objc().getRuntimeMetaclass(classDeclaration).getType();
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ObjcClassReferenceExp() {}

        public ObjcClassReferenceExp copy() {
            ObjcClassReferenceExp that = new ObjcClassReferenceExp();
            that.classDeclaration = this.classDeclaration;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
}
