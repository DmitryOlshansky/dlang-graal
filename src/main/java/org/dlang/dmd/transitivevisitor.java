package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.permissivevisitor.*;
import static org.dlang.dmd.tokens.*;

public class transitivevisitor {

    // from template ParseTimeTransitiveVisitor!(ASTBase)
    public static class ParseTimeTransitiveVisitorASTBase extends PermissiveVisitorASTBase
    {
        // from template mixin ParseVisitMethods!(ASTBase)// from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ExpStatement s) {
            if (s.exp != null && (s.exp.op & 0xFF) == 38)
            {
                ((ASTBase.DeclarationExp)s.exp).declaration.accept(this);
                return ;
            }
            if (s.exp != null)
                s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CompileStatement s) {
            this.visitArgs(s.exps, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CompoundStatement s) {
            {
                Slice<ASTBase.Statement> __r102 = (s.statements).opSlice().copy();
                int __key103 = 0;
                for (; __key103 < __r102.getLength();__key103 += 1) {
                    ASTBase.Statement sx = __r102.get(__key103);
                    if (sx != null)
                        sx.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitVarDecl(ASTBase.VarDeclaration v) {
            if (v.type != null)
                this.visitType(v.type);
            if (v._init != null)
            {
                ASTBase.ExpInitializer ie = v._init.isExpInitializer();
                if (ie != null && (ie.exp.op & 0xFF) == 95 || (ie.exp.op & 0xFF) == 96)
                    ((ASTBase.AssignExp)ie.exp).e2.accept(this);
                else
                    v._init.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CompoundDeclarationStatement s) {
            {
                Slice<ASTBase.Statement> __r108 = (s.statements).opSlice().copy();
                int __key109 = 0;
                for (; __key109 < __r108.getLength();__key109 += 1) {
                    ASTBase.Statement sx = __r108.get(__key109);
                    ASTBase.ExpStatement ds = sx != null ? sx.isExpStatement() : null;
                    if (ds != null && (ds.exp.op & 0xFF) == 38)
                    {
                        ASTBase.Dsymbol d = ((ASTBase.DeclarationExp)ds.exp).declaration;
                        assert(d.isDeclaration() != null);
                        {
                            ASTBase.VarDeclaration v = d.isVarDeclaration();
                            if (v != null)
                                this.visitVarDecl(v);
                            else
                                d.accept(this);
                        }
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ScopeStatement s) {
            if (s.statement != null)
                s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.WhileStatement s) {
            s.condition.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DoStatement s) {
            if (s._body != null)
                s._body.accept(this);
            s.condition.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ForStatement s) {
            if (s._init != null)
                s._init.accept(this);
            if (s.condition != null)
                s.condition.accept(this);
            if (s.increment != null)
                s.increment.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ForeachStatement s) {
            {
                Slice<ASTBase.Parameter> __r110 = (s.parameters).opSlice().copy();
                int __key111 = 0;
                for (; __key111 < __r110.getLength();__key111 += 1) {
                    ASTBase.Parameter p = __r110.get(__key111);
                    if (p.type != null)
                        this.visitType(p.type);
                }
            }
            s.aggr.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ForeachRangeStatement s) {
            if (s.prm.type != null)
                this.visitType(s.prm.type);
            s.lwr.accept(this);
            s.upr.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.IfStatement s) {
            if (s.prm != null && s.prm.type != null)
                this.visitType(s.prm.type);
            s.condition.accept(this);
            s.ifbody.accept(this);
            if (s.elsebody != null)
                s.elsebody.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ConditionalStatement s) {
            s.condition.accept(this);
            if (s.ifbody != null)
                s.ifbody.accept(this);
            if (s.elsebody != null)
                s.elsebody.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitArgs(DArray<ASTBase.Expression> expressions, ASTBase.Expression basis) {
            if (expressions == null || !(((expressions).length) != 0))
                return ;
            {
                Slice<ASTBase.Expression> __r100 = (expressions).opSlice().copy();
                int __key101 = 0;
                for (; __key101 < __r100.getLength();__key101 += 1) {
                    ASTBase.Expression el = __r100.get(__key101);
                    if (!(el != null))
                        el = basis;
                    if (el != null)
                        el.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.PragmaStatement s) {
            if (s.args != null && ((s.args).length) != 0)
                this.visitArgs(s.args, null);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StaticAssertStatement s) {
            s.sa.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.SwitchStatement s) {
            s.condition.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CaseStatement s) {
            s.exp.accept(this);
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CaseRangeStatement s) {
            s.first.accept(this);
            s.last.accept(this);
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DefaultStatement s) {
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.GotoCaseStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ReturnStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.SynchronizedStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.WithStatement s) {
            s.exp.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TryCatchStatement s) {
            if (s._body != null)
                s._body.accept(this);
            {
                Slice<ASTBase.Catch> __r112 = (s.catches).opSlice().copy();
                int __key113 = 0;
                for (; __key113 < __r112.getLength();__key113 += 1) {
                    ASTBase.Catch c = __r112.get(__key113);
                    this.visit(c);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TryFinallyStatement s) {
            s._body.accept(this);
            s.finalbody.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ScopeGuardStatement s) {
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ThrowStatement s) {
            s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.LabelStatement s) {
            if (s.statement != null)
                s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ImportStatement s) {
            {
                Slice<ASTBase.Dsymbol> __r114 = (s.imports).opSlice().copy();
                int __key115 = 0;
                for (; __key115 < __r114.getLength();__key115 += 1) {
                    ASTBase.Dsymbol imp = __r114.get(__key115);
                    imp.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.Catch c) {
            if (c.type != null)
                this.visitType(c.type);
            if (c.handler != null)
                c.handler.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitType(ASTBase.Type t) {
            if (!(t != null))
                return ;
            if ((t.ty & 0xFF) == ASTBase.ENUMTY.Tfunction)
            {
                this.visitFunctionType((ASTBase.TypeFunction)t, null);
                return ;
            }
            else
                t.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitFunctionType(ASTBase.TypeFunction t, ASTBase.TemplateDeclaration td) {
            if (t.next != null)
                this.visitType(t.next);
            if (td != null)
            {
                {
                    Slice<ASTBase.TemplateParameter> __r104 = (td.origParameters).opSlice().copy();
                    int __key105 = 0;
                    for (; __key105 < __r104.getLength();__key105 += 1) {
                        ASTBase.TemplateParameter p = __r104.get(__key105);
                        p.accept(this);
                    }
                }
            }
            this.visitParameters(t.parameterList.parameters);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitParameters(DArray<ASTBase.Parameter> parameters) {
            if (parameters != null)
            {
                int dim = ASTBase.Parameter.dim(parameters);
                {
                    int __key106 = 0;
                    int __limit107 = dim;
                    for (; __key106 < __limit107;__key106 += 1) {
                        int i = __key106;
                        ASTBase.Parameter fparam = ASTBase.Parameter.getNth(parameters, i, null);
                        fparam.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeVector t) {
            if (!(t.basetype != null))
                return ;
            t.basetype.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeSArray t) {
            t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeDArray t) {
            t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeAArray t) {
            t.next.accept(this);
            t.index.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypePointer t) {
            if ((t.next.ty & 0xFF) == ASTBase.ENUMTY.Tfunction)
            {
                this.visitFunctionType((ASTBase.TypeFunction)t.next, null);
            }
            else
                t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeReference t) {
            t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeFunction t) {
            this.visitFunctionType(t, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeDelegate t) {
            this.visitFunctionType((ASTBase.TypeFunction)t.next, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitTypeQualified(ASTBase.TypeQualified t) {
            {
                Slice<RootObject> __r116 = t.idents.opSlice().copy();
                int __key117 = 0;
                for (; __key117 < __r116.getLength();__key117 += 1) {
                    RootObject id = __r116.get(__key117);
                    if (id.dyncast() == DYNCAST.dsymbol)
                        ((ASTBase.TemplateInstance)id).accept(this);
                    else if (id.dyncast() == DYNCAST.expression)
                        ((ASTBase.Expression)id).accept(this);
                    else if (id.dyncast() == DYNCAST.type)
                        ((ASTBase.Type)id).accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeIdentifier t) {
            this.visitTypeQualified((ASTBase.TypeQualified)t);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeInstance t) {
            t.tempinst.accept(this);
            this.visitTypeQualified((ASTBase.TypeQualified)t);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeTypeof t) {
            t.exp.accept(this);
            this.visitTypeQualified((ASTBase.TypeQualified)t);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeReturn t) {
            this.visitTypeQualified((ASTBase.TypeQualified)t);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeTuple t) {
            this.visitParameters(t.arguments);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeSlice t) {
            t.next.accept(this);
            t.lwr.accept(this);
            t.upr.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeTraits t) {
            t.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StaticAssert s) {
            s.exp.accept(this);
            if (s.msg != null)
                s.msg.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.EnumMember em) {
            if (em.type != null)
                this.visitType(em.type);
            if (em.value() != null)
                em.value().accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitAttribDeclaration(ASTBase.AttribDeclaration d) {
            if (d.decl != null)
            {
                Slice<ASTBase.Dsymbol> __r118 = (d.decl).opSlice().copy();
                int __key119 = 0;
                for (; __key119 < __r118.getLength();__key119 += 1) {
                    ASTBase.Dsymbol de = __r118.get(__key119);
                    de.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.AttribDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StorageClassDeclaration d) {
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DeprecatedDeclaration d) {
            d.msg.accept(this);
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.LinkDeclaration d) {
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CPPMangleDeclaration d) {
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ProtDeclaration d) {
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.AlignDeclaration d) {
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.AnonDeclaration d) {
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.PragmaDeclaration d) {
            if (d.args != null && ((d.args).length) != 0)
                this.visitArgs(d.args, null);
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ConditionalDeclaration d) {
            d.condition.accept(this);
            if (d.decl != null)
            {
                Slice<ASTBase.Dsymbol> __r120 = (d.decl).opSlice().copy();
                int __key121 = 0;
                for (; __key121 < __r120.getLength();__key121 += 1) {
                    ASTBase.Dsymbol de = __r120.get(__key121);
                    de.accept(this);
                }
            }
            if (d.elsedecl != null)
            {
                Slice<ASTBase.Dsymbol> __r122 = (d.elsedecl).opSlice().copy();
                int __key123 = 0;
                for (; __key123 < __r122.getLength();__key123 += 1) {
                    ASTBase.Dsymbol de = __r122.get(__key123);
                    de.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CompileDeclaration d) {
            this.visitArgs(d.exps, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.UserAttributeDeclaration d) {
            this.visitArgs(d.atts, null);
            this.visitAttribDeclaration((ASTBase.AttribDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitFuncBody(ASTBase.FuncDeclaration f) {
            if (f.frequires != null)
            {
                {
                    Slice<ASTBase.Statement> __r124 = (f.frequires).opSlice().copy();
                    int __key125 = 0;
                    for (; __key125 < __r124.getLength();__key125 += 1) {
                        ASTBase.Statement frequire = __r124.get(__key125);
                        frequire.accept(this);
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<ASTBase.Ensure> __r126 = (f.fensures).opSlice().copy();
                    int __key127 = 0;
                    for (; __key127 < __r126.getLength();__key127 += 1) {
                        ASTBase.Ensure fensure = __r126.get(__key127).copy();
                        fensure.ensure.accept(this);
                    }
                }
            }
            if (f.fbody != null)
            {
                f.fbody.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitBaseClasses(ASTBase.ClassDeclaration d) {
            if (!(d != null) || !(((d.baseclasses).length) != 0))
                return ;
            {
                Slice<ASTBase.BaseClass> __r128 = (d.baseclasses).opSlice().copy();
                int __key129 = 0;
                for (; __key129 < __r128.getLength();__key129 += 1) {
                    ASTBase.BaseClass b = __r128.get(__key129);
                    this.visitType((b).type);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  boolean visitEponymousMember(ASTBase.TemplateDeclaration d) {
            if (d.members == null || (d.members).length != 1)
                return false;
            ASTBase.Dsymbol onemember = (d.members).get(0);
            if (!onemember.ident.equals(d.ident))
                return false;
            {
                ASTBase.FuncDeclaration fd = onemember.isFuncDeclaration();
                if (fd != null)
                {
                    assert(fd.type != null);
                    this.visitFunctionType((ASTBase.TypeFunction)fd.type, d);
                    if (d.constraint != null)
                        d.constraint.accept(this);
                    this.visitFuncBody(fd);
                    return true;
                }
            }
            {
                ASTBase.AggregateDeclaration ad = onemember.isAggregateDeclaration();
                if (ad != null)
                {
                    this.visitTemplateParameters(d.parameters);
                    if (d.constraint != null)
                        d.constraint.accept(this);
                    this.visitBaseClasses(ad.isClassDeclaration());
                    if (ad.members != null)
                    {
                        Slice<ASTBase.Dsymbol> __r132 = (ad.members).opSlice().copy();
                        int __key133 = 0;
                        for (; __key133 < __r132.getLength();__key133 += 1) {
                            ASTBase.Dsymbol s = __r132.get(__key133);
                            s.accept(this);
                        }
                    }
                    return true;
                }
            }
            {
                ASTBase.VarDeclaration vd = onemember.isVarDeclaration();
                if (vd != null)
                {
                    if (d.constraint != null)
                        return false;
                    if (vd.type != null)
                        this.visitType(vd.type);
                    this.visitTemplateParameters(d.parameters);
                    if (vd._init != null)
                    {
                        ASTBase.ExpInitializer ie = vd._init.isExpInitializer();
                        if (ie != null && (ie.exp.op & 0xFF) == 95 || (ie.exp.op & 0xFF) == 96)
                            ((ASTBase.AssignExp)ie.exp).e2.accept(this);
                        else
                            vd._init.accept(this);
                        return true;
                    }
                }
            }
            return false;
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitTemplateParameters(DArray<ASTBase.TemplateParameter> parameters) {
            if (parameters == null || !(((parameters).length) != 0))
                return ;
            {
                Slice<ASTBase.TemplateParameter> __r130 = (parameters).opSlice().copy();
                int __key131 = 0;
                for (; __key131 < __r130.getLength();__key131 += 1) {
                    ASTBase.TemplateParameter p = __r130.get(__key131);
                    p.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TemplateDeclaration d) {
            if (this.visitEponymousMember(d))
                return ;
            this.visitTemplateParameters(d.parameters);
            if (d.constraint != null)
                d.constraint.accept(this);
            {
                Slice<ASTBase.Dsymbol> __r134 = (d.members).opSlice().copy();
                int __key135 = 0;
                for (; __key135 < __r134.getLength();__key135 += 1) {
                    ASTBase.Dsymbol s = __r134.get(__key135);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitObject(RootObject oarg) {
            {
                ASTBase.Type t = ASTBase.isType(oarg);
                if (t != null)
                {
                    this.visitType(t);
                }
                else {
                    ASTBase.Expression e = ASTBase.isExpression(oarg);
                    if (e != null)
                    {
                        e.accept(this);
                    }
                    else {
                        ASTBase.Tuple v = ASTBase.isTuple(oarg);
                        if (v != null)
                        {
                            DArray<RootObject> args = v.objects;
                            {
                                Slice<RootObject> __r136 = (args).opSlice().copy();
                                int __key137 = 0;
                                for (; __key137 < __r136.getLength();__key137 += 1) {
                                    RootObject arg = __r136.get(__key137);
                                    this.visitObject(arg);
                                }
                            }
                        }
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visitTiargs(ASTBase.TemplateInstance ti) {
            if (ti.tiargs == null)
                return ;
            {
                Slice<RootObject> __r138 = (ti.tiargs).opSlice().copy();
                int __key139 = 0;
                for (; __key139 < __r138.getLength();__key139 += 1) {
                    RootObject arg = __r138.get(__key139);
                    this.visitObject(arg);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TemplateInstance ti) {
            this.visitTiargs(ti);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TemplateMixin tm) {
            this.visitType(tm.tqual);
            this.visitTiargs((ASTBase.TemplateInstance)tm);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.EnumDeclaration d) {
            if (d.memtype != null)
                this.visitType(d.memtype);
            if (d.members == null)
                return ;
            {
                Slice<ASTBase.Dsymbol> __r140 = (d.members).opSlice().copy();
                int __key141 = 0;
                for (; __key141 < __r140.getLength();__key141 += 1) {
                    ASTBase.Dsymbol em = __r140.get(__key141);
                    if (!(em != null))
                        continue;
                    em.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.Nspace d) {
            {
                Slice<ASTBase.Dsymbol> __r142 = (d.members).opSlice().copy();
                int __key143 = 0;
                for (; __key143 < __r142.getLength();__key143 += 1) {
                    ASTBase.Dsymbol s = __r142.get(__key143);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StructDeclaration d) {
            if (d.members == null)
                return ;
            {
                Slice<ASTBase.Dsymbol> __r144 = (d.members).opSlice().copy();
                int __key145 = 0;
                for (; __key145 < __r144.getLength();__key145 += 1) {
                    ASTBase.Dsymbol s = __r144.get(__key145);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ClassDeclaration d) {
            this.visitBaseClasses(d);
            if (d.members != null)
            {
                Slice<ASTBase.Dsymbol> __r146 = (d.members).opSlice().copy();
                int __key147 = 0;
                for (; __key147 < __r146.getLength();__key147 += 1) {
                    ASTBase.Dsymbol s = __r146.get(__key147);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.AliasDeclaration d) {
            if (d.aliassym != null)
                d.aliassym.accept(this);
            else
                this.visitType(d.type);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.VarDeclaration d) {
            this.visitVarDecl(d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.FuncDeclaration f) {
            ASTBase.TypeFunction tf = (ASTBase.TypeFunction)f.type;
            this.visitType((ASTBase.Type)tf);
            this.visitFuncBody(f);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.FuncLiteralDeclaration f) {
            if ((f.type.ty & 0xFF) == ASTBase.ENUMTY.Terror)
                return ;
            ASTBase.TypeFunction tf = (ASTBase.TypeFunction)f.type;
            if (!(f.inferRetType) && tf.next != null)
                this.visitType(tf.next);
            this.visitParameters(tf.parameterList.parameters);
            ASTBase.CompoundStatement cs = f.fbody.isCompoundStatement();
            ASTBase.Statement s = !(cs != null) ? f.fbody : null;
            ASTBase.ReturnStatement rs = s != null ? s.isReturnStatement() : null;
            if (rs != null && rs.exp != null)
                rs.exp.accept(this);
            else
                this.visitFuncBody((ASTBase.FuncDeclaration)f);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.PostBlitDeclaration d) {
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DtorDeclaration d) {
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StaticCtorDeclaration d) {
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StaticDtorDeclaration d) {
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.InvariantDeclaration d) {
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.UnitTestDeclaration d) {
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.NewDeclaration d) {
            this.visitParameters(d.parameters);
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DeleteDeclaration d) {
            this.visitParameters(d.parameters);
            this.visitFuncBody((ASTBase.FuncDeclaration)d);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StructInitializer si) {
            {
                Slice<Identifier> __r149 = si.field.opSlice().copy();
                int __key148 = 0;
                for (; __key148 < __r149.getLength();__key148 += 1) {
                    Identifier id = __r149.get(__key148);
                    int i = __key148;
                    {
                        ASTBase.Initializer iz = si.value.get(i);
                        if (iz != null)
                            iz.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ArrayInitializer ai) {
            {
                Slice<ASTBase.Expression> __r151 = ai.index.opSlice().copy();
                int __key150 = 0;
                for (; __key150 < __r151.getLength();__key150 += 1) {
                    ASTBase.Expression ex = __r151.get(__key150);
                    int i = __key150;
                    if (ex != null)
                        ex.accept(this);
                    {
                        ASTBase.Initializer iz = ai.value.get(i);
                        if (iz != null)
                            iz.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ExpInitializer ei) {
            ei.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ArrayLiteralExp e) {
            this.visitArgs(e.elements, e.basis);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.AssocArrayLiteralExp e) {
            {
                Slice<ASTBase.Expression> __r153 = (e.keys).opSlice().copy();
                int __key152 = 0;
                for (; __key152 < __r153.getLength();__key152 += 1) {
                    ASTBase.Expression key = __r153.get(__key152);
                    int i = __key152;
                    key.accept(this);
                    (e.values).get(i).accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeExp e) {
            this.visitType(e.type);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ScopeExp e) {
            if (e.sds.isTemplateInstance() != null)
                e.sds.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.NewExp e) {
            if (e.thisexp != null)
                e.thisexp.accept(this);
            if (e.newargs != null && ((e.newargs).length) != 0)
                this.visitArgs(e.newargs, null);
            this.visitType(e.newtype);
            if (e.arguments != null && ((e.arguments).length) != 0)
                this.visitArgs(e.arguments, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.NewAnonClassExp e) {
            if (e.thisexp != null)
                e.thisexp.accept(this);
            if (e.newargs != null && ((e.newargs).length) != 0)
                this.visitArgs(e.newargs, null);
            if (e.arguments != null && ((e.arguments).length) != 0)
                this.visitArgs(e.arguments, null);
            if (e.cd != null)
                e.cd.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TupleExp e) {
            if (e.e0 != null)
                e.e0.accept(this);
            this.visitArgs(e.exps, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.FuncExp e) {
            e.fd.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DeclarationExp e) {
            {
                ASTBase.VarDeclaration v = e.declaration.isVarDeclaration();
                if (v != null)
                    this.visitVarDecl(v);
                else
                    e.declaration.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TypeidExp e) {
            this.visitObject(e.obj);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TraitsExp e) {
            if (e.args != null)
            {
                Slice<RootObject> __r154 = (e.args).opSlice().copy();
                int __key155 = 0;
                for (; __key155 < __r154.getLength();__key155 += 1) {
                    RootObject arg = __r154.get(__key155);
                    this.visitObject(arg);
                }
            }
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.IsExp e) {
            this.visitType(e.targ);
            if (e.tspec != null)
                this.visitType(e.tspec);
            if (e.parameters != null && ((e.parameters).length) != 0)
                this.visitTemplateParameters(e.parameters);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.UnaExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.BinExp e) {
            e.e1.accept(this);
            e.e2.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CompileExp e) {
            this.visitArgs(e.exps, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ImportExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.AssertExp e) {
            e.e1.accept(this);
            if (e.msg != null)
                e.msg.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DotIdExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DotTemplateInstanceExp e) {
            e.e1.accept(this);
            e.ti.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CallExp e) {
            e.e1.accept(this);
            this.visitArgs(e.arguments, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.PtrExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.DeleteExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CastExp e) {
            if (e.to != null)
                this.visitType(e.to);
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.IntervalExp e) {
            e.lwr.accept(this);
            e.upr.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.ArrayExp e) {
            e.e1.accept(this);
            this.visitArgs(e.arguments, null);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.PostExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.CondExp e) {
            e.econd.accept(this);
            e.e1.accept(this);
            e.e2.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TemplateTypeParameter tp) {
            if (tp.specType != null)
                this.visitType(tp.specType);
            if (tp.defaultType != null)
                this.visitType(tp.defaultType);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TemplateThisParameter tp) {
            this.visit((ASTBase.TemplateTypeParameter)tp);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TemplateAliasParameter tp) {
            if (tp.specType != null)
                this.visitType(tp.specType);
            if (tp.specAlias != null)
                this.visitObject(tp.specAlias);
            if (tp.defaultAlias != null)
                this.visitObject(tp.defaultAlias);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.TemplateValueParameter tp) {
            this.visitType(tp.valType);
            if (tp.specValue != null)
                tp.specValue.accept(this);
            if (tp.defaultValue != null)
                tp.defaultValue.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.StaticIfCondition c) {
            c.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.Parameter p) {
            this.visitType(p.type);
            if (p.defaultArg != null)
                p.defaultArg.accept(this);
        }


        // from template ParseVisitMethods!(ASTBase)
        public  void visit(ASTBase.Module m) {
            {
                Slice<ASTBase.Dsymbol> __r156 = (m.members).opSlice().copy();
                int __key157 = 0;
                for (; __key157 < __r156.getLength();__key157 += 1) {
                    ASTBase.Dsymbol s = __r156.get(__key157);
                    s.accept(this);
                }
            }
        }



        protected ParseTimeTransitiveVisitorASTBase() {}

        public ParseTimeTransitiveVisitorASTBase copy() {
            ParseTimeTransitiveVisitorASTBase that = new ParseTimeTransitiveVisitorASTBase();
            return that;
        }
    }

}
