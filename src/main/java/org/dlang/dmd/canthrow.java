package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.apply.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class canthrow {
    private static class CanThrow extends StoppableVisitor
    {
        private FuncDeclaration func = null;
        private boolean mustNotThrow = false;
        public  CanThrow(FuncDeclaration func, boolean mustNotThrow) {
            Ref<FuncDeclaration> func_ref = ref(func);
            Ref<Boolean> mustNotThrow_ref = ref(mustNotThrow);
            super();
            this.func = func_ref.value;
            this.mustNotThrow = mustNotThrow_ref.value;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(DeclarationExp de) {
            Ref<DeclarationExp> de_ref = ref(de);
            this.stop = Dsymbol_canThrow(de_ref.value.declaration, this.func, this.mustNotThrow);
        }

        public  void visit(CallExp ce) {
            Ref<CallExp> ce_ref = ref(ce);
            if ((global.value.errors != 0) && (ce_ref.value.e1.type.value == null))
                return ;
            if ((ce_ref.value.f != null) && (pequals(ce_ref.value.f, this.func)))
                return ;
            Ref<Type> t = ref(ce_ref.value.e1.type.value.toBasetype());
            Ref<TypeFunction> tf = ref(t.value.isTypeFunction());
            if ((tf.value != null) && tf.value.isnothrow)
                return ;
            else
            {
                Ref<TypeDelegate> td = ref(t.value.isTypeDelegate());
                if ((td.value != null) && td.value.nextOf().isTypeFunction().isnothrow)
                    return ;
            }
            if (this.mustNotThrow)
            {
                if (ce_ref.value.f != null)
                {
                    ce_ref.value.error(new BytePtr("%s `%s` is not `nothrow`"), ce_ref.value.f.kind(), ce_ref.value.f.toPrettyChars(false));
                }
                else
                {
                    Ref<Expression> e1 = ref(ce_ref.value.e1);
                    {
                        Ref<PtrExp> pe = ref(e1.value.isPtrExp());
                        if ((pe.value) != null)
                            e1.value = pe.value.e1;
                    }
                    ce_ref.value.error(new BytePtr("`%s` is not `nothrow`"), e1.value.toChars());
                }
            }
            this.stop = true;
        }

        public  void visit(NewExp ne) {
            Ref<NewExp> ne_ref = ref(ne);
            if (ne_ref.value.member != null)
            {
                if (ne_ref.value.allocator != null)
                {
                    Ref<TypeFunction> tf = ref(ne_ref.value.allocator.type.toBasetype().isTypeFunction());
                    if ((tf.value != null) && !tf.value.isnothrow)
                    {
                        if (this.mustNotThrow)
                        {
                            ne_ref.value.error(new BytePtr("%s `%s` is not `nothrow`"), ne_ref.value.allocator.kind(), ne_ref.value.allocator.toPrettyChars(false));
                        }
                        this.stop = true;
                    }
                }
                Ref<TypeFunction> tf = ref(ne_ref.value.member.type.toBasetype().isTypeFunction());
                if ((tf.value != null) && !tf.value.isnothrow)
                {
                    if (this.mustNotThrow)
                    {
                        ne_ref.value.error(new BytePtr("%s `%s` is not `nothrow`"), ne_ref.value.member.kind(), ne_ref.value.member.toPrettyChars(false));
                    }
                    this.stop = true;
                }
            }
        }

        public  void visit(DeleteExp de) {
            Ref<DeleteExp> de_ref = ref(de);
            Ref<Type> tb = ref(de_ref.value.e1.type.value.toBasetype());
            Ref<AggregateDeclaration> ad = ref(null);
            switch ((tb.value.ty & 0xFF))
            {
                case 7:
                    ad.value = tb.value.isTypeClass().sym;
                    break;
                case 3:
                case 0:
                    Ref<TypeStruct> ts = ref(tb.value.nextOf().baseElemOf().isTypeStruct());
                    if (ts.value == null)
                        return ;
                    ad.value = ts.value.sym;
                    break;
                default:
                return ;
            }
            if (ad.value.dtor != null)
            {
                Ref<TypeFunction> tf = ref(ad.value.dtor.type.toBasetype().isTypeFunction());
                if ((tf.value != null) && !tf.value.isnothrow)
                {
                    if (this.mustNotThrow)
                    {
                        de_ref.value.error(new BytePtr("%s `%s` is not `nothrow`"), ad.value.dtor.kind(), ad.value.dtor.toPrettyChars(false));
                    }
                    this.stop = true;
                }
            }
            if ((ad.value.aggDelete != null) && ((tb.value.ty & 0xFF) != ENUMTY.Tarray))
            {
                Ref<TypeFunction> tf = ref(ad.value.aggDelete.type.isTypeFunction());
                if ((tf.value != null) && !tf.value.isnothrow)
                {
                    if (this.mustNotThrow)
                    {
                        de_ref.value.error(new BytePtr("%s `%s` is not `nothrow`"), ad.value.aggDelete.kind(), ad.value.aggDelete.toPrettyChars(false));
                    }
                    this.stop = true;
                }
            }
        }

        public  void visit(AssignExp ae) {
            Ref<AssignExp> ae_ref = ref(ae);
            if (((ae_ref.value.op & 0xFF) == 96))
                return ;
            Ref<Type> t = ref(null);
            if (((ae_ref.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!ae_ref.value.e2.value.isLvalue())
                    return ;
                t.value = ae_ref.value.type.value;
            }
            else {
                Ref<SliceExp> se = ref(ae_ref.value.e1.value.isSliceExp());
                if ((se.value) != null)
                    t.value = se.value.e1.type.value;
                else
                    return ;
            }
            Ref<TypeStruct> ts = ref(t.value.baseElemOf().isTypeStruct());
            if (ts.value == null)
                return ;
            Ref<StructDeclaration> sd = ref(ts.value.sym);
            if (sd.value.postblit == null)
                return ;
            Ref<TypeFunction> tf = ref(sd.value.postblit.type.isTypeFunction());
            if ((tf.value == null) || tf.value.isnothrow)
            {
            }
            else
            {
                if (this.mustNotThrow)
                {
                    ae_ref.value.error(new BytePtr("%s `%s` is not `nothrow`"), sd.value.postblit.kind(), sd.value.postblit.toPrettyChars(false));
                }
                this.stop = true;
            }
        }

        public  void visit(NewAnonClassExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }


        public CanThrow() {}
    }

    public static boolean canThrow(Expression e, FuncDeclaration func, boolean mustNotThrow) {
        CanThrow ct = new CanThrow(func, mustNotThrow);
        return walkPostorder(e, ct);
    }

    public static boolean Dsymbol_canThrow(Dsymbol s, FuncDeclaration func, boolean mustNotThrow) {
        Ref<FuncDeclaration> func_ref = ref(func);
        Ref<Boolean> mustNotThrow_ref = ref(mustNotThrow);
        Function1<Dsymbol,Integer> symbolDg = new Function1<Dsymbol,Integer>(){
            public Integer invoke(Dsymbol s) {
                Ref<Dsymbol> s_ref = ref(s);
                return (Dsymbol_canThrow(s_ref.value, func_ref.value, mustNotThrow_ref.value) ? 1 : 0);
            }
        };
        {
            VarDeclaration vd = s.isVarDeclaration();
            if ((vd) != null)
            {
                s = s.toAlias();
                if ((!pequals(s, vd)))
                    return Dsymbol_canThrow(s, func_ref.value, mustNotThrow_ref.value);
                if ((vd.storage_class & 8388608L) != 0)
                {
                }
                else if (vd.isStatic() || ((vd.storage_class & 1207959554L) != 0))
                {
                }
                else
                {
                    if (vd._init != null)
                    {
                        {
                            ExpInitializer ie = vd._init.isExpInitializer();
                            if ((ie) != null)
                                if (canThrow(ie.exp, func_ref.value, mustNotThrow_ref.value))
                                    return true;
                        }
                    }
                    if (vd.needsScopeDtor())
                        return canThrow(vd.edtor, func_ref.value, mustNotThrow_ref.value);
                }
            }
            else {
                AttribDeclaration ad = s.isAttribDeclaration();
                if ((ad) != null)
                {
                    return foreachDsymbol(ad.include(null), symbolDg) != 0;
                }
                else {
                    TemplateMixin tm = s.isTemplateMixin();
                    if ((tm) != null)
                    {
                        return foreachDsymbol(tm.members, symbolDg) != 0;
                    }
                    else {
                        TupleDeclaration td = s.isTupleDeclaration();
                        if ((td) != null)
                        {
                            {
                                int i = 0;
                                for (; (i < (td.objects.get()).length);i++){
                                    RootObject o = (td.objects.get()).get(i);
                                    if ((o.dyncast() == DYNCAST.expression))
                                    {
                                        Expression eo = (Expression)o;
                                        {
                                            DsymbolExp se = eo.isDsymbolExp();
                                            if ((se) != null)
                                            {
                                                if (Dsymbol_canThrow(se.s, func_ref.value, mustNotThrow_ref.value))
                                                    return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
