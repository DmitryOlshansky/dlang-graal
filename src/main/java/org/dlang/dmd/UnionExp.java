package org.dlang.dmd;

import static org.dlang.dmd.expression.*;

public class UnionExp {
    private Expression e;

    public UnionExp() { }

    public UnionExp(Expression e) {
        this.e = e;
    }

    public UnionExp(UnionExp ue) {
        this.e = ue.e;
    }

    UnionExp copy() {
        return new UnionExp(e.copy());
    }

    public Expression exp() { return e; }
}
