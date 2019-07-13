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
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class nogc {

    public static class NOGCVisitor extends StoppableVisitor
    {
        public FuncDeclaration f = null;
        public boolean err = false;
        public  NOGCVisitor(FuncDeclaration f) {
            super();
            this.f = f;
        }

        public  void doCond(Expression exp) {
            if (exp != null)
            {
                walkPostorder(exp, this);
            }
        }

        public  void visit(Expression e) {
        }

        public  void visit(DeclarationExp e) {
            VarDeclaration v = e.declaration.value.isVarDeclaration();
            if ((v != null) && ((v.storage_class.value & 8388608L) == 0) && !v.isDataseg() && (v._init.value != null))
            {
                {
                    ExpInitializer ei = v._init.value.isExpInitializer();
                    if ((ei) != null)
                    {
                        this.doCond(ei.exp.value);
                    }
                }
            }
        }

        public  void visit(CallExp e) {
        }

        public  void visit(ArrayLiteralExp e) {
            if (((e.type.value.ty.value & 0xFF) != ENUMTY.Tarray) || (e.elements.value == null) || ((e.elements.value.get()).length.value == 0))
            {
                return ;
            }
            if (this.f.setGC())
            {
                e.error(new BytePtr("array literal in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc.value, new BytePtr("array literal may cause a GC allocation"));
        }

        public  void visit(AssocArrayLiteralExp e) {
            if ((e.keys.value.get()).length.value == 0)
            {
                return ;
            }
            if (this.f.setGC())
            {
                e.error(new BytePtr("associative array literal in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc.value, new BytePtr("associative array literal may cause a GC allocation"));
        }

        public  void visit(NewExp e) {
            if ((e.member.value != null) && !e.member.value.isNogc() && this.f.setGC())
            {
                return ;
            }
            if (e.onstack)
            {
                return ;
            }
            if (e.allocator.value != null)
            {
                return ;
            }
            if (global.params.ehnogc && e.thrownew)
            {
                return ;
            }
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use `new` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc.value, new BytePtr("`new` causes a GC allocation"));
        }

        public  void visit(DeleteExp e) {
            if (((e.e1.value.op.value & 0xFF) == 26))
            {
                VarDeclaration v = ((VarExp)e.e1.value).var.value.isVarDeclaration();
                if ((v != null) && v.onstack)
                {
                    return ;
                }
            }
            Type tb = e.e1.value.type.value.toBasetype();
            AggregateDeclaration ad = null;
            switch ((tb.ty.value & 0xFF))
            {
                case 7:
                    ad = ((TypeClass)tb).sym.value;
                    break;
                case 3:
                    tb = ((TypePointer)tb).next.value.toBasetype();
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        ad = ((TypeStruct)tb).sym.value;
                    }
                    break;
                default:
                break;
            }
            if ((ad != null) && (ad.aggDelete.value != null))
            {
                return ;
            }
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use `delete` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc.value, new BytePtr("`delete` requires the GC"));
        }

        public  void visit(IndexExp e) {
            Type t1b = e.e1.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                if (this.f.setGC())
                {
                    e.error(new BytePtr("indexing an associative array in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                    this.err = true;
                    return ;
                }
                this.f.printGCUsage(e.loc.value, new BytePtr("indexing an associative array may cause a GC allocation"));
            }
        }

        public  void visit(AssignExp e) {
            if (((e.e1.value.op.value & 0xFF) == 32))
            {
                if (this.f.setGC())
                {
                    e.error(new BytePtr("setting `length` in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                    this.err = true;
                    return ;
                }
                this.f.printGCUsage(e.loc.value, new BytePtr("setting `length` may cause a GC allocation"));
            }
        }

        public  void visit(CatAssignExp e) {
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use operator `~=` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc.value, new BytePtr("operator `~=` may cause a GC allocation"));
        }

        public  void visit(CatExp e) {
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use operator `~` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc.value, new BytePtr("operator `~` may cause a GC allocation"));
        }


        public NOGCVisitor() {}

        public NOGCVisitor copy() {
            NOGCVisitor that = new NOGCVisitor();
            that.f = this.f;
            that.err = this.err;
            that.stop = this.stop;
            return that;
        }
    }
    public static Expression checkGC(Ptr<Scope> sc, Expression e) {
        FuncDeclaration f = (sc.get()).func.value;
        if ((e != null) && ((e.op.value & 0xFF) != 127) && (f != null) && ((sc.get()).intypeof.value != 1) && (((sc.get()).flags.value & 128) == 0) && ((f.type.value.ty.value & 0xFF) == ENUMTY.Tfunction) && ((TypeFunction)f.type.value).isnogc.value || ((f.flags & FUNCFLAG.nogcInprocess) != 0) || global.params.vgc && (((sc.get()).flags.value & 8) == 0))
        {
            NOGCVisitor gcv = new NOGCVisitor(f);
            walkPostorder(e, gcv);
            if (gcv.err)
            {
                return new ErrorExp();
            }
        }
        return e;
    }

}
