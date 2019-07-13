package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.visitor.*;

public class dversion {

    public static class DebugSymbol extends Dsymbol
    {
        public int level = 0;
        public  DebugSymbol(Loc loc, Identifier ident) {
            super(loc, ident);
        }

        public  DebugSymbol(Loc loc, int level) {
            super(loc, null);
            this.level = level;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            DebugSymbol ds = new DebugSymbol(this.loc.value, this.ident.value);
            ds.level = this.level;
            return ds;
        }

        public  BytePtr toChars() {
            if (this.ident.value != null)
            {
                return this.ident.value.toChars();
            }
            else
            {
                OutBuffer buf = new OutBuffer();
                try {
                    buf.print((long)this.level);
                    return buf.extractChars();
                }
                finally {
                }
            }
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            dmodule.Module m = sds.isModule();
            if (this.ident.value != null)
            {
                if (m == null)
                {
                    this.error(new BytePtr("declaration must be at module level"));
                    this.errors.value = true;
                }
                else
                {
                    if (findCondition(m.debugidsNot, this.ident.value))
                    {
                        this.error(new BytePtr("defined after use"));
                        this.errors.value = true;
                    }
                    if (m.debugids == null)
                    {
                        m.debugids = refPtr(new DArray<Identifier>());
                    }
                    (m.debugids.get()).push(this.ident.value);
                }
            }
            else
            {
                if (m == null)
                {
                    this.error(new BytePtr("level declaration must be at module level"));
                    this.errors.value = true;
                }
                else
                {
                    m.debuglevel = this.level;
                }
            }
        }

        public  BytePtr kind() {
            return new BytePtr("debug");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DebugSymbol() {}

        public DebugSymbol copy() {
            DebugSymbol that = new DebugSymbol();
            that.level = this.level;
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
    public static class VersionSymbol extends Dsymbol
    {
        public int level = 0;
        public  VersionSymbol(Loc loc, Identifier ident) {
            super(loc, ident);
        }

        public  VersionSymbol(Loc loc, int level) {
            super(loc, null);
            this.level = level;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            VersionSymbol ds = this.ident.value != null ? new VersionSymbol(this.loc.value, this.ident.value) : new VersionSymbol(this.loc.value, this.level);
            return ds;
        }

        public  BytePtr toChars() {
            if (this.ident.value != null)
            {
                return this.ident.value.toChars();
            }
            else
            {
                OutBuffer buf = new OutBuffer();
                try {
                    buf.print((long)this.level);
                    return buf.extractChars();
                }
                finally {
                }
            }
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            dmodule.Module m = sds.isModule();
            if (this.ident.value != null)
            {
                VersionCondition.checkReserved(this.loc.value, this.ident.value.asString());
                if (m == null)
                {
                    this.error(new BytePtr("declaration must be at module level"));
                    this.errors.value = true;
                }
                else
                {
                    if (findCondition(m.versionidsNot, this.ident.value))
                    {
                        this.error(new BytePtr("defined after use"));
                        this.errors.value = true;
                    }
                    if (m.versionids == null)
                    {
                        m.versionids = refPtr(new DArray<Identifier>());
                    }
                    (m.versionids.get()).push(this.ident.value);
                }
            }
            else
            {
                if (m == null)
                {
                    this.error(new BytePtr("level declaration must be at module level"));
                    this.errors.value = true;
                }
                else
                {
                    m.versionlevel = this.level;
                }
            }
        }

        public  BytePtr kind() {
            return new BytePtr("version");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VersionSymbol() {}

        public VersionSymbol copy() {
            VersionSymbol that = new VersionSymbol();
            that.level = this.level;
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
