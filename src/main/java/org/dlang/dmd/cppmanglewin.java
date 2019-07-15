package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.cppmangle.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class cppmanglewin {

    public static BytePtr toCppMangleMSVC(Dsymbol s) {
        VisualCPPMangler v = new VisualCPPMangler(!global.params.mscoff);
        return v.mangleOf(s);
    }

    public static BytePtr cppTypeInfoMangleMSVC(Dsymbol s) {
        throw new AssertionError("Unreachable code!");
    }

    public static boolean checkImmutableShared(Type type) {
        if (type.isImmutable() || type.isShared())
        {
            error(Loc.initial, new BytePtr("Internal Compiler Error: `shared` or `immutable` types can not be mapped to C++ (%s)"), type.toChars());
            fatal();
            return true;
        }
        return false;
    }

    public static class VisualCPPMangler extends Visitor
    {
        public int VC_SAVED_TYPE_CNT = 10;
        public int VC_SAVED_IDENT_CNT = 10;
        public Slice<BytePtr> saved_idents = new RawSlice<BytePtr>(new BytePtr[10]);
        public Slice<Type> saved_types = new RawSlice<Type>(new Type[10]);

        public static class Flags 
        {
            public static final int IS_NOT_TOP_TYPE = 1;
            public static final int MANGLE_RETURN_TYPE = 2;
            public static final int IGNORE_CONST = 4;
            public static final int IS_DMC = 8;
            public static final int ESCAPE = 16;
        }

        public int flags = 0;
        public OutBuffer buf = new OutBuffer();
        public  VisualCPPMangler(VisualCPPMangler rvl) {
            this.flags |= rvl.flags & Flags.IS_DMC;
            memcpy((BytePtr)(ptr(this.saved_idents)), (ptr(rvl.saved_idents)), 40);
            memcpy((BytePtr)(ptr(this.saved_types)), (ptr(rvl.saved_types)), 40);
        }

        public  VisualCPPMangler(boolean isdmc) {
            if (isdmc)
            {
                this.flags |= Flags.IS_DMC;
            }
            memset(ptr(this.saved_idents), 0, 40);
            memset(ptr(this.saved_types), 0, 40);
        }

        public  void visit(Type type) {
            if (checkImmutableShared(type))
            {
                return ;
            }
            error(Loc.initial, new BytePtr("Internal Compiler Error: type `%s` can not be mapped to C++\n"), type.toChars());
            fatal();
        }

        public  void visit(TypeNull type) {
            if (checkImmutableShared(type))
            {
                return ;
            }
            if (this.checkTypeSaved(type))
            {
                return ;
            }
            this.buf.writestring(new ByteSlice("$$T"));
            this.flags &= -2;
            this.flags &= -5;
        }

        public  void visit(TypeBasic type) {
            if (checkImmutableShared(type))
            {
                return ;
            }
            if (type.isConst() && ((this.flags & Flags.IS_NOT_TOP_TYPE) != 0) || ((this.flags & Flags.IS_DMC) != 0))
            {
                if (this.checkTypeSaved(type))
                {
                    return ;
                }
            }
            if (((type.ty & 0xFF) == ENUMTY.Tbool) && this.checkTypeSaved(type))
            {
                return ;
            }
            if ((this.flags & Flags.IS_DMC) == 0)
            {
                switch ((type.ty & 0xFF))
                {
                    case 19:
                    case 20:
                    case 42:
                    case 43:
                    case 23:
                    case 32:
                        if (this.checkTypeSaved(type))
                        {
                            return ;
                        }
                        break;
                    default:
                    break;
                }
            }
            this.mangleModifier(type);
            switch ((type.ty & 0xFF))
            {
                case 12:
                    this.buf.writeByte(88);
                    break;
                case 13:
                    this.buf.writeByte(67);
                    break;
                case 14:
                    this.buf.writeByte(69);
                    break;
                case 15:
                    this.buf.writeByte(70);
                    break;
                case 16:
                    this.buf.writeByte(71);
                    break;
                case 17:
                    this.buf.writeByte(72);
                    break;
                case 18:
                    this.buf.writeByte(73);
                    break;
                case 21:
                    this.buf.writeByte(77);
                    break;
                case 19:
                    this.buf.writestring(new ByteSlice("_J"));
                    break;
                case 20:
                    this.buf.writestring(new ByteSlice("_K"));
                    break;
                case 42:
                    this.buf.writestring(new ByteSlice("_L"));
                    break;
                case 43:
                    this.buf.writestring(new ByteSlice("_M"));
                    break;
                case 22:
                    this.buf.writeByte(78);
                    break;
                case 23:
                    if ((this.flags & Flags.IS_DMC) != 0)
                    {
                        this.buf.writestring(new ByteSlice("_Z"));
                    }
                    else
                    {
                        this.buf.writestring(new ByteSlice("_T"));
                    }
                    break;
                case 30:
                    this.buf.writestring(new ByteSlice("_N"));
                    break;
                case 31:
                    this.buf.writeByte(68);
                    break;
                case 32:
                    this.buf.writestring(new ByteSlice("_S"));
                    break;
                case 33:
                    this.buf.writestring(new ByteSlice("_U"));
                    break;
                default:
                this.visit((Type)type);
                return ;
            }
            this.flags &= -2;
            this.flags &= -5;
        }

        public  void visit(TypeVector type) {
            if (this.checkTypeSaved(type))
            {
                return ;
            }
            this.buf.writestring(new ByteSlice("T__m128@@"));
            this.flags &= -2;
            this.flags &= -5;
        }

        public  void visit(TypeSArray type) {
            if (this.checkTypeSaved(type))
            {
                return ;
            }
            if ((this.flags & Flags.IS_DMC) != 0)
            {
                this.buf.writeByte(81);
            }
            else
            {
                this.buf.writeByte(80);
            }
            this.flags |= Flags.IS_NOT_TOP_TYPE;
            assert(type.next.value != null);
            if (((type.next.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.mangleArray((TypeSArray)type.next.value);
            }
            else
            {
                type.next.value.accept(this);
            }
        }

        public  void visit(TypePointer type) {
            if (checkImmutableShared(type))
            {
                return ;
            }
            assert(type.next.value != null);
            if (((type.next.value.ty & 0xFF) == ENUMTY.Tfunction))
            {
                BytePtr arg = pcopy(this.mangleFunctionType((TypeFunction)type.next.value, false, false));
                if (this.checkTypeSaved(type))
                {
                    return ;
                }
                if (type.isConst())
                {
                    this.buf.writeByte(81);
                }
                else
                {
                    this.buf.writeByte(80);
                }
                this.buf.writeByte(54);
                this.buf.writestring(arg);
                this.flags &= -2;
                this.flags &= -5;
                return ;
            }
            else if (((type.next.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (this.checkTypeSaved(type))
                {
                    return ;
                }
                this.mangleModifier(type);
                if (type.isConst() || ((this.flags & Flags.IS_DMC) == 0))
                {
                    this.buf.writeByte(81);
                }
                else
                {
                    this.buf.writeByte(80);
                }
                if (global.params.is64bit)
                {
                    this.buf.writeByte(69);
                }
                this.flags |= Flags.IS_NOT_TOP_TYPE;
                this.mangleArray((TypeSArray)type.next.value);
                return ;
            }
            else
            {
                if (this.checkTypeSaved(type))
                {
                    return ;
                }
                this.mangleModifier(type);
                if (type.isConst())
                {
                    this.buf.writeByte(81);
                }
                else
                {
                    this.buf.writeByte(80);
                }
                if (global.params.is64bit)
                {
                    this.buf.writeByte(69);
                }
                this.flags |= Flags.IS_NOT_TOP_TYPE;
                type.next.value.accept(this);
            }
        }

        public  void visit(TypeReference type) {
            if (this.checkTypeSaved(type))
            {
                return ;
            }
            if (checkImmutableShared(type))
            {
                return ;
            }
            this.buf.writeByte(65);
            if (global.params.is64bit)
            {
                this.buf.writeByte(69);
            }
            this.flags |= Flags.IS_NOT_TOP_TYPE;
            assert(type.next.value != null);
            if (((type.next.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.mangleArray((TypeSArray)type.next.value);
            }
            else
            {
                type.next.value.accept(this);
            }
        }

        public  void visit(TypeFunction type) {
            BytePtr arg = pcopy(this.mangleFunctionType(type, false, false));
            if ((this.flags & Flags.IS_DMC) != 0)
            {
                if (this.checkTypeSaved(type))
                {
                    return ;
                }
            }
            else
            {
                this.buf.writestring(new ByteSlice("$$A6"));
            }
            this.buf.writestring(arg);
            this.flags &= -6;
        }

        public  void visit(TypeStruct type) {
            if (this.checkTypeSaved(type))
            {
                return ;
            }
            this.mangleModifier(type);
            if (type.sym.isUnionDeclaration() != null)
            {
                this.buf.writeByte(84);
            }
            else
            {
                this.buf.writeByte((type.cppmangle == CPPMANGLE.asClass) ? 86 : 85);
            }
            this.mangleIdent(type.sym, false);
            this.flags &= -2;
            this.flags &= -5;
        }

        public  void visit(TypeEnum type) {
            Identifier id = type.sym.ident;
            ByteSlice c = new RawByteSlice().copy();
            if ((pequals(id, Id.__c_long_double)))
            {
                c = new ByteSlice("O").copy();
            }
            else if ((pequals(id, Id.__c_long)))
            {
                c = new ByteSlice("J").copy();
            }
            else if ((pequals(id, Id.__c_ulong)))
            {
                c = new ByteSlice("K").copy();
            }
            else if ((pequals(id, Id.__c_longlong)))
            {
                c = new ByteSlice("_J").copy();
            }
            else if ((pequals(id, Id.__c_ulonglong)))
            {
                c = new ByteSlice("_K").copy();
            }
            else if ((pequals(id, Id.__c_wchar_t)))
            {
                c = ((this.flags & Flags.IS_DMC) != 0 ? new ByteSlice("_Y") : new ByteSlice("_W")).copy();
            }
            if (c.getLength() != 0)
            {
                if (checkImmutableShared(type))
                {
                    return ;
                }
                if (type.isConst() && ((this.flags & Flags.IS_NOT_TOP_TYPE) != 0) || ((this.flags & Flags.IS_DMC) != 0))
                {
                    if (this.checkTypeSaved(type))
                    {
                        return ;
                    }
                }
                this.mangleModifier(type);
                this.buf.writestring(c);
            }
            else
            {
                if (this.checkTypeSaved(type))
                {
                    return ;
                }
                this.mangleModifier(type);
                this.buf.writestring(new ByteSlice("W4"));
                this.mangleIdent(type.sym, false);
            }
            this.flags &= -2;
            this.flags &= -5;
        }

        public  void visit(TypeClass type) {
            if (this.checkTypeSaved(type))
            {
                return ;
            }
            if ((this.flags & Flags.IS_NOT_TOP_TYPE) != 0)
            {
                this.mangleModifier(type);
            }
            if (type.isConst())
            {
                this.buf.writeByte(81);
            }
            else
            {
                this.buf.writeByte(80);
            }
            if (global.params.is64bit)
            {
                this.buf.writeByte(69);
            }
            this.flags |= Flags.IS_NOT_TOP_TYPE;
            this.mangleModifier(type);
            this.buf.writeByte((type.cppmangle == CPPMANGLE.asStruct) ? 85 : 86);
            this.mangleIdent(type.sym, false);
            this.flags &= -2;
            this.flags &= -5;
        }

        public  BytePtr mangleOf(Dsymbol s) {
            VarDeclaration vd = s.isVarDeclaration();
            FuncDeclaration fd = s.isFuncDeclaration();
            if (vd != null)
            {
                this.mangleVariable(vd);
            }
            else if (fd != null)
            {
                this.mangleFunction(fd);
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            return this.buf.extractChars();
        }

        public  void mangleFunction(FuncDeclaration d) {
            assert(d != null);
            this.buf.writeByte(63);
            this.mangleIdent(d, false);
            if (d.needThis())
            {
                if (d.isVirtual() && (d.vtblIndex != -1) || (d.interfaceVirtual != null) || (d.overrideInterface() != null) || (d.isDtorDeclaration() != null) && (d.parent.value.isClassDeclaration() != null) && !d.isFinal())
                {
                    switch (d.protection.kind)
                    {
                        case Prot.Kind.private_:
                            this.buf.writeByte(69);
                            break;
                        case Prot.Kind.protected_:
                            this.buf.writeByte(77);
                            break;
                        default:
                        this.buf.writeByte(85);
                        break;
                    }
                }
                else
                {
                    switch (d.protection.kind)
                    {
                        case Prot.Kind.private_:
                            this.buf.writeByte(65);
                            break;
                        case Prot.Kind.protected_:
                            this.buf.writeByte(73);
                            break;
                        default:
                        this.buf.writeByte(81);
                        break;
                    }
                }
                if (global.params.is64bit)
                {
                    this.buf.writeByte(69);
                }
                if (d.type.isConst())
                {
                    this.buf.writeByte(66);
                }
                else
                {
                    this.buf.writeByte(65);
                }
            }
            else if (d.isMember2() != null)
            {
                switch (d.protection.kind)
                {
                    case Prot.Kind.private_:
                        this.buf.writeByte(67);
                        break;
                    case Prot.Kind.protected_:
                        this.buf.writeByte(75);
                        break;
                    default:
                    this.buf.writeByte(83);
                    break;
                }
            }
            else
            {
                this.buf.writeByte(89);
            }
            BytePtr args = pcopy(this.mangleFunctionType((TypeFunction)d.type, d.needThis(), (d.isCtorDeclaration() != null) || isPrimaryDtor(d)));
            this.buf.writestring(args);
        }

        public  void mangleVariable(VarDeclaration d) {
            assert(d != null);
            if ((d.storage_class & 1073741890L) == 0)
            {
                d.error(new BytePtr("Internal Compiler Error: C++ static non-__gshared non-extern variables not supported"));
                fatal();
            }
            this.buf.writeByte(63);
            this.mangleIdent(d, false);
            assert(((d.storage_class & 64L) != 0) || !d.needThis());
            Dsymbol parent = d.toParent();
            for (; (parent != null) && (parent.isNspace() != null);){
                parent = parent.toParent();
            }
            if ((parent != null) && (parent.isModule() != null))
            {
                this.buf.writeByte(51);
            }
            else
            {
                switch (d.protection.kind)
                {
                    case Prot.Kind.private_:
                        this.buf.writeByte(48);
                        break;
                    case Prot.Kind.protected_:
                        this.buf.writeByte(49);
                        break;
                    default:
                    this.buf.writeByte(50);
                    break;
                }
            }
            byte cv_mod = (byte)0;
            Type t = d.type;
            if (checkImmutableShared(t))
            {
                return ;
            }
            if (t.isConst())
            {
                cv_mod = (byte)66;
            }
            else
            {
                cv_mod = (byte)65;
            }
            if (((t.ty & 0xFF) != ENUMTY.Tpointer))
            {
                t = t.mutableOf();
            }
            t.accept(this);
            if (((t.ty & 0xFF) == ENUMTY.Tpointer) || ((t.ty & 0xFF) == ENUMTY.Treference) || ((t.ty & 0xFF) == ENUMTY.Tclass) && global.params.is64bit)
            {
                this.buf.writeByte(69);
            }
            this.buf.writeByte((cv_mod & 0xFF));
        }

        public static ByteSlice mangleSpecialName(Dsymbol sym) {
            ByteSlice mangle = new RawByteSlice().copy();
            if (sym.isCtorDeclaration() != null)
            {
                mangle = new ByteSlice("?0").copy();
            }
            else if (isPrimaryDtor(sym))
            {
                mangle = new ByteSlice("?1").copy();
            }
            else if (sym.ident == null)
            {
                return new ByteSlice();
            }
            else if ((pequals(sym.ident, Id.assign)))
            {
                mangle = new ByteSlice("?4").copy();
            }
            else if ((pequals(sym.ident, Id.eq)))
            {
                mangle = new ByteSlice("?8").copy();
            }
            else if ((pequals(sym.ident, Id.index)))
            {
                mangle = new ByteSlice("?A").copy();
            }
            else if ((pequals(sym.ident, Id.call)))
            {
                mangle = new ByteSlice("?R").copy();
            }
            else if ((pequals(sym.ident, Id.cppdtor)))
            {
                mangle = new ByteSlice("?_G").copy();
            }
            else
            {
                return new ByteSlice();
            }
            return mangle;
        }

        public  boolean mangleOperator(TemplateInstance ti, Ref<BytePtr> symName, Ref<Integer> firstTemplateArg) {
            int whichOp = isCppOperator(ti.name);
            try {
                {
                    int __dispatch6 = 0;
                    dispatched_6:
                    do {
                        switch (__dispatch6 != 0 ? __dispatch6 : whichOp)
                        {
                            case CppOperator.Unknown:
                                return false;
                            case CppOperator.Cast:
                                this.buf.writestring(new ByteSlice("?B"));
                                return true;
                            case CppOperator.Assign:
                                symName.value = pcopy(new BytePtr("?4"));
                                return false;
                            case CppOperator.Eq:
                                symName.value = pcopy(new BytePtr("?8"));
                                return false;
                            case CppOperator.Index:
                                symName.value = pcopy(new BytePtr("?A"));
                                return false;
                            case CppOperator.Call:
                                symName.value = pcopy(new BytePtr("?R"));
                                return false;
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
                                    return false;
                                }
                                Expression exp = isExpression((ti.tiargs.get()).get(0));
                                StringExp str = exp.toStringExp();
                                {
                                    int __dispatch7 = 0;
                                    dispatched_7:
                                    do {
                                        switch (__dispatch7 != 0 ? __dispatch7 : whichOp)
                                        {
                                            case CppOperator.Unary:
                                                {
                                                    int __dispatch8 = 0;
                                                    dispatched_8:
                                                    do {
                                                        switch (__dispatch8 != 0 ? __dispatch8 : __switch(str.peekSlice()))
                                                        {
                                                            case 0:
                                                                symName.value = pcopy(new BytePtr("?D"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 4:
                                                                symName.value = pcopy(new BytePtr("?E"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 5:
                                                                symName.value = pcopy(new BytePtr("?F"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 2:
                                                                symName.value = pcopy(new BytePtr("?G"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 1:
                                                                symName.value = pcopy(new BytePtr("?H"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 3:
                                                                symName.value = pcopy(new BytePtr("?S"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            default:
                                                            return false;
                                                        }
                                                    } while(__dispatch8 != 0);
                                                }
                                            case CppOperator.Binary:
                                                {
                                                    int __dispatch9 = 0;
                                                    dispatched_9:
                                                    do {
                                                        switch (__dispatch9 != 0 ? __dispatch9 : __switch(str.peekSlice()))
                                                        {
                                                            case 9:
                                                                symName.value = pcopy(new BytePtr("?5"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 8:
                                                                symName.value = pcopy(new BytePtr("?6"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 2:
                                                                symName.value = pcopy(new BytePtr("?D"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 4:
                                                                symName.value = pcopy(new BytePtr("?G"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 3:
                                                                symName.value = pcopy(new BytePtr("?H"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 1:
                                                                symName.value = pcopy(new BytePtr("?I"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 5:
                                                                symName.value = pcopy(new BytePtr("?K"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 0:
                                                                symName.value = pcopy(new BytePtr("?L"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 6:
                                                                symName.value = pcopy(new BytePtr("?T"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 7:
                                                                symName.value = pcopy(new BytePtr("?U"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            default:
                                                            return false;
                                                        }
                                                    } while(__dispatch9 != 0);
                                                }
                                            case CppOperator.OpAssign:
                                                {
                                                    int __dispatch10 = 0;
                                                    dispatched_10:
                                                    do {
                                                        switch (__dispatch10 != 0 ? __dispatch10 : __switch(str.peekSlice()))
                                                        {
                                                            case 2:
                                                                symName.value = pcopy(new BytePtr("?X"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 3:
                                                                symName.value = pcopy(new BytePtr("?Y"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 4:
                                                                symName.value = pcopy(new BytePtr("?Z"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 5:
                                                                symName.value = pcopy(new BytePtr("?_0"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 0:
                                                                symName.value = pcopy(new BytePtr("?_1"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 9:
                                                                symName.value = pcopy(new BytePtr("?_2"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 8:
                                                                symName.value = pcopy(new BytePtr("?_3"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 1:
                                                                symName.value = pcopy(new BytePtr("?_4"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 7:
                                                                symName.value = pcopy(new BytePtr("?_5"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            case 6:
                                                                symName.value = pcopy(new BytePtr("?_6"));
                                                                /*goto continue_template*/throw Dispatch0.INSTANCE;
                                                            default:
                                                            return false;
                                                        }
                                                    } while(__dispatch10 != 0);
                                                }
                                            default:
                                            throw new AssertionError("Unreachable code!");
                                        }
                                    } while(__dispatch7 != 0);
                                }
                            default:
                            throw SwitchError.INSTANCE;
                        }
                    } while(__dispatch6 != 0);
                }
            }
            catch(Dispatch0 __d){}
        /*continue_template:*/
            if (((ti.tiargs.get()).length == 1))
            {
                this.buf.writestring(symName.value);
                return true;
            }
            firstTemplateArg.value = 1;
            return false;
        }

        public  void manlgeTemplateValue(RootObject o, TemplateValueParameter tv, Dsymbol sym, boolean is_dmc_template) {
            if (!tv.valType.isintegral())
            {
                sym.error(new BytePtr("Internal Compiler Error: C++ %s template value parameter is not supported"), tv.valType.toChars());
                fatal();
                return ;
            }
            this.buf.writeByte(36);
            this.buf.writeByte(48);
            Expression e = isExpression(o);
            assert(e != null);
            if (tv.valType.isunsigned())
            {
                this.mangleNumber(e.toUInteger());
            }
            else if (is_dmc_template)
            {
                this.mangleNumber(e.toInteger());
            }
            else
            {
                long val = (long)e.toInteger();
                if ((val < 0L))
                {
                    val = -val;
                    this.buf.writeByte(63);
                }
                this.mangleNumber((long)val);
            }
        }

        public  void mangleTemplateAlias(RootObject o, Dsymbol sym) {
            Dsymbol d = isDsymbol(o);
            Expression e = isExpression(o);
            if ((d != null) && (d.isFuncDeclaration() != null))
            {
                this.buf.writeByte(36);
                this.buf.writeByte(49);
                this.mangleFunction(d.isFuncDeclaration());
            }
            else if ((e != null) && ((e.op & 0xFF) == 26) && (((VarExp)e).var.isVarDeclaration() != null))
            {
                this.buf.writeByte(36);
                if ((this.flags & Flags.IS_DMC) != 0)
                {
                    this.buf.writeByte(49);
                }
                else
                {
                    this.buf.writeByte(69);
                }
                this.mangleVariable(((VarExp)e).var.isVarDeclaration());
            }
            else if ((d != null) && (d.isTemplateDeclaration() != null) && (d.isTemplateDeclaration().onemember != null))
            {
                Dsymbol ds = d.isTemplateDeclaration().onemember;
                if ((this.flags & Flags.IS_DMC) != 0)
                {
                    this.buf.writeByte(86);
                }
                else
                {
                    if (ds.isUnionDeclaration() != null)
                    {
                        this.buf.writeByte(84);
                    }
                    else if (ds.isStructDeclaration() != null)
                    {
                        this.buf.writeByte(85);
                    }
                    else if (ds.isClassDeclaration() != null)
                    {
                        this.buf.writeByte(86);
                    }
                    else
                    {
                        sym.error(new BytePtr("Internal Compiler Error: C++ templates support only integral value, type parameters, alias templates and alias function parameters"));
                        fatal();
                    }
                }
                this.mangleIdent(d, false);
            }
            else
            {
                sym.error(new BytePtr("Internal Compiler Error: `%s` is unsupported parameter for C++ template"), o.toChars());
                fatal();
            }
        }

        public  void mangleTemplateType(RootObject o) {
            this.flags |= Flags.ESCAPE;
            Type t = isType(o);
            assert(t != null);
            t.accept(this);
            this.flags &= -17;
        }

        public  void mangleName(Dsymbol sym, boolean dont_use_back_reference) {
            BytePtr name = null;
            boolean is_dmc_template = false;
            {
                ByteSlice s = mangleSpecialName(sym).copy();
                if ((s).getLength() != 0)
                {
                    this.buf.writestring(s);
                    return ;
                }
            }
            {
                TemplateInstance ti = sym.isTemplateInstance();
                if ((ti) != null)
                {
                    Identifier id = ti.tempdecl.ident;
                    Ref<BytePtr> symName = ref(pcopy(id.toChars()));
                    Ref<Integer> firstTemplateArg = ref(0);
                    if (this.mangleOperator(ti, symName, firstTemplateArg))
                    {
                        return ;
                    }
                    VisualCPPMangler tmp = new VisualCPPMangler((this.flags & Flags.IS_DMC) != 0 ? true : false);
                    tmp.buf.writeByte(63);
                    tmp.buf.writeByte(36);
                    tmp.buf.writestring(symName.value);
                    tmp.saved_idents.set(0, symName.value);
                    if ((symName.value == id.toChars()))
                    {
                        tmp.buf.writeByte(64);
                    }
                    if ((this.flags & Flags.IS_DMC) != 0)
                    {
                        tmp.mangleIdent(sym.parent.value, true);
                        is_dmc_template = true;
                    }
                    boolean is_var_arg = false;
                    {
                        int i = firstTemplateArg.value;
                        for (; (i < (ti.tiargs.get()).length);i++){
                            RootObject o = (ti.tiargs.get()).get(i);
                            TemplateParameter tp = null;
                            TemplateValueParameter tv = null;
                            TemplateTupleParameter tt = null;
                            if (!is_var_arg)
                            {
                                TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                                assert(td != null);
                                tp = (td.parameters.get()).get(i);
                                tv = tp.isTemplateValueParameter();
                                tt = tp.isTemplateTupleParameter();
                            }
                            if (tt != null)
                            {
                                is_var_arg = true;
                                tp = null;
                            }
                            if (tv != null)
                            {
                                tmp.manlgeTemplateValue(o, tv, sym, is_dmc_template);
                            }
                            else if ((tp == null) || (tp.isTemplateTypeParameter() != null))
                            {
                                tmp.mangleTemplateType(o);
                            }
                            else if (tp.isTemplateAliasParameter() != null)
                            {
                                tmp.mangleTemplateAlias(o, sym);
                            }
                            else
                            {
                                sym.error(new BytePtr("Internal Compiler Error: C++ templates support only integral value, type parameters, alias templates and alias function parameters"));
                                fatal();
                            }
                        }
                    }
                    name = pcopy(tmp.buf.extractChars());
                }
                else
                {
                    name = pcopy(sym.ident.toChars());
                }
            }
            assert(name != null);
            if (is_dmc_template)
            {
                if (this.checkAndSaveIdent(name))
                {
                    return ;
                }
            }
            else
            {
                if (dont_use_back_reference)
                {
                    this.saveIdent(name);
                }
                else
                {
                    if (this.checkAndSaveIdent(name))
                    {
                        return ;
                    }
                }
            }
            this.buf.writestring(name);
            this.buf.writeByte(64);
        }

        public  boolean checkAndSaveIdent(BytePtr name) {
            {
                int __key862 = 0;
                int __limit863 = 10;
                for (; (__key862 < __limit863);__key862 += 1) {
                    int i = __key862;
                    if (this.saved_idents.get(i) == null)
                    {
                        this.saved_idents.set(i, name);
                        break;
                    }
                    if (strcmp(this.saved_idents.get(i), name) == 0)
                    {
                        this.buf.writeByte(i + 48);
                        return true;
                    }
                }
            }
            return false;
        }

        public  void saveIdent(BytePtr name) {
            {
                int __key864 = 0;
                int __limit865 = 10;
                for (; (__key864 < __limit865);__key864 += 1) {
                    int i = __key864;
                    if (this.saved_idents.get(i) == null)
                    {
                        this.saved_idents.set(i, name);
                        break;
                    }
                    if (strcmp(this.saved_idents.get(i), name) == 0)
                    {
                        return ;
                    }
                }
            }
        }

        public  void mangleIdent(Dsymbol sym, boolean dont_use_back_reference) {
            Dsymbol p = sym;
            if ((p.toParent() != null) && (p.toParent().isTemplateInstance() != null))
            {
                p = p.toParent();
            }
            for (; (p != null) && (p.isModule() == null);){
                this.mangleName(p, dont_use_back_reference);
                {
                    CPPNamespaceDeclaration ns = p.namespace;
                    for (; (ns != null);ns = ns.namespace) {
                        this.mangleName(ns, dont_use_back_reference);
                    }
                }
                p = p.toParent();
                if ((p.toParent() != null) && (p.toParent().isTemplateInstance() != null))
                {
                    p = p.toParent();
                }
            }
            if (!dont_use_back_reference)
            {
                this.buf.writeByte(64);
            }
        }

        // defaulted all parameters starting with #2
        public  void mangleIdent(Dsymbol sym) {
            mangleIdent(sym, false);
        }

        public  void mangleNumber(long num) {
            if (num == 0)
            {
                this.buf.writeByte(65);
                this.buf.writeByte(64);
                return ;
            }
            if ((num <= 10L))
            {
                this.buf.writeByte(((byte)(num - 1L + 48L) & 0xFF));
                return ;
            }
            ByteSlice buff = (byte)255;
            buff.set(16, (byte)0);
            int i = 16;
            for (; num != 0;){
                i -= 1;
                buff.set(i, (byte)(num % 16L + 65L));
                num /= 16L;
            }
            this.buf.writestring(ptr(buff.get(i)));
            this.buf.writeByte(64);
        }

        public  boolean checkTypeSaved(Type type) {
            if ((this.flags & Flags.IS_NOT_TOP_TYPE) != 0)
            {
                return false;
            }
            if ((this.flags & Flags.MANGLE_RETURN_TYPE) != 0)
            {
                return false;
            }
            {
                int i = 0;
                for (; (i < 10);i++){
                    if (this.saved_types.get(i) == null)
                    {
                        this.saved_types.set(i, type);
                        return false;
                    }
                    if (this.saved_types.get(i).equals(type))
                    {
                        this.buf.writeByte(i + 48);
                        this.flags &= -2;
                        this.flags &= -5;
                        return true;
                    }
                }
            }
            return false;
        }

        public  void mangleModifier(Type type) {
            if ((this.flags & Flags.IGNORE_CONST) != 0)
            {
                return ;
            }
            if (checkImmutableShared(type))
            {
                return ;
            }
            if (type.isConst())
            {
                if (((this.flags & Flags.ESCAPE) != 0) && ((type.ty & 0xFF) != ENUMTY.Tpointer))
                {
                    this.buf.writestring(new ByteSlice("$$CB"));
                }
                else if ((this.flags & Flags.IS_NOT_TOP_TYPE) != 0)
                {
                    this.buf.writeByte(66);
                }
                else if (((this.flags & Flags.IS_DMC) != 0) && ((type.ty & 0xFF) != ENUMTY.Tpointer))
                {
                    this.buf.writestring(new ByteSlice("_O"));
                }
            }
            else if ((this.flags & Flags.IS_NOT_TOP_TYPE) != 0)
            {
                this.buf.writeByte(65);
            }
            this.flags &= -17;
        }

        public  void mangleArray(TypeSArray type) {
            this.mangleModifier(type);
            int i = 0;
            Type cur = type;
            for (; (cur != null) && ((cur.ty & 0xFF) == ENUMTY.Tsarray);){
                i++;
                cur = cur.nextOf();
            }
            this.buf.writeByte(89);
            this.mangleNumber((long)i);
            cur = type;
            for (; (cur != null) && ((cur.ty & 0xFF) == ENUMTY.Tsarray);){
                TypeSArray sa = (TypeSArray)cur;
                this.mangleNumber(sa.dim != null ? sa.dim.toInteger() : 0L);
                cur = cur.nextOf();
            }
            this.flags |= Flags.IGNORE_CONST;
            cur.accept(this);
        }

        public  BytePtr mangleFunctionType(TypeFunction type, boolean needthis, boolean noreturn) {
            VisualCPPMangler tmp = new VisualCPPMangler(this);
            if (global.params.is64bit)
            {
                tmp.buf.writeByte(65);
            }
            else
            {
                switch (type.linkage)
                {
                    case LINK.c:
                        tmp.buf.writeByte(65);
                        break;
                    case LINK.cpp:
                        if (needthis && (type.parameterList.varargs != VarArg.variadic))
                        {
                            tmp.buf.writeByte(69);
                        }
                        else
                        {
                            tmp.buf.writeByte(65);
                        }
                        break;
                    case LINK.windows:
                        tmp.buf.writeByte(71);
                        break;
                    case LINK.pascal:
                        tmp.buf.writeByte(67);
                        break;
                    case LINK.d:
                    case LINK.default_:
                    case LINK.system:
                    case LINK.objc:
                        tmp.visit((Type)type);
                        break;
                    default:
                    throw SwitchError.INSTANCE;
                }
            }
            tmp.flags &= -2;
            if (noreturn)
            {
                tmp.buf.writeByte(64);
            }
            else
            {
                Type rettype = type.next.value;
                if (type.isref)
                {
                    rettype = rettype.referenceTo();
                }
                this.flags &= -5;
                if (((rettype.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    tmp.buf.writeByte(63);
                    tmp.buf.writeByte(65);
                }
                else if (((rettype.ty & 0xFF) == ENUMTY.Tenum))
                {
                    Identifier id = rettype.toDsymbol(null).ident;
                    if (!isSpecialEnumIdent(id))
                    {
                        tmp.buf.writeByte(63);
                        tmp.buf.writeByte(65);
                    }
                }
                tmp.flags |= Flags.MANGLE_RETURN_TYPE;
                rettype.accept(tmp);
                tmp.flags &= -3;
            }
            if ((type.parameterList.parameters == null) || ((type.parameterList.parameters.get()).length == 0))
            {
                if ((type.parameterList.varargs == VarArg.variadic))
                {
                    tmp.buf.writeByte(90);
                }
                else
                {
                    tmp.buf.writeByte(88);
                }
            }
            else
            {
                Function2<Integer,Parameter,Integer> mangleParameterDg = new Function2<Integer,Parameter,Integer>() {
                    public Integer invoke(Integer n, Parameter p) {
                     {
                        Ref<Type> t = ref(p.type);
                        if ((p.storageClass & 2101248L) != 0)
                        {
                            t.value = t.value.referenceTo();
                        }
                        else if ((p.storageClass & 8192L) != 0)
                        {
                            Ref<Type> td = ref(new TypeFunction(new ParameterList(null, VarArg.none), t.value, LINK.d, 0L));
                            td.value = new TypeDelegate(td.value);
                            t.value = merge(t.value);
                        }
                        if (((t.value.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            error(Loc.initial, new BytePtr("Internal Compiler Error: unable to pass static array to `extern(C++)` function."));
                            error(Loc.initial, new BytePtr("Use pointer instead."));
                            throw new AssertionError("Unreachable code!");
                        }
                        tmp.flags &= -2;
                        tmp.flags &= -5;
                        t.value.accept(tmp);
                        return 0;
                    }}

                };
                Parameter._foreach(type.parameterList.parameters, mangleParameterDg, null);
                if ((type.parameterList.varargs == VarArg.variadic))
                {
                    tmp.buf.writeByte(90);
                }
                else
                {
                    tmp.buf.writeByte(64);
                }
            }
            tmp.buf.writeByte(90);
            BytePtr ret = pcopy(tmp.buf.extractChars());
            memcpy((BytePtr)(ptr(this.saved_idents)), (ptr(tmp.saved_idents)), 40);
            memcpy((BytePtr)(ptr(this.saved_types)), (ptr(tmp.saved_types)), 40);
            return ret;
        }

        // defaulted all parameters starting with #3
        public  BytePtr mangleFunctionType(TypeFunction type, boolean needthis) {
            return mangleFunctionType(type, needthis, false);
        }

        // defaulted all parameters starting with #2
        public  BytePtr mangleFunctionType(TypeFunction type) {
            return mangleFunctionType(type, false, false);
        }


        public VisualCPPMangler() {}

        public VisualCPPMangler copy() {
            VisualCPPMangler that = new VisualCPPMangler();
            that.VC_SAVED_TYPE_CNT = this.VC_SAVED_TYPE_CNT;
            that.VC_SAVED_IDENT_CNT = this.VC_SAVED_IDENT_CNT;
            that.saved_idents = this.saved_idents;
            that.saved_types = this.saved_types;
            that.flags = this.flags;
            that.buf = this.buf;
            return that;
        }
    }
}
