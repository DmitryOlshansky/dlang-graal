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
            super();
            this.func = func;
            this.mustNotThrow = mustNotThrow;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(DeclarationExp de) {
            this.stop = Dsymbol_canThrow(de.declaration, this.func, this.mustNotThrow);
        }

        public  void visit(CallExp ce) {
            if ((global.errors != 0) && (ce.e1.value.type.value == null))
            {
                return ;
            }
            if ((ce.f != null) && (pequals(ce.f, this.func)))
            {
                return ;
            }
            Type t = ce.e1.value.type.value.toBasetype();
            TypeFunction tf = t.isTypeFunction();
            if ((tf != null) && tf.isnothrow)
            {
                return ;
            }
            else
            {
                TypeDelegate td = t.isTypeDelegate();
                if ((td != null) && td.nextOf().isTypeFunction().isnothrow)
                {
                    return ;
                }
            }
            if (this.mustNotThrow)
            {
                if (ce.f != null)
                {
                    ce.error(new BytePtr("%s `%s` is not `nothrow`"), ce.f.kind(), ce.f.toPrettyChars(false));
                }
                else
                {
                    Expression e1 = ce.e1.value;
                    {
                        PtrExp pe = e1.isPtrExp();
                        if ((pe) != null)
                        {
                            e1 = pe.e1.value;
                        }
                    }
                    ce.error(new BytePtr("`%s` is not `nothrow`"), e1.toChars());
                }
            }
            this.stop = true;
        }

        public  void visit(NewExp ne) {
            if (ne.member != null)
            {
                if (ne.allocator != null)
                {
                    TypeFunction tf = ne.allocator.type.toBasetype().isTypeFunction();
                    if ((tf != null) && !tf.isnothrow)
                    {
                        if (this.mustNotThrow)
                        {
                            ne.error(new BytePtr("%s `%s` is not `nothrow`"), ne.allocator.kind(), ne.allocator.toPrettyChars(false));
                        }
                        this.stop = true;
                    }
                }
                TypeFunction tf = ne.member.type.toBasetype().isTypeFunction();
                if ((tf != null) && !tf.isnothrow)
                {
                    if (this.mustNotThrow)
                    {
                        ne.error(new BytePtr("%s `%s` is not `nothrow`"), ne.member.kind(), ne.member.toPrettyChars(false));
                    }
                    this.stop = true;
                }
            }
        }

        public  void visit(DeleteExp de) {
            Type tb = de.e1.value.type.value.toBasetype();
            AggregateDeclaration ad = null;
            switch ((tb.ty & 0xFF))
            {
                case 7:
                    ad = tb.isTypeClass().sym;
                    break;
                case 3:
                case 0:
                    TypeStruct ts = tb.nextOf().baseElemOf().isTypeStruct();
                    if (ts == null)
                    {
                        return ;
                    }
                    ad = ts.sym;
                    break;
                default:
                return ;
            }
            if (ad.dtor != null)
            {
                TypeFunction tf = ad.dtor.type.toBasetype().isTypeFunction();
                if ((tf != null) && !tf.isnothrow)
                {
                    if (this.mustNotThrow)
                    {
                        de.error(new BytePtr("%s `%s` is not `nothrow`"), ad.dtor.kind(), ad.dtor.toPrettyChars(false));
                    }
                    this.stop = true;
                }
            }
            if ((ad.aggDelete != null) && ((tb.ty & 0xFF) != ENUMTY.Tarray))
            {
                TypeFunction tf = ad.aggDelete.type.isTypeFunction();
                if ((tf != null) && !tf.isnothrow)
                {
                    if (this.mustNotThrow)
                    {
                        de.error(new BytePtr("%s `%s` is not `nothrow`"), ad.aggDelete.kind(), ad.aggDelete.toPrettyChars(false));
                    }
                    this.stop = true;
                }
            }
        }

        public  void visit(AssignExp ae) {
            if (((ae.op & 0xFF) == 96))
            {
                return ;
            }
            Type t = null;
            if (((ae.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!ae.e2.value.isLvalue())
                {
                    return ;
                }
                t = ae.type.value;
            }
            else {
                SliceExp se = ae.e1.value.isSliceExp();
                if ((se) != null)
                {
                    t = se.e1.value.type.value;
                }
                else
                {
                    return ;
                }
            }
            TypeStruct ts = t.baseElemOf().isTypeStruct();
            if (ts == null)
            {
                return ;
            }
            StructDeclaration sd = ts.sym;
            if (sd.postblit == null)
            {
                return ;
            }
            TypeFunction tf = sd.postblit.type.isTypeFunction();
            if ((tf == null) || tf.isnothrow)
            {
            }
            else
            {
                if (this.mustNotThrow)
                {
                    ae.error(new BytePtr("%s `%s` is not `nothrow`"), sd.postblit.kind(), sd.postblit.toPrettyChars(false));
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
        // skipping duplicate class CanThrow
        CanThrow ct = new CanThrow(func, mustNotThrow);
        return walkPostorder(e, ct);
    }

    public static boolean Dsymbol_canThrow(Dsymbol s, FuncDeclaration func, boolean mustNotThrow) {
        Function1<Dsymbol,Integer> symbolDg = new Function1<Dsymbol,Integer>() {
            public Integer invoke(Dsymbol s) {
             {
                return (Dsymbol_canThrow(s, func, mustNotThrow) ? 1 : 0);
            }}

        };
        {
            VarDeclaration vd = s.isVarDeclaration();
            if ((vd) != null)
            {
                s = s.toAlias();
                if ((!pequals(s, vd)))
                {
                    return Dsymbol_canThrow(s, func, mustNotThrow);
                }
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
                            {
                                if (canThrow(ie.exp, func, mustNotThrow))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                    if (vd.needsScopeDtor())
                    {
                        return canThrow(vd.edtor, func, mustNotThrow);
                    }
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
                                                if (Dsymbol_canThrow(se.s, func, mustNotThrow))
                                                {
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
        }
        return false;
    }

}
