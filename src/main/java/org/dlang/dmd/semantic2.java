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
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.blockexit.*;
import static org.dlang.dmd.clone.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.dversion.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nogc.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.statementsem.*;
import static org.dlang.dmd.staticassert.*;
import static org.dlang.dmd.staticcond.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.templateparamsem.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.visitor.*;

public class semantic2 {

    static boolean LOG = false;
    public static void semantic2(Dsymbol dsym, Ptr<Scope> sc) {
        Semantic2Visitor v = new Semantic2Visitor(sc);
        dsym.accept(v);
    }

    public static class Semantic2Visitor extends Visitor
    {
        public Ptr<Scope> sc = null;
        public  Semantic2Visitor(Ptr<Scope> sc) {
            this.sc = sc;
        }

        public  void visit(Dsymbol _param_0) {
        }

        public  void visit(StaticAssert sa) {
            ScopeDsymbol sds = new ScopeDsymbol();
            this.sc = (this.sc.get()).push(sds);
            (this.sc.get()).tinst = null;
            (this.sc.get()).minst = null;
            Ref<Boolean> errors = ref(false);
            boolean result = evalStaticCondition(this.sc, sa.exp, sa.exp, errors);
            this.sc = (this.sc.get()).pop();
            if (errors.value)
            {
                errorSupplemental(sa.loc, new BytePtr("while evaluating: `static assert(%s)`"), sa.exp.toChars());
            }
            else if (!result)
            {
                if (sa.msg != null)
                {
                    this.sc = (this.sc.get()).startCTFE();
                    sa.msg = expressionSemantic(sa.msg, this.sc);
                    sa.msg = resolveProperties(this.sc, sa.msg);
                    this.sc = (this.sc.get()).endCTFE();
                    sa.msg = sa.msg.ctfeInterpret();
                    {
                        StringExp se = sa.msg.toStringExp();
                        if ((se) != null)
                        {
                            se = se.toUTF8(this.sc);
                            error(sa.loc, new BytePtr("static assert:  \"%.*s\""), se.len, se.string);
                        }
                        else
                            error(sa.loc, new BytePtr("static assert:  %s"), sa.msg.toChars());
                    }
                }
                else
                    error(sa.loc, new BytePtr("static assert:  `%s` is false"), sa.exp.toChars());
                if ((this.sc.get()).tinst != null)
                    (this.sc.get()).tinst.printInstantiationTrace();
                if (global.value.gag == 0)
                    fatal();
            }
        }

        public  void visit(TemplateInstance tempinst) {
            if ((tempinst.semanticRun >= PASS.semantic2))
                return ;
            tempinst.semanticRun = PASS.semantic2;
            if (!tempinst.errors && (tempinst.members != null))
            {
                TemplateDeclaration tempdecl = tempinst.tempdecl.isTemplateDeclaration();
                assert(tempdecl != null);
                this.sc = tempdecl._scope;
                assert(this.sc != null);
                this.sc = (this.sc.get()).push(tempinst.argsym);
                this.sc = (this.sc.get()).push(tempinst);
                (this.sc.get()).tinst = tempinst;
                (this.sc.get()).minst = tempinst.minst;
                int needGagging = ((tempinst.gagged && (global.value.gag == 0)) ? 1 : 0);
                int olderrors = global.value.errors;
                int oldGaggedErrors = -1;
                if (needGagging != 0)
                    oldGaggedErrors = global.value.startGagging();
                {
                    int i = 0;
                    for (; (i < (tempinst.members.get()).length);i++){
                        Dsymbol s = (tempinst.members.get()).get(i);
                        semantic2(s, this.sc);
                        if (tempinst.gagged && (global.value.errors != olderrors))
                            break;
                    }
                }
                if ((global.value.errors != olderrors))
                {
                    if (!tempinst.errors)
                    {
                        if (!tempdecl.literal)
                            tempinst.error(tempinst.loc, new BytePtr("error instantiating"));
                        if (tempinst.tinst != null)
                            tempinst.tinst.printInstantiationTrace();
                    }
                    tempinst.errors = true;
                }
                if (needGagging != 0)
                    global.value.endGagging(oldGaggedErrors);
                this.sc = (this.sc.get()).pop();
                (this.sc.get()).pop();
            }
        }

        public  void visit(TemplateMixin tmix) {
            if ((tmix.semanticRun >= PASS.semantic2))
                return ;
            tmix.semanticRun = PASS.semantic2;
            if (tmix.members != null)
            {
                assert(this.sc != null);
                this.sc = (this.sc.get()).push(tmix.argsym);
                this.sc = (this.sc.get()).push(tmix);
                {
                    int i = 0;
                    for (; (i < (tmix.members.get()).length);i++){
                        Dsymbol s = (tmix.members.get()).get(i);
                        semantic2(s, this.sc);
                    }
                }
                this.sc = (this.sc.get()).pop();
                (this.sc.get()).pop();
            }
        }

        public  void visit(VarDeclaration vd) {
            if ((vd.semanticRun < PASS.semanticdone) && (vd.inuse != 0))
                return ;
            if (vd.aliassym != null)
            {
                vd.aliassym.accept(this);
                vd.semanticRun = PASS.semantic2done;
                return ;
            }
            if ((vd._init != null) && (vd.toParent().isFuncDeclaration() == null))
            {
                vd.inuse++;
                vd._init = initializerSemantic(vd._init, this.sc, vd.type, ((this.sc.get()).intypeof == 1) ? NeedInterpret.INITnointerpret : NeedInterpret.INITinterpret);
                vd.inuse--;
            }
            if ((vd._init != null) && ((vd.storage_class & 8388608L) != 0))
            {
                {
                    ExpInitializer ei = vd._init.isExpInitializer();
                    if ((ei) != null)
                    {
                        Function1<Expression,Boolean> hasInvalidEnumInitializer = new Function1<Expression,Boolean>(){
                            public Boolean invoke(Expression e) {
                                Ref<Expression> e_ref = ref(e);
                                Function1<Ptr<DArray<Expression>>,Boolean> arrayHasInvalidEnumInitializer = new Function1<Ptr<DArray<Expression>>,Boolean>(){
                                    public Boolean invoke(Ptr<DArray<Expression>> elems) {
                                        Ref<Ptr<DArray<Expression>>> elems_ref = ref(elems);
                                        {
                                            Ref<Slice<Expression>> __r1537 = ref((elems_ref.value.get()).opSlice().copy());
                                            IntRef __key1538 = ref(0);
                                            for (; (__key1538.value < __r1537.value.getLength());__key1538.value += 1) {
                                                Ref<Expression> e = ref(__r1537.value.get(__key1538.value));
                                                if ((e.value != null) && hasInvalidEnumInitializer.invoke(e.value))
                                                    return true;
                                            }
                                        }
                                        return false;
                                    }
                                };
                                if (((e_ref.value.op & 0xFF) == 50))
                                    return true;
                                if (((e_ref.value.op & 0xFF) == 19) && ((((AddrExp)e_ref.value).e1.op & 0xFF) == 49))
                                    return true;
                                if (((e_ref.value.op & 0xFF) == 47))
                                    return arrayHasInvalidEnumInitializer.invoke(((ArrayLiteralExp)e_ref.value).elements);
                                if (((e_ref.value.op & 0xFF) == 49))
                                    return arrayHasInvalidEnumInitializer.invoke(((StructLiteralExp)e_ref.value).elements);
                                if (((e_ref.value.op & 0xFF) == 48))
                                {
                                    Ref<AssocArrayLiteralExp> ae = ref((AssocArrayLiteralExp)e_ref.value);
                                    return arrayHasInvalidEnumInitializer.invoke(ae.value.values) || arrayHasInvalidEnumInitializer.invoke(ae.value.keys);
                                }
                                return false;
                            }
                        };
                        if (hasInvalidEnumInitializer.invoke(ei.exp))
                            vd.error(new BytePtr(": Unable to initialize enum with class or pointer to struct. Use static const variable instead."));
                    }
                }
            }
            else if ((vd._init != null) && vd.isThreadlocal())
            {
                if (((vd.type.ty & 0xFF) == ENUMTY.Tclass) && vd.type.isMutable() && !vd.type.isShared())
                {
                    ExpInitializer ei = vd._init.isExpInitializer();
                    if ((ei != null) && ((ei.exp.op & 0xFF) == 50))
                        vd.error(new BytePtr("is a thread-local class and cannot have a static initializer. Use `static this()` to initialize instead."));
                }
                else if (((vd.type.ty & 0xFF) == ENUMTY.Tpointer) && ((vd.type.nextOf().ty & 0xFF) == ENUMTY.Tstruct) && vd.type.nextOf().isMutable() && !vd.type.nextOf().isShared())
                {
                    ExpInitializer ei = vd._init.isExpInitializer();
                    if ((ei != null) && ((ei.exp.op & 0xFF) == 19) && ((((AddrExp)ei.exp).e1.op & 0xFF) == 49))
                        vd.error(new BytePtr("is a thread-local pointer to struct and cannot have a static initializer. Use `static this()` to initialize instead."));
                }
            }
            vd.semanticRun = PASS.semantic2done;
        }

        public  void visit(dmodule.Module mod) {
            if ((mod.semanticRun != PASS.semanticdone))
                return ;
            mod.semanticRun = PASS.semantic2;
            Ptr<Scope> sc = Scope.createGlobal(mod);
            {
                int i = 0;
                for (; (i < (mod.members.get()).length);i++){
                    Dsymbol s = (mod.members.get()).get(i);
                    semantic2(s, sc);
                }
            }
            if (mod.userAttribDecl != null)
            {
                semantic2(mod.userAttribDecl, sc);
            }
            sc = (sc.get()).pop();
            (sc.get()).pop();
            mod.semanticRun = PASS.semantic2done;
        }

        public  void visit(FuncDeclaration fd) {
            if ((fd.semanticRun >= PASS.semantic2done))
                return ;
            assert((fd.semanticRun <= PASS.semantic2));
            fd.semanticRun = PASS.semantic2;
            if (0 != 0)
                if ((fd.overnext != null) && !fd.errors)
                {
                    Ref<OutBuffer> buf1 = ref(new OutBuffer());
                    try {
                        Ref<OutBuffer> buf2 = ref(new OutBuffer());
                        try {
                            FuncDeclaration f1 = fd;
                            mangleToFuncSignature(buf1, f1);
                            Function1<Dsymbol,Integer> __lambda2 = new Function1<Dsymbol,Integer>(){
                                public Integer invoke(Dsymbol s) {
                                    FuncDeclaration f2 = s.isFuncDeclaration();
                                    if ((f2 == null) || (pequals(f1, f2)) || f2.errors)
                                        return 0;
                                    if ((((f1.fbody != null) ? 1 : 0) != ((f2.fbody != null) ? 1 : 0)))
                                        return 0;
                                    if (f1.overrides(f2) != 0)
                                        return 0;
                                    if ((pequals(f1.ident, f2.ident)) && (pequals(f1.toParent2(), f2.toParent2())) && (f1.linkage != LINK.d) && (f1.linkage != LINK.cpp) && (f2.linkage != LINK.d) && (f2.linkage != LINK.cpp))
                                    {
                                        if ((f1.fbody == null) || (f2.fbody == null))
                                            return 0;
                                        TypeFunction tf1 = (TypeFunction)f1.type;
                                        TypeFunction tf2 = (TypeFunction)f2.type;
                                        error(f2.loc, new BytePtr("%s `%s%s` cannot be overloaded with %s`extern(%s)` function at %s"), f2.kind(), f2.toPrettyChars(false), parametersTypeToChars(tf2.parameterList), (f1.linkage == f2.linkage) ? new BytePtr("another ") : new BytePtr(""), linkageToChars(f1.linkage), f1.loc.toChars(global.value.params.showColumns));
                                        f2.type = Type.terror.value;
                                        f2.errors = true;
                                        return 0;
                                    }
                                    buf2.value.reset();
                                    mangleToFuncSignature(buf2, f2);
                                    BytePtr s1 = pcopy(buf1.value.peekChars());
                                    BytePtr s2 = pcopy(buf2.value.peekChars());
                                    if ((strcmp(s1, s2) == 0))
                                    {
                                        TypeFunction tf2 = (TypeFunction)f2.type;
                                        error(f2.loc, new BytePtr("%s `%s%s` conflicts with previous declaration at %s"), f2.kind(), f2.toPrettyChars(false), parametersTypeToChars(tf2.parameterList), f1.loc.toChars(global.value.params.showColumns));
                                        f2.type = Type.terror.value;
                                        f2.errors = true;
                                    }
                                    return 0;
                                }
                            };
                            overloadApply(f1, __lambda2, null);
                        }
                        finally {
                        }
                    }
                    finally {
                    }
                }
            if ((fd.type == null) || ((fd.type.ty & 0xFF) != ENUMTY.Tfunction))
                return ;
            TypeFunction f = (TypeFunction)fd.type;
            {
                int __key1539 = 0;
                int __limit1540 = f.parameterList.length();
                for (; (__key1539 < __limit1540);__key1539 += 1) {
                    int i = __key1539;
                    Parameter param = f.parameterList.get(i);
                    if ((param != null) && (param.userAttribDecl != null))
                        semantic2(param.userAttribDecl, this.sc);
                }
            }
        }

        public  void visit(Import i) {
            if (i.mod != null)
            {
                semantic2(i.mod, null);
                if (i.mod.needmoduleinfo != 0)
                {
                    if (this.sc != null)
                        (this.sc.get())._module.needmoduleinfo = 1;
                }
            }
        }

        public  void visit(Nspace ns) {
            if ((ns.semanticRun >= PASS.semantic2))
                return ;
            ns.semanticRun = PASS.semantic2;
            if (ns.members != null)
            {
                assert(this.sc != null);
                this.sc = (this.sc.get()).push(ns);
                (this.sc.get()).linkage = LINK.cpp;
                {
                    Slice<Dsymbol> __r1541 = (ns.members.get()).opSlice().copy();
                    int __key1542 = 0;
                    for (; (__key1542 < __r1541.getLength());__key1542 += 1) {
                        Dsymbol s = __r1541.get(__key1542);
                        semantic2(s, this.sc);
                    }
                }
                (this.sc.get()).pop();
            }
        }

        public  void visit(AttribDeclaration ad) {
            Ptr<DArray<Dsymbol>> d = ad.include(this.sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = ad.newScope(this.sc);
                {
                    int i = 0;
                    for (; (i < (d.get()).length);i++){
                        Dsymbol s = (d.get()).get(i);
                        semantic2(s, sc2);
                    }
                }
                if ((sc2 != this.sc))
                    (sc2.get()).pop();
            }
        }

        public  void visit(DeprecatedDeclaration dd) {
            getMessage(dd);
            this.visit((AttribDeclaration)dd);
        }

        public  void visit(AlignDeclaration ad) {
            getAlignment(ad, this.sc);
            this.visit((AttribDeclaration)ad);
        }

        public  void visit(UserAttributeDeclaration uad) {
            if ((uad.decl != null) && (uad.atts != null) && ((uad.atts.get()).length != 0) && (uad._scope != null))
            {
                Function2<Ptr<Scope>,Ptr<DArray<Expression>>,Void> eval = new Function2<Ptr<Scope>,Ptr<DArray<Expression>>,Void>(){
                    public Void invoke(Ptr<Scope> sc, Ptr<DArray<Expression>> exps) {
                        Ref<Ptr<Scope>> sc_ref = ref(sc);
                        Ref<Ptr<DArray<Expression>>> exps_ref = ref(exps);
                        {
                            Ref<Slice<Expression>> __r1543 = ref((exps_ref.value.get()).opSlice().copy());
                            IntRef __key1544 = ref(0);
                            for (; (__key1544.value < __r1543.value.getLength());__key1544.value += 1) {
                                Ref<Expression> e = ref(__r1543.value.get(__key1544.value));
                                if (e.value != null)
                                {
                                    e.value = expressionSemantic(e.value, sc_ref.value);
                                    if (definitelyValueParameter(e.value))
                                        e.value = e.value.ctfeInterpret();
                                    if (((e.value.op & 0xFF) == 126))
                                    {
                                        Ref<TupleExp> te = ref((TupleExp)e.value);
                                        eval.invoke(sc_ref.value, te.value.exps);
                                    }
                                }
                            }
                        }
                    }
                };
                uad._scope = null;
                eval.invoke(this.sc, uad.atts);
            }
            this.visit((AttribDeclaration)uad);
        }

        public  void visit(AggregateDeclaration ad) {
            if (ad.members == null)
                return ;
            if (ad._scope != null)
            {
                ad.error(new BytePtr("has forward references"));
                return ;
            }
            Ptr<Scope> sc2 = ad.newScope(this.sc);
            ad.determineSize(ad.loc);
            {
                int i = 0;
                for (; (i < (ad.members.get()).length);i++){
                    Dsymbol s = (ad.members.get()).get(i);
                    semantic2(s, sc2);
                }
            }
            (sc2.get()).pop();
        }


        public Semantic2Visitor() {}

        public Semantic2Visitor copy() {
            Semantic2Visitor that = new Semantic2Visitor();
            that.sc = this.sc;
            return that;
        }
    }
}
