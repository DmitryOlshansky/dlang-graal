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
        int flags = (sc.get()).flags.value & 96;
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
        if (((sc.get()).slabel != null) && (pequals((sc.get()).slabel.statement.value, statement)))
        {
            return (sc.get()).slabel;
        }
        return null;
    }

    public static Expression checkAssignmentAsCondition(Expression e) {
        Expression ec = lastComma(e);
        if (((ec.op.value & 0xFF) == 90))
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
        public Ref<Ptr<Scope>> sc = ref(null);
        public  StatementSemanticVisitor(Ptr<Scope> sc) {
            this.sc.value = sc;
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
            if (s.exp.value != null)
            {
                CommaExp.allow(s.exp.value);
                s.exp.value = expressionSemantic(s.exp.value, this.sc.value);
                s.exp.value = resolveProperties(this.sc.value, s.exp.value);
                s.exp.value = s.exp.value.addDtorHook(this.sc.value);
                if (checkNonAssignmentArrayOp(s.exp.value, false))
                    s.exp.value = new ErrorExp();
                {
                    FuncDeclaration f = isFuncAddress(s.exp.value, null);
                    if ((f) != null)
                    {
                        if (f.checkForwardRef(s.exp.value.loc.value))
                            s.exp.value = new ErrorExp();
                    }
                }
                if (discardValue(s.exp.value))
                    s.exp.value = new ErrorExp();
                s.exp.value = s.exp.value.optimize(0, false);
                s.exp.value = checkGC(this.sc.value, s.exp.value);
                if (((s.exp.value.op.value & 0xFF) == 127))
                    this.setError();
                    return ;
            }
            this.result = s;
        }

        public  void visit(CompileStatement cs) {
            Ptr<DArray<Statement>> a = cs.flatten(this.sc.value);
            if (a == null)
                return ;
            Statement s = new CompoundStatement(cs.loc, a);
            this.result = statementSemantic(s, this.sc.value);
        }

        public  void visit(CompoundStatement cs) {
            {
                int i = 0;
                for (; (i < (cs.statements.get()).length.value);){
                    Statement s = (cs.statements.get()).get(i);
                    if (s != null)
                    {
                        Ptr<DArray<Statement>> flt = s.flatten(this.sc.value);
                        if (flt != null)
                        {
                            (cs.statements.get()).remove(i);
                            (cs.statements.get()).insert(i, flt);
                            continue;
                        }
                        s = statementSemantic(s, this.sc.value);
                        cs.statements.get().set(i, s);
                        if (s != null)
                        {
                            Ref<Statement> sentry = ref(null);
                            Ref<Statement> sexception = ref(null);
                            Ref<Statement> sfinally = ref(null);
                            cs.statements.get().set(i, s.scopeCode(this.sc.value, ptr(sentry), ptr(sexception), ptr(sfinally)));
                            if (sentry.value != null)
                            {
                                sentry.value = statementSemantic(sentry.value, this.sc.value);
                                (cs.statements.get()).insert(i, sentry.value);
                                i++;
                            }
                            if (sexception.value != null)
                                sexception.value = statementSemantic(sexception.value, this.sc.value);
                            if (sexception.value != null)
                            {
                                Function1<Slice<Statement>,Boolean> isEmpty = new Function1<Slice<Statement>,Boolean>(){
                                    public Boolean invoke(Slice<Statement> statements) {
                                        {
                                            Ref<Slice<Statement>> __r1589 = ref(statements.copy());
                                            IntRef __key1590 = ref(0);
                                            for (; (__key1590.value < __r1589.value.getLength());__key1590.value += 1) {
                                                Statement s = __r1589.value.get(__key1590.value);
                                                {
                                                    CompoundStatement cs = s.isCompoundStatement();
                                                    if ((cs) != null)
                                                    {
                                                        if (!isEmpty.invoke((cs.statements.get()).opSlice()))
                                                            return false;
                                                    }
                                                    else
                                                        return false;
                                                }
                                            }
                                        }
                                        return true;
                                    }
                                };
                                if ((sfinally.value == null) && isEmpty.invoke((cs.statements.get()).opSlice(i + 1, (cs.statements.get()).length.value)))
                                {
                                }
                                else
                                {
                                    Ptr<DArray<Statement>> a = refPtr(new DArray<Statement>());
                                    (a.get()).pushSlice((cs.statements.get()).opSlice(i + 1, (cs.statements.get()).length.value));
                                    (cs.statements.get()).setDim(i + 1);
                                    Statement _body = new CompoundStatement(Loc.initial.value, a);
                                    _body = new ScopeStatement(Loc.initial.value, _body, Loc.initial.value);
                                    Identifier id = Identifier.generateId(new BytePtr("__o"));
                                    Statement handler = new PeelStatement(sexception.value);
                                    if ((blockExit(sexception.value, (this.sc.value.get()).func.value, false) & BE.fallthru) != 0)
                                    {
                                        ThrowStatement ts = new ThrowStatement(Loc.initial.value, new IdentifierExp(Loc.initial.value, id));
                                        ts.internalThrow.value = true;
                                        handler = new CompoundStatement(Loc.initial.value, slice(new Statement[]{handler, ts}));
                                    }
                                    Ptr<DArray<Catch>> catches = refPtr(new DArray<Catch>());
                                    Catch ctch = new Catch(Loc.initial.value, getThrowable(), id, handler);
                                    ctch.internalCatch.value = true;
                                    (catches.get()).push(ctch);
                                    Statement st = new TryCatchStatement(Loc.initial.value, _body, catches);
                                    if (sfinally.value != null)
                                        st = new TryFinallyStatement(Loc.initial.value, st, sfinally.value);
                                    st = statementSemantic(st, this.sc.value);
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
                                    (a.get()).pushSlice((cs.statements.get()).opSlice(i + 1, (cs.statements.get()).length.value));
                                    (cs.statements.get()).setDim(i + 1);
                                    CompoundStatement _body = new CompoundStatement(Loc.initial.value, a);
                                    Statement stf = new TryFinallyStatement(Loc.initial.value, _body, sfinally.value);
                                    stf = statementSemantic(stf, this.sc.value);
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
            Function1<Ptr<DArray<Statement>>,Void> flatten = new Function1<Ptr<DArray<Statement>>,Void>(){
                public Void invoke(Ptr<DArray<Statement>> statements) {
                    {
                        IntRef i = ref(0);
                        for (; (i.value < (statements.get()).length.value);){
                            Ref<Statement> s = ref((statements.get()).get(i.value));
                            if (s.value != null)
                            {
                                {
                                    Ref<Ptr<DArray<Statement>>> flt = ref(s.value.flatten(sc.value));
                                    if ((flt.value) != null)
                                    {
                                        (statements.get()).remove(i.value);
                                        (statements.get()).insert(i.value, flt.value);
                                        continue;
                                    }
                                }
                            }
                            i.value += 1;
                        }
                    }
                }
            };
            flatten.invoke(cs.statements);
            {
                Slice<Statement> __r1591 = (cs.statements.get()).opSlice().copy();
                int __key1592 = 0;
                for (; (__key1592 < __r1591.getLength());__key1592 += 1) {
                    Statement s = __r1591.get(__key1592);
                    if (s == null)
                        continue;
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
            if (((cs.statements.get()).length.value == 1))
            {
                this.result = (cs.statements.get()).get(0);
                return ;
            }
            this.result = cs;
        }

        public  void visit(UnrolledLoopStatement uls) {
            Ptr<Scope> scd = (this.sc.value.get()).push();
            (scd.get()).sbreak = uls;
            (scd.get()).scontinue = uls;
            Statement serror = null;
            {
                Slice<Statement> __r1594 = (uls.statements.get()).opSlice().copy();
                int __key1593 = 0;
                for (; (__key1593 < __r1594.getLength());__key1593 += 1) {
                    Statement s = __r1594.get(__key1593);
                    int i = __key1593;
                    if (s != null)
                    {
                        s = statementSemantic(s, scd);
                        if ((s != null) && (serror == null))
                            serror = s.isErrorStatement();
                    }
                }
            }
            (scd.get()).pop();
            this.result = serror != null ? serror : uls;
        }

        public  void visit(ScopeStatement ss) {
            if (ss.statement.value != null)
            {
                ScopeDsymbol sym = new ScopeDsymbol();
                sym.parent.value = (this.sc.value.get()).scopesym.value;
                sym.endlinnum = ss.endloc.linnum;
                this.sc.value = (this.sc.value.get()).push(sym);
                Ptr<DArray<Statement>> a = ss.statement.value.flatten(this.sc.value);
                if (a != null)
                {
                    ss.statement.value = new CompoundStatement(ss.loc, a);
                }
                ss.statement.value = statementSemantic(ss.statement.value, this.sc.value);
                if (ss.statement.value != null)
                {
                    if (ss.statement.value.isErrorStatement() != null)
                    {
                        (this.sc.value.get()).pop();
                        this.result = ss.statement.value;
                        return ;
                    }
                    Ref<Statement> sentry = ref(null);
                    Ref<Statement> sexception = ref(null);
                    Ref<Statement> sfinally = ref(null);
                    ss.statement.value = ss.statement.value.scopeCode(this.sc.value, ptr(sentry), ptr(sexception), ptr(sfinally));
                    assert(sentry.value == null);
                    assert(sexception.value == null);
                    if (sfinally.value != null)
                    {
                        sfinally.value = statementSemantic(sfinally.value, this.sc.value);
                        ss.statement.value = new CompoundStatement(ss.loc, slice(new Statement[]{ss.statement.value, sfinally.value}));
                    }
                }
                (this.sc.value.get()).pop();
            }
            this.result = ss;
        }

        public  void visit(ForwardingStatement ss) {
            assert(ss.sym != null);
            {
                Ptr<Scope> csc = this.sc.value;
                for (; ss.sym.forward == null;csc = (csc.get()).enclosing.value){
                    assert(csc != null);
                    ss.sym.forward = (csc.get()).scopesym.value;
                }
            }
            this.sc.value = (this.sc.value.get()).push(ss.sym);
            (this.sc.value.get()).sbreak = ss;
            (this.sc.value.get()).scontinue = ss;
            ss.statement = statementSemantic(ss.statement, this.sc.value);
            this.sc.value = (this.sc.value.get()).pop();
            this.result = ss.statement;
        }

        public  void visit(WhileStatement ws) {
            Statement s = new ForStatement(ws.loc, null, ws.condition, null, ws._body, ws.endloc);
            s = statementSemantic(s, this.sc.value);
            this.result = s;
        }

        public  void visit(DoStatement ds) {
            boolean inLoopSave = (this.sc.value.get()).inLoop;
            (this.sc.value.get()).inLoop = true;
            if (ds._body.value != null)
                ds._body.value = semanticScope(ds._body.value, this.sc.value, ds, ds);
            (this.sc.value.get()).inLoop = inLoopSave;
            if (((ds.condition.value.op.value & 0xFF) == 28))
                ((DotIdExp)ds.condition.value).noderef = true;
            ds.condition.value = checkAssignmentAsCondition(ds.condition.value);
            ds.condition.value = expressionSemantic(ds.condition.value, this.sc.value);
            ds.condition.value = resolveProperties(this.sc.value, ds.condition.value);
            if (checkNonAssignmentArrayOp(ds.condition.value, false))
                ds.condition.value = new ErrorExp();
            ds.condition.value = ds.condition.value.optimize(0, false);
            ds.condition.value = checkGC(this.sc.value, ds.condition.value);
            ds.condition.value = ds.condition.value.toBoolean(this.sc.value);
            if (((ds.condition.value.op.value & 0xFF) == 127))
                this.setError();
                return ;
            if ((ds._body.value != null) && (ds._body.value.isErrorStatement() != null))
            {
                this.result = ds._body.value;
                return ;
            }
            this.result = ds;
        }

        public  void visit(ForStatement fs) {
            if (fs._init.value != null)
            {
                Ptr<DArray<Statement>> ainit = refPtr(new DArray<Statement>());
                (ainit.get()).push(fs._init.value);
                fs._init.value = null;
                (ainit.get()).push(fs);
                Statement s = new CompoundStatement(fs.loc, ainit);
                s = new ScopeStatement(fs.loc, s, fs.endloc);
                s = statementSemantic(s, this.sc.value);
                if (s.isErrorStatement() == null)
                {
                    {
                        LabelStatement ls = checkLabeledLoop(this.sc.value, fs);
                        if ((ls) != null)
                            ls.gotoTarget = fs;
                    }
                    fs.relatedLabeled = s;
                }
                this.result = s;
                return ;
            }
            assert((fs._init.value == null));
            ScopeDsymbol sym = new ScopeDsymbol();
            sym.parent.value = (this.sc.value.get()).scopesym.value;
            sym.endlinnum = fs.endloc.linnum;
            this.sc.value = (this.sc.value.get()).push(sym);
            (this.sc.value.get()).inLoop = true;
            if (fs.condition.value != null)
            {
                if (((fs.condition.value.op.value & 0xFF) == 28))
                    ((DotIdExp)fs.condition.value).noderef = true;
                fs.condition.value = checkAssignmentAsCondition(fs.condition.value);
                fs.condition.value = expressionSemantic(fs.condition.value, this.sc.value);
                fs.condition.value = resolveProperties(this.sc.value, fs.condition.value);
                if (checkNonAssignmentArrayOp(fs.condition.value, false))
                    fs.condition.value = new ErrorExp();
                fs.condition.value = fs.condition.value.optimize(0, false);
                fs.condition.value = checkGC(this.sc.value, fs.condition.value);
                fs.condition.value = fs.condition.value.toBoolean(this.sc.value);
            }
            if (fs.increment.value != null)
            {
                CommaExp.allow(fs.increment.value);
                fs.increment.value = expressionSemantic(fs.increment.value, this.sc.value);
                fs.increment.value = resolveProperties(this.sc.value, fs.increment.value);
                if (checkNonAssignmentArrayOp(fs.increment.value, false))
                    fs.increment.value = new ErrorExp();
                fs.increment.value = fs.increment.value.optimize(0, false);
                fs.increment.value = checkGC(this.sc.value, fs.increment.value);
            }
            (this.sc.value.get()).sbreak = fs;
            (this.sc.value.get()).scontinue = fs;
            if (fs._body.value != null)
                fs._body.value = semanticNoScope(fs._body.value, this.sc.value);
            (this.sc.value.get()).pop();
            if ((fs.condition.value != null) && ((fs.condition.value.op.value & 0xFF) == 127) || (fs.increment.value != null) && ((fs.increment.value.op.value & 0xFF) == 127) || (fs._body.value != null) && (fs._body.value.isErrorStatement() != null))
                this.setError();
                return ;
            this.result = fs;
        }

        // from template MakeTupleForeachRet!(0)

        // from template MakeTupleForeachRet!(1)


        // from template makeTupleForeach!(00)
        public  void makeTupleForeach00(ForeachStatement fs) {
            Function0<Void> returnEarly00 = new Function0<Void>(){
                public Void invoke() {
                    result = new ErrorStatement();
                    return null;
                }
            };
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length.value;
            boolean skipCheck = false;
            if ((dim < 1) || (dim > 2))
            {
                fs.error(new BytePtr("only one (value) or two (key,value) arguments for tuple `foreach`"));
                this.setError();
                returnEarly00.invoke();
                return ;
            }
            Type paramtype = (fs.parameters.get()).get(dim - 1).type.value;
            if (paramtype != null)
            {
                paramtype = typeSemantic(paramtype, loc, this.sc.value);
                if (((paramtype.ty.value & 0xFF) == ENUMTY.Terror))
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
            if (((fs.aggr.value.op.value & 0xFF) == 126))
            {
                te = (TupleExp)fs.aggr.value;
                n = (te.exps.value.get()).length.value;
            }
            else if (((fs.aggr.value.op.value & 0xFF) == 20))
            {
                n = Parameter.dim(tuple.arguments.value);
            }
            else
                throw new AssertionError("Unreachable code!");
            {
                int __key1597 = 0;
                int __limit1598 = n;
                for (; (__key1597 < __limit1598);__key1597 += 1) {
                    int j = __key1597;
                    int k = ((fs.op.value & 0xFF) == 201) ? j : n - 1 - j;
                    Expression e = null;
                    Type t = null;
                    if (te != null)
                        e = (te.exps.value.get()).get(k);
                    else
                        t = Parameter.getNth(tuple.arguments.value, k, null).type.value;
                    Parameter p = (fs.parameters.get()).get(0);
                    Ptr<DArray<Statement>> st = refPtr(new DArray<Statement>());
                    boolean skip = false;
                    if ((dim == 2))
                    {
                        if ((p.storageClass.value & 2109440L) != 0)
                        {
                            fs.error(new BytePtr("no storage class for key `%s`"), p.ident.value.toChars());
                            this.setError();
                            returnEarly00.invoke();
                            return ;
                        }
                        p.type.value = typeSemantic(p.type.value, loc, this.sc.value);
                        byte keyty = p.type.value.ty.value;
                        if (((keyty & 0xFF) != ENUMTY.Tint32) && ((keyty & 0xFF) != ENUMTY.Tuns32))
                        {
                            if (global.params.isLP64)
                            {
                                if (((keyty & 0xFF) != ENUMTY.Tint64) && ((keyty & 0xFF) != ENUMTY.Tuns64))
                                {
                                    fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, `long` or `ulong`, not `%s`"), p.type.value.toChars());
                                    this.setError();
                                    returnEarly00.invoke();
                                    return ;
                                }
                            }
                            else
                            {
                                fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, not `%s`"), p.type.value.toChars());
                                this.setError();
                                returnEarly00.invoke();
                                return ;
                            }
                        }
                        Initializer ie = new ExpInitializer(Loc.initial.value, new IntegerExp((long)k));
                        VarDeclaration var = new VarDeclaration(loc, p.type.value, p.ident.value, ie, 0L);
                        var.storage_class.value |= 8388608L;
                        (st.get()).push(new ExpStatement(loc, var));
                        p = (fs.parameters.get()).get(1);
                    }
                    Function5<Long,Type,Identifier,Expression,Type,Boolean> declareVariable00 = new Function5<Long,Type,Identifier,Expression,Type,Boolean>(){
                        public Boolean invoke(Long storageClass, Type type, Identifier ident, Expression e, Type t) {
                            if (((storageClass & 12288L) != 0) || ((storageClass & 2097152L) != 0) && (te == null))
                            {
                                fs.error(new BytePtr("no storage class for value `%s`"), ident.toChars());
                                setError();
                                return false;
                            }
                            Declaration var = null;
                            if (e != null)
                            {
                                Type tb = e.type.value.toBasetype();
                                Dsymbol ds = null;
                                if ((storageClass & 8388608L) == 0)
                                {
                                    if (((tb.ty.value & 0xFF) == ENUMTY.Tfunction) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray) || ((storageClass & 268435456L) != 0) && ((e.op.value & 0xFF) == 26))
                                        ds = ((VarExp)e).var.value;
                                    else if (((e.op.value & 0xFF) == 36))
                                        ds = ((TemplateExp)e).td.value;
                                    else if (((e.op.value & 0xFF) == 203))
                                        ds = ((ScopeExp)e).sds.value;
                                    else if (((e.op.value & 0xFF) == 161))
                                    {
                                        FuncExp fe = (FuncExp)e;
                                        ds = fe.td.value != null ? fe.td.value : fe.fd.value;
                                    }
                                    else if (((e.op.value & 0xFF) == 214))
                                        ds = ((OverExp)e).vars;
                                }
                                else if ((storageClass & 268435456L) != 0)
                                {
                                    fs.error(new BytePtr("`foreach` loop variable cannot be both `enum` and `alias`"));
                                    setError();
                                    return false;
                                }
                                if (ds != null)
                                {
                                    var = new AliasDeclaration(loc, ident, ds);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("symbol `%s` cannot be `ref`"), ds.toChars());
                                        setError();
                                        return false;
                                    }
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for symbol `%s`"), ds.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else if (((e.op.value & 0xFF) == 20))
                                {
                                    var = new AliasDeclaration(loc, ident, e.type.value);
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for type `%s`"), e.type.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else
                                {
                                    e = resolveProperties(sc.value, e);
                                    type = e.type.value;
                                    if (paramtype != null)
                                        type = paramtype;
                                    Initializer ie = new ExpInitializer(Loc.initial.value, e);
                                    VarDeclaration v = new VarDeclaration(loc, type, ident, ie, 0L);
                                    if ((storageClass & 2097152L) != 0)
                                        v.storage_class.value |= 2113536L;
                                    if (((storageClass & 8388608L) != 0) || (e.isConst() != 0) || ((e.op.value & 0xFF) == 121) || ((e.op.value & 0xFF) == 49) || ((e.op.value & 0xFF) == 47))
                                    {
                                        if ((v.storage_class.value & 2097152L) != 0)
                                        {
                                            fs.error(new BytePtr("constant value `%s` cannot be `ref`"), ie.toChars());
                                            setError();
                                            return false;
                                        }
                                        else
                                            v.storage_class.value |= 8388608L;
                                    }
                                    var = v;
                                }
                            }
                            else
                            {
                                var = new AliasDeclaration(loc, ident, t);
                                if (paramtype != null)
                                {
                                    fs.error(new BytePtr("cannot specify element type for symbol `%s`"), fs.toChars());
                                    setError();
                                    return false;
                                }
                            }
                            (st.get()).push(new ExpStatement(loc, var));
                            return true;
                        }
                    };
                    if (!declareVariable00.invoke(p.storageClass.value, p.type.value, p.ident.value, e, t))
                    {
                        returnEarly00.invoke();
                        return ;
                    }
                    if (fs._body.value != null)
                        (st.get()).push(fs._body.value.syntaxCopy());
                    Statement res = new CompoundStatement(loc, st);
                    res = new ScopeStatement(loc, res, fs.endloc);
                    (statements.get()).push(res);
                }
            }
            Statement res = new UnrolledLoopStatement(loc, statements);
            {
                LabelStatement ls = checkLabeledLoop(this.sc.value, fs);
                if ((ls) != null)
                    ls.gotoTarget = res;
            }
            if ((te != null) && (te.e0.value != null))
                res = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(te.e0.value.loc.value, te.e0.value), res}));
            this.result = res;
        }


        // from template makeTupleForeach!(10)
        public  void makeTupleForeach10(ForeachStatement fs, boolean _param_1) {
            Function0<Void> returnEarly10 = new Function0<Void>(){
                public Void invoke() {
                    result = new ErrorStatement();
                    return null;
                }
            };
            boolean needExpansion = _param_1;
            assert(this.sc.value != null);
            ScopeDsymbol previous = (this.sc.value.get()).scopesym.value;
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length.value;
            boolean skipCheck = needExpansion;
            if (!skipCheck && (dim < 1) || (dim > 2))
            {
                fs.error(new BytePtr("only one (value) or two (key,value) arguments for tuple `foreach`"));
                this.setError();
                returnEarly10.invoke();
                return ;
            }
            Type paramtype = (fs.parameters.get()).get(dim - 1).type.value;
            if (paramtype != null)
            {
                paramtype = typeSemantic(paramtype, loc, this.sc.value);
                if (((paramtype.ty.value & 0xFF) == ENUMTY.Terror))
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
            if (((fs.aggr.value.op.value & 0xFF) == 126))
            {
                te = (TupleExp)fs.aggr.value;
                n = (te.exps.value.get()).length.value;
            }
            else if (((fs.aggr.value.op.value & 0xFF) == 20))
            {
                n = Parameter.dim(tuple.arguments.value);
            }
            else
                throw new AssertionError("Unreachable code!");
            {
                int __key1575 = 0;
                int __limit1576 = n;
                for (; (__key1575 < __limit1576);__key1575 += 1) {
                    int j = __key1575;
                    int k = ((fs.op.value & 0xFF) == 201) ? j : n - 1 - j;
                    Expression e = null;
                    Type t = null;
                    if (te != null)
                        e = (te.exps.value.get()).get(k);
                    else
                        t = Parameter.getNth(tuple.arguments.value, k, null).type.value;
                    Parameter p = (fs.parameters.get()).get(0);
                    Ptr<DArray<Statement>> st = refPtr(new DArray<Statement>());
                    boolean skip = needExpansion;
                    if (!skip && (dim == 2))
                    {
                        if ((p.storageClass.value & 2109440L) != 0)
                        {
                            fs.error(new BytePtr("no storage class for key `%s`"), p.ident.value.toChars());
                            this.setError();
                            returnEarly10.invoke();
                            return ;
                        }
                        if (p.type.value == null)
                        {
                            p.type.value = Type.tsize_t.value;
                        }
                        p.type.value = typeSemantic(p.type.value, loc, this.sc.value);
                        byte keyty = p.type.value.ty.value;
                        if (((keyty & 0xFF) != ENUMTY.Tint32) && ((keyty & 0xFF) != ENUMTY.Tuns32))
                        {
                            if (global.params.isLP64)
                            {
                                if (((keyty & 0xFF) != ENUMTY.Tint64) && ((keyty & 0xFF) != ENUMTY.Tuns64))
                                {
                                    fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, `long` or `ulong`, not `%s`"), p.type.value.toChars());
                                    this.setError();
                                    returnEarly10.invoke();
                                    return ;
                                }
                            }
                            else
                            {
                                fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, not `%s`"), p.type.value.toChars());
                                this.setError();
                                returnEarly10.invoke();
                                return ;
                            }
                        }
                        Initializer ie = new ExpInitializer(Loc.initial.value, new IntegerExp((long)k));
                        VarDeclaration var = new VarDeclaration(loc, p.type.value, p.ident.value, ie, 0L);
                        var.storage_class.value |= 8388608L;
                        var.storage_class.value |= 2251799813685248L;
                        (st.get()).push(new ExpStatement(loc, var));
                        p = (fs.parameters.get()).get(1);
                    }
                    Function5<Long,Type,Identifier,Expression,Type,Boolean> declareVariable10 = new Function5<Long,Type,Identifier,Expression,Type,Boolean>(){
                        public Boolean invoke(Long storageClass, Type type, Identifier ident, Expression e, Type t) {
                            if (((storageClass & 12288L) != 0) || ((storageClass & 2097152L) != 0) && (te == null))
                            {
                                fs.error(new BytePtr("no storage class for value `%s`"), ident.toChars());
                                setError();
                                return false;
                            }
                            Declaration var = null;
                            if (e != null)
                            {
                                Type tb = e.type.value.toBasetype();
                                Dsymbol ds = null;
                                if ((storageClass & 8388608L) == 0)
                                {
                                    if (((e.op.value & 0xFF) == 26))
                                        ds = ((VarExp)e).var.value;
                                    else if (((e.op.value & 0xFF) == 36))
                                        ds = ((TemplateExp)e).td.value;
                                    else if (((e.op.value & 0xFF) == 203))
                                        ds = ((ScopeExp)e).sds.value;
                                    else if (((e.op.value & 0xFF) == 161))
                                    {
                                        FuncExp fe = (FuncExp)e;
                                        ds = fe.td.value != null ? fe.td.value : fe.fd.value;
                                    }
                                    else if (((e.op.value & 0xFF) == 214))
                                        ds = ((OverExp)e).vars;
                                }
                                else if ((storageClass & 268435456L) != 0)
                                {
                                    fs.error(new BytePtr("`foreach` loop variable cannot be both `enum` and `alias`"));
                                    setError();
                                    return false;
                                }
                                if (ds != null)
                                {
                                    var = new AliasDeclaration(loc, ident, ds);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("symbol `%s` cannot be `ref`"), ds.toChars());
                                        setError();
                                        return false;
                                    }
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for symbol `%s`"), ds.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else if (((e.op.value & 0xFF) == 20))
                                {
                                    var = new AliasDeclaration(loc, ident, e.type.value);
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for type `%s`"), e.type.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else
                                {
                                    e = resolveProperties(sc.value, e);
                                    type = e.type.value;
                                    if (paramtype != null)
                                        type = paramtype;
                                    Initializer ie = new ExpInitializer(Loc.initial.value, e);
                                    VarDeclaration v = new VarDeclaration(loc, type, ident, ie, 0L);
                                    if ((storageClass & 2097152L) != 0)
                                        v.storage_class.value |= 2113536L;
                                    if (true)
                                    {
                                        if ((v.storage_class.value & 2097152L) != 0)
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
                                            v.storage_class.value |= 8388608L;
                                    }
                                    var = v;
                                }
                            }
                            else
                            {
                                var = new AliasDeclaration(loc, ident, t);
                                if (paramtype != null)
                                {
                                    fs.error(new BytePtr("cannot specify element type for symbol `%s`"), fs.toChars());
                                    setError();
                                    return false;
                                }
                            }
                            var.storage_class.value |= 2251799813685248L;
                            (st.get()).push(new ExpStatement(loc, var));
                            return true;
                        }
                    };
                    if (!needExpansion)
                    {
                        if (!declareVariable10.invoke(p.storageClass.value, p.type.value, p.ident.value, e, t))
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
                        access = expressionSemantic(access, this.sc.value);
                        if (tuple == null)
                            returnEarly10.invoke();
                            return ;
                        {
                            int __key1577 = 0;
                            int __limit1578 = dim;
                            for (; (__key1577 < __limit1578);__key1577 += 1) {
                                int l = __key1577;
                                Parameter cp = (fs.parameters.get()).get(l);
                                Expression init_ = new IndexExp(loc, access, new IntegerExp(loc, (long)l, Type.tsize_t.value));
                                init_ = expressionSemantic(init_, this.sc.value);
                                assert(init_.type.value != null);
                                declareVariable10.invoke(p.storageClass.value, init_.type.value, cp.ident.value, init_, null);
                            }
                        }
                    }
                    if (fs._body.value != null)
                        (st.get()).push(fs._body.value.syntaxCopy());
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
            Function0<Object> returnEarly11 = new Function0<Object>(){
                public Object invoke() {
                    return null;
                }
            };
            Ptr<DArray<Dsymbol>> dbody = _param_1;
            boolean needExpansion = _param_2;
            assert(this.sc.value != null);
            ScopeDsymbol previous = (this.sc.value.get()).scopesym.value;
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length.value;
            boolean skipCheck = needExpansion;
            if (!skipCheck && (dim < 1) || (dim > 2))
            {
                fs.error(new BytePtr("only one (value) or two (key,value) arguments for tuple `foreach`"));
                this.setError();
                return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
            }
            Type paramtype = (fs.parameters.get()).get(dim - 1).type.value;
            if (paramtype != null)
            {
                paramtype = typeSemantic(paramtype, loc, this.sc.value);
                if (((paramtype.ty.value & 0xFF) == ENUMTY.Terror))
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
            if (((fs.aggr.value.op.value & 0xFF) == 126))
            {
                te = (TupleExp)fs.aggr.value;
                n = (te.exps.value.get()).length.value;
            }
            else if (((fs.aggr.value.op.value & 0xFF) == 20))
            {
                n = Parameter.dim(tuple.arguments.value);
            }
            else
                throw new AssertionError("Unreachable code!");
            {
                int __key715 = 0;
                int __limit716 = n;
                for (; (__key715 < __limit716);__key715 += 1) {
                    int j = __key715;
                    int k = ((fs.op.value & 0xFF) == 201) ? j : n - 1 - j;
                    Expression e = null;
                    Type t = null;
                    if (te != null)
                        e = (te.exps.value.get()).get(k);
                    else
                        t = Parameter.getNth(tuple.arguments.value, k, null).type.value;
                    Parameter p = (fs.parameters.get()).get(0);
                    Ptr<DArray<Dsymbol>> st = refPtr(new DArray<Dsymbol>());
                    boolean skip = needExpansion;
                    if (!skip && (dim == 2))
                    {
                        if ((p.storageClass.value & 2109440L) != 0)
                        {
                            fs.error(new BytePtr("no storage class for key `%s`"), p.ident.value.toChars());
                            this.setError();
                            return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                        }
                        if (p.type.value == null)
                        {
                            p.type.value = Type.tsize_t.value;
                        }
                        p.type.value = typeSemantic(p.type.value, loc, this.sc.value);
                        byte keyty = p.type.value.ty.value;
                        if (((keyty & 0xFF) != ENUMTY.Tint32) && ((keyty & 0xFF) != ENUMTY.Tuns32))
                        {
                            if (global.params.isLP64)
                            {
                                if (((keyty & 0xFF) != ENUMTY.Tint64) && ((keyty & 0xFF) != ENUMTY.Tuns64))
                                {
                                    fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, `long` or `ulong`, not `%s`"), p.type.value.toChars());
                                    this.setError();
                                    return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                                }
                            }
                            else
                            {
                                fs.error(new BytePtr("`foreach`: key type must be `int` or `uint`, not `%s`"), p.type.value.toChars());
                                this.setError();
                                return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                            }
                        }
                        Initializer ie = new ExpInitializer(Loc.initial.value, new IntegerExp((long)k));
                        VarDeclaration var = new VarDeclaration(loc, p.type.value, p.ident.value, ie, 0L);
                        var.storage_class.value |= 8388608L;
                        var.storage_class.value |= 2251799813685248L;
                        (st.get()).push(var);
                        p = (fs.parameters.get()).get(1);
                    }
                    Function5<Long,Type,Identifier,Expression,Type,Boolean> declareVariable11 = new Function5<Long,Type,Identifier,Expression,Type,Boolean>(){
                        public Boolean invoke(Long storageClass, Type type, Identifier ident, Expression e, Type t) {
                            if (((storageClass & 12288L) != 0) || ((storageClass & 2097152L) != 0) && (te == null))
                            {
                                fs.error(new BytePtr("no storage class for value `%s`"), ident.toChars());
                                setError();
                                return false;
                            }
                            Declaration var = null;
                            if (e != null)
                            {
                                Type tb = e.type.value.toBasetype();
                                Dsymbol ds = null;
                                if ((storageClass & 8388608L) == 0)
                                {
                                    if (((e.op.value & 0xFF) == 26))
                                        ds = ((VarExp)e).var.value;
                                    else if (((e.op.value & 0xFF) == 36))
                                        ds = ((TemplateExp)e).td.value;
                                    else if (((e.op.value & 0xFF) == 203))
                                        ds = ((ScopeExp)e).sds.value;
                                    else if (((e.op.value & 0xFF) == 161))
                                    {
                                        FuncExp fe = (FuncExp)e;
                                        ds = fe.td.value != null ? fe.td.value : fe.fd.value;
                                    }
                                    else if (((e.op.value & 0xFF) == 214))
                                        ds = ((OverExp)e).vars;
                                }
                                else if ((storageClass & 268435456L) != 0)
                                {
                                    fs.error(new BytePtr("`foreach` loop variable cannot be both `enum` and `alias`"));
                                    setError();
                                    return false;
                                }
                                if (ds != null)
                                {
                                    var = new AliasDeclaration(loc, ident, ds);
                                    if ((storageClass & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("symbol `%s` cannot be `ref`"), ds.toChars());
                                        setError();
                                        return false;
                                    }
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for symbol `%s`"), ds.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else if (((e.op.value & 0xFF) == 20))
                                {
                                    var = new AliasDeclaration(loc, ident, e.type.value);
                                    if (paramtype != null)
                                    {
                                        fs.error(new BytePtr("cannot specify element type for type `%s`"), e.type.value.toChars());
                                        setError();
                                        return false;
                                    }
                                }
                                else
                                {
                                    e = resolveProperties(sc.value, e);
                                    type = e.type.value;
                                    if (paramtype != null)
                                        type = paramtype;
                                    Initializer ie = new ExpInitializer(Loc.initial.value, e);
                                    VarDeclaration v = new VarDeclaration(loc, type, ident, ie, 0L);
                                    if ((storageClass & 2097152L) != 0)
                                        v.storage_class.value |= 2113536L;
                                    if (true)
                                    {
                                        if ((v.storage_class.value & 2097152L) != 0)
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
                                            v.storage_class.value |= 8388608L;
                                    }
                                    var = v;
                                }
                            }
                            else
                            {
                                var = new AliasDeclaration(loc, ident, t);
                                if (paramtype != null)
                                {
                                    fs.error(new BytePtr("cannot specify element type for symbol `%s`"), fs.toChars());
                                    setError();
                                    return false;
                                }
                            }
                            var.storage_class.value |= 2251799813685248L;
                            (st.get()).push(var);
                            return true;
                        }
                    };
                    if (!needExpansion)
                    {
                        if (!declareVariable11.invoke(p.storageClass.value, p.type.value, p.ident.value, e, t))
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
                        access = expressionSemantic(access, this.sc.value);
                        if (tuple == null)
                            return (Ptr<DArray<Dsymbol>>)returnEarly11.invoke();
                        {
                            int __key717 = 0;
                            int __limit718 = dim;
                            for (; (__key717 < __limit718);__key717 += 1) {
                                int l = __key717;
                                Parameter cp = (fs.parameters.get()).get(l);
                                Expression init_ = new IndexExp(loc, access, new IntegerExp(loc, (long)l, Type.tsize_t.value));
                                init_ = expressionSemantic(init_, this.sc.value);
                                assert(init_.type.value != null);
                                declareVariable11.invoke(p.storageClass.value, init_.type.value, cp.ident.value, init_, null);
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
            Function1<ForeachStatement,Boolean> checkForArgTypes = new Function1<ForeachStatement,Boolean>(){
                public Boolean invoke(ForeachStatement fs) {
                    Ref<Boolean> result = ref(false);
                    {
                        Ref<Slice<Parameter>> __r1595 = ref((fs.parameters.get()).opSlice().copy());
                        IntRef __key1596 = ref(0);
                        for (; (__key1596.value < __r1595.value.getLength());__key1596.value += 1) {
                            Parameter p = __r1595.value.get(__key1596.value);
                            if (p.type.value == null)
                            {
                                fs.error(new BytePtr("cannot infer type for `foreach` variable `%s`, perhaps set it explicitly"), p.ident.value.toChars());
                                p.type.value = Type.terror.value;
                                result.value = true;
                            }
                        }
                    }
                    return result.value;
                }
            };
            Loc loc = fs.loc.copy();
            int dim = (fs.parameters.get()).length.value;
            TypeAArray taa = null;
            Type tn = null;
            Type tnv = null;
            fs.func.value = (this.sc.value.get()).func.value;
            if (fs.func.value.fes.value != null)
                fs.func.value = fs.func.value.fes.value.func.value;
            VarDeclaration vinit = null;
            fs.aggr.value = expressionSemantic(fs.aggr.value, this.sc.value);
            fs.aggr.value = resolveProperties(this.sc.value, fs.aggr.value);
            fs.aggr.value = fs.aggr.value.optimize(0, false);
            if (((fs.aggr.value.op.value & 0xFF) == 127))
                this.setError();
                return ;
            Expression oaggr = fs.aggr.value;
            if ((fs.aggr.value.type.value != null) && ((fs.aggr.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)fs.aggr.value.type.value.toBasetype()).sym.value.dtor.value != null) && ((fs.aggr.value.op.value & 0xFF) != 20) && !fs.aggr.value.isLvalue())
            {
                vinit = copyToTemp(2199023255552L, new BytePtr("__aggr"), fs.aggr.value);
                vinit.endlinnum = fs.endloc.linnum;
                dsymbolSemantic(vinit, this.sc.value);
                fs.aggr.value = new VarExp(fs.aggr.value.loc.value, vinit, true);
            }
            Ref<Dsymbol> sapply = ref(null);
            if (!inferForeachAggregate(this.sc.value, (fs.op.value & 0xFF) == 201, aggr, sapply))
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
            if (!inferApplyArgTypes(fs, this.sc.value, sapply))
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
                                if (((fparam.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((fparam.type.value.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((fparam.type.value.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
                                {
                                    TypeFunction tf = (TypeFunction)fparam.type.value.nextOf();
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
                    fs.error(new BytePtr("cannot uniquely infer `foreach` argument types"));
                this.setError();
                return ;
            }
            Type tab = fs.aggr.value.type.value.toBasetype();
            if (((tab.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                this.makeTupleForeach00(fs);
                if (vinit != null)
                    this.result = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, vinit), this.result}));
                this.result = statementSemantic(this.result, this.sc.value);
                return ;
            }
            ScopeDsymbol sym = new ScopeDsymbol();
            sym.parent.value = (this.sc.value.get()).scopesym.value;
            sym.endlinnum = fs.endloc.linnum;
            Ptr<Scope> sc2 = (this.sc.value.get()).push(sym);
            (sc2.get()).inLoop = true;
            {
                Slice<Parameter> __r1599 = (fs.parameters.get()).opSlice().copy();
                int __key1600 = 0;
                for (; (__key1600 < __r1599.getLength());__key1600 += 1) {
                    Parameter p = __r1599.get(__key1600);
                    if ((p.storageClass.value & 8388608L) != 0)
                    {
                        fs.error(new BytePtr("cannot declare `enum` loop variables for non-unrolled foreach"));
                    }
                    if ((p.storageClass.value & 268435456L) != 0)
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
                    switch (__dispatch0 != 0 ? __dispatch0 : (tab.ty.value & 0xFF))
                    {
                        case 0:
                        case 1:
                            if (checkForArgTypes.invoke(fs))
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            if ((dim < 1) || (dim > 2))
                            {
                                fs.error(new BytePtr("only one or two arguments for array `foreach`"));
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            }
                            {
                                int __key1601 = 0;
                                int __limit1602 = dim;
                                for (; (__key1601 < __limit1602);__key1601 += 1) {
                                    int i = __key1601;
                                    Parameter p = (fs.parameters.get()).get(i);
                                    p.type.value = typeSemantic(p.type.value, loc, sc2);
                                    p.type.value = p.type.value.addStorageClass(p.storageClass.value);
                                }
                            }
                            tn = tab.nextOf().toBasetype();
                            if ((dim == 2))
                            {
                                Type tindex = (fs.parameters.get()).get(0).type.value;
                                if (!tindex.isintegral())
                                {
                                    fs.error(new BytePtr("foreach: key cannot be of non-integral type `%s`"), tindex.toChars());
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                Type tv = (fs.parameters.get()).get(1).type.value.toBasetype();
                                if (((tab.ty.value & 0xFF) == ENUMTY.Tarray) || ((tn.ty.value & 0xFF) != (tv.ty.value & 0xFF)) && ((tn.ty.value & 0xFF) == ENUMTY.Tchar) || ((tn.ty.value & 0xFF) == ENUMTY.Twchar) || ((tn.ty.value & 0xFF) == ENUMTY.Tdchar) && ((tv.ty.value & 0xFF) == ENUMTY.Tchar) || ((tv.ty.value & 0xFF) == ENUMTY.Twchar) || ((tv.ty.value & 0xFF) == ENUMTY.Tdchar) && (Type.tsize_t.value.implicitConvTo(tindex) == 0))
                                {
                                    fs.deprecation(new BytePtr("foreach: loop index implicitly converted from `size_t` to `%s`"), tindex.toChars());
                                }
                            }
                            if (((tn.ty.value & 0xFF) == ENUMTY.Tchar) || ((tn.ty.value & 0xFF) == ENUMTY.Twchar) || ((tn.ty.value & 0xFF) == ENUMTY.Tdchar))
                            {
                                int i_1 = (dim == 1) ? 0 : 1;
                                Parameter p_1 = (fs.parameters.get()).get(i_1);
                                tnv = p_1.type.value.toBasetype();
                                if (((tnv.ty.value & 0xFF) != (tn.ty.value & 0xFF)) && ((tnv.ty.value & 0xFF) == ENUMTY.Tchar) || ((tnv.ty.value & 0xFF) == ENUMTY.Twchar) || ((tnv.ty.value & 0xFF) == ENUMTY.Tdchar))
                                {
                                    if ((p_1.storageClass.value & 2097152L) != 0)
                                    {
                                        fs.error(new BytePtr("`foreach`: value of UTF conversion cannot be `ref`"));
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    if ((dim == 2))
                                    {
                                        p_1 = (fs.parameters.get()).get(0);
                                        if ((p_1.storageClass.value & 2097152L) != 0)
                                        {
                                            fs.error(new BytePtr("`foreach`: key cannot be `ref`"));
                                            /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                        }
                                    }
                                    /*goto Lapply*/{ __dispatch0 = -1; continue dispatched_0; }
                                }
                            }
                            {
                                int __key1603 = 0;
                                int __limit1604 = dim;
                            L_outer1:
                                for (; (__key1603 < __limit1604);__key1603 += 1) {
                                    int i_2 = __key1603;
                                    Parameter p_2 = (fs.parameters.get()).get(i_2);
                                    VarDeclaration var = null;
                                    if ((dim == 2) && (i_2 == 0))
                                    {
                                        var = new VarDeclaration(loc, p_2.type.value.mutableOf(), Identifier.generateId(new BytePtr("__key")), null, 0L);
                                        var.storage_class.value |= 1099511644160L;
                                        if ((var.storage_class.value & 2101248L) != 0)
                                            var.storage_class.value |= 16777216L;
                                        fs.key = var;
                                        if ((p_2.storageClass.value & 2097152L) != 0)
                                        {
                                            if ((var.type.value.constConv(p_2.type.value) <= MATCH.nomatch))
                                            {
                                                fs.error(new BytePtr("key type mismatch, `%s` to `ref %s`"), var.type.value.toChars(), p_2.type.value.toChars());
                                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                            }
                                        }
                                        if (((tab.ty.value & 0xFF) == ENUMTY.Tsarray))
                                        {
                                            TypeSArray ta = (TypeSArray)tab;
                                            IntRange dimrange = getIntRange(ta.dim.value).copy();
                                            if (!IntRange.fromType(var.type.value).contains(dimrange))
                                            {
                                                fs.error(new BytePtr("index type `%s` cannot cover index range 0..%llu"), p_2.type.value.toChars(), ta.dim.value.toInteger());
                                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                            }
                                            fs.key.range.value = refPtr(new IntRange(new SignExtendedNumber(0L, false), dimrange.imax));
                                        }
                                    }
                                    else
                                    {
                                        var = new VarDeclaration(loc, p_2.type.value, p_2.ident.value, null, 0L);
                                        var.storage_class.value |= 16384L;
                                        var.storage_class.value |= p_2.storageClass.value & 2687506436L;
                                        if ((var.storage_class.value & 2101248L) != 0)
                                            var.storage_class.value |= 16777216L;
                                        fs.value = var;
                                        if ((var.storage_class.value & 2097152L) != 0)
                                        {
                                            if ((fs.aggr.value.checkModifiable(sc2, 1) == Modifiable.initialization))
                                                var.storage_class.value |= 131072L;
                                            Type t = tab.nextOf();
                                            if ((t.constConv(p_2.type.value) <= MATCH.nomatch))
                                            {
                                                fs.error(new BytePtr("argument type mismatch, `%s` to `ref %s`"), t.toChars(), p_2.type.value.toChars());
                                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                            }
                                        }
                                    }
                                }
                            }
                            Identifier id = Identifier.generateId(new BytePtr("__r"));
                            ExpInitializer ie = new ExpInitializer(loc, new SliceExp(loc, fs.aggr.value, null, null));
                            VarDeclaration tmp = null;
                            if (((fs.aggr.value.op.value & 0xFF) == 47) && (((fs.parameters.get()).get(dim - 1).storageClass.value & 2097152L) == 0))
                            {
                                ArrayLiteralExp ale = (ArrayLiteralExp)fs.aggr.value;
                                int edim = ale.elements.value != null ? (ale.elements.value.get()).length.value : 0;
                                Type telem = (fs.parameters.get()).get(dim - 1).type.value;
                                fs.aggr.value = fs.aggr.value.implicitCastTo(this.sc.value, telem.sarrayOf((long)edim));
                                if (((fs.aggr.value.op.value & 0xFF) == 127))
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                tmp = new VarDeclaration(loc, fs.aggr.value.type.value, id, ie, 0L);
                            }
                            else
                                tmp = new VarDeclaration(loc, tab.nextOf().arrayOf(), id, ie, 0L);
                            tmp.storage_class.value |= 1099511627776L;
                            Expression tmp_length = new DotIdExp(loc, new VarExp(loc, tmp, true), Id.length.value);
                            if (fs.key == null)
                            {
                                Identifier idkey = Identifier.generateId(new BytePtr("__key"));
                                fs.key = new VarDeclaration(loc, Type.tsize_t.value, idkey, null, 0L);
                                fs.key.storage_class.value |= 1099511627776L;
                            }
                            else if (((fs.key.type.value.ty.value & 0xFF) != (Type.tsize_t.value.ty.value & 0xFF)))
                            {
                                tmp_length = new CastExp(loc, tmp_length, fs.key.type.value);
                            }
                            if (((fs.op.value & 0xFF) == 202))
                                fs.key._init.value = new ExpInitializer(loc, tmp_length);
                            else
                                fs.key._init.value = new ExpInitializer(loc, new IntegerExp(loc, 0L, fs.key.type.value));
                            Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
                            if (vinit != null)
                                (cs.get()).push(new ExpStatement(loc, vinit));
                            (cs.get()).push(new ExpStatement(loc, tmp));
                            (cs.get()).push(new ExpStatement(loc, fs.key));
                            Statement forinit = new CompoundDeclarationStatement(loc, cs);
                            Expression cond = null;
                            if (((fs.op.value & 0xFF) == 202))
                            {
                                cond = new PostExp(TOK.minusMinus, loc, new VarExp(loc, fs.key, true));
                            }
                            else
                            {
                                cond = new CmpExp(TOK.lessThan, loc, new VarExp(loc, fs.key, true), tmp_length);
                            }
                            Expression increment = null;
                            if (((fs.op.value & 0xFF) == 201))
                            {
                                increment = new AddAssignExp(loc, new VarExp(loc, fs.key, true), new IntegerExp(loc, 1L, fs.key.type.value));
                            }
                            IndexExp indexExp = new IndexExp(loc, new VarExp(loc, tmp, true), new VarExp(loc, fs.key, true));
                            indexExp.indexIsInBounds = true;
                            fs.value._init.value = new ExpInitializer(loc, indexExp);
                            Statement ds = new ExpStatement(loc, fs.value);
                            if ((dim == 2))
                            {
                                Parameter p_3 = (fs.parameters.get()).get(0);
                                if (((p_3.storageClass.value & 2097152L) != 0) && p_3.type.value.equals(fs.key.type.value))
                                {
                                    fs.key.range.value = null;
                                    AliasDeclaration v = new AliasDeclaration(loc, p_3.ident.value, fs.key);
                                    fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v), fs._body.value}));
                                }
                                else
                                {
                                    ExpInitializer ei = new ExpInitializer(loc, new IdentifierExp(loc, fs.key.ident.value));
                                    VarDeclaration v_1 = new VarDeclaration(loc, p_3.type.value, p_3.ident.value, ei, 0L);
                                    v_1.storage_class.value |= 16384L | p_3.storageClass.value & 2097152L;
                                    fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v_1), fs._body.value}));
                                    if ((fs.key.range.value != null) && !p_3.type.value.isMutable())
                                    {
                                        v_1.range.value = refPtr(new IntRange((fs.key.range.value.get()).imin, (fs.key.range.value.get()).imax.opBinary_minus(new SignExtendedNumber(1L, false))));
                                    }
                                }
                            }
                            fs._body.value = new CompoundStatement(loc, slice(new Statement[]{ds, fs._body.value}));
                            s = new ForStatement(loc, forinit, cond, increment, fs._body.value, fs.endloc);
                            {
                                LabelStatement ls = checkLabeledLoop(this.sc.value, fs);
                                if ((ls) != null)
                                    ls.gotoTarget = s;
                            }
                            s = statementSemantic(s, sc2);
                            break;
                        case 2:
                            if (((fs.op.value & 0xFF) == 202))
                                fs.warning(new BytePtr("cannot use `foreach_reverse` with an associative array"));
                            if (checkForArgTypes.invoke(fs))
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
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
                                /*goto Lapply*/{ __dispatch0 = -1; continue dispatched_0; }
                            {
                                AggregateDeclaration ad = ((tab.ty.value & 0xFF) == ENUMTY.Tclass) ? ((TypeClass)tab).sym.value : ((TypeStruct)tab).sym.value;
                                Identifier idfront = null;
                                Identifier idpopFront = null;
                                if (((fs.op.value & 0xFF) == 201))
                                {
                                    idfront = Id.Ffront;
                                    idpopFront = Id.FpopFront;
                                }
                                else
                                {
                                    idfront = Id.Fback;
                                    idpopFront = Id.FpopBack;
                                }
                                Dsymbol sfront = ad.search(Loc.initial.value, idfront, 8);
                                if (sfront == null)
                                    /*goto Lapply*/{ __dispatch0 = -1; continue dispatched_0; }
                                VarDeclaration r = null;
                                Statement _init = null;
                                if ((vinit != null) && ((fs.aggr.value.op.value & 0xFF) == 26) && (pequals(((VarExp)fs.aggr.value).var.value, vinit)))
                                {
                                    r = vinit;
                                    _init = new ExpStatement(loc, vinit);
                                }
                                else
                                {
                                    r = copyToTemp(0L, new BytePtr("__r"), fs.aggr.value);
                                    dsymbolSemantic(r, this.sc.value);
                                    _init = new ExpStatement(loc, r);
                                    if (vinit != null)
                                        _init = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, vinit), _init}));
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
                                            /*goto Lrangeerr*/{ __dispatch0 = -2; continue dispatched_0; }
                                        tfront = fd.type.value;
                                    }
                                    else {
                                        TemplateDeclaration td = sfront.isTemplateDeclaration();
                                        if ((td) != null)
                                        {
                                            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                                            try {
                                                {
                                                    FuncDeclaration f = resolveFuncCall(loc, this.sc.value, td, null, tab, ptr(a), FuncResolveFlag.quiet);
                                                    if ((f) != null)
                                                        tfront = f.type.value;
                                                }
                                            }
                                            finally {
                                            }
                                        }
                                        else {
                                            Declaration d = sfront.isDeclaration();
                                            if ((d) != null)
                                            {
                                                tfront = d.type.value;
                                            }
                                        }
                                    }
                                }
                                if ((tfront == null) || ((tfront.ty.value & 0xFF) == ENUMTY.Terror))
                                    /*goto Lrangeerr*/{ __dispatch0 = -2; continue dispatched_0; }
                                if (((tfront.toBasetype().ty.value & 0xFF) == ENUMTY.Tfunction))
                                {
                                    TypeFunction ftt = (TypeFunction)tfront.toBasetype();
                                    tfront = tfront.toBasetype().nextOf();
                                    if (!ftt.isref.value)
                                    {
                                        if (tfront.needsDestruction())
                                            ignoreRef = true;
                                    }
                                }
                                if (((tfront.ty.value & 0xFF) == ENUMTY.Tvoid))
                                {
                                    fs.error(new BytePtr("`%s.front` is `void` and has no value"), oaggr.toChars());
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                if ((dim == 1))
                                {
                                    Parameter p_4 = (fs.parameters.get()).get(0);
                                    VarDeclaration ve = new VarDeclaration(loc, p_4.type.value, p_4.ident.value, new ExpInitializer(loc, einit), 0L);
                                    ve.storage_class.value |= 16384L;
                                    ve.storage_class.value |= p_4.storageClass.value & 2687506436L;
                                    if (ignoreRef)
                                        ve.storage_class.value &= -2097153L;
                                    makeargs = new ExpStatement(loc, ve);
                                }
                                else
                                {
                                    VarDeclaration vd = copyToTemp(2097152L, new BytePtr("__front"), einit);
                                    dsymbolSemantic(vd, this.sc.value);
                                    makeargs = new ExpStatement(loc, vd);
                                    tfront = tfront.substWildTo((tab.mod.value & 0xFF));
                                    Expression ve_1 = new VarExp(loc, vd, true);
                                    ve_1.type.value = tfront;
                                    Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                                    (exps.get()).push(ve_1);
                                    int pos = 0;
                                    for (; ((exps.get()).length.value < dim);){
                                        pos = expandAliasThisTuples(exps, pos);
                                        if ((pos == -1))
                                            break;
                                    }
                                    if (((exps.get()).length.value != dim))
                                    {
                                        BytePtr plural = pcopy(((exps.get()).length.value > 1) ? new BytePtr("s") : new BytePtr(""));
                                        fs.error(new BytePtr("cannot infer argument types, expected %d argument%s, not %d"), (exps.get()).length.value, plural, dim);
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                    {
                                        int __key1605 = 0;
                                        int __limit1606 = dim;
                                    L_outer2:
                                        for (; (__key1605 < __limit1606);__key1605 += 1) {
                                            int i_3 = __key1605;
                                            Parameter p_5 = (fs.parameters.get()).get(i_3);
                                            Expression exp = (exps.get()).get(i_3);
                                            if (p_5.type.value == null)
                                                p_5.type.value = exp.type.value;
                                            long sc = p_5.storageClass.value;
                                            if (ignoreRef)
                                                sc &= -2097153L;
                                            p_5.type.value = typeSemantic(p_5.type.value.addStorageClass(sc), loc, sc2);
                                            if (exp.implicitConvTo(p_5.type.value) == 0)
                                                /*goto Lrangeerr*/{ __dispatch0 = -2; continue dispatched_0; }
                                            VarDeclaration var_1 = new VarDeclaration(loc, p_5.type.value, p_5.ident.value, new ExpInitializer(loc, exp), 0L);
                                            var_1.storage_class.value |= 68721590272L;
                                            makeargs = new CompoundStatement(loc, slice(new Statement[]{makeargs, new ExpStatement(loc, var_1)}));
                                        }
                                    }
                                }
                                forbody = new CompoundStatement(loc, slice(new Statement[]{makeargs, fs._body.value}));
                                s = new ForStatement(loc, _init, condition, increment_1, forbody, fs.endloc);
                                {
                                    LabelStatement ls = checkLabeledLoop(this.sc.value, fs);
                                    if ((ls) != null)
                                        ls.gotoTarget = s;
                                }
                                s = statementSemantic(s, sc2);
                                break;
                            /*Lrangeerr:*/
                                fs.error(new BytePtr("cannot infer argument types"));
                                /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                            }
                        case 10:
                            if (((fs.op.value & 0xFF) == 202))
                                fs.deprecation(new BytePtr("cannot use `foreach_reverse` with a delegate"));
                        /*Lapply:*/
                            {
                                if (checkForArgTypes.invoke(fs))
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                TypeFunction tfld = null;
                                if (sapply.value != null)
                                {
                                    FuncDeclaration fdapply = sapply.value.isFuncDeclaration();
                                    if (fdapply != null)
                                    {
                                        assert((fdapply.type.value != null) && ((fdapply.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                        tfld = (TypeFunction)typeSemantic(fdapply.type.value, loc, sc2);
                                        /*goto Lget*//*unrolled goto*/
                                    /*Lget:*/
                                        if (((tfld.parameterList.parameters.value.get()).length.value == 1))
                                        {
                                            Parameter p_6 = tfld.parameterList.get(0);
                                            if ((p_6.type.value != null) && ((p_6.type.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                                            {
                                                Type t_1 = typeSemantic(p_6.type.value, loc, sc2);
                                                assert(((t_1.ty.value & 0xFF) == ENUMTY.Tdelegate));
                                                tfld = (TypeFunction)t_1.nextOf();
                                            }
                                        }
                                    }
                                    else if (((tab.ty.value & 0xFF) == ENUMTY.Tdelegate))
                                    {
                                        tfld = (TypeFunction)tab.nextOf();
                                    /*Lget:*/
                                        if (((tfld.parameterList.parameters.value.get()).length.value == 1))
                                        {
                                            Parameter p_6 = tfld.parameterList.get(0);
                                            if ((p_6.type.value != null) && ((p_6.type.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                                            {
                                                Type t_1 = typeSemantic(p_6.type.value, loc, sc2);
                                                assert(((t_1.ty.value & 0xFF) == ENUMTY.Tdelegate));
                                                tfld = (TypeFunction)t_1.nextOf();
                                            }
                                        }
                                    }
                                }
                                FuncExp flde = foreachBodyToFunction(sc2, fs, tfld);
                                if (flde == null)
                                    /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                {
                                    int __key1607 = 0;
                                    int __limit1608 = (fs.gotos.get()).length;
                                    for (; (__key1607 < __limit1608);__key1607 += 1) {
                                        int i_4 = __key1607;
                                        GotoStatement gs = (GotoStatement)(fs.gotos.get()).get(i_4).statement.value;
                                        if (gs.label.statement == null)
                                        {
                                            (fs.cases.get()).push(gs);
                                            s = new ReturnStatement(Loc.initial.value, new IntegerExp((long)((fs.cases.get()).length.value + 1)));
                                            (fs.gotos.get()).get(i_4).statement.value = s;
                                        }
                                    }
                                }
                                Expression e_1 = null;
                                Expression ec = null;
                                if (vinit != null)
                                {
                                    e_1 = new DeclarationExp(loc, vinit);
                                    e_1 = expressionSemantic(e_1, sc2);
                                    if (((e_1.op.value & 0xFF) == 127))
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                }
                                if (taa != null)
                                {
                                    Parameter p_7 = (fs.parameters.get()).get(0);
                                    boolean isRef = (p_7.storageClass.value & 2097152L) != 0L;
                                    Type ta_1 = p_7.type.value;
                                    if ((dim == 2))
                                    {
                                        Type ti = isRef ? taa.index.value.addMod((byte)1) : taa.index.value;
                                        if (isRef ? ti.constConv(ta_1) == 0 : ti.implicitConvTo(ta_1) == 0)
                                        {
                                            fs.error(new BytePtr("`foreach`: index must be type `%s`, not `%s`"), ti.toChars(), ta_1.toChars());
                                            /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                        }
                                        p_7 = (fs.parameters.get()).get(1);
                                        isRef = (p_7.storageClass.value & 2097152L) != 0L;
                                        ta_1 = p_7.type.value;
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
                                        (params.get()).push(new Parameter(0L, Type.tvoid.value.pointerTo(), null, null, null));
                                        (params.get()).push(new Parameter(2048L, Type.tsize_t.value, null, null, null));
                                        Ptr<DArray<Parameter>> dgparams = refPtr(new DArray<Parameter>());
                                        (dgparams.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                        if ((dim == 2))
                                            (dgparams.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                        statementsem.visitfldeTy.set(((i_5 & 0xFF)), new TypeDelegate(new TypeFunction(new ParameterList(dgparams, VarArg.none), Type.tint32.value, LINK.d, 0L)));
                                        (params.get()).push(new Parameter(0L, statementsem.visitfldeTy.get((i_5 & 0xFF)), null, null, null));
                                        statementsem.visitfdapply.set(((i_5 & 0xFF)), FuncDeclaration.genCfunc(params, Type.tint32.value, i_5 != 0 ? Id._aaApply2 : Id._aaApply, 0L));
                                    }
                                    Ptr<DArray<Expression>> exps_1 = refPtr(new DArray<Expression>());
                                    (exps_1.get()).push(fs.aggr.value);
                                    long keysize = taa.index.value.size();
                                    if ((keysize == -1L))
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    assert((keysize < -1L - (long)target.ptrsize.value));
                                    keysize = keysize + (long)(target.ptrsize.value - 1) & (long)~(target.ptrsize.value - 1);
                                    Expression fexp = flde;
                                    if (!statementsem.visitfldeTy.get((i_5 & 0xFF)).equals(flde.type.value))
                                    {
                                        fexp = new CastExp(loc, flde, flde.type.value);
                                        fexp.type.value = statementsem.visitfldeTy.get((i_5 & 0xFF));
                                    }
                                    (exps_1.get()).push(new IntegerExp(Loc.initial.value, keysize, Type.tsize_t.value));
                                    (exps_1.get()).push(fexp);
                                    ec = new VarExp(Loc.initial.value, statementsem.visitfdapply.get((i_5 & 0xFF)), false);
                                    ec = new CallExp(loc, ec, exps_1);
                                    ec.type.value = Type.tint32.value;
                                }
                                else if (((tab.ty.value & 0xFF) == ENUMTY.Tarray) || ((tab.ty.value & 0xFF) == ENUMTY.Tsarray))
                                {
                                    int BUFFER_LEN = 23;
                                    ByteSlice fdname = (byte)255;
                                    int flag = 0;
                                    switch ((tn.ty.value & 0xFF))
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
                                    switch ((tnv.ty.value & 0xFF))
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
                                    BytePtr r_1 = pcopy(((fs.op.value & 0xFF) == 202) ? new BytePtr("R") : new BytePtr(""));
                                    int j = sprintf(ptr(fdname), new BytePtr("_aApply%s%.*s%llu"), r_1, 2, statementsem.visitfntab.get(flag), (long)dim);
                                    assert((j < 23));
                                    FuncDeclaration fdapply_1 = null;
                                    TypeDelegate dgty = null;
                                    Ptr<DArray<Parameter>> params_1 = refPtr(new DArray<Parameter>());
                                    (params_1.get()).push(new Parameter(2048L, tn.arrayOf(), null, null, null));
                                    Ptr<DArray<Parameter>> dgparams_1 = refPtr(new DArray<Parameter>());
                                    (dgparams_1.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                    if ((dim == 2))
                                        (dgparams_1.get()).push(new Parameter(0L, Type.tvoidptr, null, null, null));
                                    dgty = new TypeDelegate(new TypeFunction(new ParameterList(dgparams_1, VarArg.none), Type.tint32.value, LINK.d, 0L));
                                    (params_1.get()).push(new Parameter(0L, dgty, null, null, null));
                                    fdapply_1 = FuncDeclaration.genCfunc(params_1, Type.tint32.value, ptr(fdname), 0L);
                                    if (((tab.ty.value & 0xFF) == ENUMTY.Tsarray))
                                        fs.aggr.value = fs.aggr.value.castTo(sc2, tn.arrayOf());
                                    Expression fexp_1 = flde;
                                    if (!dgty.equals(flde.type.value))
                                    {
                                        fexp_1 = new CastExp(loc, flde, flde.type.value);
                                        fexp_1.type.value = dgty;
                                    }
                                    ec = new VarExp(Loc.initial.value, fdapply_1, false);
                                    ec = new CallExp(loc, ec, fs.aggr.value, fexp_1);
                                    ec.type.value = Type.tint32.value;
                                }
                                else if (((tab.ty.value & 0xFF) == ENUMTY.Tdelegate))
                                {
                                    if (((fs.aggr.value.op.value & 0xFF) == 160) && ((DelegateExp)fs.aggr.value).func.value.isNested() && !((DelegateExp)fs.aggr.value).func.value.needThis())
                                    {
                                        fs.aggr.value = ((DelegateExp)fs.aggr.value).e1.value;
                                    }
                                    ec = new CallExp(loc, fs.aggr.value, flde);
                                    ec = expressionSemantic(ec, sc2);
                                    if (((ec.op.value & 0xFF) == 127))
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    if ((!pequals(ec.type.value, Type.tint32.value)))
                                    {
                                        fs.error(new BytePtr("`opApply()` function for `%s` must return an `int`"), tab.toChars());
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                }
                                else
                                {
                                    if (global.params.vsafe.value)
                                        flde.fd.value.tookAddressOf.value += 1;
                                    assert(((tab.ty.value & 0xFF) == ENUMTY.Tstruct) || ((tab.ty.value & 0xFF) == ENUMTY.Tclass));
                                    assert(sapply.value != null);
                                    ec = new DotIdExp(loc, fs.aggr.value, sapply.value.ident.value);
                                    ec = new CallExp(loc, ec, flde);
                                    ec = expressionSemantic(ec, sc2);
                                    if (((ec.op.value & 0xFF) == 127))
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    if ((!pequals(ec.type.value, Type.tint32.value)))
                                    {
                                        fs.error(new BytePtr("`opApply()` function for `%s` must return an `int`"), tab.toChars());
                                        /*goto case*/{ __dispatch0 = 34; continue dispatched_0; }
                                    }
                                }
                                e_1 = Expression.combine(e_1, ec);
                                if ((fs.cases.get()).length.value == 0)
                                {
                                    e_1 = new CastExp(loc, e_1, Type.tvoid.value);
                                    s = new ExpStatement(loc, e_1);
                                }
                                else
                                {
                                    Ptr<DArray<Statement>> a_1 = refPtr(new DArray<Statement>());
                                    s = new BreakStatement(Loc.initial.value, null);
                                    s = new DefaultStatement(Loc.initial.value, s);
                                    (a_1.get()).push(s);
                                    {
                                        Slice<Statement> __r1610 = (fs.cases.get()).opSlice().copy();
                                        int __key1609 = 0;
                                        for (; (__key1609 < __r1610.getLength());__key1609 += 1) {
                                            Statement c = __r1610.get(__key1609);
                                            int i_6 = __key1609;
                                            s = new CaseStatement(Loc.initial.value, new IntegerExp((long)(i_6 + 2)), c);
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
                int __key1611 = 0;
                int __limit1612 = (fs.parameters.get()).length.value;
            L_outer3:
                for (; (__key1611 < __limit1612);__key1611 += 1) {
                    int i = __key1611;
                    Parameter p = (fs.parameters.get()).get(i);
                    long stc = 2097152L;
                    Identifier id = null;
                    p.type.value = typeSemantic(p.type.value, fs.loc, sc);
                    p.type.value = p.type.value.addStorageClass(p.storageClass.value);
                    if (tfld != null)
                    {
                        Parameter prm = tfld.parameterList.get(i);
                        stc = prm.storageClass.value & 2097152L;
                        id = p.ident.value;
                        if (((p.storageClass.value & 2097152L) != stc))
                        {
                            if (stc == 0)
                            {
                                fs.error(new BytePtr("`foreach`: cannot make `%s` `ref`"), p.ident.value.toChars());
                                return null;
                            }
                            /*goto LcopyArg*//*unrolled goto*/
                            id = p.ident.value;
                        }
                    }
                    else if ((p.storageClass.value & 2097152L) != 0)
                    {
                        id = p.ident.value;
                    }
                    else
                    {
                    /*LcopyArg:*/
                        id = Identifier.generateId(new BytePtr("__applyArg"), i);
                        Initializer ie = new ExpInitializer(fs.loc, new IdentifierExp(fs.loc, id));
                        VarDeclaration v = new VarDeclaration(fs.loc, p.type.value, p.ident.value, ie, 0L);
                        v.storage_class.value |= 1099511627776L;
                        Statement s = new ExpStatement(fs.loc, v);
                        fs._body.value = new CompoundStatement(fs.loc, slice(new Statement[]{s, fs._body.value}));
                    }
                    (params.get()).push(new Parameter(stc, p.type.value, id, null, null));
                }
            }
            long stc = mergeFuncAttrs(4406703554560L, fs.func.value);
            TypeFunction tf = new TypeFunction(new ParameterList(params, VarArg.none), Type.tint32.value, LINK.d, stc);
            fs.cases = refPtr(new DArray<Statement>());
            fs.gotos = refPtr(new DArray<ScopeStatement>());
            FuncLiteralDeclaration fld = new FuncLiteralDeclaration(fs.loc, fs.endloc, tf, TOK.delegate_, fs, null);
            fld.fbody.value = fs._body.value;
            Expression flde = new FuncExp(fs.loc, fld);
            flde = expressionSemantic(flde, sc);
            fld.tookAddressOf.value = 0;
            if (((flde.op.value & 0xFF) == 127))
                return null;
            return (FuncExp)flde;
        }

        public  void visit(ForeachRangeStatement fs) {
            Loc loc = fs.loc.copy();
            fs.lwr.value = expressionSemantic(fs.lwr.value, this.sc.value);
            fs.lwr.value = resolveProperties(this.sc.value, fs.lwr.value);
            fs.lwr.value = fs.lwr.value.optimize(0, false);
            if (fs.lwr.value.type.value == null)
            {
                fs.error(new BytePtr("invalid range lower bound `%s`"), fs.lwr.value.toChars());
                this.setError();
                return ;
            }
            fs.upr.value = expressionSemantic(fs.upr.value, this.sc.value);
            fs.upr.value = resolveProperties(this.sc.value, fs.upr.value);
            fs.upr.value = fs.upr.value.optimize(0, false);
            if (fs.upr.value.type.value == null)
            {
                fs.error(new BytePtr("invalid range upper bound `%s`"), fs.upr.value.toChars());
                this.setError();
                return ;
            }
            if (fs.prm.type.value != null)
            {
                fs.prm.type.value = typeSemantic(fs.prm.type.value, loc, this.sc.value);
                fs.prm.type.value = fs.prm.type.value.addStorageClass(fs.prm.storageClass.value);
                fs.lwr.value = fs.lwr.value.implicitCastTo(this.sc.value, fs.prm.type.value);
                if ((fs.upr.value.implicitConvTo(fs.prm.type.value) != 0) || ((fs.prm.storageClass.value & 2097152L) != 0))
                {
                    fs.upr.value = fs.upr.value.implicitCastTo(this.sc.value, fs.prm.type.value);
                }
                else
                {
                    Expression limit = new MinExp(loc, fs.upr.value, literal_356A192B7913B04C());
                    limit = expressionSemantic(limit, this.sc.value);
                    limit = limit.optimize(0, false);
                    if (limit.implicitConvTo(fs.prm.type.value) == 0)
                    {
                        fs.upr.value = fs.upr.value.implicitCastTo(this.sc.value, fs.prm.type.value);
                    }
                }
            }
            else
            {
                Type tlwr = fs.lwr.value.type.value.toBasetype();
                if (((tlwr.ty.value & 0xFF) == ENUMTY.Tstruct) || ((tlwr.ty.value & 0xFF) == ENUMTY.Tclass))
                {
                    fs.prm.type.value = fs.lwr.value.type.value;
                }
                else if ((pequals(fs.lwr.value.type.value, fs.upr.value.type.value)))
                {
                    fs.prm.type.value = fs.lwr.value.type.value;
                }
                else
                {
                    AddExp ea = new AddExp(loc, fs.lwr.value, fs.upr.value);
                    if (typeCombine(ea, this.sc.value) != null)
                        this.setError();
                        return ;
                    fs.prm.type.value = ea.type.value;
                    fs.lwr.value = ea.e1.value;
                    fs.upr.value = ea.e2.value;
                }
                fs.prm.type.value = fs.prm.type.value.addStorageClass(fs.prm.storageClass.value);
            }
            if (((fs.prm.type.value.ty.value & 0xFF) == ENUMTY.Terror) || ((fs.lwr.value.op.value & 0xFF) == 127) || ((fs.upr.value.op.value & 0xFF) == 127))
            {
                this.setError();
                return ;
            }
            ExpInitializer ie = new ExpInitializer(loc, ((fs.op.value & 0xFF) == 201) ? fs.lwr.value : fs.upr.value);
            fs.key = new VarDeclaration(loc, fs.upr.value.type.value.mutableOf(), Identifier.generateId(new BytePtr("__key")), ie, 0L);
            fs.key.storage_class.value |= 1099511627776L;
            SignExtendedNumber lower = getIntRange(fs.lwr.value).imin.copy();
            SignExtendedNumber upper = getIntRange(fs.upr.value).imax.copy();
            if ((lower.opCmp(upper) <= 0))
            {
                fs.key.range.value = refPtr(new IntRange(lower, upper));
            }
            Identifier id = Identifier.generateId(new BytePtr("__limit"));
            ie = new ExpInitializer(loc, ((fs.op.value & 0xFF) == 201) ? fs.upr.value : fs.lwr.value);
            VarDeclaration tmp = new VarDeclaration(loc, fs.upr.value.type.value, id, ie, 0L);
            tmp.storage_class.value |= 1099511627776L;
            Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
            if (((fs.op.value & 0xFF) == 201))
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
            if (((fs.op.value & 0xFF) == 202))
            {
                cond = new PostExp(TOK.minusMinus, loc, new VarExp(loc, fs.key, true));
                if (fs.prm.type.value.isscalar())
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
                if (fs.prm.type.value.isscalar())
                {
                    cond = new CmpExp(TOK.lessThan, loc, new VarExp(loc, fs.key, true), new VarExp(loc, tmp, true));
                }
                else
                {
                    cond = new EqualExp(TOK.notEqual, loc, new VarExp(loc, fs.key, true), new VarExp(loc, tmp, true));
                }
            }
            Expression increment = null;
            if (((fs.op.value & 0xFF) == 201))
            {
                increment = new PreExp(TOK.prePlusPlus, loc, new VarExp(loc, fs.key, true));
            }
            if (((fs.prm.storageClass.value & 2097152L) != 0) && fs.prm.type.value.equals(fs.key.type.value))
            {
                fs.key.range.value = null;
                AliasDeclaration v = new AliasDeclaration(loc, fs.prm.ident.value, fs.key);
                fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v), fs._body.value}));
            }
            else
            {
                ie = new ExpInitializer(loc, new CastExp(loc, new VarExp(loc, fs.key, true), fs.prm.type.value));
                VarDeclaration v = new VarDeclaration(loc, fs.prm.type.value, fs.prm.ident.value, ie, 0L);
                v.storage_class.value |= 1099511644160L | fs.prm.storageClass.value & 2097152L;
                fs._body.value = new CompoundStatement(loc, slice(new Statement[]{new ExpStatement(loc, v), fs._body.value}));
                if ((fs.key.range.value != null) && !fs.prm.type.value.isMutable())
                {
                    v.range.value = refPtr(new IntRange((fs.key.range.value.get()).imin, (fs.key.range.value.get()).imax.opBinary_minus(new SignExtendedNumber(1L, false))));
                }
            }
            if ((fs.prm.storageClass.value & 2097152L) != 0)
            {
                if ((fs.key.type.value.constConv(fs.prm.type.value) <= MATCH.nomatch))
                {
                    fs.error(new BytePtr("argument type mismatch, `%s` to `ref %s`"), fs.key.type.value.toChars(), fs.prm.type.value.toChars());
                    this.setError();
                    return ;
                }
            }
            ForStatement s = new ForStatement(loc, forinit, cond, increment, fs._body.value, fs.endloc);
            {
                LabelStatement ls = checkLabeledLoop(this.sc.value, fs);
                if ((ls) != null)
                    ls.gotoTarget = s;
            }
            this.result = statementSemantic(s, this.sc.value);
        }

        public  void visit(IfStatement ifs) {
            ifs.condition.value = checkAssignmentAsCondition(ifs.condition.value);
            ScopeDsymbol sym = new ScopeDsymbol();
            sym.parent.value = (this.sc.value.get()).scopesym.value;
            sym.endlinnum = ifs.endloc.linnum;
            Ptr<Scope> scd = (this.sc.value.get()).push(sym);
            if (ifs.prm != null)
            {
                ExpInitializer ei = new ExpInitializer(ifs.loc, ifs.condition.value);
                ifs.match = new VarDeclaration(ifs.loc, ifs.prm.type.value, ifs.prm.ident.value, ei, 0L);
                ifs.match.parent.value = (scd.get()).func.value;
                ifs.match.storage_class.value |= ifs.prm.storageClass.value;
                dsymbolSemantic(ifs.match, scd);
                DeclarationExp de = new DeclarationExp(ifs.loc, ifs.match);
                VarExp ve = new VarExp(ifs.loc, ifs.match, true);
                ifs.condition.value = new CommaExp(ifs.loc, de, ve, true);
                ifs.condition.value = expressionSemantic(ifs.condition.value, scd);
                if (ifs.match.edtor.value != null)
                {
                    Statement sdtor = new DtorExpStatement(ifs.loc, ifs.match.edtor.value, ifs.match);
                    sdtor = new ScopeGuardStatement(ifs.loc, TOK.onScopeExit, sdtor);
                    ifs.ifbody.value = new CompoundStatement(ifs.loc, slice(new Statement[]{sdtor, ifs.ifbody.value}));
                    ifs.match.storage_class.value |= 16777216L;
                    Statement sdtor2 = new DtorExpStatement(ifs.loc, ifs.match.edtor.value, ifs.match);
                    if (ifs.elsebody.value != null)
                        ifs.elsebody.value = new CompoundStatement(ifs.loc, slice(new Statement[]{sdtor2, ifs.elsebody.value}));
                    else
                        ifs.elsebody.value = sdtor2;
                }
            }
            else
            {
                if (((ifs.condition.value.op.value & 0xFF) == 28))
                    ((DotIdExp)ifs.condition.value).noderef = true;
                ifs.condition.value = expressionSemantic(ifs.condition.value, scd);
                ifs.condition.value = resolveProperties(scd, ifs.condition.value);
                ifs.condition.value = ifs.condition.value.addDtorHook(scd);
            }
            if (checkNonAssignmentArrayOp(ifs.condition.value, false))
                ifs.condition.value = new ErrorExp();
            ifs.condition.value = checkGC(scd, ifs.condition.value);
            ifs.condition.value = ifs.condition.value.toBoolean(scd);
            ifs.condition.value = ifs.condition.value.optimize(0, false);
            CtorFlow ctorflow_root = (scd.get()).ctorflow.clone().copy();
            ifs.ifbody.value = semanticNoScope(ifs.ifbody.value, scd);
            (scd.get()).pop();
            CtorFlow ctorflow_then = (this.sc.value.get()).ctorflow.copy();
            (this.sc.value.get()).ctorflow = ctorflow_root.copy();
            if (ifs.elsebody.value != null)
                ifs.elsebody.value = semanticScope(ifs.elsebody.value, this.sc.value, null, null);
            (this.sc.value.get()).merge(ifs.loc, ctorflow_then);
            ctorflow_then.freeFieldinit();
            if (((ifs.condition.value.op.value & 0xFF) == 127) || (ifs.ifbody.value != null) && (ifs.ifbody.value.isErrorStatement() != null) || (ifs.elsebody.value != null) && (ifs.elsebody.value.isErrorStatement() != null))
            {
                this.setError();
                return ;
            }
            this.result = ifs;
        }

        public  void visit(ConditionalStatement cs) {
            if (cs.condition.include(this.sc.value) != 0)
            {
                DebugCondition dc = cs.condition.isDebugCondition();
                if (dc != null)
                {
                    this.sc.value = (this.sc.value.get()).push();
                    (this.sc.value.get()).flags.value |= 8;
                    cs.ifbody.value = statementSemantic(cs.ifbody.value, this.sc.value);
                    (this.sc.value.get()).pop();
                }
                else
                    cs.ifbody.value = statementSemantic(cs.ifbody.value, this.sc.value);
                this.result = cs.ifbody.value;
            }
            else
            {
                if (cs.elsebody.value != null)
                    cs.elsebody.value = statementSemantic(cs.elsebody.value, this.sc.value);
                this.result = cs.elsebody.value;
            }
        }

        public  void visit(PragmaStatement ps) {
            if ((pequals(ps.ident, Id.msg)))
            {
                if (ps.args != null)
                {
                    {
                        Slice<Expression> __r1613 = (ps.args.get()).opSlice().copy();
                        int __key1614 = 0;
                        for (; (__key1614 < __r1613.getLength());__key1614 += 1) {
                            Expression arg = __r1613.get(__key1614);
                            this.sc.value = (this.sc.value.get()).startCTFE();
                            Expression e = expressionSemantic(arg, this.sc.value);
                            e = resolveProperties(this.sc.value, e);
                            this.sc.value = (this.sc.value.get()).endCTFE();
                            e = ctfeInterpretForPragmaMsg(e);
                            if (((e.op.value & 0xFF) == 127))
                            {
                                errorSupplemental(ps.loc, new BytePtr("while evaluating `pragma(msg, %s)`"), arg.toChars());
                                this.setError();
                                return ;
                            }
                            StringExp se = e.toStringExp();
                            if (se != null)
                            {
                                se = se.toUTF8(this.sc.value);
                                fprintf(stderr, new BytePtr("%.*s"), se.len.value, se.string.value);
                            }
                            else
                                fprintf(stderr, new BytePtr("%s"), e.toChars());
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
                if ((ps.args == null) || ((ps.args.get()).length.value != 1))
                    ps.error(new BytePtr("function name expected for start address"));
                else
                {
                    Expression e = (ps.args.get()).get(0);
                    this.sc.value = (this.sc.value.get()).startCTFE();
                    e = expressionSemantic(e, this.sc.value);
                    e = resolveProperties(this.sc.value, e);
                    this.sc.value = (this.sc.value.get()).endCTFE();
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
                        ps._body = statementSemantic(ps._body, this.sc.value);
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
                if ((ps.args == null) || ((ps.args.get()).length.value == 0))
                    inlining = PINLINE.default_;
                else if ((ps.args == null) || ((ps.args.get()).length.value != 1))
                {
                    ps.error(new BytePtr("boolean expression expected for `pragma(inline)`"));
                    this.setError();
                    return ;
                }
                else
                {
                    Expression e = (ps.args.get()).get(0);
                    if (((e.op.value & 0xFF) != 135) || !e.type.value.equals(Type.tbool.value))
                    {
                        ps.error(new BytePtr("pragma(inline, true or false) expected, not `%s`"), e.toChars());
                        this.setError();
                        return ;
                    }
                    if (e.isBool(true))
                        inlining = PINLINE.always;
                    else if (e.isBool(false))
                        inlining = PINLINE.never;
                    FuncDeclaration fd = (this.sc.value.get()).func.value;
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
                ps._body = statementSemantic(ps._body, this.sc.value);
            }
            this.result = ps._body;
        }

        public  void visit(StaticAssertStatement s) {
            semantic2(s.sa, this.sc.value);
        }

        public  void visit(SwitchStatement ss) {
            ss.tf = (this.sc.value.get()).tf;
            if (ss.cases != null)
            {
                this.result = ss;
                return ;
            }
            boolean conditionError = false;
            ss.condition.value = expressionSemantic(ss.condition.value, this.sc.value);
            ss.condition.value = resolveProperties(this.sc.value, ss.condition.value);
            Type att = null;
            TypeEnum te = null;
            for (; ((ss.condition.value.op.value & 0xFF) != 127);){
                if (((ss.condition.value.type.value.ty.value & 0xFF) == ENUMTY.Tenum))
                    te = (TypeEnum)ss.condition.value.type.value;
                if (ss.condition.value.type.value.isString())
                {
                    if (((ss.condition.value.type.value.ty.value & 0xFF) != ENUMTY.Tarray))
                    {
                        ss.condition.value = ss.condition.value.implicitCastTo(this.sc.value, ss.condition.value.type.value.nextOf().arrayOf());
                    }
                    ss.condition.value.type.value = ss.condition.value.type.value.constOf();
                    break;
                }
                ss.condition.value = integralPromotions(ss.condition.value, this.sc.value);
                if (((ss.condition.value.op.value & 0xFF) != 127) && ss.condition.value.type.value.isintegral())
                    break;
                AggregateDeclaration ad = isAggregate(ss.condition.value.type.value);
                if ((ad != null) && (ad.aliasthis.value != null) && (!pequals(ss.condition.value.type.value, att)))
                {
                    if ((att == null) && ss.condition.value.type.value.checkAliasThisRec())
                        att = ss.condition.value.type.value;
                    {
                        Expression e = resolveAliasThis(this.sc.value, ss.condition.value, true);
                        if ((e) != null)
                        {
                            ss.condition.value = e;
                            continue;
                        }
                    }
                }
                if (((ss.condition.value.op.value & 0xFF) != 127))
                {
                    ss.error(new BytePtr("`%s` must be of integral or string type, it is a `%s`"), ss.condition.value.toChars(), ss.condition.value.type.value.toChars());
                    conditionError = true;
                    break;
                }
            }
            if (checkNonAssignmentArrayOp(ss.condition.value, false))
                ss.condition.value = new ErrorExp();
            ss.condition.value = ss.condition.value.optimize(0, false);
            ss.condition.value = checkGC(this.sc.value, ss.condition.value);
            if (((ss.condition.value.op.value & 0xFF) == 127))
                conditionError = true;
            boolean needswitcherror = false;
            ss.lastVar.value = (this.sc.value.get()).lastVar;
            this.sc.value = (this.sc.value.get()).push();
            (this.sc.value.get()).sbreak = ss;
            (this.sc.value.get()).sw = ss;
            ss.cases = refPtr(new DArray<CaseStatement>());
            boolean inLoopSave = (this.sc.value.get()).inLoop;
            (this.sc.value.get()).inLoop = true;
            ss._body.value = statementSemantic(ss._body.value, this.sc.value);
            (this.sc.value.get()).inLoop = inLoopSave;
            if (conditionError || (ss._body.value != null) && (ss._body.value.isErrorStatement() != null))
            {
                (this.sc.value.get()).pop();
                this.setError();
                return ;
            }
        /*Lgotocase:*/
            {
                Slice<GotoCaseStatement> __r1615 = ss.gotoCases.opSlice().copy();
                int __key1616 = 0;
                for (; (__key1616 < __r1615.getLength());__key1616 += 1) {
                    GotoCaseStatement gcs = __r1615.get(__key1616);
                    if (gcs.exp == null)
                    {
                        gcs.error(new BytePtr("no `case` statement following `goto case;`"));
                        (this.sc.value.get()).pop();
                        this.setError();
                        return ;
                    }
                    {
                        Ptr<Scope> scx = this.sc.value;
                        for (; scx != null;scx = (scx.get()).enclosing.value){
                            if ((scx.get()).sw == null)
                                continue;
                            {
                                Slice<CaseStatement> __r1617 = ((scx.get()).sw.cases.get()).opSlice().copy();
                                int __key1618 = 0;
                                for (; (__key1618 < __r1617.getLength());__key1618 += 1) {
                                    CaseStatement cs = __r1617.get(__key1618);
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
                    (this.sc.value.get()).pop();
                    this.setError();
                    return ;
                }
            }
            if (ss.isFinal)
            {
                Type t = ss.condition.value.type.value;
                Dsymbol ds = null;
                EnumDeclaration ed = null;
                if ((t != null) && ((ds = t.toDsymbol(this.sc.value)) != null))
                    ed = ds.isEnumDeclaration();
                if ((ed == null) && (te != null) && ((ds = te.toDsymbol(this.sc.value)) != null))
                    ed = ds.isEnumDeclaration();
                if (ed != null)
                {
                /*Lmembers:*/
                    {
                        Slice<Dsymbol> __r1619 = (ed.members.value.get()).opSlice().copy();
                        int __key1620 = 0;
                        for (; (__key1620 < __r1619.getLength());__key1620 += 1) {
                            Dsymbol es = __r1619.get(__key1620);
                            EnumMember em = es.isEnumMember();
                            if (em != null)
                            {
                                {
                                    Slice<CaseStatement> __r1621 = (ss.cases.get()).opSlice().copy();
                                    int __key1622 = 0;
                                    for (; (__key1622 < __r1621.getLength());__key1622 += 1) {
                                        CaseStatement cs = __r1621.get(__key1622);
                                        if (cs.exp.equals(em.value()) || !cs.exp.type.value.isString() && !em.value().type.value.isString() && (cs.exp.toInteger() == em.value().toInteger()))
                                            continue Lmembers;
                                    }
                                }
                                ss.error(new BytePtr("`enum` member `%s` not represented in `final switch`"), em.toChars());
                                (this.sc.value.get()).pop();
                                this.setError();
                                return ;
                            }
                        }
                    }
                }
                else
                    needswitcherror = true;
            }
            if (((this.sc.value.get()).sw.sdefault == null) && !ss.isFinal || needswitcherror || ((global.params.useAssert & 0xFF) == 2))
            {
                ss.hasNoDefault = 1;
                if (!ss.isFinal && (ss._body.value == null) || (ss._body.value.isErrorStatement() == null))
                    ss.error(new BytePtr("`switch` statement without a `default`; use `final switch` or add `default: assert(0);` or add `default: break;`"));
                Ptr<DArray<Statement>> a = refPtr(new DArray<Statement>());
                CompoundStatement cs = null;
                Statement s = null;
                if (((global.params.useSwitchError & 0xFF) == 2) && ((global.params.checkAction & 0xFF) != 2))
                {
                    if (((global.params.checkAction & 0xFF) == 1))
                    {
                        s = new ExpStatement(ss.loc, new AssertExp(ss.loc, new IntegerExp(ss.loc, 0L, Type.tint32.value), null));
                    }
                    else
                    {
                        if (!verifyHookExist(ss.loc, this.sc.value.get(), Id.__switch_error, new ByteSlice("generating assert messages"), Id.object.value))
                            this.setError();
                            return ;
                        Expression sl = new IdentifierExp(ss.loc, Id.empty.value);
                        sl = new DotIdExp(ss.loc, sl, Id.object.value);
                        sl = new DotIdExp(ss.loc, sl, Id.__switch_error);
                        Ptr<DArray<Expression>> args = refPtr(new DArray<Expression>(2));
                        args.get().set(0, new StringExp(ss.loc, ss.loc.filename));
                        args.get().set(1, new IntegerExp((long)ss.loc.linnum));
                        sl = new CallExp(ss.loc, sl, args);
                        expressionSemantic(sl, this.sc.value);
                        s = new SwitchErrorStatement(ss.loc, sl);
                    }
                }
                else
                    s = new ExpStatement(ss.loc, new HaltExp(ss.loc));
                (a.get()).reserve(2);
                (this.sc.value.get()).sw.sdefault = new DefaultStatement(ss.loc, s);
                (a.get()).push(ss._body.value);
                if ((blockExit(ss._body.value, (this.sc.value.get()).func.value, false) & BE.fallthru) != 0)
                    (a.get()).push(new BreakStatement(Loc.initial.value, null));
                (a.get()).push((this.sc.value.get()).sw.sdefault);
                cs = new CompoundStatement(ss.loc, a);
                ss._body.value = cs;
            }
            if (ss.checkLabel())
            {
                (this.sc.value.get()).pop();
                this.setError();
                return ;
            }
            if (ss.condition.value.type.value.isString())
            {
                if (!verifyHookExist(ss.loc, this.sc.value.get(), Id.__switch, new ByteSlice("switch cases on strings"), Id.object.value))
                    this.setError();
                    return ;
                int numcases = 0;
                if (ss.cases != null)
                    numcases = (ss.cases.get()).length;
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
                    Function2<Object,Object,Integer> sort_compare = new Function2<Object,Object,Integer>(){
                        public Integer invoke(Object x, Object y) {
                            CaseStatement ox = ((Ptr<CaseStatement>)x).get();
                            CaseStatement oy = ((Ptr<CaseStatement>)y).get();
                            Ref<StringExp> se1 = ref(ox.exp.isStringExp());
                            Ref<StringExp> se2 = ref(oy.exp.isStringExp());
                            return (se1.value != null) && (se2.value != null) ? se1.value.comparex(se2.value) : 0;
                        }
                    };
                    qsort((csCopy.get()).data, numcases, 4, sort_compare);
                }
                Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>());
                (arguments.get()).push(ss.condition.value);
                Ptr<DArray<RootObject>> compileTimeArgs = refPtr(new DArray<RootObject>());
                (compileTimeArgs.get()).push(new TypeExp(ss.loc, ss.condition.value.type.value.nextOf()));
                {
                    Slice<CaseStatement> __r1623 = (csCopy.get()).opSlice().copy();
                    int __key1624 = 0;
                    for (; (__key1624 < __r1623.getLength());__key1624 += 1) {
                        CaseStatement caseString = __r1623.get(__key1624);
                        (compileTimeArgs.get()).push(caseString.exp);
                    }
                }
                Expression sl = new IdentifierExp(ss.loc, Id.empty.value);
                sl = new DotIdExp(ss.loc, sl, Id.object.value);
                sl = new DotTemplateInstanceExp(ss.loc, sl, Id.__switch, compileTimeArgs);
                sl = new CallExp(ss.loc, sl, arguments);
                expressionSemantic(sl, this.sc.value);
                ss.condition.value = sl;
                int i = 0;
                {
                    Slice<CaseStatement> __r1625 = (csCopy.get()).opSlice().copy();
                    int __key1626 = 0;
                    for (; (__key1626 < __r1625.getLength());__key1626 += 1) {
                        CaseStatement c = __r1625.get(__key1626);
                        (ss.cases.get()).get(c.index).exp = new IntegerExp((long)i++);
                    }
                }
                statementSemantic(ss, this.sc.value);
            }
            (this.sc.value.get()).pop();
            this.result = ss;
        }

        public  void visit(CaseStatement cs) {
            SwitchStatement sw = (this.sc.value.get()).sw;
            boolean errors = false;
            this.sc.value = (this.sc.value.get()).startCTFE();
            cs.exp = expressionSemantic(cs.exp, this.sc.value);
            cs.exp = resolveProperties(this.sc.value, cs.exp);
            this.sc.value = (this.sc.value.get()).endCTFE();
            if (sw != null)
            {
                cs.exp = cs.exp.implicitCastTo(this.sc.value, sw.condition.value.type.value);
                cs.exp = cs.exp.optimize(1, false);
                Expression e = cs.exp;
                for (; ((e.op.value & 0xFF) == 12);) {
                    e = ((CastExp)e).e1.value;
                }
                try {
                    if (((e.op.value & 0xFF) == 26))
                    {
                        VarExp ve = (VarExp)e;
                        VarDeclaration v = ve.var.value.isVarDeclaration();
                        Type t = cs.exp.type.value.toBasetype();
                        if ((v != null) && t.isintegral() || ((t.ty.value & 0xFF) == ENUMTY.Tclass))
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
                                Ptr<Scope> scx = this.sc.value;
                                for (; scx != null;scx = (scx.get()).enclosing.value){
                                    if (((scx.get()).enclosing.value != null) && (pequals(((scx.get()).enclosing.value.get()).sw, sw)))
                                        continue;
                                    assert((pequals((scx.get()).sw, sw)));
                                    if ((scx.get()).search(cs.exp.loc.value, v.ident.value, null, 0) == null)
                                    {
                                        cs.error(new BytePtr("`case` variable `%s` declared at %s cannot be declared in `switch` body"), v.toChars(), v.loc.value.toChars(global.params.showColumns.value));
                                        errors = true;
                                    }
                                    break;
                                }
                            }
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                    }
                    else
                        cs.exp = cs.exp.ctfeInterpret();
                    {
                        StringExp se = cs.exp.toStringExp();
                        if ((se) != null)
                            cs.exp = se;
                        else if (((cs.exp.op.value & 0xFF) != 135) && ((cs.exp.op.value & 0xFF) != 127))
                        {
                            cs.error(new BytePtr("`case` must be a `string` or an integral constant, not `%s`"), cs.exp.toChars());
                            errors = true;
                        }
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                {
                    Slice<CaseStatement> __r1627 = (sw.cases.get()).opSlice().copy();
                    int __key1628 = 0;
                    for (; (__key1628 < __r1627.getLength());__key1628 += 1) {
                        CaseStatement cs2 = __r1627.get(__key1628);
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
                if ((!pequals((this.sc.value.get()).sw.tf, (this.sc.value.get()).tf)))
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
            (this.sc.value.get()).ctorflow.orCSX(CSX.label);
            cs.statement.value = statementSemantic(cs.statement.value, this.sc.value);
            if (cs.statement.value.isErrorStatement() != null)
            {
                this.result = cs.statement.value;
                return ;
            }
            if (errors || ((cs.exp.op.value & 0xFF) == 127))
                this.setError();
                return ;
            cs.lastVar = (this.sc.value.get()).lastVar;
            this.result = cs;
        }

        public  void visit(CaseRangeStatement crs) {
            SwitchStatement sw = (this.sc.value.get()).sw;
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
            this.sc.value = (this.sc.value.get()).startCTFE();
            crs.first = expressionSemantic(crs.first, this.sc.value);
            crs.first = resolveProperties(this.sc.value, crs.first);
            this.sc.value = (this.sc.value.get()).endCTFE();
            crs.first = crs.first.implicitCastTo(this.sc.value, sw.condition.value.type.value);
            crs.first = crs.first.ctfeInterpret();
            this.sc.value = (this.sc.value.get()).startCTFE();
            crs.last = expressionSemantic(crs.last, this.sc.value);
            crs.last = resolveProperties(this.sc.value, crs.last);
            this.sc.value = (this.sc.value.get()).endCTFE();
            crs.last = crs.last.implicitCastTo(this.sc.value, sw.condition.value.type.value);
            crs.last = crs.last.ctfeInterpret();
            if (((crs.first.op.value & 0xFF) == 127) || ((crs.last.op.value & 0xFF) == 127) || errors)
            {
                if (crs.statement != null)
                    statementSemantic(crs.statement, this.sc.value);
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
                this.setError();
                return ;
            Ptr<DArray<Statement>> statements = refPtr(new DArray<Statement>());
            {
                long i = fval;
                for (; (i != lval + 1L);i++){
                    Statement s = crs.statement;
                    if ((i != lval))
                        s = new ExpStatement(crs.loc, null);
                    Expression e = new IntegerExp(crs.loc, i, crs.first.type.value);
                    Statement cs = new CaseStatement(crs.loc, e, s);
                    (statements.get()).push(cs);
                }
            }
            Statement s = new CompoundStatement(crs.loc, statements);
            (this.sc.value.get()).ctorflow.orCSX(CSX.label);
            s = statementSemantic(s, this.sc.value);
            this.result = s;
        }

        public  void visit(DefaultStatement ds) {
            boolean errors = false;
            if ((this.sc.value.get()).sw != null)
            {
                if ((this.sc.value.get()).sw.sdefault != null)
                {
                    ds.error(new BytePtr("`switch` statement already has a default"));
                    errors = true;
                }
                (this.sc.value.get()).sw.sdefault = ds;
                if ((!pequals((this.sc.value.get()).sw.tf, (this.sc.value.get()).tf)))
                {
                    ds.error(new BytePtr("`switch` and `default` are in different `finally` blocks"));
                    errors = true;
                }
                if ((this.sc.value.get()).sw.isFinal)
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
            (this.sc.value.get()).ctorflow.orCSX(CSX.label);
            ds.statement.value = statementSemantic(ds.statement.value, this.sc.value);
            if (errors || (ds.statement.value.isErrorStatement() != null))
                this.setError();
                return ;
            ds.lastVar = (this.sc.value.get()).lastVar;
            this.result = ds;
        }

        public  void visit(GotoDefaultStatement gds) {
            gds.sw = (this.sc.value.get()).sw;
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
            if ((this.sc.value.get()).sw == null)
            {
                gcs.error(new BytePtr("`goto case` not in `switch` statement"));
                this.setError();
                return ;
            }
            if (gcs.exp != null)
            {
                gcs.exp = expressionSemantic(gcs.exp, this.sc.value);
                gcs.exp = gcs.exp.implicitCastTo(this.sc.value, (this.sc.value.get()).sw.condition.value.type.value);
                gcs.exp = gcs.exp.optimize(0, false);
                if (((gcs.exp.op.value & 0xFF) == 127))
                    this.setError();
                    return ;
            }
            (this.sc.value.get()).sw.gotoCases.push(gcs);
            this.result = gcs;
        }

        public  void visit(ReturnStatement rs) {
            FuncDeclaration fd = (this.sc.value.get()).parent.value.isFuncDeclaration();
            if (fd.fes.value != null)
                fd = fd.fes.value.func.value;
            TypeFunction tf = (TypeFunction)fd.type.value;
            assert(((tf.ty.value & 0xFF) == ENUMTY.Tfunction));
            if ((rs.exp.value != null) && ((rs.exp.value.op.value & 0xFF) == 26) && (pequals(((VarExp)rs.exp.value).var.value, fd.vresult)))
            {
                if ((this.sc.value.get()).fes != null)
                {
                    assert((rs.caseDim == 0));
                    ((this.sc.value.get()).fes.cases.get()).push(rs);
                    this.result = new ReturnStatement(Loc.initial.value, new IntegerExp((long)(((this.sc.value.get()).fes.cases.get()).length.value + 1)));
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
                    fd.returns = refPtr(new DArray<ReturnStatement>());
                (fd.returns.get()).push(rs);
                this.result = rs;
                return ;
            }
            Type tret = tf.next.value;
            Type tbret = tret != null ? tret.toBasetype() : null;
            boolean inferRef = tf.isref.value && ((fd.storage_class.value & 256L) != 0);
            Ref<Expression> e0 = ref(null);
            boolean errors = false;
            if (((this.sc.value.get()).flags.value & 96) != 0)
            {
                rs.error(new BytePtr("`return` statements cannot be in contracts"));
                errors = true;
            }
            if (((this.sc.value.get()).os != null) && (((this.sc.value.get()).os.tok & 0xFF) != 205))
            {
                rs.error(new BytePtr("`return` statements cannot be in `%s` bodies"), Token.toChars((this.sc.value.get()).os.tok));
                errors = true;
            }
            if ((this.sc.value.get()).tf != null)
            {
                rs.error(new BytePtr("`return` statements cannot be in `finally` bodies"));
                errors = true;
            }
            if (fd.isCtorDeclaration() != null)
            {
                if (rs.exp.value != null)
                {
                    rs.error(new BytePtr("cannot return expression from constructor"));
                    errors = true;
                }
                rs.exp.value = new ThisExp(Loc.initial.value);
                rs.exp.value.type.value = tret;
            }
            else if (rs.exp.value != null)
            {
                fd.hasReturnExp |= (fd.hasReturnExp & 1) != 0 ? 16 : 1;
                FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                if (tret != null)
                    rs.exp.value = inferType(rs.exp.value, tret, 0);
                else if ((fld != null) && (fld.treq.value != null))
                    rs.exp.value = inferType(rs.exp.value, fld.treq.value.nextOf().nextOf(), 0);
                rs.exp.value = expressionSemantic(rs.exp.value, this.sc.value);
                if (((rs.exp.value.op.value & 0xFF) == 20))
                    rs.exp.value = resolveAliasThis(this.sc.value, rs.exp.value, false);
                rs.exp.value = resolveProperties(this.sc.value, rs.exp.value);
                if (rs.exp.value.checkType())
                    rs.exp.value = new ErrorExp();
                {
                    FuncDeclaration f = isFuncAddress(rs.exp.value, null);
                    if ((f) != null)
                    {
                        if (fd.inferRetType && f.checkForwardRef(rs.exp.value.loc.value))
                            rs.exp.value = new ErrorExp();
                    }
                }
                if (checkNonAssignmentArrayOp(rs.exp.value, false))
                    rs.exp.value = new ErrorExp();
                rs.exp.value = Expression.extractLast(rs.exp.value, e0);
                if (((rs.exp.value.op.value & 0xFF) == 18))
                    rs.exp.value = valueNoDtor(rs.exp.value);
                if (e0.value != null)
                    e0.value = e0.value.optimize(0, false);
                if ((tbret != null) && ((tbret.ty.value & 0xFF) == ENUMTY.Tvoid) || ((rs.exp.value.type.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    if (((rs.exp.value.type.value.ty.value & 0xFF) != ENUMTY.Tvoid))
                    {
                        rs.error(new BytePtr("cannot return non-void from `void` function"));
                        errors = true;
                        rs.exp.value = new CastExp(rs.loc, rs.exp.value, Type.tvoid.value);
                        rs.exp.value = expressionSemantic(rs.exp.value, this.sc.value);
                    }
                    e0.value = Expression.combine(e0.value, rs.exp.value);
                    rs.exp.value = null;
                }
                if (e0.value != null)
                    e0.value = checkGC(this.sc.value, e0.value);
            }
            if (rs.exp.value != null)
            {
                if (fd.inferRetType)
                {
                    if (tret == null)
                    {
                        tf.next.value = rs.exp.value.type.value;
                    }
                    else if (((tret.ty.value & 0xFF) != ENUMTY.Terror) && !rs.exp.value.type.value.equals(tret))
                    {
                        int m1 = rs.exp.value.type.value.implicitConvTo(tret);
                        int m2 = tret.implicitConvTo(rs.exp.value.type.value);
                        if ((m1 != 0) && (m2 != 0))
                        {
                        }
                        else if ((m1 == 0) && (m2 != 0))
                            tf.next.value = rs.exp.value.type.value;
                        else if ((m1 != 0) && (m2 == 0))
                        {
                        }
                        else if (((rs.exp.value.op.value & 0xFF) != 127))
                        {
                            rs.error(new BytePtr("mismatched function return type inference of `%s` and `%s`"), rs.exp.value.type.value.toChars(), tret.toChars());
                            errors = true;
                            tf.next.value = Type.terror.value;
                        }
                    }
                    tret = tf.next.value;
                    tbret = tret.toBasetype();
                }
                if (inferRef)
                {
                    Function0<Void> turnOffRef = new Function0<Void>(){
                        public Void invoke() {
                            tf.isref.value = false;
                            tf.isreturn.value = false;
                            fd.storage_class.value &= -17592186044417L;
                        }
                    };
                    if (rs.exp.value.isLvalue())
                    {
                        if (checkReturnEscapeRef(this.sc.value, rs.exp.value, true))
                            turnOffRef.invoke();
                        else if (rs.exp.value.type.value.constConv(tf.next.value) == 0)
                            turnOffRef.invoke();
                    }
                    else
                        turnOffRef.invoke();
                }
                if (fd.nrvo_can && ((rs.exp.value.op.value & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)rs.exp.value;
                    VarDeclaration v = ve.var.value.isVarDeclaration();
                    if (tf.isref.value)
                    {
                        if (!inferRef)
                            fd.nrvo_can = false;
                    }
                    else if ((v == null) || v.isOut() || v.isRef())
                        fd.nrvo_can = false;
                    else if ((fd.nrvo_var == null))
                    {
                        if (!v.isDataseg() && !v.isParameter() && (pequals(v.toParent2(), fd)))
                        {
                            fd.nrvo_var = v;
                        }
                        else
                            fd.nrvo_can = false;
                    }
                    else if ((!pequals(fd.nrvo_var, v)))
                        fd.nrvo_can = false;
                }
                else
                    fd.nrvo_can = false;
            }
            else
            {
                fd.nrvo_can = false;
                if (fd.inferRetType)
                {
                    if ((tf.next.value != null) && ((tf.next.value.ty.value & 0xFF) != ENUMTY.Tvoid))
                    {
                        if (((tf.next.value.ty.value & 0xFF) != ENUMTY.Terror))
                        {
                            rs.error(new BytePtr("mismatched function return type inference of `void` and `%s`"), tf.next.value.toChars());
                        }
                        errors = true;
                        tf.next.value = Type.terror.value;
                    }
                    else
                        tf.next.value = Type.tvoid.value;
                    tret = tf.next.value;
                    tbret = tret.toBasetype();
                }
                if (inferRef)
                    tf.isref.value = false;
                if (((tbret.ty.value & 0xFF) != ENUMTY.Tvoid))
                {
                    if (((tbret.ty.value & 0xFF) != ENUMTY.Terror))
                        rs.error(new BytePtr("`return` expression expected"));
                    errors = true;
                }
                else if (fd.isMain())
                {
                    rs.exp.value = literal_B6589FC6AB0DC82C();
                }
            }
            if ((((this.sc.value.get()).ctorflow.callSuper.value & 16) != 0) && (((this.sc.value.get()).ctorflow.callSuper.value & 3) == 0))
            {
                rs.error(new BytePtr("`return` without calling constructor"));
                errors = true;
            }
            if ((this.sc.value.get()).ctorflow.fieldinit.getLength() != 0)
            {
                AggregateDeclaration ad = fd.isMemberLocal();
                assert(ad != null);
                {
                    Slice<VarDeclaration> __r1630 = ad.fields.opSlice().copy();
                    int __key1629 = 0;
                    for (; (__key1629 < __r1630.getLength());__key1629 += 1) {
                        VarDeclaration v = __r1630.get(__key1629);
                        int i = __key1629;
                        boolean mustInit = ((v.storage_class.value & 549755813888L) != 0) || v.type.value.needsNested();
                        if (mustInit && (((this.sc.value.get()).ctorflow.fieldinit.get(i).csx.value & 1) == 0))
                        {
                            rs.error(new BytePtr("an earlier `return` statement skips field `%s` initialization"), v.toChars());
                            errors = true;
                        }
                    }
                }
            }
            (this.sc.value.get()).ctorflow.orCSX(CSX.return_);
            if (errors)
                this.setError();
                return ;
            if ((this.sc.value.get()).fes != null)
            {
                if (rs.exp.value == null)
                {
                    Statement s = new ReturnStatement(Loc.initial.value, rs.exp.value);
                    ((this.sc.value.get()).fes.cases.get()).push(s);
                    rs.exp.value = new IntegerExp((long)(((this.sc.value.get()).fes.cases.get()).length.value + 1));
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
                    fd.buildResultVar(null, rs.exp.value.type.value);
                    boolean r = fd.vresult.checkNestedReference(this.sc.value, Loc.initial.value);
                    assert(!r);
                    Statement s = new ReturnStatement(Loc.initial.value, new VarExp(Loc.initial.value, fd.vresult, true));
                    ((this.sc.value.get()).fes.cases.get()).push(s);
                    rs.caseDim = ((this.sc.value.get()).fes.cases.get()).length.value + 1;
                }
            }
            if (rs.exp.value != null)
            {
                if (fd.returns == null)
                    fd.returns = refPtr(new DArray<ReturnStatement>());
                (fd.returns.get()).push(rs);
            }
            if (e0.value != null)
            {
                if (((e0.value.op.value & 0xFF) == 38) || ((e0.value.op.value & 0xFF) == 99))
                {
                    rs.exp.value = Expression.combine(e0.value, rs.exp.value);
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
            if (bs.ident.value != null)
            {
                bs.ident.value = fixupLabelName(this.sc.value, bs.ident.value);
                FuncDeclaration thisfunc = (this.sc.value.get()).func.value;
                {
                    Ptr<Scope> scx = this.sc.value;
                    for (; scx != null;scx = (scx.get()).enclosing.value){
                        if ((!pequals((scx.get()).func.value, thisfunc)))
                        {
                            if ((this.sc.value.get()).fes != null)
                            {
                                ((this.sc.value.get()).fes.cases.get()).push(bs);
                                this.result = new ReturnStatement(Loc.initial.value, new IntegerExp((long)(((this.sc.value.get()).fes.cases.get()).length.value + 1)));
                                return ;
                            }
                            break;
                        }
                        LabelStatement ls = (scx.get()).slabel;
                        if ((ls != null) && (pequals(ls.ident, bs.ident.value)))
                        {
                            Statement s = ls.statement.value;
                            if ((s == null) || !s.hasBreak())
                                bs.error(new BytePtr("label `%s` has no `break`"), bs.ident.value.toChars());
                            else if ((!pequals(ls.tf, (this.sc.value.get()).tf)))
                                bs.error(new BytePtr("cannot break out of `finally` block"));
                            else
                            {
                                ls.breaks.value = true;
                                this.result = bs;
                                return ;
                            }
                            this.setError();
                            return ;
                        }
                    }
                }
                bs.error(new BytePtr("enclosing label `%s` for `break` not found"), bs.ident.value.toChars());
                this.setError();
                return ;
            }
            else if ((this.sc.value.get()).sbreak == null)
            {
                if (((this.sc.value.get()).os != null) && (((this.sc.value.get()).os.tok & 0xFF) != 205))
                {
                    bs.error(new BytePtr("`break` is not inside `%s` bodies"), Token.toChars((this.sc.value.get()).os.tok));
                }
                else if ((this.sc.value.get()).fes != null)
                {
                    this.result = new ReturnStatement(Loc.initial.value, literal_356A192B7913B04C());
                    return ;
                }
                else
                    bs.error(new BytePtr("`break` is not inside a loop or `switch`"));
                this.setError();
                return ;
            }
            else if ((this.sc.value.get()).sbreak.isForwardingStatement() != null)
            {
                bs.error(new BytePtr("must use labeled `break` within `static foreach`"));
            }
            this.result = bs;
        }

        public  void visit(ContinueStatement cs) {
            if (cs.ident.value != null)
            {
                cs.ident.value = fixupLabelName(this.sc.value, cs.ident.value);
                Ptr<Scope> scx = null;
                FuncDeclaration thisfunc = (this.sc.value.get()).func.value;
                {
                    scx = this.sc.value;
                    for (; scx != null;scx = (scx.get()).enclosing.value){
                        LabelStatement ls = null;
                        if ((!pequals((scx.get()).func.value, thisfunc)))
                        {
                            if ((this.sc.value.get()).fes != null)
                            {
                                for (; scx != null;scx = (scx.get()).enclosing.value){
                                    ls = (scx.get()).slabel;
                                    if ((ls != null) && (pequals(ls.ident, cs.ident.value)) && (pequals(ls.statement.value, (this.sc.value.get()).fes)))
                                    {
                                        this.result = new ReturnStatement(Loc.initial.value, literal_B6589FC6AB0DC82C());
                                        return ;
                                    }
                                }
                                ((this.sc.value.get()).fes.cases.get()).push(cs);
                                this.result = new ReturnStatement(Loc.initial.value, new IntegerExp((long)(((this.sc.value.get()).fes.cases.get()).length.value + 1)));
                                return ;
                            }
                            break;
                        }
                        ls = (scx.get()).slabel;
                        if ((ls != null) && (pequals(ls.ident, cs.ident.value)))
                        {
                            Statement s = ls.statement.value;
                            if ((s == null) || !s.hasContinue())
                                cs.error(new BytePtr("label `%s` has no `continue`"), cs.ident.value.toChars());
                            else if ((!pequals(ls.tf, (this.sc.value.get()).tf)))
                                cs.error(new BytePtr("cannot continue out of `finally` block"));
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
                cs.error(new BytePtr("enclosing label `%s` for `continue` not found"), cs.ident.value.toChars());
                this.setError();
                return ;
            }
            else if ((this.sc.value.get()).scontinue == null)
            {
                if (((this.sc.value.get()).os != null) && (((this.sc.value.get()).os.tok & 0xFF) != 205))
                {
                    cs.error(new BytePtr("`continue` is not inside `%s` bodies"), Token.toChars((this.sc.value.get()).os.tok));
                }
                else if ((this.sc.value.get()).fes != null)
                {
                    this.result = new ReturnStatement(Loc.initial.value, literal_B6589FC6AB0DC82C());
                    return ;
                }
                else
                    cs.error(new BytePtr("`continue` is not inside a loop"));
                this.setError();
                return ;
            }
            else if ((this.sc.value.get()).scontinue.isForwardingStatement() != null)
            {
                cs.error(new BytePtr("must use labeled `continue` within `static foreach`"));
            }
            this.result = cs;
        }

        public  void visit(SynchronizedStatement ss) {
            if (ss.exp != null)
            {
                ss.exp = expressionSemantic(ss.exp, this.sc.value);
                ss.exp = resolveProperties(this.sc.value, ss.exp);
                ss.exp = ss.exp.optimize(0, false);
                ss.exp = checkGC(this.sc.value, ss.exp);
                if (((ss.exp.op.value & 0xFF) == 127))
                {
                    if (ss._body.value != null)
                        ss._body.value = statementSemantic(ss._body.value, this.sc.value);
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
                    if (ClassDeclaration.object.value == null)
                    {
                        ss.error(new BytePtr("missing or corrupt object.d"));
                        fatal();
                    }
                    Type t = ClassDeclaration.object.value.type.value;
                    t = typeSemantic(t, Loc.initial.value, this.sc.value).toBasetype();
                    assert(((t.ty.value & 0xFF) == ENUMTY.Tclass));
                    ss.exp = new CastExp(ss.loc, ss.exp, t);
                    ss.exp = expressionSemantic(ss.exp, this.sc.value);
                }
                VarDeclaration tmp = copyToTemp(0L, new BytePtr("__sync"), ss.exp);
                dsymbolSemantic(tmp, this.sc.value);
                Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
                (cs.get()).push(new ExpStatement(ss.loc, tmp));
                Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>());
                (args.get()).push(new Parameter(0L, ClassDeclaration.object.value.type.value, null, null, null));
                FuncDeclaration fdenter = FuncDeclaration.genCfunc(args, Type.tvoid.value, Id.monitorenter, 0L);
                Expression e = new CallExp(ss.loc, fdenter, new VarExp(ss.loc, tmp, true));
                e.type.value = Type.tvoid.value;
                (cs.get()).push(new ExpStatement(ss.loc, e));
                FuncDeclaration fdexit = FuncDeclaration.genCfunc(args, Type.tvoid.value, Id.monitorexit, 0L);
                e = new CallExp(ss.loc, fdexit, new VarExp(ss.loc, tmp, true));
                e.type.value = Type.tvoid.value;
                Statement s = new ExpStatement(ss.loc, e);
                s = new TryFinallyStatement(ss.loc, ss._body.value, s);
                (cs.get()).push(s);
                s = new CompoundStatement(ss.loc, cs);
                this.result = statementSemantic(s, this.sc.value);
            }
            else
            {
                Identifier id = Identifier.generateId(new BytePtr("__critsec"));
                Type t = Type.tint8.sarrayOf((long)(target.ptrsize.value + target.critsecsize()));
                VarDeclaration tmp = new VarDeclaration(ss.loc, t, id, null, 0L);
                tmp.storage_class.value |= 1100048498689L;
                Expression tmpExp = new VarExp(ss.loc, tmp, true);
                Ptr<DArray<Statement>> cs = refPtr(new DArray<Statement>());
                (cs.get()).push(new ExpStatement(ss.loc, tmp));
                VarDeclaration v = new VarDeclaration(ss.loc, Type.tvoidptr, Identifier.generateId(new BytePtr("__sync")), null, 0L);
                dsymbolSemantic(v, this.sc.value);
                (cs.get()).push(new ExpStatement(ss.loc, v));
                Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>());
                (args.get()).push(new Parameter(0L, t.pointerTo(), null, null, null));
                FuncDeclaration fdenter = FuncDeclaration.genCfunc(args, Type.tvoid.value, Id.criticalenter, 33554432L);
                Expression int0 = new IntegerExp(ss.loc, 0L, Type.tint8);
                Expression e = new AddrExp(ss.loc, new IndexExp(ss.loc, tmpExp, int0));
                e = expressionSemantic(e, this.sc.value);
                e = new CallExp(ss.loc, fdenter, e);
                e.type.value = Type.tvoid.value;
                (cs.get()).push(new ExpStatement(ss.loc, e));
                FuncDeclaration fdexit = FuncDeclaration.genCfunc(args, Type.tvoid.value, Id.criticalexit, 33554432L);
                e = new AddrExp(ss.loc, new IndexExp(ss.loc, tmpExp, int0));
                e = expressionSemantic(e, this.sc.value);
                e = new CallExp(ss.loc, fdexit, e);
                e.type.value = Type.tvoid.value;
                Statement s = new ExpStatement(ss.loc, e);
                s = new TryFinallyStatement(ss.loc, ss._body.value, s);
                (cs.get()).push(s);
                s = new CompoundStatement(ss.loc, cs);
                this.result = statementSemantic(s, this.sc.value);
                tmp.alignment = target.ptrsize.value;
            }
        }

        public  void visit(WithStatement ws) {
            ScopeDsymbol sym = null;
            Initializer _init = null;
            ws.exp.value = expressionSemantic(ws.exp.value, this.sc.value);
            ws.exp.value = resolveProperties(this.sc.value, ws.exp.value);
            ws.exp.value = ws.exp.value.optimize(0, false);
            ws.exp.value = checkGC(this.sc.value, ws.exp.value);
            if (((ws.exp.value.op.value & 0xFF) == 127))
                this.setError();
                return ;
            if (((ws.exp.value.op.value & 0xFF) == 203))
            {
                sym = new WithScopeSymbol(ws);
                sym.parent.value = (this.sc.value.get()).scopesym.value;
                sym.endlinnum = ws.endloc.linnum;
            }
            else if (((ws.exp.value.op.value & 0xFF) == 20))
            {
                Dsymbol s = ((TypeExp)ws.exp.value).type.value.toDsymbol(this.sc.value);
                if ((s == null) || (s.isScopeDsymbol() == null))
                {
                    ws.error(new BytePtr("`with` type `%s` has no members"), ws.exp.value.toChars());
                    this.setError();
                    return ;
                }
                sym = new WithScopeSymbol(ws);
                sym.parent.value = (this.sc.value.get()).scopesym.value;
                sym.endlinnum = ws.endloc.linnum;
            }
            else
            {
                Type t = ws.exp.value.type.value.toBasetype();
                Expression olde = ws.exp.value;
                if (((t.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    ws.exp.value = new PtrExp(ws.loc, ws.exp.value);
                    ws.exp.value = expressionSemantic(ws.exp.value, this.sc.value);
                    t = ws.exp.value.type.value.toBasetype();
                }
                assert(t != null);
                t = t.toBasetype();
                if (t.isClassHandle() != null)
                {
                    _init = new ExpInitializer(ws.loc, ws.exp.value);
                    ws.wthis = new VarDeclaration(ws.loc, ws.exp.value.type.value, Id.withSym.value, _init, 0L);
                    dsymbolSemantic(ws.wthis, this.sc.value);
                    sym = new WithScopeSymbol(ws);
                    sym.parent.value = (this.sc.value.get()).scopesym.value;
                    sym.endlinnum = ws.endloc.linnum;
                }
                else if (((t.ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    if (!ws.exp.value.isLvalue())
                    {
                        VarDeclaration tmp = copyToTemp(0L, new BytePtr("__withtmp"), ws.exp.value);
                        dsymbolSemantic(tmp, this.sc.value);
                        ExpStatement es = new ExpStatement(ws.loc, tmp);
                        ws.exp.value = new VarExp(ws.loc, tmp, true);
                        Statement ss = new ScopeStatement(ws.loc, new CompoundStatement(ws.loc, slice(new Statement[]{es, ws})), ws.endloc);
                        this.result = statementSemantic(ss, this.sc.value);
                        return ;
                    }
                    Expression e = ws.exp.value.addressOf();
                    _init = new ExpInitializer(ws.loc, e);
                    ws.wthis = new VarDeclaration(ws.loc, e.type.value, Id.withSym.value, _init, 0L);
                    dsymbolSemantic(ws.wthis, this.sc.value);
                    sym = new WithScopeSymbol(ws);
                    sym.setScope(this.sc.value);
                    sym.parent.value = (this.sc.value.get()).scopesym.value;
                    sym.endlinnum = ws.endloc.linnum;
                }
                else
                {
                    ws.error(new BytePtr("`with` expressions must be aggregate types or pointers to them, not `%s`"), olde.type.value.toChars());
                    this.setError();
                    return ;
                }
            }
            if (ws._body.value != null)
            {
                sym._scope.value = this.sc.value;
                this.sc.value = (this.sc.value.get()).push(sym);
                (this.sc.value.get()).insert(sym);
                ws._body.value = statementSemantic(ws._body.value, this.sc.value);
                (this.sc.value.get()).pop();
                if ((ws._body.value != null) && (ws._body.value.isErrorStatement() != null))
                {
                    this.result = ws._body.value;
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
            tcs._body.value = semanticScope(tcs._body.value, this.sc.value, null, null);
            assert(tcs._body.value != null);
            boolean catchErrors = false;
            {
                Slice<Catch> __r1632 = (tcs.catches.get()).opSlice().copy();
                int __key1631 = 0;
                for (; (__key1631 < __r1632.getLength());__key1631 += 1) {
                    Catch c = __r1632.get(__key1631);
                    int i = __key1631;
                    catchSemantic(c, this.sc.value);
                    if (c.errors)
                    {
                        catchErrors = true;
                        continue;
                    }
                    ClassDeclaration cd = c.type.value.toBasetype().isClassHandle();
                    flags |= cd.isCPPclass() ? 1 : 2;
                    {
                        int __key1633 = 0;
                        int __limit1634 = i;
                        for (; (__key1633 < __limit1634);__key1633 += 1) {
                            int j = __key1633;
                            Catch cj = (tcs.catches.get()).get(j);
                            BytePtr si = pcopy(c.loc.toChars(global.params.showColumns.value));
                            BytePtr sj = pcopy(cj.loc.toChars(global.params.showColumns.value));
                            if (c.type.value.toBasetype().implicitConvTo(cj.type.value.toBasetype()) != 0)
                            {
                                tcs.error(new BytePtr("`catch` at %s hides `catch` at %s"), sj, si);
                                catchErrors = true;
                            }
                        }
                    }
                }
            }
            if ((this.sc.value.get()).func.value != null)
            {
                (this.sc.value.get()).func.value.flags |= FUNCFLAG.hasCatches;
                if ((flags == 3))
                {
                    tcs.error(new BytePtr("cannot mix catching D and C++ exceptions in the same try-catch"));
                    catchErrors = true;
                }
            }
            if (catchErrors)
                this.setError();
                return ;
            if (tcs._body.value.isErrorStatement() != null)
            {
                this.result = tcs._body.value;
                return ;
            }
            if (((blockExit(tcs._body.value, (this.sc.value.get()).func.value, false) & BE.throw_) == 0) && (ClassDeclaration.exception != null))
            {
                {
                    int __limit1636 = 0;
                    int __key1635 = (tcs.catches.get()).length;
                    for (; (__key1635-- > __limit1636);) {
                        int i = __key1635;
                        Catch c = (tcs.catches.get()).get(i);
                        if ((c.type.value.toBasetype().implicitConvTo(ClassDeclaration.exception.type.value) != 0) && (c.handler.value == null) || !c.handler.value.comeFrom())
                        {
                            (tcs.catches.get()).remove(i);
                        }
                    }
                }
            }
            if (((tcs.catches.get()).length == 0))
            {
                this.result = tcs._body.value.hasCode() ? tcs._body.value : null;
                return ;
            }
            this.result = tcs;
        }

        public  void visit(TryFinallyStatement tfs) {
            tfs._body.value = statementSemantic(tfs._body.value, this.sc.value);
            this.sc.value = (this.sc.value.get()).push();
            (this.sc.value.get()).tf = tfs;
            (this.sc.value.get()).sbreak = null;
            (this.sc.value.get()).scontinue = null;
            tfs.finalbody.value = semanticNoScope(tfs.finalbody.value, this.sc.value);
            (this.sc.value.get()).pop();
            if (tfs._body.value == null)
            {
                this.result = tfs.finalbody.value;
                return ;
            }
            if (tfs.finalbody.value == null)
            {
                this.result = tfs._body.value;
                return ;
            }
            int blockexit = blockExit(tfs._body.value, (this.sc.value.get()).func.value, false);
            if (!(global.params.useExceptions && (ClassDeclaration.throwable != null)))
                blockexit &= -3;
            if (((blockexit & -17) == BE.fallthru))
            {
                this.result = new CompoundStatement(tfs.loc, slice(new Statement[]{tfs._body.value, tfs.finalbody.value}));
                return ;
            }
            tfs.bodyFallsThru = (blockexit & BE.fallthru) != 0;
            this.result = tfs;
        }

        public  void visit(ScopeGuardStatement oss) {
            if (((oss.tok & 0xFF) != 204))
            {
                if (((this.sc.value.get()).os != null) && (((this.sc.value.get()).os.tok & 0xFF) != 205))
                {
                    oss.error(new BytePtr("cannot put `%s` statement inside `%s`"), Token.toChars(oss.tok), Token.toChars((this.sc.value.get()).os.tok));
                    this.setError();
                    return ;
                }
                if ((this.sc.value.get()).tf != null)
                {
                    oss.error(new BytePtr("cannot put `%s` statement inside `finally` block"), Token.toChars(oss.tok));
                    this.setError();
                    return ;
                }
            }
            this.sc.value = (this.sc.value.get()).push();
            (this.sc.value.get()).tf = null;
            (this.sc.value.get()).os = oss;
            if (((oss.tok & 0xFF) != 205))
            {
                (this.sc.value.get()).sbreak = null;
                (this.sc.value.get()).scontinue = null;
            }
            oss.statement = semanticNoScope(oss.statement, this.sc.value);
            (this.sc.value.get()).pop();
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
            FuncDeclaration fd = (this.sc.value.get()).parent.value.isFuncDeclaration();
            fd.hasReturnExp |= 2;
            if (((ts.exp.op.value & 0xFF) == 22))
            {
                NewExp ne = (NewExp)ts.exp;
                ne.thrownew = true;
            }
            ts.exp = expressionSemantic(ts.exp, this.sc.value);
            ts.exp = resolveProperties(this.sc.value, ts.exp);
            ts.exp = checkGC(this.sc.value, ts.exp);
            if (((ts.exp.op.value & 0xFF) == 127))
                this.setError();
                return ;
            checkThrowEscape(this.sc.value, ts.exp, false);
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
                this.sc.value = (this.sc.value.get()).push();
                (this.sc.value.get()).flags.value |= 8;
                ds.statement = statementSemantic(ds.statement, this.sc.value);
                (this.sc.value.get()).pop();
            }
            this.result = ds.statement;
        }

        public  void visit(GotoStatement gs) {
            FuncDeclaration fd = (this.sc.value.get()).func.value;
            gs.ident = fixupLabelName(this.sc.value, gs.ident);
            gs.label = fd.searchLabel(gs.ident);
            gs.tf = (this.sc.value.get()).tf;
            gs.os = (this.sc.value.get()).os;
            gs.lastVar = (this.sc.value.get()).lastVar;
            if ((gs.label.statement == null) && ((this.sc.value.get()).fes != null))
            {
                ScopeStatement ss = new ScopeStatement(gs.loc, gs, gs.loc);
                ((this.sc.value.get()).fes.gotos.get()).push(ss);
                this.result = ss;
                return ;
            }
            if (gs.label.statement == null)
            {
                if (fd.gotos == null)
                    fd.gotos = refPtr(new DArray<GotoStatement>());
                (fd.gotos.get()).push(gs);
            }
            else if (gs.checkLabel())
                this.setError();
                return ;
            this.result = gs;
        }

        public  void visit(LabelStatement ls) {
            FuncDeclaration fd = (this.sc.value.get()).parent.value.isFuncDeclaration();
            ls.ident = fixupLabelName(this.sc.value, ls.ident);
            ls.tf = (this.sc.value.get()).tf;
            ls.os = (this.sc.value.get()).os;
            ls.lastVar = (this.sc.value.get()).lastVar;
            LabelDsymbol ls2 = fd.searchLabel(ls.ident);
            if (ls2.statement != null)
            {
                ls.error(new BytePtr("label `%s` already defined"), ls2.toChars());
                this.setError();
                return ;
            }
            else
                ls2.statement = ls;
            this.sc.value = (this.sc.value.get()).push();
            (this.sc.value.get()).scopesym.value = ((this.sc.value.get()).enclosing.value.get()).scopesym.value;
            (this.sc.value.get()).ctorflow.orCSX(CSX.label);
            (this.sc.value.get()).slabel = ls;
            if (ls.statement.value != null)
                ls.statement.value = statementSemantic(ls.statement.value, this.sc.value);
            (this.sc.value.get()).pop();
            this.result = ls;
        }

        public  void visit(AsmStatement s) {
            this.result = asmSemantic(s, this.sc.value);
        }

        public  void visit(CompoundAsmStatement cas) {
            this.sc.value = (this.sc.value.get()).push();
            (this.sc.value.get()).stc.value |= cas.stc.value;
            {
                Slice<Statement> __r1637 = (cas.statements.get()).opSlice().copy();
                int __key1638 = 0;
                for (; (__key1638 < __r1637.getLength());__key1638 += 1) {
                    Statement s = __r1637.get(__key1638);
                    s = s != null ? statementSemantic(s, this.sc.value) : null;
                }
            }
            assert((this.sc.value.get()).func.value != null);
            int purity = PURE.impure;
            if (((cas.stc.value & 67108864L) == 0) && ((purity = (this.sc.value.get()).func.value.isPureBypassingInference()) != PURE.impure) && (purity != PURE.fwdref))
                cas.deprecation(new BytePtr("`asm` statement is assumed to be impure - mark it with `pure` if it is not"));
            if (((cas.stc.value & 4398046511104L) == 0) && (this.sc.value.get()).func.value.isNogcBypassingInference())
                cas.deprecation(new BytePtr("`asm` statement is assumed to use the GC - mark it with `@nogc` if it does not"));
            if (((cas.stc.value & 25769803776L) == 0) && (this.sc.value.get()).func.value.setUnsafe())
                cas.error(new BytePtr("`asm` statement is assumed to be `@system` - mark it with `@trusted` if it is not"));
            (this.sc.value.get()).pop();
            this.result = cas;
        }

        public  void visit(ImportStatement imps) {
            {
                int __key1639 = 0;
                int __limit1640 = (imps.imports.get()).length.value;
                for (; (__key1639 < __limit1640);__key1639 += 1) {
                    int i = __key1639;
                    Import s = (imps.imports.get()).get(i).isImport();
                    assert(s.aliasdecls.length == 0);
                    {
                        Slice<Identifier> __r1642 = s.names.opSlice().copy();
                        int __key1641 = 0;
                        for (; (__key1641 < __r1642.getLength());__key1641 += 1) {
                            Identifier name = __r1642.get(__key1641);
                            int j = __key1641;
                            Identifier _alias = s.aliases.get(j);
                            if (_alias == null)
                                _alias = name;
                            TypeIdentifier tname = new TypeIdentifier(s.loc.value, name);
                            AliasDeclaration ad = new AliasDeclaration(s.loc.value, _alias, tname);
                            ad._import.value = s;
                            s.aliasdecls.push(ad);
                        }
                    }
                    dsymbolSemantic(s, this.sc.value);
                    if ((s.mod != null))
                    {
                        dmodule.Module.addDeferredSemantic2(s);
                        (this.sc.value.get()).insert(s);
                        {
                            Slice<AliasDeclaration> __r1643 = s.aliasdecls.opSlice().copy();
                            int __key1644 = 0;
                            for (; (__key1644 < __r1643.getLength());__key1644 += 1) {
                                AliasDeclaration aliasdecl = __r1643.get(__key1644);
                                (this.sc.value.get()).insert(aliasdecl);
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
        sym.parent.value = (sc.get()).scopesym.value;
        sc = (sc.get()).push(sym);
        if (c.type.value == null)
        {
            error(c.loc, new BytePtr("`catch` statement without an exception specification is deprecated"));
            errorSupplemental(c.loc, new BytePtr("use `catch(Throwable)` for old behavior"));
            c.errors = true;
            c.type.value = getThrowable();
        }
        c.type.value = typeSemantic(c.type.value, c.loc, sc);
        if ((pequals(c.type.value, Type.terror.value)))
            c.errors = true;
        else
        {
            long stc = 0L;
            ClassDeclaration cd = c.type.value.toBasetype().isClassHandle();
            if (cd == null)
            {
                error(c.loc, new BytePtr("can only catch class objects, not `%s`"), c.type.value.toChars());
                c.errors = true;
            }
            else if (cd.isCPPclass())
            {
                if (!target.cppExceptions)
                {
                    error(c.loc, new BytePtr("catching C++ class objects not supported for this target"));
                    c.errors = true;
                }
                if (((sc.get()).func.value != null) && ((sc.get()).intypeof.value == 0) && !c.internalCatch.value && (sc.get()).func.value.setUnsafe())
                {
                    error(c.loc, new BytePtr("cannot catch C++ class objects in `@safe` code"));
                    c.errors = true;
                }
            }
            else if ((!pequals(cd, ClassDeclaration.throwable)) && !ClassDeclaration.throwable.isBaseOf(cd, null))
            {
                error(c.loc, new BytePtr("can only catch class objects derived from `Throwable`, not `%s`"), c.type.value.toChars());
                c.errors = true;
            }
            else if (((sc.get()).func.value != null) && ((sc.get()).intypeof.value == 0) && !c.internalCatch.value && (ClassDeclaration.exception != null) && (!pequals(cd, ClassDeclaration.exception)) && !ClassDeclaration.exception.isBaseOf(cd, null) && (sc.get()).func.value.setUnsafe())
            {
                error(c.loc, new BytePtr("can only catch class objects derived from `Exception` in `@safe` code, not `%s`"), c.type.value.toChars());
                c.errors = true;
            }
            else if (global.params.ehnogc)
            {
                stc |= 524288L;
            }
            if (c.ident != null)
            {
                c.var = new VarDeclaration(c.loc, c.type.value, c.ident, null, stc);
                c.var.iscatchvar = true;
                dsymbolSemantic(c.var, sc);
                (sc.get()).insert(c.var);
                if (global.params.ehnogc && ((stc & 524288L) != 0))
                {
                    assert(c.var.edtor.value == null);
                    Loc loc = c.loc.copy();
                    Expression e = new VarExp(loc, c.var, true);
                    e = new CallExp(loc, new IdentifierExp(loc, Id._d_delThrowable), e);
                    Expression ec = new IdentifierExp(loc, Id.ctfe.value);
                    ec = new NotExp(loc, ec);
                    Statement s = new IfStatement(loc, null, ec, new ExpStatement(loc, e), null, loc);
                    c.handler.value = new TryFinallyStatement(loc, c.handler.value, s);
                }
            }
            c.handler.value = statementSemantic(c.handler.value, sc);
            if ((c.handler.value != null) && (c.handler.value.isErrorStatement() != null))
                c.errors = true;
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
        sym.parent.value = (sc.get()).scopesym.value;
        Ptr<Scope> scd = (sc.get()).push(sym);
        if (sbreak != null)
            (scd.get()).sbreak = sbreak;
        if (scontinue != null)
            (scd.get()).scontinue = scontinue;
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
