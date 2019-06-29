package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.parse.*;

public class parser {

    public static void main() {
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        ParserASTBase parser = new ParserASTBase(null, new ByteSlice(), false, diagnosticReporter);
        assert(parser != null);
        exit(0);
    }

}
