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
        private Ref<FuncDeclaration> func = ref(null);
        private Ref<Boolean> mustNotThrow = ref(false);
        public  CanThrow(FuncDeclaration func, boolean mustNotThrow) {
            Ref<FuncDeclaration> func_ref = ref(func);
            Ref<Boolean> mustNotThrow_ref = ref(mustNotThrow);
            super();
            this.func.value = func_ref.value;
            this.mustNotThrow.value = mustNotThrow_ref.value;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(DeclarationExp de) {
            this.stop.value = Dsymbol_canThrow(de.declaration.value, this.func.value, this.mustNotThrow.value);
        }

        public  void visit(CallExp ce) {
            if ((global.errors.value != 0) && (ce.e1.value.type.value == null))
                return ;
            if ((ce.f.value != null) && (pequals(ce.f.value, this.func.value)))
                return ;
            Type t = ce.e1.value.type.value.toBasetype();
            Ref<TypeFunction> tf = ref(t.isTypeFunction());
            if ((tf.value != null) && tf.value.isnothrow.value)
                return ;
            else
            {
                Ref<TypeDelegate> td = ref(t.isTypeDelegate());
                if ((td.value != null) && td.value.nextOf().isTypeFunction().isnothrow.value)
                    return ;
            }
            if (this.mustNotThrow.value)
            {
                if (ce.f.value != null)
                {
                    ce.error(new BytePtr("%s `%s` is not `nothrow`"), ce.f.value.kind(), ce.f.value.toPrettyChars(false));
                }
                else
                {
                    Ref<Expression> e1 = ref(ce.e1.value);
                    {
                        Ref<PtrExp> pe = ref(e1.value.isPtrExp());
                        if ((pe.value) != null)
                            e1.value = pe.value.e1.value;
                    }
                    ce.error(new BytePtr("`%s` is not `nothrow`"), e1.value.toChars());
                }
            }
            this.stop.value = true;
        }

        public  void visit(NewExp ne) {
            if (ne.member.value != null)
            {
                if (ne.allocator.value != null)
                {
                    Ref<TypeFunction> tf = ref(ne.allocator.value.type.value.toBasetype().isTypeFunction());
                    if ((tf.value != null) && !tf.value.isnothrow.value)
                    {
                        if (this.mustNotThrow.value)
                        {
                            ne.error(new BytePtr("%s `%s` is not `nothrow`"), ne.allocator.value.kind(), ne.allocator.value.toPrettyChars(false));
                        }
                        this.stop.value = true;
                    }
                }
                Ref<TypeFunction> tf = ref(ne.member.value.type.value.toBasetype().isTypeFunction());
                if ((tf.value != null) && !tf.value.isnothrow.value)
                {
                    if (this.mustNotThrow.value)
                    {
                        ne.error(new BytePtr("%s `%s` is not `nothrow`"), ne.member.value.kind(), ne.member.value.toPrettyChars(false));
                    }
                    this.stop.value = true;
                }
            }
        }

        public  void visit(DeleteExp de) {
            Type tb = de.e1.value.type.value.toBasetype();
            Ref<AggregateDeclaration> ad = ref(null);
            switch ((tb.ty.value & 0xFF))
            {
                case 7:
                    ad.value = tb.isTypeClass().sym.value;
                    break;
                case 3:
                case 0:
                    Ref<TypeStruct> ts = ref(tb.nextOf().baseElemOf().isTypeStruct());
                    if (ts.value == null)
                        return ;
                    ad.value = ts.value.sym.value;
                    break;
                default:
                return ;
            }
            if (ad.value.dtor.value != null)
            {
                Ref<TypeFunction> tf = ref(ad.value.dtor.value.type.value.toBasetype().isTypeFunction());
                if ((tf.value != null) && !tf.value.isnothrow.value)
                {
                    if (this.mustNotThrow.value)
                    {
                        de.error(new BytePtr("%s `%s` is not `nothrow`"), ad.value.dtor.value.kind(), ad.value.dtor.value.toPrettyChars(false));
                    }
                    this.stop.value = true;
                }
            }
            if ((ad.value.aggDelete.value != null) && ((tb.ty.value & 0xFF) != ENUMTY.Tarray))
            {
                Ref<TypeFunction> tf = ref(ad.value.aggDelete.value.type.value.isTypeFunction());
                if ((tf.value != null) && !tf.value.isnothrow.value)
                {
                    if (this.mustNotThrow.value)
                    {
                        de.error(new BytePtr("%s `%s` is not `nothrow`"), ad.value.aggDelete.value.kind(), ad.value.aggDelete.value.toPrettyChars(false));
                    }
                    this.stop.value = true;
                }
            }
        }

        public  void visit(AssignExp ae) {
            if (((ae.op.value & 0xFF) == 96))
                return ;
            Ref<Type> t = ref(null);
            if (((ae.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!ae.e2.value.isLvalue())
                    return ;
                t.value = ae.type.value;
            }
            else {
                Ref<SliceExp> se = ref(ae.e1.value.isSliceExp());
                if ((se.value) != null)
                    t.value = se.value.e1.value.type.value;
                else
                    return ;
            }
            Ref<TypeStruct> ts = ref(t.value.baseElemOf().isTypeStruct());
            if (ts.value == null)
                return ;
            StructDeclaration sd = ts.value.sym.value;
            if (sd.postblit.value == null)
                return ;
            Ref<TypeFunction> tf = ref(sd.postblit.value.type.value.isTypeFunction());
            if ((tf.value == null) || tf.value.isnothrow.value)
            {
            }
            else
            {
                if (this.mustNotThrow.value)
                {
                    ae.error(new BytePtr("%s `%s` is not `nothrow`"), sd.postblit.value.kind(), sd.postblit.value.toPrettyChars(false));
                }
                this.stop.value = true;
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
                if ((vd.storage_class.value & 8388608L) != 0)
                {
                }
                else if (vd.isStatic() || ((vd.storage_class.value & 1207959554L) != 0))
                {
                }
                else
                {
                    if (vd._init.value != null)
                    {
                        {
                            ExpInitializer ie = vd._init.value.isExpInitializer();
                            if ((ie) != null)
                                if (canThrow(ie.exp.value, func_ref.value, mustNotThrow_ref.value))
                                    return true;
                        }
                    }
                    if (vd.needsScopeDtor())
                        return canThrow(vd.edtor.value, func_ref.value, mustNotThrow_ref.value);
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
                        return foreachDsymbol(tm.members.value, symbolDg) != 0;
                    }
                    else {
                        TupleDeclaration td = s.isTupleDeclaration();
                        if ((td) != null)
                        {
                            {
                                int i = 0;
                                for (; (i < (td.objects.value.get()).length.value);i++){
                                    RootObject o = (td.objects.value.get()).get(i);
                                    if ((o.dyncast() == DYNCAST.expression))
                                    {
                                        Expression eo = (Expression)o;
                                        {
                                            DsymbolExp se = eo.isDsymbolExp();
                                            if ((se) != null)
                                            {
                                                if (Dsymbol_canThrow(se.s.value, func_ref.value, mustNotThrow_ref.value))
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
