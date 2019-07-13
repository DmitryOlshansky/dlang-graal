package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class aliasthis {

    public static class AliasThis extends Dsymbol
    {
        public Identifier ident = null;
        public  AliasThis(Loc loc, Identifier ident) {
            super(loc, null);
            this.ident = ident;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new AliasThis(this.loc.value, this.ident);
        }

        public  BytePtr kind() {
            return new BytePtr("alias this");
        }

        public  AliasThis isAliasThis() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AliasThis() {}

        public AliasThis copy() {
            AliasThis that = new AliasThis();
            that.ident = this.ident;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static Expression resolveAliasThis(Ptr<Scope> sc, Expression e, boolean gag) {
        {
            AggregateDeclaration ad = isAggregate(e.type.value);
        L_outer1:
            for (; ad != null;){
                if (ad.aliasthis.value != null)
                {
                    int olderrors = gag ? global.startGagging() : 0;
                    Loc loc = e.loc.value.copy();
                    Type tthis = ((e.op.value & 0xFF) == 20) ? e.type.value : null;
                    e = new DotIdExp(loc, e, ad.aliasthis.value.ident.value);
                    e = expressionSemantic(e, sc);
                    if ((tthis != null) && ad.aliasthis.value.needThis())
                    {
                        try {
                            if (((e.op.value & 0xFF) == 26))
                            {
                                {
                                    FuncDeclaration fd = ((VarExp)e).var.value.isFuncDeclaration();
                                    if ((fd) != null)
                                    {
                                        Ref<Boolean> hasOverloads = ref(false);
                                        {
                                            FuncDeclaration f = fd.overloadModMatch(loc, tthis, hasOverloads);
                                            if ((f) != null)
                                            {
                                                if (!hasOverloads.value)
                                                    fd = f;
                                                e = new VarExp(loc, fd, hasOverloads.value);
                                                e.type.value = f.type.value;
                                                e = new CallExp(loc, e);
                                                /*goto L1*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                    }
                                }
                            }
                            {
                                int save = (sc.get()).intypeof.value;
                                (sc.get()).intypeof.value = 1;
                                e = resolveProperties(sc, e);
                                (sc.get()).intypeof.value = save;
                            }
                        }
                        catch(Dispatch0 __d){}
                    /*L1:*/
                        e = new TypeExp(loc, new TypeTypeof(loc, e));
                        e = expressionSemantic(e, sc);
                    }
                    e = resolveProperties(sc, e);
                    if (gag && global.endGagging(olderrors))
                        e = null;
                }
                ClassDeclaration cd = ad.isClassDeclaration();
                if ((e == null) || (ad.aliasthis.value == null) && (cd != null) && (cd.baseClass.value != null) && (!pequals(cd.baseClass.value, ClassDeclaration.object.value)))
                {
                    ad = cd.baseClass.value;
                    continue L_outer1;
                }
                break;
            }
        }
        return e;
    }

    // defaulted all parameters starting with #3
    public static Expression resolveAliasThis(Ptr<Scope> sc, Expression e) {
        return resolveAliasThis(sc, e, false);
    }

}
