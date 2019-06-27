import dmd.astbase;
import dmd.errors;
import dmd.globals;
import dmd.parse;

void main()
{
    scope diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
    scope parser = new Parser!ASTBase(null, null, false, diagnosticReporter);
    assert(parser !is null);
}
