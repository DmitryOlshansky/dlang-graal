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
import static org.dlang.dmd.arrayop.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.blockexit.*;
import static org.dlang.dmd.clone.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.intrange.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nogc.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class statementsem {
    private static final FuncDeclaration[] initializer_0 = {null, null};
    private static final TypeDelegate[] initializer_1 = {null, null};
    private static final BytePtr[] initializer_2 = {new BytePtr("cc"), new BytePtr("cw"), new BytePtr("cd"), new BytePtr("wc"), new BytePtr("cc"), new BytePtr("wd"), new BytePtr("dc"), new BytePtr("dw"), new BytePtr("dd")};
    static Slice<FuncDeclaration> visitfdapply = slice(initializer_0);
    static Slice<TypeDelegate> visitfldeTy = slice(initializer_1);
    static Slice<BytePtr> visitfntab = slice(initializer_2);

    public static Identifier fixupLabelName(Ptr<Scope> sc, Identifier ident) {
        int flags = (sc.get()).flags & 96;
        ByteSlice id = ident.asString().copy();
        if ((flags != 0) && (flags != 32) && !((id.getLength() >= 2) && ((id.get(0) & 0xFF) == 95) && ((id.get(1) & 0xFF) == 95)))
        {
            OutBuffer buf = new OutBuffer();
            buf.writestring((flags == 64) ? new ByteSlice("__in_") : new ByteSlice("__out_"));
            buf.writestring(ident.asString());
            ident = Identifier.idPool(buf.peekSlice());
        }
        return ident;
    }

    public static LabelStatement checkLabeledLoop(Ptr<Scope> sc, Statement statement) {
        if (((sc.get()).slabel != null) && (pequals((sc.get()).slabel.statement, statement)))
        {
            return (sc.get()).slabel;
        }
        return null;
    }

    public static Expression checkAssignmentAsCondition(Expression e) {
        Expression ec = lastComma(e);
        if (((ec.op & 0xFF) == 90))
        {
            ec.error(new BytePtr("assignment cannot be used as a condition, perhaps `==` was meant?"));
            return new ErrorExp();
        }
        return e;
    }

    public static Statement statementSemantic(Statement s, Ptr<Scope> sc) {
        StatementSemanticVisitor v = new StatementSemanticVisitor(sc);
        s.accept(v);
        return v.result;
    }

    public static class StatementSemanticVisitor extends Visitor
    {
        public Statement result = null;
        public Ptr<Scope> sc = null;
        public  StatementSemanticVisitor(Ptr<Scope> sc) {
            this.sc = pcopy(sc);
        }

        public  void setError() {
            this.result = new ErrorStatement();
        }

        public  void visit(Statement s) {
            this.result = s;
        }

        public  void visit(ErrorStatement s) {
            this.result = s;
        }

        public  void visit(PeelStatement s) {
            this.result = s.s;
        }

        public  void visit(ExpStatement s) {
            if (s.exp != null)
            {
                CommaExp.allow(s.exp);
                s.exp = expressionSemantic(s.exp, this.sc);
                s.exp = resolveProperties(this.sc, s.exp);
                s.exp = s.exp.addDtorHook(this.sc);
                if (checkNonAssignmentArrayOp(s.exp, false))
                {
                    s.exp = new ErrorExp();
                }
                {
                    FuncDeclaration f = isFuncAddress(s.exp, null);
                    if ((f) != null)
                    {
                        if (f.checkForwardRef(s.exp.loc))
                        {
                            s.exp = new ErrorExp();
                        }
                    }
                }
                if (discardValue(s.exp))
                {
                    s.exp = new ErrorExp();
                }
                s.exp = s.exp.optimize(0, false);
                s.exp = checkGC(this.sc, s.exp);
                if (((s.exp.op & 0xFF) == 127))
                {
                    this.setError();
                    return ;
                }
            }
            this.result = s;
        }

        public  void visit(CompileStatement cs) {
            Ptr<DArray<Statement>> a = cs.flatten(this.sc);
            if (a == null)
            {
                return ;
            }
            Statement s = new CompoundStatement(cs.loc, a);
            this.result = statementSemantic(s, this.sc);
        }

        public  void visit(CompoundStatement cs) {
            {
                int i = 0;
                for (; (i < (cs.statements.get()).length);){
                    Statement s = (cs.statements.get()).get(i);
                    if (s != null)
                    {
                        Ptr<DArray<Statement>> flt = s.flatten(this.sc);
                        if (flt != null)
                        {
                            (cs.statements.get()).remove(i);
                            (cs.statements.get()).insert(i, flt);
                            continue;
                        }
                        s = statementSemantic(s, this.sc);
                        cs.statements.get().set(i, s);
                        if (s != null)
                        {
                            Ref<Statement> sentry = ref(null);
                            Ref<Statement> sexception = ref(null);
                            Ref<Statement> sfinally = ref(null);
                            cs.statements.get().set(i, s.scopeCode(this.sc, ptr(sentry), ptr(sexception), ptr(sfinally)));
                            if (sentry.value != null)
                            {
                                sentry.value = statementSemantic(sentry.value, this.sc);
                                (cs.statements.get()).insert(i, sentry.value);
                                i++;
                            }
                            if (sexception.value != null)
                            {
                                sexception.value = statementSemantic(sexception.value, this.sc);
                            }
                            if (sexception.value != null)
                            {
                                Function1<Slice<Statement>,Boolean> isEmpty = new Function1<Slice<Statement>,Boolean>() {
                                    public Boolean invoke(Slice<Statement> statements) {
                                     {
                                        {
                                            Slice<Statement> __r1605 = statements.copy();
                                            Ref<Integer> __key1606 = ref(0);
                                            for (; (__key1606.value < __r1605.getLength());__key1606.value += 1) {
                                                Statement s = __r1605.get(__key1606.value);
                                                {
                                                    CompoundStatement cs = s.isCompoundStatement();
                                                    if ((cs) != null)
                                                    {
                                                        if (!invoke((cs.statements.get()).opSlice()))
                                                        {
                                                            return false;
                                                        }
                                                    }
                                                    else
                                                    {
                                                        return false;
                                                    }
                                                }
                                            }
                                        }
                                        return true;
                                    }}

                                };
                                if ((sfinally.value == null) && isEmpty.invoke((cs.statements.get()).opSlice(i + 1, (cs.statements.get()).length)))
                                {
                                }
                                else
                                {
                                    Ptr<DArray<Statement>> a = refPtr(new DArray<Statement>());
                                    (a.get()).pushSlice((cs.statements.get()).opSlice(i + 1, (cs.statements.get()).length));
                                    (cs.statements.get()).setDim(i + 1);
                                    Statement _body = new CompoundStatement(Loc.initial, a);
                                    _body = new ScopeStatement(Loc.initial, _body, Loc.initial);
                                    Identifier id = Identifier.generateId(new BytePtr("__o"));
                                    Statement handler = new PeelStatement(sexception.value);
                                    if ((blockExit(sexception.value, (this.sc.get()).func, false) & BE.fallthru) != 0)
                                    {
                                        ThrowStatement ts = new ThrowStatement(Loc.initial, new IdentifierExp(Loc.initial, id));
                                        ts.internalThrow = true;
                                        handler = new CompoundStatement(Loc.initial, slice(new Statement[]{handler, ts}));
                                    }
                                    Ptr<DArray<Catch>> catches = refPtr(new DArray<Catch>());
                                    Catch ctch = new Catch(Loc.initial, getThrowable(), id, handler);
                                    ctch.internalCatch = true;
                                    (catches.get()).push(ctch);
                                    Statement st = new TryCatchStatement(Loc.initial, _body, catches);
                                    if (sfinally.value != null)
                                    {
                                        st = new TryFinallyStatement(Loc.initial, st, sfinally.value);
                                    }
                                    st = statementSemantic(st, this.sc);
                                    (cs.statements.get()).push(st);
                                    break;
                                }
                            }
                            else if (sfinally.value != null)
                            {
                                if (false)
                                {
                                    (cs.statements.get()).push(sfinally.value);
                                }
                                else
                                {
                                    Ptr<DArray<Statement>> a = refPtr(new DArray<Statement>());
                                    (a.get()).pushSlice((cs.statements.get()).opSlice(i + 1, (cs.statements.get()).length));
                                    (cs.statements.get()).setDim(i + 1);
                                    CompoundStatement _body = new CompoundStatement(Loc.initial, a);
                                    Statement stf = new TryFinallyStatement(Loc.initial, _body, sfinally.value);
                                    stf = statementSemantic(stf, this.sc);
                                    (cs.statements.get()).push(stf);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            (cs.statements.get()).remove(i);
                            continue;
                        }
                    }
                    i++;
                }
            }
            Function1<Ptr<DArray<Statement>>,Void> flatten = new Function1<Ptr<DArray<Statement>>,Void>() {
                public Void invoke(Ptr<DArray<Statement>> statements) {
                 {
                    {
                        Ref<Integer> i = ref(0);
                        for (; (i.value < (statements.get()).length);){
                            Statement s = (statements.get()).get(i.value);
                            if (s != null)
                            {
                                {
                                    Ptr<DArray<Statement>> flt = s.flatten(sc);
                                    if ((flt) != null)
                                    {
                                        (statements.get()).remove(i.value);
                                        (statements.get()).insert(i.value, flt);
                                        continue;
                                    }
                                }
                            }
                            i.value += 1;
                        }
                    }
                    return null;
                }}

            };
            flatten.invoke(cs.statements);
            {
                Slice<Statement> __r1607 = (cs.statements.get()).opSlice().copy();
                int __key1608 = 0;
                for (; (__key1608 < __r1607.getLength());__key1608 += 1) {
                    Statement s = __r1607.get(__key1608);
                    if (s == null)
                    {
                        continue;
                    }
                    {
                        ErrorStatement se = s.isErrorStatement();
                        if ((se) != null)
                        {
                            this.result = se;
                            return ;
                        }
                    }
                }
            }
            if (((cs.statements.get()).length == 1))
            {
                this.result = (cs.statements.get()).get(0);
                return ;
            }
            this.result = cs;
        }

        public  void visit(UnrolledLoopStatement uls) {
            Ptr<Scope> scd = (this.sc.get()).push();
            (scd.get()).sbreak = uls;
            (scd.get()).scontinue = uls;
            Statement serror = null;
            {
                Slice<Statement> __r1610 = (uls.statements.get()).opSlice().copy();
                int __key1609 = 0;
                for (; (__key1609 < __r1610.getLength());__key1609 += 1) {
                    Statement s = __r1610.get(__key1609);
                    int i = __key1609;
                    if (s != null)
                    {
                        s = statementSemantic(s, scd);
                        if ((s != null) && (serror == null))
                        {
                            serror = s.isErrorStatement();
                        }
                    }
                }
            }
            (scd.get()).pop();
            this.result = serror != null ? serror : uls;
        }

        public  void visit(ScopeStatement ss) {
            if (ss.statement != null)
            {
                ScopeDsymbol sym = new ScopeDsymbol();
                sym.parent.value = (this.sc.get()).scopesym;
                sym.endlinnum = ss.endloc.linnum;
                this.sc = pcopy((this.sc.get()).push(sym));
                Ptr<DArray<Statement>> a = ss.statement.flatten(this.sc);
                if (a != null)
                {
                    ss.statement = new CompoundStatement(ss.loc, a);
                }
                ss.statement = statementSemantic(ss.statement, this.sc);
                if (ss.statement != null)
                {
                    if (ss.statement.isErrorStatement() != null)
                    {
                        (this.sc.get()).pop();
                        this.result = ss.statement;
                        return ;
                    }
                    Ref<Statement> sentry = ref(null);
                    Ref<Statement> sexception = ref(null);
                    Ref<Statement> sfinally = ref(null);
                    ss.statement = ss.statement.scopeCode(this.sc, ptr(sentry), ptr(sexception), ptr(sfinally));
                    assert(sentry.value == null);
                    assert(sexception.value == null);
                    if (sfinally.value != null)
                    {
                        sfinally.value = statementSemantic(sfinally.value, this.sc);
                        ss.statement = new CompoundStatement(ss.loc, slice(new Statement[]{ss.statement, sfinally.value}));
                    }
                }
                (this.sc.get()).pop();
            }
            this.result = ss;
        }

        public  void visit(ForwardingStatement ss) {
            assert(ss.sym != null);
            {
                Ptr<Scope> csc = this.sc;
                for (; ss.sym.forward == null;csc = pcopy((csc.get()).enclosing)){
                    assert(csc != null);
                    ss.sym.forward = (csc.get()).scopesym;
                }
            }
            this.sc = pcopy((this.sc.get()).push(ss.sym));
            (this.sc.get()).sbreak = ss;
            (this.sc.get()).scontinue = ss;
            ss.statement = statementSemantic(ss.statement, this.sc);
            this.sc = pcopy((this.sc.get()).pop());
            this.result = ss.statement;
        }

        public  void visit(WhileStatement ws) {
            Statement s = new ForStatement(ws.loc, null, ws.condition, null, ws._body, ws.endloc);
            s = statementSemantic(s, this.sc);
            this.result = s;
        }

        public  void visit(DoStatement ds) {
            boolean inLoopSave = (this.sc.get()).inLoop;
            (this.sc.get()).inLoop = true;
            if (ds._body != null)
            {
                ds._body = semanticScope(ds._body, this.sc, ds, ds);
            }
            (this.sc.get()).inLoop = inLoopSave;
            if (((ds.condition.op & 0xFF) == 28))
            {
                ((DotIdExp)ds.condition).noderef = true;
            }
            ds.condition = checkAssignmentAsCondition(ds.condition);
            ds.condition = expressionSemantic(ds.condition, this.sc);
            ds.condition = resolveProperties(this.sc, ds.condition);
            if (checkNonAssignmentArrayOp(ds.condition, false))
            {
                ds.condition = new ErrorExp();
            }
            ds.condition = ds.condition.optimize(0, false);
            ds.condition = checkGC(this.sc, ds.condition);
            ds.condition = ds.condition.toBoolean(this.sc);
            if (((ds.condition.op & 0xFF) == 127))
            {
                this.setError();
                return ;
            }
            if ((ds._body != null) && (ds._body.isErrorStatement() != null))
            {
                this.result = ds._body;
                return ;
            }
            this.result = ds;
        }

        public  void visit(ForStatement fs) {
            if (fs._init != null)
            {
                Ptr<DArray<Statement>> ainit = refPtr(new DArray<Statement>());
                (ainit.get()).push(fs._init);
                fs._init = null;
                (ainit.get()).push(fs);
                Statement s = new CompoundStatement(fs.loc, ainit);
                s = new ScopeStatement(fs.loc, s, fs.endloc);
                s = statementSemantic(s, this.sc);
                if (s.isErrorStatement() == null)
                {
                    {
                        LabelStatement ls = checkLabeledLoop(this.sc, fs);
                        if ((ls) != null)
                        {
                            ls.gotoTarget = fs;
                        }
                    }
                    fs.relatedLabeled = s;
                }
                this.result = s;
                return ;
            }
            assert((fs._init == null));
            ScopeDsymbol sym = new ScopeDsymbol();
            sym.parent.value = (this.sc.get()).scopesym;
            sym.endlinnum = fs.endloc.linnum;
            this.sc = pcopy((this.sc.get()).push(sym));
            (this.sc.get()).inLoop = true;
            if (fs.condition != null)
            {
                if (((fs.condition.op & 0xFF) == 28))
                {
                    ((DotIdExp)fs.condition).noderef = true;
                }
                fs.condition = checkAssignmentAsCondition(fs.condition);
                fs.condition = expressionSemantic(fs.condition, this.sc);
                fs.condition = resolveProperties(this.sc, fs.condition);
                if (checkNonAssignmentArrayOp(fs.condition, false))
                {
                    fs.condition = new ErrorExp();
                }
                fs.condition = fs.condition.optimize(0, false);
                fs.condition = checkGC(this.sc, fs.condition);
                fs.condition = fs.condition.toBoolean(this.sc);
            }
            if (fs.increment != null)
            {
                CommaExp.allow(fs.increment);
                fs.increment = expressionSemantic(fs.increment, this.sc);
                fs.increment = resolveProperties(this.sc, fs.increment);
                if (checkNonAssignmentArrayOp(fs.increment, false))
                {
                    fs.increment = new ErrorExp();
                }
                fs.increment = fs.increment.optimize(0, false);
                fs.increment = checkGC(this.sc, fs.increment);
            }
            (this.sc.get()).sbreak = fs;
            (this.sc.get()).scontinue = fs;
            if (fs._body != null)
            {
                fs._body = semanticNoScope(fs._body, this.sc);
            }
            (this.sc.get()).pop();
            if ((fs.condition != null) && ((fs.condition.op & 0xFF) == 127) || (fs.increment != null) && ((fs.increment.op & 0xFF) == 127) || (fs._body != null) && (fs._body.isErrorStatement() != null))
            {
                this.setError();
                return ;
            }
            this.result = fs;
        }

        // from template MakeTupleForeachRet!(0)

        // from template MakeTupleForeachRet!(1)


        // from template makeTupleForeach!(00)
        public  void makeTupleForeach00(ForeachStatement fs) {
            Function0<Void> returnEarly00 = new Function0<Void>() {
                public Void invoke() {
                 {
                    result = new ErrorStatement();
                    return null;
                    return null;
                }}

            };
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length;
            boolean skipCheck = false;
            if ((dim < 1) || (dim > 2))
            {
                fs.error(new BytePtr("only one (value) or two (key,value) arguments for tuple `foreach`"));
                this.setError();
                returnEarly00.invoke();
                return ;
            }
            Type paramtype = (fs.parameters.get()).get(dim - 1).type;
            if (paramtype != null)
            {
                paramtype = typeSemantic(paramtype, loc, this.sc);
                if (((paramtype.ty & 0xFF) == ENUMTY.Terror))
                {
                    this.setError();
                    returnEarly00.invoke();
                    return ;
                }
            }
            Type tab = fs.aggr.value.type.value.toBasetype();
            TypeTuple tuple = (TypeTuple)tab;
            Ptr<DArray<Statement>> statements = refPtr(new DArray<Statement>());
            int n = 0;
            TupleExp te = null;
            if (((fs.aggr.value.op & 0xFF) == 126))
            {
                te = (TupleExp)fs.aggr.value;
                n = (te.exps.get()).length;
            }
            else if (((fs.aggr.value.op & 0xFF) == 20))
            {
                n = Parameter.dim(tuple.arguments);
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            {
                int __key1613 = 0;
                int __limit1614 = n;
                for (; (__key1613 < __limit1614);__key1613 += 1) {
                    int j = __key1613;
                    int k = ((fs.op & 0xFF) == 201) ? j : n - 1 - j;
                    Expression e = null;
                    Type t = null;
                    if (te != null)
                    {
                        e = (te.exps.get()).get(k);
                    }
                    else
                    {
                        t = Parameter.getNth(tuple.arguments, k, null).type;
                    }
                    Parameter p = (fs.parameters.get()).get(0);
                    Ptr<DArray<Statement>> st = refPtr(new DArray<Statement>());
                    boolean skip = false;
                    if ((dim == 2))
                    {
                        if ((p.storageClass & 2109440L) != 0)
                        {
                            fs.error(new BytePtr("no storage class for key `%s`"), p.ident.toChars());
                            this.setError();
                            returnEarly00.invoke();
                            return ;
                        }
                        p.type = typeSemantic(p.type, loc, this.sc);
                        byte keyty = p.type.ty;
                        if (((keyty & 0xFF) != ENUMTY.Tint32) && ((keyty & 0xFF) != ENUMTY.Tuns32))
                        {
                            if (global.params.isLP64)
                            {
                                if (((keyty & 0xFF) != ENUMTY.Tint64) && ((keyty & 0xFF) != ENUMTY.Tuns64))
                                {
                                    fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, `long` or `ulong`, not `%s`"), p.type.toChars());
                                    this.setError();
                                    returnEarly00.invoke();
                                    return ;
                                }
                            }
                            else
                            {
                                fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, not `%s`"), p.type.toChars());
                                this.setError();
                                returnEarly00.invoke();
                                return ;
                            }
                        }
                        Initializer ie = new ExpInitializer(Loc.initial, new IntegerExp((long)k));
                        VarDeclaration var = new VarDeclaration(loc, p.type, p.ident, ie, 0L);
                        var.storage_class |= 8388608L;
                        (st.get()).push(new ExpStatement(loc, var));
                        p = (fs.parameters.get()).get(1);
                    }
                    Function5<Long,Type,Identifier,Expression,Type,Boolean> declareVariable00 = new Function5<Long,Type,Identifier,Expression,Type,Boolean>() {
                        public Boolean invoke(Long storageClass, Type type, Identifier ident, Expression e, Type t) {
                         {
                            Ref<Type> type_ref = ref(type);
                            Ref<Expression> e_ref = ref(e);
                            if (((storageClass & 12288L) != 0) || ((storageClass & 2097152L) != 0) && (te == null))
                            {
                                fs.error(new BytePtr("no storage class for value `%s`"), ident.toChars());
                                setError();
                                return false;
                            }
                            Ref<Declaration> var = ref(null);
                            if (e_ref.value != null)
                            {
                                Type tb = e_ref.value.type.value.toBasetype();
                                Ref<Dsymbol> ds = ref(null);
                                if ((storageClass & 8388608L) == 0)
                                {
                                    if (((tb.ty & 0xFF) == ENUMTY.Tfunction) || ((tb.ty & 0xFF) == ENUMTY.Tsarray) || ((storageClass & 268435456L) != 0) && ((e_ref.value.op & 0xFF) == 26))
                                    {
                                        ds.value = ((VarExp)e_ref.value).var;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 36))
                                    {
                                        ds.value = ((TemplateExp)e_ref.value).td;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 203))
                                    {
                                        ds.value = ((ScopeExp)e_ref.value).sds;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 161))
                                    {
                                        FuncExp fe = (FuncExp)e_ref.value;
                                        ds.value = fe.td != null ? fe.td : fe.fd;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 214))
                                    {
                                        ds.value = ((OverExp)e_ref.value).vars;
                                    }
                                }
                                else if ((storageClass & 268435456L) != 0)
                                {
                                    fs.error(new BytePtr("`foreach` loop variable cannot be both `enum` and `alias`"));
                                    setError();
                                    return false;
                                }
                                if (ds.value != null)
                                {
                                    var.value = new AliasDeclaration(loc, ident, ds.value);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("symbol `%s` cannot be `ref`"), ds.value.toChars());
                                        setError();
                                        return false;
                                    }
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for symbol `%s`"), ds.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else if (((e_ref.value.op & 0xFF) == 20))
                                {
                                    var.value = new AliasDeclaration(loc, ident, e_ref.value.type.value);
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for type `%s`"), e_ref.value.type.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else
                                {
                                    e_ref.value = resolveProperties(sc, e_ref.value);
                                    type_ref.value = e_ref.value.type.value;
                                    if (paramtype != null)
                                    {
                                        type_ref.value = paramtype;
                                    }
                                    Initializer ie = new ExpInitializer(Loc.initial, e_ref.value);
                                    VarDeclaration v = new VarDeclaration(loc, type_ref.value, ident, ie, 0L);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        v.storage_class |= 2113536L;
                                    }
                                    if (((storageClass & 8388608L) != 0) || (e_ref.value.isConst() != 0) || ((e_ref.value.op & 0xFF) == 121) || ((e_ref.value.op & 0xFF) == 49) || ((e_ref.value.op & 0xFF) == 47))
                                    {
                                        if ((v.storage_class & 2097152L) != 0)
                                        {
                                            fs.error(new BytePtr("constant value `%s` cannot be `ref`"), ie.toChars());
                                            setError();
                                            return false;
                                        }
                                        else
                                        {
                                            v.storage_class |= 8388608L;
                                        }
                                    }
                                    var.value = v;
                                }
                            }
                            else
                            {
                                var.value = new AliasDeclaration(loc, ident, t);
                                if (paramtype != null)
                                {
                                    fs.error(new BytePtr("cannot specify element type for symbol `%s`"), fs.toChars());
                                    setError();
                                    return false;
                                }
                            }
                            (st.get()).push(new ExpStatement(loc, var.value));
                            return true;
                        }}

                    };
                    if (!declareVariable00.invoke(p.storageClass, p.type, p.ident, e, t))
                    {
                        returnEarly00.invoke();
                        return ;
                    }
                    if (fs._body.value != null)
                    {
                        (st.get()).push(fs._body.value.syntaxCopy());
                    }
                    Statement res = new CompoundStatement(loc, st);
                    res = new ScopeStatement(loc, res, fs.endloc);
                    (statements.get()).push(res);
                }
            }
            Statement res = new UnrolledLoopStatement(loc, statements);
            {
                LabelStatement ls = checkLabeledLoop(this.sc, fs);
                if ((ls) != null)
                {
                    ls.gotoTarget = res;
                }
            }
            if ((te != null) && (te.e0.value != null))
            {
                res = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(te.e0.value.loc, te.e0.value), res}));
            }
            this.result = res;
        }


        // from template makeTupleForeach!(10)
        public  void makeTupleForeach10(ForeachStatement fs, boolean _param_1) {
            Function0<Void> returnEarly10 = new Function0<Void>() {
                public Void invoke() {
                 {
                    result = new ErrorStatement();
                    return null;
                    return null;
                }}

            };
            boolean needExpansion = _param_1;
            assert(this.sc != null);
            ScopeDsymbol previous = (this.sc.get()).scopesym;
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length;
            boolean skipCheck = needExpansion;
            if (!skipCheck && (dim < 1) || (dim > 2))
            {
                fs.error(new BytePtr("only one (value) or two (key,value) arguments for tuple `foreach`"));
                this.setError();
                returnEarly10.invoke();
                return ;
            }
            Type paramtype = (fs.parameters.get()).get(dim - 1).type;
            if (paramtype != null)
            {
                paramtype = typeSemantic(paramtype, loc, this.sc);
                if (((paramtype.ty & 0xFF) == ENUMTY.Terror))
                {
                    this.setError();
                    returnEarly10.invoke();
                    return ;
                }
            }
            Type tab = fs.aggr.value.type.value.toBasetype();
            TypeTuple tuple = (TypeTuple)tab;
            Ptr<DArray<Statement>> statements = refPtr(new DArray<Statement>());
            int n = 0;
            TupleExp te = null;
            if (((fs.aggr.value.op & 0xFF) == 126))
            {
                te = (TupleExp)fs.aggr.value;
                n = (te.exps.get()).length;
            }
            else if (((fs.aggr.value.op & 0xFF) == 20))
            {
                n = Parameter.dim(tuple.arguments);
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            {
                int __key1591 = 0;
                int __limit1592 = n;
                for (; (__key1591 < __limit1592);__key1591 += 1) {
                    int j = __key1591;
                    int k = ((fs.op & 0xFF) == 201) ? j : n - 1 - j;
                    Expression e = null;
                    Type t = null;
                    if (te != null)
                    {
                        e = (te.exps.get()).get(k);
                    }
                    else
                    {
                        t = Parameter.getNth(tuple.arguments, k, null).type;
                    }
                    Parameter p = (fs.parameters.get()).get(0);
                    Ptr<DArray<Statement>> st = refPtr(new DArray<Statement>());
                    boolean skip = needExpansion;
                    if (!skip && (dim == 2))
                    {
                        if ((p.storageClass & 2109440L) != 0)
                        {
                            fs.error(new BytePtr("no storage class for key `%s`"), p.ident.toChars());
                            this.setError();
                            returnEarly10.invoke();
                            return ;
                        }
                        if (p.type == null)
                        {
                            p.type = Type.tsize_t;
                        }
                        p.type = typeSemantic(p.type, loc, this.sc);
                        byte keyty = p.type.ty;
                        if (((keyty & 0xFF) != ENUMTY.Tint32) && ((keyty & 0xFF) != ENUMTY.Tuns32))
                        {
                            if (global.params.isLP64)
                            {
                                if (((keyty & 0xFF) != ENUMTY.Tint64) && ((keyty & 0xFF) != ENUMTY.Tuns64))
                                {
                                    fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, `long` or `ulong`, not `%s`"), p.type.toChars());
                                    this.setError();
                                    returnEarly10.invoke();
                                    return ;
                                }
                            }
                            else
                            {
                                fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, not `%s`"), p.type.toChars());
                                this.setError();
                                returnEarly10.invoke();
                                return ;
                            }
                        }
                        Initializer ie = new ExpInitializer(Loc.initial, new IntegerExp((long)k));
                        VarDeclaration var = new VarDeclaration(loc, p.type, p.ident, ie, 0L);
                        var.storage_class |= 8388608L;
                        var.storage_class |= 2251799813685248L;
                        (st.get()).push(new ExpStatement(loc, var));
                        p = (fs.parameters.get()).get(1);
                    }
                    Function5<Long,Type,Identifier,Expression,Type,Boolean> declareVariable10 = new Function5<Long,Type,Identifier,Expression,Type,Boolean>() {
                        public Boolean invoke(Long storageClass, Type type, Identifier ident, Expression e, Type t) {
                         {
                            Ref<Type> type_ref = ref(type);
                            Ref<Expression> e_ref = ref(e);
                            if (((storageClass & 12288L) != 0) || ((storageClass & 2097152L) != 0) && (te == null))
                            {
                                fs.error(new BytePtr("no storage class for value `%s`"), ident.toChars());
                                setError();
                                return false;
                            }
                            Ref<Declaration> var = ref(null);
                            if (e_ref.value != null)
                            {
                                Type tb = e_ref.value.type.value.toBasetype();
                                Ref<Dsymbol> ds = ref(null);
                                if ((storageClass & 8388608L) == 0)
                                {
                                    if (((e_ref.value.op & 0xFF) == 26))
                                    {
                                        ds.value = ((VarExp)e_ref.value).var;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 36))
                                    {
                                        ds.value = ((TemplateExp)e_ref.value).td;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 203))
                                    {
                                        ds.value = ((ScopeExp)e_ref.value).sds;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 161))
                                    {
                                        FuncExp fe = (FuncExp)e_ref.value;
                                        ds.value = fe.td != null ? fe.td : fe.fd;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 214))
                                    {
                                        ds.value = ((OverExp)e_ref.value).vars;
                                    }
                                }
                                else if ((storageClass & 268435456L) != 0)
                                {
                                    fs.error(new BytePtr("`foreach` loop variable cannot be both `enum` and `alias`"));
                                    setError();
                                    return false;
                                }
                                if (ds.value != null)
                                {
                                    var.value = new AliasDeclaration(loc, ident, ds.value);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("symbol `%s` cannot be `ref`"), ds.value.toChars());
                                        setError();
                                        return false;
                                    }
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for symbol `%s`"), ds.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else if (((e_ref.value.op & 0xFF) == 20))
                                {
                                    var.value = new AliasDeclaration(loc, ident, e_ref.value.type.value);
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for type `%s`"), e_ref.value.type.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else
                                {
                                    e_ref.value = resolveProperties(sc, e_ref.value);
                                    type_ref.value = e_ref.value.type.value;
                                    if (paramtype != null)
                                    {
                                        type_ref.value = paramtype;
                                    }
                                    Initializer ie = new ExpInitializer(Loc.initial, e_ref.value);
                                    VarDeclaration v = new VarDeclaration(loc, type_ref.value, ident, ie, 0L);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        v.storage_class |= 2113536L;
                                    }
                                    if (true)
                                    {
                                        if ((v.storage_class & 2097152L) != 0)
                                        {
                                            if (!needExpansion)
                                            {
                                                fs.error(new BytePtr("constant value `%s` cannot be `ref`"), ie.toChars());
                                            }
                                            else
                                            {
                                                fs.error(new BytePtr("constant value `%s` cannot be `ref`"), ident.toChars());
                                            }
                                            setError();
                                            return false;
                                        }
                                        else
                                        {
                                            v.storage_class |= 8388608L;
                                        }
                                    }
                                    var.value = v;
                                }
                            }
                            else
                            {
                                var.value = new AliasDeclaration(loc, ident, t);
                                if (paramtype != null)
                                {
                                    fs.error(new BytePtr("cannot specify element type for symbol `%s`"), fs.toChars());
                                    setError();
                                    return false;
                                }
                            }
                            var.value.storage_class |= 2251799813685248L;
                            (st.get()).push(new ExpStatement(loc, var.value));
                            return true;
                        }}

                    };
                    if (!needExpansion)
                    {
                        if (!declareVariable10.invoke(p.storageClass, p.type, p.ident, e, t))
                        {
                            returnEarly10.invoke();
                            return ;
                        }
                    }
                    else
                    {
                        assert((e != null) && (t == null));
                        Identifier ident = Identifier.generateId(new BytePtr("__value"));
                        declareVariable10.invoke(0L, e.type.value, ident, e, null);
                        Identifier field = Identifier.idPool(toBytePtr(StaticForeach.tupleFieldName), 5);
                        Expression access = new DotIdExp(loc, e, field);
                        access = expressionSemantic(access, this.sc);
                        if (tuple == null)
                        {
                            returnEarly10.invoke();
                            return ;
                        }
                        {
                            int __key1593 = 0;
                            int __limit1594 = dim;
                            for (; (__key1593 < __limit1594);__key1593 += 1) {
                                int l = __key1593;
                                Parameter cp = (fs.parameters.get()).get(l);
                                Expression init_ = new IndexExp(loc, access, new IntegerExp(loc, (long)l, Type.tsize_t));
                                init_ = expressionSemantic(init_, this.sc);
                                assert(init_.type.value != null);
                                declareVariable10.invoke(p.storageClass, init_.type.value, cp.ident, init_, null);
                            }
                        }
                    }
                    if (fs._body.value != null)
                    {
                        (st.get()).push(fs._body.value.syntaxCopy());
                    }
                    Statement res = new CompoundStatement(loc, st);
                    ForwardingStatement fwd = new ForwardingStatement(loc, res);
                    previous = fwd.sym;
                    res = fwd;
                    (statements.get()).push(res);
                }
            }
            Statement res = new CompoundStatement(loc, statements);
            this.result = res;
        }


        // from template makeTupleForeach!(11)
        public  Ptr<DArray<Dsymbol>> makeTupleForeach11(ForeachStatement fs, Ptr<DArray<Dsymbol>> _param_1, boolean _param_2) {
            Function0<Object> returnEarly11 = new Function0<Object>() {
                public Object invoke() {
                 {
                    return null;
                }}

            };
            Ptr<DArray<Dsymbol>> dbody = _param_1;
            boolean needExpansion = _param_2;
            assert(this.sc != null);
            ScopeDsymbol previous = (this.sc.get()).scopesym;
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length;
            boolean skipCheck = needExpansion;
            if (!skipCheck && (dim < 1) || (dim > 2))
            {
                fs.error(new BytePtr("only one (value) or two (key,value) arguments for tuple `foreach`"));
                this.setError();
                return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
            }
            Type paramtype = (fs.parameters.get()).get(dim - 1).type;
            if (paramtype != null)
            {
                paramtype = typeSemantic(paramtype, loc, this.sc);
                if (((paramtype.ty & 0xFF) == ENUMTY.Terror))
                {
                    this.setError();
                    return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                }
            }
            Type tab = fs.aggr.value.type.value.toBasetype();
            TypeTuple tuple = (TypeTuple)tab;
            Ptr<DArray<Dsymbol>> declarations = refPtr(new DArray<Dsymbol>());
            int n = 0;
            TupleExp te = null;
            if (((fs.aggr.value.op & 0xFF) == 126))
            {
                te = (TupleExp)fs.aggr.value;
                n = (te.exps.get()).length;
            }
            else if (((fs.aggr.value.op & 0xFF) == 20))
            {
                n = Parameter.dim(tuple.arguments);
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            {
                int __key793 = 0;
                int __limit794 = n;
                for (; (__key793 < __limit794);__key793 += 1) {
                    int j = __key793;
                    int k = ((fs.op & 0xFF) == 201) ? j : n - 1 - j;
                    Expression e = null;
                    Type t = null;
                    if (te != null)
                    {
                        e = (te.exps.get()).get(k);
                    }
                    else
                    {
                        t = Parameter.getNth(tuple.arguments, k, null).type;
                    }
                    Parameter p = (fs.parameters.get()).get(0);
                    Ptr<DArray<Dsymbol>> st = refPtr(new DArray<Dsymbol>());
                    boolean skip = needExpansion;
                    if (!skip && (dim == 2))
                    {
                        if ((p.storageClass & 2109440L) != 0)
                        {
                            fs.error(new BytePtr("no storage class for key `%s`"), p.ident.toChars());
                            this.setError();
                            return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                        }
                        if (p.type == null)
                        {
                            p.type = Type.tsize_t;
                        }
                        p.type = typeSemantic(p.type, loc, this.sc);
                        byte keyty = p.type.ty;
                        if (((keyty & 0xFF) != ENUMTY.Tint32) && ((keyty & 0xFF) != ENUMTY.Tuns32))
                        {
                            if (global.params.isLP64)
                            {
                                if (((keyty & 0xFF) != ENUMTY.Tint64) && ((keyty & 0xFF) != ENUMTY.Tuns64))
                                {
                                    fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, `long` or `ulong`, not `%s`"), p.type.toChars());
                                    this.setError();
                                    return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                                }
                            }
                            else
                            {
                                fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, not `%s`"), p.type.toChars());
                                this.setError();
                                return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                            }
                        }
                        Initializer ie = new ExpInitializer(Loc.initial, new IntegerExp((long)k));
                        VarDeclaration var = new VarDeclaration(loc, p.type, p.ident, ie, 0L);
                        var.storage_class |= 8388608L;
                        var.storage_class |= 2251799813685248L;
                        (st.get()).push(var);
                        p = (fs.parameters.get()).get(1);
                    }
                    Function5<Long,Type,Identifier,Expression,Type,Boolean> declareVariable11 = new Function5<Long,Type,Identifier,Expression,Type,Boolean>() {
                        public Boolean invoke(Long storageClass, Type type, Identifier ident, Expression e, Type t) {
                         {
                            Ref<Type> type_ref = ref(type);
                            Ref<Expression> e_ref = ref(e);
                            if (((storageClass & 12288L) != 0) || ((storageClass & 2097152L) != 0) && (te == null))
                            {
                                fs.error(new BytePtr("no storage class for value `%s`"), ident.toChars());
                                setError();
                                return false;
                            }
                            Ref<Declaration> var = ref(null);
                            if (e_ref.value != null)
                            {
                                Type tb = e_ref.value.type.value.toBasetype();
                                Ref<Dsymbol> ds = ref(null);
                                if ((storageClass & 8388608L) == 0)
                                {
                                    if (((e_ref.value.op & 0xFF) == 26))
                                    {
                                        ds.value = ((VarExp)e_ref.value).var;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 36))
                                    {
                                        ds.value = ((TemplateExp)e_ref.value).td;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 203))
                                    {
                                        ds.value = ((ScopeExp)e_ref.value).sds;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 161))
                                    {
                                        FuncExp fe = (FuncExp)e_ref.value;
                                        ds.value = fe.td != null ? fe.td : fe.fd;
                                    }
                                    else if (((e_ref.value.op & 0xFF) == 214))
                                    {
                                        ds.value = ((OverExp)e_ref.value).vars;
                                    }
                                }
                                else if ((storageClass & 268435456L) != 0)
                                {
                                    fs.error(new BytePtr("`foreach` loop variable cannot be both `enum` and `alias`"));
                                    setError();
                                    return false;
                                }
                                if (ds.value != null)
                                {
                                    var.value = new AliasDeclaration(loc, ident, ds.value);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("symbol `%s` cannot be `ref`"), ds.value.toChars());
                                        setError();
                                        return false;
                                    }
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for symbol `%s`"), ds.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else if (((e_ref.value.op & 0xFF) == 20))
                                {
                                    var.value = new AliasDeclaration(loc, ident, e_ref.value.type.value);
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for type `%s`"), e_ref.value.type.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else
                                {
                                    e_ref.value = resolveProperties(sc, e_ref.value);
                                    type_ref.value = e_ref.value.type.value;
                                    if (paramtype != null)
                                    {
                                        type_ref.value = paramtype;
                                    }
                                    Initializer ie = new ExpInitializer(Loc.initial, e_ref.value);
                                    VarDeclaration v = new VarDeclaration(loc, type_ref.value, ident, ie, 0L);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        v.storage_class |= 2113536L;
                                    }
                                    if (true)
                                    {
                                        if ((v.storage_class & 2097152L) != 0)
                                        {
                                            if (!needExpansion)
                                            {
                                                fs.error(new BytePtr("constant value `%s` cannot be `ref`"), ie.toChars());
                                            }
                                            else
                                            {
                                                fs.error(new BytePtr("constant value `%s` cannot be `ref`"), ident.toChars());
                                            }
                                            setError();
                                            return false;
                                        }
                                        else
                                        {
                                            v.storage_class |= 8388608L;
                                        }
                                    }
                                    var.value = v;
                                }
                            }
                            else
                            {
                                var.value = new AliasDeclaration(loc, ident, t);
                                if (paramtype != null)
                                {
                                    fs.error(new BytePtr("cannot specify element type for symbol `%s`"), fs.toChars());
                                    setError();
                                    return false;
                                }
                            }
                            var.value.storage_class |= 2251799813685248L;
                            (st.get()).push(var.value);
                            return true;
                        }}

                    };
                    if (!needExpansion)
                    {
                        if (!declareVariable11.invoke(p.storageClass, p.type, p.ident, e, t))
                        {
                            return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                        }
                    }
                    else
                    {
                        assert((e != null) && (t == null));
                        Identifier ident = Identifier.generateId(new BytePtr("__value"));
                        declareVariable11.invoke(0L, e.type.value, ident, e, null);
                        Identifier field = Identifier.idPool(toBytePtr(StaticForeach.tupleFieldName), 5);
                        Expression access = new DotIdExp(loc, e, field);
                        access = expressionSemantic(access, this.sc);
                        if (tuple == null)
                        {
                            return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                        }
                        {
                            int __key795 = 0;
                            int __limit796 = dim;
                            for (; (__key795 < __limit796);__key795 += 1) {
                                int l = __key795;
                                Parameter cp = (fs.parameters.get()).get(l);
                                Expression init_ = new IndexExp(loc, access, new IntegerExp(loc, (long)l, Type.tsize_t));
                                init_ = expressionSemantic(init_, this.sc);
                                assert(init_.type.value != null);
                                declareVariable11.invoke(p.storageClass, init_.type.value, cp.ident, init_, null);
                            }
                        }
                    }
                    (st.get()).append(Dsymbol.arraySyntaxCopy(dbody));
                    ForwardingAttribDeclaration res = new ForwardingAttribDeclaration(st);
                    previous = res.sym;
                    (declarations.get()).push(res);
                }
            }
            Ptr<DArray<Dsymbol>> res = declarations;
            return res;
        }


        public  void visit(ForeachStatement fs) {
            Function1<ForeachStatement,Boolean> checkForArgTypes = new Function1<ForeachStatement,Boolean>() {
                public Boolean invoke(ForeachStatement fs) {
                 {
                    Ref<Boolean> result = ref(false);
                    {
                        Slice<Parameter> __r1611 = (fs.parameters.get()).opSlice().copy();
                        Ref<Integer> __key1612 = ref(0);
                        for (; (__key1612.value < __r1611.getLength());__key1612.value += 1) {
                            Parameter p = __r1611.get(__key1612.value);
                            if (p.type == null)
                            {
                                fs.error(new BytePtr("cannot infer type for `foreach` variable `%s`, perhaps set it explicitly"), p.ident.toChars());
                                p.type = Type.terror;
                                result.value = true;
                            }
                        }
                    }
                    return result.value;
                }}

            };
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length;
            TypeAArray taa = null;
            Type tn = null;
            Type tnv = null;
            fs.func = (this.sc.get()).func;
            if (fs.func.fes != null)
            {
                fs.func = fs.func.fes.func;
            }
            VarDeclaration vinit = null;
            fs.aggr.value = expressionSemantic(fs.aggr.value, this.sc);
            fs.aggr.value = resolveProperties(this.sc, fs.aggr.value);
            fs.aggr.value = fs.aggr.value.optimize(0, false);
            if (((fs.aggr.value.op & 0xFF) == 127))
            {
                this.setError();
                return ;
            }
            Expression oaggr = fs.aggr.value;
            if ((fs.aggr.value.type.value != null) && ((fs.aggr.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)fs.aggr.value.type.value.toBasetype()).sym.dtor != null) && ((fs.aggr.value.op & 0xFF) != 20) && !fs.aggr.value.isLvalue())
            {
                vinit = copyToTemp(2199023255552L, new BytePtr("__aggr"), fs.aggr.value);
                vinit.endlinnum = fs.endloc.linnum;
                dsymbolSemantic(vinit, this.sc);
                fs.aggr.value = new VarExp(fs.aggr.value.loc, vinit, true);
            }
            Ref<Dsymbol> sapply = ref(null);
            if (!inferForeachAggregate(this.sc, (fs.op & 0xFF) == 201, aggr, sapply))
            {
                BytePtr msg = pcopy(new BytePtr(""));
                if ((fs.aggr.value.type.value != null) && (isAggregate(fs.aggr.value.type.value) != null))
                {
                    msg = pcopy(new BytePtr(", define `opApply()`, range primitives, or use `.tupleof`"));
                }
                fs.error(new BytePtr("invalid `foreach` aggregate `%s`%s"), oaggr.toChars(), msg);
                this.setError();
                return ;
            }
            Dsymbol sapplyOld = sapply.value;
            if (!inferApplyArgTypes(fs, this.sc, sapply))
            {
                boolean foundMismatch = false;
                int foreachParamCount = 0;
                if (sapplyOld != null)
                {
                    {
                        FuncDeclaration fd = sapplyOld.isFuncDeclaration();
                        if ((fd) != null)
                        {
                            ParameterList fparameters = fd.getParameterList().copy();
                            if ((fparameters.length() == 1))
                            {
                                Parameter fparam = fparameters.get(0);
                                if (((fparam.type.ty & 0xFF) == ENUMTY.Tpointer) || ((fparam.type.ty & 0xFF) == ENUMTY.Tdelegate) && ((fparam.type.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
                                {
                                    TypeFunction tf = (TypeFunction)fparam.type.nextOf();
                                    foreachParamCount = tf.parameterList.length();
                                    foundMismatch = true;
                                }
                            }
                        }
                    }
                }
                if (foundMismatch && (dim != foreachParamCount))
                {
                    BytePtr plural = pcopy((foreachParamCount > 1) ? new BytePtr("s") : new BytePtr(""));
                    fs.error(new BytePtr("cannot infer argument types, expected %d argument%s, not %d"), foreachParamCount, plural, dim);
                }
                else
                {
                    fs.error(new BytePtr("cannot uniquely infer `foreach` argument types"));
                }
                this.setError();
                return ;
            }
            Type tab = fs.aggr.value.type.value.toBasetype();
            if (((tab.ty & 0xFF) == ENUMTY.Ttuple))
            {
                this.makeTupleForeach00(fs);
                if (vinit != null)
                {
                    this.result = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, vinit), this.result}));
                }
                this.result = statementSemantic(this.result, this.sc);
                return ;
            }
            ScopeDsymbol sym = new ScopeDsymbol();
            sym.parent.value = (this.sc.get()).scopesym;
            sym.endlinnum = fs.endloc.linnum;
            Ptr<Scope> sc2 = (this.sc.get()).push(sym);
            (sc2.get()).inLoop = true;
            {
                Slice<Parameter> __r1615 = (fs.parameters.get()).opSlice().copy();
                int __key1616 = 0;
                for (; (__key1616 < __r1615.getLength());__key1616 += 1) {
                    Parameter p = __r1615.get(__key1616);
                    if ((p.storageClass & 8388608L) != 0)
                    {
                        fs.error(new BytePtr("cannot declare `enum` loop variables for non-unrolled foreach"));
                    }
                    if ((p.storageClass & 268435456L) != 0)
                    {
                        fs.error(new BytePtr("cannot declare `alias` loop variables for non-unrolled foreach"));
                    }
                }
            }
            Statement s = fs;
            {
                int __dispatch0 = 0;
                dispatched_0:
                do {
                    switch (__dispatch0 != 0 ? __dispatch0 : (tab.ty & 0xFF))
                    {
                        case 0:
                        case 1:
                            if (checkForArgTypes.invoke(fs))
                            {
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            }
                            if ((dim < 1) || (dim > 2))
                            {
                                fs.error(new BytePtr("only one or two arguments for array `foreach`"));
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            }
                            {
                                int __key1617 = 0;
                                int __limit1618 = dim;
                                for (; (__key1617 < __limit1618);__key1617 += 1) {
                                    int i = __key1617;
                                    Parameter p = (fs.parameters.get()).get(i);
                                    p.type = typeSemantic(p.type, loc, sc2);
                                    p.type = p.type.addStorageClass(p.storageClass);
                                }
                            }
                            tn = tab.nextOf().toBasetype();
                            if ((dim == 2))
                            {
                                Type tindex = (fs.parameters.get()).get(0).type;
                                if (!tindex.isintegral())
                                {
                                    fs.error(new BytePtr("foreach: key cannot be of non-integral type `%s`"), tindex.toChars());
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                Type tv = (fs.parameters.get()).get(1).type.toBasetype();
                                if (((tab.ty & 0xFF) == ENUMTY.Tarray) || ((tn.ty & 0xFF) != (tv.ty & 0xFF)) && ((tn.ty & 0xFF) == ENUMTY.Tchar) || ((tn.ty & 0xFF) == ENUMTY.Twchar) || ((tn.ty & 0xFF) == ENUMTY.Tdchar) && ((tv.ty & 0xFF) == ENUMTY.Tchar) || ((tv.ty & 0xFF) == ENUMTY.Twchar) || ((tv.ty & 0xFF) == ENUMTY.Tdchar) && (Type.tsize_t.implicitConvTo(tindex) == 0))
                                {
                                    fs.deprecation(new BytePtr("foreach: loop index implicitly converted from `size_t` to `%s`"), tindex.toChars());
                                }
                            }
                            if (((tn.ty & 0xFF) == ENUMTY.Tchar) || ((tn.ty & 0xFF) == ENUMTY.Twchar) || ((tn.ty & 0xFF) == ENUMTY.Tdchar))
                            {
                                int i_1 = (dim == 1) ? 0 : 1;
                                Parameter p_1 = (fs.parameters.get()).get(i_1);
                                tnv = p_1.type.toBasetype();
                                if (((tnv.ty & 0xFF) != (tn.ty & 0xFF)) && ((tnv.ty & 0xFF) == ENUMTY.Tchar) || ((tnv.ty & 0xFF) == ENUMTY.Twchar) || ((tnv.ty & 0xFF) == ENUMTY.Tdchar))
                                {
                                    if ((p_1.storageClass & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("`foreach`: value of UTF conversion cannot be `ref`"));
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    if ((dim == 2))
                                    {
                                        p_1 = (fs.parameters.get()).get(0);
                                        if ((p_1.storageClass & 2097152L) != 0)
                                        {
                                            fs.error(new BytePtr("`foreach`: key cannot be `ref`"));
                                            /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                        }
                                    }
                                    /*goto Lapply*/{ __dispatch0 = -1; continue dispatched_0; }
                                }
                            }
                            {
                                int __key1619 = 0;
                                int __limit1620 = dim;
                            L_outer1:
                                for (; (__key1619 < __limit1620);__key1619 += 1) {
                                    int i_2 = __key1619;
                                    Parameter p_2 = (fs.parameters.get()).get(i_2);
                                    VarDeclaration var = null;
                                    if ((dim == 2) && (i_2 == 0))
                                    {
                                        var = new VarDeclaration(loc, p_2.type.mutableOf(), Identifier.generateId(new BytePtr("__key")), null, 0L);
                                        var.storage_class |= 1099511644160L;
                                        if ((var.storage_class & 2101248L) != 0)
                                        {
                                            var.storage_class |= 16777216L;
                                        }
                                        fs.key = var;
                                        if ((p_2.storageClass & 2097152L) != 0)
                                        {
                                            if ((var.type.constConv(p_2.type) <= MATCH.nomatch))
                                            {
                                                fs.error(new BytePtr("key type mismatch, `%s` to `ref %s`"), var.type.toChars(), p_2.type.toChars());
                                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                            }
                                        }
                                        if (((tab.ty & 0xFF) == ENUMTY.Tsarray))
                                        {
                                            TypeSArray ta = (TypeSArray)tab;
                                            IntRange dimrange = getIntRange(ta.dim).copy();
                                            if (!IntRange.fromType(var.type).contains(dimrange))
                                            {
                                                fs.error(new BytePtr("index type `%s` cannot cover index range 0..%llu"), p_2.type.toChars(), ta.dim.toInteger());
                                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                            }
                                            fs.key.range = pcopy((refPtr(new IntRange(new SignExtendedNumber(0L, false), dimrange.imax))));
                                        }
                                    }
                                    else
                                    {
                                        var = new VarDeclaration(loc, p_2.type, p_2.ident, null, 0L);
                                        var.storage_class |= 16384L;
                                        var.storage_class |= p_2.storageClass & 2687506436L;
                                        if ((var.storage_class & 2101248L) != 0)
                                        {
                                            var.storage_class |= 16777216L;
                                        }
                                        fs.value = var;
                                        if ((var.storage_class & 2097152L) != 0)
                                        {
                                            if ((fs.aggr.value.checkModifiable(sc2, 1) == Modifiable.initialization))
                                            {
                                                var.storage_class |= 131072L;
                                            }
                                            Type t = tab.nextOf();
                                            if ((t.constConv(p_2.type) <= MATCH.nomatch))
                                            {
                                                fs.error(new BytePtr("argument type mismatch, `%s` to `ref %s`"), t.toChars(), p_2.type.toChars());
                                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                            }
                                        }
                                    }
                                }
                            }
                            Identifier id = Identifier.generateId(new BytePtr("__r"));
                            ExpInitializer ie = new ExpInitializer(loc, new SliceExp(loc, fs.aggr.value, null, null));
                            VarDeclaration tmp = null;
                            if (((fs.aggr.value.op & 0xFF) == 47) && (((fs.parameters.get()).get(dim - 1).storageClass & 2097152L) == 0))
                            {
                                ArrayLiteralExp ale = (ArrayLiteralExp)fs.aggr.value;
                                int edim = ale.elements != null ? (ale.elements.get()).length : 0;
                                Type telem = (fs.parameters.get()).get(dim - 1).type;
                                fs.aggr.value = fs.aggr.value.implicitCastTo(this.sc, telem.sarrayOf((long)edim));
                                if (((fs.aggr.value.op & 0xFF) == 127))
                                {
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                tmp = new VarDeclaration(loc, fs.aggr.value.type.value, id, ie, 0L);
                            }
                            else
                            {
                                tmp = new VarDeclaration(loc, tab.nextOf().arrayOf(), id, ie, 0L);
                            }
                            tmp.storage_class |= 1099511627776L;
                            Expression tmp_length = new DotIdExp(loc, new VarExp(loc, tmp, true), Id.length);
                            if (fs.key == null)
                            {
                                Identifier idkey = Identifier.generateId(new BytePtr("__key"));
                                fs.key = new VarDeclaration(loc, Type.tsize_t, idkey, null, 0L);
                                fs.key.storage_class |= 1099511627776L;
                            }
                            else if (((fs.key.type.ty & 0xFF) != (Type.tsize_t.ty & 0xFF)))
                            {
                                tmp_length = new CastExp(loc, tmp_length, fs.key.type);
                            }
                            if (((fs.op & 0xFF) == 202))
                            {
                                fs.key._init = new ExpInitializer(loc, tmp_length);
                            }
                            else
                            {
                                fs.key._init = new ExpInitializer(loc, new IntegerExp(loc, 0L, fs.key.type));
                            }
                            Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
                            if (vinit != null)
                            {
                                (cs.get()).push(new ExpStatement(loc, vinit));
                            }
                            (cs.get()).push(new ExpStatement(loc, tmp));
                            (cs.get()).push(new ExpStatement(loc, fs.key));
                            Statement forinit = new CompoundDeclarationStatement(loc, cs);
                            Expression cond = null;
                            if (((fs.op & 0xFF) == 202))
                            {
                                cond = new PostExp(TOK.minusMinus, loc, new VarExp(loc, fs.key, true));
                            }
                            else
                            {
                                cond = new CmpExp(TOK.lessThan, loc, new VarExp(loc, fs.key, true), tmp_length);
                            }
                            Expression increment = null;
                            if (((fs.op & 0xFF) == 201))
                            {
                                increment = new AddAssignExp(loc, new VarExp(loc, fs.key, true), new IntegerExp(loc, 1L, fs.key.type));
                            }
                            IndexExp indexExp = new IndexExp(loc, new VarExp(loc, tmp, true), new VarExp(loc, fs.key, true));
                            indexExp.indexIsInBounds = true;
                            fs.value._init = new ExpInitializer(loc, indexExp);
                            Statement ds = new ExpStatement(loc, fs.value);
                            if ((dim == 2))
                            {
                                Parameter p_3 = (fs.parameters.get()).get(0);
                                if (((p_3.storageClass & 2097152L) != 0) && p_3.type.equals(fs.key.type))
                                {
                                    fs.key.range = null;
                                    AliasDeclaration v = new AliasDeclaration(loc, p_3.ident, fs.key);
                                    fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v), fs._body.value}));
                                }
                                else
                                {
                                    ExpInitializer ei = new ExpInitializer(loc, new IdentifierExp(loc, fs.key.ident));
                                    VarDeclaration v_1 = new VarDeclaration(loc, p_3.type, p_3.ident, ei, 0L);
                                    v_1.storage_class |= 16384L | p_3.storageClass & 2097152L;
                                    fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v_1), fs._body.value}));
                                    if ((fs.key.range != null) && !p_3.type.isMutable())
                                    {
                                        v_1.range = pcopy((refPtr(new IntRange((fs.key.range.get()).imin, (fs.key.range.get()).imax.opBinary_minus(new SignExtendedNumber(1L, false))))));
                                    }
                                }
                            }
                            fs._body.value = new CompoundStatement(loc, slice(new Statement[]{ds, fs._body.value}));
                            s = new ForStatement(loc, forinit, cond, increment, fs._body.value, fs.endloc);
                            {
                                LabelStatement ls = checkLabeledLoop(this.sc, fs);
                                if ((ls) != null)
                                {
                                    ls.gotoTarget = s;
                                }
                            }
                            s = statementSemantic(s, sc2);
                            break;
                        case 2:
                            if (((fs.op & 0xFF) == 202))
                            {
                                fs.warning(new BytePtr("cannot use `foreach_reverse` with an associative array"));
                            }
                            if (checkForArgTypes.invoke(fs))
                            {
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            }
                            taa = (TypeAArray)tab;
                            if ((dim < 1) || (dim > 2))
                            {
                                fs.error(new BytePtr("only one or two arguments for associative array `foreach`"));
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            }
                            /*goto Lapply*/{ __dispatch0 = -1; continue dispatched_0; }
                        case 7:
                        case 8:
                            if (sapply.value != null)
                            {
                                /*goto Lapply*/{ __dispatch0 = -1; continue dispatched_0; }
                            }
                            {
                                AggregateDeclaration ad = ((tab.ty & 0xFF) == ENUMTY.Tclass) ? ((TypeClass)tab).sym : ((TypeStruct)tab).sym;
                                Identifier idfront = null;
                                Identifier idpopFront = null;
                                if (((fs.op & 0xFF) == 201))
                                {
                                    idfront = Id.Ffront;
                                    idpopFront = Id.FpopFront;
                                }
                                else
                                {
                                    idfront = Id.Fback;
                                    idpopFront = Id.FpopBack;
                                }
                                Dsymbol sfront = ad.search(Loc.initial, idfront, 8);
                                if (sfront == null)
                                {
                                    /*goto Lapply*/{ __dispatch0 = -1; continue dispatched_0; }
                                }
                                VarDeclaration r = null;
                                Statement _init = null;
                                if ((vinit != null) && ((fs.aggr.value.op & 0xFF) == 26) && (pequals(((VarExp)fs.aggr.value).var, vinit)))
                                {
                                    r = vinit;
                                    _init = new ExpStatement(loc, vinit);
                                }
                                else
                                {
                                    r = copyToTemp(0L, new BytePtr("__r"), fs.aggr.value);
                                    dsymbolSemantic(r, this.sc);
                                    _init = new ExpStatement(loc, r);
                                    if (vinit != null)
                                    {
                                        _init = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, vinit), _init}));
                                    }
                                }
                                Expression e = new VarExp(loc, r, true);
                                e = new DotIdExp(loc, e, Id.Fempty);
                                Expression condition = new NotExp(loc, e);
                                e = new VarExp(loc, r, true);
                                Expression increment_1 = new CallExp(loc, new DotIdExp(loc, e, idpopFront));
                                e = new VarExp(loc, r, true);
                                Expression einit = new DotIdExp(loc, e, idfront);
                                Statement makeargs = null;
                                Statement forbody = null;
                                boolean ignoreRef = false;
                                Type tfront = null;
                                {
                                    FuncDeclaration fd = sfront.isFuncDeclaration();
                                    if ((fd) != null)
                                    {
                                        if (!fd.functionSemantic())
                                        {
                                            /*goto Lrangeerr*/{ __dispatch0 = -2; continue dispatched_0; }
                                        }
                                        tfront = fd.type;
                                    }
                                    else {
                                        TemplateDeclaration td = sfront.isTemplateDeclaration();
                                        if ((td) != null)
                                        {
                                            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                                            try {
                                                {
                                                    FuncDeclaration f = resolveFuncCall(loc, this.sc, td, null, tab, ptr(a), FuncResolveFlag.quiet);
                                                    if ((f) != null)
                                                    {
                                                        tfront = f.type;
                                                    }
                                                }
                                            }
                                            finally {
                                            }
                                        }
                                        else {
                                            Declaration d = sfront.isDeclaration();
                                            if ((d) != null)
                                            {
                                                tfront = d.type;
                                            }
                                        }
                                    }
                                }
                                if ((tfront == null) || ((tfront.ty & 0xFF) == ENUMTY.Terror))
                                {
                                    /*goto Lrangeerr*/{ __dispatch0 = -2; continue dispatched_0; }
                                }
                                if (((tfront.toBasetype().ty & 0xFF) == ENUMTY.Tfunction))
                                {
                                    TypeFunction ftt = (TypeFunction)tfront.toBasetype();
                                    tfront = tfront.toBasetype().nextOf();
                                    if (!ftt.isref)
                                    {
                                        if (tfront.needsDestruction())
                                        {
                                            ignoreRef = true;
                                        }
                                    }
                                }
                                if (((tfront.ty & 0xFF) == ENUMTY.Tvoid))
                                {
                                    fs.error(new BytePtr("`%s.front` is `void` and has no value"), oaggr.toChars());
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                if ((dim == 1))
                                {
                                    Parameter p_4 = (fs.parameters.get()).get(0);
                                    VarDeclaration ve = new VarDeclaration(loc, p_4.type, p_4.ident, new ExpInitializer(loc, einit), 0L);
                                    ve.storage_class |= 16384L;
                                    ve.storage_class |= p_4.storageClass & 2687506436L;
                                    if (ignoreRef)
                                    {
                                        ve.storage_class &= -2097153L;
                                    }
                                    makeargs = new ExpStatement(loc, ve);
                                }
                                else
                                {
                                    VarDeclaration vd = copyToTemp(2097152L, new BytePtr("__front"), einit);
                                    dsymbolSemantic(vd, this.sc);
                                    makeargs = new ExpStatement(loc, vd);
                                    tfront = tfront.substWildTo((tab.mod & 0xFF));
                                    Expression ve_1 = new VarExp(loc, vd, true);
                                    ve_1.type.value = tfront;
                                    Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                                    (exps.get()).push(ve_1);
                                    int pos = 0;
                                    for (; ((exps.get()).length < dim);){
                                        pos = expandAliasThisTuples(exps, pos);
                                        if ((pos == -1))
                                        {
                                            break;
                                        }
                                    }
                                    if (((exps.get()).length != dim))
                                    {
                                        BytePtr plural = pcopy(((exps.get()).length > 1) ? new BytePtr("s") : new BytePtr(""));
                                        fs.error(new BytePtr("cannot infer argument types, expected %d argument%s, not %d"), (exps.get()).length, plural, dim);
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    {
                                        int __key1621 = 0;
                                        int __limit1622 = dim;
                                    L_outer2:
                                        for (; (__key1621 < __limit1622);__key1621 += 1) {
                                            int i_3 = __key1621;
                                            Parameter p_5 = (fs.parameters.get()).get(i_3);
                                            Expression exp = (exps.get()).get(i_3);
                                            if (p_5.type == null)
                                            {
                                                p_5.type = exp.type.value;
                                            }
                                            long sc = p_5.storageClass;
                                            if (ignoreRef)
                                            {
                                                sc &= -2097153L;
                                            }
                                            p_5.type = typeSemantic(p_5.type.addStorageClass(sc), loc, sc2);
                                            if (exp.implicitConvTo(p_5.type) == 0)
                                            {
                                                /*goto Lrangeerr*/{ __dispatch0 = -2; continue dispatched_0; }
                                            }
                                            VarDeclaration var_1 = new VarDeclaration(loc, p_5.type, p_5.ident, new ExpInitializer(loc, exp), 0L);
                                            var_1.storage_class |= 68721590272L;
                                            makeargs = new CompoundStatement(loc, slice(new Statement[]{makeargs, new ExpStatement(loc, var_1)}));
                                        }
                                    }
                                }
                                forbody = new CompoundStatement(loc, slice(new Statement[]{makeargs, fs._body.value}));
                                s = new ForStatement(loc, _init, condition, increment_1, forbody, fs.endloc);
                                {
                                    LabelStatement ls = checkLabeledLoop(this.sc, fs);
                                    if ((ls) != null)
                                    {
                                        ls.gotoTarget = s;
                                    }
                                }
                                s = statementSemantic(s, sc2);
                                break;
                            /*Lrangeerr:*/
                                fs.error(new BytePtr("cannot infer argument types"));
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            }
                        case 10:
                            if (((fs.op & 0xFF) == 202))
                            {
                                fs.deprecation(new BytePtr("cannot use `foreach_reverse` with a delegate"));
                            }
                        /*Lapply:*/
                            {
                                if (checkForArgTypes.invoke(fs))
                                {
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                TypeFunction tfld = null;
                                if (sapply.value != null)
                                {
                                    FuncDeclaration fdapply = sapply.value.isFuncDeclaration();
                                    if (fdapply != null)
                                    {
                                        assert((fdapply.type != null) && ((fdapply.type.ty & 0xFF) == ENUMTY.Tfunction));
                                        tfld = (TypeFunction)typeSemantic(fdapply.type, loc, sc2);
                                        /*goto Lget*//*unrolled goto*/
                                    /*Lget:*/
                                        if (((tfld.parameterList.parameters.get()).length == 1))
                                        {
                                            Parameter p_6 = tfld.parameterList.get(0);
                                            if ((p_6.type != null) && ((p_6.type.ty & 0xFF) == ENUMTY.Tdelegate))
                                            {
                                                Type t_1 = typeSemantic(p_6.type, loc, sc2);
                                                assert(((t_1.ty & 0xFF) == ENUMTY.Tdelegate));
                                                tfld = (TypeFunction)t_1.nextOf();
                                            }
                                        }
                                    }
                                    else if (((tab.ty & 0xFF) == ENUMTY.Tdelegate))
                                    {
                                        tfld = (TypeFunction)tab.nextOf();
                                    /*Lget:*/
                                        if (((tfld.parameterList.parameters.get()).length == 1))
                                        {
                                            Parameter p_6 = tfld.parameterList.get(0);
                                            if ((p_6.type != null) && ((p_6.type.ty & 0xFF) == ENUMTY.Tdelegate))
                                            {
                                                Type t_1 = typeSemantic(p_6.type, loc, sc2);
                                                assert(((t_1.ty & 0xFF) == ENUMTY.Tdelegate));
                                                tfld = (TypeFunction)t_1.nextOf();
                                            }
                                        }
                                    }
                                }
                                FuncExp flde = foreachBodyToFunction(sc2, fs, tfld);
                                if (flde == null)
                                {
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                {
                                    int __key1623 = 0;
                                    int __limit1624 = (fs.gotos.get()).length;
                                    for (; (__key1623 < __limit1624);__key1623 += 1) {
                                        int i_4 = __key1623;
                                        GotoStatement gs = (GotoStatement)(fs.gotos.get()).get(i_4).statement;
                                        if (gs.label.statement == null)
                                        {
                                            (fs.cases.get()).push(gs);
                                            s = new ReturnStatement(Loc.initial, new IntegerExp((long)((fs.cases.get()).length + 1)));
                                            (fs.gotos.get()).get(i_4).statement = s;
                                        }
                                    }
                                }
                                Expression e_1 = null;
                                Expression ec = null;
                                if (vinit != null)
                                {
                                    e_1 = new DeclarationExp(loc, vinit);
                                    e_1 = expressionSemantic(e_1, sc2);
                                    if (((e_1.op & 0xFF) == 127))
                                    {
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                }
                                if (taa != null)
                                {
                                    Parameter p_7 = (fs.parameters.get()).get(0);
                                    boolean isRef = (p_7.storageClass & 2097152L) != 0L;
                                    Type ta_1 = p_7.type;
                                    if ((dim == 2))
                                    {
                                        Type ti = isRef ? taa.index.addMod((byte)1) : taa.index;
                                        if (isRef ? ti.constConv(ta_1) == 0 : ti.implicitConvTo(ta_1) == 0)
                                        {
                                            fs.error(new BytePtr("`foreach`: index must be type `%s`, not `%s`"), ti.toChars(), ta_1.toChars());
                                            /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                        }
                                        p_7 = (fs.parameters.get()).get(1);
                                        isRef = (p_7.storageClass & 2097152L) != 0L;
                                        ta_1 = p_7.type;
                                    }
                                    Type taav = taa.nextOf();
                                    if (isRef ? taav.constConv(ta_1) == 0 : taav.implicitConvTo(ta_1) == 0)
                                    {
                                        fs.error(new BytePtr("`foreach`: value must be type `%s`, not `%s`"), taav.toChars(), ta_1.toChars());
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    byte i_5 = (dim == 2) ? (byte)1 : (byte)0;
                                    if (statementsem.visitfdapply.get((i_5 & 0xFF)) == null)
                                    {
                                        Ptr<DArray<Parameter>> params = refPtr(new DArray<Parameter>());
                                        (params.get()).push(new Parameter(0L, Type.tvoid.pointerTo(), null, null, null));
                                        (params.get()).push(new Parameter(2048L, Type.tsize_t, null, null, null));
                                        Ptr<DArray<Parameter>> dgparams = refPtr(new DArray<Parameter>());
                                        (dgparams.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                        if ((dim == 2))
                                        {
                                            (dgparams.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                        }
                                        statementsem.visitfldeTy.set(((i_5 & 0xFF)), new TypeDelegate(new TypeFunction(new ParameterList(dgparams, VarArg.none), Type.tint32, LINK.d, 0L)));
                                        (params.get()).push(new Parameter(0L, statementsem.visitfldeTy.get((i_5 & 0xFF)), null, null, null));
                                        statementsem.visitfdapply.set(((i_5 & 0xFF)), FuncDeclaration.genCfunc(params, Type.tint32, i_5 != 0 ? Id._aaApply2 : Id._aaApply, 0L));
                                    }
                                    Ptr<DArray<Expression>> exps_1 = refPtr(new DArray<Expression>());
                                    (exps_1.get()).push(fs.aggr.value);
                                    long keysize = taa.index.size();
                                    if ((keysize == -1L))
                                    {
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    assert((keysize < -1L - (long)target.ptrsize));
                                    keysize = keysize + (long)(target.ptrsize - 1) & (long)~(target.ptrsize - 1);
                                    Expression fexp = flde;
                                    if (!statementsem.visitfldeTy.get((i_5 & 0xFF)).equals(flde.type.value))
                                    {
                                        fexp = new CastExp(loc, flde, flde.type.value);
                                        fexp.type.value = statementsem.visitfldeTy.get((i_5 & 0xFF));
                                    }
                                    (exps_1.get()).push(new IntegerExp(Loc.initial, keysize, Type.tsize_t));
                                    (exps_1.get()).push(fexp);
                                    ec = new VarExp(Loc.initial, statementsem.visitfdapply.get((i_5 & 0xFF)), false);
                                    ec = new CallExp(loc, ec, exps_1);
                                    ec.type.value = Type.tint32;
                                }
                                else if (((tab.ty & 0xFF) == ENUMTY.Tarray) || ((tab.ty & 0xFF) == ENUMTY.Tsarray))
                                {
                                    int BUFFER_LEN = 23;
                                    ByteSlice fdname = (byte)255;
                                    int flag = 0;
                                    switch ((tn.ty & 0xFF))
                                    {
                                        case 31:
                                            flag = 0;
                                            break;
                                        case 32:
                                            flag = 3;
                                            break;
                                        case 33:
                                            flag = 6;
                                            break;
                                        default:
                                        throw new AssertionError("Unreachable code!");
                                    }
                                    switch ((tnv.ty & 0xFF))
                                    {
                                        case 31:
                                            flag += 0;
                                            break;
                                        case 32:
                                            flag += 1;
                                            break;
                                        case 33:
                                            flag += 2;
                                            break;
                                        default:
                                        throw new AssertionError("Unreachable code!");
                                    }
                                    BytePtr r_1 = pcopy(((fs.op & 0xFF) == 202) ? new BytePtr("R") : new BytePtr(""));
                                    int j = sprintf(fdname.ptr(), new BytePtr("_aApply%s%.*s%llu"), r_1, 2, statementsem.visitfntab.get(flag), (long)dim);
                                    assert((j < 23));
                                    FuncDeclaration fdapply_1 = null;
                                    TypeDelegate dgty = null;
                                    Ptr<DArray<Parameter>> params_1 = refPtr(new DArray<Parameter>());
                                    (params_1.get()).push(new Parameter(2048L, tn.arrayOf(), null, null, null));
                                    Ptr<DArray<Parameter>> dgparams_1 = refPtr(new DArray<Parameter>());
                                    (dgparams_1.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                    if ((dim == 2))
                                    {
                                        (dgparams_1.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                    }
                                    dgty = new TypeDelegate(new TypeFunction(new ParameterList(dgparams_1, VarArg.none), Type.tint32, LINK.d, 0L));
                                    (params_1.get()).push(new Parameter(0L, dgty, null, null, null));
                                    fdapply_1 = FuncDeclaration.genCfunc(params_1, Type.tint32, fdname.ptr(), 0L);
                                    if (((tab.ty & 0xFF) == ENUMTY.Tsarray))
                                    {
                                        fs.aggr.value = fs.aggr.value.castTo(sc2, tn.arrayOf());
                                    }
                                    Expression fexp_1 = flde;
                                    if (!dgty.equals(flde.type.value))
                                    {
                                        fexp_1 = new CastExp(loc, flde, flde.type.value);
                                        fexp_1.type.value = dgty;
                                    }
                                    ec = new VarExp(Loc.initial, fdapply_1, false);
                                    ec = new CallExp(loc, ec, fs.aggr.value, fexp_1);
                                    ec.type.value = Type.tint32;
                                }
                                else if (((tab.ty & 0xFF) == ENUMTY.Tdelegate))
                                {
                                    if (((fs.aggr.value.op & 0xFF) == 160) && ((DelegateExp)fs.aggr.value).func.isNested() && !((DelegateExp)fs.aggr.value).func.needThis())
                                    {
                                        fs.aggr.value = ((DelegateExp)fs.aggr.value).e1.value;
                                    }
                                    ec = new CallExp(loc, fs.aggr.value, flde);
                                    ec = expressionSemantic(ec, sc2);
                                    if (((ec.op & 0xFF) == 127))
                                    {
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    if ((!pequals(ec.type.value, Type.tint32)))
                                    {
                                        fs.error(new BytePtr("`opApply()` function for `%s` must return an `int`"), tab.toChars());
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                }
                                else
                                {
                                    if (global.params.vsafe)
                                    {
                                        flde.fd.tookAddressOf += 1;
                                    }
                                    assert(((tab.ty & 0xFF) == ENUMTY.Tstruct) || ((tab.ty & 0xFF) == ENUMTY.Tclass));
                                    assert(sapply.value != null);
                                    ec = new DotIdExp(loc, fs.aggr.value, sapply.value.ident);
                                    ec = new CallExp(loc, ec, flde);
                                    ec = expressionSemantic(ec, sc2);
                                    if (((ec.op & 0xFF) == 127))
                                    {
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    if ((!pequals(ec.type.value, Type.tint32)))
                                    {
                                        fs.error(new BytePtr("`opApply()` function for `%s` must return an `int`"), tab.toChars());
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                }
                                e_1 = Expression.combine(e_1, ec);
                                if ((fs.cases.get()).length == 0)
                                {
                                    e_1 = new CastExp(loc, e_1, Type.tvoid);
                                    s = new ExpStatement(loc, e_1);
                                }
                                else
                                {
                                    Ptr<DArray<Statement>> a_1 = refPtr(new DArray<Statement>());
                                    s = new BreakStatement(Loc.initial, null);
                                    s = new DefaultStatement(Loc.initial, s);
                                    (a_1.get()).push(s);
                                    {
                                        Slice<Statement> __r1626 = (fs.cases.get()).opSlice().copy();
                                        int __key1625 = 0;
                                        for (; (__key1625 < __r1626.getLength());__key1625 += 1) {
                                            Statement c = __r1626.get(__key1625);
                                            int i_6 = __key1625;
                                            s = new CaseStatement(Loc.initial, new IntegerExp((long)(i_6 + 2)), c);
                                            (a_1.get()).push(s);
                                        }
                                    }
                                    s = new CompoundStatement(loc, a_1);
                                    s = new SwitchStatement(loc, e_1, s, false);
                                }
                                s = statementSemantic(s, sc2);
                                break;
                            }
                            throw new AssertionError("Unreachable code!");
                        case 34:
                            __dispatch0 = 0;
                            s = new ErrorStatement();
                            break;
                        default:
                        fs.error(new BytePtr("`foreach`: `%s` is not an aggregate type"), fs.aggr.value.type.value.toChars());
                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                    }
                } while(__dispatch0 != 0);
            }
            (sc2.get()).pop();
            this.result = s;
        }

        public static FuncExp foreachBodyToFunction(Ptr<Scope> sc, ForeachStatement fs, TypeFunction tfld) {
            Ptr<DArray<Parameter>> params = refPtr(new DArray<Parameter>());
            {
                int __key1627 = 0;
                int __limit1628 = (fs.parameters.get()).length;
            L_outer3:
                for (; (__key1627 < __limit1628);__key1627 += 1) {
                    int i = __key1627;
                    Parameter p = (fs.parameters.get()).get(i);
                    long stc = 2097152L;
                    Identifier id = null;
                    p.type = typeSemantic(p.type, fs.loc, sc);
                    p.type = p.type.addStorageClass(p.storageClass);
                    if (tfld != null)
                    {
                        Parameter prm = tfld.parameterList.get(i);
                        stc = prm.storageClass & 2097152L;
                        id = p.ident;
                        if (((p.storageClass & 2097152L) != stc))
                        {
                            if (stc == 0)
                            {
                                fs.error(new BytePtr("`foreach`: cannot make `%s` `ref`"), p.ident.toChars());
                                return null;
                            }
                            /*goto LcopyArg*//*unrolled goto*/
                            id = p.ident;
                        }
                    }
                    else if ((p.storageClass & 2097152L) != 0)
                    {
                        id = p.ident;
                    }
                    else
                    {
                    /*LcopyArg:*/
                        id = Identifier.generateId(new BytePtr("__applyArg"), i);
                        Initializer ie = new ExpInitializer(fs.loc, new IdentifierExp(fs.loc, id));
                        VarDeclaration v = new VarDeclaration(fs.loc, p.type, p.ident, ie, 0L);
                        v.storage_class |= 1099511627776L;
                        Statement s = new ExpStatement(fs.loc, v);
                        fs._body.value = new CompoundStatement(fs.loc, slice(new Statement[]{s, fs._body.value}));
                    }
                    (params.get()).push(new Parameter(stc, p.type, id, null, null));
                }
            }
            long stc = mergeFuncAttrs(4406703554560L, fs.func);
            TypeFunction tf = new TypeFunction(new ParameterList(params, VarArg.none), Type.tint32, LINK.d, stc);
            fs.cases = pcopy((refPtr(new DArray<Statement>())));
            fs.gotos = pcopy((refPtr(new DArray<ScopeStatement>())));
            FuncLiteralDeclaration fld = new FuncLiteralDeclaration(fs.loc, fs.endloc, tf, TOK.delegate_, fs, null);
            fld.fbody = fs._body.value;
            Expression flde = new FuncExp(fs.loc, fld);
            flde = expressionSemantic(flde, sc);
            fld.tookAddressOf = 0;
            if (((flde.op & 0xFF) == 127))
            {
                return null;
            }
            return (FuncExp)flde;
        }

        public  void visit(ForeachRangeStatement fs) {
            Loc loc = fs.loc.copy();
            fs.lwr = expressionSemantic(fs.lwr, this.sc);
            fs.lwr = resolveProperties(this.sc, fs.lwr);
            fs.lwr = fs.lwr.optimize(0, false);
            if (fs.lwr.type.value == null)
            {
                fs.error(new BytePtr("invalid range lower bound `%s`"), fs.lwr.toChars());
                this.setError();
                return ;
            }
            fs.upr = expressionSemantic(fs.upr, this.sc);
            fs.upr = resolveProperties(this.sc, fs.upr);
            fs.upr = fs.upr.optimize(0, false);
            if (fs.upr.type.value == null)
            {
                fs.error(new BytePtr("invalid range upper bound `%s`"), fs.upr.toChars());
                this.setError();
                return ;
            }
            if (fs.prm.type != null)
            {
                fs.prm.type = typeSemantic(fs.prm.type, loc, this.sc);
                fs.prm.type = fs.prm.type.addStorageClass(fs.prm.storageClass);
                fs.lwr = fs.lwr.implicitCastTo(this.sc, fs.prm.type);
                if ((fs.upr.implicitConvTo(fs.prm.type) != 0) || ((fs.prm.storageClass & 2097152L) != 0))
                {
                    fs.upr = fs.upr.implicitCastTo(this.sc, fs.prm.type);
                }
                else
                {
                    Expression limit = new MinExp(loc, fs.upr, literal_356A192B7913B04C());
                    limit = expressionSemantic(limit, this.sc);
                    limit = limit.optimize(0, false);
                    if (limit.implicitConvTo(fs.prm.type) == 0)
                    {
                        fs.upr = fs.upr.implicitCastTo(this.sc, fs.prm.type);
                    }
                }
            }
            else
            {
                Type tlwr = fs.lwr.type.value.toBasetype();
                if (((tlwr.ty & 0xFF) == ENUMTY.Tstruct) || ((tlwr.ty & 0xFF) == ENUMTY.Tclass))
                {
                    fs.prm.type = fs.lwr.type.value;
                }
                else if ((pequals(fs.lwr.type.value, fs.upr.type.value)))
                {
                    fs.prm.type = fs.lwr.type.value;
                }
                else
                {
                    AddExp ea = new AddExp(loc, fs.lwr, fs.upr);
                    if (typeCombine(ea, this.sc) != null)
                    {
                        this.setError();
                        return ;
                    }
                    fs.prm.type = ea.type.value;
                    fs.lwr = ea.e1.value;
                    fs.upr = ea.e2.value;
                }
                fs.prm.type = fs.prm.type.addStorageClass(fs.prm.storageClass);
            }
            if (((fs.prm.type.ty & 0xFF) == ENUMTY.Terror) || ((fs.lwr.op & 0xFF) == 127) || ((fs.upr.op & 0xFF) == 127))
            {
                this.setError();
                return ;
            }
            ExpInitializer ie = new ExpInitializer(loc, ((fs.op & 0xFF) == 201) ? fs.lwr : fs.upr);
            fs.key = new VarDeclaration(loc, fs.upr.type.value.mutableOf(), Identifier.generateId(new BytePtr("__key")), ie, 0L);
            fs.key.storage_class |= 1099511627776L;
            SignExtendedNumber lower = getIntRange(fs.lwr).imin.copy();
            SignExtendedNumber upper = getIntRange(fs.upr).imax.copy();
            if ((lower.opCmp(upper) <= 0))
            {
                fs.key.range = pcopy((refPtr(new IntRange(lower, upper))));
            }
            Identifier id = Identifier.generateId(new BytePtr("__limit"));
            ie = new ExpInitializer(loc, ((fs.op & 0xFF) == 201) ? fs.upr : fs.lwr);
            VarDeclaration tmp = new VarDeclaration(loc, fs.upr.type.value, id, ie, 0L);
            tmp.storage_class |= 1099511627776L;
            Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
            if (((fs.op & 0xFF) == 201))
            {
                (cs.get()).push(new ExpStatement(loc, fs.key));
                (cs.get()).push(new ExpStatement(loc, tmp));
            }
            else
            {
                (cs.get()).push(new ExpStatement(loc, tmp));
                (cs.get()).push(new ExpStatement(loc, fs.key));
            }
            Statement forinit = new CompoundDeclarationStatement(loc, cs);
            Expression cond = null;
            if (((fs.op & 0xFF) == 202))
            {
                cond = new PostExp(TOK.minusMinus, loc, new VarExp(loc, fs.key, true));
                if (fs.prm.type.isscalar())
                {
                    cond = new CmpExp(TOK.greaterThan, loc, cond, new VarExp(loc, tmp, true));
                }
                else
                {
                    cond = new EqualExp(TOK.notEqual, loc, cond, new VarExp(loc, tmp, true));
                }
            }
            else
            {
                if (fs.prm.type.isscalar())
                {
                    cond = new CmpExp(TOK.lessThan, loc, new VarExp(loc, fs.key, true), new VarExp(loc, tmp, true));
                }
                else
                {
                    cond = new EqualExp(TOK.notEqual, loc, new VarExp(loc, fs.key, true), new VarExp(loc, tmp, true));
                }
            }
            Expression increment = null;
            if (((fs.op & 0xFF) == 201))
            {
                increment = new PreExp(TOK.prePlusPlus, loc, new VarExp(loc, fs.key, true));
            }
            if (((fs.prm.storageClass & 2097152L) != 0) && fs.prm.type.equals(fs.key.type))
            {
                fs.key.range = null;
                AliasDeclaration v = new AliasDeclaration(loc, fs.prm.ident, fs.key);
                fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v), fs._body.value}));
            }
            else
            {
                ie = new ExpInitializer(loc, new CastExp(loc, new VarExp(loc, fs.key, true), fs.prm.type));
                VarDeclaration v = new VarDeclaration(loc, fs.prm.type, fs.prm.ident, ie, 0L);
                v.storage_class |= 1099511644160L | fs.prm.storageClass & 2097152L;
                fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v), fs._body.value}));
                if ((fs.key.range != null) && !fs.prm.type.isMutable())
                {
                    v.range = pcopy((refPtr(new IntRange((fs.key.range.get()).imin, (fs.key.range.get()).imax.opBinary_minus(new SignExtendedNumber(1L, false))))));
                }
            }
            if ((fs.prm.storageClass & 2097152L) != 0)
            {
                if ((fs.key.type.constConv(fs.prm.type) <= MATCH.nomatch))
                {
                    fs.error(new BytePtr("argument type mismatch, `%s` to `ref %s`"), fs.key.type.toChars(), fs.prm.type.toChars());
                    this.setError();
                    return ;
                }
            }
            ForStatement s = new ForStatement(loc, forinit, cond, increment, fs._body.value, fs.endloc);
            {
                LabelStatement ls = checkLabeledLoop(this.sc, fs);
                if ((ls) != null)
                {
                    ls.gotoTarget = s;
                }
            }
            this.result = statementSemantic(s, this.sc);
        }

        public  void visit(IfStatement ifs) {
            ifs.condition = checkAssignmentAsCondition(ifs.condition);
            ScopeDsymbol sym = new ScopeDsymbol();
            sym.parent.value = (this.sc.get()).scopesym;
            sym.endlinnum = ifs.endloc.linnum;
            Ptr<Scope> scd = (this.sc.get()).push(sym);
            if (ifs.prm != null)
            {
                ExpInitializer ei = new ExpInitializer(ifs.loc, ifs.condition);
                ifs.match = new VarDeclaration(ifs.loc, ifs.prm.type, ifs.prm.ident, ei, 0L);
                ifs.match.parent.value = (scd.get()).func;
                ifs.match.storage_class |= ifs.prm.storageClass;
                dsymbolSemantic(ifs.match, scd);
                DeclarationExp de = new DeclarationExp(ifs.loc, ifs.match);
                VarExp ve = new VarExp(ifs.loc, ifs.match, true);
                ifs.condition = new CommaExp(ifs.loc, de, ve, true);
                ifs.condition = expressionSemantic(ifs.condition, scd);
                if (ifs.match.edtor != null)
                {
                    Statement sdtor = new DtorExpStatement(ifs.loc, ifs.match.edtor, ifs.match);
                    sdtor = new ScopeGuardStatement(ifs.loc, TOK.onScopeExit, sdtor);
                    ifs.ifbody = new CompoundStatement(ifs.loc, slice(new Statement[]{sdtor, ifs.ifbody}));
                    ifs.match.storage_class |= 16777216L;
                    Statement sdtor2 = new DtorExpStatement(ifs.loc, ifs.match.edtor, ifs.match);
                    if (ifs.elsebody != null)
                    {
                        ifs.elsebody = new CompoundStatement(ifs.loc, slice(new Statement[]{sdtor2, ifs.elsebody}));
                    }
                    else
                    {
                        ifs.elsebody = sdtor2;
                    }
                }
            }
            else
            {
                if (((ifs.condition.op & 0xFF) == 28))
                {
                    ((DotIdExp)ifs.condition).noderef = true;
                }
                ifs.condition = expressionSemantic(ifs.condition, scd);
                ifs.condition = resolveProperties(scd, ifs.condition);
                ifs.condition = ifs.condition.addDtorHook(scd);
            }
            if (checkNonAssignmentArrayOp(ifs.condition, false))
            {
                ifs.condition = new ErrorExp();
            }
            ifs.condition = checkGC(scd, ifs.condition);
            ifs.condition = ifs.condition.toBoolean(scd);
            ifs.condition = ifs.condition.optimize(0, false);
            CtorFlow ctorflow_root = (scd.get()).ctorflow.clone().copy();
            ifs.ifbody = semanticNoScope(ifs.ifbody, scd);
            (scd.get()).pop();
            CtorFlow ctorflow_then = (this.sc.get()).ctorflow.copy();
            (this.sc.get()).ctorflow.opAssign(ctorflow_root.copy());
            if (ifs.elsebody != null)
            {
                ifs.elsebody = semanticScope(ifs.elsebody, this.sc, null, null);
            }
            (this.sc.get()).merge(ifs.loc, ctorflow_then);
            ctorflow_then.freeFieldinit();
            if (((ifs.condition.op & 0xFF) == 127) || (ifs.ifbody != null) && (ifs.ifbody.isErrorStatement() != null) || (ifs.elsebody != null) && (ifs.elsebody.isErrorStatement() != null))
            {
                this.setError();
                return ;
            }
            this.result = ifs;
        }

        public  void visit(ConditionalStatement cs) {
            if (cs.condition.include(this.sc) != 0)
            {
                DebugCondition dc = cs.condition.isDebugCondition();
                if (dc != null)
                {
                    this.sc = pcopy((this.sc.get()).push());
                    (this.sc.get()).flags |= 8;
                    cs.ifbody = statementSemantic(cs.ifbody, this.sc);
                    (this.sc.get()).pop();
                }
                else
                {
                    cs.ifbody = statementSemantic(cs.ifbody, this.sc);
                }
                this.result = cs.ifbody;
            }
            else
            {
                if (cs.elsebody != null)
                {
                    cs.elsebody = statementSemantic(cs.elsebody, this.sc);
                }
                this.result = cs.elsebody;
            }
        }

        public  void visit(PragmaStatement ps) {
            if ((pequals(ps.ident, Id.msg)))
            {
                if (ps.args != null)
                {
                    {
                        Slice<Expression> __r1629 = (ps.args.get()).opSlice().copy();
                        int __key1630 = 0;
                        for (; (__key1630 < __r1629.getLength());__key1630 += 1) {
                            Expression arg = __r1629.get(__key1630);
                            this.sc = pcopy((this.sc.get()).startCTFE());
                            Expression e = expressionSemantic(arg, this.sc);
                            e = resolveProperties(this.sc, e);
                            this.sc = pcopy((this.sc.get()).endCTFE());
                            e = ctfeInterpretForPragmaMsg(e);
                            if (((e.op & 0xFF) == 127))
                            {
                                errorSupplemental(ps.loc, new BytePtr("while evaluating `pragma(msg, %s)`"), arg.toChars());
                                this.setError();
                                return ;
                            }
                            StringExp se = e.toStringExp();
                            if (se != null)
                            {
                                se = se.toUTF8(this.sc);
                                fprintf(stderr, new BytePtr("%.*s"), se.len, se.string);
                            }
                            else
                            {
                                fprintf(stderr, new BytePtr("%s"), e.toChars());
                            }
                        }
                    }
                    fprintf(stderr, new BytePtr("\n"));
                }
            }
            else if ((pequals(ps.ident, Id.lib)))
            {
                ps.error(new BytePtr("`pragma(lib)` not allowed as statement"));
                this.setError();
                return ;
            }
            else if ((pequals(ps.ident, Id.linkerDirective)))
            {
                ps.error(new BytePtr("`pragma(linkerDirective)` not allowed as statement"));
                this.setError();
                return ;
            }
            else if ((pequals(ps.ident, Id.startaddress)))
            {
                if ((ps.args == null) || ((ps.args.get()).length != 1))
                {
                    ps.error(new BytePtr("function name expected for start address"));
                }
                else
                {
                    Expression e = (ps.args.get()).get(0);
                    this.sc = pcopy((this.sc.get()).startCTFE());
                    e = expressionSemantic(e, this.sc);
                    e = resolveProperties(this.sc, e);
                    this.sc = pcopy((this.sc.get()).endCTFE());
                    e = e.ctfeInterpret();
                    ps.args.get().set(0, e);
                    Dsymbol sa = getDsymbol(e);
                    if ((sa == null) || (sa.isFuncDeclaration() == null))
                    {
                        ps.error(new BytePtr("function name expected for start address, not `%s`"), e.toChars());
                        this.setError();
                        return ;
                    }
                    if (ps._body != null)
                    {
                        ps._body = statementSemantic(ps._body, this.sc);
                        if (ps._body.isErrorStatement() != null)
                        {
                            this.result = ps._body;
                            return ;
                        }
                    }
                    this.result = ps;
                    return ;
                }
            }
            else if ((pequals(ps.ident, Id.Pinline)))
            {
                int inlining = PINLINE.default_;
                if ((ps.args == null) || ((ps.args.get()).length == 0))
                {
                    inlining = PINLINE.default_;
                }
                else if ((ps.args == null) || ((ps.args.get()).length != 1))
                {
                    ps.error(new BytePtr("boolean expression expected for `pragma(inline)`"));
                    this.setError();
                    return ;
                }
                else
                {
                    Expression e = (ps.args.get()).get(0);
                    if (((e.op & 0xFF) != 135) || !e.type.value.equals(Type.tbool))
                    {
                        ps.error(new BytePtr("pragma(inline, true or false) expected, not `%s`"), e.toChars());
                        this.setError();
                        return ;
                    }
                    if (e.isBool(true))
                    {
                        inlining = PINLINE.always;
                    }
                    else if (e.isBool(false))
                    {
                        inlining = PINLINE.never;
                    }
                    FuncDeclaration fd = (this.sc.get()).func;
                    if (fd == null)
                    {
                        ps.error(new BytePtr("`pragma(inline)` is not inside a function"));
                        this.setError();
                        return ;
                    }
                    fd.inlining = inlining;
                }
            }
            else if (!global.params.ignoreUnsupportedPragmas)
            {
                ps.error(new BytePtr("unrecognized `pragma(%s)`"), ps.ident.toChars());
                this.setError();
                return ;
            }
            if (ps._body != null)
            {
                if ((pequals(ps.ident, Id.msg)) || (pequals(ps.ident, Id.startaddress)))
                {
                    ps.error(new BytePtr("`pragma(%s)` is missing a terminating `;`"), ps.ident.toChars());
                    this.setError();
                    return ;
                }
                ps._body = statementSemantic(ps._body, this.sc);
            }
            this.result = ps._body;
        }

        public  void visit(StaticAssertStatement s) {
            semantic2(s.sa, this.sc);
        }

        public  void visit(SwitchStatement ss) {
            ss.tf = (this.sc.get()).tf;
            if (ss.cases != null)
            {
                this.result = ss;
                return ;
            }
            boolean conditionError = false;
            ss.condition = expressionSemantic(ss.condition, this.sc);
            ss.condition = resolveProperties(this.sc, ss.condition);
            Type att = null;
            TypeEnum te = null;
            for (; ((ss.condition.op & 0xFF) != 127);){
                if (((ss.condition.type.value.ty & 0xFF) == ENUMTY.Tenum))
                {
                    te = (TypeEnum)ss.condition.type.value;
                }
                if (ss.condition.type.value.isString())
                {
                    if (((ss.condition.type.value.ty & 0xFF) != ENUMTY.Tarray))
                    {
                        ss.condition = ss.condition.implicitCastTo(this.sc, ss.condition.type.value.nextOf().arrayOf());
                    }
                    ss.condition.type.value = ss.condition.type.value.constOf();
                    break;
                }
                ss.condition = integralPromotions(ss.condition, this.sc);
                if (((ss.condition.op & 0xFF) != 127) && ss.condition.type.value.isintegral())
                {
                    break;
                }
                AggregateDeclaration ad = isAggregate(ss.condition.type.value);
                if ((ad != null) && (ad.aliasthis != null) && (!pequals(ss.condition.type.value, att)))
                {
                    if ((att == null) && ss.condition.type.value.checkAliasThisRec())
                    {
                        att = ss.condition.type.value;
                    }
                    {
                        Expression e = resolveAliasThis(this.sc, ss.condition, true);
                        if ((e) != null)
                        {
                            ss.condition = e;
                            continue;
                        }
                    }
                }
                if (((ss.condition.op & 0xFF) != 127))
                {
                    ss.error(new BytePtr("`%s` must be of integral or string type, it is a `%s`"), ss.condition.toChars(), ss.condition.type.value.toChars());
                    conditionError = true;
                    break;
                }
            }
            if (checkNonAssignmentArrayOp(ss.condition, false))
            {
                ss.condition = new ErrorExp();
            }
            ss.condition = ss.condition.optimize(0, false);
            ss.condition = checkGC(this.sc, ss.condition);
            if (((ss.condition.op & 0xFF) == 127))
            {
                conditionError = true;
            }
            boolean needswitcherror = false;
            ss.lastVar = (this.sc.get()).lastVar;
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).sbreak = ss;
            (this.sc.get()).sw = ss;
            ss.cases = pcopy((refPtr(new DArray<CaseStatement>())));
            boolean inLoopSave = (this.sc.get()).inLoop;
            (this.sc.get()).inLoop = true;
            ss._body = statementSemantic(ss._body, this.sc);
            (this.sc.get()).inLoop = inLoopSave;
            if (conditionError || (ss._body != null) && (ss._body.isErrorStatement() != null))
            {
                (this.sc.get()).pop();
                this.setError();
                return ;
            }
        /*Lgotocase:*/
            {
                Slice<GotoCaseStatement> __r1631 = ss.gotoCases.opSlice().copy();
                int __key1632 = 0;
                for (; (__key1632 < __r1631.getLength());__key1632 += 1) {
                    GotoCaseStatement gcs = __r1631.get(__key1632);
                    if (gcs.exp == null)
                    {
                        gcs.error(new BytePtr("no `case` statement following `goto case;`"));
                        (this.sc.get()).pop();
                        this.setError();
                        return ;
                    }
                    {
                        Ptr<Scope> scx = this.sc;
                        for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                            if ((scx.get()).sw == null)
                            {
                                continue;
                            }
                            {
                                Slice<CaseStatement> __r1633 = ((scx.get()).sw.cases.get()).opSlice().copy();
                                int __key1634 = 0;
                                for (; (__key1634 < __r1633.getLength());__key1634 += 1) {
                                    CaseStatement cs = __r1633.get(__key1634);
                                    if (cs.exp.equals(gcs.exp))
                                    {
                                        gcs.cs = cs;
                                        continue Lgotocase;
                                    }
                                }
                            }
                        }
                    }
                    gcs.error(new BytePtr("`case %s` not found"), gcs.exp.toChars());
                    (this.sc.get()).pop();
                    this.setError();
                    return ;
                }
            }
            if (ss.isFinal)
            {
                Type t = ss.condition.type.value;
                Dsymbol ds = null;
                EnumDeclaration ed = null;
                if ((t != null) && ((ds = t.toDsymbol(this.sc)) != null))
                {
                    ed = ds.isEnumDeclaration();
                }
                if ((ed == null) && (te != null) && ((ds = te.toDsymbol(this.sc)) != null))
                {
                    ed = ds.isEnumDeclaration();
                }
                if (ed != null)
                {
                /*Lmembers:*/
                    {
                        Slice<Dsymbol> __r1635 = (ed.members.get()).opSlice().copy();
                        int __key1636 = 0;
                        for (; (__key1636 < __r1635.getLength());__key1636 += 1) {
                            Dsymbol es = __r1635.get(__key1636);
                            EnumMember em = es.isEnumMember();
                            if (em != null)
                            {
                                {
                                    Slice<CaseStatement> __r1637 = (ss.cases.get()).opSlice().copy();
                                    int __key1638 = 0;
                                    for (; (__key1638 < __r1637.getLength());__key1638 += 1) {
                                        CaseStatement cs = __r1637.get(__key1638);
                                        if (cs.exp.equals(em.value()) || !cs.exp.type.value.isString() && !em.value().type.value.isString() && (cs.exp.toInteger() == em.value().toInteger()))
                                        {
                                            continue Lmembers;
                                        }
                                    }
                                }
                                ss.error(new BytePtr("`enum` member `%s` not represented in `final switch`"), em.toChars());
                                (this.sc.get()).pop();
                                this.setError();
                                return ;
                            }
                        }
                    }
                }
                else
                {
                    needswitcherror = true;
                }
            }
            if (((this.sc.get()).sw.sdefault == null) && !ss.isFinal || needswitcherror || ((global.params.useAssert & 0xFF) == 2))
            {
                ss.hasNoDefault = 1;
                if (!ss.isFinal && (ss._body == null) || (ss._body.isErrorStatement() == null))
                {
                    ss.error(new BytePtr("`switch` statement without a `default`; use `final switch` or add `default: assert(0);` or add `default: break;`"));
                }
                Ptr<DArray<Statement>> a = refPtr(new DArray<Statement>());
                CompoundStatement cs = null;
                Statement s = null;
                if (((global.params.useSwitchError & 0xFF) == 2) && ((global.params.checkAction & 0xFF) != 2))
                {
                    if (((global.params.checkAction & 0xFF) == 1))
                    {
                        s = new ExpStatement(ss.loc, new AssertExp(ss.loc, new IntegerExp(ss.loc, 0L, Type.tint32), null));
                    }
                    else
                    {
                        if (!verifyHookExist(ss.loc, this.sc.get(), Id.__switch_error, new ByteSlice("generating assert messages"), Id.object))
                        {
                            this.setError();
                            return ;
                        }
                        Expression sl = new IdentifierExp(ss.loc, Id.empty);
                        sl = new DotIdExp(ss.loc, sl, Id.object);
                        sl = new DotIdExp(ss.loc, sl, Id.__switch_error);
                        Ptr<DArray<Expression>> args = refPtr(new DArray<Expression>(2));
                        args.get().set(0, new StringExp(ss.loc, ss.loc.filename));
                        args.get().set(1, new IntegerExp((long)ss.loc.linnum));
                        sl = new CallExp(ss.loc, sl, args);
                        expressionSemantic(sl, this.sc);
                        s = new SwitchErrorStatement(ss.loc, sl);
                    }
                }
                else
                {
                    s = new ExpStatement(ss.loc, new HaltExp(ss.loc));
                }
                (a.get()).reserve(2);
                (this.sc.get()).sw.sdefault = new DefaultStatement(ss.loc, s);
                (a.get()).push(ss._body);
                if ((blockExit(ss._body, (this.sc.get()).func, false) & BE.fallthru) != 0)
                {
                    (a.get()).push(new BreakStatement(Loc.initial, null));
                }
                (a.get()).push((this.sc.get()).sw.sdefault);
                cs = new CompoundStatement(ss.loc, a);
                ss._body = cs;
            }
            if (ss.checkLabel())
            {
                (this.sc.get()).pop();
                this.setError();
                return ;
            }
            if (ss.condition.type.value.isString())
            {
                if (!verifyHookExist(ss.loc, this.sc.get(), Id.__switch, new ByteSlice("switch cases on strings"), Id.object))
                {
                    this.setError();
                    return ;
                }
                int numcases = 0;
                if (ss.cases != null)
                {
                    numcases = (ss.cases.get()).length;
                }
                {
                    int i = 0;
                    for (; (i < numcases);i++){
                        CaseStatement cs = (ss.cases.get()).get(i);
                        cs.index = i;
                    }
                }
                Ptr<DArray<CaseStatement>> csCopy = (ss.cases.get()).copy();
                if (numcases != 0)
                {
                    Function2<Object,Object,Integer> sort_compare = new Function2<Object,Object,Integer>() {
                        public Integer invoke(Object x, Object y) {
                         {
                            CaseStatement ox = ((Ptr<CaseStatement>)x).get();
                            CaseStatement oy = ((Ptr<CaseStatement>)y).get();
                            StringExp se1 = ox.exp.isStringExp();
                            StringExp se2 = oy.exp.isStringExp();
                            return (se1 != null) && (se2 != null) ? se1.comparex(se2) : 0;
                        }}

                    };
                    qsort((csCopy.get()).data, numcases, 4, sort_compare);
                }
                Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>());
                (arguments.get()).push(ss.condition);
                Ptr<DArray<RootObject>> compileTimeArgs = refPtr(new DArray<RootObject>());
                (compileTimeArgs.get()).push(new TypeExp(ss.loc, ss.condition.type.value.nextOf()));
                {
                    Slice<CaseStatement> __r1639 = (csCopy.get()).opSlice().copy();
                    int __key1640 = 0;
                    for (; (__key1640 < __r1639.getLength());__key1640 += 1) {
                        CaseStatement caseString = __r1639.get(__key1640);
                        (compileTimeArgs.get()).push(caseString.exp);
                    }
                }
                Expression sl = new IdentifierExp(ss.loc, Id.empty);
                sl = new DotIdExp(ss.loc, sl, Id.object);
                sl = new DotTemplateInstanceExp(ss.loc, sl, Id.__switch, compileTimeArgs);
                sl = new CallExp(ss.loc, sl, arguments);
                expressionSemantic(sl, this.sc);
                ss.condition = sl;
                int i = 0;
                {
                    Slice<CaseStatement> __r1641 = (csCopy.get()).opSlice().copy();
                    int __key1642 = 0;
                    for (; (__key1642 < __r1641.getLength());__key1642 += 1) {
                        CaseStatement c = __r1641.get(__key1642);
                        (ss.cases.get()).get(c.index).exp = new IntegerExp((long)i++);
                    }
                }
                statementSemantic(ss, this.sc);
            }
            (this.sc.get()).pop();
            this.result = ss;
        }

        public  void visit(CaseStatement cs) {
            SwitchStatement sw = (this.sc.get()).sw;
            boolean errors = false;
            this.sc = pcopy((this.sc.get()).startCTFE());
            cs.exp = expressionSemantic(cs.exp, this.sc);
            cs.exp = resolveProperties(this.sc, cs.exp);
            this.sc = pcopy((this.sc.get()).endCTFE());
            if (sw != null)
            {
                cs.exp = cs.exp.implicitCastTo(this.sc, sw.condition.type.value);
                cs.exp = cs.exp.optimize(1, false);
                Expression e = cs.exp;
                for (; ((e.op & 0xFF) == 12);) {
                    e = ((CastExp)e).e1.value;
                }
                try {
                    if (((e.op & 0xFF) == 26))
                    {
                        VarExp ve = (VarExp)e;
                        VarDeclaration v = ve.var.isVarDeclaration();
                        Type t = cs.exp.type.value.toBasetype();
                        if ((v != null) && t.isintegral() || ((t.ty & 0xFF) == ENUMTY.Tclass))
                        {
                            sw.hasVars = 1;
                            if (!v.isConst() && !v.isImmutable())
                            {
                                cs.deprecation(new BytePtr("`case` variables have to be `const` or `immutable`"));
                            }
                            if (sw.isFinal)
                            {
                                cs.error(new BytePtr("`case` variables not allowed in `final switch` statements"));
                                errors = true;
                            }
                            {
                                Ptr<Scope> scx = this.sc;
                                for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                                    if (((scx.get()).enclosing != null) && (pequals(((scx.get()).enclosing.get()).sw, sw)))
                                    {
                                        continue;
                                    }
                                    assert((pequals((scx.get()).sw, sw)));
                                    if ((scx.get()).search(cs.exp.loc, v.ident, null, 0) == null)
                                    {
                                        cs.error(new BytePtr("`case` variable `%s` declared at %s cannot be declared in `switch` body"), v.toChars(), v.loc.toChars(global.params.showColumns));
                                        errors = true;
                                    }
                                    break;
                                }
                            }
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                    }
                    else
                    {
                        cs.exp = cs.exp.ctfeInterpret();
                    }
                    {
                        StringExp se = cs.exp.toStringExp();
                        if ((se) != null)
                        {
                            cs.exp = se;
                        }
                        else if (((cs.exp.op & 0xFF) != 135) && ((cs.exp.op & 0xFF) != 127))
                        {
                            cs.error(new BytePtr("`case` must be a `string` or an integral constant, not `%s`"), cs.exp.toChars());
                            errors = true;
                        }
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                {
                    Slice<CaseStatement> __r1643 = (sw.cases.get()).opSlice().copy();
                    int __key1644 = 0;
                    for (; (__key1644 < __r1643.getLength());__key1644 += 1) {
                        CaseStatement cs2 = __r1643.get(__key1644);
                        if (cs2.exp.equals(cs.exp))
                        {
                            cs.error(new BytePtr("duplicate `case %s` in `switch` statement"), cs.exp.toChars());
                            errors = true;
                            break;
                        }
                    }
                }
                (sw.cases.get()).push(cs);
                {
                    int i = 0;
                    for (; (i < sw.gotoCases.length);){
                        GotoCaseStatement gcs = sw.gotoCases.get(i);
                        if (gcs.exp == null)
                        {
                            gcs.cs = cs;
                            sw.gotoCases.remove(i);
                            continue;
                        }
                        i++;
                    }
                }
                if ((!pequals((this.sc.get()).sw.tf, (this.sc.get()).tf)))
                {
                    cs.error(new BytePtr("`switch` and `case` are in different `finally` blocks"));
                    errors = true;
                }
            }
            else
            {
                cs.error(new BytePtr("`case` not in `switch` statement"));
                errors = true;
            }
            (this.sc.get()).ctorflow.orCSX(CSX.label);
            cs.statement = statementSemantic(cs.statement, this.sc);
            if (cs.statement.isErrorStatement() != null)
            {
                this.result = cs.statement;
                return ;
            }
            if (errors || ((cs.exp.op & 0xFF) == 127))
            {
                this.setError();
                return ;
            }
            cs.lastVar = (this.sc.get()).lastVar;
            this.result = cs;
        }

        public  void visit(CaseRangeStatement crs) {
            SwitchStatement sw = (this.sc.get()).sw;
            if ((sw == null))
            {
                crs.error(new BytePtr("case range not in `switch` statement"));
                this.setError();
                return ;
            }
            boolean errors = false;
            if (sw.isFinal)
            {
                crs.error(new BytePtr("case ranges not allowed in `final switch`"));
                errors = true;
            }
            this.sc = pcopy((this.sc.get()).startCTFE());
            crs.first = expressionSemantic(crs.first, this.sc);
            crs.first = resolveProperties(this.sc, crs.first);
            this.sc = pcopy((this.sc.get()).endCTFE());
            crs.first = crs.first.implicitCastTo(this.sc, sw.condition.type.value);
            crs.first = crs.first.ctfeInterpret();
            this.sc = pcopy((this.sc.get()).startCTFE());
            crs.last = expressionSemantic(crs.last, this.sc);
            crs.last = resolveProperties(this.sc, crs.last);
            this.sc = pcopy((this.sc.get()).endCTFE());
            crs.last = crs.last.implicitCastTo(this.sc, sw.condition.type.value);
            crs.last = crs.last.ctfeInterpret();
            if (((crs.first.op & 0xFF) == 127) || ((crs.last.op & 0xFF) == 127) || errors)
            {
                if (crs.statement != null)
                {
                    statementSemantic(crs.statement, this.sc);
                }
                this.setError();
                return ;
            }
            long fval = crs.first.toInteger();
            long lval = crs.last.toInteger();
            if (crs.first.type.value.isunsigned() && (fval > lval) || !crs.first.type.value.isunsigned() && ((long)fval > (long)lval))
            {
                crs.error(new BytePtr("first `case %s` is greater than last `case %s`"), crs.first.toChars(), crs.last.toChars());
                errors = true;
                lval = fval;
            }
            if ((lval - fval > 256L))
            {
                crs.error(new BytePtr("had %llu cases which is more than 256 cases in case range"), lval - fval);
                errors = true;
                lval = fval + 256L;
            }
            if (errors)
            {
                this.setError();
                return ;
            }
            Ptr<DArray<Statement>> statements = refPtr(new DArray<Statement>());
            {
                long i = fval;
                for (; (i != lval + 1L);i++){
                    Statement s = crs.statement;
                    if ((i != lval))
                    {
                        s = new ExpStatement(crs.loc, null);
                    }
                    Expression e = new IntegerExp(crs.loc, i, crs.first.type.value);
                    Statement cs = new CaseStatement(crs.loc, e, s);
                    (statements.get()).push(cs);
                }
            }
            Statement s = new CompoundStatement(crs.loc, statements);
            (this.sc.get()).ctorflow.orCSX(CSX.label);
            s = statementSemantic(s, this.sc);
            this.result = s;
        }

        public  void visit(DefaultStatement ds) {
            boolean errors = false;
            if ((this.sc.get()).sw != null)
            {
                if ((this.sc.get()).sw.sdefault != null)
                {
                    ds.error(new BytePtr("`switch` statement already has a default"));
                    errors = true;
                }
                (this.sc.get()).sw.sdefault = ds;
                if ((!pequals((this.sc.get()).sw.tf, (this.sc.get()).tf)))
                {
                    ds.error(new BytePtr("`switch` and `default` are in different `finally` blocks"));
                    errors = true;
                }
                if ((this.sc.get()).sw.isFinal)
                {
                    ds.error(new BytePtr("`default` statement not allowed in `final switch` statement"));
                    errors = true;
                }
            }
            else
            {
                ds.error(new BytePtr("`default` not in `switch` statement"));
                errors = true;
            }
            (this.sc.get()).ctorflow.orCSX(CSX.label);
            ds.statement = statementSemantic(ds.statement, this.sc);
            if (errors || (ds.statement.isErrorStatement() != null))
            {
                this.setError();
                return ;
            }
            ds.lastVar = (this.sc.get()).lastVar;
            this.result = ds;
        }

        public  void visit(GotoDefaultStatement gds) {
            gds.sw = (this.sc.get()).sw;
            if (gds.sw == null)
            {
                gds.error(new BytePtr("`goto default` not in `switch` statement"));
                this.setError();
                return ;
            }
            if (gds.sw.isFinal)
            {
                gds.error(new BytePtr("`goto default` not allowed in `final switch` statement"));
                this.setError();
                return ;
            }
            this.result = gds;
        }

        public  void visit(GotoCaseStatement gcs) {
            if ((this.sc.get()).sw == null)
            {
                gcs.error(new BytePtr("`goto case` not in `switch` statement"));
                this.setError();
                return ;
            }
            if (gcs.exp != null)
            {
                gcs.exp = expressionSemantic(gcs.exp, this.sc);
                gcs.exp = gcs.exp.implicitCastTo(this.sc, (this.sc.get()).sw.condition.type.value);
                gcs.exp = gcs.exp.optimize(0, false);
                if (((gcs.exp.op & 0xFF) == 127))
                {
                    this.setError();
                    return ;
                }
            }
            (this.sc.get()).sw.gotoCases.push(gcs);
            this.result = gcs;
        }

        public  void visit(ReturnStatement rs) {
            FuncDeclaration fd = (this.sc.get()).parent.value.isFuncDeclaration();
            if (fd.fes != null)
            {
                fd = fd.fes.func;
            }
            TypeFunction tf = (TypeFunction)fd.type;
            assert(((tf.ty & 0xFF) == ENUMTY.Tfunction));
            if ((rs.exp != null) && ((rs.exp.op & 0xFF) == 26) && (pequals(((VarExp)rs.exp).var, fd.vresult)))
            {
                if ((this.sc.get()).fes != null)
                {
                    assert((rs.caseDim == 0));
                    ((this.sc.get()).fes.cases.get()).push(rs);
                    this.result = new ReturnStatement(Loc.initial, new IntegerExp((long)(((this.sc.get()).fes.cases.get()).length + 1)));
                    return ;
                }
                if (fd.returnLabel != null)
                {
                    GotoStatement gs = new GotoStatement(rs.loc, Id.returnLabel);
                    gs.label = fd.returnLabel;
                    this.result = gs;
                    return ;
                }
                if (fd.returns == null)
                {
                    fd.returns = pcopy((refPtr(new DArray<ReturnStatement>())));
                }
                (fd.returns.get()).push(rs);
                this.result = rs;
                return ;
            }
            Type tret = tf.next.value;
            Type tbret = tret != null ? tret.toBasetype() : null;
            boolean inferRef = tf.isref && ((fd.storage_class & 256L) != 0);
            Ref<Expression> e0 = ref(null);
            boolean errors = false;
            if (((this.sc.get()).flags & 96) != 0)
            {
                rs.error(new BytePtr("`return` statements cannot be in contracts"));
                errors = true;
            }
            if (((this.sc.get()).os != null) && (((this.sc.get()).os.tok & 0xFF) != 205))
            {
                rs.error(new BytePtr("`return` statements cannot be in `%s` bodies"), Token.toChars((this.sc.get()).os.tok));
                errors = true;
            }
            if ((this.sc.get()).tf != null)
            {
                rs.error(new BytePtr("`return` statements cannot be in `finally` bodies"));
                errors = true;
            }
            if (fd.isCtorDeclaration() != null)
            {
                if (rs.exp != null)
                {
                    rs.error(new BytePtr("cannot return expression from constructor"));
                    errors = true;
                }
                rs.exp = new ThisExp(Loc.initial);
                rs.exp.type.value = tret;
            }
            else if (rs.exp != null)
            {
                fd.hasReturnExp |= (fd.hasReturnExp & 1) != 0 ? 16 : 1;
                FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                if (tret != null)
                {
                    rs.exp = inferType(rs.exp, tret, 0);
                }
                else if ((fld != null) && (fld.treq != null))
                {
                    rs.exp = inferType(rs.exp, fld.treq.nextOf().nextOf(), 0);
                }
                rs.exp = expressionSemantic(rs.exp, this.sc);
                if (((rs.exp.op & 0xFF) == 20))
                {
                    rs.exp = resolveAliasThis(this.sc, rs.exp, false);
                }
                rs.exp = resolveProperties(this.sc, rs.exp);
                if (rs.exp.checkType())
                {
                    rs.exp = new ErrorExp();
                }
                {
                    FuncDeclaration f = isFuncAddress(rs.exp, null);
                    if ((f) != null)
                    {
                        if (fd.inferRetType && f.checkForwardRef(rs.exp.loc))
                        {
                            rs.exp = new ErrorExp();
                        }
                    }
                }
                if (checkNonAssignmentArrayOp(rs.exp, false))
                {
                    rs.exp = new ErrorExp();
                }
                rs.exp = Expression.extractLast(rs.exp, e0);
                if (((rs.exp.op & 0xFF) == 18))
                {
                    rs.exp = valueNoDtor(rs.exp);
                }
                if (e0.value != null)
                {
                    e0.value = e0.value.optimize(0, false);
                }
                if ((tbret != null) && ((tbret.ty & 0xFF) == ENUMTY.Tvoid) || ((rs.exp.type.value.ty & 0xFF) == ENUMTY.Tvoid))
                {
                    if (((rs.exp.type.value.ty & 0xFF) != ENUMTY.Tvoid))
                    {
                        rs.error(new BytePtr("cannot return non-void from `void` function"));
                        errors = true;
                        rs.exp = new CastExp(rs.loc, rs.exp, Type.tvoid);
                        rs.exp = expressionSemantic(rs.exp, this.sc);
                    }
                    e0.value = Expression.combine(e0.value, rs.exp);
                    rs.exp = null;
                }
                if (e0.value != null)
                {
                    e0.value = checkGC(this.sc, e0.value);
                }
            }
            if (rs.exp != null)
            {
                if (fd.inferRetType)
                {
                    if (tret == null)
                    {
                        tf.next.value = rs.exp.type.value;
                    }
                    else if (((tret.ty & 0xFF) != ENUMTY.Terror) && !rs.exp.type.value.equals(tret))
                    {
                        int m1 = rs.exp.type.value.implicitConvTo(tret);
                        int m2 = tret.implicitConvTo(rs.exp.type.value);
                        if ((m1 != 0) && (m2 != 0))
                        {
                        }
                        else if ((m1 == 0) && (m2 != 0))
                        {
                            tf.next.value = rs.exp.type.value;
                        }
                        else if ((m1 != 0) && (m2 == 0))
                        {
                        }
                        else if (((rs.exp.op & 0xFF) != 127))
                        {
                            rs.error(new BytePtr("mismatched function return type inference of `%s` and `%s`"), rs.exp.type.value.toChars(), tret.toChars());
                            errors = true;
                            tf.next.value = Type.terror;
                        }
                    }
                    tret = tf.next.value;
                    tbret = tret.toBasetype();
                }
                if (inferRef)
                {
                    Function0<Void> turnOffRef = new Function0<Void>() {
                        public Void invoke() {
                         {
                            tf.isref = false;
                            tf.isreturn = false;
                            fd.storage_class &= -17592186044417L;
                            return null;
                        }}

                    };
                    if (rs.exp.isLvalue())
                    {
                        if (checkReturnEscapeRef(this.sc, rs.exp, true))
                        {
                            turnOffRef.invoke();
                        }
                        else if (rs.exp.type.value.constConv(tf.next.value) == 0)
                        {
                            turnOffRef.invoke();
                        }
                    }
                    else
                    {
                        turnOffRef.invoke();
                    }
                }
                if (fd.nrvo_can && ((rs.exp.op & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)rs.exp;
                    VarDeclaration v = ve.var.isVarDeclaration();
                    if (tf.isref)
                    {
                        if (!inferRef)
                        {
                            fd.nrvo_can = false;
                        }
                    }
                    else if ((v == null) || v.isOut() || v.isRef())
                    {
                        fd.nrvo_can = false;
                    }
                    else if ((fd.nrvo_var == null))
                    {
                        if (!v.isDataseg() && !v.isParameter() && (pequals(v.toParent2(), fd)))
                        {
                            fd.nrvo_var = v;
                        }
                        else
                        {
                            fd.nrvo_can = false;
                        }
                    }
                    else if ((!pequals(fd.nrvo_var, v)))
                    {
                        fd.nrvo_can = false;
                    }
                }
                else
                {
                    fd.nrvo_can = false;
                }
            }
            else
            {
                fd.nrvo_can = false;
                if (fd.inferRetType)
                {
                    if ((tf.next.value != null) && ((tf.next.value.ty & 0xFF) != ENUMTY.Tvoid))
                    {
                        if (((tf.next.value.ty & 0xFF) != ENUMTY.Terror))
                        {
                            rs.error(new BytePtr("mismatched function return type inference of `void` and `%s`"), tf.next.value.toChars());
                        }
                        errors = true;
                        tf.next.value = Type.terror;
                    }
                    else
                    {
                        tf.next.value = Type.tvoid;
                    }
                    tret = tf.next.value;
                    tbret = tret.toBasetype();
                }
                if (inferRef)
                {
                    tf.isref = false;
                }
                if (((tbret.ty & 0xFF) != ENUMTY.Tvoid))
                {
                    if (((tbret.ty & 0xFF) != ENUMTY.Terror))
                    {
                        rs.error(new BytePtr("`return` expression expected"));
                    }
                    errors = true;
                }
                else if (fd.isMain())
                {
                    rs.exp = literal_B6589FC6AB0DC82C();
                }
            }
            if ((((this.sc.get()).ctorflow.callSuper.value & 16) != 0) && (((this.sc.get()).ctorflow.callSuper.value & 3) == 0))
            {
                rs.error(new BytePtr("`return` without calling constructor"));
                errors = true;
            }
            if ((this.sc.get()).ctorflow.fieldinit.getLength() != 0)
            {
                AggregateDeclaration ad = fd.isMemberLocal();
                assert(ad != null);
                {
                    Slice<VarDeclaration> __r1646 = ad.fields.opSlice().copy();
                    int __key1645 = 0;
                    for (; (__key1645 < __r1646.getLength());__key1645 += 1) {
                        VarDeclaration v = __r1646.get(__key1645);
                        int i = __key1645;
                        boolean mustInit = ((v.storage_class & 549755813888L) != 0) || v.type.needsNested();
                        if (mustInit && (((this.sc.get()).ctorflow.fieldinit.get(i).csx.value & 1) == 0))
                        {
                            rs.error(new BytePtr("an earlier `return` statement skips field `%s` initialization"), v.toChars());
                            errors = true;
                        }
                    }
                }
            }
            (this.sc.get()).ctorflow.orCSX(CSX.return_);
            if (errors)
            {
                this.setError();
                return ;
            }
            if ((this.sc.get()).fes != null)
            {
                if (rs.exp == null)
                {
                    Statement s = new ReturnStatement(Loc.initial, rs.exp);
                    ((this.sc.get()).fes.cases.get()).push(s);
                    rs.exp = new IntegerExp((long)(((this.sc.get()).fes.cases.get()).length + 1));
                    if (e0.value != null)
                    {
                        this.result = new CompoundStatement(rs.loc, slice(new Statement[]{new ExpStatement(rs.loc, e0.value), rs}));
                        return ;
                    }
                    this.result = rs;
                    return ;
                }
                else
                {
                    fd.buildResultVar(null, rs.exp.type.value);
                    boolean r = fd.vresult.checkNestedReference(this.sc, Loc.initial);
                    assert(!r);
                    Statement s = new ReturnStatement(Loc.initial, new VarExp(Loc.initial, fd.vresult, true));
                    ((this.sc.get()).fes.cases.get()).push(s);
                    rs.caseDim = ((this.sc.get()).fes.cases.get()).length + 1;
                }
            }
            if (rs.exp != null)
            {
                if (fd.returns == null)
                {
                    fd.returns = pcopy((refPtr(new DArray<ReturnStatement>())));
                }
                (fd.returns.get()).push(rs);
            }
            if (e0.value != null)
            {
                if (((e0.value.op & 0xFF) == 38) || ((e0.value.op & 0xFF) == 99))
                {
                    rs.exp = Expression.combine(e0.value, rs.exp);
                }
                else
                {
                    this.result = new CompoundStatement(rs.loc, slice(new Statement[]{new ExpStatement(rs.loc, e0.value), rs}));
                    return ;
                }
            }
            this.result = rs;
        }

        public  void visit(BreakStatement bs) {
            if (bs.ident != null)
            {
                bs.ident = fixupLabelName(this.sc, bs.ident);
                FuncDeclaration thisfunc = (this.sc.get()).func;
                {
                    Ptr<Scope> scx = this.sc;
                    for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                        if ((!pequals((scx.get()).func, thisfunc)))
                        {
                            if ((this.sc.get()).fes != null)
                            {
                                ((this.sc.get()).fes.cases.get()).push(bs);
                                this.result = new ReturnStatement(Loc.initial, new IntegerExp((long)(((this.sc.get()).fes.cases.get()).length + 1)));
                                return ;
                            }
                            break;
                        }
                        LabelStatement ls = (scx.get()).slabel;
                        if ((ls != null) && (pequals(ls.ident, bs.ident)))
                        {
                            Statement s = ls.statement;
                            if ((s == null) || !s.hasBreak())
                            {
                                bs.error(new BytePtr("label `%s` has no `break`"), bs.ident.toChars());
                            }
                            else if ((!pequals(ls.tf, (this.sc.get()).tf)))
                            {
                                bs.error(new BytePtr("cannot break out of `finally` block"));
                            }
                            else
                            {
                                ls.breaks = true;
                                this.result = bs;
                                return ;
                            }
                            this.setError();
                            return ;
                        }
                    }
                }
                bs.error(new BytePtr("enclosing label `%s` for `break` not found"), bs.ident.toChars());
                this.setError();
                return ;
            }
            else if ((this.sc.get()).sbreak == null)
            {
                if (((this.sc.get()).os != null) && (((this.sc.get()).os.tok & 0xFF) != 205))
                {
                    bs.error(new BytePtr("`break` is not inside `%s` bodies"), Token.toChars((this.sc.get()).os.tok));
                }
                else if ((this.sc.get()).fes != null)
                {
                    this.result = new ReturnStatement(Loc.initial, literal_356A192B7913B04C());
                    return ;
                }
                else
                {
                    bs.error(new BytePtr("`break` is not inside a loop or `switch`"));
                }
                this.setError();
                return ;
            }
            else if ((this.sc.get()).sbreak.isForwardingStatement() != null)
            {
                bs.error(new BytePtr("must use labeled `break` within `static foreach`"));
            }
            this.result = bs;
        }

        public  void visit(ContinueStatement cs) {
            if (cs.ident != null)
            {
                cs.ident = fixupLabelName(this.sc, cs.ident);
                Ptr<Scope> scx = null;
                FuncDeclaration thisfunc = (this.sc.get()).func;
                {
                    scx = pcopy(this.sc);
                    for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                        LabelStatement ls = null;
                        if ((!pequals((scx.get()).func, thisfunc)))
                        {
                            if ((this.sc.get()).fes != null)
                            {
                                for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                                    ls = (scx.get()).slabel;
                                    if ((ls != null) && (pequals(ls.ident, cs.ident)) && (pequals(ls.statement, (this.sc.get()).fes)))
                                    {
                                        this.result = new ReturnStatement(Loc.initial, literal_B6589FC6AB0DC82C());
                                        return ;
                                    }
                                }
                                ((this.sc.get()).fes.cases.get()).push(cs);
                                this.result = new ReturnStatement(Loc.initial, new IntegerExp((long)(((this.sc.get()).fes.cases.get()).length + 1)));
                                return ;
                            }
                            break;
                        }
                        ls = (scx.get()).slabel;
                        if ((ls != null) && (pequals(ls.ident, cs.ident)))
                        {
                            Statement s = ls.statement;
                            if ((s == null) || !s.hasContinue())
                            {
                                cs.error(new BytePtr("label `%s` has no `continue`"), cs.ident.toChars());
                            }
                            else if ((!pequals(ls.tf, (this.sc.get()).tf)))
                            {
                                cs.error(new BytePtr("cannot continue out of `finally` block"));
                            }
                            else
                            {
                                this.result = cs;
                                return ;
                            }
                            this.setError();
                            return ;
                        }
                    }
                }
                cs.error(new BytePtr("enclosing label `%s` for `continue` not found"), cs.ident.toChars());
                this.setError();
                return ;
            }
            else if ((this.sc.get()).scontinue == null)
            {
                if (((this.sc.get()).os != null) && (((this.sc.get()).os.tok & 0xFF) != 205))
                {
                    cs.error(new BytePtr("`continue` is not inside `%s` bodies"), Token.toChars((this.sc.get()).os.tok));
                }
                else if ((this.sc.get()).fes != null)
                {
                    this.result = new ReturnStatement(Loc.initial, literal_B6589FC6AB0DC82C());
                    return ;
                }
                else
                {
                    cs.error(new BytePtr("`continue` is not inside a loop"));
                }
                this.setError();
                return ;
            }
            else if ((this.sc.get()).scontinue.isForwardingStatement() != null)
            {
                cs.error(new BytePtr("must use labeled `continue` within `static foreach`"));
            }
            this.result = cs;
        }

        public  void visit(SynchronizedStatement ss) {
            if (ss.exp != null)
            {
                ss.exp = expressionSemantic(ss.exp, this.sc);
                ss.exp = resolveProperties(this.sc, ss.exp);
                ss.exp = ss.exp.optimize(0, false);
                ss.exp = checkGC(this.sc, ss.exp);
                if (((ss.exp.op & 0xFF) == 127))
                {
                    if (ss._body != null)
                    {
                        ss._body = statementSemantic(ss._body, this.sc);
                    }
                    this.setError();
                    return ;
                }
                ClassDeclaration cd = ss.exp.type.value.isClassHandle();
                if (cd == null)
                {
                    ss.error(new BytePtr("can only `synchronize` on class objects, not `%s`"), ss.exp.type.value.toChars());
                    this.setError();
                    return ;
                }
                else if (cd.isInterfaceDeclaration() != null)
                {
                    if (ClassDeclaration.object == null)
                    {
                        ss.error(new BytePtr("missing or corrupt object.d"));
                        fatal();
                    }
                    Type t = ClassDeclaration.object.type;
                    t = typeSemantic(t, Loc.initial, this.sc).toBasetype();
                    assert(((t.ty & 0xFF) == ENUMTY.Tclass));
                    ss.exp = new CastExp(ss.loc, ss.exp, t);
                    ss.exp = expressionSemantic(ss.exp, this.sc);
                }
                VarDeclaration tmp = copyToTemp(0L, new BytePtr("__sync"), ss.exp);
                dsymbolSemantic(tmp, this.sc);
                Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
                (cs.get()).push(new ExpStatement(ss.loc, tmp));
                Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>());
                (args.get()).push(new Parameter(0L, ClassDeclaration.object.type, null, null, null));
                FuncDeclaration fdenter = FuncDeclaration.genCfunc(args, Type.tvoid, Id.monitorenter, 0L);
                Expression e = new CallExp(ss.loc, fdenter, new VarExp(ss.loc, tmp, true));
                e.type.value = Type.tvoid;
                (cs.get()).push(new ExpStatement(ss.loc, e));
                FuncDeclaration fdexit = FuncDeclaration.genCfunc(args, Type.tvoid, Id.monitorexit, 0L);
                e = new CallExp(ss.loc, fdexit, new VarExp(ss.loc, tmp, true));
                e.type.value = Type.tvoid;
                Statement s = new ExpStatement(ss.loc, e);
                s = new TryFinallyStatement(ss.loc, ss._body, s);
                (cs.get()).push(s);
                s = new CompoundStatement(ss.loc, cs);
                this.result = statementSemantic(s, this.sc);
            }
            else
            {
                Identifier id = Identifier.generateId(new BytePtr("__critsec"));
                Type t = Type.tint8.sarrayOf((long)(target.ptrsize + target.critsecsize()));
                VarDeclaration tmp = new VarDeclaration(ss.loc, t, id, null, 0L);
                tmp.storage_class |= 1100048498689L;
                Expression tmpExp = new VarExp(ss.loc, tmp, true);
                Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
                (cs.get()).push(new ExpStatement(ss.loc, tmp));
                VarDeclaration v = new VarDeclaration(ss.loc, Type.tvoidptr, Identifier.generateId(new BytePtr("__sync")), null, 0L);
                dsymbolSemantic(v, this.sc);
                (cs.get()).push(new ExpStatement(ss.loc, v));
                Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>());
                (args.get()).push(new Parameter(0L, t.pointerTo(), null, null, null));
                FuncDeclaration fdenter = FuncDeclaration.genCfunc(args, Type.tvoid, Id.criticalenter, 33554432L);
                Expression int0 = new IntegerExp(ss.loc, 0L, Type.tint8);
                Expression e = new AddrExp(ss.loc, new IndexExp(ss.loc, tmpExp, int0));
                e = expressionSemantic(e, this.sc);
                e = new CallExp(ss.loc, fdenter, e);
                e.type.value = Type.tvoid;
                (cs.get()).push(new ExpStatement(ss.loc, e));
                FuncDeclaration fdexit = FuncDeclaration.genCfunc(args, Type.tvoid, Id.criticalexit, 33554432L);
                e = new AddrExp(ss.loc, new IndexExp(ss.loc, tmpExp, int0));
                e = expressionSemantic(e, this.sc);
                e = new CallExp(ss.loc, fdexit, e);
                e.type.value = Type.tvoid;
                Statement s = new ExpStatement(ss.loc, e);
                s = new TryFinallyStatement(ss.loc, ss._body, s);
                (cs.get()).push(s);
                s = new CompoundStatement(ss.loc, cs);
                this.result = statementSemantic(s, this.sc);
                tmp.alignment = target.ptrsize;
            }
        }

        public  void visit(WithStatement ws) {
            ScopeDsymbol sym = null;
            Initializer _init = null;
            ws.exp = expressionSemantic(ws.exp, this.sc);
            ws.exp = resolveProperties(this.sc, ws.exp);
            ws.exp = ws.exp.optimize(0, false);
            ws.exp = checkGC(this.sc, ws.exp);
            if (((ws.exp.op & 0xFF) == 127))
            {
                this.setError();
                return ;
            }
            if (((ws.exp.op & 0xFF) == 203))
            {
                sym = new WithScopeSymbol(ws);
                sym.parent.value = (this.sc.get()).scopesym;
                sym.endlinnum = ws.endloc.linnum;
            }
            else if (((ws.exp.op & 0xFF) == 20))
            {
                Dsymbol s = ((TypeExp)ws.exp).type.value.toDsymbol(this.sc);
                if ((s == null) || (s.isScopeDsymbol() == null))
                {
                    ws.error(new BytePtr("`with` type `%s` has no members"), ws.exp.toChars());
                    this.setError();
                    return ;
                }
                sym = new WithScopeSymbol(ws);
                sym.parent.value = (this.sc.get()).scopesym;
                sym.endlinnum = ws.endloc.linnum;
            }
            else
            {
                Type t = ws.exp.type.value.toBasetype();
                Expression olde = ws.exp;
                if (((t.ty & 0xFF) == ENUMTY.Tpointer))
                {
                    ws.exp = new PtrExp(ws.loc, ws.exp);
                    ws.exp = expressionSemantic(ws.exp, this.sc);
                    t = ws.exp.type.value.toBasetype();
                }
                assert(t != null);
                t = t.toBasetype();
                if (t.isClassHandle() != null)
                {
                    _init = new ExpInitializer(ws.loc, ws.exp);
                    ws.wthis = new VarDeclaration(ws.loc, ws.exp.type.value, Id.withSym, _init, 0L);
                    dsymbolSemantic(ws.wthis, this.sc);
                    sym = new WithScopeSymbol(ws);
                    sym.parent.value = (this.sc.get()).scopesym;
                    sym.endlinnum = ws.endloc.linnum;
                }
                else if (((t.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    if (!ws.exp.isLvalue())
                    {
                        VarDeclaration tmp = copyToTemp(0L, new BytePtr("__withtmp"), ws.exp);
                        dsymbolSemantic(tmp, this.sc);
                        ExpStatement es = new ExpStatement(ws.loc, tmp);
                        ws.exp = new VarExp(ws.loc, tmp, true);
                        Statement ss = new ScopeStatement(ws.loc, new CompoundStatement(ws.loc, slice(new Statement[]{es, ws})), ws.endloc);
                        this.result = statementSemantic(ss, this.sc);
                        return ;
                    }
                    Expression e = ws.exp.addressOf();
                    _init = new ExpInitializer(ws.loc, e);
                    ws.wthis = new VarDeclaration(ws.loc, e.type.value, Id.withSym, _init, 0L);
                    dsymbolSemantic(ws.wthis, this.sc);
                    sym = new WithScopeSymbol(ws);
                    sym.setScope(this.sc);
                    sym.parent.value = (this.sc.get()).scopesym;
                    sym.endlinnum = ws.endloc.linnum;
                }
                else
                {
                    ws.error(new BytePtr("`with` expressions must be aggregate types or pointers to them, not `%s`"), olde.type.value.toChars());
                    this.setError();
                    return ;
                }
            }
            if (ws._body != null)
            {
                sym._scope = pcopy(this.sc);
                this.sc = pcopy((this.sc.get()).push(sym));
                (this.sc.get()).insert(sym);
                ws._body = statementSemantic(ws._body, this.sc);
                (this.sc.get()).pop();
                if ((ws._body != null) && (ws._body.isErrorStatement() != null))
                {
                    this.result = ws._body;
                    return ;
                }
            }
            this.result = ws;
        }

        public  void visit(TryCatchStatement tcs) {
            if (!global.params.useExceptions)
            {
                tcs.error(new BytePtr("Cannot use try-catch statements with -betterC"));
                this.setError();
                return ;
            }
            if (ClassDeclaration.throwable == null)
            {
                tcs.error(new BytePtr("Cannot use try-catch statements because `object.Throwable` was not declared"));
                this.setError();
                return ;
            }
            int flags = 0;
            int FLAGcpp = 1;
            int FLAGd = 2;
            tcs._body = semanticScope(tcs._body, this.sc, null, null);
            assert(tcs._body != null);
            boolean catchErrors = false;
            {
                Slice<Catch> __r1648 = (tcs.catches.get()).opSlice().copy();
                int __key1647 = 0;
                for (; (__key1647 < __r1648.getLength());__key1647 += 1) {
                    Catch c = __r1648.get(__key1647);
                    int i = __key1647;
                    catchSemantic(c, this.sc);
                    if (c.errors)
                    {
                        catchErrors = true;
                        continue;
                    }
                    ClassDeclaration cd = c.type.toBasetype().isClassHandle();
                    flags |= cd.isCPPclass() ? 1 : 2;
                    {
                        int __key1649 = 0;
                        int __limit1650 = i;
                        for (; (__key1649 < __limit1650);__key1649 += 1) {
                            int j = __key1649;
                            Catch cj = (tcs.catches.get()).get(j);
                            BytePtr si = pcopy(c.loc.toChars(global.params.showColumns));
                            BytePtr sj = pcopy(cj.loc.toChars(global.params.showColumns));
                            if (c.type.toBasetype().implicitConvTo(cj.type.toBasetype()) != 0)
                            {
                                tcs.error(new BytePtr("`catch` at %s hides `catch` at %s"), sj, si);
                                catchErrors = true;
                            }
                        }
                    }
                }
            }
            if ((this.sc.get()).func != null)
            {
                (this.sc.get()).func.flags |= FUNCFLAG.hasCatches;
                if ((flags == 3))
                {
                    tcs.error(new BytePtr("cannot mix catching D and C++ exceptions in the same try-catch"));
                    catchErrors = true;
                }
            }
            if (catchErrors)
            {
                this.setError();
                return ;
            }
            if (tcs._body.isErrorStatement() != null)
            {
                this.result = tcs._body;
                return ;
            }
            if (((blockExit(tcs._body, (this.sc.get()).func, false) & BE.throw_) == 0) && (ClassDeclaration.exception != null))
            {
                {
                    int __limit1652 = 0;
                    int __key1651 = (tcs.catches.get()).length;
                    for (; (__key1651-- > __limit1652);) {
                        int i = __key1651;
                        Catch c = (tcs.catches.get()).get(i);
                        if ((c.type.toBasetype().implicitConvTo(ClassDeclaration.exception.type) != 0) && (c.handler == null) || !c.handler.comeFrom())
                        {
                            (tcs.catches.get()).remove(i);
                        }
                    }
                }
            }
            if (((tcs.catches.get()).length == 0))
            {
                this.result = tcs._body.hasCode() ? tcs._body : null;
                return ;
            }
            this.result = tcs;
        }

        public  void visit(TryFinallyStatement tfs) {
            tfs._body = statementSemantic(tfs._body, this.sc);
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).tf = tfs;
            (this.sc.get()).sbreak = null;
            (this.sc.get()).scontinue = null;
            tfs.finalbody = semanticNoScope(tfs.finalbody, this.sc);
            (this.sc.get()).pop();
            if (tfs._body == null)
            {
                this.result = tfs.finalbody;
                return ;
            }
            if (tfs.finalbody == null)
            {
                this.result = tfs._body;
                return ;
            }
            int blockexit = blockExit(tfs._body, (this.sc.get()).func, false);
            if (!(global.params.useExceptions && (ClassDeclaration.throwable != null)))
            {
                blockexit &= -3;
            }
            if (((blockexit & -17) == BE.fallthru))
            {
                this.result = new CompoundStatement(tfs.loc, slice(new Statement[]{tfs._body, tfs.finalbody}));
                return ;
            }
            tfs.bodyFallsThru = (blockexit & BE.fallthru) != 0;
            this.result = tfs;
        }

        public  void visit(ScopeGuardStatement oss) {
            if (((oss.tok & 0xFF) != 204))
            {
                if (((this.sc.get()).os != null) && (((this.sc.get()).os.tok & 0xFF) != 205))
                {
                    oss.error(new BytePtr("cannot put `%s` statement inside `%s`"), Token.toChars(oss.tok), Token.toChars((this.sc.get()).os.tok));
                    this.setError();
                    return ;
                }
                if ((this.sc.get()).tf != null)
                {
                    oss.error(new BytePtr("cannot put `%s` statement inside `finally` block"), Token.toChars(oss.tok));
                    this.setError();
                    return ;
                }
            }
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).tf = null;
            (this.sc.get()).os = oss;
            if (((oss.tok & 0xFF) != 205))
            {
                (this.sc.get()).sbreak = null;
                (this.sc.get()).scontinue = null;
            }
            oss.statement = semanticNoScope(oss.statement, this.sc);
            (this.sc.get()).pop();
            if ((oss.statement == null) || (oss.statement.isErrorStatement() != null))
            {
                this.result = oss.statement;
                return ;
            }
            this.result = oss;
        }

        public  void visit(ThrowStatement ts) {
            if (!global.params.useExceptions)
            {
                ts.error(new BytePtr("Cannot use `throw` statements with -betterC"));
                this.setError();
                return ;
            }
            if (ClassDeclaration.throwable == null)
            {
                ts.error(new BytePtr("Cannot use `throw` statements because `object.Throwable` was not declared"));
                this.setError();
                return ;
            }
            FuncDeclaration fd = (this.sc.get()).parent.value.isFuncDeclaration();
            fd.hasReturnExp |= 2;
            if (((ts.exp.op & 0xFF) == 22))
            {
                NewExp ne = (NewExp)ts.exp;
                ne.thrownew = true;
            }
            ts.exp = expressionSemantic(ts.exp, this.sc);
            ts.exp = resolveProperties(this.sc, ts.exp);
            ts.exp = checkGC(this.sc, ts.exp);
            if (((ts.exp.op & 0xFF) == 127))
            {
                this.setError();
                return ;
            }
            checkThrowEscape(this.sc, ts.exp, false);
            ClassDeclaration cd = ts.exp.type.value.toBasetype().isClassHandle();
            if ((cd == null) || (!pequals(cd, ClassDeclaration.throwable)) && !ClassDeclaration.throwable.isBaseOf(cd, null))
            {
                ts.error(new BytePtr("can only throw class objects derived from `Throwable`, not type `%s`"), ts.exp.type.value.toChars());
                this.setError();
                return ;
            }
            this.result = ts;
        }

        public  void visit(DebugStatement ds) {
            if (ds.statement != null)
            {
                this.sc = pcopy((this.sc.get()).push());
                (this.sc.get()).flags |= 8;
                ds.statement = statementSemantic(ds.statement, this.sc);
                (this.sc.get()).pop();
            }
            this.result = ds.statement;
        }

        public  void visit(GotoStatement gs) {
            FuncDeclaration fd = (this.sc.get()).func;
            gs.ident = fixupLabelName(this.sc, gs.ident);
            gs.label = fd.searchLabel(gs.ident);
            gs.tf = (this.sc.get()).tf;
            gs.os = (this.sc.get()).os;
            gs.lastVar = (this.sc.get()).lastVar;
            if ((gs.label.statement == null) && ((this.sc.get()).fes != null))
            {
                ScopeStatement ss = new ScopeStatement(gs.loc, gs, gs.loc);
                ((this.sc.get()).fes.gotos.get()).push(ss);
                this.result = ss;
                return ;
            }
            if (gs.label.statement == null)
            {
                if (fd.gotos == null)
                {
                    fd.gotos = pcopy((refPtr(new DArray<GotoStatement>())));
                }
                (fd.gotos.get()).push(gs);
            }
            else if (gs.checkLabel())
            {
                this.setError();
                return ;
            }
            this.result = gs;
        }

        public  void visit(LabelStatement ls) {
            FuncDeclaration fd = (this.sc.get()).parent.value.isFuncDeclaration();
            ls.ident = fixupLabelName(this.sc, ls.ident);
            ls.tf = (this.sc.get()).tf;
            ls.os = (this.sc.get()).os;
            ls.lastVar = (this.sc.get()).lastVar;
            LabelDsymbol ls2 = fd.searchLabel(ls.ident);
            if (ls2.statement != null)
            {
                ls.error(new BytePtr("label `%s` already defined"), ls2.toChars());
                this.setError();
                return ;
            }
            else
            {
                ls2.statement = ls;
            }
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).scopesym = ((this.sc.get()).enclosing.get()).scopesym;
            (this.sc.get()).ctorflow.orCSX(CSX.label);
            (this.sc.get()).slabel = ls;
            if (ls.statement != null)
            {
                ls.statement = statementSemantic(ls.statement, this.sc);
            }
            (this.sc.get()).pop();
            this.result = ls;
        }

        public  void visit(AsmStatement s) {
            this.result = asmSemantic(s, this.sc);
        }

        public  void visit(CompoundAsmStatement cas) {
            this.sc = pcopy((this.sc.get()).push());
            (this.sc.get()).stc |= cas.stc;
            {
                Slice<Statement> __r1653 = (cas.statements.get()).opSlice().copy();
                int __key1654 = 0;
                for (; (__key1654 < __r1653.getLength());__key1654 += 1) {
                    Statement s = __r1653.get(__key1654);
                    s = s != null ? statementSemantic(s, this.sc) : null;
                }
            }
            assert((this.sc.get()).func != null);
            int purity = PURE.impure;
            if (((cas.stc & 67108864L) == 0) && ((purity = (this.sc.get()).func.isPureBypassingInference()) != PURE.impure) && (purity != PURE.fwdref))
            {
                cas.deprecation(new BytePtr("`asm` statement is assumed to be impure - mark it with `pure` if it is not"));
            }
            if (((cas.stc & 4398046511104L) == 0) && (this.sc.get()).func.isNogcBypassingInference())
            {
                cas.deprecation(new BytePtr("`asm` statement is assumed to use the GC - mark it with `@nogc` if it does not"));
            }
            if (((cas.stc & 25769803776L) == 0) && (this.sc.get()).func.setUnsafe())
            {
                cas.error(new BytePtr("`asm` statement is assumed to be `@system` - mark it with `@trusted` if it is not"));
            }
            (this.sc.get()).pop();
            this.result = cas;
        }

        public  void visit(ImportStatement imps) {
            {
                int __key1655 = 0;
                int __limit1656 = (imps.imports.get()).length;
                for (; (__key1655 < __limit1656);__key1655 += 1) {
                    int i = __key1655;
                    Import s = (imps.imports.get()).get(i).isImport();
                    assert(s.aliasdecls.length == 0);
                    {
                        Slice<Identifier> __r1658 = s.names.opSlice().copy();
                        int __key1657 = 0;
                        for (; (__key1657 < __r1658.getLength());__key1657 += 1) {
                            Identifier name = __r1658.get(__key1657);
                            int j = __key1657;
                            Identifier _alias = s.aliases.get(j);
                            if (_alias == null)
                            {
                                _alias = name;
                            }
                            TypeIdentifier tname = new TypeIdentifier(s.loc, name);
                            AliasDeclaration ad = new AliasDeclaration(s.loc, _alias, tname);
                            ad._import = s;
                            s.aliasdecls.push(ad);
                        }
                    }
                    dsymbolSemantic(s, this.sc);
                    if ((s.mod != null))
                    {
                        dmodule.Module.addDeferredSemantic2(s);
                        (this.sc.get()).insert(s);
                        {
                            Slice<AliasDeclaration> __r1659 = s.aliasdecls.opSlice().copy();
                            int __key1660 = 0;
                            for (; (__key1660 < __r1659.getLength());__key1660 += 1) {
                                AliasDeclaration aliasdecl = __r1659.get(__key1660);
                                (this.sc.get()).insert(aliasdecl);
                            }
                        }
                    }
                }
            }
            this.result = imps;
        }


        public StatementSemanticVisitor() {}

        public StatementSemanticVisitor copy() {
            StatementSemanticVisitor that = new StatementSemanticVisitor();
            that.result = this.result;
            that.sc = this.sc;
            return that;
        }
    }
    public static void catchSemantic(Catch c, Ptr<Scope> sc) {
        if (((sc.get()).os != null) && (((sc.get()).os.tok & 0xFF) != 205))
        {
            error(c.loc, new BytePtr("cannot put `catch` statement inside `%s`"), Token.toChars((sc.get()).os.tok));
            c.errors = true;
        }
        if ((sc.get()).tf != null)
        {
            error(c.loc, new BytePtr("cannot put `catch` statement inside `finally` block"));
            c.errors = true;
        }
        ScopeDsymbol sym = new ScopeDsymbol();
        sym.parent.value = (sc.get()).scopesym;
        sc = pcopy((sc.get()).push(sym));
        if (c.type == null)
        {
            error(c.loc, new BytePtr("`catch` statement without an exception specification is deprecated"));
            errorSupplemental(c.loc, new BytePtr("use `catch(Throwable)` for old behavior"));
            c.errors = true;
            c.type = getThrowable();
        }
        c.type = typeSemantic(c.type, c.loc, sc);
        if ((pequals(c.type, Type.terror)))
        {
            c.errors = true;
        }
        else
        {
            long stc = 0L;
            ClassDeclaration cd = c.type.toBasetype().isClassHandle();
            if (cd == null)
            {
                error(c.loc, new BytePtr("can only catch class objects, not `%s`"), c.type.toChars());
                c.errors = true;
            }
            else if (cd.isCPPclass())
            {
                if (!target.cppExceptions)
                {
                    error(c.loc, new BytePtr("catching C++ class objects not supported for this target"));
                    c.errors = true;
                }
                if (((sc.get()).func != null) && ((sc.get()).intypeof == 0) && !c.internalCatch && (sc.get()).func.setUnsafe())
                {
                    error(c.loc, new BytePtr("cannot catch C++ class objects in `@safe` code"));
                    c.errors = true;
                }
            }
            else if ((!pequals(cd, ClassDeclaration.throwable)) && !ClassDeclaration.throwable.isBaseOf(cd, null))
            {
                error(c.loc, new BytePtr("can only catch class objects derived from `Throwable`, not `%s`"), c.type.toChars());
                c.errors = true;
            }
            else if (((sc.get()).func != null) && ((sc.get()).intypeof == 0) && !c.internalCatch && (ClassDeclaration.exception != null) && (!pequals(cd, ClassDeclaration.exception)) && !ClassDeclaration.exception.isBaseOf(cd, null) && (sc.get()).func.setUnsafe())
            {
                error(c.loc, new BytePtr("can only catch class objects derived from `Exception` in `@safe` code, not `%s`"), c.type.toChars());
                c.errors = true;
            }
            else if (global.params.ehnogc)
            {
                stc |= 524288L;
            }
            if (c.ident != null)
            {
                c.var = new VarDeclaration(c.loc, c.type, c.ident, null, stc);
                c.var.iscatchvar = true;
                dsymbolSemantic(c.var, sc);
                (sc.get()).insert(c.var);
                if (global.params.ehnogc && ((stc & 524288L) != 0))
                {
                    assert(c.var.edtor == null);
                    Loc loc = c.loc.copy();
                    Expression e = new VarExp(loc, c.var, true);
                    e = new CallExp(loc, new IdentifierExp(loc, Id._d_delThrowable), e);
                    Expression ec = new IdentifierExp(loc, Id.ctfe);
                    ec = new NotExp(loc, ec);
                    Statement s = new IfStatement(loc, null, ec, new ExpStatement(loc, e), null, loc);
                    c.handler = new TryFinallyStatement(loc, c.handler, s);
                }
            }
            c.handler = statementSemantic(c.handler, sc);
            if ((c.handler != null) && (c.handler.isErrorStatement() != null))
            {
                c.errors = true;
            }
        }
        (sc.get()).pop();
    }

    public static Statement semanticNoScope(Statement s, Ptr<Scope> sc) {
        if ((s.isCompoundStatement() == null) && (s.isScopeStatement() == null))
        {
            s = new CompoundStatement(s.loc, slice(new Statement[]{s}));
        }
        s = statementSemantic(s, sc);
        return s;
    }

    public static Statement semanticScope(Statement s, Ptr<Scope> sc, Statement sbreak, Statement scontinue) {
        ScopeDsymbol sym = new ScopeDsymbol();
        sym.parent.value = (sc.get()).scopesym;
        Ptr<Scope> scd = (sc.get()).push(sym);
        if (sbreak != null)
        {
            (scd.get()).sbreak = sbreak;
        }
        if (scontinue != null)
        {
            (scd.get()).scontinue = scontinue;
        }
        s = semanticNoScope(s, scd);
        (scd.get()).pop();
        return s;
    }

    // from template TupleForeachArgs!(00)
    // from template Seq!()


    // from template TupleForeachArgs!(00)

    // from template TupleForeachArgs!(00)


    // from template TupleForeachArgs!(10)
    // from template Seq!(Boolean)


    // from template TupleForeachArgs!(10)


    // from template TupleForeachArgs!(10)


    // from template TupleForeachArgs!(11)
    // from template Seq!(Boolean)

    // from template Seq!(Ptr<DArray<Dsymbol>>Boolean)


    // from template TupleForeachArgs!(11)


    // from template TupleForeachArgs!(11)

    // from template TupleForeachRet!(10)

    // from template TupleForeachRet!(10)


    // from template TupleForeachRet!(11)

    // from template TupleForeachRet!(11)

    // from template makeTupleForeach!(10)
    public static Statement makeTupleForeach10(Ptr<Scope> sc, ForeachStatement fs, boolean _param_2) {
        StatementSemanticVisitor v = new StatementSemanticVisitor(sc);
        v.makeTupleForeach10(fs, _param_2);
        return v.result;
    }


    // from template makeTupleForeach!(11)
    public static Ptr<DArray<Dsymbol>> makeTupleForeach11(Ptr<Scope> sc, ForeachStatement fs, Ptr<DArray<Dsymbol>> _param_2, boolean _param_3) {
        StatementSemanticVisitor v = new StatementSemanticVisitor(sc);
        return v.makeTupleForeach11(fs, _param_2, _param_3);
    }


}
