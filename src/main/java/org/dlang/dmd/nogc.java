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
                walkPostorder(exp, this);
        }

        public  void visit(Expression e) {
        }

        public  void visit(DeclarationExp e) {
            VarDeclaration v = e.declaration.isVarDeclaration();
            if ((v != null) && ((v.storage_class & 8388608L) == 0) && !v.isDataseg() && (v._init != null))
            {
                {
                    ExpInitializer ei = v._init.isExpInitializer();
                    if ((ei) != null)
                    {
                        this.doCond(ei.exp);
                    }
                }
            }
        }

        public  void visit(CallExp e) {
        }

        public  void visit(ArrayLiteralExp e) {
            if (((e.type.value.ty & 0xFF) != ENUMTY.Tarray) || (e.elements == null) || ((e.elements.get()).length == 0))
                return ;
            if (this.f.setGC())
            {
                e.error(new BytePtr("array literal in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc, new BytePtr("array literal may cause a GC allocation"));
        }

        public  void visit(AssocArrayLiteralExp e) {
            if ((e.keys.get()).length == 0)
                return ;
            if (this.f.setGC())
            {
                e.error(new BytePtr("associative array literal in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc, new BytePtr("associative array literal may cause a GC allocation"));
        }

        public  void visit(NewExp e) {
            if ((e.member != null) && !e.member.isNogc() && this.f.setGC())
            {
                return ;
            }
            if (e.onstack)
                return ;
            if (e.allocator != null)
                return ;
            if (global.value.params.ehnogc && e.thrownew)
                return ;
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use `new` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc, new BytePtr("`new` causes a GC allocation"));
        }

        public  void visit(DeleteExp e) {
            if (((e.e1.op & 0xFF) == 26))
            {
                VarDeclaration v = ((VarExp)e.e1).var.isVarDeclaration();
                if ((v != null) && v.onstack)
                    return ;
            }
            Type tb = e.e1.type.value.toBasetype();
            AggregateDeclaration ad = null;
            switch ((tb.ty & 0xFF))
            {
                case 7:
                    ad = ((TypeClass)tb).sym;
                    break;
                case 3:
                    tb = ((TypePointer)tb).next.toBasetype();
                    if (((tb.ty & 0xFF) == ENUMTY.Tstruct))
                        ad = ((TypeStruct)tb).sym;
                    break;
                default:
                break;
            }
            if ((ad != null) && (ad.aggDelete != null))
                return ;
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use `delete` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc, new BytePtr("`delete` requires the GC"));
        }

        public  void visit(IndexExp e) {
            Type t1b = e.e1.value.type.value.toBasetype();
            if (((t1b.ty & 0xFF) == ENUMTY.Taarray))
            {
                if (this.f.setGC())
                {
                    e.error(new BytePtr("indexing an associative array in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                    this.err = true;
                    return ;
                }
                this.f.printGCUsage(e.loc, new BytePtr("indexing an associative array may cause a GC allocation"));
            }
        }

        public  void visit(AssignExp e) {
            if (((e.e1.value.op & 0xFF) == 32))
            {
                if (this.f.setGC())
                {
                    e.error(new BytePtr("setting `length` in `@nogc` %s `%s` may cause a GC allocation"), this.f.kind(), this.f.toPrettyChars(false));
                    this.err = true;
                    return ;
                }
                this.f.printGCUsage(e.loc, new BytePtr("setting `length` may cause a GC allocation"));
            }
        }

        public  void visit(CatAssignExp e) {
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use operator `~=` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc, new BytePtr("operator `~=` may cause a GC allocation"));
        }

        public  void visit(CatExp e) {
            if (this.f.setGC())
            {
                e.error(new BytePtr("cannot use operator `~` in `@nogc` %s `%s`"), this.f.kind(), this.f.toPrettyChars(false));
                this.err = true;
                return ;
            }
            this.f.printGCUsage(e.loc, new BytePtr("operator `~` may cause a GC allocation"));
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
        FuncDeclaration f = (sc.get()).func;
        if ((e != null) && ((e.op & 0xFF) != 127) && (f != null) && ((sc.get()).intypeof != 1) && (((sc.get()).flags & 128) == 0) && ((f.type.ty & 0xFF) == ENUMTY.Tfunction) && ((TypeFunction)f.type).isnogc || ((f.flags & FUNCFLAG.nogcInprocess) != 0) || global.value.params.vgc && (((sc.get()).flags & 8) == 0))
        {
            NOGCVisitor gcv = new NOGCVisitor(f);
            walkPostorder(e, gcv);
            if (gcv.err)
                return new ErrorExp();
        }
        return e;
    }

}
