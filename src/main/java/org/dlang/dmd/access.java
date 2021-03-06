package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;

public class access {

    static boolean LOG = false;
    // Erasure: checkAccess<AggregateDeclaration, Loc, Ptr, Dsymbol>
    public static boolean checkAccess(AggregateDeclaration ad, Loc loc, Ptr<Scope> sc, Dsymbol smember) {
        if (smember.toParent().isTemplateInstance() != null)
        {
            return false;
        }
        if (!symbolIsVisible(sc, smember) && (((sc.get()).flags & 1024) == 0) || (sc.get()).func.setUnsafe())
        {
            ad.error(loc, new BytePtr("member `%s` is not accessible%s"), smember.toChars(), ((sc.get()).flags & 1024) != 0 ? new BytePtr(" from `@safe` code") : new BytePtr(""));
            return true;
        }
        return false;
    }

    // Erasure: hasPackageAccess<Ptr, Dsymbol>
    public static boolean hasPackageAccess(Ptr<Scope> sc, Dsymbol s) {
        return hasPackageAccess((sc.get())._module, s);
    }

    // Erasure: hasPackageAccess<Module, Dsymbol>
    public static boolean hasPackageAccess(dmodule.Module mod, Dsymbol s) {
        dmodule.Package pkg = null;
        if (s.prot().pkg != null)
        {
            pkg = s.prot().pkg;
        }
        else
        {
            for (; s != null;s = s.parent.value){
                {
                    dmodule.Module m = s.isModule();
                    if ((m) != null)
                    {
                        DsymbolTable dst = dmodule.Package.resolve(m.md != null ? (m.md.get()).packages : null, null, null);
                        assert(dst != null);
                        Dsymbol s2 = dst.lookup(m.ident);
                        assert(s2 != null);
                        dmodule.Package p = s2.isPackage();
                        if ((p != null) && (p.isPackageMod() != null))
                        {
                            pkg = p;
                            break;
                        }
                    }
                    else if (((pkg = s.isPackage()) != null))
                    {
                        break;
                    }
                }
            }
        }
        if (pkg != null)
        {
            if ((pequals(pkg, mod.parent.value)))
            {
                return true;
            }
            if ((pequals(pkg.isPackageMod(), mod)))
            {
                return true;
            }
            Dsymbol ancestor = mod.parent.value;
            for (; ancestor != null;ancestor = ancestor.parent.value){
                if ((pequals(ancestor, pkg)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    // Erasure: hasProtectedAccess<Ptr, Dsymbol>
    public static boolean hasProtectedAccess(Ptr<Scope> sc, Dsymbol s) {
        {
            ClassDeclaration cd = s.isClassMember();
            if ((cd) != null)
            {
                {
                    Ptr<Scope> scx = sc;
                    for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                        if ((scx.get()).scopesym == null)
                        {
                            continue;
                        }
                        ClassDeclaration cd2 = (scx.get()).scopesym.isClassDeclaration();
                        if ((cd2 != null) && cd.isBaseOf(cd2, null))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return pequals((sc.get())._module, s.getAccessModule());
    }

    // Erasure: checkAccess<Loc, Ptr, Expression, Declaration>
    public static boolean checkAccess(Loc loc, Ptr<Scope> sc, Expression e, Declaration d) {
        if (((sc.get()).flags & 2) != 0)
        {
            return false;
        }
        if (d.isUnitTestDeclaration() != null)
        {
            return false;
        }
        if (e == null)
        {
            return false;
        }
        if (((e.type.value.ty & 0xFF) == ENUMTY.Tclass))
        {
            ClassDeclaration cd = (((TypeClass)e.type.value)).sym;
            if (((e.op & 0xFF) == 124))
            {
                {
                    ClassDeclaration cd2 = (sc.get()).func.toParent().isClassDeclaration();
                    if ((cd2) != null)
                    {
                        cd = cd2;
                    }
                }
            }
            return checkAccess((AggregateDeclaration)cd, loc, sc, (Dsymbol)d);
        }
        else if (((e.type.value.ty & 0xFF) == ENUMTY.Tstruct))
        {
            StructDeclaration cd = (((TypeStruct)e.type.value)).sym;
            return checkAccess((AggregateDeclaration)cd, loc, sc, (Dsymbol)d);
        }
        return false;
    }

    // Erasure: checkAccess<Loc, Ptr, Package>
    public static boolean checkAccess(Loc loc, Ptr<Scope> sc, dmodule.Package p) {
        if ((pequals((sc.get())._module, p)))
        {
            return false;
        }
        for (; sc != null;sc = pcopy((sc.get()).enclosing)){
            if (((sc.get()).scopesym != null) && (sc.get()).scopesym.isPackageAccessible(p, new Prot(Prot.Kind.private_), 0))
            {
                return false;
            }
        }
        return true;
    }

    // Erasure: symbolIsVisible<Module, Dsymbol>
    public static boolean symbolIsVisible(dmodule.Module mod, Dsymbol s) {
        s = mostVisibleOverload(s, null);
        switch (s.prot().kind)
        {
            case Prot.Kind.undefined:
                return true;
            case Prot.Kind.none:
                return false;
            case Prot.Kind.private_:
                return pequals(s.getAccessModule(), mod);
            case Prot.Kind.package_:
                return (pequals(s.getAccessModule(), mod)) || hasPackageAccess(mod, s);
            case Prot.Kind.protected_:
                return pequals(s.getAccessModule(), mod);
            case Prot.Kind.public_:
                case Prot.Kind.export_:
                    return true;
            default:
            throw SwitchError.INSTANCE;
        }
    }

    // Erasure: symbolIsVisible<Dsymbol, Dsymbol>
    public static boolean symbolIsVisible(Dsymbol origin, Dsymbol s) {
        return symbolIsVisible(origin.getAccessModule(), s);
    }

    // Erasure: symbolIsVisible<Ptr, Dsymbol>
    public static boolean symbolIsVisible(Ptr<Scope> sc, Dsymbol s) {
        s = mostVisibleOverload(s, null);
        return checkSymbolAccess(sc, s);
    }

    // Erasure: checkSymbolAccess<Ptr, Dsymbol>
    public static boolean checkSymbolAccess(Ptr<Scope> sc, Dsymbol s) {
        switch (s.prot().kind)
        {
            case Prot.Kind.undefined:
                return true;
            case Prot.Kind.none:
                return false;
            case Prot.Kind.private_:
                return pequals((sc.get())._module, s.getAccessModule());
            case Prot.Kind.package_:
                return (pequals((sc.get())._module, s.getAccessModule())) || hasPackageAccess((sc.get())._module, s);
            case Prot.Kind.protected_:
                return hasProtectedAccess(sc, s);
            case Prot.Kind.public_:
                case Prot.Kind.export_:
                    return true;
            default:
            throw SwitchError.INSTANCE;
        }
    }

    // Erasure: mostVisibleOverload<Dsymbol, Module>
    public static Dsymbol mostVisibleOverload(Dsymbol s, dmodule.Module mod) {
        if (!s.isOverloadable())
        {
            return s;
        }
        Dsymbol next = null;
        Dsymbol fstart = s;
        Dsymbol mostVisible = s;
        for (; s != null;s = next){
            {
                FuncDeclaration fd = s.isFuncDeclaration();
                if ((fd) != null)
                {
                    next = fd.overnext;
                }
                else {
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if ((td) != null)
                    {
                        next = td.overnext.value;
                    }
                    else {
                        FuncAliasDeclaration fa = s.isFuncAliasDeclaration();
                        if ((fa) != null)
                        {
                            next = fa.overnext;
                        }
                        else {
                            OverDeclaration od = s.isOverDeclaration();
                            if ((od) != null)
                            {
                                next = od.overnext;
                            }
                            else {
                                AliasDeclaration ad = s.isAliasDeclaration();
                                if ((ad) != null)
                                {
                                    assertMsg(ad.isOverloadable() || (ad.type != null) && ((ad.type.ty & 0xFF) == ENUMTY.Terror), new ByteSlice("Non overloadable Aliasee in overload list"));
                                    if ((ad.semanticRun < PASS.semanticdone))
                                    {
                                        next = ad.overnext;
                                    }
                                    else
                                    {
                                        Dsymbol aliasee = ad.toAlias();
                                        if ((aliasee.isFuncAliasDeclaration() != null) || (aliasee.isOverDeclaration() != null))
                                        {
                                            next = aliasee;
                                        }
                                        else
                                        {
                                            assertMsg((ad.overnext == null), new ByteSlice("Unresolved overload of alias"));
                                            break;
                                        }
                                    }
                                    assert((next != ad));
                                    assert((next != fstart));
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            Function2<Dsymbol,dmodule.Module,Prot> protectionSeenFromModule = new Function2<Dsymbol,dmodule.Module,Prot>() {
                public Prot invoke(Dsymbol d, dmodule.Module mod) {
                 {
                    Prot prot = d.prot().copy();
                    if ((mod != null) && (prot.kind == Prot.Kind.package_))
                    {
                        return hasPackageAccess(mod, d) ? new Prot(Prot.Kind.public_) : new Prot(Prot.Kind.private_);
                    }
                    return prot;
                }}

            };
            if ((next != null) && protectionSeenFromModule.invoke(mostVisible, mod).isMoreRestrictiveThan(protectionSeenFromModule.invoke(next, mod)))
            {
                mostVisible = next;
            }
        }
        return mostVisible;
    }

    // defaulted all parameters starting with #2
    public static Dsymbol mostVisibleOverload(Dsymbol s) {
        return mostVisibleOverload(s, (dmodule.Module)null);
    }

}
