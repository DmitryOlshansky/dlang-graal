package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.visitor.*;

public class staticassert {

    public static class StaticAssert extends Dsymbol
    {
        public Expression exp;
        public Expression msg;
        public  StaticAssert(Loc loc, Expression exp, Expression msg) {
            super(loc, Id.empty);
            this.exp = exp;
            this.msg = msg;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new StaticAssert(this.loc, this.exp.syntaxCopy(), this.msg != null ? this.msg.syntaxCopy() : null);
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            ps.set(0, null);
            return true;
        }

        public  BytePtr kind() {
            return new BytePtr("static assert");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticAssert() {}

        public StaticAssert copy() {
            StaticAssert that = new StaticAssert();
            that.exp = this.exp;
            that.msg = this.msg;
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
}
