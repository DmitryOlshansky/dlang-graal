package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class lambdacomp {

    static boolean LOG = false;

    public static class ExpType 
    {
        public static final int None = 0;
        public static final int EnumDecl = 1;
        public static final int Arg = 2;
    }

    // Erasure: isSameFuncLiteral<FuncLiteralDeclaration, FuncLiteralDeclaration, Ptr>
    public static boolean isSameFuncLiteral(FuncLiteralDeclaration l1, FuncLiteralDeclaration l2, Ptr<Scope> sc) {
        {
            ByteSlice ser1 = getSerialization(l1, sc).copy();
            if ((ser1).getLength() != 0)
            {
                {
                    ByteSlice ser2 = getSerialization(l2, sc).copy();
                    if ((ser2).getLength() != 0)
                    {
                        if (__equals(ser1, ser2))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Erasure: getSerialization<FuncLiteralDeclaration, Ptr>
    public static ByteSlice getSerialization(FuncLiteralDeclaration fld, Ptr<Scope> sc) {
        SerializeVisitor serVisitor = new SerializeVisitor(fld.parent.value._scope);
        fld.accept(serVisitor);
        int len = serVisitor.buf.offset;
        if ((len == 0))
        {
            return new ByteSlice();
        }
        return serVisitor.buf.extractChars().slice(0,len);
    }

    public static class SerializeVisitor extends SemanticTimeTransitiveVisitor
    {
        public StringTable arg_hash = new StringTable();
        public Ptr<Scope> sc = null;
        public int et = 0;
        public Dsymbol d = null;
        public OutBuffer buf = new OutBuffer();
        // Erasure: __ctor<Ptr>
        public  SerializeVisitor(Ptr<Scope> sc) {
            this.sc = pcopy(sc);
        }

        // Erasure: visit<FuncLiteralDeclaration>
        public  void visit(FuncLiteralDeclaration fld) {
            assert(((fld.type.ty & 0xFF) != ENUMTY.Terror));
            TypeFunction tf = (TypeFunction)fld.type;
            int dim = Parameter.dim(tf.parameterList.parameters);
            this.buf.printf(new BytePtr("%d:"), dim);
            this.arg_hash._init(dim + 1);
            {
                int __key1521 = 0;
                int __limit1522 = dim;
                for (; (__key1521 < __limit1522);__key1521 += 1) {
                    int i = __key1521;
                    Parameter fparam = tf.parameterList.get(i);
                    if ((fparam.ident != null))
                    {
                        ByteSlice key = fparam.ident.asString().copy();
                        OutBuffer value = new OutBuffer();
                        try {
                            value.writestring(new ByteSlice("arg"));
                            value.print((long)i);
                            this.arg_hash.insert(key, value.extractChars());
                            fparam.accept(this);
                        }
                        finally {
                        }
                    }
                }
            }
            ReturnStatement rs = fld.fbody.value.isReturnStatement();
            if ((rs != null) && (rs.exp != null))
            {
                rs.exp.accept(this);
            }
            else
            {
                this.buf.offset = 0;
            }
        }

        // Erasure: visit<DotIdExp>
        public  void visit(DotIdExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            exp.e1.value.accept(this);
            if ((this.buf.offset == 0))
            {
                return ;
            }
            if ((this.et == ExpType.EnumDecl))
            {
                Dsymbol s = this.d.search(exp.loc, exp.ident, 0);
                if (s != null)
                {
                    {
                        EnumMember em = s.isEnumMember();
                        if ((em) != null)
                        {
                            em.value().accept(this);
                        }
                    }
                    this.et = ExpType.None;
                    this.d = null;
                }
            }
            else if ((this.et == ExpType.Arg))
            {
                this.buf.setsize(this.buf.offset - 1);
                this.buf.writeByte(46);
                this.buf.writestring(exp.ident.asString());
                this.buf.writeByte(95);
            }
        }

        // Erasure: checkArgument<Ptr>
        public  boolean checkArgument(BytePtr id) {
            Ptr<StringValue> stringtable_value = this.arg_hash.lookup(id, strlen(id));
            if (stringtable_value != null)
            {
                BytePtr gen_id = pcopy(((BytePtr)(stringtable_value.get()).ptrvalue));
                this.buf.writestring(gen_id);
                this.buf.writeByte(95);
                this.et = ExpType.Arg;
                return true;
            }
            return false;
        }

        // Erasure: visit<IdentifierExp>
        public  void visit(IdentifierExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            BytePtr id = pcopy(exp.ident.toChars());
            if (!this.checkArgument(id))
            {
                Ref<Dsymbol> scopesym = ref(null);
                Dsymbol s = (this.sc.get()).search(exp.loc, exp.ident, ptr(scopesym), 0);
                if (s != null)
                {
                    VarDeclaration v = s.isVarDeclaration();
                    if ((v != null) && ((v.storage_class & 8388608L) != 0))
                    {
                        v.getConstInitializer(true).accept(this);
                    }
                    else {
                        EnumDeclaration em = s.isEnumDeclaration();
                        if ((em) != null)
                        {
                            this.d = em;
                            this.et = ExpType.EnumDecl;
                        }
                        else {
                            FuncDeclaration fd = s.isFuncDeclaration();
                            if ((fd) != null)
                            {
                                this.writeMangledName(fd);
                            }
                            else
                            {
                                this.buf.reset();
                            }
                        }
                    }
                }
                else
                {
                    this.buf.reset();
                }
            }
        }

        // Erasure: visit<DotVarExp>
        public  void visit(DotVarExp exp) {
            exp.e1.value.accept(this);
            if ((this.buf.offset == 0))
            {
                return ;
            }
            this.buf.setsize(this.buf.offset - 1);
            this.buf.writeByte(46);
            this.buf.writestring(exp.var.toChars());
            this.buf.writeByte(95);
        }

        // Erasure: visit<VarExp>
        public  void visit(VarExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            BytePtr id = pcopy(exp.var.ident.toChars());
            if (!this.checkArgument(id))
            {
                this.buf.offset = 0;
            }
        }

        // Erasure: visit<CallExp>
        public  void visit(CallExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            if (exp.f == null)
            {
                exp.e1.value.accept(this);
            }
            else
            {
                this.writeMangledName(exp.f);
            }
            this.buf.writeByte(40);
            {
                Slice<Expression> __r1523 = (exp.arguments.get()).opSlice().copy();
                int __key1524 = 0;
                for (; (__key1524 < __r1523.getLength());__key1524 += 1) {
                    Expression arg = __r1523.get(__key1524);
                    arg.accept(this);
                }
            }
            this.buf.writeByte(41);
        }

        // Erasure: visit<UnaExp>
        public  void visit(UnaExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            this.buf.writeByte(40);
            this.buf.writestring(Token.asString(exp.op));
            exp.e1.value.accept(this);
            if ((this.buf.offset != 0))
            {
                this.buf.writestring(new ByteSlice(")_"));
            }
        }

        // Erasure: visit<IntegerExp>
        public  void visit(IntegerExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            this.buf.print(exp.toInteger());
            this.buf.writeByte(95);
        }

        // Erasure: visit<RealExp>
        public  void visit(RealExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            this.buf.writestring(exp.toChars());
            this.buf.writeByte(95);
        }

        // Erasure: visit<BinExp>
        public  void visit(BinExp exp) {
            if ((this.buf.offset == 0))
            {
                return ;
            }
            this.buf.writeByte(40);
            this.buf.writestring(Token.toChars(exp.op));
            exp.e1.value.accept(this);
            if ((this.buf.offset == 0))
            {
                return ;
            }
            exp.e2.value.accept(this);
            if ((this.buf.offset == 0))
            {
                return ;
            }
            this.buf.writeByte(41);
        }

        // Erasure: visit<TypeBasic>
        public  void visit(TypeBasic t) {
            this.buf.writestring(t.dstring);
            this.buf.writeByte(95);
        }

        // Erasure: writeMangledName<Dsymbol>
        public  void writeMangledName(Dsymbol s) {
            if (s != null)
            {
                Ref<OutBuffer> mangledName = ref(new OutBuffer());
                try {
                    mangleToBuffer(s, ptr(mangledName));
                    this.buf.writestring(mangledName.value.peekSlice());
                    this.buf.writeByte(95);
                }
                finally {
                }
            }
            else
            {
                this.buf.reset();
            }
        }

        // from template checkTemplateInstance!(TypeClass)
        // Erasure: checkTemplateInstanceTypeClass<TypeClass>
        public  boolean checkTemplateInstanceTypeClass(TypeClass t) {
            if ((t.sym.parent.value != null) && (t.sym.parent.value.isTemplateInstance() != null))
            {
                this.buf.reset();
                return true;
            }
            return false;
        }


        // from template checkTemplateInstance!(TypeStruct)
        // Erasure: checkTemplateInstanceTypeStruct<TypeStruct>
        public  boolean checkTemplateInstanceTypeStruct(TypeStruct t) {
            if ((t.sym.parent.value != null) && (t.sym.parent.value.isTemplateInstance() != null))
            {
                this.buf.reset();
                return true;
            }
            return false;
        }


        // Erasure: visit<TypeStruct>
        public  void visit(TypeStruct t) {
            if (!this.checkTemplateInstanceTypeStruct(t))
            {
                this.writeMangledName(t.sym);
            }
        }

        // Erasure: visit<TypeClass>
        public  void visit(TypeClass t) {
            if (!this.checkTemplateInstanceTypeClass(t))
            {
                this.writeMangledName(t.sym);
            }
        }

        // Erasure: visit<Parameter>
        public  void visit(Parameter p) {
            if (((p.type.ty & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)p.type).ident.asString().getLength() > 3) && (strncmp(((TypeIdentifier)p.type).ident.toChars(), new BytePtr("__T"), 3) == 0))
            {
                this.buf.writestring(new ByteSlice("none_"));
            }
            else
            {
                this.visitType(p.type);
            }
        }

        // Erasure: visit<StructLiteralExp>
        public  void visit(StructLiteralExp e) {
            TypeStruct ty = (TypeStruct)e.stype;
            if (ty != null)
            {
                this.writeMangledName(ty.sym);
                int dim = (e.elements.get()).length;
                {
                    int __key1529 = 0;
                    int __limit1530 = dim;
                    for (; (__key1529 < __limit1530);__key1529 += 1) {
                        int i = __key1529;
                        Expression elem = (e.elements.get()).get(i);
                        if (elem != null)
                        {
                            elem.accept(this);
                        }
                        else
                        {
                            this.buf.writestring(new ByteSlice("null_"));
                        }
                    }
                }
            }
            else
            {
                this.buf.reset();
            }
        }

        // Erasure: visit<ArrayLiteralExp>
        public  void visit(ArrayLiteralExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<AssocArrayLiteralExp>
        public  void visit(AssocArrayLiteralExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<CompileExp>
        public  void visit(CompileExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<ComplexExp>
        public  void visit(ComplexExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<DeclarationExp>
        public  void visit(DeclarationExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<DefaultInitExp>
        public  void visit(DefaultInitExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<DsymbolExp>
        public  void visit(DsymbolExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<ErrorExp>
        public  void visit(ErrorExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<FuncExp>
        public  void visit(FuncExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<HaltExp>
        public  void visit(HaltExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<IntervalExp>
        public  void visit(IntervalExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<IsExp>
        public  void visit(IsExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<NewAnonClassExp>
        public  void visit(NewAnonClassExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<NewExp>
        public  void visit(NewExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<NullExp>
        public  void visit(NullExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<ObjcClassReferenceExp>
        public  void visit(ObjcClassReferenceExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<OverExp>
        public  void visit(OverExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<ScopeExp>
        public  void visit(ScopeExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<StringExp>
        public  void visit(StringExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<SymbolExp>
        public  void visit(SymbolExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<TemplateExp>
        public  void visit(TemplateExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<ThisExp>
        public  void visit(ThisExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<TraitsExp>
        public  void visit(TraitsExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<TupleExp>
        public  void visit(TupleExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<TypeExp>
        public  void visit(TypeExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<TypeidExp>
        public  void visit(TypeidExp _param_0) {
            this.buf.reset();
        }

        // Erasure: visit<VoidInitExp>
        public  void visit(VoidInitExp _param_0) {
            this.buf.reset();
        }


        public SerializeVisitor() {}

        public SerializeVisitor copy() {
            SerializeVisitor that = new SerializeVisitor();
            that.arg_hash = this.arg_hash;
            that.sc = this.sc;
            that.et = this.et;
            that.d = this.d;
            that.buf = this.buf;
            return that;
        }
    }
}
