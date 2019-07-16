package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class cppmangle {
    static Slice<Identifier> isCppOperatoroperators = new RawSlice<Identifier>();


    public static class CppOperator 
    {
        public static final int Cast = 0;
        public static final int Assign = 1;
        public static final int Eq = 2;
        public static final int Index = 3;
        public static final int Call = 4;
        public static final int Unary = 5;
        public static final int Binary = 6;
        public static final int OpAssign = 7;
        public static final int Unknown = 8;
    }

    // Erasure: isCppOperator<Identifier>
    public static int isCppOperator(Identifier id) {
        if (cppmangle.isCppOperatoroperators.getLength() == 0)
        {
            cppmangle.isCppOperatoroperators = slice(new Identifier[]{Id._cast, Id.assign, Id.eq, Id.index, Id.call, Id.opUnary, Id.opBinary, Id.opOpAssign}).copy();
        }
        {
            Slice<Identifier> __r849 = cppmangle.isCppOperatoroperators.copy();
            int __key848 = 0;
            for (; (__key848 < __r849.getLength());__key848 += 1) {
                Identifier op = __r849.get(__key848);
                int i = __key848;
                if ((pequals(op, id)))
                {
                    return (int)i;
                }
            }
        }
        return CppOperator.Unknown;
    }

    // Erasure: toCppMangleItanium<Dsymbol>
    public static BytePtr toCppMangleItanium(Dsymbol s) {
        Ref<OutBuffer> buf = ref(new OutBuffer());
        try {
            CppMangleVisitor v = new CppMangleVisitor(ptr(buf), s.loc);
            v.mangleOf(s);
            return buf.value.extractChars();
        }
        finally {
        }
    }

    // Erasure: cppTypeInfoMangleItanium<Dsymbol>
    public static BytePtr cppTypeInfoMangleItanium(Dsymbol s) {
        Ref<OutBuffer> buf = ref(new OutBuffer());
        try {
            buf.value.writestring(new ByteSlice("_ZTI"));
            CppMangleVisitor v = new CppMangleVisitor(ptr(buf), s.loc);
            v.cpp_mangle_name(s, false);
            return buf.value.extractChars();
        }
        finally {
        }
    }

    // Erasure: isPrimaryDtor<Dsymbol>
    public static boolean isPrimaryDtor(Dsymbol sym) {
        DtorDeclaration dtor = sym.isDtorDeclaration();
        if (dtor == null)
        {
            return false;
        }
        AggregateDeclaration ad = dtor.isMember();
        assert(ad != null);
        return pequals(dtor, ad.primaryDtor);
    }

    public static class Context
    {
        public TemplateInstance ti = null;
        public FuncDeclaration fd = null;
        public RootObject res = null;
        // Erasure: push<RootObject>
        public  Context push(RootObject next) {
            RootObject r = this.res;
            if ((r != null))
            {
                this.res = next.invoke();
            }
            return new Context(this.ti, this.fd, r);
        }

        // Erasure: pop<Context>
        public  void pop(Context prev) {
            this.res = prev.res;
        }

        public Context(){ }
        public Context copy(){
            Context r = new Context();
            r.ti = ti;
            r.fd = fd;
            r.res = res;
            return r;
        }
        public Context(TemplateInstance ti, FuncDeclaration fd, RootObject res) {
            this.ti = ti;
            this.fd = fd;
            this.res = res;
        }

        public Context opAssign(Context that) {
            this.ti = that.ti;
            this.fd = that.fd;
            this.res = that.res;
            return this;
        }
    }
    public static class CppMangleVisitor extends Visitor
    {
        public Context context = new Context();
        public DArray<RootObject> components = new DArray<RootObject>();
        public Ptr<OutBuffer> buf = null;
        public Loc loc = new Loc();
        // Erasure: __ctor<Ptr, Loc>
        public  CppMangleVisitor(Ptr<OutBuffer> buf, Loc loc) {
            this.buf = pcopy(buf);
            this.loc.opAssign(loc.copy());
        }

        // Erasure: mangleOf<Dsymbol>
        public  void mangleOf(Dsymbol s) {
            {
                VarDeclaration vd = s.isVarDeclaration();
                if ((vd) != null)
                {
                    this.mangle_variable(vd, vd.namespace != null);
                }
                else {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        this.mangle_function(fd);
                    }
                    else
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
        }

        // Erasure: mangleReturnType<TypeFunction>
        public  void mangleReturnType(TypeFunction preSemantic) {
            CppMangleVisitor __self = this;
            TypeFunction tf = (TypeFunction)asFuncDecl(this.context.res).type;
            Type rt = preSemantic.nextOf();
            if (tf.isref)
            {
                rt = rt.referenceTo();
            }
            Function0<RootObject> __dgliteral2 = new Function0<RootObject>() {
                public RootObject invoke() {
                 {
                    return tf.nextOf();
                }}

            };
            Ref<Context> prev = ref(this.context.push(__dgliteral2).copy());
            try {
                this.headOfType(rt);
            }
            finally {
                this.context.pop(prev);
            }
        }

        // Erasure: writeSequenceFromIndex<int>
        public  void writeSequenceFromIndex(int idx) {
            CppMangleVisitor __self = this;
            if (idx != 0)
            {
                Runnable1<Integer> write_seq_id = new Runnable1<Integer>() {
                    public Void invoke(Integer i) {
                     {
                        Ref<Integer> i_ref = ref(i);
                        if ((i_ref.value >= 36))
                        {
                            invoke(i_ref.value / 36);
                            i_ref.value %= 36;
                        }
                        i_ref.value += (i_ref.value < 10) ? 48 : 55;
                        (buf.get()).writeByte(((byte)i_ref.value & 0xFF));
                        return null;
                    }}

                };
                write_seq_id.invoke(idx - 1);
            }
        }

        // Erasure: substitute<RootObject>
        public  boolean substitute(RootObject p) {
            int i = this.find(p);
            if ((i >= 0))
            {
                (this.buf.get()).writeByte(83);
                this.writeSequenceFromIndex(i);
                (this.buf.get()).writeByte(95);
                return true;
            }
            return false;
        }

        // Erasure: find<RootObject>
        public  int find(RootObject p) {
            ComponentVisitor v = new ComponentVisitor(p);
            {
                Slice<RootObject> __r851 = this.components.opSlice().copy();
                int __key850 = 0;
                for (; (__key850 < __r851.getLength());__key850 += 1) {
                    RootObject component = __r851.get(__key850);
                    int i = __key850;
                    if (component != null)
                    {
                        visitObjectComponentVisitor(component, v);
                    }
                    if (v.result)
                    {
                        return i;
                    }
                }
            }
            return -1;
        }

        // Erasure: append<RootObject>
        public  void append(RootObject p) {
            this.components.push(p);
        }

        // Erasure: writeIdentifier<Identifier>
        public  void writeIdentifier(Identifier ident) {
            ByteSlice name = ident.asString().copy();
            (this.buf.get()).print((long)name.getLength());
            (this.buf.get()).writestring(name);
        }

        // Erasure: isStd<Dsymbol>
        public static boolean isStd(Dsymbol s) {
            if (s == null)
            {
                return false;
            }
            {
                CPPNamespaceDeclaration cnd = s.isCPPNamespaceDeclaration();
                if ((cnd) != null)
                {
                    return isStd(cnd);
                }
            }
            return (pequals(s.ident, Id.std)) && (s.isNspace() != null) && (getQualifier(s) == null);
        }

        // Erasure: isStd<CPPNamespaceDeclaration>
        public static boolean isStd(CPPNamespaceDeclaration s) {
            return (s != null) && (s.namespace == null) && (pequals(s.ident, Id.std));
        }

        // Erasure: isFundamentalType<Type>
        public static boolean isFundamentalType(Type t) {
            Ref<Boolean> isFundamental = ref(null);
            if (target.cppFundamentalType(t, isFundamental))
            {
                return isFundamental.value;
            }
            {
                TypeEnum te = t.isTypeEnum();
                if ((te) != null)
                {
                    if (te.sym.isSpecial())
                    {
                        t = te.memType(Loc.initial);
                    }
                }
            }
            if (((t.ty & 0xFF) == ENUMTY.Tvoid) || ((t.ty & 0xFF) == ENUMTY.Tbool))
            {
                return true;
            }
            else if (((t.ty & 0xFF) == ENUMTY.Tnull) && (global.params.cplusplus >= CppStdRevision.cpp11))
            {
                return true;
            }
            else
            {
                return (t.isTypeBasic() != null) && t.isintegral() || t.isreal();
            }
        }

        // Erasure: template_arg<TemplateInstance, int>
        public  void template_arg(TemplateInstance ti, int arg) {
            CppMangleVisitor __self = this;
            TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
            assert(td != null);
            TemplateParameter tp = (td.parameters.get()).get(arg);
            RootObject o = (ti.tiargs.get()).get(arg);
            Ptr<DArray<RootObject>> pctx = null;
            Function0<RootObject> __dgliteral4 = new Function0<RootObject>() {
                public RootObject invoke() {
                 {
                    Function0<RootObject> __lambda3 = new Function0<RootObject>() {
                        public RootObject invoke() {
                         {
                            TemplateInstance parentti = null;
                            if ((context.res.dyncast() == DYNCAST.dsymbol))
                            {
                                parentti = asFuncDecl(context.res).parent.value.isTemplateInstance();
                            }
                            else
                            {
                                parentti = asType(context.res).toDsymbol(null).parent.value.isTemplateInstance();
                            }
                            return (parentti.tiargs.get()).get(arg);
                        }}

                    };
                    return __lambda3.invoke();
                }}

            };
            Ref<Context> prev = ref(this.context.push(__dgliteral4).copy());
            try {
                if (tp.isTemplateTypeParameter() != null)
                {
                    Type t = isType(o);
                    assert(t != null);
                    t.accept(this);
                }
                else {
                    TemplateValueParameter tv = tp.isTemplateValueParameter();
                    if ((tv) != null)
                    {
                        if (tv.valType.isintegral())
                        {
                            Expression e = isExpression(o);
                            assert(e != null);
                            (this.buf.get()).writeByte(76);
                            tv.valType.accept(this);
                            long val = e.toUInteger();
                            if (!tv.valType.isunsigned() && ((long)val < 0L))
                            {
                                val = -val;
                                (this.buf.get()).writeByte(110);
                            }
                            (this.buf.get()).print(val);
                            (this.buf.get()).writeByte(69);
                        }
                        else
                        {
                            ti.error(new BytePtr("Internal Compiler Error: C++ `%s` template value parameter is not supported"), tv.valType.toChars());
                            fatal();
                        }
                    }
                    else if (tp.isTemplateAliasParameter() != null)
                    {
                        Dsymbol d = isDsymbol(o);
                        Expression e = isExpression(o);
                        if ((d != null) && (d.isFuncDeclaration() != null))
                        {
                            (this.buf.get()).writestring(new ByteSlice("XadL"));
                            this.mangle_function(d.isFuncDeclaration());
                            (this.buf.get()).writestring(new ByteSlice("EE"));
                        }
                        else if ((e != null) && ((e.op & 0xFF) == 26) && (((VarExp)e).var.isVarDeclaration() != null))
                        {
                            VarDeclaration vd = ((VarExp)e).var.isVarDeclaration();
                            (this.buf.get()).writeByte(76);
                            this.mangle_variable(vd, true);
                            (this.buf.get()).writeByte(69);
                        }
                        else if ((d != null) && (d.isTemplateDeclaration() != null) && (d.isTemplateDeclaration().onemember != null))
                        {
                            if (!this.substitute(d))
                            {
                                this.cpp_mangle_name(d, false);
                            }
                        }
                        else
                        {
                            ti.error(new BytePtr("Internal Compiler Error: C++ `%s` template alias parameter is not supported"), o.toChars());
                            fatal();
                        }
                    }
                    else if (tp.isTemplateThisParameter() != null)
                    {
                        ti.error(new BytePtr("Internal Compiler Error: C++ `%s` template this parameter is not supported"), o.toChars());
                        fatal();
                    }
                    else
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
            finally {
                this.context.pop(prev);
            }
        }

        // Erasure: template_args<TemplateInstance, int>
        public  boolean template_args(TemplateInstance ti, int firstArg) {
            if ((ti == null) || ((ti.tiargs.get()).length <= firstArg))
            {
                return false;
            }
            (this.buf.get()).writeByte(73);
            {
                int __key852 = firstArg;
                int __limit853 = (ti.tiargs.get()).length;
                for (; (__key852 < __limit853);__key852 += 1) {
                    int i = __key852;
                    TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                    assert(td != null);
                    TemplateParameter tp = (td.parameters.get()).get(i);
                    {
                        TemplateTupleParameter tt = tp.isTemplateTupleParameter();
                        if ((tt) != null)
                        {
                            (this.buf.get()).writeByte(73);
                            {
                                int __key854 = i;
                                int __limit855 = (ti.tiargs.get()).length;
                                for (; (__key854 < __limit855);__key854 += 1) {
                                    int j = __key854;
                                    Type t = isType((ti.tiargs.get()).get(j));
                                    assert(t != null);
                                    t.accept(this);
                                }
                            }
                            (this.buf.get()).writeByte(69);
                            break;
                        }
                    }
                    this.template_arg(ti, i);
                }
            }
            (this.buf.get()).writeByte(69);
            return true;
        }

        // defaulted all parameters starting with #2
        public  boolean template_args(TemplateInstance ti) {
            return template_args(ti, 0);
        }

        // Erasure: writeChained<Dsymbol, Runnable0>
        public  void writeChained(Dsymbol p, Runnable0 dg) {
            if ((p != null) && (p.isModule() == null))
            {
                (this.buf.get()).writestring(new ByteSlice("N"));
                this.source_name(p, true);
                dg.invoke();
                (this.buf.get()).writestring(new ByteSlice("E"));
            }
            else
            {
                dg.invoke();
            }
        }

        // Erasure: source_name<Dsymbol, boolean>
        public  void source_name(Dsymbol s, boolean haveNE) {
            CppMangleVisitor __self = this;
            {
                TemplateInstance ti = s.isTemplateInstance();
                if ((ti) != null)
                {
                    Ref<Boolean> needsTa = ref(false);
                    if (this.substitute(ti.tempdecl))
                    {
                        this.template_args(ti, 0);
                    }
                    else if (this.writeStdSubstitution(ti, needsTa))
                    {
                        if (needsTa.value)
                        {
                            this.template_args(ti, 0);
                        }
                    }
                    else
                    {
                        this.append(ti.tempdecl);
                        Runnable0 __lambda3 = new Runnable0() {
                            public Void invoke() {
                             {
                                writeIdentifier(ti.tempdecl.toAlias().ident);
                                template_args(ti, 0);
                                return null;
                            }}

                        };
                        this.writeNamespace(s.namespace, __lambda3, haveNE);
                    }
                }
                else
                {
                    Runnable0 __lambda4 = new Runnable0() {
                        public Void invoke() {
                         {
                            writeIdentifier(s.ident);
                            return null;
                        }}

                    };
                    this.writeNamespace(s.namespace, __lambda4, haveNE);
                }
            }
        }

        // defaulted all parameters starting with #2
        public  void source_name(Dsymbol s) {
            source_name(s, false);
        }

        // Erasure: getInstance<Dsymbol>
        public static Dsymbol getInstance(Dsymbol s) {
            Dsymbol p = s.toParent();
            if (p != null)
            {
                {
                    TemplateInstance ti = p.isTemplateInstance();
                    if ((ti) != null)
                    {
                        return ti;
                    }
                }
            }
            return s;
        }

        // Erasure: getTiNamespace<TemplateInstance>
        public  CPPNamespaceDeclaration getTiNamespace(TemplateInstance ti) {
            return ti.tempdecl != null ? ti.namespace : asType(this.context.res).toDsymbol(null).namespace;
        }

        // Erasure: getQualifier<Dsymbol>
        public static Dsymbol getQualifier(Dsymbol s) {
            Dsymbol p = s.toParent();
            return (p != null) && (p.isModule() == null) ? p : null;
        }

        // Erasure: isChar<RootObject>
        public static boolean isChar(RootObject o) {
            Type t = isType(o);
            return (t != null) && t.equals(Type.tchar);
        }

        // Erasure: isChar_traits_char<RootObject>
        public  boolean isChar_traits_char(RootObject o) {
            return this.isIdent_char(Id.char_traits, o);
        }

        // Erasure: isAllocator_char<RootObject>
        public  boolean isAllocator_char(RootObject o) {
            return this.isIdent_char(Id.allocator, o);
        }

        // Erasure: isIdent_char<Identifier, RootObject>
        public  boolean isIdent_char(Identifier ident, RootObject o) {
            Type t = isType(o);
            if ((t == null) || ((t.ty & 0xFF) != ENUMTY.Tstruct))
            {
                return false;
            }
            Dsymbol s = ((TypeStruct)t).toDsymbol(null);
            if ((!pequals(s.ident, ident)))
            {
                return false;
            }
            Dsymbol p = s.toParent();
            if (p == null)
            {
                return false;
            }
            TemplateInstance ti = p.isTemplateInstance();
            if (ti == null)
            {
                return false;
            }
            Dsymbol q = getQualifier(ti);
            boolean inStd = isStd(q) || isStd(this.getTiNamespace(ti));
            return inStd && ((ti.tiargs.get()).length == 1) && isChar((ti.tiargs.get()).get(0));
        }

        // Erasure: char_std_char_traits_char<TemplateInstance, Array>
        public  boolean char_std_char_traits_char(TemplateInstance ti, ByteSlice st) {
            if (((ti.tiargs.get()).length == 2) && isChar((ti.tiargs.get()).get(0)) && this.isChar_traits_char((ti.tiargs.get()).get(1)))
            {
                (this.buf.get()).writestring(st.getPtr(0));
                return true;
            }
            return false;
        }

        // Erasure: prefix_name<Dsymbol>
        public  void prefix_name(Dsymbol s) {
            if (this.substitute(s))
            {
                return ;
            }
            if (isStd(s))
            {
                (this.buf.get()).writestring(new ByteSlice("St"));
                return ;
            }
            Dsymbol si = getInstance(s);
            Dsymbol p = getQualifier(si);
            if (p != null)
            {
                if (isStd(p))
                {
                    Ref<Boolean> needsTa = ref(false);
                    TemplateInstance ti = si.isTemplateInstance();
                    if (this.writeStdSubstitution(ti, needsTa))
                    {
                        if (needsTa.value)
                        {
                            this.template_args(ti, 0);
                            this.append(ti);
                        }
                        return ;
                    }
                    (this.buf.get()).writestring(new ByteSlice("St"));
                }
                else
                {
                    this.prefix_name(p);
                }
            }
            this.source_name(si, true);
            if (!isStd(si))
            {
                this.append(si);
            }
        }

        // Erasure: writeStdSubstitution<TemplateInstance, boolean>
        public  boolean writeStdSubstitution(TemplateInstance ti, Ref<Boolean> needsTa) {
            needsTa.value = false;
            if (ti == null)
            {
                return false;
            }
            if (!isStd(this.getTiNamespace(ti)) && !isStd(getQualifier(ti)))
            {
                return false;
            }
            if ((pequals(ti.name, Id.allocator)))
            {
                (this.buf.get()).writestring(new ByteSlice("Sa"));
                needsTa.value = true;
                return true;
            }
            if ((pequals(ti.name, Id.basic_string)))
            {
                if (((ti.tiargs.get()).length == 3) && isChar((ti.tiargs.get()).get(0)) && this.isChar_traits_char((ti.tiargs.get()).get(1)) && this.isAllocator_char((ti.tiargs.get()).get(2)))
                {
                    (this.buf.get()).writestring(new ByteSlice("Ss"));
                    return true;
                }
                (this.buf.get()).writestring(new ByteSlice("Sb"));
                needsTa.value = true;
                return true;
            }
            if ((pequals(ti.name, Id.basic_istream)) && this.char_std_char_traits_char(ti, new ByteSlice("Si")))
            {
                return true;
            }
            if ((pequals(ti.name, Id.basic_ostream)) && this.char_std_char_traits_char(ti, new ByteSlice("So")))
            {
                return true;
            }
            if ((pequals(ti.name, Id.basic_iostream)) && this.char_std_char_traits_char(ti, new ByteSlice("Sd")))
            {
                return true;
            }
            return false;
        }

        // Erasure: cpp_mangle_name<Dsymbol, boolean>
        public  void cpp_mangle_name(Dsymbol s, boolean qualified) {
            Dsymbol p = s.toParent();
            Dsymbol se = s;
            boolean write_prefix = true;
            if ((p != null) && (p.isTemplateInstance() != null))
            {
                se = p;
                if ((this.find(p.isTemplateInstance().tempdecl) >= 0))
                {
                    write_prefix = false;
                }
                p = p.toParent();
            }
            if ((p != null) && (p.isModule() == null))
            {
                if (isStd(p) && !qualified)
                {
                    TemplateInstance ti = se.isTemplateInstance();
                    if ((pequals(s.ident, Id.allocator)))
                    {
                        (this.buf.get()).writestring(new ByteSlice("Sa"));
                        this.template_args(ti, 0);
                    }
                    else if ((pequals(s.ident, Id.basic_string)))
                    {
                        if (((ti.tiargs.get()).length == 3) && isChar((ti.tiargs.get()).get(0)) && this.isChar_traits_char((ti.tiargs.get()).get(1)) && this.isAllocator_char((ti.tiargs.get()).get(2)))
                        {
                            (this.buf.get()).writestring(new ByteSlice("Ss"));
                            return ;
                        }
                        (this.buf.get()).writestring(new ByteSlice("Sb"));
                        this.template_args(ti, 0);
                    }
                    else
                    {
                        if ((pequals(s.ident, Id.basic_istream)))
                        {
                            if (this.char_std_char_traits_char(ti, new ByteSlice("Si")))
                            {
                                return ;
                            }
                        }
                        else if ((pequals(s.ident, Id.basic_ostream)))
                        {
                            if (this.char_std_char_traits_char(ti, new ByteSlice("So")))
                            {
                                return ;
                            }
                        }
                        else if ((pequals(s.ident, Id.basic_iostream)))
                        {
                            if (this.char_std_char_traits_char(ti, new ByteSlice("Sd")))
                            {
                                return ;
                            }
                        }
                        (this.buf.get()).writestring(new ByteSlice("St"));
                        this.source_name(se, true);
                    }
                }
                else
                {
                    (this.buf.get()).writeByte(78);
                    if (write_prefix)
                    {
                        if (isStd(p))
                        {
                            (this.buf.get()).writestring(new ByteSlice("St"));
                        }
                        else
                        {
                            this.prefix_name(p);
                        }
                    }
                    this.source_name(se, true);
                    (this.buf.get()).writeByte(69);
                }
            }
            else
            {
                this.source_name(se, false);
            }
            this.append(s);
        }

        // Erasure: CV_qualifiers<Type>
        public  void CV_qualifiers(Type t) {
            if (t.isConst())
            {
                (this.buf.get()).writeByte(75);
            }
        }

        // Erasure: mangle_variable<VarDeclaration, boolean>
        public  void mangle_variable(VarDeclaration d, boolean isNested) {
            if ((d.storage_class & 1073741890L) == 0)
            {
                d.error(new BytePtr("Internal Compiler Error: C++ static non-`__gshared` non-`extern` variables not supported"));
                fatal();
            }
            Dsymbol p = d.toParent();
            if ((p != null) && (p.isModule() == null))
            {
                (this.buf.get()).writestring(new ByteSlice("_ZN"));
                this.prefix_name(p);
                this.source_name(d, true);
                (this.buf.get()).writeByte(69);
            }
            else
            {
                if (!isNested)
                {
                    (this.buf.get()).writestring(d.ident.asString());
                }
                else
                {
                    (this.buf.get()).writestring(new ByteSlice("_Z"));
                    this.source_name(d, false);
                }
            }
        }

        // Erasure: mangle_function<FuncDeclaration>
        public  void mangle_function(FuncDeclaration d) {
            TypeFunction tf = (TypeFunction)d.type;
            (this.buf.get()).writestring(new ByteSlice("_Z"));
            {
                TemplateDeclaration ftd = getFuncTemplateDecl(d);
                if ((ftd) != null)
                {
                    TemplateInstance ti = d.parent.value.isTemplateInstance();
                    assert(ti != null);
                    this.mangleTemplatedFunction(d, tf, ftd, ti);
                }
                else
                {
                    Dsymbol p = d.toParent();
                    if ((p != null) && (p.isModule() == null) && (tf.linkage == LINK.cpp))
                    {
                        this.mangleNestedFuncPrefix(tf, p);
                        {
                            CtorDeclaration ctor = d.isCtorDeclaration();
                            if ((ctor) != null)
                            {
                                (this.buf.get()).writestring(ctor.isCpCtor ? new ByteSlice("C2") : new ByteSlice("C1"));
                            }
                            else if (isPrimaryDtor(d))
                            {
                                (this.buf.get()).writestring(new ByteSlice("D1"));
                            }
                            else if ((d.ident != null) && (pequals(d.ident, Id.assign)))
                            {
                                (this.buf.get()).writestring(new ByteSlice("aS"));
                            }
                            else if ((d.ident != null) && (pequals(d.ident, Id.eq)))
                            {
                                (this.buf.get()).writestring(new ByteSlice("eq"));
                            }
                            else if ((d.ident != null) && (pequals(d.ident, Id.index)))
                            {
                                (this.buf.get()).writestring(new ByteSlice("ix"));
                            }
                            else if ((d.ident != null) && (pequals(d.ident, Id.call)))
                            {
                                (this.buf.get()).writestring(new ByteSlice("cl"));
                            }
                            else
                            {
                                this.source_name(d, true);
                            }
                        }
                        (this.buf.get()).writeByte(69);
                    }
                    else
                    {
                        this.source_name(d, false);
                    }
                    if ((tf.linkage == LINK.cpp))
                    {
                        this.mangleFunctionParameters(tf.parameterList.parameters, tf.parameterList.varargs);
                    }
                }
            }
        }

        // Erasure: writeNamespace<CPPNamespaceDeclaration, Runnable0, boolean>
        public  void writeNamespace(CPPNamespaceDeclaration ns, Runnable0 dg, boolean haveNE) {
            CppMangleVisitor __self = this;
            Runnable0 runDg = new Runnable0() {
                public Void invoke() {
                 {
                    if ((dg != (Runnable0)null))
                    {
                        dg.invoke();
                    }
                    return null;
                }}

            };
            if ((ns == null))
            {
                runDg.invoke();
                return ;
            }
            if (isStd(ns))
            {
                if (!this.substitute(ns))
                {
                    (this.buf.get()).writestring(new ByteSlice("St"));
                }
                runDg.invoke();
            }
            else if ((dg != (Runnable0)null))
            {
                if (!haveNE)
                {
                    (this.buf.get()).writestring(new ByteSlice("N"));
                }
                if (!this.substitute(ns))
                {
                    this.writeNamespace(ns.namespace, (Runnable0)null, false);
                    this.writeIdentifier(ns.ident);
                    this.append(ns);
                }
                dg.invoke();
                if (!haveNE)
                {
                    (this.buf.get()).writestring(new ByteSlice("E"));
                }
            }
            else if (!this.substitute(ns))
            {
                this.writeNamespace(ns.namespace, (Runnable0)null, false);
                this.writeIdentifier(ns.ident);
                this.append(ns);
            }
        }

        // defaulted all parameters starting with #3
        public  void writeNamespace(CPPNamespaceDeclaration ns, Runnable0 dg) {
            writeNamespace(ns, dg, false);
        }

        // Erasure: mangleTemplatedFunction<FuncDeclaration, TypeFunction, TemplateDeclaration, TemplateInstance>
        public  void mangleTemplatedFunction(FuncDeclaration d, TypeFunction tf, TemplateDeclaration ftd, TemplateInstance ti) {
            CppMangleVisitor __self = this;
            Dsymbol p = ti.toParent();
            if ((p == null) || (p.isModule() != null) || (tf.linkage != LINK.cpp))
            {
                this.context.ti = ti;
                this.context.fd = d;
                this.context.res = d;
                TypeFunction preSemantic = (TypeFunction)d.originalType;
                Dsymbol nspace = ti.toParent();
                if ((nspace != null) && (nspace.isNspace() != null))
                {
                    Runnable0 __lambda5 = new Runnable0() {
                        public Void invoke() {
                         {
                            source_name(ti, true);
                            return null;
                        }}

                    };
                    this.writeChained(ti.toParent(), __lambda5);
                }
                else
                {
                    this.source_name(ti, false);
                }
                this.mangleReturnType(preSemantic);
                this.mangleFunctionParameters(preSemantic.parameterList.parameters, tf.parameterList.varargs);
                return ;
            }
            this.mangleNestedFuncPrefix(tf, p);
            if (d.isCtorDeclaration() != null)
            {
                (this.buf.get()).writestring(new ByteSlice("C1"));
            }
            else if (isPrimaryDtor(d))
            {
                (this.buf.get()).writestring(new ByteSlice("D1"));
            }
            else
            {
                int firstTemplateArg = 0;
                boolean appendReturnType = true;
                boolean isConvertFunc = false;
                ByteSlice symName = new ByteSlice().copy();
                int whichOp = isCppOperator(ti.name);
                {
                    int __dispatch0 = 0;
                    dispatched_0:
                    do {
                        switch (__dispatch0 != 0 ? __dispatch0 : whichOp)
                        {
                            case CppOperator.Unknown:
                                break;
                            case CppOperator.Cast:
                                symName = new ByteSlice("cv").copy();
                                firstTemplateArg = 1;
                                isConvertFunc = true;
                                appendReturnType = false;
                                break;
                            case CppOperator.Assign:
                                symName = new ByteSlice("aS").copy();
                                break;
                            case CppOperator.Eq:
                                symName = new ByteSlice("eq").copy();
                                break;
                            case CppOperator.Index:
                                symName = new ByteSlice("ix").copy();
                                break;
                            case CppOperator.Call:
                                symName = new ByteSlice("cl").copy();
                                break;
                            case CppOperator.Unary:
                            case CppOperator.Binary:
                            case CppOperator.OpAssign:
                                TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                                assert(td != null);
                                assert(((ti.tiargs.get()).length >= 1));
                                TemplateParameter tp = (td.parameters.get()).get(0);
                                TemplateValueParameter tv = tp.isTemplateValueParameter();
                                if ((tv == null) || !tv.valType.isString())
                                {
                                    break;
                                }
                                Expression exp = isExpression((ti.tiargs.get()).get(0));
                                StringExp str = exp.toStringExp();
                                {
                                    int __dispatch1 = 0;
                                    dispatched_1:
                                    do {
                                        switch (__dispatch1 != 0 ? __dispatch1 : whichOp)
                                        {
                                            case CppOperator.Unary:
                                                {
                                                    int __dispatch2 = 0;
                                                    dispatched_2:
                                                    do {
                                                        switch (__dispatch2 != 0 ? __dispatch2 : __switch(str.peekSlice()))
                                                        {
                                                            case 0:
                                                                symName = new ByteSlice("de").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 4:
                                                                symName = new ByteSlice("pp").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 5:
                                                                symName = new ByteSlice("mm").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 2:
                                                                symName = new ByteSlice("ng").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 1:
                                                                symName = new ByteSlice("ps").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 3:
                                                                symName = new ByteSlice("co").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            default:
                                                            break;
                                                        }
                                                    } while(__dispatch2 != 0);
                                                }
                                                break;
                                            case CppOperator.Binary:
                                                {
                                                    int __dispatch3 = 0;
                                                    dispatched_3:
                                                    do {
                                                        switch (__dispatch3 != 0 ? __dispatch3 : __switch(str.peekSlice()))
                                                        {
                                                            case 9:
                                                                symName = new ByteSlice("rs").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 8:
                                                                symName = new ByteSlice("ls").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 2:
                                                                symName = new ByteSlice("ml").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 4:
                                                                symName = new ByteSlice("mi").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 3:
                                                                symName = new ByteSlice("pl").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 1:
                                                                symName = new ByteSlice("an").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 5:
                                                                symName = new ByteSlice("dv").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 0:
                                                                symName = new ByteSlice("rm").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 6:
                                                                symName = new ByteSlice("eo").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 7:
                                                                symName = new ByteSlice("or").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            default:
                                                            break;
                                                        }
                                                    } while(__dispatch3 != 0);
                                                }
                                                break;
                                            case CppOperator.OpAssign:
                                                {
                                                    int __dispatch4 = 0;
                                                    dispatched_4:
                                                    do {
                                                        switch (__dispatch4 != 0 ? __dispatch4 : __switch(str.peekSlice()))
                                                        {
                                                            case 2:
                                                                symName = new ByteSlice("mL").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 3:
                                                                symName = new ByteSlice("pL").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 4:
                                                                symName = new ByteSlice("mI").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 5:
                                                                symName = new ByteSlice("dV").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 0:
                                                                symName = new ByteSlice("rM").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 9:
                                                                symName = new ByteSlice("rS").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 8:
                                                                symName = new ByteSlice("lS").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 1:
                                                                symName = new ByteSlice("aN").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 7:
                                                                symName = new ByteSlice("oR").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            case 6:
                                                                symName = new ByteSlice("eO").copy();
                                                                /*goto continue_template*/{ __dispatch0 = -1; continue dispatched_0; }
                                                            default:
                                                            break;
                                                        }
                                                    } while(__dispatch4 != 0);
                                                }
                                                break;
                                            default:
                                            throw new AssertionError("Unreachable code!");
                                        /*continue_template:*/
                                        case -1:
                                        __dispatch1 = 0;
                                            firstTemplateArg = 1;
                                            break;
                                        }
                                    } while(__dispatch1 != 0);
                                }
                                break;
                            default:
                            throw SwitchError.INSTANCE;
                        }
                    } while(__dispatch0 != 0);
                }
                if ((symName.getLength() == 0))
                {
                    this.source_name(ti, true);
                }
                else
                {
                    (this.buf.get()).writestring(symName);
                    if (isConvertFunc)
                    {
                        this.template_arg(ti, 0);
                    }
                    appendReturnType = this.template_args(ti, firstTemplateArg) && appendReturnType;
                }
                (this.buf.get()).writeByte(69);
                if (appendReturnType)
                {
                    this.headOfType(tf.nextOf());
                }
            }
            this.mangleFunctionParameters(tf.parameterList.parameters, tf.parameterList.varargs);
        }

        // Erasure: mangleFunctionParameters<Ptr, int>
        public  void mangleFunctionParameters(Ptr<DArray<Parameter>> parameters, int varargs) {
            CppMangleVisitor __self = this;
            Ref<Integer> numparams = ref(0);
            Function0<RootObject> __dgliteral4 = new Function0<RootObject>() {
                public RootObject invoke() {
                 {
                    Function0<Type> __lambda3 = new Function0<Type>() {
                        public Type invoke() {
                         {
                            Ref<TypeFunction> tf = ref(null);
                            if (isDsymbol(context.res) != null)
                            {
                                tf.value = (TypeFunction)asFuncDecl(context.res).type;
                            }
                            else
                            {
                                tf.value = asType(context.res).isTypeFunction();
                            }
                            assert(tf.value != null);
                            return (tf.value.parameterList.parameters.get()).get(n).type;
                        }}

                    };
                    return __lambda3.invoke();
                }}

            };
            Function2<Integer,Parameter,Integer> paramsCppMangleDg = new Function2<Integer,Parameter,Integer>() {
                public Integer invoke(Integer n, Parameter fparam) {
                 {
                    Type t = target.cppParameterType(fparam);
                    if (((t.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        error(loc, new BytePtr("Internal Compiler Error: unable to pass static array `%s` to extern(C++) function, use pointer instead"), t.toChars());
                        fatal();
                    }
                    Ref<Context> prev = ref(context.push(__dgliteral4).copy());
                    try {
                        headOfType(t);
                        numparams.value += 1;
                        return 0;
                    }
                    finally {
                        context.pop(prev);
                    }
                }}

            };
            if (parameters != null)
            {
                Parameter._foreach(parameters, paramsCppMangleDg, null);
            }
            if ((varargs == VarArg.variadic))
            {
                (this.buf.get()).writeByte(122);
            }
            else if (numparams.value == 0)
            {
                (this.buf.get()).writeByte(118);
            }
        }

        // Erasure: error<Type>
        public  void error(Type t) {
            BytePtr p = null;
            if (t.isImmutable())
            {
                p = pcopy(new BytePtr("`immutable` "));
            }
            else if (t.isShared())
            {
                p = pcopy(new BytePtr("`shared` "));
            }
            else
            {
                p = pcopy(new BytePtr(""));
            }
            error(this.loc, new BytePtr("Internal Compiler Error: %stype `%s` can not be mapped to C++\n"), p, t.toChars());
            fatal();
        }

        // Erasure: headOfType<Type>
        public  void headOfType(Type t) {
            CppMangleVisitor __self = this;
            if (((t.ty & 0xFF) == ENUMTY.Tclass))
            {
                this.mangleTypeClass((TypeClass)t, true);
            }
            else
            {
                Function0<RootObject> __dgliteral2 = new Function0<RootObject>() {
                    public RootObject invoke() {
                     {
                        return asType(context.res).mutableOf().unSharedOf();
                    }}

                };
                Ref<Context> prev = ref(this.context.push(__dgliteral2).copy());
                try {
                    t.mutableOf().unSharedOf().accept(this);
                }
                finally {
                    this.context.pop(prev);
                }
            }
        }

        // Erasure: writeBasicType<Type, byte, byte>
        public  void writeBasicType(Type t, byte p, byte c) {
            if (!isFundamentalType(t) || t.isConst())
            {
                if (this.substitute(t))
                {
                    return ;
                }
                else
                {
                    this.append(t);
                }
            }
            this.CV_qualifiers(t);
            if (p != 0)
            {
                (this.buf.get()).writeByte((p & 0xFF));
            }
            (this.buf.get()).writeByte((c & 0xFF));
        }

        // Erasure: doSymbol<Type>
        public  void doSymbol(Type t) {
            if (this.substitute(t))
            {
                return ;
            }
            this.CV_qualifiers(t);
            {
                BytePtr tm = pcopy(target.cppTypeMangle(t));
                if ((tm) != null)
                {
                    (this.buf.get()).writestring(tm);
                }
                else
                {
                    Dsymbol s = t.toDsymbol(null);
                    Dsymbol p = s.toParent();
                    if ((p != null) && (p.isTemplateInstance() != null))
                    {
                        if (this.substitute(p))
                        {
                            return ;
                        }
                    }
                    if (!this.substitute(s))
                    {
                        this.cpp_mangle_name(s, false);
                    }
                }
            }
            if (t.isConst())
            {
                this.append(t);
            }
        }

        // Erasure: mangleTypeClass<TypeClass, boolean>
        public  void mangleTypeClass(TypeClass t, boolean head) {
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            if (this.substitute(t))
            {
                return ;
            }
            if (!head)
            {
                this.CV_qualifiers(t);
            }
            (this.buf.get()).writeByte(80);
            this.CV_qualifiers(t);
            {
                Dsymbol s = t.toDsymbol(null);
                Dsymbol p = s.toParent();
                if ((p != null) && (p.isTemplateInstance() != null))
                {
                    if (this.substitute(p))
                    {
                        return ;
                    }
                }
            }
            if (!this.substitute(t.sym))
            {
                this.cpp_mangle_name(t.sym, false);
            }
            if (t.isConst())
            {
                this.append(null);
            }
            this.append(t);
        }

        // Erasure: mangleNestedFuncPrefix<TypeFunction, Dsymbol>
        public  void mangleNestedFuncPrefix(TypeFunction tf, Dsymbol parent) {
            (this.buf.get()).writeByte(78);
            this.CV_qualifiers(tf);
            this.prefix_name(parent);
        }

        // Erasure: writeTemplateArgIndex<int, TemplateParameter>
        public  void writeTemplateArgIndex(int idx, TemplateParameter param) {
            if (param.isTemplateValueParameter() != null)
            {
                (this.buf.get()).writeByte(88);
            }
            (this.buf.get()).writeByte(84);
            this.writeSequenceFromIndex(idx);
            (this.buf.get()).writeByte(95);
            if (param.isTemplateValueParameter() != null)
            {
                (this.buf.get()).writeByte(69);
            }
        }

        // Erasure: templateParamIndex<Identifier, Ptr>
        public static int templateParamIndex(Identifier ident, Ptr<DArray<TemplateParameter>> params) {
            {
                Slice<TemplateParameter> __r857 = (params.get()).opSlice().copy();
                int __key856 = 0;
                for (; (__key856 < __r857.getLength());__key856 += 1) {
                    TemplateParameter param = __r857.get(__key856);
                    int idx = __key856;
                    if ((pequals(param.ident, ident)))
                    {
                        return idx;
                    }
                }
            }
            return (params.get()).length;
        }

        // Erasure: writeQualified<TemplateInstance, Runnable0>
        public  void writeQualified(TemplateInstance t, Runnable0 dg) {
            CppMangleVisitor __self = this;
            Type type = isType(this.context.res);
            if (type == null)
            {
                this.writeIdentifier(t.name);
                dg.invoke();
                return ;
            }
            Dsymbol sym1 = type.toDsymbol(null);
            if (sym1 == null)
            {
                this.writeIdentifier(t.name);
                dg.invoke();
                return ;
            }
            Dsymbol sym = getQualifier(sym1);
            Dsymbol sym2 = getQualifier(sym);
            if ((sym2 != null) && isStd(sym2))
            {
                Ref<Boolean> unused = ref(false);
                assert(sym.isTemplateInstance() != null);
                if (this.writeStdSubstitution(sym.isTemplateInstance(), unused))
                {
                    dg.invoke();
                    return ;
                }
                (this.buf.get()).writestring(new ByteSlice("St"));
                this.writeIdentifier(t.name);
                this.append(t);
                dg.invoke();
                return ;
            }
            else if (sym2 != null)
            {
                (this.buf.get()).writestring(new ByteSlice("N"));
                if (!this.substitute(sym2))
                {
                    sym2.accept(this);
                }
            }
            Runnable0 __lambda3 = new Runnable0() {
                public Void invoke() {
                 {
                    writeIdentifier(t.name);
                    append(t);
                    dg.invoke();
                    return null;
                }}

            };
            this.writeNamespace(sym1.namespace, __lambda3, false);
            if (sym2 != null)
            {
                (this.buf.get()).writestring(new ByteSlice("E"));
            }
        }

        // Erasure: visit<TypeNull>
        public  void visit(TypeNull t) {
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            this.writeBasicType(t, (byte)68, (byte)110);
        }

        // Erasure: visit<TypeBasic>
        public  void visit(TypeBasic t) {
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            {
                BytePtr tm = pcopy(target.cppTypeMangle(t));
                if ((tm) != null)
                {
                    if (!isFundamentalType(t) || t.isConst())
                    {
                        if (this.substitute(t))
                        {
                            return ;
                        }
                        else
                        {
                            this.append(t);
                        }
                    }
                    this.CV_qualifiers(t);
                    (this.buf.get()).writestring(tm);
                    return ;
                }
            }
            byte c = (byte)255;
            byte p = (byte)0;
            switch ((t.ty & 0xFF))
            {
                case 12:
                    c = (byte)118;
                    break;
                case 13:
                    c = (byte)97;
                    break;
                case 14:
                    c = (byte)104;
                    break;
                case 15:
                    c = (byte)115;
                    break;
                case 16:
                    c = (byte)116;
                    break;
                case 17:
                    c = (byte)105;
                    break;
                case 18:
                    c = (byte)106;
                    break;
                case 21:
                    c = (byte)102;
                    break;
                case 19:
                    c = (target.c_longsize == 8) ? (byte)108 : (byte)120;
                    break;
                case 20:
                    c = (target.c_longsize == 8) ? (byte)109 : (byte)121;
                    break;
                case 42:
                    c = (byte)110;
                    break;
                case 43:
                    c = (byte)111;
                    break;
                case 22:
                    c = (byte)100;
                    break;
                case 23:
                    c = (byte)101;
                    break;
                case 30:
                    c = (byte)98;
                    break;
                case 31:
                    c = (byte)99;
                    break;
                case 32:
                    p = (byte)68;
                    c = (byte)115;
                    break;
                case 33:
                    p = (byte)68;
                    c = (byte)105;
                    break;
                case 24:
                    p = (byte)71;
                    c = (byte)102;
                    break;
                case 25:
                    p = (byte)71;
                    c = (byte)100;
                    break;
                case 26:
                    p = (byte)71;
                    c = (byte)101;
                    break;
                case 27:
                    p = (byte)67;
                    c = (byte)102;
                    break;
                case 28:
                    p = (byte)67;
                    c = (byte)100;
                    break;
                case 29:
                    p = (byte)67;
                    c = (byte)101;
                    break;
                default:
                this.error(t);
                return ;
            }
            this.writeBasicType(t, p, c);
        }

        // Erasure: visit<TypeVector>
        public  void visit(TypeVector t) {
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            if (this.substitute(t))
            {
                return ;
            }
            this.append(t);
            this.CV_qualifiers(t);
            {
                BytePtr tm = pcopy(target.cppTypeMangle(t));
                if ((tm) != null)
                {
                    (this.buf.get()).writestring(tm);
                }
                else
                {
                    assert((t.basetype != null) && ((t.basetype.ty & 0xFF) == ENUMTY.Tsarray));
                    assert(((TypeSArray)t.basetype).dim != null);
                    (this.buf.get()).writestring(new ByteSlice("U8__vector"));
                    t.basetype.nextOf().accept(this);
                }
            }
        }

        // Erasure: visit<TypeSArray>
        public  void visit(TypeSArray t) {
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            if (!this.substitute(t))
            {
                this.append(t);
            }
            this.CV_qualifiers(t);
            (this.buf.get()).writeByte(65);
            (this.buf.get()).print(t.dim != null ? t.dim.toInteger() : 0L);
            (this.buf.get()).writeByte(95);
            t.next.value.accept(this);
        }

        // Erasure: visit<TypePointer>
        public  void visit(TypePointer t) {
            CppMangleVisitor __self = this;
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            this.CV_qualifiers(t);
            if (this.substitute(t))
            {
                return ;
            }
            (this.buf.get()).writeByte(80);
            Function0<RootObject> __dgliteral2 = new Function0<RootObject>() {
                public RootObject invoke() {
                 {
                    return asType(context.res).nextOf();
                }}

            };
            Ref<Context> prev = ref(this.context.push(__dgliteral2).copy());
            try {
                t.next.value.accept(this);
                this.append(t);
            }
            finally {
                this.context.pop(prev);
            }
        }

        // Erasure: visit<TypeReference>
        public  void visit(TypeReference t) {
            CppMangleVisitor __self = this;
            if (this.substitute(t))
            {
                return ;
            }
            (this.buf.get()).writeByte(82);
            Function0<RootObject> __dgliteral2 = new Function0<RootObject>() {
                public RootObject invoke() {
                 {
                    return asType(context.res).nextOf();
                }}

            };
            Ref<Context> prev = ref(this.context.push(__dgliteral2).copy());
            try {
                t.next.value.accept(this);
                this.append(t);
            }
            finally {
                this.context.pop(prev);
            }
        }

        // Erasure: visit<TypeFunction>
        public  void visit(TypeFunction t) {
            if (this.substitute(t))
            {
                return ;
            }
            (this.buf.get()).writeByte(70);
            if ((t.linkage == LINK.c))
            {
                (this.buf.get()).writeByte(89);
            }
            Type tn = t.next.value;
            if (t.isref)
            {
                tn = tn.referenceTo();
            }
            tn.accept(this);
            this.mangleFunctionParameters(t.parameterList.parameters, t.parameterList.varargs);
            (this.buf.get()).writeByte(69);
            this.append(t);
        }

        // Erasure: visit<TypeStruct>
        public  void visit(TypeStruct t) {
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            this.doSymbol(t);
        }

        // Erasure: visit<TypeEnum>
        public  void visit(TypeEnum t) {
            if (t.isImmutable() || t.isShared())
            {
                this.error(t);
                return ;
            }
            Identifier id = t.sym.ident;
            if ((pequals(id, Id.__c_long)))
            {
                this.writeBasicType(t, (byte)0, (byte)108);
                return ;
            }
            else if ((pequals(id, Id.__c_ulong)))
            {
                this.writeBasicType(t, (byte)0, (byte)109);
                return ;
            }
            else if ((pequals(id, Id.__c_wchar_t)))
            {
                this.writeBasicType(t, (byte)0, (byte)119);
                return ;
            }
            else if ((pequals(id, Id.__c_longlong)))
            {
                this.writeBasicType(t, (byte)0, (byte)120);
                return ;
            }
            else if ((pequals(id, Id.__c_ulonglong)))
            {
                this.writeBasicType(t, (byte)0, (byte)121);
                return ;
            }
            this.doSymbol(t);
        }

        // Erasure: visit<TypeClass>
        public  void visit(TypeClass t) {
            this.mangleTypeClass(t, false);
        }

        // Erasure: visit<TypeIdentifier>
        public  void visit(TypeIdentifier t) {
            TemplateDeclaration decl = (TemplateDeclaration)this.context.ti.tempdecl;
            assert((decl.parameters != null));
            int idx = templateParamIndex(t.ident, decl.parameters);
            if ((idx >= (decl.parameters.get()).length))
            {
                visitObjectCppMangleVisitor(this.context.res, this);
                return ;
            }
            TemplateParameter param = (decl.parameters.get()).get(idx);
            {
                Type type = isType(this.context.res);
                if ((type) != null)
                {
                    this.CV_qualifiers(type);
                }
            }
            if (this.substitute(param))
            {
                return ;
            }
            this.writeTemplateArgIndex(idx, param);
            this.append(param);
        }

        // Erasure: visit<TypeInstance>
        public  void visit(TypeInstance t) {
            assert((t.tempinst != null));
            t.tempinst.accept(this);
        }

        // Erasure: visit<TemplateInstance>
        public  void visit(TemplateInstance t) {
            CppMangleVisitor __self = this;
            Runnable0 writeArgs = new Runnable0() {
                public Void invoke() {
                 {
                    (buf.get()).writeByte(73);
                    TemplateInstance analyzed_ti = asType(context.res).toDsymbol(null).isInstantiated();
                    Ref<Context> prev = ref(context.copy());
                    try {
                        {
                            Slice<RootObject> __r859 = (t.tiargs.get()).opSlice().copy();
                            Ref<Integer> __key858 = ref(0);
                            for (; (__key858.value < __r859.getLength());__key858.value += 1) {
                                RootObject o = __r859.get(__key858.value);
                                int idx = __key858.value;
                                context.res = (analyzed_ti.tiargs.get()).get(idx);
                                visitObjectCppMangleVisitor(o, __self);
                            }
                        }
                        if (((analyzed_ti.tiargs.get()).length > (t.tiargs.get()).length))
                        {
                            Ptr<DArray<TemplateParameter>> oparams = ((TemplateDeclaration)analyzed_ti.tempdecl).origParameters;
                            {
                                Slice<TemplateParameter> __r861 = ((oparams.get()).opSlice((t.tiargs.get()).length, (oparams.get()).opSlice.getLength())).copy();
                                Ref<Integer> __key860 = ref(0);
                                for (; (__key860.value < __r861.getLength());__key860.value += 1) {
                                    TemplateParameter arg = __r861.get(__key860.value);
                                    int idx = __key860.value;
                                    context.res = (analyzed_ti.tiargs.get()).get(idx + (t.tiargs.get()).length);
                                    {
                                        TemplateTypeParameter ttp = arg.isTemplateTypeParameter();
                                        if ((ttp) != null)
                                        {
                                            ttp.defaultType.accept(__self);
                                        }
                                        else {
                                            TemplateValueParameter tvp = arg.isTemplateValueParameter();
                                            if ((tvp) != null)
                                            {
                                                tvp.defaultValue.accept(__self);
                                            }
                                            else {
                                                TemplateThisParameter tvp = arg.isTemplateThisParameter();
                                                if ((tvp) != null)
                                                {
                                                    tvp.defaultType.accept(__self);
                                                }
                                                else {
                                                    TemplateAliasParameter tvp = arg.isTemplateAliasParameter();
                                                    if ((tvp) != null)
                                                    {
                                                        visitObjectCppMangleVisitor(tvp.defaultAlias, __self);
                                                    }
                                                    else
                                                    {
                                                        throw new AssertionError("Unreachable code!");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        (buf.get()).writeByte(69);
                    }
                    finally {
                        context.pop(prev);
                    }
                }}

            };
            assert((t.name != null));
            assert((t.tiargs != null));
            Ref<Boolean> needsTa = ref(false);
            TemplateDeclaration decl = (TemplateDeclaration)this.context.ti.tempdecl;
            int idx = templateParamIndex(t.name, decl.parameters);
            if ((idx < (decl.parameters.get()).length))
            {
                TemplateParameter param = (decl.parameters.get()).get(idx);
                {
                    Type type = t.getType();
                    if ((type) != null)
                    {
                        this.CV_qualifiers(type);
                    }
                }
                if (this.substitute(param))
                {
                    return ;
                }
                this.writeTemplateArgIndex(idx, param);
                this.append(param);
                writeArgs.invoke();
            }
            else if (this.writeStdSubstitution(t, needsTa))
            {
                if (needsTa.value)
                {
                    writeArgs.invoke();
                }
            }
            else if (!this.substitute(t))
            {
                this.writeQualified(t, writeArgs);
            }
        }

        // Erasure: visit<IntegerExp>
        public  void visit(IntegerExp t) {
            (this.buf.get()).writeByte(76);
            t.type.value.accept(this);
            (this.buf.get()).print(t.getInteger());
            (this.buf.get()).writeByte(69);
        }

        // Erasure: visit<Nspace>
        public  void visit(Nspace t) {
            {
                Dsymbol p = getQualifier(t);
                if ((p) != null)
                {
                    p.accept(this);
                }
            }
            if (isStd((Dsymbol)t))
            {
                (this.buf.get()).writestring(new ByteSlice("St"));
            }
            else
            {
                this.writeIdentifier(t.ident);
                this.append(t);
            }
        }

        // Erasure: visit<Type>
        public  void visit(Type t) {
            this.error(t);
        }

        // Erasure: visit<Tuple>
        public  void visit(Tuple t) {
            throw new AssertionError("Unreachable code!");
        }


        public CppMangleVisitor() {}

        public CppMangleVisitor copy() {
            CppMangleVisitor that = new CppMangleVisitor();
            that.context = this.context;
            that.components = this.components;
            that.buf = this.buf;
            that.loc = this.loc;
            return that;
        }
    }
    // from template visitObject!(ComponentVisitor)
    // Erasure: visitObjectComponentVisitor<RootObject, ComponentVisitor>
    public static void visitObjectComponentVisitor(RootObject o, ComponentVisitor this_) {
        assert((o != null));
        {
            Type ta = isType(o);
            if ((ta) != null)
            {
                ta.accept(this_);
            }
            else {
                Expression ea = isExpression(o);
                if ((ea) != null)
                {
                    ea.accept(this_);
                }
                else {
                    Dsymbol sa = isDsymbol(o);
                    if ((sa) != null)
                    {
                        sa.accept(this_);
                    }
                    else {
                        TemplateParameter t = isTemplateParameter(o);
                        if ((t) != null)
                        {
                            t.accept(this_);
                        }
                        else {
                            Tuple t = isTuple(o);
                            if ((t) != null)
                            {
                                this_.visit(t);
                            }
                            else
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                        }
                    }
                }
            }
        }
    }


    // from template visitObject!(CppMangleVisitor)
    // Erasure: visitObjectCppMangleVisitor<RootObject, CppMangleVisitor>
    public static void visitObjectCppMangleVisitor(RootObject o, CppMangleVisitor this_) {
        assert((o != null));
        {
            Type ta = isType(o);
            if ((ta) != null)
            {
                ta.accept(this_);
            }
            else {
                Expression ea = isExpression(o);
                if ((ea) != null)
                {
                    ea.accept(this_);
                }
                else {
                    Dsymbol sa = isDsymbol(o);
                    if ((sa) != null)
                    {
                        sa.accept(this_);
                    }
                    else {
                        TemplateParameter t = isTemplateParameter(o);
                        if ((t) != null)
                        {
                            t.accept(this_);
                        }
                        else {
                            Tuple t = isTuple(o);
                            if ((t) != null)
                            {
                                this_.visit(t);
                            }
                            else
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                        }
                    }
                }
            }
        }
    }


    // Erasure: asType<RootObject>
    public static Type asType(RootObject o) {
        Type ta = isType(o);
        assertMsg((ta != null), o.asString());
        return ta;
    }

    // Erasure: asFuncDecl<RootObject>
    public static FuncDeclaration asFuncDecl(RootObject o) {
        Dsymbol d = isDsymbol(o);
        assert((d != null));
        FuncDeclaration fd = d.isFuncDeclaration();
        assert((fd != null));
        return fd;
    }

    public static class ComponentVisitor extends Visitor
    {
        public Nspace namespace = null;
        public CPPNamespaceDeclaration namespace2 = null;
        public TypePointer tpointer = null;
        public TypeReference tref = null;
        public TypeIdentifier tident = null;
        public RootObject object = null;
        public boolean result = false;
        // Erasure: __ctor<RootObject>
        public  ComponentVisitor(RootObject base) {
            {
                int __dispatch6 = 0;
                dispatched_6:
                do {
                    switch (__dispatch6 != 0 ? __dispatch6 : base.dyncast())
                    {
                        case DYNCAST.dsymbol:
                            {
                                Nspace ns = ((Dsymbol)base).isNspace();
                                if ((ns) != null)
                                {
                                    this.namespace = ns;
                                }
                                else {
                                    CPPNamespaceDeclaration ns = ((Dsymbol)base).isCPPNamespaceDeclaration();
                                    if ((ns) != null)
                                    {
                                        this.namespace2 = ns;
                                    }
                                    else
                                    {
                                        /*goto default*/ { __dispatch6 = -1; continue dispatched_6; }
                                    }
                                }
                            }
                            break;
                        case DYNCAST.type:
                            Type t = (Type)base;
                            if (((t.ty & 0xFF) == ENUMTY.Tpointer))
                            {
                                this.tpointer = (TypePointer)t;
                            }
                            else if (((t.ty & 0xFF) == ENUMTY.Treference))
                            {
                                this.tref = (TypeReference)t;
                            }
                            else if (((t.ty & 0xFF) == ENUMTY.Tident))
                            {
                                this.tident = (TypeIdentifier)t;
                            }
                            else
                            {
                                /*goto default*/ { __dispatch6 = -1; continue dispatched_6; }
                            }
                            break;
                        default:
                        __dispatch6 = 0;
                        this.object = base;
                    }
                } while(__dispatch6 != 0);
            }
        }

        // Erasure: visit<Dsymbol>
        public  void visit(Dsymbol o) {
            this.result = (this.object != null) && (pequals(this.object, o));
        }

        // Erasure: visit<Expression>
        public  void visit(Expression o) {
            this.result = (this.object != null) && (pequals(this.object, o));
        }

        // Erasure: visit<Tuple>
        public  void visit(Tuple o) {
            this.result = (this.object != null) && (pequals(this.object, o));
        }

        // Erasure: visit<Type>
        public  void visit(Type o) {
            this.result = (this.object != null) && (pequals(this.object, o));
        }

        // Erasure: visit<TemplateParameter>
        public  void visit(TemplateParameter o) {
            this.result = (this.object != null) && (pequals(this.object, o));
        }

        // Erasure: visit<TypeReference>
        public  void visit(TypeReference o) {
            if (this.tref == null)
            {
                return ;
            }
            if ((pequals(this.tref, o)))
            {
                this.result = true;
            }
            else
            {
                ComponentVisitor v = new ComponentVisitor(this.tref.next.value);
                visitObjectComponentVisitor(o.next.value, v);
                this.result = v.result;
            }
        }

        // Erasure: visit<TypePointer>
        public  void visit(TypePointer o) {
            if (this.tpointer == null)
            {
                return ;
            }
            if ((pequals(this.tpointer, o)))
            {
                this.result = true;
            }
            else
            {
                ComponentVisitor v = new ComponentVisitor(this.tpointer.next.value);
                visitObjectComponentVisitor(o.next.value, v);
                this.result = v.result;
            }
        }

        // Erasure: visit<TypeIdentifier>
        public  void visit(TypeIdentifier o) {
            this.result = (this.tident != null) && (pequals(this.tident.ident, o.ident));
        }

        // Erasure: visit<Nspace>
        public  void visit(Nspace ns) {
            this.result = isNamespaceEqual(this.namespace, ns) || isNamespaceEqual(this.namespace2, ns, 0);
        }

        // Erasure: visit<CPPNamespaceDeclaration>
        public  void visit(CPPNamespaceDeclaration ns) {
            this.result = isNamespaceEqual(this.namespace, ns) || isNamespaceEqual(this.namespace2, ns);
        }


        public ComponentVisitor() {}

        public ComponentVisitor copy() {
            ComponentVisitor that = new ComponentVisitor();
            that.namespace = this.namespace;
            that.namespace2 = this.namespace2;
            that.tpointer = this.tpointer;
            that.tref = this.tref;
            that.tident = this.tident;
            that.object = this.object;
            that.result = this.result;
            return that;
        }
    }
    // Erasure: isNamespaceEqual<Nspace, Nspace>
    public static boolean isNamespaceEqual(Nspace a, Nspace b) {
        if ((a == null) || (b == null))
        {
            return false;
        }
        return a.equals(b);
    }

    // Erasure: isNamespaceEqual<Nspace, CPPNamespaceDeclaration>
    public static boolean isNamespaceEqual(Nspace a, CPPNamespaceDeclaration b) {
        return isNamespaceEqual(b, a, 0);
    }

    // Erasure: isNamespaceEqual<CPPNamespaceDeclaration, Nspace, int>
    public static boolean isNamespaceEqual(CPPNamespaceDeclaration a, Nspace b, int idx) {
        if ((((a == null) ? 1 : 0) != ((b == null) ? 1 : 0)))
        {
            return false;
        }
        if (!a.ident.equals(b.ident))
        {
            return false;
        }
        {
            Nspace pb = b.toParent().isNspace();
            if ((pb) != null)
            {
                return isNamespaceEqual(a.namespace, pb, 0);
            }
            else
            {
                return a.namespace == null;
            }
        }
    }

    // defaulted all parameters starting with #3
    public static boolean isNamespaceEqual(CPPNamespaceDeclaration a, Nspace b) {
        return isNamespaceEqual(a, b, 0);
    }

    // Erasure: isNamespaceEqual<CPPNamespaceDeclaration, CPPNamespaceDeclaration>
    public static boolean isNamespaceEqual(CPPNamespaceDeclaration a, CPPNamespaceDeclaration b) {
        if ((a == null) || (b == null))
        {
            return false;
        }
        if ((((a.namespace == null) ? 1 : 0) != ((b.namespace == null) ? 1 : 0)))
        {
            return false;
        }
        if ((!pequals(a.ident, b.ident)))
        {
            return false;
        }
        return (a.namespace == null) ? true : isNamespaceEqual(a.namespace, b.namespace);
    }

}
