package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utils.*;

public class staticcond {

    public static boolean evalStaticCondition(Ptr<Scope> sc, Expression exp, Expression e, Ref<Boolean> errors) {
        if (((e.op & 0xFF) == 101) || ((e.op & 0xFF) == 102))
        {
            LogicalExp aae = (LogicalExp)e;
            boolean result = evalStaticCondition(sc, exp, aae.e1.value, errors);
            if (errors.value)
            {
                return false;
            }
            if (((e.op & 0xFF) == 101))
            {
                if (!result)
                {
                    return false;
                }
            }
            else
            {
                if (result)
                {
                    return true;
                }
            }
            result = evalStaticCondition(sc, exp, aae.e2.value, errors);
            return !errors.value && result;
        }
        if (((e.op & 0xFF) == 100))
        {
            CondExp ce = (CondExp)e;
            boolean result = evalStaticCondition(sc, exp, ce.econd.value, errors);
            if (errors.value)
            {
                return false;
            }
            Expression leg = result ? ce.e1.value : ce.e2.value;
            result = evalStaticCondition(sc, exp, leg, errors);
            return !errors.value && result;
        }
        int nerrors = global.errors;
        sc = pcopy((sc.get()).startCTFE());
        (sc.get()).flags |= 4;
        e = expressionSemantic(e, sc);
        e = resolveProperties(sc, e);
        sc = pcopy((sc.get()).endCTFE());
        e = e.optimize(0, false);
        if ((nerrors != global.errors) || ((e.op & 0xFF) == 127) || (pequals(e.type.value.toBasetype(), Type.terror)))
        {
            errors.value = true;
            return false;
        }
        e = resolveAliasThis(sc, e, false);
        if (!e.type.value.isBoolean())
        {
            exp.error(new BytePtr("expression `%s` of type `%s` does not have a boolean value"), exp.toChars(), e.type.value.toChars());
            errors.value = true;
            return false;
        }
        e = e.ctfeInterpret();
        if (e.isBool(true))
        {
            return true;
        }
        else if (e.isBool(false))
        {
            return false;
        }
        e.error(new BytePtr("expression `%s` is not constant"), e.toChars());
        errors.value = true;
        return false;
    }

}
