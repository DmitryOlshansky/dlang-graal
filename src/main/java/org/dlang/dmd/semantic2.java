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
    // Erasure: semantic2<Dsymbol, Ptr>
    public static void semantic2(Dsymbol dsym, Ptr<Scope> sc) {
        Semantic2Visitor v = new Semantic2Visitor(sc);
        dsym.accept(v);
    }

    public static class Semantic2Visitor extends Visitor
    {
        public Ptr<Scope> sc = null;
        // Erasure: __ctor<Ptr>
        public  Semantic2Visitor(Ptr<Scope> sc) {
            this.sc = pcopy(sc);
        }

        // Erasure: visit<Dsymbol>
        public  void visit(Dsymbol _param_0) {
        }

        // Erasure: visit<StaticAssert>
        public  void visit(StaticAssert sa) {
            ScopeDsymbol sds = new ScopeDsymbol();
            this.sc = pcopy((this.sc.get()).push(sds));
            (this.sc.get()).tinst = null;
            (this.sc.get()).minst = null;
            Ref<Boolean> errors = ref(false);
            boolean result = evalStaticCondition(this.sc, sa.exp, sa.exp, errors);
            this.sc = pcopy((this.sc.get()).pop());
            if (errors.value)
            {
                errorSupplemental(sa.loc, new BytePtr("while evaluating: `static assert(%s)`"), sa.exp.toChars());
            }
            else if (!result)
            {
                if (sa.msg != null)
                {
                    this.sc = pcopy((this.sc.get()).startCTFE());
                    sa.msg = expressionSemantic(sa.msg, this.sc);
                    sa.msg = resolveProperties(this.sc, sa.msg);
                    this.sc = pcopy((this.sc.get()).endCTFE());
                    sa.msg = sa.msg.ctfeInterpret();
                    {
                        StringExp se = sa.msg.toStringExp();
                        if ((se) != null)
                        {
                            se = se.toUTF8(this.sc);
                            error(sa.loc, new BytePtr("static assert:  \"%.*s\""), se.len, se.string);
                        }
                        else
                        {
                            error(sa.loc, new BytePtr("static assert:  %s"), sa.msg.toChars());
                        }
                    }
                }
                else
                {
                    error(sa.loc, new BytePtr("static assert:  `%s` is false"), sa.exp.toChars());
                }
                if ((this.sc.get()).tinst != null)
                {
                    (this.sc.get()).tinst.printInstantiationTrace();
                }
                if (global.gag == 0)
                {
                    fatal();
                }
            }
        }

        // Erasure: visit<TemplateInstance>
        public  void visit(TemplateInstance tempinst) {
            if ((tempinst.semanticRun >= PASS.semantic2))
            {
                return ;
            }
            tempinst.semanticRun = PASS.semantic2;
            if (!tempinst.errors && (tempinst.members != null))
            {
                TemplateDeclaration tempdecl = tempinst.tempdecl.isTemplateDeclaration();
                assert(tempdecl != null);
                this.sc = pcopy(tempdecl._scope);
                assert(this.sc != null);
                this.sc = pcopy((this.sc.get()).push(tempinst.argsym));
                this.sc = pcopy((this.sc.get()).push(tempinst));
                (this.sc.get()).tinst = tempinst;
                (this.sc.get()).minst = tempinst.minst;
                int needGagging = ((tempinst.gagged && (global.gag == 0)) ? 1 : 0);
                int olderrors = global.errors;
                int oldGaggedErrors = -1;
                if (needGagging != 0)
                {
                    oldGaggedErrors = global.startGagging();
                }
                {
                    int i = 0;
                    for (; (i < (tempinst.members).length);i++){
                        Dsymbol s = (tempinst.members).get(i);
                        semantic2(s, this.sc);
                        if (tempinst.gagged && (global.errors != olderrors))
                        {
                            break;
                        }
                    }
                }
                if ((global.errors != olderrors))
                {
                    if (!tempinst.errors)
                    {
                        if (!tempdecl.literal)
                        {
                            tempinst.error(tempinst.loc, new BytePtr("error instantiating"));
                        }
                        if (tempinst.tinst != null)
                        {
                            tempinst.tinst.printInstantiationTrace();
                        }
                    }
                    tempinst.errors = true;
                }
                if (needGagging != 0)
                {
                    global.endGagging(oldGaggedErrors);
                }
                this.sc = pcopy((this.sc.get()).pop());
                (this.sc.get()).pop();
            }
        }

        // Erasure: visit<TemplateMixin>
        public  void visit(TemplateMixin tmix) {
            if ((tmix.semanticRun >= PASS.semantic2))
            {
                return ;
            }
            tmix.semanticRun = PASS.semantic2;
            if (tmix.members != null)
            {
                assert(this.sc != null);
                this.sc = pcopy((this.sc.get()).push(tmix.argsym));
                this.sc = pcopy((this.sc.get()).push(tmix));
                {
                    int i = 0;
                    for (; (i < (tmix.members).length);i++){
                        Dsymbol s = (tmix.members).get(i);
                        semantic2(s, this.sc);
                    }
                }
                this.sc = pcopy((this.sc.get()).pop());
                (this.sc.get()).pop();
            }
        }

        // Erasure: visit<VarDeclaration>
        public  void visit(VarDeclaration vd) {
            Semantic2Visitor __self = this;
            if ((vd.semanticRun < PASS.semanticdone) && (vd.inuse != 0))
            {
                return ;
            }
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
                        Function1<Expression,Boolean> hasInvalidEnumInitializer = new Function1<Expression,Boolean>() {
                            public Boolean invoke(Expression e) {
                             {
                                Function1<DArray<Expression>,Boolean> arrayHasInvalidEnumInitializer = new Function1<DArray<Expression>,Boolean>() {
                                    public Boolean invoke(DArray<Expression> elems) {
                                     {
                                        {
                                            Slice<Expression> __r1565 = (elems).opSlice().copy();
                                            Ref<Integer> __key1566 = ref(0);
                                            for (; (__key1566.value < __r1565.getLength());__key1566.value += 1) {
                                                Expression e = __r1565.get(__key1566.value);
                                                if ((e != null) && hasInvalidEnumInitializer.invoke(e))
                                                {
                                                    return true;
                                                }
                                            }
                                        }
                                        return false;
                                    }}

                                };
                                if (((e.op & 0xFF) == 50))
                                {
                                    return true;
                                }
                                if (((e.op & 0xFF) == 19) && (((((AddrExp)e)).e1.value.op & 0xFF) == 49))
                                {
                                    return true;
                                }
                                if (((e.op & 0xFF) == 47))
                                {
                                    return arrayHasInvalidEnumInitializer.invoke((((ArrayLiteralExp)e)).elements);
                                }
                                if (((e.op & 0xFF) == 49))
                                {
                                    return arrayHasInvalidEnumInitializer.invoke((((StructLiteralExp)e)).elements);
                                }
                                if (((e.op & 0xFF) == 48))
                                {
                                    AssocArrayLiteralExp ae = ((AssocArrayLiteralExp)e);
                                    return arrayHasInvalidEnumInitializer.invoke(ae.values) || arrayHasInvalidEnumInitializer.invoke(ae.keys);
                                }
                                return false;
                            }}

                        };
                        if (hasInvalidEnumInitializer.invoke(ei.exp))
                        {
                            vd.error(new BytePtr(": Unable to initialize enum with class or pointer to struct. Use static const variable instead."));
                        }
                    }
                }
            }
            else if ((vd._init != null) && vd.isThreadlocal())
            {
                if (((vd.type.ty & 0xFF) == ENUMTY.Tclass) && vd.type.isMutable() && !vd.type.isShared())
                {
                    ExpInitializer ei = vd._init.isExpInitializer();
                    if ((ei != null) && ((ei.exp.op & 0xFF) == 50))
                    {
                        vd.error(new BytePtr("is a thread-local class and cannot have a static initializer. Use `static this()` to initialize instead."));
                    }
                }
                else if (((vd.type.ty & 0xFF) == ENUMTY.Tpointer) && ((vd.type.nextOf().ty & 0xFF) == ENUMTY.Tstruct) && vd.type.nextOf().isMutable() && !vd.type.nextOf().isShared())
                {
                    ExpInitializer ei = vd._init.isExpInitializer();
                    if ((ei != null) && ((ei.exp.op & 0xFF) == 19) && (((((AddrExp)ei.exp)).e1.value.op & 0xFF) == 49))
                    {
                        vd.error(new BytePtr("is a thread-local pointer to struct and cannot have a static initializer. Use `static this()` to initialize instead."));
                    }
                }
            }
            vd.semanticRun = PASS.semantic2done;
        }

        // Erasure: visit<Module>
        public  void visit(dmodule.Module mod) {
            if ((mod.semanticRun != PASS.semanticdone))
            {
                return ;
            }
            mod.semanticRun = PASS.semantic2;
            Ptr<Scope> sc = Scope.createGlobal(mod);
            {
                int i = 0;
                for (; (i < (mod.members).length);i++){
                    Dsymbol s = (mod.members).get(i);
                    semantic2(s, sc);
                }
            }
            if (mod.userAttribDecl != null)
            {
                semantic2(mod.userAttribDecl, sc);
            }
            sc = pcopy((sc.get()).pop());
            (sc.get()).pop();
            mod.semanticRun = PASS.semantic2done;
        }

        // Erasure: visit<FuncDeclaration>
        public  void visit(FuncDeclaration fd) {
            Semantic2Visitor __self = this;
            if ((fd.semanticRun >= PASS.semantic2done))
            {
                return ;
            }
            assert((fd.semanticRun <= PASS.semantic2));
            fd.semanticRun = PASS.semantic2;
            if (0 != 0)
            {
                if ((fd.overnext != null) && !fd.errors)
                {
                    Ref<OutBuffer> buf1 = ref(new OutBuffer());
                    try {
                        Ref<OutBuffer> buf2 = ref(new OutBuffer());
                        try {
                            FuncDeclaration f1 = fd;
                            mangleToFuncSignature(buf1, f1);
                            Function1<Dsymbol,Integer> __lambda2 = new Function1<Dsymbol,Integer>() {
                                public Integer invoke(Dsymbol s) {
                                 {
                                    FuncDeclaration f2 = s.isFuncDeclaration();
                                    if ((f2 == null) || (pequals(f1, f2)) || f2.errors)
                                    {
                                        return 0;
                                    }
                                    if ((((f1.fbody.value != null) ? 1 : 0) != ((f2.fbody.value != null) ? 1 : 0)))
                                    {
                                        return 0;
                                    }
                                    if (f1.overrides(f2) != 0)
                                    {
                                        return 0;
                                    }
                                    if ((pequals(f1.ident, f2.ident)) && (pequals(f1.toParent2(), f2.toParent2())) && (f1.linkage != LINK.d) && (f1.linkage != LINK.cpp) && (f2.linkage != LINK.d) && (f2.linkage != LINK.cpp))
                                    {
                                        if ((f1.fbody.value == null) || (f2.fbody.value == null))
                                        {
                                            return 0;
                                        }
                                        TypeFunction tf1 = ((TypeFunction)f1.type);
                                        TypeFunction tf2 = ((TypeFunction)f2.type);
                                        error(f2.loc, new BytePtr("%s `%s%s` cannot be overloaded with %s`extern(%s)` function at %s"), f2.kind(), f2.toPrettyChars(false), parametersTypeToChars(tf2.parameterList), (f1.linkage == f2.linkage) ? new BytePtr("another ") : new BytePtr(""), linkageToChars(f1.linkage), f1.loc.toChars(global.params.showColumns));
                                        f2.type = Type.terror;
                                        f2.errors = true;
                                        return 0;
                                    }
                                    buf2.value.reset();
                                    mangleToFuncSignature(buf2, f2);
                                    BytePtr s1 = pcopy(buf1.value.peekChars());
                                    BytePtr s2 = pcopy(buf2.value.peekChars());
                                    if ((strcmp(s1, s2) == 0))
                                    {
                                        TypeFunction tf2 = ((TypeFunction)f2.type);
                                        error(f2.loc, new BytePtr("%s `%s%s` conflicts with previous declaration at %s"), f2.kind(), f2.toPrettyChars(false), parametersTypeToChars(tf2.parameterList), f1.loc.toChars(global.params.showColumns));
                                        f2.type = Type.terror;
                                        f2.errors = true;
                                    }
                                    return 0;
                                }}

                            };
                            overloadApply(f1, __lambda2, null);
                        }
                        finally {
                        }
                    }
                    finally {
                    }
                }
            }
            if ((fd.type == null) || ((fd.type.ty & 0xFF) != ENUMTY.Tfunction))
            {
                return ;
            }
            TypeFunction f = ((TypeFunction)fd.type);
            {
                int __key1567 = 0;
                int __limit1568 = f.parameterList.length();
                for (; (__key1567 < __limit1568);__key1567 += 1) {
                    int i = __key1567;
                    Parameter param = f.parameterList.get(i);
                    if ((param != null) && (param.userAttribDecl != null))
                    {
                        semantic2(param.userAttribDecl, this.sc);
                    }
                }
            }
        }

        // Erasure: visit<Import>
        public  void visit(Import i) {
            if (i.mod != null)
            {
                semantic2(i.mod, null);
                if (i.mod.needmoduleinfo != 0)
                {
                    if (this.sc != null)
                    {
                        (this.sc.get())._module.needmoduleinfo = 1;
                    }
                }
            }
        }

        // Erasure: visit<Nspace>
        public  void visit(Nspace ns) {
            if ((ns.semanticRun >= PASS.semantic2))
            {
                return ;
            }
            ns.semanticRun = PASS.semantic2;
            if (ns.members != null)
            {
                assert(this.sc != null);
                this.sc = pcopy((this.sc.get()).push(ns));
                (this.sc.get()).linkage = LINK.cpp;
                {
                    Slice<Dsymbol> __r1569 = (ns.members).opSlice().copy();
                    int __key1570 = 0;
                    for (; (__key1570 < __r1569.getLength());__key1570 += 1) {
                        Dsymbol s = __r1569.get(__key1570);
                        semantic2(s, this.sc);
                    }
                }
                (this.sc.get()).pop();
            }
        }

        // Erasure: visit<AttribDeclaration>
        public  void visit(AttribDeclaration ad) {
            DArray<Dsymbol> d = ad.include(this.sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = ad.newScope(this.sc);
                {
                    int i = 0;
                    for (; (i < (d).length);i++){
                        Dsymbol s = (d).get(i);
                        semantic2(s, sc2);
                    }
                }
                if ((sc2 != this.sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        // Erasure: visit<DeprecatedDeclaration>
        public  void visit(DeprecatedDeclaration dd) {
            getMessage(dd);
            this.visit((AttribDeclaration)dd);
        }

        // Erasure: visit<AlignDeclaration>
        public  void visit(AlignDeclaration ad) {
            getAlignment(ad, this.sc);
            this.visit((AttribDeclaration)ad);
        }

        // Erasure: visit<UserAttributeDeclaration>
        public  void visit(UserAttributeDeclaration uad) {
            Semantic2Visitor __self = this;
            if ((uad.decl != null) && (uad.atts != null) && ((uad.atts).length != 0) && (uad._scope != null))
            {
                Runnable2<Ptr<Scope>,DArray<Expression>> eval = new Runnable2<Ptr<Scope>,DArray<Expression>>() {
                    public Void invoke(Ptr<Scope> sc, DArray<Expression> exps) {
                     {
                        {
                            Slice<Expression> __r1571 = (exps).opSlice().copy();
                            Ref<Integer> __key1572 = ref(0);
                            for (; (__key1572.value < __r1571.getLength());__key1572.value += 1) {
                                Ref<Expression> e = ref(__r1571.get(__key1572.value));
                                if (e.value != null)
                                {
                                    e.value = expressionSemantic(e.value, sc);
                                    if (definitelyValueParameter(e.value))
                                    {
                                        e.value = e.value.ctfeInterpret();
                                    }
                                    if (((e.value.op & 0xFF) == 126))
                                    {
                                        TupleExp te = ((TupleExp)e.value);
                                        invoke(sc, te.exps);
                                    }
                                }
                            }
                        }
                        return null;
                    }}

                };
                uad._scope = null;
                eval.invoke(this.sc, uad.atts);
            }
            this.visit((AttribDeclaration)uad);
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(AggregateDeclaration ad) {
            if (ad.members == null)
            {
                return ;
            }
            if (ad._scope != null)
            {
                ad.error(new BytePtr("has forward references"));
                return ;
            }
            Ptr<Scope> sc2 = ad.newScope(this.sc);
            ad.determineSize(ad.loc);
            {
                int i = 0;
                for (; (i < (ad.members).length);i++){
                    Dsymbol s = (ad.members).get(i);
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
