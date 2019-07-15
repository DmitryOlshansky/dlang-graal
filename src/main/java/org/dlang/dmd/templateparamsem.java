package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class templateparamsem {

    // Erasure: tpsemantic<TemplateParameter, Ptr, Ptr>
    public static boolean tpsemantic(TemplateParameter tp, Ptr<Scope> sc, Ptr<DArray<TemplateParameter>> parameters) {
        TemplateParameterSemanticVisitor v = new TemplateParameterSemanticVisitor(sc, parameters);
        tp.accept(v);
        return v.result;
    }

    public static class TemplateParameterSemanticVisitor extends Visitor
    {
        public Ptr<Scope> sc = null;
        public Ptr<DArray<TemplateParameter>> parameters = null;
        public boolean result = false;
        // Erasure: __ctor<Ptr, Ptr>
        public  TemplateParameterSemanticVisitor(Ptr<Scope> sc, Ptr<DArray<TemplateParameter>> parameters) {
            this.sc = pcopy(sc);
            this.parameters = pcopy(parameters);
        }

        // Erasure: visit<TemplateTypeParameter>
        public  void visit(TemplateTypeParameter ttp) {
            if ((ttp.specType != null) && !reliesOnTident(ttp.specType, this.parameters, 0))
            {
                ttp.specType = typeSemantic(ttp.specType, ttp.loc, this.sc);
            }
            this.result = !((ttp.specType != null) && isError(ttp.specType));
        }

        // Erasure: visit<TemplateValueParameter>
        public  void visit(TemplateValueParameter tvp) {
            tvp.valType = typeSemantic(tvp.valType, tvp.loc, this.sc);
            this.result = !isError(tvp.valType);
        }

        // Erasure: visit<TemplateAliasParameter>
        public  void visit(TemplateAliasParameter tap) {
            if ((tap.specType != null) && !reliesOnTident(tap.specType, this.parameters, 0))
            {
                tap.specType = typeSemantic(tap.specType, tap.loc, this.sc);
            }
            tap.specAlias = aliasParameterSemantic(tap.loc, this.sc, tap.specAlias, this.parameters);
            this.result = !((tap.specType != null) && isError(tap.specType)) && !((tap.specAlias != null) && isError(tap.specAlias));
        }

        // Erasure: visit<TemplateTupleParameter>
        public  void visit(TemplateTupleParameter ttp) {
            this.result = true;
        }


        public TemplateParameterSemanticVisitor() {}

        public TemplateParameterSemanticVisitor copy() {
            TemplateParameterSemanticVisitor that = new TemplateParameterSemanticVisitor();
            that.sc = this.sc;
            that.parameters = this.parameters;
            that.result = this.result;
            return that;
        }
    }
    // Erasure: aliasParameterSemantic<Loc, Ptr, RootObject, Ptr>
    public static RootObject aliasParameterSemantic(Loc loc, Ptr<Scope> sc, RootObject o, Ptr<DArray<TemplateParameter>> parameters) {
        if (o != null)
        {
            Expression ea = isExpression(o);
            Type ta = isType(o);
            if ((ta != null) && (parameters == null) || !reliesOnTident(ta, parameters, 0))
            {
                Dsymbol s = ta.toDsymbol(sc);
                if (s != null)
                {
                    o = s;
                }
                else
                {
                    o = typeSemantic(ta, loc, sc);
                }
            }
            else if (ea != null)
            {
                sc = pcopy((sc.get()).startCTFE());
                ea = expressionSemantic(ea, sc);
                sc = pcopy((sc.get()).endCTFE());
                o = ea.ctfeInterpret();
            }
        }
        return o;
    }

}
