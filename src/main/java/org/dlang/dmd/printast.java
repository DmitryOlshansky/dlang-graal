package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class printast {

    // Erasure: printAST<Expression, int>
    public static void printAST(Expression e, int indent) {
        PrintASTVisitor pav = new PrintASTVisitor(indent);
        e.accept(pav);
    }

    // defaulted all parameters starting with #2
    public static void printAST(Expression e) {
        printAST(e, 0);
    }

    public static class PrintASTVisitor extends Visitor
    {
        public int indent = 0;
        // Erasure: __ctor<int>
        public  PrintASTVisitor(int indent) {
            this.indent = indent;
        }

        // Erasure: visit<Expression>
        public  void visit(Expression e) {
            printIndent(this.indent);
            printf(new BytePtr("%s %s\n"), Token.toChars(e.op), e.type.value != null ? e.type.value.toChars() : new BytePtr(""));
        }

        // Erasure: visit<StructLiteralExp>
        public  void visit(StructLiteralExp e) {
            printIndent(this.indent);
            printf(new BytePtr("%s %s, %s\n"), Token.toChars(e.op), e.type.value != null ? e.type.value.toChars() : new BytePtr(""), e.toChars());
        }

        // Erasure: visit<SymbolExp>
        public  void visit(SymbolExp e) {
            this.visit((Expression)e);
            printIndent(this.indent + 2);
            printf(new BytePtr(".var: %s\n"), e.var != null ? e.var.toChars() : new BytePtr(""));
        }

        // Erasure: visit<DsymbolExp>
        public  void visit(DsymbolExp e) {
            this.visit((Expression)e);
            printIndent(this.indent + 2);
            printf(new BytePtr(".s: %s\n"), e.s != null ? e.s.toChars() : new BytePtr(""));
        }

        // Erasure: visit<DotIdExp>
        public  void visit(DotIdExp e) {
            this.visit((Expression)e);
            printIndent(this.indent + 2);
            printf(new BytePtr(".ident: %s\n"), e.ident.toChars());
            printAST(e.e1.value, this.indent + 2);
        }

        // Erasure: visit<UnaExp>
        public  void visit(UnaExp e) {
            this.visit((Expression)e);
            printAST(e.e1.value, this.indent + 2);
        }

        // Erasure: visit<BinExp>
        public  void visit(BinExp e) {
            this.visit((Expression)e);
            printAST(e.e1.value, this.indent + 2);
            printAST(e.e2.value, this.indent + 2);
        }

        // Erasure: visit<DelegateExp>
        public  void visit(DelegateExp e) {
            this.visit((Expression)e);
            printIndent(this.indent + 2);
            printf(new BytePtr(".func: %s\n"), e.func != null ? e.func.toChars() : new BytePtr(""));
        }

        // Erasure: printIndent<int>
        public static void printIndent(int indent) {
            {
                int __key1563 = 0;
                int __limit1564 = indent;
                for (; (__key1563 < __limit1564);__key1563 += 1) {
                    int i = __key1563;
                    putc(32, stdout);
                }
            }
        }


        public PrintASTVisitor() {}

        public PrintASTVisitor copy() {
            PrintASTVisitor that = new PrintASTVisitor();
            that.indent = this.indent;
            return that;
        }
    }
}
