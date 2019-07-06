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
    public static void tyToDecoBuffer(OutBuffer buf, int ty) {
        byte c = mangleChar.get(ty);
        (buf).writeByte((c & 0xFF));
        if ((c & 0xFF) == 122)
            (buf).writeByte(ty == ENUMTY.Tint128 ? 105 : 107);
    }

    public static void MODtoDecoBuffer(OutBuffer buf, byte mod) {
        switch ((mod & 0xFF))
        {
            case 0:
                break;
            case 1:
                (buf).writeByte(120);
                break;
            case 4:
                (buf).writeByte(121);
                break;
            case 2:
                (buf).writeByte(79);
                break;
            case 3:
                (buf).writestring(new ByteSlice("Ox"));
                break;
            case 8:
                (buf).writestring(new ByteSlice("Ng"));
                break;
            case 9:
                (buf).writestring(new ByteSlice("Ngx"));
                break;
            case 10:
                (buf).writestring(new ByteSlice("ONg"));
                break;
            case 11:
                (buf).writestring(new ByteSlice("ONgx"));
                break;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }

    public static class Mangler extends Visitor
    {
        public AssocArrayTypeInteger types = new AssocArrayTypeInteger();
        public AssocArrayIdentifierInteger idents = new AssocArrayIdentifierInteger();
        public OutBuffer buf;
        public  Mangler(OutBuffer buf) {
            this.buf = buf;
        }

        public  void writeBackRef(int pos) {
            (this.buf).writeByte(81);
            int base = 26;
            int mul = 1;
            for (; pos >= mul * 26;) {
                mul *= 26;
            }
            for (; mul >= 26;){
                byte dig = (byte)(pos / mul);
                (this.buf).writeByte((65 + (dig & 0xFF)));
                pos -= (dig & 0xFF) * mul;
                mul /= 26;
            }
            (this.buf).writeByte((97 + ((byte)pos & 0xFF)));
        }

        public  boolean backrefType(Type t) {
            if (!(t.isTypeBasic() != null))
            {
                IntPtr p = pcopy(this.types.getLvalue(t));
                if ((p.get()) != 0)
                {
                    this.writeBackRef((this.buf).offset - p.get());
                    return true;
                }
                p.set(0, (this.buf).offset);
            }
            return false;
        }

        public  boolean backrefIdentifier(Identifier id) {
            IntPtr p = pcopy(this.idents.getLvalue(id));
            if ((p.get()) != 0)
            {
                this.writeBackRef((this.buf).offset - p.get());
                return true;
            }
            p.set(0, (this.buf).offset);
            return false;
        }

        public  void mangleSymbol(Dsymbol s) {
            s.accept(this);
        }

        public  void mangleType(Type t) {
            if (!(this.backrefType(t)))
                t.accept(this);
        }

        public  void mangleIdentifier(Identifier id, Dsymbol s) {
            if (!(this.backrefIdentifier(id)))
                this.toBuffer(id.asString(), s);
        }

        public  void visitWithMask(Type t, byte modMask) {
            if ((modMask & 0xFF) != (t.mod & 0xFF))
            {
                MODtoDecoBuffer(this.buf, t.mod);
            }
            this.mangleType(t);
        }

        public  void visit(Type t) {
            tyToDecoBuffer(this.buf, (t.ty & 0xFF));
        }

        public  void visit(TypeNext t) {
            this.visit((Type)t);
            this.visitWithMask(t.next, t.mod);
        }

        public  void visit(TypeVector t) {
            (this.buf).writestring(new ByteSlice("Nh"));
            this.visitWithMask(t.basetype, t.mod);
        }

        public  void visit(TypeSArray t) {
            this.visit((Type)t);
            if (t.dim != null)
                (this.buf).print(t.dim.toInteger());
            if (t.next != null)
                this.visitWithMask(t.next, t.mod);
        }

        public  void visit(TypeDArray t) {
            this.visit((Type)t);
            if (t.next != null)
                this.visitWithMask(t.next, t.mod);
        }

        public  void visit(TypeAArray t) {
            this.visit((Type)t);
            this.visitWithMask(t.index, (byte)0);
            this.visitWithMask(t.next, t.mod);
        }

        public  void visit(TypeFunction t) {
            this.mangleFuncType(t, t, t.mod, t.next);
        }

        public  void mangleFuncType(TypeFunction t, TypeFunction ta, byte modMask, Type tret) {
            if (((t.inuse) != 0 && tret != null))
            {
                t.inuse = 2;
                return ;
            }
            t.inuse++;
            if ((modMask & 0xFF) != (t.mod & 0xFF))
                MODtoDecoBuffer(this.buf, t.mod);
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
            (this.buf).writeByte((mc & 0xFF));
            if ((ta.purity) != 0)
                (this.buf).writestring(new ByteSlice("Na"));
            if (ta.isnothrow)
                (this.buf).writestring(new ByteSlice("Nb"));
            if (ta.isref)
                (this.buf).writestring(new ByteSlice("Nc"));
            if (ta.isproperty)
                (this.buf).writestring(new ByteSlice("Nd"));
            if (ta.isnogc)
                (this.buf).writestring(new ByteSlice("Ni"));
            if ((ta.isreturn && !(ta.isreturninferred)))
                (this.buf).writestring(new ByteSlice("Nj"));
            else if ((ta.isscope && !(ta.isscopeinferred)))
                (this.buf).writestring(new ByteSlice("Nl"));
            switch (ta.trust)
            {
                case TRUST.trusted:
                    (this.buf).writestring(new ByteSlice("Ne"));
                    break;
                case TRUST.safe:
                    (this.buf).writestring(new ByteSlice("Nf"));
                    break;
                default:
                break;
            }
            this.paramsToDecoBuffer(t.parameterList.parameters);
            (this.buf).writeByte((90 - t.parameterList.varargs));
            if (tret != null)
                this.visitWithMask(tret, (byte)0);
            t.inuse--;
        }

        public  void visit(TypeIdentifier t) {
            this.visit((Type)t);
            ByteSlice name = t.ident.asString().copy();
            (this.buf).print((long)name.getLength());
            (this.buf).writestring(name);
        }

        public  void visit(TypeEnum t) {
            this.visit((Type)t);
            this.mangleSymbol(t.sym);
        }

        public  void visit(TypeStruct t) {
            this.visit((Type)t);
            this.mangleSymbol(t.sym);
        }

        public  void visit(TypeClass t) {
            this.visit((Type)t);
            this.mangleSymbol(t.sym);
        }

        public  void visit(TypeTuple t) {
            this.visit((Type)t);
            this.paramsToDecoBuffer(t.arguments);
            (this.buf).writeByte(90);
        }

        public  void visit(TypeNull t) {
            this.visit((Type)t);
        }

        public  void mangleDecl(Declaration sthis) {
            this.mangleParent(sthis);
            assert(sthis.ident != null);
            this.mangleIdentifier(sthis.ident, sthis);
            {
                FuncDeclaration fd = sthis.isFuncDeclaration();
                if (fd != null)
                {
                    this.mangleFunc(fd, false);
                }
                else if (sthis.type != null)
                {
                    this.visitWithMask(sthis.type, (byte)0);
                }
                else
                    throw new AssertionError("Unreachable code!");
            }
        }

        public  void mangleParent(Dsymbol s) {
            Dsymbol p = null;
            {
                TemplateInstance ti = s.isTemplateInstance();
                if (ti != null)
                    p = ti.isTemplateMixin() != null ? ti.parent : ti.tempdecl.parent;
                else
                    p = s.parent;
            }
            if (p != null)
            {
                this.mangleParent(p);
                TemplateInstance ti = p.isTemplateInstance();
                if ((ti != null && !(ti.isTemplateMixin() != null)))
                {
                    this.mangleTemplateInstance(ti);
                }
                else if (p.getIdent() != null)
                {
                    this.mangleIdentifier(p.ident, s);
                    {
                        FuncDeclaration f = p.isFuncDeclaration();
                        if (f != null)
                            this.mangleFunc(f, true);
                    }
                }
                else
                    (this.buf).writeByte(48);
            }
        }

        public  void mangleFunc(FuncDeclaration fd, boolean inParent) {
            if ((fd.needThis() || fd.isNested()))
                (this.buf).writeByte(77);
            if ((!(fd.type != null) || (fd.type.ty & 0xFF) == ENUMTY.Terror))
            {
                (this.buf).writestring(new ByteSlice("9__error__FZ"));
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

        public  void toBuffer(ByteSlice id, Dsymbol s) {
            int len = id.getLength();
            if ((this.buf).offset + len >= 8388608)
                s.error(new BytePtr("excessive length %llu for symbol, possible recursive expansion?"), (long)((this.buf).offset + len));
            else
            {
                (this.buf).print((long)len);
                (this.buf).writestring(id);
            }
        }

        public static ByteSlice externallyMangledIdentifier(Declaration d) {
            if (((!(d.parent != null) || d.parent.isModule() != null) || d.linkage == LINK.cpp))
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

        public  void visit(Declaration d) {
            {
                ByteSlice id = externallyMangledIdentifier(d).copy();
                if (id.getLength() != 0)
                {
                    (this.buf).writestring(id);
                    return ;
                }
            }
            (this.buf).writestring(new ByteSlice("_D"));
            this.mangleDecl(d);
        }

        public  void visit(FuncDeclaration fd) {
            if (fd.isUnique())
                this.mangleExact(fd);
            else
                this.visit((Dsymbol)fd);
        }

        public  void visit(FuncAliasDeclaration fd) {
            FuncDeclaration f = fd.toAliasFunc();
            FuncAliasDeclaration fa = f.isFuncAliasDeclaration();
            if ((!(fd.hasOverloads) && !(fa != null)))
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

        public  void visit(OverDeclaration od) {
            if (od.overnext != null)
            {
                this.visit((Dsymbol)od);
                return ;
            }
            {
                FuncDeclaration fd = od.aliassym.isFuncDeclaration();
                if (fd != null)
                {
                    if ((!(od.hasOverloads) || fd.isUnique()))
                    {
                        this.mangleExact(fd);
                        return ;
                    }
                }
            }
            {
                TemplateDeclaration td = od.aliassym.isTemplateDeclaration();
                if (td != null)
                {
                    if ((!(od.hasOverloads) || td.overnext == null))
                    {
                        this.mangleSymbol(td);
                        return ;
                    }
                }
            }
            this.visit((Dsymbol)od);
        }

        public  void mangleExact(FuncDeclaration fd) {
            assert(!(fd.isFuncAliasDeclaration() != null));
            if (fd.mangleOverride.getLength() != 0)
            {
                (this.buf).writestring(fd.mangleOverride);
                return ;
            }
            if (fd.isMain())
            {
                (this.buf).writestring(new ByteSlice("_Dmain"));
                return ;
            }
            if (((fd.isWinMain() || fd.isDllMain()) || pequals(fd.ident, Id.tls_get_addr)))
            {
                (this.buf).writestring(fd.ident.asString());
                return ;
            }
            this.visit((Declaration)fd);
        }

        public  void visit(VarDeclaration vd) {
            if (vd.mangleOverride.getLength() != 0)
            {
                (this.buf).writestring(vd.mangleOverride);
                return ;
            }
            this.visit((Declaration)vd);
        }

        public  void visit(AggregateDeclaration ad) {
            ClassDeclaration cd = ad.isClassDeclaration();
            Dsymbol parentsave = ad.parent;
            if (cd != null)
            {
                if ((((((((((pequals(cd.ident, Id.Exception) && pequals(cd.parent.ident, Id.object)) || pequals(cd.ident, Id.TypeInfo)) || pequals(cd.ident, Id.TypeInfo_Struct)) || pequals(cd.ident, Id.TypeInfo_Class)) || pequals(cd.ident, Id.TypeInfo_Tuple)) || pequals(cd, ClassDeclaration.object)) || pequals(cd, Type.typeinfoclass)) || pequals(cd, dmodule.Module.moduleinfo)) || strncmp(cd.ident.toChars(), new BytePtr("TypeInfo_"), 9) == 0))
                {
                    ad.parent = null;
                }
            }
            this.visit((Dsymbol)ad);
            ad.parent = parentsave;
        }

        public  void visit(TemplateInstance ti) {
            if (!(ti.tempdecl != null))
                ti.error(new BytePtr("is not defined"));
            else
                this.mangleParent(ti);
            if ((ti.isTemplateMixin() != null && ti.ident != null))
                this.mangleIdentifier(ti.ident, ti);
            else
                this.mangleTemplateInstance(ti);
        }

        public  void mangleTemplateInstance(TemplateInstance ti) {
            TemplateDeclaration tempdecl = ti.tempdecl.isTemplateDeclaration();
            assert(tempdecl != null);
            byte T = ti.members != null ? (byte)84 : (byte)85;
            (this.buf).printf(new BytePtr("__%c"), (T & 0xFF));
            this.mangleIdentifier(tempdecl.ident, tempdecl);
            DArray<RootObject> args = ti.tiargs;
            int nparams = (tempdecl.parameters).length - (tempdecl.isVariadic() != null ? 1 : 0);
            {
                int i = 0;
            L_outer1:
                for (; i < (args).length;i++){
                    RootObject o = (args).get(i);
                    Type ta = isType(o);
                    Expression ea = isExpression(o);
                    Dsymbol sa = isDsymbol(o);
                    Tuple va = isTuple(o);
                    if ((i < nparams && (tempdecl.parameters).get(i).specialization() != null))
                        (this.buf).writeByte(72);
                    if (ta != null)
                    {
                        (this.buf).writeByte(84);
                        this.visitWithMask(ta, (byte)0);
                    }
                    else if (ea != null)
                    {
                        boolean keepLvalue = true;
                        ea = ea.optimize(0, true);
                        {
                            VarExp ev = ea.isVarExp();
                            if (ev != null)
                            {
                                sa = ev.var;
                                ea = null;
                                /*goto Lsa*//*unrolled goto*/
                            /*Lsa:*/
                                sa = sa.toAlias();
                                {
                                    Declaration d = sa.isDeclaration();
                                    if (d != null)
                                    {
                                        {
                                            FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                            if (fad != null)
                                                d = fad.toAliasFunc();
                                        }
                                        if (d.mangleOverride.getLength() != 0)
                                        {
                                            (this.buf).writeByte(88);
                                            this.toBuffer(d.mangleOverride, d);
                                            continue L_outer1;
                                        }
                                        {
                                            ByteSlice id = externallyMangledIdentifier(d).copy();
                                            if (id.getLength() != 0)
                                            {
                                                (this.buf).writeByte(88);
                                                this.toBuffer(id, d);
                                                continue L_outer1;
                                            }
                                        }
                                        if ((!(d.type != null) || d.type.deco == null))
                                        {
                                            ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                            continue L_outer1;
                                        }
                                    }
                                }
                                (this.buf).writeByte(83);
                                this.mangleSymbol(sa);
                            }
                        }
                        {
                            ThisExp et = ea.isThisExp();
                            if (et != null)
                            {
                                sa = et.var;
                                ea = null;
                                /*goto Lsa*//*unrolled goto*/
                            /*Lsa:*/
                                sa = sa.toAlias();
                                {
                                    Declaration d = sa.isDeclaration();
                                    if (d != null)
                                    {
                                        {
                                            FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                            if (fad != null)
                                                d = fad.toAliasFunc();
                                        }
                                        if (d.mangleOverride.getLength() != 0)
                                        {
                                            (this.buf).writeByte(88);
                                            this.toBuffer(d.mangleOverride, d);
                                            continue L_outer1;
                                        }
                                        {
                                            ByteSlice id = externallyMangledIdentifier(d).copy();
                                            if (id.getLength() != 0)
                                            {
                                                (this.buf).writeByte(88);
                                                this.toBuffer(id, d);
                                                continue L_outer1;
                                            }
                                        }
                                        if ((!(d.type != null) || d.type.deco == null))
                                        {
                                            ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                            continue L_outer1;
                                        }
                                    }
                                }
                                (this.buf).writeByte(83);
                                this.mangleSymbol(sa);
                            }
                        }
                        {
                            FuncExp ef = ea.isFuncExp();
                            if (ef != null)
                            {
                                if (ef.td != null)
                                    sa = ef.td;
                                else
                                    sa = ef.fd;
                                ea = null;
                                /*goto Lsa*//*unrolled goto*/
                            /*Lsa:*/
                                sa = sa.toAlias();
                                {
                                    Declaration d = sa.isDeclaration();
                                    if (d != null)
                                    {
                                        {
                                            FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                            if (fad != null)
                                                d = fad.toAliasFunc();
                                        }
                                        if (d.mangleOverride.getLength() != 0)
                                        {
                                            (this.buf).writeByte(88);
                                            this.toBuffer(d.mangleOverride, d);
                                            continue L_outer1;
                                        }
                                        {
                                            ByteSlice id = externallyMangledIdentifier(d).copy();
                                            if (id.getLength() != 0)
                                            {
                                                (this.buf).writeByte(88);
                                                this.toBuffer(id, d);
                                                continue L_outer1;
                                            }
                                        }
                                        if ((!(d.type != null) || d.type.deco == null))
                                        {
                                            ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                            continue L_outer1;
                                        }
                                    }
                                }
                                (this.buf).writeByte(83);
                                this.mangleSymbol(sa);
                            }
                        }
                        (this.buf).writeByte(86);
                        if ((ea.op & 0xFF) == 126)
                        {
                            ea.error(new BytePtr("tuple is not a valid template value argument"));
                            continue L_outer1;
                        }
                        int olderr = global.errors;
                        ea = ea.ctfeInterpret();
                        if (((ea.op & 0xFF) == 127 || olderr != global.errors))
                            continue L_outer1;
                        this.visitWithMask(ea.type, (byte)0);
                        ea.accept(this);
                    }
                    else if (sa != null)
                    {
                    /*Lsa:*/
                        sa = sa.toAlias();
                        {
                            Declaration d = sa.isDeclaration();
                            if (d != null)
                            {
                                {
                                    FuncAliasDeclaration fad = d.isFuncAliasDeclaration();
                                    if (fad != null)
                                        d = fad.toAliasFunc();
                                }
                                if (d.mangleOverride.getLength() != 0)
                                {
                                    (this.buf).writeByte(88);
                                    this.toBuffer(d.mangleOverride, d);
                                    continue L_outer1;
                                }
                                {
                                    ByteSlice id = externallyMangledIdentifier(d).copy();
                                    if (id.getLength() != 0)
                                    {
                                        (this.buf).writeByte(88);
                                        this.toBuffer(id, d);
                                        continue L_outer1;
                                    }
                                }
                                if ((!(d.type != null) || d.type.deco == null))
                                {
                                    ti.error(new BytePtr("forward reference of %s `%s`"), d.kind(), d.toChars());
                                    continue L_outer1;
                                }
                            }
                        }
                        (this.buf).writeByte(83);
                        this.mangleSymbol(sa);
                    }
                    else if (va != null)
                    {
                        assert(i + 1 == (args).length);
                        args = va.objects;
                        i = -1;
                    }
                    else
                        throw new AssertionError("Unreachable code!");
                }
            }
            (this.buf).writeByte(90);
        }

        public  void visit(Dsymbol s) {
            this.mangleParent(s);
            if (s.ident != null)
                this.mangleIdentifier(s.ident, s);
            else
                this.toBuffer(s.asString(), s);
        }

        public  void visit(Expression e) {
            e.error(new BytePtr("expression `%s` is not a valid template value argument"), e.toChars());
        }

        public  void visit(IntegerExp e) {
            long v = e.toInteger();
            if ((long)v < 0L)
            {
                (this.buf).writeByte(78);
                (this.buf).print(-v);
            }
            else
            {
                (this.buf).writeByte(105);
                (this.buf).print(v);
            }
        }

        public  void visit(RealExp e) {
            (this.buf).writeByte(101);
            this.realToMangleBuffer(e.value);
        }

        public  void realToMangleBuffer(double value) {
            if (CTFloat.isNaN(value))
            {
                (this.buf).writestring(new ByteSlice("NAN"));
                return ;
            }
            if (value < CTFloat.zero)
            {
                (this.buf).writeByte(78);
                value = -value;
            }
            if (CTFloat.isInfinity(value))
            {
                (this.buf).writestring(new ByteSlice("INF"));
                return ;
            }
            ByteSlice buffer = new ByteSlice(new byte[36]);
            int n = CTFloat.sprint(ptr(buffer), (byte)65, value);
            assert(n < 36);
            {
                ByteSlice __r1008 = buffer.slice(2,n).copy();
                int __key1009 = 0;
                for (; __key1009 < __r1008.getLength();__key1009 += 1) {
                    byte c = __r1008.get(__key1009);
                    switch ((c & 0xFF))
                    {
                        case 45:
                            (this.buf).writeByte(78);
                            break;
                        case 43:
                        case 46:
                            break;
                        default:
                        (this.buf).writeByte((c & 0xFF));
                        break;
                    }
                }
            }
        }

        public  void visit(ComplexExp e) {
            (this.buf).writeByte(99);
            this.realToMangleBuffer(e.toReal());
            (this.buf).writeByte(99);
            this.realToMangleBuffer(e.toImaginary());
        }

        public  void visit(NullExp e) {
            (this.buf).writeByte(110);
        }

        public  void visit(StringExp e) {
            byte m = (byte)255;
            OutBuffer tmp = new OutBuffer();
            try {
                ByteSlice q = new ByteSlice();
                switch ((e.sz & 0xFF))
                {
                    case 1:
                        m = (byte)97;
                        q = e.string.slice(0,e.len).copy();
                        break;
                    case 2:
                        m = (byte)119;
                        {
                            IntRef u = ref(0);
                            for (; u.value < e.len;){
                                IntRef c = ref(0x0ffff);
                                BytePtr p = pcopy(utf_decodeWchar(e.wstring, e.len, u, c));
                                if (p != null)
                                    e.error(new BytePtr("%s"), p);
                                else
                                    tmp.writeUTF8(c.value);
                            }
                        }
                        q = tmp.peekSlice().copy();
                        break;
                    case 4:
                        m = (byte)100;
                        {
                            int __key1010 = 0;
                            int __limit1011 = e.len;
                            for (; __key1010 < __limit1011;__key1010 += 1) {
                                int u_1 = __key1010;
                                int c_1 = (toIntPtr(e.string)).get(u_1);
                                if (!(utf_isValidDchar(c_1)))
                                    e.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_1);
                                else
                                    tmp.writeUTF8(c_1);
                            }
                        }
                        q = tmp.peekSlice().copy();
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                (this.buf).reserve(12 + 2 * q.getLength());
                (this.buf).writeByte((m & 0xFF));
                (this.buf).print((long)q.getLength());
                (this.buf).writeByte(95);
                int qi = 0;
                {
                    BytePtr p = pcopy(toBytePtr((this.buf).data).plus((this.buf).offset));
                    BytePtr pend = pcopy(p.plus((2 * q.getLength())));
                    for (; p.lessThan(pend);comma(p.plusAssign(2), qi += 1)){
                        byte hi = (byte)((q.get(qi) & 0xFF) >> 4 & 15);
                        p.set(0, (hi & 0xFF) < 10 ? (byte)((hi & 0xFF) + 48) : (byte)((hi & 0xFF) - 10 + 97));
                        byte lo = (byte)((q.get(qi) & 0xFF) & 15);
                        p.set(1, (lo & 0xFF) < 10 ? (byte)((lo & 0xFF) + 48) : (byte)((lo & 0xFF) - 10 + 97));
                    }
                }
                (this.buf).offset += 2 * q.getLength();
            }
            finally {
            }
        }

        public  void visit(ArrayLiteralExp e) {
            int dim = e.elements != null ? (e.elements).length : 0;
            (this.buf).writeByte(65);
            (this.buf).print((long)dim);
            {
                int __key1012 = 0;
                int __limit1013 = dim;
                for (; __key1012 < __limit1013;__key1012 += 1) {
                    int i = __key1012;
                    e.getElement(i).accept(this);
                }
            }
        }

        public  void visit(AssocArrayLiteralExp e) {
            int dim = (e.keys).length;
            (this.buf).writeByte(65);
            (this.buf).print((long)dim);
            {
                int __key1014 = 0;
                int __limit1015 = dim;
                for (; __key1014 < __limit1015;__key1014 += 1) {
                    int i = __key1014;
                    (e.keys).get(i).accept(this);
                    (e.values).get(i).accept(this);
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            int dim = e.elements != null ? (e.elements).length : 0;
            (this.buf).writeByte(83);
            (this.buf).print((long)dim);
            {
                int __key1016 = 0;
                int __limit1017 = dim;
                for (; __key1016 < __limit1017;__key1016 += 1) {
                    int i = __key1016;
                    Expression ex = (e.elements).get(i);
                    if (ex != null)
                        ex.accept(this);
                    else
                        (this.buf).writeByte(118);
                }
            }
        }

        public  void paramsToDecoBuffer(DArray<Parameter> parameters) {
            Function2<Integer,Parameter,Integer> paramsToDecoBufferDg = new Function2<Integer,Parameter,Integer>(){
                public Integer invoke(Integer n, Parameter p){
                    p.accept(this);
                    return 0;
                }
            };
            Parameter._foreach(parameters, paramsToDecoBufferDg, null);
        }

        public  void visit(Parameter p) {
            if (((p.storageClass & 524288L) != 0 && !((p.storageClass & 562949953421312L) != 0)))
                (this.buf).writeByte(77);
            if (((p.storageClass & 17594333528064L) == 17592186044416L && !((p.storageClass & 4503599627370496L) != 0)))
                (this.buf).writestring(new ByteSlice("Nk"));
            switch (p.storageClass & 2111488L)
            {
                case 0L:
                case 2048L:
                    break;
                case 4096L:
                    (this.buf).writeByte(74);
                    break;
                case 2097152L:
                    (this.buf).writeByte(75);
                    break;
                case 8192L:
                    (this.buf).writeByte(76);
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
    public static boolean isValidMangling(int c) {
        return ((((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) || (c >= 48 && c <= 57)) || (c != 0 && strchr(new BytePtr("$%().:?@[]_"), c) != null));
    }

    public static BytePtr mangleExact(FuncDeclaration fd) {
        if (fd.mangleString == null)
        {
            OutBuffer buf = new OutBuffer();
            try {
                Mangler v = new Mangler(buf);
                v.mangleExact(fd);
                fd.mangleString = pcopy(buf.extractChars());
            }
            finally {
            }
        }
        return fd.mangleString;
    }

    public static void mangleToBuffer(Type t, OutBuffer buf) {
        if (t.deco != null)
            (buf).writestring(t.deco);
        else
        {
            Mangler v = new Mangler(buf);
            v.visitWithMask(t, (byte)0);
        }
    }

    public static void mangleToBuffer(Expression e, OutBuffer buf) {
        Mangler v = new Mangler(buf);
        e.accept(v);
    }

    public static void mangleToBuffer(Dsymbol s, OutBuffer buf) {
        Mangler v = new Mangler(buf);
        s.accept(v);
    }

    public static void mangleToBuffer(TemplateInstance ti, OutBuffer buf) {
        Mangler v = new Mangler(buf);
        v.mangleTemplateInstance(ti);
    }

    public static void mangleToFuncSignature(OutBuffer buf, FuncDeclaration fd) {
        Ref<OutBuffer> buf_ref = ref(buf);
        TypeFunction tf = fd.type.isTypeFunction();
        Mangler v = new Mangler(buf_ref.value);
        MODtoDecoBuffer(buf_ref.value, tf.mod);
        v.paramsToDecoBuffer(tf.parameterList.parameters);
        buf_ref.value.writeByte((90 - tf.parameterList.varargs));
    }

}
