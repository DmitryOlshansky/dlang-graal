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
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.visitor.*;

public class dmangle {
    private static final ByteSlice initializer_0 = {(byte)65, (byte)71, (byte)72, (byte)80, (byte)82, (byte)70, (byte)73, (byte)67, (byte)83, (byte)69, (byte)68, (byte)110, (byte)118, (byte)103, (byte)104, (byte)115, (byte)116, (byte)105, (byte)107, (byte)108, (byte)109, (byte)102, (byte)100, (byte)101, (byte)111, (byte)112, (byte)106, (byte)113, (byte)114, (byte)99, (byte)98, (byte)97, (byte)117, (byte)119, (byte)64, (byte)64, (byte)64, (byte)66, (byte)64, (byte)64, (byte)110, (byte)64, (byte)122, (byte)122, (byte)64};

    static ByteSlice mangleChar = slice(initializer_0);
    // Erasure: tyToDecoBuffer<Ptr, int>
    public static void tyToDecoBuffer(Ptr<OutBuffer> buf, int ty) {
        byte c = mangleChar.get(ty);
        (buf.get()).writeByte((c & 0xFF));
        if (((c & 0xFF) == 122))
        {
            (buf.get()).writeByte((ty == ENUMTY.Tint128) ? 105 : 107);
        }
    }

    // Erasure: MODtoDecoBuffer<Ptr, byte>
    public static void MODtoDecoBuffer(Ptr<OutBuffer> buf, byte mod) {
        switch ((mod & 0xFF))
        {
            case 0:
                break;
            case 1:
                (buf.get()).writeByte(120);
                break;
            case 4:
                (buf.get()).writeByte(121);
                break;
            case 2:
                (buf.get()).writeByte(79);
                break;
            case 3:
                (buf.get()).writestring(new ByteSlice("Ox"));
                break;
            case 8:
                (buf.get()).writestring(new ByteSlice("Ng"));
                break;
            case 9:
                (buf.get()).writestring(new ByteSlice("Ngx"));
                break;
            case 10:
                (buf.get()).writestring(new ByteSlice("ONg"));
                break;
            case 11:
                (buf.get()).writestring(new ByteSlice("ONgx"));
                break;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }

    public static class Mangler extends Visitor
    {
        public AA<Type,Integer> types = new AA<Type,Integer>();
        public AA<Identifier,Integer> idents = new AA<Identifier,Integer>();
        public Ptr<OutBuffer> buf = null;
        // Erasure: __ctor<Ptr>
        public  Mangler(Ptr<OutBuffer> buf) {
            this.buf = pcopy(buf);
        }

        // Erasure: writeBackRef<int>
        public  void writeBackRef(int pos) {
            (this.buf.get()).writeByte(81);
            int base = 26;
            int mul = 1;
            for (; (pos >= mul * 26);) {
                mul *= 26;
            }
            for (; (mul >= 26);){
                byte dig = (byte)(pos / mul);
                (this.buf.get()).writeByte((65 + (dig & 0xFF)));
                pos -= (dig & 0xFF) * mul;
                mul /= 26;
            }
            (this.buf.get()).writeByte((97 + ((byte)pos & 0xFF)));
        }

        // Erasure: backrefType<Type>
        public  boolean backrefType(Type t) {
            if (t.isTypeBasic() == null)
            {
                Ptr<Integer> p = pcopy(this.types.getLvalue(t));
                if (p.get() != 0)
                {
                    this.writeBackRef((this.buf.get()).offset - p.get());
                    return true;
                }
                p.set(0, (this.buf.get()).offset);
            }
            return false;
        }

        // Erasure: backrefIdentifier<Identifier>
        public  boolean backrefIdentifier(Identifier id) {
            Ptr<Integer> p = pcopy(this.idents.getLvalue(id));
            if (p.get() != 0)
            {
                this.writeBackRef((this.buf.get()).offset - p.get());
                return true;
            }
            p.set(0, (this.buf.get()).offset);
            return false;
        }

        // Erasure: mangleSymbol<Dsymbol>
        public  void mangleSymbol(Dsymbol s) {
            s.accept(this);
        }

        // Erasure: mangleType<Type>
        public  void mangleType(Type t) {
            if (!this.backrefType(t))
            {
                t.accept(this);
            }
        }

        // Erasure: mangleIdentifier<Identifier, Dsymbol>
        public  void mangleIdentifier(Identifier id, Dsymbol s) {
            if (!this.backrefIdentifier(id))
            {
                this.toBuffer(id.asString(), s);
            }
        }

        // Erasure: visitWithMask<Type, byte>
        public  void visitWithMask(Type t, byte modMask) {
            if (((modMask & 0xFF) != (t.mod & 0xFF)))
            {
                MODtoDecoBuffer(this.buf, t.mod);
            }
            this.mangleType(t);
        }

        // Erasure: visit<Type>
        public  void visit(Type t) {
            tyToDecoBuffer(this.buf, (t.ty & 0xFF));
        }

        // Erasure: visit<TypeNext>
        public  void visit(TypeNext t) {
            this.visit((Type)t);
            this.visitWithMask(t.next.value, t.mod);
        }

        // Erasure: visit<TypeVector>
        public  void visit(TypeVector t) {
            (this.buf.get()).writestring(new ByteSlice("Nh"));
            this.visitWithMask(t.basetype, t.mod);
        }

        // Erasure: visit<TypeSArray>
        public  void visit(TypeSArray t) {
            this.visit((Type)t);
            if (t.dim != null)
            {
                (this.buf.get()).print(t.dim.toInteger());
            }
            if (t.next.value != null)
            {
                this.visitWithMask(t.next.value, t.mod);
            }
        }

        // Erasure: visit<TypeDArray>
        public  void visit(TypeDArray t) {
            this.visit((Type)t);
            if (t.next.value != null)
            {
                this.visitWithMask(t.next.value, t.mod);
            }
        }

        // Erasure: visit<TypeAArray>
        public  void visit(TypeAArray t) {
            this.visit((Type)t);
            this.visitWithMask(t.index, (byte)0);
            this.visitWithMask(t.next.value, t.mod);
        }

        // Erasure: visit<TypeFunction>
        public  void visit(TypeFunction t) {
            this.mangleFuncType(t, t, t.mod, t.next.value);
        }

        // Erasure: mangleFuncType<TypeFunction, TypeFunction, byte, Type>
        public  void mangleFuncType(TypeFunction t, TypeFunction ta, byte modMask, Type tret) {
            if ((t.inuse != 0) && (tret != null))
            {
                t.inuse = 2;
                return ;
            }
            t.inuse++;
            if (((modMask & 0xFF) != (t.mod & 0xFF)))
            {
                MODtoDecoBuffer(this.buf, t.mod);
            }
            byte mc = (byte)255;
            switch (t.linkage)
            {
                case LINK.default_:
                case LINK.system:
                case LINK.d:
                    mc = (byte)70;
                    break;
                case LINK.c:
                    mc = (byte)85;
                    break;
                case LINK.windows:
                    mc = (byte)87;
                    break;
                case LINK.pascal:
                    mc = (byte)86;
                    break;
                case LINK.cpp:
                    mc = (byte)82;
                    break;
                case LINK.objc:
                    mc = (byte)89;
                    break;
                default:
                throw SwitchError.INSTANCE;
            }
            (this.buf.get()).writeByte((mc & 0xFF));
            if (ta.purity != 0)
            {
                (this.buf.get()).writestring(new ByteSlice("Na"));
            }
            if (ta.isnothrow)
            {
                (this.buf.get()).writestring(new ByteSlice("Nb"));
            }
            if (ta.isref)
            {
                (this.buf.get()).writestring(new ByteSlice("Nc"));
            }
            if (ta.isproperty)
            {
                (this.buf.get()).writestring(new ByteSlice("Nd"));
            }
            if (ta.isnogc)
            {
                (this.buf.get()).writestring(new ByteSlice("Ni"));
            }
            if (ta.isreturn && !ta.isreturninferred)
            {
                (this.buf.get()).writestring(new ByteSlice("Nj"));
            }
            else if (ta.isscope && !ta.isscopeinferred)
            {
                (this.buf.get()).writestring(new ByteSlice("Nl"));
            }
            switch (ta.trust)
            {
                case TRUST.trusted:
                    (this.buf.get()).writestring(new ByteSlice("Ne"));
                    break;
                case TRUST.safe:
                    (this.buf.get()).writestring(new ByteSlice("Nf"));
                    break;
                default:
                break;
            }
            this.paramsToDecoBuffer(t.parameterList.parameters);
            (this.buf.get()).writeByte((90 - t.parameterList.varargs));
            if ((tret != null))
            {
                this.visitWithMask(tret, (byte)0);
            }
            t.inuse--;
        }

        // Erasure: visit<TypeIdentifier>
        public  void visit(TypeIdentifier t) {
            this.visit((Type)t);
            ByteSlice name = t.ident.asString().copy();
            (this.buf.get()).print((long)name.getLength());
            (this.buf.get()).writestring(name);
        }

        // Erasure: visit<TypeEnum>
        public  void visit(TypeEnum t) {
            this.visit((Type)t);
            this.mangleSymbol(t.sym);
        }

        // Erasure: visit<TypeStruct>
        public  void visit(TypeStruct t) {
            this.visit((Type)t);
            this.mangleSymbol(t.sym);
        }

        // Erasure: visit<TypeClass>
        public  void visit(TypeClass t) {
            this.visit((Type)t);
            this.mangleSymbol(t.sym);
        }

        // Erasure: visit<TypeTuple>
        public  void visit(TypeTuple t) {
            this.visit((Type)t);
            this.paramsToDecoBuffer(t.arguments);
            (this.buf.get()).writeByte(90);
        }

        // Erasure: visit<TypeNull>
        public  void visit(TypeNull t) {
            this.visit((Type)t);
        }

        // Erasure: mangleDecl<Declaration>
        public  void mangleDecl(Declaration sthis) {
            this.mangleParent(sthis);
            assert(sthis.ident != null);
            this.mangleIdentifier(sthis.ident, sthis);
            {
                FuncDeclaration fd = sthis.isFuncDeclaration();
                if ((fd) != null)
                {
                    this.mangleFunc(fd, false);
                }
                else if (sthis.type != null)
                {
                    this.visitWithMask(sthis.type, (byte)0);
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
            }
        }

        // Erasure: mangleParent<Dsymbol>
        public  void mangleParent(Dsymbol s) {
            Dsymbol p = null;
            {
                TemplateInstance ti = s.isTemplateInstance();
                if ((ti) != null)
                {
                    p = ti.isTemplateMixin() != null ? ti.parent.value : ti.tempdecl.parent.value;
                }
                else
                {
                    p = s.parent.value;
                }
            }
            if (p != null)
            {
                this.mangleParent(p);
                TemplateInstance ti = p.isTemplateInstance();
                if ((ti != null) && (ti.isTemplateMixin() == null))
                {
                    this.mangleTemplateInstance(ti);
                }
                else if (p.getIdent() != null)
                {
                    this.mangleIdentifier(p.ident, s);
                    {
                        FuncDeclaration f = p.isFuncDeclaration();
                        if ((f) != null)
                        {
                            this.mangleFunc(f, true);
                        }
                    }
                }
                else
                {
                    (this.buf.get()).writeByte(48);
                }
            }
        }

        // Erasure: mangleFunc<FuncDeclaration, boolean>
        public  void mangleFunc(FuncDeclaration fd, boolean inParent) {
            if (fd.needThis() || fd.isNested())
            {
                (this.buf.get()).writeByte(77);
            }
            if ((fd.type == null) || ((fd.type.ty & 0xFF) == ENUMTY.Terror))
            {
                (this.buf.get()).writestring(new ByteSlice("9__error__FZ"));
            }
            else if (inParent)
            {
                TypeFunction tf = fd.type.isTypeFunction();
                TypeFunction tfo = fd.originalType.isTypeFunction();
                this.mangleFuncType(tf, tfo, (byte)0, null);
            }
            else
            {
                this.visitWithMask(fd.type, (byte)0);
            }
        }

        // Erasure: toBuffer<Array, Dsymbol>
        public  void toBuffer(ByteSlice id, Dsymbol s) {
            int len = id.getLength();
            if (((this.buf.get()).offset + len >= 8388608))
            {
                s.error(new BytePtr("excessive length %llu for symbol, possible recursive expansion?"), (long)((this.buf.get()).offset + len));
            }
            else
            {
                (this.buf.get()).print((long)len);
                (this.buf.get()).writestring(id);
            }
        }

        // Erasure: externallyMangledIdentifier<Declaration>
        public static ByteSlice externallyMangledIdentifier(Declaration d) {
            if ((d.parent.value == null) || (d.parent.value.isModule() != null) || (d.linkage == LINK.cpp))
            {
                switch (d.linkage)
                {
                    case LINK.d:
                        break;
                    case LINK.c:
                    case LINK.windows:
                    case LINK.pascal:
                    case LINK.objc:
                        return d.ident.asString();
                    case LINK.cpp:
                        BytePtr p = pcopy(target.toCppMangle(d));
                        return p.slice(0,strlen(p));
                    case LINK.default_:
                    case LINK.system:
                        d.error(new BytePtr("forward declaration"));
                        return d.ident.asString();
                    default:
                    throw SwitchError.INSTANCE;
                }
            }
            return new ByteSlice();
        }

        // Erasure: visit<Declaration>
        public  void visit(Declaration d) {
            {
                ByteSlice id = externallyMangledIdentifier(d).copy();
                if ((id).getLength() != 0)
                {
                    (this.buf.get()).writestring(id);
                    return ;
                }
            }
            (this.buf.get()).writestring(new ByteSlice("_D"));
            this.mangleDecl(d);
        }

        // Erasure: visit<FuncDeclaration>
        public  void visit(FuncDeclaration fd) {
            if (fd.isUnique())
            {
                this.mangleExact(fd);
            }
            else
            {
                this.visit((Dsymbol)fd);
            }
        }

        // Erasure: visit<FuncAliasDeclaration>
        public  void visit(FuncAliasDeclaration fd) {
            FuncDeclaration f = fd.toAliasFunc();
            FuncAliasDeclaration fa = f.isFuncAliasDeclaration();
            if (!fd.hasOverloads && (fa == null))
            {
                this.mangleExact(f);
                return ;
            }
            if (fa != null)
            {
                this.mangleSymbol(fa);
                return ;
            }
            this.visit((Dsymbol)fd);
        }

        // Erasure: visit<OverDeclaration>
        public  void visit(OverDeclaration od) {
            if (od.overnext != null)
            {
                this.visit((Dsymbol)od);
                return ;
            }
            {
                FuncDeclaration fd = od.aliassym.isFuncDeclaration();
                if ((fd) != null)
                {
                    if (!od.hasOverloads || fd.isUnique())
                    {
                        this.mangleExact(fd);
                        return ;
                    }
                }
            }
            {
                TemplateDeclaration td = od.aliassym.isTemplateDeclaration();
                if ((td) != null)
                {
                    if (!od.hasOverloads || (td.overnext.value == null))
                    {
                        this.mangleSymbol(td);
                        return ;
                    }
                }
            }
            this.visit((Dsymbol)od);
        }

        // Erasure: mangleExact<FuncDeclaration>
        public  void mangleExact(FuncDeclaration fd) {
            assert(fd.isFuncAliasDeclaration() == null);
            if (fd.mangleOverride.getLength() != 0)
            {
                (this.buf.get()).writestring(fd.mangleOverride);
                return ;
            }
            if (fd.isMain())
            {
                (this.buf.get()).writestring(new ByteSlice("_Dmain"));
                return ;
            }
            if (fd.isWinMain() || fd.isDllMain() || (pequals(fd.ident, Id.tls_get_addr)))
            {
                (this.buf.get()).writestring(fd.ident.asString());
                return ;
            }
            this.visit((Declaration)fd);
        }

        // Erasure: visit<VarDeclaration>
        public  void visit(VarDeclaration vd) {
            if (vd.mangleOverride.getLength() != 0)
            {
                (this.buf.get()).writestring(vd.mangleOverride);
                return ;
            }
            this.visit((Declaration)vd);
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(AggregateDeclaration ad) {
            ClassDeclaration cd = ad.isClassDeclaration();
            Dsymbol parentsave = ad.parent.value;
            if (cd != null)
            {
                if ((pequals(cd.ident, Id.Exception)) && (pequals(cd.parent.value.ident, Id.object)) || (pequals(cd.ident, Id.TypeInfo)) || (pequals(cd.ident, Id.TypeInfo_Struct)) || (pequals(cd.ident, Id.TypeInfo_Class)) || (pequals(cd.ident, Id.TypeInfo_Tuple)) || (pequals(cd, ClassDeclaration.object)) || (pequals(cd, Type.typeinfoclass)) || (pequals(cd, dmodule.Module.moduleinfo)) || (strncmp(cd.ident.toChars(), new BytePtr("TypeInfo_"), 9) == 0))
                {
                    ad.parent.value = null;
                }
            }
            this.visit((Dsymbol)ad);
            ad.parent.value = parentsave;
        }

        // Erasure: visit<TemplateInstance>
        public  void visit(TemplateInstance ti) {
            if (ti.tempdecl == null)
            {
                ti.error(new BytePtr("is not defined"));
            }
            else
            {
                this.mangleParent(ti);
            }
            if ((ti.isTemplateMixin() != null) && (ti.ident != null))
            {
                this.mangleIdentifier(ti.ident, ti);
            }
            else
            {
                this.mangleTemplateInstance(ti);
            }
        }

        // Erasure: mangleTemplateInstance<TemplateInstance>
        public  void mangleTemplateInstance(TemplateInstance ti) {
            TemplateDeclaration tempdecl = ti.tempdecl.isTemplateDeclaration();
            assert(tempdecl != null);
            byte T = ti.members != null ? (byte)84 : (byte)85;
            (this.buf.get()).printf(new BytePtr("__%c"), (T & 0xFF));
            this.mangleIdentifier(tempdecl.ident, tempdecl);
            Ptr<DArray<RootObject>> args = ti.tiargs;
            int nparams = (tempdecl.parameters.get()).length - (tempdecl.isVariadic() != null ? 1 : 0);
            {
                int i = 0;
            L_outer1:
                for (; (i < (args.get()).length);i++){
                    RootObject o = (args.get()).get(i);
                    Type ta = isType(o);
                    Expression ea = isExpression(o);
                    Dsymbol sa = isDsymbol(o);
                    Tuple va = isTuple(o);
                    if ((i < nparams) && ((tempdecl.parameters.get()).get(i).specialization() != null))
                    {
                        (this.buf.get()).writeByte(72);
                    }
                    if (ta != null)
                    {
                        (this.buf.get()).writeByte(84);
                        this.visitWithMask(ta, (byte)0);
                    }
                    else if (ea != null)
                    {
                        boolean keepLvalue = true;
                        ea = ea.optimize(0, true);
                        {
                            VarExp ev = ea.isVarExp();
                            if ((ev) != null)
                            {
                                sa = ev.var;
                                ea = null;
                                /*goto Lsa*//*unrolled goto*/
                            /*Lsa:*/
                                sa = sa.toAlias();
                                {
                                    Declaration d = sa.isDeclaration();
                                    if ((d) != null)
                                    {
                                        {
                                            FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                            if ((fad) != null)
                                            {
                                                d = fad.toAliasFunc();
                                            }
                                        }
                                        if (d.mangleOverride.getLength() != 0)
                                        {
                                            (this.buf.get()).writeByte(88);
                                            this.toBuffer(d.mangleOverride, d);
                                            continue L_outer1;
                                        }
                                        {
                                            ByteSlice id = externallyMangledIdentifier(d).copy();
                                            if ((id).getLength() != 0)
                                            {
                                                (this.buf.get()).writeByte(88);
                                                this.toBuffer(id, d);
                                                continue L_outer1;
                                            }
                                        }
                                        if ((d.type == null) || (d.type.deco == null))
                                        {
                                            ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                            continue L_outer1;
                                        }
                                    }
                                }
                                (this.buf.get()).writeByte(83);
                                this.mangleSymbol(sa);
                            }
                        }
                        {
                            ThisExp et = ea.isThisExp();
                            if ((et) != null)
                            {
                                sa = et.var;
                                ea = null;
                                /*goto Lsa*//*unrolled goto*/
                            /*Lsa:*/
                                sa = sa.toAlias();
                                {
                                    Declaration d = sa.isDeclaration();
                                    if ((d) != null)
                                    {
                                        {
                                            FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                            if ((fad) != null)
                                            {
                                                d = fad.toAliasFunc();
                                            }
                                        }
                                        if (d.mangleOverride.getLength() != 0)
                                        {
                                            (this.buf.get()).writeByte(88);
                                            this.toBuffer(d.mangleOverride, d);
                                            continue L_outer1;
                                        }
                                        {
                                            ByteSlice id = externallyMangledIdentifier(d).copy();
                                            if ((id).getLength() != 0)
                                            {
                                                (this.buf.get()).writeByte(88);
                                                this.toBuffer(id, d);
                                                continue L_outer1;
                                            }
                                        }
                                        if ((d.type == null) || (d.type.deco == null))
                                        {
                                            ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                            continue L_outer1;
                                        }
                                    }
                                }
                                (this.buf.get()).writeByte(83);
                                this.mangleSymbol(sa);
                            }
                        }
                        {
                            FuncExp ef = ea.isFuncExp();
                            if ((ef) != null)
                            {
                                if (ef.td != null)
                                {
                                    sa = ef.td;
                                }
                                else
                                {
                                    sa = ef.fd;
                                }
                                ea = null;
                                /*goto Lsa*//*unrolled goto*/
                            /*Lsa:*/
                                sa = sa.toAlias();
                                {
                                    Declaration d = sa.isDeclaration();
                                    if ((d) != null)
                                    {
                                        {
                                            FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                            if ((fad) != null)
                                            {
                                                d = fad.toAliasFunc();
                                            }
                                        }
                                        if (d.mangleOverride.getLength() != 0)
                                        {
                                            (this.buf.get()).writeByte(88);
                                            this.toBuffer(d.mangleOverride, d);
                                            continue L_outer1;
                                        }
                                        {
                                            ByteSlice id = externallyMangledIdentifier(d).copy();
                                            if ((id).getLength() != 0)
                                            {
                                                (this.buf.get()).writeByte(88);
                                                this.toBuffer(id, d);
                                                continue L_outer1;
                                            }
                                        }
                                        if ((d.type == null) || (d.type.deco == null))
                                        {
                                            ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                            continue L_outer1;
                                        }
                                    }
                                }
                                (this.buf.get()).writeByte(83);
                                this.mangleSymbol(sa);
                            }
                        }
                        (this.buf.get()).writeByte(86);
                        if (((ea.op & 0xFF) == 126))
                        {
                            ea.error(new BytePtr("tuple is not a valid template value argument"));
                            continue L_outer1;
                        }
                        int olderr = global.errors;
                        ea = ea.ctfeInterpret();
                        if (((ea.op & 0xFF) == 127) || (olderr != global.errors))
                        {
                            continue L_outer1;
                        }
                        this.visitWithMask(ea.type.value, (byte)0);
                        ea.accept(this);
                    }
                    else if (sa != null)
                    {
                    /*Lsa:*/
                        sa = sa.toAlias();
                        {
                            Declaration d = sa.isDeclaration();
                            if ((d) != null)
                            {
                                {
                                    FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                    if ((fad) != null)
                                    {
                                        d = fad.toAliasFunc();
                                    }
                                }
                                if (d.mangleOverride.getLength() != 0)
                                {
                                    (this.buf.get()).writeByte(88);
                                    this.toBuffer(d.mangleOverride, d);
                                    continue L_outer1;
                                }
                                {
                                    ByteSlice id = externallyMangledIdentifier(d).copy();
                                    if ((id).getLength() != 0)
                                    {
                                        (this.buf.get()).writeByte(88);
                                        this.toBuffer(id, d);
                                        continue L_outer1;
                                    }
                                }
                                if ((d.type == null) || (d.type.deco == null))
                                {
                                    ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                    continue L_outer1;
                                }
                            }
                        }
                        (this.buf.get()).writeByte(83);
                        this.mangleSymbol(sa);
                    }
                    else if (va != null)
                    {
                        assert((i + 1 == (args.get()).length));
                        args = pcopy((ptr(va.objects)));
                        i = -1;
                    }
                    else
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
            (this.buf.get()).writeByte(90);
        }

        // Erasure: visit<Dsymbol>
        public  void visit(Dsymbol s) {
            this.mangleParent(s);
            if (s.ident != null)
            {
                this.mangleIdentifier(s.ident, s);
            }
            else
            {
                this.toBuffer(s.asString(), s);
            }
        }

        // Erasure: visit<Expression>
        public  void visit(Expression e) {
            e.error(new BytePtr("expression `%s` is not a valid template value argument"), e.toChars());
        }

        // Erasure: visit<IntegerExp>
        public  void visit(IntegerExp e) {
            long v = e.toInteger();
            if (((long)v < 0L))
            {
                (this.buf.get()).writeByte(78);
                (this.buf.get()).print(-v);
            }
            else
            {
                (this.buf.get()).writeByte(105);
                (this.buf.get()).print(v);
            }
        }

        // Erasure: visit<RealExp>
        public  void visit(RealExp e) {
            (this.buf.get()).writeByte(101);
            this.realToMangleBuffer(e.value);
        }

        // Erasure: realToMangleBuffer<double>
        public  void realToMangleBuffer(double value) {
            if (CTFloat.isNaN(value))
            {
                (this.buf.get()).writestring(new ByteSlice("NAN"));
                return ;
            }
            if ((value < CTFloat.zero))
            {
                (this.buf.get()).writeByte(78);
                value = -value;
            }
            if (CTFloat.isInfinity(value))
            {
                (this.buf.get()).writestring(new ByteSlice("INF"));
                return ;
            }
            ByteSlice buffer = new RawByteSlice(new byte[36]);
            int n = CTFloat.sprint(buffer.ptr(), (byte)65, value);
            assert((n < 36));
            {
                ByteSlice __r1022 = buffer.slice(2,n).copy();
                int __key1023 = 0;
                for (; (__key1023 < __r1022.getLength());__key1023 += 1) {
                    byte c = __r1022.get(__key1023);
                    switch ((c & 0xFF))
                    {
                        case 45:
                            (this.buf.get()).writeByte(78);
                            break;
                        case 43:
                        case 46:
                            break;
                        default:
                        (this.buf.get()).writeByte((c & 0xFF));
                        break;
                    }
                }
            }
        }

        // Erasure: visit<ComplexExp>
        public  void visit(ComplexExp e) {
            (this.buf.get()).writeByte(99);
            this.realToMangleBuffer(e.toReal());
            (this.buf.get()).writeByte(99);
            this.realToMangleBuffer(e.toImaginary());
        }

        // Erasure: visit<NullExp>
        public  void visit(NullExp e) {
            (this.buf.get()).writeByte(110);
        }

        // Erasure: visit<StringExp>
        public  void visit(StringExp e) {
            byte m = (byte)255;
            OutBuffer tmp = new OutBuffer();
            try {
                ByteSlice q = new RawByteSlice().copy();
                switch ((e.sz & 0xFF))
                {
                    case 1:
                        m = (byte)97;
                        q = e.string.slice(0,e.len).copy();
                        break;
                    case 2:
                        m = (byte)119;
                        {
                            Ref<Integer> u = ref(0);
                            for (; (u.value < e.len);){
                                Ref<Integer> c = ref(0x0ffff);
                                BytePtr p = pcopy(utf_decodeWchar(e.wstring, e.len, u, c));
                                if (p != null)
                                {
                                    e.error(new BytePtr("%s"), p);
                                }
                                else
                                {
                                    tmp.writeUTF8(c.value);
                                }
                            }
                        }
                        q = tmp.peekSlice().copy();
                        break;
                    case 4:
                        m = (byte)100;
                        {
                            int __key1024 = 0;
                            int __limit1025 = e.len;
                            for (; (__key1024 < __limit1025);__key1024 += 1) {
                                int u_1 = __key1024;
                                int c_1 = (toPtr<Integer>(e.string)).get(u_1);
                                if (!utf_isValidDchar(c_1))
                                {
                                    e.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_1);
                                }
                                else
                                {
                                    tmp.writeUTF8(c_1);
                                }
                            }
                        }
                        q = tmp.peekSlice().copy();
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                (this.buf.get()).reserve(12 + 2 * q.getLength());
                (this.buf.get()).writeByte((m & 0xFF));
                (this.buf.get()).print((long)q.getLength());
                (this.buf.get()).writeByte(95);
                int qi = 0;
                {
                    BytePtr p = pcopy(toBytePtr(this.buf.get().data).plus((this.buf.get()).offset));
                    BytePtr pend = pcopy(p.plus((2 * q.getLength())));
                    for (; (p.lessThan(pend));comma(p.plusAssign(2), qi += 1)){
                        byte hi = (byte)((q.get(qi) & 0xFF) >> 4 & 15);
                        p.set(0, ((hi & 0xFF) < 10) ? (byte)((hi & 0xFF) + 48) : (byte)((hi & 0xFF) - 10 + 97));
                        byte lo = (byte)((q.get(qi) & 0xFF) & 15);
                        p.set(1, ((lo & 0xFF) < 10) ? (byte)((lo & 0xFF) + 48) : (byte)((lo & 0xFF) - 10 + 97));
                    }
                }
                (this.buf.get()).offset += 2 * q.getLength();
            }
            finally {
            }
        }

        // Erasure: visit<ArrayLiteralExp>
        public  void visit(ArrayLiteralExp e) {
            int dim = e.elements != null ? (e.elements.get()).length : 0;
            (this.buf.get()).writeByte(65);
            (this.buf.get()).print((long)dim);
            {
                int __key1026 = 0;
                int __limit1027 = dim;
                for (; (__key1026 < __limit1027);__key1026 += 1) {
                    int i = __key1026;
                    e.getElement(i).accept(this);
                }
            }
        }

        // Erasure: visit<AssocArrayLiteralExp>
        public  void visit(AssocArrayLiteralExp e) {
            int dim = (e.keys.get()).length;
            (this.buf.get()).writeByte(65);
            (this.buf.get()).print((long)dim);
            {
                int __key1028 = 0;
                int __limit1029 = dim;
                for (; (__key1028 < __limit1029);__key1028 += 1) {
                    int i = __key1028;
                    (e.keys.get()).get(i).accept(this);
                    (e.values.get()).get(i).accept(this);
                }
            }
        }

        // Erasure: visit<StructLiteralExp>
        public  void visit(StructLiteralExp e) {
            int dim = e.elements != null ? (e.elements.get()).length : 0;
            (this.buf.get()).writeByte(83);
            (this.buf.get()).print((long)dim);
            {
                int __key1030 = 0;
                int __limit1031 = dim;
                for (; (__key1030 < __limit1031);__key1030 += 1) {
                    int i = __key1030;
                    Expression ex = (e.elements.get()).get(i);
                    if (ex != null)
                    {
                        ex.accept(this);
                    }
                    else
                    {
                        (this.buf.get()).writeByte(118);
                    }
                }
            }
        }

        // Erasure: paramsToDecoBuffer<Ptr>
        public  void paramsToDecoBuffer(Ptr<DArray<Parameter>> parameters) {
            Function2<Integer,Parameter,Integer> paramsToDecoBufferDg = new Function2<Integer,Parameter,Integer>() {
                public Integer invoke(Integer n, Parameter p) {
                 {
                    p.accept(this);
                    return 0;
                }}

            };
            Parameter._foreach(parameters, paramsToDecoBufferDg, null);
        }

        // Erasure: visit<Parameter>
        public  void visit(Parameter p) {
            if (((p.storageClass & 524288L) != 0) && ((p.storageClass & 562949953421312L) == 0))
            {
                (this.buf.get()).writeByte(77);
            }
            if (((p.storageClass & 17594333528064L) == 17592186044416L) && ((p.storageClass & 4503599627370496L) == 0))
            {
                (this.buf.get()).writestring(new ByteSlice("Nk"));
            }
            switch ((int)p.storageClass & 2111488L)
            {
                case (int)0L:
                case (int)2048L:
                    break;
                case (int)4096L:
                    (this.buf.get()).writeByte(74);
                    break;
                case (int)2097152L:
                    (this.buf.get()).writeByte(75);
                    break;
                case (int)8192L:
                    (this.buf.get()).writeByte(76);
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            this.visitWithMask(p.type, (byte)0);
        }


        public Mangler() {}

        public Mangler copy() {
            Mangler that = new Mangler();
            that.types = this.types;
            that.idents = this.idents;
            that.buf = this.buf;
            return that;
        }
    }
    // Erasure: isValidMangling<int>
    public static boolean isValidMangling(int c) {
        return (c >= 65) && (c <= 90) || (c >= 97) && (c <= 122) || (c >= 48) && (c <= 57) || (c != 0) && (strchr(new BytePtr("$%().:?@[]_"), c) != null);
    }

    // Erasure: mangleExact<FuncDeclaration>
    public static BytePtr mangleExact(FuncDeclaration fd) {
        if (fd.mangleString == null)
        {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                Mangler v = new Mangler(ptr(buf));
                v.mangleExact(fd);
                fd.mangleString = pcopy(buf.value.extractChars());
            }
            finally {
            }
        }
        return fd.mangleString;
    }

    // Erasure: mangleToBuffer<Type, Ptr>
    public static void mangleToBuffer(Type t, Ptr<OutBuffer> buf) {
        if (t.deco != null)
        {
            (buf.get()).writestring(t.deco);
        }
        else
        {
            Mangler v = new Mangler(buf);
            v.visitWithMask(t, (byte)0);
        }
    }

    // Erasure: mangleToBuffer<Expression, Ptr>
    public static void mangleToBuffer(Expression e, Ptr<OutBuffer> buf) {
        Mangler v = new Mangler(buf);
        e.accept(v);
    }

    // Erasure: mangleToBuffer<Dsymbol, Ptr>
    public static void mangleToBuffer(Dsymbol s, Ptr<OutBuffer> buf) {
        Mangler v = new Mangler(buf);
        s.accept(v);
    }

    // Erasure: mangleToBuffer<TemplateInstance, Ptr>
    public static void mangleToBuffer(TemplateInstance ti, Ptr<OutBuffer> buf) {
        Mangler v = new Mangler(buf);
        v.mangleTemplateInstance(ti);
    }

    // Erasure: mangleToFuncSignature<OutBuffer, FuncDeclaration>
    public static void mangleToFuncSignature(OutBuffer buf, FuncDeclaration fd) {
        Ref<OutBuffer> buf_ref = ref(buf);
        TypeFunction tf = fd.type.isTypeFunction();
        Mangler v = new Mangler(ptr(buf_ref));
        MODtoDecoBuffer(ptr(buf_ref), tf.mod);
        v.paramsToDecoBuffer(tf.parameterList.parameters);
        buf_ref.value.writeByte((90 - tf.parameterList.varargs));
    }

}
